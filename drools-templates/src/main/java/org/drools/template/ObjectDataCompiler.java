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

package org.drools.template;

import org.drools.template.objects.ObjectDataProvider;
import org.drools.template.parser.DefaultTemplateContainer;
import org.drools.template.parser.TemplateContainer;
import org.drools.template.parser.TemplateDataListener;

import java.io.InputStream;
import java.util.Collection;

/**
 * This class provides additional methods for invoking the template
 * compiler, taking the actual parameters from maps or objects.
 */
public class ObjectDataCompiler extends DataProviderCompiler {

    /**
     * Compile templates, substituting from a collection of maps or objects
     * into the given template.
     *
     * @param objs     the collection of maps or objects
     * @param template the template resource pathname
     * @return the expanded rules as a string
     */
    public String compile(final Collection<?> objs, final String template) {
        final InputStream templateStream = this.getClass().getResourceAsStream(template);
        return compile(objs, templateStream);
    }

    /**
     * Compile templates, substituting from a collection of maps or objects
     * into the given template.
     *
     * @param objs           objs the collection of maps or objects
     * @param templateStream the template as a stream
     * @return the expanded rules as a string
     */
    public String compile(final Collection<?> objs,
                          final InputStream templateStream) {
        TemplateContainer tc = new DefaultTemplateContainer(templateStream);
        closeStream(templateStream);
        return compile(new ObjectDataProvider(tc, objs),
                       new TemplateDataListener(tc));
    }
}
