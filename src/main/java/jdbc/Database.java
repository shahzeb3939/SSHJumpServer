package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    private String userName;
    private String password;
    private String url;

    private Connection connection;


    public Database(DatabaseBuilder builder) {
        this.userName = builder.userName;
        this.password = builder.password;
        this.url = "jdbc:"+builder.rdbms+"://"+builder.host+":"+builder.port+"/"+ builder.db;
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(url, userName, password);
        System.out.println ("Database connection established");
    }

    public void disconnect() throws SQLException {
        if(connection != null && !connection.isClosed()){
            connection.close();
            System.out.println("Closing Database Connection");
        }
    }

    public String getAuthTokenFor(String email) throws SQLException {
        ResultSet resultSet = connection.createStatement().executeQuery(String.format("SELECT * FROM public.users_emailpin WHERE email='%s'", email));
        if(resultSet.next()) {
            if (!resultSet.getBoolean("is_deleted")) {
                System.out.println("Authentication token returned");
                return resultSet.getString("auth_token");
            }
            else {
                System.out.println("Authentication token already used");
                return "authentication_token_already_used";
            }
        } else {
            System.out.println("Email not found");
            return "email_not_found";
        }
    }

    public String getPinFor(String email) throws SQLException {
        ResultSet resultSet = connection.createStatement().executeQuery(String.format("SELECT * FROM public.users_emailpin WHERE email='%s'", email));
        if(resultSet.next()) {
            if (!resultSet.getBoolean("is_deleted")) {
                System.out.println("Pin returned");
                return resultSet.getString("pin");
            }
            else {
                System.out.println("Pin already used");
                return "pin_already_used";
            }
        } else {
            System.out.println("Email not found");
            return "email_not_found";
        }
    }
}
