package org.eclipse.jetty.issue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NoBodyTest
{
    private Server server;

    @BeforeEach
    public void setupServer() throws Exception
    {
        server = new Server();

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0); // let OS assign port

        server.addConnector(connector);

        HandlerList handlers = new HandlerList();

        Path webappPath = Paths.get("src/main/webapp").toAbsolutePath();
        if (!Files.exists(webappPath))
        {
            throw new FileNotFoundException("Unable to find src/main/webapp");
        }

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setBaseResource(new PathResource(webappPath));
        webapp.setExtraClasspath("target/classes");

        handlers.addHandler(webapp);
        handlers.addHandler(new DefaultHandler());

        server.setHandler(handlers);
        server.start();
    }

    @AfterEach
    public void teardownServer()
    {
        System.err.println("### teardownServer()");
        LifeCycle.stop(server);
    }

    @Test
    public void testDeleteRequest() throws IOException, InterruptedException
    {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = server.getURI().resolve("/nobody");
        HttpRequest httpRequest = HttpRequest.newBuilder(uri).DELETE().build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        String contentLengthResponseHeader = httpResponse.headers().firstValue("Content-Length").orElse("<null>");
        System.out.printf("Response Body (Content-Length: %s): [%s]%n", contentLengthResponseHeader, httpResponse.body());
        assertEquals(501, httpResponse.statusCode(), "Response status code");
    }
}
