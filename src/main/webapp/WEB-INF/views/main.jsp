<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html xmlns:th="http://www.thymeleaf.org">
<head th:fragment="head">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>Reco4Social</title>
    <link rel="shortcut icon" type="image/x-icon" href="<c:url value="/resources/img/favicon.ico" />"/>

    <link rel="stylesheet" href="<c:url value="/resources/css/bootstrap.min.css" />"/>
    <link rel="stylesheet" href="<c:url value="/resources/css/sticky-footer.css" />"/>
    <link rel="stylesheet" href="<c:url value="/resources/css/custom.css" />"/>

</head>
<body>

<jsp:include page="../templates/navbar.jsp">
    <jsp:param name="showHelp" value="false"/>
    <jsp:param name="showLogout" value="true"/>
</jsp:include>

<div class="jumbotron text-center">
    <h1 class="big">Recommender Store</h1>

    <p>Welcome!</p>
</div>

<div class="container">

    <%--<div id="loginbox" style="margin-top: 50px;"
         class="mainbox col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2">
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title text-center">Items recommended for you</div>
            </div>



        </div>
    </div>--%>

    <div class="col-md-12">
        <div class="page-header text-center">
            <h1 id="rec-title">
                Generating recommendations...
            </h1>
        </div>

        <div id="recommendations-box" class="text-center">
            <img src='<c:url value="/resources/ajax-loader3.gif" />' style="margin: auto"/>
        </div>
    </div>

    <%--<div class="col-md-12">

        <c:forEach items="${productMap}" var="category">
            <div class="page-header text-center">
                <h1>

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

        </c:forEach>
    </div>--%>

</div>

<jsp:include page="../templates/footer.jsp"/>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>

<script type="text/javascript">

    var get_recommendations = function() {
        $.ajax({
            type: 'GET',
            url : '<c:url value="/recommendations" />',
            success: function(data, textStatus) {
                $("#rec-title").html("Items recommended for you");
                $("#recommendations-box").html(data);
            },
            error: function() {
                alert('Not OKay');
            }
        });
    };

    $(document).ready(get_recommendations);
</script>

<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
</body>
</html>
