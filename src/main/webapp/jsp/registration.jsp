<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:if test="${sessionScope.language != null}">
    <fmt:setLocale value="${sessionScope.language}"/>
</c:if>
<fmt:setBundle basename="ui"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><fmt:message key="ui.registration"/></title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<jsp:include page="header.jsp"/>
<section id="registration" class="section">
    <div class="container tagline">
        <br>
        <h1><fmt:message key="ui.registration"/></h1>
        <br>
    <c:if test="${sessionScope.registrationStatus != ''}">
        <em><c:out value="${sessionScope.registrationStatus}"/></em><br><br>
    </c:if>
        <form action="controller" method="post">
            <input type="hidden" name="command" value="register_user"/>
            <label for="username"><fmt:message key="ui.username"/>: </label><br>
            <input type="text" name="username" id="username"
                   value="${username}" required pattern="[a-zA-Zа-яА-Я0-9_-]{3,30}"><br><br>
            <label for="password"><fmt:message key="ui.password"/>: </label><br>
            <input type="text" minlength="5" maxlength="40" name="password" id="password"
                   value="${password}" required><br><br>
            <label for="confirmPassword"><fmt:message key="ui.confirmPassword"/>: </label><br>
            <input type="text" minlength="5" maxlength="40" name="confirmPassword" id="confirmPassword"
                   value="${confirmPassword}" required><br><br>
            <label for="firstname"><fmt:message key="ui.firstname"/>: </label><br>
            <input type="text" name="firstname" id="firstname" value="${firstname}" pattern="[a-zA-Zа-яА-Я-]{2,30}"><br><br>
            <label for="lastname"><fmt:message key="ui.lastname"/>:<br>
            <input type="text" name="lastname" id="lastname" value="${lastname}" pattern="[a-zA-Zа-яА-Я-]{2,30}"><br><br>
            <label for="email">e-mail: </label><br>
            <input type="text" name="email" id="email" value="${email}" required pattern="^[a-zA-Z\d._%+-]+@[a-zA-Z\d.-]+\.[a-zA-Z]{2,6}$"><br><br>
            <input type="submit" name="signup" value="<fmt:message key="ui.signUp"/>"><br><br>
        </form>
        <br>
    </div>
</section>
<jsp:include page="footer.jsp"/>
</body>
</html>
