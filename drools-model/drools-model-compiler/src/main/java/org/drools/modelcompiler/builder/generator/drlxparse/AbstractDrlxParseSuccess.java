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

package org.drools.modelcompiler.builder.generator.drlxparse;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractDrlxParseSuccess implements DrlxParseSuccess {

    protected Set<String> watchedProperties = Collections.emptySet();

    @Override
    public AbstractDrlxParseSuccess addAllWatchedProperties( Collection<String> watchedProperties) {
        if (watchedProperties.isEmpty()) {
            return this;
        }
        if (this.watchedProperties.isEmpty()) {
            this.watchedProperties = new HashSet<>();
        }
        this.watchedProperties.addAll(watchedProperties);
        return this;
    }

    public Set<String> getWatchedProperties() {
        return watchedProperties;
    }

    @Override
    public void accept( ParseResultVoidVisitor parseVisitor ) {
        parseVisitor.onSuccess(this);
    }

    @Override
    public <T> T acceptWithReturnValue( ParseResultVisitor<T> visitor ) {
        return visitor.onSuccess(this);
    }

    @Override
    public boolean isSuccess() {
        return true;
    }
}
