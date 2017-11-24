package server.src;

import BankIDL.*;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import static utils.Utils.ResultToString;
import static utils.Utils.StateToString;

public class Bank extends IBankPOA implements Serializable {

    private static final long serialVersionUID = 1350092881346723535L;

    transient private IInterBank interBank;

    private static int ACCOUNT_TOKEN = 0;
    private static int CLIENT_TOKEN = 0;
    private static int BANK_TOKEN = 0;

    private int id;

    /**
     * Liste des clients de la banque
     * Paire <id client, liste des ids de compte></id>
     */
    private HashMap<Integer, ArrayList<Integer>> clientAccounts = new HashMap<Integer, ArrayList<Integer>>();

    /**
     * Liste des comptes dans cette banque.
     * Paire <id de compte> <Compte>
     */
    private HashMap<Integer, Account> accounts = new HashMap<Integer, Account>();

    private HashMap<Integer, BankTransaction> waitingTransactions = new HashMap<Integer, BankTransaction>();

    Bank(IInterBank interBank) {
        this(interBank, BANK_TOKEN++);
    }

    Bank(IInterBank interBank, int id) {
        this.interBank = interBank;
        System.out.println("Connectée en tant que banque " + id);
        this.id = id;
    }

    public void setInterBank(IInterBank interBank) {
        this.interBank = interBank;
    }

