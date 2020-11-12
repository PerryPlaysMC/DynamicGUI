package dev.perryplaysmc.mines.utils.guis.item;

import dev.perryplaysmc.mines.utils.guis.item.utils.ItemParser;
import dev.perryplaysmc.mines.utils.guis.utils.nbt.NBTBase;
import dev.perryplaysmc.mines.utils.guis.utils.nbt.NBTCompound;
import dev.perryplaysmc.mines.utils.guis.utils.nbt.NBTList;
import dev.perryplaysmc.mines.utils.guis.utils.nbt.types.*;
import dev.perryplaysmc.mines.utils.guis.utils.nbt.types.arrays.NBTByteArray;
import dev.perryplaysmc.mines.utils.guis.utils.nbt.types.arrays.NBTStringArray;
import com.google.common.collect.ImmutableList;
import dev.perryplaysmc.mines.utils.guis.utils.ItemNMSData;
import dev.perryplaysmc.mines.utils.guis.utils.nbt.types.arrays.NBTIntegerArray;
import dev.perryplaysmc.mines.utils.guis.utils.nbt.types.arrays.NBTLongArray;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copy Right ©
 * This code is private
 * Owner: PerryPlaysMC
 * From: 10/2020-Now
 * <p>
 * Any attempts to use these program(s) may result in a penalty of up to $1,000 USD
 **/
@SuppressWarnings("all")
public class ItemBuilder {

    private ItemStack item;
    private ItemMeta itemMeta;
    private List<String> lore;
    private ItemNMSData dataUtil;
    private HashMap<String, String> replaceLore = new HashMap<>();
    private HashMap<String, String> replaceName = new HashMap<>();
    private NBTCompound data = new NBTCompound();

    public ItemBuilder(ItemStack item) {
        this.item = item;
        this.itemMeta = item.getItemMeta();
        this.lore = new ArrayList<>();
        if(itemMeta!=null&&itemMeta.hasLore())
            lore = itemMeta.getLore();
        try {
            this.dataUtil = new ItemNMSData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.data = dataUtil.getData(item);
    }

    public ItemBuilder(String item) {
        this(ItemParser.findMaterial(item,1));
    }

    public ItemBuilder(String item, int amount) {
        this(ItemParser.findMaterial(item,amount));
    }

    public ItemBuilder(XMaterial material) {
        this(material.parseItem());
    }

    public ItemBuilder(XMaterial material, int amount) {
        this(material.parseItem(amount));
    }

    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }

    public ItemBuilder(Material material, int amount) {
        this(new ItemStack(material, amount));
    }

    public String toString() {
        return "ItemBuilder: {" + data.toString() + "}";
    }

    public ItemBuilder setCompound(NBTCompound compound) {
        this.data = compound;
        return this;
    }

    public ItemBuilder removeKey(String key) {
        data.removeKey(key);
        return this;
    }
    public ItemBuilder setNBTTag(String key, NBTBase tag) {
        data.setNBTTag(key, tag);
        return this;
    }

    private ItemBuilder setNBTTag(String key, Object value) {
        data.set(key, value);
        return this;
    }

    public <T> ItemBuilder setObject(String key, T value, Class<T> tClass) {
        data.setObject(key, value, tClass);
        return this;
    }

    public <T> T getObject(String key, Class<T> tClass) {
       return data.<T>getObject(key, tClass);
    }
    public ItemBuilder setList(String key, NBTList<?> value) {
        return setNBTTag(key, value);
    }

    public ItemBuilder setString(String key, String value) {
        return setNBTTag(key, new NBTString(value));
    }

    public ItemBuilder setInt(String key, int value) {
        return setNBTTag(key, new NBTInteger(value));
    }

    public ItemBuilder setDouble(String key, double value) {
        return setNBTTag(key, new NBTDouble(value));
    }

    public ItemBuilder setFloat(String key, float value) {
        return setNBTTag(key, new NBTFloat(value));
    }

    public ItemBuilder setShort(String key, short value) {
        return setNBTTag(key, new NBTShort(value));
    }

    public ItemBuilder setByte(String key, byte value) {
        return setNBTTag(key, new NBTByte(value));
    }

    public ItemBuilder setBoolean(String key, boolean value) {
        return setNBTTag(key, new NBTByte((byte)(value ? 1 : 0)));
    }

    public ItemBuilder setLong(String key, long value) {
        return setNBTTag(key, new NBTLong(value));
    }

    public ItemBuilder setLongArray(String key, long... value) {
        return setNBTTag(key, new NBTLongArray(value));
    }

    public ItemBuilder setByteArray(String key, byte... value) {
        return setNBTTag(key, new NBTByteArray(value));
    }

    public ItemBuilder setIntArray(String key, int... value) {
        return setNBTTag(key, new NBTIntegerArray(value));
    }

    public ItemBuilder setStringArray(String key, String[] value) {
        return setList(key, new NBTStringArray(new ArrayList<>()) {{
            for(String s : value) add(new NBTString(s));
        }});
    }

    public String[] getStringArray(String key) {
        if(!hasKey(key))return new String[]{};
        if(getList(key) instanceof NBTEmptyList) return new String[]{};
        if(getList(key) instanceof NBTStringArray) return ((NBTStringArray)getList(key)).getValue();
        return null;
    }

    public NBTCompound getCompound() {
        return data;
    }

    public NBTCompound getCompound(String key) {
        return data.getCompound(key);
    }

