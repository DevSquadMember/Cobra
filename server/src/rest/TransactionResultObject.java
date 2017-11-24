package server.src.rest;

import BankIDL.TransactionResult;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="transaction")
public class TransactionResultObject {

    @XmlElement(name="result")
    public int result;

    public TransactionResultObject() {
        this.result = TransactionResult.ERROR_ACCESS_DENIED.value();
    }

    public TransactionResultObject(TransactionResult result) {
        this.result = result.value();
    }
}
