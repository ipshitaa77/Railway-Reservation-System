package Railways;

import java.sql.*;
import java.util.Scanner;

public class User_Service {

    static Scanner input = new Scanner(System.in);

    // 1. add_user()
    public static int add_user() {
        try {
            Connection conn = DB_Connection.create_connection("Railway_Reservation_Project");

            while (true) {

                // Username:
                System.out.print("\nEnter Username: ");
                String username = input.nextLine();

                PreparedStatement checkUser = conn.prepareStatement(
                    "SELECT User_ID FROM Users WHERE Username=?"
                );
                checkUser.setString(1, username);

                ResultSet userRs = checkUser.executeQuery();

                if (userRs.next()) {
                    System.out.println("Username already exists. Try another.");
                    continue;
                }

                // Password:
                String password;

                while (true) {
                    System.out.print("Enter Password: ");
                    password = input.nextLine();

                    if (password.length() < 8) {
                        System.out.println("Password must be at least 8 characters.");
                        continue;
                    }

                    boolean hasLower = false, hasUpper = false, hasDigit = false, hasSpecial = false;

                    for (char c : password.toCharArray()) {
                        if (Character.isLowerCase(c)) hasLower = true;
                        else if (Character.isUpperCase(c)) hasUpper = true;
                        else if (Character.isDigit(c)) hasDigit = true;
                        else hasSpecial = true;
                    }

                    if (!hasLower || !hasUpper || !hasDigit || !hasSpecial) {
                        System.out.println("Password must contain uppercase, lowercase, number, and special character.");
                        continue;
                    }

                    break;
                }

                // Name:
                System.out.print("Enter Full Name: ");
                String full_name = input.nextLine();

                // E Mail:
                String email;

                while (true) {
                    System.out.print("Enter Email: ");
                    email = input.nextLine();

                    if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                        System.out.println("Invalid email format.");
                        continue;
                    }

                    PreparedStatement checkEmail = conn.prepareStatement(
                        "SELECT User_ID FROM Users WHERE E_Mail=?"
                    );
                    checkEmail.setString(1, email);

                    ResultSet emailRs = checkEmail.executeQuery();

                    if (emailRs.next()) {
                        System.out.println("Email already exists.");
                        continue;
                    }

                    break;
                }

                // Phone No.:
                String phone;

                while (true) {
                    System.out.print("Enter Phone: ");
                    phone = input.nextLine();

                    if (!phone.matches("\\d{10}")) {
                        System.out.println("Phone must be exactly 10 digits.");
                        continue;
                    }

                    PreparedStatement checkPhone = conn.prepareStatement(
                        "SELECT User_ID FROM Users WHERE Phone_No=?"
                    );
                    checkPhone.setString(1, phone);

                    ResultSet phoneRs = checkPhone.executeQuery();

                    if (phoneRs.next()) {
                        System.out.println("Phone number already exists.");
                        continue;
                    }

                    break;
                }

                // Insert:
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Users (Username, Password, Full_Name, E_Mail, Phone_No) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                );

                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, full_name);
                ps.setString(4, email);
                ps.setString(5, phone);

                ps.executeUpdate();

                ResultSet genKeys = ps.getGeneratedKeys();
                int user_id = -1;

                if (genKeys.next()) {
                    user_id = genKeys.getInt(1);
                }

                System.out.println("\nRegistration successful. You are now logged in.");

                conn.close();
                return user_id;
            }

        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }

        return -1;
    }

    // 2. get_user_fullname(user_id)
    public static String get_user_fullname(int user_id) {
        try {
            Connection conn = DB_Connection.create_connection("Railway_Reservation_Project");

            PreparedStatement ps = conn.prepareStatement(
                "SELECT Full_Name FROM Users WHERE User_ID=?"
            );
            ps.setInt(1, user_id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String name = rs.getString("Full_Name");
                conn.close();
                return name;
            }

            conn.close();
            return null;

        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    // 3. user_login()
    public static int user_login() {
        try {
            Connection conn = DB_Connection.create_connection("Railway_Reservation_Project");

            System.out.print("Enter Username: ");
            String username = input.nextLine();

            System.out.print("Enter Password: ");
            String password = input.nextLine();

            PreparedStatement ps = conn.prepareStatement(
                "SELECT User_ID FROM Users WHERE Username=? AND Password=?"
            );

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int user_id = rs.getInt("User_ID");
                System.out.println("Login success");
                conn.close();
                return user_id;
            } else {
                System.out.println("Login failed");
                conn.close();
                return -1;
            }

        } catch (Exception e) {
            System.out.println(e);
            return -1;
        }
    }
}