package com.bank.account.transactions.client;

import com.bank.account.transactions.model.Balance;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@FeignClient(name = "balanceClient", url = "${feign.client.balance.url}")
public interface BalanceClient {

    // GET endpoint: curl http://localhost:8081/api/balance/:accountId
    @GetMapping("/{accountId}")
    Balance getBalance(@PathVariable("accountId") String accountId);

    // PUT endpoint: curl -X PUT -H "Content-Type: application/json" -d :amount http://localhost:8081/api/balance/:accountId
    @PutMapping(value = "/{accountId}", consumes = "application/json")
    String updateBalance(@PathVariable("accountId") String accountId, @RequestBody BigDecimal amount);


}
