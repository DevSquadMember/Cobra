package server.src;

import BankIDL.*;
import org.omg.PortableInterceptor.SUCCESSFUL;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static utils.Utils.StateToString;

public class InterBank extends IInterBankPOA {

    private static int TRANSACTION_TOKEN = 0;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    // Liste des banques connectées à l'InterBank
    private HashMap<Integer, IBank> banks = new HashMap<Integer, IBank>();

    // Historique des transactions effectuées
    private ArrayList<BankTransaction> transactions = new ArrayList<BankTransaction>();

    // Transactions en cours d'exécution : en attente d'une réponse de la part de la banque
    private ArrayList<BankTransaction> waitingTransactions = new ArrayList<BankTransaction>();

    private String getDateToken() {
        return dateFormat.format(new Date());
    }

    @Override
    public IBank[] banks() {
        IBank[] iBanks = new IBank[this.banks.size()];
        int i = 0;
        for (IBank id : this.banks.values()) {
            iBanks[i] = id;
            i++;
        }
        return iBanks;
    }

    @Override
    public void register(IBank bank) {
        // Si la banque était déjà enregistrée, la référence précédente est remplacée par la nouvelle
        this.banks.put(bank.bankId(), bank);
        System.out.println("Banque enregistrée : " + bank.bankId());

        // Pour chaque transaction en attente
        for (BankTransaction transaction : this.waitingTransactions) {
            // Si elle est confirmée et que l'on attend que la banque source termine la transaction
            if ((transaction.bankIdSrc == bank.bankId() && transaction.state == TransactionState.CONFIRMED) ||
                    // Ou que la transaction attend que la banque destinataire effectue la transaction
                    (transaction.bankIdDest == bank.bankId() && transaction.state == TransactionState.WAITING)) {
                bank.executeTransaction(transaction);
            }
        }
    }

    @Override
    public int registerTransaction(BankTransaction transaction) {
        System.out.println("Register transaction");
        String date = getDateToken();
        System.out.println("- " + date + " : Demande de transfert depuis la banque " + transaction.bankIdSrc +
                " vers la banque " + transaction.bankIdDest + " de " + transaction.amount + "€");
        transaction.id = TRANSACTION_TOKEN++;
        // On passe la transaction à WAITING pour attendre la réponse de la banque destinataire
        transaction.state = TransactionState.WAITING;
        // On passe la transaction dans la file d'attente
        this.waitingTransactions.add(transaction);
        IBank bank = this.banks.get(transaction.bankIdDest);
        if (bank == null) {
            // prévenir la banque d'une erreur ? Tout stocker dans Transaction avec un id ?
            return transaction.id;
        }
        System.out.println("Redirection de la transaction vers la banque " + bank.bankId());
        bank.executeTransaction(transaction);
        return transaction.id;
    }

    private BankTransaction find(int transactionId) {
        for (BankTransaction transaction : this.waitingTransactions) {
            if (transaction.id == transactionId) {
                return transaction;
            }
        }
        return null;
    }

    private void endTransaction(BankTransaction transaction) {
        transaction.state = TransactionState.DONE;
        transaction.executionDate = getDateToken();
        this.waitingTransactions.remove(transaction);
        this.transactions.add(transaction);
        System.out.println("Transaction " + transaction.id + " close");
    }

    @Override
    public void registeredTransaction(int transactionId, TransactionResult result) {
        BankTransaction transaction = find(transactionId);
        if (transaction == null) {
            System.out.println("Erreur, transaction " + transactionId + " est nulle.");
            return;
        }

        if (transaction.state == TransactionState.CANCELED) {
            endTransaction(transaction);
            return;
        }

        System.out.println("Retour de la transaction : " + transactionId + " - " + StateToString(transaction.state));

        transaction.result = result;
        IBank bank = null;

        if (result != TransactionResult.SUCCESS) {
            System.out.println("Transaction annulée");
            transaction.state = TransactionState.CANCELED;
            bank = this.banks.get(transaction.bankIdSrc);
        }

        if (transaction.state == TransactionState.WAITING) { // Transaction en attente de réponse de la banque destinataire
            // Réponse de la banque destinataire reçue
            transaction.state = TransactionState.CONFIRMED;
            bank = this.banks.get(transaction.bankIdSrc);
        } else if (transaction.state == TransactionState.CONFIRMED) { // Transaction en attente de confirmation de la banque source
            // Réponse de la banque source reçue
            endTransaction(transaction);
            System.out.println("Transaction effectuée");
        }

        if (bank != null) {
            System.out.println("La transaction va être exécuter par la banque " + bank.bankId());
            bank.executeTransaction(transaction);
        }
    }
}
