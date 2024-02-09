/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.api.exceptions;

/**
 * <code>RuntimeException</code>s to be wrapping to <b>unchecked</b> ones at <i>customer</i> API boundaries
 */
public class KiePMMLInputDataException extends KiePMMLException {

    private static final long serialVersionUID = -6638828457762000141L;

    public KiePMMLInputDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public KiePMMLInputDataException(Throwable cause) {
        super(cause);
    }

    public KiePMMLInputDataException(String message) {
        super(message);
    }
}
