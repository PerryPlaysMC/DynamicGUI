package perryplaysmc.dynamicgui.guis;

import org.bukkit.event.inventory.*;
import perryplaysmc.dynamicgui.utils.Version;
import perryplaysmc.dynamicgui.utils.options.FillerType;
import perryplaysmc.dynamicgui.item.ItemBuilder;
import perryplaysmc.dynamicgui.item.XSound;
import perryplaysmc.dynamicgui.utils.TitleChanger;
import perryplaysmc.dynamicgui.utils.options.GUIFlag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Copy Right ©
 * This code is private
 * Owner: PerryPlaysMC
 * From: 10/2020-Now
 * <p>
 * Any attempts to use these program(s) may result in a penalty of up to $1,000 USD
 **/
@SuppressWarnings("all")
public class DynamicGUI {

    protected String name = "";
    private Set<Player> viewers = new HashSet<>();
    protected HashMap<Integer, ItemStack> items = new HashMap<>();
    protected HashMap<Integer, ItemBuilder> fillers = new HashMap<>();
    protected List<Integer> wasAdded = new ArrayList<>();
    private HashMap<Integer, Sound> slotSound = new HashMap<>();
    private HashMap<Integer, ClickAction> actions = new HashMap<>();
    private Set<Integer> disabledSlots = new HashSet<>();
    private Set<GUIFlag> flags = new HashSet<>();
    private int size;
    private boolean cancel = false;
    private boolean isNew = false;
    private boolean playSound = false;
    private boolean removeOnClose = false;
    private boolean isDynamic = true;
    private Sound sound = XSound.UI_BUTTON_CLICK.parseSound();
    protected Inventory pane;

    private ClickEvent click;
    private CloseEvent close;
    private OpenEvent open;
    private DragEvent drag;

    protected DynamicGUI(String name, int size) {
        while(DynamicGUIManager.getDynamicGUI(null, name).isPresent()) name = "§x" + name;
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        this.size = getInventorySize(size);
        if(size > -1) {
            if(size == 5)
                this.pane = Bukkit.createInventory(null, InventoryType.HOPPER, this.name);
            else if(size == 8)
                this.pane = Bukkit.createInventory(null, InventoryType.DISPENSER, this.name);
            else this.pane = Bukkit.createInventory(null, size, this.name);
            isDynamic = false;
        }
        this.items = new HashMap<>(54);
        this.isNew = true;
        DynamicGUIManager.addDynamicGUI(this);
    }

    /**
     Add a click action to a specified slot
     @param slot
     @param action
     */
    public DynamicGUI addAction(int slot, ClickAction action) {
        actions.put(slot, action);
        return this;
    }

    /**
     Check if the inventory has a specific material
     @param item
     */
    public boolean contains(Material mat) {
        for(ItemStack item : items.values())
            if(item.getType()==mat)return true;
        if(pane != null)
            return pane.contains(mat);
        return false;
    }

    /**
     Check if the inventory has a specific item
     @param item
     */
    public boolean contains(ItemStack item) {
        for(ItemStack itemS : items.values())
            if(itemS.isSimilar(item)) return true;
        if(pane != null)
            return pane.contains(item);
        return false;
    }

