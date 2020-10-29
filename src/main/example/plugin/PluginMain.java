package example.plugin;

import betterguis.guis.BetterGUI;
import betterguis.guis.BetterGUIManager;
import betterguis.guis.item.ItemBuilder;
import betterguis.guis.item.XMaterial;
import betterguis.guis.utils.filler.FillerType;
import betterguis.guis.utils.guitypes.BetterListGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

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
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new BetterGUIManager(), this);
    }
    @Override
    public void onDisable() {
        for(BetterGUI gui : BetterGUIManager.getBetterGUIS()) for(Player view : gui.getViewers())view.closeInventory();
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p =(Player) sender;
        BetterGUI gui = BetterGUIManager.getOrCreate("Example", 54);
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
                        BetterListGUI list = BetterGUIManager.createList(gui, "&m                  &r&c{page}/{pages}&r&m                ", 36);
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
                        list.playSound()
                                .addFiller(FillerType.BOTTOM_ROW, new ItemBuilder(XMaterial.LIME_STAINED_GLASS_PANE).name(""))
                                .allowAdd()
                                .allowDrag()
                                .denyTake()
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
