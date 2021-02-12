package perryplaysmc.dynamicgui.item.utils;

import perryplaysmc.dynamicgui.item.XMaterial;
import perryplaysmc.dynamicgui.utils.Version;
import dev.perryplaysmc.dynamicguiplugin.PluginMain;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Copy Right ©
 * This code is private
 * Owner: PerryPlaysMC
 * From: 10/2020-Now
 **/
@SuppressWarnings("all")
public class ItemParser {

    static ItemUtility util;
    static List<Material> woods, leaves;
    static HashMap<String, ItemStack> materials = new HashMap<>();
    static List<String> str = new ArrayList<>();


    static {
        util = new ItemUtility();
        woods = new ArrayList<>();
        leaves = new ArrayList<>();
        for(Material m : Material.values()) {
            if(!str.contains(m.name()))
                str.add(m.name());
        }
        if(Version.isCurrentHigher(Version.v1_12)) {
            woods.add(Material.ACACIA_LOG);
            woods.add(Material.BIRCH_LOG);
            woods.add(Material.DARK_OAK_LOG);
            woods.add(Material.BIRCH_LOG);
            woods.add(Material.JUNGLE_LOG);
            woods.add(Material.OAK_LOG);
            woods.add(Material.SPRUCE_LOG);

            woods.add(Material.STRIPPED_ACACIA_LOG);
            woods.add(Material.STRIPPED_BIRCH_LOG);
            woods.add(Material.STRIPPED_DARK_OAK_LOG);
            woods.add(Material.STRIPPED_BIRCH_LOG);
            woods.add(Material.STRIPPED_JUNGLE_LOG);
            woods.add(Material.STRIPPED_OAK_LOG);
            woods.add(Material.STRIPPED_SPRUCE_LOG);

            leaves.add(Material.ACACIA_LEAVES);
            leaves.add(Material.BIRCH_LEAVES);
            leaves.add(Material.DARK_OAK_LEAVES);
            leaves.add(Material.JUNGLE_LEAVES);
            leaves.add(Material.OAK_LEAVES);
            leaves.add(Material.SPRUCE_LEAVES);
        } else {
            woods.add(convertMaterial("LOG"));
            woods.add(convertMaterial("LOG_2"));

            leaves.add(convertMaterial("LEAVES"));
            leaves.add(convertMaterial("LEAVES_2"));
        }
        File f = new File(PluginMain.getPlugin(PluginMain.class).getDataFolder(), "items.cvs");
        if(!Version.isCurrentHigher(Version.v1_12))
            for(String s : readFile(f)) {
                //#item,id,metadata
                if(s.startsWith("#")) continue;
                String[] sp = s.split(",");
                if(sp.length == 3) {
                    Material id = byId(getInt(sp[1]));
                    //public ItemStack(@NotNull Material type, int amount, short damage) {
                    if(id != null && !materials.containsKey(sp[0].toLowerCase())) {
                        int i = getInt(sp[2]);
                        materials.put(sp[0].toLowerCase(), new ItemStack(id, 1, (short) i));
                    }
                }
            }
    }

    public static Integer getInt(String intToGet) {
        int ret = 0;
        try {
            ret = Integer.parseInt(intToGet);
        } catch (Exception e) {
        }
        return ret;
    }

    public static Material byId(int id) {
        for(Material c : Material.values()) {
            if(Version.isCurrentHigher(Version.v1_12)) {
                if(Bukkit.getUnsafe().toLegacy(c).getId() == id) return c;
            } else{
                if(c.getId() == id) return c;
            }
        }
        return null;
    }

    public static Material convertMaterial(Material m) {
        return XMaterial.matchXMaterial(m)!=null ? XMaterial.matchXMaterial(m).parseMaterial() : m;
    }

    static Material getMaterial(String name) {
        if(name.startsWith("LEGACY_"))
            return Material.matchMaterial(name, true);
        else
            return Material.matchMaterial(name);
    }

