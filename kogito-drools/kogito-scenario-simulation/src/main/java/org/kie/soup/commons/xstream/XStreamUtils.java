/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.soup.commons.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

import static com.thoughtworks.xstream.XStream.setupDefaultSecurity;

/**
 * Temporary partial copy of XStreamUtils from kie-soup to avoid the dependency
 */
public class XStreamUtils {

    private XStreamUtils() {
        // utility static class
    }

    /**
     * Only use for XML or JSON that comes from a 100% trusted source.
     * The XML/JSON must be as safe as executable java code.
     */
    public static XStream createTrustingXStream(HierarchicalStreamDriver hierarchicalStreamDriver) {
        return internalCreateTrustingXStream( new XStream(hierarchicalStreamDriver) );
    }

    /**
     * Only use for XML or JSON that comes from a 100% trusted source.
     * The XML/JSON must be as safe as executable java code.
     */
    public static XStream createTrustingXStream(HierarchicalStreamDriver hierarchicalStreamDriver, ClassLoader classLoader) {
        return internalCreateTrustingXStream( new XStream(null, hierarchicalStreamDriver, new ClassLoaderReference( classLoader )) );
    }


    /**
     * Only use for XML or JSON that comes from a 100% trusted source.
     * The XML/JSON must be as safe as executable java code.
     */
    private static XStream internalCreateTrustingXStream( XStream xstream ) {
        setupDefaultSecurity(xstream);
        // Presumes the XML content comes from a trusted source!
        xstream.addPermission(new AnyTypePermission());
        return xstream;
    }


}
