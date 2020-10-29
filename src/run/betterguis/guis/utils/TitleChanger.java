package betterguis.guis.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Copy Right ©
 * This code is private
 * Owner: PerryPlaysMC
 * From: 10/2020-Now
 * <p>
 * Any attempts to use these program(s) may result in a penalty of up to $1,000 USD
 **/
@SuppressWarnings("all")
public class TitleChanger {


    /**
     * Changes the current title of the open inventory of the player
     * @param player
     * @param title
     */
    public static void update(Player player, String newTitle) {
        try{
            if(player.getOpenInventory().getTopInventory() == null) return;
            Object ep = player.getClass().getMethod("getHandle").invoke(player);
            Class<?> pkt = Class.forName(Version.getNMSPackage() + ".PacketPlayOutOpenWindow");
            Class<?> cm = Class.forName(Version.getNMSPackage() + ".IChatBaseComponent");
            Class<?> packetClass = Class.forName(Version.getNMSPackage() + ".Packet");
            int id = (int)ep.getClass().getField("activeContainer").get(ep).getClass().getField("windowId")
                    .get(ep.getClass().getField("activeContainer").get(ep));
            Object chatMessage = Class.forName(Version.getNMSPackage() + ".ChatMessage").getConstructor(String.class).newInstance(newTitle);
            Inventory inv = player.getOpenInventory().getTopInventory();
            if(Version.isCurrentHigher(Version.v1_13)) {
                Class<?> containers = Class.forName(Version.getNMSPackage() + ".Containers");
                Class<?> cont = Class.forName(Version.getNMSPackage() + ".Container");
                Constructor<?> con = pkt.getConstructor(int.class, containers, cm);
                Field f = ep.getClass().getField("playerConnection");
                Object conn = f.get(ep);
                conn.getClass().getMethod("sendPacket", packetClass).invoke(conn, con.newInstance(
                        id, containers.getField("GENERIC_9X" + (inv.getSize()/9)).get(null),
                        chatMessage
                ));
                ep.getClass().getMethod("updateInventory", cont)
                        .invoke(ep, ep.getClass().getField("activeContainer").get(ep));
            }else {
                Constructor<?> con = pkt.getConstructor(int.class, String.class, cm, int.class);
                Field f = ep.getClass().getField("playerConnection");
                Object conn = f.get(ep);
                conn.getClass().getMethod("sendPacket", packetClass).invoke(conn, con.newInstance(
                        id, "minecraft:chest", chatMessage, inv.getSize()
                ));
                ep.getClass().getMethod("updateInventory", ep.getClass().getField("activeContainer").get(ep).getClass())
                        .invoke(ep, ep.getClass().getField("activeContainer").get(ep));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
