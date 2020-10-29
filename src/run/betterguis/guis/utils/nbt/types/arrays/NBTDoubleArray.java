package betterguis.guis.utils.nbt.types.arrays;

import betterguis.guis.utils.nbt.NBTList;
import betterguis.guis.utils.nbt.types.NBTDouble;
import betterguis.guis.utils.nbt.types.NBTTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/
@SuppressWarnings("unused")
public class NBTDoubleArray extends NBTList<NBTDouble> {

    double[] value;


    public NBTDoubleArray() {
        this(new ArrayList<>());
    }

    public NBTDoubleArray(Double[] value) {
        this(Arrays.asList(value));
    }

    public NBTDoubleArray(double[] value) {
        this(new ArrayList<Double>() {{
            for(double b : value)
                add(b);
        }});
    }

    public NBTDoubleArray(List<Double> value) {
        super(new ArrayList<NBTDouble>() {{
            for(double x : value)
                add(new NBTDouble(x));
        }}, NBTTypes.DOUBLE.getId());
        this.value = new double[value.size()];
        for(int i = 0; i < value.size(); i++)
            this.value[i] = value.get(i);
    }

    public double[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        String toString = "Double[";
        for(Object b : getList()) {
            toString+=b+", ";
        }
        return toString.substring(0, toString.length()-(toString.contains(", ") ? ", ".length() : 0)) + "]";
    }

}
