package org.jbpm.task.servlet;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class JUnitHumanTaskServiceServlet extends HumanTaskServiceServlet {

	private Properties parameters = null;
	
	public JUnitHumanTaskServiceServlet(Properties parameters) {
		this.parameters = parameters;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getInitParameter(String name) {
		
		return this.parameters.getProperty(name);
	}

	@Override
	public ServletConfig getServletConfig() {
		
		return new ServletConfig() {
			
			public String getServletName() {
				// TODO Auto-generated method stub
				return "test";
			}
			
			public ServletContext getServletContext() {
				// TODO Auto-generated method stub
				return null;
			}
			
			public Enumeration getInitParameterNames() {
				// TODO Auto-generated method stub
				return null;
			}
			
			public String getInitParameter(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	@Override
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return new ServletContext() {
			
			public void setAttribute(String arg0, Object arg1) {
				// TODO Auto-generated method stub
				
			}
			
			public void removeAttribute(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void log(String arg0, Throwable arg1) {
				// TODO Auto-generated method stub
				
			}
			
			public void log(Exception arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
			
			public void log(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public Enumeration getServlets() {
				// TODO Auto-generated method stub
				return null;
			}
			
			public Enumeration getServletNames() {
				// TODO Auto-generated method stub
				return null;
			}
			
			public String getServletContextName() {
				// TODO Auto-generated method stub
				return null;
			}
			
			public Servlet getServlet(String arg0) throws ServletException {
				// TODO Auto-generated method stub
				return null;
			}
			
			public String getServerInfo() {
				// TODO Auto-generated method stub
				return null;
			}
			
			public Set getResourcePaths(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			public InputStream getResourceAsStream(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			public URL getResource(String arg0) throws MalformedURLException {
				// TODO Auto-generated method stub
				return null;
			}
			
			public RequestDispatcher getRequestDispatcher(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			public String getRealPath(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			public RequestDispatcher getNamedDispatcher(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			public int getMinorVersion() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			public String getMimeType(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			public int getMajorVersion() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			public Enumeration getInitParameterNames() {
				// TODO Auto-generated method stub
				return null;
			}
			
			public String getInitParameter(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			public String getContextPath() {
				// TODO Auto-generated method stub
				return null;
			}
			
			public ServletContext getContext(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			public Enumeration getAttributeNames() {
				// TODO Auto-generated method stub
				return null;
			}
			
			public Object getAttribute(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
	
	

}
