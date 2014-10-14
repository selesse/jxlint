<%@ page import="com.selesse.jxlint.settings.ProgramSettings" %>
<%@ page import="com.selesse.jxlint.web.HtmlReportExecutor" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="com.google.common.net.UrlEscapers" %>
<%
    PrintWriter printWriter = response.getWriter();
    String reportFileContents = HtmlReportExecutor.reportFileContents();

    if (request.getParameter("save") != null) {
        ProgramSettings programSettings = (ProgramSettings) application.getAttribute("programSettings");
        String programName = UrlEscapers.urlPathSegmentEscaper().escape(programSettings.getProgramName());

        response.setHeader("Content-Disposition", "attachment;filename=" + programName + "-report.html");
    }
    printWriter.println(reportFileContents);
    printWriter.flush();
    printWriter.close();
%>