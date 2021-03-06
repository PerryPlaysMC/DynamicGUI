package perryplaysmc.dynamicgui.guis;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;
import perryplaysmc.dynamicgui.item.utils.ItemUtility;
import perryplaysmc.dynamicgui.utils.DynamicLogger;
import perryplaysmc.dynamicgui.utils.options.GUIFlag;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/

@SuppressWarnings("unused")
public class DynamicGUIManager implements Listener {

    private static DynamicGUIManager instance;
    private static boolean ENABLED = false;
    
    private static Set<DynamicGUI> GUIS, IGNORED, ALL;
    private static Set<DynamicListGUI> LIST_GUIS;
    private final List<Player> tookTop = new ArrayList<>(), tookBottom = new ArrayList<>();

    public static boolean warn() {
        if(!ENABLED) {
            DynamicLogger.warn("DynamicGUI's have not been enabled!",
                    "Please do &fDynamicGUIManager.enable(this);", "at the top of your onEnable method",
                    "And &fDynamicGUIManager.disable();", "at the top of your onDisable method");
            return true;
        }
        return false;
    }

    protected static void registerGUI(DynamicGUI gui) {
        if(ENABLED) {
            GUIS.add(gui);
            ALL.add(gui);
        } else warn();
    }

    public static void enable(Plugin plugin) {
        if(!ENABLED) {
            new ItemUtility();
            ALL = new HashSet<>();
            GUIS = new HashSet<>();
            IGNORED = new HashSet<>();
            LIST_GUIS = new HashSet<>();
            instance = new DynamicGUIManager();
            Bukkit.getPluginManager().registerEvents(instance, plugin);
            ENABLED = true;
        }
    }

    public static void disable() {
        if(ENABLED) {
            for(DynamicGUI gui : getDynamicGUIS()) for(Player view : gui.getViewers())view.closeInventory();
            ALL.clear();
            GUIS.clear();
            IGNORED.clear();
            LIST_GUIS.clear();
            HandlerList.unregisterAll(instance);
            ENABLED = false;
        }
    }
    
    public static Set<DynamicGUI> getAllDynamicGUIS() {
        return ALL;
    }

    public static DynamicGUI getOrCreate(String name, int size) {
        Optional<DynamicGUI> gui = getDynamicGUI(null, name);
        if(!gui.isPresent()) return new DynamicGUI(name, size);
        return gui.get().setNew(false);
    }

    public static Set<DynamicGUI> getDynamicGUIS() {
        return GUIS;
    }

    public static Set<DynamicListGUI> getDynamicListGUIS() {
        return LIST_GUIS;
    }

    public static DynamicListGUI getOrCreateList(DynamicGUI main, String name, int size) {
        Optional<DynamicListGUI> gui = getDynamicListGUI(name);
        if(!gui.isPresent()) return new DynamicListGUI(main, name, size);
        DynamicListGUI dg = gui.get();
        dg.setNew(false);
        return dg;
    }

    public static DynamicListGUI createList(DynamicGUI main, String name, int size) {
        return new DynamicListGUI(main, name, size);
    }

    public static DynamicGUI create(String name, int size) {
        return new DynamicGUI(name, size);
    }

    private void addTop(Player p) {
        if(warn()) return;
        tookTop.add(p);
        tookBottom.remove(p);
    }

    private void addBottom(Player p) {
        if(warn()) return;
        tookBottom.add(p);
        tookTop.remove(p);
    }

