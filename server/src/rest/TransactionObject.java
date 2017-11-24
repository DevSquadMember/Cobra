package server.src.rest;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="transaction")
public class TransactionObject {

    public TransactionObject() {
        this(-1, -1, -1, -1, 0);
    }

    public TransactionObject(int a,int b,int c,int d, double e) {
        this.clientId = a;
        this.accountIdSrc = b;
        this.bankIdDest = c;
        this.accountIdDest = d;
        this.amount = e;
    }

    @XmlElement(name="clientId")
    int clientId;

    @XmlElement(name="accountIdSrc")
    int accountIdSrc;

    @XmlElement(name="amount")
    double amount;

    @XmlElement(name="bankIdDest")
    int bankIdDest;

    @XmlElement(name="accountIdDest")
    int accountIdDest;
}
