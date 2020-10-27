package dev.perryplaysmc.betterguis;

import dev.perryplaysmc.betterguis.guis.BetterGUI;
import dev.perryplaysmc.betterguis.guis.BetterGuiManager;
import dev.perryplaysmc.betterguis.guis.item.ItemBuilder;
import dev.perryplaysmc.betterguis.guis.item.XMaterial;
import dev.perryplaysmc.betterguis.guis.utils.filler.FillerType;
import dev.perryplaysmc.betterguis.guis.utils.guitypes.BetterListGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Copy Right ©
 * This code is private
 * Owner: PerryPlaysMC
 * From: 10/2020-Now
 * <p>
 * Any attempts to use these program(s) may result in a penalty of up to $1,000 USD
 **/
@SuppressWarnings("all")
public class PluginMain extends JavaPlugin {
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new BetterGuiManager(), this);
    }

    @Override
    public void onDisable() {
        for(BetterGUI gui : BetterGuiManager.getBetterGUIS()) for(Player view : gui.getViewers())view.closeInventory();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        BetterGUI gui = BetterGuiManager.getOrCreate("Test", 9);
        if(gui.isNew())
            gui.onClick((e) -> {
                if(e.getSlot()==0) {
                    BetterListGUI list = BetterGuiManager.createList(gui, "&m                  &r&c{page}/{pages}&r&m                ", 36);
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
                    list.playSound()
                            .addFiller(FillerType.BOTTOM_ROW, new ItemBuilder(XMaterial.LIME_STAINED_GLASS_PANE).name(""))
                            .allowAdd()
                            .allowDrag()
                            .denyTake()
                            .removeOnClose()
                            .cancel()
                            .onClick((e1) -> {
                                if(e1.getCurrentItem()!=null)
                                    e1.getWhoClicked().getInventory().addItem(e1.getCurrentItem());
                            });
                    list.show((Player) e.getWhoClicked());
                }
            });

        gui.show((Player) sender);
        return true;
    }
}
