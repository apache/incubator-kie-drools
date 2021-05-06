/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.quarkus.nativeimage;

import javax.enterprise.inject.spi.CDI;

import org.optaplanner.quarkus.gizmo.OptaPlannerGizmoBeanFactory;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(className = "org.optaplanner.core.config.util.ConfigUtils")
public final class Substitute_ConfigUtils {

    @Substitute
    public static <T> T newInstance(Object bean, String propertyName, Class<T> clazz) {
        T out = CDI.current().getBeanManager().createInstance().select(OptaPlannerGizmoBeanFactory.class)
                .get().newInstance(clazz);
        if (out != null) {
            return out;
        } else {
            throw new IllegalArgumentException("Impossible state: could not find the " + bean.getClass().getSimpleName() +
                    "'s " + propertyName + " (" + clazz.getName() + ") generated Gizmo supplier.");
        }
    }
}
