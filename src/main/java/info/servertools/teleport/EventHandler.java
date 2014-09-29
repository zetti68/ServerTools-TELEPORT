/*
 * Copyright 2014 ServerTools
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.servertools.teleport;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import info.servertools.core.util.Location;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class EventHandler {

    public EventHandler() {

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {

        if (event.entity instanceof EntityPlayer && !(event.entity instanceof FakePlayer)) {

            EntityPlayer player = (EntityPlayer) event.entity;
            Location location = new Location(player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ);

            TeleportManager.backMap.put(player.getGameProfile().getId(), location);

            ServerToolsTeleport.log.trace("Set back location for Player: {} to DIM:{}, X:{}, Y:{}, Z:{}", player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ);
        }
    }
}
