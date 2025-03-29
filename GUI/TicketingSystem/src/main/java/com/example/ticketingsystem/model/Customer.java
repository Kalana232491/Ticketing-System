package com.example.ticketingsystem.model;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Customer implements Runnable {
    protected final TicketPool ticketPool;
    protected final int retrievalRate;
    protected final String name;
    protected final AtomicBoolean running = new AtomicBoolean(true);

    public Customer(TicketPool ticketPool, int retrievalRate, String name) {
        this.ticketPool = ticketPool;
        this.retrievalRate = retrievalRate;
        this.name = name;
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                // Attempt to retrieve a ticket with a short timeout
                Ticket ticket = ticketPool.retrieveTicket();

                if (ticket != null) {
                    log(name + " purchased " + ticket + " from " + ticket.getVendorName());
                    onTicketPurchased();
                } else {
                    // If no ticket is available, wait a bit before trying again
                    TimeUnit.MILLISECONDS.sleep(100);
                }

                // Sleep for the specified retrieval rate after each attempt
                TimeUnit.MILLISECONDS.sleep(retrievalRate);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stop() {
        running.set(false);
    }

    public String getName() {
        return name;
    }

    public abstract void log(String message);

    protected abstract void onTicketPurchased();
}