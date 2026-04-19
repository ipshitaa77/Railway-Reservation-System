package Railways;

import java.sql.*;

public class DB_Setup {

    // 1. initialise_database()
    public static void initialise_database() {
        try {
            Connection conn = DB_Connection.create_dbconnection();
            Statement stmt = conn.createStatement();
            
            // stmt.executeUpdate("DROP DATABASE Railway_Reservation_Project;");
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS Railway_Reservation_Project;");

            conn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // 2. create_tables()
    public static void create_tables() {
        try {
            Connection conn = DB_Connection.create_connection("Railway_Reservation_Project");
            Statement stmt = conn.createStatement();

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS Users (" +
                "User_ID INT AUTO_INCREMENT PRIMARY KEY," +
                "Username VARCHAR(50) UNIQUE NOT NULL," +
                "Password VARCHAR(50) NOT NULL," +
                "Full_Name VARCHAR(100)," +
                "E_Mail VARCHAR(100) UNIQUE," +
                "Phone_No VARCHAR(10) UNIQUE)"
            );

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS Stations (" +
                "Station_ID INT AUTO_INCREMENT PRIMARY KEY," +
                "Station_Name VARCHAR(100) UNIQUE NOT NULL)"
            );

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS Train_Schedule (" +
                "Train_ID INT AUTO_INCREMENT PRIMARY KEY," +
                "Train_Name VARCHAR(50) UNIQUE NOT NULL," +
                "Start_Time TIME NOT NULL," +
                "Destination_Time TIME NOT NULL," +
                "Duration_Hours VARCHAR(5))"
            );

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS Bookings (" +
                "Booking_ID INT AUTO_INCREMENT PRIMARY KEY," +
                "User_ID INT," +
                "Train_ID INT," +
                "Booking_Date DATETIME," +
                "Travel_Date DATE," +
                "Travel_Class VARCHAR(20)," +
                "Seats_Booked INT," +
                "Fare DOUBLE," +
                "Total_Fare DOUBLE)"
            );

            conn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // 3. add_stations_data()
    public static void add_stations_data() {
        try {
            Connection conn = DB_Connection.create_connection("Railway_Reservation_Project");
            Statement stmt = conn.createStatement();

            String[] stations = {
                "Agartala", "Agra", "Ahmedabad", "Aizawl", "Ajmer", "Aligarh", "Allahabad", "Amaravati", "Amritsar",
                "Aurangabad", "Ayodhya", "Bangalore", "Bareilly", "Bhavnagar", "Bhilai", "Bhopal", "Bhubaneswar",
                "Bikaner", "Bina", "Bokaro", "Chandigarh", "Chennai", "Coimbatore", "Cuttack", "Darjeeling", "Dehradun",
                "Delhi", "Dhanbad", "Dispur", "Durgapur", "Faridabad", "Gandhinagar", "Gangtok", "Gaya", "Ghaziabad",
                "Guwahati", "Gwalior", "Howrah", "Hyderabad", "Imphal", "Indore", "Itanagar", "Itarsi", "Jabalpur",
                "Jaipur", "Jalandhar", "Jammu", "Jamnagar", "Jamshedpur", "Jhansi", "Jodhpur", "Kanpur", "Kochi",
                "Kodaikanal", "Kohima", "Kolkata", "Kota", "Lucknow", "Ludhiana", "Madurai", "Mangalore", "Mathura",
                "Meerut", "Moradabad", "Mount Abu", "Mumbai", "Mussoorie", "Mysore", "Nagpur", "Nainital", "Nasik",
                "Nellore", "Ooty", "Panaji", "Patiala", "Patna", "Puducherry", "Pune", "Raipur", "Rajkot", "Ranchi",
                "Rourkela", "Shillong", "Shimla", "Shirdi", "Siliguri", "Srinagar", "Surat", "Thane",
                "Thiruvananthapuram", "Tirupati", "Tuticorin", "Udaipur", "Udupi", "Ujjain", "Vadodara", "Varanasi",
                "Vellore", "Vijayawada", "Visakhapatnam"
            };

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Stations");
            rs.next();

            if (rs.getInt(1) == 0) {
                for (String st : stations) {
                    stmt.executeUpdate(
                        "INSERT IGNORE INTO Stations (Station_Name) VALUES ('" + st + "')"
                    );
                }
            }

            conn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // 4. add_train_schedule_data()
    public static void add_train_schedule_data() {
        try {
            Connection conn = DB_Connection.create_connection("Railway_Reservation_Project");
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Train_Schedule");
            rs.next();

            if (rs.getInt(1) == 0) {
                stmt.executeUpdate(
                    "INSERT INTO Train_Schedule (Train_Name, Start_Time, Destination_Time, Duration_Hours) VALUES " +
                    "('Rajdhani Express','03:01:00','18:11:00','15')," +
                    "('Shatabdi Express','05:31:00','21:31:00','16')," +
                    "('Duronto Express','03:17:00','17:37:00','14')," +
                    "('Deccan Queen', '06:05:00', '23:35:00', '17.5')," +
                    "('Gatimaan Express', '07:23:00', '02:53:00', '19.3')," +
                    "('Garib Rath', '04:21:00', '08:33:00', '4')," +
                    "('Humsafar Express', '10:13:00', '18:59:00', '8.75')," +
                    "('Tejas Express', '16:43:00', '20:19:00', '3.75')," +
                    "('Pushpak Express', '14:27:00', '09:09:00', '18')," +
                    "('Vaishali Express', '11:53:00', '07:01:00', '19')"
                );
            }

            conn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
