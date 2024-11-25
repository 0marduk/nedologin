package ru.marduk.nedologin.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;
import ru.marduk.nedologin.NLConstants;

public class Hasher {
    public static String generateHash(String password) {
        return BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(NLConstants.BCRYPT_COST, password.toCharArray());
    }

    public static boolean verifyHash(String hash, String password) {
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified;
    }
}