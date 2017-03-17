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

import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.core.util.Msg.Message0;
import org.kie.dmn.core.util.Msg.Message1;

public class Msg {
    public static final Message0 BKM_MISSING_EXPR = new Message0( DMNMessageType.BKM_MISSING_EXPR, "BKM missing a value expression");
    public static final Message0 BKM_MISSING_VAR = new Message0( DMNMessageType.BKM_MISSING_VAR, "BKM is missing variable or name does not matche the variable name");
    public static final Message0 CONTEXT_DUP_ENTRY = new Message0( DMNMessageType.CONTEXT_DUP_ENTRY, "Context contains duplicate context entry keys");
    public static final Message0 CONTEXT_ENTRY_NOTYPEREF = new Message0( DMNMessageType.CONTEXT_ENTRY_NOTYPEREF, "Context entry is missing typeRef");
    public static final Message0 DECISION_MISSING_EXPR = new Message0( DMNMessageType.DECISION_MISSING_EXPR, "Decision is missing a value expression");
    public static final Message0 DECISION_MISSING_VAR = new Message0( DMNMessageType.DECISION_MISSING_VAR, "Decision is missing variable or name does not match the variable name");
    public static final Message0 DRGELEM_NOT_UNIQUE = new Message0( DMNMessageType.DRGELEM_NOT_UNIQUE, "DRGElement = new Message0(DMNMessageTypeId.FEEL_PROBLEM, BKM | Decision | InputData | KnowledgeSource) name not unique in the model");
    public static final Message0 DTABLE_MULTIPLEOUT_NAME = new Message0( DMNMessageType.DTABLE_MULTIPLEOUT_NAME, "Decision table with multiple output should have output name");
    public static final Message0 DTABLE_MULTIPLEOUT_TYPEREF = new Message0( DMNMessageType.DTABLE_MULTIPLEOUT_TYPEREF, "Decision table with multiple output should have output typeRef");
    public static final Message0 DTABLE_PRIORITY_MISSING_OUTVALS = new Message0( DMNMessageType.DTABLE_PRIORITY_MISSING_OUTVALS, "Decision table with Priority as hit policy requires output to specify output values");
    public static final Message0 DTABLE_SINGLEOUT_NONAME = new Message0( DMNMessageType.DTABLE_SINGLEOUT_NONAME, "Decision table with single output should not have output name");
    public static final Message0 DTABLE_SINGLEOUT_NOTYPEREF = new Message0( DMNMessageType.DTABLE_SINGLEOUT_NOTYPEREF, "Decision table with single output should not have output typeRef");
    public static final Message0 ELEMREF_MISSING_TARGET = new Message0( DMNMessageType.ELEMREF_MISSING_TARGET, "Element reference is pointing to a unknown target");
    public static final Message0 ELEMREF_NOHASH = new Message0( DMNMessageType.ELEMREF_NOHASH, "This element 'href' reference is expected to be using an anchor  = new Message0(DMNMessageTypeId.FEEL_PROBLEM, hash sign) for pointing to a target element reference");
    public static final Message0 FAILED_VALIDATOR = new Message0( DMNMessageType.FAILED_VALIDATOR, "The Validator Was unable to compile embedded DMN validation rules, validation of the DMN Model cannot be performed.");
    public static final Message0 FAILED_XML_VALIDATION = new Message0( DMNMessageType.FAILED_XML_VALIDATION, "Failed XML validation of DMN file");
    public static final Message0 FORMAL_PARAM_DUPLICATED = new Message0( DMNMessageType.FORMAL_PARAM_DUPLICATED, "formal parameter with duplicated name");
    public static final Message0 INPUTDATA_MISSING_VAR = new Message0( DMNMessageType.INPUTDATA_MISSING_VAR, "InputData is missing variable or name does not match the variable name");
    public static final Message0 INVOCATION_INCONSISTENT_PARAM_NAMES = new Message0( DMNMessageType.INVOCATION_INCONSISTENT_PARAM_NAMES, "Invocation Binding parameter names SHALL be a subset of the formalParameters of the calledFunction");
    public static final Message0 INVOCATION_MISSING_TARGET = new Message0( DMNMessageType.INVOCATION_MISSING_TARGET, "Invocation referencing a DRGElement target not found");
    public static final Message0 INVOCATION_WRONG_PARAM_COUNT = new Message0( DMNMessageType.INVOCATION_WRONG_PARAM_COUNT, "Invocation referecing a DRGElement but number of parameters is not consistent with target");
    public static final Message0 ITEMCOMP_DUPLICATED = new Message0( DMNMessageType.ITEMCOMP_DUPLICATED, "itemComponent with duplicated name within a same parent itemDefinition");
    public static final Message0 ITEMDEF_NOT_UNIQUE = new Message0( DMNMessageType.ITEMDEF_NOT_UNIQUE, "itemDefinition name is not unique in the model");
    public static final Message0 NAME_INVALID = new Message0( DMNMessageType.NAME_INVALID, "the NamedElement attribute 'name' is not a valid FEEL name definition");
    public static final Message0 RELATION_DUP_COLUMN = new Message0( DMNMessageType.RELATION_DUP_COLUMN, "Relation contains duplicate column name");
    public static final Message0 RELATION_ROW_CELL_NOTLITERAL = new Message0( DMNMessageType.RELATION_ROW_CELL_NOTLITERAL, "Relation contains a row with a cell which is not a literalExpression");
    public static final Message0 RELATION_ROW_CELLCOUNTMISMATCH = new Message0( DMNMessageType.RELATION_ROW_CELLCOUNTMISMATCH, "Relation contains a row with wrong number of cells");
    public static final Message0 REQAUTH_NOT_KNOWLEDGESOURCE = new Message0( DMNMessageType.REQAUTH_NOT_KNOWLEDGESOURCE, "RequiredAuthority is not pointing to a KnowledgeSource");
    public static final Message0 TYPEREF_NO_FEEL_TYPE = new Message0( DMNMessageType.TYPEREF_NO_FEEL_TYPE, "This element indicates a 'typeRef' which is not a valid built-in FEEL type");
    public static final Message0 TYPEREF_NOT_FEEL_NOT_DEF = new Message0( DMNMessageType.TYPEREF_NOT_FEEL_NOT_DEF, "This element indicates a 'typeRef' which is not in FEEL namespace and not defined with itemDefinition");

    
}
