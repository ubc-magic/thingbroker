<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>Thing Broker Web</title>
</head>
<body>
	<img alt="Thing Broker Logo" src="<c:url value='/resources/images/logo_large.png'/>">
	<h1>Event Posting</h1>
	<form action="<c:url value='/things/123/events?keep-stored=true'/>"
		method="post" enctype="multipart/form-data">
		<input type="file" name="file" /><br /><br /> 
		<input type="file" name="file2" /><br /><br />
		<input type="submit" />
	</form>
	<a href="resources/jquery-plugin">ThingBroker jQuery Plugin</a>
</body>
</html>