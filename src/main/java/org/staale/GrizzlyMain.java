package org.staale;

import jersey.repackaged.com.google.common.base.Stopwatch;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * Created by staaleu on 19/9/15.
 */
public class GrizzlyMain {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(GrizzlyMain.class);

    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8180/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example package
        final ResourceConfig rc = new ResourceConfig().register(new MyResourceImpl());

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * GrizzlyMain method.
     * @param args;
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final Stopwatch started = Stopwatch.createStarted();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        LOG.info("Starting jersey app");
        if (System.getenv("LOG_APPENDER") == null) {
            LOG.info("Set environment property LOG_APPENDER to TEXT for text log");
        }
        final HttpServer server = startServer();
        LOG.info("Jersey app started with WADL available at {}application.wadl", BASE_URI);
        LOG.info("Started in " + started);
        synchronized (GrizzlyMain.class) {
            GrizzlyMain.class.wait();
        }
    }
}

