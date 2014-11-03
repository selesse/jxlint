<%@ page import="com.google.common.base.Joiner" %>
<%@ page import="com.google.common.collect.Lists" %>
<%@ page import="com.selesse.jxlint.model.rules.LintRule" %>
<%@ page import="com.selesse.jxlint.settings.ProgramSettings" %>
<%@ page import="com.selesse.jxlint.web.HtmlReportExecutor" %>
<%@ page import="java.util.List" %>
<%
    String folder = request.getParameter("folder");
    String[] rulesEnabled = request.getParameterValues("ruleEnabled");

    ProgramSettings programSettings = (ProgramSettings) application.getAttribute("programSettings");

    String nameAndVersion = programSettings.getProgramName() + " " + programSettings.getProgramVersion();
%>
<!doctype html>
<html>
<head>
    <title> <%= nameAndVersion %> - web validator </title>
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

        function displaySave() {
            document.getElementById('hidden-initially').style.display = 'block';
        }
    </script>
    <style>
        div#hidden-initially {
            display: none;
        }
    </style>
</head>
<body>
<div id="container">
    <div id="content">
        <h1> <%= nameAndVersion %> - web validator </h1>

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

        <form action="index.jsp" target="_blank" onsubmit="displaySave()">
            <% for (LintRule lintRule : HtmlReportExecutor.getAvailableRules()) { %>
                <input type="checkbox" name="ruleEnabled" value="<%=lintRule.getName()%>" <%=ruleIsChecked(rulesEnabled, lintRule) ? "checked" : "" %> > <%=lintRule.getName()%> <br>
            <% } %>
            <br>
            <input type="text" name="folder" <%= folder == null ? "" : "value=\"" + folder + "\"" %> placeholder="Folder to run validations against" />
        </form>

        <div class="save-report" id="hidden-initially">
            <h4><a href="report.jsp?save"> Save Report </a></h4>
        </div>

        <%
            if (request.getParameter("folder") != null && rulesEnabled != null) {

                List<String> jxlintArgs = Lists.newArrayList("--check");

                jxlintArgs.add(Joiner.on(",").join(rulesEnabled));
                jxlintArgs.add(folder);

                rulesEnabled = new String[jxlintArgs.size()];
                jxlintArgs.toArray(rulesEnabled);

                HtmlReportExecutor htmlReportExecutor = new HtmlReportExecutor(folder, programSettings, rulesEnabled);
                if (htmlReportExecutor.directoryExists()) {
                    htmlReportExecutor.generateReport();

                    response.sendRedirect("report.jsp");
        %>
            <% } else { %>
            Directory '<%= jxlintArgs.get(jxlintArgs.size() - 1) %>' does not exist
            <% }
            } %>
    </div>
</div>
</body>
</html>
