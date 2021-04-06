package perryplaysmc.dynamicgui.guis;

import dev.perryplaysmc.dynamicguiplugin.PluginMain;
import perryplaysmc.dynamicgui.item.ItemBuilder;
import perryplaysmc.dynamicgui.item.XMaterial;
import perryplaysmc.dynamicgui.utils.Version;
import perryplaysmc.dynamicgui.utils.options.FillerType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Copy Right ©
 * This code is private
 * Owner: PerryPlaysMC
 * From: 10/2020-Now
 **/
@SuppressWarnings("all")
public class DynamicListGUI extends DynamicGUI {

    private DynamicGUI mainPage;
    private DynamicListGUI mainListPage;
    private HashMap<Integer, Set<Integer>> wasAdded = new HashMap<>();
    private JavaPlugin main = JavaPlugin.getPlugin(PluginMain.class);
    private HashMap<Integer, ItemStack> permSlot = new HashMap<>();
    private TurnPage previous, next;
    private ClickEvent onClick;
    private int currentPage = 1;
    private int removeDelay = 0;
    private HashMap<Integer, HashMap<Integer, ItemStack>> pages = new HashMap<>();

    public DynamicListGUI(String name, int size) {
        super(name, size);
        this.pane = Bukkit.createInventory(this, size, getName().replace("{page}", currentPage+"").replace("{pages}", getPages()+""));
        this.previous = new TurnPage(getSize() - 9, new ItemBuilder(XMaterial.RED_WOOL).name("§cPrevious page").setLore("&cPrevious: {previous}")
                .replaceInLore("{previous}", (currentPage-1) <= 0 ? "Main" : (currentPage-1)));
        this.next = new TurnPage(getSize() - 1, new ItemBuilder(XMaterial.LIME_WOOL).name("§aNext page").setLore("&aNext: {next}")
                .replaceInLore("{next}", (currentPage+1) > getPages() ? "You're on the last page!" : (currentPage+1)));
        if(currentPage < getPages())
            pane.setItem(next.slot, next.item.getItem());
        items = new HashMap<>();
        pages.put(1, new HashMap<>());
    }


    public DynamicListGUI(DynamicGUI mainPage, String name, int size) {
        this(name, size);
        if(mainPage!=null)
            if(mainPage instanceof DynamicListGUI)
                this.mainListPage = (DynamicListGUI) mainPage;
            else
                this.mainPage = mainPage;
        if(mainPage!=null)
            getInventory().setItem(previous.slot, previous.item.getItem());
    }

    /**
     Set the delay between page switches in ticks
     @param removeDelay
     */
    public DynamicListGUI setRemoveDelay(int removeDelay) {
        this.removeDelay = removeDelay;
        return this;
    }

