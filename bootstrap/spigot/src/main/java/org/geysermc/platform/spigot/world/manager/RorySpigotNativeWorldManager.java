/*
 * Copyright (c) 2019-2021 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.platform.spigot.world.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.geysermc.connector.network.session.RorySession;
import org.geysermc.connector.network.translators.world.block.BlockTranslator;
import org.geysermc.geyser.adapters.spigot.SpigotAdapters;
import org.geysermc.geyser.adapters.spigot.SpigotWorldAdapter;

public class RorySpigotNativeWorldManager extends RorySpigotWorldManager {
    protected final SpigotWorldAdapter adapter;

    public RorySpigotNativeWorldManager(Plugin plugin, boolean use3dBiomes) {
        super(plugin, use3dBiomes);
        adapter = SpigotAdapters.getWorldAdapter();
    }

    @Override
    public int getBlockAt(RorySession session, int x, int y, int z) {
        Player player = Bukkit.getPlayer(session.getPlayerEntity().getUsername());
        if (player == null) {
            return BlockTranslator.JAVA_AIR_ID;
        }
        return adapter.getBlockAt(player.getWorld(), x, y, z);
    }
}
