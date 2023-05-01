package com.splerix.boxgen;

import com.splerix.boxgen.Util.Box;
import com.splerix.boxgen.Util.DataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class BoxGenTabCompleter implements TabCompleter {
    List<String> arg1 = new ArrayList<>();
    List<String> arg2 = new ArrayList<>();

    public BoxGenTabCompleter(List<Box> boxList) {
        if (boxList == null) return;
        reload(boxList);
    }
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (arg1.isEmpty()) {
            arg1.add("reload"); arg1.add("regen"); arg1.add("regen-all");
        }

        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            for (String a : arg1) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                    results.add(a);
            }
            return results;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("regen")) {
            for (String a : arg2) {
                if (a.toLowerCase().startsWith(args[1].toLowerCase()))
                    results.add(a);
            }
            return results;
        }
        return null;
    }

    public void reload(List<Box> boxList) {
        if (boxList == null) return;
        for (Box box : boxList) {
            arg2.add(box.name);
        }
    }
}
