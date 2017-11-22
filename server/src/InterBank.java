package server.src;

import BankIDL.BankTransaction;
import BankIDL.IBank;
import BankIDL.IInterBankPOA;
import BankIDL.TransactionResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class InterBank extends IInterBankPOA {

    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private ArrayList<IBank> banks = new ArrayList<IBank>();
    private HashMap<String, BankTransaction> transactions = new HashMap<String, BankTransaction>();
    private HashMap<String, BankTransaction> executedTransactions = new HashMap<String, BankTransaction>();
    private HashMap<String, BankTransaction> canceledTransactions = new HashMap<String, BankTransaction>();

    private String getDateToken() {
        return dateFormat.format(new Date());
    }

    private IBank getBank(int bankId) {
        for (IBank bank : this.banks) {
            if (bank.bankId() == bankId) {
                return bank;
            }
        }
        return null;
    }

    @Override
    public IBank[] banks() {
        IBank[] iBanks = new IBank[this.banks.size()];
        int i = 0;
        for (IBank id : this.banks) {
            iBanks[i] = id;
            i++;
        }
        return iBanks;
    }

    @Override
    public void register(IBank bank) {
        this.banks.add(bank);
        System.out.println("Banque enregistrée : " + bank.bankId());
    }

    @Override
    public void registerTransaction(BankTransaction transaction) {
        String date = getDateToken();
        System.out.println("- " + date + " : Demande de transfert depuis la banque " + transaction.bankIdSrc +
                " vers la banque " + transaction.bankIdDest + " de " + transaction.amount + "€");
        this.transactions.put(date, transaction);
        IBank bank = getBank(transaction.bankIdDest);
        if (bank == null) {
            // prévenir la banque d'une erreur ? Tout stocker dans Transaction avec un id ?
            return;
        }
        bank.executeTransaction(transaction);
    }

    @Override
    public void registeredTransaction(BankTransaction transaction) {
        IBank bank = getBank(transaction.bankIdSrc);
        String date = getDateToken();
        if (bank != null && transaction.result == TransactionResult.SUCCESS) {
            bank.executeTransaction(transaction);
            this.executedTransactions.put(date, transaction);
        } else {
            this.canceledTransactions.put(date, transaction);
        }
    }
}
