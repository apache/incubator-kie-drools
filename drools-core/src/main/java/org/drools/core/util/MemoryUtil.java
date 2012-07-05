package org.drools.core.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;

public class MemoryUtil {

    public static final MemoryStats permGenStats;

    private MemoryUtil() { }

    static {
        MemoryPoolMXBean permGenBean = null;
        for (MemoryPoolMXBean mx : ManagementFactory.getMemoryPoolMXBeans()) {
            if (mx.getName() != null && mx.getName().contains("Perm")) {
                permGenBean = mx;
                break;
            }
        }
        permGenStats = new MemoryStats(permGenBean);
    }

    public static class MemoryStats {
        private final MemoryPoolMXBean memoryBean;

        public MemoryStats(MemoryPoolMXBean memoryBean) {
            this.memoryBean = memoryBean;
        }

        public boolean isUsageThresholdExceeded(int threshold) {
            MemoryUsage memoryUsage = getMemoryUsage();
            return memoryUsage != null && memoryUsage.getUsed() * 100 / memoryUsage.getMax() >= threshold;
        }

        private MemoryUsage getMemoryUsage() {
            return memoryBean != null ? memoryBean.getUsage() : ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        }
    }
}
