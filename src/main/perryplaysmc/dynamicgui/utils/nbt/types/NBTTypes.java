package perryplaysmc.dynamicgui.utils.nbt.types;


/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/
public enum NBTTypes {

    BYTE((byte)1),
    DOUBLE((byte)2),
    FLOAT((byte)3),
    INTEGER((byte)4),
    LONG((byte)5),
    SHORT((byte)6),
    STRING((byte)7);

    private byte id;

    NBTTypes(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }
}
