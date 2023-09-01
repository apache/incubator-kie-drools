/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.AccumulateImportDescrBuilder;
import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.drl.ast.descr.AccumulateImportDescr;

public class AccumulateImportDescrBuilderImpl extends BaseDescrBuilderImpl<PackageDescrBuilder, AccumulateImportDescr>
    implements
    AccumulateImportDescrBuilder {

    protected AccumulateImportDescrBuilderImpl(PackageDescrBuilder parent) {
        super( parent, new AccumulateImportDescr() );
    }

    public AccumulateImportDescrBuilder target( String target ) {
        descr.setTarget( target );
        return this;
    }

    public AccumulateImportDescrBuilder functionName(String functionName) {
        descr.setFunctionName( functionName );
        return this;
    }
    
}
