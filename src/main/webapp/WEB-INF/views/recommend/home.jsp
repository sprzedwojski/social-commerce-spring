<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

<jsp:include page="../../templates/navbar.jsp">
    <jsp:param name="showHelp" value="false"/>
    <jsp:param name="showLogout" value="false"/>
</jsp:include>

<div class="jumbotron text-center">
    <h1 class="big">Recommender Store</h1>

    <p>Get recommendations</p>
</div>

<div class="container">

    <div id="loginbox" style="margin-top: 50px;"
         class="mainbox col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2">
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title text-center">Find similar users</div>
            </div>

            <div style="margin: auto; font-family: monospace">
                <br>User Id ................ <input id="custom_uid" type="text"/>
                <br>Lowest rating .......... <input id="lowest_rating" type="text"/>
                <br>Num of similar users ... <input id="num_of_similar_users" type="text"/>
                <br>Min sim users ratings .. <input id="min_sim_users_ratings" type="text"/>
                <br>
                <button id="submit_custom_uid">Submit</button>
                <button id="submit_all">All</button>
                <button id="submit_random">Random</button>
                <br>
                <button id="submit_all_classification">Classification all</button>
            </div>

        </div>
    </div>

</div>

<jsp:include page="../../templates/footer.jsp"/>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>

<script type="text/javascript">
    $("#submit_custom_uid").click(function() {
        $.ajax({
            type: 'POST',
            url : '<c:url value="/recommend" />',
            data: "user_id=" + $("#custom_uid").val()
                    + "&lowest_rating=" + $("#lowest_rating").val()
                    + "&num_of_similar_users=" + $("#num_of_similar_users").val()
                    + "&min_sim_users_ratings=" + $("#min_sim_users_ratings").val()
        });
    });
    $("#submit_all").click(function() {
        $.ajax({
            type: 'POST',
            url : '<c:url value="/recommend/all" />',
            data: "lowest_rating=" + $("#lowest_rating").val()
            + "&num_of_similar_users=" + $("#num_of_similar_users").val()
            + "&min_sim_users_ratings=" + $("#min_sim_users_ratings").val()
        });
    });
    $("#submit_random").click(function() {
        $.ajax({
            type: 'POST',
            url : '<c:url value="/recommend/all" />',
            data: "lowest_rating=" + $("#lowest_rating").val()
            + "&num_of_similar_users=" + $("#num_of_similar_users").val()
            + "&min_sim_users_ratings=" + $("#min_sim_users_ratings").val()
            + "&random_users=true"
        });
    });
    $("#submit_all_classification").click(function() {
        $.ajax({
            type: 'POST',
            url : '<c:url value="/recommend/all_classification" />',
            data: "lowest_rating=" + $("#lowest_rating").val()
            + "&num_of_similar_users=" + $("#num_of_similar_users").val()
            + "&min_sim_users_ratings=" + $("#min_sim_users_ratings").val()
        });
    });
</script>

<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
</body>
</html>
