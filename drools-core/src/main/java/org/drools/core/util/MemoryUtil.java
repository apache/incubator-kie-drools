package org.drools.core.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;

public class MemoryUtil {

    public static final MemoryStats permGenStats;

    private MemoryUtil() { }

    static {
        if (!hasPermGen() || isGAE()) {
            permGenStats = new DummyMemoryStats();
        } else {
            MemoryPoolMXBean permGenBean = null;
            for (MemoryPoolMXBean mx : ManagementFactory.getMemoryPoolMXBeans()) {
                if (mx.getName() != null && mx.getName().contains("Perm")) {
                    permGenBean = mx;
                    break;
                }
            }
            permGenStats = new MBeanMemoryStats(permGenBean);
        }
    }

    public interface MemoryStats {
        boolean isUsageThresholdExceeded(int threshold);
    }

    public static class DummyMemoryStats implements MemoryStats {
        @Override
        public boolean isUsageThresholdExceeded( int threshold ) {
            return false;
        }
    }

    public static class MBeanMemoryStats implements MemoryStats {
        private final MemoryPoolMXBean memoryBean;

        public MBeanMemoryStats(MemoryPoolMXBean memoryBean) {
            this.memoryBean = memoryBean;
        }

        @Override
        public boolean isUsageThresholdExceeded(int threshold) {
            MemoryUsage memoryUsage = getMemoryUsage();
            return memoryUsage != null && memoryUsage.getUsed() * 100 / memoryUsage.getMax() >= threshold;
        }

        private MemoryUsage getMemoryUsage() {
            return memoryBean != null ? memoryBean.getUsage() : ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        }
    }

    public static boolean hasPermGen() {
        String javaVersion = System.getProperty("java.version");
        return javaVersion.startsWith( "1.7" ) || javaVersion.startsWith( "1.6" ) || javaVersion.startsWith( "1.5" );
    }

    private static boolean isGAE() {
        String p = System.getProperty("com.google.appengine.runtime.environment");
        return p != null && !p.equals("");
    }
}
