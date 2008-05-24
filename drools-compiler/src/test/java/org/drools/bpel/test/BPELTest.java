package org.drools.bpel.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.bpel.core.BPELActivity;
import org.drools.bpel.core.BPELAssign;
import org.drools.bpel.core.BPELFaultHandler;
import org.drools.bpel.core.BPELFlow;
import org.drools.bpel.core.BPELInvoke;
import org.drools.bpel.core.BPELProcess;
import org.drools.bpel.core.BPELReceive;
import org.drools.bpel.core.BPELReply;
import org.drools.bpel.core.BPELSequence;
import org.drools.bpel.instance.BPELProcessInstance;
import org.drools.common.AbstractRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.ProcessBuilder;
import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.core.datatype.impl.type.StringDataType;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemHandler;
import org.drools.process.instance.WorkItemManager;
import org.drools.reteoo.ReteooWorkingMemory;

public class BPELTest {

    public static BPELProcess getProcess() {
        BPELProcess process = new BPELProcess();
        process.setName("Purchase Order Process");
        process.setId("1");
        process.setVersion("1");
        process.setPackageName("org.drools.bpel.test");
    	int nodeId = 0;
        
        // variables
        VariableScope variableScope = process.getVariableScope();
        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName("PO");
        variable.setType(new StringDataType());
        variables.add(variable);
        variable = new Variable();
        variable.setName("Invoice");
        variable.setType(new StringDataType());
        variables.add(variable);
        variable = new Variable();
        variable.setName("POFault");
        variable.setType(new StringDataType());
        variables.add(variable);
        variable = new Variable();
        variable.setName("shippingRequest");
        variable.setType(new StringDataType());
        variables.add(variable);
        variable = new Variable();
        variable.setName("shippingInfo");
        variable.setType(new StringDataType());
        variables.add(variable);
        variable = new Variable();
        variable.setName("shippingSchedule");
        variable.setType(new StringDataType());
        variables.add(variable);
        variableScope.setVariables(variables);
        
        // faultHandler
        List<BPELFaultHandler> faultHandlers = new ArrayList<BPELFaultHandler>();
        BPELFaultHandler faultHandler = new BPELFaultHandler();
        faultHandler.setFaultName("cannotCompleteOrder");
        faultHandler.setFaultVariable("POFault");
        BPELReply reply = new BPELReply();
        reply.setId(++nodeId);
        reply.setPartnerLink("purchasing");
        reply.setPortType("lns:purchaseOrderPT");
        reply.setOperation("sendPurchaseOrder");
        reply.setVariable("POFault");
        reply.setFaultName("cannotCompleteOrder");
        faultHandler.setActivity(reply);
        faultHandlers.add(faultHandler);
        process.setFaultHandlers(faultHandlers);
        
        // sequence
        BPELSequence sequence = new BPELSequence();
        sequence.setId(++nodeId);
        sequence.setName("sequence");
        List<BPELActivity> sequenceActivities = new ArrayList<BPELActivity>();
        
        // Receive purchase order
        BPELReceive receive = new BPELReceive();
        receive.setId(++nodeId);
        receive.setName("Receive Purchase Order");
        receive.setPartnerLink("purchasing");
        receive.setPortType("lns:purchaseOrderPT");
        receive.setOperation("sendPurchaseOrder");
        receive.setVariable("PO");
        receive.setCreateInstance(true);
        sequenceActivities.add(receive);
        
        // flow
        BPELFlow flow = new BPELFlow();
        flow.setId(++nodeId);
        flow.setName("flow");
        flow.setLinks(new String[] { "ship-to-invoice", "ship-to-scheduling" });
        List<BPELActivity> flowActivities = new ArrayList<BPELActivity>();
        
            /********** sequence1 **********/
            BPELSequence sequence1 = new BPELSequence();
            sequence1.setId(++nodeId);
            sequence1.setName("sequence1");
            List<BPELActivity> sequence1Activities = new ArrayList<BPELActivity>();
        
            // assign1
            BPELAssign assign1 = new BPELAssign();
            assign1.setId(++nodeId);
            // $shippingRequest.customerInfo <- $PO.customerInfo
            assign1.setAction("");
            sequence1Activities.add(assign1);
            
            // invoke1
            BPELInvoke invoke1 = new BPELInvoke();
            invoke1.setId(++nodeId);
            invoke1.setName("Decide On Shipper");
            invoke1.setPartnerLink("shipping");
            invoke1.setPortType("lns:shippingPT");
            invoke1.setOperation("requestShipping");
            invoke1.setInputVariable("shippingRequest");
            invoke1.setOutputVariable("shippingInfo");
            invoke1.setSourceLinks(new String[] { "ship-to-invoice" });
            sequence1Activities.add(invoke1);
        
            // receive1
            BPELReceive receive1 = new BPELReceive();
            receive1.setId(++nodeId);
            receive1.setName("Arrange Logistics");
            receive1.setPartnerLink("shipping");
            receive1.setPortType("lns:shippingCallbackPT");
            receive1.setOperation("sendSchedule");
            receive1.setVariable("shippingSchedule");
            receive1.setCreateInstance(false);
            receive1.setSourceLinks(new String[] { "ship-to-scheduling" });
            sequence1Activities.add(receive1);
            
            sequence1.setActivities(sequence1Activities);
            flowActivities.add(sequence1);
            
            /********** sequence2 **********/
            BPELSequence sequence2 = new BPELSequence();
            sequence2.setId(++nodeId);
            sequence2.setName("sequence2");
            List<BPELActivity> sequence2Activities = new ArrayList<BPELActivity>();
        
            // invoke2a
            BPELInvoke invoke2a = new BPELInvoke();
            invoke2a.setId(++nodeId);
            invoke2a.setName("Initial Price Calculation");
            invoke2a.setPartnerLink("invoicing");
            invoke2a.setPortType("lns:computePricePT");
            invoke2a.setOperation("initiatePriceCalculation");
            invoke2a.setInputVariable("PO");
            sequence2Activities.add(invoke2a);
            
            // invoke2b
            BPELInvoke invoke2b = new BPELInvoke();
            invoke2b.setId(++nodeId);
            invoke2b.setName("Complete Price Calculation");
            invoke2b.setPartnerLink("invoicing");
            invoke2b.setPortType("lns:computePricePT");
            invoke2b.setOperation("sendShippingPrice");
            invoke2b.setInputVariable("shippingInfo");
            invoke2b.setTargetLinks(new String[] { "ship-to-invoice" });
            sequence2Activities.add(invoke2b);
        
            // receive2
            BPELReceive receive2 = new BPELReceive();
            receive2.setId(++nodeId);
            receive2.setName("Receive Invoice");
            receive2.setPartnerLink("invoicing");
            receive2.setPortType("lns:invoiceCallbackPT");
            receive2.setOperation("sendInvoice");
            receive2.setVariable("Invoice");
            receive2.setCreateInstance(false);
            sequence2Activities.add(receive2);
            
            sequence2.setActivities(sequence2Activities);
            flowActivities.add(sequence2);
            
            /********** sequence3 **********/
            BPELSequence sequence3 = new BPELSequence();
            sequence3.setId(++nodeId);
            sequence3.setName("sequence3");
            List<BPELActivity> sequence3Activities = new ArrayList<BPELActivity>();
        
            // invoke3a
            BPELInvoke invoke3a = new BPELInvoke();
            invoke3a.setId(++nodeId);
            invoke3a.setName("Initiate Production Scheduling");
            invoke3a.setPartnerLink("scheduling");
            invoke3a.setPortType("lns:schedulingPT");
            invoke3a.setOperation("requestProductionScheduling");
            invoke3a.setInputVariable("PO");
            sequence3Activities.add(invoke3a);
            
            // invoke2b
            BPELInvoke invoke3b = new BPELInvoke();
            invoke3b.setId(++nodeId);
            invoke3b.setName("Complete Production Scheduling");
            invoke3b.setPartnerLink("scheduling");
            invoke3b.setPortType("lns:schedulingPT");
            invoke3b.setOperation("sendShippingSchedule");
            invoke3b.setInputVariable("shippingSchedule");
            invoke3b.setTargetLinks(new String[] { "ship-to-scheduling" });
            sequence3Activities.add(invoke3b);
        
            sequence3.setActivities(sequence3Activities);
            flowActivities.add(sequence3);
            
        flow.setActivities(flowActivities);
        sequenceActivities.add(flow);
        
        // reply
        reply = new BPELReply();
        reply.setId(++nodeId);
        reply.setName("Invoice Processing");
        reply.setPartnerLink("purchasing");
        reply.setPortType("lns:purchaseOrderPT");
        reply.setOperation("sendPurchaseOrder");
        reply.setVariable("Invoice");
        sequenceActivities.add(reply);
        
        sequence.setActivities(sequenceActivities);
        process.setActivity(sequence);
        
        Properties properties = new Properties(); 
        properties.put( "processNodeBuilderRegistry", "bpelNodeBuilderRegistry.conf" );
        PackageBuilderConfiguration packageConf = new PackageBuilderConfiguration( properties );
        PackageBuilder packageBuilder = new PackageBuilder(packageConf);
        ProcessBuilder processBuilder = new ProcessBuilder(packageBuilder);
        processBuilder.buildProcess(process);
        return process;
    }
    
