package com.sagatcc.demo.order;

import com.sagatcc.core.api.SagaTccBusiness;
import com.sagatcc.core.api.SagaTccRequest;

/** 更新订单最终状态的 SagaTcc 请求。 */
@SagaTccBusiness(
        appId = OrderSagaRoutes.APPLICATION_ID,
        busCode = OrderSagaRoutes.FINALIZE_BUS_CODE)
public class OrderFinalizeRequest implements SagaTccRequest {

    private static final long serialVersionUID = 1L;

    private Long orderId;

    public OrderFinalizeRequest() {
    }

    public OrderFinalizeRequest(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}

