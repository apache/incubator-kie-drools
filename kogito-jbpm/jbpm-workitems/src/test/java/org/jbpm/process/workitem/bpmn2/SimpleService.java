package org.jbpm.process.workitem.bpmn2;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(serviceName="SimpleService")
public class SimpleService {

    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String name) {
        
        return "Hello " + name;
    }
}
