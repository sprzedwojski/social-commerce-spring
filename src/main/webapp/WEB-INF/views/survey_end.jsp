<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>Reco4Social</title>
    <link rel="shortcut icon" type="image/x-icon" href="<c:url value="/resources/img/favicon.ico" />" />

    <link rel="stylesheet" href="<c:url value="/resources/css/bootstrap.min.css" />" />
    <link href="<c:url value="/resources/raty/jquery.raty.css" />" rel="stylesheet" />
    <link rel="stylesheet" href="<c:url value="/resources/css/sticky-footer.css" />" />
    <link rel="stylesheet" href="<c:url value="/resources/css/bootply.css" />" />
    <link rel="stylesheet" href="<c:url value="/resources/css/custom.css" />" />
</head>
<body>

<jsp:include page="../templates/navbar.jsp"/>

<img src="<c:url value="/resources/logo_reco4social.jpg" />" class="center"/>


<div class="container">
    <div class="col-md-12">
        <div class="progress">
            <div class="progress-bar progress-bar-info progress-bar-striped" role="progressbar"
                 aria-valuenow="${progress}" aria-valuemin="0" aria-valuemax="100" style="width: ${progress}%">
            </div>
        </div>

        <div class="jumbotron">
            <h1>Thank you!</h1>
            <p>
                I really appreciate your help in getting Reco4Social started.<br>
                Make sure to come back here around September and check out for yourself how the recommendation engine works.
            </p>
            <p>
                <a class="btn btn-primary btn-lg" href="<c:url value="/survey_intro" />" role="button">Rate more!</a>
                <a class="btn btn-primary btn-lg" href="<c:url value="/logout" />" role="button">I'm done</a>
            </p>
        </div>

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

</body>
</html>