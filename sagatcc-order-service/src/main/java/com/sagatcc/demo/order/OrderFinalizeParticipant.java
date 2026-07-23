package com.sagatcc.demo.order;

import com.sagatcc.core.api.SagaTccNonRetryableException;
import com.sagatcc.core.api.SagaTccParticipant;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class OrderFinalizeParticipant implements SagaTccParticipant<OrderFinalizeRequest> {

    private final OrderApplicationService orderApplicationService;

    public OrderFinalizeParticipant(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @Override
    public void sagaTry(OrderFinalizeRequest request) {
        System.out.println(request.getSeq() + "-sagaTry:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        // Try 阶段保留订单的 PENDING 状态。
        if (request.getSeq() == 0) {
            throw new SagaTccNonRetryableException("订单支付异常");
        }
    }

    @Override
    public void sagaConfirm(OrderFinalizeRequest request) {
        System.out.println(request.getSeq() + "-sagaConfirm:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        if (request.getSeq() == 1) {
            throw new SagaTccNonRetryableException("订单支付异常");
        }
        orderApplicationService.markPaid(request.getOrderId());
        if (request.getSeq() == 2) {
            throw new SagaTccNonRetryableException("订单支付异常");
        }
    }

    @Override
    public void sagaCancel(OrderFinalizeRequest request) {
        System.out.println(request.getSeq() + "-sagaCancel:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        if (request.getSeq() == 3) {
            throw new SagaTccNonRetryableException("订单支付异常");
        }
        orderApplicationService.markCancelled(request.getOrderId());
        if (request.getSeq() == 4) {
            throw new SagaTccNonRetryableException("订单支付异常");
        }
    }
}

