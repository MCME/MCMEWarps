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
import co.mcme.warps.storage.SearchWarps;
import co.mcme.warps.storage.SearchWarps.SearchResult;
import co.mcme.warps.storage.WarpDatabase;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Set<String> warpnames = WarpDatabase.getWarps().keySet();
        if (sender instanceof Player) {
            if (args.length > 0) {
                String name = "";
                for (String arg : args) {
                    name += arg + " ";
                }
                name = name.substring(0, name.length() - 1);
                if (warpnames.contains(name)) {
                    PlayerWarp warp = WarpDatabase.getWarp(name);
                    if (warp.canWarp((Player) sender)) {
                        ((Player) sender).teleport(warp.getLocation().toBukkitLocation());
                        sender.sendMessage(getFormattedWelcome(warp, (Player) sender));
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "You are not invited to that warp.");
                        return true;
                    }
                } else {
                    SearchResult res = SearchWarps.searchWarps(name, (Player) sender);
                    if (res.getPartial().size() > 0) {
                        PlayerWarp warp = res.getPartial().get(0);
                        if (warp.canWarp((Player) sender)) {
                            ((Player) sender).teleport(warp.getLocation().toBukkitLocation());
                            sender.sendMessage(getFormattedWelcome(warp, (Player) sender));
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "You are not invited to that warp.");
                            return true;
                        }
                    }
                    sender.sendMessage(ChatColor.RED + "No warp found by the name, " + name);
                    return true;
                }
            }
        } else {
            sender.sendMessage("This command can only be used as a player");
            return true;
        }
        return false;
    }

    private String getFormattedWelcome(PlayerWarp warp, Player p) {
        String coloredmessage = ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', warp.getWelcome());
        return coloredmessage.replaceAll("%warpname%", warp.getName()).replaceAll("%world%", warp.getLocation().getWorld()).replaceAll("%player%", p.getName()).replaceAll("%owner%", warp.getOwner());
    }
}
