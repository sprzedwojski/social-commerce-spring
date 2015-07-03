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

    <%--<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">--%>
    <link rel="stylesheet" href="<c:url value="/resources/css/bootstrap.min.css" />" />
    <link href="<c:url value="/resources/raty/jquery.raty.css" />" rel="stylesheet" />
    <link rel="stylesheet" href="<c:url value="/resources/css/sticky-footer.css" />" />
    <link rel="stylesheet" href="<c:url value="/resources/css/bootply.css" />" />
    <link rel="stylesheet" href="<c:url value="/resources/css/custom.css" />" />
</head>
<body>

<jsp:include page="../templates/navbar.jsp">
    <jsp:param name="showLogout" value="true" />
</jsp:include>

<%--<div class="container">--%>
    <%--<a href="<c:url value="/logout" />" class="btn" style="float: right">Logout</a>--%>
<%--</div>--%>

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
            <form action="<c:url value="/survey_intro" />" method="POST" id="categories-form">

                <%--<h3 class="text-center">Choose categories that interest you (min. 3):</h3>--%>

                <h3>Choose categories that interest you</h3>
                <br>

                <%--<div class="btn-toolbar" role="toolbar">--%>
                <div class="btn-group btn-group-justified" data-toggle="buttons"> <!--btn-group-justified-->

                    <%--<ul>--%>
                        <c:forEach items="${productMap}" var="category">
                            <label class="btn btn-default">
                                <img class="category-icon" src="<c:url value="/resources/img/icon-${category.key}.png" />" /><br>
                                <input id="${category.key}" name="categories[]" type="checkbox" value="${category.key}"> ${category.key.toUpperCase()}
                            </label>
                            <%--<br>--%>
                        </c:forEach>

                        <%--FIXME temp hardcode--%>

                        <label class="btn btn-default">
                            <img class="category-icon" src="<c:url value="/resources/img/icon-shoes.png" />" /><br>
                            <input id="shoes" name="categories[]" type="checkbox" value="shoes"> SHOES
                        </label>
                    </div>
                    <div class="btn-group btn-group-justified" data-toggle="buttons">
                        <label class="btn btn-default">
                            <img class="category-icon" src="<c:url value="/resources/img/icon-jewellery.png" />" /><br>
                            <input id="jewellery" name="categories[]" type="checkbox" value="jewellery"> JEWELLERY
                        </label>

                        <label class="btn btn-default">
                            <img class="category-icon" src="<c:url value="/resources/img/icon-sports.png" />" /><br>
                            <input id="sports" name="categories[]" type="checkbox" value="sports"> SPORTS
                        </label>

                        <label class="btn btn-default">
                            <img class="category-icon" src="<c:url value="/resources/img/icon-bags.png" />" /><br>
                            <input id="bags" name="categories[]" type="checkbox" value="bags"> BAGS
                        </label>
                    </div>
                    <div class="btn-group btn-group-justified" data-toggle="buttons">
                        <label class="btn btn-default">
                            <img class="category-icon" src="<c:url value="/resources/img/icon-books.png" />" /><br>
                            <input id="books" name="categories[]" type="checkbox" value="books"> BOOKS
                        </label>

                        <label class="btn btn-default">
                            <img class="category-icon" src="<c:url value="/resources/img/icon-cars.png" />" /><br>
                            <input id="cars" name="categories[]" type="checkbox" value="cars"> CARS
                        </label>

                        <label class="btn btn-default">
                            <img class="category-icon" src="<c:url value="/resources/img/icon-movies.png" />" /><br>
                            <input id="movies" name="categories[]" type="checkbox" value="movies"> MOVIES
                        </label>
                    </div>
                    <div class="btn-group btn-group-justified" data-toggle="buttons">
                        <label class="btn btn-default">
                            <img class="category-icon" src="<c:url value="/resources/img/icon-music.png" />" /><br>
                            <input id="music" name="categories[]" type="checkbox" value="music"> MUSIC
                        </label>

                        <label class="btn btn-default">
                            <img class="category-icon" src="<c:url value="/resources/img/icon-outdoors.png" />" /><br>
                            <input id="outdoors" name="categories[]" type="checkbox" value="outdoors"> OUTDOORS
                        </label>

                        <label class="btn btn-default">
                            <img class="category-icon" src="<c:url value="/resources/img/icon-toys.png" />" /><br>
                            <input id="toys" name="categories[]" type="checkbox" value="toys"> TOYS
                        </label>
                    </div>
                <br>
                <label for="categories[]" class="error"></label>
                <br>
                <button type="submit" class="btn btn-primary btn-lg">Submit</button>
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
<script src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.13.1/jquery.validate.min.js"></script>
<script src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.11.1/additional-methods.js"></script>
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
        });

        $("#categories-form").validate({
            rules: {
                'categories[]': {
                    required: true,
                    minlength: 2
                }
            },
            messages: {
                'categories[]': "Please select at least 2 categories."
            }
        });
    });
</script>
</body>
</html>