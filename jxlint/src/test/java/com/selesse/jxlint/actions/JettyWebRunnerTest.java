package com.selesse.jxlint.actions;

import com.google.common.collect.Maps;
import com.selesse.jxlint.cli.ProgramOptionExtractor;
import com.selesse.jxlint.model.rules.LintRule;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.samplerules.xml.XmlLintRulesTestImpl;
import com.selesse.jxlint.settings.JxlintProgramSettings;
import com.selesse.jxlint.settings.ProgramSettings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class JettyWebRunnerTest {
    private static Thread jettyWebRunnerThread;
    private static final int TEST_PORT = Integer.parseInt(ProgramOptionExtractor.DEFAULT_PORT);
    private static final ProgramSettings PROGRAM_SETTINGS = new JxlintProgramSettings();

    private Response response;
    private String content;

    @BeforeClass
    public static void startJetty() {
        LintRulesImpl.setInstance(new XmlLintRulesTestImpl());

        jettyWebRunnerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                JettyWebRunner jettyWebRunner = new JettyWebRunner(PROGRAM_SETTINGS, "" + TEST_PORT);
                jettyWebRunner.startJettyHeadless(true);
            }
        });
        jettyWebRunnerThread.start();
    }

    @AfterClass
    public static void stopJetty() throws Exception {
        if (jettyWebRunnerThread != null) {
            jettyWebRunnerThread.join(200);
        }
    }

    @Before
    public void setup() {
        Client client = ClientBuilder.newClient();

        response = client.target("http://localhost:" + TEST_PORT).request().get();
        content = response.readEntity(String.class);
    }

    @Test
    public void testVisitingIndexRendersAsExpected() throws Exception {
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(content).contains(PROGRAM_SETTINGS.getProgramName());
        assertThat(content).contains(PROGRAM_SETTINGS.getProgramVersion());

        assertThat(LintRulesImpl.getInstance().getAllRules()).isNotEmpty();

        for (LintRule lintRule : LintRulesImpl.getInstance().getAllRules()) {
            assertThat(content).contains(lintRule.getName());
        }
    }

    @Test
    public void testRulesThatAreEnabledByDefaultAreChecked() throws Exception {
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        Document document = Jsoup.parse(content);
        Elements inputCheckboxElements = document.select("input[type=checkbox]");

        List<LintRule> allRules = LintRulesImpl.getInstance().getAllRules();

        assertThat(allRules.size()).isEqualTo(inputCheckboxElements.size());

        Map<LintRule, Element> lintRuleToInputCheckboxMap = Maps.newHashMap();

        for (Element inputCheckboxElement : inputCheckboxElements) {
            String ruleName = inputCheckboxElement.attr("value");

            LintRule lintRule = LintRulesImpl.getInstance().getLintRule(ruleName);

            lintRuleToInputCheckboxMap.put(lintRule, inputCheckboxElement);
        }

        for (Map.Entry<LintRule, Element> entry : lintRuleToInputCheckboxMap.entrySet()) {
            LintRule lintRule = entry.getKey();
            Element inputCheckbox = entry.getValue();

            boolean ruleEnabledByDefault = lintRule.isEnabled();
            boolean inputBoxIsChecked = inputCheckbox.hasAttr("checked");

            assertThat(ruleEnabledByDefault).isEqualTo(inputBoxIsChecked);
        }
    }

}