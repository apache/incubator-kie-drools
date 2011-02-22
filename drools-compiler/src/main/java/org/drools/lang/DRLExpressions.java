// $ANTLR 3.3 Nov 30, 2010 12:45:30 /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g 2011-02-22 17:11:37

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "OPERATOR", "NEG_OPERATOR", "SHIFT_EXPR", "EOL", "WS", "Exponent", "FloatTypeSuffix", "FLOAT", "HexDigit", "IntegerTypeSuffix", "HEX", "DECIMAL", "EscapeSequence", "STRING", "TimePeriod", "UnicodeEscape", "OctalEscape", "BOOL", "NULL", "AT", "PLUS_ASSIGN", "MINUS_ASSIGN", "MULT_ASSIGN", "DIV_ASSIGN", "AND_ASSIGN", "OR_ASSIGN", "XOR_ASSIGN", "MOD_ASSIGN", "DECR", "INCR", "ARROW", "SEMICOLON", "COLON", "EQUALS", "NOT_EQUALS", "GREATER_EQUALS", "LESS_EQUALS", "GREATER", "LESS", "EQUALS_ASSIGN", "LEFT_PAREN", "RIGHT_PAREN", "LEFT_SQUARE", "RIGHT_SQUARE", "LEFT_CURLY", "RIGHT_CURLY", "COMMA", "DOT", "DOUBLE_AMPER", "DOUBLE_PIPE", "QUESTION", "NEGATION", "TILDE", "PIPE", "AMPER", "XOR", "MOD", "STAR", "MINUS", "PLUS", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "IdentifierStart", "IdentifierPart", "ID", "DIV", "MISC"
    };
    public static final int EOF=-1;
    public static final int OPERATOR=4;
    public static final int NEG_OPERATOR=5;
    public static final int SHIFT_EXPR=6;
    public static final int EOL=7;
    public static final int WS=8;
    public static final int Exponent=9;
    public static final int FloatTypeSuffix=10;
    public static final int FLOAT=11;
    public static final int HexDigit=12;
    public static final int IntegerTypeSuffix=13;
    public static final int HEX=14;
    public static final int DECIMAL=15;
    public static final int EscapeSequence=16;
    public static final int STRING=17;
    public static final int TimePeriod=18;
    public static final int UnicodeEscape=19;
    public static final int OctalEscape=20;
    public static final int BOOL=21;
    public static final int NULL=22;
    public static final int AT=23;
    public static final int PLUS_ASSIGN=24;
    public static final int MINUS_ASSIGN=25;
    public static final int MULT_ASSIGN=26;
    public static final int DIV_ASSIGN=27;
    public static final int AND_ASSIGN=28;
    public static final int OR_ASSIGN=29;
    public static final int XOR_ASSIGN=30;
    public static final int MOD_ASSIGN=31;
    public static final int DECR=32;
    public static final int INCR=33;
    public static final int ARROW=34;
    public static final int SEMICOLON=35;
    public static final int COLON=36;
    public static final int EQUALS=37;
    public static final int NOT_EQUALS=38;
    public static final int GREATER_EQUALS=39;
    public static final int LESS_EQUALS=40;
    public static final int GREATER=41;
    public static final int LESS=42;
    public static final int EQUALS_ASSIGN=43;
    public static final int LEFT_PAREN=44;
    public static final int RIGHT_PAREN=45;
    public static final int LEFT_SQUARE=46;
    public static final int RIGHT_SQUARE=47;
    public static final int LEFT_CURLY=48;
    public static final int RIGHT_CURLY=49;
    public static final int COMMA=50;
    public static final int DOT=51;
    public static final int DOUBLE_AMPER=52;
    public static final int DOUBLE_PIPE=53;
    public static final int QUESTION=54;
    public static final int NEGATION=55;
    public static final int TILDE=56;
    public static final int PIPE=57;
    public static final int AMPER=58;
    public static final int XOR=59;
    public static final int MOD=60;
    public static final int STAR=61;
    public static final int MINUS=62;
    public static final int PLUS=63;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=64;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=65;
    public static final int MULTI_LINE_COMMENT=66;
    public static final int IdentifierStart=67;
    public static final int IdentifierPart=68;
    public static final int ID=69;
    public static final int DIV=70;
    public static final int MISC=71;

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

                if ( (((synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INT))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.LONG))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))))) ) {
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:89:1: dummy : expression ( AT | SEMICOLON ) ;
    public final DRLExpressions.dummy_return dummy() throws RecognitionException {
        DRLExpressions.dummy_return retval = new DRLExpressions.dummy_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set31=null;
        DRLExpressions.expression_return expression30 = null;


        Object set31_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:90:5: ( expression ( AT | SEMICOLON ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:90:7: expression ( AT | SEMICOLON )
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression_in_dummy383);
            expression30=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression30.getTree());
            set31=(Token)input.LT(1);
            if ( input.LA(1)==AT||input.LA(1)==SEMICOLON ) {
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

            pushFollow(FOLLOW_conditionalExpression_in_expression411);
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
                    pushFollow(FOLLOW_assignmentOperator_in_expression420);
                    assignmentOperator33=assignmentOperator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(assignmentOperator33.getTree(), root_0);
                    pushFollow(FOLLOW_expression_in_expression423);
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

            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression440);
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
                    QUESTION36=(Token)match(input,QUESTION,FOLLOW_QUESTION_in_conditionalExpression444); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    QUESTION36_tree = (Object)adaptor.create(QUESTION36);
                    root_0 = (Object)adaptor.becomeRoot(QUESTION36_tree, root_0);
                    }
                    pushFollow(FOLLOW_expression_in_conditionalExpression447);
                    expression37=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression37.getTree());
                    COLON38=(Token)match(input,COLON,FOLLOW_COLON_in_conditionalExpression449); if (state.failed) return retval;
                    pushFollow(FOLLOW_expression_in_conditionalExpression452);
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

            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression470);
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
            	    DOUBLE_PIPE41=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression474); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    DOUBLE_PIPE41_tree = (Object)adaptor.create(DOUBLE_PIPE41);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_PIPE41_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression477);
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

            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression496);
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
            	    DOUBLE_AMPER44=(Token)match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression500); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    DOUBLE_AMPER44_tree = (Object)adaptor.create(DOUBLE_AMPER44);
            	    root_0 = (Object)adaptor.becomeRoot(DOUBLE_AMPER44_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression503);
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

            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression521);
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
            	    PIPE47=(Token)match(input,PIPE,FOLLOW_PIPE_in_inclusiveOrExpression525); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    PIPE47_tree = (Object)adaptor.create(PIPE47);
            	    root_0 = (Object)adaptor.becomeRoot(PIPE47_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression528);
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

            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression546);
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
            	    XOR50=(Token)match(input,XOR,FOLLOW_XOR_in_exclusiveOrExpression550); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    XOR50_tree = (Object)adaptor.create(XOR50);
            	    root_0 = (Object)adaptor.becomeRoot(XOR50_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression553);
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

            pushFollow(FOLLOW_andOrRestriction_in_andExpression572);
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
            	    AMPER53=(Token)match(input,AMPER,FOLLOW_AMPER_in_andExpression576); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    AMPER53_tree = (Object)adaptor.create(AMPER53);
            	    root_0 = (Object)adaptor.becomeRoot(AMPER53_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_andOrRestriction_in_andExpression579);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:122:1: andOrRestriction : (ee= equalityExpression -> $ee) ( ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER ) op= operator se2= shiftExpression ) -> ^( $lop $andOrRestriction ^( $op $se2) ) )* ;
    public final DRLExpressions.andOrRestriction_return andOrRestriction() throws RecognitionException {
        DRLExpressions.andOrRestriction_return retval = new DRLExpressions.andOrRestriction_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token lop=null;
        DRLExpressions.equalityExpression_return ee = null;

        DRLExpressions.operator_return op = null;

        DRLExpressions.shiftExpression_return se2 = null;


        Object lop_tree=null;
        RewriteRuleTokenStream stream_DOUBLE_PIPE=new RewriteRuleTokenStream(adaptor,"token DOUBLE_PIPE");
        RewriteRuleTokenStream stream_DOUBLE_AMPER=new RewriteRuleTokenStream(adaptor,"token DOUBLE_AMPER");
        RewriteRuleSubtreeStream stream_equalityExpression=new RewriteRuleSubtreeStream(adaptor,"rule equalityExpression");
        RewriteRuleSubtreeStream stream_shiftExpression=new RewriteRuleSubtreeStream(adaptor,"rule shiftExpression");
        RewriteRuleSubtreeStream stream_operator=new RewriteRuleSubtreeStream(adaptor,"rule operator");
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:123:4: ( (ee= equalityExpression -> $ee) ( ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER ) op= operator se2= shiftExpression ) -> ^( $lop $andOrRestriction ^( $op $se2) ) )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:123:6: (ee= equalityExpression -> $ee) ( ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER ) op= operator se2= shiftExpression ) -> ^( $lop $andOrRestriction ^( $op $se2) ) )*
            {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:123:6: (ee= equalityExpression -> $ee)
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:123:7: ee= equalityExpression
            {
            pushFollow(FOLLOW_equalityExpression_in_andOrRestriction605);
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

            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:124:6: ( ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER ) op= operator se2= shiftExpression ) -> ^( $lop $andOrRestriction ^( $op $se2) ) )*
            loop21:
            do {
                int alt21=2;
                alt21 = dfa21.predict(input);
                switch (alt21) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:124:7: ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER ) op= operator se2= shiftExpression )
            	    {
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:124:7: ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER ) op= operator se2= shiftExpression )
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:124:9: ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER ) op= operator se2= shiftExpression
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
            	            lop=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_andOrRestriction635); if (state.failed) return retval; 
            	            if ( state.backtracking==0 ) stream_DOUBLE_PIPE.add(lop);


            	            }
            	            break;
            	        case 2 :
            	            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:124:65: lop= DOUBLE_AMPER
            	            {
            	            lop=(Token)match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_andOrRestriction639); if (state.failed) return retval; 
            	            if ( state.backtracking==0 ) stream_DOUBLE_AMPER.add(lop);


            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_operator_in_andOrRestriction644);
            	    op=operator();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_operator.add(op.getTree());
            	    pushFollow(FOLLOW_shiftExpression_in_andOrRestriction648);
            	    se2=shiftExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_shiftExpression.add(se2.getTree());

            	    }



            	    // AST REWRITE
            	    // elements: andOrRestriction, se2, op, lop
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

            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression707);
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
            	            EQUALS55=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_equalityExpression713); if (state.failed) return retval;
            	            if ( state.backtracking==0 ) {
            	            EQUALS55_tree = (Object)adaptor.create(EQUALS55);
            	            root_0 = (Object)adaptor.becomeRoot(EQUALS55_tree, root_0);
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:130:43: NOT_EQUALS
            	            {
            	            NOT_EQUALS56=(Token)match(input,NOT_EQUALS,FOLLOW_NOT_EQUALS_in_equalityExpression718); if (state.failed) return retval;
            	            if ( state.backtracking==0 ) {
            	            NOT_EQUALS56_tree = (Object)adaptor.create(NOT_EQUALS56);
            	            root_0 = (Object)adaptor.becomeRoot(NOT_EQUALS56_tree, root_0);
            	            }

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression723);
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

            pushFollow(FOLLOW_inExpression_in_instanceOfExpression752);
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
                    pushFollow(FOLLOW_instanceof_key_in_instanceOfExpression755);
                    instanceof_key58=instanceof_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(instanceof_key58.getTree(), root_0);
                    pushFollow(FOLLOW_type_in_instanceOfExpression758);
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
            pushFollow(FOLLOW_relationalExpression_in_inExpression787);
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
                    pushFollow(FOLLOW_not_key_in_inExpression800);
                    not_key60=not_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_not_key.add(not_key60.getTree());
                    pushFollow(FOLLOW_in_key_in_inExpression804);
                    in=in_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_in_key.add(in.getTree());
                    LEFT_PAREN61=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_inExpression806); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN61);

                    pushFollow(FOLLOW_expression_in_inExpression808);
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
                    	    COMMA63=(Token)match(input,COMMA,FOLLOW_COMMA_in_inExpression811); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA63);

                    	    pushFollow(FOLLOW_expression_in_inExpression813);
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

                    RIGHT_PAREN65=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_inExpression817); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RIGHT_PAREN.add(RIGHT_PAREN65);



                    // AST REWRITE
                    // elements: rel, expression
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
                    pushFollow(FOLLOW_in_key_in_inExpression840);
                    in=in_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_in_key.add(in.getTree());
                    LEFT_PAREN66=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_inExpression842); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEFT_PAREN.add(LEFT_PAREN66);

                    pushFollow(FOLLOW_expression_in_inExpression844);
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
                    	    COMMA68=(Token)match(input,COMMA,FOLLOW_COMMA_in_inExpression847); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA68);

                    	    pushFollow(FOLLOW_expression_in_inExpression849);
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

                    RIGHT_PAREN70=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_inExpression853); if (state.failed) return retval; 
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:146:1: relationalExpression returns [CommonTree se1] : se= shiftExpression ( ( relationalOp )=> relationalOp shiftExpression )* ;
    public final DRLExpressions.relationalExpression_return relationalExpression() throws RecognitionException {
        DRLExpressions.relationalExpression_return retval = new DRLExpressions.relationalExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLExpressions.shiftExpression_return se = null;

        DRLExpressions.relationalOp_return relationalOp71 = null;

        DRLExpressions.shiftExpression_return shiftExpression72 = null;



        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:148:3: (se= shiftExpression ( ( relationalOp )=> relationalOp shiftExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:148:5: se= shiftExpression ( ( relationalOp )=> relationalOp shiftExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_shiftExpression_in_relationalExpression897);
            se=shiftExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, se.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:148:24: ( ( relationalOp )=> relationalOp shiftExpression )*
            loop28:
            do {
                int alt28=2;
                alt28 = dfa28.predict(input);
                switch (alt28) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:148:26: ( relationalOp )=> relationalOp shiftExpression
            	    {
            	    pushFollow(FOLLOW_relationalOp_in_relationalExpression906);
            	    relationalOp71=relationalOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) root_0 = (Object)adaptor.becomeRoot(relationalOp71.getTree(), root_0);
            	    pushFollow(FOLLOW_shiftExpression_in_relationalExpression909);
            	    shiftExpression72=shiftExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpression72.getTree());

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
                    EQUALS73=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_operator927); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQUALS73_tree = (Object)adaptor.create(EQUALS73);
                    adaptor.addChild(root_0, EQUALS73_tree);
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:153:7: NOT_EQUALS
                    {
                    NOT_EQUALS74=(Token)match(input,NOT_EQUALS,FOLLOW_NOT_EQUALS_in_operator935); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NOT_EQUALS74_tree = (Object)adaptor.create(NOT_EQUALS74);
                    adaptor.addChild(root_0, NOT_EQUALS74_tree);
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:154:7: relationalOp
                    {
                    pushFollow(FOLLOW_relationalOp_in_operator943);
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
                    LESS_EQUALS76=(Token)match(input,LESS_EQUALS,FOLLOW_LESS_EQUALS_in_relationalOp966); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESS_EQUALS76_tree = (Object)adaptor.create(LESS_EQUALS76);
                    adaptor.addChild(root_0, LESS_EQUALS76_tree);
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:160:7: GREATER_EQUALS
                    {
                    GREATER_EQUALS77=(Token)match(input,GREATER_EQUALS,FOLLOW_GREATER_EQUALS_in_relationalOp974); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER_EQUALS77_tree = (Object)adaptor.create(GREATER_EQUALS77);
                    adaptor.addChild(root_0, GREATER_EQUALS77_tree);
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:161:7: LESS
                    {
                    LESS78=(Token)match(input,LESS,FOLLOW_LESS_in_relationalOp983); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESS78_tree = (Object)adaptor.create(LESS78);
                    adaptor.addChild(root_0, LESS78_tree);
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:162:7: GREATER
                    {
                    GREATER79=(Token)match(input,GREATER,FOLLOW_GREATER_in_relationalOp992); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER79_tree = (Object)adaptor.create(GREATER79);
                    adaptor.addChild(root_0, GREATER79_tree);
                    }

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:163:7: not_key neg_operator_key ( ( squareArguments )=> squareArguments )?
                    {
                    pushFollow(FOLLOW_not_key_in_relationalOp1000);
                    not_key80=not_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, not_key80.getTree());
                    pushFollow(FOLLOW_neg_operator_key_in_relationalOp1002);
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
                            pushFollow(FOLLOW_squareArguments_in_relationalOp1011);
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
                    pushFollow(FOLLOW_operator_key_in_relationalOp1021);
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
                            pushFollow(FOLLOW_squareArguments_in_relationalOp1031);
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

    public static class shiftExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "shiftExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:168:1: shiftExpression : ad1= additiveExpression ( ( shiftOp )=>so= shiftOp ad2= additiveExpression )* -> ^( SHIFT_EXPR $ad1 ( $so $ad2)* ) ;
    public final DRLExpressions.shiftExpression_return shiftExpression() throws RecognitionException {
        DRLExpressions.shiftExpression_return retval = new DRLExpressions.shiftExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLExpressions.additiveExpression_return ad1 = null;

        DRLExpressions.shiftOp_return so = null;

        DRLExpressions.additiveExpression_return ad2 = null;


        RewriteRuleSubtreeStream stream_shiftOp=new RewriteRuleSubtreeStream(adaptor,"rule shiftOp");
        RewriteRuleSubtreeStream stream_additiveExpression=new RewriteRuleSubtreeStream(adaptor,"rule additiveExpression");
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:169:3: (ad1= additiveExpression ( ( shiftOp )=>so= shiftOp ad2= additiveExpression )* -> ^( SHIFT_EXPR $ad1 ( $so $ad2)* ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:169:5: ad1= additiveExpression ( ( shiftOp )=>so= shiftOp ad2= additiveExpression )*
            {
            pushFollow(FOLLOW_additiveExpression_in_shiftExpression1056);
            ad1=additiveExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_additiveExpression.add(ad1.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:169:28: ( ( shiftOp )=>so= shiftOp ad2= additiveExpression )*
            loop33:
            do {
                int alt33=2;
                alt33 = dfa33.predict(input);
                switch (alt33) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:169:30: ( shiftOp )=>so= shiftOp ad2= additiveExpression
            	    {
            	    pushFollow(FOLLOW_shiftOp_in_shiftExpression1066);
            	    so=shiftOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_shiftOp.add(so.getTree());
            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression1070);
            	    ad2=additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_additiveExpression.add(ad2.getTree());

            	    }
            	    break;

            	default :
            	    break loop33;
                }
            } while (true);



            // AST REWRITE
            // elements: ad2, ad1, so
            // token labels: 
            // rule labels: retval, so, ad1, ad2
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_so=new RewriteRuleSubtreeStream(adaptor,"rule so",so!=null?so.tree:null);
            RewriteRuleSubtreeStream stream_ad1=new RewriteRuleSubtreeStream(adaptor,"rule ad1",ad1!=null?ad1.tree:null);
            RewriteRuleSubtreeStream stream_ad2=new RewriteRuleSubtreeStream(adaptor,"rule ad2",ad2!=null?ad2.tree:null);

            root_0 = (Object)adaptor.nil();
            // 170:5: -> ^( SHIFT_EXPR $ad1 ( $so $ad2)* )
            {
                // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:170:8: ^( SHIFT_EXPR $ad1 ( $so $ad2)* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SHIFT_EXPR, "SHIFT_EXPR"), root_1);

                adaptor.addChild(root_1, stream_ad1.nextTree());
                // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:170:26: ( $so $ad2)*
                while ( stream_ad2.hasNext()||stream_so.hasNext() ) {
                    adaptor.addChild(root_1, stream_so.nextTree());
                    adaptor.addChild(root_1, stream_ad2.nextTree());

                }
                stream_ad2.reset();
                stream_so.reset();

                adaptor.addChild(root_0, root_1);
                }

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
    // $ANTLR end "shiftExpression"

    public static class shiftOp_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "shiftOp"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:173:1: shiftOp : ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER ) ;
    public final DRLExpressions.shiftOp_return shiftOp() throws RecognitionException {
        DRLExpressions.shiftOp_return retval = new DRLExpressions.shiftOp_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LESS85=null;
        Token LESS86=null;
        Token GREATER87=null;
        Token GREATER88=null;
        Token GREATER89=null;
        Token GREATER90=null;
        Token GREATER91=null;

        Object LESS85_tree=null;
        Object LESS86_tree=null;
        Object GREATER87_tree=null;
        Object GREATER88_tree=null;
        Object GREATER89_tree=null;
        Object GREATER90_tree=null;
        Object GREATER91_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:174:5: ( ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:174:7: ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
            {
            root_0 = (Object)adaptor.nil();

            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:174:7: ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
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
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:174:8: LESS LESS
                    {
                    LESS85=(Token)match(input,LESS,FOLLOW_LESS_in_shiftOp1114); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESS85_tree = (Object)adaptor.create(LESS85);
                    adaptor.addChild(root_0, LESS85_tree);
                    }
                    LESS86=(Token)match(input,LESS,FOLLOW_LESS_in_shiftOp1116); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESS86_tree = (Object)adaptor.create(LESS86);
                    adaptor.addChild(root_0, LESS86_tree);
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:174:20: GREATER GREATER GREATER
                    {
                    GREATER87=(Token)match(input,GREATER,FOLLOW_GREATER_in_shiftOp1120); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER87_tree = (Object)adaptor.create(GREATER87);
                    adaptor.addChild(root_0, GREATER87_tree);
                    }
                    GREATER88=(Token)match(input,GREATER,FOLLOW_GREATER_in_shiftOp1122); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER88_tree = (Object)adaptor.create(GREATER88);
                    adaptor.addChild(root_0, GREATER88_tree);
                    }
                    GREATER89=(Token)match(input,GREATER,FOLLOW_GREATER_in_shiftOp1124); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER89_tree = (Object)adaptor.create(GREATER89);
                    adaptor.addChild(root_0, GREATER89_tree);
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:174:46: GREATER GREATER
                    {
                    GREATER90=(Token)match(input,GREATER,FOLLOW_GREATER_in_shiftOp1128); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER90_tree = (Object)adaptor.create(GREATER90);
                    adaptor.addChild(root_0, GREATER90_tree);
                    }
                    GREATER91=(Token)match(input,GREATER,FOLLOW_GREATER_in_shiftOp1130); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER91_tree = (Object)adaptor.create(GREATER91);
                    adaptor.addChild(root_0, GREATER91_tree);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:177:1: additiveExpression : multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )* ;
    public final DRLExpressions.additiveExpression_return additiveExpression() throws RecognitionException {
        DRLExpressions.additiveExpression_return retval = new DRLExpressions.additiveExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set93=null;
        DRLExpressions.multiplicativeExpression_return multiplicativeExpression92 = null;

        DRLExpressions.multiplicativeExpression_return multiplicativeExpression94 = null;


        Object set93_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:178:3: ( multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:178:7: multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression1149);
            multiplicativeExpression92=multiplicativeExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression92.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:178:32: ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*
            loop35:
            do {
                int alt35=2;
                alt35 = dfa35.predict(input);
                switch (alt35) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:178:34: ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression
            	    {
            	    set93=(Token)input.LT(1);
            	    if ( (input.LA(1)>=MINUS && input.LA(1)<=PLUS) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set93));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression1168);
            	    multiplicativeExpression94=multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression94.getTree());

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:181:1: multiplicativeExpression : unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )* ;
    public final DRLExpressions.multiplicativeExpression_return multiplicativeExpression() throws RecognitionException {
        DRLExpressions.multiplicativeExpression_return retval = new DRLExpressions.multiplicativeExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set96=null;
        DRLExpressions.unaryExpression_return unaryExpression95 = null;

        DRLExpressions.unaryExpression_return unaryExpression97 = null;


        Object set96_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:182:3: ( unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:182:7: unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression1188);
            unaryExpression95=unaryExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression95.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:182:23: ( ( STAR | DIV | MOD ) unaryExpression )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( ((LA36_0>=MOD && LA36_0<=STAR)||LA36_0==DIV) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:182:25: ( STAR | DIV | MOD ) unaryExpression
            	    {
            	    set96=(Token)input.LT(1);
            	    if ( (input.LA(1)>=MOD && input.LA(1)<=STAR)||input.LA(1)==DIV ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set96));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression1206);
            	    unaryExpression97=unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression97.getTree());

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:185:1: unaryExpression : ( PLUS unaryExpression | MINUS unaryExpression | INCR primary | DECR primary | unaryExpressionNotPlusMinus );
    public final DRLExpressions.unaryExpression_return unaryExpression() throws RecognitionException {
        DRLExpressions.unaryExpression_return retval = new DRLExpressions.unaryExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PLUS98=null;
        Token MINUS100=null;
        Token INCR102=null;
        Token DECR104=null;
        DRLExpressions.unaryExpression_return unaryExpression99 = null;

        DRLExpressions.unaryExpression_return unaryExpression101 = null;

        DRLExpressions.primary_return primary103 = null;

        DRLExpressions.primary_return primary105 = null;

        DRLExpressions.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus106 = null;


        Object PLUS98_tree=null;
        Object MINUS100_tree=null;
        Object INCR102_tree=null;
        Object DECR104_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:186:5: ( PLUS unaryExpression | MINUS unaryExpression | INCR primary | DECR primary | unaryExpressionNotPlusMinus )
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
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:186:9: PLUS unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    PLUS98=(Token)match(input,PLUS,FOLLOW_PLUS_in_unaryExpression1228); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    PLUS98_tree = (Object)adaptor.create(PLUS98);
                    adaptor.addChild(root_0, PLUS98_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression1230);
                    unaryExpression99=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression99.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:187:7: MINUS unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    MINUS100=(Token)match(input,MINUS,FOLLOW_MINUS_in_unaryExpression1238); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MINUS100_tree = (Object)adaptor.create(MINUS100);
                    adaptor.addChild(root_0, MINUS100_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression1240);
                    unaryExpression101=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression101.getTree());

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:188:9: INCR primary
                    {
                    root_0 = (Object)adaptor.nil();

                    INCR102=(Token)match(input,INCR,FOLLOW_INCR_in_unaryExpression1250); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INCR102_tree = (Object)adaptor.create(INCR102);
                    adaptor.addChild(root_0, INCR102_tree);
                    }
                    pushFollow(FOLLOW_primary_in_unaryExpression1252);
                    primary103=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primary103.getTree());

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:189:9: DECR primary
                    {
                    root_0 = (Object)adaptor.nil();

                    DECR104=(Token)match(input,DECR,FOLLOW_DECR_in_unaryExpression1262); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DECR104_tree = (Object)adaptor.create(DECR104);
                    adaptor.addChild(root_0, DECR104_tree);
                    }
                    pushFollow(FOLLOW_primary_in_unaryExpression1264);
                    primary105=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primary105.getTree());

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:190:9: unaryExpressionNotPlusMinus
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression1274);
                    unaryExpressionNotPlusMinus106=unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpressionNotPlusMinus106.getTree());

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:193:1: unaryExpressionNotPlusMinus : ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? );
    public final DRLExpressions.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus() throws RecognitionException {
        DRLExpressions.unaryExpressionNotPlusMinus_return retval = new DRLExpressions.unaryExpressionNotPlusMinus_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TILDE107=null;
        Token NEGATION109=null;
        Token set114=null;
        DRLExpressions.unaryExpression_return unaryExpression108 = null;

        DRLExpressions.unaryExpression_return unaryExpression110 = null;

        DRLExpressions.castExpression_return castExpression111 = null;

        DRLExpressions.primary_return primary112 = null;

        DRLExpressions.selector_return selector113 = null;


        Object TILDE107_tree=null;
        Object NEGATION109_tree=null;
        Object set114_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:194:5: ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? )
            int alt40=4;
            alt40 = dfa40.predict(input);
            switch (alt40) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:194:9: TILDE unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    TILDE107=(Token)match(input,TILDE,FOLLOW_TILDE_in_unaryExpressionNotPlusMinus1293); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TILDE107_tree = (Object)adaptor.create(TILDE107);
                    adaptor.addChild(root_0, TILDE107_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus1295);
                    unaryExpression108=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression108.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:195:8: NEGATION unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    NEGATION109=(Token)match(input,NEGATION,FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus1304); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NEGATION109_tree = (Object)adaptor.create(NEGATION109);
                    adaptor.addChild(root_0, NEGATION109_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus1306);
                    unaryExpression110=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression110.getTree());

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:196:9: ( castExpression )=> castExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus1320);
                    castExpression111=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, castExpression111.getTree());

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:197:9: primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus1330);
                    primary112=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primary112.getTree());
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:197:17: ( ( selector )=> selector )*
                    loop38:
                    do {
                        int alt38=2;
                        alt38 = dfa38.predict(input);
                        switch (alt38) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:197:18: ( selector )=> selector
                    	    {
                    	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus1337);
                    	    selector113=selector();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, selector113.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop38;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:197:41: ( ( INCR | DECR )=> ( INCR | DECR ) )?
                    int alt39=2;
                    alt39 = dfa39.predict(input);
                    switch (alt39) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:197:42: ( INCR | DECR )=> ( INCR | DECR )
                            {
                            set114=(Token)input.LT(1);
                            if ( (input.LA(1)>=DECR && input.LA(1)<=INCR) ) {
                                input.consume();
                                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set114));
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:200:1: castExpression : ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus );
    public final DRLExpressions.castExpression_return castExpression() throws RecognitionException {
        DRLExpressions.castExpression_return retval = new DRLExpressions.castExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN115=null;
        Token RIGHT_PAREN117=null;
        Token LEFT_PAREN119=null;
        Token RIGHT_PAREN121=null;
        DRLExpressions.primitiveType_return primitiveType116 = null;

        DRLExpressions.unaryExpression_return unaryExpression118 = null;

        DRLExpressions.type_return type120 = null;

        DRLExpressions.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus122 = null;


        Object LEFT_PAREN115_tree=null;
        Object RIGHT_PAREN117_tree=null;
        Object LEFT_PAREN119_tree=null;
        Object RIGHT_PAREN121_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:201:5: ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus )
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
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:201:8: ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN unaryExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    LEFT_PAREN115=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression1385); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LEFT_PAREN115_tree = (Object)adaptor.create(LEFT_PAREN115);
                    adaptor.addChild(root_0, LEFT_PAREN115_tree);
                    }
                    pushFollow(FOLLOW_primitiveType_in_castExpression1387);
                    primitiveType116=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType116.getTree());
                    RIGHT_PAREN117=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression1389); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RIGHT_PAREN117_tree = (Object)adaptor.create(RIGHT_PAREN117);
                    adaptor.addChild(root_0, RIGHT_PAREN117_tree);
                    }
                    pushFollow(FOLLOW_unaryExpression_in_castExpression1391);
                    unaryExpression118=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression118.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:202:8: ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus
                    {
                    root_0 = (Object)adaptor.nil();

                    LEFT_PAREN119=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression1408); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LEFT_PAREN119_tree = (Object)adaptor.create(LEFT_PAREN119);
                    adaptor.addChild(root_0, LEFT_PAREN119_tree);
                    }
                    pushFollow(FOLLOW_type_in_castExpression1410);
                    type120=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type120.getTree());
                    RIGHT_PAREN121=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression1412); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RIGHT_PAREN121_tree = (Object)adaptor.create(RIGHT_PAREN121);
                    adaptor.addChild(root_0, RIGHT_PAREN121_tree);
                    }
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression1414);
                    unaryExpressionNotPlusMinus122=unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpressionNotPlusMinus122.getTree());

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:205:1: primitiveType : ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key );
    public final DRLExpressions.primitiveType_return primitiveType() throws RecognitionException {
        DRLExpressions.primitiveType_return retval = new DRLExpressions.primitiveType_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLExpressions.boolean_key_return boolean_key123 = null;

        DRLExpressions.char_key_return char_key124 = null;

        DRLExpressions.byte_key_return byte_key125 = null;

        DRLExpressions.short_key_return short_key126 = null;

        DRLExpressions.int_key_return int_key127 = null;

        DRLExpressions.long_key_return long_key128 = null;

        DRLExpressions.float_key_return float_key129 = null;

        DRLExpressions.double_key_return double_key130 = null;



        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:206:5: ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key )
            int alt42=8;
            alt42 = dfa42.predict(input);
            switch (alt42) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:206:7: boolean_key
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_boolean_key_in_primitiveType1435);
                    boolean_key123=boolean_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, boolean_key123.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:207:7: char_key
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_char_key_in_primitiveType1443);
                    char_key124=char_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, char_key124.getTree());

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:208:7: byte_key
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_byte_key_in_primitiveType1451);
                    byte_key125=byte_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, byte_key125.getTree());

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:209:7: short_key
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_short_key_in_primitiveType1459);
                    short_key126=short_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, short_key126.getTree());

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:210:7: int_key
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_int_key_in_primitiveType1467);
                    int_key127=int_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, int_key127.getTree());

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:211:7: long_key
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_long_key_in_primitiveType1475);
                    long_key128=long_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, long_key128.getTree());

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:212:7: float_key
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_float_key_in_primitiveType1483);
                    float_key129=float_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, float_key129.getTree());

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:213:7: double_key
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_double_key_in_primitiveType1491);
                    double_key130=double_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, double_key130.getTree());

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:216:1: primary : ( ( parExpression )=> parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )? );
    public final DRLExpressions.primary_return primary() throws RecognitionException {
        DRLExpressions.primary_return retval = new DRLExpressions.primary_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE142=null;
        Token RIGHT_SQUARE143=null;
        Token DOT144=null;
        Token ID148=null;
        Token DOT149=null;
        Token ID150=null;
        DRLExpressions.parExpression_return parExpression131 = null;

        DRLExpressions.nonWildcardTypeArguments_return nonWildcardTypeArguments132 = null;

        DRLExpressions.explicitGenericInvocationSuffix_return explicitGenericInvocationSuffix133 = null;

        DRLExpressions.this_key_return this_key134 = null;

        DRLExpressions.arguments_return arguments135 = null;

        DRLExpressions.literal_return literal136 = null;

        DRLExpressions.super_key_return super_key137 = null;

        DRLExpressions.superSuffix_return superSuffix138 = null;

        DRLExpressions.new_key_return new_key139 = null;

        DRLExpressions.creator_return creator140 = null;

        DRLExpressions.primitiveType_return primitiveType141 = null;

        DRLExpressions.class_key_return class_key145 = null;

        DRLExpressions.inlineMapExpression_return inlineMapExpression146 = null;

        DRLExpressions.inlineListExpression_return inlineListExpression147 = null;

        DRLExpressions.identifierSuffix_return identifierSuffix151 = null;


        Object LEFT_SQUARE142_tree=null;
        Object RIGHT_SQUARE143_tree=null;
        Object DOT144_tree=null;
        Object ID148_tree=null;
        Object DOT149_tree=null;
        Object ID150_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:217:5: ( ( parExpression )=> parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )? )
            int alt47=9;
            alt47 = dfa47.predict(input);
            switch (alt47) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:217:7: ( parExpression )=> parExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_parExpression_in_primary1513);
                    parExpression131=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression131.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:218:9: ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments )
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_primary1528);
                    nonWildcardTypeArguments132=nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments132.getTree());
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:218:63: ( explicitGenericInvocationSuffix | this_key arguments )
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
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:218:64: explicitGenericInvocationSuffix
                            {
                            pushFollow(FOLLOW_explicitGenericInvocationSuffix_in_primary1531);
                            explicitGenericInvocationSuffix133=explicitGenericInvocationSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, explicitGenericInvocationSuffix133.getTree());

                            }
                            break;
                        case 2 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:218:98: this_key arguments
                            {
                            pushFollow(FOLLOW_this_key_in_primary1535);
                            this_key134=this_key();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, this_key134.getTree());
                            pushFollow(FOLLOW_arguments_in_primary1537);
                            arguments135=arguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments135.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:219:9: ( literal )=> literal
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_literal_in_primary1553);
                    literal136=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal136.getTree());

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:221:9: ( super_key )=> super_key superSuffix
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_super_key_in_primary1573);
                    super_key137=super_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, super_key137.getTree());
                    pushFollow(FOLLOW_superSuffix_in_primary1575);
                    superSuffix138=superSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, superSuffix138.getTree());

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:222:9: ( new_key )=> new_key creator
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_new_key_in_primary1590);
                    new_key139=new_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, new_key139.getTree());
                    pushFollow(FOLLOW_creator_in_primary1592);
                    creator140=creator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, creator140.getTree());

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:223:9: ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primitiveType_in_primary1607);
                    primitiveType141=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType141.getTree());
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:223:41: ( LEFT_SQUARE RIGHT_SQUARE )*
                    loop44:
                    do {
                        int alt44=2;
                        int LA44_0 = input.LA(1);

                        if ( (LA44_0==LEFT_SQUARE) ) {
                            alt44=1;
                        }


                        switch (alt44) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:223:42: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    LEFT_SQUARE142=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_primary1610); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    LEFT_SQUARE142_tree = (Object)adaptor.create(LEFT_SQUARE142);
                    	    adaptor.addChild(root_0, LEFT_SQUARE142_tree);
                    	    }
                    	    RIGHT_SQUARE143=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_primary1612); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    RIGHT_SQUARE143_tree = (Object)adaptor.create(RIGHT_SQUARE143);
                    	    adaptor.addChild(root_0, RIGHT_SQUARE143_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop44;
                        }
                    } while (true);

                    DOT144=(Token)match(input,DOT,FOLLOW_DOT_in_primary1616); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DOT144_tree = (Object)adaptor.create(DOT144);
                    adaptor.addChild(root_0, DOT144_tree);
                    }
                    pushFollow(FOLLOW_class_key_in_primary1618);
                    class_key145=class_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, class_key145.getTree());

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:225:9: ( inlineMapExpression )=> inlineMapExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_inlineMapExpression_in_primary1638);
                    inlineMapExpression146=inlineMapExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, inlineMapExpression146.getTree());

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:226:9: ( inlineListExpression )=> inlineListExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_inlineListExpression_in_primary1653);
                    inlineListExpression147=inlineListExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, inlineListExpression147.getTree());

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:227:9: ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )?
                    {
                    root_0 = (Object)adaptor.nil();

                    ID148=(Token)match(input,ID,FOLLOW_ID_in_primary1667); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID148_tree = (Object)adaptor.create(ID148);
                    adaptor.addChild(root_0, ID148_tree);
                    }
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:227:18: ( ( DOT ID )=> DOT ID )*
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
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:227:19: ( DOT ID )=> DOT ID
                    	    {
                    	    DOT149=(Token)match(input,DOT,FOLLOW_DOT_in_primary1676); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    DOT149_tree = (Object)adaptor.create(DOT149);
                    	    adaptor.addChild(root_0, DOT149_tree);
                    	    }
                    	    ID150=(Token)match(input,ID,FOLLOW_ID_in_primary1678); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    ID150_tree = (Object)adaptor.create(ID150);
                    	    adaptor.addChild(root_0, ID150_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop45;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:227:38: ( ( identifierSuffix )=> identifierSuffix )?
                    int alt46=2;
                    alt46 = dfa46.predict(input);
                    switch (alt46) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:227:39: ( identifierSuffix )=> identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary1687);
                            identifierSuffix151=identifierSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, identifierSuffix151.getTree());

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:230:1: inlineListExpression : LEFT_SQUARE ( expressionList )? RIGHT_SQUARE ;
    public final DRLExpressions.inlineListExpression_return inlineListExpression() throws RecognitionException {
        DRLExpressions.inlineListExpression_return retval = new DRLExpressions.inlineListExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE152=null;
        Token RIGHT_SQUARE154=null;
        DRLExpressions.expressionList_return expressionList153 = null;


        Object LEFT_SQUARE152_tree=null;
        Object RIGHT_SQUARE154_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:231:5: ( LEFT_SQUARE ( expressionList )? RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:231:9: LEFT_SQUARE ( expressionList )? RIGHT_SQUARE
            {
            root_0 = (Object)adaptor.nil();

            LEFT_SQUARE152=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_inlineListExpression1708); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            LEFT_SQUARE152_tree = (Object)adaptor.create(LEFT_SQUARE152);
            adaptor.addChild(root_0, LEFT_SQUARE152_tree);
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:231:21: ( expressionList )?
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==FLOAT||(LA48_0>=HEX && LA48_0<=DECIMAL)||LA48_0==STRING||(LA48_0>=BOOL && LA48_0<=NULL)||(LA48_0>=DECR && LA48_0<=INCR)||LA48_0==LESS||LA48_0==LEFT_PAREN||LA48_0==LEFT_SQUARE||(LA48_0>=NEGATION && LA48_0<=TILDE)||(LA48_0>=MINUS && LA48_0<=PLUS)||LA48_0==ID) ) {
                alt48=1;
            }
            switch (alt48) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:231:21: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_inlineListExpression1710);
                    expressionList153=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList153.getTree());

                    }
                    break;

            }

            RIGHT_SQUARE154=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_inlineListExpression1713); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RIGHT_SQUARE154_tree = (Object)adaptor.create(RIGHT_SQUARE154);
            adaptor.addChild(root_0, RIGHT_SQUARE154_tree);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:234:1: inlineMapExpression : LEFT_SQUARE ( mapExpressionList )+ RIGHT_SQUARE ;
    public final DRLExpressions.inlineMapExpression_return inlineMapExpression() throws RecognitionException {
        DRLExpressions.inlineMapExpression_return retval = new DRLExpressions.inlineMapExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE155=null;
        Token RIGHT_SQUARE157=null;
        DRLExpressions.mapExpressionList_return mapExpressionList156 = null;


        Object LEFT_SQUARE155_tree=null;
        Object RIGHT_SQUARE157_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:235:5: ( LEFT_SQUARE ( mapExpressionList )+ RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:235:7: LEFT_SQUARE ( mapExpressionList )+ RIGHT_SQUARE
            {
            root_0 = (Object)adaptor.nil();

            LEFT_SQUARE155=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_inlineMapExpression1735); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            LEFT_SQUARE155_tree = (Object)adaptor.create(LEFT_SQUARE155);
            adaptor.addChild(root_0, LEFT_SQUARE155_tree);
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:235:19: ( mapExpressionList )+
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
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:235:19: mapExpressionList
            	    {
            	    pushFollow(FOLLOW_mapExpressionList_in_inlineMapExpression1737);
            	    mapExpressionList156=mapExpressionList();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, mapExpressionList156.getTree());

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

            RIGHT_SQUARE157=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_inlineMapExpression1740); if (state.failed) return retval;
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
    // $ANTLR end "inlineMapExpression"

    public static class mapExpressionList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "mapExpressionList"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:238:1: mapExpressionList : mapEntry ( COMMA mapEntry )* ;
    public final DRLExpressions.mapExpressionList_return mapExpressionList() throws RecognitionException {
        DRLExpressions.mapExpressionList_return retval = new DRLExpressions.mapExpressionList_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA159=null;
        DRLExpressions.mapEntry_return mapEntry158 = null;

        DRLExpressions.mapEntry_return mapEntry160 = null;


        Object COMMA159_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:239:5: ( mapEntry ( COMMA mapEntry )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:239:7: mapEntry ( COMMA mapEntry )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_mapEntry_in_mapExpressionList1757);
            mapEntry158=mapEntry();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, mapEntry158.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:239:16: ( COMMA mapEntry )*
            loop50:
            do {
                int alt50=2;
                int LA50_0 = input.LA(1);

                if ( (LA50_0==COMMA) ) {
                    alt50=1;
                }


                switch (alt50) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:239:17: COMMA mapEntry
            	    {
            	    COMMA159=(Token)match(input,COMMA,FOLLOW_COMMA_in_mapExpressionList1760); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    COMMA159_tree = (Object)adaptor.create(COMMA159);
            	    adaptor.addChild(root_0, COMMA159_tree);
            	    }
            	    pushFollow(FOLLOW_mapEntry_in_mapExpressionList1762);
            	    mapEntry160=mapEntry();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, mapEntry160.getTree());

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:242:1: mapEntry : expression COLON expression ;
    public final DRLExpressions.mapEntry_return mapEntry() throws RecognitionException {
        DRLExpressions.mapEntry_return retval = new DRLExpressions.mapEntry_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON162=null;
        DRLExpressions.expression_return expression161 = null;

        DRLExpressions.expression_return expression163 = null;


        Object COLON162_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:243:5: ( expression COLON expression )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:243:7: expression COLON expression
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression_in_mapEntry1785);
            expression161=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression161.getTree());
            COLON162=(Token)match(input,COLON,FOLLOW_COLON_in_mapEntry1787); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            COLON162_tree = (Object)adaptor.create(COLON162);
            adaptor.addChild(root_0, COLON162_tree);
            }
            pushFollow(FOLLOW_expression_in_mapEntry1789);
            expression163=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression163.getTree());

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:246:1: parExpression : LEFT_PAREN expression RIGHT_PAREN ;
    public final DRLExpressions.parExpression_return parExpression() throws RecognitionException {
        DRLExpressions.parExpression_return retval = new DRLExpressions.parExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN164=null;
        Token RIGHT_PAREN166=null;
        DRLExpressions.expression_return expression165 = null;


        Object LEFT_PAREN164_tree=null;
        Object RIGHT_PAREN166_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:247:5: ( LEFT_PAREN expression RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:247:7: LEFT_PAREN expression RIGHT_PAREN
            {
            root_0 = (Object)adaptor.nil();

            LEFT_PAREN164=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_parExpression1806); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            LEFT_PAREN164_tree = (Object)adaptor.create(LEFT_PAREN164);
            adaptor.addChild(root_0, LEFT_PAREN164_tree);
            }
            pushFollow(FOLLOW_expression_in_parExpression1808);
            expression165=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression165.getTree());
            RIGHT_PAREN166=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_parExpression1810); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RIGHT_PAREN166_tree = (Object)adaptor.create(RIGHT_PAREN166);
            adaptor.addChild(root_0, RIGHT_PAREN166_tree);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:250:1: identifierSuffix : ( ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments );
    public final DRLExpressions.identifierSuffix_return identifierSuffix() throws RecognitionException {
        DRLExpressions.identifierSuffix_return retval = new DRLExpressions.identifierSuffix_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE167=null;
        Token RIGHT_SQUARE168=null;
        Token DOT169=null;
        Token LEFT_SQUARE171=null;
        Token RIGHT_SQUARE173=null;
        DRLExpressions.class_key_return class_key170 = null;

        DRLExpressions.expression_return expression172 = null;

        DRLExpressions.arguments_return arguments174 = null;


        Object LEFT_SQUARE167_tree=null;
        Object RIGHT_SQUARE168_tree=null;
        Object DOT169_tree=null;
        Object LEFT_SQUARE171_tree=null;
        Object RIGHT_SQUARE173_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:251:5: ( ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments )
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
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:251:7: ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key
                    {
                    root_0 = (Object)adaptor.nil();

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:251:35: ( LEFT_SQUARE RIGHT_SQUARE )+
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
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:251:36: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    LEFT_SQUARE167=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_identifierSuffix1834); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    LEFT_SQUARE167_tree = (Object)adaptor.create(LEFT_SQUARE167);
                    	    adaptor.addChild(root_0, LEFT_SQUARE167_tree);
                    	    }
                    	    RIGHT_SQUARE168=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_identifierSuffix1836); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    RIGHT_SQUARE168_tree = (Object)adaptor.create(RIGHT_SQUARE168);
                    	    adaptor.addChild(root_0, RIGHT_SQUARE168_tree);
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

                    DOT169=(Token)match(input,DOT,FOLLOW_DOT_in_identifierSuffix1840); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DOT169_tree = (Object)adaptor.create(DOT169);
                    adaptor.addChild(root_0, DOT169_tree);
                    }
                    pushFollow(FOLLOW_class_key_in_identifierSuffix1842);
                    class_key170=class_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, class_key170.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:252:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
                    {
                    root_0 = (Object)adaptor.nil();

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:252:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
                    int cnt52=0;
                    loop52:
                    do {
                        int alt52=2;
                        alt52 = dfa52.predict(input);
                        switch (alt52) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:252:8: ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE
                    	    {
                    	    LEFT_SQUARE171=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_identifierSuffix1857); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    LEFT_SQUARE171_tree = (Object)adaptor.create(LEFT_SQUARE171);
                    	    adaptor.addChild(root_0, LEFT_SQUARE171_tree);
                    	    }
                    	    pushFollow(FOLLOW_expression_in_identifierSuffix1859);
                    	    expression172=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression172.getTree());
                    	    RIGHT_SQUARE173=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_identifierSuffix1861); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    RIGHT_SQUARE173_tree = (Object)adaptor.create(RIGHT_SQUARE173);
                    	    adaptor.addChild(root_0, RIGHT_SQUARE173_tree);
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
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:253:9: arguments
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_arguments_in_identifierSuffix1874);
                    arguments174=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments174.getTree());

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:261:1: creator : ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) ;
    public final DRLExpressions.creator_return creator() throws RecognitionException {
        DRLExpressions.creator_return retval = new DRLExpressions.creator_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLExpressions.nonWildcardTypeArguments_return nonWildcardTypeArguments175 = null;

        DRLExpressions.createdName_return createdName176 = null;

        DRLExpressions.arrayCreatorRest_return arrayCreatorRest177 = null;

        DRLExpressions.classCreatorRest_return classCreatorRest178 = null;



        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:262:5: ( ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:262:7: ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest )
            {
            root_0 = (Object)adaptor.nil();

            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:262:7: ( nonWildcardTypeArguments )?
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==LESS) ) {
                alt54=1;
            }
            switch (alt54) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:262:7: nonWildcardTypeArguments
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator1897);
                    nonWildcardTypeArguments175=nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments175.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_createdName_in_creator1900);
            createdName176=createdName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, createdName176.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:263:9: ( arrayCreatorRest | classCreatorRest )
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
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:263:10: arrayCreatorRest
                    {
                    pushFollow(FOLLOW_arrayCreatorRest_in_creator1911);
                    arrayCreatorRest177=arrayCreatorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arrayCreatorRest177.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:263:29: classCreatorRest
                    {
                    pushFollow(FOLLOW_classCreatorRest_in_creator1915);
                    classCreatorRest178=classCreatorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classCreatorRest178.getTree());

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:266:1: createdName : ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType );
    public final DRLExpressions.createdName_return createdName() throws RecognitionException {
        DRLExpressions.createdName_return retval = new DRLExpressions.createdName_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID179=null;
        Token DOT181=null;
        Token ID182=null;
        DRLExpressions.typeArguments_return typeArguments180 = null;

        DRLExpressions.typeArguments_return typeArguments183 = null;

        DRLExpressions.primitiveType_return primitiveType184 = null;


        Object ID179_tree=null;
        Object DOT181_tree=null;
        Object ID182_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:267:5: ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType )
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==ID) && ((!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))))) {
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
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:267:7: ID ( typeArguments )? ( DOT ID ( typeArguments )? )*
                    {
                    root_0 = (Object)adaptor.nil();

                    ID179=(Token)match(input,ID,FOLLOW_ID_in_createdName1933); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID179_tree = (Object)adaptor.create(ID179);
                    adaptor.addChild(root_0, ID179_tree);
                    }
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:267:10: ( typeArguments )?
                    int alt56=2;
                    int LA56_0 = input.LA(1);

                    if ( (LA56_0==LESS) ) {
                        alt56=1;
                    }
                    switch (alt56) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:267:10: typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_createdName1935);
                            typeArguments180=typeArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArguments180.getTree());

                            }
                            break;

                    }

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:268:9: ( DOT ID ( typeArguments )? )*
                    loop58:
                    do {
                        int alt58=2;
                        int LA58_0 = input.LA(1);

                        if ( (LA58_0==DOT) ) {
                            alt58=1;
                        }


                        switch (alt58) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:268:11: DOT ID ( typeArguments )?
                    	    {
                    	    DOT181=(Token)match(input,DOT,FOLLOW_DOT_in_createdName1948); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    DOT181_tree = (Object)adaptor.create(DOT181);
                    	    adaptor.addChild(root_0, DOT181_tree);
                    	    }
                    	    ID182=(Token)match(input,ID,FOLLOW_ID_in_createdName1950); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    ID182_tree = (Object)adaptor.create(ID182);
                    	    adaptor.addChild(root_0, ID182_tree);
                    	    }
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:268:18: ( typeArguments )?
                    	    int alt57=2;
                    	    int LA57_0 = input.LA(1);

                    	    if ( (LA57_0==LESS) ) {
                    	        alt57=1;
                    	    }
                    	    switch (alt57) {
                    	        case 1 :
                    	            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:268:18: typeArguments
                    	            {
                    	            pushFollow(FOLLOW_typeArguments_in_createdName1952);
                    	            typeArguments183=typeArguments();

                    	            state._fsp--;
                    	            if (state.failed) return retval;
                    	            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArguments183.getTree());

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
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:269:11: primitiveType
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primitiveType_in_createdName1967);
                    primitiveType184=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType184.getTree());

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:272:1: innerCreator : {...}? => ID classCreatorRest ;
    public final DRLExpressions.innerCreator_return innerCreator() throws RecognitionException {
        DRLExpressions.innerCreator_return retval = new DRLExpressions.innerCreator_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID185=null;
        DRLExpressions.classCreatorRest_return classCreatorRest186 = null;


        Object ID185_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:273:5: ({...}? => ID classCreatorRest )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:273:7: {...}? => ID classCreatorRest
            {
            root_0 = (Object)adaptor.nil();

            if ( !((!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "innerCreator", "!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
            }
            ID185=(Token)match(input,ID,FOLLOW_ID_in_innerCreator1987); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID185_tree = (Object)adaptor.create(ID185);
            adaptor.addChild(root_0, ID185_tree);
            }
            pushFollow(FOLLOW_classCreatorRest_in_innerCreator1989);
            classCreatorRest186=classCreatorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, classCreatorRest186.getTree());

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:276:1: arrayCreatorRest : LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) ;
    public final DRLExpressions.arrayCreatorRest_return arrayCreatorRest() throws RecognitionException {
        DRLExpressions.arrayCreatorRest_return retval = new DRLExpressions.arrayCreatorRest_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE187=null;
        Token RIGHT_SQUARE188=null;
        Token LEFT_SQUARE189=null;
        Token RIGHT_SQUARE190=null;
        Token RIGHT_SQUARE193=null;
        Token LEFT_SQUARE194=null;
        Token RIGHT_SQUARE196=null;
        Token LEFT_SQUARE197=null;
        Token RIGHT_SQUARE198=null;
        DRLExpressions.arrayInitializer_return arrayInitializer191 = null;

        DRLExpressions.expression_return expression192 = null;

        DRLExpressions.expression_return expression195 = null;


        Object LEFT_SQUARE187_tree=null;
        Object RIGHT_SQUARE188_tree=null;
        Object LEFT_SQUARE189_tree=null;
        Object RIGHT_SQUARE190_tree=null;
        Object RIGHT_SQUARE193_tree=null;
        Object LEFT_SQUARE194_tree=null;
        Object RIGHT_SQUARE196_tree=null;
        Object LEFT_SQUARE197_tree=null;
        Object RIGHT_SQUARE198_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:277:5: ( LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:277:9: LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
            {
            root_0 = (Object)adaptor.nil();

            LEFT_SQUARE187=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2008); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            LEFT_SQUARE187_tree = (Object)adaptor.create(LEFT_SQUARE187);
            adaptor.addChild(root_0, LEFT_SQUARE187_tree);
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:278:5: ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
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
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:278:9: RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer
                    {
                    RIGHT_SQUARE188=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2018); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RIGHT_SQUARE188_tree = (Object)adaptor.create(RIGHT_SQUARE188);
                    adaptor.addChild(root_0, RIGHT_SQUARE188_tree);
                    }
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:278:22: ( LEFT_SQUARE RIGHT_SQUARE )*
                    loop60:
                    do {
                        int alt60=2;
                        int LA60_0 = input.LA(1);

                        if ( (LA60_0==LEFT_SQUARE) ) {
                            alt60=1;
                        }


                        switch (alt60) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:278:23: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    LEFT_SQUARE189=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2021); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    LEFT_SQUARE189_tree = (Object)adaptor.create(LEFT_SQUARE189);
                    	    adaptor.addChild(root_0, LEFT_SQUARE189_tree);
                    	    }
                    	    RIGHT_SQUARE190=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2023); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    RIGHT_SQUARE190_tree = (Object)adaptor.create(RIGHT_SQUARE190);
                    	    adaptor.addChild(root_0, RIGHT_SQUARE190_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop60;
                        }
                    } while (true);

                    pushFollow(FOLLOW_arrayInitializer_in_arrayCreatorRest2027);
                    arrayInitializer191=arrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arrayInitializer191.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:279:13: expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                    pushFollow(FOLLOW_expression_in_arrayCreatorRest2041);
                    expression192=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression192.getTree());
                    RIGHT_SQUARE193=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2043); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RIGHT_SQUARE193_tree = (Object)adaptor.create(RIGHT_SQUARE193);
                    adaptor.addChild(root_0, RIGHT_SQUARE193_tree);
                    }
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:279:37: ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )*
                    loop61:
                    do {
                        int alt61=2;
                        alt61 = dfa61.predict(input);
                        switch (alt61) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:279:38: {...}? => LEFT_SQUARE expression RIGHT_SQUARE
                    	    {
                    	    if ( !((!helper.validateLT(2,"]"))) ) {
                    	        if (state.backtracking>0) {state.failed=true; return retval;}
                    	        throw new FailedPredicateException(input, "arrayCreatorRest", "!helper.validateLT(2,\"]\")");
                    	    }
                    	    LEFT_SQUARE194=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2048); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    LEFT_SQUARE194_tree = (Object)adaptor.create(LEFT_SQUARE194);
                    	    adaptor.addChild(root_0, LEFT_SQUARE194_tree);
                    	    }
                    	    pushFollow(FOLLOW_expression_in_arrayCreatorRest2050);
                    	    expression195=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression195.getTree());
                    	    RIGHT_SQUARE196=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2052); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    RIGHT_SQUARE196_tree = (Object)adaptor.create(RIGHT_SQUARE196);
                    	    adaptor.addChild(root_0, RIGHT_SQUARE196_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop61;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:279:106: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
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
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:279:107: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    LEFT_SQUARE197=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2064); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    LEFT_SQUARE197_tree = (Object)adaptor.create(LEFT_SQUARE197);
                    	    adaptor.addChild(root_0, LEFT_SQUARE197_tree);
                    	    }
                    	    RIGHT_SQUARE198=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2066); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    RIGHT_SQUARE198_tree = (Object)adaptor.create(RIGHT_SQUARE198);
                    	    adaptor.addChild(root_0, RIGHT_SQUARE198_tree);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:283:1: variableInitializer : ( arrayInitializer | expression );
    public final DRLExpressions.variableInitializer_return variableInitializer() throws RecognitionException {
        DRLExpressions.variableInitializer_return retval = new DRLExpressions.variableInitializer_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLExpressions.arrayInitializer_return arrayInitializer199 = null;

        DRLExpressions.expression_return expression200 = null;



        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:284:5: ( arrayInitializer | expression )
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
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:284:7: arrayInitializer
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_arrayInitializer_in_variableInitializer2095);
                    arrayInitializer199=arrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arrayInitializer199.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:285:13: expression
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expression_in_variableInitializer2109);
                    expression200=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression200.getTree());

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:288:1: arrayInitializer : LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY ;
    public final DRLExpressions.arrayInitializer_return arrayInitializer() throws RecognitionException {
        DRLExpressions.arrayInitializer_return retval = new DRLExpressions.arrayInitializer_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_CURLY201=null;
        Token COMMA203=null;
        Token COMMA205=null;
        Token RIGHT_CURLY206=null;
        DRLExpressions.variableInitializer_return variableInitializer202 = null;

        DRLExpressions.variableInitializer_return variableInitializer204 = null;


        Object LEFT_CURLY201_tree=null;
        Object COMMA203_tree=null;
        Object COMMA205_tree=null;
        Object RIGHT_CURLY206_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:289:5: ( LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:289:7: LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY
            {
            root_0 = (Object)adaptor.nil();

            LEFT_CURLY201=(Token)match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_arrayInitializer2126); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            LEFT_CURLY201_tree = (Object)adaptor.create(LEFT_CURLY201);
            adaptor.addChild(root_0, LEFT_CURLY201_tree);
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:289:18: ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )?
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==FLOAT||(LA67_0>=HEX && LA67_0<=DECIMAL)||LA67_0==STRING||(LA67_0>=BOOL && LA67_0<=NULL)||(LA67_0>=DECR && LA67_0<=INCR)||LA67_0==LESS||LA67_0==LEFT_PAREN||LA67_0==LEFT_SQUARE||LA67_0==LEFT_CURLY||(LA67_0>=NEGATION && LA67_0<=TILDE)||(LA67_0>=MINUS && LA67_0<=PLUS)||LA67_0==ID) ) {
                alt67=1;
            }
            switch (alt67) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:289:19: variableInitializer ( COMMA variableInitializer )* ( COMMA )?
                    {
                    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer2129);
                    variableInitializer202=variableInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableInitializer202.getTree());
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:289:39: ( COMMA variableInitializer )*
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
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:289:40: COMMA variableInitializer
                    	    {
                    	    COMMA203=(Token)match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer2132); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    COMMA203_tree = (Object)adaptor.create(COMMA203);
                    	    adaptor.addChild(root_0, COMMA203_tree);
                    	    }
                    	    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer2134);
                    	    variableInitializer204=variableInitializer();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableInitializer204.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop65;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:289:68: ( COMMA )?
                    int alt66=2;
                    int LA66_0 = input.LA(1);

                    if ( (LA66_0==COMMA) ) {
                        alt66=1;
                    }
                    switch (alt66) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:289:69: COMMA
                            {
                            COMMA205=(Token)match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer2139); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            COMMA205_tree = (Object)adaptor.create(COMMA205);
                            adaptor.addChild(root_0, COMMA205_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;

            }

            RIGHT_CURLY206=(Token)match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_arrayInitializer2146); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RIGHT_CURLY206_tree = (Object)adaptor.create(RIGHT_CURLY206);
            adaptor.addChild(root_0, RIGHT_CURLY206_tree);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:292:1: classCreatorRest : arguments ;
    public final DRLExpressions.classCreatorRest_return classCreatorRest() throws RecognitionException {
        DRLExpressions.classCreatorRest_return retval = new DRLExpressions.classCreatorRest_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLExpressions.arguments_return arguments207 = null;



        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:293:5: ( arguments )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:293:7: arguments
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_arguments_in_classCreatorRest2163);
            arguments207=arguments();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments207.getTree());

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:296:1: explicitGenericInvocation : nonWildcardTypeArguments arguments ;
    public final DRLExpressions.explicitGenericInvocation_return explicitGenericInvocation() throws RecognitionException {
        DRLExpressions.explicitGenericInvocation_return retval = new DRLExpressions.explicitGenericInvocation_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        DRLExpressions.nonWildcardTypeArguments_return nonWildcardTypeArguments208 = null;

        DRLExpressions.arguments_return arguments209 = null;



        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:297:5: ( nonWildcardTypeArguments arguments )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:297:7: nonWildcardTypeArguments arguments
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation2181);
            nonWildcardTypeArguments208=nonWildcardTypeArguments();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments208.getTree());
            pushFollow(FOLLOW_arguments_in_explicitGenericInvocation2183);
            arguments209=arguments();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments209.getTree());

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:300:1: nonWildcardTypeArguments : LESS typeList GREATER ;
    public final DRLExpressions.nonWildcardTypeArguments_return nonWildcardTypeArguments() throws RecognitionException {
        DRLExpressions.nonWildcardTypeArguments_return retval = new DRLExpressions.nonWildcardTypeArguments_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LESS210=null;
        Token GREATER212=null;
        DRLExpressions.typeList_return typeList211 = null;


        Object LESS210_tree=null;
        Object GREATER212_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:301:5: ( LESS typeList GREATER )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:301:7: LESS typeList GREATER
            {
            root_0 = (Object)adaptor.nil();

            LESS210=(Token)match(input,LESS,FOLLOW_LESS_in_nonWildcardTypeArguments2200); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            LESS210_tree = (Object)adaptor.create(LESS210);
            adaptor.addChild(root_0, LESS210_tree);
            }
            pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments2202);
            typeList211=typeList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeList211.getTree());
            GREATER212=(Token)match(input,GREATER,FOLLOW_GREATER_in_nonWildcardTypeArguments2204); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            GREATER212_tree = (Object)adaptor.create(GREATER212);
            adaptor.addChild(root_0, GREATER212_tree);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:304:1: explicitGenericInvocationSuffix : ( super_key superSuffix | ID arguments );
    public final DRLExpressions.explicitGenericInvocationSuffix_return explicitGenericInvocationSuffix() throws RecognitionException {
        DRLExpressions.explicitGenericInvocationSuffix_return retval = new DRLExpressions.explicitGenericInvocationSuffix_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID215=null;
        DRLExpressions.super_key_return super_key213 = null;

        DRLExpressions.superSuffix_return superSuffix214 = null;

        DRLExpressions.arguments_return arguments216 = null;


        Object ID215_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:305:5: ( super_key superSuffix | ID arguments )
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
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:305:7: super_key superSuffix
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_super_key_in_explicitGenericInvocationSuffix2221);
                    super_key213=super_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, super_key213.getTree());
                    pushFollow(FOLLOW_superSuffix_in_explicitGenericInvocationSuffix2223);
                    superSuffix214=superSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, superSuffix214.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:306:10: ID arguments
                    {
                    root_0 = (Object)adaptor.nil();

                    ID215=(Token)match(input,ID,FOLLOW_ID_in_explicitGenericInvocationSuffix2234); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID215_tree = (Object)adaptor.create(ID215);
                    adaptor.addChild(root_0, ID215_tree);
                    }
                    pushFollow(FOLLOW_arguments_in_explicitGenericInvocationSuffix2236);
                    arguments216=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments216.getTree());

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:309:1: selector : ( ( DOT super_key )=> DOT super_key superSuffix | ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator | ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )? | ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE );
    public final DRLExpressions.selector_return selector() throws RecognitionException {
        DRLExpressions.selector_return retval = new DRLExpressions.selector_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOT217=null;
        Token DOT220=null;
        Token DOT224=null;
        Token ID225=null;
        Token LEFT_SQUARE227=null;
        Token RIGHT_SQUARE229=null;
        DRLExpressions.super_key_return super_key218 = null;

        DRLExpressions.superSuffix_return superSuffix219 = null;

        DRLExpressions.new_key_return new_key221 = null;

        DRLExpressions.nonWildcardTypeArguments_return nonWildcardTypeArguments222 = null;

        DRLExpressions.innerCreator_return innerCreator223 = null;

        DRLExpressions.arguments_return arguments226 = null;

        DRLExpressions.expression_return expression228 = null;


        Object DOT217_tree=null;
        Object DOT220_tree=null;
        Object DOT224_tree=null;
        Object ID225_tree=null;
        Object LEFT_SQUARE227_tree=null;
        Object RIGHT_SQUARE229_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:310:5: ( ( DOT super_key )=> DOT super_key superSuffix | ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator | ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )? | ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )
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
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:310:9: ( DOT super_key )=> DOT super_key superSuffix
                    {
                    root_0 = (Object)adaptor.nil();

                    DOT217=(Token)match(input,DOT,FOLLOW_DOT_in_selector2261); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DOT217_tree = (Object)adaptor.create(DOT217);
                    adaptor.addChild(root_0, DOT217_tree);
                    }
                    pushFollow(FOLLOW_super_key_in_selector2263);
                    super_key218=super_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, super_key218.getTree());
                    pushFollow(FOLLOW_superSuffix_in_selector2265);
                    superSuffix219=superSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, superSuffix219.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:311:9: ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator
                    {
                    root_0 = (Object)adaptor.nil();

                    DOT220=(Token)match(input,DOT,FOLLOW_DOT_in_selector2281); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DOT220_tree = (Object)adaptor.create(DOT220);
                    adaptor.addChild(root_0, DOT220_tree);
                    }
                    pushFollow(FOLLOW_new_key_in_selector2283);
                    new_key221=new_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, new_key221.getTree());
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:311:36: ( nonWildcardTypeArguments )?
                    int alt69=2;
                    int LA69_0 = input.LA(1);

                    if ( (LA69_0==LESS) ) {
                        alt69=1;
                    }
                    switch (alt69) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:311:37: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_selector2286);
                            nonWildcardTypeArguments222=nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments222.getTree());

                            }
                            break;

                    }

                    pushFollow(FOLLOW_innerCreator_in_selector2290);
                    innerCreator223=innerCreator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, innerCreator223.getTree());

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:9: ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )?
                    {
                    root_0 = (Object)adaptor.nil();

                    DOT224=(Token)match(input,DOT,FOLLOW_DOT_in_selector2306); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DOT224_tree = (Object)adaptor.create(DOT224);
                    adaptor.addChild(root_0, DOT224_tree);
                    }
                    ID225=(Token)match(input,ID,FOLLOW_ID_in_selector2308); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID225_tree = (Object)adaptor.create(ID225);
                    adaptor.addChild(root_0, ID225_tree);
                    }
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:26: ( ( LEFT_PAREN )=> arguments )?
                    int alt70=2;
                    alt70 = dfa70.predict(input);
                    switch (alt70) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:27: ( LEFT_PAREN )=> arguments
                            {
                            pushFollow(FOLLOW_arguments_in_selector2317);
                            arguments226=arguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments226.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:314:9: ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE
                    {
                    root_0 = (Object)adaptor.nil();

                    LEFT_SQUARE227=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_selector2338); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LEFT_SQUARE227_tree = (Object)adaptor.create(LEFT_SQUARE227);
                    adaptor.addChild(root_0, LEFT_SQUARE227_tree);
                    }
                    pushFollow(FOLLOW_expression_in_selector2340);
                    expression228=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression228.getTree());
                    RIGHT_SQUARE229=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_selector2342); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RIGHT_SQUARE229_tree = (Object)adaptor.create(RIGHT_SQUARE229);
                    adaptor.addChild(root_0, RIGHT_SQUARE229_tree);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:317:1: superSuffix : ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? );
    public final DRLExpressions.superSuffix_return superSuffix() throws RecognitionException {
        DRLExpressions.superSuffix_return retval = new DRLExpressions.superSuffix_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOT231=null;
        Token ID232=null;
        DRLExpressions.arguments_return arguments230 = null;

        DRLExpressions.arguments_return arguments233 = null;


        Object DOT231_tree=null;
        Object ID232_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:318:5: ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? )
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
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:318:7: arguments
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_arguments_in_superSuffix2359);
                    arguments230=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments230.getTree());

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:319:10: DOT ID ( ( LEFT_PAREN )=> arguments )?
                    {
                    root_0 = (Object)adaptor.nil();

                    DOT231=(Token)match(input,DOT,FOLLOW_DOT_in_superSuffix2370); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DOT231_tree = (Object)adaptor.create(DOT231);
                    adaptor.addChild(root_0, DOT231_tree);
                    }
                    ID232=(Token)match(input,ID,FOLLOW_ID_in_superSuffix2372); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID232_tree = (Object)adaptor.create(ID232);
                    adaptor.addChild(root_0, ID232_tree);
                    }
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:319:17: ( ( LEFT_PAREN )=> arguments )?
                    int alt72=2;
                    alt72 = dfa72.predict(input);
                    switch (alt72) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:319:18: ( LEFT_PAREN )=> arguments
                            {
                            pushFollow(FOLLOW_arguments_in_superSuffix2381);
                            arguments233=arguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments233.getTree());

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:322:1: squareArguments : LEFT_SQUARE ( expressionList )? RIGHT_SQUARE ;
    public final DRLExpressions.squareArguments_return squareArguments() throws RecognitionException {
        DRLExpressions.squareArguments_return retval = new DRLExpressions.squareArguments_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_SQUARE234=null;
        Token RIGHT_SQUARE236=null;
        DRLExpressions.expressionList_return expressionList235 = null;


        Object LEFT_SQUARE234_tree=null;
        Object RIGHT_SQUARE236_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:323:5: ( LEFT_SQUARE ( expressionList )? RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:323:7: LEFT_SQUARE ( expressionList )? RIGHT_SQUARE
            {
            root_0 = (Object)adaptor.nil();

            LEFT_SQUARE234=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_squareArguments2404); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            LEFT_SQUARE234_tree = (Object)adaptor.create(LEFT_SQUARE234);
            adaptor.addChild(root_0, LEFT_SQUARE234_tree);
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:323:19: ( expressionList )?
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( (LA74_0==FLOAT||(LA74_0>=HEX && LA74_0<=DECIMAL)||LA74_0==STRING||(LA74_0>=BOOL && LA74_0<=NULL)||(LA74_0>=DECR && LA74_0<=INCR)||LA74_0==LESS||LA74_0==LEFT_PAREN||LA74_0==LEFT_SQUARE||(LA74_0>=NEGATION && LA74_0<=TILDE)||(LA74_0>=MINUS && LA74_0<=PLUS)||LA74_0==ID) ) {
                alt74=1;
            }
            switch (alt74) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:323:19: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_squareArguments2406);
                    expressionList235=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList235.getTree());

                    }
                    break;

            }

            RIGHT_SQUARE236=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_squareArguments2409); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RIGHT_SQUARE236_tree = (Object)adaptor.create(RIGHT_SQUARE236);
            adaptor.addChild(root_0, RIGHT_SQUARE236_tree);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:326:1: arguments : LEFT_PAREN ( expressionList )? RIGHT_PAREN ;
    public final DRLExpressions.arguments_return arguments() throws RecognitionException {
        DRLExpressions.arguments_return retval = new DRLExpressions.arguments_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LEFT_PAREN237=null;
        Token RIGHT_PAREN239=null;
        DRLExpressions.expressionList_return expressionList238 = null;


        Object LEFT_PAREN237_tree=null;
        Object RIGHT_PAREN239_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:327:5: ( LEFT_PAREN ( expressionList )? RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:327:7: LEFT_PAREN ( expressionList )? RIGHT_PAREN
            {
            root_0 = (Object)adaptor.nil();

            LEFT_PAREN237=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_arguments2426); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            LEFT_PAREN237_tree = (Object)adaptor.create(LEFT_PAREN237);
            adaptor.addChild(root_0, LEFT_PAREN237_tree);
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:327:18: ( expressionList )?
            int alt75=2;
            int LA75_0 = input.LA(1);

            if ( (LA75_0==FLOAT||(LA75_0>=HEX && LA75_0<=DECIMAL)||LA75_0==STRING||(LA75_0>=BOOL && LA75_0<=NULL)||(LA75_0>=DECR && LA75_0<=INCR)||LA75_0==LESS||LA75_0==LEFT_PAREN||LA75_0==LEFT_SQUARE||(LA75_0>=NEGATION && LA75_0<=TILDE)||(LA75_0>=MINUS && LA75_0<=PLUS)||LA75_0==ID) ) {
                alt75=1;
            }
            switch (alt75) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:327:18: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments2428);
                    expressionList238=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList238.getTree());

                    }
                    break;

            }

            RIGHT_PAREN239=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_arguments2431); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RIGHT_PAREN239_tree = (Object)adaptor.create(RIGHT_PAREN239);
            adaptor.addChild(root_0, RIGHT_PAREN239_tree);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:330:1: expressionList : expression ( COMMA expression )* ;
    public final DRLExpressions.expressionList_return expressionList() throws RecognitionException {
        DRLExpressions.expressionList_return retval = new DRLExpressions.expressionList_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA241=null;
        DRLExpressions.expression_return expression240 = null;

        DRLExpressions.expression_return expression242 = null;


        Object COMMA241_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:331:3: ( expression ( COMMA expression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:331:7: expression ( COMMA expression )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression_in_expressionList2448);
            expression240=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression240.getTree());
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:331:18: ( COMMA expression )*
            loop76:
            do {
                int alt76=2;
                int LA76_0 = input.LA(1);

                if ( (LA76_0==COMMA) ) {
                    alt76=1;
                }


                switch (alt76) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:331:19: COMMA expression
            	    {
            	    COMMA241=(Token)match(input,COMMA,FOLLOW_COMMA_in_expressionList2451); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    COMMA241_tree = (Object)adaptor.create(COMMA241);
            	    adaptor.addChild(root_0, COMMA241_tree);
            	    }
            	    pushFollow(FOLLOW_expression_in_expressionList2453);
            	    expression242=expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression242.getTree());

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:334:1: assignmentOperator : ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN );
    public final DRLExpressions.assignmentOperator_return assignmentOperator() throws RecognitionException {
        DRLExpressions.assignmentOperator_return retval = new DRLExpressions.assignmentOperator_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS_ASSIGN243=null;
        Token PLUS_ASSIGN244=null;
        Token MINUS_ASSIGN245=null;
        Token MULT_ASSIGN246=null;
        Token DIV_ASSIGN247=null;
        Token AND_ASSIGN248=null;
        Token OR_ASSIGN249=null;
        Token XOR_ASSIGN250=null;
        Token MOD_ASSIGN251=null;
        Token LESS252=null;
        Token LESS253=null;
        Token EQUALS_ASSIGN254=null;
        Token GREATER255=null;
        Token GREATER256=null;
        Token GREATER257=null;
        Token EQUALS_ASSIGN258=null;
        Token GREATER259=null;
        Token GREATER260=null;
        Token EQUALS_ASSIGN261=null;

        Object EQUALS_ASSIGN243_tree=null;
        Object PLUS_ASSIGN244_tree=null;
        Object MINUS_ASSIGN245_tree=null;
        Object MULT_ASSIGN246_tree=null;
        Object DIV_ASSIGN247_tree=null;
        Object AND_ASSIGN248_tree=null;
        Object OR_ASSIGN249_tree=null;
        Object XOR_ASSIGN250_tree=null;
        Object MOD_ASSIGN251_tree=null;
        Object LESS252_tree=null;
        Object LESS253_tree=null;
        Object EQUALS_ASSIGN254_tree=null;
        Object GREATER255_tree=null;
        Object GREATER256_tree=null;
        Object GREATER257_tree=null;
        Object EQUALS_ASSIGN258_tree=null;
        Object GREATER259_tree=null;
        Object GREATER260_tree=null;
        Object EQUALS_ASSIGN261_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:335:5: ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN )
            int alt77=12;
            alt77 = dfa77.predict(input);
            switch (alt77) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:335:9: EQUALS_ASSIGN
                    {
                    root_0 = (Object)adaptor.nil();

                    EQUALS_ASSIGN243=(Token)match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2472); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQUALS_ASSIGN243_tree = (Object)adaptor.create(EQUALS_ASSIGN243);
                    adaptor.addChild(root_0, EQUALS_ASSIGN243_tree);
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:336:7: PLUS_ASSIGN
                    {
                    root_0 = (Object)adaptor.nil();

                    PLUS_ASSIGN244=(Token)match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_assignmentOperator2480); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    PLUS_ASSIGN244_tree = (Object)adaptor.create(PLUS_ASSIGN244);
                    adaptor.addChild(root_0, PLUS_ASSIGN244_tree);
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:337:7: MINUS_ASSIGN
                    {
                    root_0 = (Object)adaptor.nil();

                    MINUS_ASSIGN245=(Token)match(input,MINUS_ASSIGN,FOLLOW_MINUS_ASSIGN_in_assignmentOperator2488); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MINUS_ASSIGN245_tree = (Object)adaptor.create(MINUS_ASSIGN245);
                    adaptor.addChild(root_0, MINUS_ASSIGN245_tree);
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:338:7: MULT_ASSIGN
                    {
                    root_0 = (Object)adaptor.nil();

                    MULT_ASSIGN246=(Token)match(input,MULT_ASSIGN,FOLLOW_MULT_ASSIGN_in_assignmentOperator2496); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MULT_ASSIGN246_tree = (Object)adaptor.create(MULT_ASSIGN246);
                    adaptor.addChild(root_0, MULT_ASSIGN246_tree);
                    }

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:339:7: DIV_ASSIGN
                    {
                    root_0 = (Object)adaptor.nil();

                    DIV_ASSIGN247=(Token)match(input,DIV_ASSIGN,FOLLOW_DIV_ASSIGN_in_assignmentOperator2504); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DIV_ASSIGN247_tree = (Object)adaptor.create(DIV_ASSIGN247);
                    adaptor.addChild(root_0, DIV_ASSIGN247_tree);
                    }

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:340:7: AND_ASSIGN
                    {
                    root_0 = (Object)adaptor.nil();

                    AND_ASSIGN248=(Token)match(input,AND_ASSIGN,FOLLOW_AND_ASSIGN_in_assignmentOperator2512); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    AND_ASSIGN248_tree = (Object)adaptor.create(AND_ASSIGN248);
                    adaptor.addChild(root_0, AND_ASSIGN248_tree);
                    }

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:341:7: OR_ASSIGN
                    {
                    root_0 = (Object)adaptor.nil();

                    OR_ASSIGN249=(Token)match(input,OR_ASSIGN,FOLLOW_OR_ASSIGN_in_assignmentOperator2520); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    OR_ASSIGN249_tree = (Object)adaptor.create(OR_ASSIGN249);
                    adaptor.addChild(root_0, OR_ASSIGN249_tree);
                    }

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:342:7: XOR_ASSIGN
                    {
                    root_0 = (Object)adaptor.nil();

                    XOR_ASSIGN250=(Token)match(input,XOR_ASSIGN,FOLLOW_XOR_ASSIGN_in_assignmentOperator2528); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    XOR_ASSIGN250_tree = (Object)adaptor.create(XOR_ASSIGN250);
                    adaptor.addChild(root_0, XOR_ASSIGN250_tree);
                    }

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:343:7: MOD_ASSIGN
                    {
                    root_0 = (Object)adaptor.nil();

                    MOD_ASSIGN251=(Token)match(input,MOD_ASSIGN,FOLLOW_MOD_ASSIGN_in_assignmentOperator2536); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MOD_ASSIGN251_tree = (Object)adaptor.create(MOD_ASSIGN251);
                    adaptor.addChild(root_0, MOD_ASSIGN251_tree);
                    }

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:344:7: LESS LESS EQUALS_ASSIGN
                    {
                    root_0 = (Object)adaptor.nil();

                    LESS252=(Token)match(input,LESS,FOLLOW_LESS_in_assignmentOperator2544); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESS252_tree = (Object)adaptor.create(LESS252);
                    adaptor.addChild(root_0, LESS252_tree);
                    }
                    LESS253=(Token)match(input,LESS,FOLLOW_LESS_in_assignmentOperator2546); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LESS253_tree = (Object)adaptor.create(LESS253);
                    adaptor.addChild(root_0, LESS253_tree);
                    }
                    EQUALS_ASSIGN254=(Token)match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2548); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQUALS_ASSIGN254_tree = (Object)adaptor.create(EQUALS_ASSIGN254);
                    adaptor.addChild(root_0, EQUALS_ASSIGN254_tree);
                    }

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:345:7: ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN
                    {
                    root_0 = (Object)adaptor.nil();

                    GREATER255=(Token)match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2565); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER255_tree = (Object)adaptor.create(GREATER255);
                    adaptor.addChild(root_0, GREATER255_tree);
                    }
                    GREATER256=(Token)match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2567); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER256_tree = (Object)adaptor.create(GREATER256);
                    adaptor.addChild(root_0, GREATER256_tree);
                    }
                    GREATER257=(Token)match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2569); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER257_tree = (Object)adaptor.create(GREATER257);
                    adaptor.addChild(root_0, GREATER257_tree);
                    }
                    EQUALS_ASSIGN258=(Token)match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2571); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQUALS_ASSIGN258_tree = (Object)adaptor.create(EQUALS_ASSIGN258);
                    adaptor.addChild(root_0, EQUALS_ASSIGN258_tree);
                    }

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:346:7: ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN
                    {
                    root_0 = (Object)adaptor.nil();

                    GREATER259=(Token)match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2586); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER259_tree = (Object)adaptor.create(GREATER259);
                    adaptor.addChild(root_0, GREATER259_tree);
                    }
                    GREATER260=(Token)match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2588); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GREATER260_tree = (Object)adaptor.create(GREATER260);
                    adaptor.addChild(root_0, GREATER260_tree);
                    }
                    EQUALS_ASSIGN261=(Token)match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2590); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQUALS_ASSIGN261_tree = (Object)adaptor.create(EQUALS_ASSIGN261);
                    adaptor.addChild(root_0, EQUALS_ASSIGN261_tree);
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
    // $ANTLR end "assignmentOperator"

    public static class extends_key_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "extends_key"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:352:1: extends_key : {...}? =>id= ID ;
    public final DRLExpressions.extends_key_return extends_key() throws RecognitionException {
        DRLExpressions.extends_key_return retval = new DRLExpressions.extends_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:353:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:353:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "extends_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_extends_key2620); if (state.failed) return retval;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:356:1: super_key : {...}? =>id= ID ;
    public final DRLExpressions.super_key_return super_key() throws RecognitionException {
        DRLExpressions.super_key_return retval = new DRLExpressions.super_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:357:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:357:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "super_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_super_key2647); if (state.failed) return retval;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:360:1: instanceof_key : {...}? =>id= ID -> OPERATOR[$id] ;
    public final DRLExpressions.instanceof_key_return instanceof_key() throws RecognitionException {
        DRLExpressions.instanceof_key_return retval = new DRLExpressions.instanceof_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:361:5: ({...}? =>id= ID -> OPERATOR[$id] )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:361:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "instanceof_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_instanceof_key2674); if (state.failed) return retval; 
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
            // 361:85: -> OPERATOR[$id]
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:364:1: boolean_key : {...}? =>id= ID ;
    public final DRLExpressions.boolean_key_return boolean_key() throws RecognitionException {
        DRLExpressions.boolean_key_return retval = new DRLExpressions.boolean_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:365:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:365:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "boolean_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_boolean_key2706); if (state.failed) return retval;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:368:1: char_key : {...}? =>id= ID ;
    public final DRLExpressions.char_key_return char_key() throws RecognitionException {
        DRLExpressions.char_key_return retval = new DRLExpressions.char_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:369:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:369:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "char_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_char_key2733); if (state.failed) return retval;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:372:1: byte_key : {...}? =>id= ID ;
    public final DRLExpressions.byte_key_return byte_key() throws RecognitionException {
        DRLExpressions.byte_key_return retval = new DRLExpressions.byte_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:373:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:373:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "byte_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_byte_key2760); if (state.failed) return retval;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:376:1: short_key : {...}? =>id= ID ;
    public final DRLExpressions.short_key_return short_key() throws RecognitionException {
        DRLExpressions.short_key_return retval = new DRLExpressions.short_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:377:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:377:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "short_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_short_key2787); if (state.failed) return retval;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:380:1: int_key : {...}? =>id= ID ;
    public final DRLExpressions.int_key_return int_key() throws RecognitionException {
        DRLExpressions.int_key_return retval = new DRLExpressions.int_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:381:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:381:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "int_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_int_key2814); if (state.failed) return retval;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:384:1: float_key : {...}? =>id= ID ;
    public final DRLExpressions.float_key_return float_key() throws RecognitionException {
        DRLExpressions.float_key_return retval = new DRLExpressions.float_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:385:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:385:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "float_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_float_key2841); if (state.failed) return retval;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:388:1: long_key : {...}? =>id= ID ;
    public final DRLExpressions.long_key_return long_key() throws RecognitionException {
        DRLExpressions.long_key_return retval = new DRLExpressions.long_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:389:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:389:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "long_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.LONG))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_long_key2868); if (state.failed) return retval;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:392:1: double_key : {...}? =>id= ID ;
    public final DRLExpressions.double_key_return double_key() throws RecognitionException {
        DRLExpressions.double_key_return retval = new DRLExpressions.double_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:393:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:393:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "double_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_double_key2895); if (state.failed) return retval;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:396:1: void_key : {...}? =>id= ID ;
    public final DRLExpressions.void_key_return void_key() throws RecognitionException {
        DRLExpressions.void_key_return retval = new DRLExpressions.void_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:397:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:397:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.VOID)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "void_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.VOID))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_void_key2922); if (state.failed) return retval;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:400:1: this_key : {...}? =>id= ID ;
    public final DRLExpressions.this_key_return this_key() throws RecognitionException {
        DRLExpressions.this_key_return retval = new DRLExpressions.this_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:401:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:401:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "this_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.THIS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_this_key2949); if (state.failed) return retval;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:404:1: class_key : {...}? =>id= ID ;
    public final DRLExpressions.class_key_return class_key() throws RecognitionException {
        DRLExpressions.class_key_return retval = new DRLExpressions.class_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:405:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:405:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.CLASS)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "class_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.CLASS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_class_key2976); if (state.failed) return retval;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:408:1: new_key : {...}? =>id= ID ;
    public final DRLExpressions.new_key_return new_key() throws RecognitionException {
        DRLExpressions.new_key_return retval = new DRLExpressions.new_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:409:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:409:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.NEW)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "new_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.NEW))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_new_key3003); if (state.failed) return retval;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:412:1: not_key : {...}? =>id= ID ;
    public final DRLExpressions.not_key_return not_key() throws RecognitionException {
        DRLExpressions.not_key_return retval = new DRLExpressions.not_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:413:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:413:12: {...}? =>id= ID
            {
            root_0 = (Object)adaptor.nil();

            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "not_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.NOT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_not_key3030); if (state.failed) return retval;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:416:1: in_key : {...}? =>id= ID -> OPERATOR[$id] ;
    public final DRLExpressions.in_key_return in_key() throws RecognitionException {
        DRLExpressions.in_key_return retval = new DRLExpressions.in_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:417:3: ({...}? =>id= ID -> OPERATOR[$id] )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:417:10: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "in_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.IN))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_in_key3055); if (state.failed) return retval; 
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
            // 417:75: -> OPERATOR[$id]
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:420:1: operator_key : {...}? =>id= ID -> OPERATOR[$id] ;
    public final DRLExpressions.operator_key_return operator_key() throws RecognitionException {
        DRLExpressions.operator_key_return retval = new DRLExpressions.operator_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:421:3: ({...}? =>id= ID -> OPERATOR[$id] )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:421:10: {...}? =>id= ID
            {
            if ( !(((helper.isPluggableEvaluator(false)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "operator_key", "(helper.isPluggableEvaluator(false))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_operator_key3083); if (state.failed) return retval; 
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
            // 421:58: -> OPERATOR[$id]
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:424:1: neg_operator_key : {...}? =>id= ID -> NEG_OPERATOR[$id] ;
    public final DRLExpressions.neg_operator_key_return neg_operator_key() throws RecognitionException {
        DRLExpressions.neg_operator_key_return retval = new DRLExpressions.neg_operator_key_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;

        Object id_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:425:3: ({...}? =>id= ID -> NEG_OPERATOR[$id] )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:425:10: {...}? =>id= ID
            {
            if ( !(((helper.isPluggableEvaluator(true)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "neg_operator_key", "(helper.isPluggableEvaluator(true))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_neg_operator_key3111); if (state.failed) return retval; 
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
            // 425:58: -> NEG_OPERATOR[$id]
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
        pushFollow(FOLLOW_assignmentOperator_in_synpred6_DRLExpressions415);
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

        pushFollow(FOLLOW_operator_in_synpred7_DRLExpressions629);
        operator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred7_DRLExpressions

    // $ANTLR start synpred8_DRLExpressions
    public final void synpred8_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:148:26: ( relationalOp )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:148:27: relationalOp
        {
        pushFollow(FOLLOW_relationalOp_in_synpred8_DRLExpressions902);
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
        pushFollow(FOLLOW_squareArguments_in_synpred9_DRLExpressions1007);
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
        pushFollow(FOLLOW_squareArguments_in_synpred10_DRLExpressions1027);
        squareArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred10_DRLExpressions

    // $ANTLR start synpred11_DRLExpressions
    public final void synpred11_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:169:30: ( shiftOp )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:169:31: shiftOp
        {
        pushFollow(FOLLOW_shiftOp_in_synpred11_DRLExpressions1061);
        shiftOp();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred11_DRLExpressions

    // $ANTLR start synpred12_DRLExpressions
    public final void synpred12_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:178:34: ( PLUS | MINUS )
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
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:196:9: ( castExpression )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:196:10: castExpression
        {
        pushFollow(FOLLOW_castExpression_in_synpred13_DRLExpressions1317);
        castExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred13_DRLExpressions

    // $ANTLR start synpred14_DRLExpressions
    public final void synpred14_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:197:18: ( selector )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:197:19: selector
        {
        pushFollow(FOLLOW_selector_in_synpred14_DRLExpressions1334);
        selector();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred14_DRLExpressions

    // $ANTLR start synpred15_DRLExpressions
    public final void synpred15_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:197:42: ( INCR | DECR )
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
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:201:8: ( LEFT_PAREN primitiveType )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:201:9: LEFT_PAREN primitiveType
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred16_DRLExpressions1378); if (state.failed) return ;
        pushFollow(FOLLOW_primitiveType_in_synpred16_DRLExpressions1380);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred16_DRLExpressions

    // $ANTLR start synpred17_DRLExpressions
    public final void synpred17_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:202:8: ( LEFT_PAREN type )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:202:9: LEFT_PAREN type
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred17_DRLExpressions1401); if (state.failed) return ;
        pushFollow(FOLLOW_type_in_synpred17_DRLExpressions1403);
        type();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred17_DRLExpressions

    // $ANTLR start synpred18_DRLExpressions
    public final void synpred18_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:217:7: ( parExpression )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:217:8: parExpression
        {
        pushFollow(FOLLOW_parExpression_in_synpred18_DRLExpressions1509);
        parExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred18_DRLExpressions

    // $ANTLR start synpred19_DRLExpressions
    public final void synpred19_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:218:9: ( nonWildcardTypeArguments )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:218:10: nonWildcardTypeArguments
        {
        pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred19_DRLExpressions1524);
        nonWildcardTypeArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred19_DRLExpressions

    // $ANTLR start synpred20_DRLExpressions
    public final void synpred20_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:219:9: ( literal )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:219:10: literal
        {
        pushFollow(FOLLOW_literal_in_synpred20_DRLExpressions1549);
        literal();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred20_DRLExpressions

    // $ANTLR start synpred21_DRLExpressions
    public final void synpred21_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:221:9: ( super_key )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:221:10: super_key
        {
        pushFollow(FOLLOW_super_key_in_synpred21_DRLExpressions1569);
        super_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred21_DRLExpressions

    // $ANTLR start synpred22_DRLExpressions
    public final void synpred22_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:222:9: ( new_key )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:222:10: new_key
        {
        pushFollow(FOLLOW_new_key_in_synpred22_DRLExpressions1586);
        new_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred22_DRLExpressions

    // $ANTLR start synpred23_DRLExpressions
    public final void synpred23_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:223:9: ( primitiveType )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:223:10: primitiveType
        {
        pushFollow(FOLLOW_primitiveType_in_synpred23_DRLExpressions1603);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred23_DRLExpressions

    // $ANTLR start synpred24_DRLExpressions
    public final void synpred24_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:225:9: ( inlineMapExpression )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:225:10: inlineMapExpression
        {
        pushFollow(FOLLOW_inlineMapExpression_in_synpred24_DRLExpressions1634);
        inlineMapExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred24_DRLExpressions

    // $ANTLR start synpred25_DRLExpressions
    public final void synpred25_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:226:9: ( inlineListExpression )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:226:10: inlineListExpression
        {
        pushFollow(FOLLOW_inlineListExpression_in_synpred25_DRLExpressions1649);
        inlineListExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred25_DRLExpressions

    // $ANTLR start synpred26_DRLExpressions
    public final void synpred26_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:227:9: ( ID )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:227:10: ID
        {
        match(input,ID,FOLLOW_ID_in_synpred26_DRLExpressions1664); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred26_DRLExpressions

    // $ANTLR start synpred27_DRLExpressions
    public final void synpred27_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:227:19: ( DOT ID )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:227:20: DOT ID
        {
        match(input,DOT,FOLLOW_DOT_in_synpred27_DRLExpressions1671); if (state.failed) return ;
        match(input,ID,FOLLOW_ID_in_synpred27_DRLExpressions1673); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred27_DRLExpressions

    // $ANTLR start synpred28_DRLExpressions
    public final void synpred28_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:227:39: ( identifierSuffix )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:227:40: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred28_DRLExpressions1684);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred28_DRLExpressions

    // $ANTLR start synpred29_DRLExpressions
    public final void synpred29_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:251:7: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:251:8: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred29_DRLExpressions1828); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred29_DRLExpressions1830); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred29_DRLExpressions

    // $ANTLR start synpred30_DRLExpressions
    public final void synpred30_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:252:8: ( LEFT_SQUARE )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:252:9: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred30_DRLExpressions1852); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred30_DRLExpressions

    // $ANTLR start synpred31_DRLExpressions
    public final void synpred31_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:279:107: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:279:108: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred31_DRLExpressions2058); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred31_DRLExpressions2060); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred31_DRLExpressions

    // $ANTLR start synpred32_DRLExpressions
    public final void synpred32_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:310:9: ( DOT super_key )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:310:10: DOT super_key
        {
        match(input,DOT,FOLLOW_DOT_in_synpred32_DRLExpressions2256); if (state.failed) return ;
        pushFollow(FOLLOW_super_key_in_synpred32_DRLExpressions2258);
        super_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred32_DRLExpressions

    // $ANTLR start synpred33_DRLExpressions
    public final void synpred33_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:311:9: ( DOT new_key )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:311:10: DOT new_key
        {
        match(input,DOT,FOLLOW_DOT_in_synpred33_DRLExpressions2276); if (state.failed) return ;
        pushFollow(FOLLOW_new_key_in_synpred33_DRLExpressions2278);
        new_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred33_DRLExpressions

    // $ANTLR start synpred34_DRLExpressions
    public final void synpred34_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:9: ( DOT ID )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:10: DOT ID
        {
        match(input,DOT,FOLLOW_DOT_in_synpred34_DRLExpressions2301); if (state.failed) return ;
        match(input,ID,FOLLOW_ID_in_synpred34_DRLExpressions2303); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred34_DRLExpressions

    // $ANTLR start synpred35_DRLExpressions
    public final void synpred35_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:27: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:28: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred35_DRLExpressions2312); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred35_DRLExpressions

    // $ANTLR start synpred36_DRLExpressions
    public final void synpred36_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:314:9: ( LEFT_SQUARE )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:314:10: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred36_DRLExpressions2335); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred36_DRLExpressions

    // $ANTLR start synpred37_DRLExpressions
    public final void synpred37_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:319:18: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:319:19: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred37_DRLExpressions2376); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred37_DRLExpressions

    // $ANTLR start synpred38_DRLExpressions
    public final void synpred38_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:345:7: ( GREATER GREATER GREATER )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:345:8: GREATER GREATER GREATER
        {
        match(input,GREATER,FOLLOW_GREATER_in_synpred38_DRLExpressions2557); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred38_DRLExpressions2559); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred38_DRLExpressions2561); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred38_DRLExpressions

    // $ANTLR start synpred39_DRLExpressions
    public final void synpred39_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:346:7: ( GREATER GREATER )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:346:8: GREATER GREATER
        {
        match(input,GREATER,FOLLOW_GREATER_in_synpred39_DRLExpressions2580); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred39_DRLExpressions2582); if (state.failed) return ;

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
        "\1\13\1\0\52\uffff";
    static final String DFA4_maxS =
        "\1\105\1\0\52\uffff";
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
        "\1\13\1\0\52\uffff";
    static final String DFA5_maxS =
        "\1\105\1\0\52\uffff";
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
        "\16\uffff";
    static final String DFA13_minS =
        "\1\13\13\0\2\uffff";
    static final String DFA13_maxS =
        "\1\105\13\0\2\uffff";
    static final String DFA13_acceptS =
        "\14\uffff\1\2\1\1";
    static final String DFA13_specialS =
        "\1\uffff\1\5\1\12\1\11\1\2\1\0\1\4\1\3\1\6\1\10\1\1\1\7\2\uffff}>";
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
                        int LA13_5 = input.LA(1);

                         
                        int index13_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_5);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA13_10 = input.LA(1);

                         
                        int index13_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_10);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA13_4 = input.LA(1);

                         
                        int index13_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_4);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA13_7 = input.LA(1);

                         
                        int index13_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_7);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA13_6 = input.LA(1);

                         
                        int index13_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_6);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA13_1 = input.LA(1);

                         
                        int index13_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_1);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA13_8 = input.LA(1);

                         
                        int index13_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_8);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA13_11 = input.LA(1);

                         
                        int index13_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_11);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA13_9 = input.LA(1);

                         
                        int index13_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_9);
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
                        int LA13_2 = input.LA(1);

                         
                        int index13_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_2);
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
        "\50\uffff";
    static final String DFA21_minS =
        "\1\13\3\uffff\2\0\42\uffff";
    static final String DFA21_maxS =
        "\1\105\3\uffff\2\0\42\uffff";
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
            return "()* loopback of 124:6: ( ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER ) op= operator se2= shiftExpression ) -> ^( $lop $andOrRestriction ^( $op $se2) ) )*";
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
        "\52\uffff";
    static final String DFA24_minS =
        "\1\13\1\0\50\uffff";
    static final String DFA24_maxS =
        "\1\105\1\0\50\uffff";
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
        "\53\uffff";
    static final String DFA27_minS =
        "\1\13\1\0\51\uffff";
    static final String DFA27_maxS =
        "\1\105\1\0\51\uffff";
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
        "\53\uffff";
    static final String DFA28_minS =
        "\1\13\1\0\21\uffff\2\0\26\uffff";
    static final String DFA28_maxS =
        "\1\105\1\0\21\uffff\2\0\26\uffff";
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
            return "()* loopback of 148:24: ( ( relationalOp )=> relationalOp shiftExpression )*";
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

                        else if ( (LA28_0==FLOAT||(LA28_0>=HEX && LA28_0<=DECIMAL)||LA28_0==STRING||(LA28_0>=BOOL && LA28_0<=INCR)||(LA28_0>=SEMICOLON && LA28_0<=NOT_EQUALS)||(LA28_0>=EQUALS_ASSIGN && LA28_0<=RIGHT_SQUARE)||(LA28_0>=RIGHT_CURLY && LA28_0<=COMMA)||(LA28_0>=DOUBLE_AMPER && LA28_0<=XOR)||(LA28_0>=MINUS && LA28_0<=PLUS)) ) {s = 2;}

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
        "\1\13\1\0\21\uffff";
    static final String DFA30_maxS =
        "\1\105\1\0\21\uffff";
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
        "\1\13\1\0\21\uffff";
    static final String DFA31_maxS =
        "\1\105\1\0\21\uffff";
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
        "\54\uffff";
    static final String DFA33_minS =
        "\1\13\17\uffff\2\0\32\uffff";
    static final String DFA33_maxS =
        "\1\105\17\uffff\2\0\32\uffff";
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
            return "()* loopback of 169:28: ( ( shiftOp )=>so= shiftOp ad2= additiveExpression )*";
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
        "\54\uffff";
    static final String DFA35_minS =
        "\1\13\26\uffff\2\0\23\uffff";
    static final String DFA35_maxS =
        "\1\105\26\uffff\2\0\23\uffff";
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
            return "()* loopback of 178:32: ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*";
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
        "\1\13\2\uffff\1\0\12\uffff";
    static final String DFA40_maxS =
        "\1\105\2\uffff\1\0\12\uffff";
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
            return "193:1: unaryExpressionNotPlusMinus : ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? );";
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
        "\1\13\45\uffff\1\0\7\uffff";
    static final String DFA38_maxS =
        "\1\106\45\uffff\1\0\7\uffff";
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
            return "()* loopback of 197:17: ( ( selector )=> selector )*";
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
        "\1\13\1\0\31\uffff\1\0\22\uffff";
    static final String DFA39_maxS =
        "\1\106\1\0\31\uffff\1\0\22\uffff";
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
            return "197:41: ( ( INCR | DECR )=> ( INCR | DECR ) )?";
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
        "\1\105\1\0\10\uffff";
    static final String DFA42_maxS =
        "\1\105\1\0\10\uffff";
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
            return "205:1: primitiveType : ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key );";
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
        "\1\13\10\uffff\2\0\6\uffff";
    static final String DFA47_maxS =
        "\1\105\10\uffff\2\0\6\uffff";
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
            return "216:1: primary : ( ( parExpression )=> parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )? );";
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

                        else if ( (((synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INT))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))))) ) {s = 13;}

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
        "\1\13\2\0\54\uffff";
    static final String DFA46_maxS =
        "\1\106\2\0\54\uffff";
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
            return "227:38: ( ( identifierSuffix )=> identifierSuffix )?";
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
        "\1\13\45\uffff\1\0\10\uffff";
    static final String DFA52_maxS =
        "\1\106\45\uffff\1\0\10\uffff";
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
            return "()+ loopback of 252:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+";
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
        "\1\13\1\0\55\uffff";
    static final String DFA61_maxS =
        "\1\106\1\0\55\uffff";
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
            return "()* loopback of 279:37: ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )*";
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
        "\1\13\1\0\55\uffff";
    static final String DFA70_maxS =
        "\1\106\1\0\55\uffff";
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
            return "312:26: ( ( LEFT_PAREN )=> arguments )?";
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
        "\1\13\1\0\55\uffff";
    static final String DFA72_maxS =
        "\1\106\1\0\55\uffff";
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
            return "319:17: ( ( LEFT_PAREN )=> arguments )?";
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
        "\1\30\12\uffff\2\51\2\uffff";
    static final String DFA77_maxS =
        "\1\53\12\uffff\1\51\1\53\2\uffff";
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
            return "334:1: assignmentOperator : ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN );";
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
    public static final BitSet FOLLOW_type_in_typeList194 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_COMMA_in_typeList197 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_type_in_typeList199 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_type227 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_type237 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_type239 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_ID_in_type253 = new BitSet(new long[]{0x0008440000000002L});
    public static final BitSet FOLLOW_typeArguments_in_type260 = new BitSet(new long[]{0x0008400000000002L});
    public static final BitSet FOLLOW_DOT_in_type265 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_type267 = new BitSet(new long[]{0x0008440000000002L});
    public static final BitSet FOLLOW_typeArguments_in_type274 = new BitSet(new long[]{0x0008400000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_type289 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_type291 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_LESS_in_typeArguments312 = new BitSet(new long[]{0x0040000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments314 = new BitSet(new long[]{0x0004020000000000L});
    public static final BitSet FOLLOW_COMMA_in_typeArguments317 = new BitSet(new long[]{0x0040000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments319 = new BitSet(new long[]{0x0004020000000000L});
    public static final BitSet FOLLOW_GREATER_in_typeArguments323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument340 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_typeArgument348 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_extends_key_in_typeArgument352 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_super_key_in_typeArgument356 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_type_in_typeArgument359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_dummy383 = new BitSet(new long[]{0x0000000800800000L});
    public static final BitSet FOLLOW_set_in_dummy385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression411 = new BitSet(new long[]{0x00000E00FF000002L});
    public static final BitSet FOLLOW_assignmentOperator_in_expression420 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_expression423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression440 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_conditionalExpression444 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression447 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_COLON_in_conditionalExpression449 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression452 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression470 = new BitSet(new long[]{0x0020000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression474 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression477 = new BitSet(new long[]{0x0020000000000002L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression496 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression500 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression503 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression521 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_PIPE_in_inclusiveOrExpression525 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression528 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression546 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_XOR_in_exclusiveOrExpression550 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression553 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_andOrRestriction_in_andExpression572 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_AMPER_in_andExpression576 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_andOrRestriction_in_andExpression579 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_equalityExpression_in_andOrRestriction605 = new BitSet(new long[]{0x0030000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_andOrRestriction635 = new BitSet(new long[]{0x000007E000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_andOrRestriction639 = new BitSet(new long[]{0x000007E000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_operator_in_andOrRestriction644 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_shiftExpression_in_andOrRestriction648 = new BitSet(new long[]{0x0030000000000002L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression707 = new BitSet(new long[]{0x0000006000000002L});
    public static final BitSet FOLLOW_EQUALS_in_equalityExpression713 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_NOT_EQUALS_in_equalityExpression718 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression723 = new BitSet(new long[]{0x0000006000000002L});
    public static final BitSet FOLLOW_inExpression_in_instanceOfExpression752 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_instanceof_key_in_instanceOfExpression755 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression758 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalExpression_in_inExpression787 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_not_key_in_inExpression800 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_in_key_in_inExpression804 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_inExpression806 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_inExpression808 = new BitSet(new long[]{0x0004200000000000L});
    public static final BitSet FOLLOW_COMMA_in_inExpression811 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_inExpression813 = new BitSet(new long[]{0x0004200000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_inExpression817 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_in_key_in_inExpression840 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_inExpression842 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_inExpression844 = new BitSet(new long[]{0x0004200000000000L});
    public static final BitSet FOLLOW_COMMA_in_inExpression847 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_inExpression849 = new BitSet(new long[]{0x0004200000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_inExpression853 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression897 = new BitSet(new long[]{0x000007E000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_relationalOp_in_relationalExpression906 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression909 = new BitSet(new long[]{0x000007E000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_EQUALS_in_operator927 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_EQUALS_in_operator935 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalOp_in_operator943 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_EQUALS_in_relationalOp966 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_EQUALS_in_relationalOp974 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_relationalOp983 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_relationalOp992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_key_in_relationalOp1000 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_neg_operator_key_in_relationalOp1002 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_squareArguments_in_relationalOp1011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operator_key_in_relationalOp1021 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_squareArguments_in_relationalOp1031 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression1056 = new BitSet(new long[]{0x0000060000000002L});
    public static final BitSet FOLLOW_shiftOp_in_shiftExpression1066 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression1070 = new BitSet(new long[]{0x0000060000000002L});
    public static final BitSet FOLLOW_LESS_in_shiftOp1114 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_LESS_in_shiftOp1116 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp1120 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp1122 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp1124 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp1128 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp1130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression1149 = new BitSet(new long[]{0xC000000000000002L});
    public static final BitSet FOLLOW_set_in_additiveExpression1160 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression1168 = new BitSet(new long[]{0xC000000000000002L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression1188 = new BitSet(new long[]{0x3000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression1192 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression1206 = new BitSet(new long[]{0x3000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_PLUS_in_unaryExpression1228 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression1230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unaryExpression1238 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression1240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INCR_in_unaryExpression1250 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_primary_in_unaryExpression1252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DECR_in_unaryExpression1262 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_primary_in_unaryExpression1264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression1274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_unaryExpressionNotPlusMinus1293 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus1295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus1304 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus1306 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus1320 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus1330 = new BitSet(new long[]{0x0008400300000002L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus1337 = new BitSet(new long[]{0x0008400300000002L});
    public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus1349 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression1385 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression1387 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression1389 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression1391 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression1408 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_type_in_castExpression1410 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression1412 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression1414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolean_key_in_primitiveType1435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_char_key_in_primitiveType1443 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_byte_key_in_primitiveType1451 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_short_key_in_primitiveType1459 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_int_key_in_primitiveType1467 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_long_key_in_primitiveType1475 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_float_key_in_primitiveType1483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_double_key_in_primitiveType1491 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary1513 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_primary1528 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_explicitGenericInvocationSuffix_in_primary1531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_this_key_in_primary1535 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_arguments_in_primary1537 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary1553 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_primary1573 = new BitSet(new long[]{0x0008100000000000L});
    public static final BitSet FOLLOW_superSuffix_in_primary1575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_new_key_in_primary1590 = new BitSet(new long[]{0x0000040000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_creator_in_primary1592 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary1607 = new BitSet(new long[]{0x0008400000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_primary1610 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_primary1612 = new BitSet(new long[]{0x0008400000000000L});
    public static final BitSet FOLLOW_DOT_in_primary1616 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_class_key_in_primary1618 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineMapExpression_in_primary1638 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineListExpression_in_primary1653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_primary1667 = new BitSet(new long[]{0x0008500000000002L});
    public static final BitSet FOLLOW_DOT_in_primary1676 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_primary1678 = new BitSet(new long[]{0x0008500000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary1687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineListExpression1708 = new BitSet(new long[]{0xC180D4030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_expressionList_in_inlineListExpression1710 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineListExpression1713 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineMapExpression1735 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_mapExpressionList_in_inlineMapExpression1737 = new BitSet(new long[]{0xC180D4030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineMapExpression1740 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mapEntry_in_mapExpressionList1757 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_COMMA_in_mapExpressionList1760 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_mapEntry_in_mapExpressionList1762 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_expression_in_mapEntry1785 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_COLON_in_mapEntry1787 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_mapEntry1789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_parExpression1806 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_parExpression1808 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_parExpression1810 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix1834 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix1836 = new BitSet(new long[]{0x0008400000000000L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix1840 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_class_key_in_identifierSuffix1842 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix1857 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_identifierSuffix1859 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix1861 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix1874 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator1897 = new BitSet(new long[]{0x0000040000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_createdName_in_creator1900 = new BitSet(new long[]{0x0000500000000000L});
    public static final BitSet FOLLOW_arrayCreatorRest_in_creator1911 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator1915 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_createdName1933 = new BitSet(new long[]{0x0008040000000002L});
    public static final BitSet FOLLOW_typeArguments_in_createdName1935 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_DOT_in_createdName1948 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_createdName1950 = new BitSet(new long[]{0x0008040000000002L});
    public static final BitSet FOLLOW_typeArguments_in_createdName1952 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName1967 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_innerCreator1987 = new BitSet(new long[]{0x0000500000000000L});
    public static final BitSet FOLLOW_classCreatorRest_in_innerCreator1989 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2008 = new BitSet(new long[]{0xC180D4030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2018 = new BitSet(new long[]{0x0001400000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2021 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2023 = new BitSet(new long[]{0x0001400000000000L});
    public static final BitSet FOLLOW_arrayInitializer_in_arrayCreatorRest2027 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest2041 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2043 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2048 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest2050 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2052 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2064 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2066 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer2095 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer2109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_arrayInitializer2126 = new BitSet(new long[]{0xC18354030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer2129 = new BitSet(new long[]{0x0006000000000000L});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer2132 = new BitSet(new long[]{0xC18154030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer2134 = new BitSet(new long[]{0x0006000000000000L});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer2139 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_arrayInitializer2146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest2163 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation2181 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocation2183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_nonWildcardTypeArguments2200 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments2202 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_GREATER_in_nonWildcardTypeArguments2204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_explicitGenericInvocationSuffix2221 = new BitSet(new long[]{0x0008100000000000L});
    public static final BitSet FOLLOW_superSuffix_in_explicitGenericInvocationSuffix2223 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_explicitGenericInvocationSuffix2234 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocationSuffix2236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector2261 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_super_key_in_selector2263 = new BitSet(new long[]{0x0008100000000000L});
    public static final BitSet FOLLOW_superSuffix_in_selector2265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector2281 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_new_key_in_selector2283 = new BitSet(new long[]{0x0000040000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_selector2286 = new BitSet(new long[]{0x0000040000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_innerCreator_in_selector2290 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector2306 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_selector2308 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_arguments_in_selector2317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_selector2338 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_selector2340 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_selector2342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix2359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_superSuffix2370 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_superSuffix2372 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix2381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_squareArguments2404 = new BitSet(new long[]{0xC180D4030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_expressionList_in_squareArguments2406 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_squareArguments2409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_arguments2426 = new BitSet(new long[]{0xC18074030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_expressionList_in_arguments2428 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_arguments2431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList2448 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_COMMA_in_expressionList2451 = new BitSet(new long[]{0xC18054030062C800L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_expressionList2453 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_assignmentOperator2480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_ASSIGN_in_assignmentOperator2488 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MULT_ASSIGN_in_assignmentOperator2496 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIV_ASSIGN_in_assignmentOperator2504 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_ASSIGN_in_assignmentOperator2512 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_ASSIGN_in_assignmentOperator2520 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_XOR_ASSIGN_in_assignmentOperator2528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_ASSIGN_in_assignmentOperator2536 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_assignmentOperator2544 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_LESS_in_assignmentOperator2546 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2548 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2565 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2567 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2569 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2571 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2586 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2588 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2590 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_extends_key2620 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_super_key2647 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_instanceof_key2674 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_boolean_key2706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_char_key2733 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_byte_key2760 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_short_key2787 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_int_key2814 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_float_key2841 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_long_key2868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_double_key2895 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_void_key2922 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_this_key2949 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_class_key2976 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_new_key3003 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_not_key3030 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_in_key3055 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_operator_key3083 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_neg_operator_key3111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_synpred1_DRLExpressions220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred2_DRLExpressions231 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred2_DRLExpressions233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeArguments_in_synpred3_DRLExpressions257 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeArguments_in_synpred4_DRLExpressions271 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred5_DRLExpressions283 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred5_DRLExpressions285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignmentOperator_in_synpred6_DRLExpressions415 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred7_DRLExpressions623 = new BitSet(new long[]{0x000007E000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_operator_in_synpred7_DRLExpressions629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalOp_in_synpred8_DRLExpressions902 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_squareArguments_in_synpred9_DRLExpressions1007 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_squareArguments_in_synpred10_DRLExpressions1027 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftOp_in_synpred11_DRLExpressions1061 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred12_DRLExpressions1153 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_synpred13_DRLExpressions1317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selector_in_synpred14_DRLExpressions1334 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred15_DRLExpressions1342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred16_DRLExpressions1378 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_primitiveType_in_synpred16_DRLExpressions1380 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred17_DRLExpressions1401 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_type_in_synpred17_DRLExpressions1403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_synpred18_DRLExpressions1509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred19_DRLExpressions1524 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_synpred20_DRLExpressions1549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_synpred21_DRLExpressions1569 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_new_key_in_synpred22_DRLExpressions1586 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_synpred23_DRLExpressions1603 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineMapExpression_in_synpred24_DRLExpressions1634 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineListExpression_in_synpred25_DRLExpressions1649 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_synpred26_DRLExpressions1664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred27_DRLExpressions1671 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_synpred27_DRLExpressions1673 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred28_DRLExpressions1684 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred29_DRLExpressions1828 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred29_DRLExpressions1830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred30_DRLExpressions1852 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred31_DRLExpressions2058 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred31_DRLExpressions2060 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred32_DRLExpressions2256 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_super_key_in_synpred32_DRLExpressions2258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred33_DRLExpressions2276 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_new_key_in_synpred33_DRLExpressions2278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred34_DRLExpressions2301 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_synpred34_DRLExpressions2303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred35_DRLExpressions2312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred36_DRLExpressions2335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred37_DRLExpressions2376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_synpred38_DRLExpressions2557 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred38_DRLExpressions2559 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred38_DRLExpressions2561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_synpred39_DRLExpressions2580 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred39_DRLExpressions2582 = new BitSet(new long[]{0x0000000000000002L});

}