package perryplaysmc.dynamicgui.utils.nbt;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import perryplaysmc.dynamicgui.utils.nbt.types.*;
import perryplaysmc.dynamicgui.utils.nbt.types.arrays.NBTByteArray;
import perryplaysmc.dynamicgui.utils.nbt.types.arrays.NBTIntegerArray;
import perryplaysmc.dynamicgui.utils.nbt.types.arrays.NBTLongArray;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Pattern;

/**
 * Copy Right ©
 * This code is private
 * Owner: PerryPlaysMC
 * From: 10/2020-Now
 **/
@SuppressWarnings("all")
public class NBTCompound implements NBTBase, Cloneable{

    private static Pattern PATTERN = Pattern.compile("[A-Za-z0-9._+-]+");
    private HashMap<String, NBTBase> data;
    private List<String> removeKeys = new ArrayList<>();
    private NBTCompound owner = null;
    private String name;
    public NBTCompound() {
        this(new HashMap<>());
    }

    public NBTCompound(String name, NBTCompound owner) {
        this(new HashMap<>(), owner);
        this.name = name;
    }

    public NBTCompound(HashMap<String, NBTBase> data) {
        this(data, null);
    }

    public NBTCompound(HashMap<String, NBTBase> data, NBTCompound owner) {
        this.data = data;
        this.owner = owner;
    }

    public NBTCompound getOwner() {
        return owner;
    }

    public boolean hasOwner() {
        return owner!=null;
    }

    public NBTCompound addCompound(String key) {
        NBTCompound comp = getCompound(key) == null ? new NBTCompound(key,this) : getCompound(key);
        if(getCompound(key) == null)
            data.put(key, comp);
        return comp;
    }


    public void setNBTTag(String key, NBTBase value) {
        if(key.contains(".")) {
            String[] deep = key.split("\\.");
            NBTCompound compound = addCompound(deep[0]);
            for(int i = 1; i < deep.length; i++) {
                if(i == deep.length-1) compound.set(deep[i], value);
                else compound = compound.addCompound(deep[i]);
            }
            while(compound.getOwner()!=this) compound = compound.getOwner();
            data.put(deep[0], compound);
            return;
        }
        data.put(key, value);
    }
    public void setList(String key, NBTList<?> value) {
        setNBTTag(key, value);
    }

    public void setString(String key, String value) {
        setNBTTag(key, new NBTString(value));
    }

    public void setInt(String key, int value) {
        setNBTTag(key, new NBTInteger(value));
    }

    public void setDouble(String key, double value) {
        setNBTTag(key, new NBTDouble(value));
    }

    public void setFloat(String key, float value) {
        setNBTTag(key, new NBTFloat(value));
    }

    public void setShort(String key, short value) {
        setNBTTag(key, new NBTShort(value));
    }

    public void setByte(String key, byte value) {
        setNBTTag(key, new NBTByte(value));
    }

    public void setBoolean(String key, boolean value) {
        setNBTTag(key, new NBTByte((byte)(value ? 1 : 0)));
    }

    public void setLong(String key, long value) {
        setNBTTag(key, new NBTLong(value));
    }

    public void setLongArray(String key, Long[] value) {
        setNBTTag(key, new NBTLongArray(value));
    }

    public void setByteArray(String key, byte[] value) {
        setNBTTag(key, new NBTByteArray(value));
    }

    public void setIntArray(String key, int[] value) {
        setNBTTag(key, new NBTIntegerArray(value));
    }

    public void set(String key, NBTBase nbt) {
        setNBTTag(key, nbt);
    }

    public void set(String key, Object nbt) {
        if(nbt instanceof Boolean)
            set(key, new NBTByte((byte)(((Boolean) nbt).booleanValue() ? 1 : 0)));
        else if(nbt instanceof Byte)
            set(key, new NBTByte((Byte) nbt));
        else if(nbt instanceof Byte[] || nbt instanceof byte[])
            set(key, new NBTByteArray((byte[]) nbt));
        else if(nbt instanceof Double)
            set(key, new NBTDouble((Double) nbt));
        else if(nbt instanceof Float)
            set(key, new NBTFloat((Float) nbt));
        else if(nbt instanceof Integer)
            set(key, new NBTInteger((Integer) nbt));
        else if(nbt instanceof Integer[] || nbt instanceof int[])
            set(key, new NBTIntegerArray((int[])nbt));
        else if(nbt instanceof Long)
            set(key, new NBTLong((Long) nbt));
        else if(nbt instanceof Long[] || nbt instanceof long[])
            set(key, new NBTLongArray((long[]) nbt));
        else if(nbt instanceof Short)
            set(key, new NBTShort((Short) nbt));
        else if(nbt instanceof String)
            set(key, new NBTString((String) nbt));
        else
            set(key, new NBTString("Json:" + new Gson().toJson(nbt, nbt.getClass())));
    }


    public boolean hasKey(String key) {
        if(key.charAt(0)=='.')key = key.substring(1);
        String k = key;
        if(key.contains(".")) k = key.split("\\.")[0];
        if(!data.containsKey(k)) return false;
        if(!(data.get(k) instanceof NBTCompound) || k.equals(key)) return true;
        return ((NBTCompound) data.get(k)).hasKey(key.replace(k, ""));
    }

