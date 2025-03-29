package com.example.ticketingsystem.model;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TicketPool {
    private final ConcurrentLinkedQueue<Ticket> tickets;
    private final int maxCapacity;
    private final AtomicInteger ticketCounter;

    public TicketPool(int maxCapacity) {
        this.tickets = new ConcurrentLinkedQueue<>();
        this.maxCapacity = maxCapacity;
        this.ticketCounter = new AtomicInteger(0);
    }

    public synchronized Ticket addTicket(Vendor vendor) {
        if (tickets.size() >= maxCapacity) {
            return null;
        }
        int ticketNumber = ticketCounter.incrementAndGet();
        Ticket ticket = new Ticket(ticketNumber, vendor.getName());
        tickets.offer(ticket);
        return ticket;
    }

    public synchronized Ticket retrieveTicket() {
        // Additional check to ensure thread-safety
        if (tickets.isEmpty()) {
            return null;
        }
        return tickets.poll();
    }

    public synchronized int getAvailableTickets() {
        return tickets.size();
    }
}