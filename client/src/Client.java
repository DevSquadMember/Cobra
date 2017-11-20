package client.src;

import BankIDL.IBank;
import BankIDL.IBankHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public class Client
{
    public static void main(String args[]) throws Exception
    {
        org.omg.CORBA.Object objRef;

        ORB orb = ORB.init(args, null);

        objRef = orb.resolve_initial_references("NameService");
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

        objRef = ncRef.resolve_str("bank.bank");

        IBank bankRef = IBankHelper.narrow(objRef);

        int myId = bankRef.createClient();
        int myAccount = bankRef.openAccount(myId);

        /*System.out.println(bankRef.deposit(myId,myAccount,100));
        System.out.println(bankRef.withdraw(myId,myAccount,150));
        System.out.println(bankRef.withdraw(myId,myAccount,50));*/
        System.out.println(bankRef.getAccountBalance(myId,myAccount));
    }
}
	
	
