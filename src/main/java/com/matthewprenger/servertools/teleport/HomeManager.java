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

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class HomeManager {

    private static final Map<String, Map<Integer, Location>> userHomeMap = new HashMap<>();
    private static File homeDir;
    private static Gson gson;

    public static void init(File homeDirectory) {

        gson = new GsonBuilder().setPrettyPrinting().create();
        homeDir = homeDirectory;
        if (homeDir.mkdirs()) {
            ServerToolsTeleport.log.trace(String.format("Creating home directory: %s", homeDir.getAbsolutePath()));
        }

        loadHomes();
    }

    private static void saveHome(String username) {

        if (userHomeMap.containsKey(username)) {
            if (!(userHomeMap.get(username).isEmpty())) {
                ServerToolsTeleport.log.info(String.format("Saving user home file for %s", username));
                String gsonRepresentation = gson.toJson(userHomeMap.get(username));
                try {
                    FileUtils.writeStringToFile(gsonRepresentation, new File(homeDir, username + ".json"));
                } catch (IOException e) {
                    ServerToolsTeleport.log.warn(String.format("Failed to save %s's homes to file", username));
                    e.printStackTrace();
                }
            }
        }
    }

    public static void setHome(String username, int dimension, double x, double y, double z) {

        if (!userHomeMap.containsKey(username)) {
            userHomeMap.put(username, new HashMap<Integer, Location>());
        }

        userHomeMap.get(username).put(dimension, new Location(dimension, x, y, z));

        saveHome(username);
    }

    public static Location getHome(String username, int dimension) {

        if (userHomeMap.containsKey(username)) {
            if (userHomeMap.get(username).containsKey(dimension)) {
                return userHomeMap.get(username).get(dimension);
            }
        }

        return null;
    }

    public static boolean clearHome(String username, int dimension) {

        if (userHomeMap.containsKey(username)) {
            if (userHomeMap.get(username).containsKey(dimension)) {
                userHomeMap.get(username).remove(dimension);
                saveHome(username);
                return true;
            }
        }

        return false;
    }

    private static void loadHomes() {

        File[] fileList = homeDir.listFiles();

        if (fileList == null || fileList.length == 0)
            return;

        userHomeMap.clear();

        for (File file : fileList) {
            if (file.getName().endsWith(".json")) {
                String username = file.getName().substring(0, file.getName().length() - 5);

                try {

                    FileReader reader = new FileReader(file);
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    Type type = new TypeToken<Map<Integer, Location>>() {
                    }.getType();

                    Map<Integer, Location> map = gson.fromJson(bufferedReader, type);

                    if (map != null)
                        userHomeMap.put(username, map);

                    bufferedReader.close();

                } catch (JsonParseException e) {
                    e.printStackTrace();
                    ServerToolsTeleport.log.warn(String.format("The home file for %s could not be parsed as json, it will not be loaded", username));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    ServerToolsTeleport.log.warn(String.format("Tried to load home file for %s, but it didn't exist", username));
                } catch (IOException e) {
                    e.printStackTrace();
                    ServerToolsTeleport.log.warn(String.format("Failed to close buffered reader stream for: %s", file.getAbsolutePath()));
                }
            }
        }
    }
}