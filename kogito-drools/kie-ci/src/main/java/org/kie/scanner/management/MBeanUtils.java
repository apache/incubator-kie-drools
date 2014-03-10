package org.kie.scanner.management;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MBeanUtils {

    public static final String   MBEANS_PROPERTY = "kie.scanner.mbeans";
    private static final Logger  logger          = LoggerFactory.getLogger(MBeanUtils.class);
    private static final boolean IS_MBEAN_ENABLED;
    private static MBeanServer   mbeanServer;

    static {
        String prop = System.getProperty(MBEANS_PROPERTY);
        IS_MBEAN_ENABLED = prop != null && (prop.equalsIgnoreCase("enabled") || prop.equalsIgnoreCase("true"));
    }

    public static boolean isMBeanEnabled() {
        return IS_MBEAN_ENABLED;
    }

    public static synchronized <T> void registerMBean(T mbean,
            Class<T> mbeanInterface,
            ObjectName name) {
        try {
            MBeanServer mbs = getMBeanServer();
            if (!mbs.isRegistered(name)) {
                final StandardMBean adapter = new StandardMBean(mbean, mbeanInterface);
                mbs.registerMBean(adapter,
                        name);
            }
        } catch (Exception e) {
            logger.error("Unable to register mbean " + name + " into the platform MBean Server", e);
        }
    }

    public static synchronized void unregisterMBeanFromServer(ObjectName name) {
        try {
            MBeanServer mbs = getMBeanServer();
            mbs.unregisterMBean(name);
        } catch (Exception e) {
            logger.error("Exception unregistering mbean: " + name, e);
        }
    }

    public static ObjectName createObjectName(String name) {
        try {
            return new ObjectName(name);
        } catch (Exception e) {
            logger.error("Error creating ObjectName for MBean: " + name, e);
            return null;
        }
    }

    private static synchronized MBeanServer getMBeanServer() {
        if (mbeanServer == null) {
            mbeanServer = ManagementFactory.getPlatformMBeanServer();
        }
        return mbeanServer;
    }

    public static synchronized Object getAttribute(ObjectName mbeanName, String attributeName) {
        try {
            MBeanServer mbs = getMBeanServer();
            return mbs.getAttribute(mbeanName, attributeName);
        } catch (Exception e) {
            logger.error("Error retrieving attribute " + attributeName + " for MBean " + mbeanName, e);
            return null;
        }
    }

    public static synchronized void invoke(ObjectName mbeanName, String operation, Object[] params, String[] signature) {
        try {
            MBeanServer mbs = getMBeanServer();
            mbs.invoke(mbeanName, operation, params, signature);
        } catch (Exception e) {
            logger.error("Error invoking operation " + operation + " for MBean " + mbeanName, e);
        }
    }

}
