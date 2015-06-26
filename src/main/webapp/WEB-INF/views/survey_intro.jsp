<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <%--<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">--%>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>Survey</title>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
    <link href="<c:url value="/resources/raty/jquery.raty.css" />" rel="stylesheet" />
    <link rel="stylesheet" href="<c:url value="/resources/css/sticky-footer.css" />" />
    <link rel="stylesheet" href="<c:url value="/resources/css/bootply.css" />" />
</head>
<body>

<div class="container">
    <a href="<c:url value="/logout" />" class="btn" style="float: right">Logout</a>
</div>

<img src="<c:url value="/resources/logo_reco4social.jpg" />" class="center"/>


<div class="container">
    <div class="jumbotron">
        <h1>${jumboTitle}</h1>
        <p>${jumboText}</p>
        <p><a class="btn btn-primary btn-lg" href="#" role="button">Learn more</a></p>
    </div>

    <%--<div class="col-md-12">--%>
    	<%--<c:forEach items="${productMap}" var="category">--%>
        <%--<div class="productsrow ${category.key}">--%>
            <%--<c:forEach items="${category.value}" var="product">--%>
            <%--<div class="product menu-category ${product.category}">--%>
                <%--<a href="${product.productUrl}" target="_blank" class="menu-item list-group-item" rel="tooltip" title="${product.descriptionEn}">--%>
                    <%--${product.nameEn}--%>
                    <%--<span class="badge">${product.price} &euro;</span>--%>
                <%--</a>--%>
                <%--<div class="product-image">--%>
                    <%--<img class="product-image menu-item list-group-item" src="${product.imageUrl}">--%>
                <%--</div>--%>
                <%--<div id="${product.id}" class="menu-item list-group-item rating" data-score="${product.rating}"></div>--%>
            <%--</div>--%>
            <%--</c:forEach>--%>
        <%--</div>--%>
        <%--<hr>--%>
        <%--</c:forEach>--%>
    <%--</div>--%>
    <%--
    <div class="row">
        <div class="col-xs-6">
            <h3 class="text-center">Choose categories that interest you (min. 3):</h3>
            <div class="well" style="max-height: 300px;overflow: auto;">
                <form action="<c:url value="/survey_intro" />" method="POST" id="list_form">
                    <ul class="list-group checked-list-box">
                        <c:forEach items="${productMap}" var="category">
                            <li class="list-group-item" id="${category.key}">${category.key}</li>
                        </c:forEach>
                    </ul>
                    <input type="submit">
                </form>
            </div>
        </div>
    </div>
    --%>

    <%--<div class="row">--%>
        <%--<div class="col-xs-6">--%>
        <div class="jumbotron">
            <form action="<c:url value="/survey_intro" />" method="POST">

                <%--<h3 class="text-center">Choose categories that interest you (min. 3):</h3>--%>

                <h2>Choose categories that interest you (min. 3):</h2>
                <br>

                <div class="btn-group" data-toggle="buttons">

                    <%--<ul>--%>
                        <c:forEach items="${productMap}" var="category">
                            <label class="btn btn-default">
                                <input id="${category.key}" name="categories" type="checkbox" value="${category.key}"> ${category.key.toUpperCase()}
                            </label>
                        </c:forEach>
                    <%--</ul>--%>
                    <%--
                    <label class="btn btn-primary">
                        <input type="checkbox"> Option 1
                    </label>
                    <label class="btn btn-primary">
                        <input type="checkbox"> Option 2
                    </label>
                    <label class="btn btn-primary">
                        <input type="checkbox"> Option 3
                    </label>
                    --%>
                </div>
                <br>
                <br>
                <button type="submit" class="btn btn-default">Submit</button>
            </form>
        </div>
        <%--</div>--%>
    <%--</div>--%>

    <%--<form action="/survey/register-choices">--%>
        <%--<c:forEach items="${productMap}" var="category">--%>
            <%--<input type="checkbox" id="${category.key}">${category.key}<br>--%>
        <%--</c:forEach>--%>
    <%--</form>--%>
</div>

<footer class="footer">
    <div class="container">
        <p class="text-muted">Created by <a href="mailto:szymon.przedwojski@gmail.com" target="_top">Szymon Przedwojski</a> at <a href="http://amg.net.pl" target="_blank">AMG.net</a></p>
    </div>
</footer>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
<script type="text/javascript" src="<c:url value="/resources/raty/jquery.raty.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/js/my_raty.js" />"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $(':input:checked').parent('.btn').addClass('active');
        $("[name='categories']").change(function() {
            if(this.checked) {
                this.setAttribute("checked", "checked");
                this.checked = true;
            } else {
                this.setAttribute("checked", ""); // For IE
                this.removeAttribute("checked"); // For other browsers
                this.checked = false;
            }
        })
    });
</script>
</body>
</html>