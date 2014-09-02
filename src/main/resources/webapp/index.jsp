<%@ page import="com.google.common.base.Joiner" %>
<%@ page import="com.google.common.collect.Lists" %>
<%@ page import="com.selesse.jxlint.model.ProgramOptions" %>
<%@ page import="com.selesse.jxlint.model.rules.LintRule" %>
<%@ page import="com.selesse.jxlint.settings.ProgramSettings" %>
<%@ page import="com.selesse.jxlint.web.HtmlReportExecutor" %>
<%@ page import="java.util.List" %>
<!doctype html>
<html>
<head>
    <title> web validator </title>
    <link rel="stylesheet" type="text/css" href="main.css"/>
    <script>
        function setAllCheckboxes(select) {
            var inputs = document.getElementsByTagName("input");
            for (var i = 0; i < inputs.length; i++) {
                if (inputs[i].type == 'checkbox') {
                    inputs[i].checked = select;
                }
            }
        }

        function selectAll() {
            setAllCheckboxes(true);
        }

        function deselectAll() {
            setAllCheckboxes(false);
        }
    </script>
</head>
<body>
<div id="container">
    <div id="content">

        <h1> web validator </h1>

        <%
            String folder = request.getParameter("folder");
            String[] rulesEnabled = request.getParameterValues("ruleEnabled");

            ProgramSettings programSettings = (ProgramSettings) application.getAttribute("programSettings");
        %>

        <%!
            private boolean ruleIsChecked(String[] rulesEnabled, LintRule lintRule) {
                if (rulesEnabled == null) {
                    return lintRule.isEnabled();
                }
                List<String> rulesEnabledList = Lists.newArrayList(rulesEnabled);
                return rulesEnabledList.contains(lintRule.getName());
            }
        %>

        <input type="button" value="Select all" onclick="selectAll()"/>
        <input type="button" value="Deselect all" onclick="deselectAll()"/>

        <form action="index.jsp">
            <%

                for (LintRule lintRule : HtmlReportExecutor.getAvailableRules()) {
            %>
            <input type="checkbox" name="ruleEnabled" value="<%=lintRule.getName()%>" <%=ruleIsChecked(rulesEnabled, lintRule) ? "checked" : "" %> > <%=lintRule.getName()%> <br>
            <%
                }
            %>
            <br>
            <input type="text" name="folder" <%= folder == null ? "" : "value=\"" + folder + "\"" %> placeholder="Folder to run validations against" />
        </form>

        <%
            if (request.getParameter("folder") != null && rulesEnabled != null) {

                List<String> jxlintArgs = Lists.newArrayList("--check");

                jxlintArgs.add(Joiner.on(",").join(rulesEnabled));
                jxlintArgs.add(folder);

                rulesEnabled = new String[jxlintArgs.size()];
                jxlintArgs.toArray(rulesEnabled);


                HtmlReportExecutor htmlReportExecutor = new HtmlReportExecutor(programSettings, rulesEnabled);
                if (htmlReportExecutor.directoryExists()) {
                    htmlReportExecutor.generateReport();
        %>
        <h4><a href="report.jsp"> HTML Report </a></h4>
        <iframe src="report.jsp"></iframe>
        <% } else { %>
        Directory '<%= jxlintArgs.get(jxlintArgs.size() - 1) %>' does not exist
        <% }
    %>
    </div>
</div>
<% } else { %>
</div>
</div>
<% } %>
</body>
</html>
