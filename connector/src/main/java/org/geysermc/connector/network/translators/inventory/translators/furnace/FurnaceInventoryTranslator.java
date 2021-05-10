/*
 * Copyright (c) 2019-2021 RoryMC. http://geysermc.org
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
 * @author RoryMC
 * @link https://github.com/RoryMC/Rory
 */

package org.geysermc.connector.network.translators.inventory.translators.furnace;

import com.nukkitx.protocol.bedrock.data.inventory.ContainerSlotType;
import com.nukkitx.protocol.bedrock.data.inventory.ContainerType;
import org.geysermc.connector.network.translators.inventory.BedrockContainerSlot;

public class FurnaceInventoryTranslator extends AbstractFurnaceInventoryTranslator {
    public FurnaceInventoryTranslator() {
        super("minecraft:furnace[facing=north,lit=false]", ContainerType.FURNACE);
    }

    @Override
    public BedrockContainerSlot javaSlotToBedrockContainer(int slot) {
        if (slot == 0) {
            return new BedrockContainerSlot(ContainerSlotType.FURNACE_INGREDIENT, javaSlotToBedrock(slot));
        }
        return super.javaSlotToBedrockContainer(slot);
    }
}
