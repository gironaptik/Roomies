package com.roomies.Model;

public class ApartmentBalance {

    long balance;
    String name;

    public ApartmentBalance(long balance, String name) {
        this.balance = balance;
        this.name = name;
    }

    public ApartmentBalance() {
    }

    public long getBalance() {
        return balance;
    }

    public String getName() {
        return name;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public void setName(String name) {
        this.name = name;
    }
}
