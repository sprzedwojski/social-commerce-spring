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
    <div class="col-md-12">
        <div class="progress">
            <div class="progress-bar progress-bar-info progress-bar-striped" role="progressbar"
                 aria-valuenow="${progress}" aria-valuemin="0" aria-valuemax="100" style="width: ${progress}%">
            </div>
        </div>

        <div class="jumbotron">
            <h1>The end</h1>
            <p>${jumboText}</p>
            <p><a class="btn btn-primary btn-lg" href="#" role="button">Once again!</a></p>
        </div>

    </div>
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