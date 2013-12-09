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

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class SearchWarps {

    public static SearchResult searchWarps(String search, OfflinePlayer player) {
        ArrayList<PlayerWarp> exact = new ArrayList();
        ArrayList<PlayerWarp> partial = new ArrayList();
        List<String> names = new ArrayList(WarpDatabase.getWarps().keySet());

        for (String wName : names) {
            PlayerWarp warp = WarpDatabase.getWarp(wName);
            if (warp.canWarp(player)) {
                if (warp.getName().equalsIgnoreCase(search)) {
                    exact.add(warp);
                } else if (warp.getName().toLowerCase().contains(search.toLowerCase())) {
                    partial.add(warp);
                }
            }
        }
        for (PlayerWarp warp : partial) {
            if (exact.contains(warp)) {
                exact.remove(warp);
            }
        }
        return new SearchResult(exact, partial);
    }

    public static class SearchResult {

        public SearchResult(ArrayList<PlayerWarp> exact, ArrayList<PlayerWarp> partial) {
            this.exact = exact;
            this.partial = partial;
        }

        @Getter
        private final ArrayList<PlayerWarp> exact;
        @Getter
        private final ArrayList<PlayerWarp> partial;
    }
}
