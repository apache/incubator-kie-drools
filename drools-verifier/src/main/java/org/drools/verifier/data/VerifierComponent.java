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

package org.drools.verifier.data;

import java.util.Collection;
import java.util.Collections;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.report.components.Cause;

/** 
 */
public abstract class VerifierComponent<D extends BaseDescr>
    implements
    Comparable<VerifierComponent>,
    Cause {

    private D descr;
    
    public VerifierComponent(D descr) {
        this.descr = descr;
    }
    
    public abstract String getPath();

    public abstract VerifierComponentType getVerifierComponentType();

    public int compareTo(VerifierComponent another) {
        return this.getPath().compareTo( another.getPath() );
    }

    public Collection<Cause> getCauses() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " " + getPath();
    }

    public D getDescr() {
        return descr;
    }
}
