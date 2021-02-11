package perryplaysmc.dynamicgui.utils.nbt.types.arrays;

import perryplaysmc.dynamicgui.utils.nbt.NBTList;
import perryplaysmc.dynamicgui.utils.nbt.types.NBTFloat;
import perryplaysmc.dynamicgui.utils.nbt.types.NBTTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/
@SuppressWarnings("unused")
public class NBTFloatArray extends NBTList<NBTFloat> {

    float[] value;


    public NBTFloatArray() {
        this(new ArrayList<>());
    }

    public NBTFloatArray(Float[] value) {
        this(Arrays.asList(value));
    }

    public NBTFloatArray(float[] value) {
        this(new ArrayList<Float>() {{
            for(float b : value)
                add(b);
        }});
    }

    public NBTFloatArray(List<Float> value) {
        super(new ArrayList<NBTFloat>() {{
            for(float x : value)
                add(new NBTFloat(x));
        }}, NBTTypes.FLOAT.getId());
        this.value = new float[value.size()];
        for(int i = 0; i < value.size(); i++)
            this.value[i] = value.get(i);
    }

    public float[] getValue() {
        return value;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("[F;");

        for(int i = 0; i < this.value.length; ++i) {
            if (i != 0) {
                stringbuilder.append(',');
            }

            stringbuilder.append(this.value[i]).append("f");
        }

        return stringbuilder.append(']').toString();
    }

}