    // normal execution
    public static void main(String[] args) {
        BPELProcess process = getProcess();
        // execute
        Properties properties = new Properties(); 
        properties.put( "processInstanceFactoryRegistry", "bpelProcessInstanceFactory.conf" );        
        properties.put( "processNodeInstanceFactoryRegistry", "bpelProcessNodeInstanceFactory.conf" );        
        properties.put( "processContextInstanceFactoryRegistry", "bpelProcessContextInstanceFactory.conf" );        
        RuleBaseConfiguration ruleBaseConf = new RuleBaseConfiguration( properties );
        AbstractRuleBase ruleBase = (AbstractRuleBase) RuleBaseFactory.newRuleBase(ruleBaseConf);
        ruleBase.addProcess(process);
        InternalWorkingMemory workingMemory = new ReteooWorkingMemory(1, ruleBase);
        WorkItemHandler handler = new WebServiceInvocationHandler();
        workingMemory.getWorkItemManager().registerWorkItemHandler("WebServiceInvocation", handler);
        WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger(workingMemory);
        BPELProcessInstance processInstance = (BPELProcessInstance) workingMemory.startProcess("1");
        
        // start process
        webServiceInvocation(processInstance, "purchasing", "lns:purchaseOrderPT", "sendPurchaseOrder", "PURCHASE ORDER");

        // reply to web service invocations
        WorkItem workItem = findWebServiceInvocation(workingMemory, "scheduling", "lns:schedulingPT", "requestProductionScheduling");
        replyWebServiceInvocation(workingMemory, workItem, null);

        workItem = findWebServiceInvocation(workingMemory, "invoicing", "lns:computePricePT", "initiatePriceCalculation");
        replyWebServiceInvocation(workingMemory, workItem, null);
        
        workItem = findWebServiceInvocation(workingMemory, "shipping", "lns:shippingPT", "requestShipping");
        replyWebServiceInvocation(workingMemory, workItem, "SHIPPING");
        
        workItem = findWebServiceInvocation(workingMemory, "invoicing", "lns:computePricePT", "sendShippingPrice");
        replyWebServiceInvocation(workingMemory, workItem, null);
        
        // invoke web service callbacks
        webServiceInvocation(processInstance, "shipping", "lns:shippingCallbackPT", "sendSchedule", "SCHEDULE");
        webServiceInvocation(processInstance, "invoicing", "lns:invoiceCallbackPT", "sendInvoice", "INVOICE");

        // reply to web service invocation
        workItem = findWebServiceInvocation(workingMemory, "scheduling", "lns:schedulingPT", "sendShippingSchedule");
        replyWebServiceInvocation(workingMemory, workItem, null);

        logger.writeToDisk();
    }
    
