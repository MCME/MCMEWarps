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
package co.mcme.warps.storage;

import co.mcme.warps.Warps;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

public final class PlayerWarp {

    public PlayerWarp(OfflinePlayer p, Location loc, boolean invite, String name) {
        setName(name);
        setInviteonly(invite);
        setOwner(p.getName());
        location newloc = new location();
        newloc.setPitch(loc.getPitch());
        newloc.setWorld(loc.getWorld().getName());
        newloc.setX(loc.getX());
        newloc.setY(loc.getY());
        newloc.setYaw(loc.getYaw());
        newloc.setZ(loc.getZ());
        setLocation(newloc);
        setWelcome("&bWelcome to &4%warpname%&b, %player%");
        setCreateStamp(System.currentTimeMillis());
        setDirty(true);
    }

    public PlayerWarp() {
    }

    public static class location {

        @Setter
        @Getter
        private String world;
        @Setter
        @Getter
        private double x;
        @Setter
        @Getter
        private double y;
        @Setter
        @Getter
        private double z;
        @Setter
        @Getter
        private float yaw;
        @Setter
        @Getter
        private float pitch;

        public Location toBukkitLocation() {
            return new Location(Warps.getServerInstance().getWorld(world), x, y, z, yaw, pitch);
        }

        public void updateLocation(Location newloc) {
            world = newloc.getWorld().getName();
            x = newloc.getX();
            y = newloc.getY();
            z = newloc.getZ();
            yaw = newloc.getYaw();
            pitch = newloc.getPitch();
        }
    }
    @Getter
    private String name;
    @Setter
    @Getter
    private boolean inviteonly;
    @Setter
    @Getter
    private String owner;
    @Setter
    @Getter
    private String welcome;
    @Setter
    @Getter
    private location location;
    @Setter
    @Getter
    private ArrayList<String> invited = new ArrayList();
    @Getter
    private boolean dirty;
    @Setter
    @Getter
    private Long createStamp;

    public boolean invitePlayer(String name) {
        if (invited.contains(name)) {
            return false;
        } else {
            invited.add(name);
            setDirty(true);
            if (Warps.getServerInstance().getOfflinePlayer(name).isOnline()) {
                Warps.getServerInstance().getPlayer(name).sendMessage(ChatColor.GREEN + "You have been invited to /warp " + getName());
            }
            return true;
        }
    }

    public boolean uninvitePlayer(String name) {
        if (!invited.contains(name)) {
            return false;
        } else {
            invited.remove(name);
            setDirty(true);
            if (Warps.getServerInstance().getOfflinePlayer(name).isOnline()) {
                Warps.getServerInstance().getPlayer(name).sendMessage(ChatColor.RED + "You have been uninvited from " + getName());
            }
            return true;
        }
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
        if (dirty) {
            try {
                WarpDatabase.saveWarps();
            } catch (IOException ex) {
            }
        }
    }

    public void setName(String name) {
        File playerContainer = new File(Warps.getPluginDataFolder(), "warps" + Warps.getFileSeperator() + getOwner());
        File warpFile = new File(playerContainer, getName() + ".warp");
        if (warpFile.exists()) {
            warpFile.delete();
        }
        this.name = name;
        try {
            WarpDatabase.saveWarps();
        } catch (IOException ex) {
        }
    }

    public boolean canModify(String name) {
        return getOwner().equals(name) || Warps.getServerInstance().getPlayer(name).hasPermission("warps.ignoreownership");
    }

    public boolean canWarp(OfflinePlayer p) {
        if (inviteonly) {
            return invited.contains(p.getName());
        } else {
            return true;
        }
    }
}
