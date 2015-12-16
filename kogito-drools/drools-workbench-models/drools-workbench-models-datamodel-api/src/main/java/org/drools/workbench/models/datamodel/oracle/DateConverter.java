/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.datamodel.oracle;

import java.util.Date;

/**
 * Interface for different Date Conversion implementations.
 * <p/>
 * See @{link GWTDateConverter} and @{link JVMDateConverter}
 */
public interface DateConverter {

    /**
     * Convert a Date into a String
     * @param date
     * @return
     */
    String format( Date date );

    /**
     * Convert a String into a Date
     * @param text
     * @return
     */
    Date parse( String text );

}
