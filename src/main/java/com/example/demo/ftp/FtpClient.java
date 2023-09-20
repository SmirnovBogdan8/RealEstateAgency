package com.example.demo.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class FtpClient {
    @Value("${ftp.host}")
    private String server;
    @Value("${ftp.port}")
    private int port;
    @Value("${ftp.user}")
    private String user;
    @Value("${ftp.password}")
    private String password;
    private FTPClient ftpClient;

    public void open() throws IOException {
        ftpClient = new FTPClient();

        ftpClient.connect(server, port);
        int reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }

        ftpClient.login(user, password);
    }

    public boolean isConnected() {
        if(ftpClient == null) return false;
        return ftpClient.isConnected();
    }

    public boolean upload(String text,String filename) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(text.getBytes());
        ftpClient.enterLocalPassiveMode();
        return ftpClient.storeFile("/" + filename, inputStream);
    }

    public void close() throws IOException {
        ftpClient.disconnect();
    }
}
