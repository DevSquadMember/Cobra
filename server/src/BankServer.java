package server.src;

import BankIDL.IBank;
import BankIDL.IBankHelper;
import BankIDL.IInterBank;
import BankIDL.IInterBankHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class BankServer {

    public static void main(String args[]) throws Exception {
        // Initialisation d'ORB
        ORB orb = ORB.init(args, null);

        org.omg.CORBA.Object objRef = orb.resolve_initial_references("RootPOA");
        POA rootpoa = POAHelper.narrow(objRef);
        rootpoa.the_POAManager().activate();

        objRef = orb.resolve_initial_references("NameService");
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);


        /// Récupération de l'interbank
        objRef = ncRef.resolve_str("interbank.interbank");
        IInterBank interBank = IInterBankHelper.narrow(objRef);

        Bank bank = new Bank(interBank, Integer.parseInt(args[args.length - 1]));

        objRef = rootpoa.servant_to_reference(bank);

        IBank bankRef = IBankHelper.narrow(objRef);

        String sn = bank.bankId() != -1 ? bank.bankId() + ".bank" : "test.bank";
        NameComponent path[ ] = ncRef.to_name(sn);
        ncRef.rebind(path, bankRef);

        interBank.register(bankRef);

        orb.run();
    }
}
