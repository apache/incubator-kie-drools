package org.jbpm.process.workitem.bpmn2;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(serviceName="SimpleService")
public class SimpleService {

    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String name) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Hello " + name);
        return "Hello " + name;
    }
    
}
