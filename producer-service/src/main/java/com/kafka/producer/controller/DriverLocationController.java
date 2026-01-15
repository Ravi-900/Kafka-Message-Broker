package com.kafka.producer.controller;

import com.kafka.producer.model.DriverLocation;
import com.kafka.producer.service.DriverLocationPublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DriverLocationController {

    @Autowired
    DriverLocationPublisherService locationPublisherService;

    @PostMapping("/updateLocation")
    public HttpStatus updateLocation(@RequestBody DriverLocation location) {
        locationPublisherService.publishLocation(location);
        return HttpStatus.OK;
    }
}
