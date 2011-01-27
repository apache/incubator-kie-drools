package org.drools.lang;

import static org.drools.lang.DRLParser.COLON;
import static org.drools.lang.DRLParser.ID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.antlr.runtime.BitSet;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.drools.compiler.DroolsParserException;
import org.drools.lang.api.AttributeDescrBuilder;
import org.drools.lang.api.AttributeSupportBuilder;
import org.drools.lang.api.DeclareDescrBuilder;
import org.drools.lang.api.DescrBuilder;
import org.drools.lang.api.FieldDescrBuilder;
import org.drools.lang.api.GlobalDescrBuilder;
import org.drools.lang.api.ImportDescrBuilder;
import org.drools.lang.api.PackageDescrBuilder;
import org.drools.lang.api.RuleDescrBuilder;

/**
 * This is a class to hold all the helper functions/methods used
 * by the DRL parser
 */
public class ParserXHelper {
    public List<DroolsParserException>                errors                   = new ArrayList<DroolsParserException>();
    public LinkedList<DroolsSentence>                 editorInterface          = null;
    public boolean                                    isEditorInterfaceEnabled = false;
    public boolean                                    lookaheadTest            = false;
    private Stack<Map<DroolsParaphraseTypes, String>> paraphrases              = new Stack<Map<DroolsParaphraseTypes, String>>();

    // parameters from parser
    private DroolsParserExceptionFactory              errorMessageFactory      = null;
    private TokenStream                               input                    = null;
    private RecognizerSharedState                     state                    = null;

    public Stack<DescrBuilder>                        builderContext           = null;

    public ParserXHelper(String[] tokenNames,
                         TokenStream input,
                         RecognizerSharedState state) {
        this.errorMessageFactory = new DroolsParserExceptionFactory( tokenNames,
                                                                     paraphrases );
        this.input = input;
        this.state = state;
        this.builderContext = new Stack<DescrBuilder>();
    }

    public LinkedList<DroolsSentence> getEditorInterface() {
        return editorInterface;
    }

    public void enableEditorInterface() {
        isEditorInterfaceEnabled = true;
    }

    public void disableEditorInterface() {
        isEditorInterfaceEnabled = false;
    }

