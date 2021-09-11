package postgresql.tasks;

import com.jcraft.jsch.JSchException;
import jdbc.Database;
import jdbc.DatabaseBuilder;
import jsch.ForwardPort;
import jsch.PortForwardBuilder;
import jsch.RemoteHost;
import json.Deserialize;
import lombok.SneakyThrows;
import postgresql.parameters.DatabaseParameters;
import postgresql.parameters.PortForwardingParameters;
import postgresql.parameters.PostgreSQLConnectionParameters;
import postgresql.parameters.RemoteHostParameters;

import java.io.FileNotFoundException;
import java.sql.SQLException;

public class AuthDb {
    static PostgreSQLConnectionParameters parameters;

    static {
        try {
            parameters = Deserialize.databaseConnectionParameters();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static private RemoteHost remoteHost;
    static private Database database;

    @SneakyThrows
    public static String getAuthToken(String email) {
        establishDatabaseConnection();
        String authToken = database.getAuthTokenFor(email);
        closeDatabaseConnection();
        return authToken;
    }

    @SneakyThrows
    public static String getPin(String email) {
        establishDatabaseConnection();
        String pin = database.getPinFor(email);
        closeDatabaseConnection();
        return pin;
    }

    private static void establishDatabaseConnection() throws JSchException, SQLException {

        RemoteHostParameters remoteHostParameters = parameters.remoteHostParameters;
        remoteHost = new RemoteHost().onUrl(remoteHostParameters.host).forUser(remoteHostParameters.user).havingIdentityFileAt(remoteHostParameters.pemFilePath).onPort(remoteHostParameters.port);
        remoteHost.createSession();
        remoteHost.connect();

        PortForwardingParameters portForwardingParameters = parameters.portForwardingParameters;
        ForwardPort forwardPort = new PortForwardBuilder()
                .localPort(portForwardingParameters.localPort)
                .remotePort(portForwardingParameters.remotePort)
                .host(portForwardingParameters.dbHost)
                .build();
        Integer forwardedPort = forwardPort.forSession(remoteHost.getSession());

        DatabaseParameters databaseParameters = parameters.databaseParameters;
        database = new DatabaseBuilder()
                .rdbms(databaseParameters.rdbms)
                .host(databaseParameters.host)
                .port(forwardedPort)
                .db(databaseParameters.db)
                .userName(databaseParameters.dbUser)
                .password(databaseParameters.dbPassword)
                .build();

        database.connect();
    }

    private static void closeDatabaseConnection() throws SQLException {
        database.disconnect();
        remoteHost.disconnect();
    }
}
