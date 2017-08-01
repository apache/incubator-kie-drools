/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.executor.cdi.impl;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcludeNonCDIImplExtension implements Extension {
	
	private static final Logger logger = LoggerFactory.getLogger(ExcludeNonCDIImplExtension.class);
	
	private Set<String> excluded = new HashSet<String>();
	
	public ExcludeNonCDIImplExtension() {
		excluded.add("org.jbpm.executor.impl");
	}


    <X> void processAnnotatedType(@Observes final ProcessAnnotatedType<X> pat, BeanManager beanManager) {
        final AnnotatedType<X> annotatedType = pat.getAnnotatedType();
        final Class<X> javaClass = annotatedType.getJavaClass();
        final Package pkg = javaClass.getPackage();

  
        if (pkg != null && excluded.contains(pkg.getName())) {
        	logger.debug("jBPM Exeutor CDI integration :: excluding package {}", pkg.getName());
            pat.veto();
        } else if (javaClass != null && excluded.contains(javaClass.getName())) {
        	logger.debug("jBPM Exeutor CDI integration :: excluding class {}" + javaClass.getName());
        	pat.veto();
        }
        
        return;
    }



}
