/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.commons.factories;

import org.drools.compiler.lang.api.RuleDescrBuilder;
import org.kie.pmml.commons.enums.StatusCode;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.factories.KiePMMLDescrFactory.PMML4_RESULT_IDENTIFIER;
import static org.kie.pmml.commons.factories.KiePMMLDescrRulesFactory.BREAK_LABEL;
import static org.kie.pmml.commons.factories.KiePMMLDescrRulesFactory.STATUS_HOLDER;

/**
 * Class used to generate <b>Rules</b> (descr) out of a <b>Queue&lt;KiePMMLDrooledRule&gt;</b>
 */
public class KiePMMLDescrRhsFactory {

    public static final String SET_PMML4_RESULT_CODE = "\r\n" + PMML4_RESULT_IDENTIFIER + ".setResultCode(\"%s\");";
    public static final String ADD_PMML4_RESULT_VARIABLE = "\r\n" + PMML4_RESULT_IDENTIFIER + ".addResultVariable(" + PMML4_RESULT_IDENTIFIER + ".getResultObjectName()" + ", \"%s\");";

    public static final String UPDATE_STATUS_HOLDER = "\r\n" + STATUS_HOLDER + ".setStatus(\"%s\");\r\nupdate(" + STATUS_HOLDER + ");";
    public static final String FOCUS_AGENDA_GROUP = "\r\nkcontext.getKieRuntime().getAgenda().getAgendaGroup( \"%s\" ).setFocus();";

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLDescrRhsFactory.class.getName());

    final RuleDescrBuilder builder;

    private KiePMMLDescrRhsFactory(final RuleDescrBuilder builder) {
        this.builder = builder;
    }

    public static KiePMMLDescrRhsFactory factory(final RuleDescrBuilder builder) {
        return new KiePMMLDescrRhsFactory(builder);
    }

    public void declareRhs(final KiePMMLDrooledRule rule) {
        logger.info("declareRhs {}", rule);
        if (rule.getIfBreakField() != null) {
            declareIfThen(rule);
        } else {
            declareDefaultThen(rule);
        }
    }

    protected void declareDefaultThen(final KiePMMLDrooledRule rule) {
        StringBuilder rhsBuilder = new StringBuilder();
        if (rule.getStatusToSet() != null) {
            rhsBuilder.append(String.format(UPDATE_STATUS_HOLDER, rule.getStatusToSet()));
        }
        if (rule.getResultCode() != null) {
            rhsBuilder.append(String.format(SET_PMML4_RESULT_CODE, rule.getResultCode()));
        }
        if (rule.getResult() != null) {
            rhsBuilder.append(String.format(ADD_PMML4_RESULT_VARIABLE, rule.getResult()));
        }
        if (rule.getFocusedAgendaGroup() != null) {
            rhsBuilder.append(String.format(FOCUS_AGENDA_GROUP, rule.getFocusedAgendaGroup()));
        }
        builder.rhs(rhsBuilder.toString());
    }

    protected void declareIfThen(final KiePMMLDrooledRule rule) {
        builder.rhs(String.format(UPDATE_STATUS_HOLDER, rule.getStatusToSet()));
        StringBuilder rhsBuilder = new StringBuilder();
        rhsBuilder.append(String.format(UPDATE_STATUS_HOLDER, StatusCode.DONE.getName()));
        if (rule.getResultCode() != null) {
            rhsBuilder.append(String.format(SET_PMML4_RESULT_CODE, rule.getResultCode()));
        }
        if (rule.getResult() != null) {
            rhsBuilder.append(String.format(ADD_PMML4_RESULT_VARIABLE, rule.getResult()));
        }
        if (rule.getFocusedAgendaGroup() != null) {
            rhsBuilder.append(String.format(FOCUS_AGENDA_GROUP, rule.getFocusedAgendaGroup()));
        }
        builder.namedRhs(BREAK_LABEL, rhsBuilder.toString());
    }

}
