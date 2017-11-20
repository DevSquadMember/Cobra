import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;

public class Client
{
    public static void main(String args[]) throws Exception
    {
	org.omg.CORBA.Object objRef;

	ORB orb = ORB.init(args, null);
	
	objRef = orb.resolve_initial_references("NameService");
	NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

	objRef = ncRef.resolve_str("bank.bank");

	IBank bankRef = BankHelper.narrow(objRef);

	int myId = bankRef.createClient();
	int myAccount = bankRef.openAccount(myId);

	System.out.println(bankRef.deposit(myId,myAccount,100));
	System.out.println(bankRef.withdraw(myId,myAccount,150));
	System.out.println(bankRef.withdraw(myId,myAccount,50));
	System.out.println(bankRef.getAccountBalance(myId,myAccount));

    }
}
	
	
