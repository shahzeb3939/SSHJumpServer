package jsch;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Properties;

public class RemoteHost {
    private String user;
    private String host;
    private Integer port;
    private String identityFilePath;

    private Session session;

    public RemoteHost onUrl(String host) {
        this.host = host;
        return this;
    }

    public RemoteHost forUser(String user) {
        this.user = user;
        return this;
    }

    public RemoteHost onPort(Integer port) {
        this.port = port;
        return this;
    }

    public RemoteHost havingIdentityFileAt(String identityFilePath) {
        this.identityFilePath = identityFilePath;
        return this;
    }

    public void createSession() throws JSchException {
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        JSch jsch = new JSch();
        jsch.addIdentity(this.identityFilePath);
        session = jsch.getSession(this.user, this.host, this.port);
        session.setConfig(config);
    }

    public Session getSession() {
        return session;
    }

    public void connect() throws JSchException {
        session.connect();
        System.out.println("SSH connection established");
    }

    public void disconnect() {
        if(session !=null && session.isConnected()){
            System.out.println("Closing SSH Connection");
            session.disconnect();
        }
    }
}