    /** SERIALIZABLE **/

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeInt(this.id);
        out.writeInt(CLIENT_TOKEN);
        out.writeInt(ACCOUNT_TOKEN);
        out.writeObject(this.clientAccounts);
        out.writeObject(this.accounts);
        out.writeObject(this.waitingTransactions);
    }

    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.id = in.readInt();
        CLIENT_TOKEN = in.readInt();
        ACCOUNT_TOKEN = in.readInt();
        this.clientAccounts = (HashMap<Integer, ArrayList<Integer>>) in.readObject();
        this.accounts = (HashMap<Integer, Account>) in.readObject();
        this.waitingTransactions = (HashMap<Integer, BankTransaction>) in.readObject();
    }

    private void readObjectNoData() throws ObjectStreamException {

    }

    HashMap<Integer, ArrayList<Integer>> getClientAccounts() {
        return clientAccounts;
    }

    /** END OF SERIALIZABLE **/

    private boolean isClient(int clientId) {
        return this.clientAccounts.containsKey(clientId);
    }

    private ArrayList<Integer> getAccounts(int clientId) {
        return this.clientAccounts.get(clientId);
    }

    private class BankOperation {
        BankTransaction transaction;
        Account account;
        int clientId;

        BankOperation(int clientId, int accountId, double amount, TransactionType type) {
            this.clientId = clientId;
            this.transaction = new BankTransaction();
            this.transaction.accountIdSrc = accountId;
            this.transaction.amount = amount;
            this.transaction.type = type;
            this.transaction.bankIdSrc = bankId();
        }

        BankOperation(BankTransaction transaction) {
            this.clientId = -1;
            this.transaction = transaction;
        }

        TransactionResult check() {
            return check(false);
        }

        TransactionResult check(Boolean clientCheck) {
            int accountId;
            TransactionResult accountInexistant;
            if (bankId() == this.transaction.bankIdSrc) {
                accountId = this.transaction.accountIdSrc;
                accountInexistant = TransactionResult.ERROR_ACCOUNT_INEXISTANT;
            } else {
                accountId = this.transaction.accountIdDest;
                accountInexistant = TransactionResult.ERROR_ACCOUNT_DEST_INEXISTANT;
            }

            if (this.transaction.amount <= 0) {
                this.transaction.result = TransactionResult.ERROR_AMOUNT_INVALID;
            } else if (!clientCheck || isClient(this.clientId)) { // Le client existe
                // Le compte existe
                if (accounts.containsKey(accountId)) {
                    Account account = accounts.get(accountId);
                    // Le compte appartient au client
                    if (!clientCheck || account.hasAccess(this.clientId)) {
                        this.account = account;
                        this.transaction.result = TransactionResult.SUCCESS;
                    }
                    else
                        this.transaction.result = TransactionResult.ERROR_ACCESS_DENIED;
                } else
                    this.transaction.result = accountInexistant;
            } else
                this.transaction.result = TransactionResult.ERROR_CLIENT_INEXISTANT;
            return this.transaction.result;
        }

        void execute() {
            if (this.transaction.type == TransactionType.DEPOSIT) {
                this.account.deposit(this.transaction.amount);
            } else if (this.transaction.type == TransactionType.WITHDRAW) {
                this.account.withdraw(this.transaction.amount);
            } else if (this.transaction.type == TransactionType.TRANSFER) {
                if (this.transaction.bankIdSrc == bankId()) {
                    this.account.withdraw(this.transaction.amount);
                } else if (this.transaction.bankIdDest == bankId()) {
                    this.account.deposit(this.transaction.amount);
                }
            }
        }
    }

    @Override
    public int bankId() {
        return this.id;
    }

    @Override
    public int createClient() {
        int clientId = CLIENT_TOKEN;
        CLIENT_TOKEN++;
        System.out.println("Création du client : " + clientId);
        this.clientAccounts.put(clientId, new ArrayList<Integer>());
        return clientId;
    }

    @Override
    public int openAccount(int clientId) {
        if (isClient(clientId)) {
            int accountId = ACCOUNT_TOKEN;
            ACCOUNT_TOKEN++;

            System.out.println("Ouverture d'un compte " + accountId + " pour le client " + clientId);
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
    public TransactionResult closeAccount(int clientId, int accountId) {
        if (isClient(clientId)) {
            if (this.accounts.containsKey(accountId)) {
                Account account = this.accounts.get(accountId);
                if (account.hasAccess(clientId)) {
                    this.accounts.remove(accountId);
                    this.clientAccounts.get(clientId).remove(accountId);
                    return TransactionResult.SUCCESS;
                } else {
                    return TransactionResult.ERROR_ACCESS_DENIED;
                }
            } else {
                return TransactionResult.ERROR_ACCOUNT_INEXISTANT;
            }
        } else {
            return TransactionResult.ERROR_CLIENT_INEXISTANT;
        }
    }

    @Override
    public TransactionResult withdraw(int clientId, int accountId, double amount) {
        System.out.println("Retrait du client " + clientId + " sur le compte " + accountId + " d'un montant de " + amount);
        BankOperation operation = new BankOperation(clientId, accountId, amount, TransactionType.WITHDRAW);
        TransactionResult result = operation.check(true);
        if (result == TransactionResult.SUCCESS)
            operation.execute();
        return result;
    }

    @Override
    public TransactionResult deposit(int clientId, int accountId, double amount) {
        System.out.println("Dépôt du client " + clientId + " sur le compte " + accountId + " d'un montant de " + amount);
        BankOperation operation = new BankOperation(clientId, accountId, amount, TransactionType.DEPOSIT);
        TransactionResult result = operation.check(true);
        if (result == TransactionResult.SUCCESS)
            operation.execute();
        return result;
    }

    @Override
    public double getAccountBalance(int clientId, int accountId) {
        System.out.println("Demande de solde du compte " + accountId);
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
        System.out.println("Demande de la liste des comptes du client " + clientId);
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
    public void transfer(int clientId, int accountIdSrc, int bankIdDest, int accountIdDest, double amount) {
        // Transfert intrabancaire
        if (bankIdDest == bankId()) {
            System.out.println("Transfert intrabancaire du compte " + accountIdSrc + " vers " + accountIdDest + " d'un montant de " + amount);
            BankOperation opWithdraw = new BankOperation(clientId, accountIdSrc, amount, TransactionType.WITHDRAW);
            BankOperation opDeposit = new BankOperation(clientId, accountIdDest, amount, TransactionType.DEPOSIT);
            TransactionResult result = opWithdraw.check(true);
            // Si le retrait est possible
            if (result == TransactionResult.SUCCESS) {
                result = opDeposit.check();
                // Et que le dépôt également
                if (result == TransactionResult.SUCCESS) {
                    opWithdraw.execute();
                    opDeposit.execute();
                } else if (result == TransactionResult.ERROR_ACCOUNT_INEXISTANT) {
                    result = TransactionResult.ERROR_ACCOUNT_DEST_INEXISTANT;
                }
            }
        } else {
            System.out.println("Préparation du transfert du client " + clientId + " compte " + accountIdSrc + " vers le compte " +
                    accountIdDest + " de la banque " + bankIdDest + " montant : " + amount);
            BankOperation operation = new BankOperation(clientId, accountIdSrc, amount, TransactionType.WITHDRAW);
            TransactionResult result = operation.check(true);
            if (result == TransactionResult.SUCCESS) {
                BankTransaction transaction = new BankTransaction();
                transaction.id = -1;
                transaction.bankIdSrc = bankId();
                transaction.bankIdDest = bankIdDest;
                transaction.accountIdSrc = accountIdSrc;
                transaction.accountIdDest = accountIdDest;
                transaction.amount = amount;
                transaction.type = TransactionType.TRANSFER;
                transaction.result = TransactionResult.SUCCESS;
                transaction.state = TransactionState.WAITING;
                transaction.executionDate = "";
                transaction.id = this.interBank.registerTransaction(transaction);
                this.waitingTransactions.put(transaction.id, transaction);
            } else {
                System.out.println("Impossible d'effectuer la transaction - " + ResultToString(result));
            }
        }
    }

    @Override
    public void prepareTransaction(BankTransaction transaction) {
        BankOperation operation = new BankOperation(transaction);
        TransactionResult result = operation.check(true);
        this.interBank.registeredTransaction(transaction.id, result);
    }

    @Override
    public void executeTransaction(BankTransaction transaction) {
        ///System.out.println("La transaction " + transaction.id + " va être exécutée, appuyez sur Entrée pour continuer...");
        ///new Scanner(System.in).nextLine();

        if (transaction.state == TransactionState.CANCELED) {
            System.out.println("Transaction " + transaction.id + " annulée : " + ResultToString(transaction.result));
            this.waitingTransactions.remove(transaction.id);
        } else {
            System.out.println("Exécution de la transaction : " + transaction.id + " - " + StateToString(transaction.state));
            BankOperation operation = new BankOperation(transaction);
            TransactionResult result = operation.check();
            System.out.println("Résultat de la transaction : " + ResultToString(transaction.result));
            if (result == TransactionResult.SUCCESS) {
                operation.execute();
            }
        }
        this.interBank.registeredTransaction(transaction.id, transaction.result);

        if (transaction.state == TransactionState.CONFIRMED) {
            System.out.println("Transaction " + transaction.id + " effectuée");
            this.waitingTransactions.remove(transaction.id);
        }
    }
}