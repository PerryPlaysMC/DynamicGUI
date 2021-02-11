package perryplaysmc.dynamicgui.utils.nbt.types.arrays;

import perryplaysmc.dynamicgui.utils.nbt.NBTList;
import perryplaysmc.dynamicgui.utils.nbt.types.NBTShort;
import perryplaysmc.dynamicgui.utils.nbt.types.NBTTypes;

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

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("[S;");

        for(int i = 0; i < this.value.length; ++i) {
            if (i != 0) {
                stringbuilder.append(',');
            }

            stringbuilder.append(this.value[i]).append("s");
        }

        return stringbuilder.append(']').toString();
    }
}
