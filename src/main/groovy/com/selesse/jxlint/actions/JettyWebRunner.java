package com.selesse.jxlint.actions;

import com.google.common.io.Resources;
import com.selesse.jxlint.web.HtmlReportExecutor;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;

public class JettyWebRunner {
    private static final int PORT = 8080;

    public void start() {
        setupClassPath();
        startJetty();
    }

    private void setupClassPath() {
        try {
            CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
            HtmlReportExecutor.setJar(new File(codeSource.getLocation().toURI().getPath()));
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void startJetty() {
        // Set JSP to use Standard JavaC always
        System.setProperty("org.apache.jasper.compiler.disablejsr199", "false");

        String webApplicationDirectory = "webapp";

        Server server = new Server(PORT);

        String contextPath = "/";

        final URL warUrl = Resources.getResource(webApplicationDirectory);
        final String warUrlString = warUrl.toExternalForm();
        server.setHandler(new WebAppContext(warUrlString, contextPath));

        try {
            server.start();

            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI("http://localhost:" + PORT));

            server.join();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
