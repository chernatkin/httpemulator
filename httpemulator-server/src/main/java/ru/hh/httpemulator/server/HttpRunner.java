package ru.hh.httpemulator.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;


public class HttpRunner {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static JettyServer jetty;

    private static int jettyPort;

    public static void main(String [ ] args) throws Exception {
        start();
    }


    public static void start() throws Exception {
        MAPPER.registerModule(new Hibernate5Module());

        jetty = new JettyServer();
        jettyPort = jetty.start();
    }

    public static void stop() throws Exception {
        jetty.stop();
    }
}
