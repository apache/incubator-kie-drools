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
package org.kie.dmn.core.stronglytyped;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import org.drools.util.StringUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseVariantTest;
import org.kie.dmn.core.DMNRuntimeTest;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.BUILDER_DEFAULT_NOCL_TYPECHECK_TYPESAFE;
import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.KIE_API_TYPECHECK_TYPESAFE;

public class JavadocTest extends BaseVariantTest {

    public static final Logger LOG = LoggerFactory.getLogger(JavadocTest.class);

    public static Object[] params() {
        return new Object[]{BUILDER_DEFAULT_NOCL_TYPECHECK_TYPESAFE, KIE_API_TYPECHECK_TYPESAFE};
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    public void dateAndTime(VariantTestConf conf) throws Exception {
        testConfig = conf;
        final DMNRuntime runtime = createRuntime("0007-date-time.dmn", DMNRuntimeTest.class);
        runtime.addListener(DMNRuntimeUtil.createListener());

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_69430b3e-17b8-430d-b760-c505bf6469f9", "dateTime Table 58");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        // Typesafe only test
        Map<String, String> sourceMap = new HashMap<>();
        allSources.forEach((k, v) -> sourceMap.put(k.substring(k.lastIndexOf(".") + 1), v));

        String inputSet = sourceMap.get("InputSet");
        CompilationUnit cu = StaticJavaParser.parse(inputSet);
        assertJavadoc(cu, "dateTimeString", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : string }");
        assertJavadoc(cu, "Timezone", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : string }");
        assertJavadoc(cu, "oneHour", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : days and time duration }");
        assertJavadoc(cu, "Month", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "Year", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "Hours", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "timeString", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : string }");
        assertJavadoc(cu, "dateString", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : string }");
        assertJavadoc(cu, "Seconds", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "Day", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "Minutes", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "durationString", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : string }");

        String outputSet = sourceMap.get("OutputSet");
        cu = StaticJavaParser.parse(outputSet);
        assertJavadoc(cu, "Timezone", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : string }");
        assertJavadoc(cu, "Date_45Time", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : date and time }");
        assertJavadoc(cu, "Hours", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "Time", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : time }");
        assertJavadoc(cu, "Minutes", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "Date_45Time2", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : date and time }");
        assertJavadoc(cu, "years", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "dateTimeString", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : string }");
        assertJavadoc(cu, "oneHour", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : days and time duration }");
        assertJavadoc(cu, "d1seconds", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "Month", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "cDay", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "sumDurations", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : days and time duration }");
        assertJavadoc(cu, "cYear", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "cSecond", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "dateString", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : string }");
        assertJavadoc(cu, "cTimezone", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : string }");
        assertJavadoc(cu, "durationString", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : string }");
        assertJavadoc(cu, "cHour", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "Year", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "Time2", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : time }");
        assertJavadoc(cu, "timeString", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : string }");
        assertJavadoc(cu, "Time3", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : time }");
        assertJavadoc(cu, "hoursInDuration", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "dtDuration1", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : days and time duration }");
        assertJavadoc(cu, "Seconds", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "dtDuration2", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : days and time duration }");
        assertJavadoc(cu, "Day", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "cMonth", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "cMinute", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "ymDuration2", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : days and time duration }");

        String tDateVariants = sourceMap.get("TDateVariants");
        cu = StaticJavaParser.parse(tDateVariants);
        assertJavadoc(cu, "fromString", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : date }");
        assertJavadoc(cu, "fromDateTime", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : date }");
        assertJavadoc(cu, "fromYearMonthDay", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : date }");

        String tDateTimeComponents = sourceMap.get("TDateTimeComponents");
        cu = StaticJavaParser.parse(tDateTimeComponents);
        assertJavadoc(cu, "Year", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "Month", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "Day", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "Hour", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "Minute", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
        assertJavadoc(cu, "Second", "DMNType{ https://www.omg.org/spec/DMN/20230324/FEEL/ : number }");
    }

    private void assertJavadoc(CompilationUnit cu, String field, String expectedJavadocComment) {
        String lcField = StringUtils.lcFirst(field);
        Optional<FieldDeclaration> opt = cu.findFirst(FieldDeclaration.class, fd -> fd.asFieldDeclaration().getVariable(0).getNameAsString().equals(lcField));
        assertThat(opt).isPresent();
        Optional<JavadocComment> actual = opt.get().getJavadocComment();
        assertThat(actual).isPresent();
        assertThat(actual.get().getContent()).contains(expectedJavadocComment);
    }
}
