/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.process;

import java.text.MessageFormat;
import java.util.Optional;

public class ProcessCodegenException extends RuntimeException {

    public ProcessCodegenException(String message) {
        super(message);
    }

    public ProcessCodegenException(String message, Optional<? extends Throwable> cause) {
        super(message, cause.orElse(null));
    }

    public ProcessCodegenException(String path, Throwable cause) {
        super(MessageFormat.format("Error while elaborating file \"{0}\": {1}", path, cause.getMessage()), cause);
    }

    public ProcessCodegenException(String id, String packageName, Throwable cause) {
        super(MessageFormat.format("Error while elaborating process id = \"{0}\", packageName = \"{1}\": {2}", id, packageName, cause.getMessage()), cause);
    }
}
