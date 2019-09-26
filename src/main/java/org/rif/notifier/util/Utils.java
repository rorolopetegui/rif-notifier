package org.rif.notifier.util;

import java.security.SecureRandom;
import java.util.Base64;

public class Utils {

    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe

    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe

    public static Boolean isClass(String className){
        try  {
            Class.forName(className);
            return true;
        }  catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
