package betterguis.guis.utils.nbt.types.arrays;

import betterguis.guis.utils.nbt.NBTList;
import betterguis.guis.utils.nbt.types.NBTInteger;
import betterguis.guis.utils.nbt.types.NBTTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/
@SuppressWarnings("unused")
public class NBTIntegerArray extends NBTList<NBTInteger> {

    int[] value;


    public NBTIntegerArray() {
        this(new ArrayList<>());
    }

    public NBTIntegerArray(Integer[] value) {
        this(Arrays.asList(value));
    }

    public NBTIntegerArray(int[] value) {
        this(new ArrayList<Integer>() {{
            for(int b : value)
                add(b);
        }});
    }

    public NBTIntegerArray(List<Integer> value) {
        super(new ArrayList<NBTInteger>() {{
            for(Integer x : value)
                add(new NBTInteger(x));
        }}, NBTTypes.INTEGER.getId());
        this.value = new int[value.size()];
        for(int i = 0; i < value.size(); i++)
            this.value[i] = value.get(i);
    }

    public int[] getValue() {
        return value;
    }

    @Override
    public String toString() {

        String toString = "Integer[";
        for(Object b : getList()) {
            toString+=b+", ";
        }
        return toString.substring(0, toString.length()-(toString.contains(", ") ? ", ".length() : 0)) + "]";
    }
}
