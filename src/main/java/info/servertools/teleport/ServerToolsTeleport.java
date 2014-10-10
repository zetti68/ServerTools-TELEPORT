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

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLFingerprintViolationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import info.servertools.core.ServerTools;
import info.servertools.core.command.CommandManager;
import info.servertools.teleport.command.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

import static info.servertools.core.command.CommandManager.registerSTCommand;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, dependencies = Reference.DEPENDENCIES, acceptableRemoteVersions = "*", certificateFingerprint = Reference.FINGERPRINT)
public class ServerToolsTeleport {

    public EventHandler eventHandler;

    public CommandHome commandHome;
    public CommandTeleport commandTeleport;
    public CommandEditTeleport commandEditTeleport;
    public CommandBack commandBack;
    public CommandTPDim commandTPDim;
    public CommandTeleportRequest commandTeleportRequest;

    @Mod.Instance
    public static ServerToolsTeleport instance;

    public static final File serverToolsTeleportDir = new File(ServerTools.serverToolsDir, "teleport");

    public static final Logger log = LogManager.getLogger(Reference.MOD_ID);

    @Mod.EventHandler
    public void fingerprintViolation(FMLFingerprintViolationEvent event) {
        log.warn("****************************************************");
        log.warn("*     Invalid ST-TELEPORT Fingerprint Detected     *");
        log.warn("****************************************************");
        log.warn("* Expected: " + event.expectedFingerprint);
        log.warn("****************************************************");
        log.warn("* Received: ");
        for (String fingerprint : event.fingerprints) {
            log.warn("*   " + fingerprint);
        }
        log.warn("****************************************************");
        log.warn("*Unpredictable results may occur, please relownload*");
        log.warn("****************************************************");
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        TeleportConfig.init(new File(serverToolsTeleportDir, "teleport.cfg"));
    }

    @Mod.EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {

        commandHome = new CommandHome("home");
        commandTeleport = new CommandTeleport("teleport");
        commandEditTeleport = new CommandEditTeleport("editteleport");
        commandBack = new CommandBack("back");
        commandTPDim = new CommandTPDim("tpdim");
        commandTeleportRequest = new CommandTeleportRequest("tprequest");

        registerSTCommand(commandHome);
        registerSTCommand(commandTeleport);
        registerSTCommand(commandEditTeleport);
        registerSTCommand(commandBack);
        registerSTCommand(commandTPDim);
        registerSTCommand(commandTeleportRequest);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {

        HomeManager.init();
        TeleportManager.init(new File(serverToolsTeleportDir, "teleports.json"));

        if (eventHandler == null) eventHandler = new EventHandler();
    }
}
