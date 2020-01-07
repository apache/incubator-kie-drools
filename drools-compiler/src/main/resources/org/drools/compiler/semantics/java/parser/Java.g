/** A Java 1.5 grammar for ANTLR v3 derived from the spec
 *
 *  This is a very close representation of the spec; the changes
 *  are comestic (remove left recursion) and also fixes (the spec
 *  isn't exactly perfect).  I have run this on the 1.4.2 source
 *  and some nasty looking enums from 1.5, but have not really
 *  tested for 1.5 compatibility.
 *
 *  I built this with: java -Xmx100M org.antlr.Tool java.g 
 *  and got two errors that are ok (for now):
 *  java.g:691:9: Decision can match input such as
 *    "'0'..'9'{'E', 'e'}{'+', '-'}'0'..'9'{'D', 'F', 'd', 'f'}"
 *    using multiple alternatives: 3, 4
 *  As a result, alternative(s) 4 were disabled for that input
 *  java.g:734:35: Decision can match input such as "{'$', 'A'..'Z',
 *    '_', 'a'..'z', '\u00C0'..'\u00D6', '\u00D8'..'\u00F6',
 *    '\u00F8'..'\u1FFF', '\u3040'..'\u318F', '\u3300'..'\u337F',
 *    '\u3400'..'\u3D2D', '\u4E00'..'\u9FFF', '\uF900'..'\uFAFF'}"
 *    using multiple alternatives: 1, 2
 *  As a result, alternative(s) 2 were disabled for that input
 *
 *  You can turn enum on/off as a keyword :)
 *
 *  Version 1.0 -- initial release July 5, 2006 (requires 3.0b2 or higher)
 *
 *  Primary author: Terence Parr, July 2006
 *
 *  Version 1.0.1 -- corrections by Koen Vanderkimpen & Marko van Dooren,
 *      October 25, 2006;
 *      fixed normalInterfaceDeclaration: now uses typeParameters instead
 *          of typeParameter (according to JLS, 3rd edition)
 *      fixed castExpression: no longer allows expression next to type
 *          (according to semantics in JLS, in contrast with syntax in JLS)
 *
 *  Version 1.0.2 -- Terence Parr, Nov 27, 2006
 *      java spec I built this from had some bizarre for-loop control.
 *          Looked weird and so I looked elsewhere...Yep, it's messed up.
 *          simplified.
 *
 *  Version 1.0.3 -- Chris Hogue, Feb 26, 2007
 *      Factored out an annotationName rule and used it in the annotation rule.
 *          Not sure why, but typeName wasn't recognizing references to inner
 *       	`1111111   annotations (e.g. @InterfaceName.InnerAnnotation())
 *      Factored out the elementValue section of an annotation reference.  Created 
 *          elementValuePair and elementValuePairs rules, then used them in the 
 *          annotation rule.  Allows it to recognize annotation references with 
 *          multiple, comma separated attributes.
 *      Updated elementValueArrayInitializer so that it allows multiple elements.
 *          (It was only allowing 0 or 1 element).
 *      Updated localVariableDeclaration to allow annotations.  Interestingly the JLS
 *          doesn't appear to indicate this is legal, but it does work as of at least
 *          JDK 1.5.0_06.
 *      Moved the Identifier portion of annotationTypeElementRest to annotationMethodRest.
 *          Because annotationConstantRest already references variableDeclarator which 
 *          has the Identifier portion in it, the parser would fail on constants in 
 *          annotation definitions because it expected two identifiers.  
 *      Added optional trailing ';' to the alternatives in annotationTypeElementRest.
 *          Wouldn't handle an inner interface that has a trailing ';'.
 *      Swapped the expression and type rule reference order in castExpression to 
 *          make it check for genericized casts first.  It was failing to recognize a
 *          statement like  "Class<Byte> TYPE = (Class<Byte>)...;" because it was seeing
 *          'Class<Byte' in the cast expression as a less than expression, then failing 
 *          on the '>'.
 *      Changed createdName to use typeArguments instead of nonWildcardTypeArguments.
 *          Again, JLS doesn't seem to allow this, but java.lang.Class has an example of
 *          of this construct.
 *      Changed the 'this' alternative in primary to allow 'identifierSuffix' rather than
 *          just 'arguments'.  The case it couldn't handle was a call to an explicit
 *          generic method invocation (e.g. this.<E>doSomething()).  Using identifierSuffix
 *          may be overly aggressive--perhaps should create a more constrained thisSuffix rule?
 * 		
 *  Version 1.0.4 -- Hiroaki Nakamura, May 3, 2007
 *
 *	Fixed formalParameterDecls, localVariableDeclaration, forInit,
 *	and forVarControl to use variableModifier* not 'final'? (annotation)?
 */
grammar Java;
options {k=2; backtrack=true; memoize=true;}

scope VarDecl {
    org.drools.compiler.rule.builder.dialect.java.parser.JavaLocalDeclarationDescr descr;
}
 
@parser::header {
    package org.drools.compiler.rule.builder.dialect.java.parser;
    import java.util.Iterator;
    import java.util.Queue;
    import java.util.LinkedList;
    import java.util.Stack;
    import java.util.Set;
    import java.util.HashSet;
    import java.util.Collections;

    import org.drools.compiler.rule.builder.dialect.java.parser.JavaLocalDeclarationDescr;
    import org.drools.compiler.rule.builder.dialect.java.parser.JavaRootBlockDescr;
    import org.drools.compiler.rule.builder.dialect.java.parser.JavaContainerBlockDescr;
    import org.drools.compiler.rule.builder.dialect.java.parser.JavaBlockDescr;
    
}

