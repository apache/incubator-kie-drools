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
 * Enum to identify a sentence type. This is used by DRLParser and stored into
 * DroolsSentence.
 * 
 * @see DroolsSentence
 */
public enum DroolsSentenceType {
    PACKAGE, 
    UNIT,
	FUNCTION_IMPORT_STATEMENT,
    ACCUMULATE_IMPORT_STATEMENT, 
    IMPORT_STATEMENT, 
	GLOBAL, 
	FUNCTION, 
	TEMPLATE, 
	TYPE_DECLARATION, 
	RULE, 
	QUERY, 
	EVAL, 
	ENTRYPOINT_DECLARATION, 
	WINDOW_DECLARATION, 
	ENUM_DECLARATION;
}
