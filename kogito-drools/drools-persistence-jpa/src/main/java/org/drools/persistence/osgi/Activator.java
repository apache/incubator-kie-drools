package org.drools.persistence.osgi;

import org.hibernate.ejb.HibernatePersistence;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator
    implements
    BundleActivator {

    public void start(BundleContext bc) throws Exception {
        System.out.println( "In Activator : " + HibernatePersistence.class );
    }

    public void stop(BundleContext bc) throws Exception {
    }

}
