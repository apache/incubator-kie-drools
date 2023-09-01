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
package org.drools.mvel.parser.ast.visitor;

import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.CompactConstructorDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.ReceiverParameter;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.PatternExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.expr.SwitchExpr;
import com.github.javaparser.ast.expr.TextBlockLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.modules.ModuleDeclaration;
import com.github.javaparser.ast.modules.ModuleExportsDirective;
import com.github.javaparser.ast.modules.ModuleOpensDirective;
import com.github.javaparser.ast.modules.ModuleProvidesDirective;
import com.github.javaparser.ast.modules.ModuleRequiresDirective;
import com.github.javaparser.ast.modules.ModuleUsesDirective;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.LabeledStmt;
import com.github.javaparser.ast.stmt.LocalClassDeclarationStmt;
import com.github.javaparser.ast.stmt.LocalRecordDeclarationStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.UnparsableStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.stmt.YieldStmt;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.IntersectionType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.type.UnionType;
import com.github.javaparser.ast.type.UnknownType;
import com.github.javaparser.ast.type.VarType;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.type.WildcardType;
import com.github.javaparser.ast.visitor.VoidVisitor;
import org.drools.mvel.parser.ast.expr.*;

public interface DrlVoidVisitor<A> extends VoidVisitor<A> {

    default void visit(RuleDeclaration ruleDeclaration, A arg) { }

    default void visit(RuleBody ruleBody, A arg) { }

    default void visit(RulePattern rulePattern, A arg) { }

    default void visit(RuleJoinedPatterns joinedPatterns, A arg) { }


    default void visit(DrlxExpression expr, A arg) { }

    default void visit(OOPathExpr expr, A arg) { }

    default void visit(OOPathChunk chunk, A arg) { }

    default void visit(RuleConsequence ruleConsequence, A arg) { }

    default void visit(InlineCastExpr inlineCastExpr, A arg) { }

    default void visit(FullyQualifiedInlineCastExpr inlineCastExpr, A arg) { }

    default void visit(NullSafeFieldAccessExpr nullSafeFieldAccessExpr, A arg) { }

    default void visit(NullSafeMethodCallExpr nullSafeMethodCallExpr, A arg) { }

    default void visit(PointFreeExpr pointFreeExpr, A arg) { }

    default void visit(TemporalLiteralExpr temporalLiteralExpr, A arg) { }

    default void visit(TemporalLiteralChunkExpr temporalLiteralChunkExpr, A arg) {}

    default void visit(HalfBinaryExpr n, A arg) {}

    default void visit(HalfPointFreeExpr n, A arg) {}

    default void visit(BigDecimalLiteralExpr bigDecimalLiteralExpr, A arg) {}

    default void visit(BigIntegerLiteralExpr bigIntegerLiteralExpr, A arg) {}

    default void visit(TemporalLiteralInfiniteChunkExpr temporalLiteralInfiniteChunkExpr, A arg) { }

    default void visit(DrlNameExpr drlNameExpr, A arg) { }

    default void visit(ModifyStatement modifyExpression, A arg) { }

    @Override
    default void visit(NodeList n, A arg) {

    }

    @Override
    default void visit(AnnotationDeclaration n, A arg) {

    }

    @Override
    default void visit(AnnotationMemberDeclaration n, A arg) {

    }

    @Override
    default void visit(ArrayAccessExpr n, A arg) {

    }

    @Override
    default void visit(ArrayCreationExpr n, A arg) {

    }

    @Override
    default void visit(ArrayCreationLevel n, A arg) {

    }

    @Override
    default void visit(ArrayInitializerExpr n, A arg) {

    }

    @Override
    default void visit(ArrayType n, A arg) {

    }

    @Override
    default void visit(AssertStmt n, A arg) {

    }

    @Override
    default void visit(AssignExpr n, A arg) {

    }

    @Override
    default void visit(BinaryExpr n, A arg) {

    }

    @Override
    default void visit(BlockComment n, A arg) {

    }

    @Override
    default void visit(BlockStmt n, A arg) {

    }

    @Override
    default void visit(BooleanLiteralExpr n, A arg) {

    }

    @Override
    default void visit(BreakStmt n, A arg) {

    }

    @Override
    default void visit(CastExpr n, A arg) {

    }

    @Override
    default void visit(CatchClause n, A arg) {

    }

    @Override
    default void visit(CharLiteralExpr n, A arg) {

    }

    @Override
    default void visit(ClassExpr n, A arg) {

    }

    @Override
    default void visit(ClassOrInterfaceDeclaration n, A arg) {

    }

    @Override
    default void visit(ClassOrInterfaceType n, A arg) {

    }

    @Override
    default void visit(CompilationUnit n, A arg) {

    }

    @Override
    default void visit(ConditionalExpr n, A arg) {

    }

    @Override
    default void visit(ConstructorDeclaration n, A arg) {

    }

    @Override
    default void visit(ContinueStmt n, A arg) {

    }

    @Override
    default void visit(DoStmt n, A arg) {

    }

    @Override
    default void visit(DoubleLiteralExpr n, A arg) {

    }

    @Override
    default void visit(EmptyStmt n, A arg) {

    }

    @Override
    default void visit(EnclosedExpr n, A arg) {

    }

    @Override
    default void visit(EnumConstantDeclaration n, A arg) {

    }

    @Override
    default void visit(EnumDeclaration n, A arg) {

    }

    @Override
    default void visit(ExplicitConstructorInvocationStmt n, A arg) {

    }

