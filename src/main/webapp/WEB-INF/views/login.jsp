<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html xmlns:th="http://www.thymeleaf.org">
<head th:fragment="head">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>Reco4Social</title>
    <link rel="shortcut icon" type="image/x-icon" href="<c:url value="/resources/img/favicon.ico" />"/>

    <style>
        .gigya-login-footer {
            display: none !important;
        }

        img.center {
            display: block;
            margin-left: auto;
            margin-right: auto;
        }
    </style>

    <!-- gigya.js script should only be included once -->
    <%--3_Voo2zFJ8wiFw7Mjd2UWjdsGjX5xNOF-Sf9wj_ilQWk4MKEcav_2fsr28Ag2gTwvM--%>
    <script type="text/javascript"
            src="http://cdn.gigya.com/js/gigya.js?apiKey=${apikey}">
        {
            siteName: '${sitename}',
            enabledProviders:'facebook'
        }
    </script>
    <script type="text/javascript">
        var login_params =
        {
            version: 2
            , showTermsLink: 'false'
            , height: 92
            , width: 150
            , containerID: 'componentDiv'
            , UIConfig: '<config><body><controls><snbuttons buttonsize="65" /></controls></body></config>'
            , autoDetectUserProviders: ''
            , facepilePosition: 'none'
        }
    </script>

    <link rel="stylesheet" href="<c:url value="/resources/css/bootstrap.min.css" />"/>
    <link rel="stylesheet" href="<c:url value="/resources/css/sticky-footer.css" />"/>
    <link rel="stylesheet" href="<c:url value="/resources/css/custom.css" />"/>

</head>
<body>

<jsp:include page="../templates/navbar.jsp">
    <jsp:param name="showHelp" value="false"/>
    <jsp:param name="showLogout" value="false"/>
</jsp:include>

<div class="jumbotron text-center">
    <h1 class="big">Recommender Store</h1>

    <p>Login, rate and help make science!</p>
</div>

<div class="container">

    <div id="loginbox" style="margin-top: 50px;"
         class="mainbox col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2">
        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="panel-title text-center">Sign in with your social network account</div>
            </div>

            <div style="padding-top:30px" class="panel-body">
                <div id="componentDiv" style="margin:auto"></div>

                <form id="login_form" action="#" th:action="@{/}" th:object="${user}" method="post">
                    <input type="hidden" name="UID" id="UID" th:field="*{UID}"/>
                    <input type="hidden" name="signatureTimestamp" id="signatureTimestamp"
                           th:field="*{signatureTimestamp}"/>
                    <input type="hidden" name="UIDSignature" id="UIDSignature" th:field="*{UIDSignature}"/>
                </form>
            </div>
        </div>
    </div>

    <div style="display: none;">
        <input id="custom_uid" type="text"/>
        <button id="submit_custom_uid">Submit</button>
    </div>
</div>

<jsp:include page="../templates/footer.jsp"/>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
<script type="text/javascript">
    function GetUID(eventObj) {
        document.getElementById("UID").setAttribute("value", eventObj.UID);
        document.getElementById("signatureTimestamp").setAttribute("value",
                eventObj.signatureTimestamp);
        document.getElementById("UIDSignature").setAttribute("value",
                eventObj.UIDSignature);
        $("#login_form").submit();
    }
    gigya.socialize.addEventHandlers({
        onLogin: GetUID
    });
    gigya.socialize.showLoginUI(login_params);

    $(window).load(function () {
        $('.gigya-login-footer').css('display', 'none');
    });
</script>

<script type="text/javascript">
    $("#submit_custom_uid").click(function() {
        $.ajax({
            type: 'POST',
            url : '<c:url value="/login/test" />',
            data: "uid=" + $("#custom_uid").val()
        });
    });


</script>

<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
</body>
</html>
