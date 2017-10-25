package com.nymph.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class EncodingFilter implements Filter {
	
	private String defaultEncoding;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		defaultEncoding = filterConfig.getInitParameter("encoding");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		request.setCharacterEncoding(defaultEncoding);
		response.setCharacterEncoding(defaultEncoding);
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

}
