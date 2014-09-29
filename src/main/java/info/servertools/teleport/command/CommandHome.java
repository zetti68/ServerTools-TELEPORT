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
import info.servertools.core.lib.Strings;
import info.servertools.core.util.ChatUtils;
import info.servertools.core.util.Location;
import info.servertools.teleport.HomeManager;
import info.servertools.teleport.TeleportManager;
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
    public CommandLevel getCommandLevel() {
        return CommandLevel.ANYONE;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (!(sender instanceof EntityPlayerMP))
            throw new WrongUsageException(Strings.COMMAND_ERROR_ONLYPLAYER);

        EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(sender.getCommandSenderName());
        if (args.length == 0) {
            Location home = HomeManager.getHome(player.getPersistentID(), player.worldObj.provider.dimensionId);
            if (home != null) {
                TeleportManager.backMap.put(player.getPersistentID(), new Location(player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ));
                player.setPositionAndUpdate(home.x, home.y, home.z);
                player.addChatMessage(ChatUtils.getChatComponent("Teleported Home", EnumChatFormatting.GREEN));
            } else {
                player.addChatMessage(ChatUtils.getChatComponent("Could not find your home", EnumChatFormatting.RED));
            }
        } else if ("set".equalsIgnoreCase(args[0])) {
            HomeManager.setHome(player.getPersistentID(), player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ);
            player.addChatMessage(ChatUtils.getChatComponent("Set your home", EnumChatFormatting.GREEN));
        } else if ("clear".equalsIgnoreCase(args[0])) {
            if (HomeManager.clearHome(player.getPersistentID(), player.worldObj.provider.dimensionId)) {
                player.addChatMessage(ChatUtils.getChatComponent("Cleared your home", EnumChatFormatting.GREEN));
            } else {
                player.addChatMessage(ChatUtils.getChatComponent("You don't have a home to clear!", EnumChatFormatting.RED));
            }
        } else
            throw new WrongUsageException(getCommandUsage(sender));
    }

    @Override
    public String getCommandUsage(ICommandSender var1) {
        return "/" + name + "{set|clear}";
    }
}
