/*
 * Copyright 2011 JBoss Inc
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

package org.drools.lang.api;

import org.drools.lang.descr.FunctionDescr;

/**
 *  A descriptor builder for functions
 */
public interface FunctionDescrBuilder
    extends
    DescrBuilder<FunctionDescr>,
    ParameterSupportBuilder<FunctionDescrBuilder> {

    public FunctionDescrBuilder namespace( String namespace );

    public FunctionDescrBuilder name( String name );

    public FunctionDescrBuilder returnType( String type );

    public FunctionDescrBuilder body( String body );

    public FunctionDescrBuilder dialect( String dialect );
}
