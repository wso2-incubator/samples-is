package com.wso2.custom.password.generator;

import org.wso2.carbon.user.mgt.common.RandomPasswordGenerator;

import java.security.SecureRandom;
import java.util.Random;

/**
 * This class is used to generate a random password of 10 characters length
 */
public class CustomPasswordGenerator implements RandomPasswordGenerator {

    private static final int PASSWORD_LENGTH = 10;
    private static final Random RANDOM = new SecureRandom();

    public char[] generatePassword() {

        // Pick from some letters that won't be easily mistaken for each other.
        // So, for example, omit o O and 0, 1 l and L.
        // This will generate a random password which satisfy the following regex.
        // ^((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%&*])).{0,100}$}
        String characters = "23456789abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ!@#$%&*";
        String digits = "23456789";
        String lowercaseLetters = "abcdefghjkmnpqrstuvwxyz";
        String uppercaseLetters = "ABCDEFGHJKMNPQRSTUVWXYZ";
        String specialCharacters = "!@#$%&*";
        int mandatoryCharactersCount = 4;

        StringBuilder pw = new StringBuilder();
        int index;
        for (int i = 0; i < PASSWORD_LENGTH - mandatoryCharactersCount; i++) {
            index = RANDOM.nextInt(characters.length());
            pw.append(characters.charAt(index));
        }

        index = RANDOM.nextInt(digits.length());
        pw.append(digits.charAt(index));

        index = RANDOM.nextInt(lowercaseLetters.length());
        pw.append(lowercaseLetters.charAt(index));

        index = RANDOM.nextInt(uppercaseLetters.length());
        pw.append(uppercaseLetters.charAt(index));

        index = RANDOM.nextInt(specialCharacters.length());
        pw.append(specialCharacters.charAt(index));

        char[] password = new char[pw.length()];
        pw.getChars(0, pw.length(), password, 0);

        return password;
    }
}
