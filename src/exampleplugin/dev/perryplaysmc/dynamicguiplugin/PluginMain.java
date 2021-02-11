package dev.perryplaysmc.dynamicguiplugin;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import perryplaysmc.dynamicgui.guis.DynamicGUI;
import perryplaysmc.dynamicgui.guis.DynamicGUIManager;
import perryplaysmc.dynamicgui.item.ItemBuilder;
import perryplaysmc.dynamicgui.item.XMaterial;
import perryplaysmc.dynamicgui.item.utils.ItemUtility;
import perryplaysmc.dynamicgui.utils.Version;
import perryplaysmc.dynamicgui.utils.options.FillerType;
import perryplaysmc.dynamicgui.guis.DynamicListGUI;
import perryplaysmc.dynamicgui.utils.options.GUIFlag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Owner: PerryPlaysMC
 * Created: 10/2020
 **/
public class PluginMain extends JavaPlugin {


    @Override
    public void onEnable() {
        DynamicGUIManager.create("Hello", 5);
        DynamicGUIManager.enable(this);
        ItemBuilder ib = new ItemBuilder(Material.STONE);
        System.out.println(ib.getCompound().toString());
        ib.setType(Material.GOLD_INGOT);
        System.out.println(convertItemStack(ib.getItem()));
        for(Player player : Bukkit.getOnlinePlayers()) {
            System.out.println(ib.getItem().getType());
            player.getInventory().addItem(ib.getItem());
            player.spigot().sendMessage(new ComponentBuilder("Hey").event(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(
                    ib.getCompound().toString()
            ).create())).create());
        }
    }
    @Override
    public void onDisable() {
        DynamicGUIManager.disable();
    }

    public String convertItemStack(ItemStack item) {
        try {
            Class<?> nmsStackC = Class.forName(Version.getNMSPackage() + ".ItemStack");
            Class<?> cbStack = Class.forName(Version.getCBPackage() + ".inventory.CraftItemStack");
            Class<?> cmp = Class.forName(Version.getNMSPackage() + ".NBTTagCompound");
            Object nmsStack = cbStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
            boolean hasTag = ((boolean)nmsStackC.getMethod("hasTag").invoke(nmsStack));
            Object tag;
            if(hasTag) tag = nmsStackC.getMethod("getTag").invoke(nmsStack);
            else {
                tag = cmp.newInstance();
                nmsStackC.getMethod("save", cmp).invoke(nmsStack, tag);
            }
            return cmp.getMethod("toString").invoke(tag).toString();
        }catch (Exception e) {

        }
        return "";
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p =(Player) sender;
        DynamicGUI gui = DynamicGUIManager.getOrCreate("Example", 54);
        if(gui.isNew())
            gui.addItem(new ItemBuilder(Material.DIAMOND_HELMET),
                    new ItemBuilder(Material.DIAMOND_CHESTPLATE),
                    new ItemBuilder(Material.DIAMOND_LEGGINGS),
                    new ItemBuilder(Material.DIAMOND_BOOTS),
                    new ItemBuilder(Material.DIAMOND_SWORD))
                    .addItem(new ItemStack(Material.STICK, 5), new ItemStack(Material.STICK, 5))
                    .addItem(new ItemBuilder(XMaterial.LIME_WOOL).name("§aOpen list"));
        gui.cancel()
                .onClick((e -> {
                    if(e.getSlot()==6) {
                        DynamicListGUI list = DynamicGUIManager.createList(gui, "&m                  &r&c{page}/{pages}&r&m                ", 54);
                        List<String> xmat = new ArrayList<String>() {{
                            for(XMaterial mat : XMaterial.values())
                                if(!ItemBuilder.isAir(mat.parseItem()) && mat.parseMaterial().isItem())
                                    add(mat.name());
                        }};
                        Collections.sort(xmat);
                        for(String mName : xmat) {
                            XMaterial mat = XMaterial.valueOf(mName);
                            if(!ItemBuilder.isAir(mat.parseItem()) && mat.parseMaterial().isItem())
                                list.addItem(new ItemBuilder(mat.parseItem()).name("§a" + mat.name()));
                        }
                        list.setRemoveDelay(3);
                        list.addPermanentItem(5, new ItemBuilder(XMaterial.STONE).name("This is stuck here"));
                        list.playSound()
                                .addFiller(FillerType.BORDER, new ItemBuilder(XMaterial.LIME_STAINED_GLASS_PANE).name(""))
                                .addFlag(GUIFlag.ALLOW_ADD, GUIFlag.ALLOW_DRAG)
                                .removeOnClose()
                                .cancel()
                                .onClick((e1) -> {
                                    if(e1.getCurrentItem()!=null) {
                                        e1.getWhoClicked().getInventory().addItem(e1.getCurrentItem());
                                        list.removeItem(e1.getCurrentItem());
                                    }
                                });
                        list.show((Player) e.getWhoClicked());
                    }else
                    if(e.getCurrentItem()!=null) {
                        ItemStack clone = e.getCurrentItem().clone();
                        clone.setAmount(1);
                        e.getWhoClicked().getInventory().addItem(clone);
                        gui.removeItem(clone);
                    }
                }));
        gui.show(p);
        return true;
    }
}
