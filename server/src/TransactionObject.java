import javax.xml.bind.annotation.*;

@XmlRootElement(name="transaction")
public class TransactionObject
{
    public TransactionObject()
    {
	clientId = -1;
	accountSrcId = -1;
	bankIdDest = -1;
	accountIdDest = -1;
	amount = 0;
    }

    public TransactionObject(int a,int b,int c,int d, double e)
    {
	clientId = a;
	accountSrcId = b;
	bankIdDest = c;
	accountIdDest = d
	amount = e;
    }
    @XmlElement(name="clientId")
    int clientId;
    @XmlElement(name="accountSrcId")
    int accountSrcId;
    @XmlElement(name="amount")
    double amount;
    @XmlElement(name="bankIdDest")
    int bankIdDest;
    @XmlElement(name="accountIdDest")
    int accountIdDest;
}
