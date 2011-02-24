// $ANTLR 3.3 Nov 30, 2010 12:45:30 /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g 2011-02-23 18:27:20

    package org.drools.lang;

    import java.util.LinkedList;
    import org.drools.compiler.DroolsParserException;
    import org.drools.lang.ParserHelper;
    import org.drools.lang.DroolsParserExceptionFactory;
    import org.drools.CheckedDroolsException;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

public class DRLExpressions extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "OPERATOR", "NEG_OPERATOR", "SHIFT_EXPR", "SHL_ASSIGN", "SHRB_ASSIGN", "SHR_ASSIGN", "EOL", "WS", "Exponent", "FloatTypeSuffix", "FLOAT", "HexDigit", "IntegerTypeSuffix", "HEX", "DECIMAL", "EscapeSequence", "STRING", "TimePeriod", "UnicodeEscape", "OctalEscape", "BOOL", "NULL", "AT", "PLUS_ASSIGN", "MINUS_ASSIGN", "MULT_ASSIGN", "DIV_ASSIGN", "AND_ASSIGN", "OR_ASSIGN", "XOR_ASSIGN", "MOD_ASSIGN", "DECR", "INCR", "ARROW", "SEMICOLON", "COLON", "EQUALS", "NOT_EQUALS", "GREATER_EQUALS", "LESS_EQUALS", "GREATER", "LESS", "EQUALS_ASSIGN", "LEFT_PAREN", "RIGHT_PAREN", "LEFT_SQUARE", "RIGHT_SQUARE", "LEFT_CURLY", "RIGHT_CURLY", "COMMA", "DOT", "DOUBLE_AMPER", "DOUBLE_PIPE", "QUESTION", "NEGATION", "TILDE", "PIPE", "AMPER", "XOR", "MOD", "STAR", "MINUS", "PLUS", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "IdentifierStart", "IdentifierPart", "ID", "DIV", "MISC"
    };
    public static final int EOF=-1;
    public static final int OPERATOR=4;
    public static final int NEG_OPERATOR=5;
    public static final int SHIFT_EXPR=6;
    public static final int SHL_ASSIGN=7;
    public static final int SHRB_ASSIGN=8;
    public static final int SHR_ASSIGN=9;
    public static final int EOL=10;
    public static final int WS=11;
    public static final int Exponent=12;
    public static final int FloatTypeSuffix=13;
    public static final int FLOAT=14;
    public static final int HexDigit=15;
    public static final int IntegerTypeSuffix=16;
    public static final int HEX=17;
    public static final int DECIMAL=18;
    public static final int EscapeSequence=19;
    public static final int STRING=20;
    public static final int TimePeriod=21;
    public static final int UnicodeEscape=22;
    public static final int OctalEscape=23;
    public static final int BOOL=24;
    public static final int NULL=25;
    public static final int AT=26;
    public static final int PLUS_ASSIGN=27;
    public static final int MINUS_ASSIGN=28;
    public static final int MULT_ASSIGN=29;
    public static final int DIV_ASSIGN=30;
    public static final int AND_ASSIGN=31;
    public static final int OR_ASSIGN=32;
    public static final int XOR_ASSIGN=33;
    public static final int MOD_ASSIGN=34;
    public static final int DECR=35;
    public static final int INCR=36;
    public static final int ARROW=37;
    public static final int SEMICOLON=38;
    public static final int COLON=39;
    public static final int EQUALS=40;
    public static final int NOT_EQUALS=41;
    public static final int GREATER_EQUALS=42;
    public static final int LESS_EQUALS=43;
    public static final int GREATER=44;
    public static final int LESS=45;
    public static final int EQUALS_ASSIGN=46;
    public static final int LEFT_PAREN=47;
    public static final int RIGHT_PAREN=48;
    public static final int LEFT_SQUARE=49;
    public static final int RIGHT_SQUARE=50;
    public static final int LEFT_CURLY=51;
    public static final int RIGHT_CURLY=52;
    public static final int COMMA=53;
    public static final int DOT=54;
    public static final int DOUBLE_AMPER=55;
    public static final int DOUBLE_PIPE=56;
    public static final int QUESTION=57;
    public static final int NEGATION=58;
    public static final int TILDE=59;
    public static final int PIPE=60;
    public static final int AMPER=61;
    public static final int XOR=62;
    public static final int MOD=63;
    public static final int STAR=64;
    public static final int MINUS=65;
    public static final int PLUS=66;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=67;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=68;
    public static final int MULTI_LINE_COMMENT=69;
    public static final int IdentifierStart=70;
    public static final int IdentifierPart=71;
    public static final int ID=72;
    public static final int DIV=73;
    public static final int MISC=74;

    // delegates
    // delegators


        public DRLExpressions(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public DRLExpressions(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return DRLExpressions.tokenNames; }
    public String getGrammarFileName() { return "/home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g"; }


        private ParserHelper helper;
                                                        
        public DRLExpressions(TokenStream input,
                              RecognizerSharedState state,
                              ParserHelper helper ) {
            this( input,
                  state );
            this.helper = helper;
        }

        public ParserHelper getHelper()                           { return helper; }
        public boolean hasErrors()                                { return helper.hasErrors(); }
        public List<DroolsParserException> getErrors()            { return helper.getErrors(); }
        public List<String> getErrorMessages()                    { return helper.getErrorMessages(); }
        public void enableEditorInterface()                       {        helper.enableEditorInterface(); }
        public void disableEditorInterface()                      {        helper.disableEditorInterface(); }
        public LinkedList<DroolsSentence> getEditorInterface()    { return helper.getEditorInterface(); }
        public void reportError(RecognitionException ex)          {        helper.reportError( ex ); }
        public void emitErrorMessage(String msg)                  {}
        
        private boolean buildConstraint;
        public void setBuildConstraint( boolean build ) { this.buildConstraint = build; }
        public boolean isBuildConstraint() { return this.buildConstraint; }



    public static class literal_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "literal"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:57:1: literal : ( STRING | DECIMAL | HEX | FLOAT | BOOL | NULL );
    public final DRLExpressions.literal_return literal() throws RecognitionException {
        DRLExpressions.literal_return retval = new DRLExpressions.literal_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING1=null;
        Token DECIMAL2=null;
        Token HEX3=null;
        Token FLOAT4=null;
        Token BOOL5=null;
        Token NULL6=null;

        Object STRING1_tree=null;
        Object DECIMAL2_tree=null;
        Object HEX3_tree=null;
        Object FLOAT4_tree=null;
        Object BOOL5_tree=null;
        Object NULL6_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:58:5: ( STRING | DECIMAL | HEX | FLOAT | BOOL | NULL )
            int alt1=6;
            switch ( input.LA(1) ) {
            case STRING:
                {
                alt1=1;
                }
                break;
            case DECIMAL:
                {
                alt1=2;
                }
                break;
            case HEX:
                {
                alt1=3;
                }
                break;
            case FLOAT:
                {
                alt1=4;
                }
                break;
            case BOOL:
                {
                alt1=5;
                }
                break;
            case NULL:
                {
                alt1=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:58:7: STRING
                    {
                    root_0 = (Object)adaptor.nil();

                    STRING1=(Token)match(input,STRING,FOLLOW_STRING_in_literal94); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING1_tree = (Object)adaptor.create(STRING1);
                    adaptor.addChild(root_0, STRING1_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	helper.emit(STRING1, DroolsEditorType.STRING_CONST);	
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:59:7: DECIMAL
                    {
                    root_0 = (Object)adaptor.nil();

                    DECIMAL2=(Token)match(input,DECIMAL,FOLLOW_DECIMAL_in_literal109); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DECIMAL2_tree = (Object)adaptor.create(DECIMAL2);
                    adaptor.addChild(root_0, DECIMAL2_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	helper.emit(DECIMAL2, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:60:7: HEX
                    {
                    root_0 = (Object)adaptor.nil();

                    HEX3=(Token)match(input,HEX,FOLLOW_HEX_in_literal121); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    HEX3_tree = (Object)adaptor.create(HEX3);
                    adaptor.addChild(root_0, HEX3_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	helper.emit(HEX3, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:61:7: FLOAT
                    {
                    root_0 = (Object)adaptor.nil();

                    FLOAT4=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_literal137); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    FLOAT4_tree = (Object)adaptor.create(FLOAT4);
                    adaptor.addChild(root_0, FLOAT4_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	helper.emit(FLOAT4, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:62:7: BOOL
                    {
                    root_0 = (Object)adaptor.nil();

                    BOOL5=(Token)match(input,BOOL,FOLLOW_BOOL_in_literal151); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BOOL5_tree = (Object)adaptor.create(BOOL5);
                    adaptor.addChild(root_0, BOOL5_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	helper.emit(BOOL5, DroolsEditorType.BOOLEAN_CONST);	
                    }

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:63:7: NULL
                    {
                    root_0 = (Object)adaptor.nil();

                    NULL6=(Token)match(input,NULL,FOLLOW_NULL_in_literal168); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NULL6_tree = (Object)adaptor.create(NULL6);
                    adaptor.addChild(root_0, NULL6_tree);
                    }
                    if ( state.backtracking==0 ) {
                      	helper.emit(NULL6, DroolsEditorType.NULL_CONST);	
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "literal"

    public static class typeList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeList"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:66:1: typeList : type ( COMMA type )* ;
    public final DRLExpressions.typeList_return typeList() throws RecognitionException {
        DRLExpressions.typeList_return retval = new DRLExpressions.typeList_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA8=null;
        DRLExpressions.type_return type7 = null;

        DRLExpressions.type_return type9 = null;


        Object COMMA8_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:67:5: ( type ( COMMA type )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:67:7: type ( COMMA type )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_type_in_typeList194);
            type7=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type7.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:67:12: ( COMMA type )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==COMMA) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:67:13: COMMA type
            	    {
            	    COMMA8=(Token)match(input,COMMA,FOLLOW_COMMA_in_typeList197); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    COMMA8_tree = (Object)adaptor.create(COMMA8);
            	    adaptor.addChild(root_0, COMMA8_tree);
            	    }
            	    pushFollow(FOLLOW_type_in_typeList199);
            	    type9=type();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, type9.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "typeList"

    public static class type_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "type"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:70:1: type : ( ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) | ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) );
    public final DRLExpressions.type_return type() throws RecognitionException {
        DRLExpressions.type_return retval = new DRLExpressions.type_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE11=null;
        Token RIGHT_SQUARE12=null;
        Token ID13=null;
        Token DOT15=null;
        Token ID16=null;
        Token LEFT_SQUARE18=null;
        Token RIGHT_SQUARE19=null;
        DRLExpressions.primitiveType_return primitiveType10 = null;

        DRLExpressions.typeArguments_return typeArguments14 = null;

        DRLExpressions.typeArguments_return typeArguments17 = null;


        Object LEFT_SQUARE11_tree=null;
        Object RIGHT_SQUARE12_tree=null;
        Object ID13_tree=null;
        Object DOT15_tree=null;
        Object ID16_tree=null;
        Object LEFT_SQUARE18_tree=null;
        Object RIGHT_SQUARE19_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:5: ( ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) | ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==ID) ) {
                int LA8_1 = input.LA(2);

                if ( (((synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.LONG))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))))) ) {
                    alt8=1;
                }
                else if ( (true) ) {
                    alt8=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 8, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:8: ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    {
                    root_0 = (Object)adaptor.nil();

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:27: ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:29: primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                    pushFollow(FOLLOW_primitiveType_in_type227);
                    primitiveType10=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType10.getTree());
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:43: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( (LA3_0==LEFT_SQUARE) ) {
                            int LA3_2 = input.LA(2);

                            if ( (LA3_2==RIGHT_SQUARE) ) {
                                int LA3_3 = input.LA(3);

                                if ( (synpred2_DRLExpressions()) ) {
                                    alt3=1;
                                }


                            }


                        }


                        switch (alt3) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:44: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    LEFT_SQUARE11=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_type237); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    LEFT_SQUARE11_tree = (Object)adaptor.create(LEFT_SQUARE11);
                    	    adaptor.addChild(root_0, LEFT_SQUARE11_tree);
                    	    }
                    	    RIGHT_SQUARE12=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_type239); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    RIGHT_SQUARE12_tree = (Object)adaptor.create(RIGHT_SQUARE12);
                    	    adaptor.addChild(root_0, RIGHT_SQUARE12_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop3;
                        }
                    } while (true);


                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:72:7: ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    {
                    root_0 = (Object)adaptor.nil();

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:72:7: ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:72:9: ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                    ID13=(Token)match(input,ID,FOLLOW_ID_in_type253); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID13_tree = (Object)adaptor.create(ID13);
                    adaptor.addChild(root_0, ID13_tree);
                    }
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:72:12: ( ( typeArguments )=> typeArguments )?
                    int alt4=2;
                    alt4 = dfa4.predict(input);
                    switch (alt4) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:72:13: ( typeArguments )=> typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_type260);
                            typeArguments14=typeArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArguments14.getTree());

                            }
                            break;

                    }

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:72:46: ( DOT ID ( ( typeArguments )=> typeArguments )? )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==DOT) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:72:47: DOT ID ( ( typeArguments )=> typeArguments )?
                    	    {
                    	    DOT15=(Token)match(input,DOT,FOLLOW_DOT_in_type265); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    DOT15_tree = (Object)adaptor.create(DOT15);
                    	    adaptor.addChild(root_0, DOT15_tree);
                    	    }
                    	    ID16=(Token)match(input,ID,FOLLOW_ID_in_type267); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    ID16_tree = (Object)adaptor.create(ID16);
                    	    adaptor.addChild(root_0, ID16_tree);
                    	    }
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:72:54: ( ( typeArguments )=> typeArguments )?
                    	    int alt5=2;
                    	    alt5 = dfa5.predict(input);
                    	    switch (alt5) {
                    	        case 1 :
                    	            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:72:55: ( typeArguments )=> typeArguments
                    	            {
                    	            pushFollow(FOLLOW_typeArguments_in_type274);
                    	            typeArguments17=typeArguments();

                    	            state._fsp--;
                    	            if (state.failed) return retval;
                    	            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArguments17.getTree());

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:72:91: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==LEFT_SQUARE) ) {
                            int LA7_2 = input.LA(2);

                            if ( (LA7_2==RIGHT_SQUARE) ) {
                                int LA7_3 = input.LA(3);

                                if ( (synpred5_DRLExpressions()) ) {
                                    alt7=1;
                                }


                            }


                        }


                        switch (alt7) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:72:92: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    LEFT_SQUARE18=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_type289); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    LEFT_SQUARE18_tree = (Object)adaptor.create(LEFT_SQUARE18);
                    	    adaptor.addChild(root_0, LEFT_SQUARE18_tree);
                    	    }
                    	    RIGHT_SQUARE19=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_type291); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    RIGHT_SQUARE19_tree = (Object)adaptor.create(RIGHT_SQUARE19);
                    	    adaptor.addChild(root_0, RIGHT_SQUARE19_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);


                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "type"

    public static class typeArguments_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeArguments"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:75:1: typeArguments : LESS typeArgument ( COMMA typeArgument )* GREATER ;
    public final DRLExpressions.typeArguments_return typeArguments() throws RecognitionException {
        DRLExpressions.typeArguments_return retval = new DRLExpressions.typeArguments_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LESS20=null;
        Token COMMA22=null;
        Token GREATER24=null;
        DRLExpressions.typeArgument_return typeArgument21 = null;

        DRLExpressions.typeArgument_return typeArgument23 = null;


        Object LESS20_tree=null;
        Object COMMA22_tree=null;
        Object GREATER24_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:76:5: ( LESS typeArgument ( COMMA typeArgument )* GREATER )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:76:7: LESS typeArgument ( COMMA typeArgument )* GREATER
            {
            root_0 = (Object)adaptor.nil();

            LESS20=(Token)match(input,LESS,FOLLOW_LESS_in_typeArguments312); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            LESS20_tree = (Object)adaptor.create(LESS20);
            adaptor.addChild(root_0, LESS20_tree);
            }
            pushFollow(FOLLOW_typeArgument_in_typeArguments314);
            typeArgument21=typeArgument();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArgument21.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:76:25: ( COMMA typeArgument )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==COMMA) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:76:26: COMMA typeArgument
            	    {
            	    COMMA22=(Token)match(input,COMMA,FOLLOW_COMMA_in_typeArguments317); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    COMMA22_tree = (Object)adaptor.create(COMMA22);
            	    adaptor.addChild(root_0, COMMA22_tree);
            	    }
            	    pushFollow(FOLLOW_typeArgument_in_typeArguments319);
            	    typeArgument23=typeArgument();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArgument23.getTree());

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            GREATER24=(Token)match(input,GREATER,FOLLOW_GREATER_in_typeArguments323); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            GREATER24_tree = (Object)adaptor.create(GREATER24);
            adaptor.addChild(root_0, GREATER24_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "typeArguments"

    public static class typeArgument_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeArgument"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:79:1: typeArgument : ( type | QUESTION ( ( extends_key | super_key ) type )? );
    public final DRLExpressions.typeArgument_return typeArgument() throws RecognitionException {
        DRLExpressions.typeArgument_return retval = new DRLExpressions.typeArgument_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token QUESTION26=null;
        DRLExpressions.type_return type25 = null;

        DRLExpressions.extends_key_return extends_key27 = null;

        DRLExpressions.super_key_return super_key28 = null;

        DRLExpressions.type_return type29 = null;


        Object QUESTION26_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:5: ( type | QUESTION ( ( extends_key | super_key ) type )? )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==ID) ) {
                alt12=1;
            }
            else if ( (LA12_0==QUESTION) ) {
                alt12=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:7: type
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_type_in_typeArgument340);
                    type25=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type25.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:81:7: QUESTION ( ( extends_key | super_key ) type )?
                    {
                    root_0 = (Object)adaptor.nil();

                    QUESTION26=(Token)match(input,QUESTION,FOLLOW_QUESTION_in_typeArgument348); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    QUESTION26_tree = (Object)adaptor.create(QUESTION26);
                    adaptor.addChild(root_0, QUESTION26_tree);
                    }
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:81:16: ( ( extends_key | super_key ) type )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))||((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))))) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:81:17: ( extends_key | super_key ) type
                            {
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:81:17: ( extends_key | super_key )
                            int alt10=2;
                            int LA10_0 = input.LA(1);

                            if ( (LA10_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))||((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))))) {
                                int LA10_1 = input.LA(2);

                                if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))) ) {
                                    alt10=1;
                                }
                                else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
                                    alt10=2;
                                }
                                else {
                                    if (state.backtracking>0) {state.failed=true; return retval;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 10, 1, input);

                                    throw nvae;
                                }
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                NoViableAltException nvae =
                                    new NoViableAltException("", 10, 0, input);

                                throw nvae;
                            }
                            switch (alt10) {
                                case 1 :
                                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:81:18: extends_key
                                    {
                                    pushFollow(FOLLOW_extends_key_in_typeArgument352);
                                    extends_key27=extends_key();

                                    state._fsp--;
                                    if (state.failed) return retval;
                                    if ( state.backtracking==0 ) adaptor.addChild(root_0, extends_key27.getTree());

                                    }
                                    break;
                                case 2 :
                                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:81:32: super_key
                                    {
                                    pushFollow(FOLLOW_super_key_in_typeArgument356);
                                    super_key28=super_key();

                                    state._fsp--;
                                    if (state.failed) return retval;
                                    if ( state.backtracking==0 ) adaptor.addChild(root_0, super_key28.getTree());

                                    }
                                    break;

                            }

                            pushFollow(FOLLOW_type_in_typeArgument359);
                            type29=type();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, type29.getTree());

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "typeArgument"

    public static class dummy_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "dummy"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:89:1: dummy : expression ( AT | SEMICOLON | EOF ) ;
    public final DRLExpressions.dummy_return dummy() throws RecognitionException {
        DRLExpressions.dummy_return retval = new DRLExpressions.dummy_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set31=null;
        DRLExpressions.expression_return expression30 = null;


        Object set31_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:90:5: ( expression ( AT | SEMICOLON | EOF ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:90:7: expression ( AT | SEMICOLON | EOF )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression_in_dummy383);
            expression30=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression30.getTree());
            set31=(Token)input.LT(1);
            if ( input.LA(1)==EOF||input.LA(1)==AT||input.LA(1)==SEMICOLON ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set31));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "dummy"

    public static class expression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:94:1: expression : conditionalExpression ( ( assignmentOperator )=> assignmentOperator expression )? ;
    public final DRLExpressions.expression_return expression() throws RecognitionException {
        DRLExpressions.expression_return retval = new DRLExpressions.expression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLExpressions.conditionalExpression_return conditionalExpression32 = null;

        DRLExpressions.assignmentOperator_return assignmentOperator33 = null;

        DRLExpressions.expression_return expression34 = null;



        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:95:5: ( conditionalExpression ( ( assignmentOperator )=> assignmentOperator expression )? )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:95:7: conditionalExpression ( ( assignmentOperator )=> assignmentOperator expression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalExpression_in_expression414);
            conditionalExpression32=conditionalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression32.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:95:29: ( ( assignmentOperator )=> assignmentOperator expression )?
            int alt13=2;
            alt13 = dfa13.predict(input);
            switch (alt13) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:95:30: ( assignmentOperator )=> assignmentOperator expression
                    {
                    pushFollow(FOLLOW_assignmentOperator_in_expression423);
                    assignmentOperator33=assignmentOperator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(assignmentOperator33.getTree(), root_0);
                    pushFollow(FOLLOW_expression_in_expression426);
                    expression34=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression34.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "expression"

    public static class conditionalExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conditionalExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:98:1: conditionalExpression : conditionalOrExpression ( QUESTION expression COLON expression )? ;
    public final DRLExpressions.conditionalExpression_return conditionalExpression() throws RecognitionException {
        DRLExpressions.conditionalExpression_return retval = new DRLExpressions.conditionalExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token QUESTION36=null;
        Token COLON38=null;
        DRLExpressions.conditionalOrExpression_return conditionalOrExpression35 = null;

        DRLExpressions.expression_return expression37 = null;

        DRLExpressions.expression_return expression39 = null;


        Object QUESTION36_tree=null;
        Object COLON38_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:99:3: ( conditionalOrExpression ( QUESTION expression COLON expression )? )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:99:5: conditionalOrExpression ( QUESTION expression COLON expression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression443);
            conditionalOrExpression35=conditionalOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalOrExpression35.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:99:29: ( QUESTION expression COLON expression )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==QUESTION) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:99:31: QUESTION expression COLON expression
                    {
                    QUESTION36=(Token)match(input,QUESTION,FOLLOW_QUESTION_in_conditionalExpression447); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    QUESTION36_tree = (Object)adaptor.create(QUESTION36);
                    root_0 = (Object)adaptor.becomeRoot(QUESTION36_tree, root_0);
                    }
                    pushFollow(FOLLOW_expression_in_conditionalExpression450);
                    expression37=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression37.getTree());
                    COLON38=(Token)match(input,COLON,FOLLOW_COLON_in_conditionalExpression452); if (state.failed) return retval;
                    pushFollow(FOLLOW_expression_in_conditionalExpression455);
                    expression39=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression39.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "conditionalExpression"

    public static class conditionalOrExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conditionalOrExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:102:1: conditionalOrExpression : conditionalAndExpression ( DOUBLE_PIPE conditionalAndExpression )* ;
    public final DRLExpressions.conditionalOrExpression_return conditionalOrExpression() throws RecognitionException {
        DRLExpressions.conditionalOrExpression_return retval = new DRLExpressions.conditionalOrExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_PIPE41=null;
        DRLExpressions.conditionalAndExpression_return conditionalAndExpression40 = null;

        DRLExpressions.conditionalAndExpression_return conditionalAndExpression42 = null;


        Object DOUBLE_PIPE41_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:103:3: ( conditionalAndExpression ( DOUBLE_PIPE conditionalAndExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:103:5: conditionalAndExpression ( DOUBLE_PIPE conditionalAndExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression473);
            conditionalAndExpression40=conditionalAndExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression40.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:103:30: ( DOUBLE_PIPE conditionalAndExpression )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==DOUBLE_PIPE) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:103:32: DOUBLE_PIPE conditionalAndExpression
            	    {
            	    DOUBLE_PIPE41=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression477); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    DOUBLE_PIPE41_tree = (Object)adaptor.create(DOUBLE_PIPE41);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_PIPE41_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression480);
            	    conditionalAndExpression42=conditionalAndExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression42.getTree());

            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "conditionalOrExpression"

    public static class conditionalAndExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conditionalAndExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:1: conditionalAndExpression : inclusiveOrExpression ( DOUBLE_AMPER inclusiveOrExpression )* ;
    public final DRLExpressions.conditionalAndExpression_return conditionalAndExpression() throws RecognitionException {
        DRLExpressions.conditionalAndExpression_return retval = new DRLExpressions.conditionalAndExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOUBLE_AMPER44=null;
        DRLExpressions.inclusiveOrExpression_return inclusiveOrExpression43 = null;

        DRLExpressions.inclusiveOrExpression_return inclusiveOrExpression45 = null;


        Object DOUBLE_AMPER44_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:107:3: ( inclusiveOrExpression ( DOUBLE_AMPER inclusiveOrExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:107:5: inclusiveOrExpression ( DOUBLE_AMPER inclusiveOrExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression499);
            inclusiveOrExpression43=inclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression43.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:107:27: ( DOUBLE_AMPER inclusiveOrExpression )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==DOUBLE_AMPER) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:107:29: DOUBLE_AMPER inclusiveOrExpression
            	    {
            	    DOUBLE_AMPER44=(Token)match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression503); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    DOUBLE_AMPER44_tree = (Object)adaptor.create(DOUBLE_AMPER44);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_AMPER44_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression506);
            	    inclusiveOrExpression45=inclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression45.getTree());

            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "conditionalAndExpression"

    public static class inclusiveOrExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "inclusiveOrExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:110:1: inclusiveOrExpression : exclusiveOrExpression ( PIPE exclusiveOrExpression )* ;
    public final DRLExpressions.inclusiveOrExpression_return inclusiveOrExpression() throws RecognitionException {
        DRLExpressions.inclusiveOrExpression_return retval = new DRLExpressions.inclusiveOrExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PIPE47=null;
        DRLExpressions.exclusiveOrExpression_return exclusiveOrExpression46 = null;

        DRLExpressions.exclusiveOrExpression_return exclusiveOrExpression48 = null;


        Object PIPE47_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:111:3: ( exclusiveOrExpression ( PIPE exclusiveOrExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:111:5: exclusiveOrExpression ( PIPE exclusiveOrExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression524);
            exclusiveOrExpression46=exclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression46.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:111:27: ( PIPE exclusiveOrExpression )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==PIPE) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:111:29: PIPE exclusiveOrExpression
            	    {
            	    PIPE47=(Token)match(input,PIPE,FOLLOW_PIPE_in_inclusiveOrExpression528); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    PIPE47_tree = (Object)adaptor.create(PIPE47);
            	    root_0 = (Object)adaptor.becomeRoot(PIPE47_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression531);
            	    exclusiveOrExpression48=exclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression48.getTree());

            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "inclusiveOrExpression"

    public static class exclusiveOrExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "exclusiveOrExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:1: exclusiveOrExpression : andExpression ( XOR andExpression )* ;
    public final DRLExpressions.exclusiveOrExpression_return exclusiveOrExpression() throws RecognitionException {
        DRLExpressions.exclusiveOrExpression_return retval = new DRLExpressions.exclusiveOrExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token XOR50=null;
        DRLExpressions.andExpression_return andExpression49 = null;

        DRLExpressions.andExpression_return andExpression51 = null;


        Object XOR50_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:3: ( andExpression ( XOR andExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:5: andExpression ( XOR andExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression549);
            andExpression49=andExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression49.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:19: ( XOR andExpression )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==XOR) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:21: XOR andExpression
            	    {
            	    XOR50=(Token)match(input,XOR,FOLLOW_XOR_in_exclusiveOrExpression553); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    XOR50_tree = (Object)adaptor.create(XOR50);
            	    root_0 = (Object)adaptor.becomeRoot(XOR50_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression556);
            	    andExpression51=andExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression51.getTree());

            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "exclusiveOrExpression"

    public static class andExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "andExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:118:1: andExpression : andOrRestriction ( AMPER andOrRestriction )* ;
    public final DRLExpressions.andExpression_return andExpression() throws RecognitionException {
        DRLExpressions.andExpression_return retval = new DRLExpressions.andExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token AMPER53=null;
        DRLExpressions.andOrRestriction_return andOrRestriction52 = null;

        DRLExpressions.andOrRestriction_return andOrRestriction54 = null;


        Object AMPER53_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:119:3: ( andOrRestriction ( AMPER andOrRestriction )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:119:5: andOrRestriction ( AMPER andOrRestriction )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_andOrRestriction_in_andExpression575);
            andOrRestriction52=andOrRestriction();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, andOrRestriction52.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:119:22: ( AMPER andOrRestriction )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==AMPER) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:119:24: AMPER andOrRestriction
            	    {
            	    AMPER53=(Token)match(input,AMPER,FOLLOW_AMPER_in_andExpression579); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    AMPER53_tree = (Object)adaptor.create(AMPER53);
            	    root_0 = (Object)adaptor.becomeRoot(AMPER53_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_andOrRestriction_in_andExpression582);
            	    andOrRestriction54=andOrRestriction();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, andOrRestriction54.getTree());

            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "andExpression"

    public static class andOrRestriction_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "andOrRestriction"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:122:1: andOrRestriction : (ee= equalityExpression -> $ee) ( ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER ) op= operator se2= shiftExpressionTk ) -> ^( $lop $andOrRestriction ^( $op $se2) ) )* ;
    public final DRLExpressions.andOrRestriction_return andOrRestriction() throws RecognitionException {
        DRLExpressions.andOrRestriction_return retval = new DRLExpressions.andOrRestriction_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token lop=null;
        DRLExpressions.equalityExpression_return ee = null;

        DRLExpressions.operator_return op = null;

        DRLExpressions.shiftExpressionTk_return se2 = null;


        Object lop_tree=null;
        RewriteRuleTokenStream stream_DOUBLE_PIPE=new RewriteRuleTokenStream(adaptor,"token DOUBLE_PIPE");
        RewriteRuleTokenStream stream_DOUBLE_AMPER=new RewriteRuleTokenStream(adaptor,"token DOUBLE_AMPER");
        RewriteRuleSubtreeStream stream_equalityExpression=new RewriteRuleSubtreeStream(adaptor,"rule equalityExpression");
        RewriteRuleSubtreeStream stream_shiftExpressionTk=new RewriteRuleSubtreeStream(adaptor,"rule shiftExpressionTk");
        RewriteRuleSubtreeStream stream_operator=new RewriteRuleSubtreeStream(adaptor,"rule operator");
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:123:4: ( (ee= equalityExpression -> $ee) ( ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER ) op= operator se2= shiftExpressionTk ) -> ^( $lop $andOrRestriction ^( $op $se2) ) )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:123:6: (ee= equalityExpression -> $ee) ( ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER ) op= operator se2= shiftExpressionTk ) -> ^( $lop $andOrRestriction ^( $op $se2) ) )*
            {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:123:6: (ee= equalityExpression -> $ee)
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:123:7: ee= equalityExpression
            {
            pushFollow(FOLLOW_equalityExpression_in_andOrRestriction608);
            ee=equalityExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_equalityExpression.add(ee.getTree());


            // AST REWRITE
            // elements: ee
            // token labels: 
            // rule labels: retval, ee
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_ee=new RewriteRuleSubtreeStream(adaptor,"rule ee",ee!=null?ee.tree:null);

            root_0 = (Object)adaptor.nil();
            // 123:29: -> $ee
            {
                adaptor.addChild(root_0, stream_ee.nextTree());

            }

            retval.tree = root_0;}
            }

            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:124:6: ( ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER ) op= operator se2= shiftExpressionTk ) -> ^( $lop $andOrRestriction ^( $op $se2) ) )*
            loop21:
            do {
                int alt21=2;
                alt21 = dfa21.predict(input);
                switch (alt21) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:124:7: ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER ) op= operator se2= shiftExpressionTk )
            	    {
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:124:7: ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER ) op= operator se2= shiftExpressionTk )
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:124:9: ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER ) op= operator se2= shiftExpressionTk
            	    {
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:124:48: (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER )
            	    int alt20=2;
            	    int LA20_0 = input.LA(1);

            	    if ( (LA20_0==DOUBLE_PIPE) ) {
            	        alt20=1;
            	    }
            	    else if ( (LA20_0==DOUBLE_AMPER) ) {
            	        alt20=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 20, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt20) {
            	        case 1 :
            	            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:124:49: lop= DOUBLE_PIPE
            	            {
            	            lop=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_andOrRestriction638); if (state.failed) return retval; 
            	            if ( state.backtracking==0 ) stream_DOUBLE_PIPE.add(lop);


            	            }
            	            break;
            	        case 2 :
            	            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:124:65: lop= DOUBLE_AMPER
            	            {
            	            lop=(Token)match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_andOrRestriction642); if (state.failed) return retval; 
            	            if ( state.backtracking==0 ) stream_DOUBLE_AMPER.add(lop);


            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_operator_in_andOrRestriction647);
            	    op=operator();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_operator.add(op.getTree());
            	    pushFollow(FOLLOW_shiftExpressionTk_in_andOrRestriction651);
            	    se2=shiftExpressionTk();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_shiftExpressionTk.add(se2.getTree());

            	    }



            	    // AST REWRITE
            	    // elements: op, lop, se2, andOrRestriction
            	    // token labels: lop
            	    // rule labels: se2, retval, op
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleTokenStream stream_lop=new RewriteRuleTokenStream(adaptor,"token lop",lop);
            	    RewriteRuleSubtreeStream stream_se2=new RewriteRuleSubtreeStream(adaptor,"rule se2",se2!=null?se2.tree:null);
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            	    RewriteRuleSubtreeStream stream_op=new RewriteRuleSubtreeStream(adaptor,"rule op",op!=null?op.tree:null);

            	    root_0 = (Object)adaptor.nil();
            	    // 125:6: -> ^( $lop $andOrRestriction ^( $op $se2) )
            	    {
            	        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:125:9: ^( $lop $andOrRestriction ^( $op $se2) )
            	        {
            	        Object root_1 = (Object)adaptor.nil();
            	        root_1 = (Object)adaptor.becomeRoot(stream_lop.nextNode(), root_1);

            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:125:34: ^( $op $se2)
            	        {
            	        Object root_2 = (Object)adaptor.nil();
            	        root_2 = (Object)adaptor.becomeRoot(stream_op.nextNode(), root_2);

            	        adaptor.addChild(root_2, (ee!=null?ee.se1:null));
            	        adaptor.addChild(root_2, stream_se2.nextTree());

            	        adaptor.addChild(root_1, root_2);
            	        }

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "andOrRestriction"

    public static class equalityExpression_return extends ParserRuleReturnScope {
        public CommonTree se1;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "equalityExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:128:1: equalityExpression returns [CommonTree se1] : ie= instanceOfExpression ( ( EQUALS | NOT_EQUALS ) instanceOfExpression )* ;
    public final DRLExpressions.equalityExpression_return equalityExpression() throws RecognitionException {
        DRLExpressions.equalityExpression_return retval = new DRLExpressions.equalityExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS55=null;
        Token NOT_EQUALS56=null;
        DRLExpressions.instanceOfExpression_return ie = null;

        DRLExpressions.instanceOfExpression_return instanceOfExpression57 = null;


        Object EQUALS55_tree=null;
        Object NOT_EQUALS56_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:130:3: (ie= instanceOfExpression ( ( EQUALS | NOT_EQUALS ) instanceOfExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:130:5: ie= instanceOfExpression ( ( EQUALS | NOT_EQUALS ) instanceOfExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression710);
            ie=instanceOfExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, ie.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:130:29: ( ( EQUALS | NOT_EQUALS ) instanceOfExpression )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( ((LA23_0>=EQUALS && LA23_0<=NOT_EQUALS)) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:130:31: ( EQUALS | NOT_EQUALS ) instanceOfExpression
            	    {
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:130:31: ( EQUALS | NOT_EQUALS )
            	    int alt22=2;
            	    int LA22_0 = input.LA(1);

            	    if ( (LA22_0==EQUALS) ) {
            	        alt22=1;
            	    }
            	    else if ( (LA22_0==NOT_EQUALS) ) {
            	        alt22=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 22, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt22) {
            	        case 1 :
            	            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:130:33: EQUALS
            	            {
            	            EQUALS55=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_equalityExpression716); if (state.failed) return retval;
            	            if ( state.backtracking==0 ) {
            	            EQUALS55_tree = (Object)adaptor.create(EQUALS55);
            	            root_0 = (Object)adaptor.becomeRoot(EQUALS55_tree, root_0);
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:130:43: NOT_EQUALS
            	            {
            	            NOT_EQUALS56=(Token)match(input,NOT_EQUALS,FOLLOW_NOT_EQUALS_in_equalityExpression721); if (state.failed) return retval;
            	            if ( state.backtracking==0 ) {
            	            NOT_EQUALS56_tree = (Object)adaptor.create(NOT_EQUALS56);
            	            root_0 = (Object)adaptor.becomeRoot(NOT_EQUALS56_tree, root_0);
            	            }

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression726);
            	    instanceOfExpression57=instanceOfExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, instanceOfExpression57.getTree());

            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               retval.se1 = (ie!=null?ie.se1:null); 
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "equalityExpression"

    public static class instanceOfExpression_return extends ParserRuleReturnScope {
        public CommonTree se1;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "instanceOfExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:133:1: instanceOfExpression returns [CommonTree se1] : ie= inExpression ( instanceof_key type )? ;
    public final DRLExpressions.instanceOfExpression_return instanceOfExpression() throws RecognitionException {
        DRLExpressions.instanceOfExpression_return retval = new DRLExpressions.instanceOfExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLExpressions.inExpression_return ie = null;

        DRLExpressions.instanceof_key_return instanceof_key58 = null;

        DRLExpressions.type_return type59 = null;



        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:135:3: (ie= inExpression ( instanceof_key type )? )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:135:5: ie= inExpression ( instanceof_key type )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_inExpression_in_instanceOfExpression755);
            ie=inExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, ie.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:135:21: ( instanceof_key type )?
            int alt24=2;
            alt24 = dfa24.predict(input);
            switch (alt24) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:135:22: instanceof_key type
                    {
                    pushFollow(FOLLOW_instanceof_key_in_instanceOfExpression758);
                    instanceof_key58=instanceof_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(instanceof_key58.getTree(), root_0);
                    pushFollow(FOLLOW_type_in_instanceOfExpression761);
                    type59=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type59.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               retval.se1 = (ie!=null?ie.se1:null); 
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "instanceOfExpression"

    public static class inExpression_return extends ParserRuleReturnScope {
        public CommonTree se1;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "inExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:138:1: inExpression returns [CommonTree se1] : (rel= relationalExpression -> relationalExpression ) ( not_key in= in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN -> ^( NEG_OPERATOR[$in.text] $rel ( expression )+ ) | in= in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN -> ^( $in $rel ( expression )+ ) )? ;
    public final DRLExpressions.inExpression_return inExpression() throws RecognitionException {
        DRLExpressions.inExpression_return retval = new DRLExpressions.inExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN61=null;
        Token COMMA63=null;
        Token RIGHT_PAREN65=null;
        Token LEFT_PAREN66=null;
        Token COMMA68=null;
        Token RIGHT_PAREN70=null;
        DRLExpressions.relationalExpression_return rel = null;

        DRLExpressions.in_key_return in = null;

        DRLExpressions.not_key_return not_key60 = null;

        DRLExpressions.expression_return expression62 = null;

        DRLExpressions.expression_return expression64 = null;

        DRLExpressions.expression_return expression67 = null;

        DRLExpressions.expression_return expression69 = null;


        Object LEFT_PAREN61_tree=null;
        Object COMMA63_tree=null;
        Object RIGHT_PAREN65_tree=null;
        Object LEFT_PAREN66_tree=null;
        Object COMMA68_tree=null;
        Object RIGHT_PAREN70_tree=null;
        RewriteRuleTokenStream stream_LEFT_PAREN=new RewriteRuleTokenStream(adaptor,"token LEFT_PAREN");
        RewriteRuleTokenStream stream_RIGHT_PAREN=new RewriteRuleTokenStream(adaptor,"token RIGHT_PAREN");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_relationalExpression=new RewriteRuleSubtreeStream(adaptor,"rule relationalExpression");
        RewriteRuleSubtreeStream stream_not_key=new RewriteRuleSubtreeStream(adaptor,"rule not_key");
        RewriteRuleSubtreeStream stream_in_key=new RewriteRuleSubtreeStream(adaptor,"rule in_key");
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:140:3: ( (rel= relationalExpression -> relationalExpression ) ( not_key in= in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN -> ^( NEG_OPERATOR[$in.text] $rel ( expression )+ ) | in= in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN -> ^( $in $rel ( expression )+ ) )? )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:140:5: (rel= relationalExpression -> relationalExpression ) ( not_key in= in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN -> ^( NEG_OPERATOR[$in.text] $rel ( expression )+ ) | in= in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN -> ^( $in $rel ( expression )+ ) )?
            {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:140:5: (rel= relationalExpression -> relationalExpression )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:140:6: rel= relationalExpression
            {
            pushFollow(FOLLOW_relationalExpression_in_inExpression790);
            rel=relationalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_relationalExpression.add(rel.getTree());


            // AST REWRITE
            // elements: relationalExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 140:31: -> relationalExpression
            {
                adaptor.addChild(root_0, stream_relationalExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:141:5: ( not_key in= in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN -> ^( NEG_OPERATOR[$in.text] $rel ( expression )+ ) | in= in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN -> ^( $in $rel ( expression )+ ) )?
            int alt27=3;
            alt27 = dfa27.predict(input);
            switch (alt27) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:141:7: not_key in= in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN
                    {
                    pushFollow(FOLLOW_not_key_in_inExpression803);
                    not_key60=not_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_not_key.add(not_key60.getTree());
                    pushFollow(FOLLOW_in_key_in_inExpression807);
                    in=in_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_in_key.add(in.getTree());
                    LEFT_PAREN61=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_inExpression809); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN61);

                    pushFollow(FOLLOW_expression_in_inExpression811);
                    expression62=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression62.getTree());
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:141:47: ( COMMA expression )*
                    loop25:
                    do {
                        int alt25=2;
                        int LA25_0 = input.LA(1);

                        if ( (LA25_0==COMMA) ) {
                            alt25=1;
                        }


                        switch (alt25) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:141:48: COMMA expression
                    	    {
                    	    COMMA63=(Token)match(input,COMMA,FOLLOW_COMMA_in_inExpression814); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA63);

                    	    pushFollow(FOLLOW_expression_in_inExpression816);
                    	    expression64=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expression.add(expression64.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop25;
                        }
                    } while (true);

                    RIGHT_PAREN65=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_inExpression820); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN65);



                    // AST REWRITE
                    // elements: expression, rel
                    // token labels: 
                    // rule labels: retval, rel
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_rel=new RewriteRuleSubtreeStream(adaptor,"rule rel",rel!=null?rel.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 141:79: -> ^( NEG_OPERATOR[$in.text] $rel ( expression )+ )
                    {
                        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:141:82: ^( NEG_OPERATOR[$in.text] $rel ( expression )+ )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(NEG_OPERATOR, (in!=null?input.toString(in.start,in.stop):null)), root_1);

                        adaptor.addChild(root_1, stream_rel.nextTree());
                        if ( !(stream_expression.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_expression.hasNext() ) {
                            adaptor.addChild(root_1, stream_expression.nextTree());

                        }
                        stream_expression.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:142:7: in= in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN
                    {
                    pushFollow(FOLLOW_in_key_in_inExpression843);
                    in=in_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_in_key.add(in.getTree());
                    LEFT_PAREN66=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_inExpression845); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN66);

                    pushFollow(FOLLOW_expression_in_inExpression847);
                    expression67=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression67.getTree());
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:142:39: ( COMMA expression )*
                    loop26:
                    do {
                        int alt26=2;
                        int LA26_0 = input.LA(1);

                        if ( (LA26_0==COMMA) ) {
                            alt26=1;
                        }


                        switch (alt26) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:142:40: COMMA expression
                    	    {
                    	    COMMA68=(Token)match(input,COMMA,FOLLOW_COMMA_in_inExpression850); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA68);

                    	    pushFollow(FOLLOW_expression_in_inExpression852);
                    	    expression69=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expression.add(expression69.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop26;
                        }
                    } while (true);

                    RIGHT_PAREN70=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_inExpression856); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN70);



                    // AST REWRITE
                    // elements: expression, in, rel
                    // token labels: 
                    // rule labels: retval, rel, in
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_rel=new RewriteRuleSubtreeStream(adaptor,"rule rel",rel!=null?rel.tree:null);
                    RewriteRuleSubtreeStream stream_in=new RewriteRuleSubtreeStream(adaptor,"rule in",in!=null?in.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 142:71: -> ^( $in $rel ( expression )+ )
                    {
                        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:142:74: ^( $in $rel ( expression )+ )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_in.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_rel.nextTree());
                        if ( !(stream_expression.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_expression.hasNext() ) {
                            adaptor.addChild(root_1, stream_expression.nextTree());

                        }
                        stream_expression.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               retval.se1 = (rel!=null?rel.se1:null); 
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "inExpression"

    public static class relationalExpression_return extends ParserRuleReturnScope {
        public CommonTree se1;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "relationalExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:146:1: relationalExpression returns [CommonTree se1] : se= shiftExpressionTk ( ( relationalOp )=> relationalOp shiftExpressionTk )* ;
    public final DRLExpressions.relationalExpression_return relationalExpression() throws RecognitionException {
        DRLExpressions.relationalExpression_return retval = new DRLExpressions.relationalExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLExpressions.shiftExpressionTk_return se = null;

        DRLExpressions.relationalOp_return relationalOp71 = null;

        DRLExpressions.shiftExpressionTk_return shiftExpressionTk72 = null;



        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:148:3: (se= shiftExpressionTk ( ( relationalOp )=> relationalOp shiftExpressionTk )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:148:5: se= shiftExpressionTk ( ( relationalOp )=> relationalOp shiftExpressionTk )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_shiftExpressionTk_in_relationalExpression900);
            se=shiftExpressionTk();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, se.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:148:26: ( ( relationalOp )=> relationalOp shiftExpressionTk )*
            loop28:
            do {
                int alt28=2;
                alt28 = dfa28.predict(input);
                switch (alt28) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:148:28: ( relationalOp )=> relationalOp shiftExpressionTk
            	    {
            	    pushFollow(FOLLOW_relationalOp_in_relationalExpression909);
            	    relationalOp71=relationalOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(relationalOp71.getTree(), root_0);
            	    pushFollow(FOLLOW_shiftExpressionTk_in_relationalExpression912);
            	    shiftExpressionTk72=shiftExpressionTk();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpressionTk72.getTree());

            	    }
            	    break;

            	default :
            	    break loop28;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               retval.se1 = (CommonTree) (se!=null?((Object)se.tree):null); 
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "relationalExpression"

    public static class operator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "operator"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:151:1: operator : ( EQUALS | NOT_EQUALS | relationalOp ) ;
    public final DRLExpressions.operator_return operator() throws RecognitionException {
        DRLExpressions.operator_return retval = new DRLExpressions.operator_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS73=null;
        Token NOT_EQUALS74=null;
        DRLExpressions.relationalOp_return relationalOp75 = null;


        Object EQUALS73_tree=null;
        Object NOT_EQUALS74_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:152:3: ( ( EQUALS | NOT_EQUALS | relationalOp ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:152:5: ( EQUALS | NOT_EQUALS | relationalOp )
            {
            root_0 = (Object)adaptor.nil();

            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:152:5: ( EQUALS | NOT_EQUALS | relationalOp )
            int alt29=3;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==EQUALS) ) {
                alt29=1;
            }
            else if ( (LA29_0==NOT_EQUALS) ) {
                alt29=2;
            }
            else if ( ((LA29_0>=GREATER_EQUALS && LA29_0<=LESS)) ) {
                alt29=3;
            }
            else if ( (LA29_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) {
                alt29=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:152:7: EQUALS
                    {
                    EQUALS73=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_operator930); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQUALS73_tree = (Object)adaptor.create(EQUALS73);
                    adaptor.addChild(root_0, EQUALS73_tree);
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:153:7: NOT_EQUALS
                    {
                    NOT_EQUALS74=(Token)match(input,NOT_EQUALS,FOLLOW_NOT_EQUALS_in_operator938); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NOT_EQUALS74_tree = (Object)adaptor.create(NOT_EQUALS74);
                    adaptor.addChild(root_0, NOT_EQUALS74_tree);
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:154:7: relationalOp
                    {
                    pushFollow(FOLLOW_relationalOp_in_operator946);
                    relationalOp75=relationalOp();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalOp75.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "operator"

    public static class relationalOp_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "relationalOp"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:158:1: relationalOp : ( LESS_EQUALS | GREATER_EQUALS | LESS | GREATER | not_key neg_operator_key ( ( squareArguments )=> squareArguments )? | operator_key ( ( squareArguments )=> squareArguments )? ) ;
    public final DRLExpressions.relationalOp_return relationalOp() throws RecognitionException {
        DRLExpressions.relationalOp_return retval = new DRLExpressions.relationalOp_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LESS_EQUALS76=null;
        Token GREATER_EQUALS77=null;
        Token LESS78=null;
        Token GREATER79=null;
        DRLExpressions.not_key_return not_key80 = null;

        DRLExpressions.neg_operator_key_return neg_operator_key81 = null;

        DRLExpressions.squareArguments_return squareArguments82 = null;

        DRLExpressions.operator_key_return operator_key83 = null;

        DRLExpressions.squareArguments_return squareArguments84 = null;


        Object LESS_EQUALS76_tree=null;
        Object GREATER_EQUALS77_tree=null;
        Object LESS78_tree=null;
        Object GREATER79_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:159:3: ( ( LESS_EQUALS | GREATER_EQUALS | LESS | GREATER | not_key neg_operator_key ( ( squareArguments )=> squareArguments )? | operator_key ( ( squareArguments )=> squareArguments )? ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:159:5: ( LESS_EQUALS | GREATER_EQUALS | LESS | GREATER | not_key neg_operator_key ( ( squareArguments )=> squareArguments )? | operator_key ( ( squareArguments )=> squareArguments )? )
            {
            root_0 = (Object)adaptor.nil();

            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:159:5: ( LESS_EQUALS | GREATER_EQUALS | LESS | GREATER | not_key neg_operator_key ( ( squareArguments )=> squareArguments )? | operator_key ( ( squareArguments )=> squareArguments )? )
            int alt32=6;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==LESS_EQUALS) ) {
                alt32=1;
            }
            else if ( (LA32_0==GREATER_EQUALS) ) {
                alt32=2;
            }
            else if ( (LA32_0==LESS) ) {
                alt32=3;
            }
            else if ( (LA32_0==GREATER) ) {
                alt32=4;
            }
            else if ( (LA32_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) {
                int LA32_5 = input.LA(2);

                if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                    alt32=5;
                }
                else if ( (((helper.isPluggableEvaluator(false)))) ) {
                    alt32=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 5, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:159:7: LESS_EQUALS
                    {
                    LESS_EQUALS76=(Token)match(input,LESS_EQUALS,FOLLOW_LESS_EQUALS_in_relationalOp969); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESS_EQUALS76_tree = (Object)adaptor.create(LESS_EQUALS76);
                    adaptor.addChild(root_0, LESS_EQUALS76_tree);
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:160:7: GREATER_EQUALS
                    {
                    GREATER_EQUALS77=(Token)match(input,GREATER_EQUALS,FOLLOW_GREATER_EQUALS_in_relationalOp977); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER_EQUALS77_tree = (Object)adaptor.create(GREATER_EQUALS77);
                    adaptor.addChild(root_0, GREATER_EQUALS77_tree);
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:161:7: LESS
                    {
                    LESS78=(Token)match(input,LESS,FOLLOW_LESS_in_relationalOp986); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESS78_tree = (Object)adaptor.create(LESS78);
                    adaptor.addChild(root_0, LESS78_tree);
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:162:7: GREATER
                    {
                    GREATER79=(Token)match(input,GREATER,FOLLOW_GREATER_in_relationalOp995); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER79_tree = (Object)adaptor.create(GREATER79);
                    adaptor.addChild(root_0, GREATER79_tree);
                    }

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:163:7: not_key neg_operator_key ( ( squareArguments )=> squareArguments )?
                    {
                    pushFollow(FOLLOW_not_key_in_relationalOp1003);
                    not_key80=not_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, not_key80.getTree());
                    pushFollow(FOLLOW_neg_operator_key_in_relationalOp1005);
                    neg_operator_key81=neg_operator_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(neg_operator_key81.getTree(), root_0);
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:163:33: ( ( squareArguments )=> squareArguments )?
                    int alt30=2;
                    alt30 = dfa30.predict(input);
                    switch (alt30) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:163:34: ( squareArguments )=> squareArguments
                            {
                            pushFollow(FOLLOW_squareArguments_in_relationalOp1014);
                            squareArguments82=squareArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, squareArguments82.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:164:7: operator_key ( ( squareArguments )=> squareArguments )?
                    {
                    pushFollow(FOLLOW_operator_key_in_relationalOp1024);
                    operator_key83=operator_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(operator_key83.getTree(), root_0);
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:164:22: ( ( squareArguments )=> squareArguments )?
                    int alt31=2;
                    alt31 = dfa31.predict(input);
                    switch (alt31) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:164:23: ( squareArguments )=> squareArguments
                            {
                            pushFollow(FOLLOW_squareArguments_in_relationalOp1034);
                            squareArguments84=squareArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, squareArguments84.getTree());

                            }
                            break;

                    }


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "relationalOp"

    public static class shiftExpressionTk_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "shiftExpressionTk"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:168:1: shiftExpressionTk : se= shiftExpression -> SHIFT_EXPR[$se.text] ;
    public final DRLExpressions.shiftExpressionTk_return shiftExpressionTk() throws RecognitionException {
        DRLExpressions.shiftExpressionTk_return retval = new DRLExpressions.shiftExpressionTk_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLExpressions.shiftExpression_return se = null;


        RewriteRuleSubtreeStream stream_shiftExpression=new RewriteRuleSubtreeStream(adaptor,"rule shiftExpression");
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:169:5: (se= shiftExpression -> SHIFT_EXPR[$se.text] )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:169:7: se= shiftExpression
            {
            pushFollow(FOLLOW_shiftExpression_in_shiftExpressionTk1065);
            se=shiftExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_shiftExpression.add(se.getTree());


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 169:26: -> SHIFT_EXPR[$se.text]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(SHIFT_EXPR, (se!=null?input.toString(se.start,se.stop):null)));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "shiftExpressionTk"

    public static class shiftExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "shiftExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:172:1: shiftExpression : additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )* ;
    public final DRLExpressions.shiftExpression_return shiftExpression() throws RecognitionException {
        DRLExpressions.shiftExpression_return retval = new DRLExpressions.shiftExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLExpressions.additiveExpression_return additiveExpression85 = null;

        DRLExpressions.shiftOp_return shiftOp86 = null;

        DRLExpressions.additiveExpression_return additiveExpression87 = null;



        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:173:3: ( additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:173:5: additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_additiveExpression_in_shiftExpression1085);
            additiveExpression85=additiveExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression85.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:173:24: ( ( shiftOp )=> shiftOp additiveExpression )*
            loop33:
            do {
                int alt33=2;
                alt33 = dfa33.predict(input);
                switch (alt33) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:173:26: ( shiftOp )=> shiftOp additiveExpression
            	    {
            	    pushFollow(FOLLOW_shiftOp_in_shiftExpression1093);
            	    shiftOp86=shiftOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftOp86.getTree());
            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression1095);
            	    additiveExpression87=additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression87.getTree());

            	    }
            	    break;

            	default :
            	    break loop33;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "shiftExpression"

    public static class shiftOp_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "shiftOp"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:176:1: shiftOp : ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER ) ;
    public final DRLExpressions.shiftOp_return shiftOp() throws RecognitionException {
        DRLExpressions.shiftOp_return retval = new DRLExpressions.shiftOp_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LESS88=null;
        Token LESS89=null;
        Token GREATER90=null;
        Token GREATER91=null;
        Token GREATER92=null;
        Token GREATER93=null;
        Token GREATER94=null;

        Object LESS88_tree=null;
        Object LESS89_tree=null;
        Object GREATER90_tree=null;
        Object GREATER91_tree=null;
        Object GREATER92_tree=null;
        Object GREATER93_tree=null;
        Object GREATER94_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:177:5: ( ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:177:7: ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
            {
            root_0 = (Object)adaptor.nil();

            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:177:7: ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
            int alt34=3;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==LESS) ) {
                alt34=1;
            }
            else if ( (LA34_0==GREATER) ) {
                int LA34_2 = input.LA(2);

                if ( (LA34_2==GREATER) ) {
                    int LA34_3 = input.LA(3);

                    if ( (LA34_3==GREATER) ) {
                        alt34=2;
                    }
                    else if ( (LA34_3==EOF||LA34_3==FLOAT||(LA34_3>=HEX && LA34_3<=DECIMAL)||LA34_3==STRING||(LA34_3>=BOOL && LA34_3<=NULL)||(LA34_3>=DECR && LA34_3<=INCR)||LA34_3==LESS||LA34_3==LEFT_PAREN||LA34_3==LEFT_SQUARE||(LA34_3>=NEGATION && LA34_3<=TILDE)||(LA34_3>=MINUS && LA34_3<=PLUS)||LA34_3==ID) ) {
                        alt34=3;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 34, 3, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 34, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;
            }
            switch (alt34) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:177:8: LESS LESS
                    {
                    LESS88=(Token)match(input,LESS,FOLLOW_LESS_in_shiftOp1116); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESS88_tree = (Object)adaptor.create(LESS88);
                    adaptor.addChild(root_0, LESS88_tree);
                    }
                    LESS89=(Token)match(input,LESS,FOLLOW_LESS_in_shiftOp1118); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESS89_tree = (Object)adaptor.create(LESS89);
                    adaptor.addChild(root_0, LESS89_tree);
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:177:20: GREATER GREATER GREATER
                    {
                    GREATER90=(Token)match(input,GREATER,FOLLOW_GREATER_in_shiftOp1122); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER90_tree = (Object)adaptor.create(GREATER90);
                    adaptor.addChild(root_0, GREATER90_tree);
                    }
                    GREATER91=(Token)match(input,GREATER,FOLLOW_GREATER_in_shiftOp1124); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER91_tree = (Object)adaptor.create(GREATER91);
                    adaptor.addChild(root_0, GREATER91_tree);
                    }
                    GREATER92=(Token)match(input,GREATER,FOLLOW_GREATER_in_shiftOp1126); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER92_tree = (Object)adaptor.create(GREATER92);
                    adaptor.addChild(root_0, GREATER92_tree);
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:177:46: GREATER GREATER
                    {
                    GREATER93=(Token)match(input,GREATER,FOLLOW_GREATER_in_shiftOp1130); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER93_tree = (Object)adaptor.create(GREATER93);
                    adaptor.addChild(root_0, GREATER93_tree);
                    }
                    GREATER94=(Token)match(input,GREATER,FOLLOW_GREATER_in_shiftOp1132); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER94_tree = (Object)adaptor.create(GREATER94);
                    adaptor.addChild(root_0, GREATER94_tree);
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "shiftOp"

    public static class additiveExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "additiveExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:180:1: additiveExpression : multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )* ;
    public final DRLExpressions.additiveExpression_return additiveExpression() throws RecognitionException {
        DRLExpressions.additiveExpression_return retval = new DRLExpressions.additiveExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set96=null;
        DRLExpressions.multiplicativeExpression_return multiplicativeExpression95 = null;

        DRLExpressions.multiplicativeExpression_return multiplicativeExpression97 = null;


        Object set96_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:181:3: ( multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:181:7: multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression1151);
            multiplicativeExpression95=multiplicativeExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression95.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:181:32: ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*
            loop35:
            do {
                int alt35=2;
                alt35 = dfa35.predict(input);
                switch (alt35) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:181:34: ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression
            	    {
            	    set96=(Token)input.LT(1);
            	    if ( (input.LA(1)>=MINUS && input.LA(1)<=PLUS) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set96));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression1170);
            	    multiplicativeExpression97=multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression97.getTree());

            	    }
            	    break;

            	default :
            	    break loop35;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "additiveExpression"

    public static class multiplicativeExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "multiplicativeExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:184:1: multiplicativeExpression : unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )* ;
    public final DRLExpressions.multiplicativeExpression_return multiplicativeExpression() throws RecognitionException {
        DRLExpressions.multiplicativeExpression_return retval = new DRLExpressions.multiplicativeExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set99=null;
        DRLExpressions.unaryExpression_return unaryExpression98 = null;

        DRLExpressions.unaryExpression_return unaryExpression100 = null;


        Object set99_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:185:3: ( unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:185:7: unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression1190);
            unaryExpression98=unaryExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression98.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:185:23: ( ( STAR | DIV | MOD ) unaryExpression )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( ((LA36_0>=MOD && LA36_0<=STAR)||LA36_0==DIV) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:185:25: ( STAR | DIV | MOD ) unaryExpression
            	    {
            	    set99=(Token)input.LT(1);
            	    if ( (input.LA(1)>=MOD && input.LA(1)<=STAR)||input.LA(1)==DIV ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set99));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression1208);
            	    unaryExpression100=unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression100.getTree());

            	    }
            	    break;

            	default :
            	    break loop36;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "multiplicativeExpression"

    public static class unaryExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unaryExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:188:1: unaryExpression : ( PLUS unaryExpression | MINUS unaryExpression | INCR primary | DECR primary | unaryExpressionNotPlusMinus );
    public final DRLExpressions.unaryExpression_return unaryExpression() throws RecognitionException {
        DRLExpressions.unaryExpression_return retval = new DRLExpressions.unaryExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PLUS101=null;
        Token MINUS103=null;
        Token INCR105=null;
        Token DECR107=null;
        DRLExpressions.unaryExpression_return unaryExpression102 = null;

        DRLExpressions.unaryExpression_return unaryExpression104 = null;

        DRLExpressions.primary_return primary106 = null;

        DRLExpressions.primary_return primary108 = null;

        DRLExpressions.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus109 = null;


        Object PLUS101_tree=null;
        Object MINUS103_tree=null;
        Object INCR105_tree=null;
        Object DECR107_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:189:5: ( PLUS unaryExpression | MINUS unaryExpression | INCR primary | DECR primary | unaryExpressionNotPlusMinus )
            int alt37=5;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt37=1;
                }
                break;
            case MINUS:
                {
                alt37=2;
                }
                break;
            case INCR:
                {
                alt37=3;
                }
                break;
            case DECR:
                {
                alt37=4;
                }
                break;
            case FLOAT:
            case HEX:
            case DECIMAL:
            case STRING:
            case BOOL:
            case NULL:
            case LESS:
            case LEFT_PAREN:
            case LEFT_SQUARE:
            case NEGATION:
            case TILDE:
            case ID:
                {
                alt37=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 37, 0, input);

                throw nvae;
            }

            switch (alt37) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:189:9: PLUS unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    PLUS101=(Token)match(input,PLUS,FOLLOW_PLUS_in_unaryExpression1230); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    PLUS101_tree = (Object)adaptor.create(PLUS101);
                    adaptor.addChild(root_0, PLUS101_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression1232);
                    unaryExpression102=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression102.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:190:7: MINUS unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    MINUS103=(Token)match(input,MINUS,FOLLOW_MINUS_in_unaryExpression1240); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MINUS103_tree = (Object)adaptor.create(MINUS103);
                    adaptor.addChild(root_0, MINUS103_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression1242);
                    unaryExpression104=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression104.getTree());

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:191:9: INCR primary
                    {
                    root_0 = (Object)adaptor.nil();

                    INCR105=(Token)match(input,INCR,FOLLOW_INCR_in_unaryExpression1252); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INCR105_tree = (Object)adaptor.create(INCR105);
                    adaptor.addChild(root_0, INCR105_tree);
                    }
                    pushFollow(FOLLOW_primary_in_unaryExpression1254);
                    primary106=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primary106.getTree());

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:192:9: DECR primary
                    {
                    root_0 = (Object)adaptor.nil();

                    DECR107=(Token)match(input,DECR,FOLLOW_DECR_in_unaryExpression1264); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DECR107_tree = (Object)adaptor.create(DECR107);
                    adaptor.addChild(root_0, DECR107_tree);
                    }
                    pushFollow(FOLLOW_primary_in_unaryExpression1266);
                    primary108=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primary108.getTree());

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:193:9: unaryExpressionNotPlusMinus
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression1276);
                    unaryExpressionNotPlusMinus109=unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpressionNotPlusMinus109.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "unaryExpression"

    public static class unaryExpressionNotPlusMinus_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unaryExpressionNotPlusMinus"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:196:1: unaryExpressionNotPlusMinus : ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? );
    public final DRLExpressions.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus() throws RecognitionException {
        DRLExpressions.unaryExpressionNotPlusMinus_return retval = new DRLExpressions.unaryExpressionNotPlusMinus_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TILDE110=null;
        Token NEGATION112=null;
        Token set117=null;
        DRLExpressions.unaryExpression_return unaryExpression111 = null;

        DRLExpressions.unaryExpression_return unaryExpression113 = null;

        DRLExpressions.castExpression_return castExpression114 = null;

        DRLExpressions.primary_return primary115 = null;

        DRLExpressions.selector_return selector116 = null;


        Object TILDE110_tree=null;
        Object NEGATION112_tree=null;
        Object set117_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:197:5: ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? )
            int alt40=4;
            alt40 = dfa40.predict(input);
            switch (alt40) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:197:9: TILDE unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    TILDE110=(Token)match(input,TILDE,FOLLOW_TILDE_in_unaryExpressionNotPlusMinus1295); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TILDE110_tree = (Object)adaptor.create(TILDE110);
                    adaptor.addChild(root_0, TILDE110_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus1297);
                    unaryExpression111=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression111.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:198:8: NEGATION unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    NEGATION112=(Token)match(input,NEGATION,FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus1306); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NEGATION112_tree = (Object)adaptor.create(NEGATION112);
                    adaptor.addChild(root_0, NEGATION112_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus1308);
                    unaryExpression113=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression113.getTree());

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:199:9: ( castExpression )=> castExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus1322);
                    castExpression114=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, castExpression114.getTree());

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:200:9: primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus1332);
                    primary115=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primary115.getTree());
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:200:17: ( ( selector )=> selector )*
                    loop38:
                    do {
                        int alt38=2;
                        alt38 = dfa38.predict(input);
                        switch (alt38) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:200:18: ( selector )=> selector
                    	    {
                    	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus1339);
                    	    selector116=selector();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, selector116.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop38;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:200:41: ( ( INCR | DECR )=> ( INCR | DECR ) )?
                    int alt39=2;
                    alt39 = dfa39.predict(input);
                    switch (alt39) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:200:42: ( INCR | DECR )=> ( INCR | DECR )
                            {
                            set117=(Token)input.LT(1);
                            if ( (input.LA(1)>=DECR && input.LA(1)<=INCR) ) {
                                input.consume();
                                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set117));
                                state.errorRecovery=false;state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "unaryExpressionNotPlusMinus"

    public static class castExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "castExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:203:1: castExpression : ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus );
    public final DRLExpressions.castExpression_return castExpression() throws RecognitionException {
        DRLExpressions.castExpression_return retval = new DRLExpressions.castExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN118=null;
        Token RIGHT_PAREN120=null;
        Token LEFT_PAREN122=null;
        Token RIGHT_PAREN124=null;
        DRLExpressions.primitiveType_return primitiveType119 = null;

        DRLExpressions.unaryExpression_return unaryExpression121 = null;

        DRLExpressions.type_return type123 = null;

        DRLExpressions.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus125 = null;


        Object LEFT_PAREN118_tree=null;
        Object RIGHT_PAREN120_tree=null;
        Object LEFT_PAREN122_tree=null;
        Object RIGHT_PAREN124_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:204:5: ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus )
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==LEFT_PAREN) ) {
                int LA41_1 = input.LA(2);

                if ( (synpred16_DRLExpressions()) ) {
                    alt41=1;
                }
                else if ( (synpred17_DRLExpressions()) ) {
                    alt41=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 41, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 41, 0, input);

                throw nvae;
            }
            switch (alt41) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:204:8: ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    LEFT_PAREN118=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression1387); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LEFT_PAREN118_tree = (Object)adaptor.create(LEFT_PAREN118);
                    adaptor.addChild(root_0, LEFT_PAREN118_tree);
                    }
                    pushFollow(FOLLOW_primitiveType_in_castExpression1389);
                    primitiveType119=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType119.getTree());
                    RIGHT_PAREN120=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression1391); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RIGHT_PAREN120_tree = (Object)adaptor.create(RIGHT_PAREN120);
                    adaptor.addChild(root_0, RIGHT_PAREN120_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_castExpression1393);
                    unaryExpression121=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression121.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:205:8: ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus
                    {
                    root_0 = (Object)adaptor.nil();

                    LEFT_PAREN122=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression1410); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LEFT_PAREN122_tree = (Object)adaptor.create(LEFT_PAREN122);
                    adaptor.addChild(root_0, LEFT_PAREN122_tree);
                    }
                    pushFollow(FOLLOW_type_in_castExpression1412);
                    type123=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type123.getTree());
                    RIGHT_PAREN124=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression1414); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RIGHT_PAREN124_tree = (Object)adaptor.create(RIGHT_PAREN124);
                    adaptor.addChild(root_0, RIGHT_PAREN124_tree);
                    }
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression1416);
                    unaryExpressionNotPlusMinus125=unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpressionNotPlusMinus125.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "castExpression"

    public static class primitiveType_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "primitiveType"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:208:1: primitiveType : ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key );
    public final DRLExpressions.primitiveType_return primitiveType() throws RecognitionException {
        DRLExpressions.primitiveType_return retval = new DRLExpressions.primitiveType_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLExpressions.boolean_key_return boolean_key126 = null;

        DRLExpressions.char_key_return char_key127 = null;

        DRLExpressions.byte_key_return byte_key128 = null;

        DRLExpressions.short_key_return short_key129 = null;

        DRLExpressions.int_key_return int_key130 = null;

        DRLExpressions.long_key_return long_key131 = null;

        DRLExpressions.float_key_return float_key132 = null;

        DRLExpressions.double_key_return double_key133 = null;



        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:209:5: ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key )
            int alt42=8;
            alt42 = dfa42.predict(input);
            switch (alt42) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:209:7: boolean_key
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_boolean_key_in_primitiveType1437);
                    boolean_key126=boolean_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, boolean_key126.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:210:7: char_key
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_char_key_in_primitiveType1445);
                    char_key127=char_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, char_key127.getTree());

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:211:7: byte_key
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_byte_key_in_primitiveType1453);
                    byte_key128=byte_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, byte_key128.getTree());

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:212:7: short_key
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_short_key_in_primitiveType1461);
                    short_key129=short_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, short_key129.getTree());

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:213:7: int_key
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_int_key_in_primitiveType1469);
                    int_key130=int_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, int_key130.getTree());

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:214:7: long_key
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_long_key_in_primitiveType1477);
                    long_key131=long_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, long_key131.getTree());

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:215:7: float_key
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_float_key_in_primitiveType1485);
                    float_key132=float_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, float_key132.getTree());

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:216:7: double_key
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_double_key_in_primitiveType1493);
                    double_key133=double_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, double_key133.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "primitiveType"

    public static class primary_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "primary"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:219:1: primary : ( ( parExpression )=> parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )? );
    public final DRLExpressions.primary_return primary() throws RecognitionException {
        DRLExpressions.primary_return retval = new DRLExpressions.primary_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE145=null;
        Token RIGHT_SQUARE146=null;
        Token DOT147=null;
        Token ID151=null;
        Token DOT152=null;
        Token ID153=null;
        DRLExpressions.parExpression_return parExpression134 = null;

        DRLExpressions.nonWildcardTypeArguments_return nonWildcardTypeArguments135 = null;

        DRLExpressions.explicitGenericInvocationSuffix_return explicitGenericInvocationSuffix136 = null;

        DRLExpressions.this_key_return this_key137 = null;

        DRLExpressions.arguments_return arguments138 = null;

        DRLExpressions.literal_return literal139 = null;

        DRLExpressions.super_key_return super_key140 = null;

        DRLExpressions.superSuffix_return superSuffix141 = null;

        DRLExpressions.new_key_return new_key142 = null;

        DRLExpressions.creator_return creator143 = null;

        DRLExpressions.primitiveType_return primitiveType144 = null;

        DRLExpressions.class_key_return class_key148 = null;

        DRLExpressions.inlineMapExpression_return inlineMapExpression149 = null;

        DRLExpressions.inlineListExpression_return inlineListExpression150 = null;

        DRLExpressions.identifierSuffix_return identifierSuffix154 = null;


        Object LEFT_SQUARE145_tree=null;
        Object RIGHT_SQUARE146_tree=null;
        Object DOT147_tree=null;
        Object ID151_tree=null;
        Object DOT152_tree=null;
        Object ID153_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:220:5: ( ( parExpression )=> parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )? )
            int alt47=9;
            alt47 = dfa47.predict(input);
            switch (alt47) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:220:7: ( parExpression )=> parExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_parExpression_in_primary1515);
                    parExpression134=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression134.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:221:9: ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments )
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_primary1530);
                    nonWildcardTypeArguments135=nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments135.getTree());
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:221:63: ( explicitGenericInvocationSuffix | this_key arguments )
                    int alt43=2;
                    int LA43_0 = input.LA(1);

                    if ( (LA43_0==ID) ) {
                        int LA43_1 = input.LA(2);

                        if ( (!((((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))))) ) {
                            alt43=1;
                        }
                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))) ) {
                            alt43=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 43, 1, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 43, 0, input);

                        throw nvae;
                    }
                    switch (alt43) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:221:64: explicitGenericInvocationSuffix
                            {
                            pushFollow(FOLLOW_explicitGenericInvocationSuffix_in_primary1533);
                            explicitGenericInvocationSuffix136=explicitGenericInvocationSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, explicitGenericInvocationSuffix136.getTree());

                            }
                            break;
                        case 2 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:221:98: this_key arguments
                            {
                            pushFollow(FOLLOW_this_key_in_primary1537);
                            this_key137=this_key();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, this_key137.getTree());
                            pushFollow(FOLLOW_arguments_in_primary1539);
                            arguments138=arguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments138.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:222:9: ( literal )=> literal
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_literal_in_primary1555);
                    literal139=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal139.getTree());

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:224:9: ( super_key )=> super_key superSuffix
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_super_key_in_primary1575);
                    super_key140=super_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, super_key140.getTree());
                    pushFollow(FOLLOW_superSuffix_in_primary1577);
                    superSuffix141=superSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, superSuffix141.getTree());

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:225:9: ( new_key )=> new_key creator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_new_key_in_primary1592);
                    new_key142=new_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, new_key142.getTree());
                    pushFollow(FOLLOW_creator_in_primary1594);
                    creator143=creator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, creator143.getTree());

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:226:9: ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primitiveType_in_primary1609);
                    primitiveType144=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType144.getTree());
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:226:41: ( LEFT_SQUARE RIGHT_SQUARE )*
                    loop44:
                    do {
                        int alt44=2;
                        int LA44_0 = input.LA(1);

                        if ( (LA44_0==LEFT_SQUARE) ) {
                            alt44=1;
                        }


                        switch (alt44) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:226:42: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    LEFT_SQUARE145=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_primary1612); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    LEFT_SQUARE145_tree = (Object)adaptor.create(LEFT_SQUARE145);
                    	    adaptor.addChild(root_0, LEFT_SQUARE145_tree);
                    	    }
                    	    RIGHT_SQUARE146=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_primary1614); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    RIGHT_SQUARE146_tree = (Object)adaptor.create(RIGHT_SQUARE146);
                    	    adaptor.addChild(root_0, RIGHT_SQUARE146_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop44;
                        }
                    } while (true);

                    DOT147=(Token)match(input,DOT,FOLLOW_DOT_in_primary1618); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DOT147_tree = (Object)adaptor.create(DOT147);
                    adaptor.addChild(root_0, DOT147_tree);
                    }
                    pushFollow(FOLLOW_class_key_in_primary1620);
                    class_key148=class_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, class_key148.getTree());

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:228:9: ( inlineMapExpression )=> inlineMapExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_inlineMapExpression_in_primary1640);
                    inlineMapExpression149=inlineMapExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, inlineMapExpression149.getTree());

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:229:9: ( inlineListExpression )=> inlineListExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_inlineListExpression_in_primary1655);
                    inlineListExpression150=inlineListExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, inlineListExpression150.getTree());

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:230:9: ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )?
                    {
                    root_0 = (Object)adaptor.nil();

                    ID151=(Token)match(input,ID,FOLLOW_ID_in_primary1669); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID151_tree = (Object)adaptor.create(ID151);
                    adaptor.addChild(root_0, ID151_tree);
                    }
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:230:18: ( ( DOT ID )=> DOT ID )*
                    loop45:
                    do {
                        int alt45=2;
                        int LA45_0 = input.LA(1);

                        if ( (LA45_0==DOT) ) {
                            int LA45_2 = input.LA(2);

                            if ( (LA45_2==ID) ) {
                                int LA45_3 = input.LA(3);

                                if ( (synpred27_DRLExpressions()) ) {
                                    alt45=1;
                                }


                            }


                        }


                        switch (alt45) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:230:19: ( DOT ID )=> DOT ID
                    	    {
                    	    DOT152=(Token)match(input,DOT,FOLLOW_DOT_in_primary1678); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    DOT152_tree = (Object)adaptor.create(DOT152);
                    	    adaptor.addChild(root_0, DOT152_tree);
                    	    }
                    	    ID153=(Token)match(input,ID,FOLLOW_ID_in_primary1680); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    ID153_tree = (Object)adaptor.create(ID153);
                    	    adaptor.addChild(root_0, ID153_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop45;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:230:38: ( ( identifierSuffix )=> identifierSuffix )?
                    int alt46=2;
                    alt46 = dfa46.predict(input);
                    switch (alt46) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:230:39: ( identifierSuffix )=> identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary1689);
                            identifierSuffix154=identifierSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, identifierSuffix154.getTree());

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "primary"

    public static class inlineListExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "inlineListExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:233:1: inlineListExpression : LEFT_SQUARE ( expressionList )? RIGHT_SQUARE ;
    public final DRLExpressions.inlineListExpression_return inlineListExpression() throws RecognitionException {
        DRLExpressions.inlineListExpression_return retval = new DRLExpressions.inlineListExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE155=null;
        Token RIGHT_SQUARE157=null;
        DRLExpressions.expressionList_return expressionList156 = null;


        Object LEFT_SQUARE155_tree=null;
        Object RIGHT_SQUARE157_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:234:5: ( LEFT_SQUARE ( expressionList )? RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:234:9: LEFT_SQUARE ( expressionList )? RIGHT_SQUARE
            {
            root_0 = (Object)adaptor.nil();

            LEFT_SQUARE155=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_inlineListExpression1710); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            LEFT_SQUARE155_tree = (Object)adaptor.create(LEFT_SQUARE155);
            adaptor.addChild(root_0, LEFT_SQUARE155_tree);
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:234:21: ( expressionList )?
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==FLOAT||(LA48_0>=HEX && LA48_0<=DECIMAL)||LA48_0==STRING||(LA48_0>=BOOL && LA48_0<=NULL)||(LA48_0>=DECR && LA48_0<=INCR)||LA48_0==LESS||LA48_0==LEFT_PAREN||LA48_0==LEFT_SQUARE||(LA48_0>=NEGATION && LA48_0<=TILDE)||(LA48_0>=MINUS && LA48_0<=PLUS)||LA48_0==ID) ) {
                alt48=1;
            }
            switch (alt48) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:234:21: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_inlineListExpression1712);
                    expressionList156=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList156.getTree());

                    }
                    break;

            }

            RIGHT_SQUARE157=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_inlineListExpression1715); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RIGHT_SQUARE157_tree = (Object)adaptor.create(RIGHT_SQUARE157);
            adaptor.addChild(root_0, RIGHT_SQUARE157_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "inlineListExpression"

    public static class inlineMapExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "inlineMapExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:237:1: inlineMapExpression : LEFT_SQUARE ( mapExpressionList )+ RIGHT_SQUARE ;
    public final DRLExpressions.inlineMapExpression_return inlineMapExpression() throws RecognitionException {
        DRLExpressions.inlineMapExpression_return retval = new DRLExpressions.inlineMapExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE158=null;
        Token RIGHT_SQUARE160=null;
        DRLExpressions.mapExpressionList_return mapExpressionList159 = null;


        Object LEFT_SQUARE158_tree=null;
        Object RIGHT_SQUARE160_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:238:5: ( LEFT_SQUARE ( mapExpressionList )+ RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:238:7: LEFT_SQUARE ( mapExpressionList )+ RIGHT_SQUARE
            {
            root_0 = (Object)adaptor.nil();

            LEFT_SQUARE158=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_inlineMapExpression1737); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            LEFT_SQUARE158_tree = (Object)adaptor.create(LEFT_SQUARE158);
            adaptor.addChild(root_0, LEFT_SQUARE158_tree);
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:238:19: ( mapExpressionList )+
            int cnt49=0;
            loop49:
            do {
                int alt49=2;
                int LA49_0 = input.LA(1);

                if ( (LA49_0==FLOAT||(LA49_0>=HEX && LA49_0<=DECIMAL)||LA49_0==STRING||(LA49_0>=BOOL && LA49_0<=NULL)||(LA49_0>=DECR && LA49_0<=INCR)||LA49_0==LESS||LA49_0==LEFT_PAREN||LA49_0==LEFT_SQUARE||(LA49_0>=NEGATION && LA49_0<=TILDE)||(LA49_0>=MINUS && LA49_0<=PLUS)||LA49_0==ID) ) {
                    alt49=1;
                }


                switch (alt49) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:238:19: mapExpressionList
            	    {
            	    pushFollow(FOLLOW_mapExpressionList_in_inlineMapExpression1739);
            	    mapExpressionList159=mapExpressionList();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, mapExpressionList159.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt49 >= 1 ) break loop49;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(49, input);
                        throw eee;
                }
                cnt49++;
            } while (true);

            RIGHT_SQUARE160=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_inlineMapExpression1742); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RIGHT_SQUARE160_tree = (Object)adaptor.create(RIGHT_SQUARE160);
            adaptor.addChild(root_0, RIGHT_SQUARE160_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "inlineMapExpression"

    public static class mapExpressionList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "mapExpressionList"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:241:1: mapExpressionList : mapEntry ( COMMA mapEntry )* ;
    public final DRLExpressions.mapExpressionList_return mapExpressionList() throws RecognitionException {
        DRLExpressions.mapExpressionList_return retval = new DRLExpressions.mapExpressionList_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA162=null;
        DRLExpressions.mapEntry_return mapEntry161 = null;

        DRLExpressions.mapEntry_return mapEntry163 = null;


        Object COMMA162_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:242:5: ( mapEntry ( COMMA mapEntry )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:242:7: mapEntry ( COMMA mapEntry )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_mapEntry_in_mapExpressionList1759);
            mapEntry161=mapEntry();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, mapEntry161.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:242:16: ( COMMA mapEntry )*
            loop50:
            do {
                int alt50=2;
                int LA50_0 = input.LA(1);

                if ( (LA50_0==COMMA) ) {
                    alt50=1;
                }


                switch (alt50) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:242:17: COMMA mapEntry
            	    {
            	    COMMA162=(Token)match(input,COMMA,FOLLOW_COMMA_in_mapExpressionList1762); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    COMMA162_tree = (Object)adaptor.create(COMMA162);
            	    adaptor.addChild(root_0, COMMA162_tree);
            	    }
            	    pushFollow(FOLLOW_mapEntry_in_mapExpressionList1764);
            	    mapEntry163=mapEntry();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, mapEntry163.getTree());

            	    }
            	    break;

            	default :
            	    break loop50;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "mapExpressionList"

    public static class mapEntry_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "mapEntry"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:245:1: mapEntry : expression COLON expression ;
    public final DRLExpressions.mapEntry_return mapEntry() throws RecognitionException {
        DRLExpressions.mapEntry_return retval = new DRLExpressions.mapEntry_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON165=null;
        DRLExpressions.expression_return expression164 = null;

        DRLExpressions.expression_return expression166 = null;


        Object COLON165_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:246:5: ( expression COLON expression )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:246:7: expression COLON expression
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression_in_mapEntry1787);
            expression164=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression164.getTree());
            COLON165=(Token)match(input,COLON,FOLLOW_COLON_in_mapEntry1789); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            COLON165_tree = (Object)adaptor.create(COLON165);
            adaptor.addChild(root_0, COLON165_tree);
            }
            pushFollow(FOLLOW_expression_in_mapEntry1791);
            expression166=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression166.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "mapEntry"

    public static class parExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "parExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:249:1: parExpression : LEFT_PAREN expression RIGHT_PAREN ;
    public final DRLExpressions.parExpression_return parExpression() throws RecognitionException {
        DRLExpressions.parExpression_return retval = new DRLExpressions.parExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN167=null;
        Token RIGHT_PAREN169=null;
        DRLExpressions.expression_return expression168 = null;


        Object LEFT_PAREN167_tree=null;
        Object RIGHT_PAREN169_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:250:5: ( LEFT_PAREN expression RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:250:7: LEFT_PAREN expression RIGHT_PAREN
            {
            root_0 = (Object)adaptor.nil();

            LEFT_PAREN167=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_parExpression1808); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            LEFT_PAREN167_tree = (Object)adaptor.create(LEFT_PAREN167);
            adaptor.addChild(root_0, LEFT_PAREN167_tree);
            }
            pushFollow(FOLLOW_expression_in_parExpression1810);
            expression168=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression168.getTree());
            RIGHT_PAREN169=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_parExpression1812); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RIGHT_PAREN169_tree = (Object)adaptor.create(RIGHT_PAREN169);
            adaptor.addChild(root_0, RIGHT_PAREN169_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "parExpression"

    public static class identifierSuffix_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "identifierSuffix"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:253:1: identifierSuffix : ( ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments );
    public final DRLExpressions.identifierSuffix_return identifierSuffix() throws RecognitionException {
        DRLExpressions.identifierSuffix_return retval = new DRLExpressions.identifierSuffix_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE170=null;
        Token RIGHT_SQUARE171=null;
        Token DOT172=null;
        Token LEFT_SQUARE174=null;
        Token RIGHT_SQUARE176=null;
        DRLExpressions.class_key_return class_key173 = null;

        DRLExpressions.expression_return expression175 = null;

        DRLExpressions.arguments_return arguments177 = null;


        Object LEFT_SQUARE170_tree=null;
        Object RIGHT_SQUARE171_tree=null;
        Object DOT172_tree=null;
        Object LEFT_SQUARE174_tree=null;
        Object RIGHT_SQUARE176_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:254:5: ( ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments )
            int alt53=3;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==LEFT_SQUARE) ) {
                int LA53_1 = input.LA(2);

                if ( (LA53_1==RIGHT_SQUARE) && (synpred29_DRLExpressions())) {
                    alt53=1;
                }
                else if ( (LA53_1==FLOAT||(LA53_1>=HEX && LA53_1<=DECIMAL)||LA53_1==STRING||(LA53_1>=BOOL && LA53_1<=NULL)||(LA53_1>=DECR && LA53_1<=INCR)||LA53_1==LESS||LA53_1==LEFT_PAREN||LA53_1==LEFT_SQUARE||(LA53_1>=NEGATION && LA53_1<=TILDE)||(LA53_1>=MINUS && LA53_1<=PLUS)||LA53_1==ID) ) {
                    alt53=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 53, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA53_0==LEFT_PAREN) ) {
                alt53=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 53, 0, input);

                throw nvae;
            }
            switch (alt53) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:254:7: ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key
                    {
                    root_0 = (Object)adaptor.nil();

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:254:35: ( LEFT_SQUARE RIGHT_SQUARE )+
                    int cnt51=0;
                    loop51:
                    do {
                        int alt51=2;
                        int LA51_0 = input.LA(1);

                        if ( (LA51_0==LEFT_SQUARE) ) {
                            alt51=1;
                        }


                        switch (alt51) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:254:36: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    LEFT_SQUARE170=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_identifierSuffix1836); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    LEFT_SQUARE170_tree = (Object)adaptor.create(LEFT_SQUARE170);
                    	    adaptor.addChild(root_0, LEFT_SQUARE170_tree);
                    	    }
                    	    RIGHT_SQUARE171=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_identifierSuffix1838); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    RIGHT_SQUARE171_tree = (Object)adaptor.create(RIGHT_SQUARE171);
                    	    adaptor.addChild(root_0, RIGHT_SQUARE171_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt51 >= 1 ) break loop51;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(51, input);
                                throw eee;
                        }
                        cnt51++;
                    } while (true);

                    DOT172=(Token)match(input,DOT,FOLLOW_DOT_in_identifierSuffix1842); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DOT172_tree = (Object)adaptor.create(DOT172);
                    adaptor.addChild(root_0, DOT172_tree);
                    }
                    pushFollow(FOLLOW_class_key_in_identifierSuffix1844);
                    class_key173=class_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, class_key173.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:255:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
                    {
                    root_0 = (Object)adaptor.nil();

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:255:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
                    int cnt52=0;
                    loop52:
                    do {
                        int alt52=2;
                        alt52 = dfa52.predict(input);
                        switch (alt52) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:255:8: ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE
                    	    {
                    	    LEFT_SQUARE174=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_identifierSuffix1859); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    LEFT_SQUARE174_tree = (Object)adaptor.create(LEFT_SQUARE174);
                    	    adaptor.addChild(root_0, LEFT_SQUARE174_tree);
                    	    }
                    	    pushFollow(FOLLOW_expression_in_identifierSuffix1861);
                    	    expression175=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression175.getTree());
                    	    RIGHT_SQUARE176=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_identifierSuffix1863); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    RIGHT_SQUARE176_tree = (Object)adaptor.create(RIGHT_SQUARE176);
                    	    adaptor.addChild(root_0, RIGHT_SQUARE176_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt52 >= 1 ) break loop52;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(52, input);
                                throw eee;
                        }
                        cnt52++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:256:9: arguments
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_arguments_in_identifierSuffix1876);
                    arguments177=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments177.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "identifierSuffix"

    public static class creator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "creator"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:264:1: creator : ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) ;
    public final DRLExpressions.creator_return creator() throws RecognitionException {
        DRLExpressions.creator_return retval = new DRLExpressions.creator_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLExpressions.nonWildcardTypeArguments_return nonWildcardTypeArguments178 = null;

        DRLExpressions.createdName_return createdName179 = null;

        DRLExpressions.arrayCreatorRest_return arrayCreatorRest180 = null;

        DRLExpressions.classCreatorRest_return classCreatorRest181 = null;



        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:265:5: ( ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:265:7: ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest )
            {
            root_0 = (Object)adaptor.nil();

            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:265:7: ( nonWildcardTypeArguments )?
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==LESS) ) {
                alt54=1;
            }
            switch (alt54) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:265:7: nonWildcardTypeArguments
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator1899);
                    nonWildcardTypeArguments178=nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments178.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_createdName_in_creator1902);
            createdName179=createdName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, createdName179.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:266:9: ( arrayCreatorRest | classCreatorRest )
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==LEFT_SQUARE) ) {
                alt55=1;
            }
            else if ( (LA55_0==LEFT_PAREN) ) {
                alt55=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 55, 0, input);

                throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:266:10: arrayCreatorRest
                    {
                    pushFollow(FOLLOW_arrayCreatorRest_in_creator1913);
                    arrayCreatorRest180=arrayCreatorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arrayCreatorRest180.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:266:29: classCreatorRest
                    {
                    pushFollow(FOLLOW_classCreatorRest_in_creator1917);
                    classCreatorRest181=classCreatorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classCreatorRest181.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "creator"

    public static class createdName_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "createdName"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:269:1: createdName : ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType );
    public final DRLExpressions.createdName_return createdName() throws RecognitionException {
        DRLExpressions.createdName_return retval = new DRLExpressions.createdName_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID182=null;
        Token DOT184=null;
        Token ID185=null;
        DRLExpressions.typeArguments_return typeArguments183 = null;

        DRLExpressions.typeArguments_return typeArguments186 = null;

        DRLExpressions.primitiveType_return primitiveType187 = null;


        Object ID182_tree=null;
        Object DOT184_tree=null;
        Object ID185_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:270:5: ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType )
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==ID) && ((!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))))) {
                int LA59_1 = input.LA(2);

                if ( (!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))) ) {
                    alt59=1;
                }
                else if ( ((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))) ) {
                    alt59=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 59, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 59, 0, input);

                throw nvae;
            }
            switch (alt59) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:270:7: ID ( typeArguments )? ( DOT ID ( typeArguments )? )*
                    {
                    root_0 = (Object)adaptor.nil();

                    ID182=(Token)match(input,ID,FOLLOW_ID_in_createdName1935); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID182_tree = (Object)adaptor.create(ID182);
                    adaptor.addChild(root_0, ID182_tree);
                    }
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:270:10: ( typeArguments )?
                    int alt56=2;
                    int LA56_0 = input.LA(1);

                    if ( (LA56_0==LESS) ) {
                        alt56=1;
                    }
                    switch (alt56) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:270:10: typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_createdName1937);
                            typeArguments183=typeArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArguments183.getTree());

                            }
                            break;

                    }

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:271:9: ( DOT ID ( typeArguments )? )*
                    loop58:
                    do {
                        int alt58=2;
                        int LA58_0 = input.LA(1);

                        if ( (LA58_0==DOT) ) {
                            alt58=1;
                        }


                        switch (alt58) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:271:11: DOT ID ( typeArguments )?
                    	    {
                    	    DOT184=(Token)match(input,DOT,FOLLOW_DOT_in_createdName1950); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    DOT184_tree = (Object)adaptor.create(DOT184);
                    	    adaptor.addChild(root_0, DOT184_tree);
                    	    }
                    	    ID185=(Token)match(input,ID,FOLLOW_ID_in_createdName1952); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    ID185_tree = (Object)adaptor.create(ID185);
                    	    adaptor.addChild(root_0, ID185_tree);
                    	    }
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:271:18: ( typeArguments )?
                    	    int alt57=2;
                    	    int LA57_0 = input.LA(1);

                    	    if ( (LA57_0==LESS) ) {
                    	        alt57=1;
                    	    }
                    	    switch (alt57) {
                    	        case 1 :
                    	            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:271:18: typeArguments
                    	            {
                    	            pushFollow(FOLLOW_typeArguments_in_createdName1954);
                    	            typeArguments186=typeArguments();

                    	            state._fsp--;
                    	            if (state.failed) return retval;
                    	            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArguments186.getTree());

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop58;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:272:11: primitiveType
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primitiveType_in_createdName1969);
                    primitiveType187=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType187.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "createdName"

    public static class innerCreator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "innerCreator"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:275:1: innerCreator : {...}? => ID classCreatorRest ;
    public final DRLExpressions.innerCreator_return innerCreator() throws RecognitionException {
        DRLExpressions.innerCreator_return retval = new DRLExpressions.innerCreator_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID188=null;
        DRLExpressions.classCreatorRest_return classCreatorRest189 = null;


        Object ID188_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:276:5: ({...}? => ID classCreatorRest )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:276:7: {...}? => ID classCreatorRest
            {
            root_0 = (Object)adaptor.nil();

            if ( !((!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "innerCreator", "!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
            }
            ID188=(Token)match(input,ID,FOLLOW_ID_in_innerCreator1989); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID188_tree = (Object)adaptor.create(ID188);
            adaptor.addChild(root_0, ID188_tree);
            }
            pushFollow(FOLLOW_classCreatorRest_in_innerCreator1991);
            classCreatorRest189=classCreatorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, classCreatorRest189.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "innerCreator"

    public static class arrayCreatorRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "arrayCreatorRest"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:279:1: arrayCreatorRest : LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) ;
    public final DRLExpressions.arrayCreatorRest_return arrayCreatorRest() throws RecognitionException {
        DRLExpressions.arrayCreatorRest_return retval = new DRLExpressions.arrayCreatorRest_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE190=null;
        Token RIGHT_SQUARE191=null;
        Token LEFT_SQUARE192=null;
        Token RIGHT_SQUARE193=null;
        Token RIGHT_SQUARE196=null;
        Token LEFT_SQUARE197=null;
        Token RIGHT_SQUARE199=null;
        Token LEFT_SQUARE200=null;
        Token RIGHT_SQUARE201=null;
        DRLExpressions.arrayInitializer_return arrayInitializer194 = null;

        DRLExpressions.expression_return expression195 = null;

        DRLExpressions.expression_return expression198 = null;


        Object LEFT_SQUARE190_tree=null;
        Object RIGHT_SQUARE191_tree=null;
        Object LEFT_SQUARE192_tree=null;
        Object RIGHT_SQUARE193_tree=null;
        Object RIGHT_SQUARE196_tree=null;
        Object LEFT_SQUARE197_tree=null;
        Object RIGHT_SQUARE199_tree=null;
        Object LEFT_SQUARE200_tree=null;
        Object RIGHT_SQUARE201_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:280:5: ( LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:280:9: LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
            {
            root_0 = (Object)adaptor.nil();

            LEFT_SQUARE190=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2010); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            LEFT_SQUARE190_tree = (Object)adaptor.create(LEFT_SQUARE190);
            adaptor.addChild(root_0, LEFT_SQUARE190_tree);
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:281:5: ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
            int alt63=2;
            int LA63_0 = input.LA(1);

            if ( (LA63_0==RIGHT_SQUARE) ) {
                alt63=1;
            }
            else if ( (LA63_0==FLOAT||(LA63_0>=HEX && LA63_0<=DECIMAL)||LA63_0==STRING||(LA63_0>=BOOL && LA63_0<=NULL)||(LA63_0>=DECR && LA63_0<=INCR)||LA63_0==LESS||LA63_0==LEFT_PAREN||LA63_0==LEFT_SQUARE||(LA63_0>=NEGATION && LA63_0<=TILDE)||(LA63_0>=MINUS && LA63_0<=PLUS)||LA63_0==ID) ) {
                alt63=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 63, 0, input);

                throw nvae;
            }
            switch (alt63) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:281:9: RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer
                    {
                    RIGHT_SQUARE191=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2020); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RIGHT_SQUARE191_tree = (Object)adaptor.create(RIGHT_SQUARE191);
                    adaptor.addChild(root_0, RIGHT_SQUARE191_tree);
                    }
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:281:22: ( LEFT_SQUARE RIGHT_SQUARE )*
                    loop60:
                    do {
                        int alt60=2;
                        int LA60_0 = input.LA(1);

                        if ( (LA60_0==LEFT_SQUARE) ) {
                            alt60=1;
                        }


                        switch (alt60) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:281:23: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    LEFT_SQUARE192=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2023); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    LEFT_SQUARE192_tree = (Object)adaptor.create(LEFT_SQUARE192);
                    	    adaptor.addChild(root_0, LEFT_SQUARE192_tree);
                    	    }
                    	    RIGHT_SQUARE193=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2025); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    RIGHT_SQUARE193_tree = (Object)adaptor.create(RIGHT_SQUARE193);
                    	    adaptor.addChild(root_0, RIGHT_SQUARE193_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop60;
                        }
                    } while (true);

                    pushFollow(FOLLOW_arrayInitializer_in_arrayCreatorRest2029);
                    arrayInitializer194=arrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arrayInitializer194.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:282:13: expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                    pushFollow(FOLLOW_expression_in_arrayCreatorRest2043);
                    expression195=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression195.getTree());
                    RIGHT_SQUARE196=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2045); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RIGHT_SQUARE196_tree = (Object)adaptor.create(RIGHT_SQUARE196);
                    adaptor.addChild(root_0, RIGHT_SQUARE196_tree);
                    }
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:282:37: ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )*
                    loop61:
                    do {
                        int alt61=2;
                        alt61 = dfa61.predict(input);
                        switch (alt61) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:282:38: {...}? => LEFT_SQUARE expression RIGHT_SQUARE
                    	    {
                    	    if ( !((!helper.validateLT(2,"]"))) ) {
                    	        if (state.backtracking>0) {state.failed=true; return retval;}
                    	        throw new FailedPredicateException(input, "arrayCreatorRest", "!helper.validateLT(2,\"]\")");
                    	    }
                    	    LEFT_SQUARE197=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2050); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    LEFT_SQUARE197_tree = (Object)adaptor.create(LEFT_SQUARE197);
                    	    adaptor.addChild(root_0, LEFT_SQUARE197_tree);
                    	    }
                    	    pushFollow(FOLLOW_expression_in_arrayCreatorRest2052);
                    	    expression198=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression198.getTree());
                    	    RIGHT_SQUARE199=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2054); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    RIGHT_SQUARE199_tree = (Object)adaptor.create(RIGHT_SQUARE199);
                    	    adaptor.addChild(root_0, RIGHT_SQUARE199_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop61;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:282:106: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    loop62:
                    do {
                        int alt62=2;
                        int LA62_0 = input.LA(1);

                        if ( (LA62_0==LEFT_SQUARE) ) {
                            int LA62_2 = input.LA(2);

                            if ( (LA62_2==RIGHT_SQUARE) ) {
                                int LA62_3 = input.LA(3);

                                if ( (synpred31_DRLExpressions()) ) {
                                    alt62=1;
                                }


                            }


                        }


                        switch (alt62) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:282:107: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    LEFT_SQUARE200=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2066); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    LEFT_SQUARE200_tree = (Object)adaptor.create(LEFT_SQUARE200);
                    	    adaptor.addChild(root_0, LEFT_SQUARE200_tree);
                    	    }
                    	    RIGHT_SQUARE201=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2068); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    RIGHT_SQUARE201_tree = (Object)adaptor.create(RIGHT_SQUARE201);
                    	    adaptor.addChild(root_0, RIGHT_SQUARE201_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop62;
                        }
                    } while (true);


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "arrayCreatorRest"

    public static class variableInitializer_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "variableInitializer"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:286:1: variableInitializer : ( arrayInitializer | expression );
    public final DRLExpressions.variableInitializer_return variableInitializer() throws RecognitionException {
        DRLExpressions.variableInitializer_return retval = new DRLExpressions.variableInitializer_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLExpressions.arrayInitializer_return arrayInitializer202 = null;

        DRLExpressions.expression_return expression203 = null;



        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:287:5: ( arrayInitializer | expression )
            int alt64=2;
            int LA64_0 = input.LA(1);

            if ( (LA64_0==LEFT_CURLY) ) {
                alt64=1;
            }
            else if ( (LA64_0==FLOAT||(LA64_0>=HEX && LA64_0<=DECIMAL)||LA64_0==STRING||(LA64_0>=BOOL && LA64_0<=NULL)||(LA64_0>=DECR && LA64_0<=INCR)||LA64_0==LESS||LA64_0==LEFT_PAREN||LA64_0==LEFT_SQUARE||(LA64_0>=NEGATION && LA64_0<=TILDE)||(LA64_0>=MINUS && LA64_0<=PLUS)||LA64_0==ID) ) {
                alt64=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 64, 0, input);

                throw nvae;
            }
            switch (alt64) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:287:7: arrayInitializer
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_arrayInitializer_in_variableInitializer2097);
                    arrayInitializer202=arrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arrayInitializer202.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:288:13: expression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expression_in_variableInitializer2111);
                    expression203=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression203.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "variableInitializer"

    public static class arrayInitializer_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "arrayInitializer"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:291:1: arrayInitializer : LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY ;
    public final DRLExpressions.arrayInitializer_return arrayInitializer() throws RecognitionException {
        DRLExpressions.arrayInitializer_return retval = new DRLExpressions.arrayInitializer_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_CURLY204=null;
        Token COMMA206=null;
        Token COMMA208=null;
        Token RIGHT_CURLY209=null;
        DRLExpressions.variableInitializer_return variableInitializer205 = null;

        DRLExpressions.variableInitializer_return variableInitializer207 = null;


        Object LEFT_CURLY204_tree=null;
        Object COMMA206_tree=null;
        Object COMMA208_tree=null;
        Object RIGHT_CURLY209_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:292:5: ( LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:292:7: LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY
            {
            root_0 = (Object)adaptor.nil();

            LEFT_CURLY204=(Token)match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_arrayInitializer2128); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            LEFT_CURLY204_tree = (Object)adaptor.create(LEFT_CURLY204);
            adaptor.addChild(root_0, LEFT_CURLY204_tree);
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:292:18: ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )?
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==FLOAT||(LA67_0>=HEX && LA67_0<=DECIMAL)||LA67_0==STRING||(LA67_0>=BOOL && LA67_0<=NULL)||(LA67_0>=DECR && LA67_0<=INCR)||LA67_0==LESS||LA67_0==LEFT_PAREN||LA67_0==LEFT_SQUARE||LA67_0==LEFT_CURLY||(LA67_0>=NEGATION && LA67_0<=TILDE)||(LA67_0>=MINUS && LA67_0<=PLUS)||LA67_0==ID) ) {
                alt67=1;
            }
            switch (alt67) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:292:19: variableInitializer ( COMMA variableInitializer )* ( COMMA )?
                    {
                    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer2131);
                    variableInitializer205=variableInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableInitializer205.getTree());
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:292:39: ( COMMA variableInitializer )*
                    loop65:
                    do {
                        int alt65=2;
                        int LA65_0 = input.LA(1);

                        if ( (LA65_0==COMMA) ) {
                            int LA65_1 = input.LA(2);

                            if ( (LA65_1==FLOAT||(LA65_1>=HEX && LA65_1<=DECIMAL)||LA65_1==STRING||(LA65_1>=BOOL && LA65_1<=NULL)||(LA65_1>=DECR && LA65_1<=INCR)||LA65_1==LESS||LA65_1==LEFT_PAREN||LA65_1==LEFT_SQUARE||LA65_1==LEFT_CURLY||(LA65_1>=NEGATION && LA65_1<=TILDE)||(LA65_1>=MINUS && LA65_1<=PLUS)||LA65_1==ID) ) {
                                alt65=1;
                            }


                        }


                        switch (alt65) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:292:40: COMMA variableInitializer
                    	    {
                    	    COMMA206=(Token)match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer2134); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    COMMA206_tree = (Object)adaptor.create(COMMA206);
                    	    adaptor.addChild(root_0, COMMA206_tree);
                    	    }
                    	    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer2136);
                    	    variableInitializer207=variableInitializer();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableInitializer207.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop65;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:292:68: ( COMMA )?
                    int alt66=2;
                    int LA66_0 = input.LA(1);

                    if ( (LA66_0==COMMA) ) {
                        alt66=1;
                    }
                    switch (alt66) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:292:69: COMMA
                            {
                            COMMA208=(Token)match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer2141); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            COMMA208_tree = (Object)adaptor.create(COMMA208);
                            adaptor.addChild(root_0, COMMA208_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;

            }

            RIGHT_CURLY209=(Token)match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_arrayInitializer2148); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RIGHT_CURLY209_tree = (Object)adaptor.create(RIGHT_CURLY209);
            adaptor.addChild(root_0, RIGHT_CURLY209_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "arrayInitializer"

    public static class classCreatorRest_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "classCreatorRest"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:295:1: classCreatorRest : arguments ;
    public final DRLExpressions.classCreatorRest_return classCreatorRest() throws RecognitionException {
        DRLExpressions.classCreatorRest_return retval = new DRLExpressions.classCreatorRest_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLExpressions.arguments_return arguments210 = null;



        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:296:5: ( arguments )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:296:7: arguments
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_arguments_in_classCreatorRest2165);
            arguments210=arguments();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments210.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "classCreatorRest"

    public static class explicitGenericInvocation_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "explicitGenericInvocation"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:299:1: explicitGenericInvocation : nonWildcardTypeArguments arguments ;
    public final DRLExpressions.explicitGenericInvocation_return explicitGenericInvocation() throws RecognitionException {
        DRLExpressions.explicitGenericInvocation_return retval = new DRLExpressions.explicitGenericInvocation_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLExpressions.nonWildcardTypeArguments_return nonWildcardTypeArguments211 = null;

        DRLExpressions.arguments_return arguments212 = null;



        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:300:5: ( nonWildcardTypeArguments arguments )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:300:7: nonWildcardTypeArguments arguments
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation2183);
            nonWildcardTypeArguments211=nonWildcardTypeArguments();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments211.getTree());
            pushFollow(FOLLOW_arguments_in_explicitGenericInvocation2185);
            arguments212=arguments();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments212.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "explicitGenericInvocation"

    public static class nonWildcardTypeArguments_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "nonWildcardTypeArguments"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:303:1: nonWildcardTypeArguments : LESS typeList GREATER ;
    public final DRLExpressions.nonWildcardTypeArguments_return nonWildcardTypeArguments() throws RecognitionException {
        DRLExpressions.nonWildcardTypeArguments_return retval = new DRLExpressions.nonWildcardTypeArguments_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LESS213=null;
        Token GREATER215=null;
        DRLExpressions.typeList_return typeList214 = null;


        Object LESS213_tree=null;
        Object GREATER215_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:304:5: ( LESS typeList GREATER )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:304:7: LESS typeList GREATER
            {
            root_0 = (Object)adaptor.nil();

            LESS213=(Token)match(input,LESS,FOLLOW_LESS_in_nonWildcardTypeArguments2202); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            LESS213_tree = (Object)adaptor.create(LESS213);
            adaptor.addChild(root_0, LESS213_tree);
            }
            pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments2204);
            typeList214=typeList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeList214.getTree());
            GREATER215=(Token)match(input,GREATER,FOLLOW_GREATER_in_nonWildcardTypeArguments2206); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            GREATER215_tree = (Object)adaptor.create(GREATER215);
            adaptor.addChild(root_0, GREATER215_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "nonWildcardTypeArguments"

    public static class explicitGenericInvocationSuffix_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "explicitGenericInvocationSuffix"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:307:1: explicitGenericInvocationSuffix : ( super_key superSuffix | ID arguments );
    public final DRLExpressions.explicitGenericInvocationSuffix_return explicitGenericInvocationSuffix() throws RecognitionException {
        DRLExpressions.explicitGenericInvocationSuffix_return retval = new DRLExpressions.explicitGenericInvocationSuffix_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID218=null;
        DRLExpressions.super_key_return super_key216 = null;

        DRLExpressions.superSuffix_return superSuffix217 = null;

        DRLExpressions.arguments_return arguments219 = null;


        Object ID218_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:308:5: ( super_key superSuffix | ID arguments )
            int alt68=2;
            int LA68_0 = input.LA(1);

            if ( (LA68_0==ID) ) {
                int LA68_1 = input.LA(2);

                if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
                    alt68=1;
                }
                else if ( (true) ) {
                    alt68=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 68, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 68, 0, input);

                throw nvae;
            }
            switch (alt68) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:308:7: super_key superSuffix
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_super_key_in_explicitGenericInvocationSuffix2223);
                    super_key216=super_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, super_key216.getTree());
                    pushFollow(FOLLOW_superSuffix_in_explicitGenericInvocationSuffix2225);
                    superSuffix217=superSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, superSuffix217.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:309:10: ID arguments
                    {
                    root_0 = (Object)adaptor.nil();

                    ID218=(Token)match(input,ID,FOLLOW_ID_in_explicitGenericInvocationSuffix2236); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID218_tree = (Object)adaptor.create(ID218);
                    adaptor.addChild(root_0, ID218_tree);
                    }
                    pushFollow(FOLLOW_arguments_in_explicitGenericInvocationSuffix2238);
                    arguments219=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments219.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "explicitGenericInvocationSuffix"

    public static class selector_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "selector"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:1: selector : ( ( DOT super_key )=> DOT super_key superSuffix | ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator | ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )? | ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE );
    public final DRLExpressions.selector_return selector() throws RecognitionException {
        DRLExpressions.selector_return retval = new DRLExpressions.selector_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOT220=null;
        Token DOT223=null;
        Token DOT227=null;
        Token ID228=null;
        Token LEFT_SQUARE230=null;
        Token RIGHT_SQUARE232=null;
        DRLExpressions.super_key_return super_key221 = null;

        DRLExpressions.superSuffix_return superSuffix222 = null;

        DRLExpressions.new_key_return new_key224 = null;

        DRLExpressions.nonWildcardTypeArguments_return nonWildcardTypeArguments225 = null;

        DRLExpressions.innerCreator_return innerCreator226 = null;

        DRLExpressions.arguments_return arguments229 = null;

        DRLExpressions.expression_return expression231 = null;


        Object DOT220_tree=null;
        Object DOT223_tree=null;
        Object DOT227_tree=null;
        Object ID228_tree=null;
        Object LEFT_SQUARE230_tree=null;
        Object RIGHT_SQUARE232_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:313:5: ( ( DOT super_key )=> DOT super_key superSuffix | ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator | ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )? | ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )
            int alt71=4;
            int LA71_0 = input.LA(1);

            if ( (LA71_0==DOT) ) {
                int LA71_1 = input.LA(2);

                if ( (synpred32_DRLExpressions()) ) {
                    alt71=1;
                }
                else if ( (synpred33_DRLExpressions()) ) {
                    alt71=2;
                }
                else if ( (synpred34_DRLExpressions()) ) {
                    alt71=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 71, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA71_0==LEFT_SQUARE) && (synpred36_DRLExpressions())) {
                alt71=4;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 71, 0, input);

                throw nvae;
            }
            switch (alt71) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:313:9: ( DOT super_key )=> DOT super_key superSuffix
                    {
                    root_0 = (Object)adaptor.nil();

                    DOT220=(Token)match(input,DOT,FOLLOW_DOT_in_selector2263); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DOT220_tree = (Object)adaptor.create(DOT220);
                    adaptor.addChild(root_0, DOT220_tree);
                    }
                    pushFollow(FOLLOW_super_key_in_selector2265);
                    super_key221=super_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, super_key221.getTree());
                    pushFollow(FOLLOW_superSuffix_in_selector2267);
                    superSuffix222=superSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, superSuffix222.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:314:9: ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator
                    {
                    root_0 = (Object)adaptor.nil();

                    DOT223=(Token)match(input,DOT,FOLLOW_DOT_in_selector2283); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DOT223_tree = (Object)adaptor.create(DOT223);
                    adaptor.addChild(root_0, DOT223_tree);
                    }
                    pushFollow(FOLLOW_new_key_in_selector2285);
                    new_key224=new_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, new_key224.getTree());
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:314:36: ( nonWildcardTypeArguments )?
                    int alt69=2;
                    int LA69_0 = input.LA(1);

                    if ( (LA69_0==LESS) ) {
                        alt69=1;
                    }
                    switch (alt69) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:314:37: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_selector2288);
                            nonWildcardTypeArguments225=nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments225.getTree());

                            }
                            break;

                    }

                    pushFollow(FOLLOW_innerCreator_in_selector2292);
                    innerCreator226=innerCreator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, innerCreator226.getTree());

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:315:9: ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )?
                    {
                    root_0 = (Object)adaptor.nil();

                    DOT227=(Token)match(input,DOT,FOLLOW_DOT_in_selector2308); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DOT227_tree = (Object)adaptor.create(DOT227);
                    adaptor.addChild(root_0, DOT227_tree);
                    }
                    ID228=(Token)match(input,ID,FOLLOW_ID_in_selector2310); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID228_tree = (Object)adaptor.create(ID228);
                    adaptor.addChild(root_0, ID228_tree);
                    }
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:315:26: ( ( LEFT_PAREN )=> arguments )?
                    int alt70=2;
                    alt70 = dfa70.predict(input);
                    switch (alt70) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:315:27: ( LEFT_PAREN )=> arguments
                            {
                            pushFollow(FOLLOW_arguments_in_selector2319);
                            arguments229=arguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments229.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:317:9: ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE
                    {
                    root_0 = (Object)adaptor.nil();

                    LEFT_SQUARE230=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_selector2340); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LEFT_SQUARE230_tree = (Object)adaptor.create(LEFT_SQUARE230);
                    adaptor.addChild(root_0, LEFT_SQUARE230_tree);
                    }
                    pushFollow(FOLLOW_expression_in_selector2342);
                    expression231=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression231.getTree());
                    RIGHT_SQUARE232=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_selector2344); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RIGHT_SQUARE232_tree = (Object)adaptor.create(RIGHT_SQUARE232);
                    adaptor.addChild(root_0, RIGHT_SQUARE232_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "selector"

    public static class superSuffix_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "superSuffix"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:320:1: superSuffix : ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? );
    public final DRLExpressions.superSuffix_return superSuffix() throws RecognitionException {
        DRLExpressions.superSuffix_return retval = new DRLExpressions.superSuffix_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOT234=null;
        Token ID235=null;
        DRLExpressions.arguments_return arguments233 = null;

        DRLExpressions.arguments_return arguments236 = null;


        Object DOT234_tree=null;
        Object ID235_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:321:5: ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? )
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==LEFT_PAREN) ) {
                alt73=1;
            }
            else if ( (LA73_0==DOT) ) {
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
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:321:7: arguments
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_arguments_in_superSuffix2361);
                    arguments233=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments233.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:322:10: DOT ID ( ( LEFT_PAREN )=> arguments )?
                    {
                    root_0 = (Object)adaptor.nil();

                    DOT234=(Token)match(input,DOT,FOLLOW_DOT_in_superSuffix2372); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DOT234_tree = (Object)adaptor.create(DOT234);
                    adaptor.addChild(root_0, DOT234_tree);
                    }
                    ID235=(Token)match(input,ID,FOLLOW_ID_in_superSuffix2374); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID235_tree = (Object)adaptor.create(ID235);
                    adaptor.addChild(root_0, ID235_tree);
                    }
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:322:17: ( ( LEFT_PAREN )=> arguments )?
                    int alt72=2;
                    alt72 = dfa72.predict(input);
                    switch (alt72) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:322:18: ( LEFT_PAREN )=> arguments
                            {
                            pushFollow(FOLLOW_arguments_in_superSuffix2383);
                            arguments236=arguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments236.getTree());

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "superSuffix"

    public static class squareArguments_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "squareArguments"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:325:1: squareArguments : LEFT_SQUARE ( expressionList )? RIGHT_SQUARE ;
    public final DRLExpressions.squareArguments_return squareArguments() throws RecognitionException {
        DRLExpressions.squareArguments_return retval = new DRLExpressions.squareArguments_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE237=null;
        Token RIGHT_SQUARE239=null;
        DRLExpressions.expressionList_return expressionList238 = null;


        Object LEFT_SQUARE237_tree=null;
        Object RIGHT_SQUARE239_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:326:5: ( LEFT_SQUARE ( expressionList )? RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:326:7: LEFT_SQUARE ( expressionList )? RIGHT_SQUARE
            {
            root_0 = (Object)adaptor.nil();

            LEFT_SQUARE237=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_squareArguments2406); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            LEFT_SQUARE237_tree = (Object)adaptor.create(LEFT_SQUARE237);
            adaptor.addChild(root_0, LEFT_SQUARE237_tree);
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:326:19: ( expressionList )?
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( (LA74_0==FLOAT||(LA74_0>=HEX && LA74_0<=DECIMAL)||LA74_0==STRING||(LA74_0>=BOOL && LA74_0<=NULL)||(LA74_0>=DECR && LA74_0<=INCR)||LA74_0==LESS||LA74_0==LEFT_PAREN||LA74_0==LEFT_SQUARE||(LA74_0>=NEGATION && LA74_0<=TILDE)||(LA74_0>=MINUS && LA74_0<=PLUS)||LA74_0==ID) ) {
                alt74=1;
            }
            switch (alt74) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:326:19: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_squareArguments2408);
                    expressionList238=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList238.getTree());

                    }
                    break;

            }

            RIGHT_SQUARE239=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_squareArguments2411); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RIGHT_SQUARE239_tree = (Object)adaptor.create(RIGHT_SQUARE239);
            adaptor.addChild(root_0, RIGHT_SQUARE239_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "squareArguments"

    public static class arguments_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "arguments"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:329:1: arguments : LEFT_PAREN ( expressionList )? RIGHT_PAREN ;
    public final DRLExpressions.arguments_return arguments() throws RecognitionException {
        DRLExpressions.arguments_return retval = new DRLExpressions.arguments_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN240=null;
        Token RIGHT_PAREN242=null;
        DRLExpressions.expressionList_return expressionList241 = null;


        Object LEFT_PAREN240_tree=null;
        Object RIGHT_PAREN242_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:330:5: ( LEFT_PAREN ( expressionList )? RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:330:7: LEFT_PAREN ( expressionList )? RIGHT_PAREN
            {
            root_0 = (Object)adaptor.nil();

            LEFT_PAREN240=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_arguments2428); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            LEFT_PAREN240_tree = (Object)adaptor.create(LEFT_PAREN240);
            adaptor.addChild(root_0, LEFT_PAREN240_tree);
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:330:18: ( expressionList )?
            int alt75=2;
            int LA75_0 = input.LA(1);

            if ( (LA75_0==FLOAT||(LA75_0>=HEX && LA75_0<=DECIMAL)||LA75_0==STRING||(LA75_0>=BOOL && LA75_0<=NULL)||(LA75_0>=DECR && LA75_0<=INCR)||LA75_0==LESS||LA75_0==LEFT_PAREN||LA75_0==LEFT_SQUARE||(LA75_0>=NEGATION && LA75_0<=TILDE)||(LA75_0>=MINUS && LA75_0<=PLUS)||LA75_0==ID) ) {
                alt75=1;
            }
            switch (alt75) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:330:18: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments2430);
                    expressionList241=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList241.getTree());

                    }
                    break;

            }

            RIGHT_PAREN242=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_arguments2433); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RIGHT_PAREN242_tree = (Object)adaptor.create(RIGHT_PAREN242);
            adaptor.addChild(root_0, RIGHT_PAREN242_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "arguments"

    public static class expressionList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expressionList"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:333:1: expressionList : expression ( COMMA expression )* ;
    public final DRLExpressions.expressionList_return expressionList() throws RecognitionException {
        DRLExpressions.expressionList_return retval = new DRLExpressions.expressionList_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA244=null;
        DRLExpressions.expression_return expression243 = null;

        DRLExpressions.expression_return expression245 = null;


        Object COMMA244_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:334:3: ( expression ( COMMA expression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:334:7: expression ( COMMA expression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression_in_expressionList2450);
            expression243=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression243.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:334:18: ( COMMA expression )*
            loop76:
            do {
                int alt76=2;
                int LA76_0 = input.LA(1);

                if ( (LA76_0==COMMA) ) {
                    alt76=1;
                }


                switch (alt76) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:334:19: COMMA expression
            	    {
            	    COMMA244=(Token)match(input,COMMA,FOLLOW_COMMA_in_expressionList2453); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    COMMA244_tree = (Object)adaptor.create(COMMA244);
            	    adaptor.addChild(root_0, COMMA244_tree);
            	    }
            	    pushFollow(FOLLOW_expression_in_expressionList2455);
            	    expression245=expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression245.getTree());

            	    }
            	    break;

            	default :
            	    break loop76;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "expressionList"

    public static class assignmentOperator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "assignmentOperator"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:337:1: assignmentOperator : ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN -> SHL_ASSIGN[\"<<=\"] | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN -> SHRB_ASSIGN[\">>>=\"] | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN -> SHR_ASSIGN[\">>=\"] );
    public final DRLExpressions.assignmentOperator_return assignmentOperator() throws RecognitionException {
        DRLExpressions.assignmentOperator_return retval = new DRLExpressions.assignmentOperator_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS_ASSIGN246=null;
        Token PLUS_ASSIGN247=null;
        Token MINUS_ASSIGN248=null;
        Token MULT_ASSIGN249=null;
        Token DIV_ASSIGN250=null;
        Token AND_ASSIGN251=null;
        Token OR_ASSIGN252=null;
        Token XOR_ASSIGN253=null;
        Token MOD_ASSIGN254=null;
        Token LESS255=null;
        Token LESS256=null;
        Token EQUALS_ASSIGN257=null;
        Token GREATER258=null;
        Token GREATER259=null;
        Token GREATER260=null;
        Token EQUALS_ASSIGN261=null;
        Token GREATER262=null;
        Token GREATER263=null;
        Token EQUALS_ASSIGN264=null;

        Object EQUALS_ASSIGN246_tree=null;
        Object PLUS_ASSIGN247_tree=null;
        Object MINUS_ASSIGN248_tree=null;
        Object MULT_ASSIGN249_tree=null;
        Object DIV_ASSIGN250_tree=null;
        Object AND_ASSIGN251_tree=null;
        Object OR_ASSIGN252_tree=null;
        Object XOR_ASSIGN253_tree=null;
        Object MOD_ASSIGN254_tree=null;
        Object LESS255_tree=null;
        Object LESS256_tree=null;
        Object EQUALS_ASSIGN257_tree=null;
        Object GREATER258_tree=null;
        Object GREATER259_tree=null;
        Object GREATER260_tree=null;
        Object EQUALS_ASSIGN261_tree=null;
        Object GREATER262_tree=null;
        Object GREATER263_tree=null;
        Object EQUALS_ASSIGN264_tree=null;
        RewriteRuleTokenStream stream_GREATER=new RewriteRuleTokenStream(adaptor,"token GREATER");
        RewriteRuleTokenStream stream_EQUALS_ASSIGN=new RewriteRuleTokenStream(adaptor,"token EQUALS_ASSIGN");
        RewriteRuleTokenStream stream_LESS=new RewriteRuleTokenStream(adaptor,"token LESS");

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:338:5: ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN -> SHL_ASSIGN[\"<<=\"] | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN -> SHRB_ASSIGN[\">>>=\"] | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN -> SHR_ASSIGN[\">>=\"] )
            int alt77=12;
            alt77 = dfa77.predict(input);
            switch (alt77) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:338:9: EQUALS_ASSIGN
                    {
                    root_0 = (Object)adaptor.nil();

                    EQUALS_ASSIGN246=(Token)match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2474); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQUALS_ASSIGN246_tree = (Object)adaptor.create(EQUALS_ASSIGN246);
                    adaptor.addChild(root_0, EQUALS_ASSIGN246_tree);
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:339:7: PLUS_ASSIGN
                    {
                    root_0 = (Object)adaptor.nil();

                    PLUS_ASSIGN247=(Token)match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_assignmentOperator2482); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    PLUS_ASSIGN247_tree = (Object)adaptor.create(PLUS_ASSIGN247);
                    adaptor.addChild(root_0, PLUS_ASSIGN247_tree);
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:340:7: MINUS_ASSIGN
                    {
                    root_0 = (Object)adaptor.nil();

                    MINUS_ASSIGN248=(Token)match(input,MINUS_ASSIGN,FOLLOW_MINUS_ASSIGN_in_assignmentOperator2490); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MINUS_ASSIGN248_tree = (Object)adaptor.create(MINUS_ASSIGN248);
                    adaptor.addChild(root_0, MINUS_ASSIGN248_tree);
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:341:7: MULT_ASSIGN
                    {
                    root_0 = (Object)adaptor.nil();

                    MULT_ASSIGN249=(Token)match(input,MULT_ASSIGN,FOLLOW_MULT_ASSIGN_in_assignmentOperator2498); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MULT_ASSIGN249_tree = (Object)adaptor.create(MULT_ASSIGN249);
                    adaptor.addChild(root_0, MULT_ASSIGN249_tree);
                    }

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:342:7: DIV_ASSIGN
                    {
                    root_0 = (Object)adaptor.nil();

                    DIV_ASSIGN250=(Token)match(input,DIV_ASSIGN,FOLLOW_DIV_ASSIGN_in_assignmentOperator2506); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DIV_ASSIGN250_tree = (Object)adaptor.create(DIV_ASSIGN250);
                    adaptor.addChild(root_0, DIV_ASSIGN250_tree);
                    }

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:343:7: AND_ASSIGN
                    {
                    root_0 = (Object)adaptor.nil();

                    AND_ASSIGN251=(Token)match(input,AND_ASSIGN,FOLLOW_AND_ASSIGN_in_assignmentOperator2514); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    AND_ASSIGN251_tree = (Object)adaptor.create(AND_ASSIGN251);
                    adaptor.addChild(root_0, AND_ASSIGN251_tree);
                    }

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:344:7: OR_ASSIGN
                    {
                    root_0 = (Object)adaptor.nil();

                    OR_ASSIGN252=(Token)match(input,OR_ASSIGN,FOLLOW_OR_ASSIGN_in_assignmentOperator2522); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    OR_ASSIGN252_tree = (Object)adaptor.create(OR_ASSIGN252);
                    adaptor.addChild(root_0, OR_ASSIGN252_tree);
                    }

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:345:7: XOR_ASSIGN
                    {
                    root_0 = (Object)adaptor.nil();

                    XOR_ASSIGN253=(Token)match(input,XOR_ASSIGN,FOLLOW_XOR_ASSIGN_in_assignmentOperator2530); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    XOR_ASSIGN253_tree = (Object)adaptor.create(XOR_ASSIGN253);
                    adaptor.addChild(root_0, XOR_ASSIGN253_tree);
                    }

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:346:7: MOD_ASSIGN
                    {
                    root_0 = (Object)adaptor.nil();

                    MOD_ASSIGN254=(Token)match(input,MOD_ASSIGN,FOLLOW_MOD_ASSIGN_in_assignmentOperator2538); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MOD_ASSIGN254_tree = (Object)adaptor.create(MOD_ASSIGN254);
                    adaptor.addChild(root_0, MOD_ASSIGN254_tree);
                    }

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:347:7: LESS LESS EQUALS_ASSIGN
                    {
                    LESS255=(Token)match(input,LESS,FOLLOW_LESS_in_assignmentOperator2546); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LESS.add(LESS255);

                    LESS256=(Token)match(input,LESS,FOLLOW_LESS_in_assignmentOperator2548); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LESS.add(LESS256);

                    EQUALS_ASSIGN257=(Token)match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2550); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_EQUALS_ASSIGN.add(EQUALS_ASSIGN257);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 347:31: -> SHL_ASSIGN[\"<<=\"]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(SHL_ASSIGN, "<<="));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:348:7: ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN
                    {
                    GREATER258=(Token)match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2572); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_GREATER.add(GREATER258);

                    GREATER259=(Token)match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2574); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_GREATER.add(GREATER259);

                    GREATER260=(Token)match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2576); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_GREATER.add(GREATER260);

                    EQUALS_ASSIGN261=(Token)match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2578); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_EQUALS_ASSIGN.add(EQUALS_ASSIGN261);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 348:74: -> SHRB_ASSIGN[\">>>=\"]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(SHRB_ASSIGN, ">>>="));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:349:7: ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN
                    {
                    GREATER262=(Token)match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2599); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_GREATER.add(GREATER262);

                    GREATER263=(Token)match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2601); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_GREATER.add(GREATER263);

                    EQUALS_ASSIGN264=(Token)match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2603); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_EQUALS_ASSIGN.add(EQUALS_ASSIGN264);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 349:58: -> SHR_ASSIGN[\">>=\"]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(SHR_ASSIGN, ">>="));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "assignmentOperator"

    public static class extends_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "extends_key"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:355:1: extends_key : {...}? =>id= ID ;
    public final DRLExpressions.extends_key_return extends_key() throws RecognitionException {
        DRLExpressions.extends_key_return retval = new DRLExpressions.extends_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:356:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:356:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "extends_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_extends_key2639); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            id_tree = (Object)adaptor.create(id);
            adaptor.addChild(root_0, id_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "extends_key"

    public static class super_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "super_key"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:359:1: super_key : {...}? =>id= ID ;
    public final DRLExpressions.super_key_return super_key() throws RecognitionException {
        DRLExpressions.super_key_return retval = new DRLExpressions.super_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:360:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:360:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "super_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_super_key2666); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            id_tree = (Object)adaptor.create(id);
            adaptor.addChild(root_0, id_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "super_key"

    public static class instanceof_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "instanceof_key"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:363:1: instanceof_key : {...}? =>id= ID -> OPERATOR[$id] ;
    public final DRLExpressions.instanceof_key_return instanceof_key() throws RecognitionException {
        DRLExpressions.instanceof_key_return retval = new DRLExpressions.instanceof_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:364:5: ({...}? =>id= ID -> OPERATOR[$id] )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:364:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "instanceof_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_instanceof_key2693); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);



            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 364:85: -> OPERATOR[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(OPERATOR, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "instanceof_key"

    public static class boolean_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "boolean_key"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:367:1: boolean_key : {...}? =>id= ID ;
    public final DRLExpressions.boolean_key_return boolean_key() throws RecognitionException {
        DRLExpressions.boolean_key_return retval = new DRLExpressions.boolean_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:368:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:368:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "boolean_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_boolean_key2725); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            id_tree = (Object)adaptor.create(id);
            adaptor.addChild(root_0, id_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "boolean_key"

    public static class char_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "char_key"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:371:1: char_key : {...}? =>id= ID ;
    public final DRLExpressions.char_key_return char_key() throws RecognitionException {
        DRLExpressions.char_key_return retval = new DRLExpressions.char_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:372:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:372:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "char_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_char_key2752); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            id_tree = (Object)adaptor.create(id);
            adaptor.addChild(root_0, id_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "char_key"

    public static class byte_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "byte_key"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:375:1: byte_key : {...}? =>id= ID ;
    public final DRLExpressions.byte_key_return byte_key() throws RecognitionException {
        DRLExpressions.byte_key_return retval = new DRLExpressions.byte_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:376:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:376:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "byte_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_byte_key2779); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            id_tree = (Object)adaptor.create(id);
            adaptor.addChild(root_0, id_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "byte_key"

    public static class short_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "short_key"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:379:1: short_key : {...}? =>id= ID ;
    public final DRLExpressions.short_key_return short_key() throws RecognitionException {
        DRLExpressions.short_key_return retval = new DRLExpressions.short_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:380:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:380:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "short_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_short_key2806); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            id_tree = (Object)adaptor.create(id);
            adaptor.addChild(root_0, id_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "short_key"

    public static class int_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "int_key"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:383:1: int_key : {...}? =>id= ID ;
    public final DRLExpressions.int_key_return int_key() throws RecognitionException {
        DRLExpressions.int_key_return retval = new DRLExpressions.int_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:384:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:384:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "int_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_int_key2833); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            id_tree = (Object)adaptor.create(id);
            adaptor.addChild(root_0, id_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "int_key"

    public static class float_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "float_key"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:387:1: float_key : {...}? =>id= ID ;
    public final DRLExpressions.float_key_return float_key() throws RecognitionException {
        DRLExpressions.float_key_return retval = new DRLExpressions.float_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:388:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:388:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "float_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_float_key2860); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            id_tree = (Object)adaptor.create(id);
            adaptor.addChild(root_0, id_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "float_key"

    public static class long_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "long_key"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:391:1: long_key : {...}? =>id= ID ;
    public final DRLExpressions.long_key_return long_key() throws RecognitionException {
        DRLExpressions.long_key_return retval = new DRLExpressions.long_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:392:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:392:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "long_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.LONG))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_long_key2887); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            id_tree = (Object)adaptor.create(id);
            adaptor.addChild(root_0, id_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "long_key"

    public static class double_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "double_key"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:395:1: double_key : {...}? =>id= ID ;
    public final DRLExpressions.double_key_return double_key() throws RecognitionException {
        DRLExpressions.double_key_return retval = new DRLExpressions.double_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:396:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:396:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "double_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_double_key2914); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            id_tree = (Object)adaptor.create(id);
            adaptor.addChild(root_0, id_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "double_key"

    public static class void_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "void_key"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:399:1: void_key : {...}? =>id= ID ;
    public final DRLExpressions.void_key_return void_key() throws RecognitionException {
        DRLExpressions.void_key_return retval = new DRLExpressions.void_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:400:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:400:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.VOID)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "void_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.VOID))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_void_key2941); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            id_tree = (Object)adaptor.create(id);
            adaptor.addChild(root_0, id_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "void_key"

    public static class this_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "this_key"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:403:1: this_key : {...}? =>id= ID ;
    public final DRLExpressions.this_key_return this_key() throws RecognitionException {
        DRLExpressions.this_key_return retval = new DRLExpressions.this_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:404:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:404:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "this_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.THIS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_this_key2968); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            id_tree = (Object)adaptor.create(id);
            adaptor.addChild(root_0, id_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "this_key"

    public static class class_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "class_key"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:407:1: class_key : {...}? =>id= ID ;
    public final DRLExpressions.class_key_return class_key() throws RecognitionException {
        DRLExpressions.class_key_return retval = new DRLExpressions.class_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:408:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:408:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.CLASS)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "class_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.CLASS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_class_key2995); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            id_tree = (Object)adaptor.create(id);
            adaptor.addChild(root_0, id_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "class_key"

    public static class new_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "new_key"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:411:1: new_key : {...}? =>id= ID ;
    public final DRLExpressions.new_key_return new_key() throws RecognitionException {
        DRLExpressions.new_key_return retval = new DRLExpressions.new_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:412:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:412:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.NEW)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "new_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.NEW))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_new_key3022); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            id_tree = (Object)adaptor.create(id);
            adaptor.addChild(root_0, id_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "new_key"

    public static class not_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "not_key"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:415:1: not_key : {...}? =>id= ID ;
    public final DRLExpressions.not_key_return not_key() throws RecognitionException {
        DRLExpressions.not_key_return retval = new DRLExpressions.not_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:416:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:416:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "not_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.NOT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_not_key3049); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            id_tree = (Object)adaptor.create(id);
            adaptor.addChild(root_0, id_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "not_key"

    public static class in_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "in_key"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:419:1: in_key : {...}? =>id= ID -> OPERATOR[$id] ;
    public final DRLExpressions.in_key_return in_key() throws RecognitionException {
        DRLExpressions.in_key_return retval = new DRLExpressions.in_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:420:3: ({...}? =>id= ID -> OPERATOR[$id] )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:420:10: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "in_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.IN))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_in_key3074); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);



            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 420:75: -> OPERATOR[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(OPERATOR, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "in_key"

    public static class operator_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "operator_key"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:423:1: operator_key : {...}? =>id= ID -> OPERATOR[$id] ;
    public final DRLExpressions.operator_key_return operator_key() throws RecognitionException {
        DRLExpressions.operator_key_return retval = new DRLExpressions.operator_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:424:3: ({...}? =>id= ID -> OPERATOR[$id] )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:424:10: {...}? =>id= ID
            {
            if ( !(((helper.isPluggableEvaluator(false)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "operator_key", "(helper.isPluggableEvaluator(false))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_operator_key3102); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);



            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 424:58: -> OPERATOR[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(OPERATOR, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "operator_key"

    public static class neg_operator_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "neg_operator_key"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:427:1: neg_operator_key : {...}? =>id= ID -> NEG_OPERATOR[$id] ;
    public final DRLExpressions.neg_operator_key_return neg_operator_key() throws RecognitionException {
        DRLExpressions.neg_operator_key_return retval = new DRLExpressions.neg_operator_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:428:3: ({...}? =>id= ID -> NEG_OPERATOR[$id] )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:428:10: {...}? =>id= ID
            {
            if ( !(((helper.isPluggableEvaluator(true)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "neg_operator_key", "(helper.isPluggableEvaluator(true))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_neg_operator_key3130); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(id);



            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 428:58: -> NEG_OPERATOR[$id]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(NEG_OPERATOR, id));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "neg_operator_key"

    // $ANTLR start synpred1_DRLExpressions
    public final void synpred1_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:8: ( primitiveType )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:9: primitiveType
        {
        pushFollow(FOLLOW_primitiveType_in_synpred1_DRLExpressions220);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_DRLExpressions

    // $ANTLR start synpred2_DRLExpressions
    public final void synpred2_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:44: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:45: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred2_DRLExpressions231); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred2_DRLExpressions233); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_DRLExpressions

    // $ANTLR start synpred3_DRLExpressions
    public final void synpred3_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:72:13: ( typeArguments )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:72:14: typeArguments
        {
        pushFollow(FOLLOW_typeArguments_in_synpred3_DRLExpressions257);
        typeArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_DRLExpressions

    // $ANTLR start synpred4_DRLExpressions
    public final void synpred4_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:72:55: ( typeArguments )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:72:56: typeArguments
        {
        pushFollow(FOLLOW_typeArguments_in_synpred4_DRLExpressions271);
        typeArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred4_DRLExpressions

    // $ANTLR start synpred5_DRLExpressions
    public final void synpred5_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:72:92: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:72:93: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred5_DRLExpressions283); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred5_DRLExpressions285); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_DRLExpressions

    // $ANTLR start synpred6_DRLExpressions
    public final void synpred6_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:95:30: ( assignmentOperator )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:95:31: assignmentOperator
        {
        pushFollow(FOLLOW_assignmentOperator_in_synpred6_DRLExpressions418);
        assignmentOperator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred6_DRLExpressions

    // $ANTLR start synpred7_DRLExpressions
    public final void synpred7_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:124:9: ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:124:10: ( DOUBLE_PIPE | DOUBLE_AMPER ) operator
        {
        if ( (input.LA(1)>=DOUBLE_AMPER && input.LA(1)<=DOUBLE_PIPE) ) {
            input.consume();
            state.errorRecovery=false;state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            throw mse;
        }

        pushFollow(FOLLOW_operator_in_synpred7_DRLExpressions632);
        operator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred7_DRLExpressions

    // $ANTLR start synpred8_DRLExpressions
    public final void synpred8_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:148:28: ( relationalOp )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:148:29: relationalOp
        {
        pushFollow(FOLLOW_relationalOp_in_synpred8_DRLExpressions905);
        relationalOp();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred8_DRLExpressions

    // $ANTLR start synpred9_DRLExpressions
    public final void synpred9_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:163:34: ( squareArguments )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:163:35: squareArguments
        {
        pushFollow(FOLLOW_squareArguments_in_synpred9_DRLExpressions1010);
        squareArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred9_DRLExpressions

    // $ANTLR start synpred10_DRLExpressions
    public final void synpred10_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:164:23: ( squareArguments )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:164:24: squareArguments
        {
        pushFollow(FOLLOW_squareArguments_in_synpred10_DRLExpressions1030);
        squareArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred10_DRLExpressions

    // $ANTLR start synpred11_DRLExpressions
    public final void synpred11_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:173:26: ( shiftOp )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:173:27: shiftOp
        {
        pushFollow(FOLLOW_shiftOp_in_synpred11_DRLExpressions1090);
        shiftOp();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred11_DRLExpressions

    // $ANTLR start synpred12_DRLExpressions
    public final void synpred12_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:181:34: ( PLUS | MINUS )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:
        {
        if ( (input.LA(1)>=MINUS && input.LA(1)<=PLUS) ) {
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
    // $ANTLR end synpred12_DRLExpressions

    // $ANTLR start synpred13_DRLExpressions
    public final void synpred13_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:199:9: ( castExpression )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:199:10: castExpression
        {
        pushFollow(FOLLOW_castExpression_in_synpred13_DRLExpressions1319);
        castExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred13_DRLExpressions

    // $ANTLR start synpred14_DRLExpressions
    public final void synpred14_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:200:18: ( selector )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:200:19: selector
        {
        pushFollow(FOLLOW_selector_in_synpred14_DRLExpressions1336);
        selector();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred14_DRLExpressions

    // $ANTLR start synpred15_DRLExpressions
    public final void synpred15_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:200:42: ( INCR | DECR )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:
        {
        if ( (input.LA(1)>=DECR && input.LA(1)<=INCR) ) {
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
    // $ANTLR end synpred15_DRLExpressions

    // $ANTLR start synpred16_DRLExpressions
    public final void synpred16_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:204:8: ( LEFT_PAREN primitiveType )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:204:9: LEFT_PAREN primitiveType
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred16_DRLExpressions1380); if (state.failed) return ;
        pushFollow(FOLLOW_primitiveType_in_synpred16_DRLExpressions1382);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred16_DRLExpressions

    // $ANTLR start synpred17_DRLExpressions
    public final void synpred17_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:205:8: ( LEFT_PAREN type )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:205:9: LEFT_PAREN type
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred17_DRLExpressions1403); if (state.failed) return ;
        pushFollow(FOLLOW_type_in_synpred17_DRLExpressions1405);
        type();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred17_DRLExpressions

    // $ANTLR start synpred18_DRLExpressions
    public final void synpred18_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:220:7: ( parExpression )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:220:8: parExpression
        {
        pushFollow(FOLLOW_parExpression_in_synpred18_DRLExpressions1511);
        parExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred18_DRLExpressions

    // $ANTLR start synpred19_DRLExpressions
    public final void synpred19_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:221:9: ( nonWildcardTypeArguments )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:221:10: nonWildcardTypeArguments
        {
        pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred19_DRLExpressions1526);
        nonWildcardTypeArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred19_DRLExpressions

    // $ANTLR start synpred20_DRLExpressions
    public final void synpred20_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:222:9: ( literal )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:222:10: literal
        {
        pushFollow(FOLLOW_literal_in_synpred20_DRLExpressions1551);
        literal();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred20_DRLExpressions

    // $ANTLR start synpred21_DRLExpressions
    public final void synpred21_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:224:9: ( super_key )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:224:10: super_key
        {
        pushFollow(FOLLOW_super_key_in_synpred21_DRLExpressions1571);
        super_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred21_DRLExpressions

    // $ANTLR start synpred22_DRLExpressions
    public final void synpred22_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:225:9: ( new_key )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:225:10: new_key
        {
        pushFollow(FOLLOW_new_key_in_synpred22_DRLExpressions1588);
        new_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred22_DRLExpressions

    // $ANTLR start synpred23_DRLExpressions
    public final void synpred23_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:226:9: ( primitiveType )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:226:10: primitiveType
        {
        pushFollow(FOLLOW_primitiveType_in_synpred23_DRLExpressions1605);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred23_DRLExpressions

    // $ANTLR start synpred24_DRLExpressions
    public final void synpred24_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:228:9: ( inlineMapExpression )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:228:10: inlineMapExpression
        {
        pushFollow(FOLLOW_inlineMapExpression_in_synpred24_DRLExpressions1636);
        inlineMapExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred24_DRLExpressions

    // $ANTLR start synpred25_DRLExpressions
    public final void synpred25_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:229:9: ( inlineListExpression )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:229:10: inlineListExpression
        {
        pushFollow(FOLLOW_inlineListExpression_in_synpred25_DRLExpressions1651);
        inlineListExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred25_DRLExpressions

    // $ANTLR start synpred26_DRLExpressions
    public final void synpred26_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:230:9: ( ID )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:230:10: ID
        {
        match(input,ID,FOLLOW_ID_in_synpred26_DRLExpressions1666); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred26_DRLExpressions

    // $ANTLR start synpred27_DRLExpressions
    public final void synpred27_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:230:19: ( DOT ID )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:230:20: DOT ID
        {
        match(input,DOT,FOLLOW_DOT_in_synpred27_DRLExpressions1673); if (state.failed) return ;
        match(input,ID,FOLLOW_ID_in_synpred27_DRLExpressions1675); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred27_DRLExpressions

    // $ANTLR start synpred28_DRLExpressions
    public final void synpred28_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:230:39: ( identifierSuffix )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:230:40: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred28_DRLExpressions1686);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred28_DRLExpressions

    // $ANTLR start synpred29_DRLExpressions
    public final void synpred29_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:254:7: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:254:8: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred29_DRLExpressions1830); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred29_DRLExpressions1832); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred29_DRLExpressions

    // $ANTLR start synpred30_DRLExpressions
    public final void synpred30_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:255:8: ( LEFT_SQUARE )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:255:9: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred30_DRLExpressions1854); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred30_DRLExpressions

    // $ANTLR start synpred31_DRLExpressions
    public final void synpred31_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:282:107: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:282:108: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred31_DRLExpressions2060); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred31_DRLExpressions2062); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred31_DRLExpressions

    // $ANTLR start synpred32_DRLExpressions
    public final void synpred32_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:313:9: ( DOT super_key )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:313:10: DOT super_key
        {
        match(input,DOT,FOLLOW_DOT_in_synpred32_DRLExpressions2258); if (state.failed) return ;
        pushFollow(FOLLOW_super_key_in_synpred32_DRLExpressions2260);
        super_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred32_DRLExpressions

    // $ANTLR start synpred33_DRLExpressions
    public final void synpred33_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:314:9: ( DOT new_key )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:314:10: DOT new_key
        {
        match(input,DOT,FOLLOW_DOT_in_synpred33_DRLExpressions2278); if (state.failed) return ;
        pushFollow(FOLLOW_new_key_in_synpred33_DRLExpressions2280);
        new_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred33_DRLExpressions

    // $ANTLR start synpred34_DRLExpressions
    public final void synpred34_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:315:9: ( DOT ID )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:315:10: DOT ID
        {
        match(input,DOT,FOLLOW_DOT_in_synpred34_DRLExpressions2303); if (state.failed) return ;
        match(input,ID,FOLLOW_ID_in_synpred34_DRLExpressions2305); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred34_DRLExpressions

    // $ANTLR start synpred35_DRLExpressions
    public final void synpred35_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:315:27: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:315:28: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred35_DRLExpressions2314); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred35_DRLExpressions

    // $ANTLR start synpred36_DRLExpressions
    public final void synpred36_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:317:9: ( LEFT_SQUARE )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:317:10: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred36_DRLExpressions2337); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred36_DRLExpressions

    // $ANTLR start synpred37_DRLExpressions
    public final void synpred37_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:322:18: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:322:19: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred37_DRLExpressions2378); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred37_DRLExpressions

    // $ANTLR start synpred38_DRLExpressions
    public final void synpred38_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:348:7: ( GREATER GREATER GREATER )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:348:8: GREATER GREATER GREATER
        {
        match(input,GREATER,FOLLOW_GREATER_in_synpred38_DRLExpressions2564); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred38_DRLExpressions2566); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred38_DRLExpressions2568); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred38_DRLExpressions

    // $ANTLR start synpred39_DRLExpressions
    public final void synpred39_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:349:7: ( GREATER GREATER )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:349:8: GREATER GREATER
        {
        match(input,GREATER,FOLLOW_GREATER_in_synpred39_DRLExpressions2593); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred39_DRLExpressions2595); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred39_DRLExpressions

    // Delegated rules

    public final boolean synpred11_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred11_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred13_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred13_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred17_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred17_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred24_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred24_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred3_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred23_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred23_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred6_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred6_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred20_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred20_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred30_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred30_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred36_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred36_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred12_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred12_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred10_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred10_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred26_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred26_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred33_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred33_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred27_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred27_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred22_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred22_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred4_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred4_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred7_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred7_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred15_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred15_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred21_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred21_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred18_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred18_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred8_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred8_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred16_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred16_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred35_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred35_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred19_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred19_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred28_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred28_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred32_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred32_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred29_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred29_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred25_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred25_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred9_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred9_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred34_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred34_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred5_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred5_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred37_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred37_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred14_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred14_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred38_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred38_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred31_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred31_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred39_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred39_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA4 dfa4 = new DFA4(this);
    protected DFA5 dfa5 = new DFA5(this);
    protected DFA13 dfa13 = new DFA13(this);
    protected DFA21 dfa21 = new DFA21(this);
    protected DFA24 dfa24 = new DFA24(this);
    protected DFA27 dfa27 = new DFA27(this);
    protected DFA28 dfa28 = new DFA28(this);
    protected DFA30 dfa30 = new DFA30(this);
    protected DFA31 dfa31 = new DFA31(this);
    protected DFA33 dfa33 = new DFA33(this);
    protected DFA35 dfa35 = new DFA35(this);
    protected DFA40 dfa40 = new DFA40(this);
    protected DFA38 dfa38 = new DFA38(this);
    protected DFA39 dfa39 = new DFA39(this);
    protected DFA42 dfa42 = new DFA42(this);
    protected DFA47 dfa47 = new DFA47(this);
    protected DFA46 dfa46 = new DFA46(this);
    protected DFA52 dfa52 = new DFA52(this);
    protected DFA61 dfa61 = new DFA61(this);
    protected DFA70 dfa70 = new DFA70(this);
    protected DFA72 dfa72 = new DFA72(this);
    protected DFA77 dfa77 = new DFA77(this);
    static final String DFA4_eotS =
        "\54\uffff";
    static final String DFA4_eofS =
        "\1\2\53\uffff";
    static final String DFA4_minS =
        "\1\16\1\0\52\uffff";
    static final String DFA4_maxS =
        "\1\110\1\0\52\uffff";
    static final String DFA4_acceptS =
        "\2\uffff\1\2\50\uffff\1\1";
    static final String DFA4_specialS =
        "\1\uffff\1\0\52\uffff}>";
    static final String[] DFA4_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\15\2\1\uffff\4\2\2\uffff"+
            "\1\2\1\1\5\2\1\uffff\13\2\2\uffff\2\2\5\uffff\1\2",
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
            return "72:12: ( ( typeArguments )=> typeArguments )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA4_1 = input.LA(1);

                         
                        int index4_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_DRLExpressions()) ) {s = 43;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index4_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 4, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA5_eotS =
        "\54\uffff";
    static final String DFA5_eofS =
        "\1\2\53\uffff";
    static final String DFA5_minS =
        "\1\16\1\0\52\uffff";
    static final String DFA5_maxS =
        "\1\110\1\0\52\uffff";
    static final String DFA5_acceptS =
        "\2\uffff\1\2\50\uffff\1\1";
    static final String DFA5_specialS =
        "\1\uffff\1\0\52\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\15\2\1\uffff\4\2\2\uffff"+
            "\1\2\1\1\5\2\1\uffff\13\2\2\uffff\2\2\5\uffff\1\2",
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
            ""
    };

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "72:54: ( ( typeArguments )=> typeArguments )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA5_1 = input.LA(1);

                         
                        int index5_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_DRLExpressions()) ) {s = 43;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index5_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 5, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA13_eotS =
        "\16\uffff";
    static final String DFA13_eofS =
        "\1\14\15\uffff";
    static final String DFA13_minS =
        "\1\16\13\0\2\uffff";
    static final String DFA13_maxS =
        "\1\110\13\0\2\uffff";
    static final String DFA13_acceptS =
        "\14\uffff\1\2\1\1";
    static final String DFA13_specialS =
        "\1\uffff\1\3\1\1\1\11\1\6\1\7\1\12\1\2\1\4\1\0\1\10\1\5\2\uffff}>";
    static final String[] DFA13_transitionS = {
            "\1\14\2\uffff\2\14\1\uffff\1\14\3\uffff\3\14\1\2\1\3\1\4\1\5"+
            "\1\6\1\7\1\10\1\11\2\14\1\uffff\2\14\4\uffff\1\13\1\12\1\1\4"+
            "\14\1\uffff\2\14\4\uffff\2\14\5\uffff\2\14\5\uffff\1\14",
            "\1\uffff",
            "\1\uffff",
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
            ""
    };

    static final short[] DFA13_eot = DFA.unpackEncodedString(DFA13_eotS);
    static final short[] DFA13_eof = DFA.unpackEncodedString(DFA13_eofS);
    static final char[] DFA13_min = DFA.unpackEncodedStringToUnsignedChars(DFA13_minS);
    static final char[] DFA13_max = DFA.unpackEncodedStringToUnsignedChars(DFA13_maxS);
    static final short[] DFA13_accept = DFA.unpackEncodedString(DFA13_acceptS);
    static final short[] DFA13_special = DFA.unpackEncodedString(DFA13_specialS);
    static final short[][] DFA13_transition;

    static {
        int numStates = DFA13_transitionS.length;
        DFA13_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA13_transition[i] = DFA.unpackEncodedString(DFA13_transitionS[i]);
        }
    }

    class DFA13 extends DFA {

        public DFA13(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 13;
            this.eot = DFA13_eot;
            this.eof = DFA13_eof;
            this.min = DFA13_min;
            this.max = DFA13_max;
            this.accept = DFA13_accept;
            this.special = DFA13_special;
            this.transition = DFA13_transition;
        }
        public String getDescription() {
            return "95:29: ( ( assignmentOperator )=> assignmentOperator expression )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA13_9 = input.LA(1);

                         
                        int index13_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_9);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA13_2 = input.LA(1);

                         
                        int index13_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA13_7 = input.LA(1);

                         
                        int index13_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_7);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA13_1 = input.LA(1);

                         
                        int index13_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_1);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA13_8 = input.LA(1);

                         
                        int index13_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_8);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA13_11 = input.LA(1);

                         
                        int index13_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_11);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA13_4 = input.LA(1);

                         
                        int index13_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_4);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA13_5 = input.LA(1);

                         
                        int index13_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_5);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA13_10 = input.LA(1);

                         
                        int index13_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_10);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA13_3 = input.LA(1);

                         
                        int index13_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_3);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA13_6 = input.LA(1);

                         
                        int index13_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_6);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 13, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA21_eotS =
        "\50\uffff";
    static final String DFA21_eofS =
        "\1\1\47\uffff";
    static final String DFA21_minS =
        "\1\16\3\uffff\2\0\42\uffff";
    static final String DFA21_maxS =
        "\1\110\3\uffff\2\0\42\uffff";
    static final String DFA21_acceptS =
        "\1\uffff\1\2\45\uffff\1\1";
    static final String DFA21_specialS =
        "\4\uffff\1\0\1\1\42\uffff}>";
    static final String[] DFA21_transitionS = {
            "\1\1\2\uffff\2\1\1\uffff\1\1\3\uffff\15\1\1\uffff\2\1\4\uffff"+
            "\7\1\1\uffff\2\1\1\uffff\1\4\1\5\6\1\2\uffff\2\1\5\uffff\1\1",
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
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA21_eot = DFA.unpackEncodedString(DFA21_eotS);
    static final short[] DFA21_eof = DFA.unpackEncodedString(DFA21_eofS);
    static final char[] DFA21_min = DFA.unpackEncodedStringToUnsignedChars(DFA21_minS);
    static final char[] DFA21_max = DFA.unpackEncodedStringToUnsignedChars(DFA21_maxS);
    static final short[] DFA21_accept = DFA.unpackEncodedString(DFA21_acceptS);
    static final short[] DFA21_special = DFA.unpackEncodedString(DFA21_specialS);
    static final short[][] DFA21_transition;

    static {
        int numStates = DFA21_transitionS.length;
        DFA21_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA21_transition[i] = DFA.unpackEncodedString(DFA21_transitionS[i]);
        }
    }

    class DFA21 extends DFA {

        public DFA21(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 21;
            this.eot = DFA21_eot;
            this.eof = DFA21_eof;
            this.min = DFA21_min;
            this.max = DFA21_max;
            this.accept = DFA21_accept;
            this.special = DFA21_special;
            this.transition = DFA21_transition;
        }
        public String getDescription() {
            return "()* loopback of 124:6: ( ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER ) op= operator se2= shiftExpressionTk ) -> ^( $lop $andOrRestriction ^( $op $se2) ) )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA21_4 = input.LA(1);

                         
                        int index21_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_DRLExpressions()) ) {s = 39;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index21_4);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA21_5 = input.LA(1);

                         
                        int index21_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_DRLExpressions()) ) {s = 39;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index21_5);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 21, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA24_eotS =
        "\52\uffff";
    static final String DFA24_eofS =
        "\1\2\51\uffff";
    static final String DFA24_minS =
        "\1\16\1\0\50\uffff";
    static final String DFA24_maxS =
        "\1\110\1\0\50\uffff";
    static final String DFA24_acceptS =
        "\2\uffff\1\2\46\uffff\1\1";
    static final String DFA24_specialS =
        "\1\uffff\1\0\50\uffff}>";
    static final String[] DFA24_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\15\2\1\uffff\4\2\2\uffff"+
            "\7\2\1\uffff\2\2\1\uffff\10\2\2\uffff\2\2\5\uffff\1\1",
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
            ""
    };

    static final short[] DFA24_eot = DFA.unpackEncodedString(DFA24_eotS);
    static final short[] DFA24_eof = DFA.unpackEncodedString(DFA24_eofS);
    static final char[] DFA24_min = DFA.unpackEncodedStringToUnsignedChars(DFA24_minS);
    static final char[] DFA24_max = DFA.unpackEncodedStringToUnsignedChars(DFA24_maxS);
    static final short[] DFA24_accept = DFA.unpackEncodedString(DFA24_acceptS);
    static final short[] DFA24_special = DFA.unpackEncodedString(DFA24_specialS);
    static final short[][] DFA24_transition;

    static {
        int numStates = DFA24_transitionS.length;
        DFA24_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA24_transition[i] = DFA.unpackEncodedString(DFA24_transitionS[i]);
        }
    }

    class DFA24 extends DFA {

        public DFA24(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 24;
            this.eot = DFA24_eot;
            this.eof = DFA24_eof;
            this.min = DFA24_min;
            this.max = DFA24_max;
            this.accept = DFA24_accept;
            this.special = DFA24_special;
            this.transition = DFA24_transition;
        }
        public String getDescription() {
            return "135:21: ( instanceof_key type )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA24_1 = input.LA(1);

                         
                        int index24_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {s = 41;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index24_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 24, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA27_eotS =
        "\53\uffff";
    static final String DFA27_eofS =
        "\1\2\52\uffff";
    static final String DFA27_minS =
        "\1\16\1\0\51\uffff";
    static final String DFA27_maxS =
        "\1\110\1\0\51\uffff";
    static final String DFA27_acceptS =
        "\2\uffff\1\3\46\uffff\1\1\1\2";
    static final String DFA27_specialS =
        "\1\uffff\1\0\51\uffff}>";
    static final String[] DFA27_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\15\2\1\uffff\4\2\2\uffff"+
            "\7\2\1\uffff\2\2\1\uffff\10\2\2\uffff\2\2\5\uffff\1\1",
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

    static final short[] DFA27_eot = DFA.unpackEncodedString(DFA27_eotS);
    static final short[] DFA27_eof = DFA.unpackEncodedString(DFA27_eofS);
    static final char[] DFA27_min = DFA.unpackEncodedStringToUnsignedChars(DFA27_minS);
    static final char[] DFA27_max = DFA.unpackEncodedStringToUnsignedChars(DFA27_maxS);
    static final short[] DFA27_accept = DFA.unpackEncodedString(DFA27_acceptS);
    static final short[] DFA27_special = DFA.unpackEncodedString(DFA27_specialS);
    static final short[][] DFA27_transition;

    static {
        int numStates = DFA27_transitionS.length;
        DFA27_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA27_transition[i] = DFA.unpackEncodedString(DFA27_transitionS[i]);
        }
    }

    class DFA27 extends DFA {

        public DFA27(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 27;
            this.eot = DFA27_eot;
            this.eof = DFA27_eof;
            this.min = DFA27_min;
            this.max = DFA27_max;
            this.accept = DFA27_accept;
            this.special = DFA27_special;
            this.transition = DFA27_transition;
        }
        public String getDescription() {
            return "141:5: ( not_key in= in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN -> ^( NEG_OPERATOR[$in.text] $rel ( expression )+ ) | in= in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN -> ^( $in $rel ( expression )+ ) )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA27_1 = input.LA(1);

                         
                        int index27_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {s = 41;}

                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {s = 42;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index27_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 27, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA28_eotS =
        "\53\uffff";
    static final String DFA28_eofS =
        "\1\2\52\uffff";
    static final String DFA28_minS =
        "\1\16\1\0\21\uffff\2\0\26\uffff";
    static final String DFA28_maxS =
        "\1\110\1\0\21\uffff\2\0\26\uffff";
    static final String DFA28_acceptS =
        "\2\uffff\1\2\46\uffff\2\1";
    static final String DFA28_specialS =
        "\1\0\1\1\21\uffff\1\2\1\3\26\uffff}>";
    static final String[] DFA28_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\15\2\1\uffff\4\2\1\52\1"+
            "\51\1\24\1\23\5\2\1\uffff\2\2\1\uffff\10\2\2\uffff\2\2\5\uffff"+
            "\1\1",
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
            ""
    };

    static final short[] DFA28_eot = DFA.unpackEncodedString(DFA28_eotS);
    static final short[] DFA28_eof = DFA.unpackEncodedString(DFA28_eofS);
    static final char[] DFA28_min = DFA.unpackEncodedStringToUnsignedChars(DFA28_minS);
    static final char[] DFA28_max = DFA.unpackEncodedStringToUnsignedChars(DFA28_maxS);
    static final short[] DFA28_accept = DFA.unpackEncodedString(DFA28_acceptS);
    static final short[] DFA28_special = DFA.unpackEncodedString(DFA28_specialS);
    static final short[][] DFA28_transition;

    static {
        int numStates = DFA28_transitionS.length;
        DFA28_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA28_transition[i] = DFA.unpackEncodedString(DFA28_transitionS[i]);
        }
    }

    class DFA28 extends DFA {

        public DFA28(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 28;
            this.eot = DFA28_eot;
            this.eof = DFA28_eof;
            this.min = DFA28_min;
            this.max = DFA28_max;
            this.accept = DFA28_accept;
            this.special = DFA28_special;
            this.transition = DFA28_transition;
        }
        public String getDescription() {
            return "()* loopback of 148:26: ( ( relationalOp )=> relationalOp shiftExpressionTk )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA28_0 = input.LA(1);

                         
                        int index28_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA28_0==ID) ) {s = 1;}

                        else if ( (LA28_0==EOF||LA28_0==FLOAT||(LA28_0>=HEX && LA28_0<=DECIMAL)||LA28_0==STRING||(LA28_0>=BOOL && LA28_0<=INCR)||(LA28_0>=SEMICOLON && LA28_0<=NOT_EQUALS)||(LA28_0>=EQUALS_ASSIGN && LA28_0<=RIGHT_SQUARE)||(LA28_0>=RIGHT_CURLY && LA28_0<=COMMA)||(LA28_0>=DOUBLE_AMPER && LA28_0<=XOR)||(LA28_0>=MINUS && LA28_0<=PLUS)) ) {s = 2;}

                        else if ( (LA28_0==LESS) ) {s = 19;}

                        else if ( (LA28_0==GREATER) ) {s = 20;}

                        else if ( (LA28_0==LESS_EQUALS) && (synpred8_DRLExpressions())) {s = 41;}

                        else if ( (LA28_0==GREATER_EQUALS) && (synpred8_DRLExpressions())) {s = 42;}

                         
                        input.seek(index28_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA28_1 = input.LA(1);

                         
                        int index28_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((synpred8_DRLExpressions()&&((helper.isPluggableEvaluator(false))))||(synpred8_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))))) ) {s = 42;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index28_1);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA28_19 = input.LA(1);

                         
                        int index28_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 42;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index28_19);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA28_20 = input.LA(1);

                         
                        int index28_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 42;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index28_20);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 28, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA30_eotS =
        "\23\uffff";
    static final String DFA30_eofS =
        "\1\2\22\uffff";
    static final String DFA30_minS =
        "\1\16\1\0\21\uffff";
    static final String DFA30_maxS =
        "\1\110\1\0\21\uffff";
    static final String DFA30_acceptS =
        "\2\uffff\1\2\17\uffff\1\1";
    static final String DFA30_specialS =
        "\1\uffff\1\0\21\uffff}>";
    static final String[] DFA30_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\2\2\11\uffff\2\2\10\uffff"+
            "\1\2\1\uffff\1\2\1\uffff\1\1\10\uffff\2\2\5\uffff\2\2\5\uffff"+
            "\1\2",
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
            ""
    };

    static final short[] DFA30_eot = DFA.unpackEncodedString(DFA30_eotS);
    static final short[] DFA30_eof = DFA.unpackEncodedString(DFA30_eofS);
    static final char[] DFA30_min = DFA.unpackEncodedStringToUnsignedChars(DFA30_minS);
    static final char[] DFA30_max = DFA.unpackEncodedStringToUnsignedChars(DFA30_maxS);
    static final short[] DFA30_accept = DFA.unpackEncodedString(DFA30_acceptS);
    static final short[] DFA30_special = DFA.unpackEncodedString(DFA30_specialS);
    static final short[][] DFA30_transition;

    static {
        int numStates = DFA30_transitionS.length;
        DFA30_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA30_transition[i] = DFA.unpackEncodedString(DFA30_transitionS[i]);
        }
    }

    class DFA30 extends DFA {

        public DFA30(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 30;
            this.eot = DFA30_eot;
            this.eof = DFA30_eof;
            this.min = DFA30_min;
            this.max = DFA30_max;
            this.accept = DFA30_accept;
            this.special = DFA30_special;
            this.transition = DFA30_transition;
        }
        public String getDescription() {
            return "163:33: ( ( squareArguments )=> squareArguments )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA30_1 = input.LA(1);

                         
                        int index30_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred9_DRLExpressions()) ) {s = 18;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index30_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 30, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA31_eotS =
        "\23\uffff";
    static final String DFA31_eofS =
        "\1\2\22\uffff";
    static final String DFA31_minS =
        "\1\16\1\0\21\uffff";
    static final String DFA31_maxS =
        "\1\110\1\0\21\uffff";
    static final String DFA31_acceptS =
        "\2\uffff\1\2\17\uffff\1\1";
    static final String DFA31_specialS =
        "\1\uffff\1\0\21\uffff}>";
    static final String[] DFA31_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\2\2\11\uffff\2\2\10\uffff"+
            "\1\2\1\uffff\1\2\1\uffff\1\1\10\uffff\2\2\5\uffff\2\2\5\uffff"+
            "\1\2",
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
            return "164:22: ( ( squareArguments )=> squareArguments )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA31_1 = input.LA(1);

                         
                        int index31_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_DRLExpressions()) ) {s = 18;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index31_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 31, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA33_eotS =
        "\54\uffff";
    static final String DFA33_eofS =
        "\1\1\53\uffff";
    static final String DFA33_minS =
        "\1\16\17\uffff\2\0\32\uffff";
    static final String DFA33_maxS =
        "\1\110\17\uffff\2\0\32\uffff";
    static final String DFA33_acceptS =
        "\1\uffff\1\2\51\uffff\1\1";
    static final String DFA33_specialS =
        "\20\uffff\1\0\1\1\32\uffff}>";
    static final String[] DFA33_transitionS = {
            "\1\1\2\uffff\2\1\1\uffff\1\1\3\uffff\15\1\1\uffff\6\1\1\21\1"+
            "\20\5\1\1\uffff\2\1\1\uffff\10\1\2\uffff\2\1\5\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
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
            "",
            "",
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

    static final short[] DFA33_eot = DFA.unpackEncodedString(DFA33_eotS);
    static final short[] DFA33_eof = DFA.unpackEncodedString(DFA33_eofS);
    static final char[] DFA33_min = DFA.unpackEncodedStringToUnsignedChars(DFA33_minS);
    static final char[] DFA33_max = DFA.unpackEncodedStringToUnsignedChars(DFA33_maxS);
    static final short[] DFA33_accept = DFA.unpackEncodedString(DFA33_acceptS);
    static final short[] DFA33_special = DFA.unpackEncodedString(DFA33_specialS);
    static final short[][] DFA33_transition;

    static {
        int numStates = DFA33_transitionS.length;
        DFA33_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA33_transition[i] = DFA.unpackEncodedString(DFA33_transitionS[i]);
        }
    }

    class DFA33 extends DFA {

        public DFA33(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 33;
            this.eot = DFA33_eot;
            this.eof = DFA33_eof;
            this.min = DFA33_min;
            this.max = DFA33_max;
            this.accept = DFA33_accept;
            this.special = DFA33_special;
            this.transition = DFA33_transition;
        }
        public String getDescription() {
            return "()* loopback of 173:24: ( ( shiftOp )=> shiftOp additiveExpression )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA33_16 = input.LA(1);

                         
                        int index33_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_DRLExpressions()) ) {s = 43;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index33_16);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA33_17 = input.LA(1);

                         
                        int index33_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_DRLExpressions()) ) {s = 43;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index33_17);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 33, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA35_eotS =
        "\54\uffff";
    static final String DFA35_eofS =
        "\1\1\53\uffff";
    static final String DFA35_minS =
        "\1\16\26\uffff\2\0\23\uffff";
    static final String DFA35_maxS =
        "\1\110\26\uffff\2\0\23\uffff";
    static final String DFA35_acceptS =
        "\1\uffff\1\2\51\uffff\1\1";
    static final String DFA35_specialS =
        "\27\uffff\1\0\1\1\23\uffff}>";
    static final String[] DFA35_transitionS = {
            "\1\1\2\uffff\2\1\1\uffff\1\1\3\uffff\15\1\1\uffff\15\1\1\uffff"+
            "\2\1\1\uffff\10\1\2\uffff\1\30\1\27\5\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            return "()* loopback of 181:32: ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA35_23 = input.LA(1);

                         
                        int index35_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred12_DRLExpressions()) ) {s = 43;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index35_23);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA35_24 = input.LA(1);

                         
                        int index35_24 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred12_DRLExpressions()) ) {s = 43;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index35_24);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 35, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA40_eotS =
        "\16\uffff";
    static final String DFA40_eofS =
        "\16\uffff";
    static final String DFA40_minS =
        "\1\16\2\uffff\1\0\12\uffff";
    static final String DFA40_maxS =
        "\1\110\2\uffff\1\0\12\uffff";
    static final String DFA40_acceptS =
        "\1\uffff\1\1\1\2\1\uffff\1\4\10\uffff\1\3";
    static final String DFA40_specialS =
        "\3\uffff\1\0\12\uffff}>";
    static final String[] DFA40_transitionS = {
            "\1\4\2\uffff\2\4\1\uffff\1\4\3\uffff\2\4\23\uffff\1\4\1\uffff"+
            "\1\3\1\uffff\1\4\10\uffff\1\2\1\1\14\uffff\1\4",
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
            return "196:1: unaryExpressionNotPlusMinus : ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA40_3 = input.LA(1);

                         
                        int index40_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred13_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index40_3);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 40, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA38_eotS =
        "\56\uffff";
    static final String DFA38_eofS =
        "\1\1\55\uffff";
    static final String DFA38_minS =
        "\1\16\45\uffff\1\0\7\uffff";
    static final String DFA38_maxS =
        "\1\111\45\uffff\1\0\7\uffff";
    static final String DFA38_acceptS =
        "\1\uffff\1\2\53\uffff\1\1";
    static final String DFA38_specialS =
        "\1\0\45\uffff\1\1\7\uffff}>";
    static final String[] DFA38_transitionS = {
            "\1\1\2\uffff\2\1\1\uffff\1\1\3\uffff\15\1\1\uffff\13\1\1\46"+
            "\1\1\1\uffff\2\1\1\55\14\1\5\uffff\2\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            ""
    };

    static final short[] DFA38_eot = DFA.unpackEncodedString(DFA38_eotS);
    static final short[] DFA38_eof = DFA.unpackEncodedString(DFA38_eofS);
    static final char[] DFA38_min = DFA.unpackEncodedStringToUnsignedChars(DFA38_minS);
    static final char[] DFA38_max = DFA.unpackEncodedStringToUnsignedChars(DFA38_maxS);
    static final short[] DFA38_accept = DFA.unpackEncodedString(DFA38_acceptS);
    static final short[] DFA38_special = DFA.unpackEncodedString(DFA38_specialS);
    static final short[][] DFA38_transition;

    static {
        int numStates = DFA38_transitionS.length;
        DFA38_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA38_transition[i] = DFA.unpackEncodedString(DFA38_transitionS[i]);
        }
    }

    class DFA38 extends DFA {

        public DFA38(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 38;
            this.eot = DFA38_eot;
            this.eof = DFA38_eof;
            this.min = DFA38_min;
            this.max = DFA38_max;
            this.accept = DFA38_accept;
            this.special = DFA38_special;
            this.transition = DFA38_transition;
        }
        public String getDescription() {
            return "()* loopback of 200:17: ( ( selector )=> selector )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA38_0 = input.LA(1);

                         
                        int index38_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA38_0==EOF||LA38_0==FLOAT||(LA38_0>=HEX && LA38_0<=DECIMAL)||LA38_0==STRING||(LA38_0>=BOOL && LA38_0<=INCR)||(LA38_0>=SEMICOLON && LA38_0<=RIGHT_PAREN)||LA38_0==RIGHT_SQUARE||(LA38_0>=RIGHT_CURLY && LA38_0<=COMMA)||(LA38_0>=DOUBLE_AMPER && LA38_0<=PLUS)||(LA38_0>=ID && LA38_0<=DIV)) ) {s = 1;}

                        else if ( (LA38_0==LEFT_SQUARE) ) {s = 38;}

                        else if ( (LA38_0==DOT) && (synpred14_DRLExpressions())) {s = 45;}

                         
                        input.seek(index38_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA38_38 = input.LA(1);

                         
                        int index38_38 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred14_DRLExpressions()) ) {s = 45;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index38_38);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 38, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA39_eotS =
        "\56\uffff";
    static final String DFA39_eofS =
        "\1\2\55\uffff";
    static final String DFA39_minS =
        "\1\16\1\0\31\uffff\1\0\22\uffff";
    static final String DFA39_maxS =
        "\1\111\1\0\31\uffff\1\0\22\uffff";
    static final String DFA39_acceptS =
        "\2\uffff\1\2\52\uffff\1\1";
    static final String DFA39_specialS =
        "\1\uffff\1\0\31\uffff\1\1\22\uffff}>";
    static final String[] DFA39_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\13\2\1\33\1\1\1\uffff\15"+
            "\2\1\uffff\2\2\1\uffff\14\2\5\uffff\2\2",
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
            return "200:41: ( ( INCR | DECR )=> ( INCR | DECR ) )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA39_1 = input.LA(1);

                         
                        int index39_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_DRLExpressions()) ) {s = 45;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA39_27 = input.LA(1);

                         
                        int index39_27 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_DRLExpressions()) ) {s = 45;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index39_27);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 39, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA42_eotS =
        "\12\uffff";
    static final String DFA42_eofS =
        "\12\uffff";
    static final String DFA42_minS =
        "\1\110\1\0\10\uffff";
    static final String DFA42_maxS =
        "\1\110\1\0\10\uffff";
    static final String DFA42_acceptS =
        "\2\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10";
    static final String DFA42_specialS =
        "\1\0\1\1\10\uffff}>";
    static final String[] DFA42_transitionS = {
            "\1\1",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA42_eot = DFA.unpackEncodedString(DFA42_eotS);
    static final short[] DFA42_eof = DFA.unpackEncodedString(DFA42_eofS);
    static final char[] DFA42_min = DFA.unpackEncodedStringToUnsignedChars(DFA42_minS);
    static final char[] DFA42_max = DFA.unpackEncodedStringToUnsignedChars(DFA42_maxS);
    static final short[] DFA42_accept = DFA.unpackEncodedString(DFA42_acceptS);
    static final short[] DFA42_special = DFA.unpackEncodedString(DFA42_specialS);
    static final short[][] DFA42_transition;

    static {
        int numStates = DFA42_transitionS.length;
        DFA42_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA42_transition[i] = DFA.unpackEncodedString(DFA42_transitionS[i]);
        }
    }

    class DFA42 extends DFA {

        public DFA42(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 42;
            this.eot = DFA42_eot;
            this.eof = DFA42_eof;
            this.min = DFA42_min;
            this.max = DFA42_max;
            this.accept = DFA42_accept;
            this.special = DFA42_special;
            this.transition = DFA42_transition;
        }
        public String getDescription() {
            return "208:1: primitiveType : ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA42_0 = input.LA(1);

                         
                        int index42_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA42_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))))) {s = 1;}

                         
                        input.seek(index42_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA42_1 = input.LA(1);

                         
                        int index42_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {s = 2;}

                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))) ) {s = 3;}

                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))) ) {s = 4;}

                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))) ) {s = 5;}

                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))) ) {s = 6;}

                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))) ) {s = 7;}

                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))) ) {s = 8;}

                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))) ) {s = 9;}

                         
                        input.seek(index42_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 42, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA47_eotS =
        "\21\uffff";
    static final String DFA47_eofS =
        "\21\uffff";
    static final String DFA47_minS =
        "\1\16\10\uffff\2\0\6\uffff";
    static final String DFA47_maxS =
        "\1\110\10\uffff\2\0\6\uffff";
    static final String DFA47_acceptS =
        "\1\uffff\1\1\1\2\6\3\2\uffff\1\4\1\5\1\6\1\11\1\7\1\10";
    static final String DFA47_specialS =
        "\1\0\10\uffff\1\1\1\2\6\uffff}>";
    static final String[] DFA47_transitionS = {
            "\1\6\2\uffff\1\5\1\4\1\uffff\1\3\3\uffff\1\7\1\10\23\uffff\1"+
            "\2\1\uffff\1\1\1\uffff\1\12\26\uffff\1\11",
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
            ""
    };

    static final short[] DFA47_eot = DFA.unpackEncodedString(DFA47_eotS);
    static final short[] DFA47_eof = DFA.unpackEncodedString(DFA47_eofS);
    static final char[] DFA47_min = DFA.unpackEncodedStringToUnsignedChars(DFA47_minS);
    static final char[] DFA47_max = DFA.unpackEncodedStringToUnsignedChars(DFA47_maxS);
    static final short[] DFA47_accept = DFA.unpackEncodedString(DFA47_acceptS);
    static final short[] DFA47_special = DFA.unpackEncodedString(DFA47_specialS);
    static final short[][] DFA47_transition;

    static {
        int numStates = DFA47_transitionS.length;
        DFA47_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA47_transition[i] = DFA.unpackEncodedString(DFA47_transitionS[i]);
        }
    }

    class DFA47 extends DFA {

        public DFA47(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 47;
            this.eot = DFA47_eot;
            this.eof = DFA47_eof;
            this.min = DFA47_min;
            this.max = DFA47_max;
            this.accept = DFA47_accept;
            this.special = DFA47_special;
            this.transition = DFA47_transition;
        }
        public String getDescription() {
            return "219:1: primary : ( ( parExpression )=> parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA47_0 = input.LA(1);

                         
                        int index47_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA47_0==LEFT_PAREN) && (synpred18_DRLExpressions())) {s = 1;}

                        else if ( (LA47_0==LESS) && (synpred19_DRLExpressions())) {s = 2;}

                        else if ( (LA47_0==STRING) && (synpred20_DRLExpressions())) {s = 3;}

                        else if ( (LA47_0==DECIMAL) && (synpred20_DRLExpressions())) {s = 4;}

                        else if ( (LA47_0==HEX) && (synpred20_DRLExpressions())) {s = 5;}

                        else if ( (LA47_0==FLOAT) && (synpred20_DRLExpressions())) {s = 6;}

                        else if ( (LA47_0==BOOL) && (synpred20_DRLExpressions())) {s = 7;}

                        else if ( (LA47_0==NULL) && (synpred20_DRLExpressions())) {s = 8;}

                        else if ( (LA47_0==ID) ) {s = 9;}

                        else if ( (LA47_0==LEFT_SQUARE) ) {s = 10;}

                         
                        input.seek(index47_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA47_9 = input.LA(1);

                         
                        int index47_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((synpred21_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))))) ) {s = 11;}

                        else if ( ((synpred22_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.NEW))))) ) {s = 12;}

                        else if ( (((synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INT))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))))) ) {s = 13;}

                        else if ( (synpred26_DRLExpressions()) ) {s = 14;}

                         
                        input.seek(index47_9);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA47_10 = input.LA(1);

                         
                        int index47_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred24_DRLExpressions()) ) {s = 15;}

                        else if ( (synpred25_DRLExpressions()) ) {s = 16;}

                         
                        input.seek(index47_10);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 47, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA46_eotS =
        "\57\uffff";
    static final String DFA46_eofS =
        "\1\3\56\uffff";
    static final String DFA46_minS =
        "\1\16\2\0\54\uffff";
    static final String DFA46_maxS =
        "\1\111\2\0\54\uffff";
    static final String DFA46_acceptS =
        "\3\uffff\1\2\52\uffff\1\1";
    static final String DFA46_specialS =
        "\1\uffff\1\0\1\1\54\uffff}>";
    static final String[] DFA46_transitionS = {
            "\1\3\2\uffff\2\3\1\uffff\1\3\3\uffff\15\3\1\uffff\11\3\1\2\1"+
            "\3\1\1\1\3\1\uffff\17\3\5\uffff\2\3",
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
            ""
    };

    static final short[] DFA46_eot = DFA.unpackEncodedString(DFA46_eotS);
    static final short[] DFA46_eof = DFA.unpackEncodedString(DFA46_eofS);
    static final char[] DFA46_min = DFA.unpackEncodedStringToUnsignedChars(DFA46_minS);
    static final char[] DFA46_max = DFA.unpackEncodedStringToUnsignedChars(DFA46_maxS);
    static final short[] DFA46_accept = DFA.unpackEncodedString(DFA46_acceptS);
    static final short[] DFA46_special = DFA.unpackEncodedString(DFA46_specialS);
    static final short[][] DFA46_transition;

    static {
        int numStates = DFA46_transitionS.length;
        DFA46_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA46_transition[i] = DFA.unpackEncodedString(DFA46_transitionS[i]);
        }
    }

    class DFA46 extends DFA {

        public DFA46(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 46;
            this.eot = DFA46_eot;
            this.eof = DFA46_eof;
            this.min = DFA46_min;
            this.max = DFA46_max;
            this.accept = DFA46_accept;
            this.special = DFA46_special;
            this.transition = DFA46_transition;
        }
        public String getDescription() {
            return "230:38: ( ( identifierSuffix )=> identifierSuffix )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA46_1 = input.LA(1);

                         
                        int index46_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred28_DRLExpressions()) ) {s = 46;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index46_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA46_2 = input.LA(1);

                         
                        int index46_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred28_DRLExpressions()) ) {s = 46;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index46_2);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 46, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA52_eotS =
        "\57\uffff";
    static final String DFA52_eofS =
        "\1\1\56\uffff";
    static final String DFA52_minS =
        "\1\16\45\uffff\1\0\10\uffff";
    static final String DFA52_maxS =
        "\1\111\45\uffff\1\0\10\uffff";
    static final String DFA52_acceptS =
        "\1\uffff\1\2\54\uffff\1\1";
    static final String DFA52_specialS =
        "\46\uffff\1\0\10\uffff}>";
    static final String[] DFA52_transitionS = {
            "\1\1\2\uffff\2\1\1\uffff\1\1\3\uffff\15\1\1\uffff\13\1\1\46"+
            "\1\1\1\uffff\17\1\5\uffff\2\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            ""
    };

    static final short[] DFA52_eot = DFA.unpackEncodedString(DFA52_eotS);
    static final short[] DFA52_eof = DFA.unpackEncodedString(DFA52_eofS);
    static final char[] DFA52_min = DFA.unpackEncodedStringToUnsignedChars(DFA52_minS);
    static final char[] DFA52_max = DFA.unpackEncodedStringToUnsignedChars(DFA52_maxS);
    static final short[] DFA52_accept = DFA.unpackEncodedString(DFA52_acceptS);
    static final short[] DFA52_special = DFA.unpackEncodedString(DFA52_specialS);
    static final short[][] DFA52_transition;

    static {
        int numStates = DFA52_transitionS.length;
        DFA52_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA52_transition[i] = DFA.unpackEncodedString(DFA52_transitionS[i]);
        }
    }

    class DFA52 extends DFA {

        public DFA52(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 52;
            this.eot = DFA52_eot;
            this.eof = DFA52_eof;
            this.min = DFA52_min;
            this.max = DFA52_max;
            this.accept = DFA52_accept;
            this.special = DFA52_special;
            this.transition = DFA52_transition;
        }
        public String getDescription() {
            return "()+ loopback of 255:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA52_38 = input.LA(1);

                         
                        int index52_38 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred30_DRLExpressions()) ) {s = 46;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index52_38);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 52, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA61_eotS =
        "\57\uffff";
    static final String DFA61_eofS =
        "\1\2\56\uffff";
    static final String DFA61_minS =
        "\1\16\1\0\55\uffff";
    static final String DFA61_maxS =
        "\1\111\1\0\55\uffff";
    static final String DFA61_acceptS =
        "\2\uffff\1\2\53\uffff\1\1";
    static final String DFA61_specialS =
        "\1\uffff\1\0\55\uffff}>";
    static final String[] DFA61_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\15\2\1\uffff\13\2\1\1\1"+
            "\2\1\uffff\17\2\5\uffff\2\2",
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
            return "()* loopback of 282:37: ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA61_1 = input.LA(1);

                         
                        int index61_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((!helper.validateLT(2,"]"))) ) {s = 46;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index61_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 61, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA70_eotS =
        "\57\uffff";
    static final String DFA70_eofS =
        "\1\2\56\uffff";
    static final String DFA70_minS =
        "\1\16\1\0\55\uffff";
    static final String DFA70_maxS =
        "\1\111\1\0\55\uffff";
    static final String DFA70_acceptS =
        "\2\uffff\1\2\53\uffff\1\1";
    static final String DFA70_specialS =
        "\1\uffff\1\0\55\uffff}>";
    static final String[] DFA70_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\15\2\1\uffff\11\2\1\1\3"+
            "\2\1\uffff\17\2\5\uffff\2\2",
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
            return "315:26: ( ( LEFT_PAREN )=> arguments )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA70_1 = input.LA(1);

                         
                        int index70_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred35_DRLExpressions()) ) {s = 46;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index70_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 70, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA72_eotS =
        "\57\uffff";
    static final String DFA72_eofS =
        "\1\2\56\uffff";
    static final String DFA72_minS =
        "\1\16\1\0\55\uffff";
    static final String DFA72_maxS =
        "\1\111\1\0\55\uffff";
    static final String DFA72_acceptS =
        "\2\uffff\1\2\53\uffff\1\1";
    static final String DFA72_specialS =
        "\1\uffff\1\0\55\uffff}>";
    static final String[] DFA72_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\15\2\1\uffff\11\2\1\1\3"+
            "\2\1\uffff\17\2\5\uffff\2\2",
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
            ""
    };

    static final short[] DFA72_eot = DFA.unpackEncodedString(DFA72_eotS);
    static final short[] DFA72_eof = DFA.unpackEncodedString(DFA72_eofS);
    static final char[] DFA72_min = DFA.unpackEncodedStringToUnsignedChars(DFA72_minS);
    static final char[] DFA72_max = DFA.unpackEncodedStringToUnsignedChars(DFA72_maxS);
    static final short[] DFA72_accept = DFA.unpackEncodedString(DFA72_acceptS);
    static final short[] DFA72_special = DFA.unpackEncodedString(DFA72_specialS);
    static final short[][] DFA72_transition;

    static {
        int numStates = DFA72_transitionS.length;
        DFA72_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA72_transition[i] = DFA.unpackEncodedString(DFA72_transitionS[i]);
        }
    }

    class DFA72 extends DFA {

        public DFA72(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 72;
            this.eot = DFA72_eot;
            this.eof = DFA72_eof;
            this.min = DFA72_min;
            this.max = DFA72_max;
            this.accept = DFA72_accept;
            this.special = DFA72_special;
            this.transition = DFA72_transition;
        }
        public String getDescription() {
            return "322:17: ( ( LEFT_PAREN )=> arguments )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA72_1 = input.LA(1);

                         
                        int index72_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred37_DRLExpressions()) ) {s = 46;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index72_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 72, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA77_eotS =
        "\17\uffff";
    static final String DFA77_eofS =
        "\17\uffff";
    static final String DFA77_minS =
        "\1\33\12\uffff\2\54\2\uffff";
    static final String DFA77_maxS =
        "\1\56\12\uffff\1\54\1\56\2\uffff";
    static final String DFA77_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\2\uffff\1\13"+
        "\1\14";
    static final String DFA77_specialS =
        "\14\uffff\1\0\2\uffff}>";
    static final String[] DFA77_transitionS = {
            "\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\11\uffff\1\13\1\12\1\1",
            "",
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
            "\1\15\1\uffff\1\16",
            "",
            ""
    };

    static final short[] DFA77_eot = DFA.unpackEncodedString(DFA77_eotS);
    static final short[] DFA77_eof = DFA.unpackEncodedString(DFA77_eofS);
    static final char[] DFA77_min = DFA.unpackEncodedStringToUnsignedChars(DFA77_minS);
    static final char[] DFA77_max = DFA.unpackEncodedStringToUnsignedChars(DFA77_maxS);
    static final short[] DFA77_accept = DFA.unpackEncodedString(DFA77_acceptS);
    static final short[] DFA77_special = DFA.unpackEncodedString(DFA77_specialS);
    static final short[][] DFA77_transition;

    static {
        int numStates = DFA77_transitionS.length;
        DFA77_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA77_transition[i] = DFA.unpackEncodedString(DFA77_transitionS[i]);
        }
    }

    class DFA77 extends DFA {

        public DFA77(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 77;
            this.eot = DFA77_eot;
            this.eof = DFA77_eof;
            this.min = DFA77_min;
            this.max = DFA77_max;
            this.accept = DFA77_accept;
            this.special = DFA77_special;
            this.transition = DFA77_transition;
        }
        public String getDescription() {
            return "337:1: assignmentOperator : ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN -> SHL_ASSIGN[\"<<=\"] | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN -> SHRB_ASSIGN[\">>>=\"] | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN -> SHR_ASSIGN[\">>=\"] );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA77_12 = input.LA(1);

                         
                        int index77_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA77_12==GREATER) && (synpred38_DRLExpressions())) {s = 13;}

                        else if ( (LA77_12==EQUALS_ASSIGN) && (synpred39_DRLExpressions())) {s = 14;}

                         
                        input.seek(index77_12);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 77, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_STRING_in_literal94 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DECIMAL_in_literal109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HEX_in_literal121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal137 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal151 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeList194 = new BitSet(new long[]{0x0020000000000002L});
    public static final BitSet FOLLOW_COMMA_in_typeList197 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_type_in_typeList199 = new BitSet(new long[]{0x0020000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_type227 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_type237 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_type239 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_ID_in_type253 = new BitSet(new long[]{0x0042200000000002L});
    public static final BitSet FOLLOW_typeArguments_in_type260 = new BitSet(new long[]{0x0042000000000002L});
    public static final BitSet FOLLOW_DOT_in_type265 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_type267 = new BitSet(new long[]{0x0042200000000002L});
    public static final BitSet FOLLOW_typeArguments_in_type274 = new BitSet(new long[]{0x0042000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_type289 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_type291 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_LESS_in_typeArguments312 = new BitSet(new long[]{0x0200000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments314 = new BitSet(new long[]{0x0020100000000000L});
    public static final BitSet FOLLOW_COMMA_in_typeArguments317 = new BitSet(new long[]{0x0200000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments319 = new BitSet(new long[]{0x0020100000000000L});
    public static final BitSet FOLLOW_GREATER_in_typeArguments323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument340 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_typeArgument348 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_extends_key_in_typeArgument352 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_super_key_in_typeArgument356 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_type_in_typeArgument359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_dummy383 = new BitSet(new long[]{0x0000004004000000L});
    public static final BitSet FOLLOW_set_in_dummy385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression414 = new BitSet(new long[]{0x00007007F8000002L});
    public static final BitSet FOLLOW_assignmentOperator_in_expression423 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_expression_in_expression426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression443 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_conditionalExpression447 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression450 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_COLON_in_conditionalExpression452 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression473 = new BitSet(new long[]{0x0100000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression477 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression480 = new BitSet(new long[]{0x0100000000000002L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression499 = new BitSet(new long[]{0x0080000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression503 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression506 = new BitSet(new long[]{0x0080000000000002L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression524 = new BitSet(new long[]{0x1000000000000002L});
    public static final BitSet FOLLOW_PIPE_in_inclusiveOrExpression528 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression531 = new BitSet(new long[]{0x1000000000000002L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression549 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_XOR_in_exclusiveOrExpression553 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression556 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_andOrRestriction_in_andExpression575 = new BitSet(new long[]{0x2000000000000002L});
    public static final BitSet FOLLOW_AMPER_in_andExpression579 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_andOrRestriction_in_andExpression582 = new BitSet(new long[]{0x2000000000000002L});
    public static final BitSet FOLLOW_equalityExpression_in_andOrRestriction608 = new BitSet(new long[]{0x0180000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_andOrRestriction638 = new BitSet(new long[]{0x00003F0000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_andOrRestriction642 = new BitSet(new long[]{0x00003F0000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_operator_in_andOrRestriction647 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_shiftExpressionTk_in_andOrRestriction651 = new BitSet(new long[]{0x0180000000000002L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression710 = new BitSet(new long[]{0x0000030000000002L});
    public static final BitSet FOLLOW_EQUALS_in_equalityExpression716 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_NOT_EQUALS_in_equalityExpression721 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression726 = new BitSet(new long[]{0x0000030000000002L});
    public static final BitSet FOLLOW_inExpression_in_instanceOfExpression755 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_instanceof_key_in_instanceOfExpression758 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression761 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalExpression_in_inExpression790 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_not_key_in_inExpression803 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_in_key_in_inExpression807 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_inExpression809 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_expression_in_inExpression811 = new BitSet(new long[]{0x0021000000000000L});
    public static final BitSet FOLLOW_COMMA_in_inExpression814 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_expression_in_inExpression816 = new BitSet(new long[]{0x0021000000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_inExpression820 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_in_key_in_inExpression843 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_inExpression845 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_expression_in_inExpression847 = new BitSet(new long[]{0x0021000000000000L});
    public static final BitSet FOLLOW_COMMA_in_inExpression850 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_expression_in_inExpression852 = new BitSet(new long[]{0x0021000000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_inExpression856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpressionTk_in_relationalExpression900 = new BitSet(new long[]{0x00003F0000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_relationalOp_in_relationalExpression909 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_shiftExpressionTk_in_relationalExpression912 = new BitSet(new long[]{0x00003F0000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_EQUALS_in_operator930 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_EQUALS_in_operator938 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalOp_in_operator946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_EQUALS_in_relationalOp969 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_EQUALS_in_relationalOp977 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_relationalOp986 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_relationalOp995 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_key_in_relationalOp1003 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_neg_operator_key_in_relationalOp1005 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_squareArguments_in_relationalOp1014 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operator_key_in_relationalOp1024 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_squareArguments_in_relationalOp1034 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_shiftExpressionTk1065 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression1085 = new BitSet(new long[]{0x0000300000000002L});
    public static final BitSet FOLLOW_shiftOp_in_shiftExpression1093 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression1095 = new BitSet(new long[]{0x0000300000000002L});
    public static final BitSet FOLLOW_LESS_in_shiftOp1116 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_LESS_in_shiftOp1118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp1122 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp1124 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp1126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp1130 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp1132 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression1151 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000006L});
    public static final BitSet FOLLOW_set_in_additiveExpression1162 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression1170 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000006L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression1190 = new BitSet(new long[]{0x8000000000000002L,0x0000000000000201L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression1194 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression1208 = new BitSet(new long[]{0x8000000000000002L,0x0000000000000201L});
    public static final BitSet FOLLOW_PLUS_in_unaryExpression1230 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression1232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unaryExpression1240 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression1242 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INCR_in_unaryExpression1252 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_primary_in_unaryExpression1254 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DECR_in_unaryExpression1264 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_primary_in_unaryExpression1266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression1276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_unaryExpressionNotPlusMinus1295 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus1297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus1306 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus1308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus1322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus1332 = new BitSet(new long[]{0x0042001800000002L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus1339 = new BitSet(new long[]{0x0042001800000002L});
    public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus1351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression1387 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression1389 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression1391 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression1393 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression1410 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_type_in_castExpression1412 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression1414 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression1416 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolean_key_in_primitiveType1437 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_char_key_in_primitiveType1445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_byte_key_in_primitiveType1453 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_short_key_in_primitiveType1461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_int_key_in_primitiveType1469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_long_key_in_primitiveType1477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_float_key_in_primitiveType1485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_double_key_in_primitiveType1493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary1515 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_primary1530 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_explicitGenericInvocationSuffix_in_primary1533 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_this_key_in_primary1537 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_arguments_in_primary1539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary1555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_primary1575 = new BitSet(new long[]{0x0040800000000000L});
    public static final BitSet FOLLOW_superSuffix_in_primary1577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_new_key_in_primary1592 = new BitSet(new long[]{0x0000200000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_creator_in_primary1594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary1609 = new BitSet(new long[]{0x0042000000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_primary1612 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_primary1614 = new BitSet(new long[]{0x0042000000000000L});
    public static final BitSet FOLLOW_DOT_in_primary1618 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_class_key_in_primary1620 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineMapExpression_in_primary1640 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineListExpression_in_primary1655 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_primary1669 = new BitSet(new long[]{0x0042800000000002L});
    public static final BitSet FOLLOW_DOT_in_primary1678 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_primary1680 = new BitSet(new long[]{0x0042800000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary1689 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineListExpression1710 = new BitSet(new long[]{0x0C06A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_expressionList_in_inlineListExpression1712 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineListExpression1715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineMapExpression1737 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_mapExpressionList_in_inlineMapExpression1739 = new BitSet(new long[]{0x0C06A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineMapExpression1742 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mapEntry_in_mapExpressionList1759 = new BitSet(new long[]{0x0020000000000002L});
    public static final BitSet FOLLOW_COMMA_in_mapExpressionList1762 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_mapEntry_in_mapExpressionList1764 = new BitSet(new long[]{0x0020000000000002L});
    public static final BitSet FOLLOW_expression_in_mapEntry1787 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_COLON_in_mapEntry1789 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_expression_in_mapEntry1791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_parExpression1808 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_expression_in_parExpression1810 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_parExpression1812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix1836 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix1838 = new BitSet(new long[]{0x0042000000000000L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix1842 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_class_key_in_identifierSuffix1844 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix1859 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_expression_in_identifierSuffix1861 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix1863 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix1876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator1899 = new BitSet(new long[]{0x0000200000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_createdName_in_creator1902 = new BitSet(new long[]{0x0002800000000000L});
    public static final BitSet FOLLOW_arrayCreatorRest_in_creator1913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator1917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_createdName1935 = new BitSet(new long[]{0x0040200000000002L});
    public static final BitSet FOLLOW_typeArguments_in_createdName1937 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_DOT_in_createdName1950 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_createdName1952 = new BitSet(new long[]{0x0040200000000002L});
    public static final BitSet FOLLOW_typeArguments_in_createdName1954 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName1969 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_innerCreator1989 = new BitSet(new long[]{0x0002800000000000L});
    public static final BitSet FOLLOW_classCreatorRest_in_innerCreator1991 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2010 = new BitSet(new long[]{0x0C06A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2020 = new BitSet(new long[]{0x000A000000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2023 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2025 = new BitSet(new long[]{0x000A000000000000L});
    public static final BitSet FOLLOW_arrayInitializer_in_arrayCreatorRest2029 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest2043 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2045 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2050 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest2052 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2054 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2066 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2068 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer2097 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer2111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_arrayInitializer2128 = new BitSet(new long[]{0x0C1AA01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer2131 = new BitSet(new long[]{0x0030000000000000L});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer2134 = new BitSet(new long[]{0x0C0AA01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer2136 = new BitSet(new long[]{0x0030000000000000L});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer2141 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_arrayInitializer2148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest2165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation2183 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocation2185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_nonWildcardTypeArguments2202 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments2204 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_GREATER_in_nonWildcardTypeArguments2206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_explicitGenericInvocationSuffix2223 = new BitSet(new long[]{0x0040800000000000L});
    public static final BitSet FOLLOW_superSuffix_in_explicitGenericInvocationSuffix2225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_explicitGenericInvocationSuffix2236 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocationSuffix2238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector2263 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_super_key_in_selector2265 = new BitSet(new long[]{0x0040800000000000L});
    public static final BitSet FOLLOW_superSuffix_in_selector2267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector2283 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_new_key_in_selector2285 = new BitSet(new long[]{0x0000200000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_selector2288 = new BitSet(new long[]{0x0000200000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_innerCreator_in_selector2292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector2308 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_selector2310 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_arguments_in_selector2319 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_selector2340 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_expression_in_selector2342 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_selector2344 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix2361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_superSuffix2372 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_superSuffix2374 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix2383 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_squareArguments2406 = new BitSet(new long[]{0x0C06A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_expressionList_in_squareArguments2408 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_squareArguments2411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_arguments2428 = new BitSet(new long[]{0x0C03A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_expressionList_in_arguments2430 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_arguments2433 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList2450 = new BitSet(new long[]{0x0020000000000002L});
    public static final BitSet FOLLOW_COMMA_in_expressionList2453 = new BitSet(new long[]{0x0C02A01803164000L,0x0000000000000106L});
    public static final BitSet FOLLOW_expression_in_expressionList2455 = new BitSet(new long[]{0x0020000000000002L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2474 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_assignmentOperator2482 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_ASSIGN_in_assignmentOperator2490 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MULT_ASSIGN_in_assignmentOperator2498 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIV_ASSIGN_in_assignmentOperator2506 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_ASSIGN_in_assignmentOperator2514 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_ASSIGN_in_assignmentOperator2522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_XOR_ASSIGN_in_assignmentOperator2530 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_ASSIGN_in_assignmentOperator2538 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_assignmentOperator2546 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_LESS_in_assignmentOperator2548 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2550 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2572 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2574 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2576 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2599 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2601 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2603 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_extends_key2639 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_super_key2666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_instanceof_key2693 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_boolean_key2725 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_char_key2752 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_byte_key2779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_short_key2806 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_int_key2833 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_float_key2860 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_long_key2887 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_double_key2914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_void_key2941 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_this_key2968 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_class_key2995 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_new_key3022 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_not_key3049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_in_key3074 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_operator_key3102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_neg_operator_key3130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_synpred1_DRLExpressions220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred2_DRLExpressions231 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred2_DRLExpressions233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeArguments_in_synpred3_DRLExpressions257 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeArguments_in_synpred4_DRLExpressions271 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred5_DRLExpressions283 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred5_DRLExpressions285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignmentOperator_in_synpred6_DRLExpressions418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred7_DRLExpressions626 = new BitSet(new long[]{0x00003F0000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_operator_in_synpred7_DRLExpressions632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalOp_in_synpred8_DRLExpressions905 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_squareArguments_in_synpred9_DRLExpressions1010 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_squareArguments_in_synpred10_DRLExpressions1030 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftOp_in_synpred11_DRLExpressions1090 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred12_DRLExpressions1155 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_synpred13_DRLExpressions1319 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selector_in_synpred14_DRLExpressions1336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred15_DRLExpressions1344 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred16_DRLExpressions1380 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_primitiveType_in_synpred16_DRLExpressions1382 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred17_DRLExpressions1403 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_type_in_synpred17_DRLExpressions1405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_synpred18_DRLExpressions1511 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred19_DRLExpressions1526 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_synpred20_DRLExpressions1551 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_synpred21_DRLExpressions1571 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_new_key_in_synpred22_DRLExpressions1588 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_synpred23_DRLExpressions1605 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineMapExpression_in_synpred24_DRLExpressions1636 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineListExpression_in_synpred25_DRLExpressions1651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_synpred26_DRLExpressions1666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred27_DRLExpressions1673 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_synpred27_DRLExpressions1675 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred28_DRLExpressions1686 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred29_DRLExpressions1830 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred29_DRLExpressions1832 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred30_DRLExpressions1854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred31_DRLExpressions2060 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred31_DRLExpressions2062 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred32_DRLExpressions2258 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_super_key_in_synpred32_DRLExpressions2260 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred33_DRLExpressions2278 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_new_key_in_synpred33_DRLExpressions2280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred34_DRLExpressions2303 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_synpred34_DRLExpressions2305 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred35_DRLExpressions2314 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred36_DRLExpressions2337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred37_DRLExpressions2378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_synpred38_DRLExpressions2564 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred38_DRLExpressions2566 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred38_DRLExpressions2568 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_synpred39_DRLExpressions2593 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred39_DRLExpressions2595 = new BitSet(new long[]{0x0000000000000002L});

}