package ru.marduk.nedologin;

import net.minecraft.world.level.storage.LevelResource;

public final class NLConstants {
    public static final String MODID = "nedologin";
    public static final LevelResource NL_ENTRY = new LevelResource("nl_entries.dat");
    public static final int MAX_PASSWORD_LENGTH = 100;
    public static final int BCRYPT_COST = 10;
    public static final int MARIADB_TIMEOUT = 10;
}
