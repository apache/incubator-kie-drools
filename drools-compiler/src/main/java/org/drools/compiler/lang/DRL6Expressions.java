/*
 * Copyright 2015 JBoss Inc
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

// $ANTLR 3.5 src/main/resources/org/drools/compiler/lang/DRL6Expressions.g 2014-12-16 14:46:34

    package org.drools.compiler.lang;

    import java.util.LinkedList;
    import org.drools.compiler.compiler.DroolsParserException;
    import org.drools.compiler.lang.ParserHelper;
    import org.drools.compiler.lang.DroolsParserExceptionFactory;
    import org.drools.compiler.lang.Location;

    import org.drools.compiler.lang.api.AnnotatedDescrBuilder;
    import org.drools.compiler.lang.api.AnnotationDescrBuilder;

    import org.drools.compiler.lang.descr.AtomicExprDescr;
    import org.drools.compiler.lang.descr.AnnotatedBaseDescr;
    import org.drools.compiler.lang.descr.AnnotationDescr;
    import org.drools.compiler.lang.descr.BaseDescr;
    import org.drools.compiler.lang.descr.ConstraintConnectiveDescr;
    import org.drools.compiler.lang.descr.RelationalExprDescr;
    import org.drools.compiler.lang.descr.BindingDescr;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@SuppressWarnings("all")
public class DRL6Expressions extends DRLExpressions {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "AMPER", "AND_ASSIGN", "ARROW", 
		"AT", "BOOL", "COLON", "COMMA", "C_STYLE_SINGLE_LINE_COMMENT", "DECIMAL", 
		"DECR", "DIV", "DIV_ASSIGN", "DOT", "DOUBLE_AMPER", "DOUBLE_PIPE", "EOL", 
		"EQUALS", "EQUALS_ASSIGN", "EscapeSequence", "Exponent", "FLOAT", "FloatTypeSuffix", 
		"GREATER", "GREATER_EQUALS", "HASH", "HEX", "HexDigit", "ID", "INCR", 
		"IdentifierPart", "IdentifierStart", "IntegerTypeSuffix", "LEFT_CURLY", 
		"LEFT_PAREN", "LEFT_SQUARE", "LESS", "LESS_EQUALS", "MINUS", "MINUS_ASSIGN", 
		"MISC", "MOD", "MOD_ASSIGN", "MULTI_LINE_COMMENT", "MULT_ASSIGN", "NEGATION", 
		"NOT_EQUALS", "NULL", "NULL_SAFE_DOT", "OR_ASSIGN", "OctalEscape", "PIPE", 
		"PLUS", "PLUS_ASSIGN", "QUESTION", "RIGHT_CURLY", "RIGHT_PAREN", "RIGHT_SQUARE", 
		"SEMICOLON", "SHARP", "STAR", "STRING", "TILDE", "TIME_INTERVAL", "UNIFY", 
		"UnicodeEscape", "WS", "XOR", "XOR_ASSIGN"
	};
	public static final int EOF=-1;
	public static final int AMPER=4;
	public static final int AND_ASSIGN=5;
	public static final int ARROW=6;
	public static final int AT=7;
	public static final int BOOL=8;
	public static final int COLON=9;
	public static final int COMMA=10;
	public static final int C_STYLE_SINGLE_LINE_COMMENT=11;
	public static final int DECIMAL=12;
	public static final int DECR=13;
	public static final int DIV=14;
	public static final int DIV_ASSIGN=15;
	public static final int DOT=16;
	public static final int DOUBLE_AMPER=17;
	public static final int DOUBLE_PIPE=18;
	public static final int EOL=19;
	public static final int EQUALS=20;
	public static final int EQUALS_ASSIGN=21;
	public static final int EscapeSequence=22;
	public static final int Exponent=23;
	public static final int FLOAT=24;
	public static final int FloatTypeSuffix=25;
	public static final int GREATER=26;
	public static final int GREATER_EQUALS=27;
	public static final int HASH=28;
	public static final int HEX=29;
	public static final int HexDigit=30;
	public static final int ID=31;
	public static final int INCR=32;
	public static final int IdentifierPart=33;
	public static final int IdentifierStart=34;
	public static final int IntegerTypeSuffix=35;
	public static final int LEFT_CURLY=36;
	public static final int LEFT_PAREN=37;
	public static final int LEFT_SQUARE=38;
	public static final int LESS=39;
	public static final int LESS_EQUALS=40;
	public static final int MINUS=41;
	public static final int MINUS_ASSIGN=42;
	public static final int MISC=43;
	public static final int MOD=44;
	public static final int MOD_ASSIGN=45;
	public static final int MULTI_LINE_COMMENT=46;
	public static final int MULT_ASSIGN=47;
	public static final int NEGATION=48;
	public static final int NOT_EQUALS=49;
	public static final int NULL=50;
	public static final int NULL_SAFE_DOT=51;
	public static final int OR_ASSIGN=52;
	public static final int OctalEscape=53;
	public static final int PIPE=54;
	public static final int PLUS=55;
	public static final int PLUS_ASSIGN=56;
	public static final int QUESTION=57;
	public static final int RIGHT_CURLY=58;
	public static final int RIGHT_PAREN=59;
	public static final int RIGHT_SQUARE=60;
	public static final int SEMICOLON=61;
	public static final int SHARP=62;
	public static final int STAR=63;
	public static final int STRING=64;
	public static final int TILDE=65;
	public static final int TIME_INTERVAL=66;
	public static final int UNIFY=67;
	public static final int UnicodeEscape=68;
	public static final int WS=69;
	public static final int XOR=70;
	public static final int XOR_ASSIGN=71;

	// delegates
	public DRLExpressions[] getDelegates() {
		return new DRLExpressions[] {};
	}

	// delegators


	public DRL6Expressions(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public DRL6Expressions(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override public String[] getTokenNames() { return DRL6Expressions.tokenNames; }
	@Override public String getGrammarFileName() { return "src/main/resources/org/drools/compiler/lang/DRL6Expressions.g"; }


	    private ParserHelper helper;

	    public DRL6Expressions(TokenStream input,
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

	    private boolean buildDescr;
	    private int inMap = 0;
	    private int ternOp = 0;
	    private boolean hasBindings;
	    public void setBuildDescr( boolean build ) { this.buildDescr = build; }
	    public boolean isBuildDescr() { return this.buildDescr; }

	    public void setLeftMostExpr( String value ) { helper.setLeftMostExpr( value ); }
	    public String getLeftMostExpr() { return helper.getLeftMostExpr(); }

	    public void setHasBindings( boolean value ) { this.hasBindings = value; }
	    public boolean hasBindings() { return this.hasBindings; }

	    private boolean isNotEOF() {
	        if (state.backtracking != 0){
	            return false;
	        }
	        if (input.get( input.index() - 1 ).getType() == DRL6Lexer.WS){
	            return true;
	        }
	        if (input.LA(-1) == DRL6Lexer.LEFT_PAREN){
	            return true;
	        }
	        return input.get( input.index() ).getType() != DRL6Lexer.EOF;
	    }


	public static class literal_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "literal"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:89:1: literal : ( STRING | DECIMAL | HEX | FLOAT | BOOL | NULL | TIME_INTERVAL | STAR );
	public final DRL6Expressions.literal_return literal() throws RecognitionException {
		DRL6Expressions.literal_return retval = new DRL6Expressions.literal_return();
		retval.start = input.LT(1);

		Token STRING1=null;
		Token DECIMAL2=null;
		Token HEX3=null;
		Token FLOAT4=null;
		Token BOOL5=null;
		Token NULL6=null;
		Token TIME_INTERVAL7=null;
		Token STAR8=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:90:5: ( STRING | DECIMAL | HEX | FLOAT | BOOL | NULL | TIME_INTERVAL | STAR )
			int alt1=8;
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
			case TIME_INTERVAL:
				{
				alt1=7;
				}
				break;
			case STAR:
				{
				alt1=8;
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
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:90:7: STRING
					{
					STRING1=(Token)match(input,STRING,FOLLOW_STRING_in_literal92); if (state.failed) return retval;
					if ( state.backtracking==0 ) {	helper.emit(STRING1, DroolsEditorType.STRING_CONST);	}
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:91:7: DECIMAL
					{
					DECIMAL2=(Token)match(input,DECIMAL,FOLLOW_DECIMAL_in_literal109); if (state.failed) return retval;
					if ( state.backtracking==0 ) {	helper.emit(DECIMAL2, DroolsEditorType.NUMERIC_CONST);	}
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:92:7: HEX
					{
					HEX3=(Token)match(input,HEX,FOLLOW_HEX_in_literal125); if (state.failed) return retval;
					if ( state.backtracking==0 ) {	helper.emit(HEX3, DroolsEditorType.NUMERIC_CONST);	}
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:93:7: FLOAT
					{
					FLOAT4=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_literal145); if (state.failed) return retval;
					if ( state.backtracking==0 ) {	helper.emit(FLOAT4, DroolsEditorType.NUMERIC_CONST);	}
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:94:7: BOOL
					{
					BOOL5=(Token)match(input,BOOL,FOLLOW_BOOL_in_literal163); if (state.failed) return retval;
					if ( state.backtracking==0 ) {	helper.emit(BOOL5, DroolsEditorType.BOOLEAN_CONST);	}
					}
					break;
				case 6 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:95:7: NULL
					{
					NULL6=(Token)match(input,NULL,FOLLOW_NULL_in_literal182); if (state.failed) return retval;
					if ( state.backtracking==0 ) {	helper.emit(NULL6, DroolsEditorType.NULL_CONST);	}
					}
					break;
				case 7 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:96:9: TIME_INTERVAL
					{
					TIME_INTERVAL7=(Token)match(input,TIME_INTERVAL,FOLLOW_TIME_INTERVAL_in_literal203); if (state.failed) return retval;
					if ( state.backtracking==0 ) {	helper.emit(TIME_INTERVAL7, DroolsEditorType.NULL_CONST); }
					}
					break;
				case 8 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:97:9: STAR
					{
					STAR8=(Token)match(input,STAR,FOLLOW_STAR_in_literal215); if (state.failed) return retval;
					if ( state.backtracking==0 ) { helper.emit(STAR8, DroolsEditorType.NUMERIC_CONST); }
					}
					break;

			}
			retval.stop = input.LT(-1);

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "literal"


	public static class operator_return extends ParserRuleReturnScope {
		public boolean negated;
		public String opr;
	};


	// $ANTLR start "operator"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:100:1: operator returns [boolean negated, String opr] : (x= TILDE )? (op= EQUALS |op= NOT_EQUALS |rop= relationalOp ) ;
	public final DRL6Expressions.operator_return operator() throws RecognitionException {
		DRL6Expressions.operator_return retval = new DRL6Expressions.operator_return();
		retval.start = input.LT(1);

		Token x=null;
		Token op=null;
		ParserRuleReturnScope rop =null;

		 if ( isNotEOF() ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR ); helper.setHasOperator( true ); 
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:103:3: ( (x= TILDE )? (op= EQUALS |op= NOT_EQUALS |rop= relationalOp ) )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:103:5: (x= TILDE )? (op= EQUALS |op= NOT_EQUALS |rop= relationalOp )
			{
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:103:6: (x= TILDE )?
			int alt2=2;
			int LA2_0 = input.LA(1);
			if ( (LA2_0==TILDE) ) {
				int LA2_1 = input.LA(2);
				if ( (LA2_1==EQUALS||(LA2_1 >= GREATER && LA2_1 <= GREATER_EQUALS)||LA2_1==ID||(LA2_1 >= LESS && LA2_1 <= LESS_EQUALS)||LA2_1==NOT_EQUALS||LA2_1==TILDE) ) {
					alt2=1;
				}
			}
			switch (alt2) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:103:6: x= TILDE
					{
					x=(Token)match(input,TILDE,FOLLOW_TILDE_in_operator256); if (state.failed) return retval;
					}
					break;

			}

			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:104:5: (op= EQUALS |op= NOT_EQUALS |rop= relationalOp )
			int alt3=3;
			int LA3_0 = input.LA(1);
			if ( (LA3_0==EQUALS) ) {
				alt3=1;
			}
			else if ( (LA3_0==NOT_EQUALS) ) {
				alt3=2;
			}
			else if ( ((LA3_0 >= GREATER && LA3_0 <= GREATER_EQUALS)||(LA3_0 >= LESS && LA3_0 <= LESS_EQUALS)||LA3_0==TILDE) ) {
				alt3=3;
			}
			else if ( (LA3_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) {
				alt3=3;
			}

			switch (alt3) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:104:7: op= EQUALS
					{
					op=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_operator267); if (state.failed) return retval;
					if ( state.backtracking==0 ) { retval.negated = false; retval.opr =(x != null ? (x!=null?x.getText():null) : "")+(op!=null?op.getText():null); helper.emit(op, DroolsEditorType.SYMBOL); }
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:105:7: op= NOT_EQUALS
					{
					op=(Token)match(input,NOT_EQUALS,FOLLOW_NOT_EQUALS_in_operator286); if (state.failed) return retval;
					if ( state.backtracking==0 ) { retval.negated = false; retval.opr =(x != null ? (x!=null?x.getText():null) : "")+(op!=null?op.getText():null); helper.emit(op, DroolsEditorType.SYMBOL); }
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:106:7: rop= relationalOp
					{
					pushFollow(FOLLOW_relationalOp_in_operator301);
					rop=relationalOp();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) { retval.negated = (rop!=null?((DRL6Expressions.relationalOp_return)rop).negated:false); retval.opr =(x != null ? (x!=null?x.getText():null) : "")+(rop!=null?((DRL6Expressions.relationalOp_return)rop).opr:null); }
					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) { if( state.backtracking == 0 && input.LA( 1 ) != DRL6Lexer.EOF) { helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); } }
		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "operator"


	public static class relationalOp_return extends ParserRuleReturnScope {
		public boolean negated;
		public String opr;
		public java.util.List<String> params;
	};


	// $ANTLR start "relationalOp"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:112:1: relationalOp returns [boolean negated, String opr, java.util.List<String> params] : (op= LESS_EQUALS |op= GREATER_EQUALS |op= LESS |op= GREATER |xop= complexOp | not_key nop= neg_operator_key |cop= operator_key ) ;
	public final DRL6Expressions.relationalOp_return relationalOp() throws RecognitionException {
		DRL6Expressions.relationalOp_return retval = new DRL6Expressions.relationalOp_return();
		retval.start = input.LT(1);

		Token op=null;
		String xop =null;
		ParserRuleReturnScope nop =null;
		ParserRuleReturnScope cop =null;

		 if ( isNotEOF() ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR ); helper.setHasOperator( true ); 
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:115:3: ( (op= LESS_EQUALS |op= GREATER_EQUALS |op= LESS |op= GREATER |xop= complexOp | not_key nop= neg_operator_key |cop= operator_key ) )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:115:5: (op= LESS_EQUALS |op= GREATER_EQUALS |op= LESS |op= GREATER |xop= complexOp | not_key nop= neg_operator_key |cop= operator_key )
			{
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:115:5: (op= LESS_EQUALS |op= GREATER_EQUALS |op= LESS |op= GREATER |xop= complexOp | not_key nop= neg_operator_key |cop= operator_key )
			int alt4=7;
			int LA4_0 = input.LA(1);
			if ( (LA4_0==LESS_EQUALS) ) {
				alt4=1;
			}
			else if ( (LA4_0==GREATER_EQUALS) ) {
				alt4=2;
			}
			else if ( (LA4_0==LESS) ) {
				alt4=3;
			}
			else if ( (LA4_0==GREATER) ) {
				alt4=4;
			}
			else if ( (LA4_0==TILDE) ) {
				alt4=5;
			}
			else if ( (LA4_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) {
				int LA4_6 = input.LA(2);
				if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
					alt4=6;
				}
				else if ( (((helper.isPluggableEvaluator(false)))) ) {
					alt4=7;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 4, 6, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			switch (alt4) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:115:7: op= LESS_EQUALS
					{
					op=(Token)match(input,LESS_EQUALS,FOLLOW_LESS_EQUALS_in_relationalOp342); if (state.failed) return retval;
					if ( state.backtracking==0 ) { retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; helper.emit(op, DroolsEditorType.SYMBOL);}
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:116:7: op= GREATER_EQUALS
					{
					op=(Token)match(input,GREATER_EQUALS,FOLLOW_GREATER_EQUALS_in_relationalOp358); if (state.failed) return retval;
					if ( state.backtracking==0 ) { retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; helper.emit(op, DroolsEditorType.SYMBOL);}
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:117:7: op= LESS
					{
					op=(Token)match(input,LESS,FOLLOW_LESS_in_relationalOp371); if (state.failed) return retval;
					if ( state.backtracking==0 ) { retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; helper.emit(op, DroolsEditorType.SYMBOL);}
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:118:7: op= GREATER
					{
					op=(Token)match(input,GREATER,FOLLOW_GREATER_in_relationalOp394); if (state.failed) return retval;
					if ( state.backtracking==0 ) { retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; helper.emit(op, DroolsEditorType.SYMBOL);}
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:119:7: xop= complexOp
					{
					pushFollow(FOLLOW_complexOp_in_relationalOp414);
					xop=complexOp();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) { retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; helper.emit(op, DroolsEditorType.SYMBOL);}
					}
					break;
				case 6 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:120:7: not_key nop= neg_operator_key
					{
					pushFollow(FOLLOW_not_key_in_relationalOp429);
					not_key();
					state._fsp--;
					if (state.failed) return retval;
					pushFollow(FOLLOW_neg_operator_key_in_relationalOp433);
					nop=neg_operator_key();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) { retval.negated = true; retval.opr =(nop!=null?input.toString(nop.start,nop.stop):null);}
					}
					break;
				case 7 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:121:7: cop= operator_key
					{
					pushFollow(FOLLOW_operator_key_in_relationalOp445);
					cop=operator_key();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) { retval.negated = false; retval.opr =(cop!=null?input.toString(cop.start,cop.stop):null);}
					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) { if( state.backtracking == 0 && input.LA( 1 ) != DRL6Lexer.EOF) { helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); } }
		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "relationalOp"



	// $ANTLR start "complexOp"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:125:1: complexOp returns [String opr] : t= TILDE e= EQUALS_ASSIGN ;
	public final String complexOp() throws RecognitionException {
		String opr = null;


		Token t=null;
		Token e=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:126:5: (t= TILDE e= EQUALS_ASSIGN )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:126:7: t= TILDE e= EQUALS_ASSIGN
			{
			t=(Token)match(input,TILDE,FOLLOW_TILDE_in_complexOp477); if (state.failed) return opr;
			e=(Token)match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_complexOp481); if (state.failed) return opr;
			if ( state.backtracking==0 ) { opr =(t!=null?t.getText():null)+(e!=null?e.getText():null); }
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return opr;
	}
	// $ANTLR end "complexOp"



	// $ANTLR start "typeList"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:129:1: typeList : type ( COMMA type )* ;
	public final void typeList() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:130:5: ( type ( COMMA type )* )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:130:7: type ( COMMA type )*
			{
			pushFollow(FOLLOW_type_in_typeList502);
			type();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:130:12: ( COMMA type )*
			loop5:
			while (true) {
				int alt5=2;
				int LA5_0 = input.LA(1);
				if ( (LA5_0==COMMA) ) {
					alt5=1;
				}

				switch (alt5) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:130:13: COMMA type
					{
					match(input,COMMA,FOLLOW_COMMA_in_typeList505); if (state.failed) return;
					pushFollow(FOLLOW_type_in_typeList507);
					type();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop5;
				}
			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "typeList"


	public static class type_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "type"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:133:1: type : tm= typeMatch ;
	public final DRL6Expressions.type_return type() throws RecognitionException {
		DRL6Expressions.type_return retval = new DRL6Expressions.type_return();
		retval.start = input.LT(1);

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:134:5: (tm= typeMatch )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:134:8: tm= typeMatch
			{
			pushFollow(FOLLOW_typeMatch_in_type529);
			typeMatch();
			state._fsp--;
			if (state.failed) return retval;
			}

			retval.stop = input.LT(-1);

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "type"



	// $ANTLR start "typeMatch"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:137:1: typeMatch : ( ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) | ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) );
	public final void typeMatch() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:138:5: ( ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) | ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) )
			int alt11=2;
			int LA11_0 = input.LA(1);
			if ( (LA11_0==ID) ) {
				int LA11_1 = input.LA(2);
				if ( (((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))&&synpred1_DRL6Expressions())) ) {
					alt11=1;
				}
				else if ( (true) ) {
					alt11=2;
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 11, 0, input);
				throw nvae;
			}

			switch (alt11) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:138:8: ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
					{
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:138:27: ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:138:29: primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
					{
					pushFollow(FOLLOW_primitiveType_in_typeMatch555);
					primitiveType();
					state._fsp--;
					if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:138:43: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
					loop6:
					while (true) {
						int alt6=2;
						int LA6_0 = input.LA(1);
						if ( (LA6_0==LEFT_SQUARE) && (synpred2_DRL6Expressions())) {
							alt6=1;
						}

						switch (alt6) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:138:44: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
							{
							match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_typeMatch565); if (state.failed) return;
							match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_typeMatch567); if (state.failed) return;
							}
							break;

						default :
							break loop6;
						}
					}

					}

					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:7: ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
					{
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:7: ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:9: ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
					{
					match(input,ID,FOLLOW_ID_in_typeMatch581); if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:12: ( ( typeArguments )=> typeArguments )?
					int alt7=2;
					int LA7_0 = input.LA(1);
					if ( (LA7_0==LESS) ) {
						int LA7_1 = input.LA(2);
						if ( (LA7_1==ID) && (synpred3_DRL6Expressions())) {
							alt7=1;
						}
						else if ( (LA7_1==QUESTION) && (synpred3_DRL6Expressions())) {
							alt7=1;
						}
					}
					switch (alt7) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:13: ( typeArguments )=> typeArguments
							{
							pushFollow(FOLLOW_typeArguments_in_typeMatch588);
							typeArguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:46: ( DOT ID ( ( typeArguments )=> typeArguments )? )*
					loop9:
					while (true) {
						int alt9=2;
						int LA9_0 = input.LA(1);
						if ( (LA9_0==DOT) ) {
							alt9=1;
						}

						switch (alt9) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:47: DOT ID ( ( typeArguments )=> typeArguments )?
							{
							match(input,DOT,FOLLOW_DOT_in_typeMatch593); if (state.failed) return;
							match(input,ID,FOLLOW_ID_in_typeMatch595); if (state.failed) return;
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:54: ( ( typeArguments )=> typeArguments )?
							int alt8=2;
							int LA8_0 = input.LA(1);
							if ( (LA8_0==LESS) ) {
								int LA8_1 = input.LA(2);
								if ( (LA8_1==ID) && (synpred4_DRL6Expressions())) {
									alt8=1;
								}
								else if ( (LA8_1==QUESTION) && (synpred4_DRL6Expressions())) {
									alt8=1;
								}
							}
							switch (alt8) {
								case 1 :
									// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:55: ( typeArguments )=> typeArguments
									{
									pushFollow(FOLLOW_typeArguments_in_typeMatch602);
									typeArguments();
									state._fsp--;
									if (state.failed) return;
									}
									break;

							}

							}
							break;

						default :
							break loop9;
						}
					}

					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:91: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
					loop10:
					while (true) {
						int alt10=2;
						int LA10_0 = input.LA(1);
						if ( (LA10_0==LEFT_SQUARE) && (synpred5_DRL6Expressions())) {
							alt10=1;
						}

						switch (alt10) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:92: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
							{
							match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_typeMatch617); if (state.failed) return;
							match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_typeMatch619); if (state.failed) return;
							}
							break;

						default :
							break loop10;
						}
					}

					}

					}
					break;

			}
		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "typeMatch"



	// $ANTLR start "typeArguments"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:142:1: typeArguments : LESS typeArgument ( COMMA typeArgument )* GREATER ;
	public final void typeArguments() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:143:5: ( LESS typeArgument ( COMMA typeArgument )* GREATER )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:143:7: LESS typeArgument ( COMMA typeArgument )* GREATER
			{
			match(input,LESS,FOLLOW_LESS_in_typeArguments640); if (state.failed) return;
			pushFollow(FOLLOW_typeArgument_in_typeArguments642);
			typeArgument();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:143:25: ( COMMA typeArgument )*
			loop12:
			while (true) {
				int alt12=2;
				int LA12_0 = input.LA(1);
				if ( (LA12_0==COMMA) ) {
					alt12=1;
				}

				switch (alt12) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:143:26: COMMA typeArgument
					{
					match(input,COMMA,FOLLOW_COMMA_in_typeArguments645); if (state.failed) return;
					pushFollow(FOLLOW_typeArgument_in_typeArguments647);
					typeArgument();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop12;
				}
			}

			match(input,GREATER,FOLLOW_GREATER_in_typeArguments651); if (state.failed) return;
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "typeArguments"



	// $ANTLR start "typeArgument"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:146:1: typeArgument : ( type | QUESTION ( ( extends_key | super_key ) type )? );
	public final void typeArgument() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:147:5: ( type | QUESTION ( ( extends_key | super_key ) type )? )
			int alt15=2;
			int LA15_0 = input.LA(1);
			if ( (LA15_0==ID) ) {
				alt15=1;
			}
			else if ( (LA15_0==QUESTION) ) {
				alt15=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 15, 0, input);
				throw nvae;
			}

			switch (alt15) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:147:7: type
					{
					pushFollow(FOLLOW_type_in_typeArgument668);
					type();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:148:7: QUESTION ( ( extends_key | super_key ) type )?
					{
					match(input,QUESTION,FOLLOW_QUESTION_in_typeArgument676); if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:148:16: ( ( extends_key | super_key ) type )?
					int alt14=2;
					int LA14_0 = input.LA(1);
					if ( (LA14_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))||((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))))) {
						alt14=1;
					}
					switch (alt14) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:148:17: ( extends_key | super_key ) type
							{
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:148:17: ( extends_key | super_key )
							int alt13=2;
							int LA13_0 = input.LA(1);
							if ( (LA13_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))||((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))))) {
								int LA13_1 = input.LA(2);
								if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))) ) {
									alt13=1;
								}
								else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
									alt13=2;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return;}
									int nvaeMark = input.mark();
									try {
										input.consume();
										NoViableAltException nvae =
											new NoViableAltException("", 13, 1, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

							}

							switch (alt13) {
								case 1 :
									// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:148:18: extends_key
									{
									pushFollow(FOLLOW_extends_key_in_typeArgument680);
									extends_key();
									state._fsp--;
									if (state.failed) return;
									}
									break;
								case 2 :
									// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:148:32: super_key
									{
									pushFollow(FOLLOW_super_key_in_typeArgument684);
									super_key();
									state._fsp--;
									if (state.failed) return;
									}
									break;

							}

							pushFollow(FOLLOW_type_in_typeArgument687);
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
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "typeArgument"



	// $ANTLR start "dummy"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:156:1: dummy : expression ( AT | SEMICOLON | EOF | ID | RIGHT_PAREN ) ;
	public final void dummy() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:157:5: ( expression ( AT | SEMICOLON | EOF | ID | RIGHT_PAREN ) )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:157:7: expression ( AT | SEMICOLON | EOF | ID | RIGHT_PAREN )
			{
			pushFollow(FOLLOW_expression_in_dummy711);
			expression();
			state._fsp--;
			if (state.failed) return;
			if ( input.LA(1)==EOF||input.LA(1)==AT||input.LA(1)==ID||input.LA(1)==RIGHT_PAREN||input.LA(1)==SEMICOLON ) {
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
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "dummy"



	// $ANTLR start "dummy2"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:159:1: dummy2 : relationalExpression EOF ;
	public final void dummy2() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:160:5: ( relationalExpression EOF )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:160:8: relationalExpression EOF
			{
			pushFollow(FOLLOW_relationalExpression_in_dummy2747);
			relationalExpression();
			state._fsp--;
			if (state.failed) return;
			match(input,EOF,FOLLOW_EOF_in_dummy2749); if (state.failed) return;
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "dummy2"


	public static class expression_return extends ParserRuleReturnScope {
		public BaseDescr result;
	};


	// $ANTLR start "expression"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:163:1: expression returns [BaseDescr result] : left= conditionalExpression ( ( assignmentOperator )=>op= assignmentOperator right= expression )? ;
	public final DRL6Expressions.expression_return expression() throws RecognitionException {
		DRL6Expressions.expression_return retval = new DRL6Expressions.expression_return();
		retval.start = input.LT(1);

		BaseDescr left =null;
		ParserRuleReturnScope right =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:164:5: (left= conditionalExpression ( ( assignmentOperator )=>op= assignmentOperator right= expression )? )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:164:7: left= conditionalExpression ( ( assignmentOperator )=>op= assignmentOperator right= expression )?
			{
			pushFollow(FOLLOW_conditionalExpression_in_expression768);
			left=conditionalExpression();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) { if( buildDescr  ) { retval.result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:165:9: ( ( assignmentOperator )=>op= assignmentOperator right= expression )?
			int alt16=2;
			switch ( input.LA(1) ) {
				case EQUALS_ASSIGN:
					{
					int LA16_1 = input.LA(2);
					if ( (synpred6_DRL6Expressions()) ) {
						alt16=1;
					}
					}
					break;
				case PLUS_ASSIGN:
					{
					int LA16_2 = input.LA(2);
					if ( (synpred6_DRL6Expressions()) ) {
						alt16=1;
					}
					}
					break;
				case MINUS_ASSIGN:
					{
					int LA16_3 = input.LA(2);
					if ( (synpred6_DRL6Expressions()) ) {
						alt16=1;
					}
					}
					break;
				case MULT_ASSIGN:
					{
					int LA16_4 = input.LA(2);
					if ( (synpred6_DRL6Expressions()) ) {
						alt16=1;
					}
					}
					break;
				case DIV_ASSIGN:
					{
					int LA16_5 = input.LA(2);
					if ( (synpred6_DRL6Expressions()) ) {
						alt16=1;
					}
					}
					break;
				case AND_ASSIGN:
					{
					int LA16_6 = input.LA(2);
					if ( (synpred6_DRL6Expressions()) ) {
						alt16=1;
					}
					}
					break;
				case OR_ASSIGN:
					{
					int LA16_7 = input.LA(2);
					if ( (synpred6_DRL6Expressions()) ) {
						alt16=1;
					}
					}
					break;
				case XOR_ASSIGN:
					{
					int LA16_8 = input.LA(2);
					if ( (synpred6_DRL6Expressions()) ) {
						alt16=1;
					}
					}
					break;
				case MOD_ASSIGN:
					{
					int LA16_9 = input.LA(2);
					if ( (synpred6_DRL6Expressions()) ) {
						alt16=1;
					}
					}
					break;
				case LESS:
					{
					int LA16_10 = input.LA(2);
					if ( (synpred6_DRL6Expressions()) ) {
						alt16=1;
					}
					}
					break;
				case GREATER:
					{
					int LA16_11 = input.LA(2);
					if ( (synpred6_DRL6Expressions()) ) {
						alt16=1;
					}
					}
					break;
			}
			switch (alt16) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:165:10: ( assignmentOperator )=>op= assignmentOperator right= expression
					{
					pushFollow(FOLLOW_assignmentOperator_in_expression789);
					assignmentOperator();
					state._fsp--;
					if (state.failed) return retval;
					pushFollow(FOLLOW_expression_in_expression793);
					right=expression();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;

			}

			}

			retval.stop = input.LT(-1);

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "expression"



	// $ANTLR start "conditionalExpression"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:168:1: conditionalExpression returns [BaseDescr result] : left= conditionalOrExpression ( ternaryExpression )? ;
	public final BaseDescr conditionalExpression() throws RecognitionException {
		BaseDescr result = null;


		BaseDescr left =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:169:5: (left= conditionalOrExpression ( ternaryExpression )? )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:169:9: left= conditionalOrExpression ( ternaryExpression )?
			{
			pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression820);
			left=conditionalOrExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:170:9: ( ternaryExpression )?
			int alt17=2;
			int LA17_0 = input.LA(1);
			if ( (LA17_0==QUESTION) ) {
				alt17=1;
			}
			switch (alt17) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:170:9: ternaryExpression
					{
					pushFollow(FOLLOW_ternaryExpression_in_conditionalExpression832);
					ternaryExpression();
					state._fsp--;
					if (state.failed) return result;
					}
					break;

			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "conditionalExpression"



	// $ANTLR start "ternaryExpression"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:173:1: ternaryExpression : QUESTION ts= expression COLON fs= expression ;
	public final void ternaryExpression() throws RecognitionException {
		ParserRuleReturnScope ts =null;
		ParserRuleReturnScope fs =null;

		 ternOp++; 
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:175:5: ( QUESTION ts= expression COLON fs= expression )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:175:7: QUESTION ts= expression COLON fs= expression
			{
			match(input,QUESTION,FOLLOW_QUESTION_in_ternaryExpression854); if (state.failed) return;
			pushFollow(FOLLOW_expression_in_ternaryExpression858);
			ts=expression();
			state._fsp--;
			if (state.failed) return;
			match(input,COLON,FOLLOW_COLON_in_ternaryExpression860); if (state.failed) return;
			pushFollow(FOLLOW_expression_in_ternaryExpression864);
			fs=expression();
			state._fsp--;
			if (state.failed) return;
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
			 ternOp--; 
		}
	}
	// $ANTLR end "ternaryExpression"



	// $ANTLR start "fullAnnotation"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:180:1: fullAnnotation[AnnotatedDescrBuilder inDescrBuilder] returns [AnnotationDescr result] : AT name= ID ( DOT x= ID )* annotationArgs[result, annoBuilder] ;
	public final AnnotationDescr fullAnnotation(AnnotatedDescrBuilder inDescrBuilder) throws RecognitionException {
		AnnotationDescr result = null;


		Token name=null;
		Token x=null;

		 String n = ""; AnnotationDescrBuilder annoBuilder = null; 
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:182:3: ( AT name= ID ( DOT x= ID )* annotationArgs[result, annoBuilder] )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:182:5: AT name= ID ( DOT x= ID )* annotationArgs[result, annoBuilder]
			{
			match(input,AT,FOLLOW_AT_in_fullAnnotation894); if (state.failed) return result;
			name=(Token)match(input,ID,FOLLOW_ID_in_fullAnnotation898); if (state.failed) return result;
			if ( state.backtracking==0 ) { n = (name!=null?name.getText():null); }
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:182:36: ( DOT x= ID )*
			loop18:
			while (true) {
				int alt18=2;
				int LA18_0 = input.LA(1);
				if ( (LA18_0==DOT) ) {
					alt18=1;
				}

				switch (alt18) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:182:38: DOT x= ID
					{
					match(input,DOT,FOLLOW_DOT_in_fullAnnotation904); if (state.failed) return result;
					x=(Token)match(input,ID,FOLLOW_ID_in_fullAnnotation908); if (state.failed) return result;
					if ( state.backtracking==0 ) { n += "." + (x!=null?x.getText():null); }
					}
					break;

				default :
					break loop18;
				}
			}

			if ( state.backtracking==0 ) { if( buildDescr ) {
			                if ( inDescrBuilder == null ) {
			                    result = new AnnotationDescr( n );
			                } else {
			                    annoBuilder = inDescrBuilder instanceof AnnotationDescrBuilder ?
			                        ((AnnotationDescrBuilder) inDescrBuilder).newAnnotation( n ) : inDescrBuilder.newAnnotation( n );
			                    result = (AnnotationDescr) annoBuilder.getDescr();
			                }
			            }
			        }
			pushFollow(FOLLOW_annotationArgs_in_fullAnnotation929);
			annotationArgs(result, annoBuilder);
			state._fsp--;
			if (state.failed) return result;
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "fullAnnotation"



	// $ANTLR start "annotationArgs"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:196:1: annotationArgs[AnnotationDescr descr, AnnotatedDescrBuilder inDescrBuilder] : LEFT_PAREN ( ( ID EQUALS_ASSIGN )=> annotationElementValuePairs[descr, inDescrBuilder] |value= annotationValue[inDescrBuilder] )? RIGHT_PAREN ;
	public final void annotationArgs(AnnotationDescr descr, AnnotatedDescrBuilder inDescrBuilder) throws RecognitionException {
		Object value =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:197:3: ( LEFT_PAREN ( ( ID EQUALS_ASSIGN )=> annotationElementValuePairs[descr, inDescrBuilder] |value= annotationValue[inDescrBuilder] )? RIGHT_PAREN )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:197:5: LEFT_PAREN ( ( ID EQUALS_ASSIGN )=> annotationElementValuePairs[descr, inDescrBuilder] |value= annotationValue[inDescrBuilder] )? RIGHT_PAREN
			{
			match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_annotationArgs945); if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:198:5: ( ( ID EQUALS_ASSIGN )=> annotationElementValuePairs[descr, inDescrBuilder] |value= annotationValue[inDescrBuilder] )?
			int alt19=3;
			int LA19_0 = input.LA(1);
			if ( (LA19_0==ID) ) {
				int LA19_1 = input.LA(2);
				if ( (synpred7_DRL6Expressions()) ) {
					alt19=1;
				}
				else if ( (true) ) {
					alt19=2;
				}
			}
			else if ( ((LA19_0 >= AT && LA19_0 <= BOOL)||(LA19_0 >= DECIMAL && LA19_0 <= DIV)||LA19_0==FLOAT||LA19_0==HEX||LA19_0==INCR||(LA19_0 >= LEFT_CURLY && LA19_0 <= LESS)||LA19_0==MINUS||LA19_0==NEGATION||LA19_0==NULL||LA19_0==PLUS||(LA19_0 >= STAR && LA19_0 <= TIME_INTERVAL)) ) {
				alt19=2;
			}
			switch (alt19) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:199:8: ( ID EQUALS_ASSIGN )=> annotationElementValuePairs[descr, inDescrBuilder]
					{
					pushFollow(FOLLOW_annotationElementValuePairs_in_annotationArgs968);
					annotationElementValuePairs(descr, inDescrBuilder);
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:200:10: value= annotationValue[inDescrBuilder]
					{
					pushFollow(FOLLOW_annotationValue_in_annotationArgs982);
					value=annotationValue(inDescrBuilder);
					state._fsp--;
					if (state.failed) return;
					if ( state.backtracking==0 ) { if ( buildDescr ) { descr.setValue( value ); } }
					}
					break;

			}

			match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_annotationArgs998); if (state.failed) return;
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "annotationArgs"



	// $ANTLR start "annotationElementValuePairs"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:205:1: annotationElementValuePairs[AnnotationDescr descr, AnnotatedDescrBuilder inDescrBuilder] : annotationElementValuePair[descr, inDescrBuilder] ( COMMA annotationElementValuePair[descr, inDescrBuilder] )* ;
	public final void annotationElementValuePairs(AnnotationDescr descr, AnnotatedDescrBuilder inDescrBuilder) throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:206:3: ( annotationElementValuePair[descr, inDescrBuilder] ( COMMA annotationElementValuePair[descr, inDescrBuilder] )* )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:206:5: annotationElementValuePair[descr, inDescrBuilder] ( COMMA annotationElementValuePair[descr, inDescrBuilder] )*
			{
			pushFollow(FOLLOW_annotationElementValuePair_in_annotationElementValuePairs1013);
			annotationElementValuePair(descr, inDescrBuilder);
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:206:55: ( COMMA annotationElementValuePair[descr, inDescrBuilder] )*
			loop20:
			while (true) {
				int alt20=2;
				int LA20_0 = input.LA(1);
				if ( (LA20_0==COMMA) ) {
					alt20=1;
				}

				switch (alt20) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:206:57: COMMA annotationElementValuePair[descr, inDescrBuilder]
					{
					match(input,COMMA,FOLLOW_COMMA_in_annotationElementValuePairs1018); if (state.failed) return;
					pushFollow(FOLLOW_annotationElementValuePair_in_annotationElementValuePairs1020);
					annotationElementValuePair(descr, inDescrBuilder);
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop20;
				}
			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "annotationElementValuePairs"



	// $ANTLR start "annotationElementValuePair"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:209:1: annotationElementValuePair[AnnotationDescr descr, AnnotatedDescrBuilder inDescrBuilder] : key= ID EQUALS_ASSIGN val= annotationValue[inDescrBuilder] ;
	public final void annotationElementValuePair(AnnotationDescr descr, AnnotatedDescrBuilder inDescrBuilder) throws RecognitionException {
		Token key=null;
		Object val =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:210:3: (key= ID EQUALS_ASSIGN val= annotationValue[inDescrBuilder] )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:210:5: key= ID EQUALS_ASSIGN val= annotationValue[inDescrBuilder]
			{
			key=(Token)match(input,ID,FOLLOW_ID_in_annotationElementValuePair1041); if (state.failed) return;
			match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_annotationElementValuePair1043); if (state.failed) return;
			pushFollow(FOLLOW_annotationValue_in_annotationElementValuePair1047);
			val=annotationValue(inDescrBuilder);
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) { if ( buildDescr ) { descr.setKeyValue( (key!=null?key.getText():null), val ); } }
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "annotationElementValuePair"



	// $ANTLR start "annotationValue"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:213:1: annotationValue[AnnotatedDescrBuilder inDescrBuilder] returns [Object result] : (exp= expression |annos= annotationArray[inDescrBuilder] |anno= fullAnnotation[inDescrBuilder] );
	public final Object annotationValue(AnnotatedDescrBuilder inDescrBuilder) throws RecognitionException {
		Object result = null;


		ParserRuleReturnScope exp =null;
		java.util.List annos =null;
		AnnotationDescr anno =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:214:3: (exp= expression |annos= annotationArray[inDescrBuilder] |anno= fullAnnotation[inDescrBuilder] )
			int alt21=3;
			switch ( input.LA(1) ) {
			case BOOL:
			case DECIMAL:
			case DECR:
			case DIV:
			case FLOAT:
			case HEX:
			case ID:
			case INCR:
			case LEFT_PAREN:
			case LEFT_SQUARE:
			case LESS:
			case MINUS:
			case NEGATION:
			case NULL:
			case PLUS:
			case STAR:
			case STRING:
			case TILDE:
			case TIME_INTERVAL:
				{
				alt21=1;
				}
				break;
			case LEFT_CURLY:
				{
				alt21=2;
				}
				break;
			case AT:
				{
				alt21=3;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return result;}
				NoViableAltException nvae =
					new NoViableAltException("", 21, 0, input);
				throw nvae;
			}
			switch (alt21) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:214:5: exp= expression
					{
					pushFollow(FOLLOW_expression_in_annotationValue1070);
					exp=expression();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { if ( buildDescr ) result = (exp!=null?input.toString(exp.start,exp.stop):null); }
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:215:7: annos= annotationArray[inDescrBuilder]
					{
					pushFollow(FOLLOW_annotationArray_in_annotationValue1082);
					annos=annotationArray(inDescrBuilder);
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { if ( buildDescr ) result = annos.toArray(); }
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:216:7: anno= fullAnnotation[inDescrBuilder]
					{
					pushFollow(FOLLOW_fullAnnotation_in_annotationValue1095);
					anno=fullAnnotation(inDescrBuilder);
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { if ( buildDescr ) result = anno; }
					}
					break;

			}
		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "annotationValue"



	// $ANTLR start "annotationArray"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:219:1: annotationArray[AnnotatedDescrBuilder inDescrBuilder] returns [java.util.List result] : LEFT_CURLY (anno= annotationValue[inDescrBuilder] ( COMMA anno= annotationValue[inDescrBuilder] )* )? RIGHT_CURLY ;
	public final java.util.List annotationArray(AnnotatedDescrBuilder inDescrBuilder) throws RecognitionException {
		java.util.List result = null;


		Object anno =null;

		 result = new java.util.ArrayList();
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:221:3: ( LEFT_CURLY (anno= annotationValue[inDescrBuilder] ( COMMA anno= annotationValue[inDescrBuilder] )* )? RIGHT_CURLY )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:221:6: LEFT_CURLY (anno= annotationValue[inDescrBuilder] ( COMMA anno= annotationValue[inDescrBuilder] )* )? RIGHT_CURLY
			{
			match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_annotationArray1122); if (state.failed) return result;
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:221:17: (anno= annotationValue[inDescrBuilder] ( COMMA anno= annotationValue[inDescrBuilder] )* )?
			int alt23=2;
			int LA23_0 = input.LA(1);
			if ( ((LA23_0 >= AT && LA23_0 <= BOOL)||(LA23_0 >= DECIMAL && LA23_0 <= DIV)||LA23_0==FLOAT||LA23_0==HEX||(LA23_0 >= ID && LA23_0 <= INCR)||(LA23_0 >= LEFT_CURLY && LA23_0 <= LESS)||LA23_0==MINUS||LA23_0==NEGATION||LA23_0==NULL||LA23_0==PLUS||(LA23_0 >= STAR && LA23_0 <= TIME_INTERVAL)) ) {
				alt23=1;
			}
			switch (alt23) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:221:19: anno= annotationValue[inDescrBuilder] ( COMMA anno= annotationValue[inDescrBuilder] )*
					{
					pushFollow(FOLLOW_annotationValue_in_annotationArray1128);
					anno=annotationValue(inDescrBuilder);
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { result.add( anno ); }
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:222:17: ( COMMA anno= annotationValue[inDescrBuilder] )*
					loop22:
					while (true) {
						int alt22=2;
						int LA22_0 = input.LA(1);
						if ( (LA22_0==COMMA) ) {
							alt22=1;
						}

						switch (alt22) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:222:19: COMMA anno= annotationValue[inDescrBuilder]
							{
							match(input,COMMA,FOLLOW_COMMA_in_annotationArray1151); if (state.failed) return result;
							pushFollow(FOLLOW_annotationValue_in_annotationArray1155);
							anno=annotationValue(inDescrBuilder);
							state._fsp--;
							if (state.failed) return result;
							if ( state.backtracking==0 ) { result.add( anno ); }
							}
							break;

						default :
							break loop22;
						}
					}

					}
					break;

			}

			match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_annotationArray1171); if (state.failed) return result;
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "annotationArray"



	// $ANTLR start "conditionalOrExpression"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:228:1: conditionalOrExpression returns [BaseDescr result] : left= conditionalAndExpression ( DOUBLE_PIPE (args= fullAnnotation[null] )? right= conditionalAndExpression )* ;
	public final BaseDescr conditionalOrExpression() throws RecognitionException {
		BaseDescr result = null;


		BaseDescr left =null;
		AnnotationDescr args =null;
		BaseDescr right =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:229:3: (left= conditionalAndExpression ( DOUBLE_PIPE (args= fullAnnotation[null] )? right= conditionalAndExpression )* )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:229:5: left= conditionalAndExpression ( DOUBLE_PIPE (args= fullAnnotation[null] )? right= conditionalAndExpression )*
			{
			pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression1192);
			left=conditionalAndExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:230:3: ( DOUBLE_PIPE (args= fullAnnotation[null] )? right= conditionalAndExpression )*
			loop25:
			while (true) {
				int alt25=2;
				int LA25_0 = input.LA(1);
				if ( (LA25_0==DOUBLE_PIPE) ) {
					alt25=1;
				}

				switch (alt25) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:230:5: DOUBLE_PIPE (args= fullAnnotation[null] )? right= conditionalAndExpression
					{
					match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression1201); if (state.failed) return result;
					if ( state.backtracking==0 ) {  if ( isNotEOF() ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR );  }
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:232:13: (args= fullAnnotation[null] )?
					int alt24=2;
					int LA24_0 = input.LA(1);
					if ( (LA24_0==AT) ) {
						alt24=1;
					}
					switch (alt24) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:232:13: args= fullAnnotation[null]
							{
							pushFollow(FOLLOW_fullAnnotation_in_conditionalOrExpression1223);
							args=fullAnnotation(null);
							state._fsp--;
							if (state.failed) return result;
							}
							break;

					}

					pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression1229);
					right=conditionalAndExpression();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { if( buildDescr  ) {
					               ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newOr();
					               descr.addOrMerge( result );
					               descr.addOrMerge( right );
					               if ( args != null ) { descr.addAnnotation( args ); }
					               result = descr;
					           }
					         }
					}
					break;

				default :
					break loop25;
				}
			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "conditionalOrExpression"



	// $ANTLR start "conditionalAndExpression"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:244:1: conditionalAndExpression returns [BaseDescr result] : left= inclusiveOrExpression ( DOUBLE_AMPER (args= fullAnnotation[null] )? right= inclusiveOrExpression )* ;
	public final BaseDescr conditionalAndExpression() throws RecognitionException {
		BaseDescr result = null;


		BaseDescr left =null;
		AnnotationDescr args =null;
		BaseDescr right =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:245:3: (left= inclusiveOrExpression ( DOUBLE_AMPER (args= fullAnnotation[null] )? right= inclusiveOrExpression )* )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:245:5: left= inclusiveOrExpression ( DOUBLE_AMPER (args= fullAnnotation[null] )? right= inclusiveOrExpression )*
			{
			pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression1264);
			left=inclusiveOrExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:246:3: ( DOUBLE_AMPER (args= fullAnnotation[null] )? right= inclusiveOrExpression )*
			loop27:
			while (true) {
				int alt27=2;
				int LA27_0 = input.LA(1);
				if ( (LA27_0==DOUBLE_AMPER) ) {
					alt27=1;
				}

				switch (alt27) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:246:5: DOUBLE_AMPER (args= fullAnnotation[null] )? right= inclusiveOrExpression
					{
					match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression1272); if (state.failed) return result;
					if ( state.backtracking==0 ) { if ( isNotEOF() ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR ); }
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:248:13: (args= fullAnnotation[null] )?
					int alt26=2;
					int LA26_0 = input.LA(1);
					if ( (LA26_0==AT) ) {
						alt26=1;
					}
					switch (alt26) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:248:13: args= fullAnnotation[null]
							{
							pushFollow(FOLLOW_fullAnnotation_in_conditionalAndExpression1295);
							args=fullAnnotation(null);
							state._fsp--;
							if (state.failed) return result;
							}
							break;

					}

					pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression1301);
					right=inclusiveOrExpression();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { if( buildDescr  ) {
					               ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newAnd();
					               descr.addOrMerge( result );
					               descr.addOrMerge( right );
					               if ( args != null ) { descr.addAnnotation( args ); }
					               result = descr;
					           }
					         }
					}
					break;

				default :
					break loop27;
				}
			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "conditionalAndExpression"



	// $ANTLR start "inclusiveOrExpression"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:260:1: inclusiveOrExpression returns [BaseDescr result] : left= exclusiveOrExpression ( PIPE right= exclusiveOrExpression )* ;
	public final BaseDescr inclusiveOrExpression() throws RecognitionException {
		BaseDescr result = null;


		BaseDescr left =null;
		BaseDescr right =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:261:3: (left= exclusiveOrExpression ( PIPE right= exclusiveOrExpression )* )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:261:5: left= exclusiveOrExpression ( PIPE right= exclusiveOrExpression )*
			{
			pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression1336);
			left=exclusiveOrExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:262:3: ( PIPE right= exclusiveOrExpression )*
			loop28:
			while (true) {
				int alt28=2;
				int LA28_0 = input.LA(1);
				if ( (LA28_0==PIPE) ) {
					alt28=1;
				}

				switch (alt28) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:262:5: PIPE right= exclusiveOrExpression
					{
					match(input,PIPE,FOLLOW_PIPE_in_inclusiveOrExpression1344); if (state.failed) return result;
					pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression1348);
					right=exclusiveOrExpression();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { if( buildDescr  ) {
					               ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newIncOr();
					               descr.addOrMerge( result );
					               descr.addOrMerge( right );
					               result = descr;
					           }
					         }
					}
					break;

				default :
					break loop28;
				}
			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "inclusiveOrExpression"



	// $ANTLR start "exclusiveOrExpression"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:273:1: exclusiveOrExpression returns [BaseDescr result] : left= andExpression ( XOR right= andExpression )* ;
	public final BaseDescr exclusiveOrExpression() throws RecognitionException {
		BaseDescr result = null;


		BaseDescr left =null;
		BaseDescr right =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:274:3: (left= andExpression ( XOR right= andExpression )* )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:274:5: left= andExpression ( XOR right= andExpression )*
			{
			pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression1383);
			left=andExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:275:3: ( XOR right= andExpression )*
			loop29:
			while (true) {
				int alt29=2;
				int LA29_0 = input.LA(1);
				if ( (LA29_0==XOR) ) {
					alt29=1;
				}

				switch (alt29) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:275:5: XOR right= andExpression
					{
					match(input,XOR,FOLLOW_XOR_in_exclusiveOrExpression1391); if (state.failed) return result;
					pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression1395);
					right=andExpression();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { if( buildDescr  ) {
					               ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newXor();
					               descr.addOrMerge( result );
					               descr.addOrMerge( right );
					               result = descr;
					           }
					         }
					}
					break;

				default :
					break loop29;
				}
			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "exclusiveOrExpression"



	// $ANTLR start "andExpression"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:286:1: andExpression returns [BaseDescr result] : left= equalityExpression ( AMPER right= equalityExpression )* ;
	public final BaseDescr andExpression() throws RecognitionException {
		BaseDescr result = null;


		BaseDescr left =null;
		BaseDescr right =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:287:3: (left= equalityExpression ( AMPER right= equalityExpression )* )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:287:5: left= equalityExpression ( AMPER right= equalityExpression )*
			{
			pushFollow(FOLLOW_equalityExpression_in_andExpression1430);
			left=equalityExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:288:3: ( AMPER right= equalityExpression )*
			loop30:
			while (true) {
				int alt30=2;
				int LA30_0 = input.LA(1);
				if ( (LA30_0==AMPER) ) {
					alt30=1;
				}

				switch (alt30) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:288:5: AMPER right= equalityExpression
					{
					match(input,AMPER,FOLLOW_AMPER_in_andExpression1438); if (state.failed) return result;
					pushFollow(FOLLOW_equalityExpression_in_andExpression1442);
					right=equalityExpression();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { if( buildDescr  ) {
					               ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newIncAnd();
					               descr.addOrMerge( result );
					               descr.addOrMerge( right );
					               result = descr;
					           }
					         }
					}
					break;

				default :
					break loop30;
				}
			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "andExpression"



	// $ANTLR start "equalityExpression"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:299:1: equalityExpression returns [BaseDescr result] : left= instanceOfExpression ( (op= EQUALS |op= NOT_EQUALS ) right= instanceOfExpression )* ;
	public final BaseDescr equalityExpression() throws RecognitionException {
		BaseDescr result = null;


		Token op=null;
		BaseDescr left =null;
		BaseDescr right =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:300:3: (left= instanceOfExpression ( (op= EQUALS |op= NOT_EQUALS ) right= instanceOfExpression )* )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:300:5: left= instanceOfExpression ( (op= EQUALS |op= NOT_EQUALS ) right= instanceOfExpression )*
			{
			pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression1477);
			left=instanceOfExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:301:3: ( (op= EQUALS |op= NOT_EQUALS ) right= instanceOfExpression )*
			loop32:
			while (true) {
				int alt32=2;
				int LA32_0 = input.LA(1);
				if ( (LA32_0==EQUALS||LA32_0==NOT_EQUALS) ) {
					alt32=1;
				}

				switch (alt32) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:301:5: (op= EQUALS |op= NOT_EQUALS ) right= instanceOfExpression
					{
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:301:5: (op= EQUALS |op= NOT_EQUALS )
					int alt31=2;
					int LA31_0 = input.LA(1);
					if ( (LA31_0==EQUALS) ) {
						alt31=1;
					}
					else if ( (LA31_0==NOT_EQUALS) ) {
						alt31=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return result;}
						NoViableAltException nvae =
							new NoViableAltException("", 31, 0, input);
						throw nvae;
					}

					switch (alt31) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:301:7: op= EQUALS
							{
							op=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_equalityExpression1489); if (state.failed) return result;
							}
							break;
						case 2 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:301:19: op= NOT_EQUALS
							{
							op=(Token)match(input,NOT_EQUALS,FOLLOW_NOT_EQUALS_in_equalityExpression1495); if (state.failed) return result;
							}
							break;

					}

					if ( state.backtracking==0 ) {  helper.setHasOperator( true );
					       if( input.LA( 1 ) != DRL6Lexer.EOF ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); }
					pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression1511);
					right=instanceOfExpression();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { if( buildDescr  ) {
					               result = new RelationalExprDescr( (op!=null?op.getText():null), false, null, left, right );
					           }
					         }
					}
					break;

				default :
					break loop32;
				}
			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "equalityExpression"



	// $ANTLR start "instanceOfExpression"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:312:1: instanceOfExpression returns [BaseDescr result] : left= inExpression (op= instanceof_key right= type )? ;
	public final BaseDescr instanceOfExpression() throws RecognitionException {
		BaseDescr result = null;


		BaseDescr left =null;
		ParserRuleReturnScope op =null;
		ParserRuleReturnScope right =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:313:3: (left= inExpression (op= instanceof_key right= type )? )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:313:5: left= inExpression (op= instanceof_key right= type )?
			{
			pushFollow(FOLLOW_inExpression_in_instanceOfExpression1546);
			left=inExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:314:3: (op= instanceof_key right= type )?
			int alt33=2;
			int LA33_0 = input.LA(1);
			if ( (LA33_0==ID) ) {
				int LA33_1 = input.LA(2);
				if ( (LA33_1==ID) && (((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))))) {
					alt33=1;
				}
			}
			switch (alt33) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:314:5: op= instanceof_key right= type
					{
					pushFollow(FOLLOW_instanceof_key_in_instanceOfExpression1556);
					op=instanceof_key();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) {  helper.setHasOperator( true );
					       if( input.LA( 1 ) != DRL6Lexer.EOF ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); }
					pushFollow(FOLLOW_type_in_instanceOfExpression1570);
					right=type();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { if( buildDescr  ) {
					               result = new RelationalExprDescr( (op!=null?input.toString(op.start,op.stop):null), false, null, left, new AtomicExprDescr((right!=null?input.toString(right.start,right.stop):null)) );
					           }
					         }
					}
					break;

			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "instanceOfExpression"



	// $ANTLR start "inExpression"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:325:1: inExpression returns [BaseDescr result] : left= relationalExpression ( ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN |in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN )? ;
	public final BaseDescr inExpression() throws RecognitionException {
		BaseDescr result = null;


		BaseDescr left =null;
		ParserRuleReturnScope e1 =null;
		ParserRuleReturnScope e2 =null;

		 ConstraintConnectiveDescr descr = null; BaseDescr leftDescr = null; BindingDescr binding = null; 
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:328:3: (left= relationalExpression ( ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN |in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN )? )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:328:5: left= relationalExpression ( ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN |in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN )?
			{
			pushFollow(FOLLOW_relationalExpression_in_inExpression1615);
			left=relationalExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; }
			      if( left instanceof BindingDescr ) {
			          binding = (BindingDescr)left;
			          leftDescr = new AtomicExprDescr( binding.getExpression() );
			      } else {
			          leftDescr = left;
			      }
			    }
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:337:5: ( ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN |in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN )?
			int alt36=3;
			int LA36_0 = input.LA(1);
			if ( (LA36_0==ID) ) {
				int LA36_1 = input.LA(2);
				if ( (LA36_1==ID) ) {
					int LA36_3 = input.LA(3);
					if ( (LA36_3==LEFT_PAREN) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))&&synpred8_DRL6Expressions()))) {
						alt36=1;
					}
				}
				else if ( (LA36_1==LEFT_PAREN) && (((helper.validateIdentifierKey(DroolsSoftKeywords.IN))))) {
					alt36=2;
				}
			}
			switch (alt36) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:337:6: ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN
					{
					pushFollow(FOLLOW_not_key_in_inExpression1635);
					not_key();
					state._fsp--;
					if (state.failed) return result;
					pushFollow(FOLLOW_in_key_in_inExpression1639);
					in_key();
					state._fsp--;
					if (state.failed) return result;
					match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_inExpression1641); if (state.failed) return result;
					if ( state.backtracking==0 ) {   helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); }
					pushFollow(FOLLOW_expression_in_inExpression1663);
					e1=expression();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) {   descr = ConstraintConnectiveDescr.newAnd();
					            RelationalExprDescr rel = new RelationalExprDescr( "!=", false, null, leftDescr, (e1!=null?((DRL6Expressions.expression_return)e1).result:null) );
					            descr.addOrMerge( rel );
					            result = descr;
					        }
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:345:7: ( COMMA e2= expression )*
					loop34:
					while (true) {
						int alt34=2;
						int LA34_0 = input.LA(1);
						if ( (LA34_0==COMMA) ) {
							alt34=1;
						}

						switch (alt34) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:345:8: COMMA e2= expression
							{
							match(input,COMMA,FOLLOW_COMMA_in_inExpression1682); if (state.failed) return result;
							pushFollow(FOLLOW_expression_in_inExpression1686);
							e2=expression();
							state._fsp--;
							if (state.failed) return result;
							if ( state.backtracking==0 ) {   RelationalExprDescr rel = new RelationalExprDescr( "!=", false, null, leftDescr, (e2!=null?((DRL6Expressions.expression_return)e2).result:null) );
							            descr.addOrMerge( rel );
							        }
							}
							break;

						default :
							break loop34;
						}
					}

					match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_inExpression1707); if (state.failed) return result;
					if ( state.backtracking==0 ) { helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_END ); }
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:351:7: in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN
					{
					pushFollow(FOLLOW_in_key_in_inExpression1723);
					in_key();
					state._fsp--;
					if (state.failed) return result;
					match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_inExpression1725); if (state.failed) return result;
					if ( state.backtracking==0 ) {   helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); }
					pushFollow(FOLLOW_expression_in_inExpression1747);
					e1=expression();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) {   descr = ConstraintConnectiveDescr.newOr();
					            RelationalExprDescr rel = new RelationalExprDescr( "==", false, null, leftDescr, (e1!=null?((DRL6Expressions.expression_return)e1).result:null) );
					            descr.addOrMerge( rel );
					            result = descr;
					        }
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:359:7: ( COMMA e2= expression )*
					loop35:
					while (true) {
						int alt35=2;
						int LA35_0 = input.LA(1);
						if ( (LA35_0==COMMA) ) {
							alt35=1;
						}

						switch (alt35) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:359:8: COMMA e2= expression
							{
							match(input,COMMA,FOLLOW_COMMA_in_inExpression1766); if (state.failed) return result;
							pushFollow(FOLLOW_expression_in_inExpression1770);
							e2=expression();
							state._fsp--;
							if (state.failed) return result;
							if ( state.backtracking==0 ) {   RelationalExprDescr rel = new RelationalExprDescr( "==", false, null, leftDescr, (e2!=null?((DRL6Expressions.expression_return)e2).result:null) );
							            descr.addOrMerge( rel );
							        }
							}
							break;

						default :
							break loop35;
						}
					}

					match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_inExpression1791); if (state.failed) return result;
					if ( state.backtracking==0 ) { helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_END ); }
					}
					break;

			}

			}

			if ( state.backtracking==0 ) { if( binding != null && descr != null ) descr.addOrMerge( binding ); }
		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "inExpression"


	protected static class relationalExpression_scope {
		BaseDescr lsd;
	}
	protected Stack<relationalExpression_scope> relationalExpression_stack = new Stack<relationalExpression_scope>();


	// $ANTLR start "relationalExpression"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:368:1: relationalExpression returns [BaseDescr result] : left= shiftExpression ( ( operator | LEFT_PAREN )=>right= orRestriction )* ;
	public final BaseDescr relationalExpression() throws RecognitionException {
		relationalExpression_stack.push(new relationalExpression_scope());
		BaseDescr result = null;


		ParserRuleReturnScope left =null;
		BaseDescr right =null;

		 relationalExpression_stack.peek().lsd = null; 
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:371:3: (left= shiftExpression ( ( operator | LEFT_PAREN )=>right= orRestriction )* )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:371:5: left= shiftExpression ( ( operator | LEFT_PAREN )=>right= orRestriction )*
			{
			pushFollow(FOLLOW_shiftExpression_in_relationalExpression1832);
			left=shiftExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) {
			          if ( (left!=null?((DRL6Expressions.shiftExpression_return)left).result:null) == null ) {
			            result = new AtomicExprDescr( (left!=null?input.toString(left.start,left.stop):null) );
			          } else if ( (left!=null?((DRL6Expressions.shiftExpression_return)left).result:null) instanceof AtomicExprDescr ) {
			            if ( (left!=null?input.toString(left.start,left.stop):null).equals(((AtomicExprDescr)(left!=null?((DRL6Expressions.shiftExpression_return)left).result:null)).getExpression()) ) {
			              result = (left!=null?((DRL6Expressions.shiftExpression_return)left).result:null);
			            } else {
			              result = new AtomicExprDescr( (left!=null?input.toString(left.start,left.stop):null) ) ;
			            }
			          } else if ( (left!=null?((DRL6Expressions.shiftExpression_return)left).result:null) instanceof BindingDescr ) {
			              if ( (left!=null?input.toString(left.start,left.stop):null).equals(((BindingDescr)(left!=null?((DRL6Expressions.shiftExpression_return)left).result:null)).getExpression()) ) {
			                result = (left!=null?((DRL6Expressions.shiftExpression_return)left).result:null);
			              } else {
			                BindingDescr bind = (BindingDescr) (left!=null?((DRL6Expressions.shiftExpression_return)left).result:null);
			                int offset = bind.isUnification() ? 2 : 1;
			                String fullExpression = (left!=null?input.toString(left.start,left.stop):null).substring( (left!=null?input.toString(left.start,left.stop):null).indexOf( ":" ) + offset ).trim();
			                result = new BindingDescr( bind.getVariable(), bind.getExpression(), fullExpression, bind.isUnification() );
			              }
			          } else {
			              result = (left!=null?((DRL6Expressions.shiftExpression_return)left).result:null);
			          }
			          relationalExpression_stack.peek().lsd = result;
			      } 
			    }
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:396:3: ( ( operator | LEFT_PAREN )=>right= orRestriction )*
			loop37:
			while (true) {
				int alt37=2;
				int LA37_0 = input.LA(1);
				if ( (LA37_0==ID) ) {
					int LA37_2 = input.LA(2);
					if ( ((synpred9_DRL6Expressions()&&(((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) ) {
						alt37=1;
					}

				}
				else if ( (LA37_0==EQUALS) ) {
					int LA37_3 = input.LA(2);
					if ( (synpred9_DRL6Expressions()) ) {
						alt37=1;
					}

				}
				else if ( (LA37_0==NOT_EQUALS) ) {
					int LA37_4 = input.LA(2);
					if ( (synpred9_DRL6Expressions()) ) {
						alt37=1;
					}

				}
				else if ( (LA37_0==LESS) ) {
					int LA37_20 = input.LA(2);
					if ( (synpred9_DRL6Expressions()) ) {
						alt37=1;
					}

				}
				else if ( (LA37_0==GREATER) ) {
					int LA37_21 = input.LA(2);
					if ( (synpred9_DRL6Expressions()) ) {
						alt37=1;
					}

				}
				else if ( (LA37_0==TILDE) && (synpred9_DRL6Expressions())) {
					alt37=1;
				}
				else if ( (LA37_0==LESS_EQUALS) && (synpred9_DRL6Expressions())) {
					alt37=1;
				}
				else if ( (LA37_0==GREATER_EQUALS) && (synpred9_DRL6Expressions())) {
					alt37=1;
				}
				else if ( (LA37_0==LEFT_PAREN) && (synpred9_DRL6Expressions())) {
					alt37=1;
				}

				switch (alt37) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:396:5: ( operator | LEFT_PAREN )=>right= orRestriction
					{
					pushFollow(FOLLOW_orRestriction_in_relationalExpression1857);
					right=orRestriction();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { if( buildDescr  ) {
					               result = right;
					               relationalExpression_stack.peek().lsd = result;
					           }
					         }
					}
					break;

				default :
					break loop37;
				}
			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
			relationalExpression_stack.pop();
		}
		return result;
	}
	// $ANTLR end "relationalExpression"



	// $ANTLR start "orRestriction"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:405:1: orRestriction returns [BaseDescr result] : left= andRestriction ( ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )=>lop= DOUBLE_PIPE (args= fullAnnotation[null] )? right= andRestriction )* ( EOF )? ;
	public final BaseDescr orRestriction() throws RecognitionException {
		BaseDescr result = null;


		Token lop=null;
		BaseDescr left =null;
		AnnotationDescr args =null;
		BaseDescr right =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:406:3: (left= andRestriction ( ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )=>lop= DOUBLE_PIPE (args= fullAnnotation[null] )? right= andRestriction )* ( EOF )? )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:406:5: left= andRestriction ( ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )=>lop= DOUBLE_PIPE (args= fullAnnotation[null] )? right= andRestriction )* ( EOF )?
			{
			pushFollow(FOLLOW_andRestriction_in_orRestriction1892);
			left=andRestriction();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:407:5: ( ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )=>lop= DOUBLE_PIPE (args= fullAnnotation[null] )? right= andRestriction )*
			loop39:
			while (true) {
				int alt39=2;
				int LA39_0 = input.LA(1);
				if ( (LA39_0==DOUBLE_PIPE) ) {
					int LA39_9 = input.LA(2);
					if ( (synpred10_DRL6Expressions()) ) {
						alt39=1;
					}

				}

				switch (alt39) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:407:7: ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )=>lop= DOUBLE_PIPE (args= fullAnnotation[null] )? right= andRestriction
					{
					lop=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_orRestriction1914); if (state.failed) return result;
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:407:79: (args= fullAnnotation[null] )?
					int alt38=2;
					int LA38_0 = input.LA(1);
					if ( (LA38_0==AT) ) {
						alt38=1;
					}
					switch (alt38) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:407:79: args= fullAnnotation[null]
							{
							pushFollow(FOLLOW_fullAnnotation_in_orRestriction1918);
							args=fullAnnotation(null);
							state._fsp--;
							if (state.failed) return result;
							}
							break;

					}

					pushFollow(FOLLOW_andRestriction_in_orRestriction1924);
					right=andRestriction();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { if( buildDescr ) {
					               ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newOr();
					               descr.addOrMerge( result );
					               descr.addOrMerge( right );
					               if ( args != null ) { descr.addAnnotation( args ); }
					               result = descr;
					           }
					         }
					}
					break;

				default :
					break loop39;
				}
			}

			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:416:7: ( EOF )?
			int alt40=2;
			int LA40_0 = input.LA(1);
			if ( (LA40_0==EOF) ) {
				int LA40_1 = input.LA(2);
				if ( (LA40_1==EOF) ) {
					int LA40_3 = input.LA(3);
					if ( (LA40_3==EOF) ) {
						alt40=1;
					}
				}
				else if ( ((LA40_1 >= AMPER && LA40_1 <= AND_ASSIGN)||LA40_1==AT||(LA40_1 >= COLON && LA40_1 <= COMMA)||LA40_1==DIV_ASSIGN||(LA40_1 >= DOUBLE_AMPER && LA40_1 <= DOUBLE_PIPE)||(LA40_1 >= EQUALS && LA40_1 <= EQUALS_ASSIGN)||(LA40_1 >= GREATER && LA40_1 <= GREATER_EQUALS)||LA40_1==ID||LA40_1==LEFT_PAREN||(LA40_1 >= LESS && LA40_1 <= LESS_EQUALS)||LA40_1==MINUS_ASSIGN||LA40_1==MOD_ASSIGN||LA40_1==MULT_ASSIGN||LA40_1==NOT_EQUALS||LA40_1==OR_ASSIGN||LA40_1==PIPE||(LA40_1 >= PLUS_ASSIGN && LA40_1 <= SEMICOLON)||LA40_1==TILDE||(LA40_1 >= XOR && LA40_1 <= XOR_ASSIGN)) ) {
					alt40=1;
				}
			}
			switch (alt40) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:416:7: EOF
					{
					match(input,EOF,FOLLOW_EOF_in_orRestriction1943); if (state.failed) return result;
					}
					break;

			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "orRestriction"



	// $ANTLR start "andRestriction"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:419:1: andRestriction returns [BaseDescr result] : left= singleRestriction ( ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )=>lop= DOUBLE_AMPER (args= fullAnnotation[null] )? right= singleRestriction )* ;
	public final BaseDescr andRestriction() throws RecognitionException {
		BaseDescr result = null;


		Token lop=null;
		BaseDescr left =null;
		AnnotationDescr args =null;
		BaseDescr right =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:420:3: (left= singleRestriction ( ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )=>lop= DOUBLE_AMPER (args= fullAnnotation[null] )? right= singleRestriction )* )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:420:5: left= singleRestriction ( ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )=>lop= DOUBLE_AMPER (args= fullAnnotation[null] )? right= singleRestriction )*
			{
			pushFollow(FOLLOW_singleRestriction_in_andRestriction1963);
			left=singleRestriction();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:421:3: ( ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )=>lop= DOUBLE_AMPER (args= fullAnnotation[null] )? right= singleRestriction )*
			loop42:
			while (true) {
				int alt42=2;
				int LA42_0 = input.LA(1);
				if ( (LA42_0==DOUBLE_AMPER) ) {
					int LA42_9 = input.LA(2);
					if ( (synpred11_DRL6Expressions()) ) {
						alt42=1;
					}

				}

				switch (alt42) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:421:5: ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )=>lop= DOUBLE_AMPER (args= fullAnnotation[null] )? right= singleRestriction
					{
					lop=(Token)match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_andRestriction1983); if (state.failed) return result;
					if ( state.backtracking==0 ) { if ( isNotEOF() ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR ); }
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:423:13: (args= fullAnnotation[null] )?
					int alt41=2;
					int LA41_0 = input.LA(1);
					if ( (LA41_0==AT) ) {
						alt41=1;
					}
					switch (alt41) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:423:13: args= fullAnnotation[null]
							{
							pushFollow(FOLLOW_fullAnnotation_in_andRestriction2004);
							args=fullAnnotation(null);
							state._fsp--;
							if (state.failed) return result;
							}
							break;

					}

					pushFollow(FOLLOW_singleRestriction_in_andRestriction2009);
					right=singleRestriction();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { if( buildDescr  ) {
					               ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newAnd();
					               descr.addOrMerge( result );
					               descr.addOrMerge( right );
					               if ( args != null ) { descr.addAnnotation( args ); }
					               result = descr;
					           }
					         }
					}
					break;

				default :
					break loop42;
				}
			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "andRestriction"



	// $ANTLR start "singleRestriction"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:435:1: singleRestriction returns [BaseDescr result] : (op= operator ( ( squareArguments shiftExpression )=>sa= squareArguments value= shiftExpression |value= shiftExpression ) | LEFT_PAREN or= orRestriction RIGHT_PAREN );
	public final BaseDescr singleRestriction() throws RecognitionException {
		BaseDescr result = null;


		ParserRuleReturnScope op =null;
		java.util.List<String> sa =null;
		ParserRuleReturnScope value =null;
		BaseDescr or =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:436:3: (op= operator ( ( squareArguments shiftExpression )=>sa= squareArguments value= shiftExpression |value= shiftExpression ) | LEFT_PAREN or= orRestriction RIGHT_PAREN )
			int alt44=2;
			int LA44_0 = input.LA(1);
			if ( (LA44_0==EQUALS||(LA44_0 >= GREATER && LA44_0 <= GREATER_EQUALS)||(LA44_0 >= LESS && LA44_0 <= LESS_EQUALS)||LA44_0==NOT_EQUALS||LA44_0==TILDE) ) {
				alt44=1;
			}
			else if ( (LA44_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) {
				alt44=1;
			}
			else if ( (LA44_0==LEFT_PAREN) ) {
				alt44=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return result;}
				NoViableAltException nvae =
					new NoViableAltException("", 44, 0, input);
				throw nvae;
			}

			switch (alt44) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:436:6: op= operator ( ( squareArguments shiftExpression )=>sa= squareArguments value= shiftExpression |value= shiftExpression )
					{
					pushFollow(FOLLOW_operator_in_singleRestriction2045);
					op=operator();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); }
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:438:6: ( ( squareArguments shiftExpression )=>sa= squareArguments value= shiftExpression |value= shiftExpression )
					int alt43=2;
					int LA43_0 = input.LA(1);
					if ( (LA43_0==LEFT_SQUARE) ) {
						int LA43_1 = input.LA(2);
						if ( (synpred12_DRL6Expressions()) ) {
							alt43=1;
						}
						else if ( (true) ) {
							alt43=2;
						}

					}
					else if ( (LA43_0==BOOL||(LA43_0 >= DECIMAL && LA43_0 <= DIV)||LA43_0==FLOAT||LA43_0==HEX||(LA43_0 >= ID && LA43_0 <= INCR)||LA43_0==LEFT_PAREN||LA43_0==LESS||LA43_0==MINUS||LA43_0==NEGATION||LA43_0==NULL||LA43_0==PLUS||(LA43_0 >= STAR && LA43_0 <= TIME_INTERVAL)) ) {
						alt43=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return result;}
						NoViableAltException nvae =
							new NoViableAltException("", 43, 0, input);
						throw nvae;
					}

					switch (alt43) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:438:8: ( squareArguments shiftExpression )=>sa= squareArguments value= shiftExpression
							{
							pushFollow(FOLLOW_squareArguments_in_singleRestriction2074);
							sa=squareArguments();
							state._fsp--;
							if (state.failed) return result;
							pushFollow(FOLLOW_shiftExpression_in_singleRestriction2078);
							value=shiftExpression();
							state._fsp--;
							if (state.failed) return result;
							}
							break;
						case 2 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:439:10: value= shiftExpression
							{
							pushFollow(FOLLOW_shiftExpression_in_singleRestriction2091);
							value=shiftExpression();
							state._fsp--;
							if (state.failed) return result;
							}
							break;

					}

					if ( state.backtracking==0 ) { if( buildDescr  ) {
					               BaseDescr descr = ( (value!=null?((DRL6Expressions.shiftExpression_return)value).result:null) != null &&
					                                 ( (!((value!=null?((DRL6Expressions.shiftExpression_return)value).result:null) instanceof AtomicExprDescr)) ||
					                                   ((value!=null?input.toString(value.start,value.stop):null).equals(((AtomicExprDescr)(value!=null?((DRL6Expressions.shiftExpression_return)value).result:null)).getExpression())) )) ?
							                    (value!=null?((DRL6Expressions.shiftExpression_return)value).result:null) :
							                    new AtomicExprDescr( (value!=null?input.toString(value.start,value.stop):null) ) ;
					               result = new RelationalExprDescr( (op!=null?((DRL6Expressions.operator_return)op).opr:null), (op!=null?((DRL6Expressions.operator_return)op).negated:false), sa, relationalExpression_stack.peek().lsd, descr );
						       if( relationalExpression_stack.peek().lsd instanceof BindingDescr ) {
						           relationalExpression_stack.peek().lsd = new AtomicExprDescr( ((BindingDescr)relationalExpression_stack.peek().lsd).getExpression() );
						       }
					           }
					           helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_END );
					         }
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:454:6: LEFT_PAREN or= orRestriction RIGHT_PAREN
					{
					match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_singleRestriction2116); if (state.failed) return result;
					pushFollow(FOLLOW_orRestriction_in_singleRestriction2120);
					or=orRestriction();
					state._fsp--;
					if (state.failed) return result;
					match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_singleRestriction2122); if (state.failed) return result;
					if ( state.backtracking==0 ) { result = or; }
					}
					break;

			}
		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "singleRestriction"


	public static class shiftExpression_return extends ParserRuleReturnScope {
		public BaseDescr result;
	};


	// $ANTLR start "shiftExpression"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:459:1: shiftExpression returns [BaseDescr result] : left= additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )* ;
	public final DRL6Expressions.shiftExpression_return shiftExpression() throws RecognitionException {
		DRL6Expressions.shiftExpression_return retval = new DRL6Expressions.shiftExpression_return();
		retval.start = input.LT(1);

		BaseDescr left =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:460:3: (left= additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )* )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:460:5: left= additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )*
			{
			pushFollow(FOLLOW_additiveExpression_in_shiftExpression2146);
			left=additiveExpression();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) { if( buildDescr  ) { retval.result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:461:5: ( ( shiftOp )=> shiftOp additiveExpression )*
			loop45:
			while (true) {
				int alt45=2;
				int LA45_0 = input.LA(1);
				if ( (LA45_0==LESS) ) {
					int LA45_6 = input.LA(2);
					if ( (synpred13_DRL6Expressions()) ) {
						alt45=1;
					}

				}
				else if ( (LA45_0==GREATER) ) {
					int LA45_7 = input.LA(2);
					if ( (synpred13_DRL6Expressions()) ) {
						alt45=1;
					}

				}

				switch (alt45) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:461:7: ( shiftOp )=> shiftOp additiveExpression
					{
					pushFollow(FOLLOW_shiftOp_in_shiftExpression2160);
					shiftOp();
					state._fsp--;
					if (state.failed) return retval;
					pushFollow(FOLLOW_additiveExpression_in_shiftExpression2162);
					additiveExpression();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;

				default :
					break loop45;
				}
			}

			}

			retval.stop = input.LT(-1);

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "shiftExpression"



	// $ANTLR start "shiftOp"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:464:1: shiftOp : ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER ) ;
	public final void shiftOp() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:465:5: ( ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER ) )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:465:7: ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
			{
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:465:7: ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
			int alt46=3;
			int LA46_0 = input.LA(1);
			if ( (LA46_0==LESS) ) {
				alt46=1;
			}
			else if ( (LA46_0==GREATER) ) {
				int LA46_2 = input.LA(2);
				if ( (LA46_2==GREATER) ) {
					int LA46_3 = input.LA(3);
					if ( (LA46_3==GREATER) ) {
						alt46=2;
					}
					else if ( (LA46_3==EOF||LA46_3==BOOL||(LA46_3 >= DECIMAL && LA46_3 <= DIV)||LA46_3==FLOAT||LA46_3==HEX||(LA46_3 >= ID && LA46_3 <= INCR)||(LA46_3 >= LEFT_PAREN && LA46_3 <= LESS)||LA46_3==MINUS||LA46_3==NEGATION||LA46_3==NULL||LA46_3==PLUS||(LA46_3 >= STAR && LA46_3 <= TIME_INTERVAL)) ) {
						alt46=3;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 46, 3, input);
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
							new NoViableAltException("", 46, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 46, 0, input);
				throw nvae;
			}

			switch (alt46) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:465:9: LESS LESS
					{
					match(input,LESS,FOLLOW_LESS_in_shiftOp2182); if (state.failed) return;
					match(input,LESS,FOLLOW_LESS_in_shiftOp2184); if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:466:11: GREATER GREATER GREATER
					{
					match(input,GREATER,FOLLOW_GREATER_in_shiftOp2196); if (state.failed) return;
					match(input,GREATER,FOLLOW_GREATER_in_shiftOp2198); if (state.failed) return;
					match(input,GREATER,FOLLOW_GREATER_in_shiftOp2200); if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:467:11: GREATER GREATER
					{
					match(input,GREATER,FOLLOW_GREATER_in_shiftOp2212); if (state.failed) return;
					match(input,GREATER,FOLLOW_GREATER_in_shiftOp2214); if (state.failed) return;
					}
					break;

			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "shiftOp"



	// $ANTLR start "additiveExpression"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:470:1: additiveExpression returns [BaseDescr result] : left= multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )* ;
	public final BaseDescr additiveExpression() throws RecognitionException {
		BaseDescr result = null;


		BaseDescr left =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:471:5: (left= multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )* )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:471:9: left= multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*
			{
			pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression2242);
			left=multiplicativeExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:472:9: ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*
			loop47:
			while (true) {
				int alt47=2;
				int LA47_0 = input.LA(1);
				if ( (LA47_0==MINUS||LA47_0==PLUS) && (synpred14_DRL6Expressions())) {
					alt47=1;
				}

				switch (alt47) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:472:11: ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression
					{
					if ( input.LA(1)==MINUS||input.LA(1)==PLUS ) {
						input.consume();
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return result;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression2271);
					multiplicativeExpression();
					state._fsp--;
					if (state.failed) return result;
					}
					break;

				default :
					break loop47;
				}
			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "additiveExpression"



	// $ANTLR start "multiplicativeExpression"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:475:1: multiplicativeExpression returns [BaseDescr result] : left= unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )* ;
	public final BaseDescr multiplicativeExpression() throws RecognitionException {
		BaseDescr result = null;


		BaseDescr left =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:476:5: (left= unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )* )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:476:9: left= unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )*
			{
			pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression2299);
			left=unaryExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:477:7: ( ( STAR | DIV | MOD ) unaryExpression )*
			loop48:
			while (true) {
				int alt48=2;
				int LA48_0 = input.LA(1);
				if ( (LA48_0==DIV||LA48_0==MOD||LA48_0==STAR) ) {
					alt48=1;
				}

				switch (alt48) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:477:9: ( STAR | DIV | MOD ) unaryExpression
					{
					if ( input.LA(1)==DIV||input.LA(1)==MOD||input.LA(1)==STAR ) {
						input.consume();
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return result;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression2325);
					unaryExpression();
					state._fsp--;
					if (state.failed) return result;
					}
					break;

				default :
					break loop48;
				}
			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "multiplicativeExpression"



	// $ANTLR start "unaryExpression"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:480:1: unaryExpression returns [BaseDescr result] : ( PLUS ue= unaryExpression | MINUS ue= unaryExpression | INCR primary | DECR primary |left= unaryExpressionNotPlusMinus );
	public final BaseDescr unaryExpression() throws RecognitionException {
		BaseDescr result = null;


		BaseDescr ue =null;
		ParserRuleReturnScope left =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:481:5: ( PLUS ue= unaryExpression | MINUS ue= unaryExpression | INCR primary | DECR primary |left= unaryExpressionNotPlusMinus )
			int alt49=5;
			switch ( input.LA(1) ) {
			case PLUS:
				{
				alt49=1;
				}
				break;
			case MINUS:
				{
				alt49=2;
				}
				break;
			case INCR:
				{
				alt49=3;
				}
				break;
			case DECR:
				{
				alt49=4;
				}
				break;
			case BOOL:
			case DECIMAL:
			case DIV:
			case FLOAT:
			case HEX:
			case ID:
			case LEFT_PAREN:
			case LEFT_SQUARE:
			case LESS:
			case NEGATION:
			case NULL:
			case STAR:
			case STRING:
			case TILDE:
			case TIME_INTERVAL:
				{
				alt49=5;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return result;}
				NoViableAltException nvae =
					new NoViableAltException("", 49, 0, input);
				throw nvae;
			}
			switch (alt49) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:481:9: PLUS ue= unaryExpression
					{
					match(input,PLUS,FOLLOW_PLUS_in_unaryExpression2351); if (state.failed) return result;
					pushFollow(FOLLOW_unaryExpression_in_unaryExpression2355);
					ue=unaryExpression();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { if( buildDescr ) {
					            result = ue;
					            if( result instanceof AtomicExprDescr ) {
					                ((AtomicExprDescr)result).setExpression( "+" + ((AtomicExprDescr)result).getExpression() );
					            }
					        } }
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:488:7: MINUS ue= unaryExpression
					{
					match(input,MINUS,FOLLOW_MINUS_in_unaryExpression2373); if (state.failed) return result;
					pushFollow(FOLLOW_unaryExpression_in_unaryExpression2377);
					ue=unaryExpression();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { if( buildDescr ) {
					            result = ue;
					            if( result instanceof AtomicExprDescr ) {
					                ((AtomicExprDescr)result).setExpression( "-" + ((AtomicExprDescr)result).getExpression() );
					            }
					        } }
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:495:9: INCR primary
					{
					match(input,INCR,FOLLOW_INCR_in_unaryExpression2397); if (state.failed) return result;
					pushFollow(FOLLOW_primary_in_unaryExpression2399);
					primary();
					state._fsp--;
					if (state.failed) return result;
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:496:9: DECR primary
					{
					match(input,DECR,FOLLOW_DECR_in_unaryExpression2409); if (state.failed) return result;
					pushFollow(FOLLOW_primary_in_unaryExpression2411);
					primary();
					state._fsp--;
					if (state.failed) return result;
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:497:9: left= unaryExpressionNotPlusMinus
					{
					pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression2423);
					left=unaryExpressionNotPlusMinus();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { if( buildDescr ) { result = (left!=null?((DRL6Expressions.unaryExpressionNotPlusMinus_return)left).result:null); } }
					}
					break;

			}
		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "unaryExpression"


	public static class unaryExpressionNotPlusMinus_return extends ParserRuleReturnScope {
		public BaseDescr result;
	};


	// $ANTLR start "unaryExpressionNotPlusMinus"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:500:1: unaryExpressionNotPlusMinus returns [BaseDescr result] : ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )? ( ( DIV ID )=>left2= xpathPrimary |left1= primary ) ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? );
	public final DRL6Expressions.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus() throws RecognitionException {
		DRL6Expressions.unaryExpressionNotPlusMinus_return retval = new DRL6Expressions.unaryExpressionNotPlusMinus_return();
		retval.start = input.LT(1);

		Token var=null;
		Token COLON9=null;
		Token UNIFY10=null;
		BaseDescr left2 =null;
		BaseDescr left1 =null;

		 boolean isLeft = false; BindingDescr bind = null;
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:502:5: ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )? ( ( DIV ID )=>left2= xpathPrimary |left1= primary ) ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? )
			int alt54=4;
			switch ( input.LA(1) ) {
			case TILDE:
				{
				alt54=1;
				}
				break;
			case NEGATION:
				{
				alt54=2;
				}
				break;
			case LEFT_PAREN:
				{
				int LA54_3 = input.LA(2);
				if ( (synpred15_DRL6Expressions()) ) {
					alt54=3;
				}
				else if ( (true) ) {
					alt54=4;
				}

				}
				break;
			case BOOL:
			case DECIMAL:
			case DIV:
			case FLOAT:
			case HEX:
			case ID:
			case LEFT_SQUARE:
			case LESS:
			case NULL:
			case STAR:
			case STRING:
			case TIME_INTERVAL:
				{
				alt54=4;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 54, 0, input);
				throw nvae;
			}
			switch (alt54) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:502:9: TILDE unaryExpression
					{
					match(input,TILDE,FOLLOW_TILDE_in_unaryExpressionNotPlusMinus2453); if (state.failed) return retval;
					pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2455);
					unaryExpression();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:503:8: NEGATION unaryExpression
					{
					match(input,NEGATION,FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus2464); if (state.failed) return retval;
					pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2466);
					unaryExpression();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:504:9: ( castExpression )=> castExpression
					{
					pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus2480);
					castExpression();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:505:9: ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )? ( ( DIV ID )=>left2= xpathPrimary |left1= primary ) ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )?
					{
					if ( state.backtracking==0 ) { isLeft = helper.getLeftMostExpr() == null;}
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:506:9: ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )?
					int alt50=3;
					int LA50_0 = input.LA(1);
					if ( (LA50_0==ID) ) {
						int LA50_1 = input.LA(2);
						if ( (LA50_1==COLON) ) {
							int LA50_3 = input.LA(3);
							if ( ((inMap == 0 && ternOp == 0 && input.LA(2) == DRL6Lexer.COLON)) ) {
								alt50=1;
							}
						}
						else if ( (LA50_1==UNIFY) ) {
							alt50=2;
						}
					}
					switch (alt50) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:506:11: ({...}? (var= ID COLON ) )
							{
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:506:11: ({...}? (var= ID COLON ) )
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:506:12: {...}? (var= ID COLON )
							{
							if ( !((inMap == 0 && ternOp == 0 && input.LA(2) == DRL6Lexer.COLON)) ) {
								if (state.backtracking>0) {state.failed=true; return retval;}
								throw new FailedPredicateException(input, "unaryExpressionNotPlusMinus", "inMap == 0 && ternOp == 0 && input.LA(2) == DRL6Lexer.COLON");
							}
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:506:75: (var= ID COLON )
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:506:76: var= ID COLON
							{
							var=(Token)match(input,ID,FOLLOW_ID_in_unaryExpressionNotPlusMinus2508); if (state.failed) return retval;
							COLON9=(Token)match(input,COLON,FOLLOW_COLON_in_unaryExpressionNotPlusMinus2510); if (state.failed) return retval;
							if ( state.backtracking==0 ) { hasBindings = true; helper.emit(var, DroolsEditorType.IDENTIFIER_VARIABLE); helper.emit(COLON9, DroolsEditorType.SYMBOL); if( buildDescr ) { bind = new BindingDescr((var!=null?var.getText():null), null, false); helper.setStart( bind, var ); } }
							}

							}

							}
							break;
						case 2 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:508:11: ({...}? (var= ID UNIFY ) )
							{
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:508:11: ({...}? (var= ID UNIFY ) )
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:508:12: {...}? (var= ID UNIFY )
							{
							if ( !((inMap == 0 && ternOp == 0 && input.LA(2) == DRL6Lexer.UNIFY)) ) {
								if (state.backtracking>0) {state.failed=true; return retval;}
								throw new FailedPredicateException(input, "unaryExpressionNotPlusMinus", "inMap == 0 && ternOp == 0 && input.LA(2) == DRL6Lexer.UNIFY");
							}
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:508:75: (var= ID UNIFY )
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:508:76: var= ID UNIFY
							{
							var=(Token)match(input,ID,FOLLOW_ID_in_unaryExpressionNotPlusMinus2549); if (state.failed) return retval;
							UNIFY10=(Token)match(input,UNIFY,FOLLOW_UNIFY_in_unaryExpressionNotPlusMinus2551); if (state.failed) return retval;
							if ( state.backtracking==0 ) { hasBindings = true; helper.emit(var, DroolsEditorType.IDENTIFIER_VARIABLE); helper.emit(UNIFY10, DroolsEditorType.SYMBOL); if( buildDescr ) { bind = new BindingDescr((var!=null?var.getText():null), null, true); helper.setStart( bind, var ); } }
							}

							}

							}
							break;

					}

					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:512:9: ( ( DIV ID )=>left2= xpathPrimary |left1= primary )
					int alt51=2;
					int LA51_0 = input.LA(1);
					if ( (LA51_0==DIV) && (synpred16_DRL6Expressions())) {
						alt51=1;
					}
					else if ( (LA51_0==BOOL||LA51_0==DECIMAL||LA51_0==FLOAT||LA51_0==HEX||LA51_0==ID||(LA51_0 >= LEFT_PAREN && LA51_0 <= LESS)||LA51_0==NULL||(LA51_0 >= STAR && LA51_0 <= STRING)||LA51_0==TIME_INTERVAL) ) {
						alt51=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 51, 0, input);
						throw nvae;
					}

					switch (alt51) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:512:11: ( DIV ID )=>left2= xpathPrimary
							{
							pushFollow(FOLLOW_xpathPrimary_in_unaryExpressionNotPlusMinus2605);
							left2=xpathPrimary();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) { if( buildDescr ) { retval.result = left2; } }
							}
							break;
						case 2 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:513:13: left1= primary
							{
							pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus2623);
							left1=primary();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) { if( buildDescr ) { retval.result = left1; } }
							}
							break;

					}

					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:516:9: ( ( selector )=> selector )*
					loop52:
					while (true) {
						int alt52=2;
						int LA52_0 = input.LA(1);
						if ( (LA52_0==DOT) && (synpred17_DRL6Expressions())) {
							alt52=1;
						}
						else if ( (LA52_0==LEFT_SQUARE) && (synpred17_DRL6Expressions())) {
							alt52=1;
						}

						switch (alt52) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:516:10: ( selector )=> selector
							{
							pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus2651);
							selector();
							state._fsp--;
							if (state.failed) return retval;
							}
							break;

						default :
							break loop52;
						}
					}

					if ( state.backtracking==0 ) {
					            if( buildDescr ) {
					                String expr = input.toString(retval.start,input.LT(-1));
					                if( isLeft ) {
					                    helper.setLeftMostExpr( expr );
					                }
					                if( bind != null ) {
					                    if( bind.isUnification() ) {
					                        expr = expr.substring( expr.indexOf( ":=" ) + 2 ).trim();
					                    } else {
					                        expr = expr.substring( expr.indexOf( ":" ) + 1 ).trim();
					                    }
					                    bind.setExpressionAndBindingField( expr );
					                    helper.setEnd( bind );
					                    retval.result = bind;
					                }
					            }
					        }
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:535:9: ( ( INCR | DECR )=> ( INCR | DECR ) )?
					int alt53=2;
					int LA53_0 = input.LA(1);
					if ( (LA53_0==DECR||LA53_0==INCR) && (synpred18_DRL6Expressions())) {
						alt53=1;
					}
					switch (alt53) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:535:10: ( INCR | DECR )=> ( INCR | DECR )
							{
							if ( input.LA(1)==DECR||input.LA(1)==INCR ) {
								input.consume();
								state.errorRecovery=false;
								state.failed=false;
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

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "unaryExpressionNotPlusMinus"



	// $ANTLR start "castExpression"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:538:1: castExpression : ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN expr= unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus );
	public final void castExpression() throws RecognitionException {
		BaseDescr expr =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:539:5: ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN expr= unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus )
			int alt55=2;
			int LA55_0 = input.LA(1);
			if ( (LA55_0==LEFT_PAREN) ) {
				int LA55_1 = input.LA(2);
				if ( (synpred19_DRL6Expressions()) ) {
					alt55=1;
				}
				else if ( (synpred20_DRL6Expressions()) ) {
					alt55=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 55, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 55, 0, input);
				throw nvae;
			}

			switch (alt55) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:539:8: ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN expr= unaryExpression
					{
					match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression2713); if (state.failed) return;
					pushFollow(FOLLOW_primitiveType_in_castExpression2715);
					primitiveType();
					state._fsp--;
					if (state.failed) return;
					match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression2717); if (state.failed) return;
					pushFollow(FOLLOW_unaryExpression_in_castExpression2721);
					expr=unaryExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:540:8: ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus
					{
					match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression2738); if (state.failed) return;
					pushFollow(FOLLOW_type_in_castExpression2740);
					type();
					state._fsp--;
					if (state.failed) return;
					match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression2742); if (state.failed) return;
					pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression2744);
					unaryExpressionNotPlusMinus();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "castExpression"



	// $ANTLR start "primitiveType"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:543:1: primitiveType : ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key );
	public final void primitiveType() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:544:5: ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key )
			int alt56=8;
			int LA56_0 = input.LA(1);
			if ( (LA56_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))))) {
				int LA56_1 = input.LA(2);
				if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))) ) {
					alt56=1;
				}
				else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))) ) {
					alt56=2;
				}
				else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))) ) {
					alt56=3;
				}
				else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))) ) {
					alt56=4;
				}
				else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))) ) {
					alt56=5;
				}
				else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))) ) {
					alt56=6;
				}
				else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))) ) {
					alt56=7;
				}
				else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))) ) {
					alt56=8;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 56, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			switch (alt56) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:544:9: boolean_key
					{
					pushFollow(FOLLOW_boolean_key_in_primitiveType2763);
					boolean_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:545:7: char_key
					{
					pushFollow(FOLLOW_char_key_in_primitiveType2771);
					char_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:546:7: byte_key
					{
					pushFollow(FOLLOW_byte_key_in_primitiveType2779);
					byte_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:547:7: short_key
					{
					pushFollow(FOLLOW_short_key_in_primitiveType2787);
					short_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:548:7: int_key
					{
					pushFollow(FOLLOW_int_key_in_primitiveType2795);
					int_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 6 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:549:7: long_key
					{
					pushFollow(FOLLOW_long_key_in_primitiveType2803);
					long_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 7 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:550:7: float_key
					{
					pushFollow(FOLLOW_float_key_in_primitiveType2811);
					float_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 8 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:551:7: double_key
					{
					pushFollow(FOLLOW_double_key_in_primitiveType2819);
					double_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "primitiveType"



	// $ANTLR start "xpathPrimary"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:554:1: xpathPrimary returns [BaseDescr result] : ( xpathChunk )+ ;
	public final BaseDescr xpathPrimary() throws RecognitionException {
		BaseDescr result = null;


		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:555:5: ( ( xpathChunk )+ )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:555:7: ( xpathChunk )+
			{
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:555:7: ( xpathChunk )+
			int cnt57=0;
			loop57:
			while (true) {
				int alt57=2;
				int LA57_0 = input.LA(1);
				if ( (LA57_0==DIV) ) {
					int LA57_2 = input.LA(2);
					if ( (LA57_2==ID) ) {
						alt57=1;
					}

				}

				switch (alt57) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:555:7: xpathChunk
					{
					pushFollow(FOLLOW_xpathChunk_in_xpathPrimary2840);
					xpathChunk();
					state._fsp--;
					if (state.failed) return result;
					}
					break;

				default :
					if ( cnt57 >= 1 ) break loop57;
					if (state.backtracking>0) {state.failed=true; return result;}
					EarlyExitException eee = new EarlyExitException(57, input);
					throw eee;
				}
				cnt57++;
			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "xpathPrimary"



	// $ANTLR start "xpathChunk"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:558:1: xpathChunk returns [BaseDescr result] : ( DIV ID )=> DIV ID ( DOT ID )* ( LEFT_SQUARE xpathExpressionList RIGHT_SQUARE )? ;
	public final BaseDescr xpathChunk() throws RecognitionException {
		BaseDescr result = null;


		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:559:5: ( ( DIV ID )=> DIV ID ( DOT ID )* ( LEFT_SQUARE xpathExpressionList RIGHT_SQUARE )? )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:559:7: ( DIV ID )=> DIV ID ( DOT ID )* ( LEFT_SQUARE xpathExpressionList RIGHT_SQUARE )?
			{
			match(input,DIV,FOLLOW_DIV_in_xpathChunk2869); if (state.failed) return result;
			match(input,ID,FOLLOW_ID_in_xpathChunk2871); if (state.failed) return result;
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:559:25: ( DOT ID )*
			loop58:
			while (true) {
				int alt58=2;
				int LA58_0 = input.LA(1);
				if ( (LA58_0==DOT) ) {
					int LA58_2 = input.LA(2);
					if ( (LA58_2==ID) ) {
						alt58=1;
					}

				}

				switch (alt58) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:559:26: DOT ID
					{
					match(input,DOT,FOLLOW_DOT_in_xpathChunk2874); if (state.failed) return result;
					match(input,ID,FOLLOW_ID_in_xpathChunk2876); if (state.failed) return result;
					}
					break;

				default :
					break loop58;
				}
			}

			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:559:35: ( LEFT_SQUARE xpathExpressionList RIGHT_SQUARE )?
			int alt59=2;
			int LA59_0 = input.LA(1);
			if ( (LA59_0==LEFT_SQUARE) ) {
				alt59=1;
			}
			switch (alt59) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:559:36: LEFT_SQUARE xpathExpressionList RIGHT_SQUARE
					{
					match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_xpathChunk2881); if (state.failed) return result;
					pushFollow(FOLLOW_xpathExpressionList_in_xpathChunk2883);
					xpathExpressionList();
					state._fsp--;
					if (state.failed) return result;
					match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_xpathChunk2885); if (state.failed) return result;
					}
					break;

			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "xpathChunk"



	// $ANTLR start "xpathExpressionList"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:562:1: xpathExpressionList returns [java.util.List<String> exprs] : ( ( HASH ID )=> HASH ID |f= expression ) ( COMMA s= expression )* ;
	public final java.util.List<String> xpathExpressionList() throws RecognitionException {
		java.util.List<String> exprs = null;


		ParserRuleReturnScope f =null;
		ParserRuleReturnScope s =null;

		 exprs = new java.util.ArrayList<String>();
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:564:3: ( ( ( HASH ID )=> HASH ID |f= expression ) ( COMMA s= expression )* )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:564:7: ( ( HASH ID )=> HASH ID |f= expression ) ( COMMA s= expression )*
			{
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:564:7: ( ( HASH ID )=> HASH ID |f= expression )
			int alt60=2;
			int LA60_0 = input.LA(1);
			if ( (LA60_0==HASH) && (synpred22_DRL6Expressions())) {
				alt60=1;
			}
			else if ( (LA60_0==BOOL||(LA60_0 >= DECIMAL && LA60_0 <= DIV)||LA60_0==FLOAT||LA60_0==HEX||(LA60_0 >= ID && LA60_0 <= INCR)||(LA60_0 >= LEFT_PAREN && LA60_0 <= LESS)||LA60_0==MINUS||LA60_0==NEGATION||LA60_0==NULL||LA60_0==PLUS||(LA60_0 >= STAR && LA60_0 <= TIME_INTERVAL)) ) {
				alt60=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return exprs;}
				NoViableAltException nvae =
					new NoViableAltException("", 60, 0, input);
				throw nvae;
			}

			switch (alt60) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:564:8: ( HASH ID )=> HASH ID
					{
					match(input,HASH,FOLLOW_HASH_in_xpathExpressionList2921); if (state.failed) return exprs;
					match(input,ID,FOLLOW_ID_in_xpathExpressionList2923); if (state.failed) return exprs;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:564:30: f= expression
					{
					pushFollow(FOLLOW_expression_in_xpathExpressionList2929);
					f=expression();
					state._fsp--;
					if (state.failed) return exprs;
					if ( state.backtracking==0 ) { exprs.add( (f!=null?input.toString(f.start,f.stop):null) ); }
					}
					break;

			}

			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:565:7: ( COMMA s= expression )*
			loop61:
			while (true) {
				int alt61=2;
				int LA61_0 = input.LA(1);
				if ( (LA61_0==COMMA) ) {
					alt61=1;
				}

				switch (alt61) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:565:8: COMMA s= expression
					{
					match(input,COMMA,FOLLOW_COMMA_in_xpathExpressionList2941); if (state.failed) return exprs;
					pushFollow(FOLLOW_expression_in_xpathExpressionList2945);
					s=expression();
					state._fsp--;
					if (state.failed) return exprs;
					if ( state.backtracking==0 ) { exprs.add( (s!=null?input.toString(s.start,s.stop):null) ); }
					}
					break;

				default :
					break loop61;
				}
			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return exprs;
	}
	// $ANTLR end "xpathExpressionList"



	// $ANTLR start "primary"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:568:1: primary returns [BaseDescr result] : ( ( parExpression )=>expr= parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=>i1= ID ( ( ( DOT ID )=>d= DOT i2= ID ) | ( ( DOT LEFT_PAREN )=>d= DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN ) | ( ( HASH ID )=>h= HASH i2= ID ) | ( ( NULL_SAFE_DOT ID )=>n= NULL_SAFE_DOT i2= ID ) )* ( ( identifierSuffix )=> identifierSuffix )? );
	public final BaseDescr primary() throws RecognitionException {
		BaseDescr result = null;


		Token i1=null;
		Token d=null;
		Token i2=null;
		Token h=null;
		Token n=null;
		Token LEFT_PAREN12=null;
		Token COMMA13=null;
		Token RIGHT_PAREN14=null;
		BaseDescr expr =null;
		ParserRuleReturnScope literal11 =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:569:5: ( ( parExpression )=>expr= parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=>i1= ID ( ( ( DOT ID )=>d= DOT i2= ID ) | ( ( DOT LEFT_PAREN )=>d= DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN ) | ( ( HASH ID )=>h= HASH i2= ID ) | ( ( NULL_SAFE_DOT ID )=>n= NULL_SAFE_DOT i2= ID ) )* ( ( identifierSuffix )=> identifierSuffix )? )
			int alt67=9;
			int LA67_0 = input.LA(1);
			if ( (LA67_0==LEFT_PAREN) && (synpred23_DRL6Expressions())) {
				alt67=1;
			}
			else if ( (LA67_0==LESS) && (synpred24_DRL6Expressions())) {
				alt67=2;
			}
			else if ( (LA67_0==STRING) && (synpred25_DRL6Expressions())) {
				alt67=3;
			}
			else if ( (LA67_0==DECIMAL) && (synpred25_DRL6Expressions())) {
				alt67=3;
			}
			else if ( (LA67_0==HEX) && (synpred25_DRL6Expressions())) {
				alt67=3;
			}
			else if ( (LA67_0==FLOAT) && (synpred25_DRL6Expressions())) {
				alt67=3;
			}
			else if ( (LA67_0==BOOL) && (synpred25_DRL6Expressions())) {
				alt67=3;
			}
			else if ( (LA67_0==NULL) && (synpred25_DRL6Expressions())) {
				alt67=3;
			}
			else if ( (LA67_0==TIME_INTERVAL) && (synpred25_DRL6Expressions())) {
				alt67=3;
			}
			else if ( (LA67_0==STAR) && (synpred25_DRL6Expressions())) {
				alt67=3;
			}
			else if ( (LA67_0==ID) ) {
				int LA67_11 = input.LA(2);
				if ( ((synpred26_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))))) ) {
					alt67=4;
				}
				else if ( ((synpred27_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.NEW))))) ) {
					alt67=5;
				}
				else if ( (((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))&&synpred28_DRL6Expressions())) ) {
					alt67=6;
				}
				else if ( (synpred31_DRL6Expressions()) ) {
					alt67=9;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return result;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 67, 11, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA67_0==LEFT_SQUARE) ) {
				int LA67_12 = input.LA(2);
				if ( (synpred29_DRL6Expressions()) ) {
					alt67=7;
				}
				else if ( (synpred30_DRL6Expressions()) ) {
					alt67=8;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return result;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 67, 12, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return result;}
				NoViableAltException nvae =
					new NoViableAltException("", 67, 0, input);
				throw nvae;
			}

			switch (alt67) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:569:7: ( parExpression )=>expr= parExpression
					{
					pushFollow(FOLLOW_parExpression_in_primary2977);
					expr=parExpression();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) {  if( buildDescr  ) { result = expr; }  }
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:570:9: ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments )
					{
					pushFollow(FOLLOW_nonWildcardTypeArguments_in_primary2994);
					nonWildcardTypeArguments();
					state._fsp--;
					if (state.failed) return result;
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:570:63: ( explicitGenericInvocationSuffix | this_key arguments )
					int alt62=2;
					int LA62_0 = input.LA(1);
					if ( (LA62_0==ID) ) {
						int LA62_1 = input.LA(2);
						if ( (!((((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))))) ) {
							alt62=1;
						}
						else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))) ) {
							alt62=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return result;}
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 62, 1, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

					else {
						if (state.backtracking>0) {state.failed=true; return result;}
						NoViableAltException nvae =
							new NoViableAltException("", 62, 0, input);
						throw nvae;
					}

					switch (alt62) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:570:64: explicitGenericInvocationSuffix
							{
							pushFollow(FOLLOW_explicitGenericInvocationSuffix_in_primary2997);
							explicitGenericInvocationSuffix();
							state._fsp--;
							if (state.failed) return result;
							}
							break;
						case 2 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:570:98: this_key arguments
							{
							pushFollow(FOLLOW_this_key_in_primary3001);
							this_key();
							state._fsp--;
							if (state.failed) return result;
							pushFollow(FOLLOW_arguments_in_primary3003);
							arguments();
							state._fsp--;
							if (state.failed) return result;
							}
							break;

					}

					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:571:9: ( literal )=> literal
					{
					pushFollow(FOLLOW_literal_in_primary3019);
					literal11=literal();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { if( buildDescr  ) { result = new AtomicExprDescr( (literal11!=null?input.toString(literal11.start,literal11.stop):null), true ); }  }
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:573:9: ( super_key )=> super_key superSuffix
					{
					pushFollow(FOLLOW_super_key_in_primary3041);
					super_key();
					state._fsp--;
					if (state.failed) return result;
					pushFollow(FOLLOW_superSuffix_in_primary3043);
					superSuffix();
					state._fsp--;
					if (state.failed) return result;
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:574:9: ( new_key )=> new_key creator
					{
					pushFollow(FOLLOW_new_key_in_primary3058);
					new_key();
					state._fsp--;
					if (state.failed) return result;
					pushFollow(FOLLOW_creator_in_primary3060);
					creator();
					state._fsp--;
					if (state.failed) return result;
					}
					break;
				case 6 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:575:9: ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key
					{
					pushFollow(FOLLOW_primitiveType_in_primary3075);
					primitiveType();
					state._fsp--;
					if (state.failed) return result;
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:575:41: ( LEFT_SQUARE RIGHT_SQUARE )*
					loop63:
					while (true) {
						int alt63=2;
						int LA63_0 = input.LA(1);
						if ( (LA63_0==LEFT_SQUARE) ) {
							alt63=1;
						}

						switch (alt63) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:575:42: LEFT_SQUARE RIGHT_SQUARE
							{
							match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_primary3078); if (state.failed) return result;
							match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_primary3080); if (state.failed) return result;
							}
							break;

						default :
							break loop63;
						}
					}

					match(input,DOT,FOLLOW_DOT_in_primary3084); if (state.failed) return result;
					pushFollow(FOLLOW_class_key_in_primary3086);
					class_key();
					state._fsp--;
					if (state.failed) return result;
					}
					break;
				case 7 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:577:9: ( inlineMapExpression )=> inlineMapExpression
					{
					pushFollow(FOLLOW_inlineMapExpression_in_primary3106);
					inlineMapExpression();
					state._fsp--;
					if (state.failed) return result;
					}
					break;
				case 8 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:578:9: ( inlineListExpression )=> inlineListExpression
					{
					pushFollow(FOLLOW_inlineListExpression_in_primary3121);
					inlineListExpression();
					state._fsp--;
					if (state.failed) return result;
					}
					break;
				case 9 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:579:9: ( ID )=>i1= ID ( ( ( DOT ID )=>d= DOT i2= ID ) | ( ( DOT LEFT_PAREN )=>d= DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN ) | ( ( HASH ID )=>h= HASH i2= ID ) | ( ( NULL_SAFE_DOT ID )=>n= NULL_SAFE_DOT i2= ID ) )* ( ( identifierSuffix )=> identifierSuffix )?
					{
					i1=(Token)match(input,ID,FOLLOW_ID_in_primary3137); if (state.failed) return result;
					if ( state.backtracking==0 ) { helper.emit(i1, DroolsEditorType.IDENTIFIER); }
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:580:9: ( ( ( DOT ID )=>d= DOT i2= ID ) | ( ( DOT LEFT_PAREN )=>d= DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN ) | ( ( HASH ID )=>h= HASH i2= ID ) | ( ( NULL_SAFE_DOT ID )=>n= NULL_SAFE_DOT i2= ID ) )*
					loop65:
					while (true) {
						int alt65=5;
						int LA65_0 = input.LA(1);
						if ( (LA65_0==DOT) ) {
							int LA65_2 = input.LA(2);
							if ( (LA65_2==ID) ) {
								int LA65_5 = input.LA(3);
								if ( (synpred32_DRL6Expressions()) ) {
									alt65=1;
								}

							}
							else if ( (LA65_2==LEFT_PAREN) && (synpred33_DRL6Expressions())) {
								alt65=2;
							}

						}
						else if ( (LA65_0==HASH) && (synpred34_DRL6Expressions())) {
							alt65=3;
						}
						else if ( (LA65_0==NULL_SAFE_DOT) && (synpred35_DRL6Expressions())) {
							alt65=4;
						}

						switch (alt65) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:581:13: ( ( DOT ID )=>d= DOT i2= ID )
							{
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:581:13: ( ( DOT ID )=>d= DOT i2= ID )
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:581:15: ( DOT ID )=>d= DOT i2= ID
							{
							d=(Token)match(input,DOT,FOLLOW_DOT_in_primary3173); if (state.failed) return result;
							i2=(Token)match(input,ID,FOLLOW_ID_in_primary3177); if (state.failed) return result;
							if ( state.backtracking==0 ) { helper.emit(d, DroolsEditorType.SYMBOL); helper.emit(i2, DroolsEditorType.IDENTIFIER); }
							}

							}
							break;
						case 2 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:583:13: ( ( DOT LEFT_PAREN )=>d= DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN )
							{
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:583:13: ( ( DOT LEFT_PAREN )=>d= DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN )
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:583:15: ( DOT LEFT_PAREN )=>d= DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN
							{
							d=(Token)match(input,DOT,FOLLOW_DOT_in_primary3219); if (state.failed) return result;
							LEFT_PAREN12=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_primary3221); if (state.failed) return result;
							if ( state.backtracking==0 ) { helper.emit(d, DroolsEditorType.SYMBOL); helper.emit(LEFT_PAREN12, DroolsEditorType.SYMBOL); }
							pushFollow(FOLLOW_expression_in_primary3261);
							expression();
							state._fsp--;
							if (state.failed) return result;
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:584:48: ( COMMA expression )*
							loop64:
							while (true) {
								int alt64=2;
								int LA64_0 = input.LA(1);
								if ( (LA64_0==COMMA) ) {
									alt64=1;
								}

								switch (alt64) {
								case 1 :
									// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:584:49: COMMA expression
									{
									COMMA13=(Token)match(input,COMMA,FOLLOW_COMMA_in_primary3264); if (state.failed) return result;
									if ( state.backtracking==0 ) { helper.emit(COMMA13, DroolsEditorType.SYMBOL); }
									pushFollow(FOLLOW_expression_in_primary3268);
									expression();
									state._fsp--;
									if (state.failed) return result;
									}
									break;

								default :
									break loop64;
								}
							}

							RIGHT_PAREN14=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_primary3308); if (state.failed) return result;
							if ( state.backtracking==0 ) { helper.emit(RIGHT_PAREN14, DroolsEditorType.SYMBOL); }
							}

							}
							break;
						case 3 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:588:13: ( ( HASH ID )=>h= HASH i2= ID )
							{
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:588:13: ( ( HASH ID )=>h= HASH i2= ID )
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:588:15: ( HASH ID )=>h= HASH i2= ID
							{
							h=(Token)match(input,HASH,FOLLOW_HASH_in_primary3362); if (state.failed) return result;
							i2=(Token)match(input,ID,FOLLOW_ID_in_primary3366); if (state.failed) return result;
							if ( state.backtracking==0 ) { helper.emit(h, DroolsEditorType.SYMBOL); helper.emit(i2, DroolsEditorType.IDENTIFIER); }
							}

							}
							break;
						case 4 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:590:13: ( ( NULL_SAFE_DOT ID )=>n= NULL_SAFE_DOT i2= ID )
							{
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:590:13: ( ( NULL_SAFE_DOT ID )=>n= NULL_SAFE_DOT i2= ID )
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:590:15: ( NULL_SAFE_DOT ID )=>n= NULL_SAFE_DOT i2= ID
							{
							n=(Token)match(input,NULL_SAFE_DOT,FOLLOW_NULL_SAFE_DOT_in_primary3408); if (state.failed) return result;
							i2=(Token)match(input,ID,FOLLOW_ID_in_primary3412); if (state.failed) return result;
							if ( state.backtracking==0 ) { helper.emit(n, DroolsEditorType.SYMBOL); helper.emit(i2, DroolsEditorType.IDENTIFIER); }
							}

							}
							break;

						default :
							break loop65;
						}
					}

					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:591:12: ( ( identifierSuffix )=> identifierSuffix )?
					int alt66=2;
					int LA66_0 = input.LA(1);
					if ( (LA66_0==LEFT_SQUARE) ) {
						int LA66_1 = input.LA(2);
						if ( (synpred36_DRL6Expressions()) ) {
							alt66=1;
						}
					}
					else if ( (LA66_0==LEFT_PAREN) ) {
						int LA66_2 = input.LA(2);
						if ( (synpred36_DRL6Expressions()) ) {
							alt66=1;
						}
					}
					switch (alt66) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:591:13: ( identifierSuffix )=> identifierSuffix
							{
							pushFollow(FOLLOW_identifierSuffix_in_primary3434);
							identifierSuffix();
							state._fsp--;
							if (state.failed) return result;
							}
							break;

					}

					}
					break;

			}
		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "primary"



	// $ANTLR start "inlineListExpression"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:594:1: inlineListExpression : LEFT_SQUARE ( expressionList )? RIGHT_SQUARE ;
	public final void inlineListExpression() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:595:5: ( LEFT_SQUARE ( expressionList )? RIGHT_SQUARE )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:595:9: LEFT_SQUARE ( expressionList )? RIGHT_SQUARE
			{
			match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_inlineListExpression3455); if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:595:21: ( expressionList )?
			int alt68=2;
			int LA68_0 = input.LA(1);
			if ( (LA68_0==BOOL||(LA68_0 >= DECIMAL && LA68_0 <= DIV)||LA68_0==FLOAT||LA68_0==HEX||(LA68_0 >= ID && LA68_0 <= INCR)||(LA68_0 >= LEFT_PAREN && LA68_0 <= LESS)||LA68_0==MINUS||LA68_0==NEGATION||LA68_0==NULL||LA68_0==PLUS||(LA68_0 >= STAR && LA68_0 <= TIME_INTERVAL)) ) {
				alt68=1;
			}
			switch (alt68) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:595:21: expressionList
					{
					pushFollow(FOLLOW_expressionList_in_inlineListExpression3457);
					expressionList();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_inlineListExpression3460); if (state.failed) return;
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "inlineListExpression"



	// $ANTLR start "inlineMapExpression"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:598:1: inlineMapExpression : LEFT_SQUARE mapExpressionList RIGHT_SQUARE ;
	public final void inlineMapExpression() throws RecognitionException {
		 inMap++; 
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:600:5: ( LEFT_SQUARE mapExpressionList RIGHT_SQUARE )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:600:7: LEFT_SQUARE mapExpressionList RIGHT_SQUARE
			{
			match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_inlineMapExpression3481); if (state.failed) return;
			pushFollow(FOLLOW_mapExpressionList_in_inlineMapExpression3483);
			mapExpressionList();
			state._fsp--;
			if (state.failed) return;
			match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_inlineMapExpression3485); if (state.failed) return;
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
			 inMap--; 
		}
	}
	// $ANTLR end "inlineMapExpression"



	// $ANTLR start "mapExpressionList"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:604:1: mapExpressionList : mapEntry ( COMMA mapEntry )* ;
	public final void mapExpressionList() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:605:5: ( mapEntry ( COMMA mapEntry )* )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:605:7: mapEntry ( COMMA mapEntry )*
			{
			pushFollow(FOLLOW_mapEntry_in_mapExpressionList3506);
			mapEntry();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:605:16: ( COMMA mapEntry )*
			loop69:
			while (true) {
				int alt69=2;
				int LA69_0 = input.LA(1);
				if ( (LA69_0==COMMA) ) {
					alt69=1;
				}

				switch (alt69) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:605:17: COMMA mapEntry
					{
					match(input,COMMA,FOLLOW_COMMA_in_mapExpressionList3509); if (state.failed) return;
					pushFollow(FOLLOW_mapEntry_in_mapExpressionList3511);
					mapEntry();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop69;
				}
			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "mapExpressionList"



	// $ANTLR start "mapEntry"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:608:1: mapEntry : expression COLON expression ;
	public final void mapEntry() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:609:5: ( expression COLON expression )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:609:7: expression COLON expression
			{
			pushFollow(FOLLOW_expression_in_mapEntry3530);
			expression();
			state._fsp--;
			if (state.failed) return;
			match(input,COLON,FOLLOW_COLON_in_mapEntry3532); if (state.failed) return;
			pushFollow(FOLLOW_expression_in_mapEntry3534);
			expression();
			state._fsp--;
			if (state.failed) return;
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "mapEntry"



	// $ANTLR start "parExpression"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:612:1: parExpression returns [BaseDescr result] : LEFT_PAREN expr= expression RIGHT_PAREN ;
	public final BaseDescr parExpression() throws RecognitionException {
		BaseDescr result = null;


		ParserRuleReturnScope expr =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:613:5: ( LEFT_PAREN expr= expression RIGHT_PAREN )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:613:7: LEFT_PAREN expr= expression RIGHT_PAREN
			{
			match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_parExpression3555); if (state.failed) return result;
			pushFollow(FOLLOW_expression_in_parExpression3559);
			expr=expression();
			state._fsp--;
			if (state.failed) return result;
			match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_parExpression3561); if (state.failed) return result;
			if ( state.backtracking==0 ) {  if( buildDescr  ) {
			               result = (expr!=null?((DRL6Expressions.expression_return)expr).result:null);
			               if( result instanceof AtomicExprDescr ) {
			                   ((AtomicExprDescr)result).setExpression("(" +((AtomicExprDescr)result).getExpression() + ")" );
			               }
			           }
			        }
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return result;
	}
	// $ANTLR end "parExpression"



	// $ANTLR start "identifierSuffix"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:623:1: identifierSuffix : ( ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments );
	public final void identifierSuffix() throws RecognitionException {
		Token LEFT_SQUARE15=null;
		Token RIGHT_SQUARE16=null;
		Token DOT17=null;
		Token LEFT_SQUARE18=null;
		Token RIGHT_SQUARE19=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:624:5: ( ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments )
			int alt72=3;
			int LA72_0 = input.LA(1);
			if ( (LA72_0==LEFT_SQUARE) ) {
				int LA72_1 = input.LA(2);
				if ( (LA72_1==RIGHT_SQUARE) && (synpred37_DRL6Expressions())) {
					alt72=1;
				}
				else if ( (LA72_1==BOOL||(LA72_1 >= DECIMAL && LA72_1 <= DIV)||LA72_1==FLOAT||LA72_1==HEX||(LA72_1 >= ID && LA72_1 <= INCR)||(LA72_1 >= LEFT_PAREN && LA72_1 <= LESS)||LA72_1==MINUS||LA72_1==NEGATION||LA72_1==NULL||LA72_1==PLUS||(LA72_1 >= STAR && LA72_1 <= TIME_INTERVAL)) ) {
					alt72=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 72, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA72_0==LEFT_PAREN) ) {
				alt72=3;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 72, 0, input);
				throw nvae;
			}

			switch (alt72) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:624:7: ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key
					{
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:624:35: ( LEFT_SQUARE RIGHT_SQUARE )+
					int cnt70=0;
					loop70:
					while (true) {
						int alt70=2;
						int LA70_0 = input.LA(1);
						if ( (LA70_0==LEFT_SQUARE) ) {
							alt70=1;
						}

						switch (alt70) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:624:36: LEFT_SQUARE RIGHT_SQUARE
							{
							LEFT_SQUARE15=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_identifierSuffix3595); if (state.failed) return;
							if ( state.backtracking==0 ) { helper.emit(LEFT_SQUARE15, DroolsEditorType.SYMBOL); }
							RIGHT_SQUARE16=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_identifierSuffix3636); if (state.failed) return;
							if ( state.backtracking==0 ) { helper.emit(RIGHT_SQUARE16, DroolsEditorType.SYMBOL); }
							}
							break;

						default :
							if ( cnt70 >= 1 ) break loop70;
							if (state.backtracking>0) {state.failed=true; return;}
							EarlyExitException eee = new EarlyExitException(70, input);
							throw eee;
						}
						cnt70++;
					}

					DOT17=(Token)match(input,DOT,FOLLOW_DOT_in_identifierSuffix3680); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(DOT17, DroolsEditorType.SYMBOL); }
					pushFollow(FOLLOW_class_key_in_identifierSuffix3684);
					class_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:627:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
					{
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:627:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
					int cnt71=0;
					loop71:
					while (true) {
						int alt71=2;
						int LA71_0 = input.LA(1);
						if ( (LA71_0==LEFT_SQUARE) ) {
							int LA71_36 = input.LA(2);
							if ( (synpred38_DRL6Expressions()) ) {
								alt71=1;
							}

						}

						switch (alt71) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:627:8: ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE
							{
							LEFT_SQUARE18=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_identifierSuffix3699); if (state.failed) return;
							if ( state.backtracking==0 ) { helper.emit(LEFT_SQUARE18, DroolsEditorType.SYMBOL); }
							pushFollow(FOLLOW_expression_in_identifierSuffix3729);
							expression();
							state._fsp--;
							if (state.failed) return;
							RIGHT_SQUARE19=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_identifierSuffix3757); if (state.failed) return;
							if ( state.backtracking==0 ) { helper.emit(RIGHT_SQUARE19, DroolsEditorType.SYMBOL); }
							}
							break;

						default :
							if ( cnt71 >= 1 ) break loop71;
							if (state.backtracking>0) {state.failed=true; return;}
							EarlyExitException eee = new EarlyExitException(71, input);
							throw eee;
						}
						cnt71++;
					}

					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:630:9: arguments
					{
					pushFollow(FOLLOW_arguments_in_identifierSuffix3773);
					arguments();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "identifierSuffix"



	// $ANTLR start "creator"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:638:1: creator : ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) ;
	public final void creator() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:639:5: ( ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:639:7: ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest )
			{
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:639:7: ( nonWildcardTypeArguments )?
			int alt73=2;
			int LA73_0 = input.LA(1);
			if ( (LA73_0==LESS) ) {
				alt73=1;
			}
			switch (alt73) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:639:7: nonWildcardTypeArguments
					{
					pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator3795);
					nonWildcardTypeArguments();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			pushFollow(FOLLOW_createdName_in_creator3798);
			createdName();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:640:9: ( arrayCreatorRest | classCreatorRest )
			int alt74=2;
			int LA74_0 = input.LA(1);
			if ( (LA74_0==LEFT_SQUARE) ) {
				alt74=1;
			}
			else if ( (LA74_0==LEFT_PAREN) ) {
				alt74=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 74, 0, input);
				throw nvae;
			}

			switch (alt74) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:640:10: arrayCreatorRest
					{
					pushFollow(FOLLOW_arrayCreatorRest_in_creator3809);
					arrayCreatorRest();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:640:29: classCreatorRest
					{
					pushFollow(FOLLOW_classCreatorRest_in_creator3813);
					classCreatorRest();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "creator"



	// $ANTLR start "createdName"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:643:1: createdName : ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType );
	public final void createdName() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:644:5: ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType )
			int alt78=2;
			int LA78_0 = input.LA(1);
			if ( (LA78_0==ID) ) {
				int LA78_1 = input.LA(2);
				if ( (!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))) ) {
					alt78=1;
				}
				else if ( ((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))) ) {
					alt78=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 78, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 78, 0, input);
				throw nvae;
			}

			switch (alt78) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:644:7: ID ( typeArguments )? ( DOT ID ( typeArguments )? )*
					{
					match(input,ID,FOLLOW_ID_in_createdName3831); if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:644:10: ( typeArguments )?
					int alt75=2;
					int LA75_0 = input.LA(1);
					if ( (LA75_0==LESS) ) {
						alt75=1;
					}
					switch (alt75) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:644:10: typeArguments
							{
							pushFollow(FOLLOW_typeArguments_in_createdName3833);
							typeArguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:645:9: ( DOT ID ( typeArguments )? )*
					loop77:
					while (true) {
						int alt77=2;
						int LA77_0 = input.LA(1);
						if ( (LA77_0==DOT) ) {
							alt77=1;
						}

						switch (alt77) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:645:11: DOT ID ( typeArguments )?
							{
							match(input,DOT,FOLLOW_DOT_in_createdName3846); if (state.failed) return;
							match(input,ID,FOLLOW_ID_in_createdName3848); if (state.failed) return;
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:645:18: ( typeArguments )?
							int alt76=2;
							int LA76_0 = input.LA(1);
							if ( (LA76_0==LESS) ) {
								alt76=1;
							}
							switch (alt76) {
								case 1 :
									// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:645:18: typeArguments
									{
									pushFollow(FOLLOW_typeArguments_in_createdName3850);
									typeArguments();
									state._fsp--;
									if (state.failed) return;
									}
									break;

							}

							}
							break;

						default :
							break loop77;
						}
					}

					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:646:11: primitiveType
					{
					pushFollow(FOLLOW_primitiveType_in_createdName3865);
					primitiveType();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "createdName"



	// $ANTLR start "innerCreator"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:649:1: innerCreator :{...}? => ID classCreatorRest ;
	public final void innerCreator() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:650:5: ({...}? => ID classCreatorRest )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:650:7: {...}? => ID classCreatorRest
			{
			if ( !((!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "innerCreator", "!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
			}
			match(input,ID,FOLLOW_ID_in_innerCreator3885); if (state.failed) return;
			pushFollow(FOLLOW_classCreatorRest_in_innerCreator3887);
			classCreatorRest();
			state._fsp--;
			if (state.failed) return;
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "innerCreator"



	// $ANTLR start "arrayCreatorRest"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:653:1: arrayCreatorRest : LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) ;
	public final void arrayCreatorRest() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:654:5: ( LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:654:9: LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
			{
			match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3906); if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:655:5: ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
			int alt82=2;
			int LA82_0 = input.LA(1);
			if ( (LA82_0==RIGHT_SQUARE) ) {
				alt82=1;
			}
			else if ( (LA82_0==BOOL||(LA82_0 >= DECIMAL && LA82_0 <= DIV)||LA82_0==FLOAT||LA82_0==HEX||(LA82_0 >= ID && LA82_0 <= INCR)||(LA82_0 >= LEFT_PAREN && LA82_0 <= LESS)||LA82_0==MINUS||LA82_0==NEGATION||LA82_0==NULL||LA82_0==PLUS||(LA82_0 >= STAR && LA82_0 <= TIME_INTERVAL)) ) {
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
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:655:9: RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer
					{
					match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3916); if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:655:22: ( LEFT_SQUARE RIGHT_SQUARE )*
					loop79:
					while (true) {
						int alt79=2;
						int LA79_0 = input.LA(1);
						if ( (LA79_0==LEFT_SQUARE) ) {
							alt79=1;
						}

						switch (alt79) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:655:23: LEFT_SQUARE RIGHT_SQUARE
							{
							match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3919); if (state.failed) return;
							match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3921); if (state.failed) return;
							}
							break;

						default :
							break loop79;
						}
					}

					pushFollow(FOLLOW_arrayInitializer_in_arrayCreatorRest3925);
					arrayInitializer();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:656:13: expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
					{
					pushFollow(FOLLOW_expression_in_arrayCreatorRest3939);
					expression();
					state._fsp--;
					if (state.failed) return;
					match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3941); if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:656:37: ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )*
					loop80:
					while (true) {
						int alt80=2;
						int LA80_0 = input.LA(1);
						if ( (LA80_0==LEFT_SQUARE) ) {
							int LA80_1 = input.LA(2);
							if ( ((!helper.validateLT(2,"]"))) ) {
								alt80=1;
							}

						}

						switch (alt80) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:656:38: {...}? => LEFT_SQUARE expression RIGHT_SQUARE
							{
							if ( !((!helper.validateLT(2,"]"))) ) {
								if (state.backtracking>0) {state.failed=true; return;}
								throw new FailedPredicateException(input, "arrayCreatorRest", "!helper.validateLT(2,\"]\")");
							}
							match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3946); if (state.failed) return;
							pushFollow(FOLLOW_expression_in_arrayCreatorRest3948);
							expression();
							state._fsp--;
							if (state.failed) return;
							match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3950); if (state.failed) return;
							}
							break;

						default :
							break loop80;
						}
					}

					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:656:106: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
					loop81:
					while (true) {
						int alt81=2;
						int LA81_0 = input.LA(1);
						if ( (LA81_0==LEFT_SQUARE) ) {
							int LA81_2 = input.LA(2);
							if ( (LA81_2==RIGHT_SQUARE) && (synpred39_DRL6Expressions())) {
								alt81=1;
							}

						}

						switch (alt81) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:656:107: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
							{
							match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3962); if (state.failed) return;
							match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3964); if (state.failed) return;
							}
							break;

						default :
							break loop81;
						}
					}

					}
					break;

			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "arrayCreatorRest"



	// $ANTLR start "variableInitializer"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:660:1: variableInitializer : ( arrayInitializer | expression );
	public final void variableInitializer() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:661:5: ( arrayInitializer | expression )
			int alt83=2;
			int LA83_0 = input.LA(1);
			if ( (LA83_0==LEFT_CURLY) ) {
				alt83=1;
			}
			else if ( (LA83_0==BOOL||(LA83_0 >= DECIMAL && LA83_0 <= DIV)||LA83_0==FLOAT||LA83_0==HEX||(LA83_0 >= ID && LA83_0 <= INCR)||(LA83_0 >= LEFT_PAREN && LA83_0 <= LESS)||LA83_0==MINUS||LA83_0==NEGATION||LA83_0==NULL||LA83_0==PLUS||(LA83_0 >= STAR && LA83_0 <= TIME_INTERVAL)) ) {
				alt83=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 83, 0, input);
				throw nvae;
			}

			switch (alt83) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:661:7: arrayInitializer
					{
					pushFollow(FOLLOW_arrayInitializer_in_variableInitializer3993);
					arrayInitializer();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:662:13: expression
					{
					pushFollow(FOLLOW_expression_in_variableInitializer4007);
					expression();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "variableInitializer"



	// $ANTLR start "arrayInitializer"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:665:1: arrayInitializer : LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY ;
	public final void arrayInitializer() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:666:5: ( LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:666:7: LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY
			{
			match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_arrayInitializer4024); if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:666:18: ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )?
			int alt86=2;
			int LA86_0 = input.LA(1);
			if ( (LA86_0==BOOL||(LA86_0 >= DECIMAL && LA86_0 <= DIV)||LA86_0==FLOAT||LA86_0==HEX||(LA86_0 >= ID && LA86_0 <= INCR)||(LA86_0 >= LEFT_CURLY && LA86_0 <= LESS)||LA86_0==MINUS||LA86_0==NEGATION||LA86_0==NULL||LA86_0==PLUS||(LA86_0 >= STAR && LA86_0 <= TIME_INTERVAL)) ) {
				alt86=1;
			}
			switch (alt86) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:666:19: variableInitializer ( COMMA variableInitializer )* ( COMMA )?
					{
					pushFollow(FOLLOW_variableInitializer_in_arrayInitializer4027);
					variableInitializer();
					state._fsp--;
					if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:666:39: ( COMMA variableInitializer )*
					loop84:
					while (true) {
						int alt84=2;
						int LA84_0 = input.LA(1);
						if ( (LA84_0==COMMA) ) {
							int LA84_1 = input.LA(2);
							if ( (LA84_1==BOOL||(LA84_1 >= DECIMAL && LA84_1 <= DIV)||LA84_1==FLOAT||LA84_1==HEX||(LA84_1 >= ID && LA84_1 <= INCR)||(LA84_1 >= LEFT_CURLY && LA84_1 <= LESS)||LA84_1==MINUS||LA84_1==NEGATION||LA84_1==NULL||LA84_1==PLUS||(LA84_1 >= STAR && LA84_1 <= TIME_INTERVAL)) ) {
								alt84=1;
							}

						}

						switch (alt84) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:666:40: COMMA variableInitializer
							{
							match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer4030); if (state.failed) return;
							pushFollow(FOLLOW_variableInitializer_in_arrayInitializer4032);
							variableInitializer();
							state._fsp--;
							if (state.failed) return;
							}
							break;

						default :
							break loop84;
						}
					}

					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:666:68: ( COMMA )?
					int alt85=2;
					int LA85_0 = input.LA(1);
					if ( (LA85_0==COMMA) ) {
						alt85=1;
					}
					switch (alt85) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:666:69: COMMA
							{
							match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer4037); if (state.failed) return;
							}
							break;

					}

					}
					break;

			}

			match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_arrayInitializer4044); if (state.failed) return;
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "arrayInitializer"



	// $ANTLR start "classCreatorRest"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:669:1: classCreatorRest : arguments ;
	public final void classCreatorRest() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:670:5: ( arguments )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:670:7: arguments
			{
			pushFollow(FOLLOW_arguments_in_classCreatorRest4061);
			arguments();
			state._fsp--;
			if (state.failed) return;
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "classCreatorRest"



	// $ANTLR start "explicitGenericInvocation"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:673:1: explicitGenericInvocation : nonWildcardTypeArguments arguments ;
	public final void explicitGenericInvocation() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:674:5: ( nonWildcardTypeArguments arguments )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:674:7: nonWildcardTypeArguments arguments
			{
			pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation4079);
			nonWildcardTypeArguments();
			state._fsp--;
			if (state.failed) return;
			pushFollow(FOLLOW_arguments_in_explicitGenericInvocation4081);
			arguments();
			state._fsp--;
			if (state.failed) return;
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "explicitGenericInvocation"



	// $ANTLR start "nonWildcardTypeArguments"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:677:1: nonWildcardTypeArguments : LESS typeList GREATER ;
	public final void nonWildcardTypeArguments() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:678:5: ( LESS typeList GREATER )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:678:7: LESS typeList GREATER
			{
			match(input,LESS,FOLLOW_LESS_in_nonWildcardTypeArguments4098); if (state.failed) return;
			pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments4100);
			typeList();
			state._fsp--;
			if (state.failed) return;
			match(input,GREATER,FOLLOW_GREATER_in_nonWildcardTypeArguments4102); if (state.failed) return;
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "nonWildcardTypeArguments"



	// $ANTLR start "explicitGenericInvocationSuffix"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:681:1: explicitGenericInvocationSuffix : ( super_key superSuffix | ID arguments );
	public final void explicitGenericInvocationSuffix() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:682:5: ( super_key superSuffix | ID arguments )
			int alt87=2;
			int LA87_0 = input.LA(1);
			if ( (LA87_0==ID) ) {
				int LA87_1 = input.LA(2);
				if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
					alt87=1;
				}
				else if ( (true) ) {
					alt87=2;
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 87, 0, input);
				throw nvae;
			}

			switch (alt87) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:682:7: super_key superSuffix
					{
					pushFollow(FOLLOW_super_key_in_explicitGenericInvocationSuffix4119);
					super_key();
					state._fsp--;
					if (state.failed) return;
					pushFollow(FOLLOW_superSuffix_in_explicitGenericInvocationSuffix4121);
					superSuffix();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:683:10: ID arguments
					{
					match(input,ID,FOLLOW_ID_in_explicitGenericInvocationSuffix4132); if (state.failed) return;
					pushFollow(FOLLOW_arguments_in_explicitGenericInvocationSuffix4134);
					arguments();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}
		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "explicitGenericInvocationSuffix"



	// $ANTLR start "selector"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:686:1: selector : ( ( DOT super_key )=> DOT super_key superSuffix | ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator | ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )? | ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE );
	public final void selector() throws RecognitionException {
		Token DOT20=null;
		Token DOT21=null;
		Token DOT22=null;
		Token ID23=null;
		Token LEFT_SQUARE24=null;
		Token RIGHT_SQUARE25=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:687:5: ( ( DOT super_key )=> DOT super_key superSuffix | ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator | ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )? | ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )
			int alt90=4;
			int LA90_0 = input.LA(1);
			if ( (LA90_0==DOT) ) {
				int LA90_1 = input.LA(2);
				if ( (synpred40_DRL6Expressions()) ) {
					alt90=1;
				}
				else if ( (synpred41_DRL6Expressions()) ) {
					alt90=2;
				}
				else if ( (synpred42_DRL6Expressions()) ) {
					alt90=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 90, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA90_0==LEFT_SQUARE) && (synpred44_DRL6Expressions())) {
				alt90=4;
			}

			switch (alt90) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:687:9: ( DOT super_key )=> DOT super_key superSuffix
					{
					DOT20=(Token)match(input,DOT,FOLLOW_DOT_in_selector4159); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(DOT20, DroolsEditorType.SYMBOL); }
					pushFollow(FOLLOW_super_key_in_selector4163);
					super_key();
					state._fsp--;
					if (state.failed) return;
					pushFollow(FOLLOW_superSuffix_in_selector4165);
					superSuffix();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:688:9: ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator
					{
					DOT21=(Token)match(input,DOT,FOLLOW_DOT_in_selector4181); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(DOT21, DroolsEditorType.SYMBOL); }
					pushFollow(FOLLOW_new_key_in_selector4185);
					new_key();
					state._fsp--;
					if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:688:84: ( nonWildcardTypeArguments )?
					int alt88=2;
					int LA88_0 = input.LA(1);
					if ( (LA88_0==LESS) ) {
						alt88=1;
					}
					switch (alt88) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:688:85: nonWildcardTypeArguments
							{
							pushFollow(FOLLOW_nonWildcardTypeArguments_in_selector4188);
							nonWildcardTypeArguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					pushFollow(FOLLOW_innerCreator_in_selector4192);
					innerCreator();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:689:9: ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )?
					{
					DOT22=(Token)match(input,DOT,FOLLOW_DOT_in_selector4208); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(DOT22, DroolsEditorType.SYMBOL); }
					ID23=(Token)match(input,ID,FOLLOW_ID_in_selector4230); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(ID23, DroolsEditorType.IDENTIFIER); }
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:691:19: ( ( LEFT_PAREN )=> arguments )?
					int alt89=2;
					int LA89_0 = input.LA(1);
					if ( (LA89_0==LEFT_PAREN) ) {
						int LA89_1 = input.LA(2);
						if ( (synpred43_DRL6Expressions()) ) {
							alt89=1;
						}
					}
					switch (alt89) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:691:20: ( LEFT_PAREN )=> arguments
							{
							pushFollow(FOLLOW_arguments_in_selector4259);
							arguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:693:9: ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE
					{
					LEFT_SQUARE24=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_selector4280); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(LEFT_SQUARE24, DroolsEditorType.SYMBOL); }
					pushFollow(FOLLOW_expression_in_selector4307);
					expression();
					state._fsp--;
					if (state.failed) return;
					RIGHT_SQUARE25=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_selector4332); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(RIGHT_SQUARE25, DroolsEditorType.SYMBOL); }
					}
					break;

			}
		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "selector"



	// $ANTLR start "superSuffix"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:698:1: superSuffix : ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? );
	public final void superSuffix() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:699:5: ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? )
			int alt92=2;
			int LA92_0 = input.LA(1);
			if ( (LA92_0==LEFT_PAREN) ) {
				alt92=1;
			}
			else if ( (LA92_0==DOT) ) {
				alt92=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 92, 0, input);
				throw nvae;
			}

			switch (alt92) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:699:7: arguments
					{
					pushFollow(FOLLOW_arguments_in_superSuffix4351);
					arguments();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:700:10: DOT ID ( ( LEFT_PAREN )=> arguments )?
					{
					match(input,DOT,FOLLOW_DOT_in_superSuffix4362); if (state.failed) return;
					match(input,ID,FOLLOW_ID_in_superSuffix4364); if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:700:17: ( ( LEFT_PAREN )=> arguments )?
					int alt91=2;
					int LA91_0 = input.LA(1);
					if ( (LA91_0==LEFT_PAREN) ) {
						int LA91_1 = input.LA(2);
						if ( (synpred45_DRL6Expressions()) ) {
							alt91=1;
						}
					}
					switch (alt91) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:700:18: ( LEFT_PAREN )=> arguments
							{
							pushFollow(FOLLOW_arguments_in_superSuffix4373);
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
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "superSuffix"



	// $ANTLR start "squareArguments"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:703:1: squareArguments returns [java.util.List<String> args] : LEFT_SQUARE (el= expressionList )? RIGHT_SQUARE ;
	public final java.util.List<String> squareArguments() throws RecognitionException {
		java.util.List<String> args = null;


		java.util.List<String> el =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:704:5: ( LEFT_SQUARE (el= expressionList )? RIGHT_SQUARE )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:704:7: LEFT_SQUARE (el= expressionList )? RIGHT_SQUARE
			{
			match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_squareArguments4396); if (state.failed) return args;
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:704:19: (el= expressionList )?
			int alt93=2;
			int LA93_0 = input.LA(1);
			if ( (LA93_0==BOOL||(LA93_0 >= DECIMAL && LA93_0 <= DIV)||LA93_0==FLOAT||LA93_0==HEX||(LA93_0 >= ID && LA93_0 <= INCR)||(LA93_0 >= LEFT_PAREN && LA93_0 <= LESS)||LA93_0==MINUS||LA93_0==NEGATION||LA93_0==NULL||LA93_0==PLUS||(LA93_0 >= STAR && LA93_0 <= TIME_INTERVAL)) ) {
				alt93=1;
			}
			switch (alt93) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:704:20: el= expressionList
					{
					pushFollow(FOLLOW_expressionList_in_squareArguments4401);
					el=expressionList();
					state._fsp--;
					if (state.failed) return args;
					if ( state.backtracking==0 ) { args = el; }
					}
					break;

			}

			match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_squareArguments4407); if (state.failed) return args;
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return args;
	}
	// $ANTLR end "squareArguments"



	// $ANTLR start "arguments"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:707:1: arguments : LEFT_PAREN ( expressionList )? RIGHT_PAREN ;
	public final void arguments() throws RecognitionException {
		Token LEFT_PAREN26=null;
		Token RIGHT_PAREN27=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:708:5: ( LEFT_PAREN ( expressionList )? RIGHT_PAREN )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:708:7: LEFT_PAREN ( expressionList )? RIGHT_PAREN
			{
			LEFT_PAREN26=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_arguments4424); if (state.failed) return;
			if ( state.backtracking==0 ) { helper.emit(LEFT_PAREN26, DroolsEditorType.SYMBOL); }
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:709:9: ( expressionList )?
			int alt94=2;
			int LA94_0 = input.LA(1);
			if ( (LA94_0==BOOL||(LA94_0 >= DECIMAL && LA94_0 <= DIV)||LA94_0==FLOAT||LA94_0==HEX||(LA94_0 >= ID && LA94_0 <= INCR)||(LA94_0 >= LEFT_PAREN && LA94_0 <= LESS)||LA94_0==MINUS||LA94_0==NEGATION||LA94_0==NULL||LA94_0==PLUS||(LA94_0 >= STAR && LA94_0 <= TIME_INTERVAL)) ) {
				alt94=1;
			}
			switch (alt94) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:709:9: expressionList
					{
					pushFollow(FOLLOW_expressionList_in_arguments4436);
					expressionList();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			RIGHT_PAREN27=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_arguments4447); if (state.failed) return;
			if ( state.backtracking==0 ) { helper.emit(RIGHT_PAREN27, DroolsEditorType.SYMBOL); }
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "arguments"



	// $ANTLR start "expressionList"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:713:1: expressionList returns [java.util.List<String> exprs] : f= expression ( COMMA s= expression )* ;
	public final java.util.List<String> expressionList() throws RecognitionException {
		java.util.List<String> exprs = null;


		ParserRuleReturnScope f =null;
		ParserRuleReturnScope s =null;

		 exprs = new java.util.ArrayList<String>();
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:715:3: (f= expression ( COMMA s= expression )* )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:715:7: f= expression ( COMMA s= expression )*
			{
			pushFollow(FOLLOW_expression_in_expressionList4477);
			f=expression();
			state._fsp--;
			if (state.failed) return exprs;
			if ( state.backtracking==0 ) { exprs.add( (f!=null?input.toString(f.start,f.stop):null) ); }
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:716:7: ( COMMA s= expression )*
			loop95:
			while (true) {
				int alt95=2;
				int LA95_0 = input.LA(1);
				if ( (LA95_0==COMMA) ) {
					alt95=1;
				}

				switch (alt95) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:716:8: COMMA s= expression
					{
					match(input,COMMA,FOLLOW_COMMA_in_expressionList4488); if (state.failed) return exprs;
					pushFollow(FOLLOW_expression_in_expressionList4492);
					s=expression();
					state._fsp--;
					if (state.failed) return exprs;
					if ( state.backtracking==0 ) { exprs.add( (s!=null?input.toString(s.start,s.stop):null) ); }
					}
					break;

				default :
					break loop95;
				}
			}

			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return exprs;
	}
	// $ANTLR end "expressionList"



	// $ANTLR start "assignmentOperator"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:719:1: assignmentOperator : ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN );
	public final void assignmentOperator() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:720:5: ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN )
			int alt96=12;
			switch ( input.LA(1) ) {
			case EQUALS_ASSIGN:
				{
				alt96=1;
				}
				break;
			case PLUS_ASSIGN:
				{
				alt96=2;
				}
				break;
			case MINUS_ASSIGN:
				{
				alt96=3;
				}
				break;
			case MULT_ASSIGN:
				{
				alt96=4;
				}
				break;
			case DIV_ASSIGN:
				{
				alt96=5;
				}
				break;
			case AND_ASSIGN:
				{
				alt96=6;
				}
				break;
			case OR_ASSIGN:
				{
				alt96=7;
				}
				break;
			case XOR_ASSIGN:
				{
				alt96=8;
				}
				break;
			case MOD_ASSIGN:
				{
				alt96=9;
				}
				break;
			case LESS:
				{
				alt96=10;
				}
				break;
			case GREATER:
				{
				int LA96_11 = input.LA(2);
				if ( (LA96_11==GREATER) ) {
					int LA96_12 = input.LA(3);
					if ( (LA96_12==GREATER) && (synpred46_DRL6Expressions())) {
						alt96=11;
					}
					else if ( (LA96_12==EQUALS_ASSIGN) && (synpred47_DRL6Expressions())) {
						alt96=12;
					}

				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 96, 11, input);
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
					new NoViableAltException("", 96, 0, input);
				throw nvae;
			}
			switch (alt96) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:720:9: EQUALS_ASSIGN
					{
					match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4513); if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:721:7: PLUS_ASSIGN
					{
					match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_assignmentOperator4521); if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:722:7: MINUS_ASSIGN
					{
					match(input,MINUS_ASSIGN,FOLLOW_MINUS_ASSIGN_in_assignmentOperator4529); if (state.failed) return;
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:723:7: MULT_ASSIGN
					{
					match(input,MULT_ASSIGN,FOLLOW_MULT_ASSIGN_in_assignmentOperator4537); if (state.failed) return;
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:724:7: DIV_ASSIGN
					{
					match(input,DIV_ASSIGN,FOLLOW_DIV_ASSIGN_in_assignmentOperator4545); if (state.failed) return;
					}
					break;
				case 6 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:725:7: AND_ASSIGN
					{
					match(input,AND_ASSIGN,FOLLOW_AND_ASSIGN_in_assignmentOperator4553); if (state.failed) return;
					}
					break;
				case 7 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:726:7: OR_ASSIGN
					{
					match(input,OR_ASSIGN,FOLLOW_OR_ASSIGN_in_assignmentOperator4561); if (state.failed) return;
					}
					break;
				case 8 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:727:7: XOR_ASSIGN
					{
					match(input,XOR_ASSIGN,FOLLOW_XOR_ASSIGN_in_assignmentOperator4569); if (state.failed) return;
					}
					break;
				case 9 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:728:7: MOD_ASSIGN
					{
					match(input,MOD_ASSIGN,FOLLOW_MOD_ASSIGN_in_assignmentOperator4577); if (state.failed) return;
					}
					break;
				case 10 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:729:7: LESS LESS EQUALS_ASSIGN
					{
					match(input,LESS,FOLLOW_LESS_in_assignmentOperator4585); if (state.failed) return;
					match(input,LESS,FOLLOW_LESS_in_assignmentOperator4587); if (state.failed) return;
					match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4589); if (state.failed) return;
					}
					break;
				case 11 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:730:7: ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN
					{
					match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4606); if (state.failed) return;
					match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4608); if (state.failed) return;
					match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4610); if (state.failed) return;
					match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4612); if (state.failed) return;
					}
					break;
				case 12 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:731:7: ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN
					{
					match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4627); if (state.failed) return;
					match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4629); if (state.failed) return;
					match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4631); if (state.failed) return;
					}
					break;

			}
		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "assignmentOperator"



	// $ANTLR start "extends_key"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:737:1: extends_key :{...}? =>id= ID ;
	public final void extends_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:738:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:738:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "extends_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_extends_key4661); if (state.failed) return;
			if ( state.backtracking==0 ) { helper.emit(id, DroolsEditorType.KEYWORD); }
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "extends_key"



	// $ANTLR start "super_key"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:741:1: super_key :{...}? =>id= ID ;
	public final void super_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:742:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:742:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "super_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_super_key4690); if (state.failed) return;
			if ( state.backtracking==0 ) { helper.emit(id, DroolsEditorType.KEYWORD); }
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "super_key"


	public static class instanceof_key_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "instanceof_key"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:745:1: instanceof_key :{...}? =>id= ID ;
	public final DRL6Expressions.instanceof_key_return instanceof_key() throws RecognitionException {
		DRL6Expressions.instanceof_key_return retval = new DRL6Expressions.instanceof_key_return();
		retval.start = input.LT(1);

		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:746:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:746:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
				if (state.backtracking>0) {state.failed=true; return retval;}
				throw new FailedPredicateException(input, "instanceof_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_instanceof_key4719); if (state.failed) return retval;
			if ( state.backtracking==0 ) { helper.emit(id, DroolsEditorType.KEYWORD); }
			}

			retval.stop = input.LT(-1);

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "instanceof_key"



	// $ANTLR start "boolean_key"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:749:1: boolean_key :{...}? =>id= ID ;
	public final void boolean_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:750:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:750:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "boolean_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_boolean_key4748); if (state.failed) return;
			if ( state.backtracking==0 ) { helper.emit(id, DroolsEditorType.KEYWORD); }
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "boolean_key"



	// $ANTLR start "char_key"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:753:1: char_key :{...}? =>id= ID ;
	public final void char_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:754:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:754:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "char_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_char_key4777); if (state.failed) return;
			if ( state.backtracking==0 ) { helper.emit(id, DroolsEditorType.KEYWORD); }
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "char_key"



	// $ANTLR start "byte_key"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:757:1: byte_key :{...}? =>id= ID ;
	public final void byte_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:758:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:758:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "byte_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_byte_key4806); if (state.failed) return;
			if ( state.backtracking==0 ) { helper.emit(id, DroolsEditorType.KEYWORD); }
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "byte_key"



	// $ANTLR start "short_key"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:761:1: short_key :{...}? =>id= ID ;
	public final void short_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:762:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:762:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "short_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_short_key4835); if (state.failed) return;
			if ( state.backtracking==0 ) { helper.emit(id, DroolsEditorType.KEYWORD); }
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "short_key"



	// $ANTLR start "int_key"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:765:1: int_key :{...}? =>id= ID ;
	public final void int_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:766:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:766:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "int_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INT))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_int_key4864); if (state.failed) return;
			if ( state.backtracking==0 ) { helper.emit(id, DroolsEditorType.KEYWORD); }
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "int_key"



	// $ANTLR start "float_key"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:769:1: float_key :{...}? =>id= ID ;
	public final void float_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:770:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:770:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "float_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_float_key4893); if (state.failed) return;
			if ( state.backtracking==0 ) { helper.emit(id, DroolsEditorType.KEYWORD); }
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "float_key"



	// $ANTLR start "long_key"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:773:1: long_key :{...}? =>id= ID ;
	public final void long_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:774:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:774:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "long_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.LONG))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_long_key4922); if (state.failed) return;
			if ( state.backtracking==0 ) { helper.emit(id, DroolsEditorType.KEYWORD); }
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "long_key"



	// $ANTLR start "double_key"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:777:1: double_key :{...}? =>id= ID ;
	public final void double_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:778:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:778:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "double_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_double_key4951); if (state.failed) return;
			if ( state.backtracking==0 ) { helper.emit(id, DroolsEditorType.KEYWORD); }
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "double_key"



	// $ANTLR start "void_key"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:781:1: void_key :{...}? =>id= ID ;
	public final void void_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:782:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:782:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.VOID)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "void_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.VOID))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_void_key4980); if (state.failed) return;
			if ( state.backtracking==0 ) { helper.emit(id, DroolsEditorType.KEYWORD); }
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "void_key"



	// $ANTLR start "this_key"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:785:1: this_key :{...}? =>id= ID ;
	public final void this_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:786:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:786:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "this_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.THIS))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_this_key5009); if (state.failed) return;
			if ( state.backtracking==0 ) { helper.emit(id, DroolsEditorType.KEYWORD); }
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "this_key"



	// $ANTLR start "class_key"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:789:1: class_key :{...}? =>id= ID ;
	public final void class_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:790:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:790:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.CLASS)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "class_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.CLASS))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_class_key5038); if (state.failed) return;
			if ( state.backtracking==0 ) { helper.emit(id, DroolsEditorType.KEYWORD); }
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "class_key"



	// $ANTLR start "new_key"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:793:1: new_key :{...}? =>id= ID ;
	public final void new_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:794:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:794:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.NEW)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "new_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.NEW))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_new_key5068); if (state.failed) return;
			if ( state.backtracking==0 ) { helper.emit(id, DroolsEditorType.KEYWORD); }
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "new_key"



	// $ANTLR start "not_key"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:797:1: not_key :{...}? =>id= ID ;
	public final void not_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:798:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:798:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "not_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.NOT))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_not_key5097); if (state.failed) return;
			if ( state.backtracking==0 ) { helper.emit(id, DroolsEditorType.KEYWORD); }
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "not_key"



	// $ANTLR start "in_key"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:801:1: in_key :{...}? =>id= ID ;
	public final void in_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:802:3: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:802:10: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "in_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.IN))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_in_key5124); if (state.failed) return;
			if ( state.backtracking==0 ) { helper.emit(id, DroolsEditorType.KEYWORD); }
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "in_key"


	public static class operator_key_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "operator_key"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:805:1: operator_key :{...}? =>id= ID ;
	public final DRL6Expressions.operator_key_return operator_key() throws RecognitionException {
		DRL6Expressions.operator_key_return retval = new DRL6Expressions.operator_key_return();
		retval.start = input.LT(1);

		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:806:3: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:806:10: {...}? =>id= ID
			{
			if ( !(((helper.isPluggableEvaluator(false)))) ) {
				if (state.backtracking>0) {state.failed=true; return retval;}
				throw new FailedPredicateException(input, "operator_key", "(helper.isPluggableEvaluator(false))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_operator_key5149); if (state.failed) return retval;
			if ( state.backtracking==0 ) { helper.emit(id, DroolsEditorType.KEYWORD); }
			}

			retval.stop = input.LT(-1);

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "operator_key"


	public static class neg_operator_key_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "neg_operator_key"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:809:1: neg_operator_key :{...}? =>id= ID ;
	public final DRL6Expressions.neg_operator_key_return neg_operator_key() throws RecognitionException {
		DRL6Expressions.neg_operator_key_return retval = new DRL6Expressions.neg_operator_key_return();
		retval.start = input.LT(1);

		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:810:3: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:810:10: {...}? =>id= ID
			{
			if ( !(((helper.isPluggableEvaluator(true)))) ) {
				if (state.backtracking>0) {state.failed=true; return retval;}
				throw new FailedPredicateException(input, "neg_operator_key", "(helper.isPluggableEvaluator(true))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_neg_operator_key5174); if (state.failed) return retval;
			if ( state.backtracking==0 ) { helper.emit(id, DroolsEditorType.KEYWORD); }
			}

			retval.stop = input.LT(-1);

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "neg_operator_key"

	// $ANTLR start synpred1_DRL6Expressions
	public final void synpred1_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:138:8: ( primitiveType )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:138:9: primitiveType
		{
		pushFollow(FOLLOW_primitiveType_in_synpred1_DRL6Expressions548);
		primitiveType();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred1_DRL6Expressions

	// $ANTLR start synpred2_DRL6Expressions
	public final void synpred2_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:138:44: ( LEFT_SQUARE RIGHT_SQUARE )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:138:45: LEFT_SQUARE RIGHT_SQUARE
		{
		match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred2_DRL6Expressions559); if (state.failed) return;
		match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred2_DRL6Expressions561); if (state.failed) return;
		}

	}
	// $ANTLR end synpred2_DRL6Expressions

	// $ANTLR start synpred3_DRL6Expressions
	public final void synpred3_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:13: ( typeArguments )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:14: typeArguments
		{
		pushFollow(FOLLOW_typeArguments_in_synpred3_DRL6Expressions585);
		typeArguments();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred3_DRL6Expressions

	// $ANTLR start synpred4_DRL6Expressions
	public final void synpred4_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:55: ( typeArguments )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:56: typeArguments
		{
		pushFollow(FOLLOW_typeArguments_in_synpred4_DRL6Expressions599);
		typeArguments();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred4_DRL6Expressions

	// $ANTLR start synpred5_DRL6Expressions
	public final void synpred5_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:92: ( LEFT_SQUARE RIGHT_SQUARE )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:93: LEFT_SQUARE RIGHT_SQUARE
		{
		match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred5_DRL6Expressions611); if (state.failed) return;
		match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred5_DRL6Expressions613); if (state.failed) return;
		}

	}
	// $ANTLR end synpred5_DRL6Expressions

	// $ANTLR start synpred6_DRL6Expressions
	public final void synpred6_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:165:10: ( assignmentOperator )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:165:11: assignmentOperator
		{
		pushFollow(FOLLOW_assignmentOperator_in_synpred6_DRL6Expressions782);
		assignmentOperator();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred6_DRL6Expressions

	// $ANTLR start synpred7_DRL6Expressions
	public final void synpred7_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:199:8: ( ID EQUALS_ASSIGN )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:199:9: ID EQUALS_ASSIGN
		{
		match(input,ID,FOLLOW_ID_in_synpred7_DRL6Expressions961); if (state.failed) return;
		match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_synpred7_DRL6Expressions963); if (state.failed) return;
		}

	}
	// $ANTLR end synpred7_DRL6Expressions

	// $ANTLR start synpred8_DRL6Expressions
	public final void synpred8_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:337:6: ( not_key in_key )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:337:7: not_key in_key
		{
		pushFollow(FOLLOW_not_key_in_synpred8_DRL6Expressions1629);
		not_key();
		state._fsp--;
		if (state.failed) return;
		pushFollow(FOLLOW_in_key_in_synpred8_DRL6Expressions1631);
		in_key();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred8_DRL6Expressions

	// $ANTLR start synpred9_DRL6Expressions
	public final void synpred9_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:396:5: ( operator | LEFT_PAREN )
		int alt97=2;
		int LA97_0 = input.LA(1);
		if ( (LA97_0==EQUALS||(LA97_0 >= GREATER && LA97_0 <= GREATER_EQUALS)||(LA97_0 >= LESS && LA97_0 <= LESS_EQUALS)||LA97_0==NOT_EQUALS||LA97_0==TILDE) ) {
			alt97=1;
		}
		else if ( (LA97_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) {
			alt97=1;
		}
		else if ( (LA97_0==LEFT_PAREN) ) {
			alt97=2;
		}

		else {
			if (state.backtracking>0) {state.failed=true; return;}
			NoViableAltException nvae =
				new NoViableAltException("", 97, 0, input);
			throw nvae;
		}

		switch (alt97) {
			case 1 :
				// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:396:7: operator
				{
				pushFollow(FOLLOW_operator_in_synpred9_DRL6Expressions1846);
				operator();
				state._fsp--;
				if (state.failed) return;
				}
				break;
			case 2 :
				// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:396:18: LEFT_PAREN
				{
				match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred9_DRL6Expressions1850); if (state.failed) return;
				}
				break;

		}
	}
	// $ANTLR end synpred9_DRL6Expressions

	// $ANTLR start synpred10_DRL6Expressions
	public final void synpred10_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:407:7: ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:407:8: DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction
		{
		match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_synpred10_DRL6Expressions1903); if (state.failed) return;
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:407:20: ( fullAnnotation[null] )?
		int alt98=2;
		int LA98_0 = input.LA(1);
		if ( (LA98_0==AT) ) {
			alt98=1;
		}
		switch (alt98) {
			case 1 :
				// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:407:20: fullAnnotation[null]
				{
				pushFollow(FOLLOW_fullAnnotation_in_synpred10_DRL6Expressions1905);
				fullAnnotation(null);
				state._fsp--;
				if (state.failed) return;
				}
				break;

		}

		pushFollow(FOLLOW_andRestriction_in_synpred10_DRL6Expressions1909);
		andRestriction();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred10_DRL6Expressions

	// $ANTLR start synpred11_DRL6Expressions
	public final void synpred11_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:421:5: ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:421:6: DOUBLE_AMPER ( fullAnnotation[null] )? operator
		{
		match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_synpred11_DRL6Expressions1972); if (state.failed) return;
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:421:19: ( fullAnnotation[null] )?
		int alt99=2;
		int LA99_0 = input.LA(1);
		if ( (LA99_0==AT) ) {
			alt99=1;
		}
		switch (alt99) {
			case 1 :
				// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:421:19: fullAnnotation[null]
				{
				pushFollow(FOLLOW_fullAnnotation_in_synpred11_DRL6Expressions1974);
				fullAnnotation(null);
				state._fsp--;
				if (state.failed) return;
				}
				break;

		}

		pushFollow(FOLLOW_operator_in_synpred11_DRL6Expressions1978);
		operator();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred11_DRL6Expressions

	// $ANTLR start synpred12_DRL6Expressions
	public final void synpred12_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:438:8: ( squareArguments shiftExpression )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:438:9: squareArguments shiftExpression
		{
		pushFollow(FOLLOW_squareArguments_in_synpred12_DRL6Expressions2066);
		squareArguments();
		state._fsp--;
		if (state.failed) return;
		pushFollow(FOLLOW_shiftExpression_in_synpred12_DRL6Expressions2068);
		shiftExpression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred12_DRL6Expressions

	// $ANTLR start synpred13_DRL6Expressions
	public final void synpred13_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:461:7: ( shiftOp )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:461:8: shiftOp
		{
		pushFollow(FOLLOW_shiftOp_in_synpred13_DRL6Expressions2157);
		shiftOp();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred13_DRL6Expressions

	// $ANTLR start synpred14_DRL6Expressions
	public final void synpred14_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:472:11: ( PLUS | MINUS )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:
		{
		if ( input.LA(1)==MINUS||input.LA(1)==PLUS ) {
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
	// $ANTLR end synpred14_DRL6Expressions

	// $ANTLR start synpred15_DRL6Expressions
	public final void synpred15_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:504:9: ( castExpression )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:504:10: castExpression
		{
		pushFollow(FOLLOW_castExpression_in_synpred15_DRL6Expressions2477);
		castExpression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred15_DRL6Expressions

	// $ANTLR start synpred16_DRL6Expressions
	public final void synpred16_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:512:11: ( DIV ID )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:512:12: DIV ID
		{
		match(input,DIV,FOLLOW_DIV_in_synpred16_DRL6Expressions2598); if (state.failed) return;
		match(input,ID,FOLLOW_ID_in_synpred16_DRL6Expressions2600); if (state.failed) return;
		}

	}
	// $ANTLR end synpred16_DRL6Expressions

	// $ANTLR start synpred17_DRL6Expressions
	public final void synpred17_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:516:10: ( selector )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:516:11: selector
		{
		pushFollow(FOLLOW_selector_in_synpred17_DRL6Expressions2648);
		selector();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred17_DRL6Expressions

	// $ANTLR start synpred18_DRL6Expressions
	public final void synpred18_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:535:10: ( INCR | DECR )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:
		{
		if ( input.LA(1)==DECR||input.LA(1)==INCR ) {
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
	// $ANTLR end synpred18_DRL6Expressions

	// $ANTLR start synpred19_DRL6Expressions
	public final void synpred19_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:539:8: ( LEFT_PAREN primitiveType )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:539:9: LEFT_PAREN primitiveType
		{
		match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred19_DRL6Expressions2706); if (state.failed) return;
		pushFollow(FOLLOW_primitiveType_in_synpred19_DRL6Expressions2708);
		primitiveType();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred19_DRL6Expressions

	// $ANTLR start synpred20_DRL6Expressions
	public final void synpred20_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:540:8: ( LEFT_PAREN type )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:540:9: LEFT_PAREN type
		{
		match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred20_DRL6Expressions2731); if (state.failed) return;
		pushFollow(FOLLOW_type_in_synpred20_DRL6Expressions2733);
		type();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred20_DRL6Expressions

	// $ANTLR start synpred22_DRL6Expressions
	public final void synpred22_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:564:8: ( HASH ID )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:564:9: HASH ID
		{
		match(input,HASH,FOLLOW_HASH_in_synpred22_DRL6Expressions2915); if (state.failed) return;
		match(input,ID,FOLLOW_ID_in_synpred22_DRL6Expressions2917); if (state.failed) return;
		}

	}
	// $ANTLR end synpred22_DRL6Expressions

	// $ANTLR start synpred23_DRL6Expressions
	public final void synpred23_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:569:7: ( parExpression )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:569:8: parExpression
		{
		pushFollow(FOLLOW_parExpression_in_synpred23_DRL6Expressions2971);
		parExpression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred23_DRL6Expressions

	// $ANTLR start synpred24_DRL6Expressions
	public final void synpred24_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:570:9: ( nonWildcardTypeArguments )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:570:10: nonWildcardTypeArguments
		{
		pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred24_DRL6Expressions2990);
		nonWildcardTypeArguments();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred24_DRL6Expressions

	// $ANTLR start synpred25_DRL6Expressions
	public final void synpred25_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:571:9: ( literal )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:571:10: literal
		{
		pushFollow(FOLLOW_literal_in_synpred25_DRL6Expressions3015);
		literal();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred25_DRL6Expressions

	// $ANTLR start synpred26_DRL6Expressions
	public final void synpred26_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:573:9: ( super_key )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:573:10: super_key
		{
		pushFollow(FOLLOW_super_key_in_synpred26_DRL6Expressions3037);
		super_key();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred26_DRL6Expressions

	// $ANTLR start synpred27_DRL6Expressions
	public final void synpred27_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:574:9: ( new_key )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:574:10: new_key
		{
		pushFollow(FOLLOW_new_key_in_synpred27_DRL6Expressions3054);
		new_key();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred27_DRL6Expressions

	// $ANTLR start synpred28_DRL6Expressions
	public final void synpred28_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:575:9: ( primitiveType )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:575:10: primitiveType
		{
		pushFollow(FOLLOW_primitiveType_in_synpred28_DRL6Expressions3071);
		primitiveType();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred28_DRL6Expressions

	// $ANTLR start synpred29_DRL6Expressions
	public final void synpred29_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:577:9: ( inlineMapExpression )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:577:10: inlineMapExpression
		{
		pushFollow(FOLLOW_inlineMapExpression_in_synpred29_DRL6Expressions3102);
		inlineMapExpression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred29_DRL6Expressions

	// $ANTLR start synpred30_DRL6Expressions
	public final void synpred30_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:578:9: ( inlineListExpression )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:578:10: inlineListExpression
		{
		pushFollow(FOLLOW_inlineListExpression_in_synpred30_DRL6Expressions3117);
		inlineListExpression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred30_DRL6Expressions

	// $ANTLR start synpred31_DRL6Expressions
	public final void synpred31_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:579:9: ( ID )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:579:10: ID
		{
		match(input,ID,FOLLOW_ID_in_synpred31_DRL6Expressions3132); if (state.failed) return;
		}

	}
	// $ANTLR end synpred31_DRL6Expressions

	// $ANTLR start synpred32_DRL6Expressions
	public final void synpred32_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:581:15: ( DOT ID )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:581:16: DOT ID
		{
		match(input,DOT,FOLLOW_DOT_in_synpred32_DRL6Expressions3166); if (state.failed) return;
		match(input,ID,FOLLOW_ID_in_synpred32_DRL6Expressions3168); if (state.failed) return;
		}

	}
	// $ANTLR end synpred32_DRL6Expressions

	// $ANTLR start synpred33_DRL6Expressions
	public final void synpred33_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:583:15: ( DOT LEFT_PAREN )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:583:16: DOT LEFT_PAREN
		{
		match(input,DOT,FOLLOW_DOT_in_synpred33_DRL6Expressions3212); if (state.failed) return;
		match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred33_DRL6Expressions3214); if (state.failed) return;
		}

	}
	// $ANTLR end synpred33_DRL6Expressions

	// $ANTLR start synpred34_DRL6Expressions
	public final void synpred34_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:588:15: ( HASH ID )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:588:16: HASH ID
		{
		match(input,HASH,FOLLOW_HASH_in_synpred34_DRL6Expressions3355); if (state.failed) return;
		match(input,ID,FOLLOW_ID_in_synpred34_DRL6Expressions3357); if (state.failed) return;
		}

	}
	// $ANTLR end synpred34_DRL6Expressions

	// $ANTLR start synpred35_DRL6Expressions
	public final void synpred35_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:590:15: ( NULL_SAFE_DOT ID )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:590:16: NULL_SAFE_DOT ID
		{
		match(input,NULL_SAFE_DOT,FOLLOW_NULL_SAFE_DOT_in_synpred35_DRL6Expressions3401); if (state.failed) return;
		match(input,ID,FOLLOW_ID_in_synpred35_DRL6Expressions3403); if (state.failed) return;
		}

	}
	// $ANTLR end synpred35_DRL6Expressions

	// $ANTLR start synpred36_DRL6Expressions
	public final void synpred36_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:591:13: ( identifierSuffix )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:591:14: identifierSuffix
		{
		pushFollow(FOLLOW_identifierSuffix_in_synpred36_DRL6Expressions3431);
		identifierSuffix();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred36_DRL6Expressions

	// $ANTLR start synpred37_DRL6Expressions
	public final void synpred37_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:624:7: ( LEFT_SQUARE RIGHT_SQUARE )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:624:8: LEFT_SQUARE RIGHT_SQUARE
		{
		match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred37_DRL6Expressions3589); if (state.failed) return;
		match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred37_DRL6Expressions3591); if (state.failed) return;
		}

	}
	// $ANTLR end synpred37_DRL6Expressions

	// $ANTLR start synpred38_DRL6Expressions
	public final void synpred38_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:627:8: ( LEFT_SQUARE )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:627:9: LEFT_SQUARE
		{
		match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred38_DRL6Expressions3694); if (state.failed) return;
		}

	}
	// $ANTLR end synpred38_DRL6Expressions

	// $ANTLR start synpred39_DRL6Expressions
	public final void synpred39_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:656:107: ( LEFT_SQUARE RIGHT_SQUARE )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:656:108: LEFT_SQUARE RIGHT_SQUARE
		{
		match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred39_DRL6Expressions3956); if (state.failed) return;
		match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred39_DRL6Expressions3958); if (state.failed) return;
		}

	}
	// $ANTLR end synpred39_DRL6Expressions

	// $ANTLR start synpred40_DRL6Expressions
	public final void synpred40_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:687:9: ( DOT super_key )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:687:10: DOT super_key
		{
		match(input,DOT,FOLLOW_DOT_in_synpred40_DRL6Expressions4154); if (state.failed) return;
		pushFollow(FOLLOW_super_key_in_synpred40_DRL6Expressions4156);
		super_key();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred40_DRL6Expressions

	// $ANTLR start synpred41_DRL6Expressions
	public final void synpred41_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:688:9: ( DOT new_key )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:688:10: DOT new_key
		{
		match(input,DOT,FOLLOW_DOT_in_synpred41_DRL6Expressions4176); if (state.failed) return;
		pushFollow(FOLLOW_new_key_in_synpred41_DRL6Expressions4178);
		new_key();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred41_DRL6Expressions

	// $ANTLR start synpred42_DRL6Expressions
	public final void synpred42_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:689:9: ( DOT ID )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:689:10: DOT ID
		{
		match(input,DOT,FOLLOW_DOT_in_synpred42_DRL6Expressions4203); if (state.failed) return;
		match(input,ID,FOLLOW_ID_in_synpred42_DRL6Expressions4205); if (state.failed) return;
		}

	}
	// $ANTLR end synpred42_DRL6Expressions

	// $ANTLR start synpred43_DRL6Expressions
	public final void synpred43_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:691:20: ( LEFT_PAREN )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:691:21: LEFT_PAREN
		{
		match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred43_DRL6Expressions4254); if (state.failed) return;
		}

	}
	// $ANTLR end synpred43_DRL6Expressions

	// $ANTLR start synpred44_DRL6Expressions
	public final void synpred44_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:693:9: ( LEFT_SQUARE )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:693:10: LEFT_SQUARE
		{
		match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred44_DRL6Expressions4277); if (state.failed) return;
		}

	}
	// $ANTLR end synpred44_DRL6Expressions

	// $ANTLR start synpred45_DRL6Expressions
	public final void synpred45_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:700:18: ( LEFT_PAREN )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:700:19: LEFT_PAREN
		{
		match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred45_DRL6Expressions4368); if (state.failed) return;
		}

	}
	// $ANTLR end synpred45_DRL6Expressions

	// $ANTLR start synpred46_DRL6Expressions
	public final void synpred46_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:730:7: ( GREATER GREATER GREATER )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:730:8: GREATER GREATER GREATER
		{
		match(input,GREATER,FOLLOW_GREATER_in_synpred46_DRL6Expressions4598); if (state.failed) return;
		match(input,GREATER,FOLLOW_GREATER_in_synpred46_DRL6Expressions4600); if (state.failed) return;
		match(input,GREATER,FOLLOW_GREATER_in_synpred46_DRL6Expressions4602); if (state.failed) return;
		}

	}
	// $ANTLR end synpred46_DRL6Expressions

	// $ANTLR start synpred47_DRL6Expressions
	public final void synpred47_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:731:7: ( GREATER GREATER )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:731:8: GREATER GREATER
		{
		match(input,GREATER,FOLLOW_GREATER_in_synpred47_DRL6Expressions4621); if (state.failed) return;
		match(input,GREATER,FOLLOW_GREATER_in_synpred47_DRL6Expressions4623); if (state.failed) return;
		}

	}
	// $ANTLR end synpred47_DRL6Expressions

	// Delegated rules

	public final boolean synpred33_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred33_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred9_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred9_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred12_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred12_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred47_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred47_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred42_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred42_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred22_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred22_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred17_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred17_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred14_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred14_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred11_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred11_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred39_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred39_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred2_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred2_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred13_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred13_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred24_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred24_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred8_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred8_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred6_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred6_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred23_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred23_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred30_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred30_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred26_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred26_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred15_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred15_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred34_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred34_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred3_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred3_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred41_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred41_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred38_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred38_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred40_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred40_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred7_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred7_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred45_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred45_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred44_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred44_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred37_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred37_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred5_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred5_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred16_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred16_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred10_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred10_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred18_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred18_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred20_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred20_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred46_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred46_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred28_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred28_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred25_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred25_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred43_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred43_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred27_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred27_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred4_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred4_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred29_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred29_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred1_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred1_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred19_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred19_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred31_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred31_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred36_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred36_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred35_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred35_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred32_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred32_DRL6Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}



	public static final BitSet FOLLOW_STRING_in_literal92 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DECIMAL_in_literal109 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_HEX_in_literal125 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FLOAT_in_literal145 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_BOOL_in_literal163 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NULL_in_literal182 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TIME_INTERVAL_in_literal203 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_STAR_in_literal215 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TILDE_in_operator256 = new BitSet(new long[]{0x000201808C100000L,0x0000000000000002L});
	public static final BitSet FOLLOW_EQUALS_in_operator267 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NOT_EQUALS_in_operator286 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_relationalOp_in_operator301 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LESS_EQUALS_in_relationalOp342 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GREATER_EQUALS_in_relationalOp358 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LESS_in_relationalOp371 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GREATER_in_relationalOp394 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_complexOp_in_relationalOp414 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_not_key_in_relationalOp429 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_neg_operator_key_in_relationalOp433 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_operator_key_in_relationalOp445 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TILDE_in_complexOp477 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_EQUALS_ASSIGN_in_complexOp481 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_typeList502 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_COMMA_in_typeList505 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_type_in_typeList507 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_typeMatch_in_type529 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_primitiveType_in_typeMatch555 = new BitSet(new long[]{0x0000004000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_typeMatch565 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_typeMatch567 = new BitSet(new long[]{0x0000004000000002L});
	public static final BitSet FOLLOW_ID_in_typeMatch581 = new BitSet(new long[]{0x000000C000010002L});
	public static final BitSet FOLLOW_typeArguments_in_typeMatch588 = new BitSet(new long[]{0x0000004000010002L});
	public static final BitSet FOLLOW_DOT_in_typeMatch593 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_typeMatch595 = new BitSet(new long[]{0x000000C000010002L});
	public static final BitSet FOLLOW_typeArguments_in_typeMatch602 = new BitSet(new long[]{0x0000004000010002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_typeMatch617 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_typeMatch619 = new BitSet(new long[]{0x0000004000000002L});
	public static final BitSet FOLLOW_LESS_in_typeArguments640 = new BitSet(new long[]{0x0200000080000000L});
	public static final BitSet FOLLOW_typeArgument_in_typeArguments642 = new BitSet(new long[]{0x0000000004000400L});
	public static final BitSet FOLLOW_COMMA_in_typeArguments645 = new BitSet(new long[]{0x0200000080000000L});
	public static final BitSet FOLLOW_typeArgument_in_typeArguments647 = new BitSet(new long[]{0x0000000004000400L});
	public static final BitSet FOLLOW_GREATER_in_typeArguments651 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_typeArgument668 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_QUESTION_in_typeArgument676 = new BitSet(new long[]{0x0000000080000002L});
	public static final BitSet FOLLOW_extends_key_in_typeArgument680 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_super_key_in_typeArgument684 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_type_in_typeArgument687 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_dummy711 = new BitSet(new long[]{0x2800000080000080L});
	public static final BitSet FOLLOW_set_in_dummy713 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_relationalExpression_in_dummy2747 = new BitSet(new long[]{0x0000000000000000L});
	public static final BitSet FOLLOW_EOF_in_dummy2749 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_conditionalExpression_in_expression768 = new BitSet(new long[]{0x0110A48004208022L,0x0000000000000080L});
	public static final BitSet FOLLOW_assignmentOperator_in_expression789 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_expression793 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression820 = new BitSet(new long[]{0x0200000000000002L});
	public static final BitSet FOLLOW_ternaryExpression_in_conditionalExpression832 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_QUESTION_in_ternaryExpression854 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_ternaryExpression858 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_COLON_in_ternaryExpression860 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_ternaryExpression864 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AT_in_fullAnnotation894 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_fullAnnotation898 = new BitSet(new long[]{0x0000002000010000L});
	public static final BitSet FOLLOW_DOT_in_fullAnnotation904 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_fullAnnotation908 = new BitSet(new long[]{0x0000002000010000L});
	public static final BitSet FOLLOW_annotationArgs_in_fullAnnotation929 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_annotationArgs945 = new BitSet(new long[]{0x888502F1A1007180L,0x0000000000000007L});
	public static final BitSet FOLLOW_annotationElementValuePairs_in_annotationArgs968 = new BitSet(new long[]{0x0800000000000000L});
	public static final BitSet FOLLOW_annotationValue_in_annotationArgs982 = new BitSet(new long[]{0x0800000000000000L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_annotationArgs998 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotationElementValuePair_in_annotationElementValuePairs1013 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_COMMA_in_annotationElementValuePairs1018 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_annotationElementValuePair_in_annotationElementValuePairs1020 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_ID_in_annotationElementValuePair1041 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_EQUALS_ASSIGN_in_annotationElementValuePair1043 = new BitSet(new long[]{0x808502F1A1007180L,0x0000000000000007L});
	public static final BitSet FOLLOW_annotationValue_in_annotationElementValuePair1047 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_annotationValue1070 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotationArray_in_annotationValue1082 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fullAnnotation_in_annotationValue1095 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_CURLY_in_annotationArray1122 = new BitSet(new long[]{0x848502F1A1007180L,0x0000000000000007L});
	public static final BitSet FOLLOW_annotationValue_in_annotationArray1128 = new BitSet(new long[]{0x0400000000000400L});
	public static final BitSet FOLLOW_COMMA_in_annotationArray1151 = new BitSet(new long[]{0x808502F1A1007180L,0x0000000000000007L});
	public static final BitSet FOLLOW_annotationValue_in_annotationArray1155 = new BitSet(new long[]{0x0400000000000400L});
	public static final BitSet FOLLOW_RIGHT_CURLY_in_annotationArray1171 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression1192 = new BitSet(new long[]{0x0000000000040002L});
	public static final BitSet FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression1201 = new BitSet(new long[]{0x808502E1A1007180L,0x0000000000000007L});
	public static final BitSet FOLLOW_fullAnnotation_in_conditionalOrExpression1223 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression1229 = new BitSet(new long[]{0x0000000000040002L});
	public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression1264 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression1272 = new BitSet(new long[]{0x808502E1A1007180L,0x0000000000000007L});
	public static final BitSet FOLLOW_fullAnnotation_in_conditionalAndExpression1295 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression1301 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression1336 = new BitSet(new long[]{0x0040000000000002L});
	public static final BitSet FOLLOW_PIPE_in_inclusiveOrExpression1344 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression1348 = new BitSet(new long[]{0x0040000000000002L});
	public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression1383 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
	public static final BitSet FOLLOW_XOR_in_exclusiveOrExpression1391 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression1395 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
	public static final BitSet FOLLOW_equalityExpression_in_andExpression1430 = new BitSet(new long[]{0x0000000000000012L});
	public static final BitSet FOLLOW_AMPER_in_andExpression1438 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_equalityExpression_in_andExpression1442 = new BitSet(new long[]{0x0000000000000012L});
	public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression1477 = new BitSet(new long[]{0x0002000000100002L});
	public static final BitSet FOLLOW_EQUALS_in_equalityExpression1489 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_NOT_EQUALS_in_equalityExpression1495 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression1511 = new BitSet(new long[]{0x0002000000100002L});
	public static final BitSet FOLLOW_inExpression_in_instanceOfExpression1546 = new BitSet(new long[]{0x0000000080000002L});
	public static final BitSet FOLLOW_instanceof_key_in_instanceOfExpression1556 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_type_in_instanceOfExpression1570 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_relationalExpression_in_inExpression1615 = new BitSet(new long[]{0x0000000080000002L});
	public static final BitSet FOLLOW_not_key_in_inExpression1635 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_in_key_in_inExpression1639 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_inExpression1641 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_inExpression1663 = new BitSet(new long[]{0x0800000000000400L});
	public static final BitSet FOLLOW_COMMA_in_inExpression1682 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_inExpression1686 = new BitSet(new long[]{0x0800000000000400L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_inExpression1707 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_in_key_in_inExpression1723 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_inExpression1725 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_inExpression1747 = new BitSet(new long[]{0x0800000000000400L});
	public static final BitSet FOLLOW_COMMA_in_inExpression1766 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_inExpression1770 = new BitSet(new long[]{0x0800000000000400L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_inExpression1791 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_shiftExpression_in_relationalExpression1832 = new BitSet(new long[]{0x000201A08C100002L,0x0000000000000002L});
	public static final BitSet FOLLOW_orRestriction_in_relationalExpression1857 = new BitSet(new long[]{0x000201A08C100002L,0x0000000000000002L});
	public static final BitSet FOLLOW_andRestriction_in_orRestriction1892 = new BitSet(new long[]{0x0000000000040002L});
	public static final BitSet FOLLOW_DOUBLE_PIPE_in_orRestriction1914 = new BitSet(new long[]{0x000201A08C100080L,0x0000000000000002L});
	public static final BitSet FOLLOW_fullAnnotation_in_orRestriction1918 = new BitSet(new long[]{0x000201A08C100000L,0x0000000000000002L});
	public static final BitSet FOLLOW_andRestriction_in_orRestriction1924 = new BitSet(new long[]{0x0000000000040002L});
	public static final BitSet FOLLOW_EOF_in_orRestriction1943 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_singleRestriction_in_andRestriction1963 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_DOUBLE_AMPER_in_andRestriction1983 = new BitSet(new long[]{0x000201A08C100080L,0x0000000000000002L});
	public static final BitSet FOLLOW_fullAnnotation_in_andRestriction2004 = new BitSet(new long[]{0x000201A08C100000L,0x0000000000000002L});
	public static final BitSet FOLLOW_singleRestriction_in_andRestriction2009 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_operator_in_singleRestriction2045 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_squareArguments_in_singleRestriction2074 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_shiftExpression_in_singleRestriction2078 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_shiftExpression_in_singleRestriction2091 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_singleRestriction2116 = new BitSet(new long[]{0x000201A08C100000L,0x0000000000000002L});
	public static final BitSet FOLLOW_orRestriction_in_singleRestriction2120 = new BitSet(new long[]{0x0800000000000000L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_singleRestriction2122 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_additiveExpression_in_shiftExpression2146 = new BitSet(new long[]{0x0000008004000002L});
	public static final BitSet FOLLOW_shiftOp_in_shiftExpression2160 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_additiveExpression_in_shiftExpression2162 = new BitSet(new long[]{0x0000008004000002L});
	public static final BitSet FOLLOW_LESS_in_shiftOp2182 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_LESS_in_shiftOp2184 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GREATER_in_shiftOp2196 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_shiftOp2198 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_shiftOp2200 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GREATER_in_shiftOp2212 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_shiftOp2214 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression2242 = new BitSet(new long[]{0x0080020000000002L});
	public static final BitSet FOLLOW_set_in_additiveExpression2263 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression2271 = new BitSet(new long[]{0x0080020000000002L});
	public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression2299 = new BitSet(new long[]{0x8000100000004002L});
	public static final BitSet FOLLOW_set_in_multiplicativeExpression2311 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression2325 = new BitSet(new long[]{0x8000100000004002L});
	public static final BitSet FOLLOW_PLUS_in_unaryExpression2351 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpression2355 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MINUS_in_unaryExpression2373 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpression2377 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_INCR_in_unaryExpression2397 = new BitSet(new long[]{0x800400E0A1001100L,0x0000000000000005L});
	public static final BitSet FOLLOW_primary_in_unaryExpression2399 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DECR_in_unaryExpression2409 = new BitSet(new long[]{0x800400E0A1001100L,0x0000000000000005L});
	public static final BitSet FOLLOW_primary_in_unaryExpression2411 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression2423 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TILDE_in_unaryExpressionNotPlusMinus2453 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2455 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus2464 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2466 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus2480 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_unaryExpressionNotPlusMinus2508 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_COLON_in_unaryExpressionNotPlusMinus2510 = new BitSet(new long[]{0x800400E0A1005100L,0x0000000000000005L});
	public static final BitSet FOLLOW_ID_in_unaryExpressionNotPlusMinus2549 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
	public static final BitSet FOLLOW_UNIFY_in_unaryExpressionNotPlusMinus2551 = new BitSet(new long[]{0x800400E0A1005100L,0x0000000000000005L});
	public static final BitSet FOLLOW_xpathPrimary_in_unaryExpressionNotPlusMinus2605 = new BitSet(new long[]{0x0000004100012002L});
	public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus2623 = new BitSet(new long[]{0x0000004100012002L});
	public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus2651 = new BitSet(new long[]{0x0000004100012002L});
	public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus2681 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression2713 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_primitiveType_in_castExpression2715 = new BitSet(new long[]{0x0800000000000000L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression2717 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_unaryExpression_in_castExpression2721 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression2738 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_type_in_castExpression2740 = new BitSet(new long[]{0x0800000000000000L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression2742 = new BitSet(new long[]{0x800500E0A1005100L,0x0000000000000007L});
	public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression2744 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_boolean_key_in_primitiveType2763 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_char_key_in_primitiveType2771 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_byte_key_in_primitiveType2779 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_short_key_in_primitiveType2787 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_int_key_in_primitiveType2795 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_long_key_in_primitiveType2803 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_float_key_in_primitiveType2811 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_double_key_in_primitiveType2819 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_xpathChunk_in_xpathPrimary2840 = new BitSet(new long[]{0x0000000000004002L});
	public static final BitSet FOLLOW_DIV_in_xpathChunk2869 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_xpathChunk2871 = new BitSet(new long[]{0x0000004000010002L});
	public static final BitSet FOLLOW_DOT_in_xpathChunk2874 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_xpathChunk2876 = new BitSet(new long[]{0x0000004000010002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_xpathChunk2881 = new BitSet(new long[]{0x808502E1B1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_xpathExpressionList_in_xpathChunk2883 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_xpathChunk2885 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_HASH_in_xpathExpressionList2921 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_xpathExpressionList2923 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_expression_in_xpathExpressionList2929 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_COMMA_in_xpathExpressionList2941 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_xpathExpressionList2945 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_parExpression_in_primary2977 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_primary2994 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_explicitGenericInvocationSuffix_in_primary2997 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_this_key_in_primary3001 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_arguments_in_primary3003 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literal_in_primary3019 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_super_key_in_primary3041 = new BitSet(new long[]{0x0000002000010000L});
	public static final BitSet FOLLOW_superSuffix_in_primary3043 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_new_key_in_primary3058 = new BitSet(new long[]{0x0000008080000000L});
	public static final BitSet FOLLOW_creator_in_primary3060 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_primitiveType_in_primary3075 = new BitSet(new long[]{0x0000004000010000L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_primary3078 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_primary3080 = new BitSet(new long[]{0x0000004000010000L});
	public static final BitSet FOLLOW_DOT_in_primary3084 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_class_key_in_primary3086 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_inlineMapExpression_in_primary3106 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_inlineListExpression_in_primary3121 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_primary3137 = new BitSet(new long[]{0x0008006010010002L});
	public static final BitSet FOLLOW_DOT_in_primary3173 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_primary3177 = new BitSet(new long[]{0x0008006010010002L});
	public static final BitSet FOLLOW_DOT_in_primary3219 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_primary3221 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_primary3261 = new BitSet(new long[]{0x0800000000000400L});
	public static final BitSet FOLLOW_COMMA_in_primary3264 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_primary3268 = new BitSet(new long[]{0x0800000000000400L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_primary3308 = new BitSet(new long[]{0x0008006010010002L});
	public static final BitSet FOLLOW_HASH_in_primary3362 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_primary3366 = new BitSet(new long[]{0x0008006010010002L});
	public static final BitSet FOLLOW_NULL_SAFE_DOT_in_primary3408 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_primary3412 = new BitSet(new long[]{0x0008006010010002L});
	public static final BitSet FOLLOW_identifierSuffix_in_primary3434 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineListExpression3455 = new BitSet(new long[]{0x908502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expressionList_in_inlineListExpression3457 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineListExpression3460 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineMapExpression3481 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_mapExpressionList_in_inlineMapExpression3483 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineMapExpression3485 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_mapEntry_in_mapExpressionList3506 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_COMMA_in_mapExpressionList3509 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_mapEntry_in_mapExpressionList3511 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_expression_in_mapEntry3530 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_COLON_in_mapEntry3532 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_mapEntry3534 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_parExpression3555 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_parExpression3559 = new BitSet(new long[]{0x0800000000000000L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_parExpression3561 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix3595 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix3636 = new BitSet(new long[]{0x0000004000010000L});
	public static final BitSet FOLLOW_DOT_in_identifierSuffix3680 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_class_key_in_identifierSuffix3684 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix3699 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_identifierSuffix3729 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix3757 = new BitSet(new long[]{0x0000004000000002L});
	public static final BitSet FOLLOW_arguments_in_identifierSuffix3773 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator3795 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_createdName_in_creator3798 = new BitSet(new long[]{0x0000006000000000L});
	public static final BitSet FOLLOW_arrayCreatorRest_in_creator3809 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_classCreatorRest_in_creator3813 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_createdName3831 = new BitSet(new long[]{0x0000008000010002L});
	public static final BitSet FOLLOW_typeArguments_in_createdName3833 = new BitSet(new long[]{0x0000000000010002L});
	public static final BitSet FOLLOW_DOT_in_createdName3846 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_createdName3848 = new BitSet(new long[]{0x0000008000010002L});
	public static final BitSet FOLLOW_typeArguments_in_createdName3850 = new BitSet(new long[]{0x0000000000010002L});
	public static final BitSet FOLLOW_primitiveType_in_createdName3865 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_innerCreator3885 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_classCreatorRest_in_innerCreator3887 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3906 = new BitSet(new long[]{0x908502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3916 = new BitSet(new long[]{0x0000005000000000L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3919 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3921 = new BitSet(new long[]{0x0000005000000000L});
	public static final BitSet FOLLOW_arrayInitializer_in_arrayCreatorRest3925 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_arrayCreatorRest3939 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3941 = new BitSet(new long[]{0x0000004000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3946 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_arrayCreatorRest3948 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3950 = new BitSet(new long[]{0x0000004000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3962 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3964 = new BitSet(new long[]{0x0000004000000002L});
	public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer3993 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_variableInitializer4007 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_CURLY_in_arrayInitializer4024 = new BitSet(new long[]{0x848502F1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer4027 = new BitSet(new long[]{0x0400000000000400L});
	public static final BitSet FOLLOW_COMMA_in_arrayInitializer4030 = new BitSet(new long[]{0x808502F1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer4032 = new BitSet(new long[]{0x0400000000000400L});
	public static final BitSet FOLLOW_COMMA_in_arrayInitializer4037 = new BitSet(new long[]{0x0400000000000000L});
	public static final BitSet FOLLOW_RIGHT_CURLY_in_arrayInitializer4044 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_arguments_in_classCreatorRest4061 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation4079 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_arguments_in_explicitGenericInvocation4081 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LESS_in_nonWildcardTypeArguments4098 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments4100 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_nonWildcardTypeArguments4102 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_super_key_in_explicitGenericInvocationSuffix4119 = new BitSet(new long[]{0x0000002000010000L});
	public static final BitSet FOLLOW_superSuffix_in_explicitGenericInvocationSuffix4121 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_explicitGenericInvocationSuffix4132 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_arguments_in_explicitGenericInvocationSuffix4134 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_selector4159 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_super_key_in_selector4163 = new BitSet(new long[]{0x0000002000010000L});
	public static final BitSet FOLLOW_superSuffix_in_selector4165 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_selector4181 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_new_key_in_selector4185 = new BitSet(new long[]{0x0000008080000000L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_selector4188 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_innerCreator_in_selector4192 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_selector4208 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_selector4230 = new BitSet(new long[]{0x0000002000000002L});
	public static final BitSet FOLLOW_arguments_in_selector4259 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_selector4280 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_selector4307 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_selector4332 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_arguments_in_superSuffix4351 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_superSuffix4362 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_superSuffix4364 = new BitSet(new long[]{0x0000002000000002L});
	public static final BitSet FOLLOW_arguments_in_superSuffix4373 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_squareArguments4396 = new BitSet(new long[]{0x908502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expressionList_in_squareArguments4401 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_squareArguments4407 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_arguments4424 = new BitSet(new long[]{0x888502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expressionList_in_arguments4436 = new BitSet(new long[]{0x0800000000000000L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_arguments4447 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_expressionList4477 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_COMMA_in_expressionList4488 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_expressionList4492 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4513 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_PLUS_ASSIGN_in_assignmentOperator4521 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MINUS_ASSIGN_in_assignmentOperator4529 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MULT_ASSIGN_in_assignmentOperator4537 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DIV_ASSIGN_in_assignmentOperator4545 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AND_ASSIGN_in_assignmentOperator4553 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OR_ASSIGN_in_assignmentOperator4561 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_XOR_ASSIGN_in_assignmentOperator4569 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MOD_ASSIGN_in_assignmentOperator4577 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LESS_in_assignmentOperator4585 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_LESS_in_assignmentOperator4587 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4589 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GREATER_in_assignmentOperator4606 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_assignmentOperator4608 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_assignmentOperator4610 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4612 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GREATER_in_assignmentOperator4627 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_assignmentOperator4629 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4631 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_extends_key4661 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_super_key4690 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_instanceof_key4719 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_boolean_key4748 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_char_key4777 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_byte_key4806 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_short_key4835 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_int_key4864 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_float_key4893 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_long_key4922 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_double_key4951 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_void_key4980 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_this_key5009 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_class_key5038 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_new_key5068 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_not_key5097 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_in_key5124 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_operator_key5149 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_neg_operator_key5174 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_primitiveType_in_synpred1_DRL6Expressions548 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred2_DRL6Expressions559 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred2_DRL6Expressions561 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_typeArguments_in_synpred3_DRL6Expressions585 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_typeArguments_in_synpred4_DRL6Expressions599 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred5_DRL6Expressions611 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred5_DRL6Expressions613 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_assignmentOperator_in_synpred6_DRL6Expressions782 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_synpred7_DRL6Expressions961 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_EQUALS_ASSIGN_in_synpred7_DRL6Expressions963 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_not_key_in_synpred8_DRL6Expressions1629 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_in_key_in_synpred8_DRL6Expressions1631 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_operator_in_synpred9_DRL6Expressions1846 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_synpred9_DRL6Expressions1850 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOUBLE_PIPE_in_synpred10_DRL6Expressions1903 = new BitSet(new long[]{0x000201A08C100080L,0x0000000000000002L});
	public static final BitSet FOLLOW_fullAnnotation_in_synpred10_DRL6Expressions1905 = new BitSet(new long[]{0x000201A08C100000L,0x0000000000000002L});
	public static final BitSet FOLLOW_andRestriction_in_synpred10_DRL6Expressions1909 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOUBLE_AMPER_in_synpred11_DRL6Expressions1972 = new BitSet(new long[]{0x000201808C100080L,0x0000000000000002L});
	public static final BitSet FOLLOW_fullAnnotation_in_synpred11_DRL6Expressions1974 = new BitSet(new long[]{0x000201808C100000L,0x0000000000000002L});
	public static final BitSet FOLLOW_operator_in_synpred11_DRL6Expressions1978 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_squareArguments_in_synpred12_DRL6Expressions2066 = new BitSet(new long[]{0x808502E1A1007100L,0x0000000000000007L});
	public static final BitSet FOLLOW_shiftExpression_in_synpred12_DRL6Expressions2068 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_shiftOp_in_synpred13_DRL6Expressions2157 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_castExpression_in_synpred15_DRL6Expressions2477 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DIV_in_synpred16_DRL6Expressions2598 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_synpred16_DRL6Expressions2600 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_selector_in_synpred17_DRL6Expressions2648 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_synpred19_DRL6Expressions2706 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_primitiveType_in_synpred19_DRL6Expressions2708 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_synpred20_DRL6Expressions2731 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_type_in_synpred20_DRL6Expressions2733 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_HASH_in_synpred22_DRL6Expressions2915 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_synpred22_DRL6Expressions2917 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_parExpression_in_synpred23_DRL6Expressions2971 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred24_DRL6Expressions2990 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literal_in_synpred25_DRL6Expressions3015 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_super_key_in_synpred26_DRL6Expressions3037 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_new_key_in_synpred27_DRL6Expressions3054 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_primitiveType_in_synpred28_DRL6Expressions3071 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_inlineMapExpression_in_synpred29_DRL6Expressions3102 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_inlineListExpression_in_synpred30_DRL6Expressions3117 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_synpred31_DRL6Expressions3132 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_synpred32_DRL6Expressions3166 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_synpred32_DRL6Expressions3168 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_synpred33_DRL6Expressions3212 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_synpred33_DRL6Expressions3214 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_HASH_in_synpred34_DRL6Expressions3355 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_synpred34_DRL6Expressions3357 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NULL_SAFE_DOT_in_synpred35_DRL6Expressions3401 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_synpred35_DRL6Expressions3403 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_identifierSuffix_in_synpred36_DRL6Expressions3431 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred37_DRL6Expressions3589 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred37_DRL6Expressions3591 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred38_DRL6Expressions3694 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred39_DRL6Expressions3956 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred39_DRL6Expressions3958 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_synpred40_DRL6Expressions4154 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_super_key_in_synpred40_DRL6Expressions4156 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_synpred41_DRL6Expressions4176 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_new_key_in_synpred41_DRL6Expressions4178 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_synpred42_DRL6Expressions4203 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_synpred42_DRL6Expressions4205 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_synpred43_DRL6Expressions4254 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred44_DRL6Expressions4277 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_synpred45_DRL6Expressions4368 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GREATER_in_synpred46_DRL6Expressions4598 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_synpred46_DRL6Expressions4600 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_synpred46_DRL6Expressions4602 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GREATER_in_synpred47_DRL6Expressions4621 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_synpred47_DRL6Expressions4623 = new BitSet(new long[]{0x0000000000000002L});
}
