package server.src;

import BankIDL.IBankPOA;

import java.util.ArrayList;
import java.util.HashMap;

public class Bank extends IBankPOA {

    private static int CLIENT_TOKEN = 0;
    private static int BANK_TOKEN = 0;

    private int id;

    /**
     * Liste des clients de la banque
     * Paire <id client, liste des ids de compte></id>
     */
    private HashMap<Integer, ArrayList<Integer>> clientAccounts;

    /**
     * Liste des comptes dans cette banque.
     * Paire <id de compte> <Compte>
     */
    private HashMap<Integer, Account> accounts;

    public Bank() {
        this.id = BANK_TOKEN++;
        this.clientAccounts = new HashMap<Integer, ArrayList<Integer>>();
        this.accounts = new HashMap<Integer, Account>();
    }

    private boolean isClient(int clientId) {
        return this.clientAccounts.containsKey(clientId);
    }

    private ArrayList<Integer> getAccouts(int clientId) {
        return this.clientAccounts.get(clientId);
    }


    @Override
    public int createClient() {
        return 0;
    }

    @Override
    public int openAccount(int clientId) {
        return 0;
    }

    @Override
    public double getAccountBalance(int clientId, int accountId) {
        return 0;
    }

    @Override
    public int[] getAccountIds(int clientId) {
        return new int[0];
    }
}
