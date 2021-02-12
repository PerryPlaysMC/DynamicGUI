package perryplaysmc.dynamicgui.utils.nbt.types;


import perryplaysmc.dynamicgui.utils.nbt.NBTBase;
import perryplaysmc.dynamicgui.utils.nbt.NBTList;

import java.util.List;

/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/
@SuppressWarnings("unused")
public class NBTDefaultList extends NBTList<NBTBase> {


    @Override
    public List<NBTBase> getValue() {
        return getList();
    }

    public String toString() {
        StringBuilder var0 = new StringBuilder("[");

        for(int var1 = 0; var1 < getList().size(); ++var1) {
            if (var1 != 0) {
                var0.append(',');
            }

            var0.append(getList().get(var1));
        }

        return var0.append(']').toString();
    }

    @Override
    public byte getTypeId() {
        return -1;
    }
}
