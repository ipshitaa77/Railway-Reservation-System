package Railways;

import java.sql.*;
import java.util.*;

public class Booking_Service {

    static Scanner input = new Scanner(System.in);
    static Random rand = new Random();

    // 1. get_train_name
    public static String get_train_name(int train_id) {
        try {
            Connection conn = DB_Connection.create_connection("Railway_Reservation_Project");

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT Train_Name FROM Train_Schedule WHERE Train_ID=?"
            );
            ps.setInt(1, train_id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String name = rs.getString(1);
                conn.close();
                return name;
            }

            conn.close();

        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
        return "";
    }

    // 2. Generate Random Fare
    static double generateFare(String travel_class) {

        double min = 0, max = 0;

        switch (travel_class) {
            case "FIRST_AC": min = 4000; max = 7000; break;
            case "SECOND_AC": min = 2500; max = 4000; break;
            case "THIRD_AC": min = 1500; max = 2500; break;
            case "SLEEPER": min = 500; max = 1500; break;
            case "GENERAL": min = 100; max = 500; break;
        }

        return min + (max - min) * rand.nextDouble();
    }

    // 3. Display 3 Random Trains
    public static ArrayList<Integer> display_available_trains(Connection conn) {

        ArrayList<Integer> valid_ids = new ArrayList<>();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Train_Schedule");

            ArrayList<String[]> trains = new ArrayList<>();

            while (rs.next()) {
                trains.add(new String[]{
                        rs.getString("Train_ID"),
                        rs.getString("Train_Name"),
                        rs.getString("Start_Time"),
                        rs.getString("Destination_Time"),
                        rs.getString("Duration_Hours")
                });
            }

            Collections.shuffle(trains);

            System.out.println("\nAvailable trains:\n");

            for (int i = 0; i < Math.min(3, trains.size()); i++) {
                String[] t = trains.get(i);

                System.out.println(
                        "Train ID: " + t[0] +
                        ", Train Name: " + t[1] +
                        ", Start Time: " + t[2] +
                        ", Destination Time: " + t[3] +
                        ", Duration: " + t[4] + " hours"
                );

                valid_ids.add(Integer.parseInt(t[0]));
            }

        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }

