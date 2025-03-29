import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

class TicketPool {
    private final Queue<String> ticket_List;      // Queue to store tickets add by vendors
    private final int max_Capacity;
    private final CountDownLatch vendor_Completion_Latch;       // CountDownLatch to track vendor completion status

    //Constructor to initialize the TicketPool with a maximum capacity and the number of vendors.
    public TicketPool(int max_Capacity,int vendors_count){
        this.ticket_List = new LinkedList<>();
        this.max_Capacity = max_Capacity;
        this.vendor_Completion_Latch = new CountDownLatch(vendors_count);
    }

    //Adds a ticket to the pool. Waits if the pool is full.
    public synchronized void add_Ticket(String ticket){
        while (ticket_List.size() >= max_Capacity){
            try{
                wait();
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                return;
            }
        }

        // Add the ticket to the pool
        ticket_List.add(ticket);
        System.out.println(Thread.currentThread().getName() +" added ticket: "+ ticket);
        System.out.println("Available tickets:"+ ticket_List.size());
        notifyAll();// Notify waiting threads that a ticket has been added
    }

    //Retrieves a ticket from the pool. Waits if the pool is empty and vendors are still adding tickets.
    public synchronized String retrieve_Ticket() {
        while (ticket_List.isEmpty() && vendor_Completion_Latch.getCount() > 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();// Restore the interrupt status and exit the method
                return null;
            }
        }

        // Return null if no tickets are available and all vendors are done
        if (ticket_List.isEmpty()) {
            return null;
        }

        // Retrieve and remove a ticket from the pool
        String ticket = ticket_List.poll();
        System.out.println("Customer purchased: " + ticket);
        System.out.println("Available tickets: " + ticket_List.size());
        notifyAll();// Notify waiting threads that a ticket has been removed
        return ticket;
    }

    //Signals that a vendor has completed adding tickets.
    public void vendorCompleted() {
        vendor_Completion_Latch.countDown();
    }

    public boolean areAllVendorsComplete() {
        return vendor_Completion_Latch.getCount() == 0;
    }

}
