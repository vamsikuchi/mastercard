package com.mastercard.travel.handler;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.BiFunction;

@Service
public class ConnectionsHandler {
    private Map<String, Set<String>> connections = null;
    public static final BiFunction<Set<String>, Set<String>, Set<String>> mergeConnections = (exsistingConnections, newConncetion) -> {
        Set<String> allConnections = new HashSet<>(exsistingConnections);
        allConnections.addAll(newConncetion);
        return allConnections;
    };

    public ConnectionsHandler() {
        this.connections = loadConnections(new ClassPathResource("city.txt"));
    }

    public boolean searchConnection(String firstCity, String secondCity){
        return connections.get(firstCity).contains(secondCity);
    }

    /***
     * Read each lline from the resource and prepare 2 way Map .
     * Each line is split , changed to lower case and trimmed.
     *
     * 2 way map will allow the search easy and faster
     * 2 way map is duplication of data, heavy on memory.
     *
     *
     * This method will return unmodifiable map, so its thread safe to search.
     *
     * @param classPathResource ClassPath resource for the given filePath
     * @return Map of City, Connected Cities.
     */
    private Map<String, Set<String>> loadConnections(ClassPathResource classPathResource){
        Map<String,Set<String>> connectionMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(classPathResource.getInputStream()))){
            String eachLine = reader.readLine();
            if (eachLine != null) {
                String[] cities = eachLine.toLowerCase().split(",");
                String firstCity = cities[0].trim().intern();
                String secondCity = cities[1].trim().intern();
                connectionMap.merge(firstCity,Set.of(secondCity), mergeConnections);
                connectionMap.merge(secondCity.trim(),Set.of(firstCity), mergeConnections);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.unmodifiableMap(connectionMap);
    }







}
