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
<title>Social Login</title>

<!-- 3_OnY0lnwCgprNVr6pfd_PZxKhMTPK9QuihzWPV0IOyvyYoJR8wEhYW5qmDEbtr3Vu -->
<!-- gigya.js script should only be included once -->
<script type="text/javascript"
	src="http://cdn.gigya.com/js/gigya.js?apiKey=3_MLqMUEBrPfB1wNvboMY4ygUcAaszwPMGhockNgbfKXZQMT3mklNOuhmoHnA4-X6i">
{
	siteName: 'localhost'//'szymon-przedwojski.cba.pl'
	enabledProviders: 'facebook,twitter,googleplus,linkedin,yahoo,microsoft,aol,foursquare,instagram,vkontakte,renren,QQ,Sina,kaixin'
}
</script>

</head>
<body>
	<h1>Social Login</h1>

	<script type="text/javascript">
		var login_params=
		{
			version: 2,
			showTermsLink: 'false',
			height: 100,
			width: 330,
			containerID: 'componentDiv',
			buttonsStyle: 'fullLogoColored',
			autoDetectUserProviders: '',
			facepilePosition: 'none',
		}
	</script>

	<div id="componentDiv"></div>

	<button id="get-user-info" onclick="getUInfo()">getUserInfo</button>

	<form action="#" th:action="@{/}" th:object="${user}" method="post">
		<input type="hidden" name="UID" id="UID" th:field="*{UID}"/>
		<input type="hidden" name="signatureTimestamp" id="signatureTimestamp" th:field="*{signatureTimestamp}"/>
		<input type="hidden" name="UIDSignature" id="UIDSignature" th:field="*{UIDSignature}"/>
		<!-- <input type="hidden" name="userObject" id="userObject"> -->
		<input type="submit" value="submit">
	</form>
	
	<p th:text="'id: ' + ${user.UID}" ></p>	

	<script type="text/javascript">
		function GetUID(eventObj) {
			//alert("congrats on your " + eventObj.eventName + " | UID: " + eventObj.UID);
			document.getElementById("UID").setAttribute("value",  eventObj.UID);
			document.getElementById("signatureTimestamp").setAttribute("value", eventObj.signatureTimestamp);
			document.getElementById("UIDSignature").setAttribute("value", eventObj.UIDSignature);	
			document.getElementById("userObject").setAttribute("value", JSON.stringify(eventObj));
		}
		gigya.socialize.addEventHandlers({
		        onLogin:GetUID
		     }
		);
	   	gigya.socialize.showLoginUI(login_params);
	   	
	   	function printResponse(response) {  
	   	    if ( response.errorCode == 0 ) {   
	   	    	console.log(response);
	   	        var user = response['user'];
	   	        var msg = 'User '+user['nickname'] + ' is ' +user['age'] + ' years old';
	   	        //alert(msg);
	   	    }
	   	    else {
	   	        alert('Error :' + response.errorMessage);
	   	    }
	   	}
		
	   	function getUInfo() {
	   		gigya.socialize.getUserInfo({callback:printResponse}, {extraFields:'likes'});
	   	}
	</script>
</body>
</html>
