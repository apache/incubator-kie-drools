/*
 * Copyright 2015 JBoss Inc
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

package org.drools.impl.adapters;

import org.kie.api.definition.type.Annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AnnotationAdapter implements org.drools.definition.type.Annotation {

    private final Annotation delegate;

    public AnnotationAdapter(Annotation delegate) {
        this.delegate = delegate;
    }

    public String getName() {
        return delegate.getName();
    }

    public Object getPropertyValue(String key) {
        return delegate.getPropertyValue(key);
    }

    public Class getPropertyType(String key) {
        return delegate.getPropertyType(key);
    }

    public static List<org.drools.definition.type.Annotation> adaptAnnotations(Collection<org.kie.api.definition.type.Annotation> annotations) {
        List<org.drools.definition.type.Annotation> result = new ArrayList<org.drools.definition.type.Annotation>();
        for (org.kie.api.definition.type.Annotation annotation : annotations) {
            result.add(new AnnotationAdapter(annotation));
        }
        return result;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AnnotationAdapter && delegate.equals(((AnnotationAdapter)obj).delegate);
    }
}
