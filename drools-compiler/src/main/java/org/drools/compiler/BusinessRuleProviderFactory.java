package org.drools.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;

import org.drools.CheckedDroolsException;
import org.drools.util.ServiceRegistryImpl;

public class BusinessRuleProviderFactory {
    private static BusinessRuleProviderFactory instance = new BusinessRuleProviderFactory();
    private BusinessRuleProvider provider;

    private BusinessRuleProviderFactory() {
    }

    public static BusinessRuleProviderFactory getInstance() {
        return instance;
    }

    public BusinessRuleProvider getProvider() throws CheckedDroolsException {
        if (null == provider)
            provider = loadProvider();
        return provider;
    }

    private BusinessRuleProvider loadProvider() throws CheckedDroolsException {
        String interfaceName = BusinessRuleProvider.class.getName();
        try {
            URL systemResource = null;
            for (Enumeration<URL> systemResources = ClassLoader
                    .getSystemResources("META-INF/services/" + interfaceName); systemResources
                    .hasMoreElements();) {
                if (null != systemResource)
                    throwMultipleImplementationsDetected();
                systemResource = systemResources.nextElement();
            }

            if (systemResource == null) {
                throwNoImplementationFound();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(systemResource.openStream()));
            String className = null;
            for (String currentName; (currentName = reader.readLine()) != null;) {
                if (className != null) {
                    throwMultipleImplementationsDetected();
                }
                className = currentName;
            }

            if (null == className) {
                throwNoImplementationFound();
            }

            ServiceRegistryImpl.getInstance().addDefault(BusinessRuleProvider.class, className);
            return ServiceRegistryImpl.getInstance().get(BusinessRuleProvider.class);
        } catch (IOException e) {
            throw new CheckedDroolsException("Error obtaining " + interfaceName, e);
        }
    }

    private void throwNoImplementationFound() throws CheckedDroolsException {
        throw new CheckedDroolsException("Unable to find implementation for BusinessRuleProvider");
    }

    private void throwMultipleImplementationsDetected() {
        throw new IllegalStateException("multiple BusinessRuleProvider implementations detected");
    }
}
