/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.lang;

import org.antlr.runtime.BitSet;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.lang.api.AbstractClassTypeDeclarationBuilder;
import org.drools.compiler.lang.api.AccumulateDescrBuilder;
import org.drools.compiler.lang.api.AccumulateImportDescrBuilder;
import org.drools.compiler.lang.api.AttributeDescrBuilder;
import org.drools.compiler.lang.api.AttributeSupportBuilder;
import org.drools.compiler.lang.api.BehaviorDescrBuilder;
import org.drools.compiler.lang.api.CEDescrBuilder;
import org.drools.compiler.lang.api.ConditionalBranchDescrBuilder;
import org.drools.compiler.lang.api.DeclareDescrBuilder;
import org.drools.compiler.lang.api.DescrBuilder;
import org.drools.compiler.lang.api.EnumDeclarationDescrBuilder;
import org.drools.compiler.lang.api.EnumLiteralDescrBuilder;
import org.drools.compiler.lang.api.FieldDescrBuilder;
import org.drools.compiler.lang.api.ForallDescrBuilder;
import org.drools.compiler.lang.api.FunctionDescrBuilder;
import org.drools.compiler.lang.api.GlobalDescrBuilder;
import org.drools.compiler.lang.api.NamedConsequenceDescrBuilder;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.api.PatternContainerDescrBuilder;
import org.drools.compiler.lang.api.PatternDescrBuilder;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.api.CollectDescrBuilder;
import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.EntryPointDeclarationDescrBuilder;
import org.drools.compiler.lang.api.EvalDescrBuilder;
import org.drools.compiler.lang.api.ImportDescrBuilder;
import org.drools.compiler.lang.api.QueryDescrBuilder;
import org.drools.compiler.lang.api.RuleDescrBuilder;
import org.drools.compiler.lang.api.TypeDeclarationDescrBuilder;
import org.drools.compiler.lang.api.WindowDeclarationDescrBuilder;
import org.kie.internal.builder.conf.LanguageLevelOption;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static org.drools.compiler.compiler.DRLFactory.lexerId;

/**
 * This is a class to hold all the helper functions/methods used
 * by the DRL parser
 */
public class ParserHelper {
    public final String[]                             statementKeywords        = new String[]{
                                                                               DroolsSoftKeywords.PACKAGE,
                                                                               DroolsSoftKeywords.IMPORT,
                                                                               DroolsSoftKeywords.GLOBAL,
                                                                               DroolsSoftKeywords.DECLARE,
                                                                               DroolsSoftKeywords.FUNCTION,
                                                                               DroolsSoftKeywords.RULE,
                                                                               DroolsSoftKeywords.QUERY
                                                                               };

    public List<DroolsParserException>                errors                   = new ArrayList<DroolsParserException>();
    public LinkedList<DroolsSentence>                 editorInterface          = null;
    public boolean                                    isEditorInterfaceEnabled = false;
    public boolean                                    lookaheadTest            = false;
    private Stack<Map<DroolsParaphraseTypes, String>> paraphrases              = new Stack<Map<DroolsParaphraseTypes, String>>();

    // parameters from parser
    private DroolsParserExceptionFactory              errorMessageFactory      = null;
    private TokenStream                               input                    = null;
    private RecognizerSharedState                     state                    = null;

    private String                                    leftMostExpr             = null;

    // helper attribute
    private boolean                                   hasOperator              = false;

    private final LanguageLevelOption                 languageLevel;

    public ParserHelper(TokenStream input,
                        RecognizerSharedState state,
                        LanguageLevelOption languageLevel) {
        this.errorMessageFactory = new DroolsParserExceptionFactory( paraphrases, languageLevel );
        this.input = input;
        this.state = state;
        this.languageLevel = languageLevel;
    }

    public LinkedList<DroolsSentence> getEditorInterface() {
        return editorInterface;
    }

    public void setLeftMostExpr( String value ) {
        this.leftMostExpr = value;
    }

    public String getLeftMostExpr() {
        return this.leftMostExpr;
    }

