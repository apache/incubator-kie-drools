package org.acme.insurance.web;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.acme.insurance.base.AccessoriesCoverage;
import org.acme.insurance.base.Driver;
import org.acme.insurance.base.DriverAdditionalInfo;
import org.acme.insurance.base.Policy;
import org.acme.insurance.base.SupplementalInfo;
import org.drools.StatefulSession;

public class DroolsBusiness {

	private DriverAdditionalInfo driverAdit = new DriverAdditionalInfo();
	private SupplementalInfo suppinfo = new SupplementalInfo();
	private AccessoriesCoverage accessCov = new AccessoriesCoverage();
	private Driver driverMale = new Driver();
	private SimpleDateFormat df = new java.text.SimpleDateFormat("MM/dd/yyyy");
	private Date defaultBirthday;
	private Policy policy = new Policy();

	private StatefulSession session;

	public boolean isApproved() {
		return policy.isApproved();
	}

	public double getBasePrice() {
		return policy.getBasePrice();
	}

	public double getRiskFactor() {
		return driverMale.getInsuranceFactor();
	}

	public double getInsurancePrice() {
		return policy.getInsurancePrice();
	}


	public void execute(HttpServletRequest request) throws Exception {

		session = InsuranceSessionHelper.getSession();

		defaultBirthday = df.parse(request.getParameter("birthdate"));

		policy.setBasePrice(500.00);

		driverMale.setBirhDate(defaultBirthday);

		driverMale.setId(400);
		driverMale.setGenre(Integer.parseInt(request.getParameter("sexo")));
		driverMale.setMaritalState(Integer.parseInt(request
				.getParameter("estadoCivil")));
		driverMale.setHasChildren(Boolean.parseBoolean(request
				.getParameter("temFilhos")));
		driverMale.setPriorClaims(Integer.parseInt(request
				.getParameter("sinistros")));
		driverMale.setLicenceYears(Integer.parseInt(request
				.getParameter("habilitacao")));

		driverAdit.setDriverId(driverMale.getId());
		driverAdit.setDayVehiclePlace(Integer.parseInt(request
				.getParameter("garagemDia")));
		driverAdit.setNightVehiclePlace(Integer.parseInt(request
				.getParameter("garagemNoite")));
		driverAdit.setJobStatus(Integer.parseInt(request
				.getParameter("profissao")));
		driverAdit.setResidenceStatus(Integer.parseInt(request
				.getParameter("condicaoImovel")));

		suppinfo.setDriverId(driverMale.getId());
		suppinfo.setExtraAssistence(Boolean.parseBoolean(request
				.getParameter("assistencia24h")));
		suppinfo.setExtraCar(Boolean.parseBoolean(request
				.getParameter("carroReserva")));
		suppinfo.setGlassCoverage(Boolean.parseBoolean(request
				.getParameter("vidros")));
		suppinfo.setNonRelatedExpenses(Boolean.parseBoolean(request
				.getParameter("despExtra")));

		accessCov.setDriverId(driverMale.getId());

		accessCov.setAlarmSystemValue(Double.parseDouble(request
				.getParameter("sistemaSom")));
		accessCov.setArmorValue(Double.parseDouble(request
				.getParameter("blindagem")));
		accessCov.setSoundSystemValue(Double.parseDouble(request
				.getParameter("sistemaSom")));

		policy.setApproved(false);

		session.insert(policy);
		session.insert(driverMale);
		session.insert(driverAdit);
		session.insert(suppinfo);
		session.insert(accessCov);

		session.fireAllRules();

		System.out.println("Insurance Factor: "
				+ driverMale.getInsuranceFactor());
		System.out.println("Is Approved     : " + policy.isApproved());
		System.out.println("Insurance Price :" + policy.getInsurancePrice());
		
		session.dispose();
	}
}
