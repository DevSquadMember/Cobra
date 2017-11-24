package client.src.rest;

import BankIDL.TransactionResult;

import java.io.IOException;
import java.util.List;

import static utils.Utils.ResultToString;

public class ClientRest {

    private static final int DEFAULT_PORT = 8182;

    public static void main(String[] args) throws IOException {
        // Ouverture du client de requête sur le port donné en paramètre
        int port = DEFAULT_PORT;
        if (args.length > 1) {
            port = Integer.parseInt(args[args.length - 1]);
        }
        RequestClient.open(port);

        int bankId = BankClient.bankId();
        System.out.println("Communication avec la banque " + bankId);

        // Creation d'un client
        int clientId = BankClient.createClient();
        System.out.println("Création du client : " + clientId);

        //ouverture d'un compte
        int accountId = BankClient.openAccount(clientId);
        System.out.println("Ouverture d'un compte : " + accountId);

        double balance = BankClient.accountBalance(clientId, accountId);
        System.out.println("Solde du compte : " + balance);

        // Dépôt de 20€
        TransactionResult result = BankClient.deposit(clientId, accountId, 20);
        System.out.println("Résultat du dépôt : " + ResultToString(result));

        balance = BankClient.accountBalance(clientId, accountId);
        System.out.println("Solde du compte : " + balance);

        // Liste des comptes du client
        List<Integer> accounts = BankClient.listAccounts(clientId);
        for (Integer account : accounts) {
            System.out.println("Account id : " + account);
        }

        // Retrait de 42€
        result = BankClient.withdraw(clientId, accountId, 42);
        System.out.println("Résultat du retrait : " + ResultToString(result));

        balance = BankClient.accountBalance(clientId, accountId);
        System.out.println("Solde du compte : " + balance);

        int account2 = BankClient.openAccount(clientId);
        System.out.println("Ouverture d'un compte : " + accountId);

        // Dépôt de 400€
        result = BankClient.deposit(clientId, account2, 400);
        System.out.println("Résultat du dépôt : " + ResultToString(result));

        // Transfert de 120€ du compte 2 vers le compte 1
        BankClient.transfer(clientId, account2, bankId, accountId, 120);

        // Liste des comptes du client
        accounts = BankClient.listAccounts(clientId);
        for (Integer account : accounts) {
            balance = BankClient.accountBalance(clientId, account);
            System.out.println("Solde du compte " + account + " : " + balance);
        }

        // Fermeture du Client de requêtes
        RequestClient.close();
    }
}
