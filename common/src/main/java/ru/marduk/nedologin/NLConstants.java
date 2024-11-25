package ru.marduk.nedologin;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class NLConstants {
    public static final String MODID = "nedologin";
    public static final Path NL_ENTRY = Paths.get(".","nl_entries.dat");
    public static final int MAX_PASSWORD_LENGTH = 100;
    public static final int BCRYPT_COST = 10;
    public static final int MARIADB_TIMEOUT = 10;
}
