package dev.perryplaysmc.mines.utils.guis;

import dev.perryplaysmc.mines.utils.guis.utils.guitypes.BetterListGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/
@SuppressWarnings("unused")
public class BetterGUIManager implements Listener {

    private static final Set<BetterGUI> guis = new HashSet<>(), ignored = new HashSet<>();
    private static final Set<BetterListGUI> listGuis = new HashSet<>();
    private final List<Player> tookTop = new ArrayList<>(), tookBottom = new ArrayList<>();

    public static Set<BetterGUI> getBetterGUIS() {
        return guis;
    }

    public static BetterGUI getOrCreate(String name, int size) {
        BetterGUI gui = getBetterGUI(name);
        if(gui==null) return new BetterGUI(name, size);
        gui.setNew(false);
        return gui;
    }

    public static Set<BetterListGUI> getBetterListGUIS() {
        return listGuis;
    }

    public static BetterListGUI getOrCreateList(BetterGUI main, String name, int size) {
        BetterListGUI gui = getBetterListGUI(name);
        if(gui==null) return new BetterListGUI(main, name, size);
        gui.setNew(false);
        return gui;
    }

    public static BetterListGUI createList(BetterGUI main, String name, int size) {
        return new BetterListGUI(main, name, size);
    }

    public static BetterGUI create(String name, int size) {
        return new BetterGUI(name, size);
    }

    private void addTop(Player p) {
        tookTop.add(p);
        tookBottom.remove(p);
    }


    private void addBottom(Player p) {
        tookBottom.add(p);
        tookTop.remove(p);
    }

    @EventHandler void onClick(InventoryClickEvent event) {
        BetterGUI gui = getBetterGUI(event.getView().getTitle(), event.getInventory());
        if(gui == null) return;
        if(!isBetterGUIAvailable(gui)) return;
        Player p = (Player) event.getWhoClicked();
        Inventory top = event.getView().getTopInventory();
        boolean isTop = event.getClickedInventory() == event.getView().getTopInventory();
        boolean isBottom = event.getClickedInventory() == event.getView().getBottomInventory();
        boolean itemExists = event.getCurrentItem() != null && !isAir(event.getCurrentItem().getType());
        boolean cursorExists = event.getCursor() != null && !isAir(event.getCursor().getType());
        boolean isShift = event.getClick().name().contains("SHIFT");
        if(gui.isCancelled() && isTop) event.setCancelled(true);
        if(gui.isCancelled() && isShift && isBottom) event.setCancelled(true);
        if(gui.isCancelled() && event.getClick() == ClickType.DOUBLE_CLICK) event.setCancelled(true);
        if(!gui.canTake() && !gui.isCancelled()) {
            if(isTop) {
                if(tookTop.contains(p) && !itemExists) tookTop.remove(p);
                if(!event.isCancelled() && itemExists) addTop(p);
                if(isShift) event.setCancelled(true);
                if(event.getClick() == ClickType.DROP) event.setCancelled(true);
                if(event.getClick() == ClickType.CONTROL_DROP) event.setCancelled(true);
            }
            if(tookTop.contains(p) && isBottom) event.setCancelled(true);
        }
        if(!gui.canAdd() && !gui.isCancelled()) {
            if(isBottom) {
                if(tookBottom.contains(p) && !itemExists) tookBottom.remove(p);
                if(!event.isCancelled() && itemExists) addBottom(p);
                if(isShift) event.setCancelled(true);
            }
            if(tookBottom.contains(p) && isTop) event.setCancelled(true);
        }
        if(event.getClick() == ClickType.DOUBLE_CLICK && (itemExists || cursorExists)) {
            ItemStack item = itemExists ? event.getCurrentItem() : event.getCursor();
            if(item != null) {
                if(isTop && p.getInventory().contains(item.getType())) {
                    if(!gui.canAdd()) event.setCancelled(true);
                    else if(!tookTop.contains(p)) addTop(p);
                }
                if(isBottom && top.contains(item.getType())) {
                    if(!gui.canTake()) event.setCancelled(true);
                    else if(!tookBottom.contains(p)) addBottom(p);
                }
            }
        }
        if(tookTop.contains(p) && !gui.canTake() && !isTop && !isBottom && !gui.isCancelled()) event.setCancelled(true);
        if(tookTop.contains(p) && isBottom && !gui.canTake() && !gui.isCancelled()) event.setCancelled(true);
        if(tookBottom.contains(p) && isTop && !gui.canAdd() && !gui.isCancelled()) event.setCancelled(true);
        if(isTop) {
            ItemStack before = event.getCurrentItem();
            if(gui.isDisabled(event.getSlot())) event.setCancelled(true);
            if(gui.isFiller(event.getSlot())) event.setCancelled(true);
            if(gui.getAction(event.getSlot()) != null)
                gui.getAction(event.getSlot()).onEvent(gui, event.getCurrentItem(), event);
            else if(gui.getClick() != null) gui.getClick().onEvent(event);
            if(gui.canPlaySound())
                p.playSound(p.getLocation(), gui.getSound(), 0.2f, 1);
            if(before!=event.getCurrentItem())
                gui.setItem(event.getSlot(), event.getCurrentItem());
        }
        if(event.isCancelled()) p.updateInventory();
    }

