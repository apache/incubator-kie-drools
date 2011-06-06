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

package org.drools.lang.api.impl;

import org.drools.lang.api.AnnotationDescrBuilder;
import org.drools.lang.api.AttributeDescrBuilder;
import org.drools.lang.api.CEDescrBuilder;
import org.drools.lang.api.RuleDescrBuilder;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.RuleDescr;

/**
 * A descr builder for Rules
 */
public class RuleDescrBuilderImpl extends BaseDescrBuilderImpl<RuleDescr>
    implements
    RuleDescrBuilder {

    protected RuleDescrBuilderImpl() {
        super( new RuleDescr() );
    }


    public AnnotationDescrBuilder newAnnotation( String name ) {
        AnnotationDescrBuilder annotation = new AnnotationDescrBuilderImpl( name );
        descr.addAnnotation( annotation.getDescr() );
        return annotation;
    }

    public AttributeDescrBuilder attribute( String name ) {
        AttributeDescrBuilder attribute = new AttributeDescrBuilderImpl( name );
        descr.addAttribute( attribute.getDescr() );
        return attribute;
    }
    
    public RuleDescrBuilder name( String name ) {
        descr.setName( name );
        return this;
    }

    public RuleDescrBuilder extendsRule( String name ) {
        descr.setParentName( name );
        return this;
    }

    public RuleDescrBuilder rhs( String rhs ) {
        descr.setConsequence( rhs );
        return this;
    }

    public CEDescrBuilder<RuleDescrBuilder, AndDescr> lhs() {
        CEDescrBuilder<RuleDescrBuilder, AndDescr> ce = new CEDescrBuilderImpl<RuleDescrBuilder, AndDescr>( this, new AndDescr() );
        descr.setLhs( ce.getDescr() );
        return ce;
    }


}
