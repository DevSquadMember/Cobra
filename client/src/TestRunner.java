package client.src;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("\n\t-- LANCEMENT DES TESTS --\n");
        Result result = JUnitCore.runClasses(ClientTest.class);

        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }

        int total = result.getRunCount() + result.getFailureCount() + result.getIgnoreCount();

        if (result.wasSuccessful()) {
            System.out.println("\n\t-- TESTS : OK --");
            System.out.println("Tests passés avec succès : " + result.getRunCount() + " / " + total);
        } else {
            System.out.println("-- TESTS : ERREUR");
            System.out.println("Tests passés avec succès : " + result.getRunCount() + " / " + total);
            System.out.println("Tests échoués : " + result.getFailureCount());
        }
        if (result.getIgnoreCount() > 0) {
            System.out.println("Tests ignorés : " + result.getIgnoreCount());
        }
    }
}
