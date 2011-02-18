/**
 * Copyright 2010 JBoss Inc
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

package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class VerifierComponentType
    implements
    Comparable<VerifierComponentType> {
    public static final VerifierComponentType UNKNOWN                  = new VerifierComponentType( "unknown" );
    public static final VerifierComponentType FIELD                    = new VerifierComponentType( "field" );
    public static final VerifierComponentType RULE                     = new VerifierComponentType( "rule" );
    public static final VerifierComponentType CONSTRAINT               = new VerifierComponentType( "constraint" );
    public static final VerifierComponentType VARIABLE                 = new VerifierComponentType( "variable" );
    public static final VerifierComponentType PATTERN                  = new VerifierComponentType( "pattern" );
    public static final VerifierComponentType SUB_PATTERN              = new VerifierComponentType( "subPattern" );
    public static final VerifierComponentType SUB_RULE                 = new VerifierComponentType( "subRule" );
    public static final VerifierComponentType RESTRICTION              = new VerifierComponentType( "restriction" );
    public static final VerifierComponentType OPERATOR                 = new VerifierComponentType( "operator" );
    public static final VerifierComponentType FIELD_OBJECT_TYPE_LINK   = new VerifierComponentType( "fieldClassLink" );
    public static final VerifierComponentType COLLECT                  = new VerifierComponentType( "collect" );
    public static final VerifierComponentType ACCUMULATE               = new VerifierComponentType( "accumulate" );
    public static final VerifierComponentType FROM                     = new VerifierComponentType( "from" );
    public static final VerifierComponentType EVAL                     = new VerifierComponentType( "eval" );
    public static final VerifierComponentType PREDICATE                = new VerifierComponentType( "predicate" );
    public static final VerifierComponentType METHOD_ACCESSOR          = new VerifierComponentType( "method_accessor" );
    public static final VerifierComponentType FIELD_ACCESSOR           = new VerifierComponentType( "fieldAccessor" );
    public static final VerifierComponentType FUNCTION_CALL            = new VerifierComponentType( "functionCall" );
    public static final VerifierComponentType ACCESSOR                 = new VerifierComponentType( "accessor" );
    public static final VerifierComponentType RULE_PACKAGE             = new VerifierComponentType( "rulePackage" );
    public static final VerifierComponentType CONSEQUENCE              = new VerifierComponentType( "consequence" );
    public static final VerifierComponentType OBJECT_TYPE              = new VerifierComponentType( "objectType" );
    public static final VerifierComponentType INLINE_EVAL_DESCR        = new VerifierComponentType( "inlineEvalDescr" );
    public static final VerifierComponentType RETURN_VALUE_FIELD_DESCR = new VerifierComponentType( "returnValueFieldDescr" );
    public static final VerifierComponentType ENTRY_POINT_DESCR        = new VerifierComponentType( "entryPointDescr" );
    public static final VerifierComponentType WORKING_MEMORY           = new VerifierComponentType( "workingMemory" );
    public static final VerifierComponentType IMPORT                   = new VerifierComponentType( "import" );

    private final String                      type;

    public VerifierComponentType(String t) {
        type = t;
    }

    public int compareTo(VerifierComponentType another) {
        return getType().compareTo( another.getType() );
    }

    public String getType() {
        return type;
    }

    public String toString() {
        return "VerifierComponentType." + type;
    }
}
