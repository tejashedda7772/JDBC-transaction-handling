import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final String url = "jdbc:mysql://localhost:3306/udhaar";
    private static final String username = "root";
    private static final String password = "W7301@jqir#";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }


        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            String debit_query = "UPDATE transactions SET balance - ? WHERE account_number = ?";
            String credit_query = "UPDATE transactions SET balance + ? WHERE account_number = ?";
            PreparedStatement debitPreparedStatement = connection.prepareStatement(debit_query);
            PreparedStatement creditPreparedStatement = connection.prepareStatement(credit_query);
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter Account Number: ");
            int account_number = scanner.nextInt();
            double amount = scanner.nextDouble();
            debitPreparedStatement.setDouble(1, amount);
            debitPreparedStatement.setInt(2, account_number);
            creditPreparedStatement.setDouble(1, amount);
            creditPreparedStatement.setInt(2, 102);
            debitPreparedStatement.executeUpdate();
            creditPreparedStatement.executeUpdate();
            if(isSufficient(connection, account_number, amount)) {
                connection.commit();
                System.out.println("Payment Successfull");
            }else{
                connection.rollback();
                System.out.println("Payment Failed!!");
            }
            debitPreparedStatement.executeUpdate();
            creditPreparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static boolean isSufficient(Connection connection, int account_number, double amount) {
        try {
            String query = "SELECT balance FROM transactions WHERE account_number = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, account_number);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                double current_balance = resultSet.getDouble("balance");
                if (amount > current_balance) {
                    return false;
                }else{
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }
}
