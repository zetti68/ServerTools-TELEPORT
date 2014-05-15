/*
 * Copyright 2014 Matthew Prenger
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

package com.matthewprenger.servertools.teleport.command;

import com.matthewprenger.servertools.core.command.ServerToolsCommand;
import com.matthewprenger.servertools.core.util.Util;
import com.matthewprenger.servertools.teleport.HomeManager;
import com.matthewprenger.servertools.core.util.Location;
import com.matthewprenger.servertools.teleport.TeleportManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;

public class CommandHome extends ServerToolsCommand {

    public CommandHome(String defaultName) {
        super(defaultName);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(sender.getCommandSenderName());

        if (args.length == 0) {

            Location home = HomeManager.getHome(player.getCommandSenderName(), player.worldObj.provider.dimensionId);

            if (home != null) {

                TeleportManager.backMap.put(player.getGameProfile().getName() ,new Location(player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ));

                player.setPositionAndUpdate(home.x, home.y, home.z);
                player.addChatMessage(Util.getChatComponent("Teleported Home", EnumChatFormatting.GREEN));
            } else {
                player.addChatMessage(Util.getChatComponent("Could not find your home", EnumChatFormatting.RED));
            }
        } else if ("set".equalsIgnoreCase(args[0])) {

            HomeManager.setHome(player.getCommandSenderName(), player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ);
            player.addChatMessage(Util.getChatComponent("Set your home", EnumChatFormatting.GREEN));

        } else if ("clear".equalsIgnoreCase(args[0])) {

            if (HomeManager.clearHome(player.getCommandSenderName(), player.worldObj.provider.dimensionId)) {
                player.addChatMessage(Util.getChatComponent("Cleared your home", EnumChatFormatting.GREEN));
            } else {
                player.addChatMessage(Util.getChatComponent("You don't have a home to clear!", EnumChatFormatting.RED));
            }
        } else
            throw new WrongUsageException(getCommandUsage(sender));
    }

    @Override
    public String getCommandUsage(ICommandSender var1) {
        return "/" + name + "{set|clear}";
    }
}
