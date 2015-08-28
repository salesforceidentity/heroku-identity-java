<%
	String url = (String)request.getAttribute("URL");
	String app = (String)request.getAttribute("app");
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
<p>Navigate to <b>Setup | Create | Apps</b></p>
<p>Scroll down to <b>Connected Apps</b> and click <b>New</b></p>
<p>Give your app a name, enter your email address and, optionally, set a logo, icon and description</p>
<p>Scroll down to Web App Settings and check the <b>Enable SAML</b> checkbox</p>
<p>Use the following <b>Start Url</b>: <%= url %> </p>
<p>Use the following <b>Entity Id</b>: <%= url %> </p>
<p>Use the following <b>ACS URL</b>: <%= url %> </p>
<p>...and don't forget to authorize your Profile or Permission Set</p>



<h2><li>Now, configure SAML via a Metadata URL:</li></h2>
<p>Click 'Manage' from the Connected App detail page and copy your Metadata URL</p>
<p>Run this command using toolbelt:</p>
<pre>heroku config:set --app <%= app %> SAML_METADATA=&lt;your metadata url&gt;</pre>
<p><a href="javascript:window.location.reload(true)">Refresh this page</a></p>


<h2><li>Or...configured SAML via a Metadata File</li></h2>
<p>Click 'Manage' from the Connected App detail page and download your Connected App's SAML Metadata</p>
<p>Base64 Encode the Metadata</p>
<p>Run this command using toolbelt:</p>
<pre>heroku config:set --app <%= app %> SAML_METADATA=&lt;your base64 encoded metadata&gt;</pre>
<p><a href="javascript:window.location.reload(true)">Refresh this page</a></p>
</body>
</html>