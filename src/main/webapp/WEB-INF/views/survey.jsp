<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Survey</title>

    <link href="<c:url value="/resources/raty/jquery.raty.css" />" rel="stylesheet" />
    <style type="text/css">
        img.center {
            display: block;
            margin-left: auto;
            margin-right: auto;
            padding-bottom: 20px;
        }
    </style>
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet"
          href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
    <style>
        /*
A custom Bootstrap 3.2 theme from http://bootply.com
This CSS code should follow the 'bootstrap.css'
in your HTML file.

license: MIT
author: bootply.com
*/

        /*body {*/
            /*padding-top:70px;*/
        /*}*/

        .productsrow {
            -moz-column-width: 18em;
            -webkit-column-width: 18em;
            -moz-column-gap: 1em;
            -webkit-column-gap: 1em;
        }

        .menu-category {
            display: inline-block;
            margin-bottom:  0.25rem;
            padding:  1rem;
            width:  100%;
        }

        .product-image {
            width: 100%;
        }

        .product {
            padding-top:22px;
        }

        .btn-product {
            background-color:#222;
            color:#eee;
            border-radius:0;
        }

        .yellow {
            color:yellow;
            text-shadow:#ccc 1px 1px 0;
        }

        /* end custom CSS */
    </style>
    <style type="text/css">
        /* Sticky footer styles
        -------------------------------------------------- */
        html {
            position: relative;
            min-height: 100%;
        }
        body {
            /* Margin bottom by footer height */
            margin-bottom: 60px;
        }
        .footer {
            position: absolute;
            bottom: 0;
            width: 100%;
            /* Set the fixed height of the footer here */
            height: 60px;
            background-color: #f5f5f5;
        }

        .container .text-muted {
            margin: 20px 0;
        }
    </style>
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

    <div class="col-md-12">
        <div class="productsrow">
            <c:forEach items="${productList}" var="product">
            <div class="product menu-category">
                <a href="${product.productUrl}" target="_blank" class="menu-item list-group-item" rel="tooltip" title="${product.descriptionEn}">
                    ${product.nameEn}
                    <span class="badge">${product.price} &euro;</span>
                </a>
                <div class="product-image">
                    <img class="product-image menu-item list-group-item" src="${product.imageUrl}">
                </div>
                <div id="${product.id}" class="menu-item list-group-item rating"></div>
            </div>
            </c:forEach>
        </div>
    </div>

</div>

<footer class="footer">
    <div class="container">
        <p class="text-muted">Created by <a href="mailto:szymon.przedwojski@gmail.com" target="_top">Szymon Przedwojski</a> at <a href="http://amg.net.pl" target="_blank">AMG.net</a></p>
    </div>
</footer>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
<!-- Latest compiled and minified JavaScript -->
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
        click: function(score, evt) {
            rateProductAjax(this.id, score);
        }
    });

    // TODO uid z sesji (po stronie Springa)?
    function rateProductAjax(/*uid, */prod_id, score) {
        $.ajax({
            type: 'POST',
            url : '<c:url value="/survey" />',
            data:/*'uid=' + uid + */"prod_id=" + prod_id + "&score=" + score
        });
    }

    $(document).ready(function(){
        $("[rel=tooltip]").tooltip({ placement: 'bottom'});
    });
</script>
</body>
</html>