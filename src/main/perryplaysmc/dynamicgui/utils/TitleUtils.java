package perryplaysmc.dynamicgui.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Copy Right ©
 * This code is private
 * Owner: PerryPlaysMC
 * From: 10/2020-Now
 **/

public class TitleUtils {


    private static Method getHandle, sendPacket, updateInventory, getName, getTitle, getMCTitle, getInventory;
    private static Field ac, wi, pc;
    private static Class<?> ppoow, icbc, cs, cic, ci, mci;
    private static Constructor<?> chatMessageCon;

    static {
        try {
            {
                sendPacket =  getMethod(Version.Minecraft.getClass("PlayerConnection"),"sendPacket", Version.Minecraft.getClass("Packet"));
                chatMessageCon = getConstructor(Version.Minecraft.getClass("ChatMessage"), String.class);
                getHandle = getMethod(Version.CraftBukkit.getClass("entity.CraftPlayer"),"getHandle");
                ppoow = Version.Minecraft.getClass("PacketPlayOutOpenWindow");
                icbc = Version.Minecraft.getClass("IChatBaseComponent");
                Class<?> ep = Version.Minecraft.getClass("EntityPlayer");
                cs = Version.Minecraft.getClass("Containers");
                Class<?> c = Version.Minecraft.getClass("Container");
                pc = getField(ep,"playerConnection");
                ac = getField(ep,"activeContainer");
                wi = getField(c,"windowId");
                updateInventory = ep.getMethod("updateInventory", c);
            }
            {
                cic = Version.CraftBukkit.getClass("inventory.CraftInventoryCustom");
                ci = Version.CraftBukkit.getClass("inventory.CraftInventory");
                mci = cic.getDeclaredClasses()[0];
                getInventory = getMethod(ci, "getInventory");
                Class<?> inv = Inventory.class;
                getTitle = getMethod(inv,"getTitle");
                getName = getMethod(inv,"getName");
                getMCTitle = getMethod(mci, "getTitle");
                getMCTitle.setAccessible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Constructor<?> getConstructor(Class<?> c, Class<?>... args) {
        if(c == null) return null;
        try {
            return c.getConstructor(args);
        }catch (Exception e) {
            try {
                return c.getDeclaredConstructor(args);
            } catch (NoSuchMethodException noSuchMethodException) {
                return null;
            }
        }
    }

    private static Field getField(Class<?> c, String name) {
        if(c == null) return null;
        try {
            return c.getField(name);
        }catch (Exception e) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException noSuchFieldException) {
                return null;
            }
        }
    }

    private static Method getMethod(Class<?> c, String name, Class<?>... args) {
        if(c == null) return null;
        try {
            return c.getMethod(name, args);
        }catch (Exception e) {
            try {
                return c.getDeclaredMethod(name, args);
            } catch (NoSuchMethodException noSuchMethodException) {
                return null;
            }
        }
    }

    /**
     * Changes the current title of the open inventory of the player
     * @param player
     * @param newTitle
     */
    public static void update(Player player, String newTitle) {
        try{
            if(player.getOpenInventory().getTopInventory() == null) return;
            Inventory inv = player.getOpenInventory().getTopInventory();
            Object ep = getHandle.invoke(player);
            Object activeContainer = ac.get(ep);
            Object chatMessage = chatMessageCon.newInstance(newTitle);
            int id = (int)wi.get(activeContainer);
            if(Version.isCurrentHigher(Version.v1_13)) {
                Constructor<?> con = ppoow.getConstructor(int.class, cs, icbc);
                sendPacket.invoke(pc.get(ep),
                        con.newInstance(id, cs.getField("GENERIC_9X" + (inv.getSize()/9)).get(null), chatMessage));
            }else {
                Constructor<?> con = ppoow.getConstructor(int.class, String.class, icbc, int.class);
                sendPacket.invoke(pc.get(ep),
                        con.newInstance(id, "minecraft:chest", chatMessage, inv.getSize()));
            }
            updateInventory.invoke(ep, activeContainer);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getName(Inventory inv) {
        try {
            return (String) getName.invoke(inv);
        } catch (Exception ignored) {
            try {
                return (String) getTitle.invoke(inv);
            } catch (Exception ignored1) {
                try {
                    String title = "";
                    if(inv.getClass().isAssignableFrom(cic))
                        title = (String) getMCTitle.invoke(mci.cast(getInventory.invoke(ci.cast(inv))));
                    return inv.getClass().isAssignableFrom(cic) ? title : inv.getType().getDefaultTitle();
                } catch (Exception e) {
                    e.printStackTrace();//Print errors to let peeps know if something happened
                    System.out.println("There was an error while getting an inventory name");
                    return "Error, check console";
                }
            }
        }
    }

}
