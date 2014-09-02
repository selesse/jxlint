package com.selesse.jxlint.actions;

import com.google.common.io.Resources;
import com.selesse.jxlint.settings.ProgramSettings;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.awt.*;
import java.net.URI;
import java.net.URL;

public class JettyWebRunner {
    private static final int PORT = 8080;
    private static final String webApplicationDirectory = "webapp";
    private static final String contextPath = "/";
    private ProgramSettings programSettings;


    public JettyWebRunner(ProgramSettings programSettings) {
        this.programSettings = programSettings;
    }

    public void start() {
        startJetty();
    }

    private void startJetty() {
        // Set JSP to use Standard JavaC always
        System.setProperty("org.apache.jasper.compiler.disablejsr199", "false");

        Server server = new Server(PORT);

        URL warUrl = Resources.getResource(webApplicationDirectory);
        String warUrlString = warUrl.toExternalForm();
        WebAppContext context = new WebAppContext(warUrlString, contextPath);
        context.setAttribute("programSettings", programSettings);
        server.setHandler(context);

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
