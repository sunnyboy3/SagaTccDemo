package com.sagatcc.demo.wallet;

import com.sagatcc.core.api.SagaTccException;
import com.sagatcc.core.api.SagaTccNonRetryableException;
import com.sagatcc.demo.wallet.api.WalletPayRequest;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class WalletAccountService {

    private final JdbcTemplate jdbcTemplate;

    public WalletAccountService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void tryPay(WalletPayRequest request) {
        System.out.println(request.getSeq() + "-tryPay" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        if (request.getSeq() == 5) {
            throw new SagaTccNonRetryableException("订单支付异常");
        }
        validate(request);
        int updated = jdbcTemplate.update(
                "update wallet_account set frozen_amount = frozen_amount + ? "
                        + " where user_id = ? "
                        + "and total_amount - frozen_amount >= ?",
                request.getAmount(), request.getUserId(), request.getAmount());
        if (updated != 1) {
            throw new SagaTccNonRetryableException("钱包不存在或可用余额不足");
        }
        if (request.getSeq() == 6) {
            throw new SagaTccNonRetryableException("订单支付异常");
        }
    }

    public void confirmPay(WalletPayRequest request) {
        System.out.println(request.getSeq() + "-confirmPay" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        if (request.getSeq() == 7) {
            throw new SagaTccNonRetryableException("订单支付异常");
        }
        validate(request);
        int updated = jdbcTemplate.update(
                "update wallet_account set total_amount = total_amount - ?, "
                        + "frozen_amount = frozen_amount - ? "
                        + "where user_id = ?",
                request.getAmount(), request.getAmount(), request.getUserId());
        if (updated != 1) {
            throw new SagaTccNonRetryableException("确认扣款时冻结金额不足");
        }
        if (request.getSeq() == 8) {
            throw new SagaTccNonRetryableException("订单支付异常");
        }
        jdbcTemplate.update(
                "update wallet_account set update_time = current_timestamp(3) where user_id = ?",
                request.getUserId());
    }

    public void cancelPay(WalletPayRequest request) {
        System.out.println(request.getSeq() + "-cancelPay" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        if (request.getSeq() == 9) {
            throw new SagaTccNonRetryableException("订单支付异常");
        }
        validate(request);
        int updated = jdbcTemplate.update(
                "update wallet_account set frozen_amount = frozen_amount - ?, "
                        + "update_time = current_timestamp(3) where user_id = ? and frozen_amount >= ?",
                request.getAmount(), request.getUserId(), request.getAmount());
        if (updated != 1) {
            throw new SagaTccNonRetryableException("取消支付时冻结金额不足");
        }
        if (request.getSeq() == 10) {
            throw new SagaTccNonRetryableException("订单支付异常");
        }
    }

    public WalletAccount findByUserId(long userId) {
        try {
            return jdbcTemplate.queryForObject(
                    "select user_id, total_amount, frozen_amount from wallet_account where user_id = ?",
                    new BeanPropertyRowMapper<WalletAccount>(WalletAccount.class), userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private void validate(WalletPayRequest request) {
        if (request == null || request.getUserId() == null || request.getAmount() == null
                || request.getUserId() <= 0 || request.getAmount() <= 0) {
            throw new SagaTccNonRetryableException("钱包支付参数无效");
        }
    }
}

