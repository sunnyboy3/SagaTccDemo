package com.sagatcc.demo.wallet;

import com.sagatcc.core.api.SagaTccParticipant;
import com.sagatcc.demo.wallet.api.WalletPayRequest;

import org.springframework.stereotype.Component;

@Component
public class WalletPayParticipant implements SagaTccParticipant<WalletPayRequest> {

    private final WalletAccountService walletAccountService;

    public WalletPayParticipant(WalletAccountService walletAccountService) {
        this.walletAccountService = walletAccountService;
    }

    @Override
    public void sagaTry(WalletPayRequest request) {
        walletAccountService.tryPay(request);
    }

    @Override
    public void sagaConfirm(WalletPayRequest request) {
        walletAccountService.confirmPay(request);
    }

    @Override
    public void sagaCancel(WalletPayRequest request) {
        walletAccountService.cancelPay(request);
    }
}

