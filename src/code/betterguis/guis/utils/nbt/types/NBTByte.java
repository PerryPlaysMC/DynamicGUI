package betterguis.guis.utils.nbt.types;

import betterguis.guis.utils.nbt.NBTBase;


/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/
@SuppressWarnings("unused")
public class NBTByte implements NBTBase {

    private final Byte value;

    public NBTByte(Byte value) {
        this.value = value;
    }

    public Byte getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue().toString() + "b";
    }

    @Override
    public byte getTypeId() {
        return 1;
    }
}