@parser::members {
    private Set<String> identifiers = new HashSet<String>();
    public Set<String> getIdentifiers() { return identifiers; }

    private Stack<List<JavaLocalDeclarationDescr>> localDeclarationsStack = new Stack<List<JavaLocalDeclarationDescr>>(); 
    { localDeclarationsStack.push( new ArrayList<JavaLocalDeclarationDescr>() ); }
    private int localVariableLevel = 0;

    public static final CommonToken IGNORE_TOKEN = new CommonToken(null,0,99,0,0);
    private List errors = new ArrayList();
    
        private JavaRootBlockDescr rootBlockDescr = new JavaRootBlockDescr();
        private LinkedList<JavaContainerBlockDescr> blocks;
        private Set<String> assignedVariables;

        public void addBlockDescr(JavaBlockDescr blockDescr) {
            if ( this.blocks == null ) {
                this.blocks = new LinkedList<JavaContainerBlockDescr>();
                this.blocks.add( this.rootBlockDescr );
            }
            blocks.getLast().addJavaBlockDescr( blockDescr );
        }

        public void addAssignment(String variable) {
            if ( this.assignedVariables == null ) {
                this.assignedVariables = new HashSet<String>();
            }
            this.assignedVariables.add( variable );
        }

            public void pushContainerBlockDescr(JavaContainerBlockDescr blockDescr, boolean addToParent) {
                if ( addToParent ) {
                    addBlockDescr(blockDescr);
                }
                blocks.add( blockDescr );
            }      
            
            public void popContainerBlockDescr() {
                blocks.removeLast( );
            }          
        
        public JavaRootBlockDescr getRootBlockDescr() { return rootBlockDescr; }

        public Set<String> getAssignedVariables() { return assignedVariables != null ? assignedVariables : Collections.emptySet(); }

    private String source = "unknown";

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return this.source;
    }

    public void reportError(RecognitionException ex) {
            // if we've already reported an error and have not matched a token
                // yet successfully, don't report any errors.
                if ( state.errorRecovery ) {
                        //System.err.print("[SPURIOUS] ");
                        return;
                }
                state.errorRecovery = true;

        errors.add( ex );
    }

         /** return the raw RecognitionException errors */
         public List getErrors() {
             return errors;
         }

         /** Return a list of pretty strings summarising the errors */
         public List getErrorMessages() {
             List messages = new ArrayList();
         for ( Iterator errorIter = errors.iterator() ; errorIter.hasNext() ; ) {
                      messages.add( createErrorMessage( (RecognitionException) errorIter.next() ) );
                  }
                  return messages;
         }

         /** return true if any parser errors were accumulated */
         public boolean hasErrors() {
          return ! errors.isEmpty();
         }

         /** This will take a RecognitionException, and create a sensible error message out of it */
         public String createErrorMessage(RecognitionException e)
        {
        StringBuilder message = new StringBuilder();
                message.append( source + ":"+e.line+":"+e.charPositionInLine+" ");
                if ( e instanceof MismatchedTokenException ) {
                        MismatchedTokenException mte = (MismatchedTokenException)e;
                        message.append("mismatched token: "+
                                                           e.token+
                                                           "; expecting type "+
                                                           tokenNames[mte.expecting]);
                }
                else if ( e instanceof MismatchedTreeNodeException ) {
                        MismatchedTreeNodeException mtne = (MismatchedTreeNodeException)e;
                        message.append("mismatched tree node: "+
                                                           mtne.node+
                                                           "; expecting type "+
                                                           tokenNames[mtne.expecting]);
                }
                else if ( e instanceof NoViableAltException ) {
                        NoViableAltException nvae = (NoViableAltException)e;
            message.append( "Unexpected token '" + e.token.getText() + "'" );
                        /*
                        message.append("decision=<<"+nvae.grammarDecisionDescription+">>"+
                                                           " state "+nvae.stateNumber+
                                                           " (decision="+nvae.decisionNumber+
                                                           ") no viable alt; token="+
                                                           e.token);
                                                           */
                }
                else if ( e instanceof EarlyExitException ) {
                        EarlyExitException eee = (EarlyExitException)e;
                        message.append("required (...)+ loop (decision="+
                                                           eee.decisionNumber+
                                                           ") did not match anything; token="+
                                                           e.token);
                }
                else if ( e instanceof MismatchedSetException ) {
                        MismatchedSetException mse = (MismatchedSetException)e;
                        message.append("mismatched token '"+
                                                           e.token+
                                                           "' expecting set "+mse.expecting);
                }
                else if ( e instanceof MismatchedNotSetException ) {
                        MismatchedNotSetException mse = (MismatchedNotSetException)e;
                        message.append("mismatched token '"+
                                                           e.token+
                                                           "' expecting set "+mse.expecting);
                }
                else if ( e instanceof FailedPredicateException ) {
                        FailedPredicateException fpe = (FailedPredicateException)e;
                        message.append("rule "+fpe.ruleName+" failed predicate: {"+
                                                           fpe.predicateText+"}?");
        }
                   return message.toString();
        }   
        
        public void increaseLevel() {
            this.localVariableLevel++;
            localDeclarationsStack.push( new ArrayList<JavaLocalDeclarationDescr>() );
        }
        
        public void decreaseLevel() {
            this.localVariableLevel--;
            localDeclarationsStack.pop();
        }
        
        public void addLocalDeclaration( JavaLocalDeclarationDescr decl ) {
            localDeclarationsStack.peek().add(decl);
        }

        public List<JavaLocalDeclarationDescr> getLocalDeclarations() { 
            if( localDeclarationsStack.size() > 1 ) {
                List<JavaLocalDeclarationDescr> decls = new ArrayList<JavaLocalDeclarationDescr>();
                for( List<JavaLocalDeclarationDescr> local : localDeclarationsStack ) {
                    decls.addAll( local );
                }
                return decls;
            }
            // the stack should never be empty, so it is safe to do a peek() here
            return new ArrayList<JavaLocalDeclarationDescr>( localDeclarationsStack.peek() ); 
        }
} 

