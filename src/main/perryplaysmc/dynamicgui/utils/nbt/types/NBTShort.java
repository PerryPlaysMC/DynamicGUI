package perryplaysmc.dynamicgui.utils.nbt.types;

import perryplaysmc.dynamicgui.utils.nbt.NBTBase;

/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/
@SuppressWarnings("unused")
public class NBTShort implements NBTBase {

    private final Short value;

    public NBTShort(Short value) {
        this.value = value;
    }

    public Short getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue().toString() + "s";
    }

    @Override
    public byte getTypeId() {
        return 6;
    }
}
