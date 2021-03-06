package server.src;

import BankIDL.IBank;
import BankIDL.IBankHelper;
import BankIDL.IInterBank;
import BankIDL.IInterBankHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import static server.src.DataPersistent.loadBank;
import static server.src.DataPersistent.saveBank;

public class BankServer {

    private static int bankId;
    private static Bank bank;

    public static void main(String args[]) throws Exception {
        // Initialisation d'ORB
        ORB orb = ORB.init(args, null);

        org.omg.CORBA.Object objRef = orb.resolve_initial_references("RootPOA");
        POA rootpoa = POAHelper.narrow(objRef);
        rootpoa.the_POAManager().activate();

        objRef = orb.resolve_initial_references("NameService");
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

        /// Récupération de l'interbank
        IInterBank interBank;
        try {
            objRef = ncRef.resolve_str("interbank.interbank");
            interBank = IInterBankHelper.narrow(objRef);
        } catch (NotFound notFound) {
            interBank = null;
        }

        bankId = Integer.parseInt(args[args.length - 1]);
        bank = loadBank(interBank, bankId);

        objRef = rootpoa.servant_to_reference(bank);

        IBank bankRef = IBankHelper.narrow(objRef);

        String sn = bank.bankId() != -1 ? bank.bankId() + ".bank" : "test.bank";
        NameComponent path[] = ncRef.to_name(sn);
        ncRef.rebind(path, bankRef);

        if (interBank != null) {
            interBank.register(bankRef);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                saveBank(bank, bankId);
            }
        });

        orb.run();
    }
}
