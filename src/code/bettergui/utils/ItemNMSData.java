package bettergui.utils;

import bettergui.utils.nbt.NBTBase;
import bettergui.utils.nbt.NBTCompound;
import bettergui.utils.nbt.NBTList;
import bettergui.utils.nbt.types.*;
import bettergui.utils.nbt.types.arrays.*;
import dev.perryplaysmc.mines.utils.guis.utils.nbt.types.*;
import dev.perryplaysmc.mines.utils.guis.utils.nbt.types.arrays.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;


/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/
@SuppressWarnings("unused")
public class ItemNMSData {

    private final Class<?> comp = Class.forName(Version.getNMSPackage() + ".NBTTagCompound"),
            nbtBase = Class.forName(Version.getNMSPackage()+".NBTBase"),
            nbtNum = Class.forName(Version.getNMSPackage()+".NBTNumber"),
            nbtByte = Class.forName(Version.getNMSPackage()+".NBTTagByte"),
            nbtString = Class.forName(Version.getNMSPackage()+".NBTTagString"),
            nbtByteArray = Class.forName(Version.getNMSPackage()+".NBTTagByteArray"),
            nbtShort = Class.forName(Version.getNMSPackage()+".NBTTagShort"),
            nbtDouble = Class.forName(Version.getNMSPackage()+".NBTTagDouble"),
            nbtIntArray = Class.forName(Version.getNMSPackage()+".NBTTagIntArray"),
            nbtInt = Class.forName(Version.getNMSPackage()+".NBTTagInt"),
            nbtLong = Class.forName(Version.getNMSPackage()+".NBTTagLong"),
            nbtLongArray = Class.forName(Version.getNMSPackage()+".NBTTagLongArray"),
            nbtFloat = Class.forName(Version.getNMSPackage()+".NBTTagFloat"),
            nbtList = Class.forName(Version.getNMSPackage()+".NBTTagList"),
            craftPlayer = Class.forName(Version.getCBPackage() + ".entity.CraftPlayer"),
            entityPlayer = Class.forName(Version.getNMSPackage() + ".EntityPlayer"),
            cis = Class.forName(Version.getCBPackage() + ".inventory.CraftItemStack");

    private final Method gByte = comp.getMethod("getByte", String.class),
            gString = comp.getMethod("getString", String.class),
            gByteArr = comp.getMethod("getByte", String.class),
            gShort = comp.getMethod("getShort", String.class),
            gDouble = comp.getMethod("getDouble", String.class),
            gIntArr = comp.getMethod("getIntArray", String.class),
            gInt = comp.getMethod("getInt", String.class),
            gLong = comp.getMethod("getLong", String.class),
            gFloat = comp.getMethod("getFloat", String.class),
            gKeys = comp.getMethod("getKeys"),
            asString = nbtBase.getMethod("asString"),
            add = nbtList.getMethod("add", int.class, nbtBase),
            asInt = nbtNum.getMethod("asInt"),
            asLong = nbtNum.getMethod("asLong"),
            asShort = nbtNum.getMethod("asShort"),
            asByte = nbtNum.getMethod("asByte"),
            asDouble = nbtNum.getMethod("asDouble"),
            asNMSCopy = cis.getMethod("asNMSCopy", ItemStack.class),
            asFloat = nbtNum.getMethod("asFloat");

    private Method
            sByte, sString, sByteArr, sShort,
            sDouble, sIntArr, sBoolean,
            sInt, sLong, sFloat, sLongArray, set, get, size;
    private final Constructor<?>
            intCon = getConstructor(nbtInt, int.class),
            doubleCon = getConstructor(nbtDouble, double.class),
            stringCon = getConstructor(nbtString, String.class),
            byteCon = getConstructor(nbtByte, byte.class),
            byteArrCon = getConstructor(nbtByteArray, byte[].class),
            intArrCon = getConstructor(nbtIntArray, int[].class),
            longCon = getConstructor(nbtLong, long.class),
            shortCon = getConstructor(nbtShort, short.class),
            longArrCon = getConstructor(nbtLongArray, long[].class),
            floatCon = getConstructor(nbtFloat, float.class);