    /**
     Adds a filler type (Empty spaces) with a random replacement
     @param type
     @param replace[]
     */
    public DynamicGUI addFiller(FillerType type, ItemBuilder... replace) {
        int loopSize = size;
        int start = 0;
        switch(type) {
            case TOP:
                loopSize = size / 3;
                break;
            case MIDDLE:
                loopSize = (size / 3) * 2;
                start = size / 3;
                break;
            case BOTTOM:
                start = (size / 3) * 2;
                break;
            case ROW_1: case TOP_ROW:
                if(size / 9 >= 1) {
                    loopSize = 9;
                    start = 0;
                } else {
                    start = size - 9;
                }
                break;
            case ROW_2:
                if(size / 9 >= 2) {
                    loopSize = 18;
                    start = 9;
                } else {
                    start = size - 9;
                }
                break;
            case ROW_3:
                if(size / 9 >= 3) {
                    loopSize = 27;
                    start = 18;
                } else {
                    start = size - 9;
                }
                break;
            case ROW_4:
                if(size / 9 >= 4) {
                    loopSize = 36;
                    start = 27;
                } else {
                    start = size - 9;
                }
                break;
            case ROW_5:
                if(size / 9 >= 5) {
                    loopSize = 45;
                    start = 36;
                } else {
                    start = size - 9;
                }
                break;
            case ROW_6:
                if(size / 9 == 6) {
                    start = size - 9;
                } else {
                    start = size - 9;
                }
                break;
            case BOTTOM_ROW:
                start = size - 9;
                break;
        }
        for(int i = start; i < loopSize; i++) {
            int slot = i;
            List<ItemStack> hold = new ArrayList<>();
            List<Integer> skip = new ArrayList<>();
            while(items.containsKey(slot) && slot < size) {
                hold.add(items.get(slot));
                if(!wasAdded.contains(slot)) skip.add(slot);
                slot++;
            }
            slot--;
            for(int x = slot; x > i; x--) {
                if(skip.contains(x)) continue;
                if(type == FillerType.BORDER && (x < 9 || x > size - 9 || x % 9 == 0 || x % 9 == 8)) continue;
                items.put(x, hold.get(x - i));
                if(pane != null)
                    pane.setItem(x, hold.get(x - i));
            }
            ItemStack item = pane != null ? getItem(i) : items.get(i);
            if(type == FillerType.BORDER) {
                if(ItemBuilder.isAir(item) && (i < 9 || i > size - 9 || i % 9 == 0 || i % 9 == 8))
                    fillers.put(i, replace[new Random().nextInt(replace.length)]);
            }else {
                if(ItemBuilder.isAir(item)) {
                    fillers.put(i, replace[new Random().nextInt(replace.length)]);
                }
            }
        }
        return this;
    }

    /**
     * Removes the provided item from the gui
     * @param item
     * @return
     */
    public DynamicGUI removeItem(ItemStack item) {
        ItemStack itemClone = item.clone();
        itemClone.setAmount(1);
        int amt = item.getAmount();
        List<Integer> removeSlots = new ArrayList<>();
        for(Integer slot : items.keySet()) {
            if(amt == 0||slot==null) continue;
            ItemStack slotItem = items.get(slot);
            if(slotItem==null)continue;
            ItemStack slotClone = slotItem.clone();
            slotClone.setAmount(1);
            if(slotClone.isSimilar(itemClone)) {
                if((slotItem.getAmount() - amt) > 0) {
                    slotItem.setAmount(slotItem.getAmount() - amt);
                    amt = 0;
                    items.put(slot, slotItem);
                    if(pane != null)
                        pane.setItem(slot, slotItem);
                    break;
                }
                if((slotItem.getAmount() - amt) <= 0) {
                    amt-=slotItem.getAmount();
                    removeSlots.add(slot);
                    if(amt == 0) {
                        items.remove(slot);
                        if(pane != null)
                            pane.setItem(slot, null);
                        break;
                    }
                }
            }
        }
        if(removeSlots.size()>0 && amt == 0)
            removeSlots.forEach(s -> {
                items.remove(s);
                if(pane != null)
                    pane.setItem(s, null);
            });
        return this;
    }

    /**
     * Removes the specified amount of the provided material from the gui
     * @param item
     * @param amount
     * @return
     */
    public DynamicGUI removeItem(Material item, int amount) {
        int amt = amount;
        List<Integer> removeSlots = new ArrayList<>();
        for(Integer slot : items.keySet()) {
            if(amt == 0) continue;
            ItemStack slotItem = items.get(slot);
            if(slotItem.getType() == item) {
                if((slotItem.getAmount() - amt) > 0) {
                    slotItem.setAmount(slotItem.getAmount() - amt);
                    amt = 0;
                    items.put(slot, slotItem);
                    if(pane != null)
                        pane.setItem(slot, slotItem);
                    break;
                }
                if((slotItem.getAmount() - amt) <= 0) {
                    amt-=slotItem.getAmount();
                    removeSlots.add(slot);
                    if(amt == 0) {
                        items.remove(slot);
                        if(pane != null)
                            pane.setItem(slot, null);
                        break;
                    }
                }
            }
        }
        if(removeSlots.size()>0 && amt == 0)
            removeSlots.forEach(s -> {
                items.remove(s);
                if(pane != null)
                    pane.setItem(s, null);
            });
        return this;
    }