    @EventHandler void onClick(InventoryClickEvent event) {
        if(!ENABLED) return;
        if(!(event.getInventory().getHolder() instanceof DynamicGUI)) return;
        DynamicGUI gui = (DynamicGUI) event.getInventory().getHolder();
        if(!isDynamicGUIAvailable(gui)) return;
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
        if(!gui.hasFlag(GUIFlag.ALLOW_TAKE) && !gui.isCancelled()) {
            if(isTop) {
                if(tookTop.contains(p) && !itemExists) tookTop.remove(p);
                if(!event.isCancelled() && itemExists) addTop(p);
                if(isShift) event.setCancelled(true);
                if(event.getClick() == ClickType.DROP) event.setCancelled(true);
                if(event.getClick() == ClickType.CONTROL_DROP) event.setCancelled(true);
            }
            if(tookTop.contains(p) && isBottom) event.setCancelled(true);
        }
        if(!gui.hasFlag(GUIFlag.ALLOW_ADD) && !gui.isCancelled()) {
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
                    if(!gui.hasFlag(GUIFlag.ALLOW_ADD)) event.setCancelled(true);
                    else if(!tookTop.contains(p)) addTop(p);
                }
                if(isBottom && top.contains(item.getType())) {
                    if(!gui.hasFlag(GUIFlag.ALLOW_TAKE)) event.setCancelled(true);
                    else if(!tookBottom.contains(p)) addBottom(p);
                }
            }
        }
        if(tookTop.contains(p) && !gui.hasFlag(GUIFlag.ALLOW_TAKE) && !isTop && !isBottom && !gui.isCancelled()) event.setCancelled(true);
        if(tookTop.contains(p) && isBottom && !gui.hasFlag(GUIFlag.ALLOW_TAKE) && !gui.isCancelled()) event.setCancelled(true);
        if(tookBottom.contains(p) && isTop && !gui.hasFlag(GUIFlag.ALLOW_ADD) && !gui.isCancelled()) event.setCancelled(true);
        if(isTop) {
            ItemStack before = event.getCurrentItem();
            if(gui.isDisabled(event.getSlot())) event.setCancelled(true);
            if(gui.isFiller(event.getSlot())) event.setCancelled(true);
            if(gui.getAction(event.getSlot()) != null)
                gui.getAction(event.getSlot()).onEvent(gui, event.getCurrentItem(), event);
            else if(gui.getClick() != null) gui.getClick().onEvent(event);
            if(gui.hasSound(event.getSlot()))
                p.playSound(p.getLocation(), gui.getSound(event.getSlot()), 1, 1);
            else if(gui.canPlaySound())
                p.playSound(p.getLocation(), gui.getSound(), 0.2f, 1);
            if(before!=event.getCurrentItem())
                gui.setItem(event.getSlot(), event.getCurrentItem());
        }
        if(event.isCancelled()) p.updateInventory();
    }

    @EventHandler void onClose(InventoryCloseEvent event) {
        if(!ENABLED) return;
        if(!(event.getInventory().getHolder() instanceof DynamicGUI)) return;
        DynamicGUI gui = (DynamicGUI) event.getInventory().getHolder();
        if(!isDynamicGUIAvailable(gui))return;
        Player p = (Player) event.getPlayer();
        tookBottom.remove(p);
        tookTop.remove(p);
        if(gui.getClose()!=null) gui.getClose().onEvent(event);
        gui.close((Player) event.getPlayer());

    }

    @EventHandler void onOpen(InventoryOpenEvent event) {
        if(!ENABLED) return;
        if(!(event.getInventory().getHolder() instanceof DynamicGUI)) return;
        DynamicGUI gui = (DynamicGUI) event.getInventory().getHolder();
        if(!isDynamicGUIAvailable(gui))return;
        if(gui.getOpen()!=null) gui.getOpen().onEvent(event);
    }

    @EventHandler void onDrag(InventoryDragEvent event) {
        if(!ENABLED) return;
        if(!(event.getInventory().getHolder() instanceof DynamicGUI)) return;
        DynamicGUI gui = (DynamicGUI) event.getInventory().getHolder();
        if(!isDynamicGUIAvailable(gui))return;
        Player p = (Player) event.getWhoClicked();
        for(Integer i : event.getRawSlots()) {
            if(i > event.getView().getTopInventory().getSize()) {
                if(tookTop.contains(p)&&!gui.hasFlag(GUIFlag.ALLOW_TAKE)) {
                    event.setCancelled(true);
                    break;
                }
            }
            if(i < event.getView().getTopInventory().getSize()) {
                if(tookBottom.contains(p)&&!gui.hasFlag(GUIFlag.ALLOW_ADD)) {
                    event.setCancelled(true);
                    break;
                }
                if(gui.isCancelled()) event.setCancelled(true);
                if(!gui.hasFlag(GUIFlag.ALLOW_DRAG)) event.setCancelled(true);
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
            for(int slot : event.getRawSlots())
                if(gui.hasSound(slot))
                    p.playSound(p.getLocation(), gui.getSound(slot), 1, 1);
            if(gui.canPlaySound())
                p.playSound(p.getLocation(), gui.getSound(), 1, 1);
            p.updateInventory();
        }
    }


    public static boolean isAir(Material m) {
        return m.name().endsWith("AIR") && !m.name().endsWith("AIRS");
    }

    public static void disableGUI(DynamicGUI gui) {
        if(warn()) return;
        IGNORED.add(gui);
    }

    public static void enableGUI(DynamicGUI gui) {
        if(warn()) return;
        IGNORED.remove(gui);
    }

    public static boolean addDynamicGUI(DynamicGUI gui) {
        if(warn()) return false;
        if(!GUIS.contains(gui)) {
            GUIS.add(gui);
            ALL.add(gui);
            return true;
        }
        return false;
    }

    public static boolean removeDynamicGUI(DynamicGUI gui) {
        if(warn()) return false;
        if(GUIS.contains(gui)) {
            GUIS.remove(gui);
            ALL.remove(gui);
            return true;
        }
        return false;
    }

    public static boolean isDynamicGUIAvailable(DynamicGUI gui) {
        if(warn()) return false;
        if(IGNORED.contains(gui)) return false;
        return GUIS.contains(gui);
    }

    public static Optional<DynamicGUI> getDynamicGUI(@Nullable Player player, String name) {
        if(warn()) return Optional.empty();
        return ALL.stream().filter(gui->checkGUI(player, name, gui)).findFirst();
    }
    public static Optional<DynamicGUI> getDynamicGUI(@Nullable Player player, String name, ItemStack[] contents) {
        if(warn()) return Optional.empty();
        return ALL.stream().filter(gui->checkGUI(player, name, contents, gui)).findFirst();
    }


    public static Optional<DynamicGUI> getDynamicGUI(@Nullable Player player, String name, Inventory inventory) {
       return getDynamicGUI(player,name,inventory.getContents());
    }
    public static Optional<DynamicListGUI> getDynamicListGUI(String name) {
        if(warn()) return Optional.empty();
        return LIST_GUIS.stream().filter(gui->gui.getName().equalsIgnoreCase(name)).findFirst();
    }

    public static Optional<DynamicListGUI> getDynamicListGUI(String name, ItemStack[] contents) {
        if(warn()) return Optional.empty();
        return LIST_GUIS.stream().filter(gui->gui.getName().equalsIgnoreCase(name)).filter(gui -> compare(contents, gui.getInventory().getContents())).findFirst();
    }

    public static Optional<DynamicListGUI> getDynamicListGUI(String name, Inventory inventory) {
        if(warn()) return Optional.empty();
        return LIST_GUIS.stream().filter(gui->gui.getName().equalsIgnoreCase(name)).filter(gui->inventory==gui.getInventory()).findFirst();
    }


    private static boolean checkGUI(@Nullable Player player, String name, DynamicGUI gui) {
        if(gui.getName().equalsIgnoreCase(name))
            return player == null || gui.getViewers().contains(player);
        return false;
    }


    private static boolean checkGUI(@Nullable Player player, String name, ItemStack[] contents, DynamicGUI gui) {
        if(name.equalsIgnoreCase(gui.getName()) && compare(contents, gui.getInventory().getContents()))
            return player == null || gui.getViewers().contains(player);
        return false;
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

}
