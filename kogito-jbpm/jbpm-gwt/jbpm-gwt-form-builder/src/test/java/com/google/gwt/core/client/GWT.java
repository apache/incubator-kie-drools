/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.gwt.core.client;

import java.lang.reflect.Constructor;

/**
 * Mocks core functionality that in some cases requires direct support from
 * the compiler and runtime systems such as runtime type information and
 * deferred binding.
 */
public class GWT {

    public interface UncaughtExceptionHandler {
        void onUncaughtException(Throwable e);
    }

    private static final class DefaultUncaughtExceptionHandler implements UncaughtExceptionHandler {
        public void onUncaughtException(Throwable e) {
            log("Uncaught exception escaped", e);
        }
    }

    public static final String HOSTED_MODE_PERMUTATION_STRONG_NAME = "HostedMode";

    private static UncaughtExceptionHandler sUncaughtExceptionHandler = new DefaultUncaughtExceptionHandler();

    /**
     * Returns null always
     * @param <T>
     * @param classLiteral
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T create(Class<?> classLiteral) {
        Constructor<?>[] constructors = classLiteral.getConstructors();
        if (constructors != null) {
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterTypes().length == 0) {
                    try {
                        return (T) classLiteral.newInstance();
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public static String getHostPageBaseURL() {
        return "/test";
    }

    public static String getModuleBaseURL() {
        return "mock";
    }

    public static String getModuleName() {
        return "mock";
    }

    public static String getPermutationStrongName() {
        return HOSTED_MODE_PERMUTATION_STRONG_NAME;
    }

    @Deprecated
    public static String getTypeName(Object o) {
        return (o == null) ? null : o.getClass().getName();
    }

    public static UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return sUncaughtExceptionHandler;
    }

    public static String getUniqueThreadId() {
        return "";
    }

    public static String getVersion() {
        return "2.3.0";
    }

    public static boolean isClient() {
        return true;
    }

    public static boolean isProdMode() {
        return false;
    }

    public static boolean isScript() {
        return false;
    }

    public static void log(String message) {
        log(message, null);
    }

    public static void log(String message, Throwable e) {
        System.out.println(message);
        e.printStackTrace(System.out);
    }

    public static void runAsync(Class<?> name, RunAsyncCallback callback) {
        runAsyncWithoutCodeSplitting(callback);
    }

    public static void runAsync(RunAsyncCallback callback) {
        runAsyncWithoutCodeSplitting(callback);
    }

    public static void setUncaughtExceptionHandler(UncaughtExceptionHandler handler) {
        sUncaughtExceptionHandler = handler;
    }

    static void setBridge(GWTBridge bridge) {
    }

    private static void runAsyncWithoutCodeSplitting(RunAsyncCallback callback) {
        UncaughtExceptionHandler handler = sUncaughtExceptionHandler;
        if (handler == null) {
            callback.onSuccess();
        } else {
            try {
                callback.onSuccess();
            } catch (Throwable e) {
                handler.onUncaughtException(e);
            }
        }
    }
}
