import java.util.ArrayList;
import java.util.HashMap;

public class Bank {

    private static int CLIENT_TOKEN = 0;
    private static int BANK_TOKEN = 0;

    private int id;

    private ArrayList<Integer> clients;

    /**
     * Liste des comptes dans cette banque.
     * Paire <id de compte> <Compte>
     */
    private HashMap<Integer, Account> accounts;

    public Bank() {
        this.id = BANK_TOKEN++;
        this.clients = new ArrayList<Integer>();
        this.accounts = new HashMap<Integer, Account>();
    }

    private boolean isClient(int clientId) {
        return this.clients.contains(clientId);
    }


}
