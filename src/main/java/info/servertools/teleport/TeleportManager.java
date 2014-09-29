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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import info.servertools.core.lib.Reference;
import info.servertools.core.util.FileUtils;
import info.servertools.core.util.Location;
import info.servertools.core.util.SaveThread;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager {

    /** Stores locations for using /back */
    public static final Map<UUID, Location> backMap = new HashMap<>();

    /** Stores all server teleports */
    public static Map<String, Location> teleportMap = new HashMap<>();

    private static File teleportSaveFile;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /** Used when file I/O is performed on the teleports */
    private static final Object lock = new Object();

    /**
     * Initialize the teleport manager
     *
     * @param saveFile
     *         the file to save teleports to
     */
    public static void init(File saveFile) {
        teleportSaveFile = saveFile;
        loadTeleportFile();
    }

    /**
     * Set a teleport
     *
     * @param name
     *         the teleport name
     * @param location
     *         the teleport's {@link info.servertools.core.util.Location Location}
     */
    public static void setTeleport(String name, Location location) {
        teleportMap.put(name, location);
        saveTeleportFile();
    }

    /**
     * Get a teleport
     *
     * @param name
     *         the teleport name
     *
     * @return the {@link info.servertools.core.util.Location Location} of the teleport, or {@code null} if no teleport was found
     */
    @Nullable
    public static Location getTeleport(String name) {
        return teleportMap.get(name);
    }

    /**
     * Remove a teleport
     *
     * @param name
     *         the teleport name
     *
     * @return {@code true} if the teleport existed, {@code false} otherwise
     */
    public static boolean removeTeleport(String name) {
        if (teleportMap.remove(name) != null) {
            saveTeleportFile();
            return true;
        }
        return false;
    }

    private static void saveTeleportFile() {
        new SaveThread(gson.toJson(teleportMap)) {
            @Override
            public void run() {
                synchronized (lock) {
                    FileUtils.writeStringToFile(data, teleportSaveFile);
                }
            }
        }.start();
    }

    private static void loadTeleportFile() {

        if (!teleportSaveFile.exists()) {
            ServerToolsTeleport.log.log(Level.TRACE, "Teleport save file doesn't exist, skipping it");
            return;
        }

        teleportMap.clear();

        synchronized (lock) {
            try {
                String data = Files.toString(teleportSaveFile, Reference.CHARSET);
                Type type = new TypeToken<Map<String, Location>>() {}.getType();

                Map<String, Location> map = gson.fromJson(data, type);

                if (map != null)
                    teleportMap = map;

            } catch (JsonParseException e) {
                ServerToolsTeleport.log.warn(String.format("The teleport file %s could not be parsed as valid JSON, it will not be loaded", teleportSaveFile.getAbsolutePath()), e);
            } catch (FileNotFoundException e) {
                ServerToolsTeleport.log.warn(String.format("Tried to load non-existant file: %s", teleportSaveFile.getAbsolutePath()), e);
            } catch (IOException e) {
                ServerToolsTeleport.log.warn(String.format("Failed to close buffered reader stream for: %s", teleportSaveFile.getAbsolutePath()), e);
            }
        }
    }
}
