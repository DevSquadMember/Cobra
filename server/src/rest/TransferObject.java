package server.src.rest;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="transfertransaction")
public class TransferObject {
    public TransferObject() {
        this(-1, -1, 0);
    }

    public TransferObject(int a,int b, double c) {
        this.clientId = a;
        this.accountId = b;
        this.amount = c;
    }
    @XmlElement(name="clientId")
    int clientId;

    @XmlElement(name="accountId")
    int accountId;

    @XmlElement(name="amount")
    double amount;
}
