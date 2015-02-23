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

import com.google.common.collect.ImmutableList;
import gnu.trove.map.hash.THashMap;
import info.servertools.core.command.CommandLevel;
import info.servertools.core.command.ServerToolsCommand;
import info.servertools.core.util.Location;
import info.servertools.core.util.ServerUtils;
import info.servertools.teleport.TeleportConfig;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CommandTeleportRequest extends ServerToolsCommand {

    /**
     * Stores the teleport requests
     * <br>
     * Keys are the UUIDs of the players who the request is for<br>
     * Values are the UUIDs of the requesting players
     */
    private final Map<UUID, UUID> requestMap = new THashMap<>();

    public CommandTeleportRequest(String defaultName) {
        super(defaultName);
    }

    @Override
    public CommandLevel getCommandLevel() {
        return CommandLevel.ANYONE;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + name + " [USERNAME] |OR| /" + name + " -a / -d | accept / deny";
    }

    @Override
    public List<String> getCommandAliases() {
        return ImmutableList.of("tpr");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            if (args[0].startsWith("-")) return getListOfStringsMatchingLastWord(args, "-a", "-d");
            else return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        } else if (args.length == 0) {
            return Arrays.asList(MinecraftServer.getServer().getAllUsernames());
        } else {
            return null;
        }
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        if (args.length == 1 && args[0].startsWith("-")) return false;
        else if (index == 0) return true;
        return false;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) throw new WrongUsageException("This command must be used by a player");
        if (args.length != 1) throw new WrongUsageException(getCommandUsage(sender));

        EntityPlayerMP senderPlayer = (EntityPlayerMP) sender;

        switch (args[0]) {
            case "-a":
            case "-accept":
                if (requestMap.containsKey(senderPlayer.getPersistentID())) {
                    EntityPlayerMP requestor = ServerUtils.getPlayerForUUID(requestMap.remove(senderPlayer.getPersistentID()));
                    if (requestor == null) throw new PlayerNotFoundException("Requesting player is no longer online");
                    teleportIfPossible(requestor, senderPlayer);
                    addChatMessage(senderPlayer, String.format("Teleporting %s to you", requestor.getDisplayName()));
                    addChatMessage(requestor, String.format("%s has accepted your TP request, teleporting now", senderPlayer.getDisplayName()));
                } else {
                    throw new PlayerNotFoundException("You don't have a pending TP request");
                }
                break;
            case "-d":
            case "-deny":
                if (requestMap.containsKey(senderPlayer.getPersistentID())) {
                    EntityPlayerMP requestor = ServerUtils.getPlayerForUUID(requestMap.get(senderPlayer.getPersistentID()));
                    if (requestor != null) {
                        addChatMessage(requestor, senderPlayer.getDisplayName() + " has denied your TP request");
                    }
                    requestMap.remove(senderPlayer.getPersistentID());
                    addChatMessage(senderPlayer, "Denied TP request");
                } else {
                    throw new PlayerNotFoundException("You don't have a pending TP request");
                }
                break;
            default: /* Creating a request */
                EntityPlayerMP toPlayer = getPlayer(sender, args[0]);
                requestMap.put(toPlayer.getPersistentID(), senderPlayer.getPersistentID());
                addChatMessage(toPlayer, String.format("%s has requested to teleport to you", senderPlayer.getDisplayName()));
                addChatMessage(toPlayer, String.format("Type /%s -a to accept, or /%s -d to deny", name, name));
                addChatMessage(senderPlayer, "Requested teleport from " + toPlayer.getDisplayName());
        }
    }

    private static void teleportIfPossible(EntityPlayerMP requester, EntityPlayerMP target) throws WrongUsageException {
        if (requester.worldObj.provider.dimensionId != target.worldObj.provider.dimensionId && !TeleportConfig.interDimTPRequest) {
            throw new WrongUsageException("Teleport requests across dimensions have been disabled");
        }

        ServerUtils.teleportPlayer(requester, new Location(target));
    }
}
