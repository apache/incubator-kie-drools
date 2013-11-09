package org.jbpm.process.workitem.bpmn2;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService(serviceName="SimpleService")
public class SimpleService {
    
    private static final Logger logger = LoggerFactory.getLogger(SimpleService.class);

    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String name) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            logger.error("Interupted while waiting", e);
        }
        logger.info("Hello {}", name);
        return "Hello " + name;
    }
    
    @WebMethod(operationName = "helloException")
    public String helloException(@WebParam(name = "name") String name) {
        logger.info("Throwing error for {}", name);
        throw new RuntimeException("Hello exception " + name);
    }
    
    @WebMethod(operationName = "helloMulti")
    public String helloMulitpleParams(@WebParam(name = "name") String name, @WebParam(name = "lastname") String lastname) {

        logger.info("Hello first name {} and last name {}", name, lastname);
        return "Hello " + lastname + ", " + name;
    }
    
    @WebMethod(operationName = "helloMultiInt")
    public String helloMulitpleIntParams(@WebParam(name = "name") int first, @WebParam(name = "lastname") int second) {

        logger.info("Got numbers first {} and last {}", first, second);
        return "Hello " + first + ", " + second;
    }
    
}