    /**
     * Removes the provided material from the gui
     * @param item
     * @param amount
     * @return
     */
    public DynamicGUI removeItem(Material item) {
        for(Integer slot : items.keySet()) {
            ItemStack slotItem = items.get(slot);
            if(slotItem.getType() == item) {
                items.remove(slot);
                if(pane != null)
                    pane.setItem(slot, null);
            }
        }
        return this;
    }


    /**
     Sets an item in the specified slot
     @param slot
     @param item
     */
    public DynamicGUI setItem(int slot, ItemStack item) {
        if(slot > (size-1))return this;
        items.put(slot, item);
        if(pane != null)
            pane.setItem(slot, item);
        return this;
    }

    /**
     Sets an item in the specified slot
     @param slot
     @param item
     */
    public DynamicGUI setItem(int row, int slot, ItemStack item) {
        return setItem((row*9)+slot, item);
    }

    /**
     Sets an item in the specified slot
     @param slot
     @param item
     */
    public DynamicGUI setItem(int slot, ItemBuilder item) {
        return setItem(slot, item.getItem());
    }

    /**
     Sets an item in the specified slot
     @param slot
     @param item
     */
    public DynamicGUI setItem(int row, int slot, ItemBuilder item) {
        return setItem((row*9)+slot, item.getItem());
    }



    /**
     Adds the provided items to the gui with overflow
     @param items
     @return HashMap<Integer, ItemStack>
     */
    public HashMap<Integer, ItemStack> addItemLeftover(ItemStack... items) {
        int slot = 0;
        HashMap<Integer, ItemStack> overflow = new HashMap<>();
        A:for(ItemStack item : items) {
            if(Version.isCurrentHigher(Version.v1_11_R1))
                if(!item.getType().isItem())continue;
            ItemStack itemClone = item.clone();
            itemClone.setAmount(1);
            while(this.items.containsKey(slot) || this.fillers.containsKey(slot)) slot++;

            if(item.getAmount() <= 0) continue;
            if(slot > (size-1)) {
                if(pane != null)
                    overflow.putAll(pane.addItem(item));
                continue;
            }
            B:for(int newSlot : this.items.keySet()) {
                ItemStack slotItem = this.items.get(newSlot);
                ItemStack slotClone = slotItem.clone();
                slotClone.setAmount(1);
                if(slotClone.isSimilar(itemClone)) {
                    if(slotItem.getAmount() == slotItem.getMaxStackSize()) continue B;
                    slotItem.setAmount(slotItem.getAmount() + item.getAmount());
                    if(slotItem.getAmount() > slotItem.getMaxStackSize()) {
                        ItemStack newItem = slotItem.clone();
                        newItem.setAmount(slotItem.getAmount() - slotItem.getMaxStackSize());
                        item.setAmount(slotItem.getMaxStackSize());
                        this.items.put(newSlot, slotItem);
                        if(newItem.getAmount() > 0)
                            addItem(newItem);
                        if(pane != null)
                            pane.setItem(newSlot, slotItem);
                        continue A;
                    }else {
                        this.items.put(newSlot, slotItem);
                        if(pane != null)
                            pane.setItem(newSlot, slotItem);
                        continue A;
                    }
                }
            }
            if(item.getAmount() > item.getMaxStackSize()) {
                ItemStack newItem = item.clone();
                newItem.setAmount(item.getAmount() - item.getMaxStackSize());
                item.setAmount(item.getMaxStackSize());
                addItem(newItem);
            }
            this.items.put(slot, item);
            wasAdded.add(slot);
            if(pane != null)
                pane.setItem(slot, item);
            slot++;
        }
        return overflow;
    }


    /**
     Adds the provided items to the gui
     @param items
     */
    public DynamicGUI addItem(ItemStack... items) {
        addItemLeftover(items);
        return this;
    }



