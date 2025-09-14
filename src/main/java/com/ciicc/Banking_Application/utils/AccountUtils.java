package com.ciicc.Banking_Application.utils;

import java.time.Year;
import java.util.Random;

public class AccountUtils {

    private static final Random random = new Random();

    // Generate account number: 1 + year + random 5 digits
    public static String generateAccountNumber() {
        String prefix = "1";
        String year = String.valueOf(Year.now().getValue());
        int randomFive = 10000 + random.nextInt(99999); // ensures 5 digits
        return prefix + year + randomFive;
//        return year + randomFive;
    }
}
