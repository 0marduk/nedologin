package ru.marduk.nedologin.server.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.marduk.nedologin.NLConstants;
import ru.marduk.nedologin.Nedologin;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class StorageProviderMariaDB extends StorageProviderSQL {

    public static class StorageProviderMariaDBSettings {
        public String sqlHost;
        public int sqlPort;
        public String sqlUser;
        public String sqlPassword;
        public String sqlDatabase;

        public StorageProviderMariaDBSettings defaultOptions() {
            sqlHost = "localhost";
            sqlDatabase = "minecraft";
            sqlPort = 3306;
            sqlUser = "root";
            sqlPassword = "";

            return this;
        }

        public static StorageProviderMariaDBSettings fetchConfig() throws IOException {
            Path path = Paths.get(".","nl_db_settings.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            if (!Files.exists(path)) {
                try (Writer w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                    gson.toJson(new StorageProviderMariaDBSettings().defaultOptions(), w);
                } catch (Throwable t) {
                    Nedologin.logger.error("Failed to write default mariadb config file.", t);
                }
            }

            config = gson.fromJson(Files.lines(path, StandardCharsets.UTF_8).collect(Collectors.joining(System.lineSeparator())), StorageProviderMariaDBSettings.class);

            return config;
        }
    }

    public static StorageProviderMariaDBSettings config;

    static {
        try {
            config = StorageProviderMariaDBSettings.fetchConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public StorageProviderMariaDB() throws SQLException {
        super(DriverManager.getConnection("jdbc:mariadb://" + config.sqlHost + ":" + config.sqlPort + "/" + config.sqlDatabase, config.sqlUser, config.sqlPassword));
    }

    @Override
    protected void checkValidity() throws SQLException {
        if (!super.conn.isValid(NLConstants.MARIADB_TIMEOUT)) {
            // Close the connection and make a new one with the same parameters
            super.conn.close();
            super.conn = DriverManager.getConnection("jdbc:mariadb://" + config.sqlHost + ":" + config.sqlPort + "/" + config.sqlDatabase, config.sqlUser, config.sqlPassword);
        }
    }
}
