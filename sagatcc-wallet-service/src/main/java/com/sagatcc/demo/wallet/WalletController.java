package com.sagatcc.demo.wallet;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletAccountService walletAccountService;

    public WalletController(WalletAccountService walletAccountService) {
        this.walletAccountService = walletAccountService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<WalletAccount> find(@PathVariable long userId) {
        WalletAccount account = walletAccountService.findByUserId(userId);
        return account == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(account);
    }
}