@lexer::header {
    package org.drools.compiler.rule.builder.dialect.java.parser;
}

@lexer::members {
    public static final CommonToken IGNORE_TOKEN = new CommonToken(null,0,99,0,0);
    protected boolean enumIsKeyword = true;
}
 

// starting point for parsing a java file
compilationUnit
    :	annotations?
        packageDeclaration?
        importDeclaration*
        typeDeclaration*
    ;

packageDeclaration
    :	'package' qualifiedName ';'
    ;

importDeclaration
    :	'import' 'static'? Identifier ('.' Identifier)* ('.' '*')? ';'
    ;

typeDeclaration
    :	classOrInterfaceDeclaration
    |   ';'
    ;

classOrInterfaceDeclaration
    :	modifier* (classDeclaration | interfaceDeclaration)
    ;

classDeclaration
    :	normalClassDeclaration
    |   enumDeclaration
    ;

normalClassDeclaration
    :	'class' Identifier (typeParameters)?
        ('extends' type)?
        ('implements' typeList)?
        classBody
    ;

typeParameters
    :	'<' typeParameter (',' typeParameter)* '>'
    ;

typeParameter
    :	Identifier ('extends' bound)?
    ;

bound
    :	type ('&' type)*
    ;

enumDeclaration
    :	ENUM Identifier ('implements' typeList)? enumBody
    ;

enumBody
    :	'{' enumConstants? ','? enumBodyDeclarations? '}'
    ;

enumConstants
    :	enumConstant (',' enumConstant)*
    ;

enumConstant
    :	annotations? Identifier (arguments)? (classBody)?
    ;

enumBodyDeclarations
    :	';' (classBodyDeclaration)*
    ;

interfaceDeclaration
    :	normalInterfaceDeclaration
        | annotationTypeDeclaration
    ;

normalInterfaceDeclaration
    :	'interface' Identifier typeParameters? ('extends' typeList)? interfaceBody
    ;

typeList
    :	type (',' type)*
    ;

classBody
    :	'{' classBodyDeclaration* '}'
    ;

interfaceBody
    :	'{' interfaceBodyDeclaration* '}'
    ;

classBodyDeclaration
    :	';'
    |	'static'? block
    |	modifier* memberDecl
    ;

memberDecl
    :	genericMethodOrConstructorDecl
    |	methodDeclaration
    |	fieldDeclaration
    |	'void' Identifier voidMethodDeclaratorRest
    |	Identifier constructorDeclaratorRest
    |	interfaceDeclaration
    |	classDeclaration
    ;

genericMethodOrConstructorDecl
    :	typeParameters genericMethodOrConstructorRest
    ;

genericMethodOrConstructorRest
    :	(type | 'void') Identifier methodDeclaratorRest
    |	Identifier constructorDeclaratorRest
    ;

methodDeclaration
scope VarDecl;
    :	type Identifier methodDeclaratorRest
    ;

fieldDeclaration
    :	type variableDeclarators ';'
    ;

interfaceBodyDeclaration
    :	modifier* interfaceMemberDecl
    |   ';'
    ;

interfaceMemberDecl
    :	interfaceMethodOrFieldDecl
    |   interfaceGenericMethodDecl
    |   'void' Identifier voidInterfaceMethodDeclaratorRest
    |   interfaceDeclaration
    |   classDeclaration
    ;

interfaceMethodOrFieldDecl
    :	type Identifier interfaceMethodOrFieldRest
    ;

interfaceMethodOrFieldRest
    :	constantDeclaratorsRest ';'
    |	interfaceMethodDeclaratorRest
    ;

methodDeclaratorRest
    :	formalParameters ('[' ']')*
        ('throws' qualifiedNameList)?
        (   methodBody
        |   ';'
        )
    ;

voidMethodDeclaratorRest
    :	formalParameters ('throws' qualifiedNameList)?
        (   methodBody
        |   ';'
        )
    ;

interfaceMethodDeclaratorRest
    :	formalParameters ('[' ']')* ('throws' qualifiedNameList)? ';'
    ;

interfaceGenericMethodDecl
    :	typeParameters (type | 'void') Identifier
        interfaceMethodDeclaratorRest
    ;

voidInterfaceMethodDeclaratorRest
    :	formalParameters ('throws' qualifiedNameList)? ';'
    ;

constructorDeclaratorRest
    :	formalParameters ('throws' qualifiedNameList)? methodBody
    ;

constantDeclarator
    :	Identifier constantDeclaratorRest
    ;

