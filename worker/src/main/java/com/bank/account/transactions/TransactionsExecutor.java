package com.bank.account.transactions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TransactionsExecutor {

	public static void main(String[] args) {
		SpringApplication.run(TransactionsExecutor.class, args);
	}

}
