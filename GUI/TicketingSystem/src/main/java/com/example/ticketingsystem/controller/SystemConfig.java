package com.example.ticketingsystem.controller;

public class SystemConfig {
    private int maxCapacity;
    private int totalTickets;
    private int vendorRate;
    private int customerRate;
    private int vendorCount;
    private int customerCount;

    // Validate method to check all constraints
    public boolean validate() {
        // Check for positive values
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Max capacity must be greater than 0");
        }

        if (totalTickets <= 0) {
            throw new IllegalArgumentException("Total tickets must be greater than 0");
        }

        if (vendorRate <= 0) {
            throw new IllegalArgumentException("Vendor rate must be greater than 0");
        }

        if (customerRate <= 0) {
            throw new IllegalArgumentException("Customer rate must be greater than 0");
        }

        if (vendorCount <= 0) {
            throw new IllegalArgumentException("Vendor count must be greater than 0");
        }

        if (customerCount <= 0) {
            throw new IllegalArgumentException("Customer count must be greater than 0");
        }

        // Check that max capacity is greater than total tickets
        if (maxCapacity <= totalTickets) {
            throw new IllegalArgumentException("Max capacity must be greater than total tickets");
        }

        return true;
    }

    // Existing getters and setters
    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public int getVendorRate() {
        return vendorRate;
    }

    public void setVendorRate(int vendorRate) {
        this.vendorRate = vendorRate;
    }

    public int getCustomerRate() {
        return customerRate;
    }

    public void setCustomerRate(int customerRate) {
        this.customerRate = customerRate;
    }

    public int getVendorCount() {
        return vendorCount;
    }

    public void setVendorCount(int vendorCount) {
        this.vendorCount = vendorCount;
    }

    public int getCustomerCount() {
        return customerCount;
    }

    public void setCustomerCount(int customerCount) {
        this.customerCount = customerCount;
    }
}