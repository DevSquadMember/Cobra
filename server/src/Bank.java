package server.src;

import BankIDL.IBankPOA;
import BankIDL.OperationResult;

import java.util.ArrayList;
import java.util.HashMap;

public class Bank extends IBankPOA {

    private static int ACCOUNT_TOKEN = 0;
    private static int CLIENT_TOKEN = 0;
    private static int BANK_TOKEN = 0;

    private enum Operation { WITHDRAW, DEPOSIT };

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

    private ArrayList<Integer> getAccounts(int clientId) {
        return this.clientAccounts.get(clientId);
    }


    private class BankOperation {
        int clientId;
        int accountId;
        double amount;
        Account account;
        Operation operation;
        boolean clientCheck;

        BankOperation(int clientId, int accountId, double amount, Operation operation) {
            this(clientId, accountId, amount, operation, true);
        }

        BankOperation(int clientId, int accountId, double amount, Operation operation, boolean clientCheck) {
            this.clientId = clientId;
            this.accountId = accountId;
            this.amount = amount;
            this.operation = operation;
            this.clientCheck = clientCheck;
        }

        OperationResult check() {
            if (amount <= 0) {
                return OperationResult.ERROR_AMOUNT_INVALID;
            }

            // Le client existe
            if (!clientCheck || isClient(clientId)) {
                // Le compte existe
                if (accounts.containsKey(accountId)) {
                    Account account = accounts.get(accountId);
                    // Le compte appartient au client
                    if (!clientCheck || account.hasAccess(clientId)) {
                        this.account = account;
                        return OperationResult.SUCCESS;
                    }
                    else
                        return OperationResult.ERROR_ACCESS_DENIED;
                } else
                    return OperationResult.ERROR_ACCOUNT_INEXISTANT;
            } else
                return OperationResult.ERROR_CLIENT_INEXISTANT;
        }

        void execute() {
            switch (this.operation) {
                case WITHDRAW:
                    this.account.withdraw(amount);
                    break;
                case DEPOSIT:
                    this.account.deposit(amount);
                    break;
            }
        }
    }

    @Override
    public int createClient() {
        int clientId = CLIENT_TOKEN;
        CLIENT_TOKEN++;
        this.clientAccounts.put(clientId, new ArrayList<Integer>());
        return clientId;
    }

    @Override
    public int openAccount(int clientId) {
        if (isClient(clientId)) {
            int accountId = ACCOUNT_TOKEN;
            ACCOUNT_TOKEN++;

            // On créé le compte
            this.accounts.put(accountId, new Account(accountId, clientId));
            // On ajoute l'id du compte à la liste des comptes du client
            this.clientAccounts.get(clientId).add(accountId);

            return accountId;
        }
        // Le client n'existe pas
        return -1;
    }

    @Override
    public OperationResult withdraw(int clientId, int accountId, double amount) {
        BankOperation operation = new BankOperation(clientId, accountId, amount, Operation.WITHDRAW);
        OperationResult result = operation.check();
        if (result == OperationResult.SUCCESS)
            operation.execute();
        return result;
    }

    @Override
    public OperationResult deposit(int clientId, int accountId, double amount) {
        BankOperation operation = new BankOperation(clientId, accountId, amount, Operation.DEPOSIT);
        OperationResult result = operation.check();
        if (result == OperationResult.SUCCESS)
            operation.execute();
        return result;
    }

    @Override
    public double getAccountBalance(int clientId, int accountId) {
        // Si le client est à cette banque, et que le compte existe
        if (isClient(clientId) && this.accounts.containsKey(accountId)) {
            Account account = this.accounts.get(accountId);
            // Si le compte appartient au client
            if (account.hasAccess(clientId)) {
                return account.getBalance();
            }
        }
        return -1;
    }

    @Override
    public int[] getAccountIds(int clientId) {
        if (isClient(clientId)) {
            ArrayList<Integer> accountIds = this.clientAccounts.get(clientId);
            int[] ids = new int[accountIds.size()];
            int i = 0;
            for (Integer id : accountIds) {
                ids[i] = id;
                i++;
            }
            return ids;
        }
        return new int[0];
    }

    @Override
    public OperationResult transfer(int clientId, int accountIdSrc, int accountIdDest, double amount) {
        BankOperation opWithdraw = new BankOperation(clientId, accountIdSrc, amount, Operation.WITHDRAW);
        BankOperation opDeposit = new BankOperation(clientId, accountIdDest, amount, Operation.DEPOSIT, false);
        OperationResult result = opWithdraw.check();
        // Si le retrait est possible
        if (result == OperationResult.SUCCESS) {
            result = opDeposit.check();
            // Et que le dépôt également
            if (result == OperationResult.SUCCESS) {
                opWithdraw.execute();
                opDeposit.execute();
            } else if (result == OperationResult.ERROR_ACCOUNT_INEXISTANT) {
                result = OperationResult.ERROR_ACCOUNT_DEST_INEXISTANT;
            }
        }
        return result;
    }
}
