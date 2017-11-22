package server.src;

import BankIDL.IInterBank;
import BankIDL.IInterBankHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class Server {
    public static void main(String args[]) throws Exception {
        // Initialisation d'ORB
        ORB orb = ORB.init(args, null);

        org.omg.CORBA.Object objRef = orb.resolve_initial_references("RootPOA");
        POA rootpoa = POAHelper.narrow(objRef);
        rootpoa.the_POAManager().activate();

        objRef = orb.resolve_initial_references("NameService");
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);


        /** CODE APPLICATIF DU SERVER **/
        InterBank interBank = new InterBank();

        objRef = rootpoa.servant_to_reference(interBank);

        IInterBank bankRef = IInterBankHelper.narrow(objRef);

        NameComponent path[ ] = ncRef.to_name("interbank.interbank");
        ncRef.rebind(path, bankRef);

        orb.run();
    }
}
