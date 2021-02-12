package perryplaysmc.dynamicgui.utils.nbt.types.arrays;


import perryplaysmc.dynamicgui.utils.nbt.NBTList;
import perryplaysmc.dynamicgui.utils.nbt.types.NBTByte;
import perryplaysmc.dynamicgui.utils.nbt.types.NBTTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/
@SuppressWarnings("unused")
public class NBTByteArray extends NBTList<NBTByte> {

    byte[] value;

    public NBTByteArray() {
        this(new ArrayList<>());
    }

    public NBTByteArray(Byte[] value) {
        this(Arrays.asList(value));
    }

    public NBTByteArray(byte[] value) {
        this(new ArrayList<Byte>() {{
            for(byte b : value)
                add(b);
        }});
    }

    public NBTByteArray(List<Byte> value) {
        super(new ArrayList<NBTByte>() {{
            for(Byte x : value)
                add(new NBTByte(x));
        }}, NBTTypes.BYTE.getId());
        this.value = new byte[value.size()];
        for(int i = 0; i < value.size(); i++)
            this.value[i] = value.get(i);
    }

    public byte[] getValue() {
        return value;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("[B;");

        for(int i = 0; i < this.value.length; ++i) {
            if (i != 0) {
                stringbuilder.append(',');
            }

            stringbuilder.append(this.value[i]).append('B');
        }

        return stringbuilder.append(']').toString();
    }

}
