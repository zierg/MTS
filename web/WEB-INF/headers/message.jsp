<%-- 
    Document   : message
    Created on : 26.04.2014, 18:08:07
    Author     : Ivan
--%>

<%@page import="pack.HTMLHelper"%>
<%@page import="pack.MessageBean"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <% String ROOT = request.getContextPath();%>
        <%= HTMLHelper.includeCSS(ROOT)%>
    </head>
    <body>
        <%
            MessageBean requestMessage = (MessageBean) request.getAttribute(MessageBean.ATTR_NAME);
            if (requestMessage != null) {
                %>
                <label class="infoMessage">
                    <%= requestMessage.getMessage()%>
                </label>
                <%
            }
            
            MessageBean sessionMessage = (MessageBean) session.getAttribute(MessageBean.ATTR_NAME);
            if (sessionMessage != null) {
                %>
                <label class="infoMessage">
                    <%= sessionMessage.getMessage()%>
                </label>
                <%
                session.setAttribute(MessageBean.ATTR_NAME, null);
            }
        %>
        
    </body>
</html>
