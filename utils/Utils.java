package utils;

import BankIDL.TransactionResult;
import BankIDL.TransactionState;

public class Utils {
    public static String StateToString(TransactionState state) {
        if (state == TransactionState.WAITING) {
            return "WAITING";
        } else if (state == TransactionState.CONFIRMED) {
            return "CONFIRMED";
        } else if (state == TransactionState.DONE) {
            return "DONE";
        } else if (state == TransactionState.CANCELED) {
            return "CANCELED";
        }
        return "STATE NON REPERTORIE";
    }

    public static String ResultToString(TransactionResult result) {
        if (result == TransactionResult.SUCCESS) {
            return "SUCCESS";
        } else if (result == TransactionResult.ERROR_CLIENT_INEXISTANT) {
            return "ERROR_CLIENT_INEXISTANT";
        } else if (result == TransactionResult.ERROR_ACCESS_DENIED) {
            return "ERROR_ACCESS_DENIED";
        } else if (result == TransactionResult.ERROR_ACCOUNT_DEST_INEXISTANT) {
            return "ERROR_ACCOUNT_DEST_INEXISTANT";
        } else if (result == TransactionResult.ERROR_ACCOUNT_INEXISTANT) {
            return "ERROR_ACCOUNT_INEXISTANT";
        } else if (result == TransactionResult.ERROR_AMOUNT_INVALID) {
            return "ERROR_AMOUNT_INVALID";
        }
        return "ERREUR NON REPERTORIEE";
    }
}
