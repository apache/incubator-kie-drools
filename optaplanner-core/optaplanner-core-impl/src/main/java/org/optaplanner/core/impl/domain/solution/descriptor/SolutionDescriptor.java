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

package org.optaplanner.core.impl.domain.solution.descriptor;

import static java.util.stream.Stream.concat;
import static org.optaplanner.core.impl.domain.common.accessor.MemberAccessorFactory.MemberAccessorType.FIELD_OR_GETTER_METHOD;
import static org.optaplanner.core.impl.domain.common.accessor.MemberAccessorFactory.MemberAccessorType.FIELD_OR_READ_METHOD;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.optaplanner.core.api.domain.autodiscover.AutoDiscoverMemberType;
import org.optaplanner.core.api.domain.common.DomainAccessType;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfigurationProvider;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningEntityProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.AbstractBendableScore;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.common.ReflectionHelper;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessorFactory;
import org.optaplanner.core.impl.domain.common.accessor.ReflectionFieldMemberAccessor;
import org.optaplanner.core.impl.domain.constraintweight.descriptor.ConstraintConfigurationDescriptor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.lookup.LookUpStrategyResolver;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;
import org.optaplanner.core.impl.domain.score.descriptor.ScoreDescriptor;
import org.optaplanner.core.impl.domain.solution.cloner.FieldAccessingSolutionCloner;
import org.optaplanner.core.impl.domain.solution.cloner.gizmo.GizmoSolutionClonerFactory;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.score.definition.AbstractBendableScoreDefinition;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.util.MutableInt;
import org.optaplanner.core.impl.util.MutableLong;
import org.optaplanner.core.impl.util.MutablePair;
import org.optaplanner.core.impl.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class SolutionDescriptor<Solution_> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolutionDescriptor.class);
    private static final EntityDescriptor<?> NULL_ENTITY_DESCRIPTOR = new EntityDescriptor<>(null, PlanningEntity.class);

    public static <Solution_> SolutionDescriptor<Solution_> buildSolutionDescriptor(Class<Solution_> solutionClass,
            Class<?>... entityClasses) {
        return buildSolutionDescriptor(solutionClass, Arrays.asList(entityClasses));
    }

    public static <Solution_> SolutionDescriptor<Solution_> buildSolutionDescriptor(DomainAccessType domainAccessType,
            Class<Solution_> solutionClass,
            Class<?>... entityClasses) {
        return buildSolutionDescriptor(domainAccessType, solutionClass, null, null, Arrays.asList(entityClasses));
    }

    public static <Solution_> SolutionDescriptor<Solution_> buildSolutionDescriptor(Class<Solution_> solutionClass,
            List<Class<?>> entityClassList) {
        return buildSolutionDescriptor(DomainAccessType.REFLECTION, solutionClass, null, null, entityClassList);
    }

    public static <Solution_> SolutionDescriptor<Solution_> buildSolutionDescriptor(DomainAccessType domainAccessType,
            Class<Solution_> solutionClass, Map<String, MemberAccessor> memberAccessorMap,
            Map<String, SolutionCloner> solutionClonerMap, List<Class<?>> entityClassList) {
        memberAccessorMap = Objects.requireNonNullElse(memberAccessorMap, Collections.emptyMap());
        solutionClonerMap = Objects.requireNonNullElse(solutionClonerMap, Collections.emptyMap());
        DescriptorPolicy descriptorPolicy = new DescriptorPolicy();
        descriptorPolicy.setDomainAccessType(domainAccessType);
        descriptorPolicy.setGeneratedMemberAccessorMap(memberAccessorMap);
        descriptorPolicy.setGeneratedSolutionClonerMap(solutionClonerMap);
        SolutionDescriptor<Solution_> solutionDescriptor = new SolutionDescriptor<>(solutionClass);
        solutionDescriptor.processAnnotations(descriptorPolicy, entityClassList);
        for (Class<?> entityClass : sortEntityClassList(entityClassList)) {
            EntityDescriptor<Solution_> entityDescriptor = new EntityDescriptor<>(solutionDescriptor, entityClass);
            solutionDescriptor.addEntityDescriptor(entityDescriptor);
            entityDescriptor.processAnnotations(descriptorPolicy);
        }
        solutionDescriptor.afterAnnotationsProcessed(descriptorPolicy);
        return solutionDescriptor;
    }

    private static List<Class<?>> sortEntityClassList(List<Class<?>> entityClassList) {
        List<Class<?>> sortedEntityClassList = new ArrayList<>(entityClassList.size());
        for (Class<?> entityClass : entityClassList) {
            boolean added = false;
            for (int i = 0; i < sortedEntityClassList.size(); i++) {
                Class<?> sortedEntityClass = sortedEntityClassList.get(i);
                if (entityClass.isAssignableFrom(sortedEntityClass)) {
                    sortedEntityClassList.add(i, entityClass);
                    added = true;
                    break;
                }
            }
            if (!added) {
                sortedEntityClassList.add(entityClass);
            }
        }
        return sortedEntityClassList;
    }

    // ************************************************************************
    // Non-static members
    // ************************************************************************

    private final Class<Solution_> solutionClass;

    private DomainAccessType domainAccessType;
    private Map<String, MemberAccessor> generatedMemberAccessorMap;
    private AutoDiscoverMemberType autoDiscoverMemberType;
    private LookUpStrategyResolver lookUpStrategyResolver;

    private MemberAccessor constraintConfigurationMemberAccessor;
    private final Map<String, MemberAccessor> problemFactMemberAccessorMap = new LinkedHashMap<>();
    private final Map<String, MemberAccessor> problemFactCollectionMemberAccessorMap = new LinkedHashMap<>();
    private final Map<String, MemberAccessor> entityMemberAccessorMap = new LinkedHashMap<>();
    private final Map<String, MemberAccessor> entityCollectionMemberAccessorMap = new LinkedHashMap<>();
    private Set<Class<?>> problemFactOrEntityClassSet;
    private List<ListVariableDescriptor<Solution_>> listVariableDescriptors;
    private ScoreDescriptor scoreDescriptor;

    private ConstraintConfigurationDescriptor<Solution_> constraintConfigurationDescriptor;
    private final Map<Class<?>, EntityDescriptor<Solution_>> entityDescriptorMap = new LinkedHashMap<>();
    private final List<Class<?>> reversedEntityClassList = new ArrayList<>();
    private final ConcurrentMap<Class<?>, EntityDescriptor<Solution_>> lowestEntityDescriptorMap = new ConcurrentHashMap<>();

    private SolutionCloner<Solution_> solutionCloner;
    private boolean assertModelForCloning = false;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public SolutionDescriptor(Class<Solution_> solutionClass) {
        this.solutionClass = solutionClass;
        if (solutionClass.getPackage() == null) {
            LOGGER.warn("The solutionClass ({}) should be in a proper java package.", solutionClass);
        }
    }

    public void addEntityDescriptor(EntityDescriptor<Solution_> entityDescriptor) {
        Class<?> entityClass = entityDescriptor.getEntityClass();
        for (Class<?> otherEntityClass : entityDescriptorMap.keySet()) {
            if (entityClass.isAssignableFrom(otherEntityClass)) {
                throw new IllegalArgumentException("An earlier entityClass (" + otherEntityClass
                        + ") should not be a subclass of a later entityClass (" + entityClass
                        + "). Switch their declaration so superclasses are defined earlier.");
            }
        }
        entityDescriptorMap.put(entityClass, entityDescriptor);
        reversedEntityClassList.add(0, entityClass);
        lowestEntityDescriptorMap.put(entityClass, entityDescriptor);
    }

    public void processAnnotations(DescriptorPolicy descriptorPolicy,
            List<Class<?>> entityClassList) {
        domainAccessType = descriptorPolicy.getDomainAccessType();
        generatedMemberAccessorMap = descriptorPolicy.getGeneratedMemberAccessorMap();
        processSolutionAnnotations(descriptorPolicy);
        ArrayList<Method> potentiallyOverwritingMethodList = new ArrayList<>();
        // Iterate inherited members too (unlike for EntityDescriptor where each one is declared)
        // to make sure each one is registered
        for (Class<?> lineageClass : ConfigUtils.getAllAnnotatedLineageClasses(solutionClass, PlanningSolution.class)) {
            List<Member> memberList = ConfigUtils.getDeclaredMembers(lineageClass);
            for (Member member : memberList) {
                if (member instanceof Method && potentiallyOverwritingMethodList.stream().anyMatch(
                        m -> member.getName().equals(m.getName()) // Short cut to discard negatives faster
                                && ReflectionHelper.isMethodOverwritten((Method) member, m.getDeclaringClass()))) {
                    // Ignore member because it is an overwritten method
                    continue;
                }
                processValueRangeProviderAnnotation(descriptorPolicy, member);
                processFactEntityOrScoreAnnotation(descriptorPolicy, member, entityClassList);
            }
            potentiallyOverwritingMethodList.ensureCapacity(potentiallyOverwritingMethodList.size() + memberList.size());
            memberList.stream().filter(member -> member instanceof Method)
                    .forEach(member -> potentiallyOverwritingMethodList.add((Method) member));
        }
        if (entityCollectionMemberAccessorMap.isEmpty() && entityMemberAccessorMap.isEmpty()) {
            throw new IllegalStateException("The solutionClass (" + solutionClass
                    + ") must have at least 1 member with a "
                    + PlanningEntityCollectionProperty.class.getSimpleName() + " annotation or a "
                    + PlanningEntityProperty.class.getSimpleName() + " annotation.");
        }
        // Do not check if problemFactCollectionMemberAccessorMap and problemFactMemberAccessorMap are empty
        // because they are only required for scoreDRL and ConstraintStreams.
        if (scoreDescriptor == null) {
            throw new IllegalStateException("The solutionClass (" + solutionClass
                    + ") must have 1 member with a @" + PlanningScore.class.getSimpleName() + " annotation.\n"
                    + "Maybe add a getScore() method with a @" + PlanningScore.class.getSimpleName() + " annotation.");
        }
        if (constraintConfigurationMemberAccessor != null) {
            // The scoreDescriptor is definitely initialized at this point.
            constraintConfigurationDescriptor.processAnnotations(descriptorPolicy, scoreDescriptor.getScoreDefinition());
        }
    }

    /**
     * Only called if Drools score calculation is used.
     */
    public void assertProblemFactsExist() {
        if (problemFactCollectionMemberAccessorMap.isEmpty() && problemFactMemberAccessorMap.isEmpty()) {
            throw new IllegalStateException("The solutionClass (" + solutionClass
                    + ") must have at least 1 member with a "
                    + ProblemFactCollectionProperty.class.getSimpleName() + " annotation or a "
                    + ProblemFactProperty.class.getSimpleName() + " annotation"
                    + " when used with Drools score calculation.");
        }
    }

    private void processSolutionAnnotations(DescriptorPolicy descriptorPolicy) {
        PlanningSolution solutionAnnotation = solutionClass.getAnnotation(PlanningSolution.class);
        if (solutionAnnotation == null) {
            throw new IllegalStateException("The solutionClass (" + solutionClass
                    + ") has been specified as a solution in the configuration," +
                    " but does not have a @" + PlanningSolution.class.getSimpleName() + " annotation.");
        }
        autoDiscoverMemberType = solutionAnnotation.autoDiscoverMemberType();
        Class<? extends SolutionCloner> solutionClonerClass = solutionAnnotation.solutionCloner();
        if (solutionClonerClass != PlanningSolution.NullSolutionCloner.class) {
            solutionCloner = ConfigUtils.newInstance(this::toString, "solutionClonerClass", solutionClonerClass);
        }
        lookUpStrategyResolver =
                new LookUpStrategyResolver(descriptorPolicy.getDomainAccessType(),
                        descriptorPolicy.getGeneratedMemberAccessorMap(),
                        solutionAnnotation.lookUpStrategyType());
    }

    private void processValueRangeProviderAnnotation(DescriptorPolicy descriptorPolicy, Member member) {
        if (((AnnotatedElement) member).isAnnotationPresent(ValueRangeProvider.class)) {
            MemberAccessor memberAccessor = MemberAccessorFactory.buildMemberAccessor(member, FIELD_OR_READ_METHOD,
                    ValueRangeProvider.class, descriptorPolicy.getDomainAccessType(),
                    descriptorPolicy.getGeneratedMemberAccessorMap());
            descriptorPolicy.addFromSolutionValueRangeProvider(memberAccessor);
        }
    }

    private void processFactEntityOrScoreAnnotation(DescriptorPolicy descriptorPolicy,
            Member member,
            List<Class<?>> entityClassList) {
        Class<? extends Annotation> annotationClass = extractFactEntityOrScoreAnnotationClassOrAutoDiscover(
                member, entityClassList);
        if (annotationClass == null) {
            return;
        } else if (annotationClass.equals(ConstraintConfigurationProvider.class)) {
            processConstraintConfigurationProviderAnnotation(descriptorPolicy, member, annotationClass);
        } else if (annotationClass.equals(ProblemFactProperty.class)
                || annotationClass.equals(ProblemFactCollectionProperty.class)) {
            processProblemFactPropertyAnnotation(descriptorPolicy, member, annotationClass);
        } else if (annotationClass.equals(PlanningEntityProperty.class)
                || annotationClass.equals(PlanningEntityCollectionProperty.class)) {
            processPlanningEntityPropertyAnnotation(descriptorPolicy, member, annotationClass);
        } else if (annotationClass.equals(PlanningScore.class)) {
            if (scoreDescriptor == null) {
                // Bottom class wins. Bottom classes are parsed first due to ConfigUtil.getAllAnnotatedLineageClasses().
                scoreDescriptor = ScoreDescriptor.buildScoreDescriptor(descriptorPolicy, member, solutionClass);
            } else {
                scoreDescriptor.failFastOnDuplicateMember(descriptorPolicy, member, solutionClass);
            }
        }
    }

    private Class<? extends Annotation> extractFactEntityOrScoreAnnotationClassOrAutoDiscover(
            Member member, List<Class<?>> entityClassList) {
        Class<? extends Annotation> annotationClass = ConfigUtils.extractAnnotationClass(member,
                ConstraintConfigurationProvider.class,
                ProblemFactProperty.class,
                ProblemFactCollectionProperty.class,
                PlanningEntityProperty.class, PlanningEntityCollectionProperty.class,
                PlanningScore.class);
        if (annotationClass == null) {
            Class<?> type;
            if (autoDiscoverMemberType == AutoDiscoverMemberType.FIELD
                    && member instanceof Field) {
                Field field = (Field) member;
                type = field.getType();
            } else if (autoDiscoverMemberType == AutoDiscoverMemberType.GETTER
                    && (member instanceof Method) && ReflectionHelper.isGetterMethod((Method) member)) {
                Method method = (Method) member;
                type = method.getReturnType();
            } else {
                type = null;
            }
            if (type != null) {
                if (Score.class.isAssignableFrom(type)) {
                    annotationClass = PlanningScore.class;
                } else if (Collection.class.isAssignableFrom(type) || type.isArray()) {
                    Class<?> elementType;
                    if (Collection.class.isAssignableFrom(type)) {
                        Type genericType = (member instanceof Field) ? ((Field) member).getGenericType()
                                : ((Method) member).getGenericReturnType();
                        String memberName = member.getName();
                        if (!(genericType instanceof ParameterizedType)) {
                            throw new IllegalArgumentException("The solutionClass (" + solutionClass + ") has a "
                                    + "auto discovered member (" + memberName + ") with a member type (" + type
                                    + ") that returns a " + Collection.class.getSimpleName()
                                    + " which has no generic parameters.\n"
                                    + "Maybe the member (" + memberName + ") should return a typed "
                                    + Collection.class.getSimpleName() + ".");
                        }
                        elementType = ConfigUtils.extractCollectionGenericTypeParameterLeniently(
                                "solutionClass", solutionClass,
                                type, genericType,
                                null, member.getName());
                    } else {
                        elementType = type.getComponentType();
                    }
                    if (entityClassList.stream().anyMatch(entityClass -> entityClass.isAssignableFrom(elementType))) {
                        annotationClass = PlanningEntityCollectionProperty.class;
                    } else if (elementType.isAnnotationPresent(ConstraintConfiguration.class)) {
                        throw new IllegalStateException("The autoDiscoverMemberType (" + autoDiscoverMemberType
                                + ") cannot accept a member (" + member
                                + ") of type (" + type
                                + ") with an elementType (" + elementType
                                + ") that has a @" + ConstraintConfiguration.class.getSimpleName() + " annotation.\n"
                                + "Maybe use a member of the type (" + elementType + ") directly instead of a "
                                + Collection.class.getSimpleName() + " or array of that type.");
                    } else {
                        annotationClass = ProblemFactCollectionProperty.class;
                    }
                } else if (Map.class.isAssignableFrom(type)) {
                    throw new IllegalStateException("The autoDiscoverMemberType (" + autoDiscoverMemberType
                            + ") does not yet support the member (" + member
                            + ") of type (" + type
                            + ") which is an implementation of " + Map.class.getSimpleName() + ".");
                } else if (entityClassList.stream().anyMatch(entityClass -> entityClass.isAssignableFrom(type))) {
                    annotationClass = PlanningEntityProperty.class;
                } else if (type.isAnnotationPresent(ConstraintConfiguration.class)) {
                    annotationClass = ConstraintConfigurationProvider.class;
                } else {
                    annotationClass = ProblemFactProperty.class;
                }
            }
        }
        return annotationClass;
    }

    private void processConstraintConfigurationProviderAnnotation(
            DescriptorPolicy descriptorPolicy, Member member,
            Class<? extends Annotation> annotationClass) {
        MemberAccessor memberAccessor = MemberAccessorFactory.buildMemberAccessor(member, FIELD_OR_READ_METHOD, annotationClass,
                descriptorPolicy.getDomainAccessType(), descriptorPolicy.getGeneratedMemberAccessorMap());
        if (constraintConfigurationMemberAccessor != null) {
            if (!constraintConfigurationMemberAccessor.getName().equals(memberAccessor.getName())
                    || !constraintConfigurationMemberAccessor.getClass().equals(memberAccessor.getClass())) {
                throw new IllegalStateException("The solutionClass (" + solutionClass
                        + ") has a @" + ConstraintConfigurationProvider.class.getSimpleName()
                        + " annotated member (" + memberAccessor
                        + ") that is duplicated by another member (" + constraintConfigurationMemberAccessor + ").\n"
                        + "Maybe the annotation is defined on both the field and its getter.");
            }
            // Bottom class wins. Bottom classes are parsed first due to ConfigUtil.getAllAnnotatedLineageClasses()
            return;
        }
        assertNoFieldAndGetterDuplicationOrConflict(memberAccessor, annotationClass);
        constraintConfigurationMemberAccessor = memberAccessor;
        // Every ConstraintConfiguration is also a problem fact
        problemFactMemberAccessorMap.put(memberAccessor.getName(), memberAccessor);

        Class<?> constraintConfigurationClass = constraintConfigurationMemberAccessor.getType();
        if (!constraintConfigurationClass.isAnnotationPresent(ConstraintConfiguration.class)) {
            throw new IllegalStateException("The solutionClass (" + solutionClass
                    + ") has a @" + ConstraintConfigurationProvider.class.getSimpleName()
                    + " annotated member (" + member + ") that does not return a class ("
                    + constraintConfigurationClass + ") that has a "
                    + ConstraintConfiguration.class.getSimpleName() + " annotation.");
        }
        constraintConfigurationDescriptor = new ConstraintConfigurationDescriptor<>(this, constraintConfigurationClass);
    }

    private void processProblemFactPropertyAnnotation(DescriptorPolicy descriptorPolicy,
            Member member,
            Class<? extends Annotation> annotationClass) {
        MemberAccessor memberAccessor = MemberAccessorFactory.buildMemberAccessor(member, FIELD_OR_READ_METHOD, annotationClass,
                descriptorPolicy.getDomainAccessType(), descriptorPolicy.getGeneratedMemberAccessorMap());
        assertNoFieldAndGetterDuplicationOrConflict(memberAccessor, annotationClass);
        if (annotationClass == ProblemFactProperty.class) {
            problemFactMemberAccessorMap.put(memberAccessor.getName(), memberAccessor);
        } else if (annotationClass == ProblemFactCollectionProperty.class) {
            Class<?> type = memberAccessor.getType();
            if (!(Collection.class.isAssignableFrom(type) || type.isArray())) {
                throw new IllegalStateException("The solutionClass (" + solutionClass
                        + ") has a @" + ProblemFactCollectionProperty.class.getSimpleName()
                        + " annotated member (" + member + ") that does not return a "
                        + Collection.class.getSimpleName() + " or an array.");
            }
            problemFactCollectionMemberAccessorMap.put(memberAccessor.getName(), memberAccessor);
        } else {
            throw new IllegalStateException("Impossible situation with annotationClass (" + annotationClass + ").");
        }
    }

    private void processPlanningEntityPropertyAnnotation(DescriptorPolicy descriptorPolicy,
            Member member,
            Class<? extends Annotation> annotationClass) {
        MemberAccessor memberAccessor = MemberAccessorFactory.buildMemberAccessor(member, FIELD_OR_GETTER_METHOD,
                annotationClass, descriptorPolicy.getDomainAccessType(), descriptorPolicy.getGeneratedMemberAccessorMap());
        assertNoFieldAndGetterDuplicationOrConflict(memberAccessor, annotationClass);
        if (annotationClass == PlanningEntityProperty.class) {
            entityMemberAccessorMap.put(memberAccessor.getName(), memberAccessor);
        } else if (annotationClass == PlanningEntityCollectionProperty.class) {
            Class<?> type = memberAccessor.getType();
            if (!(Collection.class.isAssignableFrom(type) || type.isArray())) {
                throw new IllegalStateException("The solutionClass (" + solutionClass
                        + ") has a @" + PlanningEntityCollectionProperty.class.getSimpleName()
                        + " annotated member (" + member + ") that does not return a "
                        + Collection.class.getSimpleName() + " or an array.");
            }
            entityCollectionMemberAccessorMap.put(memberAccessor.getName(), memberAccessor);
        } else {
            throw new IllegalStateException("Impossible situation with annotationClass (" + annotationClass + ").");
        }
    }

    private void assertNoFieldAndGetterDuplicationOrConflict(
            MemberAccessor memberAccessor, Class<? extends Annotation> annotationClass) {
        MemberAccessor duplicate;
        Class<? extends Annotation> otherAnnotationClass;
        String memberName = memberAccessor.getName();
        if (constraintConfigurationMemberAccessor != null
                && constraintConfigurationMemberAccessor.getName().equals(memberName)) {
            duplicate = constraintConfigurationMemberAccessor;
            otherAnnotationClass = ConstraintConfigurationProvider.class;
        } else if (problemFactMemberAccessorMap.containsKey(memberName)) {
            duplicate = problemFactMemberAccessorMap.get(memberName);
            otherAnnotationClass = ProblemFactProperty.class;
        } else if (problemFactCollectionMemberAccessorMap.containsKey(memberName)) {
            duplicate = problemFactCollectionMemberAccessorMap.get(memberName);
            otherAnnotationClass = ProblemFactCollectionProperty.class;
        } else if (entityMemberAccessorMap.containsKey(memberName)) {
            duplicate = entityMemberAccessorMap.get(memberName);
            otherAnnotationClass = PlanningEntityProperty.class;
        } else if (entityCollectionMemberAccessorMap.containsKey(memberName)) {
            duplicate = entityCollectionMemberAccessorMap.get(memberName);
            otherAnnotationClass = PlanningEntityCollectionProperty.class;
        } else {
            return;
        }
        throw new IllegalStateException("The solutionClass (" + solutionClass
                + ") has a @" + annotationClass.getSimpleName()
                + " annotated member (" + memberAccessor
                + ") that is duplicated by a @" + otherAnnotationClass.getSimpleName()
                + " annotated member (" + duplicate + ").\n"
                + (annotationClass.equals(otherAnnotationClass)
                        ? "Maybe the annotation is defined on both the field and its getter."
                        : "Maybe 2 mutually exclusive annotations are configured."));
    }

    private void afterAnnotationsProcessed(DescriptorPolicy descriptorPolicy) {
        for (EntityDescriptor<Solution_> entityDescriptor : entityDescriptorMap.values()) {
            entityDescriptor.linkEntityDescriptors(descriptorPolicy);
        }
        for (EntityDescriptor<Solution_> entityDescriptor : entityDescriptorMap.values()) {
            entityDescriptor.linkVariableDescriptors(descriptorPolicy);
        }
        determineGlobalShadowOrder();
        problemFactOrEntityClassSet = collectEntityAndProblemFactClasses();
        listVariableDescriptors = findListVariableDescriptors();
        // And finally log the successful completion of processing.
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("    Model annotations parsed for solution {}:", solutionClass.getSimpleName());
            for (Map.Entry<Class<?>, EntityDescriptor<Solution_>> entry : entityDescriptorMap.entrySet()) {
                EntityDescriptor<Solution_> entityDescriptor = entry.getValue();
                LOGGER.trace("        Entity {}:", entityDescriptor.getEntityClass().getSimpleName());
                for (VariableDescriptor<Solution_> variableDescriptor : entityDescriptor.getDeclaredVariableDescriptors()) {
                    LOGGER.trace("            {} variable {} ({})",
                            variableDescriptor instanceof GenuineVariableDescriptor ? "Genuine" : "Shadow",
                            variableDescriptor.getVariableName(),
                            variableDescriptor.getMemberAccessorSpeedNote());
                }
            }
        }
        initSolutionCloner(descriptorPolicy);
    }

    private void determineGlobalShadowOrder() {
        // Topological sorting with Kahn's algorithm
        List<MutablePair<ShadowVariableDescriptor<Solution_>, Integer>> pairList = new ArrayList<>();
        Map<ShadowVariableDescriptor<Solution_>, MutablePair<ShadowVariableDescriptor<Solution_>, Integer>> shadowToPairMap =
                new HashMap<>();
        for (EntityDescriptor<Solution_> entityDescriptor : entityDescriptorMap.values()) {
            for (ShadowVariableDescriptor<Solution_> shadow : entityDescriptor.getDeclaredShadowVariableDescriptors()) {
                int sourceSize = shadow.getSourceVariableDescriptorList().size();
                MutablePair<ShadowVariableDescriptor<Solution_>, Integer> pair = MutablePair.of(shadow, sourceSize);
                pairList.add(pair);
                shadowToPairMap.put(shadow, pair);
            }
        }
        for (EntityDescriptor<Solution_> entityDescriptor : entityDescriptorMap.values()) {
            for (GenuineVariableDescriptor<Solution_> genuine : entityDescriptor.getDeclaredGenuineVariableDescriptors()) {
                for (ShadowVariableDescriptor<Solution_> sink : genuine.getSinkVariableDescriptorList()) {
                    MutablePair<ShadowVariableDescriptor<Solution_>, Integer> sinkPair = shadowToPairMap.get(sink);
                    sinkPair.setValue(sinkPair.getValue() - 1);
                }
            }
        }
        int globalShadowOrder = 0;
        while (!pairList.isEmpty()) {
            pairList.sort(Comparator.comparingInt(Pair::getValue));
            Pair<ShadowVariableDescriptor<Solution_>, Integer> pair = pairList.remove(0);
            ShadowVariableDescriptor<Solution_> shadow = pair.getKey();
            if (pair.getValue() != 0) {
                if (pair.getValue() < 0) {
                    throw new IllegalStateException("Impossible state because the shadowVariable ("
                            + shadow.getSimpleEntityAndVariableName()
                            + ") cannot be used more as a sink than it has sources.");
                }
                throw new IllegalStateException("There is a cyclic shadow variable path"
                        + " that involves the shadowVariable (" + shadow.getSimpleEntityAndVariableName()
                        + ") because it must be later than its sources (" + shadow.getSourceVariableDescriptorList()
                        + ") and also earlier than its sinks (" + shadow.getSinkVariableDescriptorList() + ").");
            }
            for (ShadowVariableDescriptor<Solution_> sink : shadow.getSinkVariableDescriptorList()) {
                MutablePair<ShadowVariableDescriptor<Solution_>, Integer> sinkPair = shadowToPairMap.get(sink);
                sinkPair.setValue(sinkPair.getValue() - 1);
            }
            shadow.setGlobalShadowOrder(globalShadowOrder);
            globalShadowOrder++;
        }
    }

    private Set<Class<?>> collectEntityAndProblemFactClasses() {
        // Figure out all problem fact or entity types that are used within this solution,
        // using the knowledge we've already gained by processing all the annotations.
        Stream<Class<?>> entityClassStream = entityDescriptorMap.keySet()
                .stream();
        Stream<Class<?>> factClassStream = problemFactMemberAccessorMap
                .values()
                .stream()
                .map(MemberAccessor::getType);
        Stream<Class<?>> problemFactOrEntityClassStream = concat(entityClassStream, factClassStream);
        Stream<Class<?>> factCollectionClassStream = problemFactCollectionMemberAccessorMap.values()
                .stream()
                .map(accessor -> ConfigUtils.extractCollectionGenericTypeParameterLeniently(
                        "solutionClass", getSolutionClass(),
                        accessor.getType(), accessor.getGenericType(), ProblemFactCollectionProperty.class,
                        accessor.getName()));
        problemFactOrEntityClassStream = concat(problemFactOrEntityClassStream, factCollectionClassStream);
        // Add constraint configuration, if configured.
        if (constraintConfigurationDescriptor != null) {
            problemFactOrEntityClassStream = concat(problemFactOrEntityClassStream,
                    Stream.of(constraintConfigurationDescriptor.getConstraintConfigurationClass()));
        }
        return problemFactOrEntityClassStream.collect(Collectors.toSet());
    }

    private List<ListVariableDescriptor<Solution_>> findListVariableDescriptors() {
        return getGenuineEntityDescriptors().stream()
                .map(EntityDescriptor::getGenuineVariableDescriptorList)
                .flatMap(Collection::stream)
                .filter(GenuineVariableDescriptor::isListVariable)
                .map(variableDescriptor -> ((ListVariableDescriptor<Solution_>) variableDescriptor))
                .collect(Collectors.toList());
    }

    private void initSolutionCloner(DescriptorPolicy descriptorPolicy) {
        solutionCloner = solutionCloner == null ? descriptorPolicy.getGeneratedSolutionClonerMap()
                .get(GizmoSolutionClonerFactory.getGeneratedClassName(this))
                : solutionCloner;
        if (solutionCloner == null) {
            switch (descriptorPolicy.getDomainAccessType()) {
                case GIZMO:
                    solutionCloner = GizmoSolutionClonerFactory.build(this);
                    break;
                case REFLECTION:
                    solutionCloner = new FieldAccessingSolutionCloner<>(this);
                    break;
                default:
                    throw new IllegalStateException("The domainAccessType (" + domainAccessType
                            + ") is not implemented.");
            }
        }
        if (assertModelForCloning) {
            // TODO https://issues.redhat.com/browse/PLANNER-2395
        }
    }

    public Class<Solution_> getSolutionClass() {
        return solutionClass;
    }

    public DomainAccessType getDomainAccessType() {
        return domainAccessType;
    }

    public Map<String, MemberAccessor> getGeneratedMemberAccessorMap() {
        return generatedMemberAccessorMap;
    }

    public ScoreDefinition getScoreDefinition() {
        return scoreDescriptor.getScoreDefinition();
    }

    public Map<String, MemberAccessor> getProblemFactMemberAccessorMap() {
        return problemFactMemberAccessorMap;
    }

    public Map<String, MemberAccessor> getProblemFactCollectionMemberAccessorMap() {
        return problemFactCollectionMemberAccessorMap;
    }

    public List<String> getProblemFactMemberAndProblemFactCollectionMemberNames() {
        List<String> memberNames = new ArrayList<>(problemFactMemberAccessorMap.size()
                + problemFactCollectionMemberAccessorMap.size());
        memberNames.addAll(problemFactMemberAccessorMap.keySet());
        memberNames.addAll(problemFactCollectionMemberAccessorMap.keySet());
        return memberNames;
    }

    public Map<String, MemberAccessor> getEntityMemberAccessorMap() {
        return entityMemberAccessorMap;
    }

    public Map<String, MemberAccessor> getEntityCollectionMemberAccessorMap() {
        return entityCollectionMemberAccessorMap;
    }

    public List<String> getEntityMemberAndEntityCollectionMemberNames() {
        List<String> memberNames = new ArrayList<>(entityMemberAccessorMap.size()
                + entityCollectionMemberAccessorMap.size());
        memberNames.addAll(entityMemberAccessorMap.keySet());
        memberNames.addAll(entityCollectionMemberAccessorMap.keySet());
        return memberNames;
    }

    public Set<Class<?>> getProblemFactOrEntityClassSet() {
        return problemFactOrEntityClassSet;
    }

    public List<ListVariableDescriptor<Solution_>> getListVariableDescriptors() {
        return listVariableDescriptors;
    }

    public SolutionCloner<Solution_> getSolutionCloner() {
        return solutionCloner;
    }

    public void setAssertModelForCloning(boolean assertModelForCloning) {
        this.assertModelForCloning = assertModelForCloning;
    }

    // ************************************************************************
    // Model methods
    // ************************************************************************

    public MemberAccessor getConstraintConfigurationMemberAccessor() {
        return constraintConfigurationMemberAccessor;
    }

    /**
     * @return sometimes null
     */
    public ConstraintConfigurationDescriptor<Solution_> getConstraintConfigurationDescriptor() {
        return constraintConfigurationDescriptor;
    }

    public Set<Class<?>> getEntityClassSet() {
        return entityDescriptorMap.keySet();
    }

    public Collection<EntityDescriptor<Solution_>> getEntityDescriptors() {
        return entityDescriptorMap.values();
    }

    public Collection<EntityDescriptor<Solution_>> getGenuineEntityDescriptors() {
        List<EntityDescriptor<Solution_>> genuineEntityDescriptorList = new ArrayList<>(entityDescriptorMap.size());
        for (EntityDescriptor<Solution_> entityDescriptor : entityDescriptorMap.values()) {
            if (entityDescriptor.hasAnyDeclaredGenuineVariableDescriptor()) {
                genuineEntityDescriptorList.add(entityDescriptor);
            }
        }
        return genuineEntityDescriptorList;
    }

    public boolean hasEntityDescriptorStrict(Class<?> entityClass) {
        return entityDescriptorMap.containsKey(entityClass);
    }

    public EntityDescriptor<Solution_> getEntityDescriptorStrict(Class<?> entityClass) {
        return entityDescriptorMap.get(entityClass);
    }

    public boolean hasEntityDescriptor(Class<?> entitySubclass) {
        EntityDescriptor<Solution_> entityDescriptor = findEntityDescriptor(entitySubclass);
        return entityDescriptor != null;
    }

    public EntityDescriptor<Solution_> findEntityDescriptorOrFail(Class<?> entitySubclass) {
        EntityDescriptor<Solution_> entityDescriptor = findEntityDescriptor(entitySubclass);
        if (entityDescriptor == null) {
            throw new IllegalArgumentException("A planning entity is an instance of a class (" + entitySubclass
                    + ") that is not configured as a planning entity class (" + getEntityClassSet() + ").\n" +
                    "If that class (" + entitySubclass.getSimpleName()
                    + ") (or superclass thereof) is not a @" + PlanningEntity.class.getSimpleName()
                    + " annotated class, maybe your @" + PlanningSolution.class.getSimpleName()
                    + " annotated class has an incorrect @" + PlanningEntityCollectionProperty.class.getSimpleName()
                    + " or @" + PlanningEntityProperty.class.getSimpleName() + " annotated member.\n"
                    + "Otherwise, if you're not using the Quarkus extension or Spring Boot starter,"
                    + " maybe that entity class (" + entitySubclass.getSimpleName()
                    + ") is missing from your solver configuration.");
        }
        return entityDescriptor;
    }

    public EntityDescriptor<Solution_> findEntityDescriptor(Class<?> entitySubclass) {
        /*
         * A slightly optimized variant of map.computeIfAbsent(...).
         * computeIfAbsent(...) would require the creation of a capturing lambda every time this method is called,
         * which is created, executed once, and immediately thrown away.
         * This is a micro-optimization, but it is valuable on the hot path.
         */
        EntityDescriptor<Solution_> cachedEntityDescriptor = lowestEntityDescriptorMap.get(entitySubclass);
        if (cachedEntityDescriptor == NULL_ENTITY_DESCRIPTOR) { // Cache hit, no descriptor found.
            return null;
        } else if (cachedEntityDescriptor != null) { // Cache hit, descriptor found.
            return cachedEntityDescriptor;
        }
        // Cache miss, look for the descriptor.
        EntityDescriptor<Solution_> newEntityDescriptor = innerFindEntityDescriptor(entitySubclass);
        if (newEntityDescriptor == null) {
            // Dummy entity descriptor value, as ConcurrentMap does not allow null values.
            lowestEntityDescriptorMap.put(entitySubclass, (EntityDescriptor<Solution_>) NULL_ENTITY_DESCRIPTOR);
            return null;
        } else {
            lowestEntityDescriptorMap.put(entitySubclass, newEntityDescriptor);
            return newEntityDescriptor;
        }
    }

    private EntityDescriptor<Solution_> innerFindEntityDescriptor(Class<?> entitySubclass) {
        // Reverse order to find the nearest ancestor
        for (Class<?> entityClass : reversedEntityClassList) {
            if (entityClass.isAssignableFrom(entitySubclass)) {
                return entityDescriptorMap.get(entityClass);
            }
        }
        return null;
    }

    public GenuineVariableDescriptor<Solution_> findGenuineVariableDescriptor(Object entity, String variableName) {
        EntityDescriptor<Solution_> entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
        return entityDescriptor.getGenuineVariableDescriptor(variableName);
    }

    public GenuineVariableDescriptor<Solution_> findGenuineVariableDescriptorOrFail(Object entity, String variableName) {
        EntityDescriptor<Solution_> entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
        GenuineVariableDescriptor<Solution_> variableDescriptor = entityDescriptor.getGenuineVariableDescriptor(variableName);
        if (variableDescriptor == null) {
            throw new IllegalArgumentException(entityDescriptor.buildInvalidVariableNameExceptionMessage(variableName));
        }
        return variableDescriptor;
    }

    public VariableDescriptor<Solution_> findVariableDescriptor(Object entity, String variableName) {
        EntityDescriptor<Solution_> entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
        return entityDescriptor.getVariableDescriptor(variableName);
    }

    public VariableDescriptor<Solution_> findVariableDescriptorOrFail(Object entity, String variableName) {
        EntityDescriptor<Solution_> entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
        VariableDescriptor<Solution_> variableDescriptor = entityDescriptor.getVariableDescriptor(variableName);
        if (variableDescriptor == null) {
            throw new IllegalArgumentException(entityDescriptor.buildInvalidVariableNameExceptionMessage(variableName));
        }
        return variableDescriptor;
    }

    // ************************************************************************
    // Look up methods
    // ************************************************************************

    public LookUpStrategyResolver getLookUpStrategyResolver() {
        return lookUpStrategyResolver;
    }

    public void validateConstraintWeight(String constraintPackage, String constraintName, Score<?> constraintWeight) {
        if (constraintWeight == null) {
            throw new IllegalArgumentException("The constraintWeight (" + constraintWeight
                    + ") for constraintPackage (" + constraintPackage
                    + ") and constraintName (" + constraintName
                    + ") must not be null.\n"
                    + (constraintConfigurationDescriptor == null ? "Maybe check your constraint implementation."
                            : "Maybe validate the data input of your constraintConfigurationClass ("
                                    + constraintConfigurationDescriptor.getConstraintConfigurationClass()
                                    + ") for that constraint (" + constraintName + ")."));
        }
        if (!scoreDescriptor.getScoreClass().isAssignableFrom(constraintWeight.getClass())) {
            throw new IllegalArgumentException("The constraintWeight (" + constraintWeight
                    + ") of class (" + constraintWeight.getClass()
                    + ") for constraintPackage (" + constraintPackage + ") and constraintName (" + constraintName
                    + ") must be of the scoreClass (" + scoreDescriptor.getScoreClass() + ").\n"
                    + (constraintConfigurationDescriptor == null ? "Maybe check your constraint implementation."
                            : "Maybe validate the data input of your constraintConfigurationClass ("
                                    + constraintConfigurationDescriptor.getConstraintConfigurationClass()
                                    + ") for that constraint (" + constraintName + ")."));
        }
        if (constraintWeight.getInitScore() != 0) {
            throw new IllegalArgumentException("The constraintWeight (" + constraintWeight
                    + ") for constraintPackage (" + constraintPackage
                    + ") and constraintName (" + constraintName
                    + ") must have an initScore (" + constraintWeight.getInitScore() + ") equal to 0.\n"
                    + (constraintConfigurationDescriptor == null ? "Maybe check your constraint implementation."
                            : "Maybe validate the data input of your constraintConfigurationClass ("
                                    + constraintConfigurationDescriptor.getConstraintConfigurationClass()
                                    + ") for that constraint (" + constraintName + ")."));
        }
        if (!((ScoreDefinition) scoreDescriptor.getScoreDefinition()).isPositiveOrZero(constraintWeight)) {
            throw new IllegalArgumentException("The constraintWeight (" + constraintWeight
                    + ") for constraintPackage (" + constraintPackage
                    + ") and constraintName (" + constraintName
                    + ") must have a positive or zero constraintWeight (" + constraintWeight + ").\n"
                    + (constraintConfigurationDescriptor == null ? "Maybe check your constraint implementation."
                            : "Maybe validate the data input of your constraintConfigurationClass ("
                                    + constraintConfigurationDescriptor.getConstraintConfigurationClass()
                                    + ") for that constraint (" + constraintName + ")."));
        }
        if (constraintWeight instanceof AbstractBendableScore) {
            AbstractBendableScore bendableConstraintWeight = (AbstractBendableScore) constraintWeight;
            AbstractBendableScoreDefinition bendableScoreDefinition =
                    (AbstractBendableScoreDefinition) scoreDescriptor.getScoreDefinition();
            if (bendableConstraintWeight.getHardLevelsSize() != bendableScoreDefinition.getHardLevelsSize()
                    || bendableConstraintWeight.getSoftLevelsSize() != bendableScoreDefinition.getSoftLevelsSize()) {
                throw new IllegalArgumentException("The bendable constraintWeight (" + constraintWeight
                        + ") for constraintPackage (" + constraintPackage
                        + ") and constraintName (" + constraintName
                        + ") has a hardLevelsSize (" + bendableConstraintWeight.getHardLevelsSize()
                        + ") or a softLevelsSize (" + bendableConstraintWeight.getSoftLevelsSize()
                        + ") that doesn't match the score definition's hardLevelsSize ("
                        + bendableScoreDefinition.getHardLevelsSize()
                        + ") or softLevelsSize (" + bendableScoreDefinition.getSoftLevelsSize() + ").\n"
                        + (constraintConfigurationDescriptor == null ? "Maybe check your constraint implementation."
                                : "Maybe validate the data input of your constraintConfigurationClass ("
                                        + constraintConfigurationDescriptor.getConstraintConfigurationClass()
                                        + ") for that constraint (" + constraintName + ")."));
            }
        }
    }

    // ************************************************************************
    // Extraction methods
    // ************************************************************************

    public void visitAllFacts(Solution_ solution, Consumer<Object> visitor) {
        // Visit entities.
        for (MemberAccessor entityMemberAccessor : entityMemberAccessorMap.values()) {
            Object entity = extractMemberObject(entityMemberAccessor, solution);
            if (entity != null) {
                visitor.accept(entity);
            }
        }
        // Visits facts.
        for (MemberAccessor accessor : problemFactMemberAccessorMap.values()) {
            Object object = extractMemberObject(accessor, solution);
            if (object != null) {
                visitor.accept(object);
            }
        }
        // Visit all entities from entity collections.
        for (MemberAccessor entityCollectionMemberAccessor : entityCollectionMemberAccessorMap.values()) {
            Collection<Object> entityCollection = extractMemberCollectionOrArray(entityCollectionMemberAccessor, solution,
                    false);
            for (Object entity : entityCollection) {
                visitor.accept(entity);
            }
        }
        // Visits problem facts from problem fact collections.
        for (MemberAccessor accessor : problemFactCollectionMemberAccessorMap.values()) {
            Collection<Object> objects = extractMemberCollectionOrArray(accessor, solution, true);
            for (Object object : objects) {
                visitor.accept(object);
            }
        }
    }

    public Collection<Object> getAllFacts(Solution_ solution) {
        List<Object> facts = new ArrayList<>();
        visitAllFacts(solution, facts::add);
        return facts;
    }

    /**
     * @param solution never null
     * @return {@code >= 0}
     */
    public int getEntityCount(Solution_ solution) {
        MutableInt entityCount = new MutableInt();
        visitAllEntities(solution,
                fact -> entityCount.increment(),
                collection -> entityCount.add(collection.size()));
        return entityCount.intValue();
    }

    public void visitAllEntities(Solution_ solution, Consumer<Object> visitor) {
        visitAllEntities(solution, visitor, collection -> collection.forEach(visitor));
    }

    private void visitAllEntities(Solution_ solution, Consumer<Object> visitor,
            Consumer<Collection<Object>> collectionVisitor) {
        for (MemberAccessor entityMemberAccessor : entityMemberAccessorMap.values()) {
            Object entity = extractMemberObject(entityMemberAccessor, solution);
            if (entity != null) {
                visitor.accept(entity);
            }
        }
        for (MemberAccessor entityCollectionMemberAccessor : entityCollectionMemberAccessorMap.values()) {
            Collection<Object> entityCollection = extractMemberCollectionOrArray(entityCollectionMemberAccessor, solution,
                    false);
            collectionVisitor.accept(entityCollection);
        }
    }

    public List<Object> getEntityListByEntityClass(Solution_ solution, Class<?> entityClass) {
        List<Object> entityList = new ArrayList<>();
        for (MemberAccessor entityMemberAccessor : entityMemberAccessorMap.values()) {
            if (entityMemberAccessor.getType().isAssignableFrom(entityClass)) {
                Object entity = extractMemberObject(entityMemberAccessor, solution);
                if (entity != null && entityClass.isInstance(entity)) {
                    entityList.add(entity);
                }
            }
        }
        for (MemberAccessor entityCollectionMemberAccessor : entityCollectionMemberAccessorMap.values()) {
            // TODO if (entityCollectionPropertyAccessor.getPropertyType().getElementType().isAssignableFrom(entityClass)) {
            Collection<Object> entityCollection = extractMemberCollectionOrArray(entityCollectionMemberAccessor, solution,
                    false);
            for (Object entity : entityCollection) {
                if (entityClass.isInstance(entity)) {
                    entityList.add(entity);
                }
            }
        }
        return entityList;
    }

    /**
     * @param scoreDirector never null
     * @return {@code >= 0}
     */
    public boolean hasMovableEntities(ScoreDirector<Solution_> scoreDirector) {
        return extractAllEntitiesStream(scoreDirector.getWorkingSolution())
                .anyMatch(entity -> findEntityDescriptorOrFail(entity.getClass()).isMovable(scoreDirector, entity));
    }

    /**
     * @param solution never null
     * @return {@code >= 0}
     */
    public long getGenuineVariableCount(Solution_ solution) {
        MutableLong result = new MutableLong();
        visitAllEntities(solution,
                entity -> result.add(findEntityDescriptorOrFail(entity.getClass()).getGenuineVariableCount()));
        return result.longValue();
    }

    public long getMaximumValueCount(Solution_ solution) {
        return extractAllEntitiesStream(solution)
                .mapToLong(entity -> findEntityDescriptorOrFail(entity.getClass()).getMaximumValueCount(solution, entity))
                .max().orElse(0L);
    }

    /**
     * @param solution never null
     * @return {@code >= 0}
     */
    public int getValueCount(Solution_ solution) {
        int valueCount = 0;
        // TODO FIXME for ValueRatioTabuSizeStrategy (or reuse maximumValueCount() for that variable descriptor?)
        throw new UnsupportedOperationException(
                "getValueCount is not yet supported - this blocks ValueRatioTabuSizeStrategy");
        // return valueCount;
    }

    /**
     * Calculates an indication on how big this problem instance is.
     * This is intentionally very loosely defined for now.
     *
     * @param solution never null
     * @return {@code >= 0}
     */
    public long getProblemScale(Solution_ solution) {
        MutableLong result = new MutableLong();
        visitAllEntities(solution,
                entity -> result.add(findEntityDescriptorOrFail(entity.getClass()).getProblemScale(solution, entity)));
        return result.longValue();
    }

    /**
     * Calculates the number of elements that need to be processed in the Construction Heuristics phase.
     * The negative value of this is the {@code initScore}. It represents how many Construction Heuristics steps need to
     * be taken before the solution is fully initialized.
     *
     * @param solution never null
     * @return {@code >= 0}
     */
    public int countUninitialized(Solution_ solution) {
        if (!listVariableDescriptors.isEmpty()) {
            return listVariableDescriptors.stream()
                    .mapToInt(variableDescriptor -> countUnassignedValues(solution, variableDescriptor))
                    .sum();
        }
        return countUninitializedVariables(solution);
    }

    private int countUninitializedVariables(Solution_ solution) {
        MutableInt result = new MutableInt();
        visitAllEntities(solution,
                entity -> result.add(findEntityDescriptorOrFail(entity.getClass()).countUninitializedVariables(entity)));
        return result.intValue();
    }

    private int countUnassignedValues(Solution_ solution, ListVariableDescriptor<Solution_> variableDescriptor) {
        long totalValueCount = variableDescriptor.getValueCount(solution, null);
        MutableInt assignedValuesCount = new MutableInt();
        visitAllEntities(solution,
                entity -> assignedValuesCount.add(variableDescriptor.getListSize(entity)));
        // TODO maybe detect duplicates and elements that are outside the value range
        return Math.toIntExact(totalValueCount - assignedValuesCount.intValue());
    }

    private Stream<Object> extractAllEntitiesStream(Solution_ solution) {
        Stream<Object> stream = Stream.empty();
        for (MemberAccessor memberAccessor : entityMemberAccessorMap.values()) {
            Object entity = extractMemberObject(memberAccessor, solution);
            if (entity != null) {
                stream = concat(stream, Stream.of(entity));
            }
        }
        for (MemberAccessor memberAccessor : entityCollectionMemberAccessorMap.values()) {
            Collection<Object> entityCollection = extractMemberCollectionOrArray(memberAccessor, solution, false);
            stream = concat(stream, entityCollection.stream());
        }
        return stream;
    }

    private Object extractMemberObject(MemberAccessor memberAccessor, Solution_ solution) {
        return memberAccessor.executeGetter(solution);
    }

    private Collection<Object> extractMemberCollectionOrArray(MemberAccessor memberAccessor, Solution_ solution,
            boolean isFact) {
        Collection<Object> collection;
        if (memberAccessor.getType().isArray()) {
            Object arrayObject = memberAccessor.executeGetter(solution);
            collection = ReflectionHelper.transformArrayToList(arrayObject);
        } else {
            collection = (Collection<Object>) memberAccessor.executeGetter(solution);
        }
        if (collection == null) {
            throw new IllegalArgumentException("The solutionClass (" + solutionClass
                    + ")'s " + (isFact ? "factCollectionProperty" : "entityCollectionProperty") + " ("
                    + memberAccessor + ") should never return null.\n"
                    + (memberAccessor instanceof ReflectionFieldMemberAccessor ? ""
                            : "Maybe the getter/method always returns null instead of the actual data.\n")
                    + "Maybe that property (" + memberAccessor.getName()
                    + ") was set with null instead of an empty collection/array when the class ("
                    + solutionClass.getSimpleName() + ") instance was created.");
        }
        return collection;
    }

    /**
     * @param solution never null
     * @return sometimes null, if the {@link Score} hasn't been calculated yet
     */
    public Score getScore(Solution_ solution) {
        return scoreDescriptor.getScore(solution);
    }

    /**
     * Called when the {@link Score} has been calculated or predicted.
     *
     * @param solution never null
     * @param score sometimes null, in rare occasions to indicate that the old {@link Score} is stale,
     *        but no new ones has been calculated
     */
    public void setScore(Solution_ solution, Score score) {
        scoreDescriptor.setScore(solution, score);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + solutionClass.getName() + ")";
    }

}
