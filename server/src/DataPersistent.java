package server.src;

import BankIDL.IInterBank;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class DataPersistent {

    private static String SAVE_DIRECTORY = "save";

    private static String INTERBANK_FILENAME = SAVE_DIRECTORY + "/interbank.ser";

    private static String getBankFileName(int bankId) {
        return SAVE_DIRECTORY + "/bank" + bankId + ".ser";
    }

    static Bank loadBank(IInterBank interBank, int bankId) {
        Bank bank = null;
        ObjectInputStream ois = null;
        String filename = getBankFileName(bankId);
        if (new File(filename).isFile()) {
            try {
                final FileInputStream file = new FileInputStream(filename);
                ois = new ObjectInputStream(file);
                bank = (Bank) ois.readObject();
                bank.setInterBank(interBank);
                System.out.println("Chargement de la banque " + bankId);
                HashMap<Integer, ArrayList<Integer>> clientAccounts = bank.getClientAccounts();
                System.out.println("Clients chargés : " + clientAccounts.size());
                /*for (Map.Entry<Integer, ArrayList<Integer>> clientAccount : clientAccounts.entrySet()) {
                    System.out.println("Compte du client " + clientAccount.getKey());
                    for (Integer account : clientAccount.getValue()) {
                        System.out.println("- compte Id : " + account + " - balance : " + bank.getAccountBalance(clientAccount.getKey(), account));
                    }
                }*/
            } catch (final java.io.IOException e) {
                e.printStackTrace();
            } catch (final ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ois != null) {
                        ois.close();
                    }
                } catch (final IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        if (bank == null) {
            bank = new Bank(interBank, bankId);
        }
        return bank;
    }

    private static void save(Serializable serializable, String filename) {
        ObjectOutputStream oos = null;
        try {
            File directory = new File(SAVE_DIRECTORY);
            if (!directory.isDirectory()) {
                directory.mkdir();
            }
            final FileOutputStream file = new FileOutputStream(filename);
            oos = new ObjectOutputStream(file);
            oos.writeObject(serializable);
            oos.flush();
        } catch (final java.io.IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null) {
                    oos.flush();
                    oos.close();
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    static void saveBank(Bank bank, int bankId) {
        System.out.println("Sauvegarde des données de la banque");
        save(bank, getBankFileName(bankId));
    }

    static InterBank loadInterBank() {
        InterBank interBank = null;
        ObjectInputStream ois = null;
        String filename = INTERBANK_FILENAME;
        if (new File(filename).isFile()) {
            try {
                final FileInputStream file = new FileInputStream(filename);
                ois = new ObjectInputStream(file);
                interBank = (InterBank) ois.readObject();
                System.out.println("Chargement de l'interbank ");
            } catch (final java.io.IOException e) {
                e.printStackTrace();
            } catch (final ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ois != null) {
                        ois.close();
                    }
                } catch (final IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        if (interBank == null) {
            interBank = new InterBank();
        }
        return interBank;
    }

    static void saveInterBank(InterBank interBank) {
        System.out.println("Sauvegarde des données de l'interbank");
        save(interBank, INTERBANK_FILENAME);
    }
}
