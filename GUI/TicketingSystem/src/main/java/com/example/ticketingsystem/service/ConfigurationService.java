package com.example.ticketingsystem.service;

import com.example.ticketingsystem.controller.SystemConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class ConfigurationService {
    private static final String CONFIG_FILE_PATH = "system-config.json";
    private final ObjectMapper objectMapper;

    public ConfigurationService() {
        this.objectMapper = new ObjectMapper();
    }

    public SystemConfig loadConfiguration() {
        File configFile = new File(CONFIG_FILE_PATH);

        if (configFile.exists()) {
            try {
                return objectMapper.readValue(configFile, SystemConfig.class);
            } catch (IOException e) {
                // Log the error, but return a default configuration
                System.err.println("Error reading configuration file: " + e.getMessage());
                return createDefaultConfiguration();
            }
        }

        // If no config file exists, return default configuration
        return createDefaultConfiguration();
    }

    public void saveConfiguration(SystemConfig config) {
        try {
            // Validate the configuration before saving
            config.validate();

            // Create the file if it doesn't exist
            File configFile = new File(CONFIG_FILE_PATH);

            // Write the configuration to the file
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(configFile, config);

            System.out.println("Configuration saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving configuration: " + e.getMessage());
        }
    }

    private SystemConfig createDefaultConfiguration() {
        SystemConfig defaultConfig = new SystemConfig();

        // Set default values matching the frontend defaults
        defaultConfig.setMaxCapacity(20);
        defaultConfig.setTotalTickets(10);
        defaultConfig.setVendorRate(1000);
        defaultConfig.setCustomerRate(2000);
        defaultConfig.setVendorCount(2);
        defaultConfig.setCustomerCount(2);

        return defaultConfig;
    }
}
