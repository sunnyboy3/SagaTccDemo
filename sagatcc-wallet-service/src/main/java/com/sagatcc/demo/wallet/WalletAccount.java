package com.sagatcc.demo.wallet;

/** 钱包账户查询结果。 */
public class WalletAccount {

    private Long userId;
    private Long totalAmount;
    private Long frozenAmount;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getFrozenAmount() {
        return frozenAmount;
    }

    public void setFrozenAmount(Long frozenAmount) {
        this.frozenAmount = frozenAmount;
    }

    public Long getAvailableAmount() {
        return totalAmount - frozenAmount;
    }
}

