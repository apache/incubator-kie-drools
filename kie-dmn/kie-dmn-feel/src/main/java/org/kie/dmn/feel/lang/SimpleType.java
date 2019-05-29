/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.lang;

/**
 * A simple type definition interface, i.e., a type that does not contain fields
 */
public interface SimpleType extends Type {

    public static final String LIST = "list";
    public static final String CONTEXT = "context";
    public static final String FUNCTION = "function";
    public static final String BOOLEAN = "boolean";
    public static final String YEARS_AND_MONTHS_DURATION = "years and months duration";
    public static final String DAYS_AND_TIME_DURATION = "days and time duration";
    public static final String DATE_AND_TIME = "date and time";
    public static final String TIME = "time";
    public static final String DATE = "date";
    public static final String STRING = "string";
    public static final String NUMBER = "number";
    public static final String ANY = "Any";
}
