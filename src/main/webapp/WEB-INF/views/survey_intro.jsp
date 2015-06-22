<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
    <div class="row">
        <div class="col-xs-6">
            <h3 class="text-center">Choose categories that interest you (min. 3):</h3>
            <div class="well" style="max-height: 300px;overflow: auto;">
                <form action="<c:url value="/survey/register-choices" />" method="POST">
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
            url : '<c:url value="/survey" />',
            data: "prod_id=" + prod_id + "&score=" + score
        });
    }

    $(document).ready(function(){
        $("[rel=tooltip]").tooltip({ placement: 'bottom'});
    });
</script>

<script>
    $(function () {
        $('.list-group.checked-list-box .list-group-item').each(function () {

            // Settings
            var $widget = $(this),
                    $checkbox = $('<input type="checkbox" class="hidden" />'),
                    color = ($widget.data('color') ? $widget.data('color') : "primary"),
                    style = ($widget.data('style') == "button" ? "btn-" : "list-group-item-"),
                    settings = {
                        on: {
                            icon: 'glyphicon glyphicon-check'
                        },
                        off: {
                            icon: 'glyphicon glyphicon-unchecked'
                        }
                    };

            $widget.css('cursor', 'pointer')
            $widget.append($checkbox);

            // Event Handlers
            $widget.on('click', function () {
                $checkbox.prop('checked', !$checkbox.is(':checked'));
                $checkbox.triggerHandler('change');
                updateDisplay();
            });
            $checkbox.on('change', function () {
                updateDisplay();
            });


            // Actions
            function updateDisplay() {
                var isChecked = $checkbox.is(':checked');

                // Set the button's state
                $widget.data('state', (isChecked) ? "on" : "off");

                // Set the button's icon
                $widget.find('.state-icon')
                        .removeClass()
                        .addClass('state-icon ' + settings[$widget.data('state')].icon);

                // Update the button's color
                if (isChecked) {
                    $widget.addClass(style + color + ' active');
                } else {
                    $widget.removeClass(style + color + ' active');
                }
            }

            // Initialization
            function init() {

                if ($widget.data('checked') == true) {
                    $checkbox.prop('checked', !$checkbox.is(':checked'));
                }

                updateDisplay();

                // Inject the icon if applicable
                if ($widget.find('.state-icon').length == 0) {
                    $widget.prepend('<span class="state-icon ' + settings[$widget.data('state')].icon + '"></span>');
                }
            }
            init();
        });

        $('#get-checked-data').on('click', function(event) {
            event.preventDefault();
            var checkedItems = {}, counter = 0;
            $("#check-list-box li.active").each(function(idx, li) {
                checkedItems[counter] = $(li).text();
                counter++;
            });
            $('#display-json').html(JSON.stringify(checkedItems, null, '\t'));
        });
    });
</script>

</body>
</html>