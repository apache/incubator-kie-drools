/*
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
package org.kie.kogito.codegen.process.util;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;
import org.kie.kogito.codegen.process.ProcessCodegenException;
import org.kie.kogito.codegen.process.util.calendars.AbstractCustomBusinessCalendar;
import org.kie.kogito.codegen.process.util.calendars.CustomBusinessCalendar;
import org.kie.kogito.codegen.process.util.calendars.CustomBusinessCalendarInterface;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;

import static org.assertj.core.api.Assertions.*;
import static org.kie.kogito.codegen.process.ProcessCodegen.BUSINESS_CALENDAR_PRODUCER_TEMPLATE;
import static org.kie.kogito.codegen.process.util.BusinessCalendarUtil.*;

public class BusinessCalendarUtilTest {

    private KogitoBuildContext context;

    @BeforeEach
    public void setup() {
        this.context = QuarkusKogitoBuildContext.builder().build();
    }

    @Test
    public void testValidateBusinessCalendarClass() {
        assertThatThrownBy(() -> validateBusinessCalendarClass(CustomBusinessCalendarInterface.class.getCanonicalName(), context))
                .isExactlyInstanceOf(ProcessCodegenException.class)
                .hasMessage("Custom Business Calendar class 'org.kie.kogito.codegen.process.util.calendars.CustomBusinessCalendarInterface' must be a concrete class");

        assertThatThrownBy(() -> validateBusinessCalendarClass(AbstractCustomBusinessCalendar.class.getCanonicalName(), context))
                .isExactlyInstanceOf(ProcessCodegenException.class)
                .hasMessage("Custom Business Calendar class 'org.kie.kogito.codegen.process.util.calendars.AbstractCustomBusinessCalendar' must be a concrete class");

        assertThatThrownBy(() -> validateBusinessCalendarClass("this is a totally wrong value", context))
                .isExactlyInstanceOf(ProcessCodegenException.class)
                .hasMessage("Custom Business Calendar class 'this is a totally wrong value' not found or it is not an instance of 'org.kie.kogito.calendar.BusinessCalendar'");

        assertThatThrownBy(() -> validateBusinessCalendarClass(null, context))
                .isExactlyInstanceOf(ProcessCodegenException.class)
                .hasMessage("Custom Business Calendar class cannot be null");

        assertThatThrownBy(() -> validateBusinessCalendarClass(DefaultAccessorCustomBusinessCalendar.class.getCanonicalName(), context))
                .isExactlyInstanceOf(ProcessCodegenException.class)
                .hasMessage("Custom Business Calendar class 'org.kie.kogito.codegen.process.util.DefaultAccessorCustomBusinessCalendar' must be a public class");

        assertThatNoException()
                .isThrownBy(() -> validateBusinessCalendarClass(CustomBusinessCalendar.class.getCanonicalName(), context));
    }

    @Test
    public void testGetBusinessCalendarCreationExpression() {
        ObjectCreationExpr expression = getBusinessCalendarCreationExpression(CustomBusinessCalendar.class.getCanonicalName()).asObjectCreationExpr();

        assertThat(expression)
                .isNotNull()
                .hasFieldOrPropertyWithValue("typeAsString", CustomBusinessCalendar.class.getCanonicalName());
    }

    @Test
    public void testConditionallyAddCustomBusinessCalendarWithoutCustomCalendarClassName() {
        CompilationUnit compilationUnit = getCompilationUnit();

        assertThatThrownBy(() -> conditionallyAddCustomBusinessCalendar(compilationUnit, context, null))
                .isExactlyInstanceOf(ProcessCodegenException.class)
                .hasMessage("Custom Business Calendar class cannot be null");

        // This is just to verify that the Compilation unit has not been modified by BusinessCalendarUtil
        MethodCallExpr expression = new MethodCallExpr(new MethodCallExpr(new NameExpr("BusinessCalendarImpl"), "builder"), "build");
        assertBusinessCalendarProducerConstructor(compilationUnit, expression);
    }

    @Test
    public void testConditionallyAddCustomBusinessCalendar() {
        CompilationUnit compilationUnit = getCompilationUnit();

        conditionallyAddCustomBusinessCalendar(compilationUnit, context, CustomBusinessCalendar.class.getCanonicalName());

        ObjectCreationExpr expression = getBusinessCalendarCreationExpression(CustomBusinessCalendar.class.getCanonicalName());

        assertBusinessCalendarProducerConstructor(compilationUnit, expression);
    }

    void assertBusinessCalendarProducerConstructor(CompilationUnit compilationUnit, Expression expectedInitExpression) {

        ClassOrInterfaceDeclaration producerClass = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow();

        BlockStmt constructorBody = producerClass.getDefaultConstructor()
                .orElseThrow()
                .getBody();

        List<Node> nodes = constructorBody.getStatements()
                .getFirst()
                .orElseThrow()
                .getChildNodes();

        assertThat(nodes)
                .isNotNull()
                .hasSize(1);

        Node firstNode = nodes.get(0);

        FieldAccessExpr expectedTargetExpression = new FieldAccessExpr(new ThisExpr(), BUSINESS_CALENDAR_FIELD_NAME);

        assertThat(firstNode)
                .isNotNull()
                .isInstanceOf(AssignExpr.class)
                .hasFieldOrPropertyWithValue("target", expectedTargetExpression)
                .hasFieldOrPropertyWithValue("value", expectedInitExpression);
    }

    public CompilationUnit getCompilationUnit() {
        TemplatedGenerator generator = TemplatedGenerator.builder()
                .withTemplateBasePath("/class-templates/producer/")
                .withFallbackContext(JavaKogitoBuildContext.CONTEXT_NAME)
                .withTargetTypeName(BUSINESS_CALENDAR_PRODUCER_TEMPLATE)
                .build(context, BUSINESS_CALENDAR_PRODUCER_TEMPLATE);

        return generator.compilationUnitOrThrow();
    }
}
