package ru.marduk.nedologin;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class NLConfig {
    public static class Server {
        public final ForgeConfigSpec.IntValue secs;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> whitelistCommands;

        public final ForgeConfigSpec.ConfigValue<Boolean> autoRegister;

        public final ForgeConfigSpec.ConfigValue<String> storageProvider;

        public final ForgeConfigSpec.IntValue defaultGameType;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> plugins;

        Server(ForgeConfigSpec.Builder builder) {
            builder.push("server");

            autoRegister = builder
                    .comment("Automatically register players (disable this if you choose to register players differently)")
                    .define("autoRegister", true);

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

    static final ForgeConfigSpec SERVER_SPEC;
    public static final Server SERVER;

    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }
}
