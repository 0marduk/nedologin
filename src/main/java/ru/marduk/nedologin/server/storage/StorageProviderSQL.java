package ru.marduk.nedologin.server.storage;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.GameType;
import at.favre.lib.crypto.bcrypt.*;
import ru.marduk.nedologin.NLConfig;
import ru.marduk.nedologin.NLConstants;
import ru.marduk.nedologin.Nedologin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class StorageProviderSQL implements StorageProvider {
    private final Connection conn;

    public StorageProviderSQL(Connection conn) {
        this.conn = conn;
        try {
            conn.createStatement()
                    .execute("""
                            CREATE TABLE IF NOT EXISTS nl_entries
                            (
                                username        varchar(32),
                                defaultGameType tinyint,
                                password        varchar(255),
                                PRIMARY KEY (username)
                            )""");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    @Override
    public boolean checkPassword(String username, String password) {
        try {
            PreparedStatement st = conn.prepareStatement("""
                    SELECT password
                    FROM nl_entries
                    WHERE username = ?""");
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if (!rs.next()) return false;

            return BCrypt.verifyer().verify(password.toCharArray(), rs.getString("password")).verified;
        } catch (SQLException ex) {
            Nedologin.logger.error("Error looking up password", ex);
            return false;
        }
    }

    @Override
    public void unregister(String username) {
        try {
            PreparedStatement st = conn.prepareStatement("""
                    DELETE
                    FROM nl_entries
                    WHERE username = ?""");
            st.setString(1, username);
            st.execute();
        } catch (SQLException ex) {
            Nedologin.logger.error("Error deleting entry", ex);
        }
    }

    @Override
    public boolean registered(String username) {
        try {
            PreparedStatement st = conn.prepareStatement("SELECT EXISTS(SELECT * from nl_entries WHERE username = ?)");
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            return rs.next() && rs.getBoolean(1);
        } catch (SQLException ex) {
            Nedologin.logger.error("Error looking up entry", ex);
            return false;
        }
    }

    @Override
    public void register(String username, String password) {
        if (registered(username)) return;
        try {
            PreparedStatement st = conn.prepareStatement("INSERT INTO nl_entries (username, password, defaultGameType)\n" +
                    "VALUES (?, ?, ?)");
            st.setString(1, username);
            st.setString(2, BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(NLConstants.BCRYPT_COST, password.toCharArray()));
            st.setInt(3, NLConfig.SERVER.defaultGameType.get());
            st.execute();
        } catch (SQLException ex) {
            Nedologin.logger.error("Error registering entry", ex);
        }
    }

    @Override
    public void save() {
        // NO-OP
    }

    @Override
    public GameType gameType(String username) {
        try {
            PreparedStatement st = conn.prepareStatement("""
                    SELECT defaultGameType
                    FROM nl_entries
                    where username = ?""");
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if (!rs.next()) return null;
            return GameType.byId(rs.getInt("defaultGameType"));
        } catch (SQLException ex) {
            Nedologin.logger.error("Error looking up entry", ex);
            return GameType.byId(NLConfig.SERVER.defaultGameType.get());
        }
    }

    @Override
    public void setGameType(String username, GameType gameType) {
        try {
            PreparedStatement st = conn.prepareStatement("""
                    UPDATE nl_entries
                    SET defaultGameType=?
                    WHERE username = ?""");
            st.setInt(1, gameType.getId());
            st.setString(2, username);
            st.execute();
        } catch (SQLException ex) {
            Nedologin.logger.error("Error updating entry", ex);
        }
    }

    @Override
    public void changePassword(String username, String newPassword) {
        try {
            PreparedStatement st = conn.prepareStatement("""
                    UPDATE nl_entries
                    SET password=?
                    WHERE username = ?""");
            st.setString(1, BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(NLConstants.BCRYPT_COST, newPassword.toCharArray()));
            st.setString(2, username);
            st.execute();
        } catch (SQLException ex) {
            Nedologin.logger.error("Error updating entry", ex);
        }
    }

    @Override
    public boolean dirty() {
        // We don't need to save
        return false;
    }

    @Override
    public Collection<String> getAllRegisteredUsername() {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT username\n" +
                    "FROM nl_entries");
            while (rs.next()) {
                builder.add(rs.getString("username"));
            }
        } catch (SQLException ex) {
            Nedologin.logger.error("Error looking up entry", ex);
        }
        return builder.build();
    }
}
