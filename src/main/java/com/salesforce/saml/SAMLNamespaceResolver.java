package com.salesforce.saml;

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

public class SAMLNamespaceResolver implements NamespaceContext {

    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("No prefix provided!");
        } else if (prefix.equals("samlp")) {
            return "urn:oasis:names:tc:SAML:2.0:protocol";
        } else if (prefix.equals("saml")) {
            return "urn:oasis:names:tc:SAML:2.0:assertion";
        } else if (prefix.equals("ds")) {
            return "http://www.w3.org/2000/09/xmldsig#";
        } else if (prefix.equals("md")) {
            return "urn:oasis:names:tc:SAML:2.0:metadata";
        } else return null;

    }

    public String getPrefix(String namespaceURI) {
        return null;
    }

    public Iterator getPrefixes(String namespaceURI) {
        return null;
    }

}
