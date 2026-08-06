<!DOCTYPE html>
<%@page import="java.util.Enumeration"%>
<html lang="en">
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<link rel="stylesheet" type="text/css" href="static/css/bootstrap.min.css">
		<script type="text/javascript" src="static/js/jquery.js"></script>
		<script type="text/javascript" src="static/js/bootstrap.min.js"></script>
		<title>TrustedX eIDAS Platform - Integration examples</title>
	</head>
	
<%
Enumeration<String> enumeration = request.getAttributeNames();
while (enumeration.hasMoreElements()) {
	String key = enumeration.nextElement();
	System.out.println(key + "=>" + request.getAttribute(key));	
}

Enumeration<String> enumerationS = session.getAttributeNames();
while (enumerationS.hasMoreElements()) {
	String key = enumerationS.nextElement();
	System.out.println(key + "=>" + session.getAttribute(key));	
}

%>	
	<body>
	<div class="container">
	<div class="row">
		<div class="col-md-3">
		<h4> Menu</h4>
			<ul class="nav nav-pills nav-stacked">
		        <li class="active"><a href="/fileTransfer/home">Home</a></li>
			</ul>
		</div>
		<div class="col-md-9">
				<h3>Ha ocurrido algun error:</h3>
				<table class="table col-md-12">
<!-- 					<thead> -->
<!-- 					<tr> -->
<!-- 						<th class="field-label col-md-3"> -->
<!-- 							Parameter  -->
<!-- 						</th> -->
<!-- 						<th class="field-label col-md-4"> -->
<!-- 							Value -->
<!-- 						</th> -->
<!-- 						<th class="field-label col-md-5"> -->
<!-- 							Additional info -->
<!-- 						</th> -->
<!-- 					</tr> -->
<!-- 					</thead> -->
					<tbody>
					<tr>
						<th class="col-md-3">
							Error: 
						</th>
						<th class="col-md-4">
							${error}
						</th>
						<th class="col-md-5">
							${error}
						</th>
					</tr>
					</tbody>
				</table>
		</div>
	</div>
	</div>
	</body>
</html>