        return valid_ids;
    }

    // 4. Book Ticket
    public static void book_ticket(int user_id) {
        try {
            Connection conn = DB_Connection.create_connection("Railway_Reservation_Project");

            // SOURCE
            System.out.print("\nEnter source station: ");
            String source = input.nextLine();

            // DESTINATION
            System.out.print("\nEnter destination station: ");
            String destination = input.nextLine();

            // SHOW TRAINS
            ArrayList<Integer> valid_ids = display_available_trains(conn);

            // TRAIN SELECTION
            int train_id;
            while (true) {
                System.out.print("\nEnter the required Train ID from the list: ");
                train_id = input.nextInt();

                if (valid_ids.contains(train_id)) break;
                else System.out.println("\nInvalid Train ID.");
            }

            input.nextLine(); // buffer clear

            // DATE
            System.out.print("\nEnter Travel Date (YYYY-MM-DD): ");
            String travel_date = input.nextLine();

            // SEATS
            System.out.print("\nEnter the number of seats to book: ");
            int seats = input.nextInt();

            // CLASS SELECTION
            System.out.println("\nSelect the travel class:");
            System.out.println("1: First AC");
            System.out.println("2: Second AC");
            System.out.println("3: Third AC");
            System.out.println("4: Sleeper");
            System.out.println("5: General");

            String travel_class = "";

            while (true) {
                System.out.print("\nPress 1, 2, 3, 4 or 5: ");
                int ch = input.nextInt();

                if (ch == 1) { travel_class = "FIRST_AC"; break; }
                else if (ch == 2) { travel_class = "SECOND_AC"; break; }
                else if (ch == 3) { travel_class = "THIRD_AC"; break; }
                else if (ch == 4) { travel_class = "SLEEPER"; break; }
                else if (ch == 5) { travel_class = "GENERAL"; break; }
                else System.out.println("\nInvalid choice.");
            }

            double fare = generateFare(travel_class);
            double total = fare * seats;

            System.out.printf(
                    "\nTotal fare for %d seat(s) in %s class is: %.2f INR\n",
                    seats, travel_class, total
            );

            input.nextLine(); // buffer clear

            // CONFIRM
            System.out.print("\nDo you want to confirm the booking? (Yes / No): ");
            String confirm = input.nextLine();

            if (confirm.equalsIgnoreCase("yes")) {

                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO Bookings (User_ID, Train_ID, Booking_Date, Travel_Date, Travel_Class, Seats_Booked, Fare, Total_Fare) VALUES (?, ?, NOW(), ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );

                ps.setInt(1, user_id);
                ps.setInt(2, train_id);
                ps.setString(3, travel_date);
                ps.setString(4, travel_class);
                ps.setInt(5, seats);
                ps.setDouble(6, fare);
                ps.setDouble(7, total);

                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                int booking_id = -1;
                if (rs.next()) booking_id = rs.getInt(1);

                String name = User_Service.get_user_fullname(user_id);
                String train = get_train_name(train_id);

                System.out.printf(
                        "\nDear %s, %d seats booked successfully on %d %s in %s class on %s.\n",
                        name, seats, train_id, train, travel_class, travel_date
                );

                System.out.printf(
                        "Fare: %.2f INR, Total Fare: %.2f INR\n",
                        fare, total
                );

                System.out.println("Booking ID: " + booking_id);
            }
            else {
                System.out.println("\nBooking Cancelled.");
            }

            conn.close();

        } catch (Exception e) {
            System.out.println("\nError occurred: " + e.getMessage());
        }
    }

    // 5. View Booking
    public static void view_booking(int user_id) {
        try {
            Connection conn = DB_Connection.create_connection("Railway_Reservation_Project");

            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM Bookings WHERE User_ID=?"
            );

            ps.setInt(1, user_id);
            ResultSet rs = ps.executeQuery();

            boolean found = false;

            System.out.println("\nYour Bookings:\n");

            while (rs.next()) {
                found = true;

                System.out.println(
                    "Booking ID: " + rs.getInt("Booking_ID") +
                    ", Train ID: " + rs.getInt("Train_ID") +
                    ", Travel Date: " + rs.getString("Travel_Date") +
                    ", Class: " + rs.getString("Travel_Class") +
                    ", Seats: " + rs.getInt("Seats_Booked") +
                    ", Fare: " + String.format("%.2f", rs.getDouble("Fare")) +
                    ", Total: " + String.format("%.2f", rs.getDouble("Total_Fare"))
                );
            }

            if (!found) {
                System.out.println("No bookings found.");
            }

            conn.close();

        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    // 6. Modify Booking
    public static void modify_booking(int user_id) {
        try {
            Connection conn = DB_Connection.create_connection("Railway_Reservation_Project");

            // Display Bookings:
            PreparedStatement view = conn.prepareStatement(
                    "SELECT * FROM Bookings WHERE User_ID=?"
            );
            view.setInt(1, user_id);

            ResultSet rs = view.executeQuery();

            ArrayList<Integer> valid_ids = new ArrayList<>();

            System.out.println("\nYour Bookings:\n");

            while (rs.next()) {
                int id = rs.getInt("Booking_ID");
                valid_ids.add(id);

                System.out.println(
                    "Booking ID: " + id +
                    ", Train ID: " + rs.getInt("Train_ID") +
                    ", Travel Date: " + rs.getString("Travel_Date") +
                    ", Class: " + rs.getString("Travel_Class") +
                    ", Seats: " + rs.getInt("Seats_Booked")
                );
            }

            if (valid_ids.isEmpty()) {
                System.out.println("No bookings found.");
                conn.close();
                return;
            }

            // Select Bookings:
            int booking_id;

            while (true) {
                System.out.print("\nEnter Booking ID to modify: ");
                booking_id = input.nextInt();
                input.nextLine();

                if (valid_ids.contains(booking_id)) break;
                else System.out.println("Invalid Booking ID.");
            }

            // Ask Field by Field:
            String new_date = null;
            String new_class = null;
            Integer new_seats = null;

            System.out.print("Update Travel Date? (yes/no): ");
            if (input.nextLine().equalsIgnoreCase("yes")) {
                System.out.print("Enter new date (YYYY-MM-DD): ");
                new_date = input.nextLine();
            }

            System.out.print("Update Class? (yes/no): ");
            if (input.nextLine().equalsIgnoreCase("yes")) {

                System.out.println("1 First AC\n2 Second AC\n3 Third AC\n4 Sleeper\n5 General");

                while (true) {
                    int ch = input.nextInt();
                    input.nextLine();

                    if (ch == 1) { new_class = "FIRST_AC"; break; }
                    else if (ch == 2) { new_class = "SECOND_AC"; break; }
                    else if (ch == 3) { new_class = "THIRD_AC"; break; }
                    else if (ch == 4) { new_class = "SLEEPER"; break; }
                    else if (ch == 5) { new_class = "GENERAL"; break; }
                    else System.out.println("Invalid choice.");
                }
            }

            System.out.print("Update Seats? (yes/no): ");
            if (input.nextLine().equalsIgnoreCase("yes")) {
                System.out.print("Enter new number of seats: ");
                new_seats = input.nextInt();
                input.nextLine();
            }

            // Fetch Current Values:
            PreparedStatement fetch = conn.prepareStatement(
                    "SELECT Travel_Class, Seats_Booked FROM Bookings WHERE Booking_ID=? AND User_ID=?"
            );
            fetch.setInt(1, booking_id);
            fetch.setInt(2, user_id);

            ResultSet current = fetch.executeQuery();
            current.next();

            String final_class = (new_class != null) ? new_class : current.getString("Travel_Class");
            int final_seats = (new_seats != null) ? new_seats : current.getInt("Seats_Booked");

            // Fetch Current Fare & Seats:
            PreparedStatement fetchFull = conn.prepareStatement(
                    "SELECT Fare, Seats_Booked, Travel_Class FROM Bookings WHERE Booking_ID=? AND User_ID=?"
            );
            fetchFull.setInt(1, booking_id);
            fetchFull.setInt(2, user_id);

            ResultSet oldData = fetchFull.executeQuery();
            oldData.next();

            double old_fare = oldData.getDouble("Fare");
            int old_seats = oldData.getInt("Seats_Booked");
            String old_class = oldData.getString("Travel_Class");

            double old_total = old_fare * old_seats;

            // Determine New Fare:
            double new_fare;

            // If class changed, then new random fare:
            if (new_class != null && !new_class.equals(old_class)) {
                new_fare = generateFare(final_class);
            } else {
                new_fare = old_fare; // keep same per-seat fare
            }

            double new_total = new_fare * final_seats;

            // Price Difference:
            double diff = new_total - old_total;

            // Build Query:
            StringBuilder query = new StringBuilder("UPDATE Bookings SET ");
            ArrayList<Object> values = new ArrayList<>();

            if (new_date != null) {
                query.append("Travel_Date=?, ");
                values.add(new_date);
            }
            if (new_class != null) {
                query.append("Travel_Class=?, ");
                values.add(new_class);
            }
            if (new_seats != null) {
                query.append("Seats_Booked=?, ");
                values.add(new_seats);
            }

            // Always update fare if anything changed:
            query.append("Fare=?, Total_Fare=? ");
            values.add(new_fare);
            values.add(new_total);

            query.append("WHERE Booking_ID=? AND User_ID=?");
            values.add(booking_id);
            values.add(user_id);

            PreparedStatement ps = conn.prepareStatement(query.toString());

            for (int i = 0; i < values.size(); i++) {
                ps.setObject(i + 1, values.get(i));
            }

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("\nBooking updated successfully.");

                System.out.println("Old Total: " + String.format("%.2f", old_total));
                System.out.println("New Total: " + String.format("%.2f", new_total));

                if (diff > 0) {
                    System.out.println("Please pay additional: " + String.format("%.2f", diff));
                }
                else if (diff < 0) {
                    System.out.println("Refund Amount: " + String.format("%.2f", Math.abs(diff)));
                }
                else {
                    System.out.println("No price change.");
                }
            }
            else {
                System.out.println("\nUpdate failed.");
            }

            conn.close();

        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    // 7. Cancel Booking
    public static void cancel_booking(int user_id) {
        try {
            Connection conn = DB_Connection.create_connection("Railway_Reservation_Project");

            // Show Bookings:
            PreparedStatement view = conn.prepareStatement(
                    "SELECT * FROM Bookings WHERE User_ID=?"
            );
            view.setInt(1, user_id);

            ResultSet rs = view.executeQuery();

            ArrayList<Integer> valid_ids = new ArrayList<>();

            System.out.println("\nYour Bookings:\n");

            while (rs.next()) {
                int id = rs.getInt("Booking_ID");
                valid_ids.add(id);

                System.out.println(
                    "Booking ID: " + id +
                    ", Train ID: " + rs.getInt("Train_ID") +
                    ", Travel Date: " + rs.getString("Travel_Date") +
                    ", Class: " + rs.getString("Travel_Class") +
                    ", Seats: " + rs.getInt("Seats_Booked") +
                    ", Total: " + String.format("%.2f", rs.getDouble("Total_Fare"))
                );
            }

            if (valid_ids.isEmpty()) {
                System.out.println("No bookings found.");
                conn.close();
                return;
            }

            // Select Booking:
            int booking_id;

            while (true) {
                System.out.print("\nEnter Booking ID to cancel: ");
                booking_id = input.nextInt();
                input.nextLine();

                if (valid_ids.contains(booking_id)) break;
                else System.out.println("Invalid Booking ID.");
            }

            // Fetch Details:
            PreparedStatement fetch = conn.prepareStatement(
                    "SELECT Travel_Class, Total_Fare FROM Bookings WHERE Booking_ID=? AND User_ID=?"
            );
            fetch.setInt(1, booking_id);
            fetch.setInt(2, user_id);

            ResultSet data = fetch.executeQuery();
            data.next();

            String travel_class = data.getString("Travel_Class");
            double total = data.getDouble("Total_Fare");

            // Deduction Logic:
            double refund_percent = 0;

            switch (travel_class) {
                case "GENERAL": refund_percent = 0.85; break;
                case "SLEEPER": refund_percent = 0.75; break;
                case "THIRD_AC": refund_percent = 0.60; break;
                case "SECOND_AC": refund_percent = 0.50; break;
                case "FIRST_AC": refund_percent = 0.25; break;
            }

            double refund = total * refund_percent;
            double deducted = total - refund;

            // Delete Booking:
            PreparedStatement delete = conn.prepareStatement(
                    "DELETE FROM Bookings WHERE Booking_ID=? AND User_ID=?"
            );
            delete.setInt(1, booking_id);
            delete.setInt(2, user_id);

            int rows = delete.executeUpdate();

            // Output:
            if (rows > 0) {

                int deduction_percent = (int)((1 - refund_percent) * 100);

                System.out.println("\nBooking cancelled successfully.");

                String class_display;
                switch (travel_class) {
                    case "FIRST_AC": class_display = "First AC"; break;
                    case "SECOND_AC": class_display = "Second AC"; break;
                    case "THIRD_AC": class_display = "Third AC"; break;
                    case "SLEEPER": class_display = "Sleeper"; break;
                    case "GENERAL": class_display = "General"; break;
                    default: class_display = travel_class;
                }
                System.out.println("For " + class_display + " class, only " + (int)(refund_percent * 100) + "% of the fare is refunded.");
                System.out.println("Amount Deducted: " + String.format("%.2f", deducted));
                System.out.println("Refund Amount: " + String.format("%.2f", refund));
            }
            else {
                System.out.println("\nCancellation failed.");
            }

            conn.close();

        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }
}