package org.kie.test.util.db;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import org.osjava.sj.memory.MemoryContext;

public class CloseSafeMemoryContextFactory implements InitialContextFactory {

    @SuppressWarnings("rawtypes")
    public Context getInitialContext(Hashtable environment) throws NamingException {

        return new MemoryContext((Hashtable) environment.clone()) {
            @Override
            public Object lookup(String name) throws NamingException {
                Object toReturn = super.lookup(name);
                if (toReturn == null) {
                    throw new NamingException("Name not found: " + name);
                }
                return toReturn;
            }

            @Override
            public void close() throws NamingException {
                // simple-jndi will close your context: http://meri-stuff.blogspot.co.uk/2012/01/running-jndi-and-jpa-without-j2ee.html
            }
        };
    }
}
