package com.example.ticketingsystem.service;

import com.example.ticketingsystem.model.Customer;
import com.example.ticketingsystem.model.TicketPool;
import com.example.ticketingsystem.model.Vendor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TicketingService {
    private TicketPool ticketPool;
    private ExecutorService vendorExecutor;
    private ExecutorService customerExecutor;
    private final List<Vendor> vendors = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<String> systemLogs = new CopyOnWriteArrayList<>();
    private AtomicInteger totalTicketsPurchased = new AtomicInteger(0);
    private int totalTickets;
    private boolean allTicketsSoldOut = false;

    public void initializeSystem(int maxCapacity, int totalTickets, int vendorRate, int customerRate, int vendorCount, int customerCount) {
        // Shutdown existing threads if any
        shutdownThreads();

        // Reset logs and counters
        systemLogs.clear();
        totalTicketsPurchased.set(0);
        this.totalTickets = totalTickets;
        this.allTicketsSoldOut = false;

        ticketPool = new TicketPool(maxCapacity);
        vendorExecutor = Executors.newFixedThreadPool(vendorCount);
        customerExecutor = Executors.newFixedThreadPool(customerCount);

        // Distribute tickets evenly among vendors
        int ticketsPerVendor = totalTickets / vendorCount;
        int remainingTickets = totalTickets % vendorCount;

        // Create vendors
        for (int i = 0; i < vendorCount; i++) {
            int vendorTickets = ticketsPerVendor + (i < remainingTickets ? 1 : 0);
            Vendor vendor = new Vendor(ticketPool, vendorTickets, vendorRate, "Vendor-" + (i + 1)) {
                @Override
                public void log(String message) {
                    systemLogs.add(message);
                }
            };
            vendors.add(vendor);
        }
        
        // Create customers
        for (int i = 0; i < customerCount; i++) {
            Customer customer = new Customer(ticketPool, customerRate, "Customer-" + (i + 1)) {
                @Override
                public void log(String message) {
                    systemLogs.add(message);
                }

                @Override
                protected void onTicketPurchased() {
                    int purchasedCount = totalTicketsPurchased.incrementAndGet();
                    if (purchasedCount >= totalTickets) {
                        setAllTicketsSoldOut(true);
                        stopSystem();
                    }
                }
            };
            customers.add(customer);
        }
    }

    public void startSystem() {
        // Submit vendors and customers to their respective executors
        vendors.forEach(vendorExecutor::submit);
        customers.forEach(customerExecutor::submit);

        // Add a monitoring thread to automatically stop when all tickets are sold
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                while (!allTicketsSoldOut) {
                    Thread.sleep(1000); // Check every second
                    if (totalTicketsPurchased.get() >= totalTickets) {
                        stopSystem();
                        break;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    public void stopSystem() {
        // Stop vendors and customers
        vendors.forEach(Vendor::stop);
        customers.forEach(Customer::stop);

        // Shutdown executors
        shutdownThreads();
    }

    private void shutdownThreads() {
        if (vendorExecutor != null) {
            vendorExecutor.shutdown();
            try {
                if (!vendorExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
                    vendorExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                vendorExecutor.shutdownNow();
            }
        }

        if (customerExecutor != null) {
            customerExecutor.shutdown();
            try {
                if (!customerExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
                    customerExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                customerExecutor.shutdownNow();
            }
        }

        vendors.clear();
        customers.clear();
    }

    public int getAvailableTickets() {
        return ticketPool.getAvailableTickets();
    }

    public List<String> getSystemLogs() {
        return new ArrayList<>(systemLogs);
    }

    private synchronized void setAllTicketsSoldOut(boolean status) {
        this.allTicketsSoldOut = status;
        if (status) {
            systemLogs.add("ALL TICKETS SOLD OUT!");
            stopSystem(); // Ensure system stops when all tickets are sold
        }
    }

    public boolean isAllTicketsSoldOut() {
        return allTicketsSoldOut;
    }
}