    NBTCompound data, prev;
    public ItemNMSData() throws Exception {
        for(Method m : comp.getMethods()) {
            if(m.getName().equalsIgnoreCase("setByte")) sByte = m;
            if(m.getName().equalsIgnoreCase("setString")) sString = m;
            if(m.getName().equalsIgnoreCase("setByteArray")) sByteArr = m;
            if(m.getName().equalsIgnoreCase("setShort")) sShort = m;
            if(m.getName().equalsIgnoreCase("setDouble")) sDouble = m;
            if(m.getName().equalsIgnoreCase("setIntArray")) sIntArr = m;
            if(m.getName().equalsIgnoreCase("setBoolean")) sBoolean = m;
            if(m.getName().equalsIgnoreCase("setInt")) sInt = m;
            if(m.getName().equalsIgnoreCase("setLong")) sLong = m;
            if(m.getName().equalsIgnoreCase("setFloat")) sFloat = m;
            if(m.getName().equalsIgnoreCase("set")) set = m;
            if(m.getParameterTypes().length == 2)
                if(m.getParameterTypes()[0]==String.class && m.getParameterTypes()[1]==long[].class) sLongArray = m;
        }
        for(Method m : nbtList.getMethods()) {
            if(m.getName().equals("get")) get = m;
            if(m.getName().equals("size")) size = m;
        }
    }

