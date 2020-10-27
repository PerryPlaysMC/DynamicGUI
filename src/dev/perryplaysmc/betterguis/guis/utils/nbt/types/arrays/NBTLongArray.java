package dev.perryplaysmc.betterguis.guis.utils.nbt.types.arrays;

import dev.perryplaysmc.betterguis.guis.utils.nbt.NBTBase;
import dev.perryplaysmc.betterguis.guis.utils.nbt.NBTList;
import dev.perryplaysmc.betterguis.guis.utils.nbt.types.NBTLong;
import dev.perryplaysmc.betterguis.guis.utils.nbt.types.NBTTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/
@SuppressWarnings("unused")
public class NBTLongArray extends NBTList<NBTLong> {

    long[] value;

    public NBTLongArray() {
        this(new ArrayList<>());
    }

    public NBTLongArray(Long[] value) {
        this(Arrays.asList(value));
    }

    public NBTLongArray(long[] value) {
        this(new ArrayList<Long>() {{
            for(long b : value)
                add(b);
        }});
    }

    public NBTLongArray(List<Long> value) {
        super(new ArrayList<NBTLong>() {{
            for(long x : value)
                add(new NBTLong(x));
        }}, NBTTypes.LONG.getId());
        this.value = new long[value.size()];
        for(int i = 0; i < value.size(); i++)
            this.value[i] = value.get(i);
    }

    public long[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        String toString = "Long[";
        for(Object b : getList()) {
            toString+=b+", ";
        }
        return toString.substring(0, toString.length()-(toString.contains(", ") ? ", ".length() : 0)) + "]";
    }
}
