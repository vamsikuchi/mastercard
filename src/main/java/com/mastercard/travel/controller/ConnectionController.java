package com.mastercard.travel.controller;


import com.mastercard.travel.handler.ConnectionsHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class ConnectionController {

    @Autowired
    ConnectionsHandler connectionsHandler;

    @GetMapping("/connected")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public String isConnected(@RequestParam(name = "origin") String origin,
                              @RequestParam(name = "destination") String destination){
        return connectionsHandler.searchConnection(origin,destination) ?"yes":"no";
    }

}
