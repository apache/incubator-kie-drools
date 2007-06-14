// $ANTLR 3.0 /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g 2007-06-14 12:26:44

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "Identifier", "ENUM", "FloatingPointLiteral", "CharacterLiteral", "StringLiteral", "HexLiteral", "OctalLiteral", "DecimalLiteral", "HexDigit", "IntegerTypeSuffix", "Exponent", "FloatTypeSuffix", "EscapeSequence", "UnicodeEscape", "OctalEscape", "Letter", "JavaIDDigit", "WS", "COMMENT", "LINE_COMMENT", "'package'", "';'", "'import'", "'static'", "'.'", "'*'", "'class'", "'extends'", "'implements'", "'<'", "','", "'>'", "'&'", "'{'", "'}'", "'interface'", "'void'", "'['", "']'", "'throws'", "'='", "'public'", "'protected'", "'private'", "'abstract'", "'final'", "'native'", "'synchronized'", "'transient'", "'volatile'", "'strictfp'", "'boolean'", "'char'", "'byte'", "'short'", "'int'", "'long'", "'float'", "'double'", "'?'", "'super'", "'('", "')'", "'...'", "'null'", "'true'", "'false'", "'@'", "'default'", "'assert'", "':'", "'if'", "'else'", "'for'", "'while'", "'do'", "'try'", "'finally'", "'switch'", "'return'", "'throw'", "'break'", "'continue'", "'catch'", "'case'", "'+='", "'-='", "'*='", "'/='", "'&='", "'|='", "'^='", "'%='", "'||'", "'&&'", "'|'", "'^'", "'=='", "'!='", "'instanceof'", "'+'", "'-'", "'/'", "'%'", "'++'", "'--'", "'~'", "'!'", "'this'", "'new'"
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
            ruleMemo = new HashMap[403+1];
         }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g"; }


    	private List identifiers = new ArrayList();
    	public List getIdentifiers() { return identifiers; }
    	public static final CommonToken IGNORE_TOKEN = new CommonToken(null,0,99,0,0);
    	private List errors = new ArrayList();
    	
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
                                                               mtne.token.getText()+
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:201:1: compilationUnit : ( annotations )? ( packageDeclaration )? ( importDeclaration )* ( typeDeclaration )* ;
    public final void compilationUnit() throws RecognitionException {
        int compilationUnit_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 1) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:202:4: ( ( annotations )? ( packageDeclaration )? ( importDeclaration )* ( typeDeclaration )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:202:4: ( annotations )? ( packageDeclaration )? ( importDeclaration )* ( typeDeclaration )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:202:4: ( annotations )?
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

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:203:3: ( packageDeclaration )?
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

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:204:9: ( importDeclaration )*
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

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:205:9: ( typeDeclaration )*
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:208:1: packageDeclaration : 'package' qualifiedName ';' ;
    public final void packageDeclaration() throws RecognitionException {
        int packageDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 2) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:209:4: ( 'package' qualifiedName ';' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:209:4: 'package' qualifiedName ';'
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:212:1: importDeclaration : 'import' ( 'static' )? Identifier ( '.' Identifier )* ( '.' '*' )? ';' ;
    public final void importDeclaration() throws RecognitionException {
        int importDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 3) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:213:4: ( 'import' ( 'static' )? Identifier ( '.' Identifier )* ( '.' '*' )? ';' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:213:4: 'import' ( 'static' )? Identifier ( '.' Identifier )* ( '.' '*' )? ';'
            {
            match(input,26,FOLLOW_26_in_importDeclaration125); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:213:13: ( 'static' )?
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:213:34: ( '.' Identifier )*
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:213:35: '.' Identifier
            	    {
            	    match(input,28,FOLLOW_28_in_importDeclaration133); if (failed) return ;
            	    match(input,Identifier,FOLLOW_Identifier_in_importDeclaration135); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:213:52: ( '.' '*' )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==28) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:213:53: '.' '*'
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:216:1: typeDeclaration : ( classOrInterfaceDeclaration | ';' );
    public final void typeDeclaration() throws RecognitionException {
        int typeDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 4) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:217:4: ( classOrInterfaceDeclaration | ';' )
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
                    new NoViableAltException("216:1: typeDeclaration : ( classOrInterfaceDeclaration | ';' );", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:217:4: classOrInterfaceDeclaration
                    {
                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration158);
                    classOrInterfaceDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:218:9: ';'
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:221:1: classOrInterfaceDeclaration : ( modifier )* ( classDeclaration | interfaceDeclaration ) ;
    public final void classOrInterfaceDeclaration() throws RecognitionException {
        int classOrInterfaceDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 5) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:222:4: ( ( modifier )* ( classDeclaration | interfaceDeclaration ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:222:4: ( modifier )* ( classDeclaration | interfaceDeclaration )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:222:4: ( modifier )*
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

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:222:14: ( classDeclaration | interfaceDeclaration )
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
                    new NoViableAltException("222:14: ( classDeclaration | interfaceDeclaration )", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:222:15: classDeclaration
                    {
                    pushFollow(FOLLOW_classDeclaration_in_classOrInterfaceDeclaration184);
                    classDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:222:34: interfaceDeclaration
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:225:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );
    public final void classDeclaration() throws RecognitionException {
        int classDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 6) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:226:4: ( normalClassDeclaration | enumDeclaration )
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
                    new NoViableAltException("225:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:226:4: normalClassDeclaration
                    {
                    pushFollow(FOLLOW_normalClassDeclaration_in_classDeclaration201);
                    normalClassDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:227:9: enumDeclaration
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:230:1: normalClassDeclaration : 'class' Identifier ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody ;
    public final void normalClassDeclaration() throws RecognitionException {
        int normalClassDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 7) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:231:4: ( 'class' Identifier ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:231:4: 'class' Identifier ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody
            {
            match(input,30,FOLLOW_30_in_normalClassDeclaration223); if (failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_normalClassDeclaration225); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:231:23: ( typeParameters )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==33) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:231:24: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_normalClassDeclaration228);
                    typeParameters();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:232:9: ( 'extends' type )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==31) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:232:10: 'extends' type
                    {
                    match(input,31,FOLLOW_31_in_normalClassDeclaration241); if (failed) return ;
                    pushFollow(FOLLOW_type_in_normalClassDeclaration243);
                    type();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:233:9: ( 'implements' typeList )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==32) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:233:10: 'implements' typeList
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:237:1: typeParameters : '<' typeParameter ( ',' typeParameter )* '>' ;
    public final void typeParameters() throws RecognitionException {
        int typeParameters_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 8) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:238:4: ( '<' typeParameter ( ',' typeParameter )* '>' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:238:4: '<' typeParameter ( ',' typeParameter )* '>'
            {
            match(input,33,FOLLOW_33_in_typeParameters282); if (failed) return ;
            pushFollow(FOLLOW_typeParameter_in_typeParameters284);
            typeParameter();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:238:22: ( ',' typeParameter )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==34) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:238:23: ',' typeParameter
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:241:1: typeParameter : Identifier ( 'extends' bound )? ;
    public final void typeParameter() throws RecognitionException {
        int typeParameter_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 9) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:242:4: ( Identifier ( 'extends' bound )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:242:4: Identifier ( 'extends' bound )?
            {
            match(input,Identifier,FOLLOW_Identifier_in_typeParameter304); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:242:15: ( 'extends' bound )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==31) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:242:16: 'extends' bound
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:245:1: bound : type ( '&' type )* ;
    public final void bound() throws RecognitionException {
        int bound_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 10) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:246:4: ( type ( '&' type )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:246:4: type ( '&' type )*
            {
            pushFollow(FOLLOW_type_in_bound324);
            type();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:246:9: ( '&' type )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==36) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:246:10: '&' type
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:249:1: enumDeclaration : ENUM Identifier ( 'implements' typeList )? enumBody ;
    public final void enumDeclaration() throws RecognitionException {
        int enumDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 11) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:250:4: ( ENUM Identifier ( 'implements' typeList )? enumBody )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:250:4: ENUM Identifier ( 'implements' typeList )? enumBody
            {
            match(input,ENUM,FOLLOW_ENUM_in_enumDeclaration342); if (failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_enumDeclaration344); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:250:20: ( 'implements' typeList )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==32) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:250:21: 'implements' typeList
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:253:1: enumBody : '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}' ;
    public final void enumBody() throws RecognitionException {
        int enumBody_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 12) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:254:4: ( '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:254:4: '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}'
            {
            match(input,37,FOLLOW_37_in_enumBody365); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:254:8: ( enumConstants )?
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

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:254:23: ( ',' )?
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

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:254:28: ( enumBodyDeclarations )?
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:257:1: enumConstants : enumConstant ( ',' enumConstant )* ;
    public final void enumConstants() throws RecognitionException {
        int enumConstants_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 13) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:258:4: ( enumConstant ( ',' enumConstant )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:258:4: enumConstant ( ',' enumConstant )*
            {
            pushFollow(FOLLOW_enumConstant_in_enumConstants387);
            enumConstant();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:258:17: ( ',' enumConstant )*
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:258:18: ',' enumConstant
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:261:1: enumConstant : ( annotations )? Identifier ( arguments )? ( classBody )? ;
    public final void enumConstant() throws RecognitionException {
        int enumConstant_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 14) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:262:4: ( ( annotations )? Identifier ( arguments )? ( classBody )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:262:4: ( annotations )? Identifier ( arguments )? ( classBody )?
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:262:4: ( annotations )?
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:262:28: ( arguments )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==65) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:262:29: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_enumConstant412);
                    arguments();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:262:41: ( classBody )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==37) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:262:42: classBody
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:265:1: enumBodyDeclarations : ';' ( classBodyDeclaration )* ;
    public final void enumBodyDeclarations() throws RecognitionException {
        int enumBodyDeclarations_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 15) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:266:4: ( ';' ( classBodyDeclaration )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:266:4: ';' ( classBodyDeclaration )*
            {
            match(input,25,FOLLOW_25_in_enumBodyDeclarations431); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:266:8: ( classBodyDeclaration )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( ((LA26_0>=Identifier && LA26_0<=ENUM)||LA26_0==25||LA26_0==27||LA26_0==30||LA26_0==33||LA26_0==37||(LA26_0>=39 && LA26_0<=40)||(LA26_0>=45 && LA26_0<=62)||LA26_0==71) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:266:9: classBodyDeclaration
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:269:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );
    public final void interfaceDeclaration() throws RecognitionException {
        int interfaceDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 16) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:270:4: ( normalInterfaceDeclaration | annotationTypeDeclaration )
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
                    new NoViableAltException("269:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:270:4: normalInterfaceDeclaration
                    {
                    pushFollow(FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration448);
                    normalInterfaceDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:271:5: annotationTypeDeclaration
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:274:1: normalInterfaceDeclaration : 'interface' Identifier ( typeParameters )? ( 'extends' typeList )? interfaceBody ;
    public final void normalInterfaceDeclaration() throws RecognitionException {
        int normalInterfaceDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 17) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:275:4: ( 'interface' Identifier ( typeParameters )? ( 'extends' typeList )? interfaceBody )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:275:4: 'interface' Identifier ( typeParameters )? ( 'extends' typeList )? interfaceBody
            {
            match(input,39,FOLLOW_39_in_normalInterfaceDeclaration466); if (failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_normalInterfaceDeclaration468); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:275:27: ( typeParameters )?
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

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:275:43: ( 'extends' typeList )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==31) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:275:44: 'extends' typeList
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:278:1: typeList : type ( ',' type )* ;
    public final void typeList() throws RecognitionException {
        int typeList_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 18) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:279:4: ( type ( ',' type )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:279:4: type ( ',' type )*
            {
            pushFollow(FOLLOW_type_in_typeList492);
            type();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:279:9: ( ',' type )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==34) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:279:10: ',' type
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:282:1: classBody : '{' ( classBodyDeclaration )* '}' ;
    public final void classBody() throws RecognitionException {
        int classBody_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 19) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:283:4: ( '{' ( classBodyDeclaration )* '}' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:283:4: '{' ( classBodyDeclaration )* '}'
            {
            match(input,37,FOLLOW_37_in_classBody511); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:283:8: ( classBodyDeclaration )*
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:286:1: interfaceBody : '{' ( interfaceBodyDeclaration )* '}' ;
    public final void interfaceBody() throws RecognitionException {
        int interfaceBody_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 20) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:287:4: ( '{' ( interfaceBodyDeclaration )* '}' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:287:4: '{' ( interfaceBodyDeclaration )* '}'
            {
            match(input,37,FOLLOW_37_in_interfaceBody528); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:287:8: ( interfaceBodyDeclaration )*
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:290:1: classBodyDeclaration : ( ';' | ( 'static' )? block | ( modifier )* memberDecl );
    public final void classBodyDeclaration() throws RecognitionException {
        int classBodyDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 21) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:291:4: ( ';' | ( 'static' )? block | ( modifier )* memberDecl )
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

                if ( (LA35_2==37) ) {
                    alt35=2;
                }
                else if ( ((LA35_2>=Identifier && LA35_2<=ENUM)||LA35_2==27||LA35_2==30||LA35_2==33||(LA35_2>=39 && LA35_2<=40)||(LA35_2>=45 && LA35_2<=62)||LA35_2==71) ) {
                    alt35=3;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("290:1: classBodyDeclaration : ( ';' | ( 'static' )? block | ( modifier )* memberDecl );", 35, 2, input);

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
                    new NoViableAltException("290:1: classBodyDeclaration : ( ';' | ( 'static' )? block | ( modifier )* memberDecl );", 35, 0, input);

                throw nvae;
            }

            switch (alt35) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:291:4: ';'
                    {
                    match(input,25,FOLLOW_25_in_classBodyDeclaration544); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:292:4: ( 'static' )? block
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:292:4: ( 'static' )?
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:293:4: ( modifier )* memberDecl
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:293:4: ( modifier )*
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:296:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );
    public final void memberDecl() throws RecognitionException {
        int memberDecl_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 22) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:297:4: ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration )
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
                case 65:
                    {
                    alt36=5;
                    }
                    break;
                case 33:
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
                            new NoViableAltException("296:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );", 36, 10, input);

                        throw nvae;
                    }
                    }
                    break;
                case 28:
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
                            new NoViableAltException("296:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );", 36, 11, input);

                        throw nvae;
                    }
                    }
                    break;
                case 41:
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
                            new NoViableAltException("296:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );", 36, 12, input);

                        throw nvae;
                    }
                    }
                    break;
                case Identifier:
                    {
                    int LA36_13 = input.LA(3);

                    if ( (synpred38()) ) {
                        alt36=2;
                    }
                    else if ( (synpred39()) ) {
                        alt36=3;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("296:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );", 36, 13, input);

                        throw nvae;
                    }
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("296:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );", 36, 2, input);

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
                            new NoViableAltException("296:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );", 36, 14, input);

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
                            new NoViableAltException("296:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );", 36, 15, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("296:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );", 36, 3, input);

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
                    new NoViableAltException("296:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );", 36, 0, input);

                throw nvae;
            }

            switch (alt36) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:297:4: genericMethodOrConstructorDecl
                    {
                    pushFollow(FOLLOW_genericMethodOrConstructorDecl_in_memberDecl572);
                    genericMethodOrConstructorDecl();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:298:4: methodDeclaration
                    {
                    pushFollow(FOLLOW_methodDeclaration_in_memberDecl577);
                    methodDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:299:4: fieldDeclaration
                    {
                    pushFollow(FOLLOW_fieldDeclaration_in_memberDecl582);
                    fieldDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:300:4: 'void' Identifier voidMethodDeclaratorRest
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:301:4: Identifier constructorDeclaratorRest
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_memberDecl596); if (failed) return ;
                    pushFollow(FOLLOW_constructorDeclaratorRest_in_memberDecl598);
                    constructorDeclaratorRest();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:302:4: interfaceDeclaration
                    {
                    pushFollow(FOLLOW_interfaceDeclaration_in_memberDecl603);
                    interfaceDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:303:4: classDeclaration
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:306:1: genericMethodOrConstructorDecl : typeParameters genericMethodOrConstructorRest ;
    public final void genericMethodOrConstructorDecl() throws RecognitionException {
        int genericMethodOrConstructorDecl_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 23) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:307:4: ( typeParameters genericMethodOrConstructorRest )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:307:4: typeParameters genericMethodOrConstructorRest
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:310:1: genericMethodOrConstructorRest : ( ( type | 'void' ) Identifier methodDeclaratorRest | Identifier constructorDeclaratorRest );
    public final void genericMethodOrConstructorRest() throws RecognitionException {
        int genericMethodOrConstructorRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 24) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:311:4: ( ( type | 'void' ) Identifier methodDeclaratorRest | Identifier constructorDeclaratorRest )
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==Identifier) ) {
                int LA38_1 = input.LA(2);

                if ( (LA38_1==65) ) {
                    alt38=2;
                }
                else if ( (LA38_1==Identifier||LA38_1==28||LA38_1==33||LA38_1==41) ) {
                    alt38=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("310:1: genericMethodOrConstructorRest : ( ( type | 'void' ) Identifier methodDeclaratorRest | Identifier constructorDeclaratorRest );", 38, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA38_0==40||(LA38_0>=55 && LA38_0<=62)) ) {
                alt38=1;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("310:1: genericMethodOrConstructorRest : ( ( type | 'void' ) Identifier methodDeclaratorRest | Identifier constructorDeclaratorRest );", 38, 0, input);

                throw nvae;
            }
            switch (alt38) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:311:4: ( type | 'void' ) Identifier methodDeclaratorRest
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:311:4: ( type | 'void' )
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
                            new NoViableAltException("311:4: ( type | 'void' )", 37, 0, input);

                        throw nvae;
                    }
                    switch (alt37) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:311:5: type
                            {
                            pushFollow(FOLLOW_type_in_genericMethodOrConstructorRest635);
                            type();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;
                        case 2 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:311:12: 'void'
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:312:4: Identifier constructorDeclaratorRest
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:315:1: methodDeclaration : type Identifier methodDeclaratorRest ;
    public final void methodDeclaration() throws RecognitionException {
        int methodDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 25) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:316:4: ( type Identifier methodDeclaratorRest )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:316:4: type Identifier methodDeclaratorRest
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:319:1: fieldDeclaration : type variableDeclarators ';' ;
    public final void fieldDeclaration() throws RecognitionException {
        int fieldDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 26) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:320:4: ( type variableDeclarators ';' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:320:4: type variableDeclarators ';'
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:323:1: interfaceBodyDeclaration : ( ( modifier )* interfaceMemberDecl | ';' );
    public final void interfaceBodyDeclaration() throws RecognitionException {
        int interfaceBodyDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 27) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:324:4: ( ( modifier )* interfaceMemberDecl | ';' )
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
                    new NoViableAltException("323:1: interfaceBodyDeclaration : ( ( modifier )* interfaceMemberDecl | ';' );", 40, 0, input);

                throw nvae;
            }
            switch (alt40) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:324:4: ( modifier )* interfaceMemberDecl
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:324:4: ( modifier )*
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:325:6: ';'
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:328:1: interfaceMemberDecl : ( interfaceMethodOrFieldDecl | interfaceGenericMethodDecl | 'void' Identifier voidInterfaceMethodDeclaratorRest | interfaceDeclaration | classDeclaration );
    public final void interfaceMemberDecl() throws RecognitionException {
        int interfaceMemberDecl_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 28) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:329:4: ( interfaceMethodOrFieldDecl | interfaceGenericMethodDecl | 'void' Identifier voidInterfaceMethodDeclaratorRest | interfaceDeclaration | classDeclaration )
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
                    new NoViableAltException("328:1: interfaceMemberDecl : ( interfaceMethodOrFieldDecl | interfaceGenericMethodDecl | 'void' Identifier voidInterfaceMethodDeclaratorRest | interfaceDeclaration | classDeclaration );", 41, 0, input);

                throw nvae;
            }

            switch (alt41) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:329:4: interfaceMethodOrFieldDecl
                    {
                    pushFollow(FOLLOW_interfaceMethodOrFieldDecl_in_interfaceMemberDecl715);
                    interfaceMethodOrFieldDecl();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:330:6: interfaceGenericMethodDecl
                    {
                    pushFollow(FOLLOW_interfaceGenericMethodDecl_in_interfaceMemberDecl722);
                    interfaceGenericMethodDecl();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:331:9: 'void' Identifier voidInterfaceMethodDeclaratorRest
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:332:9: interfaceDeclaration
                    {
                    pushFollow(FOLLOW_interfaceDeclaration_in_interfaceMemberDecl746);
                    interfaceDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:333:9: classDeclaration
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:336:1: interfaceMethodOrFieldDecl : type Identifier interfaceMethodOrFieldRest ;
    public final void interfaceMethodOrFieldDecl() throws RecognitionException {
        int interfaceMethodOrFieldDecl_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 29) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:337:4: ( type Identifier interfaceMethodOrFieldRest )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:337:4: type Identifier interfaceMethodOrFieldRest
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:340:1: interfaceMethodOrFieldRest : ( constantDeclaratorsRest ';' | interfaceMethodDeclaratorRest );
    public final void interfaceMethodOrFieldRest() throws RecognitionException {
        int interfaceMethodOrFieldRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 30) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:341:4: ( constantDeclaratorsRest ';' | interfaceMethodDeclaratorRest )
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
                    new NoViableAltException("340:1: interfaceMethodOrFieldRest : ( constantDeclaratorsRest ';' | interfaceMethodDeclaratorRest );", 42, 0, input);

                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:341:4: constantDeclaratorsRest ';'
                    {
                    pushFollow(FOLLOW_constantDeclaratorsRest_in_interfaceMethodOrFieldRest784);
                    constantDeclaratorsRest();
                    _fsp--;
                    if (failed) return ;
                    match(input,25,FOLLOW_25_in_interfaceMethodOrFieldRest786); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:342:4: interfaceMethodDeclaratorRest
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:345:1: methodDeclaratorRest : formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( methodBody | ';' ) ;
    public final void methodDeclaratorRest() throws RecognitionException {
        int methodDeclaratorRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 31) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:346:4: ( formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( methodBody | ';' ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:346:4: formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( methodBody | ';' )
            {
            pushFollow(FOLLOW_formalParameters_in_methodDeclaratorRest803);
            formalParameters();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:346:21: ( '[' ']' )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);

                if ( (LA43_0==41) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:346:22: '[' ']'
            	    {
            	    match(input,41,FOLLOW_41_in_methodDeclaratorRest806); if (failed) return ;
            	    match(input,42,FOLLOW_42_in_methodDeclaratorRest808); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop43;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:347:9: ( 'throws' qualifiedNameList )?
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==43) ) {
                alt44=1;
            }
            switch (alt44) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:347:10: 'throws' qualifiedNameList
                    {
                    match(input,43,FOLLOW_43_in_methodDeclaratorRest821); if (failed) return ;
                    pushFollow(FOLLOW_qualifiedNameList_in_methodDeclaratorRest823);
                    qualifiedNameList();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:348:9: ( methodBody | ';' )
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
                    new NoViableAltException("348:9: ( methodBody | ';' )", 45, 0, input);

                throw nvae;
            }
            switch (alt45) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:348:13: methodBody
                    {
                    pushFollow(FOLLOW_methodBody_in_methodDeclaratorRest839);
                    methodBody();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:349:13: ';'
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:353:1: voidMethodDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' ) ;
    public final void voidMethodDeclaratorRest() throws RecognitionException {
        int voidMethodDeclaratorRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 32) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:354:4: ( formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:354:4: formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' )
            {
            pushFollow(FOLLOW_formalParameters_in_voidMethodDeclaratorRest875);
            formalParameters();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:354:21: ( 'throws' qualifiedNameList )?
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==43) ) {
                alt46=1;
            }
            switch (alt46) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:354:22: 'throws' qualifiedNameList
                    {
                    match(input,43,FOLLOW_43_in_voidMethodDeclaratorRest878); if (failed) return ;
                    pushFollow(FOLLOW_qualifiedNameList_in_voidMethodDeclaratorRest880);
                    qualifiedNameList();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:355:9: ( methodBody | ';' )
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
                    new NoViableAltException("355:9: ( methodBody | ';' )", 47, 0, input);

                throw nvae;
            }
            switch (alt47) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:355:13: methodBody
                    {
                    pushFollow(FOLLOW_methodBody_in_voidMethodDeclaratorRest896);
                    methodBody();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:356:13: ';'
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:360:1: interfaceMethodDeclaratorRest : formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' ;
    public final void interfaceMethodDeclaratorRest() throws RecognitionException {
        int interfaceMethodDeclaratorRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 33) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:361:4: ( formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:361:4: formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';'
            {
            pushFollow(FOLLOW_formalParameters_in_interfaceMethodDeclaratorRest932);
            formalParameters();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:361:21: ( '[' ']' )*
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);

                if ( (LA48_0==41) ) {
                    alt48=1;
                }


                switch (alt48) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:361:22: '[' ']'
            	    {
            	    match(input,41,FOLLOW_41_in_interfaceMethodDeclaratorRest935); if (failed) return ;
            	    match(input,42,FOLLOW_42_in_interfaceMethodDeclaratorRest937); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop48;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:361:32: ( 'throws' qualifiedNameList )?
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==43) ) {
                alt49=1;
            }
            switch (alt49) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:361:33: 'throws' qualifiedNameList
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:364:1: interfaceGenericMethodDecl : typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest ;
    public final void interfaceGenericMethodDecl() throws RecognitionException {
        int interfaceGenericMethodDecl_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 34) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:365:4: ( typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:365:4: typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest
            {
            pushFollow(FOLLOW_typeParameters_in_interfaceGenericMethodDecl960);
            typeParameters();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:365:19: ( type | 'void' )
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
                    new NoViableAltException("365:19: ( type | 'void' )", 50, 0, input);

                throw nvae;
            }
            switch (alt50) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:365:20: type
                    {
                    pushFollow(FOLLOW_type_in_interfaceGenericMethodDecl963);
                    type();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:365:27: 'void'
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:369:1: voidInterfaceMethodDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? ';' ;
    public final void voidInterfaceMethodDeclaratorRest() throws RecognitionException {
        int voidInterfaceMethodDeclaratorRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 35) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:370:4: ( formalParameters ( 'throws' qualifiedNameList )? ';' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:370:4: formalParameters ( 'throws' qualifiedNameList )? ';'
            {
            pushFollow(FOLLOW_formalParameters_in_voidInterfaceMethodDeclaratorRest992);
            formalParameters();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:370:21: ( 'throws' qualifiedNameList )?
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==43) ) {
                alt51=1;
            }
            switch (alt51) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:370:22: 'throws' qualifiedNameList
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:373:1: constructorDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? methodBody ;
    public final void constructorDeclaratorRest() throws RecognitionException {
        int constructorDeclaratorRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 36) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:374:4: ( formalParameters ( 'throws' qualifiedNameList )? methodBody )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:374:4: formalParameters ( 'throws' qualifiedNameList )? methodBody
            {
            pushFollow(FOLLOW_formalParameters_in_constructorDeclaratorRest1013);
            formalParameters();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:374:21: ( 'throws' qualifiedNameList )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==43) ) {
                alt52=1;
            }
            switch (alt52) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:374:22: 'throws' qualifiedNameList
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:377:1: constantDeclarator : Identifier constantDeclaratorRest ;
    public final void constantDeclarator() throws RecognitionException {
        int constantDeclarator_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 37) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:378:4: ( Identifier constantDeclaratorRest )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:378:4: Identifier constantDeclaratorRest
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:381:1: variableDeclarators : variableDeclarator ( ',' variableDeclarator )* ;
    public final void variableDeclarators() throws RecognitionException {
        int variableDeclarators_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 38) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:382:4: ( variableDeclarator ( ',' variableDeclarator )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:382:4: variableDeclarator ( ',' variableDeclarator )*
            {
            pushFollow(FOLLOW_variableDeclarator_in_variableDeclarators1047);
            variableDeclarator();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:382:23: ( ',' variableDeclarator )*
            loop53:
            do {
                int alt53=2;
                int LA53_0 = input.LA(1);

                if ( (LA53_0==34) ) {
                    alt53=1;
                }


                switch (alt53) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:382:24: ',' variableDeclarator
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


    // $ANTLR start variableDeclarator
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:385:1: variableDeclarator : Identifier variableDeclaratorRest ;
    public final void variableDeclarator() throws RecognitionException {
        int variableDeclarator_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 39) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:386:4: ( Identifier variableDeclaratorRest )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:386:4: Identifier variableDeclaratorRest
            {
            match(input,Identifier,FOLLOW_Identifier_in_variableDeclarator1065); if (failed) return ;
            pushFollow(FOLLOW_variableDeclaratorRest_in_variableDeclarator1067);
            variableDeclaratorRest();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 39, variableDeclarator_StartIndex); }
        }
        return ;
    }
    // $ANTLR end variableDeclarator


    // $ANTLR start variableDeclaratorRest
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:389:1: variableDeclaratorRest : ( ( '[' ']' )+ ( '=' variableInitializer )? | '=' variableInitializer | );
    public final void variableDeclaratorRest() throws RecognitionException {
        int variableDeclaratorRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 40) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:390:4: ( ( '[' ']' )+ ( '=' variableInitializer )? | '=' variableInitializer | )
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
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("389:1: variableDeclaratorRest : ( ( '[' ']' )+ ( '=' variableInitializer )? | '=' variableInitializer | );", 56, 0, input);

                throw nvae;
            }

            switch (alt56) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:390:4: ( '[' ']' )+ ( '=' variableInitializer )?
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:390:4: ( '[' ']' )+
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
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:390:5: '[' ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_variableDeclaratorRest1080); if (failed) return ;
                    	    match(input,42,FOLLOW_42_in_variableDeclaratorRest1082); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt54 >= 1 ) break loop54;
                    	    if (backtracking>0) {failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(54, input);
                                throw eee;
                        }
                        cnt54++;
                    } while (true);

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:390:15: ( '=' variableInitializer )?
                    int alt55=2;
                    int LA55_0 = input.LA(1);

                    if ( (LA55_0==44) ) {
                        alt55=1;
                    }
                    switch (alt55) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:390:16: '=' variableInitializer
                            {
                            match(input,44,FOLLOW_44_in_variableDeclaratorRest1087); if (failed) return ;
                            pushFollow(FOLLOW_variableInitializer_in_variableDeclaratorRest1089);
                            variableInitializer();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:391:4: '=' variableInitializer
                    {
                    match(input,44,FOLLOW_44_in_variableDeclaratorRest1096); if (failed) return ;
                    pushFollow(FOLLOW_variableInitializer_in_variableDeclaratorRest1098);
                    variableInitializer();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:393:2: 
                    {
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 40, variableDeclaratorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end variableDeclaratorRest


    // $ANTLR start constantDeclaratorsRest
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:395:1: constantDeclaratorsRest : constantDeclaratorRest ( ',' constantDeclarator )* ;
    public final void constantDeclaratorsRest() throws RecognitionException {
        int constantDeclaratorsRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 41) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:396:9: ( constantDeclaratorRest ( ',' constantDeclarator )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:396:9: constantDeclaratorRest ( ',' constantDeclarator )*
            {
            pushFollow(FOLLOW_constantDeclaratorRest_in_constantDeclaratorsRest1118);
            constantDeclaratorRest();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:396:32: ( ',' constantDeclarator )*
            loop57:
            do {
                int alt57=2;
                int LA57_0 = input.LA(1);

                if ( (LA57_0==34) ) {
                    alt57=1;
                }


                switch (alt57) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:396:33: ',' constantDeclarator
            	    {
            	    match(input,34,FOLLOW_34_in_constantDeclaratorsRest1121); if (failed) return ;
            	    pushFollow(FOLLOW_constantDeclarator_in_constantDeclaratorsRest1123);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:399:1: constantDeclaratorRest : ( '[' ']' )* '=' variableInitializer ;
    public final void constantDeclaratorRest() throws RecognitionException {
        int constantDeclaratorRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 42) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:400:4: ( ( '[' ']' )* '=' variableInitializer )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:400:4: ( '[' ']' )* '=' variableInitializer
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:400:4: ( '[' ']' )*
            loop58:
            do {
                int alt58=2;
                int LA58_0 = input.LA(1);

                if ( (LA58_0==41) ) {
                    alt58=1;
                }


                switch (alt58) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:400:5: '[' ']'
            	    {
            	    match(input,41,FOLLOW_41_in_constantDeclaratorRest1140); if (failed) return ;
            	    match(input,42,FOLLOW_42_in_constantDeclaratorRest1142); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop58;
                }
            } while (true);

            match(input,44,FOLLOW_44_in_constantDeclaratorRest1146); if (failed) return ;
            pushFollow(FOLLOW_variableInitializer_in_constantDeclaratorRest1148);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:403:1: variableDeclaratorId : Identifier ( '[' ']' )* ;
    public final void variableDeclaratorId() throws RecognitionException {
        int variableDeclaratorId_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 43) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:404:4: ( Identifier ( '[' ']' )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:404:4: Identifier ( '[' ']' )*
            {
            match(input,Identifier,FOLLOW_Identifier_in_variableDeclaratorId1160); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:404:15: ( '[' ']' )*
            loop59:
            do {
                int alt59=2;
                int LA59_0 = input.LA(1);

                if ( (LA59_0==41) ) {
                    alt59=1;
                }


                switch (alt59) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:404:16: '[' ']'
            	    {
            	    match(input,41,FOLLOW_41_in_variableDeclaratorId1163); if (failed) return ;
            	    match(input,42,FOLLOW_42_in_variableDeclaratorId1165); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:407:1: variableInitializer : ( arrayInitializer | expression );
    public final void variableInitializer() throws RecognitionException {
        int variableInitializer_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 44) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:408:4: ( arrayInitializer | expression )
            int alt60=2;
            int LA60_0 = input.LA(1);

            if ( (LA60_0==37) ) {
                alt60=1;
            }
            else if ( (LA60_0==Identifier||(LA60_0>=FloatingPointLiteral && LA60_0<=DecimalLiteral)||LA60_0==33||LA60_0==40||(LA60_0>=55 && LA60_0<=62)||(LA60_0>=64 && LA60_0<=65)||(LA60_0>=68 && LA60_0<=70)||(LA60_0>=104 && LA60_0<=105)||(LA60_0>=108 && LA60_0<=113)) ) {
                alt60=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("407:1: variableInitializer : ( arrayInitializer | expression );", 60, 0, input);

                throw nvae;
            }
            switch (alt60) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:408:4: arrayInitializer
                    {
                    pushFollow(FOLLOW_arrayInitializer_in_variableInitializer1178);
                    arrayInitializer();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:409:9: expression
                    {
                    pushFollow(FOLLOW_expression_in_variableInitializer1188);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:412:1: arrayInitializer : '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}' ;
    public final void arrayInitializer() throws RecognitionException {
        int arrayInitializer_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 45) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:413:4: ( '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:413:4: '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}'
            {
            match(input,37,FOLLOW_37_in_arrayInitializer1200); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:413:8: ( variableInitializer ( ',' variableInitializer )* ( ',' )? )?
            int alt63=2;
            int LA63_0 = input.LA(1);

            if ( (LA63_0==Identifier||(LA63_0>=FloatingPointLiteral && LA63_0<=DecimalLiteral)||LA63_0==33||LA63_0==37||LA63_0==40||(LA63_0>=55 && LA63_0<=62)||(LA63_0>=64 && LA63_0<=65)||(LA63_0>=68 && LA63_0<=70)||(LA63_0>=104 && LA63_0<=105)||(LA63_0>=108 && LA63_0<=113)) ) {
                alt63=1;
            }
            switch (alt63) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:413:9: variableInitializer ( ',' variableInitializer )* ( ',' )?
                    {
                    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer1203);
                    variableInitializer();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:413:29: ( ',' variableInitializer )*
                    loop61:
                    do {
                        int alt61=2;
                        int LA61_0 = input.LA(1);

                        if ( (LA61_0==34) ) {
                            int LA61_1 = input.LA(2);

                            if ( (LA61_1==Identifier||(LA61_1>=FloatingPointLiteral && LA61_1<=DecimalLiteral)||LA61_1==33||LA61_1==37||LA61_1==40||(LA61_1>=55 && LA61_1<=62)||(LA61_1>=64 && LA61_1<=65)||(LA61_1>=68 && LA61_1<=70)||(LA61_1>=104 && LA61_1<=105)||(LA61_1>=108 && LA61_1<=113)) ) {
                                alt61=1;
                            }


                        }


                        switch (alt61) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:413:30: ',' variableInitializer
                    	    {
                    	    match(input,34,FOLLOW_34_in_arrayInitializer1206); if (failed) return ;
                    	    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer1208);
                    	    variableInitializer();
                    	    _fsp--;
                    	    if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop61;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:413:56: ( ',' )?
                    int alt62=2;
                    int LA62_0 = input.LA(1);

                    if ( (LA62_0==34) ) {
                        alt62=1;
                    }
                    switch (alt62) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:413:57: ','
                            {
                            match(input,34,FOLLOW_34_in_arrayInitializer1213); if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }

            match(input,38,FOLLOW_38_in_arrayInitializer1220); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:416:1: modifier : ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' );
    public final void modifier() throws RecognitionException {
        int modifier_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 46) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:417:9: ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' )
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
                    new NoViableAltException("416:1: modifier : ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' );", 64, 0, input);

                throw nvae;
            }

            switch (alt64) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:417:9: annotation
                    {
                    pushFollow(FOLLOW_annotation_in_modifier1236);
                    annotation();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:418:9: 'public'
                    {
                    match(input,45,FOLLOW_45_in_modifier1246); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:419:9: 'protected'
                    {
                    match(input,46,FOLLOW_46_in_modifier1256); if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:420:9: 'private'
                    {
                    match(input,47,FOLLOW_47_in_modifier1266); if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:421:9: 'static'
                    {
                    match(input,27,FOLLOW_27_in_modifier1276); if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:422:9: 'abstract'
                    {
                    match(input,48,FOLLOW_48_in_modifier1286); if (failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:423:9: 'final'
                    {
                    match(input,49,FOLLOW_49_in_modifier1296); if (failed) return ;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:424:9: 'native'
                    {
                    match(input,50,FOLLOW_50_in_modifier1306); if (failed) return ;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:425:9: 'synchronized'
                    {
                    match(input,51,FOLLOW_51_in_modifier1316); if (failed) return ;

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:426:9: 'transient'
                    {
                    match(input,52,FOLLOW_52_in_modifier1326); if (failed) return ;

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:427:9: 'volatile'
                    {
                    match(input,53,FOLLOW_53_in_modifier1336); if (failed) return ;

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:428:9: 'strictfp'
                    {
                    match(input,54,FOLLOW_54_in_modifier1346); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:431:1: packageOrTypeName : Identifier ( '.' Identifier )* ;
    public final void packageOrTypeName() throws RecognitionException {
        int packageOrTypeName_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 47) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:432:4: ( Identifier ( '.' Identifier )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:432:4: Identifier ( '.' Identifier )*
            {
            match(input,Identifier,FOLLOW_Identifier_in_packageOrTypeName1360); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:432:15: ( '.' Identifier )*
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:432:16: '.' Identifier
            	    {
            	    match(input,28,FOLLOW_28_in_packageOrTypeName1363); if (failed) return ;
            	    match(input,Identifier,FOLLOW_Identifier_in_packageOrTypeName1365); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:435:1: enumConstantName : Identifier ;
    public final void enumConstantName() throws RecognitionException {
        int enumConstantName_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 48) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:436:9: ( Identifier )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:436:9: Identifier
            {
            match(input,Identifier,FOLLOW_Identifier_in_enumConstantName1383); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:439:1: typeName : ( Identifier | packageOrTypeName '.' Identifier );
    public final void typeName() throws RecognitionException {
        int typeName_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 49) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:440:6: ( Identifier | packageOrTypeName '.' Identifier )
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
                        new NoViableAltException("439:1: typeName : ( Identifier | packageOrTypeName '.' Identifier );", 66, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("439:1: typeName : ( Identifier | packageOrTypeName '.' Identifier );", 66, 0, input);

                throw nvae;
            }
            switch (alt66) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:440:6: Identifier
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_typeName1399); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:441:9: packageOrTypeName '.' Identifier
                    {
                    pushFollow(FOLLOW_packageOrTypeName_in_typeName1409);
                    packageOrTypeName();
                    _fsp--;
                    if (failed) return ;
                    match(input,28,FOLLOW_28_in_typeName1411); if (failed) return ;
                    match(input,Identifier,FOLLOW_Identifier_in_typeName1413); if (failed) return ;

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


    // $ANTLR start type
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:444:1: type : ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* ( '[' ']' )* | primitiveType ( '[' ']' )* );
    public final void type() throws RecognitionException {
        int type_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 50) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:445:4: ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* ( '[' ']' )* | primitiveType ( '[' ']' )* )
            int alt72=2;
            int LA72_0 = input.LA(1);

            if ( (LA72_0==Identifier) ) {
                alt72=1;
            }
            else if ( ((LA72_0>=55 && LA72_0<=62)) ) {
                alt72=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("444:1: type : ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* ( '[' ']' )* | primitiveType ( '[' ']' )* );", 72, 0, input);

                throw nvae;
            }
            switch (alt72) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:445:4: Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* ( '[' ']' )*
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_type1424); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:445:15: ( typeArguments )?
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
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:445:16: typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_type1427);
                            typeArguments();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:445:32: ( '.' Identifier ( typeArguments )? )*
                    loop69:
                    do {
                        int alt69=2;
                        int LA69_0 = input.LA(1);

                        if ( (LA69_0==28) ) {
                            alt69=1;
                        }


                        switch (alt69) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:445:33: '.' Identifier ( typeArguments )?
                    	    {
                    	    match(input,28,FOLLOW_28_in_type1432); if (failed) return ;
                    	    match(input,Identifier,FOLLOW_Identifier_in_type1434); if (failed) return ;
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:445:48: ( typeArguments )?
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
                    	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:445:49: typeArguments
                    	            {
                    	            pushFollow(FOLLOW_typeArguments_in_type1437);
                    	            typeArguments();
                    	            _fsp--;
                    	            if (failed) return ;

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop69;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:445:68: ( '[' ']' )*
                    loop70:
                    do {
                        int alt70=2;
                        int LA70_0 = input.LA(1);

                        if ( (LA70_0==41) ) {
                            alt70=1;
                        }


                        switch (alt70) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:445:69: '[' ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_type1445); if (failed) return ;
                    	    match(input,42,FOLLOW_42_in_type1447); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop70;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:446:4: primitiveType ( '[' ']' )*
                    {
                    pushFollow(FOLLOW_primitiveType_in_type1454);
                    primitiveType();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:446:18: ( '[' ']' )*
                    loop71:
                    do {
                        int alt71=2;
                        int LA71_0 = input.LA(1);

                        if ( (LA71_0==41) ) {
                            alt71=1;
                        }


                        switch (alt71) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:446:19: '[' ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_type1457); if (failed) return ;
                    	    match(input,42,FOLLOW_42_in_type1459); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop71;
                        }
                    } while (true);


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 50, type_StartIndex); }
        }
        return ;
    }
    // $ANTLR end type


    // $ANTLR start primitiveType
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:449:1: primitiveType : ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' );
    public final void primitiveType() throws RecognitionException {
        int primitiveType_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 51) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:450:9: ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' )
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


    // $ANTLR start variableModifier
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:460:1: variableModifier : ( 'final' | annotation );
    public final void variableModifier() throws RecognitionException {
        int variableModifier_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 52) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:461:4: ( 'final' | annotation )
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==49) ) {
                alt73=1;
            }
            else if ( (LA73_0==71) ) {
                alt73=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("460:1: variableModifier : ( 'final' | annotation );", 73, 0, input);

                throw nvae;
            }
            switch (alt73) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:461:4: 'final'
                    {
                    match(input,49,FOLLOW_49_in_variableModifier1547); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:462:9: annotation
                    {
                    pushFollow(FOLLOW_annotation_in_variableModifier1557);
                    annotation();
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
            if ( backtracking>0 ) { memoize(input, 52, variableModifier_StartIndex); }
        }
        return ;
    }
    // $ANTLR end variableModifier


    // $ANTLR start typeArguments
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:465:1: typeArguments : '<' typeArgument ( ',' typeArgument )* '>' ;
    public final void typeArguments() throws RecognitionException {
        int typeArguments_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 53) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:466:4: ( '<' typeArgument ( ',' typeArgument )* '>' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:466:4: '<' typeArgument ( ',' typeArgument )* '>'
            {
            match(input,33,FOLLOW_33_in_typeArguments1568); if (failed) return ;
            pushFollow(FOLLOW_typeArgument_in_typeArguments1570);
            typeArgument();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:466:21: ( ',' typeArgument )*
            loop74:
            do {
                int alt74=2;
                int LA74_0 = input.LA(1);

                if ( (LA74_0==34) ) {
                    alt74=1;
                }


                switch (alt74) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:466:22: ',' typeArgument
            	    {
            	    match(input,34,FOLLOW_34_in_typeArguments1573); if (failed) return ;
            	    pushFollow(FOLLOW_typeArgument_in_typeArguments1575);
            	    typeArgument();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop74;
                }
            } while (true);

            match(input,35,FOLLOW_35_in_typeArguments1579); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:469:1: typeArgument : ( type | '?' ( ( 'extends' | 'super' ) type )? );
    public final void typeArgument() throws RecognitionException {
        int typeArgument_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 54) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:470:4: ( type | '?' ( ( 'extends' | 'super' ) type )? )
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
                    new NoViableAltException("469:1: typeArgument : ( type | '?' ( ( 'extends' | 'super' ) type )? );", 76, 0, input);

                throw nvae;
            }
            switch (alt76) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:470:4: type
                    {
                    pushFollow(FOLLOW_type_in_typeArgument1591);
                    type();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:471:4: '?' ( ( 'extends' | 'super' ) type )?
                    {
                    match(input,63,FOLLOW_63_in_typeArgument1596); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:471:8: ( ( 'extends' | 'super' ) type )?
                    int alt75=2;
                    int LA75_0 = input.LA(1);

                    if ( (LA75_0==31||LA75_0==64) ) {
                        alt75=1;
                    }
                    switch (alt75) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:471:9: ( 'extends' | 'super' ) type
                            {
                            if ( input.LA(1)==31||input.LA(1)==64 ) {
                                input.consume();
                                errorRecovery=false;failed=false;
                            }
                            else {
                                if (backtracking>0) {failed=true; return ;}
                                MismatchedSetException mse =
                                    new MismatchedSetException(null,input);
                                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_typeArgument1599);    throw mse;
                            }

                            pushFollow(FOLLOW_type_in_typeArgument1607);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:474:1: qualifiedNameList : qualifiedName ( ',' qualifiedName )* ;
    public final void qualifiedNameList() throws RecognitionException {
        int qualifiedNameList_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 55) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:475:4: ( qualifiedName ( ',' qualifiedName )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:475:4: qualifiedName ( ',' qualifiedName )*
            {
            pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList1621);
            qualifiedName();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:475:18: ( ',' qualifiedName )*
            loop77:
            do {
                int alt77=2;
                int LA77_0 = input.LA(1);

                if ( (LA77_0==34) ) {
                    alt77=1;
                }


                switch (alt77) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:475:19: ',' qualifiedName
            	    {
            	    match(input,34,FOLLOW_34_in_qualifiedNameList1624); if (failed) return ;
            	    pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList1626);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:478:1: formalParameters : '(' ( formalParameterDecls )? ')' ;
    public final void formalParameters() throws RecognitionException {
        int formalParameters_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 56) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:479:4: ( '(' ( formalParameterDecls )? ')' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:479:4: '(' ( formalParameterDecls )? ')'
            {
            match(input,65,FOLLOW_65_in_formalParameters1640); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:479:8: ( formalParameterDecls )?
            int alt78=2;
            int LA78_0 = input.LA(1);

            if ( (LA78_0==Identifier||LA78_0==49||(LA78_0>=55 && LA78_0<=62)||LA78_0==71) ) {
                alt78=1;
            }
            switch (alt78) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: formalParameterDecls
                    {
                    pushFollow(FOLLOW_formalParameterDecls_in_formalParameters1642);
                    formalParameterDecls();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            match(input,66,FOLLOW_66_in_formalParameters1645); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:482:1: formalParameterDecls : ( variableModifier )* type ( formalParameterDeclsRest )? ;
    public final void formalParameterDecls() throws RecognitionException {
        int formalParameterDecls_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 57) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:483:4: ( ( variableModifier )* type ( formalParameterDeclsRest )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:483:4: ( variableModifier )* type ( formalParameterDeclsRest )?
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:483:4: ( variableModifier )*
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
            	    pushFollow(FOLLOW_variableModifier_in_formalParameterDecls1657);
            	    variableModifier();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop79;
                }
            } while (true);

            pushFollow(FOLLOW_type_in_formalParameterDecls1660);
            type();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:483:27: ( formalParameterDeclsRest )?
            int alt80=2;
            int LA80_0 = input.LA(1);

            if ( (LA80_0==Identifier||LA80_0==67) ) {
                alt80=1;
            }
            switch (alt80) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: formalParameterDeclsRest
                    {
                    pushFollow(FOLLOW_formalParameterDeclsRest_in_formalParameterDecls1662);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:486:1: formalParameterDeclsRest : ( variableDeclaratorId ( ',' formalParameterDecls )? | '...' variableDeclaratorId );
    public final void formalParameterDeclsRest() throws RecognitionException {
        int formalParameterDeclsRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 58) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:487:4: ( variableDeclaratorId ( ',' formalParameterDecls )? | '...' variableDeclaratorId )
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
                    new NoViableAltException("486:1: formalParameterDeclsRest : ( variableDeclaratorId ( ',' formalParameterDecls )? | '...' variableDeclaratorId );", 82, 0, input);

                throw nvae;
            }
            switch (alt82) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:487:4: variableDeclaratorId ( ',' formalParameterDecls )?
                    {
                    pushFollow(FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest1675);
                    variableDeclaratorId();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:487:25: ( ',' formalParameterDecls )?
                    int alt81=2;
                    int LA81_0 = input.LA(1);

                    if ( (LA81_0==34) ) {
                        alt81=1;
                    }
                    switch (alt81) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:487:26: ',' formalParameterDecls
                            {
                            match(input,34,FOLLOW_34_in_formalParameterDeclsRest1678); if (failed) return ;
                            pushFollow(FOLLOW_formalParameterDecls_in_formalParameterDeclsRest1680);
                            formalParameterDecls();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:488:6: '...' variableDeclaratorId
                    {
                    match(input,67,FOLLOW_67_in_formalParameterDeclsRest1689); if (failed) return ;
                    pushFollow(FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest1691);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:491:1: methodBody : block ;
    public final void methodBody() throws RecognitionException {
        int methodBody_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 59) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:492:4: ( block )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:492:4: block
            {
            pushFollow(FOLLOW_block_in_methodBody1703);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:495:1: qualifiedName : Identifier ( '.' Identifier )* ;
    public final void qualifiedName() throws RecognitionException {
        int qualifiedName_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 60) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:496:4: ( Identifier ( '.' Identifier )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:496:4: Identifier ( '.' Identifier )*
            {
            match(input,Identifier,FOLLOW_Identifier_in_qualifiedName1714); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:496:15: ( '.' Identifier )*
            loop83:
            do {
                int alt83=2;
                int LA83_0 = input.LA(1);

                if ( (LA83_0==28) ) {
                    alt83=1;
                }


                switch (alt83) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:496:16: '.' Identifier
            	    {
            	    match(input,28,FOLLOW_28_in_qualifiedName1717); if (failed) return ;
            	    match(input,Identifier,FOLLOW_Identifier_in_qualifiedName1719); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:499:1: literal : ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | booleanLiteral | 'null' );
    public final void literal() throws RecognitionException {
        int literal_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 61) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:500:6: ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | booleanLiteral | 'null' )
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
                    new NoViableAltException("499:1: literal : ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | booleanLiteral | 'null' );", 84, 0, input);

                throw nvae;
            }

            switch (alt84) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:500:6: integerLiteral
                    {
                    pushFollow(FOLLOW_integerLiteral_in_literal1736);
                    integerLiteral();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:501:9: FloatingPointLiteral
                    {
                    match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_literal1746); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:502:9: CharacterLiteral
                    {
                    match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_literal1756); if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:503:9: StringLiteral
                    {
                    match(input,StringLiteral,FOLLOW_StringLiteral_in_literal1766); if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:504:9: booleanLiteral
                    {
                    pushFollow(FOLLOW_booleanLiteral_in_literal1776);
                    booleanLiteral();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:505:9: 'null'
                    {
                    match(input,68,FOLLOW_68_in_literal1786); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:508:1: integerLiteral : ( HexLiteral | OctalLiteral | DecimalLiteral );
    public final void integerLiteral() throws RecognitionException {
        int integerLiteral_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 62) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:509:9: ( HexLiteral | OctalLiteral | DecimalLiteral )
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:514:1: booleanLiteral : ( 'true' | 'false' );
    public final void booleanLiteral() throws RecognitionException {
        int booleanLiteral_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 63) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:515:9: ( 'true' | 'false' )
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:521:1: annotations : ( annotation )+ ;
    public final void annotations() throws RecognitionException {
        int annotations_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 64) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:522:4: ( ( annotation )+ )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:522:4: ( annotation )+
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:522:4: ( annotation )+
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
            	    pushFollow(FOLLOW_annotation_in_annotations1867);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:525:1: annotation : '@' annotationName ( '(' ( elementValuePairs )? ')' )? ;
    public final void annotation() throws RecognitionException {
        int annotation_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 65) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:526:4: ( '@' annotationName ( '(' ( elementValuePairs )? ')' )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:526:4: '@' annotationName ( '(' ( elementValuePairs )? ')' )?
            {
            match(input,71,FOLLOW_71_in_annotation1879); if (failed) return ;
            pushFollow(FOLLOW_annotationName_in_annotation1881);
            annotationName();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:526:23: ( '(' ( elementValuePairs )? ')' )?
            int alt87=2;
            int LA87_0 = input.LA(1);

            if ( (LA87_0==65) ) {
                alt87=1;
            }
            switch (alt87) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:526:24: '(' ( elementValuePairs )? ')'
                    {
                    match(input,65,FOLLOW_65_in_annotation1884); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:526:28: ( elementValuePairs )?
                    int alt86=2;
                    int LA86_0 = input.LA(1);

                    if ( (LA86_0==Identifier||(LA86_0>=FloatingPointLiteral && LA86_0<=DecimalLiteral)||LA86_0==33||LA86_0==37||LA86_0==40||(LA86_0>=55 && LA86_0<=62)||(LA86_0>=64 && LA86_0<=65)||(LA86_0>=68 && LA86_0<=71)||(LA86_0>=104 && LA86_0<=105)||(LA86_0>=108 && LA86_0<=113)) ) {
                        alt86=1;
                    }
                    switch (alt86) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: elementValuePairs
                            {
                            pushFollow(FOLLOW_elementValuePairs_in_annotation1886);
                            elementValuePairs();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    match(input,66,FOLLOW_66_in_annotation1889); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:529:1: annotationName : Identifier ( '.' Identifier )* ;
    public final void annotationName() throws RecognitionException {
        int annotationName_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 66) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:530:4: ( Identifier ( '.' Identifier )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:530:4: Identifier ( '.' Identifier )*
            {
            match(input,Identifier,FOLLOW_Identifier_in_annotationName1903); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:530:15: ( '.' Identifier )*
            loop88:
            do {
                int alt88=2;
                int LA88_0 = input.LA(1);

                if ( (LA88_0==28) ) {
                    alt88=1;
                }


                switch (alt88) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:530:16: '.' Identifier
            	    {
            	    match(input,28,FOLLOW_28_in_annotationName1906); if (failed) return ;
            	    match(input,Identifier,FOLLOW_Identifier_in_annotationName1908); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:533:1: elementValuePairs : elementValuePair ( ',' elementValuePair )* ;
    public final void elementValuePairs() throws RecognitionException {
        int elementValuePairs_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 67) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:534:4: ( elementValuePair ( ',' elementValuePair )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:534:4: elementValuePair ( ',' elementValuePair )*
            {
            pushFollow(FOLLOW_elementValuePair_in_elementValuePairs1922);
            elementValuePair();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:534:21: ( ',' elementValuePair )*
            loop89:
            do {
                int alt89=2;
                int LA89_0 = input.LA(1);

                if ( (LA89_0==34) ) {
                    alt89=1;
                }


                switch (alt89) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:534:22: ',' elementValuePair
            	    {
            	    match(input,34,FOLLOW_34_in_elementValuePairs1925); if (failed) return ;
            	    pushFollow(FOLLOW_elementValuePair_in_elementValuePairs1927);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:537:1: elementValuePair : ( Identifier '=' )? elementValue ;
    public final void elementValuePair() throws RecognitionException {
        int elementValuePair_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 68) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:538:4: ( ( Identifier '=' )? elementValue )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:538:4: ( Identifier '=' )? elementValue
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:538:4: ( Identifier '=' )?
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:538:5: Identifier '='
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_elementValuePair1942); if (failed) return ;
                    match(input,44,FOLLOW_44_in_elementValuePair1944); if (failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_elementValue_in_elementValuePair1948);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:541:1: elementValue : ( conditionalExpression | annotation | elementValueArrayInitializer );
    public final void elementValue() throws RecognitionException {
        int elementValue_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 69) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:542:4: ( conditionalExpression | annotation | elementValueArrayInitializer )
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
            case 104:
            case 105:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
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
                    new NoViableAltException("541:1: elementValue : ( conditionalExpression | annotation | elementValueArrayInitializer );", 91, 0, input);

                throw nvae;
            }

            switch (alt91) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:542:4: conditionalExpression
                    {
                    pushFollow(FOLLOW_conditionalExpression_in_elementValue1960);
                    conditionalExpression();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:543:6: annotation
                    {
                    pushFollow(FOLLOW_annotation_in_elementValue1967);
                    annotation();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:544:6: elementValueArrayInitializer
                    {
                    pushFollow(FOLLOW_elementValueArrayInitializer_in_elementValue1974);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:547:1: elementValueArrayInitializer : '{' ( elementValue ( ',' elementValue )* )? '}' ;
    public final void elementValueArrayInitializer() throws RecognitionException {
        int elementValueArrayInitializer_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 70) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:548:4: ( '{' ( elementValue ( ',' elementValue )* )? '}' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:548:4: '{' ( elementValue ( ',' elementValue )* )? '}'
            {
            match(input,37,FOLLOW_37_in_elementValueArrayInitializer1986); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:548:8: ( elementValue ( ',' elementValue )* )?
            int alt93=2;
            int LA93_0 = input.LA(1);

            if ( (LA93_0==Identifier||(LA93_0>=FloatingPointLiteral && LA93_0<=DecimalLiteral)||LA93_0==33||LA93_0==37||LA93_0==40||(LA93_0>=55 && LA93_0<=62)||(LA93_0>=64 && LA93_0<=65)||(LA93_0>=68 && LA93_0<=71)||(LA93_0>=104 && LA93_0<=105)||(LA93_0>=108 && LA93_0<=113)) ) {
                alt93=1;
            }
            switch (alt93) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:548:9: elementValue ( ',' elementValue )*
                    {
                    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer1989);
                    elementValue();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:548:22: ( ',' elementValue )*
                    loop92:
                    do {
                        int alt92=2;
                        int LA92_0 = input.LA(1);

                        if ( (LA92_0==34) ) {
                            alt92=1;
                        }


                        switch (alt92) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:548:23: ',' elementValue
                    	    {
                    	    match(input,34,FOLLOW_34_in_elementValueArrayInitializer1992); if (failed) return ;
                    	    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer1994);
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

            match(input,38,FOLLOW_38_in_elementValueArrayInitializer2001); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:551:1: annotationTypeDeclaration : '@' 'interface' Identifier annotationTypeBody ;
    public final void annotationTypeDeclaration() throws RecognitionException {
        int annotationTypeDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 71) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:552:4: ( '@' 'interface' Identifier annotationTypeBody )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:552:4: '@' 'interface' Identifier annotationTypeBody
            {
            match(input,71,FOLLOW_71_in_annotationTypeDeclaration2013); if (failed) return ;
            match(input,39,FOLLOW_39_in_annotationTypeDeclaration2015); if (failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_annotationTypeDeclaration2017); if (failed) return ;
            pushFollow(FOLLOW_annotationTypeBody_in_annotationTypeDeclaration2019);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:555:1: annotationTypeBody : '{' ( annotationTypeElementDeclarations )? '}' ;
    public final void annotationTypeBody() throws RecognitionException {
        int annotationTypeBody_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 72) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:556:4: ( '{' ( annotationTypeElementDeclarations )? '}' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:556:4: '{' ( annotationTypeElementDeclarations )? '}'
            {
            match(input,37,FOLLOW_37_in_annotationTypeBody2031); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:556:8: ( annotationTypeElementDeclarations )?
            int alt94=2;
            int LA94_0 = input.LA(1);

            if ( ((LA94_0>=Identifier && LA94_0<=ENUM)||LA94_0==27||LA94_0==30||LA94_0==39||(LA94_0>=45 && LA94_0<=62)||LA94_0==71) ) {
                alt94=1;
            }
            switch (alt94) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:556:9: annotationTypeElementDeclarations
                    {
                    pushFollow(FOLLOW_annotationTypeElementDeclarations_in_annotationTypeBody2034);
                    annotationTypeElementDeclarations();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            match(input,38,FOLLOW_38_in_annotationTypeBody2038); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:559:1: annotationTypeElementDeclarations : ( annotationTypeElementDeclaration ) ( annotationTypeElementDeclaration )* ;
    public final void annotationTypeElementDeclarations() throws RecognitionException {
        int annotationTypeElementDeclarations_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 73) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:560:4: ( ( annotationTypeElementDeclaration ) ( annotationTypeElementDeclaration )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:560:4: ( annotationTypeElementDeclaration ) ( annotationTypeElementDeclaration )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:560:4: ( annotationTypeElementDeclaration )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:560:5: annotationTypeElementDeclaration
            {
            pushFollow(FOLLOW_annotationTypeElementDeclaration_in_annotationTypeElementDeclarations2051);
            annotationTypeElementDeclaration();
            _fsp--;
            if (failed) return ;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:560:39: ( annotationTypeElementDeclaration )*
            loop95:
            do {
                int alt95=2;
                int LA95_0 = input.LA(1);

                if ( ((LA95_0>=Identifier && LA95_0<=ENUM)||LA95_0==27||LA95_0==30||LA95_0==39||(LA95_0>=45 && LA95_0<=62)||LA95_0==71) ) {
                    alt95=1;
                }


                switch (alt95) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:560:40: annotationTypeElementDeclaration
            	    {
            	    pushFollow(FOLLOW_annotationTypeElementDeclaration_in_annotationTypeElementDeclarations2055);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:563:1: annotationTypeElementDeclaration : ( modifier )* annotationTypeElementRest ;
    public final void annotationTypeElementDeclaration() throws RecognitionException {
        int annotationTypeElementDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 74) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:564:4: ( ( modifier )* annotationTypeElementRest )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:564:4: ( modifier )* annotationTypeElementRest
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:564:4: ( modifier )*
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:564:5: modifier
            	    {
            	    pushFollow(FOLLOW_modifier_in_annotationTypeElementDeclaration2070);
            	    modifier();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop96;
                }
            } while (true);

            pushFollow(FOLLOW_annotationTypeElementRest_in_annotationTypeElementDeclaration2074);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:567:1: annotationTypeElementRest : ( type annotationMethodOrConstantRest ';' | classDeclaration ( ';' )? | interfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? );
    public final void annotationTypeElementRest() throws RecognitionException {
        int annotationTypeElementRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 75) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:568:4: ( type annotationMethodOrConstantRest ';' | classDeclaration ( ';' )? | interfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? )
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
                            new NoViableAltException("567:1: annotationTypeElementRest : ( type annotationMethodOrConstantRest ';' | classDeclaration ( ';' )? | interfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? );", 101, 7, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("567:1: annotationTypeElementRest : ( type annotationMethodOrConstantRest ';' | classDeclaration ( ';' )? | interfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? );", 101, 4, input);

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
                            new NoViableAltException("567:1: annotationTypeElementRest : ( type annotationMethodOrConstantRest ';' | classDeclaration ( ';' )? | interfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? );", 101, 8, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("567:1: annotationTypeElementRest : ( type annotationMethodOrConstantRest ';' | classDeclaration ( ';' )? | interfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? );", 101, 6, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("567:1: annotationTypeElementRest : ( type annotationMethodOrConstantRest ';' | classDeclaration ( ';' )? | interfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? );", 101, 0, input);

                throw nvae;
            }

            switch (alt101) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:568:4: type annotationMethodOrConstantRest ';'
                    {
                    pushFollow(FOLLOW_type_in_annotationTypeElementRest2086);
                    type();
                    _fsp--;
                    if (failed) return ;
                    pushFollow(FOLLOW_annotationMethodOrConstantRest_in_annotationTypeElementRest2088);
                    annotationMethodOrConstantRest();
                    _fsp--;
                    if (failed) return ;
                    match(input,25,FOLLOW_25_in_annotationTypeElementRest2090); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:569:6: classDeclaration ( ';' )?
                    {
                    pushFollow(FOLLOW_classDeclaration_in_annotationTypeElementRest2097);
                    classDeclaration();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:569:23: ( ';' )?
                    int alt97=2;
                    int LA97_0 = input.LA(1);

                    if ( (LA97_0==25) ) {
                        alt97=1;
                    }
                    switch (alt97) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ';'
                            {
                            match(input,25,FOLLOW_25_in_annotationTypeElementRest2099); if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:570:6: interfaceDeclaration ( ';' )?
                    {
                    pushFollow(FOLLOW_interfaceDeclaration_in_annotationTypeElementRest2107);
                    interfaceDeclaration();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:570:27: ( ';' )?
                    int alt98=2;
                    int LA98_0 = input.LA(1);

                    if ( (LA98_0==25) ) {
                        alt98=1;
                    }
                    switch (alt98) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ';'
                            {
                            match(input,25,FOLLOW_25_in_annotationTypeElementRest2109); if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:571:6: enumDeclaration ( ';' )?
                    {
                    pushFollow(FOLLOW_enumDeclaration_in_annotationTypeElementRest2117);
                    enumDeclaration();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:571:22: ( ';' )?
                    int alt99=2;
                    int LA99_0 = input.LA(1);

                    if ( (LA99_0==25) ) {
                        alt99=1;
                    }
                    switch (alt99) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ';'
                            {
                            match(input,25,FOLLOW_25_in_annotationTypeElementRest2119); if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:572:6: annotationTypeDeclaration ( ';' )?
                    {
                    pushFollow(FOLLOW_annotationTypeDeclaration_in_annotationTypeElementRest2127);
                    annotationTypeDeclaration();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:572:32: ( ';' )?
                    int alt100=2;
                    int LA100_0 = input.LA(1);

                    if ( (LA100_0==25) ) {
                        alt100=1;
                    }
                    switch (alt100) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ';'
                            {
                            match(input,25,FOLLOW_25_in_annotationTypeElementRest2129); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:575:1: annotationMethodOrConstantRest : ( annotationMethodRest | annotationConstantRest );
    public final void annotationMethodOrConstantRest() throws RecognitionException {
        int annotationMethodOrConstantRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 76) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:576:4: ( annotationMethodRest | annotationConstantRest )
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
                        new NoViableAltException("575:1: annotationMethodOrConstantRest : ( annotationMethodRest | annotationConstantRest );", 102, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("575:1: annotationMethodOrConstantRest : ( annotationMethodRest | annotationConstantRest );", 102, 0, input);

                throw nvae;
            }
            switch (alt102) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:576:4: annotationMethodRest
                    {
                    pushFollow(FOLLOW_annotationMethodRest_in_annotationMethodOrConstantRest2142);
                    annotationMethodRest();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:577:6: annotationConstantRest
                    {
                    pushFollow(FOLLOW_annotationConstantRest_in_annotationMethodOrConstantRest2149);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:580:1: annotationMethodRest : Identifier '(' ')' ( defaultValue )? ;
    public final void annotationMethodRest() throws RecognitionException {
        int annotationMethodRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 77) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:581:5: ( Identifier '(' ')' ( defaultValue )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:581:5: Identifier '(' ')' ( defaultValue )?
            {
            match(input,Identifier,FOLLOW_Identifier_in_annotationMethodRest2162); if (failed) return ;
            match(input,65,FOLLOW_65_in_annotationMethodRest2164); if (failed) return ;
            match(input,66,FOLLOW_66_in_annotationMethodRest2166); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:581:24: ( defaultValue )?
            int alt103=2;
            int LA103_0 = input.LA(1);

            if ( (LA103_0==72) ) {
                alt103=1;
            }
            switch (alt103) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:581:25: defaultValue
                    {
                    pushFollow(FOLLOW_defaultValue_in_annotationMethodRest2169);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:584:1: annotationConstantRest : variableDeclarators ;
    public final void annotationConstantRest() throws RecognitionException {
        int annotationConstantRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 78) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:585:5: ( variableDeclarators )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:585:5: variableDeclarators
            {
            pushFollow(FOLLOW_variableDeclarators_in_annotationConstantRest2186);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:588:1: defaultValue : 'default' elementValue ;
    public final void defaultValue() throws RecognitionException {
        int defaultValue_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 79) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:589:5: ( 'default' elementValue )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:589:5: 'default' elementValue
            {
            match(input,72,FOLLOW_72_in_defaultValue2201); if (failed) return ;
            pushFollow(FOLLOW_elementValue_in_defaultValue2203);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:594:1: block : '{' ( blockStatement )* '}' ;
    public final void block() throws RecognitionException {
        int block_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 80) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:595:4: ( '{' ( blockStatement )* '}' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:595:4: '{' ( blockStatement )* '}'
            {
            match(input,37,FOLLOW_37_in_block2217); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:595:8: ( blockStatement )*
            loop104:
            do {
                int alt104=2;
                int LA104_0 = input.LA(1);

                if ( ((LA104_0>=Identifier && LA104_0<=DecimalLiteral)||LA104_0==25||LA104_0==27||LA104_0==30||LA104_0==33||LA104_0==37||(LA104_0>=39 && LA104_0<=40)||(LA104_0>=45 && LA104_0<=62)||(LA104_0>=64 && LA104_0<=65)||(LA104_0>=68 && LA104_0<=71)||LA104_0==73||LA104_0==75||(LA104_0>=77 && LA104_0<=80)||(LA104_0>=82 && LA104_0<=86)||(LA104_0>=104 && LA104_0<=105)||(LA104_0>=108 && LA104_0<=113)) ) {
                    alt104=1;
                }


                switch (alt104) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_block2219);
            	    blockStatement();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop104;
                }
            } while (true);

            match(input,38,FOLLOW_38_in_block2222); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:598:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );
    public final void blockStatement() throws RecognitionException {
        int blockStatement_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 81) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:599:4: ( localVariableDeclaration | classOrInterfaceDeclaration | statement )
            int alt105=3;
            switch ( input.LA(1) ) {
            case 49:
                {
                switch ( input.LA(2) ) {
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
                case 71:
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
                            new NoViableAltException("598:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 52, input);

                        throw nvae;
                    }
                    }
                    break;
                case 49:
                    {
                    int LA105_58 = input.LA(3);

                    if ( (synpred144()) ) {
                        alt105=1;
                    }
                    else if ( (synpred145()) ) {
                        alt105=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("598:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 58, input);

                        throw nvae;
                    }
                    }
                    break;
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
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("598:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 1, input);

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
                    int LA105_67 = input.LA(3);

                    if ( (synpred144()) ) {
                        alt105=1;
                    }
                    else if ( (synpred145()) ) {
                        alt105=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("598:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 67, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("598:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 2, input);

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
                case 89:
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
                    {
                    alt105=3;
                    }
                    break;
                case 28:
                    {
                    int LA105_69 = input.LA(3);

                    if ( (synpred144()) ) {
                        alt105=1;
                    }
                    else if ( (true) ) {
                        alt105=3;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("598:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 69, input);

                        throw nvae;
                    }
                    }
                    break;
                case 41:
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
                            new NoViableAltException("598:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 70, input);

                        throw nvae;
                    }
                    }
                    break;
                case 33:
                    {
                    int LA105_75 = input.LA(3);

                    if ( (synpred144()) ) {
                        alt105=1;
                    }
                    else if ( (true) ) {
                        alt105=3;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("598:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 75, input);

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
                        new NoViableAltException("598:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 3, input);

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
                    int LA105_96 = input.LA(3);

                    if ( (synpred144()) ) {
                        alt105=1;
                    }
                    else if ( (true) ) {
                        alt105=3;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("598:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 96, input);

                        throw nvae;
                    }
                    }
                    break;
                case 28:
                    {
                    alt105=3;
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
                        new NoViableAltException("598:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 4, input);

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
                        new NoViableAltException("598:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 11, input);

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
            case 104:
            case 105:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
                {
                alt105=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("598:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );", 105, 0, input);

                throw nvae;
            }

            switch (alt105) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:599:4: localVariableDeclaration
                    {
                    pushFollow(FOLLOW_localVariableDeclaration_in_blockStatement2234);
                    localVariableDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:600:4: classOrInterfaceDeclaration
                    {
                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_blockStatement2239);
                    classOrInterfaceDeclaration();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:601:8: statement
                    {
                    pushFollow(FOLLOW_statement_in_blockStatement2248);
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


    // $ANTLR start localVariableDeclaration
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:604:1: localVariableDeclaration : ( variableModifier )* type variableDeclarators ';' ;
    public final void localVariableDeclaration() throws RecognitionException {
        int localVariableDeclaration_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 82) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:605:4: ( ( variableModifier )* type variableDeclarators ';' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:605:4: ( variableModifier )* type variableDeclarators ';'
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:605:4: ( variableModifier )*
            loop106:
            do {
                int alt106=2;
                int LA106_0 = input.LA(1);

                if ( (LA106_0==49||LA106_0==71) ) {
                    alt106=1;
                }


                switch (alt106) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: variableModifier
            	    {
            	    pushFollow(FOLLOW_variableModifier_in_localVariableDeclaration2260);
            	    variableModifier();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop106;
                }
            } while (true);

            pushFollow(FOLLOW_type_in_localVariableDeclaration2263);
            type();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_variableDeclarators_in_localVariableDeclaration2265);
            variableDeclarators();
            _fsp--;
            if (failed) return ;
            match(input,25,FOLLOW_25_in_localVariableDeclaration2267); if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 82, localVariableDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end localVariableDeclaration


    // $ANTLR start statement
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:608:1: statement : ( block | 'assert' expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | ';' | statementExpression ';' | Identifier ':' statement );
    public final void statement() throws RecognitionException {
        int statement_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 83) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:609:4: ( block | 'assert' expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | ';' | statementExpression ';' | Identifier ':' statement )
            int alt113=16;
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
            case 25:
                {
                alt113=14;
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
            case 104:
            case 105:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
                {
                alt113=15;
                }
                break;
            case Identifier:
                {
                int LA113_32 = input.LA(2);

                if ( (LA113_32==74) ) {
                    alt113=16;
                }
                else if ( (LA113_32==25||(LA113_32>=28 && LA113_32<=29)||LA113_32==33||(LA113_32>=35 && LA113_32<=36)||LA113_32==41||LA113_32==44||LA113_32==63||LA113_32==65||(LA113_32>=89 && LA113_32<=109)) ) {
                    alt113=15;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("608:1: statement : ( block | 'assert' expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | ';' | statementExpression ';' | Identifier ':' statement );", 113, 32, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("608:1: statement : ( block | 'assert' expression ( ':' expression )? ';' | 'if' parExpression statement ( options {k=1; } : 'else' statement )? | 'for' '(' forControl ')' statement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | 'try' block ( catches 'finally' block | catches | 'finally' block ) | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | ';' | statementExpression ';' | Identifier ':' statement );", 113, 0, input);

                throw nvae;
            }

            switch (alt113) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:609:4: block
                    {
                    pushFollow(FOLLOW_block_in_statement2279);
                    block();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:610:7: 'assert' expression ( ':' expression )? ';'
                    {
                    match(input,73,FOLLOW_73_in_statement2287); if (failed) return ;
                    pushFollow(FOLLOW_expression_in_statement2289);
                    expression();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:610:27: ( ':' expression )?
                    int alt107=2;
                    int LA107_0 = input.LA(1);

                    if ( (LA107_0==74) ) {
                        alt107=1;
                    }
                    switch (alt107) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:610:28: ':' expression
                            {
                            match(input,74,FOLLOW_74_in_statement2292); if (failed) return ;
                            pushFollow(FOLLOW_expression_in_statement2294);
                            expression();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    match(input,25,FOLLOW_25_in_statement2298); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:611:7: 'if' parExpression statement ( options {k=1; } : 'else' statement )?
                    {
                    match(input,75,FOLLOW_75_in_statement2306); if (failed) return ;
                    pushFollow(FOLLOW_parExpression_in_statement2308);
                    parExpression();
                    _fsp--;
                    if (failed) return ;
                    pushFollow(FOLLOW_statement_in_statement2310);
                    statement();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:611:36: ( options {k=1; } : 'else' statement )?
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
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:611:52: 'else' statement
                            {
                            match(input,76,FOLLOW_76_in_statement2320); if (failed) return ;
                            pushFollow(FOLLOW_statement_in_statement2322);
                            statement();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:612:7: 'for' '(' forControl ')' statement
                    {
                    match(input,77,FOLLOW_77_in_statement2332); if (failed) return ;
                    match(input,65,FOLLOW_65_in_statement2334); if (failed) return ;
                    pushFollow(FOLLOW_forControl_in_statement2336);
                    forControl();
                    _fsp--;
                    if (failed) return ;
                    match(input,66,FOLLOW_66_in_statement2338); if (failed) return ;
                    pushFollow(FOLLOW_statement_in_statement2340);
                    statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:613:7: 'while' parExpression statement
                    {
                    match(input,78,FOLLOW_78_in_statement2348); if (failed) return ;
                    pushFollow(FOLLOW_parExpression_in_statement2350);
                    parExpression();
                    _fsp--;
                    if (failed) return ;
                    pushFollow(FOLLOW_statement_in_statement2352);
                    statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:614:7: 'do' statement 'while' parExpression ';'
                    {
                    match(input,79,FOLLOW_79_in_statement2360); if (failed) return ;
                    pushFollow(FOLLOW_statement_in_statement2362);
                    statement();
                    _fsp--;
                    if (failed) return ;
                    match(input,78,FOLLOW_78_in_statement2364); if (failed) return ;
                    pushFollow(FOLLOW_parExpression_in_statement2366);
                    parExpression();
                    _fsp--;
                    if (failed) return ;
                    match(input,25,FOLLOW_25_in_statement2368); if (failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:615:7: 'try' block ( catches 'finally' block | catches | 'finally' block )
                    {
                    match(input,80,FOLLOW_80_in_statement2376); if (failed) return ;
                    pushFollow(FOLLOW_block_in_statement2378);
                    block();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:616:7: ( catches 'finally' block | catches | 'finally' block )
                    int alt109=3;
                    int LA109_0 = input.LA(1);

                    if ( (LA109_0==87) ) {
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
                                    new NoViableAltException("616:7: ( catches 'finally' block | catches | 'finally' block )", 109, 3, input);

                                throw nvae;
                            }
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("616:7: ( catches 'finally' block | catches | 'finally' block )", 109, 1, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA109_0==81) ) {
                        alt109=3;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("616:7: ( catches 'finally' block | catches | 'finally' block )", 109, 0, input);

                        throw nvae;
                    }
                    switch (alt109) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:616:9: catches 'finally' block
                            {
                            pushFollow(FOLLOW_catches_in_statement2388);
                            catches();
                            _fsp--;
                            if (failed) return ;
                            match(input,81,FOLLOW_81_in_statement2390); if (failed) return ;
                            pushFollow(FOLLOW_block_in_statement2392);
                            block();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;
                        case 2 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:617:9: catches
                            {
                            pushFollow(FOLLOW_catches_in_statement2402);
                            catches();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;
                        case 3 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:618:9: 'finally' block
                            {
                            match(input,81,FOLLOW_81_in_statement2412); if (failed) return ;
                            pushFollow(FOLLOW_block_in_statement2414);
                            block();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:620:7: 'switch' parExpression '{' switchBlockStatementGroups '}'
                    {
                    match(input,82,FOLLOW_82_in_statement2430); if (failed) return ;
                    pushFollow(FOLLOW_parExpression_in_statement2432);
                    parExpression();
                    _fsp--;
                    if (failed) return ;
                    match(input,37,FOLLOW_37_in_statement2434); if (failed) return ;
                    pushFollow(FOLLOW_switchBlockStatementGroups_in_statement2436);
                    switchBlockStatementGroups();
                    _fsp--;
                    if (failed) return ;
                    match(input,38,FOLLOW_38_in_statement2438); if (failed) return ;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:621:7: 'synchronized' parExpression block
                    {
                    match(input,51,FOLLOW_51_in_statement2446); if (failed) return ;
                    pushFollow(FOLLOW_parExpression_in_statement2448);
                    parExpression();
                    _fsp--;
                    if (failed) return ;
                    pushFollow(FOLLOW_block_in_statement2450);
                    block();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:622:7: 'return' ( expression )? ';'
                    {
                    match(input,83,FOLLOW_83_in_statement2458); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:622:16: ( expression )?
                    int alt110=2;
                    int LA110_0 = input.LA(1);

                    if ( (LA110_0==Identifier||(LA110_0>=FloatingPointLiteral && LA110_0<=DecimalLiteral)||LA110_0==33||LA110_0==40||(LA110_0>=55 && LA110_0<=62)||(LA110_0>=64 && LA110_0<=65)||(LA110_0>=68 && LA110_0<=70)||(LA110_0>=104 && LA110_0<=105)||(LA110_0>=108 && LA110_0<=113)) ) {
                        alt110=1;
                    }
                    switch (alt110) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: expression
                            {
                            pushFollow(FOLLOW_expression_in_statement2460);
                            expression();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    match(input,25,FOLLOW_25_in_statement2463); if (failed) return ;

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:623:7: 'throw' expression ';'
                    {
                    match(input,84,FOLLOW_84_in_statement2471); if (failed) return ;
                    pushFollow(FOLLOW_expression_in_statement2473);
                    expression();
                    _fsp--;
                    if (failed) return ;
                    match(input,25,FOLLOW_25_in_statement2475); if (failed) return ;

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:624:7: 'break' ( Identifier )? ';'
                    {
                    match(input,85,FOLLOW_85_in_statement2483); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:624:15: ( Identifier )?
                    int alt111=2;
                    int LA111_0 = input.LA(1);

                    if ( (LA111_0==Identifier) ) {
                        alt111=1;
                    }
                    switch (alt111) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: Identifier
                            {
                            match(input,Identifier,FOLLOW_Identifier_in_statement2485); if (failed) return ;

                            }
                            break;

                    }

                    match(input,25,FOLLOW_25_in_statement2488); if (failed) return ;

                    }
                    break;
                case 13 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:625:7: 'continue' ( Identifier )? ';'
                    {
                    match(input,86,FOLLOW_86_in_statement2496); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:625:18: ( Identifier )?
                    int alt112=2;
                    int LA112_0 = input.LA(1);

                    if ( (LA112_0==Identifier) ) {
                        alt112=1;
                    }
                    switch (alt112) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: Identifier
                            {
                            match(input,Identifier,FOLLOW_Identifier_in_statement2498); if (failed) return ;

                            }
                            break;

                    }

                    match(input,25,FOLLOW_25_in_statement2501); if (failed) return ;

                    }
                    break;
                case 14 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:626:7: ';'
                    {
                    match(input,25,FOLLOW_25_in_statement2509); if (failed) return ;

                    }
                    break;
                case 15 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:627:7: statementExpression ';'
                    {
                    pushFollow(FOLLOW_statementExpression_in_statement2517);
                    statementExpression();
                    _fsp--;
                    if (failed) return ;
                    match(input,25,FOLLOW_25_in_statement2519); if (failed) return ;

                    }
                    break;
                case 16 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:628:7: Identifier ':' statement
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_statement2527); if (failed) return ;
                    match(input,74,FOLLOW_74_in_statement2529); if (failed) return ;
                    pushFollow(FOLLOW_statement_in_statement2531);
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


    // $ANTLR start catches
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:631:1: catches : catchClause ( catchClause )* ;
    public final void catches() throws RecognitionException {
        int catches_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 84) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:632:4: ( catchClause ( catchClause )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:632:4: catchClause ( catchClause )*
            {
            pushFollow(FOLLOW_catchClause_in_catches2543);
            catchClause();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:632:16: ( catchClause )*
            loop114:
            do {
                int alt114=2;
                int LA114_0 = input.LA(1);

                if ( (LA114_0==87) ) {
                    alt114=1;
                }


                switch (alt114) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:632:17: catchClause
            	    {
            	    pushFollow(FOLLOW_catchClause_in_catches2546);
            	    catchClause();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop114;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 84, catches_StartIndex); }
        }
        return ;
    }
    // $ANTLR end catches


    // $ANTLR start catchClause
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:635:1: catchClause : 'catch' '(' formalParameter ')' block ;
    public final void catchClause() throws RecognitionException {
        int catchClause_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 85) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:636:4: ( 'catch' '(' formalParameter ')' block )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:636:4: 'catch' '(' formalParameter ')' block
            {
            match(input,87,FOLLOW_87_in_catchClause2560); if (failed) return ;
            match(input,65,FOLLOW_65_in_catchClause2562); if (failed) return ;
            pushFollow(FOLLOW_formalParameter_in_catchClause2564);
            formalParameter();
            _fsp--;
            if (failed) return ;
            match(input,66,FOLLOW_66_in_catchClause2566); if (failed) return ;
            pushFollow(FOLLOW_block_in_catchClause2568);
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
            if ( backtracking>0 ) { memoize(input, 85, catchClause_StartIndex); }
        }
        return ;
    }
    // $ANTLR end catchClause


    // $ANTLR start formalParameter
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:639:1: formalParameter : ( variableModifier )* type variableDeclaratorId ;
    public final void formalParameter() throws RecognitionException {
        int formalParameter_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 86) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:640:4: ( ( variableModifier )* type variableDeclaratorId )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:640:4: ( variableModifier )* type variableDeclaratorId
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:640:4: ( variableModifier )*
            loop115:
            do {
                int alt115=2;
                int LA115_0 = input.LA(1);

                if ( (LA115_0==49||LA115_0==71) ) {
                    alt115=1;
                }


                switch (alt115) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: variableModifier
            	    {
            	    pushFollow(FOLLOW_variableModifier_in_formalParameter2579);
            	    variableModifier();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop115;
                }
            } while (true);

            pushFollow(FOLLOW_type_in_formalParameter2582);
            type();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_variableDeclaratorId_in_formalParameter2584);
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
            if ( backtracking>0 ) { memoize(input, 86, formalParameter_StartIndex); }
        }
        return ;
    }
    // $ANTLR end formalParameter


    // $ANTLR start switchBlockStatementGroups
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:643:1: switchBlockStatementGroups : ( switchBlockStatementGroup )* ;
    public final void switchBlockStatementGroups() throws RecognitionException {
        int switchBlockStatementGroups_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 87) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:644:4: ( ( switchBlockStatementGroup )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:644:4: ( switchBlockStatementGroup )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:644:4: ( switchBlockStatementGroup )*
            loop116:
            do {
                int alt116=2;
                int LA116_0 = input.LA(1);

                if ( (LA116_0==72||LA116_0==88) ) {
                    alt116=1;
                }


                switch (alt116) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:644:5: switchBlockStatementGroup
            	    {
            	    pushFollow(FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups2598);
            	    switchBlockStatementGroup();
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
            if ( backtracking>0 ) { memoize(input, 87, switchBlockStatementGroups_StartIndex); }
        }
        return ;
    }
    // $ANTLR end switchBlockStatementGroups


    // $ANTLR start switchBlockStatementGroup
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:647:1: switchBlockStatementGroup : switchLabel ( blockStatement )* ;
    public final void switchBlockStatementGroup() throws RecognitionException {
        int switchBlockStatementGroup_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 88) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:648:4: ( switchLabel ( blockStatement )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:648:4: switchLabel ( blockStatement )*
            {
            pushFollow(FOLLOW_switchLabel_in_switchBlockStatementGroup2612);
            switchLabel();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:648:16: ( blockStatement )*
            loop117:
            do {
                int alt117=2;
                int LA117_0 = input.LA(1);

                if ( ((LA117_0>=Identifier && LA117_0<=DecimalLiteral)||LA117_0==25||LA117_0==27||LA117_0==30||LA117_0==33||LA117_0==37||(LA117_0>=39 && LA117_0<=40)||(LA117_0>=45 && LA117_0<=62)||(LA117_0>=64 && LA117_0<=65)||(LA117_0>=68 && LA117_0<=71)||LA117_0==73||LA117_0==75||(LA117_0>=77 && LA117_0<=80)||(LA117_0>=82 && LA117_0<=86)||(LA117_0>=104 && LA117_0<=105)||(LA117_0>=108 && LA117_0<=113)) ) {
                    alt117=1;
                }


                switch (alt117) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_switchBlockStatementGroup2614);
            	    blockStatement();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop117;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 88, switchBlockStatementGroup_StartIndex); }
        }
        return ;
    }
    // $ANTLR end switchBlockStatementGroup


    // $ANTLR start switchLabel
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:651:1: switchLabel : ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' );
    public final void switchLabel() throws RecognitionException {
        int switchLabel_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 89) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:652:4: ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' )
            int alt118=3;
            int LA118_0 = input.LA(1);

            if ( (LA118_0==88) ) {
                int LA118_1 = input.LA(2);

                if ( (LA118_1==Identifier) ) {
                    int LA118_3 = input.LA(3);

                    if ( (synpred173()) ) {
                        alt118=1;
                    }
                    else if ( (synpred174()) ) {
                        alt118=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("651:1: switchLabel : ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' );", 118, 3, input);

                        throw nvae;
                    }
                }
                else if ( ((LA118_1>=FloatingPointLiteral && LA118_1<=DecimalLiteral)||LA118_1==33||LA118_1==40||(LA118_1>=55 && LA118_1<=62)||(LA118_1>=64 && LA118_1<=65)||(LA118_1>=68 && LA118_1<=70)||(LA118_1>=104 && LA118_1<=105)||(LA118_1>=108 && LA118_1<=113)) ) {
                    alt118=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("651:1: switchLabel : ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' );", 118, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA118_0==72) ) {
                alt118=3;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("651:1: switchLabel : ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' );", 118, 0, input);

                throw nvae;
            }
            switch (alt118) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:652:4: 'case' constantExpression ':'
                    {
                    match(input,88,FOLLOW_88_in_switchLabel2627); if (failed) return ;
                    pushFollow(FOLLOW_constantExpression_in_switchLabel2629);
                    constantExpression();
                    _fsp--;
                    if (failed) return ;
                    match(input,74,FOLLOW_74_in_switchLabel2631); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:653:6: 'case' enumConstantName ':'
                    {
                    match(input,88,FOLLOW_88_in_switchLabel2638); if (failed) return ;
                    pushFollow(FOLLOW_enumConstantName_in_switchLabel2640);
                    enumConstantName();
                    _fsp--;
                    if (failed) return ;
                    match(input,74,FOLLOW_74_in_switchLabel2642); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:654:6: 'default' ':'
                    {
                    match(input,72,FOLLOW_72_in_switchLabel2649); if (failed) return ;
                    match(input,74,FOLLOW_74_in_switchLabel2651); if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 89, switchLabel_StartIndex); }
        }
        return ;
    }
    // $ANTLR end switchLabel


    // $ANTLR start moreStatementExpressions
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:657:1: moreStatementExpressions : ( ',' statementExpression )* ;
    public final void moreStatementExpressions() throws RecognitionException {
        int moreStatementExpressions_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 90) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:658:4: ( ( ',' statementExpression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:658:4: ( ',' statementExpression )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:658:4: ( ',' statementExpression )*
            loop119:
            do {
                int alt119=2;
                int LA119_0 = input.LA(1);

                if ( (LA119_0==34) ) {
                    alt119=1;
                }


                switch (alt119) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:658:5: ',' statementExpression
            	    {
            	    match(input,34,FOLLOW_34_in_moreStatementExpressions2664); if (failed) return ;
            	    pushFollow(FOLLOW_statementExpression_in_moreStatementExpressions2666);
            	    statementExpression();
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
            if ( backtracking>0 ) { memoize(input, 90, moreStatementExpressions_StartIndex); }
        }
        return ;
    }
    // $ANTLR end moreStatementExpressions


    // $ANTLR start forControl
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );
    public final void forControl() throws RecognitionException {
        int forControl_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 91) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:663:4: ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? )
            int alt123=2;
            switch ( input.LA(1) ) {
            case 49:
                {
                switch ( input.LA(2) ) {
                case Identifier:
                    {
                    switch ( input.LA(3) ) {
                    case 33:
                        {
                        int LA123_60 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 60, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA123_61 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 61, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 41:
                        {
                        int LA123_62 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 62, input);

                            throw nvae;
                        }
                        }
                        break;
                    case Identifier:
                        {
                        int LA123_63 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 63, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 24, input);

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
                    int LA123_25 = input.LA(3);

                    if ( (LA123_25==41) ) {
                        int LA123_64 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 64, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA123_25==Identifier) ) {
                        int LA123_65 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 65, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 25, input);

                        throw nvae;
                    }
                    }
                    break;
                case 49:
                    {
                    switch ( input.LA(3) ) {
                    case Identifier:
                        {
                        int LA123_66 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 66, input);

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
                        int LA123_67 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 67, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA123_68 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 68, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 71:
                        {
                        int LA123_69 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 69, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 26, input);

                        throw nvae;
                    }

                    }
                    break;
                case 71:
                    {
                    int LA123_27 = input.LA(3);

                    if ( (LA123_27==Identifier) ) {
                        int LA123_70 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 70, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 27, input);

                        throw nvae;
                    }
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 1, input);

                    throw nvae;
                }

                }
                break;
            case 71:
                {
                int LA123_2 = input.LA(2);

                if ( (LA123_2==Identifier) ) {
                    switch ( input.LA(3) ) {
                    case 28:
                        {
                        int LA123_71 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 71, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 65:
                        {
                        int LA123_72 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 72, input);

                            throw nvae;
                        }
                        }
                        break;
                    case Identifier:
                        {
                        int LA123_73 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 73, input);

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
                        int LA123_74 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 74, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA123_75 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 75, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 71:
                        {
                        int LA123_76 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 76, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 28, input);

                        throw nvae;
                    }

                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 2, input);

                    throw nvae;
                }
                }
                break;
            case Identifier:
                {
                switch ( input.LA(2) ) {
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
                    case 104:
                    case 105:
                    case 108:
                    case 109:
                    case 110:
                    case 111:
                    case 112:
                    case 113:
                        {
                        alt123=2;
                        }
                        break;
                    case Identifier:
                        {
                        int LA123_95 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 95, input);

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
                        int LA123_96 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 96, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 63:
                        {
                        int LA123_98 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 98, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 29, input);

                        throw nvae;
                    }

                    }
                    break;
                case 28:
                    {
                    int LA123_30 = input.LA(3);

                    if ( (LA123_30==30||LA123_30==33||LA123_30==64||(LA123_30>=112 && LA123_30<=113)) ) {
                        alt123=2;
                    }
                    else if ( (LA123_30==Identifier) ) {
                        int LA123_100 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 100, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 30, input);

                        throw nvae;
                    }
                    }
                    break;
                case 41:
                    {
                    int LA123_31 = input.LA(3);

                    if ( (LA123_31==42) ) {
                        int LA123_105 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 105, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA123_31==Identifier||(LA123_31>=FloatingPointLiteral && LA123_31<=DecimalLiteral)||LA123_31==33||LA123_31==40||(LA123_31>=55 && LA123_31<=62)||(LA123_31>=64 && LA123_31<=65)||(LA123_31>=68 && LA123_31<=70)||(LA123_31>=104 && LA123_31<=105)||(LA123_31>=108 && LA123_31<=113)) ) {
                        alt123=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 31, input);

                        throw nvae;
                    }
                    }
                    break;
                case Identifier:
                    {
                    int LA123_32 = input.LA(3);

                    if ( (LA123_32==74) ) {
                        alt123=1;
                    }
                    else if ( (LA123_32==25||LA123_32==34||LA123_32==41||LA123_32==44) ) {
                        alt123=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 32, input);

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
                case 89:
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
                    {
                    alt123=2;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 3, input);

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
                    int LA123_57 = input.LA(3);

                    if ( (LA123_57==42) ) {
                        int LA123_131 = input.LA(4);

                        if ( (synpred176()) ) {
                            alt123=1;
                        }
                        else if ( (true) ) {
                            alt123=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 131, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 57, input);

                        throw nvae;
                    }
                    }
                    break;
                case Identifier:
                    {
                    int LA123_58 = input.LA(3);

                    if ( (LA123_58==74) ) {
                        alt123=1;
                    }
                    else if ( (LA123_58==25||LA123_58==34||LA123_58==41||LA123_58==44) ) {
                        alt123=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 58, input);

                        throw nvae;
                    }
                    }
                    break;
                case 28:
                    {
                    alt123=2;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 4, input);

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
            case 104:
            case 105:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
                {
                alt123=2;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("661:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );", 123, 0, input);

                throw nvae;
            }

            switch (alt123) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:663:4: forVarControl
                    {
                    pushFollow(FOLLOW_forVarControl_in_forControl2687);
                    forVarControl();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:664:4: ( forInit )? ';' ( expression )? ';' ( forUpdate )?
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:664:4: ( forInit )?
                    int alt120=2;
                    int LA120_0 = input.LA(1);

                    if ( (LA120_0==Identifier||(LA120_0>=FloatingPointLiteral && LA120_0<=DecimalLiteral)||LA120_0==33||LA120_0==40||LA120_0==49||(LA120_0>=55 && LA120_0<=62)||(LA120_0>=64 && LA120_0<=65)||(LA120_0>=68 && LA120_0<=71)||(LA120_0>=104 && LA120_0<=105)||(LA120_0>=108 && LA120_0<=113)) ) {
                        alt120=1;
                    }
                    switch (alt120) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: forInit
                            {
                            pushFollow(FOLLOW_forInit_in_forControl2692);
                            forInit();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    match(input,25,FOLLOW_25_in_forControl2695); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:664:17: ( expression )?
                    int alt121=2;
                    int LA121_0 = input.LA(1);

                    if ( (LA121_0==Identifier||(LA121_0>=FloatingPointLiteral && LA121_0<=DecimalLiteral)||LA121_0==33||LA121_0==40||(LA121_0>=55 && LA121_0<=62)||(LA121_0>=64 && LA121_0<=65)||(LA121_0>=68 && LA121_0<=70)||(LA121_0>=104 && LA121_0<=105)||(LA121_0>=108 && LA121_0<=113)) ) {
                        alt121=1;
                    }
                    switch (alt121) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: expression
                            {
                            pushFollow(FOLLOW_expression_in_forControl2697);
                            expression();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    match(input,25,FOLLOW_25_in_forControl2700); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:664:33: ( forUpdate )?
                    int alt122=2;
                    int LA122_0 = input.LA(1);

                    if ( (LA122_0==Identifier||(LA122_0>=FloatingPointLiteral && LA122_0<=DecimalLiteral)||LA122_0==33||LA122_0==40||(LA122_0>=55 && LA122_0<=62)||(LA122_0>=64 && LA122_0<=65)||(LA122_0>=68 && LA122_0<=70)||(LA122_0>=104 && LA122_0<=105)||(LA122_0>=108 && LA122_0<=113)) ) {
                        alt122=1;
                    }
                    switch (alt122) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: forUpdate
                            {
                            pushFollow(FOLLOW_forUpdate_in_forControl2702);
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
            if ( backtracking>0 ) { memoize(input, 91, forControl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end forControl


    // $ANTLR start forInit
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:667:1: forInit : ( ( variableModifier )* type variableDeclarators | expressionList );
    public final void forInit() throws RecognitionException {
        int forInit_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 92) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:668:4: ( ( variableModifier )* type variableDeclarators | expressionList )
            int alt125=2;
            switch ( input.LA(1) ) {
            case 49:
            case 71:
                {
                alt125=1;
                }
                break;
            case Identifier:
                {
                switch ( input.LA(2) ) {
                case 28:
                    {
                    int LA125_23 = input.LA(3);

                    if ( (synpred181()) ) {
                        alt125=1;
                    }
                    else if ( (true) ) {
                        alt125=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("667:1: forInit : ( ( variableModifier )* type variableDeclarators | expressionList );", 125, 23, input);

                        throw nvae;
                    }
                    }
                    break;
                case 41:
                    {
                    int LA125_24 = input.LA(3);

                    if ( (synpred181()) ) {
                        alt125=1;
                    }
                    else if ( (true) ) {
                        alt125=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("667:1: forInit : ( ( variableModifier )* type variableDeclarators | expressionList );", 125, 24, input);

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
                case 89:
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
                    {
                    alt125=2;
                    }
                    break;
                case 33:
                    {
                    int LA125_29 = input.LA(3);

                    if ( (synpred181()) ) {
                        alt125=1;
                    }
                    else if ( (true) ) {
                        alt125=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("667:1: forInit : ( ( variableModifier )* type variableDeclarators | expressionList );", 125, 29, input);

                        throw nvae;
                    }
                    }
                    break;
                case Identifier:
                    {
                    alt125=1;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("667:1: forInit : ( ( variableModifier )* type variableDeclarators | expressionList );", 125, 3, input);

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
                    int LA125_52 = input.LA(3);

                    if ( (synpred181()) ) {
                        alt125=1;
                    }
                    else if ( (true) ) {
                        alt125=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("667:1: forInit : ( ( variableModifier )* type variableDeclarators | expressionList );", 125, 52, input);

                        throw nvae;
                    }
                    }
                    break;
                case Identifier:
                    {
                    alt125=1;
                    }
                    break;
                case 28:
                    {
                    alt125=2;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("667:1: forInit : ( ( variableModifier )* type variableDeclarators | expressionList );", 125, 4, input);

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
            case 104:
            case 105:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
                {
                alt125=2;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("667:1: forInit : ( ( variableModifier )* type variableDeclarators | expressionList );", 125, 0, input);

                throw nvae;
            }

            switch (alt125) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:668:4: ( variableModifier )* type variableDeclarators
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:668:4: ( variableModifier )*
                    loop124:
                    do {
                        int alt124=2;
                        int LA124_0 = input.LA(1);

                        if ( (LA124_0==49||LA124_0==71) ) {
                            alt124=1;
                        }


                        switch (alt124) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: variableModifier
                    	    {
                    	    pushFollow(FOLLOW_variableModifier_in_forInit2714);
                    	    variableModifier();
                    	    _fsp--;
                    	    if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop124;
                        }
                    } while (true);

                    pushFollow(FOLLOW_type_in_forInit2717);
                    type();
                    _fsp--;
                    if (failed) return ;
                    pushFollow(FOLLOW_variableDeclarators_in_forInit2719);
                    variableDeclarators();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:669:4: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_forInit2724);
                    expressionList();
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
            if ( backtracking>0 ) { memoize(input, 92, forInit_StartIndex); }
        }
        return ;
    }
    // $ANTLR end forInit


    // $ANTLR start forVarControl
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:672:1: forVarControl : ( variableModifier )* type Identifier ':' expression ;
    public final void forVarControl() throws RecognitionException {
        int forVarControl_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 93) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:673:4: ( ( variableModifier )* type Identifier ':' expression )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:673:4: ( variableModifier )* type Identifier ':' expression
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:673:4: ( variableModifier )*
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
            	    pushFollow(FOLLOW_variableModifier_in_forVarControl2736);
            	    variableModifier();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop126;
                }
            } while (true);

            pushFollow(FOLLOW_type_in_forVarControl2739);
            type();
            _fsp--;
            if (failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_forVarControl2741); if (failed) return ;
            match(input,74,FOLLOW_74_in_forVarControl2743); if (failed) return ;
            pushFollow(FOLLOW_expression_in_forVarControl2745);
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
            if ( backtracking>0 ) { memoize(input, 93, forVarControl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end forVarControl


    // $ANTLR start forUpdate
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:676:1: forUpdate : expressionList ;
    public final void forUpdate() throws RecognitionException {
        int forUpdate_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 94) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:677:4: ( expressionList )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:677:4: expressionList
            {
            pushFollow(FOLLOW_expressionList_in_forUpdate2756);
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
            if ( backtracking>0 ) { memoize(input, 94, forUpdate_StartIndex); }
        }
        return ;
    }
    // $ANTLR end forUpdate


    // $ANTLR start parExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:682:1: parExpression : '(' expression ')' ;
    public final void parExpression() throws RecognitionException {
        int parExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 95) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:683:4: ( '(' expression ')' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:683:4: '(' expression ')'
            {
            match(input,65,FOLLOW_65_in_parExpression2769); if (failed) return ;
            pushFollow(FOLLOW_expression_in_parExpression2771);
            expression();
            _fsp--;
            if (failed) return ;
            match(input,66,FOLLOW_66_in_parExpression2773); if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 95, parExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end parExpression


    // $ANTLR start expressionList
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:686:1: expressionList : expression ( ',' expression )* ;
    public final void expressionList() throws RecognitionException {
        int expressionList_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 96) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:687:9: ( expression ( ',' expression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:687:9: expression ( ',' expression )*
            {
            pushFollow(FOLLOW_expression_in_expressionList2790);
            expression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:687:20: ( ',' expression )*
            loop127:
            do {
                int alt127=2;
                int LA127_0 = input.LA(1);

                if ( (LA127_0==34) ) {
                    alt127=1;
                }


                switch (alt127) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:687:21: ',' expression
            	    {
            	    match(input,34,FOLLOW_34_in_expressionList2793); if (failed) return ;
            	    pushFollow(FOLLOW_expression_in_expressionList2795);
            	    expression();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop127;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 96, expressionList_StartIndex); }
        }
        return ;
    }
    // $ANTLR end expressionList


    // $ANTLR start statementExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:690:1: statementExpression : expression ;
    public final void statementExpression() throws RecognitionException {
        int statementExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 97) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:691:4: ( expression )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:691:4: expression
            {
            pushFollow(FOLLOW_expression_in_statementExpression2811);
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
            if ( backtracking>0 ) { memoize(input, 97, statementExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end statementExpression


    // $ANTLR start constantExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:694:1: constantExpression : expression ;
    public final void constantExpression() throws RecognitionException {
        int constantExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 98) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:695:4: ( expression )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:695:4: expression
            {
            pushFollow(FOLLOW_expression_in_constantExpression2823);
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
            if ( backtracking>0 ) { memoize(input, 98, constantExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end constantExpression


    // $ANTLR start expression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:698:1: expression : conditionalExpression ( assignmentOperator expression )? ;
    public final void expression() throws RecognitionException {
        int expression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 99) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:699:4: ( conditionalExpression ( assignmentOperator expression )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:699:4: conditionalExpression ( assignmentOperator expression )?
            {
            pushFollow(FOLLOW_conditionalExpression_in_expression2835);
            conditionalExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:699:26: ( assignmentOperator expression )?
            int alt128=2;
            switch ( input.LA(1) ) {
                case 44:
                    {
                    int LA128_1 = input.LA(2);

                    if ( (synpred184()) ) {
                        alt128=1;
                    }
                    }
                    break;
                case 89:
                    {
                    int LA128_2 = input.LA(2);

                    if ( (synpred184()) ) {
                        alt128=1;
                    }
                    }
                    break;
                case 90:
                    {
                    int LA128_3 = input.LA(2);

                    if ( (synpred184()) ) {
                        alt128=1;
                    }
                    }
                    break;
                case 91:
                    {
                    int LA128_4 = input.LA(2);

                    if ( (synpred184()) ) {
                        alt128=1;
                    }
                    }
                    break;
                case 92:
                    {
                    int LA128_5 = input.LA(2);

                    if ( (synpred184()) ) {
                        alt128=1;
                    }
                    }
                    break;
                case 93:
                    {
                    int LA128_6 = input.LA(2);

                    if ( (synpred184()) ) {
                        alt128=1;
                    }
                    }
                    break;
                case 94:
                    {
                    int LA128_7 = input.LA(2);

                    if ( (synpred184()) ) {
                        alt128=1;
                    }
                    }
                    break;
                case 95:
                    {
                    int LA128_8 = input.LA(2);

                    if ( (synpred184()) ) {
                        alt128=1;
                    }
                    }
                    break;
                case 96:
                    {
                    int LA128_9 = input.LA(2);

                    if ( (synpred184()) ) {
                        alt128=1;
                    }
                    }
                    break;
                case 33:
                    {
                    int LA128_10 = input.LA(2);

                    if ( (synpred184()) ) {
                        alt128=1;
                    }
                    }
                    break;
                case 35:
                    {
                    int LA128_11 = input.LA(2);

                    if ( (synpred184()) ) {
                        alt128=1;
                    }
                    }
                    break;
            }

            switch (alt128) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:699:27: assignmentOperator expression
                    {
                    pushFollow(FOLLOW_assignmentOperator_in_expression2838);
                    assignmentOperator();
                    _fsp--;
                    if (failed) return ;
                    pushFollow(FOLLOW_expression_in_expression2840);
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
            if ( backtracking>0 ) { memoize(input, 99, expression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end expression


    // $ANTLR start assignmentOperator
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:702:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '=' | '>' '>' '>' '=' );
    public final void assignmentOperator() throws RecognitionException {
        int assignmentOperator_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 100) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:703:4: ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '=' | '>' '>' '>' '=' )
            int alt129=12;
            switch ( input.LA(1) ) {
            case 44:
                {
                alt129=1;
                }
                break;
            case 89:
                {
                alt129=2;
                }
                break;
            case 90:
                {
                alt129=3;
                }
                break;
            case 91:
                {
                alt129=4;
                }
                break;
            case 92:
                {
                alt129=5;
                }
                break;
            case 93:
                {
                alt129=6;
                }
                break;
            case 94:
                {
                alt129=7;
                }
                break;
            case 95:
                {
                alt129=8;
                }
                break;
            case 96:
                {
                alt129=9;
                }
                break;
            case 33:
                {
                alt129=10;
                }
                break;
            case 35:
                {
                int LA129_11 = input.LA(2);

                if ( (LA129_11==35) ) {
                    int LA129_12 = input.LA(3);

                    if ( (synpred195()) ) {
                        alt129=11;
                    }
                    else if ( (true) ) {
                        alt129=12;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("702:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '=' | '>' '>' '>' '=' );", 129, 12, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("702:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '=' | '>' '>' '>' '=' );", 129, 11, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("702:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '=' | '>' '>' '>' '=' );", 129, 0, input);

                throw nvae;
            }

            switch (alt129) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:703:4: '='
                    {
                    match(input,44,FOLLOW_44_in_assignmentOperator2854); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:704:9: '+='
                    {
                    match(input,89,FOLLOW_89_in_assignmentOperator2864); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:705:9: '-='
                    {
                    match(input,90,FOLLOW_90_in_assignmentOperator2874); if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:706:9: '*='
                    {
                    match(input,91,FOLLOW_91_in_assignmentOperator2884); if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:707:9: '/='
                    {
                    match(input,92,FOLLOW_92_in_assignmentOperator2894); if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:708:9: '&='
                    {
                    match(input,93,FOLLOW_93_in_assignmentOperator2904); if (failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:709:9: '|='
                    {
                    match(input,94,FOLLOW_94_in_assignmentOperator2914); if (failed) return ;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:710:9: '^='
                    {
                    match(input,95,FOLLOW_95_in_assignmentOperator2924); if (failed) return ;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:711:9: '%='
                    {
                    match(input,96,FOLLOW_96_in_assignmentOperator2934); if (failed) return ;

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:712:9: '<' '<' '='
                    {
                    match(input,33,FOLLOW_33_in_assignmentOperator2944); if (failed) return ;
                    match(input,33,FOLLOW_33_in_assignmentOperator2946); if (failed) return ;
                    match(input,44,FOLLOW_44_in_assignmentOperator2948); if (failed) return ;

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:713:9: '>' '>' '='
                    {
                    match(input,35,FOLLOW_35_in_assignmentOperator2958); if (failed) return ;
                    match(input,35,FOLLOW_35_in_assignmentOperator2960); if (failed) return ;
                    match(input,44,FOLLOW_44_in_assignmentOperator2962); if (failed) return ;

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:714:9: '>' '>' '>' '='
                    {
                    match(input,35,FOLLOW_35_in_assignmentOperator2972); if (failed) return ;
                    match(input,35,FOLLOW_35_in_assignmentOperator2974); if (failed) return ;
                    match(input,35,FOLLOW_35_in_assignmentOperator2976); if (failed) return ;
                    match(input,44,FOLLOW_44_in_assignmentOperator2978); if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 100, assignmentOperator_StartIndex); }
        }
        return ;
    }
    // $ANTLR end assignmentOperator


    // $ANTLR start conditionalExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:717:1: conditionalExpression : conditionalOrExpression ( '?' expression ':' expression )? ;
    public final void conditionalExpression() throws RecognitionException {
        int conditionalExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 101) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:718:9: ( conditionalOrExpression ( '?' expression ':' expression )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:718:9: conditionalOrExpression ( '?' expression ':' expression )?
            {
            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression2994);
            conditionalOrExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:718:33: ( '?' expression ':' expression )?
            int alt130=2;
            int LA130_0 = input.LA(1);

            if ( (LA130_0==63) ) {
                alt130=1;
            }
            switch (alt130) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:718:35: '?' expression ':' expression
                    {
                    match(input,63,FOLLOW_63_in_conditionalExpression2998); if (failed) return ;
                    pushFollow(FOLLOW_expression_in_conditionalExpression3000);
                    expression();
                    _fsp--;
                    if (failed) return ;
                    match(input,74,FOLLOW_74_in_conditionalExpression3002); if (failed) return ;
                    pushFollow(FOLLOW_expression_in_conditionalExpression3004);
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
            if ( backtracking>0 ) { memoize(input, 101, conditionalExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end conditionalExpression


    // $ANTLR start conditionalOrExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:721:1: conditionalOrExpression : conditionalAndExpression ( '||' conditionalAndExpression )* ;
    public final void conditionalOrExpression() throws RecognitionException {
        int conditionalOrExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 102) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:722:9: ( conditionalAndExpression ( '||' conditionalAndExpression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:722:9: conditionalAndExpression ( '||' conditionalAndExpression )*
            {
            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression3023);
            conditionalAndExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:722:34: ( '||' conditionalAndExpression )*
            loop131:
            do {
                int alt131=2;
                int LA131_0 = input.LA(1);

                if ( (LA131_0==97) ) {
                    alt131=1;
                }


                switch (alt131) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:722:36: '||' conditionalAndExpression
            	    {
            	    match(input,97,FOLLOW_97_in_conditionalOrExpression3027); if (failed) return ;
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression3029);
            	    conditionalAndExpression();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop131;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 102, conditionalOrExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end conditionalOrExpression


    // $ANTLR start conditionalAndExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:725:1: conditionalAndExpression : inclusiveOrExpression ( '&&' inclusiveOrExpression )* ;
    public final void conditionalAndExpression() throws RecognitionException {
        int conditionalAndExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 103) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:726:9: ( inclusiveOrExpression ( '&&' inclusiveOrExpression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:726:9: inclusiveOrExpression ( '&&' inclusiveOrExpression )*
            {
            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression3048);
            inclusiveOrExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:726:31: ( '&&' inclusiveOrExpression )*
            loop132:
            do {
                int alt132=2;
                int LA132_0 = input.LA(1);

                if ( (LA132_0==98) ) {
                    alt132=1;
                }


                switch (alt132) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:726:33: '&&' inclusiveOrExpression
            	    {
            	    match(input,98,FOLLOW_98_in_conditionalAndExpression3052); if (failed) return ;
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression3054);
            	    inclusiveOrExpression();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop132;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 103, conditionalAndExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end conditionalAndExpression


    // $ANTLR start inclusiveOrExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:729:1: inclusiveOrExpression : exclusiveOrExpression ( '|' exclusiveOrExpression )* ;
    public final void inclusiveOrExpression() throws RecognitionException {
        int inclusiveOrExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 104) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:730:9: ( exclusiveOrExpression ( '|' exclusiveOrExpression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:730:9: exclusiveOrExpression ( '|' exclusiveOrExpression )*
            {
            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression3073);
            exclusiveOrExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:730:31: ( '|' exclusiveOrExpression )*
            loop133:
            do {
                int alt133=2;
                int LA133_0 = input.LA(1);

                if ( (LA133_0==99) ) {
                    alt133=1;
                }


                switch (alt133) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:730:33: '|' exclusiveOrExpression
            	    {
            	    match(input,99,FOLLOW_99_in_inclusiveOrExpression3077); if (failed) return ;
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression3079);
            	    exclusiveOrExpression();
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
            if ( backtracking>0 ) { memoize(input, 104, inclusiveOrExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end inclusiveOrExpression


    // $ANTLR start exclusiveOrExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:733:1: exclusiveOrExpression : andExpression ( '^' andExpression )* ;
    public final void exclusiveOrExpression() throws RecognitionException {
        int exclusiveOrExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 105) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:734:9: ( andExpression ( '^' andExpression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:734:9: andExpression ( '^' andExpression )*
            {
            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression3098);
            andExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:734:23: ( '^' andExpression )*
            loop134:
            do {
                int alt134=2;
                int LA134_0 = input.LA(1);

                if ( (LA134_0==100) ) {
                    alt134=1;
                }


                switch (alt134) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:734:25: '^' andExpression
            	    {
            	    match(input,100,FOLLOW_100_in_exclusiveOrExpression3102); if (failed) return ;
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression3104);
            	    andExpression();
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
            if ( backtracking>0 ) { memoize(input, 105, exclusiveOrExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end exclusiveOrExpression


    // $ANTLR start andExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:737:1: andExpression : equalityExpression ( '&' equalityExpression )* ;
    public final void andExpression() throws RecognitionException {
        int andExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 106) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:738:9: ( equalityExpression ( '&' equalityExpression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:738:9: equalityExpression ( '&' equalityExpression )*
            {
            pushFollow(FOLLOW_equalityExpression_in_andExpression3123);
            equalityExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:738:28: ( '&' equalityExpression )*
            loop135:
            do {
                int alt135=2;
                int LA135_0 = input.LA(1);

                if ( (LA135_0==36) ) {
                    alt135=1;
                }


                switch (alt135) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:738:30: '&' equalityExpression
            	    {
            	    match(input,36,FOLLOW_36_in_andExpression3127); if (failed) return ;
            	    pushFollow(FOLLOW_equalityExpression_in_andExpression3129);
            	    equalityExpression();
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
            if ( backtracking>0 ) { memoize(input, 106, andExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end andExpression


    // $ANTLR start equalityExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:741:1: equalityExpression : instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* ;
    public final void equalityExpression() throws RecognitionException {
        int equalityExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 107) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:742:9: ( instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:742:9: instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )*
            {
            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression3148);
            instanceOfExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:742:30: ( ( '==' | '!=' ) instanceOfExpression )*
            loop136:
            do {
                int alt136=2;
                int LA136_0 = input.LA(1);

                if ( ((LA136_0>=101 && LA136_0<=102)) ) {
                    alt136=1;
                }


                switch (alt136) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:742:32: ( '==' | '!=' ) instanceOfExpression
            	    {
            	    if ( (input.LA(1)>=101 && input.LA(1)<=102) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_equalityExpression3152);    throw mse;
            	    }

            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression3160);
            	    instanceOfExpression();
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
            if ( backtracking>0 ) { memoize(input, 107, equalityExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end equalityExpression


    // $ANTLR start instanceOfExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:745:1: instanceOfExpression : relationalExpression ( 'instanceof' type )? ;
    public final void instanceOfExpression() throws RecognitionException {
        int instanceOfExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 108) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:746:9: ( relationalExpression ( 'instanceof' type )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:746:9: relationalExpression ( 'instanceof' type )?
            {
            pushFollow(FOLLOW_relationalExpression_in_instanceOfExpression3179);
            relationalExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:746:30: ( 'instanceof' type )?
            int alt137=2;
            int LA137_0 = input.LA(1);

            if ( (LA137_0==103) ) {
                alt137=1;
            }
            switch (alt137) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:746:31: 'instanceof' type
                    {
                    match(input,103,FOLLOW_103_in_instanceOfExpression3182); if (failed) return ;
                    pushFollow(FOLLOW_type_in_instanceOfExpression3184);
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
            if ( backtracking>0 ) { memoize(input, 108, instanceOfExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end instanceOfExpression


    // $ANTLR start relationalExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:749:1: relationalExpression : shiftExpression ( relationalOp shiftExpression )* ;
    public final void relationalExpression() throws RecognitionException {
        int relationalExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 109) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:750:9: ( shiftExpression ( relationalOp shiftExpression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:750:9: shiftExpression ( relationalOp shiftExpression )*
            {
            pushFollow(FOLLOW_shiftExpression_in_relationalExpression3202);
            shiftExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:750:25: ( relationalOp shiftExpression )*
            loop138:
            do {
                int alt138=2;
                int LA138_0 = input.LA(1);

                if ( (LA138_0==33) ) {
                    int LA138_23 = input.LA(2);

                    if ( (LA138_23==Identifier||(LA138_23>=FloatingPointLiteral && LA138_23<=DecimalLiteral)||LA138_23==40||LA138_23==44||(LA138_23>=55 && LA138_23<=62)||(LA138_23>=64 && LA138_23<=65)||(LA138_23>=68 && LA138_23<=70)||(LA138_23>=104 && LA138_23<=105)||(LA138_23>=108 && LA138_23<=113)) ) {
                        alt138=1;
                    }
                    else if ( (LA138_23==33) ) {
                        int LA138_28 = input.LA(3);

                        if ( (synpred205()) ) {
                            alt138=1;
                        }


                    }


                }
                else if ( (LA138_0==35) ) {
                    int LA138_24 = input.LA(2);

                    if ( (LA138_24==Identifier||(LA138_24>=FloatingPointLiteral && LA138_24<=DecimalLiteral)||LA138_24==33||LA138_24==40||LA138_24==44||(LA138_24>=55 && LA138_24<=62)||(LA138_24>=64 && LA138_24<=65)||(LA138_24>=68 && LA138_24<=70)||(LA138_24>=104 && LA138_24<=105)||(LA138_24>=108 && LA138_24<=113)) ) {
                        alt138=1;
                    }


                }


                switch (alt138) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:750:27: relationalOp shiftExpression
            	    {
            	    pushFollow(FOLLOW_relationalOp_in_relationalExpression3206);
            	    relationalOp();
            	    _fsp--;
            	    if (failed) return ;
            	    pushFollow(FOLLOW_shiftExpression_in_relationalExpression3208);
            	    shiftExpression();
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
            if ( backtracking>0 ) { memoize(input, 109, relationalExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end relationalExpression


    // $ANTLR start relationalOp
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:753:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' ) ;
    public final void relationalOp() throws RecognitionException {
        int relationalOp_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 110) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:754:4: ( ( '<' '=' | '>' '=' | '<' | '>' ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:754:4: ( '<' '=' | '>' '=' | '<' | '>' )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:754:4: ( '<' '=' | '>' '=' | '<' | '>' )
            int alt139=4;
            int LA139_0 = input.LA(1);

            if ( (LA139_0==33) ) {
                int LA139_1 = input.LA(2);

                if ( (LA139_1==44) ) {
                    alt139=1;
                }
                else if ( (LA139_1==Identifier||(LA139_1>=FloatingPointLiteral && LA139_1<=DecimalLiteral)||LA139_1==33||LA139_1==40||(LA139_1>=55 && LA139_1<=62)||(LA139_1>=64 && LA139_1<=65)||(LA139_1>=68 && LA139_1<=70)||(LA139_1>=104 && LA139_1<=105)||(LA139_1>=108 && LA139_1<=113)) ) {
                    alt139=3;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("754:4: ( '<' '=' | '>' '=' | '<' | '>' )", 139, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA139_0==35) ) {
                int LA139_2 = input.LA(2);

                if ( (LA139_2==44) ) {
                    alt139=2;
                }
                else if ( (LA139_2==Identifier||(LA139_2>=FloatingPointLiteral && LA139_2<=DecimalLiteral)||LA139_2==33||LA139_2==40||(LA139_2>=55 && LA139_2<=62)||(LA139_2>=64 && LA139_2<=65)||(LA139_2>=68 && LA139_2<=70)||(LA139_2>=104 && LA139_2<=105)||(LA139_2>=108 && LA139_2<=113)) ) {
                    alt139=4;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("754:4: ( '<' '=' | '>' '=' | '<' | '>' )", 139, 2, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("754:4: ( '<' '=' | '>' '=' | '<' | '>' )", 139, 0, input);

                throw nvae;
            }
            switch (alt139) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:754:5: '<' '='
                    {
                    match(input,33,FOLLOW_33_in_relationalOp3224); if (failed) return ;
                    match(input,44,FOLLOW_44_in_relationalOp3226); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:754:15: '>' '='
                    {
                    match(input,35,FOLLOW_35_in_relationalOp3230); if (failed) return ;
                    match(input,44,FOLLOW_44_in_relationalOp3232); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:754:25: '<'
                    {
                    match(input,33,FOLLOW_33_in_relationalOp3236); if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:754:31: '>'
                    {
                    match(input,35,FOLLOW_35_in_relationalOp3240); if (failed) return ;

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
            if ( backtracking>0 ) { memoize(input, 110, relationalOp_StartIndex); }
        }
        return ;
    }
    // $ANTLR end relationalOp


    // $ANTLR start shiftExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:757:1: shiftExpression : additiveExpression ( shiftOp additiveExpression )* ;
    public final void shiftExpression() throws RecognitionException {
        int shiftExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 111) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:758:9: ( additiveExpression ( shiftOp additiveExpression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:758:9: additiveExpression ( shiftOp additiveExpression )*
            {
            pushFollow(FOLLOW_additiveExpression_in_shiftExpression3257);
            additiveExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:758:28: ( shiftOp additiveExpression )*
            loop140:
            do {
                int alt140=2;
                int LA140_0 = input.LA(1);

                if ( (LA140_0==33) ) {
                    int LA140_1 = input.LA(2);

                    if ( (LA140_1==33) ) {
                        int LA140_27 = input.LA(3);

                        if ( (synpred209()) ) {
                            alt140=1;
                        }


                    }


                }
                else if ( (LA140_0==35) ) {
                    int LA140_2 = input.LA(2);

                    if ( (LA140_2==35) ) {
                        int LA140_48 = input.LA(3);

                        if ( (synpred209()) ) {
                            alt140=1;
                        }


                    }


                }


                switch (alt140) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:758:30: shiftOp additiveExpression
            	    {
            	    pushFollow(FOLLOW_shiftOp_in_shiftExpression3261);
            	    shiftOp();
            	    _fsp--;
            	    if (failed) return ;
            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression3263);
            	    additiveExpression();
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
            if ( backtracking>0 ) { memoize(input, 111, shiftExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end shiftExpression


    // $ANTLR start shiftOp
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:762:1: shiftOp : ( '<' '<' | '>' '>' '>' | '>' '>' ) ;
    public final void shiftOp() throws RecognitionException {
        int shiftOp_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 112) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:763:4: ( ( '<' '<' | '>' '>' '>' | '>' '>' ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:763:4: ( '<' '<' | '>' '>' '>' | '>' '>' )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:763:4: ( '<' '<' | '>' '>' '>' | '>' '>' )
            int alt141=3;
            int LA141_0 = input.LA(1);

            if ( (LA141_0==33) ) {
                alt141=1;
            }
            else if ( (LA141_0==35) ) {
                int LA141_2 = input.LA(2);

                if ( (LA141_2==35) ) {
                    int LA141_3 = input.LA(3);

                    if ( (synpred211()) ) {
                        alt141=2;
                    }
                    else if ( (true) ) {
                        alt141=3;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("763:4: ( '<' '<' | '>' '>' '>' | '>' '>' )", 141, 3, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("763:4: ( '<' '<' | '>' '>' '>' | '>' '>' )", 141, 2, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("763:4: ( '<' '<' | '>' '>' '>' | '>' '>' )", 141, 0, input);

                throw nvae;
            }
            switch (alt141) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:763:5: '<' '<'
                    {
                    match(input,33,FOLLOW_33_in_shiftOp3287); if (failed) return ;
                    match(input,33,FOLLOW_33_in_shiftOp3289); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:763:15: '>' '>' '>'
                    {
                    match(input,35,FOLLOW_35_in_shiftOp3293); if (failed) return ;
                    match(input,35,FOLLOW_35_in_shiftOp3295); if (failed) return ;
                    match(input,35,FOLLOW_35_in_shiftOp3297); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:763:29: '>' '>'
                    {
                    match(input,35,FOLLOW_35_in_shiftOp3301); if (failed) return ;
                    match(input,35,FOLLOW_35_in_shiftOp3303); if (failed) return ;

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
            if ( backtracking>0 ) { memoize(input, 112, shiftOp_StartIndex); }
        }
        return ;
    }
    // $ANTLR end shiftOp


    // $ANTLR start additiveExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:767:1: additiveExpression : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* ;
    public final void additiveExpression() throws RecognitionException {
        int additiveExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 113) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:768:9: ( multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:768:9: multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression3321);
            multiplicativeExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:768:34: ( ( '+' | '-' ) multiplicativeExpression )*
            loop142:
            do {
                int alt142=2;
                int LA142_0 = input.LA(1);

                if ( ((LA142_0>=104 && LA142_0<=105)) ) {
                    alt142=1;
                }


                switch (alt142) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:768:36: ( '+' | '-' ) multiplicativeExpression
            	    {
            	    if ( (input.LA(1)>=104 && input.LA(1)<=105) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_additiveExpression3325);    throw mse;
            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression3333);
            	    multiplicativeExpression();
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
            if ( backtracking>0 ) { memoize(input, 113, additiveExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end additiveExpression


    // $ANTLR start multiplicativeExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:771:1: multiplicativeExpression : unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* ;
    public final void multiplicativeExpression() throws RecognitionException {
        int multiplicativeExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 114) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:772:9: ( unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:772:9: unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
            {
            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression3352);
            unaryExpression();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:772:25: ( ( '*' | '/' | '%' ) unaryExpression )*
            loop143:
            do {
                int alt143=2;
                int LA143_0 = input.LA(1);

                if ( (LA143_0==29||(LA143_0>=106 && LA143_0<=107)) ) {
                    alt143=1;
                }


                switch (alt143) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:772:27: ( '*' | '/' | '%' ) unaryExpression
            	    {
            	    if ( input.LA(1)==29||(input.LA(1)>=106 && input.LA(1)<=107) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_multiplicativeExpression3356);    throw mse;
            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression3370);
            	    unaryExpression();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop143;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 114, multiplicativeExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end multiplicativeExpression


    // $ANTLR start unaryExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:775:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' primary | '--' primary | unaryExpressionNotPlusMinus );
    public final void unaryExpression() throws RecognitionException {
        int unaryExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 115) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:776:9: ( '+' unaryExpression | '-' unaryExpression | '++' primary | '--' primary | unaryExpressionNotPlusMinus )
            int alt144=5;
            switch ( input.LA(1) ) {
            case 104:
                {
                alt144=1;
                }
                break;
            case 105:
                {
                alt144=2;
                }
                break;
            case 108:
                {
                alt144=3;
                }
                break;
            case 109:
                {
                alt144=4;
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
            case 110:
            case 111:
            case 112:
            case 113:
                {
                alt144=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("775:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' primary | '--' primary | unaryExpressionNotPlusMinus );", 144, 0, input);

                throw nvae;
            }

            switch (alt144) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:776:9: '+' unaryExpression
                    {
                    match(input,104,FOLLOW_104_in_unaryExpression3390); if (failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression3392);
                    unaryExpression();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:777:7: '-' unaryExpression
                    {
                    match(input,105,FOLLOW_105_in_unaryExpression3400); if (failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression3402);
                    unaryExpression();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:778:9: '++' primary
                    {
                    match(input,108,FOLLOW_108_in_unaryExpression3412); if (failed) return ;
                    pushFollow(FOLLOW_primary_in_unaryExpression3414);
                    primary();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:779:9: '--' primary
                    {
                    match(input,109,FOLLOW_109_in_unaryExpression3424); if (failed) return ;
                    pushFollow(FOLLOW_primary_in_unaryExpression3426);
                    primary();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:780:9: unaryExpressionNotPlusMinus
                    {
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression3436);
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
            if ( backtracking>0 ) { memoize(input, 115, unaryExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end unaryExpression


    // $ANTLR start unaryExpressionNotPlusMinus
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );
    public final void unaryExpressionNotPlusMinus() throws RecognitionException {
        int unaryExpressionNotPlusMinus_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 116) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:784:9: ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? )
            int alt147=4;
            switch ( input.LA(1) ) {
            case 110:
                {
                alt147=1;
                }
                break;
            case 111:
                {
                alt147=2;
                }
                break;
            case 65:
                {
                switch ( input.LA(2) ) {
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                    {
                    int LA147_17 = input.LA(3);

                    if ( (synpred223()) ) {
                        alt147=3;
                    }
                    else if ( (true) ) {
                        alt147=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 147, 17, input);

                        throw nvae;
                    }
                    }
                    break;
                case Identifier:
                    {
                    int LA147_18 = input.LA(3);

                    if ( (synpred223()) ) {
                        alt147=3;
                    }
                    else if ( (true) ) {
                        alt147=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 147, 18, input);

                        throw nvae;
                    }
                    }
                    break;
                case 104:
                    {
                    int LA147_19 = input.LA(3);

                    if ( (synpred223()) ) {
                        alt147=3;
                    }
                    else if ( (true) ) {
                        alt147=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 147, 19, input);

                        throw nvae;
                    }
                    }
                    break;
                case 105:
                    {
                    int LA147_20 = input.LA(3);

                    if ( (synpred223()) ) {
                        alt147=3;
                    }
                    else if ( (true) ) {
                        alt147=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 147, 20, input);

                        throw nvae;
                    }
                    }
                    break;
                case 108:
                    {
                    int LA147_21 = input.LA(3);

                    if ( (synpred223()) ) {
                        alt147=3;
                    }
                    else if ( (true) ) {
                        alt147=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 147, 21, input);

                        throw nvae;
                    }
                    }
                    break;
                case 109:
                    {
                    int LA147_22 = input.LA(3);

                    if ( (synpred223()) ) {
                        alt147=3;
                    }
                    else if ( (true) ) {
                        alt147=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 147, 22, input);

                        throw nvae;
                    }
                    }
                    break;
                case 110:
                    {
                    int LA147_23 = input.LA(3);

                    if ( (synpred223()) ) {
                        alt147=3;
                    }
                    else if ( (true) ) {
                        alt147=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 147, 23, input);

                        throw nvae;
                    }
                    }
                    break;
                case 111:
                    {
                    int LA147_24 = input.LA(3);

                    if ( (synpred223()) ) {
                        alt147=3;
                    }
                    else if ( (true) ) {
                        alt147=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 147, 24, input);

                        throw nvae;
                    }
                    }
                    break;
                case 65:
                    {
                    int LA147_25 = input.LA(3);

                    if ( (synpred223()) ) {
                        alt147=3;
                    }
                    else if ( (true) ) {
                        alt147=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 147, 25, input);

                        throw nvae;
                    }
                    }
                    break;
                case 33:
                    {
                    int LA147_26 = input.LA(3);

                    if ( (synpred223()) ) {
                        alt147=3;
                    }
                    else if ( (true) ) {
                        alt147=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 147, 26, input);

                        throw nvae;
                    }
                    }
                    break;
                case 112:
                    {
                    int LA147_27 = input.LA(3);

                    if ( (synpred223()) ) {
                        alt147=3;
                    }
                    else if ( (true) ) {
                        alt147=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 147, 27, input);

                        throw nvae;
                    }
                    }
                    break;
                case 64:
                    {
                    int LA147_28 = input.LA(3);

                    if ( (synpred223()) ) {
                        alt147=3;
                    }
                    else if ( (true) ) {
                        alt147=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 147, 28, input);

                        throw nvae;
                    }
                    }
                    break;
                case HexLiteral:
                case OctalLiteral:
                case DecimalLiteral:
                    {
                    int LA147_29 = input.LA(3);

                    if ( (synpred223()) ) {
                        alt147=3;
                    }
                    else if ( (true) ) {
                        alt147=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 147, 29, input);

                        throw nvae;
                    }
                    }
                    break;
                case FloatingPointLiteral:
                    {
                    int LA147_30 = input.LA(3);

                    if ( (synpred223()) ) {
                        alt147=3;
                    }
                    else if ( (true) ) {
                        alt147=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 147, 30, input);

                        throw nvae;
                    }
                    }
                    break;
                case CharacterLiteral:
                    {
                    int LA147_31 = input.LA(3);

                    if ( (synpred223()) ) {
                        alt147=3;
                    }
                    else if ( (true) ) {
                        alt147=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 147, 31, input);

                        throw nvae;
                    }
                    }
                    break;
                case StringLiteral:
                    {
                    int LA147_32 = input.LA(3);

                    if ( (synpred223()) ) {
                        alt147=3;
                    }
                    else if ( (true) ) {
                        alt147=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 147, 32, input);

                        throw nvae;
                    }
                    }
                    break;
                case 69:
                case 70:
                    {
                    int LA147_33 = input.LA(3);

                    if ( (synpred223()) ) {
                        alt147=3;
                    }
                    else if ( (true) ) {
                        alt147=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 147, 33, input);

                        throw nvae;
                    }
                    }
                    break;
                case 68:
                    {
                    int LA147_34 = input.LA(3);

                    if ( (synpred223()) ) {
                        alt147=3;
                    }
                    else if ( (true) ) {
                        alt147=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 147, 34, input);

                        throw nvae;
                    }
                    }
                    break;
                case 113:
                    {
                    int LA147_35 = input.LA(3);

                    if ( (synpred223()) ) {
                        alt147=3;
                    }
                    else if ( (true) ) {
                        alt147=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 147, 35, input);

                        throw nvae;
                    }
                    }
                    break;
                case 40:
                    {
                    int LA147_36 = input.LA(3);

                    if ( (synpred223()) ) {
                        alt147=3;
                    }
                    else if ( (true) ) {
                        alt147=4;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 147, 36, input);

                        throw nvae;
                    }
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 147, 3, input);

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
            case 112:
            case 113:
                {
                alt147=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("783:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );", 147, 0, input);

                throw nvae;
            }

            switch (alt147) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:784:9: '~' unaryExpression
                    {
                    match(input,110,FOLLOW_110_in_unaryExpressionNotPlusMinus3455); if (failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus3457);
                    unaryExpression();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:785:8: '!' unaryExpression
                    {
                    match(input,111,FOLLOW_111_in_unaryExpressionNotPlusMinus3466); if (failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus3468);
                    unaryExpression();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:786:9: castExpression
                    {
                    pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus3478);
                    castExpression();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:787:9: primary ( selector )* ( '++' | '--' )?
                    {
                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus3488);
                    primary();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:787:17: ( selector )*
                    loop145:
                    do {
                        int alt145=2;
                        int LA145_0 = input.LA(1);

                        if ( (LA145_0==28||LA145_0==41) ) {
                            alt145=1;
                        }


                        switch (alt145) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: selector
                    	    {
                    	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus3490);
                    	    selector();
                    	    _fsp--;
                    	    if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop145;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:787:27: ( '++' | '--' )?
                    int alt146=2;
                    int LA146_0 = input.LA(1);

                    if ( ((LA146_0>=108 && LA146_0<=109)) ) {
                        alt146=1;
                    }
                    switch (alt146) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:
                            {
                            if ( (input.LA(1)>=108 && input.LA(1)<=109) ) {
                                input.consume();
                                errorRecovery=false;failed=false;
                            }
                            else {
                                if (backtracking>0) {failed=true; return ;}
                                MismatchedSetException mse =
                                    new MismatchedSetException(null,input);
                                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_unaryExpressionNotPlusMinus3493);    throw mse;
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
            if ( backtracking>0 ) { memoize(input, 116, unaryExpressionNotPlusMinus_StartIndex); }
        }
        return ;
    }
    // $ANTLR end unaryExpressionNotPlusMinus


    // $ANTLR start castExpression
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:790:1: castExpression : ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus );
    public final void castExpression() throws RecognitionException {
        int castExpression_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 117) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:791:8: ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus )
            int alt149=2;
            int LA149_0 = input.LA(1);

            if ( (LA149_0==65) ) {
                int LA149_1 = input.LA(2);

                if ( ((LA149_1>=55 && LA149_1<=62)) ) {
                    int LA149_2 = input.LA(3);

                    if ( (synpred227()) ) {
                        alt149=1;
                    }
                    else if ( (true) ) {
                        alt149=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("790:1: castExpression : ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus );", 149, 2, input);

                        throw nvae;
                    }
                }
                else if ( (LA149_1==Identifier||(LA149_1>=FloatingPointLiteral && LA149_1<=DecimalLiteral)||LA149_1==33||LA149_1==40||(LA149_1>=64 && LA149_1<=65)||(LA149_1>=68 && LA149_1<=70)||(LA149_1>=104 && LA149_1<=105)||(LA149_1>=108 && LA149_1<=113)) ) {
                    alt149=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("790:1: castExpression : ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus );", 149, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("790:1: castExpression : ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus );", 149, 0, input);

                throw nvae;
            }
            switch (alt149) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:791:8: '(' primitiveType ')' unaryExpression
                    {
                    match(input,65,FOLLOW_65_in_castExpression3516); if (failed) return ;
                    pushFollow(FOLLOW_primitiveType_in_castExpression3518);
                    primitiveType();
                    _fsp--;
                    if (failed) return ;
                    match(input,66,FOLLOW_66_in_castExpression3520); if (failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_castExpression3522);
                    unaryExpression();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:792:8: '(' ( type | expression ) ')' unaryExpressionNotPlusMinus
                    {
                    match(input,65,FOLLOW_65_in_castExpression3531); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:792:12: ( type | expression )
                    int alt148=2;
                    switch ( input.LA(1) ) {
                    case Identifier:
                        {
                        int LA148_1 = input.LA(2);

                        if ( (synpred228()) ) {
                            alt148=1;
                        }
                        else if ( (true) ) {
                            alt148=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("792:12: ( type | expression )", 148, 1, input);

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
                            int LA148_48 = input.LA(3);

                            if ( (synpred228()) ) {
                                alt148=1;
                            }
                            else if ( (true) ) {
                                alt148=2;
                            }
                            else {
                                if (backtracking>0) {failed=true; return ;}
                                NoViableAltException nvae =
                                    new NoViableAltException("792:12: ( type | expression )", 148, 48, input);

                                throw nvae;
                            }
                            }
                            break;
                        case 28:
                            {
                            alt148=2;
                            }
                            break;
                        case 66:
                            {
                            alt148=1;
                            }
                            break;
                        default:
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("792:12: ( type | expression )", 148, 2, input);

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
                    case 104:
                    case 105:
                    case 108:
                    case 109:
                    case 110:
                    case 111:
                    case 112:
                    case 113:
                        {
                        alt148=2;
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("792:12: ( type | expression )", 148, 0, input);

                        throw nvae;
                    }

                    switch (alt148) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:792:13: type
                            {
                            pushFollow(FOLLOW_type_in_castExpression3534);
                            type();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;
                        case 2 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:792:20: expression
                            {
                            pushFollow(FOLLOW_expression_in_castExpression3538);
                            expression();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    match(input,66,FOLLOW_66_in_castExpression3541); if (failed) return ;
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression3543);
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
            if ( backtracking>0 ) { memoize(input, 117, castExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end castExpression


    // $ANTLR start primary
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:795:1: primary : ( parExpression | nonWildcardTypeArguments ( explicitGenericInvocationSuffix | 'this' arguments ) | 'this' ( '.' Identifier )* ( identifierSuffix )? | 'super' superSuffix | literal | 'new' creator | i= Identifier ( '.' Identifier )* ( identifierSuffix )? | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );
    public final void primary() throws RecognitionException {
        int primary_StartIndex = input.index();
        Token i=null;

        try {
            if ( backtracking>0 && alreadyParsedRule(input, 118) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:796:7: ( parExpression | nonWildcardTypeArguments ( explicitGenericInvocationSuffix | 'this' arguments ) | 'this' ( '.' Identifier )* ( identifierSuffix )? | 'super' superSuffix | literal | 'new' creator | i= Identifier ( '.' Identifier )* ( identifierSuffix )? | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' )
            int alt156=9;
            switch ( input.LA(1) ) {
            case 65:
                {
                alt156=1;
                }
                break;
            case 33:
                {
                alt156=2;
                }
                break;
            case 112:
                {
                alt156=3;
                }
                break;
            case 64:
                {
                alt156=4;
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
                alt156=5;
                }
                break;
            case 113:
                {
                alt156=6;
                }
                break;
            case Identifier:
                {
                alt156=7;
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
                alt156=8;
                }
                break;
            case 40:
                {
                alt156=9;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("795:1: primary : ( parExpression | nonWildcardTypeArguments ( explicitGenericInvocationSuffix | 'this' arguments ) | 'this' ( '.' Identifier )* ( identifierSuffix )? | 'super' superSuffix | literal | 'new' creator | i= Identifier ( '.' Identifier )* ( identifierSuffix )? | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );", 156, 0, input);

                throw nvae;
            }

            switch (alt156) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:796:7: parExpression
                    {
                    pushFollow(FOLLOW_parExpression_in_primary3560);
                    parExpression();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:797:9: nonWildcardTypeArguments ( explicitGenericInvocationSuffix | 'this' arguments )
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_primary3570);
                    nonWildcardTypeArguments();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:798:9: ( explicitGenericInvocationSuffix | 'this' arguments )
                    int alt150=2;
                    int LA150_0 = input.LA(1);

                    if ( (LA150_0==Identifier||LA150_0==64) ) {
                        alt150=1;
                    }
                    else if ( (LA150_0==112) ) {
                        alt150=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("798:9: ( explicitGenericInvocationSuffix | 'this' arguments )", 150, 0, input);

                        throw nvae;
                    }
                    switch (alt150) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:798:10: explicitGenericInvocationSuffix
                            {
                            pushFollow(FOLLOW_explicitGenericInvocationSuffix_in_primary3581);
                            explicitGenericInvocationSuffix();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;
                        case 2 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:798:44: 'this' arguments
                            {
                            match(input,112,FOLLOW_112_in_primary3585); if (failed) return ;
                            pushFollow(FOLLOW_arguments_in_primary3587);
                            arguments();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:799:9: 'this' ( '.' Identifier )* ( identifierSuffix )?
                    {
                    match(input,112,FOLLOW_112_in_primary3598); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:799:16: ( '.' Identifier )*
                    loop151:
                    do {
                        int alt151=2;
                        int LA151_0 = input.LA(1);

                        if ( (LA151_0==28) ) {
                            int LA151_3 = input.LA(2);

                            if ( (LA151_3==Identifier) ) {
                                int LA151_35 = input.LA(3);

                                if ( (synpred232()) ) {
                                    alt151=1;
                                }


                            }


                        }


                        switch (alt151) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:799:17: '.' Identifier
                    	    {
                    	    match(input,28,FOLLOW_28_in_primary3601); if (failed) return ;
                    	    match(input,Identifier,FOLLOW_Identifier_in_primary3603); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop151;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:799:34: ( identifierSuffix )?
                    int alt152=2;
                    switch ( input.LA(1) ) {
                        case 41:
                            {
                            switch ( input.LA(2) ) {
                                case 42:
                                    {
                                    alt152=1;
                                    }
                                    break;
                                case 104:
                                    {
                                    int LA152_34 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
                                    }
                                    }
                                    break;
                                case 105:
                                    {
                                    int LA152_35 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
                                    }
                                    }
                                    break;
                                case 108:
                                    {
                                    int LA152_36 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
                                    }
                                    }
                                    break;
                                case 109:
                                    {
                                    int LA152_37 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
                                    }
                                    }
                                    break;
                                case 110:
                                    {
                                    int LA152_38 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
                                    }
                                    }
                                    break;
                                case 111:
                                    {
                                    int LA152_39 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
                                    }
                                    }
                                    break;
                                case 65:
                                    {
                                    int LA152_40 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
                                    }
                                    }
                                    break;
                                case 33:
                                    {
                                    int LA152_41 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
                                    }
                                    }
                                    break;
                                case 112:
                                    {
                                    int LA152_42 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
                                    }
                                    }
                                    break;
                                case 64:
                                    {
                                    int LA152_43 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
                                    }
                                    }
                                    break;
                                case HexLiteral:
                                case OctalLiteral:
                                case DecimalLiteral:
                                    {
                                    int LA152_44 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
                                    }
                                    }
                                    break;
                                case FloatingPointLiteral:
                                    {
                                    int LA152_45 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
                                    }
                                    }
                                    break;
                                case CharacterLiteral:
                                    {
                                    int LA152_46 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
                                    }
                                    }
                                    break;
                                case StringLiteral:
                                    {
                                    int LA152_47 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
                                    }
                                    }
                                    break;
                                case 69:
                                case 70:
                                    {
                                    int LA152_48 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
                                    }
                                    }
                                    break;
                                case 68:
                                    {
                                    int LA152_49 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
                                    }
                                    }
                                    break;
                                case 113:
                                    {
                                    int LA152_50 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
                                    }
                                    }
                                    break;
                                case Identifier:
                                    {
                                    int LA152_51 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
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
                                    int LA152_52 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
                                    }
                                    }
                                    break;
                                case 40:
                                    {
                                    int LA152_53 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
                                    }
                                    }
                                    break;
                            }

                            }
                            break;
                        case 65:
                            {
                            alt152=1;
                            }
                            break;
                        case 28:
                            {
                            switch ( input.LA(2) ) {
                                case 112:
                                    {
                                    int LA152_54 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
                                    }
                                    }
                                    break;
                                case 30:
                                case 33:
                                    {
                                    alt152=1;
                                    }
                                    break;
                                case 64:
                                    {
                                    int LA152_56 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
                                    }
                                    }
                                    break;
                                case 113:
                                    {
                                    int LA152_57 = input.LA(3);

                                    if ( (synpred233()) ) {
                                        alt152=1;
                                    }
                                    }
                                    break;
                            }

                            }
                            break;
                    }

                    switch (alt152) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:799:35: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary3608);
                            identifierSuffix();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:800:9: 'super' superSuffix
                    {
                    match(input,64,FOLLOW_64_in_primary3620); if (failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_primary3622);
                    superSuffix();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:801:9: literal
                    {
                    pushFollow(FOLLOW_literal_in_primary3632);
                    literal();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:802:9: 'new' creator
                    {
                    match(input,113,FOLLOW_113_in_primary3642); if (failed) return ;
                    pushFollow(FOLLOW_creator_in_primary3644);
                    creator();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:803:9: i= Identifier ( '.' Identifier )* ( identifierSuffix )?
                    {
                    i=(Token)input.LT(1);
                    match(input,Identifier,FOLLOW_Identifier_in_primary3656); if (failed) return ;
                    if ( backtracking==0 ) {
                       identifiers.add( i.getText() );  
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:803:55: ( '.' Identifier )*
                    loop153:
                    do {
                        int alt153=2;
                        int LA153_0 = input.LA(1);

                        if ( (LA153_0==28) ) {
                            int LA153_3 = input.LA(2);

                            if ( (LA153_3==Identifier) ) {
                                int LA153_36 = input.LA(3);

                                if ( (synpred238()) ) {
                                    alt153=1;
                                }


                            }


                        }


                        switch (alt153) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:803:56: '.' Identifier
                    	    {
                    	    match(input,28,FOLLOW_28_in_primary3661); if (failed) return ;
                    	    match(input,Identifier,FOLLOW_Identifier_in_primary3663); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop153;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:803:73: ( identifierSuffix )?
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
                                case 104:
                                    {
                                    int LA154_34 = input.LA(3);

                                    if ( (synpred239()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 105:
                                    {
                                    int LA154_35 = input.LA(3);

                                    if ( (synpred239()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 108:
                                    {
                                    int LA154_36 = input.LA(3);

                                    if ( (synpred239()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 109:
                                    {
                                    int LA154_37 = input.LA(3);

                                    if ( (synpred239()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 110:
                                    {
                                    int LA154_38 = input.LA(3);

                                    if ( (synpred239()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 111:
                                    {
                                    int LA154_39 = input.LA(3);

                                    if ( (synpred239()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 65:
                                    {
                                    int LA154_40 = input.LA(3);

                                    if ( (synpred239()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 33:
                                    {
                                    int LA154_41 = input.LA(3);

                                    if ( (synpred239()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 112:
                                    {
                                    int LA154_42 = input.LA(3);

                                    if ( (synpred239()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 64:
                                    {
                                    int LA154_43 = input.LA(3);

                                    if ( (synpred239()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case HexLiteral:
                                case OctalLiteral:
                                case DecimalLiteral:
                                    {
                                    int LA154_44 = input.LA(3);

                                    if ( (synpred239()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case FloatingPointLiteral:
                                    {
                                    int LA154_45 = input.LA(3);

                                    if ( (synpred239()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case CharacterLiteral:
                                    {
                                    int LA154_46 = input.LA(3);

                                    if ( (synpred239()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case StringLiteral:
                                    {
                                    int LA154_47 = input.LA(3);

                                    if ( (synpred239()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 69:
                                case 70:
                                    {
                                    int LA154_48 = input.LA(3);

                                    if ( (synpred239()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 68:
                                    {
                                    int LA154_49 = input.LA(3);

                                    if ( (synpred239()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 113:
                                    {
                                    int LA154_50 = input.LA(3);

                                    if ( (synpred239()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case Identifier:
                                    {
                                    int LA154_51 = input.LA(3);

                                    if ( (synpred239()) ) {
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

                                    if ( (synpred239()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 40:
                                    {
                                    int LA154_53 = input.LA(3);

                                    if ( (synpred239()) ) {
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
                                case 64:
                                    {
                                    int LA154_54 = input.LA(3);

                                    if ( (synpred239()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 113:
                                    {
                                    int LA154_55 = input.LA(3);

                                    if ( (synpred239()) ) {
                                        alt154=1;
                                    }
                                    }
                                    break;
                                case 112:
                                    {
                                    int LA154_56 = input.LA(3);

                                    if ( (synpred239()) ) {
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
                            }

                            }
                            break;
                    }

                    switch (alt154) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:803:74: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary3668);
                            identifierSuffix();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:804:9: primitiveType ( '[' ']' )* '.' 'class'
                    {
                    pushFollow(FOLLOW_primitiveType_in_primary3680);
                    primitiveType();
                    _fsp--;
                    if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:804:23: ( '[' ']' )*
                    loop155:
                    do {
                        int alt155=2;
                        int LA155_0 = input.LA(1);

                        if ( (LA155_0==41) ) {
                            alt155=1;
                        }


                        switch (alt155) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:804:24: '[' ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_primary3683); if (failed) return ;
                    	    match(input,42,FOLLOW_42_in_primary3685); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop155;
                        }
                    } while (true);

                    match(input,28,FOLLOW_28_in_primary3689); if (failed) return ;
                    match(input,30,FOLLOW_30_in_primary3691); if (failed) return ;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:805:9: 'void' '.' 'class'
                    {
                    match(input,40,FOLLOW_40_in_primary3701); if (failed) return ;
                    match(input,28,FOLLOW_28_in_primary3703); if (failed) return ;
                    match(input,30,FOLLOW_30_in_primary3705); if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 118, primary_StartIndex); }
        }
        return ;
    }
    // $ANTLR end primary


    // $ANTLR start identifierSuffix
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:808:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' ( nonWildcardTypeArguments )? innerCreator );
    public final void identifierSuffix() throws RecognitionException {
        int identifierSuffix_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 119) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:809:4: ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' ( nonWildcardTypeArguments )? innerCreator )
            int alt160=8;
            switch ( input.LA(1) ) {
            case 41:
                {
                int LA160_1 = input.LA(2);

                if ( (LA160_1==42) ) {
                    alt160=1;
                }
                else if ( (LA160_1==Identifier||(LA160_1>=FloatingPointLiteral && LA160_1<=DecimalLiteral)||LA160_1==33||LA160_1==40||(LA160_1>=55 && LA160_1<=62)||(LA160_1>=64 && LA160_1<=65)||(LA160_1>=68 && LA160_1<=70)||(LA160_1>=104 && LA160_1<=105)||(LA160_1>=108 && LA160_1<=113)) ) {
                    alt160=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("808:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' ( nonWildcardTypeArguments )? innerCreator );", 160, 1, input);

                    throw nvae;
                }
                }
                break;
            case 65:
                {
                alt160=3;
                }
                break;
            case 28:
                {
                switch ( input.LA(2) ) {
                case 113:
                    {
                    alt160=8;
                    }
                    break;
                case 64:
                    {
                    alt160=7;
                    }
                    break;
                case 30:
                    {
                    alt160=4;
                    }
                    break;
                case 112:
                    {
                    alt160=6;
                    }
                    break;
                case 33:
                    {
                    alt160=5;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("808:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' ( nonWildcardTypeArguments )? innerCreator );", 160, 3, input);

                    throw nvae;
                }

                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("808:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' ( nonWildcardTypeArguments )? innerCreator );", 160, 0, input);

                throw nvae;
            }

            switch (alt160) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:809:4: ( '[' ']' )+ '.' 'class'
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:809:4: ( '[' ']' )+
                    int cnt157=0;
                    loop157:
                    do {
                        int alt157=2;
                        int LA157_0 = input.LA(1);

                        if ( (LA157_0==41) ) {
                            alt157=1;
                        }


                        switch (alt157) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:809:5: '[' ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_identifierSuffix3717); if (failed) return ;
                    	    match(input,42,FOLLOW_42_in_identifierSuffix3719); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt157 >= 1 ) break loop157;
                    	    if (backtracking>0) {failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(157, input);
                                throw eee;
                        }
                        cnt157++;
                    } while (true);

                    match(input,28,FOLLOW_28_in_identifierSuffix3723); if (failed) return ;
                    match(input,30,FOLLOW_30_in_identifierSuffix3725); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:810:4: ( '[' expression ']' )+
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:810:4: ( '[' expression ']' )+
                    int cnt158=0;
                    loop158:
                    do {
                        int alt158=2;
                        int LA158_0 = input.LA(1);

                        if ( (LA158_0==41) ) {
                            switch ( input.LA(2) ) {
                            case 104:
                                {
                                int LA158_32 = input.LA(3);

                                if ( (synpred245()) ) {
                                    alt158=1;
                                }


                                }
                                break;
                            case 105:
                                {
                                int LA158_33 = input.LA(3);

                                if ( (synpred245()) ) {
                                    alt158=1;
                                }


                                }
                                break;
                            case 108:
                                {
                                int LA158_34 = input.LA(3);

                                if ( (synpred245()) ) {
                                    alt158=1;
                                }


                                }
                                break;
                            case 109:
                                {
                                int LA158_35 = input.LA(3);

                                if ( (synpred245()) ) {
                                    alt158=1;
                                }


                                }
                                break;
                            case 110:
                                {
                                int LA158_36 = input.LA(3);

                                if ( (synpred245()) ) {
                                    alt158=1;
                                }


                                }
                                break;
                            case 111:
                                {
                                int LA158_37 = input.LA(3);

                                if ( (synpred245()) ) {
                                    alt158=1;
                                }


                                }
                                break;
                            case 65:
                                {
                                int LA158_38 = input.LA(3);

                                if ( (synpred245()) ) {
                                    alt158=1;
                                }


                                }
                                break;
                            case 33:
                                {
                                int LA158_39 = input.LA(3);

                                if ( (synpred245()) ) {
                                    alt158=1;
                                }


                                }
                                break;
                            case 112:
                                {
                                int LA158_40 = input.LA(3);

                                if ( (synpred245()) ) {
                                    alt158=1;
                                }


                                }
                                break;
                            case 64:
                                {
                                int LA158_41 = input.LA(3);

                                if ( (synpred245()) ) {
                                    alt158=1;
                                }


                                }
                                break;
                            case HexLiteral:
                            case OctalLiteral:
                            case DecimalLiteral:
                                {
                                int LA158_42 = input.LA(3);

                                if ( (synpred245()) ) {
                                    alt158=1;
                                }


                                }
                                break;
                            case FloatingPointLiteral:
                                {
                                int LA158_43 = input.LA(3);

                                if ( (synpred245()) ) {
                                    alt158=1;
                                }


                                }
                                break;
                            case CharacterLiteral:
                                {
                                int LA158_44 = input.LA(3);

                                if ( (synpred245()) ) {
                                    alt158=1;
                                }


                                }
                                break;
                            case StringLiteral:
                                {
                                int LA158_45 = input.LA(3);

                                if ( (synpred245()) ) {
                                    alt158=1;
                                }


                                }
                                break;
                            case 69:
                            case 70:
                                {
                                int LA158_46 = input.LA(3);

                                if ( (synpred245()) ) {
                                    alt158=1;
                                }


                                }
                                break;
                            case 68:
                                {
                                int LA158_47 = input.LA(3);

                                if ( (synpred245()) ) {
                                    alt158=1;
                                }


                                }
                                break;
                            case 113:
                                {
                                int LA158_48 = input.LA(3);

                                if ( (synpred245()) ) {
                                    alt158=1;
                                }


                                }
                                break;
                            case Identifier:
                                {
                                int LA158_49 = input.LA(3);

                                if ( (synpred245()) ) {
                                    alt158=1;
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
                                int LA158_50 = input.LA(3);

                                if ( (synpred245()) ) {
                                    alt158=1;
                                }


                                }
                                break;
                            case 40:
                                {
                                int LA158_51 = input.LA(3);

                                if ( (synpred245()) ) {
                                    alt158=1;
                                }


                                }
                                break;

                            }

                        }


                        switch (alt158) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:810:5: '[' expression ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_identifierSuffix3731); if (failed) return ;
                    	    pushFollow(FOLLOW_expression_in_identifierSuffix3733);
                    	    expression();
                    	    _fsp--;
                    	    if (failed) return ;
                    	    match(input,42,FOLLOW_42_in_identifierSuffix3735); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt158 >= 1 ) break loop158;
                    	    if (backtracking>0) {failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(158, input);
                                throw eee;
                        }
                        cnt158++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:811:9: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_identifierSuffix3748);
                    arguments();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:812:9: '.' 'class'
                    {
                    match(input,28,FOLLOW_28_in_identifierSuffix3758); if (failed) return ;
                    match(input,30,FOLLOW_30_in_identifierSuffix3760); if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:813:9: '.' explicitGenericInvocation
                    {
                    match(input,28,FOLLOW_28_in_identifierSuffix3770); if (failed) return ;
                    pushFollow(FOLLOW_explicitGenericInvocation_in_identifierSuffix3772);
                    explicitGenericInvocation();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:814:9: '.' 'this'
                    {
                    match(input,28,FOLLOW_28_in_identifierSuffix3782); if (failed) return ;
                    match(input,112,FOLLOW_112_in_identifierSuffix3784); if (failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:815:9: '.' 'super' arguments
                    {
                    match(input,28,FOLLOW_28_in_identifierSuffix3794); if (failed) return ;
                    match(input,64,FOLLOW_64_in_identifierSuffix3796); if (failed) return ;
                    pushFollow(FOLLOW_arguments_in_identifierSuffix3798);
                    arguments();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:816:9: '.' 'new' ( nonWildcardTypeArguments )? innerCreator
                    {
                    match(input,28,FOLLOW_28_in_identifierSuffix3808); if (failed) return ;
                    match(input,113,FOLLOW_113_in_identifierSuffix3810); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:816:19: ( nonWildcardTypeArguments )?
                    int alt159=2;
                    int LA159_0 = input.LA(1);

                    if ( (LA159_0==33) ) {
                        alt159=1;
                    }
                    switch (alt159) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:816:20: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_identifierSuffix3813);
                            nonWildcardTypeArguments();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_innerCreator_in_identifierSuffix3817);
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
            if ( backtracking>0 ) { memoize(input, 119, identifierSuffix_StartIndex); }
        }
        return ;
    }
    // $ANTLR end identifierSuffix


    // $ANTLR start creator
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:819:1: creator : ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) ;
    public final void creator() throws RecognitionException {
        int creator_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 120) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:820:4: ( ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:820:4: ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:820:4: ( nonWildcardTypeArguments )?
            int alt161=2;
            int LA161_0 = input.LA(1);

            if ( (LA161_0==33) ) {
                alt161=1;
            }
            switch (alt161) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: nonWildcardTypeArguments
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator3829);
                    nonWildcardTypeArguments();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_createdName_in_creator3832);
            createdName();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:821:9: ( arrayCreatorRest | classCreatorRest )
            int alt162=2;
            int LA162_0 = input.LA(1);

            if ( (LA162_0==41) ) {
                alt162=1;
            }
            else if ( (LA162_0==65) ) {
                alt162=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("821:9: ( arrayCreatorRest | classCreatorRest )", 162, 0, input);

                throw nvae;
            }
            switch (alt162) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:821:10: arrayCreatorRest
                    {
                    pushFollow(FOLLOW_arrayCreatorRest_in_creator3843);
                    arrayCreatorRest();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:821:29: classCreatorRest
                    {
                    pushFollow(FOLLOW_classCreatorRest_in_creator3847);
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
            if ( backtracking>0 ) { memoize(input, 120, creator_StartIndex); }
        }
        return ;
    }
    // $ANTLR end creator


    // $ANTLR start createdName
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:824:1: createdName : ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* | primitiveType );
    public final void createdName() throws RecognitionException {
        int createdName_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 121) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:825:4: ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* | primitiveType )
            int alt166=2;
            int LA166_0 = input.LA(1);

            if ( (LA166_0==Identifier) ) {
                alt166=1;
            }
            else if ( ((LA166_0>=55 && LA166_0<=62)) ) {
                alt166=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("824:1: createdName : ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* | primitiveType );", 166, 0, input);

                throw nvae;
            }
            switch (alt166) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:825:4: Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )*
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_createdName3859); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:825:15: ( typeArguments )?
                    int alt163=2;
                    int LA163_0 = input.LA(1);

                    if ( (LA163_0==33) ) {
                        alt163=1;
                    }
                    switch (alt163) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_createdName3861);
                            typeArguments();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:826:9: ( '.' Identifier ( typeArguments )? )*
                    loop165:
                    do {
                        int alt165=2;
                        int LA165_0 = input.LA(1);

                        if ( (LA165_0==28) ) {
                            alt165=1;
                        }


                        switch (alt165) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:826:10: '.' Identifier ( typeArguments )?
                    	    {
                    	    match(input,28,FOLLOW_28_in_createdName3873); if (failed) return ;
                    	    match(input,Identifier,FOLLOW_Identifier_in_createdName3875); if (failed) return ;
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:826:25: ( typeArguments )?
                    	    int alt164=2;
                    	    int LA164_0 = input.LA(1);

                    	    if ( (LA164_0==33) ) {
                    	        alt164=1;
                    	    }
                    	    switch (alt164) {
                    	        case 1 :
                    	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: typeArguments
                    	            {
                    	            pushFollow(FOLLOW_typeArguments_in_createdName3877);
                    	            typeArguments();
                    	            _fsp--;
                    	            if (failed) return ;

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop165;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:827:7: primitiveType
                    {
                    pushFollow(FOLLOW_primitiveType_in_createdName3888);
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
            if ( backtracking>0 ) { memoize(input, 121, createdName_StartIndex); }
        }
        return ;
    }
    // $ANTLR end createdName


    // $ANTLR start innerCreator
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:830:1: innerCreator : Identifier classCreatorRest ;
    public final void innerCreator() throws RecognitionException {
        int innerCreator_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 122) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:831:4: ( Identifier classCreatorRest )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:831:4: Identifier classCreatorRest
            {
            match(input,Identifier,FOLLOW_Identifier_in_innerCreator3900); if (failed) return ;
            pushFollow(FOLLOW_classCreatorRest_in_innerCreator3902);
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
            if ( backtracking>0 ) { memoize(input, 122, innerCreator_StartIndex); }
        }
        return ;
    }
    // $ANTLR end innerCreator


    // $ANTLR start arrayCreatorRest
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:834:1: arrayCreatorRest : '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* ) ;
    public final void arrayCreatorRest() throws RecognitionException {
        int arrayCreatorRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 123) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:835:4: ( '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:835:4: '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* )
            {
            match(input,41,FOLLOW_41_in_arrayCreatorRest3913); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:836:9: ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* )
            int alt170=2;
            int LA170_0 = input.LA(1);

            if ( (LA170_0==42) ) {
                alt170=1;
            }
            else if ( (LA170_0==Identifier||(LA170_0>=FloatingPointLiteral && LA170_0<=DecimalLiteral)||LA170_0==33||LA170_0==40||(LA170_0>=55 && LA170_0<=62)||(LA170_0>=64 && LA170_0<=65)||(LA170_0>=68 && LA170_0<=70)||(LA170_0>=104 && LA170_0<=105)||(LA170_0>=108 && LA170_0<=113)) ) {
                alt170=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("836:9: ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* )", 170, 0, input);

                throw nvae;
            }
            switch (alt170) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:836:13: ']' ( '[' ']' )* arrayInitializer
                    {
                    match(input,42,FOLLOW_42_in_arrayCreatorRest3927); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:836:17: ( '[' ']' )*
                    loop167:
                    do {
                        int alt167=2;
                        int LA167_0 = input.LA(1);

                        if ( (LA167_0==41) ) {
                            alt167=1;
                        }


                        switch (alt167) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:836:18: '[' ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_arrayCreatorRest3930); if (failed) return ;
                    	    match(input,42,FOLLOW_42_in_arrayCreatorRest3932); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop167;
                        }
                    } while (true);

                    pushFollow(FOLLOW_arrayInitializer_in_arrayCreatorRest3936);
                    arrayInitializer();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:837:13: expression ']' ( '[' expression ']' )* ( '[' ']' )*
                    {
                    pushFollow(FOLLOW_expression_in_arrayCreatorRest3950);
                    expression();
                    _fsp--;
                    if (failed) return ;
                    match(input,42,FOLLOW_42_in_arrayCreatorRest3952); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:837:28: ( '[' expression ']' )*
                    loop168:
                    do {
                        int alt168=2;
                        int LA168_0 = input.LA(1);

                        if ( (LA168_0==41) ) {
                            switch ( input.LA(2) ) {
                            case 104:
                                {
                                int LA168_33 = input.LA(3);

                                if ( (synpred261()) ) {
                                    alt168=1;
                                }


                                }
                                break;
                            case 105:
                                {
                                int LA168_34 = input.LA(3);

                                if ( (synpred261()) ) {
                                    alt168=1;
                                }


                                }
                                break;
                            case 108:
                                {
                                int LA168_35 = input.LA(3);

                                if ( (synpred261()) ) {
                                    alt168=1;
                                }


                                }
                                break;
                            case 109:
                                {
                                int LA168_36 = input.LA(3);

                                if ( (synpred261()) ) {
                                    alt168=1;
                                }


                                }
                                break;
                            case 110:
                                {
                                int LA168_37 = input.LA(3);

                                if ( (synpred261()) ) {
                                    alt168=1;
                                }


                                }
                                break;
                            case 111:
                                {
                                int LA168_38 = input.LA(3);

                                if ( (synpred261()) ) {
                                    alt168=1;
                                }


                                }
                                break;
                            case 65:
                                {
                                int LA168_39 = input.LA(3);

                                if ( (synpred261()) ) {
                                    alt168=1;
                                }


                                }
                                break;
                            case 33:
                                {
                                int LA168_40 = input.LA(3);

                                if ( (synpred261()) ) {
                                    alt168=1;
                                }


                                }
                                break;
                            case 112:
                                {
                                int LA168_41 = input.LA(3);

                                if ( (synpred261()) ) {
                                    alt168=1;
                                }


                                }
                                break;
                            case 64:
                                {
                                int LA168_42 = input.LA(3);

                                if ( (synpred261()) ) {
                                    alt168=1;
                                }


                                }
                                break;
                            case HexLiteral:
                            case OctalLiteral:
                            case DecimalLiteral:
                                {
                                int LA168_43 = input.LA(3);

                                if ( (synpred261()) ) {
                                    alt168=1;
                                }


                                }
                                break;
                            case FloatingPointLiteral:
                                {
                                int LA168_44 = input.LA(3);

                                if ( (synpred261()) ) {
                                    alt168=1;
                                }


                                }
                                break;
                            case CharacterLiteral:
                                {
                                int LA168_45 = input.LA(3);

                                if ( (synpred261()) ) {
                                    alt168=1;
                                }


                                }
                                break;
                            case StringLiteral:
                                {
                                int LA168_46 = input.LA(3);

                                if ( (synpred261()) ) {
                                    alt168=1;
                                }


                                }
                                break;
                            case 69:
                            case 70:
                                {
                                int LA168_47 = input.LA(3);

                                if ( (synpred261()) ) {
                                    alt168=1;
                                }


                                }
                                break;
                            case 68:
                                {
                                int LA168_48 = input.LA(3);

                                if ( (synpred261()) ) {
                                    alt168=1;
                                }


                                }
                                break;
                            case 113:
                                {
                                int LA168_49 = input.LA(3);

                                if ( (synpred261()) ) {
                                    alt168=1;
                                }


                                }
                                break;
                            case Identifier:
                                {
                                int LA168_50 = input.LA(3);

                                if ( (synpred261()) ) {
                                    alt168=1;
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
                                int LA168_51 = input.LA(3);

                                if ( (synpred261()) ) {
                                    alt168=1;
                                }


                                }
                                break;
                            case 40:
                                {
                                int LA168_52 = input.LA(3);

                                if ( (synpred261()) ) {
                                    alt168=1;
                                }


                                }
                                break;

                            }

                        }


                        switch (alt168) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:837:29: '[' expression ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_arrayCreatorRest3955); if (failed) return ;
                    	    pushFollow(FOLLOW_expression_in_arrayCreatorRest3957);
                    	    expression();
                    	    _fsp--;
                    	    if (failed) return ;
                    	    match(input,42,FOLLOW_42_in_arrayCreatorRest3959); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop168;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:837:50: ( '[' ']' )*
                    loop169:
                    do {
                        int alt169=2;
                        int LA169_0 = input.LA(1);

                        if ( (LA169_0==41) ) {
                            int LA169_30 = input.LA(2);

                            if ( (LA169_30==42) ) {
                                alt169=1;
                            }


                        }


                        switch (alt169) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:837:51: '[' ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_arrayCreatorRest3964); if (failed) return ;
                    	    match(input,42,FOLLOW_42_in_arrayCreatorRest3966); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop169;
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
            if ( backtracking>0 ) { memoize(input, 123, arrayCreatorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end arrayCreatorRest


    // $ANTLR start classCreatorRest
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:841:1: classCreatorRest : arguments ( classBody )? ;
    public final void classCreatorRest() throws RecognitionException {
        int classCreatorRest_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 124) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:842:4: ( arguments ( classBody )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:842:4: arguments ( classBody )?
            {
            pushFollow(FOLLOW_arguments_in_classCreatorRest3989);
            arguments();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:842:14: ( classBody )?
            int alt171=2;
            int LA171_0 = input.LA(1);

            if ( (LA171_0==37) ) {
                alt171=1;
            }
            switch (alt171) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: classBody
                    {
                    pushFollow(FOLLOW_classBody_in_classCreatorRest3991);
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
            if ( backtracking>0 ) { memoize(input, 124, classCreatorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end classCreatorRest


    // $ANTLR start explicitGenericInvocation
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:845:1: explicitGenericInvocation : nonWildcardTypeArguments explicitGenericInvocationSuffix ;
    public final void explicitGenericInvocation() throws RecognitionException {
        int explicitGenericInvocation_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 125) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:846:4: ( nonWildcardTypeArguments explicitGenericInvocationSuffix )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:846:4: nonWildcardTypeArguments explicitGenericInvocationSuffix
            {
            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation4004);
            nonWildcardTypeArguments();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_explicitGenericInvocationSuffix_in_explicitGenericInvocation4006);
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
            if ( backtracking>0 ) { memoize(input, 125, explicitGenericInvocation_StartIndex); }
        }
        return ;
    }
    // $ANTLR end explicitGenericInvocation


    // $ANTLR start nonWildcardTypeArguments
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:849:1: nonWildcardTypeArguments : '<' typeList '>' ;
    public final void nonWildcardTypeArguments() throws RecognitionException {
        int nonWildcardTypeArguments_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 126) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:850:4: ( '<' typeList '>' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:850:4: '<' typeList '>'
            {
            match(input,33,FOLLOW_33_in_nonWildcardTypeArguments4018); if (failed) return ;
            pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments4020);
            typeList();
            _fsp--;
            if (failed) return ;
            match(input,35,FOLLOW_35_in_nonWildcardTypeArguments4022); if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 126, nonWildcardTypeArguments_StartIndex); }
        }
        return ;
    }
    // $ANTLR end nonWildcardTypeArguments


    // $ANTLR start explicitGenericInvocationSuffix
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:853:1: explicitGenericInvocationSuffix : ( 'super' superSuffix | Identifier arguments );
    public final void explicitGenericInvocationSuffix() throws RecognitionException {
        int explicitGenericInvocationSuffix_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 127) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:854:4: ( 'super' superSuffix | Identifier arguments )
            int alt172=2;
            int LA172_0 = input.LA(1);

            if ( (LA172_0==64) ) {
                alt172=1;
            }
            else if ( (LA172_0==Identifier) ) {
                alt172=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("853:1: explicitGenericInvocationSuffix : ( 'super' superSuffix | Identifier arguments );", 172, 0, input);

                throw nvae;
            }
            switch (alt172) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:854:4: 'super' superSuffix
                    {
                    match(input,64,FOLLOW_64_in_explicitGenericInvocationSuffix4034); if (failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_explicitGenericInvocationSuffix4036);
                    superSuffix();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:855:6: Identifier arguments
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_explicitGenericInvocationSuffix4043); if (failed) return ;
                    pushFollow(FOLLOW_arguments_in_explicitGenericInvocationSuffix4045);
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
            if ( backtracking>0 ) { memoize(input, 127, explicitGenericInvocationSuffix_StartIndex); }
        }
        return ;
    }
    // $ANTLR end explicitGenericInvocationSuffix


    // $ANTLR start selector
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:858:1: selector : ( '.' Identifier ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '.' 'new' ( nonWildcardTypeArguments )? innerCreator | '[' expression ']' );
    public final void selector() throws RecognitionException {
        int selector_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 128) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:859:4: ( '.' Identifier ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '.' 'new' ( nonWildcardTypeArguments )? innerCreator | '[' expression ']' )
            int alt175=5;
            int LA175_0 = input.LA(1);

            if ( (LA175_0==28) ) {
                switch ( input.LA(2) ) {
                case 64:
                    {
                    alt175=3;
                    }
                    break;
                case 113:
                    {
                    alt175=4;
                    }
                    break;
                case Identifier:
                    {
                    alt175=1;
                    }
                    break;
                case 112:
                    {
                    alt175=2;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("858:1: selector : ( '.' Identifier ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '.' 'new' ( nonWildcardTypeArguments )? innerCreator | '[' expression ']' );", 175, 1, input);

                    throw nvae;
                }

            }
            else if ( (LA175_0==41) ) {
                alt175=5;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("858:1: selector : ( '.' Identifier ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '.' 'new' ( nonWildcardTypeArguments )? innerCreator | '[' expression ']' );", 175, 0, input);

                throw nvae;
            }
            switch (alt175) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:859:4: '.' Identifier ( arguments )?
                    {
                    match(input,28,FOLLOW_28_in_selector4057); if (failed) return ;
                    match(input,Identifier,FOLLOW_Identifier_in_selector4059); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:859:19: ( arguments )?
                    int alt173=2;
                    int LA173_0 = input.LA(1);

                    if ( (LA173_0==65) ) {
                        alt173=1;
                    }
                    switch (alt173) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:859:20: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_selector4062);
                            arguments();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:860:6: '.' 'this'
                    {
                    match(input,28,FOLLOW_28_in_selector4071); if (failed) return ;
                    match(input,112,FOLLOW_112_in_selector4073); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:861:6: '.' 'super' superSuffix
                    {
                    match(input,28,FOLLOW_28_in_selector4080); if (failed) return ;
                    match(input,64,FOLLOW_64_in_selector4082); if (failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_selector4084);
                    superSuffix();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:862:6: '.' 'new' ( nonWildcardTypeArguments )? innerCreator
                    {
                    match(input,28,FOLLOW_28_in_selector4091); if (failed) return ;
                    match(input,113,FOLLOW_113_in_selector4093); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:862:16: ( nonWildcardTypeArguments )?
                    int alt174=2;
                    int LA174_0 = input.LA(1);

                    if ( (LA174_0==33) ) {
                        alt174=1;
                    }
                    switch (alt174) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:862:17: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_selector4096);
                            nonWildcardTypeArguments();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_innerCreator_in_selector4100);
                    innerCreator();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:863:6: '[' expression ']'
                    {
                    match(input,41,FOLLOW_41_in_selector4107); if (failed) return ;
                    pushFollow(FOLLOW_expression_in_selector4109);
                    expression();
                    _fsp--;
                    if (failed) return ;
                    match(input,42,FOLLOW_42_in_selector4111); if (failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 128, selector_StartIndex); }
        }
        return ;
    }
    // $ANTLR end selector


    // $ANTLR start superSuffix
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:866:1: superSuffix : ( arguments | '.' Identifier ( arguments )? );
    public final void superSuffix() throws RecognitionException {
        int superSuffix_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 129) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:867:4: ( arguments | '.' Identifier ( arguments )? )
            int alt177=2;
            int LA177_0 = input.LA(1);

            if ( (LA177_0==65) ) {
                alt177=1;
            }
            else if ( (LA177_0==28) ) {
                alt177=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("866:1: superSuffix : ( arguments | '.' Identifier ( arguments )? );", 177, 0, input);

                throw nvae;
            }
            switch (alt177) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:867:4: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_superSuffix4123);
                    arguments();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:868:6: '.' Identifier ( arguments )?
                    {
                    match(input,28,FOLLOW_28_in_superSuffix4130); if (failed) return ;
                    match(input,Identifier,FOLLOW_Identifier_in_superSuffix4132); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:868:21: ( arguments )?
                    int alt176=2;
                    int LA176_0 = input.LA(1);

                    if ( (LA176_0==65) ) {
                        alt176=1;
                    }
                    switch (alt176) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:868:22: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_superSuffix4135);
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
            if ( backtracking>0 ) { memoize(input, 129, superSuffix_StartIndex); }
        }
        return ;
    }
    // $ANTLR end superSuffix


    // $ANTLR start arguments
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:871:1: arguments : '(' ( expressionList )? ')' ;
    public final void arguments() throws RecognitionException {
        int arguments_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 130) ) { return ; }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:872:4: ( '(' ( expressionList )? ')' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:872:4: '(' ( expressionList )? ')'
            {
            match(input,65,FOLLOW_65_in_arguments4151); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:872:8: ( expressionList )?
            int alt178=2;
            int LA178_0 = input.LA(1);

            if ( (LA178_0==Identifier||(LA178_0>=FloatingPointLiteral && LA178_0<=DecimalLiteral)||LA178_0==33||LA178_0==40||(LA178_0>=55 && LA178_0<=62)||(LA178_0>=64 && LA178_0<=65)||(LA178_0>=68 && LA178_0<=70)||(LA178_0>=104 && LA178_0<=105)||(LA178_0>=108 && LA178_0<=113)) ) {
                alt178=1;
            }
            switch (alt178) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments4153);
                    expressionList();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            match(input,66,FOLLOW_66_in_arguments4156); if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 130, arguments_StartIndex); }
        }
        return ;
    }
    // $ANTLR end arguments

    // $ANTLR start synpred1
    public final void synpred1_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:202:4: ( annotations )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:202:4: annotations
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
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:298:4: ( methodDeclaration )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:298:4: methodDeclaration
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
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:299:4: ( fieldDeclaration )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:299:4: fieldDeclaration
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
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:432:16: ( '.' Identifier )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:432:16: '.' Identifier
        {
        match(input,28,FOLLOW_28_in_synpred851363); if (failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred851365); if (failed) return ;

        }
    }
    // $ANTLR end synpred85

    // $ANTLR start synpred120
    public final void synpred120_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:522:4: ( annotation )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:522:4: annotation
        {
        pushFollow(FOLLOW_annotation_in_synpred1201867);
        annotation();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred120

    // $ANTLR start synpred135
    public final void synpred135_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:569:6: ( classDeclaration ( ';' )? )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:569:6: classDeclaration ( ';' )?
        {
        pushFollow(FOLLOW_classDeclaration_in_synpred1352097);
        classDeclaration();
        _fsp--;
        if (failed) return ;
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:569:23: ( ';' )?
        int alt194=2;
        int LA194_0 = input.LA(1);

        if ( (LA194_0==25) ) {
            alt194=1;
        }
        switch (alt194) {
            case 1 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ';'
                {
                match(input,25,FOLLOW_25_in_synpred1352099); if (failed) return ;

                }
                break;

        }


        }
    }
    // $ANTLR end synpred135

    // $ANTLR start synpred137
    public final void synpred137_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:570:6: ( interfaceDeclaration ( ';' )? )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:570:6: interfaceDeclaration ( ';' )?
        {
        pushFollow(FOLLOW_interfaceDeclaration_in_synpred1372107);
        interfaceDeclaration();
        _fsp--;
        if (failed) return ;
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:570:27: ( ';' )?
        int alt195=2;
        int LA195_0 = input.LA(1);

        if ( (LA195_0==25) ) {
            alt195=1;
        }
        switch (alt195) {
            case 1 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ';'
                {
                match(input,25,FOLLOW_25_in_synpred1372109); if (failed) return ;

                }
                break;

        }


        }
    }
    // $ANTLR end synpred137

    // $ANTLR start synpred139
    public final void synpred139_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:571:6: ( enumDeclaration ( ';' )? )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:571:6: enumDeclaration ( ';' )?
        {
        pushFollow(FOLLOW_enumDeclaration_in_synpred1392117);
        enumDeclaration();
        _fsp--;
        if (failed) return ;
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:571:22: ( ';' )?
        int alt196=2;
        int LA196_0 = input.LA(1);

        if ( (LA196_0==25) ) {
            alt196=1;
        }
        switch (alt196) {
            case 1 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ';'
                {
                match(input,25,FOLLOW_25_in_synpred1392119); if (failed) return ;

                }
                break;

        }


        }
    }
    // $ANTLR end synpred139

    // $ANTLR start synpred144
    public final void synpred144_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:599:4: ( localVariableDeclaration )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:599:4: localVariableDeclaration
        {
        pushFollow(FOLLOW_localVariableDeclaration_in_synpred1442234);
        localVariableDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred144

    // $ANTLR start synpred145
    public final void synpred145_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:600:4: ( classOrInterfaceDeclaration )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:600:4: classOrInterfaceDeclaration
        {
        pushFollow(FOLLOW_classOrInterfaceDeclaration_in_synpred1452239);
        classOrInterfaceDeclaration();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred145

    // $ANTLR start synpred150
    public final void synpred150_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:611:52: ( 'else' statement )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:611:52: 'else' statement
        {
        match(input,76,FOLLOW_76_in_synpred1502320); if (failed) return ;
        pushFollow(FOLLOW_statement_in_synpred1502322);
        statement();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred150

    // $ANTLR start synpred155
    public final void synpred155_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:616:9: ( catches 'finally' block )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:616:9: catches 'finally' block
        {
        pushFollow(FOLLOW_catches_in_synpred1552388);
        catches();
        _fsp--;
        if (failed) return ;
        match(input,81,FOLLOW_81_in_synpred1552390); if (failed) return ;
        pushFollow(FOLLOW_block_in_synpred1552392);
        block();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred155

    // $ANTLR start synpred156
    public final void synpred156_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:617:9: ( catches )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:617:9: catches
        {
        pushFollow(FOLLOW_catches_in_synpred1562402);
        catches();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred156

    // $ANTLR start synpred173
    public final void synpred173_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:652:4: ( 'case' constantExpression ':' )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:652:4: 'case' constantExpression ':'
        {
        match(input,88,FOLLOW_88_in_synpred1732627); if (failed) return ;
        pushFollow(FOLLOW_constantExpression_in_synpred1732629);
        constantExpression();
        _fsp--;
        if (failed) return ;
        match(input,74,FOLLOW_74_in_synpred1732631); if (failed) return ;

        }
    }
    // $ANTLR end synpred173

    // $ANTLR start synpred174
    public final void synpred174_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:653:6: ( 'case' enumConstantName ':' )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:653:6: 'case' enumConstantName ':'
        {
        match(input,88,FOLLOW_88_in_synpred1742638); if (failed) return ;
        pushFollow(FOLLOW_enumConstantName_in_synpred1742640);
        enumConstantName();
        _fsp--;
        if (failed) return ;
        match(input,74,FOLLOW_74_in_synpred1742642); if (failed) return ;

        }
    }
    // $ANTLR end synpred174

    // $ANTLR start synpred176
    public final void synpred176_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:663:4: ( forVarControl )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:663:4: forVarControl
        {
        pushFollow(FOLLOW_forVarControl_in_synpred1762687);
        forVarControl();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred176

    // $ANTLR start synpred181
    public final void synpred181_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:668:4: ( ( variableModifier )* type variableDeclarators )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:668:4: ( variableModifier )* type variableDeclarators
        {
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:668:4: ( variableModifier )*
        loop203:
        do {
            int alt203=2;
            int LA203_0 = input.LA(1);

            if ( (LA203_0==49||LA203_0==71) ) {
                alt203=1;
            }


            switch (alt203) {
        	case 1 :
        	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: variableModifier
        	    {
        	    pushFollow(FOLLOW_variableModifier_in_synpred1812714);
        	    variableModifier();
        	    _fsp--;
        	    if (failed) return ;

        	    }
        	    break;

        	default :
        	    break loop203;
            }
        } while (true);

        pushFollow(FOLLOW_type_in_synpred1812717);
        type();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_variableDeclarators_in_synpred1812719);
        variableDeclarators();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred181

    // $ANTLR start synpred184
    public final void synpred184_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:699:27: ( assignmentOperator expression )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:699:27: assignmentOperator expression
        {
        pushFollow(FOLLOW_assignmentOperator_in_synpred1842838);
        assignmentOperator();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_expression_in_synpred1842840);
        expression();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred184

    // $ANTLR start synpred195
    public final void synpred195_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:713:9: ( '>' '>' '=' )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:713:9: '>' '>' '='
        {
        match(input,35,FOLLOW_35_in_synpred1952958); if (failed) return ;
        match(input,35,FOLLOW_35_in_synpred1952960); if (failed) return ;
        match(input,44,FOLLOW_44_in_synpred1952962); if (failed) return ;

        }
    }
    // $ANTLR end synpred195

    // $ANTLR start synpred205
    public final void synpred205_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:750:27: ( relationalOp shiftExpression )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:750:27: relationalOp shiftExpression
        {
        pushFollow(FOLLOW_relationalOp_in_synpred2053206);
        relationalOp();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_shiftExpression_in_synpred2053208);
        shiftExpression();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred205

    // $ANTLR start synpred209
    public final void synpred209_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:758:30: ( shiftOp additiveExpression )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:758:30: shiftOp additiveExpression
        {
        pushFollow(FOLLOW_shiftOp_in_synpred2093261);
        shiftOp();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_additiveExpression_in_synpred2093263);
        additiveExpression();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred209

    // $ANTLR start synpred211
    public final void synpred211_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:763:15: ( '>' '>' '>' )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:763:15: '>' '>' '>'
        {
        match(input,35,FOLLOW_35_in_synpred2113293); if (failed) return ;
        match(input,35,FOLLOW_35_in_synpred2113295); if (failed) return ;
        match(input,35,FOLLOW_35_in_synpred2113297); if (failed) return ;

        }
    }
    // $ANTLR end synpred211

    // $ANTLR start synpred223
    public final void synpred223_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:786:9: ( castExpression )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:786:9: castExpression
        {
        pushFollow(FOLLOW_castExpression_in_synpred2233478);
        castExpression();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred223

    // $ANTLR start synpred227
    public final void synpred227_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:791:8: ( '(' primitiveType ')' unaryExpression )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:791:8: '(' primitiveType ')' unaryExpression
        {
        match(input,65,FOLLOW_65_in_synpred2273516); if (failed) return ;
        pushFollow(FOLLOW_primitiveType_in_synpred2273518);
        primitiveType();
        _fsp--;
        if (failed) return ;
        match(input,66,FOLLOW_66_in_synpred2273520); if (failed) return ;
        pushFollow(FOLLOW_unaryExpression_in_synpred2273522);
        unaryExpression();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred227

    // $ANTLR start synpred228
    public final void synpred228_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:792:13: ( type )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:792:13: type
        {
        pushFollow(FOLLOW_type_in_synpred2283534);
        type();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred228

    // $ANTLR start synpred232
    public final void synpred232_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:799:17: ( '.' Identifier )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:799:17: '.' Identifier
        {
        match(input,28,FOLLOW_28_in_synpred2323601); if (failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred2323603); if (failed) return ;

        }
    }
    // $ANTLR end synpred232

    // $ANTLR start synpred233
    public final void synpred233_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:799:35: ( identifierSuffix )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:799:35: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred2333608);
        identifierSuffix();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred233

    // $ANTLR start synpred238
    public final void synpred238_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:803:56: ( '.' Identifier )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:803:56: '.' Identifier
        {
        match(input,28,FOLLOW_28_in_synpred2383661); if (failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred2383663); if (failed) return ;

        }
    }
    // $ANTLR end synpred238

    // $ANTLR start synpred239
    public final void synpred239_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:803:74: ( identifierSuffix )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:803:74: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred2393668);
        identifierSuffix();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred239

    // $ANTLR start synpred245
    public final void synpred245_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:810:5: ( '[' expression ']' )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:810:5: '[' expression ']'
        {
        match(input,41,FOLLOW_41_in_synpred2453731); if (failed) return ;
        pushFollow(FOLLOW_expression_in_synpred2453733);
        expression();
        _fsp--;
        if (failed) return ;
        match(input,42,FOLLOW_42_in_synpred2453735); if (failed) return ;

        }
    }
    // $ANTLR end synpred245

    // $ANTLR start synpred261
    public final void synpred261_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:837:29: ( '[' expression ']' )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/semantics/java/parser/Java.g:837:29: '[' expression ']'
        {
        match(input,41,FOLLOW_41_in_synpred2613955); if (failed) return ;
        pushFollow(FOLLOW_expression_in_synpred2613957);
        expression();
        _fsp--;
        if (failed) return ;
        match(input,42,FOLLOW_42_in_synpred2613959); if (failed) return ;

        }
    }
    // $ANTLR end synpred261

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
    public final boolean synpred209() {
        backtracking++;
        int start = input.mark();
        try {
            synpred209_fragment(); // can never throw exception
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
    public final boolean synpred232() {
        backtracking++;
        int start = input.mark();
        try {
            synpred232_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred228() {
        backtracking++;
        int start = input.mark();
        try {
            synpred228_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred195() {
        backtracking++;
        int start = input.mark();
        try {
            synpred195_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred173() {
        backtracking++;
        int start = input.mark();
        try {
            synpred173_fragment(); // can never throw exception
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
    public final boolean synpred227() {
        backtracking++;
        int start = input.mark();
        try {
            synpred227_fragment(); // can never throw exception
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
    public final boolean synpred205() {
        backtracking++;
        int start = input.mark();
        try {
            synpred205_fragment(); // can never throw exception
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
    public final boolean synpred233() {
        backtracking++;
        int start = input.mark();
        try {
            synpred233_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred174() {
        backtracking++;
        int start = input.mark();
        try {
            synpred174_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred261() {
        backtracking++;
        int start = input.mark();
        try {
            synpred261_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred211() {
        backtracking++;
        int start = input.mark();
        try {
            synpred211_fragment(); // can never throw exception
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
    public final boolean synpred181() {
        backtracking++;
        int start = input.mark();
        try {
            synpred181_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred238() {
        backtracking++;
        int start = input.mark();
        try {
            synpred238_fragment(); // can never throw exception
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
    public final boolean synpred239() {
        backtracking++;
        int start = input.mark();
        try {
            synpred239_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred245() {
        backtracking++;
        int start = input.mark();
        try {
            synpred245_fragment(); // can never throw exception
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
    public final boolean synpred223() {
        backtracking++;
        int start = input.mark();
        try {
            synpred223_fragment(); // can never throw exception
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
    public static final BitSet FOLLOW_Identifier_in_variableDeclarator1065 = new BitSet(new long[]{0x0000120000000002L});
    public static final BitSet FOLLOW_variableDeclaratorRest_in_variableDeclarator1067 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_variableDeclaratorRest1080 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_variableDeclaratorRest1082 = new BitSet(new long[]{0x0000120000000002L});
    public static final BitSet FOLLOW_44_in_variableDeclaratorRest1087 = new BitSet(new long[]{0x7F80012200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_variableInitializer_in_variableDeclaratorRest1089 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_variableDeclaratorRest1096 = new BitSet(new long[]{0x7F80012200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_variableInitializer_in_variableDeclaratorRest1098 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constantDeclaratorRest_in_constantDeclaratorsRest1118 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_constantDeclaratorsRest1121 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_constantDeclarator_in_constantDeclaratorsRest1123 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_41_in_constantDeclaratorRest1140 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_constantDeclaratorRest1142 = new BitSet(new long[]{0x0000120000000000L});
    public static final BitSet FOLLOW_44_in_constantDeclaratorRest1146 = new BitSet(new long[]{0x7F80012200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_variableInitializer_in_constantDeclaratorRest1148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_variableDeclaratorId1160 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_variableDeclaratorId1163 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_variableDeclaratorId1165 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer1178 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer1188 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_arrayInitializer1200 = new BitSet(new long[]{0x7F80016200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer1203 = new BitSet(new long[]{0x0000004400000000L});
    public static final BitSet FOLLOW_34_in_arrayInitializer1206 = new BitSet(new long[]{0x7F80012200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer1208 = new BitSet(new long[]{0x0000004400000000L});
    public static final BitSet FOLLOW_34_in_arrayInitializer1213 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_arrayInitializer1220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_modifier1236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_modifier1246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_modifier1256 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_modifier1266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_modifier1276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_modifier1286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_modifier1296 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_modifier1306 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_modifier1316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_modifier1326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_modifier1336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_modifier1346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_packageOrTypeName1360 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_28_in_packageOrTypeName1363 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_packageOrTypeName1365 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_Identifier_in_enumConstantName1383 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_typeName1399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_packageOrTypeName_in_typeName1409 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_typeName1411 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_typeName1413 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_type1424 = new BitSet(new long[]{0x0000020210000002L});
    public static final BitSet FOLLOW_typeArguments_in_type1427 = new BitSet(new long[]{0x0000020010000002L});
    public static final BitSet FOLLOW_28_in_type1432 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_type1434 = new BitSet(new long[]{0x0000020210000002L});
    public static final BitSet FOLLOW_typeArguments_in_type1437 = new BitSet(new long[]{0x0000020010000002L});
    public static final BitSet FOLLOW_41_in_type1445 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_type1447 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_primitiveType_in_type1454 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_type1457 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_type1459 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_set_in_primitiveType0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_variableModifier1547 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_variableModifier1557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_typeArguments1568 = new BitSet(new long[]{0xFF80000000000010L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments1570 = new BitSet(new long[]{0x0000000C00000000L});
    public static final BitSet FOLLOW_34_in_typeArguments1573 = new BitSet(new long[]{0xFF80000000000010L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments1575 = new BitSet(new long[]{0x0000000C00000000L});
    public static final BitSet FOLLOW_35_in_typeArguments1579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument1591 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_63_in_typeArgument1596 = new BitSet(new long[]{0x0000000080000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_set_in_typeArgument1599 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_type_in_typeArgument1607 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList1621 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_qualifiedNameList1624 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList1626 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_65_in_formalParameters1640 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000084L});
    public static final BitSet FOLLOW_formalParameterDecls_in_formalParameters1642 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_formalParameters1645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_formalParameterDecls1657 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_type_in_formalParameterDecls1660 = new BitSet(new long[]{0x0000000000000012L,0x0000000000000008L});
    public static final BitSet FOLLOW_formalParameterDeclsRest_in_formalParameterDecls1662 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest1675 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_formalParameterDeclsRest1678 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_formalParameterDecls_in_formalParameterDeclsRest1680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_67_in_formalParameterDeclsRest1689 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest1691 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_methodBody1703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_qualifiedName1714 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_28_in_qualifiedName1717 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_qualifiedName1719 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_integerLiteral_in_literal1736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FloatingPointLiteral_in_literal1746 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharacterLiteral_in_literal1756 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_literal1766 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_literal1776 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_68_in_literal1786 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_integerLiteral0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_booleanLiteral0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_annotations1867 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_71_in_annotation1879 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_annotationName_in_annotation1881 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_annotation1884 = new BitSet(new long[]{0x7F80012200000FD0L,0x0003F300000000F7L});
    public static final BitSet FOLLOW_elementValuePairs_in_annotation1886 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_annotation1889 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_annotationName1903 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_28_in_annotationName1906 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_annotationName1908 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs1922 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_elementValuePairs1925 = new BitSet(new long[]{0x7F80012200000FD0L,0x0003F300000000F3L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs1927 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_Identifier_in_elementValuePair1942 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_elementValuePair1944 = new BitSet(new long[]{0x7F80012200000FD0L,0x0003F300000000F3L});
    public static final BitSet FOLLOW_elementValue_in_elementValuePair1948 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_elementValue1960 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_elementValue1967 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elementValueArrayInitializer_in_elementValue1974 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_elementValueArrayInitializer1986 = new BitSet(new long[]{0x7F80016200000FD0L,0x0003F300000000F3L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer1989 = new BitSet(new long[]{0x0000004400000000L});
    public static final BitSet FOLLOW_34_in_elementValueArrayInitializer1992 = new BitSet(new long[]{0x7F80012200000FD0L,0x0003F300000000F3L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer1994 = new BitSet(new long[]{0x0000004400000000L});
    public static final BitSet FOLLOW_38_in_elementValueArrayInitializer2001 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_71_in_annotationTypeDeclaration2013 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_annotationTypeDeclaration2015 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_annotationTypeDeclaration2017 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_annotationTypeBody_in_annotationTypeDeclaration2019 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_annotationTypeBody2031 = new BitSet(new long[]{0x7FFFE0C048000030L,0x0000000000000080L});
    public static final BitSet FOLLOW_annotationTypeElementDeclarations_in_annotationTypeBody2034 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_annotationTypeBody2038 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeElementDeclaration_in_annotationTypeElementDeclarations2051 = new BitSet(new long[]{0x7FFFE08048000032L,0x0000000000000080L});
    public static final BitSet FOLLOW_annotationTypeElementDeclaration_in_annotationTypeElementDeclarations2055 = new BitSet(new long[]{0x7FFFE08048000032L,0x0000000000000080L});
    public static final BitSet FOLLOW_modifier_in_annotationTypeElementDeclaration2070 = new BitSet(new long[]{0x7FFFE08048000030L,0x0000000000000080L});
    public static final BitSet FOLLOW_annotationTypeElementRest_in_annotationTypeElementDeclaration2074 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_annotationTypeElementRest2086 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_annotationMethodOrConstantRest_in_annotationTypeElementRest2088 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_annotationTypeElementRest2090 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_annotationTypeElementRest2097 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_annotationTypeElementRest2099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_annotationTypeElementRest2107 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_annotationTypeElementRest2109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_annotationTypeElementRest2117 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_annotationTypeElementRest2119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_annotationTypeElementRest2127 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_annotationTypeElementRest2129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationMethodRest_in_annotationMethodOrConstantRest2142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationConstantRest_in_annotationMethodOrConstantRest2149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_annotationMethodRest2162 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_annotationMethodRest2164 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_annotationMethodRest2166 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_defaultValue_in_annotationMethodRest2169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclarators_in_annotationConstantRest2186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_defaultValue2201 = new BitSet(new long[]{0x7F80012200000FD0L,0x0003F300000000F3L});
    public static final BitSet FOLLOW_elementValue_in_defaultValue2203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_block2217 = new BitSet(new long[]{0x7FFFE1E24A000FF0L,0x0003F300007DEAF3L});
    public static final BitSet FOLLOW_blockStatement_in_block2219 = new BitSet(new long[]{0x7FFFE1E24A000FF0L,0x0003F300007DEAF3L});
    public static final BitSet FOLLOW_38_in_block2222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_blockStatement2234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_blockStatement2239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statement_in_blockStatement2248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_localVariableDeclaration2260 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_type_in_localVariableDeclaration2263 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarators_in_localVariableDeclaration2265 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_localVariableDeclaration2267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_statement2279 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_statement2287 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_expression_in_statement2289 = new BitSet(new long[]{0x0000000002000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_statement2292 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_expression_in_statement2294 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_statement2298 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_75_in_statement2306 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_statement2308 = new BitSet(new long[]{0x7F88012202000FD0L,0x0003F300007DEA73L});
    public static final BitSet FOLLOW_statement_in_statement2310 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_76_in_statement2320 = new BitSet(new long[]{0x7F88012202000FD0L,0x0003F300007DEA73L});
    public static final BitSet FOLLOW_statement_in_statement2322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_77_in_statement2332 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_statement2334 = new BitSet(new long[]{0x7F82010202000FD0L,0x0003F300000000F3L});
    public static final BitSet FOLLOW_forControl_in_statement2336 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_statement2338 = new BitSet(new long[]{0x7F88012202000FD0L,0x0003F300007DEA73L});
    public static final BitSet FOLLOW_statement_in_statement2340 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_statement2348 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_statement2350 = new BitSet(new long[]{0x7F88012202000FD0L,0x0003F300007DEA73L});
    public static final BitSet FOLLOW_statement_in_statement2352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_79_in_statement2360 = new BitSet(new long[]{0x7F88012202000FD0L,0x0003F300007DEA73L});
    public static final BitSet FOLLOW_statement_in_statement2362 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_78_in_statement2364 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_statement2366 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_statement2368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_80_in_statement2376 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_block_in_statement2378 = new BitSet(new long[]{0x0000000000000000L,0x0000000000820000L});
    public static final BitSet FOLLOW_catches_in_statement2388 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_81_in_statement2390 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_block_in_statement2392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_statement2402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_81_in_statement2412 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_block_in_statement2414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_82_in_statement2430 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_statement2432 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_statement2434 = new BitSet(new long[]{0x0000004000000000L,0x0000000001000100L});
    public static final BitSet FOLLOW_switchBlockStatementGroups_in_statement2436 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_statement2438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_statement2446 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_statement2448 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_block_in_statement2450 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_83_in_statement2458 = new BitSet(new long[]{0x7F80010202000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_expression_in_statement2460 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_statement2463 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_84_in_statement2471 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_expression_in_statement2473 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_statement2475 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_85_in_statement2483 = new BitSet(new long[]{0x0000000002000010L});
    public static final BitSet FOLLOW_Identifier_in_statement2485 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_statement2488 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_86_in_statement2496 = new BitSet(new long[]{0x0000000002000010L});
    public static final BitSet FOLLOW_Identifier_in_statement2498 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_statement2501 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_statement2509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statementExpression_in_statement2517 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_statement2519 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_statement2527 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_statement2529 = new BitSet(new long[]{0x7F88012202000FD0L,0x0003F300007DEA73L});
    public static final BitSet FOLLOW_statement_in_statement2531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catchClause_in_catches2543 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_catchClause_in_catches2546 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_87_in_catchClause2560 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_catchClause2562 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_formalParameter_in_catchClause2564 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_catchClause2566 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_block_in_catchClause2568 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_formalParameter2579 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_type_in_formalParameter2582 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameter2584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups2598 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000100L});
    public static final BitSet FOLLOW_switchLabel_in_switchBlockStatementGroup2612 = new BitSet(new long[]{0x7FFFE1A24A000FF2L,0x0003F300007DEAF3L});
    public static final BitSet FOLLOW_blockStatement_in_switchBlockStatementGroup2614 = new BitSet(new long[]{0x7FFFE1A24A000FF2L,0x0003F300007DEAF3L});
    public static final BitSet FOLLOW_88_in_switchLabel2627 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_constantExpression_in_switchLabel2629 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_switchLabel2631 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_88_in_switchLabel2638 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_enumConstantName_in_switchLabel2640 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_switchLabel2642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_switchLabel2649 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_switchLabel2651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_moreStatementExpressions2664 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_statementExpression_in_moreStatementExpressions2666 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_forVarControl_in_forControl2687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forInit_in_forControl2692 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_forControl2695 = new BitSet(new long[]{0x7F80010202000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_expression_in_forControl2697 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_forControl2700 = new BitSet(new long[]{0x7F80010200000FD2L,0x0003F30000000073L});
    public static final BitSet FOLLOW_forUpdate_in_forControl2702 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_forInit2714 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_type_in_forInit2717 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarators_in_forInit2719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_forInit2724 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_forVarControl2736 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_type_in_forVarControl2739 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_forVarControl2741 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_forVarControl2743 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_expression_in_forVarControl2745 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_forUpdate2756 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_parExpression2769 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_expression_in_parExpression2771 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_parExpression2773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList2790 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_expressionList2793 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_expression_in_expressionList2795 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_expression_in_statementExpression2811 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_constantExpression2823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression2835 = new BitSet(new long[]{0x0000100A00000002L,0x00000001FE000000L});
    public static final BitSet FOLLOW_assignmentOperator_in_expression2838 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_expression_in_expression2840 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_assignmentOperator2854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_assignmentOperator2864 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_90_in_assignmentOperator2874 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_91_in_assignmentOperator2884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_92_in_assignmentOperator2894 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_93_in_assignmentOperator2904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_94_in_assignmentOperator2914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_95_in_assignmentOperator2924 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_96_in_assignmentOperator2934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_assignmentOperator2944 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_assignmentOperator2946 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_assignmentOperator2948 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_assignmentOperator2958 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_assignmentOperator2960 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_assignmentOperator2962 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_assignmentOperator2972 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_assignmentOperator2974 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_assignmentOperator2976 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_assignmentOperator2978 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression2994 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_63_in_conditionalExpression2998 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression3000 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_conditionalExpression3002 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression3004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression3023 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_97_in_conditionalOrExpression3027 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression3029 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression3048 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_98_in_conditionalAndExpression3052 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression3054 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression3073 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_99_in_inclusiveOrExpression3077 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression3079 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression3098 = new BitSet(new long[]{0x0000000000000002L,0x0000001000000000L});
    public static final BitSet FOLLOW_100_in_exclusiveOrExpression3102 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression3104 = new BitSet(new long[]{0x0000000000000002L,0x0000001000000000L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression3123 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_36_in_andExpression3127 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression3129 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression3148 = new BitSet(new long[]{0x0000000000000002L,0x0000006000000000L});
    public static final BitSet FOLLOW_set_in_equalityExpression3152 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression3160 = new BitSet(new long[]{0x0000000000000002L,0x0000006000000000L});
    public static final BitSet FOLLOW_relationalExpression_in_instanceOfExpression3179 = new BitSet(new long[]{0x0000000000000002L,0x0000008000000000L});
    public static final BitSet FOLLOW_103_in_instanceOfExpression3182 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression3184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression3202 = new BitSet(new long[]{0x0000000A00000002L});
    public static final BitSet FOLLOW_relationalOp_in_relationalExpression3206 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression3208 = new BitSet(new long[]{0x0000000A00000002L});
    public static final BitSet FOLLOW_33_in_relationalOp3224 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_relationalOp3226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_relationalOp3230 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_relationalOp3232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_relationalOp3236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_relationalOp3240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression3257 = new BitSet(new long[]{0x0000000A00000002L});
    public static final BitSet FOLLOW_shiftOp_in_shiftExpression3261 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression3263 = new BitSet(new long[]{0x0000000A00000002L});
    public static final BitSet FOLLOW_33_in_shiftOp3287 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_shiftOp3289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_shiftOp3293 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_shiftOp3295 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_shiftOp3297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_shiftOp3301 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_shiftOp3303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression3321 = new BitSet(new long[]{0x0000000000000002L,0x0000030000000000L});
    public static final BitSet FOLLOW_set_in_additiveExpression3325 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression3333 = new BitSet(new long[]{0x0000000000000002L,0x0000030000000000L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression3352 = new BitSet(new long[]{0x0000000020000002L,0x00000C0000000000L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression3356 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression3370 = new BitSet(new long[]{0x0000000020000002L,0x00000C0000000000L});
    public static final BitSet FOLLOW_104_in_unaryExpression3390 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression3392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_105_in_unaryExpression3400 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression3402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_108_in_unaryExpression3412 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003000000000073L});
    public static final BitSet FOLLOW_primary_in_unaryExpression3414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_109_in_unaryExpression3424 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003000000000073L});
    public static final BitSet FOLLOW_primary_in_unaryExpression3426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression3436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_110_in_unaryExpressionNotPlusMinus3455 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus3457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_111_in_unaryExpressionNotPlusMinus3466 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus3468 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus3478 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus3488 = new BitSet(new long[]{0x0000020010000002L,0x0000300000000000L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus3490 = new BitSet(new long[]{0x0000020010000002L,0x0000300000000000L});
    public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus3493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_castExpression3516 = new BitSet(new long[]{0x7F80000000000000L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression3518 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_castExpression3520 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression3522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_castExpression3531 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_type_in_castExpression3534 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_castExpression3538 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_castExpression3541 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003C00000000073L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression3543 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary3560 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_primary3570 = new BitSet(new long[]{0x0000000000000010L,0x0001000000000001L});
    public static final BitSet FOLLOW_explicitGenericInvocationSuffix_in_primary3581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_112_in_primary3585 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_primary3587 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_112_in_primary3598 = new BitSet(new long[]{0x0000020010000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_primary3601 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_primary3603 = new BitSet(new long[]{0x0000020010000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary3608 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_primary3620 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_superSuffix_in_primary3622 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary3632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_113_in_primary3642 = new BitSet(new long[]{0x7F80000200000010L});
    public static final BitSet FOLLOW_creator_in_primary3644 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_primary3656 = new BitSet(new long[]{0x0000020010000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_primary3661 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_primary3663 = new BitSet(new long[]{0x0000020010000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary3668 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary3680 = new BitSet(new long[]{0x0000020010000000L});
    public static final BitSet FOLLOW_41_in_primary3683 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_primary3685 = new BitSet(new long[]{0x0000020010000000L});
    public static final BitSet FOLLOW_28_in_primary3689 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_primary3691 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_primary3701 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_primary3703 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_primary3705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_identifierSuffix3717 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_identifierSuffix3719 = new BitSet(new long[]{0x0000020010000000L});
    public static final BitSet FOLLOW_28_in_identifierSuffix3723 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_identifierSuffix3725 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_identifierSuffix3731 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_expression_in_identifierSuffix3733 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_identifierSuffix3735 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix3748 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_identifierSuffix3758 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_identifierSuffix3760 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_identifierSuffix3770 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_explicitGenericInvocation_in_identifierSuffix3772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_identifierSuffix3782 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_112_in_identifierSuffix3784 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_identifierSuffix3794 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_identifierSuffix3796 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix3798 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_identifierSuffix3808 = new BitSet(new long[]{0x0000000000000000L,0x0002000000000000L});
    public static final BitSet FOLLOW_113_in_identifierSuffix3810 = new BitSet(new long[]{0x0000000200000010L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_identifierSuffix3813 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_innerCreator_in_identifierSuffix3817 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator3829 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_createdName_in_creator3832 = new BitSet(new long[]{0x0000020000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_arrayCreatorRest_in_creator3843 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator3847 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_createdName3859 = new BitSet(new long[]{0x0000000210000002L});
    public static final BitSet FOLLOW_typeArguments_in_createdName3861 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_28_in_createdName3873 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_createdName3875 = new BitSet(new long[]{0x0000000210000002L});
    public static final BitSet FOLLOW_typeArguments_in_createdName3877 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName3888 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_innerCreator3900 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_classCreatorRest_in_innerCreator3902 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_arrayCreatorRest3913 = new BitSet(new long[]{0x7F80050200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_42_in_arrayCreatorRest3927 = new BitSet(new long[]{0x0000022000000000L});
    public static final BitSet FOLLOW_41_in_arrayCreatorRest3930 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_arrayCreatorRest3932 = new BitSet(new long[]{0x0000022000000000L});
    public static final BitSet FOLLOW_arrayInitializer_in_arrayCreatorRest3936 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest3950 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_arrayCreatorRest3952 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_arrayCreatorRest3955 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest3957 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_arrayCreatorRest3959 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_arrayCreatorRest3964 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_arrayCreatorRest3966 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest3989 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_classBody_in_classCreatorRest3991 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation4004 = new BitSet(new long[]{0x0000000000000010L,0x0000000000000001L});
    public static final BitSet FOLLOW_explicitGenericInvocationSuffix_in_explicitGenericInvocation4006 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_nonWildcardTypeArguments4018 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments4020 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_nonWildcardTypeArguments4022 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_explicitGenericInvocationSuffix4034 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_superSuffix_in_explicitGenericInvocationSuffix4036 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_explicitGenericInvocationSuffix4043 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocationSuffix4045 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_selector4057 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_selector4059 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_selector4062 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_selector4071 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_112_in_selector4073 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_selector4080 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_selector4082 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_superSuffix_in_selector4084 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_selector4091 = new BitSet(new long[]{0x0000000000000000L,0x0002000000000000L});
    public static final BitSet FOLLOW_113_in_selector4093 = new BitSet(new long[]{0x0000000200000010L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_selector4096 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_innerCreator_in_selector4100 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_selector4107 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_expression_in_selector4109 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_selector4111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix4123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_superSuffix4130 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_superSuffix4132 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix4135 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_arguments4151 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000077L});
    public static final BitSet FOLLOW_expressionList_in_arguments4153 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_arguments4156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotations_in_synpred170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodDeclaration_in_synpred38577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldDeclaration_in_synpred39582 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_synpred851363 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_synpred851365 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_synpred1201867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_synpred1352097 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_synpred1352099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_synpred1372107 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_synpred1372109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_synpred1392117 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_synpred1392119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_synpred1442234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_synpred1452239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_synpred1502320 = new BitSet(new long[]{0x7F88012202000FD0L,0x0003F300007DEA73L});
    public static final BitSet FOLLOW_statement_in_synpred1502322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_synpred1552388 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_81_in_synpred1552390 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_block_in_synpred1552392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_synpred1562402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_88_in_synpred1732627 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_constantExpression_in_synpred1732629 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_synpred1732631 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_88_in_synpred1742638 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_enumConstantName_in_synpred1742640 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_synpred1742642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forVarControl_in_synpred1762687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_synpred1812714 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_type_in_synpred1812717 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarators_in_synpred1812719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignmentOperator_in_synpred1842838 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_expression_in_synpred1842840 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_synpred1952958 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_synpred1952960 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_synpred1952962 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalOp_in_synpred2053206 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_shiftExpression_in_synpred2053208 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftOp_in_synpred2093261 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_additiveExpression_in_synpred2093263 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_synpred2113293 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_synpred2113295 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_synpred2113297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_synpred2233478 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_synpred2273516 = new BitSet(new long[]{0x7F80000000000000L});
    public static final BitSet FOLLOW_primitiveType_in_synpred2273518 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_synpred2273520 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_unaryExpression_in_synpred2273522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_synpred2283534 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_synpred2323601 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_synpred2323603 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred2333608 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_synpred2383661 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_synpred2383663 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred2393668 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_synpred2453731 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_expression_in_synpred2453733 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred2453735 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_synpred2613955 = new BitSet(new long[]{0x7F80010200000FD0L,0x0003F30000000073L});
    public static final BitSet FOLLOW_expression_in_synpred2613957 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred2613959 = new BitSet(new long[]{0x0000000000000002L});

}