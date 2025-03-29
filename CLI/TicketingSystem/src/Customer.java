class Customer implements Runnable {
    private final TicketPool ticketPool;
    private final int retrieval_Rate;
    private final int total_Tickets_Purchase;
    private int tickets_Purchased = 0;
    private boolean running = true;

    //Constructor to initialize the Customer instance with a ticket pool,retrieval rate, and total tickets to purchase.
    public Customer(TicketPool ticketPool, int retrieval_Rate, int total_Tickets_Purchase){
        this.ticketPool = ticketPool;
        this.retrieval_Rate = retrieval_Rate;
        this.total_Tickets_Purchase = total_Tickets_Purchase;
    }

    //The run method executed when the thread starts.
    @Override
    public void run() {
        while (running && tickets_Purchased < total_Tickets_Purchase) {
            // Attempt to retrieve a ticket from the pool
            String ticket = ticketPool.retrieve_Ticket();
            if (ticket != null) {
                tickets_Purchased++;
            }

            // Exit the loop if all vendors are done and no ticket was retrieved
            if (ticketPool.areAllVendorsComplete() && ticket == null) {
                break;
            }

            try {
                Thread.sleep(retrieval_Rate);
            } catch (InterruptedException e) {
                // Restore the interrupt status and break out of the loop
                Thread.currentThread().interrupt();
                break;
            }
        }
        stop();
    }

    //Stops the thread by setting the running flag to false.
    public void stop(){
        running = false;
    }

    public void reset(){
        running = true;
        tickets_Purchased = 0;
    }
}
