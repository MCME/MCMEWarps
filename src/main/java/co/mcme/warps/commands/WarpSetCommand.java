/*  This file is part of MCMEWarps.
 * 
 *  MCMEWarps is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MCMEWarps is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MCMEWarps.  If not, see <http://www.gnu.org/licenses/>.
 */
package co.mcme.warps.commands;

import co.mcme.warps.storage.PlayerWarp;
import co.mcme.warps.storage.WarpDatabase;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpSetCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Set<String> warpnames = WarpDatabase.getWarps().keySet();
        if (sender instanceof Player) {
            if (args.length > 0) {
                boolean inviteOnly = false;
                String name = "";
                for (String arg : args) {
                    if (arg.equalsIgnoreCase("-p")) {
                        inviteOnly = true;
                    }
                    name += arg + " ";
                }
                name = name.replaceAll("-p ", "");
                name = name.substring(0, name.length() - 1);
                System.out.println(name);
                if (!warpnames.contains(name)) {
                    Location loc = ((Player) sender).getLocation();
                    if (WarpDatabase.addWarp((OfflinePlayer) sender, loc, inviteOnly, name)) {
                        PlayerWarp warp = WarpDatabase.getWarp(name);
                        sender.sendMessage(ChatColor.GREEN + "Successfully created warp, " + name);
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "Could not create warp, " + name);
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "A warp by that name already exists");
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You must provide a name for the warp");
                return true;
            }
        } else {
            sender.sendMessage("You must be a player to send this command.");
            return true;
        }
    }
}
