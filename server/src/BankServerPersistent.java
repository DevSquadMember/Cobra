package server.src;

import BankIDL.IBank;
import BankIDL.IBankHelper;
import BankIDL.IInterBank;
import BankIDL.IInterBankHelper;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;

public class BankServerPersistent {

    public static Integer id;

    public static void main(String args[]) {
        id = Integer.parseInt(args[args.length - 1]);

        System.out.println("Server.main");
        Properties properties = System.getProperties();
        properties.put( "org.omg.CORBA.ORBInitialHost", "localhost" );
        properties.put( "org.omg.CORBA.ORBInitialPort", "1050" );

        // Initialisation d'ORB
        ORB orb = ORB.init(args, properties);

        install(orb);
    }

    public static void install(org.omg.CORBA.ORB orb) {
        try {
            System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("file-bank.txt")), true));
            System.setErr(new PrintStream(new BufferedOutputStream(new FileOutputStream("file-bank.err.txt")), true));
            org.omg.CORBA.Object objRef = orb.string_to_object("corbaname::localhost:1050#interbank");
            // Récupération de l'InterBank
            IInterBank interBank = IInterBankHelper.narrow(objRef);
            Bank bank = new Bank(interBank, id);

            // Création d'un POA persistant
            objRef = orb.resolve_initial_references("RootPOA");
            POA rootpoa = POAHelper.narrow(objRef);
            Policy[] persistentPolicy = new Policy[1];
            persistentPolicy[0] = rootpoa.create_lifespan_policy(LifespanPolicyValue.PERSISTENT);
            POA persistentPOA = rootpoa.create_POA("childPOA", null, persistentPolicy);
            persistentPOA.the_POAManager().activate();

            // Association de la bank au POA persistant
            persistentPOA.activate_object(bank);

            org.omg.CORBA.Object obj = orb.resolve_initial_references("NameService");
            NamingContextExt rootContext = NamingContextExtHelper.narrow(obj);

            NameComponent[] nc = rootContext.to_name(id + ".bank");
            objRef = persistentPOA.servant_to_reference(bank);
            rootContext.rebind(nc, objRef);

            IBank bankRef = IBankHelper.narrow(objRef);

            ///interBank.register(bankRef);

            // Lancement du serveur orb
            orb.run();
        } catch ( Exception e ) {
            System.err.println( "Exception in Persistent Server Startup " + e );
        }
    }
}
