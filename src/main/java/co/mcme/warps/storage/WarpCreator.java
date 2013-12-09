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
import lombok.Getter;

public class WarpCreator {

    @Getter
    private final String name;
    @Getter
    private ArrayList<PlayerWarp> warps = new ArrayList();

    public WarpCreator(String name) {
        this.name = name;
    }

    public void addWarp(PlayerWarp warp) {
        if (!warps.contains(warp)) {
            warps.add(warp);
        }
    }

    public void removeWarp(PlayerWarp warp) {
        if (warps.contains(warp)) {
            warps.remove(warp);
        }
    }
}
