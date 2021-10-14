/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.memorycompiler.resources;

import java.util.Collection;

/**
 * A ResourceReader provide access to resource like e.g. source code
 */
public interface ResourceReader {

    boolean isAvailable( KiePath resourcePath );
    default boolean isAvailable( String resourceName ) {
        return isAvailable(KiePath.of(resourceName));
    }

    byte[] getBytes( final KiePath resourcePath );
    default byte[] getBytes( String resourceName ) {
        return getBytes(KiePath.of(resourceName));
    }

    Collection<KiePath> getFilePaths();

    void mark();
    Collection<String> getModifiedResourcesSinceLastMark();
}
