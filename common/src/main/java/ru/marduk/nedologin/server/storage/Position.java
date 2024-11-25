package ru.marduk.nedologin.server.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public record Position(double x, double y, double z) {

    @Override
    public boolean equals(Object o) {
        if (o instanceof Position cast)
            return x == cast.x() && y == cast.y() && z == cast.z();

        return false;
    }

    public Tag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("x", x);
        tag.putDouble("y", y);
        tag.putDouble("z", z);
        return tag;
    }

    public static Position fromNBT(CompoundTag nbt) {
        return new Position(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
    }
}
