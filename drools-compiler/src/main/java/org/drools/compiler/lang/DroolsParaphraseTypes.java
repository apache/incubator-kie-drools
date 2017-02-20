/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.drools.compiler.lang;

/**
 * Simple enum to identify a paraphrase type. This enum is used to better format
 * error messages during parsing.
 */
public enum DroolsParaphraseTypes {
    PACKAGE, 
    UNIT,
    IMPORT,
    FUNCTION_IMPORT, 
    ACCUMULATE_IMPORT, 
    GLOBAL, 
    FUNCTION, 
    QUERY, 
    TEMPLATE, 
    RULE, 
    RULE_ATTRIBUTE, 
    PATTERN, 
    TYPE_DECLARE, 
    EVAL, 
    ENTRYPOINT_DECLARE, 
    WINDOW_DECLARE,
	ENUM_DECLARE;

}
