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

import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.RuleDescr;

/**
 *  A descriptor builder for rules
 */
public interface RuleDescrBuilder
    extends
    AnnotatedDescrBuilder<RuleDescrBuilder>,
    AttributeSupportBuilder<RuleDescrBuilder>,
    DescrBuilder<PackageDescrBuilder, RuleDescr> {

    public RuleDescrBuilder name( String name );

    public RuleDescrBuilder extendsRule( String name );

    public RuleDescrBuilder rhs( String rhs );

    public CEDescrBuilder<RuleDescrBuilder, AndDescr> lhs();

}
