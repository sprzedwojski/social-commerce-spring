<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
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
    <%--<div class="jumbotron">--%>
        <%--<h1>${jumboTitle}</h1>--%>
        <%--<p>${jumboText}</p>--%>
        <%--<p><a class="btn btn-primary btn-lg" href="#" role="button">Learn more</a></p>--%>
    <%--</div>--%>

        <div class="col-md-12">
    <div class="progress">
        <div class="progress-bar progress-bar-info progress-bar-striped" role="progressbar" aria-valuenow="20" aria-valuemin="0" aria-valuemax="100" style="width: 20%">
            <span class="sr-only">20% Complete</span>
            20%
        </div>
    </div>


    	<c:forEach items="${productMap}" var="category">
        <div class="productsrow ${category.key}">
            <c:forEach items="${category.value}" var="product">
            <div class="product menu-category ${product.category}">
                <a href="${product.productUrl}" target="_blank" class="menu-item list-group-item" rel="tooltip" title="${product.descriptionEn}">
                    ${product.nameEn}
                    <span class="badge">${product.price} &euro;</span>
                </a>
                <div class="product-image">
                    <img class="product-image menu-item list-group-item" src="${product.imageUrl}">
                </div>
                <div id="${product.id}" class="menu-item list-group-item rating" data-score="${product.rating}"></div>
            </div>
            </c:forEach>
        </div>
        <hr>

        <form action="<c:url value="/survey" />" method="POST" id="next-category">
            <input type="hidden" name="current_category" value="${category.key}"/>
            <button type="submit" class="btn btn-default">Next</button>
        </form>
        </c:forEach>
    </div>

</div>

<footer class="footer">
    <div class="container">
        <p class="text-muted">Created by <a href="mailto:szymon.przedwojski@gmail.com" target="_top">Szymon Przedwojski</a> at <a href="http://amg.net.pl" target="_blank">AMG.net</a></p>
    </div>
</footer>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
<script type="text/javascript" src="<c:url value="/resources/raty/jquery.raty.js" />"></script>
<script>

    $('.rating').raty({
        cancel  : true,
        path: "resources/raty/images/",
        starOff : 'star-off-big.png',
        starOn  : 'star-on-big.png',
        cancelOff: 'cancel-off-big.png',
        cancelOn: 'cancel-on-big.png',
        hints: ['hate it', 'not bad', 'ok', 'like it', 'love it'],        
        score: function() {
            return $(this).attr('data-score');
        },
        click: function(score, evt) {
            rateProductAjax(this.id, score);
        }
    });

    // TODO co jeśli sesja wygaśnie??
    function rateProductAjax(prod_id, score) {
        $.ajax({
            type: 'POST',
            url : '<c:url value="/survey/rate" />',
            data: "prod_id=" + prod_id + "&score=" + score
        });
    }

    $(document).ready(function(){
        $("[rel=tooltip]").tooltip({ placement: 'bottom'});
    });
</script>
</body>
</html>