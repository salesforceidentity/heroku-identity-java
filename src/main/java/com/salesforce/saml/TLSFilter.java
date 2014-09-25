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

        if(request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            if(!request.isSecure()) {
                HttpServletRequest httpReq = (HttpServletRequest) request;
                String redirectTarget = httpReq.getRequestURL().toString();
                redirectTarget = redirectTarget.replaceFirst("http", "https");
                ((HttpServletResponse)response).sendRedirect(redirectTarget);
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    @Override
    public void destroy() {

    }
}