    @EventHandler void onClose(InventoryCloseEvent event) {
        BetterGUI gui = getBetterGUI(event.getView().getTitle());
        if(gui==null)return;
        if(!isBetterGUIAvailable(gui))return;
        Player p = (Player) event.getPlayer();
        tookBottom.remove(p);
        tookTop.remove(p);
        if(gui.getClose()!=null) gui.getClose().onEvent(event);
        gui.close((Player) event.getPlayer());

    }

    @EventHandler void onOpen(InventoryOpenEvent event) {
        BetterGUI gui = getBetterGUI(event.getView().getTitle());
        if(gui==null)return;
        if(!isBetterGUIAvailable(gui))return;
        if(gui.getOpen()!=null) gui.getOpen().onEvent(event);
    }

    @EventHandler void onDrag(InventoryDragEvent event) {
        BetterGUI gui = getBetterGUI(event.getView().getTitle(), event.getInventory());
        if(gui==null)return;
        if(!isBetterGUIAvailable(gui))return;
        Player p = (Player) event.getWhoClicked();
        for(Integer i : event.getRawSlots()) {
            if(i > event.getView().getTopInventory().getSize()) {
                if(tookTop.contains(p))
                    if(!gui.canTake()) {
                        event.setCancelled(true);
                        break;
                    }
            }
            if(i < event.getView().getTopInventory().getSize()) {
                if(tookBottom.contains(p))
                    if(!gui.canAdd()) {
                        event.setCancelled(true);
                        break;
                    }
                if(gui.isCancelled()) event.setCancelled(true);
                if(!gui.canDrag()) event.setCancelled(true);
                if(gui.getDrag()!=null) gui.getDrag().onEvent(event);
                if(event.isCancelled() || gui.getDrag()!=null) break;
            }
        }
        if(!event.isCancelled())
            if(event.getCursor() == null || isAir(event.getCursor().getType())) {
                tookTop.remove(p);
                tookBottom.remove(p);
            }
        if(event.isCancelled()) {
            if(gui.canPlaySound())
                p.playSound(p.getLocation(), gui.getSound(), 1, 1);
            p.updateInventory();
        }
    }


    public static boolean isAir(Material m) {
        return m.name().endsWith("AIR") && !m.name().endsWith("AIRS");
    }

    public static void disableGUI(BetterGUI gui) {
        ignored.add(gui);
    }

    public static void enableGUI(BetterGUI gui) {
        ignored.remove(gui);
    }

    public static boolean addBetterGUI(BetterGUI gui) {
        if(!guis.contains(gui)) {
            guis.add(gui);
            return true;
        }
        return false;
    }

    public static boolean removeBetterGUI(BetterGUI gui) {
        if(guis.contains(gui)) {
            guis.remove(gui);
            return true;
        }
        return false;
    }

    public static boolean isBetterGUIAvailable(BetterGUI gui) {
        if(ignored.contains(gui)) return false;
        return guis.contains(gui);
    }