variableDeclarators
    :	variableDeclarator (',' variableDeclarator)*
    ;

variableDeclarator
    scope {
        JavaLocalDeclarationDescr.IdentifierDescr ident;
    }
    @init {
        $variableDeclarator::ident = new JavaLocalDeclarationDescr.IdentifierDescr();
    }
    @after {
            if( $VarDecl::descr != null ) {
                $VarDecl::descr.addIdentifier( $variableDeclarator::ident );
            }
    }
    :	id=Identifier rest=variableDeclaratorRest
        {
            $variableDeclarator::ident.setIdentifier( $id.text );
            $variableDeclarator::ident.setStart( ((CommonToken)$id).getStartIndex() - 1 );
            if( $rest.stop != null ) {
                   $variableDeclarator::ident.setEnd( ((CommonToken)$rest.stop).getStopIndex() );
            }
        }
    ;

variableDeclaratorRest
    :	('[' ']')+ ('=' variableInitializer)?
    |	'=' variableInitializer
    |
    ;

constantDeclaratorsRest
    :   constantDeclaratorRest (',' constantDeclarator)*
    ;

constantDeclaratorRest
    :	('[' ']')* '=' variableInitializer
    ;

variableDeclaratorId
    :	Identifier ('[' ']')*
    ;

variableInitializer
    :	arrayInitializer
    |   expression
    ;

arrayInitializer
    :	'{' (variableInitializer (',' variableInitializer)* (',')? )? '}'
    ;

modifier
    :   annotation
    |   'public'
    |   'protected'
    |   'private'
    |   'static'
    |   'abstract'
    |   'final'
    |   'native'
    |   'synchronized'
    |   'transient'
    |   'volatile'
    |   'strictfp'
    ;

packageOrTypeName
    :	Identifier ('.' Identifier)*
    ;

enumConstantName
    :   Identifier
    ;

typeName
    :   Identifier
    |   packageOrTypeName '.' Identifier
    ;

type
    :	Identifier (typeArguments)? ('.' Identifier (typeArguments)? )* ('[' ']')*
    |	primitiveType ('[' ']')*
    ;

primitiveType
    :   'boolean'
    |	'char'
    |	'byte'
    |	'short'
    |	'int'
    |	'long'
    |	'float'
    |	'double'
    ;

variableModifier
    :	'final'
    |   annotation
    ;

typeArguments
    :	'<' typeArgument (',' typeArgument)* '>'
    ;

typeArgument
    :	type
    |	'?' (('extends' | 'super') type)?
    ;

qualifiedNameList
    :	qualifiedName (',' qualifiedName)*
    ;

formalParameters
    :	'(' formalParameterDecls? ')'
    ;

formalParameterDecls
    :	variableModifier* type formalParameterDeclsRest?
    ;

formalParameterDeclsRest
    :	variableDeclaratorId (',' formalParameterDecls)?
    |   '...' variableDeclaratorId
    ;

methodBody
    :	block
    ;

qualifiedName
    :	Identifier ('.' Identifier)*
    ;

literal	
    :   integerLiteral
    |   FloatingPointLiteral
    |   CharacterLiteral
    |   StringLiteral
    |   booleanLiteral
    |   'null'
    ;

integerLiteral
    :   HexLiteral
    |   OctalLiteral
    |   DecimalLiteral
    ;

booleanLiteral
    :   'true'
    |   'false'
    ;

// ANNOTATIONS

annotations
    :	annotation+
    ;

annotation
    :	'@' annotationName ('(' elementValuePairs? ')')?
    ;

annotationName
    : Identifier ('.' Identifier)*
    ;

elementValuePairs
    : elementValuePair (',' elementValuePair)*
    ;

elementValuePair
    : (Identifier '=')? elementValue
    ;

elementValue
    :	conditionalExpression
    |   annotation
    |   elementValueArrayInitializer
    ;

elementValueArrayInitializer
    :	'{' (elementValue (',' elementValue )*)? '}'
    ;

annotationTypeDeclaration
    :	'@' 'interface' Identifier annotationTypeBody
    ;

annotationTypeBody
    :	'{' (annotationTypeElementDeclarations)? '}'
    ;

annotationTypeElementDeclarations
    :	(annotationTypeElementDeclaration) (annotationTypeElementDeclaration)*
    ;

annotationTypeElementDeclaration
    :	(modifier)* annotationTypeElementRest
    ;

annotationTypeElementRest
    :	type annotationMethodOrConstantRest ';'
    |   classDeclaration ';'?
    |   interfaceDeclaration ';'?
    |   enumDeclaration ';'?
    |   annotationTypeDeclaration ';'?
    ;

annotationMethodOrConstantRest
    :	annotationMethodRest
    |   annotationConstantRest
    ;

annotationMethodRest
     :	Identifier '(' ')' (defaultValue)?
     ;

annotationConstantRest
scope VarDecl;
     :	variableDeclarators
     ;

defaultValue
     :	'default' elementValue
     ;

// STATEMENTS / BLOCKS

block
        @init {
            increaseLevel();
        }
        @after {
            if( localVariableLevel <= 1 ) {
                // this is the top level block, so set the top level declarations
                rootBlockDescr.setInScopeLocalVars( getLocalDeclarations() );
            }
            decreaseLevel();
        }
    :	'{' blockStatement* '}'
    ;

