package client.src.rest;

import BankIDL.TransactionResult;

import java.util.List;

class BankClient {

    // Id de la banque
    static int bankId() {
        return RequestClient.getIntResponse("bank", "");
    }

    // Creation d'un client
    static int createClient() {
        return RequestClient.postIntResponse("client", "");
    }

    // Liste des comptes d'un client
    static List<Integer> listAccounts(int clientId) {
        return RequestClient.getAccountsResponse("client/" + clientId, "");
    }

    // Ouverture d'un compte
    static int openAccount(int clientId) {
        return RequestClient.postIntResponse("client/" + clientId + "/account", "");
    }

    // Solde du compte
    static double accountBalance(int clientId, int accountId) {
        return RequestClient.getDoubleResponse("account/" + accountId, "clientId=" + clientId);
    }

    // Fermeture d'un compte
    static TransactionResult closeAccount(int clientId, int accountId) {
        return RequestClient.deleteResponse("client/" + clientId + "/account/" + accountId, "");
    }

    // Dépôt
    static TransactionResult deposit(int clientId, int accountId, double amount) {
        return RequestClient.postTransactionResultResponse("/account/" + accountId + "/deposit", "clientId=" + clientId + "&amount=" + amount);
    }

    // Retrait
    static TransactionResult withdraw(int clientId, int accountId, double amount) {
        return RequestClient.postTransactionResultResponse("/account/" + accountId + "/withdraw", "clientId=" + clientId + "&amount=" + amount);
    }

    // Transfert
    static void transfer(int clientId, int accountIdSrc, int bankIdDest, int accountIdDest, double amount) {
        String params = "clientId=" + clientId + "&amount=" + amount + "&bankId=" + bankIdDest + "&accountIdDest=" + bankIdDest;
        RequestClient.postResponse("/account/" + accountIdSrc + "/transfer", params);
    }
}
