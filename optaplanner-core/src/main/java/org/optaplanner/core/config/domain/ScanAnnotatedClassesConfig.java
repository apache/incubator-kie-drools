/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.lang3.StringUtils;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.SolverConfigContext;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.AbstractSolution;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

@XStreamAlias("scanAnnotatedClasses")
public class ScanAnnotatedClassesConfig extends AbstractConfig<ScanAnnotatedClassesConfig> {

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

    public SolutionDescriptor buildSolutionDescriptor(SolverConfigContext configContext,
            ClassLoader classLoader, ScoreDefinition deprecatedScoreDefinition) {
        ClassLoader[] classLoaders = (classLoader != null) ? new ClassLoader[]{classLoader} : new ClassLoader[0];
        if (configContext.getKieContainer() != null) {
            ReflectionsKieVfsUrlType.register(configContext.getKieContainer());
        }
        ConfigurationBuilder builder = new ConfigurationBuilder();
        if (!ConfigUtils.isEmptyCollection(packageIncludeList)) {
            FilterBuilder filterBuilder = new FilterBuilder();
            for (String packageInclude : packageIncludeList) {
                if (StringUtils.isEmpty(packageInclude)) {
                    throw new IllegalArgumentException("The scanAnnotatedClasses (" + this
                            + ") has a packageInclude (" + packageInclude
                            + ") that is empty or null. Remove it or fill it in.");
                }
                builder.addUrls(ReflectionsWorkaroundClasspathHelper.forPackage(packageInclude, classLoaders));
                filterBuilder.includePackage(packageInclude);
            }
            builder.filterInputsBy(filterBuilder);
        } else {
            builder.addUrls(ReflectionsWorkaroundClasspathHelper.forPackage("", classLoaders));
        }
        builder.setClassLoaders(classLoaders);
        Reflections reflections = new Reflections(builder);
        Class<?> solutionClass = loadSolutionClass(reflections);
        List<Class<?>> entityClassList = loadEntityClassList(reflections);
        return SolutionDescriptor.buildSolutionDescriptor(solutionClass, entityClassList, deprecatedScoreDefinition);
    }

    protected Class<?> loadSolutionClass(Reflections reflections) {
        Set<Class<?>> solutionClassSet = reflections.getTypesAnnotatedWith(PlanningSolution.class);
        retainOnlyClassesWithDeclaredAnnotation(solutionClassSet, PlanningSolution.class);
        if (solutionClassSet.contains(AbstractSolution.class)) {
            // Remove that core class to avoid a pointless fail-fast.
            // (if users have a class like this, they need to use packageIncludeList)
            solutionClassSet.remove(AbstractSolution.class);
            // Note: Another abstract solution class might be fine, if extended by an unannotated solution class.
        }
        if (ConfigUtils.isEmptyCollection(solutionClassSet)) {
            throw new IllegalStateException("The scanAnnotatedClasses (" + this
                    + ") did not find any classes with a " + PlanningSolution.class.getSimpleName()
                    + " annotation.\n"
                    + "Maybe you forgot to annotate a class with a " + PlanningSolution.class.getSimpleName()
                    + " annotation.\n"
                    + (ConfigUtils.isEmptyCollection(packageIncludeList) ? ""
                    : "Maybe the annotated class does match the packageIncludeList (" + packageIncludeList + ").\n")
                    + "Maybe you're using special classloading mechanisms (OSGi, ...) and this is a bug."
                    + " If you can confirm that, report it to our issue tracker"
                    + " and workaround it by defining the classes explicitly in the solver configuration.");
        } else if (solutionClassSet.size() > 1) {
            throw new IllegalStateException("The scanAnnotatedClasses (" + this
                    + ") found multiple classes (" + solutionClassSet
                    + ") with a " + PlanningSolution.class.getSimpleName() + " annotation.");
        }
        Class<?> solutionClass = solutionClassSet.iterator().next();
        return solutionClass;
    }

    protected List<Class<?>> loadEntityClassList(Reflections reflections) {
        Set<Class<?>> entityClassSet = reflections.getTypesAnnotatedWith(PlanningEntity.class);
        retainOnlyClassesWithDeclaredAnnotation(entityClassSet, PlanningEntity.class);
        if (ConfigUtils.isEmptyCollection(entityClassSet)) {
            throw new IllegalStateException("The scanAnnotatedClasses (" + this
                    + ") did not find any classes with a " + PlanningEntity.class.getSimpleName()
                    + " annotation.");
        }
        return new ArrayList<>(entityClassSet);
    }

    // TODO We need unit test for this: annotation scanning with TestdataUnannotatedExtendedEntity
    private void retainOnlyClassesWithDeclaredAnnotation(Set<Class<?>> classSet, Class<? extends Annotation> annotation) {
        classSet.removeIf(clazz -> !clazz.isAnnotationPresent(annotation));
    }

    @Override
    public void inherit(ScanAnnotatedClassesConfig inheritedConfig) {
        packageIncludeList = ConfigUtils.inheritMergeableListProperty(
                packageIncludeList, inheritedConfig.getPackageIncludeList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + (packageIncludeList == null ? "" : packageIncludeList) + ")";
    }

}