blockStatement
    :	localVariableDeclaration
    |	classOrInterfaceDeclaration
        |	statement
    ;

localVariableDeclaration
scope VarDecl;
        @init {
            $VarDecl::descr = new JavaLocalDeclarationDescr();
        }
        @after {
            addLocalDeclaration( $VarDecl::descr );
        }
    :
    ( variableModifier
        {
            $VarDecl::descr.updateStart( ((CommonToken)$variableModifier.start).getStartIndex() - 1 );
            $VarDecl::descr.addModifier( $variableModifier.text );
        }
    )*
    type
        {
            $VarDecl::descr.updateStart( ((CommonToken)$type.start).getStartIndex() - 1 );
            $VarDecl::descr.setType( $type.text );
            $VarDecl::descr.setEnd( ((CommonToken)$type.stop).getStopIndex() );
        }
    variableDeclarators ';'
    ;

statement
    : block
    | 'assert' expression (':' expression)? ';'
    | ifStatement
    | forStatement //'for' '(' forControl ')' statement
    | whileStatement//'while' parExpression statement
    | 'do' statement 'while' parExpression ';'
    
    | tryStatement
      
    | 'switch' parExpression '{' switchBlockStatementGroups '}'
    | 'synchronized' parExpression block
    | 'return' expression? ';'
    |  throwStatement
    | 'break' Identifier? ';'
    | 'continue' Identifier? ';'
    // adding support to drools modify block
    | modifyStatement | updateStatement | retractStatement | deleteStatement | insertStatement
    | ';'
    | statementExpression ';'
    | Identifier ':' statement
    ;
    

throwStatement
    @init {
        JavaThrowBlockDescr d = null;
    }
    : s='throw'
    expression
    c = ';'
        {
        d = new JavaThrowBlockDescr( );
        d.setStart( ((CommonToken)$s).getStartIndex() );
        d.setTextStart( ((CommonToken)$expression.start).getStartIndex() );        
        this.addBlockDescr( d );
        d.setEnd( ((CommonToken)$c).getStopIndex() ); 
        }
    ;
    
ifStatement
    @init {
         JavaIfBlockDescr id = null;
         JavaElseBlockDescr ed = null;         
    }
    :     
    //    | 'if' parExpression statement (options {k=1;}:'else' statement)?
    s='if' parExpression
    {
        increaseLevel();
        id = new JavaIfBlockDescr();
        id.setStart( ((CommonToken)$s).getStartIndex() );  pushContainerBlockDescr(id, true); 
    }    
    x=statement 
    {
        decreaseLevel();
        id.setTextStart(((CommonToken)$x.start).getStartIndex() );
        id.setEnd(((CommonToken)$x.stop).getStopIndex() ); popContainerBlockDescr(); 
    }
    
    (
     y='else'  ('if' parExpression )?
    {
        increaseLevel();
        ed = new JavaElseBlockDescr();
        ed.setStart( ((CommonToken)$y).getStartIndex() );  pushContainerBlockDescr(ed, true); 
    }             
     z=statement
    {
        decreaseLevel();
        ed.setTextStart(((CommonToken)$z.start).getStartIndex() );
        ed.setEnd(((CommonToken)$z.stop).getStopIndex() ); popContainerBlockDescr();               
    })*       
    ;      
       
forStatement
options {k=3;}
scope VarDecl;
    @init {
         JavaForBlockDescr fd = null;
         increaseLevel();
         $VarDecl::descr = new JavaLocalDeclarationDescr();
    }
    @after {
         addLocalDeclaration( $VarDecl::descr );
         decreaseLevel();
    }
    : 
    x='for' y='('
    {   fd = new JavaForBlockDescr( ); 
        fd.setStart( ((CommonToken)$x).getStartIndex() ); pushContainerBlockDescr(fd, true);    
        fd.setStartParen( ((CommonToken)$y).getStartIndex() );            
    }      
    (    
        ( ( variableModifier
            {
                $VarDecl::descr.updateStart( ((CommonToken)$variableModifier.start).getStartIndex() - 1 );
                $VarDecl::descr.addModifier( $variableModifier.text );
            }
          )*
          type
          {
            $VarDecl::descr.updateStart( ((CommonToken)$type.start).getStartIndex() - 1 );
            $VarDecl::descr.setType( $type.text );
            $VarDecl::descr.setEnd( ((CommonToken)$type.stop).getStopIndex() );
          }
          id=Identifier 
          {
            JavaLocalDeclarationDescr.IdentifierDescr ident = new JavaLocalDeclarationDescr.IdentifierDescr();
            ident.setIdentifier( $id.text );
            ident.setStart( ((CommonToken)$id).getStartIndex() - 1 );
            ident.setEnd( ((CommonToken)$id).getStopIndex() );
            $VarDecl::descr.addIdentifier( ident );
          }
          z=':' expression
          {
             fd.setInitEnd( ((CommonToken)$z).getStartIndex() );        
          })
        |  
        (forInit? z=';' expression? ';' forUpdate?        
         {
             fd.setInitEnd( ((CommonToken)$z).getStartIndex() );        
          })

    
      )    
     ')' bs=statement
    {                
        fd.setTextStart(((CommonToken)$bs.start).getStartIndex() );
        fd.setEnd(((CommonToken)$bs.stop).getStopIndex() ); popContainerBlockDescr();     
    }
    ;       

