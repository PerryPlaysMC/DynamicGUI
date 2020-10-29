package betterguis.guis.utils.nbt.types;

import betterguis.guis.utils.nbt.NBTBase;

/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/
@SuppressWarnings("unused")
public class NBTInteger implements NBTBase {

    private final Integer value;

    public NBTInteger(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue().toString();
    }

    @Override
    public byte getTypeId() {
        return 4;
    }
}
