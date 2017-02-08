/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.validation;

public enum Msg {
    BKM_MISSING_EXPR("BKM missing a value expression"),
    BKM_MISSING_VAR("BKM is missing variable or name does not matche the variable name"),
    CONTEXT_DUP_ENTRY("Context contains duplicate context entry keys"),
    CONTEXT_ENTRY_NOTYPEREF("Context entry is missing typeRef"),
    DECISION_MISSING_EXPR("Decision is missing a value expression"),
    DECISION_MISSING_VAR("Decision is missing variable or name does not matche the variable name"),
    DRGELEM_NOT_UNIQUE("DRGElement(BKM | Decision | InputData | KnowledgeSource) name not unique in the model"),
    DTABLE_MULTIPLEOUT_NAME("Decision table with multiple output should have output name"),
    DTABLE_MULTIPLEOUT_TYPEREF("Decision table with multiple output should have output typeRef"),
    DTABLE_PRIORITY_MISSING_OUTVALS("Decision table with Priority as hit policy requires output to specify output values"),
    DTABLE_SINGLEOUT_NONAME("Decision table with single output should not have output name"),
    DTABLE_SINGLEOUT_NOTYPEREF("Decision table with single output should not have output typeRef"),
    ELEMREF_MISSING_TARGET("Element reference is pointing to a unknown target"),
    ELEMREF_NOHASH("Element reference should point to an anchor (using hash sign) for an element to be referenced"),
    FAILED_VALIDATOR("The Validator Was unable to compile embedded DMN validation rules, validation of the DMN Model cannot be performed"),
    FAILED_XML_VALIDATION("Failed XML validation of DMN file"),
    FORMAL_PARAM_DUPLICATED("formal parameter with duplicated name"),
    INPUTDATA_MISSING_VAR("InputData is missing variable or name does not matche the variable name"),
    INVOCATION_INCONSISTENT_PARAM_NAMES("Invocation Binding parameter names SHALL be a subset of the formalParameters of the calledFunction"),
    INVOCATION_MISSING_TARGET("Invocation referencing a DRGElement target not found"),
    INVOCATION_WRONG_PARAM_COUNT("Invocation referecing a DRGElement but number of parameters is not consistent with target"),
    ITEMCOMP_DUPLICATED("itemComponent with duplicated name within a same parent itemDefinition"),
    ITEMDEF_NOT_UNIQUE("itemDefinition name is not unique in the model"),
    NAME_INVALID("the NamedElement attribute 'name' is not a valid FEEL name definition"),
    RELATION_DUP_COLUMN("Relation contains duplicate column name"),
    RELATION_ROW_CELL_NOTLITERAL("Relation contains a row with a cell which is not a literalExpression"),
    RELATION_ROW_CELLCOUNTMISMATCH("Relation contains a row with wrong number of cells"),
    REQAUTH_NOT_KNOWLEDGESOURCE("RequiredAuthority is not pointing to a KnowledgeSource"),
    TYPEREF_NO_FEEL_TYPE("typeRef is not a valid built-in FEEL type"),
    TYPEREF_NOT_FEEL_NOT_DEF("typeRef is not in FEEL namespace and not defined with itemDefinition")
    ;

    private String shortname;
    
    Msg(String shortname) {
        this.shortname = shortname;
    }

    
    public String getShortname() {
        return shortname;
    }
    
}
