<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--<c:forEach items="${productMap}" var="product">
    ${product.key.nameEn}: ${product.key.rating}<br>
</c:forEach>--%>

<html>
<head>
    <link href="<c:url value="/resources/raty/jquery.raty.css" />" rel="stylesheet"/>
    <link rel="stylesheet" href="<c:url value="/resources/css/bootply.css" />"/>
    <link rel="stylesheet" href="<c:url value="/resources/css/custom.css" />"/>
</head>

<div class="productsrow">
    <c:forEach items="${productMap}" var="product">
        <div class="product menu-category">
            <div class="menu-item list-group-item" rel="tooltip"
                 title="${product.key.descriptionEn}">
                <c:if test="${product.key.price > 0.0}">
                    <span class="badge">${product.key.price} &euro;</span>
                </c:if>
                    ${product.key.nameEn}
            </div>

            <div class="product-image">
                <img class="product-image menu-item list-group-item" src="${product.key.imageUrl}">
            </div>
            <div id="${product.key.id}" class="menu-item list-group-item rating"
                 data-score="${product.key.rating}"></div>
        </div>
    </c:forEach>
</div>

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
        }
/*            click: function(score, evt) {
            rateProductAjax(this.id, score, $(".product-category").attr('data-category'));
        }*/
    });
</script>

</html>