<%@ page import="com.salesforce.saml.Identity,com.salesforce.util.Bag,java.util.Set,java.util.Iterator,java.util.ArrayList" %>
<%
Identity identity = null;
Cookie[] cookies = request.getCookies();
if (cookies != null) {
 for (Cookie cookie : cookies) {
   if (cookie.getName().equals("IDENTITY")) {
     identity = new Identity(cookie.getValue(),true);
    }
  }
}

%>

<html>
<head>
<link href="/css/style.css" rel="stylesheet" type="text/css">
</head>

<body>


<% if (identity != null ) { %>
<center>
<h2><%= identity.getSubject() %></h2>
<table border="0" cellpadding="5">
<%
	Bag attributes = identity.getAttributes();
	Set keySet = attributes.keySet();
	Iterator iterator = keySet.iterator();
	while (iterator.hasNext()){
		String key = (String)iterator.next();
		%><tr><td><b><%= key %>:</b></td><td><%
		ArrayList<String> values = (ArrayList<String>)attributes.getValues(key);
		for (String value : values) {
			%><%= value %><br/><%
		}
		%></td></tr><%

	}

%>
</table>
<br>
<a href="/_saml?logout=true" class="button center">Logout</a>
</center>
<% } else {  %>
 <div class="centered">
 <img src="<%@ page import="com.salesforce.saml.Identity,com.salesforce.util.Bag,java.util.Set,java.util.Iterator,java.util.ArrayList" %>
<%
Identity identity = null;
Cookie[] cookies = request.getCookies();
if (cookies != null) {
 for (Cookie cookie : cookies) {
   if (cookie.getName().equals("IDENTITY")) {
     identity = new Identity(cookie.getValue(),true);
    }
  }
}

%>

<html>
<head>
<link href="/css/style.css" rel="stylesheet" type="text/css">
</head>

<body>


<% if (identity != null ) { %>
<center>
<h2><%= identity.getSubject() %></h2>
<table border="0" cellpadding="5">
<%
	Bag attributes = identity.getAttributes();
	Set keySet = attributes.keySet();
	Iterator iterator = keySet.iterator();
	while (iterator.hasNext()){
		String key = (String)iterator.next();
		%><tr><td><b><%= key %>:</b></td><td><%
		ArrayList<String> values = (ArrayList<String>)attributes.getValues(key);
		for (String value : values) {
			%><%= value %><br/><%
		}
		%></td></tr><%

	}

%>
</table>
<br>
<a href="/_saml?logout=true" class="button center">Logout</a>
</center>
<% } else {  %>
 <div class="centered">
 Hi
 <br/>
 <img src="http://www.salesforce-online-training.com/wp-content/uploads/2013/11/sales-force-online-favicon.jpg" alt="shah-the-trainer" width=""200px height="200px" />
 <br/>
 <span class=""><a href="/_saml?RelayState=%2F" class="button center">Login</a></span>
 </div>

<% } %>


</body>
</html>"
 <span class=""><a href="/_saml?RelayState=%2F" class="button center">Login</a></span>
 </div>

<% } %>


</body>
</html>