    /**
     Adds the provided items to the gui and returns the overflow
     @param items
     */
    public HashMap<Integer, ItemStack> addItemLeftover(ItemBuilder... items) {
        HashMap<Integer, ItemStack> overflow = new HashMap<>();
        for(ItemBuilder item : items)
            overflow.putAll(addItemLeftover(item.getItem()));
        return overflow;
    }

    /**
     Adds the provided items to the gui
     @param items
     */
    public DynamicGUI addItem(ItemBuilder... items) {
        addItemLeftover(items);
        return this;
    }

    /**
     Adds an item to the gui
     @param item
     */
    public DynamicGUI addItem(ItemBuilder item) {
        addItem(new ItemBuilder[]{item});
        return this;
    }

    public DynamicGUI setName(String newName) {
        if(name.equalsIgnoreCase(translate(newName)))return this;
        this.name = translate(newName);
        Set<Player> old = new HashSet<>(getViewers());
        for(Player p : old) TitleChanger.update(p, name);
        return this;
    }
    /**
     * Clears the slot
     */
    public DynamicGUI clear(int slot) {
        if(pane != null)
            pane.setItem(slot, null);
        items.remove(slot);
        return this;
    }

    /**
     * Clears the inventory
     * @return
     */
    public DynamicGUI clear() {
        for(Integer slot : items.keySet())
            if(pane != null)
                pane.setItem(slot, null);
        items.clear();
        return this;
    }

    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    /**
     Disable a slot
     when they click it cancels it
     @param slot
     */
    public DynamicGUI disableSlot(int slot) {
        disabledSlots.add(slot);
        return this;
    }


    /**
     Enable a disabled slot
     see disableSlot
     @param slot
     */
    public DynamicGUI enableSlot(int slot) {
        disabledSlots.remove(slot);
        return this;
    }


    /**
     * Toggle a slot
     * @param slot
     */
    public DynamicGUI toggleSlot(int slot) {
        if(disabledSlots.contains(slot)) disabledSlots.remove(slot);
        else disabledSlots.add(slot);
        return this;
    }

