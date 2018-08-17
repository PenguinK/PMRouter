package com.penguinmore.pm_router.utils;

public class StringUtils {

    /**
     * Captialize first letter in Word.
     *
     * @param self
     * @return
     */
    public static String capitalize(CharSequence self) {
        return self.length() == 0 ? "" :
                "" + Character.toUpperCase(self.charAt(0)) + self.subSequence(1, self.length());
    }
}