    // shipping returns fault
    public static void main1(String[] args) {
        BPELProcess process = getProcess();
        // execute
        Properties properties = new Properties(); 
        properties.put( "processInstanceFactoryRegistry", "bpelProcessInstanceFactory.conf" );        
        properties.put( "processNodeInstanceFactoryRegistry", "bpelProcessNodeInstanceFactory.conf" );        
        properties.put( "processContextInstanceFactoryRegistry", "bpelProcessContextInstanceFactory.conf" );        
        RuleBaseConfiguration ruleBaseConf = new RuleBaseConfiguration( properties );
        AbstractRuleBase ruleBase = (AbstractRuleBase) RuleBaseFactory.newRuleBase(ruleBaseConf);
        ruleBase.addProcess(process);
        InternalWorkingMemory workingMemory = new ReteooWorkingMemory(1, ruleBase);
        WorkItemHandler handler = new WebServiceInvocationHandler();
        workingMemory.getWorkItemManager().registerWorkItemHandler("WebServiceInvocation", handler);
        WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger(workingMemory);
        BPELProcessInstance processInstance = (BPELProcessInstance) workingMemory.startProcess("1");
        
        // start process
        webServiceInvocation(processInstance, "purchasing", "lns:purchaseOrderPT", "sendPurchaseOrder", "PURCHASE ORDER");

        // reply to web service invocations
        WorkItem workItem = findWebServiceInvocation(workingMemory, "scheduling", "lns:schedulingPT", "requestProductionScheduling");
        replyWebServiceInvocation(workingMemory, workItem, null);

        workItem = findWebServiceInvocation(workingMemory, "invoicing", "lns:computePricePT", "initiatePriceCalculation");
        replyWebServiceInvocation(workingMemory, workItem, null);
        
        workItem = findWebServiceInvocation(workingMemory, "shipping", "lns:shippingPT", "requestShipping");
        replyWebServiceInvocationFault(workingMemory, workItem, "cannotCompleteOrder", "SHIPPING FAULT");

        logger.writeToDisk();
    }
    