    public String getName() {
        return name;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public DynamicGUI cancel() {
        cancel = true;
        return this;
    }

    /**
     * Add a flag to the gui
     * @param flags
     * @return
     */
    public DynamicGUI addFlag(GUIFlag... flags) {
        this.flags.addAll(Arrays.asList(flags));
        return this;
    }
    /**
     * Remove flags from the gui
     * @param flags
     * @return
     */
    public DynamicGUI removeFlag(GUIFlag... flags) {
        this.flags.removeAll(Arrays.asList(flags));
        return this;
    }

    /**
     * Deletes the gui when it's closed
     */
    public DynamicGUI removeOnClose() {
        removeOnClose = true;
        return this;
    }

    /**
     * Enables playing of sounds when slot clicked
     */
    public DynamicGUI playSound() {
        playSound = true;
        return this;
    }
    /**
     * Disables playing of sounds when slot clicked
     */
    public DynamicGUI stopSound() {
        playSound = false;
        return this;
    }
    /**
     * Toggles playing of sounds when slot clicked
     */
    public DynamicGUI toggleSound() {
        playSound = !playSound;
        return this;
    }

    /**
     * Sets the sound that plays when slot clicked
     * @param clickSound
     */
    public DynamicGUI setSound(Sound clickSound) {
        this.sound = clickSound;
        return this;
    }

    /**
     * Sets the sound that plays when slot clicked
     * @param clickSound
     */
    public DynamicGUI setSound(XSound clickSound) {
        this.sound = clickSound.parseSound();
        return this;
    }

    /**
     * Sets the sound that plays when a specific slot clicked
     * @param clickSound
     */
    public DynamicGUI setSound(int slot, Sound clickSound) {
        if(clickSound==null) {
            slotSound.remove(slot);
            return this;
        }
        this.slotSound.put(slot, clickSound);
        return this;
    }

    /**
     * Sets the sound that plays when a specific slot clicked
     * @param clickSound
     */
    public DynamicGUI setSound(int slot, XSound clickSound) {
        if(clickSound==null) {
            slotSound.remove(slot);
            return this;
        }
        this.slotSound.put(slot, clickSound.parseSound());
        return this;
    }

    /**
     * Gets the sound that plays when a specific slot clicked
     * @param clickSound
     */
    public Sound getSound(int slot) {
        if(!slotSound.containsKey(slot)) return null;
        return this.slotSound.get(slot);
    }

    /**
     * Checks if the sound for the slot exists
     * @param clickSound
     */
    public boolean hasSound(int slot) {
        return this.slotSound.containsKey(slot);
    }

    /**
     * Check if gui has a flag set
     * @param flag
     * @return
     */
    public boolean hasFlag(GUIFlag flag){
        return flags.contains(flag);
    }
    /**
     * Does the sound play when a slot is clicked
     */
    public boolean canPlaySound() {
        return playSound;
    }

    public Sound getSound() {
        return sound;
    }

    public ClickEvent getClick() {
        return click;
    }
    public CloseEvent getClose() {
        return close;
    }
    public OpenEvent getOpen() {
        return open;
    }
    public DragEvent getDrag() {
        return drag;
    }

    public ClickAction getAction(int slot) {
        return actions.containsKey(slot) ? actions.get(slot) : null;
    }

    public DynamicGUI onClick(ClickEvent click) {
        this.click = click;
        return this;
    }
    public DynamicGUI onClose(CloseEvent close) {
        this.close = close;
        return this;
    }
    public DynamicGUI onOpen(OpenEvent open) {
        this.open = open;
        return this;
    }
    public DynamicGUI onDrag(DragEvent drag) {
        this.drag = drag;
        return this;
    }

    public DynamicGUI disable() {
        DynamicGUIManager.disableGUI(this);
        return this;
    }

    public DynamicGUI enable() {
        DynamicGUIManager.enableGUI(this);
        return this;
    }

    public boolean isDisabled(int slot) {
        return disabledSlots.contains(slot);
    }

    public boolean isFiller(int slot) {
        return fillers.containsKey(slot);
    }

    public void show(Player player) {
        if(pane == null) {
            size = genInventorySize(items.size());
            this.pane = Bukkit.createInventory(null, size, this.name);
            for(Integer slot : new HashSet<>(items.keySet())) {
                pane.setItem(slot, items.get(slot));
            }
        }
        for(Integer slot : new HashMap<>(fillers).keySet()) {
            if(items.containsKey(slot)) {
                fillers.remove(slot);
                continue;
            }
            if(!ItemBuilder.isAir(pane.getItem(slot))) {
                fillers.remove(slot);
                continue;
            }
            pane.setItem(slot, fillers.get(slot).getItem());
        }
        viewers.add(player);
        player.openInventory(pane);
    }

    protected int genInventorySize(int size) {
        return size <= 9 ? 9 : size <= 18 ? 18 : size <= 27 ? 27 : size <= 36 ? 36 : size <= 45 ? 45 : 54;
    }

    public int getSize() {
        return size;
    }

    public int getActualSize() {
        return getSize() - fillers.size();
    }

    public Inventory getInventory() {
        return pane;
    }

    public boolean isNew() {
        return isNew;
    }

    protected DynamicGUI setNew(boolean isNew) {
        this.isNew = isNew;
        return this;
    }


    public void close(Player player) {
        viewers.remove(player);
        if(removeOnClose)
            DynamicGUIManager.removeDynamicGUI(this);
    }

    public Set<Player> getViewers() {
        return viewers;
    }


    public interface ClickAction {
        void onEvent(DynamicGUI inv, ItemStack item, InventoryClickEvent e);
    }

    public interface ClickEvent {
        void onEvent(InventoryClickEvent e);
    }

    public interface CloseEvent {
        void onEvent(InventoryCloseEvent e);
    }

    public interface OpenEvent {
        void onEvent(InventoryOpenEvent e);
    }

    public interface DragEvent {
        void onEvent(InventoryDragEvent e);
    }


    private int getInventorySize(int size) {
        return size <= 5 ? 5
                : size <= 9 ? 9
                : size <= 18 ? 18
                : size <= 27 ? 27
                : size <= 36 ? 36
                : size <= 45 ? 45
                : 54;
    }

    private String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
