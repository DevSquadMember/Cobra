package client.src;

import BankIDL.IBank;
import BankIDL.IInterBank;
import BankIDL.IInterBankHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.util.Scanner;

public class ClientPersistent {
    public static void main(String args[]) throws Exception {
        // Initialisation d'ORB
        /*ORB orb = ORB.init(args, null);
        org.omg.CORBA.Object objRef = orb.string_to_object("corbaname::localhost:1050#PersistentServer");*/

        // Initialisation d'ORB
        ORB orb = ORB.init(args, null);

        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

        /// Récupération de l'interbank
        objRef = ncRef.resolve_str("interbank");
        IInterBank interBank = IInterBankHelper.narrow(objRef);

        //NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

        //objRef = ncRef.resolve_str("bank_default_servant_poa");
        // Récupération de l'InterBank
        //IInterBank interBank = IInterBankHelper.narrow(objRef);

        if (interBank == null) {
            System.out.println("Interbank is null");
        } else {
            System.out.println("Interbank OK");
        }

        for (int i = 0 ; i < 10 ; i++) {
            System.out.println( "Calling Persistent Server.." );
            IBank[] banks = interBank.banks();
            System.out.println("Banques connectées : " + banks.length);
            for (IBank b : banks) {
                System.out.println("Banque : " + b.bankId());
            }
            System.out.println("Tapez sur entrée pour continuer");
            new Scanner(System.in).nextLine();
        }
    }
}