    public void enableEditorInterface() {
        isEditorInterfaceEnabled = true;
    }

    public void disableEditorInterface() {
        isEditorInterfaceEnabled = false;
    }

    public void setHasOperator( boolean hasOperator ) {
        this.hasOperator = hasOperator;
    }

    public boolean getHasOperator() {
        return hasOperator;
    }

    public void beginSentence( DroolsSentenceType sentenceType ) {
        if ( isEditorInterfaceEnabled ) {
            if ( null == editorInterface ) {
                editorInterface = new LinkedList<DroolsSentence>();
            }
            if (editorInterface.isEmpty()){
                DroolsSentence sentence = new DroolsSentence();
                sentence.setType( sentenceType );
                editorInterface.add( sentence );
            }
        }
    }

    public DroolsSentence getActiveSentence() {
        return editorInterface.getLast();
    }

    public void emit( List< ? > tokens,
                      DroolsEditorType editorType ) {
        if ( isEditorInterfaceEnabled && tokens != null ) {
            for ( Object activeObject : tokens ) {
                emit( (Token) activeObject,
                      editorType );
            }
        }
    }

    public void emit( Token token,
                      DroolsEditorType editorType ) {
        if ( isEditorInterfaceEnabled && token != null && editorType != null ) {
            ((DroolsToken) token).setEditorType( editorType );
            getActiveSentence().addContent( (DroolsToken) token );
        }
    }

    public void emit( boolean forceEmit,
                      int activeContext ) {
        if ( isEditorInterfaceEnabled ) {
            getActiveSentence().addContent( activeContext );
        }
    }

    public void emit( int activeContext ) {
        if ( isEditorInterfaceEnabled ) {
            emit( false,
                  activeContext );
        }
    }

    public DroolsToken getLastTokenOnList( LinkedList< ? > list ) {
        DroolsToken lastToken = null;
        for ( Object object : list ) {
            if ( object instanceof DroolsToken ) {
                lastToken = (DroolsToken) object;
            }
        }
        return lastToken;
    }

    public int getLastIntegerValue( LinkedList< ? > list ) {
        int lastIntergerValue = -1;
        for ( Object object : list ) {
            if ( object instanceof Integer ) {
                lastIntergerValue = (Integer) object;
            }
        }
        return lastIntergerValue;
    }

    public String retrieveLT( int LTNumber ) {
        if ( null == input ) return null;
        if ( null == input.LT( LTNumber ) ) return null;
        if ( null == input.LT( LTNumber ).getText() ) return null;

        return input.LT( LTNumber ).getText();
    }

    public boolean validateLT( int LTNumber,
                               String text ) {
        String text2Validate = retrieveLT( LTNumber );
        return text2Validate == null ? false : text2Validate.equals( text );
    }

    public boolean isPluggableEvaluator( int offset,
                                         boolean negated ) {
        String text2Validate = retrieveLT( offset );
        return text2Validate != null && DroolsSoftKeywords.isOperator(text2Validate, negated);
    }

    public boolean isPluggableEvaluator( boolean negated ) {
        return isPluggableEvaluator( 1,
                                     negated );
    }

    public boolean validateIdentifierKey( String text ) {
        return validateLT( 1,
                           text );
    }

    public boolean validateSpecialID( int index ) {
        return validateLT( index,
                           DroolsSoftKeywords.THIS ) ||
               validateLT( index,
                           DroolsSoftKeywords.SUPER ) ||
               validateLT( index,
                           DroolsSoftKeywords.NEW ) ||
               validateLT( index,
                           DroolsSoftKeywords.CLASS );
    }

