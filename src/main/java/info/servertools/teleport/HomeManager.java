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

import com.google.common.io.Files;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import info.servertools.core.lib.Reference;
import info.servertools.core.util.FileUtils;
import info.servertools.core.util.Location;
import info.servertools.core.util.SaveThread;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeManager {

    /** Stores all player's homes */
    private static Map<UUID, Map<Integer, Location>> userHomeMap = new HashMap<>();

    private static final File homeFile = new File(ServerToolsTeleport.serverToolsTeleportDir, "homes.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /** Used when file IO is preformed on home data */
    private static final Object lock = new Object();

    /**
     * Initialize the home manager
     */
    public static void init() {
        load();
    }


    /**
     * Set a player's home location for a given dimension
     *
     * @param uuid
     *         the player's {@link java.util.UUID UUID}
     * @param dimension
     *         the dimension for the home
     * @param x
     *         the X location
     * @param y
     *         the Y location
     * @param z
     *         the Z location
     */
    public static void setHome(UUID uuid, int dimension, double x, double y, double z) {
        if (!userHomeMap.containsKey(uuid)) {
            userHomeMap.put(uuid, new HashMap<Integer, Location>());
        }
        userHomeMap.get(uuid).put(dimension, new Location(dimension, x, y, z));
        save();
    }

    /**
     * Get a player's home for the given dimension
     *
     * @param uuid
     *         the player's {@link java.util.UUID UUID}
     * @param dimension
     *         the dimension of the home
     *
     * @return the {@link info.servertools.core.util.Location Location} of the home, or {@code null} if no home exists
     */
    @Nullable
    public static Location getHome(UUID uuid, int dimension) {
        if (userHomeMap.containsKey(uuid)) {
            return userHomeMap.get(uuid).get(dimension);
        }
        return null;
    }

    /**
     * Clear a player's home for a given dimension
     *
     * @param uuid
     *         the player's {@link java.util.UUID UUID}
     * @param dimension
     *         the dimension of the home
     *
     * @return {@code true} if the home existed, {@code false} if not
     */
    public static boolean clearHome(UUID uuid, int dimension) {
        if (userHomeMap.containsKey(uuid)) {
            if (userHomeMap.get(uuid).remove(dimension) != null) {
                save();
                return true;
            }
        }
        return false;
    }

    /**
     * Save the home map to file
     */
    private static void save() {
        new SaveThread(gson.toJson(userHomeMap)) {
            @Override
            public void run() {
                synchronized (lock) {
                    FileUtils.writeStringToFile(data, homeFile);
                }
            }
        }.start();
    }

    /**
     * Load the home map from file
     */
    private static void load() {
        if (!homeFile.exists()) return;
        synchronized (lock) {
            try {
                String data = Files.toString(homeFile, Reference.CHARSET);
                userHomeMap = gson.fromJson(data, new TypeToken<Map<UUID, Map<Integer, Location>>>() {}.getType());
            } catch (IOException e) {
                ServerToolsTeleport.log.warn("Failed to load homes from file", e);
            } finally {
                if (userHomeMap == null) {
                    userHomeMap = new HashMap<>();
                }
            }
        }
    }
}