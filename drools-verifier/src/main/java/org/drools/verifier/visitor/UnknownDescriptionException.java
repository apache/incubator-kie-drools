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

package org.drools.verifier.visitor;

import org.drools.compiler.lang.descr.BaseDescr;

/**
 * This exception is thrown when verifier tries to handle a descr that it is not
 * familiar with.
 */
public class UnknownDescriptionException extends Exception {
    private static final long serialVersionUID = 510l;

    final BaseDescr           descr;

    public UnknownDescriptionException(BaseDescr descr) {
        super( "Descr ( " + descr.getClass() + " ) is unknown to drools verifier." );
        this.descr = descr;
    }

}
