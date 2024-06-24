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
package org.kie.dmn.feel.codegen.feel11;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;

/**
 * Class used to store constant values needed for codegen
 */
public class CodegenConstants {


    // String
    public static final String ADDFIELD_S = "addField";
    public static final String ASLIST_S = "asList";
    public static final String INSTANCE_S = "INSTANCE";
    public static final String OF_S = "of";
    public static final String PARSE_S = "parse";
    public static final String PUT_S = "put";
    public static final String VAR_S = "var";

    // NameExpr
    public static final NameExpr DURATION_N = new NameExpr(Duration.class.getCanonicalName());
    public static final NameExpr LOCAL_DATE_N = new NameExpr(LocalDate.class.getCanonicalName());
    public static final NameExpr LOCAL_DATE_TIME_N = new NameExpr(LocalDateTime.class.getCanonicalName());
    public static final NameExpr LOCAL_TIME_N = new NameExpr(LocalTime.class.getCanonicalName());
    public static final NameExpr OFFSETTIME_N = new NameExpr(OffsetTime.class.getCanonicalName());
    public static final NameExpr ZONED_DATE_TIME_N = new NameExpr(ZonedDateTime.class.getCanonicalName());
    public static final NameExpr ZONE_ID_N = new NameExpr(ZoneId.class.getCanonicalName());
    public static final NameExpr ZONE_OFFSET_N = new NameExpr(ZoneOffset.class.getCanonicalName());


    // ClassOrInterfaceType
    public static final ClassOrInterfaceType BIGDECIMAL_CT = parseClassOrInterfaceType(BigDecimal.class.getCanonicalName());
    public static final ClassOrInterfaceType DURATION_CT = parseClassOrInterfaceType(Duration.class.getCanonicalName());
    public static final ClassOrInterfaceType HASHMAP_CT = parseClassOrInterfaceType(HashMap.class.getCanonicalName());
    public static final ClassOrInterfaceType ILLEGALSTATEEXCEPTION_CT = parseClassOrInterfaceType(IllegalStateException.class.getCanonicalName());
    public static final ClassOrInterfaceType INTEGER_CT = parseClassOrInterfaceType(Integer.class.getCanonicalName());
    public static final ClassOrInterfaceType LOCAL_DATE_CT = parseClassOrInterfaceType(LocalDate.class.getCanonicalName());
    public static final ClassOrInterfaceType LOCAL_DATE_TIME_CT = parseClassOrInterfaceType(LocalDateTime.class.getCanonicalName());
    public static final ClassOrInterfaceType LOCAL_TIME_CT = parseClassOrInterfaceType(LocalTime.class.getCanonicalName());
    public static final ClassOrInterfaceType OFFSETTIME_CT = parseClassOrInterfaceType(OffsetTime.class.getCanonicalName());
    public static final ClassOrInterfaceType MAP_CT = parseClassOrInterfaceType(Map.class.getCanonicalName());
    public static final ClassOrInterfaceType STRING_CT = parseClassOrInterfaceType(String.class.getCanonicalName());
    public static final ClassOrInterfaceType TEMPORALACCESSOR_CT = parseClassOrInterfaceType(TemporalAccessor.class.getCanonicalName());
    public static final ClassOrInterfaceType ZONED_DATE_TIME_CT = parseClassOrInterfaceType(ZonedDateTime.class.getCanonicalName());


    private CodegenConstants() {
    }
}
