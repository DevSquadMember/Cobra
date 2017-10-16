public class Account {
    private int id;
    private int clientId;

    public Account(int id, int clientId) {
        this.id = id;
        this.clientId = clientId;
    }

    public int getId() {
        return this.id;
    }

    public boolean hasAccess(int clientId) {
        return this.clientId == clientId;
    }
}
