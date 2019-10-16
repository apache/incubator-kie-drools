/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.solver.io;

import java.io.File;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.extended.FileConverter;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.config.solver.SolverConfig;

public final class XStreamConfigReader {

    /**
     * Builds the {@link XStream} setup which is used to read/write {@link SolverConfig solver configs} and benchmark configs.
     * It should never be used to read/write {@link PlanningSolution solutions}.
     * Use XStreamSolutionFileIO for that instead.
     * @return never null.
     */
    public static XStream buildXStream() {
        XStream xStream = new XStream();
        xStream.setMode(XStream.ID_REFERENCES);
        xStream.aliasSystemAttribute("xStreamId", "id");
        xStream.aliasSystemAttribute("xStreamRef", "reference");
        xStream.processAnnotations(SolverConfig.class);
        XStream.setupDefaultSecurity(xStream);
        xStream.allowTypesByRegExp(new String[]{"org\\.optaplanner\\.\\w+\\.config\\..*"});
        return xStream;
    }

    /**
     * As defined by {@link #buildXStream()}.
     * @param classLoader sometimes null, ignored if null
     * @return never null
     */
    public static XStream buildXStream(ClassLoader classLoader) {
        XStream xStream = buildXStream();
        if (classLoader != null) {
            xStream.setClassLoader(classLoader);
        }
        return xStream;
    }

    public static XStream buildXStream(ClassLoader classLoader, Class... xStreamAnnotations) {
        XStream xStream = buildXStream(classLoader);
        if (xStreamAnnotations.length > 0) {
            xStream.processAnnotations(xStreamAnnotations);
            xStream.allowTypes(xStreamAnnotations);
        }
        return xStream;
    }

    public static XStream buildXStreamPortable(ClassLoader classLoader, Class... xStreamAnnotations) {
        XStream xStream = buildXStream(classLoader, xStreamAnnotations);
        xStream.registerConverter(new FileConverter() {
            @Override
            public String toString(Object obj) {
                // Write "/" path separators (even on Windows) for portability
                String path = ((File) obj).getPath();
                return path == null ? null : path.replace('\\', '/');
            }
        });
        return xStream;
    }

    // ************************************************************************
    // Private constructor
    // ************************************************************************

    private XStreamConfigReader() {}

}
