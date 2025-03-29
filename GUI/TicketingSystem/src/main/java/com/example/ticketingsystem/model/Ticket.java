package com.example.ticketingsystem.model;

import java.security.SecureRandom;

public class Ticket {
    private final int ticketNumber;
    private final String vendorName;
    private final String uniqueId;

    public Ticket(int ticketNumber, String vendorName) {
        this.ticketNumber = ticketNumber;
        this.vendorName = vendorName;
        this.uniqueId = generateUniqueId();
    }

    private String generateUniqueId() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("Ticket-%d (ID: %s) ", ticketNumber, uniqueId);
    }

    // Getters
    public int getTicketNumber() { return ticketNumber; }
    public String getVendorName() { return vendorName; }
    public String getUniqueTicketId() { return uniqueId; }
}