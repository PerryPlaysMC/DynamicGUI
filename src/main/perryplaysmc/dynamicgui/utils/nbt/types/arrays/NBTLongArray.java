package perryplaysmc.dynamicgui.utils.nbt.types.arrays;

import perryplaysmc.dynamicgui.utils.nbt.NBTList;
import perryplaysmc.dynamicgui.utils.nbt.types.NBTLong;
import perryplaysmc.dynamicgui.utils.nbt.types.NBTTypes;

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


    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("[L;");

        for(int i = 0; i < this.value.length; ++i) {
            if (i != 0) {
                stringbuilder.append(',');
            }

            stringbuilder.append(this.value[i]).append("L");
        }

        return stringbuilder.append(']').toString();
    }
}
