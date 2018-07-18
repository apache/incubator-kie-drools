package org.drools.core.command.runtime.pmml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.core.command.IdentifiableResult;
import org.drools.core.command.RequestContextImpl;
import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.kie.api.KieBase;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.RuleUnitExecutor;

@XmlRootElement(name="apply-pmml-model-command")
@XmlAccessorType(XmlAccessType.NONE)
public class ApplyPmmlModelCommand implements ExecutableCommand<PMML4Result>, IdentifiableResult {
	private static final long serialVersionUID = 19630331;
	@XmlAttribute(name="outIdentifier")
	private String outIdentifier;
	@XmlElement(name="requestData")
	private PMMLRequestData requestData;
	
	public ApplyPmmlModelCommand() {
		// Necessary for JAXB
	}
	
	public ApplyPmmlModelCommand(PMMLRequestData requestData) {
		this.requestData = requestData;
	}
	
	public PMMLRequestData getRequestData() {
		return requestData;
	}

	public void setRequestData(PMMLRequestData requestData) {
		this.requestData = requestData;
	}

	@Override
	public String getOutIdentifier() {
		return outIdentifier;
	}

	@Override
	public void setOutIdentifier(String outIdentifier) {
		this.outIdentifier = outIdentifier;
	}

	@Override
	public PMML4Result execute(Context context) {
		PMML4Result resultHolder = new PMML4Result(requestData.getCorrelationId());
		RequestContextImpl ctx = (RequestContextImpl)context;
		KieBase kbase = ((RegistryContext)context).lookup(KieBase.class);
		if (kbase == null) {
			System.out.println("KieBase not found in context!");
			return null;
		} else {
			KieContainer kcont =  ((KnowledgeBaseImpl)kbase).getKieContainer();
			kcont.getKieBaseNames().forEach(n -> {System.out.println(n);});
			kbase.getKiePackages().forEach(kp -> {
				System.out.println("Package: "+kp.getName());
			});
		}
		return null;
//		RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
//		DataSource<PMMLRequestData> data = executor.newDataSource("request", this.requestData);
//		DataSource<PMML4Result> resultData = executor.newDataSource("results", resultHolder);
//		System.out.println("Setting result: "+resultHolder);
//		ctx.lookup(ExecutionResultImpl.class).setResult(this.outIdentifier, resultHolder);
//		return resultHolder;
	}

}
