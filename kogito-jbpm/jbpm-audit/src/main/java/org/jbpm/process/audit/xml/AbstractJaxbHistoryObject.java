package org.jbpm.process.audit.xml;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;

public abstract class AbstractJaxbHistoryObject<T> {
    
    protected Class<?> realClass;
   
    public AbstractJaxbHistoryObject() {
        throw new UnsupportedOperationException("No-arg constructor must be implemented by the concrete class.");
    }
    
    public AbstractJaxbHistoryObject(Class<?> realClass) { 
       this.realClass = realClass; 
    }
    
    public AbstractJaxbHistoryObject(T taskObject, Class objectInterface) {
        this(objectInterface);
        for (Method getIsMethod : objectInterface.getDeclaredMethods() ) { 
            String methodName = getIsMethod.getName();
            String fieldName;
            if (methodName.startsWith("get")) {
                fieldName = methodName.substring(3);
            } else if (methodName.startsWith("is")) {
                fieldName = methodName.substring(2);
            } 
            else {
                continue;
//                throw new UnsupportedOperationException("Unknown method ´" + methodName + "' in "+ this.getClass().getSimpleName() + ".");
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
    
    public T createEntityInstance() throws Exception { 
        Class [] constructorArgTypes = new Class[0];
        Constructor<?> constructor = this.realClass.getConstructor(constructorArgTypes);
        Object [] initArgs = new Object[0];
        T entity = (T) constructor.newInstance(initArgs);
        
        for (Field field : this.getClass().getDeclaredFields() ) { 
            String fieldName = field.getName();
            try { 
                Field entityField = this.realClass.getDeclaredField(fieldName);
                
                boolean origAccessStatus = field.isAccessible();
                boolean entityOrigAccessStatus = entityField.isAccessible();
                field.setAccessible(true);
                entityField.setAccessible(true);
                
                Object setObject = field.get(this);
                entityField.set(entity, setObject);
                
                field.setAccessible(origAccessStatus);
                entityField.setAccessible(entityOrigAccessStatus);
            } catch( Exception e ) { 
               throw new RuntimeException("Unable to initialize " + fieldName + " when creating " + this.getClass().getSimpleName() + ".", e ); 
            }
        }
        
        return entity;
    }
    
    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        String methodName = (new Throwable()).getStackTrace()[0].getMethodName();
        throw new UnsupportedOperationException(methodName + " is not supported on the JAXB " + realClass.getSimpleName()
                + " implementation.");
    }

    public void writeExternal(ObjectOutput arg0) throws IOException {
        String methodName = (new Throwable()).getStackTrace()[0].getMethodName();
        throw new UnsupportedOperationException(methodName + " is not supported on the JAXB " + realClass.getSimpleName()
                + " implementation.");
    }
}
