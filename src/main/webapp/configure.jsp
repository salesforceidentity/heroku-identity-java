<%
	String url = (String)request.getAttribute("URL");
%>

<html>
<head>
<link href="/css/style.css" rel="stylesheet" type="text/css">
</head>
<body>

<h1>Whoops!</h1>

It doesn't look like you've configured SAML quite yet....please follow these steps:

<ol>

<h2><li>Go to your Salesforce org, and create a Connected App:</li></h2>
<p>Check the <b>Enable SAML</b> checkbox</p>
<p>Use the following <b>Start Url</b>: <%= url %> </p>
<p>Use the following <b>Entity Id</b>: <%= url %> </p>
<p>Use the following <b>ACS URL</b>: <%= url %> </p>
<p>...and don't forget to authorize your Profile or Permission Set</p>


<h2><li>Set the following Heroku Config Variables:</li></h2>
<p>Download your Connected App's SAML Metadata</p>
<p>Base64 Encode the Metadata</p>
<p>Run this command using toolbelt:</p>
<pre>heroku config:set SAML_METADATA=<your base64 encoded metadata></pre>
</body>
</html>