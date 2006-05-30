package org.drools.lang.descr;

/*
 * Copyright 2005 JBoss Inc
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

import java.util.ArrayList;
import java.util.List;

public class ExistsDescr extends PatternDescr
    implements
    ConditionalElementDescr {

    /**
     * 
     */
    private static final long serialVersionUID = -1617901384156245788L;
    private final List descrs = new ArrayList( 1 );

    public ExistsDescr() {
    }

    public ExistsDescr(final ColumnDescr column) {
        addDescr( column );
    }

    public void addDescr(final PatternDescr patternDescr) {
        this.descrs.add( patternDescr );
    }

    public List getDescrs() {
        return this.descrs;
    }

}