    public static BetterGUI getBetterGUI(String name) {
        for(BetterGUI gui : guis) {
            if(gui.getName().equalsIgnoreCase(name)
                    ||ChatColor.stripColor(gui.getName()).equalsIgnoreCase(name)
                    ||ChatColor.stripColor(gui.getName()).equalsIgnoreCase(ChatColor.stripColor(name))
                    ||gui.getName().equalsIgnoreCase(ChatColor.stripColor(name))
                    ||getName(gui.getInventory()).equalsIgnoreCase(name)
                    ||ChatColor.stripColor(getName(gui.getInventory())).equalsIgnoreCase(name)
                    ||ChatColor.stripColor(getName(gui.getInventory())).equalsIgnoreCase(ChatColor.stripColor(name))
                    ||getName(gui.getInventory()).equalsIgnoreCase(ChatColor.stripColor(name))) return gui;
        }
        for(BetterListGUI gui : listGuis) {
            if(gui.getName().equalsIgnoreCase(name)
                    ||ChatColor.stripColor(gui.getName()).equalsIgnoreCase(name)
                    ||ChatColor.stripColor(gui.getName()).equalsIgnoreCase(ChatColor.stripColor(name))
                    ||gui.getName().equalsIgnoreCase(ChatColor.stripColor(name))
                    ||getName(gui.getInventory()).equalsIgnoreCase(name)
                    ||ChatColor.stripColor(getName(gui.getInventory())).equalsIgnoreCase(name)
                    ||ChatColor.stripColor(getName(gui.getInventory())).equalsIgnoreCase(ChatColor.stripColor(name))
                    ||getName(gui.getInventory()).equalsIgnoreCase(ChatColor.stripColor(name))) return gui;
        }
        return null;
    }

    public static BetterGUI getBetterGUI(String name, ItemStack[] contents) {
        for(BetterGUI gui : guis) {
            if((gui.getName().equalsIgnoreCase(name) && compare(contents, gui.getInventory().getContents()))
                    ||(ChatColor.stripColor(gui.getName()).equalsIgnoreCase(name) && compare(contents, gui.getInventory().getContents()))
                    ||(ChatColor.stripColor(gui.getName()).equalsIgnoreCase(ChatColor.stripColor(name)) && compare(contents, gui.getInventory().getContents()))
                    ||(gui.getName().equalsIgnoreCase(ChatColor.stripColor(name)) && compare(contents, gui.getInventory().getContents()))
                    ||getName(gui.getInventory()).equalsIgnoreCase(name) && compare(contents, gui.getInventory().getContents())
                    ||(ChatColor.stripColor(getName(gui.getInventory())).equalsIgnoreCase(name) && compare(contents, gui.getInventory().getContents()))
                    ||(ChatColor.stripColor(getName(gui.getInventory())).equalsIgnoreCase(ChatColor.stripColor(name)) && compare(contents, gui.getInventory().getContents()))
                    ||(getName(gui.getInventory()).equalsIgnoreCase(ChatColor.stripColor(name)) && compare(contents, gui.getInventory().getContents()))) return gui;
        }
        for(BetterListGUI gui : listGuis) {
            if((gui.getName().equalsIgnoreCase(name) && compare(contents, gui.getInventory().getContents()))
                    || (ChatColor.stripColor(gui.getName()).equalsIgnoreCase(name) && compare(contents, gui.getInventory().getContents()))
                    || (ChatColor.stripColor(gui.getName()).equalsIgnoreCase(ChatColor.stripColor(name)) && compare(contents, gui.getInventory().getContents()))
                    || (gui.getName().equalsIgnoreCase(ChatColor.stripColor(name)) && compare(contents, gui.getInventory().getContents()))
                    || getName(gui.getInventory()).equalsIgnoreCase(name) && compare(contents, gui.getInventory().getContents())
                    || (ChatColor.stripColor(getName(gui.getInventory())).equalsIgnoreCase(name) && compare(contents, gui.getInventory().getContents()))
                    || (ChatColor.stripColor(getName(gui.getInventory())).equalsIgnoreCase(ChatColor.stripColor(name)) && compare(contents, gui.getInventory().getContents()))
                    || (getName(gui.getInventory()).equalsIgnoreCase(ChatColor.stripColor(name)) && compare(contents, gui.getInventory().getContents())))
                return gui;
        }
        return null;
    }

