package betterguis.guis;

import betterguis.guis.utils.Version;
import betterguis.guis.utils.filler.FillerType;
import betterguis.guis.item.ItemBuilder;
import betterguis.guis.item.XSound;
import betterguis.guis.utils.TitleChanger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
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
public class BetterGUI {

    protected String name = "";
    private Set<Player> viewers = new HashSet<>();
    protected HashMap<Integer, ItemStack> items = new HashMap<>();
    protected HashMap<Integer, ItemBuilder> fillers = new HashMap<>();
    protected List<Integer> wasAdded = new ArrayList<>();
    private HashMap<Integer, ClickAction> actions = new HashMap<>();
    private Set<Integer> disabledSlots = new HashSet<>();
    private int size;
    private boolean canTake = false;
    private boolean canAdd = false;
    private boolean canDrag = false;
    private boolean cancel = false;
    private boolean isNew = false;
    private boolean playSound = false;
    private boolean removeOnClose = false;
    private Sound sound = XSound.UI_BUTTON_CLICK.parseSound();
    protected Inventory pane;

    private ClickEvent click;
    private CloseEvent close;
    private OpenEvent open;
    private DragEvent drag;

    protected BetterGUI(String name, int size) {
        while(BetterGUIManager.getBetterGUI(name)!=null) name = "§x" + name;
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        this.size = getInventorySize(size);
        this.pane = Bukkit.createInventory(null, size, this.name);
        this.items = new HashMap<>(size);
        this.isNew = true;
        BetterGUIManager.addBetterGUI(this);
    }

    /**
     Add a click action to a specified slot
     @param slot
     @param action
     */
    public BetterGUI addAction(int slot, ClickAction action) {
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
        return pane.contains(mat);
    }

    /**
     Check if the inventory has a specific item
     @param item
     */
    public boolean contains(ItemStack item) {
        for(ItemStack itemS : items.values())
            if(itemS.isSimilar(item)) return true;
        return pane.contains(item);
    }

