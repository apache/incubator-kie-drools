/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.runtime.cdi;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivateExtension implements Extension {

    private static final Logger logger = LoggerFactory.getLogger(ActivateExtension.class);

    <X> void processAnnotatedType(@Observes final ProcessAnnotatedType<X> pat, BeanManager beanManager) {
        final AnnotatedType<X> annotatedType = pat.getAnnotatedType();
        final Class<X> javaClass = annotatedType.getJavaClass();

        if (javaClass.isAnnotationPresent(Activate.class)) {

            Activate veto = javaClass.getAnnotation(Activate.class);
            String whenAvailable = veto.whenAvailable();
            String whenNotAvailable = veto.whenNotAvailable();

            if (isNotEmpty(whenAvailable) && !isAvailable(whenAvailable, true)) {
                // veto bean in case whenAvailable is not present
                pat.veto();
            }

            if (isNotEmpty(whenNotAvailable) && isAvailable(whenNotAvailable, false)) {
                // veto bean in case whenNotAvailable is present
                pat.veto();
            }
        }
        return;
    }

    protected boolean isAvailable(String classname, boolean expected) {
        ClassLoader cl = this.getClass().getClassLoader();

        boolean result = isAvailable(classname, cl);
        if (result != expected) {
            result = isAvailable(classname, Thread.currentThread().getContextClassLoader());
        }

        return result;
    }

    protected boolean isAvailable(String classname, ClassLoader classloader) {
        logger.debug("Checking if class {} exists in classloader {}", classname, classloader);
        try {
            Class.forName(classname, true, classloader);
            logger.debug("Class {} exists", classname);
            return true;
        } catch (ClassNotFoundException e) {
            logger.debug("Class {} does NOT exists", classname);
            return false;
        }
    }

    protected boolean isNotEmpty(String value) {
        if (value != null && !value.isEmpty()) {
            return true;
        }

        return false;
    }
}
