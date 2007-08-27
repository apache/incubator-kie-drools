package org.acme.insurance.web;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.acme.insurance.base.AccessoriesCoverage;
import org.acme.insurance.base.Driver;
import org.acme.insurance.base.DriverAdditionalInfo;
import org.acme.insurance.base.Policy;
import org.acme.insurance.base.SupplementalInfo;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.agent.RuleAgent;
import org.drools.compiler.PackageBuilder;

public class DroolsBusiness {

	private DriverAdditionalInfo driverAdit = new DriverAdditionalInfo();
	private SupplementalInfo suppinfo = new SupplementalInfo();
	private AccessoriesCoverage accessCov = new AccessoriesCoverage();
	private Driver driverMale = new Driver();
	private SimpleDateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy");
	private Date defaultBirthday;
	private Policy policy = new Policy();

	private RuleBase rulebase;
	private StatefulSession session;

	public boolean isApproved() {
		return policy.isApproved();
	}

	public Double getBasePrice() {
		return policy.getBasePrice();
	}

	public Double getRiskFactor() {
		return driverMale.getInsuranceFactor();
	}

	public Double getInsurancePrice() {
		return policy.getInsurancePrice();
	}

	public RuleBase loadRuleBaseFromRuleAgent() {
		RuleAgent agent = RuleAgent
				.newRuleAgent("/brmsdeployedrules.properties");
		RuleBase rulebase = agent.getRuleBase();
		return rulebase;
	}

	private RuleBase loadRuleBaseFromDRL() throws Exception {

		PackageBuilder builder = new PackageBuilder();
		builder.addPackageFromDrl(getTechnicalRules("/approval/insurancefactor.drl"));
		builder.addPackageFromDrl(getTechnicalRules("/approval/approval.drl"));
		builder.addPackageFromDrl(getTechnicalRules("/approval/calculateInsurance.drl"));
		builder.addPackageFromDrl(getTechnicalRules("/approval/marginalage.drl"));
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage(builder.getPackage());
		return ruleBase;
	}

	private Reader getTechnicalRules(String name) {

		InputStream stream = this.getClass().getResourceAsStream(name);

		return new InputStreamReader(stream);
	}

	protected void setUp() throws Exception {

		rulebase = loadRuleBaseFromRuleAgent();
		session = rulebase.newStatefulSession();

		session.setFocus("risk assessment");

		SimpleDateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy");
		defaultBirthday = df.parse("18/09/1983");

	}

	public void execute(HttpServletRequest request) throws Exception {

		setUp();

		defaultBirthday = df
				.parse(Integer.parseInt(request
						.getParameter("data_nascimento_dia"))
						+ "/"
						+ Integer.parseInt(request
								.getParameter("data_nascimento_mes"))
						+ "/"
						+ Integer.parseInt(request
								.getParameter("data_nascimento_ano")));

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

		session.setFocus("risk assessment");

		session.fireAllRules();

		System.out.println("Insurance Factor: "
				+ driverMale.getInsuranceFactor());
		System.out.println("Is Approved     : " + policy.isApproved());
		System.out.println("Insurance Price :" + policy.getInsurancePrice());

	}

}
