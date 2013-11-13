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
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
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
    @Setter
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
    private ArrayList<String> invited;
    @Setter
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
            return true;
        }
    }

    public boolean uninvitePlayer(String name) {
        if (!invited.contains(name)) {
            return false;
        } else {
            invited.remove(name);
            setDirty(true);
            return true;
        }
    }
}
/* 
Warp document:
{
    "name": "myawesomewarp",
    "inviteonly": true,
    "owner": "meggawatts",
    "welcome": "Welcome to Awesome Town",
    "location": {
        "world": "Off-Topic",
        "x": 0,
        "y": 5,
        "z": 0,
        "yaw": 180.74
        "pitch": -0.19,
    },
    "invited": [
        "q220",
        "Ma5terMinD",
        "Credoo",
        "HomieNo"
    ]
}
*/