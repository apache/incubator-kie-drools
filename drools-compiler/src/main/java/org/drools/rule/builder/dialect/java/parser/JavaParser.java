// $ANTLR 3.3 Nov 30, 2010 12:46:29 src/main/resources/org/drools/semantics/java/parser/Java.g 2012-06-01 16:27:27

    package org.drools.rule.builder.dialect.java.parser;
    import java.util.Iterator;
    import java.util.Queue;
    import java.util.LinkedList;   
    import java.util.Stack; 
    


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
public class JavaParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "Identifier", "ENUM", "FloatingPointLiteral", "CharacterLiteral", "StringLiteral", "HexLiteral", "OctalLiteral", "DecimalLiteral", "HexDigit", "IntegerTypeSuffix", "Exponent", "FloatTypeSuffix", "EscapeSequence", "UnicodeEscape", "OctalEscape", "Letter", "JavaIDDigit", "WS", "COMMENT", "LINE_COMMENT", "'package'", "';'", "'import'", "'static'", "'.'", "'*'", "'class'", "'extends'", "'implements'", "'<'", "','", "'>'", "'&'", "'{'", "'}'", "'interface'", "'void'", "'['", "']'", "'throws'", "'='", "'public'", "'protected'", "'private'", "'abstract'", "'final'", "'native'", "'synchronized'", "'transient'", "'volatile'", "'strictfp'", "'boolean'", "'char'", "'byte'", "'short'", "'int'", "'long'", "'float'", "'double'", "'?'", "'super'", "'('", "')'", "'...'", "'null'", "'true'", "'false'", "'@'", "'default'", "'assert'", "':'", "'do'", "'while'", "'switch'", "'return'", "'break'", "'continue'", "'throw'", "'if'", "'else'", "'for'", "'try'", "'catch'", "'finally'", "'modify'", "'update'", "'retract'", "'exitPoints'", "'entryPoints'", "'channels'", "'case'", "'+='", "'-='", "'*='", "'/='", "'&='", "'|='", "'^='", "'%='", "'||'", "'&&'", "'|'", "'^'", "'=='", "'!='", "'instanceof'", "'+'", "'-'", "'/'", "'%'", "'++'", "'--'", "'~'", "'!'", "'this'", "'new'"
    };
    public static final int EOF=-1;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
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
    public static final int Identifier=4;
    public static final int ENUM=5;
    public static final int FloatingPointLiteral=6;
    public static final int CharacterLiteral=7;
    public static final int StringLiteral=8;
    public static final int HexLiteral=9;
    public static final int OctalLiteral=10;
    public static final int DecimalLiteral=11;
    public static final int HexDigit=12;
    public static final int IntegerTypeSuffix=13;
    public static final int Exponent=14;
    public static final int FloatTypeSuffix=15;
    public static final int EscapeSequence=16;
    public static final int UnicodeEscape=17;
    public static final int OctalEscape=18;
    public static final int Letter=19;
    public static final int JavaIDDigit=20;
    public static final int WS=21;
    public static final int COMMENT=22;
    public static final int LINE_COMMENT=23;

    // delegates
    // delegators

    protected static class VarDecl_scope {
        JavaLocalDeclarationDescr descr;
    }
    protected Stack VarDecl_stack = new Stack();


        public JavaParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public JavaParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
            this.state.ruleMemo = new HashMap[428+1];
             
             
        }
        

    public String[] getTokenNames() { return JavaParser.tokenNames; }
    public String getGrammarFileName() { return "src/main/resources/org/drools/semantics/java/parser/Java.g"; }


        private List identifiers = new ArrayList();
        public List getIdentifiers() { return identifiers; }

        private Stack<List<JavaLocalDeclarationDescr>> localDeclarationsStack = new Stack<List<JavaLocalDeclarationDescr>>(); 
        { localDeclarationsStack.push( new ArrayList<JavaLocalDeclarationDescr>() ); }
        private int localVariableLevel = 0;

        public static final CommonToken IGNORE_TOKEN = new CommonToken(null,0,99,0,0);
        private List errors = new ArrayList();
        
            private JavaRootBlockDescr rootBlockDescr = new JavaRootBlockDescr();
            private LinkedList<JavaContainerBlockDescr> blocks;
            
            public void addBlockDescr(JavaBlockDescr blockDescr) {
                if ( this.blocks == null ) {
                    this.blocks = new LinkedList<JavaContainerBlockDescr>();          
                    this.blocks.add( this.rootBlockDescr );
                }
                blocks.getLast().addJavaBlockDescr( blockDescr );
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
    // src/main/resources/org/drools/semantics/java/parser/Java.g:264:1: compilationUnit : ( annotations )? ( packageDeclaration )? ( importDeclaration )* ( typeDeclaration )* ;
    public final void compilationUnit() throws RecognitionException {
        int compilationUnit_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 1) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:265:5: ( ( annotations )? ( packageDeclaration )? ( importDeclaration )* ( typeDeclaration )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:265:7: ( annotations )? ( packageDeclaration )? ( importDeclaration )* ( typeDeclaration )*
            {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:265:7: ( annotations )?
            int alt1=2;
            alt1 = dfa1.predict(input);
            switch (alt1) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: annotations
                    {
                    pushFollow(FOLLOW_annotations_in_compilationUnit81);
                    annotations();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            // src/main/resources/org/drools/semantics/java/parser/Java.g:266:9: ( packageDeclaration )?
            int alt2=2;
            alt2 = dfa2.predict(input);
            switch (alt2) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: packageDeclaration
                    {
                    pushFollow(FOLLOW_packageDeclaration_in_compilationUnit92);
                    packageDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            // src/main/resources/org/drools/semantics/java/parser/Java.g:267:9: ( importDeclaration )*
            loop3:
            do {
                int alt3=2;
                alt3 = dfa3.predict(input);
                switch (alt3) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: importDeclaration
            	    {
            	    pushFollow(FOLLOW_importDeclaration_in_compilationUnit103);
            	    importDeclaration();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            // src/main/resources/org/drools/semantics/java/parser/Java.g:268:9: ( typeDeclaration )*
            loop4:
            do {
                int alt4=2;
                alt4 = dfa4.predict(input);
                switch (alt4) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: typeDeclaration
            	    {
            	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit114);
            	    typeDeclaration();

            	    state._fsp--;
            	    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 1, compilationUnit_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "compilationUnit"


    // $ANTLR start "packageDeclaration"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:271:1: packageDeclaration : 'package' qualifiedName ';' ;
    public final void packageDeclaration() throws RecognitionException {
        int packageDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 2) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:272:5: ( 'package' qualifiedName ';' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:272:7: 'package' qualifiedName ';'
            {
            match(input,24,FOLLOW_24_in_packageDeclaration132); if (state.failed) return ;
            pushFollow(FOLLOW_qualifiedName_in_packageDeclaration134);
            qualifiedName();

            state._fsp--;
            if (state.failed) return ;
            match(input,25,FOLLOW_25_in_packageDeclaration136); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 2, packageDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "packageDeclaration"


    // $ANTLR start "importDeclaration"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:275:1: importDeclaration : 'import' ( 'static' )? Identifier ( '.' Identifier )* ( '.' '*' )? ';' ;
    public final void importDeclaration() throws RecognitionException {
        int importDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 3) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:276:5: ( 'import' ( 'static' )? Identifier ( '.' Identifier )* ( '.' '*' )? ';' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:276:7: 'import' ( 'static' )? Identifier ( '.' Identifier )* ( '.' '*' )? ';'
            {
            match(input,26,FOLLOW_26_in_importDeclaration153); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:276:16: ( 'static' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==27) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: 'static'
                    {
                    match(input,27,FOLLOW_27_in_importDeclaration155); if (state.failed) return ;

                    }
                    break;

            }

            match(input,Identifier,FOLLOW_Identifier_in_importDeclaration158); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:276:37: ( '.' Identifier )*
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
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:276:38: '.' Identifier
            	    {
            	    match(input,28,FOLLOW_28_in_importDeclaration161); if (state.failed) return ;
            	    match(input,Identifier,FOLLOW_Identifier_in_importDeclaration163); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            // src/main/resources/org/drools/semantics/java/parser/Java.g:276:55: ( '.' '*' )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==28) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:276:56: '.' '*'
                    {
                    match(input,28,FOLLOW_28_in_importDeclaration168); if (state.failed) return ;
                    match(input,29,FOLLOW_29_in_importDeclaration170); if (state.failed) return ;

                    }
                    break;

            }

            match(input,25,FOLLOW_25_in_importDeclaration174); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 3, importDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "importDeclaration"


    // $ANTLR start "typeDeclaration"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:279:1: typeDeclaration : ( classOrInterfaceDeclaration | ';' );
    public final void typeDeclaration() throws RecognitionException {
        int typeDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 4) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:280:5: ( classOrInterfaceDeclaration | ';' )
            int alt8=2;
            alt8 = dfa8.predict(input);
            switch (alt8) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:280:7: classOrInterfaceDeclaration
                    {
                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration191);
                    classOrInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:281:9: ';'
                    {
                    match(input,25,FOLLOW_25_in_typeDeclaration201); if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 4, typeDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "typeDeclaration"


    // $ANTLR start "classOrInterfaceDeclaration"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:284:1: classOrInterfaceDeclaration : ( modifier )* ( classDeclaration | interfaceDeclaration ) ;
    public final void classOrInterfaceDeclaration() throws RecognitionException {
        int classOrInterfaceDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 5) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:285:5: ( ( modifier )* ( classDeclaration | interfaceDeclaration ) )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:285:7: ( modifier )* ( classDeclaration | interfaceDeclaration )
            {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:285:7: ( modifier )*
            loop9:
            do {
                int alt9=2;
                alt9 = dfa9.predict(input);
                switch (alt9) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: modifier
            	    {
            	    pushFollow(FOLLOW_modifier_in_classOrInterfaceDeclaration218);
            	    modifier();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            // src/main/resources/org/drools/semantics/java/parser/Java.g:285:17: ( classDeclaration | interfaceDeclaration )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==ENUM||LA10_0==30) ) {
                alt10=1;
            }
            else if ( (LA10_0==39||LA10_0==71) ) {
                alt10=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:285:18: classDeclaration
                    {
                    pushFollow(FOLLOW_classDeclaration_in_classOrInterfaceDeclaration222);
                    classDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:285:37: interfaceDeclaration
                    {
                    pushFollow(FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration226);
                    interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 5, classOrInterfaceDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "classOrInterfaceDeclaration"


    // $ANTLR start "classDeclaration"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:288:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );
    public final void classDeclaration() throws RecognitionException {
        int classDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:289:5: ( normalClassDeclaration | enumDeclaration )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==30) ) {
                alt11=1;
            }
            else if ( (LA11_0==ENUM) ) {
                alt11=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:289:7: normalClassDeclaration
                    {
                    pushFollow(FOLLOW_normalClassDeclaration_in_classDeclaration244);
                    normalClassDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:290:9: enumDeclaration
                    {
                    pushFollow(FOLLOW_enumDeclaration_in_classDeclaration254);
                    enumDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 6, classDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "classDeclaration"


    // $ANTLR start "normalClassDeclaration"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:293:1: normalClassDeclaration : 'class' Identifier ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody ;
    public final void normalClassDeclaration() throws RecognitionException {
        int normalClassDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 7) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:294:5: ( 'class' Identifier ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:294:7: 'class' Identifier ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody
            {
            match(input,30,FOLLOW_30_in_normalClassDeclaration271); if (state.failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_normalClassDeclaration273); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:294:26: ( typeParameters )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==33) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:294:27: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_normalClassDeclaration276);
                    typeParameters();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            // src/main/resources/org/drools/semantics/java/parser/Java.g:295:9: ( 'extends' type )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==31) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:295:10: 'extends' type
                    {
                    match(input,31,FOLLOW_31_in_normalClassDeclaration289); if (state.failed) return ;
                    pushFollow(FOLLOW_type_in_normalClassDeclaration291);
                    type();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            // src/main/resources/org/drools/semantics/java/parser/Java.g:296:9: ( 'implements' typeList )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==32) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:296:10: 'implements' typeList
                    {
                    match(input,32,FOLLOW_32_in_normalClassDeclaration304); if (state.failed) return ;
                    pushFollow(FOLLOW_typeList_in_normalClassDeclaration306);
                    typeList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_classBody_in_normalClassDeclaration318);
            classBody();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 7, normalClassDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "normalClassDeclaration"


    // $ANTLR start "typeParameters"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:300:1: typeParameters : '<' typeParameter ( ',' typeParameter )* '>' ;
    public final void typeParameters() throws RecognitionException {
        int typeParameters_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 8) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:301:5: ( '<' typeParameter ( ',' typeParameter )* '>' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:301:7: '<' typeParameter ( ',' typeParameter )* '>'
            {
            match(input,33,FOLLOW_33_in_typeParameters335); if (state.failed) return ;
            pushFollow(FOLLOW_typeParameter_in_typeParameters337);
            typeParameter();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:301:25: ( ',' typeParameter )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==34) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:301:26: ',' typeParameter
            	    {
            	    match(input,34,FOLLOW_34_in_typeParameters340); if (state.failed) return ;
            	    pushFollow(FOLLOW_typeParameter_in_typeParameters342);
            	    typeParameter();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);

            match(input,35,FOLLOW_35_in_typeParameters346); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 8, typeParameters_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "typeParameters"


    // $ANTLR start "typeParameter"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:304:1: typeParameter : Identifier ( 'extends' bound )? ;
    public final void typeParameter() throws RecognitionException {
        int typeParameter_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:305:5: ( Identifier ( 'extends' bound )? )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:305:7: Identifier ( 'extends' bound )?
            {
            match(input,Identifier,FOLLOW_Identifier_in_typeParameter363); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:305:18: ( 'extends' bound )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==31) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:305:19: 'extends' bound
                    {
                    match(input,31,FOLLOW_31_in_typeParameter366); if (state.failed) return ;
                    pushFollow(FOLLOW_bound_in_typeParameter368);
                    bound();

                    state._fsp--;
                    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 9, typeParameter_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "typeParameter"


    // $ANTLR start "bound"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:308:1: bound : type ( '&' type )* ;
    public final void bound() throws RecognitionException {
        int bound_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 10) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:309:5: ( type ( '&' type )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:309:7: type ( '&' type )*
            {
            pushFollow(FOLLOW_type_in_bound387);
            type();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:309:12: ( '&' type )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==36) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:309:13: '&' type
            	    {
            	    match(input,36,FOLLOW_36_in_bound390); if (state.failed) return ;
            	    pushFollow(FOLLOW_type_in_bound392);
            	    type();

            	    state._fsp--;
            	    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 10, bound_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "bound"


    // $ANTLR start "enumDeclaration"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:312:1: enumDeclaration : ENUM Identifier ( 'implements' typeList )? enumBody ;
    public final void enumDeclaration() throws RecognitionException {
        int enumDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 11) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:313:5: ( ENUM Identifier ( 'implements' typeList )? enumBody )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:313:7: ENUM Identifier ( 'implements' typeList )? enumBody
            {
            match(input,ENUM,FOLLOW_ENUM_in_enumDeclaration411); if (state.failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_enumDeclaration413); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:313:23: ( 'implements' typeList )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==32) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:313:24: 'implements' typeList
                    {
                    match(input,32,FOLLOW_32_in_enumDeclaration416); if (state.failed) return ;
                    pushFollow(FOLLOW_typeList_in_enumDeclaration418);
                    typeList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_enumBody_in_enumDeclaration422);
            enumBody();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 11, enumDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "enumDeclaration"


    // $ANTLR start "enumBody"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:316:1: enumBody : '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}' ;
    public final void enumBody() throws RecognitionException {
        int enumBody_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 12) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:317:5: ( '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:317:7: '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}'
            {
            match(input,37,FOLLOW_37_in_enumBody439); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:317:11: ( enumConstants )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==Identifier||LA19_0==71) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: enumConstants
                    {
                    pushFollow(FOLLOW_enumConstants_in_enumBody441);
                    enumConstants();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            // src/main/resources/org/drools/semantics/java/parser/Java.g:317:26: ( ',' )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==34) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ','
                    {
                    match(input,34,FOLLOW_34_in_enumBody444); if (state.failed) return ;

                    }
                    break;

            }

            // src/main/resources/org/drools/semantics/java/parser/Java.g:317:31: ( enumBodyDeclarations )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==25) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: enumBodyDeclarations
                    {
                    pushFollow(FOLLOW_enumBodyDeclarations_in_enumBody447);
                    enumBodyDeclarations();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,38,FOLLOW_38_in_enumBody450); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 12, enumBody_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "enumBody"


    // $ANTLR start "enumConstants"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:320:1: enumConstants : enumConstant ( ',' enumConstant )* ;
    public final void enumConstants() throws RecognitionException {
        int enumConstants_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 13) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:321:5: ( enumConstant ( ',' enumConstant )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:321:7: enumConstant ( ',' enumConstant )*
            {
            pushFollow(FOLLOW_enumConstant_in_enumConstants467);
            enumConstant();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:321:20: ( ',' enumConstant )*
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
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:321:21: ',' enumConstant
            	    {
            	    match(input,34,FOLLOW_34_in_enumConstants470); if (state.failed) return ;
            	    pushFollow(FOLLOW_enumConstant_in_enumConstants472);
            	    enumConstant();

            	    state._fsp--;
            	    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 13, enumConstants_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "enumConstants"


    // $ANTLR start "enumConstant"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:324:1: enumConstant : ( annotations )? Identifier ( arguments )? ( classBody )? ;
    public final void enumConstant() throws RecognitionException {
        int enumConstant_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 14) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:325:5: ( ( annotations )? Identifier ( arguments )? ( classBody )? )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:325:7: ( annotations )? Identifier ( arguments )? ( classBody )?
            {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:325:7: ( annotations )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==71) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: annotations
                    {
                    pushFollow(FOLLOW_annotations_in_enumConstant491);
                    annotations();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,Identifier,FOLLOW_Identifier_in_enumConstant494); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:325:31: ( arguments )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==65) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:325:32: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_enumConstant497);
                    arguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            // src/main/resources/org/drools/semantics/java/parser/Java.g:325:44: ( classBody )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==37) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:325:45: classBody
                    {
                    pushFollow(FOLLOW_classBody_in_enumConstant502);
                    classBody();

                    state._fsp--;
                    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 14, enumConstant_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "enumConstant"


    // $ANTLR start "enumBodyDeclarations"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:328:1: enumBodyDeclarations : ';' ( classBodyDeclaration )* ;
    public final void enumBodyDeclarations() throws RecognitionException {
        int enumBodyDeclarations_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 15) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:329:5: ( ';' ( classBodyDeclaration )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:329:7: ';' ( classBodyDeclaration )*
            {
            match(input,25,FOLLOW_25_in_enumBodyDeclarations521); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:329:11: ( classBodyDeclaration )*
            loop26:
            do {
                int alt26=2;
                alt26 = dfa26.predict(input);
                switch (alt26) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:329:12: classBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_classBodyDeclaration_in_enumBodyDeclarations524);
            	    classBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 15, enumBodyDeclarations_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "enumBodyDeclarations"


    // $ANTLR start "interfaceDeclaration"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:332:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );
    public final void interfaceDeclaration() throws RecognitionException {
        int interfaceDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 16) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:333:5: ( normalInterfaceDeclaration | annotationTypeDeclaration )
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==39) ) {
                alt27=1;
            }
            else if ( (LA27_0==71) ) {
                alt27=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:333:7: normalInterfaceDeclaration
                    {
                    pushFollow(FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration543);
                    normalInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:334:11: annotationTypeDeclaration
                    {
                    pushFollow(FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration555);
                    annotationTypeDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 16, interfaceDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "interfaceDeclaration"


    // $ANTLR start "normalInterfaceDeclaration"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:337:1: normalInterfaceDeclaration : 'interface' Identifier ( typeParameters )? ( 'extends' typeList )? interfaceBody ;
    public final void normalInterfaceDeclaration() throws RecognitionException {
        int normalInterfaceDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 17) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:338:5: ( 'interface' Identifier ( typeParameters )? ( 'extends' typeList )? interfaceBody )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:338:7: 'interface' Identifier ( typeParameters )? ( 'extends' typeList )? interfaceBody
            {
            match(input,39,FOLLOW_39_in_normalInterfaceDeclaration572); if (state.failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_normalInterfaceDeclaration574); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:338:30: ( typeParameters )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==33) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_normalInterfaceDeclaration576);
                    typeParameters();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            // src/main/resources/org/drools/semantics/java/parser/Java.g:338:46: ( 'extends' typeList )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==31) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:338:47: 'extends' typeList
                    {
                    match(input,31,FOLLOW_31_in_normalInterfaceDeclaration580); if (state.failed) return ;
                    pushFollow(FOLLOW_typeList_in_normalInterfaceDeclaration582);
                    typeList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_interfaceBody_in_normalInterfaceDeclaration586);
            interfaceBody();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 17, normalInterfaceDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "normalInterfaceDeclaration"


    // $ANTLR start "typeList"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:341:1: typeList : type ( ',' type )* ;
    public final void typeList() throws RecognitionException {
        int typeList_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 18) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:342:5: ( type ( ',' type )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:342:7: type ( ',' type )*
            {
            pushFollow(FOLLOW_type_in_typeList603);
            type();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:342:12: ( ',' type )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==34) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:342:13: ',' type
            	    {
            	    match(input,34,FOLLOW_34_in_typeList606); if (state.failed) return ;
            	    pushFollow(FOLLOW_type_in_typeList608);
            	    type();

            	    state._fsp--;
            	    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 18, typeList_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "typeList"


    // $ANTLR start "classBody"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:345:1: classBody : '{' ( classBodyDeclaration )* '}' ;
    public final void classBody() throws RecognitionException {
        int classBody_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 19) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:346:5: ( '{' ( classBodyDeclaration )* '}' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:346:7: '{' ( classBodyDeclaration )* '}'
            {
            match(input,37,FOLLOW_37_in_classBody627); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:346:11: ( classBodyDeclaration )*
            loop31:
            do {
                int alt31=2;
                alt31 = dfa31.predict(input);
                switch (alt31) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: classBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_classBodyDeclaration_in_classBody629);
            	    classBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop31;
                }
            } while (true);

            match(input,38,FOLLOW_38_in_classBody632); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 19, classBody_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "classBody"


    // $ANTLR start "interfaceBody"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:349:1: interfaceBody : '{' ( interfaceBodyDeclaration )* '}' ;
    public final void interfaceBody() throws RecognitionException {
        int interfaceBody_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 20) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:350:5: ( '{' ( interfaceBodyDeclaration )* '}' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:350:7: '{' ( interfaceBodyDeclaration )* '}'
            {
            match(input,37,FOLLOW_37_in_interfaceBody649); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:350:11: ( interfaceBodyDeclaration )*
            loop32:
            do {
                int alt32=2;
                alt32 = dfa32.predict(input);
                switch (alt32) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: interfaceBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_interfaceBodyDeclaration_in_interfaceBody651);
            	    interfaceBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop32;
                }
            } while (true);

            match(input,38,FOLLOW_38_in_interfaceBody654); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 20, interfaceBody_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "interfaceBody"


    // $ANTLR start "classBodyDeclaration"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:353:1: classBodyDeclaration : ( ';' | ( 'static' )? block | ( modifier )* memberDecl );
    public final void classBodyDeclaration() throws RecognitionException {
        int classBodyDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 21) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:354:5: ( ';' | ( 'static' )? block | ( modifier )* memberDecl )
            int alt35=3;
            alt35 = dfa35.predict(input);
            switch (alt35) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:354:7: ';'
                    {
                    match(input,25,FOLLOW_25_in_classBodyDeclaration671); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:355:7: ( 'static' )? block
                    {
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:355:7: ( 'static' )?
                    int alt33=2;
                    int LA33_0 = input.LA(1);

                    if ( (LA33_0==27) ) {
                        alt33=1;
                    }
                    switch (alt33) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: 'static'
                            {
                            match(input,27,FOLLOW_27_in_classBodyDeclaration679); if (state.failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_block_in_classBodyDeclaration682);
                    block();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:356:7: ( modifier )* memberDecl
                    {
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:356:7: ( modifier )*
                    loop34:
                    do {
                        int alt34=2;
                        alt34 = dfa34.predict(input);
                        switch (alt34) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: modifier
                    	    {
                    	    pushFollow(FOLLOW_modifier_in_classBodyDeclaration690);
                    	    modifier();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop34;
                        }
                    } while (true);

                    pushFollow(FOLLOW_memberDecl_in_classBodyDeclaration693);
                    memberDecl();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 21, classBodyDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "classBodyDeclaration"


    // $ANTLR start "memberDecl"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:359:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );
    public final void memberDecl() throws RecognitionException {
        int memberDecl_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 22) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:360:5: ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration )
            int alt36=7;
            alt36 = dfa36.predict(input);
            switch (alt36) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:360:7: genericMethodOrConstructorDecl
                    {
                    pushFollow(FOLLOW_genericMethodOrConstructorDecl_in_memberDecl710);
                    genericMethodOrConstructorDecl();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:361:7: methodDeclaration
                    {
                    pushFollow(FOLLOW_methodDeclaration_in_memberDecl718);
                    methodDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:362:7: fieldDeclaration
                    {
                    pushFollow(FOLLOW_fieldDeclaration_in_memberDecl726);
                    fieldDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:363:7: 'void' Identifier voidMethodDeclaratorRest
                    {
                    match(input,40,FOLLOW_40_in_memberDecl734); if (state.failed) return ;
                    match(input,Identifier,FOLLOW_Identifier_in_memberDecl736); if (state.failed) return ;
                    pushFollow(FOLLOW_voidMethodDeclaratorRest_in_memberDecl738);
                    voidMethodDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:364:7: Identifier constructorDeclaratorRest
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_memberDecl746); if (state.failed) return ;
                    pushFollow(FOLLOW_constructorDeclaratorRest_in_memberDecl748);
                    constructorDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:365:7: interfaceDeclaration
                    {
                    pushFollow(FOLLOW_interfaceDeclaration_in_memberDecl756);
                    interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:366:7: classDeclaration
                    {
                    pushFollow(FOLLOW_classDeclaration_in_memberDecl764);
                    classDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 22, memberDecl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "memberDecl"


    // $ANTLR start "genericMethodOrConstructorDecl"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:369:1: genericMethodOrConstructorDecl : typeParameters genericMethodOrConstructorRest ;
    public final void genericMethodOrConstructorDecl() throws RecognitionException {
        int genericMethodOrConstructorDecl_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 23) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:370:5: ( typeParameters genericMethodOrConstructorRest )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:370:7: typeParameters genericMethodOrConstructorRest
            {
            pushFollow(FOLLOW_typeParameters_in_genericMethodOrConstructorDecl781);
            typeParameters();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_genericMethodOrConstructorRest_in_genericMethodOrConstructorDecl783);
            genericMethodOrConstructorRest();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 23, genericMethodOrConstructorDecl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "genericMethodOrConstructorDecl"


    // $ANTLR start "genericMethodOrConstructorRest"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:373:1: genericMethodOrConstructorRest : ( ( type | 'void' ) Identifier methodDeclaratorRest | Identifier constructorDeclaratorRest );
    public final void genericMethodOrConstructorRest() throws RecognitionException {
        int genericMethodOrConstructorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 24) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:374:5: ( ( type | 'void' ) Identifier methodDeclaratorRest | Identifier constructorDeclaratorRest )
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
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 38, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA38_0==40||(LA38_0>=55 && LA38_0<=62)) ) {
                alt38=1;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 38, 0, input);

                throw nvae;
            }
            switch (alt38) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:374:7: ( type | 'void' ) Identifier methodDeclaratorRest
                    {
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:374:7: ( type | 'void' )
                    int alt37=2;
                    int LA37_0 = input.LA(1);

                    if ( (LA37_0==Identifier||(LA37_0>=55 && LA37_0<=62)) ) {
                        alt37=1;
                    }
                    else if ( (LA37_0==40) ) {
                        alt37=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 37, 0, input);

                        throw nvae;
                    }
                    switch (alt37) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:374:8: type
                            {
                            pushFollow(FOLLOW_type_in_genericMethodOrConstructorRest801);
                            type();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:374:15: 'void'
                            {
                            match(input,40,FOLLOW_40_in_genericMethodOrConstructorRest805); if (state.failed) return ;

                            }
                            break;

                    }

                    match(input,Identifier,FOLLOW_Identifier_in_genericMethodOrConstructorRest808); if (state.failed) return ;
                    pushFollow(FOLLOW_methodDeclaratorRest_in_genericMethodOrConstructorRest810);
                    methodDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:375:7: Identifier constructorDeclaratorRest
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_genericMethodOrConstructorRest818); if (state.failed) return ;
                    pushFollow(FOLLOW_constructorDeclaratorRest_in_genericMethodOrConstructorRest820);
                    constructorDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 24, genericMethodOrConstructorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "genericMethodOrConstructorRest"


    // $ANTLR start "methodDeclaration"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:378:1: methodDeclaration : type Identifier methodDeclaratorRest ;
    public final void methodDeclaration() throws RecognitionException {
        VarDecl_stack.push(new VarDecl_scope());

        int methodDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 25) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:380:5: ( type Identifier methodDeclaratorRest )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:380:7: type Identifier methodDeclaratorRest
            {
            pushFollow(FOLLOW_type_in_methodDeclaration842);
            type();

            state._fsp--;
            if (state.failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_methodDeclaration844); if (state.failed) return ;
            pushFollow(FOLLOW_methodDeclaratorRest_in_methodDeclaration846);
            methodDeclaratorRest();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 25, methodDeclaration_StartIndex); }
            VarDecl_stack.pop();

        }
        return ;
    }
    // $ANTLR end "methodDeclaration"


    // $ANTLR start "fieldDeclaration"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:383:1: fieldDeclaration : type variableDeclarators ';' ;
    public final void fieldDeclaration() throws RecognitionException {
        int fieldDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 26) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:384:5: ( type variableDeclarators ';' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:384:7: type variableDeclarators ';'
            {
            pushFollow(FOLLOW_type_in_fieldDeclaration863);
            type();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_variableDeclarators_in_fieldDeclaration865);
            variableDeclarators();

            state._fsp--;
            if (state.failed) return ;
            match(input,25,FOLLOW_25_in_fieldDeclaration867); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 26, fieldDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "fieldDeclaration"


    // $ANTLR start "interfaceBodyDeclaration"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:387:1: interfaceBodyDeclaration : ( ( modifier )* interfaceMemberDecl | ';' );
    public final void interfaceBodyDeclaration() throws RecognitionException {
        int interfaceBodyDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 27) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:388:5: ( ( modifier )* interfaceMemberDecl | ';' )
            int alt40=2;
            alt40 = dfa40.predict(input);
            switch (alt40) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:388:7: ( modifier )* interfaceMemberDecl
                    {
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:388:7: ( modifier )*
                    loop39:
                    do {
                        int alt39=2;
                        alt39 = dfa39.predict(input);
                        switch (alt39) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: modifier
                    	    {
                    	    pushFollow(FOLLOW_modifier_in_interfaceBodyDeclaration884);
                    	    modifier();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop39;
                        }
                    } while (true);

                    pushFollow(FOLLOW_interfaceMemberDecl_in_interfaceBodyDeclaration887);
                    interfaceMemberDecl();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:389:9: ';'
                    {
                    match(input,25,FOLLOW_25_in_interfaceBodyDeclaration897); if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 27, interfaceBodyDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "interfaceBodyDeclaration"


    // $ANTLR start "interfaceMemberDecl"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:392:1: interfaceMemberDecl : ( interfaceMethodOrFieldDecl | interfaceGenericMethodDecl | 'void' Identifier voidInterfaceMethodDeclaratorRest | interfaceDeclaration | classDeclaration );
    public final void interfaceMemberDecl() throws RecognitionException {
        int interfaceMemberDecl_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 28) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:393:5: ( interfaceMethodOrFieldDecl | interfaceGenericMethodDecl | 'void' Identifier voidInterfaceMethodDeclaratorRest | interfaceDeclaration | classDeclaration )
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
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 41, 0, input);

                throw nvae;
            }

            switch (alt41) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:393:7: interfaceMethodOrFieldDecl
                    {
                    pushFollow(FOLLOW_interfaceMethodOrFieldDecl_in_interfaceMemberDecl914);
                    interfaceMethodOrFieldDecl();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:394:9: interfaceGenericMethodDecl
                    {
                    pushFollow(FOLLOW_interfaceGenericMethodDecl_in_interfaceMemberDecl924);
                    interfaceGenericMethodDecl();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:395:9: 'void' Identifier voidInterfaceMethodDeclaratorRest
                    {
                    match(input,40,FOLLOW_40_in_interfaceMemberDecl934); if (state.failed) return ;
                    match(input,Identifier,FOLLOW_Identifier_in_interfaceMemberDecl936); if (state.failed) return ;
                    pushFollow(FOLLOW_voidInterfaceMethodDeclaratorRest_in_interfaceMemberDecl938);
                    voidInterfaceMethodDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:396:9: interfaceDeclaration
                    {
                    pushFollow(FOLLOW_interfaceDeclaration_in_interfaceMemberDecl948);
                    interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:397:9: classDeclaration
                    {
                    pushFollow(FOLLOW_classDeclaration_in_interfaceMemberDecl958);
                    classDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 28, interfaceMemberDecl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "interfaceMemberDecl"


    // $ANTLR start "interfaceMethodOrFieldDecl"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:400:1: interfaceMethodOrFieldDecl : type Identifier interfaceMethodOrFieldRest ;
    public final void interfaceMethodOrFieldDecl() throws RecognitionException {
        int interfaceMethodOrFieldDecl_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 29) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:401:5: ( type Identifier interfaceMethodOrFieldRest )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:401:7: type Identifier interfaceMethodOrFieldRest
            {
            pushFollow(FOLLOW_type_in_interfaceMethodOrFieldDecl975);
            type();

            state._fsp--;
            if (state.failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_interfaceMethodOrFieldDecl977); if (state.failed) return ;
            pushFollow(FOLLOW_interfaceMethodOrFieldRest_in_interfaceMethodOrFieldDecl979);
            interfaceMethodOrFieldRest();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 29, interfaceMethodOrFieldDecl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "interfaceMethodOrFieldDecl"


    // $ANTLR start "interfaceMethodOrFieldRest"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:404:1: interfaceMethodOrFieldRest : ( constantDeclaratorsRest ';' | interfaceMethodDeclaratorRest );
    public final void interfaceMethodOrFieldRest() throws RecognitionException {
        int interfaceMethodOrFieldRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 30) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:405:5: ( constantDeclaratorsRest ';' | interfaceMethodDeclaratorRest )
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==41||LA42_0==44) ) {
                alt42=1;
            }
            else if ( (LA42_0==65) ) {
                alt42=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 42, 0, input);

                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:405:7: constantDeclaratorsRest ';'
                    {
                    pushFollow(FOLLOW_constantDeclaratorsRest_in_interfaceMethodOrFieldRest996);
                    constantDeclaratorsRest();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,25,FOLLOW_25_in_interfaceMethodOrFieldRest998); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:406:7: interfaceMethodDeclaratorRest
                    {
                    pushFollow(FOLLOW_interfaceMethodDeclaratorRest_in_interfaceMethodOrFieldRest1006);
                    interfaceMethodDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 30, interfaceMethodOrFieldRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "interfaceMethodOrFieldRest"


    // $ANTLR start "methodDeclaratorRest"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:409:1: methodDeclaratorRest : formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( methodBody | ';' ) ;
    public final void methodDeclaratorRest() throws RecognitionException {
        int methodDeclaratorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 31) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:410:5: ( formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( methodBody | ';' ) )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:410:7: formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( methodBody | ';' )
            {
            pushFollow(FOLLOW_formalParameters_in_methodDeclaratorRest1023);
            formalParameters();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:410:24: ( '[' ']' )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);

                if ( (LA43_0==41) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:410:25: '[' ']'
            	    {
            	    match(input,41,FOLLOW_41_in_methodDeclaratorRest1026); if (state.failed) return ;
            	    match(input,42,FOLLOW_42_in_methodDeclaratorRest1028); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop43;
                }
            } while (true);

            // src/main/resources/org/drools/semantics/java/parser/Java.g:411:9: ( 'throws' qualifiedNameList )?
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==43) ) {
                alt44=1;
            }
            switch (alt44) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:411:10: 'throws' qualifiedNameList
                    {
                    match(input,43,FOLLOW_43_in_methodDeclaratorRest1041); if (state.failed) return ;
                    pushFollow(FOLLOW_qualifiedNameList_in_methodDeclaratorRest1043);
                    qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            // src/main/resources/org/drools/semantics/java/parser/Java.g:412:9: ( methodBody | ';' )
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==37) ) {
                alt45=1;
            }
            else if ( (LA45_0==25) ) {
                alt45=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 45, 0, input);

                throw nvae;
            }
            switch (alt45) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:412:13: methodBody
                    {
                    pushFollow(FOLLOW_methodBody_in_methodDeclaratorRest1059);
                    methodBody();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:413:13: ';'
                    {
                    match(input,25,FOLLOW_25_in_methodDeclaratorRest1073); if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 31, methodDeclaratorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "methodDeclaratorRest"


    // $ANTLR start "voidMethodDeclaratorRest"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:417:1: voidMethodDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' ) ;
    public final void voidMethodDeclaratorRest() throws RecognitionException {
        int voidMethodDeclaratorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 32) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:418:5: ( formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' ) )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:418:7: formalParameters ( 'throws' qualifiedNameList )? ( methodBody | ';' )
            {
            pushFollow(FOLLOW_formalParameters_in_voidMethodDeclaratorRest1100);
            formalParameters();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:418:24: ( 'throws' qualifiedNameList )?
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==43) ) {
                alt46=1;
            }
            switch (alt46) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:418:25: 'throws' qualifiedNameList
                    {
                    match(input,43,FOLLOW_43_in_voidMethodDeclaratorRest1103); if (state.failed) return ;
                    pushFollow(FOLLOW_qualifiedNameList_in_voidMethodDeclaratorRest1105);
                    qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            // src/main/resources/org/drools/semantics/java/parser/Java.g:419:9: ( methodBody | ';' )
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==37) ) {
                alt47=1;
            }
            else if ( (LA47_0==25) ) {
                alt47=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 47, 0, input);

                throw nvae;
            }
            switch (alt47) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:419:13: methodBody
                    {
                    pushFollow(FOLLOW_methodBody_in_voidMethodDeclaratorRest1121);
                    methodBody();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:420:13: ';'
                    {
                    match(input,25,FOLLOW_25_in_voidMethodDeclaratorRest1135); if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 32, voidMethodDeclaratorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "voidMethodDeclaratorRest"


    // $ANTLR start "interfaceMethodDeclaratorRest"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:424:1: interfaceMethodDeclaratorRest : formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' ;
    public final void interfaceMethodDeclaratorRest() throws RecognitionException {
        int interfaceMethodDeclaratorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 33) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:425:5: ( formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:425:7: formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';'
            {
            pushFollow(FOLLOW_formalParameters_in_interfaceMethodDeclaratorRest1162);
            formalParameters();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:425:24: ( '[' ']' )*
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);

                if ( (LA48_0==41) ) {
                    alt48=1;
                }


                switch (alt48) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:425:25: '[' ']'
            	    {
            	    match(input,41,FOLLOW_41_in_interfaceMethodDeclaratorRest1165); if (state.failed) return ;
            	    match(input,42,FOLLOW_42_in_interfaceMethodDeclaratorRest1167); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop48;
                }
            } while (true);

            // src/main/resources/org/drools/semantics/java/parser/Java.g:425:35: ( 'throws' qualifiedNameList )?
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==43) ) {
                alt49=1;
            }
            switch (alt49) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:425:36: 'throws' qualifiedNameList
                    {
                    match(input,43,FOLLOW_43_in_interfaceMethodDeclaratorRest1172); if (state.failed) return ;
                    pushFollow(FOLLOW_qualifiedNameList_in_interfaceMethodDeclaratorRest1174);
                    qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,25,FOLLOW_25_in_interfaceMethodDeclaratorRest1178); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 33, interfaceMethodDeclaratorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "interfaceMethodDeclaratorRest"


    // $ANTLR start "interfaceGenericMethodDecl"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:428:1: interfaceGenericMethodDecl : typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest ;
    public final void interfaceGenericMethodDecl() throws RecognitionException {
        int interfaceGenericMethodDecl_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 34) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:429:5: ( typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:429:7: typeParameters ( type | 'void' ) Identifier interfaceMethodDeclaratorRest
            {
            pushFollow(FOLLOW_typeParameters_in_interfaceGenericMethodDecl1195);
            typeParameters();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:429:22: ( type | 'void' )
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==Identifier||(LA50_0>=55 && LA50_0<=62)) ) {
                alt50=1;
            }
            else if ( (LA50_0==40) ) {
                alt50=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 50, 0, input);

                throw nvae;
            }
            switch (alt50) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:429:23: type
                    {
                    pushFollow(FOLLOW_type_in_interfaceGenericMethodDecl1198);
                    type();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:429:30: 'void'
                    {
                    match(input,40,FOLLOW_40_in_interfaceGenericMethodDecl1202); if (state.failed) return ;

                    }
                    break;

            }

            match(input,Identifier,FOLLOW_Identifier_in_interfaceGenericMethodDecl1205); if (state.failed) return ;
            pushFollow(FOLLOW_interfaceMethodDeclaratorRest_in_interfaceGenericMethodDecl1215);
            interfaceMethodDeclaratorRest();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 34, interfaceGenericMethodDecl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "interfaceGenericMethodDecl"


    // $ANTLR start "voidInterfaceMethodDeclaratorRest"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:433:1: voidInterfaceMethodDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? ';' ;
    public final void voidInterfaceMethodDeclaratorRest() throws RecognitionException {
        int voidInterfaceMethodDeclaratorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 35) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:434:5: ( formalParameters ( 'throws' qualifiedNameList )? ';' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:434:7: formalParameters ( 'throws' qualifiedNameList )? ';'
            {
            pushFollow(FOLLOW_formalParameters_in_voidInterfaceMethodDeclaratorRest1232);
            formalParameters();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:434:24: ( 'throws' qualifiedNameList )?
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==43) ) {
                alt51=1;
            }
            switch (alt51) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:434:25: 'throws' qualifiedNameList
                    {
                    match(input,43,FOLLOW_43_in_voidInterfaceMethodDeclaratorRest1235); if (state.failed) return ;
                    pushFollow(FOLLOW_qualifiedNameList_in_voidInterfaceMethodDeclaratorRest1237);
                    qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,25,FOLLOW_25_in_voidInterfaceMethodDeclaratorRest1241); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 35, voidInterfaceMethodDeclaratorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "voidInterfaceMethodDeclaratorRest"


    // $ANTLR start "constructorDeclaratorRest"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:437:1: constructorDeclaratorRest : formalParameters ( 'throws' qualifiedNameList )? methodBody ;
    public final void constructorDeclaratorRest() throws RecognitionException {
        int constructorDeclaratorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 36) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:438:5: ( formalParameters ( 'throws' qualifiedNameList )? methodBody )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:438:7: formalParameters ( 'throws' qualifiedNameList )? methodBody
            {
            pushFollow(FOLLOW_formalParameters_in_constructorDeclaratorRest1258);
            formalParameters();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:438:24: ( 'throws' qualifiedNameList )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==43) ) {
                alt52=1;
            }
            switch (alt52) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:438:25: 'throws' qualifiedNameList
                    {
                    match(input,43,FOLLOW_43_in_constructorDeclaratorRest1261); if (state.failed) return ;
                    pushFollow(FOLLOW_qualifiedNameList_in_constructorDeclaratorRest1263);
                    qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_methodBody_in_constructorDeclaratorRest1267);
            methodBody();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 36, constructorDeclaratorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "constructorDeclaratorRest"


    // $ANTLR start "constantDeclarator"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:441:1: constantDeclarator : Identifier constantDeclaratorRest ;
    public final void constantDeclarator() throws RecognitionException {
        int constantDeclarator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 37) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:442:5: ( Identifier constantDeclaratorRest )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:442:7: Identifier constantDeclaratorRest
            {
            match(input,Identifier,FOLLOW_Identifier_in_constantDeclarator1284); if (state.failed) return ;
            pushFollow(FOLLOW_constantDeclaratorRest_in_constantDeclarator1286);
            constantDeclaratorRest();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 37, constantDeclarator_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "constantDeclarator"


    // $ANTLR start "variableDeclarators"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:445:1: variableDeclarators : variableDeclarator ( ',' variableDeclarator )* ;
    public final void variableDeclarators() throws RecognitionException {
        int variableDeclarators_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 38) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:446:5: ( variableDeclarator ( ',' variableDeclarator )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:446:7: variableDeclarator ( ',' variableDeclarator )*
            {
            pushFollow(FOLLOW_variableDeclarator_in_variableDeclarators1303);
            variableDeclarator();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:446:26: ( ',' variableDeclarator )*
            loop53:
            do {
                int alt53=2;
                int LA53_0 = input.LA(1);

                if ( (LA53_0==34) ) {
                    alt53=1;
                }


                switch (alt53) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:446:27: ',' variableDeclarator
            	    {
            	    match(input,34,FOLLOW_34_in_variableDeclarators1306); if (state.failed) return ;
            	    pushFollow(FOLLOW_variableDeclarator_in_variableDeclarators1308);
            	    variableDeclarator();

            	    state._fsp--;
            	    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 38, variableDeclarators_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "variableDeclarators"

    protected static class variableDeclarator_scope {
        JavaLocalDeclarationDescr.IdentifierDescr ident;
    }
    protected Stack variableDeclarator_stack = new Stack();


    // $ANTLR start "variableDeclarator"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:449:1: variableDeclarator : id= Identifier rest= variableDeclaratorRest ;
    public final void variableDeclarator() throws RecognitionException {
        variableDeclarator_stack.push(new variableDeclarator_scope());
        int variableDeclarator_StartIndex = input.index();
        Token id=null;
        JavaParser.variableDeclaratorRest_return rest = null;



                ((variableDeclarator_scope)variableDeclarator_stack.peek()).ident = new JavaLocalDeclarationDescr.IdentifierDescr();
            
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 39) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:461:5: (id= Identifier rest= variableDeclaratorRest )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:461:7: id= Identifier rest= variableDeclaratorRest
            {
            id=(Token)match(input,Identifier,FOLLOW_Identifier_in_variableDeclarator1355); if (state.failed) return ;
            pushFollow(FOLLOW_variableDeclaratorRest_in_variableDeclarator1359);
            rest=variableDeclaratorRest();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {

                          ((variableDeclarator_scope)variableDeclarator_stack.peek()).ident.setIdentifier( (id!=null?id.getText():null) );
                          ((variableDeclarator_scope)variableDeclarator_stack.peek()).ident.setStart( ((CommonToken)id).getStartIndex() - 1 );
                          if( (rest!=null?((Token)rest.stop):null) != null ) {
                                 ((variableDeclarator_scope)variableDeclarator_stack.peek()).ident.setEnd( ((CommonToken)(rest!=null?((Token)rest.stop):null)).getStopIndex() );
                          }
                      
            }

            }

            if ( state.backtracking==0 ) {

                          if( ((VarDecl_scope)VarDecl_stack.peek()).descr != null ) {
                              ((VarDecl_scope)VarDecl_stack.peek()).descr.addIdentifier( ((variableDeclarator_scope)variableDeclarator_stack.peek()).ident );
                          }
                  
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 39, variableDeclarator_StartIndex); }
            variableDeclarator_stack.pop();
        }
        return ;
    }
    // $ANTLR end "variableDeclarator"

    public static class variableDeclaratorRest_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "variableDeclaratorRest"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:471:1: variableDeclaratorRest : ( ( '[' ']' )+ ( '=' variableInitializer )? | '=' variableInitializer | );
    public final JavaParser.variableDeclaratorRest_return variableDeclaratorRest() throws RecognitionException {
        JavaParser.variableDeclaratorRest_return retval = new JavaParser.variableDeclaratorRest_return();
        retval.start = input.LT(1);
        int variableDeclaratorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 40) ) { return retval; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:472:5: ( ( '[' ']' )+ ( '=' variableInitializer )? | '=' variableInitializer | )
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
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 56, 0, input);

                throw nvae;
            }

            switch (alt56) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:472:7: ( '[' ']' )+ ( '=' variableInitializer )?
                    {
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:472:7: ( '[' ']' )+
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
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:472:8: '[' ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_variableDeclaratorRest1387); if (state.failed) return retval;
                    	    match(input,42,FOLLOW_42_in_variableDeclaratorRest1389); if (state.failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt54 >= 1 ) break loop54;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(54, input);
                                throw eee;
                        }
                        cnt54++;
                    } while (true);

                    // src/main/resources/org/drools/semantics/java/parser/Java.g:472:18: ( '=' variableInitializer )?
                    int alt55=2;
                    int LA55_0 = input.LA(1);

                    if ( (LA55_0==44) ) {
                        alt55=1;
                    }
                    switch (alt55) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:472:19: '=' variableInitializer
                            {
                            match(input,44,FOLLOW_44_in_variableDeclaratorRest1394); if (state.failed) return retval;
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
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:473:7: '=' variableInitializer
                    {
                    match(input,44,FOLLOW_44_in_variableDeclaratorRest1406); if (state.failed) return retval;
                    pushFollow(FOLLOW_variableInitializer_in_variableDeclaratorRest1408);
                    variableInitializer();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:475:5: 
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
            if ( state.backtracking>0 ) { memoize(input, 40, variableDeclaratorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "variableDeclaratorRest"


    // $ANTLR start "constantDeclaratorsRest"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:477:1: constantDeclaratorsRest : constantDeclaratorRest ( ',' constantDeclarator )* ;
    public final void constantDeclaratorsRest() throws RecognitionException {
        int constantDeclaratorsRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 41) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:478:5: ( constantDeclaratorRest ( ',' constantDeclarator )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:478:9: constantDeclaratorRest ( ',' constantDeclarator )*
            {
            pushFollow(FOLLOW_constantDeclaratorRest_in_constantDeclaratorsRest1433);
            constantDeclaratorRest();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:478:32: ( ',' constantDeclarator )*
            loop57:
            do {
                int alt57=2;
                int LA57_0 = input.LA(1);

                if ( (LA57_0==34) ) {
                    alt57=1;
                }


                switch (alt57) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:478:33: ',' constantDeclarator
            	    {
            	    match(input,34,FOLLOW_34_in_constantDeclaratorsRest1436); if (state.failed) return ;
            	    pushFollow(FOLLOW_constantDeclarator_in_constantDeclaratorsRest1438);
            	    constantDeclarator();

            	    state._fsp--;
            	    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 41, constantDeclaratorsRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "constantDeclaratorsRest"


    // $ANTLR start "constantDeclaratorRest"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:481:1: constantDeclaratorRest : ( '[' ']' )* '=' variableInitializer ;
    public final void constantDeclaratorRest() throws RecognitionException {
        int constantDeclaratorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 42) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:482:5: ( ( '[' ']' )* '=' variableInitializer )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:482:7: ( '[' ']' )* '=' variableInitializer
            {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:482:7: ( '[' ']' )*
            loop58:
            do {
                int alt58=2;
                int LA58_0 = input.LA(1);

                if ( (LA58_0==41) ) {
                    alt58=1;
                }


                switch (alt58) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:482:8: '[' ']'
            	    {
            	    match(input,41,FOLLOW_41_in_constantDeclaratorRest1458); if (state.failed) return ;
            	    match(input,42,FOLLOW_42_in_constantDeclaratorRest1460); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop58;
                }
            } while (true);

            match(input,44,FOLLOW_44_in_constantDeclaratorRest1464); if (state.failed) return ;
            pushFollow(FOLLOW_variableInitializer_in_constantDeclaratorRest1466);
            variableInitializer();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 42, constantDeclaratorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "constantDeclaratorRest"


    // $ANTLR start "variableDeclaratorId"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:485:1: variableDeclaratorId : Identifier ( '[' ']' )* ;
    public final void variableDeclaratorId() throws RecognitionException {
        int variableDeclaratorId_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 43) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:486:5: ( Identifier ( '[' ']' )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:486:7: Identifier ( '[' ']' )*
            {
            match(input,Identifier,FOLLOW_Identifier_in_variableDeclaratorId1483); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:486:18: ( '[' ']' )*
            loop59:
            do {
                int alt59=2;
                int LA59_0 = input.LA(1);

                if ( (LA59_0==41) ) {
                    alt59=1;
                }


                switch (alt59) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:486:19: '[' ']'
            	    {
            	    match(input,41,FOLLOW_41_in_variableDeclaratorId1486); if (state.failed) return ;
            	    match(input,42,FOLLOW_42_in_variableDeclaratorId1488); if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 43, variableDeclaratorId_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "variableDeclaratorId"


    // $ANTLR start "variableInitializer"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:489:1: variableInitializer : ( arrayInitializer | expression );
    public final void variableInitializer() throws RecognitionException {
        int variableInitializer_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 44) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:490:5: ( arrayInitializer | expression )
            int alt60=2;
            alt60 = dfa60.predict(input);
            switch (alt60) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:490:7: arrayInitializer
                    {
                    pushFollow(FOLLOW_arrayInitializer_in_variableInitializer1507);
                    arrayInitializer();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:491:9: expression
                    {
                    pushFollow(FOLLOW_expression_in_variableInitializer1517);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 44, variableInitializer_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "variableInitializer"


    // $ANTLR start "arrayInitializer"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:494:1: arrayInitializer : '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}' ;
    public final void arrayInitializer() throws RecognitionException {
        int arrayInitializer_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 45) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:495:5: ( '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:495:7: '{' ( variableInitializer ( ',' variableInitializer )* ( ',' )? )? '}'
            {
            match(input,37,FOLLOW_37_in_arrayInitializer1534); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:495:11: ( variableInitializer ( ',' variableInitializer )* ( ',' )? )?
            int alt63=2;
            alt63 = dfa63.predict(input);
            switch (alt63) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:495:12: variableInitializer ( ',' variableInitializer )* ( ',' )?
                    {
                    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer1537);
                    variableInitializer();

                    state._fsp--;
                    if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:495:32: ( ',' variableInitializer )*
                    loop61:
                    do {
                        int alt61=2;
                        alt61 = dfa61.predict(input);
                        switch (alt61) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:495:33: ',' variableInitializer
                    	    {
                    	    match(input,34,FOLLOW_34_in_arrayInitializer1540); if (state.failed) return ;
                    	    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer1542);
                    	    variableInitializer();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop61;
                        }
                    } while (true);

                    // src/main/resources/org/drools/semantics/java/parser/Java.g:495:59: ( ',' )?
                    int alt62=2;
                    int LA62_0 = input.LA(1);

                    if ( (LA62_0==34) ) {
                        alt62=1;
                    }
                    switch (alt62) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:495:60: ','
                            {
                            match(input,34,FOLLOW_34_in_arrayInitializer1547); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }

            match(input,38,FOLLOW_38_in_arrayInitializer1554); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 45, arrayInitializer_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "arrayInitializer"


    // $ANTLR start "modifier"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:498:1: modifier : ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' );
    public final void modifier() throws RecognitionException {
        int modifier_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 46) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:499:5: ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' )
            int alt64=12;
            alt64 = dfa64.predict(input);
            switch (alt64) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:499:9: annotation
                    {
                    pushFollow(FOLLOW_annotation_in_modifier1573);
                    annotation();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:500:9: 'public'
                    {
                    match(input,45,FOLLOW_45_in_modifier1583); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:501:9: 'protected'
                    {
                    match(input,46,FOLLOW_46_in_modifier1593); if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:502:9: 'private'
                    {
                    match(input,47,FOLLOW_47_in_modifier1603); if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:503:9: 'static'
                    {
                    match(input,27,FOLLOW_27_in_modifier1613); if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:504:9: 'abstract'
                    {
                    match(input,48,FOLLOW_48_in_modifier1623); if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:505:9: 'final'
                    {
                    match(input,49,FOLLOW_49_in_modifier1633); if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:506:9: 'native'
                    {
                    match(input,50,FOLLOW_50_in_modifier1643); if (state.failed) return ;

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:507:9: 'synchronized'
                    {
                    match(input,51,FOLLOW_51_in_modifier1653); if (state.failed) return ;

                    }
                    break;
                case 10 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:508:9: 'transient'
                    {
                    match(input,52,FOLLOW_52_in_modifier1663); if (state.failed) return ;

                    }
                    break;
                case 11 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:509:9: 'volatile'
                    {
                    match(input,53,FOLLOW_53_in_modifier1673); if (state.failed) return ;

                    }
                    break;
                case 12 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:510:9: 'strictfp'
                    {
                    match(input,54,FOLLOW_54_in_modifier1683); if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 46, modifier_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "modifier"


    // $ANTLR start "packageOrTypeName"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:513:1: packageOrTypeName : Identifier ( '.' Identifier )* ;
    public final void packageOrTypeName() throws RecognitionException {
        int packageOrTypeName_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 47) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:514:5: ( Identifier ( '.' Identifier )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:514:7: Identifier ( '.' Identifier )*
            {
            match(input,Identifier,FOLLOW_Identifier_in_packageOrTypeName1700); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:514:18: ( '.' Identifier )*
            loop65:
            do {
                int alt65=2;
                int LA65_0 = input.LA(1);

                if ( (LA65_0==28) ) {
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
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:514:19: '.' Identifier
            	    {
            	    match(input,28,FOLLOW_28_in_packageOrTypeName1703); if (state.failed) return ;
            	    match(input,Identifier,FOLLOW_Identifier_in_packageOrTypeName1705); if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 47, packageOrTypeName_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "packageOrTypeName"


    // $ANTLR start "enumConstantName"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:517:1: enumConstantName : Identifier ;
    public final void enumConstantName() throws RecognitionException {
        int enumConstantName_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 48) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:518:5: ( Identifier )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:518:9: Identifier
            {
            match(input,Identifier,FOLLOW_Identifier_in_enumConstantName1726); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 48, enumConstantName_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "enumConstantName"


    // $ANTLR start "typeName"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:521:1: typeName : ( Identifier | packageOrTypeName '.' Identifier );
    public final void typeName() throws RecognitionException {
        int typeName_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 49) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:522:5: ( Identifier | packageOrTypeName '.' Identifier )
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
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 66, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 66, 0, input);

                throw nvae;
            }
            switch (alt66) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:522:9: Identifier
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_typeName1745); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:523:9: packageOrTypeName '.' Identifier
                    {
                    pushFollow(FOLLOW_packageOrTypeName_in_typeName1755);
                    packageOrTypeName();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,28,FOLLOW_28_in_typeName1757); if (state.failed) return ;
                    match(input,Identifier,FOLLOW_Identifier_in_typeName1759); if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 49, typeName_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "typeName"

    public static class type_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "type"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:526:1: type : ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* ( '[' ']' )* | primitiveType ( '[' ']' )* );
    public final JavaParser.type_return type() throws RecognitionException {
        JavaParser.type_return retval = new JavaParser.type_return();
        retval.start = input.LT(1);
        int type_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 50) ) { return retval; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:527:5: ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* ( '[' ']' )* | primitiveType ( '[' ']' )* )
            int alt72=2;
            int LA72_0 = input.LA(1);

            if ( (LA72_0==Identifier) ) {
                alt72=1;
            }
            else if ( ((LA72_0>=55 && LA72_0<=62)) ) {
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
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:527:7: Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* ( '[' ']' )*
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_type1776); if (state.failed) return retval;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:527:18: ( typeArguments )?
                    int alt67=2;
                    alt67 = dfa67.predict(input);
                    switch (alt67) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:527:19: typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_type1779);
                            typeArguments();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }

                    // src/main/resources/org/drools/semantics/java/parser/Java.g:527:35: ( '.' Identifier ( typeArguments )? )*
                    loop69:
                    do {
                        int alt69=2;
                        alt69 = dfa69.predict(input);
                        switch (alt69) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:527:36: '.' Identifier ( typeArguments )?
                    	    {
                    	    match(input,28,FOLLOW_28_in_type1784); if (state.failed) return retval;
                    	    match(input,Identifier,FOLLOW_Identifier_in_type1786); if (state.failed) return retval;
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:527:51: ( typeArguments )?
                    	    int alt68=2;
                    	    alt68 = dfa68.predict(input);
                    	    switch (alt68) {
                    	        case 1 :
                    	            // src/main/resources/org/drools/semantics/java/parser/Java.g:527:52: typeArguments
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
                    } while (true);

                    // src/main/resources/org/drools/semantics/java/parser/Java.g:527:71: ( '[' ']' )*
                    loop70:
                    do {
                        int alt70=2;
                        alt70 = dfa70.predict(input);
                        switch (alt70) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:527:72: '[' ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_type1797); if (state.failed) return retval;
                    	    match(input,42,FOLLOW_42_in_type1799); if (state.failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    break loop70;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:528:7: primitiveType ( '[' ']' )*
                    {
                    pushFollow(FOLLOW_primitiveType_in_type1809);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:528:21: ( '[' ']' )*
                    loop71:
                    do {
                        int alt71=2;
                        alt71 = dfa71.predict(input);
                        switch (alt71) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:528:22: '[' ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_type1812); if (state.failed) return retval;
                    	    match(input,42,FOLLOW_42_in_type1814); if (state.failed) return retval;

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
            if ( state.backtracking>0 ) { memoize(input, 50, type_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "type"


    // $ANTLR start "primitiveType"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:531:1: primitiveType : ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' );
    public final void primitiveType() throws RecognitionException {
        int primitiveType_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 51) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:532:5: ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:
            {
            if ( (input.LA(1)>=55 && input.LA(1)<=62) ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
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
            if ( state.backtracking>0 ) { memoize(input, 51, primitiveType_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "primitiveType"

    public static class variableModifier_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "variableModifier"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:542:1: variableModifier : ( 'final' | annotation );
    public final JavaParser.variableModifier_return variableModifier() throws RecognitionException {
        JavaParser.variableModifier_return retval = new JavaParser.variableModifier_return();
        retval.start = input.LT(1);
        int variableModifier_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 52) ) { return retval; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:543:5: ( 'final' | annotation )
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==49) ) {
                alt73=1;
            }
            else if ( (LA73_0==71) ) {
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
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:543:7: 'final'
                    {
                    match(input,49,FOLLOW_49_in_variableModifier1908); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:544:9: annotation
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
            if ( state.backtracking>0 ) { memoize(input, 52, variableModifier_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "variableModifier"


    // $ANTLR start "typeArguments"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:547:1: typeArguments : '<' typeArgument ( ',' typeArgument )* '>' ;
    public final void typeArguments() throws RecognitionException {
        int typeArguments_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 53) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:548:5: ( '<' typeArgument ( ',' typeArgument )* '>' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:548:7: '<' typeArgument ( ',' typeArgument )* '>'
            {
            match(input,33,FOLLOW_33_in_typeArguments1935); if (state.failed) return ;
            pushFollow(FOLLOW_typeArgument_in_typeArguments1937);
            typeArgument();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:548:24: ( ',' typeArgument )*
            loop74:
            do {
                int alt74=2;
                int LA74_0 = input.LA(1);

                if ( (LA74_0==34) ) {
                    alt74=1;
                }


                switch (alt74) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:548:25: ',' typeArgument
            	    {
            	    match(input,34,FOLLOW_34_in_typeArguments1940); if (state.failed) return ;
            	    pushFollow(FOLLOW_typeArgument_in_typeArguments1942);
            	    typeArgument();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop74;
                }
            } while (true);

            match(input,35,FOLLOW_35_in_typeArguments1946); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 53, typeArguments_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "typeArguments"


    // $ANTLR start "typeArgument"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:551:1: typeArgument : ( type | '?' ( ( 'extends' | 'super' ) type )? );
    public final void typeArgument() throws RecognitionException {
        int typeArgument_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 54) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:552:5: ( type | '?' ( ( 'extends' | 'super' ) type )? )
            int alt76=2;
            int LA76_0 = input.LA(1);

            if ( (LA76_0==Identifier||(LA76_0>=55 && LA76_0<=62)) ) {
                alt76=1;
            }
            else if ( (LA76_0==63) ) {
                alt76=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 76, 0, input);

                throw nvae;
            }
            switch (alt76) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:552:7: type
                    {
                    pushFollow(FOLLOW_type_in_typeArgument1963);
                    type();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:553:7: '?' ( ( 'extends' | 'super' ) type )?
                    {
                    match(input,63,FOLLOW_63_in_typeArgument1971); if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:553:11: ( ( 'extends' | 'super' ) type )?
                    int alt75=2;
                    int LA75_0 = input.LA(1);

                    if ( (LA75_0==31||LA75_0==64) ) {
                        alt75=1;
                    }
                    switch (alt75) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:553:12: ( 'extends' | 'super' ) type
                            {
                            if ( input.LA(1)==31||input.LA(1)==64 ) {
                                input.consume();
                                state.errorRecovery=false;state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return ;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }

                            pushFollow(FOLLOW_type_in_typeArgument1982);
                            type();

                            state._fsp--;
                            if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 54, typeArgument_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "typeArgument"


    // $ANTLR start "qualifiedNameList"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:556:1: qualifiedNameList : qualifiedName ( ',' qualifiedName )* ;
    public final void qualifiedNameList() throws RecognitionException {
        int qualifiedNameList_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 55) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:557:5: ( qualifiedName ( ',' qualifiedName )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:557:7: qualifiedName ( ',' qualifiedName )*
            {
            pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList2001);
            qualifiedName();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:557:21: ( ',' qualifiedName )*
            loop77:
            do {
                int alt77=2;
                int LA77_0 = input.LA(1);

                if ( (LA77_0==34) ) {
                    alt77=1;
                }


                switch (alt77) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:557:22: ',' qualifiedName
            	    {
            	    match(input,34,FOLLOW_34_in_qualifiedNameList2004); if (state.failed) return ;
            	    pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList2006);
            	    qualifiedName();

            	    state._fsp--;
            	    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 55, qualifiedNameList_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "qualifiedNameList"


    // $ANTLR start "formalParameters"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:560:1: formalParameters : '(' ( formalParameterDecls )? ')' ;
    public final void formalParameters() throws RecognitionException {
        int formalParameters_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 56) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:561:5: ( '(' ( formalParameterDecls )? ')' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:561:7: '(' ( formalParameterDecls )? ')'
            {
            match(input,65,FOLLOW_65_in_formalParameters2025); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:561:11: ( formalParameterDecls )?
            int alt78=2;
            int LA78_0 = input.LA(1);

            if ( (LA78_0==Identifier||LA78_0==49||(LA78_0>=55 && LA78_0<=62)||LA78_0==71) ) {
                alt78=1;
            }
            switch (alt78) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: formalParameterDecls
                    {
                    pushFollow(FOLLOW_formalParameterDecls_in_formalParameters2027);
                    formalParameterDecls();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,66,FOLLOW_66_in_formalParameters2030); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 56, formalParameters_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "formalParameters"


    // $ANTLR start "formalParameterDecls"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:564:1: formalParameterDecls : ( variableModifier )* type ( formalParameterDeclsRest )? ;
    public final void formalParameterDecls() throws RecognitionException {
        int formalParameterDecls_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 57) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:565:5: ( ( variableModifier )* type ( formalParameterDeclsRest )? )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:565:7: ( variableModifier )* type ( formalParameterDeclsRest )?
            {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:565:7: ( variableModifier )*
            loop79:
            do {
                int alt79=2;
                int LA79_0 = input.LA(1);

                if ( (LA79_0==49||LA79_0==71) ) {
                    alt79=1;
                }


                switch (alt79) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: variableModifier
            	    {
            	    pushFollow(FOLLOW_variableModifier_in_formalParameterDecls2047);
            	    variableModifier();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop79;
                }
            } while (true);

            pushFollow(FOLLOW_type_in_formalParameterDecls2050);
            type();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:565:30: ( formalParameterDeclsRest )?
            int alt80=2;
            int LA80_0 = input.LA(1);

            if ( (LA80_0==Identifier||LA80_0==67) ) {
                alt80=1;
            }
            switch (alt80) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: formalParameterDeclsRest
                    {
                    pushFollow(FOLLOW_formalParameterDeclsRest_in_formalParameterDecls2052);
                    formalParameterDeclsRest();

                    state._fsp--;
                    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 57, formalParameterDecls_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "formalParameterDecls"


    // $ANTLR start "formalParameterDeclsRest"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:568:1: formalParameterDeclsRest : ( variableDeclaratorId ( ',' formalParameterDecls )? | '...' variableDeclaratorId );
    public final void formalParameterDeclsRest() throws RecognitionException {
        int formalParameterDeclsRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 58) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:569:5: ( variableDeclaratorId ( ',' formalParameterDecls )? | '...' variableDeclaratorId )
            int alt82=2;
            int LA82_0 = input.LA(1);

            if ( (LA82_0==Identifier) ) {
                alt82=1;
            }
            else if ( (LA82_0==67) ) {
                alt82=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 82, 0, input);

                throw nvae;
            }
            switch (alt82) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:569:7: variableDeclaratorId ( ',' formalParameterDecls )?
                    {
                    pushFollow(FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest2070);
                    variableDeclaratorId();

                    state._fsp--;
                    if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:569:28: ( ',' formalParameterDecls )?
                    int alt81=2;
                    int LA81_0 = input.LA(1);

                    if ( (LA81_0==34) ) {
                        alt81=1;
                    }
                    switch (alt81) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:569:29: ',' formalParameterDecls
                            {
                            match(input,34,FOLLOW_34_in_formalParameterDeclsRest2073); if (state.failed) return ;
                            pushFollow(FOLLOW_formalParameterDecls_in_formalParameterDeclsRest2075);
                            formalParameterDecls();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:570:9: '...' variableDeclaratorId
                    {
                    match(input,67,FOLLOW_67_in_formalParameterDeclsRest2087); if (state.failed) return ;
                    pushFollow(FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest2089);
                    variableDeclaratorId();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 58, formalParameterDeclsRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "formalParameterDeclsRest"


    // $ANTLR start "methodBody"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:573:1: methodBody : block ;
    public final void methodBody() throws RecognitionException {
        int methodBody_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 59) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:574:5: ( block )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:574:7: block
            {
            pushFollow(FOLLOW_block_in_methodBody2106);
            block();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 59, methodBody_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "methodBody"


    // $ANTLR start "qualifiedName"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:577:1: qualifiedName : Identifier ( '.' Identifier )* ;
    public final void qualifiedName() throws RecognitionException {
        int qualifiedName_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 60) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:578:5: ( Identifier ( '.' Identifier )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:578:7: Identifier ( '.' Identifier )*
            {
            match(input,Identifier,FOLLOW_Identifier_in_qualifiedName2123); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:578:18: ( '.' Identifier )*
            loop83:
            do {
                int alt83=2;
                int LA83_0 = input.LA(1);

                if ( (LA83_0==28) ) {
                    alt83=1;
                }


                switch (alt83) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:578:19: '.' Identifier
            	    {
            	    match(input,28,FOLLOW_28_in_qualifiedName2126); if (state.failed) return ;
            	    match(input,Identifier,FOLLOW_Identifier_in_qualifiedName2128); if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 60, qualifiedName_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "qualifiedName"


    // $ANTLR start "literal"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:581:1: literal : ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | booleanLiteral | 'null' );
    public final void literal() throws RecognitionException {
        int literal_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 61) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:582:5: ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | booleanLiteral | 'null' )
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
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 84, 0, input);

                throw nvae;
            }

            switch (alt84) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:582:9: integerLiteral
                    {
                    pushFollow(FOLLOW_integerLiteral_in_literal2150);
                    integerLiteral();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:583:9: FloatingPointLiteral
                    {
                    match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_literal2160); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:584:9: CharacterLiteral
                    {
                    match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_literal2170); if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:585:9: StringLiteral
                    {
                    match(input,StringLiteral,FOLLOW_StringLiteral_in_literal2180); if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:586:9: booleanLiteral
                    {
                    pushFollow(FOLLOW_booleanLiteral_in_literal2190);
                    booleanLiteral();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:587:9: 'null'
                    {
                    match(input,68,FOLLOW_68_in_literal2200); if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 61, literal_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "literal"


    // $ANTLR start "integerLiteral"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:590:1: integerLiteral : ( HexLiteral | OctalLiteral | DecimalLiteral );
    public final void integerLiteral() throws RecognitionException {
        int integerLiteral_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 62) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:591:5: ( HexLiteral | OctalLiteral | DecimalLiteral )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:
            {
            if ( (input.LA(1)>=HexLiteral && input.LA(1)<=DecimalLiteral) ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
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
            if ( state.backtracking>0 ) { memoize(input, 62, integerLiteral_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "integerLiteral"


    // $ANTLR start "booleanLiteral"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:596:1: booleanLiteral : ( 'true' | 'false' );
    public final void booleanLiteral() throws RecognitionException {
        int booleanLiteral_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 63) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:597:5: ( 'true' | 'false' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:
            {
            if ( (input.LA(1)>=69 && input.LA(1)<=70) ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
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
            if ( state.backtracking>0 ) { memoize(input, 63, booleanLiteral_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "booleanLiteral"


    // $ANTLR start "annotations"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:603:1: annotations : ( annotation )+ ;
    public final void annotations() throws RecognitionException {
        int annotations_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 64) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:604:5: ( ( annotation )+ )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:604:7: ( annotation )+
            {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:604:7: ( annotation )+
            int cnt85=0;
            loop85:
            do {
                int alt85=2;
                alt85 = dfa85.predict(input);
                switch (alt85) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: annotation
            	    {
            	    pushFollow(FOLLOW_annotation_in_annotations2287);
            	    annotation();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt85 >= 1 ) break loop85;
            	    if (state.backtracking>0) {state.failed=true; return ;}
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
            if ( state.backtracking>0 ) { memoize(input, 64, annotations_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "annotations"


    // $ANTLR start "annotation"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:607:1: annotation : '@' annotationName ( '(' ( elementValuePairs )? ')' )? ;
    public final void annotation() throws RecognitionException {
        int annotation_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 65) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:608:5: ( '@' annotationName ( '(' ( elementValuePairs )? ')' )? )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:608:7: '@' annotationName ( '(' ( elementValuePairs )? ')' )?
            {
            match(input,71,FOLLOW_71_in_annotation2305); if (state.failed) return ;
            pushFollow(FOLLOW_annotationName_in_annotation2307);
            annotationName();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:608:26: ( '(' ( elementValuePairs )? ')' )?
            int alt87=2;
            alt87 = dfa87.predict(input);
            switch (alt87) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:608:27: '(' ( elementValuePairs )? ')'
                    {
                    match(input,65,FOLLOW_65_in_annotation2310); if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:608:31: ( elementValuePairs )?
                    int alt86=2;
                    alt86 = dfa86.predict(input);
                    switch (alt86) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: elementValuePairs
                            {
                            pushFollow(FOLLOW_elementValuePairs_in_annotation2312);
                            elementValuePairs();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    match(input,66,FOLLOW_66_in_annotation2315); if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 65, annotation_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "annotation"


    // $ANTLR start "annotationName"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:611:1: annotationName : Identifier ( '.' Identifier )* ;
    public final void annotationName() throws RecognitionException {
        int annotationName_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 66) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:612:5: ( Identifier ( '.' Identifier )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:612:7: Identifier ( '.' Identifier )*
            {
            match(input,Identifier,FOLLOW_Identifier_in_annotationName2334); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:612:18: ( '.' Identifier )*
            loop88:
            do {
                int alt88=2;
                alt88 = dfa88.predict(input);
                switch (alt88) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:612:19: '.' Identifier
            	    {
            	    match(input,28,FOLLOW_28_in_annotationName2337); if (state.failed) return ;
            	    match(input,Identifier,FOLLOW_Identifier_in_annotationName2339); if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 66, annotationName_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "annotationName"


    // $ANTLR start "elementValuePairs"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:615:1: elementValuePairs : elementValuePair ( ',' elementValuePair )* ;
    public final void elementValuePairs() throws RecognitionException {
        int elementValuePairs_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 67) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:616:5: ( elementValuePair ( ',' elementValuePair )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:616:7: elementValuePair ( ',' elementValuePair )*
            {
            pushFollow(FOLLOW_elementValuePair_in_elementValuePairs2358);
            elementValuePair();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:616:24: ( ',' elementValuePair )*
            loop89:
            do {
                int alt89=2;
                int LA89_0 = input.LA(1);

                if ( (LA89_0==34) ) {
                    alt89=1;
                }


                switch (alt89) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:616:25: ',' elementValuePair
            	    {
            	    match(input,34,FOLLOW_34_in_elementValuePairs2361); if (state.failed) return ;
            	    pushFollow(FOLLOW_elementValuePair_in_elementValuePairs2363);
            	    elementValuePair();

            	    state._fsp--;
            	    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 67, elementValuePairs_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "elementValuePairs"


    // $ANTLR start "elementValuePair"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:619:1: elementValuePair : ( Identifier '=' )? elementValue ;
    public final void elementValuePair() throws RecognitionException {
        int elementValuePair_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 68) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:620:5: ( ( Identifier '=' )? elementValue )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:620:7: ( Identifier '=' )? elementValue
            {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:620:7: ( Identifier '=' )?
            int alt90=2;
            alt90 = dfa90.predict(input);
            switch (alt90) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:620:8: Identifier '='
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_elementValuePair2383); if (state.failed) return ;
                    match(input,44,FOLLOW_44_in_elementValuePair2385); if (state.failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_elementValue_in_elementValuePair2389);
            elementValue();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 68, elementValuePair_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "elementValuePair"


    // $ANTLR start "elementValue"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:623:1: elementValue : ( conditionalExpression | annotation | elementValueArrayInitializer );
    public final void elementValue() throws RecognitionException {
        int elementValue_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 69) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:624:5: ( conditionalExpression | annotation | elementValueArrayInitializer )
            int alt91=3;
            alt91 = dfa91.predict(input);
            switch (alt91) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:624:7: conditionalExpression
                    {
                    pushFollow(FOLLOW_conditionalExpression_in_elementValue2406);
                    conditionalExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:625:9: annotation
                    {
                    pushFollow(FOLLOW_annotation_in_elementValue2416);
                    annotation();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:626:9: elementValueArrayInitializer
                    {
                    pushFollow(FOLLOW_elementValueArrayInitializer_in_elementValue2426);
                    elementValueArrayInitializer();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 69, elementValue_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "elementValue"


    // $ANTLR start "elementValueArrayInitializer"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:629:1: elementValueArrayInitializer : '{' ( elementValue ( ',' elementValue )* )? '}' ;
    public final void elementValueArrayInitializer() throws RecognitionException {
        int elementValueArrayInitializer_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 70) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:630:5: ( '{' ( elementValue ( ',' elementValue )* )? '}' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:630:7: '{' ( elementValue ( ',' elementValue )* )? '}'
            {
            match(input,37,FOLLOW_37_in_elementValueArrayInitializer2443); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:630:11: ( elementValue ( ',' elementValue )* )?
            int alt93=2;
            alt93 = dfa93.predict(input);
            switch (alt93) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:630:12: elementValue ( ',' elementValue )*
                    {
                    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer2446);
                    elementValue();

                    state._fsp--;
                    if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:630:25: ( ',' elementValue )*
                    loop92:
                    do {
                        int alt92=2;
                        int LA92_0 = input.LA(1);

                        if ( (LA92_0==34) ) {
                            alt92=1;
                        }


                        switch (alt92) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:630:26: ',' elementValue
                    	    {
                    	    match(input,34,FOLLOW_34_in_elementValueArrayInitializer2449); if (state.failed) return ;
                    	    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer2451);
                    	    elementValue();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop92;
                        }
                    } while (true);


                    }
                    break;

            }

            match(input,38,FOLLOW_38_in_elementValueArrayInitializer2458); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 70, elementValueArrayInitializer_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "elementValueArrayInitializer"


    // $ANTLR start "annotationTypeDeclaration"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:633:1: annotationTypeDeclaration : '@' 'interface' Identifier annotationTypeBody ;
    public final void annotationTypeDeclaration() throws RecognitionException {
        int annotationTypeDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 71) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:634:5: ( '@' 'interface' Identifier annotationTypeBody )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:634:7: '@' 'interface' Identifier annotationTypeBody
            {
            match(input,71,FOLLOW_71_in_annotationTypeDeclaration2475); if (state.failed) return ;
            match(input,39,FOLLOW_39_in_annotationTypeDeclaration2477); if (state.failed) return ;
            match(input,Identifier,FOLLOW_Identifier_in_annotationTypeDeclaration2479); if (state.failed) return ;
            pushFollow(FOLLOW_annotationTypeBody_in_annotationTypeDeclaration2481);
            annotationTypeBody();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 71, annotationTypeDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "annotationTypeDeclaration"


    // $ANTLR start "annotationTypeBody"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:637:1: annotationTypeBody : '{' ( annotationTypeElementDeclarations )? '}' ;
    public final void annotationTypeBody() throws RecognitionException {
        int annotationTypeBody_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 72) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:638:5: ( '{' ( annotationTypeElementDeclarations )? '}' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:638:7: '{' ( annotationTypeElementDeclarations )? '}'
            {
            match(input,37,FOLLOW_37_in_annotationTypeBody2498); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:638:11: ( annotationTypeElementDeclarations )?
            int alt94=2;
            alt94 = dfa94.predict(input);
            switch (alt94) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:638:12: annotationTypeElementDeclarations
                    {
                    pushFollow(FOLLOW_annotationTypeElementDeclarations_in_annotationTypeBody2501);
                    annotationTypeElementDeclarations();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,38,FOLLOW_38_in_annotationTypeBody2505); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 72, annotationTypeBody_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "annotationTypeBody"


    // $ANTLR start "annotationTypeElementDeclarations"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:641:1: annotationTypeElementDeclarations : ( annotationTypeElementDeclaration ) ( annotationTypeElementDeclaration )* ;
    public final void annotationTypeElementDeclarations() throws RecognitionException {
        int annotationTypeElementDeclarations_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 73) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:642:5: ( ( annotationTypeElementDeclaration ) ( annotationTypeElementDeclaration )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:642:7: ( annotationTypeElementDeclaration ) ( annotationTypeElementDeclaration )*
            {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:642:7: ( annotationTypeElementDeclaration )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:642:8: annotationTypeElementDeclaration
            {
            pushFollow(FOLLOW_annotationTypeElementDeclaration_in_annotationTypeElementDeclarations2523);
            annotationTypeElementDeclaration();

            state._fsp--;
            if (state.failed) return ;

            }

            // src/main/resources/org/drools/semantics/java/parser/Java.g:642:42: ( annotationTypeElementDeclaration )*
            loop95:
            do {
                int alt95=2;
                alt95 = dfa95.predict(input);
                switch (alt95) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:642:43: annotationTypeElementDeclaration
            	    {
            	    pushFollow(FOLLOW_annotationTypeElementDeclaration_in_annotationTypeElementDeclarations2527);
            	    annotationTypeElementDeclaration();

            	    state._fsp--;
            	    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 73, annotationTypeElementDeclarations_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "annotationTypeElementDeclarations"


    // $ANTLR start "annotationTypeElementDeclaration"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:645:1: annotationTypeElementDeclaration : ( modifier )* annotationTypeElementRest ;
    public final void annotationTypeElementDeclaration() throws RecognitionException {
        int annotationTypeElementDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 74) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:646:5: ( ( modifier )* annotationTypeElementRest )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:646:7: ( modifier )* annotationTypeElementRest
            {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:646:7: ( modifier )*
            loop96:
            do {
                int alt96=2;
                alt96 = dfa96.predict(input);
                switch (alt96) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:646:8: modifier
            	    {
            	    pushFollow(FOLLOW_modifier_in_annotationTypeElementDeclaration2547);
            	    modifier();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop96;
                }
            } while (true);

            pushFollow(FOLLOW_annotationTypeElementRest_in_annotationTypeElementDeclaration2551);
            annotationTypeElementRest();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 74, annotationTypeElementDeclaration_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "annotationTypeElementDeclaration"


    // $ANTLR start "annotationTypeElementRest"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:649:1: annotationTypeElementRest : ( type annotationMethodOrConstantRest ';' | classDeclaration ( ';' )? | interfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? );
    public final void annotationTypeElementRest() throws RecognitionException {
        int annotationTypeElementRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 75) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:650:5: ( type annotationMethodOrConstantRest ';' | classDeclaration ( ';' )? | interfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? )
            int alt101=5;
            alt101 = dfa101.predict(input);
            switch (alt101) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:650:7: type annotationMethodOrConstantRest ';'
                    {
                    pushFollow(FOLLOW_type_in_annotationTypeElementRest2568);
                    type();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_annotationMethodOrConstantRest_in_annotationTypeElementRest2570);
                    annotationMethodOrConstantRest();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,25,FOLLOW_25_in_annotationTypeElementRest2572); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:651:9: classDeclaration ( ';' )?
                    {
                    pushFollow(FOLLOW_classDeclaration_in_annotationTypeElementRest2582);
                    classDeclaration();

                    state._fsp--;
                    if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:651:26: ( ';' )?
                    int alt97=2;
                    alt97 = dfa97.predict(input);
                    switch (alt97) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ';'
                            {
                            match(input,25,FOLLOW_25_in_annotationTypeElementRest2584); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:652:9: interfaceDeclaration ( ';' )?
                    {
                    pushFollow(FOLLOW_interfaceDeclaration_in_annotationTypeElementRest2595);
                    interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:652:30: ( ';' )?
                    int alt98=2;
                    alt98 = dfa98.predict(input);
                    switch (alt98) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ';'
                            {
                            match(input,25,FOLLOW_25_in_annotationTypeElementRest2597); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:653:9: enumDeclaration ( ';' )?
                    {
                    pushFollow(FOLLOW_enumDeclaration_in_annotationTypeElementRest2608);
                    enumDeclaration();

                    state._fsp--;
                    if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:653:25: ( ';' )?
                    int alt99=2;
                    alt99 = dfa99.predict(input);
                    switch (alt99) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ';'
                            {
                            match(input,25,FOLLOW_25_in_annotationTypeElementRest2610); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:654:9: annotationTypeDeclaration ( ';' )?
                    {
                    pushFollow(FOLLOW_annotationTypeDeclaration_in_annotationTypeElementRest2621);
                    annotationTypeDeclaration();

                    state._fsp--;
                    if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:654:35: ( ';' )?
                    int alt100=2;
                    alt100 = dfa100.predict(input);
                    switch (alt100) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ';'
                            {
                            match(input,25,FOLLOW_25_in_annotationTypeElementRest2623); if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 75, annotationTypeElementRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "annotationTypeElementRest"


    // $ANTLR start "annotationMethodOrConstantRest"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:657:1: annotationMethodOrConstantRest : ( annotationMethodRest | annotationConstantRest );
    public final void annotationMethodOrConstantRest() throws RecognitionException {
        int annotationMethodOrConstantRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 76) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:658:5: ( annotationMethodRest | annotationConstantRest )
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
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 102, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 102, 0, input);

                throw nvae;
            }
            switch (alt102) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:658:7: annotationMethodRest
                    {
                    pushFollow(FOLLOW_annotationMethodRest_in_annotationMethodOrConstantRest2641);
                    annotationMethodRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:659:9: annotationConstantRest
                    {
                    pushFollow(FOLLOW_annotationConstantRest_in_annotationMethodOrConstantRest2651);
                    annotationConstantRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 76, annotationMethodOrConstantRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "annotationMethodOrConstantRest"


    // $ANTLR start "annotationMethodRest"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:662:1: annotationMethodRest : Identifier '(' ')' ( defaultValue )? ;
    public final void annotationMethodRest() throws RecognitionException {
        int annotationMethodRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 77) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:663:6: ( Identifier '(' ')' ( defaultValue )? )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:663:8: Identifier '(' ')' ( defaultValue )?
            {
            match(input,Identifier,FOLLOW_Identifier_in_annotationMethodRest2669); if (state.failed) return ;
            match(input,65,FOLLOW_65_in_annotationMethodRest2671); if (state.failed) return ;
            match(input,66,FOLLOW_66_in_annotationMethodRest2673); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:663:27: ( defaultValue )?
            int alt103=2;
            int LA103_0 = input.LA(1);

            if ( (LA103_0==72) ) {
                alt103=1;
            }
            switch (alt103) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:663:28: defaultValue
                    {
                    pushFollow(FOLLOW_defaultValue_in_annotationMethodRest2676);
                    defaultValue();

                    state._fsp--;
                    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 77, annotationMethodRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "annotationMethodRest"


    // $ANTLR start "annotationConstantRest"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:666:1: annotationConstantRest : variableDeclarators ;
    public final void annotationConstantRest() throws RecognitionException {
        VarDecl_stack.push(new VarDecl_scope());

        int annotationConstantRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 78) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:668:6: ( variableDeclarators )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:668:8: variableDeclarators
            {
            pushFollow(FOLLOW_variableDeclarators_in_annotationConstantRest2702);
            variableDeclarators();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 78, annotationConstantRest_StartIndex); }
            VarDecl_stack.pop();

        }
        return ;
    }
    // $ANTLR end "annotationConstantRest"


    // $ANTLR start "defaultValue"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:671:1: defaultValue : 'default' elementValue ;
    public final void defaultValue() throws RecognitionException {
        int defaultValue_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 79) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:672:6: ( 'default' elementValue )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:672:8: 'default' elementValue
            {
            match(input,72,FOLLOW_72_in_defaultValue2721); if (state.failed) return ;
            pushFollow(FOLLOW_elementValue_in_defaultValue2723);
            elementValue();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 79, defaultValue_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "defaultValue"


    // $ANTLR start "block"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:677:1: block : '{' ( blockStatement )* '}' ;
    public final void block() throws RecognitionException {
        int block_StartIndex = input.index();

                    increaseLevel();
                
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 80) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:688:5: ( '{' ( blockStatement )* '}' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:688:7: '{' ( blockStatement )* '}'
            {
            match(input,37,FOLLOW_37_in_block2769); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:688:11: ( blockStatement )*
            loop104:
            do {
                int alt104=2;
                alt104 = dfa104.predict(input);
                switch (alt104) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_block2771);
            	    blockStatement();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop104;
                }
            } while (true);

            match(input,38,FOLLOW_38_in_block2774); if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 80, block_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "block"


    // $ANTLR start "blockStatement"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:691:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );
    public final void blockStatement() throws RecognitionException {
        int blockStatement_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 81) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:692:5: ( localVariableDeclaration | classOrInterfaceDeclaration | statement )
            int alt105=3;
            alt105 = dfa105.predict(input);
            switch (alt105) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:692:7: localVariableDeclaration
                    {
                    pushFollow(FOLLOW_localVariableDeclaration_in_blockStatement2791);
                    localVariableDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:693:7: classOrInterfaceDeclaration
                    {
                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_blockStatement2799);
                    classOrInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:694:11: statement
                    {
                    pushFollow(FOLLOW_statement_in_blockStatement2811);
                    statement();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 81, blockStatement_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "blockStatement"


    // $ANTLR start "localVariableDeclaration"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:697:1: localVariableDeclaration : ( variableModifier )* type variableDeclarators ';' ;
    public final void localVariableDeclaration() throws RecognitionException {
        VarDecl_stack.push(new VarDecl_scope());

        int localVariableDeclaration_StartIndex = input.index();
        JavaParser.variableModifier_return variableModifier1 = null;

        JavaParser.type_return type2 = null;



                    ((VarDecl_scope)VarDecl_stack.peek()).descr = new JavaLocalDeclarationDescr();
                
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 82) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:705:5: ( ( variableModifier )* type variableDeclarators ';' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:706:5: ( variableModifier )* type variableDeclarators ';'
            {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:706:5: ( variableModifier )*
            loop106:
            do {
                int alt106=2;
                int LA106_0 = input.LA(1);

                if ( (LA106_0==49||LA106_0==71) ) {
                    alt106=1;
                }


                switch (alt106) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:706:7: variableModifier
            	    {
            	    pushFollow(FOLLOW_variableModifier_in_localVariableDeclaration2865);
            	    variableModifier1=variableModifier();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    if ( state.backtracking==0 ) {

            	                  ((VarDecl_scope)VarDecl_stack.peek()).descr.updateStart( ((CommonToken)(variableModifier1!=null?((Token)variableModifier1.start):null)).getStartIndex() - 1 );
            	                  ((VarDecl_scope)VarDecl_stack.peek()).descr.addModifier( (variableModifier1!=null?input.toString(variableModifier1.start,variableModifier1.stop):null) );
            	              
            	    }

            	    }
            	    break;

            	default :
            	    break loop106;
                }
            } while (true);

            pushFollow(FOLLOW_type_in_localVariableDeclaration2888);
            type2=type();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {

                          ((VarDecl_scope)VarDecl_stack.peek()).descr.updateStart( ((CommonToken)(type2!=null?((Token)type2.start):null)).getStartIndex() - 1 );
                          ((VarDecl_scope)VarDecl_stack.peek()).descr.setType( (type2!=null?input.toString(type2.start,type2.stop):null) );
                          ((VarDecl_scope)VarDecl_stack.peek()).descr.setEnd( ((CommonToken)(type2!=null?((Token)type2.stop):null)).getStopIndex() );
                      
            }
            pushFollow(FOLLOW_variableDeclarators_in_localVariableDeclaration2904);
            variableDeclarators();

            state._fsp--;
            if (state.failed) return ;
            match(input,25,FOLLOW_25_in_localVariableDeclaration2906); if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {

                          addLocalDeclaration( ((VarDecl_scope)VarDecl_stack.peek()).descr );
                      
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 82, localVariableDeclaration_StartIndex); }
            VarDecl_stack.pop();

        }
        return ;
    }
    // $ANTLR end "localVariableDeclaration"

    public static class statement_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "statement"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:721:1: statement : ( block | 'assert' expression ( ':' expression )? ';' | ifStatement | forStatement | whileStatement | 'do' statement 'while' parExpression ';' | tryStatement | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | throwStatement | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | modifyStatement | updateStatement | retractStatement | ';' | statementExpression ';' | Identifier ':' statement );
    public final JavaParser.statement_return statement() throws RecognitionException {
        JavaParser.statement_return retval = new JavaParser.statement_return();
        retval.start = input.LT(1);
        int statement_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 83) ) { return retval; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:722:5: ( block | 'assert' expression ( ':' expression )? ';' | ifStatement | forStatement | whileStatement | 'do' statement 'while' parExpression ';' | tryStatement | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | throwStatement | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | modifyStatement | updateStatement | retractStatement | ';' | statementExpression ';' | Identifier ':' statement )
            int alt111=19;
            alt111 = dfa111.predict(input);
            switch (alt111) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:722:7: block
                    {
                    pushFollow(FOLLOW_block_in_statement2923);
                    block();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:723:7: 'assert' expression ( ':' expression )? ';'
                    {
                    match(input,73,FOLLOW_73_in_statement2931); if (state.failed) return retval;
                    pushFollow(FOLLOW_expression_in_statement2933);
                    expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:723:27: ( ':' expression )?
                    int alt107=2;
                    int LA107_0 = input.LA(1);

                    if ( (LA107_0==74) ) {
                        alt107=1;
                    }
                    switch (alt107) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:723:28: ':' expression
                            {
                            match(input,74,FOLLOW_74_in_statement2936); if (state.failed) return retval;
                            pushFollow(FOLLOW_expression_in_statement2938);
                            expression();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }

                    match(input,25,FOLLOW_25_in_statement2942); if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:724:7: ifStatement
                    {
                    pushFollow(FOLLOW_ifStatement_in_statement2950);
                    ifStatement();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:725:7: forStatement
                    {
                    pushFollow(FOLLOW_forStatement_in_statement2958);
                    forStatement();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:726:7: whileStatement
                    {
                    pushFollow(FOLLOW_whileStatement_in_statement2967);
                    whileStatement();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:727:7: 'do' statement 'while' parExpression ';'
                    {
                    match(input,75,FOLLOW_75_in_statement2975); if (state.failed) return retval;
                    pushFollow(FOLLOW_statement_in_statement2977);
                    statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,76,FOLLOW_76_in_statement2979); if (state.failed) return retval;
                    pushFollow(FOLLOW_parExpression_in_statement2981);
                    parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,25,FOLLOW_25_in_statement2983); if (state.failed) return retval;

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:729:7: tryStatement
                    {
                    pushFollow(FOLLOW_tryStatement_in_statement2996);
                    tryStatement();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:731:7: 'switch' parExpression '{' switchBlockStatementGroups '}'
                    {
                    match(input,77,FOLLOW_77_in_statement3011); if (state.failed) return retval;
                    pushFollow(FOLLOW_parExpression_in_statement3013);
                    parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,37,FOLLOW_37_in_statement3015); if (state.failed) return retval;
                    pushFollow(FOLLOW_switchBlockStatementGroups_in_statement3017);
                    switchBlockStatementGroups();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,38,FOLLOW_38_in_statement3019); if (state.failed) return retval;

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:732:7: 'synchronized' parExpression block
                    {
                    match(input,51,FOLLOW_51_in_statement3027); if (state.failed) return retval;
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
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:733:7: 'return' ( expression )? ';'
                    {
                    match(input,78,FOLLOW_78_in_statement3039); if (state.failed) return retval;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:733:16: ( expression )?
                    int alt108=2;
                    alt108 = dfa108.predict(input);
                    switch (alt108) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: expression
                            {
                            pushFollow(FOLLOW_expression_in_statement3041);
                            expression();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }

                    match(input,25,FOLLOW_25_in_statement3044); if (state.failed) return retval;

                    }
                    break;
                case 11 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:734:8: throwStatement
                    {
                    pushFollow(FOLLOW_throwStatement_in_statement3053);
                    throwStatement();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 12 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:735:7: 'break' ( Identifier )? ';'
                    {
                    match(input,79,FOLLOW_79_in_statement3061); if (state.failed) return retval;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:735:15: ( Identifier )?
                    int alt109=2;
                    int LA109_0 = input.LA(1);

                    if ( (LA109_0==Identifier) ) {
                        alt109=1;
                    }
                    switch (alt109) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: Identifier
                            {
                            match(input,Identifier,FOLLOW_Identifier_in_statement3063); if (state.failed) return retval;

                            }
                            break;

                    }

                    match(input,25,FOLLOW_25_in_statement3066); if (state.failed) return retval;

                    }
                    break;
                case 13 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:736:7: 'continue' ( Identifier )? ';'
                    {
                    match(input,80,FOLLOW_80_in_statement3074); if (state.failed) return retval;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:736:18: ( Identifier )?
                    int alt110=2;
                    int LA110_0 = input.LA(1);

                    if ( (LA110_0==Identifier) ) {
                        alt110=1;
                    }
                    switch (alt110) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: Identifier
                            {
                            match(input,Identifier,FOLLOW_Identifier_in_statement3076); if (state.failed) return retval;

                            }
                            break;

                    }

                    match(input,25,FOLLOW_25_in_statement3079); if (state.failed) return retval;

                    }
                    break;
                case 14 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:738:7: modifyStatement
                    {
                    pushFollow(FOLLOW_modifyStatement_in_statement3092);
                    modifyStatement();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 15 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:738:28: updateStatement
                    {
                    pushFollow(FOLLOW_updateStatement_in_statement3099);
                    updateStatement();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 16 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:738:49: retractStatement
                    {
                    pushFollow(FOLLOW_retractStatement_in_statement3106);
                    retractStatement();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 17 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:739:7: ';'
                    {
                    match(input,25,FOLLOW_25_in_statement3114); if (state.failed) return retval;

                    }
                    break;
                case 18 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:740:7: statementExpression ';'
                    {
                    pushFollow(FOLLOW_statementExpression_in_statement3122);
                    statementExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,25,FOLLOW_25_in_statement3124); if (state.failed) return retval;

                    }
                    break;
                case 19 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:741:7: Identifier ':' statement
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_statement3132); if (state.failed) return retval;
                    match(input,74,FOLLOW_74_in_statement3134); if (state.failed) return retval;
                    pushFollow(FOLLOW_statement_in_statement3136);
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
            if ( state.backtracking>0 ) { memoize(input, 83, statement_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "statement"


    // $ANTLR start "throwStatement"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:745:1: throwStatement : s= 'throw' expression c= ';' ;
    public final void throwStatement() throws RecognitionException {
        int throwStatement_StartIndex = input.index();
        Token s=null;
        Token c=null;
        JavaParser.expression_return expression3 = null;



                JavaThrowBlockDescr d = null;
            
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 84) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:749:5: (s= 'throw' expression c= ';' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:749:7: s= 'throw' expression c= ';'
            {
            s=(Token)match(input,81,FOLLOW_81_in_throwStatement3169); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_throwStatement3175);
            expression3=expression();

            state._fsp--;
            if (state.failed) return ;
            c=(Token)match(input,25,FOLLOW_25_in_throwStatement3185); if (state.failed) return ;
            if ( state.backtracking==0 ) {

                      d = new JavaThrowBlockDescr( );
                      d.setStart( ((CommonToken)s).getStartIndex() );
                      d.setTextStart( ((CommonToken)(expression3!=null?((Token)expression3.start):null)).getStartIndex() );        
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
            if ( state.backtracking>0 ) { memoize(input, 84, throwStatement_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "throwStatement"


    // $ANTLR start "ifStatement"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:761:1: ifStatement : s= 'if' parExpression x= statement (y= 'else' ( 'if' parExpression )? z= statement )* ;
    public final void ifStatement() throws RecognitionException {
        int ifStatement_StartIndex = input.index();
        Token s=null;
        Token y=null;
        JavaParser.statement_return x = null;

        JavaParser.statement_return z = null;



                 JavaIfBlockDescr id = null;
                 JavaElseBlockDescr ed = null;         
            
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 85) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:766:5: (s= 'if' parExpression x= statement (y= 'else' ( 'if' parExpression )? z= statement )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:768:5: s= 'if' parExpression x= statement (y= 'else' ( 'if' parExpression )? z= statement )*
            {
            s=(Token)match(input,82,FOLLOW_82_in_ifStatement3241); if (state.failed) return ;
            pushFollow(FOLLOW_parExpression_in_ifStatement3243);
            parExpression();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {

                      increaseLevel();
                      id = new JavaIfBlockDescr();
                      id.setStart( ((CommonToken)s).getStartIndex() );  pushContainerBlockDescr(id, true); 
                  
            }
            pushFollow(FOLLOW_statement_in_ifStatement3261);
            x=statement();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {

                      decreaseLevel();
                      id.setTextStart(((CommonToken)(x!=null?((Token)x.start):null)).getStartIndex() );
                      id.setEnd(((CommonToken)(x!=null?((Token)x.stop):null)).getStopIndex() ); popContainerBlockDescr(); 
                  
            }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:781:5: (y= 'else' ( 'if' parExpression )? z= statement )*
            loop113:
            do {
                int alt113=2;
                alt113 = dfa113.predict(input);
                switch (alt113) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:782:6: y= 'else' ( 'if' parExpression )? z= statement
            	    {
            	    y=(Token)match(input,83,FOLLOW_83_in_ifStatement3288); if (state.failed) return ;
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:782:16: ( 'if' parExpression )?
            	    int alt112=2;
            	    alt112 = dfa112.predict(input);
            	    switch (alt112) {
            	        case 1 :
            	            // src/main/resources/org/drools/semantics/java/parser/Java.g:782:17: 'if' parExpression
            	            {
            	            match(input,82,FOLLOW_82_in_ifStatement3292); if (state.failed) return ;
            	            pushFollow(FOLLOW_parExpression_in_ifStatement3294);
            	            parExpression();

            	            state._fsp--;
            	            if (state.failed) return ;

            	            }
            	            break;

            	    }

            	    if ( state.backtracking==0 ) {

            	              increaseLevel();
            	              ed = new JavaElseBlockDescr();
            	              ed.setStart( ((CommonToken)y).getStartIndex() );  pushContainerBlockDescr(ed, true); 
            	          
            	    }
            	    pushFollow(FOLLOW_statement_in_ifStatement3325);
            	    z=statement();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    if ( state.backtracking==0 ) {

            	              decreaseLevel();
            	              ed.setTextStart(((CommonToken)(z!=null?((Token)z.start):null)).getStartIndex() );
            	              ed.setEnd(((CommonToken)(z!=null?((Token)z.stop):null)).getStopIndex() ); popContainerBlockDescr();               
            	          
            	    }

            	    }
            	    break;

            	default :
            	    break loop113;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 85, ifStatement_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "ifStatement"


    // $ANTLR start "forStatement"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:796:1: forStatement options {k=3; } : x= 'for' y= '(' ( ( ( variableModifier )* type id= Identifier z= ':' expression ) | ( ( forInit )? z= ';' ( expression )? ';' ( forUpdate )? ) ) ')' bs= statement ;
    public final void forStatement() throws RecognitionException {
        VarDecl_stack.push(new VarDecl_scope());

        int forStatement_StartIndex = input.index();
        Token x=null;
        Token y=null;
        Token id=null;
        Token z=null;
        JavaParser.statement_return bs = null;

        JavaParser.variableModifier_return variableModifier4 = null;

        JavaParser.type_return type5 = null;



                 JavaForBlockDescr fd = null;
                 increaseLevel();
                 ((VarDecl_scope)VarDecl_stack.peek()).descr = new JavaLocalDeclarationDescr();
            
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 86) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:808:5: (x= 'for' y= '(' ( ( ( variableModifier )* type id= Identifier z= ':' expression ) | ( ( forInit )? z= ';' ( expression )? ';' ( forUpdate )? ) ) ')' bs= statement )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:809:5: x= 'for' y= '(' ( ( ( variableModifier )* type id= Identifier z= ':' expression ) | ( ( forInit )? z= ';' ( expression )? ';' ( forUpdate )? ) ) ')' bs= statement
            {
            x=(Token)match(input,84,FOLLOW_84_in_forStatement3407); if (state.failed) return ;
            y=(Token)match(input,65,FOLLOW_65_in_forStatement3411); if (state.failed) return ;
            if ( state.backtracking==0 ) {
                 fd = new JavaForBlockDescr( ); 
                      fd.setStart( ((CommonToken)x).getStartIndex() ); pushContainerBlockDescr(fd, true);    
                      fd.setStartParen( ((CommonToken)y).getStartIndex() );            
                  
            }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:814:5: ( ( ( variableModifier )* type id= Identifier z= ':' expression ) | ( ( forInit )? z= ';' ( expression )? ';' ( forUpdate )? ) )
            int alt118=2;
            alt118 = dfa118.predict(input);
            switch (alt118) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:815:9: ( ( variableModifier )* type id= Identifier z= ':' expression )
                    {
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:815:9: ( ( variableModifier )* type id= Identifier z= ':' expression )
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:815:11: ( variableModifier )* type id= Identifier z= ':' expression
                    {
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:815:11: ( variableModifier )*
                    loop114:
                    do {
                        int alt114=2;
                        int LA114_0 = input.LA(1);

                        if ( (LA114_0==49||LA114_0==71) ) {
                            alt114=1;
                        }


                        switch (alt114) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:815:13: variableModifier
                    	    {
                    	    pushFollow(FOLLOW_variableModifier_in_forStatement3447);
                    	    variableModifier4=variableModifier();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    if ( state.backtracking==0 ) {

                    	                      ((VarDecl_scope)VarDecl_stack.peek()).descr.updateStart( ((CommonToken)(variableModifier4!=null?((Token)variableModifier4.start):null)).getStartIndex() - 1 );
                    	                      ((VarDecl_scope)VarDecl_stack.peek()).descr.addModifier( (variableModifier4!=null?input.toString(variableModifier4.start,variableModifier4.stop):null) );
                    	                  
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop114;
                        }
                    } while (true);

                    pushFollow(FOLLOW_type_in_forStatement3486);
                    type5=type();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {

                                  ((VarDecl_scope)VarDecl_stack.peek()).descr.updateStart( ((CommonToken)(type5!=null?((Token)type5.start):null)).getStartIndex() - 1 );
                                  ((VarDecl_scope)VarDecl_stack.peek()).descr.setType( (type5!=null?input.toString(type5.start,type5.stop):null) );
                                  ((VarDecl_scope)VarDecl_stack.peek()).descr.setEnd( ((CommonToken)(type5!=null?((Token)type5.stop):null)).getStopIndex() );
                                
                    }
                    id=(Token)match(input,Identifier,FOLLOW_Identifier_in_forStatement3512); if (state.failed) return ;
                    if ( state.backtracking==0 ) {

                                  JavaLocalDeclarationDescr.IdentifierDescr ident = new JavaLocalDeclarationDescr.IdentifierDescr();
                                  ident.setIdentifier( (id!=null?id.getText():null) );
                                  ident.setStart( ((CommonToken)id).getStartIndex() - 1 );
                                  ident.setEnd( ((CommonToken)id).getStopIndex() );
                                  ((VarDecl_scope)VarDecl_stack.peek()).descr.addIdentifier( ident );
                                
                    }
                    z=(Token)match(input,74,FOLLOW_74_in_forStatement3539); if (state.failed) return ;
                    pushFollow(FOLLOW_expression_in_forStatement3541);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {

                                   fd.setInitEnd( ((CommonToken)z).getStartIndex() );        
                                
                    }

                    }


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:840:9: ( ( forInit )? z= ';' ( expression )? ';' ( forUpdate )? )
                    {
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:840:9: ( ( forInit )? z= ';' ( expression )? ';' ( forUpdate )? )
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:840:10: ( forInit )? z= ';' ( expression )? ';' ( forUpdate )?
                    {
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:840:10: ( forInit )?
                    int alt115=2;
                    alt115 = dfa115.predict(input);
                    switch (alt115) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: forInit
                            {
                            pushFollow(FOLLOW_forInit_in_forStatement3577);
                            forInit();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    z=(Token)match(input,25,FOLLOW_25_in_forStatement3582); if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:840:25: ( expression )?
                    int alt116=2;
                    alt116 = dfa116.predict(input);
                    switch (alt116) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: expression
                            {
                            pushFollow(FOLLOW_expression_in_forStatement3584);
                            expression();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    match(input,25,FOLLOW_25_in_forStatement3587); if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:840:41: ( forUpdate )?
                    int alt117=2;
                    alt117 = dfa117.predict(input);
                    switch (alt117) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: forUpdate
                            {
                            pushFollow(FOLLOW_forUpdate_in_forStatement3589);
                            forUpdate();

                            state._fsp--;
                            if (state.failed) return ;

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

            match(input,66,FOLLOW_66_in_forStatement3635); if (state.failed) return ;
            pushFollow(FOLLOW_statement_in_forStatement3639);
            bs=statement();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
                              
                      fd.setTextStart(((CommonToken)(bs!=null?((Token)bs.start):null)).getStartIndex() );
                      fd.setEnd(((CommonToken)(bs!=null?((Token)bs.stop):null)).getStopIndex() ); popContainerBlockDescr();     
                  
            }

            }

            if ( state.backtracking==0 ) {

                       addLocalDeclaration( ((VarDecl_scope)VarDecl_stack.peek()).descr );
                       decreaseLevel();
                  
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 86, forStatement_StartIndex); }
            VarDecl_stack.pop();

        }
        return ;
    }
    // $ANTLR end "forStatement"


    // $ANTLR start "whileStatement"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:854:1: whileStatement : s= 'while' parExpression bs= statement ;
    public final void whileStatement() throws RecognitionException {
        int whileStatement_StartIndex = input.index();
        Token s=null;
        JavaParser.statement_return bs = null;



                 JavaWhileBlockDescr wd = null;         
            
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 87) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:858:5: (s= 'while' parExpression bs= statement )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:860:5: s= 'while' parExpression bs= statement
            {
            s=(Token)match(input,76,FOLLOW_76_in_whileStatement3698); if (state.failed) return ;
            pushFollow(FOLLOW_parExpression_in_whileStatement3700);
            parExpression();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
                 wd = new JavaWhileBlockDescr( ); wd.setStart( ((CommonToken)s).getStartIndex() ); pushContainerBlockDescr(wd, true);    
                  
            }
            pushFollow(FOLLOW_statement_in_whileStatement3717);
            bs=statement();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
                              
                      wd.setTextStart(((CommonToken)(bs!=null?((Token)bs.start):null)).getStartIndex() );
                      wd.setEnd(((CommonToken)(bs!=null?((Token)bs.stop):null)).getStopIndex() ); popContainerBlockDescr();     
                  
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 87, whileStatement_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "whileStatement"


    // $ANTLR start "tryStatement"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:870:1: tryStatement : s= 'try' bs= '{' ( blockStatement )* c= '}' (s= 'catch' '(' formalParameter ')' bs= '{' ( blockStatement )* c= '}' )* (s= 'finally' bs= '{' ( blockStatement )* c= '}' )? ;
    public final void tryStatement() throws RecognitionException {
        int tryStatement_StartIndex = input.index();
        Token s=null;
        Token bs=null;
        Token c=null;
        JavaParser.formalParameter_return formalParameter6 = null;



                 JavaTryBlockDescr td = null;
                 JavaCatchBlockDescr cd = null;
                 JavaFinalBlockDescr fd = null;
                 
            
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 88) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:877:5: (s= 'try' bs= '{' ( blockStatement )* c= '}' (s= 'catch' '(' formalParameter ')' bs= '{' ( blockStatement )* c= '}' )* (s= 'finally' bs= '{' ( blockStatement )* c= '}' )? )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:878:5: s= 'try' bs= '{' ( blockStatement )* c= '}' (s= 'catch' '(' formalParameter ')' bs= '{' ( blockStatement )* c= '}' )* (s= 'finally' bs= '{' ( blockStatement )* c= '}' )?
            {
            s=(Token)match(input,85,FOLLOW_85_in_tryStatement3770); if (state.failed) return ;
            if ( state.backtracking==0 ) {
                 increaseLevel();
                      td = new JavaTryBlockDescr( ); td.setStart( ((CommonToken)s).getStartIndex() ); pushContainerBlockDescr(td, true);    
                  
            }
            bs=(Token)match(input,37,FOLLOW_37_in_tryStatement3781); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:881:14: ( blockStatement )*
            loop119:
            do {
                int alt119=2;
                alt119 = dfa119.predict(input);
                switch (alt119) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_tryStatement3783);
            	    blockStatement();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop119;
                }
            } while (true);

            if ( state.backtracking==0 ) {

                              
                      td.setTextStart( ((CommonToken)bs).getStartIndex() );        

                  
            }
            c=(Token)match(input,38,FOLLOW_38_in_tryStatement3794); if (state.failed) return ;
            if ( state.backtracking==0 ) {
              td.setEnd( ((CommonToken)c).getStopIndex() ); decreaseLevel(); popContainerBlockDescr();    
            }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:889:5: (s= 'catch' '(' formalParameter ')' bs= '{' ( blockStatement )* c= '}' )*
            loop121:
            do {
                int alt121=2;
                alt121 = dfa121.predict(input);
                switch (alt121) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:889:6: s= 'catch' '(' formalParameter ')' bs= '{' ( blockStatement )* c= '}'
            	    {
            	    s=(Token)match(input,86,FOLLOW_86_in_tryStatement3812); if (state.failed) return ;
            	    match(input,65,FOLLOW_65_in_tryStatement3814); if (state.failed) return ;
            	    pushFollow(FOLLOW_formalParameter_in_tryStatement3816);
            	    formalParameter6=formalParameter();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    match(input,66,FOLLOW_66_in_tryStatement3818); if (state.failed) return ;
            	    if ( state.backtracking==0 ) {
            	        increaseLevel();
            	              cd = new JavaCatchBlockDescr( (formalParameter6!=null?input.toString(formalParameter6.start,formalParameter6.stop):null) );
            	              cd.setClauseStart( ((CommonToken)(formalParameter6!=null?((Token)formalParameter6.start):null)).getStartIndex() ); 
            	              cd.setStart( ((CommonToken)s).getStartIndex() );  pushContainerBlockDescr(cd, false);
            	           
            	    }
            	    bs=(Token)match(input,37,FOLLOW_37_in_tryStatement3830); if (state.failed) return ;
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:894:15: ( blockStatement )*
            	    loop120:
            	    do {
            	        int alt120=2;
            	        alt120 = dfa120.predict(input);
            	        switch (alt120) {
            	    	case 1 :
            	    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: blockStatement
            	    	    {
            	    	    pushFollow(FOLLOW_blockStatement_in_tryStatement3832);
            	    	    blockStatement();

            	    	    state._fsp--;
            	    	    if (state.failed) return ;

            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop120;
            	        }
            	    } while (true);

            	    if ( state.backtracking==0 ) {
            	       
            	              cd.setTextStart( ((CommonToken)bs).getStartIndex() );
            	              td.addCatch( cd );        
            	           
            	    }
            	    c=(Token)match(input,38,FOLLOW_38_in_tryStatement3845); if (state.failed) return ;
            	    if ( state.backtracking==0 ) {
            	      cd.setEnd( ((CommonToken)c).getStopIndex() ); decreaseLevel(); popContainerBlockDescr(); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop121;
                }
            } while (true);

            // src/main/resources/org/drools/semantics/java/parser/Java.g:902:6: (s= 'finally' bs= '{' ( blockStatement )* c= '}' )?
            int alt123=2;
            alt123 = dfa123.predict(input);
            switch (alt123) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:902:7: s= 'finally' bs= '{' ( blockStatement )* c= '}'
                    {
                    s=(Token)match(input,87,FOLLOW_87_in_tryStatement3879); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                        increaseLevel();
                              fd = new JavaFinalBlockDescr( ); fd.setStart( ((CommonToken)s).getStartIndex() ); pushContainerBlockDescr(fd, false);
                           
                    }
                    bs=(Token)match(input,37,FOLLOW_37_in_tryStatement3891); if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:905:15: ( blockStatement )*
                    loop122:
                    do {
                        int alt122=2;
                        alt122 = dfa122.predict(input);
                        switch (alt122) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: blockStatement
                    	    {
                    	    pushFollow(FOLLOW_blockStatement_in_tryStatement3893);
                    	    blockStatement();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop122;
                        }
                    } while (true);

                    if ( state.backtracking==0 ) {

                              fd.setTextStart( ((CommonToken)bs).getStartIndex() );        
                              td.setFinally( fd );         
                            
                    }
                    c=(Token)match(input,38,FOLLOW_38_in_tryStatement3907); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      fd.setEnd( ((CommonToken)c).getStopIndex() ); decreaseLevel(); popContainerBlockDescr(); 
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
            if ( state.backtracking>0 ) { memoize(input, 88, tryStatement_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "tryStatement"


    // $ANTLR start "modifyStatement"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:912:1: modifyStatement : s= 'modify' parExpression '{' (e= expression ( ',' e= expression )* )? c= '}' ;
    public final void modifyStatement() throws RecognitionException {
        int modifyStatement_StartIndex = input.index();
        Token s=null;
        Token c=null;
        JavaParser.expression_return e = null;

        JavaParser.parExpression_return parExpression7 = null;



                JavaModifyBlockDescr d = null;
            
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 89) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:916:5: (s= 'modify' parExpression '{' (e= expression ( ',' e= expression )* )? c= '}' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:916:7: s= 'modify' parExpression '{' (e= expression ( ',' e= expression )* )? c= '}'
            {
            s=(Token)match(input,88,FOLLOW_88_in_modifyStatement3949); if (state.failed) return ;
            pushFollow(FOLLOW_parExpression_in_modifyStatement3951);
            parExpression7=parExpression();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {

                      d = new JavaModifyBlockDescr( (parExpression7!=null?input.toString(parExpression7.start,parExpression7.stop):null) );
                      d.setStart( ((CommonToken)s).getStartIndex() );
                      d.setInScopeLocalVars( getLocalDeclarations() );
                      this.addBlockDescr( d );

                  
            }
            match(input,37,FOLLOW_37_in_modifyStatement3963); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:924:9: (e= expression ( ',' e= expression )* )?
            int alt125=2;
            alt125 = dfa125.predict(input);
            switch (alt125) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:924:11: e= expression ( ',' e= expression )*
                    {
                    pushFollow(FOLLOW_expression_in_modifyStatement3971);
                    e=expression();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       d.getExpressions().add( (e!=null?input.toString(e.start,e.stop):null) ); 
                    }
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:925:12: ( ',' e= expression )*
                    loop124:
                    do {
                        int alt124=2;
                        int LA124_0 = input.LA(1);

                        if ( (LA124_0==34) ) {
                            alt124=1;
                        }


                        switch (alt124) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:925:13: ',' e= expression
                    	    {
                    	    match(input,34,FOLLOW_34_in_modifyStatement3987); if (state.failed) return ;
                    	    pushFollow(FOLLOW_expression_in_modifyStatement3991);
                    	    e=expression();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    if ( state.backtracking==0 ) {
                    	       d.getExpressions().add( (e!=null?input.toString(e.start,e.stop):null) ); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop124;
                        }
                    } while (true);


                    }
                    break;

            }

            c=(Token)match(input,38,FOLLOW_38_in_modifyStatement4015); if (state.failed) return ;
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
            if ( state.backtracking>0 ) { memoize(input, 89, modifyStatement_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "modifyStatement"


    // $ANTLR start "updateStatement"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:933:1: updateStatement : s= 'update' '(' expression c= ')' ;
    public final void updateStatement() throws RecognitionException {
        int updateStatement_StartIndex = input.index();
        Token s=null;
        Token c=null;
        JavaParser.expression_return expression8 = null;



                JavaUpdateBlockDescr d = null;
            
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 90) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:937:5: (s= 'update' '(' expression c= ')' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:937:7: s= 'update' '(' expression c= ')'
            {
            s=(Token)match(input,89,FOLLOW_89_in_updateStatement4053); if (state.failed) return ;
            match(input,65,FOLLOW_65_in_updateStatement4055); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_updateStatement4061);
            expression8=expression();

            state._fsp--;
            if (state.failed) return ;
            c=(Token)match(input,66,FOLLOW_66_in_updateStatement4071); if (state.failed) return ;
            if ( state.backtracking==0 ) {

                      d = new JavaUpdateBlockDescr( (expression8!=null?input.toString(expression8.start,expression8.stop):null) );
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
            if ( state.backtracking>0 ) { memoize(input, 90, updateStatement_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "updateStatement"


    // $ANTLR start "retractStatement"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:948:1: retractStatement : s= 'retract' '(' expression c= ')' ;
    public final void retractStatement() throws RecognitionException {
        int retractStatement_StartIndex = input.index();
        Token s=null;
        Token c=null;
        JavaParser.expression_return expression9 = null;



                JavaRetractBlockDescr d = null;
            
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 91) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:952:5: (s= 'retract' '(' expression c= ')' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:952:7: s= 'retract' '(' expression c= ')'
            {
            s=(Token)match(input,90,FOLLOW_90_in_retractStatement4113); if (state.failed) return ;
            match(input,65,FOLLOW_65_in_retractStatement4115); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_retractStatement4121);
            expression9=expression();

            state._fsp--;
            if (state.failed) return ;
            c=(Token)match(input,66,FOLLOW_66_in_retractStatement4131); if (state.failed) return ;
            if ( state.backtracking==0 ) {
              	
                      d = new JavaRetractBlockDescr( (expression9!=null?input.toString(expression9.start,expression9.stop):null) );
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
            if ( state.backtracking>0 ) { memoize(input, 91, retractStatement_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "retractStatement"


    // $ANTLR start "epStatement"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:964:1: epStatement : (s= 'exitPoints' '[' id= StringLiteral c= ']' | s= 'entryPoints' '[' id= StringLiteral c= ']' | s= 'channels' '[' id= StringLiteral c= ']' ) ;
    public final void epStatement() throws RecognitionException {
        int epStatement_StartIndex = input.index();
        Token s=null;
        Token id=null;
        Token c=null;


                JavaInterfacePointsDescr d = null;
            
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 92) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:968:9: ( (s= 'exitPoints' '[' id= StringLiteral c= ']' | s= 'entryPoints' '[' id= StringLiteral c= ']' | s= 'channels' '[' id= StringLiteral c= ']' ) )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:969:9: (s= 'exitPoints' '[' id= StringLiteral c= ']' | s= 'entryPoints' '[' id= StringLiteral c= ']' | s= 'channels' '[' id= StringLiteral c= ']' )
            {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:969:9: (s= 'exitPoints' '[' id= StringLiteral c= ']' | s= 'entryPoints' '[' id= StringLiteral c= ']' | s= 'channels' '[' id= StringLiteral c= ']' )
            int alt126=3;
            switch ( input.LA(1) ) {
            case 91:
                {
                alt126=1;
                }
                break;
            case 92:
                {
                alt126=2;
                }
                break;
            case 93:
                {
                alt126=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 126, 0, input);

                throw nvae;
            }

            switch (alt126) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:969:11: s= 'exitPoints' '[' id= StringLiteral c= ']'
                    {
                    s=(Token)match(input,91,FOLLOW_91_in_epStatement4184); if (state.failed) return ;
                    match(input,41,FOLLOW_41_in_epStatement4186); if (state.failed) return ;
                    id=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_epStatement4190); if (state.failed) return ;
                    c=(Token)match(input,42,FOLLOW_42_in_epStatement4194); if (state.failed) return ;
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
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:977:12: s= 'entryPoints' '[' id= StringLiteral c= ']'
                    {
                    s=(Token)match(input,92,FOLLOW_92_in_epStatement4220); if (state.failed) return ;
                    match(input,41,FOLLOW_41_in_epStatement4222); if (state.failed) return ;
                    id=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_epStatement4226); if (state.failed) return ;
                    c=(Token)match(input,42,FOLLOW_42_in_epStatement4230); if (state.failed) return ;
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
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:985:12: s= 'channels' '[' id= StringLiteral c= ']'
                    {
                    s=(Token)match(input,93,FOLLOW_93_in_epStatement4256); if (state.failed) return ;
                    match(input,41,FOLLOW_41_in_epStatement4258); if (state.failed) return ;
                    id=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_epStatement4262); if (state.failed) return ;
                    c=(Token)match(input,42,FOLLOW_42_in_epStatement4266); if (state.failed) return ;
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
            if ( state.backtracking>0 ) { memoize(input, 92, epStatement_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "epStatement"

    public static class formalParameter_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "formalParameter"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:996:1: formalParameter : ( variableModifier )* type variableDeclaratorId ;
    public final JavaParser.formalParameter_return formalParameter() throws RecognitionException {
        JavaParser.formalParameter_return retval = new JavaParser.formalParameter_return();
        retval.start = input.LT(1);
        int formalParameter_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 93) ) { return retval; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:997:5: ( ( variableModifier )* type variableDeclaratorId )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:997:7: ( variableModifier )* type variableDeclaratorId
            {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:997:7: ( variableModifier )*
            loop127:
            do {
                int alt127=2;
                int LA127_0 = input.LA(1);

                if ( (LA127_0==49||LA127_0==71) ) {
                    alt127=1;
                }


                switch (alt127) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: variableModifier
            	    {
            	    pushFollow(FOLLOW_variableModifier_in_formalParameter4310);
            	    variableModifier();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop127;
                }
            } while (true);

            pushFollow(FOLLOW_type_in_formalParameter4313);
            type();

            state._fsp--;
            if (state.failed) return retval;
            pushFollow(FOLLOW_variableDeclaratorId_in_formalParameter4315);
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
            if ( state.backtracking>0 ) { memoize(input, 93, formalParameter_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "formalParameter"


    // $ANTLR start "switchBlockStatementGroups"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1000:1: switchBlockStatementGroups : ( switchBlockStatementGroup )* ;
    public final void switchBlockStatementGroups() throws RecognitionException {
        int switchBlockStatementGroups_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 94) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1001:5: ( ( switchBlockStatementGroup )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1001:7: ( switchBlockStatementGroup )*
            {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1001:7: ( switchBlockStatementGroup )*
            loop128:
            do {
                int alt128=2;
                int LA128_0 = input.LA(1);

                if ( (LA128_0==72||LA128_0==94) ) {
                    alt128=1;
                }


                switch (alt128) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1001:8: switchBlockStatementGroup
            	    {
            	    pushFollow(FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups4333);
            	    switchBlockStatementGroup();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop128;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 94, switchBlockStatementGroups_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "switchBlockStatementGroups"


    // $ANTLR start "switchBlockStatementGroup"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1004:1: switchBlockStatementGroup : switchLabel ( blockStatement )* ;
    public final void switchBlockStatementGroup() throws RecognitionException {
        int switchBlockStatementGroup_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 95) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1005:5: ( switchLabel ( blockStatement )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1005:7: switchLabel ( blockStatement )*
            {
            pushFollow(FOLLOW_switchLabel_in_switchBlockStatementGroup4352);
            switchLabel();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1005:19: ( blockStatement )*
            loop129:
            do {
                int alt129=2;
                alt129 = dfa129.predict(input);
                switch (alt129) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_switchBlockStatementGroup4354);
            	    blockStatement();

            	    state._fsp--;
            	    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 95, switchBlockStatementGroup_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "switchBlockStatementGroup"


    // $ANTLR start "switchLabel"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1008:1: switchLabel : ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' );
    public final void switchLabel() throws RecognitionException {
        int switchLabel_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 96) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1009:5: ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' )
            int alt130=3;
            alt130 = dfa130.predict(input);
            switch (alt130) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1009:7: 'case' constantExpression ':'
                    {
                    match(input,94,FOLLOW_94_in_switchLabel4372); if (state.failed) return ;
                    pushFollow(FOLLOW_constantExpression_in_switchLabel4374);
                    constantExpression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,74,FOLLOW_74_in_switchLabel4376); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1010:9: 'case' enumConstantName ':'
                    {
                    match(input,94,FOLLOW_94_in_switchLabel4386); if (state.failed) return ;
                    pushFollow(FOLLOW_enumConstantName_in_switchLabel4388);
                    enumConstantName();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,74,FOLLOW_74_in_switchLabel4390); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1011:9: 'default' ':'
                    {
                    match(input,72,FOLLOW_72_in_switchLabel4400); if (state.failed) return ;
                    match(input,74,FOLLOW_74_in_switchLabel4402); if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 96, switchLabel_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "switchLabel"


    // $ANTLR start "moreStatementExpressions"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1014:1: moreStatementExpressions : ( ',' statementExpression )* ;
    public final void moreStatementExpressions() throws RecognitionException {
        int moreStatementExpressions_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 97) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1015:5: ( ( ',' statementExpression )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1015:7: ( ',' statementExpression )*
            {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1015:7: ( ',' statementExpression )*
            loop131:
            do {
                int alt131=2;
                int LA131_0 = input.LA(1);

                if ( (LA131_0==34) ) {
                    alt131=1;
                }


                switch (alt131) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1015:8: ',' statementExpression
            	    {
            	    match(input,34,FOLLOW_34_in_moreStatementExpressions4420); if (state.failed) return ;
            	    pushFollow(FOLLOW_statementExpression_in_moreStatementExpressions4422);
            	    statementExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 97, moreStatementExpressions_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "moreStatementExpressions"


    // $ANTLR start "forControl"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1018:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );
    public final void forControl() throws RecognitionException {
        VarDecl_stack.push(new VarDecl_scope());

        int forControl_StartIndex = input.index();

                    increaseLevel();
                    ((VarDecl_scope)VarDecl_stack.peek()).descr = new JavaLocalDeclarationDescr();
                
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 98) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1029:5: ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? )
            int alt135=2;
            alt135 = dfa135.predict(input);
            switch (alt135) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1029:7: forVarControl
                    {
                    pushFollow(FOLLOW_forVarControl_in_forControl4480);
                    forVarControl();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1030:7: ( forInit )? ';' ( expression )? ';' ( forUpdate )?
                    {
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1030:7: ( forInit )?
                    int alt132=2;
                    alt132 = dfa132.predict(input);
                    switch (alt132) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: forInit
                            {
                            pushFollow(FOLLOW_forInit_in_forControl4488);
                            forInit();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    match(input,25,FOLLOW_25_in_forControl4491); if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1030:20: ( expression )?
                    int alt133=2;
                    alt133 = dfa133.predict(input);
                    switch (alt133) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: expression
                            {
                            pushFollow(FOLLOW_expression_in_forControl4493);
                            expression();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    match(input,25,FOLLOW_25_in_forControl4496); if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1030:36: ( forUpdate )?
                    int alt134=2;
                    alt134 = dfa134.predict(input);
                    switch (alt134) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: forUpdate
                            {
                            pushFollow(FOLLOW_forUpdate_in_forControl4498);
                            forUpdate();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }
            if ( state.backtracking==0 ) {

                          addLocalDeclaration( ((VarDecl_scope)VarDecl_stack.peek()).descr );
                          decreaseLevel();
                      
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 98, forControl_StartIndex); }
            VarDecl_stack.pop();

        }
        return ;
    }
    // $ANTLR end "forControl"


    // $ANTLR start "forInit"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1033:1: forInit : ( ( variableModifier )* type variableDeclarators | expressionList );
    public final void forInit() throws RecognitionException {
        int forInit_StartIndex = input.index();
        JavaParser.variableModifier_return variableModifier10 = null;

        JavaParser.type_return type11 = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 99) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1034:5: ( ( variableModifier )* type variableDeclarators | expressionList )
            int alt137=2;
            alt137 = dfa137.predict(input);
            switch (alt137) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1034:7: ( variableModifier )* type variableDeclarators
                    {
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1034:7: ( variableModifier )*
                    loop136:
                    do {
                        int alt136=2;
                        int LA136_0 = input.LA(1);

                        if ( (LA136_0==49||LA136_0==71) ) {
                            alt136=1;
                        }


                        switch (alt136) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1034:9: variableModifier
                    	    {
                    	    pushFollow(FOLLOW_variableModifier_in_forInit4518);
                    	    variableModifier10=variableModifier();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    if ( state.backtracking==0 ) {

                    	                      ((VarDecl_scope)VarDecl_stack.peek()).descr.updateStart( ((CommonToken)(variableModifier10!=null?((Token)variableModifier10.start):null)).getStartIndex() - 1 );
                    	                      ((VarDecl_scope)VarDecl_stack.peek()).descr.addModifier( (variableModifier10!=null?input.toString(variableModifier10.start,variableModifier10.stop):null) );
                    	                  
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop136;
                        }
                    } while (true);

                    pushFollow(FOLLOW_type_in_forInit4553);
                    type11=type();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {

                                  ((VarDecl_scope)VarDecl_stack.peek()).descr.updateStart( ((CommonToken)(type11!=null?((Token)type11.start):null)).getStartIndex() - 1 );
                                  ((VarDecl_scope)VarDecl_stack.peek()).descr.setType( (type11!=null?input.toString(type11.start,type11.stop):null) );
                                  ((VarDecl_scope)VarDecl_stack.peek()).descr.setEnd( ((CommonToken)(type11!=null?((Token)type11.stop):null)).getStopIndex() );
                              
                    }
                    pushFollow(FOLLOW_variableDeclarators_in_forInit4573);
                    variableDeclarators();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1047:7: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_forInit4581);
                    expressionList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 99, forInit_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "forInit"


    // $ANTLR start "forVarControl"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1050:1: forVarControl : ( variableModifier )* type id= Identifier ':' expression ;
    public final void forVarControl() throws RecognitionException {
        int forVarControl_StartIndex = input.index();
        Token id=null;
        JavaParser.variableModifier_return variableModifier12 = null;

        JavaParser.type_return type13 = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 100) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1051:5: ( ( variableModifier )* type id= Identifier ':' expression )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1051:7: ( variableModifier )* type id= Identifier ':' expression
            {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1051:7: ( variableModifier )*
            loop138:
            do {
                int alt138=2;
                int LA138_0 = input.LA(1);

                if ( (LA138_0==49||LA138_0==71) ) {
                    alt138=1;
                }


                switch (alt138) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1051:9: variableModifier
            	    {
            	    pushFollow(FOLLOW_variableModifier_in_forVarControl4600);
            	    variableModifier12=variableModifier();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    if ( state.backtracking==0 ) {

            	                      ((VarDecl_scope)VarDecl_stack.peek()).descr.updateStart( ((CommonToken)(variableModifier12!=null?((Token)variableModifier12.start):null)).getStartIndex() - 1 );
            	                      ((VarDecl_scope)VarDecl_stack.peek()).descr.addModifier( (variableModifier12!=null?input.toString(variableModifier12.start,variableModifier12.stop):null) );
            	                  
            	    }

            	    }
            	    break;

            	default :
            	    break loop138;
                }
            } while (true);

            pushFollow(FOLLOW_type_in_forVarControl4635);
            type13=type();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {

                          ((VarDecl_scope)VarDecl_stack.peek()).descr.updateStart( ((CommonToken)(type13!=null?((Token)type13.start):null)).getStartIndex() - 1 );
                          ((VarDecl_scope)VarDecl_stack.peek()).descr.setType( (type13!=null?input.toString(type13.start,type13.stop):null) );
                          ((VarDecl_scope)VarDecl_stack.peek()).descr.setEnd( ((CommonToken)(type13!=null?((Token)type13.stop):null)).getStopIndex() );
                      
            }
            id=(Token)match(input,Identifier,FOLLOW_Identifier_in_forVarControl4657); if (state.failed) return ;
            if ( state.backtracking==0 ) {

                          JavaLocalDeclarationDescr.IdentifierDescr ident = new JavaLocalDeclarationDescr.IdentifierDescr();
                          ident.setIdentifier( (id!=null?id.getText():null) );
                          ident.setStart( ((CommonToken)id).getStartIndex() - 1 );
                          ident.setEnd( ((CommonToken)id).getStopIndex() );
                          ((VarDecl_scope)VarDecl_stack.peek()).descr.addIdentifier( ident );
                      
            }
            match(input,74,FOLLOW_74_in_forVarControl4678); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_forVarControl4680);
            expression();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 100, forVarControl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "forVarControl"


    // $ANTLR start "forUpdate"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1074:1: forUpdate : expressionList ;
    public final void forUpdate() throws RecognitionException {
        int forUpdate_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 101) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1075:5: ( expressionList )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1075:7: expressionList
            {
            pushFollow(FOLLOW_expressionList_in_forUpdate4697);
            expressionList();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 101, forUpdate_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "forUpdate"

    public static class parExpression_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "parExpression"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1080:1: parExpression : '(' expression ')' ;
    public final JavaParser.parExpression_return parExpression() throws RecognitionException {
        JavaParser.parExpression_return retval = new JavaParser.parExpression_return();
        retval.start = input.LT(1);
        int parExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 102) ) { return retval; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1081:5: ( '(' expression ')' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1081:7: '(' expression ')'
            {
            match(input,65,FOLLOW_65_in_parExpression4716); if (state.failed) return retval;
            pushFollow(FOLLOW_expression_in_parExpression4718);
            expression();

            state._fsp--;
            if (state.failed) return retval;
            match(input,66,FOLLOW_66_in_parExpression4720); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 102, parExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "parExpression"


    // $ANTLR start "expressionList"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1084:1: expressionList : expression ( ',' expression )* ;
    public final void expressionList() throws RecognitionException {
        int expressionList_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 103) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1085:5: ( expression ( ',' expression )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1085:9: expression ( ',' expression )*
            {
            pushFollow(FOLLOW_expression_in_expressionList4739);
            expression();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1085:20: ( ',' expression )*
            loop139:
            do {
                int alt139=2;
                int LA139_0 = input.LA(1);

                if ( (LA139_0==34) ) {
                    alt139=1;
                }


                switch (alt139) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1085:21: ',' expression
            	    {
            	    match(input,34,FOLLOW_34_in_expressionList4742); if (state.failed) return ;
            	    pushFollow(FOLLOW_expression_in_expressionList4744);
            	    expression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop139;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 103, expressionList_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "expressionList"


    // $ANTLR start "statementExpression"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1088:1: statementExpression : expression ;
    public final void statementExpression() throws RecognitionException {
        int statementExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 104) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1089:5: ( expression )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1089:7: expression
            {
            pushFollow(FOLLOW_expression_in_statementExpression4763);
            expression();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 104, statementExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "statementExpression"


    // $ANTLR start "constantExpression"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1092:1: constantExpression : expression ;
    public final void constantExpression() throws RecognitionException {
        int constantExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 105) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1093:5: ( expression )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1093:7: expression
            {
            pushFollow(FOLLOW_expression_in_constantExpression4780);
            expression();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 105, constantExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "constantExpression"

    public static class expression_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "expression"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1096:1: expression : conditionalExpression ( assignmentOperator expression )? ;
    public final JavaParser.expression_return expression() throws RecognitionException {
        JavaParser.expression_return retval = new JavaParser.expression_return();
        retval.start = input.LT(1);
        int expression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 106) ) { return retval; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1097:5: ( conditionalExpression ( assignmentOperator expression )? )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1097:7: conditionalExpression ( assignmentOperator expression )?
            {
            pushFollow(FOLLOW_conditionalExpression_in_expression4797);
            conditionalExpression();

            state._fsp--;
            if (state.failed) return retval;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1097:29: ( assignmentOperator expression )?
            int alt140=2;
            alt140 = dfa140.predict(input);
            switch (alt140) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1097:30: assignmentOperator expression
                    {
                    pushFollow(FOLLOW_assignmentOperator_in_expression4800);
                    assignmentOperator();

                    state._fsp--;
                    if (state.failed) return retval;
                    pushFollow(FOLLOW_expression_in_expression4802);
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
            if ( state.backtracking>0 ) { memoize(input, 106, expression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "expression"


    // $ANTLR start "assignmentOperator"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1100:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '=' | '>' '>' '>' '=' );
    public final void assignmentOperator() throws RecognitionException {
        int assignmentOperator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 107) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1101:5: ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '=' | '>' '>' '>' '=' )
            int alt141=12;
            alt141 = dfa141.predict(input);
            switch (alt141) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1101:7: '='
                    {
                    match(input,44,FOLLOW_44_in_assignmentOperator4821); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1102:9: '+='
                    {
                    match(input,95,FOLLOW_95_in_assignmentOperator4831); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1103:9: '-='
                    {
                    match(input,96,FOLLOW_96_in_assignmentOperator4841); if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1104:9: '*='
                    {
                    match(input,97,FOLLOW_97_in_assignmentOperator4851); if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1105:9: '/='
                    {
                    match(input,98,FOLLOW_98_in_assignmentOperator4861); if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1106:9: '&='
                    {
                    match(input,99,FOLLOW_99_in_assignmentOperator4871); if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1107:9: '|='
                    {
                    match(input,100,FOLLOW_100_in_assignmentOperator4881); if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1108:9: '^='
                    {
                    match(input,101,FOLLOW_101_in_assignmentOperator4891); if (state.failed) return ;

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1109:9: '%='
                    {
                    match(input,102,FOLLOW_102_in_assignmentOperator4901); if (state.failed) return ;

                    }
                    break;
                case 10 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1110:9: '<' '<' '='
                    {
                    match(input,33,FOLLOW_33_in_assignmentOperator4911); if (state.failed) return ;
                    match(input,33,FOLLOW_33_in_assignmentOperator4913); if (state.failed) return ;
                    match(input,44,FOLLOW_44_in_assignmentOperator4915); if (state.failed) return ;

                    }
                    break;
                case 11 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1111:9: '>' '>' '='
                    {
                    match(input,35,FOLLOW_35_in_assignmentOperator4925); if (state.failed) return ;
                    match(input,35,FOLLOW_35_in_assignmentOperator4927); if (state.failed) return ;
                    match(input,44,FOLLOW_44_in_assignmentOperator4929); if (state.failed) return ;

                    }
                    break;
                case 12 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1112:9: '>' '>' '>' '='
                    {
                    match(input,35,FOLLOW_35_in_assignmentOperator4939); if (state.failed) return ;
                    match(input,35,FOLLOW_35_in_assignmentOperator4941); if (state.failed) return ;
                    match(input,35,FOLLOW_35_in_assignmentOperator4943); if (state.failed) return ;
                    match(input,44,FOLLOW_44_in_assignmentOperator4945); if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 107, assignmentOperator_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "assignmentOperator"


    // $ANTLR start "conditionalExpression"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1115:1: conditionalExpression : conditionalOrExpression ( '?' expression ':' expression )? ;
    public final void conditionalExpression() throws RecognitionException {
        int conditionalExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 108) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1116:5: ( conditionalOrExpression ( '?' expression ':' expression )? )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1116:9: conditionalOrExpression ( '?' expression ':' expression )?
            {
            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression4964);
            conditionalOrExpression();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1116:33: ( '?' expression ':' expression )?
            int alt142=2;
            alt142 = dfa142.predict(input);
            switch (alt142) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1116:35: '?' expression ':' expression
                    {
                    match(input,63,FOLLOW_63_in_conditionalExpression4968); if (state.failed) return ;
                    pushFollow(FOLLOW_expression_in_conditionalExpression4970);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,74,FOLLOW_74_in_conditionalExpression4972); if (state.failed) return ;
                    pushFollow(FOLLOW_expression_in_conditionalExpression4974);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 108, conditionalExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "conditionalExpression"


    // $ANTLR start "conditionalOrExpression"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1119:1: conditionalOrExpression : conditionalAndExpression ( '||' conditionalAndExpression )* ;
    public final void conditionalOrExpression() throws RecognitionException {
        int conditionalOrExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 109) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1120:5: ( conditionalAndExpression ( '||' conditionalAndExpression )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1120:9: conditionalAndExpression ( '||' conditionalAndExpression )*
            {
            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression4996);
            conditionalAndExpression();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1120:34: ( '||' conditionalAndExpression )*
            loop143:
            do {
                int alt143=2;
                alt143 = dfa143.predict(input);
                switch (alt143) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1120:36: '||' conditionalAndExpression
            	    {
            	    match(input,103,FOLLOW_103_in_conditionalOrExpression5000); if (state.failed) return ;
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression5002);
            	    conditionalAndExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 109, conditionalOrExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "conditionalOrExpression"


    // $ANTLR start "conditionalAndExpression"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1123:1: conditionalAndExpression : inclusiveOrExpression ( '&&' inclusiveOrExpression )* ;
    public final void conditionalAndExpression() throws RecognitionException {
        int conditionalAndExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 110) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1124:5: ( inclusiveOrExpression ( '&&' inclusiveOrExpression )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1124:9: inclusiveOrExpression ( '&&' inclusiveOrExpression )*
            {
            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5024);
            inclusiveOrExpression();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1124:31: ( '&&' inclusiveOrExpression )*
            loop144:
            do {
                int alt144=2;
                alt144 = dfa144.predict(input);
                switch (alt144) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1124:33: '&&' inclusiveOrExpression
            	    {
            	    match(input,104,FOLLOW_104_in_conditionalAndExpression5028); if (state.failed) return ;
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5030);
            	    inclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 110, conditionalAndExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "conditionalAndExpression"


    // $ANTLR start "inclusiveOrExpression"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1127:1: inclusiveOrExpression : exclusiveOrExpression ( '|' exclusiveOrExpression )* ;
    public final void inclusiveOrExpression() throws RecognitionException {
        int inclusiveOrExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 111) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1128:5: ( exclusiveOrExpression ( '|' exclusiveOrExpression )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1128:9: exclusiveOrExpression ( '|' exclusiveOrExpression )*
            {
            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5052);
            exclusiveOrExpression();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1128:31: ( '|' exclusiveOrExpression )*
            loop145:
            do {
                int alt145=2;
                alt145 = dfa145.predict(input);
                switch (alt145) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1128:33: '|' exclusiveOrExpression
            	    {
            	    match(input,105,FOLLOW_105_in_inclusiveOrExpression5056); if (state.failed) return ;
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5058);
            	    exclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 111, inclusiveOrExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "inclusiveOrExpression"


    // $ANTLR start "exclusiveOrExpression"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1131:1: exclusiveOrExpression : andExpression ( '^' andExpression )* ;
    public final void exclusiveOrExpression() throws RecognitionException {
        int exclusiveOrExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 112) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1132:5: ( andExpression ( '^' andExpression )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1132:9: andExpression ( '^' andExpression )*
            {
            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression5080);
            andExpression();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1132:23: ( '^' andExpression )*
            loop146:
            do {
                int alt146=2;
                alt146 = dfa146.predict(input);
                switch (alt146) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1132:25: '^' andExpression
            	    {
            	    match(input,106,FOLLOW_106_in_exclusiveOrExpression5084); if (state.failed) return ;
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression5086);
            	    andExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop146;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 112, exclusiveOrExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "exclusiveOrExpression"


    // $ANTLR start "andExpression"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1135:1: andExpression : equalityExpression ( '&' equalityExpression )* ;
    public final void andExpression() throws RecognitionException {
        int andExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 113) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1136:5: ( equalityExpression ( '&' equalityExpression )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1136:9: equalityExpression ( '&' equalityExpression )*
            {
            pushFollow(FOLLOW_equalityExpression_in_andExpression5108);
            equalityExpression();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1136:28: ( '&' equalityExpression )*
            loop147:
            do {
                int alt147=2;
                alt147 = dfa147.predict(input);
                switch (alt147) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1136:30: '&' equalityExpression
            	    {
            	    match(input,36,FOLLOW_36_in_andExpression5112); if (state.failed) return ;
            	    pushFollow(FOLLOW_equalityExpression_in_andExpression5114);
            	    equalityExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop147;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 113, andExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "andExpression"


    // $ANTLR start "equalityExpression"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1139:1: equalityExpression : instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* ;
    public final void equalityExpression() throws RecognitionException {
        int equalityExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 114) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1140:5: ( instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1140:9: instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )*
            {
            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression5136);
            instanceOfExpression();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1140:30: ( ( '==' | '!=' ) instanceOfExpression )*
            loop148:
            do {
                int alt148=2;
                alt148 = dfa148.predict(input);
                switch (alt148) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1140:32: ( '==' | '!=' ) instanceOfExpression
            	    {
            	    if ( (input.LA(1)>=107 && input.LA(1)<=108) ) {
            	        input.consume();
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression5148);
            	    instanceOfExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop148;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 114, equalityExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "equalityExpression"


    // $ANTLR start "instanceOfExpression"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1143:1: instanceOfExpression : relationalExpression ( 'instanceof' type )? ;
    public final void instanceOfExpression() throws RecognitionException {
        int instanceOfExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 115) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1144:5: ( relationalExpression ( 'instanceof' type )? )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1144:9: relationalExpression ( 'instanceof' type )?
            {
            pushFollow(FOLLOW_relationalExpression_in_instanceOfExpression5170);
            relationalExpression();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1144:30: ( 'instanceof' type )?
            int alt149=2;
            alt149 = dfa149.predict(input);
            switch (alt149) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1144:31: 'instanceof' type
                    {
                    match(input,109,FOLLOW_109_in_instanceOfExpression5173); if (state.failed) return ;
                    pushFollow(FOLLOW_type_in_instanceOfExpression5175);
                    type();

                    state._fsp--;
                    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 115, instanceOfExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "instanceOfExpression"


    // $ANTLR start "relationalExpression"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1147:1: relationalExpression : shiftExpression ( relationalOp shiftExpression )* ;
    public final void relationalExpression() throws RecognitionException {
        int relationalExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 116) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1148:5: ( shiftExpression ( relationalOp shiftExpression )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1148:9: shiftExpression ( relationalOp shiftExpression )*
            {
            pushFollow(FOLLOW_shiftExpression_in_relationalExpression5196);
            shiftExpression();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1148:25: ( relationalOp shiftExpression )*
            loop150:
            do {
                int alt150=2;
                alt150 = dfa150.predict(input);
                switch (alt150) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1148:27: relationalOp shiftExpression
            	    {
            	    pushFollow(FOLLOW_relationalOp_in_relationalExpression5200);
            	    relationalOp();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    pushFollow(FOLLOW_shiftExpression_in_relationalExpression5202);
            	    shiftExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop150;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 116, relationalExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "relationalExpression"


    // $ANTLR start "relationalOp"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1151:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' ) ;
    public final void relationalOp() throws RecognitionException {
        int relationalOp_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 117) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1152:5: ( ( '<' '=' | '>' '=' | '<' | '>' ) )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1152:7: ( '<' '=' | '>' '=' | '<' | '>' )
            {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1152:7: ( '<' '=' | '>' '=' | '<' | '>' )
            int alt151=4;
            alt151 = dfa151.predict(input);
            switch (alt151) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1152:8: '<' '='
                    {
                    match(input,33,FOLLOW_33_in_relationalOp5223); if (state.failed) return ;
                    match(input,44,FOLLOW_44_in_relationalOp5225); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1152:18: '>' '='
                    {
                    match(input,35,FOLLOW_35_in_relationalOp5229); if (state.failed) return ;
                    match(input,44,FOLLOW_44_in_relationalOp5231); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1152:28: '<'
                    {
                    match(input,33,FOLLOW_33_in_relationalOp5235); if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1152:34: '>'
                    {
                    match(input,35,FOLLOW_35_in_relationalOp5239); if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 117, relationalOp_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "relationalOp"


    // $ANTLR start "shiftExpression"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1155:1: shiftExpression : additiveExpression ( shiftOp additiveExpression )* ;
    public final void shiftExpression() throws RecognitionException {
        int shiftExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 118) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1156:5: ( additiveExpression ( shiftOp additiveExpression )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1156:9: additiveExpression ( shiftOp additiveExpression )*
            {
            pushFollow(FOLLOW_additiveExpression_in_shiftExpression5259);
            additiveExpression();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1156:28: ( shiftOp additiveExpression )*
            loop152:
            do {
                int alt152=2;
                alt152 = dfa152.predict(input);
                switch (alt152) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1156:30: shiftOp additiveExpression
            	    {
            	    pushFollow(FOLLOW_shiftOp_in_shiftExpression5263);
            	    shiftOp();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression5265);
            	    additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop152;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 118, shiftExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "shiftExpression"


    // $ANTLR start "shiftOp"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1160:1: shiftOp : ( '<' '<' | '>' '>' '>' | '>' '>' ) ;
    public final void shiftOp() throws RecognitionException {
        int shiftOp_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 119) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1161:5: ( ( '<' '<' | '>' '>' '>' | '>' '>' ) )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1161:7: ( '<' '<' | '>' '>' '>' | '>' '>' )
            {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1161:7: ( '<' '<' | '>' '>' '>' | '>' '>' )
            int alt153=3;
            int LA153_0 = input.LA(1);

            if ( (LA153_0==33) ) {
                alt153=1;
            }
            else if ( (LA153_0==35) ) {
                int LA153_2 = input.LA(2);

                if ( (LA153_2==35) ) {
                    int LA153_3 = input.LA(3);

                    if ( (synpred226_Java()) ) {
                        alt153=2;
                    }
                    else if ( (true) ) {
                        alt153=3;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 153, 3, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 153, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 153, 0, input);

                throw nvae;
            }
            switch (alt153) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1161:8: '<' '<'
                    {
                    match(input,33,FOLLOW_33_in_shiftOp5295); if (state.failed) return ;
                    match(input,33,FOLLOW_33_in_shiftOp5297); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1161:18: '>' '>' '>'
                    {
                    match(input,35,FOLLOW_35_in_shiftOp5301); if (state.failed) return ;
                    match(input,35,FOLLOW_35_in_shiftOp5303); if (state.failed) return ;
                    match(input,35,FOLLOW_35_in_shiftOp5305); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1161:32: '>' '>'
                    {
                    match(input,35,FOLLOW_35_in_shiftOp5309); if (state.failed) return ;
                    match(input,35,FOLLOW_35_in_shiftOp5311); if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 119, shiftOp_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "shiftOp"


    // $ANTLR start "additiveExpression"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1165:1: additiveExpression : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* ;
    public final void additiveExpression() throws RecognitionException {
        int additiveExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 120) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1166:5: ( multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1166:9: multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression5332);
            multiplicativeExpression();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1166:34: ( ( '+' | '-' ) multiplicativeExpression )*
            loop154:
            do {
                int alt154=2;
                alt154 = dfa154.predict(input);
                switch (alt154) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1166:36: ( '+' | '-' ) multiplicativeExpression
            	    {
            	    if ( (input.LA(1)>=110 && input.LA(1)<=111) ) {
            	        input.consume();
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression5344);
            	    multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop154;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 120, additiveExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "additiveExpression"


    // $ANTLR start "multiplicativeExpression"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1169:1: multiplicativeExpression : unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* ;
    public final void multiplicativeExpression() throws RecognitionException {
        int multiplicativeExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 121) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1170:5: ( unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1170:9: unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
            {
            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression5366);
            unaryExpression();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1170:25: ( ( '*' | '/' | '%' ) unaryExpression )*
            loop155:
            do {
                int alt155=2;
                alt155 = dfa155.predict(input);
                switch (alt155) {
            	case 1 :
            	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1170:27: ( '*' | '/' | '%' ) unaryExpression
            	    {
            	    if ( input.LA(1)==29||(input.LA(1)>=112 && input.LA(1)<=113) ) {
            	        input.consume();
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression5384);
            	    unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop155;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 121, multiplicativeExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "multiplicativeExpression"


    // $ANTLR start "unaryExpression"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1173:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' primary | '--' primary | unaryExpressionNotPlusMinus );
    public final void unaryExpression() throws RecognitionException {
        int unaryExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 122) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1174:5: ( '+' unaryExpression | '-' unaryExpression | '++' primary | '--' primary | unaryExpressionNotPlusMinus )
            int alt156=5;
            alt156 = dfa156.predict(input);
            switch (alt156) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1174:9: '+' unaryExpression
                    {
                    match(input,110,FOLLOW_110_in_unaryExpression5406); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression5408);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1175:7: '-' unaryExpression
                    {
                    match(input,111,FOLLOW_111_in_unaryExpression5416); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression5418);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1176:9: '++' primary
                    {
                    match(input,114,FOLLOW_114_in_unaryExpression5428); if (state.failed) return ;
                    pushFollow(FOLLOW_primary_in_unaryExpression5430);
                    primary();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1177:9: '--' primary
                    {
                    match(input,115,FOLLOW_115_in_unaryExpression5440); if (state.failed) return ;
                    pushFollow(FOLLOW_primary_in_unaryExpression5442);
                    primary();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1178:9: unaryExpressionNotPlusMinus
                    {
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression5452);
                    unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 122, unaryExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "unaryExpression"


    // $ANTLR start "unaryExpressionNotPlusMinus"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1181:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );
    public final void unaryExpressionNotPlusMinus() throws RecognitionException {
        int unaryExpressionNotPlusMinus_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 123) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1182:5: ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? )
            int alt159=4;
            alt159 = dfa159.predict(input);
            switch (alt159) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1182:9: '~' unaryExpression
                    {
                    match(input,116,FOLLOW_116_in_unaryExpressionNotPlusMinus5471); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5473);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1183:8: '!' unaryExpression
                    {
                    match(input,117,FOLLOW_117_in_unaryExpressionNotPlusMinus5482); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5484);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1184:9: castExpression
                    {
                    pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus5494);
                    castExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1185:9: primary ( selector )* ( '++' | '--' )?
                    {
                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus5504);
                    primary();

                    state._fsp--;
                    if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1185:17: ( selector )*
                    loop157:
                    do {
                        int alt157=2;
                        alt157 = dfa157.predict(input);
                        switch (alt157) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: selector
                    	    {
                    	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus5506);
                    	    selector();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop157;
                        }
                    } while (true);

                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1185:27: ( '++' | '--' )?
                    int alt158=2;
                    alt158 = dfa158.predict(input);
                    switch (alt158) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:
                            {
                            if ( (input.LA(1)>=114 && input.LA(1)<=115) ) {
                                input.consume();
                                state.errorRecovery=false;state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return ;}
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
            if ( state.backtracking>0 ) { memoize(input, 123, unaryExpressionNotPlusMinus_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "unaryExpressionNotPlusMinus"


    // $ANTLR start "castExpression"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1188:1: castExpression : ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus );
    public final void castExpression() throws RecognitionException {
        int castExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 124) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1189:5: ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus )
            int alt161=2;
            alt161 = dfa161.predict(input);
            switch (alt161) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1189:8: '(' primitiveType ')' unaryExpression
                    {
                    match(input,65,FOLLOW_65_in_castExpression5532); if (state.failed) return ;
                    pushFollow(FOLLOW_primitiveType_in_castExpression5534);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,66,FOLLOW_66_in_castExpression5536); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_castExpression5538);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1190:8: '(' ( type | expression ) ')' unaryExpressionNotPlusMinus
                    {
                    match(input,65,FOLLOW_65_in_castExpression5547); if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1190:12: ( type | expression )
                    int alt160=2;
                    alt160 = dfa160.predict(input);
                    switch (alt160) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:1190:13: type
                            {
                            pushFollow(FOLLOW_type_in_castExpression5550);
                            type();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:1190:20: expression
                            {
                            pushFollow(FOLLOW_expression_in_castExpression5554);
                            expression();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    match(input,66,FOLLOW_66_in_castExpression5557); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression5559);
                    unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 124, castExpression_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "castExpression"


    // $ANTLR start "primary"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1193:1: primary : ( parExpression | nonWildcardTypeArguments ( explicitGenericInvocationSuffix | 'this' arguments ) | 'this' ( '.' Identifier )* ( identifierSuffix )? | 'super' superSuffix | epStatement ( '.' Identifier )* ( identifierSuffix )? | literal | 'new' creator | i= Identifier ( '.' Identifier )* ( identifierSuffix )? | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );
    public final void primary() throws RecognitionException {
        int primary_StartIndex = input.index();
        Token i=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 125) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1194:5: ( parExpression | nonWildcardTypeArguments ( explicitGenericInvocationSuffix | 'this' arguments ) | 'this' ( '.' Identifier )* ( identifierSuffix )? | 'super' superSuffix | epStatement ( '.' Identifier )* ( identifierSuffix )? | literal | 'new' creator | i= Identifier ( '.' Identifier )* ( identifierSuffix )? | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' )
            int alt170=10;
            alt170 = dfa170.predict(input);
            switch (alt170) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1194:7: parExpression
                    {
                    pushFollow(FOLLOW_parExpression_in_primary5576);
                    parExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1195:9: nonWildcardTypeArguments ( explicitGenericInvocationSuffix | 'this' arguments )
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_primary5586);
                    nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1196:9: ( explicitGenericInvocationSuffix | 'this' arguments )
                    int alt162=2;
                    int LA162_0 = input.LA(1);

                    if ( (LA162_0==Identifier||LA162_0==64) ) {
                        alt162=1;
                    }
                    else if ( (LA162_0==118) ) {
                        alt162=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 162, 0, input);

                        throw nvae;
                    }
                    switch (alt162) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:1196:10: explicitGenericInvocationSuffix
                            {
                            pushFollow(FOLLOW_explicitGenericInvocationSuffix_in_primary5597);
                            explicitGenericInvocationSuffix();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:1196:44: 'this' arguments
                            {
                            match(input,118,FOLLOW_118_in_primary5601); if (state.failed) return ;
                            pushFollow(FOLLOW_arguments_in_primary5603);
                            arguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1197:9: 'this' ( '.' Identifier )* ( identifierSuffix )?
                    {
                    match(input,118,FOLLOW_118_in_primary5614); if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1197:16: ( '.' Identifier )*
                    loop163:
                    do {
                        int alt163=2;
                        alt163 = dfa163.predict(input);
                        switch (alt163) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1197:17: '.' Identifier
                    	    {
                    	    match(input,28,FOLLOW_28_in_primary5617); if (state.failed) return ;
                    	    match(input,Identifier,FOLLOW_Identifier_in_primary5619); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop163;
                        }
                    } while (true);

                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1197:34: ( identifierSuffix )?
                    int alt164=2;
                    alt164 = dfa164.predict(input);
                    switch (alt164) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:1197:35: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary5624);
                            identifierSuffix();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1198:9: 'super' superSuffix
                    {
                    match(input,64,FOLLOW_64_in_primary5636); if (state.failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_primary5638);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1199:9: epStatement ( '.' Identifier )* ( identifierSuffix )?
                    {
                    pushFollow(FOLLOW_epStatement_in_primary5648);
                    epStatement();

                    state._fsp--;
                    if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1199:21: ( '.' Identifier )*
                    loop165:
                    do {
                        int alt165=2;
                        alt165 = dfa165.predict(input);
                        switch (alt165) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1199:22: '.' Identifier
                    	    {
                    	    match(input,28,FOLLOW_28_in_primary5651); if (state.failed) return ;
                    	    match(input,Identifier,FOLLOW_Identifier_in_primary5653); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop165;
                        }
                    } while (true);

                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1199:39: ( identifierSuffix )?
                    int alt166=2;
                    alt166 = dfa166.predict(input);
                    switch (alt166) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:1199:40: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary5658);
                            identifierSuffix();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1200:9: literal
                    {
                    pushFollow(FOLLOW_literal_in_primary5670);
                    literal();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1201:9: 'new' creator
                    {
                    match(input,119,FOLLOW_119_in_primary5680); if (state.failed) return ;
                    pushFollow(FOLLOW_creator_in_primary5682);
                    creator();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1202:9: i= Identifier ( '.' Identifier )* ( identifierSuffix )?
                    {
                    i=(Token)match(input,Identifier,FOLLOW_Identifier_in_primary5694); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       if( ! "(".equals( input.LT(1) == null ? "" : input.LT(1).getText() ) ) identifiers.add( (i!=null?i.getText():null) );  
                    }
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1202:126: ( '.' Identifier )*
                    loop167:
                    do {
                        int alt167=2;
                        alt167 = dfa167.predict(input);
                        switch (alt167) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1202:127: '.' Identifier
                    	    {
                    	    match(input,28,FOLLOW_28_in_primary5699); if (state.failed) return ;
                    	    match(input,Identifier,FOLLOW_Identifier_in_primary5701); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop167;
                        }
                    } while (true);

                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1202:144: ( identifierSuffix )?
                    int alt168=2;
                    alt168 = dfa168.predict(input);
                    switch (alt168) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:1202:145: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary5706);
                            identifierSuffix();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1203:9: primitiveType ( '[' ']' )* '.' 'class'
                    {
                    pushFollow(FOLLOW_primitiveType_in_primary5718);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1203:23: ( '[' ']' )*
                    loop169:
                    do {
                        int alt169=2;
                        int LA169_0 = input.LA(1);

                        if ( (LA169_0==41) ) {
                            alt169=1;
                        }


                        switch (alt169) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1203:24: '[' ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_primary5721); if (state.failed) return ;
                    	    match(input,42,FOLLOW_42_in_primary5723); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop169;
                        }
                    } while (true);

                    match(input,28,FOLLOW_28_in_primary5727); if (state.failed) return ;
                    match(input,30,FOLLOW_30_in_primary5729); if (state.failed) return ;

                    }
                    break;
                case 10 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1204:9: 'void' '.' 'class'
                    {
                    match(input,40,FOLLOW_40_in_primary5739); if (state.failed) return ;
                    match(input,28,FOLLOW_28_in_primary5741); if (state.failed) return ;
                    match(input,30,FOLLOW_30_in_primary5743); if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 125, primary_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "primary"


    // $ANTLR start "identifierSuffix"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1207:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' ( nonWildcardTypeArguments )? innerCreator );
    public final void identifierSuffix() throws RecognitionException {
        int identifierSuffix_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 126) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1208:5: ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' ( nonWildcardTypeArguments )? innerCreator )
            int alt174=8;
            alt174 = dfa174.predict(input);
            switch (alt174) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1208:7: ( '[' ']' )+ '.' 'class'
                    {
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1208:7: ( '[' ']' )+
                    int cnt171=0;
                    loop171:
                    do {
                        int alt171=2;
                        int LA171_0 = input.LA(1);

                        if ( (LA171_0==41) ) {
                            alt171=1;
                        }


                        switch (alt171) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1208:8: '[' ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_identifierSuffix5761); if (state.failed) return ;
                    	    match(input,42,FOLLOW_42_in_identifierSuffix5763); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt171 >= 1 ) break loop171;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(171, input);
                                throw eee;
                        }
                        cnt171++;
                    } while (true);

                    match(input,28,FOLLOW_28_in_identifierSuffix5767); if (state.failed) return ;
                    match(input,30,FOLLOW_30_in_identifierSuffix5769); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1209:7: ( '[' expression ']' )+
                    {
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1209:7: ( '[' expression ']' )+
                    int cnt172=0;
                    loop172:
                    do {
                        int alt172=2;
                        alt172 = dfa172.predict(input);
                        switch (alt172) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1209:8: '[' expression ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_identifierSuffix5778); if (state.failed) return ;
                    	    pushFollow(FOLLOW_expression_in_identifierSuffix5780);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    match(input,42,FOLLOW_42_in_identifierSuffix5782); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt172 >= 1 ) break loop172;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(172, input);
                                throw eee;
                        }
                        cnt172++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1210:9: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_identifierSuffix5795);
                    arguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1211:9: '.' 'class'
                    {
                    match(input,28,FOLLOW_28_in_identifierSuffix5805); if (state.failed) return ;
                    match(input,30,FOLLOW_30_in_identifierSuffix5807); if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1212:9: '.' explicitGenericInvocation
                    {
                    match(input,28,FOLLOW_28_in_identifierSuffix5817); if (state.failed) return ;
                    pushFollow(FOLLOW_explicitGenericInvocation_in_identifierSuffix5819);
                    explicitGenericInvocation();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1213:9: '.' 'this'
                    {
                    match(input,28,FOLLOW_28_in_identifierSuffix5829); if (state.failed) return ;
                    match(input,118,FOLLOW_118_in_identifierSuffix5831); if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1214:9: '.' 'super' arguments
                    {
                    match(input,28,FOLLOW_28_in_identifierSuffix5841); if (state.failed) return ;
                    match(input,64,FOLLOW_64_in_identifierSuffix5843); if (state.failed) return ;
                    pushFollow(FOLLOW_arguments_in_identifierSuffix5845);
                    arguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1215:9: '.' 'new' ( nonWildcardTypeArguments )? innerCreator
                    {
                    match(input,28,FOLLOW_28_in_identifierSuffix5855); if (state.failed) return ;
                    match(input,119,FOLLOW_119_in_identifierSuffix5857); if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1215:19: ( nonWildcardTypeArguments )?
                    int alt173=2;
                    int LA173_0 = input.LA(1);

                    if ( (LA173_0==33) ) {
                        alt173=1;
                    }
                    switch (alt173) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:1215:20: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_identifierSuffix5860);
                            nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_innerCreator_in_identifierSuffix5864);
                    innerCreator();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 126, identifierSuffix_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "identifierSuffix"


    // $ANTLR start "creator"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1218:1: creator : ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) ;
    public final void creator() throws RecognitionException {
        int creator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 127) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1219:5: ( ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1219:7: ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest )
            {
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1219:7: ( nonWildcardTypeArguments )?
            int alt175=2;
            int LA175_0 = input.LA(1);

            if ( (LA175_0==33) ) {
                alt175=1;
            }
            switch (alt175) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: nonWildcardTypeArguments
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator5881);
                    nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_createdName_in_creator5884);
            createdName();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1220:9: ( arrayCreatorRest | classCreatorRest )
            int alt176=2;
            int LA176_0 = input.LA(1);

            if ( (LA176_0==41) ) {
                alt176=1;
            }
            else if ( (LA176_0==65) ) {
                alt176=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 176, 0, input);

                throw nvae;
            }
            switch (alt176) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1220:10: arrayCreatorRest
                    {
                    pushFollow(FOLLOW_arrayCreatorRest_in_creator5895);
                    arrayCreatorRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1220:29: classCreatorRest
                    {
                    pushFollow(FOLLOW_classCreatorRest_in_creator5899);
                    classCreatorRest();

                    state._fsp--;
                    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 127, creator_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "creator"


    // $ANTLR start "createdName"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1223:1: createdName : ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* | primitiveType );
    public final void createdName() throws RecognitionException {
        int createdName_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 128) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1224:5: ( Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )* | primitiveType )
            int alt180=2;
            int LA180_0 = input.LA(1);

            if ( (LA180_0==Identifier) ) {
                alt180=1;
            }
            else if ( ((LA180_0>=55 && LA180_0<=62)) ) {
                alt180=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 180, 0, input);

                throw nvae;
            }
            switch (alt180) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1224:7: Identifier ( typeArguments )? ( '.' Identifier ( typeArguments )? )*
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_createdName5917); if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1224:18: ( typeArguments )?
                    int alt177=2;
                    int LA177_0 = input.LA(1);

                    if ( (LA177_0==33) ) {
                        alt177=1;
                    }
                    switch (alt177) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_createdName5919);
                            typeArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1225:9: ( '.' Identifier ( typeArguments )? )*
                    loop179:
                    do {
                        int alt179=2;
                        int LA179_0 = input.LA(1);

                        if ( (LA179_0==28) ) {
                            alt179=1;
                        }


                        switch (alt179) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1225:10: '.' Identifier ( typeArguments )?
                    	    {
                    	    match(input,28,FOLLOW_28_in_createdName5931); if (state.failed) return ;
                    	    match(input,Identifier,FOLLOW_Identifier_in_createdName5933); if (state.failed) return ;
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1225:25: ( typeArguments )?
                    	    int alt178=2;
                    	    int LA178_0 = input.LA(1);

                    	    if ( (LA178_0==33) ) {
                    	        alt178=1;
                    	    }
                    	    switch (alt178) {
                    	        case 1 :
                    	            // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: typeArguments
                    	            {
                    	            pushFollow(FOLLOW_typeArguments_in_createdName5935);
                    	            typeArguments();

                    	            state._fsp--;
                    	            if (state.failed) return ;

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop179;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1226:7: primitiveType
                    {
                    pushFollow(FOLLOW_primitiveType_in_createdName5946);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 128, createdName_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "createdName"


    // $ANTLR start "innerCreator"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1229:1: innerCreator : Identifier classCreatorRest ;
    public final void innerCreator() throws RecognitionException {
        int innerCreator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 129) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1230:5: ( Identifier classCreatorRest )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1230:7: Identifier classCreatorRest
            {
            match(input,Identifier,FOLLOW_Identifier_in_innerCreator5963); if (state.failed) return ;
            pushFollow(FOLLOW_classCreatorRest_in_innerCreator5965);
            classCreatorRest();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 129, innerCreator_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "innerCreator"


    // $ANTLR start "arrayCreatorRest"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1233:1: arrayCreatorRest : '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* ) ;
    public final void arrayCreatorRest() throws RecognitionException {
        int arrayCreatorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 130) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1234:5: ( '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* ) )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1234:7: '[' ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* )
            {
            match(input,41,FOLLOW_41_in_arrayCreatorRest5982); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1235:9: ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* )
            int alt184=2;
            alt184 = dfa184.predict(input);
            switch (alt184) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1235:13: ']' ( '[' ']' )* arrayInitializer
                    {
                    match(input,42,FOLLOW_42_in_arrayCreatorRest5996); if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1235:17: ( '[' ']' )*
                    loop181:
                    do {
                        int alt181=2;
                        int LA181_0 = input.LA(1);

                        if ( (LA181_0==41) ) {
                            alt181=1;
                        }


                        switch (alt181) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1235:18: '[' ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_arrayCreatorRest5999); if (state.failed) return ;
                    	    match(input,42,FOLLOW_42_in_arrayCreatorRest6001); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop181;
                        }
                    } while (true);

                    pushFollow(FOLLOW_arrayInitializer_in_arrayCreatorRest6005);
                    arrayInitializer();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1236:13: expression ']' ( '[' expression ']' )* ( '[' ']' )*
                    {
                    pushFollow(FOLLOW_expression_in_arrayCreatorRest6019);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,42,FOLLOW_42_in_arrayCreatorRest6021); if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1236:28: ( '[' expression ']' )*
                    loop182:
                    do {
                        int alt182=2;
                        alt182 = dfa182.predict(input);
                        switch (alt182) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1236:29: '[' expression ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_arrayCreatorRest6024); if (state.failed) return ;
                    	    pushFollow(FOLLOW_expression_in_arrayCreatorRest6026);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    match(input,42,FOLLOW_42_in_arrayCreatorRest6028); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop182;
                        }
                    } while (true);

                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1236:50: ( '[' ']' )*
                    loop183:
                    do {
                        int alt183=2;
                        alt183 = dfa183.predict(input);
                        switch (alt183) {
                    	case 1 :
                    	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1236:51: '[' ']'
                    	    {
                    	    match(input,41,FOLLOW_41_in_arrayCreatorRest6033); if (state.failed) return ;
                    	    match(input,42,FOLLOW_42_in_arrayCreatorRest6035); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop183;
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
            if ( state.backtracking>0 ) { memoize(input, 130, arrayCreatorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "arrayCreatorRest"


    // $ANTLR start "classCreatorRest"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1240:1: classCreatorRest : arguments ( classBody )? ;
    public final void classCreatorRest() throws RecognitionException {
        int classCreatorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 131) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1241:5: ( arguments ( classBody )? )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1241:7: arguments ( classBody )?
            {
            pushFollow(FOLLOW_arguments_in_classCreatorRest6064);
            arguments();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1241:17: ( classBody )?
            int alt185=2;
            alt185 = dfa185.predict(input);
            switch (alt185) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: classBody
                    {
                    pushFollow(FOLLOW_classBody_in_classCreatorRest6066);
                    classBody();

                    state._fsp--;
                    if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 131, classCreatorRest_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "classCreatorRest"


    // $ANTLR start "explicitGenericInvocation"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1244:1: explicitGenericInvocation : nonWildcardTypeArguments explicitGenericInvocationSuffix ;
    public final void explicitGenericInvocation() throws RecognitionException {
        int explicitGenericInvocation_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 132) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1245:5: ( nonWildcardTypeArguments explicitGenericInvocationSuffix )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1245:7: nonWildcardTypeArguments explicitGenericInvocationSuffix
            {
            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation6084);
            nonWildcardTypeArguments();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_explicitGenericInvocationSuffix_in_explicitGenericInvocation6086);
            explicitGenericInvocationSuffix();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 132, explicitGenericInvocation_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "explicitGenericInvocation"


    // $ANTLR start "nonWildcardTypeArguments"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1248:1: nonWildcardTypeArguments : '<' typeList '>' ;
    public final void nonWildcardTypeArguments() throws RecognitionException {
        int nonWildcardTypeArguments_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 133) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1249:5: ( '<' typeList '>' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1249:7: '<' typeList '>'
            {
            match(input,33,FOLLOW_33_in_nonWildcardTypeArguments6103); if (state.failed) return ;
            pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments6105);
            typeList();

            state._fsp--;
            if (state.failed) return ;
            match(input,35,FOLLOW_35_in_nonWildcardTypeArguments6107); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 133, nonWildcardTypeArguments_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "nonWildcardTypeArguments"


    // $ANTLR start "explicitGenericInvocationSuffix"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1252:1: explicitGenericInvocationSuffix : ( 'super' superSuffix | Identifier arguments );
    public final void explicitGenericInvocationSuffix() throws RecognitionException {
        int explicitGenericInvocationSuffix_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 134) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1253:5: ( 'super' superSuffix | Identifier arguments )
            int alt186=2;
            int LA186_0 = input.LA(1);

            if ( (LA186_0==64) ) {
                alt186=1;
            }
            else if ( (LA186_0==Identifier) ) {
                alt186=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 186, 0, input);

                throw nvae;
            }
            switch (alt186) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1253:7: 'super' superSuffix
                    {
                    match(input,64,FOLLOW_64_in_explicitGenericInvocationSuffix6124); if (state.failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_explicitGenericInvocationSuffix6126);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1254:9: Identifier arguments
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_explicitGenericInvocationSuffix6136); if (state.failed) return ;
                    pushFollow(FOLLOW_arguments_in_explicitGenericInvocationSuffix6138);
                    arguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 134, explicitGenericInvocationSuffix_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "explicitGenericInvocationSuffix"


    // $ANTLR start "selector"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1257:1: selector : ( '.' Identifier ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '.' 'new' ( nonWildcardTypeArguments )? innerCreator | '[' expression ']' );
    public final void selector() throws RecognitionException {
        int selector_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 135) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1258:5: ( '.' Identifier ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '.' 'new' ( nonWildcardTypeArguments )? innerCreator | '[' expression ']' )
            int alt189=5;
            int LA189_0 = input.LA(1);

            if ( (LA189_0==28) ) {
                switch ( input.LA(2) ) {
                case Identifier:
                    {
                    alt189=1;
                    }
                    break;
                case 118:
                    {
                    alt189=2;
                    }
                    break;
                case 64:
                    {
                    alt189=3;
                    }
                    break;
                case 119:
                    {
                    alt189=4;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 189, 1, input);

                    throw nvae;
                }

            }
            else if ( (LA189_0==41) ) {
                alt189=5;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 189, 0, input);

                throw nvae;
            }
            switch (alt189) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1258:7: '.' Identifier ( arguments )?
                    {
                    match(input,28,FOLLOW_28_in_selector6155); if (state.failed) return ;
                    match(input,Identifier,FOLLOW_Identifier_in_selector6157); if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1258:22: ( arguments )?
                    int alt187=2;
                    alt187 = dfa187.predict(input);
                    switch (alt187) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:1258:23: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_selector6160);
                            arguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1259:9: '.' 'this'
                    {
                    match(input,28,FOLLOW_28_in_selector6172); if (state.failed) return ;
                    match(input,118,FOLLOW_118_in_selector6174); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1260:9: '.' 'super' superSuffix
                    {
                    match(input,28,FOLLOW_28_in_selector6184); if (state.failed) return ;
                    match(input,64,FOLLOW_64_in_selector6186); if (state.failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_selector6188);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1261:9: '.' 'new' ( nonWildcardTypeArguments )? innerCreator
                    {
                    match(input,28,FOLLOW_28_in_selector6198); if (state.failed) return ;
                    match(input,119,FOLLOW_119_in_selector6200); if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1261:19: ( nonWildcardTypeArguments )?
                    int alt188=2;
                    int LA188_0 = input.LA(1);

                    if ( (LA188_0==33) ) {
                        alt188=1;
                    }
                    switch (alt188) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:1261:20: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_selector6203);
                            nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_innerCreator_in_selector6207);
                    innerCreator();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1262:9: '[' expression ']'
                    {
                    match(input,41,FOLLOW_41_in_selector6217); if (state.failed) return ;
                    pushFollow(FOLLOW_expression_in_selector6219);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,42,FOLLOW_42_in_selector6221); if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 135, selector_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "selector"


    // $ANTLR start "superSuffix"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1265:1: superSuffix : ( arguments | '.' Identifier ( arguments )? );
    public final void superSuffix() throws RecognitionException {
        int superSuffix_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 136) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1266:5: ( arguments | '.' Identifier ( arguments )? )
            int alt191=2;
            int LA191_0 = input.LA(1);

            if ( (LA191_0==65) ) {
                alt191=1;
            }
            else if ( (LA191_0==28) ) {
                alt191=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 191, 0, input);

                throw nvae;
            }
            switch (alt191) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1266:7: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_superSuffix6238);
                    arguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1267:9: '.' Identifier ( arguments )?
                    {
                    match(input,28,FOLLOW_28_in_superSuffix6248); if (state.failed) return ;
                    match(input,Identifier,FOLLOW_Identifier_in_superSuffix6250); if (state.failed) return ;
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:1267:24: ( arguments )?
                    int alt190=2;
                    alt190 = dfa190.predict(input);
                    switch (alt190) {
                        case 1 :
                            // src/main/resources/org/drools/semantics/java/parser/Java.g:1267:25: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_superSuffix6253);
                            arguments();

                            state._fsp--;
                            if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 136, superSuffix_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "superSuffix"


    // $ANTLR start "arguments"
    // src/main/resources/org/drools/semantics/java/parser/Java.g:1270:5: arguments : '(' ( expressionList )? ')' ;
    public final void arguments() throws RecognitionException {
        int arguments_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 137) ) { return ; }
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1271:5: ( '(' ( expressionList )? ')' )
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1271:7: '(' ( expressionList )? ')'
            {
            match(input,65,FOLLOW_65_in_arguments6280); if (state.failed) return ;
            // src/main/resources/org/drools/semantics/java/parser/Java.g:1271:11: ( expressionList )?
            int alt192=2;
            alt192 = dfa192.predict(input);
            switch (alt192) {
                case 1 :
                    // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments6282);
                    expressionList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,66,FOLLOW_66_in_arguments6285); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 137, arguments_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "arguments"

    // $ANTLR start synpred1_Java
    public final void synpred1_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:265:7: ( annotations )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:265:7: annotations
        {
        pushFollow(FOLLOW_annotations_in_synpred1_Java81);
        annotations();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_Java

    // $ANTLR start synpred38_Java
    public final void synpred38_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:361:7: ( methodDeclaration )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:361:7: methodDeclaration
        {
        pushFollow(FOLLOW_methodDeclaration_in_synpred38_Java718);
        methodDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred38_Java

    // $ANTLR start synpred39_Java
    public final void synpred39_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:362:7: ( fieldDeclaration )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:362:7: fieldDeclaration
        {
        pushFollow(FOLLOW_fieldDeclaration_in_synpred39_Java726);
        fieldDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred39_Java

    // $ANTLR start synpred85_Java
    public final void synpred85_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:514:19: ( '.' Identifier )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:514:19: '.' Identifier
        {
        match(input,28,FOLLOW_28_in_synpred85_Java1703); if (state.failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred85_Java1705); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred85_Java

    // $ANTLR start synpred120_Java
    public final void synpred120_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:604:7: ( annotation )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:604:7: annotation
        {
        pushFollow(FOLLOW_annotation_in_synpred120_Java2287);
        annotation();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred120_Java

    // $ANTLR start synpred135_Java
    public final void synpred135_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:651:9: ( classDeclaration ( ';' )? )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:651:9: classDeclaration ( ';' )?
        {
        pushFollow(FOLLOW_classDeclaration_in_synpred135_Java2582);
        classDeclaration();

        state._fsp--;
        if (state.failed) return ;
        // src/main/resources/org/drools/semantics/java/parser/Java.g:651:26: ( ';' )?
        int alt208=2;
        int LA208_0 = input.LA(1);

        if ( (LA208_0==25) ) {
            alt208=1;
        }
        switch (alt208) {
            case 1 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ';'
                {
                match(input,25,FOLLOW_25_in_synpred135_Java2584); if (state.failed) return ;

                }
                break;

        }


        }
    }
    // $ANTLR end synpred135_Java

    // $ANTLR start synpred137_Java
    public final void synpred137_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:652:9: ( interfaceDeclaration ( ';' )? )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:652:9: interfaceDeclaration ( ';' )?
        {
        pushFollow(FOLLOW_interfaceDeclaration_in_synpred137_Java2595);
        interfaceDeclaration();

        state._fsp--;
        if (state.failed) return ;
        // src/main/resources/org/drools/semantics/java/parser/Java.g:652:30: ( ';' )?
        int alt209=2;
        int LA209_0 = input.LA(1);

        if ( (LA209_0==25) ) {
            alt209=1;
        }
        switch (alt209) {
            case 1 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ';'
                {
                match(input,25,FOLLOW_25_in_synpred137_Java2597); if (state.failed) return ;

                }
                break;

        }


        }
    }
    // $ANTLR end synpred137_Java

    // $ANTLR start synpred139_Java
    public final void synpred139_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:653:9: ( enumDeclaration ( ';' )? )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:653:9: enumDeclaration ( ';' )?
        {
        pushFollow(FOLLOW_enumDeclaration_in_synpred139_Java2608);
        enumDeclaration();

        state._fsp--;
        if (state.failed) return ;
        // src/main/resources/org/drools/semantics/java/parser/Java.g:653:25: ( ';' )?
        int alt210=2;
        int LA210_0 = input.LA(1);

        if ( (LA210_0==25) ) {
            alt210=1;
        }
        switch (alt210) {
            case 1 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:0:0: ';'
                {
                match(input,25,FOLLOW_25_in_synpred139_Java2610); if (state.failed) return ;

                }
                break;

        }


        }
    }
    // $ANTLR end synpred139_Java

    // $ANTLR start synpred144_Java
    public final void synpred144_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:692:7: ( localVariableDeclaration )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:692:7: localVariableDeclaration
        {
        pushFollow(FOLLOW_localVariableDeclaration_in_synpred144_Java2791);
        localVariableDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred144_Java

    // $ANTLR start synpred145_Java
    public final void synpred145_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:693:7: ( classOrInterfaceDeclaration )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:693:7: classOrInterfaceDeclaration
        {
        pushFollow(FOLLOW_classOrInterfaceDeclaration_in_synpred145_Java2799);
        classOrInterfaceDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred145_Java

    // $ANTLR start synpred169_Java
    public final void synpred169_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:782:17: ( 'if' parExpression )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:782:17: 'if' parExpression
        {
        match(input,82,FOLLOW_82_in_synpred169_Java3292); if (state.failed) return ;
        pushFollow(FOLLOW_parExpression_in_synpred169_Java3294);
        parExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred169_Java

    // $ANTLR start synpred170_Java
    public final void synpred170_Java_fragment() throws RecognitionException {   
        Token y=null;
        JavaParser.statement_return z = null;


        // src/main/resources/org/drools/semantics/java/parser/Java.g:782:6: (y= 'else' ( 'if' parExpression )? z= statement )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:782:6: y= 'else' ( 'if' parExpression )? z= statement
        {
        y=(Token)match(input,83,FOLLOW_83_in_synpred170_Java3288); if (state.failed) return ;
        // src/main/resources/org/drools/semantics/java/parser/Java.g:782:16: ( 'if' parExpression )?
        int alt215=2;
        alt215 = dfa215.predict(input);
        switch (alt215) {
            case 1 :
                // src/main/resources/org/drools/semantics/java/parser/Java.g:782:17: 'if' parExpression
                {
                match(input,82,FOLLOW_82_in_synpred170_Java3292); if (state.failed) return ;
                pushFollow(FOLLOW_parExpression_in_synpred170_Java3294);
                parExpression();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }

        pushFollow(FOLLOW_statement_in_synpred170_Java3325);
        z=statement();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred170_Java

    // $ANTLR start synpred172_Java
    public final void synpred172_Java_fragment() throws RecognitionException {   
        Token id=null;
        Token z=null;

        // src/main/resources/org/drools/semantics/java/parser/Java.g:815:9: ( ( ( variableModifier )* type id= Identifier z= ':' expression ) )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:815:9: ( ( variableModifier )* type id= Identifier z= ':' expression )
        {
        // src/main/resources/org/drools/semantics/java/parser/Java.g:815:9: ( ( variableModifier )* type id= Identifier z= ':' expression )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:815:11: ( variableModifier )* type id= Identifier z= ':' expression
        {
        // src/main/resources/org/drools/semantics/java/parser/Java.g:815:11: ( variableModifier )*
        loop216:
        do {
            int alt216=2;
            int LA216_0 = input.LA(1);

            if ( (LA216_0==49||LA216_0==71) ) {
                alt216=1;
            }


            switch (alt216) {
        	case 1 :
        	    // src/main/resources/org/drools/semantics/java/parser/Java.g:815:13: variableModifier
        	    {
        	    pushFollow(FOLLOW_variableModifier_in_synpred172_Java3447);
        	    variableModifier();

        	    state._fsp--;
        	    if (state.failed) return ;

        	    }
        	    break;

        	default :
        	    break loop216;
            }
        } while (true);

        pushFollow(FOLLOW_type_in_synpred172_Java3486);
        type();

        state._fsp--;
        if (state.failed) return ;
        id=(Token)match(input,Identifier,FOLLOW_Identifier_in_synpred172_Java3512); if (state.failed) return ;
        z=(Token)match(input,74,FOLLOW_74_in_synpred172_Java3539); if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred172_Java3541);
        expression();

        state._fsp--;
        if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred172_Java

    // $ANTLR start synpred188_Java
    public final void synpred188_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1009:7: ( 'case' constantExpression ':' )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1009:7: 'case' constantExpression ':'
        {
        match(input,94,FOLLOW_94_in_synpred188_Java4372); if (state.failed) return ;
        pushFollow(FOLLOW_constantExpression_in_synpred188_Java4374);
        constantExpression();

        state._fsp--;
        if (state.failed) return ;
        match(input,74,FOLLOW_74_in_synpred188_Java4376); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred188_Java

    // $ANTLR start synpred189_Java
    public final void synpred189_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1010:9: ( 'case' enumConstantName ':' )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1010:9: 'case' enumConstantName ':'
        {
        match(input,94,FOLLOW_94_in_synpred189_Java4386); if (state.failed) return ;
        pushFollow(FOLLOW_enumConstantName_in_synpred189_Java4388);
        enumConstantName();

        state._fsp--;
        if (state.failed) return ;
        match(input,74,FOLLOW_74_in_synpred189_Java4390); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred189_Java

    // $ANTLR start synpred191_Java
    public final void synpred191_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1029:7: ( forVarControl )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1029:7: forVarControl
        {
        pushFollow(FOLLOW_forVarControl_in_synpred191_Java4480);
        forVarControl();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred191_Java

    // $ANTLR start synpred196_Java
    public final void synpred196_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1034:7: ( ( variableModifier )* type variableDeclarators )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1034:7: ( variableModifier )* type variableDeclarators
        {
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1034:7: ( variableModifier )*
        loop220:
        do {
            int alt220=2;
            int LA220_0 = input.LA(1);

            if ( (LA220_0==49||LA220_0==71) ) {
                alt220=1;
            }


            switch (alt220) {
        	case 1 :
        	    // src/main/resources/org/drools/semantics/java/parser/Java.g:1034:9: variableModifier
        	    {
        	    pushFollow(FOLLOW_variableModifier_in_synpred196_Java4518);
        	    variableModifier();

        	    state._fsp--;
        	    if (state.failed) return ;

        	    }
        	    break;

        	default :
        	    break loop220;
            }
        } while (true);

        pushFollow(FOLLOW_type_in_synpred196_Java4553);
        type();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_variableDeclarators_in_synpred196_Java4573);
        variableDeclarators();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred196_Java

    // $ANTLR start synpred199_Java
    public final void synpred199_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1097:30: ( assignmentOperator expression )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1097:30: assignmentOperator expression
        {
        pushFollow(FOLLOW_assignmentOperator_in_synpred199_Java4800);
        assignmentOperator();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred199_Java4802);
        expression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred199_Java

    // $ANTLR start synpred210_Java
    public final void synpred210_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1111:9: ( '>' '>' '=' )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1111:9: '>' '>' '='
        {
        match(input,35,FOLLOW_35_in_synpred210_Java4925); if (state.failed) return ;
        match(input,35,FOLLOW_35_in_synpred210_Java4927); if (state.failed) return ;
        match(input,44,FOLLOW_44_in_synpred210_Java4929); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred210_Java

    // $ANTLR start synpred220_Java
    public final void synpred220_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1148:27: ( relationalOp shiftExpression )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1148:27: relationalOp shiftExpression
        {
        pushFollow(FOLLOW_relationalOp_in_synpred220_Java5200);
        relationalOp();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_shiftExpression_in_synpred220_Java5202);
        shiftExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred220_Java

    // $ANTLR start synpred224_Java
    public final void synpred224_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1156:30: ( shiftOp additiveExpression )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1156:30: shiftOp additiveExpression
        {
        pushFollow(FOLLOW_shiftOp_in_synpred224_Java5263);
        shiftOp();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_additiveExpression_in_synpred224_Java5265);
        additiveExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred224_Java

    // $ANTLR start synpred226_Java
    public final void synpred226_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1161:18: ( '>' '>' '>' )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1161:18: '>' '>' '>'
        {
        match(input,35,FOLLOW_35_in_synpred226_Java5301); if (state.failed) return ;
        match(input,35,FOLLOW_35_in_synpred226_Java5303); if (state.failed) return ;
        match(input,35,FOLLOW_35_in_synpred226_Java5305); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred226_Java

    // $ANTLR start synpred238_Java
    public final void synpred238_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1184:9: ( castExpression )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1184:9: castExpression
        {
        pushFollow(FOLLOW_castExpression_in_synpred238_Java5494);
        castExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred238_Java

    // $ANTLR start synpred242_Java
    public final void synpred242_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1189:8: ( '(' primitiveType ')' unaryExpression )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1189:8: '(' primitiveType ')' unaryExpression
        {
        match(input,65,FOLLOW_65_in_synpred242_Java5532); if (state.failed) return ;
        pushFollow(FOLLOW_primitiveType_in_synpred242_Java5534);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;
        match(input,66,FOLLOW_66_in_synpred242_Java5536); if (state.failed) return ;
        pushFollow(FOLLOW_unaryExpression_in_synpred242_Java5538);
        unaryExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred242_Java

    // $ANTLR start synpred243_Java
    public final void synpred243_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1190:13: ( type )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1190:13: type
        {
        pushFollow(FOLLOW_type_in_synpred243_Java5550);
        type();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred243_Java

    // $ANTLR start synpred247_Java
    public final void synpred247_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1197:17: ( '.' Identifier )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1197:17: '.' Identifier
        {
        match(input,28,FOLLOW_28_in_synpred247_Java5617); if (state.failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred247_Java5619); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred247_Java

    // $ANTLR start synpred248_Java
    public final void synpred248_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1197:35: ( identifierSuffix )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1197:35: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred248_Java5624);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred248_Java

    // $ANTLR start synpred251_Java
    public final void synpred251_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1199:22: ( '.' Identifier )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1199:22: '.' Identifier
        {
        match(input,28,FOLLOW_28_in_synpred251_Java5651); if (state.failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred251_Java5653); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred251_Java

    // $ANTLR start synpred252_Java
    public final void synpred252_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1199:40: ( identifierSuffix )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1199:40: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred252_Java5658);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred252_Java

    // $ANTLR start synpred256_Java
    public final void synpred256_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1202:127: ( '.' Identifier )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1202:127: '.' Identifier
        {
        match(input,28,FOLLOW_28_in_synpred256_Java5699); if (state.failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred256_Java5701); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred256_Java

    // $ANTLR start synpred257_Java
    public final void synpred257_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1202:145: ( identifierSuffix )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1202:145: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred257_Java5706);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred257_Java

    // $ANTLR start synpred263_Java
    public final void synpred263_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1209:8: ( '[' expression ']' )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1209:8: '[' expression ']'
        {
        match(input,41,FOLLOW_41_in_synpred263_Java5778); if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred263_Java5780);
        expression();

        state._fsp--;
        if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred263_Java5782); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred263_Java

    // $ANTLR start synpred279_Java
    public final void synpred279_Java_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1236:29: ( '[' expression ']' )
        // src/main/resources/org/drools/semantics/java/parser/Java.g:1236:29: '[' expression ']'
        {
        match(input,41,FOLLOW_41_in_synpred279_Java6024); if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred279_Java6026);
        expression();

        state._fsp--;
        if (state.failed) return ;
        match(input,42,FOLLOW_42_in_synpred279_Java6028); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred279_Java

    // Delegated rules

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
    public final boolean synpred224_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred224_Java_fragment(); // can never throw exception
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
    public final boolean synpred243_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred243_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred256_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred256_Java_fragment(); // can never throw exception
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
    public final boolean synpred210_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred210_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred220_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred220_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred189_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred189_Java_fragment(); // can never throw exception
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
    public final boolean synpred196_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred196_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred238_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred238_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred252_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred252_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred188_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred188_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred257_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred257_Java_fragment(); // can never throw exception
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
    public final boolean synpred170_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred170_Java_fragment(); // can never throw exception
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
    public final boolean synpred169_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred169_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred199_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred199_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred242_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred242_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred247_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred247_Java_fragment(); // can never throw exception
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
    public final boolean synpred263_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred263_Java_fragment(); // can never throw exception
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
    public final boolean synpred251_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred251_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred248_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred248_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred279_Java() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred279_Java_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA1 dfa1 = new DFA1(this);
    protected DFA2 dfa2 = new DFA2(this);
    protected DFA3 dfa3 = new DFA3(this);
    protected DFA4 dfa4 = new DFA4(this);
    protected DFA8 dfa8 = new DFA8(this);
    protected DFA9 dfa9 = new DFA9(this);
    protected DFA26 dfa26 = new DFA26(this);
    protected DFA31 dfa31 = new DFA31(this);
    protected DFA32 dfa32 = new DFA32(this);
    protected DFA35 dfa35 = new DFA35(this);
    protected DFA34 dfa34 = new DFA34(this);
    protected DFA36 dfa36 = new DFA36(this);
    protected DFA40 dfa40 = new DFA40(this);
    protected DFA39 dfa39 = new DFA39(this);
    protected DFA60 dfa60 = new DFA60(this);
    protected DFA63 dfa63 = new DFA63(this);
    protected DFA61 dfa61 = new DFA61(this);
    protected DFA64 dfa64 = new DFA64(this);
    protected DFA67 dfa67 = new DFA67(this);
    protected DFA69 dfa69 = new DFA69(this);
    protected DFA68 dfa68 = new DFA68(this);
    protected DFA70 dfa70 = new DFA70(this);
    protected DFA71 dfa71 = new DFA71(this);
    protected DFA85 dfa85 = new DFA85(this);
    protected DFA87 dfa87 = new DFA87(this);
    protected DFA86 dfa86 = new DFA86(this);
    protected DFA88 dfa88 = new DFA88(this);
    protected DFA90 dfa90 = new DFA90(this);
    protected DFA91 dfa91 = new DFA91(this);
    protected DFA93 dfa93 = new DFA93(this);
    protected DFA94 dfa94 = new DFA94(this);
    protected DFA95 dfa95 = new DFA95(this);
    protected DFA96 dfa96 = new DFA96(this);
    protected DFA101 dfa101 = new DFA101(this);
    protected DFA97 dfa97 = new DFA97(this);
    protected DFA98 dfa98 = new DFA98(this);
    protected DFA99 dfa99 = new DFA99(this);
    protected DFA100 dfa100 = new DFA100(this);
    protected DFA104 dfa104 = new DFA104(this);
    protected DFA105 dfa105 = new DFA105(this);
    protected DFA111 dfa111 = new DFA111(this);
    protected DFA108 dfa108 = new DFA108(this);
    protected DFA113 dfa113 = new DFA113(this);
    protected DFA112 dfa112 = new DFA112(this);
    protected DFA118 dfa118 = new DFA118(this);
    protected DFA115 dfa115 = new DFA115(this);
    protected DFA116 dfa116 = new DFA116(this);
    protected DFA117 dfa117 = new DFA117(this);
    protected DFA119 dfa119 = new DFA119(this);
    protected DFA121 dfa121 = new DFA121(this);
    protected DFA120 dfa120 = new DFA120(this);
    protected DFA123 dfa123 = new DFA123(this);
    protected DFA122 dfa122 = new DFA122(this);
    protected DFA125 dfa125 = new DFA125(this);
    protected DFA129 dfa129 = new DFA129(this);
    protected DFA130 dfa130 = new DFA130(this);
    protected DFA135 dfa135 = new DFA135(this);
    protected DFA132 dfa132 = new DFA132(this);
    protected DFA133 dfa133 = new DFA133(this);
    protected DFA134 dfa134 = new DFA134(this);
    protected DFA137 dfa137 = new DFA137(this);
    protected DFA140 dfa140 = new DFA140(this);
    protected DFA141 dfa141 = new DFA141(this);
    protected DFA142 dfa142 = new DFA142(this);
    protected DFA143 dfa143 = new DFA143(this);
    protected DFA144 dfa144 = new DFA144(this);
    protected DFA145 dfa145 = new DFA145(this);
    protected DFA146 dfa146 = new DFA146(this);
    protected DFA147 dfa147 = new DFA147(this);
    protected DFA148 dfa148 = new DFA148(this);
    protected DFA149 dfa149 = new DFA149(this);
    protected DFA150 dfa150 = new DFA150(this);
    protected DFA151 dfa151 = new DFA151(this);
    protected DFA152 dfa152 = new DFA152(this);
    protected DFA154 dfa154 = new DFA154(this);
    protected DFA155 dfa155 = new DFA155(this);
    protected DFA156 dfa156 = new DFA156(this);
    protected DFA159 dfa159 = new DFA159(this);
    protected DFA157 dfa157 = new DFA157(this);
    protected DFA158 dfa158 = new DFA158(this);
    protected DFA161 dfa161 = new DFA161(this);
    protected DFA160 dfa160 = new DFA160(this);
    protected DFA170 dfa170 = new DFA170(this);
    protected DFA163 dfa163 = new DFA163(this);
    protected DFA164 dfa164 = new DFA164(this);
    protected DFA165 dfa165 = new DFA165(this);
    protected DFA166 dfa166 = new DFA166(this);
    protected DFA167 dfa167 = new DFA167(this);
    protected DFA168 dfa168 = new DFA168(this);
    protected DFA174 dfa174 = new DFA174(this);
    protected DFA172 dfa172 = new DFA172(this);
    protected DFA184 dfa184 = new DFA184(this);
    protected DFA182 dfa182 = new DFA182(this);
    protected DFA183 dfa183 = new DFA183(this);
    protected DFA185 dfa185 = new DFA185(this);
    protected DFA187 dfa187 = new DFA187(this);
    protected DFA190 dfa190 = new DFA190(this);
    protected DFA192 dfa192 = new DFA192(this);
    protected DFA215 dfa215 = new DFA215(this);
    static final String DFA1_eotS =
        "\27\uffff";
    static final String DFA1_eofS =
        "\1\2\26\uffff";
    static final String DFA1_minS =
        "\1\5\1\4\23\uffff\1\0\1\uffff";
    static final String DFA1_maxS =
        "\1\107\1\47\23\uffff\1\0\1\uffff";
    static final String DFA1_acceptS =
        "\2\uffff\1\2\23\uffff\1\1";
    static final String DFA1_specialS =
        "\25\uffff\1\0\1\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\2\22\uffff\4\2\2\uffff\1\2\10\uffff\1\2\5\uffff\12\2\20\uffff"+
            "\1\1",
            "\1\25\42\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            ""
    };

    static final short[] DFA1_eot = DFA.unpackEncodedString(DFA1_eotS);
    static final short[] DFA1_eof = DFA.unpackEncodedString(DFA1_eofS);
    static final char[] DFA1_min = DFA.unpackEncodedStringToUnsignedChars(DFA1_minS);
    static final char[] DFA1_max = DFA.unpackEncodedStringToUnsignedChars(DFA1_maxS);
    static final short[] DFA1_accept = DFA.unpackEncodedString(DFA1_acceptS);
    static final short[] DFA1_special = DFA.unpackEncodedString(DFA1_specialS);
    static final short[][] DFA1_transition;

    static {
        int numStates = DFA1_transitionS.length;
        DFA1_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA1_transition[i] = DFA.unpackEncodedString(DFA1_transitionS[i]);
        }
    }

    class DFA1 extends DFA {

        public DFA1(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 1;
            this.eot = DFA1_eot;
            this.eof = DFA1_eof;
            this.min = DFA1_min;
            this.max = DFA1_max;
            this.accept = DFA1_accept;
            this.special = DFA1_special;
            this.transition = DFA1_transition;
        }
        public String getDescription() {
            return "265:7: ( annotations )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA1_21 = input.LA(1);

                         
                        int index1_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_Java()) ) {s = 22;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index1_21);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 1, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA2_eotS =
        "\24\uffff";
    static final String DFA2_eofS =
        "\1\2\23\uffff";
    static final String DFA2_minS =
        "\1\5\23\uffff";
    static final String DFA2_maxS =
        "\1\107\23\uffff";
    static final String DFA2_acceptS =
        "\1\uffff\1\1\1\2\21\uffff";
    static final String DFA2_specialS =
        "\24\uffff}>";
    static final String[] DFA2_transitionS = {
            "\1\2\22\uffff\1\1\3\2\2\uffff\1\2\10\uffff\1\2\5\uffff\12\2"+
            "\20\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA2_eot = DFA.unpackEncodedString(DFA2_eotS);
    static final short[] DFA2_eof = DFA.unpackEncodedString(DFA2_eofS);
    static final char[] DFA2_min = DFA.unpackEncodedStringToUnsignedChars(DFA2_minS);
    static final char[] DFA2_max = DFA.unpackEncodedStringToUnsignedChars(DFA2_maxS);
    static final short[] DFA2_accept = DFA.unpackEncodedString(DFA2_acceptS);
    static final short[] DFA2_special = DFA.unpackEncodedString(DFA2_specialS);
    static final short[][] DFA2_transition;

    static {
        int numStates = DFA2_transitionS.length;
        DFA2_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA2_transition[i] = DFA.unpackEncodedString(DFA2_transitionS[i]);
        }
    }

    class DFA2 extends DFA {

        public DFA2(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 2;
            this.eot = DFA2_eot;
            this.eof = DFA2_eof;
            this.min = DFA2_min;
            this.max = DFA2_max;
            this.accept = DFA2_accept;
            this.special = DFA2_special;
            this.transition = DFA2_transition;
        }
        public String getDescription() {
            return "266:9: ( packageDeclaration )?";
        }
    }
    static final String DFA3_eotS =
        "\23\uffff";
    static final String DFA3_eofS =
        "\1\1\22\uffff";
    static final String DFA3_minS =
        "\1\5\22\uffff";
    static final String DFA3_maxS =
        "\1\107\22\uffff";
    static final String DFA3_acceptS =
        "\1\uffff\1\2\20\uffff\1\1";
    static final String DFA3_specialS =
        "\23\uffff}>";
    static final String[] DFA3_transitionS = {
            "\1\1\23\uffff\1\1\1\22\1\1\2\uffff\1\1\10\uffff\1\1\5\uffff"+
            "\12\1\20\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA3_eot = DFA.unpackEncodedString(DFA3_eotS);
    static final short[] DFA3_eof = DFA.unpackEncodedString(DFA3_eofS);
    static final char[] DFA3_min = DFA.unpackEncodedStringToUnsignedChars(DFA3_minS);
    static final char[] DFA3_max = DFA.unpackEncodedStringToUnsignedChars(DFA3_maxS);
    static final short[] DFA3_accept = DFA.unpackEncodedString(DFA3_acceptS);
    static final short[] DFA3_special = DFA.unpackEncodedString(DFA3_specialS);
    static final short[][] DFA3_transition;

    static {
        int numStates = DFA3_transitionS.length;
        DFA3_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA3_transition[i] = DFA.unpackEncodedString(DFA3_transitionS[i]);
        }
    }

    class DFA3 extends DFA {

        public DFA3(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 3;
            this.eot = DFA3_eot;
            this.eof = DFA3_eof;
            this.min = DFA3_min;
            this.max = DFA3_max;
            this.accept = DFA3_accept;
            this.special = DFA3_special;
            this.transition = DFA3_transition;
        }
        public String getDescription() {
            return "()* loopback of 267:9: ( importDeclaration )*";
        }
    }
    static final String DFA4_eotS =
        "\22\uffff";
    static final String DFA4_eofS =
        "\1\1\21\uffff";
    static final String DFA4_minS =
        "\1\5\21\uffff";
    static final String DFA4_maxS =
        "\1\107\21\uffff";
    static final String DFA4_acceptS =
        "\1\uffff\1\2\1\1\17\uffff";
    static final String DFA4_specialS =
        "\22\uffff}>";
    static final String[] DFA4_transitionS = {
            "\1\2\23\uffff\1\2\1\uffff\1\2\2\uffff\1\2\10\uffff\1\2\5\uffff"+
            "\12\2\20\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA4_eot = DFA.unpackEncodedString(DFA4_eotS);
    static final short[] DFA4_eof = DFA.unpackEncodedString(DFA4_eofS);
    static final char[] DFA4_min = DFA.unpackEncodedStringToUnsignedChars(DFA4_minS);
    static final char[] DFA4_max = DFA.unpackEncodedStringToUnsignedChars(DFA4_maxS);
    static final short[] DFA4_accept = DFA.unpackEncodedString(DFA4_acceptS);
    static final short[] DFA4_special = DFA.unpackEncodedString(DFA4_specialS);
    static final short[][] DFA4_transition;

    static {
        int numStates = DFA4_transitionS.length;
        DFA4_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA4_transition[i] = DFA.unpackEncodedString(DFA4_transitionS[i]);
        }
    }

    class DFA4 extends DFA {

        public DFA4(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 4;
            this.eot = DFA4_eot;
            this.eof = DFA4_eof;
            this.min = DFA4_min;
            this.max = DFA4_max;
            this.accept = DFA4_accept;
            this.special = DFA4_special;
            this.transition = DFA4_transition;
        }
        public String getDescription() {
            return "()* loopback of 268:9: ( typeDeclaration )*";
        }
    }
    static final String DFA8_eotS =
        "\21\uffff";
    static final String DFA8_eofS =
        "\21\uffff";
    static final String DFA8_minS =
        "\1\5\20\uffff";
    static final String DFA8_maxS =
        "\1\107\20\uffff";
    static final String DFA8_acceptS =
        "\1\uffff\1\1\16\uffff\1\2";
    static final String DFA8_specialS =
        "\21\uffff}>";
    static final String[] DFA8_transitionS = {
            "\1\1\23\uffff\1\20\1\uffff\1\1\2\uffff\1\1\10\uffff\1\1\5\uffff"+
            "\12\1\20\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
    static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
    static final char[] DFA8_min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
    static final char[] DFA8_max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
    static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
    static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
    static final short[][] DFA8_transition;

    static {
        int numStates = DFA8_transitionS.length;
        DFA8_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
        }
    }

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA8_eot;
            this.eof = DFA8_eof;
            this.min = DFA8_min;
            this.max = DFA8_max;
            this.accept = DFA8_accept;
            this.special = DFA8_special;
            this.transition = DFA8_transition;
        }
        public String getDescription() {
            return "279:1: typeDeclaration : ( classOrInterfaceDeclaration | ';' );";
        }
    }
    static final String DFA9_eotS =
        "\22\uffff";
    static final String DFA9_eofS =
        "\22\uffff";
    static final String DFA9_minS =
        "\1\5\3\uffff\1\4\15\uffff";
    static final String DFA9_maxS =
        "\1\107\3\uffff\1\47\15\uffff";
    static final String DFA9_acceptS =
        "\1\uffff\1\2\3\uffff\1\1\14\uffff";
    static final String DFA9_specialS =
        "\22\uffff}>";
    static final String[] DFA9_transitionS = {
            "\1\1\25\uffff\1\5\2\uffff\1\1\10\uffff\1\1\5\uffff\12\5\20\uffff"+
            "\1\4",
            "",
            "",
            "",
            "\1\5\42\uffff\1\1",
            "",
            "",
            "",
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

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }
        public String getDescription() {
            return "()* loopback of 285:7: ( modifier )*";
        }
    }
    static final String DFA26_eotS =
        "\30\uffff";
    static final String DFA26_eofS =
        "\1\1\27\uffff";
    static final String DFA26_minS =
        "\1\4\27\uffff";
    static final String DFA26_maxS =
        "\1\107\27\uffff";
    static final String DFA26_acceptS =
        "\1\uffff\1\2\1\uffff\1\1\24\uffff";
    static final String DFA26_specialS =
        "\30\uffff}>";
    static final String[] DFA26_transitionS = {
            "\2\3\23\uffff\1\3\1\uffff\1\3\2\uffff\1\3\2\uffff\1\3\3\uffff"+
            "\1\3\1\1\2\3\4\uffff\22\3\10\uffff\1\3",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA26_eot = DFA.unpackEncodedString(DFA26_eotS);
    static final short[] DFA26_eof = DFA.unpackEncodedString(DFA26_eofS);
    static final char[] DFA26_min = DFA.unpackEncodedStringToUnsignedChars(DFA26_minS);
    static final char[] DFA26_max = DFA.unpackEncodedStringToUnsignedChars(DFA26_maxS);
    static final short[] DFA26_accept = DFA.unpackEncodedString(DFA26_acceptS);
    static final short[] DFA26_special = DFA.unpackEncodedString(DFA26_specialS);
    static final short[][] DFA26_transition;

    static {
        int numStates = DFA26_transitionS.length;
        DFA26_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA26_transition[i] = DFA.unpackEncodedString(DFA26_transitionS[i]);
        }
    }

    class DFA26 extends DFA {

        public DFA26(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 26;
            this.eot = DFA26_eot;
            this.eof = DFA26_eof;
            this.min = DFA26_min;
            this.max = DFA26_max;
            this.accept = DFA26_accept;
            this.special = DFA26_special;
            this.transition = DFA26_transition;
        }
        public String getDescription() {
            return "()* loopback of 329:11: ( classBodyDeclaration )*";
        }
    }
    static final String DFA31_eotS =
        "\27\uffff";
    static final String DFA31_eofS =
        "\27\uffff";
    static final String DFA31_minS =
        "\1\4\26\uffff";
    static final String DFA31_maxS =
        "\1\107\26\uffff";
    static final String DFA31_acceptS =
        "\1\uffff\1\2\1\1\24\uffff";
    static final String DFA31_specialS =
        "\27\uffff}>";
    static final String[] DFA31_transitionS = {
            "\2\2\23\uffff\1\2\1\uffff\1\2\2\uffff\1\2\2\uffff\1\2\3\uffff"+
            "\1\2\1\1\2\2\4\uffff\22\2\10\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA31_eot = DFA.unpackEncodedString(DFA31_eotS);
    static final short[] DFA31_eof = DFA.unpackEncodedString(DFA31_eofS);
    static final char[] DFA31_min = DFA.unpackEncodedStringToUnsignedChars(DFA31_minS);
    static final char[] DFA31_max = DFA.unpackEncodedStringToUnsignedChars(DFA31_maxS);
    static final short[] DFA31_accept = DFA.unpackEncodedString(DFA31_acceptS);
    static final short[] DFA31_special = DFA.unpackEncodedString(DFA31_specialS);
    static final short[][] DFA31_transition;

    static {
        int numStates = DFA31_transitionS.length;
        DFA31_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA31_transition[i] = DFA.unpackEncodedString(DFA31_transitionS[i]);
        }
    }

    class DFA31 extends DFA {

        public DFA31(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 31;
            this.eot = DFA31_eot;
            this.eof = DFA31_eof;
            this.min = DFA31_min;
            this.max = DFA31_max;
            this.accept = DFA31_accept;
            this.special = DFA31_special;
            this.transition = DFA31_transition;
        }
        public String getDescription() {
            return "()* loopback of 346:11: ( classBodyDeclaration )*";
        }
    }
    static final String DFA32_eotS =
        "\26\uffff";
    static final String DFA32_eofS =
        "\26\uffff";
    static final String DFA32_minS =
        "\1\4\25\uffff";
    static final String DFA32_maxS =
        "\1\107\25\uffff";
    static final String DFA32_acceptS =
        "\1\uffff\1\2\1\1\23\uffff";
    static final String DFA32_specialS =
        "\26\uffff}>";
    static final String[] DFA32_transitionS = {
            "\2\2\23\uffff\1\2\1\uffff\1\2\2\uffff\1\2\2\uffff\1\2\4\uffff"+
            "\1\1\2\2\4\uffff\22\2\10\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA32_eot = DFA.unpackEncodedString(DFA32_eotS);
    static final short[] DFA32_eof = DFA.unpackEncodedString(DFA32_eofS);
    static final char[] DFA32_min = DFA.unpackEncodedStringToUnsignedChars(DFA32_minS);
    static final char[] DFA32_max = DFA.unpackEncodedStringToUnsignedChars(DFA32_maxS);
    static final short[] DFA32_accept = DFA.unpackEncodedString(DFA32_acceptS);
    static final short[] DFA32_special = DFA.unpackEncodedString(DFA32_specialS);
    static final short[][] DFA32_transition;

    static {
        int numStates = DFA32_transitionS.length;
        DFA32_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA32_transition[i] = DFA.unpackEncodedString(DFA32_transitionS[i]);
        }
    }

    class DFA32 extends DFA {

        public DFA32(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 32;
            this.eot = DFA32_eot;
            this.eof = DFA32_eof;
            this.min = DFA32_min;
            this.max = DFA32_max;
            this.accept = DFA32_accept;
            this.special = DFA32_special;
            this.transition = DFA32_transition;
        }
        public String getDescription() {
            return "()* loopback of 350:11: ( interfaceBodyDeclaration )*";
        }
    }
    static final String DFA35_eotS =
        "\52\uffff";
    static final String DFA35_eofS =
        "\52\uffff";
    static final String DFA35_minS =
        "\1\4\1\uffff\1\4\47\uffff";
    static final String DFA35_maxS =
        "\1\107\1\uffff\1\107\47\uffff";
    static final String DFA35_acceptS =
        "\1\uffff\1\1\1\uffff\1\2\1\3\45\uffff";
    static final String DFA35_specialS =
        "\52\uffff}>";
    static final String[] DFA35_transitionS = {
            "\2\4\23\uffff\1\1\1\uffff\1\2\2\uffff\1\4\2\uffff\1\4\3\uffff"+
            "\1\3\1\uffff\2\4\4\uffff\22\4\10\uffff\1\4",
            "",
            "\2\4\25\uffff\1\4\2\uffff\1\4\2\uffff\1\4\3\uffff\1\3\1\uffff"+
            "\2\4\4\uffff\22\4\10\uffff\1\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA35_eot = DFA.unpackEncodedString(DFA35_eotS);
    static final short[] DFA35_eof = DFA.unpackEncodedString(DFA35_eofS);
    static final char[] DFA35_min = DFA.unpackEncodedStringToUnsignedChars(DFA35_minS);
    static final char[] DFA35_max = DFA.unpackEncodedStringToUnsignedChars(DFA35_maxS);
    static final short[] DFA35_accept = DFA.unpackEncodedString(DFA35_acceptS);
    static final short[] DFA35_special = DFA.unpackEncodedString(DFA35_specialS);
    static final short[][] DFA35_transition;

    static {
        int numStates = DFA35_transitionS.length;
        DFA35_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA35_transition[i] = DFA.unpackEncodedString(DFA35_transitionS[i]);
        }
    }

    class DFA35 extends DFA {

        public DFA35(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 35;
            this.eot = DFA35_eot;
            this.eof = DFA35_eof;
            this.min = DFA35_min;
            this.max = DFA35_max;
            this.accept = DFA35_accept;
            this.special = DFA35_special;
            this.transition = DFA35_transition;
        }
        public String getDescription() {
            return "353:1: classBodyDeclaration : ( ';' | ( 'static' )? block | ( modifier )* memberDecl );";
        }
    }
    static final String DFA34_eotS =
        "\26\uffff";
    static final String DFA34_eofS =
        "\26\uffff";
    static final String DFA34_minS =
        "\1\4\5\uffff\1\4\17\uffff";
    static final String DFA34_maxS =
        "\1\107\5\uffff\1\47\17\uffff";
    static final String DFA34_acceptS =
        "\1\uffff\1\2\7\uffff\1\1\14\uffff";
    static final String DFA34_specialS =
        "\26\uffff}>";
    static final String[] DFA34_transitionS = {
            "\2\1\25\uffff\1\11\2\uffff\1\1\2\uffff\1\1\5\uffff\2\1\4\uffff"+
            "\12\11\10\1\10\uffff\1\6",
            "",
            "",
            "",
            "",
            "",
            "\1\11\42\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA34_eot = DFA.unpackEncodedString(DFA34_eotS);
    static final short[] DFA34_eof = DFA.unpackEncodedString(DFA34_eofS);
    static final char[] DFA34_min = DFA.unpackEncodedStringToUnsignedChars(DFA34_minS);
    static final char[] DFA34_max = DFA.unpackEncodedStringToUnsignedChars(DFA34_maxS);
    static final short[] DFA34_accept = DFA.unpackEncodedString(DFA34_acceptS);
    static final short[] DFA34_special = DFA.unpackEncodedString(DFA34_specialS);
    static final short[][] DFA34_transition;

    static {
        int numStates = DFA34_transitionS.length;
        DFA34_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA34_transition[i] = DFA.unpackEncodedString(DFA34_transitionS[i]);
        }
    }

    class DFA34 extends DFA {

        public DFA34(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 34;
            this.eot = DFA34_eot;
            this.eof = DFA34_eof;
            this.min = DFA34_min;
            this.max = DFA34_max;
            this.accept = DFA34_accept;
            this.special = DFA34_special;
            this.transition = DFA34_transition;
        }
        public String getDescription() {
            return "()* loopback of 356:7: ( modifier )*";
        }
    }
    static final String DFA36_eotS =
        "\22\uffff";
    static final String DFA36_eofS =
        "\22\uffff";
    static final String DFA36_minS =
        "\1\4\1\uffff\2\4\5\uffff\4\0\1\uffff\2\0\2\uffff";
    static final String DFA36_maxS =
        "\1\107\1\uffff\1\101\1\51\5\uffff\4\0\1\uffff\2\0\2\uffff";
    static final String DFA36_acceptS =
        "\1\uffff\1\1\2\uffff\1\4\1\6\1\uffff\1\7\5\uffff\1\5\2\uffff\1\2"+
        "\1\3";
    static final String DFA36_specialS =
        "\11\uffff\1\0\1\1\1\2\1\3\1\uffff\1\4\1\5\2\uffff}>";
    static final String[] DFA36_transitionS = {
            "\1\2\1\7\30\uffff\1\7\2\uffff\1\1\5\uffff\1\5\1\4\16\uffff\10"+
            "\3\10\uffff\1\5",
            "",
            "\1\14\27\uffff\1\12\4\uffff\1\11\7\uffff\1\13\27\uffff\1\15",
            "\1\17\44\uffff\1\16",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA36_eot = DFA.unpackEncodedString(DFA36_eotS);
    static final short[] DFA36_eof = DFA.unpackEncodedString(DFA36_eofS);
    static final char[] DFA36_min = DFA.unpackEncodedStringToUnsignedChars(DFA36_minS);
    static final char[] DFA36_max = DFA.unpackEncodedStringToUnsignedChars(DFA36_maxS);
    static final short[] DFA36_accept = DFA.unpackEncodedString(DFA36_acceptS);
    static final short[] DFA36_special = DFA.unpackEncodedString(DFA36_specialS);
    static final short[][] DFA36_transition;

    static {
        int numStates = DFA36_transitionS.length;
        DFA36_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA36_transition[i] = DFA.unpackEncodedString(DFA36_transitionS[i]);
        }
    }

    class DFA36 extends DFA {

        public DFA36(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 36;
            this.eot = DFA36_eot;
            this.eof = DFA36_eof;
            this.min = DFA36_min;
            this.max = DFA36_max;
            this.accept = DFA36_accept;
            this.special = DFA36_special;
            this.transition = DFA36_transition;
        }
        public String getDescription() {
            return "359:1: memberDecl : ( genericMethodOrConstructorDecl | methodDeclaration | fieldDeclaration | 'void' Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA36_9 = input.LA(1);

                         
                        int index36_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred38_Java()) ) {s = 16;}

                        else if ( (synpred39_Java()) ) {s = 17;}

                         
                        input.seek(index36_9);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA36_10 = input.LA(1);

                         
                        int index36_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred38_Java()) ) {s = 16;}

                        else if ( (synpred39_Java()) ) {s = 17;}

                         
                        input.seek(index36_10);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA36_11 = input.LA(1);

                         
                        int index36_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred38_Java()) ) {s = 16;}

                        else if ( (synpred39_Java()) ) {s = 17;}

                         
                        input.seek(index36_11);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA36_12 = input.LA(1);

                         
                        int index36_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred38_Java()) ) {s = 16;}

                        else if ( (synpred39_Java()) ) {s = 17;}

                         
                        input.seek(index36_12);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA36_14 = input.LA(1);

                         
                        int index36_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred38_Java()) ) {s = 16;}

                        else if ( (synpred39_Java()) ) {s = 17;}

                         
                        input.seek(index36_14);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA36_15 = input.LA(1);

                         
                        int index36_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred38_Java()) ) {s = 16;}

                        else if ( (synpred39_Java()) ) {s = 17;}

                         
                        input.seek(index36_15);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 36, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA40_eotS =
        "\25\uffff";
    static final String DFA40_eofS =
        "\25\uffff";
    static final String DFA40_minS =
        "\1\4\24\uffff";
    static final String DFA40_maxS =
        "\1\107\24\uffff";
    static final String DFA40_acceptS =
        "\1\uffff\1\1\22\uffff\1\2";
    static final String DFA40_specialS =
        "\25\uffff}>";
    static final String[] DFA40_transitionS = {
            "\2\1\23\uffff\1\24\1\uffff\1\1\2\uffff\1\1\2\uffff\1\1\5\uffff"+
            "\2\1\4\uffff\22\1\10\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA40_eot = DFA.unpackEncodedString(DFA40_eotS);
    static final short[] DFA40_eof = DFA.unpackEncodedString(DFA40_eofS);
    static final char[] DFA40_min = DFA.unpackEncodedStringToUnsignedChars(DFA40_minS);
    static final char[] DFA40_max = DFA.unpackEncodedStringToUnsignedChars(DFA40_maxS);
    static final short[] DFA40_accept = DFA.unpackEncodedString(DFA40_acceptS);
    static final short[] DFA40_special = DFA.unpackEncodedString(DFA40_specialS);
    static final short[][] DFA40_transition;

    static {
        int numStates = DFA40_transitionS.length;
        DFA40_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA40_transition[i] = DFA.unpackEncodedString(DFA40_transitionS[i]);
        }
    }

    class DFA40 extends DFA {

        public DFA40(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 40;
            this.eot = DFA40_eot;
            this.eof = DFA40_eof;
            this.min = DFA40_min;
            this.max = DFA40_max;
            this.accept = DFA40_accept;
            this.special = DFA40_special;
            this.transition = DFA40_transition;
        }
        public String getDescription() {
            return "387:1: interfaceBodyDeclaration : ( ( modifier )* interfaceMemberDecl | ';' );";
        }
    }
    static final String DFA39_eotS =
        "\26\uffff";
    static final String DFA39_eofS =
        "\26\uffff";
    static final String DFA39_minS =
        "\1\4\5\uffff\1\4\17\uffff";
    static final String DFA39_maxS =
        "\1\107\5\uffff\1\47\17\uffff";
    static final String DFA39_acceptS =
        "\1\uffff\1\2\7\uffff\1\1\14\uffff";
    static final String DFA39_specialS =
        "\26\uffff}>";
    static final String[] DFA39_transitionS = {
            "\2\1\25\uffff\1\11\2\uffff\1\1\2\uffff\1\1\5\uffff\2\1\4\uffff"+
            "\12\11\10\1\10\uffff\1\6",
            "",
            "",
            "",
            "",
            "",
            "\1\11\42\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA39_eot = DFA.unpackEncodedString(DFA39_eotS);
    static final short[] DFA39_eof = DFA.unpackEncodedString(DFA39_eofS);
    static final char[] DFA39_min = DFA.unpackEncodedStringToUnsignedChars(DFA39_minS);
    static final char[] DFA39_max = DFA.unpackEncodedStringToUnsignedChars(DFA39_maxS);
    static final short[] DFA39_accept = DFA.unpackEncodedString(DFA39_acceptS);
    static final short[] DFA39_special = DFA.unpackEncodedString(DFA39_specialS);
    static final short[][] DFA39_transition;

    static {
        int numStates = DFA39_transitionS.length;
        DFA39_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA39_transition[i] = DFA.unpackEncodedString(DFA39_transitionS[i]);
        }
    }

    class DFA39 extends DFA {

        public DFA39(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 39;
            this.eot = DFA39_eot;
            this.eof = DFA39_eof;
            this.min = DFA39_min;
            this.max = DFA39_max;
            this.accept = DFA39_accept;
            this.special = DFA39_special;
            this.transition = DFA39_transition;
        }
        public String getDescription() {
            return "()* loopback of 388:7: ( modifier )*";
        }
    }
    static final String DFA60_eotS =
        "\31\uffff";
    static final String DFA60_eofS =
        "\31\uffff";
    static final String DFA60_minS =
        "\1\4\30\uffff";
    static final String DFA60_maxS =
        "\1\167\30\uffff";
    static final String DFA60_acceptS =
        "\1\uffff\1\1\1\2\26\uffff";
    static final String DFA60_specialS =
        "\31\uffff}>";
    static final String[] DFA60_transitionS = {
            "\1\2\1\uffff\6\2\25\uffff\1\2\3\uffff\1\1\2\uffff\1\2\16\uffff"+
            "\10\2\1\uffff\2\2\2\uffff\3\2\24\uffff\3\2\20\uffff\2\2\2\uffff"+
            "\6\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA60_eot = DFA.unpackEncodedString(DFA60_eotS);
    static final short[] DFA60_eof = DFA.unpackEncodedString(DFA60_eofS);
    static final char[] DFA60_min = DFA.unpackEncodedStringToUnsignedChars(DFA60_minS);
    static final char[] DFA60_max = DFA.unpackEncodedStringToUnsignedChars(DFA60_maxS);
    static final short[] DFA60_accept = DFA.unpackEncodedString(DFA60_acceptS);
    static final short[] DFA60_special = DFA.unpackEncodedString(DFA60_specialS);
    static final short[][] DFA60_transition;

    static {
        int numStates = DFA60_transitionS.length;
        DFA60_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA60_transition[i] = DFA.unpackEncodedString(DFA60_transitionS[i]);
        }
    }

    class DFA60 extends DFA {

        public DFA60(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 60;
            this.eot = DFA60_eot;
            this.eof = DFA60_eof;
            this.min = DFA60_min;
            this.max = DFA60_max;
            this.accept = DFA60_accept;
            this.special = DFA60_special;
            this.transition = DFA60_transition;
        }
        public String getDescription() {
            return "489:1: variableInitializer : ( arrayInitializer | expression );";
        }
    }
    static final String DFA63_eotS =
        "\32\uffff";
    static final String DFA63_eofS =
        "\32\uffff";
    static final String DFA63_minS =
        "\1\4\31\uffff";
    static final String DFA63_maxS =
        "\1\167\31\uffff";
    static final String DFA63_acceptS =
        "\1\uffff\1\1\27\uffff\1\2";
    static final String DFA63_specialS =
        "\32\uffff}>";
    static final String[] DFA63_transitionS = {
            "\1\1\1\uffff\6\1\25\uffff\1\1\3\uffff\1\1\1\31\1\uffff\1\1\16"+
            "\uffff\10\1\1\uffff\2\1\2\uffff\3\1\24\uffff\3\1\20\uffff\2"+
            "\1\2\uffff\6\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA63_eot = DFA.unpackEncodedString(DFA63_eotS);
    static final short[] DFA63_eof = DFA.unpackEncodedString(DFA63_eofS);
    static final char[] DFA63_min = DFA.unpackEncodedStringToUnsignedChars(DFA63_minS);
    static final char[] DFA63_max = DFA.unpackEncodedStringToUnsignedChars(DFA63_maxS);
    static final short[] DFA63_accept = DFA.unpackEncodedString(DFA63_acceptS);
    static final short[] DFA63_special = DFA.unpackEncodedString(DFA63_specialS);
    static final short[][] DFA63_transition;

    static {
        int numStates = DFA63_transitionS.length;
        DFA63_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA63_transition[i] = DFA.unpackEncodedString(DFA63_transitionS[i]);
        }
    }

    class DFA63 extends DFA {

        public DFA63(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 63;
            this.eot = DFA63_eot;
            this.eof = DFA63_eof;
            this.min = DFA63_min;
            this.max = DFA63_max;
            this.accept = DFA63_accept;
            this.special = DFA63_special;
            this.transition = DFA63_transition;
        }
        public String getDescription() {
            return "495:11: ( variableInitializer ( ',' variableInitializer )* ( ',' )? )?";
        }
    }
    static final String DFA61_eotS =
        "\34\uffff";
    static final String DFA61_eofS =
        "\34\uffff";
    static final String DFA61_minS =
        "\1\42\1\4\32\uffff";
    static final String DFA61_maxS =
        "\1\46\1\167\32\uffff";
    static final String DFA61_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\27\uffff";
    static final String DFA61_specialS =
        "\34\uffff}>";
    static final String[] DFA61_transitionS = {
            "\1\1\3\uffff\1\2",
            "\1\4\1\uffff\6\4\25\uffff\1\4\3\uffff\1\4\1\2\1\uffff\1\4\16"+
            "\uffff\10\4\1\uffff\2\4\2\uffff\3\4\24\uffff\3\4\20\uffff\2"+
            "\4\2\uffff\6\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA61_eot = DFA.unpackEncodedString(DFA61_eotS);
    static final short[] DFA61_eof = DFA.unpackEncodedString(DFA61_eofS);
    static final char[] DFA61_min = DFA.unpackEncodedStringToUnsignedChars(DFA61_minS);
    static final char[] DFA61_max = DFA.unpackEncodedStringToUnsignedChars(DFA61_maxS);
    static final short[] DFA61_accept = DFA.unpackEncodedString(DFA61_acceptS);
    static final short[] DFA61_special = DFA.unpackEncodedString(DFA61_specialS);
    static final short[][] DFA61_transition;

    static {
        int numStates = DFA61_transitionS.length;
        DFA61_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA61_transition[i] = DFA.unpackEncodedString(DFA61_transitionS[i]);
        }
    }

    class DFA61 extends DFA {

        public DFA61(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 61;
            this.eot = DFA61_eot;
            this.eof = DFA61_eof;
            this.min = DFA61_min;
            this.max = DFA61_max;
            this.accept = DFA61_accept;
            this.special = DFA61_special;
            this.transition = DFA61_transition;
        }
        public String getDescription() {
            return "()* loopback of 495:32: ( ',' variableInitializer )*";
        }
    }
    static final String DFA64_eotS =
        "\15\uffff";
    static final String DFA64_eofS =
        "\15\uffff";
    static final String DFA64_minS =
        "\1\33\14\uffff";
    static final String DFA64_maxS =
        "\1\107\14\uffff";
    static final String DFA64_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14";
    static final String DFA64_specialS =
        "\15\uffff}>";
    static final String[] DFA64_transitionS = {
            "\1\5\21\uffff\1\2\1\3\1\4\1\6\1\7\1\10\1\11\1\12\1\13\1\14\20"+
            "\uffff\1\1",
            "",
            "",
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

    static final short[] DFA64_eot = DFA.unpackEncodedString(DFA64_eotS);
    static final short[] DFA64_eof = DFA.unpackEncodedString(DFA64_eofS);
    static final char[] DFA64_min = DFA.unpackEncodedStringToUnsignedChars(DFA64_minS);
    static final char[] DFA64_max = DFA.unpackEncodedStringToUnsignedChars(DFA64_maxS);
    static final short[] DFA64_accept = DFA.unpackEncodedString(DFA64_acceptS);
    static final short[] DFA64_special = DFA.unpackEncodedString(DFA64_specialS);
    static final short[][] DFA64_transition;

    static {
        int numStates = DFA64_transitionS.length;
        DFA64_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA64_transition[i] = DFA.unpackEncodedString(DFA64_transitionS[i]);
        }
    }

    class DFA64 extends DFA {

        public DFA64(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 64;
            this.eot = DFA64_eot;
            this.eof = DFA64_eof;
            this.min = DFA64_min;
            this.max = DFA64_max;
            this.accept = DFA64_accept;
            this.special = DFA64_special;
            this.transition = DFA64_transition;
        }
        public String getDescription() {
            return "498:1: modifier : ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' );";
        }
    }
    static final String DFA67_eotS =
        "\44\uffff";
    static final String DFA67_eofS =
        "\1\2\43\uffff";
    static final String DFA67_minS =
        "\2\4\42\uffff";
    static final String DFA67_maxS =
        "\1\154\1\77\42\uffff";
    static final String DFA67_acceptS =
        "\2\uffff\1\2\36\uffff\1\1\2\uffff";
    static final String DFA67_specialS =
        "\44\uffff}>";
    static final String[] DFA67_transitionS = {
            "\1\2\24\uffff\1\2\2\uffff\1\2\3\uffff\1\2\1\1\5\2\2\uffff\2"+
            "\2\1\uffff\1\2\22\uffff\1\2\2\uffff\2\2\6\uffff\1\2\24\uffff"+
            "\16\2",
            "\1\41\34\uffff\1\2\25\uffff\11\41",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA67_eot = DFA.unpackEncodedString(DFA67_eotS);
    static final short[] DFA67_eof = DFA.unpackEncodedString(DFA67_eofS);
    static final char[] DFA67_min = DFA.unpackEncodedStringToUnsignedChars(DFA67_minS);
    static final char[] DFA67_max = DFA.unpackEncodedStringToUnsignedChars(DFA67_maxS);
    static final short[] DFA67_accept = DFA.unpackEncodedString(DFA67_acceptS);
    static final short[] DFA67_special = DFA.unpackEncodedString(DFA67_specialS);
    static final short[][] DFA67_transition;

    static {
        int numStates = DFA67_transitionS.length;
        DFA67_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA67_transition[i] = DFA.unpackEncodedString(DFA67_transitionS[i]);
        }
    }

    class DFA67 extends DFA {

        public DFA67(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 67;
            this.eot = DFA67_eot;
            this.eof = DFA67_eof;
            this.min = DFA67_min;
            this.max = DFA67_max;
            this.accept = DFA67_accept;
            this.special = DFA67_special;
            this.transition = DFA67_transition;
        }
        public String getDescription() {
            return "527:18: ( typeArguments )?";
        }
    }
    static final String DFA69_eotS =
        "\40\uffff";
    static final String DFA69_eofS =
        "\1\1\37\uffff";
    static final String DFA69_minS =
        "\1\4\37\uffff";
    static final String DFA69_maxS =
        "\1\154\37\uffff";
    static final String DFA69_acceptS =
        "\1\uffff\1\2\35\uffff\1\1";
    static final String DFA69_specialS =
        "\40\uffff}>";
    static final String[] DFA69_transitionS = {
            "\1\1\24\uffff\1\1\2\uffff\1\37\3\uffff\7\1\2\uffff\2\1\1\uffff"+
            "\1\1\22\uffff\1\1\2\uffff\2\1\6\uffff\1\1\24\uffff\16\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA69_eot = DFA.unpackEncodedString(DFA69_eotS);
    static final short[] DFA69_eof = DFA.unpackEncodedString(DFA69_eofS);
    static final char[] DFA69_min = DFA.unpackEncodedStringToUnsignedChars(DFA69_minS);
    static final char[] DFA69_max = DFA.unpackEncodedStringToUnsignedChars(DFA69_maxS);
    static final short[] DFA69_accept = DFA.unpackEncodedString(DFA69_acceptS);
    static final short[] DFA69_special = DFA.unpackEncodedString(DFA69_specialS);
    static final short[][] DFA69_transition;

    static {
        int numStates = DFA69_transitionS.length;
        DFA69_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA69_transition[i] = DFA.unpackEncodedString(DFA69_transitionS[i]);
        }
    }

    class DFA69 extends DFA {

        public DFA69(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 69;
            this.eot = DFA69_eot;
            this.eof = DFA69_eof;
            this.min = DFA69_min;
            this.max = DFA69_max;
            this.accept = DFA69_accept;
            this.special = DFA69_special;
            this.transition = DFA69_transition;
        }
        public String getDescription() {
            return "()* loopback of 527:35: ( '.' Identifier ( typeArguments )? )*";
        }
    }
    static final String DFA68_eotS =
        "\44\uffff";
    static final String DFA68_eofS =
        "\1\2\43\uffff";
    static final String DFA68_minS =
        "\2\4\42\uffff";
    static final String DFA68_maxS =
        "\1\154\1\77\42\uffff";
    static final String DFA68_acceptS =
        "\2\uffff\1\2\36\uffff\1\1\2\uffff";
    static final String DFA68_specialS =
        "\44\uffff}>";
    static final String[] DFA68_transitionS = {
            "\1\2\24\uffff\1\2\2\uffff\1\2\3\uffff\1\2\1\1\5\2\2\uffff\2"+
            "\2\1\uffff\1\2\22\uffff\1\2\2\uffff\2\2\6\uffff\1\2\24\uffff"+
            "\16\2",
            "\1\41\34\uffff\1\2\25\uffff\11\41",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA68_eot = DFA.unpackEncodedString(DFA68_eotS);
    static final short[] DFA68_eof = DFA.unpackEncodedString(DFA68_eofS);
    static final char[] DFA68_min = DFA.unpackEncodedStringToUnsignedChars(DFA68_minS);
    static final char[] DFA68_max = DFA.unpackEncodedStringToUnsignedChars(DFA68_maxS);
    static final short[] DFA68_accept = DFA.unpackEncodedString(DFA68_acceptS);
    static final short[] DFA68_special = DFA.unpackEncodedString(DFA68_specialS);
    static final short[][] DFA68_transition;

    static {
        int numStates = DFA68_transitionS.length;
        DFA68_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA68_transition[i] = DFA.unpackEncodedString(DFA68_transitionS[i]);
        }
    }

    class DFA68 extends DFA {

        public DFA68(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 68;
            this.eot = DFA68_eot;
            this.eof = DFA68_eof;
            this.min = DFA68_min;
            this.max = DFA68_max;
            this.accept = DFA68_accept;
            this.special = DFA68_special;
            this.transition = DFA68_transition;
        }
        public String getDescription() {
            return "527:51: ( typeArguments )?";
        }
    }
    static final String DFA70_eotS =
        "\37\uffff";
    static final String DFA70_eofS =
        "\1\1\36\uffff";
    static final String DFA70_minS =
        "\1\4\36\uffff";
    static final String DFA70_maxS =
        "\1\154\36\uffff";
    static final String DFA70_acceptS =
        "\1\uffff\1\2\34\uffff\1\1";
    static final String DFA70_specialS =
        "\37\uffff}>";
    static final String[] DFA70_transitionS = {
            "\1\1\24\uffff\1\1\6\uffff\7\1\2\uffff\1\36\1\1\1\uffff\1\1\22"+
            "\uffff\1\1\2\uffff\2\1\6\uffff\1\1\24\uffff\16\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA70_eot = DFA.unpackEncodedString(DFA70_eotS);
    static final short[] DFA70_eof = DFA.unpackEncodedString(DFA70_eofS);
    static final char[] DFA70_min = DFA.unpackEncodedStringToUnsignedChars(DFA70_minS);
    static final char[] DFA70_max = DFA.unpackEncodedStringToUnsignedChars(DFA70_maxS);
    static final short[] DFA70_accept = DFA.unpackEncodedString(DFA70_acceptS);
    static final short[] DFA70_special = DFA.unpackEncodedString(DFA70_specialS);
    static final short[][] DFA70_transition;

    static {
        int numStates = DFA70_transitionS.length;
        DFA70_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA70_transition[i] = DFA.unpackEncodedString(DFA70_transitionS[i]);
        }
    }

    class DFA70 extends DFA {

        public DFA70(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 70;
            this.eot = DFA70_eot;
            this.eof = DFA70_eof;
            this.min = DFA70_min;
            this.max = DFA70_max;
            this.accept = DFA70_accept;
            this.special = DFA70_special;
            this.transition = DFA70_transition;
        }
        public String getDescription() {
            return "()* loopback of 527:71: ( '[' ']' )*";
        }
    }
    static final String DFA71_eotS =
        "\37\uffff";
    static final String DFA71_eofS =
        "\1\1\36\uffff";
    static final String DFA71_minS =
        "\1\4\36\uffff";
    static final String DFA71_maxS =
        "\1\154\36\uffff";
    static final String DFA71_acceptS =
        "\1\uffff\1\2\34\uffff\1\1";
    static final String DFA71_specialS =
        "\37\uffff}>";
    static final String[] DFA71_transitionS = {
            "\1\1\24\uffff\1\1\6\uffff\7\1\2\uffff\1\36\1\1\1\uffff\1\1\22"+
            "\uffff\1\1\2\uffff\2\1\6\uffff\1\1\24\uffff\16\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA71_eot = DFA.unpackEncodedString(DFA71_eotS);
    static final short[] DFA71_eof = DFA.unpackEncodedString(DFA71_eofS);
    static final char[] DFA71_min = DFA.unpackEncodedStringToUnsignedChars(DFA71_minS);
    static final char[] DFA71_max = DFA.unpackEncodedStringToUnsignedChars(DFA71_maxS);
    static final short[] DFA71_accept = DFA.unpackEncodedString(DFA71_acceptS);
    static final short[] DFA71_special = DFA.unpackEncodedString(DFA71_specialS);
    static final short[][] DFA71_transition;

    static {
        int numStates = DFA71_transitionS.length;
        DFA71_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA71_transition[i] = DFA.unpackEncodedString(DFA71_transitionS[i]);
        }
    }

    class DFA71 extends DFA {

        public DFA71(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 71;
            this.eot = DFA71_eot;
            this.eof = DFA71_eof;
            this.min = DFA71_min;
            this.max = DFA71_max;
            this.accept = DFA71_accept;
            this.special = DFA71_special;
            this.transition = DFA71_transition;
        }
        public String getDescription() {
            return "()* loopback of 528:21: ( '[' ']' )*";
        }
    }
    static final String DFA85_eotS =
        "\30\uffff";
    static final String DFA85_eofS =
        "\1\1\27\uffff";
    static final String DFA85_minS =
        "\1\4\2\uffff\1\4\22\uffff\1\0\1\uffff";
    static final String DFA85_maxS =
        "\1\107\2\uffff\1\47\22\uffff\1\0\1\uffff";
    static final String DFA85_acceptS =
        "\1\uffff\1\2\25\uffff\1\1";
    static final String DFA85_specialS =
        "\26\uffff\1\0\1\uffff}>";
    static final String[] DFA85_transitionS = {
            "\2\1\22\uffff\4\1\2\uffff\1\1\10\uffff\1\1\5\uffff\12\1\20\uffff"+
            "\1\3",
            "",
            "",
            "\1\26\42\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            ""
    };

    static final short[] DFA85_eot = DFA.unpackEncodedString(DFA85_eotS);
    static final short[] DFA85_eof = DFA.unpackEncodedString(DFA85_eofS);
    static final char[] DFA85_min = DFA.unpackEncodedStringToUnsignedChars(DFA85_minS);
    static final char[] DFA85_max = DFA.unpackEncodedStringToUnsignedChars(DFA85_maxS);
    static final short[] DFA85_accept = DFA.unpackEncodedString(DFA85_acceptS);
    static final short[] DFA85_special = DFA.unpackEncodedString(DFA85_specialS);
    static final short[][] DFA85_transition;

    static {
        int numStates = DFA85_transitionS.length;
        DFA85_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA85_transition[i] = DFA.unpackEncodedString(DFA85_transitionS[i]);
        }
    }

    class DFA85 extends DFA {

        public DFA85(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 85;
            this.eot = DFA85_eot;
            this.eof = DFA85_eof;
            this.min = DFA85_min;
            this.max = DFA85_max;
            this.accept = DFA85_accept;
            this.special = DFA85_special;
            this.transition = DFA85_transition;
        }
        public String getDescription() {
            return "()+ loopback of 604:7: ( annotation )+";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA85_22 = input.LA(1);

                         
                        int index85_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred120_Java()) ) {s = 23;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index85_22);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 85, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA87_eotS =
        "\34\uffff";
    static final String DFA87_eofS =
        "\1\2\33\uffff";
    static final String DFA87_minS =
        "\1\4\33\uffff";
    static final String DFA87_maxS =
        "\1\107\33\uffff";
    static final String DFA87_acceptS =
        "\1\uffff\1\1\1\2\31\uffff";
    static final String DFA87_specialS =
        "\34\uffff}>";
    static final String[] DFA87_transitionS = {
            "\2\2\22\uffff\4\2\2\uffff\1\2\2\uffff\2\2\3\uffff\3\2\4\uffff"+
            "\22\2\2\uffff\1\1\1\2\4\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA87_eot = DFA.unpackEncodedString(DFA87_eotS);
    static final short[] DFA87_eof = DFA.unpackEncodedString(DFA87_eofS);
    static final char[] DFA87_min = DFA.unpackEncodedStringToUnsignedChars(DFA87_minS);
    static final char[] DFA87_max = DFA.unpackEncodedStringToUnsignedChars(DFA87_maxS);
    static final short[] DFA87_accept = DFA.unpackEncodedString(DFA87_acceptS);
    static final short[] DFA87_special = DFA.unpackEncodedString(DFA87_specialS);
    static final short[][] DFA87_transition;

    static {
        int numStates = DFA87_transitionS.length;
        DFA87_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA87_transition[i] = DFA.unpackEncodedString(DFA87_transitionS[i]);
        }
    }

    class DFA87 extends DFA {

        public DFA87(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 87;
            this.eot = DFA87_eot;
            this.eof = DFA87_eof;
            this.min = DFA87_min;
            this.max = DFA87_max;
            this.accept = DFA87_accept;
            this.special = DFA87_special;
            this.transition = DFA87_transition;
        }
        public String getDescription() {
            return "608:26: ( '(' ( elementValuePairs )? ')' )?";
        }
    }
    static final String DFA86_eotS =
        "\33\uffff";
    static final String DFA86_eofS =
        "\33\uffff";
    static final String DFA86_minS =
        "\1\4\32\uffff";
    static final String DFA86_maxS =
        "\1\167\32\uffff";
    static final String DFA86_acceptS =
        "\1\uffff\1\1\30\uffff\1\2";
    static final String DFA86_specialS =
        "\33\uffff}>";
    static final String[] DFA86_transitionS = {
            "\1\1\1\uffff\6\1\25\uffff\1\1\3\uffff\1\1\2\uffff\1\1\16\uffff"+
            "\10\1\1\uffff\2\1\1\32\1\uffff\4\1\23\uffff\3\1\20\uffff\2\1"+
            "\2\uffff\6\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA86_eot = DFA.unpackEncodedString(DFA86_eotS);
    static final short[] DFA86_eof = DFA.unpackEncodedString(DFA86_eofS);
    static final char[] DFA86_min = DFA.unpackEncodedStringToUnsignedChars(DFA86_minS);
    static final char[] DFA86_max = DFA.unpackEncodedStringToUnsignedChars(DFA86_maxS);
    static final short[] DFA86_accept = DFA.unpackEncodedString(DFA86_acceptS);
    static final short[] DFA86_special = DFA.unpackEncodedString(DFA86_specialS);
    static final short[][] DFA86_transition;

    static {
        int numStates = DFA86_transitionS.length;
        DFA86_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA86_transition[i] = DFA.unpackEncodedString(DFA86_transitionS[i]);
        }
    }

    class DFA86 extends DFA {

        public DFA86(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 86;
            this.eot = DFA86_eot;
            this.eof = DFA86_eof;
            this.min = DFA86_min;
            this.max = DFA86_max;
            this.accept = DFA86_accept;
            this.special = DFA86_special;
            this.transition = DFA86_transition;
        }
        public String getDescription() {
            return "608:31: ( elementValuePairs )?";
        }
    }
    static final String DFA88_eotS =
        "\35\uffff";
    static final String DFA88_eofS =
        "\1\1\34\uffff";
    static final String DFA88_minS =
        "\1\4\34\uffff";
    static final String DFA88_maxS =
        "\1\107\34\uffff";
    static final String DFA88_acceptS =
        "\1\uffff\1\2\32\uffff\1\1";
    static final String DFA88_specialS =
        "\35\uffff}>";
    static final String[] DFA88_transitionS = {
            "\2\1\22\uffff\4\1\1\34\1\uffff\1\1\2\uffff\2\1\3\uffff\3\1\4"+
            "\uffff\22\1\2\uffff\2\1\4\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA88_eot = DFA.unpackEncodedString(DFA88_eotS);
    static final short[] DFA88_eof = DFA.unpackEncodedString(DFA88_eofS);
    static final char[] DFA88_min = DFA.unpackEncodedStringToUnsignedChars(DFA88_minS);
    static final char[] DFA88_max = DFA.unpackEncodedStringToUnsignedChars(DFA88_maxS);
    static final short[] DFA88_accept = DFA.unpackEncodedString(DFA88_acceptS);
    static final short[] DFA88_special = DFA.unpackEncodedString(DFA88_specialS);
    static final short[][] DFA88_transition;

    static {
        int numStates = DFA88_transitionS.length;
        DFA88_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA88_transition[i] = DFA.unpackEncodedString(DFA88_transitionS[i]);
        }
    }

    class DFA88 extends DFA {

        public DFA88(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 88;
            this.eot = DFA88_eot;
            this.eof = DFA88_eof;
            this.min = DFA88_min;
            this.max = DFA88_max;
            this.accept = DFA88_accept;
            this.special = DFA88_special;
            this.transition = DFA88_transition;
        }
        public String getDescription() {
            return "()* loopback of 612:18: ( '.' Identifier )*";
        }
    }
    static final String DFA90_eotS =
        "\56\uffff";
    static final String DFA90_eofS =
        "\1\uffff\1\2\54\uffff";
    static final String DFA90_minS =
        "\1\4\1\34\54\uffff";
    static final String DFA90_maxS =
        "\1\167\1\163\54\uffff";
    static final String DFA90_acceptS =
        "\2\uffff\1\2\27\uffff\1\1\23\uffff";
    static final String DFA90_specialS =
        "\56\uffff}>";
    static final String[] DFA90_transitionS = {
            "\1\1\1\uffff\6\2\25\uffff\1\2\3\uffff\1\2\2\uffff\1\2\16\uffff"+
            "\10\2\1\uffff\2\2\2\uffff\4\2\23\uffff\3\2\20\uffff\2\2\2\uffff"+
            "\6\2",
            "\2\2\3\uffff\4\2\4\uffff\1\2\2\uffff\1\32\22\uffff\1\2\1\uffff"+
            "\2\2\44\uffff\15\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA90_eot = DFA.unpackEncodedString(DFA90_eotS);
    static final short[] DFA90_eof = DFA.unpackEncodedString(DFA90_eofS);
    static final char[] DFA90_min = DFA.unpackEncodedStringToUnsignedChars(DFA90_minS);
    static final char[] DFA90_max = DFA.unpackEncodedStringToUnsignedChars(DFA90_maxS);
    static final short[] DFA90_accept = DFA.unpackEncodedString(DFA90_acceptS);
    static final short[] DFA90_special = DFA.unpackEncodedString(DFA90_specialS);
    static final short[][] DFA90_transition;

    static {
        int numStates = DFA90_transitionS.length;
        DFA90_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA90_transition[i] = DFA.unpackEncodedString(DFA90_transitionS[i]);
        }
    }

    class DFA90 extends DFA {

        public DFA90(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 90;
            this.eot = DFA90_eot;
            this.eof = DFA90_eof;
            this.min = DFA90_min;
            this.max = DFA90_max;
            this.accept = DFA90_accept;
            this.special = DFA90_special;
            this.transition = DFA90_transition;
        }
        public String getDescription() {
            return "620:7: ( Identifier '=' )?";
        }
    }
    static final String DFA91_eotS =
        "\32\uffff";
    static final String DFA91_eofS =
        "\32\uffff";
    static final String DFA91_minS =
        "\1\4\31\uffff";
    static final String DFA91_maxS =
        "\1\167\31\uffff";
    static final String DFA91_acceptS =
        "\1\uffff\1\1\26\uffff\1\2\1\3";
    static final String DFA91_specialS =
        "\32\uffff}>";
    static final String[] DFA91_transitionS = {
            "\1\1\1\uffff\6\1\25\uffff\1\1\3\uffff\1\31\2\uffff\1\1\16\uffff"+
            "\10\1\1\uffff\2\1\2\uffff\3\1\1\30\23\uffff\3\1\20\uffff\2\1"+
            "\2\uffff\6\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA91_eot = DFA.unpackEncodedString(DFA91_eotS);
    static final short[] DFA91_eof = DFA.unpackEncodedString(DFA91_eofS);
    static final char[] DFA91_min = DFA.unpackEncodedStringToUnsignedChars(DFA91_minS);
    static final char[] DFA91_max = DFA.unpackEncodedStringToUnsignedChars(DFA91_maxS);
    static final short[] DFA91_accept = DFA.unpackEncodedString(DFA91_acceptS);
    static final short[] DFA91_special = DFA.unpackEncodedString(DFA91_specialS);
    static final short[][] DFA91_transition;

    static {
        int numStates = DFA91_transitionS.length;
        DFA91_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA91_transition[i] = DFA.unpackEncodedString(DFA91_transitionS[i]);
        }
    }

    class DFA91 extends DFA {

        public DFA91(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 91;
            this.eot = DFA91_eot;
            this.eof = DFA91_eof;
            this.min = DFA91_min;
            this.max = DFA91_max;
            this.accept = DFA91_accept;
            this.special = DFA91_special;
            this.transition = DFA91_transition;
        }
        public String getDescription() {
            return "623:1: elementValue : ( conditionalExpression | annotation | elementValueArrayInitializer );";
        }
    }
    static final String DFA93_eotS =
        "\33\uffff";
    static final String DFA93_eofS =
        "\33\uffff";
    static final String DFA93_minS =
        "\1\4\32\uffff";
    static final String DFA93_maxS =
        "\1\167\32\uffff";
    static final String DFA93_acceptS =
        "\1\uffff\1\1\30\uffff\1\2";
    static final String DFA93_specialS =
        "\33\uffff}>";
    static final String[] DFA93_transitionS = {
            "\1\1\1\uffff\6\1\25\uffff\1\1\3\uffff\1\1\1\32\1\uffff\1\1\16"+
            "\uffff\10\1\1\uffff\2\1\2\uffff\4\1\23\uffff\3\1\20\uffff\2"+
            "\1\2\uffff\6\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA93_eot = DFA.unpackEncodedString(DFA93_eotS);
    static final short[] DFA93_eof = DFA.unpackEncodedString(DFA93_eofS);
    static final char[] DFA93_min = DFA.unpackEncodedStringToUnsignedChars(DFA93_minS);
    static final char[] DFA93_max = DFA.unpackEncodedStringToUnsignedChars(DFA93_maxS);
    static final short[] DFA93_accept = DFA.unpackEncodedString(DFA93_acceptS);
    static final short[] DFA93_special = DFA.unpackEncodedString(DFA93_specialS);
    static final short[][] DFA93_transition;

    static {
        int numStates = DFA93_transitionS.length;
        DFA93_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA93_transition[i] = DFA.unpackEncodedString(DFA93_transitionS[i]);
        }
    }

    class DFA93 extends DFA {

        public DFA93(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 93;
            this.eot = DFA93_eot;
            this.eof = DFA93_eof;
            this.min = DFA93_min;
            this.max = DFA93_max;
            this.accept = DFA93_accept;
            this.special = DFA93_special;
            this.transition = DFA93_transition;
        }
        public String getDescription() {
            return "630:11: ( elementValue ( ',' elementValue )* )?";
        }
    }
    static final String DFA94_eotS =
        "\23\uffff";
    static final String DFA94_eofS =
        "\23\uffff";
    static final String DFA94_minS =
        "\1\4\22\uffff";
    static final String DFA94_maxS =
        "\1\107\22\uffff";
    static final String DFA94_acceptS =
        "\1\uffff\1\1\20\uffff\1\2";
    static final String DFA94_specialS =
        "\23\uffff}>";
    static final String[] DFA94_transitionS = {
            "\2\1\25\uffff\1\1\2\uffff\1\1\7\uffff\1\22\1\1\5\uffff\22\1"+
            "\10\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA94_eot = DFA.unpackEncodedString(DFA94_eotS);
    static final short[] DFA94_eof = DFA.unpackEncodedString(DFA94_eofS);
    static final char[] DFA94_min = DFA.unpackEncodedStringToUnsignedChars(DFA94_minS);
    static final char[] DFA94_max = DFA.unpackEncodedStringToUnsignedChars(DFA94_maxS);
    static final short[] DFA94_accept = DFA.unpackEncodedString(DFA94_acceptS);
    static final short[] DFA94_special = DFA.unpackEncodedString(DFA94_specialS);
    static final short[][] DFA94_transition;

    static {
        int numStates = DFA94_transitionS.length;
        DFA94_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA94_transition[i] = DFA.unpackEncodedString(DFA94_transitionS[i]);
        }
    }

    class DFA94 extends DFA {

        public DFA94(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 94;
            this.eot = DFA94_eot;
            this.eof = DFA94_eof;
            this.min = DFA94_min;
            this.max = DFA94_max;
            this.accept = DFA94_accept;
            this.special = DFA94_special;
            this.transition = DFA94_transition;
        }
        public String getDescription() {
            return "638:11: ( annotationTypeElementDeclarations )?";
        }
    }
    static final String DFA95_eotS =
        "\24\uffff";
    static final String DFA95_eofS =
        "\1\1\23\uffff";
    static final String DFA95_minS =
        "\1\4\23\uffff";
    static final String DFA95_maxS =
        "\1\107\23\uffff";
    static final String DFA95_acceptS =
        "\1\uffff\1\2\1\uffff\1\1\20\uffff";
    static final String DFA95_specialS =
        "\24\uffff}>";
    static final String[] DFA95_transitionS = {
            "\2\3\25\uffff\1\3\2\uffff\1\3\7\uffff\1\1\1\3\5\uffff\22\3\10"+
            "\uffff\1\3",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA95_eot = DFA.unpackEncodedString(DFA95_eotS);
    static final short[] DFA95_eof = DFA.unpackEncodedString(DFA95_eofS);
    static final char[] DFA95_min = DFA.unpackEncodedStringToUnsignedChars(DFA95_minS);
    static final char[] DFA95_max = DFA.unpackEncodedStringToUnsignedChars(DFA95_maxS);
    static final short[] DFA95_accept = DFA.unpackEncodedString(DFA95_acceptS);
    static final short[] DFA95_special = DFA.unpackEncodedString(DFA95_specialS);
    static final short[][] DFA95_transition;

    static {
        int numStates = DFA95_transitionS.length;
        DFA95_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA95_transition[i] = DFA.unpackEncodedString(DFA95_transitionS[i]);
        }
    }

    class DFA95 extends DFA {

        public DFA95(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 95;
            this.eot = DFA95_eot;
            this.eof = DFA95_eof;
            this.min = DFA95_min;
            this.max = DFA95_max;
            this.accept = DFA95_accept;
            this.special = DFA95_special;
            this.transition = DFA95_transition;
        }
        public String getDescription() {
            return "()* loopback of 642:42: ( annotationTypeElementDeclaration )*";
        }
    }
    static final String DFA96_eotS =
        "\24\uffff";
    static final String DFA96_eofS =
        "\24\uffff";
    static final String DFA96_minS =
        "\1\4\5\uffff\1\4\15\uffff";
    static final String DFA96_maxS =
        "\1\107\5\uffff\1\47\15\uffff";
    static final String DFA96_acceptS =
        "\1\uffff\1\2\5\uffff\1\1\14\uffff";
    static final String DFA96_specialS =
        "\24\uffff}>";
    static final String[] DFA96_transitionS = {
            "\2\1\25\uffff\1\7\2\uffff\1\1\10\uffff\1\1\5\uffff\12\7\10\1"+
            "\10\uffff\1\6",
            "",
            "",
            "",
            "",
            "",
            "\1\7\42\uffff\1\1",
            "",
            "",
            "",
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

    static final short[] DFA96_eot = DFA.unpackEncodedString(DFA96_eotS);
    static final short[] DFA96_eof = DFA.unpackEncodedString(DFA96_eofS);
    static final char[] DFA96_min = DFA.unpackEncodedStringToUnsignedChars(DFA96_minS);
    static final char[] DFA96_max = DFA.unpackEncodedStringToUnsignedChars(DFA96_maxS);
    static final short[] DFA96_accept = DFA.unpackEncodedString(DFA96_acceptS);
    static final short[] DFA96_special = DFA.unpackEncodedString(DFA96_specialS);
    static final short[][] DFA96_transition;

    static {
        int numStates = DFA96_transitionS.length;
        DFA96_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA96_transition[i] = DFA.unpackEncodedString(DFA96_transitionS[i]);
        }
    }

    class DFA96 extends DFA {

        public DFA96(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 96;
            this.eot = DFA96_eot;
            this.eof = DFA96_eof;
            this.min = DFA96_min;
            this.max = DFA96_max;
            this.accept = DFA96_accept;
            this.special = DFA96_special;
            this.transition = DFA96_transition;
        }
        public String getDescription() {
            return "()* loopback of 646:7: ( modifier )*";
        }
    }
    static final String DFA101_eotS =
        "\13\uffff";
    static final String DFA101_eofS =
        "\13\uffff";
    static final String DFA101_minS =
        "\1\4\3\uffff\1\4\1\uffff\1\47\2\0\2\uffff";
    static final String DFA101_maxS =
        "\1\107\3\uffff\1\4\1\uffff\1\47\2\0\2\uffff";
    static final String DFA101_acceptS =
        "\1\uffff\1\1\1\uffff\1\2\1\uffff\1\3\3\uffff\1\4\1\5";
    static final String DFA101_specialS =
        "\7\uffff\1\0\1\1\2\uffff}>";
    static final String[] DFA101_transitionS = {
            "\1\1\1\4\30\uffff\1\3\10\uffff\1\5\17\uffff\10\1\10\uffff\1"+
            "\6",
            "",
            "",
            "",
            "\1\7",
            "",
            "\1\10",
            "\1\uffff",
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA101_eot = DFA.unpackEncodedString(DFA101_eotS);
    static final short[] DFA101_eof = DFA.unpackEncodedString(DFA101_eofS);
    static final char[] DFA101_min = DFA.unpackEncodedStringToUnsignedChars(DFA101_minS);
    static final char[] DFA101_max = DFA.unpackEncodedStringToUnsignedChars(DFA101_maxS);
    static final short[] DFA101_accept = DFA.unpackEncodedString(DFA101_acceptS);
    static final short[] DFA101_special = DFA.unpackEncodedString(DFA101_specialS);
    static final short[][] DFA101_transition;

    static {
        int numStates = DFA101_transitionS.length;
        DFA101_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA101_transition[i] = DFA.unpackEncodedString(DFA101_transitionS[i]);
        }
    }

    class DFA101 extends DFA {

        public DFA101(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 101;
            this.eot = DFA101_eot;
            this.eof = DFA101_eof;
            this.min = DFA101_min;
            this.max = DFA101_max;
            this.accept = DFA101_accept;
            this.special = DFA101_special;
            this.transition = DFA101_transition;
        }
        public String getDescription() {
            return "649:1: annotationTypeElementRest : ( type annotationMethodOrConstantRest ';' | classDeclaration ( ';' )? | interfaceDeclaration ( ';' )? | enumDeclaration ( ';' )? | annotationTypeDeclaration ( ';' )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA101_7 = input.LA(1);

                         
                        int index101_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred135_Java()) ) {s = 3;}

                        else if ( (synpred139_Java()) ) {s = 9;}

                         
                        input.seek(index101_7);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA101_8 = input.LA(1);

                         
                        int index101_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred137_Java()) ) {s = 5;}

                        else if ( (true) ) {s = 10;}

                         
                        input.seek(index101_8);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 101, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA97_eotS =
        "\25\uffff";
    static final String DFA97_eofS =
        "\1\2\24\uffff";
    static final String DFA97_minS =
        "\1\4\24\uffff";
    static final String DFA97_maxS =
        "\1\107\24\uffff";
    static final String DFA97_acceptS =
        "\1\uffff\1\1\1\2\22\uffff";
    static final String DFA97_specialS =
        "\25\uffff}>";
    static final String[] DFA97_transitionS = {
            "\2\2\23\uffff\1\1\1\uffff\1\2\2\uffff\1\2\7\uffff\2\2\5\uffff"+
            "\22\2\10\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA97_eot = DFA.unpackEncodedString(DFA97_eotS);
    static final short[] DFA97_eof = DFA.unpackEncodedString(DFA97_eofS);
    static final char[] DFA97_min = DFA.unpackEncodedStringToUnsignedChars(DFA97_minS);
    static final char[] DFA97_max = DFA.unpackEncodedStringToUnsignedChars(DFA97_maxS);
    static final short[] DFA97_accept = DFA.unpackEncodedString(DFA97_acceptS);
    static final short[] DFA97_special = DFA.unpackEncodedString(DFA97_specialS);
    static final short[][] DFA97_transition;

    static {
        int numStates = DFA97_transitionS.length;
        DFA97_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA97_transition[i] = DFA.unpackEncodedString(DFA97_transitionS[i]);
        }
    }

    class DFA97 extends DFA {

        public DFA97(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 97;
            this.eot = DFA97_eot;
            this.eof = DFA97_eof;
            this.min = DFA97_min;
            this.max = DFA97_max;
            this.accept = DFA97_accept;
            this.special = DFA97_special;
            this.transition = DFA97_transition;
        }
        public String getDescription() {
            return "651:26: ( ';' )?";
        }
    }
    static final String DFA98_eotS =
        "\25\uffff";
    static final String DFA98_eofS =
        "\1\2\24\uffff";
    static final String DFA98_minS =
        "\1\4\24\uffff";
    static final String DFA98_maxS =
        "\1\107\24\uffff";
    static final String DFA98_acceptS =
        "\1\uffff\1\1\1\2\22\uffff";
    static final String DFA98_specialS =
        "\25\uffff}>";
    static final String[] DFA98_transitionS = {
            "\2\2\23\uffff\1\1\1\uffff\1\2\2\uffff\1\2\7\uffff\2\2\5\uffff"+
            "\22\2\10\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA98_eot = DFA.unpackEncodedString(DFA98_eotS);
    static final short[] DFA98_eof = DFA.unpackEncodedString(DFA98_eofS);
    static final char[] DFA98_min = DFA.unpackEncodedStringToUnsignedChars(DFA98_minS);
    static final char[] DFA98_max = DFA.unpackEncodedStringToUnsignedChars(DFA98_maxS);
    static final short[] DFA98_accept = DFA.unpackEncodedString(DFA98_acceptS);
    static final short[] DFA98_special = DFA.unpackEncodedString(DFA98_specialS);
    static final short[][] DFA98_transition;

    static {
        int numStates = DFA98_transitionS.length;
        DFA98_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA98_transition[i] = DFA.unpackEncodedString(DFA98_transitionS[i]);
        }
    }

    class DFA98 extends DFA {

        public DFA98(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 98;
            this.eot = DFA98_eot;
            this.eof = DFA98_eof;
            this.min = DFA98_min;
            this.max = DFA98_max;
            this.accept = DFA98_accept;
            this.special = DFA98_special;
            this.transition = DFA98_transition;
        }
        public String getDescription() {
            return "652:30: ( ';' )?";
        }
    }
    static final String DFA99_eotS =
        "\25\uffff";
    static final String DFA99_eofS =
        "\1\2\24\uffff";
    static final String DFA99_minS =
        "\1\4\24\uffff";
    static final String DFA99_maxS =
        "\1\107\24\uffff";
    static final String DFA99_acceptS =
        "\1\uffff\1\1\1\2\22\uffff";
    static final String DFA99_specialS =
        "\25\uffff}>";
    static final String[] DFA99_transitionS = {
            "\2\2\23\uffff\1\1\1\uffff\1\2\2\uffff\1\2\7\uffff\2\2\5\uffff"+
            "\22\2\10\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA99_eot = DFA.unpackEncodedString(DFA99_eotS);
    static final short[] DFA99_eof = DFA.unpackEncodedString(DFA99_eofS);
    static final char[] DFA99_min = DFA.unpackEncodedStringToUnsignedChars(DFA99_minS);
    static final char[] DFA99_max = DFA.unpackEncodedStringToUnsignedChars(DFA99_maxS);
    static final short[] DFA99_accept = DFA.unpackEncodedString(DFA99_acceptS);
    static final short[] DFA99_special = DFA.unpackEncodedString(DFA99_specialS);
    static final short[][] DFA99_transition;

    static {
        int numStates = DFA99_transitionS.length;
        DFA99_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA99_transition[i] = DFA.unpackEncodedString(DFA99_transitionS[i]);
        }
    }

    class DFA99 extends DFA {

        public DFA99(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 99;
            this.eot = DFA99_eot;
            this.eof = DFA99_eof;
            this.min = DFA99_min;
            this.max = DFA99_max;
            this.accept = DFA99_accept;
            this.special = DFA99_special;
            this.transition = DFA99_transition;
        }
        public String getDescription() {
            return "653:25: ( ';' )?";
        }
    }
    static final String DFA100_eotS =
        "\25\uffff";
    static final String DFA100_eofS =
        "\1\2\24\uffff";
    static final String DFA100_minS =
        "\1\4\24\uffff";
    static final String DFA100_maxS =
        "\1\107\24\uffff";
    static final String DFA100_acceptS =
        "\1\uffff\1\1\1\2\22\uffff";
    static final String DFA100_specialS =
        "\25\uffff}>";
    static final String[] DFA100_transitionS = {
            "\2\2\23\uffff\1\1\1\uffff\1\2\2\uffff\1\2\7\uffff\2\2\5\uffff"+
            "\22\2\10\uffff\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA100_eot = DFA.unpackEncodedString(DFA100_eotS);
    static final short[] DFA100_eof = DFA.unpackEncodedString(DFA100_eofS);
    static final char[] DFA100_min = DFA.unpackEncodedStringToUnsignedChars(DFA100_minS);
    static final char[] DFA100_max = DFA.unpackEncodedStringToUnsignedChars(DFA100_maxS);
    static final short[] DFA100_accept = DFA.unpackEncodedString(DFA100_acceptS);
    static final short[] DFA100_special = DFA.unpackEncodedString(DFA100_specialS);
    static final short[][] DFA100_transition;

    static {
        int numStates = DFA100_transitionS.length;
        DFA100_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA100_transition[i] = DFA.unpackEncodedString(DFA100_transitionS[i]);
        }
    }

    class DFA100 extends DFA {

        public DFA100(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 100;
            this.eot = DFA100_eot;
            this.eof = DFA100_eof;
            this.min = DFA100_min;
            this.max = DFA100_max;
            this.accept = DFA100_accept;
            this.special = DFA100_special;
            this.transition = DFA100_transition;
        }
        public String getDescription() {
            return "654:35: ( ';' )?";
        }
    }
    static final String DFA104_eotS =
        "\70\uffff";
    static final String DFA104_eofS =
        "\70\uffff";
    static final String DFA104_minS =
        "\1\4\67\uffff";
    static final String DFA104_maxS =
        "\1\167\67\uffff";
    static final String DFA104_acceptS =
        "\1\uffff\1\2\1\1\65\uffff";
    static final String DFA104_specialS =
        "\70\uffff}>";
    static final String[] DFA104_transitionS = {
            "\10\2\15\uffff\1\2\1\uffff\1\2\2\uffff\1\2\2\uffff\1\2\3\uffff"+
            "\1\2\1\1\2\2\4\uffff\22\2\1\uffff\2\2\2\uffff\4\2\1\uffff\1"+
            "\2\1\uffff\10\2\1\uffff\2\2\2\uffff\6\2\20\uffff\2\2\2\uffff"+
            "\6\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA104_eot = DFA.unpackEncodedString(DFA104_eotS);
    static final short[] DFA104_eof = DFA.unpackEncodedString(DFA104_eofS);
    static final char[] DFA104_min = DFA.unpackEncodedStringToUnsignedChars(DFA104_minS);
    static final char[] DFA104_max = DFA.unpackEncodedStringToUnsignedChars(DFA104_maxS);
    static final short[] DFA104_accept = DFA.unpackEncodedString(DFA104_acceptS);
    static final short[] DFA104_special = DFA.unpackEncodedString(DFA104_specialS);
    static final short[][] DFA104_transition;

    static {
        int numStates = DFA104_transitionS.length;
        DFA104_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA104_transition[i] = DFA.unpackEncodedString(DFA104_transitionS[i]);
        }
    }

    class DFA104 extends DFA {

        public DFA104(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 104;
            this.eot = DFA104_eot;
            this.eof = DFA104_eof;
            this.min = DFA104_min;
            this.max = DFA104_max;
            this.accept = DFA104_accept;
            this.special = DFA104_special;
            this.transition = DFA104_transition;
        }
        public String getDescription() {
            return "()* loopback of 688:11: ( blockStatement )*";
        }
    }
    static final String DFA105_eotS =
        "\171\uffff";
    static final String DFA105_eofS =
        "\171\uffff";
    static final String DFA105_minS =
        "\5\4\6\uffff\1\5\55\uffff\2\0\16\uffff\1\0\1\uffff\3\0\30\uffff"+
        "\1\0\22\uffff";
    static final String DFA105_maxS =
        "\1\167\1\107\1\47\1\163\1\51\6\uffff\1\107\55\uffff\2\0\16\uffff"+
        "\1\0\1\uffff\3\0\30\uffff\1\0\22\uffff";
    static final String DFA105_acceptS =
        "\5\uffff\1\2\14\uffff\1\3\44\uffff\1\1\101\uffff";
    static final String DFA105_specialS =
        "\71\uffff\1\0\1\1\16\uffff\1\2\1\uffff\1\3\1\4\1\5\30\uffff\1\6"+
        "\22\uffff}>";
    static final String[] DFA105_transitionS = {
            "\1\3\1\5\6\22\15\uffff\1\22\1\uffff\1\5\2\uffff\1\5\2\uffff"+
            "\1\22\3\uffff\1\22\1\uffff\1\5\1\22\4\uffff\4\5\1\1\1\5\1\13"+
            "\3\5\10\4\1\uffff\2\22\2\uffff\3\22\1\2\1\uffff\1\22\1\uffff"+
            "\10\22\1\uffff\2\22\2\uffff\6\22\20\uffff\2\22\2\uffff\6\22",
            "\1\67\1\5\25\uffff\1\5\2\uffff\1\5\10\uffff\1\5\5\uffff\4\5"+
            "\1\71\5\5\10\67\10\uffff\1\72",
            "\1\111\42\uffff\1\5",
            "\1\67\24\uffff\1\22\2\uffff\1\114\1\22\3\uffff\1\113\1\uffff"+
            "\2\22\4\uffff\1\115\2\uffff\1\22\22\uffff\1\22\1\uffff\1\22"+
            "\10\uffff\1\22\24\uffff\25\22",
            "\1\67\27\uffff\1\22\14\uffff\1\146",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\5\25\uffff\1\5\2\uffff\1\5\10\uffff\1\5\5\uffff\12\5\12"+
            "\uffff\1\22\5\uffff\1\5",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    class DFA105 extends DFA {

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
        public String getDescription() {
            return "691:1: blockStatement : ( localVariableDeclaration | classOrInterfaceDeclaration | statement );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA105_57 = input.LA(1);

                         
                        int index105_57 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred144_Java()) ) {s = 55;}

                        else if ( (synpred145_Java()) ) {s = 5;}

                         
                        input.seek(index105_57);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA105_58 = input.LA(1);

                         
                        int index105_58 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred144_Java()) ) {s = 55;}

                        else if ( (synpred145_Java()) ) {s = 5;}

                         
                        input.seek(index105_58);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA105_73 = input.LA(1);

                         
                        int index105_73 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred144_Java()) ) {s = 55;}

                        else if ( (synpred145_Java()) ) {s = 5;}

                         
                        input.seek(index105_73);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA105_75 = input.LA(1);

                         
                        int index105_75 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred144_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 18;}

                         
                        input.seek(index105_75);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA105_76 = input.LA(1);

                         
                        int index105_76 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred144_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 18;}

                         
                        input.seek(index105_76);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA105_77 = input.LA(1);

                         
                        int index105_77 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred144_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 18;}

                         
                        input.seek(index105_77);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA105_102 = input.LA(1);

                         
                        int index105_102 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred144_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 18;}

                         
                        input.seek(index105_102);
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
        "\104\uffff";
    static final String DFA111_eofS =
        "\104\uffff";
    static final String DFA111_minS =
        "\1\4\45\uffff\1\31\35\uffff";
    static final String DFA111_maxS =
        "\1\167\45\uffff\1\163\35\uffff";
    static final String DFA111_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1"+
        "\15\1\16\1\17\1\20\1\21\1\22\26\uffff\1\23\32\uffff";
    static final String DFA111_specialS =
        "\104\uffff}>";
    static final String[] DFA111_transitionS = {
            "\1\46\1\uffff\6\22\15\uffff\1\21\7\uffff\1\22\3\uffff\1\1\2"+
            "\uffff\1\22\12\uffff\1\11\3\uffff\10\22\1\uffff\2\22\2\uffff"+
            "\3\22\2\uffff\1\2\1\uffff\1\6\1\5\1\10\1\12\1\14\1\15\1\13\1"+
            "\3\1\uffff\1\4\1\7\2\uffff\1\16\1\17\1\20\3\22\20\uffff\2\22"+
            "\2\uffff\6\22",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\22\2\uffff\2\22\3\uffff\1\22\1\uffff\2\22\4\uffff\1\22\2"+
            "\uffff\1\22\22\uffff\1\22\1\uffff\1\22\10\uffff\1\51\24\uffff"+
            "\25\22",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    class DFA111 extends DFA {

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
        public String getDescription() {
            return "721:1: statement : ( block | 'assert' expression ( ':' expression )? ';' | ifStatement | forStatement | whileStatement | 'do' statement 'while' parExpression ';' | tryStatement | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | throwStatement | 'break' ( Identifier )? ';' | 'continue' ( Identifier )? ';' | modifyStatement | updateStatement | retractStatement | ';' | statementExpression ';' | Identifier ':' statement );";
        }
    }
    static final String DFA108_eotS =
        "\31\uffff";
    static final String DFA108_eofS =
        "\31\uffff";
    static final String DFA108_minS =
        "\1\4\30\uffff";
    static final String DFA108_maxS =
        "\1\167\30\uffff";
    static final String DFA108_acceptS =
        "\1\uffff\1\1\26\uffff\1\2";
    static final String DFA108_specialS =
        "\31\uffff}>";
    static final String[] DFA108_transitionS = {
            "\1\1\1\uffff\6\1\15\uffff\1\30\7\uffff\1\1\6\uffff\1\1\16\uffff"+
            "\10\1\1\uffff\2\1\2\uffff\3\1\24\uffff\3\1\20\uffff\2\1\2\uffff"+
            "\6\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA108_eot = DFA.unpackEncodedString(DFA108_eotS);
    static final short[] DFA108_eof = DFA.unpackEncodedString(DFA108_eofS);
    static final char[] DFA108_min = DFA.unpackEncodedStringToUnsignedChars(DFA108_minS);
    static final char[] DFA108_max = DFA.unpackEncodedStringToUnsignedChars(DFA108_maxS);
    static final short[] DFA108_accept = DFA.unpackEncodedString(DFA108_acceptS);
    static final short[] DFA108_special = DFA.unpackEncodedString(DFA108_specialS);
    static final short[][] DFA108_transition;

    static {
        int numStates = DFA108_transitionS.length;
        DFA108_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA108_transition[i] = DFA.unpackEncodedString(DFA108_transitionS[i]);
        }
    }

    class DFA108 extends DFA {

        public DFA108(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 108;
            this.eot = DFA108_eot;
            this.eof = DFA108_eof;
            this.min = DFA108_min;
            this.max = DFA108_max;
            this.accept = DFA108_accept;
            this.special = DFA108_special;
            this.transition = DFA108_transition;
        }
        public String getDescription() {
            return "733:16: ( expression )?";
        }
    }
    static final String DFA113_eotS =
        "\145\uffff";
    static final String DFA113_eofS =
        "\1\1\144\uffff";
    static final String DFA113_minS =
        "\1\4\72\uffff\1\0\51\uffff";
    static final String DFA113_maxS =
        "\1\167\72\uffff\1\0\51\uffff";
    static final String DFA113_acceptS =
        "\1\uffff\1\2\142\uffff\1\1";
    static final String DFA113_specialS =
        "\73\uffff\1\0\51\uffff}>";
    static final String[] DFA113_transitionS = {
            "\10\1\15\uffff\1\1\1\uffff\1\1\2\uffff\1\1\2\uffff\1\1\3\uffff"+
            "\4\1\4\uffff\22\1\1\uffff\2\1\2\uffff\6\1\1\uffff\10\1\1\73"+
            "\2\1\2\uffff\7\1\17\uffff\2\1\2\uffff\6\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    class DFA113 extends DFA {

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
        public String getDescription() {
            return "()* loopback of 781:5: (y= 'else' ( 'if' parExpression )? z= statement )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA113_59 = input.LA(1);

                         
                        int index113_59 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred170_Java()) ) {s = 100;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index113_59);
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
    static final String DFA112_eotS =
        "\53\uffff";
    static final String DFA112_eofS =
        "\53\uffff";
    static final String DFA112_minS =
        "\1\4\1\101\47\uffff\1\0\1\uffff";
    static final String DFA112_maxS =
        "\1\167\1\101\47\uffff\1\0\1\uffff";
    static final String DFA112_acceptS =
        "\2\uffff\1\2\47\uffff\1\1";
    static final String DFA112_specialS =
        "\51\uffff\1\0\1\uffff}>";
    static final String[] DFA112_transitionS = {
            "\1\2\1\uffff\6\2\15\uffff\1\2\7\uffff\1\2\3\uffff\1\2\2\uffff"+
            "\1\2\12\uffff\1\2\3\uffff\10\2\1\uffff\2\2\2\uffff\3\2\2\uffff"+
            "\1\2\1\uffff\7\2\1\1\1\uffff\2\2\2\uffff\6\2\20\uffff\2\2\2"+
            "\uffff\6\2",
            "\1\51",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            ""
    };

    static final short[] DFA112_eot = DFA.unpackEncodedString(DFA112_eotS);
    static final short[] DFA112_eof = DFA.unpackEncodedString(DFA112_eofS);
    static final char[] DFA112_min = DFA.unpackEncodedStringToUnsignedChars(DFA112_minS);
    static final char[] DFA112_max = DFA.unpackEncodedStringToUnsignedChars(DFA112_maxS);
    static final short[] DFA112_accept = DFA.unpackEncodedString(DFA112_acceptS);
    static final short[] DFA112_special = DFA.unpackEncodedString(DFA112_specialS);
    static final short[][] DFA112_transition;

    static {
        int numStates = DFA112_transitionS.length;
        DFA112_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA112_transition[i] = DFA.unpackEncodedString(DFA112_transitionS[i]);
        }
    }

    class DFA112 extends DFA {

        public DFA112(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 112;
            this.eot = DFA112_eot;
            this.eof = DFA112_eof;
            this.min = DFA112_min;
            this.max = DFA112_max;
            this.accept = DFA112_accept;
            this.special = DFA112_special;
            this.transition = DFA112_transition;
        }
        public String getDescription() {
            return "782:16: ( 'if' parExpression )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA112_41 = input.LA(1);

                         
                        int index112_41 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred169_Java()) ) {s = 42;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index112_41);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 112, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA118_eotS =
        "\100\uffff";
    static final String DFA118_eofS =
        "\100\uffff";
    static final String DFA118_minS =
        "\5\4\26\uffff\11\0\30\uffff\2\0\2\uffff";
    static final String DFA118_maxS =
        "\1\167\1\107\1\4\1\163\1\51\26\uffff\11\0\30\uffff\2\0\2\uffff";
    static final String DFA118_acceptS =
        "\5\uffff\1\2\71\uffff\1\1";
    static final String DFA118_specialS =
        "\33\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\30\uffff\1\11\1\12"+
        "\2\uffff}>";
    static final String[] DFA118_transitionS = {
            "\1\3\1\uffff\6\5\15\uffff\1\5\7\uffff\1\5\6\uffff\1\5\10\uffff"+
            "\1\1\5\uffff\10\4\1\uffff\2\5\2\uffff\3\5\1\2\23\uffff\3\5\20"+
            "\uffff\2\5\2\uffff\6\5",
            "\1\33\54\uffff\1\35\5\uffff\10\34\10\uffff\1\36",
            "\1\37",
            "\1\43\24\uffff\1\5\2\uffff\1\41\1\5\3\uffff\1\40\3\5\4\uffff"+
            "\1\42\2\uffff\1\5\22\uffff\1\5\1\uffff\1\5\35\uffff\25\5",
            "\1\75\27\uffff\1\5\14\uffff\1\74",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    class DFA118 extends DFA {

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
        public String getDescription() {
            return "814:5: ( ( ( variableModifier )* type id= Identifier z= ':' expression ) | ( ( forInit )? z= ';' ( expression )? ';' ( forUpdate )? ) )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA118_27 = input.LA(1);

                         
                        int index118_27 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred172_Java()) ) {s = 63;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index118_27);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA118_28 = input.LA(1);

                         
                        int index118_28 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred172_Java()) ) {s = 63;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index118_28);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA118_29 = input.LA(1);

                         
                        int index118_29 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred172_Java()) ) {s = 63;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index118_29);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA118_30 = input.LA(1);

                         
                        int index118_30 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred172_Java()) ) {s = 63;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index118_30);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA118_31 = input.LA(1);

                         
                        int index118_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred172_Java()) ) {s = 63;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index118_31);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA118_32 = input.LA(1);

                         
                        int index118_32 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred172_Java()) ) {s = 63;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index118_32);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA118_33 = input.LA(1);

                         
                        int index118_33 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred172_Java()) ) {s = 63;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index118_33);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA118_34 = input.LA(1);

                         
                        int index118_34 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred172_Java()) ) {s = 63;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index118_34);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA118_35 = input.LA(1);

                         
                        int index118_35 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred172_Java()) ) {s = 63;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index118_35);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA118_60 = input.LA(1);

                         
                        int index118_60 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred172_Java()) ) {s = 63;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index118_60);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA118_61 = input.LA(1);

                         
                        int index118_61 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred172_Java()) ) {s = 63;}

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
    static final String DFA115_eotS =
        "\33\uffff";
    static final String DFA115_eofS =
        "\33\uffff";
    static final String DFA115_minS =
        "\1\4\32\uffff";
    static final String DFA115_maxS =
        "\1\167\32\uffff";
    static final String DFA115_acceptS =
        "\1\uffff\1\1\30\uffff\1\2";
    static final String DFA115_specialS =
        "\33\uffff}>";
    static final String[] DFA115_transitionS = {
            "\1\1\1\uffff\6\1\15\uffff\1\32\7\uffff\1\1\6\uffff\1\1\10\uffff"+
            "\1\1\5\uffff\10\1\1\uffff\2\1\2\uffff\4\1\23\uffff\3\1\20\uffff"+
            "\2\1\2\uffff\6\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA115_eot = DFA.unpackEncodedString(DFA115_eotS);
    static final short[] DFA115_eof = DFA.unpackEncodedString(DFA115_eofS);
    static final char[] DFA115_min = DFA.unpackEncodedStringToUnsignedChars(DFA115_minS);
    static final char[] DFA115_max = DFA.unpackEncodedStringToUnsignedChars(DFA115_maxS);
    static final short[] DFA115_accept = DFA.unpackEncodedString(DFA115_acceptS);
    static final short[] DFA115_special = DFA.unpackEncodedString(DFA115_specialS);
    static final short[][] DFA115_transition;

    static {
        int numStates = DFA115_transitionS.length;
        DFA115_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA115_transition[i] = DFA.unpackEncodedString(DFA115_transitionS[i]);
        }
    }

    class DFA115 extends DFA {

        public DFA115(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 115;
            this.eot = DFA115_eot;
            this.eof = DFA115_eof;
            this.min = DFA115_min;
            this.max = DFA115_max;
            this.accept = DFA115_accept;
            this.special = DFA115_special;
            this.transition = DFA115_transition;
        }
        public String getDescription() {
            return "840:10: ( forInit )?";
        }
    }
    static final String DFA116_eotS =
        "\31\uffff";
    static final String DFA116_eofS =
        "\31\uffff";
    static final String DFA116_minS =
        "\1\4\30\uffff";
    static final String DFA116_maxS =
        "\1\167\30\uffff";
    static final String DFA116_acceptS =
        "\1\uffff\1\1\26\uffff\1\2";
    static final String DFA116_specialS =
        "\31\uffff}>";
    static final String[] DFA116_transitionS = {
            "\1\1\1\uffff\6\1\15\uffff\1\30\7\uffff\1\1\6\uffff\1\1\16\uffff"+
            "\10\1\1\uffff\2\1\2\uffff\3\1\24\uffff\3\1\20\uffff\2\1\2\uffff"+
            "\6\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA116_eot = DFA.unpackEncodedString(DFA116_eotS);
    static final short[] DFA116_eof = DFA.unpackEncodedString(DFA116_eofS);
    static final char[] DFA116_min = DFA.unpackEncodedStringToUnsignedChars(DFA116_minS);
    static final char[] DFA116_max = DFA.unpackEncodedStringToUnsignedChars(DFA116_maxS);
    static final short[] DFA116_accept = DFA.unpackEncodedString(DFA116_acceptS);
    static final short[] DFA116_special = DFA.unpackEncodedString(DFA116_specialS);
    static final short[][] DFA116_transition;

    static {
        int numStates = DFA116_transitionS.length;
        DFA116_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA116_transition[i] = DFA.unpackEncodedString(DFA116_transitionS[i]);
        }
    }

    class DFA116 extends DFA {

        public DFA116(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 116;
            this.eot = DFA116_eot;
            this.eof = DFA116_eof;
            this.min = DFA116_min;
            this.max = DFA116_max;
            this.accept = DFA116_accept;
            this.special = DFA116_special;
            this.transition = DFA116_transition;
        }
        public String getDescription() {
            return "840:25: ( expression )?";
        }
    }
    static final String DFA117_eotS =
        "\31\uffff";
    static final String DFA117_eofS =
        "\31\uffff";
    static final String DFA117_minS =
        "\1\4\30\uffff";
    static final String DFA117_maxS =
        "\1\167\30\uffff";
    static final String DFA117_acceptS =
        "\1\uffff\1\1\26\uffff\1\2";
    static final String DFA117_specialS =
        "\31\uffff}>";
    static final String[] DFA117_transitionS = {
            "\1\1\1\uffff\6\1\25\uffff\1\1\6\uffff\1\1\16\uffff\10\1\1\uffff"+
            "\2\1\1\30\1\uffff\3\1\24\uffff\3\1\20\uffff\2\1\2\uffff\6\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA117_eot = DFA.unpackEncodedString(DFA117_eotS);
    static final short[] DFA117_eof = DFA.unpackEncodedString(DFA117_eofS);
    static final char[] DFA117_min = DFA.unpackEncodedStringToUnsignedChars(DFA117_minS);
    static final char[] DFA117_max = DFA.unpackEncodedStringToUnsignedChars(DFA117_maxS);
    static final short[] DFA117_accept = DFA.unpackEncodedString(DFA117_acceptS);
    static final short[] DFA117_special = DFA.unpackEncodedString(DFA117_specialS);
    static final short[][] DFA117_transition;

    static {
        int numStates = DFA117_transitionS.length;
        DFA117_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA117_transition[i] = DFA.unpackEncodedString(DFA117_transitionS[i]);
        }
    }

    class DFA117 extends DFA {

        public DFA117(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 117;
            this.eot = DFA117_eot;
            this.eof = DFA117_eof;
            this.min = DFA117_min;
            this.max = DFA117_max;
            this.accept = DFA117_accept;
            this.special = DFA117_special;
            this.transition = DFA117_transition;
        }
        public String getDescription() {
            return "840:41: ( forUpdate )?";
        }
    }
    static final String DFA119_eotS =
        "\70\uffff";
    static final String DFA119_eofS =
        "\70\uffff";
    static final String DFA119_minS =
        "\1\4\67\uffff";
    static final String DFA119_maxS =
        "\1\167\67\uffff";
    static final String DFA119_acceptS =
        "\1\uffff\1\2\1\1\65\uffff";
    static final String DFA119_specialS =
        "\70\uffff}>";
    static final String[] DFA119_transitionS = {
            "\10\2\15\uffff\1\2\1\uffff\1\2\2\uffff\1\2\2\uffff\1\2\3\uffff"+
            "\1\2\1\1\2\2\4\uffff\22\2\1\uffff\2\2\2\uffff\4\2\1\uffff\1"+
            "\2\1\uffff\10\2\1\uffff\2\2\2\uffff\6\2\20\uffff\2\2\2\uffff"+
            "\6\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA119_eot = DFA.unpackEncodedString(DFA119_eotS);
    static final short[] DFA119_eof = DFA.unpackEncodedString(DFA119_eofS);
    static final char[] DFA119_min = DFA.unpackEncodedStringToUnsignedChars(DFA119_minS);
    static final char[] DFA119_max = DFA.unpackEncodedStringToUnsignedChars(DFA119_maxS);
    static final short[] DFA119_accept = DFA.unpackEncodedString(DFA119_acceptS);
    static final short[] DFA119_special = DFA.unpackEncodedString(DFA119_specialS);
    static final short[][] DFA119_transition;

    static {
        int numStates = DFA119_transitionS.length;
        DFA119_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA119_transition[i] = DFA.unpackEncodedString(DFA119_transitionS[i]);
        }
    }

    class DFA119 extends DFA {

        public DFA119(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 119;
            this.eot = DFA119_eot;
            this.eof = DFA119_eof;
            this.min = DFA119_min;
            this.max = DFA119_max;
            this.accept = DFA119_accept;
            this.special = DFA119_special;
            this.transition = DFA119_transition;
        }
        public String getDescription() {
            return "()* loopback of 881:14: ( blockStatement )*";
        }
    }
    static final String DFA121_eotS =
        "\76\uffff";
    static final String DFA121_eofS =
        "\1\1\75\uffff";
    static final String DFA121_minS =
        "\1\4\75\uffff";
    static final String DFA121_maxS =
        "\1\167\75\uffff";
    static final String DFA121_acceptS =
        "\1\uffff\1\2\73\uffff\1\1";
    static final String DFA121_specialS =
        "\76\uffff}>";
    static final String[] DFA121_transitionS = {
            "\10\1\15\uffff\1\1\1\uffff\1\1\2\uffff\1\1\2\uffff\1\1\3\uffff"+
            "\4\1\4\uffff\22\1\1\uffff\2\1\2\uffff\6\1\1\uffff\13\1\1\75"+
            "\10\1\17\uffff\2\1\2\uffff\6\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    class DFA121 extends DFA {

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
        public String getDescription() {
            return "()* loopback of 889:5: (s= 'catch' '(' formalParameter ')' bs= '{' ( blockStatement )* c= '}' )*";
        }
    }
    static final String DFA120_eotS =
        "\70\uffff";
    static final String DFA120_eofS =
        "\70\uffff";
    static final String DFA120_minS =
        "\1\4\67\uffff";
    static final String DFA120_maxS =
        "\1\167\67\uffff";
    static final String DFA120_acceptS =
        "\1\uffff\1\2\1\1\65\uffff";
    static final String DFA120_specialS =
        "\70\uffff}>";
    static final String[] DFA120_transitionS = {
            "\10\2\15\uffff\1\2\1\uffff\1\2\2\uffff\1\2\2\uffff\1\2\3\uffff"+
            "\1\2\1\1\2\2\4\uffff\22\2\1\uffff\2\2\2\uffff\4\2\1\uffff\1"+
            "\2\1\uffff\10\2\1\uffff\2\2\2\uffff\6\2\20\uffff\2\2\2\uffff"+
            "\6\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA120_eot = DFA.unpackEncodedString(DFA120_eotS);
    static final short[] DFA120_eof = DFA.unpackEncodedString(DFA120_eofS);
    static final char[] DFA120_min = DFA.unpackEncodedStringToUnsignedChars(DFA120_minS);
    static final char[] DFA120_max = DFA.unpackEncodedStringToUnsignedChars(DFA120_maxS);
    static final short[] DFA120_accept = DFA.unpackEncodedString(DFA120_acceptS);
    static final short[] DFA120_special = DFA.unpackEncodedString(DFA120_specialS);
    static final short[][] DFA120_transition;

    static {
        int numStates = DFA120_transitionS.length;
        DFA120_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA120_transition[i] = DFA.unpackEncodedString(DFA120_transitionS[i]);
        }
    }

    class DFA120 extends DFA {

        public DFA120(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 120;
            this.eot = DFA120_eot;
            this.eof = DFA120_eof;
            this.min = DFA120_min;
            this.max = DFA120_max;
            this.accept = DFA120_accept;
            this.special = DFA120_special;
            this.transition = DFA120_transition;
        }
        public String getDescription() {
            return "()* loopback of 894:15: ( blockStatement )*";
        }
    }
    static final String DFA123_eotS =
        "\75\uffff";
    static final String DFA123_eofS =
        "\1\2\74\uffff";
    static final String DFA123_minS =
        "\1\4\74\uffff";
    static final String DFA123_maxS =
        "\1\167\74\uffff";
    static final String DFA123_acceptS =
        "\1\uffff\1\1\1\2\72\uffff";
    static final String DFA123_specialS =
        "\75\uffff}>";
    static final String[] DFA123_transitionS = {
            "\10\2\15\uffff\1\2\1\uffff\1\2\2\uffff\1\2\2\uffff\1\2\3\uffff"+
            "\4\2\4\uffff\22\2\1\uffff\2\2\2\uffff\6\2\1\uffff\13\2\1\uffff"+
            "\1\1\7\2\17\uffff\2\2\2\uffff\6\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    class DFA123 extends DFA {

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
        public String getDescription() {
            return "902:6: (s= 'finally' bs= '{' ( blockStatement )* c= '}' )?";
        }
    }
    static final String DFA122_eotS =
        "\70\uffff";
    static final String DFA122_eofS =
        "\70\uffff";
    static final String DFA122_minS =
        "\1\4\67\uffff";
    static final String DFA122_maxS =
        "\1\167\67\uffff";
    static final String DFA122_acceptS =
        "\1\uffff\1\2\1\1\65\uffff";
    static final String DFA122_specialS =
        "\70\uffff}>";
    static final String[] DFA122_transitionS = {
            "\10\2\15\uffff\1\2\1\uffff\1\2\2\uffff\1\2\2\uffff\1\2\3\uffff"+
            "\1\2\1\1\2\2\4\uffff\22\2\1\uffff\2\2\2\uffff\4\2\1\uffff\1"+
            "\2\1\uffff\10\2\1\uffff\2\2\2\uffff\6\2\20\uffff\2\2\2\uffff"+
            "\6\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA122_eot = DFA.unpackEncodedString(DFA122_eotS);
    static final short[] DFA122_eof = DFA.unpackEncodedString(DFA122_eofS);
    static final char[] DFA122_min = DFA.unpackEncodedStringToUnsignedChars(DFA122_minS);
    static final char[] DFA122_max = DFA.unpackEncodedStringToUnsignedChars(DFA122_maxS);
    static final short[] DFA122_accept = DFA.unpackEncodedString(DFA122_acceptS);
    static final short[] DFA122_special = DFA.unpackEncodedString(DFA122_specialS);
    static final short[][] DFA122_transition;

    static {
        int numStates = DFA122_transitionS.length;
        DFA122_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA122_transition[i] = DFA.unpackEncodedString(DFA122_transitionS[i]);
        }
    }

    class DFA122 extends DFA {

        public DFA122(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 122;
            this.eot = DFA122_eot;
            this.eof = DFA122_eof;
            this.min = DFA122_min;
            this.max = DFA122_max;
            this.accept = DFA122_accept;
            this.special = DFA122_special;
            this.transition = DFA122_transition;
        }
        public String getDescription() {
            return "()* loopback of 905:15: ( blockStatement )*";
        }
    }
    static final String DFA125_eotS =
        "\31\uffff";
    static final String DFA125_eofS =
        "\31\uffff";
    static final String DFA125_minS =
        "\1\4\30\uffff";
    static final String DFA125_maxS =
        "\1\167\30\uffff";
    static final String DFA125_acceptS =
        "\1\uffff\1\1\26\uffff\1\2";
    static final String DFA125_specialS =
        "\31\uffff}>";
    static final String[] DFA125_transitionS = {
            "\1\1\1\uffff\6\1\25\uffff\1\1\4\uffff\1\30\1\uffff\1\1\16\uffff"+
            "\10\1\1\uffff\2\1\2\uffff\3\1\24\uffff\3\1\20\uffff\2\1\2\uffff"+
            "\6\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA125_eot = DFA.unpackEncodedString(DFA125_eotS);
    static final short[] DFA125_eof = DFA.unpackEncodedString(DFA125_eofS);
    static final char[] DFA125_min = DFA.unpackEncodedStringToUnsignedChars(DFA125_minS);
    static final char[] DFA125_max = DFA.unpackEncodedStringToUnsignedChars(DFA125_maxS);
    static final short[] DFA125_accept = DFA.unpackEncodedString(DFA125_acceptS);
    static final short[] DFA125_special = DFA.unpackEncodedString(DFA125_specialS);
    static final short[][] DFA125_transition;

    static {
        int numStates = DFA125_transitionS.length;
        DFA125_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA125_transition[i] = DFA.unpackEncodedString(DFA125_transitionS[i]);
        }
    }

    class DFA125 extends DFA {

        public DFA125(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 125;
            this.eot = DFA125_eot;
            this.eof = DFA125_eof;
            this.min = DFA125_min;
            this.max = DFA125_max;
            this.accept = DFA125_accept;
            this.special = DFA125_special;
            this.transition = DFA125_transition;
        }
        public String getDescription() {
            return "924:9: (e= expression ( ',' e= expression )* )?";
        }
    }
    static final String DFA129_eotS =
        "\73\uffff";
    static final String DFA129_eofS =
        "\1\1\72\uffff";
    static final String DFA129_minS =
        "\1\4\72\uffff";
    static final String DFA129_maxS =
        "\1\167\72\uffff";
    static final String DFA129_acceptS =
        "\1\uffff\1\2\3\uffff\1\1\65\uffff";
    static final String DFA129_specialS =
        "\73\uffff}>";
    static final String[] DFA129_transitionS = {
            "\10\5\15\uffff\1\5\1\uffff\1\5\2\uffff\1\5\2\uffff\1\5\3\uffff"+
            "\1\5\1\1\2\5\4\uffff\22\5\1\uffff\2\5\2\uffff\4\5\1\1\1\5\1"+
            "\uffff\10\5\1\uffff\2\5\2\uffff\6\5\1\1\17\uffff\2\5\2\uffff"+
            "\6\5",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    class DFA129 extends DFA {

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
        public String getDescription() {
            return "()* loopback of 1005:19: ( blockStatement )*";
        }
    }
    static final String DFA130_eotS =
        "\33\uffff";
    static final String DFA130_eofS =
        "\33\uffff";
    static final String DFA130_minS =
        "\1\110\1\4\25\uffff\1\0\3\uffff";
    static final String DFA130_maxS =
        "\1\136\1\167\25\uffff\1\0\3\uffff";
    static final String DFA130_acceptS =
        "\2\uffff\1\3\1\1\26\uffff\1\2";
    static final String DFA130_specialS =
        "\27\uffff\1\0\3\uffff}>";
    static final String[] DFA130_transitionS = {
            "\1\2\25\uffff\1\1",
            "\1\27\1\uffff\6\3\25\uffff\1\3\6\uffff\1\3\16\uffff\10\3\1"+
            "\uffff\2\3\2\uffff\3\3\24\uffff\3\3\20\uffff\2\3\2\uffff\6\3",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            ""
    };

    static final short[] DFA130_eot = DFA.unpackEncodedString(DFA130_eotS);
    static final short[] DFA130_eof = DFA.unpackEncodedString(DFA130_eofS);
    static final char[] DFA130_min = DFA.unpackEncodedStringToUnsignedChars(DFA130_minS);
    static final char[] DFA130_max = DFA.unpackEncodedStringToUnsignedChars(DFA130_maxS);
    static final short[] DFA130_accept = DFA.unpackEncodedString(DFA130_acceptS);
    static final short[] DFA130_special = DFA.unpackEncodedString(DFA130_specialS);
    static final short[][] DFA130_transition;

    static {
        int numStates = DFA130_transitionS.length;
        DFA130_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA130_transition[i] = DFA.unpackEncodedString(DFA130_transitionS[i]);
        }
    }

    class DFA130 extends DFA {

        public DFA130(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 130;
            this.eot = DFA130_eot;
            this.eof = DFA130_eof;
            this.min = DFA130_min;
            this.max = DFA130_max;
            this.accept = DFA130_accept;
            this.special = DFA130_special;
            this.transition = DFA130_transition;
        }
        public String getDescription() {
            return "1008:1: switchLabel : ( 'case' constantExpression ':' | 'case' enumConstantName ':' | 'default' ':' );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA130_23 = input.LA(1);

                         
                        int index130_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred188_Java()) ) {s = 3;}

                        else if ( (synpred189_Java()) ) {s = 26;}

                         
                        input.seek(index130_23);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 130, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA135_eotS =
        "\u0092\uffff";
    static final String DFA135_eofS =
        "\u0092\uffff";
    static final String DFA135_minS =
        "\5\4\26\uffff\10\4\1\31\30\uffff\1\52\1\31\1\uffff\21\0\2\uffff"+
        "\3\0\24\uffff\1\0\5\uffff\1\0\34\uffff\1\0\5\uffff";
    static final String DFA135_maxS =
        "\1\167\1\107\1\4\1\163\1\51\26\uffff\2\51\1\107\1\4\1\107\3\167"+
        "\1\112\30\uffff\1\52\1\112\1\uffff\21\0\2\uffff\3\0\24\uffff\1\0"+
        "\5\uffff\1\0\34\uffff\1\0\5\uffff";
    static final String DFA135_acceptS =
        "\5\uffff\1\2\u0081\uffff\1\1\12\uffff";
    static final String DFA135_specialS =
        "\77\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1"+
        "\14\1\15\1\16\1\17\1\20\2\uffff\1\21\1\22\1\23\24\uffff\1\24\5\uffff"+
        "\1\25\34\uffff\1\26\5\uffff}>";
    static final String[] DFA135_transitionS = {
            "\1\3\1\uffff\6\5\15\uffff\1\5\7\uffff\1\5\6\uffff\1\5\10\uffff"+
            "\1\1\5\uffff\10\4\1\uffff\2\5\2\uffff\3\5\1\2\23\uffff\3\5\20"+
            "\uffff\2\5\2\uffff\6\5",
            "\1\33\54\uffff\1\35\5\uffff\10\34\10\uffff\1\36",
            "\1\37",
            "\1\43\24\uffff\1\5\2\uffff\1\41\1\5\3\uffff\1\40\3\5\4\uffff"+
            "\1\42\2\uffff\1\5\22\uffff\1\5\1\uffff\1\5\35\uffff\25\5",
            "\1\75\27\uffff\1\5\14\uffff\1\74",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\102\27\uffff\1\100\4\uffff\1\77\7\uffff\1\101",
            "\1\104\44\uffff\1\103",
            "\1\105\54\uffff\1\107\5\uffff\10\106\10\uffff\1\110",
            "\1\111",
            "\1\114\27\uffff\1\112\24\uffff\1\116\5\uffff\10\115\2\uffff"+
            "\1\113\5\uffff\1\117",
            "\1\122\1\uffff\6\5\25\uffff\1\5\6\uffff\1\5\3\uffff\1\5\12"+
            "\uffff\10\123\1\124\2\5\2\uffff\3\5\24\uffff\3\5\20\uffff\2"+
            "\5\2\uffff\6\5",
            "\1\151\31\uffff\1\5\2\uffff\1\5\36\uffff\1\5\65\uffff\2\5",
            "\1\5\1\uffff\6\5\25\uffff\1\5\6\uffff\1\5\1\uffff\1\157\14"+
            "\uffff\10\5\1\uffff\2\5\2\uffff\3\5\24\uffff\3\5\20\uffff\2"+
            "\5\2\uffff\6\5",
            "\1\5\10\uffff\1\5\6\uffff\1\5\2\uffff\1\5\35\uffff\1\u0087",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u008c",
            "\1\5\10\uffff\1\5\6\uffff\1\5\2\uffff\1\5\35\uffff\1\u0087",
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

    class DFA135 extends DFA {

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
        public String getDescription() {
            return "1018:1: forControl options {k=3; } : ( forVarControl | ( forInit )? ';' ( expression )? ';' ( forUpdate )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA135_63 = input.LA(1);

                         
                        int index135_63 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_63);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA135_64 = input.LA(1);

                         
                        int index135_64 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_64);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA135_65 = input.LA(1);

                         
                        int index135_65 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_65);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA135_66 = input.LA(1);

                         
                        int index135_66 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_66);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA135_67 = input.LA(1);

                         
                        int index135_67 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_67);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA135_68 = input.LA(1);

                         
                        int index135_68 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_68);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA135_69 = input.LA(1);

                         
                        int index135_69 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_69);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA135_70 = input.LA(1);

                         
                        int index135_70 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_70);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA135_71 = input.LA(1);

                         
                        int index135_71 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_71);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA135_72 = input.LA(1);

                         
                        int index135_72 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_72);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA135_73 = input.LA(1);

                         
                        int index135_73 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_73);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA135_74 = input.LA(1);

                         
                        int index135_74 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_74);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA135_75 = input.LA(1);

                         
                        int index135_75 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_75);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA135_76 = input.LA(1);

                         
                        int index135_76 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_76);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA135_77 = input.LA(1);

                         
                        int index135_77 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_77);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA135_78 = input.LA(1);

                         
                        int index135_78 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_78);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA135_79 = input.LA(1);

                         
                        int index135_79 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_79);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA135_82 = input.LA(1);

                         
                        int index135_82 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_82);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA135_83 = input.LA(1);

                         
                        int index135_83 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_83);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA135_84 = input.LA(1);

                         
                        int index135_84 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_84);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA135_105 = input.LA(1);

                         
                        int index135_105 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_105);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA135_111 = input.LA(1);

                         
                        int index135_111 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_111);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA135_140 = input.LA(1);

                         
                        int index135_140 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred191_Java()) ) {s = 135;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index135_140);
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
    static final String DFA132_eotS =
        "\33\uffff";
    static final String DFA132_eofS =
        "\33\uffff";
    static final String DFA132_minS =
        "\1\4\32\uffff";
    static final String DFA132_maxS =
        "\1\167\32\uffff";
    static final String DFA132_acceptS =
        "\1\uffff\1\1\30\uffff\1\2";
    static final String DFA132_specialS =
        "\33\uffff}>";
    static final String[] DFA132_transitionS = {
            "\1\1\1\uffff\6\1\15\uffff\1\32\7\uffff\1\1\6\uffff\1\1\10\uffff"+
            "\1\1\5\uffff\10\1\1\uffff\2\1\2\uffff\4\1\23\uffff\3\1\20\uffff"+
            "\2\1\2\uffff\6\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA132_eot = DFA.unpackEncodedString(DFA132_eotS);
    static final short[] DFA132_eof = DFA.unpackEncodedString(DFA132_eofS);
    static final char[] DFA132_min = DFA.unpackEncodedStringToUnsignedChars(DFA132_minS);
    static final char[] DFA132_max = DFA.unpackEncodedStringToUnsignedChars(DFA132_maxS);
    static final short[] DFA132_accept = DFA.unpackEncodedString(DFA132_acceptS);
    static final short[] DFA132_special = DFA.unpackEncodedString(DFA132_specialS);
    static final short[][] DFA132_transition;

    static {
        int numStates = DFA132_transitionS.length;
        DFA132_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA132_transition[i] = DFA.unpackEncodedString(DFA132_transitionS[i]);
        }
    }

    class DFA132 extends DFA {

        public DFA132(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 132;
            this.eot = DFA132_eot;
            this.eof = DFA132_eof;
            this.min = DFA132_min;
            this.max = DFA132_max;
            this.accept = DFA132_accept;
            this.special = DFA132_special;
            this.transition = DFA132_transition;
        }
        public String getDescription() {
            return "1030:7: ( forInit )?";
        }
    }
    static final String DFA133_eotS =
        "\31\uffff";
    static final String DFA133_eofS =
        "\31\uffff";
    static final String DFA133_minS =
        "\1\4\30\uffff";
    static final String DFA133_maxS =
        "\1\167\30\uffff";
    static final String DFA133_acceptS =
        "\1\uffff\1\1\26\uffff\1\2";
    static final String DFA133_specialS =
        "\31\uffff}>";
    static final String[] DFA133_transitionS = {
            "\1\1\1\uffff\6\1\15\uffff\1\30\7\uffff\1\1\6\uffff\1\1\16\uffff"+
            "\10\1\1\uffff\2\1\2\uffff\3\1\24\uffff\3\1\20\uffff\2\1\2\uffff"+
            "\6\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA133_eot = DFA.unpackEncodedString(DFA133_eotS);
    static final short[] DFA133_eof = DFA.unpackEncodedString(DFA133_eofS);
    static final char[] DFA133_min = DFA.unpackEncodedStringToUnsignedChars(DFA133_minS);
    static final char[] DFA133_max = DFA.unpackEncodedStringToUnsignedChars(DFA133_maxS);
    static final short[] DFA133_accept = DFA.unpackEncodedString(DFA133_acceptS);
    static final short[] DFA133_special = DFA.unpackEncodedString(DFA133_specialS);
    static final short[][] DFA133_transition;

    static {
        int numStates = DFA133_transitionS.length;
        DFA133_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA133_transition[i] = DFA.unpackEncodedString(DFA133_transitionS[i]);
        }
    }

    class DFA133 extends DFA {

        public DFA133(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 133;
            this.eot = DFA133_eot;
            this.eof = DFA133_eof;
            this.min = DFA133_min;
            this.max = DFA133_max;
            this.accept = DFA133_accept;
            this.special = DFA133_special;
            this.transition = DFA133_transition;
        }
        public String getDescription() {
            return "1030:20: ( expression )?";
        }
    }
    static final String DFA134_eotS =
        "\31\uffff";
    static final String DFA134_eofS =
        "\1\30\30\uffff";
    static final String DFA134_minS =
        "\1\4\30\uffff";
    static final String DFA134_maxS =
        "\1\167\30\uffff";
    static final String DFA134_acceptS =
        "\1\uffff\1\1\26\uffff\1\2";
    static final String DFA134_specialS =
        "\31\uffff}>";
    static final String[] DFA134_transitionS = {
            "\1\1\1\uffff\6\1\25\uffff\1\1\6\uffff\1\1\16\uffff\10\1\1\uffff"+
            "\2\1\2\uffff\3\1\24\uffff\3\1\20\uffff\2\1\2\uffff\6\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA134_eot = DFA.unpackEncodedString(DFA134_eotS);
    static final short[] DFA134_eof = DFA.unpackEncodedString(DFA134_eofS);
    static final char[] DFA134_min = DFA.unpackEncodedStringToUnsignedChars(DFA134_minS);
    static final char[] DFA134_max = DFA.unpackEncodedStringToUnsignedChars(DFA134_maxS);
    static final short[] DFA134_accept = DFA.unpackEncodedString(DFA134_acceptS);
    static final short[] DFA134_special = DFA.unpackEncodedString(DFA134_specialS);
    static final short[][] DFA134_transition;

    static {
        int numStates = DFA134_transitionS.length;
        DFA134_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA134_transition[i] = DFA.unpackEncodedString(DFA134_transitionS[i]);
        }
    }

    class DFA134 extends DFA {

        public DFA134(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 134;
            this.eot = DFA134_eot;
            this.eof = DFA134_eof;
            this.min = DFA134_min;
            this.max = DFA134_max;
            this.accept = DFA134_accept;
            this.special = DFA134_special;
            this.transition = DFA134_transition;
        }
        public String getDescription() {
            return "1030:36: ( forUpdate )?";
        }
    }
    static final String DFA137_eotS =
        "\72\uffff";
    static final String DFA137_eofS =
        "\3\uffff\1\5\66\uffff";
    static final String DFA137_minS =
        "\1\4\2\uffff\2\4\25\uffff\3\0\32\uffff\1\0\2\uffff";
    static final String DFA137_maxS =
        "\1\167\2\uffff\1\163\1\51\25\uffff\3\0\32\uffff\1\0\2\uffff";
    static final String DFA137_acceptS =
        "\1\uffff\1\1\3\uffff\1\2\64\uffff";
    static final String DFA137_specialS =
        "\32\uffff\1\0\1\1\1\2\32\uffff\1\3\2\uffff}>";
    static final String[] DFA137_transitionS = {
            "\1\3\1\uffff\6\5\25\uffff\1\5\6\uffff\1\5\10\uffff\1\1\5\uffff"+
            "\10\4\1\uffff\2\5\2\uffff\3\5\1\1\23\uffff\3\5\20\uffff\2\5"+
            "\2\uffff\6\5",
            "",
            "",
            "\1\1\24\uffff\1\5\2\uffff\1\33\1\5\3\uffff\1\32\3\5\4\uffff"+
            "\1\34\2\uffff\1\5\22\uffff\1\5\1\uffff\1\5\35\uffff\25\5",
            "\1\1\27\uffff\1\5\14\uffff\1\67",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA137_eot = DFA.unpackEncodedString(DFA137_eotS);
    static final short[] DFA137_eof = DFA.unpackEncodedString(DFA137_eofS);
    static final char[] DFA137_min = DFA.unpackEncodedStringToUnsignedChars(DFA137_minS);
    static final char[] DFA137_max = DFA.unpackEncodedStringToUnsignedChars(DFA137_maxS);
    static final short[] DFA137_accept = DFA.unpackEncodedString(DFA137_acceptS);
    static final short[] DFA137_special = DFA.unpackEncodedString(DFA137_specialS);
    static final short[][] DFA137_transition;

    static {
        int numStates = DFA137_transitionS.length;
        DFA137_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA137_transition[i] = DFA.unpackEncodedString(DFA137_transitionS[i]);
        }
    }

    class DFA137 extends DFA {

        public DFA137(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 137;
            this.eot = DFA137_eot;
            this.eof = DFA137_eof;
            this.min = DFA137_min;
            this.max = DFA137_max;
            this.accept = DFA137_accept;
            this.special = DFA137_special;
            this.transition = DFA137_transition;
        }
        public String getDescription() {
            return "1033:1: forInit : ( ( variableModifier )* type variableDeclarators | expressionList );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA137_26 = input.LA(1);

                         
                        int index137_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred196_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index137_26);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA137_27 = input.LA(1);

                         
                        int index137_27 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred196_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index137_27);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA137_28 = input.LA(1);

                         
                        int index137_28 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred196_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index137_28);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA137_55 = input.LA(1);

                         
                        int index137_55 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred196_Java()) ) {s = 1;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index137_55);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 137, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA140_eotS =
        "\u00e5\uffff";
    static final String DFA140_eofS =
        "\1\14\u00e4\uffff";
    static final String DFA140_minS =
        "\1\31\13\0\u00d9\uffff";
    static final String DFA140_maxS =
        "\1\146\13\0\u00d9\uffff";
    static final String DFA140_acceptS =
        "\14\uffff\1\2\35\uffff\1\1\u00ba\uffff";
    static final String DFA140_specialS =
        "\1\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\u00d9\uffff}>";
    static final String[] DFA140_transitionS = {
            "\1\14\7\uffff\1\12\1\14\1\13\2\uffff\1\14\3\uffff\1\14\1\uffff"+
            "\1\1\25\uffff\1\14\7\uffff\1\14\24\uffff\1\2\1\3\1\4\1\5\1\6"+
            "\1\7\1\10\1\11",
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
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    class DFA140 extends DFA {

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
        public String getDescription() {
            return "1097:29: ( assignmentOperator expression )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA140_1 = input.LA(1);

                         
                        int index140_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred199_Java()) ) {s = 42;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index140_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA140_2 = input.LA(1);

                         
                        int index140_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred199_Java()) ) {s = 42;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index140_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA140_3 = input.LA(1);

                         
                        int index140_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred199_Java()) ) {s = 42;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index140_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA140_4 = input.LA(1);

                         
                        int index140_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred199_Java()) ) {s = 42;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index140_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA140_5 = input.LA(1);

                         
                        int index140_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred199_Java()) ) {s = 42;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index140_5);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA140_6 = input.LA(1);

                         
                        int index140_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred199_Java()) ) {s = 42;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index140_6);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA140_7 = input.LA(1);

                         
                        int index140_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred199_Java()) ) {s = 42;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index140_7);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA140_8 = input.LA(1);

                         
                        int index140_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred199_Java()) ) {s = 42;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index140_8);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA140_9 = input.LA(1);

                         
                        int index140_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred199_Java()) ) {s = 42;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index140_9);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA140_10 = input.LA(1);

                         
                        int index140_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred199_Java()) ) {s = 42;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index140_10);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA140_11 = input.LA(1);

                         
                        int index140_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred199_Java()) ) {s = 42;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index140_11);
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
    static final String DFA141_eotS =
        "\17\uffff";
    static final String DFA141_eofS =
        "\17\uffff";
    static final String DFA141_minS =
        "\1\41\12\uffff\1\43\1\0\2\uffff";
    static final String DFA141_maxS =
        "\1\146\12\uffff\1\43\1\0\2\uffff";
    static final String DFA141_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\2\uffff\1\13"+
        "\1\14";
    static final String DFA141_specialS =
        "\14\uffff\1\0\2\uffff}>";
    static final String[] DFA141_transitionS = {
            "\1\12\1\uffff\1\13\10\uffff\1\1\62\uffff\1\2\1\3\1\4\1\5\1\6"+
            "\1\7\1\10\1\11",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\14",
            "\1\uffff",
            "",
            ""
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

    class DFA141 extends DFA {

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
        public String getDescription() {
            return "1100:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '=' | '>' '>' '>' '=' );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA141_12 = input.LA(1);

                         
                        int index141_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred210_Java()) ) {s = 13;}

                        else if ( (true) ) {s = 14;}

                         
                        input.seek(index141_12);
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
    static final String DFA142_eotS =
        "\24\uffff";
    static final String DFA142_eofS =
        "\1\2\23\uffff";
    static final String DFA142_minS =
        "\1\31\23\uffff";
    static final String DFA142_maxS =
        "\1\146\23\uffff";
    static final String DFA142_acceptS =
        "\1\uffff\1\1\1\2\21\uffff";
    static final String DFA142_specialS =
        "\24\uffff}>";
    static final String[] DFA142_transitionS = {
            "\1\2\7\uffff\3\2\2\uffff\1\2\3\uffff\1\2\1\uffff\1\2\22\uffff"+
            "\1\1\2\uffff\1\2\7\uffff\1\2\24\uffff\10\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA142_eot = DFA.unpackEncodedString(DFA142_eotS);
    static final short[] DFA142_eof = DFA.unpackEncodedString(DFA142_eofS);
    static final char[] DFA142_min = DFA.unpackEncodedStringToUnsignedChars(DFA142_minS);
    static final char[] DFA142_max = DFA.unpackEncodedStringToUnsignedChars(DFA142_maxS);
    static final short[] DFA142_accept = DFA.unpackEncodedString(DFA142_acceptS);
    static final short[] DFA142_special = DFA.unpackEncodedString(DFA142_specialS);
    static final short[][] DFA142_transition;

    static {
        int numStates = DFA142_transitionS.length;
        DFA142_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA142_transition[i] = DFA.unpackEncodedString(DFA142_transitionS[i]);
        }
    }

    class DFA142 extends DFA {

        public DFA142(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 142;
            this.eot = DFA142_eot;
            this.eof = DFA142_eof;
            this.min = DFA142_min;
            this.max = DFA142_max;
            this.accept = DFA142_accept;
            this.special = DFA142_special;
            this.transition = DFA142_transition;
        }
        public String getDescription() {
            return "1116:33: ( '?' expression ':' expression )?";
        }
    }
    static final String DFA143_eotS =
        "\25\uffff";
    static final String DFA143_eofS =
        "\1\1\24\uffff";
    static final String DFA143_minS =
        "\1\31\24\uffff";
    static final String DFA143_maxS =
        "\1\147\24\uffff";
    static final String DFA143_acceptS =
        "\1\uffff\1\2\22\uffff\1\1";
    static final String DFA143_specialS =
        "\25\uffff}>";
    static final String[] DFA143_transitionS = {
            "\1\1\7\uffff\3\1\2\uffff\1\1\3\uffff\1\1\1\uffff\1\1\22\uffff"+
            "\1\1\2\uffff\1\1\7\uffff\1\1\24\uffff\10\1\1\24",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA143_eot = DFA.unpackEncodedString(DFA143_eotS);
    static final short[] DFA143_eof = DFA.unpackEncodedString(DFA143_eofS);
    static final char[] DFA143_min = DFA.unpackEncodedStringToUnsignedChars(DFA143_minS);
    static final char[] DFA143_max = DFA.unpackEncodedStringToUnsignedChars(DFA143_maxS);
    static final short[] DFA143_accept = DFA.unpackEncodedString(DFA143_acceptS);
    static final short[] DFA143_special = DFA.unpackEncodedString(DFA143_specialS);
    static final short[][] DFA143_transition;

    static {
        int numStates = DFA143_transitionS.length;
        DFA143_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA143_transition[i] = DFA.unpackEncodedString(DFA143_transitionS[i]);
        }
    }

    class DFA143 extends DFA {

        public DFA143(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 143;
            this.eot = DFA143_eot;
            this.eof = DFA143_eof;
            this.min = DFA143_min;
            this.max = DFA143_max;
            this.accept = DFA143_accept;
            this.special = DFA143_special;
            this.transition = DFA143_transition;
        }
        public String getDescription() {
            return "()* loopback of 1120:34: ( '||' conditionalAndExpression )*";
        }
    }
    static final String DFA144_eotS =
        "\26\uffff";
    static final String DFA144_eofS =
        "\1\1\25\uffff";
    static final String DFA144_minS =
        "\1\31\25\uffff";
    static final String DFA144_maxS =
        "\1\150\25\uffff";
    static final String DFA144_acceptS =
        "\1\uffff\1\2\23\uffff\1\1";
    static final String DFA144_specialS =
        "\26\uffff}>";
    static final String[] DFA144_transitionS = {
            "\1\1\7\uffff\3\1\2\uffff\1\1\3\uffff\1\1\1\uffff\1\1\22\uffff"+
            "\1\1\2\uffff\1\1\7\uffff\1\1\24\uffff\11\1\1\25",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA144_eot = DFA.unpackEncodedString(DFA144_eotS);
    static final short[] DFA144_eof = DFA.unpackEncodedString(DFA144_eofS);
    static final char[] DFA144_min = DFA.unpackEncodedStringToUnsignedChars(DFA144_minS);
    static final char[] DFA144_max = DFA.unpackEncodedStringToUnsignedChars(DFA144_maxS);
    static final short[] DFA144_accept = DFA.unpackEncodedString(DFA144_acceptS);
    static final short[] DFA144_special = DFA.unpackEncodedString(DFA144_specialS);
    static final short[][] DFA144_transition;

    static {
        int numStates = DFA144_transitionS.length;
        DFA144_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA144_transition[i] = DFA.unpackEncodedString(DFA144_transitionS[i]);
        }
    }

    class DFA144 extends DFA {

        public DFA144(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 144;
            this.eot = DFA144_eot;
            this.eof = DFA144_eof;
            this.min = DFA144_min;
            this.max = DFA144_max;
            this.accept = DFA144_accept;
            this.special = DFA144_special;
            this.transition = DFA144_transition;
        }
        public String getDescription() {
            return "()* loopback of 1124:31: ( '&&' inclusiveOrExpression )*";
        }
    }
    static final String DFA145_eotS =
        "\27\uffff";
    static final String DFA145_eofS =
        "\1\1\26\uffff";
    static final String DFA145_minS =
        "\1\31\26\uffff";
    static final String DFA145_maxS =
        "\1\151\26\uffff";
    static final String DFA145_acceptS =
        "\1\uffff\1\2\24\uffff\1\1";
    static final String DFA145_specialS =
        "\27\uffff}>";
    static final String[] DFA145_transitionS = {
            "\1\1\7\uffff\3\1\2\uffff\1\1\3\uffff\1\1\1\uffff\1\1\22\uffff"+
            "\1\1\2\uffff\1\1\7\uffff\1\1\24\uffff\12\1\1\26",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA145_eot = DFA.unpackEncodedString(DFA145_eotS);
    static final short[] DFA145_eof = DFA.unpackEncodedString(DFA145_eofS);
    static final char[] DFA145_min = DFA.unpackEncodedStringToUnsignedChars(DFA145_minS);
    static final char[] DFA145_max = DFA.unpackEncodedStringToUnsignedChars(DFA145_maxS);
    static final short[] DFA145_accept = DFA.unpackEncodedString(DFA145_acceptS);
    static final short[] DFA145_special = DFA.unpackEncodedString(DFA145_specialS);
    static final short[][] DFA145_transition;

    static {
        int numStates = DFA145_transitionS.length;
        DFA145_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA145_transition[i] = DFA.unpackEncodedString(DFA145_transitionS[i]);
        }
    }

    class DFA145 extends DFA {

        public DFA145(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 145;
            this.eot = DFA145_eot;
            this.eof = DFA145_eof;
            this.min = DFA145_min;
            this.max = DFA145_max;
            this.accept = DFA145_accept;
            this.special = DFA145_special;
            this.transition = DFA145_transition;
        }
        public String getDescription() {
            return "()* loopback of 1128:31: ( '|' exclusiveOrExpression )*";
        }
    }
    static final String DFA146_eotS =
        "\30\uffff";
    static final String DFA146_eofS =
        "\1\1\27\uffff";
    static final String DFA146_minS =
        "\1\31\27\uffff";
    static final String DFA146_maxS =
        "\1\152\27\uffff";
    static final String DFA146_acceptS =
        "\1\uffff\1\2\25\uffff\1\1";
    static final String DFA146_specialS =
        "\30\uffff}>";
    static final String[] DFA146_transitionS = {
            "\1\1\7\uffff\3\1\2\uffff\1\1\3\uffff\1\1\1\uffff\1\1\22\uffff"+
            "\1\1\2\uffff\1\1\7\uffff\1\1\24\uffff\13\1\1\27",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA146_eot = DFA.unpackEncodedString(DFA146_eotS);
    static final short[] DFA146_eof = DFA.unpackEncodedString(DFA146_eofS);
    static final char[] DFA146_min = DFA.unpackEncodedStringToUnsignedChars(DFA146_minS);
    static final char[] DFA146_max = DFA.unpackEncodedStringToUnsignedChars(DFA146_maxS);
    static final short[] DFA146_accept = DFA.unpackEncodedString(DFA146_acceptS);
    static final short[] DFA146_special = DFA.unpackEncodedString(DFA146_specialS);
    static final short[][] DFA146_transition;

    static {
        int numStates = DFA146_transitionS.length;
        DFA146_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA146_transition[i] = DFA.unpackEncodedString(DFA146_transitionS[i]);
        }
    }

    class DFA146 extends DFA {

        public DFA146(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 146;
            this.eot = DFA146_eot;
            this.eof = DFA146_eof;
            this.min = DFA146_min;
            this.max = DFA146_max;
            this.accept = DFA146_accept;
            this.special = DFA146_special;
            this.transition = DFA146_transition;
        }
        public String getDescription() {
            return "()* loopback of 1132:23: ( '^' andExpression )*";
        }
    }
    static final String DFA147_eotS =
        "\31\uffff";
    static final String DFA147_eofS =
        "\1\1\30\uffff";
    static final String DFA147_minS =
        "\1\31\30\uffff";
    static final String DFA147_maxS =
        "\1\152\30\uffff";
    static final String DFA147_acceptS =
        "\1\uffff\1\2\26\uffff\1\1";
    static final String DFA147_specialS =
        "\31\uffff}>";
    static final String[] DFA147_transitionS = {
            "\1\1\7\uffff\3\1\1\30\1\uffff\1\1\3\uffff\1\1\1\uffff\1\1\22"+
            "\uffff\1\1\2\uffff\1\1\7\uffff\1\1\24\uffff\14\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA147_eot = DFA.unpackEncodedString(DFA147_eotS);
    static final short[] DFA147_eof = DFA.unpackEncodedString(DFA147_eofS);
    static final char[] DFA147_min = DFA.unpackEncodedStringToUnsignedChars(DFA147_minS);
    static final char[] DFA147_max = DFA.unpackEncodedStringToUnsignedChars(DFA147_maxS);
    static final short[] DFA147_accept = DFA.unpackEncodedString(DFA147_acceptS);
    static final short[] DFA147_special = DFA.unpackEncodedString(DFA147_specialS);
    static final short[][] DFA147_transition;

    static {
        int numStates = DFA147_transitionS.length;
        DFA147_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA147_transition[i] = DFA.unpackEncodedString(DFA147_transitionS[i]);
        }
    }

    class DFA147 extends DFA {

        public DFA147(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 147;
            this.eot = DFA147_eot;
            this.eof = DFA147_eof;
            this.min = DFA147_min;
            this.max = DFA147_max;
            this.accept = DFA147_accept;
            this.special = DFA147_special;
            this.transition = DFA147_transition;
        }
        public String getDescription() {
            return "()* loopback of 1136:28: ( '&' equalityExpression )*";
        }
    }
    static final String DFA148_eotS =
        "\32\uffff";
    static final String DFA148_eofS =
        "\1\1\31\uffff";
    static final String DFA148_minS =
        "\1\31\31\uffff";
    static final String DFA148_maxS =
        "\1\154\31\uffff";
    static final String DFA148_acceptS =
        "\1\uffff\1\2\27\uffff\1\1";
    static final String DFA148_specialS =
        "\32\uffff}>";
    static final String[] DFA148_transitionS = {
            "\1\1\7\uffff\4\1\1\uffff\1\1\3\uffff\1\1\1\uffff\1\1\22\uffff"+
            "\1\1\2\uffff\1\1\7\uffff\1\1\24\uffff\14\1\2\31",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA148_eot = DFA.unpackEncodedString(DFA148_eotS);
    static final short[] DFA148_eof = DFA.unpackEncodedString(DFA148_eofS);
    static final char[] DFA148_min = DFA.unpackEncodedStringToUnsignedChars(DFA148_minS);
    static final char[] DFA148_max = DFA.unpackEncodedStringToUnsignedChars(DFA148_maxS);
    static final short[] DFA148_accept = DFA.unpackEncodedString(DFA148_acceptS);
    static final short[] DFA148_special = DFA.unpackEncodedString(DFA148_specialS);
    static final short[][] DFA148_transition;

    static {
        int numStates = DFA148_transitionS.length;
        DFA148_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA148_transition[i] = DFA.unpackEncodedString(DFA148_transitionS[i]);
        }
    }

    class DFA148 extends DFA {

        public DFA148(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 148;
            this.eot = DFA148_eot;
            this.eof = DFA148_eof;
            this.min = DFA148_min;
            this.max = DFA148_max;
            this.accept = DFA148_accept;
            this.special = DFA148_special;
            this.transition = DFA148_transition;
        }
        public String getDescription() {
            return "()* loopback of 1140:30: ( ( '==' | '!=' ) instanceOfExpression )*";
        }
    }
    static final String DFA149_eotS =
        "\33\uffff";
    static final String DFA149_eofS =
        "\1\2\32\uffff";
    static final String DFA149_minS =
        "\1\31\32\uffff";
    static final String DFA149_maxS =
        "\1\155\32\uffff";
    static final String DFA149_acceptS =
        "\1\uffff\1\1\1\2\30\uffff";
    static final String DFA149_specialS =
        "\33\uffff}>";
    static final String[] DFA149_transitionS = {
            "\1\2\7\uffff\4\2\1\uffff\1\2\3\uffff\1\2\1\uffff\1\2\22\uffff"+
            "\1\2\2\uffff\1\2\7\uffff\1\2\24\uffff\16\2\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA149_eot = DFA.unpackEncodedString(DFA149_eotS);
    static final short[] DFA149_eof = DFA.unpackEncodedString(DFA149_eofS);
    static final char[] DFA149_min = DFA.unpackEncodedStringToUnsignedChars(DFA149_minS);
    static final char[] DFA149_max = DFA.unpackEncodedStringToUnsignedChars(DFA149_maxS);
    static final short[] DFA149_accept = DFA.unpackEncodedString(DFA149_acceptS);
    static final short[] DFA149_special = DFA.unpackEncodedString(DFA149_specialS);
    static final short[][] DFA149_transition;

    static {
        int numStates = DFA149_transitionS.length;
        DFA149_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA149_transition[i] = DFA.unpackEncodedString(DFA149_transitionS[i]);
        }
    }

    class DFA149 extends DFA {

        public DFA149(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 149;
            this.eot = DFA149_eot;
            this.eof = DFA149_eof;
            this.min = DFA149_min;
            this.max = DFA149_max;
            this.accept = DFA149_accept;
            this.special = DFA149_special;
            this.transition = DFA149_transition;
        }
        public String getDescription() {
            return "1144:30: ( 'instanceof' type )?";
        }
    }
    static final String DFA150_eotS =
        "\114\uffff";
    static final String DFA150_eofS =
        "\1\1\113\uffff";
    static final String DFA150_minS =
        "\1\31\26\uffff\2\4\2\uffff\1\0\60\uffff";
    static final String DFA150_maxS =
        "\1\155\26\uffff\2\167\2\uffff\1\0\60\uffff";
    static final String DFA150_acceptS =
        "\1\uffff\1\2\32\uffff\1\1\57\uffff";
    static final String DFA150_specialS =
        "\33\uffff\1\0\60\uffff}>";
    static final String[] DFA150_transitionS = {
            "\1\1\7\uffff\1\27\1\1\1\30\1\1\1\uffff\1\1\3\uffff\1\1\1\uffff"+
            "\1\1\22\uffff\1\1\2\uffff\1\1\7\uffff\1\1\24\uffff\17\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\34\1\uffff\6\34\25\uffff\1\33\6\uffff\1\34\3\uffff\1\34"+
            "\12\uffff\10\34\1\uffff\2\34\2\uffff\3\34\24\uffff\3\34\20\uffff"+
            "\2\34\2\uffff\6\34",
            "\1\34\1\uffff\6\34\25\uffff\1\34\1\uffff\1\1\4\uffff\1\34\3"+
            "\uffff\1\34\12\uffff\10\34\1\uffff\2\34\2\uffff\3\34\24\uffff"+
            "\3\34\20\uffff\2\34\2\uffff\6\34",
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

    static final short[] DFA150_eot = DFA.unpackEncodedString(DFA150_eotS);
    static final short[] DFA150_eof = DFA.unpackEncodedString(DFA150_eofS);
    static final char[] DFA150_min = DFA.unpackEncodedStringToUnsignedChars(DFA150_minS);
    static final char[] DFA150_max = DFA.unpackEncodedStringToUnsignedChars(DFA150_maxS);
    static final short[] DFA150_accept = DFA.unpackEncodedString(DFA150_acceptS);
    static final short[] DFA150_special = DFA.unpackEncodedString(DFA150_specialS);
    static final short[][] DFA150_transition;

    static {
        int numStates = DFA150_transitionS.length;
        DFA150_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA150_transition[i] = DFA.unpackEncodedString(DFA150_transitionS[i]);
        }
    }

    class DFA150 extends DFA {

        public DFA150(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 150;
            this.eot = DFA150_eot;
            this.eof = DFA150_eof;
            this.min = DFA150_min;
            this.max = DFA150_max;
            this.accept = DFA150_accept;
            this.special = DFA150_special;
            this.transition = DFA150_transition;
        }
        public String getDescription() {
            return "()* loopback of 1148:25: ( relationalOp shiftExpression )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA150_27 = input.LA(1);

                         
                        int index150_27 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred220_Java()) ) {s = 28;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index150_27);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 150, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA151_eotS =
        "\63\uffff";
    static final String DFA151_eofS =
        "\63\uffff";
    static final String DFA151_minS =
        "\1\41\2\4\60\uffff";
    static final String DFA151_maxS =
        "\1\43\2\167\60\uffff";
    static final String DFA151_acceptS =
        "\3\uffff\1\1\1\3\26\uffff\1\2\1\4\26\uffff";
    static final String DFA151_specialS =
        "\63\uffff}>";
    static final String[] DFA151_transitionS = {
            "\1\1\1\uffff\1\2",
            "\1\4\1\uffff\6\4\25\uffff\1\4\6\uffff\1\4\3\uffff\1\3\12\uffff"+
            "\10\4\1\uffff\2\4\2\uffff\3\4\24\uffff\3\4\20\uffff\2\4\2\uffff"+
            "\6\4",
            "\1\34\1\uffff\6\34\25\uffff\1\34\6\uffff\1\34\3\uffff\1\33"+
            "\12\uffff\10\34\1\uffff\2\34\2\uffff\3\34\24\uffff\3\34\20\uffff"+
            "\2\34\2\uffff\6\34",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    class DFA151 extends DFA {

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
        public String getDescription() {
            return "1152:7: ( '<' '=' | '>' '=' | '<' | '>' )";
        }
    }
    static final String DFA152_eotS =
        "\115\uffff";
    static final String DFA152_eofS =
        "\1\3\114\uffff";
    static final String DFA152_minS =
        "\1\31\2\4\31\uffff\1\0\27\uffff\1\0\30\uffff";
    static final String DFA152_maxS =
        "\1\155\2\167\31\uffff\1\0\27\uffff\1\0\30\uffff";
    static final String DFA152_acceptS =
        "\3\uffff\1\2\110\uffff\1\1";
    static final String DFA152_specialS =
        "\34\uffff\1\0\27\uffff\1\1\30\uffff}>";
    static final String[] DFA152_transitionS = {
            "\1\3\7\uffff\1\1\1\3\1\2\1\3\1\uffff\1\3\3\uffff\1\3\1\uffff"+
            "\1\3\22\uffff\1\3\2\uffff\1\3\7\uffff\1\3\24\uffff\17\3",
            "\1\3\1\uffff\6\3\25\uffff\1\34\6\uffff\1\3\3\uffff\1\3\12\uffff"+
            "\10\3\1\uffff\2\3\2\uffff\3\3\24\uffff\3\3\20\uffff\2\3\2\uffff"+
            "\6\3",
            "\1\3\1\uffff\6\3\25\uffff\1\3\1\uffff\1\64\4\uffff\1\3\3\uffff"+
            "\1\3\12\uffff\10\3\1\uffff\2\3\2\uffff\3\3\24\uffff\3\3\20\uffff"+
            "\2\3\2\uffff\6\3",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA152_eot = DFA.unpackEncodedString(DFA152_eotS);
    static final short[] DFA152_eof = DFA.unpackEncodedString(DFA152_eofS);
    static final char[] DFA152_min = DFA.unpackEncodedStringToUnsignedChars(DFA152_minS);
    static final char[] DFA152_max = DFA.unpackEncodedStringToUnsignedChars(DFA152_maxS);
    static final short[] DFA152_accept = DFA.unpackEncodedString(DFA152_acceptS);
    static final short[] DFA152_special = DFA.unpackEncodedString(DFA152_specialS);
    static final short[][] DFA152_transition;

    static {
        int numStates = DFA152_transitionS.length;
        DFA152_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA152_transition[i] = DFA.unpackEncodedString(DFA152_transitionS[i]);
        }
    }

    class DFA152 extends DFA {

        public DFA152(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 152;
            this.eot = DFA152_eot;
            this.eof = DFA152_eof;
            this.min = DFA152_min;
            this.max = DFA152_max;
            this.accept = DFA152_accept;
            this.special = DFA152_special;
            this.transition = DFA152_transition;
        }
        public String getDescription() {
            return "()* loopback of 1156:28: ( shiftOp additiveExpression )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA152_28 = input.LA(1);

                         
                        int index152_28 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred224_Java()) ) {s = 76;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index152_28);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA152_52 = input.LA(1);

                         
                        int index152_52 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred224_Java()) ) {s = 76;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index152_52);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 152, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA154_eotS =
        "\34\uffff";
    static final String DFA154_eofS =
        "\1\1\33\uffff";
    static final String DFA154_minS =
        "\1\31\33\uffff";
    static final String DFA154_maxS =
        "\1\157\33\uffff";
    static final String DFA154_acceptS =
        "\1\uffff\1\2\31\uffff\1\1";
    static final String DFA154_specialS =
        "\34\uffff}>";
    static final String[] DFA154_transitionS = {
            "\1\1\7\uffff\4\1\1\uffff\1\1\3\uffff\1\1\1\uffff\1\1\22\uffff"+
            "\1\1\2\uffff\1\1\7\uffff\1\1\24\uffff\17\1\2\33",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA154_eot = DFA.unpackEncodedString(DFA154_eotS);
    static final short[] DFA154_eof = DFA.unpackEncodedString(DFA154_eofS);
    static final char[] DFA154_min = DFA.unpackEncodedStringToUnsignedChars(DFA154_minS);
    static final char[] DFA154_max = DFA.unpackEncodedStringToUnsignedChars(DFA154_maxS);
    static final short[] DFA154_accept = DFA.unpackEncodedString(DFA154_acceptS);
    static final short[] DFA154_special = DFA.unpackEncodedString(DFA154_specialS);
    static final short[][] DFA154_transition;

    static {
        int numStates = DFA154_transitionS.length;
        DFA154_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA154_transition[i] = DFA.unpackEncodedString(DFA154_transitionS[i]);
        }
    }

    class DFA154 extends DFA {

        public DFA154(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 154;
            this.eot = DFA154_eot;
            this.eof = DFA154_eof;
            this.min = DFA154_min;
            this.max = DFA154_max;
            this.accept = DFA154_accept;
            this.special = DFA154_special;
            this.transition = DFA154_transition;
        }
        public String getDescription() {
            return "()* loopback of 1166:34: ( ( '+' | '-' ) multiplicativeExpression )*";
        }
    }
    static final String DFA155_eotS =
        "\35\uffff";
    static final String DFA155_eofS =
        "\1\1\34\uffff";
    static final String DFA155_minS =
        "\1\31\34\uffff";
    static final String DFA155_maxS =
        "\1\161\34\uffff";
    static final String DFA155_acceptS =
        "\1\uffff\1\2\32\uffff\1\1";
    static final String DFA155_specialS =
        "\35\uffff}>";
    static final String[] DFA155_transitionS = {
            "\1\1\3\uffff\1\34\3\uffff\4\1\1\uffff\1\1\3\uffff\1\1\1\uffff"+
            "\1\1\22\uffff\1\1\2\uffff\1\1\7\uffff\1\1\24\uffff\21\1\2\34",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA155_eot = DFA.unpackEncodedString(DFA155_eotS);
    static final short[] DFA155_eof = DFA.unpackEncodedString(DFA155_eofS);
    static final char[] DFA155_min = DFA.unpackEncodedStringToUnsignedChars(DFA155_minS);
    static final char[] DFA155_max = DFA.unpackEncodedStringToUnsignedChars(DFA155_maxS);
    static final short[] DFA155_accept = DFA.unpackEncodedString(DFA155_acceptS);
    static final short[] DFA155_special = DFA.unpackEncodedString(DFA155_specialS);
    static final short[][] DFA155_transition;

    static {
        int numStates = DFA155_transitionS.length;
        DFA155_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA155_transition[i] = DFA.unpackEncodedString(DFA155_transitionS[i]);
        }
    }

    class DFA155 extends DFA {

        public DFA155(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 155;
            this.eot = DFA155_eot;
            this.eof = DFA155_eof;
            this.min = DFA155_min;
            this.max = DFA155_max;
            this.accept = DFA155_accept;
            this.special = DFA155_special;
            this.transition = DFA155_transition;
        }
        public String getDescription() {
            return "()* loopback of 1170:25: ( ( '*' | '/' | '%' ) unaryExpression )*";
        }
    }
    static final String DFA156_eotS =
        "\30\uffff";
    static final String DFA156_eofS =
        "\30\uffff";
    static final String DFA156_minS =
        "\1\4\27\uffff";
    static final String DFA156_maxS =
        "\1\167\27\uffff";
    static final String DFA156_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\22\uffff";
    static final String DFA156_specialS =
        "\30\uffff}>";
    static final String[] DFA156_transitionS = {
            "\1\5\1\uffff\6\5\25\uffff\1\5\6\uffff\1\5\16\uffff\10\5\1\uffff"+
            "\2\5\2\uffff\3\5\24\uffff\3\5\20\uffff\1\1\1\2\2\uffff\1\3\1"+
            "\4\4\5",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA156_eot = DFA.unpackEncodedString(DFA156_eotS);
    static final short[] DFA156_eof = DFA.unpackEncodedString(DFA156_eofS);
    static final char[] DFA156_min = DFA.unpackEncodedStringToUnsignedChars(DFA156_minS);
    static final char[] DFA156_max = DFA.unpackEncodedStringToUnsignedChars(DFA156_maxS);
    static final short[] DFA156_accept = DFA.unpackEncodedString(DFA156_acceptS);
    static final short[] DFA156_special = DFA.unpackEncodedString(DFA156_specialS);
    static final short[][] DFA156_transition;

    static {
        int numStates = DFA156_transitionS.length;
        DFA156_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA156_transition[i] = DFA.unpackEncodedString(DFA156_transitionS[i]);
        }
    }

    class DFA156 extends DFA {

        public DFA156(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 156;
            this.eot = DFA156_eot;
            this.eof = DFA156_eof;
            this.min = DFA156_min;
            this.max = DFA156_max;
            this.accept = DFA156_accept;
            this.special = DFA156_special;
            this.transition = DFA156_transition;
        }
        public String getDescription() {
            return "1173:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' primary | '--' primary | unaryExpressionNotPlusMinus );";
        }
    }
    static final String DFA159_eotS =
        "\54\uffff";
    static final String DFA159_eofS =
        "\54\uffff";
    static final String DFA159_minS =
        "\1\4\2\uffff\1\4\20\uffff\27\0\1\uffff";
    static final String DFA159_maxS =
        "\1\167\2\uffff\1\167\20\uffff\27\0\1\uffff";
    static final String DFA159_acceptS =
        "\1\uffff\1\1\1\2\1\uffff\1\4\46\uffff\1\3";
    static final String DFA159_specialS =
        "\24\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1"+
        "\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\uffff}>";
    static final String[] DFA159_transitionS = {
            "\1\4\1\uffff\6\4\25\uffff\1\4\6\uffff\1\4\16\uffff\10\4\1\uffff"+
            "\1\4\1\3\2\uffff\3\4\24\uffff\3\4\26\uffff\1\1\1\2\2\4",
            "",
            "",
            "\1\25\1\uffff\1\44\1\45\1\46\3\43\25\uffff\1\35\6\uffff\1\52"+
            "\16\uffff\10\24\1\uffff\1\37\1\34\2\uffff\1\50\2\47\24\uffff"+
            "\1\40\1\41\1\42\20\uffff\1\26\1\27\2\uffff\1\30\1\31\1\32\1"+
            "\33\1\36\1\51",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            ""
    };

    static final short[] DFA159_eot = DFA.unpackEncodedString(DFA159_eotS);
    static final short[] DFA159_eof = DFA.unpackEncodedString(DFA159_eofS);
    static final char[] DFA159_min = DFA.unpackEncodedStringToUnsignedChars(DFA159_minS);
    static final char[] DFA159_max = DFA.unpackEncodedStringToUnsignedChars(DFA159_maxS);
    static final short[] DFA159_accept = DFA.unpackEncodedString(DFA159_acceptS);
    static final short[] DFA159_special = DFA.unpackEncodedString(DFA159_specialS);
    static final short[][] DFA159_transition;

    static {
        int numStates = DFA159_transitionS.length;
        DFA159_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA159_transition[i] = DFA.unpackEncodedString(DFA159_transitionS[i]);
        }
    }

    class DFA159 extends DFA {

        public DFA159(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 159;
            this.eot = DFA159_eot;
            this.eof = DFA159_eof;
            this.min = DFA159_min;
            this.max = DFA159_max;
            this.accept = DFA159_accept;
            this.special = DFA159_special;
            this.transition = DFA159_transition;
        }
        public String getDescription() {
            return "1181:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA159_20 = input.LA(1);

                         
                        int index159_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_20);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA159_21 = input.LA(1);

                         
                        int index159_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_21);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA159_22 = input.LA(1);

                         
                        int index159_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_22);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA159_23 = input.LA(1);

                         
                        int index159_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_23);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA159_24 = input.LA(1);

                         
                        int index159_24 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_24);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA159_25 = input.LA(1);

                         
                        int index159_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_25);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA159_26 = input.LA(1);

                         
                        int index159_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_26);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA159_27 = input.LA(1);

                         
                        int index159_27 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_27);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA159_28 = input.LA(1);

                         
                        int index159_28 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_28);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA159_29 = input.LA(1);

                         
                        int index159_29 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_29);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA159_30 = input.LA(1);

                         
                        int index159_30 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_30);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA159_31 = input.LA(1);

                         
                        int index159_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_31);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA159_32 = input.LA(1);

                         
                        int index159_32 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_32);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA159_33 = input.LA(1);

                         
                        int index159_33 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_33);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA159_34 = input.LA(1);

                         
                        int index159_34 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_34);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA159_35 = input.LA(1);

                         
                        int index159_35 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_35);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA159_36 = input.LA(1);

                         
                        int index159_36 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_36);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA159_37 = input.LA(1);

                         
                        int index159_37 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_37);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA159_38 = input.LA(1);

                         
                        int index159_38 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_38);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA159_39 = input.LA(1);

                         
                        int index159_39 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_39);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA159_40 = input.LA(1);

                         
                        int index159_40 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_40);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA159_41 = input.LA(1);

                         
                        int index159_41 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_41);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA159_42 = input.LA(1);

                         
                        int index159_42 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred238_Java()) ) {s = 43;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index159_42);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 159, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA157_eotS =
        "\40\uffff";
    static final String DFA157_eofS =
        "\1\1\37\uffff";
    static final String DFA157_minS =
        "\1\31\37\uffff";
    static final String DFA157_maxS =
        "\1\163\37\uffff";
    static final String DFA157_acceptS =
        "\1\uffff\1\2\34\uffff\1\1\1\uffff";
    static final String DFA157_specialS =
        "\40\uffff}>";
    static final String[] DFA157_transitionS = {
            "\1\1\2\uffff\1\36\1\1\3\uffff\4\1\1\uffff\1\1\2\uffff\1\36\1"+
            "\1\1\uffff\1\1\22\uffff\1\1\2\uffff\1\1\7\uffff\1\1\24\uffff"+
            "\25\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA157_eot = DFA.unpackEncodedString(DFA157_eotS);
    static final short[] DFA157_eof = DFA.unpackEncodedString(DFA157_eofS);
    static final char[] DFA157_min = DFA.unpackEncodedStringToUnsignedChars(DFA157_minS);
    static final char[] DFA157_max = DFA.unpackEncodedStringToUnsignedChars(DFA157_maxS);
    static final short[] DFA157_accept = DFA.unpackEncodedString(DFA157_acceptS);
    static final short[] DFA157_special = DFA.unpackEncodedString(DFA157_specialS);
    static final short[][] DFA157_transition;

    static {
        int numStates = DFA157_transitionS.length;
        DFA157_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA157_transition[i] = DFA.unpackEncodedString(DFA157_transitionS[i]);
        }
    }

    class DFA157 extends DFA {

        public DFA157(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 157;
            this.eot = DFA157_eot;
            this.eof = DFA157_eof;
            this.min = DFA157_min;
            this.max = DFA157_max;
            this.accept = DFA157_accept;
            this.special = DFA157_special;
            this.transition = DFA157_transition;
        }
        public String getDescription() {
            return "()* loopback of 1185:17: ( selector )*";
        }
    }
    static final String DFA158_eotS =
        "\36\uffff";
    static final String DFA158_eofS =
        "\1\2\35\uffff";
    static final String DFA158_minS =
        "\1\31\35\uffff";
    static final String DFA158_maxS =
        "\1\163\35\uffff";
    static final String DFA158_acceptS =
        "\1\uffff\1\1\1\2\33\uffff";
    static final String DFA158_specialS =
        "\36\uffff}>";
    static final String[] DFA158_transitionS = {
            "\1\2\3\uffff\1\2\3\uffff\4\2\1\uffff\1\2\3\uffff\1\2\1\uffff"+
            "\1\2\22\uffff\1\2\2\uffff\1\2\7\uffff\1\2\24\uffff\23\2\2\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA158_eot = DFA.unpackEncodedString(DFA158_eotS);
    static final short[] DFA158_eof = DFA.unpackEncodedString(DFA158_eofS);
    static final char[] DFA158_min = DFA.unpackEncodedStringToUnsignedChars(DFA158_minS);
    static final char[] DFA158_max = DFA.unpackEncodedStringToUnsignedChars(DFA158_maxS);
    static final short[] DFA158_accept = DFA.unpackEncodedString(DFA158_acceptS);
    static final short[] DFA158_special = DFA.unpackEncodedString(DFA158_specialS);
    static final short[][] DFA158_transition;

    static {
        int numStates = DFA158_transitionS.length;
        DFA158_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA158_transition[i] = DFA.unpackEncodedString(DFA158_transitionS[i]);
        }
    }

    class DFA158 extends DFA {

        public DFA158(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 158;
            this.eot = DFA158_eot;
            this.eof = DFA158_eof;
            this.min = DFA158_min;
            this.max = DFA158_max;
            this.accept = DFA158_accept;
            this.special = DFA158_special;
            this.transition = DFA158_transition;
        }
        public String getDescription() {
            return "1185:27: ( '++' | '--' )?";
        }
    }
    static final String DFA161_eotS =
        "\32\uffff";
    static final String DFA161_eofS =
        "\32\uffff";
    static final String DFA161_minS =
        "\1\101\1\4\1\0\27\uffff";
    static final String DFA161_maxS =
        "\1\101\1\167\1\0\27\uffff";
    static final String DFA161_acceptS =
        "\3\uffff\1\2\25\uffff\1\1";
    static final String DFA161_specialS =
        "\2\uffff\1\0\27\uffff}>";
    static final String[] DFA161_transitionS = {
            "\1\1",
            "\1\3\1\uffff\6\3\25\uffff\1\3\6\uffff\1\3\16\uffff\10\2\1\uffff"+
            "\2\3\2\uffff\3\3\24\uffff\3\3\20\uffff\2\3\2\uffff\6\3",
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
            ""
    };

    static final short[] DFA161_eot = DFA.unpackEncodedString(DFA161_eotS);
    static final short[] DFA161_eof = DFA.unpackEncodedString(DFA161_eofS);
    static final char[] DFA161_min = DFA.unpackEncodedStringToUnsignedChars(DFA161_minS);
    static final char[] DFA161_max = DFA.unpackEncodedStringToUnsignedChars(DFA161_maxS);
    static final short[] DFA161_accept = DFA.unpackEncodedString(DFA161_acceptS);
    static final short[] DFA161_special = DFA.unpackEncodedString(DFA161_specialS);
    static final short[][] DFA161_transition;

    static {
        int numStates = DFA161_transitionS.length;
        DFA161_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA161_transition[i] = DFA.unpackEncodedString(DFA161_transitionS[i]);
        }
    }

    class DFA161 extends DFA {

        public DFA161(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 161;
            this.eot = DFA161_eot;
            this.eof = DFA161_eof;
            this.min = DFA161_min;
            this.max = DFA161_max;
            this.accept = DFA161_accept;
            this.special = DFA161_special;
            this.transition = DFA161_transition;
        }
        public String getDescription() {
            return "1188:1: castExpression : ( '(' primitiveType ')' unaryExpression | '(' ( type | expression ) ')' unaryExpressionNotPlusMinus );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA161_2 = input.LA(1);

                         
                        int index161_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred242_Java()) ) {s = 25;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index161_2);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 161, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA160_eotS =
        "\66\uffff";
    static final String DFA160_eofS =
        "\66\uffff";
    static final String DFA160_minS =
        "\1\4\1\0\1\34\60\uffff\1\0\2\uffff";
    static final String DFA160_maxS =
        "\1\167\1\0\1\102\60\uffff\1\0\2\uffff";
    static final String DFA160_acceptS =
        "\3\uffff\1\2\56\uffff\1\1\3\uffff";
    static final String DFA160_specialS =
        "\1\uffff\1\0\61\uffff\1\1\2\uffff}>";
    static final String[] DFA160_transitionS = {
            "\1\1\1\uffff\6\3\25\uffff\1\3\6\uffff\1\3\16\uffff\10\2\1\uffff"+
            "\2\3\2\uffff\3\3\24\uffff\3\3\20\uffff\2\3\2\uffff\6\3",
            "\1\uffff",
            "\1\3\14\uffff\1\63\30\uffff\1\62",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            ""
    };

    static final short[] DFA160_eot = DFA.unpackEncodedString(DFA160_eotS);
    static final short[] DFA160_eof = DFA.unpackEncodedString(DFA160_eofS);
    static final char[] DFA160_min = DFA.unpackEncodedStringToUnsignedChars(DFA160_minS);
    static final char[] DFA160_max = DFA.unpackEncodedStringToUnsignedChars(DFA160_maxS);
    static final short[] DFA160_accept = DFA.unpackEncodedString(DFA160_acceptS);
    static final short[] DFA160_special = DFA.unpackEncodedString(DFA160_specialS);
    static final short[][] DFA160_transition;

    static {
        int numStates = DFA160_transitionS.length;
        DFA160_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA160_transition[i] = DFA.unpackEncodedString(DFA160_transitionS[i]);
        }
    }

    class DFA160 extends DFA {

        public DFA160(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 160;
            this.eot = DFA160_eot;
            this.eof = DFA160_eof;
            this.min = DFA160_min;
            this.max = DFA160_max;
            this.accept = DFA160_accept;
            this.special = DFA160_special;
            this.transition = DFA160_transition;
        }
        public String getDescription() {
            return "1190:12: ( type | expression )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA160_1 = input.LA(1);

                         
                        int index160_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred243_Java()) ) {s = 50;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index160_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA160_51 = input.LA(1);

                         
                        int index160_51 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred243_Java()) ) {s = 50;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index160_51);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 160, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA170_eotS =
        "\22\uffff";
    static final String DFA170_eofS =
        "\22\uffff";
    static final String DFA170_minS =
        "\1\4\21\uffff";
    static final String DFA170_maxS =
        "\1\167\21\uffff";
    static final String DFA170_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\2\uffff\1\6\5\uffff\1\7\1\10\1\11\1"+
        "\12";
    static final String DFA170_specialS =
        "\22\uffff}>";
    static final String[] DFA170_transitionS = {
            "\1\17\1\uffff\6\10\25\uffff\1\2\6\uffff\1\21\16\uffff\10\20"+
            "\1\uffff\1\4\1\1\2\uffff\3\10\24\uffff\3\5\30\uffff\1\3\1\16",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA170_eot = DFA.unpackEncodedString(DFA170_eotS);
    static final short[] DFA170_eof = DFA.unpackEncodedString(DFA170_eofS);
    static final char[] DFA170_min = DFA.unpackEncodedStringToUnsignedChars(DFA170_minS);
    static final char[] DFA170_max = DFA.unpackEncodedStringToUnsignedChars(DFA170_maxS);
    static final short[] DFA170_accept = DFA.unpackEncodedString(DFA170_acceptS);
    static final short[] DFA170_special = DFA.unpackEncodedString(DFA170_specialS);
    static final short[][] DFA170_transition;

    static {
        int numStates = DFA170_transitionS.length;
        DFA170_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA170_transition[i] = DFA.unpackEncodedString(DFA170_transitionS[i]);
        }
    }

    class DFA170 extends DFA {

        public DFA170(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 170;
            this.eot = DFA170_eot;
            this.eof = DFA170_eof;
            this.min = DFA170_min;
            this.max = DFA170_max;
            this.accept = DFA170_accept;
            this.special = DFA170_special;
            this.transition = DFA170_transition;
        }
        public String getDescription() {
            return "1193:1: primary : ( parExpression | nonWildcardTypeArguments ( explicitGenericInvocationSuffix | 'this' arguments ) | 'this' ( '.' Identifier )* ( identifierSuffix )? | 'super' superSuffix | epStatement ( '.' Identifier )* ( identifierSuffix )? | literal | 'new' creator | i= Identifier ( '.' Identifier )* ( identifierSuffix )? | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );";
        }
    }
    static final String DFA163_eotS =
        "\50\uffff";
    static final String DFA163_eofS =
        "\1\1\47\uffff";
    static final String DFA163_minS =
        "\1\31\2\uffff\1\4\41\uffff\1\0\2\uffff";
    static final String DFA163_maxS =
        "\1\163\2\uffff\1\167\41\uffff\1\0\2\uffff";
    static final String DFA163_acceptS =
        "\1\uffff\1\2\45\uffff\1\1";
    static final String DFA163_specialS =
        "\45\uffff\1\0\2\uffff}>";
    static final String[] DFA163_transitionS = {
            "\1\1\2\uffff\1\3\1\1\3\uffff\4\1\1\uffff\1\1\2\uffff\2\1\1\uffff"+
            "\1\1\22\uffff\1\1\1\uffff\2\1\7\uffff\1\1\24\uffff\25\1",
            "",
            "",
            "\1\45\31\uffff\1\1\2\uffff\1\1\36\uffff\1\1\65\uffff\2\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            ""
    };

    static final short[] DFA163_eot = DFA.unpackEncodedString(DFA163_eotS);
    static final short[] DFA163_eof = DFA.unpackEncodedString(DFA163_eofS);
    static final char[] DFA163_min = DFA.unpackEncodedStringToUnsignedChars(DFA163_minS);
    static final char[] DFA163_max = DFA.unpackEncodedStringToUnsignedChars(DFA163_maxS);
    static final short[] DFA163_accept = DFA.unpackEncodedString(DFA163_acceptS);
    static final short[] DFA163_special = DFA.unpackEncodedString(DFA163_specialS);
    static final short[][] DFA163_transition;

    static {
        int numStates = DFA163_transitionS.length;
        DFA163_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA163_transition[i] = DFA.unpackEncodedString(DFA163_transitionS[i]);
        }
    }

    class DFA163 extends DFA {

        public DFA163(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 163;
            this.eot = DFA163_eot;
            this.eof = DFA163_eof;
            this.min = DFA163_min;
            this.max = DFA163_max;
            this.accept = DFA163_accept;
            this.special = DFA163_special;
            this.transition = DFA163_transition;
        }
        public String getDescription() {
            return "()* loopback of 1197:16: ( '.' Identifier )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA163_37 = input.LA(1);

                         
                        int index163_37 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred247_Java()) ) {s = 39;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index163_37);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 163, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA164_eotS =
        "\77\uffff";
    static final String DFA164_eofS =
        "\1\4\76\uffff";
    static final String DFA164_minS =
        "\1\31\1\4\1\uffff\1\4\36\uffff\27\0\1\uffff\3\0\2\uffff";
    static final String DFA164_maxS =
        "\1\163\1\167\1\uffff\1\167\36\uffff\27\0\1\uffff\3\0\2\uffff";
    static final String DFA164_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\72\uffff";
    static final String DFA164_specialS =
        "\42\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1"+
        "\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\uffff\1"+
        "\27\1\30\1\31\2\uffff}>";
    static final String[] DFA164_transitionS = {
            "\1\4\2\uffff\1\3\1\4\3\uffff\4\4\1\uffff\1\4\2\uffff\1\1\1\4"+
            "\1\uffff\1\4\22\uffff\1\4\1\uffff\1\2\1\4\7\uffff\1\4\24\uffff"+
            "\25\4",
            "\1\66\1\uffff\1\60\1\61\1\62\3\57\25\uffff\1\51\6\uffff\1\70"+
            "\1\uffff\1\2\14\uffff\10\67\1\uffff\1\53\1\50\2\uffff\1\64\2"+
            "\63\24\uffff\1\54\1\55\1\56\20\uffff\1\42\1\43\2\uffff\1\44"+
            "\1\45\1\46\1\47\1\52\1\65",
            "",
            "\1\4\31\uffff\1\2\2\uffff\1\2\36\uffff\1\73\65\uffff\1\72\1"+
            "\74",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA164_eot = DFA.unpackEncodedString(DFA164_eotS);
    static final short[] DFA164_eof = DFA.unpackEncodedString(DFA164_eofS);
    static final char[] DFA164_min = DFA.unpackEncodedStringToUnsignedChars(DFA164_minS);
    static final char[] DFA164_max = DFA.unpackEncodedStringToUnsignedChars(DFA164_maxS);
    static final short[] DFA164_accept = DFA.unpackEncodedString(DFA164_acceptS);
    static final short[] DFA164_special = DFA.unpackEncodedString(DFA164_specialS);
    static final short[][] DFA164_transition;

    static {
        int numStates = DFA164_transitionS.length;
        DFA164_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA164_transition[i] = DFA.unpackEncodedString(DFA164_transitionS[i]);
        }
    }

    class DFA164 extends DFA {

        public DFA164(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 164;
            this.eot = DFA164_eot;
            this.eof = DFA164_eof;
            this.min = DFA164_min;
            this.max = DFA164_max;
            this.accept = DFA164_accept;
            this.special = DFA164_special;
            this.transition = DFA164_transition;
        }
        public String getDescription() {
            return "1197:34: ( identifierSuffix )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA164_34 = input.LA(1);

                         
                        int index164_34 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_34);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA164_35 = input.LA(1);

                         
                        int index164_35 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_35);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA164_36 = input.LA(1);

                         
                        int index164_36 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_36);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA164_37 = input.LA(1);

                         
                        int index164_37 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_37);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA164_38 = input.LA(1);

                         
                        int index164_38 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_38);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA164_39 = input.LA(1);

                         
                        int index164_39 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_39);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA164_40 = input.LA(1);

                         
                        int index164_40 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_40);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA164_41 = input.LA(1);

                         
                        int index164_41 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_41);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA164_42 = input.LA(1);

                         
                        int index164_42 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_42);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA164_43 = input.LA(1);

                         
                        int index164_43 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_43);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA164_44 = input.LA(1);

                         
                        int index164_44 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_44);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA164_45 = input.LA(1);

                         
                        int index164_45 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_45);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA164_46 = input.LA(1);

                         
                        int index164_46 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_46);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA164_47 = input.LA(1);

                         
                        int index164_47 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_47);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA164_48 = input.LA(1);

                         
                        int index164_48 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_48);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA164_49 = input.LA(1);

                         
                        int index164_49 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_49);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA164_50 = input.LA(1);

                         
                        int index164_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_50);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA164_51 = input.LA(1);

                         
                        int index164_51 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_51);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA164_52 = input.LA(1);

                         
                        int index164_52 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_52);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA164_53 = input.LA(1);

                         
                        int index164_53 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_53);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA164_54 = input.LA(1);

                         
                        int index164_54 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_54);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA164_55 = input.LA(1);

                         
                        int index164_55 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_55);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA164_56 = input.LA(1);

                         
                        int index164_56 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_56);
                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA164_58 = input.LA(1);

                         
                        int index164_58 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_58);
                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA164_59 = input.LA(1);

                         
                        int index164_59 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_59);
                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA164_60 = input.LA(1);

                         
                        int index164_60 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred248_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_60);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 164, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA165_eotS =
        "\50\uffff";
    static final String DFA165_eofS =
        "\1\1\47\uffff";
    static final String DFA165_minS =
        "\1\31\2\uffff\1\4\41\uffff\1\0\2\uffff";
    static final String DFA165_maxS =
        "\1\163\2\uffff\1\167\41\uffff\1\0\2\uffff";
    static final String DFA165_acceptS =
        "\1\uffff\1\2\45\uffff\1\1";
    static final String DFA165_specialS =
        "\45\uffff\1\0\2\uffff}>";
    static final String[] DFA165_transitionS = {
            "\1\1\2\uffff\1\3\1\1\3\uffff\4\1\1\uffff\1\1\2\uffff\2\1\1\uffff"+
            "\1\1\22\uffff\1\1\1\uffff\2\1\7\uffff\1\1\24\uffff\25\1",
            "",
            "",
            "\1\45\31\uffff\1\1\2\uffff\1\1\36\uffff\1\1\65\uffff\2\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    class DFA165 extends DFA {

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
        public String getDescription() {
            return "()* loopback of 1199:21: ( '.' Identifier )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA165_37 = input.LA(1);

                         
                        int index165_37 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred251_Java()) ) {s = 39;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index165_37);
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
    static final String DFA166_eotS =
        "\77\uffff";
    static final String DFA166_eofS =
        "\1\4\76\uffff";
    static final String DFA166_minS =
        "\1\31\1\4\1\uffff\1\4\36\uffff\27\0\1\uffff\3\0\2\uffff";
    static final String DFA166_maxS =
        "\1\163\1\167\1\uffff\1\167\36\uffff\27\0\1\uffff\3\0\2\uffff";
    static final String DFA166_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\72\uffff";
    static final String DFA166_specialS =
        "\42\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1"+
        "\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\uffff\1"+
        "\27\1\30\1\31\2\uffff}>";
    static final String[] DFA166_transitionS = {
            "\1\4\2\uffff\1\3\1\4\3\uffff\4\4\1\uffff\1\4\2\uffff\1\1\1\4"+
            "\1\uffff\1\4\22\uffff\1\4\1\uffff\1\2\1\4\7\uffff\1\4\24\uffff"+
            "\25\4",
            "\1\66\1\uffff\1\60\1\61\1\62\3\57\25\uffff\1\51\6\uffff\1\70"+
            "\1\uffff\1\2\14\uffff\10\67\1\uffff\1\53\1\50\2\uffff\1\64\2"+
            "\63\24\uffff\1\54\1\55\1\56\20\uffff\1\42\1\43\2\uffff\1\44"+
            "\1\45\1\46\1\47\1\52\1\65",
            "",
            "\1\4\31\uffff\1\2\2\uffff\1\2\36\uffff\1\73\65\uffff\1\72\1"+
            "\74",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA166_eot = DFA.unpackEncodedString(DFA166_eotS);
    static final short[] DFA166_eof = DFA.unpackEncodedString(DFA166_eofS);
    static final char[] DFA166_min = DFA.unpackEncodedStringToUnsignedChars(DFA166_minS);
    static final char[] DFA166_max = DFA.unpackEncodedStringToUnsignedChars(DFA166_maxS);
    static final short[] DFA166_accept = DFA.unpackEncodedString(DFA166_acceptS);
    static final short[] DFA166_special = DFA.unpackEncodedString(DFA166_specialS);
    static final short[][] DFA166_transition;

    static {
        int numStates = DFA166_transitionS.length;
        DFA166_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA166_transition[i] = DFA.unpackEncodedString(DFA166_transitionS[i]);
        }
    }

    class DFA166 extends DFA {

        public DFA166(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 166;
            this.eot = DFA166_eot;
            this.eof = DFA166_eof;
            this.min = DFA166_min;
            this.max = DFA166_max;
            this.accept = DFA166_accept;
            this.special = DFA166_special;
            this.transition = DFA166_transition;
        }
        public String getDescription() {
            return "1199:39: ( identifierSuffix )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA166_34 = input.LA(1);

                         
                        int index166_34 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_34);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA166_35 = input.LA(1);

                         
                        int index166_35 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_35);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA166_36 = input.LA(1);

                         
                        int index166_36 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_36);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA166_37 = input.LA(1);

                         
                        int index166_37 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_37);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA166_38 = input.LA(1);

                         
                        int index166_38 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_38);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA166_39 = input.LA(1);

                         
                        int index166_39 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_39);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA166_40 = input.LA(1);

                         
                        int index166_40 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_40);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA166_41 = input.LA(1);

                         
                        int index166_41 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_41);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA166_42 = input.LA(1);

                         
                        int index166_42 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_42);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA166_43 = input.LA(1);

                         
                        int index166_43 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_43);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA166_44 = input.LA(1);

                         
                        int index166_44 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_44);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA166_45 = input.LA(1);

                         
                        int index166_45 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_45);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA166_46 = input.LA(1);

                         
                        int index166_46 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_46);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA166_47 = input.LA(1);

                         
                        int index166_47 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_47);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA166_48 = input.LA(1);

                         
                        int index166_48 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_48);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA166_49 = input.LA(1);

                         
                        int index166_49 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_49);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA166_50 = input.LA(1);

                         
                        int index166_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_50);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA166_51 = input.LA(1);

                         
                        int index166_51 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_51);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA166_52 = input.LA(1);

                         
                        int index166_52 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_52);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA166_53 = input.LA(1);

                         
                        int index166_53 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_53);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA166_54 = input.LA(1);

                         
                        int index166_54 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_54);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA166_55 = input.LA(1);

                         
                        int index166_55 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_55);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA166_56 = input.LA(1);

                         
                        int index166_56 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_56);
                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA166_58 = input.LA(1);

                         
                        int index166_58 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_58);
                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA166_59 = input.LA(1);

                         
                        int index166_59 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_59);
                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA166_60 = input.LA(1);

                         
                        int index166_60 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred252_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index166_60);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 166, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA167_eotS =
        "\50\uffff";
    static final String DFA167_eofS =
        "\1\1\47\uffff";
    static final String DFA167_minS =
        "\1\31\2\uffff\1\4\41\uffff\1\0\2\uffff";
    static final String DFA167_maxS =
        "\1\163\2\uffff\1\167\41\uffff\1\0\2\uffff";
    static final String DFA167_acceptS =
        "\1\uffff\1\2\45\uffff\1\1";
    static final String DFA167_specialS =
        "\45\uffff\1\0\2\uffff}>";
    static final String[] DFA167_transitionS = {
            "\1\1\2\uffff\1\3\1\1\3\uffff\4\1\1\uffff\1\1\2\uffff\2\1\1\uffff"+
            "\1\1\22\uffff\1\1\1\uffff\2\1\7\uffff\1\1\24\uffff\25\1",
            "",
            "",
            "\1\45\31\uffff\1\1\2\uffff\1\1\36\uffff\1\1\65\uffff\2\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    class DFA167 extends DFA {

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
        public String getDescription() {
            return "()* loopback of 1202:126: ( '.' Identifier )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA167_37 = input.LA(1);

                         
                        int index167_37 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred256_Java()) ) {s = 39;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index167_37);
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
    static final String DFA168_eotS =
        "\77\uffff";
    static final String DFA168_eofS =
        "\1\4\76\uffff";
    static final String DFA168_minS =
        "\1\31\1\4\1\uffff\1\4\36\uffff\27\0\1\uffff\3\0\2\uffff";
    static final String DFA168_maxS =
        "\1\163\1\167\1\uffff\1\167\36\uffff\27\0\1\uffff\3\0\2\uffff";
    static final String DFA168_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\72\uffff";
    static final String DFA168_specialS =
        "\42\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1"+
        "\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\uffff\1"+
        "\27\1\30\1\31\2\uffff}>";
    static final String[] DFA168_transitionS = {
            "\1\4\2\uffff\1\3\1\4\3\uffff\4\4\1\uffff\1\4\2\uffff\1\1\1\4"+
            "\1\uffff\1\4\22\uffff\1\4\1\uffff\1\2\1\4\7\uffff\1\4\24\uffff"+
            "\25\4",
            "\1\66\1\uffff\1\60\1\61\1\62\3\57\25\uffff\1\51\6\uffff\1\70"+
            "\1\uffff\1\2\14\uffff\10\67\1\uffff\1\53\1\50\2\uffff\1\64\2"+
            "\63\24\uffff\1\54\1\55\1\56\20\uffff\1\42\1\43\2\uffff\1\44"+
            "\1\45\1\46\1\47\1\52\1\65",
            "",
            "\1\4\31\uffff\1\2\2\uffff\1\2\36\uffff\1\73\65\uffff\1\72\1"+
            "\74",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA168_eot = DFA.unpackEncodedString(DFA168_eotS);
    static final short[] DFA168_eof = DFA.unpackEncodedString(DFA168_eofS);
    static final char[] DFA168_min = DFA.unpackEncodedStringToUnsignedChars(DFA168_minS);
    static final char[] DFA168_max = DFA.unpackEncodedStringToUnsignedChars(DFA168_maxS);
    static final short[] DFA168_accept = DFA.unpackEncodedString(DFA168_acceptS);
    static final short[] DFA168_special = DFA.unpackEncodedString(DFA168_specialS);
    static final short[][] DFA168_transition;

    static {
        int numStates = DFA168_transitionS.length;
        DFA168_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA168_transition[i] = DFA.unpackEncodedString(DFA168_transitionS[i]);
        }
    }

    class DFA168 extends DFA {

        public DFA168(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 168;
            this.eot = DFA168_eot;
            this.eof = DFA168_eof;
            this.min = DFA168_min;
            this.max = DFA168_max;
            this.accept = DFA168_accept;
            this.special = DFA168_special;
            this.transition = DFA168_transition;
        }
        public String getDescription() {
            return "1202:144: ( identifierSuffix )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA168_34 = input.LA(1);

                         
                        int index168_34 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_34);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA168_35 = input.LA(1);

                         
                        int index168_35 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_35);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA168_36 = input.LA(1);

                         
                        int index168_36 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_36);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA168_37 = input.LA(1);

                         
                        int index168_37 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_37);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA168_38 = input.LA(1);

                         
                        int index168_38 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_38);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA168_39 = input.LA(1);

                         
                        int index168_39 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_39);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA168_40 = input.LA(1);

                         
                        int index168_40 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_40);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA168_41 = input.LA(1);

                         
                        int index168_41 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_41);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA168_42 = input.LA(1);

                         
                        int index168_42 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_42);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA168_43 = input.LA(1);

                         
                        int index168_43 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_43);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA168_44 = input.LA(1);

                         
                        int index168_44 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_44);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA168_45 = input.LA(1);

                         
                        int index168_45 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_45);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA168_46 = input.LA(1);

                         
                        int index168_46 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_46);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA168_47 = input.LA(1);

                         
                        int index168_47 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_47);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA168_48 = input.LA(1);

                         
                        int index168_48 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_48);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA168_49 = input.LA(1);

                         
                        int index168_49 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_49);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA168_50 = input.LA(1);

                         
                        int index168_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_50);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA168_51 = input.LA(1);

                         
                        int index168_51 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_51);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA168_52 = input.LA(1);

                         
                        int index168_52 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_52);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA168_53 = input.LA(1);

                         
                        int index168_53 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_53);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA168_54 = input.LA(1);

                         
                        int index168_54 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_54);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA168_55 = input.LA(1);

                         
                        int index168_55 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_55);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA168_56 = input.LA(1);

                         
                        int index168_56 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_56);
                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA168_58 = input.LA(1);

                         
                        int index168_58 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_58);
                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA168_59 = input.LA(1);

                         
                        int index168_59 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_59);
                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA168_60 = input.LA(1);

                         
                        int index168_60 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred257_Java()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_60);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 168, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA174_eotS =
        "\41\uffff";
    static final String DFA174_eofS =
        "\41\uffff";
    static final String DFA174_minS =
        "\1\34\1\4\1\uffff\1\36\35\uffff";
    static final String DFA174_maxS =
        "\1\101\1\167\1\uffff\1\167\35\uffff";
    static final String DFA174_acceptS =
        "\2\uffff\1\3\1\uffff\1\1\1\2\26\uffff\1\4\1\6\1\7\1\10\1\5";
    static final String DFA174_specialS =
        "\41\uffff}>";
    static final String[] DFA174_transitionS = {
            "\1\3\14\uffff\1\1\27\uffff\1\2",
            "\1\5\1\uffff\6\5\25\uffff\1\5\6\uffff\1\5\1\uffff\1\4\14\uffff"+
            "\10\5\1\uffff\2\5\2\uffff\3\5\24\uffff\3\5\20\uffff\2\5\2\uffff"+
            "\6\5",
            "",
            "\1\34\2\uffff\1\40\36\uffff\1\36\65\uffff\1\35\1\37",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA174_eot = DFA.unpackEncodedString(DFA174_eotS);
    static final short[] DFA174_eof = DFA.unpackEncodedString(DFA174_eofS);
    static final char[] DFA174_min = DFA.unpackEncodedStringToUnsignedChars(DFA174_minS);
    static final char[] DFA174_max = DFA.unpackEncodedStringToUnsignedChars(DFA174_maxS);
    static final short[] DFA174_accept = DFA.unpackEncodedString(DFA174_acceptS);
    static final short[] DFA174_special = DFA.unpackEncodedString(DFA174_specialS);
    static final short[][] DFA174_transition;

    static {
        int numStates = DFA174_transitionS.length;
        DFA174_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA174_transition[i] = DFA.unpackEncodedString(DFA174_transitionS[i]);
        }
    }

    class DFA174 extends DFA {

        public DFA174(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 174;
            this.eot = DFA174_eot;
            this.eof = DFA174_eof;
            this.min = DFA174_min;
            this.max = DFA174_max;
            this.accept = DFA174_accept;
            this.special = DFA174_special;
            this.transition = DFA174_transition;
        }
        public String getDescription() {
            return "1207:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' explicitGenericInvocation | '.' 'this' | '.' 'super' arguments | '.' 'new' ( nonWildcardTypeArguments )? innerCreator );";
        }
    }
    static final String DFA172_eotS =
        "\70\uffff";
    static final String DFA172_eofS =
        "\1\1\67\uffff";
    static final String DFA172_minS =
        "\1\31\35\uffff\1\4\1\uffff\27\0\1\uffff";
    static final String DFA172_maxS =
        "\1\163\35\uffff\1\167\1\uffff\27\0\1\uffff";
    static final String DFA172_acceptS =
        "\1\uffff\1\2\65\uffff\1\1";
    static final String DFA172_specialS =
        "\40\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1"+
        "\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\uffff}>";
    static final String[] DFA172_transitionS = {
            "\1\1\2\uffff\2\1\3\uffff\4\1\1\uffff\1\1\2\uffff\1\36\1\1\1"+
            "\uffff\1\1\22\uffff\1\1\2\uffff\1\1\7\uffff\1\1\24\uffff\25"+
            "\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\64\1\uffff\1\56\1\57\1\60\3\55\25\uffff\1\47\6\uffff\1\66"+
            "\16\uffff\10\65\1\uffff\1\51\1\46\2\uffff\1\62\2\61\24\uffff"+
            "\1\52\1\53\1\54\20\uffff\1\40\1\41\2\uffff\1\42\1\43\1\44\1"+
            "\45\1\50\1\63",
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
            ""
    };

    static final short[] DFA172_eot = DFA.unpackEncodedString(DFA172_eotS);
    static final short[] DFA172_eof = DFA.unpackEncodedString(DFA172_eofS);
    static final char[] DFA172_min = DFA.unpackEncodedStringToUnsignedChars(DFA172_minS);
    static final char[] DFA172_max = DFA.unpackEncodedStringToUnsignedChars(DFA172_maxS);
    static final short[] DFA172_accept = DFA.unpackEncodedString(DFA172_acceptS);
    static final short[] DFA172_special = DFA.unpackEncodedString(DFA172_specialS);
    static final short[][] DFA172_transition;

    static {
        int numStates = DFA172_transitionS.length;
        DFA172_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA172_transition[i] = DFA.unpackEncodedString(DFA172_transitionS[i]);
        }
    }

    class DFA172 extends DFA {

        public DFA172(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 172;
            this.eot = DFA172_eot;
            this.eof = DFA172_eof;
            this.min = DFA172_min;
            this.max = DFA172_max;
            this.accept = DFA172_accept;
            this.special = DFA172_special;
            this.transition = DFA172_transition;
        }
        public String getDescription() {
            return "()+ loopback of 1209:7: ( '[' expression ']' )+";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA172_32 = input.LA(1);

                         
                        int index172_32 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_32);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA172_33 = input.LA(1);

                         
                        int index172_33 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_33);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA172_34 = input.LA(1);

                         
                        int index172_34 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_34);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA172_35 = input.LA(1);

                         
                        int index172_35 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_35);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA172_36 = input.LA(1);

                         
                        int index172_36 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_36);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA172_37 = input.LA(1);

                         
                        int index172_37 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_37);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA172_38 = input.LA(1);

                         
                        int index172_38 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_38);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA172_39 = input.LA(1);

                         
                        int index172_39 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_39);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA172_40 = input.LA(1);

                         
                        int index172_40 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_40);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA172_41 = input.LA(1);

                         
                        int index172_41 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_41);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA172_42 = input.LA(1);

                         
                        int index172_42 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_42);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA172_43 = input.LA(1);

                         
                        int index172_43 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_43);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA172_44 = input.LA(1);

                         
                        int index172_44 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_44);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA172_45 = input.LA(1);

                         
                        int index172_45 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_45);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA172_46 = input.LA(1);

                         
                        int index172_46 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_46);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA172_47 = input.LA(1);

                         
                        int index172_47 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_47);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA172_48 = input.LA(1);

                         
                        int index172_48 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_48);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA172_49 = input.LA(1);

                         
                        int index172_49 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_49);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA172_50 = input.LA(1);

                         
                        int index172_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_50);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA172_51 = input.LA(1);

                         
                        int index172_51 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_51);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA172_52 = input.LA(1);

                         
                        int index172_52 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_52);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA172_53 = input.LA(1);

                         
                        int index172_53 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_53);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA172_54 = input.LA(1);

                         
                        int index172_54 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_Java()) ) {s = 55;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index172_54);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 172, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA184_eotS =
        "\31\uffff";
    static final String DFA184_eofS =
        "\31\uffff";
    static final String DFA184_minS =
        "\1\4\30\uffff";
    static final String DFA184_maxS =
        "\1\167\30\uffff";
    static final String DFA184_acceptS =
        "\1\uffff\1\1\1\2\26\uffff";
    static final String DFA184_specialS =
        "\31\uffff}>";
    static final String[] DFA184_transitionS = {
            "\1\2\1\uffff\6\2\25\uffff\1\2\6\uffff\1\2\1\uffff\1\1\14\uffff"+
            "\10\2\1\uffff\2\2\2\uffff\3\2\24\uffff\3\2\20\uffff\2\2\2\uffff"+
            "\6\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA184_eot = DFA.unpackEncodedString(DFA184_eotS);
    static final short[] DFA184_eof = DFA.unpackEncodedString(DFA184_eofS);
    static final char[] DFA184_min = DFA.unpackEncodedStringToUnsignedChars(DFA184_minS);
    static final char[] DFA184_max = DFA.unpackEncodedStringToUnsignedChars(DFA184_maxS);
    static final short[] DFA184_accept = DFA.unpackEncodedString(DFA184_acceptS);
    static final short[] DFA184_special = DFA.unpackEncodedString(DFA184_specialS);
    static final short[][] DFA184_transition;

    static {
        int numStates = DFA184_transitionS.length;
        DFA184_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA184_transition[i] = DFA.unpackEncodedString(DFA184_transitionS[i]);
        }
    }

    class DFA184 extends DFA {

        public DFA184(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 184;
            this.eot = DFA184_eot;
            this.eof = DFA184_eof;
            this.min = DFA184_min;
            this.max = DFA184_max;
            this.accept = DFA184_accept;
            this.special = DFA184_special;
            this.transition = DFA184_transition;
        }
        public String getDescription() {
            return "1235:9: ( ']' ( '[' ']' )* arrayInitializer | expression ']' ( '[' expression ']' )* ( '[' ']' )* )";
        }
    }
    static final String DFA182_eotS =
        "\71\uffff";
    static final String DFA182_eofS =
        "\1\2\70\uffff";
    static final String DFA182_minS =
        "\1\31\1\4\37\uffff\27\0\1\uffff";
    static final String DFA182_maxS =
        "\1\163\1\167\37\uffff\27\0\1\uffff";
    static final String DFA182_acceptS =
        "\2\uffff\1\2\65\uffff\1\1";
    static final String DFA182_specialS =
        "\41\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1"+
        "\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\uffff}>";
    static final String[] DFA182_transitionS = {
            "\1\2\2\uffff\2\2\3\uffff\4\2\1\uffff\1\2\2\uffff\1\1\1\2\1\uffff"+
            "\1\2\22\uffff\1\2\2\uffff\1\2\7\uffff\1\2\24\uffff\25\2",
            "\1\65\1\uffff\1\57\1\60\1\61\3\56\25\uffff\1\50\6\uffff\1\67"+
            "\1\uffff\1\2\14\uffff\10\66\1\uffff\1\52\1\47\2\uffff\1\63\2"+
            "\62\24\uffff\1\53\1\54\1\55\20\uffff\1\41\1\42\2\uffff\1\43"+
            "\1\44\1\45\1\46\1\51\1\64",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            ""
    };

    static final short[] DFA182_eot = DFA.unpackEncodedString(DFA182_eotS);
    static final short[] DFA182_eof = DFA.unpackEncodedString(DFA182_eofS);
    static final char[] DFA182_min = DFA.unpackEncodedStringToUnsignedChars(DFA182_minS);
    static final char[] DFA182_max = DFA.unpackEncodedStringToUnsignedChars(DFA182_maxS);
    static final short[] DFA182_accept = DFA.unpackEncodedString(DFA182_acceptS);
    static final short[] DFA182_special = DFA.unpackEncodedString(DFA182_specialS);
    static final short[][] DFA182_transition;

    static {
        int numStates = DFA182_transitionS.length;
        DFA182_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA182_transition[i] = DFA.unpackEncodedString(DFA182_transitionS[i]);
        }
    }

    class DFA182 extends DFA {

        public DFA182(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 182;
            this.eot = DFA182_eot;
            this.eof = DFA182_eof;
            this.min = DFA182_min;
            this.max = DFA182_max;
            this.accept = DFA182_accept;
            this.special = DFA182_special;
            this.transition = DFA182_transition;
        }
        public String getDescription() {
            return "()* loopback of 1236:28: ( '[' expression ']' )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA182_33 = input.LA(1);

                         
                        int index182_33 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_33);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA182_34 = input.LA(1);

                         
                        int index182_34 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_34);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA182_35 = input.LA(1);

                         
                        int index182_35 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_35);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA182_36 = input.LA(1);

                         
                        int index182_36 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_36);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA182_37 = input.LA(1);

                         
                        int index182_37 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_37);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA182_38 = input.LA(1);

                         
                        int index182_38 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_38);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA182_39 = input.LA(1);

                         
                        int index182_39 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_39);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA182_40 = input.LA(1);

                         
                        int index182_40 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_40);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA182_41 = input.LA(1);

                         
                        int index182_41 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_41);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA182_42 = input.LA(1);

                         
                        int index182_42 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_42);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA182_43 = input.LA(1);

                         
                        int index182_43 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_43);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA182_44 = input.LA(1);

                         
                        int index182_44 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_44);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA182_45 = input.LA(1);

                         
                        int index182_45 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_45);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA182_46 = input.LA(1);

                         
                        int index182_46 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_46);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA182_47 = input.LA(1);

                         
                        int index182_47 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_47);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA182_48 = input.LA(1);

                         
                        int index182_48 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_48);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA182_49 = input.LA(1);

                         
                        int index182_49 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_49);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA182_50 = input.LA(1);

                         
                        int index182_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_50);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA182_51 = input.LA(1);

                         
                        int index182_51 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_51);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA182_52 = input.LA(1);

                         
                        int index182_52 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_52);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA182_53 = input.LA(1);

                         
                        int index182_53 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_53);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA182_54 = input.LA(1);

                         
                        int index182_54 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_54);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA182_55 = input.LA(1);

                         
                        int index182_55 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred279_Java()) ) {s = 56;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index182_55);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 182, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA183_eotS =
        "\70\uffff";
    static final String DFA183_eofS =
        "\1\1\67\uffff";
    static final String DFA183_minS =
        "\1\31\35\uffff\1\4\31\uffff";
    static final String DFA183_maxS =
        "\1\163\35\uffff\1\167\31\uffff";
    static final String DFA183_acceptS =
        "\1\uffff\1\2\36\uffff\1\1\27\uffff";
    static final String DFA183_specialS =
        "\70\uffff}>";
    static final String[] DFA183_transitionS = {
            "\1\1\2\uffff\2\1\3\uffff\4\1\1\uffff\1\1\2\uffff\1\36\1\1\1"+
            "\uffff\1\1\22\uffff\1\1\2\uffff\1\1\7\uffff\1\1\24\uffff\25"+
            "\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\1\1\uffff\6\1\25\uffff\1\1\6\uffff\1\1\1\uffff\1\40\14\uffff"+
            "\10\1\1\uffff\2\1\2\uffff\3\1\24\uffff\3\1\20\uffff\2\1\2\uffff"+
            "\6\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA183_eot = DFA.unpackEncodedString(DFA183_eotS);
    static final short[] DFA183_eof = DFA.unpackEncodedString(DFA183_eofS);
    static final char[] DFA183_min = DFA.unpackEncodedStringToUnsignedChars(DFA183_minS);
    static final char[] DFA183_max = DFA.unpackEncodedStringToUnsignedChars(DFA183_maxS);
    static final short[] DFA183_accept = DFA.unpackEncodedString(DFA183_acceptS);
    static final short[] DFA183_special = DFA.unpackEncodedString(DFA183_specialS);
    static final short[][] DFA183_transition;

    static {
        int numStates = DFA183_transitionS.length;
        DFA183_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA183_transition[i] = DFA.unpackEncodedString(DFA183_transitionS[i]);
        }
    }

    class DFA183 extends DFA {

        public DFA183(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 183;
            this.eot = DFA183_eot;
            this.eof = DFA183_eof;
            this.min = DFA183_min;
            this.max = DFA183_max;
            this.accept = DFA183_accept;
            this.special = DFA183_special;
            this.transition = DFA183_transition;
        }
        public String getDescription() {
            return "()* loopback of 1236:50: ( '[' ']' )*";
        }
    }
    static final String DFA185_eotS =
        "\41\uffff";
    static final String DFA185_eofS =
        "\1\2\40\uffff";
    static final String DFA185_minS =
        "\1\31\40\uffff";
    static final String DFA185_maxS =
        "\1\163\40\uffff";
    static final String DFA185_acceptS =
        "\1\uffff\1\1\1\2\36\uffff";
    static final String DFA185_specialS =
        "\41\uffff}>";
    static final String[] DFA185_transitionS = {
            "\1\2\2\uffff\2\2\3\uffff\4\2\1\1\1\2\2\uffff\2\2\1\uffff\1\2"+
            "\22\uffff\1\2\2\uffff\1\2\7\uffff\1\2\24\uffff\25\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA185_eot = DFA.unpackEncodedString(DFA185_eotS);
    static final short[] DFA185_eof = DFA.unpackEncodedString(DFA185_eofS);
    static final char[] DFA185_min = DFA.unpackEncodedStringToUnsignedChars(DFA185_minS);
    static final char[] DFA185_max = DFA.unpackEncodedStringToUnsignedChars(DFA185_maxS);
    static final short[] DFA185_accept = DFA.unpackEncodedString(DFA185_acceptS);
    static final short[] DFA185_special = DFA.unpackEncodedString(DFA185_specialS);
    static final short[][] DFA185_transition;

    static {
        int numStates = DFA185_transitionS.length;
        DFA185_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA185_transition[i] = DFA.unpackEncodedString(DFA185_transitionS[i]);
        }
    }

    class DFA185 extends DFA {

        public DFA185(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 185;
            this.eot = DFA185_eot;
            this.eof = DFA185_eof;
            this.min = DFA185_min;
            this.max = DFA185_max;
            this.accept = DFA185_accept;
            this.special = DFA185_special;
            this.transition = DFA185_transition;
        }
        public String getDescription() {
            return "1241:17: ( classBody )?";
        }
    }
    static final String DFA187_eotS =
        "\41\uffff";
    static final String DFA187_eofS =
        "\1\2\40\uffff";
    static final String DFA187_minS =
        "\1\31\40\uffff";
    static final String DFA187_maxS =
        "\1\163\40\uffff";
    static final String DFA187_acceptS =
        "\1\uffff\1\1\1\2\36\uffff";
    static final String DFA187_specialS =
        "\41\uffff}>";
    static final String[] DFA187_transitionS = {
            "\1\2\2\uffff\2\2\3\uffff\4\2\1\uffff\1\2\2\uffff\2\2\1\uffff"+
            "\1\2\22\uffff\1\2\1\uffff\1\1\1\2\7\uffff\1\2\24\uffff\25\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA187_eot = DFA.unpackEncodedString(DFA187_eotS);
    static final short[] DFA187_eof = DFA.unpackEncodedString(DFA187_eofS);
    static final char[] DFA187_min = DFA.unpackEncodedStringToUnsignedChars(DFA187_minS);
    static final char[] DFA187_max = DFA.unpackEncodedStringToUnsignedChars(DFA187_maxS);
    static final short[] DFA187_accept = DFA.unpackEncodedString(DFA187_acceptS);
    static final short[] DFA187_special = DFA.unpackEncodedString(DFA187_specialS);
    static final short[][] DFA187_transition;

    static {
        int numStates = DFA187_transitionS.length;
        DFA187_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA187_transition[i] = DFA.unpackEncodedString(DFA187_transitionS[i]);
        }
    }

    class DFA187 extends DFA {

        public DFA187(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 187;
            this.eot = DFA187_eot;
            this.eof = DFA187_eof;
            this.min = DFA187_min;
            this.max = DFA187_max;
            this.accept = DFA187_accept;
            this.special = DFA187_special;
            this.transition = DFA187_transition;
        }
        public String getDescription() {
            return "1258:22: ( arguments )?";
        }
    }
    static final String DFA190_eotS =
        "\41\uffff";
    static final String DFA190_eofS =
        "\1\2\40\uffff";
    static final String DFA190_minS =
        "\1\31\40\uffff";
    static final String DFA190_maxS =
        "\1\163\40\uffff";
    static final String DFA190_acceptS =
        "\1\uffff\1\1\1\2\36\uffff";
    static final String DFA190_specialS =
        "\41\uffff}>";
    static final String[] DFA190_transitionS = {
            "\1\2\2\uffff\2\2\3\uffff\4\2\1\uffff\1\2\2\uffff\2\2\1\uffff"+
            "\1\2\22\uffff\1\2\1\uffff\1\1\1\2\7\uffff\1\2\24\uffff\25\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA190_eot = DFA.unpackEncodedString(DFA190_eotS);
    static final short[] DFA190_eof = DFA.unpackEncodedString(DFA190_eofS);
    static final char[] DFA190_min = DFA.unpackEncodedStringToUnsignedChars(DFA190_minS);
    static final char[] DFA190_max = DFA.unpackEncodedStringToUnsignedChars(DFA190_maxS);
    static final short[] DFA190_accept = DFA.unpackEncodedString(DFA190_acceptS);
    static final short[] DFA190_special = DFA.unpackEncodedString(DFA190_specialS);
    static final short[][] DFA190_transition;

    static {
        int numStates = DFA190_transitionS.length;
        DFA190_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA190_transition[i] = DFA.unpackEncodedString(DFA190_transitionS[i]);
        }
    }

    class DFA190 extends DFA {

        public DFA190(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 190;
            this.eot = DFA190_eot;
            this.eof = DFA190_eof;
            this.min = DFA190_min;
            this.max = DFA190_max;
            this.accept = DFA190_accept;
            this.special = DFA190_special;
            this.transition = DFA190_transition;
        }
        public String getDescription() {
            return "1267:24: ( arguments )?";
        }
    }
    static final String DFA192_eotS =
        "\31\uffff";
    static final String DFA192_eofS =
        "\31\uffff";
    static final String DFA192_minS =
        "\1\4\30\uffff";
    static final String DFA192_maxS =
        "\1\167\30\uffff";
    static final String DFA192_acceptS =
        "\1\uffff\1\1\26\uffff\1\2";
    static final String DFA192_specialS =
        "\31\uffff}>";
    static final String[] DFA192_transitionS = {
            "\1\1\1\uffff\6\1\25\uffff\1\1\6\uffff\1\1\16\uffff\10\1\1\uffff"+
            "\2\1\1\30\1\uffff\3\1\24\uffff\3\1\20\uffff\2\1\2\uffff\6\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA192_eot = DFA.unpackEncodedString(DFA192_eotS);
    static final short[] DFA192_eof = DFA.unpackEncodedString(DFA192_eofS);
    static final char[] DFA192_min = DFA.unpackEncodedStringToUnsignedChars(DFA192_minS);
    static final char[] DFA192_max = DFA.unpackEncodedStringToUnsignedChars(DFA192_maxS);
    static final short[] DFA192_accept = DFA.unpackEncodedString(DFA192_acceptS);
    static final short[] DFA192_special = DFA.unpackEncodedString(DFA192_specialS);
    static final short[][] DFA192_transition;

    static {
        int numStates = DFA192_transitionS.length;
        DFA192_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA192_transition[i] = DFA.unpackEncodedString(DFA192_transitionS[i]);
        }
    }

    class DFA192 extends DFA {

        public DFA192(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 192;
            this.eot = DFA192_eot;
            this.eof = DFA192_eof;
            this.min = DFA192_min;
            this.max = DFA192_max;
            this.accept = DFA192_accept;
            this.special = DFA192_special;
            this.transition = DFA192_transition;
        }
        public String getDescription() {
            return "1271:11: ( expressionList )?";
        }
    }
    static final String DFA215_eotS =
        "\53\uffff";
    static final String DFA215_eofS =
        "\53\uffff";
    static final String DFA215_minS =
        "\1\4\1\101\47\uffff\1\0\1\uffff";
    static final String DFA215_maxS =
        "\1\167\1\101\47\uffff\1\0\1\uffff";
    static final String DFA215_acceptS =
        "\2\uffff\1\2\47\uffff\1\1";
    static final String DFA215_specialS =
        "\51\uffff\1\0\1\uffff}>";
    static final String[] DFA215_transitionS = {
            "\1\2\1\uffff\6\2\15\uffff\1\2\7\uffff\1\2\3\uffff\1\2\2\uffff"+
            "\1\2\12\uffff\1\2\3\uffff\10\2\1\uffff\2\2\2\uffff\3\2\2\uffff"+
            "\1\2\1\uffff\7\2\1\1\1\uffff\2\2\2\uffff\6\2\20\uffff\2\2\2"+
            "\uffff\6\2",
            "\1\51",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            ""
    };

    static final short[] DFA215_eot = DFA.unpackEncodedString(DFA215_eotS);
    static final short[] DFA215_eof = DFA.unpackEncodedString(DFA215_eofS);
    static final char[] DFA215_min = DFA.unpackEncodedStringToUnsignedChars(DFA215_minS);
    static final char[] DFA215_max = DFA.unpackEncodedStringToUnsignedChars(DFA215_maxS);
    static final short[] DFA215_accept = DFA.unpackEncodedString(DFA215_acceptS);
    static final short[] DFA215_special = DFA.unpackEncodedString(DFA215_specialS);
    static final short[][] DFA215_transition;

    static {
        int numStates = DFA215_transitionS.length;
        DFA215_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA215_transition[i] = DFA.unpackEncodedString(DFA215_transitionS[i]);
        }
    }

    class DFA215 extends DFA {

        public DFA215(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 215;
            this.eot = DFA215_eot;
            this.eof = DFA215_eof;
            this.min = DFA215_min;
            this.max = DFA215_max;
            this.accept = DFA215_accept;
            this.special = DFA215_special;
            this.transition = DFA215_transition;
        }
        public String getDescription() {
            return "782:16: ( 'if' parExpression )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA215_41 = input.LA(1);

                         
                        int index215_41 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred169_Java()) ) {s = 42;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index215_41);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 215, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_annotations_in_compilationUnit81 = new BitSet(new long[]{0x007FE0804F000022L,0x0000000000000080L});
    public static final BitSet FOLLOW_packageDeclaration_in_compilationUnit92 = new BitSet(new long[]{0x007FE0804E000022L,0x0000000000000080L});
    public static final BitSet FOLLOW_importDeclaration_in_compilationUnit103 = new BitSet(new long[]{0x007FE0804E000022L,0x0000000000000080L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit114 = new BitSet(new long[]{0x007FE0804A000022L,0x0000000000000080L});
    public static final BitSet FOLLOW_24_in_packageDeclaration132 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_packageDeclaration134 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_packageDeclaration136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_importDeclaration153 = new BitSet(new long[]{0x0000000008000010L});
    public static final BitSet FOLLOW_27_in_importDeclaration155 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_importDeclaration158 = new BitSet(new long[]{0x0000000012000000L});
    public static final BitSet FOLLOW_28_in_importDeclaration161 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_importDeclaration163 = new BitSet(new long[]{0x0000000012000000L});
    public static final BitSet FOLLOW_28_in_importDeclaration168 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_importDeclaration170 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_importDeclaration174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration191 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_typeDeclaration201 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifier_in_classOrInterfaceDeclaration218 = new BitSet(new long[]{0x007FE08048000020L,0x0000000000000080L});
    public static final BitSet FOLLOW_classDeclaration_in_classOrInterfaceDeclaration222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_classDeclaration244 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_classDeclaration254 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_normalClassDeclaration271 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_normalClassDeclaration273 = new BitSet(new long[]{0x0000002380000000L});
    public static final BitSet FOLLOW_typeParameters_in_normalClassDeclaration276 = new BitSet(new long[]{0x0000002380000000L});
    public static final BitSet FOLLOW_31_in_normalClassDeclaration289 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_type_in_normalClassDeclaration291 = new BitSet(new long[]{0x0000002380000000L});
    public static final BitSet FOLLOW_32_in_normalClassDeclaration304 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_typeList_in_normalClassDeclaration306 = new BitSet(new long[]{0x0000002380000000L});
    public static final BitSet FOLLOW_classBody_in_normalClassDeclaration318 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_typeParameters335 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_typeParameter_in_typeParameters337 = new BitSet(new long[]{0x0000000C00000000L});
    public static final BitSet FOLLOW_34_in_typeParameters340 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_typeParameter_in_typeParameters342 = new BitSet(new long[]{0x0000000C00000000L});
    public static final BitSet FOLLOW_35_in_typeParameters346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_typeParameter363 = new BitSet(new long[]{0x0000000080000002L});
    public static final BitSet FOLLOW_31_in_typeParameter366 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_bound_in_typeParameter368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_bound387 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_36_in_bound390 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_type_in_bound392 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_ENUM_in_enumDeclaration411 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_enumDeclaration413 = new BitSet(new long[]{0x0000002100000000L});
    public static final BitSet FOLLOW_32_in_enumDeclaration416 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_typeList_in_enumDeclaration418 = new BitSet(new long[]{0x0000002100000000L});
    public static final BitSet FOLLOW_enumBody_in_enumDeclaration422 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_enumBody439 = new BitSet(new long[]{0x0000004402000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_enumConstants_in_enumBody441 = new BitSet(new long[]{0x0000004402000000L});
    public static final BitSet FOLLOW_34_in_enumBody444 = new BitSet(new long[]{0x0000004002000000L});
    public static final BitSet FOLLOW_enumBodyDeclarations_in_enumBody447 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_enumBody450 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumConstant_in_enumConstants467 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_enumConstants470 = new BitSet(new long[]{0x0000000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_enumConstant_in_enumConstants472 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_annotations_in_enumConstant491 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_enumConstant494 = new BitSet(new long[]{0x0000002380000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_enumConstant497 = new BitSet(new long[]{0x0000002380000002L});
    public static final BitSet FOLLOW_classBody_in_enumConstant502 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_enumBodyDeclarations521 = new BitSet(new long[]{0x7FFFE1A24A000032L,0x0000000000000080L});
    public static final BitSet FOLLOW_classBodyDeclaration_in_enumBodyDeclarations524 = new BitSet(new long[]{0x7FFFE1A24A000032L,0x0000000000000080L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration543 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_normalInterfaceDeclaration572 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_normalInterfaceDeclaration574 = new BitSet(new long[]{0x0000002280000000L});
    public static final BitSet FOLLOW_typeParameters_in_normalInterfaceDeclaration576 = new BitSet(new long[]{0x0000002280000000L});
    public static final BitSet FOLLOW_31_in_normalInterfaceDeclaration580 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_typeList_in_normalInterfaceDeclaration582 = new BitSet(new long[]{0x0000002280000000L});
    public static final BitSet FOLLOW_interfaceBody_in_normalInterfaceDeclaration586 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeList603 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_typeList606 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_type_in_typeList608 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_37_in_classBody627 = new BitSet(new long[]{0x7FFFE1E24A000030L,0x0000000000000080L});
    public static final BitSet FOLLOW_classBodyDeclaration_in_classBody629 = new BitSet(new long[]{0x7FFFE1E24A000030L,0x0000000000000080L});
    public static final BitSet FOLLOW_38_in_classBody632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_interfaceBody649 = new BitSet(new long[]{0x7FFFE1C24A000030L,0x0000000000000080L});
    public static final BitSet FOLLOW_interfaceBodyDeclaration_in_interfaceBody651 = new BitSet(new long[]{0x7FFFE1C24A000030L,0x0000000000000080L});
    public static final BitSet FOLLOW_38_in_interfaceBody654 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_classBodyDeclaration671 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_classBodyDeclaration679 = new BitSet(new long[]{0x0000002008000000L});
    public static final BitSet FOLLOW_block_in_classBodyDeclaration682 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifier_in_classBodyDeclaration690 = new BitSet(new long[]{0x7FFFE1A24A000030L,0x0000000000000080L});
    public static final BitSet FOLLOW_memberDecl_in_classBodyDeclaration693 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_genericMethodOrConstructorDecl_in_memberDecl710 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodDeclaration_in_memberDecl718 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldDeclaration_in_memberDecl726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_memberDecl734 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_memberDecl736 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_voidMethodDeclaratorRest_in_memberDecl738 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_memberDecl746 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_constructorDeclaratorRest_in_memberDecl748 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_memberDecl756 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_memberDecl764 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeParameters_in_genericMethodOrConstructorDecl781 = new BitSet(new long[]{0x7F80010000000010L});
    public static final BitSet FOLLOW_genericMethodOrConstructorRest_in_genericMethodOrConstructorDecl783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_genericMethodOrConstructorRest801 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_40_in_genericMethodOrConstructorRest805 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_genericMethodOrConstructorRest808 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_methodDeclaratorRest_in_genericMethodOrConstructorRest810 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_genericMethodOrConstructorRest818 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_constructorDeclaratorRest_in_genericMethodOrConstructorRest820 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_methodDeclaration842 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_methodDeclaration844 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_methodDeclaratorRest_in_methodDeclaration846 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_fieldDeclaration863 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarators_in_fieldDeclaration865 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_fieldDeclaration867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifier_in_interfaceBodyDeclaration884 = new BitSet(new long[]{0x7FFFE18248000030L,0x0000000000000080L});
    public static final BitSet FOLLOW_interfaceMemberDecl_in_interfaceBodyDeclaration887 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_interfaceBodyDeclaration897 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodOrFieldDecl_in_interfaceMemberDecl914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceGenericMethodDecl_in_interfaceMemberDecl924 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_interfaceMemberDecl934 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_interfaceMemberDecl936 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_voidInterfaceMethodDeclaratorRest_in_interfaceMemberDecl938 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_interfaceMemberDecl948 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_interfaceMemberDecl958 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_interfaceMethodOrFieldDecl975 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_interfaceMethodOrFieldDecl977 = new BitSet(new long[]{0x0000120000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodOrFieldRest_in_interfaceMethodOrFieldDecl979 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constantDeclaratorsRest_in_interfaceMethodOrFieldRest996 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_interfaceMethodOrFieldRest998 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodDeclaratorRest_in_interfaceMethodOrFieldRest1006 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_methodDeclaratorRest1023 = new BitSet(new long[]{0x00000A200A000000L});
    public static final BitSet FOLLOW_41_in_methodDeclaratorRest1026 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_methodDeclaratorRest1028 = new BitSet(new long[]{0x00000A200A000000L});
    public static final BitSet FOLLOW_43_in_methodDeclaratorRest1041 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_methodDeclaratorRest1043 = new BitSet(new long[]{0x000000200A000000L});
    public static final BitSet FOLLOW_methodBody_in_methodDeclaratorRest1059 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_methodDeclaratorRest1073 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_voidMethodDeclaratorRest1100 = new BitSet(new long[]{0x000008200A000000L});
    public static final BitSet FOLLOW_43_in_voidMethodDeclaratorRest1103 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_voidMethodDeclaratorRest1105 = new BitSet(new long[]{0x000000200A000000L});
    public static final BitSet FOLLOW_methodBody_in_voidMethodDeclaratorRest1121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_voidMethodDeclaratorRest1135 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_interfaceMethodDeclaratorRest1162 = new BitSet(new long[]{0x00000A0002000000L});
    public static final BitSet FOLLOW_41_in_interfaceMethodDeclaratorRest1165 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_interfaceMethodDeclaratorRest1167 = new BitSet(new long[]{0x00000A0002000000L});
    public static final BitSet FOLLOW_43_in_interfaceMethodDeclaratorRest1172 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_interfaceMethodDeclaratorRest1174 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_interfaceMethodDeclaratorRest1178 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeParameters_in_interfaceGenericMethodDecl1195 = new BitSet(new long[]{0x7F80010000000010L});
    public static final BitSet FOLLOW_type_in_interfaceGenericMethodDecl1198 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_40_in_interfaceGenericMethodDecl1202 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_interfaceGenericMethodDecl1205 = new BitSet(new long[]{0x0000120000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodDeclaratorRest_in_interfaceGenericMethodDecl1215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_voidInterfaceMethodDeclaratorRest1232 = new BitSet(new long[]{0x0000080002000000L});
    public static final BitSet FOLLOW_43_in_voidInterfaceMethodDeclaratorRest1235 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_voidInterfaceMethodDeclaratorRest1237 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_voidInterfaceMethodDeclaratorRest1241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_constructorDeclaratorRest1258 = new BitSet(new long[]{0x0000082008000000L});
    public static final BitSet FOLLOW_43_in_constructorDeclaratorRest1261 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedNameList_in_constructorDeclaratorRest1263 = new BitSet(new long[]{0x0000002008000000L});
    public static final BitSet FOLLOW_methodBody_in_constructorDeclaratorRest1267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_constantDeclarator1284 = new BitSet(new long[]{0x0000120000000000L});
    public static final BitSet FOLLOW_constantDeclaratorRest_in_constantDeclarator1286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclarator_in_variableDeclarators1303 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_variableDeclarators1306 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarator_in_variableDeclarators1308 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_Identifier_in_variableDeclarator1355 = new BitSet(new long[]{0x0000120000000000L});
    public static final BitSet FOLLOW_variableDeclaratorRest_in_variableDeclarator1359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_variableDeclaratorRest1387 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_variableDeclaratorRest1389 = new BitSet(new long[]{0x0000120000000002L});
    public static final BitSet FOLLOW_44_in_variableDeclaratorRest1394 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_variableInitializer_in_variableDeclaratorRest1396 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_variableDeclaratorRest1406 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_variableInitializer_in_variableDeclaratorRest1408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constantDeclaratorRest_in_constantDeclaratorsRest1433 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_constantDeclaratorsRest1436 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_constantDeclarator_in_constantDeclaratorsRest1438 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_41_in_constantDeclaratorRest1458 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_constantDeclaratorRest1460 = new BitSet(new long[]{0x0000120000000000L});
    public static final BitSet FOLLOW_44_in_constantDeclaratorRest1464 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_variableInitializer_in_constantDeclaratorRest1466 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_variableDeclaratorId1483 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_variableDeclaratorId1486 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_variableDeclaratorId1488 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer1507 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer1517 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_arrayInitializer1534 = new BitSet(new long[]{0x7F80016200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer1537 = new BitSet(new long[]{0x0000004400000000L});
    public static final BitSet FOLLOW_34_in_arrayInitializer1540 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer1542 = new BitSet(new long[]{0x0000004400000000L});
    public static final BitSet FOLLOW_34_in_arrayInitializer1547 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_arrayInitializer1554 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_modifier1573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_modifier1583 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_modifier1593 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_modifier1603 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_modifier1613 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_modifier1623 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_modifier1633 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_modifier1643 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_modifier1653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_modifier1663 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_modifier1673 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_modifier1683 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_packageOrTypeName1700 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_28_in_packageOrTypeName1703 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_packageOrTypeName1705 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_Identifier_in_enumConstantName1726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_typeName1745 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_packageOrTypeName_in_typeName1755 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_typeName1757 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_typeName1759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_type1776 = new BitSet(new long[]{0x0000020210000002L});
    public static final BitSet FOLLOW_typeArguments_in_type1779 = new BitSet(new long[]{0x0000020010000002L});
    public static final BitSet FOLLOW_28_in_type1784 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_type1786 = new BitSet(new long[]{0x0000020210000002L});
    public static final BitSet FOLLOW_typeArguments_in_type1789 = new BitSet(new long[]{0x0000020010000002L});
    public static final BitSet FOLLOW_41_in_type1797 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_type1799 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_primitiveType_in_type1809 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_type1812 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_type1814 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_set_in_primitiveType0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_variableModifier1908 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_variableModifier1918 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_typeArguments1935 = new BitSet(new long[]{0xFF80000000000010L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments1937 = new BitSet(new long[]{0x0000000C00000000L});
    public static final BitSet FOLLOW_34_in_typeArguments1940 = new BitSet(new long[]{0xFF80000000000010L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments1942 = new BitSet(new long[]{0x0000000C00000000L});
    public static final BitSet FOLLOW_35_in_typeArguments1946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument1963 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_63_in_typeArgument1971 = new BitSet(new long[]{0x0000000080000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_set_in_typeArgument1974 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_type_in_typeArgument1982 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList2001 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_qualifiedNameList2004 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList2006 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_65_in_formalParameters2025 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000084L});
    public static final BitSet FOLLOW_formalParameterDecls_in_formalParameters2027 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_formalParameters2030 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_formalParameterDecls2047 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_type_in_formalParameterDecls2050 = new BitSet(new long[]{0x0000000000000012L,0x0000000000000008L});
    public static final BitSet FOLLOW_formalParameterDeclsRest_in_formalParameterDecls2052 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest2070 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_formalParameterDeclsRest2073 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_formalParameterDecls_in_formalParameterDeclsRest2075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_67_in_formalParameterDeclsRest2087 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest2089 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_methodBody2106 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_qualifiedName2123 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_28_in_qualifiedName2126 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_qualifiedName2128 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_integerLiteral_in_literal2150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FloatingPointLiteral_in_literal2160 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharacterLiteral_in_literal2170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_literal2180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_literal2190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_68_in_literal2200 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_integerLiteral0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_booleanLiteral0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_annotations2287 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_71_in_annotation2305 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_annotationName_in_annotation2307 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_annotation2310 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC000380000F7L});
    public static final BitSet FOLLOW_elementValuePairs_in_annotation2312 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_annotation2315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_annotationName2334 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_28_in_annotationName2337 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_annotationName2339 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs2358 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_elementValuePairs2361 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC000380000F3L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs2363 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_Identifier_in_elementValuePair2383 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_elementValuePair2385 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC000380000F3L});
    public static final BitSet FOLLOW_elementValue_in_elementValuePair2389 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_elementValue2406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_elementValue2416 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elementValueArrayInitializer_in_elementValue2426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_elementValueArrayInitializer2443 = new BitSet(new long[]{0x7F80016200000FD0L,0x00FCC000380000F3L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer2446 = new BitSet(new long[]{0x0000004400000000L});
    public static final BitSet FOLLOW_34_in_elementValueArrayInitializer2449 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC000380000F3L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer2451 = new BitSet(new long[]{0x0000004400000000L});
    public static final BitSet FOLLOW_38_in_elementValueArrayInitializer2458 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_71_in_annotationTypeDeclaration2475 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_annotationTypeDeclaration2477 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_annotationTypeDeclaration2479 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_annotationTypeBody_in_annotationTypeDeclaration2481 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_annotationTypeBody2498 = new BitSet(new long[]{0x7FFFE0C048000030L,0x0000000000000080L});
    public static final BitSet FOLLOW_annotationTypeElementDeclarations_in_annotationTypeBody2501 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_annotationTypeBody2505 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeElementDeclaration_in_annotationTypeElementDeclarations2523 = new BitSet(new long[]{0x7FFFE08048000032L,0x0000000000000080L});
    public static final BitSet FOLLOW_annotationTypeElementDeclaration_in_annotationTypeElementDeclarations2527 = new BitSet(new long[]{0x7FFFE08048000032L,0x0000000000000080L});
    public static final BitSet FOLLOW_modifier_in_annotationTypeElementDeclaration2547 = new BitSet(new long[]{0x7FFFE08048000030L,0x0000000000000080L});
    public static final BitSet FOLLOW_annotationTypeElementRest_in_annotationTypeElementDeclaration2551 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_annotationTypeElementRest2568 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_annotationMethodOrConstantRest_in_annotationTypeElementRest2570 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_annotationTypeElementRest2572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_annotationTypeElementRest2582 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_annotationTypeElementRest2584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_annotationTypeElementRest2595 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_annotationTypeElementRest2597 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_annotationTypeElementRest2608 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_annotationTypeElementRest2610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_annotationTypeElementRest2621 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_annotationTypeElementRest2623 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationMethodRest_in_annotationMethodOrConstantRest2641 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationConstantRest_in_annotationMethodOrConstantRest2651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_annotationMethodRest2669 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_annotationMethodRest2671 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_annotationMethodRest2673 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_defaultValue_in_annotationMethodRest2676 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclarators_in_annotationConstantRest2702 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_defaultValue2721 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC000380000F3L});
    public static final BitSet FOLLOW_elementValue_in_defaultValue2723 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_block2769 = new BitSet(new long[]{0x7FFFE1E24A000FF0L,0x00FCC0003F37FAF3L});
    public static final BitSet FOLLOW_blockStatement_in_block2771 = new BitSet(new long[]{0x7FFFE1E24A000FF0L,0x00FCC0003F37FAF3L});
    public static final BitSet FOLLOW_38_in_block2774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_blockStatement2791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_blockStatement2799 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statement_in_blockStatement2811 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_localVariableDeclaration2865 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_type_in_localVariableDeclaration2888 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarators_in_localVariableDeclaration2904 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_localVariableDeclaration2906 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_statement2923 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_statement2931 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_statement2933 = new BitSet(new long[]{0x0000000002000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_statement2936 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_statement2938 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_statement2942 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ifStatement_in_statement2950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forStatement_in_statement2958 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_whileStatement_in_statement2967 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_75_in_statement2975 = new BitSet(new long[]{0x7FFFE1A24A000FF0L,0x00FCC0003F37FAF3L});
    public static final BitSet FOLLOW_statement_in_statement2977 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_76_in_statement2979 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_statement2981 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_statement2983 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tryStatement_in_statement2996 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_77_in_statement3011 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_statement3013 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_statement3015 = new BitSet(new long[]{0x0000004000000000L,0x0000000040000100L});
    public static final BitSet FOLLOW_switchBlockStatementGroups_in_statement3017 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_statement3019 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_statement3027 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_statement3029 = new BitSet(new long[]{0x0000002008000000L});
    public static final BitSet FOLLOW_block_in_statement3031 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_statement3039 = new BitSet(new long[]{0x7F80012202000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_statement3041 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_statement3044 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_throwStatement_in_statement3053 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_79_in_statement3061 = new BitSet(new long[]{0x0000000002000010L});
    public static final BitSet FOLLOW_Identifier_in_statement3063 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_statement3066 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_80_in_statement3074 = new BitSet(new long[]{0x0000000002000010L});
    public static final BitSet FOLLOW_Identifier_in_statement3076 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_statement3079 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifyStatement_in_statement3092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_updateStatement_in_statement3099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_retractStatement_in_statement3106 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_statement3114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statementExpression_in_statement3122 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_statement3124 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_statement3132 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_statement3134 = new BitSet(new long[]{0x7FFFE1A24A000FF0L,0x00FCC0003F37FAF3L});
    public static final BitSet FOLLOW_statement_in_statement3136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_81_in_throwStatement3169 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_throwStatement3175 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_throwStatement3185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_82_in_ifStatement3241 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_ifStatement3243 = new BitSet(new long[]{0x7FFFE1A24A000FF0L,0x00FCC0003F37FAF3L});
    public static final BitSet FOLLOW_statement_in_ifStatement3261 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_83_in_ifStatement3288 = new BitSet(new long[]{0x7FFFE1A24A000FF0L,0x00FCC0003F37FAF3L});
    public static final BitSet FOLLOW_82_in_ifStatement3292 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_ifStatement3294 = new BitSet(new long[]{0x7FFFE1A24A000FF0L,0x00FCC0003F37FAF3L});
    public static final BitSet FOLLOW_statement_in_ifStatement3325 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_84_in_forStatement3407 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_forStatement3411 = new BitSet(new long[]{0x7F82012202000FD0L,0x00FCC000380000F3L});
    public static final BitSet FOLLOW_variableModifier_in_forStatement3447 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_type_in_forStatement3486 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_forStatement3512 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_forStatement3539 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_forStatement3541 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_forInit_in_forStatement3577 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_forStatement3582 = new BitSet(new long[]{0x7F80012202000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_forStatement3584 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_forStatement3587 = new BitSet(new long[]{0x7F82012200000FD0L,0x00FCC000380000F7L});
    public static final BitSet FOLLOW_forUpdate_in_forStatement3589 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_forStatement3635 = new BitSet(new long[]{0x7FFFE1A24A000FF0L,0x00FCC0003F37FAF3L});
    public static final BitSet FOLLOW_statement_in_forStatement3639 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_whileStatement3698 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_whileStatement3700 = new BitSet(new long[]{0x7FFFE1A24A000FF0L,0x00FCC0003F37FAF3L});
    public static final BitSet FOLLOW_statement_in_whileStatement3717 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_85_in_tryStatement3770 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_tryStatement3781 = new BitSet(new long[]{0x7FFFE1E24A000FF0L,0x00FCC0003F37FAF3L});
    public static final BitSet FOLLOW_blockStatement_in_tryStatement3783 = new BitSet(new long[]{0x7FFFE1E24A000FF0L,0x00FCC0003F37FAF3L});
    public static final BitSet FOLLOW_38_in_tryStatement3794 = new BitSet(new long[]{0x0000000000000002L,0x0000000000C00000L});
    public static final BitSet FOLLOW_86_in_tryStatement3812 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_tryStatement3814 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_formalParameter_in_tryStatement3816 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_tryStatement3818 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_tryStatement3830 = new BitSet(new long[]{0x7FFFE1E24A000FF0L,0x00FCC0003F37FAF3L});
    public static final BitSet FOLLOW_blockStatement_in_tryStatement3832 = new BitSet(new long[]{0x7FFFE1E24A000FF0L,0x00FCC0003F37FAF3L});
    public static final BitSet FOLLOW_38_in_tryStatement3845 = new BitSet(new long[]{0x0000000000000002L,0x0000000000C00000L});
    public static final BitSet FOLLOW_87_in_tryStatement3879 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_tryStatement3891 = new BitSet(new long[]{0x7FFFE1E24A000FF0L,0x00FCC0003F37FAF3L});
    public static final BitSet FOLLOW_blockStatement_in_tryStatement3893 = new BitSet(new long[]{0x7FFFE1E24A000FF0L,0x00FCC0003F37FAF3L});
    public static final BitSet FOLLOW_38_in_tryStatement3907 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_88_in_modifyStatement3949 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_modifyStatement3951 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_modifyStatement3963 = new BitSet(new long[]{0x7F80016200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_modifyStatement3971 = new BitSet(new long[]{0x0000004400000000L});
    public static final BitSet FOLLOW_34_in_modifyStatement3987 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_modifyStatement3991 = new BitSet(new long[]{0x0000004400000000L});
    public static final BitSet FOLLOW_38_in_modifyStatement4015 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_updateStatement4053 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_updateStatement4055 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_updateStatement4061 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_updateStatement4071 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_90_in_retractStatement4113 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_retractStatement4115 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_retractStatement4121 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_retractStatement4131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_91_in_epStatement4184 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_epStatement4186 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_StringLiteral_in_epStatement4190 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_epStatement4194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_92_in_epStatement4220 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_epStatement4222 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_StringLiteral_in_epStatement4226 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_epStatement4230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_93_in_epStatement4256 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_epStatement4258 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_StringLiteral_in_epStatement4262 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_epStatement4266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_formalParameter4310 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_type_in_formalParameter4313 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameter4315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups4333 = new BitSet(new long[]{0x0000000000000002L,0x0000000040000100L});
    public static final BitSet FOLLOW_switchLabel_in_switchBlockStatementGroup4352 = new BitSet(new long[]{0x7FFFE1A24A000FF2L,0x00FCC0003F37FAF3L});
    public static final BitSet FOLLOW_blockStatement_in_switchBlockStatementGroup4354 = new BitSet(new long[]{0x7FFFE1A24A000FF2L,0x00FCC0003F37FAF3L});
    public static final BitSet FOLLOW_94_in_switchLabel4372 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_constantExpression_in_switchLabel4374 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_switchLabel4376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_94_in_switchLabel4386 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_enumConstantName_in_switchLabel4388 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_switchLabel4390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_switchLabel4400 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_switchLabel4402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_moreStatementExpressions4420 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_statementExpression_in_moreStatementExpressions4422 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_forVarControl_in_forControl4480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forInit_in_forControl4488 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_forControl4491 = new BitSet(new long[]{0x7F80012202000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_forControl4493 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_forControl4496 = new BitSet(new long[]{0x7F82012200000FD2L,0x00FCC000380000F3L});
    public static final BitSet FOLLOW_forUpdate_in_forControl4498 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_forInit4518 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_type_in_forInit4553 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarators_in_forInit4573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_forInit4581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_forVarControl4600 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_type_in_forVarControl4635 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_forVarControl4657 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_forVarControl4678 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_forVarControl4680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_forUpdate4697 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_parExpression4716 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_parExpression4718 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_parExpression4720 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList4739 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_expressionList4742 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_expressionList4744 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_expression_in_statementExpression4763 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_constantExpression4780 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression4797 = new BitSet(new long[]{0x0000100A00000002L,0x0000007F80000000L});
    public static final BitSet FOLLOW_assignmentOperator_in_expression4800 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_expression4802 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_assignmentOperator4821 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_95_in_assignmentOperator4831 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_96_in_assignmentOperator4841 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_97_in_assignmentOperator4851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_98_in_assignmentOperator4861 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_99_in_assignmentOperator4871 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_100_in_assignmentOperator4881 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_101_in_assignmentOperator4891 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_102_in_assignmentOperator4901 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_assignmentOperator4911 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_assignmentOperator4913 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_assignmentOperator4915 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_assignmentOperator4925 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_assignmentOperator4927 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_assignmentOperator4929 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_assignmentOperator4939 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_assignmentOperator4941 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_assignmentOperator4943 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_assignmentOperator4945 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression4964 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_63_in_conditionalExpression4968 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression4970 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_conditionalExpression4972 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression4974 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression4996 = new BitSet(new long[]{0x0000000000000002L,0x0000008000000000L});
    public static final BitSet FOLLOW_103_in_conditionalOrExpression5000 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression5002 = new BitSet(new long[]{0x0000000000000002L,0x0000008000000000L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5024 = new BitSet(new long[]{0x0000000000000002L,0x0000010000000000L});
    public static final BitSet FOLLOW_104_in_conditionalAndExpression5028 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5030 = new BitSet(new long[]{0x0000000000000002L,0x0000010000000000L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5052 = new BitSet(new long[]{0x0000000000000002L,0x0000020000000000L});
    public static final BitSet FOLLOW_105_in_inclusiveOrExpression5056 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5058 = new BitSet(new long[]{0x0000000000000002L,0x0000020000000000L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression5080 = new BitSet(new long[]{0x0000000000000002L,0x0000040000000000L});
    public static final BitSet FOLLOW_106_in_exclusiveOrExpression5084 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression5086 = new BitSet(new long[]{0x0000000000000002L,0x0000040000000000L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression5108 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_36_in_andExpression5112 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression5114 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression5136 = new BitSet(new long[]{0x0000000000000002L,0x0000180000000000L});
    public static final BitSet FOLLOW_set_in_equalityExpression5140 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression5148 = new BitSet(new long[]{0x0000000000000002L,0x0000180000000000L});
    public static final BitSet FOLLOW_relationalExpression_in_instanceOfExpression5170 = new BitSet(new long[]{0x0000000000000002L,0x0000200000000000L});
    public static final BitSet FOLLOW_109_in_instanceOfExpression5173 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression5175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression5196 = new BitSet(new long[]{0x0000000A00000002L});
    public static final BitSet FOLLOW_relationalOp_in_relationalExpression5200 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression5202 = new BitSet(new long[]{0x0000000A00000002L});
    public static final BitSet FOLLOW_33_in_relationalOp5223 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_relationalOp5225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_relationalOp5229 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_relationalOp5231 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_relationalOp5235 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_relationalOp5239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression5259 = new BitSet(new long[]{0x0000000A00000002L});
    public static final BitSet FOLLOW_shiftOp_in_shiftExpression5263 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression5265 = new BitSet(new long[]{0x0000000A00000002L});
    public static final BitSet FOLLOW_33_in_shiftOp5295 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_shiftOp5297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_shiftOp5301 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_shiftOp5303 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_shiftOp5305 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_shiftOp5309 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_shiftOp5311 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression5332 = new BitSet(new long[]{0x0000000000000002L,0x0000C00000000000L});
    public static final BitSet FOLLOW_set_in_additiveExpression5336 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression5344 = new BitSet(new long[]{0x0000000000000002L,0x0000C00000000000L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression5366 = new BitSet(new long[]{0x0000000020000002L,0x0003000000000000L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression5370 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression5384 = new BitSet(new long[]{0x0000000020000002L,0x0003000000000000L});
    public static final BitSet FOLLOW_110_in_unaryExpression5406 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression5408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_111_in_unaryExpression5416 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression5418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_114_in_unaryExpression5428 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_primary_in_unaryExpression5430 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_115_in_unaryExpression5440 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_primary_in_unaryExpression5442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression5452 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_116_in_unaryExpressionNotPlusMinus5471 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_117_in_unaryExpressionNotPlusMinus5482 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus5484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus5494 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus5504 = new BitSet(new long[]{0x0000020010000002L,0x000C000000000000L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus5506 = new BitSet(new long[]{0x0000020010000002L,0x000C000000000000L});
    public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus5509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_castExpression5532 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression5534 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_castExpression5536 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression5538 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_castExpression5547 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_type_in_castExpression5550 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_castExpression5554 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_castExpression5557 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression5559 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary5576 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_primary5586 = new BitSet(new long[]{0x0000000000000010L,0x0040000000000001L});
    public static final BitSet FOLLOW_explicitGenericInvocationSuffix_in_primary5597 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_118_in_primary5601 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_primary5603 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_118_in_primary5614 = new BitSet(new long[]{0x0000020010000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_primary5617 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_primary5619 = new BitSet(new long[]{0x0000020010000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary5624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_primary5636 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_superSuffix_in_primary5638 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_epStatement_in_primary5648 = new BitSet(new long[]{0x0000020010000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_primary5651 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_primary5653 = new BitSet(new long[]{0x0000020010000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary5658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary5670 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_119_in_primary5680 = new BitSet(new long[]{0x7F80000200000010L});
    public static final BitSet FOLLOW_creator_in_primary5682 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_primary5694 = new BitSet(new long[]{0x0000020010000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_primary5699 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_primary5701 = new BitSet(new long[]{0x0000020010000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary5706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary5718 = new BitSet(new long[]{0x0000020010000000L});
    public static final BitSet FOLLOW_41_in_primary5721 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_primary5723 = new BitSet(new long[]{0x0000020010000000L});
    public static final BitSet FOLLOW_28_in_primary5727 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_primary5729 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_primary5739 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_primary5741 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_primary5743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_identifierSuffix5761 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_identifierSuffix5763 = new BitSet(new long[]{0x0000020010000000L});
    public static final BitSet FOLLOW_28_in_identifierSuffix5767 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_identifierSuffix5769 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_identifierSuffix5778 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_identifierSuffix5780 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_identifierSuffix5782 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix5795 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_identifierSuffix5805 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_identifierSuffix5807 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_identifierSuffix5817 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_explicitGenericInvocation_in_identifierSuffix5819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_identifierSuffix5829 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_118_in_identifierSuffix5831 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_identifierSuffix5841 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_identifierSuffix5843 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix5845 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_identifierSuffix5855 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_119_in_identifierSuffix5857 = new BitSet(new long[]{0x0000000200000010L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_identifierSuffix5860 = new BitSet(new long[]{0x0000000200000010L});
    public static final BitSet FOLLOW_innerCreator_in_identifierSuffix5864 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator5881 = new BitSet(new long[]{0x7F80000200000010L});
    public static final BitSet FOLLOW_createdName_in_creator5884 = new BitSet(new long[]{0x0000020000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_arrayCreatorRest_in_creator5895 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator5899 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_createdName5917 = new BitSet(new long[]{0x0000000210000002L});
    public static final BitSet FOLLOW_typeArguments_in_createdName5919 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_28_in_createdName5931 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_createdName5933 = new BitSet(new long[]{0x0000000210000002L});
    public static final BitSet FOLLOW_typeArguments_in_createdName5935 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName5946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_innerCreator5963 = new BitSet(new long[]{0x0000020000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_classCreatorRest_in_innerCreator5965 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_arrayCreatorRest5982 = new BitSet(new long[]{0x7F80052200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_42_in_arrayCreatorRest5996 = new BitSet(new long[]{0x0000022000000000L});
    public static final BitSet FOLLOW_41_in_arrayCreatorRest5999 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_arrayCreatorRest6001 = new BitSet(new long[]{0x0000022000000000L});
    public static final BitSet FOLLOW_arrayInitializer_in_arrayCreatorRest6005 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest6019 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_arrayCreatorRest6021 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_arrayCreatorRest6024 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest6026 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_arrayCreatorRest6028 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_arrayCreatorRest6033 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_arrayCreatorRest6035 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest6064 = new BitSet(new long[]{0x0000002380000002L});
    public static final BitSet FOLLOW_classBody_in_classCreatorRest6066 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation6084 = new BitSet(new long[]{0x0000000000000010L,0x0000000000000001L});
    public static final BitSet FOLLOW_explicitGenericInvocationSuffix_in_explicitGenericInvocation6086 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_nonWildcardTypeArguments6103 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments6105 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_nonWildcardTypeArguments6107 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_explicitGenericInvocationSuffix6124 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_superSuffix_in_explicitGenericInvocationSuffix6126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_explicitGenericInvocationSuffix6136 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocationSuffix6138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_selector6155 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_selector6157 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_selector6160 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_selector6172 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_118_in_selector6174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_selector6184 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_64_in_selector6186 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_superSuffix_in_selector6188 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_selector6198 = new BitSet(new long[]{0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_119_in_selector6200 = new BitSet(new long[]{0x0000000200000010L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_selector6203 = new BitSet(new long[]{0x0000000200000010L});
    public static final BitSet FOLLOW_innerCreator_in_selector6207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_selector6217 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_selector6219 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_selector6221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix6238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_superSuffix6248 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_superSuffix6250 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix6253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_arguments6280 = new BitSet(new long[]{0x7F82012200000FD0L,0x00FCC000380000F7L});
    public static final BitSet FOLLOW_expressionList_in_arguments6282 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_arguments6285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotations_in_synpred1_Java81 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodDeclaration_in_synpred38_Java718 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldDeclaration_in_synpred39_Java726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_synpred85_Java1703 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_synpred85_Java1705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_synpred120_Java2287 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_synpred135_Java2582 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_synpred135_Java2584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_synpred137_Java2595 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_synpred137_Java2597 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_synpred139_Java2608 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_synpred139_Java2610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_synpred144_Java2791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_synpred145_Java2799 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_82_in_synpred169_Java3292 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_synpred169_Java3294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_83_in_synpred170_Java3288 = new BitSet(new long[]{0x7FFFE1A24A000FF0L,0x00FCC0003F37FAF3L});
    public static final BitSet FOLLOW_82_in_synpred170_Java3292 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_synpred170_Java3294 = new BitSet(new long[]{0x7FFFE1A24A000FF0L,0x00FCC0003F37FAF3L});
    public static final BitSet FOLLOW_statement_in_synpred170_Java3325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_synpred172_Java3447 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_type_in_synpred172_Java3486 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_synpred172_Java3512 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_synpred172_Java3539 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_synpred172_Java3541 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_94_in_synpred188_Java4372 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_constantExpression_in_synpred188_Java4374 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_synpred188_Java4376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_94_in_synpred189_Java4386 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_enumConstantName_in_synpred189_Java4388 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_synpred189_Java4390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forVarControl_in_synpred191_Java4480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_synpred196_Java4518 = new BitSet(new long[]{0x7F82000000000010L,0x0000000000000080L});
    public static final BitSet FOLLOW_type_in_synpred196_Java4553 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variableDeclarators_in_synpred196_Java4573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignmentOperator_in_synpred199_Java4800 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_synpred199_Java4802 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_synpred210_Java4925 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_synpred210_Java4927 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_synpred210_Java4929 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalOp_in_synpred220_Java5200 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_shiftExpression_in_synpred220_Java5202 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftOp_in_synpred224_Java5263 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_additiveExpression_in_synpred224_Java5265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_synpred226_Java5301 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_synpred226_Java5303 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_synpred226_Java5305 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_synpred238_Java5494 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_synpred242_Java5532 = new BitSet(new long[]{0x7F80000000000010L});
    public static final BitSet FOLLOW_primitiveType_in_synpred242_Java5534 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_66_in_synpred242_Java5536 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_unaryExpression_in_synpred242_Java5538 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_synpred243_Java5550 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_synpred247_Java5617 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_synpred247_Java5619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred248_Java5624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_synpred251_Java5651 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_synpred251_Java5653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred252_Java5658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_synpred256_Java5699 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_Identifier_in_synpred256_Java5701 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred257_Java5706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_synpred263_Java5778 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_synpred263_Java5780 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred263_Java5782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_synpred279_Java6024 = new BitSet(new long[]{0x7F80012200000FD0L,0x00FCC00038000073L});
    public static final BitSet FOLLOW_expression_in_synpred279_Java6026 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_synpred279_Java6028 = new BitSet(new long[]{0x0000000000000002L});

}