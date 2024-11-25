package ru.marduk.nedologin.fabric.network;

import net.minecraft.resources.ResourceLocation;
import ru.marduk.nedologin.NLConstants;

public class NetworkConsts {
    public static final ResourceLocation MessageRequestLogin = new ResourceLocation(NLConstants.MODID, "message_request_login");
    public static final ResourceLocation MessageChangePasswordResponse = new ResourceLocation(NLConstants.MODID, "message_change_password_response");
    public static final ResourceLocation MessageLogin = new ResourceLocation(NLConstants.MODID, "message_login");
    public static final ResourceLocation MessageChangePassword = new ResourceLocation(NLConstants.MODID, "message_change_password");
}
