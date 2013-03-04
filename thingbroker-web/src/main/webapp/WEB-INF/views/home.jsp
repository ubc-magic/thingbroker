<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>Thing Broker Web</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- Le styles -->
<link href="<c:url value='/resources/bootstrap/css/bootstrap.css'/>" rel="stylesheet">
<style type="text/css">

  /* Sticky footer styles
  -------------------------------------------------- */

  html,
  body {
    height: 100%;
    /* The html and body elements cannot have any padding or margin. */
  }

  /* Wrapper for page content to push down footer */
  #wrap {
    min-height: 100%;
    height: auto !important;
    height: 100%;
    /* Negative indent footer by it's height */
    margin: 0 auto -60px;
  }

  /* Set the fixed height of the footer here */
  #push,
  #footer {
    height: 60px;
  }
  #footer {
    background-color: #f5f5f5;
  }

  /* Lastly, apply responsive CSS fixes as necessary */
  @media (max-width: 767px) {
    #footer {
      margin-left: -20px;
      margin-right: -20px;
      padding-left: 20px;
      padding-right: 20px;
    }
  }

  /* Custom page CSS
  -------------------------------------------------- */
  /* Not required for template or sticky footer method. */

  #wrap > .container {
    padding-top: 60px;
  }
  .container .credit {
    margin: 20px 0;
  }

  code {
    font-size: 80%;
  }

</style>
<link href="<c:url value='/resources/bootstrap/css/bootstrap-responsive.css'/>" rel="stylesheet">

<!-- Bootstrap -->
<link href="<c:url value='resources/bootstrap/css/bootstrap.min.css'/>" rel="stylesheet" media="screen">
<!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
      <script src="../assets/js/html5shiv.js"></script>
    <![endif]-->
</head>
<body>
 <div id="wrap">

	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container">
				<button type="button" class="btn btn-navbar" data-toggle="collapse"
					data-target=".nav-collapse">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="brand" href="#">Thing Broker</a>
				<div class="nav-collapse collapse">
					<ul class="nav">
						<li class="active"><a href="#">Home</a></li>
						<li><a href="https://github.com/ubc-magic/thingbroker/wiki">Documentation</a></li>
						<li><a href="resources/jquery-plugin">Demos</a></li>
					</ul>
				</div>
				<!--/.nav-collapse -->
			</div>
		</div>
	</div>

	<div class="container">
		<h1>Welcome to the Thing Broker</h1>
    	<div class="row">
  	    <div class="page-header">
		</div>    		<div class="span6">
				<img class="logo" alt="Thing Broker Logo" src="<c:url value='/resources/images/logo_large.png'/>">
			</div>
			<div class="span6">
				<h1>Test Event Posting</h1>
				<form action="<c:url value='/things/123/events?keep-stored=true'/>"
					method="post" enctype="multipart/form-data">
					<input type="file" name="file" /><br />
					<br /> <input type="file" name="file2" /><br />
					<br /> <input type="submit" />
				</form>
			</div>
		</div>
		<div class="row">
		</div>
	</div>
    </div>
	
	<div id="footer">
      <div class="container">
        <p class="muted credit">Created by the University of British Columbia <a href="http://www.magic.ubc.ca">MAGIC Lab</a> and <a href="http://www2.ufscar.br/home/index.php">UFSCar</a> - The Federal University of São Carlos.</p>
      </div>
</div>
<script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
<script src="<c:url value='resources/bootstrap/js/bootstrap.min.js'/>"></script>
</body>
</html>
