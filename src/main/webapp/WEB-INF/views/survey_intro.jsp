<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>Reco4Social</title>
    <link rel="shortcut icon" type="image/x-icon" href="<c:url value="/resources/img/favicon.ico" />" />

    <link rel="stylesheet" href="<c:url value="/resources/css/bootstrap.min.css" />"/>
    <link href="<c:url value="/resources/raty/jquery.raty.css" />" rel="stylesheet"/>
    <link rel="stylesheet" href="<c:url value="/resources/css/sticky-footer.css" />"/>
    <link rel="stylesheet" href="<c:url value="/resources/css/bootply.css" />"/>
    <link rel="stylesheet" href="<c:url value="/resources/css/custom.css" />"/>
</head>
<body>

<jsp:include page="../templates/navbar.jsp">
    <jsp:param name="showLogout" value="true"/>
</jsp:include>

<img src="<c:url value="/resources/logo_reco4social.jpg" />" class="center"/>

<div class="container">
    <div class="jumbotron">
        <h1>${jumboTitle}</h1>

        <p>${jumboText}</p>

        <p>
            <a class="btn btn-primary btn-lg" href="" data-toggle="modal" data-target="#aboutModal">Learn more</a>
            <button id="btn-scroll2start" class="btn btn-primary btn-lg" role="button">Go for it!</button>
        </p>
    </div>

    <div class="jumbotron" id="category-chooser">
        <form action="<c:url value="/survey_intro" />" method="POST" id="categories-form">

            <h3>Choose categories that interest you</h3><h5>(At least 3, the more the better)</h5>
            <br>

            <c:forEach items="${productMap}" var="category" varStatus="status">
                <c:if test="${status.index % 3 == 0}">
                    <div class="btn-group btn-group-justified" data-toggle="buttons">
                </c:if>

                <label class="btn btn-default">
                    <img class="category-icon" src="<c:url value="/resources/img/icon-${category.key}.png" />"/><br>
                    <input id="${category.key}" name="categories[]" type="checkbox"
                           value="${category.key}"> ${category.key.toUpperCase()}
                </label>

                <c:if test="${status.index % 3 == 2 or status.last}">
                    </div>
                </c:if>

            </c:forEach>

            <br>
            <label for="categories[]" class="error"></label>
            <br>
            <button type="submit" class="btn btn-primary btn-lg btn-block">Start</button>
        </form>
    </div>

</div>

<jsp:include page="../templates/footer.jsp"/>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
<script src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.13.1/jquery.validate.min.js"></script>
<script src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.11.1/additional-methods.js"></script>
<script type="text/javascript" src="<c:url value="/resources/raty/jquery.raty.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/js/my_raty_example.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/js/my_raty_common.js" />"></script>

<script type="text/javascript">
    $(document).ready(function () {
        $(':input:checked').parent('.btn').addClass('active');
        $("[name='categories']").change(function () {
            if (this.checked) {
                this.setAttribute("checked", "checked");
                this.checked = true;
            } else {
                this.setAttribute("checked", ""); // For IE
                this.removeAttribute("checked"); // For other browsers
                this.checked = false;
            }
        });

        $("#categories-form").validate({
            rules: {
                'categories[]': {
                    required: true,
                    minlength: 3
                }
            },
            messages: {
                'categories[]': "Please select at least 3 categories."
            }
        });

        $("#btn-scroll2start").click(function() {
            $('html, body').animate({
                scrollTop: $("#category-chooser").offset().top
            }, 600);
        });
    });
</script>
</body>
</html>