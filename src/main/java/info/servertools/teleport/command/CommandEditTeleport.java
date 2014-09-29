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

import info.servertools.teleport.TeleportManager;
import info.servertools.core.command.CommandLevel;
import info.servertools.core.command.ServerToolsCommand;
import info.servertools.core.util.ChatUtils;
import info.servertools.core.util.Location;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;
import java.util.Set;

public class CommandEditTeleport extends ServerToolsCommand {

    public CommandEditTeleport(String defaultName) {
        super(defaultName);
    }

    @Override
    public CommandLevel getCommandLevel() {
        return CommandLevel.OP;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {


        if (par2ArrayOfStr.length == 1 && "delete".equalsIgnoreCase(par2ArrayOfStr[0])) {
            Set<String> var = TeleportManager.teleportMap.keySet();
            return getListOfStringsMatchingLastWord(par2ArrayOfStr, var.toArray(new String[var.size()]));
        }

        return null;
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {

        return "/" + name + " [set|delete] [teleportname]";
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {

        if (astring.length < 2)
            throw new WrongUsageException(getCommandUsage(icommandsender));

        if (!(icommandsender instanceof EntityPlayer))
            throw new WrongUsageException("This command must be used by a player");

        EntityPlayer player = (EntityPlayer) icommandsender;

        if ("set".equalsIgnoreCase(astring[0])) {

            TeleportManager.setTeleport(astring[1], new Location(player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ));
            icommandsender.addChatMessage(ChatUtils.getChatComponent(String.format("Set teleport: %s", astring[1]), EnumChatFormatting.GREEN));

        } else if ("delete".equalsIgnoreCase(astring[0])) {

            if (TeleportManager.removeTeleport(astring[1])) {
                icommandsender.addChatMessage(ChatUtils.getChatComponent(String.format("Removed teleport: %s", astring[1]), EnumChatFormatting.GREEN));
            } else
                throw new PlayerNotFoundException("That teleport doesn't exist");

        } else
            throw new WrongUsageException(getCommandUsage(icommandsender));
    }
}
