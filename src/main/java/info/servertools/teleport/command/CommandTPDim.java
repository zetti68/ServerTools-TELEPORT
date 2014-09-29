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
import info.servertools.core.util.Location;
import info.servertools.core.util.ServerUtils;
import info.servertools.core.util.Util;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import java.util.List;

public class CommandTPDim extends ServerToolsCommand {

    public CommandTPDim(String defaultName) {

        super(defaultName);
    }

    @Override
    public CommandLevel getCommandLevel() {
        return CommandLevel.ANYONE;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {

        return "/" + name + "[target player] <destination player> OR /" + name + " [target player] | <DIM> <[x] [y] [z]>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length < 1)
            throw new WrongUsageException(getCommandUsage(sender));

        EntityPlayerMP target;
        Location destination;

        if (args.length != 2 && args.length != 5)
            target = getCommandSenderAsPlayer(sender);
        else {
            target = getPlayer(sender, args[0]);
            if (target == null)
                throw new PlayerNotFoundException();
        }

        if (args.length == 1) {
            WorldServer worldServer = DimensionManager.getWorld(parseInt(sender, args[0]));
            if (worldServer == null)
                throw new PlayerNotFoundException("That dimension doesn't exist");
            destination = new Location(worldServer.provider.dimensionId, worldServer.getSpawnPoint());
        } else if (args.length == 2) {
            if (Util.matchesPlayer(args[1])) {
                destination = new Location(getPlayer(sender, args[1]));
            } else {
                WorldServer worldServer = DimensionManager.getWorld(parseInt(sender, args[1]));
                if (worldServer == null)
                    throw new PlayerNotFoundException("That dimension doesn't exist");
                destination = new Location(worldServer.provider.dimensionId, worldServer.getSpawnPoint());
            }
        } else if (args.length == 5) {
            WorldServer worldServer = DimensionManager.getWorld(parseInt(sender, args[1]));
            if (worldServer == null)
                throw new PlayerNotFoundException("That dimension doesn't exist");
            destination = new Location(worldServer.provider.dimensionId,
                    parseInt(sender, args[2]), parseInt(sender, args[3]), parseInt(sender, args[4]));
        } else
            throw new WrongUsageException(getCommandUsage(sender));

        ServerUtils.teleportPlayer(target, destination);
        sender.addChatMessage(Util.getChatComponent(String.format("Teleported %s to %s %s %s in Dim %s",
                target.getCommandSenderName(), destination.x, destination.y, destination.z, destination.dimID), EnumChatFormatting.GRAY));
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {

        if (args.length == 1 || args.length == 2)
            return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());

        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int argsLength) {

        return argsLength == 0 || argsLength == 1;
    }
}
