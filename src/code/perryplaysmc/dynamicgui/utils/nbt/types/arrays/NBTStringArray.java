package perryplaysmc.dynamicgui.utils.nbt.types.arrays;

import perryplaysmc.dynamicgui.utils.nbt.NBTList;
import perryplaysmc.dynamicgui.utils.nbt.types.NBTString;
import perryplaysmc.dynamicgui.utils.nbt.types.NBTTypes;

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

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("[");

        for(int i = 0; i < this.value.length; ++i) {
            if (i != 0) {
                stringbuilder.append(',');
            }

            stringbuilder.append(this.value[i]);
        }

        return stringbuilder.append(']').toString();
    }
}
