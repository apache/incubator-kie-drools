/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.verifier.report.components;

public class ReasonType {

    public static final ReasonType MISSING_VALUE = new ReasonType( "MISSING_VALUE" );
    public static final ReasonType REDUNDANT     = new ReasonType( "REDUNDANT" );
    public static final ReasonType SUBSUMPTANT   = new ReasonType( "SUBSUMPTANT" );
    public static final ReasonType ALWAYS_TRUE   = new ReasonType( "ALWAYS_TRUE" );
    public static final ReasonType ALWAYS_FALSE  = new ReasonType( "ALWAYS_FALSE" );
    public static final ReasonType INCOMPATIBLE  = new ReasonType( "INCOMPATIBLE" );
    public static final ReasonType OPPOSITY      = new ReasonType( "OPPOSITY" );

    public final String            type;

    public ReasonType(String type) {
        this.type = type;
    }
}
