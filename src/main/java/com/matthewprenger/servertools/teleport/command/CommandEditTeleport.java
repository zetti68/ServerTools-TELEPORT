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
import com.matthewprenger.servertools.core.util.Location;
import com.matthewprenger.servertools.teleport.TeleportManager;
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
    public int getRequiredPermissionLevel() {
        return 2;
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
            icommandsender.addChatMessage(Util.getChatComponent(String.format("Set teleport: %s", astring[1]), EnumChatFormatting.GREEN));

        } else if ("delete".equalsIgnoreCase(astring[0])) {

            if (TeleportManager.removeTeleport(astring[1])) {
                icommandsender.addChatMessage(Util.getChatComponent(String.format("Removed teleport: %s", astring[1]), EnumChatFormatting.GREEN));
            } else
                throw new PlayerNotFoundException("That teleport doesn't exist");

        } else
            throw new WrongUsageException(getCommandUsage(icommandsender));
    }
}
