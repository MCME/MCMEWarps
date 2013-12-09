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
import co.mcme.warps.commands.WarpModifyCommand;
import co.mcme.warps.commands.WarpSearchCommand;
import co.mcme.warps.commands.WarpSetCommand;
import co.mcme.warps.commands.WarpUnsetCommand;
import co.mcme.warps.storage.WarpDatabase;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class Warps extends JavaPlugin {

    @Getter
    private static Server serverInstance;
    @Getter
    private static Warps pluginInstance;
    @Getter
    private static File pluginDataFolder;
    @Getter
    private static ObjectMapper jsonMapper = new ObjectMapper().configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
    @Getter
    private static String fileSeperator = System.getProperty("file.separator");
    @Getter
    private static SimpleDateFormat dateformat = new SimpleDateFormat("MMM d y");

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
        }
        getCommand("warp").setExecutor(new WarpCommand());
        getCommand("listwarps").setExecutor(new WarpListCommand());
        getCommand("setwarp").setExecutor(new WarpSetCommand());
        getCommand("unsetwarp").setExecutor(new WarpUnsetCommand());
        getCommand("modifywarp").setExecutor(new WarpModifyCommand());
        getCommand("searchwarps").setExecutor(new WarpSearchCommand());
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
