package org.jbpm.casemgmt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;

public class CaseMgmtCaseFileTest extends JbpmJUnitBaseTestCase {
    
    public CaseMgmtCaseFileTest() {
        super(true, true);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testAdHoc() {
        createRuntimeManager("EmptyCase.bpmn2");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        CaseMgmtService caseMgmtService = new CaseMgmtUtil(runtimeEngine);
        
        ProcessInstance processInstance = caseMgmtService.startNewCase("My new case");
        assertEquals("My new case", caseMgmtService.getProcessInstanceDescription(processInstance.getId()));
        
        Map<String, Object> data = caseMgmtService.getCaseData(processInstance.getId());
        prettyPrintData(data);
        assertEquals(1, data.size());
        
        caseMgmtService.setCaseData(processInstance.getId(), "customerId", "CustomerX");
        caseMgmtService.setCaseData(processInstance.getId(), "productCount", 2);
        List<ProductInfo> products = new ArrayList<ProductInfo>();
        products.add(new ProductInfo(1, "TV"));
        products.add(new ProductInfo(2, "Stereo"));
        caseMgmtService.setCaseData(processInstance.getId(), "products", products);
        
        data = caseMgmtService.getCaseData(processInstance.getId());
        prettyPrintData(data);
        assertEquals(4, data.size());
        
        assertEquals("CustomerX", data.get("customerId"));
        assertEquals(2, data.get("productCount"));
        assertEquals(2, ((List<ProductInfo>) data.get("products")).size());
    }
    
    public void prettyPrintData(Map<String, Object> data) {
        System.out.println("***** Case data: *****");
        for (Map.Entry<String, Object> entry: data.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }
    
    public static class ProductInfo implements Serializable {
        private static final long serialVersionUID = 630L;
        private long id;
        private String name;
        public ProductInfo(long id, String name) {
            this.id = id;
            this.name = name;
        }
        public long getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public String toString() {
            return name + "(" + id + ")";
        }
    }
    
}
