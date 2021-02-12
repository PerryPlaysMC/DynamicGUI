package perryplaysmc.dynamicgui.utils.nbt.types;


import perryplaysmc.dynamicgui.utils.nbt.NBTBase;

/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/
@SuppressWarnings("unused")
public class NBTFloat implements NBTBase {

    private final Float value;

    public NBTFloat(Float value) {
        this.value = value;
    }

    public Float getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue().toString() + "f";
    }

    @Override
    public byte getTypeId() {
        return 3;
    }
}
