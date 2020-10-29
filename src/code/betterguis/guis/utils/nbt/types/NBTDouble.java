package betterguis.guis.utils.nbt.types;

import betterguis.guis.utils.nbt.NBTBase;


/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/
@SuppressWarnings("unused")
public class NBTDouble implements NBTBase {

    private final Double value;

    public NBTDouble(Double value) {
        this.value = value;
    }

    public Double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue().toString();
    }

    @Override
    public byte getTypeId() {
        return 2;
    }
}
