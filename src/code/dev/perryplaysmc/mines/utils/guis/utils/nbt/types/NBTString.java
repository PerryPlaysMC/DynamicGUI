package dev.perryplaysmc.mines.utils.guis.utils.nbt.types;

import dev.perryplaysmc.mines.utils.guis.utils.nbt.NBTBase;


/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/
@SuppressWarnings("unused")
public class NBTString implements NBTBase {

    private final String value;

    public NBTString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public byte getTypeId() {
        return 7;
    }
}
