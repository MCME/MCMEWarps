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
package co.mcme.warps;

import co.mcme.warps.commands.WarpCommand;
import co.mcme.warps.commands.WarpListCommand;
import co.mcme.warps.commands.WarpSetCommand;
import co.mcme.warps.storage.WarpDatabase;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.codehaus.jackson.map.ObjectMapper;

public class Warps extends JavaPlugin {

    @Getter
    static Server serverInstance;
    @Getter
    static Warps pluginInstance;
    @Getter
    static File pluginDataFolder;
    @Getter
    static ObjectMapper JsonMapper = new ObjectMapper();

    @Override
    public void onEnable() {
        serverInstance = getServer();
        pluginInstance = this;
        pluginDataFolder = pluginInstance.getDataFolder();
        if (!pluginDataFolder.exists()) {
            pluginDataFolder.mkdirs();
        }
        try {
            int warpsloaded = WarpDatabase.loadWarps();
        } catch (IOException ex) {
            Logger.getLogger(Warps.class.getName()).log(Level.SEVERE, null, ex);
        }
        getCommand("warp").setExecutor(new WarpCommand());
        getCommand("listwarps").setExecutor(new WarpListCommand());
        getCommand("setwarp").setExecutor(new WarpSetCommand());
    }

    @Override
    public void onDisable() {
        try {
            WarpDatabase.saveWarps();
        } catch (IOException ex) {
            Logger.getLogger(Warps.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
