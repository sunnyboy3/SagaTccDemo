package com.sagatcc.demo.order;

/** 创建订单响应。 */
public class CreateOrderResponse {

    private final Long orderId;
    private final String sagaId;
    private final String status;

    public CreateOrderResponse(Long orderId, String sagaId, String status) {
        this.orderId = orderId;
        this.sagaId = sagaId;
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getSagaId() {
        return sagaId;
    }

    public String getStatus() {
        return status;
    }
}

