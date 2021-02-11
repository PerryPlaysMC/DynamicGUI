package perryplaysmc.dynamicgui.utils.nbt.types;


import perryplaysmc.dynamicgui.utils.nbt.NBTBase;

/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/
@SuppressWarnings("unused")
public class NBTDouble implements NBTBase {

    private final Double value;

    public NBTDouble(Double value) {
        this.value = value;
    }

    public Double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue().toString() + "d";
    }

    @Override
    public byte getTypeId() {
        return 2;
    }
}
