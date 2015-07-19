<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>Reco4Social::Survey</title>
    <link rel="shortcut icon" type="image/x-icon" href="<c:url value="/resources/img/favicon.ico" />" />

    <link rel="stylesheet" href="<c:url value="/resources/css/bootstrap.min.css" />"/>
    <link href="<c:url value="/resources/raty/jquery.raty.css" />" rel="stylesheet"/>
    <link rel="stylesheet" href="<c:url value="/resources/css/sticky-footer.css" />"/>
    <link rel="stylesheet" href="<c:url value="/resources/css/bootply.css" />"/>
    <link rel="stylesheet" href="<c:url value="/resources/css/custom.css" />"/>
</head>
<body>

<jsp:include page="../templates/navbar.jsp">
    <jsp:param name="showHelp" value="true"/>
    <jsp:param name="showLogout" value="true"/>
</jsp:include>

<img src="<c:url value="/resources/logo_reco4social.jpg" />" class="center"/>

<div class="container">

    <div class="col-md-12">
        <div class="progress">
            <div class="progress-bar progress-bar-info progress-bar-striped" role="progressbar"
                 aria-valuenow="${progress}" aria-valuemin="0" aria-valuemax="100" style="width: ${progress}%">
            </div>
        </div>


        <c:forEach items="${productMap}" var="category">
            <div class="page-header text-center">
                <h1>
                        ${fn:toUpperCase(fn:substring(category.key, 0, 1))}${fn:toLowerCase(fn:substring(category.key, 1,fn:length(category.key)))}
                </h1>
            </div>

            <div class="productsrow ${category.key}">
                <c:forEach items="${category.value}" var="product">
                    <div class="product menu-category ${product.category}">
                        <c:choose>
                            <c:when test="${not empty product.productUrl}">
                                <a href="${product.productUrl}" target="_blank" class="menu-item list-group-item" rel="tooltip"
                                   title="${product.descriptionEn}">
                                    <c:if test="${product.price > 0.0}">
                                        <span class="badge">${product.price} &euro;</span>
                                    </c:if>
                                    ${product.nameEn}
                                </a>
                            </c:when>
                            <c:otherwise>
                                <div class="menu-item list-group-item" rel="tooltip"
                                   title="${product.descriptionEn}">
                                    <c:if test="${product.price > 0.0}">
                                        <span class="badge">${product.price} &euro;</span>
                                    </c:if>
                                    ${product.nameEn}
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <div class="product-image">
                            <img class="product-image menu-item list-group-item" src="${product.imageUrl}">
                        </div>
                        <div id="${product.id}" class="menu-item list-group-item rating"
                             data-score="${product.rating}"></div>
                    </div>
                    <div class="product-category" style="display: none;" data-category="${category.key}"></div>
                </c:forEach>
            </div>
            <hr>

            <form action="<c:url value="/survey" />" method="POST" id="next-category">
                <input type="hidden" name="current_category" value="${category.key}"/>
                <button type="submit"
                        class="btn btn-primary btn-lg btn-block btn-submit-bottom">${isLast ? 'Finish' : 'Next category'}</button>
            </form>
        </c:forEach>
    </div>

</div>

<jsp:include page="../templates/footer.jsp"/>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
<script type="text/javascript" src="<c:url value="/resources/raty/jquery.raty.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/js/my_raty_survey.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/js/my_raty_example.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/js/my_raty_common.js" />"></script>

</body>
</html>