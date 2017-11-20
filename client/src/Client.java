package client.src;

import BankIDL.IBank;
import BankIDL.IBankHelper;
import BankIDL.OperationResult;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public class Client {

    private static void test(IBank bank) {
        int client1 = bank.createClient();
        int client2 = bank.createClient();
        int client3 = -1;

        int account11 = bank.openAccount(client1);
        int account21 = -1;
        int account31 = bank.openAccount(client3);

        // Test du solde nul
        assert bank.getAccountBalance(client1, account11) == 0.;

        // Test avec client qui n'a pas de compte
        assert bank.deposit(client3, account31, 100) == OperationResult.ERROR_CLIENT_INEXISTANT;

        // Test avec client sans compte
        client3 = bank.createClient();
        assert bank.deposit(client3, account31, 100) == OperationResult.ERROR_ACCOUNT_INEXISTANT;

        // Test avec compte n'appartenant pas au client
        assert bank.deposit(client3, account11, 100) == OperationResult.ERROR_ACCESS_DENIED;

        account31 = bank.openAccount(client3);
        int account32 = bank.openAccount(client3);
        assert bank.getAccountBalance(client3, account32) == 0.;

        // Test de dépôt et retrait avec montant négatif et nul
        assert bank.deposit(client3, account31, -3) == OperationResult.ERROR_AMOUNT_INVALID;
        assert bank.deposit(client3, account31, 0) == OperationResult.ERROR_AMOUNT_INVALID;
        assert bank.withdraw(client3, account31, -10) == OperationResult.ERROR_AMOUNT_INVALID;
        assert bank.withdraw(client3, account31, 0) == OperationResult.ERROR_AMOUNT_INVALID;

        // Test de dépôt autorisé
        assert bank.deposit(client1, account11, 50) == OperationResult.SUCCESS;

        // Test de solde après dépôt
        assert bank.getAccountBalance(client1, account11) == 50.;

        // Test de retrait autorisé
        assert bank.withdraw(client3, account31, 50) == OperationResult.SUCCESS;

        // Test de solde après retrait
        assert bank.getAccountBalance(client3, account31) == -50.;

        /** Test de virement entre comptes **/

        // Compte destinataire inexistant
        assert bank.transfer(client1, account11, account21, 20.) == OperationResult.ERROR_ACCOUNT_DEST_INEXISTANT;

        account21 = bank.openAccount(client2);
        assert bank.getAccountBalance(client2, account21) == 0.;

        // Transfert autorisé
        assert bank.transfer(client1, account11, account21, 20.) == OperationResult.SUCCESS;
        assert bank.getAccountBalance(client2, account21) == 20.;
        assert bank.getAccountBalance(client1, account11) == 30.;

        // Transfert vers son propre compte
        bank.deposit(client3, account32, 200);
        assert bank.getAccountBalance(client3, account32) == 200.;
        assert bank.transfer(client3, account32, account31, 120.) == OperationResult.SUCCESS;
        assert bank.getAccountBalance(client3, account32) == 80.;
        assert bank.getAccountBalance(client3, account31) == 70.;
    }

    public static void main(String args[]) throws Exception {
        org.omg.CORBA.Object objRef;

        ORB orb = ORB.init(args, null);

        objRef = orb.resolve_initial_references("NameService");
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

        objRef = ncRef.resolve_str("bank.bank");

        IBank bank = IBankHelper.narrow(objRef);

        test(bank);
    }
}
	
	
