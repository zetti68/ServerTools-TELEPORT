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

package com.matthewprenger.servertools.teleport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.matthewprenger.servertools.core.util.FileUtils;
import com.matthewprenger.servertools.core.util.Location;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TeleportManager {

    public static final Map<String, Location> backMap = new HashMap<>();

    public static Map<String, Location> teleportMap = new HashMap<>();
    private static File teleportSaveFile;
    private static Gson gson;

    public static void init(File saveFile) {

        teleportSaveFile = saveFile;
        gson = new GsonBuilder().setPrettyPrinting().create();

        loadTeleportFile();
    }

    public static void setTeleport(String name, Location location) {

        teleportMap.put(name, location);

        saveTeleportFile();
    }

    public static Location getTeleport(String name) {

        if (teleportMap.containsKey(name)) {
            return teleportMap.get(name);
        }

        return null;
    }

    public static boolean removeTeleport(String name) {

        if (teleportMap.containsKey(name)) {
            teleportMap.remove(name);
            saveTeleportFile();
            return true;
        }

        return false;
    }

    private static void saveTeleportFile() {

        try {
            FileUtils.writeStringToFile(gson.toJson(teleportMap), teleportSaveFile);
        } catch (IOException e) {
            e.printStackTrace();
            ServerToolsTeleport.log.log(Level.WARN, "Failed to save teleport file");
        }
    }

    private static void loadTeleportFile() {

        if (!teleportSaveFile.exists()) {
            ServerToolsTeleport.log.log(Level.TRACE, "Teleport save file doesn't exist, skipping it");
            return;
        }

        teleportMap.clear();

        try {

            FileReader fileReader = new FileReader(teleportSaveFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            Type type = new TypeToken<Map<String, Location>>() {
            }.getType();

            Map<String, Location> map = gson.fromJson(bufferedReader, type);

            if (map != null)
                teleportMap = map;

            bufferedReader.close();

        } catch (JsonParseException e) {
            e.printStackTrace();
            ServerToolsTeleport.log.log(Level.WARN, String.format("The teleport file %s could not be parsed as valid JSON, it will not be loaded", teleportSaveFile.getAbsolutePath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ServerToolsTeleport.log.log(Level.WARN, String.format("Tried to load non-existant file: %s", teleportSaveFile.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
            ServerToolsTeleport.log.log(Level.WARN, String.format("Failed to close buffered reader stream for: %s", teleportSaveFile.getAbsolutePath()));
        }
    }
}
