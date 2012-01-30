package org.drools.marshalling.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitingReadWriteLockProxy implements InvocationHandler { 
   
    private static Logger logger = LoggerFactory.getLogger(WaitingReadWriteLockProxy.class);
   
    public static Object semaphore = new Object();
    
    public static Object newInstance( ReadWriteLock readWriteLock ) {
        return Proxy.newProxyInstance(
                readWriteLock.getClass().getClassLoader(), 
                getAllInterfaces(readWriteLock),
                new WaitingReadWriteLockProxy(readWriteLock));
    }
    
    public static Object newInstance( Lock readOrWriteLock ) {
        return Proxy.newProxyInstance(
                readOrWriteLock.getClass().getClassLoader(), 
                getAllInterfaces(readOrWriteLock),
                new WaitingReadWriteLockProxy(readOrWriteLock));
    }
    
    /**
     * This method is used in the {@link #newInstance(Object)} method to retrieve all applicable interfaces
     *   that the proxy object must conform to. 
     * @param obj The object that will be proxied. 
     * @return Class<?> [] an array of all applicable interfaces.
     */
    protected static Class<?> [] getAllInterfaces( Object obj ) { 
        Class<?> [] interfaces = new Class [0];
        Class<?> superClass = obj.getClass();
        while( superClass != null ) { 
            Class<?> [] addThese = superClass.getInterfaces();
            if( addThese.length > 0 ) { 
                Class<?> [] moreinterfaces = new Class [interfaces.length + addThese.length];
                System.arraycopy(interfaces, 0, moreinterfaces, 0, interfaces.length);
                System.arraycopy(addThese, 0, moreinterfaces, interfaces.length, addThese.length);
                interfaces = moreinterfaces;
            }
            superClass = superClass.getSuperclass();
        }
        return interfaces;
    }
    

    ReadWriteLock master = null;
    ReadLock reader = null;
    WriteLock writer = null;
    Lock readerInstance = null;
    Lock writerInstance = null;
    
    private WaitingReadWriteLockProxy(ReadWriteLock readWriteLock) { 
        this.master = readWriteLock;
        this.readerInstance = (Lock) newInstance(master.readLock());
        this.writerInstance = (Lock) newInstance(master.writeLock());
    }
    
    private WaitingReadWriteLockProxy(Lock readerOrWriter) { 
        if( readerOrWriter instanceof ReadLock ) { 
            this.reader = (ReadLock) readerOrWriter;
        }
        else if( readerOrWriter instanceof WriteLock ) { 
            this.writer = (WriteLock) readerOrWriter;
        }
        else { 
            throw new UnsupportedOperationException("Lock type not appropriate for this proxy: " + readerOrWriter.getClass().getSimpleName() );
        }
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        String methodName = method.getName();
        
        if( "readLock".equals(methodName) ) { 
           return readerInstance;
        }
        if( "writeLock".equals(methodName) ) { 
           return writerInstance;
        }
        if( "unlock".equals(methodName) ) { 
           if( reader != null ) { 
               reader.unlock();
               synchronized(semaphore) { 
                   StackTraceElement [] ste = Thread.currentThread().getStackTrace();
                   for( int i = 0; i < ste.length; ++i ) { 
                       if( (SerializablePlaceholderResolverStrategyTest.class.getName() + "$ReadAndWaitTestCase")
                            .equals(ste[i].getClassName()) ) {
                           semaphore.wait();
                           break;
                       }
                   }
               }
           }
           else if( writer != null ) { 
               writer.unlock();
           }
           else { 
                throw new UnsupportedOperationException("No real objects available to invoke method " + methodName );
           }
        }
        else { 
            if( master != null ) { 
                result = invoke(method, master, args);
            }
            else if( reader != null ) { 
                result = invoke(method, reader, args);
            }
            else if( writer != null ) { 
                result = invoke(method, writer, args);
            }
            else { 
                throw new UnsupportedOperationException("No real objects available to invoke method " + methodName );
            }
        }
        
        return result;
    }

    private Object invoke( Method method, Object object, Object[] args) throws Throwable { 
        Object result = null;
        try { 
            result = method.invoke(object, args);
        } catch( InvocationTargetException ite ) { 
           logger.warn(method.getName() + " threw " + ite.getClass().getSimpleName() + ": " + ite.getMessage());
           throw ite;
        }
        return result;
    }
    
    public ReadWriteLock getMasterLock() { 
        return master;
    }
    
}
