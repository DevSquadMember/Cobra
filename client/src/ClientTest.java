package client.src;

import BankIDL.IBank;
import BankIDL.TransactionResult;
import BankIDL.TransactionResult;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import static org.junit.Assert.assertEquals;

public class ClientTest {
    
    private static IBank bank;

    @BeforeClass
    public static void load() {
        System.out.println("Test des accès à la banque côté client");
        try {
            Client client = new Client(new String[] {"-ORBInitRef", "NameService=corbaloc::localhost:1050/NameService"});
            bank = client.bank;
        } catch (InvalidName invalidName) {
            invalidName.printStackTrace();
        } catch (CannotProceed cannotProceed) {
            cannotProceed.printStackTrace();
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName invalidName) {
            invalidName.printStackTrace();
        } catch (NotFound notFound) {
            notFound.printStackTrace();
        }
    }

    @Test
    public void testCreateClientAccount() {
        System.out.print("- testCreateClientAccount");
        int client1 = bank.createClient();
        int account1 = bank.openAccount(client1);

        assertEquals(bank.getAccountBalance(client1, account1), 0., 0.);
        System.out.println(" - OK");
    }

    @Test
    public void testDepositNotClient() {
        System.out.print("- testDepositNotClient");
        int client1 = -1;
        int account1 = bank.openAccount(client1);

        assertEquals(bank.deposit(client1, account1, 100), TransactionResult.ERROR_CLIENT_INEXISTANT);
        System.out.println(" - OK");
    }

    @Test
    public void testDepositNoAccount() {
        System.out.print("- testDepositNoAccount");
        int client1 = bank.createClient();
        int account1 = -1;

        assertEquals(bank.deposit(client1, account1, 100), TransactionResult.ERROR_ACCOUNT_INEXISTANT);
        System.out.println(" - OK");
    }

    @Test
    public void testDepositNotClientAccount() {
        System.out.print("- testDepositNotClientAccount");
        int client1 = bank.createClient();
        int client2 = bank.createClient();

        int account1 = bank.openAccount(client1);
        int account2 = bank.openAccount(client2);

        assertEquals(bank.getAccountBalance(client1, account1), 0., 0.);
        assertEquals(bank.getAccountBalance(client2, account2), 0., 0.);

        assertEquals(bank.deposit(client1, account2, 100), TransactionResult.ERROR_ACCESS_DENIED);
        assertEquals(bank.deposit(client2, account1, 100), TransactionResult.ERROR_ACCESS_DENIED);

        assertEquals(bank.getAccountBalance(client1, account1), 0., 0.);
        assertEquals(bank.getAccountBalance(client2, account2), 0., 0.);
        System.out.println(" - OK");
    }

    @Test
    public void testOperationNegativeAmount() {
        System.out.print("- testOperationNegativeAmount");
        int client1 = bank.createClient();
        int account1 = bank.openAccount(client1);

        assertEquals(bank.deposit(client1, account1, -3), TransactionResult.ERROR_AMOUNT_INVALID);
        assertEquals(bank.deposit(client1, account1, 0), TransactionResult.ERROR_AMOUNT_INVALID);
        assertEquals(bank.withdraw(client1, account1, -10), TransactionResult.ERROR_AMOUNT_INVALID);
        assertEquals(bank.withdraw(client1, account1, 0), TransactionResult.ERROR_AMOUNT_INVALID);
        System.out.println(" - OK");
    }

    @Test
    public void testOperationOK() {
        System.out.print("- testOperationOK");
        int client1 = bank.createClient();
        int account1 = bank.openAccount(client1);

        assertEquals(bank.getAccountBalance(client1, account1), 0., 0.);
        assertEquals(bank.deposit(client1, account1, 50), TransactionResult.SUCCESS);
        assertEquals(bank.getAccountBalance(client1, account1), 50., 0.);

        assertEquals(bank.withdraw(client1, account1, 20), TransactionResult.SUCCESS);
        assertEquals(bank.getAccountBalance(client1, account1), 30., 0.);
        System.out.println(" - OK");
    }

    @Test
    public void testTransferToOwnAccount() {
        System.out.print("- testTransferToOwnAccount");
        int client1 = bank.createClient();
        int account1 = bank.openAccount(client1);
        int account2 = bank.openAccount(client1);

        assertEquals(bank.getAccountBalance(client1, account1), 0., 0.);
        assertEquals(bank.getAccountBalance(client1, account2), 0., 0.);
        bank.transfer(client1, account1, bank.bankId(), account2, 60.);

        /// TODO : wait ?
        assertEquals(bank.getAccountBalance(client1, account1), -60., 0.);
        assertEquals(bank.getAccountBalance(client1, account2), 60., 0.);
        System.out.println(" - OK");
    }

    @Test
    public void testTransferToOtherAccount() {
        System.out.print("- testTransferToOtherAccount");
        int client1 = bank.createClient();
        int client2 = bank.createClient();
        int account1 = bank.openAccount(client1);
        int account2 = bank.openAccount(client2);

        assertEquals(bank.getAccountBalance(client1, account1), 0., 0.);
        assertEquals(bank.getAccountBalance(client2, account2), 0., 0.);
        bank.transfer(client1, account1, bank.bankId(), account2, 70.);

        /// TODO : wait ?
        assertEquals(bank.getAccountBalance(client1, account1), -70., 0.);
        assertEquals(bank.getAccountBalance(client2, account2), 70., 0.);
        System.out.println(" - OK");
    }
}
