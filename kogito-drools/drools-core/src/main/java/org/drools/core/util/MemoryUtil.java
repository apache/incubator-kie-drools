package org.drools.core.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;

public class MemoryUtil {

    public static final MemoryStats permGenStats;

    private MemoryUtil() { }

    static {
        if(!ClassUtils.isAndroid()) {
            MemoryPoolMXBean permGenBean = null;
            for (MemoryPoolMXBean mx : ManagementFactory.getMemoryPoolMXBeans()) {
                if (mx.getName() != null && mx.getName().contains("Perm")) {
                    permGenBean = mx;
                    break;
                }
            }
            permGenStats = new MemoryStats(permGenBean);
        } else {
            permGenStats = new MemoryStats();
        }
    }

    public static class MemoryStats {
        private final MemoryPoolMXBean memoryBean;

        public MemoryStats() {
            memoryBean = null;
        }

        public MemoryStats(MemoryPoolMXBean memoryBean) {
            this.memoryBean = memoryBean;
        }

        public boolean isUsageThresholdExceeded(int threshold) {
            if (!ClassUtils.isAndroid()) {
                MemoryUsage memoryUsage = getMemoryUsage();
                return memoryUsage != null && memoryUsage.getUsed() * 100 / memoryUsage.getMax() >= threshold;
            } else {
                return false;
            }
        }

        public MemoryUsage getMemoryUsage() {
            return memoryBean != null ? memoryBean.getUsage() : ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        }
    }
}
