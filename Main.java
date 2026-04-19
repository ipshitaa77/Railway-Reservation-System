package Railways;

import java.util.Scanner;

public class Main {

    static Scanner input = new Scanner(System.in);

    // 1. logout()
    public static void logout() {
        System.out.println("Logged Out");
    }

    // 2. main_menu()
    public static void main_menu() {

        while (true) {
            System.out.println("\nWELCOME TO RAILWAY RESERVATION SERVICE\n");
            System.out.println("1. Admin Login\n2. User Login\n3. Exit");

            int choice = input.nextInt();
            input.nextLine();

            if (choice == 1) {

                if (Admin_Service.admin_login()) {

                    while (true) {
                        System.out.println("\n1. View Data");
                        System.out.println("2. Modify Data");
                        System.out.println("3. Logout");

                        int ch = input.nextInt();
                        input.nextLine();

                        if (ch == 1) {
                            Admin_Service.view_data();
                        }
                        else if (ch == 2) {
                            Admin_Service.modify_data();
                        }
                        else {
                            logout();
                            break;
                        }
                    }
                }

            }
            else if (choice == 2) {

                System.out.println("1. Sign Up");
                System.out.println("2. Login");

                int ch = input.nextInt();
                input.nextLine();

                int user_id = -1;

                if (ch == 1) {
                    user_id = User_Service.add_user();   // login after signup
                }
                else if (ch == 2) {
                    user_id = User_Service.user_login();
                }
                else {
                    System.out.println("Invalid choice.");
                    continue;
                }

                if (user_id != -1) {

                    while (true) {
                        System.out.println("\n1. Book Ticket");
                        System.out.println("2. View Booking");
                        System.out.println("3. Modify Booking");
                        System.out.println("4. Cancel Booking");
                        System.out.println("5. Logout");

                        int c = input.nextInt();
                        input.nextLine();

                        if (c == 1) {
                            Booking_Service.book_ticket(user_id);
                        }
                        else if (c == 2) {
                            Booking_Service.view_booking(user_id);
                        }
                        else if (c == 3) {
                            Booking_Service.modify_booking(user_id);
                        }
                        else if (c == 4) {
                            Booking_Service.cancel_booking(user_id);
                        }
                        else {
                            logout();
                            break;
                        }
                    }
                }
            }
        }
    }

    // 3. main()
    public static void main(String[] args) {

        DB_Setup.initialise_database();
        DB_Setup.create_tables();
        DB_Setup.add_stations_data();
        DB_Setup.add_train_schedule_data();

        main_menu();
    }
}