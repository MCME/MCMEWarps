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
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.PlayerNamePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WarpModifyCommand implements CommandExecutor, ConversationAbandonedListener {

    private ConversationFactory conversationFactory;

    public WarpModifyCommand() {
        conversationFactory = new ConversationFactory(Warps.getPluginInstance())
                .withModality(true)
                .withFirstPrompt(new whichWarp())
                .withEscapeSequence("/cancel")
                .withTimeout(300)
                .thatExcludesNonPlayersWithMessage("You must be a player to send thus command")
                .withPrefix(new warpModifyPrefix())
                .addConversationAbandonedListener(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Conversable) {
            conversationFactory.buildConversation((Conversable) sender).begin();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {
        if (abandonedEvent.gracefulExit()) {
            abandonedEvent.getContext().getForWhom().sendRawMessage(ChatColor.AQUA + "Modify Warp exited.");
        } else {
            abandonedEvent.getContext().getForWhom().sendRawMessage(ChatColor.AQUA + "Modify Warp timed out");
        }
    }

    private class whichWarp extends StringPrompt {

        Set<String> warpnames = WarpDatabase.getWarps().keySet();

        @Override
        public String getPromptText(ConversationContext cc) {
            return "Which warp would you like to edit?"
                    + "\n" + "or exit with /cancel";
        }

        @Override
        public Prompt acceptInput(ConversationContext cc, String string) {
            if (warpnames.contains(string)) {
                cc.setSessionData("warpname", string);
                return new whichAttribute();
            } else {
                return new unknownWarpPrompt();
            }
        }
    }

    private class whichAttribute extends FixedSetPrompt {

        Set<String> warpnames = WarpDatabase.getWarps().keySet();

        public whichAttribute() {
            super("name", "inviteonly", "welcome", "location", "invite", "uninvite");
        }

        @Override
        public String getPromptText(ConversationContext cc) {
            return "What attribute would you like to modify? \n" + formatFixedSet();
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext cc, String s) {
            cc.setSessionData("attribute", s);
            switch (s) {
                case "name": {
                    return new namePrompt();
                }
                case "inviteonly": {
                    return new inviteOnlyPrompt();
                }
                case "welcome": {
                    return new welcomePrompt();
                }
                case "location": {
                    return new updateLocationPrompt();
                }
                case "invite": {
                    return new invitePlayerPrompt(Warps.getPluginInstance());
                }
                case "uninvite": {
                    return new uninvitePlayerPrompt(Warps.getPluginInstance());
                }
                default: {
                    return Prompt.END_OF_CONVERSATION;
                }
            }
        }
    }

    private class namePrompt extends StringPrompt {

        Set<String> warpnames = WarpDatabase.getWarps().keySet();

        @Override
        public String getPromptText(ConversationContext cc) {
            return "What would you like the new name to be?";
        }

        @Override
        public Prompt acceptInput(ConversationContext cc, String string) {
            if (!warpnames.contains(string)) {
                PlayerWarp warp = WarpDatabase.getWarp((String) cc.getSessionData("warpname"));
                if (warp.canModify(((Player) cc.getForWhom()).getName())) {
                    warp.setName(string);
                    warp.setDirty(true);
                    cc.setSessionData("warpname", string);
                    return new successPrompt();
                } else {
                    return new unownedWarpPrompt();
                }
            } else {
                return new failurePrompt();
            }
        }
    }

    public class inviteOnlyPrompt extends BooleanPrompt {

        @Override
        protected Prompt acceptValidatedInput(ConversationContext cc, boolean bln) {
            PlayerWarp warp = WarpDatabase.getWarp((String) cc.getSessionData("warpname"));
            if (warp.canModify(((Player) cc.getForWhom()).getName())) {
                warp.setInviteonly(bln);
                warp.invitePlayer(((Player) cc.getForWhom()).getName());
                warp.setDirty(true);
                return new successPrompt();
            } else {
                return new unownedWarpPrompt();
            }
        }

        @Override
        public String getPromptText(ConversationContext cc) {
            return "Should the warp be invite only? (true or false)";
        }
    }

    public class welcomePrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext cc) {
            return "What would you like the welcome to be?"
                    + "\n" + "You can use & for color codes,"
                    + "\n" + "and any of these variables:"
                    + "\n" + "%warpname% - The name of the warp"
                    + "\n" + "%world% - The world the warp is in"
                    + "\n" + "%player% - The player who warped"
                    + "\n" + "%owner% - The player who made the warp";
        }

        @Override
        public Prompt acceptInput(ConversationContext cc, String string) {
            PlayerWarp warp = WarpDatabase.getWarp((String) cc.getSessionData("warpname"));
            if (warp.canModify(((Player) cc.getForWhom()).getName())) {
                warp.setWelcome(string);
                warp.setDirty(true);
                return new successPrompt();
            } else {
                return new unownedWarpPrompt();
            }
        }
    }

    public class updateLocationPrompt extends MessagePrompt {

        @Override
        protected Prompt getNextPrompt(ConversationContext cc) {
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        public String getPromptText(ConversationContext cc) {
            PlayerWarp warp = WarpDatabase.getWarp((String) cc.getSessionData("warpname"));
            if (warp.canModify(((Player) cc.getForWhom()).getName())) {
                warp.getLocation().updateLocation(((Player) cc.getForWhom()).getLocation());
                warp.setDirty(true);
                return "Successfully saved the warp, " + cc.getSessionData("warpname") + "'s location to your current position.";
            } else {
                return new unownedWarpPrompt().getPromptText(cc);
            }

        }
    }

    public class invitePlayerPrompt extends PlayerNamePrompt {

        public invitePlayerPrompt(Plugin plugin) {
            super(plugin);
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext cc, Player player) {
            PlayerWarp warp = WarpDatabase.getWarp((String) cc.getSessionData("warpname"));
            if (warp.canModify(((Player) cc.getForWhom()).getName())) {
                warp.invitePlayer(player.getName());
                warp.setDirty(true);
                return new successPrompt();
            } else {
                return new unownedWarpPrompt();
            }
        }

        @Override
        public String getPromptText(ConversationContext cc) {
            return "Who would you like to invite to the warp, " + cc.getSessionData("warpname") + "?";
        }
    }

    public class uninvitePlayerPrompt extends PlayerNamePrompt {

        public uninvitePlayerPrompt(Plugin plugin) {
            super(plugin);
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext cc, Player player) {
            PlayerWarp warp = WarpDatabase.getWarp((String) cc.getSessionData("warpname"));
            if (warp.canModify(((Player) cc.getForWhom()).getName())) {
                warp.uninvitePlayer(player.getName());
                warp.setDirty(true);
                return new successPrompt();
            } else {
                return new unownedWarpPrompt();
            }
        }

        @Override
        public String getPromptText(ConversationContext cc) {
            return "Who would you like to uninvite to the warp, " + cc.getSessionData("warpname") + "?";
        }
    }

    public class successPrompt extends MessagePrompt {

        Set<String> warpnames = WarpDatabase.getWarps().keySet();

        @Override
        protected Prompt getNextPrompt(ConversationContext cc) {
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        public String getPromptText(ConversationContext cc) {
            return "Successfully saved the warp, " + cc.getSessionData("warpname");
        }
    }

    public class failurePrompt extends MessagePrompt {

        Set<String> warpnames = WarpDatabase.getWarps().keySet();

        @Override
        protected Prompt getNextPrompt(ConversationContext cc) {
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        public String getPromptText(ConversationContext cc) {
            return "Could not save the warp, " + cc.getSessionData("warpname");
        }
    }

    public class unknownWarpPrompt extends MessagePrompt {

        @Override
        protected Prompt getNextPrompt(ConversationContext cc) {
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        public String getPromptText(ConversationContext cc) {
            return "That warp could not be found.";
        }
    }

    public class unownedWarpPrompt extends MessagePrompt {

        @Override
        protected Prompt getNextPrompt(ConversationContext cc) {
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        public String getPromptText(ConversationContext cc) {
            return "You do not own that warp.";
        }
    }

    public class warpModifyPrefix implements ConversationPrefix {

        @Override
        public String getPrefix(ConversationContext cc) {
            String prefix = ChatColor.GRAY + "";
            String warpname = (String) cc.getSessionData("warpname");
            if (warpname != null) {
                prefix += "editing " + ChatColor.GOLD + warpname + ChatColor.AQUA + "\n";
            }
            return prefix;
        }
    }
}
