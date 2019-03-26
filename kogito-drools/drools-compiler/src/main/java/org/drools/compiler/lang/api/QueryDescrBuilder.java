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

package org.drools.compiler.lang.api;

import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.QueryDescr;

/**
 *  A descriptor builder for queries
 */
public interface QueryDescrBuilder
    extends
    AnnotatedDescrBuilder<QueryDescrBuilder>,
    ParameterSupportBuilder<QueryDescrBuilder>,
    DescrBuilder<PackageDescrBuilder, QueryDescr> {

    public QueryDescrBuilder name( String name );

    public CEDescrBuilder<QueryDescrBuilder, AndDescr> lhs();

}
