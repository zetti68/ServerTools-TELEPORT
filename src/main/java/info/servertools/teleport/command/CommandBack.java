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
package info.servertools.teleport.command;

import info.servertools.core.command.CommandLevel;
import info.servertools.core.command.ServerToolsCommand;
import info.servertools.core.util.ChatUtils;
import info.servertools.core.util.Location;
import info.servertools.teleport.TeleportManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.EnumChatFormatting;

public class CommandBack extends ServerToolsCommand {

    public CommandBack(String defaultName) {
        super(defaultName);
    }

    @Override
    public CommandLevel getCommandLevel() {
        return CommandLevel.ANYONE;
    }

    @Override
    public String getCommandUsage(ICommandSender var1) {

        return "/" + name;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (!(sender instanceof EntityPlayer))
            throw new WrongUsageException("That command can only be used by a player");

        EntityPlayer player = (EntityPlayer) sender;
        Location backLocation = TeleportManager.backMap.get(player.getPersistentID());

        ChatComponentStyle componentText;

        if (backLocation != null) {

            if (player.worldObj.provider.dimensionId != backLocation.dimID) {

                player.travelToDimension(backLocation.dimID);
            }

            player.setPositionAndUpdate(backLocation.x, backLocation.y, backLocation.z);

            TeleportManager.backMap.remove(player.getPersistentID());

            componentText = ChatUtils.getChatComponent("Teleported back", EnumChatFormatting.GREEN);

        } else
            componentText = ChatUtils.getChatComponent("No back location", EnumChatFormatting.RED);

        player.addChatComponentMessage(componentText);
    }
}
