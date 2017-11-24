package server.src.rest;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name="accounts")
public class AccountsObject {

    @XmlElement(name="list")
    public List<Integer> accounts;

    public AccountsObject() {

    }

    public AccountsObject(List<Integer> accounts) {
        this.accounts = accounts;
    }

    public AccountsObject(int[] accounts) {
        this.accounts = new ArrayList<Integer>();
        for (int account : accounts) {
            this.accounts.add(account);
        }
    }
}