whileStatement
    @init {
         JavaWhileBlockDescr wd = null;         
    }
    :         
    // 'while' parExpression statement    
    s='while' parExpression 
    {   wd = new JavaWhileBlockDescr( ); wd.setStart( ((CommonToken)$s).getStartIndex() ); pushContainerBlockDescr(wd, true);    
    } 
    bs= statement
    {                
        wd.setTextStart(((CommonToken)$bs.start).getStartIndex() );
        wd.setEnd(((CommonToken)$bs.stop).getStopIndex() ); popContainerBlockDescr();     
    }    
    ;  
    
tryStatement
    @init {
         JavaTryBlockDescr td = null;
         JavaCatchBlockDescr cd = null;
         JavaFinalBlockDescr fd = null;
         
    }
    :     
    s='try' 
    {   increaseLevel();
        td = new JavaTryBlockDescr( ); td.setStart( ((CommonToken)$s).getStartIndex() ); pushContainerBlockDescr(td, true);    
    } bs='{' blockStatement*
    {
                
        td.setTextStart( ((CommonToken)$bs).getStartIndex() );        

    } c='}' {td.setEnd( ((CommonToken)$c).getStopIndex() ); decreaseLevel(); popContainerBlockDescr();    }
    
 
    (s='catch' '(' formalParameter ')' 
     {  increaseLevel();
        cd = new JavaCatchBlockDescr( $formalParameter.text );
        cd.setClauseStart( ((CommonToken)$formalParameter.start).getStartIndex() ); 
        cd.setStart( ((CommonToken)$s).getStartIndex() );  pushContainerBlockDescr(cd, false);
     } bs='{' blockStatement*
     { 
        cd.setTextStart( ((CommonToken)$bs).getStartIndex() );
        td.addCatch( cd );        
     }  c='}' {cd.setEnd( ((CommonToken)$c).getStopIndex() ); decreaseLevel(); popContainerBlockDescr(); } )* 
     
     
     
     (s='finally' 
     {  increaseLevel();
        fd = new JavaFinalBlockDescr( ); fd.setStart( ((CommonToken)$s).getStartIndex() ); pushContainerBlockDescr(fd, false);
     } bs='{' blockStatement*
      {
        fd.setTextStart( ((CommonToken)$bs).getStartIndex() );        
        td.setFinally( fd );         
      }  c='}' {fd.setEnd( ((CommonToken)$c).getStopIndex() ); decreaseLevel(); popContainerBlockDescr(); } )?     
    ;    

modifyStatement
    @init {
        JavaModifyBlockDescr d = null;
    }
    : s='modify' parExpression
    {
        d = new JavaModifyBlockDescr( $parExpression.text );
        d.setStart( ((CommonToken)$s).getStartIndex() );
        d.setInScopeLocalVars( getLocalDeclarations() );
        this.addBlockDescr( d );

    }
    '{' ( e = expression { d.getExpressions().add( $e.text ); }
           (',' e=expression { d.getExpressions().add( $e.text ); } )*
        )?
    c='}'
        {
            d.setEnd( ((CommonToken)$c).getStopIndex() ); 
        }
    ;

updateStatement
    : s='update' '('
    expression
    c = ')'
        {
        JavaStatementBlockDescr d = new JavaStatementBlockDescr( $expression.text, JavaBlockDescr.BlockType.UPDATE );
        d.setStart( ((CommonToken)$s).getStartIndex() );
        this.addBlockDescr( d );
        d.setEnd( ((CommonToken)$c).getStopIndex() ); 
        }
    ;
    
retractStatement
    : s='retract' '('
    expression
    c = ')'
        {	
        JavaStatementBlockDescr d = new JavaStatementBlockDescr( $expression.text, JavaBlockDescr.BlockType.DELETE );
        d.setStart( ((CommonToken)$s).getStartIndex() );
        this.addBlockDescr( d );
        d.setEnd( ((CommonToken)$c).getStopIndex() );
    }
    ;

deleteStatement
    : s='delete' '('
    expression
    c = ')'
        {
        JavaStatementBlockDescr d = new JavaStatementBlockDescr( $expression.text, JavaBlockDescr.BlockType.DELETE );
        d.setStart( ((CommonToken)$s).getStartIndex() );
        this.addBlockDescr( d );
        d.setEnd( ((CommonToken)$c).getStopIndex() );
    }
    ;

insertStatement
    : s='insert' '('
    expression
    c = ')'
        {
        JavaStatementBlockDescr d = new JavaStatementBlockDescr( $expression.text, JavaBlockDescr.BlockType.INSERT );
        d.setStart( ((CommonToken)$s).getStartIndex() );
        this.addBlockDescr( d );
        d.setEnd( ((CommonToken)$c).getStopIndex() );
    }
    ;

epStatement
    @init {
        JavaInterfacePointsDescr d = null;
    }
        : 
        ( s='exitPoints' '[' id=StringLiteral c=']' 
        {
            d = new JavaInterfacePointsDescr( $id.text );
            d.setType( JavaBlockDescr.BlockType.EXIT );
            d.setStart( ((CommonToken)$s).getStartIndex() );
            d.setEnd( ((CommonToken)$c).getStopIndex() ); 
            this.addBlockDescr( d );
        }
        |  s='entryPoints' '[' id=StringLiteral c=']' 
        {
            d = new JavaInterfacePointsDescr( $id.text );
            d.setType( JavaBlockDescr.BlockType.ENTRY );
            d.setStart( ((CommonToken)$s).getStartIndex() );
            d.setEnd( ((CommonToken)$c).getStopIndex() ); 
            this.addBlockDescr( d );
        }
        |  s='channels' '[' id=StringLiteral c=']' 
        {
            d = new JavaInterfacePointsDescr( $id.text );
            d.setType( JavaBlockDescr.BlockType.CHANNEL );
            d.setStart( ((CommonToken)$s).getStartIndex() );
            d.setEnd( ((CommonToken)$c).getStopIndex() ); 
            this.addBlockDescr( d );
        }
        ) 
        ;	

