package client.src;

import BankIDL.IBank;
import BankIDL.IBankHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public class Client {
    public static void main(String args[]) throws Exception {
        org.omg.CORBA.Object objRef;

        ORB orb = ORB.init(args, null);

        objRef = orb.resolve_initial_references("NameService");
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

        objRef = ncRef.resolve_str("bank.bank");

        IBank bank = IBankHelper.narrow(objRef);

        int myId = bank.createClient();
        int myAccount = bank.openAccount(myId);

        System.out.println(bank.deposit(myId, myAccount,100));
        System.out.println(bank.withdraw(myId, myAccount,150));
        System.out.println(bank.withdraw(myId, myAccount,50));
        System.out.println(bank.getAccountBalance(myId, myAccount));
    }
}
	
	
