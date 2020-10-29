package betterguis.guis.utils.nbt.types;

import betterguis.guis.utils.nbt.NBTBase;


/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/
@SuppressWarnings("unused")
public class NBTLong implements NBTBase {

    private final Long value;

    public NBTLong(Long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue().toString();
    }

    @Override
    public byte getTypeId() {
        return 5;
    }
}
