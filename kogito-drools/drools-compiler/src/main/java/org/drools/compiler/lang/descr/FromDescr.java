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

package org.drools.compiler.lang.descr;

import java.util.Collections;
import java.util.List;

public class FromDescr extends PatternSourceDescr
    implements
    ConditionalElementDescr
    {

    private static final long serialVersionUID = 510l;
    private DeclarativeInvokerDescr dataSource;

    public DeclarativeInvokerDescr getDataSource() {
        return this.dataSource;
    }

    public void setDataSource(final DeclarativeInvokerDescr dataSource) {
        this.dataSource = dataSource;
    }

    public void addDescr(final BaseDescr baseDescr) {
        throw new UnsupportedOperationException("Can't add descriptors to "+this.getClass().getName());
    }
    
    public void insertBeforeLast(final Class<?> clazz ,final BaseDescr baseDescr ) {
        throw new UnsupportedOperationException( "Can't add descriptors to " + this.getClass().getName() );
    }

    public List<BaseDescr> getDescrs() {
        return Collections.emptyList();
    }

    public void addOrMerge(BaseDescr baseDescr) {
        throw new UnsupportedOperationException("Can't add descriptors to "+this.getClass().getName());
    }

    public boolean removeDescr(BaseDescr baseDescr) {
        throw new UnsupportedOperationException("Can't remove descriptors from "+this.getClass().getName());
    }

    public String toString() {
        return "from "+this.dataSource.toString();
    }
    
    @Override
    public String getText() {
        return this.toString();
    }

}
