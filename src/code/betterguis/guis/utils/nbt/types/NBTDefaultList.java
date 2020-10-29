package betterguis.guis.utils.nbt.types;

import betterguis.guis.utils.nbt.NBTBase;
import betterguis.guis.utils.nbt.NBTList;

import java.util.List;


/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/
@SuppressWarnings("unused")
public class NBTDefaultList extends NBTList<NBTBase> {


    @Override
    public List<NBTBase> getValue() {
        return getList();
    }

    @Override
    public String toString() {
        String toString = "[";
        for(NBTBase base : getList()) {
            toString+=base.asString() + ", ";
        }
        return toString.substring(0, toString.contains(", ") ? toString.length()-", ".length() : toString.length()) + "]";
    }

    @Override
    public byte getTypeId() {
        return -1;
    }
}
