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

package org.geysermc.connector.entity;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.data.entity.EntityData;
import com.nukkitx.protocol.bedrock.data.entity.EntityFlag;
import org.geysermc.connector.entity.type.EntityType;
import org.geysermc.connector.network.session.RorySession;

public class EnderCrystalEntity extends Entity {

    public EnderCrystalEntity(long entityId, long geyserId, EntityType entityType, Vector3f position, Vector3f motion, Vector3f rotation) {
        super(entityId, geyserId, entityType, position, motion, rotation);

        // Bedrock 1.16.100+ - prevents the entity from appearing on fire itself when fire is underneath it
        metadata.getFlags().setFlag(EntityFlag.FIRE_IMMUNE, true);
    }

    @Override
    public void updateBedrockMetadata(EntityMetadata entityMetadata, RorySession session) {
        // Show beam
        // Usually performed client-side on Bedrock except for Ender Dragon respawn event
        if (entityMetadata.getId() == 7) {
            if (entityMetadata.getValue() instanceof Position) {
                Position pos = (Position) entityMetadata.getValue();
                metadata.put(EntityData.BLOCK_TARGET, Vector3i.from(pos.getX(), pos.getY(), pos.getZ()));
            } else {
                metadata.put(EntityData.BLOCK_TARGET, Vector3i.ZERO);
            }
        }
        // There is a base located on the ender crystal
        if (entityMetadata.getId() == 8) {
            metadata.getFlags().setFlag(EntityFlag.SHOW_BOTTOM, (boolean) entityMetadata.getValue());
        }
        super.updateBedrockMetadata(entityMetadata, session);
    }
}
