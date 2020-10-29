package betterguis.guis.utils.nbt.types.arrays;

import betterguis.guis.utils.nbt.NBTList;
import betterguis.guis.utils.nbt.types.NBTByte;
import betterguis.guis.utils.nbt.types.NBTTypes;

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

    @Override
    public String toString() {
        String toString = "Byte[";
        for(Object b : getList()) {
            toString+=b+", ";
        }
        return toString.substring(0, toString.length()-(toString.contains(", ") ? ", ".length() : 0)) + "]";
    }

}
