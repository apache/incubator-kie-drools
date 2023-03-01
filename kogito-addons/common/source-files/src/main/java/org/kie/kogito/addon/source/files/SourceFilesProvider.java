/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.addon.source.files;

import java.util.Collection;
import java.util.Optional;

public interface SourceFilesProvider {

    /**
     * Returns the source file that has the specified URI.
     * 
     * @param uri the URI
     * @return the source file
     */
    Optional<SourceFile> getSourceFilesByUri(String uri);

    /**
     * Returns the source files for the given processId.
     * 
     * @param processId the process identifier
     * @return the source files collection. The collection may be empty but not null.
     */
    Collection<SourceFile> getProcessSourceFiles(String processId);

    /**
     * Returns the source file for the given processId.
     *
     * @param processId the process identifier
     * @return the source file.
     */
    Optional<SourceFile> getProcessSourceFile(String processId);
}
