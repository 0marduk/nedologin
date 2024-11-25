package ru.marduk.nedologin.server.storage;

import ru.marduk.nedologin.NLConstants;

import java.sql.DriverManager;
import java.sql.SQLException;

public final class StorageProviderSQLite extends StorageProviderSQL {
    public StorageProviderSQLite() throws SQLException {
        // Default path at $WORLD_DIR/sl_entries.dat
        super(DriverManager.getConnection("jdbc:sqlite:" + NLConstants.NL_ENTRY));
    }
}