formalParameter
    :	variableModifier* type variableDeclaratorId
    ;

switchBlockStatementGroups
    :	(switchBlockStatementGroup)*
    ;

switchBlockStatementGroup
    :	switchLabel blockStatement*
    ;

switchLabel
    :	'case' constantExpression ':'
    |   'case' enumConstantName ':'
    |   'default' ':'
    ;

moreStatementExpressions
    :	(',' statementExpression)*
    ;

forControl
options {k=3;} // be efficient for common case: for (ID ID : ID) ...
scope VarDecl;
        @init {
            increaseLevel();
            $VarDecl::descr = new JavaLocalDeclarationDescr();
        }
        @after {
            addLocalDeclaration( $VarDecl::descr );
            decreaseLevel();
        }
    :	forVarControl
    |	forInit? ';' expression? ';' forUpdate?
    ;

forInit
    :	( variableModifier
            {
                $VarDecl::descr.updateStart( ((CommonToken)$variableModifier.start).getStartIndex() - 1 );
                $VarDecl::descr.addModifier( $variableModifier.text );
            }
        )*
        type
        {
            $VarDecl::descr.updateStart( ((CommonToken)$type.start).getStartIndex() - 1 );
            $VarDecl::descr.setType( $type.text );
            $VarDecl::descr.setEnd( ((CommonToken)$type.stop).getStopIndex() );
        }
        variableDeclarators
    |	expressionList
    ;

forVarControl
    :	( variableModifier
            {
                $VarDecl::descr.updateStart( ((CommonToken)$variableModifier.start).getStartIndex() - 1 );
                $VarDecl::descr.addModifier( $variableModifier.text );
            }
        )*
        type
        {
            $VarDecl::descr.updateStart( ((CommonToken)$type.start).getStartIndex() - 1 );
            $VarDecl::descr.setType( $type.text );
            $VarDecl::descr.setEnd( ((CommonToken)$type.stop).getStopIndex() );
        }
        id=Identifier 
        {
            JavaLocalDeclarationDescr.IdentifierDescr ident = new JavaLocalDeclarationDescr.IdentifierDescr();
            ident.setIdentifier( $id.text );
            ident.setStart( ((CommonToken)$id).getStartIndex() - 1 );
            ident.setEnd( ((CommonToken)$id).getStopIndex() );
            $VarDecl::descr.addIdentifier( ident );
        }
        ':' expression
    ;

forUpdate
    :	expressionList
    ;

// EXPRESSIONS

parExpression
    :	'(' expression ')'
    ;

expressionList
    :   expression (',' expression)*
    ;

statementExpression
    :	expression
    ;

constantExpression
    :	expression
    ;

expression
    :	assignmentExpression
    |   conditionalExpression (assignmentOperator expression)?
    ;

assignmentExpression
    :	id=conditionalExpression '=' expression
        {
            this.addAssignment( $id.text );
        }
    ;

assignmentOperator
    :	'+='
    |   '-='
    |   '*='
    |   '/='
    |   '&='
    |   '|='
    |   '^='
    |   '%='
    |   '<' '<' '='
    |   '>' '>' '='
    |   '>' '>' '>' '='
    ;

conditionalExpression
    :   conditionalOrExpression ( '?' expression ':' expression )?
    ;

conditionalOrExpression
    :   conditionalAndExpression ( '||' conditionalAndExpression )*
    ;

conditionalAndExpression
    :   inclusiveOrExpression ( '&&' inclusiveOrExpression )*
    ;

inclusiveOrExpression
    :   exclusiveOrExpression ( '|' exclusiveOrExpression )*
    ;

exclusiveOrExpression
    :   andExpression ( '^' andExpression )*
    ;

andExpression
    :   equalityExpression ( '&' equalityExpression )*
    ;

equalityExpression
    :   instanceOfExpression ( ('==' | '!=') instanceOfExpression )*
    ;

instanceOfExpression
    :   relationalExpression ('instanceof' type)?
    ;

relationalExpression
    :   shiftExpression ( relationalOp shiftExpression )*
    ;

relationalOp
    :	('<' '=' | '>' '=' | '<' | '>')
    ;

shiftExpression
    :   additiveExpression ( shiftOp additiveExpression )*
    ;

        // TODO: need a sem pred to check column on these >>>
shiftOp
    :	('<' '<' | '>' '>' '>' | '>' '>')
    ;


additiveExpression
    :   multiplicativeExpression ( ('+' | '-') multiplicativeExpression )*
    ;

multiplicativeExpression
    :   unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
    ;

unaryExpression
    :   '+' unaryExpression
    |	'-' unaryExpression
    |   '++' primary
    |   '--' primary
    |   unaryExpressionNotPlusMinus
    ;

