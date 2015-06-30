/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.core.config.domain;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;

@XStreamAlias("scanAnnotatedClasses")
public class ScanAnnotatedClassesConfig {

    @XStreamImplicit(itemFieldName = "packageInclude")
    private List<String> packageIncludeList = null;

    public List<String> getPackageIncludeList() {
        return packageIncludeList;
    }

    public void setPackageIncludeList(List<String> packageIncludeList) {
        this.packageIncludeList = packageIncludeList;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public SolutionDescriptor buildSolutionDescriptor() {
        AnnotationDB annotationDB = new AnnotationDB();
        annotationDB.setScanClassAnnotations(true);
        annotationDB.setScanFieldAnnotations(false);
        annotationDB.setScanMethodAnnotations(false);
        annotationDB.setScanParameterAnnotations(false);
        if (!ConfigUtils.isEmptyCollection(packageIncludeList)) {
            annotationDB.setScanPackages(packageIncludeList.toArray(new String[packageIncludeList.size()]));
        }
        URL[] urls = ClasspathUrlFinder.findClassPaths();
        try {
            annotationDB.scanArchives(urls);
        } catch (IOException e) {
            throw new IllegalStateException("The scanAnnotatedClasses (" + this
                    + ") could not scan for annotated classes using urls (" + urls + ").", e);
        }
        Map<String, Set<String>> annotationIndex = annotationDB.getAnnotationIndex();
        Class<? extends Solution> solutionClass = loadSolutionClass(annotationIndex);
        List<Class<?>> entityClassList = loadEntityClassList(annotationIndex);
        return SolutionDescriptor.buildSolutionDescriptor(solutionClass, entityClassList);
    }

    protected Class<? extends Solution> loadSolutionClass(Map<String, Set<String>> annotationIndex) {
        Set<String> solutionClassNameSet = annotationIndex.get(PlanningSolution.class.getName());
        if (ConfigUtils.isEmptyCollection(solutionClassNameSet)) {
            throw new IllegalStateException("The scanAnnotatedClasses (" + this
                    + ") did not find any classes with a " + PlanningSolution.class.getSimpleName()
                    + " annotation.");
        } else if (solutionClassNameSet.size() > 1) {
            throw new IllegalStateException("The scanAnnotatedClasses (" + this
                    + ") found multiple classes (" + solutionClassNameSet
                    + ") with a " + PlanningSolution.class.getSimpleName() + " annotation.");
        }
        Class<? extends Solution> solutionClass;
        String solutionClassName = solutionClassNameSet.iterator().next();
        try {
            solutionClass = (Class<? extends Solution>) Class.forName(solutionClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("The solution class (" + solutionClassName
                    + ") with a " + PlanningSolution.class.getSimpleName()
                    + " annotation could not be loaded by scanAnnotatedClasses (" + this + ").", e);
        }
        return solutionClass;
    }

    protected List<Class<?>> loadEntityClassList(Map<String, Set<String>> annotationIndex) {
        Set<String> entityClassNameSet = annotationIndex.get(PlanningEntity.class.getName());
        if (ConfigUtils.isEmptyCollection(entityClassNameSet)) {
            throw new IllegalStateException("The scanAnnotatedClasses (" + this
                    + ") did not find any classes with a " + PlanningEntity.class.getSimpleName()
                    + " annotation.");
        }
        List<Class<?>> entityClassList = new ArrayList<Class<?>>(entityClassNameSet.size());
        for (String entityClassName : entityClassNameSet) {
            Class<?> entityClass;
            try {
                entityClass = (Class<? extends Solution>) Class.forName(entityClassName);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("The entity class (" + entityClassName
                        + ") with a " + PlanningEntity.class.getSimpleName()
                        + " annotation could not be loaded by scanAnnotatedClasses (" + this + ").", e);
            }
            entityClassList.add(entityClass);
        }
        return entityClassList;
    }

    public void inherit(ScanAnnotatedClassesConfig inheritedConfig) {
        packageIncludeList = ConfigUtils.inheritMergeableListProperty(
                packageIncludeList, inheritedConfig.getPackageIncludeList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + (packageIncludeList == null ? "" : packageIncludeList) + ")";
    }

}
