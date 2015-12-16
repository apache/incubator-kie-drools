/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import org.drools.compiler.lang.descr.NamedConsequenceDescr;

public interface NamedConsequenceDescrBuilder<P extends DescrBuilder< ? , ? >> extends DescrBuilder<P, NamedConsequenceDescr> {

    /**
     * Sets the consequence name
     *
     * @param name the name of the consequence to be invoked
     * @return itself
     */
    public NamedConsequenceDescrBuilder<P> name( String name );

    /**
     * Sets the consequence invocation as breaking or not
     *
     * @param breaking
     * @return itself
     */
    public NamedConsequenceDescrBuilder<P> breaking( boolean breaking );
}
