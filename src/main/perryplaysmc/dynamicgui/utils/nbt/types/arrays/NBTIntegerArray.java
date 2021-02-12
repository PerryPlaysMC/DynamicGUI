package perryplaysmc.dynamicgui.utils.nbt.types.arrays;

import perryplaysmc.dynamicgui.utils.nbt.NBTList;
import perryplaysmc.dynamicgui.utils.nbt.types.NBTInteger;
import perryplaysmc.dynamicgui.utils.nbt.types.NBTTypes;

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

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("[I;");

        for(int i = 0; i < this.value.length; ++i) {
            if (i != 0) {
                stringbuilder.append(',');
            }

            stringbuilder.append(this.value[i]);
        }

        return stringbuilder.append(']').toString();
    }
}