    public static BetterGUI getBetterGUI(String name, Inventory inventory) {
        for(BetterGUI gui : guis) {
            if((gui.getName().equalsIgnoreCase(name) && inventory == gui.getInventory())
                    ||(ChatColor.stripColor(gui.getName()).equalsIgnoreCase(name) && inventory == gui.getInventory())
                    ||(ChatColor.stripColor(gui.getName()).equalsIgnoreCase(ChatColor.stripColor(name)) && inventory == gui.getInventory())
                    ||(gui.getName().equalsIgnoreCase(ChatColor.stripColor(name)) && inventory == gui.getInventory())
                    ||(getName(gui.getInventory()).equalsIgnoreCase(name) && inventory == gui.getInventory())
                    ||(ChatColor.stripColor(getName(gui.getInventory())).equalsIgnoreCase(name) && inventory == gui.getInventory())
                    ||(ChatColor.stripColor(getName(gui.getInventory())).equalsIgnoreCase(ChatColor.stripColor(name)) && inventory == gui.getInventory())
                    ||(getName(gui.getInventory()).equalsIgnoreCase(ChatColor.stripColor(name)) && inventory == gui.getInventory())) return gui;
        }
        for(BetterListGUI gui : listGuis) {
            if((gui.getName().equalsIgnoreCase(name) && inventory == gui.getInventory())
                    ||(ChatColor.stripColor(gui.getName()).equalsIgnoreCase(name) && inventory == gui.getInventory())
                    ||(ChatColor.stripColor(gui.getName()).equalsIgnoreCase(ChatColor.stripColor(name)) && inventory == gui.getInventory())
                    ||(gui.getName().equalsIgnoreCase(ChatColor.stripColor(name)) && inventory == gui.getInventory())
                    ||(getName(gui.getInventory()).equalsIgnoreCase(name) && inventory == gui.getInventory())
                    ||(ChatColor.stripColor(getName(gui.getInventory())).equalsIgnoreCase(name) && inventory == gui.getInventory())
                    ||(ChatColor.stripColor(getName(gui.getInventory())).equalsIgnoreCase(ChatColor.stripColor(name)) && inventory == gui.getInventory())
                    ||(getName(gui.getInventory()).equalsIgnoreCase(ChatColor.stripColor(name)) && inventory == gui.getInventory())) return gui;
        }
        return null;
    }



    public static BetterListGUI getBetterListGUI(String name) {
        for(BetterListGUI gui : listGuis) {
            if(gui.getName().equalsIgnoreCase(name)
                    ||ChatColor.stripColor(gui.getName()).equalsIgnoreCase(name)
                    ||ChatColor.stripColor(gui.getName()).equalsIgnoreCase(ChatColor.stripColor(name))
                    ||gui.getName().equalsIgnoreCase(ChatColor.stripColor(name))
                    ||getName(gui.getInventory()).equalsIgnoreCase(name)
                    ||ChatColor.stripColor(getName(gui.getInventory())).equalsIgnoreCase(name)
                    ||ChatColor.stripColor(getName(gui.getInventory())).equalsIgnoreCase(ChatColor.stripColor(name))
                    ||getName(gui.getInventory()).equalsIgnoreCase(ChatColor.stripColor(name))) return gui;
        }
        return null;
    }

    public static BetterListGUI getBetterListGUI(String name, ItemStack[] contents) {
        for(BetterListGUI gui : listGuis) {
            if((gui.getName().equalsIgnoreCase(name) && compare(contents, gui.getInventory().getContents()))
                    || (ChatColor.stripColor(gui.getName()).equalsIgnoreCase(name) && compare(contents, gui.getInventory().getContents()))
                    || (ChatColor.stripColor(gui.getName()).equalsIgnoreCase(ChatColor.stripColor(name)) && compare(contents, gui.getInventory().getContents()))
                    || (gui.getName().equalsIgnoreCase(ChatColor.stripColor(name)) && compare(contents, gui.getInventory().getContents()))
                    || getName(gui.getInventory()).equalsIgnoreCase(name) && compare(contents, gui.getInventory().getContents())
                    || (ChatColor.stripColor(getName(gui.getInventory())).equalsIgnoreCase(name) && compare(contents, gui.getInventory().getContents()))
                    || (ChatColor.stripColor(getName(gui.getInventory())).equalsIgnoreCase(ChatColor.stripColor(name)) && compare(contents, gui.getInventory().getContents()))
                    || (getName(gui.getInventory()).equalsIgnoreCase(ChatColor.stripColor(name)) && compare(contents, gui.getInventory().getContents())))
                return gui;
        }
        return null;
    }

