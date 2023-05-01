package com.splerix.boxgen.Util;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;

public class Box {
    //Box Gen Object
    public final String name;
    public final World world;
    public final Vector lowLoc;
    public final Vector highLoc;

    public final Location safetyTeleportLoc;
    
    //Material = Block used, Double = percentage chance of block
    public final Map<Material, Integer> blocks;

    //If value is set to -1 then it will be command only
    public final int regenTime;
    public final String regenMessage;
    public final List<String> commandList;

    public Box(String name, World world, Vector lowLoc, Vector highLoc, Location safetyTeleportLocation,
               int regenTime, Map<Material, Integer> blocks, String regenMessage, List<String> commandList ) {
        this.name = name;
        this.world = world;
        this.lowLoc = lowLoc;
        this.highLoc = highLoc;
        this.safetyTeleportLoc = safetyTeleportLocation;
        this.regenTime = regenTime;
        this.blocks = blocks;
        this.regenMessage = ChatColor.translateAlternateColorCodes('&',regenMessage);
        this.commandList = commandList;
    }

    //Checks to see if the location is inside the box
    public boolean inBox(Location loc) {
        if (!(loc.getX() >= lowLoc.getX() && loc.getX() <= highLoc.getX()+1)) return false;
        if (!(loc.getY() >= lowLoc.getY() && loc.getY() <= highLoc.getY())) return false;
        if (!(loc.getZ() >= lowLoc.getZ()-1 && loc.getZ() <= highLoc.getZ())) return false;
        return true;
    }
}
