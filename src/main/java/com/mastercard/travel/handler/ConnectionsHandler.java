package com.mastercard.travel.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class will be initiated by Spring Framework and Single ton class.
 * While creating instance required Connections map is prepared.
 * From application.properties , input source file name is read with property name connections.file.
 */

@Service
public class ConnectionsHandler {

    private Map<String, Set<String>> connections = null;
    private static final Logger logger = LoggerFactory.getLogger(ConnectionsHandler.class);

    private static final BiFunction<Set<String>, Set<String>, Set<String>> mergeConnections = (exsistingConnections, newConncetion) -> {
        Set<String> allConnections = new HashSet<>(exsistingConnections);
        allConnections.addAll(newConncetion);
        return allConnections;
    };

    public static final Predicate<String[]> isValidCities = cities -> {
        return cities != null && cities.length == 2 && cities[0].trim().length() > 0 && cities[1].trim().length() > 0;
    };

    /**
     * Object Creation will fail, if connections file does not exist ot connections file
     *Throws IOException.
     * @throws IOException
     */
    public ConnectionsHandler(@Value("${connections.file}") String filePath) throws IOException {
        logger.debug(" Connection loading completed." + filePath);
        try {
            this.connections = loadConnections(new ClassPathResource(filePath));
            logger.debug(" Connection loading completed.");
        } catch (IOException ex) {
            logger.error(" Exception occurred while reading connections file " + filePath);
            throw ex;
        }
    }

    public boolean searchConnection(String firstCity, String secondCity) {
        logger.debug("Connections : " + connections);
        Set<String> cityConnections = connections.get(firstCity);
        return cityConnections!=null? cityConnections.contains(secondCity):Boolean.FALSE;
    }


    /***
     * Read each lline from the resource and prepare 2 way Map .
     * Each line is split , changed to lower case and trimmed.
     * 2 way map will allow the search easy and faster
     * 2 way map is duplication of data, heavy on memory.
     * This method will return unmodifiable map.
     *
     * @param classPathResource ClassPath resource for the given filePath
     * @return Map of City, Connected Cities.
     */
    private Map<String, Set<String>> loadConnections(ClassPathResource classPathResource) throws IOException {
        Map<String, Set<String>> connectionMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(classPathResource.getInputStream()))) {
            String eachLine = null;
            while ((eachLine = reader.readLine()) != null) {
                String[] cities = eachLine.toLowerCase().split(",");
                if (isValidCities.test(cities)) {
                    String firstCity = cities[0].trim();
                    String secondCity = cities[1].trim();
                    connectionMap.merge(firstCity, Set.of(secondCity), mergeConnections);
                    connectionMap.merge(secondCity.trim(), Set.of(firstCity), mergeConnections);
                }
            }
            return Collections.unmodifiableMap(connectionMap);
        }
    }

}
