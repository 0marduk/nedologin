package ru.marduk.nedologin.platform;

public class Service {
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final INetworkHelper NETWORK = load(INetworkHelper.class);
    public static final IClientNetworkHelper CLIENT_NETWORK = load(IClientNetworkHelper.class);

    public static <T> T load(Class<T> clazz) {
        return java.util.ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}
