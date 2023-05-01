package com.splerix.boxgen.Util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;


public class BoxInfoReader {
    private final Plugin plugin;

    public BoxInfoReader(Plugin plugin) {
        this.plugin = plugin;
    }
    public List<Box> getBoxList() {
        //Gets a list of all the enabled boxes and their variables
        List<Box> boxList = new ArrayList<>();

        //Loops through all the boxes in the name list then accesses their info
        for (String n : plugin.getConfig().getStringList("boxes.box_name_list")) {
            //Makes sure box is enabled
            String name = n.toLowerCase();
            if (!getBoolean("boxes." + name + ".enabled")) continue;
            World world;
            Vector lowLoc;
            Vector highLoc;
            Vector safetyTPLoc;
            Map<Material, Integer> blocks = new HashMap<>();
            int regenTime = 30;
            String regenMessage = null;
            List<String> commandList = new ArrayList<>();
            //Easier to read
            String currentSection = "";

            try {
                currentSection = "boxes." + name + ".world";
                world = plugin.getServer().getWorld(getString(currentSection));

                currentSection = "boxes." + name + ".low_point";
                lowLoc = getPoint(currentSection);

                currentSection = "boxes." + name + ".high_point";
                highLoc = getPoint(currentSection);

                currentSection = "boxes." + name + ".safety_teleport";
                safetyTPLoc = getPoint(currentSection);

                currentSection = "boxes." + name + ".block_types";
                try {
                    //Method because it's huge
                    blocks = getBlockMap(currentSection);
                } catch (Exception e) {
                    plugin.getLogger().log(Level.SEVERE, "Problem with getting blocks at " + currentSection + " check and " +
                            "make sure you spelled blocks names right, etc", e);
                    return null;
                }

                currentSection = "boxes." + name + ".regen_method";
                String[] regenMethod = getString(currentSection).split(" ");
                //Switch in case I add more later methods later
                switch (regenMethod[0]) {
                    case "command":
                        regenTime = -1;
                        break;
                    case "timer":
                        int time = Integer.parseInt(regenMethod[1]);
                        if (!(time > 0)) {
                            plugin.getLogger().log(Level.SEVERE, "In " + currentSection + " for timer a wrong value has been " +
                                    "entered. Make sure the value is a positive number");
                            plugin.getLogger().log(Level.SEVERE, "Defaulting to 30 minutes in the timer section for the box " +  name);
                            regenTime = 30;
                            break;
                        }
                        regenTime = time;
                }

                currentSection = "boxes." + name + ".regen_message";
                try {
                    regenMessage = getString(currentSection);
                } catch (Exception e) {
                    //Leave empty in case they don't want any regen message
                }

                currentSection = "boxes." + name + ".regen_commands";
                commandList = plugin.getConfig().getStringList(currentSection);


            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Problem with getting values from the section: " + currentSection,e);
                return null;
            }
            boxList.add(new Box(name,world,lowLoc,highLoc,
                    new Location(world,safetyTPLoc.getX(),safetyTPLoc.getY(),safetyTPLoc.getZ())
                    ,regenTime,blocks, regenMessage, commandList));
        }
        return boxList;
    }

    private Map<Material, Integer> getBlockMap(String path) {

        Map<Material, Integer> blocks = new HashMap<>();

        //If the total percentage goes above 100 it becomes invalid and splits them evenly
        int totalPercentage = 0;
        ArrayList<Material> blockList = new ArrayList<>();
        ArrayList<Integer> percentageList = new ArrayList<>();

        //Loops through all the blocks and percentages
        for (String s : getString(path).split(" ")) {
            //Separates the blocks from percentage and adds them to the lists
            String[] blockInfo = s.split("%");
            int percentage = Integer.parseInt(blockInfo[0]);
            percentageList.add(percentage);
            totalPercentage = totalPercentage + percentage;

            blockList.add(Material.valueOf(blockInfo[1]));
        }
        //Check percentage is right
        if (totalPercentage != 100) {
            plugin.getLogger().log(Level.INFO, "Problem with percentages at " + path +
                    " defaulting to evenly splitting up blocks");
            plugin.getLogger().log(Level.INFO, "Make sure percentages add up to 100");

            int defaultPercentage = 100 / blockList.size();

            //Adds the block list blocks to the block hashmap
            for (int i = 0; i < blockList.size(); i++) {
                if (i == blockList.size() - 1) {
                    //Fills the remainder
                    defaultPercentage = defaultPercentage + (100 % defaultPercentage);
                    blocks.put(blockList.get(i), defaultPercentage);
                    continue;
                }
                blocks.put(blockList.get(i), defaultPercentage);
            }
            return blocks;
        }

        //If percentage is right transfers block list and percentage list into block hashmap
        for (int i = 0; i < blockList.size(); i++) blocks.put(blockList.get(i), percentageList.get(i));
        return blocks;
    }
    private Vector getPoint(String path) {
        //Returns a location/the x,y,z from the config
        double x;
        double y;
        double z;
        String s = plugin.getConfig().getString(path);
        String[] values =s.split(",");
        try {
            x = Double.parseDouble(values[0]);
            y = Double.parseDouble(values[1]);
            z = Double.parseDouble(values[2]);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Problem with getting cord at " + path);
            return null;
        }
        return new Vector(x,y,z);
    }
    private String getString(String path) {
        //Returns a string from the config
        return plugin.getConfig().getString(path);
    }
    private Boolean getBoolean(String path) {
        //Returns a boolean from the config
        try {
            return plugin.getConfig().getBoolean(path);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Problem with getting boolean at " + path);
            return null;
        }
    }
}