    @Override
    public DynamicListGUI addFiller(FillerType type, ItemBuilder... replace) {
        int size = getSize();
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
            if(wasSet(i)) continue;
            ItemBuilder rand = replace[new Random().nextInt(replace.length)];
            if(type == FillerType.BORDER) {
                if((i < 9 || i > size - 9 || i % 9 == 0 || i % 9 == 8)) {
                    fillers.put(i, rand);
                    pane.setItem(i, rand.getItem());
                }
            }else {
                fillers.put(i, rand);
                pane.setItem(i, rand.getItem());
            }
        }
        List<ItemStack> map = new ArrayList<>();
        int page = 1;
        for(HashMap<Integer, ItemStack> maps : new HashMap<>(pages).values()) {
            new HashMap<>(maps).forEach((slot, item) -> {
                if(wasAdded(slot)) {
                    map.add(item);
                    maps.remove(slot);
                }
            });
            pages.put(page, maps);
            page++;
        }
        wasAdded.clear();
        for(ItemStack item : map)
            addItem(item);
        return this;
    }

    /**
     Check if slot has been set
     @param slot
     */
    private boolean wasSet(int slot) {
        if(permSlot.containsKey(slot))return true;
        for(HashMap<Integer, ItemStack> map : pages.values())
            if(map.containsKey(slot) && !wasAdded(slot))return true;
        return false;
    }

    /**
     Check if slot has been added
     @param slot
     */
    private boolean wasAdded(int slot) {
        for(Set<Integer> slot1 : wasAdded.values()) {
            if(slot1.contains(slot))return true;
        }
        return false;
    }


    @Override
    public DynamicListGUI setItem(int slot, ItemStack item) { return setItem(slot, new ItemBuilder(item)); }

    @Override
    public DynamicListGUI setItem(int slot, ItemBuilder item) {
        items = pages.get(1);
        if(slot > (getSize()-1) || getPages() > 0) {
            int i = 1;
            if(slot > (getSize()-1))
                while(slot> getSize()-1) {
                    if(!pages.containsKey(i))
                        pages.put(i, new HashMap<>());
                    slot-=getSize();
                    i++;
                }
            for(int index : pages.keySet())
                if(pages.get(index).size() < getActualSize()) {
                    i = index;
                    break;
                }
            while(fillers.containsKey(slot)) slot++;
            if(slot > getSize()) while(fillers.containsKey(slot)) slot--;
            if(!pages.containsKey(i-1))pages.put(i-1, new HashMap<>());
            pages.get(i-1).put(slot, item.getItem());
            return this;
        }
        items.put(slot, item.getItem());
        if(items.size() == getActualSize()) pages.put(1, items);
        getInventory().setItem(slot, item.getItem());
        return this;
    }

    public DynamicListGUI setItem(int page, int slot, ItemBuilder item) {
        items = pages.get(page);
        if(slot > (getSize()-1) || getPages() > 0) {
            int i = page;
            if(slot > (getSize()-1))
                while(slot> getSize()-1) {
                    if(!pages.containsKey(i))
                        pages.put(i, new HashMap<>());
                    slot-=getSize();
                    i++;
                }
            for(int index : pages.keySet())
                if(pages.get(index).size() < getActualSize()) {
                    i = index;
                    break;
                }
            while(fillers.containsKey(slot)) {
                slot++;
            }
            if(slot > getSize()) {
                while(fillers.containsKey(slot))
                    slot--;
            }
            pages.get(i-1).put(slot, item.getItem());
            return this;
        }
        items.put(slot, item.getItem());
        if(items.size() == getActualSize()) pages.put(1, items);
        getInventory().setItem(slot, item.getItem());
        return this;
    }

    @Override
    public HashMap<Integer, ItemStack> addItemLeftover(ItemStack... items) {
        addItem(items);
        return new HashMap<>();
    }

    @Override
    public DynamicListGUI addItem(ItemStack... itemstacks) {
        int slot = 0;
        int page = 1;
        items = pages.get(page);
        for(ItemStack item : itemstacks) {
            if(Version.isCurrentHigher(Version.v1_11_R1))
                if(!item.getType().isItem())continue;
            while(items.containsKey(slot) || fillers.containsKey(slot) || permSlot.containsKey(slot)) {
                slot++;
                if(slot == getSize()) {
                    slot = 0;
                    page++;
                    if(!pages.containsKey(page))
                        pages.put(page, new HashMap<>());
                    items = pages.get(page);
                }
            }
            Set<Integer> slots = wasAdded.getOrDefault(page, new HashSet<>());
            slots.add(slot);
            wasAdded.put(page, slots);
            items.put(slot, item);
            if(page == currentPage)
                getInventory().setItem(slot, item);
        }
        new HashMap<>(pages).forEach((pageN, map)-> {
            if(map.isEmpty())pages.remove(pageN);
        });
        if(!pages.containsKey(page))
            pages.put(page, items);
        return this;
    }

    @Override
    public HashMap<Integer, ItemStack> addItemLeftover(ItemBuilder... items) {
        for(ItemBuilder item : items)
            addItem(item.getItem());
        return new HashMap<>();
    }
    @Override
    public DynamicListGUI addItem(ItemBuilder... items) {
        addItemLeftover(items);
        return this;
    }

    @Override
    public DynamicListGUI addItem(ItemBuilder item) {
        addItem(new ItemBuilder[]{item});
        return this;
    }

    public DynamicListGUI addPermanentItem(int slot, ItemBuilder item) {
        permSlot.put(slot, item.getItem());
        getInventory().setItem(slot, item.getItem());
        return this;
    }


    /**
     * Clears the slot
     */
    public DynamicGUI clear(int slot) {
        pane.setItem(slot, null);
        items.remove(slot);
        return this;
    }

    /**
     * Clears the inventory
     * @return
     */
    public DynamicGUI clear() {
        for(Integer slot : pages.get(currentPage).keySet())
            pane.setItem(slot, null);
        pages.clear();
        items.clear();
        return this;
    }

    @Override
    public ItemStack getItem(int slot) {
        return pages.get(currentPage).get(slot);
    }

    public ItemStack getItem(int page, int slot) {
        return pages.get(page).get(slot);
    }

    /**
     * Removes the provided item from the gui
     * @param item
     * @return
     */
    public DynamicListGUI removeItem(ItemStack item) {
        ItemStack itemClone = item.clone();
        itemClone.setAmount(1);
        int amt = item.getAmount();
        HashMap<Integer, List<Integer>> toRemove = new HashMap<>();
        for(int page : pages.keySet()) {
            HashMap<Integer, ItemStack> items = pages.get(page);
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
                        if(page==currentPage);
                        pane.setItem(slot, slotItem);
                        break;
                    }
                    if((slotItem.getAmount() - amt) <= 0) {
                        amt-=slotItem.getAmount();
                        removeSlots.add(slot);
                        if(amt == 0) {
                            items.remove(slot);
                            if(page==currentPage);
                            pane.setItem(slot, null);
                            break;
                        }
                    }
                }
            }
            pages.put(page, items);
            if(removeSlots.size()>0)toRemove.put(page, removeSlots);
        }
        if(toRemove.size()>0 && amt == 0)
            toRemove.forEach((page, list) -> {
                list.forEach(s->{
                    items.remove(s);
                    if(page == currentPage)
                        pane.setItem(s, null);
                });
            });
        return this;
    }

    /**
     * Removes the specified amount of the provided material from the gui
     * @param item
     * @param amount
     * @return
     */
    public DynamicListGUI removeItem(Material item, int amount) {
        int amt = amount;
        HashMap<Integer, List<Integer>> toRemove = new HashMap<>();
        for(int page : pages.keySet()) {
            HashMap<Integer, ItemStack> items = pages.get(page);
            List<Integer> removeSlots = new ArrayList<>();
            for(Integer slot : items.keySet()) {
                if(amt == 0) continue;
                ItemStack slotItem = items.get(slot);
                if(slotItem.getType() == item) {
                    if((slotItem.getAmount() - amt) > 0) {
                        slotItem.setAmount(slotItem.getAmount() - amt);
                        amt = 0;
                        items.put(slot, slotItem);
                        if(page == currentPage)
                            pane.setItem(slot, slotItem);
                        break;
                    }
                    if((slotItem.getAmount() - amt) <= 0) {
                        amt -= slotItem.getAmount();
                        removeSlots.add(slot);
                        if(amt == 0) {
                            items.remove(slot);
                            if(page == currentPage)
                                pane.setItem(slot, null);
                            break;
                        }
                    }
                }
            }
            pages.put(page, items);
            if(removeSlots.size()>0)toRemove.put(page, removeSlots);
        }
        if(toRemove.size()>0 && amt == 0)
            toRemove.forEach((page, list) -> {
                list.forEach(s->{
                    items.remove(s);
                    if(page == currentPage)
                        pane.setItem(s, null);
                });
            });
        return this;
    }

    /**
     * Removes the provided material from the gui
     * @param item
     * @param amount
     * @return
     */
    public DynamicListGUI removeItem(Material item) {
        for(int page : pages.keySet()) {
            HashMap<Integer, ItemStack> items = pages.get(page);
            for(Integer slot : items.keySet()) {
                ItemStack slotItem = items.get(slot);
                if(slotItem.getType() == item) {
                    items.remove(slot);
                    if(page == currentPage)
                        pane.setItem(slot, null);
                }
            }
            pages.put(page, items);
        }
        return this;
    }


    public DynamicListGUI setNextPageItem(int slot, ItemBuilder item)  {
        if(item == null) return this;
        next = new TurnPage(slot, item.replaceInLore("{next}", (currentPage+1) > getPages() ? "You're on the last page!" : (currentPage+1)));
        return this;
    }

    public DynamicListGUI setPreviousPageItem(int slot, ItemBuilder item)  {
        if(item == null) return this;
        previous = new TurnPage(slot, item.replaceInLore("{previous}", (currentPage-1) <= 0 ? "Main" : (currentPage-1)));
        return this;
    }

    public ItemBuilder getNextPageItem()  {
        return next.item.replaceInLore("{next}", (currentPage+1) > getPages() ? "You're on the last page!" : (currentPage+1));
    }

    public ItemBuilder getPreviousPageItem()  {
        return previous.item.replaceInLore("{previous}", (currentPage-1) <= 0 ? "Main" : (currentPage-1));
    }


    public int getPages() {
        return pages.size();
    }

    public int getCurrentPage() {
        return currentPage;
    }


    @Override
    public DynamicListGUI onClick(ClickEvent click) {
        this.onClick = click;
        return this;
    }

    public void show(Player player, int page) {
        this.currentPage = page > getPages() ? getPages() : (page < 1 ? 1 : page);
        int slot = 0;
        for(ItemStack item : pages.get(currentPage).values()) {
            while(fillers.containsKey(slot))slot++;
            getInventory().setItem(slot, item);
            slot++;
        }
        show(player);
    }

    @Override
    public void show(Player player) {
        getViewers().add(player);
        if(currentPage < getPages())
            getInventory().setItem(next.slot, getNextPageItem().getItem());
        if(mainPage!=null || currentPage > 1)
            getInventory().setItem(previous.slot, getPreviousPageItem().getItem());
        if(super.getClick() ==null) {
            super.onClick((event)-> {
                if(event.getSlot() == previous.slot && (event.getCurrentItem()!=null&&event.getCurrentItem().isSimilar(previous.item.getItem()))) {
                    if(currentPage == 1) {
                        if(mainPage!=null)
                            mainPage.show((Player) event.getWhoClicked());
                        if(mainListPage!=null)
                            mainListPage.show((Player) event.getWhoClicked());
                        return;
                    }
                    if((currentPage-1) > 0) currentPage--;
                    if(currentPage == 1) pane.setItem(next.slot, getPreviousPageItem().getItem());
                    if(getName().contains("{page}")||getName().contains("{pages}")) {
                        String old = getName();
                        setName(getName().replace("{page}", currentPage + "").replace("{pages}", getPages() + ""));
                        this.name = old;
                    }
                    fillers.forEach((slot, item)->{
                        pane.setItem(slot, item.getItem());
                    });
                    final int[] delay = {0};
                    for(Integer slot : pages.get(currentPage+1).keySet()) {
                        if(this.removeDelay == 0)
                            pane.setItem(slot, null);
                        else
                            (new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if(slot < 0) return;
                                    pane.setItem(slot, null);
                                }
                            }).runTaskLaterAsynchronously(main, delay[0]+=this.removeDelay);
                    }
                    delay[0] = this.removeDelay;
                    pages.get(currentPage).forEach((slot, item) -> {
                        if(this.removeDelay == 0)
                            pane.setItem(slot, item);
                        else
                            (new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if(slot < 0) return;
                                    pane.setItem(slot, item);
                                }
                            }).runTaskLaterAsynchronously(main, delay[0]+=this.removeDelay);

                    });
                    if(currentPage <= 1 && (mainPage == null && mainListPage == null))
                        if(fillers.containsKey(previous.slot)) pane.setItem(previous.slot, fillers.get(previous.slot).getItem());
                    if(currentPage > 1 || (mainPage != null || mainListPage != null))
                        pane.setItem(previous.slot, getPreviousPageItem().getItem());
                    pane.setItem(next.slot, getNextPageItem().getItem());
                    pane.setItem(next.slot, getNextPageItem().getItem());
                    event.setCancelled(true);
                    return;
                }
                if(event.getSlot() == next.slot && (event.getCurrentItem()!=null&&event.getCurrentItem().isSimilar(next.item.getItem()))) {
                    if((currentPage+1) <= getPages()) currentPage++;
                    if(currentPage > 1) pane.setItem(previous.slot, getPreviousPageItem().getItem());
                    if(getName().contains("{page}")||getName().contains("{pages}")) {
                        String old = getName();
                        setName(getName().replace("{page}", currentPage + "").replace("{pages}", getPages() + ""));
                        this.name = old;
                    }
                    fillers.forEach((slot, item)->{
                        pane.setItem(slot, item.getItem());
                    });
                    final int[] delay = {0};
                    for(Integer slot : pages.get(currentPage-1).keySet()) {
                        if(this.removeDelay == 0)
                            pane.setItem(slot, null);
                        else
                            (new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if(slot > getSize()) return;
                                    pane.setItem(slot, null);
                                }
                            }).runTaskLaterAsynchronously(main, delay[0]+=this.removeDelay);
                    }
                    delay[0] = this.removeDelay;
                    pages.get(currentPage).forEach((slot, item) -> {
                        if(this.removeDelay == 0)
                            pane.setItem(slot, item);
                        else
                            (new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if(slot > getSize()) return;
                                    pane.setItem(slot, item);
                                }
                            }).runTaskLaterAsynchronously(main, delay[0]+=this.removeDelay);

                    });
                    if(currentPage>=getPages())
                        if(fillers.containsKey(next.slot)) pane.setItem(next.slot, fillers.get(next.slot).getItem());
                    pane.setItem(previous.slot, getPreviousPageItem().getItem());
                    if(currentPage < getPages()) pane.setItem(next.slot, getNextPageItem().getItem());
                    event.setCancelled(true);
                    return;
                }
                if(pages.get(currentPage).containsKey(event.getSlot()) || !isFiller(event.getSlot())) {
                    if(onClick!=null)
                        onClick.onEvent(event);
                    return;
                }
            });
        }
        player.openInventory(pane);
        if(getName().contains("{page}")||getName().contains("{pages}")) {
            String old = getName();
            setName(getName().replace("{page}", currentPage + "").replace("{pages}", getPages() + ""));
            this.name = old;
        }
    }

    private class TurnPage {
        int slot;
        ItemBuilder item;
        public TurnPage(int slot, ItemBuilder item) {
            this.slot = slot;
            this.item = item;
        }
    }

}
