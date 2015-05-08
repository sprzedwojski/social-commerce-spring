<%--
 <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
 --%>
<!-- <html>
<head>
	<title>Home</title>
</head>
<body>
<h1>
	Hello world!  
</h1>

<P>  The time on the server is ${serverTime}. </P>
</body>
</html>-->


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns:th="http://www.thymeleaf.org">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Web Recommender Store</title>
	
	<!-- 3_OnY0lnwCgprNVr6pfd_PZxKhMTPK9QuihzWPV0IOyvyYoJR8wEhYW5qmDEbtr3Vu -->
	<!-- gigya.js script should only be included once -->
	<script type="text/javascript"
		src="http://cdn.gigya.com/js/gigya.js?apiKey=3_MLqMUEBrPfB1wNvboMY4ygUcAaszwPMGhockNgbfKXZQMT3mklNOuhmoHnA4-X6i">
		{
			siteName: 'localhost'//'szymon-przedwojski.cba.pl'
			enabledProviders: 'facebook,twitter,googleplus,linkedin,yahoo,microsoft,aol,foursquare,instagram,vkontakte,renren,QQ,Sina,kaixin'
		}
	</script>
	<script type="text/javascript">
		var login_params = {
			version : 2,
			showTermsLink : 'false',
			height : 100,
			width : 330,
			containerID : 'componentDiv',
			buttonsStyle : 'fullLogoColored',
			autoDetectUserProviders : '',
			facepilePosition : 'none',
		}
	</script>

	<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet"
		href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">	
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
		<div class="page-header text-center">
		  <h1>Demo Web Recommender Store <br><small>Login, have fun and help make science!</small></h1>
		</div>		
		<div id="loginbox" style="margin-top: 50px;"
			class="mainbox col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2">
			<div class="panel panel-info" >
	            <div class="panel-heading">
	                <div class="panel-title text-center">Sign In with your favourite social network</div>
	            </div> 			


 			<div style="padding-top:30px" class="panel-body" >
				<div id="componentDiv" style="margin:auto"></div>
	
				<!-- 
		<button id="get-user-info" onclick="getUInfo()">getUserInfo</button>
		 -->
	
				<form action="#" th:action="@{/}" th:object="${user}" method="post">
					<input type="hidden" name="UID" id="UID" th:field="*{UID}" /> <input
						type="hidden" name="signatureTimestamp" id="signatureTimestamp"
						th:field="*{signatureTimestamp}" /> <input type="hidden"
						name="UIDSignature" id="UIDSignature" th:field="*{UIDSignature}" />
					<!-- <input type="hidden" name="userObject" id="userObject"> -->
					<input type="submit" class="btn btn-default" value="submit" style="display:none;">
				</form>
			</div>
		</div>
		</div>
		
	</div>

	<p th:text="'id: ' + ${user.UID}"></p>

    <footer class="footer">
      <div class="container">
        <p class="text-muted">Created by <a href="mailto:szymon.przedwojski@amg.net.pl" target="_top">Szymon Przedwojski</a> at <a href="http://amg.net.pl">AMG.net</a></p>
      </div>
    </footer>


	<script type="text/javascript">
		function GetUID(eventObj) {
			//alert("congrats on your " + eventObj.eventName + " | UID: " + eventObj.UID);
			document.getElementById("UID").setAttribute("value", eventObj.UID);
			document.getElementById("signatureTimestamp").setAttribute("value",
					eventObj.signatureTimestamp);
			document.getElementById("UIDSignature").setAttribute("value",
					eventObj.UIDSignature);
			document.getElementById("userObject").setAttribute("value",
					JSON.stringify(eventObj));
		}
		gigya.socialize.addEventHandlers({
			onLogin : GetUID
		});
		gigya.socialize.showLoginUI(login_params);

		function printResponse(response) {
			if (response.errorCode == 0) {
				console.log(response);
				var user = response['user'];
				var msg = 'User ' + user['nickname'] + ' is ' + user['age']
						+ ' years old';
				//alert(msg);
			} else {
				alert('Error :' + response.errorMessage);
			}
		}

		function getUInfo() {
			gigya.socialize.getUserInfo({
				callback : printResponse
			}, {
				extraFields : 'likes'
			});
		}
	</script>

	<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
	<!-- Latest compiled and minified JavaScript -->
	<script
		src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
</body>
</html>
