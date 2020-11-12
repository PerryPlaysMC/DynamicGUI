package bettergui.utils.nbt.types.arrays;

import bettergui.utils.nbt.NBTList;
import bettergui.utils.nbt.types.NBTFloat;
import bettergui.utils.nbt.types.NBTTypes;

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

    @Override
    public String toString() {
        String toString = "Float[";
        for(Object b : getList()) {
            toString+=b+", ";
        }
        return toString.substring(0, toString.length()-(toString.contains(", ") ? ", ".length() : 0)) + "]";
    }

}
