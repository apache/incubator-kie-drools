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

package org.drools.verifier.components;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.verifier.data.VerifierComponent;

public abstract class PackageComponent<D extends BaseDescr> extends VerifierComponent<D> {

    private String packageName;
    
    public PackageComponent(D descr, RulePackage rulePackage) {
        super(descr);
        setPackageName( rulePackage.getName() );
    }

    protected PackageComponent(D descr, String packageName) {
        super(descr);
        setPackageName( packageName );
    }

    public String getPackageName() {
        return packageName;
    }

    protected void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackagePath() {
        return String.format( "package[@name='%s']",
                              getPackageName() );
    }

}