    public void beginSentence( DroolsSentenceType sentenceType ) {
        if ( isEditorInterfaceEnabled ) {
            if ( null == editorInterface ) {
                editorInterface = new LinkedList<DroolsSentence>();
            }
            DroolsSentence sentence = new DroolsSentence();
            sentence.setType( sentenceType );
            editorInterface.add( sentence );
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
        return text2Validate == null ? false : text2Validate.equalsIgnoreCase( text );
    }

    public boolean isPluggableEvaluator( int offset,
                                         boolean negated ) {
        String text2Validate = retrieveLT( offset );
        return text2Validate == null ? false : DroolsSoftKeywords.isOperator( text2Validate,
                                                                              negated );
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
                           DroolsSoftKeywords.DURATION );
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
                    .createTrailingSemicolonException( ((DroolsToken) token)
                                                               .getLine(),
                                                       ((DroolsToken) token)
                                                               .getCharPositionInLine(),
                                                       ((DroolsToken) token)
                                                               .getStopIndex() ) );
        }
    }

    public boolean validateNotWithBinding() {
        if ( input.LA( 1 ) == ID && input.LA( 2 ) == ID && input.LA( 3 ) == COLON ) {
            return true;
        }
        return false;
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
        if ( state.errorRecovery ) {
            return;
        }
        errors.add( errorMessageFactory.createDroolsException( e, input.LT( 1 ) ) );
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

    private boolean memberOfFollowSet( BitSet follow ) {
        boolean isMember = follow.member( input.LA( 1 ) );
        if ( input.LA( 1 ) == DRLParser.ID ) {
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

    // ---------------------------------------------------------------------------------
    // END COPIED FROM: http://www.antlr.org/wiki/display/ANTLR3/Custom+Syntax+Error+Recovery
    // ---------------------------------------------------------------------------------

    void setStart( DescrBuilder db ) {
        setStart( db,
                  input.LT( 1 ) );
    }

    void setStart( DescrBuilder db,
                   Token first ) {
        if ( db != null && first != null ) {
            db.startCharacter( ((CommonToken) first).getStartIndex() ).startLocation( first.getLine(),
                                                                                           first.getCharPositionInLine() );
        }
    }

    void setEnd() {
        Token last = input.LT( -1 );
        if ( last != null && !builderContext.empty() ) {
            int endLocation = last.getText() != null ? last.getCharPositionInLine() + last.getText().length() - 1 : last.getCharPositionInLine();
            builderContext.peek().endCharacter( ((CommonToken) last).getStopIndex() + 1 ).endLocation( last.getLine(),
                                                                                                       endLocation );
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends DescrBuilder> T start( Class<T> clazz,
                                             String param ) {
        if ( state.backtracking == 0 ) {
            if ( PackageDescrBuilder.class.isAssignableFrom( clazz ) ) {
                pushParaphrases( DroolsParaphraseTypes.PACKAGE );
                beginSentence( DroolsSentenceType.PACKAGE );
                setStart( builderContext.empty() ? null : builderContext.peek() );
            } else if ( ImportDescrBuilder.class.isAssignableFrom( clazz ) ) {
                ImportDescrBuilder imp;
                if ( validateLT( 2,
                                 DroolsSoftKeywords.FUNCTION ) ) {
                    imp = ((PackageDescrBuilder) builderContext.peek()).newFunctionImport();
                } else {
                    imp = ((PackageDescrBuilder) builderContext.peek()).newImport();
                }
                builderContext.push( imp );
                pushParaphrases( DroolsParaphraseTypes.IMPORT );
                beginSentence( DroolsSentenceType.IMPORT_STATEMENT );
                setStart( imp );
                return (T) imp;
            } else if ( GlobalDescrBuilder.class.isAssignableFrom( clazz ) ) {
                GlobalDescrBuilder global = ((PackageDescrBuilder) builderContext.peek()).newGlobal();
                builderContext.push( global );
                pushParaphrases( DroolsParaphraseTypes.GLOBAL );
                beginSentence( DroolsSentenceType.GLOBAL );
                setStart( global );
                return (T) global;
            } else if ( DeclareDescrBuilder.class.isAssignableFrom( clazz ) ) {
                DeclareDescrBuilder declare = ((PackageDescrBuilder) builderContext.peek()).newDeclare();
                builderContext.push( declare );
                pushParaphrases( DroolsParaphraseTypes.TYPE_DECLARE );
                beginSentence( DroolsSentenceType.TYPE_DECLARATION );
                setStart( declare );
                return (T) declare;
            } else if ( FieldDescrBuilder.class.isAssignableFrom( clazz ) ) {
                FieldDescrBuilder field = ((DeclareDescrBuilder) builderContext.peek()).newField( param );
                builderContext.push( field );
                setStart( field );
                return (T) field;
            } else if ( RuleDescrBuilder.class.isAssignableFrom( clazz ) ) {
                RuleDescrBuilder rule = ((PackageDescrBuilder) builderContext.peek()).newRule();
                builderContext.push( rule );
                pushParaphrases( DroolsParaphraseTypes.RULE );
                beginSentence( DroolsSentenceType.RULE );
                setStart( rule );
                return (T) rule;
            } else if ( AttributeDescrBuilder.class.isAssignableFrom( clazz ) ) {
                AttributeDescrBuilder attribute = ((AttributeSupportBuilder) builderContext.peek()).attribute( param );
                builderContext.push( attribute );
                setStart( attribute );
                return (T) attribute;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends DescrBuilder> T end( Class<T> clazz ) {
        if ( state.backtracking == 0 ) {
            if ( !( FieldDescrBuilder.class.isAssignableFrom( clazz ) ||
                    AttributeDescrBuilder.class.isAssignableFrom( clazz )) ) {
                popParaphrases();
            }
            setEnd();
            if ( PackageDescrBuilder.class.isAssignableFrom( clazz ) ) {
                return (T) (builderContext.empty() ? null : builderContext.peek());
            }
            return (T) builderContext.pop();
        }
        return null;
    }
}