    public static ItemStack findMaterial(String line, int amount) {
        if(materials.containsKey(line.toLowerCase())) {
            ItemStack im = materials.get(line.toLowerCase());
            im.setAmount(amount);
            return im;
        }
        line = line.toUpperCase();
        for(Material m : Material.values()) {
            if(line.equalsIgnoreCase(m.name()) ||
                    ("LEGACY_" + line).equalsIgnoreCase(m.name()) ||
                    line.replace("LEGACY_", "").equalsIgnoreCase(m.name()) ||
                    line.equalsIgnoreCase(m.name().replace("_", "")))
                return new ItemStack(m, amount);
        }
        if(Material.getMaterial(line) != null)
            return new ItemStack(Material.getMaterial(line), amount);

        if(Material.getMaterial(line.replace("LEGACY_", "")) != null)
            return new ItemStack(Material.getMaterial(line.replace("LEGACY_", "")), amount);

        if(Material.getMaterial(line.replace("_", "")) != null)
            return new ItemStack(Material.getMaterial(line), amount);

        if(Material.getMaterial("LEGACY_" + line) != null)
            return new ItemStack(Material.getMaterial("LEGACY_" + line), amount);

        if(Material.getMaterial(line.replace("GOLDEN", "GOLD")) != null)
            return new ItemStack(Material.getMaterial(line.replace("GOLDEN", "GOLD")), amount);

        if(Material.getMaterial(line.replace("SHOVEL", "SPADE")) != null)
            return new ItemStack(Material.getMaterial(line.replace("SHOVEL", "SPADE")), amount);
        line = line.toLowerCase();
        return util.get(line);
    }

    public static Material convertMaterial(String m) {
        if(Version.isCurrentHigher(Version.v1_12)) {
            if(m == null || m.isEmpty()) return null;
            if(getMaterial("LEGACY_" + m.toUpperCase()) != null)
                return getMaterial("LEGACY_" + m.toUpperCase());
            if(getMaterial(m.toUpperCase()) != null)
                return getMaterial(m.toUpperCase());
            return getMaterial(m.toUpperCase());
        }
        if(m.toUpperCase().startsWith("LEGACY_"))
            return Material.getMaterial(m.toUpperCase().replace("LEGACY_", "").replace("SHOVEL", "SPADE"));
        if(findMaterial(m, 1) != null && !isAir(findMaterial(m, 1).getType()))
            return findMaterial(m, 1).getType();
        return Material.getMaterial(m.toUpperCase());
    }

    public static Material getMaterialFromLine(String line) {
        line = line.toUpperCase();
        Material m = Material.getMaterial("LEGACY_" + line);
        if(line.contains("SIGN") && (m = Material.getMaterial("OAK_" + line)) != null) return m;
        if((m = Material.getMaterial("LEGACY_" + line)) != null) return m;
        if((m = Material.getMaterial(line)) != null) return m;
        if((m = Material.getMaterial(line.replace("LEGACY_", ""))) != null) return m;
        if((m = Material.getMaterial(line)) != null) return m;
        for(Material m1 : Material.values()) {
            if(line.equalsIgnoreCase(m1.name()) ||
                    ("LEGACY_" + line).equalsIgnoreCase(m1.name()) ||
                    line.replace("LEGACY_", "").equalsIgnoreCase(m1.name()) ||
                    line.equalsIgnoreCase(m1.name().replace("_", "")))
                return m1;
        }
        return null;
    }

    public static boolean isAir(Material m) {
        return m.name().endsWith("AIR") && !m.name().endsWith("AIRS");
    }


    public static ItemStack toStack(String material) {
        return new ItemStack(convertMaterial(material));
    }

    public static List<Material> getLeaves() {
        return leaves;
    }

    public static List<Material> getWoods() {
        return woods;
    }

    public static boolean isWood(Material m) {
        return woods.contains(m);
    }

    private static List<String> readFile(File f) {
        try {
            if(!f.exists())
                return new ArrayList<>();
            FileInputStream i = new FileInputStream(f);
            int index;
            String line = "";
            List<String> lines = new ArrayList<>();
            while((index = i.read()) !=-1) {
                char c = (char) index;
                if(c=='\n') {
                    lines.add(line);
                    line="";
                    continue;
                }
                line+=c;
            }
            return lines;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