    private static WorkItem findWebServiceInvocation(WorkingMemory workingMemory, String partnerLink, String portType, String operation) {
        Set<WorkItem> workItems = workingMemory.getWorkItemManager().getWorkItems();
        for (Iterator<WorkItem> iterator = workItems.iterator(); iterator.hasNext(); ) {
            WorkItem workItem = iterator.next();
            if ("WebServiceInvocation".equals(workItem.getName())
                    && workItem.getParameter("PartnerLink").equals(partnerLink)
                    && workItem.getParameter("PortType").equals(portType)
                    && workItem.getParameter("Operation").equals(operation)) {
                return workItem;
            }
        }
        return null;
    }
    
    private static void replyWebServiceInvocation(WorkingMemory workingMemory, WorkItem workItem, String result) {
        System.out.println("Replying to web service invocation "
                + workItem.getParameter("PartnerLink") + " "
                + workItem.getParameter("PortType") + " "
                + workItem.getParameter("Operation") + ", message = "
                + workItem.getParameter("Message") + ": "
                + result);
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result", result);
        workingMemory.getWorkItemManager().completeWorkItem(workItem.getId(), results);
    }
    
    private static void replyWebServiceInvocationFault(WorkingMemory workingMemory, WorkItem workItem, String faultName, String result) {
        System.out.println("Replying to web service invocation "
                + workItem.getParameter("PartnerLink") + " "
                + workItem.getParameter("PortType") + " "
                + workItem.getParameter("Operation") + ", faultName = "
                + workItem.getParameter("FaultName") + ", message = "
                + workItem.getParameter("Message") + ": "
                + result);
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result", result);
        results.put("FaultName", faultName);
        workingMemory.getWorkItemManager().completeWorkItem(workItem.getId(), results);
    }
    
    private static void webServiceInvocation(BPELProcessInstance processInstance, String partnerLink, String portType, String operation, String result) {
        System.out.println("Web service invocation "
                + partnerLink + " "
                + portType + " "
                + operation + ": "
                + result);
        processInstance.acceptMessage(partnerLink, portType, operation, result);
    }
    
    public static class WebServiceInvocationHandler implements WorkItemHandler {

        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            System.out.println("Web service invoked "
                + workItem.getParameter("PartnerLink") + " "
                + workItem.getParameter("PortType") + " "
                + (workItem.getParameter("FaultName") == null ? ""
                		: "fault=" + workItem.getParameter("FaultName"))
                + workItem.getParameter("Operation") + ", message = "
                + workItem.getParameter("Message"));
        }
        
        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
            System.out.println("Web service invocation aborted "
                + workItem.getParameter("PartnerLink") + " "
                + workItem.getParameter("PortType") + " "
                + (workItem.getParameter("FaultName") == null ? ""
            		: workItem.getParameter("FaultName"))
                + workItem.getParameter("Operation") + ", message = "
                + workItem.getParameter("Message"));
        }

    }
    
}
