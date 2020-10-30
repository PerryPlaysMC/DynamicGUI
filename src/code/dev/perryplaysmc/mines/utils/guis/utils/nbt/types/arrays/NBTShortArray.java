package dev.perryplaysmc.mines.utils.guis.utils.nbt.types.arrays;

import dev.perryplaysmc.mines.utils.guis.utils.nbt.NBTList;
import dev.perryplaysmc.mines.utils.guis.utils.nbt.types.NBTShort;
import dev.perryplaysmc.mines.utils.guis.utils.nbt.types.NBTTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/
@SuppressWarnings("unused")
public class NBTShortArray extends NBTList<NBTShort> {

    short[] value;

    public NBTShortArray() {
        this(new ArrayList<>());
    }

    public NBTShortArray(Short[] value) {
        this(Arrays.asList(value));
    }

    public NBTShortArray(short[] value) {
        this(new ArrayList<Short>() {{
            for(short b : value)
                add(b);
        }});
    }

    public NBTShortArray(List<Short> value) {
        super(new ArrayList<NBTShort>() {{
            for(short x : value)
                add(new NBTShort(x));
        }}, NBTTypes.SHORT.getId());
        this.value = new short[value.size()];
        for(int i = 0; i < value.size(); i++)
            this.value[i] = value.get(i);
    }

    public short[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        String toString = "Short[";
        for(Object b : getList()) {
            toString+=b+", ";
        }
        return toString.substring(0, toString.length()-(toString.contains(", ") ? ", ".length() : 0)) + "]";
    }
}
