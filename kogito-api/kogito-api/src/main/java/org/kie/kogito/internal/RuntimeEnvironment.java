/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.internal;

public enum RuntimeEnvironment {

    JDK,
    BUILDING_NATIVE,
    RUNNING_NATIVE;

    public static RuntimeEnvironment get() {
        String graalvmNativeImage = System.getProperty("org.graalvm.nativeimage.imagecode");
        if ("buildtime".equals(graalvmNativeImage)) {
            return BUILDING_NATIVE;
        }
        if ("runtime".equals(graalvmNativeImage)) {
            return RUNNING_NATIVE;
        }
        return JDK;
    }

    public static boolean isNative() {
        return !isJdk();
    }

    public static boolean isJdk() {
        return get() == JDK;
    }
}
