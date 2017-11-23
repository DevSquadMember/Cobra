import javax.xml.bind.annotation.*;

@XmlRootElement(name="transfertransaction")
public class TransferObject
{
    public TransferObject()
    {
	clientId = -1;
	accountId = -1;
	amount = 0;
    }

    public TransfereObject(int a,int b, double c)
    {
	clientId = a;
	accountId = b;
	amount = c;
    }
    @XmlElement(name="clientId")
    int clientId;
    @XmlElement(name="accountId")
    int accountId;
    @XmlElement(name="amount")
    double amount;
}
