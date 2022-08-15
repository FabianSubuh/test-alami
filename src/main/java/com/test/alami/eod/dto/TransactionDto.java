package com.test.alami.eod.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private int id;
    private String name;
    private int age;
    private int balance;
    private int prevBalance;
    private int avgBalance;
    private int freeTransfer;
    private String thread1No;
    private String thread2aNo;
    private String thread2bNo;
    private String thread3No;

    public TransactionDto(int id, String name, int age, int balance, int prevBalance, int avgBalance, int freeTransfer) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.balance = balance;
        this.prevBalance = prevBalance;
        this.avgBalance = avgBalance;
        this.freeTransfer = freeTransfer;
    }

    @Override
    public String toString() {
        return
                id +
                ";" + name +
                ";" + age +
                ";" + balance +
                        ";" + thread2bNo +
                        ";" + thread3No +
                ";" + prevBalance +
                ";" + avgBalance +
                        ";" + thread1No +
                ";" + freeTransfer +
                ";" + thread2aNo +
                ";";
    }
}
