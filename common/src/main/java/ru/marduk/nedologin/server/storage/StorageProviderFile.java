package ru.marduk.nedologin.server.storage;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import net.minecraft.world.level.GameType;
import ru.marduk.nedologin.NLConfig;
import ru.marduk.nedologin.Nedologin;
import ru.marduk.nedologin.utils.Hasher;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
            return Hasher.verifyHash(entries.get(username).password, password);
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
            entries.put(username, newEntry(username, Hasher.generateHash(password)));
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
            entries.get(username).password = Hasher.generateHash(newPassword);
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
        entry.gameType = NLConfig.INSTANCE.defaultGameType;
        return entry;
    }

    private static class POJOUserEntry {
        public String password, username;
        public int gameType;
    }
}
