package com.mastercard.travel.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * This class will be initiated by Spring Framework and Single ton class.
 * While creating instance required Connections map is prepared.
 * From application.properties , input source file name is read with property name connections.file.
 */

@Service
public class ConnectionsHandler {

    private List<Set<String>> connections = null;
    private static final Logger logger = LoggerFactory.getLogger(ConnectionsHandler.class);

    private static final BiFunction<Set<String>, Set<String>, Set<String>> mergeConnections = (exsistingConnections, newConncetion) -> {
        Set<String> allConnections = new HashSet<>(exsistingConnections);
        allConnections.addAll(newConncetion);
        return allConnections;
    };

    private static final BiPredicate<Set<String>,List<String>> checkRouteCities = (routeCities,inputCities) ->{
      return routeCities.containsAll(inputCities);
    };

    public static final Predicate<String[]> isValidCities = cities -> {
        return cities != null && cities.length == 2 && cities[0].trim().length() > 0 && cities[1].trim().length() > 0;
    };

    public static final BiPredicate<String,String> isValidInputCities = (city1,city2) ->{
      return StringUtils.isEmpty(city1) || StringUtils.isEmpty(city2) ?Boolean.FALSE: Boolean.TRUE;
    };

    /**
     * Object Creation will fail, if connections file does not exist ot connections file
     *Throws IOException.
     * @throws IOException, throws Exception if input resource reading is failed
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

    /***
     *  Takes two params as inputs and retuen two cities connection status as True/False
     *  If one of the param is not available , return false
     *  If both cities are same, return true
     *  Method input params are not case sensitive.
     * @param firstCity
     * @param secondCity
     * @return Boolean, if both citis connect True else False
     */
    public boolean searchConnection(String firstCity, String secondCity) {
        boolean flag = Boolean.FALSE;
        if(isValidInputCities.test(firstCity,secondCity)) {
            if(firstCity.equalsIgnoreCase(secondCity)) return Boolean.TRUE;
            List<String> inputCities = Arrays.asList(firstCity.trim().toLowerCase(), secondCity.trim().toLowerCase());
            for(int count=0;count<connections.size();count++){
                if(checkRouteCities.test(connections.get(count),inputCities)){
                    flag =Boolean.TRUE;
                    break;
                }
            }
        }
        return flag;
    }


    /***
     * Each line is split , changed to lower case and trimmed.
     *  This method will return unmodifiable List<Set<String>>
     *  Each element of List contains list of cities in a route , so this method will
     *  calculate the list of all connected cities as one List, we can call this as connected cities route

     *  If both cities are not in the list of routes, we create a new route
     *  If one of the city exists on our routes , then search the route and append the new city to existing routes.
     *  If a city is found in multiple routes, both routes are connected, so merge the routes as one route
     *
     * @param classPathResource ClassPath resource for the given filePath
     * @return Map of City, Connected Cities.
     */
    private List<Set<String>> loadConnections(ClassPathResource classPathResource) throws IOException {
        Map<String, Set<String>> connectionMap = new HashMap<>();
        List<String> routes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(classPathResource.getInputStream()))) {
            String eachLine = null;
            List<String> exsistingCities = new ArrayList<>();
            while ((eachLine = reader.readLine()) != null) {
                String[] cities = eachLine.toLowerCase().split(",");
                if (isValidCities.test(cities)) {
                    routes = createOrMergeRoutes(cities[0].trim(),cities[1].trim(),exsistingCities,routes);
                }
            }
            List<Set<String>> connectionRoutes = new ArrayList<>();
            routes.forEach(eachRoute ->{
                connectionRoutes.add(
                        Arrays.asList(eachRoute.split(","))
                                .stream()
                                .filter(s -> s.length() > 0)
                                .collect(Collectors.toSet()));
            }
            );
            return Collections.unmodifiableList(connectionRoutes);
        }

    }

    /***
     *   All new cities list will be maintained (new cities).
     *  If both cities(input pair)  are new its a new route
     *          new route will be saved in the list of routes.
     *  If at least one of the city is not new city from (new cities) ,
     *      then no new route needed.
     *   check the routes list for the city (exsisting city from the pair) and new city to those route(s).
     *   if new cityis added to multiple routes that mean all those routes are connected.
     *
     *   if routes are connected , then all those cities will be merged and created a new route ,
     *   route which are merged will be deleted.
     *
     * @return
     */

    private List<String> createOrMergeRoutes(String city1, String city2, List<String> exsistingCities, List<String> routes){
        if( (!exsistingCities.contains(city1)) && (!exsistingCities.contains(city2))){
            exsistingCities.add(city1);
            exsistingCities.add(city2);
            routes.add(city1+","+city2);
        }else{
            boolean contains = exsistingCities.contains(city1);
            boolean b = !exsistingCities.contains(city2);
            if(contains &&  b) {
                exsistingCities.add(city2);
                routes=  mergeRoutes(city1,city2, routes);
            } else if( (!exsistingCities.contains(city1)) &&  exsistingCities.contains(city2)){
                exsistingCities.add(city1);
                routes= mergeRoutes(city2,city1, routes);
            }else{
               routes= mergeRoutes(city1,city2, routes);
               routes=mergeRoutes(city2,city1, routes);
            }
        }
        return routes;
    }

    private List<String> mergeRoutes(String city1, String city2, List<String> routes) {
        List<Integer> newCityAddedroutesList = new ArrayList<>();
        for(int count=0;count<routes.size();count++){
            if( routes.get(count).contains(city1)){
                String concat = routes.get(count).concat(",").concat(city2);
                routes.set(count,concat);
                newCityAddedroutesList.add(count);
            }
        }
        if(newCityAddedroutesList.size()>1){
            // new city added to multiple routes,
            // so all those routes should be merged and new route should be created by merging these routes
            StringBuilder builder = new StringBuilder();
            for(int count=0;count<newCityAddedroutesList.size();count++) {
                builder.append(routes.get(count)).append(",");
            }
            List<String> newRoutes = new ArrayList<>();

            for(int count=0;count<routes.size();count++) {
                if(!newCityAddedroutesList.contains(count)){
                    newRoutes.add(routes.get(count));
                }
            }
            newRoutes.add(builder.toString());
            return newRoutes;
        }
        return routes;
    }


}
