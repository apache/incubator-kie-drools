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

import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.AndDescr;

/**
 *  A descriptor builder for Accumulate
 */
public interface AccumulateDescrBuilder<P extends DescrBuilder< ? >>
    extends
    DescrBuilder<AccumulateDescr> {
    
    public CEDescrBuilder<AccumulateDescrBuilder<P>, AndDescr> source(); 

    public AccumulateDescrBuilder<P> function( String name, String[] parameters);
    
    public AccumulateDescrBuilder<P> init( String block );
    public AccumulateDescrBuilder<P> action( String block );
    public AccumulateDescrBuilder<P> reverse( String block );
    public AccumulateDescrBuilder<P> result( String expr );
    
    public P end();
}
