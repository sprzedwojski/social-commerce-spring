<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html xmlns:th="http://www.thymeleaf.org">
<head th:fragment="head">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Web Recommender Store</title>
	
	<style>
	.gigya-login-footer{
		display: none !important;
	}

	img.center {
		display: block;
		margin-left: auto;
		margin-right: auto;
	}
	</style>	
	
	<!-- HEROKU -->
	<!-- 
	src="http://cdn.gigya.com/js/gigya.js?apiKey=3_sFgwvnq40GfyHqzMpOxL0E3fCpHbGODZRhsOycfgim6rg85ZBcsewcid3mIcu0ne"
	 -->
	
	<!-- LOCALHOST -->
	<!-- 
	 -->
<!-- gigya.js script should only be included once -->
<script type="text/javascript" src="http://cdn.gigya.com/js/gigya.js?apiKey=3_MLqMUEBrPfB1wNvboMY4ygUcAaszwPMGhockNgbfKXZQMT3mklNOuhmoHnA4-X6i">
{
	siteName: 'localhost.com'
	,enabledProviders: 'facebook'
}
</script>
<script type="text/javascript">
var login_params=
{
	version: 2
	,showTermsLink: 'false'
	,height: 92
	,width: 150
	,containerID: 'componentDiv'
	,UIConfig: '<config><body><controls><snbuttons buttonsize="65" /></controls></body></config>'
	,autoDetectUserProviders: ''
	,facepilePosition: 'none'
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

		<img src="<c:url value="/resources/logo_reco4social.jpg" />" class="center"/>

		<div id="loginbox" style="margin-top: 50px;"
			class="mainbox col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2">
			<div class="panel panel-info" >
	            <div class="panel-heading">
	                <div class="panel-title text-center">Sign in with your favourite social network</div>
	            </div> 			


 			<div style="padding-top:30px" class="panel-body" >
				<div id="componentDiv" style="margin:auto"></div>
	
				<!-- 
			<button id="get-user-info" onclick="getUInfo()">getUserInfo</button>
		 -->
	

	
				<form id="login_form" action="#" th:action="@{/}" th:object="${user}" method="post">
					<input type="hidden" name="UID" id="UID" th:field="*{UID}" /> <input
						type="hidden" name="signatureTimestamp" id="signatureTimestamp"
						th:field="*{signatureTimestamp}" /> <input type="hidden"
						name="UIDSignature" id="UIDSignature" th:field="*{UIDSignature}" />
					<!-- <input type="hidden" name="userObject" id="userObject"> -->
					<!-- <input type="submit" class="btn btn-default" value="submit" style="display:none;"> -->
				</form>
			</div>
		</div>
		</div>
		
	</div>
	
	
	<%--<form id="get_user_data_form" action="/socialcommerce/getUserData" th:action="@{/}" th:object="${user}" method="post">--%>
		<%--<input type="text" name="UID" id="UID" th:field="*{UID}" th:object="${user}" /> --%>
		<%--<input type="submit" value="getUserData" />--%>
	<%--</form>--%>
			<%----%>

	<%--<p th:text="'id: ' + ${user.UID}"></p>--%>

    <footer class="footer">
      <div class="container">
        <p class="text-muted">Created by <a href="mailto:szymon.przedwojski@gmail.com" target="_top">Szymon Przedwojski</a> at <a href="http://amg.net.pl" target="_blank">AMG.net</a></p>
      </div>
    </footer>

	<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
	<script type="text/javascript">
		function GetUID(eventObj) {
			//alert("congrats on your " + eventObj.eventName + " | UID: " + eventObj.UID);
			document.getElementById("UID").setAttribute("value", eventObj.UID);
			document.getElementById("signatureTimestamp").setAttribute("value",
					eventObj.signatureTimestamp);
			document.getElementById("UIDSignature").setAttribute("value",
					eventObj.UIDSignature);
			$("#login_form").submit();
			//document.getElementById("login_submit_btn").click();
			//document.getElementById("userObject").setAttribute("value",
				//	JSON.stringify(eventObj));
			//$("#login_form").submit();
			//$("#login_submit_btn").click();
		}
		gigya.socialize.addEventHandlers({
			onLogin : GetUID
		});
		gigya.socialize.showLoginUI(login_params);
		
		function onLogin(response) {
			document.getElementById("login_submit_btn").click();
		}

//		function printResponse(response) {
//			if (response.errorCode == 0) {
//				console.log(response);
//				var user = response['user'];
//				var msg = 'User ' + user['nickname'] + ' is ' + user['age']
//						+ ' years old';
//				//alert(msg);
//			} else {
//				alert('Error :' + response.errorMessage);
//			}
//		}

//		function getUInfo() {
//			gigya.socialize.getUserInfo({
//				callback : printResponse
//			}, {
//				extraFields : 'likes'
//			});
//		}
		
//		$(document).ready(function() {
//			$('.gigya-login-footer').css('display', 'none');
//			console.log("document ready");
//		});
		$(window).load(function() {
			$('.gigya-login-footer').css('display', 'none');
			console.log("window load");
		});		
	</script>

	<!-- Latest compiled and minified JavaScript -->
	<script
		src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
</body>
</html>
