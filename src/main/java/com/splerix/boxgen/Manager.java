package com.splerix.boxgen;

import com.splerix.boxgen.Util.Box;
import com.splerix.boxgen.Util.BoxInfoReader;
import com.splerix.boxgen.Util.DataManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.logging.Level;

public class Manager implements Listener {
    private final BoxInfoReader configReader;
    private final Plugin plugin;
    public final DataManager data;

    private List<Box> boxList;

    public Manager(Plugin plugin) {
        this.plugin = plugin;
        configReader = new BoxInfoReader(plugin);
        boxList = configReader.getBoxList();
        data = new DataManager(plugin);
        startBoxLoop();
    }
    public void reloadConfig() {
        boxList = configReader.getBoxList();
        plugin.getLogger().log(Level.INFO, "The BoxGen config has been reloaded");
    }

    public List<Box> getBoxList() {
        return boxList;
    }
    private void startBoxLoop() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            //To reduce the overall amount of times the plugin gets players locations
            //For example if multiple boxes restart at the same time it will only need to get the locations once
            List<Player> playerList = new ArrayList<>();
            @Override
            public void run() {
                if (boxList == null) return;
                for (Box box : boxList){
                    //If it's a command only then skip
                    if (box.regenTime == -1) continue;
                    String name = box.name;
                    String path = "boxes." + name + ".minutes_until_gen";
                    if (data.getConfig().contains(path)) {
                        //Gets the amount of time before the gen gets reset and removes a minute
                        int timeLeft = data.getConfig().getInt(path) - 1;
                        //If it has more time than keep going
                        if (timeLeft > 0) {
                            data.getConfig().set(path, timeLeft);
                            continue;
                        }

                        if (playerList.isEmpty())
                            for (Player p : Bukkit.getOnlinePlayers())
                                playerList.add(p);


                        //Regen the box and reset the time
                        generateGen(box,playerList);
                    }
                }
                data.saveConfig();
                playerList.clear();
            }
        },1,60*20);
    }

    public void generateGen(Box box, List<Player> playerList) {
        //Teleport players out of box first
        if (!playerList.isEmpty())
            for (Player p : playerList) {
                if (!box.inBox(p.getLocation())) continue;
                Location loc = box.safetyTeleportLoc;
                loc.setDirection(p.getLocation().getDirection());
                loc.setPitch(p.getLocation().getPitch());
                loc.setYaw(p.getLocation().getYaw());
                p.teleport(loc);
                p.sendMessage(ChatColor.GREEN + "The box you were in has reset");
            }

        //Transferring box variables to local for performance
        Map<Material, Integer> blocks = box.blocks;

        World world = box.world;
        Vector low = box.lowLoc;
        Vector high = box.highLoc;

        //Random Material Variables
        Random random = new Random();
        List<Integer> percentages = new ArrayList<>();
        int r;
        int totalPercentage;
        int chosenIndex = 0;

        //Make a list with all the percentages
        for (Material mat : blocks.keySet())
            percentages.add(blocks.get(mat));

        for (int i = low.getBlockX(); i<high.getBlockX()+1; i++){
            for (int j = low.getBlockY(); j<high.getBlockY(); j++)
                for (int k = low.getBlockZ()-1; k<high.getBlockZ();k++) {
                    totalPercentage = 0;
                    r = random.nextInt(100)+1;
                    //Get the random material
                    for (int l = 0; l < percentages.size();l++) {
                        totalPercentage = percentages.get(l)+totalPercentage;
                        if (totalPercentage>=r) {
                            chosenIndex = l;
                            break;
                        }
                    }
                    //Set the block to a random material
                    world.getBlockAt(new Location(world, i,j,k)).setType((Material) blocks.keySet().toArray()[chosenIndex]);
                }

        }


        //Reset the time if it is a timer based
        data.getConfig().set("boxes." + box.name + ".minutes_until_gen", box.regenTime);

        //Send the regen message
        if (box.regenMessage != null)
            for (Player player : Bukkit.getOnlinePlayers())
                player.sendMessage(box.regenMessage);

        //Iterate through the commands
        if (!box.commandList.isEmpty())
            for (int i = 0; i<box.commandList.size(); i++)
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),box.commandList.get(i));
    }
}
