package server.src;

public class Account {

    /** Identifiant unique du compte */
    private int id;

    /** Identifiant du client (unique dans la banque */
    private int clientId;

    /** Solde du compte */
    private double balance;

    public Account(int id, int clientId) {
        this.id = id;
        this.clientId = clientId;
        this.balance = 0.;
    }

    public int getId() {
        return this.id;
    }

    public boolean hasAccess(int clientId) {
        return this.clientId == clientId;
    }

    public void deposit(double amount) {
        this.balance += amount;
    }

    public void withdraw(double amount) {
        this.balance -= amount;
    }

    public double getBalance() {
        return this.balance;
    }
}
