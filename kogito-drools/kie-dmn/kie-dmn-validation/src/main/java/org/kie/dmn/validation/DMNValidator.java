/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.validation;

import java.io.File;
import java.io.Reader;
import java.util.List;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.model.v1_1.Definitions;

public interface DMNValidator {

    enum Validation {
        VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION
    }

    /**
     * Validate the model and return the results. This
     * is the same as invoking method
     * @{link #validate( Definitions dmnModel, Validation... options ) }
     * with option <code>Validation.VALIDATE_MODEL</code>
     *
     * @param dmnModel the model to validate
     *
     * @return returns a list of messages from the validation, or an empty
     *         list otherwise.
     */
    List<DMNMessage> validate( Definitions dmnModel );

    /**
     * Validate the model and return the results. The options field
     * defines which validations to apply. E.g.:
     *
     * <code>validate( dmnModel, VALIDATE_MODEL, VALIDATE_COMPILATION )</code>
     *
     * <b>IMPORTANT:</b> this method does not support VALIDATE_SCHEMA. In
     * order to validate the schema, please use one of the other signatures
     * of this method, like @{link #validate(Reader reader, Validation... options)}.
     *
     * @param dmnModel the model to validate
     * @param options selects which validations to apply
     *
     * @return returns a list of messages from the validation, or an empty
     *         list otherwise.
     */
    List<DMNMessage> validate( Definitions dmnModel, Validation... options );

    /**
     * Validate the model and return the results. This
     * is the same as invoking method
     * @{link #validate( File xmlFile, Validation... options )}
     * with option <code>Validation.VALIDATE_MODEL</code>
     *
     * @param xmlFile the file to validate
     *
     * @return returns a list of messages from the validation, or an empty
     *         list otherwise.
     */
    List<DMNMessage> validate( File xmlFile );

    /**
     * Validate the model and return the results. The options field
     * defines which validations to apply. E.g.:
     *
     * <code>validate( xmlFile, VALIDATE_MODEL, VALIDATE_COMPILATION )</code>
     *
     * @param xmlFile the model to validate
     * @param options selects which validations to apply
     *
     * @return returns a list of messages from the validation, or an empty
     *         list otherwise.
     */
    List<DMNMessage> validate( File xmlFile, Validation... options );

    /**
     * Validate the model and return the results. This
     * is the same as invoking method
     * @{link #validate( Reader reader, Validation... options )}
     * with option <code>Validation.VALIDATE_MODEL</code>
     *
     * @param reader a reader for the model to validate
     *
     * @return returns a list of messages from the validation, or an empty
     *         list otherwise.
     */
    List<DMNMessage> validate( Reader reader );

    /**
     * Validate the model and return the results. The options field
     * defines which validations to apply. E.g.:
     *
     * <code>validate( reader, VALIDATE_MODEL, VALIDATE_COMPILATION )</code>
     *
     * @param reader the model to validate
     * @param options selects which validations to apply
     *
     * @return returns a list of messages from the validation, or an empty
     *         list otherwise.
     */
    List<DMNMessage> validate( Reader reader, Validation... options );

    /**
     * Release all resources associated with this DMNValidator.
     */
    void dispose();
}
