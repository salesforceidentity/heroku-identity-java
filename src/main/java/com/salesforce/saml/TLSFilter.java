package com.salesforce.saml;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class TLSFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response,  FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String scheme = httpRequest.getHeader("x-forwarded-proto");

        if(!scheme.equals("https")) {
            StringBuilder tlsURL = new StringBuilder("https://");
            tlsURL.append(request.getServerName());
            if (httpRequest.getRequestURI() != null) {
                tlsURL.append(httpRequest.getRequestURI());
            }
            if (httpRequest.getQueryString() != null) {
                tlsURL.append("?").append(httpRequest.getQueryString());
            }

            httpResponse.sendRedirect(tlsURL.toString());

        } else {
            if (chain != null) chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}