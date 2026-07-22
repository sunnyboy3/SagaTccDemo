package com.sagatcc.demo.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import com.sagatcc.core.api.SagaTccOperations;
import com.sagatcc.demo.wallet.api.WalletPayRequest;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderApplicationService {

    private final JdbcTemplate jdbcTemplate;
    private final SagaTccOperations sagaTccOperations;

    public OrderApplicationService(JdbcTemplate jdbcTemplate, SagaTccOperations sagaTccOperations) {
        this.jdbcTemplate = jdbcTemplate;
        this.sagaTccOperations = sagaTccOperations;
    }

    @Transactional
    public CreateOrderResponse createOrder(long userId, long amount) {
        if (userId <= 0 || amount <= 0) {
            throw new IllegalArgumentException("用户标识和支付金额必须大于零");
        }

        Long orderId = insertOrder(userId, amount);
        String sagaId = sagaTccOperations.begin("createOrder", String.valueOf(orderId));
        sagaTccOperations.enlist(new OrderFinalizeRequest(orderId));
        sagaTccOperations.enlist(new WalletPayRequest(userId, amount));
        return new CreateOrderResponse(orderId, sagaId, "PENDING");
    }

    public OrderRecord findById(long orderId) {
        try {
            return jdbcTemplate.queryForObject(
                    "select order_id, user_id, amount, status, create_time, update_time "
                            + "from demo_order where order_id = ?",
                    new BeanPropertyRowMapper<OrderRecord>(OrderRecord.class), orderId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void markPaid(long orderId) {
        updateStatus(orderId, "PAID");
    }

    public void markCancelled(long orderId) {
        updateStatus(orderId, "CANCELLED");
    }

    private Long insertOrder(long userId, long amount) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int updated = jdbcTemplate.update(connection -> createInsertStatement(connection, userId, amount), keyHolder);
        if (updated != 1 || keyHolder.getKey() == null) {
            throw new IllegalStateException("创建订单失败");
        }
        return keyHolder.getKey().longValue();
    }

    private PreparedStatement createInsertStatement(Connection connection, long userId, long amount)
            throws java.sql.SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "insert into demo_order (user_id, amount, status, create_time, update_time) "
                        + "values (?, ?, 'PENDING', current_timestamp(3), current_timestamp(3))",
                Statement.RETURN_GENERATED_KEYS);
        statement.setLong(1, userId);
        statement.setLong(2, amount);
        return statement;
    }

    private void updateStatus(long orderId, String status) {
        int updated = jdbcTemplate.update(
                "update demo_order set status = ?, update_time = current_timestamp(3) "
                        + "where order_id = ? and status = 'PENDING'",
                status, orderId);
        if (updated != 1) {
            OrderRecord current = findById(orderId);
            if (current == null || !status.equals(current.getStatus())) {
                throw new IllegalStateException("订单状态更新失败，orderId=" + orderId + "，status=" + status);
            }
        }
    }
}

