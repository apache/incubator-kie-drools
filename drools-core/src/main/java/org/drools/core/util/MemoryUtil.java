/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
