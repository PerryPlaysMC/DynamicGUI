package dev.perryplaysmc.betterguis.guis.utils.nbt;


/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/
public interface NBTBase {

    Object getValue();

    String toString();

    byte getTypeId();

    default String asString() {
        return this.toString();
    }

}
