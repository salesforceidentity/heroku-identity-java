package com.salesforce.saml;

import com.salesforce.util.Bag;
import com.salesforce.util.XSDDateTime;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class SAMLServlet extends HttpServlet{

    private static final String SAML_REQUEST = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><samlp:AuthnRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" AssertionConsumerServiceURL=\"{0}\" Destination=\"{1}\" ID=\"_{2}\" IssueInstant=\"{3}\" ProtocolBinding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" Version=\"2.0\"><saml:Issuer xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">{4}</saml:Issuer></samlp:AuthnRequest>";
    private Boolean INITIALIZED = false;
    private String ISSUER = null;
    private String IDP_URL = null;
    private PublicKey IDP_PUBLIC_KEY = null;

    @Override
    public void init() throws ServletException {
        String samlMetadata = System.getenv("SAML_METADATA");



        if (samlMetadata != null) {

            Document metadataDocument = null;

            try {

                String response = null;

                if (samlMetadata.toLowerCase().startsWith("https")) {

                    HttpClient client = new HttpClient();

                    // Create a method instance.
                    GetMethod method = new GetMethod(samlMetadata);

                    // Provide custom retry handler is necessary
                    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

                    // Execute the method.
                    int statusCode = client.executeMethod(method);

                    if (statusCode != HttpStatus.SC_OK) {
                        System.err.println("Method failed: " + method.getStatusLine());
                    }

                    // Read the response body.
                    byte[] responseBody = method.getResponseBody();

                    // Deal with the response.
                    response = new String(responseBody, "UTF-8");

                } else {

                    response = new String(Base64.decodeBase64(samlMetadata.getBytes("UTF-8")),"UTF-8");

                }

                DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                domFactory.setNamespaceAware(true);
                DocumentBuilder builder = null;
                builder = domFactory.newDocumentBuilder();
                metadataDocument = builder.parse(new InputSource(new ByteArrayInputStream(response.getBytes("UTF-8"))));

            } catch (Exception e) {
                throw new ServletException("Error decoding SAML Metadata", e);
            }

            //Setup XPath
            NamespaceContext namespaceContext = new SAMLNamespaceResolver();
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            xpath.setNamespaceContext(namespaceContext);

            try {

                XPathExpression edXPath = xpath.compile("/md:EntityDescriptor");
                NodeList edXPathResult = (NodeList) edXPath.evaluate(metadataDocument, XPathConstants.NODESET);
                if (edXPathResult.getLength() != 1) throw new ServletException("No EntityDescriptor in SAML_METADATA");
                Node edNode = edXPathResult.item(0);
                ISSUER = edNode.getAttributes().getNamedItem("entityID").getTextContent();
                if (ISSUER == null)throw new ServletException("No entityID on Entity Descriptor in SAML_METADATA");

                XPathExpression certXPath = xpath.compile("/md:EntityDescriptor/md:IDPSSODescriptor/md:KeyDescriptor/ds:KeyInfo/ds:X509Data/ds:X509Certificate");
                NodeList certXPathResult = (NodeList) certXPath.evaluate(metadataDocument, XPathConstants.NODESET);
                if (certXPathResult.getLength() != 1) throw new ServletException("No X509Certificate node");
                Node certNode = certXPathResult.item(0);
                StringBuffer encodedCert = new StringBuffer("-----BEGIN CERTIFICATE-----\n");
                encodedCert.append(certNode.getTextContent());
                encodedCert.append("\n-----END CERTIFICATE-----\n");
                String cert = encodedCert.toString();
                if (cert == null)throw new ServletException("No cert in cert node");
                //System.out.println(cert);

                try {
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    Certificate certificate = cf.generateCertificate(new ByteArrayInputStream(cert.getBytes("UTF-8")));
                    IDP_PUBLIC_KEY = certificate.getPublicKey();
                } catch (CertificateException e) {
                    throw new ServletException("Error getting PublicKey from Cert", e);
                } catch (UnsupportedEncodingException e) {
                    throw new ServletException("Error getting PublicKey from Cert", e);
                }

                XPathExpression ssoXPath = xpath.compile("//md:SingleSignOnService[@Binding='urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect']");
                NodeList ssoXPathResult = (NodeList) ssoXPath.evaluate(metadataDocument, XPathConstants.NODESET);
                if (ssoXPathResult.getLength() != 1) throw new ServletException("No SingleSignOnService with Redirect Binding");
                Node ssoNode = ssoXPathResult.item(0);
                IDP_URL = ssoNode.getAttributes().getNamedItem("Location").getTextContent();
                if (IDP_URL == null)throw new ServletException("No Location for SingleSignOnService with Redirect Binding");


            } catch (XPathExpressionException e) {
                throw new ServletException("Error Executing XPaths on Metadata", e);
            }

            System.out.println("Initialized SAML with:");
            System.out.println("ISSUER:" + ISSUER);
            System.out.println("IDP_URL:" + IDP_URL);
            System.out.println("IDP_PUBLIC_KEY:" + IDP_PUBLIC_KEY);
            INITIALIZED = true;

        } else {
            System.out.println("SAML isn't yet initialized!");
        }


    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String logout = request.getParameter("logout");
        if (logout != null) {
            Cookie cookie = new Cookie("IDENTITY", "");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            response.sendRedirect("/");
            return;
        }


        String url = request.getRequestURL().toString();
        //herokuism
        url = url.replaceFirst("http", "https");
        String app = null;
        try {
            app = new URI(url).getHost().split("\\.")[0];
        } catch (URISyntaxException e) {
            throw new ServletException(e);
        }
        if (!INITIALIZED)  {
            //DO some error handling
            request.setAttribute("URL", url);
            request.setAttribute("app", app);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/configure.jsp");
            dispatcher.forward(request, response);
            return;

        } else {

            //Pretty simply way to build a SAML Request.  Beats building a DOM...
            String[] args = new String[5];
            args[0] = url;
            args[1] = IDP_URL;
            args[2] = UUID.randomUUID().toString();
            args[3] = new XSDDateTime().getDateTime();
            args[4] = url;
            MessageFormat html;
            html = new MessageFormat(SAML_REQUEST);
            String requestXml = html.format(args);
            byte[] input = requestXml.getBytes("UTF-8");

            //Deflate that XML
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Deflater d = new Deflater(Deflater.DEFLATED, true);
            DeflaterOutputStream dout = new DeflaterOutputStream(baos, d);
            dout.write(input);
            dout.close();

            //B64 encode it
            String encodedRequest = Base64.encodeBase64String(baos.toByteArray());

            //URLEncode it
            String SAMLRequest = URLEncoder.encode(encodedRequest, "UTF-8");

            //Redirect that browser
            String relayState = request.getParameter("RelayState");
            String redirect = IDP_URL + "?SAMLRequest=" + SAMLRequest;
            if (relayState != null) redirect += "&RelayState=" + relayState;
            response.sendRedirect(redirect);

        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String url = request.getRequestURL().toString();
        //herokuism
        url = url.replaceFirst("http", "https");

        //Get the SAMLResponse and RelayState
        String encodedResponse = request.getParameter("SAMLResponse");
        String relayState = request.getParameter("RelayState");
        if ((relayState == null) || ( relayState.equals(""))) relayState = "/";

        //validate the response
        SAMLValidator sv = new SAMLValidator();
        try {
            Identity identity = sv.validate(encodedResponse, IDP_PUBLIC_KEY, null, ISSUER, url, url);
            //DO something with the Identity
            JSONObject identityJSON = new JSONObject();
            identityJSON.put("subject",identity.getSubject());
            Bag attributes = identity.getAttributes();
            Set keySet = attributes.keySet();
            Iterator iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                identityJSON.put(key, (ArrayList<String>) attributes.getValues(key));
            }
            Cookie identityCookie = new Cookie("IDENTITY", Base64.encodeBase64URLSafeString(identityJSON.toString().getBytes("UTF-8")));
            response.addCookie(identityCookie);
        } catch (Exception e) {
            response.sendError(401, "Access Denied: " + e.getMessage());
            return;
        }
        response.sendRedirect(relayState);



    }

}
