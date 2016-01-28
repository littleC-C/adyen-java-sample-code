<%@page import="java.util.*" %>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>Adyen - Create Payment On Hosted Payment Page (HPP)</title>
	</head>
	<body>
		<form method="POST" action="${hppUrl}" target="_blank">
		
		
		
		<%
			for (Enumeration<String> enumeration = request.getAttributeNames(); enumeration.hasMoreElements();) {
			    String attributeName = enumeration.nextElement();
			    Object attribute = request.getAttribute(attributeName);    
	  
					    if(attributeName.equals("javax.servlet.forward.request_uri") 
					    	|| attributeName.equals("javax.servlet.forward.context_path") 
					    	|| attributeName.equals("javax.servlet.forward.servlet_path")
					    	|| attributeName.equals("hppUrl")){
	    				}else{
	    %>
	    					<input type="hidden" name="<%=attributeName%>" value="<%=attribute.toString()%>">
	    <% 	
	    				} 
			}
		%>
							<input type="submit" value="Create payment">
		</form>
	</body>
</html>