    /**
     Adds a filler type (Empty spaces) with a random replacement
     @param type
     @param replace[]
     */
    public BetterGUI addFiller(FillerType type, ItemBuilder... replace) {
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
                pane.setItem(x, hold.get(x - i));
            }
            ItemStack item = pane.getItem(i);
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
    public BetterGUI removeItem(ItemStack item) {
        ItemStack itemClone = item.clone();
        itemClone.setAmount(1);
        int amt = item.getAmount();
        List<Integer> removeSlots = new ArrayList<>();
        for(Integer slot : items.keySet()) {
            if(amt == 0) continue;
            ItemStack slotItem = items.get(slot);
            ItemStack slotClone = slotItem.clone();
            slotClone.setAmount(1);
            if(slotClone.isSimilar(itemClone)) {
                if((slotItem.getAmount() - amt) > 0) {
                    slotItem.setAmount(slotItem.getAmount() - amt);
                    amt = 0;
                    items.put(slot, slotItem);
                    pane.setItem(slot, slotItem);
                    break;
                }
                if((slotItem.getAmount() - amt) <= 0) {
                    amt-=slotItem.getAmount();
                    removeSlots.add(slot);
                    if(amt == 0) {
                        items.remove(slot);
                        pane.setItem(slot, null);
                        break;
                    }
                }
            }
        }
        if(removeSlots.size()>0 && amt == 0)
            removeSlots.forEach(s -> {
                items.remove(s);
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
    public BetterGUI removeItem(Material item, int amount) {
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
                    pane.setItem(slot, slotItem);
                    break;
                }
                if((slotItem.getAmount() - amt) <= 0) {
                    amt-=slotItem.getAmount();
                    removeSlots.add(slot);
                    if(amt == 0) {
                        items.remove(slot);
                        pane.setItem(slot, null);
                        break;
                    }
                }
            }
        }
        if(removeSlots.size()>0 && amt == 0)
            removeSlots.forEach(s -> {
                items.remove(s);
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
    public BetterGUI removeItem(Material item) {
        for(Integer slot : items.keySet()) {
            ItemStack slotItem = items.get(slot);
            if(slotItem.getType() == item) {
                items.remove(slot);
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
    public BetterGUI setItem(int slot, ItemStack item) {
        if(slot > (size-1))return this;
        items.put(slot, item);
        pane.setItem(slot, item);
        return this;
    }

    /**
     Sets an item in the specified slot
     @param slot
     @param item
     */
    public BetterGUI setItem(int row, int slot, ItemStack item) {
        return setItem((row*9)+slot, item);
    }

    /**
     Sets an item in the specified slot
     @param slot
     @param item
     */
    public BetterGUI setItem(int slot, ItemBuilder item) {
        return setItem(slot, item.getItem());
    }

    /**
     Sets an item in the specified slot
     @param slot
     @param item
     */
    public BetterGUI setItem(int row, int slot, ItemBuilder item) {
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
                        pane.setItem(newSlot, slotItem);
                        continue A;
                    }else {
                        this.items.put(newSlot, slotItem);
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
            pane.setItem(slot, item);
            slot++;
        }
        return overflow;
    }


    /**
     Adds the provided items to the gui
     @param items
     */
    public BetterGUI addItem(ItemStack... items) {
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
    public BetterGUI addItem(ItemBuilder... items) {
        addItemLeftover(items);
        return this;
    }

    /**
     Adds an item to the gui
     @param item
     */
    public BetterGUI addItem(ItemBuilder item) {
        addItem(new ItemBuilder[]{item});
        return this;
    }

    public BetterGUI setName(String newName) {
        if(name.equalsIgnoreCase(translate(newName)))return this;
        this.name = translate(newName);
        Set<Player> old = new HashSet<>(getViewers());
        for(Player p : old) TitleChanger.update(p, name);
        return this;
    }

    /**
     Disable a slot
     when they click it cancels it
     @param slot
     */
    public BetterGUI disableSlot(int slot) {
        disabledSlots.add(slot);
        return this;
    }


    /**
     Enable a disabled slot
     see disableSlot
     @param slot
     */
    public BetterGUI enableSlot(int slot) {
        disabledSlots.remove(slot);
        return this;
    }


    /**
     * Toggle a slot
     * @param slot
     */
    public BetterGUI toggleSlot(int slot) {
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

    public BetterGUI cancel() {
        cancel = true;
        return this;
    }


    /**
     * Disables dragging of items
     */
    public BetterGUI denyDrag() {
        canDrag = false;
        return this;
    }
    /**
     * Enables dragging of items
     */
    public BetterGUI allowDrag() {
        canDrag = true;
        return this;
    }
    /**
     * Toggles dragging of items
     */
    public BetterGUI toggleDrag() {
        canDrag = !canDrag;
        return this;
    }

    /**
     * Disables adding of items
     */
    public BetterGUI denyAdd() {
        canAdd = false;
        return this;
    }
    /**
     * Enables adding of items
     */
    public BetterGUI allowAdd() {
        canAdd = true;
        return this;
    }
    /**
     * Toggles adding of items
     */
    public BetterGUI toggleAdd() {
        canAdd = !canAdd;
        return this;
    }

    /**
     * Disables taking of items
     */
    public BetterGUI denyTake() {
        canTake = false;
        return this;
    }
    /**
     * Enables taking of items
     */
    public BetterGUI allowTake() {
        canTake = true;
        return this;
    }
    /**
     * Toggles taking of items
     */
    public BetterGUI toggleTake() {
        canTake = !canTake;
        return this;
    }

    /**
     * Deletes the gui when it's closed
     */
    public BetterGUI removeOnClose() {
        removeOnClose = true;
        return this;
    }

    /**
     * Enables playing of sounds when slot clicked
     */
    public BetterGUI playSound() {
        playSound = true;
        return this;
    }
    /**
     * Disables playing of sounds when slot clicked
     */
    public BetterGUI stopSound() {
        playSound = false;
        return this;
    }
    /**
     * Toggles playing of sounds when slot clicked
     */
    public BetterGUI toggleSound() {
        playSound = !playSound;
        return this;
    }

    /**
     * Sets the sound that plays when slot clicked
     * @param clickSound
     */
    public BetterGUI setSound(Sound clickSound) {
        this.sound = clickSound;
        return this;
    }

    /**
     * Sets the sound that plays when slot clicked
     * @param clickSound
     */
    public BetterGUI setSound(XSound clickSound) {
        this.sound = clickSound.parseSound();
        return this;
    }

    /**
     * Can you drag in the gui
     */
    public boolean canDrag() {
        return canDrag;
    }

    /**
     * Can you take items from the gui
     */
    public boolean canTake() {
        return canTake;
    }
    /**
     * Can you add items from to gui
     */
    public boolean canAdd() {
        return canAdd;
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

    public BetterGUI onClick(ClickEvent click) {
        this.click = click;
        return this;
    }
    public BetterGUI onClose(CloseEvent close) {
        this.close = close;
        return this;
    }
    public BetterGUI onOpen(OpenEvent open) {
        this.open = open;
        return this;
    }
    public BetterGUI onDrag(DragEvent drag) {
        this.drag = drag;
        return this;
    }

    public BetterGUI disable() {
        BetterGUIManager.disableGUI(this);
        return this;
    }

    public BetterGUI enable() {
        BetterGUIManager.enableGUI(this);
        return this;
    }

    public boolean isDisabled(int slot) {
        return disabledSlots.contains(slot);
    }

    public boolean isFiller(int slot) {
        return fillers.containsKey(slot);
    }

    public void show(Player player) {
        viewers.add(player);
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
        player.openInventory(pane);
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

    protected void setNew(boolean isNew) {
        this.isNew = isNew;
    }


    public void close(Player player) {
        viewers.remove(player);
        if(removeOnClose)
            BetterGUIManager.removeBetterGUI(this);
    }

    public Set<Player> getViewers() {
        return viewers;
    }


    public interface ClickAction {
        void onEvent(BetterGUI inv, ItemStack item, InventoryClickEvent e);
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
