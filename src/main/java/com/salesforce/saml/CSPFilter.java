package com.salesforce.saml;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class CSPFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response,  FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        final String csp = System.getenv("CSPHeader");
        if (null != csp && csp.length() > 0) {
            httpResponse.addHeader("Content-Security-Policy", MessageFormat.format("frame-src {0};", csp));
        }
        if (chain != null) chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
