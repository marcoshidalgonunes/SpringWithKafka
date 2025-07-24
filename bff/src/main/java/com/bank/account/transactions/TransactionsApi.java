package com.bank.account.transactions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class TransactionsApi {

	public static void main(String[] args) {
		SpringApplication.run(TransactionsApi.class, args);
	}

}
