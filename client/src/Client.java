package client.src;

import BankIDL.IBank;
import BankIDL.IBankHelper;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import java.util.Scanner;

public class Client {

    private static ORB orb = null;
    public IBank bank;

    enum MENU {
        DISPLAY_BALANCE,
        QUIT,
        UNDEFINED
    }

    public Client() {

    }

    public Client(String args[]) throws InvalidName, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound {
        // Initialisation d'ORB
        ORB orb = ORB.init(args, null);

        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

        objRef = ncRef.resolve_str("bank.bank");

        this.bank = IBankHelper.narrow(objRef);
    }

    private static IBank connectToBank(String args[], String bankId) throws Exception {
        if (orb == null) {
            // Initialisation d'ORB
            orb = ORB.init(args, null);
        }

        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
        String sn = bankId + ".bank";
        System.out.println("Trying to connect to Bank : " + sn);
        objRef = ncRef.resolve_str(bankId + ".bank");
        return IBankHelper.narrow(objRef);
    }

    public static MENU displayMenu() {
        System.out.println("-- Menu du client --");
        System.out.println("1 - Afficher le solde des comptes");
        System.out.println("2 - Quitter");
        System.out.println("Entrez votre sélection");
        String choice = new Scanner(System.in).nextLine();
        if (choice.equals("1")) {
            return MENU.DISPLAY_BALANCE;
        } else if (choice.equals("2")) {
            return MENU.QUIT;
        }
        return MENU.UNDEFINED;
    }

    public static void main(String args[]) throws Exception {
        IBank bank1 = connectToBank(args, "1");
        IBank bank2 = connectToBank(args, "2");

        int client1 = bank1.createClient();
        int client2 = bank2.createClient();

        int account1 = bank1.openAccount(client1);
        int account2 = bank2.openAccount(client2);

        bank1.transfer(client1, account1, bank2.bankId(), account2, 49);

        MENU choice = MENU.UNDEFINED;
        while (choice != MENU.QUIT) {
            choice = displayMenu();
            if (choice == MENU.DISPLAY_BALANCE) {
                System.out.println("Le solde du compte du client " + client1 + "(" + account1 + ") est de : " +
                        bank1.getAccountBalance(client1, account1));
                System.out.println("Le solde du compte du client " + client2 + "(" + account2 + ") est de : " +
                        bank2.getAccountBalance(client2, account2));
            }
        }
        System.out.println("Fermeture du client");
    }
}
	
	
