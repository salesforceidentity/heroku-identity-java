package com.salesforce.saml;

public class InvalidSignatureException extends Exception{
    public InvalidSignatureException() {
        super();
    }

    public InvalidSignatureException(String s) {
        super(s);
    }

    public InvalidSignatureException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public InvalidSignatureException(Throwable throwable) {
        super(throwable);
    }
}
