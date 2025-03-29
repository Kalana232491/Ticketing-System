class Vendor implements Runnable{
    private final TicketPool ticketPool;
    private final int total_Tickets;
    private final int release_Rate;
    private boolean running = true;

    //Constructor to initialize the Vendor instance with a ticket pool,total tickets to release, and the release rate.
    public Vendor(TicketPool ticketPool, int total_Tickets, int release_Rate){
        this.ticketPool = ticketPool;
        this.total_Tickets = total_Tickets;
        this.release_Rate = release_Rate;
    }

    //The run method executed when the thread starts.
    @Override
    public void run(){
        for (int i = 1; i<= total_Tickets && running; i++){
            // Add a uniquely labeled ticket to the TicketPool
            ticketPool.add_Ticket("Ticket-"+ i);
            try{
                Thread.sleep(release_Rate);
            } catch (InterruptedException e){
                // Restore the interrupt status and exit the loop
                Thread.currentThread().interrupt();
                break;
            }
        }
        ticketPool.vendorCompleted();
    }

    // Notify the TicketPool that this vendor has completed its task
    public void  reset() {
        running = true;
    }
}
