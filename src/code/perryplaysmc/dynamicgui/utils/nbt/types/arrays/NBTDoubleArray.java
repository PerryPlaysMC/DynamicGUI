package perryplaysmc.dynamicgui.utils.nbt.types.arrays;


import perryplaysmc.dynamicgui.utils.nbt.NBTList;
import perryplaysmc.dynamicgui.utils.nbt.types.NBTDouble;
import perryplaysmc.dynamicgui.utils.nbt.types.NBTTypes;

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


    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("[D;");

        for(int i = 0; i < this.value.length; ++i) {
            if (i != 0) {
                stringbuilder.append(',');
            }

            stringbuilder.append(this.value[i]).append("d");
        }

        return stringbuilder.append(']').toString();
    }


}
