/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.drools.compiler.commons.jci.compilers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.AND_AND_Expression;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.AssertStatement;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.BreakStatement;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.Clinit;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CompoundAssignment;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ContinueStatement;
import org.eclipse.jdt.internal.compiler.ast.DoStatement;
import org.eclipse.jdt.internal.compiler.ast.EmptyStatement;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.ForStatement;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.eclipse.jdt.internal.compiler.ast.IntersectionCastTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Javadoc;
import org.eclipse.jdt.internal.compiler.ast.JavadocAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.JavadocArgumentExpression;
import org.eclipse.jdt.internal.compiler.ast.JavadocArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocArraySingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocFieldReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocImplicitTypeReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocMessageSend;
import org.eclipse.jdt.internal.compiler.ast.JavadocQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.JavadocSingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
import org.eclipse.jdt.internal.compiler.ast.OR_OR_Expression;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.PostfixExpression;
import org.eclipse.jdt.internal.compiler.ast.PrefixExpression;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedSuperReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedThisReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.StringLiteralConcatenation;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.UnaryExpression;
import org.eclipse.jdt.internal.compiler.ast.UnionTypeReference;
import org.eclipse.jdt.internal.compiler.ast.WhileStatement;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an extended implementation of the {@link ASTVisitor} class
 * (the eclipse *compiler* {@link ASTVisitor} class, not the eclipse *parser/DOM* org.eclipse.jdt.core.com.ASTVisitor).
 * </p>
 * It should be used after compilation in order to collect the full list of fully qualified type (class) names that are used
 * in a compiled Drools rule file/resource.
 * </p>
 * See the end of the {@link EclipseJavaCompiler#compile(String[], org.drools.compiler.commons.jci.readers.ResourceReader, org.drools.compiler.commons.jci.stores.ResourceStore, ClassLoader, JavaCompilerSettings)}
 * method for more info.
 * </hr>
 *
 * I've organized the methods as follows:
 * - Various
 * - Annotations
 * - Assignments/Initializations
 * - Declarations
 * - Expressions
 * - Literals
 * - References
 * - (Control) Statements
 * - Javadoc (never visit)
 */
public class TypeReferenceNameCollector extends ASTVisitor {

    private static Logger debugLogger = LoggerFactory.getLogger(TypeReferenceNameCollector.class);

    private List<char []> typeReferences = new ArrayList<char []>(10);

    // DBG: delete me!
    private Set<String> debugTypeRefSet = new HashSet<String>();
    private boolean debug = true;

    public TypeReferenceNameCollector() {
        // default constructor
    }

    public TypeReferenceNameCollector(boolean debug) {
        if( debug ) {
            this.debug = true;
            debugTypeRefSet = new HashSet<String>();
        }
    }

    public List<char []> getTypeReferenceNames() {
        return typeReferences;
    }

    private void addTypeReferenceNames(TypeBinding... binding) {
        for( TypeBinding bindingElem : binding ) {
//            assert bindingElem != null : "Null binding!";
            if( bindingElem != null ) {
                char [] nameCharArr = bindingElem.readableName();
                typeReferences.add(nameCharArr);
                if( debug ) {
                    String name = new String(nameCharArr);
                    if( ! debugTypeRefSet.add(name) ) {
                        debugLogger.info("-- type: " + name );
                    }
                }
                if( bindingElem.isGenericType() ) {
                    addTypeReferenceNames(bindingElem.typeVariables());
                }
            }
        }
    }

    private void logCall(String callId, ASTNode node) {
        StringBuffer log = new StringBuffer("## " + callId + " :[");
        StackTraceElement [] stack = Thread.currentThread().getStackTrace();
        for( StackTraceElement ste : stack ) {
           if( ste.getClassName().endsWith("Test") ) {
               log.append( ste.getClassName() + "]: ");
               break;
           }
        }
        log.append( node.toString() );
        debugLogger.info(log.toString());
    }


    // == Vistor methods ========================================================================

    // CompilationUnitDeclaration

    @Override
    public void endVisit( CompilationUnitDeclaration compUnitDecl, CompilationUnitScope scope ) {
        // The code below is adapted from the
        // org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration.traverse(ASTVisitor, CompilationUnitScope)
        // method.

        if (compUnitDecl.types != null && compUnitDecl.isPackageInfo()) {
            // resolve synthetic type declaration
            final TypeDeclaration syntheticTypeDeclaration = compUnitDecl.types[0];
            final MethodScope methodScope = syntheticTypeDeclaration.staticInitializerScope;

            // Don't traverse in null scope and invite trouble a la bug 252555.
            if (compUnitDecl.currentPackage != null && methodScope != null) {
                final Annotation[] annotations = compUnitDecl.currentPackage.annotations;
                if (annotations != null) {
                    int annotationsLength = annotations.length;
                    for (int i = 0; i < annotationsLength; i++) {
                        annotations[i].traverse(this, methodScope);
                    }
                }
            }
        }

        // Drools: do *not* add the current package, since that is the DRL package

        if (compUnitDecl.imports != null) {
            int importLength = compUnitDecl.imports.length;
            for (int i = 0; i < importLength; i++) {
                compUnitDecl.imports[i].traverse(this, scope);
            }
        }
        if (compUnitDecl.types != null) {
            int typesLength = compUnitDecl.types.length;
            for (int i = 0; i < typesLength; i++) {
                compUnitDecl.types[i].traverse(this, scope);
            }
        }
        if( compUnitDecl.localTypes != null ) {
            for( LocalTypeBinding typeBinding : compUnitDecl.localTypes ) {
                addTypeReferenceNames(typeBinding);
            }
        }
    }

    @Override
    public boolean visit( CompilationUnitDeclaration compilationUnitDeclaration, CompilationUnitScope scope ) {
        return false;
    }

    // == Various

    // Argument

    @Override
    public void endVisit( Argument arg, BlockScope scope ) {
        // do nothing!! (resolveForCatch(scope) causes errors!)
    }

    @Override
    public void endVisit(Argument arg, ClassScope scope) {
        // do nothing: all type references are taken care of in specific endVisit(..) methods
    }

    @Override
    public boolean visit( Argument argument, BlockScope scope ) {
        logCall("arg (block)", argument);
        // contains type references
        return true;
    }

    @Override
    public boolean visit( Argument argument, ClassScope scope ) {
        logCall("arg (class)", argument);
        // contains type references
        return true;
    }

    // Block

    @Override
    public void endVisit( Block block, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( Block block, BlockScope scope ) {
        logCall("block", block);
        return true;
    }

    // Clinit

    @Override
    public void endVisit( Clinit clinit, ClassScope scope ) {
        if( clinit.arguments != null ) {
           for( int i = 0; i < clinit.arguments.length; ++i ) {
               clinit.arguments[i].toString();
               // TODO
           }
        }
    }

    @Override
    public boolean visit( Clinit clinit, ClassScope scope ) {
        logCall("clinit", clinit);
        return true;
    }

    // MemberValuePair

    @Override
    public void endVisit( MemberValuePair pair, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( MemberValuePair memberValPair, BlockScope scope ) {
        logCall("memberValPair",memberValPair);
        return true;
    }

    @Override
    public void endVisit(MemberValuePair pair, ClassScope scope) {
        super.endVisit(pair, scope);
    }

    @Override
    public boolean visit(MemberValuePair pair, ClassScope scope) {
        return super.visit(pair, scope);
    }

    // MessageSend

    @Override
    public void endVisit( MessageSend messageSend, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( MessageSend msgSend, BlockScope scope ) {
        logCall("msgSend",msgSend);
        return true;
    }

    // TypeParameter

    @Override
    public void endVisit( TypeParameter typeParameter, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( TypeParameter typeParam, BlockScope scope ) {
        logCall("typeParam (block)",typeParam);
        return true;
    }

    @Override
    public void endVisit( TypeParameter typeParameter, ClassScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( TypeParameter typeParam, ClassScope scope ) {
        logCall("typeParam (class)",typeParam);
        return true;
    }

    // Wildcard

    @Override
    public void endVisit( Wildcard wildcard, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( Wildcard wildcard, BlockScope scope ) {
        logCall("wildcard (block)",wildcard);
        return true;
    }

    @Override
    public void endVisit( Wildcard wildcard, ClassScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( Wildcard wildcard, ClassScope scope ) {
        logCall("wildcard (class)",wildcard);
        return true;
    }

    // == Annotations

    // MarkerAnnotation

    @Override
    public void endVisit( MarkerAnnotation annotation, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( MarkerAnnotation markerAnno, BlockScope scope ) {
        logCall("markerAnno",markerAnno);
        return true;
    }

    @Override
    public void endVisit(MarkerAnnotation annotation, ClassScope scope) {
        super.endVisit(annotation, scope);
    }

    @Override
    public boolean visit(MarkerAnnotation annotation, ClassScope scope) {
        return super.visit(annotation, scope);
    }

    // NormalAnnotation

    @Override
    public void endVisit( NormalAnnotation annotation, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( NormalAnnotation normalAnno, BlockScope scope ) {
        logCall("normalAnno",normalAnno);
        return true;
    }

    @Override
    public void endVisit(NormalAnnotation annotation, ClassScope scope) {
        super.endVisit(annotation, scope);
    }

    @Override
    public boolean visit(NormalAnnotation annotation, ClassScope scope) {
        return super.visit(annotation, scope);
    }

    // SingleMemberAnnotation

    @Override
    public void endVisit( SingleMemberAnnotation annotation, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( SingleMemberAnnotation singleMemAnno, BlockScope scope ) {
        logCall("singleMemAnno",singleMemAnno);
        return true;
    }

    @Override
    public void endVisit(SingleMemberAnnotation annotation, ClassScope scope) {
        super.endVisit(annotation, scope);
    }

    @Override
    public boolean visit(SingleMemberAnnotation annotation, ClassScope scope) {
        return super.visit(annotation, scope);
    }


    // == Assignments/Initializations

    // Assignment

    @Override
    public void endVisit( Assignment assignment, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( Assignment assign, BlockScope scope ) {
        logCall("assign", assign);
        return true;
    }

    // NullLiteral
    // CompoundAssignment

    @Override
    public void endVisit( CompoundAssignment compoundAssignment, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( CompoundAssignment compAssign, BlockScope scope ) {
        logCall("comp assign", compAssign);
        return true;
    }

    // ArrayInitializer

    @Override
    public void endVisit( ArrayInitializer arrayInitializer, BlockScope scope ) {
        // do nothing: all type references are taken care of in specific endVisit(..) methods
    }

    @Override
    public boolean visit( ArrayInitializer arrayInit, BlockScope scope ) {
        logCall("array init", arrayInit);
        // contains type references
        return true;
    }

    @Override
    public void endVisit(ArrayInitializer arrayInitializer, ClassScope scope) {
        super.endVisit(arrayInitializer, scope);
    }

    @Override
    public boolean visit(ArrayInitializer arrayInitializer, ClassScope scope) {
        return false;
    }

    // Initializer

    @Override
    public void endVisit( Initializer initializer, MethodScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( Initializer init, MethodScope scope ) {
        logCall("init",init);
        return true;
    }

    // ExplicitConstructorCall: no endVisit needed, is correctly traversed

    @Override
    public boolean visit( ExplicitConstructorCall explConstCall, BlockScope scope ) {
        logCall("explicit const call", explConstCall);
        return true;
    }

    // == Declarations

    // AnnotationMethodDeclaration

    @Override
    public void endVisit(AnnotationMethodDeclaration annotationTypeDeclaration, ClassScope classScope) {
        // do nothing: all type references are taken care of in specific endVisit(..) methods
    }

    @Override
    public boolean visit( AnnotationMethodDeclaration annotationTypeDeclaration, ClassScope classScope ) {
        // contains type references
        logCall("annotationMethod", annotationTypeDeclaration);
        return true;
    }

    // ConstructorDeclaration: no endVisit needed, is correctly traversed

    @Override
    public boolean visit( ConstructorDeclaration constrDecl, ClassScope scope ) {
        logCall("const decl", constrDecl);
        return true;
    }

    // FieldDeclaration

    @Override
    public void endVisit( FieldDeclaration fieldDeclaration, MethodScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( FieldDeclaration fieldDecl, MethodScope scope ) {
        logCall("field decl", fieldDecl);
        return true;
    }

    // LocalDeclaration

    @Override
    public void endVisit( LocalDeclaration localDeclaration, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( LocalDeclaration localDecl, BlockScope scope ) {
        logCall("localDecl",localDecl);
        return true;
    }

    // MethodDeclaration

    @Override
    public void endVisit( MethodDeclaration methodDeclaration, ClassScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( MethodDeclaration methodDecl, ClassScope scope ) {
        logCall("methodDecl",methodDecl);
        return true;
    }

    // TypeDeclaration

    @Override
    public void endVisit( TypeDeclaration localTypeDeclaration, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( TypeDeclaration localTypeDecl, BlockScope scope ) {
        logCall("localTypeDecl",localTypeDecl);
        return true;
    }

    @Override
    public void endVisit( TypeDeclaration memberTypeDeclaration, ClassScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( TypeDeclaration memberTypeDecl, ClassScope scope ) {
        logCall("memberTypeDecl",memberTypeDecl);
        return true;
    }

    @Override
    public void endVisit( TypeDeclaration typeDeclaration, CompilationUnitScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( TypeDeclaration typeDecl, CompilationUnitScope scope ) {
        logCall("typeDecl",typeDecl);
        return true;
    }

    // == Expressions


    @Override
    public void endVisit( AllocationExpression allocExpr, BlockScope scope ) {
        if( allocExpr.genericTypeArguments != null && allocExpr.genericTypeArguments.length > 0 ) {
            addTypeReferenceNames(allocExpr.genericTypeArguments);
        }
        // other type references are taken care by other visit(..) methods
    }

    @Override
    public boolean visit( AllocationExpression allocationExpression, BlockScope scope ) {
        logCall("allocExpr", allocationExpression);
        // contains type references
        return true;
    }

    // AND_AND_Expression

    @Override
    public void endVisit( AND_AND_Expression and_and_Expr, BlockScope scope ) {
       addTypeReferenceNames(and_and_Expr.resolveType(scope));
        // other type references are taken care by other visit(..) methods
    }

    @Override
    public boolean visit( AND_AND_Expression and_and_Expression, BlockScope scope ) {
        logCall("and/and expr", and_and_Expression);
        // contains type references
        return true;
    }

    // ArrayAllocationExpression

    @Override
    public void endVisit( ArrayAllocationExpression arrayAllocationExpression, BlockScope scope ) {
        // do nothing: all type references are taken care of in specific endVisit(..) methods
    }

    @Override
    public boolean visit( ArrayAllocationExpression arrayAllocExpr, BlockScope scope ) {
        logCall("array alloc expr", arrayAllocExpr);
        // contains type references
        return true;
    }

    // BinaryExpression: contains references, do not endVisit

    @Override
    public boolean visit( BinaryExpression binaryExpr, BlockScope scope ) {
        return true;
    }

    // CastExpression

    @Override
    public void endVisit( CastExpression castExpression, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( CastExpression castExpr, BlockScope scope ) {
        logCall("cast expr", castExpr);
        return true;
    }

    // ConditionalExpression

    @Override
    public void endVisit( ConditionalExpression conditionalExpression, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( ConditionalExpression condExpr, BlockScope scope ) {
        logCall("cond expr", condExpr);
        return true;
    }

    // EqualExpression

    @Override
    public void endVisit( EqualExpression equalExpression, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( EqualExpression equalExpr, BlockScope scope ) {
        logCall("equal Expr", equalExpr);
        return true;
    }

    // InstanceOfExpression

    @Override
    public void endVisit( InstanceOfExpression instanceOfExpression, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( InstanceOfExpression instOfExpr, BlockScope scope ) {
        logCall("instOfExpr",instOfExpr);
        return true;
    }

    // LambdaExpression

    @Override
    public void endVisit(LambdaExpression lambdaExpression, BlockScope blockScope) {
        super.endVisit(lambdaExpression, blockScope);
    }

    @Override
    public boolean visit(LambdaExpression lambdaExpression, BlockScope blockScope) {
        return super.visit(lambdaExpression, blockScope);
    }

    // OR_OR_Expression

    @Override
    public void endVisit( OR_OR_Expression or_or_Expression, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( OR_OR_Expression or_or_Expr, BlockScope scope ) {
        logCall("or_or_Expr",or_or_Expr);
        return true;
    }

    // PostfixExpression

    @Override
    public void endVisit( PostfixExpression postfixExpression, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( PostfixExpression postfixExpr, BlockScope scope ) {
        logCall("postfixExpr",postfixExpr);
        return true;
    }

    // PrefixExpression

    @Override
    public void endVisit( PrefixExpression prefixExpression, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( PrefixExpression prefixExpr, BlockScope scope ) {
        logCall("prefixExpr",prefixExpr);
        return true;
    }

    // QualifiedAllocationExpression

    @Override
    public void endVisit( QualifiedAllocationExpression qualifiedAllocationExpression, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( QualifiedAllocationExpression qualAllocExpr, BlockScope scope ) {
        logCall("qualAllocExpr",qualAllocExpr);
        return true;
    }

    // ReferenceExpression

    @Override
    public void endVisit(ReferenceExpression referenceExpression, BlockScope blockScope) {
        super.endVisit(referenceExpression, blockScope);
    }

    @Override
    public boolean visit(ReferenceExpression referenceExpression, BlockScope blockScope) {
        return super.visit(referenceExpression, blockScope);
    }

    // UnaryExpression

    @Override
    public void endVisit( UnaryExpression unaryExpression, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( UnaryExpression unaryExpr, BlockScope scope ) {
        logCall("unaryExpr",unaryExpr);
        return true;
    }

    // == Literals: most literals can be ignored because they do not contain class info

    // Primitive literals: skip them becuase they contain no class info and are not traversed in any case

    // ClassLiteralAccess

    @Override
    public void endVisit( ClassLiteralAccess classLiteral, BlockScope scope ) {
        addTypeReferenceNames(classLiteral.resolveType(scope));
    }

    @Override
    public boolean visit( ClassLiteralAccess classLit, BlockScope scope ) {
        return true;
    }

    // StringLiteralConcatenation: contains other literals, including a possible class literal

    @Override
    public boolean visit( StringLiteralConcatenation stringLitConcat, BlockScope scope ) {
        return true;
    }

    // == References
    // 1. In general: do *not* visit references: there are no user-defined annotations on reference
    // 2. However, *do* collect resolved type info in the visit(..) methods (while still returning false)
    //    (Collecting info in the visit method and not overriding the endVisit method gives the best performance)
    // 3. Lastly, even those these are references (and not declarations), still

    // ArrayQualifiedTypeReference

    @Override
    public boolean visit( ArrayQualifiedTypeReference arrQualTypeRef, BlockScope scope ) {
        addTypeReferenceNames(arrQualTypeRef.resolveType(scope));
        // TODO: meta
        return false;
    }

    @Override
    public boolean visit( ArrayQualifiedTypeReference arrQualTypeRef, ClassScope scope ) {
        addTypeReferenceNames(arrQualTypeRef.resolveType(scope));
        // TODO: meta
        return false;
    }

    // ArrayReference

    @Override
    public boolean visit( ArrayReference arrRef, BlockScope scope ) {
        addTypeReferenceNames(arrRef.resolveType(scope));
        // TODO: meta
        return false;
    }

    // ArrayTypeReference

    @Override
    public void endVisit( ArrayTypeReference arrayTypeReference, BlockScope scope ) {
        arrayTypeReference.getTypeReferences();
        // do nothing
    }

    @Override
    public boolean visit( ArrayTypeReference arrTypeRef, BlockScope scope ) {
        logCall("arr type ref (block)", arrTypeRef);
        // TODO: maybe this is NOT necessary, because ArrayReference.resolveType(scope) returns all the info we need?
        return true;
    }

    @Override
    public void endVisit( ArrayTypeReference arrayTypeReference, ClassScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( ArrayTypeReference arrTypeRef, ClassScope scope ) {
        logCall("arr type ref (class)", arrTypeRef);
        return true;
    }

    // FieldReference

    @Override
    public void endVisit( FieldReference fieldReference, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( FieldReference fieldRef, BlockScope scope ) {
        logCall("field ref", fieldRef);
        return true;
    }

    @Override
    public void endVisit( FieldReference fieldReference, ClassScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( FieldReference fieldRef, ClassScope scope ) {
        logCall("field ref", fieldRef);
        return true;
    }

    // ImportReference

    @Override
    public void endVisit( ImportReference importRef, CompilationUnitScope scope ) {
        // only add if not a wildcard import
        if( (importRef.bits & ASTNode.OnDemand) == 0 ) {
            logCall("importRef: " + importRef.toString(), importRef);
            // WAIT: and if it's the DRL package, not a class?
            // !! Check the scope???
            typeReferences.add(importRef.toString().toCharArray());
        }
    }

    @Override
    public boolean visit( ImportReference importRef, CompilationUnitScope scope ) {
        // just an import statement
        return true;
    }

    // IntersectionCastTypeReference

    @Override
    public void endVisit(IntersectionCastTypeReference intersectionCastTypeReference, ClassScope scope) {
        super.endVisit(intersectionCastTypeReference, scope);
    }

    @Override
    public void endVisit(IntersectionCastTypeReference intersectionCastTypeReference, BlockScope scope) {
        super.endVisit(intersectionCastTypeReference, scope);
    }

    @Override
    public boolean visit(IntersectionCastTypeReference intersectionCastTypeReference, ClassScope scope) {
        return super.visit(intersectionCastTypeReference, scope);
    }

    @Override
    public boolean visit(IntersectionCastTypeReference intersectionCastTypeReference, BlockScope scope) {
        return super.visit(intersectionCastTypeReference, scope);
    }

    // ParameterizedQualifiedTypeReference

    @Override
    public void endVisit( ParameterizedQualifiedTypeReference paramQualifiedTypeReference, BlockScope scope ) {
        addTypeReferenceNames(paramQualifiedTypeReference.resolveType(scope));
    }

    // ParameterizedQualifiedTypeReference

    @Override
    public void endVisit( ParameterizedQualifiedTypeReference paramQualifiedTypeReference, ClassScope scope ) {
        addTypeReferenceNames(paramQualifiedTypeReference.resolveType(scope));
    }

    // ParameterizedSingleTypeReference

    @Override
    public void endVisit( ParameterizedSingleTypeReference paramSingleTypeReference, BlockScope scope ) {
        addTypeReferenceNames(paramSingleTypeReference.resolveType(scope));
    }

    // ParameterizedSingleTypeReference

    @Override
    public void endVisit( ParameterizedSingleTypeReference paramSingleTypeReference, ClassScope scope ) {
        addTypeReferenceNames(paramSingleTypeReference.resolveType(scope));
    }

    @Override
    public boolean visit( ParameterizedSingleTypeReference paramSingleTypeRef, ClassScope scope ) {
        logCall("paramSingleTypeRef (class)",paramSingleTypeRef);
        return true;
    }

    // QualifiedNameReference

    @Override
    public void endVisit( QualifiedNameReference qualNameRef, BlockScope scope ) {
        addTypeReferenceNames(qualNameRef.resolveType(scope));
    }

    @Override
    public boolean visit( QualifiedNameReference qualNameRef, BlockScope scope ) {
        logCall("qualNameRef (block)",qualNameRef);
        return true;
    }

    // QualifiedNameReference

    @Override
    public void endVisit( QualifiedNameReference qualNameRef, ClassScope scope ) {
        addTypeReferenceNames(qualNameRef.resolveType(scope));
    }

    @Override
    public boolean visit( QualifiedNameReference qualNameRef, ClassScope scope ) {
        logCall("qualNameRef (class)",qualNameRef);
        return true;
    }

    // QualifiedSuperReference

    @Override
    public void endVisit( QualifiedSuperReference qualifiedSuperReference, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( QualifiedSuperReference qualSuperRef, BlockScope scope ) {
        logCall("qualSuperRef (block)",qualSuperRef);
        return true;
    }

    // QualifiedSuperReference

    @Override
    public void endVisit( QualifiedSuperReference qualifiedSuperReference, ClassScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( QualifiedSuperReference qualSuperRef, ClassScope scope ) {
        logCall("qualSuperRef (class)",qualSuperRef);
        return true;
    }

    // QualifiedThisReference

    @Override
    public void endVisit( QualifiedThisReference qualifiedThisReference, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( QualifiedThisReference qualThisRef, BlockScope scope ) {
        logCall("qualThisRef (block)",qualThisRef);
        return true;
    }

    @Override
    public void endVisit( QualifiedThisReference qualifiedThisReference, ClassScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( QualifiedThisReference qualThisRef, ClassScope scope ) {
        logCall("qualThisRef (class)",qualThisRef);
        return true;
    }

    // QualifiedTypeReference

    @Override
    public void endVisit( QualifiedTypeReference qualTypeRef, BlockScope scope ) {
        addTypeReferenceNames(qualTypeRef.resolveType(scope));
    }

    @Override
    public boolean visit( QualifiedTypeReference qualTypeRef, BlockScope scope ) {
        return true;
    }

    @Override
    public void endVisit( QualifiedTypeReference qualTypeRef, ClassScope scope ) {
        addTypeReferenceNames(qualTypeRef.resolveType(scope));
    }

    @Override
    public boolean visit( QualifiedTypeReference qualTypeRef, ClassScope scope ) {
        logCall("qualTypeRef (class)",qualTypeRef);
        return true;
    }

    // SingleNameReference

    @Override
    public void endVisit( SingleNameReference singleNameReference, BlockScope scope ) {
        // do *NOT* use
        // - org.eclipse.jdt.internal.compiler.ast.SingleNameReference.resolveType(BlockScope)
        // because that will cause compiler errors
        addTypeReferenceNames(singleNameReference.resolvedType);
    }

    @Override
    public void endVisit( SingleNameReference singleNameReference, ClassScope scope ) {
        addTypeReferenceNames(singleNameReference.resolveType(scope));
    }

    @Override
    public boolean visit( SingleNameReference singleNameRef, ClassScope scope ) {
        logCall("singleNameRef (class)",singleNameRef);
        return true;
    }

    // SingleTypeReference

    @Override
    public void endVisit( SingleTypeReference singleTypeRef, BlockScope scope ) {
        addTypeReferenceNames(singleTypeRef.resolveType(scope));
    }

    @Override
    public boolean visit( SingleTypeReference singleTypeRef, BlockScope scope ) {
        logCall("singleTypeRef (block)",singleTypeRef);
        return true;
    }

    // SingleTypeReference

    @Override
    public void endVisit( SingleTypeReference singleTypeReference, ClassScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( SingleTypeReference singleTypeRef, ClassScope scope ) {
        logCall("singleTypeRef (class)",singleTypeRef);
        return true;
    }

    // SuperReference

    @Override
    public void endVisit( SuperReference superReference, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( SuperReference superRef, BlockScope scope ) {
        logCall("superRef",superRef);
        return true;
    }

    // ThisReference

    @Override
    public void endVisit( ThisReference thisReference, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( ThisReference thisRef, BlockScope scope ) {
        logCall("thisRef (block)",thisRef);
        return true;
    }

    @Override
    public void endVisit( ThisReference thisReference, ClassScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( ThisReference thisRef, ClassScope scope ) {
        logCall("thisRef (class)",thisRef);
        return true;
    }

    // UnionTypeReference

    @Override
    public void endVisit( UnionTypeReference unionTypeReference, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( UnionTypeReference unionTypeRef, BlockScope scope ) {
        logCall("unionTypeRef (block)",unionTypeRef);
        return true;
    }

    // UnionTypeReference

    @Override
    public void endVisit( UnionTypeReference unionTypeReference, ClassScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( UnionTypeReference unionTypeRef, ClassScope scope ) {
        logCall("unionTypeRef (class)",unionTypeRef);
        return true;
    }

    // ==




    // AssertStatement

    @Override
    public void endVisit( AssertStatement assertStatement, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( AssertStatement assertStat, BlockScope scope ) {
        logCall("assert stat", assertStat);
        return true;
    }

    // Block



    // BreakStatement

    @Override
    public void endVisit( BreakStatement breakStatement, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( BreakStatement breakStat, BlockScope scope ) {
        logCall("break stat", breakStat);
        return true;
    }

    // CaseStatement

    @Override
    public void endVisit( CaseStatement caseStatement, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( CaseStatement caseStat, BlockScope scope ) {
        logCall("case stat", caseStat);
        return true;
    }

    // Clinit



    // ContinueStatement

    @Override
    public void endVisit( ContinueStatement continueStatement, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( ContinueStatement contStat, BlockScope scope ) {
        logCall("cont stat", contStat);
        return true;
    }

    // DoStatement

    @Override
    public void endVisit( DoStatement doStatement, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( DoStatement doStat, BlockScope scope ) {
        logCall("do stat", doStat);
        return true;
    }

    // EmptyStatement

    @Override
    public void endVisit( EmptyStatement emptyStatement, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( EmptyStatement emptyStat, BlockScope scope ) {
        logCall("empty stat", emptyStat);
        return true;
    }

    // ForeachStatement

    @Override
    public void endVisit( ForeachStatement forStatement, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( ForeachStatement forStat, BlockScope scope ) {
        logCall("forStat",forStat);
        return true;
    }

    // ForStatement

    @Override
    public void endVisit( ForStatement forStatement, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( ForStatement forStat, BlockScope scope ) {
        logCall("forStat",forStat);
        return true;
    }

    // IfStatement

    @Override
    public void endVisit( IfStatement ifStatement, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( IfStatement ifStat, BlockScope scope ) {
        logCall("ifStat",ifStat);
        return true;
    }

    // LabeledStatement

    @Override
    public void endVisit( LabeledStatement labeledStatement, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( LabeledStatement labeledStat, BlockScope scope ) {
        logCall("labeledStat",labeledStat);
        return true;
    }



    // ReturnStatement

    @Override
    public void endVisit( ReturnStatement returnStatement, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( ReturnStatement retStat, BlockScope scope ) {
        logCall("returnStat",retStat);
        return true;
    }

    // SwitchStatement

    @Override
    public void endVisit( SwitchStatement switchStatement, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( SwitchStatement switchStat, BlockScope scope ) {
        logCall("switchStat",switchStat);
        return true;
    }

    // SynchronizedStatement

    @Override
    public void endVisit( SynchronizedStatement synchronizedStatement, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( SynchronizedStatement syncStat, BlockScope scope ) {
        logCall("syncStat",syncStat);
        return true;
    }

    // ThrowStatement

    @Override
    public void endVisit( ThrowStatement throwStatement, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( ThrowStatement throwStat, BlockScope scope ) {
        logCall("throwStat",throwStat);
        return true;
    }

    // TryStatement

    @Override
    public void endVisit( TryStatement tryStatement, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( TryStatement tryStat, BlockScope scope ) {
        logCall("tryStat",tryStat);
        return true;
    }

    // WhileStatement

    @Override
    public void endVisit( WhileStatement whileStatement, BlockScope scope ) {
        // do nothing
    }

    @Override
    public boolean visit( WhileStatement whileStat, BlockScope scope ) {
        logCall("whileStat",whileStat);
        return true;
    }


    // = Javadoc: there's no reason to visit any Javadoc tree

    @Override
    public boolean visit(Javadoc javadoc, BlockScope scope) {
        return false;
    }

    @Override
    public boolean visit(Javadoc javadoc, ClassScope scope) {
        return false;
    }

    @Override
    public boolean visit(JavadocAllocationExpression expression, BlockScope scope) {
        return false;
    }

    @Override
    public boolean visit(JavadocAllocationExpression expression, ClassScope scope) {
        return false;
    }

    @Override
    public boolean visit(JavadocArgumentExpression expression, BlockScope scope) {
        return false;
    }

    @Override
    public boolean visit(JavadocArgumentExpression expression, ClassScope scope) {
        return false;
    }

    @Override
    public boolean visit(JavadocArrayQualifiedTypeReference typeRef, BlockScope scope) {
        return false;
    }

    @Override
    public boolean visit(JavadocArrayQualifiedTypeReference typeRef, ClassScope scope) {
        return false;
    }

    @Override
    public boolean visit(JavadocArraySingleTypeReference typeRef, BlockScope scope) {
        return false;
    }

    @Override
    public boolean visit(JavadocArraySingleTypeReference typeRef, ClassScope scope) {
        return false;
    }

    @Override
    public boolean visit(JavadocFieldReference fieldRef, BlockScope scope) {
        return false;
    }

    @Override
    public boolean visit(JavadocFieldReference fieldRef, ClassScope scope) {
        return false;
    }

    @Override
    public boolean visit(JavadocImplicitTypeReference implicitTypeReference, BlockScope scope) {
        return false;
    }

    @Override
    public boolean visit(JavadocImplicitTypeReference implicitTypeReference, ClassScope scope) {
        return false;
    }

    @Override
    public boolean visit(JavadocMessageSend messageSend, BlockScope scope) {
        return false;
    }

    @Override
    public boolean visit(JavadocMessageSend messageSend, ClassScope scope) {
        return false;
    }

    @Override
    public boolean visit(JavadocQualifiedTypeReference typeRef, BlockScope scope) {
        return false;
    }

    @Override
    public boolean visit(JavadocQualifiedTypeReference typeRef, ClassScope scope) {
        return false;
    }

    @Override
    public boolean visit(JavadocReturnStatement statement, BlockScope scope) {
        return false;
    }

    @Override
    public boolean visit(JavadocReturnStatement statement, ClassScope scope) {
        return false;
    }

    @Override
    public boolean visit(JavadocSingleNameReference argument, BlockScope scope) {
        return false;
    }

    @Override
    public boolean visit(JavadocSingleNameReference argument, ClassScope scope) {
        return false;
    }

    @Override
    public boolean visit(JavadocSingleTypeReference typeRef, BlockScope scope) {
        return false;
    }

    @Override
    public boolean visit(JavadocSingleTypeReference typeRef, ClassScope scope) {
        return false;
    }

}
