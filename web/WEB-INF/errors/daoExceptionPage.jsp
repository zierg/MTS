<%-- 
    Document   : daoExceptionPage
    Created on : 24.04.2014, 17:22:36
    Author     : Ivan
--%>
<%--
    Сюда попадаем, если не обработано DaoException.
--%>
<%@page import="java.io.PrintWriter"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <% String ROOT = request.getContextPath();%>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Произошла ошибка базы данных.</h1>
        Подробнее в логах.
        <a class="other" href="<%= ROOT%>">На главную</a>
    </body>
</html>