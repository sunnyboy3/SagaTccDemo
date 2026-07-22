package com.sagatcc.demo.order;

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
        System.out.println("0-sagaTry" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        // Try 阶段保留订单的 PENDING 状态。
    }

    @Override
    public void sagaConfirm(OrderFinalizeRequest request) {
        System.out.println("0-sagaConfirm" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        orderApplicationService.markPaid(request.getOrderId());
    }

    @Override
    public void sagaCancel(OrderFinalizeRequest request) {
        System.out.println("0-sagaCancel" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        orderApplicationService.markCancelled(request.getOrderId());
    }
}

