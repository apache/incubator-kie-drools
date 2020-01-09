// $ANTLR 3.5.2 src/main/resources/org/drools/compiler/lang/DRL6Expressions.g 2020-01-07 16:38:43

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
		"PLUS", "PLUS_ASSIGN", "QUESTION", "QUESTION_DIV", "RIGHT_CURLY", "RIGHT_PAREN", 
		"RIGHT_SQUARE", "SEMICOLON", "SHARP", "STAR", "STRING", "TILDE", "TIME_INTERVAL", 
		"UNIFY", "UnicodeEscape", "WS", "XOR", "XOR_ASSIGN"
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
	public static final int QUESTION_DIV=58;
	public static final int RIGHT_CURLY=59;
	public static final int RIGHT_PAREN=60;
	public static final int RIGHT_SQUARE=61;
	public static final int SEMICOLON=62;
	public static final int SHARP=63;
	public static final int STAR=64;
	public static final int STRING=65;
	public static final int TILDE=66;
	public static final int TIME_INTERVAL=67;
	public static final int UNIFY=68;
	public static final int UnicodeEscape=69;
	public static final int WS=70;
	public static final int XOR=71;
	public static final int XOR_ASSIGN=72;

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
				if ( ((synpred1_DRL6Expressions()&&(((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))))) ) {
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
					if ( (LA14_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))||((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))))) {
						alt14=1;
					}
					switch (alt14) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:148:17: ( extends_key | super_key ) type
							{
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:148:17: ( extends_key | super_key )
							int alt13=2;
							int LA13_0 = input.LA(1);
							if ( (LA13_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))||((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))))) {
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
			else if ( ((LA19_0 >= AT && LA19_0 <= BOOL)||(LA19_0 >= DECIMAL && LA19_0 <= DIV)||LA19_0==DOT||LA19_0==FLOAT||LA19_0==HEX||LA19_0==INCR||(LA19_0 >= LEFT_CURLY && LA19_0 <= LESS)||LA19_0==MINUS||LA19_0==NEGATION||LA19_0==NULL||LA19_0==PLUS||LA19_0==QUESTION_DIV||(LA19_0 >= STAR && LA19_0 <= TIME_INTERVAL)) ) {
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
			case DOT:
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
			case QUESTION_DIV:
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
			if ( ((LA23_0 >= AT && LA23_0 <= BOOL)||(LA23_0 >= DECIMAL && LA23_0 <= DIV)||LA23_0==DOT||LA23_0==FLOAT||LA23_0==HEX||(LA23_0 >= ID && LA23_0 <= INCR)||(LA23_0 >= LEFT_CURLY && LA23_0 <= LESS)||LA23_0==MINUS||LA23_0==NEGATION||LA23_0==NULL||LA23_0==PLUS||LA23_0==QUESTION_DIV||(LA23_0 >= STAR && LA23_0 <= TIME_INTERVAL)) ) {
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
					if ( (((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false))))&&synpred9_DRL6Expressions())) ) {
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
				else if ( ((LA40_1 >= AMPER && LA40_1 <= AND_ASSIGN)||LA40_1==AT||(LA40_1 >= COLON && LA40_1 <= COMMA)||LA40_1==DIV_ASSIGN||(LA40_1 >= DOUBLE_AMPER && LA40_1 <= DOUBLE_PIPE)||(LA40_1 >= EQUALS && LA40_1 <= EQUALS_ASSIGN)||(LA40_1 >= GREATER && LA40_1 <= GREATER_EQUALS)||LA40_1==ID||LA40_1==LEFT_PAREN||(LA40_1 >= LESS && LA40_1 <= LESS_EQUALS)||LA40_1==MINUS_ASSIGN||LA40_1==MOD_ASSIGN||LA40_1==MULT_ASSIGN||LA40_1==NOT_EQUALS||LA40_1==OR_ASSIGN||LA40_1==PIPE||(LA40_1 >= PLUS_ASSIGN && LA40_1 <= QUESTION)||(LA40_1 >= RIGHT_CURLY && LA40_1 <= SEMICOLON)||LA40_1==TILDE||(LA40_1 >= XOR && LA40_1 <= XOR_ASSIGN)) ) {
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
					else if ( (LA43_0==BOOL||(LA43_0 >= DECIMAL && LA43_0 <= DIV)||LA43_0==DOT||LA43_0==FLOAT||LA43_0==HEX||(LA43_0 >= ID && LA43_0 <= INCR)||LA43_0==LEFT_PAREN||LA43_0==LESS||LA43_0==MINUS||LA43_0==NEGATION||LA43_0==NULL||LA43_0==PLUS||LA43_0==QUESTION_DIV||(LA43_0 >= STAR && LA43_0 <= TIME_INTERVAL)) ) {
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
					else if ( (LA46_3==EOF||LA46_3==BOOL||(LA46_3 >= DECIMAL && LA46_3 <= DIV)||LA46_3==DOT||LA46_3==FLOAT||LA46_3==HEX||(LA46_3 >= ID && LA46_3 <= INCR)||(LA46_3 >= LEFT_PAREN && LA46_3 <= LESS)||LA46_3==MINUS||LA46_3==NEGATION||LA46_3==NULL||LA46_3==PLUS||LA46_3==QUESTION_DIV||(LA46_3 >= STAR && LA46_3 <= TIME_INTERVAL)) ) {
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
			case DOT:
			case FLOAT:
			case HEX:
			case ID:
			case LEFT_PAREN:
			case LEFT_SQUARE:
			case LESS:
			case NEGATION:
			case NULL:
			case QUESTION_DIV:
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:500:1: unaryExpressionNotPlusMinus returns [BaseDescr result] : ( TILDE unaryExpression | NEGATION ue= unaryExpression | ( castExpression )=> castExpression | ( backReferenceExpression )=> backReferenceExpression | ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )? ( ( xpathSeparator ID )=>left2= xpathPrimary |left1= primary ) ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? );
	public final DRL6Expressions.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus() throws RecognitionException {
		DRL6Expressions.unaryExpressionNotPlusMinus_return retval = new DRL6Expressions.unaryExpressionNotPlusMinus_return();
		retval.start = input.LT(1);

		Token var=null;
		Token COLON9=null;
		Token UNIFY10=null;
		BaseDescr ue =null;
		BaseDescr left2 =null;
		BaseDescr left1 =null;

		 boolean isLeft = false; BindingDescr bind = null;
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:502:5: ( TILDE unaryExpression | NEGATION ue= unaryExpression | ( castExpression )=> castExpression | ( backReferenceExpression )=> backReferenceExpression | ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )? ( ( xpathSeparator ID )=>left2= xpathPrimary |left1= primary ) ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? )
			int alt54=5;
			int LA54_0 = input.LA(1);
			if ( (LA54_0==TILDE) ) {
				alt54=1;
			}
			else if ( (LA54_0==NEGATION) ) {
				alt54=2;
			}
			else if ( (LA54_0==LEFT_PAREN) ) {
				int LA54_3 = input.LA(2);
				if ( (synpred15_DRL6Expressions()) ) {
					alt54=3;
				}
				else if ( (true) ) {
					alt54=5;
				}

			}
			else if ( (LA54_0==DOT) && (synpred16_DRL6Expressions())) {
				alt54=4;
			}
			else if ( (LA54_0==BOOL||LA54_0==DECIMAL||LA54_0==DIV||LA54_0==FLOAT||LA54_0==HEX||LA54_0==ID||(LA54_0 >= LEFT_SQUARE && LA54_0 <= LESS)||LA54_0==NULL||LA54_0==QUESTION_DIV||(LA54_0 >= STAR && LA54_0 <= STRING)||LA54_0==TIME_INTERVAL) ) {
				alt54=5;
			}

			else {
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
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:503:8: NEGATION ue= unaryExpression
					{
					match(input,NEGATION,FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus2464); if (state.failed) return retval;
					pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2468);
					ue=unaryExpression();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					            if( buildDescr && ue != null ) {
					                retval.result = ue.negate();
					            }
					        }
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:509:9: ( castExpression )=> castExpression
					{
					pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus2492);
					castExpression();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:510:9: ( backReferenceExpression )=> backReferenceExpression
					{
					pushFollow(FOLLOW_backReferenceExpression_in_unaryExpressionNotPlusMinus2506);
					backReferenceExpression();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:511:9: ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )? ( ( xpathSeparator ID )=>left2= xpathPrimary |left1= primary ) ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )?
					{
					if ( state.backtracking==0 ) { isLeft = helper.getLeftMostExpr() == null;}
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:512:9: ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )?
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
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:512:11: ({...}? (var= ID COLON ) )
							{
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:512:11: ({...}? (var= ID COLON ) )
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:512:12: {...}? (var= ID COLON )
							{
							if ( !((inMap == 0 && ternOp == 0 && input.LA(2) == DRL6Lexer.COLON)) ) {
								if (state.backtracking>0) {state.failed=true; return retval;}
								throw new FailedPredicateException(input, "unaryExpressionNotPlusMinus", "inMap == 0 && ternOp == 0 && input.LA(2) == DRL6Lexer.COLON");
							}
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:512:75: (var= ID COLON )
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:512:76: var= ID COLON
							{
							var=(Token)match(input,ID,FOLLOW_ID_in_unaryExpressionNotPlusMinus2534); if (state.failed) return retval;
							COLON9=(Token)match(input,COLON,FOLLOW_COLON_in_unaryExpressionNotPlusMinus2536); if (state.failed) return retval;
							if ( state.backtracking==0 ) { hasBindings = true; helper.emit(var, DroolsEditorType.IDENTIFIER_VARIABLE); helper.emit(COLON9, DroolsEditorType.SYMBOL); if( buildDescr ) { bind = new BindingDescr((var!=null?var.getText():null), null, false); helper.setStart( bind, var ); } }
							}

							}

							}
							break;
						case 2 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:514:11: ({...}? (var= ID UNIFY ) )
							{
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:514:11: ({...}? (var= ID UNIFY ) )
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:514:12: {...}? (var= ID UNIFY )
							{
							if ( !((inMap == 0 && ternOp == 0 && input.LA(2) == DRL6Lexer.UNIFY)) ) {
								if (state.backtracking>0) {state.failed=true; return retval;}
								throw new FailedPredicateException(input, "unaryExpressionNotPlusMinus", "inMap == 0 && ternOp == 0 && input.LA(2) == DRL6Lexer.UNIFY");
							}
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:514:75: (var= ID UNIFY )
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:514:76: var= ID UNIFY
							{
							var=(Token)match(input,ID,FOLLOW_ID_in_unaryExpressionNotPlusMinus2575); if (state.failed) return retval;
							UNIFY10=(Token)match(input,UNIFY,FOLLOW_UNIFY_in_unaryExpressionNotPlusMinus2577); if (state.failed) return retval;
							if ( state.backtracking==0 ) { hasBindings = true; helper.emit(var, DroolsEditorType.IDENTIFIER_VARIABLE); helper.emit(UNIFY10, DroolsEditorType.SYMBOL); if( buildDescr ) { bind = new BindingDescr((var!=null?var.getText():null), null, true); helper.setStart( bind, var ); } }
							}

							}

							}
							break;

					}

					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:518:9: ( ( xpathSeparator ID )=>left2= xpathPrimary |left1= primary )
					int alt51=2;
					int LA51_0 = input.LA(1);
					if ( (LA51_0==DIV||LA51_0==QUESTION_DIV) && (synpred17_DRL6Expressions())) {
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
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:518:11: ( xpathSeparator ID )=>left2= xpathPrimary
							{
							pushFollow(FOLLOW_xpathPrimary_in_unaryExpressionNotPlusMinus2631);
							left2=xpathPrimary();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) { if( buildDescr ) { retval.result = left2; } }
							}
							break;
						case 2 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:519:13: left1= primary
							{
							pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus2649);
							left1=primary();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) { if( buildDescr ) { retval.result = left1; } }
							}
							break;

					}

					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:522:9: ( ( selector )=> selector )*
					loop52:
					while (true) {
						int alt52=2;
						int LA52_0 = input.LA(1);
						if ( (LA52_0==DOT) && (synpred18_DRL6Expressions())) {
							alt52=1;
						}
						else if ( (LA52_0==NULL_SAFE_DOT) && (synpred18_DRL6Expressions())) {
							alt52=1;
						}
						else if ( (LA52_0==LEFT_SQUARE) && (synpred18_DRL6Expressions())) {
							alt52=1;
						}

						switch (alt52) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:522:10: ( selector )=> selector
							{
							pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus2677);
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
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:541:9: ( ( INCR | DECR )=> ( INCR | DECR ) )?
					int alt53=2;
					int LA53_0 = input.LA(1);
					if ( (LA53_0==DECR||LA53_0==INCR) && (synpred19_DRL6Expressions())) {
						alt53=1;
					}
					switch (alt53) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:541:10: ( INCR | DECR )=> ( INCR | DECR )
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:544:1: castExpression : ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN expr= unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus );
	public final void castExpression() throws RecognitionException {
		BaseDescr expr =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:545:5: ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN expr= unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus )
			int alt55=2;
			int LA55_0 = input.LA(1);
			if ( (LA55_0==LEFT_PAREN) ) {
				int LA55_1 = input.LA(2);
				if ( (synpred20_DRL6Expressions()) ) {
					alt55=1;
				}
				else if ( (synpred21_DRL6Expressions()) ) {
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
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:545:8: ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN expr= unaryExpression
					{
					match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression2739); if (state.failed) return;
					pushFollow(FOLLOW_primitiveType_in_castExpression2741);
					primitiveType();
					state._fsp--;
					if (state.failed) return;
					match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression2743); if (state.failed) return;
					pushFollow(FOLLOW_unaryExpression_in_castExpression2747);
					expr=unaryExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:546:8: ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus
					{
					match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression2764); if (state.failed) return;
					pushFollow(FOLLOW_type_in_castExpression2766);
					type();
					state._fsp--;
					if (state.failed) return;
					match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression2768); if (state.failed) return;
					pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression2770);
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



	// $ANTLR start "backReferenceExpression"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:549:1: backReferenceExpression : ( DOT DOT DIV )=> ( DOT DOT DIV )+ unaryExpressionNotPlusMinus ;
	public final void backReferenceExpression() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:550:5: ( ( DOT DOT DIV )=> ( DOT DOT DIV )+ unaryExpressionNotPlusMinus )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:550:8: ( DOT DOT DIV )=> ( DOT DOT DIV )+ unaryExpressionNotPlusMinus
			{
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:550:25: ( DOT DOT DIV )+
			int cnt56=0;
			loop56:
			while (true) {
				int alt56=2;
				int LA56_0 = input.LA(1);
				if ( (LA56_0==DOT) ) {
					alt56=1;
				}

				switch (alt56) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:550:26: DOT DOT DIV
					{
					match(input,DOT,FOLLOW_DOT_in_backReferenceExpression2799); if (state.failed) return;
					match(input,DOT,FOLLOW_DOT_in_backReferenceExpression2801); if (state.failed) return;
					match(input,DIV,FOLLOW_DIV_in_backReferenceExpression2803); if (state.failed) return;
					}
					break;

				default :
					if ( cnt56 >= 1 ) break loop56;
					if (state.backtracking>0) {state.failed=true; return;}
					EarlyExitException eee = new EarlyExitException(56, input);
					throw eee;
				}
				cnt56++;
			}

			pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_backReferenceExpression2807);
			unaryExpressionNotPlusMinus();
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
	// $ANTLR end "backReferenceExpression"



	// $ANTLR start "primitiveType"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:553:1: primitiveType : ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key );
	public final void primitiveType() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:554:5: ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key )
			int alt57=8;
			int LA57_0 = input.LA(1);
			if ( (LA57_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))))) {
				int LA57_1 = input.LA(2);
				if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))) ) {
					alt57=1;
				}
				else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))) ) {
					alt57=2;
				}
				else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))) ) {
					alt57=3;
				}
				else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))) ) {
					alt57=4;
				}
				else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))) ) {
					alt57=5;
				}
				else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))) ) {
					alt57=6;
				}
				else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))) ) {
					alt57=7;
				}
				else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))) ) {
					alt57=8;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 57, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			switch (alt57) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:554:9: boolean_key
					{
					pushFollow(FOLLOW_boolean_key_in_primitiveType2826);
					boolean_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:555:7: char_key
					{
					pushFollow(FOLLOW_char_key_in_primitiveType2834);
					char_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:556:7: byte_key
					{
					pushFollow(FOLLOW_byte_key_in_primitiveType2842);
					byte_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:557:7: short_key
					{
					pushFollow(FOLLOW_short_key_in_primitiveType2850);
					short_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:558:7: int_key
					{
					pushFollow(FOLLOW_int_key_in_primitiveType2858);
					int_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 6 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:559:7: long_key
					{
					pushFollow(FOLLOW_long_key_in_primitiveType2866);
					long_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 7 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:560:7: float_key
					{
					pushFollow(FOLLOW_float_key_in_primitiveType2874);
					float_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 8 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:561:7: double_key
					{
					pushFollow(FOLLOW_double_key_in_primitiveType2882);
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



	// $ANTLR start "xpathSeparator"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:564:1: xpathSeparator : ( DIV | QUESTION_DIV );
	public final void xpathSeparator() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:565:5: ( DIV | QUESTION_DIV )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:
			{
			if ( input.LA(1)==DIV||input.LA(1)==QUESTION_DIV ) {
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
	// $ANTLR end "xpathSeparator"



	// $ANTLR start "xpathPrimary"
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:569:1: xpathPrimary returns [BaseDescr result] : ( xpathChunk )+ ;
	public final BaseDescr xpathPrimary() throws RecognitionException {
		BaseDescr result = null;


		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:570:5: ( ( xpathChunk )+ )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:570:7: ( xpathChunk )+
			{
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:570:7: ( xpathChunk )+
			int cnt58=0;
			loop58:
			while (true) {
				int alt58=2;
				int LA58_0 = input.LA(1);
				if ( (LA58_0==DIV) ) {
					int LA58_2 = input.LA(2);
					if ( (LA58_2==ID) ) {
						alt58=1;
					}

				}
				else if ( (LA58_0==QUESTION_DIV) ) {
					alt58=1;
				}

				switch (alt58) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:570:7: xpathChunk
					{
					pushFollow(FOLLOW_xpathChunk_in_xpathPrimary2930);
					xpathChunk();
					state._fsp--;
					if (state.failed) return result;
					}
					break;

				default :
					if ( cnt58 >= 1 ) break loop58;
					if (state.backtracking>0) {state.failed=true; return result;}
					EarlyExitException eee = new EarlyExitException(58, input);
					throw eee;
				}
				cnt58++;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:573:1: xpathChunk returns [BaseDescr result] : ( xpathSeparator ID )=> xpathSeparator ID ( DOT ID )* ( HASH ID )? ( LEFT_SQUARE xpathExpressionList RIGHT_SQUARE )? ;
	public final BaseDescr xpathChunk() throws RecognitionException {
		BaseDescr result = null;


		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:574:5: ( ( xpathSeparator ID )=> xpathSeparator ID ( DOT ID )* ( HASH ID )? ( LEFT_SQUARE xpathExpressionList RIGHT_SQUARE )? )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:574:7: ( xpathSeparator ID )=> xpathSeparator ID ( DOT ID )* ( HASH ID )? ( LEFT_SQUARE xpathExpressionList RIGHT_SQUARE )?
			{
			pushFollow(FOLLOW_xpathSeparator_in_xpathChunk2959);
			xpathSeparator();
			state._fsp--;
			if (state.failed) return result;
			match(input,ID,FOLLOW_ID_in_xpathChunk2961); if (state.failed) return result;
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:574:47: ( DOT ID )*
			loop59:
			while (true) {
				int alt59=2;
				int LA59_0 = input.LA(1);
				if ( (LA59_0==DOT) ) {
					int LA59_2 = input.LA(2);
					if ( (LA59_2==ID) ) {
						alt59=1;
					}

				}

				switch (alt59) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:574:48: DOT ID
					{
					match(input,DOT,FOLLOW_DOT_in_xpathChunk2964); if (state.failed) return result;
					match(input,ID,FOLLOW_ID_in_xpathChunk2966); if (state.failed) return result;
					}
					break;

				default :
					break loop59;
				}
			}

			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:574:57: ( HASH ID )?
			int alt60=2;
			int LA60_0 = input.LA(1);
			if ( (LA60_0==HASH) ) {
				alt60=1;
			}
			switch (alt60) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:574:58: HASH ID
					{
					match(input,HASH,FOLLOW_HASH_in_xpathChunk2971); if (state.failed) return result;
					match(input,ID,FOLLOW_ID_in_xpathChunk2973); if (state.failed) return result;
					}
					break;

			}

			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:574:68: ( LEFT_SQUARE xpathExpressionList RIGHT_SQUARE )?
			int alt61=2;
			int LA61_0 = input.LA(1);
			if ( (LA61_0==LEFT_SQUARE) ) {
				alt61=1;
			}
			switch (alt61) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:574:69: LEFT_SQUARE xpathExpressionList RIGHT_SQUARE
					{
					match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_xpathChunk2978); if (state.failed) return result;
					pushFollow(FOLLOW_xpathExpressionList_in_xpathChunk2980);
					xpathExpressionList();
					state._fsp--;
					if (state.failed) return result;
					match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_xpathChunk2982); if (state.failed) return result;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:577:1: xpathExpressionList returns [java.util.List<String> exprs] : f= expression ( COMMA s= expression )* ;
	public final java.util.List<String> xpathExpressionList() throws RecognitionException {
		java.util.List<String> exprs = null;


		ParserRuleReturnScope f =null;
		ParserRuleReturnScope s =null;

		 exprs = new java.util.ArrayList<String>();
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:579:3: (f= expression ( COMMA s= expression )* )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:579:7: f= expression ( COMMA s= expression )*
			{
			pushFollow(FOLLOW_expression_in_xpathExpressionList3012);
			f=expression();
			state._fsp--;
			if (state.failed) return exprs;
			if ( state.backtracking==0 ) { exprs.add( (f!=null?input.toString(f.start,f.stop):null) ); }
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:580:7: ( COMMA s= expression )*
			loop62:
			while (true) {
				int alt62=2;
				int LA62_0 = input.LA(1);
				if ( (LA62_0==COMMA) ) {
					alt62=1;
				}

				switch (alt62) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:580:8: COMMA s= expression
					{
					match(input,COMMA,FOLLOW_COMMA_in_xpathExpressionList3023); if (state.failed) return exprs;
					pushFollow(FOLLOW_expression_in_xpathExpressionList3027);
					s=expression();
					state._fsp--;
					if (state.failed) return exprs;
					if ( state.backtracking==0 ) { exprs.add( (s!=null?input.toString(s.start,s.stop):null) ); }
					}
					break;

				default :
					break loop62;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:583:1: primary returns [BaseDescr result] : ( ( LEFT_PAREN )=>expr= parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=>i1= ID ( ( ( DOT ID )=>d= DOT i2= ID ) | ( ( ( DOT | NULL_SAFE_DOT ) LEFT_PAREN )=>d= ( DOT | NULL_SAFE_DOT ) LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN ) | ( ( HASH ID )=>h= HASH i2= ID ) | ( ( NULL_SAFE_DOT ID )=>n= NULL_SAFE_DOT i2= ID ) )* ( ( identifierSuffix )=> identifierSuffix )? );
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
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:584:5: ( ( LEFT_PAREN )=>expr= parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=>i1= ID ( ( ( DOT ID )=>d= DOT i2= ID ) | ( ( ( DOT | NULL_SAFE_DOT ) LEFT_PAREN )=>d= ( DOT | NULL_SAFE_DOT ) LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN ) | ( ( HASH ID )=>h= HASH i2= ID ) | ( ( NULL_SAFE_DOT ID )=>n= NULL_SAFE_DOT i2= ID ) )* ( ( identifierSuffix )=> identifierSuffix )? )
			int alt68=9;
			int LA68_0 = input.LA(1);
			if ( (LA68_0==LEFT_PAREN) && (synpred24_DRL6Expressions())) {
				alt68=1;
			}
			else if ( (LA68_0==LESS) && (synpred25_DRL6Expressions())) {
				alt68=2;
			}
			else if ( (LA68_0==STRING) && (synpred26_DRL6Expressions())) {
				alt68=3;
			}
			else if ( (LA68_0==DECIMAL) && (synpred26_DRL6Expressions())) {
				alt68=3;
			}
			else if ( (LA68_0==HEX) && (synpred26_DRL6Expressions())) {
				alt68=3;
			}
			else if ( (LA68_0==FLOAT) && (synpred26_DRL6Expressions())) {
				alt68=3;
			}
			else if ( (LA68_0==BOOL) && (synpred26_DRL6Expressions())) {
				alt68=3;
			}
			else if ( (LA68_0==NULL) && (synpred26_DRL6Expressions())) {
				alt68=3;
			}
			else if ( (LA68_0==TIME_INTERVAL) && (synpred26_DRL6Expressions())) {
				alt68=3;
			}
			else if ( (LA68_0==STAR) && (synpred26_DRL6Expressions())) {
				alt68=3;
			}
			else if ( (LA68_0==ID) ) {
				int LA68_11 = input.LA(2);
				if ( ((synpred27_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))))) ) {
					alt68=4;
				}
				else if ( ((((helper.validateIdentifierKey(DroolsSoftKeywords.NEW)))&&synpred28_DRL6Expressions())) ) {
					alt68=5;
				}
				else if ( ((synpred29_DRL6Expressions()&&(((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))))) ) {
					alt68=6;
				}
				else if ( (synpred32_DRL6Expressions()) ) {
					alt68=9;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return result;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 68, 11, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA68_0==LEFT_SQUARE) ) {
				int LA68_12 = input.LA(2);
				if ( (synpred30_DRL6Expressions()) ) {
					alt68=7;
				}
				else if ( (synpred31_DRL6Expressions()) ) {
					alt68=8;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return result;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 68, 12, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return result;}
				NoViableAltException nvae =
					new NoViableAltException("", 68, 0, input);
				throw nvae;
			}

			switch (alt68) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:584:7: ( LEFT_PAREN )=>expr= parExpression
					{
					pushFollow(FOLLOW_parExpression_in_primary3057);
					expr=parExpression();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) {  if( buildDescr  ) { result = expr; }  }
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:585:9: ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments )
					{
					pushFollow(FOLLOW_nonWildcardTypeArguments_in_primary3074);
					nonWildcardTypeArguments();
					state._fsp--;
					if (state.failed) return result;
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:585:63: ( explicitGenericInvocationSuffix | this_key arguments )
					int alt63=2;
					int LA63_0 = input.LA(1);
					if ( (LA63_0==ID) ) {
						int LA63_1 = input.LA(2);
						if ( (!((((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))))) ) {
							alt63=1;
						}
						else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))) ) {
							alt63=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return result;}
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 63, 1, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

					else {
						if (state.backtracking>0) {state.failed=true; return result;}
						NoViableAltException nvae =
							new NoViableAltException("", 63, 0, input);
						throw nvae;
					}

					switch (alt63) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:585:64: explicitGenericInvocationSuffix
							{
							pushFollow(FOLLOW_explicitGenericInvocationSuffix_in_primary3077);
							explicitGenericInvocationSuffix();
							state._fsp--;
							if (state.failed) return result;
							}
							break;
						case 2 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:585:98: this_key arguments
							{
							pushFollow(FOLLOW_this_key_in_primary3081);
							this_key();
							state._fsp--;
							if (state.failed) return result;
							pushFollow(FOLLOW_arguments_in_primary3083);
							arguments();
							state._fsp--;
							if (state.failed) return result;
							}
							break;

					}

					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:586:9: ( literal )=> literal
					{
					pushFollow(FOLLOW_literal_in_primary3099);
					literal11=literal();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { if( buildDescr  ) { result = new AtomicExprDescr( (literal11!=null?input.toString(literal11.start,literal11.stop):null), true ); }  }
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:588:9: ( super_key )=> super_key superSuffix
					{
					pushFollow(FOLLOW_super_key_in_primary3121);
					super_key();
					state._fsp--;
					if (state.failed) return result;
					pushFollow(FOLLOW_superSuffix_in_primary3123);
					superSuffix();
					state._fsp--;
					if (state.failed) return result;
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:589:9: ( new_key )=> new_key creator
					{
					pushFollow(FOLLOW_new_key_in_primary3138);
					new_key();
					state._fsp--;
					if (state.failed) return result;
					pushFollow(FOLLOW_creator_in_primary3140);
					creator();
					state._fsp--;
					if (state.failed) return result;
					}
					break;
				case 6 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:590:9: ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key
					{
					pushFollow(FOLLOW_primitiveType_in_primary3155);
					primitiveType();
					state._fsp--;
					if (state.failed) return result;
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:590:41: ( LEFT_SQUARE RIGHT_SQUARE )*
					loop64:
					while (true) {
						int alt64=2;
						int LA64_0 = input.LA(1);
						if ( (LA64_0==LEFT_SQUARE) ) {
							alt64=1;
						}

						switch (alt64) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:590:42: LEFT_SQUARE RIGHT_SQUARE
							{
							match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_primary3158); if (state.failed) return result;
							match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_primary3160); if (state.failed) return result;
							}
							break;

						default :
							break loop64;
						}
					}

					match(input,DOT,FOLLOW_DOT_in_primary3164); if (state.failed) return result;
					pushFollow(FOLLOW_class_key_in_primary3166);
					class_key();
					state._fsp--;
					if (state.failed) return result;
					}
					break;
				case 7 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:592:9: ( inlineMapExpression )=> inlineMapExpression
					{
					pushFollow(FOLLOW_inlineMapExpression_in_primary3186);
					inlineMapExpression();
					state._fsp--;
					if (state.failed) return result;
					}
					break;
				case 8 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:593:9: ( inlineListExpression )=> inlineListExpression
					{
					pushFollow(FOLLOW_inlineListExpression_in_primary3201);
					inlineListExpression();
					state._fsp--;
					if (state.failed) return result;
					}
					break;
				case 9 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:594:9: ( ID )=>i1= ID ( ( ( DOT ID )=>d= DOT i2= ID ) | ( ( ( DOT | NULL_SAFE_DOT ) LEFT_PAREN )=>d= ( DOT | NULL_SAFE_DOT ) LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN ) | ( ( HASH ID )=>h= HASH i2= ID ) | ( ( NULL_SAFE_DOT ID )=>n= NULL_SAFE_DOT i2= ID ) )* ( ( identifierSuffix )=> identifierSuffix )?
					{
					i1=(Token)match(input,ID,FOLLOW_ID_in_primary3217); if (state.failed) return result;
					if ( state.backtracking==0 ) { helper.emit(i1, DroolsEditorType.IDENTIFIER); }
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:595:9: ( ( ( DOT ID )=>d= DOT i2= ID ) | ( ( ( DOT | NULL_SAFE_DOT ) LEFT_PAREN )=>d= ( DOT | NULL_SAFE_DOT ) LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN ) | ( ( HASH ID )=>h= HASH i2= ID ) | ( ( NULL_SAFE_DOT ID )=>n= NULL_SAFE_DOT i2= ID ) )*
					loop66:
					while (true) {
						int alt66=5;
						int LA66_0 = input.LA(1);
						if ( (LA66_0==DOT) ) {
							int LA66_2 = input.LA(2);
							if ( (LA66_2==ID) ) {
								int LA66_5 = input.LA(3);
								if ( (synpred33_DRL6Expressions()) ) {
									alt66=1;
								}

							}
							else if ( (LA66_2==LEFT_PAREN) && (synpred34_DRL6Expressions())) {
								alt66=2;
							}

						}
						else if ( (LA66_0==NULL_SAFE_DOT) ) {
							int LA66_3 = input.LA(2);
							if ( (LA66_3==LEFT_PAREN) && (synpred34_DRL6Expressions())) {
								alt66=2;
							}
							else if ( (LA66_3==ID) ) {
								int LA66_7 = input.LA(3);
								if ( (synpred36_DRL6Expressions()) ) {
									alt66=4;
								}

							}

						}
						else if ( (LA66_0==HASH) && (synpred35_DRL6Expressions())) {
							alt66=3;
						}

						switch (alt66) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:596:13: ( ( DOT ID )=>d= DOT i2= ID )
							{
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:596:13: ( ( DOT ID )=>d= DOT i2= ID )
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:596:15: ( DOT ID )=>d= DOT i2= ID
							{
							d=(Token)match(input,DOT,FOLLOW_DOT_in_primary3253); if (state.failed) return result;
							i2=(Token)match(input,ID,FOLLOW_ID_in_primary3257); if (state.failed) return result;
							if ( state.backtracking==0 ) { helper.emit(d, DroolsEditorType.SYMBOL); helper.emit(i2, DroolsEditorType.IDENTIFIER); }
							}

							}
							break;
						case 2 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:598:13: ( ( ( DOT | NULL_SAFE_DOT ) LEFT_PAREN )=>d= ( DOT | NULL_SAFE_DOT ) LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN )
							{
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:598:13: ( ( ( DOT | NULL_SAFE_DOT ) LEFT_PAREN )=>d= ( DOT | NULL_SAFE_DOT ) LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN )
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:598:15: ( ( DOT | NULL_SAFE_DOT ) LEFT_PAREN )=>d= ( DOT | NULL_SAFE_DOT ) LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN
							{
							d=input.LT(1);
							if ( input.LA(1)==DOT||input.LA(1)==NULL_SAFE_DOT ) {
								input.consume();
								state.errorRecovery=false;
								state.failed=false;
							}
							else {
								if (state.backtracking>0) {state.failed=true; return result;}
								MismatchedSetException mse = new MismatchedSetException(null,input);
								throw mse;
							}
							LEFT_PAREN12=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_primary3309); if (state.failed) return result;
							if ( state.backtracking==0 ) { helper.emit(d, DroolsEditorType.SYMBOL); helper.emit(LEFT_PAREN12, DroolsEditorType.SYMBOL); }
							pushFollow(FOLLOW_expression_in_primary3349);
							expression();
							state._fsp--;
							if (state.failed) return result;
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:599:48: ( COMMA expression )*
							loop65:
							while (true) {
								int alt65=2;
								int LA65_0 = input.LA(1);
								if ( (LA65_0==COMMA) ) {
									alt65=1;
								}

								switch (alt65) {
								case 1 :
									// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:599:49: COMMA expression
									{
									COMMA13=(Token)match(input,COMMA,FOLLOW_COMMA_in_primary3352); if (state.failed) return result;
									if ( state.backtracking==0 ) { helper.emit(COMMA13, DroolsEditorType.SYMBOL); }
									pushFollow(FOLLOW_expression_in_primary3356);
									expression();
									state._fsp--;
									if (state.failed) return result;
									}
									break;

								default :
									break loop65;
								}
							}

							RIGHT_PAREN14=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_primary3396); if (state.failed) return result;
							if ( state.backtracking==0 ) { helper.emit(RIGHT_PAREN14, DroolsEditorType.SYMBOL); }
							}

							}
							break;
						case 3 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:603:13: ( ( HASH ID )=>h= HASH i2= ID )
							{
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:603:13: ( ( HASH ID )=>h= HASH i2= ID )
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:603:15: ( HASH ID )=>h= HASH i2= ID
							{
							h=(Token)match(input,HASH,FOLLOW_HASH_in_primary3450); if (state.failed) return result;
							i2=(Token)match(input,ID,FOLLOW_ID_in_primary3454); if (state.failed) return result;
							if ( state.backtracking==0 ) { helper.emit(h, DroolsEditorType.SYMBOL); helper.emit(i2, DroolsEditorType.IDENTIFIER); }
							}

							}
							break;
						case 4 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:605:13: ( ( NULL_SAFE_DOT ID )=>n= NULL_SAFE_DOT i2= ID )
							{
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:605:13: ( ( NULL_SAFE_DOT ID )=>n= NULL_SAFE_DOT i2= ID )
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:605:15: ( NULL_SAFE_DOT ID )=>n= NULL_SAFE_DOT i2= ID
							{
							n=(Token)match(input,NULL_SAFE_DOT,FOLLOW_NULL_SAFE_DOT_in_primary3496); if (state.failed) return result;
							i2=(Token)match(input,ID,FOLLOW_ID_in_primary3500); if (state.failed) return result;
							if ( state.backtracking==0 ) { helper.emit(n, DroolsEditorType.SYMBOL); helper.emit(i2, DroolsEditorType.IDENTIFIER); }
							}

							}
							break;

						default :
							break loop66;
						}
					}

					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:606:12: ( ( identifierSuffix )=> identifierSuffix )?
					int alt67=2;
					int LA67_0 = input.LA(1);
					if ( (LA67_0==LEFT_SQUARE) ) {
						int LA67_1 = input.LA(2);
						if ( (synpred37_DRL6Expressions()) ) {
							alt67=1;
						}
					}
					else if ( (LA67_0==LEFT_PAREN) ) {
						int LA67_2 = input.LA(2);
						if ( (synpred37_DRL6Expressions()) ) {
							alt67=1;
						}
					}
					switch (alt67) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:606:13: ( identifierSuffix )=> identifierSuffix
							{
							pushFollow(FOLLOW_identifierSuffix_in_primary3522);
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:609:1: inlineListExpression : LEFT_SQUARE ( expressionList )? RIGHT_SQUARE ;
	public final void inlineListExpression() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:610:5: ( LEFT_SQUARE ( expressionList )? RIGHT_SQUARE )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:610:9: LEFT_SQUARE ( expressionList )? RIGHT_SQUARE
			{
			match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_inlineListExpression3543); if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:610:21: ( expressionList )?
			int alt69=2;
			int LA69_0 = input.LA(1);
			if ( (LA69_0==BOOL||(LA69_0 >= DECIMAL && LA69_0 <= DIV)||LA69_0==DOT||LA69_0==FLOAT||LA69_0==HEX||(LA69_0 >= ID && LA69_0 <= INCR)||(LA69_0 >= LEFT_PAREN && LA69_0 <= LESS)||LA69_0==MINUS||LA69_0==NEGATION||LA69_0==NULL||LA69_0==PLUS||LA69_0==QUESTION_DIV||(LA69_0 >= STAR && LA69_0 <= TIME_INTERVAL)) ) {
				alt69=1;
			}
			switch (alt69) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:610:21: expressionList
					{
					pushFollow(FOLLOW_expressionList_in_inlineListExpression3545);
					expressionList();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_inlineListExpression3548); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:613:1: inlineMapExpression : LEFT_SQUARE mapExpressionList RIGHT_SQUARE ;
	public final void inlineMapExpression() throws RecognitionException {
		 inMap++; 
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:615:5: ( LEFT_SQUARE mapExpressionList RIGHT_SQUARE )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:615:7: LEFT_SQUARE mapExpressionList RIGHT_SQUARE
			{
			match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_inlineMapExpression3569); if (state.failed) return;
			pushFollow(FOLLOW_mapExpressionList_in_inlineMapExpression3571);
			mapExpressionList();
			state._fsp--;
			if (state.failed) return;
			match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_inlineMapExpression3573); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:619:1: mapExpressionList : mapEntry ( COMMA mapEntry )* ;
	public final void mapExpressionList() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:620:5: ( mapEntry ( COMMA mapEntry )* )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:620:7: mapEntry ( COMMA mapEntry )*
			{
			pushFollow(FOLLOW_mapEntry_in_mapExpressionList3594);
			mapEntry();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:620:16: ( COMMA mapEntry )*
			loop70:
			while (true) {
				int alt70=2;
				int LA70_0 = input.LA(1);
				if ( (LA70_0==COMMA) ) {
					alt70=1;
				}

				switch (alt70) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:620:17: COMMA mapEntry
					{
					match(input,COMMA,FOLLOW_COMMA_in_mapExpressionList3597); if (state.failed) return;
					pushFollow(FOLLOW_mapEntry_in_mapExpressionList3599);
					mapEntry();
					state._fsp--;
					if (state.failed) return;
					}
					break;

				default :
					break loop70;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:623:1: mapEntry : expression COLON expression ;
	public final void mapEntry() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:624:5: ( expression COLON expression )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:624:7: expression COLON expression
			{
			pushFollow(FOLLOW_expression_in_mapEntry3618);
			expression();
			state._fsp--;
			if (state.failed) return;
			match(input,COLON,FOLLOW_COLON_in_mapEntry3620); if (state.failed) return;
			pushFollow(FOLLOW_expression_in_mapEntry3622);
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:627:1: parExpression returns [BaseDescr result] : LEFT_PAREN expr= expression RIGHT_PAREN ;
	public final BaseDescr parExpression() throws RecognitionException {
		BaseDescr result = null;


		ParserRuleReturnScope expr =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:628:5: ( LEFT_PAREN expr= expression RIGHT_PAREN )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:628:7: LEFT_PAREN expr= expression RIGHT_PAREN
			{
			match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_parExpression3643); if (state.failed) return result;
			pushFollow(FOLLOW_expression_in_parExpression3647);
			expr=expression();
			state._fsp--;
			if (state.failed) return result;
			match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_parExpression3649); if (state.failed) return result;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:638:1: identifierSuffix : ( ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments );
	public final void identifierSuffix() throws RecognitionException {
		Token LEFT_SQUARE15=null;
		Token RIGHT_SQUARE16=null;
		Token DOT17=null;
		Token LEFT_SQUARE18=null;
		Token RIGHT_SQUARE19=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:639:5: ( ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments )
			int alt73=3;
			int LA73_0 = input.LA(1);
			if ( (LA73_0==LEFT_SQUARE) ) {
				int LA73_1 = input.LA(2);
				if ( (LA73_1==RIGHT_SQUARE) && (synpred38_DRL6Expressions())) {
					alt73=1;
				}
				else if ( (LA73_1==BOOL||(LA73_1 >= DECIMAL && LA73_1 <= DIV)||LA73_1==DOT||LA73_1==FLOAT||LA73_1==HEX||(LA73_1 >= ID && LA73_1 <= INCR)||(LA73_1 >= LEFT_PAREN && LA73_1 <= LESS)||LA73_1==MINUS||LA73_1==NEGATION||LA73_1==NULL||LA73_1==PLUS||LA73_1==QUESTION_DIV||(LA73_1 >= STAR && LA73_1 <= TIME_INTERVAL)) ) {
					alt73=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 73, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA73_0==LEFT_PAREN) ) {
				alt73=3;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 73, 0, input);
				throw nvae;
			}

			switch (alt73) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:639:7: ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key
					{
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:639:35: ( LEFT_SQUARE RIGHT_SQUARE )+
					int cnt71=0;
					loop71:
					while (true) {
						int alt71=2;
						int LA71_0 = input.LA(1);
						if ( (LA71_0==LEFT_SQUARE) ) {
							alt71=1;
						}

						switch (alt71) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:639:36: LEFT_SQUARE RIGHT_SQUARE
							{
							LEFT_SQUARE15=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_identifierSuffix3683); if (state.failed) return;
							if ( state.backtracking==0 ) { helper.emit(LEFT_SQUARE15, DroolsEditorType.SYMBOL); }
							RIGHT_SQUARE16=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_identifierSuffix3724); if (state.failed) return;
							if ( state.backtracking==0 ) { helper.emit(RIGHT_SQUARE16, DroolsEditorType.SYMBOL); }
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

					DOT17=(Token)match(input,DOT,FOLLOW_DOT_in_identifierSuffix3768); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(DOT17, DroolsEditorType.SYMBOL); }
					pushFollow(FOLLOW_class_key_in_identifierSuffix3772);
					class_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:642:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
					{
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:642:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
					int cnt72=0;
					loop72:
					while (true) {
						int alt72=2;
						int LA72_0 = input.LA(1);
						if ( (LA72_0==LEFT_SQUARE) ) {
							int LA72_37 = input.LA(2);
							if ( (synpred39_DRL6Expressions()) ) {
								alt72=1;
							}

						}

						switch (alt72) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:642:8: ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE
							{
							LEFT_SQUARE18=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_identifierSuffix3787); if (state.failed) return;
							if ( state.backtracking==0 ) { helper.emit(LEFT_SQUARE18, DroolsEditorType.SYMBOL); }
							pushFollow(FOLLOW_expression_in_identifierSuffix3817);
							expression();
							state._fsp--;
							if (state.failed) return;
							RIGHT_SQUARE19=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_identifierSuffix3845); if (state.failed) return;
							if ( state.backtracking==0 ) { helper.emit(RIGHT_SQUARE19, DroolsEditorType.SYMBOL); }
							}
							break;

						default :
							if ( cnt72 >= 1 ) break loop72;
							if (state.backtracking>0) {state.failed=true; return;}
							EarlyExitException eee = new EarlyExitException(72, input);
							throw eee;
						}
						cnt72++;
					}

					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:645:9: arguments
					{
					pushFollow(FOLLOW_arguments_in_identifierSuffix3861);
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:653:1: creator : ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) ;
	public final void creator() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:654:5: ( ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:654:7: ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest )
			{
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:654:7: ( nonWildcardTypeArguments )?
			int alt74=2;
			int LA74_0 = input.LA(1);
			if ( (LA74_0==LESS) ) {
				alt74=1;
			}
			switch (alt74) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:654:7: nonWildcardTypeArguments
					{
					pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator3883);
					nonWildcardTypeArguments();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			pushFollow(FOLLOW_createdName_in_creator3886);
			createdName();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:655:9: ( arrayCreatorRest | classCreatorRest )
			int alt75=2;
			int LA75_0 = input.LA(1);
			if ( (LA75_0==LEFT_SQUARE) ) {
				alt75=1;
			}
			else if ( (LA75_0==LEFT_PAREN) ) {
				alt75=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 75, 0, input);
				throw nvae;
			}

			switch (alt75) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:655:10: arrayCreatorRest
					{
					pushFollow(FOLLOW_arrayCreatorRest_in_creator3897);
					arrayCreatorRest();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:655:29: classCreatorRest
					{
					pushFollow(FOLLOW_classCreatorRest_in_creator3901);
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:658:1: createdName : ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType );
	public final void createdName() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:659:5: ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType )
			int alt79=2;
			int LA79_0 = input.LA(1);
			if ( (LA79_0==ID) ) {
				int LA79_1 = input.LA(2);
				if ( (!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))))))) ) {
					alt79=1;
				}
				else if ( ((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))))) ) {
					alt79=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 79, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 79, 0, input);
				throw nvae;
			}

			switch (alt79) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:659:7: ID ( typeArguments )? ( DOT ID ( typeArguments )? )*
					{
					match(input,ID,FOLLOW_ID_in_createdName3919); if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:659:10: ( typeArguments )?
					int alt76=2;
					int LA76_0 = input.LA(1);
					if ( (LA76_0==LESS) ) {
						alt76=1;
					}
					switch (alt76) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:659:10: typeArguments
							{
							pushFollow(FOLLOW_typeArguments_in_createdName3921);
							typeArguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:660:9: ( DOT ID ( typeArguments )? )*
					loop78:
					while (true) {
						int alt78=2;
						int LA78_0 = input.LA(1);
						if ( (LA78_0==DOT) ) {
							alt78=1;
						}

						switch (alt78) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:660:11: DOT ID ( typeArguments )?
							{
							match(input,DOT,FOLLOW_DOT_in_createdName3934); if (state.failed) return;
							match(input,ID,FOLLOW_ID_in_createdName3936); if (state.failed) return;
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:660:18: ( typeArguments )?
							int alt77=2;
							int LA77_0 = input.LA(1);
							if ( (LA77_0==LESS) ) {
								alt77=1;
							}
							switch (alt77) {
								case 1 :
									// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:660:18: typeArguments
									{
									pushFollow(FOLLOW_typeArguments_in_createdName3938);
									typeArguments();
									state._fsp--;
									if (state.failed) return;
									}
									break;

							}

							}
							break;

						default :
							break loop78;
						}
					}

					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:661:11: primitiveType
					{
					pushFollow(FOLLOW_primitiveType_in_createdName3953);
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:664:1: innerCreator :{...}? => ID classCreatorRest ;
	public final void innerCreator() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:665:5: ({...}? => ID classCreatorRest )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:665:7: {...}? => ID classCreatorRest
			{
			if ( !((!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "innerCreator", "!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
			}
			match(input,ID,FOLLOW_ID_in_innerCreator3973); if (state.failed) return;
			pushFollow(FOLLOW_classCreatorRest_in_innerCreator3975);
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:668:1: arrayCreatorRest : LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) ;
	public final void arrayCreatorRest() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:669:5: ( LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:669:9: LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
			{
			match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3994); if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:670:5: ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
			int alt83=2;
			int LA83_0 = input.LA(1);
			if ( (LA83_0==RIGHT_SQUARE) ) {
				alt83=1;
			}
			else if ( (LA83_0==BOOL||(LA83_0 >= DECIMAL && LA83_0 <= DIV)||LA83_0==DOT||LA83_0==FLOAT||LA83_0==HEX||(LA83_0 >= ID && LA83_0 <= INCR)||(LA83_0 >= LEFT_PAREN && LA83_0 <= LESS)||LA83_0==MINUS||LA83_0==NEGATION||LA83_0==NULL||LA83_0==PLUS||LA83_0==QUESTION_DIV||(LA83_0 >= STAR && LA83_0 <= TIME_INTERVAL)) ) {
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
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:670:9: RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer
					{
					match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest4004); if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:670:22: ( LEFT_SQUARE RIGHT_SQUARE )*
					loop80:
					while (true) {
						int alt80=2;
						int LA80_0 = input.LA(1);
						if ( (LA80_0==LEFT_SQUARE) ) {
							alt80=1;
						}

						switch (alt80) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:670:23: LEFT_SQUARE RIGHT_SQUARE
							{
							match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest4007); if (state.failed) return;
							match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest4009); if (state.failed) return;
							}
							break;

						default :
							break loop80;
						}
					}

					pushFollow(FOLLOW_arrayInitializer_in_arrayCreatorRest4013);
					arrayInitializer();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:671:13: expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
					{
					pushFollow(FOLLOW_expression_in_arrayCreatorRest4027);
					expression();
					state._fsp--;
					if (state.failed) return;
					match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest4029); if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:671:37: ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )*
					loop81:
					while (true) {
						int alt81=2;
						int LA81_0 = input.LA(1);
						if ( (LA81_0==LEFT_SQUARE) ) {
							int LA81_1 = input.LA(2);
							if ( ((!helper.validateLT(2,"]"))) ) {
								alt81=1;
							}

						}

						switch (alt81) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:671:38: {...}? => LEFT_SQUARE expression RIGHT_SQUARE
							{
							if ( !((!helper.validateLT(2,"]"))) ) {
								if (state.backtracking>0) {state.failed=true; return;}
								throw new FailedPredicateException(input, "arrayCreatorRest", "!helper.validateLT(2,\"]\")");
							}
							match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest4034); if (state.failed) return;
							pushFollow(FOLLOW_expression_in_arrayCreatorRest4036);
							expression();
							state._fsp--;
							if (state.failed) return;
							match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest4038); if (state.failed) return;
							}
							break;

						default :
							break loop81;
						}
					}

					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:671:106: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
					loop82:
					while (true) {
						int alt82=2;
						int LA82_0 = input.LA(1);
						if ( (LA82_0==LEFT_SQUARE) ) {
							int LA82_2 = input.LA(2);
							if ( (LA82_2==RIGHT_SQUARE) && (synpred40_DRL6Expressions())) {
								alt82=1;
							}

						}

						switch (alt82) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:671:107: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
							{
							match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest4050); if (state.failed) return;
							match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest4052); if (state.failed) return;
							}
							break;

						default :
							break loop82;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:675:1: variableInitializer : ( arrayInitializer | expression );
	public final void variableInitializer() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:676:5: ( arrayInitializer | expression )
			int alt84=2;
			int LA84_0 = input.LA(1);
			if ( (LA84_0==LEFT_CURLY) ) {
				alt84=1;
			}
			else if ( (LA84_0==BOOL||(LA84_0 >= DECIMAL && LA84_0 <= DIV)||LA84_0==DOT||LA84_0==FLOAT||LA84_0==HEX||(LA84_0 >= ID && LA84_0 <= INCR)||(LA84_0 >= LEFT_PAREN && LA84_0 <= LESS)||LA84_0==MINUS||LA84_0==NEGATION||LA84_0==NULL||LA84_0==PLUS||LA84_0==QUESTION_DIV||(LA84_0 >= STAR && LA84_0 <= TIME_INTERVAL)) ) {
				alt84=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 84, 0, input);
				throw nvae;
			}

			switch (alt84) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:676:7: arrayInitializer
					{
					pushFollow(FOLLOW_arrayInitializer_in_variableInitializer4081);
					arrayInitializer();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:677:13: expression
					{
					pushFollow(FOLLOW_expression_in_variableInitializer4095);
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:680:1: arrayInitializer : LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY ;
	public final void arrayInitializer() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:681:5: ( LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:681:7: LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY
			{
			match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_arrayInitializer4112); if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:681:18: ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )?
			int alt87=2;
			int LA87_0 = input.LA(1);
			if ( (LA87_0==BOOL||(LA87_0 >= DECIMAL && LA87_0 <= DIV)||LA87_0==DOT||LA87_0==FLOAT||LA87_0==HEX||(LA87_0 >= ID && LA87_0 <= INCR)||(LA87_0 >= LEFT_CURLY && LA87_0 <= LESS)||LA87_0==MINUS||LA87_0==NEGATION||LA87_0==NULL||LA87_0==PLUS||LA87_0==QUESTION_DIV||(LA87_0 >= STAR && LA87_0 <= TIME_INTERVAL)) ) {
				alt87=1;
			}
			switch (alt87) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:681:19: variableInitializer ( COMMA variableInitializer )* ( COMMA )?
					{
					pushFollow(FOLLOW_variableInitializer_in_arrayInitializer4115);
					variableInitializer();
					state._fsp--;
					if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:681:39: ( COMMA variableInitializer )*
					loop85:
					while (true) {
						int alt85=2;
						int LA85_0 = input.LA(1);
						if ( (LA85_0==COMMA) ) {
							int LA85_1 = input.LA(2);
							if ( (LA85_1==BOOL||(LA85_1 >= DECIMAL && LA85_1 <= DIV)||LA85_1==DOT||LA85_1==FLOAT||LA85_1==HEX||(LA85_1 >= ID && LA85_1 <= INCR)||(LA85_1 >= LEFT_CURLY && LA85_1 <= LESS)||LA85_1==MINUS||LA85_1==NEGATION||LA85_1==NULL||LA85_1==PLUS||LA85_1==QUESTION_DIV||(LA85_1 >= STAR && LA85_1 <= TIME_INTERVAL)) ) {
								alt85=1;
							}

						}

						switch (alt85) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:681:40: COMMA variableInitializer
							{
							match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer4118); if (state.failed) return;
							pushFollow(FOLLOW_variableInitializer_in_arrayInitializer4120);
							variableInitializer();
							state._fsp--;
							if (state.failed) return;
							}
							break;

						default :
							break loop85;
						}
					}

					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:681:68: ( COMMA )?
					int alt86=2;
					int LA86_0 = input.LA(1);
					if ( (LA86_0==COMMA) ) {
						alt86=1;
					}
					switch (alt86) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:681:69: COMMA
							{
							match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer4125); if (state.failed) return;
							}
							break;

					}

					}
					break;

			}

			match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_arrayInitializer4132); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:684:1: classCreatorRest : arguments ;
	public final void classCreatorRest() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:685:5: ( arguments )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:685:7: arguments
			{
			pushFollow(FOLLOW_arguments_in_classCreatorRest4149);
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:688:1: explicitGenericInvocation : nonWildcardTypeArguments arguments ;
	public final void explicitGenericInvocation() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:689:5: ( nonWildcardTypeArguments arguments )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:689:7: nonWildcardTypeArguments arguments
			{
			pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation4167);
			nonWildcardTypeArguments();
			state._fsp--;
			if (state.failed) return;
			pushFollow(FOLLOW_arguments_in_explicitGenericInvocation4169);
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:692:1: nonWildcardTypeArguments : LESS typeList GREATER ;
	public final void nonWildcardTypeArguments() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:693:5: ( LESS typeList GREATER )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:693:7: LESS typeList GREATER
			{
			match(input,LESS,FOLLOW_LESS_in_nonWildcardTypeArguments4186); if (state.failed) return;
			pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments4188);
			typeList();
			state._fsp--;
			if (state.failed) return;
			match(input,GREATER,FOLLOW_GREATER_in_nonWildcardTypeArguments4190); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:696:1: explicitGenericInvocationSuffix : ( super_key superSuffix | ID arguments );
	public final void explicitGenericInvocationSuffix() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:697:5: ( super_key superSuffix | ID arguments )
			int alt88=2;
			int LA88_0 = input.LA(1);
			if ( (LA88_0==ID) ) {
				int LA88_1 = input.LA(2);
				if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
					alt88=1;
				}
				else if ( (true) ) {
					alt88=2;
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 88, 0, input);
				throw nvae;
			}

			switch (alt88) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:697:7: super_key superSuffix
					{
					pushFollow(FOLLOW_super_key_in_explicitGenericInvocationSuffix4207);
					super_key();
					state._fsp--;
					if (state.failed) return;
					pushFollow(FOLLOW_superSuffix_in_explicitGenericInvocationSuffix4209);
					superSuffix();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:698:10: ID arguments
					{
					match(input,ID,FOLLOW_ID_in_explicitGenericInvocationSuffix4220); if (state.failed) return;
					pushFollow(FOLLOW_arguments_in_explicitGenericInvocationSuffix4222);
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:701:1: selector : ( ( DOT super_key )=> DOT super_key superSuffix | ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator | ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )? | ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT ID ( ( LEFT_PAREN )=> arguments )? | ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE );
	public final void selector() throws RecognitionException {
		Token DOT20=null;
		Token DOT21=null;
		Token DOT22=null;
		Token ID23=null;
		Token NULL_SAFE_DOT24=null;
		Token ID25=null;
		Token LEFT_SQUARE26=null;
		Token RIGHT_SQUARE27=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:702:5: ( ( DOT super_key )=> DOT super_key superSuffix | ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator | ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )? | ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT ID ( ( LEFT_PAREN )=> arguments )? | ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )
			int alt92=5;
			int LA92_0 = input.LA(1);
			if ( (LA92_0==DOT) ) {
				int LA92_1 = input.LA(2);
				if ( (synpred41_DRL6Expressions()) ) {
					alt92=1;
				}
				else if ( (synpred42_DRL6Expressions()) ) {
					alt92=2;
				}
				else if ( (synpred43_DRL6Expressions()) ) {
					alt92=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 92, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA92_0==NULL_SAFE_DOT) && (synpred45_DRL6Expressions())) {
				alt92=4;
			}
			else if ( (LA92_0==LEFT_SQUARE) && (synpred47_DRL6Expressions())) {
				alt92=5;
			}

			switch (alt92) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:702:9: ( DOT super_key )=> DOT super_key superSuffix
					{
					DOT20=(Token)match(input,DOT,FOLLOW_DOT_in_selector4247); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(DOT20, DroolsEditorType.SYMBOL); }
					pushFollow(FOLLOW_super_key_in_selector4251);
					super_key();
					state._fsp--;
					if (state.failed) return;
					pushFollow(FOLLOW_superSuffix_in_selector4253);
					superSuffix();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:703:9: ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator
					{
					DOT21=(Token)match(input,DOT,FOLLOW_DOT_in_selector4269); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(DOT21, DroolsEditorType.SYMBOL); }
					pushFollow(FOLLOW_new_key_in_selector4273);
					new_key();
					state._fsp--;
					if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:703:84: ( nonWildcardTypeArguments )?
					int alt89=2;
					int LA89_0 = input.LA(1);
					if ( (LA89_0==LESS) ) {
						alt89=1;
					}
					switch (alt89) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:703:85: nonWildcardTypeArguments
							{
							pushFollow(FOLLOW_nonWildcardTypeArguments_in_selector4276);
							nonWildcardTypeArguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					pushFollow(FOLLOW_innerCreator_in_selector4280);
					innerCreator();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:704:9: ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )?
					{
					DOT22=(Token)match(input,DOT,FOLLOW_DOT_in_selector4296); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(DOT22, DroolsEditorType.SYMBOL); }
					ID23=(Token)match(input,ID,FOLLOW_ID_in_selector4318); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(ID23, DroolsEditorType.IDENTIFIER); }
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:706:19: ( ( LEFT_PAREN )=> arguments )?
					int alt90=2;
					int LA90_0 = input.LA(1);
					if ( (LA90_0==LEFT_PAREN) ) {
						int LA90_1 = input.LA(2);
						if ( (synpred44_DRL6Expressions()) ) {
							alt90=1;
						}
					}
					switch (alt90) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:706:20: ( LEFT_PAREN )=> arguments
							{
							pushFollow(FOLLOW_arguments_in_selector4347);
							arguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:707:9: ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT ID ( ( LEFT_PAREN )=> arguments )?
					{
					NULL_SAFE_DOT24=(Token)match(input,NULL_SAFE_DOT,FOLLOW_NULL_SAFE_DOT_in_selector4365); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(NULL_SAFE_DOT24, DroolsEditorType.SYMBOL); }
					ID25=(Token)match(input,ID,FOLLOW_ID_in_selector4387); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(ID25, DroolsEditorType.IDENTIFIER); }
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:709:19: ( ( LEFT_PAREN )=> arguments )?
					int alt91=2;
					int LA91_0 = input.LA(1);
					if ( (LA91_0==LEFT_PAREN) ) {
						int LA91_1 = input.LA(2);
						if ( (synpred46_DRL6Expressions()) ) {
							alt91=1;
						}
					}
					switch (alt91) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:709:20: ( LEFT_PAREN )=> arguments
							{
							pushFollow(FOLLOW_arguments_in_selector4416);
							arguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:711:9: ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE
					{
					LEFT_SQUARE26=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_selector4437); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(LEFT_SQUARE26, DroolsEditorType.SYMBOL); }
					pushFollow(FOLLOW_expression_in_selector4464);
					expression();
					state._fsp--;
					if (state.failed) return;
					RIGHT_SQUARE27=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_selector4489); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(RIGHT_SQUARE27, DroolsEditorType.SYMBOL); }
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:716:1: superSuffix : ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? );
	public final void superSuffix() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:717:5: ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? )
			int alt94=2;
			int LA94_0 = input.LA(1);
			if ( (LA94_0==LEFT_PAREN) ) {
				alt94=1;
			}
			else if ( (LA94_0==DOT) ) {
				alt94=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 94, 0, input);
				throw nvae;
			}

			switch (alt94) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:717:7: arguments
					{
					pushFollow(FOLLOW_arguments_in_superSuffix4508);
					arguments();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:718:10: DOT ID ( ( LEFT_PAREN )=> arguments )?
					{
					match(input,DOT,FOLLOW_DOT_in_superSuffix4519); if (state.failed) return;
					match(input,ID,FOLLOW_ID_in_superSuffix4521); if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:718:17: ( ( LEFT_PAREN )=> arguments )?
					int alt93=2;
					int LA93_0 = input.LA(1);
					if ( (LA93_0==LEFT_PAREN) ) {
						int LA93_1 = input.LA(2);
						if ( (synpred48_DRL6Expressions()) ) {
							alt93=1;
						}
					}
					switch (alt93) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:718:18: ( LEFT_PAREN )=> arguments
							{
							pushFollow(FOLLOW_arguments_in_superSuffix4530);
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:721:1: squareArguments returns [java.util.List<String> args] : LEFT_SQUARE (el= expressionList )? RIGHT_SQUARE ;
	public final java.util.List<String> squareArguments() throws RecognitionException {
		java.util.List<String> args = null;


		java.util.List<String> el =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:722:5: ( LEFT_SQUARE (el= expressionList )? RIGHT_SQUARE )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:722:7: LEFT_SQUARE (el= expressionList )? RIGHT_SQUARE
			{
			match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_squareArguments4553); if (state.failed) return args;
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:722:19: (el= expressionList )?
			int alt95=2;
			int LA95_0 = input.LA(1);
			if ( (LA95_0==BOOL||(LA95_0 >= DECIMAL && LA95_0 <= DIV)||LA95_0==DOT||LA95_0==FLOAT||LA95_0==HEX||(LA95_0 >= ID && LA95_0 <= INCR)||(LA95_0 >= LEFT_PAREN && LA95_0 <= LESS)||LA95_0==MINUS||LA95_0==NEGATION||LA95_0==NULL||LA95_0==PLUS||LA95_0==QUESTION_DIV||(LA95_0 >= STAR && LA95_0 <= TIME_INTERVAL)) ) {
				alt95=1;
			}
			switch (alt95) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:722:20: el= expressionList
					{
					pushFollow(FOLLOW_expressionList_in_squareArguments4558);
					el=expressionList();
					state._fsp--;
					if (state.failed) return args;
					if ( state.backtracking==0 ) { args = el; }
					}
					break;

			}

			match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_squareArguments4564); if (state.failed) return args;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:725:1: arguments : LEFT_PAREN ( expressionList )? RIGHT_PAREN ;
	public final void arguments() throws RecognitionException {
		Token LEFT_PAREN28=null;
		Token RIGHT_PAREN29=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:726:5: ( LEFT_PAREN ( expressionList )? RIGHT_PAREN )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:726:7: LEFT_PAREN ( expressionList )? RIGHT_PAREN
			{
			LEFT_PAREN28=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_arguments4581); if (state.failed) return;
			if ( state.backtracking==0 ) { helper.emit(LEFT_PAREN28, DroolsEditorType.SYMBOL); }
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:727:9: ( expressionList )?
			int alt96=2;
			int LA96_0 = input.LA(1);
			if ( (LA96_0==BOOL||(LA96_0 >= DECIMAL && LA96_0 <= DIV)||LA96_0==DOT||LA96_0==FLOAT||LA96_0==HEX||(LA96_0 >= ID && LA96_0 <= INCR)||(LA96_0 >= LEFT_PAREN && LA96_0 <= LESS)||LA96_0==MINUS||LA96_0==NEGATION||LA96_0==NULL||LA96_0==PLUS||LA96_0==QUESTION_DIV||(LA96_0 >= STAR && LA96_0 <= TIME_INTERVAL)) ) {
				alt96=1;
			}
			switch (alt96) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:727:9: expressionList
					{
					pushFollow(FOLLOW_expressionList_in_arguments4593);
					expressionList();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			RIGHT_PAREN29=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_arguments4604); if (state.failed) return;
			if ( state.backtracking==0 ) { helper.emit(RIGHT_PAREN29, DroolsEditorType.SYMBOL); }
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:731:1: expressionList returns [java.util.List<String> exprs] : f= expression ( COMMA s= expression )* ;
	public final java.util.List<String> expressionList() throws RecognitionException {
		java.util.List<String> exprs = null;


		ParserRuleReturnScope f =null;
		ParserRuleReturnScope s =null;

		 exprs = new java.util.ArrayList<String>();
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:733:3: (f= expression ( COMMA s= expression )* )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:733:7: f= expression ( COMMA s= expression )*
			{
			pushFollow(FOLLOW_expression_in_expressionList4634);
			f=expression();
			state._fsp--;
			if (state.failed) return exprs;
			if ( state.backtracking==0 ) { exprs.add( (f!=null?input.toString(f.start,f.stop):null) ); }
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:734:7: ( COMMA s= expression )*
			loop97:
			while (true) {
				int alt97=2;
				int LA97_0 = input.LA(1);
				if ( (LA97_0==COMMA) ) {
					alt97=1;
				}

				switch (alt97) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:734:8: COMMA s= expression
					{
					match(input,COMMA,FOLLOW_COMMA_in_expressionList4645); if (state.failed) return exprs;
					pushFollow(FOLLOW_expression_in_expressionList4649);
					s=expression();
					state._fsp--;
					if (state.failed) return exprs;
					if ( state.backtracking==0 ) { exprs.add( (s!=null?input.toString(s.start,s.stop):null) ); }
					}
					break;

				default :
					break loop97;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:737:1: assignmentOperator : ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN );
	public final void assignmentOperator() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:738:5: ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN )
			int alt98=12;
			switch ( input.LA(1) ) {
			case EQUALS_ASSIGN:
				{
				alt98=1;
				}
				break;
			case PLUS_ASSIGN:
				{
				alt98=2;
				}
				break;
			case MINUS_ASSIGN:
				{
				alt98=3;
				}
				break;
			case MULT_ASSIGN:
				{
				alt98=4;
				}
				break;
			case DIV_ASSIGN:
				{
				alt98=5;
				}
				break;
			case AND_ASSIGN:
				{
				alt98=6;
				}
				break;
			case OR_ASSIGN:
				{
				alt98=7;
				}
				break;
			case XOR_ASSIGN:
				{
				alt98=8;
				}
				break;
			case MOD_ASSIGN:
				{
				alt98=9;
				}
				break;
			case LESS:
				{
				alt98=10;
				}
				break;
			case GREATER:
				{
				int LA98_11 = input.LA(2);
				if ( (LA98_11==GREATER) ) {
					int LA98_12 = input.LA(3);
					if ( (LA98_12==GREATER) && (synpred49_DRL6Expressions())) {
						alt98=11;
					}
					else if ( (LA98_12==EQUALS_ASSIGN) && (synpred50_DRL6Expressions())) {
						alt98=12;
					}

				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 98, 11, input);
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
					new NoViableAltException("", 98, 0, input);
				throw nvae;
			}
			switch (alt98) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:738:9: EQUALS_ASSIGN
					{
					match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4670); if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:739:7: PLUS_ASSIGN
					{
					match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_assignmentOperator4678); if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:740:7: MINUS_ASSIGN
					{
					match(input,MINUS_ASSIGN,FOLLOW_MINUS_ASSIGN_in_assignmentOperator4686); if (state.failed) return;
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:741:7: MULT_ASSIGN
					{
					match(input,MULT_ASSIGN,FOLLOW_MULT_ASSIGN_in_assignmentOperator4694); if (state.failed) return;
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:742:7: DIV_ASSIGN
					{
					match(input,DIV_ASSIGN,FOLLOW_DIV_ASSIGN_in_assignmentOperator4702); if (state.failed) return;
					}
					break;
				case 6 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:743:7: AND_ASSIGN
					{
					match(input,AND_ASSIGN,FOLLOW_AND_ASSIGN_in_assignmentOperator4710); if (state.failed) return;
					}
					break;
				case 7 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:744:7: OR_ASSIGN
					{
					match(input,OR_ASSIGN,FOLLOW_OR_ASSIGN_in_assignmentOperator4718); if (state.failed) return;
					}
					break;
				case 8 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:745:7: XOR_ASSIGN
					{
					match(input,XOR_ASSIGN,FOLLOW_XOR_ASSIGN_in_assignmentOperator4726); if (state.failed) return;
					}
					break;
				case 9 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:746:7: MOD_ASSIGN
					{
					match(input,MOD_ASSIGN,FOLLOW_MOD_ASSIGN_in_assignmentOperator4734); if (state.failed) return;
					}
					break;
				case 10 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:747:7: LESS LESS EQUALS_ASSIGN
					{
					match(input,LESS,FOLLOW_LESS_in_assignmentOperator4742); if (state.failed) return;
					match(input,LESS,FOLLOW_LESS_in_assignmentOperator4744); if (state.failed) return;
					match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4746); if (state.failed) return;
					}
					break;
				case 11 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:748:7: ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN
					{
					match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4763); if (state.failed) return;
					match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4765); if (state.failed) return;
					match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4767); if (state.failed) return;
					match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4769); if (state.failed) return;
					}
					break;
				case 12 :
					// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:749:7: ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN
					{
					match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4784); if (state.failed) return;
					match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4786); if (state.failed) return;
					match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4788); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:755:1: extends_key :{...}? =>id= ID ;
	public final void extends_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:756:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:756:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "extends_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_extends_key4818); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:759:1: super_key :{...}? =>id= ID ;
	public final void super_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:760:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:760:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "super_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_super_key4847); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:763:1: instanceof_key :{...}? =>id= ID ;
	public final DRL6Expressions.instanceof_key_return instanceof_key() throws RecognitionException {
		DRL6Expressions.instanceof_key_return retval = new DRL6Expressions.instanceof_key_return();
		retval.start = input.LT(1);

		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:764:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:764:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
				if (state.backtracking>0) {state.failed=true; return retval;}
				throw new FailedPredicateException(input, "instanceof_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_instanceof_key4876); if (state.failed) return retval;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:767:1: boolean_key :{...}? =>id= ID ;
	public final void boolean_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:768:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:768:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "boolean_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_boolean_key4905); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:771:1: char_key :{...}? =>id= ID ;
	public final void char_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:772:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:772:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "char_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_char_key4934); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:775:1: byte_key :{...}? =>id= ID ;
	public final void byte_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:776:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:776:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "byte_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_byte_key4963); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:779:1: short_key :{...}? =>id= ID ;
	public final void short_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:780:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:780:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "short_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_short_key4992); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:783:1: int_key :{...}? =>id= ID ;
	public final void int_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:784:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:784:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "int_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INT))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_int_key5021); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:787:1: float_key :{...}? =>id= ID ;
	public final void float_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:788:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:788:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "float_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_float_key5050); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:791:1: long_key :{...}? =>id= ID ;
	public final void long_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:792:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:792:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "long_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.LONG))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_long_key5079); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:795:1: double_key :{...}? =>id= ID ;
	public final void double_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:796:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:796:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "double_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_double_key5108); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:799:1: void_key :{...}? =>id= ID ;
	public final void void_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:800:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:800:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.VOID)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "void_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.VOID))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_void_key5137); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:803:1: this_key :{...}? =>id= ID ;
	public final void this_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:804:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:804:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "this_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.THIS))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_this_key5166); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:807:1: class_key :{...}? =>id= ID ;
	public final void class_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:808:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:808:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.CLASS)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "class_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.CLASS))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_class_key5195); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:811:1: new_key :{...}? =>id= ID ;
	public final void new_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:812:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:812:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.NEW)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "new_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.NEW))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_new_key5225); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:815:1: not_key :{...}? =>id= ID ;
	public final void not_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:816:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:816:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "not_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.NOT))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_not_key5254); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:819:1: in_key :{...}? =>id= ID ;
	public final void in_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:820:3: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:820:10: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "in_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.IN))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_in_key5281); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:823:1: operator_key :{...}? =>id= ID ;
	public final DRL6Expressions.operator_key_return operator_key() throws RecognitionException {
		DRL6Expressions.operator_key_return retval = new DRL6Expressions.operator_key_return();
		retval.start = input.LT(1);

		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:824:3: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:824:10: {...}? =>id= ID
			{
			if ( !(((helper.isPluggableEvaluator(false)))) ) {
				if (state.backtracking>0) {state.failed=true; return retval;}
				throw new FailedPredicateException(input, "operator_key", "(helper.isPluggableEvaluator(false))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_operator_key5306); if (state.failed) return retval;
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
	// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:827:1: neg_operator_key :{...}? =>id= ID ;
	public final DRL6Expressions.neg_operator_key_return neg_operator_key() throws RecognitionException {
		DRL6Expressions.neg_operator_key_return retval = new DRL6Expressions.neg_operator_key_return();
		retval.start = input.LT(1);

		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:828:3: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:828:10: {...}? =>id= ID
			{
			if ( !(((helper.isPluggableEvaluator(true)))) ) {
				if (state.backtracking>0) {state.failed=true; return retval;}
				throw new FailedPredicateException(input, "neg_operator_key", "(helper.isPluggableEvaluator(true))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_neg_operator_key5331); if (state.failed) return retval;
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
		int alt99=2;
		int LA99_0 = input.LA(1);
		if ( (LA99_0==EQUALS||(LA99_0 >= GREATER && LA99_0 <= GREATER_EQUALS)||(LA99_0 >= LESS && LA99_0 <= LESS_EQUALS)||LA99_0==NOT_EQUALS||LA99_0==TILDE) ) {
			alt99=1;
		}
		else if ( (LA99_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) {
			alt99=1;
		}
		else if ( (LA99_0==LEFT_PAREN) ) {
			alt99=2;
		}

		else {
			if (state.backtracking>0) {state.failed=true; return;}
			NoViableAltException nvae =
				new NoViableAltException("", 99, 0, input);
			throw nvae;
		}

		switch (alt99) {
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
		int alt100=2;
		int LA100_0 = input.LA(1);
		if ( (LA100_0==AT) ) {
			alt100=1;
		}
		switch (alt100) {
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
		int alt101=2;
		int LA101_0 = input.LA(1);
		if ( (LA101_0==AT) ) {
			alt101=1;
		}
		switch (alt101) {
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
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:509:9: ( castExpression )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:509:10: castExpression
		{
		pushFollow(FOLLOW_castExpression_in_synpred15_DRL6Expressions2489);
		castExpression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred15_DRL6Expressions

	// $ANTLR start synpred16_DRL6Expressions
	public final void synpred16_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:510:9: ( backReferenceExpression )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:510:10: backReferenceExpression
		{
		pushFollow(FOLLOW_backReferenceExpression_in_synpred16_DRL6Expressions2503);
		backReferenceExpression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred16_DRL6Expressions

	// $ANTLR start synpred17_DRL6Expressions
	public final void synpred17_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:518:11: ( xpathSeparator ID )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:518:12: xpathSeparator ID
		{
		pushFollow(FOLLOW_xpathSeparator_in_synpred17_DRL6Expressions2624);
		xpathSeparator();
		state._fsp--;
		if (state.failed) return;
		match(input,ID,FOLLOW_ID_in_synpred17_DRL6Expressions2626); if (state.failed) return;
		}

	}
	// $ANTLR end synpred17_DRL6Expressions

	// $ANTLR start synpred18_DRL6Expressions
	public final void synpred18_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:522:10: ( selector )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:522:11: selector
		{
		pushFollow(FOLLOW_selector_in_synpred18_DRL6Expressions2674);
		selector();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred18_DRL6Expressions

	// $ANTLR start synpred19_DRL6Expressions
	public final void synpred19_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:541:10: ( INCR | DECR )
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
	// $ANTLR end synpred19_DRL6Expressions

	// $ANTLR start synpred20_DRL6Expressions
	public final void synpred20_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:545:8: ( LEFT_PAREN primitiveType )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:545:9: LEFT_PAREN primitiveType
		{
		match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred20_DRL6Expressions2732); if (state.failed) return;
		pushFollow(FOLLOW_primitiveType_in_synpred20_DRL6Expressions2734);
		primitiveType();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred20_DRL6Expressions

	// $ANTLR start synpred21_DRL6Expressions
	public final void synpred21_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:546:8: ( LEFT_PAREN type )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:546:9: LEFT_PAREN type
		{
		match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred21_DRL6Expressions2757); if (state.failed) return;
		pushFollow(FOLLOW_type_in_synpred21_DRL6Expressions2759);
		type();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred21_DRL6Expressions

	// $ANTLR start synpred24_DRL6Expressions
	public final void synpred24_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:584:7: ( LEFT_PAREN )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:584:8: LEFT_PAREN
		{
		match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred24_DRL6Expressions3051); if (state.failed) return;
		}

	}
	// $ANTLR end synpred24_DRL6Expressions

	// $ANTLR start synpred25_DRL6Expressions
	public final void synpred25_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:585:9: ( nonWildcardTypeArguments )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:585:10: nonWildcardTypeArguments
		{
		pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred25_DRL6Expressions3070);
		nonWildcardTypeArguments();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred25_DRL6Expressions

	// $ANTLR start synpred26_DRL6Expressions
	public final void synpred26_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:586:9: ( literal )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:586:10: literal
		{
		pushFollow(FOLLOW_literal_in_synpred26_DRL6Expressions3095);
		literal();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred26_DRL6Expressions

	// $ANTLR start synpred27_DRL6Expressions
	public final void synpred27_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:588:9: ( super_key )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:588:10: super_key
		{
		pushFollow(FOLLOW_super_key_in_synpred27_DRL6Expressions3117);
		super_key();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred27_DRL6Expressions

	// $ANTLR start synpred28_DRL6Expressions
	public final void synpred28_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:589:9: ( new_key )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:589:10: new_key
		{
		pushFollow(FOLLOW_new_key_in_synpred28_DRL6Expressions3134);
		new_key();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred28_DRL6Expressions

	// $ANTLR start synpred29_DRL6Expressions
	public final void synpred29_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:590:9: ( primitiveType )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:590:10: primitiveType
		{
		pushFollow(FOLLOW_primitiveType_in_synpred29_DRL6Expressions3151);
		primitiveType();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred29_DRL6Expressions

	// $ANTLR start synpred30_DRL6Expressions
	public final void synpred30_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:592:9: ( inlineMapExpression )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:592:10: inlineMapExpression
		{
		pushFollow(FOLLOW_inlineMapExpression_in_synpred30_DRL6Expressions3182);
		inlineMapExpression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred30_DRL6Expressions

	// $ANTLR start synpred31_DRL6Expressions
	public final void synpred31_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:593:9: ( inlineListExpression )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:593:10: inlineListExpression
		{
		pushFollow(FOLLOW_inlineListExpression_in_synpred31_DRL6Expressions3197);
		inlineListExpression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred31_DRL6Expressions

	// $ANTLR start synpred32_DRL6Expressions
	public final void synpred32_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:594:9: ( ID )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:594:10: ID
		{
		match(input,ID,FOLLOW_ID_in_synpred32_DRL6Expressions3212); if (state.failed) return;
		}

	}
	// $ANTLR end synpred32_DRL6Expressions

	// $ANTLR start synpred33_DRL6Expressions
	public final void synpred33_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:596:15: ( DOT ID )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:596:16: DOT ID
		{
		match(input,DOT,FOLLOW_DOT_in_synpred33_DRL6Expressions3246); if (state.failed) return;
		match(input,ID,FOLLOW_ID_in_synpred33_DRL6Expressions3248); if (state.failed) return;
		}

	}
	// $ANTLR end synpred33_DRL6Expressions

	// $ANTLR start synpred34_DRL6Expressions
	public final void synpred34_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:598:15: ( ( DOT | NULL_SAFE_DOT ) LEFT_PAREN )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:598:16: ( DOT | NULL_SAFE_DOT ) LEFT_PAREN
		{
		if ( input.LA(1)==DOT||input.LA(1)==NULL_SAFE_DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred34_DRL6Expressions3298); if (state.failed) return;
		}

	}
	// $ANTLR end synpred34_DRL6Expressions

	// $ANTLR start synpred35_DRL6Expressions
	public final void synpred35_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:603:15: ( HASH ID )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:603:16: HASH ID
		{
		match(input,HASH,FOLLOW_HASH_in_synpred35_DRL6Expressions3443); if (state.failed) return;
		match(input,ID,FOLLOW_ID_in_synpred35_DRL6Expressions3445); if (state.failed) return;
		}

	}
	// $ANTLR end synpred35_DRL6Expressions

	// $ANTLR start synpred36_DRL6Expressions
	public final void synpred36_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:605:15: ( NULL_SAFE_DOT ID )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:605:16: NULL_SAFE_DOT ID
		{
		match(input,NULL_SAFE_DOT,FOLLOW_NULL_SAFE_DOT_in_synpred36_DRL6Expressions3489); if (state.failed) return;
		match(input,ID,FOLLOW_ID_in_synpred36_DRL6Expressions3491); if (state.failed) return;
		}

	}
	// $ANTLR end synpred36_DRL6Expressions

	// $ANTLR start synpred37_DRL6Expressions
	public final void synpred37_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:606:13: ( identifierSuffix )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:606:14: identifierSuffix
		{
		pushFollow(FOLLOW_identifierSuffix_in_synpred37_DRL6Expressions3519);
		identifierSuffix();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred37_DRL6Expressions

	// $ANTLR start synpred38_DRL6Expressions
	public final void synpred38_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:639:7: ( LEFT_SQUARE RIGHT_SQUARE )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:639:8: LEFT_SQUARE RIGHT_SQUARE
		{
		match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred38_DRL6Expressions3677); if (state.failed) return;
		match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred38_DRL6Expressions3679); if (state.failed) return;
		}

	}
	// $ANTLR end synpred38_DRL6Expressions

	// $ANTLR start synpred39_DRL6Expressions
	public final void synpred39_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:642:8: ( LEFT_SQUARE )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:642:9: LEFT_SQUARE
		{
		match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred39_DRL6Expressions3782); if (state.failed) return;
		}

	}
	// $ANTLR end synpred39_DRL6Expressions

	// $ANTLR start synpred40_DRL6Expressions
	public final void synpred40_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:671:107: ( LEFT_SQUARE RIGHT_SQUARE )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:671:108: LEFT_SQUARE RIGHT_SQUARE
		{
		match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred40_DRL6Expressions4044); if (state.failed) return;
		match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred40_DRL6Expressions4046); if (state.failed) return;
		}

	}
	// $ANTLR end synpred40_DRL6Expressions

	// $ANTLR start synpred41_DRL6Expressions
	public final void synpred41_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:702:9: ( DOT super_key )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:702:10: DOT super_key
		{
		match(input,DOT,FOLLOW_DOT_in_synpred41_DRL6Expressions4242); if (state.failed) return;
		pushFollow(FOLLOW_super_key_in_synpred41_DRL6Expressions4244);
		super_key();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred41_DRL6Expressions

	// $ANTLR start synpred42_DRL6Expressions
	public final void synpred42_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:703:9: ( DOT new_key )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:703:10: DOT new_key
		{
		match(input,DOT,FOLLOW_DOT_in_synpred42_DRL6Expressions4264); if (state.failed) return;
		pushFollow(FOLLOW_new_key_in_synpred42_DRL6Expressions4266);
		new_key();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred42_DRL6Expressions

	// $ANTLR start synpred43_DRL6Expressions
	public final void synpred43_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:704:9: ( DOT ID )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:704:10: DOT ID
		{
		match(input,DOT,FOLLOW_DOT_in_synpred43_DRL6Expressions4291); if (state.failed) return;
		match(input,ID,FOLLOW_ID_in_synpred43_DRL6Expressions4293); if (state.failed) return;
		}

	}
	// $ANTLR end synpred43_DRL6Expressions

	// $ANTLR start synpred44_DRL6Expressions
	public final void synpred44_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:706:20: ( LEFT_PAREN )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:706:21: LEFT_PAREN
		{
		match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred44_DRL6Expressions4342); if (state.failed) return;
		}

	}
	// $ANTLR end synpred44_DRL6Expressions

	// $ANTLR start synpred45_DRL6Expressions
	public final void synpred45_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:707:9: ( NULL_SAFE_DOT ID )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:707:10: NULL_SAFE_DOT ID
		{
		match(input,NULL_SAFE_DOT,FOLLOW_NULL_SAFE_DOT_in_synpred45_DRL6Expressions4360); if (state.failed) return;
		match(input,ID,FOLLOW_ID_in_synpred45_DRL6Expressions4362); if (state.failed) return;
		}

	}
	// $ANTLR end synpred45_DRL6Expressions

	// $ANTLR start synpred46_DRL6Expressions
	public final void synpred46_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:709:20: ( LEFT_PAREN )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:709:21: LEFT_PAREN
		{
		match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred46_DRL6Expressions4411); if (state.failed) return;
		}

	}
	// $ANTLR end synpred46_DRL6Expressions

	// $ANTLR start synpred47_DRL6Expressions
	public final void synpred47_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:711:9: ( LEFT_SQUARE )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:711:10: LEFT_SQUARE
		{
		match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred47_DRL6Expressions4434); if (state.failed) return;
		}

	}
	// $ANTLR end synpred47_DRL6Expressions

	// $ANTLR start synpred48_DRL6Expressions
	public final void synpred48_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:718:18: ( LEFT_PAREN )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:718:19: LEFT_PAREN
		{
		match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred48_DRL6Expressions4525); if (state.failed) return;
		}

	}
	// $ANTLR end synpred48_DRL6Expressions

	// $ANTLR start synpred49_DRL6Expressions
	public final void synpred49_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:748:7: ( GREATER GREATER GREATER )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:748:8: GREATER GREATER GREATER
		{
		match(input,GREATER,FOLLOW_GREATER_in_synpred49_DRL6Expressions4755); if (state.failed) return;
		match(input,GREATER,FOLLOW_GREATER_in_synpred49_DRL6Expressions4757); if (state.failed) return;
		match(input,GREATER,FOLLOW_GREATER_in_synpred49_DRL6Expressions4759); if (state.failed) return;
		}

	}
	// $ANTLR end synpred49_DRL6Expressions

	// $ANTLR start synpred50_DRL6Expressions
	public final void synpred50_DRL6Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:749:7: ( GREATER GREATER )
		// src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:749:8: GREATER GREATER
		{
		match(input,GREATER,FOLLOW_GREATER_in_synpred50_DRL6Expressions4778); if (state.failed) return;
		match(input,GREATER,FOLLOW_GREATER_in_synpred50_DRL6Expressions4780); if (state.failed) return;
		}

	}
	// $ANTLR end synpred50_DRL6Expressions

	// Delegated rules

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
	public final boolean synpred50_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred50_DRL6Expressions_fragment(); // can never throw exception
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
	public final boolean synpred21_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred21_DRL6Expressions_fragment(); // can never throw exception
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
	public final boolean synpred49_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred49_DRL6Expressions_fragment(); // can never throw exception
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
	public final boolean synpred48_DRL6Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred48_DRL6Expressions_fragment(); // can never throw exception
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



	public static final BitSet FOLLOW_STRING_in_literal92 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DECIMAL_in_literal109 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_HEX_in_literal125 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FLOAT_in_literal145 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_BOOL_in_literal163 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NULL_in_literal182 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TIME_INTERVAL_in_literal203 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_STAR_in_literal215 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TILDE_in_operator256 = new BitSet(new long[]{0x000201808C100000L,0x0000000000000004L});
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
	public static final BitSet FOLLOW_LEFT_SQUARE_in_typeMatch565 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_typeMatch567 = new BitSet(new long[]{0x0000004000000002L});
	public static final BitSet FOLLOW_ID_in_typeMatch581 = new BitSet(new long[]{0x000000C000010002L});
	public static final BitSet FOLLOW_typeArguments_in_typeMatch588 = new BitSet(new long[]{0x0000004000010002L});
	public static final BitSet FOLLOW_DOT_in_typeMatch593 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_typeMatch595 = new BitSet(new long[]{0x000000C000010002L});
	public static final BitSet FOLLOW_typeArguments_in_typeMatch602 = new BitSet(new long[]{0x0000004000010002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_typeMatch617 = new BitSet(new long[]{0x2000000000000000L});
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
	public static final BitSet FOLLOW_expression_in_dummy711 = new BitSet(new long[]{0x5000000080000080L});
	public static final BitSet FOLLOW_set_in_dummy713 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_relationalExpression_in_dummy2747 = new BitSet(new long[]{0x0000000000000000L});
	public static final BitSet FOLLOW_EOF_in_dummy2749 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_conditionalExpression_in_expression768 = new BitSet(new long[]{0x0110A48004208022L,0x0000000000000100L});
	public static final BitSet FOLLOW_assignmentOperator_in_expression789 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_expression_in_expression793 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression820 = new BitSet(new long[]{0x0200000000000002L});
	public static final BitSet FOLLOW_ternaryExpression_in_conditionalExpression832 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_QUESTION_in_ternaryExpression854 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_expression_in_ternaryExpression858 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_COLON_in_ternaryExpression860 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_expression_in_ternaryExpression864 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AT_in_fullAnnotation894 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_fullAnnotation898 = new BitSet(new long[]{0x0000002000010000L});
	public static final BitSet FOLLOW_DOT_in_fullAnnotation904 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_fullAnnotation908 = new BitSet(new long[]{0x0000002000010000L});
	public static final BitSet FOLLOW_annotationArgs_in_fullAnnotation929 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_annotationArgs945 = new BitSet(new long[]{0x148502F1A1017180L,0x000000000000000FL});
	public static final BitSet FOLLOW_annotationElementValuePairs_in_annotationArgs968 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_annotationValue_in_annotationArgs982 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_annotationArgs998 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotationElementValuePair_in_annotationElementValuePairs1013 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_COMMA_in_annotationElementValuePairs1018 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_annotationElementValuePair_in_annotationElementValuePairs1020 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_ID_in_annotationElementValuePair1041 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_EQUALS_ASSIGN_in_annotationElementValuePair1043 = new BitSet(new long[]{0x048502F1A1017180L,0x000000000000000FL});
	public static final BitSet FOLLOW_annotationValue_in_annotationElementValuePair1047 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_annotationValue1070 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotationArray_in_annotationValue1082 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fullAnnotation_in_annotationValue1095 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_CURLY_in_annotationArray1122 = new BitSet(new long[]{0x0C8502F1A1017180L,0x000000000000000FL});
	public static final BitSet FOLLOW_annotationValue_in_annotationArray1128 = new BitSet(new long[]{0x0800000000000400L});
	public static final BitSet FOLLOW_COMMA_in_annotationArray1151 = new BitSet(new long[]{0x048502F1A1017180L,0x000000000000000FL});
	public static final BitSet FOLLOW_annotationValue_in_annotationArray1155 = new BitSet(new long[]{0x0800000000000400L});
	public static final BitSet FOLLOW_RIGHT_CURLY_in_annotationArray1171 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression1192 = new BitSet(new long[]{0x0000000000040002L});
	public static final BitSet FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression1201 = new BitSet(new long[]{0x048502E1A1017180L,0x000000000000000FL});
	public static final BitSet FOLLOW_fullAnnotation_in_conditionalOrExpression1223 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression1229 = new BitSet(new long[]{0x0000000000040002L});
	public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression1264 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression1272 = new BitSet(new long[]{0x048502E1A1017180L,0x000000000000000FL});
	public static final BitSet FOLLOW_fullAnnotation_in_conditionalAndExpression1295 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression1301 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression1336 = new BitSet(new long[]{0x0040000000000002L});
	public static final BitSet FOLLOW_PIPE_in_inclusiveOrExpression1344 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression1348 = new BitSet(new long[]{0x0040000000000002L});
	public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression1383 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
	public static final BitSet FOLLOW_XOR_in_exclusiveOrExpression1391 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression1395 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
	public static final BitSet FOLLOW_equalityExpression_in_andExpression1430 = new BitSet(new long[]{0x0000000000000012L});
	public static final BitSet FOLLOW_AMPER_in_andExpression1438 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_equalityExpression_in_andExpression1442 = new BitSet(new long[]{0x0000000000000012L});
	public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression1477 = new BitSet(new long[]{0x0002000000100002L});
	public static final BitSet FOLLOW_EQUALS_in_equalityExpression1489 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_NOT_EQUALS_in_equalityExpression1495 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression1511 = new BitSet(new long[]{0x0002000000100002L});
	public static final BitSet FOLLOW_inExpression_in_instanceOfExpression1546 = new BitSet(new long[]{0x0000000080000002L});
	public static final BitSet FOLLOW_instanceof_key_in_instanceOfExpression1556 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_type_in_instanceOfExpression1570 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_relationalExpression_in_inExpression1615 = new BitSet(new long[]{0x0000000080000002L});
	public static final BitSet FOLLOW_not_key_in_inExpression1635 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_in_key_in_inExpression1639 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_inExpression1641 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_expression_in_inExpression1663 = new BitSet(new long[]{0x1000000000000400L});
	public static final BitSet FOLLOW_COMMA_in_inExpression1682 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_expression_in_inExpression1686 = new BitSet(new long[]{0x1000000000000400L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_inExpression1707 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_in_key_in_inExpression1723 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_inExpression1725 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_expression_in_inExpression1747 = new BitSet(new long[]{0x1000000000000400L});
	public static final BitSet FOLLOW_COMMA_in_inExpression1766 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_expression_in_inExpression1770 = new BitSet(new long[]{0x1000000000000400L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_inExpression1791 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_shiftExpression_in_relationalExpression1832 = new BitSet(new long[]{0x000201A08C100002L,0x0000000000000004L});
	public static final BitSet FOLLOW_orRestriction_in_relationalExpression1857 = new BitSet(new long[]{0x000201A08C100002L,0x0000000000000004L});
	public static final BitSet FOLLOW_andRestriction_in_orRestriction1892 = new BitSet(new long[]{0x0000000000040002L});
	public static final BitSet FOLLOW_DOUBLE_PIPE_in_orRestriction1914 = new BitSet(new long[]{0x000201A08C100080L,0x0000000000000004L});
	public static final BitSet FOLLOW_fullAnnotation_in_orRestriction1918 = new BitSet(new long[]{0x000201A08C100000L,0x0000000000000004L});
	public static final BitSet FOLLOW_andRestriction_in_orRestriction1924 = new BitSet(new long[]{0x0000000000040002L});
	public static final BitSet FOLLOW_EOF_in_orRestriction1943 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_singleRestriction_in_andRestriction1963 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_DOUBLE_AMPER_in_andRestriction1983 = new BitSet(new long[]{0x000201A08C100080L,0x0000000000000004L});
	public static final BitSet FOLLOW_fullAnnotation_in_andRestriction2004 = new BitSet(new long[]{0x000201A08C100000L,0x0000000000000004L});
	public static final BitSet FOLLOW_singleRestriction_in_andRestriction2009 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_operator_in_singleRestriction2045 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_squareArguments_in_singleRestriction2074 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_shiftExpression_in_singleRestriction2078 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_shiftExpression_in_singleRestriction2091 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_singleRestriction2116 = new BitSet(new long[]{0x000201A08C100000L,0x0000000000000004L});
	public static final BitSet FOLLOW_orRestriction_in_singleRestriction2120 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_singleRestriction2122 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_additiveExpression_in_shiftExpression2146 = new BitSet(new long[]{0x0000008004000002L});
	public static final BitSet FOLLOW_shiftOp_in_shiftExpression2160 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_additiveExpression_in_shiftExpression2162 = new BitSet(new long[]{0x0000008004000002L});
	public static final BitSet FOLLOW_LESS_in_shiftOp2182 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_LESS_in_shiftOp2184 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GREATER_in_shiftOp2196 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_shiftOp2198 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_shiftOp2200 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GREATER_in_shiftOp2212 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_shiftOp2214 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression2242 = new BitSet(new long[]{0x0080020000000002L});
	public static final BitSet FOLLOW_set_in_additiveExpression2263 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression2271 = new BitSet(new long[]{0x0080020000000002L});
	public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression2299 = new BitSet(new long[]{0x0000100000004002L,0x0000000000000001L});
	public static final BitSet FOLLOW_set_in_multiplicativeExpression2311 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression2325 = new BitSet(new long[]{0x0000100000004002L,0x0000000000000001L});
	public static final BitSet FOLLOW_PLUS_in_unaryExpression2351 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpression2355 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MINUS_in_unaryExpression2373 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpression2377 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_INCR_in_unaryExpression2397 = new BitSet(new long[]{0x000400E0A1001100L,0x000000000000000BL});
	public static final BitSet FOLLOW_primary_in_unaryExpression2399 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DECR_in_unaryExpression2409 = new BitSet(new long[]{0x000400E0A1001100L,0x000000000000000BL});
	public static final BitSet FOLLOW_primary_in_unaryExpression2411 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression2423 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TILDE_in_unaryExpressionNotPlusMinus2453 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2455 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus2464 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2468 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus2492 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_backReferenceExpression_in_unaryExpressionNotPlusMinus2506 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_unaryExpressionNotPlusMinus2534 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_COLON_in_unaryExpressionNotPlusMinus2536 = new BitSet(new long[]{0x040400E0A1005100L,0x000000000000000BL});
	public static final BitSet FOLLOW_ID_in_unaryExpressionNotPlusMinus2575 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
	public static final BitSet FOLLOW_UNIFY_in_unaryExpressionNotPlusMinus2577 = new BitSet(new long[]{0x040400E0A1005100L,0x000000000000000BL});
	public static final BitSet FOLLOW_xpathPrimary_in_unaryExpressionNotPlusMinus2631 = new BitSet(new long[]{0x0008004100012002L});
	public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus2649 = new BitSet(new long[]{0x0008004100012002L});
	public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus2677 = new BitSet(new long[]{0x0008004100012002L});
	public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus2707 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression2739 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_primitiveType_in_castExpression2741 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression2743 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_unaryExpression_in_castExpression2747 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression2764 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_type_in_castExpression2766 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression2768 = new BitSet(new long[]{0x040500E0A1015100L,0x000000000000000FL});
	public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression2770 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_backReferenceExpression2799 = new BitSet(new long[]{0x0000000000010000L});
	public static final BitSet FOLLOW_DOT_in_backReferenceExpression2801 = new BitSet(new long[]{0x0000000000004000L});
	public static final BitSet FOLLOW_DIV_in_backReferenceExpression2803 = new BitSet(new long[]{0x040500E0A1015100L,0x000000000000000FL});
	public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_backReferenceExpression2807 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_boolean_key_in_primitiveType2826 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_char_key_in_primitiveType2834 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_byte_key_in_primitiveType2842 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_short_key_in_primitiveType2850 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_int_key_in_primitiveType2858 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_long_key_in_primitiveType2866 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_float_key_in_primitiveType2874 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_double_key_in_primitiveType2882 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_xpathChunk_in_xpathPrimary2930 = new BitSet(new long[]{0x0400000000004002L});
	public static final BitSet FOLLOW_xpathSeparator_in_xpathChunk2959 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_xpathChunk2961 = new BitSet(new long[]{0x0000004010010002L});
	public static final BitSet FOLLOW_DOT_in_xpathChunk2964 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_xpathChunk2966 = new BitSet(new long[]{0x0000004010010002L});
	public static final BitSet FOLLOW_HASH_in_xpathChunk2971 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_xpathChunk2973 = new BitSet(new long[]{0x0000004000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_xpathChunk2978 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_xpathExpressionList_in_xpathChunk2980 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_xpathChunk2982 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_xpathExpressionList3012 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_COMMA_in_xpathExpressionList3023 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_expression_in_xpathExpressionList3027 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_parExpression_in_primary3057 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_primary3074 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_explicitGenericInvocationSuffix_in_primary3077 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_this_key_in_primary3081 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_arguments_in_primary3083 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literal_in_primary3099 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_super_key_in_primary3121 = new BitSet(new long[]{0x0000002000010000L});
	public static final BitSet FOLLOW_superSuffix_in_primary3123 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_new_key_in_primary3138 = new BitSet(new long[]{0x0000008080000000L});
	public static final BitSet FOLLOW_creator_in_primary3140 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_primitiveType_in_primary3155 = new BitSet(new long[]{0x0000004000010000L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_primary3158 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_primary3160 = new BitSet(new long[]{0x0000004000010000L});
	public static final BitSet FOLLOW_DOT_in_primary3164 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_class_key_in_primary3166 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_inlineMapExpression_in_primary3186 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_inlineListExpression_in_primary3201 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_primary3217 = new BitSet(new long[]{0x0008006010010002L});
	public static final BitSet FOLLOW_DOT_in_primary3253 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_primary3257 = new BitSet(new long[]{0x0008006010010002L});
	public static final BitSet FOLLOW_set_in_primary3303 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_primary3309 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_expression_in_primary3349 = new BitSet(new long[]{0x1000000000000400L});
	public static final BitSet FOLLOW_COMMA_in_primary3352 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_expression_in_primary3356 = new BitSet(new long[]{0x1000000000000400L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_primary3396 = new BitSet(new long[]{0x0008006010010002L});
	public static final BitSet FOLLOW_HASH_in_primary3450 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_primary3454 = new BitSet(new long[]{0x0008006010010002L});
	public static final BitSet FOLLOW_NULL_SAFE_DOT_in_primary3496 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_primary3500 = new BitSet(new long[]{0x0008006010010002L});
	public static final BitSet FOLLOW_identifierSuffix_in_primary3522 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineListExpression3543 = new BitSet(new long[]{0x248502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_expressionList_in_inlineListExpression3545 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineListExpression3548 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineMapExpression3569 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_mapExpressionList_in_inlineMapExpression3571 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineMapExpression3573 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_mapEntry_in_mapExpressionList3594 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_COMMA_in_mapExpressionList3597 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_mapEntry_in_mapExpressionList3599 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_expression_in_mapEntry3618 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_COLON_in_mapEntry3620 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_expression_in_mapEntry3622 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_parExpression3643 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_expression_in_parExpression3647 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_parExpression3649 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix3683 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix3724 = new BitSet(new long[]{0x0000004000010000L});
	public static final BitSet FOLLOW_DOT_in_identifierSuffix3768 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_class_key_in_identifierSuffix3772 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix3787 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_expression_in_identifierSuffix3817 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix3845 = new BitSet(new long[]{0x0000004000000002L});
	public static final BitSet FOLLOW_arguments_in_identifierSuffix3861 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator3883 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_createdName_in_creator3886 = new BitSet(new long[]{0x0000006000000000L});
	public static final BitSet FOLLOW_arrayCreatorRest_in_creator3897 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_classCreatorRest_in_creator3901 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_createdName3919 = new BitSet(new long[]{0x0000008000010002L});
	public static final BitSet FOLLOW_typeArguments_in_createdName3921 = new BitSet(new long[]{0x0000000000010002L});
	public static final BitSet FOLLOW_DOT_in_createdName3934 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_createdName3936 = new BitSet(new long[]{0x0000008000010002L});
	public static final BitSet FOLLOW_typeArguments_in_createdName3938 = new BitSet(new long[]{0x0000000000010002L});
	public static final BitSet FOLLOW_primitiveType_in_createdName3953 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_innerCreator3973 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_classCreatorRest_in_innerCreator3975 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3994 = new BitSet(new long[]{0x248502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest4004 = new BitSet(new long[]{0x0000005000000000L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest4007 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest4009 = new BitSet(new long[]{0x0000005000000000L});
	public static final BitSet FOLLOW_arrayInitializer_in_arrayCreatorRest4013 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_arrayCreatorRest4027 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest4029 = new BitSet(new long[]{0x0000004000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest4034 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_expression_in_arrayCreatorRest4036 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest4038 = new BitSet(new long[]{0x0000004000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest4050 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest4052 = new BitSet(new long[]{0x0000004000000002L});
	public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer4081 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_variableInitializer4095 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_CURLY_in_arrayInitializer4112 = new BitSet(new long[]{0x0C8502F1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer4115 = new BitSet(new long[]{0x0800000000000400L});
	public static final BitSet FOLLOW_COMMA_in_arrayInitializer4118 = new BitSet(new long[]{0x048502F1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer4120 = new BitSet(new long[]{0x0800000000000400L});
	public static final BitSet FOLLOW_COMMA_in_arrayInitializer4125 = new BitSet(new long[]{0x0800000000000000L});
	public static final BitSet FOLLOW_RIGHT_CURLY_in_arrayInitializer4132 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_arguments_in_classCreatorRest4149 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation4167 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_arguments_in_explicitGenericInvocation4169 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LESS_in_nonWildcardTypeArguments4186 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments4188 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_nonWildcardTypeArguments4190 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_super_key_in_explicitGenericInvocationSuffix4207 = new BitSet(new long[]{0x0000002000010000L});
	public static final BitSet FOLLOW_superSuffix_in_explicitGenericInvocationSuffix4209 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_explicitGenericInvocationSuffix4220 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_arguments_in_explicitGenericInvocationSuffix4222 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_selector4247 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_super_key_in_selector4251 = new BitSet(new long[]{0x0000002000010000L});
	public static final BitSet FOLLOW_superSuffix_in_selector4253 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_selector4269 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_new_key_in_selector4273 = new BitSet(new long[]{0x0000008080000000L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_selector4276 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_innerCreator_in_selector4280 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_selector4296 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_selector4318 = new BitSet(new long[]{0x0000002000000002L});
	public static final BitSet FOLLOW_arguments_in_selector4347 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NULL_SAFE_DOT_in_selector4365 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_selector4387 = new BitSet(new long[]{0x0000002000000002L});
	public static final BitSet FOLLOW_arguments_in_selector4416 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_selector4437 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_expression_in_selector4464 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_selector4489 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_arguments_in_superSuffix4508 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_superSuffix4519 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_superSuffix4521 = new BitSet(new long[]{0x0000002000000002L});
	public static final BitSet FOLLOW_arguments_in_superSuffix4530 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_squareArguments4553 = new BitSet(new long[]{0x248502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_expressionList_in_squareArguments4558 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_squareArguments4564 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_arguments4581 = new BitSet(new long[]{0x148502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_expressionList_in_arguments4593 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_arguments4604 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_expressionList4634 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_COMMA_in_expressionList4645 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_expression_in_expressionList4649 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4670 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_PLUS_ASSIGN_in_assignmentOperator4678 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MINUS_ASSIGN_in_assignmentOperator4686 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MULT_ASSIGN_in_assignmentOperator4694 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DIV_ASSIGN_in_assignmentOperator4702 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AND_ASSIGN_in_assignmentOperator4710 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OR_ASSIGN_in_assignmentOperator4718 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_XOR_ASSIGN_in_assignmentOperator4726 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MOD_ASSIGN_in_assignmentOperator4734 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LESS_in_assignmentOperator4742 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_LESS_in_assignmentOperator4744 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4746 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GREATER_in_assignmentOperator4763 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_assignmentOperator4765 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_assignmentOperator4767 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4769 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GREATER_in_assignmentOperator4784 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_assignmentOperator4786 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4788 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_extends_key4818 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_super_key4847 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_instanceof_key4876 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_boolean_key4905 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_char_key4934 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_byte_key4963 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_short_key4992 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_int_key5021 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_float_key5050 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_long_key5079 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_double_key5108 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_void_key5137 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_this_key5166 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_class_key5195 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_new_key5225 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_not_key5254 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_in_key5281 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_operator_key5306 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_neg_operator_key5331 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_primitiveType_in_synpred1_DRL6Expressions548 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred2_DRL6Expressions559 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred2_DRL6Expressions561 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_typeArguments_in_synpred3_DRL6Expressions585 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_typeArguments_in_synpred4_DRL6Expressions599 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred5_DRL6Expressions611 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred5_DRL6Expressions613 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_assignmentOperator_in_synpred6_DRL6Expressions782 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_synpred7_DRL6Expressions961 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_EQUALS_ASSIGN_in_synpred7_DRL6Expressions963 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_not_key_in_synpred8_DRL6Expressions1629 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_in_key_in_synpred8_DRL6Expressions1631 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_operator_in_synpred9_DRL6Expressions1846 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_synpred9_DRL6Expressions1850 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOUBLE_PIPE_in_synpred10_DRL6Expressions1903 = new BitSet(new long[]{0x000201A08C100080L,0x0000000000000004L});
	public static final BitSet FOLLOW_fullAnnotation_in_synpred10_DRL6Expressions1905 = new BitSet(new long[]{0x000201A08C100000L,0x0000000000000004L});
	public static final BitSet FOLLOW_andRestriction_in_synpred10_DRL6Expressions1909 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOUBLE_AMPER_in_synpred11_DRL6Expressions1972 = new BitSet(new long[]{0x000201808C100080L,0x0000000000000004L});
	public static final BitSet FOLLOW_fullAnnotation_in_synpred11_DRL6Expressions1974 = new BitSet(new long[]{0x000201808C100000L,0x0000000000000004L});
	public static final BitSet FOLLOW_operator_in_synpred11_DRL6Expressions1978 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_squareArguments_in_synpred12_DRL6Expressions2066 = new BitSet(new long[]{0x048502E1A1017100L,0x000000000000000FL});
	public static final BitSet FOLLOW_shiftExpression_in_synpred12_DRL6Expressions2068 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_shiftOp_in_synpred13_DRL6Expressions2157 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_castExpression_in_synpred15_DRL6Expressions2489 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_backReferenceExpression_in_synpred16_DRL6Expressions2503 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_xpathSeparator_in_synpred17_DRL6Expressions2624 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_synpred17_DRL6Expressions2626 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_selector_in_synpred18_DRL6Expressions2674 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_synpred20_DRL6Expressions2732 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_primitiveType_in_synpred20_DRL6Expressions2734 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_synpred21_DRL6Expressions2757 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_type_in_synpred21_DRL6Expressions2759 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_synpred24_DRL6Expressions3051 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred25_DRL6Expressions3070 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literal_in_synpred26_DRL6Expressions3095 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_super_key_in_synpred27_DRL6Expressions3117 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_new_key_in_synpred28_DRL6Expressions3134 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_primitiveType_in_synpred29_DRL6Expressions3151 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_inlineMapExpression_in_synpred30_DRL6Expressions3182 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_inlineListExpression_in_synpred31_DRL6Expressions3197 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_synpred32_DRL6Expressions3212 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_synpred33_DRL6Expressions3246 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_synpred33_DRL6Expressions3248 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_synpred34_DRL6Expressions3292 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_synpred34_DRL6Expressions3298 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_HASH_in_synpred35_DRL6Expressions3443 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_synpred35_DRL6Expressions3445 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NULL_SAFE_DOT_in_synpred36_DRL6Expressions3489 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_synpred36_DRL6Expressions3491 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_identifierSuffix_in_synpred37_DRL6Expressions3519 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred38_DRL6Expressions3677 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred38_DRL6Expressions3679 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred39_DRL6Expressions3782 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred40_DRL6Expressions4044 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred40_DRL6Expressions4046 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_synpred41_DRL6Expressions4242 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_super_key_in_synpred41_DRL6Expressions4244 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_synpred42_DRL6Expressions4264 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_new_key_in_synpred42_DRL6Expressions4266 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_synpred43_DRL6Expressions4291 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_synpred43_DRL6Expressions4293 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_synpred44_DRL6Expressions4342 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NULL_SAFE_DOT_in_synpred45_DRL6Expressions4360 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_synpred45_DRL6Expressions4362 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_synpred46_DRL6Expressions4411 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred47_DRL6Expressions4434 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_synpred48_DRL6Expressions4525 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GREATER_in_synpred49_DRL6Expressions4755 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_synpred49_DRL6Expressions4757 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_synpred49_DRL6Expressions4759 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GREATER_in_synpred50_DRL6Expressions4778 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_synpred50_DRL6Expressions4780 = new BitSet(new long[]{0x0000000000000002L});
}
