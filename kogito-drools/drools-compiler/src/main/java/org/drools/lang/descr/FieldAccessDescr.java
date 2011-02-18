/*
 * Copyright 2010 JBoss Inc
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

package org.drools.lang.descr;

/**
 * This represents direct field access.
 * Not a whole lot different from what you can do with the method access,
 * but in this case it is just a field by name (same as in a pattern).
 *
 *
 * eg: foo.bar
 */
public class FieldAccessDescr extends DeclarativeInvokerDescr {

    private static final long serialVersionUID = 510l;

    private String            fieldName;
    private String            argument;

    public FieldAccessDescr(final String fieldName) {
        this.fieldName = fieldName;
    }

    public FieldAccessDescr(final String fieldName,
                            final String argument) {
        this.fieldName = fieldName;
        this.argument = argument;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }

    public String getArgument() {
        return this.argument;
    }

    public void setArgument(final String argument) {
        this.argument = argument;
    }

    public String toString() {
        return this.fieldName + ((this.argument != null) ? this.argument : "");
    }

}
