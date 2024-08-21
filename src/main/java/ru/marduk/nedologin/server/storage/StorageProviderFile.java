package ru.marduk.nedologin.server.storage;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import ru.marduk.nedologin.NLConfig;
import ru.marduk.nedologin.NLConstants;
import ru.marduk.nedologin.Nedologin;
import at.favre.lib.crypto.bcrypt.*;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ThreadSafe
@OnlyIn(Dist.DEDICATED_SERVER)
public class StorageProviderFile implements StorageProvider {
    private final Gson gson = new Gson();
    private final Path path;
    private final Map<String, POJOUserEntry> entries = new ConcurrentHashMap<>();
    private boolean dirty = false;

    public StorageProviderFile(Path path) throws IOException {
        this.path = path;

        if (Files.exists(path)) {
            POJOUserEntry[] buf = gson.fromJson(Files.newBufferedReader(path, StandardCharsets.UTF_8), POJOUserEntry[].class);
            if (buf != null) {
                Arrays.stream(buf).peek(e -> e.username = e.username.toLowerCase()).forEach(e -> entries.put(e.username, e));
            }
        } else {
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            Files.createFile(path);
        }
    }

    @Override
    public boolean checkPassword(String username, String password) {
        if (entries.containsKey(username)) {
            return BCrypt.verifyer().verify(password.toCharArray(), entries.get(username).password).verified;
        }
        return false;
    }

    @Override
    public void unregister(String username) {
        dirty = true;
        entries.remove(username);
    }

    @Override
    public boolean registered(String username) {
        return entries.containsKey(username);
    }

    @Override
    public void register(String username, String password) {
        if (!entries.containsKey(username)) {
            entries.put(username, newEntry(username, BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(NLConstants.BCRYPT_COST, password.toCharArray())));
            dirty = true;
        }
    }

    @Override
    public synchronized void save() throws IOException {
        try {
            Files.writeString(path, gson.toJson(entries.values().toArray()), StandardOpenOption.TRUNCATE_EXISTING);
            dirty = false;
        } catch (IOException ex) {
            Nedologin.logger.error("Unable to save entries", ex);
            throw ex;
        }
    }

    @Override
    public GameType gameType(String username) {
        return GameType.byId(entries.get(username).gameType);
    }

    @Override
    public void setGameType(String username, GameType gameType) {
        if (entries.containsKey(username)) {
            dirty = true;
            entries.get(username).gameType = gameType.getId();
        }
    }

    @Override
    public void changePassword(String username, String newPassword) {
        if (entries.containsKey(username)) {
            dirty = true;
            entries.get(username).password = BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(NLConstants.BCRYPT_COST, newPassword.toCharArray());
        }
    }

    @Override
    public boolean dirty() {
        return dirty;
    }

    @Override
    public Collection<String> getAllRegisteredUsername() {
        return new ImmutableList.Builder<String>().addAll(entries.keySet()).build();
    }

    private static POJOUserEntry newEntry(String username, String password) {
        POJOUserEntry entry = new POJOUserEntry();
        entry.username = username;
        entry.password = password;
        entry.gameType = NLConfig.SERVER.defaultGameType.get();
        return entry;
    }

    private static class POJOUserEntry {
        public String password, username;
        public int gameType;
    }
}