    public NBTCompound getData(ItemStack stack) {
        data = new NBTCompound();
        prev = new NBTCompound();
        try {
            Object nmSStack = asNMSCopy.invoke(null, stack);
            Class<?> nmsStackC = nmSStack.getClass();
            Object compound = ((boolean)nmsStackC.getMethod("hasTag").invoke(nmSStack)) ?
                    nmsStackC.getMethod("getTag").invoke(nmSStack) : comp.newInstance();
            data = convert(compound);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    private NBTCompound convert(Object compound) throws Exception {
        NBTCompound cmp = new NBTCompound();
        Set<String> keys = (Set<String>) gKeys.invoke(compound);
        if(keys != null && keys.size() > 0) {
            for(String key : keys) {
                Object a = comp.getMethod("get", String.class).invoke(compound, key);
                Class<?> c = a.getClass();
                if(c.getName().equals(nbtByte.getName())) cmp.set(key, new NBTByte((Byte) gByte.invoke(compound, key)));
                if(c.getName().equals(nbtString.getName())) cmp.set(key, new NBTString((String) gString.invoke(compound, key)));
                if(c.getName().equals(nbtByteArray.getName())) cmp.set(key, new NBTByteArray((byte[]) gByteArr.invoke(compound, key)));
                if(c.getName().equals(nbtShort.getName())) cmp.set(key, new NBTShort((Short) gShort.invoke(compound, key)));
                if(c.getName().equals(nbtDouble.getName())) cmp.set(key, new NBTDouble((Double) gDouble.invoke(compound, key)));
                if(c.getName().equals(nbtIntArray.getName())) cmp.set(key, new NBTIntegerArray((int[]) gIntArr.invoke(compound, key)));
                if(c.getName().equals(nbtInt.getName())) cmp.set(key, new NBTInteger((Integer) gInt.invoke(compound, key)));
                if(c.getName().equals(nbtLong.getName())) cmp.set(key, new NBTLong((Long) gLong.invoke(compound, key)));
                if(c.getName().equals(nbtFloat.getName())) cmp.set(key, new NBTFloat((Float) gFloat.invoke(compound, key)));
                if(c.getName().equals(nbtLongArray.getName())) cmp.set(key, new NBTLongArray((long[]) gFloat.invoke(compound, key)));
                if(c.getName().equals(comp.getName())) cmp.set(key, convert(a));
                if(c.getName().equals(nbtList.getName())) cmp.set(key, getList(a));
            }
        }
        return cmp;
    }

    private NBTList<?> getList(Object listComp) throws Exception {
        NBTList<? extends NBTBase> list = null;
        int size = (int) this.size.invoke(listComp);
        for(int i = 0; i < size; i++) {
            Object comp = get.invoke(listComp, i);
            Class<?> c = comp.getClass();
            if(c.getName().equals(nbtByte.getName())) {
                if(list == null) list = new NBTByteArray(new ArrayList<>());
                if(list instanceof NBTByteArray)
                    ((NBTByteArray)list).add(new NBTByte((Byte) asByte.invoke(get.invoke(listComp, i))));
            }
            if(c.getName().equals(nbtString.getName())){
                if(list == null) list = new NBTStringArray(new ArrayList<>());
                if(list instanceof NBTStringArray)
                    ((NBTStringArray)list).add(new NBTString((String) asString.invoke(get.invoke(listComp, i))));
            }
            if(c.getName().equals(nbtShort.getName())){
                if(list == null) list = new NBTShortArray(new ArrayList<>());
                if(list instanceof NBTShortArray)
                    ((NBTShortArray)list).add(new NBTShort((Short)asShort.invoke(get.invoke(listComp, i))));
            }
            if(c.getName().equals(nbtDouble.getName())){
                if(list == null) list = new NBTDoubleArray(new ArrayList<>());
                if(list instanceof NBTDoubleArray)
                    ((NBTDoubleArray)list).add(new NBTDouble((Double)asDouble.invoke(get.invoke(listComp, i))));
            }
            if(c.getName().equals(nbtInt.getName())) {
                if(list == null) list = new NBTIntegerArray(new ArrayList<>());
                if(list instanceof NBTIntegerArray)
                    ((NBTIntegerArray) list).add(new NBTInteger((Integer) asInt.invoke(get.invoke(listComp, i))));
            }
            if(c.getName().equals(nbtLong.getName())) {
                if(list == null) list = new NBTLongArray(new ArrayList<>());
                if(list instanceof NBTLongArray)
                    ((NBTLongArray) list).add(new NBTLong((Long)asLong.invoke(get.invoke(listComp, i))));
            }
            if(c.getName().equals(nbtFloat.getName())){
                if(list == null) list = new NBTFloatArray(new ArrayList<>());
                if(list instanceof NBTFloatArray)
                    ((NBTFloatArray) list).add(new NBTFloat((Float)asFloat.invoke(get.invoke(listComp, i))));
            }
        }
        return list;
    }

    public void setData(NBTCompound newData) {
        prev = data;
        this.data = newData;
    }

    public ItemStack clearData(ItemStack stack) {
        try{
            Object nmSStack = asNMSCopy.invoke(null, stack);
            Class<?> nmsStackC = nmSStack.getClass();
            Object emptyTag = comp.newInstance();
            nmsStackC.getMethod("setTag", emptyTag.getClass()).invoke(nmSStack, emptyTag);
            Method asBukkitCopy = Class.forName(Version.getCBPackage() + ".inventory.CraftItemStack").getMethod("asBukkitCopy", nmsStackC);
            stack = (ItemStack) asBukkitCopy.invoke(null, nmSStack);
        }catch (Exception e) {
            System.out.println("Error occurred while clearing NBT data");
            e.printStackTrace();
        }
        return stack;
    }

    public ItemStack finish(ItemStack item) {
        try {
            Object nmSStack = asNMSCopy.invoke(null, item);
            Class<?> nmsStackC = nmSStack.getClass();
            Object compound = comp.newInstance();
            if(data != null) {
                NBTCompound old = data;
                data = getData(item);
                set(old.merge(data), compound);
                nmsStackC.getMethod("setTag", comp).invoke(nmSStack, compound);
                Method asBukkitCopy = Class.forName(Version.getCBPackage() + ".inventory.CraftItemStack").getMethod("asBukkitCopy", nmsStackC);
                Object bukkitItemStack = asBukkitCopy.invoke(null, nmSStack);
                item = (ItemStack) bukkitItemStack;
            }
        }catch (Exception e) {
        }
        return item;
    }


    private Object set(NBTCompound data, Object compound) throws Exception {
        for(Map.Entry<String, NBTBase> key : data.getMap().entrySet()) {
            if(!(key.getValue() instanceof NBTList) && !(key.getValue() instanceof NBTCompound))
                setCompound(compound, key.getKey(), key.getValue());
            else if(key.getValue() instanceof NBTCompound)
                set.invoke(compound, key.getKey(), set((NBTCompound) key.getValue(), comp.newInstance()));
            else setList(compound, key.getKey(), (NBTList<?>) key.getValue());
        }
        return compound;
    }

    private void setList(Object compound, String key, NBTList<?> tags) throws Exception {
        Object list = nbtList.newInstance();
        int i = 0;
        for(NBTBase tag : tags.getList()) {
            switch(tag.getClass().getSimpleName()) {
                case "NBTInteger":
                    add.invoke(list, i, intCon.newInstance(tag.getValue()));
                    break;
                case "NBTDouble":
                    add.invoke(list, i, doubleCon.newInstance(tag.getValue()));
                    break;
                case "NBTString":
                    add.invoke(list, i, stringCon.newInstance(tag.getValue()));
                    break;
                case "NBTByte":
                    add.invoke(list, i, byteCon.newInstance(tag.getValue()));
                    break;
                case "NBTByteArray":
                    add.invoke(list, i, byteArrCon.newInstance(tag.getValue()));
                    break;
                case "NBTFloat":
                    add.invoke(list, i, floatCon.newInstance(tag.getValue()));
                    break;
                case "NBTIntegerArray":
                    add.invoke(list, i, intArrCon.newInstance(tag.getValue()));
                    break;
                case "NBTLong":
                    add.invoke(list, i, longCon.newInstance(tag.getValue()));
                    break;
                case "NBTShort":
                    add.invoke(list, i, shortCon.newInstance(tag.getValue()));
                    break;
                case "NBTLongArray":
                    add.invoke(list, i, longArrCon.newInstance(tag.getValue()));
                    break;
                case "NBTCompound":
                    Object compo = comp.newInstance();
                    for(Map.Entry<String, NBTBase> val : ((HashMap<String, NBTBase>)tag.getValue()).entrySet()) {
                        setCompound(compo, val.getKey(), val.getValue());
                    }
                    add.invoke(list, i, comp);
                    break;
            }
            i++;
        }
        set.invoke(compound, key, list);
    }

    private Constructor<?> getConstructor(Class<?> clazz, Class<?>... types) throws Exception {
        Constructor<?> con;
        try {
            con = clazz.getConstructor(types);
            con.setAccessible(true);
        }catch (Exception e) {
            con = clazz.getDeclaredConstructor(types);
            con.setAccessible(true);
        }
        return con;
    }

    private void setCompound(Object compound, String key, NBTBase tag) throws Exception {
        switch(tag.getClass().getSimpleName()) {
            case "NBTInteger":
                sInt.invoke(compound, key, tag.getValue());
                break;
            case "NBTDouble":
                sDouble.invoke(compound, key, tag.getValue());
                break;
            case "NBTString":
                sString.invoke(compound, key, tag.asString());
                break;
            case "NBTBoolean":
                sBoolean.invoke(compound, key, tag.getValue());
                break;
            case "NBTByte":
                sByte.invoke(compound, key, tag.getValue());
                break;
            case "NBTByteArray":
                sByteArr.invoke(compound, key, tag.getValue());
                break;
            case "NBTFloat":
                sFloat.invoke(compound, key, tag.getValue());
                break;
            case "NBTIntegerArray":
                sIntArr.invoke(compound, key, tag.getValue());
                break;
            case "NBTLong":
                sLong.invoke(compound, key, tag.getValue());
                break;
            case "NBTShort":
                sShort.invoke(compound, key, tag.getValue());
                break;
            case "NBTLongArray":
                sLongArray.invoke(compound, key, tag.getValue());
                break;
            case "NBTCompound":
                Object compo = comp.newInstance();
                for(Map.Entry<String, NBTBase> val : ((HashMap<String, NBTBase>)tag.getValue()).entrySet()) {
                    setCompound(compo, val.getKey(), val.getValue());
                }
                set.invoke(compound, key, compo);
                break;
        }
    }

    public ItemStack damage(Player player, ItemStack itemStack, int amount) {
        try {
            Object nmSStack = asNMSCopy.invoke(null, itemStack);
            Class<?> nmsStackC = nmSStack.getClass();
            Object cp = craftPlayer.cast(player);
            Object ep = craftPlayer.getMethod("getHandle").invoke(cp);
            Method damage;
            try {
                damage = nmsStackC.getMethod("damage", int.class, entityPlayer, Consumer.class);
                damage.invoke(nmSStack,amount, ep, null);
            }catch (NoSuchMethodException e) {
                damage = nmsStackC.getMethod("damage", int.class, entityPlayer);
                damage.invoke(nmSStack, amount, ep);
            }
            Method asBukkitCopy = Class.forName(Version.getCBPackage() + ".inventory.CraftItemStack").getMethod("asBukkitCopy", nmsStackC);
            Object bukkitItemStack = asBukkitCopy.invoke(null, nmSStack);
            return (ItemStack) bukkitItemStack;
        }catch (Exception e) {
            return itemStack;
        }
    }
}
