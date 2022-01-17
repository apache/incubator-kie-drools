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
package org.kie.kogito.core.rules.incubation.quarkus.support.adapters;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;

import org.kie.kogito.incubation.common.ExtendedReferenceContext;
import org.kie.kogito.incubation.common.MetaDataContext;
import org.kie.kogito.incubation.common.ReferenceContext;
import org.kie.kogito.incubation.rules.RuleUnitId;
import org.kie.kogito.incubation.rules.RuleUnitIds;
import org.kie.kogito.incubation.rules.RuleUnitInstanceId;
import org.kie.kogito.incubation.rules.services.StatefulRuleUnitService;
import org.kie.kogito.incubation.rules.services.adapters.RuleUnitInstance;
import org.kie.kogito.incubation.rules.services.contexts.RuleUnitMetaDataContext;

@ApplicationScoped
public class RuleUnitInstanceProvider {
    /**
     * for an annotated field, creates the corresponding rule unit instance
     * <p>
     * e.g.
     *
     * <code><pre>
     *      &#64;Inject RuleUnitInstance<MyContext> instance;
     * </pre></code>
     */
    @Produces
    <T extends ReferenceContext> RuleUnitInstance<T> createRuleUnitInstance(
            RuleUnitIds componentRoot,
            StatefulRuleUnitService svc,
            InjectionPoint ip) {

        Type t = ip.getType();
        if (!(t instanceof ParameterizedType))
            throw new IllegalArgumentException(t + " must be a ParameterizedType");
        ParameterizedType pt = (ParameterizedType) t;

        Type[] actualTypeArguments = pt.getActualTypeArguments();
        if (actualTypeArguments.length != 1)
            throw new IllegalArgumentException(t + " must have exactly one type parameter");

        Class<?> ctxType = (Class<?>) actualTypeArguments[0];

        Instance<?> inst = CDI.current().select(ctxType);
        T ctx = (T) inst.get();

        RuleUnitId ruleUnitId = componentRoot.get(ctxType);

        MetaDataContext result = svc.create(ruleUnitId, ExtendedReferenceContext.ofData(ctx));
        RuleUnitInstanceId instanceId = result.as(RuleUnitMetaDataContext.class).id(RuleUnitInstanceId.class);

        return new QuarkusRuleUnitInstanceImpl<>(instanceId, ctx, svc);
    }

}
