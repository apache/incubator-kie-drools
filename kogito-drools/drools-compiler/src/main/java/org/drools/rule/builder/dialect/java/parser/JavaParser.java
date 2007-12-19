// $ANTLR 3.0.1 /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g 2007-12-19 16:46:01

	package org.drools.rule.builder.dialect.java.parser;
	import java.util.Iterator;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
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
 *          annotations (e.g. @InterfaceName.InnerAnnotation())
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
public class JavaParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "Identifier", "ENUM", "FloatingPointLiteral", "CharacterLiteral", "StringLiteral", "HexLiteral", "OctalLiteral", "DecimalLiteral", "HexDigit", "IntegerTypeSuffix", "Exponent", "FloatTypeSuffix", "EscapeSequence", "UnicodeEscape", "OctalEscape", "Letter", "JavaIDDigit", "WS", "COMMENT", "LINE_COMMENT", "'package'", "';'", "'import'", "'static'", "'.'", "'*'", "'class'", "'extends'", "'implements'", "'<'", "','", "'>'", "'&'", "'{'", "'}'", "'interface'", "'void'", "'['", "']'", "'throws'", "'='", "'public'", "'protected'", "'private'", "'abstract'", "'final'", "'native'", "'synchronized'", "'transient'", "'volatile'", "'strictfp'", "'boolean'", "'char'", "'byte'", "'short'", "'int'", "'long'", "'float'", "'double'", "'?'", "'super'", "'('", "')'", "'...'", "'null'", "'true'", "'false'", "'@'", "'default'", "'assert'", "':'", "'if'", "'else'", "'for'", "'while'", "'do'", "'try'", "'finally'", "'switch'", "'return'", "'throw'", "'break'", "'continue'", "'modify'", "'catch'", "'case'", "'+='", "'-='", "'*='", "'/='", "'&='", "'|='", "'^='", "'%='", "'||'", "'&&'", "'|'", "'^'", "'=='", "'!='", "'instanceof'", "'+'", "'-'", "'/'", "'%'", "'++'", "'--'", "'~'", "'!'", "'this'", "'new'"
    };
    public static final int Exponent=14;
    public static final int OctalLiteral=10;
    public static final int IntegerTypeSuffix=13;
    public static final int Identifier=4;
    public static final int HexDigit=12;
    public static final int WS=21;
    public static final int CharacterLiteral=7;
    public static final int COMMENT=22;
    public static final int StringLiteral=8;
    public static final int LINE_COMMENT=23;
    public static final int JavaIDDigit=20;
    public static final int Letter=19;
    public static final int UnicodeEscape=17;
    public static final int HexLiteral=9;
    public static final int EscapeSequence=16;
    public static final int EOF=-1;
    public static final int DecimalLiteral=11;
    public static final int OctalEscape=18;
    public static final int FloatingPointLiteral=6;
    public static final int FloatTypeSuffix=15;
    public static final int ENUM=5;

        public JavaParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[407+1];
         }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g"; }


    	private List identifiers = new ArrayList();
    	public List getIdentifiers() { return identifiers; }
    	private List localDeclarations = new ArrayList();
    	public List getLocalDeclarations() { return localDeclarations; }
    	public static final CommonToken IGNORE_TOKEN = new CommonToken(null,0,99,0,0);
    	private List errors = new ArrayList();
    	private int localVariableLevel = 0;
    	private List modifyBlocks = new ArrayList();
    	public List getModifyBlocks() { return modifyBlocks; }
    	
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
                    if ( errorRecovery ) {
                            //System.err.print("[SPURIOUS] ");
                            return;
                    }
                    errorRecovery = true;

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
    		StringBuffer message = new StringBuffer();		
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



    // $ANTLR start compilationUnit
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:206:1: compilationUnit : ( annotations )? ( packageDeclaration )? ( importDeclaration )* ( typeDeclaration )* ;
    public final void compilationUnit() throws RecognitionException {
        int compilationUnit_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 1) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:207:2: ( ( annotations )? ( packageDeclaration )? ( importDeclaration )* ( typeDeclaration )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:207:4: ( annotations )? ( packageDeclaration )? ( importDeclaration )* ( typeDeclaration )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:207:4: ( annotations )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==71) ) {
                int LA1_1 = input.LA(2);

                if ( (LA1_1==Identifier) ) {
                    int LA1_21 = input.LA(3);

                    if ( (synpred1()) ) {
                        alt1=1;
                    }
                }
            }
            switch (alt1) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: annotations
                    {
                    pushFollow(FOLLOW_annotations_in_compilationUnit70);
                    annotations();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:208:3: ( packageDeclaration )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==24) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: packageDeclaration
                    {
                    pushFollow(FOLLOW_packageDeclaration_in_compilationUnit75);
                    packageDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:209:9: ( importDeclaration )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==26) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: importDeclaration
            	    {
            	    pushFollow(FOLLOW_importDeclaration_in_compilationUnit86);
            	    importDeclaration();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:210:9: ( typeDeclaration )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==ENUM||LA4_0==25||LA4_0==27||LA4_0==30||LA4_0==39||(LA4_0>=45 && LA4_0<=54)||LA4_0==71) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: typeDeclaration
            	    {
            	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit97);
            	    typeDeclaration();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 1, compilationUnit_StartIndex); }
        }
        return ;
    }
    // $ANTLR end compilationUnit


    // $ANTLR start packageDeclaration
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:213:1: packageDeclaration : 'package' qualifiedName ';' ;
    public final void packageDeclaration() throws RecognitionException {
        int packageDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 2) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:214:2: ( 'package' qualifiedName ';' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:214:4: 'package' qualifiedName ';'
            {
            match(input,24,FOLLOW_24_in_packageDeclaration109); if (failed) return ;
            pushFollow(FOLLOW_qualifiedName_in_packageDeclaration111);
            qualifiedName();
            _fsp--;
            if (failed) return ;
            match(input,25,FOLLOW_25_in_packageDeclaration113); if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 2, packageDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end packageDeclaration


    // $ANTLR start importDeclaration
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:217:1: importDeclaration : 'import' ( 'static' )? Identifier ( '.' Identifier )* ( '.' '*' )? ';' ;
    public final void importDeclaration() throws RecognitionException {
        int importDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 3) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:218:2: ( 'import' ( 'static' )? Identifier ( '.' Identifier )* ( '.' '*' )? ';' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:218:4: 'import' ( 'static' )? Identifier ( '.' Identifier )* ( '.' '*' )? ';'
            {
            match(input,26,FOLLOW_26_in_importDeclaration125); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:218:13: ( 'static' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==27) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: 'static'
                    {
                    match(input,27,FOLLOW_27_in_importDeclaration127); if (failed) return ;

                    }
                    break;

            }

            match(input,Identifier,FOLLOW_Identifier_in_importDeclaration130); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:218:34: ( '.' Identifier )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==28) ) {
                    int LA6_1 = input.LA(2);

                    if ( (LA6_1==Identifier) ) {
                        alt6=1;
                    }


                }


                switch (alt6) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:218:35: '.' Identifier
            	    {
            	    match(input,28,FOLLOW_28_in_importDeclaration133); if (failed) return ;
            	    match(input,Identifier,FOLLOW_Identifier_in_importDeclaration135); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:218:52: ( '.' '*' )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==28) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:218:53: '.' '*'
                    {
                    match(input,28,FOLLOW_28_in_importDeclaration140); if (failed) return ;
                    match(input,29,FOLLOW_29_in_importDeclaration142); if (failed) return ;

                    }
                    break;

            }

            match(input,25,FOLLOW_25_in_importDeclaration146); if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 3, importDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end importDeclaration


    // $ANTLR start typeDeclaration
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:221:1: typeDeclaration : ( classOrInterfaceDeclaration | ';' );
    public final void typeDeclaration() throws RecognitionException {
        int typeDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 4) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:222:2: ( classOrInterfaceDeclaration | ';' )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==ENUM||LA8_0==27||LA8_0==30||LA8_0==39||(LA8_0>=45 && LA8_0<=54)||LA8_0==71) ) {
                alt8=1;
            }
            else if ( (LA8_0==25) ) {
                alt8=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("221:1: typeDeclaration : ( classOrInterfaceDeclaration | ';' );", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:222:4: classOrInterfaceDeclaration
                    {
                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration158);
                    classOrInterfaceDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:223:9: ';'
                    {
                    match(input,25,FOLLOW_25_in_typeDeclaration168); if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 4, typeDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end typeDeclaration


    // $ANTLR start classOrInterfaceDeclaration
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:226:1: classOrInterfaceDeclaration : ( modifier )* ( classDeclaration | interfaceDeclaration ) ;
    public final void classOrInterfaceDeclaration() throws RecognitionException {
        int classOrInterfaceDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 5) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:227:2: ( ( modifier )* ( classDeclaration | interfaceDeclaration ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:227:4: ( modifier )* ( classDeclaration | interfaceDeclaration )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:227:4: ( modifier )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==71) ) {
                    int LA9_4 = input.LA(2);

                    if ( (LA9_4==Identifier) ) {
                        alt9=1;
                    }


                }
                else if ( (LA9_0==27||(LA9_0>=45 && LA9_0<=54)) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: modifier
            	    {
            	    pushFollow(FOLLOW_modifier_in_classOrInterfaceDeclaration180);
            	    modifier();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:227:14: ( classDeclaration | interfaceDeclaration )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==ENUM||LA10_0==30) ) {
                alt10=1;
            }
            else if ( (LA10_0==39||LA10_0==71) ) {
                alt10=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("227:14: ( classDeclaration | interfaceDeclaration )", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:227:15: classDeclaration
                    {
                    pushFollow(FOLLOW_classDeclaration_in_classOrInterfaceDeclaration184);
                    classDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:227:34: interfaceDeclaration
                    {
                    pushFollow(FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration188);
                    interfaceDeclaration();
                    _fsp--;
                    if (failed) return ;

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
            if ( backtracking>0 ) { memoize(input, 5, classOrInterfaceDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end classOrInterfaceDeclaration


    // $ANTLR start classDeclaration
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:230:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );
    public final void classDeclaration() throws RecognitionException {
        int classDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 6) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:231:2: ( normalClassDeclaration | enumDeclaration )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==30) ) {
                alt11=1;
            }
            else if ( (LA11_0==ENUM) ) {
                alt11=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("230:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:231:4: normalClassDeclaration
                    {
                    pushFollow(FOLLOW_normalClassDeclaration_in_classDeclaration201);
                    normalClassDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:232:9: enumDeclaration
                    {
                    pushFollow(FOLLOW_enumDeclaration_in_classDeclaration211);
                    enumDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 6, classDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end classDeclaration


    // $ANTLR start normalClassDeclaration
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:235:1: normalClassDeclaration : 'class' Identifier ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody ;
    public final void normalClassDeclaration() throws RecognitionException {
        int normalClassDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 7) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:236:2: ( 'class' Identifier ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:236:4: 'class' Identifier ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody
            {
            match(input,30,FOLLOW_30_in_normalClassDeclaration223); if (failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_normalClassDeclaration225); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:236:23: ( typeParameters )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==33) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:236:24: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_normalClassDeclaration228);
                    typeParameters();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:237:9: ( 'extends' type )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==31) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:237:10: 'extends' type
                    {
                    match(input,31,FOLLOW_31_in_normalClassDeclaration241); if (failed) return ;
                    pushFollow(FOLLOW_type_in_normalClassDeclaration243);
                    type();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:238:9: ( 'implements' typeList )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==32) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:238:10: 'implements' typeList
                    {
                    match(input,32,FOLLOW_32_in_normalClassDeclaration256); if (failed) return ;
                    pushFollow(FOLLOW_typeList_in_normalClassDeclaration258);
                    typeList();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_classBody_in_normalClassDeclaration270);
            classBody();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 7, normalClassDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end normalClassDeclaration


    // $ANTLR start typeParameters
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:242:1: typeParameters : '<' typeParameter ( ',' typeParameter )* '>' ;
    public final void typeParameters() throws RecognitionException {
        int typeParameters_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 8) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:243:2: ( '<' typeParameter ( ',' typeParameter )* '>' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:243:4: '<' typeParameter ( ',' typeParameter )* '>'
            {
            match(input,33,FOLLOW_33_in_typeParameters282); if (failed) return ;
            pushFollow(FOLLOW_typeParameter_in_typeParameters284);
            typeParameter();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:243:22: ( ',' typeParameter )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==34) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:243:23: ',' typeParameter
            	    {
            	    match(input,34,FOLLOW_34_in_typeParameters287); if (failed) return ;
            	    pushFollow(FOLLOW_typeParameter_in_typeParameters289);
            	    typeParameter();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);

            match(input,35,FOLLOW_35_in_typeParameters293); if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 8, typeParameters_StartIndex); }
        }
        return ;
    }
    // $ANTLR end typeParameters


    // $ANTLR start typeParameter
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:246:1: typeParameter : Identifier ( 'extends' bound )? ;
    public final void typeParameter() throws RecognitionException {
        int typeParameter_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 9) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:247:2: ( Identifier ( 'extends' bound )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:247:4: Identifier ( 'extends' bound )?
            {
            match(input,Identifier,FOLLOW_Identifier_in_typeParameter304); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:247:15: ( 'extends' bound )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==31) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:247:16: 'extends' bound
                    {
                    match(input,31,FOLLOW_31_in_typeParameter307); if (failed) return ;
                    pushFollow(FOLLOW_bound_in_typeParameter309);
                    bound();
                    _fsp--;
                    if (failed) return ;

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
            if ( backtracking>0 ) { memoize(input, 9, typeParameter_StartIndex); }
        }
        return ;
    }
    // $ANTLR end typeParameter


    // $ANTLR start bound
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:250:1: bound : type ( '&' type )* ;
    public final void bound() throws RecognitionException {
        int bound_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 10) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:251:2: ( type ( '&' type )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:251:4: type ( '&' type )*
            {
            pushFollow(FOLLOW_type_in_bound324);
            type();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:251:9: ( '&' type )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==36) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:251:10: '&' type
            	    {
            	    match(input,36,FOLLOW_36_in_bound327); if (failed) return ;
            	    pushFollow(FOLLOW_type_in_bound329);
            	    type();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 10, bound_StartIndex); }
        }
        return ;
    }
    // $ANTLR end bound


    // $ANTLR start enumDeclaration
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:254:1: enumDeclaration : ENUM Identifier ( 'implements' typeList )? enumBody ;
    public final void enumDeclaration() throws RecognitionException {
        int enumDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 11) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:255:2: ( ENUM Identifier ( 'implements' typeList )? enumBody )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:255:4: ENUM Identifier ( 'implements' typeList )? enumBody
            {
            match(input,ENUM,FOLLOW_ENUM_in_enumDeclaration342); if (failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_enumDeclaration344); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:255:20: ( 'implements' typeList )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==32) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:255:21: 'implements' typeList
                    {
                    match(input,32,FOLLOW_32_in_enumDeclaration347); if (failed) return ;
                    pushFollow(FOLLOW_typeList_in_enumDeclaration349);
                    typeList();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_enumBody_in_enumDeclaration353);
            enumBody();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 11, enumDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end enumDeclaration


    // $ANTLR start enumBody
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:258:1: enumBody : '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}' ;
    public final void enumBody() throws RecognitionException {
        int enumBody_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 12) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:259:2: ( '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:259:4: '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}'
            {
            match(input,37,FOLLOW_37_in_enumBody365); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:259:8: ( enumConstants )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==Identifier||LA19_0==71) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: enumConstants
                    {
                    pushFollow(FOLLOW_enumConstants_in_enumBody367);
                    enumConstants();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:259:23: ( ',' )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==34) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ','
                    {
                    match(input,34,FOLLOW_34_in_enumBody370); if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:259:28: ( enumBodyDeclarations )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==25) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: enumBodyDeclarations
                    {
                    pushFollow(FOLLOW_enumBodyDeclarations_in_enumBody373);
                    enumBodyDeclarations();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            match(input,38,FOLLOW_38_in_enumBody376); if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 12, enumBody_StartIndex); }
        }
        return ;
    }
    // $ANTLR end enumBody


    // $ANTLR start enumConstants
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:262:1: enumConstants : enumConstant ( ',' enumConstant )* ;
    public final void enumConstants() throws RecognitionException {
        int enumConstants_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 13) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:263:2: ( enumConstant ( ',' enumConstant )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:263:4: enumConstant ( ',' enumConstant )*
            {
            pushFollow(FOLLOW_enumConstant_in_enumConstants387);
            enumConstant();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:263:17: ( ',' enumConstant )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==34) ) {
                    int LA22_1 = input.LA(2);

                    if ( (LA22_1==Identifier||LA22_1==71) ) {
                        alt22=1;
                    }


                }


                switch (alt22) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:263:18: ',' enumConstant
            	    {
            	    match(input,34,FOLLOW_34_in_enumConstants390); if (failed) return ;
            	    pushFollow(FOLLOW_enumConstant_in_enumConstants392);
            	    enumConstant();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 13, enumConstants_StartIndex); }
        }
        return ;
    }
    // $ANTLR end enumConstants


    // $ANTLR start enumConstant
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:266:1: enumConstant : ( annotations )? Identifier ( arguments )? ( classBody )? ;
    public final void enumConstant() throws RecognitionException {
        int enumConstant_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 14) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:267:2: ( ( annotations )? Identifier ( arguments )? ( classBody )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:267:4: ( annotations )? Identifier ( arguments )? ( classBody )?
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:267:4: ( annotations )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==71) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: annotations
                    {
                    pushFollow(FOLLOW_annotations_in_enumConstant406);
                    annotations();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            match(input,Identifier,FOLLOW_Identifier_in_enumConstant409); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:267:28: ( arguments )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==65) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:267:29: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_enumConstant412);
                    arguments();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:267:41: ( classBody )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==37) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:267:42: classBody
                    {
                    pushFollow(FOLLOW_classBody_in_enumConstant417);
                    classBody();
                    _fsp--;
                    if (failed) return ;

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
            if ( backtracking>0 ) { memoize(input, 14, enumConstant_StartIndex); }
        }
        return ;
    }
    // $ANTLR end enumConstant


    // $ANTLR start enumBodyDeclarations
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:270:1: enumBodyDeclarations : ';' ( classBodyDeclaration )* ;
    public final void enumBodyDeclarations() throws RecognitionException {
        int enumBodyDeclarations_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 15) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:271:2: ( ';' ( classBodyDeclaration )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:271:4: ';' ( classBodyDeclaration )*
            {
            match(input,25,FOLLOW_25_in_enumBodyDeclarations431); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:271:8: ( classBodyDeclaration )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( ((LA26_0>=Identifier && LA26_0<=ENUM)||LA26_0==25||LA26_0==27||LA26_0==30||LA26_0==33||LA26_0==37||(LA26_0>=39 && LA26_0<=40)||(LA26_0>=45 && LA26_0<=62)||LA26_0==71) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:271:9: classBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_classBodyDeclaration_in_enumBodyDeclarations434);
            	    classBodyDeclaration();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 15, enumBodyDeclarations_StartIndex); }
        }
        return ;
    }
    // $ANTLR end enumBodyDeclarations


    // $ANTLR start interfaceDeclaration
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:274:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );
    public final void interfaceDeclaration() throws RecognitionException {
        int interfaceDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 16) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:275:2: ( normalInterfaceDeclaration | annotationTypeDeclaration )
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==39) ) {
                alt27=1;
            }
            else if ( (LA27_0==71) ) {
                alt27=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("274:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:275:4: normalInterfaceDeclaration
                    {
                    pushFollow(FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration448);
                    normalInterfaceDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:276:5: annotationTypeDeclaration
                    {
                    pushFollow(FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration454);
                    annotationTypeDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 16, interfaceDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end interfaceDeclaration


    // $ANTLR start normalInterfaceDeclaration
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:279:1: normalInterfaceDeclaration : 'interface' Identifier ( typeParameters )? ( 'extends' typeList )? interfaceBody ;
    public final void normalInterfaceDeclaration() throws RecognitionException {
        int normalInterfaceDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 17) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:280:2: ( 'interface' Identifier ( typeParameters )? ( 'extends' typeList )? interfaceBody )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:280:4: 'interface' Identifier ( typeParameters )? ( 'extends' typeList )? interfaceBody
            {
            match(input,39,FOLLOW_39_in_normalInterfaceDeclaration466); if (failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_normalInterfaceDeclaration468); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:280:27: ( typeParameters )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==33) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_normalInterfaceDeclaration470);
                    typeParameters();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:280:43: ( 'extends' typeList )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==31) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:280:44: 'extends' typeList
                    {
                    match(input,31,FOLLOW_31_in_normalInterfaceDeclaration474); if (failed) return ;
                    pushFollow(FOLLOW_typeList_in_normalInterfaceDeclaration476);
                    typeList();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_interfaceBody_in_normalInterfaceDeclaration480);
            interfaceBody();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 17, normalInterfaceDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end normalInterfaceDeclaration


    // $ANTLR start typeList
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:283:1: typeList : type ( ',' type )* ;
    public final void typeList() throws RecognitionException {
        int typeList_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 18) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:284:2: ( type ( ',' type )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:284:4: type ( ',' type )*
            {
            pushFollow(FOLLOW_type_in_typeList492);
            type();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:284:9: ( ',' type )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==34) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:284:10: ',' type
            	    {
            	    match(input,34,FOLLOW_34_in_typeList495); if (failed) return ;
            	    pushFollow(FOLLOW_type_in_typeList497);
            	    type();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop30;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 18, typeList_StartIndex); }
        }
        return ;
    }
    // $ANTLR end typeList


    // $ANTLR start classBody
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:287:1: classBody : '{' ( classBodyDeclaration )* '}' ;
    public final void classBody() throws RecognitionException {
        int classBody_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 19) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:288:2: ( '{' ( classBodyDeclaration )* '}' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:288:4: '{' ( classBodyDeclaration )* '}'
            {
            match(input,37,FOLLOW_37_in_classBody511); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:288:8: ( classBodyDeclaration )*
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( ((LA31_0>=Identifier && LA31_0<=ENUM)||LA31_0==25||LA31_0==27||LA31_0==30||LA31_0==33||LA31_0==37||(LA31_0>=39 && LA31_0<=40)||(LA31_0>=45 && LA31_0<=62)||LA31_0==71) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: classBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_classBodyDeclaration_in_classBody513);
            	    classBodyDeclaration();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop31;
                }
            } while (true);

            match(input,38,FOLLOW_38_in_classBody516); if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 19, classBody_StartIndex); }
        }
        return ;
    }
    // $ANTLR end classBody


    // $ANTLR start interfaceBody
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:291:1: interfaceBody : '{' ( interfaceBodyDeclaration )* '}' ;
    public final void interfaceBody() throws RecognitionException {
        int interfaceBody_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 20) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:292:2: ( '{' ( interfaceBodyDeclaration )* '}' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:292:4: '{' ( interfaceBodyDeclaration )* '}'
            {
            match(input,37,FOLLOW_37_in_interfaceBody528); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:292:8: ( interfaceBodyDeclaration )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);

                if ( ((LA32_0>=Identifier && LA32_0<=ENUM)||LA32_0==25||LA32_0==27||LA32_0==30||LA32_0==33||(LA32_0>=39 && LA32_0<=40)||(LA32_0>=45 && LA32_0<=62)||LA32_0==71) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: interfaceBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_interfaceBodyDeclaration_in_interfaceBody530);
            	    interfaceBodyDeclaration();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop32;
                }
            } while (true);

            match(input,38,FOLLOW_38_in_interfaceBody533); if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 20, interfaceBody_StartIndex); }
        }
        return ;
    }
    // $ANTLR end interfaceBody


    // $ANTLR start classBodyDeclaration
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:295:1: classBodyDeclaration : ( ';' | ( 'static' )? block | ( modifier )* memberDecl );
    public final void classBodyDeclaration() throws RecognitionException {
        int classBodyDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 21) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:296:2: ( ';' | ( 'static' )? block | ( modifier )* memberDecl )
            int alt35=3;
            switch ( input.LA(1) ) {
            case 25:
                {
                alt35=1;
                }
                break;
            case 27:
                {
                int LA35_2 = input.LA(2);

                if ( ((LA35_2>=Identifier && LA35_2<=ENUM)||LA35_2==27||LA35_2==30||LA35_2==33||(LA35_2>=39 && LA35_2<=40)||(LA35_2>=45 && LA35_2<=62)||LA35_2==71) ) {
                    alt35=3;
                }
                else if ( (LA35_2==37) ) {
                    alt35=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("295:1: classBodyDeclaration : ( ';' | ( 'static' )? block | ( modifier )* memberDecl );", 35, 2, input);

                    throw nvae;
                }
                }
                break;
            case 37:
                {
                alt35=2;
                }
                break;
            case Identifier:
            case ENUM:
            case 30:
            case 33:
            case 39:
            case 40:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 71:
                {
                alt35=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("295:1: classBodyDeclaration : ( ';' | ( 'static' )? block | ( modifier )* memberDecl );", 35, 0, input);

                throw nvae;
            }

            switch (alt35) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:296:4: ';'
                    {
                    match(input,25,FOLLOW_25_in_classBodyDeclaration544); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:297:4: ( 'static' )? block
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:297:4: ( 'static' )?
                    int alt33=2;
                    int LA33_0 = input.LA(1);

                    if ( (LA33_0==27) ) {
                        alt33=1;
                    }
                    switch (alt33) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: 'static'
                            {
                            match(input,27,FOLLOW_27_in_classBodyDeclaration549); if (failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_block_in_classBodyDeclaration552);
                    block();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:298:4: ( modifier )* memberDecl
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:298:4: ( modifier )*
                    loop34:
                    do {
                        int alt34=2;
                        int LA34_0 = input.LA(1);

                        if ( (LA34_0==71) ) {
                            int LA34_6 = input.LA(2);

                            if ( (LA34_6==Identifier) ) {
                                alt34=1;
                            }


                        }
                        else if ( (LA34_0==27||(LA34_0>=45 && LA34_0<=54)) ) {
                            alt34=1;
                        }


                        switch (alt34) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: modifier
                    	    {
                    	    pushFollow(FOLLOW_modifier_in_classBodyDeclaration557);
                    	    modifier();
                    	    _fsp--;
                    	    if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop34;
                        }
                    } while (true);

                    pushFollow(FOLLOW_memberDecl_in_classBodyDeclaration560);
                    memberDecl();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 21, classBodyDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end classBodyDeclaration


    // $ANTLR start memberDecl
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:301:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );
    public final void memberDecl() throws RecognitionException {
        int memberDecl_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 22) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:302:2: ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration )
            int alt36=7;
            switch ( input.LA(1) ) {
            case 33:
                {
                alt36=1;
                }
                break;
            case Identifier:
                {
                switch ( input.LA(2) ) {
                case 33:
                    {
                    int LA36_9 = input.LA(3);

                    if ( (synpred38()) ) {
                        alt36=2;
                    }
                    else if ( (synpred39()) ) {
                        alt36=3;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("301:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );", 36, 9, input);

                        throw nvae;
                    }
                    }
                    break;
                case 28:
                    {
                    int LA36_10 = input.LA(3);

                    if ( (synpred38()) ) {
                        alt36=2;
                    }
                    else if ( (synpred39()) ) {
                        alt36=3;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("301:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );", 36, 10, input);

                        throw nvae;
                    }
                    }
                    break;
                case 41:
                    {
                    int LA36_11 = input.LA(3);

                    if ( (synpred38()) ) {
                        alt36=2;
                    }
                    else if ( (synpred39()) ) {
                        alt36=3;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("301:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );", 36, 11, input);

                        throw nvae;
                    }
                    }
                    break;
                case Identifier:
                    {
                    int LA36_12 = input.LA(3);

                    if ( (synpred38()) ) {
                        alt36=2;
                    }
                    else if ( (synpred39()) ) {
                        alt36=3;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("301:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );", 36, 12, input);

                        throw nvae;
                    }
                    }
                    break;
                case 65:
                    {
                    alt36=5;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("301:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );", 36, 2, input);

                    throw nvae;
                }

                }
                break;
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
                {
                int LA36_3 = input.LA(2);

                if ( (LA36_3==41) ) {
                    int LA36_14 = input.LA(3);

                    if ( (synpred38()) ) {
                        alt36=2;
                    }
                    else if ( (synpred39()) ) {
                        alt36=3;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("301:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );", 36, 14, input);

                        throw nvae;
                    }
                }
                else if ( (LA36_3==Identifier) ) {
                    int LA36_15 = input.LA(3);

                    if ( (synpred38()) ) {
                        alt36=2;
                    }
                    else if ( (synpred39()) ) {
                        alt36=3;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("301:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );", 36, 15, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("301:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );", 36, 3, input);

                    throw nvae;
                }
                }
                break;
            case 40:
                {
                alt36=4;
                }
                break;
            case 39:
            case 71:
                {
                alt36=6;
                }
                break;
            case ENUM:
            case 30:
                {
                alt36=7;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("301:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );", 36, 0, input);

                throw nvae;
            }

            switch (alt36) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:302:4: genericMethodOrConstructorDecl
                    {
                    pushFollow(FOLLOW_genericMethodOrConstructorDecl_in_memberDecl572);
                    genericMethodOrConstructorDecl();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:303:4: methodDeclaration
                    {
                    pushFollow(FOLLOW_methodDeclaration_in_memberDecl577);
                    methodDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:304:4: fieldDeclaration
                    {
                    pushFollow(FOLLOW_fieldDeclaration_in_memberDecl582);
                    fieldDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:305:4: 'void' Identifier voidMethodDeclaratorRest
                    {
                    match(input,40,FOLLOW_40_in_memberDecl587); if (failed) return ;
                    match(input,Identifier,FOLLOW_Identifier_in_memberDecl589); if (failed) return ;
                    pushFollow(FOLLOW_voidMethodDeclaratorRest_in_memberDecl591);
                    voidMethodDeclaratorRest();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:306:4: Identifier constructorDeclaratorRest
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_memberDecl596); if (failed) return ;
                    pushFollow(FOLLOW_constructorDeclaratorRest_in_memberDecl598);
                    constructorDeclaratorRest();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:307:4: interfaceDeclaration
                    {
                    pushFollow(FOLLOW_interfaceDeclaration_in_memberDecl603);
                    interfaceDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:308:4: classDeclaration
                    {
                    pushFollow(FOLLOW_classDeclaration_in_memberDecl608);
                    classDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 22, memberDecl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end memberDecl


    // $ANTLR start genericMethodOrConstructorDecl
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:311:1: genericMethodOrConstructorDecl : typeParameters genericMethodOrConstructorRest ;
    public final void genericMethodOrConstructorDecl() throws RecognitionException {
        int genericMethodOrConstructorDecl_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 23) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:312:2: ( typeParameters genericMethodOrConstructorRest )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:312:4: typeParameters genericMethodOrConstructorRest
            {
            pushFollow(FOLLOW_typeParameters_in_genericMethodOrConstructorDecl620);
            typeParameters();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_genericMethodOrConstructorRest_in_genericMethodOrConstructorDecl622);
            genericMethodOrConstructorRest();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 23, genericMethodOrConstructorDecl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end genericMethodOrConstructorDecl


    // $ANTLR start genericMethodOrConstructorRest
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:315:1: genericMethodOrConstructorRest : ( ( type | 'void' ) Identifier methodDeclaratorRest | Identifier constructorDeclaratorRest );
    public final void genericMethodOrConstructorRest() throws RecognitionException {
        int genericMethodOrConstructorRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 24) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:316:2: ( ( type | 'void' ) Identifier methodDeclaratorRest | Identifier constructorDeclaratorRest )
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==Identifier) ) {
                int LA38_1 = input.LA(2);

                if ( (LA38_1==Identifier||LA38_1==28||LA38_1==33||LA38_1==41) ) {
                    alt38=1;
                }
                else if ( (LA38_1==65) ) {
                    alt38=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("315:1: genericMethodOrConstructorRest : ( ( type | 'void' ) Identifier methodDeclaratorRest | Identifier constructorDeclaratorRest );", 38, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA38_0==40||(LA38_0>=55 && LA38_0<=62)) ) {
                alt38=1;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("315:1: genericMethodOrConstructorRest : ( ( type | 'void' ) Identifier methodDeclaratorRest | Identifier constructorDeclaratorRest );", 38, 0, input);

                throw nvae;
            }
            switch (alt38) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:316:4: ( type | 'void' ) Identifier methodDeclaratorRest
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:316:4: ( type | 'void' )
                    int alt37=2;
                    int LA37_0 = input.LA(1);

                    if ( (LA37_0==Identifier||(LA37_0>=55 && LA37_0<=62)) ) {
                        alt37=1;
                    }
                    else if ( (LA37_0==40) ) {
                        alt37=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("316:4: ( type | 'void' )", 37, 0, input);

                        throw nvae;
                    }
                    switch (alt37) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:316:5: type
                            {
                            pushFollow(FOLLOW_type_in_genericMethodOrConstructorRest635);
                            type();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;
                        case 2 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:316:12: 'void'
                            {
                            match(input,40,FOLLOW_40_in_genericMethodOrConstructorRest639); if (failed) return ;

                            }
                            break;

                    }

                    match(input,Identifier,FOLLOW_Identifier_in_genericMethodOrConstructorRest642); if (failed) return ;
                    pushFollow(FOLLOW_methodDeclaratorRest_in_genericMethodOrConstructorRest644);
                    methodDeclaratorRest();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:317:4: Identifier constructorDeclaratorRest
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_genericMethodOrConstructorRest649); if (failed) return ;
                    pushFollow(FOLLOW_constructorDeclaratorRest_in_genericMethodOrConstructorRest651);
                    constructorDeclaratorRest();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 24, genericMethodOrConstructorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end genericMethodOrConstructorRest


    // $ANTLR start methodDeclaration
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:320:1: methodDeclaration : type Identifier methodDeclaratorRest ;
    public final void methodDeclaration() throws RecognitionException {
        int methodDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 25) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:321:2: ( type Identifier methodDeclaratorRest )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:321:4: type Identifier methodDeclaratorRest
            {
            pushFollow(FOLLOW_type_in_methodDeclaration662);
            type();
            _fsp--;
            if (failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_methodDeclaration664); if (failed) return ;
            pushFollow(FOLLOW_methodDeclaratorRest_in_methodDeclaration666);
            methodDeclaratorRest();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 25, methodDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end methodDeclaration


    // $ANTLR start fieldDeclaration
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:324:1: fieldDeclaration : type variableDeclarators ';' ;
    public final void fieldDeclaration() throws RecognitionException {
        int fieldDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 26) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:325:2: ( type variableDeclarators ';' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:325:4: type variableDeclarators ';'
            {
            pushFollow(FOLLOW_type_in_fieldDeclaration677);
            type();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_variableDeclarators_in_fieldDeclaration679);
            variableDeclarators();
            _fsp--;
            if (failed) return ;
            match(input,25,FOLLOW_25_in_fieldDeclaration681); if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 26, fieldDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end fieldDeclaration


    // $ANTLR start interfaceBodyDeclaration
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:328:1: interfaceBodyDeclaration : ( ( modifier )* interfaceMemberDecl | ';' );
    public final void interfaceBodyDeclaration() throws RecognitionException {
        int interfaceBodyDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 27) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:329:2: ( ( modifier )* interfaceMemberDecl | ';' )
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( ((LA40_0>=Identifier && LA40_0<=ENUM)||LA40_0==27||LA40_0==30||LA40_0==33||(LA40_0>=39 && LA40_0<=40)||(LA40_0>=45 && LA40_0<=62)||LA40_0==71) ) {
                alt40=1;
            }
            else if ( (LA40_0==25) ) {
                alt40=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("328:1: interfaceBodyDeclaration : ( ( modifier )* interfaceMemberDecl | ';' );", 40, 0, input);

                throw nvae;
            }
            switch (alt40) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:329:4: ( modifier )* interfaceMemberDecl
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:329:4: ( modifier )*
                    loop39:
                    do {
                        int alt39=2;
                        int LA39_0 = input.LA(1);

                        if ( (LA39_0==71) ) {
                            int LA39_6 = input.LA(2);

                            if ( (LA39_6==Identifier) ) {
                                alt39=1;
                            }


                        }
                        else if ( (LA39_0==27||(LA39_0>=45 && LA39_0<=54)) ) {
                            alt39=1;
                        }


                        switch (alt39) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: modifier
                    	    {
                    	    pushFollow(FOLLOW_modifier_in_interfaceBodyDeclaration694);
                    	    modifier();
                    	    _fsp--;
                    	    if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop39;
                        }
                    } while (true);

                    pushFollow(FOLLOW_interfaceMemberDecl_in_interfaceBodyDeclaration697);
                    interfaceMemberDecl();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:330:6: ';'
                    {
                    match(input,25,FOLLOW_25_in_interfaceBodyDeclaration704); if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 27, interfaceBodyDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end interfaceBodyDeclaration


    // $ANTLR start interfaceMemberDecl
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:333:1: interfaceMemberDecl : ( interfaceMethodOrFieldDecl | interfaceGenericMethodDecl | 'void' Identifier voidInterfaceMethodDeclaratorRest | interfaceDeclaration | classDeclaration );
    public final void interfaceMemberDecl() throws RecognitionException {
        int interfaceMemberDecl_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 28) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:334:2: ( interfaceMethodOrFieldDecl | interfaceGenericMethodDecl | 'void' Identifier voidInterfaceMethodDeclaratorRest | interfaceDeclaration | classDeclaration )
            int alt41=5;
            switch ( input.LA(1) ) {
            case Identifier:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
                {
                alt41=1;
                }
                break;
            case 33:
                {
                alt41=2;
                }
                break;
            case 40:
                {
                alt41=3;
                }
                break;
            case 39:
            case 71:
                {
                alt41=4;
                }
                break;
            case ENUM:
            case 30:
                {
                alt41=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("333:1: interfaceMemberDecl : ( interfaceMethodOrFieldDecl | interfaceGenericMethodDecl | 'void' Identifier voidInterfaceMethodDeclaratorRest | interfaceDeclaration | classDeclaration );", 41, 0, input);

                throw nvae;
            }

            switch (alt41) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:334:4: interfaceMethodOrFieldDecl
                    {
                    pushFollow(FOLLOW_interfaceMethodOrFieldDecl_in_interfaceMemberDecl715);
                    interfaceMethodOrFieldDecl();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:335:6: interfaceGenericMethodDecl
                    {
                    pushFollow(FOLLOW_interfaceGenericMethodDecl_in_interfaceMemberDecl722);
                    interfaceGenericMethodDecl();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:336:9: 'void' Identifier voidInterfaceMethodDeclaratorRest
                    {
                    match(input,40,FOLLOW_40_in_interfaceMemberDecl732); if (failed) return ;
                    match(input,Identifier,FOLLOW_Identifier_in_interfaceMemberDecl734); if (failed) return ;
                    pushFollow(FOLLOW_voidInterfaceMethodDeclaratorRest_in_interfaceMemberDecl736);
                    voidInterfaceMethodDeclaratorRest();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:337:9: interfaceDeclaration
                    {
                    pushFollow(FOLLOW_interfaceDeclaration_in_interfaceMemberDecl746);
                    interfaceDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:338:9: classDeclaration
                    {
                    pushFollow(FOLLOW_classDeclaration_in_interfaceMemberDecl756);
                    classDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 28, interfaceMemberDecl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end interfaceMemberDecl


    // $ANTLR start interfaceMethodOrFieldDecl
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:341:1: interfaceMethodOrFieldDecl : type Identifier interfaceMethodOrFieldRest ;
    public final void interfaceMethodOrFieldDecl() throws RecognitionException {
        int interfaceMethodOrFieldDecl_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 29) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:342:2: ( type Identifier interfaceMethodOrFieldRest )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:342:4: type Identifier interfaceMethodOrFieldRest
            {
            pushFollow(FOLLOW_type_in_interfaceMethodOrFieldDecl768);
            type();
            _fsp--;
            if (failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_interfaceMethodOrFieldDecl770); if (failed) return ;
            pushFollow(FOLLOW_interfaceMethodOrFieldRest_in_interfaceMethodOrFieldDecl772);
            interfaceMethodOrFieldRest();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 29, interfaceMethodOrFieldDecl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end interfaceMethodOrFieldDecl


    // $ANTLR start interfaceMethodOrFieldRest
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:345:1: interfaceMethodOrFieldRest : ( constantDeclaratorsRest ';' | interfaceMethodDeclaratorRest );
    public final void interfaceMethodOrFieldRest() throws RecognitionException {
        int interfaceMethodOrFieldRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 30) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:346:2: ( constantDeclaratorsRest ';' | interfaceMethodDeclaratorRest )
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==41||LA42_0==44) ) {
                alt42=1;
            }
            else if ( (LA42_0==65) ) {
                alt42=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("345:1: interfaceMethodOrFieldRest : ( constantDeclaratorsRest ';' | interfaceMethodDeclaratorRest );", 42, 0, input);

                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:346:4: constantDeclaratorsRest ';'
                    {
                    pushFollow(FOLLOW_constantDeclaratorsRest_in_interfaceMethodOrFieldRest784);
                    constantDeclaratorsRest();
                    _fsp--;
                    if (failed) return ;
                    match(input,25,FOLLOW_25_in_interfaceMethodOrFieldRest786); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:347:4: interfaceMethodDeclaratorRest
                    {
                    pushFollow(FOLLOW_interfaceMethodDeclaratorRest_in_interfaceMethodOrFieldRest791);
                    interfaceMethodDeclaratorRest();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 30, interfaceMethodOrFieldRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end interfaceMethodOrFieldRest


    // $ANTLR start methodDeclaratorRest
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:350:1: methodDeclaratorRest : formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( methodBody | ';' ) ;
    public final void methodDeclaratorRest() throws RecognitionException {
        int methodDeclaratorRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 31) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:351:2: ( formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( methodBody | ';' ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:351:4: formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( methodBody | ';' )
            {
            pushFollow(FOLLOW_formalParameters_in_methodDeclaratorRest803);
            formalParameters();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:351:21: ( '[' ']' )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);

                if ( (LA43_0==41) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:351:22: '[' ']'
            	    {
            	    match(input,41,FOLLOW_41_in_methodDeclaratorRest806); if (failed) return ;
            	    match(input,42,FOLLOW_42_in_methodDeclaratorRest808); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop43;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:352:9: ( 'throws' qualifiedNameList )?
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==43) ) {
                alt44=1;
            }
            switch (alt44) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:352:10: 'throws' qualifiedNameList
                    {
                    match(input,43,FOLLOW_43_in_methodDeclaratorRest821); if (failed) return ;
                    pushFollow(FOLLOW_qualifiedNameList_in_methodDeclaratorRest823);
                    qualifiedNameList();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:353:9: ( methodBody | ';' )
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==37) ) {
                alt45=1;
            }
            else if ( (LA45_0==25) ) {
                alt45=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("353:9: ( methodBody | ';' )", 45, 0, input);

                throw nvae;
            }
            switch (alt45) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:353:13: methodBody
                    {
                    pushFollow(FOLLOW_methodBody_in_methodDeclaratorRest839);
                    methodBody();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:354:13: ';'
                    {
                    match(input,25,FOLLOW_25_in_methodDeclaratorRest853); if (failed) return ;

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
            if ( backtracking>0 ) { memoize(input, 31, methodDeclaratorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end methodDeclaratorRest


    // $ANTLR start voidMethodDeclaratorRest
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:358:1: voidMethodDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' ) ;
    public final void voidMethodDeclaratorRest() throws RecognitionException {
        int voidMethodDeclaratorRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 32) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:359:2: ( formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:359:4: formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' )
            {
            pushFollow(FOLLOW_formalParameters_in_voidMethodDeclaratorRest875);
            formalParameters();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:359:21: ( 'throws' qualifiedNameList )?
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==43) ) {
                alt46=1;
            }
            switch (alt46) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:359:22: 'throws' qualifiedNameList
                    {
                    match(input,43,FOLLOW_43_in_voidMethodDeclaratorRest878); if (failed) return ;
                    pushFollow(FOLLOW_qualifiedNameList_in_voidMethodDeclaratorRest880);
                    qualifiedNameList();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:360:9: ( methodBody | ';' )
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==37) ) {
                alt47=1;
            }
            else if ( (LA47_0==25) ) {
                alt47=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("360:9: ( methodBody | ';' )", 47, 0, input);

                throw nvae;
            }
            switch (alt47) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:360:13: methodBody
                    {
                    pushFollow(FOLLOW_methodBody_in_voidMethodDeclaratorRest896);
                    methodBody();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:361:13: ';'
                    {
                    match(input,25,FOLLOW_25_in_voidMethodDeclaratorRest910); if (failed) return ;

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
            if ( backtracking>0 ) { memoize(input, 32, voidMethodDeclaratorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end voidMethodDeclaratorRest


    // $ANTLR start interfaceMethodDeclaratorRest
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:365:1: interfaceMethodDeclaratorRest : formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' ;
    public final void interfaceMethodDeclaratorRest() throws RecognitionException {
        int interfaceMethodDeclaratorRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 33) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:366:2: ( formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:366:4: formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';'
            {
            pushFollow(FOLLOW_formalParameters_in_interfaceMethodDeclaratorRest932);
            formalParameters();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:366:21: ( '[' ']' )*
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);

                if ( (LA48_0==41) ) {
                    alt48=1;
                }


                switch (alt48) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:366:22: '[' ']'
            	    {
            	    match(input,41,FOLLOW_41_in_interfaceMethodDeclaratorRest935); if (failed) return ;
            	    match(input,42,FOLLOW_42_in_interfaceMethodDeclaratorRest937); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop48;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:366:32: ( 'throws' qualifiedNameList )?
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==43) ) {
                alt49=1;
            }
            switch (alt49) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:366:33: 'throws' qualifiedNameList
                    {
                    match(input,43,FOLLOW_43_in_interfaceMethodDeclaratorRest942); if (failed) return ;
                    pushFollow(FOLLOW_qualifiedNameList_in_interfaceMethodDeclaratorRest944);
                    qualifiedNameList();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            match(input,25,FOLLOW_25_in_interfaceMethodDeclaratorRest948); if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 33, interfaceMethodDeclaratorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end interfaceMethodDeclaratorRest


    // $ANTLR start interfaceGenericMethodDecl
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:369:1: interfaceGenericMethodDecl : typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest ;
    public final void interfaceGenericMethodDecl() throws RecognitionException {
        int interfaceGenericMethodDecl_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 34) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:370:2: ( typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:370:4: typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest
            {
            pushFollow(FOLLOW_typeParameters_in_interfaceGenericMethodDecl960);
            typeParameters();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:370:19: ( type | 'void' )
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==Identifier||(LA50_0>=55 && LA50_0<=62)) ) {
                alt50=1;
            }
            else if ( (LA50_0==40) ) {
                alt50=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("370:19: ( type | 'void' )", 50, 0, input);

                throw nvae;
            }
            switch (alt50) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:370:20: type
                    {
                    pushFollow(FOLLOW_type_in_interfaceGenericMethodDecl963);
                    type();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:370:27: 'void'
                    {
                    match(input,40,FOLLOW_40_in_interfaceGenericMethodDecl967); if (failed) return ;

                    }
                    break;

            }

            match(input,Identifier,FOLLOW_Identifier_in_interfaceGenericMethodDecl970); if (failed) return ;
            pushFollow(FOLLOW_interfaceMethodDeclaratorRest_in_interfaceGenericMethodDecl980);
            interfaceMethodDeclaratorRest();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 34, interfaceGenericMethodDecl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end interfaceGenericMethodDecl


    // $ANTLR start voidInterfaceMethodDeclaratorRest
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:374:1: voidInterfaceMethodDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? ';' ;
    public final void voidInterfaceMethodDeclaratorRest() throws RecognitionException {
        int voidInterfaceMethodDeclaratorRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 35) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:375:2: ( formalParameters ( 'throws' qualifiedNameList )? ';' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:375:4: formalParameters ( 'throws' qualifiedNameList )? ';'
            {
            pushFollow(FOLLOW_formalParameters_in_voidInterfaceMethodDeclaratorRest992);
            formalParameters();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:375:21: ( 'throws' qualifiedNameList )?
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==43) ) {
                alt51=1;
            }
            switch (alt51) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:375:22: 'throws' qualifiedNameList
                    {
                    match(input,43,FOLLOW_43_in_voidInterfaceMethodDeclaratorRest995); if (failed) return ;
                    pushFollow(FOLLOW_qualifiedNameList_in_voidInterfaceMethodDeclaratorRest997);
                    qualifiedNameList();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            match(input,25,FOLLOW_25_in_voidInterfaceMethodDeclaratorRest1001); if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 35, voidInterfaceMethodDeclaratorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end voidInterfaceMethodDeclaratorRest


    // $ANTLR start constructorDeclaratorRest
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:378:1: constructorDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? methodBody ;
    public final void constructorDeclaratorRest() throws RecognitionException {
        int constructorDeclaratorRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 36) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:379:2: ( formalParameters ( 'throws' qualifiedNameList )? methodBody )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:379:4: formalParameters ( 'throws' qualifiedNameList )? methodBody
            {
            pushFollow(FOLLOW_formalParameters_in_constructorDeclaratorRest1013);
            formalParameters();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:379:21: ( 'throws' qualifiedNameList )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==43) ) {
                alt52=1;
            }
            switch (alt52) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:379:22: 'throws' qualifiedNameList
                    {
                    match(input,43,FOLLOW_43_in_constructorDeclaratorRest1016); if (failed) return ;
                    pushFollow(FOLLOW_qualifiedNameList_in_constructorDeclaratorRest1018);
                    qualifiedNameList();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_methodBody_in_constructorDeclaratorRest1022);
            methodBody();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 36, constructorDeclaratorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end constructorDeclaratorRest


    // $ANTLR start constantDeclarator
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:382:1: constantDeclarator : Identifier constantDeclaratorRest ;
    public final void constantDeclarator() throws RecognitionException {
        int constantDeclarator_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 37) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:383:2: ( Identifier constantDeclaratorRest )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:383:4: Identifier constantDeclaratorRest
            {
            match(input,Identifier,FOLLOW_Identifier_in_constantDeclarator1033); if (failed) return ;
            pushFollow(FOLLOW_constantDeclaratorRest_in_constantDeclarator1035);
            constantDeclaratorRest();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 37, constantDeclarator_StartIndex); }
        }
        return ;
    }
    // $ANTLR end constantDeclarator


    // $ANTLR start variableDeclarators
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:386:1: variableDeclarators : variableDeclarator ( ',' variableDeclarator )* ;
    public final void variableDeclarators() throws RecognitionException {
        int variableDeclarators_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 38) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:387:2: ( variableDeclarator ( ',' variableDeclarator )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:387:4: variableDeclarator ( ',' variableDeclarator )*
            {
            pushFollow(FOLLOW_variableDeclarator_in_variableDeclarators1047);
            variableDeclarator();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:387:23: ( ',' variableDeclarator )*
            loop53:
            do {
                int alt53=2;
                int LA53_0 = input.LA(1);

                if ( (LA53_0==34) ) {
                    alt53=1;
                }


                switch (alt53) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:387:24: ',' variableDeclarator
            	    {
            	    match(input,34,FOLLOW_34_in_variableDeclarators1050); if (failed) return ;
            	    pushFollow(FOLLOW_variableDeclarator_in_variableDeclarators1052);
            	    variableDeclarator();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop53;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 38, variableDeclarators_StartIndex); }
        }
        return ;
    }
    // $ANTLR end variableDeclarators

    protected static class variableDeclarator_scope {
        JavaLocalDeclarationDescr.IdentifierDescr ident;
    }
    protected Stack variableDeclarator_stack = new Stack();


    // $ANTLR start variableDeclarator
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:390:1: variableDeclarator : id= Identifier rest= variableDeclaratorRest ;
    public final void variableDeclarator() throws RecognitionException {
        variableDeclarator_stack.push(new variableDeclarator_scope());
        int variableDeclarator_StartIndex = input.index();
        Token id=null;
        variableDeclaratorRest_return rest = null;



        		if( this.localVariableLevel == 1 ) { // we only want top level local vars
        			((variableDeclarator_scope)variableDeclarator_stack.peek()).ident = new JavaLocalDeclarationDescr.IdentifierDescr();
        		}
        	
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 39) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:404:2: (id= Identifier rest= variableDeclaratorRest )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:404:4: id= Identifier rest= variableDeclaratorRest
            {
            id=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_variableDeclarator1084); if (failed) return ;
            pushFollow(FOLLOW_variableDeclaratorRest_in_variableDeclarator1088);
            rest=variableDeclaratorRest();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               
              			if( this.localVariableLevel == 1 ) { // we only want top level local vars
              				((variableDeclarator_scope)variableDeclarator_stack.peek()).ident.setIdentifier( id.getText() );
              				((variableDeclarator_scope)variableDeclarator_stack.peek()).ident.setStart( ((CommonToken)id).getStartIndex() - 1 );
              				if( ((Token)rest.stop) != null ) {
                 					((variableDeclarator_scope)variableDeclarator_stack.peek()).ident.setEnd( ((CommonToken)((Token)rest.stop)).getStopIndex() );
              				}
              			}
              		
            }

            }

            if ( backtracking==0 ) {

              	        if( this.localVariableLevel == 1 ) { // we only want top level local vars
              	        	((localVariableDeclaration_scope)localVariableDeclaration_stack.peek()).descr.addIdentifier( ((variableDeclarator_scope)variableDeclarator_stack.peek()).ident );
              	        }
              	
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 39, variableDeclarator_StartIndex); }
            variableDeclarator_stack.pop();
        }
        return ;
    }
    // $ANTLR end variableDeclarator

    public static class variableDeclaratorRest_return extends ParserRuleReturnScope {
    };

    // $ANTLR start variableDeclaratorRest
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:416:1: variableDeclaratorRest : ( ( '[' ']' )+ ( '=' variableInitializer )? | '=' variableInitializer | );
    public final variableDeclaratorRest_return variableDeclaratorRest() throws RecognitionException {
        variableDeclaratorRest_return retval = new variableDeclaratorRest_return();
        retval.start = input.LT(1);
        int variableDeclaratorRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 40) ) { return retval; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:417:2: ( ( '[' ']' )+ ( '=' variableInitializer )? | '=' variableInitializer | )
            int alt56=3;
            switch ( input.LA(1) ) {
            case 41:
                {
                alt56=1;
                }
                break;
            case 44:
                {
                alt56=2;
                }
                break;
            case EOF:
            case 25:
            case 34:
                {
                alt56=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("416:1: variableDeclaratorRest : ( ( '[' ']' )+ ( '=' variableInitializer )? | '=' variableInitializer | );", 56, 0, input);

                throw nvae;
            }

            switch (alt56) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:417:4: ( '[' ']' )+ ( '=' variableInitializer )?
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:417:4: ( '[' ']' )+
                    int cnt54=0;
                    loop54:
                    do {
                        int alt54=2;
                        int LA54_0 = input.LA(1);

                        if ( (LA54_0==41) ) {
                            alt54=1;
                        }


                        switch (alt54) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:417:5: '[' ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_variableDeclaratorRest1106); if (failed) return retval;
                    	    match(input,42,FOLLOW_42_in_variableDeclaratorRest1108); if (failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt54 >= 1 ) break loop54;
                    	    if (backtracking>0) {failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(54, input);
                                throw eee;
                        }
                        cnt54++;
                    } while (true);

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:417:15: ( '=' variableInitializer )?
                    int alt55=2;
                    int LA55_0 = input.LA(1);

                    if ( (LA55_0==44) ) {
                        alt55=1;
                    }
                    switch (alt55) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:417:16: '=' variableInitializer
                            {
                            match(input,44,FOLLOW_44_in_variableDeclaratorRest1113); if (failed) return retval;
                            pushFollow(FOLLOW_variableInitializer_in_variableDeclaratorRest1115);
                            variableInitializer();
                            _fsp--;
                            if (failed) return retval;

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:418:4: '=' variableInitializer
                    {
                    match(input,44,FOLLOW_44_in_variableDeclaratorRest1122); if (failed) return retval;
                    pushFollow(FOLLOW_variableInitializer_in_variableDeclaratorRest1124);
                    variableInitializer();
                    _fsp--;
                    if (failed) return retval;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:420:2: 
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
            if ( backtracking>0 ) { memoize(input, 40, variableDeclaratorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end variableDeclaratorRest


    // $ANTLR start constantDeclaratorsRest
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:422:1: constantDeclaratorsRest : constantDeclaratorRest ( ',' constantDeclarator )* ;
    public final void constantDeclaratorsRest() throws RecognitionException {
        int constantDeclaratorsRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 41) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:423:5: ( constantDeclaratorRest ( ',' constantDeclarator )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:423:9: constantDeclaratorRest ( ',' constantDeclarator )*
            {
            pushFollow(FOLLOW_constantDeclaratorRest_in_constantDeclaratorsRest1144);
            constantDeclaratorRest();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:423:32: ( ',' constantDeclarator )*
            loop57:
            do {
                int alt57=2;
                int LA57_0 = input.LA(1);

                if ( (LA57_0==34) ) {
                    alt57=1;
                }


                switch (alt57) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:423:33: ',' constantDeclarator
            	    {
            	    match(input,34,FOLLOW_34_in_constantDeclaratorsRest1147); if (failed) return ;
            	    pushFollow(FOLLOW_constantDeclarator_in_constantDeclaratorsRest1149);
            	    constantDeclarator();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop57;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 41, constantDeclaratorsRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end constantDeclaratorsRest


    // $ANTLR start constantDeclaratorRest
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:426:1: constantDeclaratorRest : ( '[' ']' )* '=' variableInitializer ;
    public final void constantDeclaratorRest() throws RecognitionException {
        int constantDeclaratorRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 42) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:427:2: ( ( '[' ']' )* '=' variableInitializer )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:427:4: ( '[' ']' )* '=' variableInitializer
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:427:4: ( '[' ']' )*
            loop58:
            do {
                int alt58=2;
                int LA58_0 = input.LA(1);

                if ( (LA58_0==41) ) {
                    alt58=1;
                }


                switch (alt58) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:427:5: '[' ']'
            	    {
            	    match(input,41,FOLLOW_41_in_constantDeclaratorRest1166); if (failed) return ;
            	    match(input,42,FOLLOW_42_in_constantDeclaratorRest1168); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop58;
                }
            } while (true);

            match(input,44,FOLLOW_44_in_constantDeclaratorRest1172); if (failed) return ;
            pushFollow(FOLLOW_variableInitializer_in_constantDeclaratorRest1174);
            variableInitializer();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 42, constantDeclaratorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end constantDeclaratorRest


    // $ANTLR start variableDeclaratorId
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:430:1: variableDeclaratorId : Identifier ( '[' ']' )* ;
    public final void variableDeclaratorId() throws RecognitionException {
        int variableDeclaratorId_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 43) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:431:2: ( Identifier ( '[' ']' )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:431:4: Identifier ( '[' ']' )*
            {
            match(input,Identifier,FOLLOW_Identifier_in_variableDeclaratorId1186); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:431:15: ( '[' ']' )*
            loop59:
            do {
                int alt59=2;
                int LA59_0 = input.LA(1);

                if ( (LA59_0==41) ) {
                    alt59=1;
                }


                switch (alt59) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:431:16: '[' ']'
            	    {
            	    match(input,41,FOLLOW_41_in_variableDeclaratorId1189); if (failed) return ;
            	    match(input,42,FOLLOW_42_in_variableDeclaratorId1191); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop59;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 43, variableDeclaratorId_StartIndex); }
        }
        return ;
    }
    // $ANTLR end variableDeclaratorId


    // $ANTLR start variableInitializer
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:434:1: variableInitializer : ( arrayInitializer | expression );
    public final void variableInitializer() throws RecognitionException {
        int variableInitializer_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 44) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:435:2: ( arrayInitializer | expression )
            int alt60=2;
            int LA60_0 = input.LA(1);

            if ( (LA60_0==37) ) {
                alt60=1;
            }
            else if ( (LA60_0==Identifier||(LA60_0>=FloatingPointLiteral && LA60_0<=DecimalLiteral)||LA60_0==33||LA60_0==40||(LA60_0>=55 && LA60_0<=62)||(LA60_0>=64 && LA60_0<=65)||(LA60_0>=68 && LA60_0<=70)||(LA60_0>=105 && LA60_0<=106)||(LA60_0>=109 && LA60_0<=114)) ) {
                alt60=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("434:1: variableInitializer : ( arrayInitializer | expression );", 60, 0, input);

                throw nvae;
            }
            switch (alt60) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:435:4: arrayInitializer
                    {
                    pushFollow(FOLLOW_arrayInitializer_in_variableInitializer1204);
                    arrayInitializer();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:436:9: expression
                    {
                    pushFollow(FOLLOW_expression_in_variableInitializer1214);
                    expression();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 44, variableInitializer_StartIndex); }
        }
        return ;
    }
    // $ANTLR end variableInitializer


    // $ANTLR start arrayInitializer
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:439:1: arrayInitializer : '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}' ;
    public final void arrayInitializer() throws RecognitionException {
        int arrayInitializer_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 45) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:440:2: ( '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:440:4: '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}'
            {
            match(input,37,FOLLOW_37_in_arrayInitializer1226); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:440:8: ( variableInitializer ( ',' variableInitializer )* ( ',' )? )?
            int alt63=2;
            int LA63_0 = input.LA(1);

            if ( (LA63_0==Identifier||(LA63_0>=FloatingPointLiteral && LA63_0<=DecimalLiteral)||LA63_0==33||LA63_0==37||LA63_0==40||(LA63_0>=55 && LA63_0<=62)||(LA63_0>=64 && LA63_0<=65)||(LA63_0>=68 && LA63_0<=70)||(LA63_0>=105 && LA63_0<=106)||(LA63_0>=109 && LA63_0<=114)) ) {
                alt63=1;
            }
            switch (alt63) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:440:9: variableInitializer ( ',' variableInitializer )* ( ',' )?
                    {
                    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer1229);
                    variableInitializer();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:440:29: ( ',' variableInitializer )*
                    loop61:
                    do {
                        int alt61=2;
                        int LA61_0 = input.LA(1);

                        if ( (LA61_0==34) ) {
                            int LA61_1 = input.LA(2);

                            if ( (LA61_1==Identifier||(LA61_1>=FloatingPointLiteral && LA61_1<=DecimalLiteral)||LA61_1==33||LA61_1==37||LA61_1==40||(LA61_1>=55 && LA61_1<=62)||(LA61_1>=64 && LA61_1<=65)||(LA61_1>=68 && LA61_1<=70)||(LA61_1>=105 && LA61_1<=106)||(LA61_1>=109 && LA61_1<=114)) ) {
                                alt61=1;
                            }


                        }


                        switch (alt61) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:440:30: ',' variableInitializer
                    	    {
                    	    match(input,34,FOLLOW_34_in_arrayInitializer1232); if (failed) return ;
                    	    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer1234);
                    	    variableInitializer();
                    	    _fsp--;
                    	    if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop61;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:440:56: ( ',' )?
                    int alt62=2;
                    int LA62_0 = input.LA(1);

                    if ( (LA62_0==34) ) {
                        alt62=1;
                    }
                    switch (alt62) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:440:57: ','
                            {
                            match(input,34,FOLLOW_34_in_arrayInitializer1239); if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }

            match(input,38,FOLLOW_38_in_arrayInitializer1246); if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 45, arrayInitializer_StartIndex); }
        }
        return ;
    }
    // $ANTLR end arrayInitializer


    // $ANTLR start modifier
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:443:1: modifier : ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' );
    public final void modifier() throws RecognitionException {
        int modifier_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 46) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:444:5: ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' )
            int alt64=12;
            switch ( input.LA(1) ) {
            case 71:
                {
                alt64=1;
                }
                break;
            case 45:
                {
                alt64=2;
                }
                break;
            case 46:
                {
                alt64=3;
                }
                break;
            case 47:
                {
                alt64=4;
                }
                break;
            case 27:
                {
                alt64=5;
                }
                break;
            case 48:
                {
                alt64=6;
                }
                break;
            case 49:
                {
                alt64=7;
                }
                break;
            case 50:
                {
                alt64=8;
                }
                break;
            case 51:
                {
                alt64=9;
                }
                break;
            case 52:
                {
                alt64=10;
                }
                break;
            case 53:
                {
                alt64=11;
                }
                break;
            case 54:
                {
                alt64=12;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("443:1: modifier : ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' );", 64, 0, input);

                throw nvae;
            }

            switch (alt64) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:444:9: annotation
                    {
                    pushFollow(FOLLOW_annotation_in_modifier1262);
                    annotation();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:445:9: 'public'
                    {
                    match(input,45,FOLLOW_45_in_modifier1272); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:446:9: 'protected'
                    {
                    match(input,46,FOLLOW_46_in_modifier1282); if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:447:9: 'private'
                    {
                    match(input,47,FOLLOW_47_in_modifier1292); if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:448:9: 'static'
                    {
                    match(input,27,FOLLOW_27_in_modifier1302); if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:449:9: 'abstract'
                    {
                    match(input,48,FOLLOW_48_in_modifier1312); if (failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:450:9: 'final'
                    {
                    match(input,49,FOLLOW_49_in_modifier1322); if (failed) return ;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:451:9: 'native'
                    {
                    match(input,50,FOLLOW_50_in_modifier1332); if (failed) return ;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:452:9: 'synchronized'
                    {
                    match(input,51,FOLLOW_51_in_modifier1342); if (failed) return ;

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:453:9: 'transient'
                    {
                    match(input,52,FOLLOW_52_in_modifier1352); if (failed) return ;

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:454:9: 'volatile'
                    {
                    match(input,53,FOLLOW_53_in_modifier1362); if (failed) return ;

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:455:9: 'strictfp'
                    {
                    match(input,54,FOLLOW_54_in_modifier1372); if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 46, modifier_StartIndex); }
        }
        return ;
    }
    // $ANTLR end modifier


    // $ANTLR start packageOrTypeName
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:458:1: packageOrTypeName : Identifier ( '.' Identifier )* ;
    public final void packageOrTypeName() throws RecognitionException {
        int packageOrTypeName_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 47) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:459:2: ( Identifier ( '.' Identifier )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:459:4: Identifier ( '.' Identifier )*
            {
            match(input,Identifier,FOLLOW_Identifier_in_packageOrTypeName1386); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:459:15: ( '.' Identifier )*
            loop65:
            do {
                int alt65=2;
                int LA65_0 = input.LA(1);

                if ( (LA65_0==28) ) {
                    int LA65_1 = input.LA(2);

                    if ( (LA65_1==Identifier) ) {
                        int LA65_2 = input.LA(3);

                        if ( (synpred85()) ) {
                            alt65=1;
                        }


                    }


                }


                switch (alt65) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:459:16: '.' Identifier
            	    {
            	    match(input,28,FOLLOW_28_in_packageOrTypeName1389); if (failed) return ;
            	    match(input,Identifier,FOLLOW_Identifier_in_packageOrTypeName1391); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop65;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 47, packageOrTypeName_StartIndex); }
        }
        return ;
    }
    // $ANTLR end packageOrTypeName


    // $ANTLR start enumConstantName
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:462:1: enumConstantName : Identifier ;
    public final void enumConstantName() throws RecognitionException {
        int enumConstantName_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 48) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:463:5: ( Identifier )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:463:9: Identifier
            {
            match(input,Identifier,FOLLOW_Identifier_in_enumConstantName1409); if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 48, enumConstantName_StartIndex); }
        }
        return ;
    }
    // $ANTLR end enumConstantName


    // $ANTLR start typeName
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:466:1: typeName : ( Identifier | packageOrTypeName '.' Identifier );
    public final void typeName() throws RecognitionException {
        int typeName_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 49) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:467:2: ( Identifier | packageOrTypeName '.' Identifier )
            int alt66=2;
            int LA66_0 = input.LA(1);

            if ( (LA66_0==Identifier) ) {
                int LA66_1 = input.LA(2);

                if ( (LA66_1==EOF) ) {
                    alt66=1;
                }
                else if ( (LA66_1==28) ) {
                    alt66=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("466:1: typeName : ( Identifier | packageOrTypeName '.' Identifier );", 66, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("466:1: typeName : ( Identifier | packageOrTypeName '.' Identifier );", 66, 0, input);

                throw nvae;
            }
            switch (alt66) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:467:6: Identifier
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_typeName1425); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:468:9: packageOrTypeName '.' Identifier
                    {
                    pushFollow(FOLLOW_packageOrTypeName_in_typeName1435);
                    packageOrTypeName();
                    _fsp--;
                    if (failed) return ;
                    match(input,28,FOLLOW_28_in_typeName1437); if (failed) return ;
                    match(input,Identifier,FOLLOW_Identifier_in_typeName1439); if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 49, typeName_StartIndex); }
        }
        return ;
    }
    // $ANTLR end typeName

    public static class type_return extends ParserRuleReturnScope {
    };

    // $ANTLR start type
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:471:1: type : ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* ( '[' ']' )* | primitiveType ( '[' ']' )* );
    public final type_return type() throws RecognitionException {
        type_return retval = new type_return();
        retval.start = input.LT(1);
        int type_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 50) ) { return retval; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:472:2: ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* ( '[' ']' )* | primitiveType ( '[' ']' )* )
            int alt72=2;
            int LA72_0 = input.LA(1);

            if ( (LA72_0==Identifier) ) {
                alt72=1;
            }
            else if ( ((LA72_0>=55 && LA72_0<=62)) ) {
                alt72=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("471:1: type : ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* ( '[' ']' )* | primitiveType ( '[' ']' )* );", 72, 0, input);

                throw nvae;
            }
            switch (alt72) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:472:4: Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* ( '[' ']' )*
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_type1450); if (failed) return retval;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:472:15: ( typeArguments )?
                    int alt67=2;
                    int LA67_0 = input.LA(1);

                    if ( (LA67_0==33) ) {
                        int LA67_1 = input.LA(2);

                        if ( (LA67_1==Identifier||(LA67_1>=55 && LA67_1<=63)) ) {
                            alt67=1;
                        }
                    }
                    switch (alt67) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:472:16: typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_type1453);
                            typeArguments();
                            _fsp--;
                            if (failed) return retval;

                            }
                            break;

                    }

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:472:32: ( '.' Identifier ( typeArguments )? )*
                    loop69:
                    do {
                        int alt69=2;
                        int LA69_0 = input.LA(1);

                        if ( (LA69_0==28) ) {
                            alt69=1;
                        }


                        switch (alt69) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:472:33: '.' Identifier ( typeArguments )?
                    	    {
                    	    match(input,28,FOLLOW_28_in_type1458); if (failed) return retval;
                    	    match(input,Identifier,FOLLOW_Identifier_in_type1460); if (failed) return retval;
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:472:48: ( typeArguments )?
                    	    int alt68=2;
                    	    int LA68_0 = input.LA(1);

                    	    if ( (LA68_0==33) ) {
                    	        int LA68_1 = input.LA(2);

                    	        if ( (LA68_1==Identifier||(LA68_1>=55 && LA68_1<=63)) ) {
                    	            alt68=1;
                    	        }
                    	    }
                    	    switch (alt68) {
                    	        case 1 :
                    	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:472:49: typeArguments
                    	            {
                    	            pushFollow(FOLLOW_typeArguments_in_type1463);
                    	            typeArguments();
                    	            _fsp--;
                    	            if (failed) return retval;

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop69;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:472:68: ( '[' ']' )*
                    loop70:
                    do {
                        int alt70=2;
                        int LA70_0 = input.LA(1);

                        if ( (LA70_0==41) ) {
                            alt70=1;
                        }


                        switch (alt70) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:472:69: '[' ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_type1471); if (failed) return retval;
                    	    match(input,42,FOLLOW_42_in_type1473); if (failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    break loop70;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:473:4: primitiveType ( '[' ']' )*
                    {
                    pushFollow(FOLLOW_primitiveType_in_type1480);
                    primitiveType();
                    _fsp--;
                    if (failed) return retval;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:473:18: ( '[' ']' )*
                    loop71:
                    do {
                        int alt71=2;
                        int LA71_0 = input.LA(1);

                        if ( (LA71_0==41) ) {
                            alt71=1;
                        }


                        switch (alt71) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:473:19: '[' ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_type1483); if (failed) return retval;
                    	    match(input,42,FOLLOW_42_in_type1485); if (failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    break loop71;
                        }
                    } while (true);


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
            if ( backtracking>0 ) { memoize(input, 50, type_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end type


    // $ANTLR start primitiveType
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:476:1: primitiveType : ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' );
    public final void primitiveType() throws RecognitionException {
        int primitiveType_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 51) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:477:5: ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:
            {
            if ( (input.LA(1)>=55 && input.LA(1)<=62) ) {
                input.consume();
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_primitiveType0);    throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 51, primitiveType_StartIndex); }
        }
        return ;
    }
    // $ANTLR end primitiveType

    public static class variableModifier_return extends ParserRuleReturnScope {
    };

    // $ANTLR start variableModifier
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:487:1: variableModifier : ( 'final' | annotation );
    public final variableModifier_return variableModifier() throws RecognitionException {
        variableModifier_return retval = new variableModifier_return();
        retval.start = input.LT(1);
        int variableModifier_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 52) ) { return retval; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:488:2: ( 'final' | annotation )
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==49) ) {
                alt73=1;
            }
            else if ( (LA73_0==71) ) {
                alt73=2;
            }
            else {
                if (backtracking>0) {failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("487:1: variableModifier : ( 'final' | annotation );", 73, 0, input);

                throw nvae;
            }
            switch (alt73) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:488:4: 'final'
                    {
                    match(input,49,FOLLOW_49_in_variableModifier1573); if (failed) return retval;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:489:9: annotation
                    {
                    pushFollow(FOLLOW_annotation_in_variableModifier1583);
                    annotation();
                    _fsp--;
                    if (failed) return retval;

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
            if ( backtracking>0 ) { memoize(input, 52, variableModifier_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end variableModifier


    // $ANTLR start typeArguments
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:492:1: typeArguments : '<' typeArgument ( ',' typeArgument )* '>' ;
    public final void typeArguments() throws RecognitionException {
        int typeArguments_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 53) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:493:2: ( '<' typeArgument ( ',' typeArgument )* '>' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:493:4: '<' typeArgument ( ',' typeArgument )* '>'
            {
            match(input,33,FOLLOW_33_in_typeArguments1594); if (failed) return ;
            pushFollow(FOLLOW_typeArgument_in_typeArguments1596);
            typeArgument();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:493:21: ( ',' typeArgument )*
            loop74:
            do {
                int alt74=2;
                int LA74_0 = input.LA(1);

                if ( (LA74_0==34) ) {
                    alt74=1;
                }


                switch (alt74) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:493:22: ',' typeArgument
            	    {
            	    match(input,34,FOLLOW_34_in_typeArguments1599); if (failed) return ;
            	    pushFollow(FOLLOW_typeArgument_in_typeArguments1601);
            	    typeArgument();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop74;
                }
            } while (true);

            match(input,35,FOLLOW_35_in_typeArguments1605); if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 53, typeArguments_StartIndex); }
        }
        return ;
    }
    // $ANTLR end typeArguments


    // $ANTLR start typeArgument
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:496:1: typeArgument : ( type | '?' ( ( 'extends' | 'super' ) type )? );
    public final void typeArgument() throws RecognitionException {
        int typeArgument_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 54) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:497:2: ( type | '?' ( ( 'extends' | 'super' ) type )? )
            int alt76=2;
            int LA76_0 = input.LA(1);

            if ( (LA76_0==Identifier||(LA76_0>=55 && LA76_0<=62)) ) {
                alt76=1;
            }
            else if ( (LA76_0==63) ) {
                alt76=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("496:1: typeArgument : ( type | '?' ( ( 'extends' | 'super' ) type )? );", 76, 0, input);

                throw nvae;
            }
            switch (alt76) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:497:4: type
                    {
                    pushFollow(FOLLOW_type_in_typeArgument1617);
                    type();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:498:4: '?' ( ( 'extends' | 'super' ) type )?
                    {
                    match(input,63,FOLLOW_63_in_typeArgument1622); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:498:8: ( ( 'extends' | 'super' ) type )?
                    int alt75=2;
                    int LA75_0 = input.LA(1);

                    if ( (LA75_0==31||LA75_0==64) ) {
                        alt75=1;
                    }
                    switch (alt75) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:498:9: ( 'extends' | 'super' ) type
                            {
                            if ( input.LA(1)==31||input.LA(1)==64 ) {
                                input.consume();
                                errorRecovery=false;failed=false;
                            }
                            else {
                                if (backtracking>0) {failed=true; return ;}
                                MismatchedSetException mse =
                                    new MismatchedSetException(null,input);
                                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_typeArgument1625);    throw mse;
                            }

                            pushFollow(FOLLOW_type_in_typeArgument1633);
                            type();
                            _fsp--;
                            if (failed) return ;

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
            if ( backtracking>0 ) { memoize(input, 54, typeArgument_StartIndex); }
        }
        return ;
    }
    // $ANTLR end typeArgument


    // $ANTLR start qualifiedNameList
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:501:1: qualifiedNameList : qualifiedName ( ',' qualifiedName )* ;
    public final void qualifiedNameList() throws RecognitionException {
        int qualifiedNameList_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 55) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:502:2: ( qualifiedName ( ',' qualifiedName )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:502:4: qualifiedName ( ',' qualifiedName )*
            {
            pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList1647);
            qualifiedName();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:502:18: ( ',' qualifiedName )*
            loop77:
            do {
                int alt77=2;
                int LA77_0 = input.LA(1);

                if ( (LA77_0==34) ) {
                    alt77=1;
                }


                switch (alt77) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:502:19: ',' qualifiedName
            	    {
            	    match(input,34,FOLLOW_34_in_qualifiedNameList1650); if (failed) return ;
            	    pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList1652);
            	    qualifiedName();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop77;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 55, qualifiedNameList_StartIndex); }
        }
        return ;
    }
    // $ANTLR end qualifiedNameList


    // $ANTLR start formalParameters
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:505:1: formalParameters : '(' ( formalParameterDecls )? ')' ;
    public final void formalParameters() throws RecognitionException {
        int formalParameters_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 56) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:506:2: ( '(' ( formalParameterDecls )? ')' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:506:4: '(' ( formalParameterDecls )? ')'
            {
            match(input,65,FOLLOW_65_in_formalParameters1666); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:506:8: ( formalParameterDecls )?
            int alt78=2;
            int LA78_0 = input.LA(1);

            if ( (LA78_0==Identifier||LA78_0==49||(LA78_0>=55 && LA78_0<=62)||LA78_0==71) ) {
                alt78=1;
            }
            switch (alt78) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: formalParameterDecls
                    {
                    pushFollow(FOLLOW_formalParameterDecls_in_formalParameters1668);
                    formalParameterDecls();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            match(input,66,FOLLOW_66_in_formalParameters1671); if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 56, formalParameters_StartIndex); }
        }
        return ;
    }
    // $ANTLR end formalParameters


    // $ANTLR start formalParameterDecls
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:509:1: formalParameterDecls : ( variableModifier )* type ( formalParameterDeclsRest )? ;
    public final void formalParameterDecls() throws RecognitionException {
        int formalParameterDecls_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 57) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:510:2: ( ( variableModifier )* type ( formalParameterDeclsRest )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:510:4: ( variableModifier )* type ( formalParameterDeclsRest )?
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:510:4: ( variableModifier )*
            loop79:
            do {
                int alt79=2;
                int LA79_0 = input.LA(1);

                if ( (LA79_0==49||LA79_0==71) ) {
                    alt79=1;
                }


                switch (alt79) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: variableModifier
            	    {
            	    pushFollow(FOLLOW_variableModifier_in_formalParameterDecls1683);
            	    variableModifier();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop79;
                }
            } while (true);

            pushFollow(FOLLOW_type_in_formalParameterDecls1686);
            type();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:510:27: ( formalParameterDeclsRest )?
            int alt80=2;
            int LA80_0 = input.LA(1);

            if ( (LA80_0==Identifier||LA80_0==67) ) {
                alt80=1;
            }
            switch (alt80) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: formalParameterDeclsRest
                    {
                    pushFollow(FOLLOW_formalParameterDeclsRest_in_formalParameterDecls1688);
                    formalParameterDeclsRest();
                    _fsp--;
                    if (failed) return ;

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
            if ( backtracking>0 ) { memoize(input, 57, formalParameterDecls_StartIndex); }
        }
        return ;
    }
    // $ANTLR end formalParameterDecls


    // $ANTLR start formalParameterDeclsRest
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:513:1: formalParameterDeclsRest : ( variableDeclaratorId ( ',' formalParameterDecls )? | '...' variableDeclaratorId );
    public final void formalParameterDeclsRest() throws RecognitionException {
        int formalParameterDeclsRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 58) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:514:2: ( variableDeclaratorId ( ',' formalParameterDecls )? | '...' variableDeclaratorId )
            int alt82=2;
            int LA82_0 = input.LA(1);

            if ( (LA82_0==Identifier) ) {
                alt82=1;
            }
            else if ( (LA82_0==67) ) {
                alt82=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("513:1: formalParameterDeclsRest : ( variableDeclaratorId ( ',' formalParameterDecls )? | '...' variableDeclaratorId );", 82, 0, input);

                throw nvae;
            }
            switch (alt82) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:514:4: variableDeclaratorId ( ',' formalParameterDecls )?
                    {
                    pushFollow(FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest1701);
                    variableDeclaratorId();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:514:25: ( ',' formalParameterDecls )?
                    int alt81=2;
                    int LA81_0 = input.LA(1);

                    if ( (LA81_0==34) ) {
                        alt81=1;
                    }
                    switch (alt81) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:514:26: ',' formalParameterDecls
                            {
                            match(input,34,FOLLOW_34_in_formalParameterDeclsRest1704); if (failed) return ;
                            pushFollow(FOLLOW_formalParameterDecls_in_formalParameterDeclsRest1706);
                            formalParameterDecls();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:515:6: '...' variableDeclaratorId
                    {
                    match(input,67,FOLLOW_67_in_formalParameterDeclsRest1715); if (failed) return ;
                    pushFollow(FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest1717);
                    variableDeclaratorId();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 58, formalParameterDeclsRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end formalParameterDeclsRest


    // $ANTLR start methodBody
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:518:1: methodBody : block ;
    public final void methodBody() throws RecognitionException {
        int methodBody_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 59) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:519:2: ( block )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:519:4: block
            {
            pushFollow(FOLLOW_block_in_methodBody1729);
            block();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 59, methodBody_StartIndex); }
        }
        return ;
    }
    // $ANTLR end methodBody


    // $ANTLR start qualifiedName
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:522:1: qualifiedName : Identifier ( '.' Identifier )* ;
    public final void qualifiedName() throws RecognitionException {
        int qualifiedName_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 60) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:523:2: ( Identifier ( '.' Identifier )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:523:4: Identifier ( '.' Identifier )*
            {
            match(input,Identifier,FOLLOW_Identifier_in_qualifiedName1740); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:523:15: ( '.' Identifier )*
            loop83:
            do {
                int alt83=2;
                int LA83_0 = input.LA(1);

                if ( (LA83_0==28) ) {
                    alt83=1;
                }


                switch (alt83) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:523:16: '.' Identifier
            	    {
            	    match(input,28,FOLLOW_28_in_qualifiedName1743); if (failed) return ;
            	    match(input,Identifier,FOLLOW_Identifier_in_qualifiedName1745); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop83;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 60, qualifiedName_StartIndex); }
        }
        return ;
    }
    // $ANTLR end qualifiedName


    // $ANTLR start literal
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:526:1: literal : ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | booleanLiteral | 'null' );
    public final void literal() throws RecognitionException {
        int literal_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 61) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:527:2: ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | booleanLiteral | 'null' )
            int alt84=6;
            switch ( input.LA(1) ) {
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
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
            case 69:
            case 70:
                {
                alt84=5;
                }
                break;
            case 68:
                {
                alt84=6;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("526:1: literal : ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | booleanLiteral | 'null' );", 84, 0, input);

                throw nvae;
            }

            switch (alt84) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:527:6: integerLiteral
                    {
                    pushFollow(FOLLOW_integerLiteral_in_literal1762);
                    integerLiteral();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:528:9: FloatingPointLiteral
                    {
                    match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_literal1772); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:529:9: CharacterLiteral
                    {
                    match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_literal1782); if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:530:9: StringLiteral
                    {
                    match(input,StringLiteral,FOLLOW_StringLiteral_in_literal1792); if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:531:9: booleanLiteral
                    {
                    pushFollow(FOLLOW_booleanLiteral_in_literal1802);
                    booleanLiteral();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:532:9: 'null'
                    {
                    match(input,68,FOLLOW_68_in_literal1812); if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 61, literal_StartIndex); }
        }
        return ;
    }
    // $ANTLR end literal


    // $ANTLR start integerLiteral
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:535:1: integerLiteral : ( HexLiteral | OctalLiteral | DecimalLiteral );
    public final void integerLiteral() throws RecognitionException {
        int integerLiteral_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 62) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:536:5: ( HexLiteral | OctalLiteral | DecimalLiteral )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:
            {
            if ( (input.LA(1)>=HexLiteral && input.LA(1)<=DecimalLiteral) ) {
                input.consume();
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_integerLiteral0);    throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 62, integerLiteral_StartIndex); }
        }
        return ;
    }
    // $ANTLR end integerLiteral


    // $ANTLR start booleanLiteral
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:541:1: booleanLiteral : ( 'true' | 'false' );
    public final void booleanLiteral() throws RecognitionException {
        int booleanLiteral_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 63) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:542:5: ( 'true' | 'false' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:
            {
            if ( (input.LA(1)>=69 && input.LA(1)<=70) ) {
                input.consume();
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_booleanLiteral0);    throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 63, booleanLiteral_StartIndex); }
        }
        return ;
    }
    // $ANTLR end booleanLiteral


    // $ANTLR start annotations
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:548:1: annotations : ( annotation )+ ;
    public final void annotations() throws RecognitionException {
        int annotations_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 64) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:549:2: ( ( annotation )+ )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:549:4: ( annotation )+
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:549:4: ( annotation )+
            int cnt85=0;
            loop85:
            do {
                int alt85=2;
                int LA85_0 = input.LA(1);

                if ( (LA85_0==71) ) {
                    int LA85_3 = input.LA(2);

                    if ( (LA85_3==Identifier) ) {
                        int LA85_22 = input.LA(3);

                        if ( (synpred120()) ) {
                            alt85=1;
                        }


                    }


                }


                switch (alt85) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: annotation
            	    {
            	    pushFollow(FOLLOW_annotation_in_annotations1893);
            	    annotation();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt85 >= 1 ) break loop85;
            	    if (backtracking>0) {failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(85, input);
                        throw eee;
                }
                cnt85++;
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 64, annotations_StartIndex); }
        }
        return ;
    }
    // $ANTLR end annotations


    // $ANTLR start annotation
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:552:1: annotation : '@' annotationName ( '(' ( elementValuePairs )? ')' )? ;
    public final void annotation() throws RecognitionException {
        int annotation_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 65) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:553:2: ( '@' annotationName ( '(' ( elementValuePairs )? ')' )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:553:4: '@' annotationName ( '(' ( elementValuePairs )? ')' )?
            {
            match(input,71,FOLLOW_71_in_annotation1905); if (failed) return ;
            pushFollow(FOLLOW_annotationName_in_annotation1907);
            annotationName();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:553:23: ( '(' ( elementValuePairs )? ')' )?
            int alt87=2;
            int LA87_0 = input.LA(1);

            if ( (LA87_0==65) ) {
                alt87=1;
            }
            switch (alt87) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:553:24: '(' ( elementValuePairs )? ')'
                    {
                    match(input,65,FOLLOW_65_in_annotation1910); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:553:28: ( elementValuePairs )?
                    int alt86=2;
                    int LA86_0 = input.LA(1);

                    if ( (LA86_0==Identifier||(LA86_0>=FloatingPointLiteral && LA86_0<=DecimalLiteral)||LA86_0==33||LA86_0==37||LA86_0==40||(LA86_0>=55 && LA86_0<=62)||(LA86_0>=64 && LA86_0<=65)||(LA86_0>=68 && LA86_0<=71)||(LA86_0>=105 && LA86_0<=106)||(LA86_0>=109 && LA86_0<=114)) ) {
                        alt86=1;
                    }
                    switch (alt86) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: elementValuePairs
                            {
                            pushFollow(FOLLOW_elementValuePairs_in_annotation1912);
                            elementValuePairs();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    match(input,66,FOLLOW_66_in_annotation1915); if (failed) return ;

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
            if ( backtracking>0 ) { memoize(input, 65, annotation_StartIndex); }
        }
        return ;
    }
    // $ANTLR end annotation


    // $ANTLR start annotationName
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:556:1: annotationName : Identifier ( '.' Identifier )* ;
    public final void annotationName() throws RecognitionException {
        int annotationName_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 66) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:557:2: ( Identifier ( '.' Identifier )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:557:4: Identifier ( '.' Identifier )*
            {
            match(input,Identifier,FOLLOW_Identifier_in_annotationName1929); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:557:15: ( '.' Identifier )*
            loop88:
            do {
                int alt88=2;
                int LA88_0 = input.LA(1);

                if ( (LA88_0==28) ) {
                    alt88=1;
                }


                switch (alt88) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:557:16: '.' Identifier
            	    {
            	    match(input,28,FOLLOW_28_in_annotationName1932); if (failed) return ;
            	    match(input,Identifier,FOLLOW_Identifier_in_annotationName1934); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop88;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 66, annotationName_StartIndex); }
        }
        return ;
    }
    // $ANTLR end annotationName


    // $ANTLR start elementValuePairs
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:560:1: elementValuePairs : elementValuePair ( ',' elementValuePair )* ;
    public final void elementValuePairs() throws RecognitionException {
        int elementValuePairs_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 67) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:561:2: ( elementValuePair ( ',' elementValuePair )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:561:4: elementValuePair ( ',' elementValuePair )*
            {
            pushFollow(FOLLOW_elementValuePair_in_elementValuePairs1948);
            elementValuePair();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:561:21: ( ',' elementValuePair )*
            loop89:
            do {
                int alt89=2;
                int LA89_0 = input.LA(1);

                if ( (LA89_0==34) ) {
                    alt89=1;
                }


                switch (alt89) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:561:22: ',' elementValuePair
            	    {
            	    match(input,34,FOLLOW_34_in_elementValuePairs1951); if (failed) return ;
            	    pushFollow(FOLLOW_elementValuePair_in_elementValuePairs1953);
            	    elementValuePair();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop89;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 67, elementValuePairs_StartIndex); }
        }
        return ;
    }
    // $ANTLR end elementValuePairs


    // $ANTLR start elementValuePair
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:564:1: elementValuePair : ( Identifier '=' )? elementValue ;
    public final void elementValuePair() throws RecognitionException {
        int elementValuePair_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 68) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:565:2: ( ( Identifier '=' )? elementValue )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:565:4: ( Identifier '=' )? elementValue
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:565:4: ( Identifier '=' )?
            int alt90=2;
            int LA90_0 = input.LA(1);

            if ( (LA90_0==Identifier) ) {
                int LA90_1 = input.LA(2);

                if ( (LA90_1==44) ) {
                    alt90=1;
                }
            }
            switch (alt90) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:565:5: Identifier '='
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_elementValuePair1968); if (failed) return ;
                    match(input,44,FOLLOW_44_in_elementValuePair1970); if (failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_elementValue_in_elementValuePair1974);
            elementValue();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 68, elementValuePair_StartIndex); }
        }
        return ;
    }
    // $ANTLR end elementValuePair


    // $ANTLR start elementValue
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:568:1: elementValue : ( conditionalExpression | annotation | elementValueArrayInitializer );
    public final void elementValue() throws RecognitionException {
        int elementValue_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 69) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:569:2: ( conditionalExpression | annotation | elementValueArrayInitializer )
            int alt91=3;
            switch ( input.LA(1) ) {
            case Identifier:
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
            case 33:
            case 40:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 64:
            case 65:
            case 68:
            case 69:
            case 70:
            case 105:
            case 106:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
                {
                alt91=1;
                }
                break;
            case 71:
                {
                alt91=2;
                }
                break;
            case 37:
                {
                alt91=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("568:1: elementValue : ( conditionalExpression | annotation | elementValueArrayInitializer );", 91, 0, input);

                throw nvae;
            }

            switch (alt91) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:569:4: conditionalExpression
                    {
                    pushFollow(FOLLOW_conditionalExpression_in_elementValue1986);
                    conditionalExpression();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:570:6: annotation
                    {
                    pushFollow(FOLLOW_annotation_in_elementValue1993);
                    annotation();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:571:6: elementValueArrayInitializer
                    {
                    pushFollow(FOLLOW_elementValueArrayInitializer_in_elementValue2000);
                    elementValueArrayInitializer();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 69, elementValue_StartIndex); }
        }
        return ;
    }
    // $ANTLR end elementValue


    // $ANTLR start elementValueArrayInitializer
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:574:1: elementValueArrayInitializer : '{' ( elementValue ( ',' elementValue )* )? '}' ;
    public final void elementValueArrayInitializer() throws RecognitionException {
        int elementValueArrayInitializer_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 70) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:575:2: ( '{' ( elementValue ( ',' elementValue )* )? '}' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:575:4: '{' ( elementValue ( ',' elementValue )* )? '}'
            {
            match(input,37,FOLLOW_37_in_elementValueArrayInitializer2012); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:575:8: ( elementValue ( ',' elementValue )* )?
            int alt93=2;
            int LA93_0 = input.LA(1);

            if ( (LA93_0==Identifier||(LA93_0>=FloatingPointLiteral && LA93_0<=DecimalLiteral)||LA93_0==33||LA93_0==37||LA93_0==40||(LA93_0>=55 && LA93_0<=62)||(LA93_0>=64 && LA93_0<=65)||(LA93_0>=68 && LA93_0<=71)||(LA93_0>=105 && LA93_0<=106)||(LA93_0>=109 && LA93_0<=114)) ) {
                alt93=1;
            }
            switch (alt93) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:575:9: elementValue ( ',' elementValue )*
                    {
                    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer2015);
                    elementValue();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:575:22: ( ',' elementValue )*
                    loop92:
                    do {
                        int alt92=2;
                        int LA92_0 = input.LA(1);

                        if ( (LA92_0==34) ) {
                            alt92=1;
                        }


                        switch (alt92) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:575:23: ',' elementValue
                    	    {
                    	    match(input,34,FOLLOW_34_in_elementValueArrayInitializer2018); if (failed) return ;
                    	    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer2020);
                    	    elementValue();
                    	    _fsp--;
                    	    if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop92;
                        }
                    } while (true);


                    }
                    break;

            }

            match(input,38,FOLLOW_38_in_elementValueArrayInitializer2027); if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 70, elementValueArrayInitializer_StartIndex); }
        }
        return ;
    }
    // $ANTLR end elementValueArrayInitializer


    // $ANTLR start annotationTypeDeclaration
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:578:1: annotationTypeDeclaration : '@' 'interface' Identifier annotationTypeBody ;
    public final void annotationTypeDeclaration() throws RecognitionException {
        int annotationTypeDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 71) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:579:2: ( '@' 'interface' Identifier annotationTypeBody )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:579:4: '@' 'interface' Identifier annotationTypeBody
            {
            match(input,71,FOLLOW_71_in_annotationTypeDeclaration2039); if (failed) return ;
            match(input,39,FOLLOW_39_in_annotationTypeDeclaration2041); if (failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_annotationTypeDeclaration2043); if (failed) return ;
            pushFollow(FOLLOW_annotationTypeBody_in_annotationTypeDeclaration2045);
            annotationTypeBody();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 71, annotationTypeDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end annotationTypeDeclaration


    // $ANTLR start annotationTypeBody
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:582:1: annotationTypeBody : '{' ( annotationTypeElementDeclarations )? '}' ;
    public final void annotationTypeBody() throws RecognitionException {
        int annotationTypeBody_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 72) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:583:2: ( '{' ( annotationTypeElementDeclarations )? '}' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:583:4: '{' ( annotationTypeElementDeclarations )? '}'
            {
            match(input,37,FOLLOW_37_in_annotationTypeBody2057); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:583:8: ( annotationTypeElementDeclarations )?
            int alt94=2;
            int LA94_0 = input.LA(1);

            if ( ((LA94_0>=Identifier && LA94_0<=ENUM)||LA94_0==27||LA94_0==30||LA94_0==39||(LA94_0>=45 && LA94_0<=62)||LA94_0==71) ) {
                alt94=1;
            }
            switch (alt94) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:583:9: annotationTypeElementDeclarations
                    {
                    pushFollow(FOLLOW_annotationTypeElementDeclarations_in_annotationTypeBody2060);
                    annotationTypeElementDeclarations();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            match(input,38,FOLLOW_38_in_annotationTypeBody2064); if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 72, annotationTypeBody_StartIndex); }
        }
        return ;
    }
    // $ANTLR end annotationTypeBody


    // $ANTLR start annotationTypeElementDeclarations
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:586:1: annotationTypeElementDeclarations : ( annotationTypeElementDeclaration ) ( annotationTypeElementDeclaration )* ;
    public final void annotationTypeElementDeclarations() throws RecognitionException {
        int annotationTypeElementDeclarations_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 73) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:587:2: ( ( annotationTypeElementDeclaration ) ( annotationTypeElementDeclaration )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:587:4: ( annotationTypeElementDeclaration ) ( annotationTypeElementDeclaration )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:587:4: ( annotationTypeElementDeclaration )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:587:5: annotationTypeElementDeclaration
            {
            pushFollow(FOLLOW_annotationTypeElementDeclaration_in_annotationTypeElementDeclarations2077);
            annotationTypeElementDeclaration();
            _fsp--;
            if (failed) return ;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:587:39: ( annotationTypeElementDeclaration )*
            loop95:
            do {
                int alt95=2;
                int LA95_0 = input.LA(1);

                if ( ((LA95_0>=Identifier && LA95_0<=ENUM)||LA95_0==27||LA95_0==30||LA95_0==39||(LA95_0>=45 && LA95_0<=62)||LA95_0==71) ) {
                    alt95=1;
                }


                switch (alt95) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:587:40: annotationTypeElementDeclaration
            	    {
            	    pushFollow(FOLLOW_annotationTypeElementDeclaration_in_annotationTypeElementDeclarations2081);
            	    annotationTypeElementDeclaration();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop95;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 73, annotationTypeElementDeclarations_StartIndex); }
        }
        return ;
    }
    // $ANTLR end annotationTypeElementDeclarations


    // $ANTLR start annotationTypeElementDeclaration
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:590:1: annotationTypeElementDeclaration : ( modifier )* annotationTypeElementRest ;
    public final void annotationTypeElementDeclaration() throws RecognitionException {
        int annotationTypeElementDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 74) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:591:2: ( ( modifier )* annotationTypeElementRest )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:591:4: ( modifier )* annotationTypeElementRest
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:591:4: ( modifier )*
            loop96:
            do {
                int alt96=2;
                int LA96_0 = input.LA(1);

                if ( (LA96_0==71) ) {
                    int LA96_6 = input.LA(2);

                    if ( (LA96_6==Identifier) ) {
                        alt96=1;
                    }


                }
                else if ( (LA96_0==27||(LA96_0>=45 && LA96_0<=54)) ) {
                    alt96=1;
                }


                switch (alt96) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:591:5: modifier
            	    {
            	    pushFollow(FOLLOW_modifier_in_annotationTypeElementDeclaration2096);
            	    modifier();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop96;
                }
            } while (true);

            pushFollow(FOLLOW_annotationTypeElementRest_in_annotationTypeElementDeclaration2100);
            annotationTypeElementRest();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 74, annotationTypeElementDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end annotationTypeElementDeclaration


    // $ANTLR start annotationTypeElementRest
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:594:1: annotationTypeElementRest : ( type annotationMethodOrConstantRest ';' | classDeclaration ( ';' )? | interfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? );
    public final void annotationTypeElementRest() throws RecognitionException {
        int annotationTypeElementRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 75) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:595:2: ( type annotationMethodOrConstantRest ';' | classDeclaration ( ';' )? | interfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? )
            int alt101=5;
            switch ( input.LA(1) ) {
            case Identifier:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
                {
                alt101=1;
                }
                break;
            case 30:
                {
                alt101=2;
                }
                break;
            case ENUM:
                {
                int LA101_4 = input.LA(2);

                if ( (LA101_4==Identifier) ) {
                    int LA101_7 = input.LA(3);

                    if ( (synpred135()) ) {
                        alt101=2;
                    }
                    else if ( (synpred139()) ) {
                        alt101=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("594:1: annotationTypeElementRest : ( type annotationMethodOrConstantRest ';' | classDeclaration ( ';' )? | interfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? );", 101, 7, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("594:1: annotationTypeElementRest : ( type annotationMethodOrConstantRest ';' | classDeclaration ( ';' )? | interfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? );", 101, 4, input);

                    throw nvae;
                }
                }
                break;
            case 39:
                {
                alt101=3;
                }
                break;
            case 71:
                {
                int LA101_6 = input.LA(2);

                if ( (LA101_6==39) ) {
                    int LA101_8 = input.LA(3);

                    if ( (synpred137()) ) {
                        alt101=3;
                    }
                    else if ( (true) ) {
                        alt101=5;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("594:1: annotationTypeElementRest : ( type annotationMethodOrConstantRest ';' | classDeclaration ( ';' )? | interfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? );", 101, 8, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("594:1: annotationTypeElementRest : ( type annotationMethodOrConstantRest ';' | classDeclaration ( ';' )? | interfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? );", 101, 6, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("594:1: annotationTypeElementRest : ( type annotationMethodOrConstantRest ';' | classDeclaration ( ';' )? | interfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? );", 101, 0, input);

                throw nvae;
            }

            switch (alt101) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:595:4: type annotationMethodOrConstantRest ';'
                    {
                    pushFollow(FOLLOW_type_in_annotationTypeElementRest2112);
                    type();
                    _fsp--;
                    if (failed) return ;
                    pushFollow(FOLLOW_annotationMethodOrConstantRest_in_annotationTypeElementRest2114);
                    annotationMethodOrConstantRest();
                    _fsp--;
                    if (failed) return ;
                    match(input,25,FOLLOW_25_in_annotationTypeElementRest2116); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:596:6: classDeclaration ( ';' )?
                    {
                    pushFollow(FOLLOW_classDeclaration_in_annotationTypeElementRest2123);
                    classDeclaration();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:596:23: ( ';' )?
                    int alt97=2;
                    int LA97_0 = input.LA(1);

                    if ( (LA97_0==25) ) {
                        alt97=1;
                    }
                    switch (alt97) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ';'
                            {
                            match(input,25,FOLLOW_25_in_annotationTypeElementRest2125); if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:597:6: interfaceDeclaration ( ';' )?
                    {
                    pushFollow(FOLLOW_interfaceDeclaration_in_annotationTypeElementRest2133);
                    interfaceDeclaration();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:597:27: ( ';' )?
                    int alt98=2;
                    int LA98_0 = input.LA(1);

                    if ( (LA98_0==25) ) {
                        alt98=1;
                    }
                    switch (alt98) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ';'
                            {
                            match(input,25,FOLLOW_25_in_annotationTypeElementRest2135); if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:598:6: enumDeclaration ( ';' )?
                    {
                    pushFollow(FOLLOW_enumDeclaration_in_annotationTypeElementRest2143);
                    enumDeclaration();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:598:22: ( ';' )?
                    int alt99=2;
                    int LA99_0 = input.LA(1);

                    if ( (LA99_0==25) ) {
                        alt99=1;
                    }
                    switch (alt99) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ';'
                            {
                            match(input,25,FOLLOW_25_in_annotationTypeElementRest2145); if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:599:6: annotationTypeDeclaration ( ';' )?
                    {
                    pushFollow(FOLLOW_annotationTypeDeclaration_in_annotationTypeElementRest2153);
                    annotationTypeDeclaration();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:599:32: ( ';' )?
                    int alt100=2;
                    int LA100_0 = input.LA(1);

                    if ( (LA100_0==25) ) {
                        alt100=1;
                    }
                    switch (alt100) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ';'
                            {
                            match(input,25,FOLLOW_25_in_annotationTypeElementRest2155); if (failed) return ;

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
            if ( backtracking>0 ) { memoize(input, 75, annotationTypeElementRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end annotationTypeElementRest


    // $ANTLR start annotationMethodOrConstantRest
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:602:1: annotationMethodOrConstantRest : ( annotationMethodRest | annotationConstantRest );
    public final void annotationMethodOrConstantRest() throws RecognitionException {
        int annotationMethodOrConstantRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 76) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:603:2: ( annotationMethodRest | annotationConstantRest )
            int alt102=2;
            int LA102_0 = input.LA(1);

            if ( (LA102_0==Identifier) ) {
                int LA102_1 = input.LA(2);

                if ( (LA102_1==65) ) {
                    alt102=1;
                }
                else if ( (LA102_1==25||LA102_1==34||LA102_1==41||LA102_1==44) ) {
                    alt102=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("602:1: annotationMethodOrConstantRest : ( annotationMethodRest | annotationConstantRest );", 102, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("602:1: annotationMethodOrConstantRest : ( annotationMethodRest | annotationConstantRest );", 102, 0, input);

                throw nvae;
            }
            switch (alt102) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:603:4: annotationMethodRest
                    {
                    pushFollow(FOLLOW_annotationMethodRest_in_annotationMethodOrConstantRest2168);
                    annotationMethodRest();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:604:6: annotationConstantRest
                    {
                    pushFollow(FOLLOW_annotationConstantRest_in_annotationMethodOrConstantRest2175);
                    annotationConstantRest();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 76, annotationMethodOrConstantRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end annotationMethodOrConstantRest


    // $ANTLR start annotationMethodRest
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:607:1: annotationMethodRest : Identifier '(' ')' ( defaultValue )? ;
    public final void annotationMethodRest() throws RecognitionException {
        int annotationMethodRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 77) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:608:3: ( Identifier '(' ')' ( defaultValue )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:608:5: Identifier '(' ')' ( defaultValue )?
            {
            match(input,Identifier,FOLLOW_Identifier_in_annotationMethodRest2188); if (failed) return ;
            match(input,65,FOLLOW_65_in_annotationMethodRest2190); if (failed) return ;
            match(input,66,FOLLOW_66_in_annotationMethodRest2192); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:608:24: ( defaultValue )?
            int alt103=2;
            int LA103_0 = input.LA(1);

            if ( (LA103_0==72) ) {
                alt103=1;
            }
            switch (alt103) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:608:25: defaultValue
                    {
                    pushFollow(FOLLOW_defaultValue_in_annotationMethodRest2195);
                    defaultValue();
                    _fsp--;
                    if (failed) return ;

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
            if ( backtracking>0 ) { memoize(input, 77, annotationMethodRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end annotationMethodRest


    // $ANTLR start annotationConstantRest
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:611:1: annotationConstantRest : variableDeclarators ;
    public final void annotationConstantRest() throws RecognitionException {
        int annotationConstantRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 78) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:612:3: ( variableDeclarators )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:612:5: variableDeclarators
            {
            pushFollow(FOLLOW_variableDeclarators_in_annotationConstantRest2212);
            variableDeclarators();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 78, annotationConstantRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end annotationConstantRest


    // $ANTLR start defaultValue
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:615:1: defaultValue : 'default' elementValue ;
    public final void defaultValue() throws RecognitionException {
        int defaultValue_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 79) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:616:3: ( 'default' elementValue )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:616:5: 'default' elementValue
            {
            match(input,72,FOLLOW_72_in_defaultValue2227); if (failed) return ;
            pushFollow(FOLLOW_elementValue_in_defaultValue2229);
            elementValue();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 79, defaultValue_StartIndex); }
        }
        return ;
    }
    // $ANTLR end defaultValue


    // $ANTLR start block
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:621:1: block : '{' ( blockStatement )* '}' ;
    public final void block() throws RecognitionException {
        int block_StartIndex = input.index();

                    this.localVariableLevel++;
                
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 80) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:628:2: ( '{' ( blockStatement )* '}' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:628:4: '{' ( blockStatement )* '}'
            {
            match(input,37,FOLLOW_37_in_block2269); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:628:8: ( blockStatement )*
            loop104:
            do {
                int alt104=2;
                int LA104_0 = input.LA(1);

                if ( ((LA104_0>=Identifier && LA104_0<=DecimalLiteral)||LA104_0==25||LA104_0==27||LA104_0==30||LA104_0==33||LA104_0==37||(LA104_0>=39 && LA104_0<=40)||(LA104_0>=45 && LA104_0<=62)||(LA104_0>=64 && LA104_0<=65)||(LA104_0>=68 && LA104_0<=71)||LA104_0==73||LA104_0==75||(LA104_0>=77 && LA104_0<=80)||(LA104_0>=82 && LA104_0<=87)||(LA104_0>=105 && LA104_0<=106)||(LA104_0>=109 && LA104_0<=114)) ) {
                    alt104=1;
                }


                switch (alt104) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_block2271);
            	    blockStatement();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop104;
                }
            } while (true);

            match(input,38,FOLLOW_38_in_block2274); if (failed) return ;

            }

            if ( backtracking==0 ) {

                          this.localVariableLevel--;
                      
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 80, block_StartIndex); }
        }
        return ;
    }
    // $ANTLR end block


    // $ANTLR start blockStatement
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:631:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );
    public final void blockStatement() throws RecognitionException {
        int blockStatement_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 81) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:632:2: ( localVariableDeclaration | classOrInterfaceDeclaration | statement )
            int alt105=3;
            switch ( input.LA(1) ) {
            case 49:
                {
                switch ( input.LA(2) ) {
                case Identifier:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                    {
                    alt105=1;
                    }
                    break;
                case 49:
                    {
                    int LA105_52 = input.LA(3);

                    if ( (synpred144()) ) {
                        alt105=1;
                    }
                    else if ( (synpred145()) ) {
                        alt105=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("631:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 52, input);

                        throw nvae;
                    }
                    }
                    break;
                case 71:
                    {
                    int LA105_53 = input.LA(3);

                    if ( (synpred144()) ) {
                        alt105=1;
                    }
                    else if ( (synpred145()) ) {
                        alt105=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("631:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 53, input);

                        throw nvae;
                    }
                    }
                    break;
                case ENUM:
                case 27:
                case 30:
                case 39:
                case 45:
                case 46:
                case 47:
                case 48:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                    {
                    alt105=2;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("631:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 1, input);

                    throw nvae;
                }

                }
                break;
            case 71:
                {
                int LA105_2 = input.LA(2);

                if ( (LA105_2==39) ) {
                    alt105=2;
                }
                else if ( (LA105_2==Identifier) ) {
                    int LA105_68 = input.LA(3);

                    if ( (synpred144()) ) {
                        alt105=1;
                    }
                    else if ( (synpred145()) ) {
                        alt105=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("631:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 68, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("631:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 2, input);

                    throw nvae;
                }
                }
                break;
            case Identifier:
                {
                switch ( input.LA(2) ) {
                case 25:
                case 29:
                case 35:
                case 36:
                case 44:
                case 63:
                case 65:
                case 74:
                case 90:
                case 91:
                case 92:
                case 93:
                case 94:
                case 95:
                case 96:
                case 97:
                case 98:
                case 99:
                case 100:
                case 101:
                case 102:
                case 103:
                case 104:
                case 105:
                case 106:
                case 107:
                case 108:
                case 109:
                case 110:
                    {
                    alt105=3;
                    }
                    break;
                case 28:
                    {
                    int LA105_70 = input.LA(3);

                    if ( (synpred144()) ) {
                        alt105=1;
                    }
                    else if ( (true) ) {
                        alt105=3;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("631:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 70, input);

                        throw nvae;
                    }
                    }
                    break;
                case 41:
                    {
                    int LA105_71 = input.LA(3);

                    if ( (synpred144()) ) {
                        alt105=1;
                    }
                    else if ( (true) ) {
                        alt105=3;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("631:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 71, input);

                        throw nvae;
                    }
                    }
                    break;
                case 33:
                    {
                    int LA105_76 = input.LA(3);

                    if ( (synpred144()) ) {
                        alt105=1;
                    }
                    else if ( (true) ) {
                        alt105=3;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("631:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 76, input);

                        throw nvae;
                    }
                    }
                    break;
                case Identifier:
                    {
                    alt105=1;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("631:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 3, input);

                    throw nvae;
                }

                }
                break;
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
                {
                switch ( input.LA(2) ) {
                case 41:
                    {
                    int LA105_97 = input.LA(3);

                    if ( (synpred144()) ) {
                        alt105=1;
                    }
                    else if ( (true) ) {
                        alt105=3;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("631:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 97, input);

                        throw nvae;
                    }
                    }
                    break;
                case Identifier:
                    {
                    alt105=1;
                    }
                    break;
                case 28:
                    {
                    alt105=3;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("631:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 4, input);

                    throw nvae;
                }

                }
                break;
            case ENUM:
            case 27:
            case 30:
            case 39:
            case 45:
            case 46:
            case 47:
            case 48:
            case 50:
            case 52:
            case 53:
            case 54:
                {
                alt105=2;
                }
                break;
            case 51:
                {
                int LA105_11 = input.LA(2);

                if ( (LA105_11==ENUM||LA105_11==27||LA105_11==30||LA105_11==39||(LA105_11>=45 && LA105_11<=54)||LA105_11==71) ) {
                    alt105=2;
                }
                else if ( (LA105_11==65) ) {
                    alt105=3;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("631:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 11, input);

                    throw nvae;
                }
                }
                break;
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
            case 25:
            case 33:
            case 37:
            case 40:
            case 64:
            case 65:
            case 68:
            case 69:
            case 70:
            case 73:
            case 75:
            case 77:
            case 78:
            case 79:
            case 80:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 105:
            case 106:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
                {
                alt105=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("631:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 0, input);

                throw nvae;
            }

            switch (alt105) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:632:4: localVariableDeclaration
                    {
                    pushFollow(FOLLOW_localVariableDeclaration_in_blockStatement2286);
                    localVariableDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:633:4: classOrInterfaceDeclaration
                    {
                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_blockStatement2291);
                    classOrInterfaceDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:634:8: statement
                    {
                    pushFollow(FOLLOW_statement_in_blockStatement2300);
                    statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 81, blockStatement_StartIndex); }
        }
        return ;
    }
    // $ANTLR end blockStatement

    protected static class localVariableDeclaration_scope {
        JavaLocalDeclarationDescr descr;
    }
    protected Stack localVariableDeclaration_stack = new Stack();


    // $ANTLR start localVariableDeclaration
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:637:1: localVariableDeclaration : ( variableModifier )* type variableDeclarators ';' ;
    public final void localVariableDeclaration() throws RecognitionException {
        localVariableDeclaration_stack.push(new localVariableDeclaration_scope());
        int localVariableDeclaration_StartIndex = input.index();
        variableModifier_return variableModifier1 = null;

        type_return type2 = null;



                    ((localVariableDeclaration_scope)localVariableDeclaration_stack.peek()).descr = new JavaLocalDeclarationDescr();
                
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 82) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:647:2: ( ( variableModifier )* type variableDeclarators ';' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:648:2: ( variableModifier )* type variableDeclarators ';'
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:648:2: ( variableModifier )*
            loop106:
            do {
                int alt106=2;
                int LA106_0 = input.LA(1);

                if ( (LA106_0==49||LA106_0==71) ) {
                    alt106=1;
                }


                switch (alt106) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:648:4: variableModifier
            	    {
            	    pushFollow(FOLLOW_variableModifier_in_localVariableDeclaration2348);
            	    variableModifier1=variableModifier();
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	       
            	      	        ((localVariableDeclaration_scope)localVariableDeclaration_stack.peek()).descr.updateStart( ((CommonToken)((Token)variableModifier1.start)).getStartIndex() - 1 ); 
            	      	        ((localVariableDeclaration_scope)localVariableDeclaration_stack.peek()).descr.addModifier( input.toString(variableModifier1.start,variableModifier1.stop) ); 
            	      	    
            	    }

            	    }
            	    break;

            	default :
            	    break loop106;
                }
            } while (true);

            pushFollow(FOLLOW_type_in_localVariableDeclaration2365);
            type2=type();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               
              	        ((localVariableDeclaration_scope)localVariableDeclaration_stack.peek()).descr.updateStart( ((CommonToken)((Token)type2.start)).getStartIndex() - 1 ); 
              	        ((localVariableDeclaration_scope)localVariableDeclaration_stack.peek()).descr.setType( input.toString(type2.start,type2.stop) ); 
              	        ((localVariableDeclaration_scope)localVariableDeclaration_stack.peek()).descr.setEnd( ((CommonToken)((Token)type2.stop)).getStopIndex() ); 
              	    
            }
            pushFollow(FOLLOW_variableDeclarators_in_localVariableDeclaration2376);
            variableDeclarators();
            _fsp--;
            if (failed) return ;
            match(input,25,FOLLOW_25_in_localVariableDeclaration2378); if (failed) return ;

            }

            if ( backtracking==0 ) {

                          localDeclarations.add( ((localVariableDeclaration_scope)localVariableDeclaration_stack.peek()).descr );
                      
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 82, localVariableDeclaration_StartIndex); }
            localVariableDeclaration_stack.pop();
        }
        return ;
    }
    // $ANTLR end localVariableDeclaration


    // $ANTLR start statement
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:663:1: statement : ( block | 'assert' expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | modifyStatement | ';' | statementExpression ';' | Identifier ':' statement );
    public final void statement() throws RecognitionException {
        int statement_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 83) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:664:2: ( block | 'assert' expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | modifyStatement | ';' | statementExpression ';' | Identifier ':' statement )
            int alt113=17;
            switch ( input.LA(1) ) {
            case 37:
                {
                alt113=1;
                }
                break;
            case 73:
                {
                alt113=2;
                }
                break;
            case 75:
                {
                alt113=3;
                }
                break;
            case 77:
                {
                alt113=4;
                }
                break;
            case 78:
                {
                alt113=5;
                }
                break;
            case 79:
                {
                alt113=6;
                }
                break;
            case 80:
                {
                alt113=7;
                }
                break;
            case 82:
                {
                alt113=8;
                }
                break;
            case 51:
                {
                alt113=9;
                }
                break;
            case 83:
                {
                alt113=10;
                }
                break;
            case 84:
                {
                alt113=11;
                }
                break;
            case 85:
                {
                alt113=12;
                }
                break;
            case 86:
                {
                alt113=13;
                }
                break;
            case 87:
                {
                alt113=14;
                }
                break;
            case 25:
                {
                alt113=15;
                }
                break;
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
            case 33:
            case 40:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 64:
            case 65:
            case 68:
            case 69:
            case 70:
            case 105:
            case 106:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
                {
                alt113=16;
                }
                break;
            case Identifier:
                {
                int LA113_33 = input.LA(2);

                if ( (LA113_33==74) ) {
                    alt113=17;
                }
                else if ( (LA113_33==25||(LA113_33>=28 && LA113_33<=29)||LA113_33==33||(LA113_33>=35 && LA113_33<=36)||LA113_33==41||LA113_33==44||LA113_33==63||LA113_33==65||(LA113_33>=90 && LA113_33<=110)) ) {
                    alt113=16;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("663:1: statement : ( block | 'assert' expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | modifyStatement | ';' | statementExpression ';' | Identifier ':' statement );", 113, 33, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("663:1: statement : ( block | 'assert' expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | modifyStatement | ';' | statementExpression ';' | Identifier ':' statement );", 113, 0, input);

                throw nvae;
            }

            switch (alt113) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:664:4: block
                    {
                    pushFollow(FOLLOW_block_in_statement2390);
                    block();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:665:7: 'assert' expression ( ':' expression )? ';'
                    {
                    match(input,73,FOLLOW_73_in_statement2398); if (failed) return ;
                    pushFollow(FOLLOW_expression_in_statement2400);
                    expression();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:665:27: ( ':' expression )?
                    int alt107=2;
                    int LA107_0 = input.LA(1);

                    if ( (LA107_0==74) ) {
                        alt107=1;
                    }
                    switch (alt107) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:665:28: ':' expression
                            {
                            match(input,74,FOLLOW_74_in_statement2403); if (failed) return ;
                            pushFollow(FOLLOW_expression_in_statement2405);
                            expression();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    match(input,25,FOLLOW_25_in_statement2409); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:666:7: 'if' parExpression statement ( options {k=1; } : 'else' statement )?
                    {
                    match(input,75,FOLLOW_75_in_statement2417); if (failed) return ;
                    pushFollow(FOLLOW_parExpression_in_statement2419);
                    parExpression();
                    _fsp--;
                    if (failed) return ;
                    pushFollow(FOLLOW_statement_in_statement2421);
                    statement();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:666:36: ( options {k=1; } : 'else' statement )?
                    int alt108=2;
                    int LA108_0 = input.LA(1);

                    if ( (LA108_0==76) ) {
                        int LA108_1 = input.LA(2);

                        if ( (synpred150()) ) {
                            alt108=1;
                        }
                    }
                    switch (alt108) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:666:52: 'else' statement
                            {
                            match(input,76,FOLLOW_76_in_statement2431); if (failed) return ;
                            pushFollow(FOLLOW_statement_in_statement2433);
                            statement();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:667:7: 'for' '(' forControl ')' statement
                    {
                    match(input,77,FOLLOW_77_in_statement2443); if (failed) return ;
                    match(input,65,FOLLOW_65_in_statement2445); if (failed) return ;
                    pushFollow(FOLLOW_forControl_in_statement2447);
                    forControl();
                    _fsp--;
                    if (failed) return ;
                    match(input,66,FOLLOW_66_in_statement2449); if (failed) return ;
                    pushFollow(FOLLOW_statement_in_statement2451);
                    statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:668:7: 'while' parExpression statement
                    {
                    match(input,78,FOLLOW_78_in_statement2459); if (failed) return ;
                    pushFollow(FOLLOW_parExpression_in_statement2461);
                    parExpression();
                    _fsp--;
                    if (failed) return ;
                    pushFollow(FOLLOW_statement_in_statement2463);
                    statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:669:7: 'do' statement 'while' parExpression ';'
                    {
                    match(input,79,FOLLOW_79_in_statement2471); if (failed) return ;
                    pushFollow(FOLLOW_statement_in_statement2473);
                    statement();
                    _fsp--;
                    if (failed) return ;
                    match(input,78,FOLLOW_78_in_statement2475); if (failed) return ;
                    pushFollow(FOLLOW_parExpression_in_statement2477);
                    parExpression();
                    _fsp--;
                    if (failed) return ;
                    match(input,25,FOLLOW_25_in_statement2479); if (failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:670:7: 'try' block ( catches 'finally' block | catches | 'finally' block )
                    {
                    match(input,80,FOLLOW_80_in_statement2487); if (failed) return ;
                    pushFollow(FOLLOW_block_in_statement2489);
                    block();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:671:7: ( catches 'finally' block | catches | 'finally' block )
                    int alt109=3;
                    int LA109_0 = input.LA(1);

                    if ( (LA109_0==88) ) {
                        int LA109_1 = input.LA(2);

                        if ( (LA109_1==65) ) {
                            int LA109_3 = input.LA(3);

                            if ( (synpred155()) ) {
                                alt109=1;
                            }
                            else if ( (synpred156()) ) {
                                alt109=2;
                            }
                            else {
                                if (backtracking>0) {failed=true; return ;}
                                NoViableAltException nvae =
                                    new NoViableAltException("671:7: ( catches 'finally' block | catches | 'finally' block )", 109, 3, input);

                                throw nvae;
                            }
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("671:7: ( catches 'finally' block | catches | 'finally' block )", 109, 1, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA109_0==81) ) {
                        alt109=3;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("671:7: ( catches 'finally' block | catches | 'finally' block )", 109, 0, input);

                        throw nvae;
                    }
                    switch (alt109) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:671:9: catches 'finally' block
                            {
                            pushFollow(FOLLOW_catches_in_statement2499);
                            catches();
                            _fsp--;
                            if (failed) return ;
                            match(input,81,FOLLOW_81_in_statement2501); if (failed) return ;
                            pushFollow(FOLLOW_block_in_statement2503);
                            block();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;
                        case 2 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:672:9: catches
                            {
                            pushFollow(FOLLOW_catches_in_statement2513);
                            catches();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;
                        case 3 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:673:9: 'finally' block
                            {
                            match(input,81,FOLLOW_81_in_statement2523); if (failed) return ;
                            pushFollow(FOLLOW_block_in_statement2525);
                            block();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:675:7: 'switch' parExpression '{' switchBlockStatementGroups '}'
                    {
                    match(input,82,FOLLOW_82_in_statement2541); if (failed) return ;
                    pushFollow(FOLLOW_parExpression_in_statement2543);
                    parExpression();
                    _fsp--;
                    if (failed) return ;
                    match(input,37,FOLLOW_37_in_statement2545); if (failed) return ;
                    pushFollow(FOLLOW_switchBlockStatementGroups_in_statement2547);
                    switchBlockStatementGroups();
                    _fsp--;
                    if (failed) return ;
                    match(input,38,FOLLOW_38_in_statement2549); if (failed) return ;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:676:7: 'synchronized' parExpression block
                    {
                    match(input,51,FOLLOW_51_in_statement2557); if (failed) return ;
                    pushFollow(FOLLOW_parExpression_in_statement2559);
                    parExpression();
                    _fsp--;
                    if (failed) return ;
                    pushFollow(FOLLOW_block_in_statement2561);
                    block();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:677:7: 'return' ( expression )? ';'
                    {
                    match(input,83,FOLLOW_83_in_statement2569); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:677:16: ( expression )?
                    int alt110=2;
                    int LA110_0 = input.LA(1);

                    if ( (LA110_0==Identifier||(LA110_0>=FloatingPointLiteral && LA110_0<=DecimalLiteral)||LA110_0==33||LA110_0==40||(LA110_0>=55 && LA110_0<=62)||(LA110_0>=64 && LA110_0<=65)||(LA110_0>=68 && LA110_0<=70)||(LA110_0>=105 && LA110_0<=106)||(LA110_0>=109 && LA110_0<=114)) ) {
                        alt110=1;
                    }
                    switch (alt110) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: expression
                            {
                            pushFollow(FOLLOW_expression_in_statement2571);
                            expression();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    match(input,25,FOLLOW_25_in_statement2574); if (failed) return ;

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:678:7: 'throw' expression ';'
                    {
                    match(input,84,FOLLOW_84_in_statement2582); if (failed) return ;
                    pushFollow(FOLLOW_expression_in_statement2584);
                    expression();
                    _fsp--;
                    if (failed) return ;
                    match(input,25,FOLLOW_25_in_statement2586); if (failed) return ;

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:679:7: 'break' ( Identifier )? ';'
                    {
                    match(input,85,FOLLOW_85_in_statement2594); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:679:15: ( Identifier )?
                    int alt111=2;
                    int LA111_0 = input.LA(1);

                    if ( (LA111_0==Identifier) ) {
                        alt111=1;
                    }
                    switch (alt111) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: Identifier
                            {
                            match(input,Identifier,FOLLOW_Identifier_in_statement2596); if (failed) return ;

                            }
                            break;

                    }

                    match(input,25,FOLLOW_25_in_statement2599); if (failed) return ;

                    }
                    break;
                case 13 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:680:7: 'continue' ( Identifier )? ';'
                    {
                    match(input,86,FOLLOW_86_in_statement2607); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:680:18: ( Identifier )?
                    int alt112=2;
                    int LA112_0 = input.LA(1);

                    if ( (LA112_0==Identifier) ) {
                        alt112=1;
                    }
                    switch (alt112) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: Identifier
                            {
                            match(input,Identifier,FOLLOW_Identifier_in_statement2609); if (failed) return ;

                            }
                            break;

                    }

                    match(input,25,FOLLOW_25_in_statement2612); if (failed) return ;

                    }
                    break;
                case 14 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:682:7: modifyStatement
                    {
                    pushFollow(FOLLOW_modifyStatement_in_statement2625);
                    modifyStatement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 15 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:683:7: ';'
                    {
                    match(input,25,FOLLOW_25_in_statement2633); if (failed) return ;

                    }
                    break;
                case 16 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:684:7: statementExpression ';'
                    {
                    pushFollow(FOLLOW_statementExpression_in_statement2641);
                    statementExpression();
                    _fsp--;
                    if (failed) return ;
                    match(input,25,FOLLOW_25_in_statement2643); if (failed) return ;

                    }
                    break;
                case 17 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:685:7: Identifier ':' statement
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_statement2651); if (failed) return ;
                    match(input,74,FOLLOW_74_in_statement2653); if (failed) return ;
                    pushFollow(FOLLOW_statement_in_statement2655);
                    statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 83, statement_StartIndex); }
        }
        return ;
    }
    // $ANTLR end statement


    // $ANTLR start modifyStatement
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:688:1: modifyStatement : s= 'modify' parExpression '{' (e= expression ( ',' e= expression )* )? c= '}' ;
    public final void modifyStatement() throws RecognitionException {
        int modifyStatement_StartIndex = input.index();
        Token s=null;
        Token c=null;
        expression_return e = null;

        parExpression_return parExpression3 = null;



        	    JavaModifyBlockDescr d = null;
        	
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 84) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:692:2: (s= 'modify' parExpression '{' (e= expression ( ',' e= expression )* )? c= '}' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:692:4: s= 'modify' parExpression '{' (e= expression ( ',' e= expression )* )? c= '}'
            {
            s=(Token)input.LT(1);
            match(input,87,FOLLOW_87_in_modifyStatement2675); if (failed) return ;
            pushFollow(FOLLOW_parExpression_in_modifyStatement2677);
            parExpression3=parExpression();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              	    d = new JavaModifyBlockDescr( input.toString(parExpression3.start,parExpression3.stop) );
              	    d.setStart( ((CommonToken)s).getStartIndex() );
              	    this.modifyBlocks.add( d );
              	    
              	
            }
            match(input,37,FOLLOW_37_in_modifyStatement2684); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:699:6: (e= expression ( ',' e= expression )* )?
            int alt115=2;
            int LA115_0 = input.LA(1);

            if ( (LA115_0==Identifier||(LA115_0>=FloatingPointLiteral && LA115_0<=DecimalLiteral)||LA115_0==33||LA115_0==40||(LA115_0>=55 && LA115_0<=62)||(LA115_0>=64 && LA115_0<=65)||(LA115_0>=68 && LA115_0<=70)||(LA115_0>=105 && LA115_0<=106)||(LA115_0>=109 && LA115_0<=114)) ) {
                alt115=1;
            }
            switch (alt115) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:699:8: e= expression ( ',' e= expression )*
                    {
                    pushFollow(FOLLOW_expression_in_modifyStatement2692);
                    e=expression();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       d.getExpressions().add( input.toString(e.start,e.stop) ); 
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:700:9: ( ',' e= expression )*
                    loop114:
                    do {
                        int alt114=2;
                        int LA114_0 = input.LA(1);

                        if ( (LA114_0==34) ) {
                            alt114=1;
                        }


                        switch (alt114) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:700:10: ',' e= expression
                    	    {
                    	    match(input,34,FOLLOW_34_in_modifyStatement2705); if (failed) return ;
                    	    pushFollow(FOLLOW_expression_in_modifyStatement2709);
                    	    e=expression();
                    	    _fsp--;
                    	    if (failed) return ;
                    	    if ( backtracking==0 ) {
                    	       d.getExpressions().add( input.toString(e.start,e.stop) ); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop114;
                        }
                    } while (true);


                    }
                    break;

            }

            c=(Token)input.LT(1);
            match(input,38,FOLLOW_38_in_modifyStatement2728); if (failed) return ;
            if ( backtracking==0 ) {

                          d.setEnd( ((CommonToken)c).getStopIndex() ); 
                      
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 84, modifyStatement_StartIndex); }
        }
        return ;
    }
    // $ANTLR end modifyStatement


    // $ANTLR start catches
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:708:1: catches : catchClause ( catchClause )* ;
    public final void catches() throws RecognitionException {
        int catches_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 85) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:709:2: ( catchClause ( catchClause )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:709:4: catchClause ( catchClause )*
            {
            pushFollow(FOLLOW_catchClause_in_catches2752);
            catchClause();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:709:16: ( catchClause )*
            loop116:
            do {
                int alt116=2;
                int LA116_0 = input.LA(1);

                if ( (LA116_0==88) ) {
                    alt116=1;
                }


                switch (alt116) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:709:17: catchClause
            	    {
            	    pushFollow(FOLLOW_catchClause_in_catches2755);
            	    catchClause();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop116;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 85, catches_StartIndex); }
        }
        return ;
    }
    // $ANTLR end catches


    // $ANTLR start catchClause
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:712:1: catchClause : 'catch' '(' formalParameter ')' block ;
    public final void catchClause() throws RecognitionException {
        int catchClause_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 86) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:713:2: ( 'catch' '(' formalParameter ')' block )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:713:4: 'catch' '(' formalParameter ')' block
            {
            match(input,88,FOLLOW_88_in_catchClause2769); if (failed) return ;
            match(input,65,FOLLOW_65_in_catchClause2771); if (failed) return ;
            pushFollow(FOLLOW_formalParameter_in_catchClause2773);
            formalParameter();
            _fsp--;
            if (failed) return ;
            match(input,66,FOLLOW_66_in_catchClause2775); if (failed) return ;
            pushFollow(FOLLOW_block_in_catchClause2777);
            block();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 86, catchClause_StartIndex); }
        }
        return ;
    }
    // $ANTLR end catchClause


    // $ANTLR start formalParameter
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:716:1: formalParameter : ( variableModifier )* type variableDeclaratorId ;
    public final void formalParameter() throws RecognitionException {
        int formalParameter_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 87) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:717:2: ( ( variableModifier )* type variableDeclaratorId )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:717:4: ( variableModifier )* type variableDeclaratorId
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:717:4: ( variableModifier )*
            loop117:
            do {
                int alt117=2;
                int LA117_0 = input.LA(1);

                if ( (LA117_0==49||LA117_0==71) ) {
                    alt117=1;
                }


                switch (alt117) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: variableModifier
            	    {
            	    pushFollow(FOLLOW_variableModifier_in_formalParameter2788);
            	    variableModifier();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop117;
                }
            } while (true);

            pushFollow(FOLLOW_type_in_formalParameter2791);
            type();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_variableDeclaratorId_in_formalParameter2793);
            variableDeclaratorId();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 87, formalParameter_StartIndex); }
        }
        return ;
    }
    // $ANTLR end formalParameter


    // $ANTLR start switchBlockStatementGroups
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:720:1: switchBlockStatementGroups : ( switchBlockStatementGroup )* ;
    public final void switchBlockStatementGroups() throws RecognitionException {
        int switchBlockStatementGroups_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 88) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:721:2: ( ( switchBlockStatementGroup )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:721:4: ( switchBlockStatementGroup )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:721:4: ( switchBlockStatementGroup )*
            loop118:
            do {
                int alt118=2;
                int LA118_0 = input.LA(1);

                if ( (LA118_0==72||LA118_0==89) ) {
                    alt118=1;
                }


                switch (alt118) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:721:5: switchBlockStatementGroup
            	    {
            	    pushFollow(FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups2807);
            	    switchBlockStatementGroup();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop118;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 88, switchBlockStatementGroups_StartIndex); }
        }
        return ;
    }
    // $ANTLR end switchBlockStatementGroups


    // $ANTLR start switchBlockStatementGroup
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:724:1: switchBlockStatementGroup : switchLabel ( blockStatement )* ;
    public final void switchBlockStatementGroup() throws RecognitionException {
        int switchBlockStatementGroup_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 89) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:725:2: ( switchLabel ( blockStatement )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:725:4: switchLabel ( blockStatement )*
            {
            pushFollow(FOLLOW_switchLabel_in_switchBlockStatementGroup2821);
            switchLabel();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:725:16: ( blockStatement )*
            loop119:
            do {
                int alt119=2;
                int LA119_0 = input.LA(1);

                if ( ((LA119_0>=Identifier && LA119_0<=DecimalLiteral)||LA119_0==25||LA119_0==27||LA119_0==30||LA119_0==33||LA119_0==37||(LA119_0>=39 && LA119_0<=40)||(LA119_0>=45 && LA119_0<=62)||(LA119_0>=64 && LA119_0<=65)||(LA119_0>=68 && LA119_0<=71)||LA119_0==73||LA119_0==75||(LA119_0>=77 && LA119_0<=80)||(LA119_0>=82 && LA119_0<=87)||(LA119_0>=105 && LA119_0<=106)||(LA119_0>=109 && LA119_0<=114)) ) {
                    alt119=1;
                }


                switch (alt119) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_switchBlockStatementGroup2823);
            	    blockStatement();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop119;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 89, switchBlockStatementGroup_StartIndex); }
        }
        return ;
    }
    // $ANTLR end switchBlockStatementGroup


    // $ANTLR start switchLabel
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:728:1: switchLabel : ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' );
    public final void switchLabel() throws RecognitionException {
        int switchLabel_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 90) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:729:2: ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' )
            int alt120=3;
            int LA120_0 = input.LA(1);

            if ( (LA120_0==89) ) {
                int LA120_1 = input.LA(2);

                if ( ((LA120_1>=FloatingPointLiteral && LA120_1<=DecimalLiteral)||LA120_1==33||LA120_1==40||(LA120_1>=55 && LA120_1<=62)||(LA120_1>=64 && LA120_1<=65)||(LA120_1>=68 && LA120_1<=70)||(LA120_1>=105 && LA120_1<=106)||(LA120_1>=109 && LA120_1<=114)) ) {
                    alt120=1;
                }
                else if ( (LA120_1==Identifier) ) {
                    int LA120_20 = input.LA(3);

                    if ( (synpred176()) ) {
                        alt120=1;
                    }
                    else if ( (synpred177()) ) {
                        alt120=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("728:1: switchLabel : ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' );", 120, 20, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("728:1: switchLabel : ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' );", 120, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA120_0==72) ) {
                alt120=3;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("728:1: switchLabel : ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' );", 120, 0, input);

                throw nvae;
            }
            switch (alt120) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:729:4: 'case' constantExpression ':'
                    {
                    match(input,89,FOLLOW_89_in_switchLabel2836); if (failed) return ;
                    pushFollow(FOLLOW_constantExpression_in_switchLabel2838);
                    constantExpression();
                    _fsp--;
                    if (failed) return ;
                    match(input,74,FOLLOW_74_in_switchLabel2840); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:730:6: 'case' enumConstantName ':'
                    {
                    match(input,89,FOLLOW_89_in_switchLabel2847); if (failed) return ;
                    pushFollow(FOLLOW_enumConstantName_in_switchLabel2849);
                    enumConstantName();
                    _fsp--;
                    if (failed) return ;
                    match(input,74,FOLLOW_74_in_switchLabel2851); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:731:6: 'default' ':'
                    {
                    match(input,72,FOLLOW_72_in_switchLabel2858); if (failed) return ;
                    match(input,74,FOLLOW_74_in_switchLabel2860); if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 90, switchLabel_StartIndex); }
        }
        return ;
    }
    // $ANTLR end switchLabel


    // $ANTLR start moreStatementExpressions
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:734:1: moreStatementExpressions : ( ',' statementExpression )* ;
    public final void moreStatementExpressions() throws RecognitionException {
        int moreStatementExpressions_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 91) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:735:2: ( ( ',' statementExpression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:735:4: ( ',' statementExpression )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:735:4: ( ',' statementExpression )*
            loop121:
            do {
                int alt121=2;
                int LA121_0 = input.LA(1);

                if ( (LA121_0==34) ) {
                    alt121=1;
                }


                switch (alt121) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:735:5: ',' statementExpression
            	    {
            	    match(input,34,FOLLOW_34_in_moreStatementExpressions2873); if (failed) return ;
            	    pushFollow(FOLLOW_statementExpression_in_moreStatementExpressions2875);
            	    statementExpression();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop121;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 91, moreStatementExpressions_StartIndex); }
        }
        return ;
    }
    // $ANTLR end moreStatementExpressions


    // $ANTLR start forControl
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );
    public final void forControl() throws RecognitionException {
        int forControl_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 92) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:740:2: ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? )
            int alt125=2;
            switch ( input.LA(1) ) {
            case 49:
                {
                switch ( input.LA(2) ) {
                case Identifier:
                    {
                    switch ( input.LA(3) ) {
                    case 33:
                        {
                        int LA125_60 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 60, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA125_61 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 61, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 41:
                        {
                        int LA125_62 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 62, input);

                            throw nvae;
                        }
                        }
                        break;
                    case Identifier:
                        {
                        int LA125_63 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 63, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 24, input);

                        throw nvae;
                    }

                    }
                    break;
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                    {
                    int LA125_25 = input.LA(3);

                    if ( (LA125_25==41) ) {
                        int LA125_64 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 64, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA125_25==Identifier) ) {
                        int LA125_65 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 65, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 25, input);

                        throw nvae;
                    }
                    }
                    break;
                case 49:
                    {
                    switch ( input.LA(3) ) {
                    case Identifier:
                        {
                        int LA125_66 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 66, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 55:
                    case 56:
                    case 57:
                    case 58:
                    case 59:
                    case 60:
                    case 61:
                    case 62:
                        {
                        int LA125_67 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 67, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA125_68 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 68, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 71:
                        {
                        int LA125_69 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 69, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 26, input);

                        throw nvae;
                    }

                    }
                    break;
                case 71:
                    {
                    int LA125_27 = input.LA(3);

                    if ( (LA125_27==Identifier) ) {
                        int LA125_70 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 70, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 27, input);

                        throw nvae;
                    }
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 1, input);

                    throw nvae;
                }

                }
                break;
            case 71:
                {
                int LA125_2 = input.LA(2);

                if ( (LA125_2==Identifier) ) {
                    switch ( input.LA(3) ) {
                    case 28:
                        {
                        int LA125_71 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 71, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 65:
                        {
                        int LA125_72 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 72, input);

                            throw nvae;
                        }
                        }
                        break;
                    case Identifier:
                        {
                        int LA125_73 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 73, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 55:
                    case 56:
                    case 57:
                    case 58:
                    case 59:
                    case 60:
                    case 61:
                    case 62:
                        {
                        int LA125_74 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 74, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA125_75 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 75, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 71:
                        {
                        int LA125_76 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 76, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 28, input);

                        throw nvae;
                    }

                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 2, input);

                    throw nvae;
                }
                }
                break;
            case Identifier:
                {
                switch ( input.LA(2) ) {
                case 28:
                    {
                    int LA125_29 = input.LA(3);

                    if ( (LA125_29==30||LA125_29==33||LA125_29==64||(LA125_29>=113 && LA125_29<=114)) ) {
                        alt125=2;
                    }
                    else if ( (LA125_29==Identifier) ) {
                        int LA125_79 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 79, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 29, input);

                        throw nvae;
                    }
                    }
                    break;
                case 41:
                    {
                    int LA125_30 = input.LA(3);

                    if ( (LA125_30==42) ) {
                        int LA125_83 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 83, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA125_30==Identifier||(LA125_30>=FloatingPointLiteral && LA125_30<=DecimalLiteral)||LA125_30==33||LA125_30==40||(LA125_30>=55 && LA125_30<=62)||(LA125_30>=64 && LA125_30<=65)||(LA125_30>=68 && LA125_30<=70)||(LA125_30>=105 && LA125_30<=106)||(LA125_30>=109 && LA125_30<=114)) ) {
                        alt125=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 30, input);

                        throw nvae;
                    }
                    }
                    break;
                case 25:
                case 29:
                case 34:
                case 35:
                case 36:
                case 44:
                case 63:
                case 65:
                case 90:
                case 91:
                case 92:
                case 93:
                case 94:
                case 95:
                case 96:
                case 97:
                case 98:
                case 99:
                case 100:
                case 101:
                case 102:
                case 103:
                case 104:
                case 105:
                case 106:
                case 107:
                case 108:
                case 109:
                case 110:
                    {
                    alt125=2;
                    }
                    break;
                case 33:
                    {
                    switch ( input.LA(3) ) {
                    case FloatingPointLiteral:
                    case CharacterLiteral:
                    case StringLiteral:
                    case HexLiteral:
                    case OctalLiteral:
                    case DecimalLiteral:
                    case 33:
                    case 40:
                    case 44:
                    case 64:
                    case 65:
                    case 68:
                    case 69:
                    case 70:
                    case 105:
                    case 106:
                    case 109:
                    case 110:
                    case 111:
                    case 112:
                    case 113:
                    case 114:
                        {
                        alt125=2;
                        }
                        break;
                    case Identifier:
                        {
                        int LA125_106 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 106, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 55:
                    case 56:
                    case 57:
                    case 58:
                    case 59:
                    case 60:
                    case 61:
                    case 62:
                        {
                        int LA125_107 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 107, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 63:
                        {
                        int LA125_108 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 108, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 35, input);

                        throw nvae;
                    }

                    }
                    break;
                case Identifier:
                    {
                    int LA125_56 = input.LA(3);

                    if ( (LA125_56==74) ) {
                        alt125=1;
                    }
                    else if ( (LA125_56==25||LA125_56==34||LA125_56==41||LA125_56==44) ) {
                        alt125=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 56, input);

                        throw nvae;
                    }
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 3, input);

                    throw nvae;
                }

                }
                break;
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
                {
                switch ( input.LA(2) ) {
                case 41:
                    {
                    int LA125_57 = input.LA(3);

                    if ( (LA125_57==42) ) {
                        int LA125_131 = input.LA(4);

                        if ( (synpred179()) ) {
                            alt125=1;
                        }
                        else if ( (true) ) {
                            alt125=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 131, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 57, input);

                        throw nvae;
                    }
                    }
                    break;
                case 28:
                    {
                    alt125=2;
                    }
                    break;
                case Identifier:
                    {
                    int LA125_59 = input.LA(3);

                    if ( (LA125_59==74) ) {
                        alt125=1;
                    }
                    else if ( (LA125_59==25||LA125_59==34||LA125_59==41||LA125_59==44) ) {
                        alt125=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 59, input);

                        throw nvae;
                    }
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 4, input);

                    throw nvae;
                }

                }
                break;
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
            case 25:
            case 33:
            case 40:
            case 64:
            case 65:
            case 68:
            case 69:
            case 70:
            case 105:
            case 106:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
                {
                alt125=2;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("738:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 125, 0, input);

                throw nvae;
            }

            switch (alt125) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:740:4: forVarControl
                    {
                    pushFollow(FOLLOW_forVarControl_in_forControl2896);
                    forVarControl();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:741:4: ( forInit )? ';' ( expression )? ';' ( forUpdate )?
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:741:4: ( forInit )?
                    int alt122=2;
                    int LA122_0 = input.LA(1);

                    if ( (LA122_0==Identifier||(LA122_0>=FloatingPointLiteral && LA122_0<=DecimalLiteral)||LA122_0==33||LA122_0==40||LA122_0==49||(LA122_0>=55 && LA122_0<=62)||(LA122_0>=64 && LA122_0<=65)||(LA122_0>=68 && LA122_0<=71)||(LA122_0>=105 && LA122_0<=106)||(LA122_0>=109 && LA122_0<=114)) ) {
                        alt122=1;
                    }
                    switch (alt122) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: forInit
                            {
                            pushFollow(FOLLOW_forInit_in_forControl2901);
                            forInit();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    match(input,25,FOLLOW_25_in_forControl2904); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:741:17: ( expression )?
                    int alt123=2;
                    int LA123_0 = input.LA(1);

                    if ( (LA123_0==Identifier||(LA123_0>=FloatingPointLiteral && LA123_0<=DecimalLiteral)||LA123_0==33||LA123_0==40||(LA123_0>=55 && LA123_0<=62)||(LA123_0>=64 && LA123_0<=65)||(LA123_0>=68 && LA123_0<=70)||(LA123_0>=105 && LA123_0<=106)||(LA123_0>=109 && LA123_0<=114)) ) {
                        alt123=1;
                    }
                    switch (alt123) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: expression
                            {
                            pushFollow(FOLLOW_expression_in_forControl2906);
                            expression();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    match(input,25,FOLLOW_25_in_forControl2909); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:741:33: ( forUpdate )?
                    int alt124=2;
                    int LA124_0 = input.LA(1);

                    if ( (LA124_0==Identifier||(LA124_0>=FloatingPointLiteral && LA124_0<=DecimalLiteral)||LA124_0==33||LA124_0==40||(LA124_0>=55 && LA124_0<=62)||(LA124_0>=64 && LA124_0<=65)||(LA124_0>=68 && LA124_0<=70)||(LA124_0>=105 && LA124_0<=106)||(LA124_0>=109 && LA124_0<=114)) ) {
                        alt124=1;
                    }
                    switch (alt124) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: forUpdate
                            {
                            pushFollow(FOLLOW_forUpdate_in_forControl2911);
                            forUpdate();
                            _fsp--;
                            if (failed) return ;

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
            if ( backtracking>0 ) { memoize(input, 92, forControl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end forControl


    // $ANTLR start forInit
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:744:1: forInit : ( ( variableModifier )* type variableDeclarators | expressionList );
    public final void forInit() throws RecognitionException {
        int forInit_StartIndex = input.index();

                    this.localVariableLevel++;
                
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 93) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:751:2: ( ( variableModifier )* type variableDeclarators | expressionList )
            int alt127=2;
            switch ( input.LA(1) ) {
            case 49:
            case 71:
                {
                alt127=1;
                }
                break;
            case Identifier:
                {
                switch ( input.LA(2) ) {
                case 28:
                    {
                    int LA127_23 = input.LA(3);

                    if ( (synpred184()) ) {
                        alt127=1;
                    }
                    else if ( (true) ) {
                        alt127=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("744:1: forInit : ( ( variableModifier )* type variableDeclarators | expressionList );", 127, 23, input);

                        throw nvae;
                    }
                    }
                    break;
                case 41:
                    {
                    int LA127_24 = input.LA(3);

                    if ( (synpred184()) ) {
                        alt127=1;
                    }
                    else if ( (true) ) {
                        alt127=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("744:1: forInit : ( ( variableModifier )* type variableDeclarators | expressionList );", 127, 24, input);

                        throw nvae;
                    }
                    }
                    break;
                case EOF:
                case 25:
                case 29:
                case 34:
                case 35:
                case 36:
                case 44:
                case 63:
                case 65:
                case 90:
                case 91:
                case 92:
                case 93:
                case 94:
                case 95:
                case 96:
                case 97:
                case 98:
                case 99:
                case 100:
                case 101:
                case 102:
                case 103:
                case 104:
                case 105:
                case 106:
                case 107:
                case 108:
                case 109:
                case 110:
                    {
                    alt127=2;
                    }
                    break;
                case 33:
                    {
                    int LA127_29 = input.LA(3);

                    if ( (synpred184()) ) {
                        alt127=1;
                    }
                    else if ( (true) ) {
                        alt127=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("744:1: forInit : ( ( variableModifier )* type variableDeclarators | expressionList );", 127, 29, input);

                        throw nvae;
                    }
                    }
                    break;
                case Identifier:
                    {
                    alt127=1;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("744:1: forInit : ( ( variableModifier )* type variableDeclarators | expressionList );", 127, 3, input);

                    throw nvae;
                }

                }
                break;
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
                {
                switch ( input.LA(2) ) {
                case 41:
                    {
                    int LA127_52 = input.LA(3);

                    if ( (synpred184()) ) {
                        alt127=1;
                    }
                    else if ( (true) ) {
                        alt127=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("744:1: forInit : ( ( variableModifier )* type variableDeclarators | expressionList );", 127, 52, input);

                        throw nvae;
                    }
                    }
                    break;
                case Identifier:
                    {
                    alt127=1;
                    }
                    break;
                case 28:
                    {
                    alt127=2;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("744:1: forInit : ( ( variableModifier )* type variableDeclarators | expressionList );", 127, 4, input);

                    throw nvae;
                }

                }
                break;
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
            case 33:
            case 40:
            case 64:
            case 65:
            case 68:
            case 69:
            case 70:
            case 105:
            case 106:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
                {
                alt127=2;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("744:1: forInit : ( ( variableModifier )* type variableDeclarators | expressionList );", 127, 0, input);

                throw nvae;
            }

            switch (alt127) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:751:4: ( variableModifier )* type variableDeclarators
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:751:4: ( variableModifier )*
                    loop126:
                    do {
                        int alt126=2;
                        int LA126_0 = input.LA(1);

                        if ( (LA126_0==49||LA126_0==71) ) {
                            alt126=1;
                        }


                        switch (alt126) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: variableModifier
                    	    {
                    	    pushFollow(FOLLOW_variableModifier_in_forInit2949);
                    	    variableModifier();
                    	    _fsp--;
                    	    if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop126;
                        }
                    } while (true);

                    pushFollow(FOLLOW_type_in_forInit2952);
                    type();
                    _fsp--;
                    if (failed) return ;
                    pushFollow(FOLLOW_variableDeclarators_in_forInit2954);
                    variableDeclarators();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:752:4: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_forInit2959);
                    expressionList();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }
            if ( backtracking==0 ) {

                          this.localVariableLevel--;
                      
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 93, forInit_StartIndex); }
        }
        return ;
    }
    // $ANTLR end forInit


    // $ANTLR start forVarControl
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:755:1: forVarControl : ( variableModifier )* type Identifier ':' expression ;
    public final void forVarControl() throws RecognitionException {
        int forVarControl_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 94) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:756:2: ( ( variableModifier )* type Identifier ':' expression )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:756:4: ( variableModifier )* type Identifier ':' expression
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:756:4: ( variableModifier )*
            loop128:
            do {
                int alt128=2;
                int LA128_0 = input.LA(1);

                if ( (LA128_0==49||LA128_0==71) ) {
                    alt128=1;
                }


                switch (alt128) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: variableModifier
            	    {
            	    pushFollow(FOLLOW_variableModifier_in_forVarControl2971);
            	    variableModifier();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop128;
                }
            } while (true);

            pushFollow(FOLLOW_type_in_forVarControl2974);
            type();
            _fsp--;
            if (failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_forVarControl2976); if (failed) return ;
            match(input,74,FOLLOW_74_in_forVarControl2978); if (failed) return ;
            pushFollow(FOLLOW_expression_in_forVarControl2980);
            expression();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 94, forVarControl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end forVarControl


    // $ANTLR start forUpdate
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:759:1: forUpdate : expressionList ;
    public final void forUpdate() throws RecognitionException {
        int forUpdate_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 95) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:760:2: ( expressionList )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:760:4: expressionList
            {
            pushFollow(FOLLOW_expressionList_in_forUpdate2991);
            expressionList();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 95, forUpdate_StartIndex); }
        }
        return ;
    }
    // $ANTLR end forUpdate

    public static class parExpression_return extends ParserRuleReturnScope {
    };

    // $ANTLR start parExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:765:1: parExpression : '(' expression ')' ;
    public final parExpression_return parExpression() throws RecognitionException {
        parExpression_return retval = new parExpression_return();
        retval.start = input.LT(1);
        int parExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 96) ) { return retval; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:766:2: ( '(' expression ')' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:766:4: '(' expression ')'
            {
            match(input,65,FOLLOW_65_in_parExpression3004); if (failed) return retval;
            pushFollow(FOLLOW_expression_in_parExpression3006);
            expression();
            _fsp--;
            if (failed) return retval;
            match(input,66,FOLLOW_66_in_parExpression3008); if (failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 96, parExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end parExpression


    // $ANTLR start expressionList
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:769:1: expressionList : expression ( ',' expression )* ;
    public final void expressionList() throws RecognitionException {
        int expressionList_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 97) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:770:5: ( expression ( ',' expression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:770:9: expression ( ',' expression )*
            {
            pushFollow(FOLLOW_expression_in_expressionList3025);
            expression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:770:20: ( ',' expression )*
            loop129:
            do {
                int alt129=2;
                int LA129_0 = input.LA(1);

                if ( (LA129_0==34) ) {
                    alt129=1;
                }


                switch (alt129) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:770:21: ',' expression
            	    {
            	    match(input,34,FOLLOW_34_in_expressionList3028); if (failed) return ;
            	    pushFollow(FOLLOW_expression_in_expressionList3030);
            	    expression();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop129;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 97, expressionList_StartIndex); }
        }
        return ;
    }
    // $ANTLR end expressionList


    // $ANTLR start statementExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:773:1: statementExpression : expression ;
    public final void statementExpression() throws RecognitionException {
        int statementExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 98) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:774:2: ( expression )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:774:4: expression
            {
            pushFollow(FOLLOW_expression_in_statementExpression3046);
            expression();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 98, statementExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end statementExpression


    // $ANTLR start constantExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:777:1: constantExpression : expression ;
    public final void constantExpression() throws RecognitionException {
        int constantExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 99) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:778:2: ( expression )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:778:4: expression
            {
            pushFollow(FOLLOW_expression_in_constantExpression3058);
            expression();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 99, constantExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end constantExpression

    public static class expression_return extends ParserRuleReturnScope {
    };

    // $ANTLR start expression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:781:1: expression : conditionalExpression ( assignmentOperator expression )? ;
    public final expression_return expression() throws RecognitionException {
        expression_return retval = new expression_return();
        retval.start = input.LT(1);
        int expression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 100) ) { return retval; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:782:2: ( conditionalExpression ( assignmentOperator expression )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:782:4: conditionalExpression ( assignmentOperator expression )?
            {
            pushFollow(FOLLOW_conditionalExpression_in_expression3070);
            conditionalExpression();
            _fsp--;
            if (failed) return retval;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:782:26: ( assignmentOperator expression )?
            int alt130=2;
            switch ( input.LA(1) ) {
                case 44:
                    {
                    int LA130_1 = input.LA(2);

                    if ( (synpred187()) ) {
                        alt130=1;
                    }
                    }
                    break;
                case 90:
                    {
                    int LA130_2 = input.LA(2);

                    if ( (synpred187()) ) {
                        alt130=1;
                    }
                    }
                    break;
                case 91:
                    {
                    int LA130_3 = input.LA(2);

                    if ( (synpred187()) ) {
                        alt130=1;
                    }
                    }
                    break;
                case 92:
                    {
                    int LA130_4 = input.LA(2);

                    if ( (synpred187()) ) {
                        alt130=1;
                    }
                    }
                    break;
                case 93:
                    {
                    int LA130_5 = input.LA(2);

                    if ( (synpred187()) ) {
                        alt130=1;
                    }
                    }
                    break;
                case 94:
                    {
                    int LA130_6 = input.LA(2);

                    if ( (synpred187()) ) {
                        alt130=1;
                    }
                    }
                    break;
                case 95:
                    {
                    int LA130_7 = input.LA(2);

                    if ( (synpred187()) ) {
                        alt130=1;
                    }
                    }
                    break;
                case 96:
                    {
                    int LA130_8 = input.LA(2);

                    if ( (synpred187()) ) {
                        alt130=1;
                    }
                    }
                    break;
                case 97:
                    {
                    int LA130_9 = input.LA(2);

                    if ( (synpred187()) ) {
                        alt130=1;
                    }
                    }
                    break;
                case 33:
                    {
                    int LA130_10 = input.LA(2);

                    if ( (synpred187()) ) {
                        alt130=1;
                    }
                    }
                    break;
                case 35:
                    {
                    int LA130_11 = input.LA(2);

                    if ( (synpred187()) ) {
                        alt130=1;
                    }
                    }
                    break;
            }

            switch (alt130) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:782:27: assignmentOperator expression
                    {
                    pushFollow(FOLLOW_assignmentOperator_in_expression3073);
                    assignmentOperator();
                    _fsp--;
                    if (failed) return retval;
                    pushFollow(FOLLOW_expression_in_expression3075);
                    expression();
                    _fsp--;
                    if (failed) return retval;

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
            if ( backtracking>0 ) { memoize(input, 100, expression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end expression


    // $ANTLR start assignmentOperator
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:785:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '=' | '>' '>' '>' '=' );
    public final void assignmentOperator() throws RecognitionException {
        int assignmentOperator_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 101) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:786:2: ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '=' | '>' '>' '>' '=' )
            int alt131=12;
            switch ( input.LA(1) ) {
            case 44:
                {
                alt131=1;
                }
                break;
            case 90:
                {
                alt131=2;
                }
                break;
            case 91:
                {
                alt131=3;
                }
                break;
            case 92:
                {
                alt131=4;
                }
                break;
            case 93:
                {
                alt131=5;
                }
                break;
            case 94:
                {
                alt131=6;
                }
                break;
            case 95:
                {
                alt131=7;
                }
                break;
            case 96:
                {
                alt131=8;
                }
                break;
            case 97:
                {
                alt131=9;
                }
                break;
            case 33:
                {
                alt131=10;
                }
                break;
            case 35:
                {
                int LA131_11 = input.LA(2);

                if ( (LA131_11==35) ) {
                    int LA131_12 = input.LA(3);

                    if ( (synpred198()) ) {
                        alt131=11;
                    }
                    else if ( (true) ) {
                        alt131=12;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("785:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '=' | '>' '>' '>' '=' );", 131, 12, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("785:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '=' | '>' '>' '>' '=' );", 131, 11, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("785:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '=' | '>' '>' '>' '=' );", 131, 0, input);

                throw nvae;
            }

            switch (alt131) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:786:4: '='
                    {
                    match(input,44,FOLLOW_44_in_assignmentOperator3089); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:787:9: '+='
                    {
                    match(input,90,FOLLOW_90_in_assignmentOperator3099); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:788:9: '-='
                    {
                    match(input,91,FOLLOW_91_in_assignmentOperator3109); if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:789:9: '*='
                    {
                    match(input,92,FOLLOW_92_in_assignmentOperator3119); if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:790:9: '/='
                    {
                    match(input,93,FOLLOW_93_in_assignmentOperator3129); if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:791:9: '&='
                    {
                    match(input,94,FOLLOW_94_in_assignmentOperator3139); if (failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:792:9: '|='
                    {
                    match(input,95,FOLLOW_95_in_assignmentOperator3149); if (failed) return ;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:793:9: '^='
                    {
                    match(input,96,FOLLOW_96_in_assignmentOperator3159); if (failed) return ;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:794:9: '%='
                    {
                    match(input,97,FOLLOW_97_in_assignmentOperator3169); if (failed) return ;

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:795:9: '<' '<' '='
                    {
                    match(input,33,FOLLOW_33_in_assignmentOperator3179); if (failed) return ;
                    match(input,33,FOLLOW_33_in_assignmentOperator3181); if (failed) return ;
                    match(input,44,FOLLOW_44_in_assignmentOperator3183); if (failed) return ;

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:796:9: '>' '>' '='
                    {
                    match(input,35,FOLLOW_35_in_assignmentOperator3193); if (failed) return ;
                    match(input,35,FOLLOW_35_in_assignmentOperator3195); if (failed) return ;
                    match(input,44,FOLLOW_44_in_assignmentOperator3197); if (failed) return ;

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:797:9: '>' '>' '>' '='
                    {
                    match(input,35,FOLLOW_35_in_assignmentOperator3207); if (failed) return ;
                    match(input,35,FOLLOW_35_in_assignmentOperator3209); if (failed) return ;
                    match(input,35,FOLLOW_35_in_assignmentOperator3211); if (failed) return ;
                    match(input,44,FOLLOW_44_in_assignmentOperator3213); if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 101, assignmentOperator_StartIndex); }
        }
        return ;
    }
    // $ANTLR end assignmentOperator


    // $ANTLR start conditionalExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:800:1: conditionalExpression : conditionalOrExpression ( '?' expression ':' expression )? ;
    public final void conditionalExpression() throws RecognitionException {
        int conditionalExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 102) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:801:5: ( conditionalOrExpression ( '?' expression ':' expression )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:801:9: conditionalOrExpression ( '?' expression ':' expression )?
            {
            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression3229);
            conditionalOrExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:801:33: ( '?' expression ':' expression )?
            int alt132=2;
            int LA132_0 = input.LA(1);

            if ( (LA132_0==63) ) {
                alt132=1;
            }
            switch (alt132) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:801:35: '?' expression ':' expression
                    {
                    match(input,63,FOLLOW_63_in_conditionalExpression3233); if (failed) return ;
                    pushFollow(FOLLOW_expression_in_conditionalExpression3235);
                    expression();
                    _fsp--;
                    if (failed) return ;
                    match(input,74,FOLLOW_74_in_conditionalExpression3237); if (failed) return ;
                    pushFollow(FOLLOW_expression_in_conditionalExpression3239);
                    expression();
                    _fsp--;
                    if (failed) return ;

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
            if ( backtracking>0 ) { memoize(input, 102, conditionalExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end conditionalExpression


    // $ANTLR start conditionalOrExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:804:1: conditionalOrExpression : conditionalAndExpression ( '||' conditionalAndExpression )* ;
    public final void conditionalOrExpression() throws RecognitionException {
        int conditionalOrExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 103) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:805:5: ( conditionalAndExpression ( '||' conditionalAndExpression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:805:9: conditionalAndExpression ( '||' conditionalAndExpression )*
            {
            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression3258);
            conditionalAndExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:805:34: ( '||' conditionalAndExpression )*
            loop133:
            do {
                int alt133=2;
                int LA133_0 = input.LA(1);

                if ( (LA133_0==98) ) {
                    alt133=1;
                }


                switch (alt133) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:805:36: '||' conditionalAndExpression
            	    {
            	    match(input,98,FOLLOW_98_in_conditionalOrExpression3262); if (failed) return ;
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression3264);
            	    conditionalAndExpression();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop133;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 103, conditionalOrExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end conditionalOrExpression


    // $ANTLR start conditionalAndExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:808:1: conditionalAndExpression : inclusiveOrExpression ( '&&' inclusiveOrExpression )* ;
    public final void conditionalAndExpression() throws RecognitionException {
        int conditionalAndExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 104) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:809:5: ( inclusiveOrExpression ( '&&' inclusiveOrExpression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:809:9: inclusiveOrExpression ( '&&' inclusiveOrExpression )*
            {
            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression3283);
            inclusiveOrExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:809:31: ( '&&' inclusiveOrExpression )*
            loop134:
            do {
                int alt134=2;
                int LA134_0 = input.LA(1);

                if ( (LA134_0==99) ) {
                    alt134=1;
                }


                switch (alt134) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:809:33: '&&' inclusiveOrExpression
            	    {
            	    match(input,99,FOLLOW_99_in_conditionalAndExpression3287); if (failed) return ;
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression3289);
            	    inclusiveOrExpression();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop134;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 104, conditionalAndExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end conditionalAndExpression


    // $ANTLR start inclusiveOrExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:812:1: inclusiveOrExpression : exclusiveOrExpression ( '|' exclusiveOrExpression )* ;
    public final void inclusiveOrExpression() throws RecognitionException {
        int inclusiveOrExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 105) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:813:5: ( exclusiveOrExpression ( '|' exclusiveOrExpression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:813:9: exclusiveOrExpression ( '|' exclusiveOrExpression )*
            {
            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression3308);
            exclusiveOrExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:813:31: ( '|' exclusiveOrExpression )*
            loop135:
            do {
                int alt135=2;
                int LA135_0 = input.LA(1);

                if ( (LA135_0==100) ) {
                    alt135=1;
                }


                switch (alt135) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:813:33: '|' exclusiveOrExpression
            	    {
            	    match(input,100,FOLLOW_100_in_inclusiveOrExpression3312); if (failed) return ;
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression3314);
            	    exclusiveOrExpression();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop135;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 105, inclusiveOrExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end inclusiveOrExpression


    // $ANTLR start exclusiveOrExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:816:1: exclusiveOrExpression : andExpression ( '^' andExpression )* ;
    public final void exclusiveOrExpression() throws RecognitionException {
        int exclusiveOrExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 106) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:817:5: ( andExpression ( '^' andExpression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:817:9: andExpression ( '^' andExpression )*
            {
            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression3333);
            andExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:817:23: ( '^' andExpression )*
            loop136:
            do {
                int alt136=2;
                int LA136_0 = input.LA(1);

                if ( (LA136_0==101) ) {
                    alt136=1;
                }


                switch (alt136) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:817:25: '^' andExpression
            	    {
            	    match(input,101,FOLLOW_101_in_exclusiveOrExpression3337); if (failed) return ;
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression3339);
            	    andExpression();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop136;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 106, exclusiveOrExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end exclusiveOrExpression


    // $ANTLR start andExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:820:1: andExpression : equalityExpression ( '&' equalityExpression )* ;
    public final void andExpression() throws RecognitionException {
        int andExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 107) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:821:5: ( equalityExpression ( '&' equalityExpression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:821:9: equalityExpression ( '&' equalityExpression )*
            {
            pushFollow(FOLLOW_equalityExpression_in_andExpression3358);
            equalityExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:821:28: ( '&' equalityExpression )*
            loop137:
            do {
                int alt137=2;
                int LA137_0 = input.LA(1);

                if ( (LA137_0==36) ) {
                    alt137=1;
                }


                switch (alt137) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:821:30: '&' equalityExpression
            	    {
            	    match(input,36,FOLLOW_36_in_andExpression3362); if (failed) return ;
            	    pushFollow(FOLLOW_equalityExpression_in_andExpression3364);
            	    equalityExpression();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop137;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 107, andExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end andExpression


    // $ANTLR start equalityExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:824:1: equalityExpression : instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* ;
    public final void equalityExpression() throws RecognitionException {
        int equalityExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 108) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:825:5: ( instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:825:9: instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )*
            {
            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression3383);
            instanceOfExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:825:30: ( ( '==' | '!=' ) instanceOfExpression )*
            loop138:
            do {
                int alt138=2;
                int LA138_0 = input.LA(1);

                if ( ((LA138_0>=102 && LA138_0<=103)) ) {
                    alt138=1;
                }


                switch (alt138) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:825:32: ( '==' | '!=' ) instanceOfExpression
            	    {
            	    if ( (input.LA(1)>=102 && input.LA(1)<=103) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_equalityExpression3387);    throw mse;
            	    }

            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression3395);
            	    instanceOfExpression();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop138;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 108, equalityExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end equalityExpression


    // $ANTLR start instanceOfExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:828:1: instanceOfExpression : relationalExpression ( 'instanceof' type )? ;
    public final void instanceOfExpression() throws RecognitionException {
        int instanceOfExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 109) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:829:5: ( relationalExpression ( 'instanceof' type )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:829:9: relationalExpression ( 'instanceof' type )?
            {
            pushFollow(FOLLOW_relationalExpression_in_instanceOfExpression3414);
            relationalExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:829:30: ( 'instanceof' type )?
            int alt139=2;
            int LA139_0 = input.LA(1);

            if ( (LA139_0==104) ) {
                alt139=1;
            }
            switch (alt139) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:829:31: 'instanceof' type
                    {
                    match(input,104,FOLLOW_104_in_instanceOfExpression3417); if (failed) return ;
                    pushFollow(FOLLOW_type_in_instanceOfExpression3419);
                    type();
                    _fsp--;
                    if (failed) return ;

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
            if ( backtracking>0 ) { memoize(input, 109, instanceOfExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end instanceOfExpression


    // $ANTLR start relationalExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:832:1: relationalExpression : shiftExpression ( relationalOp shiftExpression )* ;
    public final void relationalExpression() throws RecognitionException {
        int relationalExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 110) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:833:5: ( shiftExpression ( relationalOp shiftExpression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:833:9: shiftExpression ( relationalOp shiftExpression )*
            {
            pushFollow(FOLLOW_shiftExpression_in_relationalExpression3437);
            shiftExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:833:25: ( relationalOp shiftExpression )*
            loop140:
            do {
                int alt140=2;
                int LA140_0 = input.LA(1);

                if ( (LA140_0==33) ) {
                    int LA140_23 = input.LA(2);

                    if ( (LA140_23==Identifier||(LA140_23>=FloatingPointLiteral && LA140_23<=DecimalLiteral)||LA140_23==40||LA140_23==44||(LA140_23>=55 && LA140_23<=62)||(LA140_23>=64 && LA140_23<=65)||(LA140_23>=68 && LA140_23<=70)||(LA140_23>=105 && LA140_23<=106)||(LA140_23>=109 && LA140_23<=114)) ) {
                        alt140=1;
                    }
                    else if ( (LA140_23==33) ) {
                        int LA140_28 = input.LA(3);

                        if ( (synpred208()) ) {
                            alt140=1;
                        }


                    }


                }
                else if ( (LA140_0==35) ) {
                    int LA140_24 = input.LA(2);

                    if ( (LA140_24==Identifier||(LA140_24>=FloatingPointLiteral && LA140_24<=DecimalLiteral)||LA140_24==33||LA140_24==40||LA140_24==44||(LA140_24>=55 && LA140_24<=62)||(LA140_24>=64 && LA140_24<=65)||(LA140_24>=68 && LA140_24<=70)||(LA140_24>=105 && LA140_24<=106)||(LA140_24>=109 && LA140_24<=114)) ) {
                        alt140=1;
                    }


                }


                switch (alt140) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:833:27: relationalOp shiftExpression
            	    {
            	    pushFollow(FOLLOW_relationalOp_in_relationalExpression3441);
            	    relationalOp();
            	    _fsp--;
            	    if (failed) return ;
            	    pushFollow(FOLLOW_shiftExpression_in_relationalExpression3443);
            	    shiftExpression();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop140;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 110, relationalExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end relationalExpression


    // $ANTLR start relationalOp
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:836:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' ) ;
    public final void relationalOp() throws RecognitionException {
        int relationalOp_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 111) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:837:2: ( ( '<' '=' | '>' '=' | '<' | '>' ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:837:4: ( '<' '=' | '>' '=' | '<' | '>' )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:837:4: ( '<' '=' | '>' '=' | '<' | '>' )
            int alt141=4;
            int LA141_0 = input.LA(1);

            if ( (LA141_0==33) ) {
                int LA141_1 = input.LA(2);

                if ( (LA141_1==44) ) {
                    alt141=1;
                }
                else if ( (LA141_1==Identifier||(LA141_1>=FloatingPointLiteral && LA141_1<=DecimalLiteral)||LA141_1==33||LA141_1==40||(LA141_1>=55 && LA141_1<=62)||(LA141_1>=64 && LA141_1<=65)||(LA141_1>=68 && LA141_1<=70)||(LA141_1>=105 && LA141_1<=106)||(LA141_1>=109 && LA141_1<=114)) ) {
                    alt141=3;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("837:4: ( '<' '=' | '>' '=' | '<' | '>' )", 141, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA141_0==35) ) {
                int LA141_2 = input.LA(2);

                if ( (LA141_2==44) ) {
                    alt141=2;
                }
                else if ( (LA141_2==Identifier||(LA141_2>=FloatingPointLiteral && LA141_2<=DecimalLiteral)||LA141_2==33||LA141_2==40||(LA141_2>=55 && LA141_2<=62)||(LA141_2>=64 && LA141_2<=65)||(LA141_2>=68 && LA141_2<=70)||(LA141_2>=105 && LA141_2<=106)||(LA141_2>=109 && LA141_2<=114)) ) {
                    alt141=4;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("837:4: ( '<' '=' | '>' '=' | '<' | '>' )", 141, 2, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("837:4: ( '<' '=' | '>' '=' | '<' | '>' )", 141, 0, input);

                throw nvae;
            }
            switch (alt141) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:837:5: '<' '='
                    {
                    match(input,33,FOLLOW_33_in_relationalOp3459); if (failed) return ;
                    match(input,44,FOLLOW_44_in_relationalOp3461); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:837:15: '>' '='
                    {
                    match(input,35,FOLLOW_35_in_relationalOp3465); if (failed) return ;
                    match(input,44,FOLLOW_44_in_relationalOp3467); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:837:25: '<'
                    {
                    match(input,33,FOLLOW_33_in_relationalOp3471); if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:837:31: '>'
                    {
                    match(input,35,FOLLOW_35_in_relationalOp3475); if (failed) return ;

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
            if ( backtracking>0 ) { memoize(input, 111, relationalOp_StartIndex); }
        }
        return ;
    }
    // $ANTLR end relationalOp


    // $ANTLR start shiftExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:840:1: shiftExpression : additiveExpression ( shiftOp additiveExpression )* ;
    public final void shiftExpression() throws RecognitionException {
        int shiftExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 112) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:841:5: ( additiveExpression ( shiftOp additiveExpression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:841:9: additiveExpression ( shiftOp additiveExpression )*
            {
            pushFollow(FOLLOW_additiveExpression_in_shiftExpression3492);
            additiveExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:841:28: ( shiftOp additiveExpression )*
            loop142:
            do {
                int alt142=2;
                int LA142_0 = input.LA(1);

                if ( (LA142_0==33) ) {
                    int LA142_1 = input.LA(2);

                    if ( (LA142_1==33) ) {
                        int LA142_27 = input.LA(3);

                        if ( (synpred212()) ) {
                            alt142=1;
                        }


                    }


                }
                else if ( (LA142_0==35) ) {
                    int LA142_2 = input.LA(2);

                    if ( (LA142_2==35) ) {
                        int LA142_48 = input.LA(3);

                        if ( (synpred212()) ) {
                            alt142=1;
                        }


                    }


                }


                switch (alt142) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:841:30: shiftOp additiveExpression
            	    {
            	    pushFollow(FOLLOW_shiftOp_in_shiftExpression3496);
            	    shiftOp();
            	    _fsp--;
            	    if (failed) return ;
            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression3498);
            	    additiveExpression();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop142;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 112, shiftExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end shiftExpression


    // $ANTLR start shiftOp
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:845:1: shiftOp : ( '<' '<' | '>' '>' '>' | '>' '>' ) ;
    public final void shiftOp() throws RecognitionException {
        int shiftOp_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 113) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:846:2: ( ( '<' '<' | '>' '>' '>' | '>' '>' ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:846:4: ( '<' '<' | '>' '>' '>' | '>' '>' )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:846:4: ( '<' '<' | '>' '>' '>' | '>' '>' )
            int alt143=3;
            int LA143_0 = input.LA(1);

            if ( (LA143_0==33) ) {
                alt143=1;
            }
            else if ( (LA143_0==35) ) {
                int LA143_2 = input.LA(2);

                if ( (LA143_2==35) ) {
                    int LA143_3 = input.LA(3);

                    if ( (synpred214()) ) {
                        alt143=2;
                    }
                    else if ( (true) ) {
                        alt143=3;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("846:4: ( '<' '<' | '>' '>' '>' | '>' '>' )", 143, 3, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("846:4: ( '<' '<' | '>' '>' '>' | '>' '>' )", 143, 2, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("846:4: ( '<' '<' | '>' '>' '>' | '>' '>' )", 143, 0, input);

                throw nvae;
            }
            switch (alt143) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:846:5: '<' '<'
                    {
                    match(input,33,FOLLOW_33_in_shiftOp3522); if (failed) return ;
                    match(input,33,FOLLOW_33_in_shiftOp3524); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:846:15: '>' '>' '>'
                    {
                    match(input,35,FOLLOW_35_in_shiftOp3528); if (failed) return ;
                    match(input,35,FOLLOW_35_in_shiftOp3530); if (failed) return ;
                    match(input,35,FOLLOW_35_in_shiftOp3532); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:846:29: '>' '>'
                    {
                    match(input,35,FOLLOW_35_in_shiftOp3536); if (failed) return ;
                    match(input,35,FOLLOW_35_in_shiftOp3538); if (failed) return ;

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
            if ( backtracking>0 ) { memoize(input, 113, shiftOp_StartIndex); }
        }
        return ;
    }
    // $ANTLR end shiftOp


    // $ANTLR start additiveExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:850:1: additiveExpression : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* ;
    public final void additiveExpression() throws RecognitionException {
        int additiveExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 114) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:851:5: ( multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:851:9: multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression3556);
            multiplicativeExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:851:34: ( ( '+' | '-' ) multiplicativeExpression )*
            loop144:
            do {
                int alt144=2;
                int LA144_0 = input.LA(1);

                if ( ((LA144_0>=105 && LA144_0<=106)) ) {
                    alt144=1;
                }


                switch (alt144) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:851:36: ( '+' | '-' ) multiplicativeExpression
            	    {
            	    if ( (input.LA(1)>=105 && input.LA(1)<=106) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_additiveExpression3560);    throw mse;
            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression3568);
            	    multiplicativeExpression();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop144;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 114, additiveExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end additiveExpression


    // $ANTLR start multiplicativeExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:854:1: multiplicativeExpression : unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* ;
    public final void multiplicativeExpression() throws RecognitionException {
        int multiplicativeExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 115) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:855:5: ( unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:855:9: unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
            {
            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression3587);
            unaryExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:855:25: ( ( '*' | '/' | '%' ) unaryExpression )*
            loop145:
            do {
                int alt145=2;
                int LA145_0 = input.LA(1);

                if ( (LA145_0==29||(LA145_0>=107 && LA145_0<=108)) ) {
                    alt145=1;
                }


                switch (alt145) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:855:27: ( '*' | '/' | '%' ) unaryExpression
            	    {
            	    if ( input.LA(1)==29||(input.LA(1)>=107 && input.LA(1)<=108) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_multiplicativeExpression3591);    throw mse;
            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression3605);
            	    unaryExpression();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop145;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 115, multiplicativeExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end multiplicativeExpression


    // $ANTLR start unaryExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:858:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' primary | '--' primary | unaryExpressionNotPlusMinus );
    public final void unaryExpression() throws RecognitionException {
        int unaryExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 116) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:859:5: ( '+' unaryExpression | '-' unaryExpression | '++' primary | '--' primary | unaryExpressionNotPlusMinus )
            int alt146=5;
            switch ( input.LA(1) ) {
            case 105:
                {
                alt146=1;
                }
                break;
            case 106:
                {
                alt146=2;
                }
                break;
            case 109:
                {
                alt146=3;
                }
                break;
            case 110:
                {
                alt146=4;
                }
                break;
            case Identifier:
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
            case 33:
            case 40:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 64:
            case 65:
            case 68:
            case 69:
            case 70:
            case 111:
            case 112:
            case 113:
            case 114:
                {
                alt146=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("858:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' primary | '--' primary | unaryExpressionNotPlusMinus );", 146, 0, input);

                throw nvae;
            }

            switch (alt146) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:859:9: '+' unaryExpression
                    {
                    match(input,105,FOLLOW_105_in_unaryExpression3625); if (failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression3627);
                    unaryExpression();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:860:7: '-' unaryExpression
                    {
                    match(input,106,FOLLOW_106_in_unaryExpression3635); if (failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression3637);
                    unaryExpression();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:861:9: '++' primary
                    {
                    match(input,109,FOLLOW_109_in_unaryExpression3647); if (failed) return ;
                    pushFollow(FOLLOW_primary_in_unaryExpression3649);
                    primary();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:862:9: '--' primary
                    {
                    match(input,110,FOLLOW_110_in_unaryExpression3659); if (failed) return ;
                    pushFollow(FOLLOW_primary_in_unaryExpression3661);
                    primary();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:863:9: unaryExpressionNotPlusMinus
                    {
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression3671);
                    unaryExpressionNotPlusMinus();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 116, unaryExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end unaryExpression


    // $ANTLR start unaryExpressionNotPlusMinus
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );
    public final void unaryExpressionNotPlusMinus() throws RecognitionException {
        int unaryExpressionNotPlusMinus_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 117) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:867:5: ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? )
            int alt149=4;
            switch ( input.LA(1) ) {
            case 111:
                {
                alt149=1;
                }
                break;
            case 112:
                {
                alt149=2;
                }
                break;
            case 65:
                {
                switch ( input.LA(2) ) {
                case Identifier:
                    {
                    int LA149_17 = input.LA(3);

                    if ( (synpred226()) ) {
                        alt149=3;
                    }
                    else if ( (true) ) {
                        alt149=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 149, 17, input);

                        throw nvae;
                    }
                    }
                    break;
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                    {
                    int LA149_18 = input.LA(3);

                    if ( (synpred226()) ) {
                        alt149=3;
                    }
                    else if ( (true) ) {
                        alt149=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 149, 18, input);

                        throw nvae;
                    }
                    }
                    break;
                case 105:
                    {
                    int LA149_19 = input.LA(3);

                    if ( (synpred226()) ) {
                        alt149=3;
                    }
                    else if ( (true) ) {
                        alt149=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 149, 19, input);

                        throw nvae;
                    }
                    }
                    break;
                case 106:
                    {
                    int LA149_20 = input.LA(3);

                    if ( (synpred226()) ) {
                        alt149=3;
                    }
                    else if ( (true) ) {
                        alt149=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 149, 20, input);

                        throw nvae;
                    }
                    }
                    break;
                case 109:
                    {
                    int LA149_21 = input.LA(3);

                    if ( (synpred226()) ) {
                        alt149=3;
                    }
                    else if ( (true) ) {
                        alt149=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 149, 21, input);

                        throw nvae;
                    }
                    }
                    break;
                case 110:
                    {
                    int LA149_22 = input.LA(3);

                    if ( (synpred226()) ) {
                        alt149=3;
                    }
                    else if ( (true) ) {
                        alt149=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 149, 22, input);

                        throw nvae;
                    }
                    }
                    break;
                case 111:
                    {
                    int LA149_23 = input.LA(3);

                    if ( (synpred226()) ) {
                        alt149=3;
                    }
                    else if ( (true) ) {
                        alt149=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 149, 23, input);

                        throw nvae;
                    }
                    }
                    break;
                case 112:
                    {
                    int LA149_24 = input.LA(3);

                    if ( (synpred226()) ) {
                        alt149=3;
                    }
                    else if ( (true) ) {
                        alt149=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 149, 24, input);

                        throw nvae;
                    }
                    }
                    break;
                case 65:
                    {
                    int LA149_25 = input.LA(3);

                    if ( (synpred226()) ) {
                        alt149=3;
                    }
                    else if ( (true) ) {
                        alt149=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 149, 25, input);

                        throw nvae;
                    }
                    }
                    break;
                case 33:
                    {
                    int LA149_26 = input.LA(3);

                    if ( (synpred226()) ) {
                        alt149=3;
                    }
                    else if ( (true) ) {
                        alt149=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 149, 26, input);

                        throw nvae;
                    }
                    }
                    break;
                case 113:
                    {
                    int LA149_27 = input.LA(3);

                    if ( (synpred226()) ) {
                        alt149=3;
                    }
                    else if ( (true) ) {
                        alt149=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 149, 27, input);

                        throw nvae;
                    }
                    }
                    break;
                case 64:
                    {
                    int LA149_28 = input.LA(3);

                    if ( (synpred226()) ) {
                        alt149=3;
                    }
                    else if ( (true) ) {
                        alt149=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 149, 28, input);

                        throw nvae;
                    }
                    }
                    break;
                case HexLiteral:
                case OctalLiteral:
                case DecimalLiteral:
                    {
                    int LA149_29 = input.LA(3);

                    if ( (synpred226()) ) {
                        alt149=3;
                    }
                    else if ( (true) ) {
                        alt149=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 149, 29, input);

                        throw nvae;
                    }
                    }
                    break;
                case FloatingPointLiteral:
                    {
                    int LA149_30 = input.LA(3);

                    if ( (synpred226()) ) {
                        alt149=3;
                    }
                    else if ( (true) ) {
                        alt149=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 149, 30, input);

                        throw nvae;
                    }
                    }
                    break;
                case CharacterLiteral:
                    {
                    int LA149_31 = input.LA(3);

                    if ( (synpred226()) ) {
                        alt149=3;
                    }
                    else if ( (true) ) {
                        alt149=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 149, 31, input);

                        throw nvae;
                    }
                    }
                    break;
                case StringLiteral:
                    {
                    int LA149_32 = input.LA(3);

                    if ( (synpred226()) ) {
                        alt149=3;
                    }
                    else if ( (true) ) {
                        alt149=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 149, 32, input);

                        throw nvae;
                    }
                    }
                    break;
                case 69:
                case 70:
                    {
                    int LA149_33 = input.LA(3);

                    if ( (synpred226()) ) {
                        alt149=3;
                    }
                    else if ( (true) ) {
                        alt149=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 149, 33, input);

                        throw nvae;
                    }
                    }
                    break;
                case 68:
                    {
                    int LA149_34 = input.LA(3);

                    if ( (synpred226()) ) {
                        alt149=3;
                    }
                    else if ( (true) ) {
                        alt149=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 149, 34, input);

                        throw nvae;
                    }
                    }
                    break;
                case 114:
                    {
                    int LA149_35 = input.LA(3);

                    if ( (synpred226()) ) {
                        alt149=3;
                    }
                    else if ( (true) ) {
                        alt149=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 149, 35, input);

                        throw nvae;
                    }
                    }
                    break;
                case 40:
                    {
                    int LA149_36 = input.LA(3);

                    if ( (synpred226()) ) {
                        alt149=3;
                    }
                    else if ( (true) ) {
                        alt149=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 149, 36, input);

                        throw nvae;
                    }
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 149, 3, input);

                    throw nvae;
                }

                }
                break;
            case Identifier:
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
            case 33:
            case 40:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 64:
            case 68:
            case 69:
            case 70:
            case 113:
            case 114:
                {
                alt149=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("866:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 149, 0, input);

                throw nvae;
            }

            switch (alt149) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:867:9: '~' unaryExpression
                    {
                    match(input,111,FOLLOW_111_in_unaryExpressionNotPlusMinus3690); if (failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus3692);
                    unaryExpression();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:868:8: '!' unaryExpression
                    {
                    match(input,112,FOLLOW_112_in_unaryExpressionNotPlusMinus3701); if (failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus3703);
                    unaryExpression();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:869:9: castExpression
                    {
                    pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus3713);
                    castExpression();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:870:9: primary ( selector )* ( '++' | '--' )?
                    {
                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus3723);
                    primary();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:870:17: ( selector )*
                    loop147:
                    do {
                        int alt147=2;
                        int LA147_0 = input.LA(1);

                        if ( (LA147_0==28||LA147_0==41) ) {
                            alt147=1;
                        }


                        switch (alt147) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: selector
                    	    {
                    	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus3725);
                    	    selector();
                    	    _fsp--;
                    	    if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop147;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:870:27: ( '++' | '--' )?
                    int alt148=2;
                    int LA148_0 = input.LA(1);

                    if ( ((LA148_0>=109 && LA148_0<=110)) ) {
                        alt148=1;
                    }
                    switch (alt148) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:
                            {
                            if ( (input.LA(1)>=109 && input.LA(1)<=110) ) {
                                input.consume();
                                errorRecovery=false;failed=false;
                            }
                            else {
                                if (backtracking>0) {failed=true; return ;}
                                MismatchedSetException mse =
                                    new MismatchedSetException(null,input);
                                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_unaryExpressionNotPlusMinus3728);    throw mse;
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
            if ( backtracking>0 ) { memoize(input, 117, unaryExpressionNotPlusMinus_StartIndex); }
        }
        return ;
    }
    // $ANTLR end unaryExpressionNotPlusMinus


    // $ANTLR start castExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:873:1: castExpression : ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus );
    public final void castExpression() throws RecognitionException {
        int castExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 118) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:874:5: ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus )
            int alt151=2;
            int LA151_0 = input.LA(1);

            if ( (LA151_0==65) ) {
                int LA151_1 = input.LA(2);

                if ( ((LA151_1>=55 && LA151_1<=62)) ) {
                    int LA151_2 = input.LA(3);

                    if ( (synpred230()) ) {
                        alt151=1;
                    }
                    else if ( (true) ) {
                        alt151=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("873:1: castExpression : ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus );", 151, 2, input);

                        throw nvae;
                    }
                }
                else if ( (LA151_1==Identifier||(LA151_1>=FloatingPointLiteral && LA151_1<=DecimalLiteral)||LA151_1==33||LA151_1==40||(LA151_1>=64 && LA151_1<=65)||(LA151_1>=68 && LA151_1<=70)||(LA151_1>=105 && LA151_1<=106)||(LA151_1>=109 && LA151_1<=114)) ) {
                    alt151=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("873:1: castExpression : ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus );", 151, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("873:1: castExpression : ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus );", 151, 0, input);

                throw nvae;
            }
            switch (alt151) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:874:8: '(' primitiveType ')' unaryExpression
                    {
                    match(input,65,FOLLOW_65_in_castExpression3751); if (failed) return ;
                    pushFollow(FOLLOW_primitiveType_in_castExpression3753);
                    primitiveType();
                    _fsp--;
                    if (failed) return ;
                    match(input,66,FOLLOW_66_in_castExpression3755); if (failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_castExpression3757);
                    unaryExpression();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:875:8: '(' ( type | expression ) ')' unaryExpressionNotPlusMinus
                    {
                    match(input,65,FOLLOW_65_in_castExpression3766); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:875:12: ( type | expression )
                    int alt150=2;
                    switch ( input.LA(1) ) {
                    case Identifier:
                        {
                        int LA150_1 = input.LA(2);

                        if ( (synpred231()) ) {
                            alt150=1;
                        }
                        else if ( (true) ) {
                            alt150=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("875:12: ( type | expression )", 150, 1, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 55:
                    case 56:
                    case 57:
                    case 58:
                    case 59:
                    case 60:
                    case 61:
                    case 62:
                        {
                        switch ( input.LA(2) ) {
                        case 41:
                            {
                            int LA150_48 = input.LA(3);

                            if ( (synpred231()) ) {
                                alt150=1;
                            }
                            else if ( (true) ) {
                                alt150=2;
                            }
                            else {
                                if (backtracking>0) {failed=true; return ;}
                                NoViableAltException nvae =
                                    new NoViableAltException("875:12: ( type | expression )", 150, 48, input);

                                throw nvae;
                            }
                            }
                            break;
                        case 66:
                            {
                            alt150=1;
                            }
                            break;
                        case 28:
                            {
                            alt150=2;
                            }
                            break;
                        default:
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("875:12: ( type | expression )", 150, 2, input);

                            throw nvae;
                        }

                        }
                        break;
                    case FloatingPointLiteral:
                    case CharacterLiteral:
                    case StringLiteral:
                    case HexLiteral:
                    case OctalLiteral:
                    case DecimalLiteral:
                    case 33:
                    case 40:
                    case 64:
                    case 65:
                    case 68:
                    case 69:
                    case 70:
                    case 105:
                    case 106:
                    case 109:
                    case 110:
                    case 111:
                    case 112:
                    case 113:
                    case 114:
                        {
                        alt150=2;
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("875:12: ( type | expression )", 150, 0, input);

                        throw nvae;
                    }

                    switch (alt150) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:875:13: type
                            {
                            pushFollow(FOLLOW_type_in_castExpression3769);
                            type();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;
                        case 2 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:875:20: expression
                            {
                            pushFollow(FOLLOW_expression_in_castExpression3773);
                            expression();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    match(input,66,FOLLOW_66_in_castExpression3776); if (failed) return ;
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression3778);
                    unaryExpressionNotPlusMinus();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 118, castExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end castExpression


    // $ANTLR start primary
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:878:1: primary : ( parExpression | nonWildcardTypeArguments ( explicitGenericInvocationSuffix | 'this' arguments ) | 'this' ( '.' Identifier )* ( identifierSuffix )? | 'super' superSuffix | literal | 'new' creator | i= Identifier ( '.' Identifier )* ( identifierSuffix )? | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );
    public final void primary() throws RecognitionException {
        int primary_StartIndex = input.index();
        Token i=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 119) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:879:5: ( parExpression | nonWildcardTypeArguments ( explicitGenericInvocationSuffix | 'this' arguments ) | 'this' ( '.' Identifier )* ( identifierSuffix )? | 'super' superSuffix | literal | 'new' creator | i= Identifier ( '.' Identifier )* ( identifierSuffix )? | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' )
            int alt158=9;
            switch ( input.LA(1) ) {
            case 65:
                {
                alt158=1;
                }
                break;
            case 33:
                {
                alt158=2;
                }
                break;
            case 113:
                {
                alt158=3;
                }
                break;
            case 64:
                {
                alt158=4;
                }
                break;
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
            case 68:
            case 69:
            case 70:
                {
                alt158=5;
                }
                break;
            case 114:
                {
                alt158=6;
                }
                break;
            case Identifier:
                {
                alt158=7;
                }
                break;
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
                {
                alt158=8;
                }
                break;
            case 40:
                {
                alt158=9;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("878:1: primary : ( parExpression | nonWildcardTypeArguments ( explicitGenericInvocationSuffix | 'this' arguments ) | 'this' ( '.' Identifier )* ( identifierSuffix )? | 'super' superSuffix | literal | 'new' creator | i= Identifier ( '.' Identifier )* ( identifierSuffix )? | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );", 158, 0, input);

                throw nvae;
            }

            switch (alt158) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:879:7: parExpression
                    {
                    pushFollow(FOLLOW_parExpression_in_primary3795);
                    parExpression();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:880:9: nonWildcardTypeArguments ( explicitGenericInvocationSuffix | 'this' arguments )
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_primary3805);
                    nonWildcardTypeArguments();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:881:9: ( explicitGenericInvocationSuffix | 'this' arguments )
                    int alt152=2;
                    int LA152_0 = input.LA(1);

                    if ( (LA152_0==Identifier||LA152_0==64) ) {
                        alt152=1;
                    }
                    else if ( (LA152_0==113) ) {
                        alt152=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("881:9: ( explicitGenericInvocationSuffix | 'this' arguments )", 152, 0, input);

                        throw nvae;
                    }
                    switch (alt152) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:881:10: explicitGenericInvocationSuffix
                            {
                            pushFollow(FOLLOW_explicitGenericInvocationSuffix_in_primary3816);
                            explicitGenericInvocationSuffix();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;
                        case 2 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:881:44: 'this' arguments
                            {
                            match(input,113,FOLLOW_113_in_primary3820); if (failed) return ;
                            pushFollow(FOLLOW_arguments_in_primary3822);
                            arguments();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:882:9: 'this' ( '.' Identifier )* ( identifierSuffix )?
                    {
                    match(input,113,FOLLOW_113_in_primary3833); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:882:16: ( '.' Identifier )*
                    loop153:
                    do {
                        int alt153=2;
                        int LA153_0 = input.LA(1);

                        if ( (LA153_0==28) ) {
                            int LA153_3 = input.LA(2);

                            if ( (LA153_3==Identifier) ) {
                                int LA153_36 = input.LA(3);

                                if ( (synpred235()) ) {
                                    alt153=1;
                                }


                            }


                        }


                        switch (alt153) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:882:17: '.' Identifier
                    	    {
                    	    match(input,28,FOLLOW_28_in_primary3836); if (failed) return ;
                    	    match(input,Identifier,FOLLOW_Identifier_in_primary3838); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop153;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:882:34: ( identifierSuffix )?
                    int alt154=2;
                    switch ( input.LA(1) ) {
                        case 41:
                            {
                            switch ( input.LA(2) ) {
                                case 42:
                                    {
                                    alt154=1;
                                    }
                                    break;
                                case 105:
                                    {
                                    int LA154_34 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 106:
                                    {
                                    int LA154_35 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 109:
                                    {
                                    int LA154_36 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 110:
                                    {
                                    int LA154_37 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 111:
                                    {
                                    int LA154_38 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 112:
                                    {
                                    int LA154_39 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 65:
                                    {
                                    int LA154_40 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 33:
                                    {
                                    int LA154_41 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 113:
                                    {
                                    int LA154_42 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 64:
                                    {
                                    int LA154_43 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case HexLiteral:
                                case OctalLiteral:
                                case DecimalLiteral:
                                    {
                                    int LA154_44 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case FloatingPointLiteral:
                                    {
                                    int LA154_45 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case CharacterLiteral:
                                    {
                                    int LA154_46 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case StringLiteral:
                                    {
                                    int LA154_47 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 69:
                                case 70:
                                    {
                                    int LA154_48 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 68:
                                    {
                                    int LA154_49 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 114:
                                    {
                                    int LA154_50 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case Identifier:
                                    {
                                    int LA154_51 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 55:
                                case 56:
                                case 57:
                                case 58:
                                case 59:
                                case 60:
                                case 61:
                                case 62:
                                    {
                                    int LA154_52 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 40:
                                    {
                                    int LA154_53 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                            }

                            }
                            break;
                        case 65:
                            {
                            alt154=1;
                            }
                            break;
                        case 28:
                            {
                            switch ( input.LA(2) ) {
                                case 113:
                                    {
                                    int LA154_54 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 30:
                                case 33:
                                    {
                                    alt154=1;
                                    }
                                    break;
                                case 64:
                                    {
                                    int LA154_56 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 114:
                                    {
                                    int LA154_57 = input.LA(3);

                                    if ( (synpred236()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                            }

                            }
                            break;
                    }

                    switch (alt154) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:882:35: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary3843);
                            identifierSuffix();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:883:9: 'super' superSuffix
                    {
                    match(input,64,FOLLOW_64_in_primary3855); if (failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_primary3857);
                    superSuffix();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:884:9: literal
                    {
                    pushFollow(FOLLOW_literal_in_primary3867);
                    literal();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:885:9: 'new' creator
                    {
                    match(input,114,FOLLOW_114_in_primary3877); if (failed) return ;
                    pushFollow(FOLLOW_creator_in_primary3879);
                    creator();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:886:9: i= Identifier ( '.' Identifier )* ( identifierSuffix )?
                    {
                    i=(Token)input.LT(1);
                    match(input,Identifier,FOLLOW_Identifier_in_primary3891); if (failed) return ;
                    if ( backtracking==0 ) {
                       if( ! "(".equals( input.LT(1) == null ? "" : input.LT(1).getText() ) ) identifiers.add( i.getText() );  
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:886:126: ( '.' Identifier )*
                    loop155:
                    do {
                        int alt155=2;
                        int LA155_0 = input.LA(1);

                        if ( (LA155_0==28) ) {
                            int LA155_3 = input.LA(2);

                            if ( (LA155_3==Identifier) ) {
                                int LA155_37 = input.LA(3);

                                if ( (synpred241()) ) {
                                    alt155=1;
                                }


                            }


                        }


                        switch (alt155) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:886:127: '.' Identifier
                    	    {
                    	    match(input,28,FOLLOW_28_in_primary3896); if (failed) return ;
                    	    match(input,Identifier,FOLLOW_Identifier_in_primary3898); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop155;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:886:144: ( identifierSuffix )?
                    int alt156=2;
                    switch ( input.LA(1) ) {
                        case 41:
                            {
                            switch ( input.LA(2) ) {
                                case 42:
                                    {
                                    alt156=1;
                                    }
                                    break;
                                case 105:
                                    {
                                    int LA156_34 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                                case 106:
                                    {
                                    int LA156_35 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                                case 109:
                                    {
                                    int LA156_36 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                                case 110:
                                    {
                                    int LA156_37 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                                case 111:
                                    {
                                    int LA156_38 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                                case 112:
                                    {
                                    int LA156_39 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                                case 65:
                                    {
                                    int LA156_40 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                                case 33:
                                    {
                                    int LA156_41 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                                case 113:
                                    {
                                    int LA156_42 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                                case 64:
                                    {
                                    int LA156_43 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                                case HexLiteral:
                                case OctalLiteral:
                                case DecimalLiteral:
                                    {
                                    int LA156_44 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                                case FloatingPointLiteral:
                                    {
                                    int LA156_45 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                                case CharacterLiteral:
                                    {
                                    int LA156_46 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                                case StringLiteral:
                                    {
                                    int LA156_47 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                                case 69:
                                case 70:
                                    {
                                    int LA156_48 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                                case 68:
                                    {
                                    int LA156_49 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                                case 114:
                                    {
                                    int LA156_50 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                                case Identifier:
                                    {
                                    int LA156_51 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                                case 55:
                                case 56:
                                case 57:
                                case 58:
                                case 59:
                                case 60:
                                case 61:
                                case 62:
                                    {
                                    int LA156_52 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                                case 40:
                                    {
                                    int LA156_53 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                            }

                            }
                            break;
                        case 65:
                            {
                            alt156=1;
                            }
                            break;
                        case 28:
                            {
                            switch ( input.LA(2) ) {
                                case 113:
                                    {
                                    int LA156_54 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                                case 64:
                                    {
                                    int LA156_55 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                                case 114:
                                    {
                                    int LA156_56 = input.LA(3);

                                    if ( (synpred242()) ) {
                                        alt156=1;
                                    }
                                    }
                                    break;
                                case 30:
                                case 33:
                                    {
                                    alt156=1;
                                    }
                                    break;
                            }

                            }
                            break;
                    }

                    switch (alt156) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:886:145: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary3903);
                            identifierSuffix();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:887:9: primitiveType ( '[' ']' )* '.' 'class'
                    {
                    pushFollow(FOLLOW_primitiveType_in_primary3915);
                    primitiveType();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:887:23: ( '[' ']' )*
                    loop157:
                    do {
                        int alt157=2;
                        int LA157_0 = input.LA(1);

                        if ( (LA157_0==41) ) {
                            alt157=1;
                        }


                        switch (alt157) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:887:24: '[' ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_primary3918); if (failed) return ;
                    	    match(input,42,FOLLOW_42_in_primary3920); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop157;
                        }
                    } while (true);

                    match(input,28,FOLLOW_28_in_primary3924); if (failed) return ;
                    match(input,30,FOLLOW_30_in_primary3926); if (failed) return ;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:888:9: 'void' '.' 'class'
                    {
                    match(input,40,FOLLOW_40_in_primary3936); if (failed) return ;
                    match(input,28,FOLLOW_28_in_primary3938); if (failed) return ;
                    match(input,30,FOLLOW_30_in_primary3940); if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 119, primary_StartIndex); }
        }
        return ;
    }
    // $ANTLR end primary


    // $ANTLR start identifierSuffix
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:891:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' ( nonWildcardTypeArguments )? innerCreator );
    public final void identifierSuffix() throws RecognitionException {
        int identifierSuffix_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 120) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:892:2: ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' ( nonWildcardTypeArguments )? innerCreator )
            int alt162=8;
            switch ( input.LA(1) ) {
            case 41:
                {
                int LA162_1 = input.LA(2);

                if ( (LA162_1==42) ) {
                    alt162=1;
                }
                else if ( (LA162_1==Identifier||(LA162_1>=FloatingPointLiteral && LA162_1<=DecimalLiteral)||LA162_1==33||LA162_1==40||(LA162_1>=55 && LA162_1<=62)||(LA162_1>=64 && LA162_1<=65)||(LA162_1>=68 && LA162_1<=70)||(LA162_1>=105 && LA162_1<=106)||(LA162_1>=109 && LA162_1<=114)) ) {
                    alt162=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("891:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' ( nonWildcardTypeArguments )? innerCreator );", 162, 1, input);

                    throw nvae;
                }
                }
                break;
            case 65:
                {
                alt162=3;
                }
                break;
            case 28:
                {
                switch ( input.LA(2) ) {
                case 114:
                    {
                    alt162=8;
                    }
                    break;
                case 113:
                    {
                    alt162=6;
                    }
                    break;
                case 64:
                    {
                    alt162=7;
                    }
                    break;
                case 30:
                    {
                    alt162=4;
                    }
                    break;
                case 33:
                    {
                    alt162=5;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("891:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' ( nonWildcardTypeArguments )? innerCreator );", 162, 3, input);

                    throw nvae;
                }

                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("891:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' ( nonWildcardTypeArguments )? innerCreator );", 162, 0, input);

                throw nvae;
            }

            switch (alt162) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:892:4: ( '[' ']' )+ '.' 'class'
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:892:4: ( '[' ']' )+
                    int cnt159=0;
                    loop159:
                    do {
                        int alt159=2;
                        int LA159_0 = input.LA(1);

                        if ( (LA159_0==41) ) {
                            alt159=1;
                        }


                        switch (alt159) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:892:5: '[' ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_identifierSuffix3952); if (failed) return ;
                    	    match(input,42,FOLLOW_42_in_identifierSuffix3954); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt159 >= 1 ) break loop159;
                    	    if (backtracking>0) {failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(159, input);
                                throw eee;
                        }
                        cnt159++;
                    } while (true);

                    match(input,28,FOLLOW_28_in_identifierSuffix3958); if (failed) return ;
                    match(input,30,FOLLOW_30_in_identifierSuffix3960); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:893:4: ( '[' expression ']' )+
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:893:4: ( '[' expression ']' )+
                    int cnt160=0;
                    loop160:
                    do {
                        int alt160=2;
                        int LA160_0 = input.LA(1);

                        if ( (LA160_0==41) ) {
                            switch ( input.LA(2) ) {
                            case 105:
                                {
                                int LA160_32 = input.LA(3);

                                if ( (synpred248()) ) {
                                    alt160=1;
                                }


                                }
                                break;
                            case 106:
                                {
                                int LA160_33 = input.LA(3);

                                if ( (synpred248()) ) {
                                    alt160=1;
                                }


                                }
                                break;
                            case 109:
                                {
                                int LA160_34 = input.LA(3);

                                if ( (synpred248()) ) {
                                    alt160=1;
                                }


                                }
                                break;
                            case 110:
                                {
                                int LA160_35 = input.LA(3);

                                if ( (synpred248()) ) {
                                    alt160=1;
                                }


                                }
                                break;
                            case 111:
                                {
                                int LA160_36 = input.LA(3);

                                if ( (synpred248()) ) {
                                    alt160=1;
                                }


                                }
                                break;
                            case 112:
                                {
                                int LA160_37 = input.LA(3);

                                if ( (synpred248()) ) {
                                    alt160=1;
                                }


                                }
                                break;
                            case 65:
                                {
                                int LA160_38 = input.LA(3);

                                if ( (synpred248()) ) {
                                    alt160=1;
                                }


                                }
                                break;
                            case 33:
                                {
                                int LA160_39 = input.LA(3);

                                if ( (synpred248()) ) {
                                    alt160=1;
                                }


                                }
                                break;
                            case 113:
                                {
                                int LA160_40 = input.LA(3);

                                if ( (synpred248()) ) {
                                    alt160=1;
                                }


                                }
                                break;
                            case 64:
                                {
                                int LA160_41 = input.LA(3);

                                if ( (synpred248()) ) {
                                    alt160=1;
                                }


                                }
                                break;
                            case HexLiteral:
                            case OctalLiteral:
                            case DecimalLiteral:
                                {
                                int LA160_42 = input.LA(3);

                                if ( (synpred248()) ) {
                                    alt160=1;
                                }


                                }
                                break;
                            case FloatingPointLiteral:
                                {
                                int LA160_43 = input.LA(3);

                                if ( (synpred248()) ) {
                                    alt160=1;
                                }


                                }
                                break;
                            case CharacterLiteral:
                                {
                                int LA160_44 = input.LA(3);

                                if ( (synpred248()) ) {
                                    alt160=1;
                                }


                                }
                                break;
                            case StringLiteral:
                                {
                                int LA160_45 = input.LA(3);

                                if ( (synpred248()) ) {
                                    alt160=1;
                                }


                                }
                                break;
                            case 69:
                            case 70:
                                {
                                int LA160_46 = input.LA(3);

                                if ( (synpred248()) ) {
                                    alt160=1;
                                }


                                }
                                break;
                            case 68:
                                {
                                int LA160_47 = input.LA(3);

                                if ( (synpred248()) ) {
                                    alt160=1;
                                }


                                }
                                break;
                            case 114:
                                {
                                int LA160_48 = input.LA(3);

                                if ( (synpred248()) ) {
                                    alt160=1;
                                }


                                }
                                break;
                            case Identifier:
                                {
                                int LA160_49 = input.LA(3);

                                if ( (synpred248()) ) {
                                    alt160=1;
                                }


                                }
                                break;
                            case 55:
                            case 56:
                            case 57:
                            case 58:
                            case 59:
                            case 60:
                            case 61:
                            case 62:
                                {
                                int LA160_50 = input.LA(3);

                                if ( (synpred248()) ) {
                                    alt160=1;
                                }


                                }
                                break;
                            case 40:
                                {
                                int LA160_51 = input.LA(3);

                                if ( (synpred248()) ) {
                                    alt160=1;
                                }


                                }
                                break;

                            }

                        }


                        switch (alt160) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:893:5: '[' expression ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_identifierSuffix3966); if (failed) return ;
                    	    pushFollow(FOLLOW_expression_in_identifierSuffix3968);
                    	    expression();
                    	    _fsp--;
                    	    if (failed) return ;
                    	    match(input,42,FOLLOW_42_in_identifierSuffix3970); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt160 >= 1 ) break loop160;
                    	    if (backtracking>0) {failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(160, input);
                                throw eee;
                        }
                        cnt160++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:894:9: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_identifierSuffix3983);
                    arguments();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:895:9: '.' 'class'
                    {
                    match(input,28,FOLLOW_28_in_identifierSuffix3993); if (failed) return ;
                    match(input,30,FOLLOW_30_in_identifierSuffix3995); if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:896:9: '.' explicitGenericInvocation
                    {
                    match(input,28,FOLLOW_28_in_identifierSuffix4005); if (failed) return ;
                    pushFollow(FOLLOW_explicitGenericInvocation_in_identifierSuffix4007);
                    explicitGenericInvocation();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:897:9: '.' 'this'
                    {
                    match(input,28,FOLLOW_28_in_identifierSuffix4017); if (failed) return ;
                    match(input,113,FOLLOW_113_in_identifierSuffix4019); if (failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:898:9: '.' 'super' arguments
                    {
                    match(input,28,FOLLOW_28_in_identifierSuffix4029); if (failed) return ;
                    match(input,64,FOLLOW_64_in_identifierSuffix4031); if (failed) return ;
                    pushFollow(FOLLOW_arguments_in_identifierSuffix4033);
                    arguments();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:899:9: '.' 'new' ( nonWildcardTypeArguments )? innerCreator
                    {
                    match(input,28,FOLLOW_28_in_identifierSuffix4043); if (failed) return ;
                    match(input,114,FOLLOW_114_in_identifierSuffix4045); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:899:19: ( nonWildcardTypeArguments )?
                    int alt161=2;
                    int LA161_0 = input.LA(1);

                    if ( (LA161_0==33) ) {
                        alt161=1;
                    }
                    switch (alt161) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:899:20: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_identifierSuffix4048);
                            nonWildcardTypeArguments();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_innerCreator_in_identifierSuffix4052);
                    innerCreator();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 120, identifierSuffix_StartIndex); }
        }
        return ;
    }
    // $ANTLR end identifierSuffix


    // $ANTLR start creator
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:902:1: creator : ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) ;
    public final void creator() throws RecognitionException {
        int creator_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 121) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:903:2: ( ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:903:4: ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:903:4: ( nonWildcardTypeArguments )?
            int alt163=2;
            int LA163_0 = input.LA(1);

            if ( (LA163_0==33) ) {
                alt163=1;
            }
            switch (alt163) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: nonWildcardTypeArguments
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator4064);
                    nonWildcardTypeArguments();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_createdName_in_creator4067);
            createdName();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:904:9: ( arrayCreatorRest | classCreatorRest )
            int alt164=2;
            int LA164_0 = input.LA(1);

            if ( (LA164_0==41) ) {
                alt164=1;
            }
            else if ( (LA164_0==65) ) {
                alt164=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("904:9: ( arrayCreatorRest | classCreatorRest )", 164, 0, input);

                throw nvae;
            }
            switch (alt164) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:904:10: arrayCreatorRest
                    {
                    pushFollow(FOLLOW_arrayCreatorRest_in_creator4078);
                    arrayCreatorRest();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:904:29: classCreatorRest
                    {
                    pushFollow(FOLLOW_classCreatorRest_in_creator4082);
                    classCreatorRest();
                    _fsp--;
                    if (failed) return ;

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
            if ( backtracking>0 ) { memoize(input, 121, creator_StartIndex); }
        }
        return ;
    }
    // $ANTLR end creator


    // $ANTLR start createdName
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:907:1: createdName : ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* | primitiveType );
    public final void createdName() throws RecognitionException {
        int createdName_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 122) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:908:2: ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* | primitiveType )
            int alt168=2;
            int LA168_0 = input.LA(1);

            if ( (LA168_0==Identifier) ) {
                alt168=1;
            }
            else if ( ((LA168_0>=55 && LA168_0<=62)) ) {
                alt168=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("907:1: createdName : ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* | primitiveType );", 168, 0, input);

                throw nvae;
            }
            switch (alt168) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:908:4: Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )*
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_createdName4094); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:908:15: ( typeArguments )?
                    int alt165=2;
                    int LA165_0 = input.LA(1);

                    if ( (LA165_0==33) ) {
                        alt165=1;
                    }
                    switch (alt165) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_createdName4096);
                            typeArguments();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:909:9: ( '.' Identifier ( typeArguments )? )*
                    loop167:
                    do {
                        int alt167=2;
                        int LA167_0 = input.LA(1);

                        if ( (LA167_0==28) ) {
                            alt167=1;
                        }


                        switch (alt167) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:909:10: '.' Identifier ( typeArguments )?
                    	    {
                    	    match(input,28,FOLLOW_28_in_createdName4108); if (failed) return ;
                    	    match(input,Identifier,FOLLOW_Identifier_in_createdName4110); if (failed) return ;
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:909:25: ( typeArguments )?
                    	    int alt166=2;
                    	    int LA166_0 = input.LA(1);

                    	    if ( (LA166_0==33) ) {
                    	        alt166=1;
                    	    }
                    	    switch (alt166) {
                    	        case 1 :
                    	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: typeArguments
                    	            {
                    	            pushFollow(FOLLOW_typeArguments_in_createdName4112);
                    	            typeArguments();
                    	            _fsp--;
                    	            if (failed) return ;

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop167;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:910:7: primitiveType
                    {
                    pushFollow(FOLLOW_primitiveType_in_createdName4123);
                    primitiveType();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 122, createdName_StartIndex); }
        }
        return ;
    }
    // $ANTLR end createdName


    // $ANTLR start innerCreator
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:913:1: innerCreator : Identifier classCreatorRest ;
    public final void innerCreator() throws RecognitionException {
        int innerCreator_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 123) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:914:2: ( Identifier classCreatorRest )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:914:4: Identifier classCreatorRest
            {
            match(input,Identifier,FOLLOW_Identifier_in_innerCreator4135); if (failed) return ;
            pushFollow(FOLLOW_classCreatorRest_in_innerCreator4137);
            classCreatorRest();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 123, innerCreator_StartIndex); }
        }
        return ;
    }
    // $ANTLR end innerCreator


    // $ANTLR start arrayCreatorRest
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:917:1: arrayCreatorRest : '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* ) ;
    public final void arrayCreatorRest() throws RecognitionException {
        int arrayCreatorRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 124) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:918:2: ( '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:918:4: '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* )
            {
            match(input,41,FOLLOW_41_in_arrayCreatorRest4148); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:919:9: ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* )
            int alt172=2;
            int LA172_0 = input.LA(1);

            if ( (LA172_0==42) ) {
                alt172=1;
            }
            else if ( (LA172_0==Identifier||(LA172_0>=FloatingPointLiteral && LA172_0<=DecimalLiteral)||LA172_0==33||LA172_0==40||(LA172_0>=55 && LA172_0<=62)||(LA172_0>=64 && LA172_0<=65)||(LA172_0>=68 && LA172_0<=70)||(LA172_0>=105 && LA172_0<=106)||(LA172_0>=109 && LA172_0<=114)) ) {
                alt172=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("919:9: ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* )", 172, 0, input);

                throw nvae;
            }
            switch (alt172) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:919:13: ']' ( '[' ']' )* arrayInitializer
                    {
                    match(input,42,FOLLOW_42_in_arrayCreatorRest4162); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:919:17: ( '[' ']' )*
                    loop169:
                    do {
                        int alt169=2;
                        int LA169_0 = input.LA(1);

                        if ( (LA169_0==41) ) {
                            alt169=1;
                        }


                        switch (alt169) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:919:18: '[' ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_arrayCreatorRest4165); if (failed) return ;
                    	    match(input,42,FOLLOW_42_in_arrayCreatorRest4167); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop169;
                        }
                    } while (true);

                    pushFollow(FOLLOW_arrayInitializer_in_arrayCreatorRest4171);
                    arrayInitializer();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:920:13: expression ']' ( '[' expression ']' )* ( '[' ']' )*
                    {
                    pushFollow(FOLLOW_expression_in_arrayCreatorRest4185);
                    expression();
                    _fsp--;
                    if (failed) return ;
                    match(input,42,FOLLOW_42_in_arrayCreatorRest4187); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:920:28: ( '[' expression ']' )*
                    loop170:
                    do {
                        int alt170=2;
                        int LA170_0 = input.LA(1);

                        if ( (LA170_0==41) ) {
                            switch ( input.LA(2) ) {
                            case 105:
                                {
                                int LA170_33 = input.LA(3);

                                if ( (synpred264()) ) {
                                    alt170=1;
                                }


                                }
                                break;
                            case 106:
                                {
                                int LA170_34 = input.LA(3);

                                if ( (synpred264()) ) {
                                    alt170=1;
                                }


                                }
                                break;
                            case 109:
                                {
                                int LA170_35 = input.LA(3);

                                if ( (synpred264()) ) {
                                    alt170=1;
                                }


                                }
                                break;
                            case 110:
                                {
                                int LA170_36 = input.LA(3);

                                if ( (synpred264()) ) {
                                    alt170=1;
                                }


                                }
                                break;
                            case 111:
                                {
                                int LA170_37 = input.LA(3);

                                if ( (synpred264()) ) {
                                    alt170=1;
                                }


                                }
                                break;
                            case 112:
                                {
                                int LA170_38 = input.LA(3);

                                if ( (synpred264()) ) {
                                    alt170=1;
                                }


                                }
                                break;
                            case 65:
                                {
                                int LA170_39 = input.LA(3);

                                if ( (synpred264()) ) {
                                    alt170=1;
                                }


                                }
                                break;
                            case 33:
                                {
                                int LA170_40 = input.LA(3);

                                if ( (synpred264()) ) {
                                    alt170=1;
                                }


                                }
                                break;
                            case 113:
                                {
                                int LA170_41 = input.LA(3);

                                if ( (synpred264()) ) {
                                    alt170=1;
                                }


                                }
                                break;
                            case 64:
                                {
                                int LA170_42 = input.LA(3);

                                if ( (synpred264()) ) {
                                    alt170=1;
                                }


                                }
                                break;
                            case HexLiteral:
                            case OctalLiteral:
                            case DecimalLiteral:
                                {
                                int LA170_43 = input.LA(3);

                                if ( (synpred264()) ) {
                                    alt170=1;
                                }


                                }
                                break;
                            case FloatingPointLiteral:
                                {
                                int LA170_44 = input.LA(3);

                                if ( (synpred264()) ) {
                                    alt170=1;
                                }


                                }
                                break;
                            case CharacterLiteral:
                                {
                                int LA170_45 = input.LA(3);

                                if ( (synpred264()) ) {
                                    alt170=1;
                                }


                                }
                                break;
                            case StringLiteral:
                                {
                                int LA170_46 = input.LA(3);

                                if ( (synpred264()) ) {
                                    alt170=1;
                                }


                                }
                                break;
                            case 69:
                            case 70:
                                {
                                int LA170_47 = input.LA(3);

                                if ( (synpred264()) ) {
                                    alt170=1;
                                }


                                }
                                break;
                            case 68:
                                {
                                int LA170_48 = input.LA(3);

                                if ( (synpred264()) ) {
                                    alt170=1;
                                }


                                }
                                break;
                            case 114:
                                {
                                int LA170_49 = input.LA(3);

                                if ( (synpred264()) ) {
                                    alt170=1;
                                }


                                }
                                break;
                            case Identifier:
                                {
                                int LA170_50 = input.LA(3);

                                if ( (synpred264()) ) {
                                    alt170=1;
                                }


                                }
                                break;
                            case 55:
                            case 56:
                            case 57:
                            case 58:
                            case 59:
                            case 60:
                            case 61:
                            case 62:
                                {
                                int LA170_51 = input.LA(3);

                                if ( (synpred264()) ) {
                                    alt170=1;
                                }


                                }
                                break;
                            case 40:
                                {
                                int LA170_52 = input.LA(3);

                                if ( (synpred264()) ) {
                                    alt170=1;
                                }


                                }
                                break;

                            }

                        }


                        switch (alt170) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:920:29: '[' expression ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_arrayCreatorRest4190); if (failed) return ;
                    	    pushFollow(FOLLOW_expression_in_arrayCreatorRest4192);
                    	    expression();
                    	    _fsp--;
                    	    if (failed) return ;
                    	    match(input,42,FOLLOW_42_in_arrayCreatorRest4194); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop170;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:920:50: ( '[' ']' )*
                    loop171:
                    do {
                        int alt171=2;
                        int LA171_0 = input.LA(1);

                        if ( (LA171_0==41) ) {
                            int LA171_30 = input.LA(2);

                            if ( (LA171_30==42) ) {
                                alt171=1;
                            }


                        }


                        switch (alt171) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:920:51: '[' ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_arrayCreatorRest4199); if (failed) return ;
                    	    match(input,42,FOLLOW_42_in_arrayCreatorRest4201); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop171;
                        }
                    } while (true);


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
            if ( backtracking>0 ) { memoize(input, 124, arrayCreatorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end arrayCreatorRest


    // $ANTLR start classCreatorRest
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:924:1: classCreatorRest : arguments ( classBody )? ;
    public final void classCreatorRest() throws RecognitionException {
        int classCreatorRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 125) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:925:2: ( arguments ( classBody )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:925:4: arguments ( classBody )?
            {
            pushFollow(FOLLOW_arguments_in_classCreatorRest4224);
            arguments();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:925:14: ( classBody )?
            int alt173=2;
            int LA173_0 = input.LA(1);

            if ( (LA173_0==37) ) {
                alt173=1;
            }
            switch (alt173) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: classBody
                    {
                    pushFollow(FOLLOW_classBody_in_classCreatorRest4226);
                    classBody();
                    _fsp--;
                    if (failed) return ;

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
            if ( backtracking>0 ) { memoize(input, 125, classCreatorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end classCreatorRest


    // $ANTLR start explicitGenericInvocation
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:928:1: explicitGenericInvocation : nonWildcardTypeArguments explicitGenericInvocationSuffix ;
    public final void explicitGenericInvocation() throws RecognitionException {
        int explicitGenericInvocation_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 126) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:929:2: ( nonWildcardTypeArguments explicitGenericInvocationSuffix )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:929:4: nonWildcardTypeArguments explicitGenericInvocationSuffix
            {
            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation4239);
            nonWildcardTypeArguments();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_explicitGenericInvocationSuffix_in_explicitGenericInvocation4241);
            explicitGenericInvocationSuffix();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 126, explicitGenericInvocation_StartIndex); }
        }
        return ;
    }
    // $ANTLR end explicitGenericInvocation


    // $ANTLR start nonWildcardTypeArguments
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:932:1: nonWildcardTypeArguments : '<' typeList '>' ;
    public final void nonWildcardTypeArguments() throws RecognitionException {
        int nonWildcardTypeArguments_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 127) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:933:2: ( '<' typeList '>' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:933:4: '<' typeList '>'
            {
            match(input,33,FOLLOW_33_in_nonWildcardTypeArguments4253); if (failed) return ;
            pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments4255);
            typeList();
            _fsp--;
            if (failed) return ;
            match(input,35,FOLLOW_35_in_nonWildcardTypeArguments4257); if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 127, nonWildcardTypeArguments_StartIndex); }
        }
        return ;
    }
    // $ANTLR end nonWildcardTypeArguments


    // $ANTLR start explicitGenericInvocationSuffix
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:936:1: explicitGenericInvocationSuffix : ( 'super' superSuffix | Identifier arguments );
    public final void explicitGenericInvocationSuffix() throws RecognitionException {
        int explicitGenericInvocationSuffix_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 128) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:937:2: ( 'super' superSuffix | Identifier arguments )
            int alt174=2;
            int LA174_0 = input.LA(1);

            if ( (LA174_0==64) ) {
                alt174=1;
            }
            else if ( (LA174_0==Identifier) ) {
                alt174=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("936:1: explicitGenericInvocationSuffix : ( 'super' superSuffix | Identifier arguments );", 174, 0, input);

                throw nvae;
            }
            switch (alt174) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:937:4: 'super' superSuffix
                    {
                    match(input,64,FOLLOW_64_in_explicitGenericInvocationSuffix4269); if (failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_explicitGenericInvocationSuffix4271);
                    superSuffix();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:938:6: Identifier arguments
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_explicitGenericInvocationSuffix4278); if (failed) return ;
                    pushFollow(FOLLOW_arguments_in_explicitGenericInvocationSuffix4280);
                    arguments();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 128, explicitGenericInvocationSuffix_StartIndex); }
        }
        return ;
    }
    // $ANTLR end explicitGenericInvocationSuffix


    // $ANTLR start selector
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:941:1: selector : ( '.' Identifier ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '.' 'new' ( nonWildcardTypeArguments )? innerCreator | '[' expression ']' );
    public final void selector() throws RecognitionException {
        int selector_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 129) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:942:2: ( '.' Identifier ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '.' 'new' ( nonWildcardTypeArguments )? innerCreator | '[' expression ']' )
            int alt177=5;
            int LA177_0 = input.LA(1);

            if ( (LA177_0==28) ) {
                switch ( input.LA(2) ) {
                case Identifier:
                    {
                    alt177=1;
                    }
                    break;
                case 113:
                    {
                    alt177=2;
                    }
                    break;
                case 114:
                    {
                    alt177=4;
                    }
                    break;
                case 64:
                    {
                    alt177=3;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("941:1: selector : ( '.' Identifier ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '.' 'new' ( nonWildcardTypeArguments )? innerCreator | '[' expression ']' );", 177, 1, input);

                    throw nvae;
                }

            }
            else if ( (LA177_0==41) ) {
                alt177=5;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("941:1: selector : ( '.' Identifier ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '.' 'new' ( nonWildcardTypeArguments )? innerCreator | '[' expression ']' );", 177, 0, input);

                throw nvae;
            }
            switch (alt177) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:942:4: '.' Identifier ( arguments )?
                    {
                    match(input,28,FOLLOW_28_in_selector4292); if (failed) return ;
                    match(input,Identifier,FOLLOW_Identifier_in_selector4294); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:942:19: ( arguments )?
                    int alt175=2;
                    int LA175_0 = input.LA(1);

                    if ( (LA175_0==65) ) {
                        alt175=1;
                    }
                    switch (alt175) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:942:20: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_selector4297);
                            arguments();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:943:6: '.' 'this'
                    {
                    match(input,28,FOLLOW_28_in_selector4306); if (failed) return ;
                    match(input,113,FOLLOW_113_in_selector4308); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:944:6: '.' 'super' superSuffix
                    {
                    match(input,28,FOLLOW_28_in_selector4315); if (failed) return ;
                    match(input,64,FOLLOW_64_in_selector4317); if (failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_selector4319);
                    superSuffix();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:945:6: '.' 'new' ( nonWildcardTypeArguments )? innerCreator
                    {
                    match(input,28,FOLLOW_28_in_selector4326); if (failed) return ;
                    match(input,114,FOLLOW_114_in_selector4328); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:945:16: ( nonWildcardTypeArguments )?
                    int alt176=2;
                    int LA176_0 = input.LA(1);

                    if ( (LA176_0==33) ) {
                        alt176=1;
                    }
                    switch (alt176) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:945:17: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_selector4331);
                            nonWildcardTypeArguments();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_innerCreator_in_selector4335);
                    innerCreator();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:946:6: '[' expression ']'
                    {
                    match(input,41,FOLLOW_41_in_selector4342); if (failed) return ;
                    pushFollow(FOLLOW_expression_in_selector4344);
                    expression();
                    _fsp--;
                    if (failed) return ;
                    match(input,42,FOLLOW_42_in_selector4346); if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 129, selector_StartIndex); }
        }
        return ;
    }
    // $ANTLR end selector


    // $ANTLR start superSuffix
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:949:1: superSuffix : ( arguments | '.' Identifier ( arguments )? );
    public final void superSuffix() throws RecognitionException {
        int superSuffix_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 130) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:950:2: ( arguments | '.' Identifier ( arguments )? )
            int alt179=2;
            int LA179_0 = input.LA(1);

            if ( (LA179_0==65) ) {
                alt179=1;
            }
            else if ( (LA179_0==28) ) {
                alt179=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("949:1: superSuffix : ( arguments | '.' Identifier ( arguments )? );", 179, 0, input);

                throw nvae;
            }
            switch (alt179) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:950:4: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_superSuffix4358);
                    arguments();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:951:6: '.' Identifier ( arguments )?
                    {
                    match(input,28,FOLLOW_28_in_superSuffix4365); if (failed) return ;
                    match(input,Identifier,FOLLOW_Identifier_in_superSuffix4367); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:951:21: ( arguments )?
                    int alt178=2;
                    int LA178_0 = input.LA(1);

                    if ( (LA178_0==65) ) {
                        alt178=1;
                    }
                    switch (alt178) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:951:22: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_superSuffix4370);
                            arguments();
                            _fsp--;
                            if (failed) return ;

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
            if ( backtracking>0 ) { memoize(input, 130, superSuffix_StartIndex); }
        }
        return ;
    }
    // $ANTLR end superSuffix


    // $ANTLR start arguments
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:954:1: arguments : '(' ( expressionList )? ')' ;
    public final void arguments() throws RecognitionException {
        int arguments_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 131) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:955:2: ( '(' ( expressionList )? ')' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:955:4: '(' ( expressionList )? ')'
            {
            match(input,65,FOLLOW_65_in_arguments4386); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:955:8: ( expressionList )?
            int alt180=2;
            int LA180_0 = input.LA(1);

            if ( (LA180_0==Identifier||(LA180_0>=FloatingPointLiteral && LA180_0<=DecimalLiteral)||LA180_0==33||LA180_0==40||(LA180_0>=55 && LA180_0<=62)||(LA180_0>=64 && LA180_0<=65)||(LA180_0>=68 && LA180_0<=70)||(LA180_0>=105 && LA180_0<=106)||(LA180_0>=109 && LA180_0<=114)) ) {
                alt180=1;
            }
            switch (alt180) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments4388);
                    expressionList();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            match(input,66,FOLLOW_66_in_arguments4391); if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 131, arguments_StartIndex); }
        }
        return ;
    }
    // $ANTLR end arguments

    // $ANTLR start synpred1
    public final void synpred1_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:207:4: ( annotations )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:207:4: annotations
        {
        pushFollow(FOLLOW_annotations_in_synpred170);
        annotations();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred1

    // $ANTLR start synpred38
    public final void synpred38_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:303:4: ( methodDeclaration )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:303:4: methodDeclaration
        {
        pushFollow(FOLLOW_methodDeclaration_in_synpred38577);
        methodDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred38

    // $ANTLR start synpred39
    public final void synpred39_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:304:4: ( fieldDeclaration )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:304:4: fieldDeclaration
        {
        pushFollow(FOLLOW_fieldDeclaration_in_synpred39582);
        fieldDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred39

    // $ANTLR start synpred85
    public final void synpred85_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:459:16: ( '.' Identifier )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:459:16: '.' Identifier
        {
        match(input,28,FOLLOW_28_in_synpred851389); if (failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred851391); if (failed) return ;

        }
    }
    // $ANTLR end synpred85

    // $ANTLR start synpred120
    public final void synpred120_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:549:4: ( annotation )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:549:4: annotation
        {
        pushFollow(FOLLOW_annotation_in_synpred1201893);
        annotation();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred120

    // $ANTLR start synpred135
    public final void synpred135_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:596:6: ( classDeclaration ( ';' )? )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:596:6: classDeclaration ( ';' )?
        {
        pushFollow(FOLLOW_classDeclaration_in_synpred1352123);
        classDeclaration();
        _fsp--;
        if (failed) return ;
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:596:23: ( ';' )?
        int alt196=2;
        int LA196_0 = input.LA(1);

        if ( (LA196_0==25) ) {
            alt196=1;
        }
        switch (alt196) {
            case 1 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ';'
                {
                match(input,25,FOLLOW_25_in_synpred1352125); if (failed) return ;

                }
                break;

        }


        }
    }
    // $ANTLR end synpred135

    // $ANTLR start synpred137
    public final void synpred137_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:597:6: ( interfaceDeclaration ( ';' )? )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:597:6: interfaceDeclaration ( ';' )?
        {
        pushFollow(FOLLOW_interfaceDeclaration_in_synpred1372133);
        interfaceDeclaration();
        _fsp--;
        if (failed) return ;
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:597:27: ( ';' )?
        int alt197=2;
        int LA197_0 = input.LA(1);

        if ( (LA197_0==25) ) {
            alt197=1;
        }
        switch (alt197) {
            case 1 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ';'
                {
                match(input,25,FOLLOW_25_in_synpred1372135); if (failed) return ;

                }
                break;

        }


        }
    }
    // $ANTLR end synpred137

    // $ANTLR start synpred139
    public final void synpred139_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:598:6: ( enumDeclaration ( ';' )? )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:598:6: enumDeclaration ( ';' )?
        {
        pushFollow(FOLLOW_enumDeclaration_in_synpred1392143);
        enumDeclaration();
        _fsp--;
        if (failed) return ;
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:598:22: ( ';' )?
        int alt198=2;
        int LA198_0 = input.LA(1);

        if ( (LA198_0==25) ) {
            alt198=1;
        }
        switch (alt198) {
            case 1 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ';'
                {
                match(input,25,FOLLOW_25_in_synpred1392145); if (failed) return ;

                }
                break;

        }


        }
    }
    // $ANTLR end synpred139

    // $ANTLR start synpred144
    public final void synpred144_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:632:4: ( localVariableDeclaration )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:632:4: localVariableDeclaration
        {
        pushFollow(FOLLOW_localVariableDeclaration_in_synpred1442286);
        localVariableDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred144

    // $ANTLR start synpred145
    public final void synpred145_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:633:4: ( classOrInterfaceDeclaration )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:633:4: classOrInterfaceDeclaration
        {
        pushFollow(FOLLOW_classOrInterfaceDeclaration_in_synpred1452291);
        classOrInterfaceDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred145

    // $ANTLR start synpred150
    public final void synpred150_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:666:52: ( 'else' statement )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:666:52: 'else' statement
        {
        match(input,76,FOLLOW_76_in_synpred1502431); if (failed) return ;
        pushFollow(FOLLOW_statement_in_synpred1502433);
        statement();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred150

    // $ANTLR start synpred155
    public final void synpred155_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:671:9: ( catches 'finally' block )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:671:9: catches 'finally' block
        {
        pushFollow(FOLLOW_catches_in_synpred1552499);
        catches();
        _fsp--;
        if (failed) return ;
        match(input,81,FOLLOW_81_in_synpred1552501); if (failed) return ;
        pushFollow(FOLLOW_block_in_synpred1552503);
        block();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred155

    // $ANTLR start synpred156
    public final void synpred156_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:672:9: ( catches )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:672:9: catches
        {
        pushFollow(FOLLOW_catches_in_synpred1562513);
        catches();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred156

    // $ANTLR start synpred176
    public final void synpred176_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:729:4: ( 'case' constantExpression ':' )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:729:4: 'case' constantExpression ':'
        {
        match(input,89,FOLLOW_89_in_synpred1762836); if (failed) return ;
        pushFollow(FOLLOW_constantExpression_in_synpred1762838);
        constantExpression();
        _fsp--;
        if (failed) return ;
        match(input,74,FOLLOW_74_in_synpred1762840); if (failed) return ;

        }
    }
    // $ANTLR end synpred176

    // $ANTLR start synpred177
    public final void synpred177_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:730:6: ( 'case' enumConstantName ':' )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:730:6: 'case' enumConstantName ':'
        {
        match(input,89,FOLLOW_89_in_synpred1772847); if (failed) return ;
        pushFollow(FOLLOW_enumConstantName_in_synpred1772849);
        enumConstantName();
        _fsp--;
        if (failed) return ;
        match(input,74,FOLLOW_74_in_synpred1772851); if (failed) return ;

        }
    }
    // $ANTLR end synpred177

    // $ANTLR start synpred179
    public final void synpred179_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:740:4: ( forVarControl )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:740:4: forVarControl
        {
        pushFollow(FOLLOW_forVarControl_in_synpred1792896);
        forVarControl();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred179

    // $ANTLR start synpred184
    public final void synpred184_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:751:4: ( ( variableModifier )* type variableDeclarators )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:751:4: ( variableModifier )* type variableDeclarators
        {
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:751:4: ( variableModifier )*
        loop206:
        do {
            int alt206=2;
            int LA206_0 = input.LA(1);

            if ( (LA206_0==49||LA206_0==71) ) {
                alt206=1;
            }


            switch (alt206) {
        	case 1 :
        	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: variableModifier
        	    {
        	    pushFollow(FOLLOW_variableModifier_in_synpred1842949);
        	    variableModifier();
        	    _fsp--;
        	    if (failed) return ;

        	    }
        	    break;

        	default :
        	    break loop206;
            }
        } while (true);

        pushFollow(FOLLOW_type_in_synpred1842952);
        type();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_variableDeclarators_in_synpred1842954);
        variableDeclarators();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred184

    // $ANTLR start synpred187
    public final void synpred187_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:782:27: ( assignmentOperator expression )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:782:27: assignmentOperator expression
        {
        pushFollow(FOLLOW_assignmentOperator_in_synpred1873073);
        assignmentOperator();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_expression_in_synpred1873075);
        expression();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred187

    // $ANTLR start synpred198
    public final void synpred198_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:796:9: ( '>' '>' '=' )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:796:9: '>' '>' '='
        {
        match(input,35,FOLLOW_35_in_synpred1983193); if (failed) return ;
        match(input,35,FOLLOW_35_in_synpred1983195); if (failed) return ;
        match(input,44,FOLLOW_44_in_synpred1983197); if (failed) return ;

        }
    }
    // $ANTLR end synpred198

    // $ANTLR start synpred208
    public final void synpred208_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:833:27: ( relationalOp shiftExpression )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:833:27: relationalOp shiftExpression
        {
        pushFollow(FOLLOW_relationalOp_in_synpred2083441);
        relationalOp();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_shiftExpression_in_synpred2083443);
        shiftExpression();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred208

    // $ANTLR start synpred212
    public final void synpred212_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:841:30: ( shiftOp additiveExpression )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:841:30: shiftOp additiveExpression
        {
        pushFollow(FOLLOW_shiftOp_in_synpred2123496);
        shiftOp();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_additiveExpression_in_synpred2123498);
        additiveExpression();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred212

    // $ANTLR start synpred214
    public final void synpred214_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:846:15: ( '>' '>' '>' )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:846:15: '>' '>' '>'
        {
        match(input,35,FOLLOW_35_in_synpred2143528); if (failed) return ;
        match(input,35,FOLLOW_35_in_synpred2143530); if (failed) return ;
        match(input,35,FOLLOW_35_in_synpred2143532); if (failed) return ;

        }
    }
    // $ANTLR end synpred214

    // $ANTLR start synpred226
    public final void synpred226_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:869:9: ( castExpression )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:869:9: castExpression
        {
        pushFollow(FOLLOW_castExpression_in_synpred2263713);
        castExpression();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred226

    // $ANTLR start synpred230
    public final void synpred230_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:874:8: ( '(' primitiveType ')' unaryExpression )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:874:8: '(' primitiveType ')' unaryExpression
        {
        match(input,65,FOLLOW_65_in_synpred2303751); if (failed) return ;
        pushFollow(FOLLOW_primitiveType_in_synpred2303753);
        primitiveType();
        _fsp--;
        if (failed) return ;
        match(input,66,FOLLOW_66_in_synpred2303755); if (failed) return ;
        pushFollow(FOLLOW_unaryExpression_in_synpred2303757);
        unaryExpression();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred230

    // $ANTLR start synpred231
    public final void synpred231_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:875:13: ( type )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:875:13: type
        {
        pushFollow(FOLLOW_type_in_synpred2313769);
        type();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred231

    // $ANTLR start synpred235
    public final void synpred235_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:882:17: ( '.' Identifier )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:882:17: '.' Identifier
        {
        match(input,28,FOLLOW_28_in_synpred2353836); if (failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred2353838); if (failed) return ;

        }
    }
    // $ANTLR end synpred235

    // $ANTLR start synpred236
    public final void synpred236_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:882:35: ( identifierSuffix )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:882:35: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred2363843);
        identifierSuffix();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred236

    // $ANTLR start synpred241
    public final void synpred241_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:886:127: ( '.' Identifier )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:886:127: '.' Identifier
        {
        match(input,28,FOLLOW_28_in_synpred2413896); if (failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred2413898); if (failed) return ;

        }
    }
    // $ANTLR end synpred241

    // $ANTLR start synpred242
    public final void synpred242_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:886:145: ( identifierSuffix )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:886:145: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred2423903);
        identifierSuffix();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred242

    // $ANTLR start synpred248
    public final void synpred248_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:893:5: ( '[' expression ']' )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:893:5: '[' expression ']'
        {
        match(input,41,FOLLOW_41_in_synpred2483966); if (failed) return ;
        pushFollow(FOLLOW_expression_in_synpred2483968);
        expression();
        _fsp--;
        if (failed) return ;
        match(input,42,FOLLOW_42_in_synpred2483970); if (failed) return ;

        }
    }
    // $ANTLR end synpred248

    // $ANTLR start synpred264
    public final void synpred264_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:920:29: ( '[' expression ']' )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:920:29: '[' expression ']'
        {
        match(input,41,FOLLOW_41_in_synpred2644190); if (failed) return ;
        pushFollow(FOLLOW_expression_in_synpred2644192);
        expression();
        _fsp--;
        if (failed) return ;
        match(input,42,FOLLOW_42_in_synpred2644194); if (failed) return ;

        }
    }
    // $ANTLR end synpred264

    public final boolean synpred139() {
        backtracking++;
        int start = input.mark();
        try {
            synpred139_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred242() {
        backtracking++;
        int start = input.mark();
        try {
            synpred242_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred156() {
        backtracking++;
        int start = input.mark();
        try {
            synpred156_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred120() {
        backtracking++;
        int start = input.mark();
        try {
            synpred120_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred264() {
        backtracking++;
        int start = input.mark();
        try {
            synpred264_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred38() {
        backtracking++;
        int start = input.mark();
        try {
            synpred38_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred184() {
        backtracking++;
        int start = input.mark();
        try {
            synpred184_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred155() {
        backtracking++;
        int start = input.mark();
        try {
            synpred155_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred235() {
        backtracking++;
        int start = input.mark();
        try {
            synpred235_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred85() {
        backtracking++;
        int start = input.mark();
        try {
            synpred85_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred39() {
        backtracking++;
        int start = input.mark();
        try {
            synpred39_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred1() {
        backtracking++;
        int start = input.mark();
        try {
            synpred1_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred177() {
        backtracking++;
        int start = input.mark();
        try {
            synpred177_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred212() {
        backtracking++;
        int start = input.mark();
        try {
            synpred212_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred198() {
        backtracking++;
        int start = input.mark();
        try {
            synpred198_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred145() {
        backtracking++;
        int start = input.mark();
        try {
            synpred145_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred176() {
        backtracking++;
        int start = input.mark();
        try {
            synpred176_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred208() {
        backtracking++;
        int start = input.mark();
        try {
            synpred208_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred241() {
        backtracking++;
        int start = input.mark();
        try {
            synpred241_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred187() {
        backtracking++;
        int start = input.mark();
        try {
            synpred187_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred135() {
        backtracking++;
        int start = input.mark();
        try {
            synpred135_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred231() {
        backtracking++;
        int start = input.mark();
        try {
            synpred231_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred236() {
        backtracking++;
        int start = input.mark();
        try {
            synpred236_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred214() {
        backtracking++;
        int start = input.mark();
        try {
            synpred214_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred179() {
        backtracking++;
        int start = input.mark();
        try {
            synpred179_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred150() {
        backtracking++;
        int start = input.mark();
        try {
            synpred150_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred137() {
        backtracking++;
        int start = input.mark();
        try {
            synpred137_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred248() {
        backtracking++;
        int start = input.mark();
        try {
            synpred248_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred144() {
        backtracking++;
        int start = input.mark();
        try {
            synpred144_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred226() {
        backtracking++;
        int start = input.mark();
        try {
            synpred226_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred230() {
        backtracking++;
        int start = input.mark();
        try {
            synpred230_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }


 

    public static final BitSet FOLLOW_annotations_in_compilationUnit70 = new BitSet(new long[]{0x007FE0804F000022L,0x0000000000000080L});
    public static final BitSet FOLLOW_packageDeclaration_in_compilationUnit75 = new BitSet(new long[]{0x007FE0804E000022L,0x0000000000000080L});
    public static final BitSet FOLLOW_importDeclaration_in_compilationUnit86 = new BitSet(new long[]{0x007FE0804E000022L,0x0000000000000080L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit97 = new BitSet(new long[]{0x007FE0804A000022L,0x0000000000000080L});
    public static final BitSet FOLLOW_24_in_packageDeclaration109 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_packageDeclaration111 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_packageDeclaration113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_importDeclaration125 = new BitSet(new long[]{0x0000000008000010L});
    public static final BitSet FOLLOW_27_in_importDeclaration127 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_importDeclaration130 = new BitSet(new long[]{0x0000000012000000L});
    public static final BitSet FOLLOW_28_in_importDeclaration133 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_importDeclaration135 = new BitSet(new long[]{0x0000000012000000L});
    public static final BitSet FOLLOW_28_in_importDeclaration140 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_importDeclaration142 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_importDeclaration146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration158 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_typeDeclaration168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifier_in_classOrInterfaceDeclaration180 = new BitSet(new long[]{0x007FE08048000020L,0x0000000000000080L});
    public static final BitSet FOLLOW_classDeclaration_in_classOrInterfaceDeclaration184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration188 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_classDeclaration201 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_classDeclaration211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_normalClassDeclaration223 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_normalClassDeclaration225 = new BitSet(new long[]{0x0000002380000000L});
    public static final BitSet FOLLOW_typeParameters_in_normalClassDeclaration228 = new BitSet(new long[]{0x0000002180000000L});
    public static final BitSet FOLLOW_31_in_normalClassDeclaration241 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_type_in_normalClassDeclaration243 = new BitSet(new long[]{0x0000002100000000L});
    public static final BitSet FOLLOW_32_in_normalClassDeclaration256 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_typeList_in_normalClassDeclaration258 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_classBody_in_normalClassDeclaration270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_typeParameters282 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_typeParameter_in_typeParameters284 = new BitSet(new long[]{0x0000000C00000000L});
    public static final BitSet FOLLOW_34_in_typeParameters287 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_typeParameter_in_typeParameters289 = new BitSet(new long[]{0x0000000C00000000L});
    public static final BitSet FOLLOW_35_in_typeParameters293 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_typeParameter304 = new BitSet(new long[]{0x0000000080000002L});
    public static final BitSet FOLLOW_31_in_typeParameter307 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_bound_in_typeParameter309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_bound324 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_36_in_bound327 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_type_in_bound329 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_ENUM_in_enumDeclaration342 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_enumDeclaration344 = new BitSet(new long[]{0x0000002100000000L});
    public static final BitSet FOLLOW_32_in_enumDeclaration347 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_typeList_in_enumDeclaration349 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_enumBody_in_enumDeclaration353 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_enumBody365 = new BitSet(new long[]{0x0000004402000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_enumConstants_in_enumBody367 = new BitSet(new long[]{0x0000004402000000L});
    public static final BitSet FOLLOW_34_in_enumBody370 = new BitSet(new long[]{0x0000004002000000L});
    public static final BitSet FOLLOW_enumBodyDeclarations_in_enumBody373 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_enumBody376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumConstant_in_enumConstants387 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_enumConstants390 = new BitSet(new long[]{0x0000000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_enumConstant_in_enumConstants392 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_annotations_in_enumConstant406 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_enumConstant409 = new BitSet(new long[]{0x0000002000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_enumConstant412 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_classBody_in_enumConstant417 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_enumBodyDeclarations431 = new BitSet(new long[]{0x7FFFE1A24A000032L,0x0000000000000080L});
    public static final BitSet FOLLOW_classBodyDeclaration_in_enumBodyDeclarations434 = new BitSet(new long[]{0x7FFFE1A24A000032L,0x0000000000000080L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration448 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_normalInterfaceDeclaration466 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_normalInterfaceDeclaration468 = new BitSet(new long[]{0x0000002280000000L});
    public static final BitSet FOLLOW_typeParameters_in_normalInterfaceDeclaration470 = new BitSet(new long[]{0x0000002080000000L});
    public static final BitSet FOLLOW_31_in_normalInterfaceDeclaration474 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_typeList_in_normalInterfaceDeclaration476 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_interfaceBody_in_normalInterfaceDeclaration480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeList492 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_typeList495 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_type_in_typeList497 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_37_in_classBody511 = new BitSet(new long[]{0x7FFFE1E24A000030L,0x0000000000000080L});
    public static final BitSet FOLLOW_classBodyDeclaration_in_classBody513 = new BitSet(new long[]{0x7FFFE1E24A000030L,0x0000000000000080L});
    public static final BitSet FOLLOW_38_in_classBody516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_interfaceBody528 = new BitSet(new long[]{0x7FFFE1C24A000030L,0x0000000000000080L});
    public static final BitSet FOLLOW_interfaceBodyDeclaration_in_interfaceBody530 = new BitSet(new long[]{0x7FFFE1C24A000030L,0x0000000000000080L});
    public static final BitSet FOLLOW_38_in_interfaceBody533 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_classBodyDeclaration544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_classBodyDeclaration549 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_block_in_classBodyDeclaration552 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifier_in_classBodyDeclaration557 = new BitSet(new long[]{0x7FFFE18248000030L,0x0000000000000080L});
    public static final BitSet FOLLOW_memberDecl_in_classBodyDeclaration560 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_genericMethodOrConstructorDecl_in_memberDecl572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodDeclaration_in_memberDecl577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldDeclaration_in_memberDecl582 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_memberDecl587 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_memberDecl589 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_voidMethodDeclaratorRest_in_memberDecl591 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_memberDecl596 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_constructorDeclaratorRest_in_memberDecl598 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_memberDecl603 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_memberDecl608 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeParameters_in_genericMethodOrConstructorDecl620 = new BitSet(new long[]{0x7F80010000000010L});
    public static final BitSet FOLLOW_genericMethodOrConstructorRest_in_genericMethodOrConstructorDecl622 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_genericMethodOrConstructorRest635 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_40_in_genericMethodOrConstructorRest639 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_genericMethodOrConstructorRest642 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_methodDeclaratorRest_in_genericMethodOrConstructorRest644 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_genericMethodOrConstructorRest649 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_constructorDeclaratorRest_in_genericMethodOrConstructorRest651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_methodDeclaration662 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_methodDeclaration664 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_methodDeclaratorRest_in_methodDeclaration666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_fieldDeclaration677 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarators_in_fieldDeclaration679 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_fieldDeclaration681 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifier_in_interfaceBodyDeclaration694 = new BitSet(new long[]{0x7FFFE18248000030L,0x0000000000000080L});
    public static final BitSet FOLLOW_interfaceMemberDecl_in_interfaceBodyDeclaration697 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_interfaceBodyDeclaration704 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodOrFieldDecl_in_interfaceMemberDecl715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceGenericMethodDecl_in_interfaceMemberDecl722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_interfaceMemberDecl732 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_interfaceMemberDecl734 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_voidInterfaceMethodDeclaratorRest_in_interfaceMemberDecl736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_interfaceMemberDecl746 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_interfaceMemberDecl756 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_interfaceMethodOrFieldDecl768 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_interfaceMethodOrFieldDecl770 = new BitSet(new long[]{0x0000120000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodOrFieldRest_in_interfaceMethodOrFieldDecl772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constantDeclaratorsRest_in_interfaceMethodOrFieldRest784 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_interfaceMethodOrFieldRest786 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodDeclaratorRest_in_interfaceMethodOrFieldRest791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_methodDeclaratorRest803 = new BitSet(new long[]{0x00000A2002000000L});
    public static final BitSet FOLLOW_41_in_methodDeclaratorRest806 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_methodDeclaratorRest808 = new BitSet(new long[]{0x00000A2002000000L});
    public static final BitSet FOLLOW_43_in_methodDeclaratorRest821 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_methodDeclaratorRest823 = new BitSet(new long[]{0x0000002002000000L});
    public static final BitSet FOLLOW_methodBody_in_methodDeclaratorRest839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_methodDeclaratorRest853 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_voidMethodDeclaratorRest875 = new BitSet(new long[]{0x0000082002000000L});
    public static final BitSet FOLLOW_43_in_voidMethodDeclaratorRest878 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_voidMethodDeclaratorRest880 = new BitSet(new long[]{0x0000002002000000L});
    public static final BitSet FOLLOW_methodBody_in_voidMethodDeclaratorRest896 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_voidMethodDeclaratorRest910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_interfaceMethodDeclaratorRest932 = new BitSet(new long[]{0x00000A0002000000L});
    public static final BitSet FOLLOW_41_in_interfaceMethodDeclaratorRest935 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_interfaceMethodDeclaratorRest937 = new BitSet(new long[]{0x00000A0002000000L});
    public static final BitSet FOLLOW_43_in_interfaceMethodDeclaratorRest942 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_interfaceMethodDeclaratorRest944 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_interfaceMethodDeclaratorRest948 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeParameters_in_interfaceGenericMethodDecl960 = new BitSet(new long[]{0x7F80010000000010L});
    public static final BitSet FOLLOW_type_in_interfaceGenericMethodDecl963 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_40_in_interfaceGenericMethodDecl967 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_interfaceGenericMethodDecl970 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodDeclaratorRest_in_interfaceGenericMethodDecl980 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_voidInterfaceMethodDeclaratorRest992 = new BitSet(new long[]{0x0000080002000000L});
    public static final BitSet FOLLOW_43_in_voidInterfaceMethodDeclaratorRest995 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_voidInterfaceMethodDeclaratorRest997 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_voidInterfaceMethodDeclaratorRest1001 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_constructorDeclaratorRest1013 = new BitSet(new long[]{0x0000082000000000L});
    public static final BitSet FOLLOW_43_in_constructorDeclaratorRest1016 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_constructorDeclaratorRest1018 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_methodBody_in_constructorDeclaratorRest1022 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_constantDeclarator1033 = new BitSet(new long[]{0x0000120000000000L});
    public static final BitSet FOLLOW_constantDeclaratorRest_in_constantDeclarator1035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclarator_in_variableDeclarators1047 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_variableDeclarators1050 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarator_in_variableDeclarators1052 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_Identifier_in_variableDeclarator1084 = new BitSet(new long[]{0x0000120000000002L});
    public static final BitSet FOLLOW_variableDeclaratorRest_in_variableDeclarator1088 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_variableDeclaratorRest1106 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_variableDeclaratorRest1108 = new BitSet(new long[]{0x0000120000000002L});
    public static final BitSet FOLLOW_44_in_variableDeclaratorRest1113 = new BitSet(new long[]{0x7F80012200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_variableInitializer_in_variableDeclaratorRest1115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_variableDeclaratorRest1122 = new BitSet(new long[]{0x7F80012200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_variableInitializer_in_variableDeclaratorRest1124 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constantDeclaratorRest_in_constantDeclaratorsRest1144 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_constantDeclaratorsRest1147 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_constantDeclarator_in_constantDeclaratorsRest1149 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_41_in_constantDeclaratorRest1166 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_constantDeclaratorRest1168 = new BitSet(new long[]{0x0000120000000000L});
    public static final BitSet FOLLOW_44_in_constantDeclaratorRest1172 = new BitSet(new long[]{0x7F80012200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_variableInitializer_in_constantDeclaratorRest1174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_variableDeclaratorId1186 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_variableDeclaratorId1189 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_variableDeclaratorId1191 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer1204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer1214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_arrayInitializer1226 = new BitSet(new long[]{0x7F80016200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer1229 = new BitSet(new long[]{0x0000004400000000L});
    public static final BitSet FOLLOW_34_in_arrayInitializer1232 = new BitSet(new long[]{0x7F80012200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer1234 = new BitSet(new long[]{0x0000004400000000L});
    public static final BitSet FOLLOW_34_in_arrayInitializer1239 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_arrayInitializer1246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_modifier1262 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_modifier1272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_modifier1282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_modifier1292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_modifier1302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_modifier1312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_modifier1322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_modifier1332 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_modifier1342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_modifier1352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_modifier1362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_modifier1372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_packageOrTypeName1386 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_28_in_packageOrTypeName1389 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_packageOrTypeName1391 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_Identifier_in_enumConstantName1409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_typeName1425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_packageOrTypeName_in_typeName1435 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_typeName1437 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_typeName1439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_type1450 = new BitSet(new long[]{0x0000020210000002L});
    public static final BitSet FOLLOW_typeArguments_in_type1453 = new BitSet(new long[]{0x0000020010000002L});
    public static final BitSet FOLLOW_28_in_type1458 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_type1460 = new BitSet(new long[]{0x0000020210000002L});
    public static final BitSet FOLLOW_typeArguments_in_type1463 = new BitSet(new long[]{0x0000020010000002L});
    public static final BitSet FOLLOW_41_in_type1471 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_type1473 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_primitiveType_in_type1480 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_type1483 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_type1485 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_set_in_primitiveType0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_variableModifier1573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_variableModifier1583 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_typeArguments1594 = new BitSet(new long[]{0xFF80000000000010L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments1596 = new BitSet(new long[]{0x0000000C00000000L});
    public static final BitSet FOLLOW_34_in_typeArguments1599 = new BitSet(new long[]{0xFF80000000000010L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments1601 = new BitSet(new long[]{0x0000000C00000000L});
    public static final BitSet FOLLOW_35_in_typeArguments1605 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument1617 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_63_in_typeArgument1622 = new BitSet(new long[]{0x0000000080000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_set_in_typeArgument1625 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_type_in_typeArgument1633 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList1647 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_qualifiedNameList1650 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList1652 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_65_in_formalParameters1666 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000084L});
    public static final BitSet FOLLOW_formalParameterDecls_in_formalParameters1668 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_formalParameters1671 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_formalParameterDecls1683 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_type_in_formalParameterDecls1686 = new BitSet(new long[]{0x0000000000000012L,0x0000000000000008L});
    public static final BitSet FOLLOW_formalParameterDeclsRest_in_formalParameterDecls1688 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest1701 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_formalParameterDeclsRest1704 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_formalParameterDecls_in_formalParameterDeclsRest1706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_67_in_formalParameterDeclsRest1715 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest1717 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_methodBody1729 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_qualifiedName1740 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_28_in_qualifiedName1743 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_qualifiedName1745 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_integerLiteral_in_literal1762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FloatingPointLiteral_in_literal1772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharacterLiteral_in_literal1782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_literal1792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_literal1802 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_68_in_literal1812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_integerLiteral0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_booleanLiteral0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_annotations1893 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_71_in_annotation1905 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_annotationName_in_annotation1907 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_annotation1910 = new BitSet(new long[]{0x7F80012200000FD0L,0x0007E600000000F7L});
    public static final BitSet FOLLOW_elementValuePairs_in_annotation1912 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_annotation1915 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_annotationName1929 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_28_in_annotationName1932 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_annotationName1934 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs1948 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_elementValuePairs1951 = new BitSet(new long[]{0x7F80012200000FD0L,0x0007E600000000F3L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs1953 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_Identifier_in_elementValuePair1968 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_elementValuePair1970 = new BitSet(new long[]{0x7F80012200000FD0L,0x0007E600000000F3L});
    public static final BitSet FOLLOW_elementValue_in_elementValuePair1974 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_elementValue1986 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_elementValue1993 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elementValueArrayInitializer_in_elementValue2000 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_elementValueArrayInitializer2012 = new BitSet(new long[]{0x7F80016200000FD0L,0x0007E600000000F3L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer2015 = new BitSet(new long[]{0x0000004400000000L});
    public static final BitSet FOLLOW_34_in_elementValueArrayInitializer2018 = new BitSet(new long[]{0x7F80012200000FD0L,0x0007E600000000F3L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer2020 = new BitSet(new long[]{0x0000004400000000L});
    public static final BitSet FOLLOW_38_in_elementValueArrayInitializer2027 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_71_in_annotationTypeDeclaration2039 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_annotationTypeDeclaration2041 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_annotationTypeDeclaration2043 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_annotationTypeBody_in_annotationTypeDeclaration2045 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_annotationTypeBody2057 = new BitSet(new long[]{0x7FFFE0C048000030L,0x0000000000000080L});
    public static final BitSet FOLLOW_annotationTypeElementDeclarations_in_annotationTypeBody2060 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_annotationTypeBody2064 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeElementDeclaration_in_annotationTypeElementDeclarations2077 = new BitSet(new long[]{0x7FFFE08048000032L,0x0000000000000080L});
    public static final BitSet FOLLOW_annotationTypeElementDeclaration_in_annotationTypeElementDeclarations2081 = new BitSet(new long[]{0x7FFFE08048000032L,0x0000000000000080L});
    public static final BitSet FOLLOW_modifier_in_annotationTypeElementDeclaration2096 = new BitSet(new long[]{0x7FFFE08048000030L,0x0000000000000080L});
    public static final BitSet FOLLOW_annotationTypeElementRest_in_annotationTypeElementDeclaration2100 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_annotationTypeElementRest2112 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_annotationMethodOrConstantRest_in_annotationTypeElementRest2114 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_annotationTypeElementRest2116 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_annotationTypeElementRest2123 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_annotationTypeElementRest2125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_annotationTypeElementRest2133 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_annotationTypeElementRest2135 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_annotationTypeElementRest2143 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_annotationTypeElementRest2145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_annotationTypeElementRest2153 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_annotationTypeElementRest2155 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationMethodRest_in_annotationMethodOrConstantRest2168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationConstantRest_in_annotationMethodOrConstantRest2175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_annotationMethodRest2188 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_annotationMethodRest2190 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_annotationMethodRest2192 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_defaultValue_in_annotationMethodRest2195 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclarators_in_annotationConstantRest2212 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_defaultValue2227 = new BitSet(new long[]{0x7F80012200000FD0L,0x0007E600000000F3L});
    public static final BitSet FOLLOW_elementValue_in_defaultValue2229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_block2269 = new BitSet(new long[]{0x7FFFE1E24A000FF0L,0x0007E60000FDEAF3L});
    public static final BitSet FOLLOW_blockStatement_in_block2271 = new BitSet(new long[]{0x7FFFE1E24A000FF0L,0x0007E60000FDEAF3L});
    public static final BitSet FOLLOW_38_in_block2274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_blockStatement2286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_blockStatement2291 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statement_in_blockStatement2300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_localVariableDeclaration2348 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_type_in_localVariableDeclaration2365 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarators_in_localVariableDeclaration2376 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_localVariableDeclaration2378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_statement2390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_statement2398 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_expression_in_statement2400 = new BitSet(new long[]{0x0000000002000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_statement2403 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_expression_in_statement2405 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_statement2409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_75_in_statement2417 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_statement2419 = new BitSet(new long[]{0x7F88012202000FD0L,0x0007E60000FDEA73L});
    public static final BitSet FOLLOW_statement_in_statement2421 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_76_in_statement2431 = new BitSet(new long[]{0x7F88012202000FD0L,0x0007E60000FDEA73L});
    public static final BitSet FOLLOW_statement_in_statement2433 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_77_in_statement2443 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_statement2445 = new BitSet(new long[]{0x7F82010202000FD0L,0x0007E600000000F3L});
    public static final BitSet FOLLOW_forControl_in_statement2447 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_statement2449 = new BitSet(new long[]{0x7F88012202000FD0L,0x0007E60000FDEA73L});
    public static final BitSet FOLLOW_statement_in_statement2451 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_statement2459 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_statement2461 = new BitSet(new long[]{0x7F88012202000FD0L,0x0007E60000FDEA73L});
    public static final BitSet FOLLOW_statement_in_statement2463 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_79_in_statement2471 = new BitSet(new long[]{0x7F88012202000FD0L,0x0007E60000FDEA73L});
    public static final BitSet FOLLOW_statement_in_statement2473 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_78_in_statement2475 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_statement2477 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_statement2479 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_80_in_statement2487 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_block_in_statement2489 = new BitSet(new long[]{0x0000000000000000L,0x0000000001020000L});
    public static final BitSet FOLLOW_catches_in_statement2499 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_81_in_statement2501 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_block_in_statement2503 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_statement2513 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_81_in_statement2523 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_block_in_statement2525 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_82_in_statement2541 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_statement2543 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_statement2545 = new BitSet(new long[]{0x0000004000000000L,0x0000000002000100L});
    public static final BitSet FOLLOW_switchBlockStatementGroups_in_statement2547 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_statement2549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_statement2557 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_statement2559 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_block_in_statement2561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_83_in_statement2569 = new BitSet(new long[]{0x7F80010202000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_expression_in_statement2571 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_statement2574 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_84_in_statement2582 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_expression_in_statement2584 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_statement2586 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_85_in_statement2594 = new BitSet(new long[]{0x0000000002000010L});
    public static final BitSet FOLLOW_Identifier_in_statement2596 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_statement2599 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_86_in_statement2607 = new BitSet(new long[]{0x0000000002000010L});
    public static final BitSet FOLLOW_Identifier_in_statement2609 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_statement2612 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifyStatement_in_statement2625 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_statement2633 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statementExpression_in_statement2641 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_statement2643 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_statement2651 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_statement2653 = new BitSet(new long[]{0x7F88012202000FD0L,0x0007E60000FDEA73L});
    public static final BitSet FOLLOW_statement_in_statement2655 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_87_in_modifyStatement2675 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_modifyStatement2677 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_modifyStatement2684 = new BitSet(new long[]{0x7F80014200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_expression_in_modifyStatement2692 = new BitSet(new long[]{0x0000004400000000L});
    public static final BitSet FOLLOW_34_in_modifyStatement2705 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_expression_in_modifyStatement2709 = new BitSet(new long[]{0x0000004400000000L});
    public static final BitSet FOLLOW_38_in_modifyStatement2728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catchClause_in_catches2752 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_catchClause_in_catches2755 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_88_in_catchClause2769 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_catchClause2771 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_formalParameter_in_catchClause2773 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_catchClause2775 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_block_in_catchClause2777 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_formalParameter2788 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_type_in_formalParameter2791 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameter2793 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups2807 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000100L});
    public static final BitSet FOLLOW_switchLabel_in_switchBlockStatementGroup2821 = new BitSet(new long[]{0x7FFFE1A24A000FF2L,0x0007E60000FDEAF3L});
    public static final BitSet FOLLOW_blockStatement_in_switchBlockStatementGroup2823 = new BitSet(new long[]{0x7FFFE1A24A000FF2L,0x0007E60000FDEAF3L});
    public static final BitSet FOLLOW_89_in_switchLabel2836 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_constantExpression_in_switchLabel2838 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_switchLabel2840 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_switchLabel2847 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_enumConstantName_in_switchLabel2849 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_switchLabel2851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_switchLabel2858 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_switchLabel2860 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_moreStatementExpressions2873 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_statementExpression_in_moreStatementExpressions2875 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_forVarControl_in_forControl2896 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forInit_in_forControl2901 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_forControl2904 = new BitSet(new long[]{0x7F80010202000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_expression_in_forControl2906 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_forControl2909 = new BitSet(new long[]{0x7F80010200000FD2L,0x0007E60000000073L});
    public static final BitSet FOLLOW_forUpdate_in_forControl2911 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_forInit2949 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_type_in_forInit2952 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarators_in_forInit2954 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_forInit2959 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_forVarControl2971 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_type_in_forVarControl2974 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_forVarControl2976 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_forVarControl2978 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_expression_in_forVarControl2980 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_forUpdate2991 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_parExpression3004 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_expression_in_parExpression3006 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_parExpression3008 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList3025 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_expressionList3028 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_expression_in_expressionList3030 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_expression_in_statementExpression3046 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_constantExpression3058 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression3070 = new BitSet(new long[]{0x0000100A00000002L,0x00000003FC000000L});
    public static final BitSet FOLLOW_assignmentOperator_in_expression3073 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_expression_in_expression3075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_assignmentOperator3089 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_90_in_assignmentOperator3099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_91_in_assignmentOperator3109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_92_in_assignmentOperator3119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_93_in_assignmentOperator3129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_94_in_assignmentOperator3139 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_95_in_assignmentOperator3149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_96_in_assignmentOperator3159 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_97_in_assignmentOperator3169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_assignmentOperator3179 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_assignmentOperator3181 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_assignmentOperator3183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_assignmentOperator3193 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_assignmentOperator3195 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_assignmentOperator3197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_assignmentOperator3207 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_assignmentOperator3209 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_assignmentOperator3211 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_assignmentOperator3213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression3229 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_63_in_conditionalExpression3233 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression3235 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_conditionalExpression3237 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression3239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression3258 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_98_in_conditionalOrExpression3262 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression3264 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression3283 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_99_in_conditionalAndExpression3287 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression3289 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression3308 = new BitSet(new long[]{0x0000000000000002L,0x0000001000000000L});
    public static final BitSet FOLLOW_100_in_inclusiveOrExpression3312 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression3314 = new BitSet(new long[]{0x0000000000000002L,0x0000001000000000L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression3333 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_101_in_exclusiveOrExpression3337 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression3339 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression3358 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_36_in_andExpression3362 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression3364 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression3383 = new BitSet(new long[]{0x0000000000000002L,0x000000C000000000L});
    public static final BitSet FOLLOW_set_in_equalityExpression3387 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression3395 = new BitSet(new long[]{0x0000000000000002L,0x000000C000000000L});
    public static final BitSet FOLLOW_relationalExpression_in_instanceOfExpression3414 = new BitSet(new long[]{0x0000000000000002L,0x0000010000000000L});
    public static final BitSet FOLLOW_104_in_instanceOfExpression3417 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression3419 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression3437 = new BitSet(new long[]{0x0000000A00000002L});
    public static final BitSet FOLLOW_relationalOp_in_relationalExpression3441 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression3443 = new BitSet(new long[]{0x0000000A00000002L});
    public static final BitSet FOLLOW_33_in_relationalOp3459 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_relationalOp3461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_relationalOp3465 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_relationalOp3467 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_relationalOp3471 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_relationalOp3475 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression3492 = new BitSet(new long[]{0x0000000A00000002L});
    public static final BitSet FOLLOW_shiftOp_in_shiftExpression3496 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression3498 = new BitSet(new long[]{0x0000000A00000002L});
    public static final BitSet FOLLOW_33_in_shiftOp3522 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_shiftOp3524 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_shiftOp3528 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_shiftOp3530 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_shiftOp3532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_shiftOp3536 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_shiftOp3538 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression3556 = new BitSet(new long[]{0x0000000000000002L,0x0000060000000000L});
    public static final BitSet FOLLOW_set_in_additiveExpression3560 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression3568 = new BitSet(new long[]{0x0000000000000002L,0x0000060000000000L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression3587 = new BitSet(new long[]{0x0000000020000002L,0x0000180000000000L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression3591 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression3605 = new BitSet(new long[]{0x0000000020000002L,0x0000180000000000L});
    public static final BitSet FOLLOW_105_in_unaryExpression3625 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression3627 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_106_in_unaryExpression3635 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression3637 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_109_in_unaryExpression3647 = new BitSet(new long[]{0x7F80010200000FD0L,0x0006000000000073L});
    public static final BitSet FOLLOW_primary_in_unaryExpression3649 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_110_in_unaryExpression3659 = new BitSet(new long[]{0x7F80010200000FD0L,0x0006000000000073L});
    public static final BitSet FOLLOW_primary_in_unaryExpression3661 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression3671 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_111_in_unaryExpressionNotPlusMinus3690 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus3692 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_112_in_unaryExpressionNotPlusMinus3701 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus3703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus3713 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus3723 = new BitSet(new long[]{0x0000020010000002L,0x0000600000000000L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus3725 = new BitSet(new long[]{0x0000020010000002L,0x0000600000000000L});
    public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus3728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_castExpression3751 = new BitSet(new long[]{0x7F80000000000000L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression3753 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_castExpression3755 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression3757 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_castExpression3766 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_type_in_castExpression3769 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_castExpression3773 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_castExpression3776 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007800000000073L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression3778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary3795 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_primary3805 = new BitSet(new long[]{0x0000000000000010L,0x0002000000000001L});
    public static final BitSet FOLLOW_explicitGenericInvocationSuffix_in_primary3816 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_113_in_primary3820 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_primary3822 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_113_in_primary3833 = new BitSet(new long[]{0x0000020010000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_primary3836 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_primary3838 = new BitSet(new long[]{0x0000020010000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary3843 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_primary3855 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_superSuffix_in_primary3857 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary3867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_114_in_primary3877 = new BitSet(new long[]{0x7F80000200000010L});
    public static final BitSet FOLLOW_creator_in_primary3879 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_primary3891 = new BitSet(new long[]{0x0000020010000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_primary3896 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_primary3898 = new BitSet(new long[]{0x0000020010000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary3903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary3915 = new BitSet(new long[]{0x0000020010000000L});
    public static final BitSet FOLLOW_41_in_primary3918 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_primary3920 = new BitSet(new long[]{0x0000020010000000L});
    public static final BitSet FOLLOW_28_in_primary3924 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_primary3926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_primary3936 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_primary3938 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_primary3940 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_identifierSuffix3952 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_identifierSuffix3954 = new BitSet(new long[]{0x0000020010000000L});
    public static final BitSet FOLLOW_28_in_identifierSuffix3958 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_identifierSuffix3960 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_identifierSuffix3966 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_expression_in_identifierSuffix3968 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_identifierSuffix3970 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix3983 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_identifierSuffix3993 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_identifierSuffix3995 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_identifierSuffix4005 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_explicitGenericInvocation_in_identifierSuffix4007 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_identifierSuffix4017 = new BitSet(new long[]{0x0000000000000000L,0x0002000000000000L});
    public static final BitSet FOLLOW_113_in_identifierSuffix4019 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_identifierSuffix4029 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_identifierSuffix4031 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix4033 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_identifierSuffix4043 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_114_in_identifierSuffix4045 = new BitSet(new long[]{0x0000000200000010L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_identifierSuffix4048 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_innerCreator_in_identifierSuffix4052 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator4064 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_createdName_in_creator4067 = new BitSet(new long[]{0x0000020000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_arrayCreatorRest_in_creator4078 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator4082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_createdName4094 = new BitSet(new long[]{0x0000000210000002L});
    public static final BitSet FOLLOW_typeArguments_in_createdName4096 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_28_in_createdName4108 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_createdName4110 = new BitSet(new long[]{0x0000000210000002L});
    public static final BitSet FOLLOW_typeArguments_in_createdName4112 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName4123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_innerCreator4135 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_classCreatorRest_in_innerCreator4137 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_arrayCreatorRest4148 = new BitSet(new long[]{0x7F80050200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_42_in_arrayCreatorRest4162 = new BitSet(new long[]{0x0000022000000000L});
    public static final BitSet FOLLOW_41_in_arrayCreatorRest4165 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_arrayCreatorRest4167 = new BitSet(new long[]{0x0000022000000000L});
    public static final BitSet FOLLOW_arrayInitializer_in_arrayCreatorRest4171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest4185 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_arrayCreatorRest4187 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_arrayCreatorRest4190 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest4192 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_arrayCreatorRest4194 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_arrayCreatorRest4199 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_arrayCreatorRest4201 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest4224 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_classBody_in_classCreatorRest4226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation4239 = new BitSet(new long[]{0x0000000000000010L,0x0000000000000001L});
    public static final BitSet FOLLOW_explicitGenericInvocationSuffix_in_explicitGenericInvocation4241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_nonWildcardTypeArguments4253 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments4255 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_nonWildcardTypeArguments4257 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_explicitGenericInvocationSuffix4269 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_superSuffix_in_explicitGenericInvocationSuffix4271 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_explicitGenericInvocationSuffix4278 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocationSuffix4280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_selector4292 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_selector4294 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_selector4297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_selector4306 = new BitSet(new long[]{0x0000000000000000L,0x0002000000000000L});
    public static final BitSet FOLLOW_113_in_selector4308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_selector4315 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_selector4317 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_superSuffix_in_selector4319 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_selector4326 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_114_in_selector4328 = new BitSet(new long[]{0x0000000200000010L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_selector4331 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_innerCreator_in_selector4335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_selector4342 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_expression_in_selector4344 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_selector4346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix4358 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_superSuffix4365 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_superSuffix4367 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix4370 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_arguments4386 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000077L});
    public static final BitSet FOLLOW_expressionList_in_arguments4388 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_arguments4391 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotations_in_synpred170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodDeclaration_in_synpred38577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldDeclaration_in_synpred39582 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_synpred851389 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_synpred851391 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_synpred1201893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_synpred1352123 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_synpred1352125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_synpred1372133 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_synpred1372135 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_synpred1392143 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_synpred1392145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_synpred1442286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_synpred1452291 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_synpred1502431 = new BitSet(new long[]{0x7F88012202000FD0L,0x0007E60000FDEA73L});
    public static final BitSet FOLLOW_statement_in_synpred1502433 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_synpred1552499 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_81_in_synpred1552501 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_block_in_synpred1552503 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_synpred1562513 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_synpred1762836 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_constantExpression_in_synpred1762838 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_synpred1762840 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_synpred1772847 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_enumConstantName_in_synpred1772849 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_synpred1772851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forVarControl_in_synpred1792896 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_synpred1842949 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_type_in_synpred1842952 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarators_in_synpred1842954 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignmentOperator_in_synpred1873073 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_expression_in_synpred1873075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_synpred1983193 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_synpred1983195 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_synpred1983197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalOp_in_synpred2083441 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_shiftExpression_in_synpred2083443 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftOp_in_synpred2123496 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_additiveExpression_in_synpred2123498 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_synpred2143528 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_synpred2143530 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_synpred2143532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_synpred2263713 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_synpred2303751 = new BitSet(new long[]{0x7F80000000000000L});
    public static final BitSet FOLLOW_primitiveType_in_synpred2303753 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_synpred2303755 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_unaryExpression_in_synpred2303757 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_synpred2313769 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_synpred2353836 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_synpred2353838 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred2363843 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_synpred2413896 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_synpred2413898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred2423903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_synpred2483966 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_expression_in_synpred2483968 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred2483970 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_synpred2644190 = new BitSet(new long[]{0x7F80010200000FD0L,0x0007E60000000073L});
    public static final BitSet FOLLOW_expression_in_synpred2644192 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred2644194 = new BitSet(new long[]{0x0000000000000002L});

}