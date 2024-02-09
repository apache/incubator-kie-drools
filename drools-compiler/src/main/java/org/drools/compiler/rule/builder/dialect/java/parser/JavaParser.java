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
// $ANTLR 3.5.2 src/main/resources/org/drools/compiler/semantics/java/parser/Java.g 2020-01-03 12:48:30

    package org.drools.compiler.rule.builder.dialect.java.parser;
    import java.util.Iterator;
    import java.util.LinkedList;
    import java.util.Set;
    import java.util.HashSet;
    import java.util.Collections;

    import org.drools.compiler.rule.builder.dialect.java.parser.JavaLocalDeclarationDescr;
    import org.drools.compiler.rule.builder.dialect.java.parser.JavaRootBlockDescr;
    import org.drools.compiler.rule.builder.dialect.java.parser.JavaContainerBlockDescr;
    import org.drools.compiler.rule.builder.dialect.java.parser.JavaBlockDescr;
    


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

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
@SuppressWarnings("all")
public class JavaParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "COMMENT", "CharacterLiteral", 
		"DecimalLiteral", "Digit", "DigitOrUnderscore", "Digits", "ENUM", "EscapeSequence", 
		"Exponent", "FloatTypeSuffix", "FloatingPointLiteral", "HexDigit", "HexLiteral", 
		"Identifier", "IntegerTypeSuffix", "JavaLetter", "JavaLetterOrDigit", 
		"LINE_COMMENT", "NonZeroDigit", "OctalEscape", "OctalLiteral", "StringLiteral", 
		"Underscores", "UnicodeEscape", "WS", "'!'", "'!='", "'%'", "'%='", "'&&'", 
		"'&'", "'&='", "'('", "')'", "'*'", "'*='", "'+'", "'++'", "'+='", "','", 
		"'-'", "'--'", "'-='", "'.'", "'...'", "'/'", "'/='", "':'", "';'", "'<'", 
		"'='", "'=='", "'>'", "'?'", "'@'", "'['", "']'", "'^'", "'^='", "'abstract'", 
		"'assert'", "'boolean'", "'break'", "'byte'", "'case'", "'catch'", "'channels'", 
		"'char'", "'class'", "'continue'", "'default'", "'delete'", "'do'", "'double'", 
		"'else'", "'entryPoints'", "'exitPoints'", "'extends'", "'false'", "'final'", 
		"'finally'", "'float'", "'for'", "'if'", "'implements'", "'import'", "'insert'", 
		"'instanceof'", "'int'", "'interface'", "'long'", "'modify'", "'native'", 
		"'new'", "'null'", "'package'", "'private'", "'protected'", "'public'", 
		"'retract'", "'return'", "'short'", "'static'", "'strictfp'", "'super'", 
		"'switch'", "'synchronized'", "'this'", "'throw'", "'throws'", "'transient'", 
		"'true'", "'try'", "'update'", "'void'", "'volatile'", "'while'", "'{'", 
		"'|'", "'|='", "'||'", "'}'", "'~'"
	};
	public static final int EOF=-1;
	public static final int T__29=29;
	public static final int T__30=30;
	public static final int T__31=31;
	public static final int T__32=32;
	public static final int T__33=33;
	public static final int T__34=34;
	public static final int T__35=35;
	public static final int T__36=36;
	public static final int T__37=37;
	public static final int T__38=38;
	public static final int T__39=39;
	public static final int T__40=40;
	public static final int T__41=41;
	public static final int T__42=42;
	public static final int T__43=43;
	public static final int T__44=44;
	public static final int T__45=45;
	public static final int T__46=46;
	public static final int T__47=47;
	public static final int T__48=48;
	public static final int T__49=49;
	public static final int T__50=50;
	public static final int T__51=51;
	public static final int T__52=52;
	public static final int T__53=53;
	public static final int T__54=54;
	public static final int T__55=55;
	public static final int T__56=56;
	public static final int T__57=57;
	public static final int T__58=58;
	public static final int T__59=59;
	public static final int T__60=60;
	public static final int T__61=61;
	public static final int T__62=62;
	public static final int T__63=63;
	public static final int T__64=64;
	public static final int T__65=65;
	public static final int T__66=66;
	public static final int T__67=67;
	public static final int T__68=68;
	public static final int T__69=69;
	public static final int T__70=70;
	public static final int T__71=71;
	public static final int T__72=72;
	public static final int T__73=73;
	public static final int T__74=74;
	public static final int T__75=75;
	public static final int T__76=76;
	public static final int T__77=77;
	public static final int T__78=78;
	public static final int T__79=79;
	public static final int T__80=80;
	public static final int T__81=81;
	public static final int T__82=82;
	public static final int T__83=83;
	public static final int T__84=84;
	public static final int T__85=85;
	public static final int T__86=86;
	public static final int T__87=87;
	public static final int T__88=88;
	public static final int T__89=89;
	public static final int T__90=90;
	public static final int T__91=91;
	public static final int T__92=92;
	public static final int T__93=93;
	public static final int T__94=94;
	public static final int T__95=95;
	public static final int T__96=96;
	public static final int T__97=97;
	public static final int T__98=98;
	public static final int T__99=99;
	public static final int T__100=100;
	public static final int T__101=101;
	public static final int T__102=102;
	public static final int T__103=103;
	public static final int T__104=104;
	public static final int T__105=105;
	public static final int T__106=106;
	public static final int T__107=107;
	public static final int T__108=108;
	public static final int T__109=109;
	public static final int T__110=110;
	public static final int T__111=111;
	public static final int T__112=112;
	public static final int T__113=113;
	public static final int T__114=114;
	public static final int T__115=115;
	public static final int T__116=116;
	public static final int T__117=117;
	public static final int T__118=118;
	public static final int T__119=119;
	public static final int T__120=120;
	public static final int T__121=121;
	public static final int T__122=122;
	public static final int T__123=123;
	public static final int T__124=124;
	public static final int T__125=125;
	public static final int T__126=126;
	public static final int COMMENT=4;
	public static final int CharacterLiteral=5;
	public static final int DecimalLiteral=6;
	public static final int Digit=7;
	public static final int DigitOrUnderscore=8;
	public static final int Digits=9;
	public static final int ENUM=10;
	public static final int EscapeSequence=11;
	public static final int Exponent=12;
	public static final int FloatTypeSuffix=13;
	public static final int FloatingPointLiteral=14;
	public static final int HexDigit=15;
	public static final int HexLiteral=16;
	public static final int Identifier=17;
	public static final int IntegerTypeSuffix=18;
	public static final int JavaLetter=19;
	public static final int JavaLetterOrDigit=20;
	public static final int LINE_COMMENT=21;
	public static final int NonZeroDigit=22;
	public static final int OctalEscape=23;
	public static final int OctalLiteral=24;
	public static final int StringLiteral=25;
	public static final int Underscores=26;
	public static final int UnicodeEscape=27;
	public static final int WS=28;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators

	protected static class VarDecl_scope {
		org.drools.compiler.rule.builder.dialect.java.parser.JavaLocalDeclarationDescr descr;
	}
	protected Stack<VarDecl_scope> VarDecl_stack = new Stack<VarDecl_scope>();


	public JavaParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public JavaParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
		this.state.ruleMemo = new HashMap[439+1];


	}

	@Override public String[] getTokenNames() { return JavaParser.tokenNames; }
	@Override public String getGrammarFileName() { return "src/main/resources/org/drools/compiler/semantics/java/parser/Java.g"; }


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



	// $ANTLR start "compilationUnit"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:282:1: compilationUnit : ( annotations )? ( packageDeclaration )? ( importDeclaration )* ( typeDeclaration )* ;
	public final void compilationUnit() throws RecognitionException {
		int compilationUnit_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 1) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:283:5: ( ( annotations )? ( packageDeclaration )? ( importDeclaration )* ( typeDeclaration )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:283:7: ( annotations )? ( packageDeclaration )? ( importDeclaration )* ( typeDeclaration )*
			{
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:283:7: ( annotations )?
			int alt1=2;
			int LA1_0 = input.LA(1);
			if ( (LA1_0==58) ) {
				int LA1_1 = input.LA(2);
				if ( (LA1_1==Identifier) ) {
					int LA1_21 = input.LA(3);
					if ( (synpred1_Java()) ) {
						alt1=1;
					}
				}
			}
			switch (alt1) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:283:7: annotations
					{
					pushFollow(FOLLOW_annotations_in_compilationUnit81);
					annotations();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:284:9: ( packageDeclaration )?
			int alt2=2;
			int LA2_0 = input.LA(1);
			if ( (LA2_0==99) ) {
				alt2=1;
			}
			switch (alt2) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:284:9: packageDeclaration
					{
					pushFollow(FOLLOW_packageDeclaration_in_compilationUnit92);
					packageDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:285:9: ( importDeclaration )*
			loop3:
			while (true) {
				int alt3=2;
				int LA3_0 = input.LA(1);
				if ( (LA3_0==89) ) {
					alt3=1;
				}

				switch (alt3) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:285:9: importDeclaration
					{
					pushFollow(FOLLOW_importDeclaration_in_compilationUnit103);
					importDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop3;
				}
			}

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:286:9: ( typeDeclaration )*
			loop4:
			while (true) {
				int alt4=2;
				int LA4_0 = input.LA(1);
				if ( (LA4_0==ENUM||LA4_0==52||LA4_0==58||LA4_0==63||LA4_0==72||LA4_0==83||LA4_0==93||LA4_0==96||(LA4_0 >= 100 && LA4_0 <= 102)||(LA4_0 >= 106 && LA4_0 <= 107)||LA4_0==110||LA4_0==114||LA4_0==119) ) {
					alt4=1;
				}

				switch (alt4) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:286:9: typeDeclaration
					{
					pushFollow(FOLLOW_typeDeclaration_in_compilationUnit114);
					typeDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop4;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 1, compilationUnit_StartIndex); }

		}
	}
	// $ANTLR end "compilationUnit"



	// $ANTLR start "packageDeclaration"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:289:1: packageDeclaration : 'package' qualifiedName ';' ;
	public final void packageDeclaration() throws RecognitionException {
		int packageDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 2) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:290:5: ( 'package' qualifiedName ';' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:290:7: 'package' qualifiedName ';'
			{
			match(input,99,FOLLOW_99_in_packageDeclaration132); if (state.failed) return;
			pushFollow(FOLLOW_qualifiedName_in_packageDeclaration134);
			qualifiedName();
			state._fsp--;
			if (state.failed) return;
			match(input,52,FOLLOW_52_in_packageDeclaration136); if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 2, packageDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "packageDeclaration"



	// $ANTLR start "importDeclaration"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:293:1: importDeclaration : 'import' ( 'static' )? Identifier ( '.' Identifier )* ( '.' '*' )? ';' ;
	public final void importDeclaration() throws RecognitionException {
		int importDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 3) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:294:5: ( 'import' ( 'static' )? Identifier ( '.' Identifier )* ( '.' '*' )? ';' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:294:7: 'import' ( 'static' )? Identifier ( '.' Identifier )* ( '.' '*' )? ';'
			{
			match(input,89,FOLLOW_89_in_importDeclaration153); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:294:16: ( 'static' )?
			int alt5=2;
			int LA5_0 = input.LA(1);
			if ( (LA5_0==106) ) {
				alt5=1;
			}
			switch (alt5) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:294:16: 'static'
					{
					match(input,106,FOLLOW_106_in_importDeclaration155); if (state.failed) return;
					}
					break;

			}

			match(input,Identifier,FOLLOW_Identifier_in_importDeclaration158); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:294:37: ( '.' Identifier )*
			loop6:
			while (true) {
				int alt6=2;
				int LA6_0 = input.LA(1);
				if ( (LA6_0==47) ) {
					int LA6_1 = input.LA(2);
					if ( (LA6_1==Identifier) ) {
						alt6=1;
					}

				}

				switch (alt6) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:294:38: '.' Identifier
					{
					match(input,47,FOLLOW_47_in_importDeclaration161); if (state.failed) return;
					match(input,Identifier,FOLLOW_Identifier_in_importDeclaration163); if (state.failed) return;
					}
					break;

				default :
					break loop6;
				}
			}

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:294:55: ( '.' '*' )?
			int alt7=2;
			int LA7_0 = input.LA(1);
			if ( (LA7_0==47) ) {
				alt7=1;
			}
			switch (alt7) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:294:56: '.' '*'
					{
					match(input,47,FOLLOW_47_in_importDeclaration168); if (state.failed) return;
					match(input,38,FOLLOW_38_in_importDeclaration170); if (state.failed) return;
					}
					break;

			}

			match(input,52,FOLLOW_52_in_importDeclaration174); if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 3, importDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "importDeclaration"



	// $ANTLR start "typeDeclaration"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:297:1: typeDeclaration : ( classOrInterfaceDeclaration | ';' );
	public final void typeDeclaration() throws RecognitionException {
		int typeDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 4) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:298:5: ( classOrInterfaceDeclaration | ';' )
			int alt8=2;
			int LA8_0 = input.LA(1);
			if ( (LA8_0==ENUM||LA8_0==58||LA8_0==63||LA8_0==72||LA8_0==83||LA8_0==93||LA8_0==96||(LA8_0 >= 100 && LA8_0 <= 102)||(LA8_0 >= 106 && LA8_0 <= 107)||LA8_0==110||LA8_0==114||LA8_0==119) ) {
				alt8=1;
			}
			else if ( (LA8_0==52) ) {
				alt8=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 8, 0, input);
				throw nvae;
			}

			switch (alt8) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:298:7: classOrInterfaceDeclaration
					{
					pushFollow(FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration191);
					classOrInterfaceDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:299:9: ';'
					{
					match(input,52,FOLLOW_52_in_typeDeclaration201); if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 4, typeDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "typeDeclaration"



	// $ANTLR start "classOrInterfaceDeclaration"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:302:1: classOrInterfaceDeclaration : ( modifier )* ( classDeclaration | interfaceDeclaration ) ;
	public final void classOrInterfaceDeclaration() throws RecognitionException {
		int classOrInterfaceDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 5) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:303:5: ( ( modifier )* ( classDeclaration | interfaceDeclaration ) )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:303:7: ( modifier )* ( classDeclaration | interfaceDeclaration )
			{
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:303:7: ( modifier )*
			loop9:
			while (true) {
				int alt9=2;
				int LA9_0 = input.LA(1);
				if ( (LA9_0==58) ) {
					int LA9_4 = input.LA(2);
					if ( (LA9_4==Identifier) ) {
						alt9=1;
					}

				}
				else if ( (LA9_0==63||LA9_0==83||LA9_0==96||(LA9_0 >= 100 && LA9_0 <= 102)||(LA9_0 >= 106 && LA9_0 <= 107)||LA9_0==110||LA9_0==114||LA9_0==119) ) {
					alt9=1;
				}

				switch (alt9) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:303:7: modifier
					{
					pushFollow(FOLLOW_modifier_in_classOrInterfaceDeclaration218);
					modifier();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop9;
				}
			}

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:303:17: ( classDeclaration | interfaceDeclaration )
			int alt10=2;
			int LA10_0 = input.LA(1);
			if ( (LA10_0==ENUM||LA10_0==72) ) {
				alt10=1;
			}
			else if ( (LA10_0==58||LA10_0==93) ) {
				alt10=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 10, 0, input);
				throw nvae;
			}

			switch (alt10) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:303:18: classDeclaration
					{
					pushFollow(FOLLOW_classDeclaration_in_classOrInterfaceDeclaration222);
					classDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:303:37: interfaceDeclaration
					{
					pushFollow(FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration226);
					interfaceDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 5, classOrInterfaceDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "classOrInterfaceDeclaration"



	// $ANTLR start "classDeclaration"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:306:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );
	public final void classDeclaration() throws RecognitionException {
		int classDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:307:5: ( normalClassDeclaration | enumDeclaration )
			int alt11=2;
			int LA11_0 = input.LA(1);
			if ( (LA11_0==72) ) {
				alt11=1;
			}
			else if ( (LA11_0==ENUM) ) {
				alt11=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 11, 0, input);
				throw nvae;
			}

			switch (alt11) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:307:7: normalClassDeclaration
					{
					pushFollow(FOLLOW_normalClassDeclaration_in_classDeclaration244);
					normalClassDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:308:9: enumDeclaration
					{
					pushFollow(FOLLOW_enumDeclaration_in_classDeclaration254);
					enumDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 6, classDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "classDeclaration"



	// $ANTLR start "normalClassDeclaration"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:311:1: normalClassDeclaration : 'class' Identifier ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody ;
	public final void normalClassDeclaration() throws RecognitionException {
		int normalClassDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 7) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:312:5: ( 'class' Identifier ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:312:7: 'class' Identifier ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody
			{
			match(input,72,FOLLOW_72_in_normalClassDeclaration271); if (state.failed) return;
			match(input,Identifier,FOLLOW_Identifier_in_normalClassDeclaration273); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:312:26: ( typeParameters )?
			int alt12=2;
			int LA12_0 = input.LA(1);
			if ( (LA12_0==53) ) {
				alt12=1;
			}
			switch (alt12) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:312:27: typeParameters
					{
					pushFollow(FOLLOW_typeParameters_in_normalClassDeclaration276);
					typeParameters();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:313:9: ( 'extends' type )?
			int alt13=2;
			int LA13_0 = input.LA(1);
			if ( (LA13_0==81) ) {
				alt13=1;
			}
			switch (alt13) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:313:10: 'extends' type
					{
					match(input,81,FOLLOW_81_in_normalClassDeclaration289); if (state.failed) return;
					pushFollow(FOLLOW_type_in_normalClassDeclaration291);
					type();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:314:9: ( 'implements' typeList )?
			int alt14=2;
			int LA14_0 = input.LA(1);
			if ( (LA14_0==88) ) {
				alt14=1;
			}
			switch (alt14) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:314:10: 'implements' typeList
					{
					match(input,88,FOLLOW_88_in_normalClassDeclaration304); if (state.failed) return;
					pushFollow(FOLLOW_typeList_in_normalClassDeclaration306);
					typeList();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			pushFollow(FOLLOW_classBody_in_normalClassDeclaration318);
			classBody();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 7, normalClassDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "normalClassDeclaration"



	// $ANTLR start "typeParameters"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:318:1: typeParameters : '<' typeParameter ( ',' typeParameter )* '>' ;
	public final void typeParameters() throws RecognitionException {
		int typeParameters_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 8) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:319:5: ( '<' typeParameter ( ',' typeParameter )* '>' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:319:7: '<' typeParameter ( ',' typeParameter )* '>'
			{
			match(input,53,FOLLOW_53_in_typeParameters335); if (state.failed) return;
			pushFollow(FOLLOW_typeParameter_in_typeParameters337);
			typeParameter();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:319:25: ( ',' typeParameter )*
			loop15:
			while (true) {
				int alt15=2;
				int LA15_0 = input.LA(1);
				if ( (LA15_0==43) ) {
					alt15=1;
				}

				switch (alt15) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:319:26: ',' typeParameter
					{
					match(input,43,FOLLOW_43_in_typeParameters340); if (state.failed) return;
					pushFollow(FOLLOW_typeParameter_in_typeParameters342);
					typeParameter();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop15;
				}
			}

			match(input,56,FOLLOW_56_in_typeParameters346); if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 8, typeParameters_StartIndex); }

		}
	}
	// $ANTLR end "typeParameters"



	// $ANTLR start "typeParameter"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:322:1: typeParameter : Identifier ( 'extends' bound )? ;
	public final void typeParameter() throws RecognitionException {
		int typeParameter_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:323:5: ( Identifier ( 'extends' bound )? )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:323:7: Identifier ( 'extends' bound )?
			{
			match(input,Identifier,FOLLOW_Identifier_in_typeParameter363); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:323:18: ( 'extends' bound )?
			int alt16=2;
			int LA16_0 = input.LA(1);
			if ( (LA16_0==81) ) {
				alt16=1;
			}
			switch (alt16) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:323:19: 'extends' bound
					{
					match(input,81,FOLLOW_81_in_typeParameter366); if (state.failed) return;
					pushFollow(FOLLOW_bound_in_typeParameter368);
					bound();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 9, typeParameter_StartIndex); }

		}
	}
	// $ANTLR end "typeParameter"



	// $ANTLR start "bound"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:326:1: bound : type ( '&' type )* ;
	public final void bound() throws RecognitionException {
		int bound_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 10) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:327:5: ( type ( '&' type )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:327:7: type ( '&' type )*
			{
			pushFollow(FOLLOW_type_in_bound387);
			type();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:327:12: ( '&' type )*
			loop17:
			while (true) {
				int alt17=2;
				int LA17_0 = input.LA(1);
				if ( (LA17_0==34) ) {
					alt17=1;
				}

				switch (alt17) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:327:13: '&' type
					{
					match(input,34,FOLLOW_34_in_bound390); if (state.failed) return;
					pushFollow(FOLLOW_type_in_bound392);
					type();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop17;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 10, bound_StartIndex); }

		}
	}
	// $ANTLR end "bound"



	// $ANTLR start "enumDeclaration"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:330:1: enumDeclaration : ENUM Identifier ( 'implements' typeList )? enumBody ;
	public final void enumDeclaration() throws RecognitionException {
		int enumDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 11) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:331:5: ( ENUM Identifier ( 'implements' typeList )? enumBody )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:331:7: ENUM Identifier ( 'implements' typeList )? enumBody
			{
			match(input,ENUM,FOLLOW_ENUM_in_enumDeclaration411); if (state.failed) return;
			match(input,Identifier,FOLLOW_Identifier_in_enumDeclaration413); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:331:23: ( 'implements' typeList )?
			int alt18=2;
			int LA18_0 = input.LA(1);
			if ( (LA18_0==88) ) {
				alt18=1;
			}
			switch (alt18) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:331:24: 'implements' typeList
					{
					match(input,88,FOLLOW_88_in_enumDeclaration416); if (state.failed) return;
					pushFollow(FOLLOW_typeList_in_enumDeclaration418);
					typeList();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			pushFollow(FOLLOW_enumBody_in_enumDeclaration422);
			enumBody();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 11, enumDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "enumDeclaration"



	// $ANTLR start "enumBody"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:334:1: enumBody : '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}' ;
	public final void enumBody() throws RecognitionException {
		int enumBody_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 12) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:335:5: ( '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:335:7: '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}'
			{
			match(input,121,FOLLOW_121_in_enumBody439); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:335:11: ( enumConstants )?
			int alt19=2;
			int LA19_0 = input.LA(1);
			if ( (LA19_0==Identifier||LA19_0==58) ) {
				alt19=1;
			}
			switch (alt19) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:335:11: enumConstants
					{
					pushFollow(FOLLOW_enumConstants_in_enumBody441);
					enumConstants();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:335:26: ( ',' )?
			int alt20=2;
			int LA20_0 = input.LA(1);
			if ( (LA20_0==43) ) {
				alt20=1;
			}
			switch (alt20) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:335:26: ','
					{
					match(input,43,FOLLOW_43_in_enumBody444); if (state.failed) return;
					}
					break;

			}

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:335:31: ( enumBodyDeclarations )?
			int alt21=2;
			int LA21_0 = input.LA(1);
			if ( (LA21_0==52) ) {
				alt21=1;
			}
			switch (alt21) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:335:31: enumBodyDeclarations
					{
					pushFollow(FOLLOW_enumBodyDeclarations_in_enumBody447);
					enumBodyDeclarations();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			match(input,125,FOLLOW_125_in_enumBody450); if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 12, enumBody_StartIndex); }

		}
	}
	// $ANTLR end "enumBody"



	// $ANTLR start "enumConstants"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:338:1: enumConstants : enumConstant ( ',' enumConstant )* ;
	public final void enumConstants() throws RecognitionException {
		int enumConstants_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 13) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:339:5: ( enumConstant ( ',' enumConstant )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:339:7: enumConstant ( ',' enumConstant )*
			{
			pushFollow(FOLLOW_enumConstant_in_enumConstants467);
			enumConstant();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:339:20: ( ',' enumConstant )*
			loop22:
			while (true) {
				int alt22=2;
				int LA22_0 = input.LA(1);
				if ( (LA22_0==43) ) {
					int LA22_1 = input.LA(2);
					if ( (LA22_1==Identifier||LA22_1==58) ) {
						alt22=1;
					}

				}

				switch (alt22) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:339:21: ',' enumConstant
					{
					match(input,43,FOLLOW_43_in_enumConstants470); if (state.failed) return;
					pushFollow(FOLLOW_enumConstant_in_enumConstants472);
					enumConstant();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop22;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 13, enumConstants_StartIndex); }

		}
	}
	// $ANTLR end "enumConstants"



	// $ANTLR start "enumConstant"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:342:1: enumConstant : ( annotations )? Identifier ( arguments )? ( classBody )? ;
	public final void enumConstant() throws RecognitionException {
		int enumConstant_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 14) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:343:5: ( ( annotations )? Identifier ( arguments )? ( classBody )? )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:343:7: ( annotations )? Identifier ( arguments )? ( classBody )?
			{
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:343:7: ( annotations )?
			int alt23=2;
			int LA23_0 = input.LA(1);
			if ( (LA23_0==58) ) {
				alt23=1;
			}
			switch (alt23) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:343:7: annotations
					{
					pushFollow(FOLLOW_annotations_in_enumConstant491);
					annotations();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			match(input,Identifier,FOLLOW_Identifier_in_enumConstant494); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:343:31: ( arguments )?
			int alt24=2;
			int LA24_0 = input.LA(1);
			if ( (LA24_0==36) ) {
				alt24=1;
			}
			switch (alt24) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:343:32: arguments
					{
					pushFollow(FOLLOW_arguments_in_enumConstant497);
					arguments();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:343:44: ( classBody )?
			int alt25=2;
			int LA25_0 = input.LA(1);
			if ( (LA25_0==121) ) {
				alt25=1;
			}
			switch (alt25) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:343:45: classBody
					{
					pushFollow(FOLLOW_classBody_in_enumConstant502);
					classBody();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 14, enumConstant_StartIndex); }

		}
	}
	// $ANTLR end "enumConstant"



	// $ANTLR start "enumBodyDeclarations"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:346:1: enumBodyDeclarations : ';' ( classBodyDeclaration )* ;
	public final void enumBodyDeclarations() throws RecognitionException {
		int enumBodyDeclarations_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 15) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:347:5: ( ';' ( classBodyDeclaration )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:347:7: ';' ( classBodyDeclaration )*
			{
			match(input,52,FOLLOW_52_in_enumBodyDeclarations521); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:347:11: ( classBodyDeclaration )*
			loop26:
			while (true) {
				int alt26=2;
				int LA26_0 = input.LA(1);
				if ( (LA26_0==ENUM||LA26_0==Identifier||(LA26_0 >= 52 && LA26_0 <= 53)||LA26_0==58||LA26_0==63||LA26_0==65||LA26_0==67||(LA26_0 >= 71 && LA26_0 <= 72)||LA26_0==77||LA26_0==83||LA26_0==85||(LA26_0 >= 92 && LA26_0 <= 94)||LA26_0==96||(LA26_0 >= 100 && LA26_0 <= 102)||(LA26_0 >= 105 && LA26_0 <= 107)||LA26_0==110||LA26_0==114||(LA26_0 >= 118 && LA26_0 <= 119)||LA26_0==121) ) {
					alt26=1;
				}

				switch (alt26) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:347:12: classBodyDeclaration
					{
					pushFollow(FOLLOW_classBodyDeclaration_in_enumBodyDeclarations524);
					classBodyDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop26;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 15, enumBodyDeclarations_StartIndex); }

		}
	}
	// $ANTLR end "enumBodyDeclarations"



	// $ANTLR start "interfaceDeclaration"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:350:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );
	public final void interfaceDeclaration() throws RecognitionException {
		int interfaceDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 16) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:351:5: ( normalInterfaceDeclaration | annotationTypeDeclaration )
			int alt27=2;
			int LA27_0 = input.LA(1);
			if ( (LA27_0==93) ) {
				alt27=1;
			}
			else if ( (LA27_0==58) ) {
				alt27=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 27, 0, input);
				throw nvae;
			}

			switch (alt27) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:351:7: normalInterfaceDeclaration
					{
					pushFollow(FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration543);
					normalInterfaceDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:352:11: annotationTypeDeclaration
					{
					pushFollow(FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration555);
					annotationTypeDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 16, interfaceDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "interfaceDeclaration"



	// $ANTLR start "normalInterfaceDeclaration"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:355:1: normalInterfaceDeclaration : 'interface' Identifier ( typeParameters )? ( 'extends' typeList )? interfaceBody ;
	public final void normalInterfaceDeclaration() throws RecognitionException {
		int normalInterfaceDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 17) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:356:5: ( 'interface' Identifier ( typeParameters )? ( 'extends' typeList )? interfaceBody )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:356:7: 'interface' Identifier ( typeParameters )? ( 'extends' typeList )? interfaceBody
			{
			match(input,93,FOLLOW_93_in_normalInterfaceDeclaration572); if (state.failed) return;
			match(input,Identifier,FOLLOW_Identifier_in_normalInterfaceDeclaration574); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:356:30: ( typeParameters )?
			int alt28=2;
			int LA28_0 = input.LA(1);
			if ( (LA28_0==53) ) {
				alt28=1;
			}
			switch (alt28) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:356:30: typeParameters
					{
					pushFollow(FOLLOW_typeParameters_in_normalInterfaceDeclaration576);
					typeParameters();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:356:46: ( 'extends' typeList )?
			int alt29=2;
			int LA29_0 = input.LA(1);
			if ( (LA29_0==81) ) {
				alt29=1;
			}
			switch (alt29) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:356:47: 'extends' typeList
					{
					match(input,81,FOLLOW_81_in_normalInterfaceDeclaration580); if (state.failed) return;
					pushFollow(FOLLOW_typeList_in_normalInterfaceDeclaration582);
					typeList();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			pushFollow(FOLLOW_interfaceBody_in_normalInterfaceDeclaration586);
			interfaceBody();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 17, normalInterfaceDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "normalInterfaceDeclaration"



	// $ANTLR start "typeList"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:359:1: typeList : type ( ',' type )* ;
	public final void typeList() throws RecognitionException {
		int typeList_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 18) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:360:5: ( type ( ',' type )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:360:7: type ( ',' type )*
			{
			pushFollow(FOLLOW_type_in_typeList603);
			type();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:360:12: ( ',' type )*
			loop30:
			while (true) {
				int alt30=2;
				int LA30_0 = input.LA(1);
				if ( (LA30_0==43) ) {
					alt30=1;
				}

				switch (alt30) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:360:13: ',' type
					{
					match(input,43,FOLLOW_43_in_typeList606); if (state.failed) return;
					pushFollow(FOLLOW_type_in_typeList608);
					type();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop30;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 18, typeList_StartIndex); }

		}
	}
	// $ANTLR end "typeList"



	// $ANTLR start "classBody"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:363:1: classBody : '{' ( classBodyDeclaration )* '}' ;
	public final void classBody() throws RecognitionException {
		int classBody_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 19) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:364:5: ( '{' ( classBodyDeclaration )* '}' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:364:7: '{' ( classBodyDeclaration )* '}'
			{
			match(input,121,FOLLOW_121_in_classBody627); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:364:11: ( classBodyDeclaration )*
			loop31:
			while (true) {
				int alt31=2;
				int LA31_0 = input.LA(1);
				if ( (LA31_0==ENUM||LA31_0==Identifier||(LA31_0 >= 52 && LA31_0 <= 53)||LA31_0==58||LA31_0==63||LA31_0==65||LA31_0==67||(LA31_0 >= 71 && LA31_0 <= 72)||LA31_0==77||LA31_0==83||LA31_0==85||(LA31_0 >= 92 && LA31_0 <= 94)||LA31_0==96||(LA31_0 >= 100 && LA31_0 <= 102)||(LA31_0 >= 105 && LA31_0 <= 107)||LA31_0==110||LA31_0==114||(LA31_0 >= 118 && LA31_0 <= 119)||LA31_0==121) ) {
					alt31=1;
				}

				switch (alt31) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:364:11: classBodyDeclaration
					{
					pushFollow(FOLLOW_classBodyDeclaration_in_classBody629);
					classBodyDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop31;
				}
			}

			match(input,125,FOLLOW_125_in_classBody632); if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 19, classBody_StartIndex); }

		}
	}
	// $ANTLR end "classBody"



	// $ANTLR start "interfaceBody"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:367:1: interfaceBody : '{' ( interfaceBodyDeclaration )* '}' ;
	public final void interfaceBody() throws RecognitionException {
		int interfaceBody_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 20) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:368:5: ( '{' ( interfaceBodyDeclaration )* '}' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:368:7: '{' ( interfaceBodyDeclaration )* '}'
			{
			match(input,121,FOLLOW_121_in_interfaceBody649); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:368:11: ( interfaceBodyDeclaration )*
			loop32:
			while (true) {
				int alt32=2;
				int LA32_0 = input.LA(1);
				if ( (LA32_0==ENUM||LA32_0==Identifier||(LA32_0 >= 52 && LA32_0 <= 53)||LA32_0==58||LA32_0==63||LA32_0==65||LA32_0==67||(LA32_0 >= 71 && LA32_0 <= 72)||LA32_0==77||LA32_0==83||LA32_0==85||(LA32_0 >= 92 && LA32_0 <= 94)||LA32_0==96||(LA32_0 >= 100 && LA32_0 <= 102)||(LA32_0 >= 105 && LA32_0 <= 107)||LA32_0==110||LA32_0==114||(LA32_0 >= 118 && LA32_0 <= 119)) ) {
					alt32=1;
				}

				switch (alt32) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:368:11: interfaceBodyDeclaration
					{
					pushFollow(FOLLOW_interfaceBodyDeclaration_in_interfaceBody651);
					interfaceBodyDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop32;
				}
			}

			match(input,125,FOLLOW_125_in_interfaceBody654); if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 20, interfaceBody_StartIndex); }

		}
	}
	// $ANTLR end "interfaceBody"



	// $ANTLR start "classBodyDeclaration"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:371:1: classBodyDeclaration : ( ';' | ( 'static' )? block | ( modifier )* memberDecl );
	public final void classBodyDeclaration() throws RecognitionException {
		int classBodyDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 21) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:372:5: ( ';' | ( 'static' )? block | ( modifier )* memberDecl )
			int alt35=3;
			switch ( input.LA(1) ) {
			case 52:
				{
				alt35=1;
				}
				break;
			case 106:
				{
				int LA35_2 = input.LA(2);
				if ( (LA35_2==121) ) {
					alt35=2;
				}
				else if ( (LA35_2==ENUM||LA35_2==Identifier||LA35_2==53||LA35_2==58||LA35_2==63||LA35_2==65||LA35_2==67||(LA35_2 >= 71 && LA35_2 <= 72)||LA35_2==77||LA35_2==83||LA35_2==85||(LA35_2 >= 92 && LA35_2 <= 94)||LA35_2==96||(LA35_2 >= 100 && LA35_2 <= 102)||(LA35_2 >= 105 && LA35_2 <= 107)||LA35_2==110||LA35_2==114||(LA35_2 >= 118 && LA35_2 <= 119)) ) {
					alt35=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 35, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 121:
				{
				alt35=2;
				}
				break;
			case ENUM:
			case Identifier:
			case 53:
			case 58:
			case 63:
			case 65:
			case 67:
			case 71:
			case 72:
			case 77:
			case 83:
			case 85:
			case 92:
			case 93:
			case 94:
			case 96:
			case 100:
			case 101:
			case 102:
			case 105:
			case 107:
			case 110:
			case 114:
			case 118:
			case 119:
				{
				alt35=3;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 35, 0, input);
				throw nvae;
			}
			switch (alt35) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:372:7: ';'
					{
					match(input,52,FOLLOW_52_in_classBodyDeclaration671); if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:373:7: ( 'static' )? block
					{
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:373:7: ( 'static' )?
					int alt33=2;
					int LA33_0 = input.LA(1);
					if ( (LA33_0==106) ) {
						alt33=1;
					}
					switch (alt33) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:373:7: 'static'
							{
							match(input,106,FOLLOW_106_in_classBodyDeclaration679); if (state.failed) return;
							}
							break;

					}

					pushFollow(FOLLOW_block_in_classBodyDeclaration682);
					block();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:374:7: ( modifier )* memberDecl
					{
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:374:7: ( modifier )*
					loop34:
					while (true) {
						int alt34=2;
						int LA34_0 = input.LA(1);
						if ( (LA34_0==58) ) {
							int LA34_6 = input.LA(2);
							if ( (LA34_6==Identifier) ) {
								alt34=1;
							}

						}
						else if ( (LA34_0==63||LA34_0==83||LA34_0==96||(LA34_0 >= 100 && LA34_0 <= 102)||(LA34_0 >= 106 && LA34_0 <= 107)||LA34_0==110||LA34_0==114||LA34_0==119) ) {
							alt34=1;
						}

						switch (alt34) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:374:7: modifier
							{
							pushFollow(FOLLOW_modifier_in_classBodyDeclaration690);
							modifier();
							state._fsp--;
							if (state.failed) return;
							}
							break;

						default :
							break loop34;
						}
					}

					pushFollow(FOLLOW_memberDecl_in_classBodyDeclaration693);
					memberDecl();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 21, classBodyDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "classBodyDeclaration"



	// $ANTLR start "memberDecl"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:377:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );
	public final void memberDecl() throws RecognitionException {
		int memberDecl_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 22) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:378:5: ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration )
			int alt36=7;
			switch ( input.LA(1) ) {
			case 53:
				{
				alt36=1;
				}
				break;
			case Identifier:
				{
				switch ( input.LA(2) ) {
				case 53:
					{
					int LA36_9 = input.LA(3);
					if ( (synpred38_Java()) ) {
						alt36=2;
					}
					else if ( (synpred39_Java()) ) {
						alt36=3;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 36, 9, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 47:
					{
					int LA36_10 = input.LA(3);
					if ( (synpred38_Java()) ) {
						alt36=2;
					}
					else if ( (synpred39_Java()) ) {
						alt36=3;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 36, 10, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 59:
					{
					int LA36_11 = input.LA(3);
					if ( (synpred38_Java()) ) {
						alt36=2;
					}
					else if ( (synpred39_Java()) ) {
						alt36=3;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 36, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case Identifier:
					{
					int LA36_12 = input.LA(3);
					if ( (synpred38_Java()) ) {
						alt36=2;
					}
					else if ( (synpred39_Java()) ) {
						alt36=3;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 36, 12, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 36:
					{
					alt36=5;
					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 36, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case 65:
			case 67:
			case 71:
			case 77:
			case 85:
			case 92:
			case 94:
			case 105:
				{
				int LA36_3 = input.LA(2);
				if ( (LA36_3==59) ) {
					int LA36_14 = input.LA(3);
					if ( (synpred38_Java()) ) {
						alt36=2;
					}
					else if ( (synpred39_Java()) ) {
						alt36=3;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 36, 14, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}
				else if ( (LA36_3==Identifier) ) {
					int LA36_15 = input.LA(3);
					if ( (synpred38_Java()) ) {
						alt36=2;
					}
					else if ( (synpred39_Java()) ) {
						alt36=3;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 36, 15, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 36, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 118:
				{
				alt36=4;
				}
				break;
			case 58:
			case 93:
				{
				alt36=6;
				}
				break;
			case ENUM:
			case 72:
				{
				alt36=7;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 36, 0, input);
				throw nvae;
			}
			switch (alt36) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:378:7: genericMethodOrConstructorDecl
					{
					pushFollow(FOLLOW_genericMethodOrConstructorDecl_in_memberDecl710);
					genericMethodOrConstructorDecl();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:379:7: methodDeclaration
					{
					pushFollow(FOLLOW_methodDeclaration_in_memberDecl718);
					methodDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:380:7: fieldDeclaration
					{
					pushFollow(FOLLOW_fieldDeclaration_in_memberDecl726);
					fieldDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:381:7: 'void' Identifier voidMethodDeclaratorRest
					{
					match(input,118,FOLLOW_118_in_memberDecl734); if (state.failed) return;
					match(input,Identifier,FOLLOW_Identifier_in_memberDecl736); if (state.failed) return;
					pushFollow(FOLLOW_voidMethodDeclaratorRest_in_memberDecl738);
					voidMethodDeclaratorRest();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:382:7: Identifier constructorDeclaratorRest
					{
					match(input,Identifier,FOLLOW_Identifier_in_memberDecl746); if (state.failed) return;
					pushFollow(FOLLOW_constructorDeclaratorRest_in_memberDecl748);
					constructorDeclaratorRest();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 6 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:383:7: interfaceDeclaration
					{
					pushFollow(FOLLOW_interfaceDeclaration_in_memberDecl756);
					interfaceDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 7 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:384:7: classDeclaration
					{
					pushFollow(FOLLOW_classDeclaration_in_memberDecl764);
					classDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 22, memberDecl_StartIndex); }

		}
	}
	// $ANTLR end "memberDecl"



	// $ANTLR start "genericMethodOrConstructorDecl"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:387:1: genericMethodOrConstructorDecl : typeParameters genericMethodOrConstructorRest ;
	public final void genericMethodOrConstructorDecl() throws RecognitionException {
		int genericMethodOrConstructorDecl_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 23) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:388:5: ( typeParameters genericMethodOrConstructorRest )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:388:7: typeParameters genericMethodOrConstructorRest
			{
			pushFollow(FOLLOW_typeParameters_in_genericMethodOrConstructorDecl781);
			typeParameters();
			state._fsp--;
			if (state.failed) return;
			pushFollow(FOLLOW_genericMethodOrConstructorRest_in_genericMethodOrConstructorDecl783);
			genericMethodOrConstructorRest();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 23, genericMethodOrConstructorDecl_StartIndex); }

		}
	}
	// $ANTLR end "genericMethodOrConstructorDecl"



	// $ANTLR start "genericMethodOrConstructorRest"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:391:1: genericMethodOrConstructorRest : ( ( type | 'void' ) Identifier methodDeclaratorRest | Identifier constructorDeclaratorRest );
	public final void genericMethodOrConstructorRest() throws RecognitionException {
		int genericMethodOrConstructorRest_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 24) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:392:5: ( ( type | 'void' ) Identifier methodDeclaratorRest | Identifier constructorDeclaratorRest )
			int alt38=2;
			int LA38_0 = input.LA(1);
			if ( (LA38_0==Identifier) ) {
				int LA38_1 = input.LA(2);
				if ( (LA38_1==Identifier||LA38_1==47||LA38_1==53||LA38_1==59) ) {
					alt38=1;
				}
				else if ( (LA38_1==36) ) {
					alt38=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 38, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA38_0==65||LA38_0==67||LA38_0==71||LA38_0==77||LA38_0==85||LA38_0==92||LA38_0==94||LA38_0==105||LA38_0==118) ) {
				alt38=1;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 38, 0, input);
				throw nvae;
			}

			switch (alt38) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:392:7: ( type | 'void' ) Identifier methodDeclaratorRest
					{
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:392:7: ( type | 'void' )
					int alt37=2;
					int LA37_0 = input.LA(1);
					if ( (LA37_0==Identifier||LA37_0==65||LA37_0==67||LA37_0==71||LA37_0==77||LA37_0==85||LA37_0==92||LA37_0==94||LA37_0==105) ) {
						alt37=1;
					}
					else if ( (LA37_0==118) ) {
						alt37=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return;}
						NoViableAltException nvae =
							new NoViableAltException("", 37, 0, input);
						throw nvae;
					}

					switch (alt37) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:392:8: type
							{
							pushFollow(FOLLOW_type_in_genericMethodOrConstructorRest801);
							type();
							state._fsp--;
							if (state.failed) return;
							}
							break;
						case 2 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:392:15: 'void'
							{
							match(input,118,FOLLOW_118_in_genericMethodOrConstructorRest805); if (state.failed) return;
							}
							break;

					}

					match(input,Identifier,FOLLOW_Identifier_in_genericMethodOrConstructorRest808); if (state.failed) return;
					pushFollow(FOLLOW_methodDeclaratorRest_in_genericMethodOrConstructorRest810);
					methodDeclaratorRest();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:393:7: Identifier constructorDeclaratorRest
					{
					match(input,Identifier,FOLLOW_Identifier_in_genericMethodOrConstructorRest818); if (state.failed) return;
					pushFollow(FOLLOW_constructorDeclaratorRest_in_genericMethodOrConstructorRest820);
					constructorDeclaratorRest();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 24, genericMethodOrConstructorRest_StartIndex); }

		}
	}
	// $ANTLR end "genericMethodOrConstructorRest"



	// $ANTLR start "methodDeclaration"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:396:1: methodDeclaration : type Identifier methodDeclaratorRest ;
	public final void methodDeclaration() throws RecognitionException {
		VarDecl_stack.push(new VarDecl_scope());

		int methodDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 25) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:398:5: ( type Identifier methodDeclaratorRest )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:398:7: type Identifier methodDeclaratorRest
			{
			pushFollow(FOLLOW_type_in_methodDeclaration842);
			type();
			state._fsp--;
			if (state.failed) return;
			match(input,Identifier,FOLLOW_Identifier_in_methodDeclaration844); if (state.failed) return;
			pushFollow(FOLLOW_methodDeclaratorRest_in_methodDeclaration846);
			methodDeclaratorRest();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 25, methodDeclaration_StartIndex); }

			VarDecl_stack.pop();

		}
	}
	// $ANTLR end "methodDeclaration"



	// $ANTLR start "fieldDeclaration"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:401:1: fieldDeclaration : type variableDeclarators ';' ;
	public final void fieldDeclaration() throws RecognitionException {
		int fieldDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 26) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:402:5: ( type variableDeclarators ';' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:402:7: type variableDeclarators ';'
			{
			pushFollow(FOLLOW_type_in_fieldDeclaration863);
			type();
			state._fsp--;
			if (state.failed) return;
			pushFollow(FOLLOW_variableDeclarators_in_fieldDeclaration865);
			variableDeclarators();
			state._fsp--;
			if (state.failed) return;
			match(input,52,FOLLOW_52_in_fieldDeclaration867); if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 26, fieldDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "fieldDeclaration"



	// $ANTLR start "interfaceBodyDeclaration"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:405:1: interfaceBodyDeclaration : ( ( modifier )* interfaceMemberDecl | ';' );
	public final void interfaceBodyDeclaration() throws RecognitionException {
		int interfaceBodyDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 27) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:406:5: ( ( modifier )* interfaceMemberDecl | ';' )
			int alt40=2;
			int LA40_0 = input.LA(1);
			if ( (LA40_0==ENUM||LA40_0==Identifier||LA40_0==53||LA40_0==58||LA40_0==63||LA40_0==65||LA40_0==67||(LA40_0 >= 71 && LA40_0 <= 72)||LA40_0==77||LA40_0==83||LA40_0==85||(LA40_0 >= 92 && LA40_0 <= 94)||LA40_0==96||(LA40_0 >= 100 && LA40_0 <= 102)||(LA40_0 >= 105 && LA40_0 <= 107)||LA40_0==110||LA40_0==114||(LA40_0 >= 118 && LA40_0 <= 119)) ) {
				alt40=1;
			}
			else if ( (LA40_0==52) ) {
				alt40=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 40, 0, input);
				throw nvae;
			}

			switch (alt40) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:406:7: ( modifier )* interfaceMemberDecl
					{
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:406:7: ( modifier )*
					loop39:
					while (true) {
						int alt39=2;
						int LA39_0 = input.LA(1);
						if ( (LA39_0==58) ) {
							int LA39_6 = input.LA(2);
							if ( (LA39_6==Identifier) ) {
								alt39=1;
							}

						}
						else if ( (LA39_0==63||LA39_0==83||LA39_0==96||(LA39_0 >= 100 && LA39_0 <= 102)||(LA39_0 >= 106 && LA39_0 <= 107)||LA39_0==110||LA39_0==114||LA39_0==119) ) {
							alt39=1;
						}

						switch (alt39) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:406:7: modifier
							{
							pushFollow(FOLLOW_modifier_in_interfaceBodyDeclaration884);
							modifier();
							state._fsp--;
							if (state.failed) return;
							}
							break;

						default :
							break loop39;
						}
					}

					pushFollow(FOLLOW_interfaceMemberDecl_in_interfaceBodyDeclaration887);
					interfaceMemberDecl();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:407:9: ';'
					{
					match(input,52,FOLLOW_52_in_interfaceBodyDeclaration897); if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 27, interfaceBodyDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "interfaceBodyDeclaration"



	// $ANTLR start "interfaceMemberDecl"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:410:1: interfaceMemberDecl : ( interfaceMethodOrFieldDecl | interfaceGenericMethodDecl | 'void' Identifier voidInterfaceMethodDeclaratorRest | interfaceDeclaration | classDeclaration );
	public final void interfaceMemberDecl() throws RecognitionException {
		int interfaceMemberDecl_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 28) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:411:5: ( interfaceMethodOrFieldDecl | interfaceGenericMethodDecl | 'void' Identifier voidInterfaceMethodDeclaratorRest | interfaceDeclaration | classDeclaration )
			int alt41=5;
			switch ( input.LA(1) ) {
			case Identifier:
			case 65:
			case 67:
			case 71:
			case 77:
			case 85:
			case 92:
			case 94:
			case 105:
				{
				alt41=1;
				}
				break;
			case 53:
				{
				alt41=2;
				}
				break;
			case 118:
				{
				alt41=3;
				}
				break;
			case 58:
			case 93:
				{
				alt41=4;
				}
				break;
			case ENUM:
			case 72:
				{
				alt41=5;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 41, 0, input);
				throw nvae;
			}
			switch (alt41) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:411:7: interfaceMethodOrFieldDecl
					{
					pushFollow(FOLLOW_interfaceMethodOrFieldDecl_in_interfaceMemberDecl914);
					interfaceMethodOrFieldDecl();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:412:9: interfaceGenericMethodDecl
					{
					pushFollow(FOLLOW_interfaceGenericMethodDecl_in_interfaceMemberDecl924);
					interfaceGenericMethodDecl();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:413:9: 'void' Identifier voidInterfaceMethodDeclaratorRest
					{
					match(input,118,FOLLOW_118_in_interfaceMemberDecl934); if (state.failed) return;
					match(input,Identifier,FOLLOW_Identifier_in_interfaceMemberDecl936); if (state.failed) return;
					pushFollow(FOLLOW_voidInterfaceMethodDeclaratorRest_in_interfaceMemberDecl938);
					voidInterfaceMethodDeclaratorRest();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:414:9: interfaceDeclaration
					{
					pushFollow(FOLLOW_interfaceDeclaration_in_interfaceMemberDecl948);
					interfaceDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:415:9: classDeclaration
					{
					pushFollow(FOLLOW_classDeclaration_in_interfaceMemberDecl958);
					classDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 28, interfaceMemberDecl_StartIndex); }

		}
	}
	// $ANTLR end "interfaceMemberDecl"



	// $ANTLR start "interfaceMethodOrFieldDecl"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:418:1: interfaceMethodOrFieldDecl : type Identifier interfaceMethodOrFieldRest ;
	public final void interfaceMethodOrFieldDecl() throws RecognitionException {
		int interfaceMethodOrFieldDecl_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 29) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:419:5: ( type Identifier interfaceMethodOrFieldRest )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:419:7: type Identifier interfaceMethodOrFieldRest
			{
			pushFollow(FOLLOW_type_in_interfaceMethodOrFieldDecl975);
			type();
			state._fsp--;
			if (state.failed) return;
			match(input,Identifier,FOLLOW_Identifier_in_interfaceMethodOrFieldDecl977); if (state.failed) return;
			pushFollow(FOLLOW_interfaceMethodOrFieldRest_in_interfaceMethodOrFieldDecl979);
			interfaceMethodOrFieldRest();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 29, interfaceMethodOrFieldDecl_StartIndex); }

		}
	}
	// $ANTLR end "interfaceMethodOrFieldDecl"



	// $ANTLR start "interfaceMethodOrFieldRest"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:422:1: interfaceMethodOrFieldRest : ( constantDeclaratorsRest ';' | interfaceMethodDeclaratorRest );
	public final void interfaceMethodOrFieldRest() throws RecognitionException {
		int interfaceMethodOrFieldRest_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 30) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:423:5: ( constantDeclaratorsRest ';' | interfaceMethodDeclaratorRest )
			int alt42=2;
			int LA42_0 = input.LA(1);
			if ( (LA42_0==54||LA42_0==59) ) {
				alt42=1;
			}
			else if ( (LA42_0==36) ) {
				alt42=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 42, 0, input);
				throw nvae;
			}

			switch (alt42) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:423:7: constantDeclaratorsRest ';'
					{
					pushFollow(FOLLOW_constantDeclaratorsRest_in_interfaceMethodOrFieldRest996);
					constantDeclaratorsRest();
					state._fsp--;
					if (state.failed) return;
					match(input,52,FOLLOW_52_in_interfaceMethodOrFieldRest998); if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:424:7: interfaceMethodDeclaratorRest
					{
					pushFollow(FOLLOW_interfaceMethodDeclaratorRest_in_interfaceMethodOrFieldRest1006);
					interfaceMethodDeclaratorRest();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 30, interfaceMethodOrFieldRest_StartIndex); }

		}
	}
	// $ANTLR end "interfaceMethodOrFieldRest"



	// $ANTLR start "methodDeclaratorRest"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:427:1: methodDeclaratorRest : formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( methodBody | ';' ) ;
	public final void methodDeclaratorRest() throws RecognitionException {
		int methodDeclaratorRest_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 31) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:428:5: ( formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( methodBody | ';' ) )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:428:7: formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( methodBody | ';' )
			{
			pushFollow(FOLLOW_formalParameters_in_methodDeclaratorRest1023);
			formalParameters();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:428:24: ( '[' ']' )*
			loop43:
			while (true) {
				int alt43=2;
				int LA43_0 = input.LA(1);
				if ( (LA43_0==59) ) {
					alt43=1;
				}

				switch (alt43) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:428:25: '[' ']'
					{
					match(input,59,FOLLOW_59_in_methodDeclaratorRest1026); if (state.failed) return;
					match(input,60,FOLLOW_60_in_methodDeclaratorRest1028); if (state.failed) return;
					}
					break;

				default :
					break loop43;
				}
			}

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:429:9: ( 'throws' qualifiedNameList )?
			int alt44=2;
			int LA44_0 = input.LA(1);
			if ( (LA44_0==113) ) {
				alt44=1;
			}
			switch (alt44) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:429:10: 'throws' qualifiedNameList
					{
					match(input,113,FOLLOW_113_in_methodDeclaratorRest1041); if (state.failed) return;
					pushFollow(FOLLOW_qualifiedNameList_in_methodDeclaratorRest1043);
					qualifiedNameList();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:430:9: ( methodBody | ';' )
			int alt45=2;
			int LA45_0 = input.LA(1);
			if ( (LA45_0==121) ) {
				alt45=1;
			}
			else if ( (LA45_0==52) ) {
				alt45=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 45, 0, input);
				throw nvae;
			}

			switch (alt45) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:430:13: methodBody
					{
					pushFollow(FOLLOW_methodBody_in_methodDeclaratorRest1059);
					methodBody();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:431:13: ';'
					{
					match(input,52,FOLLOW_52_in_methodDeclaratorRest1073); if (state.failed) return;
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 31, methodDeclaratorRest_StartIndex); }

		}
	}
	// $ANTLR end "methodDeclaratorRest"



	// $ANTLR start "voidMethodDeclaratorRest"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:435:1: voidMethodDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' ) ;
	public final void voidMethodDeclaratorRest() throws RecognitionException {
		int voidMethodDeclaratorRest_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 32) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:436:5: ( formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' ) )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:436:7: formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' )
			{
			pushFollow(FOLLOW_formalParameters_in_voidMethodDeclaratorRest1100);
			formalParameters();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:436:24: ( 'throws' qualifiedNameList )?
			int alt46=2;
			int LA46_0 = input.LA(1);
			if ( (LA46_0==113) ) {
				alt46=1;
			}
			switch (alt46) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:436:25: 'throws' qualifiedNameList
					{
					match(input,113,FOLLOW_113_in_voidMethodDeclaratorRest1103); if (state.failed) return;
					pushFollow(FOLLOW_qualifiedNameList_in_voidMethodDeclaratorRest1105);
					qualifiedNameList();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:437:9: ( methodBody | ';' )
			int alt47=2;
			int LA47_0 = input.LA(1);
			if ( (LA47_0==121) ) {
				alt47=1;
			}
			else if ( (LA47_0==52) ) {
				alt47=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 47, 0, input);
				throw nvae;
			}

			switch (alt47) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:437:13: methodBody
					{
					pushFollow(FOLLOW_methodBody_in_voidMethodDeclaratorRest1121);
					methodBody();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:438:13: ';'
					{
					match(input,52,FOLLOW_52_in_voidMethodDeclaratorRest1135); if (state.failed) return;
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 32, voidMethodDeclaratorRest_StartIndex); }

		}
	}
	// $ANTLR end "voidMethodDeclaratorRest"



	// $ANTLR start "interfaceMethodDeclaratorRest"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:442:1: interfaceMethodDeclaratorRest : formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' ;
	public final void interfaceMethodDeclaratorRest() throws RecognitionException {
		int interfaceMethodDeclaratorRest_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 33) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:443:5: ( formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:443:7: formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';'
			{
			pushFollow(FOLLOW_formalParameters_in_interfaceMethodDeclaratorRest1162);
			formalParameters();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:443:24: ( '[' ']' )*
			loop48:
			while (true) {
				int alt48=2;
				int LA48_0 = input.LA(1);
				if ( (LA48_0==59) ) {
					alt48=1;
				}

				switch (alt48) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:443:25: '[' ']'
					{
					match(input,59,FOLLOW_59_in_interfaceMethodDeclaratorRest1165); if (state.failed) return;
					match(input,60,FOLLOW_60_in_interfaceMethodDeclaratorRest1167); if (state.failed) return;
					}
					break;

				default :
					break loop48;
				}
			}

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:443:35: ( 'throws' qualifiedNameList )?
			int alt49=2;
			int LA49_0 = input.LA(1);
			if ( (LA49_0==113) ) {
				alt49=1;
			}
			switch (alt49) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:443:36: 'throws' qualifiedNameList
					{
					match(input,113,FOLLOW_113_in_interfaceMethodDeclaratorRest1172); if (state.failed) return;
					pushFollow(FOLLOW_qualifiedNameList_in_interfaceMethodDeclaratorRest1174);
					qualifiedNameList();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			match(input,52,FOLLOW_52_in_interfaceMethodDeclaratorRest1178); if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 33, interfaceMethodDeclaratorRest_StartIndex); }

		}
	}
	// $ANTLR end "interfaceMethodDeclaratorRest"



	// $ANTLR start "interfaceGenericMethodDecl"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:446:1: interfaceGenericMethodDecl : typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest ;
	public final void interfaceGenericMethodDecl() throws RecognitionException {
		int interfaceGenericMethodDecl_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 34) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:447:5: ( typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:447:7: typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest
			{
			pushFollow(FOLLOW_typeParameters_in_interfaceGenericMethodDecl1195);
			typeParameters();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:447:22: ( type | 'void' )
			int alt50=2;
			int LA50_0 = input.LA(1);
			if ( (LA50_0==Identifier||LA50_0==65||LA50_0==67||LA50_0==71||LA50_0==77||LA50_0==85||LA50_0==92||LA50_0==94||LA50_0==105) ) {
				alt50=1;
			}
			else if ( (LA50_0==118) ) {
				alt50=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 50, 0, input);
				throw nvae;
			}

			switch (alt50) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:447:23: type
					{
					pushFollow(FOLLOW_type_in_interfaceGenericMethodDecl1198);
					type();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:447:30: 'void'
					{
					match(input,118,FOLLOW_118_in_interfaceGenericMethodDecl1202); if (state.failed) return;
					}
					break;

			}

			match(input,Identifier,FOLLOW_Identifier_in_interfaceGenericMethodDecl1205); if (state.failed) return;
			pushFollow(FOLLOW_interfaceMethodDeclaratorRest_in_interfaceGenericMethodDecl1215);
			interfaceMethodDeclaratorRest();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 34, interfaceGenericMethodDecl_StartIndex); }

		}
	}
	// $ANTLR end "interfaceGenericMethodDecl"



	// $ANTLR start "voidInterfaceMethodDeclaratorRest"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:451:1: voidInterfaceMethodDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? ';' ;
	public final void voidInterfaceMethodDeclaratorRest() throws RecognitionException {
		int voidInterfaceMethodDeclaratorRest_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 35) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:452:5: ( formalParameters ( 'throws' qualifiedNameList )? ';' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:452:7: formalParameters ( 'throws' qualifiedNameList )? ';'
			{
			pushFollow(FOLLOW_formalParameters_in_voidInterfaceMethodDeclaratorRest1232);
			formalParameters();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:452:24: ( 'throws' qualifiedNameList )?
			int alt51=2;
			int LA51_0 = input.LA(1);
			if ( (LA51_0==113) ) {
				alt51=1;
			}
			switch (alt51) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:452:25: 'throws' qualifiedNameList
					{
					match(input,113,FOLLOW_113_in_voidInterfaceMethodDeclaratorRest1235); if (state.failed) return;
					pushFollow(FOLLOW_qualifiedNameList_in_voidInterfaceMethodDeclaratorRest1237);
					qualifiedNameList();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			match(input,52,FOLLOW_52_in_voidInterfaceMethodDeclaratorRest1241); if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 35, voidInterfaceMethodDeclaratorRest_StartIndex); }

		}
	}
	// $ANTLR end "voidInterfaceMethodDeclaratorRest"



	// $ANTLR start "constructorDeclaratorRest"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:455:1: constructorDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? methodBody ;
	public final void constructorDeclaratorRest() throws RecognitionException {
		int constructorDeclaratorRest_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 36) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:456:5: ( formalParameters ( 'throws' qualifiedNameList )? methodBody )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:456:7: formalParameters ( 'throws' qualifiedNameList )? methodBody
			{
			pushFollow(FOLLOW_formalParameters_in_constructorDeclaratorRest1258);
			formalParameters();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:456:24: ( 'throws' qualifiedNameList )?
			int alt52=2;
			int LA52_0 = input.LA(1);
			if ( (LA52_0==113) ) {
				alt52=1;
			}
			switch (alt52) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:456:25: 'throws' qualifiedNameList
					{
					match(input,113,FOLLOW_113_in_constructorDeclaratorRest1261); if (state.failed) return;
					pushFollow(FOLLOW_qualifiedNameList_in_constructorDeclaratorRest1263);
					qualifiedNameList();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			pushFollow(FOLLOW_methodBody_in_constructorDeclaratorRest1267);
			methodBody();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 36, constructorDeclaratorRest_StartIndex); }

		}
	}
	// $ANTLR end "constructorDeclaratorRest"



	// $ANTLR start "constantDeclarator"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:459:1: constantDeclarator : Identifier constantDeclaratorRest ;
	public final void constantDeclarator() throws RecognitionException {
		int constantDeclarator_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 37) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:460:5: ( Identifier constantDeclaratorRest )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:460:7: Identifier constantDeclaratorRest
			{
			match(input,Identifier,FOLLOW_Identifier_in_constantDeclarator1284); if (state.failed) return;
			pushFollow(FOLLOW_constantDeclaratorRest_in_constantDeclarator1286);
			constantDeclaratorRest();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 37, constantDeclarator_StartIndex); }

		}
	}
	// $ANTLR end "constantDeclarator"



	// $ANTLR start "variableDeclarators"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:463:1: variableDeclarators : variableDeclarator ( ',' variableDeclarator )* ;
	public final void variableDeclarators() throws RecognitionException {
		int variableDeclarators_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 38) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:464:5: ( variableDeclarator ( ',' variableDeclarator )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:464:7: variableDeclarator ( ',' variableDeclarator )*
			{
			pushFollow(FOLLOW_variableDeclarator_in_variableDeclarators1303);
			variableDeclarator();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:464:26: ( ',' variableDeclarator )*
			loop53:
			while (true) {
				int alt53=2;
				int LA53_0 = input.LA(1);
				if ( (LA53_0==43) ) {
					alt53=1;
				}

				switch (alt53) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:464:27: ',' variableDeclarator
					{
					match(input,43,FOLLOW_43_in_variableDeclarators1306); if (state.failed) return;
					pushFollow(FOLLOW_variableDeclarator_in_variableDeclarators1308);
					variableDeclarator();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop53;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 38, variableDeclarators_StartIndex); }

		}
	}
	// $ANTLR end "variableDeclarators"


	protected static class variableDeclarator_scope {
		JavaLocalDeclarationDescr.IdentifierDescr ident;
	}
	protected Stack<variableDeclarator_scope> variableDeclarator_stack = new Stack<variableDeclarator_scope>();


	// $ANTLR start "variableDeclarator"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:467:1: variableDeclarator : id= Identifier rest= variableDeclaratorRest ;
	public final void variableDeclarator() throws RecognitionException {
		variableDeclarator_stack.push(new variableDeclarator_scope());
		int variableDeclarator_StartIndex = input.index();

		Token id=null;
		ParserRuleReturnScope rest =null;


		        variableDeclarator_stack.peek().ident = new JavaLocalDeclarationDescr.IdentifierDescr();
		    
		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 39) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:479:5: (id= Identifier rest= variableDeclaratorRest )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:479:7: id= Identifier rest= variableDeclaratorRest
			{
			id=(Token)match(input,Identifier,FOLLOW_Identifier_in_variableDeclarator1355); if (state.failed) return;
			pushFollow(FOLLOW_variableDeclaratorRest_in_variableDeclarator1359);
			rest=variableDeclaratorRest();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) {
			            variableDeclarator_stack.peek().ident.setIdentifier( (id!=null?id.getText():null) );
			            variableDeclarator_stack.peek().ident.setStart( ((CommonToken)id).getStartIndex() - 1 );
			            if( (rest!=null?(rest.stop):null) != null ) {
			                   variableDeclarator_stack.peek().ident.setEnd( ((CommonToken)(rest!=null?(rest.stop):null)).getStopIndex() );
			            }
			        }
			}

			if ( state.backtracking==0 ) {
			            if( VarDecl_stack.peek().descr != null ) {
			                VarDecl_stack.peek().descr.addIdentifier( variableDeclarator_stack.peek().ident );
			            }
			    }
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 39, variableDeclarator_StartIndex); }

			variableDeclarator_stack.pop();
		}
	}
	// $ANTLR end "variableDeclarator"


	public static class variableDeclaratorRest_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "variableDeclaratorRest"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:489:1: variableDeclaratorRest : ( ( '[' ']' )+ ( '=' variableInitializer )? | '=' variableInitializer |);
	public final JavaParser.variableDeclaratorRest_return variableDeclaratorRest() throws RecognitionException {
		JavaParser.variableDeclaratorRest_return retval = new JavaParser.variableDeclaratorRest_return();
		retval.start = input.LT(1);
		int variableDeclaratorRest_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 40) ) { return retval; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:490:5: ( ( '[' ']' )+ ( '=' variableInitializer )? | '=' variableInitializer |)
			int alt56=3;
			switch ( input.LA(1) ) {
			case 59:
				{
				alt56=1;
				}
				break;
			case 54:
				{
				alt56=2;
				}
				break;
			case EOF:
			case 43:
			case 52:
				{
				alt56=3;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 56, 0, input);
				throw nvae;
			}
			switch (alt56) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:490:7: ( '[' ']' )+ ( '=' variableInitializer )?
					{
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:490:7: ( '[' ']' )+
					int cnt54=0;
					loop54:
					while (true) {
						int alt54=2;
						int LA54_0 = input.LA(1);
						if ( (LA54_0==59) ) {
							alt54=1;
						}

						switch (alt54) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:490:8: '[' ']'
							{
							match(input,59,FOLLOW_59_in_variableDeclaratorRest1387); if (state.failed) return retval;
							match(input,60,FOLLOW_60_in_variableDeclaratorRest1389); if (state.failed) return retval;
							}
							break;

						default :
							if ( cnt54 >= 1 ) break loop54;
							if (state.backtracking>0) {state.failed=true; return retval;}
							EarlyExitException eee = new EarlyExitException(54, input);
							throw eee;
						}
						cnt54++;
					}

					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:490:18: ( '=' variableInitializer )?
					int alt55=2;
					int LA55_0 = input.LA(1);
					if ( (LA55_0==54) ) {
						alt55=1;
					}
					switch (alt55) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:490:19: '=' variableInitializer
							{
							match(input,54,FOLLOW_54_in_variableDeclaratorRest1394); if (state.failed) return retval;
							pushFollow(FOLLOW_variableInitializer_in_variableDeclaratorRest1396);
							variableInitializer();
							state._fsp--;
							if (state.failed) return retval;
							}
							break;

					}

					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:491:7: '=' variableInitializer
					{
					match(input,54,FOLLOW_54_in_variableDeclaratorRest1406); if (state.failed) return retval;
					pushFollow(FOLLOW_variableInitializer_in_variableDeclaratorRest1408);
					variableInitializer();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:493:5: 
					{
					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 40, variableDeclaratorRest_StartIndex); }

		}
		return retval;
	}
	// $ANTLR end "variableDeclaratorRest"



	// $ANTLR start "constantDeclaratorsRest"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:495:1: constantDeclaratorsRest : constantDeclaratorRest ( ',' constantDeclarator )* ;
	public final void constantDeclaratorsRest() throws RecognitionException {
		int constantDeclaratorsRest_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 41) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:496:5: ( constantDeclaratorRest ( ',' constantDeclarator )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:496:9: constantDeclaratorRest ( ',' constantDeclarator )*
			{
			pushFollow(FOLLOW_constantDeclaratorRest_in_constantDeclaratorsRest1433);
			constantDeclaratorRest();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:496:32: ( ',' constantDeclarator )*
			loop57:
			while (true) {
				int alt57=2;
				int LA57_0 = input.LA(1);
				if ( (LA57_0==43) ) {
					alt57=1;
				}

				switch (alt57) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:496:33: ',' constantDeclarator
					{
					match(input,43,FOLLOW_43_in_constantDeclaratorsRest1436); if (state.failed) return;
					pushFollow(FOLLOW_constantDeclarator_in_constantDeclaratorsRest1438);
					constantDeclarator();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop57;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 41, constantDeclaratorsRest_StartIndex); }

		}
	}
	// $ANTLR end "constantDeclaratorsRest"



	// $ANTLR start "constantDeclaratorRest"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:499:1: constantDeclaratorRest : ( '[' ']' )* '=' variableInitializer ;
	public final void constantDeclaratorRest() throws RecognitionException {
		int constantDeclaratorRest_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 42) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:500:5: ( ( '[' ']' )* '=' variableInitializer )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:500:7: ( '[' ']' )* '=' variableInitializer
			{
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:500:7: ( '[' ']' )*
			loop58:
			while (true) {
				int alt58=2;
				int LA58_0 = input.LA(1);
				if ( (LA58_0==59) ) {
					alt58=1;
				}

				switch (alt58) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:500:8: '[' ']'
					{
					match(input,59,FOLLOW_59_in_constantDeclaratorRest1458); if (state.failed) return;
					match(input,60,FOLLOW_60_in_constantDeclaratorRest1460); if (state.failed) return;
					}
					break;

				default :
					break loop58;
				}
			}

			match(input,54,FOLLOW_54_in_constantDeclaratorRest1464); if (state.failed) return;
			pushFollow(FOLLOW_variableInitializer_in_constantDeclaratorRest1466);
			variableInitializer();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 42, constantDeclaratorRest_StartIndex); }

		}
	}
	// $ANTLR end "constantDeclaratorRest"



	// $ANTLR start "variableDeclaratorId"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:503:1: variableDeclaratorId : Identifier ( '[' ']' )* ;
	public final void variableDeclaratorId() throws RecognitionException {
		int variableDeclaratorId_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 43) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:504:5: ( Identifier ( '[' ']' )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:504:7: Identifier ( '[' ']' )*
			{
			match(input,Identifier,FOLLOW_Identifier_in_variableDeclaratorId1483); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:504:18: ( '[' ']' )*
			loop59:
			while (true) {
				int alt59=2;
				int LA59_0 = input.LA(1);
				if ( (LA59_0==59) ) {
					alt59=1;
				}

				switch (alt59) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:504:19: '[' ']'
					{
					match(input,59,FOLLOW_59_in_variableDeclaratorId1486); if (state.failed) return;
					match(input,60,FOLLOW_60_in_variableDeclaratorId1488); if (state.failed) return;
					}
					break;

				default :
					break loop59;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 43, variableDeclaratorId_StartIndex); }

		}
	}
	// $ANTLR end "variableDeclaratorId"



	// $ANTLR start "variableInitializer"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:507:1: variableInitializer : ( arrayInitializer | expression );
	public final void variableInitializer() throws RecognitionException {
		int variableInitializer_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 44) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:508:5: ( arrayInitializer | expression )
			int alt60=2;
			int LA60_0 = input.LA(1);
			if ( (LA60_0==121) ) {
				alt60=1;
			}
			else if ( ((LA60_0 >= CharacterLiteral && LA60_0 <= DecimalLiteral)||LA60_0==FloatingPointLiteral||(LA60_0 >= HexLiteral && LA60_0 <= Identifier)||(LA60_0 >= OctalLiteral && LA60_0 <= StringLiteral)||LA60_0==29||LA60_0==36||(LA60_0 >= 40 && LA60_0 <= 41)||(LA60_0 >= 44 && LA60_0 <= 45)||LA60_0==53||LA60_0==65||LA60_0==67||(LA60_0 >= 70 && LA60_0 <= 71)||LA60_0==77||(LA60_0 >= 79 && LA60_0 <= 80)||LA60_0==82||LA60_0==85||LA60_0==92||LA60_0==94||(LA60_0 >= 97 && LA60_0 <= 98)||LA60_0==105||LA60_0==108||LA60_0==111||LA60_0==115||LA60_0==118||LA60_0==126) ) {
				alt60=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 60, 0, input);
				throw nvae;
			}

			switch (alt60) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:508:7: arrayInitializer
					{
					pushFollow(FOLLOW_arrayInitializer_in_variableInitializer1507);
					arrayInitializer();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:509:9: expression
					{
					pushFollow(FOLLOW_expression_in_variableInitializer1517);
					expression();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 44, variableInitializer_StartIndex); }

		}
	}
	// $ANTLR end "variableInitializer"



	// $ANTLR start "arrayInitializer"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:512:1: arrayInitializer : '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}' ;
	public final void arrayInitializer() throws RecognitionException {
		int arrayInitializer_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 45) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:513:5: ( '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:513:7: '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}'
			{
			match(input,121,FOLLOW_121_in_arrayInitializer1534); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:513:11: ( variableInitializer ( ',' variableInitializer )* ( ',' )? )?
			int alt63=2;
			int LA63_0 = input.LA(1);
			if ( ((LA63_0 >= CharacterLiteral && LA63_0 <= DecimalLiteral)||LA63_0==FloatingPointLiteral||(LA63_0 >= HexLiteral && LA63_0 <= Identifier)||(LA63_0 >= OctalLiteral && LA63_0 <= StringLiteral)||LA63_0==29||LA63_0==36||(LA63_0 >= 40 && LA63_0 <= 41)||(LA63_0 >= 44 && LA63_0 <= 45)||LA63_0==53||LA63_0==65||LA63_0==67||(LA63_0 >= 70 && LA63_0 <= 71)||LA63_0==77||(LA63_0 >= 79 && LA63_0 <= 80)||LA63_0==82||LA63_0==85||LA63_0==92||LA63_0==94||(LA63_0 >= 97 && LA63_0 <= 98)||LA63_0==105||LA63_0==108||LA63_0==111||LA63_0==115||LA63_0==118||LA63_0==121||LA63_0==126) ) {
				alt63=1;
			}
			switch (alt63) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:513:12: variableInitializer ( ',' variableInitializer )* ( ',' )?
					{
					pushFollow(FOLLOW_variableInitializer_in_arrayInitializer1537);
					variableInitializer();
					state._fsp--;
					if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:513:32: ( ',' variableInitializer )*
					loop61:
					while (true) {
						int alt61=2;
						int LA61_0 = input.LA(1);
						if ( (LA61_0==43) ) {
							int LA61_1 = input.LA(2);
							if ( ((LA61_1 >= CharacterLiteral && LA61_1 <= DecimalLiteral)||LA61_1==FloatingPointLiteral||(LA61_1 >= HexLiteral && LA61_1 <= Identifier)||(LA61_1 >= OctalLiteral && LA61_1 <= StringLiteral)||LA61_1==29||LA61_1==36||(LA61_1 >= 40 && LA61_1 <= 41)||(LA61_1 >= 44 && LA61_1 <= 45)||LA61_1==53||LA61_1==65||LA61_1==67||(LA61_1 >= 70 && LA61_1 <= 71)||LA61_1==77||(LA61_1 >= 79 && LA61_1 <= 80)||LA61_1==82||LA61_1==85||LA61_1==92||LA61_1==94||(LA61_1 >= 97 && LA61_1 <= 98)||LA61_1==105||LA61_1==108||LA61_1==111||LA61_1==115||LA61_1==118||LA61_1==121||LA61_1==126) ) {
								alt61=1;
							}

						}

						switch (alt61) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:513:33: ',' variableInitializer
							{
							match(input,43,FOLLOW_43_in_arrayInitializer1540); if (state.failed) return;
							pushFollow(FOLLOW_variableInitializer_in_arrayInitializer1542);
							variableInitializer();
							state._fsp--;
							if (state.failed) return;
							}
							break;

						default :
							break loop61;
						}
					}

					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:513:59: ( ',' )?
					int alt62=2;
					int LA62_0 = input.LA(1);
					if ( (LA62_0==43) ) {
						alt62=1;
					}
					switch (alt62) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:513:60: ','
							{
							match(input,43,FOLLOW_43_in_arrayInitializer1547); if (state.failed) return;
							}
							break;

					}

					}
					break;

			}

			match(input,125,FOLLOW_125_in_arrayInitializer1554); if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 45, arrayInitializer_StartIndex); }

		}
	}
	// $ANTLR end "arrayInitializer"



	// $ANTLR start "modifier"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:516:1: modifier : ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' );
	public final void modifier() throws RecognitionException {
		int modifier_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 46) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:517:5: ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' )
			int alt64=12;
			switch ( input.LA(1) ) {
			case 58:
				{
				alt64=1;
				}
				break;
			case 102:
				{
				alt64=2;
				}
				break;
			case 101:
				{
				alt64=3;
				}
				break;
			case 100:
				{
				alt64=4;
				}
				break;
			case 106:
				{
				alt64=5;
				}
				break;
			case 63:
				{
				alt64=6;
				}
				break;
			case 83:
				{
				alt64=7;
				}
				break;
			case 96:
				{
				alt64=8;
				}
				break;
			case 110:
				{
				alt64=9;
				}
				break;
			case 114:
				{
				alt64=10;
				}
				break;
			case 119:
				{
				alt64=11;
				}
				break;
			case 107:
				{
				alt64=12;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 64, 0, input);
				throw nvae;
			}
			switch (alt64) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:517:9: annotation
					{
					pushFollow(FOLLOW_annotation_in_modifier1573);
					annotation();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:518:9: 'public'
					{
					match(input,102,FOLLOW_102_in_modifier1583); if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:519:9: 'protected'
					{
					match(input,101,FOLLOW_101_in_modifier1593); if (state.failed) return;
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:520:9: 'private'
					{
					match(input,100,FOLLOW_100_in_modifier1603); if (state.failed) return;
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:521:9: 'static'
					{
					match(input,106,FOLLOW_106_in_modifier1613); if (state.failed) return;
					}
					break;
				case 6 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:522:9: 'abstract'
					{
					match(input,63,FOLLOW_63_in_modifier1623); if (state.failed) return;
					}
					break;
				case 7 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:523:9: 'final'
					{
					match(input,83,FOLLOW_83_in_modifier1633); if (state.failed) return;
					}
					break;
				case 8 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:524:9: 'native'
					{
					match(input,96,FOLLOW_96_in_modifier1643); if (state.failed) return;
					}
					break;
				case 9 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:525:9: 'synchronized'
					{
					match(input,110,FOLLOW_110_in_modifier1653); if (state.failed) return;
					}
					break;
				case 10 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:526:9: 'transient'
					{
					match(input,114,FOLLOW_114_in_modifier1663); if (state.failed) return;
					}
					break;
				case 11 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:527:9: 'volatile'
					{
					match(input,119,FOLLOW_119_in_modifier1673); if (state.failed) return;
					}
					break;
				case 12 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:528:9: 'strictfp'
					{
					match(input,107,FOLLOW_107_in_modifier1683); if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 46, modifier_StartIndex); }

		}
	}
	// $ANTLR end "modifier"



	// $ANTLR start "packageOrTypeName"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:531:1: packageOrTypeName : Identifier ( '.' Identifier )* ;
	public final void packageOrTypeName() throws RecognitionException {
		int packageOrTypeName_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 47) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:532:5: ( Identifier ( '.' Identifier )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:532:7: Identifier ( '.' Identifier )*
			{
			match(input,Identifier,FOLLOW_Identifier_in_packageOrTypeName1700); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:532:18: ( '.' Identifier )*
			loop65:
			while (true) {
				int alt65=2;
				int LA65_0 = input.LA(1);
				if ( (LA65_0==47) ) {
					int LA65_1 = input.LA(2);
					if ( (LA65_1==Identifier) ) {
						int LA65_2 = input.LA(3);
						if ( (synpred85_Java()) ) {
							alt65=1;
						}

					}

				}

				switch (alt65) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:532:19: '.' Identifier
					{
					match(input,47,FOLLOW_47_in_packageOrTypeName1703); if (state.failed) return;
					match(input,Identifier,FOLLOW_Identifier_in_packageOrTypeName1705); if (state.failed) return;
					}
					break;

				default :
					break loop65;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 47, packageOrTypeName_StartIndex); }

		}
	}
	// $ANTLR end "packageOrTypeName"



	// $ANTLR start "enumConstantName"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:535:1: enumConstantName : Identifier ;
	public final void enumConstantName() throws RecognitionException {
		int enumConstantName_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 48) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:536:5: ( Identifier )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:536:9: Identifier
			{
			match(input,Identifier,FOLLOW_Identifier_in_enumConstantName1726); if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 48, enumConstantName_StartIndex); }

		}
	}
	// $ANTLR end "enumConstantName"



	// $ANTLR start "typeName"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:539:1: typeName : ( Identifier | packageOrTypeName '.' Identifier );
	public final void typeName() throws RecognitionException {
		int typeName_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 49) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:540:5: ( Identifier | packageOrTypeName '.' Identifier )
			int alt66=2;
			int LA66_0 = input.LA(1);
			if ( (LA66_0==Identifier) ) {
				int LA66_1 = input.LA(2);
				if ( (LA66_1==EOF) ) {
					alt66=1;
				}
				else if ( (LA66_1==47) ) {
					alt66=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 66, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 66, 0, input);
				throw nvae;
			}

			switch (alt66) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:540:9: Identifier
					{
					match(input,Identifier,FOLLOW_Identifier_in_typeName1745); if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:541:9: packageOrTypeName '.' Identifier
					{
					pushFollow(FOLLOW_packageOrTypeName_in_typeName1755);
					packageOrTypeName();
					state._fsp--;
					if (state.failed) return;
					match(input,47,FOLLOW_47_in_typeName1757); if (state.failed) return;
					match(input,Identifier,FOLLOW_Identifier_in_typeName1759); if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 49, typeName_StartIndex); }

		}
	}
	// $ANTLR end "typeName"


	public static class type_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "type"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:544:1: type : ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* ( '[' ']' )* | primitiveType ( '[' ']' )* );
	public final JavaParser.type_return type() throws RecognitionException {
		JavaParser.type_return retval = new JavaParser.type_return();
		retval.start = input.LT(1);
		int type_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 50) ) { return retval; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:545:5: ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* ( '[' ']' )* | primitiveType ( '[' ']' )* )
			int alt72=2;
			int LA72_0 = input.LA(1);
			if ( (LA72_0==Identifier) ) {
				alt72=1;
			}
			else if ( (LA72_0==65||LA72_0==67||LA72_0==71||LA72_0==77||LA72_0==85||LA72_0==92||LA72_0==94||LA72_0==105) ) {
				alt72=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 72, 0, input);
				throw nvae;
			}

			switch (alt72) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:545:7: Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* ( '[' ']' )*
					{
					match(input,Identifier,FOLLOW_Identifier_in_type1776); if (state.failed) return retval;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:545:18: ( typeArguments )?
					int alt67=2;
					int LA67_0 = input.LA(1);
					if ( (LA67_0==53) ) {
						int LA67_1 = input.LA(2);
						if ( (LA67_1==Identifier||LA67_1==57||LA67_1==65||LA67_1==67||LA67_1==71||LA67_1==77||LA67_1==85||LA67_1==92||LA67_1==94||LA67_1==105) ) {
							alt67=1;
						}
					}
					switch (alt67) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:545:19: typeArguments
							{
							pushFollow(FOLLOW_typeArguments_in_type1779);
							typeArguments();
							state._fsp--;
							if (state.failed) return retval;
							}
							break;

					}

					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:545:35: ( '.' Identifier ( typeArguments )? )*
					loop69:
					while (true) {
						int alt69=2;
						int LA69_0 = input.LA(1);
						if ( (LA69_0==47) ) {
							alt69=1;
						}

						switch (alt69) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:545:36: '.' Identifier ( typeArguments )?
							{
							match(input,47,FOLLOW_47_in_type1784); if (state.failed) return retval;
							match(input,Identifier,FOLLOW_Identifier_in_type1786); if (state.failed) return retval;
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:545:51: ( typeArguments )?
							int alt68=2;
							int LA68_0 = input.LA(1);
							if ( (LA68_0==53) ) {
								int LA68_1 = input.LA(2);
								if ( (LA68_1==Identifier||LA68_1==57||LA68_1==65||LA68_1==67||LA68_1==71||LA68_1==77||LA68_1==85||LA68_1==92||LA68_1==94||LA68_1==105) ) {
									alt68=1;
								}
							}
							switch (alt68) {
								case 1 :
									// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:545:52: typeArguments
									{
									pushFollow(FOLLOW_typeArguments_in_type1789);
									typeArguments();
									state._fsp--;
									if (state.failed) return retval;
									}
									break;

							}

							}
							break;

						default :
							break loop69;
						}
					}

					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:545:71: ( '[' ']' )*
					loop70:
					while (true) {
						int alt70=2;
						int LA70_0 = input.LA(1);
						if ( (LA70_0==59) ) {
							alt70=1;
						}

						switch (alt70) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:545:72: '[' ']'
							{
							match(input,59,FOLLOW_59_in_type1797); if (state.failed) return retval;
							match(input,60,FOLLOW_60_in_type1799); if (state.failed) return retval;
							}
							break;

						default :
							break loop70;
						}
					}

					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:546:7: primitiveType ( '[' ']' )*
					{
					pushFollow(FOLLOW_primitiveType_in_type1809);
					primitiveType();
					state._fsp--;
					if (state.failed) return retval;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:546:21: ( '[' ']' )*
					loop71:
					while (true) {
						int alt71=2;
						int LA71_0 = input.LA(1);
						if ( (LA71_0==59) ) {
							alt71=1;
						}

						switch (alt71) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:546:22: '[' ']'
							{
							match(input,59,FOLLOW_59_in_type1812); if (state.failed) return retval;
							match(input,60,FOLLOW_60_in_type1814); if (state.failed) return retval;
							}
							break;

						default :
							break loop71;
						}
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 50, type_StartIndex); }

		}
		return retval;
	}
	// $ANTLR end "type"



	// $ANTLR start "primitiveType"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:549:1: primitiveType : ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' );
	public final void primitiveType() throws RecognitionException {
		int primitiveType_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 51) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:550:5: ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:
			{
			if ( input.LA(1)==65||input.LA(1)==67||input.LA(1)==71||input.LA(1)==77||input.LA(1)==85||input.LA(1)==92||input.LA(1)==94||input.LA(1)==105 ) {
				input.consume();
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 51, primitiveType_StartIndex); }

		}
	}
	// $ANTLR end "primitiveType"


	public static class variableModifier_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "variableModifier"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:560:1: variableModifier : ( 'final' | annotation );
	public final JavaParser.variableModifier_return variableModifier() throws RecognitionException {
		JavaParser.variableModifier_return retval = new JavaParser.variableModifier_return();
		retval.start = input.LT(1);
		int variableModifier_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 52) ) { return retval; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:561:5: ( 'final' | annotation )
			int alt73=2;
			int LA73_0 = input.LA(1);
			if ( (LA73_0==83) ) {
				alt73=1;
			}
			else if ( (LA73_0==58) ) {
				alt73=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 73, 0, input);
				throw nvae;
			}

			switch (alt73) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:561:7: 'final'
					{
					match(input,83,FOLLOW_83_in_variableModifier1908); if (state.failed) return retval;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:562:9: annotation
					{
					pushFollow(FOLLOW_annotation_in_variableModifier1918);
					annotation();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 52, variableModifier_StartIndex); }

		}
		return retval;
	}
	// $ANTLR end "variableModifier"



	// $ANTLR start "typeArguments"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:565:1: typeArguments : '<' typeArgument ( ',' typeArgument )* '>' ;
	public final void typeArguments() throws RecognitionException {
		int typeArguments_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 53) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:566:5: ( '<' typeArgument ( ',' typeArgument )* '>' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:566:7: '<' typeArgument ( ',' typeArgument )* '>'
			{
			match(input,53,FOLLOW_53_in_typeArguments1935); if (state.failed) return;
			pushFollow(FOLLOW_typeArgument_in_typeArguments1937);
			typeArgument();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:566:24: ( ',' typeArgument )*
			loop74:
			while (true) {
				int alt74=2;
				int LA74_0 = input.LA(1);
				if ( (LA74_0==43) ) {
					alt74=1;
				}

				switch (alt74) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:566:25: ',' typeArgument
					{
					match(input,43,FOLLOW_43_in_typeArguments1940); if (state.failed) return;
					pushFollow(FOLLOW_typeArgument_in_typeArguments1942);
					typeArgument();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop74;
				}
			}

			match(input,56,FOLLOW_56_in_typeArguments1946); if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 53, typeArguments_StartIndex); }

		}
	}
	// $ANTLR end "typeArguments"



	// $ANTLR start "typeArgument"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:569:1: typeArgument : ( type | '?' ( ( 'extends' | 'super' ) type )? );
	public final void typeArgument() throws RecognitionException {
		int typeArgument_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 54) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:570:5: ( type | '?' ( ( 'extends' | 'super' ) type )? )
			int alt76=2;
			int LA76_0 = input.LA(1);
			if ( (LA76_0==Identifier||LA76_0==65||LA76_0==67||LA76_0==71||LA76_0==77||LA76_0==85||LA76_0==92||LA76_0==94||LA76_0==105) ) {
				alt76=1;
			}
			else if ( (LA76_0==57) ) {
				alt76=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 76, 0, input);
				throw nvae;
			}

			switch (alt76) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:570:7: type
					{
					pushFollow(FOLLOW_type_in_typeArgument1963);
					type();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:571:7: '?' ( ( 'extends' | 'super' ) type )?
					{
					match(input,57,FOLLOW_57_in_typeArgument1971); if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:571:11: ( ( 'extends' | 'super' ) type )?
					int alt75=2;
					int LA75_0 = input.LA(1);
					if ( (LA75_0==81||LA75_0==108) ) {
						alt75=1;
					}
					switch (alt75) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:571:12: ( 'extends' | 'super' ) type
							{
							if ( input.LA(1)==81||input.LA(1)==108 ) {
								input.consume();
								state.errorRecovery=false;
								state.failed=false;
							}
							else {
								if (state.backtracking>0) {state.failed=true; return;}
								MismatchedSetException mse = new MismatchedSetException(null,input);
								throw mse;
							}
							pushFollow(FOLLOW_type_in_typeArgument1982);
							type();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 54, typeArgument_StartIndex); }

		}
	}
	// $ANTLR end "typeArgument"



	// $ANTLR start "qualifiedNameList"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:574:1: qualifiedNameList : qualifiedName ( ',' qualifiedName )* ;
	public final void qualifiedNameList() throws RecognitionException {
		int qualifiedNameList_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 55) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:575:5: ( qualifiedName ( ',' qualifiedName )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:575:7: qualifiedName ( ',' qualifiedName )*
			{
			pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList2001);
			qualifiedName();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:575:21: ( ',' qualifiedName )*
			loop77:
			while (true) {
				int alt77=2;
				int LA77_0 = input.LA(1);
				if ( (LA77_0==43) ) {
					alt77=1;
				}

				switch (alt77) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:575:22: ',' qualifiedName
					{
					match(input,43,FOLLOW_43_in_qualifiedNameList2004); if (state.failed) return;
					pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList2006);
					qualifiedName();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop77;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 55, qualifiedNameList_StartIndex); }

		}
	}
	// $ANTLR end "qualifiedNameList"



	// $ANTLR start "formalParameters"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:578:1: formalParameters : '(' ( formalParameterDecls )? ')' ;
	public final void formalParameters() throws RecognitionException {
		int formalParameters_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 56) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:579:5: ( '(' ( formalParameterDecls )? ')' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:579:7: '(' ( formalParameterDecls )? ')'
			{
			match(input,36,FOLLOW_36_in_formalParameters2025); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:579:11: ( formalParameterDecls )?
			int alt78=2;
			int LA78_0 = input.LA(1);
			if ( (LA78_0==Identifier||LA78_0==58||LA78_0==65||LA78_0==67||LA78_0==71||LA78_0==77||LA78_0==83||LA78_0==85||LA78_0==92||LA78_0==94||LA78_0==105) ) {
				alt78=1;
			}
			switch (alt78) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:579:11: formalParameterDecls
					{
					pushFollow(FOLLOW_formalParameterDecls_in_formalParameters2027);
					formalParameterDecls();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			match(input,37,FOLLOW_37_in_formalParameters2030); if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 56, formalParameters_StartIndex); }

		}
	}
	// $ANTLR end "formalParameters"



	// $ANTLR start "formalParameterDecls"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:582:1: formalParameterDecls : ( variableModifier )* type ( formalParameterDeclsRest )? ;
	public final void formalParameterDecls() throws RecognitionException {
		int formalParameterDecls_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 57) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:583:5: ( ( variableModifier )* type ( formalParameterDeclsRest )? )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:583:7: ( variableModifier )* type ( formalParameterDeclsRest )?
			{
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:583:7: ( variableModifier )*
			loop79:
			while (true) {
				int alt79=2;
				int LA79_0 = input.LA(1);
				if ( (LA79_0==58||LA79_0==83) ) {
					alt79=1;
				}

				switch (alt79) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:583:7: variableModifier
					{
					pushFollow(FOLLOW_variableModifier_in_formalParameterDecls2047);
					variableModifier();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop79;
				}
			}

			pushFollow(FOLLOW_type_in_formalParameterDecls2050);
			type();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:583:30: ( formalParameterDeclsRest )?
			int alt80=2;
			int LA80_0 = input.LA(1);
			if ( (LA80_0==Identifier||LA80_0==48) ) {
				alt80=1;
			}
			switch (alt80) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:583:30: formalParameterDeclsRest
					{
					pushFollow(FOLLOW_formalParameterDeclsRest_in_formalParameterDecls2052);
					formalParameterDeclsRest();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 57, formalParameterDecls_StartIndex); }

		}
	}
	// $ANTLR end "formalParameterDecls"



	// $ANTLR start "formalParameterDeclsRest"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:586:1: formalParameterDeclsRest : ( variableDeclaratorId ( ',' formalParameterDecls )? | '...' variableDeclaratorId );
	public final void formalParameterDeclsRest() throws RecognitionException {
		int formalParameterDeclsRest_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 58) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:587:5: ( variableDeclaratorId ( ',' formalParameterDecls )? | '...' variableDeclaratorId )
			int alt82=2;
			int LA82_0 = input.LA(1);
			if ( (LA82_0==Identifier) ) {
				alt82=1;
			}
			else if ( (LA82_0==48) ) {
				alt82=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 82, 0, input);
				throw nvae;
			}

			switch (alt82) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:587:7: variableDeclaratorId ( ',' formalParameterDecls )?
					{
					pushFollow(FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest2070);
					variableDeclaratorId();
					state._fsp--;
					if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:587:28: ( ',' formalParameterDecls )?
					int alt81=2;
					int LA81_0 = input.LA(1);
					if ( (LA81_0==43) ) {
						alt81=1;
					}
					switch (alt81) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:587:29: ',' formalParameterDecls
							{
							match(input,43,FOLLOW_43_in_formalParameterDeclsRest2073); if (state.failed) return;
							pushFollow(FOLLOW_formalParameterDecls_in_formalParameterDeclsRest2075);
							formalParameterDecls();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:588:9: '...' variableDeclaratorId
					{
					match(input,48,FOLLOW_48_in_formalParameterDeclsRest2087); if (state.failed) return;
					pushFollow(FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest2089);
					variableDeclaratorId();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 58, formalParameterDeclsRest_StartIndex); }

		}
	}
	// $ANTLR end "formalParameterDeclsRest"



	// $ANTLR start "methodBody"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:591:1: methodBody : block ;
	public final void methodBody() throws RecognitionException {
		int methodBody_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 59) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:592:5: ( block )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:592:7: block
			{
			pushFollow(FOLLOW_block_in_methodBody2106);
			block();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 59, methodBody_StartIndex); }

		}
	}
	// $ANTLR end "methodBody"



	// $ANTLR start "qualifiedName"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:595:1: qualifiedName : Identifier ( '.' Identifier )* ;
	public final void qualifiedName() throws RecognitionException {
		int qualifiedName_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 60) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:596:5: ( Identifier ( '.' Identifier )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:596:7: Identifier ( '.' Identifier )*
			{
			match(input,Identifier,FOLLOW_Identifier_in_qualifiedName2123); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:596:18: ( '.' Identifier )*
			loop83:
			while (true) {
				int alt83=2;
				int LA83_0 = input.LA(1);
				if ( (LA83_0==47) ) {
					alt83=1;
				}

				switch (alt83) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:596:19: '.' Identifier
					{
					match(input,47,FOLLOW_47_in_qualifiedName2126); if (state.failed) return;
					match(input,Identifier,FOLLOW_Identifier_in_qualifiedName2128); if (state.failed) return;
					}
					break;

				default :
					break loop83;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 60, qualifiedName_StartIndex); }

		}
	}
	// $ANTLR end "qualifiedName"



	// $ANTLR start "literal"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:599:1: literal : ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | booleanLiteral | 'null' );
	public final void literal() throws RecognitionException {
		int literal_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 61) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:600:5: ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | booleanLiteral | 'null' )
			int alt84=6;
			switch ( input.LA(1) ) {
			case DecimalLiteral:
			case HexLiteral:
			case OctalLiteral:
				{
				alt84=1;
				}
				break;
			case FloatingPointLiteral:
				{
				alt84=2;
				}
				break;
			case CharacterLiteral:
				{
				alt84=3;
				}
				break;
			case StringLiteral:
				{
				alt84=4;
				}
				break;
			case 82:
			case 115:
				{
				alt84=5;
				}
				break;
			case 98:
				{
				alt84=6;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 84, 0, input);
				throw nvae;
			}
			switch (alt84) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:600:9: integerLiteral
					{
					pushFollow(FOLLOW_integerLiteral_in_literal2150);
					integerLiteral();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:601:9: FloatingPointLiteral
					{
					match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_literal2160); if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:602:9: CharacterLiteral
					{
					match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_literal2170); if (state.failed) return;
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:603:9: StringLiteral
					{
					match(input,StringLiteral,FOLLOW_StringLiteral_in_literal2180); if (state.failed) return;
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:604:9: booleanLiteral
					{
					pushFollow(FOLLOW_booleanLiteral_in_literal2190);
					booleanLiteral();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 6 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:605:9: 'null'
					{
					match(input,98,FOLLOW_98_in_literal2200); if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 61, literal_StartIndex); }

		}
	}
	// $ANTLR end "literal"



	// $ANTLR start "integerLiteral"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:608:1: integerLiteral : ( HexLiteral | OctalLiteral | DecimalLiteral );
	public final void integerLiteral() throws RecognitionException {
		int integerLiteral_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 62) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:609:5: ( HexLiteral | OctalLiteral | DecimalLiteral )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:
			{
			if ( input.LA(1)==DecimalLiteral||input.LA(1)==HexLiteral||input.LA(1)==OctalLiteral ) {
				input.consume();
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 62, integerLiteral_StartIndex); }

		}
	}
	// $ANTLR end "integerLiteral"



	// $ANTLR start "booleanLiteral"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:614:1: booleanLiteral : ( 'true' | 'false' );
	public final void booleanLiteral() throws RecognitionException {
		int booleanLiteral_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 63) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:615:5: ( 'true' | 'false' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:
			{
			if ( input.LA(1)==82||input.LA(1)==115 ) {
				input.consume();
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 63, booleanLiteral_StartIndex); }

		}
	}
	// $ANTLR end "booleanLiteral"



	// $ANTLR start "annotations"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:621:1: annotations : ( annotation )+ ;
	public final void annotations() throws RecognitionException {
		int annotations_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 64) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:622:5: ( ( annotation )+ )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:622:7: ( annotation )+
			{
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:622:7: ( annotation )+
			int cnt85=0;
			loop85:
			while (true) {
				int alt85=2;
				int LA85_0 = input.LA(1);
				if ( (LA85_0==58) ) {
					int LA85_3 = input.LA(2);
					if ( (LA85_3==Identifier) ) {
						int LA85_22 = input.LA(3);
						if ( (synpred120_Java()) ) {
							alt85=1;
						}

					}

				}

				switch (alt85) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:622:7: annotation
					{
					pushFollow(FOLLOW_annotation_in_annotations2287);
					annotation();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					if ( cnt85 >= 1 ) break loop85;
					if (state.backtracking>0) {state.failed=true; return;}
					EarlyExitException eee = new EarlyExitException(85, input);
					throw eee;
				}
				cnt85++;
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 64, annotations_StartIndex); }

		}
	}
	// $ANTLR end "annotations"



	// $ANTLR start "annotation"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:625:1: annotation : '@' annotationName ( '(' ( elementValuePairs )? ')' )? ;
	public final void annotation() throws RecognitionException {
		int annotation_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 65) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:626:5: ( '@' annotationName ( '(' ( elementValuePairs )? ')' )? )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:626:7: '@' annotationName ( '(' ( elementValuePairs )? ')' )?
			{
			match(input,58,FOLLOW_58_in_annotation2305); if (state.failed) return;
			pushFollow(FOLLOW_annotationName_in_annotation2307);
			annotationName();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:626:26: ( '(' ( elementValuePairs )? ')' )?
			int alt87=2;
			int LA87_0 = input.LA(1);
			if ( (LA87_0==36) ) {
				alt87=1;
			}
			switch (alt87) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:626:27: '(' ( elementValuePairs )? ')'
					{
					match(input,36,FOLLOW_36_in_annotation2310); if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:626:31: ( elementValuePairs )?
					int alt86=2;
					int LA86_0 = input.LA(1);
					if ( ((LA86_0 >= CharacterLiteral && LA86_0 <= DecimalLiteral)||LA86_0==FloatingPointLiteral||(LA86_0 >= HexLiteral && LA86_0 <= Identifier)||(LA86_0 >= OctalLiteral && LA86_0 <= StringLiteral)||LA86_0==29||LA86_0==36||(LA86_0 >= 40 && LA86_0 <= 41)||(LA86_0 >= 44 && LA86_0 <= 45)||LA86_0==53||LA86_0==58||LA86_0==65||LA86_0==67||(LA86_0 >= 70 && LA86_0 <= 71)||LA86_0==77||(LA86_0 >= 79 && LA86_0 <= 80)||LA86_0==82||LA86_0==85||LA86_0==92||LA86_0==94||(LA86_0 >= 97 && LA86_0 <= 98)||LA86_0==105||LA86_0==108||LA86_0==111||LA86_0==115||LA86_0==118||LA86_0==121||LA86_0==126) ) {
						alt86=1;
					}
					switch (alt86) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:626:31: elementValuePairs
							{
							pushFollow(FOLLOW_elementValuePairs_in_annotation2312);
							elementValuePairs();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					match(input,37,FOLLOW_37_in_annotation2315); if (state.failed) return;
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 65, annotation_StartIndex); }

		}
	}
	// $ANTLR end "annotation"



	// $ANTLR start "annotationName"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:629:1: annotationName : Identifier ( '.' Identifier )* ;
	public final void annotationName() throws RecognitionException {
		int annotationName_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 66) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:630:5: ( Identifier ( '.' Identifier )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:630:7: Identifier ( '.' Identifier )*
			{
			match(input,Identifier,FOLLOW_Identifier_in_annotationName2334); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:630:18: ( '.' Identifier )*
			loop88:
			while (true) {
				int alt88=2;
				int LA88_0 = input.LA(1);
				if ( (LA88_0==47) ) {
					alt88=1;
				}

				switch (alt88) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:630:19: '.' Identifier
					{
					match(input,47,FOLLOW_47_in_annotationName2337); if (state.failed) return;
					match(input,Identifier,FOLLOW_Identifier_in_annotationName2339); if (state.failed) return;
					}
					break;

				default :
					break loop88;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 66, annotationName_StartIndex); }

		}
	}
	// $ANTLR end "annotationName"



	// $ANTLR start "elementValuePairs"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:633:1: elementValuePairs : elementValuePair ( ',' elementValuePair )* ;
	public final void elementValuePairs() throws RecognitionException {
		int elementValuePairs_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 67) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:634:5: ( elementValuePair ( ',' elementValuePair )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:634:7: elementValuePair ( ',' elementValuePair )*
			{
			pushFollow(FOLLOW_elementValuePair_in_elementValuePairs2358);
			elementValuePair();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:634:24: ( ',' elementValuePair )*
			loop89:
			while (true) {
				int alt89=2;
				int LA89_0 = input.LA(1);
				if ( (LA89_0==43) ) {
					alt89=1;
				}

				switch (alt89) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:634:25: ',' elementValuePair
					{
					match(input,43,FOLLOW_43_in_elementValuePairs2361); if (state.failed) return;
					pushFollow(FOLLOW_elementValuePair_in_elementValuePairs2363);
					elementValuePair();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop89;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 67, elementValuePairs_StartIndex); }

		}
	}
	// $ANTLR end "elementValuePairs"



	// $ANTLR start "elementValuePair"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:637:1: elementValuePair : ( Identifier '=' )? elementValue ;
	public final void elementValuePair() throws RecognitionException {
		int elementValuePair_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 68) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:638:5: ( ( Identifier '=' )? elementValue )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:638:7: ( Identifier '=' )? elementValue
			{
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:638:7: ( Identifier '=' )?
			int alt90=2;
			int LA90_0 = input.LA(1);
			if ( (LA90_0==Identifier) ) {
				int LA90_1 = input.LA(2);
				if ( (LA90_1==54) ) {
					alt90=1;
				}
			}
			switch (alt90) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:638:8: Identifier '='
					{
					match(input,Identifier,FOLLOW_Identifier_in_elementValuePair2383); if (state.failed) return;
					match(input,54,FOLLOW_54_in_elementValuePair2385); if (state.failed) return;
					}
					break;

			}

			pushFollow(FOLLOW_elementValue_in_elementValuePair2389);
			elementValue();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 68, elementValuePair_StartIndex); }

		}
	}
	// $ANTLR end "elementValuePair"



	// $ANTLR start "elementValue"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:641:1: elementValue : ( conditionalExpression | annotation | elementValueArrayInitializer );
	public final void elementValue() throws RecognitionException {
		int elementValue_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 69) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:642:5: ( conditionalExpression | annotation | elementValueArrayInitializer )
			int alt91=3;
			switch ( input.LA(1) ) {
			case CharacterLiteral:
			case DecimalLiteral:
			case FloatingPointLiteral:
			case HexLiteral:
			case Identifier:
			case OctalLiteral:
			case StringLiteral:
			case 29:
			case 36:
			case 40:
			case 41:
			case 44:
			case 45:
			case 53:
			case 65:
			case 67:
			case 70:
			case 71:
			case 77:
			case 79:
			case 80:
			case 82:
			case 85:
			case 92:
			case 94:
			case 97:
			case 98:
			case 105:
			case 108:
			case 111:
			case 115:
			case 118:
			case 126:
				{
				alt91=1;
				}
				break;
			case 58:
				{
				alt91=2;
				}
				break;
			case 121:
				{
				alt91=3;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 91, 0, input);
				throw nvae;
			}
			switch (alt91) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:642:7: conditionalExpression
					{
					pushFollow(FOLLOW_conditionalExpression_in_elementValue2406);
					conditionalExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:643:9: annotation
					{
					pushFollow(FOLLOW_annotation_in_elementValue2416);
					annotation();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:644:9: elementValueArrayInitializer
					{
					pushFollow(FOLLOW_elementValueArrayInitializer_in_elementValue2426);
					elementValueArrayInitializer();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 69, elementValue_StartIndex); }

		}
	}
	// $ANTLR end "elementValue"



	// $ANTLR start "elementValueArrayInitializer"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:647:1: elementValueArrayInitializer : '{' ( elementValue ( ',' elementValue )* )? '}' ;
	public final void elementValueArrayInitializer() throws RecognitionException {
		int elementValueArrayInitializer_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 70) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:648:5: ( '{' ( elementValue ( ',' elementValue )* )? '}' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:648:7: '{' ( elementValue ( ',' elementValue )* )? '}'
			{
			match(input,121,FOLLOW_121_in_elementValueArrayInitializer2443); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:648:11: ( elementValue ( ',' elementValue )* )?
			int alt93=2;
			int LA93_0 = input.LA(1);
			if ( ((LA93_0 >= CharacterLiteral && LA93_0 <= DecimalLiteral)||LA93_0==FloatingPointLiteral||(LA93_0 >= HexLiteral && LA93_0 <= Identifier)||(LA93_0 >= OctalLiteral && LA93_0 <= StringLiteral)||LA93_0==29||LA93_0==36||(LA93_0 >= 40 && LA93_0 <= 41)||(LA93_0 >= 44 && LA93_0 <= 45)||LA93_0==53||LA93_0==58||LA93_0==65||LA93_0==67||(LA93_0 >= 70 && LA93_0 <= 71)||LA93_0==77||(LA93_0 >= 79 && LA93_0 <= 80)||LA93_0==82||LA93_0==85||LA93_0==92||LA93_0==94||(LA93_0 >= 97 && LA93_0 <= 98)||LA93_0==105||LA93_0==108||LA93_0==111||LA93_0==115||LA93_0==118||LA93_0==121||LA93_0==126) ) {
				alt93=1;
			}
			switch (alt93) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:648:12: elementValue ( ',' elementValue )*
					{
					pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer2446);
					elementValue();
					state._fsp--;
					if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:648:25: ( ',' elementValue )*
					loop92:
					while (true) {
						int alt92=2;
						int LA92_0 = input.LA(1);
						if ( (LA92_0==43) ) {
							alt92=1;
						}

						switch (alt92) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:648:26: ',' elementValue
							{
							match(input,43,FOLLOW_43_in_elementValueArrayInitializer2449); if (state.failed) return;
							pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer2451);
							elementValue();
							state._fsp--;
							if (state.failed) return;
							}
							break;

						default :
							break loop92;
						}
					}

					}
					break;

			}

			match(input,125,FOLLOW_125_in_elementValueArrayInitializer2458); if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 70, elementValueArrayInitializer_StartIndex); }

		}
	}
	// $ANTLR end "elementValueArrayInitializer"



	// $ANTLR start "annotationTypeDeclaration"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:651:1: annotationTypeDeclaration : '@' 'interface' Identifier annotationTypeBody ;
	public final void annotationTypeDeclaration() throws RecognitionException {
		int annotationTypeDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 71) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:652:5: ( '@' 'interface' Identifier annotationTypeBody )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:652:7: '@' 'interface' Identifier annotationTypeBody
			{
			match(input,58,FOLLOW_58_in_annotationTypeDeclaration2475); if (state.failed) return;
			match(input,93,FOLLOW_93_in_annotationTypeDeclaration2477); if (state.failed) return;
			match(input,Identifier,FOLLOW_Identifier_in_annotationTypeDeclaration2479); if (state.failed) return;
			pushFollow(FOLLOW_annotationTypeBody_in_annotationTypeDeclaration2481);
			annotationTypeBody();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 71, annotationTypeDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "annotationTypeDeclaration"



	// $ANTLR start "annotationTypeBody"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:655:1: annotationTypeBody : '{' ( annotationTypeElementDeclarations )? '}' ;
	public final void annotationTypeBody() throws RecognitionException {
		int annotationTypeBody_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 72) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:656:5: ( '{' ( annotationTypeElementDeclarations )? '}' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:656:7: '{' ( annotationTypeElementDeclarations )? '}'
			{
			match(input,121,FOLLOW_121_in_annotationTypeBody2498); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:656:11: ( annotationTypeElementDeclarations )?
			int alt94=2;
			int LA94_0 = input.LA(1);
			if ( (LA94_0==ENUM||LA94_0==Identifier||LA94_0==58||LA94_0==63||LA94_0==65||LA94_0==67||(LA94_0 >= 71 && LA94_0 <= 72)||LA94_0==77||LA94_0==83||LA94_0==85||(LA94_0 >= 92 && LA94_0 <= 94)||LA94_0==96||(LA94_0 >= 100 && LA94_0 <= 102)||(LA94_0 >= 105 && LA94_0 <= 107)||LA94_0==110||LA94_0==114||LA94_0==119) ) {
				alt94=1;
			}
			switch (alt94) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:656:12: annotationTypeElementDeclarations
					{
					pushFollow(FOLLOW_annotationTypeElementDeclarations_in_annotationTypeBody2501);
					annotationTypeElementDeclarations();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			match(input,125,FOLLOW_125_in_annotationTypeBody2505); if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 72, annotationTypeBody_StartIndex); }

		}
	}
	// $ANTLR end "annotationTypeBody"



	// $ANTLR start "annotationTypeElementDeclarations"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:659:1: annotationTypeElementDeclarations : ( annotationTypeElementDeclaration ) ( annotationTypeElementDeclaration )* ;
	public final void annotationTypeElementDeclarations() throws RecognitionException {
		int annotationTypeElementDeclarations_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 73) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:660:5: ( ( annotationTypeElementDeclaration ) ( annotationTypeElementDeclaration )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:660:7: ( annotationTypeElementDeclaration ) ( annotationTypeElementDeclaration )*
			{
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:660:7: ( annotationTypeElementDeclaration )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:660:8: annotationTypeElementDeclaration
			{
			pushFollow(FOLLOW_annotationTypeElementDeclaration_in_annotationTypeElementDeclarations2523);
			annotationTypeElementDeclaration();
			state._fsp--;
			if (state.failed) return;
			}

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:660:42: ( annotationTypeElementDeclaration )*
			loop95:
			while (true) {
				int alt95=2;
				int LA95_0 = input.LA(1);
				if ( (LA95_0==ENUM||LA95_0==Identifier||LA95_0==58||LA95_0==63||LA95_0==65||LA95_0==67||(LA95_0 >= 71 && LA95_0 <= 72)||LA95_0==77||LA95_0==83||LA95_0==85||(LA95_0 >= 92 && LA95_0 <= 94)||LA95_0==96||(LA95_0 >= 100 && LA95_0 <= 102)||(LA95_0 >= 105 && LA95_0 <= 107)||LA95_0==110||LA95_0==114||LA95_0==119) ) {
					alt95=1;
				}

				switch (alt95) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:660:43: annotationTypeElementDeclaration
					{
					pushFollow(FOLLOW_annotationTypeElementDeclaration_in_annotationTypeElementDeclarations2527);
					annotationTypeElementDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop95;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 73, annotationTypeElementDeclarations_StartIndex); }

		}
	}
	// $ANTLR end "annotationTypeElementDeclarations"



	// $ANTLR start "annotationTypeElementDeclaration"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:663:1: annotationTypeElementDeclaration : ( modifier )* annotationTypeElementRest ;
	public final void annotationTypeElementDeclaration() throws RecognitionException {
		int annotationTypeElementDeclaration_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 74) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:664:5: ( ( modifier )* annotationTypeElementRest )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:664:7: ( modifier )* annotationTypeElementRest
			{
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:664:7: ( modifier )*
			loop96:
			while (true) {
				int alt96=2;
				int LA96_0 = input.LA(1);
				if ( (LA96_0==58) ) {
					int LA96_6 = input.LA(2);
					if ( (LA96_6==Identifier) ) {
						alt96=1;
					}

				}
				else if ( (LA96_0==63||LA96_0==83||LA96_0==96||(LA96_0 >= 100 && LA96_0 <= 102)||(LA96_0 >= 106 && LA96_0 <= 107)||LA96_0==110||LA96_0==114||LA96_0==119) ) {
					alt96=1;
				}

				switch (alt96) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:664:8: modifier
					{
					pushFollow(FOLLOW_modifier_in_annotationTypeElementDeclaration2547);
					modifier();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop96;
				}
			}

			pushFollow(FOLLOW_annotationTypeElementRest_in_annotationTypeElementDeclaration2551);
			annotationTypeElementRest();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 74, annotationTypeElementDeclaration_StartIndex); }

		}
	}
	// $ANTLR end "annotationTypeElementDeclaration"



	// $ANTLR start "annotationTypeElementRest"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:667:1: annotationTypeElementRest : ( type annotationMethodOrConstantRest ';' | classDeclaration ( ';' )? | interfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? );
	public final void annotationTypeElementRest() throws RecognitionException {
		int annotationTypeElementRest_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 75) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:668:5: ( type annotationMethodOrConstantRest ';' | classDeclaration ( ';' )? | interfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? )
			int alt101=5;
			switch ( input.LA(1) ) {
			case Identifier:
			case 65:
			case 67:
			case 71:
			case 77:
			case 85:
			case 92:
			case 94:
			case 105:
				{
				alt101=1;
				}
				break;
			case 72:
				{
				alt101=2;
				}
				break;
			case ENUM:
				{
				int LA101_4 = input.LA(2);
				if ( (LA101_4==Identifier) ) {
					int LA101_7 = input.LA(3);
					if ( (synpred135_Java()) ) {
						alt101=2;
					}
					else if ( (synpred139_Java()) ) {
						alt101=4;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 101, 7, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 101, 4, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 93:
				{
				alt101=3;
				}
				break;
			case 58:
				{
				int LA101_6 = input.LA(2);
				if ( (LA101_6==93) ) {
					int LA101_8 = input.LA(3);
					if ( (synpred137_Java()) ) {
						alt101=3;
					}
					else if ( (true) ) {
						alt101=5;
					}

				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 101, 6, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 101, 0, input);
				throw nvae;
			}
			switch (alt101) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:668:7: type annotationMethodOrConstantRest ';'
					{
					pushFollow(FOLLOW_type_in_annotationTypeElementRest2568);
					type();
					state._fsp--;
					if (state.failed) return;
					pushFollow(FOLLOW_annotationMethodOrConstantRest_in_annotationTypeElementRest2570);
					annotationMethodOrConstantRest();
					state._fsp--;
					if (state.failed) return;
					match(input,52,FOLLOW_52_in_annotationTypeElementRest2572); if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:669:9: classDeclaration ( ';' )?
					{
					pushFollow(FOLLOW_classDeclaration_in_annotationTypeElementRest2582);
					classDeclaration();
					state._fsp--;
					if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:669:26: ( ';' )?
					int alt97=2;
					int LA97_0 = input.LA(1);
					if ( (LA97_0==52) ) {
						alt97=1;
					}
					switch (alt97) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:669:26: ';'
							{
							match(input,52,FOLLOW_52_in_annotationTypeElementRest2584); if (state.failed) return;
							}
							break;

					}

					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:670:9: interfaceDeclaration ( ';' )?
					{
					pushFollow(FOLLOW_interfaceDeclaration_in_annotationTypeElementRest2595);
					interfaceDeclaration();
					state._fsp--;
					if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:670:30: ( ';' )?
					int alt98=2;
					int LA98_0 = input.LA(1);
					if ( (LA98_0==52) ) {
						alt98=1;
					}
					switch (alt98) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:670:30: ';'
							{
							match(input,52,FOLLOW_52_in_annotationTypeElementRest2597); if (state.failed) return;
							}
							break;

					}

					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:671:9: enumDeclaration ( ';' )?
					{
					pushFollow(FOLLOW_enumDeclaration_in_annotationTypeElementRest2608);
					enumDeclaration();
					state._fsp--;
					if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:671:25: ( ';' )?
					int alt99=2;
					int LA99_0 = input.LA(1);
					if ( (LA99_0==52) ) {
						alt99=1;
					}
					switch (alt99) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:671:25: ';'
							{
							match(input,52,FOLLOW_52_in_annotationTypeElementRest2610); if (state.failed) return;
							}
							break;

					}

					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:672:9: annotationTypeDeclaration ( ';' )?
					{
					pushFollow(FOLLOW_annotationTypeDeclaration_in_annotationTypeElementRest2621);
					annotationTypeDeclaration();
					state._fsp--;
					if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:672:35: ( ';' )?
					int alt100=2;
					int LA100_0 = input.LA(1);
					if ( (LA100_0==52) ) {
						alt100=1;
					}
					switch (alt100) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:672:35: ';'
							{
							match(input,52,FOLLOW_52_in_annotationTypeElementRest2623); if (state.failed) return;
							}
							break;

					}

					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 75, annotationTypeElementRest_StartIndex); }

		}
	}
	// $ANTLR end "annotationTypeElementRest"



	// $ANTLR start "annotationMethodOrConstantRest"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:675:1: annotationMethodOrConstantRest : ( annotationMethodRest | annotationConstantRest );
	public final void annotationMethodOrConstantRest() throws RecognitionException {
		int annotationMethodOrConstantRest_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 76) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:676:5: ( annotationMethodRest | annotationConstantRest )
			int alt102=2;
			int LA102_0 = input.LA(1);
			if ( (LA102_0==Identifier) ) {
				int LA102_1 = input.LA(2);
				if ( (LA102_1==36) ) {
					alt102=1;
				}
				else if ( (LA102_1==43||LA102_1==52||LA102_1==54||LA102_1==59) ) {
					alt102=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 102, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 102, 0, input);
				throw nvae;
			}

			switch (alt102) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:676:7: annotationMethodRest
					{
					pushFollow(FOLLOW_annotationMethodRest_in_annotationMethodOrConstantRest2641);
					annotationMethodRest();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:677:9: annotationConstantRest
					{
					pushFollow(FOLLOW_annotationConstantRest_in_annotationMethodOrConstantRest2651);
					annotationConstantRest();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 76, annotationMethodOrConstantRest_StartIndex); }

		}
	}
	// $ANTLR end "annotationMethodOrConstantRest"



	// $ANTLR start "annotationMethodRest"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:680:1: annotationMethodRest : Identifier '(' ')' ( defaultValue )? ;
	public final void annotationMethodRest() throws RecognitionException {
		int annotationMethodRest_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 77) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:681:6: ( Identifier '(' ')' ( defaultValue )? )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:681:8: Identifier '(' ')' ( defaultValue )?
			{
			match(input,Identifier,FOLLOW_Identifier_in_annotationMethodRest2669); if (state.failed) return;
			match(input,36,FOLLOW_36_in_annotationMethodRest2671); if (state.failed) return;
			match(input,37,FOLLOW_37_in_annotationMethodRest2673); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:681:27: ( defaultValue )?
			int alt103=2;
			int LA103_0 = input.LA(1);
			if ( (LA103_0==74) ) {
				alt103=1;
			}
			switch (alt103) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:681:28: defaultValue
					{
					pushFollow(FOLLOW_defaultValue_in_annotationMethodRest2676);
					defaultValue();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 77, annotationMethodRest_StartIndex); }

		}
	}
	// $ANTLR end "annotationMethodRest"



	// $ANTLR start "annotationConstantRest"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:684:1: annotationConstantRest : variableDeclarators ;
	public final void annotationConstantRest() throws RecognitionException {
		VarDecl_stack.push(new VarDecl_scope());

		int annotationConstantRest_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 78) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:686:6: ( variableDeclarators )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:686:8: variableDeclarators
			{
			pushFollow(FOLLOW_variableDeclarators_in_annotationConstantRest2702);
			variableDeclarators();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 78, annotationConstantRest_StartIndex); }

			VarDecl_stack.pop();

		}
	}
	// $ANTLR end "annotationConstantRest"



	// $ANTLR start "defaultValue"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:689:1: defaultValue : 'default' elementValue ;
	public final void defaultValue() throws RecognitionException {
		int defaultValue_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 79) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:690:6: ( 'default' elementValue )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:690:8: 'default' elementValue
			{
			match(input,74,FOLLOW_74_in_defaultValue2721); if (state.failed) return;
			pushFollow(FOLLOW_elementValue_in_defaultValue2723);
			elementValue();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 79, defaultValue_StartIndex); }

		}
	}
	// $ANTLR end "defaultValue"



	// $ANTLR start "block"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:695:1: block : '{' ( blockStatement )* '}' ;
	public final void block() throws RecognitionException {
		int block_StartIndex = input.index();


		            increaseLevel();
		        
		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 80) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:706:5: ( '{' ( blockStatement )* '}' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:706:7: '{' ( blockStatement )* '}'
			{
			match(input,121,FOLLOW_121_in_block2769); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:706:11: ( blockStatement )*
			loop104:
			while (true) {
				int alt104=2;
				int LA104_0 = input.LA(1);
				if ( ((LA104_0 >= CharacterLiteral && LA104_0 <= DecimalLiteral)||LA104_0==ENUM||LA104_0==FloatingPointLiteral||(LA104_0 >= HexLiteral && LA104_0 <= Identifier)||(LA104_0 >= OctalLiteral && LA104_0 <= StringLiteral)||LA104_0==29||LA104_0==36||(LA104_0 >= 40 && LA104_0 <= 41)||(LA104_0 >= 44 && LA104_0 <= 45)||(LA104_0 >= 52 && LA104_0 <= 53)||LA104_0==58||(LA104_0 >= 63 && LA104_0 <= 67)||(LA104_0 >= 70 && LA104_0 <= 73)||(LA104_0 >= 75 && LA104_0 <= 77)||(LA104_0 >= 79 && LA104_0 <= 80)||(LA104_0 >= 82 && LA104_0 <= 83)||(LA104_0 >= 85 && LA104_0 <= 87)||LA104_0==90||(LA104_0 >= 92 && LA104_0 <= 98)||(LA104_0 >= 100 && LA104_0 <= 112)||(LA104_0 >= 114 && LA104_0 <= 121)||LA104_0==126) ) {
					alt104=1;
				}

				switch (alt104) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:706:11: blockStatement
					{
					pushFollow(FOLLOW_blockStatement_in_block2771);
					blockStatement();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop104;
				}
			}

			match(input,125,FOLLOW_125_in_block2774); if (state.failed) return;
			}

			if ( state.backtracking==0 ) {
			            if( localVariableLevel <= 1 ) {
			                // this is the top level block, so set the top level declarations
			                rootBlockDescr.setInScopeLocalVars( getLocalDeclarations() );
			            }
			            decreaseLevel();
			        }
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 80, block_StartIndex); }

		}
	}
	// $ANTLR end "block"



	// $ANTLR start "blockStatement"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:709:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );
	public final void blockStatement() throws RecognitionException {
		int blockStatement_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 81) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:710:5: ( localVariableDeclaration | classOrInterfaceDeclaration | statement )
			int alt105=3;
			alt105 = dfa105.predict(input);
			switch (alt105) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:710:7: localVariableDeclaration
					{
					pushFollow(FOLLOW_localVariableDeclaration_in_blockStatement2791);
					localVariableDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:711:7: classOrInterfaceDeclaration
					{
					pushFollow(FOLLOW_classOrInterfaceDeclaration_in_blockStatement2799);
					classOrInterfaceDeclaration();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:712:11: statement
					{
					pushFollow(FOLLOW_statement_in_blockStatement2811);
					statement();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 81, blockStatement_StartIndex); }

		}
	}
	// $ANTLR end "blockStatement"



	// $ANTLR start "localVariableDeclaration"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:715:1: localVariableDeclaration : ( variableModifier )* type variableDeclarators ';' ;
	public final void localVariableDeclaration() throws RecognitionException {
		VarDecl_stack.push(new VarDecl_scope());

		int localVariableDeclaration_StartIndex = input.index();

		ParserRuleReturnScope variableModifier1 =null;
		ParserRuleReturnScope type2 =null;


		            VarDecl_stack.peek().descr = new JavaLocalDeclarationDescr();
		        
		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 82) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:723:5: ( ( variableModifier )* type variableDeclarators ';' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:724:5: ( variableModifier )* type variableDeclarators ';'
			{
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:724:5: ( variableModifier )*
			loop106:
			while (true) {
				int alt106=2;
				int LA106_0 = input.LA(1);
				if ( (LA106_0==58||LA106_0==83) ) {
					alt106=1;
				}

				switch (alt106) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:724:7: variableModifier
					{
					pushFollow(FOLLOW_variableModifier_in_localVariableDeclaration2865);
					variableModifier1=variableModifier();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {
					            VarDecl_stack.peek().descr.updateStart( ((CommonToken)(variableModifier1!=null?(variableModifier1.start):null)).getStartIndex() - 1 );
					            VarDecl_stack.peek().descr.addModifier( (variableModifier1!=null?input.toString(variableModifier1.start,variableModifier1.stop):null) );
					        }
					}
					break;

				default :
					break loop106;
				}
			}

			pushFollow(FOLLOW_type_in_localVariableDeclaration2888);
			type2=type();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) {
			            VarDecl_stack.peek().descr.updateStart( ((CommonToken)(type2!=null?(type2.start):null)).getStartIndex() - 1 );
			            VarDecl_stack.peek().descr.setType( (type2!=null?input.toString(type2.start,type2.stop):null) );
			            VarDecl_stack.peek().descr.setEnd( ((CommonToken)(type2!=null?(type2.stop):null)).getStopIndex() );
			        }
			pushFollow(FOLLOW_variableDeclarators_in_localVariableDeclaration2904);
			variableDeclarators();
			state._fsp--;
			if (state.failed) return;
			match(input,52,FOLLOW_52_in_localVariableDeclaration2906); if (state.failed) return;
			}

			if ( state.backtracking==0 ) {
			            addLocalDeclaration( VarDecl_stack.peek().descr );
			        }
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 82, localVariableDeclaration_StartIndex); }

			VarDecl_stack.pop();

		}
	}
	// $ANTLR end "localVariableDeclaration"


	public static class statement_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "statement"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:739:1: statement : ( block | 'assert' expression ( ':' expression )? ';' | ifStatement | forStatement | whileStatement | 'do' statement 'while' parExpression ';' | tryStatement | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | throwStatement | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | modifyStatement | updateStatement | retractStatement | deleteStatement | insertStatement | ';' | statementExpression ';' | Identifier ':' statement );
	public final JavaParser.statement_return statement() throws RecognitionException {
		JavaParser.statement_return retval = new JavaParser.statement_return();
		retval.start = input.LT(1);
		int statement_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 83) ) { return retval; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:740:5: ( block | 'assert' expression ( ':' expression )? ';' | ifStatement | forStatement | whileStatement | 'do' statement 'while' parExpression ';' | tryStatement | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | throwStatement | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | modifyStatement | updateStatement | retractStatement | deleteStatement | insertStatement | ';' | statementExpression ';' | Identifier ':' statement )
			int alt111=21;
			alt111 = dfa111.predict(input);
			switch (alt111) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:740:7: block
					{
					pushFollow(FOLLOW_block_in_statement2923);
					block();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:741:7: 'assert' expression ( ':' expression )? ';'
					{
					match(input,64,FOLLOW_64_in_statement2931); if (state.failed) return retval;
					pushFollow(FOLLOW_expression_in_statement2933);
					expression();
					state._fsp--;
					if (state.failed) return retval;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:741:27: ( ':' expression )?
					int alt107=2;
					int LA107_0 = input.LA(1);
					if ( (LA107_0==51) ) {
						alt107=1;
					}
					switch (alt107) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:741:28: ':' expression
							{
							match(input,51,FOLLOW_51_in_statement2936); if (state.failed) return retval;
							pushFollow(FOLLOW_expression_in_statement2938);
							expression();
							state._fsp--;
							if (state.failed) return retval;
							}
							break;

					}

					match(input,52,FOLLOW_52_in_statement2942); if (state.failed) return retval;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:742:7: ifStatement
					{
					pushFollow(FOLLOW_ifStatement_in_statement2950);
					ifStatement();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:743:7: forStatement
					{
					pushFollow(FOLLOW_forStatement_in_statement2958);
					forStatement();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:744:7: whileStatement
					{
					pushFollow(FOLLOW_whileStatement_in_statement2967);
					whileStatement();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 6 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:745:7: 'do' statement 'while' parExpression ';'
					{
					match(input,76,FOLLOW_76_in_statement2975); if (state.failed) return retval;
					pushFollow(FOLLOW_statement_in_statement2977);
					statement();
					state._fsp--;
					if (state.failed) return retval;
					match(input,120,FOLLOW_120_in_statement2979); if (state.failed) return retval;
					pushFollow(FOLLOW_parExpression_in_statement2981);
					parExpression();
					state._fsp--;
					if (state.failed) return retval;
					match(input,52,FOLLOW_52_in_statement2983); if (state.failed) return retval;
					}
					break;
				case 7 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:747:7: tryStatement
					{
					pushFollow(FOLLOW_tryStatement_in_statement2996);
					tryStatement();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 8 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:749:7: 'switch' parExpression '{' switchBlockStatementGroups '}'
					{
					match(input,109,FOLLOW_109_in_statement3011); if (state.failed) return retval;
					pushFollow(FOLLOW_parExpression_in_statement3013);
					parExpression();
					state._fsp--;
					if (state.failed) return retval;
					match(input,121,FOLLOW_121_in_statement3015); if (state.failed) return retval;
					pushFollow(FOLLOW_switchBlockStatementGroups_in_statement3017);
					switchBlockStatementGroups();
					state._fsp--;
					if (state.failed) return retval;
					match(input,125,FOLLOW_125_in_statement3019); if (state.failed) return retval;
					}
					break;
				case 9 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:750:7: 'synchronized' parExpression block
					{
					match(input,110,FOLLOW_110_in_statement3027); if (state.failed) return retval;
					pushFollow(FOLLOW_parExpression_in_statement3029);
					parExpression();
					state._fsp--;
					if (state.failed) return retval;
					pushFollow(FOLLOW_block_in_statement3031);
					block();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 10 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:751:7: 'return' ( expression )? ';'
					{
					match(input,104,FOLLOW_104_in_statement3039); if (state.failed) return retval;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:751:16: ( expression )?
					int alt108=2;
					int LA108_0 = input.LA(1);
					if ( ((LA108_0 >= CharacterLiteral && LA108_0 <= DecimalLiteral)||LA108_0==FloatingPointLiteral||(LA108_0 >= HexLiteral && LA108_0 <= Identifier)||(LA108_0 >= OctalLiteral && LA108_0 <= StringLiteral)||LA108_0==29||LA108_0==36||(LA108_0 >= 40 && LA108_0 <= 41)||(LA108_0 >= 44 && LA108_0 <= 45)||LA108_0==53||LA108_0==65||LA108_0==67||(LA108_0 >= 70 && LA108_0 <= 71)||LA108_0==77||(LA108_0 >= 79 && LA108_0 <= 80)||LA108_0==82||LA108_0==85||LA108_0==92||LA108_0==94||(LA108_0 >= 97 && LA108_0 <= 98)||LA108_0==105||LA108_0==108||LA108_0==111||LA108_0==115||LA108_0==118||LA108_0==126) ) {
						alt108=1;
					}
					switch (alt108) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:751:16: expression
							{
							pushFollow(FOLLOW_expression_in_statement3041);
							expression();
							state._fsp--;
							if (state.failed) return retval;
							}
							break;

					}

					match(input,52,FOLLOW_52_in_statement3044); if (state.failed) return retval;
					}
					break;
				case 11 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:752:8: throwStatement
					{
					pushFollow(FOLLOW_throwStatement_in_statement3053);
					throwStatement();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 12 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:753:7: 'break' ( Identifier )? ';'
					{
					match(input,66,FOLLOW_66_in_statement3061); if (state.failed) return retval;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:753:15: ( Identifier )?
					int alt109=2;
					int LA109_0 = input.LA(1);
					if ( (LA109_0==Identifier) ) {
						alt109=1;
					}
					switch (alt109) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:753:15: Identifier
							{
							match(input,Identifier,FOLLOW_Identifier_in_statement3063); if (state.failed) return retval;
							}
							break;

					}

					match(input,52,FOLLOW_52_in_statement3066); if (state.failed) return retval;
					}
					break;
				case 13 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:754:7: 'continue' ( Identifier )? ';'
					{
					match(input,73,FOLLOW_73_in_statement3074); if (state.failed) return retval;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:754:18: ( Identifier )?
					int alt110=2;
					int LA110_0 = input.LA(1);
					if ( (LA110_0==Identifier) ) {
						alt110=1;
					}
					switch (alt110) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:754:18: Identifier
							{
							match(input,Identifier,FOLLOW_Identifier_in_statement3076); if (state.failed) return retval;
							}
							break;

					}

					match(input,52,FOLLOW_52_in_statement3079); if (state.failed) return retval;
					}
					break;
				case 14 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:756:7: modifyStatement
					{
					pushFollow(FOLLOW_modifyStatement_in_statement3092);
					modifyStatement();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 15 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:756:25: updateStatement
					{
					pushFollow(FOLLOW_updateStatement_in_statement3096);
					updateStatement();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 16 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:756:43: retractStatement
					{
					pushFollow(FOLLOW_retractStatement_in_statement3100);
					retractStatement();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 17 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:756:62: deleteStatement
					{
					pushFollow(FOLLOW_deleteStatement_in_statement3104);
					deleteStatement();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 18 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:756:80: insertStatement
					{
					pushFollow(FOLLOW_insertStatement_in_statement3108);
					insertStatement();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 19 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:757:7: ';'
					{
					match(input,52,FOLLOW_52_in_statement3116); if (state.failed) return retval;
					}
					break;
				case 20 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:758:7: statementExpression ';'
					{
					pushFollow(FOLLOW_statementExpression_in_statement3124);
					statementExpression();
					state._fsp--;
					if (state.failed) return retval;
					match(input,52,FOLLOW_52_in_statement3126); if (state.failed) return retval;
					}
					break;
				case 21 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:759:7: Identifier ':' statement
					{
					match(input,Identifier,FOLLOW_Identifier_in_statement3134); if (state.failed) return retval;
					match(input,51,FOLLOW_51_in_statement3136); if (state.failed) return retval;
					pushFollow(FOLLOW_statement_in_statement3138);
					statement();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 83, statement_StartIndex); }

		}
		return retval;
	}
	// $ANTLR end "statement"



	// $ANTLR start "throwStatement"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:763:1: throwStatement : s= 'throw' expression c= ';' ;
	public final void throwStatement() throws RecognitionException {
		int throwStatement_StartIndex = input.index();

		Token s=null;
		Token c=null;
		ParserRuleReturnScope expression3 =null;


		        JavaThrowBlockDescr d = null;
		    
		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 84) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:767:5: (s= 'throw' expression c= ';' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:767:7: s= 'throw' expression c= ';'
			{
			s=(Token)match(input,112,FOLLOW_112_in_throwStatement3171); if (state.failed) return;
			pushFollow(FOLLOW_expression_in_throwStatement3177);
			expression3=expression();
			state._fsp--;
			if (state.failed) return;
			c=(Token)match(input,52,FOLLOW_52_in_throwStatement3187); if (state.failed) return;
			if ( state.backtracking==0 ) {
			        d = new JavaThrowBlockDescr( );
			        d.setStart( ((CommonToken)s).getStartIndex() );
			        d.setTextStart( ((CommonToken)(expression3!=null?(expression3.start):null)).getStartIndex() );        
			        this.addBlockDescr( d );
			        d.setEnd( ((CommonToken)c).getStopIndex() ); 
			        }
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 84, throwStatement_StartIndex); }

		}
	}
	// $ANTLR end "throwStatement"



	// $ANTLR start "ifStatement"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:779:1: ifStatement : s= 'if' parExpression x= statement (y= 'else' ( 'if' parExpression )? z= statement )* ;
	public final void ifStatement() throws RecognitionException {
		int ifStatement_StartIndex = input.index();

		Token s=null;
		Token y=null;
		ParserRuleReturnScope x =null;
		ParserRuleReturnScope z =null;


		         JavaIfBlockDescr id = null;
		         JavaElseBlockDescr ed = null;         
		    
		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 85) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:784:5: (s= 'if' parExpression x= statement (y= 'else' ( 'if' parExpression )? z= statement )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:786:5: s= 'if' parExpression x= statement (y= 'else' ( 'if' parExpression )? z= statement )*
			{
			s=(Token)match(input,87,FOLLOW_87_in_ifStatement3243); if (state.failed) return;
			pushFollow(FOLLOW_parExpression_in_ifStatement3245);
			parExpression();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) {
			        increaseLevel();
			        id = new JavaIfBlockDescr();
			        id.setStart( ((CommonToken)s).getStartIndex() );  pushContainerBlockDescr(id, true); 
			    }
			pushFollow(FOLLOW_statement_in_ifStatement3263);
			x=statement();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) {
			        decreaseLevel();
			        id.setTextStart(((CommonToken)(x!=null?(x.start):null)).getStartIndex() );
			        id.setEnd(((CommonToken)(x!=null?(x.stop):null)).getStopIndex() ); popContainerBlockDescr(); 
			    }
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:799:5: (y= 'else' ( 'if' parExpression )? z= statement )*
			loop113:
			while (true) {
				int alt113=2;
				alt113 = dfa113.predict(input);
				switch (alt113) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:800:6: y= 'else' ( 'if' parExpression )? z= statement
					{
					y=(Token)match(input,78,FOLLOW_78_in_ifStatement3290); if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:800:16: ( 'if' parExpression )?
					int alt112=2;
					int LA112_0 = input.LA(1);
					if ( (LA112_0==87) ) {
						int LA112_1 = input.LA(2);
						if ( (LA112_1==36) ) {
							int LA112_43 = input.LA(3);
							if ( (synpred171_Java()) ) {
								alt112=1;
							}
						}
					}
					switch (alt112) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:800:17: 'if' parExpression
							{
							match(input,87,FOLLOW_87_in_ifStatement3294); if (state.failed) return;
							pushFollow(FOLLOW_parExpression_in_ifStatement3296);
							parExpression();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					if ( state.backtracking==0 ) {
					        increaseLevel();
					        ed = new JavaElseBlockDescr();
					        ed.setStart( ((CommonToken)y).getStartIndex() );  pushContainerBlockDescr(ed, true); 
					    }
					pushFollow(FOLLOW_statement_in_ifStatement3327);
					z=statement();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {
					        decreaseLevel();
					        ed.setTextStart(((CommonToken)(z!=null?(z.start):null)).getStartIndex() );
					        ed.setEnd(((CommonToken)(z!=null?(z.stop):null)).getStopIndex() ); popContainerBlockDescr();               
					    }
					}
					break;

				default :
					break loop113;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 85, ifStatement_StartIndex); }

		}
	}
	// $ANTLR end "ifStatement"



	// $ANTLR start "forStatement"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:814:1: forStatement options {k=3; } : x= 'for' y= '(' ( ( ( variableModifier )* type id= Identifier z= ':' expression ) | ( ( forInit )? z= ';' ( expression )? ';' ( forUpdate )? ) ) ')' bs= statement ;
	public final void forStatement() throws RecognitionException {
		VarDecl_stack.push(new VarDecl_scope());

		int forStatement_StartIndex = input.index();

		Token x=null;
		Token y=null;
		Token id=null;
		Token z=null;
		ParserRuleReturnScope bs =null;
		ParserRuleReturnScope variableModifier4 =null;
		ParserRuleReturnScope type5 =null;


		         JavaForBlockDescr fd = null;
		         increaseLevel();
		         VarDecl_stack.peek().descr = new JavaLocalDeclarationDescr();
		    
		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 86) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:826:5: (x= 'for' y= '(' ( ( ( variableModifier )* type id= Identifier z= ':' expression ) | ( ( forInit )? z= ';' ( expression )? ';' ( forUpdate )? ) ) ')' bs= statement )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:827:5: x= 'for' y= '(' ( ( ( variableModifier )* type id= Identifier z= ':' expression ) | ( ( forInit )? z= ';' ( expression )? ';' ( forUpdate )? ) ) ')' bs= statement
			{
			x=(Token)match(input,86,FOLLOW_86_in_forStatement3409); if (state.failed) return;
			y=(Token)match(input,36,FOLLOW_36_in_forStatement3413); if (state.failed) return;
			if ( state.backtracking==0 ) {   fd = new JavaForBlockDescr( ); 
			        fd.setStart( ((CommonToken)x).getStartIndex() ); pushContainerBlockDescr(fd, true);    
			        fd.setStartParen( ((CommonToken)y).getStartIndex() );            
			    }
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:832:5: ( ( ( variableModifier )* type id= Identifier z= ':' expression ) | ( ( forInit )? z= ';' ( expression )? ';' ( forUpdate )? ) )
			int alt118=2;
			alt118 = dfa118.predict(input);
			switch (alt118) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:833:9: ( ( variableModifier )* type id= Identifier z= ':' expression )
					{
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:833:9: ( ( variableModifier )* type id= Identifier z= ':' expression )
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:833:11: ( variableModifier )* type id= Identifier z= ':' expression
					{
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:833:11: ( variableModifier )*
					loop114:
					while (true) {
						int alt114=2;
						int LA114_0 = input.LA(1);
						if ( (LA114_0==58||LA114_0==83) ) {
							alt114=1;
						}

						switch (alt114) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:833:13: variableModifier
							{
							pushFollow(FOLLOW_variableModifier_in_forStatement3449);
							variableModifier4=variableModifier();
							state._fsp--;
							if (state.failed) return;
							if ( state.backtracking==0 ) {
							                VarDecl_stack.peek().descr.updateStart( ((CommonToken)(variableModifier4!=null?(variableModifier4.start):null)).getStartIndex() - 1 );
							                VarDecl_stack.peek().descr.addModifier( (variableModifier4!=null?input.toString(variableModifier4.start,variableModifier4.stop):null) );
							            }
							}
							break;

						default :
							break loop114;
						}
					}

					pushFollow(FOLLOW_type_in_forStatement3488);
					type5=type();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {
					            VarDecl_stack.peek().descr.updateStart( ((CommonToken)(type5!=null?(type5.start):null)).getStartIndex() - 1 );
					            VarDecl_stack.peek().descr.setType( (type5!=null?input.toString(type5.start,type5.stop):null) );
					            VarDecl_stack.peek().descr.setEnd( ((CommonToken)(type5!=null?(type5.stop):null)).getStopIndex() );
					          }
					id=(Token)match(input,Identifier,FOLLOW_Identifier_in_forStatement3514); if (state.failed) return;
					if ( state.backtracking==0 ) {
					            JavaLocalDeclarationDescr.IdentifierDescr ident = new JavaLocalDeclarationDescr.IdentifierDescr();
					            ident.setIdentifier( (id!=null?id.getText():null) );
					            ident.setStart( ((CommonToken)id).getStartIndex() - 1 );
					            ident.setEnd( ((CommonToken)id).getStopIndex() );
					            VarDecl_stack.peek().descr.addIdentifier( ident );
					          }
					z=(Token)match(input,51,FOLLOW_51_in_forStatement3541); if (state.failed) return;
					pushFollow(FOLLOW_expression_in_forStatement3543);
					expression();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {
					             fd.setInitEnd( ((CommonToken)z).getStartIndex() );        
					          }
					}

					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:858:9: ( ( forInit )? z= ';' ( expression )? ';' ( forUpdate )? )
					{
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:858:9: ( ( forInit )? z= ';' ( expression )? ';' ( forUpdate )? )
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:858:10: ( forInit )? z= ';' ( expression )? ';' ( forUpdate )?
					{
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:858:10: ( forInit )?
					int alt115=2;
					int LA115_0 = input.LA(1);
					if ( ((LA115_0 >= CharacterLiteral && LA115_0 <= DecimalLiteral)||LA115_0==FloatingPointLiteral||(LA115_0 >= HexLiteral && LA115_0 <= Identifier)||(LA115_0 >= OctalLiteral && LA115_0 <= StringLiteral)||LA115_0==29||LA115_0==36||(LA115_0 >= 40 && LA115_0 <= 41)||(LA115_0 >= 44 && LA115_0 <= 45)||LA115_0==53||LA115_0==58||LA115_0==65||LA115_0==67||(LA115_0 >= 70 && LA115_0 <= 71)||LA115_0==77||(LA115_0 >= 79 && LA115_0 <= 80)||(LA115_0 >= 82 && LA115_0 <= 83)||LA115_0==85||LA115_0==92||LA115_0==94||(LA115_0 >= 97 && LA115_0 <= 98)||LA115_0==105||LA115_0==108||LA115_0==111||LA115_0==115||LA115_0==118||LA115_0==126) ) {
						alt115=1;
					}
					switch (alt115) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:858:10: forInit
							{
							pushFollow(FOLLOW_forInit_in_forStatement3579);
							forInit();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					z=(Token)match(input,52,FOLLOW_52_in_forStatement3584); if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:858:25: ( expression )?
					int alt116=2;
					int LA116_0 = input.LA(1);
					if ( ((LA116_0 >= CharacterLiteral && LA116_0 <= DecimalLiteral)||LA116_0==FloatingPointLiteral||(LA116_0 >= HexLiteral && LA116_0 <= Identifier)||(LA116_0 >= OctalLiteral && LA116_0 <= StringLiteral)||LA116_0==29||LA116_0==36||(LA116_0 >= 40 && LA116_0 <= 41)||(LA116_0 >= 44 && LA116_0 <= 45)||LA116_0==53||LA116_0==65||LA116_0==67||(LA116_0 >= 70 && LA116_0 <= 71)||LA116_0==77||(LA116_0 >= 79 && LA116_0 <= 80)||LA116_0==82||LA116_0==85||LA116_0==92||LA116_0==94||(LA116_0 >= 97 && LA116_0 <= 98)||LA116_0==105||LA116_0==108||LA116_0==111||LA116_0==115||LA116_0==118||LA116_0==126) ) {
						alt116=1;
					}
					switch (alt116) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:858:25: expression
							{
							pushFollow(FOLLOW_expression_in_forStatement3586);
							expression();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					match(input,52,FOLLOW_52_in_forStatement3589); if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:858:41: ( forUpdate )?
					int alt117=2;
					int LA117_0 = input.LA(1);
					if ( ((LA117_0 >= CharacterLiteral && LA117_0 <= DecimalLiteral)||LA117_0==FloatingPointLiteral||(LA117_0 >= HexLiteral && LA117_0 <= Identifier)||(LA117_0 >= OctalLiteral && LA117_0 <= StringLiteral)||LA117_0==29||LA117_0==36||(LA117_0 >= 40 && LA117_0 <= 41)||(LA117_0 >= 44 && LA117_0 <= 45)||LA117_0==53||LA117_0==65||LA117_0==67||(LA117_0 >= 70 && LA117_0 <= 71)||LA117_0==77||(LA117_0 >= 79 && LA117_0 <= 80)||LA117_0==82||LA117_0==85||LA117_0==92||LA117_0==94||(LA117_0 >= 97 && LA117_0 <= 98)||LA117_0==105||LA117_0==108||LA117_0==111||LA117_0==115||LA117_0==118||LA117_0==126) ) {
						alt117=1;
					}
					switch (alt117) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:858:41: forUpdate
							{
							pushFollow(FOLLOW_forUpdate_in_forStatement3591);
							forUpdate();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					if ( state.backtracking==0 ) {
					             fd.setInitEnd( ((CommonToken)z).getStartIndex() );        
					          }
					}

					}
					break;

			}

			match(input,37,FOLLOW_37_in_forStatement3637); if (state.failed) return;
			pushFollow(FOLLOW_statement_in_forStatement3641);
			bs=statement();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) {                
			        fd.setTextStart(((CommonToken)(bs!=null?(bs.start):null)).getStartIndex() );
			        fd.setEnd(((CommonToken)(bs!=null?(bs.stop):null)).getStopIndex() ); popContainerBlockDescr();     
			    }
			}

			if ( state.backtracking==0 ) {
			         addLocalDeclaration( VarDecl_stack.peek().descr );
			         decreaseLevel();
			    }
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 86, forStatement_StartIndex); }

			VarDecl_stack.pop();

		}
	}
	// $ANTLR end "forStatement"



	// $ANTLR start "whileStatement"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:872:1: whileStatement : s= 'while' parExpression bs= statement ;
	public final void whileStatement() throws RecognitionException {
		int whileStatement_StartIndex = input.index();

		Token s=null;
		ParserRuleReturnScope bs =null;


		         JavaWhileBlockDescr wd = null;         
		    
		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 87) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:876:5: (s= 'while' parExpression bs= statement )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:878:5: s= 'while' parExpression bs= statement
			{
			s=(Token)match(input,120,FOLLOW_120_in_whileStatement3700); if (state.failed) return;
			pushFollow(FOLLOW_parExpression_in_whileStatement3702);
			parExpression();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) {   wd = new JavaWhileBlockDescr( ); wd.setStart( ((CommonToken)s).getStartIndex() ); pushContainerBlockDescr(wd, true);    
			    }
			pushFollow(FOLLOW_statement_in_whileStatement3719);
			bs=statement();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) {                
			        wd.setTextStart(((CommonToken)(bs!=null?(bs.start):null)).getStartIndex() );
			        wd.setEnd(((CommonToken)(bs!=null?(bs.stop):null)).getStopIndex() ); popContainerBlockDescr();     
			    }
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 87, whileStatement_StartIndex); }

		}
	}
	// $ANTLR end "whileStatement"



	// $ANTLR start "tryStatement"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:888:1: tryStatement : s= 'try' bs= '{' ( blockStatement )* c= '}' (s= 'catch' '(' formalParameter ')' bs= '{' ( blockStatement )* c= '}' )* (s= 'finally' bs= '{' ( blockStatement )* c= '}' )? ;
	public final void tryStatement() throws RecognitionException {
		int tryStatement_StartIndex = input.index();

		Token s=null;
		Token bs=null;
		Token c=null;
		ParserRuleReturnScope formalParameter6 =null;


		         JavaTryBlockDescr td = null;
		         JavaCatchBlockDescr cd = null;
		         JavaFinalBlockDescr fd = null;
		         
		    
		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 88) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:895:5: (s= 'try' bs= '{' ( blockStatement )* c= '}' (s= 'catch' '(' formalParameter ')' bs= '{' ( blockStatement )* c= '}' )* (s= 'finally' bs= '{' ( blockStatement )* c= '}' )? )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:896:5: s= 'try' bs= '{' ( blockStatement )* c= '}' (s= 'catch' '(' formalParameter ')' bs= '{' ( blockStatement )* c= '}' )* (s= 'finally' bs= '{' ( blockStatement )* c= '}' )?
			{
			s=(Token)match(input,116,FOLLOW_116_in_tryStatement3772); if (state.failed) return;
			if ( state.backtracking==0 ) {   increaseLevel();
			        td = new JavaTryBlockDescr( ); td.setStart( ((CommonToken)s).getStartIndex() ); pushContainerBlockDescr(td, true);    
			    }
			bs=(Token)match(input,121,FOLLOW_121_in_tryStatement3783); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:899:14: ( blockStatement )*
			loop119:
			while (true) {
				int alt119=2;
				int LA119_0 = input.LA(1);
				if ( ((LA119_0 >= CharacterLiteral && LA119_0 <= DecimalLiteral)||LA119_0==ENUM||LA119_0==FloatingPointLiteral||(LA119_0 >= HexLiteral && LA119_0 <= Identifier)||(LA119_0 >= OctalLiteral && LA119_0 <= StringLiteral)||LA119_0==29||LA119_0==36||(LA119_0 >= 40 && LA119_0 <= 41)||(LA119_0 >= 44 && LA119_0 <= 45)||(LA119_0 >= 52 && LA119_0 <= 53)||LA119_0==58||(LA119_0 >= 63 && LA119_0 <= 67)||(LA119_0 >= 70 && LA119_0 <= 73)||(LA119_0 >= 75 && LA119_0 <= 77)||(LA119_0 >= 79 && LA119_0 <= 80)||(LA119_0 >= 82 && LA119_0 <= 83)||(LA119_0 >= 85 && LA119_0 <= 87)||LA119_0==90||(LA119_0 >= 92 && LA119_0 <= 98)||(LA119_0 >= 100 && LA119_0 <= 112)||(LA119_0 >= 114 && LA119_0 <= 121)||LA119_0==126) ) {
					alt119=1;
				}

				switch (alt119) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:899:14: blockStatement
					{
					pushFollow(FOLLOW_blockStatement_in_tryStatement3785);
					blockStatement();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop119;
				}
			}

			if ( state.backtracking==0 ) {
			                
			        td.setTextStart( ((CommonToken)bs).getStartIndex() );        

			    }
			c=(Token)match(input,125,FOLLOW_125_in_tryStatement3796); if (state.failed) return;
			if ( state.backtracking==0 ) {td.setEnd( ((CommonToken)c).getStopIndex() ); decreaseLevel(); popContainerBlockDescr();    }
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:907:5: (s= 'catch' '(' formalParameter ')' bs= '{' ( blockStatement )* c= '}' )*
			loop121:
			while (true) {
				int alt121=2;
				alt121 = dfa121.predict(input);
				switch (alt121) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:907:6: s= 'catch' '(' formalParameter ')' bs= '{' ( blockStatement )* c= '}'
					{
					s=(Token)match(input,69,FOLLOW_69_in_tryStatement3814); if (state.failed) return;
					match(input,36,FOLLOW_36_in_tryStatement3816); if (state.failed) return;
					pushFollow(FOLLOW_formalParameter_in_tryStatement3818);
					formalParameter6=formalParameter();
					state._fsp--;
					if (state.failed) return;
					match(input,37,FOLLOW_37_in_tryStatement3820); if (state.failed) return;
					if ( state.backtracking==0 ) {  increaseLevel();
					        cd = new JavaCatchBlockDescr( (formalParameter6!=null?input.toString(formalParameter6.start,formalParameter6.stop):null) );
					        cd.setClauseStart( ((CommonToken)(formalParameter6!=null?(formalParameter6.start):null)).getStartIndex() ); 
					        cd.setStart( ((CommonToken)s).getStartIndex() );  pushContainerBlockDescr(cd, false);
					     }
					bs=(Token)match(input,121,FOLLOW_121_in_tryStatement3832); if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:912:15: ( blockStatement )*
					loop120:
					while (true) {
						int alt120=2;
						int LA120_0 = input.LA(1);
						if ( ((LA120_0 >= CharacterLiteral && LA120_0 <= DecimalLiteral)||LA120_0==ENUM||LA120_0==FloatingPointLiteral||(LA120_0 >= HexLiteral && LA120_0 <= Identifier)||(LA120_0 >= OctalLiteral && LA120_0 <= StringLiteral)||LA120_0==29||LA120_0==36||(LA120_0 >= 40 && LA120_0 <= 41)||(LA120_0 >= 44 && LA120_0 <= 45)||(LA120_0 >= 52 && LA120_0 <= 53)||LA120_0==58||(LA120_0 >= 63 && LA120_0 <= 67)||(LA120_0 >= 70 && LA120_0 <= 73)||(LA120_0 >= 75 && LA120_0 <= 77)||(LA120_0 >= 79 && LA120_0 <= 80)||(LA120_0 >= 82 && LA120_0 <= 83)||(LA120_0 >= 85 && LA120_0 <= 87)||LA120_0==90||(LA120_0 >= 92 && LA120_0 <= 98)||(LA120_0 >= 100 && LA120_0 <= 112)||(LA120_0 >= 114 && LA120_0 <= 121)||LA120_0==126) ) {
							alt120=1;
						}

						switch (alt120) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:912:15: blockStatement
							{
							pushFollow(FOLLOW_blockStatement_in_tryStatement3834);
							blockStatement();
							state._fsp--;
							if (state.failed) return;
							}
							break;

						default :
							break loop120;
						}
					}

					if ( state.backtracking==0 ) { 
					        cd.setTextStart( ((CommonToken)bs).getStartIndex() );
					        td.addCatch( cd );        
					     }
					c=(Token)match(input,125,FOLLOW_125_in_tryStatement3847); if (state.failed) return;
					if ( state.backtracking==0 ) {cd.setEnd( ((CommonToken)c).getStopIndex() ); decreaseLevel(); popContainerBlockDescr(); }
					}
					break;

				default :
					break loop121;
				}
			}

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:920:6: (s= 'finally' bs= '{' ( blockStatement )* c= '}' )?
			int alt123=2;
			alt123 = dfa123.predict(input);
			switch (alt123) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:920:7: s= 'finally' bs= '{' ( blockStatement )* c= '}'
					{
					s=(Token)match(input,84,FOLLOW_84_in_tryStatement3881); if (state.failed) return;
					if ( state.backtracking==0 ) {  increaseLevel();
					        fd = new JavaFinalBlockDescr( ); fd.setStart( ((CommonToken)s).getStartIndex() ); pushContainerBlockDescr(fd, false);
					     }
					bs=(Token)match(input,121,FOLLOW_121_in_tryStatement3893); if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:923:15: ( blockStatement )*
					loop122:
					while (true) {
						int alt122=2;
						int LA122_0 = input.LA(1);
						if ( ((LA122_0 >= CharacterLiteral && LA122_0 <= DecimalLiteral)||LA122_0==ENUM||LA122_0==FloatingPointLiteral||(LA122_0 >= HexLiteral && LA122_0 <= Identifier)||(LA122_0 >= OctalLiteral && LA122_0 <= StringLiteral)||LA122_0==29||LA122_0==36||(LA122_0 >= 40 && LA122_0 <= 41)||(LA122_0 >= 44 && LA122_0 <= 45)||(LA122_0 >= 52 && LA122_0 <= 53)||LA122_0==58||(LA122_0 >= 63 && LA122_0 <= 67)||(LA122_0 >= 70 && LA122_0 <= 73)||(LA122_0 >= 75 && LA122_0 <= 77)||(LA122_0 >= 79 && LA122_0 <= 80)||(LA122_0 >= 82 && LA122_0 <= 83)||(LA122_0 >= 85 && LA122_0 <= 87)||LA122_0==90||(LA122_0 >= 92 && LA122_0 <= 98)||(LA122_0 >= 100 && LA122_0 <= 112)||(LA122_0 >= 114 && LA122_0 <= 121)||LA122_0==126) ) {
							alt122=1;
						}

						switch (alt122) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:923:15: blockStatement
							{
							pushFollow(FOLLOW_blockStatement_in_tryStatement3895);
							blockStatement();
							state._fsp--;
							if (state.failed) return;
							}
							break;

						default :
							break loop122;
						}
					}

					if ( state.backtracking==0 ) {
					        fd.setTextStart( ((CommonToken)bs).getStartIndex() );        
					        td.setFinally( fd );         
					      }
					c=(Token)match(input,125,FOLLOW_125_in_tryStatement3909); if (state.failed) return;
					if ( state.backtracking==0 ) {fd.setEnd( ((CommonToken)c).getStopIndex() ); decreaseLevel(); popContainerBlockDescr(); }
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 88, tryStatement_StartIndex); }

		}
	}
	// $ANTLR end "tryStatement"



	// $ANTLR start "modifyStatement"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:930:1: modifyStatement : s= 'modify' parExpression '{' (e= expression ( ',' e= expression )* )? c= '}' ;
	public final void modifyStatement() throws RecognitionException {
		int modifyStatement_StartIndex = input.index();

		Token s=null;
		Token c=null;
		ParserRuleReturnScope e =null;
		ParserRuleReturnScope parExpression7 =null;


		        JavaModifyBlockDescr d = null;
		    
		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 89) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:934:5: (s= 'modify' parExpression '{' (e= expression ( ',' e= expression )* )? c= '}' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:934:7: s= 'modify' parExpression '{' (e= expression ( ',' e= expression )* )? c= '}'
			{
			s=(Token)match(input,95,FOLLOW_95_in_modifyStatement3951); if (state.failed) return;
			pushFollow(FOLLOW_parExpression_in_modifyStatement3953);
			parExpression7=parExpression();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) {
			        d = new JavaModifyBlockDescr( (parExpression7!=null?input.toString(parExpression7.start,parExpression7.stop):null) );
			        d.setStart( ((CommonToken)s).getStartIndex() );
			        d.setInScopeLocalVars( getLocalDeclarations() );
			        this.addBlockDescr( d );

			    }
			match(input,121,FOLLOW_121_in_modifyStatement3965); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:942:9: (e= expression ( ',' e= expression )* )?
			int alt125=2;
			int LA125_0 = input.LA(1);
			if ( ((LA125_0 >= CharacterLiteral && LA125_0 <= DecimalLiteral)||LA125_0==FloatingPointLiteral||(LA125_0 >= HexLiteral && LA125_0 <= Identifier)||(LA125_0 >= OctalLiteral && LA125_0 <= StringLiteral)||LA125_0==29||LA125_0==36||(LA125_0 >= 40 && LA125_0 <= 41)||(LA125_0 >= 44 && LA125_0 <= 45)||LA125_0==53||LA125_0==65||LA125_0==67||(LA125_0 >= 70 && LA125_0 <= 71)||LA125_0==77||(LA125_0 >= 79 && LA125_0 <= 80)||LA125_0==82||LA125_0==85||LA125_0==92||LA125_0==94||(LA125_0 >= 97 && LA125_0 <= 98)||LA125_0==105||LA125_0==108||LA125_0==111||LA125_0==115||LA125_0==118||LA125_0==126) ) {
				alt125=1;
			}
			switch (alt125) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:942:11: e= expression ( ',' e= expression )*
					{
					pushFollow(FOLLOW_expression_in_modifyStatement3973);
					e=expression();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) { d.getExpressions().add( (e!=null?input.toString(e.start,e.stop):null) ); }
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:943:12: ( ',' e= expression )*
					loop124:
					while (true) {
						int alt124=2;
						int LA124_0 = input.LA(1);
						if ( (LA124_0==43) ) {
							alt124=1;
						}

						switch (alt124) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:943:13: ',' e= expression
							{
							match(input,43,FOLLOW_43_in_modifyStatement3989); if (state.failed) return;
							pushFollow(FOLLOW_expression_in_modifyStatement3993);
							e=expression();
							state._fsp--;
							if (state.failed) return;
							if ( state.backtracking==0 ) { d.getExpressions().add( (e!=null?input.toString(e.start,e.stop):null) ); }
							}
							break;

						default :
							break loop124;
						}
					}

					}
					break;

			}

			c=(Token)match(input,125,FOLLOW_125_in_modifyStatement4017); if (state.failed) return;
			if ( state.backtracking==0 ) {
			            d.setEnd( ((CommonToken)c).getStopIndex() ); 
			        }
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 89, modifyStatement_StartIndex); }

		}
	}
	// $ANTLR end "modifyStatement"



	// $ANTLR start "updateStatement"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:951:1: updateStatement : s= 'update' '(' expression c= ')' ;
	public final void updateStatement() throws RecognitionException {
		int updateStatement_StartIndex = input.index();

		Token s=null;
		Token c=null;
		ParserRuleReturnScope expression8 =null;

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 90) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:952:5: (s= 'update' '(' expression c= ')' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:952:7: s= 'update' '(' expression c= ')'
			{
			s=(Token)match(input,117,FOLLOW_117_in_updateStatement4046); if (state.failed) return;
			match(input,36,FOLLOW_36_in_updateStatement4048); if (state.failed) return;
			pushFollow(FOLLOW_expression_in_updateStatement4054);
			expression8=expression();
			state._fsp--;
			if (state.failed) return;
			c=(Token)match(input,37,FOLLOW_37_in_updateStatement4064); if (state.failed) return;
			if ( state.backtracking==0 ) {
			        JavaStatementBlockDescr d = new JavaStatementBlockDescr( (expression8!=null?input.toString(expression8.start,expression8.stop):null), JavaBlockDescr.BlockType.UPDATE );
			        d.setStart( ((CommonToken)s).getStartIndex() );
			        this.addBlockDescr( d );
			        d.setEnd( ((CommonToken)c).getStopIndex() ); 
			        }
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 90, updateStatement_StartIndex); }

		}
	}
	// $ANTLR end "updateStatement"



	// $ANTLR start "retractStatement"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:963:1: retractStatement : s= 'retract' '(' expression c= ')' ;
	public final void retractStatement() throws RecognitionException {
		int retractStatement_StartIndex = input.index();

		Token s=null;
		Token c=null;
		ParserRuleReturnScope expression9 =null;

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 91) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:964:5: (s= 'retract' '(' expression c= ')' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:964:7: s= 'retract' '(' expression c= ')'
			{
			s=(Token)match(input,103,FOLLOW_103_in_retractStatement4097); if (state.failed) return;
			match(input,36,FOLLOW_36_in_retractStatement4099); if (state.failed) return;
			pushFollow(FOLLOW_expression_in_retractStatement4105);
			expression9=expression();
			state._fsp--;
			if (state.failed) return;
			c=(Token)match(input,37,FOLLOW_37_in_retractStatement4115); if (state.failed) return;
			if ( state.backtracking==0 ) {	
			        JavaStatementBlockDescr d = new JavaStatementBlockDescr( (expression9!=null?input.toString(expression9.start,expression9.stop):null), JavaBlockDescr.BlockType.DELETE );
			        d.setStart( ((CommonToken)s).getStartIndex() );
			        this.addBlockDescr( d );
			        d.setEnd( ((CommonToken)c).getStopIndex() );
			    }
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 91, retractStatement_StartIndex); }

		}
	}
	// $ANTLR end "retractStatement"



	// $ANTLR start "deleteStatement"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:975:1: deleteStatement : s= 'delete' '(' expression c= ')' ;
	public final void deleteStatement() throws RecognitionException {
		int deleteStatement_StartIndex = input.index();

		Token s=null;
		Token c=null;
		ParserRuleReturnScope expression10 =null;

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 92) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:976:5: (s= 'delete' '(' expression c= ')' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:976:7: s= 'delete' '(' expression c= ')'
			{
			s=(Token)match(input,75,FOLLOW_75_in_deleteStatement4144); if (state.failed) return;
			match(input,36,FOLLOW_36_in_deleteStatement4146); if (state.failed) return;
			pushFollow(FOLLOW_expression_in_deleteStatement4152);
			expression10=expression();
			state._fsp--;
			if (state.failed) return;
			c=(Token)match(input,37,FOLLOW_37_in_deleteStatement4162); if (state.failed) return;
			if ( state.backtracking==0 ) {
			        JavaStatementBlockDescr d = new JavaStatementBlockDescr( (expression10!=null?input.toString(expression10.start,expression10.stop):null), JavaBlockDescr.BlockType.DELETE );
			        d.setStart( ((CommonToken)s).getStartIndex() );
			        this.addBlockDescr( d );
			        d.setEnd( ((CommonToken)c).getStopIndex() );
			    }
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 92, deleteStatement_StartIndex); }

		}
	}
	// $ANTLR end "deleteStatement"



	// $ANTLR start "insertStatement"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:987:1: insertStatement : s= 'insert' '(' expression c= ')' ;
	public final void insertStatement() throws RecognitionException {
		int insertStatement_StartIndex = input.index();

		Token s=null;
		Token c=null;
		ParserRuleReturnScope expression11 =null;

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 93) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:988:5: (s= 'insert' '(' expression c= ')' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:988:7: s= 'insert' '(' expression c= ')'
			{
			s=(Token)match(input,90,FOLLOW_90_in_insertStatement4191); if (state.failed) return;
			match(input,36,FOLLOW_36_in_insertStatement4193); if (state.failed) return;
			pushFollow(FOLLOW_expression_in_insertStatement4199);
			expression11=expression();
			state._fsp--;
			if (state.failed) return;
			c=(Token)match(input,37,FOLLOW_37_in_insertStatement4209); if (state.failed) return;
			if ( state.backtracking==0 ) {
			        JavaStatementBlockDescr d = new JavaStatementBlockDescr( (expression11!=null?input.toString(expression11.start,expression11.stop):null), JavaBlockDescr.BlockType.INSERT );
			        d.setStart( ((CommonToken)s).getStartIndex() );
			        this.addBlockDescr( d );
			        d.setEnd( ((CommonToken)c).getStopIndex() );
			    }
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 93, insertStatement_StartIndex); }

		}
	}
	// $ANTLR end "insertStatement"



	// $ANTLR start "epStatement"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:999:1: epStatement : (s= 'exitPoints' '[' id= StringLiteral c= ']' |s= 'entryPoints' '[' id= StringLiteral c= ']' |s= 'channels' '[' id= StringLiteral c= ']' ) ;
	public final void epStatement() throws RecognitionException {
		int epStatement_StartIndex = input.index();

		Token s=null;
		Token id=null;
		Token c=null;


		        JavaInterfacePointsDescr d = null;
		    
		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 94) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1003:9: ( (s= 'exitPoints' '[' id= StringLiteral c= ']' |s= 'entryPoints' '[' id= StringLiteral c= ']' |s= 'channels' '[' id= StringLiteral c= ']' ) )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1004:9: (s= 'exitPoints' '[' id= StringLiteral c= ']' |s= 'entryPoints' '[' id= StringLiteral c= ']' |s= 'channels' '[' id= StringLiteral c= ']' )
			{
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1004:9: (s= 'exitPoints' '[' id= StringLiteral c= ']' |s= 'entryPoints' '[' id= StringLiteral c= ']' |s= 'channels' '[' id= StringLiteral c= ']' )
			int alt126=3;
			switch ( input.LA(1) ) {
			case 80:
				{
				alt126=1;
				}
				break;
			case 79:
				{
				alt126=2;
				}
				break;
			case 70:
				{
				alt126=3;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 126, 0, input);
				throw nvae;
			}
			switch (alt126) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1004:11: s= 'exitPoints' '[' id= StringLiteral c= ']'
					{
					s=(Token)match(input,80,FOLLOW_80_in_epStatement4262); if (state.failed) return;
					match(input,59,FOLLOW_59_in_epStatement4264); if (state.failed) return;
					id=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_epStatement4268); if (state.failed) return;
					c=(Token)match(input,60,FOLLOW_60_in_epStatement4272); if (state.failed) return;
					if ( state.backtracking==0 ) {
					            d = new JavaInterfacePointsDescr( (id!=null?id.getText():null) );
					            d.setType( JavaBlockDescr.BlockType.EXIT );
					            d.setStart( ((CommonToken)s).getStartIndex() );
					            d.setEnd( ((CommonToken)c).getStopIndex() ); 
					            this.addBlockDescr( d );
					        }
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1012:12: s= 'entryPoints' '[' id= StringLiteral c= ']'
					{
					s=(Token)match(input,79,FOLLOW_79_in_epStatement4298); if (state.failed) return;
					match(input,59,FOLLOW_59_in_epStatement4300); if (state.failed) return;
					id=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_epStatement4304); if (state.failed) return;
					c=(Token)match(input,60,FOLLOW_60_in_epStatement4308); if (state.failed) return;
					if ( state.backtracking==0 ) {
					            d = new JavaInterfacePointsDescr( (id!=null?id.getText():null) );
					            d.setType( JavaBlockDescr.BlockType.ENTRY );
					            d.setStart( ((CommonToken)s).getStartIndex() );
					            d.setEnd( ((CommonToken)c).getStopIndex() ); 
					            this.addBlockDescr( d );
					        }
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1020:12: s= 'channels' '[' id= StringLiteral c= ']'
					{
					s=(Token)match(input,70,FOLLOW_70_in_epStatement4334); if (state.failed) return;
					match(input,59,FOLLOW_59_in_epStatement4336); if (state.failed) return;
					id=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_epStatement4340); if (state.failed) return;
					c=(Token)match(input,60,FOLLOW_60_in_epStatement4344); if (state.failed) return;
					if ( state.backtracking==0 ) {
					            d = new JavaInterfacePointsDescr( (id!=null?id.getText():null) );
					            d.setType( JavaBlockDescr.BlockType.CHANNEL );
					            d.setStart( ((CommonToken)s).getStartIndex() );
					            d.setEnd( ((CommonToken)c).getStopIndex() ); 
					            this.addBlockDescr( d );
					        }
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 94, epStatement_StartIndex); }

		}
	}
	// $ANTLR end "epStatement"


	public static class formalParameter_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "formalParameter"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1031:1: formalParameter : ( variableModifier )* type variableDeclaratorId ;
	public final JavaParser.formalParameter_return formalParameter() throws RecognitionException {
		JavaParser.formalParameter_return retval = new JavaParser.formalParameter_return();
		retval.start = input.LT(1);
		int formalParameter_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 95) ) { return retval; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1032:5: ( ( variableModifier )* type variableDeclaratorId )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1032:7: ( variableModifier )* type variableDeclaratorId
			{
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1032:7: ( variableModifier )*
			loop127:
			while (true) {
				int alt127=2;
				int LA127_0 = input.LA(1);
				if ( (LA127_0==58||LA127_0==83) ) {
					alt127=1;
				}

				switch (alt127) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1032:7: variableModifier
					{
					pushFollow(FOLLOW_variableModifier_in_formalParameter4388);
					variableModifier();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;

				default :
					break loop127;
				}
			}

			pushFollow(FOLLOW_type_in_formalParameter4391);
			type();
			state._fsp--;
			if (state.failed) return retval;
			pushFollow(FOLLOW_variableDeclaratorId_in_formalParameter4393);
			variableDeclaratorId();
			state._fsp--;
			if (state.failed) return retval;
			}

			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 95, formalParameter_StartIndex); }

		}
		return retval;
	}
	// $ANTLR end "formalParameter"



	// $ANTLR start "switchBlockStatementGroups"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1035:1: switchBlockStatementGroups : ( switchBlockStatementGroup )* ;
	public final void switchBlockStatementGroups() throws RecognitionException {
		int switchBlockStatementGroups_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 96) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1036:5: ( ( switchBlockStatementGroup )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1036:7: ( switchBlockStatementGroup )*
			{
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1036:7: ( switchBlockStatementGroup )*
			loop128:
			while (true) {
				int alt128=2;
				int LA128_0 = input.LA(1);
				if ( (LA128_0==68||LA128_0==74) ) {
					alt128=1;
				}

				switch (alt128) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1036:8: switchBlockStatementGroup
					{
					pushFollow(FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups4411);
					switchBlockStatementGroup();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop128;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 96, switchBlockStatementGroups_StartIndex); }

		}
	}
	// $ANTLR end "switchBlockStatementGroups"



	// $ANTLR start "switchBlockStatementGroup"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1039:1: switchBlockStatementGroup : switchLabel ( blockStatement )* ;
	public final void switchBlockStatementGroup() throws RecognitionException {
		int switchBlockStatementGroup_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 97) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1040:5: ( switchLabel ( blockStatement )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1040:7: switchLabel ( blockStatement )*
			{
			pushFollow(FOLLOW_switchLabel_in_switchBlockStatementGroup4430);
			switchLabel();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1040:19: ( blockStatement )*
			loop129:
			while (true) {
				int alt129=2;
				alt129 = dfa129.predict(input);
				switch (alt129) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1040:19: blockStatement
					{
					pushFollow(FOLLOW_blockStatement_in_switchBlockStatementGroup4432);
					blockStatement();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop129;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 97, switchBlockStatementGroup_StartIndex); }

		}
	}
	// $ANTLR end "switchBlockStatementGroup"



	// $ANTLR start "switchLabel"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1043:1: switchLabel : ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' );
	public final void switchLabel() throws RecognitionException {
		int switchLabel_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 98) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1044:5: ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' )
			int alt130=3;
			int LA130_0 = input.LA(1);
			if ( (LA130_0==68) ) {
				int LA130_1 = input.LA(2);
				if ( ((LA130_1 >= CharacterLiteral && LA130_1 <= DecimalLiteral)||LA130_1==FloatingPointLiteral||LA130_1==HexLiteral||(LA130_1 >= OctalLiteral && LA130_1 <= StringLiteral)||LA130_1==29||LA130_1==36||(LA130_1 >= 40 && LA130_1 <= 41)||(LA130_1 >= 44 && LA130_1 <= 45)||LA130_1==53||LA130_1==65||LA130_1==67||(LA130_1 >= 70 && LA130_1 <= 71)||LA130_1==77||(LA130_1 >= 79 && LA130_1 <= 80)||LA130_1==82||LA130_1==85||LA130_1==92||LA130_1==94||(LA130_1 >= 97 && LA130_1 <= 98)||LA130_1==105||LA130_1==108||LA130_1==111||LA130_1==115||LA130_1==118||LA130_1==126) ) {
					alt130=1;
				}
				else if ( (LA130_1==Identifier) ) {
					int LA130_23 = input.LA(3);
					if ( (synpred190_Java()) ) {
						alt130=1;
					}
					else if ( (synpred191_Java()) ) {
						alt130=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 130, 23, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 130, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA130_0==74) ) {
				alt130=3;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 130, 0, input);
				throw nvae;
			}

			switch (alt130) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1044:7: 'case' constantExpression ':'
					{
					match(input,68,FOLLOW_68_in_switchLabel4450); if (state.failed) return;
					pushFollow(FOLLOW_constantExpression_in_switchLabel4452);
					constantExpression();
					state._fsp--;
					if (state.failed) return;
					match(input,51,FOLLOW_51_in_switchLabel4454); if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1045:9: 'case' enumConstantName ':'
					{
					match(input,68,FOLLOW_68_in_switchLabel4464); if (state.failed) return;
					pushFollow(FOLLOW_enumConstantName_in_switchLabel4466);
					enumConstantName();
					state._fsp--;
					if (state.failed) return;
					match(input,51,FOLLOW_51_in_switchLabel4468); if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1046:9: 'default' ':'
					{
					match(input,74,FOLLOW_74_in_switchLabel4478); if (state.failed) return;
					match(input,51,FOLLOW_51_in_switchLabel4480); if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 98, switchLabel_StartIndex); }

		}
	}
	// $ANTLR end "switchLabel"



	// $ANTLR start "moreStatementExpressions"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1049:1: moreStatementExpressions : ( ',' statementExpression )* ;
	public final void moreStatementExpressions() throws RecognitionException {
		int moreStatementExpressions_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 99) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1050:5: ( ( ',' statementExpression )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1050:7: ( ',' statementExpression )*
			{
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1050:7: ( ',' statementExpression )*
			loop131:
			while (true) {
				int alt131=2;
				int LA131_0 = input.LA(1);
				if ( (LA131_0==43) ) {
					alt131=1;
				}

				switch (alt131) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1050:8: ',' statementExpression
					{
					match(input,43,FOLLOW_43_in_moreStatementExpressions4498); if (state.failed) return;
					pushFollow(FOLLOW_statementExpression_in_moreStatementExpressions4500);
					statementExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop131;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 99, moreStatementExpressions_StartIndex); }

		}
	}
	// $ANTLR end "moreStatementExpressions"



	// $ANTLR start "forControl"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1053:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );
	public final void forControl() throws RecognitionException {
		VarDecl_stack.push(new VarDecl_scope());

		int forControl_StartIndex = input.index();


		            increaseLevel();
		            VarDecl_stack.peek().descr = new JavaLocalDeclarationDescr();
		        
		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 100) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1064:5: ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? )
			int alt135=2;
			alt135 = dfa135.predict(input);
			switch (alt135) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1064:7: forVarControl
					{
					pushFollow(FOLLOW_forVarControl_in_forControl4558);
					forVarControl();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1065:7: ( forInit )? ';' ( expression )? ';' ( forUpdate )?
					{
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1065:7: ( forInit )?
					int alt132=2;
					int LA132_0 = input.LA(1);
					if ( ((LA132_0 >= CharacterLiteral && LA132_0 <= DecimalLiteral)||LA132_0==FloatingPointLiteral||(LA132_0 >= HexLiteral && LA132_0 <= Identifier)||(LA132_0 >= OctalLiteral && LA132_0 <= StringLiteral)||LA132_0==29||LA132_0==36||(LA132_0 >= 40 && LA132_0 <= 41)||(LA132_0 >= 44 && LA132_0 <= 45)||LA132_0==53||LA132_0==58||LA132_0==65||LA132_0==67||(LA132_0 >= 70 && LA132_0 <= 71)||LA132_0==77||(LA132_0 >= 79 && LA132_0 <= 80)||(LA132_0 >= 82 && LA132_0 <= 83)||LA132_0==85||LA132_0==92||LA132_0==94||(LA132_0 >= 97 && LA132_0 <= 98)||LA132_0==105||LA132_0==108||LA132_0==111||LA132_0==115||LA132_0==118||LA132_0==126) ) {
						alt132=1;
					}
					switch (alt132) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1065:7: forInit
							{
							pushFollow(FOLLOW_forInit_in_forControl4566);
							forInit();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					match(input,52,FOLLOW_52_in_forControl4569); if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1065:20: ( expression )?
					int alt133=2;
					int LA133_0 = input.LA(1);
					if ( ((LA133_0 >= CharacterLiteral && LA133_0 <= DecimalLiteral)||LA133_0==FloatingPointLiteral||(LA133_0 >= HexLiteral && LA133_0 <= Identifier)||(LA133_0 >= OctalLiteral && LA133_0 <= StringLiteral)||LA133_0==29||LA133_0==36||(LA133_0 >= 40 && LA133_0 <= 41)||(LA133_0 >= 44 && LA133_0 <= 45)||LA133_0==53||LA133_0==65||LA133_0==67||(LA133_0 >= 70 && LA133_0 <= 71)||LA133_0==77||(LA133_0 >= 79 && LA133_0 <= 80)||LA133_0==82||LA133_0==85||LA133_0==92||LA133_0==94||(LA133_0 >= 97 && LA133_0 <= 98)||LA133_0==105||LA133_0==108||LA133_0==111||LA133_0==115||LA133_0==118||LA133_0==126) ) {
						alt133=1;
					}
					switch (alt133) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1065:20: expression
							{
							pushFollow(FOLLOW_expression_in_forControl4571);
							expression();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					match(input,52,FOLLOW_52_in_forControl4574); if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1065:36: ( forUpdate )?
					int alt134=2;
					int LA134_0 = input.LA(1);
					if ( ((LA134_0 >= CharacterLiteral && LA134_0 <= DecimalLiteral)||LA134_0==FloatingPointLiteral||(LA134_0 >= HexLiteral && LA134_0 <= Identifier)||(LA134_0 >= OctalLiteral && LA134_0 <= StringLiteral)||LA134_0==29||LA134_0==36||(LA134_0 >= 40 && LA134_0 <= 41)||(LA134_0 >= 44 && LA134_0 <= 45)||LA134_0==53||LA134_0==65||LA134_0==67||(LA134_0 >= 70 && LA134_0 <= 71)||LA134_0==77||(LA134_0 >= 79 && LA134_0 <= 80)||LA134_0==82||LA134_0==85||LA134_0==92||LA134_0==94||(LA134_0 >= 97 && LA134_0 <= 98)||LA134_0==105||LA134_0==108||LA134_0==111||LA134_0==115||LA134_0==118||LA134_0==126) ) {
						alt134=1;
					}
					switch (alt134) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1065:36: forUpdate
							{
							pushFollow(FOLLOW_forUpdate_in_forControl4576);
							forUpdate();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					}
					break;

			}
			if ( state.backtracking==0 ) {
			            addLocalDeclaration( VarDecl_stack.peek().descr );
			            decreaseLevel();
			        }
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 100, forControl_StartIndex); }

			VarDecl_stack.pop();

		}
	}
	// $ANTLR end "forControl"



	// $ANTLR start "forInit"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1068:1: forInit : ( ( variableModifier )* type variableDeclarators | expressionList );
	public final void forInit() throws RecognitionException {
		int forInit_StartIndex = input.index();

		ParserRuleReturnScope variableModifier12 =null;
		ParserRuleReturnScope type13 =null;

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 101) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1069:5: ( ( variableModifier )* type variableDeclarators | expressionList )
			int alt137=2;
			switch ( input.LA(1) ) {
			case 58:
			case 83:
				{
				alt137=1;
				}
				break;
			case Identifier:
				{
				switch ( input.LA(2) ) {
				case 53:
					{
					int LA137_26 = input.LA(3);
					if ( (synpred198_Java()) ) {
						alt137=1;
					}
					else if ( (true) ) {
						alt137=2;
					}

					}
					break;
				case 47:
					{
					int LA137_27 = input.LA(3);
					if ( (synpred198_Java()) ) {
						alt137=1;
					}
					else if ( (true) ) {
						alt137=2;
					}

					}
					break;
				case 59:
					{
					int LA137_28 = input.LA(3);
					if ( (synpred198_Java()) ) {
						alt137=1;
					}
					else if ( (true) ) {
						alt137=2;
					}

					}
					break;
				case Identifier:
					{
					alt137=1;
					}
					break;
				case EOF:
				case 30:
				case 31:
				case 32:
				case 33:
				case 34:
				case 35:
				case 36:
				case 38:
				case 39:
				case 40:
				case 41:
				case 42:
				case 43:
				case 44:
				case 45:
				case 46:
				case 49:
				case 50:
				case 52:
				case 54:
				case 55:
				case 56:
				case 57:
				case 61:
				case 62:
				case 91:
				case 122:
				case 123:
				case 124:
					{
					alt137=2;
					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 137, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case 65:
			case 67:
			case 71:
			case 77:
			case 85:
			case 92:
			case 94:
			case 105:
				{
				switch ( input.LA(2) ) {
				case 59:
					{
					int LA137_55 = input.LA(3);
					if ( (synpred198_Java()) ) {
						alt137=1;
					}
					else if ( (true) ) {
						alt137=2;
					}

					}
					break;
				case Identifier:
					{
					alt137=1;
					}
					break;
				case 47:
					{
					alt137=2;
					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 137, 4, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case CharacterLiteral:
			case DecimalLiteral:
			case FloatingPointLiteral:
			case HexLiteral:
			case OctalLiteral:
			case StringLiteral:
			case 29:
			case 36:
			case 40:
			case 41:
			case 44:
			case 45:
			case 53:
			case 70:
			case 79:
			case 80:
			case 82:
			case 97:
			case 98:
			case 108:
			case 111:
			case 115:
			case 118:
			case 126:
				{
				alt137=2;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 137, 0, input);
				throw nvae;
			}
			switch (alt137) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1069:7: ( variableModifier )* type variableDeclarators
					{
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1069:7: ( variableModifier )*
					loop136:
					while (true) {
						int alt136=2;
						int LA136_0 = input.LA(1);
						if ( (LA136_0==58||LA136_0==83) ) {
							alt136=1;
						}

						switch (alt136) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1069:9: variableModifier
							{
							pushFollow(FOLLOW_variableModifier_in_forInit4596);
							variableModifier12=variableModifier();
							state._fsp--;
							if (state.failed) return;
							if ( state.backtracking==0 ) {
							                VarDecl_stack.peek().descr.updateStart( ((CommonToken)(variableModifier12!=null?(variableModifier12.start):null)).getStartIndex() - 1 );
							                VarDecl_stack.peek().descr.addModifier( (variableModifier12!=null?input.toString(variableModifier12.start,variableModifier12.stop):null) );
							            }
							}
							break;

						default :
							break loop136;
						}
					}

					pushFollow(FOLLOW_type_in_forInit4631);
					type13=type();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {
					            VarDecl_stack.peek().descr.updateStart( ((CommonToken)(type13!=null?(type13.start):null)).getStartIndex() - 1 );
					            VarDecl_stack.peek().descr.setType( (type13!=null?input.toString(type13.start,type13.stop):null) );
					            VarDecl_stack.peek().descr.setEnd( ((CommonToken)(type13!=null?(type13.stop):null)).getStopIndex() );
					        }
					pushFollow(FOLLOW_variableDeclarators_in_forInit4651);
					variableDeclarators();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1082:7: expressionList
					{
					pushFollow(FOLLOW_expressionList_in_forInit4659);
					expressionList();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 101, forInit_StartIndex); }

		}
	}
	// $ANTLR end "forInit"



	// $ANTLR start "forVarControl"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1085:1: forVarControl : ( variableModifier )* type id= Identifier ':' expression ;
	public final void forVarControl() throws RecognitionException {
		int forVarControl_StartIndex = input.index();

		Token id=null;
		ParserRuleReturnScope variableModifier14 =null;
		ParserRuleReturnScope type15 =null;

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 102) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1086:5: ( ( variableModifier )* type id= Identifier ':' expression )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1086:7: ( variableModifier )* type id= Identifier ':' expression
			{
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1086:7: ( variableModifier )*
			loop138:
			while (true) {
				int alt138=2;
				int LA138_0 = input.LA(1);
				if ( (LA138_0==58||LA138_0==83) ) {
					alt138=1;
				}

				switch (alt138) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1086:9: variableModifier
					{
					pushFollow(FOLLOW_variableModifier_in_forVarControl4678);
					variableModifier14=variableModifier();
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) {
					                VarDecl_stack.peek().descr.updateStart( ((CommonToken)(variableModifier14!=null?(variableModifier14.start):null)).getStartIndex() - 1 );
					                VarDecl_stack.peek().descr.addModifier( (variableModifier14!=null?input.toString(variableModifier14.start,variableModifier14.stop):null) );
					            }
					}
					break;

				default :
					break loop138;
				}
			}

			pushFollow(FOLLOW_type_in_forVarControl4713);
			type15=type();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) {
			            VarDecl_stack.peek().descr.updateStart( ((CommonToken)(type15!=null?(type15.start):null)).getStartIndex() - 1 );
			            VarDecl_stack.peek().descr.setType( (type15!=null?input.toString(type15.start,type15.stop):null) );
			            VarDecl_stack.peek().descr.setEnd( ((CommonToken)(type15!=null?(type15.stop):null)).getStopIndex() );
			        }
			id=(Token)match(input,Identifier,FOLLOW_Identifier_in_forVarControl4735); if (state.failed) return;
			if ( state.backtracking==0 ) {
			            JavaLocalDeclarationDescr.IdentifierDescr ident = new JavaLocalDeclarationDescr.IdentifierDescr();
			            ident.setIdentifier( (id!=null?id.getText():null) );
			            ident.setStart( ((CommonToken)id).getStartIndex() - 1 );
			            ident.setEnd( ((CommonToken)id).getStopIndex() );
			            VarDecl_stack.peek().descr.addIdentifier( ident );
			        }
			match(input,51,FOLLOW_51_in_forVarControl4756); if (state.failed) return;
			pushFollow(FOLLOW_expression_in_forVarControl4758);
			expression();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 102, forVarControl_StartIndex); }

		}
	}
	// $ANTLR end "forVarControl"



	// $ANTLR start "forUpdate"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1109:1: forUpdate : expressionList ;
	public final void forUpdate() throws RecognitionException {
		int forUpdate_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 103) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1110:5: ( expressionList )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1110:7: expressionList
			{
			pushFollow(FOLLOW_expressionList_in_forUpdate4775);
			expressionList();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 103, forUpdate_StartIndex); }

		}
	}
	// $ANTLR end "forUpdate"


	public static class parExpression_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "parExpression"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1115:1: parExpression : '(' expression ')' ;
	public final JavaParser.parExpression_return parExpression() throws RecognitionException {
		JavaParser.parExpression_return retval = new JavaParser.parExpression_return();
		retval.start = input.LT(1);
		int parExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 104) ) { return retval; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1116:5: ( '(' expression ')' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1116:7: '(' expression ')'
			{
			match(input,36,FOLLOW_36_in_parExpression4794); if (state.failed) return retval;
			pushFollow(FOLLOW_expression_in_parExpression4796);
			expression();
			state._fsp--;
			if (state.failed) return retval;
			match(input,37,FOLLOW_37_in_parExpression4798); if (state.failed) return retval;
			}

			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 104, parExpression_StartIndex); }

		}
		return retval;
	}
	// $ANTLR end "parExpression"



	// $ANTLR start "expressionList"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1119:1: expressionList : expression ( ',' expression )* ;
	public final void expressionList() throws RecognitionException {
		int expressionList_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 105) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1120:5: ( expression ( ',' expression )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1120:9: expression ( ',' expression )*
			{
			pushFollow(FOLLOW_expression_in_expressionList4817);
			expression();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1120:20: ( ',' expression )*
			loop139:
			while (true) {
				int alt139=2;
				int LA139_0 = input.LA(1);
				if ( (LA139_0==43) ) {
					alt139=1;
				}

				switch (alt139) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1120:21: ',' expression
					{
					match(input,43,FOLLOW_43_in_expressionList4820); if (state.failed) return;
					pushFollow(FOLLOW_expression_in_expressionList4822);
					expression();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop139;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 105, expressionList_StartIndex); }

		}
	}
	// $ANTLR end "expressionList"



	// $ANTLR start "statementExpression"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1123:1: statementExpression : expression ;
	public final void statementExpression() throws RecognitionException {
		int statementExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 106) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1124:5: ( expression )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1124:7: expression
			{
			pushFollow(FOLLOW_expression_in_statementExpression4841);
			expression();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 106, statementExpression_StartIndex); }

		}
	}
	// $ANTLR end "statementExpression"



	// $ANTLR start "constantExpression"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1127:1: constantExpression : expression ;
	public final void constantExpression() throws RecognitionException {
		int constantExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 107) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1128:5: ( expression )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1128:7: expression
			{
			pushFollow(FOLLOW_expression_in_constantExpression4858);
			expression();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 107, constantExpression_StartIndex); }

		}
	}
	// $ANTLR end "constantExpression"


	public static class expression_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "expression"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1131:1: expression : ( assignmentExpression | conditionalExpression ( assignmentOperator expression )? );
	public final JavaParser.expression_return expression() throws RecognitionException {
		JavaParser.expression_return retval = new JavaParser.expression_return();
		retval.start = input.LT(1);
		int expression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 108) ) { return retval; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1132:5: ( assignmentExpression | conditionalExpression ( assignmentOperator expression )? )
			int alt141=2;
			alt141 = dfa141.predict(input);
			switch (alt141) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1132:7: assignmentExpression
					{
					pushFollow(FOLLOW_assignmentExpression_in_expression4875);
					assignmentExpression();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1133:9: conditionalExpression ( assignmentOperator expression )?
					{
					pushFollow(FOLLOW_conditionalExpression_in_expression4885);
					conditionalExpression();
					state._fsp--;
					if (state.failed) return retval;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1133:31: ( assignmentOperator expression )?
					int alt140=2;
					alt140 = dfa140.predict(input);
					switch (alt140) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1133:32: assignmentOperator expression
							{
							pushFollow(FOLLOW_assignmentOperator_in_expression4888);
							assignmentOperator();
							state._fsp--;
							if (state.failed) return retval;
							pushFollow(FOLLOW_expression_in_expression4890);
							expression();
							state._fsp--;
							if (state.failed) return retval;
							}
							break;

					}

					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 108, expression_StartIndex); }

		}
		return retval;
	}
	// $ANTLR end "expression"



	// $ANTLR start "assignmentExpression"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1136:1: assignmentExpression : id= conditionalExpression '=' expression ;
	public final void assignmentExpression() throws RecognitionException {
		int assignmentExpression_StartIndex = input.index();

		ParserRuleReturnScope id =null;

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 109) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1137:5: (id= conditionalExpression '=' expression )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1137:7: id= conditionalExpression '=' expression
			{
			pushFollow(FOLLOW_conditionalExpression_in_assignmentExpression4911);
			id=conditionalExpression();
			state._fsp--;
			if (state.failed) return;
			match(input,54,FOLLOW_54_in_assignmentExpression4913); if (state.failed) return;
			pushFollow(FOLLOW_expression_in_assignmentExpression4915);
			expression();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) {
			            this.addAssignment( (id!=null?input.toString(id.start,id.stop):null) );
			        }
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 109, assignmentExpression_StartIndex); }

		}
	}
	// $ANTLR end "assignmentExpression"



	// $ANTLR start "assignmentOperator"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1143:1: assignmentOperator : ( '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '=' | '>' '>' '>' '=' );
	public final void assignmentOperator() throws RecognitionException {
		int assignmentOperator_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 110) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1144:5: ( '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '=' | '>' '>' '>' '=' )
			int alt142=11;
			switch ( input.LA(1) ) {
			case 42:
				{
				alt142=1;
				}
				break;
			case 46:
				{
				alt142=2;
				}
				break;
			case 39:
				{
				alt142=3;
				}
				break;
			case 50:
				{
				alt142=4;
				}
				break;
			case 35:
				{
				alt142=5;
				}
				break;
			case 123:
				{
				alt142=6;
				}
				break;
			case 62:
				{
				alt142=7;
				}
				break;
			case 32:
				{
				alt142=8;
				}
				break;
			case 53:
				{
				alt142=9;
				}
				break;
			case 56:
				{
				int LA142_10 = input.LA(2);
				if ( (LA142_10==56) ) {
					int LA142_11 = input.LA(3);
					if ( (synpred212_Java()) ) {
						alt142=10;
					}
					else if ( (true) ) {
						alt142=11;
					}

				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 142, 10, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 142, 0, input);
				throw nvae;
			}
			switch (alt142) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1144:7: '+='
					{
					match(input,42,FOLLOW_42_in_assignmentOperator4942); if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1145:9: '-='
					{
					match(input,46,FOLLOW_46_in_assignmentOperator4952); if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1146:9: '*='
					{
					match(input,39,FOLLOW_39_in_assignmentOperator4962); if (state.failed) return;
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1147:9: '/='
					{
					match(input,50,FOLLOW_50_in_assignmentOperator4972); if (state.failed) return;
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1148:9: '&='
					{
					match(input,35,FOLLOW_35_in_assignmentOperator4982); if (state.failed) return;
					}
					break;
				case 6 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1149:9: '|='
					{
					match(input,123,FOLLOW_123_in_assignmentOperator4992); if (state.failed) return;
					}
					break;
				case 7 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1150:9: '^='
					{
					match(input,62,FOLLOW_62_in_assignmentOperator5002); if (state.failed) return;
					}
					break;
				case 8 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1151:9: '%='
					{
					match(input,32,FOLLOW_32_in_assignmentOperator5012); if (state.failed) return;
					}
					break;
				case 9 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1152:9: '<' '<' '='
					{
					match(input,53,FOLLOW_53_in_assignmentOperator5022); if (state.failed) return;
					match(input,53,FOLLOW_53_in_assignmentOperator5024); if (state.failed) return;
					match(input,54,FOLLOW_54_in_assignmentOperator5026); if (state.failed) return;
					}
					break;
				case 10 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1153:9: '>' '>' '='
					{
					match(input,56,FOLLOW_56_in_assignmentOperator5036); if (state.failed) return;
					match(input,56,FOLLOW_56_in_assignmentOperator5038); if (state.failed) return;
					match(input,54,FOLLOW_54_in_assignmentOperator5040); if (state.failed) return;
					}
					break;
				case 11 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1154:9: '>' '>' '>' '='
					{
					match(input,56,FOLLOW_56_in_assignmentOperator5050); if (state.failed) return;
					match(input,56,FOLLOW_56_in_assignmentOperator5052); if (state.failed) return;
					match(input,56,FOLLOW_56_in_assignmentOperator5054); if (state.failed) return;
					match(input,54,FOLLOW_54_in_assignmentOperator5056); if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 110, assignmentOperator_StartIndex); }

		}
	}
	// $ANTLR end "assignmentOperator"


	public static class conditionalExpression_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "conditionalExpression"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1157:1: conditionalExpression : conditionalOrExpression ( '?' expression ':' expression )? ;
	public final JavaParser.conditionalExpression_return conditionalExpression() throws RecognitionException {
		JavaParser.conditionalExpression_return retval = new JavaParser.conditionalExpression_return();
		retval.start = input.LT(1);
		int conditionalExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 111) ) { return retval; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1158:5: ( conditionalOrExpression ( '?' expression ':' expression )? )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1158:9: conditionalOrExpression ( '?' expression ':' expression )?
			{
			pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression5075);
			conditionalOrExpression();
			state._fsp--;
			if (state.failed) return retval;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1158:33: ( '?' expression ':' expression )?
			int alt143=2;
			int LA143_0 = input.LA(1);
			if ( (LA143_0==57) ) {
				alt143=1;
			}
			switch (alt143) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1158:35: '?' expression ':' expression
					{
					match(input,57,FOLLOW_57_in_conditionalExpression5079); if (state.failed) return retval;
					pushFollow(FOLLOW_expression_in_conditionalExpression5081);
					expression();
					state._fsp--;
					if (state.failed) return retval;
					match(input,51,FOLLOW_51_in_conditionalExpression5083); if (state.failed) return retval;
					pushFollow(FOLLOW_expression_in_conditionalExpression5085);
					expression();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;

			}

			}

			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 111, conditionalExpression_StartIndex); }

		}
		return retval;
	}
	// $ANTLR end "conditionalExpression"



	// $ANTLR start "conditionalOrExpression"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1161:1: conditionalOrExpression : conditionalAndExpression ( '||' conditionalAndExpression )* ;
	public final void conditionalOrExpression() throws RecognitionException {
		int conditionalOrExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 112) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1162:5: ( conditionalAndExpression ( '||' conditionalAndExpression )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1162:9: conditionalAndExpression ( '||' conditionalAndExpression )*
			{
			pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression5107);
			conditionalAndExpression();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1162:34: ( '||' conditionalAndExpression )*
			loop144:
			while (true) {
				int alt144=2;
				int LA144_0 = input.LA(1);
				if ( (LA144_0==124) ) {
					alt144=1;
				}

				switch (alt144) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1162:36: '||' conditionalAndExpression
					{
					match(input,124,FOLLOW_124_in_conditionalOrExpression5111); if (state.failed) return;
					pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression5113);
					conditionalAndExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop144;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 112, conditionalOrExpression_StartIndex); }

		}
	}
	// $ANTLR end "conditionalOrExpression"



	// $ANTLR start "conditionalAndExpression"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1165:1: conditionalAndExpression : inclusiveOrExpression ( '&&' inclusiveOrExpression )* ;
	public final void conditionalAndExpression() throws RecognitionException {
		int conditionalAndExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 113) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1166:5: ( inclusiveOrExpression ( '&&' inclusiveOrExpression )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1166:9: inclusiveOrExpression ( '&&' inclusiveOrExpression )*
			{
			pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5135);
			inclusiveOrExpression();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1166:31: ( '&&' inclusiveOrExpression )*
			loop145:
			while (true) {
				int alt145=2;
				int LA145_0 = input.LA(1);
				if ( (LA145_0==33) ) {
					alt145=1;
				}

				switch (alt145) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1166:33: '&&' inclusiveOrExpression
					{
					match(input,33,FOLLOW_33_in_conditionalAndExpression5139); if (state.failed) return;
					pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5141);
					inclusiveOrExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop145;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 113, conditionalAndExpression_StartIndex); }

		}
	}
	// $ANTLR end "conditionalAndExpression"



	// $ANTLR start "inclusiveOrExpression"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1169:1: inclusiveOrExpression : exclusiveOrExpression ( '|' exclusiveOrExpression )* ;
	public final void inclusiveOrExpression() throws RecognitionException {
		int inclusiveOrExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 114) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1170:5: ( exclusiveOrExpression ( '|' exclusiveOrExpression )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1170:9: exclusiveOrExpression ( '|' exclusiveOrExpression )*
			{
			pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5163);
			exclusiveOrExpression();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1170:31: ( '|' exclusiveOrExpression )*
			loop146:
			while (true) {
				int alt146=2;
				int LA146_0 = input.LA(1);
				if ( (LA146_0==122) ) {
					alt146=1;
				}

				switch (alt146) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1170:33: '|' exclusiveOrExpression
					{
					match(input,122,FOLLOW_122_in_inclusiveOrExpression5167); if (state.failed) return;
					pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5169);
					exclusiveOrExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop146;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 114, inclusiveOrExpression_StartIndex); }

		}
	}
	// $ANTLR end "inclusiveOrExpression"



	// $ANTLR start "exclusiveOrExpression"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1173:1: exclusiveOrExpression : andExpression ( '^' andExpression )* ;
	public final void exclusiveOrExpression() throws RecognitionException {
		int exclusiveOrExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 115) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1174:5: ( andExpression ( '^' andExpression )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1174:9: andExpression ( '^' andExpression )*
			{
			pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression5191);
			andExpression();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1174:23: ( '^' andExpression )*
			loop147:
			while (true) {
				int alt147=2;
				int LA147_0 = input.LA(1);
				if ( (LA147_0==61) ) {
					alt147=1;
				}

				switch (alt147) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1174:25: '^' andExpression
					{
					match(input,61,FOLLOW_61_in_exclusiveOrExpression5195); if (state.failed) return;
					pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression5197);
					andExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop147;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 115, exclusiveOrExpression_StartIndex); }

		}
	}
	// $ANTLR end "exclusiveOrExpression"



	// $ANTLR start "andExpression"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1177:1: andExpression : equalityExpression ( '&' equalityExpression )* ;
	public final void andExpression() throws RecognitionException {
		int andExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 116) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1178:5: ( equalityExpression ( '&' equalityExpression )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1178:9: equalityExpression ( '&' equalityExpression )*
			{
			pushFollow(FOLLOW_equalityExpression_in_andExpression5219);
			equalityExpression();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1178:28: ( '&' equalityExpression )*
			loop148:
			while (true) {
				int alt148=2;
				int LA148_0 = input.LA(1);
				if ( (LA148_0==34) ) {
					alt148=1;
				}

				switch (alt148) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1178:30: '&' equalityExpression
					{
					match(input,34,FOLLOW_34_in_andExpression5223); if (state.failed) return;
					pushFollow(FOLLOW_equalityExpression_in_andExpression5225);
					equalityExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop148;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 116, andExpression_StartIndex); }

		}
	}
	// $ANTLR end "andExpression"



	// $ANTLR start "equalityExpression"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1181:1: equalityExpression : instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* ;
	public final void equalityExpression() throws RecognitionException {
		int equalityExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 117) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1182:5: ( instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1182:9: instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )*
			{
			pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression5247);
			instanceOfExpression();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1182:30: ( ( '==' | '!=' ) instanceOfExpression )*
			loop149:
			while (true) {
				int alt149=2;
				int LA149_0 = input.LA(1);
				if ( (LA149_0==30||LA149_0==55) ) {
					alt149=1;
				}

				switch (alt149) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1182:32: ( '==' | '!=' ) instanceOfExpression
					{
					if ( input.LA(1)==30||input.LA(1)==55 ) {
						input.consume();
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression5259);
					instanceOfExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop149;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 117, equalityExpression_StartIndex); }

		}
	}
	// $ANTLR end "equalityExpression"



	// $ANTLR start "instanceOfExpression"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1185:1: instanceOfExpression : relationalExpression ( 'instanceof' type )? ;
	public final void instanceOfExpression() throws RecognitionException {
		int instanceOfExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 118) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1186:5: ( relationalExpression ( 'instanceof' type )? )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1186:9: relationalExpression ( 'instanceof' type )?
			{
			pushFollow(FOLLOW_relationalExpression_in_instanceOfExpression5281);
			relationalExpression();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1186:30: ( 'instanceof' type )?
			int alt150=2;
			int LA150_0 = input.LA(1);
			if ( (LA150_0==91) ) {
				alt150=1;
			}
			switch (alt150) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1186:31: 'instanceof' type
					{
					match(input,91,FOLLOW_91_in_instanceOfExpression5284); if (state.failed) return;
					pushFollow(FOLLOW_type_in_instanceOfExpression5286);
					type();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 118, instanceOfExpression_StartIndex); }

		}
	}
	// $ANTLR end "instanceOfExpression"



	// $ANTLR start "relationalExpression"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1189:1: relationalExpression : shiftExpression ( relationalOp shiftExpression )* ;
	public final void relationalExpression() throws RecognitionException {
		int relationalExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 119) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1190:5: ( shiftExpression ( relationalOp shiftExpression )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1190:9: shiftExpression ( relationalOp shiftExpression )*
			{
			pushFollow(FOLLOW_shiftExpression_in_relationalExpression5307);
			shiftExpression();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1190:25: ( relationalOp shiftExpression )*
			loop151:
			while (true) {
				int alt151=2;
				alt151 = dfa151.predict(input);
				switch (alt151) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1190:27: relationalOp shiftExpression
					{
					pushFollow(FOLLOW_relationalOp_in_relationalExpression5311);
					relationalOp();
					state._fsp--;
					if (state.failed) return;
					pushFollow(FOLLOW_shiftExpression_in_relationalExpression5313);
					shiftExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop151;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 119, relationalExpression_StartIndex); }

		}
	}
	// $ANTLR end "relationalExpression"



	// $ANTLR start "relationalOp"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1193:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' ) ;
	public final void relationalOp() throws RecognitionException {
		int relationalOp_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 120) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1194:5: ( ( '<' '=' | '>' '=' | '<' | '>' ) )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1194:7: ( '<' '=' | '>' '=' | '<' | '>' )
			{
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1194:7: ( '<' '=' | '>' '=' | '<' | '>' )
			int alt152=4;
			int LA152_0 = input.LA(1);
			if ( (LA152_0==53) ) {
				int LA152_1 = input.LA(2);
				if ( (LA152_1==54) ) {
					alt152=1;
				}
				else if ( ((LA152_1 >= CharacterLiteral && LA152_1 <= DecimalLiteral)||LA152_1==FloatingPointLiteral||(LA152_1 >= HexLiteral && LA152_1 <= Identifier)||(LA152_1 >= OctalLiteral && LA152_1 <= StringLiteral)||LA152_1==29||LA152_1==36||(LA152_1 >= 40 && LA152_1 <= 41)||(LA152_1 >= 44 && LA152_1 <= 45)||LA152_1==53||LA152_1==65||LA152_1==67||(LA152_1 >= 70 && LA152_1 <= 71)||LA152_1==77||(LA152_1 >= 79 && LA152_1 <= 80)||LA152_1==82||LA152_1==85||LA152_1==92||LA152_1==94||(LA152_1 >= 97 && LA152_1 <= 98)||LA152_1==105||LA152_1==108||LA152_1==111||LA152_1==115||LA152_1==118||LA152_1==126) ) {
					alt152=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 152, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA152_0==56) ) {
				int LA152_2 = input.LA(2);
				if ( (LA152_2==54) ) {
					alt152=2;
				}
				else if ( ((LA152_2 >= CharacterLiteral && LA152_2 <= DecimalLiteral)||LA152_2==FloatingPointLiteral||(LA152_2 >= HexLiteral && LA152_2 <= Identifier)||(LA152_2 >= OctalLiteral && LA152_2 <= StringLiteral)||LA152_2==29||LA152_2==36||(LA152_2 >= 40 && LA152_2 <= 41)||(LA152_2 >= 44 && LA152_2 <= 45)||LA152_2==53||LA152_2==65||LA152_2==67||(LA152_2 >= 70 && LA152_2 <= 71)||LA152_2==77||(LA152_2 >= 79 && LA152_2 <= 80)||LA152_2==82||LA152_2==85||LA152_2==92||LA152_2==94||(LA152_2 >= 97 && LA152_2 <= 98)||LA152_2==105||LA152_2==108||LA152_2==111||LA152_2==115||LA152_2==118||LA152_2==126) ) {
					alt152=4;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 152, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 152, 0, input);
				throw nvae;
			}

			switch (alt152) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1194:8: '<' '='
					{
					match(input,53,FOLLOW_53_in_relationalOp5334); if (state.failed) return;
					match(input,54,FOLLOW_54_in_relationalOp5336); if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1194:18: '>' '='
					{
					match(input,56,FOLLOW_56_in_relationalOp5340); if (state.failed) return;
					match(input,54,FOLLOW_54_in_relationalOp5342); if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1194:28: '<'
					{
					match(input,53,FOLLOW_53_in_relationalOp5346); if (state.failed) return;
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1194:34: '>'
					{
					match(input,56,FOLLOW_56_in_relationalOp5350); if (state.failed) return;
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 120, relationalOp_StartIndex); }

		}
	}
	// $ANTLR end "relationalOp"



	// $ANTLR start "shiftExpression"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1197:1: shiftExpression : additiveExpression ( shiftOp additiveExpression )* ;
	public final void shiftExpression() throws RecognitionException {
		int shiftExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 121) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1198:5: ( additiveExpression ( shiftOp additiveExpression )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1198:9: additiveExpression ( shiftOp additiveExpression )*
			{
			pushFollow(FOLLOW_additiveExpression_in_shiftExpression5370);
			additiveExpression();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1198:28: ( shiftOp additiveExpression )*
			loop153:
			while (true) {
				int alt153=2;
				alt153 = dfa153.predict(input);
				switch (alt153) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1198:30: shiftOp additiveExpression
					{
					pushFollow(FOLLOW_shiftOp_in_shiftExpression5374);
					shiftOp();
					state._fsp--;
					if (state.failed) return;
					pushFollow(FOLLOW_additiveExpression_in_shiftExpression5376);
					additiveExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop153;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 121, shiftExpression_StartIndex); }

		}
	}
	// $ANTLR end "shiftExpression"



	// $ANTLR start "shiftOp"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1202:1: shiftOp : ( '<' '<' | '>' '>' '>' | '>' '>' ) ;
	public final void shiftOp() throws RecognitionException {
		int shiftOp_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 122) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1203:5: ( ( '<' '<' | '>' '>' '>' | '>' '>' ) )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1203:7: ( '<' '<' | '>' '>' '>' | '>' '>' )
			{
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1203:7: ( '<' '<' | '>' '>' '>' | '>' '>' )
			int alt154=3;
			int LA154_0 = input.LA(1);
			if ( (LA154_0==53) ) {
				alt154=1;
			}
			else if ( (LA154_0==56) ) {
				int LA154_2 = input.LA(2);
				if ( (LA154_2==56) ) {
					int LA154_3 = input.LA(3);
					if ( (synpred228_Java()) ) {
						alt154=2;
					}
					else if ( (true) ) {
						alt154=3;
					}

				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 154, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 154, 0, input);
				throw nvae;
			}

			switch (alt154) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1203:8: '<' '<'
					{
					match(input,53,FOLLOW_53_in_shiftOp5406); if (state.failed) return;
					match(input,53,FOLLOW_53_in_shiftOp5408); if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1203:18: '>' '>' '>'
					{
					match(input,56,FOLLOW_56_in_shiftOp5412); if (state.failed) return;
					match(input,56,FOLLOW_56_in_shiftOp5414); if (state.failed) return;
					match(input,56,FOLLOW_56_in_shiftOp5416); if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1203:32: '>' '>'
					{
					match(input,56,FOLLOW_56_in_shiftOp5420); if (state.failed) return;
					match(input,56,FOLLOW_56_in_shiftOp5422); if (state.failed) return;
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 122, shiftOp_StartIndex); }

		}
	}
	// $ANTLR end "shiftOp"



	// $ANTLR start "additiveExpression"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1207:1: additiveExpression : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* ;
	public final void additiveExpression() throws RecognitionException {
		int additiveExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 123) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1208:5: ( multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1208:9: multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
			{
			pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression5443);
			multiplicativeExpression();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1208:34: ( ( '+' | '-' ) multiplicativeExpression )*
			loop155:
			while (true) {
				int alt155=2;
				int LA155_0 = input.LA(1);
				if ( (LA155_0==40||LA155_0==44) ) {
					alt155=1;
				}

				switch (alt155) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1208:36: ( '+' | '-' ) multiplicativeExpression
					{
					if ( input.LA(1)==40||input.LA(1)==44 ) {
						input.consume();
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression5455);
					multiplicativeExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop155;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 123, additiveExpression_StartIndex); }

		}
	}
	// $ANTLR end "additiveExpression"



	// $ANTLR start "multiplicativeExpression"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1211:1: multiplicativeExpression : unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* ;
	public final void multiplicativeExpression() throws RecognitionException {
		int multiplicativeExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 124) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1212:5: ( unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1212:9: unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
			{
			pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression5477);
			unaryExpression();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1212:25: ( ( '*' | '/' | '%' ) unaryExpression )*
			loop156:
			while (true) {
				int alt156=2;
				int LA156_0 = input.LA(1);
				if ( (LA156_0==31||LA156_0==38||LA156_0==49) ) {
					alt156=1;
				}

				switch (alt156) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1212:27: ( '*' | '/' | '%' ) unaryExpression
					{
					if ( input.LA(1)==31||input.LA(1)==38||input.LA(1)==49 ) {
						input.consume();
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression5495);
					unaryExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop156;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 124, multiplicativeExpression_StartIndex); }

		}
	}
	// $ANTLR end "multiplicativeExpression"



	// $ANTLR start "unaryExpression"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1215:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' primary | '--' primary | unaryExpressionNotPlusMinus );
	public final void unaryExpression() throws RecognitionException {
		int unaryExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 125) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1216:5: ( '+' unaryExpression | '-' unaryExpression | '++' primary | '--' primary | unaryExpressionNotPlusMinus )
			int alt157=5;
			switch ( input.LA(1) ) {
			case 40:
				{
				alt157=1;
				}
				break;
			case 44:
				{
				alt157=2;
				}
				break;
			case 41:
				{
				alt157=3;
				}
				break;
			case 45:
				{
				alt157=4;
				}
				break;
			case CharacterLiteral:
			case DecimalLiteral:
			case FloatingPointLiteral:
			case HexLiteral:
			case Identifier:
			case OctalLiteral:
			case StringLiteral:
			case 29:
			case 36:
			case 53:
			case 65:
			case 67:
			case 70:
			case 71:
			case 77:
			case 79:
			case 80:
			case 82:
			case 85:
			case 92:
			case 94:
			case 97:
			case 98:
			case 105:
			case 108:
			case 111:
			case 115:
			case 118:
			case 126:
				{
				alt157=5;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 157, 0, input);
				throw nvae;
			}
			switch (alt157) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1216:9: '+' unaryExpression
					{
					match(input,40,FOLLOW_40_in_unaryExpression5517); if (state.failed) return;
					pushFollow(FOLLOW_unaryExpression_in_unaryExpression5519);
					unaryExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1217:7: '-' unaryExpression
					{
					match(input,44,FOLLOW_44_in_unaryExpression5527); if (state.failed) return;
					pushFollow(FOLLOW_unaryExpression_in_unaryExpression5529);
					unaryExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1218:9: '++' primary
					{
					match(input,41,FOLLOW_41_in_unaryExpression5539); if (state.failed) return;
					pushFollow(FOLLOW_primary_in_unaryExpression5541);
					primary();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1219:9: '--' primary
					{
					match(input,45,FOLLOW_45_in_unaryExpression5551); if (state.failed) return;
					pushFollow(FOLLOW_primary_in_unaryExpression5553);
					primary();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1220:9: unaryExpressionNotPlusMinus
					{
					pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression5563);
					unaryExpressionNotPlusMinus();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 125, unaryExpression_StartIndex); }

		}
	}
	// $ANTLR end "unaryExpression"



	// $ANTLR start "unaryExpressionNotPlusMinus"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1223:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );
	public final void unaryExpressionNotPlusMinus() throws RecognitionException {
		int unaryExpressionNotPlusMinus_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 126) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1224:5: ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? )
			int alt160=4;
			switch ( input.LA(1) ) {
			case 126:
				{
				alt160=1;
				}
				break;
			case 29:
				{
				alt160=2;
				}
				break;
			case 36:
				{
				switch ( input.LA(2) ) {
				case 65:
				case 67:
				case 71:
				case 77:
				case 85:
				case 92:
				case 94:
				case 105:
					{
					int LA160_20 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				case Identifier:
					{
					int LA160_21 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				case 40:
					{
					int LA160_22 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				case 44:
					{
					int LA160_23 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				case 41:
					{
					int LA160_24 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				case 45:
					{
					int LA160_25 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				case 126:
					{
					int LA160_26 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				case 29:
					{
					int LA160_27 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				case 36:
					{
					int LA160_28 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				case 53:
					{
					int LA160_29 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				case 111:
					{
					int LA160_30 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				case 108:
					{
					int LA160_31 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				case 80:
					{
					int LA160_32 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				case 79:
					{
					int LA160_33 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				case 70:
					{
					int LA160_34 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				case DecimalLiteral:
				case HexLiteral:
				case OctalLiteral:
					{
					int LA160_35 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				case FloatingPointLiteral:
					{
					int LA160_36 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				case CharacterLiteral:
					{
					int LA160_37 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				case StringLiteral:
					{
					int LA160_38 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				case 82:
				case 115:
					{
					int LA160_39 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				case 98:
					{
					int LA160_40 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				case 97:
					{
					int LA160_41 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				case 118:
					{
					int LA160_42 = input.LA(3);
					if ( (synpred240_Java()) ) {
						alt160=3;
					}
					else if ( (true) ) {
						alt160=4;
					}

					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 160, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case CharacterLiteral:
			case DecimalLiteral:
			case FloatingPointLiteral:
			case HexLiteral:
			case Identifier:
			case OctalLiteral:
			case StringLiteral:
			case 53:
			case 65:
			case 67:
			case 70:
			case 71:
			case 77:
			case 79:
			case 80:
			case 82:
			case 85:
			case 92:
			case 94:
			case 97:
			case 98:
			case 105:
			case 108:
			case 111:
			case 115:
			case 118:
				{
				alt160=4;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 160, 0, input);
				throw nvae;
			}
			switch (alt160) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1224:9: '~' unaryExpression
					{
					match(input,126,FOLLOW_126_in_unaryExpressionNotPlusMinus5582); if (state.failed) return;
					pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5584);
					unaryExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1225:8: '!' unaryExpression
					{
					match(input,29,FOLLOW_29_in_unaryExpressionNotPlusMinus5593); if (state.failed) return;
					pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5595);
					unaryExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1226:9: castExpression
					{
					pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus5605);
					castExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1227:9: primary ( selector )* ( '++' | '--' )?
					{
					pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus5615);
					primary();
					state._fsp--;
					if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1227:17: ( selector )*
					loop158:
					while (true) {
						int alt158=2;
						int LA158_0 = input.LA(1);
						if ( (LA158_0==47||LA158_0==59) ) {
							alt158=1;
						}

						switch (alt158) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1227:17: selector
							{
							pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus5617);
							selector();
							state._fsp--;
							if (state.failed) return;
							}
							break;

						default :
							break loop158;
						}
					}

					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1227:27: ( '++' | '--' )?
					int alt159=2;
					int LA159_0 = input.LA(1);
					if ( (LA159_0==41||LA159_0==45) ) {
						alt159=1;
					}
					switch (alt159) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:
							{
							if ( input.LA(1)==41||input.LA(1)==45 ) {
								input.consume();
								state.errorRecovery=false;
								state.failed=false;
							}
							else {
								if (state.backtracking>0) {state.failed=true; return;}
								MismatchedSetException mse = new MismatchedSetException(null,input);
								throw mse;
							}
							}
							break;

					}

					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 126, unaryExpressionNotPlusMinus_StartIndex); }

		}
	}
	// $ANTLR end "unaryExpressionNotPlusMinus"



	// $ANTLR start "castExpression"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1230:1: castExpression : ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus );
	public final void castExpression() throws RecognitionException {
		int castExpression_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 127) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1231:5: ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus )
			int alt162=2;
			int LA162_0 = input.LA(1);
			if ( (LA162_0==36) ) {
				int LA162_1 = input.LA(2);
				if ( (LA162_1==65||LA162_1==67||LA162_1==71||LA162_1==77||LA162_1==85||LA162_1==92||LA162_1==94||LA162_1==105) ) {
					int LA162_2 = input.LA(3);
					if ( (synpred244_Java()) ) {
						alt162=1;
					}
					else if ( (true) ) {
						alt162=2;
					}

				}
				else if ( ((LA162_1 >= CharacterLiteral && LA162_1 <= DecimalLiteral)||LA162_1==FloatingPointLiteral||(LA162_1 >= HexLiteral && LA162_1 <= Identifier)||(LA162_1 >= OctalLiteral && LA162_1 <= StringLiteral)||LA162_1==29||LA162_1==36||(LA162_1 >= 40 && LA162_1 <= 41)||(LA162_1 >= 44 && LA162_1 <= 45)||LA162_1==53||LA162_1==70||(LA162_1 >= 79 && LA162_1 <= 80)||LA162_1==82||(LA162_1 >= 97 && LA162_1 <= 98)||LA162_1==108||LA162_1==111||LA162_1==115||LA162_1==118||LA162_1==126) ) {
					alt162=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 162, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 162, 0, input);
				throw nvae;
			}

			switch (alt162) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1231:8: '(' primitiveType ')' unaryExpression
					{
					match(input,36,FOLLOW_36_in_castExpression5643); if (state.failed) return;
					pushFollow(FOLLOW_primitiveType_in_castExpression5645);
					primitiveType();
					state._fsp--;
					if (state.failed) return;
					match(input,37,FOLLOW_37_in_castExpression5647); if (state.failed) return;
					pushFollow(FOLLOW_unaryExpression_in_castExpression5649);
					unaryExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1232:8: '(' ( type | expression ) ')' unaryExpressionNotPlusMinus
					{
					match(input,36,FOLLOW_36_in_castExpression5658); if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1232:12: ( type | expression )
					int alt161=2;
					switch ( input.LA(1) ) {
					case Identifier:
						{
						int LA161_1 = input.LA(2);
						if ( (synpred245_Java()) ) {
							alt161=1;
						}
						else if ( (true) ) {
							alt161=2;
						}

						}
						break;
					case 65:
					case 67:
					case 71:
					case 77:
					case 85:
					case 92:
					case 94:
					case 105:
						{
						switch ( input.LA(2) ) {
						case 59:
							{
							int LA161_51 = input.LA(3);
							if ( (synpred245_Java()) ) {
								alt161=1;
							}
							else if ( (true) ) {
								alt161=2;
							}

							}
							break;
						case 37:
							{
							alt161=1;
							}
							break;
						case 47:
							{
							alt161=2;
							}
							break;
						default:
							if (state.backtracking>0) {state.failed=true; return;}
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 161, 2, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}
						}
						break;
					case CharacterLiteral:
					case DecimalLiteral:
					case FloatingPointLiteral:
					case HexLiteral:
					case OctalLiteral:
					case StringLiteral:
					case 29:
					case 36:
					case 40:
					case 41:
					case 44:
					case 45:
					case 53:
					case 70:
					case 79:
					case 80:
					case 82:
					case 97:
					case 98:
					case 108:
					case 111:
					case 115:
					case 118:
					case 126:
						{
						alt161=2;
						}
						break;
					default:
						if (state.backtracking>0) {state.failed=true; return;}
						NoViableAltException nvae =
							new NoViableAltException("", 161, 0, input);
						throw nvae;
					}
					switch (alt161) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1232:13: type
							{
							pushFollow(FOLLOW_type_in_castExpression5661);
							type();
							state._fsp--;
							if (state.failed) return;
							}
							break;
						case 2 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1232:20: expression
							{
							pushFollow(FOLLOW_expression_in_castExpression5665);
							expression();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					match(input,37,FOLLOW_37_in_castExpression5668); if (state.failed) return;
					pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression5670);
					unaryExpressionNotPlusMinus();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 127, castExpression_StartIndex); }

		}
	}
	// $ANTLR end "castExpression"



	// $ANTLR start "primary"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1235:1: primary : ( parExpression | nonWildcardTypeArguments ( explicitGenericInvocationSuffix | 'this' arguments ) | 'this' ( '.' Identifier )* ( identifierSuffix )? | 'super' superSuffix | epStatement ( '.' methodName )* ( identifierSuffix )? | literal | 'new' creator |i= Identifier ( '.' methodName )* ( identifierSuffix )? | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );
	public final void primary() throws RecognitionException {
		int primary_StartIndex = input.index();

		Token i=null;

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 128) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1236:5: ( parExpression | nonWildcardTypeArguments ( explicitGenericInvocationSuffix | 'this' arguments ) | 'this' ( '.' Identifier )* ( identifierSuffix )? | 'super' superSuffix | epStatement ( '.' methodName )* ( identifierSuffix )? | literal | 'new' creator |i= Identifier ( '.' methodName )* ( identifierSuffix )? | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' )
			int alt171=10;
			switch ( input.LA(1) ) {
			case 36:
				{
				alt171=1;
				}
				break;
			case 53:
				{
				alt171=2;
				}
				break;
			case 111:
				{
				alt171=3;
				}
				break;
			case 108:
				{
				alt171=4;
				}
				break;
			case 70:
			case 79:
			case 80:
				{
				alt171=5;
				}
				break;
			case CharacterLiteral:
			case DecimalLiteral:
			case FloatingPointLiteral:
			case HexLiteral:
			case OctalLiteral:
			case StringLiteral:
			case 82:
			case 98:
			case 115:
				{
				alt171=6;
				}
				break;
			case 97:
				{
				alt171=7;
				}
				break;
			case Identifier:
				{
				alt171=8;
				}
				break;
			case 65:
			case 67:
			case 71:
			case 77:
			case 85:
			case 92:
			case 94:
			case 105:
				{
				alt171=9;
				}
				break;
			case 118:
				{
				alt171=10;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 171, 0, input);
				throw nvae;
			}
			switch (alt171) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1236:7: parExpression
					{
					pushFollow(FOLLOW_parExpression_in_primary5687);
					parExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1237:9: nonWildcardTypeArguments ( explicitGenericInvocationSuffix | 'this' arguments )
					{
					pushFollow(FOLLOW_nonWildcardTypeArguments_in_primary5697);
					nonWildcardTypeArguments();
					state._fsp--;
					if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1238:9: ( explicitGenericInvocationSuffix | 'this' arguments )
					int alt163=2;
					int LA163_0 = input.LA(1);
					if ( (LA163_0==Identifier||LA163_0==108) ) {
						alt163=1;
					}
					else if ( (LA163_0==111) ) {
						alt163=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return;}
						NoViableAltException nvae =
							new NoViableAltException("", 163, 0, input);
						throw nvae;
					}

					switch (alt163) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1238:10: explicitGenericInvocationSuffix
							{
							pushFollow(FOLLOW_explicitGenericInvocationSuffix_in_primary5708);
							explicitGenericInvocationSuffix();
							state._fsp--;
							if (state.failed) return;
							}
							break;
						case 2 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1238:44: 'this' arguments
							{
							match(input,111,FOLLOW_111_in_primary5712); if (state.failed) return;
							pushFollow(FOLLOW_arguments_in_primary5714);
							arguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1239:9: 'this' ( '.' Identifier )* ( identifierSuffix )?
					{
					match(input,111,FOLLOW_111_in_primary5725); if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1239:16: ( '.' Identifier )*
					loop164:
					while (true) {
						int alt164=2;
						int LA164_0 = input.LA(1);
						if ( (LA164_0==47) ) {
							int LA164_3 = input.LA(2);
							if ( (LA164_3==Identifier) ) {
								int LA164_37 = input.LA(3);
								if ( (synpred249_Java()) ) {
									alt164=1;
								}

							}

						}

						switch (alt164) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1239:17: '.' Identifier
							{
							match(input,47,FOLLOW_47_in_primary5728); if (state.failed) return;
							match(input,Identifier,FOLLOW_Identifier_in_primary5730); if (state.failed) return;
							}
							break;

						default :
							break loop164;
						}
					}

					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1239:34: ( identifierSuffix )?
					int alt165=2;
					alt165 = dfa165.predict(input);
					switch (alt165) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1239:35: identifierSuffix
							{
							pushFollow(FOLLOW_identifierSuffix_in_primary5735);
							identifierSuffix();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1240:9: 'super' superSuffix
					{
					match(input,108,FOLLOW_108_in_primary5747); if (state.failed) return;
					pushFollow(FOLLOW_superSuffix_in_primary5749);
					superSuffix();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1241:9: epStatement ( '.' methodName )* ( identifierSuffix )?
					{
					pushFollow(FOLLOW_epStatement_in_primary5759);
					epStatement();
					state._fsp--;
					if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1241:21: ( '.' methodName )*
					loop166:
					while (true) {
						int alt166=2;
						int LA166_0 = input.LA(1);
						if ( (LA166_0==47) ) {
							int LA166_3 = input.LA(2);
							if ( (LA166_3==Identifier||LA166_3==75||LA166_3==90||LA166_3==95||LA166_3==103||LA166_3==117) ) {
								int LA166_38 = input.LA(3);
								if ( (synpred253_Java()) ) {
									alt166=1;
								}

							}

						}

						switch (alt166) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1241:22: '.' methodName
							{
							match(input,47,FOLLOW_47_in_primary5762); if (state.failed) return;
							pushFollow(FOLLOW_methodName_in_primary5764);
							methodName();
							state._fsp--;
							if (state.failed) return;
							}
							break;

						default :
							break loop166;
						}
					}

					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1241:39: ( identifierSuffix )?
					int alt167=2;
					alt167 = dfa167.predict(input);
					switch (alt167) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1241:40: identifierSuffix
							{
							pushFollow(FOLLOW_identifierSuffix_in_primary5769);
							identifierSuffix();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					}
					break;
				case 6 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1242:9: literal
					{
					pushFollow(FOLLOW_literal_in_primary5781);
					literal();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 7 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1243:9: 'new' creator
					{
					match(input,97,FOLLOW_97_in_primary5791); if (state.failed) return;
					pushFollow(FOLLOW_creator_in_primary5793);
					creator();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 8 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1244:9: i= Identifier ( '.' methodName )* ( identifierSuffix )?
					{
					i=(Token)match(input,Identifier,FOLLOW_Identifier_in_primary5805); if (state.failed) return;
					if ( state.backtracking==0 ) { if( ! "(".equals( input.LT(1) == null ? "" : input.LT(1).getText() ) ) identifiers.add( (i!=null?i.getText():null) );  }
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1244:126: ( '.' methodName )*
					loop168:
					while (true) {
						int alt168=2;
						int LA168_0 = input.LA(1);
						if ( (LA168_0==47) ) {
							int LA168_3 = input.LA(2);
							if ( (LA168_3==Identifier||LA168_3==75||LA168_3==90||LA168_3==95||LA168_3==103||LA168_3==117) ) {
								int LA168_38 = input.LA(3);
								if ( (synpred258_Java()) ) {
									alt168=1;
								}

							}

						}

						switch (alt168) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1244:127: '.' methodName
							{
							match(input,47,FOLLOW_47_in_primary5810); if (state.failed) return;
							pushFollow(FOLLOW_methodName_in_primary5812);
							methodName();
							state._fsp--;
							if (state.failed) return;
							}
							break;

						default :
							break loop168;
						}
					}

					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1244:144: ( identifierSuffix )?
					int alt169=2;
					alt169 = dfa169.predict(input);
					switch (alt169) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1244:145: identifierSuffix
							{
							pushFollow(FOLLOW_identifierSuffix_in_primary5817);
							identifierSuffix();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					}
					break;
				case 9 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1245:9: primitiveType ( '[' ']' )* '.' 'class'
					{
					pushFollow(FOLLOW_primitiveType_in_primary5829);
					primitiveType();
					state._fsp--;
					if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1245:23: ( '[' ']' )*
					loop170:
					while (true) {
						int alt170=2;
						int LA170_0 = input.LA(1);
						if ( (LA170_0==59) ) {
							alt170=1;
						}

						switch (alt170) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1245:24: '[' ']'
							{
							match(input,59,FOLLOW_59_in_primary5832); if (state.failed) return;
							match(input,60,FOLLOW_60_in_primary5834); if (state.failed) return;
							}
							break;

						default :
							break loop170;
						}
					}

					match(input,47,FOLLOW_47_in_primary5838); if (state.failed) return;
					match(input,72,FOLLOW_72_in_primary5840); if (state.failed) return;
					}
					break;
				case 10 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1246:9: 'void' '.' 'class'
					{
					match(input,118,FOLLOW_118_in_primary5850); if (state.failed) return;
					match(input,47,FOLLOW_47_in_primary5852); if (state.failed) return;
					match(input,72,FOLLOW_72_in_primary5854); if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 128, primary_StartIndex); }

		}
	}
	// $ANTLR end "primary"



	// $ANTLR start "methodName"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1249:1: methodName : ( Identifier | 'insert' | 'update' | 'modify' | 'retract' | 'delete' );
	public final void methodName() throws RecognitionException {
		int methodName_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 129) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1250:5: ( Identifier | 'insert' | 'update' | 'modify' | 'retract' | 'delete' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:
			{
			if ( input.LA(1)==Identifier||input.LA(1)==75||input.LA(1)==90||input.LA(1)==95||input.LA(1)==103||input.LA(1)==117 ) {
				input.consume();
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 129, methodName_StartIndex); }

		}
	}
	// $ANTLR end "methodName"



	// $ANTLR start "identifierSuffix"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1253:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' ( nonWildcardTypeArguments )? innerCreator );
	public final void identifierSuffix() throws RecognitionException {
		int identifierSuffix_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 130) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1254:5: ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' ( nonWildcardTypeArguments )? innerCreator )
			int alt175=8;
			switch ( input.LA(1) ) {
			case 59:
				{
				int LA175_1 = input.LA(2);
				if ( (LA175_1==60) ) {
					alt175=1;
				}
				else if ( ((LA175_1 >= CharacterLiteral && LA175_1 <= DecimalLiteral)||LA175_1==FloatingPointLiteral||(LA175_1 >= HexLiteral && LA175_1 <= Identifier)||(LA175_1 >= OctalLiteral && LA175_1 <= StringLiteral)||LA175_1==29||LA175_1==36||(LA175_1 >= 40 && LA175_1 <= 41)||(LA175_1 >= 44 && LA175_1 <= 45)||LA175_1==53||LA175_1==65||LA175_1==67||(LA175_1 >= 70 && LA175_1 <= 71)||LA175_1==77||(LA175_1 >= 79 && LA175_1 <= 80)||LA175_1==82||LA175_1==85||LA175_1==92||LA175_1==94||(LA175_1 >= 97 && LA175_1 <= 98)||LA175_1==105||LA175_1==108||LA175_1==111||LA175_1==115||LA175_1==118||LA175_1==126) ) {
					alt175=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 175, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 36:
				{
				alt175=3;
				}
				break;
			case 47:
				{
				switch ( input.LA(2) ) {
				case 72:
					{
					alt175=4;
					}
					break;
				case 111:
					{
					alt175=6;
					}
					break;
				case 108:
					{
					alt175=7;
					}
					break;
				case 97:
					{
					alt175=8;
					}
					break;
				case 53:
					{
					alt175=5;
					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 175, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 175, 0, input);
				throw nvae;
			}
			switch (alt175) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1254:7: ( '[' ']' )+ '.' 'class'
					{
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1254:7: ( '[' ']' )+
					int cnt172=0;
					loop172:
					while (true) {
						int alt172=2;
						int LA172_0 = input.LA(1);
						if ( (LA172_0==59) ) {
							alt172=1;
						}

						switch (alt172) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1254:8: '[' ']'
							{
							match(input,59,FOLLOW_59_in_identifierSuffix5909); if (state.failed) return;
							match(input,60,FOLLOW_60_in_identifierSuffix5911); if (state.failed) return;
							}
							break;

						default :
							if ( cnt172 >= 1 ) break loop172;
							if (state.backtracking>0) {state.failed=true; return;}
							EarlyExitException eee = new EarlyExitException(172, input);
							throw eee;
						}
						cnt172++;
					}

					match(input,47,FOLLOW_47_in_identifierSuffix5915); if (state.failed) return;
					match(input,72,FOLLOW_72_in_identifierSuffix5917); if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1255:7: ( '[' expression ']' )+
					{
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1255:7: ( '[' expression ']' )+
					int cnt173=0;
					loop173:
					while (true) {
						int alt173=2;
						int LA173_0 = input.LA(1);
						if ( (LA173_0==59) ) {
							switch ( input.LA(2) ) {
							case 40:
								{
								int LA173_32 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							case 44:
								{
								int LA173_33 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							case 41:
								{
								int LA173_34 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							case 45:
								{
								int LA173_35 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							case 126:
								{
								int LA173_36 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							case 29:
								{
								int LA173_37 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							case 36:
								{
								int LA173_38 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							case 53:
								{
								int LA173_39 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							case 111:
								{
								int LA173_40 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							case 108:
								{
								int LA173_41 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							case 80:
								{
								int LA173_42 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							case 79:
								{
								int LA173_43 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							case 70:
								{
								int LA173_44 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							case DecimalLiteral:
							case HexLiteral:
							case OctalLiteral:
								{
								int LA173_45 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							case FloatingPointLiteral:
								{
								int LA173_46 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							case CharacterLiteral:
								{
								int LA173_47 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							case StringLiteral:
								{
								int LA173_48 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							case 82:
							case 115:
								{
								int LA173_49 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							case 98:
								{
								int LA173_50 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							case 97:
								{
								int LA173_51 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							case Identifier:
								{
								int LA173_52 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							case 65:
							case 67:
							case 71:
							case 77:
							case 85:
							case 92:
							case 94:
							case 105:
								{
								int LA173_53 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							case 118:
								{
								int LA173_54 = input.LA(3);
								if ( (synpred270_Java()) ) {
									alt173=1;
								}

								}
								break;
							}
						}

						switch (alt173) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1255:8: '[' expression ']'
							{
							match(input,59,FOLLOW_59_in_identifierSuffix5926); if (state.failed) return;
							pushFollow(FOLLOW_expression_in_identifierSuffix5928);
							expression();
							state._fsp--;
							if (state.failed) return;
							match(input,60,FOLLOW_60_in_identifierSuffix5930); if (state.failed) return;
							}
							break;

						default :
							if ( cnt173 >= 1 ) break loop173;
							if (state.backtracking>0) {state.failed=true; return;}
							EarlyExitException eee = new EarlyExitException(173, input);
							throw eee;
						}
						cnt173++;
					}

					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1256:9: arguments
					{
					pushFollow(FOLLOW_arguments_in_identifierSuffix5943);
					arguments();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1257:9: '.' 'class'
					{
					match(input,47,FOLLOW_47_in_identifierSuffix5953); if (state.failed) return;
					match(input,72,FOLLOW_72_in_identifierSuffix5955); if (state.failed) return;
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1258:9: '.' explicitGenericInvocation
					{
					match(input,47,FOLLOW_47_in_identifierSuffix5965); if (state.failed) return;
					pushFollow(FOLLOW_explicitGenericInvocation_in_identifierSuffix5967);
					explicitGenericInvocation();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 6 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1259:9: '.' 'this'
					{
					match(input,47,FOLLOW_47_in_identifierSuffix5977); if (state.failed) return;
					match(input,111,FOLLOW_111_in_identifierSuffix5979); if (state.failed) return;
					}
					break;
				case 7 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1260:9: '.' 'super' arguments
					{
					match(input,47,FOLLOW_47_in_identifierSuffix5989); if (state.failed) return;
					match(input,108,FOLLOW_108_in_identifierSuffix5991); if (state.failed) return;
					pushFollow(FOLLOW_arguments_in_identifierSuffix5993);
					arguments();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 8 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1261:9: '.' 'new' ( nonWildcardTypeArguments )? innerCreator
					{
					match(input,47,FOLLOW_47_in_identifierSuffix6003); if (state.failed) return;
					match(input,97,FOLLOW_97_in_identifierSuffix6005); if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1261:19: ( nonWildcardTypeArguments )?
					int alt174=2;
					int LA174_0 = input.LA(1);
					if ( (LA174_0==53) ) {
						alt174=1;
					}
					switch (alt174) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1261:20: nonWildcardTypeArguments
							{
							pushFollow(FOLLOW_nonWildcardTypeArguments_in_identifierSuffix6008);
							nonWildcardTypeArguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					pushFollow(FOLLOW_innerCreator_in_identifierSuffix6012);
					innerCreator();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 130, identifierSuffix_StartIndex); }

		}
	}
	// $ANTLR end "identifierSuffix"



	// $ANTLR start "creator"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1264:1: creator : ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) ;
	public final void creator() throws RecognitionException {
		int creator_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 131) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1265:5: ( ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1265:7: ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest )
			{
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1265:7: ( nonWildcardTypeArguments )?
			int alt176=2;
			int LA176_0 = input.LA(1);
			if ( (LA176_0==53) ) {
				alt176=1;
			}
			switch (alt176) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1265:7: nonWildcardTypeArguments
					{
					pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator6029);
					nonWildcardTypeArguments();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			pushFollow(FOLLOW_createdName_in_creator6032);
			createdName();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1266:9: ( arrayCreatorRest | classCreatorRest )
			int alt177=2;
			int LA177_0 = input.LA(1);
			if ( (LA177_0==59) ) {
				alt177=1;
			}
			else if ( (LA177_0==36) ) {
				alt177=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 177, 0, input);
				throw nvae;
			}

			switch (alt177) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1266:10: arrayCreatorRest
					{
					pushFollow(FOLLOW_arrayCreatorRest_in_creator6043);
					arrayCreatorRest();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1266:29: classCreatorRest
					{
					pushFollow(FOLLOW_classCreatorRest_in_creator6047);
					classCreatorRest();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 131, creator_StartIndex); }

		}
	}
	// $ANTLR end "creator"



	// $ANTLR start "createdName"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1269:1: createdName : ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* | primitiveType );
	public final void createdName() throws RecognitionException {
		int createdName_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 132) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1270:5: ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* | primitiveType )
			int alt181=2;
			int LA181_0 = input.LA(1);
			if ( (LA181_0==Identifier) ) {
				alt181=1;
			}
			else if ( (LA181_0==65||LA181_0==67||LA181_0==71||LA181_0==77||LA181_0==85||LA181_0==92||LA181_0==94||LA181_0==105) ) {
				alt181=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 181, 0, input);
				throw nvae;
			}

			switch (alt181) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1270:7: Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )*
					{
					match(input,Identifier,FOLLOW_Identifier_in_createdName6065); if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1270:18: ( typeArguments )?
					int alt178=2;
					int LA178_0 = input.LA(1);
					if ( (LA178_0==53) ) {
						alt178=1;
					}
					switch (alt178) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1270:18: typeArguments
							{
							pushFollow(FOLLOW_typeArguments_in_createdName6067);
							typeArguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1271:9: ( '.' Identifier ( typeArguments )? )*
					loop180:
					while (true) {
						int alt180=2;
						int LA180_0 = input.LA(1);
						if ( (LA180_0==47) ) {
							alt180=1;
						}

						switch (alt180) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1271:10: '.' Identifier ( typeArguments )?
							{
							match(input,47,FOLLOW_47_in_createdName6079); if (state.failed) return;
							match(input,Identifier,FOLLOW_Identifier_in_createdName6081); if (state.failed) return;
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1271:25: ( typeArguments )?
							int alt179=2;
							int LA179_0 = input.LA(1);
							if ( (LA179_0==53) ) {
								alt179=1;
							}
							switch (alt179) {
								case 1 :
									// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1271:25: typeArguments
									{
									pushFollow(FOLLOW_typeArguments_in_createdName6083);
									typeArguments();
									state._fsp--;
									if (state.failed) return;
									}
									break;

							}

							}
							break;

						default :
							break loop180;
						}
					}

					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1272:7: primitiveType
					{
					pushFollow(FOLLOW_primitiveType_in_createdName6094);
					primitiveType();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 132, createdName_StartIndex); }

		}
	}
	// $ANTLR end "createdName"



	// $ANTLR start "innerCreator"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1275:1: innerCreator : Identifier classCreatorRest ;
	public final void innerCreator() throws RecognitionException {
		int innerCreator_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 133) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1276:5: ( Identifier classCreatorRest )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1276:7: Identifier classCreatorRest
			{
			match(input,Identifier,FOLLOW_Identifier_in_innerCreator6111); if (state.failed) return;
			pushFollow(FOLLOW_classCreatorRest_in_innerCreator6113);
			classCreatorRest();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 133, innerCreator_StartIndex); }

		}
	}
	// $ANTLR end "innerCreator"



	// $ANTLR start "arrayCreatorRest"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1279:1: arrayCreatorRest : '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* ) ;
	public final void arrayCreatorRest() throws RecognitionException {
		int arrayCreatorRest_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 134) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1280:5: ( '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* ) )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1280:7: '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* )
			{
			match(input,59,FOLLOW_59_in_arrayCreatorRest6130); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1281:9: ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* )
			int alt185=2;
			int LA185_0 = input.LA(1);
			if ( (LA185_0==60) ) {
				alt185=1;
			}
			else if ( ((LA185_0 >= CharacterLiteral && LA185_0 <= DecimalLiteral)||LA185_0==FloatingPointLiteral||(LA185_0 >= HexLiteral && LA185_0 <= Identifier)||(LA185_0 >= OctalLiteral && LA185_0 <= StringLiteral)||LA185_0==29||LA185_0==36||(LA185_0 >= 40 && LA185_0 <= 41)||(LA185_0 >= 44 && LA185_0 <= 45)||LA185_0==53||LA185_0==65||LA185_0==67||(LA185_0 >= 70 && LA185_0 <= 71)||LA185_0==77||(LA185_0 >= 79 && LA185_0 <= 80)||LA185_0==82||LA185_0==85||LA185_0==92||LA185_0==94||(LA185_0 >= 97 && LA185_0 <= 98)||LA185_0==105||LA185_0==108||LA185_0==111||LA185_0==115||LA185_0==118||LA185_0==126) ) {
				alt185=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 185, 0, input);
				throw nvae;
			}

			switch (alt185) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1281:13: ']' ( '[' ']' )* arrayInitializer
					{
					match(input,60,FOLLOW_60_in_arrayCreatorRest6144); if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1281:17: ( '[' ']' )*
					loop182:
					while (true) {
						int alt182=2;
						int LA182_0 = input.LA(1);
						if ( (LA182_0==59) ) {
							alt182=1;
						}

						switch (alt182) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1281:18: '[' ']'
							{
							match(input,59,FOLLOW_59_in_arrayCreatorRest6147); if (state.failed) return;
							match(input,60,FOLLOW_60_in_arrayCreatorRest6149); if (state.failed) return;
							}
							break;

						default :
							break loop182;
						}
					}

					pushFollow(FOLLOW_arrayInitializer_in_arrayCreatorRest6153);
					arrayInitializer();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1282:13: expression ']' ( '[' expression ']' )* ( '[' ']' )*
					{
					pushFollow(FOLLOW_expression_in_arrayCreatorRest6167);
					expression();
					state._fsp--;
					if (state.failed) return;
					match(input,60,FOLLOW_60_in_arrayCreatorRest6169); if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1282:28: ( '[' expression ']' )*
					loop183:
					while (true) {
						int alt183=2;
						int LA183_0 = input.LA(1);
						if ( (LA183_0==59) ) {
							switch ( input.LA(2) ) {
							case 40:
								{
								int LA183_33 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							case 44:
								{
								int LA183_34 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							case 41:
								{
								int LA183_35 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							case 45:
								{
								int LA183_36 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							case 126:
								{
								int LA183_37 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							case 29:
								{
								int LA183_38 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							case 36:
								{
								int LA183_39 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							case 53:
								{
								int LA183_40 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							case 111:
								{
								int LA183_41 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							case 108:
								{
								int LA183_42 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							case 80:
								{
								int LA183_43 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							case 79:
								{
								int LA183_44 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							case 70:
								{
								int LA183_45 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							case DecimalLiteral:
							case HexLiteral:
							case OctalLiteral:
								{
								int LA183_46 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							case FloatingPointLiteral:
								{
								int LA183_47 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							case CharacterLiteral:
								{
								int LA183_48 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							case StringLiteral:
								{
								int LA183_49 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							case 82:
							case 115:
								{
								int LA183_50 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							case 98:
								{
								int LA183_51 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							case 97:
								{
								int LA183_52 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							case Identifier:
								{
								int LA183_53 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							case 65:
							case 67:
							case 71:
							case 77:
							case 85:
							case 92:
							case 94:
							case 105:
								{
								int LA183_54 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							case 118:
								{
								int LA183_55 = input.LA(3);
								if ( (synpred286_Java()) ) {
									alt183=1;
								}

								}
								break;
							}
						}

						switch (alt183) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1282:29: '[' expression ']'
							{
							match(input,59,FOLLOW_59_in_arrayCreatorRest6172); if (state.failed) return;
							pushFollow(FOLLOW_expression_in_arrayCreatorRest6174);
							expression();
							state._fsp--;
							if (state.failed) return;
							match(input,60,FOLLOW_60_in_arrayCreatorRest6176); if (state.failed) return;
							}
							break;

						default :
							break loop183;
						}
					}

					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1282:50: ( '[' ']' )*
					loop184:
					while (true) {
						int alt184=2;
						int LA184_0 = input.LA(1);
						if ( (LA184_0==59) ) {
							int LA184_30 = input.LA(2);
							if ( (LA184_30==60) ) {
								alt184=1;
							}

						}

						switch (alt184) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1282:51: '[' ']'
							{
							match(input,59,FOLLOW_59_in_arrayCreatorRest6181); if (state.failed) return;
							match(input,60,FOLLOW_60_in_arrayCreatorRest6183); if (state.failed) return;
							}
							break;

						default :
							break loop184;
						}
					}

					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 134, arrayCreatorRest_StartIndex); }

		}
	}
	// $ANTLR end "arrayCreatorRest"



	// $ANTLR start "classCreatorRest"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1286:1: classCreatorRest : arguments ( classBody )? ;
	public final void classCreatorRest() throws RecognitionException {
		int classCreatorRest_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 135) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1287:5: ( arguments ( classBody )? )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1287:7: arguments ( classBody )?
			{
			pushFollow(FOLLOW_arguments_in_classCreatorRest6212);
			arguments();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1287:17: ( classBody )?
			int alt186=2;
			int LA186_0 = input.LA(1);
			if ( (LA186_0==121) ) {
				alt186=1;
			}
			switch (alt186) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1287:17: classBody
					{
					pushFollow(FOLLOW_classBody_in_classCreatorRest6214);
					classBody();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 135, classCreatorRest_StartIndex); }

		}
	}
	// $ANTLR end "classCreatorRest"



	// $ANTLR start "explicitGenericInvocation"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1290:1: explicitGenericInvocation : nonWildcardTypeArguments explicitGenericInvocationSuffix ;
	public final void explicitGenericInvocation() throws RecognitionException {
		int explicitGenericInvocation_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 136) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1291:5: ( nonWildcardTypeArguments explicitGenericInvocationSuffix )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1291:7: nonWildcardTypeArguments explicitGenericInvocationSuffix
			{
			pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation6232);
			nonWildcardTypeArguments();
			state._fsp--;
			if (state.failed) return;
			pushFollow(FOLLOW_explicitGenericInvocationSuffix_in_explicitGenericInvocation6234);
			explicitGenericInvocationSuffix();
			state._fsp--;
			if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 136, explicitGenericInvocation_StartIndex); }

		}
	}
	// $ANTLR end "explicitGenericInvocation"



	// $ANTLR start "nonWildcardTypeArguments"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1294:1: nonWildcardTypeArguments : '<' typeList '>' ;
	public final void nonWildcardTypeArguments() throws RecognitionException {
		int nonWildcardTypeArguments_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 137) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1295:5: ( '<' typeList '>' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1295:7: '<' typeList '>'
			{
			match(input,53,FOLLOW_53_in_nonWildcardTypeArguments6251); if (state.failed) return;
			pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments6253);
			typeList();
			state._fsp--;
			if (state.failed) return;
			match(input,56,FOLLOW_56_in_nonWildcardTypeArguments6255); if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 137, nonWildcardTypeArguments_StartIndex); }

		}
	}
	// $ANTLR end "nonWildcardTypeArguments"



	// $ANTLR start "explicitGenericInvocationSuffix"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1298:1: explicitGenericInvocationSuffix : ( 'super' superSuffix | Identifier arguments );
	public final void explicitGenericInvocationSuffix() throws RecognitionException {
		int explicitGenericInvocationSuffix_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 138) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1299:5: ( 'super' superSuffix | Identifier arguments )
			int alt187=2;
			int LA187_0 = input.LA(1);
			if ( (LA187_0==108) ) {
				alt187=1;
			}
			else if ( (LA187_0==Identifier) ) {
				alt187=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 187, 0, input);
				throw nvae;
			}

			switch (alt187) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1299:7: 'super' superSuffix
					{
					match(input,108,FOLLOW_108_in_explicitGenericInvocationSuffix6272); if (state.failed) return;
					pushFollow(FOLLOW_superSuffix_in_explicitGenericInvocationSuffix6274);
					superSuffix();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1300:9: Identifier arguments
					{
					match(input,Identifier,FOLLOW_Identifier_in_explicitGenericInvocationSuffix6284); if (state.failed) return;
					pushFollow(FOLLOW_arguments_in_explicitGenericInvocationSuffix6286);
					arguments();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 138, explicitGenericInvocationSuffix_StartIndex); }

		}
	}
	// $ANTLR end "explicitGenericInvocationSuffix"



	// $ANTLR start "selector"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1303:1: selector : ( '.' methodName ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '.' 'new' ( nonWildcardTypeArguments )? innerCreator | '[' expression ']' );
	public final void selector() throws RecognitionException {
		int selector_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 139) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1304:5: ( '.' methodName ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '.' 'new' ( nonWildcardTypeArguments )? innerCreator | '[' expression ']' )
			int alt190=5;
			int LA190_0 = input.LA(1);
			if ( (LA190_0==47) ) {
				switch ( input.LA(2) ) {
				case 111:
					{
					alt190=2;
					}
					break;
				case 108:
					{
					alt190=3;
					}
					break;
				case 97:
					{
					alt190=4;
					}
					break;
				case Identifier:
				case 75:
				case 90:
				case 95:
				case 103:
				case 117:
					{
					alt190=1;
					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 190, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
			}
			else if ( (LA190_0==59) ) {
				alt190=5;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 190, 0, input);
				throw nvae;
			}

			switch (alt190) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1304:7: '.' methodName ( arguments )?
					{
					match(input,47,FOLLOW_47_in_selector6303); if (state.failed) return;
					pushFollow(FOLLOW_methodName_in_selector6305);
					methodName();
					state._fsp--;
					if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1304:22: ( arguments )?
					int alt188=2;
					int LA188_0 = input.LA(1);
					if ( (LA188_0==36) ) {
						alt188=1;
					}
					switch (alt188) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1304:23: arguments
							{
							pushFollow(FOLLOW_arguments_in_selector6308);
							arguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1305:9: '.' 'this'
					{
					match(input,47,FOLLOW_47_in_selector6320); if (state.failed) return;
					match(input,111,FOLLOW_111_in_selector6322); if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1306:9: '.' 'super' superSuffix
					{
					match(input,47,FOLLOW_47_in_selector6332); if (state.failed) return;
					match(input,108,FOLLOW_108_in_selector6334); if (state.failed) return;
					pushFollow(FOLLOW_superSuffix_in_selector6336);
					superSuffix();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1307:9: '.' 'new' ( nonWildcardTypeArguments )? innerCreator
					{
					match(input,47,FOLLOW_47_in_selector6346); if (state.failed) return;
					match(input,97,FOLLOW_97_in_selector6348); if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1307:19: ( nonWildcardTypeArguments )?
					int alt189=2;
					int LA189_0 = input.LA(1);
					if ( (LA189_0==53) ) {
						alt189=1;
					}
					switch (alt189) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1307:20: nonWildcardTypeArguments
							{
							pushFollow(FOLLOW_nonWildcardTypeArguments_in_selector6351);
							nonWildcardTypeArguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					pushFollow(FOLLOW_innerCreator_in_selector6355);
					innerCreator();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1308:9: '[' expression ']'
					{
					match(input,59,FOLLOW_59_in_selector6365); if (state.failed) return;
					pushFollow(FOLLOW_expression_in_selector6367);
					expression();
					state._fsp--;
					if (state.failed) return;
					match(input,60,FOLLOW_60_in_selector6369); if (state.failed) return;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 139, selector_StartIndex); }

		}
	}
	// $ANTLR end "selector"



	// $ANTLR start "superSuffix"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1311:1: superSuffix : ( arguments | '.' Identifier ( arguments )? );
	public final void superSuffix() throws RecognitionException {
		int superSuffix_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 140) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1312:5: ( arguments | '.' Identifier ( arguments )? )
			int alt192=2;
			int LA192_0 = input.LA(1);
			if ( (LA192_0==36) ) {
				alt192=1;
			}
			else if ( (LA192_0==47) ) {
				alt192=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 192, 0, input);
				throw nvae;
			}

			switch (alt192) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1312:7: arguments
					{
					pushFollow(FOLLOW_arguments_in_superSuffix6386);
					arguments();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1313:9: '.' Identifier ( arguments )?
					{
					match(input,47,FOLLOW_47_in_superSuffix6396); if (state.failed) return;
					match(input,Identifier,FOLLOW_Identifier_in_superSuffix6398); if (state.failed) return;
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1313:24: ( arguments )?
					int alt191=2;
					int LA191_0 = input.LA(1);
					if ( (LA191_0==36) ) {
						alt191=1;
					}
					switch (alt191) {
						case 1 :
							// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1313:25: arguments
							{
							pushFollow(FOLLOW_arguments_in_superSuffix6401);
							arguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 140, superSuffix_StartIndex); }

		}
	}
	// $ANTLR end "superSuffix"



	// $ANTLR start "arguments"
	// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1316:5: arguments : '(' ( expressionList )? ')' ;
	public final void arguments() throws RecognitionException {
		int arguments_StartIndex = input.index();

		try {
			if ( state.backtracking>0 && alreadyParsedRule(input, 141) ) { return; }

			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1317:5: ( '(' ( expressionList )? ')' )
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1317:7: '(' ( expressionList )? ')'
			{
			match(input,36,FOLLOW_36_in_arguments6428); if (state.failed) return;
			// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1317:11: ( expressionList )?
			int alt193=2;
			int LA193_0 = input.LA(1);
			if ( ((LA193_0 >= CharacterLiteral && LA193_0 <= DecimalLiteral)||LA193_0==FloatingPointLiteral||(LA193_0 >= HexLiteral && LA193_0 <= Identifier)||(LA193_0 >= OctalLiteral && LA193_0 <= StringLiteral)||LA193_0==29||LA193_0==36||(LA193_0 >= 40 && LA193_0 <= 41)||(LA193_0 >= 44 && LA193_0 <= 45)||LA193_0==53||LA193_0==65||LA193_0==67||(LA193_0 >= 70 && LA193_0 <= 71)||LA193_0==77||(LA193_0 >= 79 && LA193_0 <= 80)||LA193_0==82||LA193_0==85||LA193_0==92||LA193_0==94||(LA193_0 >= 97 && LA193_0 <= 98)||LA193_0==105||LA193_0==108||LA193_0==111||LA193_0==115||LA193_0==118||LA193_0==126) ) {
				alt193=1;
			}
			switch (alt193) {
				case 1 :
					// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1317:11: expressionList
					{
					pushFollow(FOLLOW_expressionList_in_arguments6430);
					expressionList();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			match(input,37,FOLLOW_37_in_arguments6433); if (state.failed) return;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			if ( state.backtracking>0 ) { memoize(input, 141, arguments_StartIndex); }

		}
	}
	// $ANTLR end "arguments"

	// $ANTLR start synpred1_Java
	public final void synpred1_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:283:7: ( annotations )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:283:7: annotations
		{
		pushFollow(FOLLOW_annotations_in_synpred1_Java81);
		annotations();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred1_Java

	// $ANTLR start synpred38_Java
	public final void synpred38_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:379:7: ( methodDeclaration )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:379:7: methodDeclaration
		{
		pushFollow(FOLLOW_methodDeclaration_in_synpred38_Java718);
		methodDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred38_Java

	// $ANTLR start synpred39_Java
	public final void synpred39_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:380:7: ( fieldDeclaration )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:380:7: fieldDeclaration
		{
		pushFollow(FOLLOW_fieldDeclaration_in_synpred39_Java726);
		fieldDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred39_Java

	// $ANTLR start synpred85_Java
	public final void synpred85_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:532:19: ( '.' Identifier )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:532:19: '.' Identifier
		{
		match(input,47,FOLLOW_47_in_synpred85_Java1703); if (state.failed) return;
		match(input,Identifier,FOLLOW_Identifier_in_synpred85_Java1705); if (state.failed) return;
		}

	}
	// $ANTLR end synpred85_Java

	// $ANTLR start synpred120_Java
	public final void synpred120_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:622:7: ( annotation )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:622:7: annotation
		{
		pushFollow(FOLLOW_annotation_in_synpred120_Java2287);
		annotation();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred120_Java

	// $ANTLR start synpred135_Java
	public final void synpred135_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:669:9: ( classDeclaration ( ';' )? )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:669:9: classDeclaration ( ';' )?
		{
		pushFollow(FOLLOW_classDeclaration_in_synpred135_Java2582);
		classDeclaration();
		state._fsp--;
		if (state.failed) return;
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:669:26: ( ';' )?
		int alt209=2;
		int LA209_0 = input.LA(1);
		if ( (LA209_0==52) ) {
			alt209=1;
		}
		switch (alt209) {
			case 1 :
				// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:669:26: ';'
				{
				match(input,52,FOLLOW_52_in_synpred135_Java2584); if (state.failed) return;
				}
				break;

		}

		}

	}
	// $ANTLR end synpred135_Java

	// $ANTLR start synpred137_Java
	public final void synpred137_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:670:9: ( interfaceDeclaration ( ';' )? )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:670:9: interfaceDeclaration ( ';' )?
		{
		pushFollow(FOLLOW_interfaceDeclaration_in_synpred137_Java2595);
		interfaceDeclaration();
		state._fsp--;
		if (state.failed) return;
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:670:30: ( ';' )?
		int alt210=2;
		int LA210_0 = input.LA(1);
		if ( (LA210_0==52) ) {
			alt210=1;
		}
		switch (alt210) {
			case 1 :
				// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:670:30: ';'
				{
				match(input,52,FOLLOW_52_in_synpred137_Java2597); if (state.failed) return;
				}
				break;

		}

		}

	}
	// $ANTLR end synpred137_Java

	// $ANTLR start synpred139_Java
	public final void synpred139_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:671:9: ( enumDeclaration ( ';' )? )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:671:9: enumDeclaration ( ';' )?
		{
		pushFollow(FOLLOW_enumDeclaration_in_synpred139_Java2608);
		enumDeclaration();
		state._fsp--;
		if (state.failed) return;
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:671:25: ( ';' )?
		int alt211=2;
		int LA211_0 = input.LA(1);
		if ( (LA211_0==52) ) {
			alt211=1;
		}
		switch (alt211) {
			case 1 :
				// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:671:25: ';'
				{
				match(input,52,FOLLOW_52_in_synpred139_Java2610); if (state.failed) return;
				}
				break;

		}

		}

	}
	// $ANTLR end synpred139_Java

	// $ANTLR start synpred144_Java
	public final void synpred144_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:710:7: ( localVariableDeclaration )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:710:7: localVariableDeclaration
		{
		pushFollow(FOLLOW_localVariableDeclaration_in_synpred144_Java2791);
		localVariableDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred144_Java

	// $ANTLR start synpred145_Java
	public final void synpred145_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:711:7: ( classOrInterfaceDeclaration )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:711:7: classOrInterfaceDeclaration
		{
		pushFollow(FOLLOW_classOrInterfaceDeclaration_in_synpred145_Java2799);
		classOrInterfaceDeclaration();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred145_Java

	// $ANTLR start synpred171_Java
	public final void synpred171_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:800:17: ( 'if' parExpression )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:800:17: 'if' parExpression
		{
		match(input,87,FOLLOW_87_in_synpred171_Java3294); if (state.failed) return;
		pushFollow(FOLLOW_parExpression_in_synpred171_Java3296);
		parExpression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred171_Java

	// $ANTLR start synpred172_Java
	public final void synpred172_Java_fragment() throws RecognitionException {
		Token y=null;
		ParserRuleReturnScope z =null;

		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:800:6: (y= 'else' ( 'if' parExpression )? z= statement )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:800:6: y= 'else' ( 'if' parExpression )? z= statement
		{
		y=(Token)match(input,78,FOLLOW_78_in_synpred172_Java3290); if (state.failed) return;
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:800:16: ( 'if' parExpression )?
		int alt216=2;
		int LA216_0 = input.LA(1);
		if ( (LA216_0==87) ) {
			int LA216_1 = input.LA(2);
			if ( (LA216_1==36) ) {
				int LA216_43 = input.LA(3);
				if ( (synpred171_Java()) ) {
					alt216=1;
				}
			}
		}
		switch (alt216) {
			case 1 :
				// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:800:17: 'if' parExpression
				{
				match(input,87,FOLLOW_87_in_synpred172_Java3294); if (state.failed) return;
				pushFollow(FOLLOW_parExpression_in_synpred172_Java3296);
				parExpression();
				state._fsp--;
				if (state.failed) return;
				}
				break;

		}

		pushFollow(FOLLOW_statement_in_synpred172_Java3327);
		z=statement();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred172_Java

	// $ANTLR start synpred174_Java
	public final void synpred174_Java_fragment() throws RecognitionException {
		Token id=null;
		Token z=null;

		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:833:9: ( ( ( variableModifier )* type id= Identifier z= ':' expression ) )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:833:9: ( ( variableModifier )* type id= Identifier z= ':' expression )
		{
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:833:9: ( ( variableModifier )* type id= Identifier z= ':' expression )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:833:11: ( variableModifier )* type id= Identifier z= ':' expression
		{
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:833:11: ( variableModifier )*
		loop217:
		while (true) {
			int alt217=2;
			int LA217_0 = input.LA(1);
			if ( (LA217_0==58||LA217_0==83) ) {
				alt217=1;
			}

			switch (alt217) {
			case 1 :
				// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:833:13: variableModifier
				{
				pushFollow(FOLLOW_variableModifier_in_synpred174_Java3449);
				variableModifier();
				state._fsp--;
				if (state.failed) return;
				}
				break;

			default :
				break loop217;
			}
		}

		pushFollow(FOLLOW_type_in_synpred174_Java3488);
		type();
		state._fsp--;
		if (state.failed) return;
		id=(Token)match(input,Identifier,FOLLOW_Identifier_in_synpred174_Java3514); if (state.failed) return;
		z=(Token)match(input,51,FOLLOW_51_in_synpred174_Java3541); if (state.failed) return;
		pushFollow(FOLLOW_expression_in_synpred174_Java3543);
		expression();
		state._fsp--;
		if (state.failed) return;
		}

		}

	}
	// $ANTLR end synpred174_Java

	// $ANTLR start synpred190_Java
	public final void synpred190_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1044:7: ( 'case' constantExpression ':' )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1044:7: 'case' constantExpression ':'
		{
		match(input,68,FOLLOW_68_in_synpred190_Java4450); if (state.failed) return;
		pushFollow(FOLLOW_constantExpression_in_synpred190_Java4452);
		constantExpression();
		state._fsp--;
		if (state.failed) return;
		match(input,51,FOLLOW_51_in_synpred190_Java4454); if (state.failed) return;
		}

	}
	// $ANTLR end synpred190_Java

	// $ANTLR start synpred191_Java
	public final void synpred191_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1045:9: ( 'case' enumConstantName ':' )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1045:9: 'case' enumConstantName ':'
		{
		match(input,68,FOLLOW_68_in_synpred191_Java4464); if (state.failed) return;
		pushFollow(FOLLOW_enumConstantName_in_synpred191_Java4466);
		enumConstantName();
		state._fsp--;
		if (state.failed) return;
		match(input,51,FOLLOW_51_in_synpred191_Java4468); if (state.failed) return;
		}

	}
	// $ANTLR end synpred191_Java

	// $ANTLR start synpred193_Java
	public final void synpred193_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1064:7: ( forVarControl )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1064:7: forVarControl
		{
		pushFollow(FOLLOW_forVarControl_in_synpred193_Java4558);
		forVarControl();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred193_Java

	// $ANTLR start synpred198_Java
	public final void synpred198_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1069:7: ( ( variableModifier )* type variableDeclarators )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1069:7: ( variableModifier )* type variableDeclarators
		{
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1069:7: ( variableModifier )*
		loop221:
		while (true) {
			int alt221=2;
			int LA221_0 = input.LA(1);
			if ( (LA221_0==58||LA221_0==83) ) {
				alt221=1;
			}

			switch (alt221) {
			case 1 :
				// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1069:9: variableModifier
				{
				pushFollow(FOLLOW_variableModifier_in_synpred198_Java4596);
				variableModifier();
				state._fsp--;
				if (state.failed) return;
				}
				break;

			default :
				break loop221;
			}
		}

		pushFollow(FOLLOW_type_in_synpred198_Java4631);
		type();
		state._fsp--;
		if (state.failed) return;
		pushFollow(FOLLOW_variableDeclarators_in_synpred198_Java4651);
		variableDeclarators();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred198_Java

	// $ANTLR start synpred201_Java
	public final void synpred201_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1132:7: ( assignmentExpression )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1132:7: assignmentExpression
		{
		pushFollow(FOLLOW_assignmentExpression_in_synpred201_Java4875);
		assignmentExpression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred201_Java

	// $ANTLR start synpred202_Java
	public final void synpred202_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1133:32: ( assignmentOperator expression )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1133:32: assignmentOperator expression
		{
		pushFollow(FOLLOW_assignmentOperator_in_synpred202_Java4888);
		assignmentOperator();
		state._fsp--;
		if (state.failed) return;
		pushFollow(FOLLOW_expression_in_synpred202_Java4890);
		expression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred202_Java

	// $ANTLR start synpred212_Java
	public final void synpred212_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1153:9: ( '>' '>' '=' )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1153:9: '>' '>' '='
		{
		match(input,56,FOLLOW_56_in_synpred212_Java5036); if (state.failed) return;
		match(input,56,FOLLOW_56_in_synpred212_Java5038); if (state.failed) return;
		match(input,54,FOLLOW_54_in_synpred212_Java5040); if (state.failed) return;
		}

	}
	// $ANTLR end synpred212_Java

	// $ANTLR start synpred222_Java
	public final void synpred222_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1190:27: ( relationalOp shiftExpression )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1190:27: relationalOp shiftExpression
		{
		pushFollow(FOLLOW_relationalOp_in_synpred222_Java5311);
		relationalOp();
		state._fsp--;
		if (state.failed) return;
		pushFollow(FOLLOW_shiftExpression_in_synpred222_Java5313);
		shiftExpression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred222_Java

	// $ANTLR start synpred226_Java
	public final void synpred226_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1198:30: ( shiftOp additiveExpression )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1198:30: shiftOp additiveExpression
		{
		pushFollow(FOLLOW_shiftOp_in_synpred226_Java5374);
		shiftOp();
		state._fsp--;
		if (state.failed) return;
		pushFollow(FOLLOW_additiveExpression_in_synpred226_Java5376);
		additiveExpression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred226_Java

	// $ANTLR start synpred228_Java
	public final void synpred228_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1203:18: ( '>' '>' '>' )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1203:18: '>' '>' '>'
		{
		match(input,56,FOLLOW_56_in_synpred228_Java5412); if (state.failed) return;
		match(input,56,FOLLOW_56_in_synpred228_Java5414); if (state.failed) return;
		match(input,56,FOLLOW_56_in_synpred228_Java5416); if (state.failed) return;
		}

	}
	// $ANTLR end synpred228_Java

	// $ANTLR start synpred240_Java
	public final void synpred240_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1226:9: ( castExpression )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1226:9: castExpression
		{
		pushFollow(FOLLOW_castExpression_in_synpred240_Java5605);
		castExpression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred240_Java

	// $ANTLR start synpred244_Java
	public final void synpred244_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1231:8: ( '(' primitiveType ')' unaryExpression )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1231:8: '(' primitiveType ')' unaryExpression
		{
		match(input,36,FOLLOW_36_in_synpred244_Java5643); if (state.failed) return;
		pushFollow(FOLLOW_primitiveType_in_synpred244_Java5645);
		primitiveType();
		state._fsp--;
		if (state.failed) return;
		match(input,37,FOLLOW_37_in_synpred244_Java5647); if (state.failed) return;
		pushFollow(FOLLOW_unaryExpression_in_synpred244_Java5649);
		unaryExpression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred244_Java

	// $ANTLR start synpred245_Java
	public final void synpred245_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1232:13: ( type )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1232:13: type
		{
		pushFollow(FOLLOW_type_in_synpred245_Java5661);
		type();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred245_Java

	// $ANTLR start synpred249_Java
	public final void synpred249_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1239:17: ( '.' Identifier )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1239:17: '.' Identifier
		{
		match(input,47,FOLLOW_47_in_synpred249_Java5728); if (state.failed) return;
		match(input,Identifier,FOLLOW_Identifier_in_synpred249_Java5730); if (state.failed) return;
		}

	}
	// $ANTLR end synpred249_Java

	// $ANTLR start synpred250_Java
	public final void synpred250_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1239:35: ( identifierSuffix )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1239:35: identifierSuffix
		{
		pushFollow(FOLLOW_identifierSuffix_in_synpred250_Java5735);
		identifierSuffix();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred250_Java

	// $ANTLR start synpred253_Java
	public final void synpred253_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1241:22: ( '.' methodName )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1241:22: '.' methodName
		{
		match(input,47,FOLLOW_47_in_synpred253_Java5762); if (state.failed) return;
		pushFollow(FOLLOW_methodName_in_synpred253_Java5764);
		methodName();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred253_Java

	// $ANTLR start synpred254_Java
	public final void synpred254_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1241:40: ( identifierSuffix )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1241:40: identifierSuffix
		{
		pushFollow(FOLLOW_identifierSuffix_in_synpred254_Java5769);
		identifierSuffix();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred254_Java

	// $ANTLR start synpred258_Java
	public final void synpred258_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1244:127: ( '.' methodName )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1244:127: '.' methodName
		{
		match(input,47,FOLLOW_47_in_synpred258_Java5810); if (state.failed) return;
		pushFollow(FOLLOW_methodName_in_synpred258_Java5812);
		methodName();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred258_Java

	// $ANTLR start synpred259_Java
	public final void synpred259_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1244:145: ( identifierSuffix )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1244:145: identifierSuffix
		{
		pushFollow(FOLLOW_identifierSuffix_in_synpred259_Java5817);
		identifierSuffix();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred259_Java

	// $ANTLR start synpred270_Java
	public final void synpred270_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1255:8: ( '[' expression ']' )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1255:8: '[' expression ']'
		{
		match(input,59,FOLLOW_59_in_synpred270_Java5926); if (state.failed) return;
		pushFollow(FOLLOW_expression_in_synpred270_Java5928);
		expression();
		state._fsp--;
		if (state.failed) return;
		match(input,60,FOLLOW_60_in_synpred270_Java5930); if (state.failed) return;
		}

	}
	// $ANTLR end synpred270_Java

	// $ANTLR start synpred286_Java
	public final void synpred286_Java_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1282:29: ( '[' expression ']' )
		// src/main/resources/org/drools/compiler/semantics/java/parser/Java.g:1282:29: '[' expression ']'
		{
		match(input,59,FOLLOW_59_in_synpred286_Java6172); if (state.failed) return;
		pushFollow(FOLLOW_expression_in_synpred286_Java6174);
		expression();
		state._fsp--;
		if (state.failed) return;
		match(input,60,FOLLOW_60_in_synpred286_Java6176); if (state.failed) return;
		}

	}
	// $ANTLR end synpred286_Java

	// Delegated rules

	public final boolean synpred222_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred222_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred139_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred139_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred228_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred228_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred286_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred286_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred172_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred172_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred259_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred259_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred1_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred1_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred144_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred144_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred244_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred244_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred190_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred190_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred202_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred202_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred174_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred174_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred258_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred258_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred171_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred171_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred201_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred201_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred240_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred240_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred212_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred212_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred249_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred249_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred193_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred193_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred135_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred135_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred254_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred254_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred39_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred39_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred191_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred191_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred270_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred270_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred85_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred85_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred137_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred137_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred245_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred245_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred38_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred38_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred120_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred120_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred226_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred226_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred198_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred198_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred253_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred253_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred145_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred145_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred250_Java() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred250_Java_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}


	protected DFA105 dfa105 = new DFA105(this);
	protected DFA111 dfa111 = new DFA111(this);
	protected DFA113 dfa113 = new DFA113(this);
	protected DFA118 dfa118 = new DFA118(this);
	protected DFA121 dfa121 = new DFA121(this);
	protected DFA123 dfa123 = new DFA123(this);
	protected DFA129 dfa129 = new DFA129(this);
	protected DFA135 dfa135 = new DFA135(this);
	protected DFA141 dfa141 = new DFA141(this);
	protected DFA140 dfa140 = new DFA140(this);
	protected DFA151 dfa151 = new DFA151(this);
	protected DFA153 dfa153 = new DFA153(this);
	protected DFA165 dfa165 = new DFA165(this);
	protected DFA167 dfa167 = new DFA167(this);
	protected DFA169 dfa169 = new DFA169(this);
	static final String DFA105_eotS =
		"\173\uffff";
	static final String DFA105_eofS =
		"\173\uffff";
	static final String DFA105_minS =
		"\1\5\1\12\3\21\6\uffff\1\12\57\uffff\2\0\16\uffff\1\0\1\uffff\3\0\30\uffff"+
		"\1\0\22\uffff";
	static final String DFA105_maxS =
		"\1\176\1\167\1\135\1\174\1\73\6\uffff\1\167\57\uffff\2\0\16\uffff\1\0"+
		"\1\uffff\3\0\30\uffff\1\0\22\uffff";
	static final String DFA105_acceptS =
		"\5\uffff\1\2\14\uffff\1\3\46\uffff\1\1\101\uffff";
	static final String DFA105_specialS =
		"\73\uffff\1\0\1\1\16\uffff\1\2\1\uffff\1\3\1\4\1\5\30\uffff\1\6\22\uffff}>";
	static final String[] DFA105_transitionS = {
			"\2\22\3\uffff\1\5\3\uffff\1\22\1\uffff\1\22\1\3\6\uffff\2\22\3\uffff"+
			"\1\22\6\uffff\1\22\3\uffff\2\22\2\uffff\2\22\6\uffff\2\22\4\uffff\1\2"+
			"\4\uffff\1\5\1\22\1\4\1\22\1\4\2\uffff\1\22\1\4\1\5\1\22\1\uffff\2\22"+
			"\1\4\1\uffff\2\22\1\uffff\1\22\1\1\1\uffff\1\4\2\22\2\uffff\1\22\1\uffff"+
			"\1\4\1\5\1\4\1\22\1\5\2\22\1\uffff\3\5\2\22\1\4\2\5\2\22\1\13\2\22\1"+
			"\uffff\1\5\4\22\1\5\2\22\4\uffff\1\22",
			"\1\5\6\uffff\1\71\50\uffff\1\74\4\uffff\1\5\1\uffff\1\71\1\uffff\1\71"+
			"\3\uffff\1\71\1\5\4\uffff\1\71\5\uffff\1\73\1\uffff\1\71\6\uffff\1\71"+
			"\1\5\1\71\1\uffff\1\5\3\uffff\3\5\2\uffff\1\71\2\5\2\uffff\1\5\3\uffff"+
			"\1\5\4\uffff\1\5",
			"\1\113\113\uffff\1\5",
			"\1\71\14\uffff\7\22\1\uffff\5\22\1\uffff\3\22\1\116\1\uffff\4\22\1\115"+
			"\4\22\1\uffff\1\117\1\uffff\2\22\34\uffff\1\22\36\uffff\3\22",
			"\1\71\35\uffff\1\22\13\uffff\1\150",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\5\31\uffff\1\22\25\uffff\1\5\4\uffff\1\5\10\uffff\1\5\12\uffff\1"+
			"\5\11\uffff\1\5\2\uffff\1\5\3\uffff\3\5\3\uffff\2\5\2\uffff\1\5\3\uffff"+
			"\1\5\4\uffff\1\5",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\uffff",
			"\1\uffff",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\uffff",
			"",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\uffff",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			""
	};

	static final short[] DFA105_eot = DFA.unpackEncodedString(DFA105_eotS);
	static final short[] DFA105_eof = DFA.unpackEncodedString(DFA105_eofS);
	static final char[] DFA105_min = DFA.unpackEncodedStringToUnsignedChars(DFA105_minS);
	static final char[] DFA105_max = DFA.unpackEncodedStringToUnsignedChars(DFA105_maxS);
	static final short[] DFA105_accept = DFA.unpackEncodedString(DFA105_acceptS);
	static final short[] DFA105_special = DFA.unpackEncodedString(DFA105_specialS);
	static final short[][] DFA105_transition;

	static {
		int numStates = DFA105_transitionS.length;
		DFA105_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA105_transition[i] = DFA.unpackEncodedString(DFA105_transitionS[i]);
		}
	}

	protected class DFA105 extends DFA {

		public DFA105(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 105;
			this.eot = DFA105_eot;
			this.eof = DFA105_eof;
			this.min = DFA105_min;
			this.max = DFA105_max;
			this.accept = DFA105_accept;
			this.special = DFA105_special;
			this.transition = DFA105_transition;
		}
		@Override
		public String getDescription() {
			return "709:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA105_59 = input.LA(1);
						 
						int index105_59 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred144_Java()) ) {s = 57;}
						else if ( (synpred145_Java()) ) {s = 5;}
						 
						input.seek(index105_59);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA105_60 = input.LA(1);
						 
						int index105_60 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred144_Java()) ) {s = 57;}
						else if ( (synpred145_Java()) ) {s = 5;}
						 
						input.seek(index105_60);
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA105_75 = input.LA(1);
						 
						int index105_75 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred144_Java()) ) {s = 57;}
						else if ( (synpred145_Java()) ) {s = 5;}
						 
						input.seek(index105_75);
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA105_77 = input.LA(1);
						 
						int index105_77 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred144_Java()) ) {s = 57;}
						else if ( (true) ) {s = 18;}
						 
						input.seek(index105_77);
						if ( s>=0 ) return s;
						break;

					case 4 : 
						int LA105_78 = input.LA(1);
						 
						int index105_78 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred144_Java()) ) {s = 57;}
						else if ( (true) ) {s = 18;}
						 
						input.seek(index105_78);
						if ( s>=0 ) return s;
						break;

					case 5 : 
						int LA105_79 = input.LA(1);
						 
						int index105_79 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred144_Java()) ) {s = 57;}
						else if ( (true) ) {s = 18;}
						 
						input.seek(index105_79);
						if ( s>=0 ) return s;
						break;

					case 6 : 
						int LA105_104 = input.LA(1);
						 
						int index105_104 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred144_Java()) ) {s = 57;}
						else if ( (true) ) {s = 18;}
						 
						input.seek(index105_104);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 105, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	static final String DFA111_eotS =
		"\106\uffff";
	static final String DFA111_eofS =
		"\106\uffff";
	static final String DFA111_minS =
		"\1\5\47\uffff\1\36\35\uffff";
	static final String DFA111_maxS =
		"\1\176\47\uffff\1\174\35\uffff";
	static final String DFA111_acceptS =
		"\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15\1\16"+
		"\1\17\1\20\1\21\1\22\1\23\1\24\26\uffff\1\25\32\uffff";
	static final String DFA111_specialS =
		"\106\uffff}>";
	static final String[] DFA111_transitionS = {
			"\2\24\7\uffff\1\24\1\uffff\1\24\1\50\6\uffff\2\24\3\uffff\1\24\6\uffff"+
			"\1\24\3\uffff\2\24\2\uffff\2\24\6\uffff\1\23\1\24\12\uffff\1\2\1\24\1"+
			"\14\1\24\2\uffff\2\24\1\uffff\1\15\1\uffff\1\21\1\6\1\24\1\uffff\2\24"+
			"\1\uffff\1\24\2\uffff\1\24\1\4\1\3\2\uffff\1\22\1\uffff\1\24\1\uffff"+
			"\1\24\1\16\1\uffff\2\24\4\uffff\1\20\1\12\1\24\2\uffff\1\24\1\10\1\11"+
			"\1\24\1\13\2\uffff\1\24\1\7\1\17\1\24\1\uffff\1\5\1\1\4\uffff\1\24",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\7\24\1\uffff\5\24\1\uffff\4\24\1\uffff\2\24\1\53\6\24\1\uffff\1\24"+
			"\1\uffff\2\24\34\uffff\1\24\36\uffff\3\24",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			""
	};

	static final short[] DFA111_eot = DFA.unpackEncodedString(DFA111_eotS);
	static final short[] DFA111_eof = DFA.unpackEncodedString(DFA111_eofS);
	static final char[] DFA111_min = DFA.unpackEncodedStringToUnsignedChars(DFA111_minS);
	static final char[] DFA111_max = DFA.unpackEncodedStringToUnsignedChars(DFA111_maxS);
	static final short[] DFA111_accept = DFA.unpackEncodedString(DFA111_acceptS);
	static final short[] DFA111_special = DFA.unpackEncodedString(DFA111_specialS);
	static final short[][] DFA111_transition;

	static {
		int numStates = DFA111_transitionS.length;
		DFA111_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA111_transition[i] = DFA.unpackEncodedString(DFA111_transitionS[i]);
		}
	}

	protected class DFA111 extends DFA {

		public DFA111(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 111;
			this.eot = DFA111_eot;
			this.eof = DFA111_eof;
			this.min = DFA111_min;
			this.max = DFA111_max;
			this.accept = DFA111_accept;
			this.special = DFA111_special;
			this.transition = DFA111_transition;
		}
		@Override
		public String getDescription() {
			return "739:1: statement : ( block | 'assert' expression ( ':' expression )? ';' | ifStatement | forStatement | whileStatement | 'do' statement 'while' parExpression ';' | tryStatement | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | throwStatement | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | modifyStatement | updateStatement | retractStatement | deleteStatement | insertStatement | ';' | statementExpression ';' | Identifier ':' statement );";
		}
	}

	static final String DFA113_eotS =
		"\151\uffff";
	static final String DFA113_eofS =
		"\1\1\150\uffff";
	static final String DFA113_minS =
		"\1\5\74\uffff\1\0\53\uffff";
	static final String DFA113_maxS =
		"\1\176\74\uffff\1\0\53\uffff";
	static final String DFA113_acceptS =
		"\1\uffff\1\2\146\uffff\1\1";
	static final String DFA113_specialS =
		"\75\uffff\1\0\53\uffff}>";
	static final String[] DFA113_transitionS = {
			"\2\1\3\uffff\1\1\3\uffff\1\1\1\uffff\2\1\6\uffff\2\1\3\uffff\1\1\6\uffff"+
			"\1\1\3\uffff\2\1\2\uffff\2\1\6\uffff\2\1\4\uffff\1\1\4\uffff\6\1\1\uffff"+
			"\10\1\1\75\2\1\1\uffff\2\1\1\uffff\3\1\2\uffff\1\1\1\uffff\7\1\1\uffff"+
			"\15\1\1\uffff\10\1\3\uffff\2\1",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\uffff",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			""
	};

	static final short[] DFA113_eot = DFA.unpackEncodedString(DFA113_eotS);
	static final short[] DFA113_eof = DFA.unpackEncodedString(DFA113_eofS);
	static final char[] DFA113_min = DFA.unpackEncodedStringToUnsignedChars(DFA113_minS);
	static final char[] DFA113_max = DFA.unpackEncodedStringToUnsignedChars(DFA113_maxS);
	static final short[] DFA113_accept = DFA.unpackEncodedString(DFA113_acceptS);
	static final short[] DFA113_special = DFA.unpackEncodedString(DFA113_specialS);
	static final short[][] DFA113_transition;

	static {
		int numStates = DFA113_transitionS.length;
		DFA113_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA113_transition[i] = DFA.unpackEncodedString(DFA113_transitionS[i]);
		}
	}

	protected class DFA113 extends DFA {

		public DFA113(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 113;
			this.eot = DFA113_eot;
			this.eof = DFA113_eof;
			this.min = DFA113_min;
			this.max = DFA113_max;
			this.accept = DFA113_accept;
			this.special = DFA113_special;
			this.transition = DFA113_transition;
		}
		@Override
		public String getDescription() {
			return "()* loopback of 799:5: (y= 'else' ( 'if' parExpression )? z= statement )*";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA113_61 = input.LA(1);
						 
						int index113_61 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred172_Java()) ) {s = 104;}
						else if ( (true) ) {s = 1;}
						 
						input.seek(index113_61);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 113, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	static final String DFA118_eotS =
		"\100\uffff";
	static final String DFA118_eofS =
		"\100\uffff";
	static final String DFA118_minS =
		"\1\5\4\21\26\uffff\11\0\30\uffff\2\0\2\uffff";
	static final String DFA118_maxS =
		"\1\176\1\151\1\21\1\174\1\73\26\uffff\11\0\30\uffff\2\0\2\uffff";
	static final String DFA118_acceptS =
		"\5\uffff\1\2\71\uffff\1\1";
	static final String DFA118_specialS =
		"\33\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\30\uffff\1\11\1\12\2\uffff}>";
	static final String[] DFA118_transitionS = {
			"\2\5\7\uffff\1\5\1\uffff\1\5\1\3\6\uffff\2\5\3\uffff\1\5\6\uffff\1\5"+
			"\3\uffff\2\5\2\uffff\2\5\6\uffff\2\5\4\uffff\1\2\6\uffff\1\4\1\uffff"+
			"\1\4\2\uffff\1\5\1\4\5\uffff\1\4\1\uffff\2\5\1\uffff\1\5\1\1\1\uffff"+
			"\1\4\6\uffff\1\4\1\uffff\1\4\2\uffff\2\5\6\uffff\1\4\2\uffff\1\5\2\uffff"+
			"\1\5\3\uffff\1\5\2\uffff\1\5\7\uffff\1\5",
			"\1\33\50\uffff\1\36\6\uffff\1\34\1\uffff\1\34\3\uffff\1\34\5\uffff\1"+
			"\34\5\uffff\1\35\1\uffff\1\34\6\uffff\1\34\1\uffff\1\34\12\uffff\1\34",
			"\1\37",
			"\1\43\14\uffff\7\5\1\uffff\11\5\1\41\1\uffff\2\5\1\uffff\1\5\1\40\4"+
			"\5\1\uffff\1\42\1\uffff\2\5\34\uffff\1\5\36\uffff\3\5",
			"\1\75\35\uffff\1\5\13\uffff\1\74",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\uffff",
			"\1\uffff",
			"",
			""
	};

	static final short[] DFA118_eot = DFA.unpackEncodedString(DFA118_eotS);
	static final short[] DFA118_eof = DFA.unpackEncodedString(DFA118_eofS);
	static final char[] DFA118_min = DFA.unpackEncodedStringToUnsignedChars(DFA118_minS);
	static final char[] DFA118_max = DFA.unpackEncodedStringToUnsignedChars(DFA118_maxS);
	static final short[] DFA118_accept = DFA.unpackEncodedString(DFA118_acceptS);
	static final short[] DFA118_special = DFA.unpackEncodedString(DFA118_specialS);
	static final short[][] DFA118_transition;

	static {
		int numStates = DFA118_transitionS.length;
		DFA118_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA118_transition[i] = DFA.unpackEncodedString(DFA118_transitionS[i]);
		}
	}

	protected class DFA118 extends DFA {

		public DFA118(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 118;
			this.eot = DFA118_eot;
			this.eof = DFA118_eof;
			this.min = DFA118_min;
			this.max = DFA118_max;
			this.accept = DFA118_accept;
			this.special = DFA118_special;
			this.transition = DFA118_transition;
		}
		@Override
		public String getDescription() {
			return "832:5: ( ( ( variableModifier )* type id= Identifier z= ':' expression ) | ( ( forInit )? z= ';' ( expression )? ';' ( forUpdate )? ) )";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA118_27 = input.LA(1);
						 
						int index118_27 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred174_Java()) ) {s = 63;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index118_27);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA118_28 = input.LA(1);
						 
						int index118_28 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred174_Java()) ) {s = 63;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index118_28);
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA118_29 = input.LA(1);
						 
						int index118_29 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred174_Java()) ) {s = 63;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index118_29);
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA118_30 = input.LA(1);
						 
						int index118_30 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred174_Java()) ) {s = 63;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index118_30);
						if ( s>=0 ) return s;
						break;

					case 4 : 
						int LA118_31 = input.LA(1);
						 
						int index118_31 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred174_Java()) ) {s = 63;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index118_31);
						if ( s>=0 ) return s;
						break;

					case 5 : 
						int LA118_32 = input.LA(1);
						 
						int index118_32 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred174_Java()) ) {s = 63;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index118_32);
						if ( s>=0 ) return s;
						break;

					case 6 : 
						int LA118_33 = input.LA(1);
						 
						int index118_33 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred174_Java()) ) {s = 63;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index118_33);
						if ( s>=0 ) return s;
						break;

					case 7 : 
						int LA118_34 = input.LA(1);
						 
						int index118_34 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred174_Java()) ) {s = 63;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index118_34);
						if ( s>=0 ) return s;
						break;

					case 8 : 
						int LA118_35 = input.LA(1);
						 
						int index118_35 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred174_Java()) ) {s = 63;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index118_35);
						if ( s>=0 ) return s;
						break;

					case 9 : 
						int LA118_60 = input.LA(1);
						 
						int index118_60 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred174_Java()) ) {s = 63;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index118_60);
						if ( s>=0 ) return s;
						break;

					case 10 : 
						int LA118_61 = input.LA(1);
						 
						int index118_61 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred174_Java()) ) {s = 63;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index118_61);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 118, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	static final String DFA121_eotS =
		"\100\uffff";
	static final String DFA121_eofS =
		"\1\1\77\uffff";
	static final String DFA121_minS =
		"\1\5\77\uffff";
	static final String DFA121_maxS =
		"\1\176\77\uffff";
	static final String DFA121_acceptS =
		"\1\uffff\1\2\75\uffff\1\1";
	static final String DFA121_specialS =
		"\100\uffff}>";
	static final String[] DFA121_transitionS = {
			"\2\1\3\uffff\1\1\3\uffff\1\1\1\uffff\2\1\6\uffff\2\1\3\uffff\1\1\6\uffff"+
			"\1\1\3\uffff\2\1\2\uffff\2\1\6\uffff\2\1\4\uffff\1\1\4\uffff\6\1\1\77"+
			"\13\1\1\uffff\6\1\2\uffff\1\1\1\uffff\7\1\1\uffff\15\1\1\uffff\10\1\3"+
			"\uffff\2\1",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			""
	};

	static final short[] DFA121_eot = DFA.unpackEncodedString(DFA121_eotS);
	static final short[] DFA121_eof = DFA.unpackEncodedString(DFA121_eofS);
	static final char[] DFA121_min = DFA.unpackEncodedStringToUnsignedChars(DFA121_minS);
	static final char[] DFA121_max = DFA.unpackEncodedStringToUnsignedChars(DFA121_maxS);
	static final short[] DFA121_accept = DFA.unpackEncodedString(DFA121_acceptS);
	static final short[] DFA121_special = DFA.unpackEncodedString(DFA121_specialS);
	static final short[][] DFA121_transition;

	static {
		int numStates = DFA121_transitionS.length;
		DFA121_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA121_transition[i] = DFA.unpackEncodedString(DFA121_transitionS[i]);
		}
	}

	protected class DFA121 extends DFA {

		public DFA121(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 121;
			this.eot = DFA121_eot;
			this.eof = DFA121_eof;
			this.min = DFA121_min;
			this.max = DFA121_max;
			this.accept = DFA121_accept;
			this.special = DFA121_special;
			this.transition = DFA121_transition;
		}
		@Override
		public String getDescription() {
			return "()* loopback of 907:5: (s= 'catch' '(' formalParameter ')' bs= '{' ( blockStatement )* c= '}' )*";
		}
	}

	static final String DFA123_eotS =
		"\77\uffff";
	static final String DFA123_eofS =
		"\1\2\76\uffff";
	static final String DFA123_minS =
		"\1\5\76\uffff";
	static final String DFA123_maxS =
		"\1\176\76\uffff";
	static final String DFA123_acceptS =
		"\1\uffff\1\1\1\2\74\uffff";
	static final String DFA123_specialS =
		"\77\uffff}>";
	static final String[] DFA123_transitionS = {
			"\2\2\3\uffff\1\2\3\uffff\1\2\1\uffff\2\2\6\uffff\2\2\3\uffff\1\2\6\uffff"+
			"\1\2\3\uffff\2\2\2\uffff\2\2\6\uffff\2\2\4\uffff\1\2\4\uffff\6\2\1\uffff"+
			"\13\2\1\uffff\2\2\1\1\3\2\2\uffff\1\2\1\uffff\7\2\1\uffff\15\2\1\uffff"+
			"\10\2\3\uffff\2\2",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			""
	};

	static final short[] DFA123_eot = DFA.unpackEncodedString(DFA123_eotS);
	static final short[] DFA123_eof = DFA.unpackEncodedString(DFA123_eofS);
	static final char[] DFA123_min = DFA.unpackEncodedStringToUnsignedChars(DFA123_minS);
	static final char[] DFA123_max = DFA.unpackEncodedStringToUnsignedChars(DFA123_maxS);
	static final short[] DFA123_accept = DFA.unpackEncodedString(DFA123_acceptS);
	static final short[] DFA123_special = DFA.unpackEncodedString(DFA123_specialS);
	static final short[][] DFA123_transition;

	static {
		int numStates = DFA123_transitionS.length;
		DFA123_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA123_transition[i] = DFA.unpackEncodedString(DFA123_transitionS[i]);
		}
	}

	protected class DFA123 extends DFA {

		public DFA123(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 123;
			this.eot = DFA123_eot;
			this.eof = DFA123_eof;
			this.min = DFA123_min;
			this.max = DFA123_max;
			this.accept = DFA123_accept;
			this.special = DFA123_special;
			this.transition = DFA123_transition;
		}
		@Override
		public String getDescription() {
			return "920:6: (s= 'finally' bs= '{' ( blockStatement )* c= '}' )?";
		}
	}

	static final String DFA129_eotS =
		"\75\uffff";
	static final String DFA129_eofS =
		"\1\1\74\uffff";
	static final String DFA129_minS =
		"\1\5\74\uffff";
	static final String DFA129_maxS =
		"\1\176\74\uffff";
	static final String DFA129_acceptS =
		"\1\uffff\1\2\3\uffff\1\1\67\uffff";
	static final String DFA129_specialS =
		"\75\uffff}>";
	static final String[] DFA129_transitionS = {
			"\2\5\3\uffff\1\5\3\uffff\1\5\1\uffff\2\5\6\uffff\2\5\3\uffff\1\5\6\uffff"+
			"\1\5\3\uffff\2\5\2\uffff\2\5\6\uffff\2\5\4\uffff\1\5\4\uffff\5\5\1\1"+
			"\1\uffff\4\5\1\1\3\5\1\uffff\2\5\1\uffff\2\5\1\uffff\3\5\2\uffff\1\5"+
			"\1\uffff\7\5\1\uffff\15\5\1\uffff\10\5\3\uffff\1\1\1\5",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			""
	};

	static final short[] DFA129_eot = DFA.unpackEncodedString(DFA129_eotS);
	static final short[] DFA129_eof = DFA.unpackEncodedString(DFA129_eofS);
	static final char[] DFA129_min = DFA.unpackEncodedStringToUnsignedChars(DFA129_minS);
	static final char[] DFA129_max = DFA.unpackEncodedStringToUnsignedChars(DFA129_maxS);
	static final short[] DFA129_accept = DFA.unpackEncodedString(DFA129_acceptS);
	static final short[] DFA129_special = DFA.unpackEncodedString(DFA129_specialS);
	static final short[][] DFA129_transition;

	static {
		int numStates = DFA129_transitionS.length;
		DFA129_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA129_transition[i] = DFA.unpackEncodedString(DFA129_transitionS[i]);
		}
	}

	protected class DFA129 extends DFA {

		public DFA129(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 129;
			this.eot = DFA129_eot;
			this.eof = DFA129_eof;
			this.min = DFA129_min;
			this.max = DFA129_max;
			this.accept = DFA129_accept;
			this.special = DFA129_special;
			this.transition = DFA129_transition;
		}
		@Override
		public String getDescription() {
			return "()* loopback of 1040:19: ( blockStatement )*";
		}
	}

	static final String DFA135_eotS =
		"\u0093\uffff";
	static final String DFA135_eofS =
		"\u0093\uffff";
	static final String DFA135_minS =
		"\1\5\4\21\26\uffff\5\21\1\5\1\21\1\5\1\53\30\uffff\1\74\1\53\1\uffff\21"+
		"\0\2\uffff\3\0\24\uffff\1\0\6\uffff\1\0\34\uffff\1\0\5\uffff";
	static final String DFA135_maxS =
		"\1\176\1\151\1\21\1\174\1\73\26\uffff\2\73\1\151\1\21\1\151\1\176\1\165"+
		"\1\176\1\73\30\uffff\1\74\1\73\1\uffff\21\0\2\uffff\3\0\24\uffff\1\0\6"+
		"\uffff\1\0\34\uffff\1\0\5\uffff";
	static final String DFA135_acceptS =
		"\5\uffff\1\2\u0082\uffff\1\1\12\uffff";
	static final String DFA135_specialS =
		"\77\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15"+
		"\1\16\1\17\1\20\2\uffff\1\21\1\22\1\23\24\uffff\1\24\6\uffff\1\25\34\uffff"+
		"\1\26\5\uffff}>";
	static final String[] DFA135_transitionS = {
			"\2\5\7\uffff\1\5\1\uffff\1\5\1\3\6\uffff\2\5\3\uffff\1\5\6\uffff\1\5"+
			"\3\uffff\2\5\2\uffff\2\5\6\uffff\2\5\4\uffff\1\2\6\uffff\1\4\1\uffff"+
			"\1\4\2\uffff\1\5\1\4\5\uffff\1\4\1\uffff\2\5\1\uffff\1\5\1\1\1\uffff"+
			"\1\4\6\uffff\1\4\1\uffff\1\4\2\uffff\2\5\6\uffff\1\4\2\uffff\1\5\2\uffff"+
			"\1\5\3\uffff\1\5\2\uffff\1\5\7\uffff\1\5",
			"\1\33\50\uffff\1\36\6\uffff\1\34\1\uffff\1\34\3\uffff\1\34\5\uffff\1"+
			"\34\5\uffff\1\35\1\uffff\1\34\6\uffff\1\34\1\uffff\1\34\12\uffff\1\34",
			"\1\37",
			"\1\43\14\uffff\7\5\1\uffff\11\5\1\41\1\uffff\2\5\1\uffff\1\5\1\40\4"+
			"\5\1\uffff\1\42\1\uffff\2\5\34\uffff\1\5\36\uffff\3\5",
			"\1\75\35\uffff\1\5\13\uffff\1\74",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\102\35\uffff\1\100\5\uffff\1\77\5\uffff\1\101",
			"\1\104\51\uffff\1\103",
			"\1\105\50\uffff\1\110\6\uffff\1\106\1\uffff\1\106\3\uffff\1\106\5\uffff"+
			"\1\106\5\uffff\1\107\1\uffff\1\106\6\uffff\1\106\1\uffff\1\106\12\uffff"+
			"\1\106",
			"\1\111",
			"\1\114\22\uffff\1\113\12\uffff\1\112\12\uffff\1\117\6\uffff\1\115\1"+
			"\uffff\1\115\3\uffff\1\115\5\uffff\1\115\5\uffff\1\116\1\uffff\1\115"+
			"\6\uffff\1\115\1\uffff\1\115\12\uffff\1\115",
			"\2\5\7\uffff\1\5\1\uffff\1\5\1\122\6\uffff\2\5\3\uffff\1\5\6\uffff\1"+
			"\5\3\uffff\2\5\2\uffff\2\5\7\uffff\2\5\2\uffff\1\124\7\uffff\1\123\1"+
			"\uffff\1\123\2\uffff\1\5\1\123\5\uffff\1\123\1\uffff\2\5\1\uffff\1\5"+
			"\2\uffff\1\123\6\uffff\1\123\1\uffff\1\123\2\uffff\2\5\6\uffff\1\123"+
			"\2\uffff\1\5\2\uffff\1\5\3\uffff\1\5\2\uffff\1\5\7\uffff\1\5",
			"\1\151\43\uffff\1\5\22\uffff\1\5\2\uffff\1\5\16\uffff\1\5\4\uffff\1"+
			"\5\1\uffff\1\5\5\uffff\1\5\4\uffff\1\5\2\uffff\1\5\5\uffff\1\5",
			"\2\5\7\uffff\1\5\1\uffff\2\5\6\uffff\2\5\3\uffff\1\5\6\uffff\1\5\3\uffff"+
			"\2\5\2\uffff\2\5\7\uffff\1\5\6\uffff\1\160\4\uffff\1\5\1\uffff\1\5\2"+
			"\uffff\2\5\5\uffff\1\5\1\uffff\2\5\1\uffff\1\5\2\uffff\1\5\6\uffff\1"+
			"\5\1\uffff\1\5\2\uffff\2\5\6\uffff\1\5\2\uffff\1\5\2\uffff\1\5\3\uffff"+
			"\1\5\2\uffff\1\5\7\uffff\1\5",
			"\1\5\7\uffff\1\u0088\1\5\1\uffff\1\5\4\uffff\1\5",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\u008d",
			"\1\5\7\uffff\1\u0088\1\5\1\uffff\1\5\4\uffff\1\5",
			"",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"",
			"",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\uffff",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\uffff",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\uffff",
			"",
			"",
			"",
			"",
			""
	};

	static final short[] DFA135_eot = DFA.unpackEncodedString(DFA135_eotS);
	static final short[] DFA135_eof = DFA.unpackEncodedString(DFA135_eofS);
	static final char[] DFA135_min = DFA.unpackEncodedStringToUnsignedChars(DFA135_minS);
	static final char[] DFA135_max = DFA.unpackEncodedStringToUnsignedChars(DFA135_maxS);
	static final short[] DFA135_accept = DFA.unpackEncodedString(DFA135_acceptS);
	static final short[] DFA135_special = DFA.unpackEncodedString(DFA135_specialS);
	static final short[][] DFA135_transition;

	static {
		int numStates = DFA135_transitionS.length;
		DFA135_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA135_transition[i] = DFA.unpackEncodedString(DFA135_transitionS[i]);
		}
	}

	protected class DFA135 extends DFA {

		public DFA135(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 135;
			this.eot = DFA135_eot;
			this.eof = DFA135_eof;
			this.min = DFA135_min;
			this.max = DFA135_max;
			this.accept = DFA135_accept;
			this.special = DFA135_special;
			this.transition = DFA135_transition;
		}
		@Override
		public String getDescription() {
			return "1053:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA135_63 = input.LA(1);
						 
						int index135_63 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_63);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA135_64 = input.LA(1);
						 
						int index135_64 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_64);
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA135_65 = input.LA(1);
						 
						int index135_65 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_65);
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA135_66 = input.LA(1);
						 
						int index135_66 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_66);
						if ( s>=0 ) return s;
						break;

					case 4 : 
						int LA135_67 = input.LA(1);
						 
						int index135_67 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_67);
						if ( s>=0 ) return s;
						break;

					case 5 : 
						int LA135_68 = input.LA(1);
						 
						int index135_68 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_68);
						if ( s>=0 ) return s;
						break;

					case 6 : 
						int LA135_69 = input.LA(1);
						 
						int index135_69 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_69);
						if ( s>=0 ) return s;
						break;

					case 7 : 
						int LA135_70 = input.LA(1);
						 
						int index135_70 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_70);
						if ( s>=0 ) return s;
						break;

					case 8 : 
						int LA135_71 = input.LA(1);
						 
						int index135_71 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_71);
						if ( s>=0 ) return s;
						break;

					case 9 : 
						int LA135_72 = input.LA(1);
						 
						int index135_72 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_72);
						if ( s>=0 ) return s;
						break;

					case 10 : 
						int LA135_73 = input.LA(1);
						 
						int index135_73 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_73);
						if ( s>=0 ) return s;
						break;

					case 11 : 
						int LA135_74 = input.LA(1);
						 
						int index135_74 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_74);
						if ( s>=0 ) return s;
						break;

					case 12 : 
						int LA135_75 = input.LA(1);
						 
						int index135_75 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_75);
						if ( s>=0 ) return s;
						break;

					case 13 : 
						int LA135_76 = input.LA(1);
						 
						int index135_76 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_76);
						if ( s>=0 ) return s;
						break;

					case 14 : 
						int LA135_77 = input.LA(1);
						 
						int index135_77 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_77);
						if ( s>=0 ) return s;
						break;

					case 15 : 
						int LA135_78 = input.LA(1);
						 
						int index135_78 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_78);
						if ( s>=0 ) return s;
						break;

					case 16 : 
						int LA135_79 = input.LA(1);
						 
						int index135_79 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_79);
						if ( s>=0 ) return s;
						break;

					case 17 : 
						int LA135_82 = input.LA(1);
						 
						int index135_82 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_82);
						if ( s>=0 ) return s;
						break;

					case 18 : 
						int LA135_83 = input.LA(1);
						 
						int index135_83 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_83);
						if ( s>=0 ) return s;
						break;

					case 19 : 
						int LA135_84 = input.LA(1);
						 
						int index135_84 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_84);
						if ( s>=0 ) return s;
						break;

					case 20 : 
						int LA135_105 = input.LA(1);
						 
						int index135_105 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_105);
						if ( s>=0 ) return s;
						break;

					case 21 : 
						int LA135_112 = input.LA(1);
						 
						int index135_112 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_112);
						if ( s>=0 ) return s;
						break;

					case 22 : 
						int LA135_141 = input.LA(1);
						 
						int index135_141 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred193_Java()) ) {s = 136;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index135_141);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 135, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	static final String DFA141_eotS =
		"\u01b6\uffff";
	static final String DFA141_eofS =
		"\u01b6\uffff";
	static final String DFA141_minS =
		"\10\5\1\21\1\0\1\44\3\73\6\0\1\21\1\0\2\57\u0097\0\42\uffff\5\0\u00ba"+
		"\uffff\3\0\40\uffff\3\0";
	static final String DFA141_maxS =
		"\3\176\2\166\3\176\1\151\1\0\1\57\3\73\6\0\1\151\1\0\1\73\1\57\u0097\0"+
		"\42\uffff\5\0\u00ba\uffff\3\0\40\uffff\3\0";
	static final String DFA141_acceptS =
		"\u00cf\uffff\1\1\1\2\u00e5\uffff";
	static final String DFA141_specialS =
		"\11\uffff\1\0\4\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\uffff\1\7\2\uffff\1\10"+
		"\1\11\1\12\1\13\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26"+
		"\1\27\1\30\1\31\1\32\1\33\1\34\1\35\1\36\1\37\1\40\1\41\1\42\1\43\1\44"+
		"\1\45\1\46\1\47\1\50\1\51\1\52\1\53\1\54\1\55\1\56\1\57\1\60\1\61\1\62"+
		"\1\63\1\64\1\65\1\66\1\67\1\70\1\71\1\72\1\73\1\74\1\75\1\76\1\77\1\100"+
		"\1\101\1\102\1\103\1\104\1\105\1\106\1\107\1\110\1\111\1\112\1\113\1\114"+
		"\1\115\1\116\1\117\1\120\1\121\1\122\1\123\1\124\1\125\1\126\1\127\1\130"+
		"\1\131\1\132\1\133\1\134\1\135\1\136\1\137\1\140\1\141\1\142\1\143\1\144"+
		"\1\145\1\146\1\147\1\150\1\151\1\152\1\153\1\154\1\155\1\156\1\157\1\160"+
		"\1\161\1\162\1\163\1\164\1\165\1\166\1\167\1\170\1\171\1\172\1\173\1\174"+
		"\1\175\1\176\1\177\1\u0080\1\u0081\1\u0082\1\u0083\1\u0084\1\u0085\1\u0086"+
		"\1\u0087\1\u0088\1\u0089\1\u008a\1\u008b\1\u008c\1\u008d\1\u008e\1\u008f"+
		"\1\u0090\1\u0091\1\u0092\1\u0093\1\u0094\1\u0095\1\u0096\1\u0097\1\u0098"+
		"\1\u0099\1\u009a\1\u009b\1\u009c\1\u009d\1\u009e\42\uffff\1\u009f\1\u00a0"+
		"\1\u00a1\1\u00a2\1\u00a3\u00ba\uffff\1\u00a4\1\u00a5\1\u00a6\40\uffff"+
		"\1\u00a7\1\u00a8\1\u00a9}>";
	static final String[] DFA141_transitionS = {
			"\1\20\1\16\7\uffff\1\17\1\uffff\1\16\1\25\6\uffff\1\16\1\21\3\uffff\1"+
			"\6\6\uffff\1\7\3\uffff\1\1\1\3\2\uffff\1\2\1\4\7\uffff\1\10\13\uffff"+
			"\1\26\1\uffff\1\26\2\uffff\1\15\1\26\5\uffff\1\26\1\uffff\1\14\1\13\1"+
			"\uffff\1\22\2\uffff\1\26\6\uffff\1\26\1\uffff\1\26\2\uffff\1\24\1\23"+
			"\6\uffff\1\26\2\uffff\1\12\2\uffff\1\11\3\uffff\1\22\2\uffff\1\27\7\uffff"+
			"\1\5",
			"\1\47\1\45\7\uffff\1\46\1\uffff\1\45\1\54\6\uffff\1\45\1\50\3\uffff"+
			"\1\35\6\uffff\1\36\3\uffff\1\30\1\32\2\uffff\1\31\1\33\7\uffff\1\37\13"+
			"\uffff\1\55\1\uffff\1\55\2\uffff\1\44\1\55\5\uffff\1\55\1\uffff\1\43"+
			"\1\42\1\uffff\1\51\2\uffff\1\55\6\uffff\1\55\1\uffff\1\55\2\uffff\1\53"+
			"\1\52\6\uffff\1\55\2\uffff\1\41\2\uffff\1\40\3\uffff\1\51\2\uffff\1\56"+
			"\7\uffff\1\34",
			"\1\76\1\74\7\uffff\1\75\1\uffff\1\74\1\103\6\uffff\1\74\1\77\3\uffff"+
			"\1\64\6\uffff\1\65\3\uffff\1\57\1\61\2\uffff\1\60\1\62\7\uffff\1\66\13"+
			"\uffff\1\104\1\uffff\1\104\2\uffff\1\73\1\104\5\uffff\1\104\1\uffff\1"+
			"\72\1\71\1\uffff\1\100\2\uffff\1\104\6\uffff\1\104\1\uffff\1\104\2\uffff"+
			"\1\102\1\101\6\uffff\1\104\2\uffff\1\70\2\uffff\1\67\3\uffff\1\100\2"+
			"\uffff\1\105\7\uffff\1\63",
			"\1\117\1\115\7\uffff\1\116\1\uffff\1\115\1\124\6\uffff\1\115\1\120\12"+
			"\uffff\1\106\20\uffff\1\107\13\uffff\1\125\1\uffff\1\125\2\uffff\1\114"+
			"\1\125\5\uffff\1\125\1\uffff\1\113\1\112\1\uffff\1\121\2\uffff\1\125"+
			"\6\uffff\1\125\1\uffff\1\125\2\uffff\1\123\1\122\6\uffff\1\125\2\uffff"+
			"\1\111\2\uffff\1\110\3\uffff\1\121\2\uffff\1\126",
			"\1\140\1\136\7\uffff\1\137\1\uffff\1\136\1\145\6\uffff\1\136\1\141\12"+
			"\uffff\1\127\20\uffff\1\130\13\uffff\1\146\1\uffff\1\146\2\uffff\1\135"+
			"\1\146\5\uffff\1\146\1\uffff\1\134\1\133\1\uffff\1\142\2\uffff\1\146"+
			"\6\uffff\1\146\1\uffff\1\146\2\uffff\1\144\1\143\6\uffff\1\146\2\uffff"+
			"\1\132\2\uffff\1\131\3\uffff\1\142\2\uffff\1\147",
			"\1\167\1\165\7\uffff\1\166\1\uffff\1\165\1\174\6\uffff\1\165\1\170\3"+
			"\uffff\1\155\6\uffff\1\156\3\uffff\1\150\1\152\2\uffff\1\151\1\153\7"+
			"\uffff\1\157\13\uffff\1\175\1\uffff\1\175\2\uffff\1\164\1\175\5\uffff"+
			"\1\175\1\uffff\1\163\1\162\1\uffff\1\171\2\uffff\1\175\6\uffff\1\175"+
			"\1\uffff\1\175\2\uffff\1\173\1\172\6\uffff\1\175\2\uffff\1\161\2\uffff"+
			"\1\160\3\uffff\1\171\2\uffff\1\176\7\uffff\1\154",
			"\1\u008e\1\u008c\7\uffff\1\u008d\1\uffff\1\u008c\1\u0093\6\uffff\1\u008c"+
			"\1\u008f\3\uffff\1\u0084\6\uffff\1\u0085\3\uffff\1\177\1\u0081\2\uffff"+
			"\1\u0080\1\u0082\7\uffff\1\u0086\13\uffff\1\u0094\1\uffff\1\u0094\2\uffff"+
			"\1\u008b\1\u0094\5\uffff\1\u0094\1\uffff\1\u008a\1\u0089\1\uffff\1\u0090"+
			"\2\uffff\1\u0094\6\uffff\1\u0094\1\uffff\1\u0094\2\uffff\1\u0092\1\u0091"+
			"\6\uffff\1\u0094\2\uffff\1\u0088\2\uffff\1\u0087\3\uffff\1\u0090\2\uffff"+
			"\1\u0095\7\uffff\1\u0083",
			"\1\u00a7\1\u00a5\7\uffff\1\u00a6\1\uffff\1\u00a5\1\u0097\6\uffff\1\u00a5"+
			"\1\u00a8\3\uffff\1\u009d\6\uffff\1\u009e\3\uffff\1\u0098\1\u009a\2\uffff"+
			"\1\u0099\1\u009b\7\uffff\1\u009f\13\uffff\1\u0096\1\uffff\1\u0096\2\uffff"+
			"\1\u00a4\1\u0096\5\uffff\1\u0096\1\uffff\1\u00a3\1\u00a2\1\uffff\1\u00a9"+
			"\2\uffff\1\u0096\6\uffff\1\u0096\1\uffff\1\u0096\2\uffff\1\u00ab\1\u00aa"+
			"\6\uffff\1\u0096\2\uffff\1\u00a1\2\uffff\1\u00a0\3\uffff\1\u00a9\2\uffff"+
			"\1\u00ac\7\uffff\1\u009c",
			"\1\u00ad\57\uffff\1\u00ae\1\uffff\1\u00ae\3\uffff\1\u00ae\5\uffff\1"+
			"\u00ae\7\uffff\1\u00ae\6\uffff\1\u00ae\1\uffff\1\u00ae\12\uffff\1\u00ae",
			"\1\uffff",
			"\1\u00d1\12\uffff\1\u00d2",
			"\1\u00d3",
			"\1\u00d4",
			"\1\u00d5",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\u0191\43\uffff\1\u0190\13\uffff\1\u0192\1\uffff\1\u0192\3\uffff\1"+
			"\u0192\5\uffff\1\u0192\7\uffff\1\u0192\6\uffff\1\u0192\1\uffff\1\u0192"+
			"\12\uffff\1\u0192",
			"\1\uffff",
			"\1\u01b4\13\uffff\1\u01b3",
			"\1\u01b5",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff"
	};

	static final short[] DFA141_eot = DFA.unpackEncodedString(DFA141_eotS);
	static final short[] DFA141_eof = DFA.unpackEncodedString(DFA141_eofS);
	static final char[] DFA141_min = DFA.unpackEncodedStringToUnsignedChars(DFA141_minS);
	static final char[] DFA141_max = DFA.unpackEncodedStringToUnsignedChars(DFA141_maxS);
	static final short[] DFA141_accept = DFA.unpackEncodedString(DFA141_acceptS);
	static final short[] DFA141_special = DFA.unpackEncodedString(DFA141_specialS);
	static final short[][] DFA141_transition;

	static {
		int numStates = DFA141_transitionS.length;
		DFA141_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA141_transition[i] = DFA.unpackEncodedString(DFA141_transitionS[i]);
		}
	}

	protected class DFA141 extends DFA {

		public DFA141(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 141;
			this.eot = DFA141_eot;
			this.eof = DFA141_eof;
			this.min = DFA141_min;
			this.max = DFA141_max;
			this.accept = DFA141_accept;
			this.special = DFA141_special;
			this.transition = DFA141_transition;
		}
		@Override
		public String getDescription() {
			return "1131:1: expression : ( assignmentExpression | conditionalExpression ( assignmentOperator expression )? );";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA141_9 = input.LA(1);
						 
						int index141_9 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_9);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA141_14 = input.LA(1);
						 
						int index141_14 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_14);
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA141_15 = input.LA(1);
						 
						int index141_15 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_15);
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA141_16 = input.LA(1);
						 
						int index141_16 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_16);
						if ( s>=0 ) return s;
						break;

					case 4 : 
						int LA141_17 = input.LA(1);
						 
						int index141_17 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_17);
						if ( s>=0 ) return s;
						break;

					case 5 : 
						int LA141_18 = input.LA(1);
						 
						int index141_18 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_18);
						if ( s>=0 ) return s;
						break;

					case 6 : 
						int LA141_19 = input.LA(1);
						 
						int index141_19 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_19);
						if ( s>=0 ) return s;
						break;

					case 7 : 
						int LA141_21 = input.LA(1);
						 
						int index141_21 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_21);
						if ( s>=0 ) return s;
						break;

					case 8 : 
						int LA141_24 = input.LA(1);
						 
						int index141_24 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_24);
						if ( s>=0 ) return s;
						break;

					case 9 : 
						int LA141_25 = input.LA(1);
						 
						int index141_25 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_25);
						if ( s>=0 ) return s;
						break;

					case 10 : 
						int LA141_26 = input.LA(1);
						 
						int index141_26 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_26);
						if ( s>=0 ) return s;
						break;

					case 11 : 
						int LA141_27 = input.LA(1);
						 
						int index141_27 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_27);
						if ( s>=0 ) return s;
						break;

					case 12 : 
						int LA141_28 = input.LA(1);
						 
						int index141_28 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_28);
						if ( s>=0 ) return s;
						break;

					case 13 : 
						int LA141_29 = input.LA(1);
						 
						int index141_29 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_29);
						if ( s>=0 ) return s;
						break;

					case 14 : 
						int LA141_30 = input.LA(1);
						 
						int index141_30 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_30);
						if ( s>=0 ) return s;
						break;

					case 15 : 
						int LA141_31 = input.LA(1);
						 
						int index141_31 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_31);
						if ( s>=0 ) return s;
						break;

					case 16 : 
						int LA141_32 = input.LA(1);
						 
						int index141_32 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_32);
						if ( s>=0 ) return s;
						break;

					case 17 : 
						int LA141_33 = input.LA(1);
						 
						int index141_33 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_33);
						if ( s>=0 ) return s;
						break;

					case 18 : 
						int LA141_34 = input.LA(1);
						 
						int index141_34 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_34);
						if ( s>=0 ) return s;
						break;

					case 19 : 
						int LA141_35 = input.LA(1);
						 
						int index141_35 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_35);
						if ( s>=0 ) return s;
						break;

					case 20 : 
						int LA141_36 = input.LA(1);
						 
						int index141_36 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_36);
						if ( s>=0 ) return s;
						break;

					case 21 : 
						int LA141_37 = input.LA(1);
						 
						int index141_37 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_37);
						if ( s>=0 ) return s;
						break;

					case 22 : 
						int LA141_38 = input.LA(1);
						 
						int index141_38 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_38);
						if ( s>=0 ) return s;
						break;

					case 23 : 
						int LA141_39 = input.LA(1);
						 
						int index141_39 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_39);
						if ( s>=0 ) return s;
						break;

					case 24 : 
						int LA141_40 = input.LA(1);
						 
						int index141_40 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_40);
						if ( s>=0 ) return s;
						break;

					case 25 : 
						int LA141_41 = input.LA(1);
						 
						int index141_41 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_41);
						if ( s>=0 ) return s;
						break;

					case 26 : 
						int LA141_42 = input.LA(1);
						 
						int index141_42 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_42);
						if ( s>=0 ) return s;
						break;

					case 27 : 
						int LA141_43 = input.LA(1);
						 
						int index141_43 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_43);
						if ( s>=0 ) return s;
						break;

					case 28 : 
						int LA141_44 = input.LA(1);
						 
						int index141_44 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_44);
						if ( s>=0 ) return s;
						break;

					case 29 : 
						int LA141_45 = input.LA(1);
						 
						int index141_45 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_45);
						if ( s>=0 ) return s;
						break;

					case 30 : 
						int LA141_46 = input.LA(1);
						 
						int index141_46 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_46);
						if ( s>=0 ) return s;
						break;

					case 31 : 
						int LA141_47 = input.LA(1);
						 
						int index141_47 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_47);
						if ( s>=0 ) return s;
						break;

					case 32 : 
						int LA141_48 = input.LA(1);
						 
						int index141_48 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_48);
						if ( s>=0 ) return s;
						break;

					case 33 : 
						int LA141_49 = input.LA(1);
						 
						int index141_49 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_49);
						if ( s>=0 ) return s;
						break;

					case 34 : 
						int LA141_50 = input.LA(1);
						 
						int index141_50 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_50);
						if ( s>=0 ) return s;
						break;

					case 35 : 
						int LA141_51 = input.LA(1);
						 
						int index141_51 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_51);
						if ( s>=0 ) return s;
						break;

					case 36 : 
						int LA141_52 = input.LA(1);
						 
						int index141_52 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_52);
						if ( s>=0 ) return s;
						break;

					case 37 : 
						int LA141_53 = input.LA(1);
						 
						int index141_53 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_53);
						if ( s>=0 ) return s;
						break;

					case 38 : 
						int LA141_54 = input.LA(1);
						 
						int index141_54 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_54);
						if ( s>=0 ) return s;
						break;

					case 39 : 
						int LA141_55 = input.LA(1);
						 
						int index141_55 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_55);
						if ( s>=0 ) return s;
						break;

					case 40 : 
						int LA141_56 = input.LA(1);
						 
						int index141_56 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_56);
						if ( s>=0 ) return s;
						break;

					case 41 : 
						int LA141_57 = input.LA(1);
						 
						int index141_57 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_57);
						if ( s>=0 ) return s;
						break;

					case 42 : 
						int LA141_58 = input.LA(1);
						 
						int index141_58 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_58);
						if ( s>=0 ) return s;
						break;

					case 43 : 
						int LA141_59 = input.LA(1);
						 
						int index141_59 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_59);
						if ( s>=0 ) return s;
						break;

					case 44 : 
						int LA141_60 = input.LA(1);
						 
						int index141_60 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_60);
						if ( s>=0 ) return s;
						break;

					case 45 : 
						int LA141_61 = input.LA(1);
						 
						int index141_61 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_61);
						if ( s>=0 ) return s;
						break;

					case 46 : 
						int LA141_62 = input.LA(1);
						 
						int index141_62 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_62);
						if ( s>=0 ) return s;
						break;

					case 47 : 
						int LA141_63 = input.LA(1);
						 
						int index141_63 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_63);
						if ( s>=0 ) return s;
						break;

					case 48 : 
						int LA141_64 = input.LA(1);
						 
						int index141_64 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_64);
						if ( s>=0 ) return s;
						break;

					case 49 : 
						int LA141_65 = input.LA(1);
						 
						int index141_65 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_65);
						if ( s>=0 ) return s;
						break;

					case 50 : 
						int LA141_66 = input.LA(1);
						 
						int index141_66 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_66);
						if ( s>=0 ) return s;
						break;

					case 51 : 
						int LA141_67 = input.LA(1);
						 
						int index141_67 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_67);
						if ( s>=0 ) return s;
						break;

					case 52 : 
						int LA141_68 = input.LA(1);
						 
						int index141_68 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_68);
						if ( s>=0 ) return s;
						break;

					case 53 : 
						int LA141_69 = input.LA(1);
						 
						int index141_69 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_69);
						if ( s>=0 ) return s;
						break;

					case 54 : 
						int LA141_70 = input.LA(1);
						 
						int index141_70 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_70);
						if ( s>=0 ) return s;
						break;

					case 55 : 
						int LA141_71 = input.LA(1);
						 
						int index141_71 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_71);
						if ( s>=0 ) return s;
						break;

					case 56 : 
						int LA141_72 = input.LA(1);
						 
						int index141_72 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_72);
						if ( s>=0 ) return s;
						break;

					case 57 : 
						int LA141_73 = input.LA(1);
						 
						int index141_73 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_73);
						if ( s>=0 ) return s;
						break;

					case 58 : 
						int LA141_74 = input.LA(1);
						 
						int index141_74 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_74);
						if ( s>=0 ) return s;
						break;

					case 59 : 
						int LA141_75 = input.LA(1);
						 
						int index141_75 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_75);
						if ( s>=0 ) return s;
						break;

					case 60 : 
						int LA141_76 = input.LA(1);
						 
						int index141_76 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_76);
						if ( s>=0 ) return s;
						break;

					case 61 : 
						int LA141_77 = input.LA(1);
						 
						int index141_77 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_77);
						if ( s>=0 ) return s;
						break;

					case 62 : 
						int LA141_78 = input.LA(1);
						 
						int index141_78 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_78);
						if ( s>=0 ) return s;
						break;

					case 63 : 
						int LA141_79 = input.LA(1);
						 
						int index141_79 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_79);
						if ( s>=0 ) return s;
						break;

					case 64 : 
						int LA141_80 = input.LA(1);
						 
						int index141_80 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_80);
						if ( s>=0 ) return s;
						break;

					case 65 : 
						int LA141_81 = input.LA(1);
						 
						int index141_81 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_81);
						if ( s>=0 ) return s;
						break;

					case 66 : 
						int LA141_82 = input.LA(1);
						 
						int index141_82 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_82);
						if ( s>=0 ) return s;
						break;

					case 67 : 
						int LA141_83 = input.LA(1);
						 
						int index141_83 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_83);
						if ( s>=0 ) return s;
						break;

					case 68 : 
						int LA141_84 = input.LA(1);
						 
						int index141_84 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_84);
						if ( s>=0 ) return s;
						break;

					case 69 : 
						int LA141_85 = input.LA(1);
						 
						int index141_85 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_85);
						if ( s>=0 ) return s;
						break;

					case 70 : 
						int LA141_86 = input.LA(1);
						 
						int index141_86 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_86);
						if ( s>=0 ) return s;
						break;

					case 71 : 
						int LA141_87 = input.LA(1);
						 
						int index141_87 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_87);
						if ( s>=0 ) return s;
						break;

					case 72 : 
						int LA141_88 = input.LA(1);
						 
						int index141_88 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_88);
						if ( s>=0 ) return s;
						break;

					case 73 : 
						int LA141_89 = input.LA(1);
						 
						int index141_89 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_89);
						if ( s>=0 ) return s;
						break;

					case 74 : 
						int LA141_90 = input.LA(1);
						 
						int index141_90 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_90);
						if ( s>=0 ) return s;
						break;

					case 75 : 
						int LA141_91 = input.LA(1);
						 
						int index141_91 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_91);
						if ( s>=0 ) return s;
						break;

					case 76 : 
						int LA141_92 = input.LA(1);
						 
						int index141_92 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_92);
						if ( s>=0 ) return s;
						break;

					case 77 : 
						int LA141_93 = input.LA(1);
						 
						int index141_93 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_93);
						if ( s>=0 ) return s;
						break;

					case 78 : 
						int LA141_94 = input.LA(1);
						 
						int index141_94 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_94);
						if ( s>=0 ) return s;
						break;

					case 79 : 
						int LA141_95 = input.LA(1);
						 
						int index141_95 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_95);
						if ( s>=0 ) return s;
						break;

					case 80 : 
						int LA141_96 = input.LA(1);
						 
						int index141_96 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_96);
						if ( s>=0 ) return s;
						break;

					case 81 : 
						int LA141_97 = input.LA(1);
						 
						int index141_97 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_97);
						if ( s>=0 ) return s;
						break;

					case 82 : 
						int LA141_98 = input.LA(1);
						 
						int index141_98 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_98);
						if ( s>=0 ) return s;
						break;

					case 83 : 
						int LA141_99 = input.LA(1);
						 
						int index141_99 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_99);
						if ( s>=0 ) return s;
						break;

					case 84 : 
						int LA141_100 = input.LA(1);
						 
						int index141_100 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_100);
						if ( s>=0 ) return s;
						break;

					case 85 : 
						int LA141_101 = input.LA(1);
						 
						int index141_101 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_101);
						if ( s>=0 ) return s;
						break;

					case 86 : 
						int LA141_102 = input.LA(1);
						 
						int index141_102 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_102);
						if ( s>=0 ) return s;
						break;

					case 87 : 
						int LA141_103 = input.LA(1);
						 
						int index141_103 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_103);
						if ( s>=0 ) return s;
						break;

					case 88 : 
						int LA141_104 = input.LA(1);
						 
						int index141_104 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_104);
						if ( s>=0 ) return s;
						break;

					case 89 : 
						int LA141_105 = input.LA(1);
						 
						int index141_105 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_105);
						if ( s>=0 ) return s;
						break;

					case 90 : 
						int LA141_106 = input.LA(1);
						 
						int index141_106 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_106);
						if ( s>=0 ) return s;
						break;

					case 91 : 
						int LA141_107 = input.LA(1);
						 
						int index141_107 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_107);
						if ( s>=0 ) return s;
						break;

					case 92 : 
						int LA141_108 = input.LA(1);
						 
						int index141_108 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_108);
						if ( s>=0 ) return s;
						break;

					case 93 : 
						int LA141_109 = input.LA(1);
						 
						int index141_109 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_109);
						if ( s>=0 ) return s;
						break;

					case 94 : 
						int LA141_110 = input.LA(1);
						 
						int index141_110 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_110);
						if ( s>=0 ) return s;
						break;

					case 95 : 
						int LA141_111 = input.LA(1);
						 
						int index141_111 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_111);
						if ( s>=0 ) return s;
						break;

					case 96 : 
						int LA141_112 = input.LA(1);
						 
						int index141_112 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_112);
						if ( s>=0 ) return s;
						break;

					case 97 : 
						int LA141_113 = input.LA(1);
						 
						int index141_113 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_113);
						if ( s>=0 ) return s;
						break;

					case 98 : 
						int LA141_114 = input.LA(1);
						 
						int index141_114 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_114);
						if ( s>=0 ) return s;
						break;

					case 99 : 
						int LA141_115 = input.LA(1);
						 
						int index141_115 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_115);
						if ( s>=0 ) return s;
						break;

					case 100 : 
						int LA141_116 = input.LA(1);
						 
						int index141_116 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_116);
						if ( s>=0 ) return s;
						break;

					case 101 : 
						int LA141_117 = input.LA(1);
						 
						int index141_117 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_117);
						if ( s>=0 ) return s;
						break;

					case 102 : 
						int LA141_118 = input.LA(1);
						 
						int index141_118 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_118);
						if ( s>=0 ) return s;
						break;

					case 103 : 
						int LA141_119 = input.LA(1);
						 
						int index141_119 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_119);
						if ( s>=0 ) return s;
						break;

					case 104 : 
						int LA141_120 = input.LA(1);
						 
						int index141_120 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_120);
						if ( s>=0 ) return s;
						break;

					case 105 : 
						int LA141_121 = input.LA(1);
						 
						int index141_121 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_121);
						if ( s>=0 ) return s;
						break;

					case 106 : 
						int LA141_122 = input.LA(1);
						 
						int index141_122 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_122);
						if ( s>=0 ) return s;
						break;

					case 107 : 
						int LA141_123 = input.LA(1);
						 
						int index141_123 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_123);
						if ( s>=0 ) return s;
						break;

					case 108 : 
						int LA141_124 = input.LA(1);
						 
						int index141_124 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_124);
						if ( s>=0 ) return s;
						break;

					case 109 : 
						int LA141_125 = input.LA(1);
						 
						int index141_125 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_125);
						if ( s>=0 ) return s;
						break;

					case 110 : 
						int LA141_126 = input.LA(1);
						 
						int index141_126 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_126);
						if ( s>=0 ) return s;
						break;

					case 111 : 
						int LA141_127 = input.LA(1);
						 
						int index141_127 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_127);
						if ( s>=0 ) return s;
						break;

					case 112 : 
						int LA141_128 = input.LA(1);
						 
						int index141_128 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_128);
						if ( s>=0 ) return s;
						break;

					case 113 : 
						int LA141_129 = input.LA(1);
						 
						int index141_129 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_129);
						if ( s>=0 ) return s;
						break;

					case 114 : 
						int LA141_130 = input.LA(1);
						 
						int index141_130 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_130);
						if ( s>=0 ) return s;
						break;

					case 115 : 
						int LA141_131 = input.LA(1);
						 
						int index141_131 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_131);
						if ( s>=0 ) return s;
						break;

					case 116 : 
						int LA141_132 = input.LA(1);
						 
						int index141_132 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_132);
						if ( s>=0 ) return s;
						break;

					case 117 : 
						int LA141_133 = input.LA(1);
						 
						int index141_133 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_133);
						if ( s>=0 ) return s;
						break;

					case 118 : 
						int LA141_134 = input.LA(1);
						 
						int index141_134 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_134);
						if ( s>=0 ) return s;
						break;

					case 119 : 
						int LA141_135 = input.LA(1);
						 
						int index141_135 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_135);
						if ( s>=0 ) return s;
						break;

					case 120 : 
						int LA141_136 = input.LA(1);
						 
						int index141_136 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_136);
						if ( s>=0 ) return s;
						break;

					case 121 : 
						int LA141_137 = input.LA(1);
						 
						int index141_137 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_137);
						if ( s>=0 ) return s;
						break;

					case 122 : 
						int LA141_138 = input.LA(1);
						 
						int index141_138 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_138);
						if ( s>=0 ) return s;
						break;

					case 123 : 
						int LA141_139 = input.LA(1);
						 
						int index141_139 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_139);
						if ( s>=0 ) return s;
						break;

					case 124 : 
						int LA141_140 = input.LA(1);
						 
						int index141_140 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_140);
						if ( s>=0 ) return s;
						break;

					case 125 : 
						int LA141_141 = input.LA(1);
						 
						int index141_141 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_141);
						if ( s>=0 ) return s;
						break;

					case 126 : 
						int LA141_142 = input.LA(1);
						 
						int index141_142 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_142);
						if ( s>=0 ) return s;
						break;

					case 127 : 
						int LA141_143 = input.LA(1);
						 
						int index141_143 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_143);
						if ( s>=0 ) return s;
						break;

					case 128 : 
						int LA141_144 = input.LA(1);
						 
						int index141_144 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_144);
						if ( s>=0 ) return s;
						break;

					case 129 : 
						int LA141_145 = input.LA(1);
						 
						int index141_145 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_145);
						if ( s>=0 ) return s;
						break;

					case 130 : 
						int LA141_146 = input.LA(1);
						 
						int index141_146 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_146);
						if ( s>=0 ) return s;
						break;

					case 131 : 
						int LA141_147 = input.LA(1);
						 
						int index141_147 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_147);
						if ( s>=0 ) return s;
						break;

					case 132 : 
						int LA141_148 = input.LA(1);
						 
						int index141_148 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_148);
						if ( s>=0 ) return s;
						break;

					case 133 : 
						int LA141_149 = input.LA(1);
						 
						int index141_149 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_149);
						if ( s>=0 ) return s;
						break;

					case 134 : 
						int LA141_150 = input.LA(1);
						 
						int index141_150 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_150);
						if ( s>=0 ) return s;
						break;

					case 135 : 
						int LA141_151 = input.LA(1);
						 
						int index141_151 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_151);
						if ( s>=0 ) return s;
						break;

					case 136 : 
						int LA141_152 = input.LA(1);
						 
						int index141_152 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_152);
						if ( s>=0 ) return s;
						break;

					case 137 : 
						int LA141_153 = input.LA(1);
						 
						int index141_153 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_153);
						if ( s>=0 ) return s;
						break;

					case 138 : 
						int LA141_154 = input.LA(1);
						 
						int index141_154 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_154);
						if ( s>=0 ) return s;
						break;

					case 139 : 
						int LA141_155 = input.LA(1);
						 
						int index141_155 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_155);
						if ( s>=0 ) return s;
						break;

					case 140 : 
						int LA141_156 = input.LA(1);
						 
						int index141_156 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_156);
						if ( s>=0 ) return s;
						break;

					case 141 : 
						int LA141_157 = input.LA(1);
						 
						int index141_157 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_157);
						if ( s>=0 ) return s;
						break;

					case 142 : 
						int LA141_158 = input.LA(1);
						 
						int index141_158 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_158);
						if ( s>=0 ) return s;
						break;

					case 143 : 
						int LA141_159 = input.LA(1);
						 
						int index141_159 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_159);
						if ( s>=0 ) return s;
						break;

					case 144 : 
						int LA141_160 = input.LA(1);
						 
						int index141_160 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_160);
						if ( s>=0 ) return s;
						break;

					case 145 : 
						int LA141_161 = input.LA(1);
						 
						int index141_161 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_161);
						if ( s>=0 ) return s;
						break;

					case 146 : 
						int LA141_162 = input.LA(1);
						 
						int index141_162 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_162);
						if ( s>=0 ) return s;
						break;

					case 147 : 
						int LA141_163 = input.LA(1);
						 
						int index141_163 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_163);
						if ( s>=0 ) return s;
						break;

					case 148 : 
						int LA141_164 = input.LA(1);
						 
						int index141_164 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_164);
						if ( s>=0 ) return s;
						break;

					case 149 : 
						int LA141_165 = input.LA(1);
						 
						int index141_165 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_165);
						if ( s>=0 ) return s;
						break;

					case 150 : 
						int LA141_166 = input.LA(1);
						 
						int index141_166 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_166);
						if ( s>=0 ) return s;
						break;

					case 151 : 
						int LA141_167 = input.LA(1);
						 
						int index141_167 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_167);
						if ( s>=0 ) return s;
						break;

					case 152 : 
						int LA141_168 = input.LA(1);
						 
						int index141_168 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_168);
						if ( s>=0 ) return s;
						break;

					case 153 : 
						int LA141_169 = input.LA(1);
						 
						int index141_169 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_169);
						if ( s>=0 ) return s;
						break;

					case 154 : 
						int LA141_170 = input.LA(1);
						 
						int index141_170 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_170);
						if ( s>=0 ) return s;
						break;

					case 155 : 
						int LA141_171 = input.LA(1);
						 
						int index141_171 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_171);
						if ( s>=0 ) return s;
						break;

					case 156 : 
						int LA141_172 = input.LA(1);
						 
						int index141_172 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_172);
						if ( s>=0 ) return s;
						break;

					case 157 : 
						int LA141_173 = input.LA(1);
						 
						int index141_173 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_173);
						if ( s>=0 ) return s;
						break;

					case 158 : 
						int LA141_174 = input.LA(1);
						 
						int index141_174 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_174);
						if ( s>=0 ) return s;
						break;

					case 159 : 
						int LA141_209 = input.LA(1);
						 
						int index141_209 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_209);
						if ( s>=0 ) return s;
						break;

					case 160 : 
						int LA141_210 = input.LA(1);
						 
						int index141_210 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_210);
						if ( s>=0 ) return s;
						break;

					case 161 : 
						int LA141_211 = input.LA(1);
						 
						int index141_211 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_211);
						if ( s>=0 ) return s;
						break;

					case 162 : 
						int LA141_212 = input.LA(1);
						 
						int index141_212 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_212);
						if ( s>=0 ) return s;
						break;

					case 163 : 
						int LA141_213 = input.LA(1);
						 
						int index141_213 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_213);
						if ( s>=0 ) return s;
						break;

					case 164 : 
						int LA141_400 = input.LA(1);
						 
						int index141_400 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_400);
						if ( s>=0 ) return s;
						break;

					case 165 : 
						int LA141_401 = input.LA(1);
						 
						int index141_401 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_401);
						if ( s>=0 ) return s;
						break;

					case 166 : 
						int LA141_402 = input.LA(1);
						 
						int index141_402 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_402);
						if ( s>=0 ) return s;
						break;

					case 167 : 
						int LA141_435 = input.LA(1);
						 
						int index141_435 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_435);
						if ( s>=0 ) return s;
						break;

					case 168 : 
						int LA141_436 = input.LA(1);
						 
						int index141_436 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_436);
						if ( s>=0 ) return s;
						break;

					case 169 : 
						int LA141_437 = input.LA(1);
						 
						int index141_437 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred201_Java()) ) {s = 207;}
						else if ( (true) ) {s = 208;}
						 
						input.seek(index141_437);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 141, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	static final String DFA140_eotS =
		"\u00ce\uffff";
	static final String DFA140_eofS =
		"\1\13\u00cd\uffff";
	static final String DFA140_minS =
		"\1\40\12\0\u00c3\uffff";
	static final String DFA140_maxS =
		"\1\175\12\0\u00c3\uffff";
	static final String DFA140_acceptS =
		"\13\uffff\1\2\36\uffff\1\1\u00a3\uffff";
	static final String DFA140_specialS =
		"\1\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\u00c3\uffff}>";
	static final String[] DFA140_transitionS = {
			"\1\10\2\uffff\1\5\1\uffff\1\13\1\uffff\1\3\2\uffff\1\1\1\13\2\uffff\1"+
			"\2\3\uffff\1\4\2\13\1\11\1\13\1\uffff\1\12\3\uffff\1\13\1\uffff\1\7\74"+
			"\uffff\1\6\1\uffff\1\13",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			""
	};

	static final short[] DFA140_eot = DFA.unpackEncodedString(DFA140_eotS);
	static final short[] DFA140_eof = DFA.unpackEncodedString(DFA140_eofS);
	static final char[] DFA140_min = DFA.unpackEncodedStringToUnsignedChars(DFA140_minS);
	static final char[] DFA140_max = DFA.unpackEncodedStringToUnsignedChars(DFA140_maxS);
	static final short[] DFA140_accept = DFA.unpackEncodedString(DFA140_acceptS);
	static final short[] DFA140_special = DFA.unpackEncodedString(DFA140_specialS);
	static final short[][] DFA140_transition;

	static {
		int numStates = DFA140_transitionS.length;
		DFA140_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA140_transition[i] = DFA.unpackEncodedString(DFA140_transitionS[i]);
		}
	}

	protected class DFA140 extends DFA {

		public DFA140(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 140;
			this.eot = DFA140_eot;
			this.eof = DFA140_eof;
			this.min = DFA140_min;
			this.max = DFA140_max;
			this.accept = DFA140_accept;
			this.special = DFA140_special;
			this.transition = DFA140_transition;
		}
		@Override
		public String getDescription() {
			return "1133:31: ( assignmentOperator expression )?";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA140_1 = input.LA(1);
						 
						int index140_1 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred202_Java()) ) {s = 42;}
						else if ( (true) ) {s = 11;}
						 
						input.seek(index140_1);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA140_2 = input.LA(1);
						 
						int index140_2 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred202_Java()) ) {s = 42;}
						else if ( (true) ) {s = 11;}
						 
						input.seek(index140_2);
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA140_3 = input.LA(1);
						 
						int index140_3 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred202_Java()) ) {s = 42;}
						else if ( (true) ) {s = 11;}
						 
						input.seek(index140_3);
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA140_4 = input.LA(1);
						 
						int index140_4 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred202_Java()) ) {s = 42;}
						else if ( (true) ) {s = 11;}
						 
						input.seek(index140_4);
						if ( s>=0 ) return s;
						break;

					case 4 : 
						int LA140_5 = input.LA(1);
						 
						int index140_5 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred202_Java()) ) {s = 42;}
						else if ( (true) ) {s = 11;}
						 
						input.seek(index140_5);
						if ( s>=0 ) return s;
						break;

					case 5 : 
						int LA140_6 = input.LA(1);
						 
						int index140_6 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred202_Java()) ) {s = 42;}
						else if ( (true) ) {s = 11;}
						 
						input.seek(index140_6);
						if ( s>=0 ) return s;
						break;

					case 6 : 
						int LA140_7 = input.LA(1);
						 
						int index140_7 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred202_Java()) ) {s = 42;}
						else if ( (true) ) {s = 11;}
						 
						input.seek(index140_7);
						if ( s>=0 ) return s;
						break;

					case 7 : 
						int LA140_8 = input.LA(1);
						 
						int index140_8 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred202_Java()) ) {s = 42;}
						else if ( (true) ) {s = 11;}
						 
						input.seek(index140_8);
						if ( s>=0 ) return s;
						break;

					case 8 : 
						int LA140_9 = input.LA(1);
						 
						int index140_9 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred202_Java()) ) {s = 42;}
						else if ( (true) ) {s = 11;}
						 
						input.seek(index140_9);
						if ( s>=0 ) return s;
						break;

					case 9 : 
						int LA140_10 = input.LA(1);
						 
						int index140_10 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred202_Java()) ) {s = 42;}
						else if ( (true) ) {s = 11;}
						 
						input.seek(index140_10);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 140, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	static final String DFA151_eotS =
		"\114\uffff";
	static final String DFA151_eofS =
		"\1\1\113\uffff";
	static final String DFA151_minS =
		"\1\36\25\uffff\2\5\3\uffff\1\0\60\uffff";
	static final String DFA151_maxS =
		"\1\175\25\uffff\2\176\3\uffff\1\0\60\uffff";
	static final String DFA151_acceptS =
		"\1\uffff\1\2\32\uffff\1\1\57\uffff";
	static final String DFA151_specialS =
		"\33\uffff\1\0\60\uffff}>";
	static final String[] DFA151_transitionS = {
			"\1\1\1\uffff\4\1\1\uffff\1\1\1\uffff\1\1\2\uffff\2\1\2\uffff\1\1\3\uffff"+
			"\3\1\1\26\2\1\1\27\1\1\2\uffff\3\1\34\uffff\1\1\36\uffff\4\1",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\2\34\7\uffff\1\34\1\uffff\2\34\6\uffff\2\34\3\uffff\1\34\6\uffff\1"+
			"\34\3\uffff\2\34\2\uffff\2\34\7\uffff\1\33\1\34\12\uffff\1\34\1\uffff"+
			"\1\34\2\uffff\2\34\5\uffff\1\34\1\uffff\2\34\1\uffff\1\34\2\uffff\1\34"+
			"\6\uffff\1\34\1\uffff\1\34\2\uffff\2\34\6\uffff\1\34\2\uffff\1\34\2\uffff"+
			"\1\34\3\uffff\1\34\2\uffff\1\34\7\uffff\1\34",
			"\2\34\7\uffff\1\34\1\uffff\2\34\6\uffff\2\34\3\uffff\1\34\6\uffff\1"+
			"\34\3\uffff\2\34\2\uffff\2\34\7\uffff\2\34\1\uffff\1\1\10\uffff\1\34"+
			"\1\uffff\1\34\2\uffff\2\34\5\uffff\1\34\1\uffff\2\34\1\uffff\1\34\2\uffff"+
			"\1\34\6\uffff\1\34\1\uffff\1\34\2\uffff\2\34\6\uffff\1\34\2\uffff\1\34"+
			"\2\uffff\1\34\3\uffff\1\34\2\uffff\1\34\7\uffff\1\34",
			"",
			"",
			"",
			"\1\uffff",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			""
	};

	static final short[] DFA151_eot = DFA.unpackEncodedString(DFA151_eotS);
	static final short[] DFA151_eof = DFA.unpackEncodedString(DFA151_eofS);
	static final char[] DFA151_min = DFA.unpackEncodedStringToUnsignedChars(DFA151_minS);
	static final char[] DFA151_max = DFA.unpackEncodedStringToUnsignedChars(DFA151_maxS);
	static final short[] DFA151_accept = DFA.unpackEncodedString(DFA151_acceptS);
	static final short[] DFA151_special = DFA.unpackEncodedString(DFA151_specialS);
	static final short[][] DFA151_transition;

	static {
		int numStates = DFA151_transitionS.length;
		DFA151_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA151_transition[i] = DFA.unpackEncodedString(DFA151_transitionS[i]);
		}
	}

	protected class DFA151 extends DFA {

		public DFA151(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 151;
			this.eot = DFA151_eot;
			this.eof = DFA151_eof;
			this.min = DFA151_min;
			this.max = DFA151_max;
			this.accept = DFA151_accept;
			this.special = DFA151_special;
			this.transition = DFA151_transition;
		}
		@Override
		public String getDescription() {
			return "()* loopback of 1190:25: ( relationalOp shiftExpression )*";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA151_27 = input.LA(1);
						 
						int index151_27 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred222_Java()) ) {s = 28;}
						else if ( (true) ) {s = 1;}
						 
						input.seek(index151_27);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 151, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	static final String DFA153_eotS =
		"\115\uffff";
	static final String DFA153_eofS =
		"\1\3\114\uffff";
	static final String DFA153_minS =
		"\1\36\2\5\31\uffff\1\0\27\uffff\1\0\30\uffff";
	static final String DFA153_maxS =
		"\1\175\2\176\31\uffff\1\0\27\uffff\1\0\30\uffff";
	static final String DFA153_acceptS =
		"\3\uffff\1\2\110\uffff\1\1";
	static final String DFA153_specialS =
		"\34\uffff\1\0\27\uffff\1\1\30\uffff}>";
	static final String[] DFA153_transitionS = {
			"\1\3\1\uffff\4\3\1\uffff\1\3\1\uffff\1\3\2\uffff\2\3\2\uffff\1\3\3\uffff"+
			"\3\3\1\1\2\3\1\2\1\3\2\uffff\3\3\34\uffff\1\3\36\uffff\4\3",
			"\2\3\7\uffff\1\3\1\uffff\2\3\6\uffff\2\3\3\uffff\1\3\6\uffff\1\3\3\uffff"+
			"\2\3\2\uffff\2\3\7\uffff\1\34\1\3\12\uffff\1\3\1\uffff\1\3\2\uffff\2"+
			"\3\5\uffff\1\3\1\uffff\2\3\1\uffff\1\3\2\uffff\1\3\6\uffff\1\3\1\uffff"+
			"\1\3\2\uffff\2\3\6\uffff\1\3\2\uffff\1\3\2\uffff\1\3\3\uffff\1\3\2\uffff"+
			"\1\3\7\uffff\1\3",
			"\2\3\7\uffff\1\3\1\uffff\2\3\6\uffff\2\3\3\uffff\1\3\6\uffff\1\3\3\uffff"+
			"\2\3\2\uffff\2\3\7\uffff\2\3\1\uffff\1\64\10\uffff\1\3\1\uffff\1\3\2"+
			"\uffff\2\3\5\uffff\1\3\1\uffff\2\3\1\uffff\1\3\2\uffff\1\3\6\uffff\1"+
			"\3\1\uffff\1\3\2\uffff\2\3\6\uffff\1\3\2\uffff\1\3\2\uffff\1\3\3\uffff"+
			"\1\3\2\uffff\1\3\7\uffff\1\3",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\uffff",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\uffff",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			""
	};

	static final short[] DFA153_eot = DFA.unpackEncodedString(DFA153_eotS);
	static final short[] DFA153_eof = DFA.unpackEncodedString(DFA153_eofS);
	static final char[] DFA153_min = DFA.unpackEncodedStringToUnsignedChars(DFA153_minS);
	static final char[] DFA153_max = DFA.unpackEncodedStringToUnsignedChars(DFA153_maxS);
	static final short[] DFA153_accept = DFA.unpackEncodedString(DFA153_acceptS);
	static final short[] DFA153_special = DFA.unpackEncodedString(DFA153_specialS);
	static final short[][] DFA153_transition;

	static {
		int numStates = DFA153_transitionS.length;
		DFA153_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA153_transition[i] = DFA.unpackEncodedString(DFA153_transitionS[i]);
		}
	}

	protected class DFA153 extends DFA {

		public DFA153(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 153;
			this.eot = DFA153_eot;
			this.eof = DFA153_eof;
			this.min = DFA153_min;
			this.max = DFA153_max;
			this.accept = DFA153_accept;
			this.special = DFA153_special;
			this.transition = DFA153_transition;
		}
		@Override
		public String getDescription() {
			return "()* loopback of 1198:28: ( shiftOp additiveExpression )*";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA153_28 = input.LA(1);
						 
						int index153_28 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred226_Java()) ) {s = 76;}
						else if ( (true) ) {s = 3;}
						 
						input.seek(index153_28);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA153_52 = input.LA(1);
						 
						int index153_52 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred226_Java()) ) {s = 76;}
						else if ( (true) ) {s = 3;}
						 
						input.seek(index153_52);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 153, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	static final String DFA165_eotS =
		"\77\uffff";
	static final String DFA165_eofS =
		"\1\4\76\uffff";
	static final String DFA165_minS =
		"\1\36\1\5\1\uffff\1\21\36\uffff\27\0\1\uffff\3\0\2\uffff";
	static final String DFA165_maxS =
		"\1\175\1\176\1\uffff\1\165\36\uffff\27\0\1\uffff\3\0\2\uffff";
	static final String DFA165_acceptS =
		"\2\uffff\1\1\1\uffff\1\2\72\uffff";
	static final String DFA165_specialS =
		"\42\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15"+
		"\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\uffff\1\27\1\30\1\31\2"+
		"\uffff}>";
	static final String[] DFA165_transitionS = {
			"\6\4\1\2\12\4\1\3\1\uffff\11\4\1\uffff\1\1\3\4\34\uffff\1\4\36\uffff"+
			"\4\4",
			"\1\61\1\57\7\uffff\1\60\1\uffff\1\57\1\66\6\uffff\1\57\1\62\3\uffff"+
			"\1\47\6\uffff\1\50\3\uffff\1\42\1\44\2\uffff\1\43\1\45\7\uffff\1\51\6"+
			"\uffff\1\2\4\uffff\1\67\1\uffff\1\67\2\uffff\1\56\1\67\5\uffff\1\67\1"+
			"\uffff\1\55\1\54\1\uffff\1\63\2\uffff\1\67\6\uffff\1\67\1\uffff\1\67"+
			"\2\uffff\1\65\1\64\6\uffff\1\67\2\uffff\1\53\2\uffff\1\52\3\uffff\1\63"+
			"\2\uffff\1\70\7\uffff\1\46",
			"",
			"\1\4\43\uffff\1\2\22\uffff\1\2\2\uffff\1\4\16\uffff\1\4\4\uffff\1\4"+
			"\1\uffff\1\74\5\uffff\1\4\4\uffff\1\73\2\uffff\1\72\5\uffff\1\4",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"",
			""
	};

	static final short[] DFA165_eot = DFA.unpackEncodedString(DFA165_eotS);
	static final short[] DFA165_eof = DFA.unpackEncodedString(DFA165_eofS);
	static final char[] DFA165_min = DFA.unpackEncodedStringToUnsignedChars(DFA165_minS);
	static final char[] DFA165_max = DFA.unpackEncodedStringToUnsignedChars(DFA165_maxS);
	static final short[] DFA165_accept = DFA.unpackEncodedString(DFA165_acceptS);
	static final short[] DFA165_special = DFA.unpackEncodedString(DFA165_specialS);
	static final short[][] DFA165_transition;

	static {
		int numStates = DFA165_transitionS.length;
		DFA165_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA165_transition[i] = DFA.unpackEncodedString(DFA165_transitionS[i]);
		}
	}

	protected class DFA165 extends DFA {

		public DFA165(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 165;
			this.eot = DFA165_eot;
			this.eof = DFA165_eof;
			this.min = DFA165_min;
			this.max = DFA165_max;
			this.accept = DFA165_accept;
			this.special = DFA165_special;
			this.transition = DFA165_transition;
		}
		@Override
		public String getDescription() {
			return "1239:34: ( identifierSuffix )?";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA165_34 = input.LA(1);
						 
						int index165_34 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_34);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA165_35 = input.LA(1);
						 
						int index165_35 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_35);
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA165_36 = input.LA(1);
						 
						int index165_36 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_36);
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA165_37 = input.LA(1);
						 
						int index165_37 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_37);
						if ( s>=0 ) return s;
						break;

					case 4 : 
						int LA165_38 = input.LA(1);
						 
						int index165_38 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_38);
						if ( s>=0 ) return s;
						break;

					case 5 : 
						int LA165_39 = input.LA(1);
						 
						int index165_39 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_39);
						if ( s>=0 ) return s;
						break;

					case 6 : 
						int LA165_40 = input.LA(1);
						 
						int index165_40 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_40);
						if ( s>=0 ) return s;
						break;

					case 7 : 
						int LA165_41 = input.LA(1);
						 
						int index165_41 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_41);
						if ( s>=0 ) return s;
						break;

					case 8 : 
						int LA165_42 = input.LA(1);
						 
						int index165_42 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_42);
						if ( s>=0 ) return s;
						break;

					case 9 : 
						int LA165_43 = input.LA(1);
						 
						int index165_43 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_43);
						if ( s>=0 ) return s;
						break;

					case 10 : 
						int LA165_44 = input.LA(1);
						 
						int index165_44 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_44);
						if ( s>=0 ) return s;
						break;

					case 11 : 
						int LA165_45 = input.LA(1);
						 
						int index165_45 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_45);
						if ( s>=0 ) return s;
						break;

					case 12 : 
						int LA165_46 = input.LA(1);
						 
						int index165_46 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_46);
						if ( s>=0 ) return s;
						break;

					case 13 : 
						int LA165_47 = input.LA(1);
						 
						int index165_47 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_47);
						if ( s>=0 ) return s;
						break;

					case 14 : 
						int LA165_48 = input.LA(1);
						 
						int index165_48 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_48);
						if ( s>=0 ) return s;
						break;

					case 15 : 
						int LA165_49 = input.LA(1);
						 
						int index165_49 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_49);
						if ( s>=0 ) return s;
						break;

					case 16 : 
						int LA165_50 = input.LA(1);
						 
						int index165_50 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_50);
						if ( s>=0 ) return s;
						break;

					case 17 : 
						int LA165_51 = input.LA(1);
						 
						int index165_51 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_51);
						if ( s>=0 ) return s;
						break;

					case 18 : 
						int LA165_52 = input.LA(1);
						 
						int index165_52 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_52);
						if ( s>=0 ) return s;
						break;

					case 19 : 
						int LA165_53 = input.LA(1);
						 
						int index165_53 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_53);
						if ( s>=0 ) return s;
						break;

					case 20 : 
						int LA165_54 = input.LA(1);
						 
						int index165_54 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_54);
						if ( s>=0 ) return s;
						break;

					case 21 : 
						int LA165_55 = input.LA(1);
						 
						int index165_55 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_55);
						if ( s>=0 ) return s;
						break;

					case 22 : 
						int LA165_56 = input.LA(1);
						 
						int index165_56 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_56);
						if ( s>=0 ) return s;
						break;

					case 23 : 
						int LA165_58 = input.LA(1);
						 
						int index165_58 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_58);
						if ( s>=0 ) return s;
						break;

					case 24 : 
						int LA165_59 = input.LA(1);
						 
						int index165_59 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_59);
						if ( s>=0 ) return s;
						break;

					case 25 : 
						int LA165_60 = input.LA(1);
						 
						int index165_60 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred250_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index165_60);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 165, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	static final String DFA167_eotS =
		"\77\uffff";
	static final String DFA167_eofS =
		"\1\4\76\uffff";
	static final String DFA167_minS =
		"\1\36\1\5\1\uffff\1\21\36\uffff\27\0\1\uffff\3\0\2\uffff";
	static final String DFA167_maxS =
		"\1\175\1\176\1\uffff\1\165\36\uffff\27\0\1\uffff\3\0\2\uffff";
	static final String DFA167_acceptS =
		"\2\uffff\1\1\1\uffff\1\2\72\uffff";
	static final String DFA167_specialS =
		"\42\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15"+
		"\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\uffff\1\27\1\30\1\31\2"+
		"\uffff}>";
	static final String[] DFA167_transitionS = {
			"\6\4\1\2\12\4\1\3\1\uffff\11\4\1\uffff\1\1\3\4\34\uffff\1\4\36\uffff"+
			"\4\4",
			"\1\61\1\57\7\uffff\1\60\1\uffff\1\57\1\66\6\uffff\1\57\1\62\3\uffff"+
			"\1\47\6\uffff\1\50\3\uffff\1\42\1\44\2\uffff\1\43\1\45\7\uffff\1\51\6"+
			"\uffff\1\2\4\uffff\1\67\1\uffff\1\67\2\uffff\1\56\1\67\5\uffff\1\67\1"+
			"\uffff\1\55\1\54\1\uffff\1\63\2\uffff\1\67\6\uffff\1\67\1\uffff\1\67"+
			"\2\uffff\1\65\1\64\6\uffff\1\67\2\uffff\1\53\2\uffff\1\52\3\uffff\1\63"+
			"\2\uffff\1\70\7\uffff\1\46",
			"",
			"\1\4\43\uffff\1\2\22\uffff\1\2\2\uffff\1\4\16\uffff\1\4\4\uffff\1\4"+
			"\1\uffff\1\74\5\uffff\1\4\4\uffff\1\73\2\uffff\1\72\5\uffff\1\4",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"",
			""
	};

	static final short[] DFA167_eot = DFA.unpackEncodedString(DFA167_eotS);
	static final short[] DFA167_eof = DFA.unpackEncodedString(DFA167_eofS);
	static final char[] DFA167_min = DFA.unpackEncodedStringToUnsignedChars(DFA167_minS);
	static final char[] DFA167_max = DFA.unpackEncodedStringToUnsignedChars(DFA167_maxS);
	static final short[] DFA167_accept = DFA.unpackEncodedString(DFA167_acceptS);
	static final short[] DFA167_special = DFA.unpackEncodedString(DFA167_specialS);
	static final short[][] DFA167_transition;

	static {
		int numStates = DFA167_transitionS.length;
		DFA167_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA167_transition[i] = DFA.unpackEncodedString(DFA167_transitionS[i]);
		}
	}

	protected class DFA167 extends DFA {

		public DFA167(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 167;
			this.eot = DFA167_eot;
			this.eof = DFA167_eof;
			this.min = DFA167_min;
			this.max = DFA167_max;
			this.accept = DFA167_accept;
			this.special = DFA167_special;
			this.transition = DFA167_transition;
		}
		@Override
		public String getDescription() {
			return "1241:39: ( identifierSuffix )?";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA167_34 = input.LA(1);
						 
						int index167_34 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_34);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA167_35 = input.LA(1);
						 
						int index167_35 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_35);
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA167_36 = input.LA(1);
						 
						int index167_36 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_36);
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA167_37 = input.LA(1);
						 
						int index167_37 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_37);
						if ( s>=0 ) return s;
						break;

					case 4 : 
						int LA167_38 = input.LA(1);
						 
						int index167_38 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_38);
						if ( s>=0 ) return s;
						break;

					case 5 : 
						int LA167_39 = input.LA(1);
						 
						int index167_39 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_39);
						if ( s>=0 ) return s;
						break;

					case 6 : 
						int LA167_40 = input.LA(1);
						 
						int index167_40 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_40);
						if ( s>=0 ) return s;
						break;

					case 7 : 
						int LA167_41 = input.LA(1);
						 
						int index167_41 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_41);
						if ( s>=0 ) return s;
						break;

					case 8 : 
						int LA167_42 = input.LA(1);
						 
						int index167_42 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_42);
						if ( s>=0 ) return s;
						break;

					case 9 : 
						int LA167_43 = input.LA(1);
						 
						int index167_43 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_43);
						if ( s>=0 ) return s;
						break;

					case 10 : 
						int LA167_44 = input.LA(1);
						 
						int index167_44 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_44);
						if ( s>=0 ) return s;
						break;

					case 11 : 
						int LA167_45 = input.LA(1);
						 
						int index167_45 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_45);
						if ( s>=0 ) return s;
						break;

					case 12 : 
						int LA167_46 = input.LA(1);
						 
						int index167_46 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_46);
						if ( s>=0 ) return s;
						break;

					case 13 : 
						int LA167_47 = input.LA(1);
						 
						int index167_47 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_47);
						if ( s>=0 ) return s;
						break;

					case 14 : 
						int LA167_48 = input.LA(1);
						 
						int index167_48 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_48);
						if ( s>=0 ) return s;
						break;

					case 15 : 
						int LA167_49 = input.LA(1);
						 
						int index167_49 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_49);
						if ( s>=0 ) return s;
						break;

					case 16 : 
						int LA167_50 = input.LA(1);
						 
						int index167_50 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_50);
						if ( s>=0 ) return s;
						break;

					case 17 : 
						int LA167_51 = input.LA(1);
						 
						int index167_51 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_51);
						if ( s>=0 ) return s;
						break;

					case 18 : 
						int LA167_52 = input.LA(1);
						 
						int index167_52 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_52);
						if ( s>=0 ) return s;
						break;

					case 19 : 
						int LA167_53 = input.LA(1);
						 
						int index167_53 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_53);
						if ( s>=0 ) return s;
						break;

					case 20 : 
						int LA167_54 = input.LA(1);
						 
						int index167_54 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_54);
						if ( s>=0 ) return s;
						break;

					case 21 : 
						int LA167_55 = input.LA(1);
						 
						int index167_55 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_55);
						if ( s>=0 ) return s;
						break;

					case 22 : 
						int LA167_56 = input.LA(1);
						 
						int index167_56 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_56);
						if ( s>=0 ) return s;
						break;

					case 23 : 
						int LA167_58 = input.LA(1);
						 
						int index167_58 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_58);
						if ( s>=0 ) return s;
						break;

					case 24 : 
						int LA167_59 = input.LA(1);
						 
						int index167_59 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_59);
						if ( s>=0 ) return s;
						break;

					case 25 : 
						int LA167_60 = input.LA(1);
						 
						int index167_60 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred254_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index167_60);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 167, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	static final String DFA169_eotS =
		"\77\uffff";
	static final String DFA169_eofS =
		"\1\4\76\uffff";
	static final String DFA169_minS =
		"\1\36\1\5\1\uffff\1\21\36\uffff\27\0\1\uffff\3\0\2\uffff";
	static final String DFA169_maxS =
		"\1\175\1\176\1\uffff\1\165\36\uffff\27\0\1\uffff\3\0\2\uffff";
	static final String DFA169_acceptS =
		"\2\uffff\1\1\1\uffff\1\2\72\uffff";
	static final String DFA169_specialS =
		"\42\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15"+
		"\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\uffff\1\27\1\30\1\31\2"+
		"\uffff}>";
	static final String[] DFA169_transitionS = {
			"\6\4\1\2\12\4\1\3\1\uffff\11\4\1\uffff\1\1\3\4\34\uffff\1\4\36\uffff"+
			"\4\4",
			"\1\61\1\57\7\uffff\1\60\1\uffff\1\57\1\66\6\uffff\1\57\1\62\3\uffff"+
			"\1\47\6\uffff\1\50\3\uffff\1\42\1\44\2\uffff\1\43\1\45\7\uffff\1\51\6"+
			"\uffff\1\2\4\uffff\1\67\1\uffff\1\67\2\uffff\1\56\1\67\5\uffff\1\67\1"+
			"\uffff\1\55\1\54\1\uffff\1\63\2\uffff\1\67\6\uffff\1\67\1\uffff\1\67"+
			"\2\uffff\1\65\1\64\6\uffff\1\67\2\uffff\1\53\2\uffff\1\52\3\uffff\1\63"+
			"\2\uffff\1\70\7\uffff\1\46",
			"",
			"\1\4\43\uffff\1\2\22\uffff\1\2\2\uffff\1\4\16\uffff\1\4\4\uffff\1\4"+
			"\1\uffff\1\74\5\uffff\1\4\4\uffff\1\73\2\uffff\1\72\5\uffff\1\4",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"",
			""
	};

	static final short[] DFA169_eot = DFA.unpackEncodedString(DFA169_eotS);
	static final short[] DFA169_eof = DFA.unpackEncodedString(DFA169_eofS);
	static final char[] DFA169_min = DFA.unpackEncodedStringToUnsignedChars(DFA169_minS);
	static final char[] DFA169_max = DFA.unpackEncodedStringToUnsignedChars(DFA169_maxS);
	static final short[] DFA169_accept = DFA.unpackEncodedString(DFA169_acceptS);
	static final short[] DFA169_special = DFA.unpackEncodedString(DFA169_specialS);
	static final short[][] DFA169_transition;

	static {
		int numStates = DFA169_transitionS.length;
		DFA169_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA169_transition[i] = DFA.unpackEncodedString(DFA169_transitionS[i]);
		}
	}

	protected class DFA169 extends DFA {

		public DFA169(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 169;
			this.eot = DFA169_eot;
			this.eof = DFA169_eof;
			this.min = DFA169_min;
			this.max = DFA169_max;
			this.accept = DFA169_accept;
			this.special = DFA169_special;
			this.transition = DFA169_transition;
		}
		@Override
		public String getDescription() {
			return "1244:144: ( identifierSuffix )?";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA169_34 = input.LA(1);
						 
						int index169_34 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_34);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA169_35 = input.LA(1);
						 
						int index169_35 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_35);
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA169_36 = input.LA(1);
						 
						int index169_36 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_36);
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA169_37 = input.LA(1);
						 
						int index169_37 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_37);
						if ( s>=0 ) return s;
						break;

					case 4 : 
						int LA169_38 = input.LA(1);
						 
						int index169_38 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_38);
						if ( s>=0 ) return s;
						break;

					case 5 : 
						int LA169_39 = input.LA(1);
						 
						int index169_39 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_39);
						if ( s>=0 ) return s;
						break;

					case 6 : 
						int LA169_40 = input.LA(1);
						 
						int index169_40 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_40);
						if ( s>=0 ) return s;
						break;

					case 7 : 
						int LA169_41 = input.LA(1);
						 
						int index169_41 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_41);
						if ( s>=0 ) return s;
						break;

					case 8 : 
						int LA169_42 = input.LA(1);
						 
						int index169_42 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_42);
						if ( s>=0 ) return s;
						break;

					case 9 : 
						int LA169_43 = input.LA(1);
						 
						int index169_43 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_43);
						if ( s>=0 ) return s;
						break;

					case 10 : 
						int LA169_44 = input.LA(1);
						 
						int index169_44 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_44);
						if ( s>=0 ) return s;
						break;

					case 11 : 
						int LA169_45 = input.LA(1);
						 
						int index169_45 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_45);
						if ( s>=0 ) return s;
						break;

					case 12 : 
						int LA169_46 = input.LA(1);
						 
						int index169_46 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_46);
						if ( s>=0 ) return s;
						break;

					case 13 : 
						int LA169_47 = input.LA(1);
						 
						int index169_47 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_47);
						if ( s>=0 ) return s;
						break;

					case 14 : 
						int LA169_48 = input.LA(1);
						 
						int index169_48 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_48);
						if ( s>=0 ) return s;
						break;

					case 15 : 
						int LA169_49 = input.LA(1);
						 
						int index169_49 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_49);
						if ( s>=0 ) return s;
						break;

					case 16 : 
						int LA169_50 = input.LA(1);
						 
						int index169_50 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_50);
						if ( s>=0 ) return s;
						break;

					case 17 : 
						int LA169_51 = input.LA(1);
						 
						int index169_51 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_51);
						if ( s>=0 ) return s;
						break;

					case 18 : 
						int LA169_52 = input.LA(1);
						 
						int index169_52 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_52);
						if ( s>=0 ) return s;
						break;

					case 19 : 
						int LA169_53 = input.LA(1);
						 
						int index169_53 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_53);
						if ( s>=0 ) return s;
						break;

					case 20 : 
						int LA169_54 = input.LA(1);
						 
						int index169_54 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_54);
						if ( s>=0 ) return s;
						break;

					case 21 : 
						int LA169_55 = input.LA(1);
						 
						int index169_55 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_55);
						if ( s>=0 ) return s;
						break;

					case 22 : 
						int LA169_56 = input.LA(1);
						 
						int index169_56 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_56);
						if ( s>=0 ) return s;
						break;

					case 23 : 
						int LA169_58 = input.LA(1);
						 
						int index169_58 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_58);
						if ( s>=0 ) return s;
						break;

					case 24 : 
						int LA169_59 = input.LA(1);
						 
						int index169_59 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_59);
						if ( s>=0 ) return s;
						break;

					case 25 : 
						int LA169_60 = input.LA(1);
						 
						int index169_60 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred259_Java()) ) {s = 2;}
						else if ( (true) ) {s = 4;}
						 
						input.seek(index169_60);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 169, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	public static final BitSet FOLLOW_annotations_in_compilationUnit81 = new BitSet(new long[]{0x8410000000000402L,0x00844C7922080100L});
	public static final BitSet FOLLOW_packageDeclaration_in_compilationUnit92 = new BitSet(new long[]{0x8410000000000402L,0x00844C7122080100L});
	public static final BitSet FOLLOW_importDeclaration_in_compilationUnit103 = new BitSet(new long[]{0x8410000000000402L,0x00844C7122080100L});
	public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit114 = new BitSet(new long[]{0x8410000000000402L,0x00844C7120080100L});
	public static final BitSet FOLLOW_99_in_packageDeclaration132 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_qualifiedName_in_packageDeclaration134 = new BitSet(new long[]{0x0010000000000000L});
	public static final BitSet FOLLOW_52_in_packageDeclaration136 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_89_in_importDeclaration153 = new BitSet(new long[]{0x0000000000020000L,0x0000040000000000L});
	public static final BitSet FOLLOW_106_in_importDeclaration155 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_importDeclaration158 = new BitSet(new long[]{0x0010800000000000L});
	public static final BitSet FOLLOW_47_in_importDeclaration161 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_importDeclaration163 = new BitSet(new long[]{0x0010800000000000L});
	public static final BitSet FOLLOW_47_in_importDeclaration168 = new BitSet(new long[]{0x0000004000000000L});
	public static final BitSet FOLLOW_38_in_importDeclaration170 = new BitSet(new long[]{0x0010000000000000L});
	public static final BitSet FOLLOW_52_in_importDeclaration174 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration191 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_52_in_typeDeclaration201 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifier_in_classOrInterfaceDeclaration218 = new BitSet(new long[]{0x8400000000000400L,0x00844C7120080100L});
	public static final BitSet FOLLOW_classDeclaration_in_classOrInterfaceDeclaration222 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration226 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_normalClassDeclaration_in_classDeclaration244 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_enumDeclaration_in_classDeclaration254 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_72_in_normalClassDeclaration271 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_normalClassDeclaration273 = new BitSet(new long[]{0x0020000000000000L,0x0200000001020000L});
	public static final BitSet FOLLOW_typeParameters_in_normalClassDeclaration276 = new BitSet(new long[]{0x0000000000000000L,0x0200000001020000L});
	public static final BitSet FOLLOW_81_in_normalClassDeclaration289 = new BitSet(new long[]{0x0000000000020000L,0x000002005020208AL});
	public static final BitSet FOLLOW_type_in_normalClassDeclaration291 = new BitSet(new long[]{0x0000000000000000L,0x0200000001000000L});
	public static final BitSet FOLLOW_88_in_normalClassDeclaration304 = new BitSet(new long[]{0x0000000000020000L,0x000002005020208AL});
	public static final BitSet FOLLOW_typeList_in_normalClassDeclaration306 = new BitSet(new long[]{0x0000000000000000L,0x0200000000000000L});
	public static final BitSet FOLLOW_classBody_in_normalClassDeclaration318 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_53_in_typeParameters335 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_typeParameter_in_typeParameters337 = new BitSet(new long[]{0x0100080000000000L});
	public static final BitSet FOLLOW_43_in_typeParameters340 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_typeParameter_in_typeParameters342 = new BitSet(new long[]{0x0100080000000000L});
	public static final BitSet FOLLOW_56_in_typeParameters346 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Identifier_in_typeParameter363 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
	public static final BitSet FOLLOW_81_in_typeParameter366 = new BitSet(new long[]{0x0000000000020000L,0x000002005020208AL});
	public static final BitSet FOLLOW_bound_in_typeParameter368 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_bound387 = new BitSet(new long[]{0x0000000400000002L});
	public static final BitSet FOLLOW_34_in_bound390 = new BitSet(new long[]{0x0000000000020000L,0x000002005020208AL});
	public static final BitSet FOLLOW_type_in_bound392 = new BitSet(new long[]{0x0000000400000002L});
	public static final BitSet FOLLOW_ENUM_in_enumDeclaration411 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_enumDeclaration413 = new BitSet(new long[]{0x0000000000000000L,0x0200000001000000L});
	public static final BitSet FOLLOW_88_in_enumDeclaration416 = new BitSet(new long[]{0x0000000000020000L,0x000002005020208AL});
	public static final BitSet FOLLOW_typeList_in_enumDeclaration418 = new BitSet(new long[]{0x0000000000000000L,0x0200000000000000L});
	public static final BitSet FOLLOW_enumBody_in_enumDeclaration422 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_121_in_enumBody439 = new BitSet(new long[]{0x0410080000020000L,0x2000000000000000L});
	public static final BitSet FOLLOW_enumConstants_in_enumBody441 = new BitSet(new long[]{0x0010080000000000L,0x2000000000000000L});
	public static final BitSet FOLLOW_43_in_enumBody444 = new BitSet(new long[]{0x0010000000000000L,0x2000000000000000L});
	public static final BitSet FOLLOW_enumBodyDeclarations_in_enumBody447 = new BitSet(new long[]{0x0000000000000000L,0x2000000000000000L});
	public static final BitSet FOLLOW_125_in_enumBody450 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_enumConstant_in_enumConstants467 = new BitSet(new long[]{0x0000080000000002L});
	public static final BitSet FOLLOW_43_in_enumConstants470 = new BitSet(new long[]{0x0400000000020000L});
	public static final BitSet FOLLOW_enumConstant_in_enumConstants472 = new BitSet(new long[]{0x0000080000000002L});
	public static final BitSet FOLLOW_annotations_in_enumConstant491 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_enumConstant494 = new BitSet(new long[]{0x0000001000000002L,0x0200000000000000L});
	public static final BitSet FOLLOW_arguments_in_enumConstant497 = new BitSet(new long[]{0x0000000000000002L,0x0200000000000000L});
	public static final BitSet FOLLOW_classBody_in_enumConstant502 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_52_in_enumBodyDeclarations521 = new BitSet(new long[]{0x8430000000020402L,0x02C44E717028218AL});
	public static final BitSet FOLLOW_classBodyDeclaration_in_enumBodyDeclarations524 = new BitSet(new long[]{0x8430000000020402L,0x02C44E717028218AL});
	public static final BitSet FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration543 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration555 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_93_in_normalInterfaceDeclaration572 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_normalInterfaceDeclaration574 = new BitSet(new long[]{0x0020000000000000L,0x0200000000020000L});
	public static final BitSet FOLLOW_typeParameters_in_normalInterfaceDeclaration576 = new BitSet(new long[]{0x0000000000000000L,0x0200000000020000L});
	public static final BitSet FOLLOW_81_in_normalInterfaceDeclaration580 = new BitSet(new long[]{0x0000000000020000L,0x000002005020208AL});
	public static final BitSet FOLLOW_typeList_in_normalInterfaceDeclaration582 = new BitSet(new long[]{0x0000000000000000L,0x0200000000000000L});
	public static final BitSet FOLLOW_interfaceBody_in_normalInterfaceDeclaration586 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_typeList603 = new BitSet(new long[]{0x0000080000000002L});
	public static final BitSet FOLLOW_43_in_typeList606 = new BitSet(new long[]{0x0000000000020000L,0x000002005020208AL});
	public static final BitSet FOLLOW_type_in_typeList608 = new BitSet(new long[]{0x0000080000000002L});
	public static final BitSet FOLLOW_121_in_classBody627 = new BitSet(new long[]{0x8430000000020400L,0x22C44E717028218AL});
	public static final BitSet FOLLOW_classBodyDeclaration_in_classBody629 = new BitSet(new long[]{0x8430000000020400L,0x22C44E717028218AL});
	public static final BitSet FOLLOW_125_in_classBody632 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_121_in_interfaceBody649 = new BitSet(new long[]{0x8430000000020400L,0x20C44E717028218AL});
	public static final BitSet FOLLOW_interfaceBodyDeclaration_in_interfaceBody651 = new BitSet(new long[]{0x8430000000020400L,0x20C44E717028218AL});
	public static final BitSet FOLLOW_125_in_interfaceBody654 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_52_in_classBodyDeclaration671 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_106_in_classBodyDeclaration679 = new BitSet(new long[]{0x0000000000000000L,0x0200000000000000L});
	public static final BitSet FOLLOW_block_in_classBodyDeclaration682 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifier_in_classBodyDeclaration690 = new BitSet(new long[]{0x8420000000020400L,0x00C44E717028218AL});
	public static final BitSet FOLLOW_memberDecl_in_classBodyDeclaration693 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_genericMethodOrConstructorDecl_in_memberDecl710 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_methodDeclaration_in_memberDecl718 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fieldDeclaration_in_memberDecl726 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_118_in_memberDecl734 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_memberDecl736 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_voidMethodDeclaratorRest_in_memberDecl738 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Identifier_in_memberDecl746 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_constructorDeclaratorRest_in_memberDecl748 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_interfaceDeclaration_in_memberDecl756 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_classDeclaration_in_memberDecl764 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_typeParameters_in_genericMethodOrConstructorDecl781 = new BitSet(new long[]{0x0000000000020000L,0x004002005020208AL});
	public static final BitSet FOLLOW_genericMethodOrConstructorRest_in_genericMethodOrConstructorDecl783 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_genericMethodOrConstructorRest801 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_118_in_genericMethodOrConstructorRest805 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_genericMethodOrConstructorRest808 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_methodDeclaratorRest_in_genericMethodOrConstructorRest810 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Identifier_in_genericMethodOrConstructorRest818 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_constructorDeclaratorRest_in_genericMethodOrConstructorRest820 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_methodDeclaration842 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_methodDeclaration844 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_methodDeclaratorRest_in_methodDeclaration846 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_fieldDeclaration863 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_variableDeclarators_in_fieldDeclaration865 = new BitSet(new long[]{0x0010000000000000L});
	public static final BitSet FOLLOW_52_in_fieldDeclaration867 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifier_in_interfaceBodyDeclaration884 = new BitSet(new long[]{0x8420000000020400L,0x00C44E717028218AL});
	public static final BitSet FOLLOW_interfaceMemberDecl_in_interfaceBodyDeclaration887 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_52_in_interfaceBodyDeclaration897 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_interfaceMethodOrFieldDecl_in_interfaceMemberDecl914 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_interfaceGenericMethodDecl_in_interfaceMemberDecl924 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_118_in_interfaceMemberDecl934 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_interfaceMemberDecl936 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_voidInterfaceMethodDeclaratorRest_in_interfaceMemberDecl938 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_interfaceDeclaration_in_interfaceMemberDecl948 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_classDeclaration_in_interfaceMemberDecl958 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_interfaceMethodOrFieldDecl975 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_interfaceMethodOrFieldDecl977 = new BitSet(new long[]{0x0840001000000000L});
	public static final BitSet FOLLOW_interfaceMethodOrFieldRest_in_interfaceMethodOrFieldDecl979 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_constantDeclaratorsRest_in_interfaceMethodOrFieldRest996 = new BitSet(new long[]{0x0010000000000000L});
	public static final BitSet FOLLOW_52_in_interfaceMethodOrFieldRest998 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_interfaceMethodDeclaratorRest_in_interfaceMethodOrFieldRest1006 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_formalParameters_in_methodDeclaratorRest1023 = new BitSet(new long[]{0x0810000000000000L,0x0202000000000000L});
	public static final BitSet FOLLOW_59_in_methodDeclaratorRest1026 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_60_in_methodDeclaratorRest1028 = new BitSet(new long[]{0x0810000000000000L,0x0202000000000000L});
	public static final BitSet FOLLOW_113_in_methodDeclaratorRest1041 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_qualifiedNameList_in_methodDeclaratorRest1043 = new BitSet(new long[]{0x0010000000000000L,0x0200000000000000L});
	public static final BitSet FOLLOW_methodBody_in_methodDeclaratorRest1059 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_52_in_methodDeclaratorRest1073 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_formalParameters_in_voidMethodDeclaratorRest1100 = new BitSet(new long[]{0x0010000000000000L,0x0202000000000000L});
	public static final BitSet FOLLOW_113_in_voidMethodDeclaratorRest1103 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_qualifiedNameList_in_voidMethodDeclaratorRest1105 = new BitSet(new long[]{0x0010000000000000L,0x0200000000000000L});
	public static final BitSet FOLLOW_methodBody_in_voidMethodDeclaratorRest1121 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_52_in_voidMethodDeclaratorRest1135 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_formalParameters_in_interfaceMethodDeclaratorRest1162 = new BitSet(new long[]{0x0810000000000000L,0x0002000000000000L});
	public static final BitSet FOLLOW_59_in_interfaceMethodDeclaratorRest1165 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_60_in_interfaceMethodDeclaratorRest1167 = new BitSet(new long[]{0x0810000000000000L,0x0002000000000000L});
	public static final BitSet FOLLOW_113_in_interfaceMethodDeclaratorRest1172 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_qualifiedNameList_in_interfaceMethodDeclaratorRest1174 = new BitSet(new long[]{0x0010000000000000L});
	public static final BitSet FOLLOW_52_in_interfaceMethodDeclaratorRest1178 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_typeParameters_in_interfaceGenericMethodDecl1195 = new BitSet(new long[]{0x0000000000020000L,0x004002005020208AL});
	public static final BitSet FOLLOW_type_in_interfaceGenericMethodDecl1198 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_118_in_interfaceGenericMethodDecl1202 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_interfaceGenericMethodDecl1205 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_interfaceMethodDeclaratorRest_in_interfaceGenericMethodDecl1215 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_formalParameters_in_voidInterfaceMethodDeclaratorRest1232 = new BitSet(new long[]{0x0010000000000000L,0x0002000000000000L});
	public static final BitSet FOLLOW_113_in_voidInterfaceMethodDeclaratorRest1235 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_qualifiedNameList_in_voidInterfaceMethodDeclaratorRest1237 = new BitSet(new long[]{0x0010000000000000L});
	public static final BitSet FOLLOW_52_in_voidInterfaceMethodDeclaratorRest1241 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_formalParameters_in_constructorDeclaratorRest1258 = new BitSet(new long[]{0x0000000000000000L,0x0202000000000000L});
	public static final BitSet FOLLOW_113_in_constructorDeclaratorRest1261 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_qualifiedNameList_in_constructorDeclaratorRest1263 = new BitSet(new long[]{0x0000000000000000L,0x0200000000000000L});
	public static final BitSet FOLLOW_methodBody_in_constructorDeclaratorRest1267 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Identifier_in_constantDeclarator1284 = new BitSet(new long[]{0x0840000000000000L});
	public static final BitSet FOLLOW_constantDeclaratorRest_in_constantDeclarator1286 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_variableDeclarator_in_variableDeclarators1303 = new BitSet(new long[]{0x0000080000000002L});
	public static final BitSet FOLLOW_43_in_variableDeclarators1306 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_variableDeclarator_in_variableDeclarators1308 = new BitSet(new long[]{0x0000080000000002L});
	public static final BitSet FOLLOW_Identifier_in_variableDeclarator1355 = new BitSet(new long[]{0x0840000000000000L});
	public static final BitSet FOLLOW_variableDeclaratorRest_in_variableDeclarator1359 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_59_in_variableDeclaratorRest1387 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_60_in_variableDeclaratorRest1389 = new BitSet(new long[]{0x0840000000000002L});
	public static final BitSet FOLLOW_54_in_variableDeclaratorRest1394 = new BitSet(new long[]{0x0020331023034060L,0x424892065025A0CAL});
	public static final BitSet FOLLOW_variableInitializer_in_variableDeclaratorRest1396 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_54_in_variableDeclaratorRest1406 = new BitSet(new long[]{0x0020331023034060L,0x424892065025A0CAL});
	public static final BitSet FOLLOW_variableInitializer_in_variableDeclaratorRest1408 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_constantDeclaratorRest_in_constantDeclaratorsRest1433 = new BitSet(new long[]{0x0000080000000002L});
	public static final BitSet FOLLOW_43_in_constantDeclaratorsRest1436 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_constantDeclarator_in_constantDeclaratorsRest1438 = new BitSet(new long[]{0x0000080000000002L});
	public static final BitSet FOLLOW_59_in_constantDeclaratorRest1458 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_60_in_constantDeclaratorRest1460 = new BitSet(new long[]{0x0840000000000000L});
	public static final BitSet FOLLOW_54_in_constantDeclaratorRest1464 = new BitSet(new long[]{0x0020331023034060L,0x424892065025A0CAL});
	public static final BitSet FOLLOW_variableInitializer_in_constantDeclaratorRest1466 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Identifier_in_variableDeclaratorId1483 = new BitSet(new long[]{0x0800000000000002L});
	public static final BitSet FOLLOW_59_in_variableDeclaratorId1486 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_60_in_variableDeclaratorId1488 = new BitSet(new long[]{0x0800000000000002L});
	public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer1507 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_variableInitializer1517 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_121_in_arrayInitializer1534 = new BitSet(new long[]{0x0020331023034060L,0x624892065025A0CAL});
	public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer1537 = new BitSet(new long[]{0x0000080000000000L,0x2000000000000000L});
	public static final BitSet FOLLOW_43_in_arrayInitializer1540 = new BitSet(new long[]{0x0020331023034060L,0x424892065025A0CAL});
	public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer1542 = new BitSet(new long[]{0x0000080000000000L,0x2000000000000000L});
	public static final BitSet FOLLOW_43_in_arrayInitializer1547 = new BitSet(new long[]{0x0000000000000000L,0x2000000000000000L});
	public static final BitSet FOLLOW_125_in_arrayInitializer1554 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotation_in_modifier1573 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_102_in_modifier1583 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_101_in_modifier1593 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_100_in_modifier1603 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_106_in_modifier1613 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_63_in_modifier1623 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_83_in_modifier1633 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_96_in_modifier1643 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_110_in_modifier1653 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_114_in_modifier1663 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_119_in_modifier1673 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_107_in_modifier1683 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Identifier_in_packageOrTypeName1700 = new BitSet(new long[]{0x0000800000000002L});
	public static final BitSet FOLLOW_47_in_packageOrTypeName1703 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_packageOrTypeName1705 = new BitSet(new long[]{0x0000800000000002L});
	public static final BitSet FOLLOW_Identifier_in_enumConstantName1726 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Identifier_in_typeName1745 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_packageOrTypeName_in_typeName1755 = new BitSet(new long[]{0x0000800000000000L});
	public static final BitSet FOLLOW_47_in_typeName1757 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_typeName1759 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Identifier_in_type1776 = new BitSet(new long[]{0x0820800000000002L});
	public static final BitSet FOLLOW_typeArguments_in_type1779 = new BitSet(new long[]{0x0800800000000002L});
	public static final BitSet FOLLOW_47_in_type1784 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_type1786 = new BitSet(new long[]{0x0820800000000002L});
	public static final BitSet FOLLOW_typeArguments_in_type1789 = new BitSet(new long[]{0x0800800000000002L});
	public static final BitSet FOLLOW_59_in_type1797 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_60_in_type1799 = new BitSet(new long[]{0x0800000000000002L});
	public static final BitSet FOLLOW_primitiveType_in_type1809 = new BitSet(new long[]{0x0800000000000002L});
	public static final BitSet FOLLOW_59_in_type1812 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_60_in_type1814 = new BitSet(new long[]{0x0800000000000002L});
	public static final BitSet FOLLOW_83_in_variableModifier1908 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotation_in_variableModifier1918 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_53_in_typeArguments1935 = new BitSet(new long[]{0x0200000000020000L,0x000002005020208AL});
	public static final BitSet FOLLOW_typeArgument_in_typeArguments1937 = new BitSet(new long[]{0x0100080000000000L});
	public static final BitSet FOLLOW_43_in_typeArguments1940 = new BitSet(new long[]{0x0200000000020000L,0x000002005020208AL});
	public static final BitSet FOLLOW_typeArgument_in_typeArguments1942 = new BitSet(new long[]{0x0100080000000000L});
	public static final BitSet FOLLOW_56_in_typeArguments1946 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_typeArgument1963 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_57_in_typeArgument1971 = new BitSet(new long[]{0x0000000000000002L,0x0000100000020000L});
	public static final BitSet FOLLOW_set_in_typeArgument1974 = new BitSet(new long[]{0x0000000000020000L,0x000002005020208AL});
	public static final BitSet FOLLOW_type_in_typeArgument1982 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList2001 = new BitSet(new long[]{0x0000080000000002L});
	public static final BitSet FOLLOW_43_in_qualifiedNameList2004 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList2006 = new BitSet(new long[]{0x0000080000000002L});
	public static final BitSet FOLLOW_36_in_formalParameters2025 = new BitSet(new long[]{0x0400002000020000L,0x000002005028208AL});
	public static final BitSet FOLLOW_formalParameterDecls_in_formalParameters2027 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_37_in_formalParameters2030 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_variableModifier_in_formalParameterDecls2047 = new BitSet(new long[]{0x0400000000020000L,0x000002005028208AL});
	public static final BitSet FOLLOW_type_in_formalParameterDecls2050 = new BitSet(new long[]{0x0001000000020002L});
	public static final BitSet FOLLOW_formalParameterDeclsRest_in_formalParameterDecls2052 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest2070 = new BitSet(new long[]{0x0000080000000002L});
	public static final BitSet FOLLOW_43_in_formalParameterDeclsRest2073 = new BitSet(new long[]{0x0400000000020000L,0x000002005028208AL});
	public static final BitSet FOLLOW_formalParameterDecls_in_formalParameterDeclsRest2075 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_48_in_formalParameterDeclsRest2087 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest2089 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_block_in_methodBody2106 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Identifier_in_qualifiedName2123 = new BitSet(new long[]{0x0000800000000002L});
	public static final BitSet FOLLOW_47_in_qualifiedName2126 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_qualifiedName2128 = new BitSet(new long[]{0x0000800000000002L});
	public static final BitSet FOLLOW_integerLiteral_in_literal2150 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FloatingPointLiteral_in_literal2160 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CharacterLiteral_in_literal2170 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_StringLiteral_in_literal2180 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_booleanLiteral_in_literal2190 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_98_in_literal2200 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotation_in_annotations2287 = new BitSet(new long[]{0x0400000000000002L});
	public static final BitSet FOLLOW_58_in_annotation2305 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_annotationName_in_annotation2307 = new BitSet(new long[]{0x0000001000000002L});
	public static final BitSet FOLLOW_36_in_annotation2310 = new BitSet(new long[]{0x0420333023034060L,0x424892065025A0CAL});
	public static final BitSet FOLLOW_elementValuePairs_in_annotation2312 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_37_in_annotation2315 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Identifier_in_annotationName2334 = new BitSet(new long[]{0x0000800000000002L});
	public static final BitSet FOLLOW_47_in_annotationName2337 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_annotationName2339 = new BitSet(new long[]{0x0000800000000002L});
	public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs2358 = new BitSet(new long[]{0x0000080000000002L});
	public static final BitSet FOLLOW_43_in_elementValuePairs2361 = new BitSet(new long[]{0x0420331023034060L,0x424892065025A0CAL});
	public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs2363 = new BitSet(new long[]{0x0000080000000002L});
	public static final BitSet FOLLOW_Identifier_in_elementValuePair2383 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_54_in_elementValuePair2385 = new BitSet(new long[]{0x0420331023034060L,0x424892065025A0CAL});
	public static final BitSet FOLLOW_elementValue_in_elementValuePair2389 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_conditionalExpression_in_elementValue2406 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotation_in_elementValue2416 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_elementValueArrayInitializer_in_elementValue2426 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_121_in_elementValueArrayInitializer2443 = new BitSet(new long[]{0x0420331023034060L,0x624892065025A0CAL});
	public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer2446 = new BitSet(new long[]{0x0000080000000000L,0x2000000000000000L});
	public static final BitSet FOLLOW_43_in_elementValueArrayInitializer2449 = new BitSet(new long[]{0x0420331023034060L,0x424892065025A0CAL});
	public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer2451 = new BitSet(new long[]{0x0000080000000000L,0x2000000000000000L});
	public static final BitSet FOLLOW_125_in_elementValueArrayInitializer2458 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_58_in_annotationTypeDeclaration2475 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
	public static final BitSet FOLLOW_93_in_annotationTypeDeclaration2477 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_annotationTypeDeclaration2479 = new BitSet(new long[]{0x0000000000000000L,0x0200000000000000L});
	public static final BitSet FOLLOW_annotationTypeBody_in_annotationTypeDeclaration2481 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_121_in_annotationTypeBody2498 = new BitSet(new long[]{0x8400000000020400L,0x20844E717028218AL});
	public static final BitSet FOLLOW_annotationTypeElementDeclarations_in_annotationTypeBody2501 = new BitSet(new long[]{0x0000000000000000L,0x2000000000000000L});
	public static final BitSet FOLLOW_125_in_annotationTypeBody2505 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotationTypeElementDeclaration_in_annotationTypeElementDeclarations2523 = new BitSet(new long[]{0x8400000000020402L,0x00844E717028218AL});
	public static final BitSet FOLLOW_annotationTypeElementDeclaration_in_annotationTypeElementDeclarations2527 = new BitSet(new long[]{0x8400000000020402L,0x00844E717028218AL});
	public static final BitSet FOLLOW_modifier_in_annotationTypeElementDeclaration2547 = new BitSet(new long[]{0x8400000000020400L,0x00844E717028218AL});
	public static final BitSet FOLLOW_annotationTypeElementRest_in_annotationTypeElementDeclaration2551 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_annotationTypeElementRest2568 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_annotationMethodOrConstantRest_in_annotationTypeElementRest2570 = new BitSet(new long[]{0x0010000000000000L});
	public static final BitSet FOLLOW_52_in_annotationTypeElementRest2572 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_classDeclaration_in_annotationTypeElementRest2582 = new BitSet(new long[]{0x0010000000000002L});
	public static final BitSet FOLLOW_52_in_annotationTypeElementRest2584 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_interfaceDeclaration_in_annotationTypeElementRest2595 = new BitSet(new long[]{0x0010000000000002L});
	public static final BitSet FOLLOW_52_in_annotationTypeElementRest2597 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_enumDeclaration_in_annotationTypeElementRest2608 = new BitSet(new long[]{0x0010000000000002L});
	public static final BitSet FOLLOW_52_in_annotationTypeElementRest2610 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotationTypeDeclaration_in_annotationTypeElementRest2621 = new BitSet(new long[]{0x0010000000000002L});
	public static final BitSet FOLLOW_52_in_annotationTypeElementRest2623 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotationMethodRest_in_annotationMethodOrConstantRest2641 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotationConstantRest_in_annotationMethodOrConstantRest2651 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Identifier_in_annotationMethodRest2669 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_36_in_annotationMethodRest2671 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_37_in_annotationMethodRest2673 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000400L});
	public static final BitSet FOLLOW_defaultValue_in_annotationMethodRest2676 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_variableDeclarators_in_annotationConstantRest2702 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_74_in_defaultValue2721 = new BitSet(new long[]{0x0420331023034060L,0x424892065025A0CAL});
	public static final BitSet FOLLOW_elementValue_in_defaultValue2723 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_121_in_block2769 = new BitSet(new long[]{0x8430331023034460L,0x63FDFFF7F4EDBBCFL});
	public static final BitSet FOLLOW_blockStatement_in_block2771 = new BitSet(new long[]{0x8430331023034460L,0x63FDFFF7F4EDBBCFL});
	public static final BitSet FOLLOW_125_in_block2774 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_localVariableDeclaration_in_blockStatement2791 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_blockStatement2799 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_statement_in_blockStatement2811 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_variableModifier_in_localVariableDeclaration2865 = new BitSet(new long[]{0x0400000000020000L,0x000002005028208AL});
	public static final BitSet FOLLOW_type_in_localVariableDeclaration2888 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_variableDeclarators_in_localVariableDeclaration2904 = new BitSet(new long[]{0x0010000000000000L});
	public static final BitSet FOLLOW_52_in_localVariableDeclaration2906 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_block_in_statement2923 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_64_in_statement2931 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_statement2933 = new BitSet(new long[]{0x0018000000000000L});
	public static final BitSet FOLLOW_51_in_statement2936 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_statement2938 = new BitSet(new long[]{0x0010000000000000L});
	public static final BitSet FOLLOW_52_in_statement2942 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ifStatement_in_statement2950 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_forStatement_in_statement2958 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_whileStatement_in_statement2967 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_76_in_statement2975 = new BitSet(new long[]{0x0030331023034060L,0x4379F386D4E5BACFL});
	public static final BitSet FOLLOW_statement_in_statement2977 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
	public static final BitSet FOLLOW_120_in_statement2979 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_parExpression_in_statement2981 = new BitSet(new long[]{0x0010000000000000L});
	public static final BitSet FOLLOW_52_in_statement2983 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_tryStatement_in_statement2996 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_109_in_statement3011 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_parExpression_in_statement3013 = new BitSet(new long[]{0x0000000000000000L,0x0200000000000000L});
	public static final BitSet FOLLOW_121_in_statement3015 = new BitSet(new long[]{0x0000000000000000L,0x2000000000000410L});
	public static final BitSet FOLLOW_switchBlockStatementGroups_in_statement3017 = new BitSet(new long[]{0x0000000000000000L,0x2000000000000000L});
	public static final BitSet FOLLOW_125_in_statement3019 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_110_in_statement3027 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_parExpression_in_statement3029 = new BitSet(new long[]{0x0000000000000000L,0x0200000000000000L});
	public static final BitSet FOLLOW_block_in_statement3031 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_104_in_statement3039 = new BitSet(new long[]{0x0030331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_statement3041 = new BitSet(new long[]{0x0010000000000000L});
	public static final BitSet FOLLOW_52_in_statement3044 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_throwStatement_in_statement3053 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_66_in_statement3061 = new BitSet(new long[]{0x0010000000020000L});
	public static final BitSet FOLLOW_Identifier_in_statement3063 = new BitSet(new long[]{0x0010000000000000L});
	public static final BitSet FOLLOW_52_in_statement3066 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_73_in_statement3074 = new BitSet(new long[]{0x0010000000020000L});
	public static final BitSet FOLLOW_Identifier_in_statement3076 = new BitSet(new long[]{0x0010000000000000L});
	public static final BitSet FOLLOW_52_in_statement3079 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifyStatement_in_statement3092 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_updateStatement_in_statement3096 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_retractStatement_in_statement3100 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_deleteStatement_in_statement3104 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_insertStatement_in_statement3108 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_52_in_statement3116 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_statementExpression_in_statement3124 = new BitSet(new long[]{0x0010000000000000L});
	public static final BitSet FOLLOW_52_in_statement3126 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Identifier_in_statement3134 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_51_in_statement3136 = new BitSet(new long[]{0x0030331023034060L,0x4379F386D4E5BACFL});
	public static final BitSet FOLLOW_statement_in_statement3138 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_112_in_throwStatement3171 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_throwStatement3177 = new BitSet(new long[]{0x0010000000000000L});
	public static final BitSet FOLLOW_52_in_throwStatement3187 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_87_in_ifStatement3243 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_parExpression_in_ifStatement3245 = new BitSet(new long[]{0x0030331023034060L,0x4379F386D4E5BACFL});
	public static final BitSet FOLLOW_statement_in_ifStatement3263 = new BitSet(new long[]{0x0000000000000002L,0x0000000000004000L});
	public static final BitSet FOLLOW_78_in_ifStatement3290 = new BitSet(new long[]{0x0030331023034060L,0x4379F386D4E5BACFL});
	public static final BitSet FOLLOW_87_in_ifStatement3294 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_parExpression_in_ifStatement3296 = new BitSet(new long[]{0x0030331023034060L,0x4379F386D4E5BACFL});
	public static final BitSet FOLLOW_statement_in_ifStatement3327 = new BitSet(new long[]{0x0000000000000002L,0x0000000000004000L});
	public static final BitSet FOLLOW_86_in_forStatement3409 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_36_in_forStatement3413 = new BitSet(new long[]{0x0430331023034060L,0x40489206502DA0CAL});
	public static final BitSet FOLLOW_variableModifier_in_forStatement3449 = new BitSet(new long[]{0x0400000000020000L,0x000002005028208AL});
	public static final BitSet FOLLOW_type_in_forStatement3488 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_forStatement3514 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_51_in_forStatement3541 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_forStatement3543 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_forInit_in_forStatement3579 = new BitSet(new long[]{0x0010000000000000L});
	public static final BitSet FOLLOW_52_in_forStatement3584 = new BitSet(new long[]{0x0030331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_forStatement3586 = new BitSet(new long[]{0x0010000000000000L});
	public static final BitSet FOLLOW_52_in_forStatement3589 = new BitSet(new long[]{0x0020333023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_forUpdate_in_forStatement3591 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_37_in_forStatement3637 = new BitSet(new long[]{0x0030331023034060L,0x4379F386D4E5BACFL});
	public static final BitSet FOLLOW_statement_in_forStatement3641 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_120_in_whileStatement3700 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_parExpression_in_whileStatement3702 = new BitSet(new long[]{0x0030331023034060L,0x4379F386D4E5BACFL});
	public static final BitSet FOLLOW_statement_in_whileStatement3719 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_116_in_tryStatement3772 = new BitSet(new long[]{0x0000000000000000L,0x0200000000000000L});
	public static final BitSet FOLLOW_121_in_tryStatement3783 = new BitSet(new long[]{0x8430331023034460L,0x63FDFFF7F4EDBBCFL});
	public static final BitSet FOLLOW_blockStatement_in_tryStatement3785 = new BitSet(new long[]{0x8430331023034460L,0x63FDFFF7F4EDBBCFL});
	public static final BitSet FOLLOW_125_in_tryStatement3796 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100020L});
	public static final BitSet FOLLOW_69_in_tryStatement3814 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_36_in_tryStatement3816 = new BitSet(new long[]{0x0400000000020000L,0x000002005028208AL});
	public static final BitSet FOLLOW_formalParameter_in_tryStatement3818 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_37_in_tryStatement3820 = new BitSet(new long[]{0x0000000000000000L,0x0200000000000000L});
	public static final BitSet FOLLOW_121_in_tryStatement3832 = new BitSet(new long[]{0x8430331023034460L,0x63FDFFF7F4EDBBCFL});
	public static final BitSet FOLLOW_blockStatement_in_tryStatement3834 = new BitSet(new long[]{0x8430331023034460L,0x63FDFFF7F4EDBBCFL});
	public static final BitSet FOLLOW_125_in_tryStatement3847 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100020L});
	public static final BitSet FOLLOW_84_in_tryStatement3881 = new BitSet(new long[]{0x0000000000000000L,0x0200000000000000L});
	public static final BitSet FOLLOW_121_in_tryStatement3893 = new BitSet(new long[]{0x8430331023034460L,0x63FDFFF7F4EDBBCFL});
	public static final BitSet FOLLOW_blockStatement_in_tryStatement3895 = new BitSet(new long[]{0x8430331023034460L,0x63FDFFF7F4EDBBCFL});
	public static final BitSet FOLLOW_125_in_tryStatement3909 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_95_in_modifyStatement3951 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_parExpression_in_modifyStatement3953 = new BitSet(new long[]{0x0000000000000000L,0x0200000000000000L});
	public static final BitSet FOLLOW_121_in_modifyStatement3965 = new BitSet(new long[]{0x0020331023034060L,0x604892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_modifyStatement3973 = new BitSet(new long[]{0x0000080000000000L,0x2000000000000000L});
	public static final BitSet FOLLOW_43_in_modifyStatement3989 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_modifyStatement3993 = new BitSet(new long[]{0x0000080000000000L,0x2000000000000000L});
	public static final BitSet FOLLOW_125_in_modifyStatement4017 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_117_in_updateStatement4046 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_36_in_updateStatement4048 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_updateStatement4054 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_37_in_updateStatement4064 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_103_in_retractStatement4097 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_36_in_retractStatement4099 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_retractStatement4105 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_37_in_retractStatement4115 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_75_in_deleteStatement4144 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_36_in_deleteStatement4146 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_deleteStatement4152 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_37_in_deleteStatement4162 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_90_in_insertStatement4191 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_36_in_insertStatement4193 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_insertStatement4199 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_37_in_insertStatement4209 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_80_in_epStatement4262 = new BitSet(new long[]{0x0800000000000000L});
	public static final BitSet FOLLOW_59_in_epStatement4264 = new BitSet(new long[]{0x0000000002000000L});
	public static final BitSet FOLLOW_StringLiteral_in_epStatement4268 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_60_in_epStatement4272 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_79_in_epStatement4298 = new BitSet(new long[]{0x0800000000000000L});
	public static final BitSet FOLLOW_59_in_epStatement4300 = new BitSet(new long[]{0x0000000002000000L});
	public static final BitSet FOLLOW_StringLiteral_in_epStatement4304 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_60_in_epStatement4308 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_70_in_epStatement4334 = new BitSet(new long[]{0x0800000000000000L});
	public static final BitSet FOLLOW_59_in_epStatement4336 = new BitSet(new long[]{0x0000000002000000L});
	public static final BitSet FOLLOW_StringLiteral_in_epStatement4340 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_60_in_epStatement4344 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_variableModifier_in_formalParameter4388 = new BitSet(new long[]{0x0400000000020000L,0x000002005028208AL});
	public static final BitSet FOLLOW_type_in_formalParameter4391 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameter4393 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups4411 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000410L});
	public static final BitSet FOLLOW_switchLabel_in_switchBlockStatementGroup4430 = new BitSet(new long[]{0x8430331023034462L,0x43FDFFF7F4EDBBCFL});
	public static final BitSet FOLLOW_blockStatement_in_switchBlockStatementGroup4432 = new BitSet(new long[]{0x8430331023034462L,0x43FDFFF7F4EDBBCFL});
	public static final BitSet FOLLOW_68_in_switchLabel4450 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_constantExpression_in_switchLabel4452 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_51_in_switchLabel4454 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_68_in_switchLabel4464 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_enumConstantName_in_switchLabel4466 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_51_in_switchLabel4468 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_74_in_switchLabel4478 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_51_in_switchLabel4480 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_43_in_moreStatementExpressions4498 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_statementExpression_in_moreStatementExpressions4500 = new BitSet(new long[]{0x0000080000000002L});
	public static final BitSet FOLLOW_forVarControl_in_forControl4558 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_forInit_in_forControl4566 = new BitSet(new long[]{0x0010000000000000L});
	public static final BitSet FOLLOW_52_in_forControl4569 = new BitSet(new long[]{0x0030331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_forControl4571 = new BitSet(new long[]{0x0010000000000000L});
	public static final BitSet FOLLOW_52_in_forControl4574 = new BitSet(new long[]{0x0020331023034062L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_forUpdate_in_forControl4576 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_variableModifier_in_forInit4596 = new BitSet(new long[]{0x0400000000020000L,0x000002005028208AL});
	public static final BitSet FOLLOW_type_in_forInit4631 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_variableDeclarators_in_forInit4651 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expressionList_in_forInit4659 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_variableModifier_in_forVarControl4678 = new BitSet(new long[]{0x0400000000020000L,0x000002005028208AL});
	public static final BitSet FOLLOW_type_in_forVarControl4713 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_forVarControl4735 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_51_in_forVarControl4756 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_forVarControl4758 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expressionList_in_forUpdate4775 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_36_in_parExpression4794 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_parExpression4796 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_37_in_parExpression4798 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_expressionList4817 = new BitSet(new long[]{0x0000080000000002L});
	public static final BitSet FOLLOW_43_in_expressionList4820 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_expressionList4822 = new BitSet(new long[]{0x0000080000000002L});
	public static final BitSet FOLLOW_expression_in_statementExpression4841 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_constantExpression4858 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_assignmentExpression_in_expression4875 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_conditionalExpression_in_expression4885 = new BitSet(new long[]{0x4124448900000002L,0x0800000000000000L});
	public static final BitSet FOLLOW_assignmentOperator_in_expression4888 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_expression4890 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_conditionalExpression_in_assignmentExpression4911 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_54_in_assignmentExpression4913 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_assignmentExpression4915 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_42_in_assignmentOperator4942 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_46_in_assignmentOperator4952 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_39_in_assignmentOperator4962 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_50_in_assignmentOperator4972 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_35_in_assignmentOperator4982 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_123_in_assignmentOperator4992 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_62_in_assignmentOperator5002 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_32_in_assignmentOperator5012 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_53_in_assignmentOperator5022 = new BitSet(new long[]{0x0020000000000000L});
	public static final BitSet FOLLOW_53_in_assignmentOperator5024 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_54_in_assignmentOperator5026 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_56_in_assignmentOperator5036 = new BitSet(new long[]{0x0100000000000000L});
	public static final BitSet FOLLOW_56_in_assignmentOperator5038 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_54_in_assignmentOperator5040 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_56_in_assignmentOperator5050 = new BitSet(new long[]{0x0100000000000000L});
	public static final BitSet FOLLOW_56_in_assignmentOperator5052 = new BitSet(new long[]{0x0100000000000000L});
	public static final BitSet FOLLOW_56_in_assignmentOperator5054 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_54_in_assignmentOperator5056 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression5075 = new BitSet(new long[]{0x0200000000000002L});
	public static final BitSet FOLLOW_57_in_conditionalExpression5079 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_conditionalExpression5081 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_51_in_conditionalExpression5083 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_conditionalExpression5085 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression5107 = new BitSet(new long[]{0x0000000000000002L,0x1000000000000000L});
	public static final BitSet FOLLOW_124_in_conditionalOrExpression5111 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression5113 = new BitSet(new long[]{0x0000000000000002L,0x1000000000000000L});
	public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5135 = new BitSet(new long[]{0x0000000200000002L});
	public static final BitSet FOLLOW_33_in_conditionalAndExpression5139 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5141 = new BitSet(new long[]{0x0000000200000002L});
	public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5163 = new BitSet(new long[]{0x0000000000000002L,0x0400000000000000L});
	public static final BitSet FOLLOW_122_in_inclusiveOrExpression5167 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5169 = new BitSet(new long[]{0x0000000000000002L,0x0400000000000000L});
	public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression5191 = new BitSet(new long[]{0x2000000000000002L});
	public static final BitSet FOLLOW_61_in_exclusiveOrExpression5195 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression5197 = new BitSet(new long[]{0x2000000000000002L});
	public static final BitSet FOLLOW_equalityExpression_in_andExpression5219 = new BitSet(new long[]{0x0000000400000002L});
	public static final BitSet FOLLOW_34_in_andExpression5223 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_equalityExpression_in_andExpression5225 = new BitSet(new long[]{0x0000000400000002L});
	public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression5247 = new BitSet(new long[]{0x0080000040000002L});
	public static final BitSet FOLLOW_set_in_equalityExpression5251 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression5259 = new BitSet(new long[]{0x0080000040000002L});
	public static final BitSet FOLLOW_relationalExpression_in_instanceOfExpression5281 = new BitSet(new long[]{0x0000000000000002L,0x0000000008000000L});
	public static final BitSet FOLLOW_91_in_instanceOfExpression5284 = new BitSet(new long[]{0x0000000000020000L,0x000002005020208AL});
	public static final BitSet FOLLOW_type_in_instanceOfExpression5286 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_shiftExpression_in_relationalExpression5307 = new BitSet(new long[]{0x0120000000000002L});
	public static final BitSet FOLLOW_relationalOp_in_relationalExpression5311 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_shiftExpression_in_relationalExpression5313 = new BitSet(new long[]{0x0120000000000002L});
	public static final BitSet FOLLOW_53_in_relationalOp5334 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_54_in_relationalOp5336 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_56_in_relationalOp5340 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_54_in_relationalOp5342 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_53_in_relationalOp5346 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_56_in_relationalOp5350 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_additiveExpression_in_shiftExpression5370 = new BitSet(new long[]{0x0120000000000002L});
	public static final BitSet FOLLOW_shiftOp_in_shiftExpression5374 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_additiveExpression_in_shiftExpression5376 = new BitSet(new long[]{0x0120000000000002L});
	public static final BitSet FOLLOW_53_in_shiftOp5406 = new BitSet(new long[]{0x0020000000000000L});
	public static final BitSet FOLLOW_53_in_shiftOp5408 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_56_in_shiftOp5412 = new BitSet(new long[]{0x0100000000000000L});
	public static final BitSet FOLLOW_56_in_shiftOp5414 = new BitSet(new long[]{0x0100000000000000L});
	public static final BitSet FOLLOW_56_in_shiftOp5416 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_56_in_shiftOp5420 = new BitSet(new long[]{0x0100000000000000L});
	public static final BitSet FOLLOW_56_in_shiftOp5422 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression5443 = new BitSet(new long[]{0x0000110000000002L});
	public static final BitSet FOLLOW_set_in_additiveExpression5447 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression5455 = new BitSet(new long[]{0x0000110000000002L});
	public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression5477 = new BitSet(new long[]{0x0002004080000002L});
	public static final BitSet FOLLOW_set_in_multiplicativeExpression5481 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression5495 = new BitSet(new long[]{0x0002004080000002L});
	public static final BitSet FOLLOW_40_in_unaryExpression5517 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpression5519 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_44_in_unaryExpression5527 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpression5529 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_41_in_unaryExpression5539 = new BitSet(new long[]{0x0020001003034060L,0x004892065025A0CAL});
	public static final BitSet FOLLOW_primary_in_unaryExpression5541 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_45_in_unaryExpression5551 = new BitSet(new long[]{0x0020001003034060L,0x004892065025A0CAL});
	public static final BitSet FOLLOW_primary_in_unaryExpression5553 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression5563 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_126_in_unaryExpressionNotPlusMinus5582 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5584 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_29_in_unaryExpressionNotPlusMinus5593 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5595 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus5605 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus5615 = new BitSet(new long[]{0x0800A20000000002L});
	public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus5617 = new BitSet(new long[]{0x0800A20000000002L});
	public static final BitSet FOLLOW_36_in_castExpression5643 = new BitSet(new long[]{0x0000000000000000L,0x000002005020208AL});
	public static final BitSet FOLLOW_primitiveType_in_castExpression5645 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_37_in_castExpression5647 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_unaryExpression_in_castExpression5649 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_36_in_castExpression5658 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_type_in_castExpression5661 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_expression_in_castExpression5665 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_37_in_castExpression5668 = new BitSet(new long[]{0x0020001023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression5670 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_parExpression_in_primary5687 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_primary5697 = new BitSet(new long[]{0x0000000000020000L,0x0000900000000000L});
	public static final BitSet FOLLOW_explicitGenericInvocationSuffix_in_primary5708 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_111_in_primary5712 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_arguments_in_primary5714 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_111_in_primary5725 = new BitSet(new long[]{0x0800801000000002L});
	public static final BitSet FOLLOW_47_in_primary5728 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_primary5730 = new BitSet(new long[]{0x0800801000000002L});
	public static final BitSet FOLLOW_identifierSuffix_in_primary5735 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_108_in_primary5747 = new BitSet(new long[]{0x0000801000000000L});
	public static final BitSet FOLLOW_superSuffix_in_primary5749 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_epStatement_in_primary5759 = new BitSet(new long[]{0x0800801000000002L});
	public static final BitSet FOLLOW_47_in_primary5762 = new BitSet(new long[]{0x0000000000020000L,0x0020008084000800L});
	public static final BitSet FOLLOW_methodName_in_primary5764 = new BitSet(new long[]{0x0800801000000002L});
	public static final BitSet FOLLOW_identifierSuffix_in_primary5769 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literal_in_primary5781 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_97_in_primary5791 = new BitSet(new long[]{0x0020000000020000L,0x000002005020208AL});
	public static final BitSet FOLLOW_creator_in_primary5793 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Identifier_in_primary5805 = new BitSet(new long[]{0x0800801000000002L});
	public static final BitSet FOLLOW_47_in_primary5810 = new BitSet(new long[]{0x0000000000020000L,0x0020008084000800L});
	public static final BitSet FOLLOW_methodName_in_primary5812 = new BitSet(new long[]{0x0800801000000002L});
	public static final BitSet FOLLOW_identifierSuffix_in_primary5817 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_primitiveType_in_primary5829 = new BitSet(new long[]{0x0800800000000000L});
	public static final BitSet FOLLOW_59_in_primary5832 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_60_in_primary5834 = new BitSet(new long[]{0x0800800000000000L});
	public static final BitSet FOLLOW_47_in_primary5838 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
	public static final BitSet FOLLOW_72_in_primary5840 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_118_in_primary5850 = new BitSet(new long[]{0x0000800000000000L});
	public static final BitSet FOLLOW_47_in_primary5852 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
	public static final BitSet FOLLOW_72_in_primary5854 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_59_in_identifierSuffix5909 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_60_in_identifierSuffix5911 = new BitSet(new long[]{0x0800800000000000L});
	public static final BitSet FOLLOW_47_in_identifierSuffix5915 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
	public static final BitSet FOLLOW_72_in_identifierSuffix5917 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_59_in_identifierSuffix5926 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_identifierSuffix5928 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_60_in_identifierSuffix5930 = new BitSet(new long[]{0x0800000000000002L});
	public static final BitSet FOLLOW_arguments_in_identifierSuffix5943 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_47_in_identifierSuffix5953 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
	public static final BitSet FOLLOW_72_in_identifierSuffix5955 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_47_in_identifierSuffix5965 = new BitSet(new long[]{0x0020000000000000L});
	public static final BitSet FOLLOW_explicitGenericInvocation_in_identifierSuffix5967 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_47_in_identifierSuffix5977 = new BitSet(new long[]{0x0000000000000000L,0x0000800000000000L});
	public static final BitSet FOLLOW_111_in_identifierSuffix5979 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_47_in_identifierSuffix5989 = new BitSet(new long[]{0x0000000000000000L,0x0000100000000000L});
	public static final BitSet FOLLOW_108_in_identifierSuffix5991 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_arguments_in_identifierSuffix5993 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_47_in_identifierSuffix6003 = new BitSet(new long[]{0x0000000000000000L,0x0000000200000000L});
	public static final BitSet FOLLOW_97_in_identifierSuffix6005 = new BitSet(new long[]{0x0020000000020000L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_identifierSuffix6008 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_innerCreator_in_identifierSuffix6012 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator6029 = new BitSet(new long[]{0x0000000000020000L,0x000002005020208AL});
	public static final BitSet FOLLOW_createdName_in_creator6032 = new BitSet(new long[]{0x0800001000000000L});
	public static final BitSet FOLLOW_arrayCreatorRest_in_creator6043 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_classCreatorRest_in_creator6047 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Identifier_in_createdName6065 = new BitSet(new long[]{0x0020800000000002L});
	public static final BitSet FOLLOW_typeArguments_in_createdName6067 = new BitSet(new long[]{0x0000800000000002L});
	public static final BitSet FOLLOW_47_in_createdName6079 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_createdName6081 = new BitSet(new long[]{0x0020800000000002L});
	public static final BitSet FOLLOW_typeArguments_in_createdName6083 = new BitSet(new long[]{0x0000800000000002L});
	public static final BitSet FOLLOW_primitiveType_in_createdName6094 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Identifier_in_innerCreator6111 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_classCreatorRest_in_innerCreator6113 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_59_in_arrayCreatorRest6130 = new BitSet(new long[]{0x1020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_60_in_arrayCreatorRest6144 = new BitSet(new long[]{0x0800000000000000L,0x0200000000000000L});
	public static final BitSet FOLLOW_59_in_arrayCreatorRest6147 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_60_in_arrayCreatorRest6149 = new BitSet(new long[]{0x0800000000000000L,0x0200000000000000L});
	public static final BitSet FOLLOW_arrayInitializer_in_arrayCreatorRest6153 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_arrayCreatorRest6167 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_60_in_arrayCreatorRest6169 = new BitSet(new long[]{0x0800000000000002L});
	public static final BitSet FOLLOW_59_in_arrayCreatorRest6172 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_arrayCreatorRest6174 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_60_in_arrayCreatorRest6176 = new BitSet(new long[]{0x0800000000000002L});
	public static final BitSet FOLLOW_59_in_arrayCreatorRest6181 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_60_in_arrayCreatorRest6183 = new BitSet(new long[]{0x0800000000000002L});
	public static final BitSet FOLLOW_arguments_in_classCreatorRest6212 = new BitSet(new long[]{0x0000000000000002L,0x0200000000000000L});
	public static final BitSet FOLLOW_classBody_in_classCreatorRest6214 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation6232 = new BitSet(new long[]{0x0000000000020000L,0x0000100000000000L});
	public static final BitSet FOLLOW_explicitGenericInvocationSuffix_in_explicitGenericInvocation6234 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_53_in_nonWildcardTypeArguments6251 = new BitSet(new long[]{0x0000000000020000L,0x000002005020208AL});
	public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments6253 = new BitSet(new long[]{0x0100000000000000L});
	public static final BitSet FOLLOW_56_in_nonWildcardTypeArguments6255 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_108_in_explicitGenericInvocationSuffix6272 = new BitSet(new long[]{0x0000801000000000L});
	public static final BitSet FOLLOW_superSuffix_in_explicitGenericInvocationSuffix6274 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Identifier_in_explicitGenericInvocationSuffix6284 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_arguments_in_explicitGenericInvocationSuffix6286 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_47_in_selector6303 = new BitSet(new long[]{0x0000000000020000L,0x0020008084000800L});
	public static final BitSet FOLLOW_methodName_in_selector6305 = new BitSet(new long[]{0x0000001000000002L});
	public static final BitSet FOLLOW_arguments_in_selector6308 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_47_in_selector6320 = new BitSet(new long[]{0x0000000000000000L,0x0000800000000000L});
	public static final BitSet FOLLOW_111_in_selector6322 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_47_in_selector6332 = new BitSet(new long[]{0x0000000000000000L,0x0000100000000000L});
	public static final BitSet FOLLOW_108_in_selector6334 = new BitSet(new long[]{0x0000801000000000L});
	public static final BitSet FOLLOW_superSuffix_in_selector6336 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_47_in_selector6346 = new BitSet(new long[]{0x0000000000000000L,0x0000000200000000L});
	public static final BitSet FOLLOW_97_in_selector6348 = new BitSet(new long[]{0x0020000000020000L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_selector6351 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_innerCreator_in_selector6355 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_59_in_selector6365 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_selector6367 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_60_in_selector6369 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_arguments_in_superSuffix6386 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_47_in_superSuffix6396 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_superSuffix6398 = new BitSet(new long[]{0x0000001000000002L});
	public static final BitSet FOLLOW_arguments_in_superSuffix6401 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_36_in_arguments6428 = new BitSet(new long[]{0x0020333023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expressionList_in_arguments6430 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_37_in_arguments6433 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotations_in_synpred1_Java81 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_methodDeclaration_in_synpred38_Java718 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fieldDeclaration_in_synpred39_Java726 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_47_in_synpred85_Java1703 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_synpred85_Java1705 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotation_in_synpred120_Java2287 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_classDeclaration_in_synpred135_Java2582 = new BitSet(new long[]{0x0010000000000002L});
	public static final BitSet FOLLOW_52_in_synpred135_Java2584 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_interfaceDeclaration_in_synpred137_Java2595 = new BitSet(new long[]{0x0010000000000002L});
	public static final BitSet FOLLOW_52_in_synpred137_Java2597 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_enumDeclaration_in_synpred139_Java2608 = new BitSet(new long[]{0x0010000000000002L});
	public static final BitSet FOLLOW_52_in_synpred139_Java2610 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_localVariableDeclaration_in_synpred144_Java2791 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_synpred145_Java2799 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_87_in_synpred171_Java3294 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_parExpression_in_synpred171_Java3296 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_78_in_synpred172_Java3290 = new BitSet(new long[]{0x0030331023034060L,0x4379F386D4E5BACFL});
	public static final BitSet FOLLOW_87_in_synpred172_Java3294 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_parExpression_in_synpred172_Java3296 = new BitSet(new long[]{0x0030331023034060L,0x4379F386D4E5BACFL});
	public static final BitSet FOLLOW_statement_in_synpred172_Java3327 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_variableModifier_in_synpred174_Java3449 = new BitSet(new long[]{0x0400000000020000L,0x000002005028208AL});
	public static final BitSet FOLLOW_type_in_synpred174_Java3488 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_synpred174_Java3514 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_51_in_synpred174_Java3541 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_synpred174_Java3543 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_68_in_synpred190_Java4450 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_constantExpression_in_synpred190_Java4452 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_51_in_synpred190_Java4454 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_68_in_synpred191_Java4464 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_enumConstantName_in_synpred191_Java4466 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_51_in_synpred191_Java4468 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_forVarControl_in_synpred193_Java4558 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_variableModifier_in_synpred198_Java4596 = new BitSet(new long[]{0x0400000000020000L,0x000002005028208AL});
	public static final BitSet FOLLOW_type_in_synpred198_Java4631 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_variableDeclarators_in_synpred198_Java4651 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_assignmentExpression_in_synpred201_Java4875 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_assignmentOperator_in_synpred202_Java4888 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_synpred202_Java4890 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_56_in_synpred212_Java5036 = new BitSet(new long[]{0x0100000000000000L});
	public static final BitSet FOLLOW_56_in_synpred212_Java5038 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_54_in_synpred212_Java5040 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_relationalOp_in_synpred222_Java5311 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_shiftExpression_in_synpred222_Java5313 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_shiftOp_in_synpred226_Java5374 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_additiveExpression_in_synpred226_Java5376 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_56_in_synpred228_Java5412 = new BitSet(new long[]{0x0100000000000000L});
	public static final BitSet FOLLOW_56_in_synpred228_Java5414 = new BitSet(new long[]{0x0100000000000000L});
	public static final BitSet FOLLOW_56_in_synpred228_Java5416 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_castExpression_in_synpred240_Java5605 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_36_in_synpred244_Java5643 = new BitSet(new long[]{0x0000000000000000L,0x000002005020208AL});
	public static final BitSet FOLLOW_primitiveType_in_synpred244_Java5645 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_37_in_synpred244_Java5647 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_unaryExpression_in_synpred244_Java5649 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_synpred245_Java5661 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_47_in_synpred249_Java5728 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_Identifier_in_synpred249_Java5730 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_identifierSuffix_in_synpred250_Java5735 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_47_in_synpred253_Java5762 = new BitSet(new long[]{0x0000000000020000L,0x0020008084000800L});
	public static final BitSet FOLLOW_methodName_in_synpred253_Java5764 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_identifierSuffix_in_synpred254_Java5769 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_47_in_synpred258_Java5810 = new BitSet(new long[]{0x0000000000020000L,0x0020008084000800L});
	public static final BitSet FOLLOW_methodName_in_synpred258_Java5812 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_identifierSuffix_in_synpred259_Java5817 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_59_in_synpred270_Java5926 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_synpred270_Java5928 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_60_in_synpred270_Java5930 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_59_in_synpred286_Java6172 = new BitSet(new long[]{0x0020331023034060L,0x404892065025A0CAL});
	public static final BitSet FOLLOW_expression_in_synpred286_Java6174 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_60_in_synpred286_Java6176 = new BitSet(new long[]{0x0000000000000002L});
}
