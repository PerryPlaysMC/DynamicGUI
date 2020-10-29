package betterguis.guis.utils.nbt;

import java.util.ArrayList;
import java.util.List;

/**
 * Copy Right ©
 * This code is private
 * Owner: PerryPlaysMC
 * From: 10/2020-Now
 * <p>
 * Any attempts to use these program(s) may result in a penalty of up to $1,000 USD
 **/
@SuppressWarnings("all")
public abstract class NBTList<T extends NBTBase> implements NBTBase {

    private List<T> list;
    private byte id;

    public NBTList() {
        this(new ArrayList<>(), (byte)0);
    }

    public NBTList(List<T> list, byte id) {
        this.list = list;
        this.id = id;
    }

    public void add(T base) {
        if(canAdd(base)) list.add(base);
    }


    public void add(int index, T base) {
        if(canAdd(base))
            list.add(index, base);
    }

    public boolean canAdd(T base) {
        if(base.getTypeId()==0) {
            return false;
        }
        if(id == 0) {
            return true;
        }
        return id == base.getTypeId();
    }

    public List<T> getList() {
        return list;
    }

    @Override
    public String toString() {
        String toString = "List[";
        for(NBTBase base : list) {
            toString+=base.asString() + ", ";
        }
        return toString.substring(0, toString.contains(", ") ? toString.length()-", ".length() : toString.length()) + "]";
    }

    @Override
    public byte getTypeId() {
        return id;
    }
}