    private NBTBase dig(String key) {
        String k = key;
        if(key.contains(".")) k = key.split("\\.")[0];
        if(getMap().get(k)==null) {
            return null;
        }
        if(!(getMap().get(k) instanceof NBTCompound)) {
            return getMap().get(key);
        }
        if(key.equals(k))return getMap().get(k);
        return ((NBTCompound) getMap().get(k)).dig(key.replace(k + ".", ""));
    }

    public NBTBase get(String key) {
        return dig(key);
    }

    private void removeKey(NBTCompound comp, String key) {
        String k = key;
        if(key.contains(".")) k = key.split("\\.")[0];
        if(comp.data.get(k)==null) {
            return;
        }
        if(!(comp.data.get(k) instanceof NBTCompound)) {
            comp.data.remove(key);
            while(comp.data.isEmpty() && comp.hasOwner()) {
                comp.getOwner().data.remove(comp.name);
                comp = comp.getOwner();
            }
            if(comp.hasOwner() && comp.data.isEmpty())  comp.getOwner().data.remove(comp.name);
            return;
        }
        ((NBTCompound) comp.data.get(k)).removeKey(key.replace(k + ".", ""));
    }

    public void removeKey(String key) {
        if(hasKey(key)) {
            if(!removeKeys.contains(key) && !hasOwner())
                removeKeys.add(key);
            removeKey(this, key);
        }
    }

    public NBTCompound getCompound(String key) {
        return hasKey(key) && get(key) instanceof NBTCompound ? (NBTCompound) data.get(key) : null;
    }

    public <T> void setObject(String key, T object, Class<T> tclass) {
        setString(key, "Json:" + new Gson().toJson(object, tclass));
    }

    public <T> T getObject(String key, Class<T> tclass) {
        String value = getString(key);
        if(value.startsWith("Json:")) return new Gson().fromJson(value.replaceFirst("Json:", ""), tclass);
        return null;
    }
    public NBTList<?> getList(String key) {
        return hasKey(key) && get(key) instanceof NBTList ? ((NBTList)get(key)) : new NBTEmptyList();
    }

    public Byte getByte(String key) {
        return hasKey(key) && get(key) instanceof NBTByte ? ((NBTByte)get(key)).getValue() : -1;
    }

    public byte[] getByteArray(String key) {
        return hasKey(key) && get(key) instanceof NBTByteArray ? ((NBTByteArray)get(key)).getValue() : new byte[]{};
    }

    public Double getDouble(String key) {
        return hasKey(key) && get(key) instanceof NBTDouble ? ((NBTDouble)get(key)).getValue() : -1;
    }

    public Float getFloat(String key) {
        return hasKey(key) && get(key) instanceof NBTFloat ? ((NBTFloat)get(key)).getValue() : -1;
    }

    public Integer getInteger(String key) {
        return hasKey(key) && get(key) instanceof NBTInteger ? ((NBTInteger)get(key)).getValue() : -1;
    }

    public int[] getIntegerArray(String key) {
        return hasKey(key) && get(key) instanceof NBTIntegerArray ? ((NBTIntegerArray)get(key)).getValue() : new int[]{};
    }

    public Long getLong(String key) {
        return hasKey(key) && get(key) instanceof NBTLong ? ((NBTLong)get(key)).getValue() : -1;
    }

    public Short getShort(String key) {
        return hasKey(key) && get(key) instanceof NBTShort ? ((NBTShort)get(key)).getValue() : -1;
    }

    public String getString(String key) {
        return hasKey(key) && get(key) instanceof NBTString ? ((NBTString)get(key)).asString() : "null";
    }

    public long[] getLongArray(String key) {
        return hasKey(key) && get(key) instanceof NBTLongArray ? ((NBTLongArray)get(key)).getValue() : new long[]{};
    }

    public Boolean getBoolean(String key) {
        return hasKey(key) ? (getByte(key) == 1) : false;
    }

    public HashMap<String, NBTBase> getMap() {
        return data;
    }

    @Override
    public NBTCompound clone() {
        return new NBTCompound(data);
    }

    public NBTCompound merge(NBTCompound other) {
        NBTCompound comp = this;
        for(String key : other.data.keySet()) if(!comp.hasKey(key) && other.data.get(key)!=null ) comp.setNBTTag(key, other.data.get(key));
        for(String s : removeKeys) if(comp.hasKey(s)) comp.removeKey(s);
        return comp;
    }

    public String toString() {
        StringBuilder var0 = new StringBuilder("{");
        Collection<String> var1 = this.data.keySet();
        List<String> var2 = Lists.newArrayList(this.data.keySet());
        Collections.sort(var2);
        var1 = var2;

        String var3;
        for(Iterator var5 =
            ((Collection)var1).iterator();
            var5.hasNext();
            var0.append(s(var3)).append(':').append(this.data.get(var3))) {
            var3 = (String)var5.next();
            if (var0.length() != 1) {
                var0.append(',');
            }
        }

        return var0.append('}').toString();
    }

    protected static String s(String var0) {
        return PATTERN.matcher(var0).matches() ? var0 : NBTString.convert(var0);
    }

    @Override
    public HashMap<String, NBTBase> getValue() {
        return data;
    }

    @Override
    public byte getTypeId() {
        return -1;
    }
}
