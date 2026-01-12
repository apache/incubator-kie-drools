/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.services.context;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Utility class for managing process instance context in logging operations.
 * This class uses SLF4J's Mapped Diagnostic Context (MDC) to ensure process instance IDs
 * are automatically included in log messages.
 *
 * When no process instance is available, an empty string is used as the default value
 * to provide cleaner formatting and easier searching in log aggregation systems.
 *
 * Thread Safety: This class is thread-safe and properly manages context isolation
 * between different threads using MDC's inherent ThreadLocal-based storage.
 */
public final class ProcessInstanceContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessInstanceContext.class);

    private static final Map<String, ContextExtension> EXTENSIONS = new ConcurrentHashMap<>();

    /**
     * MDC key used to store the process instance ID.
     * This key should be referenced in logging configurations.
     */
    public static final String MDC_PROCESS_INSTANCE_KEY = "processInstanceId";

    /**
     * Default value used when no process instance ID is available.
     * Empty string for cleaner log formatting and easier searching.
     */
    public static final String GENERAL_CONTEXT = "";

    // Private constructor to prevent instantiation
    private ProcessInstanceContext() {
        // Utility class
    }

    /**
     * Sets the process instance ID for the current thread context.
     * This method updates SLF4J MDC and only performs the update if the value changes.
     *
     * @param processInstanceId the process instance ID to set, or null to use general context
     */
    public static void setProcessInstanceId(String processInstanceId) {
        String effectiveId = processInstanceId != null ? processInstanceId : GENERAL_CONTEXT;

        // Only update MDC if the value is changing (optimization)
        String currentId = MDC.get(MDC_PROCESS_INSTANCE_KEY);
        if (!effectiveId.equals(currentId)) {
            MDC.put(MDC_PROCESS_INSTANCE_KEY, effectiveId);
        }
    }

    /**
     * Gets the current process instance ID from MDC.
     *
     * @return the current process instance ID, or empty string if no context is set
     */
    public static String getProcessInstanceId() {
        String id = MDC.get(MDC_PROCESS_INSTANCE_KEY);
        return id != null ? id : GENERAL_CONTEXT;
    }

    /**
     * Clears the process instance context for the current thread.
     * This resets the MDC to the general context (empty string).
     */
    public static void clear() {
        MDC.remove(MDC_PROCESS_INSTANCE_KEY);
    }

    /**
     * Checks if a process instance context is currently set.
     *
     * @return true if a process instance context is set, false otherwise
     */
    public static boolean hasContext() {
        String id = MDC.get(MDC_PROCESS_INSTANCE_KEY);
        return id != null && !GENERAL_CONTEXT.equals(id);
    }

    /**
     * Gets a copy of the current MDC context map for propagation to other threads.
     * This is useful for async operations that need to maintain the same logging context.
     *
     * @return a copy of the current MDC context map, or null if no context is set
     */
    public static Map<String, String> copyContextForAsync() {
        return MDC.getCopyOfContextMap();
    }

    /**
     * Append to the MDC context map the extensions related data from a previously copied context.
     * This is useful for async operations that need to restore logging context.
     * Only registered extensions keys will be appended in the MDC
     *
     * @param contextMap the context map to append
     */
    public static void setContextFromAsync(Map<String, String> contextMap) {
        if (contextMap != null) {
            Map<String, String> finalContextMap;

            // Preserve existing MDC context by merging with async context
            Map<String, String> currentMdc = MDC.getCopyOfContextMap();
            if (currentMdc != null) {
                // Merge current context with async context, giving precedence to async values
                finalContextMap = new HashMap<>(currentMdc);
                finalContextMap.putAll(contextMap); // async values override existing ones
            } else {
                finalContextMap = new HashMap<>(contextMap);
            }

            // Restore all non-extension keys directly to MDC
            Map<String, String> nonExtensionKeys = new HashMap<>();
            for (Map.Entry<String, String> entry : finalContextMap.entrySet()) {
                String key = entry.getKey();
                boolean isExtensionKey = false;

                // Check if this key belongs to any registered extension
                for (String prefix : EXTENSIONS.keySet()) {
                    if (key.startsWith(prefix)) {
                        isExtensionKey = true;
                        break;
                    }
                }

                // If not an extension key, restore it directly
                if (!isExtensionKey) {
                    nonExtensionKeys.put(key, entry.getValue());
                }
            }

            // Set non-extension keys directly in MDC
            nonExtensionKeys.forEach(MDC::put);

            // Restore extension keys through their handlers
            restoreExtensionKeys(finalContextMap);
        }
    }

    /**
     * Registers a context extension that will participate in context preservation.
     *
     * @param prefix the MDC key prefix this extension manages. Must ends with a dot '.'
     * @param extension the extension to register
     * @throws IllegalArgumentException if prefix is null/empty/does not end by a dot or if extension is null/empty or if prefix is already used
     */
    public static void registerExtension(String prefix, ContextExtension extension) {
        if (prefix == null || prefix.isEmpty()) {
            throw new IllegalArgumentException("Extension prefix must not be null or empty");
        }
        if (!prefix.endsWith(".")) {
            throw new IllegalArgumentException("Extension prefix must end with a dot '.' to prevent collisions");
        }
        if (extension == null) {
            throw new IllegalArgumentException("Extension must not be null");
        }
        if (EXTENSIONS.containsKey(prefix) && !EXTENSIONS.get(prefix).equals(extension)) {
            throw new IllegalArgumentException("Extension prefix already used by another extension: " + EXTENSIONS.get(prefix).getClass());
        }
        EXTENSIONS.put(prefix, extension);
        LOGGER.debug("Registered context extension for prefix: {}", prefix);
    }

    /**
     * Filters the context map to include only core ProcessInstanceContext keys.
     *
     * @param contextMap the context map to filter
     * @return filtered context map containing only core keys
     */
    private static Map<String, String> filterCoreKeys(Map<String, String> contextMap) {
        Map<String, String> coreKeys = new HashMap<>();

        // Always preserve the process instance ID
        String processInstanceId = contextMap.get(MDC_PROCESS_INSTANCE_KEY);
        if (processInstanceId != null) {
            coreKeys.put(MDC_PROCESS_INSTANCE_KEY, processInstanceId);
        }

        return coreKeys;
    }

    /**
     * Restores extension-specific keys using registered extensions.
     *
     * @param contextMap the full context map containing all keys
     */
    private static void restoreExtensionKeys(Map<String, String> contextMap) {
        for (Map.Entry<String, ContextExtension> entry : EXTENSIONS.entrySet()) {
            String prefix = entry.getKey();
            ContextExtension extension = entry.getValue();

            // Extract keys for this extension
            Map<String, String> extensionKeys = new HashMap<>();
            for (Map.Entry<String, String> contextEntry : contextMap.entrySet()) {
                if (contextEntry.getKey().startsWith(prefix)) {
                    extensionKeys.put(contextEntry.getKey(), contextEntry.getValue());
                }
            }

            // Let the extension restore its keys
            if (!extensionKeys.isEmpty()) {
                try {
                    extension.restoreKeys(extensionKeys);
                } catch (Exception e) {
                    LOGGER.warn("Extension for prefix '{}' failed to restore keys: {}", prefix, e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Gets the registered extension matching the prefix
     * 
     * @param prefix the prefix name of the extension
     * @return th ContextExtension assigned with the provided prefix, or null if no context is set
     */
    public static ContextExtension getExtension(String prefix) {
        return EXTENSIONS.get(prefix);
    }

    /**
     * Clears all registered extensions.
     * This is primarily for testing purposes to ensure test isolation.
     */
    public static void clearExtensions() {
        EXTENSIONS.clear();
    }

}
