package ru.marduk.nedologin.server;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.saveddata.SavedData;
import ru.marduk.nedologin.NLConstants;
import ru.marduk.nedologin.server.storage.Position;

import java.util.HashMap;

public class LastPosData extends SavedData {
    public static final Position defaultPosition = new Position(0, 255, 0);
    public HashMap<String, Position> players = new HashMap<>();

    @SuppressWarnings("NullableProblems")
    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider pRegistries) {
        CompoundTag playersNbt = new CompoundTag();
        players.forEach((username, lastPos) -> playersNbt.put(username, lastPos.toNBT()));

        compoundTag.put("lastPositions", playersNbt);

        return compoundTag;
    }

    public static LastPosData loadFromNbt(CompoundTag tag, HolderLookup.Provider pRegistries) {
        LastPosData state = new LastPosData();

        CompoundTag playersNbt = tag.getCompound("lastPositions");
        playersNbt.getAllKeys().forEach(key -> {
            Position lastPos = Position.fromNBT(playersNbt.getCompound(key));

            state.players.put(key, lastPos);
        });

        return state;
    }

    public static LastPosData create() {
        return new LastPosData();
    }

    public static LastPosData getData(MinecraftServer server) {
        LastPosData data = server.overworld().getDataStorage().computeIfAbsent(new Factory<LastPosData>(LastPosData::create, LastPosData::loadFromNbt), NLConstants.MODID + "_positions");

        data.setDirty();

        return data;
    }

    @SuppressWarnings("DataFlowIssue")
    public static Position getLastPos(LivingEntity player) {
        LastPosData state = getData(player.level().getServer());

        return state.players.computeIfAbsent(player.getName().getString(), name -> defaultPosition);
    }

    @SuppressWarnings("DataFlowIssue")
    public static void setLastPos(LivingEntity player, Position pos) {
        LastPosData state = getData(player.getServer());

        state.players.put(player.getName().getString(), pos);
    }
}