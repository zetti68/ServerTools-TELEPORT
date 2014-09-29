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

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class TeleportConfig {

    public static boolean ENABLE_TELEPORT_ACROSS_DIMENSION;
    public static boolean REQUIRE_OP_EDIT_TELEPORT;

    public static void init(File configFile) {

        Configuration teleportConfig = new Configuration(configFile);

        try {
            teleportConfig.load();

            ENABLE_TELEPORT_ACROSS_DIMENSION = teleportConfig.get(Configuration.CATEGORY_GENERAL, "Enable Cross-Dimension Teleport", false, "Enables teleports to be available across dimensions").getBoolean(false);
            REQUIRE_OP_EDIT_TELEPORT = teleportConfig.get(Configuration.CATEGORY_GENERAL, "Require OP Edit Teleport", true, "Only server operators can edit teleports").getBoolean(true);

        } catch (Exception e) {
            ServerToolsTeleport.log.fatal("Failed to load Teleport config", e);
        } finally {
            if (teleportConfig.hasChanged()) {
                teleportConfig.save();
            }
        }
    }
}
