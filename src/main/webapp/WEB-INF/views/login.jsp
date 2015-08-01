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
    <%--<script type="text/javascript"
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
            , facepilePosition: 'none',
            extraFields: 'religion,politicalView,likes,relationshipStatus,hometown'
        }
        /*languages,education,work,favorites,educationLevel,*/
    </script>--%>

    <script>
        // This is called with the results from from FB.getLoginStatus().
        function statusChangeCallback(response) {
            console.log('statusChangeCallback');
            console.log(response);
            // The response object is returned with a status field that lets the
            // app know the current login status of the person.
            // Full docs on the response object can be found in the documentation
            // for FB.getLoginStatus().
            if (response.status === 'connected') {
                // Logged into your app and Facebook.
                /*testAPI();*/
                submitPage(response);
            } else if (response.status === 'not_authorized') {
                // The person is logged into Facebook, but not your app.
                document.getElementById('status').innerHTML = 'Please log ' +
                        'into this app.';
            } else {
                // The person is not logged into Facebook, so we're not sure if
                // they are logged into this app or not.
                document.getElementById('status').innerHTML = 'Please log ' +
                        'into Facebook.';
            }
        }

        // This function is called when someone finishes with the Login
        // Button.  See the onlogin handler attached to it in the sample
        // code below.
        function checkLoginState() {
            FB.getLoginStatus(function(response) {
                statusChangeCallback(response);
            });
        }

        window.fbAsyncInit = function() {
            FB.init({
                /* PROD */
                /*appId      : '846404535443906',*/

                /* TEST */
                appId      : ${fbAppId},/*'891425247608501',*/

                cookie     : true,  // enable cookies to allow the server to access
                                    // the session
                xfbml      : true,  // parse social plugins on this page
                version    : 'v2.4' // use version 2.4
            });

            // Now that we've initialized the JavaScript SDK, we call
            // FB.getLoginStatus().  This function gets the state of the
            // person visiting this page and can return one of three states to
            // the callback you provide.  They can be:
            //
            // 1. Logged into your app ('connected')
            // 2. Logged into Facebook, but not your app ('not_authorized')
            // 3. Not logged into Facebook and can't tell if they are logged into
            //    your app or not.
            //
            // These three cases are handled in the callback function.

            /* Zmienione zeby dzialal logout */
            /*FB.getLoginStatus(function(response) {
                statusChangeCallback(response);
            });*/

        };

        // Load the SDK asynchronously
        (function(d, s, id) {
            var js, fjs = d.getElementsByTagName(s)[0];
            if (d.getElementById(id)) return;
            js = d.createElement(s); js.id = id;
            js.src = "//connect.facebook.net/en_US/sdk.js";
            fjs.parentNode.insertBefore(js, fjs);
        }(document, 'script', 'facebook-jssdk'));

        // Here we run a very simple test of the Graph API after login is
        // successful.  See statusChangeCallback() for when this call is made.
        /*function testAPI() {
            console.log('Welcome!  Fetching your information.... ');
            FB.api('/me', function(response) {
                console.log('Successful login for: ' + response.name);
                document.getElementById('status').innerHTML =
                        'Thanks for logging in, ' + response.name + '!';
            });
        }*/

        function submitPage(response) {
            console.log(response.authResponse.accessToken);

            document.getElementById("accessToken").setAttribute("value", response.authResponse.accessToken);
            $("#login_form").submit();
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
                <%--<div id="componentDiv" style="margin:auto"></div>--%>

                <!--
                Below we include the Login Button social plugin. This button uses
                the JavaScript SDK to present a graphical Login button that triggers
                the FB.login() function when clicked.
                -->

                <center>
                    <fb:login-button
                            scope="public_profile,user_relationship_details,user_hometown,user_likes,user_religion_politics,user_location"
                            onlogin="checkLoginState();"
                            size="xlarge">
                    </fb:login-button>

                    <div id="status"></div>
                </center>

                <form id="login_form" action="#" th:action="@{/}" th:object="${user}" method="post">
                    <%--<input type="hidden" name="UID" id="UID" th:field="*{UID}"/>
                    <input type="hidden" name="signatureTimestamp" id="signatureTimestamp"
                           th:field="*{signatureTimestamp}"/>
                    <input type="hidden" name="UIDSignature" id="UIDSignature" th:field="*{UIDSignature}"/>--%>
                    <input type="hidden" name="accessToken" id="accessToken" th:field="*{accessToken}"/>
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
<%--<script type="text/javascript">
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
</script>--%>

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
