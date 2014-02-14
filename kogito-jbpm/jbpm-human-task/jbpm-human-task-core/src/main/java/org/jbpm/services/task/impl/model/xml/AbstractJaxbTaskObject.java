package org.jbpm.services.task.impl.model.xml;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.kie.api.task.model.Group;
import org.kie.api.task.model.User;

public class AbstractJaxbTaskObject<T> {

    protected Class<?> realClass;
   
    public AbstractJaxbTaskObject() {
        throw new UnsupportedOperationException("No-arg constructor must be implemented by the concrete class.");
    }
    
    public AbstractJaxbTaskObject(Class<?> realClass) { 
       this.realClass = realClass; 
    }
    
    public AbstractJaxbTaskObject(T taskObject, Class<?> objectInterface) {
        this(objectInterface);
        if (taskObject != null) {
	        for (Method getIsMethod : objectInterface.getDeclaredMethods() ) { 
	            String methodName = getIsMethod.getName();
	            String fieldName;
	            if( getIsMethod.getReturnType().equals(User.class) ) { 
	                continue;
	            }
	            if (methodName.startsWith("get")) {
	                fieldName = methodName.substring(3);
	            } else if (methodName.startsWith("is")) {
	                fieldName = methodName.substring(2);
	            } 
	            else {
	                assert false : "Unknown method ´" + methodName + "' in "+ this.getClass().getSimpleName() + ".";
	                continue;
	            }
	            // getField -> field (lowercase f)
	            fieldName = fieldName.substring(0,1).toLowerCase() + fieldName.substring(1);
	            try { 
	                Field field = this.getClass().getDeclaredField(fieldName);
	                boolean origAccessStatus = field.isAccessible();
	                field.setAccessible(true);
	                Object setObject = getIsMethod.invoke(taskObject, new Object[0]);
	                field.set(this, setObject);
	                field.setAccessible(origAccessStatus);
	            } catch( Exception e ) { 
	               throw new RuntimeException("Unable to initialize " + fieldName + " when creating " + this.getClass().getSimpleName() + ".", e ); 
	            }
	        }
        }
    }
    
    /**
     * I was forced to do this because we put the interfaces to our *ENTITIES* in the *PUBLIC* API. 
     */
    static class GetterUser implements User {
    
        private final String id;
        public GetterUser(String id) { 
            this.id = id;
        }
        
        @Override
        public String getId() {
            return this.id;
        }
    
        public void writeExternal(ObjectOutput out) throws IOException { unsupported(User.class); }
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException { unsupported(User.class); } 
    }
    
    static class GetterGroup implements Group { 
        private final String id;
        public GetterGroup(String id) { 
            this.id = id;
        }
        
        @Override
        public String getId() {
            return this.id;
        }
    
        public void writeExternal(ObjectOutput out) throws IOException { unsupported(User.class); }
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException { unsupported(User.class); } 
    }

    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        unsupported(realClass);
    }

    public void writeExternal(ObjectOutput arg0) throws IOException {
        unsupported(realClass);
    }
    
    static Object unsupported(Class<?> realClass) { 
        String methodName = (new Throwable()).getStackTrace()[1].getMethodName();
        throw new UnsupportedOperationException(methodName + " is not supported on the JAXB " + realClass.getSimpleName() + " implementation.");
    }
}
