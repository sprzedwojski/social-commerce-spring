<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head th:replace="login :: head">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Survey</title>

    <link href="<c:url value="/resources/raty/jquery.raty.css" />" rel="stylesheet" />
</head>
<body>

<c:forEach items="${productSet}" var="product">
    ${product.name} <div></div><br>
</c:forEach>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
<script type="text/javascript" src="<c:url value="/resources/raty/jquery.raty.js" />"></script>
<script>
    $('div').raty();
</script>
</body>
</html>