    public static BetterListGUI getBetterListGUI(String name, Inventory inventory) {
        for(BetterListGUI gui : listGuis) {
            if((gui.getName().equalsIgnoreCase(name) && inventory == gui.getInventory())
                    ||(ChatColor.stripColor(gui.getName()).equalsIgnoreCase(name) && inventory == gui.getInventory())
                    ||(ChatColor.stripColor(gui.getName()).equalsIgnoreCase(ChatColor.stripColor(name)) && inventory == gui.getInventory())
                    ||(gui.getName().equalsIgnoreCase(ChatColor.stripColor(name)) && inventory == gui.getInventory())
                    ||(getName(gui.getInventory()).equalsIgnoreCase(name) && inventory == gui.getInventory())
                    ||(ChatColor.stripColor(getName(gui.getInventory())).equalsIgnoreCase(name) && inventory == gui.getInventory())
                    ||(ChatColor.stripColor(getName(gui.getInventory())).equalsIgnoreCase(ChatColor.stripColor(name)) && inventory == gui.getInventory())
                    ||(getName(gui.getInventory()).equalsIgnoreCase(ChatColor.stripColor(name)) && inventory == gui.getInventory())) return gui;
        }
        return null;
    }

    private static boolean compare(ItemStack[] contents1, ItemStack[] contents2) {
        if(contents1.length!= contents2.length)return false;
        for(int i = 0; i < contents1.length; i++) {
            if(contents1[i]==null&&contents2[i]!=null) return false;
            if(contents1[i]!=null&&contents2[i]==null) return false;
            if(contents1[i]==null&&contents2[i]==null) continue;
            if(!contents1[i].isSimilar(contents2[i]))return false;
        }
        return true;
    }

    private static int getOutOfPlace(ItemStack[] contents1, ItemStack[] contents2) {
        if(contents1.length!= contents2.length)return -1;
        for(int i = 0; i < contents1.length; i++) {
            if(contents1[i]==null&&contents2[i]!=null) return i;
            if(contents1[i]!=null&&contents2[i]==null) return i;
            if(contents1[i]==null&&contents2[i]==null) continue;
            if(!contents1[i].isSimilar(contents2[i])) return i;
        }
        return -1;
    }

    //Here's how to get an InventoryName in Bukkit/Spigot 1.13+
    private static String getName(Inventory inv) {
        try {//First check if it is 1.13+
            Method m = inv.getClass().getMethod("getName");
            m.setAccessible(true);
            return (String) m.invoke(inv);
        } catch (Exception ignored) {
            try {//Still checking if it is 1.13+
                Method m = inv.getClass().getMethod("getTitle");
                m.setAccessible(true);
                return (String) m.invoke(inv);
            } catch (Exception ignored1) {
                try {//Get the version
                    String pack = Bukkit.getServer().getClass().getPackage().getName();
                    String version = pack.substring(pack.lastIndexOf('.') + 1);
                    //get the Bukkit Inventory Classes +/ MinecraftInventory
                    Class<?> cic = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftInventoryCustom");
                    Class<?> ci = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftInventory");
                    Class<?> mci = cic.getDeclaredClasses()[0];
                    String title = "";
                    if(inv.getClass().isAssignableFrom(cic)) {//Check if it's a custom inventory
                        Method m = ci.getMethod("getInventory");
                        m.setAccessible(true);
                        Object mcii = mci.cast(m.invoke(ci.cast(inv)));
                        //get the IInventory and cast to MinecraftInventory
                        try {
                            m = mci.getMethod("getTitle");
                            m.setAccessible(true);
                            //Get the Title Method and set accessable
                            title = (String) m.invoke(mcii);//INVOKE!
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }//One last check and return the title else return default name
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