    @Override
    default void visit(ExpressionStmt n, A arg) {

    }

    @Override
    default void visit(FieldAccessExpr n, A arg) {

    }

    @Override
    default void visit(FieldDeclaration n, A arg) {

    }

    @Override
    default void visit(ForStmt n, A arg) {

    }

    @Override
    default void visit(ForEachStmt n, A arg) {

    }

    @Override
    default void visit(IfStmt n, A arg) {

    }

    @Override
    default void visit(ImportDeclaration n, A arg) {

    }

    @Override
    default void visit(InitializerDeclaration n, A arg) {

    }

    @Override
    default void visit(InstanceOfExpr n, A arg) {

    }

    @Override
    default void visit(IntegerLiteralExpr n, A arg) {

    }

    @Override
    default void visit(IntersectionType n, A arg) {

    }

    @Override
    default void visit(JavadocComment n, A arg) {

    }

    @Override
    default void visit(LabeledStmt n, A arg) {

    }

    @Override
    default void visit(LambdaExpr n, A arg) {

    }

    @Override
    default void visit(LineComment n, A arg) {

    }

    @Override
    default void visit(LocalClassDeclarationStmt n, A arg) {

    }

    @Override
    default void visit(LongLiteralExpr n, A arg) {

    }

    @Override
    default void visit(MarkerAnnotationExpr n, A arg) {

    }

    @Override
    default void visit(MemberValuePair n, A arg) {

    }

    @Override
    default void visit(MethodCallExpr n, A arg) {

    }

    @Override
    default void visit(MethodDeclaration n, A arg) {

    }

    @Override
    default void visit(MethodReferenceExpr n, A arg) {

    }

    @Override
    default void visit(NameExpr n, A arg) {

    }

    @Override
    default void visit(Name n, A arg) {

    }

    @Override
    default void visit(NormalAnnotationExpr n, A arg) {

    }

    @Override
    default void visit(NullLiteralExpr n, A arg) {

    }

    @Override
    default void visit(ObjectCreationExpr n, A arg) {

    }

    @Override
    default void visit(PackageDeclaration n, A arg) {

    }

    @Override
    default void visit(Parameter n, A arg) {

    }

    @Override
    default void visit(PrimitiveType n, A arg) {

    }

    @Override
    default void visit(ReturnStmt n, A arg) {

    }

    @Override
    default void visit(SimpleName n, A arg) {

    }

    @Override
    default void visit(SingleMemberAnnotationExpr n, A arg) {

    }

    @Override
    default void visit(StringLiteralExpr n, A arg) {

    }

    @Override
    default void visit(SuperExpr n, A arg) {

    }

    @Override
    default void visit(SwitchEntry n, A arg) {

    }

    @Override
    default void visit(SwitchStmt n, A arg) {

    }

    @Override
    default void visit(SynchronizedStmt n, A arg) {

    }

    @Override
    default void visit(ThisExpr n, A arg) {

    }

    @Override
    default void visit(ThrowStmt n, A arg) {

    }

    @Override
    default void visit(TryStmt n, A arg) {

    }

    @Override
    default void visit(TypeExpr n, A arg) {

    }

    @Override
    default void visit(TypeParameter n, A arg) {

    }

    @Override
    default void visit(UnaryExpr n, A arg) {

    }

    @Override
    default void visit(UnionType n, A arg) {

    }

    @Override
    default void visit(UnknownType n, A arg) {

    }

    @Override
    default void visit(VariableDeclarationExpr n, A arg) {

    }

    @Override
    default void visit(VariableDeclarator n, A arg) {

    }

    @Override
    default void visit(VoidType n, A arg) {

    }

    @Override
    default void visit(WhileStmt n, A arg) {

    }

    @Override
    default void visit(WildcardType n, A arg) {

    }

    @Override
    default void visit(ModuleDeclaration n, A arg) {

    }

    @Override
    default void visit(ModuleRequiresDirective n, A arg) {

    }

    @Override
    default void visit(ModuleExportsDirective n, A arg) {

    }

    @Override
    default void visit(ModuleProvidesDirective n, A arg) {

    }

    @Override
    default void visit(ModuleUsesDirective n, A arg) {

    }

    @Override
    default void visit(ModuleOpensDirective n, A arg) {

    }

    @Override
    default void visit(UnparsableStmt n, A arg) {

    }

    @Override
    default void visit(ReceiverParameter n, A arg) {

    }

    @Override
    default void visit(VarType n, A arg) {

    }

    @Override
    default void visit(Modifier n, A arg) {

    }

    @Override
    default void visit(SwitchExpr switchExpr, A arg) {

    }

    default void visit(MapCreationLiteralExpression n, A arg) { }

    default void visit(MapCreationLiteralExpressionKeyValuePair n, A arg) { }

    default void visit(ListCreationLiteralExpression n, A arg) { }

    default void visit(ListCreationLiteralExpressionElement n, A arg) { }

    default void visit(WithStatement withStatement, A arg) { }

    @Override
    default void visit(RecordDeclaration n, A arg) {
    }

    @Override
    default void visit(LocalRecordDeclarationStmt n, A arg) {
    }

    @Override
    default void visit(CompactConstructorDeclaration n, A arg) {
    }

    @Override
    default void visit(TextBlockLiteralExpr n, A arg) {
    }

    @Override
    default void visit(YieldStmt yieldStmt, A arg) {
    }

    @Override
    default void visit(PatternExpr n, A arg) {
    }
}
