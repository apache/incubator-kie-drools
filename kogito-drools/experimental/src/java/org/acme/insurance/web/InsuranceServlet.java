package org.acme.insurance.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class for Servlet: InsuranceServlet
 * 
 */
public class InsuranceServlet extends javax.servlet.http.HttpServlet implements
		javax.servlet.Servlet {
	static final long serialVersionUID = 1L;

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public InsuranceServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		DroolsBusiness business = new DroolsBusiness();
		try {
			business.execute(request);

			String redirectURL = "index.jsp";
			redirectURL += "?calculo=sim";
			redirectURL += "&aprovado=" + business.isApproved();
			redirectURL += "&fatorrisco=" + business.getRiskFactor();
			redirectURL += "&base=" + business.getBasePrice();
			redirectURL += "&valor=" + business.getInsurancePrice();
			
			response.sendRedirect(redirectURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
}