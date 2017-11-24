package server.src.rest;

import BankIDL.IBankHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.ext.jaxrs.JaxRsApplication;

public class BankRESTServer {

    private static final int DEFAULT_PORT = 8182;

    private static void loadBank(String args[]) throws Exception {
        // Initialisation d'ORB
        ORB orb = ORB.init(args, null);

        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

        Integer bankId = Integer.parseInt(args[2]);
        objRef = ncRef.resolve_str(bankId + ".bank");

        BankManager.setIBank(IBankHelper.narrow(objRef));
    }

    public static void main(String args[]) throws Exception {
        loadBank(args);

        // create Component (as ever for Restlet)
        Component comp = new Component();
        int port = DEFAULT_PORT;
        if (args.length > 3) {
            port = Integer.parseInt(args[3]);
        }
        Server server = comp.getServers().add(Protocol.HTTP, port);

        // create JAX-RS runtime environment
        JaxRsApplication application = new JaxRsApplication(comp.getContext().createChildContext());

        // attach Application
        application.add(new BankApplication());

        // Attach the application to the component and start it
        comp.getDefaultHost().attach(application);
        comp.start();

        System.out.println("Server started on port " + server.getPort() + ", listening to connections...");
    }
}
