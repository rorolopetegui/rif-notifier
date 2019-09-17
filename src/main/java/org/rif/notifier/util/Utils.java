package org.rif.notifier.util;

public class Utils {
    public static Boolean isClass(String className){
        try  {
            Class.forName(className);
            return true;
        }  catch (ClassNotFoundException e) {
            return false;
        }
    }
}
