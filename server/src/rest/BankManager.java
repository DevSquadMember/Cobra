package server.src.rest;

import BankIDL.IBank;
import server.src.Bank;

public class BankManager {
    private static Bank bank;
    private static IBank ibank;

    public static void setBank(Bank bank) {
        BankManager.bank = bank;
    }

    public static Bank getBank() {
        return BankManager.bank;
    }

    public static void setIBank(IBank ibank) {
        BankManager.ibank = ibank;
    }

    public static IBank getIBank() {
        return BankManager.ibank;
    }
}
