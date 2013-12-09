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

import co.mcme.warps.Warps;
import co.mcme.warps.storage.PlayerWarp;
import co.mcme.warps.storage.SearchWarps;
import co.mcme.warps.storage.SearchWarps.SearchResult;
import co.mcme.warps.storage.WarpDatabase;
import java.util.ArrayList;
import java.util.Date;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.ChatPaginator;

public class WarpSearchCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to run this command.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length > 0) {
            String search = args[0];
            if (search.toLowerCase().contains("@")) {
                search = search.replaceAll("@", "");
                if (WarpDatabase.getWarpCreators().containsKey(search)) {
                    ArrayList<PlayerWarp> warps = WarpDatabase.getWarpCreators().get(search).getWarps();
                    StringBuilder customList = new StringBuilder();
                    boolean first = true;
                    for (PlayerWarp warp : warps) {
                        if (!first) {
                            customList.append("\n");
                        }
                        ChatColor col = ChatColor.GREEN;
                        if (warp.isInviteonly()) {
                            col = ChatColor.RED;
                        }
                        Date date = new Date(warp.getCreateStamp());
                        customList.append(col).append(warp.getName())
                                .append(ChatColor.GRAY).append(" created by ")
                                .append(ChatColor.AQUA).append(warp.getOwner())
                                .append(ChatColor.GRAY).append(" on ")
                                .append(ChatColor.AQUA).append(Warps.getDateformat().format(date));
                        if (first) {
                            first = false;
                        }
                    }
                    if (args.length > 1) {
                        ChatPaginator.ChatPage page = ChatPaginator.paginate(customList.toString(), Integer.valueOf(args[1]), ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH, 8);
                        sender.sendMessage(ChatColor.GRAY + search + "'s warps " + ChatColor.AQUA + page.getPageNumber() + ChatColor.GRAY + " of " + ChatColor.AQUA + page.getTotalPages());
                        for (String line : page.getLines()) {
                            sender.sendMessage(line);
                        }
                        return true;
                    } else {
                        ChatPaginator.ChatPage page = ChatPaginator.paginate(customList.toString(), 1, ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH, 8);
                        sender.sendMessage(ChatColor.GRAY + search + "'s warps " + ChatColor.AQUA + page.getPageNumber() + ChatColor.GRAY + " of " + ChatColor.AQUA + page.getTotalPages());
                        for (String line : page.getLines()) {
                            sender.sendMessage(line);
                        }
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Cannot find any warps by " + search);
                    return true;
                }
            }
            SearchResult res = SearchWarps.searchWarps(search, player);
            StringBuilder exactLines = new StringBuilder();
            boolean first = true;
            for (PlayerWarp warp : res.getExact()) {
                if (!first) {
                    exactLines.append("\n");
                }
                ChatColor col = ChatColor.GREEN;
                if (warp.isInviteonly()) {
                    col = ChatColor.RED;
                }
                Date date = new Date(warp.getCreateStamp());
                exactLines.append(col).append(warp.getName())
                        .append(ChatColor.GRAY).append(" created by ")
                        .append(ChatColor.AQUA).append(warp.getOwner())
                        .append(ChatColor.GRAY).append(" on ")
                        .append(ChatColor.AQUA).append(Warps.getDateformat().format(date));
                if (first) {
                    first = false;
                }
            }

            StringBuilder partialLines = new StringBuilder();
            first = true;
            for (PlayerWarp warp : res.getPartial()) {
                if (!first) {
                    partialLines.append("\n");
                }
                ChatColor col = ChatColor.GREEN;
                if (warp.isInviteonly()) {
                    col = ChatColor.RED;
                }
                Date date = new Date(warp.getCreateStamp());
                partialLines.append(col).append(warp.getName())
                        .append(ChatColor.GRAY).append(" created by ")
                        .append(ChatColor.AQUA).append(warp.getOwner())
                        .append(ChatColor.GRAY).append(" on ")
                        .append(ChatColor.AQUA).append(Warps.getDateformat().format(date));
                if (first) {
                    first = false;
                }
            }
            if (res.getExact().size() > 0) {
                ChatPaginator.ChatPage page = ChatPaginator.paginate(partialLines.toString(), 1, ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH, 8);
                sender.sendMessage(ChatColor.GRAY + "Exact Matches for " + args[0]);
                for (String line : page.getLines()) {
                    sender.sendMessage(line);
                }
                return true;
            }
            if (res.getPartial().size() > 0) {
                if (args.length > 1) {
                    ChatPaginator.ChatPage page = ChatPaginator.paginate(partialLines.toString(), Integer.valueOf(args[1]), ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH, 8);
                    sender.sendMessage(ChatColor.GRAY + "Partial Matches page " + ChatColor.AQUA + page.getPageNumber() + ChatColor.GRAY + " of " + ChatColor.AQUA + page.getTotalPages());
                    for (String line : page.getLines()) {
                        sender.sendMessage(line);
                    }
                    return true;
                } else {
                    ChatPaginator.ChatPage page = ChatPaginator.paginate(partialLines.toString(), 1, ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH, 8);
                    sender.sendMessage(ChatColor.GRAY + "Partial Matches page " + ChatColor.AQUA + page.getPageNumber() + ChatColor.GRAY + " of " + ChatColor.AQUA + page.getTotalPages());
                    for (String line : page.getLines()) {
                        sender.sendMessage(line);
                    }
                    return true;
                }
            }
            sender.sendMessage(ChatColor.RED + "No warps found for " + args[0]);
            return true;
        }
        return false;
    }
}