unaryExpressionNotPlusMinus
    :   '~' unaryExpression
    | 	'!' unaryExpression
    |   castExpression
    |   primary selector* ('++'|'--')?
    ;

castExpression
    :  '(' primitiveType ')' unaryExpression
    |  '(' (type | expression) ')' unaryExpressionNotPlusMinus
    ;

primary
    :	parExpression
    |   nonWildcardTypeArguments
        (explicitGenericInvocationSuffix | 'this' arguments)
    |   'this' ('.' Identifier)* (identifierSuffix)?
    |   'super' superSuffix
    |   epStatement ('.' methodName)* (identifierSuffix)?
    |   literal
    |   'new' creator
    |   i=Identifier { if( ! "(".equals( input.LT(1) == null ? "" : input.LT(1).getText() ) ) identifiers.add( $i.text );  } ('.' methodName)* (identifierSuffix)?
    |   primitiveType ('[' ']')* '.' 'class'
    |   'void' '.' 'class'
    ;

methodName
    : Identifier | 'insert' | 'update' | 'modify' | 'retract' | 'delete'
    ;

identifierSuffix
    :	('[' ']')+ '.' 'class'
    |	('[' expression ']')+ // can also be matched by selector, but do here
    |   arguments
    |   '.' 'class'
    |   '.' explicitGenericInvocation
    |   '.' 'this'
    |   '.' 'super' arguments
    |   '.' 'new' (nonWildcardTypeArguments)? innerCreator
    ;

creator
    :	nonWildcardTypeArguments? createdName
        (arrayCreatorRest | classCreatorRest)
    ;

createdName
    :	Identifier typeArguments?
        ('.' Identifier typeArguments?)*
    |	primitiveType
    ;

innerCreator
    :	Identifier classCreatorRest
    ;

arrayCreatorRest
    :	'['
        (   ']' ('[' ']')* arrayInitializer
        |   expression ']' ('[' expression ']')* ('[' ']')*
        )
    ;

classCreatorRest
    :	arguments classBody?
    ;

explicitGenericInvocation
    :	nonWildcardTypeArguments explicitGenericInvocationSuffix
    ;

nonWildcardTypeArguments
    :	'<' typeList '>'
    ;

explicitGenericInvocationSuffix
    :	'super' superSuffix
    |   Identifier arguments
    ;

selector
    :	'.' methodName (arguments)?
    |   '.' 'this'
    |   '.' 'super' superSuffix
    |   '.' 'new' (nonWildcardTypeArguments)? innerCreator
    |   '[' expression ']'
    ;

superSuffix
    :	arguments
    |   '.' Identifier (arguments)?
    ;
    
    arguments
    :	'(' expressionList? ')'
    ;

// LEXER

HexLiteral : '0' ('x'|'X') HexDigit+ IntegerTypeSuffix? ;

DecimalLiteral : ('0' | NonZeroDigit (Digits? | Underscores Digits)) IntegerTypeSuffix? ;

OctalLiteral : '0' ('0'..'7')+ IntegerTypeSuffix? ;

fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
IntegerTypeSuffix : ('l'|'L') ;

FloatingPointLiteral
    :   Digits '.' Digits? Exponent? FloatTypeSuffix?
    |   '.' Digits Exponent? FloatTypeSuffix?
    |   Digits Exponent FloatTypeSuffix?
    |   Digits Exponent? FloatTypeSuffix
    ;

fragment
Digits: Digit (DigitOrUnderscore* Digit)? ;

fragment
Digit: '0' | NonZeroDigit ;

fragment
NonZeroDigit: ('1'..'9') ;

fragment
DigitOrUnderscore: Digit | '_' ;

fragment
Underscores: '_'+ ;

fragment
Exponent : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
FloatTypeSuffix : ('f'|'F'|'d'|'D') ;

CharacterLiteral
    :   '\'' ( EscapeSequence | ~('\''|'\\') ) '\''
    ;

StringLiteral
    :  '"' ( EscapeSequence | ~('\\'|'"') )* '"'
    ;

fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UnicodeEscape
    |   OctalEscape
    ;

fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

ENUM:	'enum' {if ( !enumIsKeyword ) $type=Identifier;}
    ;

Identifier
	:	JavaLetter JavaLetterOrDigit*
	;

fragment
JavaLetter
	:	('a'..'z'|'A'..'Z'|'$'|'_')
	|   {Character.isJavaIdentifierStart(input.LA(1))}?
	    ~('\u0000'..'\u007F' | '\uD800'..'\uDBFF')
    |   {Character.isJavaIdentifierStart(Character.toCodePoint((char)input.LA(-1), (char)input.LA(1)))}?
        ('\uD800'..'\uDBFF') ('\uDC00'..'\uDFFF')
	;

fragment
JavaLetterOrDigit
	:	('a'..'z'|'A'..'Z'|'0'..'9'|'$'|'_')
    |   {Character.isJavaIdentifierPart(input.LA(1))}?
        ~('\u0000'..'\u007F' | '\uD800'..'\uDBFF')
    |   {Character.isJavaIdentifierPart(Character.toCodePoint((char)input.LA(-1), (char)input.LA(1)))}?
        ('\uD800'..'\uDBFF') ('\uDC00'..'\uDFFF')
    ;


WS  :  (' '|'\r'|'\t'|'\u000C'|'\n') {$channel=HIDDEN;}
    ;

COMMENT
    :   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;

LINE_COMMENT
    : '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    ;
