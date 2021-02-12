package perryplaysmc.dynamicgui.utils.nbt.types;

import perryplaysmc.dynamicgui.utils.nbt.NBTBase;
import perryplaysmc.dynamicgui.utils.nbt.NBTList;

import java.util.ArrayList;
import java.util.List;

/**
 * Copy Right ©
 * This code is private
 * Owner: PerryPlaysMC
 * From: 10/2020-Now
 **/
@SuppressWarnings("all")
public class NBTEmptyList extends NBTList<NBTBase> {


    public List<NBTBase> getValue() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "[]";
    }

    @Override
    public byte getTypeId() {
        return 2;
    }
}
