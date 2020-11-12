package bettergui.utils.nbt.types.arrays;

import bettergui.utils.nbt.NBTList;
import bettergui.utils.nbt.types.NBTString;
import bettergui.utils.nbt.types.NBTTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/
@SuppressWarnings("unused")
public class NBTStringArray extends NBTList<NBTString> {

    String[] value;


    public NBTStringArray() {
        this(new ArrayList<>());
    }
    public NBTStringArray(String[] value) {
        this(Arrays.asList(value));
    }
    public NBTStringArray(List<String> value) {
        super(new ArrayList<NBTString>() {{
            for(String s : value)
                add(new NBTString(s));
        }}, NBTTypes.STRING.getId());
        this.value = new String[value.size()];
        for(int i = 0; i < value.size(); i++)
            this.value[i] = value.get(i);
    }

    public String[] getValue() {
        return value;
    }

    @Override
    public String toString() {

        String toString = "String[";
        for(Object b : getList()) {
            toString+=b+", ";
        }
        return toString.substring(0, toString.length()-(toString.contains(", ") ? ", ".length() : 0)) + "]";
    }
}
