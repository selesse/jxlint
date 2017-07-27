package com.selesse.jxlint.actions.web;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.Resources;
import com.selesse.jxlint.cli.ProgramOptionExtractor;
import com.selesse.jxlint.settings.ProgramSettings;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class JettyWebRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(JettyWebRunner.class);
    private static final String webApplicationDirectory = "webapp";
    private static final String contextPath = "/";
    private ProgramSettings programSettings;
    private String portStringValue;

    public JettyWebRunner(ProgramSettings programSettings, String port) {
        this.programSettings = programSettings;
        this.portStringValue = port;
    }

    public void start() {
        startJettyHeadless(false);
    }

    @VisibleForTesting
    void startJettyHeadless(boolean headless) {
        startJetty(getPortValue(), headless);
    }

    private int getPortValue() {
        int portIntValue;

        try {
            portIntValue = Integer.parseInt(portStringValue);
        }
        catch (NumberFormatException e) {
            String defaultPort = ProgramOptionExtractor.DEFAULT_PORT;
            LOGGER.error("Error parsing port '{}', reverting to default of {}", portStringValue, defaultPort);
            portIntValue = Integer.parseInt(defaultPort);
        }

        return portIntValue;
    }

    private void startJetty(int port, boolean headless) {
        // Set JSP to use Standard JavaC always
        System.setProperty("org.apache.jasper.compiler.disablejsr199", "false");

        Server server = new Server(port);

        URL warUrl = Resources.getResource(webApplicationDirectory);
        String warUrlString = warUrl.toExternalForm();
        WebAppContext context = new WebAppContext(warUrlString, contextPath);
        context.setAttribute("programSettings", programSettings);
        server.setHandler(context);

        try {
            server.start();

            if (!headless) {
                openUserDefaultBrowserToIndex(port);
            }

            server.join();
        }
        catch (Exception e) {
            LOGGER.error("Error starting Jetty", e);
        }
    }

    private void openUserDefaultBrowserToIndex(int port) throws URISyntaxException {
        URI jettyUri = new URI("http://localhost:" + port);
        Desktop desktop = Desktop.getDesktop();
        try {
            LOGGER.info("Opening user's browser to {}", jettyUri);
            desktop.browse(jettyUri);
        }
        catch (Exception e) {
            LOGGER.error("Error opening " + jettyUri + ", try visiting the URL manually", e);
        }
    }
}
