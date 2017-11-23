package server.src;

import BankIDL.*;

import java.util.ArrayList;
import java.util.HashMap;

import static utils.Utils.ResultToString;
import static utils.Utils.StateToString;

public class Bank extends IBankPOA {

    private IInterBank interBank;

    private static int ACCOUNT_TOKEN = 0;
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

    private HashMap<Integer, BankTransaction> waitingTransactions;

    Bank(IInterBank interBank) {
        this(interBank, BANK_TOKEN++);
    }

    Bank(IInterBank interBank, int id) {
        this.interBank = interBank;
        System.out.println("ID is " + id);
        this.id = id;
        this.clientAccounts = new HashMap<Integer, ArrayList<Integer>>();
        this.accounts = new HashMap<Integer, Account>();
        this.waitingTransactions = new HashMap<Integer, BankTransaction>();
    }

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
    public TransactionResult withdraw(int clientId, int accountId, double amount) {
        BankOperation operation = new BankOperation(clientId, accountId, amount, TransactionType.WITHDRAW);
        TransactionResult result = operation.check();
        if (result == TransactionResult.SUCCESS)
            operation.execute();
        return result;
    }

    @Override
    public TransactionResult deposit(int clientId, int accountId, double amount) {
        BankOperation operation = new BankOperation(clientId, accountId, amount, TransactionType.DEPOSIT);
        TransactionResult result = operation.check();
        if (result == TransactionResult.SUCCESS)
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
    public void transfer(int clientId, int accountIdSrc, int bankIdDest, int accountIdDest, double amount) {
        System.out.println("Preparing transfer");
        BankOperation operation = new BankOperation(clientId, accountIdSrc, amount, TransactionType.WITHDRAW);
        if (operation.check(true) == TransactionResult.SUCCESS) {
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
        }
        /*BankOperation opWithdraw = new BankOperation(clientId, accountIdSrc, amount, Operation.WITHDRAW);
        BankOperation opDeposit = new BankOperation(clientId, accountIdDest, amount, Operation.DEPOSIT, false);
        TransactionResult result = opWithdraw.check();
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
        return result;*/
    }

    @Override
    public void prepareTransaction(BankTransaction transaction) {
        BankOperation operation = new BankOperation(transaction);
        TransactionResult result = operation.check();
        this.interBank.registeredTransaction(transaction.id, result);
    }

    @Override
    public void executeTransaction(BankTransaction transaction) {
        if (transaction.state == TransactionState.CANCELED) {
            System.out.println("Transaction " + transaction.id + " annulée : " + ResultToString(transaction.result));
            this.waitingTransactions.remove(transaction.id);
        }

        System.out.println("Exécution de la transaction : " + transaction.id + " - " + StateToString(transaction.state));
        BankOperation operation = new BankOperation(transaction);
        TransactionResult result = operation.check();
        System.out.println("Résultat de la transaction : " + ResultToString(transaction.result));
        if (result == TransactionResult.SUCCESS) {
            operation.execute();
        }
        this.interBank.registeredTransaction(transaction.id, result);

        if (transaction.state == TransactionState.CONFIRMED) {
            System.out.println("Transaction " + transaction.id + " effectuée");
            this.waitingTransactions.remove(transaction.id);
        }
    }


    @GET
    @Path("/getClientId")
    @Produces("text/plain")
    public int getClientIdRest()
    {
	return this.createClient();
    }

    @GET
    @Path("/getNewAccount/{clientId}")
    @Produces("text/plain")
    public int getNewAccountRest(@PathParam("clientId") int clientId)
    {
	return this.openAccount(clientId);
    }

    @POST
    @Path("/deposit")
    @Consumes(MediaType.TEXT_XML)
    @Produces("text/plain")
    public TransactionResult depositRest(TransactionObject t)
    {
	return this.deposit(t.clientId,t.accountId,t.amount);
    }

    @POST
    @Path("/withdraw")
    @Consumes(MediaType.TEXT_XML)
    @Produces("text/plain")
    public TransactionResult withdrawRest(TransactionObject t)
    {
	return this.withdraw(t.clientId,t.accountId,t.amount);
    }

    @GET
    @Path("/getAccountId/{clientId}")
    @Produces("text/plain")
    public int[] getAccountIdsRest(@PathParam("clientId") int clientId)
    {
	return this.getAccountsIds(clientId);
    }

    @GET
    @Path("/getBalance/{clientId}/{accountId}")
    @Produces("text/plain")
    public int getBalanceRest(@PathParam("clientId") int clientId,@PathParam("accountId") int accountId)
    {
	return getAccountBalance(clientId,accountId);
    }

    @POST
    @Path("/transfer")
    @Consumes(MediaType.TEXT_XML)
    @Produces("text/plain")
    public TransactionResult transferRest(TransferObject t)
    {
	return transfer(t.clientId,t.accountSrcId,t.bankIdDest,t.accountIdDest,t.amount);
    }
}
