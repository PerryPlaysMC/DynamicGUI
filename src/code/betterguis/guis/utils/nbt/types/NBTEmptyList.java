package betterguis.guis.utils.nbt.types;

import betterguis.guis.utils.nbt.NBTBase;
import betterguis.guis.utils.nbt.NBTList;

import java.util.ArrayList;
import java.util.List;

/**
 * Copy Right ©
 * This code is private
 * Owner: PerryPlaysMC
 * From: 10/2020-Now
 * <p>
 * Any attempts to use these program(s) may result in a penalty of up to $1,000 USD
 **/
@SuppressWarnings("all")
public class NBTEmptyList extends NBTList<NBTBase> {


    public List<NBTBase> getValue() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "EmptyList";
    }

    @Override
    public byte getTypeId() {
        return 2;
    }
}
