package ru.marduk.nedologin;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class NLConfig {
    public static class Server {
        public final ModConfigSpec.IntValue secs;

        public final ModConfigSpec.ConfigValue<List<? extends String>> whitelistCommands;

        public final ModConfigSpec.ConfigValue<Boolean> autoRegister;

        public final ModConfigSpec.ConfigValue<Boolean> enableChangePassword;

        public final ModConfigSpec.ConfigValue<String> storageProvider;

        public final ModConfigSpec.IntValue defaultGameType;

        public final ModConfigSpec.ConfigValue<List<? extends String>> plugins;

        Server(ModConfigSpec.Builder builder) {
            builder.push("server");

            autoRegister = builder
                    .comment("Automatically register players (disable this if you choose to register players differently)")
                    .define("autoRegister", true);

            enableChangePassword = builder
                    .comment("Should the player be able to change the password?")
                    .define("enableChangePassword", true);

            secs = builder
                    .comment("Login Timeout(s)")
                    .defineInRange("secs", 600, 0, 1200);

            whitelistCommands = builder
                    .comment("Commands in whitelist can be executed before player login.")
                    .defineList("commandNames", Collections.emptyList(), o -> o instanceof String);

            storageProvider = builder
                    .comment("Which storage provider to use")
                    .comment("Nedologin provides to available providers by default:")
                    .comment("nedologin:file -> file based storage")
                    .comment("nedologin:sqlite -> sqlite based storage")
                    .comment("nedologin:mariadb -> mariadb based storage (requires additional configuration)")
                    .comment("Note that you need to add JDBC sqlite & mariadb yourself if you want to use database based storage")
                    .define("storageProvider", "nedologin:file");

            defaultGameType = builder
                    .comment("Default game type switched after player login")
                    .comment("0,1,2,3 represents survival,creative,adventure,spectator")
                    .defineInRange("defaultGameType", 0, 0, 3);

            plugins = builder
                    .comment("Player login handler plugins to load")
                    .comment("nedologin:protect_coord is disabled by default, add to here to enable coord protect feature")
                    .defineList("plugins",
                            Arrays.asList(
                                    "nedologin:auto_save",
                                    "nedologin:resend_request",
                                    "nedologin:restrict_game_type",
                                    "nedologin:restrict_movement",
                                    "nedologin:timeout"
                            ),
                            o -> o instanceof String);

            builder.pop();
        }
    }

    static final ModConfigSpec SERVER_SPEC;
    public static final Server SERVER;

    static {
        final Pair<Server, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Server::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }
}
