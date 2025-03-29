package com.example.ticketingsystem.controller;

import com.example.ticketingsystem.service.ConfigurationService;
import com.example.ticketingsystem.service.TicketingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/ticketingsystem")
@CrossOrigin(origins = "http://localhost:3000")
public class TicketingController {
    private final TicketingService ticketingService;
    private final ConfigurationService configurationService;

    public TicketingController(TicketingService ticketingService, ConfigurationService configurationService) {
        this.ticketingService = ticketingService;
        this.configurationService = configurationService;
    }

    @GetMapping("/load-config")
    public SystemConfig loadConfiguration() {
        return configurationService.loadConfiguration();
    }

    @PostMapping("/initialize")
    public String initializeSystem(@RequestBody SystemConfig config) {
        try {
            // Validate the configuration
            config.validate();

            // Save the configuration to JSON
            configurationService.saveConfiguration(config);

            // If validation passes, initialize the system
            ticketingService.initializeSystem(
                    config.getMaxCapacity(),
                    config.getTotalTickets(),
                    config.getVendorRate(),
                    config.getCustomerRate(),
                    config.getVendorCount(),
                    config.getCustomerCount()
            );
            return "System Initialized";
        } catch (IllegalArgumentException e) {
            // Convert validation errors to HTTP Bad Request
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage(),
                    e
            );
        }
    }

    @PostMapping("/start")
    public String startSystem() {
        ticketingService.startSystem();
        return "System Started";
    }

    @PostMapping("/stop")
    public String stopSystem() {
        ticketingService.stopSystem();
        return "System Stopped";
    }

    @GetMapping("/tickets")
    public int getAvailableTickets() {
        return ticketingService.getAvailableTickets();
    }

    @GetMapping("/logs")
    public List<String> getSystemLogs() {
        return ticketingService.getSystemLogs();
    }

    @GetMapping("/soldout")
    public boolean checkSoldOut() {
        return ticketingService.isAllTicketsSoldOut();
    }
}