    public boolean validateCEKeyword( int index ) {
        return validateLT( index,
                           DroolsSoftKeywords.NOT ) ||
               validateLT( index,
                           DroolsSoftKeywords.EXISTS ) ||
               validateLT( index,
                           DroolsSoftKeywords.FORALL ) ||
               validateLT( index,
                           DroolsSoftKeywords.AND ) ||
               validateLT( index,
                           DroolsSoftKeywords.OR ) ||
               validateLT( index,
                           DroolsSoftKeywords.COLLECT ) ||
               validateLT( index,
                           DroolsSoftKeywords.FROM ) ||
               validateLT( index,
                           DroolsSoftKeywords.END ) ||
               validateLT( index,
                           DroolsSoftKeywords.EVAL ) ||
               validateLT( index,
                           DroolsSoftKeywords.OVER ) ||
               validateLT( index,
                           DroolsSoftKeywords.THEN );
    }

    public boolean validateStatement( int index ) {
        boolean ret = false;
        for ( String st : statementKeywords ) {
            if ( validateLT( index,
                             st ) ) {
                ret = true;
                break;
            }
        }
        return ret || validateAttribute( index );
    }

    public boolean validateAttribute( int index ) {
        return validateLT( index,
                           DroolsSoftKeywords.SALIENCE ) ||
               validateLT( index,
                           DroolsSoftKeywords.ENABLED ) ||
               (validateLT( index,
                            DroolsSoftKeywords.NO ) &&
                 validateLT( index + 1,
                             "-" ) &&
                 validateLT( index + 2,
                             DroolsSoftKeywords.LOOP )) ||
               (validateLT( index,
                            DroolsSoftKeywords.AUTO ) &&
                 validateLT( index + 1,
                             "-" ) &&
                 validateLT( index + 2,
                             DroolsSoftKeywords.FOCUS )) ||
               (validateLT( index,
                            DroolsSoftKeywords.LOCK ) &&
                 validateLT( index + 1,
                             "-" ) &&
                 validateLT( index + 2,
                             DroolsSoftKeywords.ON ) &&
                 validateLT( index + 3,
                             "-" ) &&
                 validateLT( index + 4,
                             DroolsSoftKeywords.ACTIVE )) ||
               (validateLT( index,
                            DroolsSoftKeywords.AGENDA ) &&
                 validateLT( index + 1,
                             "-" ) &&
                 validateLT( index + 2,
                             DroolsSoftKeywords.GROUP )) ||
               (validateLT( index,
                            DroolsSoftKeywords.ACTIVATION ) &&
                 validateLT( index + 1,
                             "-" ) &&
                 validateLT( index + 2,
                             DroolsSoftKeywords.GROUP )) ||
               (validateLT( index,
                            DroolsSoftKeywords.RULEFLOW ) &&
                 validateLT( index + 1,
                             "-" ) &&
                 validateLT( index + 2,
                             DroolsSoftKeywords.GROUP )) ||
               (validateLT( index,
                            DroolsSoftKeywords.DATE ) &&
                 validateLT( index + 1,
                             "-" ) &&
                 validateLT( index + 2,
                             DroolsSoftKeywords.EFFECTIVE )) ||
               (validateLT( index,
                            DroolsSoftKeywords.DATE ) &&
                 validateLT( index + 1,
                             "-" ) &&
                 validateLT( index + 2,
                             DroolsSoftKeywords.EXPIRES )) ||
               validateLT( index,
                           DroolsSoftKeywords.DIALECT ) ||
               validateLT( index,
                           DroolsSoftKeywords.CALENDARS ) ||
               validateLT( index,
                           DroolsSoftKeywords.TIMER ) ||
               validateLT( index,
                           DroolsSoftKeywords.DURATION ) ||
               validateLT( index,
                           DroolsSoftKeywords.REFRACT ) ||
               validateLT( index,
                           DroolsSoftKeywords.DIRECT );
    }

    public boolean validateIdentifierSufix() {
        return validateLT( 1,
                           "[" ) ||
               validateLT( 1,
                           "(" ) ||
               validateLT( 1,
                           "<" ) ||
               (validateLT( 1,
                            "." ) && validateSpecialID( 2 ));
    }

