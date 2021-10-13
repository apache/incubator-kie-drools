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

package org.kie.api.runtime.conf;


/**
 * Option to configure if the KieBase should retain a reference to the
 * KieSession or not. The default is YES, i.e., the reference is retained.
 */
public enum KeepReferenceOption implements SingleValueKieSessionOption {

    YES(true),
    NO(false);

    private static final long serialVersionUID = 510l;

    /**
     * The property name for the keep reference configuration
     */
    public static final String PROPERTY_NAME = "drools.keepReference";

    private final boolean keepReference;

    /**
     * Private constructor to enforce the use of the factory method
     * @param keepReference
     */
    private KeepReferenceOption( final boolean keepReference ) {
        this.keepReference = keepReference;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isKeepReference() {
        return keepReference;
    }

}
