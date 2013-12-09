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
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

public class WarpDatabase {

    @Getter
    private final static TreeMap<String, PlayerWarp> warps = new TreeMap();
    @Getter
    private final static TreeMap<String, WarpCreator> warpCreators = new TreeMap();

    public static PlayerWarp getWarp(String name) {
        return warps.get(name);
    }

    public static int loadWarps() throws IOException {
        int count = 0;
        File warpfolder = new File(Warps.getPluginDataFolder(), "warps");
        if (!warpfolder.exists()) {
            warpfolder.mkdirs();
        }
        String[] playerfolders = warpfolder.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }
        });
        for (String pfolder : playerfolders) {
            File folder = new File(warpfolder, pfolder);
            if (folder.isDirectory()) {
                String[] pwarps = folder.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".warp");
                    }
                });
                ArrayList<PlayerWarp> twarps = new ArrayList();
                for (String warpfile : pwarps) {
                    PlayerWarp warp = Warps.getJsonMapper().readValue(new File(folder, warpfile), PlayerWarp.class);
                    twarps.add(warp);
                }
                for (PlayerWarp warp : twarps) {
                    WarpCreator wc;
                    if (!warpCreators.containsKey(warp.getOwner())) {
                        wc = new WarpCreator(warp.getOwner());
                        warpCreators.put(warp.getOwner(), wc);
                    }
                    wc = warpCreators.get(warp.getOwner());
                    wc.addWarp(warp);
                    warp.setDirty(false);
                    warps.put(warp.getName(), warp);
                }
            }
        }
        return count;
    }

    public static boolean addWarp(OfflinePlayer p, Location loc, boolean invite, String name) {
        PlayerWarp warp = new PlayerWarp(p, loc, invite, name);
        warps.put(warp.getName(), warp);
        WarpCreator wc;
        if (!warpCreators.containsKey(warp.getOwner())) {
            wc = new WarpCreator(warp.getOwner());
            warpCreators.put(warp.getOwner(), wc);
        }
        wc = warpCreators.get(warp.getOwner());
        wc.addWarp(warp);
        try {
            saveWarps();
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    public static void removeWarp(String name) {
        if (warps.containsKey(name)) {
            PlayerWarp warp = warps.get(name);
            File playerContainer = new File(Warps.getPluginDataFolder(), "warps" + Warps.getFileSeperator() + warp.getOwner());
            if (playerContainer.exists()) {
                File warpFile = new File(playerContainer, warp.getName() + ".warp");
                if (warpFile.exists()) {
                    warpFile.delete();
                }
            }
            warps.remove(name);
            WarpCreator wc;
            if (!warpCreators.containsKey(warp.getOwner())) {
                wc = new WarpCreator(warp.getOwner());
                warpCreators.put(warp.getOwner(), wc);
            }
            wc = warpCreators.get(warp.getOwner());
            wc.removeWarp(warp);
            try {
                saveWarps();
            } catch (IOException ex) {
            }
        }
    }

    public static void saveWarps() throws IOException {
        for (PlayerWarp warp : warps.values()) {
            File playerContainer = new File(Warps.getPluginDataFolder(), "warps" + Warps.getFileSeperator() + warp.getOwner());
            if (!playerContainer.exists()) {
                playerContainer.mkdirs();
            }
            File warpFile = new File(playerContainer, warp.getName() + ".warp");
            if (warp.isDirty()) {
                Warps.getJsonMapper().writeValue(warpFile, warp);
                warp.setDirty(false);
            }
        }
    }
}
