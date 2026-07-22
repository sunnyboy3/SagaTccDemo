package com.sagatcc.demo.wallet.api;

import com.sagatcc.core.api.SagaTccBusiness;
import com.sagatcc.core.api.SagaTccRequest;

/** 钱包支付 SagaTcc 请求契约。 */
@SagaTccBusiness(
        appId = WalletSagaRoutes.APPLICATION_ID,
        busCode = WalletSagaRoutes.WALLET_PAY_BUS_CODE)
public class WalletPayRequest implements SagaTccRequest {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private Long amount;

    public WalletPayRequest() {
    }

    public WalletPayRequest(Long userId, Long amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}

