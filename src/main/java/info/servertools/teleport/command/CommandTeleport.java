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

import info.servertools.teleport.TeleportConfig;
import info.servertools.teleport.TeleportManager;
import info.servertools.core.command.CommandLevel;
import info.servertools.core.command.ServerToolsCommand;
import info.servertools.core.util.ChatUtils;
import info.servertools.core.util.Location;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;
import java.util.Set;

public class CommandTeleport extends ServerToolsCommand {

    public CommandTeleport(String defaultName) {
        super(defaultName);
    }

    @Override
    public CommandLevel getCommandLevel() {
        return CommandLevel.ANYONE;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {

        Set<String> entries = TeleportManager.teleportMap.keySet();
        return getListOfStringsMatchingLastWord(par2ArrayOfStr, entries.toArray(new String[entries.size()]));
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {

        return "/" + name + " [teleportname]";
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {

        if (astring.length < 1)
            throw new WrongUsageException(getCommandUsage(icommandsender));

        if (!(icommandsender instanceof EntityPlayer))
            throw new WrongUsageException("This command must be used by a player");

        EntityPlayer player = (EntityPlayer) icommandsender;

        Location teleport = TeleportManager.getTeleport(astring[0]);

        if (teleport != null) {

            TeleportManager.backMap.put(player.getPersistentID(), new Location(player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ));

            if (teleport.dimID != player.worldObj.provider.dimensionId) {
                if (!TeleportConfig.interDimTeleporting) {
                    icommandsender.addChatMessage(ChatUtils.getChatComponent("Teleporting across dimensions isn't allowed", EnumChatFormatting.RED));
                    return;
                }

                player.travelToDimension(teleport.dimID);
            }

            player.setPositionAndUpdate(teleport.x, teleport.y, teleport.z);
            icommandsender.addChatMessage(ChatUtils.getChatComponent(String.format("Teleported to: %s", astring[0]), EnumChatFormatting.GREEN));

        } else {
            icommandsender.addChatMessage(ChatUtils.getChatComponent("That teleport doesn't exist", EnumChatFormatting.RED));
        }
    }
}