    public NBTBase getNBTTag(String key) {
        return data.get(key);
    }

    public boolean hasKey(String key) {
        return data.hasKey(key);
    }

    public NBTList<?> getList(String key) {
        return data.getList(key);
    }

    public String getString(String key) {
        return data.getString(key);
    }

    public Integer getInteger(String key) {
        return data.getInteger(key);
    }

    public Double getDouble(String key) {
        return data.getDouble(key);
    }

    public Float getFloat(String key) {
        return data.getFloat(key);
    }

    public Short getShort(String key) {
        return data.getShort(key);
    }

    public Byte getByte(String key) {
        return data.getByte(key);
    }

    public Boolean getBoolean(String key) {
        return data.getBoolean(key);
    }

    public Long getLong(String key) {
        return data.getLong(key);
    }

    public long[] getLongArray(String key) {
        return data.getLongArray(key);
    }

    public byte[] getByteArray(String key) {
        return data.getByteArray(key);
    }

    public int[] getIntArray(String key) {
        return data.getIntegerArray(key);
    }

    public ItemBuilder name(String name) {
        if(itemMeta==null) itemMeta = item.getItemMeta();
        if(itemMeta==null) return this;
        if(name.isEmpty() || name == null) name = "§";
        itemMeta.setDisplayName(translate(name));
        return this;
    }

    public ItemBuilder replaceInName(String key, Object replacement) {
        if(itemMeta==null) itemMeta = item.getItemMeta();
        if(itemMeta==null) return this;
        replaceName.put(key, replacement+"");
        return this;
    }

    public ItemBuilder replaceInLore(String key, Object replacement) {
        if(itemMeta==null) itemMeta = item.getItemMeta();
        if(itemMeta==null) return this;
        replaceLore.put(key, replacement+"");
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        if(itemMeta==null) itemMeta = item.getItemMeta();
        if(itemMeta==null) return this;
        for(String line : lore)
            this.lore.add(translate(line));
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        if(itemMeta==null) itemMeta = item.getItemMeta();
        if(itemMeta==null) return this;
        this.lore.clear();
        for(String line : lore)
            this.lore.add(translate(line));
        return this;
    }

    public ItemBuilder setLoreLine(int index, String line) {
        if(itemMeta==null) itemMeta = item.getItemMeta();
        if(itemMeta==null) return this;
        lore.set(index, translate(line));
        return this;
    }

    public ItemBuilder removeLoreLine(int index) {
        if(itemMeta==null) itemMeta = item.getItemMeta();
        if(itemMeta==null) return this;
        lore.remove(index);
        return this;
    }

    public ItemBuilder removeLoreLine(String text) {
        if(itemMeta==null) itemMeta = item.getItemMeta();
        if(itemMeta==null) return this;
        lore.remove(translate(text));
        return this;
    }

    public ItemBuilder addAttribbute(Attribute attribbute, AttributeModifier mod) {
        if(itemMeta==null) itemMeta = item.getItemMeta();
        if(itemMeta==null) return this;
        itemMeta.addAttributeModifier(attribbute, mod);
        return this;
    }


    public ItemBuilder addFlags(ItemFlag... flags) {
        if(itemMeta==null) itemMeta = item.getItemMeta();
        if(itemMeta==null) return this;
        itemMeta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder removeFlags(ItemFlag... flags) {
        if(itemMeta==null) itemMeta = item.getItemMeta();
        if(itemMeta==null) return this;
        itemMeta.removeItemFlags(flags);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        if(itemMeta==null) itemMeta = item.getItemMeta();
        if(itemMeta==null) return this;
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        if(itemMeta==null) itemMeta = item.getItemMeta();
        if(itemMeta==null) return this;
        itemMeta.removeEnchant(enchantment);
        return this;
    }

    public boolean hasEnchantment(Enchantment enchantment) {
        if(itemMeta==null) itemMeta = item.getItemMeta();
        if(itemMeta==null) return false;
        return itemMeta.hasEnchant(enchantment);
    }

    public String getName() {
        if(itemMeta==null) itemMeta = item.getItemMeta();
        if(itemMeta==null) return "null";
        return itemMeta.getDisplayName();
    }

    public List<String> getLore() {
        if(itemMeta==null) itemMeta = item.getItemMeta();
        if(itemMeta==null) return ImmutableList.of();
        return new ArrayList<>(lore);
    }

    public ItemStack getItem() {
        if(itemMeta!=null) {
            List<String> newLore = new ArrayList<>();
            for(String line : lore) {
                for(Map.Entry<String, String> entry : replaceLore.entrySet()) {
                    String key = entry.getKey();
                    String replacement = entry.getValue();
                    line = line.replace(key, replacement);
                }
                newLore.add(line);
            }
            String name = itemMeta.getDisplayName();
            for(Map.Entry<String, String> entry : replaceName.entrySet()) {
                String key = entry.getKey();
                String replacement = entry.getValue();
                name = name.replace(key, replacement);
            }
            itemMeta.setDisplayName(name);
            itemMeta.setLore(newLore);
            item.setItemMeta(itemMeta);
        }
        if(data != null) {
            dataUtil.setData(data);
            item = dataUtil.finish(item);
        }
        return item;
    }

    public static boolean isAir(Material mat) {
        return mat.name().endsWith("AIR") && !mat.name().endsWith("AIRS");
    }

    public static boolean isAir(ItemStack item) {
        return item == null || isAir(item.getType());
    }

    private String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