    public void checkTrailingSemicolon( String text,
                                        Token token ) {
        if ( text.trim().endsWith( ";" ) ) {
            errors.add( errorMessageFactory
                    .createTrailingSemicolonException( token.getLine(),
                                                       token.getCharPositionInLine(),
                                                       ((CommonToken) token).getStopIndex() ) );
        }
    }

    public String safeSubstring( String text,
                                 int start,
                                 int end ) {
        return text.substring( Math.min( start,
                                         text.length() ),
                               Math.min( Math
                                                 .max( start,
                                                       end ),
                                         text.length() ) );
    }

    public void reportError( RecognitionException ex ) {
        // if we've already reported an error and have not matched a token
        // yet successfully, don't report any errors.
        if ( state.errorRecovery ) {
            return;
        }
        state.errorRecovery = true;

        errors.add( errorMessageFactory.createDroolsException( ex ) );
    }

    public void reportError( Exception e ) {
        errors.add( errorMessageFactory.createDroolsException( e,
                                                               input.LT( 1 ) ) );
    }

    /** return the raw DroolsParserException errors */
    public List<DroolsParserException> getErrors() {
        return errors;
    }

    /** Return a list of pretty strings summarising the errors */
    public List<String> getErrorMessages() {
        List<String> messages = new ArrayList<String>( errors.size() );

        for ( DroolsParserException activeException : errors ) {
            messages.add( activeException.getMessage() );
        }

        return messages;
    }

    /** return true if any parser errors were accumulated */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Method that adds a paraphrase type into paraphrases stack.
     * 
     * @param type
     *            paraphrase type
     */
    public void pushParaphrases( DroolsParaphraseTypes type ) {
        Map<DroolsParaphraseTypes, String> activeMap = new HashMap<DroolsParaphraseTypes, String>();
        activeMap.put( type,
                       "" );
        paraphrases.push( activeMap );
    }

    public Map<DroolsParaphraseTypes, String> popParaphrases() {
        return paraphrases.pop();
    }

    /**
     * Method that sets paraphrase value for a type into paraphrases stack.
     * 
     * @param type
     *            paraphrase type
     * @param value
     *            paraphrase value
     */
    public void setParaphrasesValue( DroolsParaphraseTypes type,
                                     String value ) {
        paraphrases.peek().put( type,
                                value );
    }

