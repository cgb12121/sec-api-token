package com.backend.vertwo.utils.email;

public class EmailUtils {

    public static String getEmailMessage(String name, String host, String key) {
        return "Dear " + name + "," +
                "\n\nPlease click the following link to verify your account:\n" + getVerificationUrl(host, key) +
                "\n\nThank you!";
    }

    public static String getVerificationUrl(String host, String key) {
        return "http://" + host + "/verify/account?key=" + key;
    }

    public static String getResetPasswordMessage(String name, String host, String key) {
        return "Dear " + name + "," +
                "\n\nYou have sent a request to reset your password." +
                "If it was you, please click on this link below:\n" +
                getResetPasswordUrl(name, host, key) +
                "\n\nThe link will expire in 5 minutes." +
                "Thank you!";
    }

    public static String getResetPasswordUrl(String name, String host, String key) {
        return "http://" + host + "/reset/password?key=" + key;
    }

}
