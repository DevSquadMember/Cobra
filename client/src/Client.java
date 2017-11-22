package client.src;

import BankIDL.IBank;
import BankIDL.IBankHelper;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

public class Client {

    public IBank bank;

    public Client(String args[]) throws InvalidName, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound {
        // Initialisation d'ORB
        ORB orb = ORB.init(args, null);

        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

        objRef = ncRef.resolve_str("bank.bank");

        this.bank = IBankHelper.narrow(objRef);
    }

    public static void main(String args[]) throws Exception {
        Client client = new Client(args);
    }
}
	
	
