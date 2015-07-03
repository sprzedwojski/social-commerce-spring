<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns:th="http://www.thymeleaf.org">
<body>
<div th:fragment="header">
<!-- Fixed navbar -->
<nav class="navbar navbar-default navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">Reco4Social</a>
            <%--<img src="<c:url value="/resources/logo_reco4social.jpg" />"/>--%>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <%--<li class="active"><a href="#">Home</a></li>--%>
                <li><a href="#about">About</a></li>
                <li><a href="#help">Help</a></li>
                <li><a href="#problem">Report problem</a></li>
                <%--<li class="dropdown">--%>
                    <%--<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Dropdown <span class="caret"></span></a>--%>
                    <%--<ul class="dropdown-menu">--%>
                        <%--<li><a href="#">Action</a></li>--%>
                        <%--<li><a href="#">Another action</a></li>--%>
                        <%--<li><a href="#">Something else here</a></li>--%>
                        <%--<li role="separator" class="divider"></li>--%>
                        <%--<li class="dropdown-header">Nav header</li>--%>
                        <%--<li><a href="#">Separated link</a></li>--%>
                        <%--<li><a href="#">One more separated link</a></li>--%>
                    <%--</ul>--%>
                <%--</li>--%>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <c:if test="${param.showLogout}">
                    <li><a href="<c:url value="/logout" />" class="btn" style="float: right">Logout</a></li>
                </c:if>
                <%--<li><a href="../navbar-static-top/">Static top</a></li>--%>
                <%--<li class="active"><a href="./">Fixed top <span class="sr-only">(current)</span></a></li>--%>
            </ul>
        </div><!--/.nav-collapse -->
    </div>
</nav>
</div>
</body>
</html>