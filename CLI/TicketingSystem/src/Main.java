import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {       // Defines entry point
    private static final String save_File ="config_save_file.txt";

    public static void main(String[] args) {
        Scanner Input = new Scanner(System.in);

        System.out.println("Real-Time Ticketing System");

        // Variable Declarations
        int total_Tickets;
        int tickets_Release_Rate;
        int customers_Retrieval_Rate;
        int max_Ticket_Capacity;
        int customers_Count;
        int vendors_Count;

        File config_Save_File = new File(save_File);
        if(config_Save_File.exists()){          // Check config file
            System.out.print("Do you want to import configuration data from file? (yes/no) :");
            String choice = Input.nextLine().trim().toLowerCase();       // convert lowercase and remove spaces
            if(choice.equals("yes")){
                try(BufferedReader file_Data = new BufferedReader(new FileReader(config_Save_File))) {       // Get data and partitioning data
                    total_Tickets = Integer.parseInt(file_Data.readLine());
                    customers_Retrieval_Rate = Integer.parseInt(file_Data.readLine());
                    tickets_Release_Rate = Integer.parseInt(file_Data.readLine());
                    max_Ticket_Capacity = Integer.parseInt(file_Data.readLine());
                    System.out.println("Configuration imported successfully.");
                } catch (IOException |NumberFormatException e){
                    System.out.println("Import configuration failed. Please enter data manually.");        // Get data from user
                    total_Tickets = ask_For_Input(Input, "Enter Total Tickets: ", 1, Integer.MAX_VALUE);
                    tickets_Release_Rate = ask_For_Input(Input, "Enter Ticket Release Rate (milliseconds): ", 100, Integer.MAX_VALUE);
                    customers_Retrieval_Rate = ask_For_Input(Input, "Enter Customer Retrieval Rate (milliseconds): ", 100, Integer.MAX_VALUE);
                    max_Ticket_Capacity = ask_For_Input(Input, "Enter Max Ticket Capacity: ", total_Tickets + 1, Integer.MAX_VALUE);
                }
            } else {
                total_Tickets = ask_For_Input(Input, "Enter Total Tickets: ", 1, Integer.MAX_VALUE);          // Get data from user
                tickets_Release_Rate = ask_For_Input(Input, "Enter Ticket Release Rate (milliseconds): ", 100, Integer.MAX_VALUE);
                customers_Retrieval_Rate = ask_For_Input(Input, "Enter Customer Retrieval Rate (milliseconds): ", 100, Integer.MAX_VALUE);
                max_Ticket_Capacity = ask_For_Input(Input, "Enter Max Ticket Capacity: ", total_Tickets + 1, Integer.MAX_VALUE);
            }
        } else {
            total_Tickets = ask_For_Input(Input, "Enter Total Tickets: ", 1, Integer.MAX_VALUE);         // Get data from user
            tickets_Release_Rate = ask_For_Input(Input, "Enter Ticket Release Rate (milliseconds): ", 100, Integer.MAX_VALUE);
            customers_Retrieval_Rate = ask_For_Input(Input, "Enter Customer Retrieval Rate (milliseconds): ", 100, Integer.MAX_VALUE);
            max_Ticket_Capacity = ask_For_Input(Input, "Enter Max Ticket Capacity: ", total_Tickets + 1, Integer.MAX_VALUE);
        }

        vendors_Count = ask_For_Input(Input, "Enter Number of Vendors: ", 1, 10);
        customers_Count = ask_For_Input(Input, "Enter Number of Customers: ", 1, 50);

        // Save configuration data to file.
        save_Config_to_file(total_Tickets, tickets_Release_Rate, customers_Retrieval_Rate, max_Ticket_Capacity);

        //Initialize shared ticket pool
        TicketPool ticketPool = new TicketPool(max_Ticket_Capacity , vendors_Count);

        List<Thread> vendor_Threads = new ArrayList<>();
        List<Thread> customer_Threads = new ArrayList<>();
        List<Vendor> vendors = new ArrayList<>();
        List<Customer> customers = new ArrayList<>();

        //Create threads
        for (int i = 0; i < vendors_Count; i++) {
            Vendor vendor = new Vendor(ticketPool, total_Tickets / vendors_Count, tickets_Release_Rate);
            Thread vendor_Thread = new Thread(vendor, "Vendor-" + (i + 1));
            vendors.add(vendor);
            vendor_Threads.add(vendor_Thread);
        }

        for (int i = 0; i < customers_Count; i++) {
            Customer customer = new Customer(ticketPool, customers_Retrieval_Rate, total_Tickets);
            Thread customer_Thread = new Thread(customer, "Customer-"+(i+1));
            customers.add(customer);
            customer_Threads.add(customer_Thread);
        }

        //Automatically start the system
        for (Vendor vendor : vendors) vendor.reset();
        for (Customer customer : customers) customer.reset();
        for (Thread vendor_Thread : vendor_Threads) vendor_Thread.start();
        for (Thread customer_Thread : customer_Threads) customer_Thread.start();

        System.out.println("System Started.......");

        // Wait for all tickets tobe sold out.
        try{
            for (Thread customer_Thread : customer_Threads) {
                customer_Thread.join();
            }
        } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
        }

        System.out.println("All TICKETS HAVE BEEN SOLD OUT....!");

        // Prompt user with commands after tickets are sold out
        while(true){
            System.out.println("\nCommands : ");
            System.out.println("1. Start Program");
            System.out.println("2. Stop Program");
            System.out.print("Enter Command: ");

            int command = ask_For_Input(Input, "", 1, 2);

            if(command == 1){
                main(args);
                break;
            } else {
                System.out.println("Existing Program.....");
                break;
            }
        }
        Input.close();
    }

    //Create input method
    private static int ask_For_Input(Scanner Input, String prompt, int min, int max){
        if(!prompt.isEmpty()){
            System.out.print(prompt);
        }

        int input;
        while (true){
            try {
                input = Integer.parseInt(Input.nextLine());
                if(input >= min && input <= max){
                    return input;
                }
                System.out.print("Invalid input. Please enter a value between " + min + " and " + max + ": ");
            }catch (NumberFormatException e){
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    private static void save_Config_to_file(int total_Tickets, int tickets_Release_Rate, int customers_Retrieval_Rate, int max_Ticket_Capacity){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(save_File))){
            writer.write(total_Tickets + "\n");
            writer.write(tickets_Release_Rate + "\n");
            writer.write(customers_Retrieval_Rate + "\n");
            writer.write(max_Ticket_Capacity + "\n");
            System.out.println("Configuration settings saved to file successfully.");
        } catch (IOException e){
            System.out.println("Failed to save configuration settings to file.");
        }
    }
}