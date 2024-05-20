package ru.marduk.nedologin.server.capability;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ru.marduk.nedologin.server.storage.Position;

@OnlyIn(Dist.DEDICATED_SERVER)
public interface ILastPos {
    Position getLastPos();

    void setLastPos(Position pos);
}
