<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>Thing Broker Web</title>
</head>
<body>
	<h1>Welcome to Thing Broker</h1><br/><br/>
	<h1>Event Posting</h1>
	<form action="/thingbroker/events/event/thing/123?keep-stored=true"
		method="post" enctype="multipart/form-data">
		<input type="file" name="file" /><br /><br /> 
		<input type="file" name="file2" /><br /><br />
		<input type="submit" />
	</form>
</body>
</html>