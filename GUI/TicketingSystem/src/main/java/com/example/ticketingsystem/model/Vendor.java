package com.example.ticketingsystem.model;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Vendor implements Runnable {
    protected final TicketPool ticketPool;
    protected final int totalTickets;
    protected final int releaseRate;
    protected final String name;
    protected final AtomicBoolean running = new AtomicBoolean(true);

    public Vendor(TicketPool ticketPool, int totalTickets, int releaseRate, String name) {
        this.ticketPool = ticketPool;
        this.totalTickets = totalTickets;
        this.releaseRate = releaseRate;
        this.name = name;
    }

    @Override
    public void run() {
        for (int i = 1; i <= totalTickets && running.get(); i++) {
            try {
                Ticket ticket = ticketPool.addTicket(this);
                if (ticket != null) {
                    log(name + " added " + ticket);
                }
                Thread.sleep(releaseRate);
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
}