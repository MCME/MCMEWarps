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
import co.mcme.warps.storage.WarpDatabase;
import java.util.ArrayList;
import java.util.Date;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.ChatPaginator;

public class WarpListCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArrayList<PlayerWarp> list = new ArrayList();
        for (PlayerWarp warp : WarpDatabase.getWarps().values()) {
            if (warp.isInviteonly()) {
                if (warp.getInvited() != null && warp.getInvited().contains(sender.getName())) {
                    list.add(warp);
                }
            } else {
                list.add(warp);
            }
        }
        StringBuilder lines = new StringBuilder();
        boolean first = true;
        for (PlayerWarp warp : list) {
            if (!first) {
                lines.append("\n");
            }
            ChatColor col = ChatColor.GREEN;
            if (warp.isInviteonly()) {
                col = ChatColor.RED;
            }
            Date date = new Date(warp.getCreateStamp());
            lines.append(col).append(warp.getName())
                    .append(ChatColor.GRAY).append(" created by ")
                    .append(ChatColor.AQUA).append(warp.getOwner())
                    .append(ChatColor.GRAY).append(" on ")
                    .append(ChatColor.AQUA).append(Warps.getDateformat().format(date));
            if (first) {
                first = false;
            }
        }

        if (args.length > 0) {
            ChatPaginator.ChatPage page = ChatPaginator.paginate(lines.toString(), Integer.valueOf(args[0]), ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH, 8);
            sender.sendMessage(ChatColor.GRAY + "Warp List page " + ChatColor.AQUA + page.getPageNumber() + ChatColor.GRAY + " of " + ChatColor.AQUA + page.getTotalPages());
            for (String line : page.getLines()) {
                sender.sendMessage(line);
            }
            return true;
        } else {
            ChatPaginator.ChatPage page = ChatPaginator.paginate(lines.toString(), 1, ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH, 8);
            sender.sendMessage(ChatColor.GRAY + "Warp List page " + ChatColor.AQUA + page.getPageNumber() + ChatColor.GRAY + " of " + ChatColor.AQUA + page.getTotalPages());
            for (String line : page.getLines()) {
                sender.sendMessage(line);
            }
            return true;
        }
    }
}
