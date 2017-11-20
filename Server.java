import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import java.util.Properties;
import org.omg.CosNaming.*;

public class BankServer
{
public static void main(String args[]) throws Exception
    {
	ORB orb = ORB.init(args, null);

	org.omg.CORBA.Object objRef = orb.resolve_initial_references("RootPOA");
	POA rootpoa = POAHelper.narrow(objRef);
	rootpoa.the_POAManager().activate();


	objRef = orb.resolve_initial_references("NameService");
	NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);


	BankImpl bankImpl = new BankImpl();

	objRef = rootpoa.servant_to_reference(bankImpl);

	Bank bankRef = EchoHelper.narrow(objRef);

	NameComponent path[ ] = ncRef.to_name("bank.bank");
	ncRef.rebind(path, bankRef);

	orb.run();
    }
}
