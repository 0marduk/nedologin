package ru.marduk.nedologin.platform;

public interface IClientNetworkHelper {
    void SendMessageLogin(String pwd);
    void SendMessageChangePassword(String original, String to);
}
