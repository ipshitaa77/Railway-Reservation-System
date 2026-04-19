package Railways;

import java.sql.*;

import java.util.ArrayList;

import java.util.Scanner;

public class Admin_Service {

static Scanner input = new Scanner(System.in);



// 1. admin_login()

public static boolean admin_login() {

    System.out.print("Enter Admin Username: ");

    String username = input.nextLine();



    System.out.print("Enter Admin Password: ");

    String password = input.nextLine();



    if (username.equals("root") && password.equals("admin")) {

        System.out.println("Admin Login Success");

        return true;

    } else {

        System.out.println("Invalid Admin Credentials");

        return false;

    }

}



// 2. view_data()

public static void view_data() {

    try {

        Connection conn = DB_Connection.create_connection("Railway_Reservation_Project");

        Statement stmt = conn.createStatement();



        System.out.println("\n1. Users\n2. Stations\n3. Train Schedule\n4. Bookings\n5. Go Back\n");

        System.out.print("Enter choice: ");



        int choice = input.nextInt();

        input.nextLine();



        // View Train Schedule Table:

        if (choice == 3) {



            ResultSet rs = stmt.executeQuery("SELECT * FROM Train_Schedule");



            System.out.println("\nTrain Schedule Table:\n");



            while (rs.next()) {

                System.out.println(

                    "ID: " + rs.getInt("Train_ID") +

                    ", Name: " + rs.getString("Train_Name") +

                    ", Start: " + rs.getString("Start_Time") +

                    ", Destination: " + rs.getString("Destination_Time") +

                    ", Duration: " + rs.getString("Duration_Hours") + " hrs"

                );

            }

        }



        // View Stations Table:

        else if (choice == 2) {



            ResultSet rs = stmt.executeQuery("SELECT * FROM Stations");



            System.out.println("\nStations Table:\n");

            System.out.println("Station ID, Station Name");



            while (rs.next()) {

                System.out.println(

                    rs.getInt("Station_ID") + ". " +

                    rs.getString("Station_Name")

                );

            }

        }



        // View Users Table:

        else if (choice == 1) {



            ResultSet rs = stmt.executeQuery("SELECT * FROM Users");



            System.out.println("\nUsers Table:\n");



            while (rs.next()) {

                System.out.println(

                    "ID: " + rs.getInt("User_ID") +

                    ", Username: " + rs.getString("Username") +

                    ", Name: " + rs.getString("Full_Name") +

                    ", E Mail: " + rs.getString("E_Mail") +

                    ", Phone No.: " + rs.getString("Phone_No")

                );

            }

        }



        // View Bookings Table:

        else if (choice == 4) {



            ResultSet rs = stmt.executeQuery("SELECT * FROM Bookings");



            System.out.println("\nBookings Table:\n");



            while (rs.next()) {

                System.out.println(

                    "Booking ID: " + rs.getInt("Booking_ID") +

                    ", User ID: " + rs.getInt("User_ID") +

                    ", Train ID: " + rs.getInt("Train_ID") +

                    ", Travel Date: " + rs.getString("Travel_Date") +

                    ", Class: " + rs.getString("Travel_Class") +

                    ", Seats: " + rs.getInt("Seats_Booked") +

                    ", Fare: " + String.format("%.2f", rs.getDouble("Fare")) +

                    ", Total: " + String.format("%.2f", rs.getDouble("Total_Fare"))

                );

            }

        }



        else if (choice == 5) {

            System.out.println("\nReturning to previous menu...");

            return;

        }



        else {

            System.out.println("\nInvalid choice.");

        }



        conn.close();



    } catch (Exception e) {

        System.out.println("\nError: " + e.getMessage());

    }

}



// 3. modify_data()

public static void modify_data() {

    while (true) {

        System.out.println("\nSelect the table you wish to modify:");

        System.out.println("1. Stations table");

        System.out.println("2. Train Schedule table");

        System.out.println("3. Go Back");

        System.out.print("Press 1, 2 or 3: ");



        int choice = input.nextInt();

        input.nextLine();



        if (choice == 1) modify_stations();

        else if (choice == 2) modify_trains();

        else if (choice == 3) return;

        else System.out.println("\nInvalid choice.");

    }

}



// 4. modify_stations()

public static void modify_stations() {



    while (true) {



        System.out.println("\n1. Add Station");

        System.out.println("2. Delete Station");

        System.out.println("3. Update Station");

        System.out.println("4. Go Back");

        System.out.print("Press 1, 2, 3 or 4: ");



        int ch = input.nextInt();

        input.nextLine();



        if (ch == 1) add_station();

        else if (ch == 2) delete_station();

        else if (ch == 3) update_stations_data();

        else if (ch == 4) return;

        else System.out.println("\nInvalid choice.");

    }

}



// 5. add_station()

public static void add_station() {

    try {

        Connection conn = DB_Connection.create_connection("Railway_Reservation_Project");



        System.out.print("\nEnter Station Name: ");

        String name = input.nextLine();



        PreparedStatement ps = conn.prepareStatement(

            "INSERT INTO Stations (Station_Name) VALUES (?)"

        );



        ps.setString(1, name);

        ps.executeUpdate();



        System.out.println("\nStation added.");



        conn.close();



    } catch (Exception e) {

        System.out.println("\nError: " + e.getMessage());

    }

}



// 6. delete_station()

public static void delete_station() {

    try {

        Connection conn = DB_Connection.create_connection("Railway_Reservation_Project");



        System.out.print("\nEnter Station Name to delete: ");

        String name = input.nextLine();



        PreparedStatement ps = conn.prepareStatement(

            "DELETE FROM Stations WHERE Station_Name=?"

        );



        ps.setString(1, name);



        int rows = ps.executeUpdate();



        if (rows > 0) System.out.println("\nStation deleted.");

        else System.out.println("\nStation not found.");



        conn.close();



    } catch (Exception e) {

        System.out.println("\nError: " + e.getMessage());

    }

}



// 7. update_stations_data()

public static void update_stations_data() {

    try {

        Connection conn = DB_Connection.create_connection("Railway_Reservation_Project");



        // Get Old Station Name:

        String old_name;



        while (true) {

            System.out.print("Enter existing Station Name: ");

            old_name = input.nextLine();



            PreparedStatement check = conn.prepareStatement(

                    "SELECT Station_ID FROM Stations WHERE Station_Name=?"

            );



            check.setString(1, old_name);

            ResultSet rs = check.executeQuery();



            if (rs.next()) break;

            else System.out.println("\nThis station does not exist.\nTry Again.\n");

        }



        // Get New Name:

        String new_name;



        while (true) {

            System.out.print("Enter new Station Name: ");

            new_name = input.nextLine();



            PreparedStatement check = conn.prepareStatement(

                    "SELECT Station_ID FROM Stations WHERE Station_Name=?"

            );



            check.setString(1, new_name);

            ResultSet rs = check.executeQuery();



            if (rs.next()) {

                System.out.println("\nThis station name already exists.\nChoose Another.\n");

            } else break;

        }



        // Update Query:

        PreparedStatement ps = conn.prepareStatement(

                "UPDATE Stations SET Station_Name=? WHERE Station_Name=?"

        );



        ps.setString(1, new_name);

        ps.setString(2, old_name);



        int rows = ps.executeUpdate();



        if (rows > 0) {

            System.out.println("\nStation updated successfully.");

        } else {

            System.out.println("\nUpdate failed.");

        }



        conn.close();



    } catch (Exception e) {

        System.out.println("\nError: " + e.getMessage());

    }

}



// 8. modify_trains()

public static void modify_trains() {

    while (true) {

        display_all_trains();



        System.out.println("\n1. Add Train");

        System.out.println("2. Delete Train");

        System.out.println("3. Update Train");

        System.out.println("4. Go Back");

        System.out.print("Press 1, 2, 3 or 4: ");



        int ch = input.nextInt();

        input.nextLine();



        if (ch == 1) add_train();

        else if (ch == 2) delete_train();

        else if (ch == 3) update_train();

        else if (ch == 4) return;

        else System.out.println("\nInvalid choice.");

    }

}



// 9. display_all_trains()

public static void display_all_trains() {

    try {

        Connection conn = DB_Connection.create_connection("Railway_Reservation_Project");

        Statement stmt = conn.createStatement();



        ResultSet rs = stmt.executeQuery("SELECT * FROM Train_Schedule");



        System.out.println("\nAvailable Trains:\n");



        while (rs.next()) {

            System.out.println(

                "ID: " + rs.getInt("Train_ID") +

                ", Name: " + rs.getString("Train_Name") +

                ", Start: " + rs.getString("Start_Time") +

                ", Destination: " + rs.getString("Destination_Time") +

                ", Duration: " + rs.getString("Duration_Hours") + " hrs"

            );

        }



        conn.close();



    } catch (Exception e) {

        System.out.println("\nError: " + e.getMessage());

    }

}



// 10. add_train()

public static void add_train() {

    try {

        Connection conn = DB_Connection.create_connection("Railway_Reservation_Project");



        System.out.print("\nEnter Train Name: ");

        String name = input.nextLine();



        System.out.print("Enter Start Time (HH:MM:SS): ");

        String start = input.nextLine();



        System.out.print("Enter Destination Time (HH:MM:SS): ");

        String dest = input.nextLine();



        System.out.print("Enter Duration: ");

        String dur = input.nextLine();



        PreparedStatement ps = conn.prepareStatement(

            "INSERT INTO Train_Schedule (Train_Name, Start_Time, Destination_Time, Duration_Hours) VALUES (?, ?, ?, ?)"

        );



        ps.setString(1, name);

        ps.setString(2, start);

        ps.setString(3, dest);

        ps.setString(4, dur);



        ps.executeUpdate();



        System.out.println("\nTrain added successfully.");



        conn.close();



    } catch (Exception e) {

        System.out.println("\nError: " + e.getMessage());

    }

}



// 11. delete_train()

public static void delete_train() {

    try {

        Connection conn = DB_Connection.create_connection("Railway_Reservation_Project");



        System.out.print("\nEnter Train ID to delete: ");

        int id = input.nextInt();

        input.nextLine();



        PreparedStatement ps = conn.prepareStatement(

            "DELETE FROM Train_Schedule WHERE Train_ID=?"

        );



        ps.setInt(1, id);



        int rows = ps.executeUpdate();



        if (rows > 0) System.out.println("\nTrain deleted.");

        else System.out.println("\nInvalid Train ID.");



        conn.close();



    } catch (Exception e) {

        System.out.println("\nError: " + e.getMessage());

    }

}



// 12. update_train()

public static void update_train() {

    try {

        Connection conn = DB_Connection.create_connection("Railway_Reservation_Project");



        System.out.print("\nEnter Train ID: ");

        int id = input.nextInt();

        input.nextLine();



        String name = null, start = null, dest = null, dur = null;



        System.out.print("Update Train Name? (yes/no): ");

        if (input.nextLine().equalsIgnoreCase("yes")) {

            System.out.print("Enter new name: ");

            name = input.nextLine();

        }



        System.out.print("Update Start Time? (yes/no): ");

        if (input.nextLine().equalsIgnoreCase("yes")) {

            System.out.print("Enter new start time: ");

            start = input.nextLine();

        }



        System.out.print("Update Destination Time? (yes/no): ");

        if (input.nextLine().equalsIgnoreCase("yes")) {

            System.out.print("Enter new destination time: ");

            dest = input.nextLine();

        }



        System.out.print("Update Duration? (yes/no): ");

        if (input.nextLine().equalsIgnoreCase("yes")) {

            System.out.print("Enter new duration: ");

            dur = input.nextLine();

        }



        StringBuilder query = new StringBuilder("UPDATE Train_Schedule SET ");

        ArrayList<Object> values = new ArrayList<>();



        if (name != null) { query.append("Train_Name=?, "); values.add(name); }

        if (start != null) { query.append("Start_Time=?, "); values.add(start); }

        if (dest != null) { query.append("Destination_Time=?, "); values.add(dest); }

        if (dur != null) { query.append("Duration_Hours=?, "); values.add(dur); }



        if (values.isEmpty()) {

            System.out.println("\nNo changes made.");

            conn.close();

            return;

        }



        query.setLength(query.length() - 2);

        query.append(" WHERE Train_ID=?");

        values.add(id);



        PreparedStatement ps = conn.prepareStatement(query.toString());



        for (int i = 0; i < values.size(); i++) {

            ps.setObject(i + 1, values.get(i));

        }



        int rows = ps.executeUpdate();



        if (rows > 0) System.out.println("\nTrain updated.");

        else System.out.println("\nInvalid Train ID.");



        conn.close();



    } catch (Exception e) {

        System.out.println("\nError: " + e.getMessage());

    }

}

} now do the same for this admin code