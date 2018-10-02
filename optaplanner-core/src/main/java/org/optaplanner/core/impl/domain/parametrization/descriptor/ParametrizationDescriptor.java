/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.parametrization.descriptor;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.domain.parametrization.PlanningParameter;
import org.optaplanner.core.api.domain.parametrization.PlanningParametrization;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.common.ReflectionHelper;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessorFactory;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

import static org.optaplanner.core.impl.domain.common.accessor.MemberAccessorFactory.MemberAccessorType.*;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class ParametrizationDescriptor<Solution_> {

    private final SolutionDescriptor<Solution_> solutionDescriptor;

    private final Class<?> parametrizationClass;

    private final Map<String, MemberAccessor> parameterMemberAccessorMap;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public ParametrizationDescriptor(SolutionDescriptor<Solution_> solutionDescriptor, Class<?> parametrizationClass) {
        this.solutionDescriptor = solutionDescriptor;
        this.parametrizationClass = parametrizationClass;
        parameterMemberAccessorMap = new LinkedHashMap<>();
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    public void processAnnotations(DescriptorPolicy descriptorPolicy, ScoreDefinition scoreDefinition) {
        processParametrizationAnnotations(descriptorPolicy);
        ArrayList<Method> potentiallyOverwritingMethodList = new ArrayList<>();
        // Iterate inherited members too (unlike for EntityDescriptor where each one is declared)
        // to make sure each one is registered
        for (Class<?> lineageClass : ConfigUtils.getAllAnnotatedLineageClasses(parametrizationClass, PlanningParametrization.class)) {
            List<Member> memberList = ConfigUtils.getDeclaredMembers(lineageClass);
            for (Member member : memberList) {
                if (member instanceof Method && potentiallyOverwritingMethodList.stream().anyMatch(
                        m -> member.getName().equals(m.getName()) // Short cut to discard negatives faster
                                && ReflectionHelper.isMethodOverwritten((Method) member, m.getDeclaringClass()))) {
                    // Ignore member because it is an overwritten method
                    continue;
                }
                processParameterAnnotation(descriptorPolicy, member, scoreDefinition);
            }
            potentiallyOverwritingMethodList.ensureCapacity(potentiallyOverwritingMethodList.size() + memberList.size());
            memberList.stream().filter(member -> member instanceof Method)
                    .forEach(member -> potentiallyOverwritingMethodList.add((Method) member));
        }
        if (parameterMemberAccessorMap.isEmpty()) {
            throw new IllegalStateException("The parametrizationClass (" + parametrizationClass
                    + ") must have at least 1 member with a "
                    + PlanningParameter.class.getSimpleName() + " annotation.");
        }
    }

    private void processParametrizationAnnotations(DescriptorPolicy descriptorPolicy) {
        PlanningParametrization parametrizationAnnotation = parametrizationClass.getAnnotation(PlanningParametrization.class);
        if (parametrizationAnnotation == null) {
            throw new IllegalStateException("The parametrizationClass (" + parametrizationClass
                    + ") has been specified as a planning parametrization in the solver class ("
                    + solutionDescriptor.getSolutionClass() + ")," +
                    " but does not have a " + PlanningParametrization.class.getSimpleName() + " annotation.");
        }
    }

    private void processParameterAnnotation(DescriptorPolicy descriptorPolicy, Member member,
            ScoreDefinition scoreDefinition) {
        if (((AnnotatedElement) member).isAnnotationPresent(PlanningParameter.class)) {
            MemberAccessor memberAccessor = MemberAccessorFactory.buildMemberAccessor(
                    member, FIELD_OR_READ_METHOD, PlanningParameter.class);
            if (parameterMemberAccessorMap.containsKey(memberAccessor.getName())) {
                MemberAccessor duplicate = parameterMemberAccessorMap.get(memberAccessor.getName());
                throw new IllegalStateException("The parametrizationClass (" + parametrizationClass
                        + ") has a " + PlanningParameter.class.getSimpleName()
                        + " annotated member (" + memberAccessor
                        + ") that is duplicated by a member (" + duplicate + ").\n"
                        + "Maybe the annotation is defined on both the field and its getter.");
            }
            if (!scoreDefinition.getScoreClass().isAssignableFrom(memberAccessor.getType())) {
                throw new IllegalStateException("The parametrizationClass (" + parametrizationClass
                        + ") has a " + PlanningParameter.class.getSimpleName()
                        + " annotated member (" + memberAccessor
                        + ") with a return type (" + memberAccessor.getType()
                        + ") that is not assignable to the score class (" + scoreDefinition.getScoreClass() + ").\n"
                        + "Maybe make that member (" + memberAccessor.getName() + ") return the score class ("
                        + scoreDefinition.getScoreClass().getSimpleName() + ") instead.");
            }
            parameterMemberAccessorMap.put(memberAccessor.getName(), memberAccessor);
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public SolutionDescriptor<Solution_> getSolutionDescriptor() {
        return solutionDescriptor;
    }

    public Class<?> getParametrizationClass() {
        return parametrizationClass;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + parametrizationClass.getName() + ")";
    }

}