    /**
     * Helper method that creates a string from a token list.
     * 
     * @param tokenList
     *            token list
     * @return string
     */
    public String buildStringFromTokens( List<Token> tokenList ) {
        StringBuilder sb = new StringBuilder();
        if ( null != tokenList ) {
            for ( Token activeToken : tokenList ) {
                if ( null != activeToken ) {
                    sb.append( activeToken.getText() );
                }
            }
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------------------------
    // COPIED FROM: http://www.antlr.org/wiki/display/ANTLR3/Custom+Syntax+Error+Recovery
    // ---------------------------------------------------------------------------------
    /**
     * Use the current stacked followset to work out the valid tokens that
     * can follow on from the current point in the parse, then recover by
     * eating tokens that are not a member of the follow set we compute.
     *
     * This method is used whenever we wish to force a sync, even though
     * the parser has not yet checked LA(1) for alt selection. This is useful
     * in situations where only a subset of tokens can begin a new construct
     * (such as the start of a new statement in a block) and we want to
     * proactively detect garbage so that the current rule does not exit on
     * on an exception.
     *
     * We could override recover() to make this the default behavior but that
     * is too much like using a sledge hammer to crack a nut. We want finer
     * grained control of the recovery and error mechanisms.
     */
    protected void syncToSet() {
        // Compute the followset that is in context wherever we are in the
        // rule chain/stack
        //
        BitSet follow = state.following[state._fsp]; //computeContextSensitiveRuleFOLLOW();

        syncToSet( follow );
    }

    protected void syncToSet( BitSet follow ) {
        int mark = -1;

        try {

            mark = input.mark();

            // Consume all tokens in the stream until we find a member of the follow
            // set, which means the next production should be guaranteed to be happy.
            //
            while ( !memberOfFollowSet( follow ) ) {

                if ( input.LA( 1 ) == Token.EOF ) {

                    // Looks like we didn't find anything at all that can help us here
                    // so we need to rewind to where we were and let normal error handling
                    // bail out.
                    //
                    input.rewind();
                    mark = -1;
                    return;
                }
                reportError( new MismatchedSetException( follow,
                                                         input ) );
                input.consume();

                // Now here, because you are consuming some tokens, yu will probably want
                // to raise an error message such as "Spurious elements after the class member were discarded"
                // using whatever your override of displayRecognitionError() routine does to record
                // error messages. The exact error my depend on context etc.
                //
            }
        } catch ( Exception e ) {

            // Just ignore any errors here, we will just let the recognizer
            // try to resync as normal - something must be very screwed.
            //
            e.printStackTrace();
        } finally {

            // Always release the mark we took
            //
            if ( mark != -1 ) {
                input.release( mark );
            }
        }
    }

    // ---------------------------------------------------------------------------------
    // END COPIED FROM: http://www.antlr.org/wiki/display/ANTLR3/Custom+Syntax+Error+Recovery
    // ---------------------------------------------------------------------------------

    private boolean memberOfFollowSet( BitSet follow ) {
        boolean isMember = follow.member( input.LA( 1 ) );
        if ( input.LA( 1 ) == lexerId(languageLevel) ) {
            String token = input.LT( 1 ).getText();
            isMember = (DroolsSoftKeywords.IMPORT.equals( token ) ||
                         DroolsSoftKeywords.GLOBAL.equals( token ) ||
                         DroolsSoftKeywords.FUNCTION.equals( token ) ||
                         DroolsSoftKeywords.DECLARE.equals( token ) ||
                         DroolsSoftKeywords.RULE.equals( token ) ||
                         DroolsSoftKeywords.QUERY.equals( token ) ||
                         DroolsSoftKeywords.SALIENCE.equals( token ) ||
                         DroolsSoftKeywords.NO.equals( token ) ||
                         DroolsSoftKeywords.AGENDA.equals( token ) ||
                         DroolsSoftKeywords.TIMER.equals( token ) ||
                         DroolsSoftKeywords.ACTIVATION.equals( token ) ||
                         DroolsSoftKeywords.AUTO.equals( token ) ||
                         DroolsSoftKeywords.DATE.equals( token ) ||
                         DroolsSoftKeywords.ENABLED.equals( token ) ||
                         DroolsSoftKeywords.RULEFLOW.equals( token ) ||
                         DroolsSoftKeywords.DIALECT.equals( token ) ||
                         DroolsSoftKeywords.CALENDARS.equals( token )
                        );
        }
        return isMember;
    }

    void setStart( DescrBuilder< ? , ? > db ) {
        setStart( db,
                  input.LT( 1 ) );
    }

    void setStart( DescrBuilder< ? , ? > db,
                   Token first ) {
        if ( db != null && first != null ) {
            db.startCharacter( ((CommonToken) first).getStartIndex() ).startLocation( first.getLine(),
                                                                                      first.getCharPositionInLine() );
        }
    }

    void setStart( BaseDescr descr,
                   Token first ) {
        if ( descr != null && first != null ) {
            descr.setLocation( first.getLine(),
                               first.getCharPositionInLine() );
            descr.setStartCharacter( ((CommonToken) first).getStartIndex() );
        }
    }

    void setEnd( BaseDescr descr ) {
        Token last = input.LT( -1 );
        if ( descr != null && last != null ) {
            int endLocation = last.getText() != null ? last.getCharPositionInLine() + last.getText().length() - 1 : last.getCharPositionInLine();
            descr.setEndCharacter( ((CommonToken) last).getStopIndex() + 1 );
            descr.setEndLocation( last.getLine(),
                                  endLocation );
        }
    }

    void setEnd( DescrBuilder< ? , ? > db ) {
        Token last = input.LT( -1 );
        if ( db != null && last != null ) {
            int endLocation = last.getText() != null ? last.getCharPositionInLine() + last.getText().length() - 1 : last.getCharPositionInLine();
            db.endCharacter( ((CommonToken) last).getStopIndex() + 1 ).endLocation( last.getLine(),
                                                                                    endLocation );
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends DescrBuilder< ? , ? >> T start( DescrBuilder< ? , ? > ctxBuilder,
                                                      Class<T> clazz,
                                                      String param ) {
        if ( state.backtracking == 0 ) {
            if ( PackageDescrBuilder.class.isAssignableFrom( clazz ) ) {
                pushParaphrases( DroolsParaphraseTypes.PACKAGE );
                beginSentence( DroolsSentenceType.PACKAGE );
                setStart( ctxBuilder );
            } else if ( ImportDescrBuilder.class.isAssignableFrom( clazz ) ) {
                ImportDescrBuilder imp;
                if ( validateLT( 2,
                                 DroolsSoftKeywords.FUNCTION ) ||
                     validateLT( 2,
                                 DroolsSoftKeywords.STATIC ) ) {
                    imp = ctxBuilder == null ?
                          DescrFactory.newPackage().newFunctionImport() :
                          ((PackageDescrBuilder) ctxBuilder).newFunctionImport();
                } else {
                    imp = ctxBuilder == null ?
                          DescrFactory.newPackage().newImport() :
                          ((PackageDescrBuilder) ctxBuilder).newImport();
                }
                pushParaphrases( DroolsParaphraseTypes.IMPORT );
                beginSentence( DroolsSentenceType.IMPORT_STATEMENT );
                setStart( imp );
                return (T) imp;
            } else if ( AccumulateImportDescrBuilder.class.isAssignableFrom( clazz ) ) {
                AccumulateImportDescrBuilder imp = ctxBuilder == null ?
                          DescrFactory.newPackage().newAccumulateImport() :
                          ((PackageDescrBuilder) ctxBuilder).newAccumulateImport();
                pushParaphrases( DroolsParaphraseTypes.ACCUMULATE_IMPORT );
                beginSentence( DroolsSentenceType.ACCUMULATE_IMPORT_STATEMENT );
                setStart( imp );
                return (T) imp;
            } else if ( GlobalDescrBuilder.class.isAssignableFrom( clazz ) ) {
                GlobalDescrBuilder global = ctxBuilder == null ?
                                            DescrFactory.newPackage().newGlobal() :
                                            ((PackageDescrBuilder) ctxBuilder).newGlobal();
                pushParaphrases( DroolsParaphraseTypes.GLOBAL );
                beginSentence( DroolsSentenceType.GLOBAL );
                setStart( global );
                return (T) global;
            } else if ( DeclareDescrBuilder.class.isAssignableFrom( clazz ) ) {
                DeclareDescrBuilder declare = ctxBuilder == null ?
                                              DescrFactory.newPackage().newDeclare() :
                                              ((PackageDescrBuilder) ctxBuilder).newDeclare();
                return (T) declare;
            } else if ( TypeDeclarationDescrBuilder.class.isAssignableFrom( clazz ) ) {
                TypeDeclarationDescrBuilder declare = ctxBuilder == null ?
                                                      DescrFactory.newPackage().newDeclare().type() :
                                                      ((DeclareDescrBuilder) ctxBuilder).type();
                pushParaphrases( DroolsParaphraseTypes.TYPE_DECLARE );
                beginSentence( DroolsSentenceType.TYPE_DECLARATION );
                setStart( declare );
                return (T) declare;
            } else if ( EnumDeclarationDescrBuilder.class.isAssignableFrom( clazz ) ) {
                EnumDeclarationDescrBuilder declare = ctxBuilder == null ?
                        DescrFactory.newPackage().newDeclare().enumerative() :
                        ((DeclareDescrBuilder) ctxBuilder).enumerative();
                pushParaphrases( DroolsParaphraseTypes.ENUM_DECLARE );
                beginSentence( DroolsSentenceType.ENUM_DECLARATION );
                setStart( declare );
                return (T) declare;
            }else if ( EntryPointDeclarationDescrBuilder.class.isAssignableFrom( clazz ) ) {
                EntryPointDeclarationDescrBuilder declare = ctxBuilder == null ?
                        DescrFactory.newPackage().newDeclare().entryPoint() :
                        ((DeclareDescrBuilder) ctxBuilder).entryPoint();
                pushParaphrases( DroolsParaphraseTypes.ENTRYPOINT_DECLARE );
                beginSentence( DroolsSentenceType.ENTRYPOINT_DECLARATION );
                setStart( declare );
                return (T) declare;
            } else if ( WindowDeclarationDescrBuilder.class.isAssignableFrom( clazz ) ) {
                WindowDeclarationDescrBuilder declare = ctxBuilder == null ?
                                                            DescrFactory.newPackage().newDeclare().window() :
                                                            ((DeclareDescrBuilder) ctxBuilder).window();
                pushParaphrases( DroolsParaphraseTypes.WINDOW_DECLARE );
                beginSentence( DroolsSentenceType.WINDOW_DECLARATION );
                setStart( declare );
                return (T) declare;
            } else if ( FieldDescrBuilder.class.isAssignableFrom( clazz ) ) {
                FieldDescrBuilder field = ((AbstractClassTypeDeclarationBuilder) ctxBuilder).newField( param );
                setStart( field );
                return (T) field;
            } else if ( EnumLiteralDescrBuilder.class.isAssignableFrom( clazz ) ) {
                EnumLiteralDescrBuilder literal = ((EnumDeclarationDescrBuilder) ctxBuilder).newEnumLiteral( param );
                setStart( literal );
                return (T) literal;
            } else if ( FunctionDescrBuilder.class.isAssignableFrom( clazz ) ) {
                FunctionDescrBuilder function;
                if ( ctxBuilder == null ) {
                    function = DescrFactory.newPackage().newFunction();
                } else {
                    PackageDescrBuilder pkg = (PackageDescrBuilder) ctxBuilder;
                    function = pkg.newFunction().namespace( pkg.getDescr().getName() );
                    AttributeDescr attribute = pkg.getDescr().getAttribute( "dialect" );
                    if ( attribute != null ) {
                        function.dialect( attribute.getValue() );
                    }
                }
                pushParaphrases( DroolsParaphraseTypes.FUNCTION );
                beginSentence( DroolsSentenceType.FUNCTION );
                setStart( function );
                return (T) function;
            } else if ( RuleDescrBuilder.class.isAssignableFrom( clazz ) ) {
                RuleDescrBuilder rule = ctxBuilder == null ?
                                        DescrFactory.newPackage().newRule() :
                                        ((PackageDescrBuilder) ctxBuilder).newRule();
                pushParaphrases( DroolsParaphraseTypes.RULE );
                beginSentence( DroolsSentenceType.RULE );
                setStart( rule );
                return (T) rule;
            } else if ( QueryDescrBuilder.class.isAssignableFrom( clazz ) ) {
                QueryDescrBuilder query = ctxBuilder == null ?
                                        DescrFactory.newPackage().newQuery() :
                                        ((PackageDescrBuilder) ctxBuilder).newQuery();
                pushParaphrases( DroolsParaphraseTypes.QUERY );
                beginSentence( DroolsSentenceType.QUERY );
                setStart( query );
                return (T) query;
            } else if ( AttributeDescrBuilder.class.isAssignableFrom( clazz ) ) {
                AttributeDescrBuilder< ? > attribute = ((AttributeSupportBuilder< ? >) ctxBuilder).attribute(param);
                setStart( attribute );
                return (T) attribute;
            } else if ( EvalDescrBuilder.class.isAssignableFrom( clazz ) ) {
                EvalDescrBuilder< ? > eval = ((CEDescrBuilder< ? , ? >) ctxBuilder).eval();
                pushParaphrases( DroolsParaphraseTypes.EVAL );
                beginSentence( DroolsSentenceType.EVAL );
                setStart( eval );
                return (T) eval;
            } else if ( ForallDescrBuilder.class.isAssignableFrom( clazz ) ) {
                ForallDescrBuilder< ? > forall = ((CEDescrBuilder< ? , ? >) ctxBuilder).forall();
                setStart( forall );
                return (T) forall;
            } else if ( CEDescrBuilder.class.isAssignableFrom( clazz ) ) {
                setStart( ctxBuilder );
                return (T) ctxBuilder;
            } else if ( PatternDescrBuilder.class.isAssignableFrom( clazz ) ) {
                PatternDescrBuilder< ? > pattern = ((PatternContainerDescrBuilder< ? , ? >) ctxBuilder).pattern();
                pushParaphrases( DroolsParaphraseTypes.PATTERN );
                setStart( pattern );
                return (T) pattern;
            } else if ( CollectDescrBuilder.class.isAssignableFrom( clazz ) ) {
                CollectDescrBuilder< ? > collect = ((PatternDescrBuilder< ? >) ctxBuilder).from().collect();
                setStart( collect );
                return (T) collect;
            } else if ( AccumulateDescrBuilder.class.isAssignableFrom( clazz ) ) {
                AccumulateDescrBuilder< ? > accumulate = ((PatternDescrBuilder< ? >) ctxBuilder).from().accumulate();
                setStart( accumulate );
                return (T) accumulate;
            } else if ( BehaviorDescrBuilder.class.isAssignableFrom( clazz ) ) {
                BehaviorDescrBuilder< ? > behavior = ((PatternDescrBuilder< ? >) ctxBuilder).behavior();
                setStart( behavior );
                return (T) behavior;
            } else if ( NamedConsequenceDescrBuilder.class.isAssignableFrom( clazz ) ) {
                NamedConsequenceDescrBuilder< ? > namedConsequence = ((CEDescrBuilder< ? , ? >) ctxBuilder).namedConsequence();
                setStart( namedConsequence );
                return (T) namedConsequence;
            } else if ( ConditionalBranchDescrBuilder.class.isAssignableFrom( clazz ) ) {
                ConditionalBranchDescrBuilder< ? > conditionalBranch = ((CEDescrBuilder< ? , ? >) ctxBuilder).conditionalBranch();
                setStart( conditionalBranch );
                return (T) conditionalBranch;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends DescrBuilder< ? , ? >> T end( Class<T> clazz,
                                                    DescrBuilder< ? , ? > builder ) {
        if ( state.backtracking == 0 ) {
            if ( !(FieldDescrBuilder.class.isAssignableFrom( clazz ) ||
                   AttributeDescrBuilder.class.isAssignableFrom( clazz ) ||
                   CEDescrBuilder.class.isAssignableFrom( clazz ) ||
                   CollectDescrBuilder.class.isAssignableFrom( clazz ) ||
                   AccumulateDescrBuilder.class.isAssignableFrom( clazz ) ||
                   ForallDescrBuilder.class.isAssignableFrom( clazz ) ||
                   BehaviorDescrBuilder.class.isAssignableFrom( clazz ) ||
                   ConditionalBranchDescrBuilder.class.isAssignableFrom( clazz ) ||
                   NamedConsequenceDescrBuilder.class.isAssignableFrom( clazz )) ) {
                popParaphrases();
            }
            setEnd( builder );
            return (T) builder;
        }
        return null;
    }

    public String[] getStatementKeywords() {
        return statementKeywords;
    }

    public boolean validateKeyword( int i ) {
        String token = input.LT( i ).getText();
        if ( token != null ) {
            for ( Field field : DroolsSoftKeywords.class.getFields() ) {
                if ( Modifier.isStatic( field.getModifiers() ) && Modifier.isPublic( field.getModifiers() ) ) {
                    try {
                        if ( token.equals( field.get( null ) ) ) {
                            return true;
                        }
                    } catch ( Exception e ) {
                        //nothing to do
                    }
                }
            }
        }
        return false;
    }

}
