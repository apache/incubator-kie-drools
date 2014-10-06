// $ANTLR 3.5 src/main/resources/org/drools/compiler/lang/DRL5Expressions.g 2014-10-05 20:03:07

    package org.drools.compiler.lang;

    import java.util.LinkedList;
    import org.drools.compiler.compiler.DroolsParserException;
    import org.drools.compiler.lang.ParserHelper;
    import org.drools.compiler.lang.DroolsParserExceptionFactory;
    import org.drools.compiler.lang.Location;

    import org.drools.compiler.lang.api.AnnotatedDescrBuilder;

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
public class DRL5Expressions extends DRLExpressions {
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


	public DRL5Expressions(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public DRL5Expressions(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override public String[] getTokenNames() { return DRL5Expressions.tokenNames; }
	@Override public String getGrammarFileName() { return "src/main/resources/org/drools/compiler/lang/DRL5Expressions.g"; }


	    private ParserHelper helper;

	    public DRL5Expressions(TokenStream input,
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
	        if (input.get( input.index() - 1 ).getType() == DRL5Lexer.WS){
	            return true;
	        }
	        if (input.LA(-1) == DRL5Lexer.LEFT_PAREN){
	            return true;
	        }
	        return input.get( input.index() ).getType() != DRL5Lexer.EOF;
	    }


	public static class literal_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "literal"
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:89:1: literal : ( STRING | DECIMAL | HEX | FLOAT | BOOL | NULL | TIME_INTERVAL | STAR );
	public final DRL5Expressions.literal_return literal() throws RecognitionException {
		DRL5Expressions.literal_return retval = new DRL5Expressions.literal_return();
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
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:90:5: ( STRING | DECIMAL | HEX | FLOAT | BOOL | NULL | TIME_INTERVAL | STAR )
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
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:90:7: STRING
					{
					STRING1=(Token)match(input,STRING,FOLLOW_STRING_in_literal92); if (state.failed) return retval;
					if ( state.backtracking==0 ) {	helper.emit(STRING1, DroolsEditorType.STRING_CONST);	}
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:91:7: DECIMAL
					{
					DECIMAL2=(Token)match(input,DECIMAL,FOLLOW_DECIMAL_in_literal109); if (state.failed) return retval;
					if ( state.backtracking==0 ) {	helper.emit(DECIMAL2, DroolsEditorType.NUMERIC_CONST);	}
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:92:7: HEX
					{
					HEX3=(Token)match(input,HEX,FOLLOW_HEX_in_literal125); if (state.failed) return retval;
					if ( state.backtracking==0 ) {	helper.emit(HEX3, DroolsEditorType.NUMERIC_CONST);	}
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:93:7: FLOAT
					{
					FLOAT4=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_literal145); if (state.failed) return retval;
					if ( state.backtracking==0 ) {	helper.emit(FLOAT4, DroolsEditorType.NUMERIC_CONST);	}
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:94:7: BOOL
					{
					BOOL5=(Token)match(input,BOOL,FOLLOW_BOOL_in_literal163); if (state.failed) return retval;
					if ( state.backtracking==0 ) {	helper.emit(BOOL5, DroolsEditorType.BOOLEAN_CONST);	}
					}
					break;
				case 6 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:95:7: NULL
					{
					NULL6=(Token)match(input,NULL,FOLLOW_NULL_in_literal182); if (state.failed) return retval;
					if ( state.backtracking==0 ) {	helper.emit(NULL6, DroolsEditorType.NULL_CONST);	}
					}
					break;
				case 7 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:96:9: TIME_INTERVAL
					{
					TIME_INTERVAL7=(Token)match(input,TIME_INTERVAL,FOLLOW_TIME_INTERVAL_in_literal203); if (state.failed) return retval;
					if ( state.backtracking==0 ) {	helper.emit(TIME_INTERVAL7, DroolsEditorType.NULL_CONST); }
					}
					break;
				case 8 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:97:9: STAR
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:100:1: operator returns [boolean negated, String opr] : (x= TILDE )? (op= EQUALS |op= NOT_EQUALS |rop= relationalOp ) ;
	public final DRL5Expressions.operator_return operator() throws RecognitionException {
		DRL5Expressions.operator_return retval = new DRL5Expressions.operator_return();
		retval.start = input.LT(1);

		Token x=null;
		Token op=null;
		ParserRuleReturnScope rop =null;

		 if ( isNotEOF() ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR ); helper.setHasOperator( true ); 
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:103:3: ( (x= TILDE )? (op= EQUALS |op= NOT_EQUALS |rop= relationalOp ) )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:103:5: (x= TILDE )? (op= EQUALS |op= NOT_EQUALS |rop= relationalOp )
			{
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:103:6: (x= TILDE )?
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
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:103:6: x= TILDE
					{
					x=(Token)match(input,TILDE,FOLLOW_TILDE_in_operator256); if (state.failed) return retval;
					}
					break;

			}

			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:104:5: (op= EQUALS |op= NOT_EQUALS |rop= relationalOp )
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
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:104:7: op= EQUALS
					{
					op=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_operator267); if (state.failed) return retval;
					if ( state.backtracking==0 ) { retval.negated = false; retval.opr =(x != null ? (x!=null?x.getText():null) : "")+(op!=null?op.getText():null); helper.emit(op, DroolsEditorType.SYMBOL); }
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:105:7: op= NOT_EQUALS
					{
					op=(Token)match(input,NOT_EQUALS,FOLLOW_NOT_EQUALS_in_operator286); if (state.failed) return retval;
					if ( state.backtracking==0 ) { retval.negated = false; retval.opr =(x != null ? (x!=null?x.getText():null) : "")+(op!=null?op.getText():null); helper.emit(op, DroolsEditorType.SYMBOL); }
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:106:7: rop= relationalOp
					{
					pushFollow(FOLLOW_relationalOp_in_operator301);
					rop=relationalOp();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) { retval.negated = (rop!=null?((DRL5Expressions.relationalOp_return)rop).negated:false); retval.opr =(x != null ? (x!=null?x.getText():null) : "")+(rop!=null?((DRL5Expressions.relationalOp_return)rop).opr:null); }
					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) { if( state.backtracking == 0 && input.LA( 1 ) != DRL5Lexer.EOF) { helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); } }
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:112:1: relationalOp returns [boolean negated, String opr, java.util.List<String> params] : (op= LESS_EQUALS |op= GREATER_EQUALS |op= LESS |op= GREATER |xop= complexOp | not_key nop= neg_operator_key |cop= operator_key ) ;
	public final DRL5Expressions.relationalOp_return relationalOp() throws RecognitionException {
		DRL5Expressions.relationalOp_return retval = new DRL5Expressions.relationalOp_return();
		retval.start = input.LT(1);

		Token op=null;
		String xop =null;
		ParserRuleReturnScope nop =null;
		ParserRuleReturnScope cop =null;

		 if ( isNotEOF() ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR ); helper.setHasOperator( true ); 
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:115:3: ( (op= LESS_EQUALS |op= GREATER_EQUALS |op= LESS |op= GREATER |xop= complexOp | not_key nop= neg_operator_key |cop= operator_key ) )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:115:5: (op= LESS_EQUALS |op= GREATER_EQUALS |op= LESS |op= GREATER |xop= complexOp | not_key nop= neg_operator_key |cop= operator_key )
			{
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:115:5: (op= LESS_EQUALS |op= GREATER_EQUALS |op= LESS |op= GREATER |xop= complexOp | not_key nop= neg_operator_key |cop= operator_key )
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
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:115:7: op= LESS_EQUALS
					{
					op=(Token)match(input,LESS_EQUALS,FOLLOW_LESS_EQUALS_in_relationalOp342); if (state.failed) return retval;
					if ( state.backtracking==0 ) { retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; helper.emit(op, DroolsEditorType.SYMBOL);}
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:116:7: op= GREATER_EQUALS
					{
					op=(Token)match(input,GREATER_EQUALS,FOLLOW_GREATER_EQUALS_in_relationalOp358); if (state.failed) return retval;
					if ( state.backtracking==0 ) { retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; helper.emit(op, DroolsEditorType.SYMBOL);}
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:117:7: op= LESS
					{
					op=(Token)match(input,LESS,FOLLOW_LESS_in_relationalOp371); if (state.failed) return retval;
					if ( state.backtracking==0 ) { retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; helper.emit(op, DroolsEditorType.SYMBOL);}
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:118:7: op= GREATER
					{
					op=(Token)match(input,GREATER,FOLLOW_GREATER_in_relationalOp394); if (state.failed) return retval;
					if ( state.backtracking==0 ) { retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; helper.emit(op, DroolsEditorType.SYMBOL);}
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:119:7: xop= complexOp
					{
					pushFollow(FOLLOW_complexOp_in_relationalOp414);
					xop=complexOp();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) { retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; helper.emit(op, DroolsEditorType.SYMBOL);}
					}
					break;
				case 6 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:120:7: not_key nop= neg_operator_key
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
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:121:7: cop= operator_key
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

			if ( state.backtracking==0 ) { if( state.backtracking == 0 && input.LA( 1 ) != DRL5Lexer.EOF) { helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); } }
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:125:1: complexOp returns [String opr] : t= TILDE e= EQUALS_ASSIGN ;
	public final String complexOp() throws RecognitionException {
		String opr = null;


		Token t=null;
		Token e=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:126:5: (t= TILDE e= EQUALS_ASSIGN )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:126:7: t= TILDE e= EQUALS_ASSIGN
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:129:1: typeList : type ( COMMA type )* ;
	public final void typeList() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:130:5: ( type ( COMMA type )* )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:130:7: type ( COMMA type )*
			{
			pushFollow(FOLLOW_type_in_typeList502);
			type();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:130:12: ( COMMA type )*
			loop5:
			while (true) {
				int alt5=2;
				int LA5_0 = input.LA(1);
				if ( (LA5_0==COMMA) ) {
					alt5=1;
				}

				switch (alt5) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:130:13: COMMA type
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:133:1: type : tm= typeMatch ;
	public final DRL5Expressions.type_return type() throws RecognitionException {
		DRL5Expressions.type_return retval = new DRL5Expressions.type_return();
		retval.start = input.LT(1);

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:134:5: (tm= typeMatch )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:134:8: tm= typeMatch
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:137:1: typeMatch : ( ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) | ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) );
	public final void typeMatch() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:138:5: ( ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) | ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) )
			int alt11=2;
			int LA11_0 = input.LA(1);
			if ( (LA11_0==ID) ) {
				int LA11_1 = input.LA(2);
				if ( (((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))&&synpred1_DRL5Expressions())) ) {
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
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:138:8: ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
					{
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:138:27: ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:138:29: primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
					{
					pushFollow(FOLLOW_primitiveType_in_typeMatch555);
					primitiveType();
					state._fsp--;
					if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:138:43: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
					loop6:
					while (true) {
						int alt6=2;
						int LA6_0 = input.LA(1);
						if ( (LA6_0==LEFT_SQUARE) && (synpred2_DRL5Expressions())) {
							alt6=1;
						}

						switch (alt6) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:138:44: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
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
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:139:7: ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
					{
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:139:7: ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:139:9: ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
					{
					match(input,ID,FOLLOW_ID_in_typeMatch581); if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:139:12: ( ( typeArguments )=> typeArguments )?
					int alt7=2;
					int LA7_0 = input.LA(1);
					if ( (LA7_0==LESS) ) {
						int LA7_1 = input.LA(2);
						if ( (LA7_1==ID) && (synpred3_DRL5Expressions())) {
							alt7=1;
						}
						else if ( (LA7_1==QUESTION) && (synpred3_DRL5Expressions())) {
							alt7=1;
						}
					}
					switch (alt7) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:139:13: ( typeArguments )=> typeArguments
							{
							pushFollow(FOLLOW_typeArguments_in_typeMatch588);
							typeArguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:139:46: ( DOT ID ( ( typeArguments )=> typeArguments )? )*
					loop9:
					while (true) {
						int alt9=2;
						int LA9_0 = input.LA(1);
						if ( (LA9_0==DOT) ) {
							alt9=1;
						}

						switch (alt9) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:139:47: DOT ID ( ( typeArguments )=> typeArguments )?
							{
							match(input,DOT,FOLLOW_DOT_in_typeMatch593); if (state.failed) return;
							match(input,ID,FOLLOW_ID_in_typeMatch595); if (state.failed) return;
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:139:54: ( ( typeArguments )=> typeArguments )?
							int alt8=2;
							int LA8_0 = input.LA(1);
							if ( (LA8_0==LESS) ) {
								int LA8_1 = input.LA(2);
								if ( (LA8_1==ID) && (synpred4_DRL5Expressions())) {
									alt8=1;
								}
								else if ( (LA8_1==QUESTION) && (synpred4_DRL5Expressions())) {
									alt8=1;
								}
							}
							switch (alt8) {
								case 1 :
									// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:139:55: ( typeArguments )=> typeArguments
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

					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:139:91: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
					loop10:
					while (true) {
						int alt10=2;
						int LA10_0 = input.LA(1);
						if ( (LA10_0==LEFT_SQUARE) && (synpred5_DRL5Expressions())) {
							alt10=1;
						}

						switch (alt10) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:139:92: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:142:1: typeArguments : LESS typeArgument ( COMMA typeArgument )* GREATER ;
	public final void typeArguments() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:143:5: ( LESS typeArgument ( COMMA typeArgument )* GREATER )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:143:7: LESS typeArgument ( COMMA typeArgument )* GREATER
			{
			match(input,LESS,FOLLOW_LESS_in_typeArguments640); if (state.failed) return;
			pushFollow(FOLLOW_typeArgument_in_typeArguments642);
			typeArgument();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:143:25: ( COMMA typeArgument )*
			loop12:
			while (true) {
				int alt12=2;
				int LA12_0 = input.LA(1);
				if ( (LA12_0==COMMA) ) {
					alt12=1;
				}

				switch (alt12) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:143:26: COMMA typeArgument
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:146:1: typeArgument : ( type | QUESTION ( ( extends_key | super_key ) type )? );
	public final void typeArgument() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:147:5: ( type | QUESTION ( ( extends_key | super_key ) type )? )
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
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:147:7: type
					{
					pushFollow(FOLLOW_type_in_typeArgument668);
					type();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:148:7: QUESTION ( ( extends_key | super_key ) type )?
					{
					match(input,QUESTION,FOLLOW_QUESTION_in_typeArgument676); if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:148:16: ( ( extends_key | super_key ) type )?
					int alt14=2;
					int LA14_0 = input.LA(1);
					if ( (LA14_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))||((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))))) {
						alt14=1;
					}
					switch (alt14) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:148:17: ( extends_key | super_key ) type
							{
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:148:17: ( extends_key | super_key )
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
									// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:148:18: extends_key
									{
									pushFollow(FOLLOW_extends_key_in_typeArgument680);
									extends_key();
									state._fsp--;
									if (state.failed) return;
									}
									break;
								case 2 :
									// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:148:32: super_key
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:156:1: dummy : expression ( AT | SEMICOLON | EOF | ID | RIGHT_PAREN ) ;
	public final void dummy() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:157:5: ( expression ( AT | SEMICOLON | EOF | ID | RIGHT_PAREN ) )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:157:7: expression ( AT | SEMICOLON | EOF | ID | RIGHT_PAREN )
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:159:1: dummy2 : relationalExpression EOF ;
	public final void dummy2() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:160:5: ( relationalExpression EOF )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:160:8: relationalExpression EOF
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:163:1: expression returns [BaseDescr result] : left= conditionalExpression ( ( assignmentOperator )=>op= assignmentOperator right= expression )? ;
	public final DRL5Expressions.expression_return expression() throws RecognitionException {
		DRL5Expressions.expression_return retval = new DRL5Expressions.expression_return();
		retval.start = input.LT(1);

		BaseDescr left =null;
		ParserRuleReturnScope right =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:164:5: (left= conditionalExpression ( ( assignmentOperator )=>op= assignmentOperator right= expression )? )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:164:7: left= conditionalExpression ( ( assignmentOperator )=>op= assignmentOperator right= expression )?
			{
			pushFollow(FOLLOW_conditionalExpression_in_expression768);
			left=conditionalExpression();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) { if( buildDescr  ) { retval.result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:165:9: ( ( assignmentOperator )=>op= assignmentOperator right= expression )?
			int alt16=2;
			switch ( input.LA(1) ) {
				case EQUALS_ASSIGN:
					{
					int LA16_1 = input.LA(2);
					if ( (synpred6_DRL5Expressions()) ) {
						alt16=1;
					}
					}
					break;
				case PLUS_ASSIGN:
					{
					int LA16_2 = input.LA(2);
					if ( (synpred6_DRL5Expressions()) ) {
						alt16=1;
					}
					}
					break;
				case MINUS_ASSIGN:
					{
					int LA16_3 = input.LA(2);
					if ( (synpred6_DRL5Expressions()) ) {
						alt16=1;
					}
					}
					break;
				case MULT_ASSIGN:
					{
					int LA16_4 = input.LA(2);
					if ( (synpred6_DRL5Expressions()) ) {
						alt16=1;
					}
					}
					break;
				case DIV_ASSIGN:
					{
					int LA16_5 = input.LA(2);
					if ( (synpred6_DRL5Expressions()) ) {
						alt16=1;
					}
					}
					break;
				case AND_ASSIGN:
					{
					int LA16_6 = input.LA(2);
					if ( (synpred6_DRL5Expressions()) ) {
						alt16=1;
					}
					}
					break;
				case OR_ASSIGN:
					{
					int LA16_7 = input.LA(2);
					if ( (synpred6_DRL5Expressions()) ) {
						alt16=1;
					}
					}
					break;
				case XOR_ASSIGN:
					{
					int LA16_8 = input.LA(2);
					if ( (synpred6_DRL5Expressions()) ) {
						alt16=1;
					}
					}
					break;
				case MOD_ASSIGN:
					{
					int LA16_9 = input.LA(2);
					if ( (synpred6_DRL5Expressions()) ) {
						alt16=1;
					}
					}
					break;
				case LESS:
					{
					int LA16_10 = input.LA(2);
					if ( (synpred6_DRL5Expressions()) ) {
						alt16=1;
					}
					}
					break;
				case GREATER:
					{
					int LA16_11 = input.LA(2);
					if ( (synpred6_DRL5Expressions()) ) {
						alt16=1;
					}
					}
					break;
			}
			switch (alt16) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:165:10: ( assignmentOperator )=>op= assignmentOperator right= expression
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:168:1: conditionalExpression returns [BaseDescr result] : left= conditionalOrExpression ( ternaryExpression )? ;
	public final BaseDescr conditionalExpression() throws RecognitionException {
		BaseDescr result = null;


		BaseDescr left =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:169:5: (left= conditionalOrExpression ( ternaryExpression )? )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:169:9: left= conditionalOrExpression ( ternaryExpression )?
			{
			pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression820);
			left=conditionalOrExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:170:9: ( ternaryExpression )?
			int alt17=2;
			int LA17_0 = input.LA(1);
			if ( (LA17_0==QUESTION) ) {
				alt17=1;
			}
			switch (alt17) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:170:9: ternaryExpression
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:173:1: ternaryExpression : QUESTION ts= expression COLON fs= expression ;
	public final void ternaryExpression() throws RecognitionException {
		ParserRuleReturnScope ts =null;
		ParserRuleReturnScope fs =null;

		 ternOp++; 
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:175:5: ( QUESTION ts= expression COLON fs= expression )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:175:7: QUESTION ts= expression COLON fs= expression
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:180:1: fullAnnotation[AnnotatedDescrBuilder inDescrBuilder] returns [AnnotationDescr result] : AT name= ID ( DOT x= ID )* annotationArgs[result] ;
	public final AnnotationDescr fullAnnotation(AnnotatedDescrBuilder inDescrBuilder) throws RecognitionException {
		AnnotationDescr result = null;


		Token name=null;
		Token x=null;

		 String n = ""; 
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:182:3: ( AT name= ID ( DOT x= ID )* annotationArgs[result] )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:182:5: AT name= ID ( DOT x= ID )* annotationArgs[result]
			{
			match(input,AT,FOLLOW_AT_in_fullAnnotation894); if (state.failed) return result;
			name=(Token)match(input,ID,FOLLOW_ID_in_fullAnnotation898); if (state.failed) return result;
			if ( state.backtracking==0 ) { n = (name!=null?name.getText():null); }
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:182:36: ( DOT x= ID )*
			loop18:
			while (true) {
				int alt18=2;
				int LA18_0 = input.LA(1);
				if ( (LA18_0==DOT) ) {
					alt18=1;
				}

				switch (alt18) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:182:38: DOT x= ID
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

			if ( state.backtracking==0 ) { if( buildDescr ) { result = inDescrBuilder != null ? (AnnotationDescr) inDescrBuilder.newAnnotation( n ).getDescr() : new AnnotationDescr( n ); } }
			pushFollow(FOLLOW_annotationArgs_in_fullAnnotation929);
			annotationArgs(result);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:187:1: annotationArgs[AnnotationDescr descr] : LEFT_PAREN (value= ID | annotationElementValuePairs[descr] )? RIGHT_PAREN ;
	public final void annotationArgs(AnnotationDescr descr) throws RecognitionException {
		Token value=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:188:3: ( LEFT_PAREN (value= ID | annotationElementValuePairs[descr] )? RIGHT_PAREN )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:188:5: LEFT_PAREN (value= ID | annotationElementValuePairs[descr] )? RIGHT_PAREN
			{
			match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_annotationArgs945); if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:189:5: (value= ID | annotationElementValuePairs[descr] )?
			int alt19=3;
			int LA19_0 = input.LA(1);
			if ( (LA19_0==ID) ) {
				int LA19_1 = input.LA(2);
				if ( (LA19_1==EQUALS_ASSIGN) ) {
					alt19=2;
				}
				else if ( (LA19_1==RIGHT_PAREN) ) {
					alt19=1;
				}
			}
			switch (alt19) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:190:8: value= ID
					{
					value=(Token)match(input,ID,FOLLOW_ID_in_annotationArgs962); if (state.failed) return;
					if ( state.backtracking==0 ) { if ( buildDescr ) { descr.setValue( (value!=null?value.getText():null) ); } }
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:191:10: annotationElementValuePairs[descr]
					{
					pushFollow(FOLLOW_annotationElementValuePairs_in_annotationArgs975);
					annotationElementValuePairs(descr);
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_annotationArgs989); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:196:1: annotationElementValuePairs[AnnotationDescr descr] : annotationElementValuePair[descr] ( COMMA annotationElementValuePair[descr] )* ;
	public final void annotationElementValuePairs(AnnotationDescr descr) throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:197:3: ( annotationElementValuePair[descr] ( COMMA annotationElementValuePair[descr] )* )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:197:5: annotationElementValuePair[descr] ( COMMA annotationElementValuePair[descr] )*
			{
			pushFollow(FOLLOW_annotationElementValuePair_in_annotationElementValuePairs1004);
			annotationElementValuePair(descr);
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:197:39: ( COMMA annotationElementValuePair[descr] )*
			loop20:
			while (true) {
				int alt20=2;
				int LA20_0 = input.LA(1);
				if ( (LA20_0==COMMA) ) {
					alt20=1;
				}

				switch (alt20) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:197:41: COMMA annotationElementValuePair[descr]
					{
					match(input,COMMA,FOLLOW_COMMA_in_annotationElementValuePairs1009); if (state.failed) return;
					pushFollow(FOLLOW_annotationElementValuePair_in_annotationElementValuePairs1011);
					annotationElementValuePair(descr);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:200:1: annotationElementValuePair[AnnotationDescr descr] : key= ID EQUALS_ASSIGN val= annotationValue ;
	public final void annotationElementValuePair(AnnotationDescr descr) throws RecognitionException {
		Token key=null;
		ParserRuleReturnScope val =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:201:3: (key= ID EQUALS_ASSIGN val= annotationValue )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:201:5: key= ID EQUALS_ASSIGN val= annotationValue
			{
			key=(Token)match(input,ID,FOLLOW_ID_in_annotationElementValuePair1032); if (state.failed) return;
			match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_annotationElementValuePair1034); if (state.failed) return;
			pushFollow(FOLLOW_annotationValue_in_annotationElementValuePair1038);
			val=annotationValue();
			state._fsp--;
			if (state.failed) return;
			if ( state.backtracking==0 ) { if ( buildDescr ) { descr.setKeyValue( (key!=null?key.getText():null), (val!=null?input.toString(val.start,val.stop):null) ); } }
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


	public static class annotationValue_return extends ParserRuleReturnScope {
	};


	// $ANTLR start "annotationValue"
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:204:1: annotationValue : ( expression | annotationArray );
	public final DRL5Expressions.annotationValue_return annotationValue() throws RecognitionException {
		DRL5Expressions.annotationValue_return retval = new DRL5Expressions.annotationValue_return();
		retval.start = input.LT(1);

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:205:3: ( expression | annotationArray )
			int alt21=2;
			int LA21_0 = input.LA(1);
			if ( (LA21_0==BOOL||(LA21_0 >= DECIMAL && LA21_0 <= DECR)||LA21_0==FLOAT||LA21_0==HEX||(LA21_0 >= ID && LA21_0 <= INCR)||(LA21_0 >= LEFT_PAREN && LA21_0 <= LESS)||LA21_0==MINUS||LA21_0==NEGATION||LA21_0==NULL||LA21_0==PLUS||(LA21_0 >= STAR && LA21_0 <= TIME_INTERVAL)) ) {
				alt21=1;
			}
			else if ( (LA21_0==LEFT_CURLY) ) {
				alt21=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 21, 0, input);
				throw nvae;
			}

			switch (alt21) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:205:5: expression
					{
					pushFollow(FOLLOW_expression_in_annotationValue1053);
					expression();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:205:18: annotationArray
					{
					pushFollow(FOLLOW_annotationArray_in_annotationValue1057);
					annotationArray();
					state._fsp--;
					if (state.failed) return retval;
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
	// $ANTLR end "annotationValue"



	// $ANTLR start "annotationArray"
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:208:1: annotationArray : LEFT_CURLY ( annotationValue ( COMMA annotationValue )* )? RIGHT_CURLY ;
	public final void annotationArray() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:209:3: ( LEFT_CURLY ( annotationValue ( COMMA annotationValue )* )? RIGHT_CURLY )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:209:6: LEFT_CURLY ( annotationValue ( COMMA annotationValue )* )? RIGHT_CURLY
			{
			match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_annotationArray1071); if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:209:17: ( annotationValue ( COMMA annotationValue )* )?
			int alt23=2;
			int LA23_0 = input.LA(1);
			if ( (LA23_0==BOOL||(LA23_0 >= DECIMAL && LA23_0 <= DECR)||LA23_0==FLOAT||LA23_0==HEX||(LA23_0 >= ID && LA23_0 <= INCR)||(LA23_0 >= LEFT_CURLY && LA23_0 <= LESS)||LA23_0==MINUS||LA23_0==NEGATION||LA23_0==NULL||LA23_0==PLUS||(LA23_0 >= STAR && LA23_0 <= TIME_INTERVAL)) ) {
				alt23=1;
			}
			switch (alt23) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:209:19: annotationValue ( COMMA annotationValue )*
					{
					pushFollow(FOLLOW_annotationValue_in_annotationArray1075);
					annotationValue();
					state._fsp--;
					if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:209:35: ( COMMA annotationValue )*
					loop22:
					while (true) {
						int alt22=2;
						int LA22_0 = input.LA(1);
						if ( (LA22_0==COMMA) ) {
							alt22=1;
						}

						switch (alt22) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:209:37: COMMA annotationValue
							{
							match(input,COMMA,FOLLOW_COMMA_in_annotationArray1079); if (state.failed) return;
							pushFollow(FOLLOW_annotationValue_in_annotationArray1081);
							annotationValue();
							state._fsp--;
							if (state.failed) return;
							}
							break;

						default :
							break loop22;
						}
					}

					}
					break;

			}

			match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_annotationArray1089); if (state.failed) return;
			}

		}

		catch (RecognitionException re) {
		    throw re;
		}

		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "annotationArray"



	// $ANTLR start "conditionalOrExpression"
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:214:1: conditionalOrExpression returns [BaseDescr result] : left= conditionalAndExpression ( DOUBLE_PIPE (args= fullAnnotation[null] )? right= conditionalAndExpression )* ;
	public final BaseDescr conditionalOrExpression() throws RecognitionException {
		BaseDescr result = null;


		BaseDescr left =null;
		AnnotationDescr args =null;
		BaseDescr right =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:215:3: (left= conditionalAndExpression ( DOUBLE_PIPE (args= fullAnnotation[null] )? right= conditionalAndExpression )* )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:215:5: left= conditionalAndExpression ( DOUBLE_PIPE (args= fullAnnotation[null] )? right= conditionalAndExpression )*
			{
			pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression1110);
			left=conditionalAndExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:216:3: ( DOUBLE_PIPE (args= fullAnnotation[null] )? right= conditionalAndExpression )*
			loop25:
			while (true) {
				int alt25=2;
				int LA25_0 = input.LA(1);
				if ( (LA25_0==DOUBLE_PIPE) ) {
					alt25=1;
				}

				switch (alt25) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:216:5: DOUBLE_PIPE (args= fullAnnotation[null] )? right= conditionalAndExpression
					{
					match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression1119); if (state.failed) return result;
					if ( state.backtracking==0 ) {  if ( isNotEOF() ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR );  }
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:218:13: (args= fullAnnotation[null] )?
					int alt24=2;
					int LA24_0 = input.LA(1);
					if ( (LA24_0==AT) ) {
						alt24=1;
					}
					switch (alt24) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:218:13: args= fullAnnotation[null]
							{
							pushFollow(FOLLOW_fullAnnotation_in_conditionalOrExpression1141);
							args=fullAnnotation(null);
							state._fsp--;
							if (state.failed) return result;
							}
							break;

					}

					pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression1147);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:230:1: conditionalAndExpression returns [BaseDescr result] : left= inclusiveOrExpression ( DOUBLE_AMPER (args= fullAnnotation[null] )? right= inclusiveOrExpression )* ;
	public final BaseDescr conditionalAndExpression() throws RecognitionException {
		BaseDescr result = null;


		BaseDescr left =null;
		AnnotationDescr args =null;
		BaseDescr right =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:231:3: (left= inclusiveOrExpression ( DOUBLE_AMPER (args= fullAnnotation[null] )? right= inclusiveOrExpression )* )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:231:5: left= inclusiveOrExpression ( DOUBLE_AMPER (args= fullAnnotation[null] )? right= inclusiveOrExpression )*
			{
			pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression1182);
			left=inclusiveOrExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:232:3: ( DOUBLE_AMPER (args= fullAnnotation[null] )? right= inclusiveOrExpression )*
			loop27:
			while (true) {
				int alt27=2;
				int LA27_0 = input.LA(1);
				if ( (LA27_0==DOUBLE_AMPER) ) {
					alt27=1;
				}

				switch (alt27) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:232:5: DOUBLE_AMPER (args= fullAnnotation[null] )? right= inclusiveOrExpression
					{
					match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression1190); if (state.failed) return result;
					if ( state.backtracking==0 ) { if ( isNotEOF() ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR ); }
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:234:13: (args= fullAnnotation[null] )?
					int alt26=2;
					int LA26_0 = input.LA(1);
					if ( (LA26_0==AT) ) {
						alt26=1;
					}
					switch (alt26) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:234:13: args= fullAnnotation[null]
							{
							pushFollow(FOLLOW_fullAnnotation_in_conditionalAndExpression1213);
							args=fullAnnotation(null);
							state._fsp--;
							if (state.failed) return result;
							}
							break;

					}

					pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression1219);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:246:1: inclusiveOrExpression returns [BaseDescr result] : left= exclusiveOrExpression ( PIPE right= exclusiveOrExpression )* ;
	public final BaseDescr inclusiveOrExpression() throws RecognitionException {
		BaseDescr result = null;


		BaseDescr left =null;
		BaseDescr right =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:247:3: (left= exclusiveOrExpression ( PIPE right= exclusiveOrExpression )* )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:247:5: left= exclusiveOrExpression ( PIPE right= exclusiveOrExpression )*
			{
			pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression1254);
			left=exclusiveOrExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:248:3: ( PIPE right= exclusiveOrExpression )*
			loop28:
			while (true) {
				int alt28=2;
				int LA28_0 = input.LA(1);
				if ( (LA28_0==PIPE) ) {
					alt28=1;
				}

				switch (alt28) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:248:5: PIPE right= exclusiveOrExpression
					{
					match(input,PIPE,FOLLOW_PIPE_in_inclusiveOrExpression1262); if (state.failed) return result;
					pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression1266);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:259:1: exclusiveOrExpression returns [BaseDescr result] : left= andExpression ( XOR right= andExpression )* ;
	public final BaseDescr exclusiveOrExpression() throws RecognitionException {
		BaseDescr result = null;


		BaseDescr left =null;
		BaseDescr right =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:260:3: (left= andExpression ( XOR right= andExpression )* )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:260:5: left= andExpression ( XOR right= andExpression )*
			{
			pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression1301);
			left=andExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:261:3: ( XOR right= andExpression )*
			loop29:
			while (true) {
				int alt29=2;
				int LA29_0 = input.LA(1);
				if ( (LA29_0==XOR) ) {
					alt29=1;
				}

				switch (alt29) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:261:5: XOR right= andExpression
					{
					match(input,XOR,FOLLOW_XOR_in_exclusiveOrExpression1309); if (state.failed) return result;
					pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression1313);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:272:1: andExpression returns [BaseDescr result] : left= equalityExpression ( AMPER right= equalityExpression )* ;
	public final BaseDescr andExpression() throws RecognitionException {
		BaseDescr result = null;


		BaseDescr left =null;
		BaseDescr right =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:273:3: (left= equalityExpression ( AMPER right= equalityExpression )* )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:273:5: left= equalityExpression ( AMPER right= equalityExpression )*
			{
			pushFollow(FOLLOW_equalityExpression_in_andExpression1348);
			left=equalityExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:274:3: ( AMPER right= equalityExpression )*
			loop30:
			while (true) {
				int alt30=2;
				int LA30_0 = input.LA(1);
				if ( (LA30_0==AMPER) ) {
					alt30=1;
				}

				switch (alt30) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:274:5: AMPER right= equalityExpression
					{
					match(input,AMPER,FOLLOW_AMPER_in_andExpression1356); if (state.failed) return result;
					pushFollow(FOLLOW_equalityExpression_in_andExpression1360);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:285:1: equalityExpression returns [BaseDescr result] : left= instanceOfExpression ( (op= EQUALS |op= NOT_EQUALS ) right= instanceOfExpression )* ;
	public final BaseDescr equalityExpression() throws RecognitionException {
		BaseDescr result = null;


		Token op=null;
		BaseDescr left =null;
		BaseDescr right =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:286:3: (left= instanceOfExpression ( (op= EQUALS |op= NOT_EQUALS ) right= instanceOfExpression )* )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:286:5: left= instanceOfExpression ( (op= EQUALS |op= NOT_EQUALS ) right= instanceOfExpression )*
			{
			pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression1395);
			left=instanceOfExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:287:3: ( (op= EQUALS |op= NOT_EQUALS ) right= instanceOfExpression )*
			loop32:
			while (true) {
				int alt32=2;
				int LA32_0 = input.LA(1);
				if ( (LA32_0==EQUALS||LA32_0==NOT_EQUALS) ) {
					alt32=1;
				}

				switch (alt32) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:287:5: (op= EQUALS |op= NOT_EQUALS ) right= instanceOfExpression
					{
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:287:5: (op= EQUALS |op= NOT_EQUALS )
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
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:287:7: op= EQUALS
							{
							op=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_equalityExpression1407); if (state.failed) return result;
							}
							break;
						case 2 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:287:19: op= NOT_EQUALS
							{
							op=(Token)match(input,NOT_EQUALS,FOLLOW_NOT_EQUALS_in_equalityExpression1413); if (state.failed) return result;
							}
							break;

					}

					if ( state.backtracking==0 ) {  helper.setHasOperator( true );
					       if( input.LA( 1 ) != DRL5Lexer.EOF ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); }
					pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression1429);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:298:1: instanceOfExpression returns [BaseDescr result] : left= inExpression (op= instanceof_key right= type )? ;
	public final BaseDescr instanceOfExpression() throws RecognitionException {
		BaseDescr result = null;


		BaseDescr left =null;
		ParserRuleReturnScope op =null;
		ParserRuleReturnScope right =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:299:3: (left= inExpression (op= instanceof_key right= type )? )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:299:5: left= inExpression (op= instanceof_key right= type )?
			{
			pushFollow(FOLLOW_inExpression_in_instanceOfExpression1464);
			left=inExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:300:3: (op= instanceof_key right= type )?
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
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:300:5: op= instanceof_key right= type
					{
					pushFollow(FOLLOW_instanceof_key_in_instanceOfExpression1474);
					op=instanceof_key();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) {  helper.setHasOperator( true );
					       if( input.LA( 1 ) != DRL5Lexer.EOF ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); }
					pushFollow(FOLLOW_type_in_instanceOfExpression1488);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:311:1: inExpression returns [BaseDescr result] : left= relationalExpression ( ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN |in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN )? ;
	public final BaseDescr inExpression() throws RecognitionException {
		BaseDescr result = null;


		BaseDescr left =null;
		ParserRuleReturnScope e1 =null;
		ParserRuleReturnScope e2 =null;

		 ConstraintConnectiveDescr descr = null; BaseDescr leftDescr = null; BindingDescr binding = null; 
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:314:3: (left= relationalExpression ( ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN |in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN )? )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:314:5: left= relationalExpression ( ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN |in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN )?
			{
			pushFollow(FOLLOW_relationalExpression_in_inExpression1533);
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
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:323:5: ( ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN |in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN )?
			int alt36=3;
			int LA36_0 = input.LA(1);
			if ( (LA36_0==ID) ) {
				int LA36_1 = input.LA(2);
				if ( (LA36_1==ID) ) {
					int LA36_3 = input.LA(3);
					if ( (LA36_3==LEFT_PAREN) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))&&synpred7_DRL5Expressions()))) {
						alt36=1;
					}
				}
				else if ( (LA36_1==LEFT_PAREN) && (((helper.validateIdentifierKey(DroolsSoftKeywords.IN))))) {
					alt36=2;
				}
			}
			switch (alt36) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:323:6: ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN
					{
					pushFollow(FOLLOW_not_key_in_inExpression1553);
					not_key();
					state._fsp--;
					if (state.failed) return result;
					pushFollow(FOLLOW_in_key_in_inExpression1557);
					in_key();
					state._fsp--;
					if (state.failed) return result;
					match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_inExpression1559); if (state.failed) return result;
					if ( state.backtracking==0 ) {   helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); }
					pushFollow(FOLLOW_expression_in_inExpression1581);
					e1=expression();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) {   descr = ConstraintConnectiveDescr.newAnd();
					            RelationalExprDescr rel = new RelationalExprDescr( "!=", false, null, leftDescr, (e1!=null?((DRL5Expressions.expression_return)e1).result:null) );
					            descr.addOrMerge( rel );
					            result = descr;
					        }
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:331:7: ( COMMA e2= expression )*
					loop34:
					while (true) {
						int alt34=2;
						int LA34_0 = input.LA(1);
						if ( (LA34_0==COMMA) ) {
							alt34=1;
						}

						switch (alt34) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:331:8: COMMA e2= expression
							{
							match(input,COMMA,FOLLOW_COMMA_in_inExpression1600); if (state.failed) return result;
							pushFollow(FOLLOW_expression_in_inExpression1604);
							e2=expression();
							state._fsp--;
							if (state.failed) return result;
							if ( state.backtracking==0 ) {   RelationalExprDescr rel = new RelationalExprDescr( "!=", false, null, leftDescr, (e2!=null?((DRL5Expressions.expression_return)e2).result:null) );
							            descr.addOrMerge( rel );
							        }
							}
							break;

						default :
							break loop34;
						}
					}

					match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_inExpression1625); if (state.failed) return result;
					if ( state.backtracking==0 ) { helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_END ); }
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:337:7: in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN
					{
					pushFollow(FOLLOW_in_key_in_inExpression1641);
					in_key();
					state._fsp--;
					if (state.failed) return result;
					match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_inExpression1643); if (state.failed) return result;
					if ( state.backtracking==0 ) {   helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); }
					pushFollow(FOLLOW_expression_in_inExpression1665);
					e1=expression();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) {   descr = ConstraintConnectiveDescr.newOr();
					            RelationalExprDescr rel = new RelationalExprDescr( "==", false, null, leftDescr, (e1!=null?((DRL5Expressions.expression_return)e1).result:null) );
					            descr.addOrMerge( rel );
					            result = descr;
					        }
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:345:7: ( COMMA e2= expression )*
					loop35:
					while (true) {
						int alt35=2;
						int LA35_0 = input.LA(1);
						if ( (LA35_0==COMMA) ) {
							alt35=1;
						}

						switch (alt35) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:345:8: COMMA e2= expression
							{
							match(input,COMMA,FOLLOW_COMMA_in_inExpression1684); if (state.failed) return result;
							pushFollow(FOLLOW_expression_in_inExpression1688);
							e2=expression();
							state._fsp--;
							if (state.failed) return result;
							if ( state.backtracking==0 ) {   RelationalExprDescr rel = new RelationalExprDescr( "==", false, null, leftDescr, (e2!=null?((DRL5Expressions.expression_return)e2).result:null) );
							            descr.addOrMerge( rel );
							        }
							}
							break;

						default :
							break loop35;
						}
					}

					match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_inExpression1709); if (state.failed) return result;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:354:1: relationalExpression returns [BaseDescr result] : left= shiftExpression ( ( operator | LEFT_PAREN )=>right= orRestriction )* ;
	public final BaseDescr relationalExpression() throws RecognitionException {
		relationalExpression_stack.push(new relationalExpression_scope());
		BaseDescr result = null;


		ParserRuleReturnScope left =null;
		BaseDescr right =null;

		 relationalExpression_stack.peek().lsd = null; 
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:357:3: (left= shiftExpression ( ( operator | LEFT_PAREN )=>right= orRestriction )* )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:357:5: left= shiftExpression ( ( operator | LEFT_PAREN )=>right= orRestriction )*
			{
			pushFollow(FOLLOW_shiftExpression_in_relationalExpression1750);
			left=shiftExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) {
			          if ( (left!=null?((DRL5Expressions.shiftExpression_return)left).result:null) == null ) {
			            result = new AtomicExprDescr( (left!=null?input.toString(left.start,left.stop):null) );
			          } else if ( (left!=null?((DRL5Expressions.shiftExpression_return)left).result:null) instanceof AtomicExprDescr ) {
			            if ( (left!=null?input.toString(left.start,left.stop):null).equals(((AtomicExprDescr)(left!=null?((DRL5Expressions.shiftExpression_return)left).result:null)).getExpression()) ) {
			              result = (left!=null?((DRL5Expressions.shiftExpression_return)left).result:null);
			            } else {
			              result = new AtomicExprDescr( (left!=null?input.toString(left.start,left.stop):null) ) ;
			            }
			          } else if ( (left!=null?((DRL5Expressions.shiftExpression_return)left).result:null) instanceof BindingDescr ) {
			              if ( (left!=null?input.toString(left.start,left.stop):null).equals(((BindingDescr)(left!=null?((DRL5Expressions.shiftExpression_return)left).result:null)).getExpression()) ) {
			                result = (left!=null?((DRL5Expressions.shiftExpression_return)left).result:null);
			              } else {
			                BindingDescr bind = (BindingDescr) (left!=null?((DRL5Expressions.shiftExpression_return)left).result:null);
			                int offset = bind.isUnification() ? 2 : 1;
			                String fullExpression = (left!=null?input.toString(left.start,left.stop):null).substring( (left!=null?input.toString(left.start,left.stop):null).indexOf( ":" ) + offset ).trim();
			                result = new BindingDescr( bind.getVariable(), bind.getExpression(), fullExpression, bind.isUnification() );
			              }
			          } else {
			              result = (left!=null?((DRL5Expressions.shiftExpression_return)left).result:null);
			          }
			          relationalExpression_stack.peek().lsd = result;
			      } 
			    }
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:382:3: ( ( operator | LEFT_PAREN )=>right= orRestriction )*
			loop37:
			while (true) {
				int alt37=2;
				int LA37_0 = input.LA(1);
				if ( (LA37_0==ID) ) {
					int LA37_2 = input.LA(2);
					if ( ((synpred8_DRL5Expressions()&&(((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) ) {
						alt37=1;
					}

				}
				else if ( (LA37_0==EQUALS) ) {
					int LA37_3 = input.LA(2);
					if ( (synpred8_DRL5Expressions()) ) {
						alt37=1;
					}

				}
				else if ( (LA37_0==NOT_EQUALS) ) {
					int LA37_4 = input.LA(2);
					if ( (synpred8_DRL5Expressions()) ) {
						alt37=1;
					}

				}
				else if ( (LA37_0==LESS) ) {
					int LA37_20 = input.LA(2);
					if ( (synpred8_DRL5Expressions()) ) {
						alt37=1;
					}

				}
				else if ( (LA37_0==GREATER) ) {
					int LA37_21 = input.LA(2);
					if ( (synpred8_DRL5Expressions()) ) {
						alt37=1;
					}

				}
				else if ( (LA37_0==TILDE) && (synpred8_DRL5Expressions())) {
					alt37=1;
				}
				else if ( (LA37_0==LESS_EQUALS) && (synpred8_DRL5Expressions())) {
					alt37=1;
				}
				else if ( (LA37_0==GREATER_EQUALS) && (synpred8_DRL5Expressions())) {
					alt37=1;
				}
				else if ( (LA37_0==LEFT_PAREN) && (synpred8_DRL5Expressions())) {
					alt37=1;
				}

				switch (alt37) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:382:5: ( operator | LEFT_PAREN )=>right= orRestriction
					{
					pushFollow(FOLLOW_orRestriction_in_relationalExpression1775);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:391:1: orRestriction returns [BaseDescr result] : left= andRestriction ( ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )=>lop= DOUBLE_PIPE (args= fullAnnotation[null] )? right= andRestriction )* ( EOF )? ;
	public final BaseDescr orRestriction() throws RecognitionException {
		BaseDescr result = null;


		Token lop=null;
		BaseDescr left =null;
		AnnotationDescr args =null;
		BaseDescr right =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:392:3: (left= andRestriction ( ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )=>lop= DOUBLE_PIPE (args= fullAnnotation[null] )? right= andRestriction )* ( EOF )? )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:392:5: left= andRestriction ( ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )=>lop= DOUBLE_PIPE (args= fullAnnotation[null] )? right= andRestriction )* ( EOF )?
			{
			pushFollow(FOLLOW_andRestriction_in_orRestriction1810);
			left=andRestriction();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:393:5: ( ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )=>lop= DOUBLE_PIPE (args= fullAnnotation[null] )? right= andRestriction )*
			loop39:
			while (true) {
				int alt39=2;
				int LA39_0 = input.LA(1);
				if ( (LA39_0==DOUBLE_PIPE) ) {
					int LA39_9 = input.LA(2);
					if ( (synpred9_DRL5Expressions()) ) {
						alt39=1;
					}

				}

				switch (alt39) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:393:7: ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )=>lop= DOUBLE_PIPE (args= fullAnnotation[null] )? right= andRestriction
					{
					lop=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_orRestriction1832); if (state.failed) return result;
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:393:79: (args= fullAnnotation[null] )?
					int alt38=2;
					int LA38_0 = input.LA(1);
					if ( (LA38_0==AT) ) {
						alt38=1;
					}
					switch (alt38) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:393:79: args= fullAnnotation[null]
							{
							pushFollow(FOLLOW_fullAnnotation_in_orRestriction1836);
							args=fullAnnotation(null);
							state._fsp--;
							if (state.failed) return result;
							}
							break;

					}

					pushFollow(FOLLOW_andRestriction_in_orRestriction1842);
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

			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:402:7: ( EOF )?
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
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:402:7: EOF
					{
					match(input,EOF,FOLLOW_EOF_in_orRestriction1861); if (state.failed) return result;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:405:1: andRestriction returns [BaseDescr result] : left= singleRestriction ( ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )=>lop= DOUBLE_AMPER (args= fullAnnotation[null] )? right= singleRestriction )* ;
	public final BaseDescr andRestriction() throws RecognitionException {
		BaseDescr result = null;


		Token lop=null;
		BaseDescr left =null;
		AnnotationDescr args =null;
		BaseDescr right =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:406:3: (left= singleRestriction ( ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )=>lop= DOUBLE_AMPER (args= fullAnnotation[null] )? right= singleRestriction )* )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:406:5: left= singleRestriction ( ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )=>lop= DOUBLE_AMPER (args= fullAnnotation[null] )? right= singleRestriction )*
			{
			pushFollow(FOLLOW_singleRestriction_in_andRestriction1881);
			left=singleRestriction();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:407:3: ( ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )=>lop= DOUBLE_AMPER (args= fullAnnotation[null] )? right= singleRestriction )*
			loop42:
			while (true) {
				int alt42=2;
				int LA42_0 = input.LA(1);
				if ( (LA42_0==DOUBLE_AMPER) ) {
					int LA42_9 = input.LA(2);
					if ( (synpred10_DRL5Expressions()) ) {
						alt42=1;
					}

				}

				switch (alt42) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:407:5: ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )=>lop= DOUBLE_AMPER (args= fullAnnotation[null] )? right= singleRestriction
					{
					lop=(Token)match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_andRestriction1901); if (state.failed) return result;
					if ( state.backtracking==0 ) { if ( isNotEOF() ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR ); }
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:409:13: (args= fullAnnotation[null] )?
					int alt41=2;
					int LA41_0 = input.LA(1);
					if ( (LA41_0==AT) ) {
						alt41=1;
					}
					switch (alt41) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:409:13: args= fullAnnotation[null]
							{
							pushFollow(FOLLOW_fullAnnotation_in_andRestriction1922);
							args=fullAnnotation(null);
							state._fsp--;
							if (state.failed) return result;
							}
							break;

					}

					pushFollow(FOLLOW_singleRestriction_in_andRestriction1927);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:421:1: singleRestriction returns [BaseDescr result] : (op= operator ( ( squareArguments shiftExpression )=>sa= squareArguments value= shiftExpression |value= shiftExpression ) | LEFT_PAREN or= orRestriction RIGHT_PAREN );
	public final BaseDescr singleRestriction() throws RecognitionException {
		BaseDescr result = null;


		ParserRuleReturnScope op =null;
		java.util.List<String> sa =null;
		ParserRuleReturnScope value =null;
		BaseDescr or =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:422:3: (op= operator ( ( squareArguments shiftExpression )=>sa= squareArguments value= shiftExpression |value= shiftExpression ) | LEFT_PAREN or= orRestriction RIGHT_PAREN )
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
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:422:6: op= operator ( ( squareArguments shiftExpression )=>sa= squareArguments value= shiftExpression |value= shiftExpression )
					{
					pushFollow(FOLLOW_operator_in_singleRestriction1963);
					op=operator();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); }
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:424:6: ( ( squareArguments shiftExpression )=>sa= squareArguments value= shiftExpression |value= shiftExpression )
					int alt43=2;
					int LA43_0 = input.LA(1);
					if ( (LA43_0==LEFT_SQUARE) ) {
						int LA43_1 = input.LA(2);
						if ( (synpred11_DRL5Expressions()) ) {
							alt43=1;
						}
						else if ( (true) ) {
							alt43=2;
						}

					}
					else if ( (LA43_0==BOOL||(LA43_0 >= DECIMAL && LA43_0 <= DECR)||LA43_0==FLOAT||LA43_0==HEX||(LA43_0 >= ID && LA43_0 <= INCR)||LA43_0==LEFT_PAREN||LA43_0==LESS||LA43_0==MINUS||LA43_0==NEGATION||LA43_0==NULL||LA43_0==PLUS||(LA43_0 >= STAR && LA43_0 <= TIME_INTERVAL)) ) {
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
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:424:8: ( squareArguments shiftExpression )=>sa= squareArguments value= shiftExpression
							{
							pushFollow(FOLLOW_squareArguments_in_singleRestriction1992);
							sa=squareArguments();
							state._fsp--;
							if (state.failed) return result;
							pushFollow(FOLLOW_shiftExpression_in_singleRestriction1996);
							value=shiftExpression();
							state._fsp--;
							if (state.failed) return result;
							}
							break;
						case 2 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:425:10: value= shiftExpression
							{
							pushFollow(FOLLOW_shiftExpression_in_singleRestriction2009);
							value=shiftExpression();
							state._fsp--;
							if (state.failed) return result;
							}
							break;

					}

					if ( state.backtracking==0 ) { if( buildDescr  ) {
					               BaseDescr descr = ( (value!=null?((DRL5Expressions.shiftExpression_return)value).result:null) != null &&
					                                 ( (!((value!=null?((DRL5Expressions.shiftExpression_return)value).result:null) instanceof AtomicExprDescr)) ||
					                                   ((value!=null?input.toString(value.start,value.stop):null).equals(((AtomicExprDescr)(value!=null?((DRL5Expressions.shiftExpression_return)value).result:null)).getExpression())) )) ?
							                    (value!=null?((DRL5Expressions.shiftExpression_return)value).result:null) :
							                    new AtomicExprDescr( (value!=null?input.toString(value.start,value.stop):null) ) ;
					               result = new RelationalExprDescr( (op!=null?((DRL5Expressions.operator_return)op).opr:null), (op!=null?((DRL5Expressions.operator_return)op).negated:false), sa, relationalExpression_stack.peek().lsd, descr );
						       if( relationalExpression_stack.peek().lsd instanceof BindingDescr ) {
						           relationalExpression_stack.peek().lsd = new AtomicExprDescr( ((BindingDescr)relationalExpression_stack.peek().lsd).getExpression() );
						       }
					           }
					           helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_END );
					         }
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:440:6: LEFT_PAREN or= orRestriction RIGHT_PAREN
					{
					match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_singleRestriction2034); if (state.failed) return result;
					pushFollow(FOLLOW_orRestriction_in_singleRestriction2038);
					or=orRestriction();
					state._fsp--;
					if (state.failed) return result;
					match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_singleRestriction2040); if (state.failed) return result;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:445:1: shiftExpression returns [BaseDescr result] : left= additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )* ;
	public final DRL5Expressions.shiftExpression_return shiftExpression() throws RecognitionException {
		DRL5Expressions.shiftExpression_return retval = new DRL5Expressions.shiftExpression_return();
		retval.start = input.LT(1);

		BaseDescr left =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:446:3: (left= additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )* )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:446:5: left= additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )*
			{
			pushFollow(FOLLOW_additiveExpression_in_shiftExpression2064);
			left=additiveExpression();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) { if( buildDescr  ) { retval.result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:447:5: ( ( shiftOp )=> shiftOp additiveExpression )*
			loop45:
			while (true) {
				int alt45=2;
				int LA45_0 = input.LA(1);
				if ( (LA45_0==LESS) ) {
					int LA45_6 = input.LA(2);
					if ( (synpred12_DRL5Expressions()) ) {
						alt45=1;
					}

				}
				else if ( (LA45_0==GREATER) ) {
					int LA45_7 = input.LA(2);
					if ( (synpred12_DRL5Expressions()) ) {
						alt45=1;
					}

				}

				switch (alt45) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:447:7: ( shiftOp )=> shiftOp additiveExpression
					{
					pushFollow(FOLLOW_shiftOp_in_shiftExpression2078);
					shiftOp();
					state._fsp--;
					if (state.failed) return retval;
					pushFollow(FOLLOW_additiveExpression_in_shiftExpression2080);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:450:1: shiftOp : ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER ) ;
	public final void shiftOp() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:451:5: ( ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER ) )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:451:7: ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
			{
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:451:7: ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
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
					else if ( (LA46_3==EOF||LA46_3==BOOL||(LA46_3 >= DECIMAL && LA46_3 <= DECR)||LA46_3==FLOAT||LA46_3==HEX||(LA46_3 >= ID && LA46_3 <= INCR)||(LA46_3 >= LEFT_PAREN && LA46_3 <= LESS)||LA46_3==MINUS||LA46_3==NEGATION||LA46_3==NULL||LA46_3==PLUS||(LA46_3 >= STAR && LA46_3 <= TIME_INTERVAL)) ) {
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
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:451:9: LESS LESS
					{
					match(input,LESS,FOLLOW_LESS_in_shiftOp2100); if (state.failed) return;
					match(input,LESS,FOLLOW_LESS_in_shiftOp2102); if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:452:11: GREATER GREATER GREATER
					{
					match(input,GREATER,FOLLOW_GREATER_in_shiftOp2114); if (state.failed) return;
					match(input,GREATER,FOLLOW_GREATER_in_shiftOp2116); if (state.failed) return;
					match(input,GREATER,FOLLOW_GREATER_in_shiftOp2118); if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:453:11: GREATER GREATER
					{
					match(input,GREATER,FOLLOW_GREATER_in_shiftOp2130); if (state.failed) return;
					match(input,GREATER,FOLLOW_GREATER_in_shiftOp2132); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:456:1: additiveExpression returns [BaseDescr result] : left= multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )* ;
	public final BaseDescr additiveExpression() throws RecognitionException {
		BaseDescr result = null;


		BaseDescr left =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:457:5: (left= multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )* )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:457:9: left= multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*
			{
			pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression2160);
			left=multiplicativeExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:458:9: ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*
			loop47:
			while (true) {
				int alt47=2;
				int LA47_0 = input.LA(1);
				if ( (LA47_0==MINUS||LA47_0==PLUS) && (synpred13_DRL5Expressions())) {
					alt47=1;
				}

				switch (alt47) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:458:11: ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression
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
					pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression2189);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:461:1: multiplicativeExpression returns [BaseDescr result] : left= unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )* ;
	public final BaseDescr multiplicativeExpression() throws RecognitionException {
		BaseDescr result = null;


		BaseDescr left =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:462:5: (left= unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )* )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:462:9: left= unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )*
			{
			pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression2217);
			left=unaryExpression();
			state._fsp--;
			if (state.failed) return result;
			if ( state.backtracking==0 ) { if( buildDescr  ) { result = left; } }
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:463:7: ( ( STAR | DIV | MOD ) unaryExpression )*
			loop48:
			while (true) {
				int alt48=2;
				int LA48_0 = input.LA(1);
				if ( (LA48_0==DIV||LA48_0==MOD||LA48_0==STAR) ) {
					alt48=1;
				}

				switch (alt48) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:463:9: ( STAR | DIV | MOD ) unaryExpression
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
					pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression2243);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:466:1: unaryExpression returns [BaseDescr result] : ( PLUS ue= unaryExpression | MINUS ue= unaryExpression | INCR primary | DECR primary |left= unaryExpressionNotPlusMinus );
	public final BaseDescr unaryExpression() throws RecognitionException {
		BaseDescr result = null;


		BaseDescr ue =null;
		ParserRuleReturnScope left =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:467:5: ( PLUS ue= unaryExpression | MINUS ue= unaryExpression | INCR primary | DECR primary |left= unaryExpressionNotPlusMinus )
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
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:467:9: PLUS ue= unaryExpression
					{
					match(input,PLUS,FOLLOW_PLUS_in_unaryExpression2269); if (state.failed) return result;
					pushFollow(FOLLOW_unaryExpression_in_unaryExpression2273);
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
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:474:7: MINUS ue= unaryExpression
					{
					match(input,MINUS,FOLLOW_MINUS_in_unaryExpression2291); if (state.failed) return result;
					pushFollow(FOLLOW_unaryExpression_in_unaryExpression2295);
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
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:481:9: INCR primary
					{
					match(input,INCR,FOLLOW_INCR_in_unaryExpression2315); if (state.failed) return result;
					pushFollow(FOLLOW_primary_in_unaryExpression2317);
					primary();
					state._fsp--;
					if (state.failed) return result;
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:482:9: DECR primary
					{
					match(input,DECR,FOLLOW_DECR_in_unaryExpression2327); if (state.failed) return result;
					pushFollow(FOLLOW_primary_in_unaryExpression2329);
					primary();
					state._fsp--;
					if (state.failed) return result;
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:483:9: left= unaryExpressionNotPlusMinus
					{
					pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression2341);
					left=unaryExpressionNotPlusMinus();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { if( buildDescr ) { result = (left!=null?((DRL5Expressions.unaryExpressionNotPlusMinus_return)left).result:null); } }
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:486:1: unaryExpressionNotPlusMinus returns [BaseDescr result] : ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )? left= primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? );
	public final DRL5Expressions.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus() throws RecognitionException {
		DRL5Expressions.unaryExpressionNotPlusMinus_return retval = new DRL5Expressions.unaryExpressionNotPlusMinus_return();
		retval.start = input.LT(1);

		Token var=null;
		Token COLON9=null;
		Token UNIFY10=null;
		BaseDescr left =null;

		 boolean isLeft = false; BindingDescr bind = null;
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:488:5: ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )? left= primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? )
			int alt53=4;
			switch ( input.LA(1) ) {
			case TILDE:
				{
				alt53=1;
				}
				break;
			case NEGATION:
				{
				alt53=2;
				}
				break;
			case LEFT_PAREN:
				{
				int LA53_3 = input.LA(2);
				if ( (synpred14_DRL5Expressions()) ) {
					alt53=3;
				}
				else if ( (true) ) {
					alt53=4;
				}

				}
				break;
			case BOOL:
			case DECIMAL:
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
				alt53=4;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 53, 0, input);
				throw nvae;
			}
			switch (alt53) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:488:9: TILDE unaryExpression
					{
					match(input,TILDE,FOLLOW_TILDE_in_unaryExpressionNotPlusMinus2371); if (state.failed) return retval;
					pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2373);
					unaryExpression();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:489:8: NEGATION unaryExpression
					{
					match(input,NEGATION,FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus2382); if (state.failed) return retval;
					pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2384);
					unaryExpression();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:490:9: ( castExpression )=> castExpression
					{
					pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus2398);
					castExpression();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:491:9: ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )? left= primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )?
					{
					if ( state.backtracking==0 ) { isLeft = helper.getLeftMostExpr() == null;}
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:492:9: ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )?
					int alt50=3;
					int LA50_0 = input.LA(1);
					if ( (LA50_0==ID) ) {
						int LA50_1 = input.LA(2);
						if ( (LA50_1==COLON) ) {
							int LA50_3 = input.LA(3);
							if ( ((inMap == 0 && ternOp == 0 && input.LA(2) == DRL5Lexer.COLON)) ) {
								alt50=1;
							}
						}
						else if ( (LA50_1==UNIFY) ) {
							alt50=2;
						}
					}
					switch (alt50) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:492:11: ({...}? (var= ID COLON ) )
							{
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:492:11: ({...}? (var= ID COLON ) )
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:492:12: {...}? (var= ID COLON )
							{
							if ( !((inMap == 0 && ternOp == 0 && input.LA(2) == DRL5Lexer.COLON)) ) {
								if (state.backtracking>0) {state.failed=true; return retval;}
								throw new FailedPredicateException(input, "unaryExpressionNotPlusMinus", "inMap == 0 && ternOp == 0 && input.LA(2) == DRL5Lexer.COLON");
							}
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:492:75: (var= ID COLON )
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:492:76: var= ID COLON
							{
							var=(Token)match(input,ID,FOLLOW_ID_in_unaryExpressionNotPlusMinus2426); if (state.failed) return retval;
							COLON9=(Token)match(input,COLON,FOLLOW_COLON_in_unaryExpressionNotPlusMinus2428); if (state.failed) return retval;
							if ( state.backtracking==0 ) { hasBindings = true; helper.emit(var, DroolsEditorType.IDENTIFIER_VARIABLE); helper.emit(COLON9, DroolsEditorType.SYMBOL); if( buildDescr ) { bind = new BindingDescr((var!=null?var.getText():null), null, false); helper.setStart( bind, var ); } }
							}

							}

							}
							break;
						case 2 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:494:11: ({...}? (var= ID UNIFY ) )
							{
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:494:11: ({...}? (var= ID UNIFY ) )
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:494:12: {...}? (var= ID UNIFY )
							{
							if ( !((inMap == 0 && ternOp == 0 && input.LA(2) == DRL5Lexer.UNIFY)) ) {
								if (state.backtracking>0) {state.failed=true; return retval;}
								throw new FailedPredicateException(input, "unaryExpressionNotPlusMinus", "inMap == 0 && ternOp == 0 && input.LA(2) == DRL5Lexer.UNIFY");
							}
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:494:75: (var= ID UNIFY )
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:494:76: var= ID UNIFY
							{
							var=(Token)match(input,ID,FOLLOW_ID_in_unaryExpressionNotPlusMinus2467); if (state.failed) return retval;
							UNIFY10=(Token)match(input,UNIFY,FOLLOW_UNIFY_in_unaryExpressionNotPlusMinus2469); if (state.failed) return retval;
							if ( state.backtracking==0 ) { hasBindings = true; helper.emit(var, DroolsEditorType.IDENTIFIER_VARIABLE); helper.emit(UNIFY10, DroolsEditorType.SYMBOL); if( buildDescr ) { bind = new BindingDescr((var!=null?var.getText():null), null, true); helper.setStart( bind, var ); } }
							}

							}

							}
							break;

					}

					pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus2514);
					left=primary();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) { if( buildDescr ) { retval.result = left; } }
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:498:9: ( ( selector )=> selector )*
					loop51:
					while (true) {
						int alt51=2;
						int LA51_0 = input.LA(1);
						if ( (LA51_0==DOT) && (synpred15_DRL5Expressions())) {
							alt51=1;
						}
						else if ( (LA51_0==LEFT_SQUARE) && (synpred15_DRL5Expressions())) {
							alt51=1;
						}

						switch (alt51) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:498:10: ( selector )=> selector
							{
							pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus2531);
							selector();
							state._fsp--;
							if (state.failed) return retval;
							}
							break;

						default :
							break loop51;
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
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:517:9: ( ( INCR | DECR )=> ( INCR | DECR ) )?
					int alt52=2;
					int LA52_0 = input.LA(1);
					if ( (LA52_0==DECR||LA52_0==INCR) && (synpred16_DRL5Expressions())) {
						alt52=1;
					}
					switch (alt52) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:517:10: ( INCR | DECR )=> ( INCR | DECR )
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:520:1: castExpression : ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN expr= unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus );
	public final void castExpression() throws RecognitionException {
		BaseDescr expr =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:521:5: ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN expr= unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus )
			int alt54=2;
			int LA54_0 = input.LA(1);
			if ( (LA54_0==LEFT_PAREN) ) {
				int LA54_1 = input.LA(2);
				if ( (synpred17_DRL5Expressions()) ) {
					alt54=1;
				}
				else if ( (synpred18_DRL5Expressions()) ) {
					alt54=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 54, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 54, 0, input);
				throw nvae;
			}

			switch (alt54) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:521:8: ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN expr= unaryExpression
					{
					match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression2593); if (state.failed) return;
					pushFollow(FOLLOW_primitiveType_in_castExpression2595);
					primitiveType();
					state._fsp--;
					if (state.failed) return;
					match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression2597); if (state.failed) return;
					pushFollow(FOLLOW_unaryExpression_in_castExpression2601);
					expr=unaryExpression();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:522:8: ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus
					{
					match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression2618); if (state.failed) return;
					pushFollow(FOLLOW_type_in_castExpression2620);
					type();
					state._fsp--;
					if (state.failed) return;
					match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression2622); if (state.failed) return;
					pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression2624);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:525:1: primitiveType : ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key );
	public final void primitiveType() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:526:5: ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key )
			int alt55=8;
			int LA55_0 = input.LA(1);
			if ( (LA55_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))))) {
				int LA55_1 = input.LA(2);
				if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))) ) {
					alt55=1;
				}
				else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))) ) {
					alt55=2;
				}
				else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))) ) {
					alt55=3;
				}
				else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))) ) {
					alt55=4;
				}
				else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))) ) {
					alt55=5;
				}
				else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))) ) {
					alt55=6;
				}
				else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))) ) {
					alt55=7;
				}
				else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))) ) {
					alt55=8;
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

			switch (alt55) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:526:9: boolean_key
					{
					pushFollow(FOLLOW_boolean_key_in_primitiveType2643);
					boolean_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:527:7: char_key
					{
					pushFollow(FOLLOW_char_key_in_primitiveType2651);
					char_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:528:7: byte_key
					{
					pushFollow(FOLLOW_byte_key_in_primitiveType2659);
					byte_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:529:7: short_key
					{
					pushFollow(FOLLOW_short_key_in_primitiveType2667);
					short_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:530:7: int_key
					{
					pushFollow(FOLLOW_int_key_in_primitiveType2675);
					int_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 6 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:531:7: long_key
					{
					pushFollow(FOLLOW_long_key_in_primitiveType2683);
					long_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 7 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:532:7: float_key
					{
					pushFollow(FOLLOW_float_key_in_primitiveType2691);
					float_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 8 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:533:7: double_key
					{
					pushFollow(FOLLOW_double_key_in_primitiveType2699);
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



	// $ANTLR start "primary"
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:536:1: primary returns [BaseDescr result] : ( ( parExpression )=>expr= parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=>i1= ID ( ( ( DOT ID )=> DOT i2= ID ) | ( ( SHARP ID )=> SHARP i2= ID ) | ( ( HASH ID )=> HASH i2= ID ) | ( ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID ) )* ( ( identifierSuffix )=> identifierSuffix )? );
	public final BaseDescr primary() throws RecognitionException {
		BaseDescr result = null;


		Token i1=null;
		Token i2=null;
		Token DOT12=null;
		Token SHARP13=null;
		Token HASH14=null;
		Token NULL_SAFE_DOT15=null;
		BaseDescr expr =null;
		ParserRuleReturnScope literal11 =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:537:5: ( ( parExpression )=>expr= parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=>i1= ID ( ( ( DOT ID )=> DOT i2= ID ) | ( ( SHARP ID )=> SHARP i2= ID ) | ( ( HASH ID )=> HASH i2= ID ) | ( ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID ) )* ( ( identifierSuffix )=> identifierSuffix )? )
			int alt60=9;
			int LA60_0 = input.LA(1);
			if ( (LA60_0==LEFT_PAREN) && (synpred19_DRL5Expressions())) {
				alt60=1;
			}
			else if ( (LA60_0==LESS) && (synpred20_DRL5Expressions())) {
				alt60=2;
			}
			else if ( (LA60_0==STRING) && (synpred21_DRL5Expressions())) {
				alt60=3;
			}
			else if ( (LA60_0==DECIMAL) && (synpred21_DRL5Expressions())) {
				alt60=3;
			}
			else if ( (LA60_0==HEX) && (synpred21_DRL5Expressions())) {
				alt60=3;
			}
			else if ( (LA60_0==FLOAT) && (synpred21_DRL5Expressions())) {
				alt60=3;
			}
			else if ( (LA60_0==BOOL) && (synpred21_DRL5Expressions())) {
				alt60=3;
			}
			else if ( (LA60_0==NULL) && (synpred21_DRL5Expressions())) {
				alt60=3;
			}
			else if ( (LA60_0==TIME_INTERVAL) && (synpred21_DRL5Expressions())) {
				alt60=3;
			}
			else if ( (LA60_0==STAR) && (synpred21_DRL5Expressions())) {
				alt60=3;
			}
			else if ( (LA60_0==ID) ) {
				int LA60_11 = input.LA(2);
				if ( ((((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))&&synpred22_DRL5Expressions())) ) {
					alt60=4;
				}
				else if ( ((synpred23_DRL5Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.NEW))))) ) {
					alt60=5;
				}
				else if ( (((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))&&synpred24_DRL5Expressions())) ) {
					alt60=6;
				}
				else if ( (synpred27_DRL5Expressions()) ) {
					alt60=9;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return result;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 60, 11, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA60_0==LEFT_SQUARE) ) {
				int LA60_12 = input.LA(2);
				if ( (synpred25_DRL5Expressions()) ) {
					alt60=7;
				}
				else if ( (synpred26_DRL5Expressions()) ) {
					alt60=8;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return result;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 60, 12, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return result;}
				NoViableAltException nvae =
					new NoViableAltException("", 60, 0, input);
				throw nvae;
			}

			switch (alt60) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:537:7: ( parExpression )=>expr= parExpression
					{
					pushFollow(FOLLOW_parExpression_in_primary2727);
					expr=parExpression();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) {  if( buildDescr  ) { result = expr; }  }
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:538:9: ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments )
					{
					pushFollow(FOLLOW_nonWildcardTypeArguments_in_primary2744);
					nonWildcardTypeArguments();
					state._fsp--;
					if (state.failed) return result;
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:538:63: ( explicitGenericInvocationSuffix | this_key arguments )
					int alt56=2;
					int LA56_0 = input.LA(1);
					if ( (LA56_0==ID) ) {
						int LA56_1 = input.LA(2);
						if ( (!((((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))))) ) {
							alt56=1;
						}
						else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))) ) {
							alt56=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return result;}
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

					else {
						if (state.backtracking>0) {state.failed=true; return result;}
						NoViableAltException nvae =
							new NoViableAltException("", 56, 0, input);
						throw nvae;
					}

					switch (alt56) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:538:64: explicitGenericInvocationSuffix
							{
							pushFollow(FOLLOW_explicitGenericInvocationSuffix_in_primary2747);
							explicitGenericInvocationSuffix();
							state._fsp--;
							if (state.failed) return result;
							}
							break;
						case 2 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:538:98: this_key arguments
							{
							pushFollow(FOLLOW_this_key_in_primary2751);
							this_key();
							state._fsp--;
							if (state.failed) return result;
							pushFollow(FOLLOW_arguments_in_primary2753);
							arguments();
							state._fsp--;
							if (state.failed) return result;
							}
							break;

					}

					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:539:9: ( literal )=> literal
					{
					pushFollow(FOLLOW_literal_in_primary2769);
					literal11=literal();
					state._fsp--;
					if (state.failed) return result;
					if ( state.backtracking==0 ) { if( buildDescr  ) { result = new AtomicExprDescr( (literal11!=null?input.toString(literal11.start,literal11.stop):null), true ); }  }
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:541:9: ( super_key )=> super_key superSuffix
					{
					pushFollow(FOLLOW_super_key_in_primary2791);
					super_key();
					state._fsp--;
					if (state.failed) return result;
					pushFollow(FOLLOW_superSuffix_in_primary2793);
					superSuffix();
					state._fsp--;
					if (state.failed) return result;
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:542:9: ( new_key )=> new_key creator
					{
					pushFollow(FOLLOW_new_key_in_primary2808);
					new_key();
					state._fsp--;
					if (state.failed) return result;
					pushFollow(FOLLOW_creator_in_primary2810);
					creator();
					state._fsp--;
					if (state.failed) return result;
					}
					break;
				case 6 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:543:9: ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key
					{
					pushFollow(FOLLOW_primitiveType_in_primary2825);
					primitiveType();
					state._fsp--;
					if (state.failed) return result;
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:543:41: ( LEFT_SQUARE RIGHT_SQUARE )*
					loop57:
					while (true) {
						int alt57=2;
						int LA57_0 = input.LA(1);
						if ( (LA57_0==LEFT_SQUARE) ) {
							alt57=1;
						}

						switch (alt57) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:543:42: LEFT_SQUARE RIGHT_SQUARE
							{
							match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_primary2828); if (state.failed) return result;
							match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_primary2830); if (state.failed) return result;
							}
							break;

						default :
							break loop57;
						}
					}

					match(input,DOT,FOLLOW_DOT_in_primary2834); if (state.failed) return result;
					pushFollow(FOLLOW_class_key_in_primary2836);
					class_key();
					state._fsp--;
					if (state.failed) return result;
					}
					break;
				case 7 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:545:9: ( inlineMapExpression )=> inlineMapExpression
					{
					pushFollow(FOLLOW_inlineMapExpression_in_primary2856);
					inlineMapExpression();
					state._fsp--;
					if (state.failed) return result;
					}
					break;
				case 8 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:546:9: ( inlineListExpression )=> inlineListExpression
					{
					pushFollow(FOLLOW_inlineListExpression_in_primary2871);
					inlineListExpression();
					state._fsp--;
					if (state.failed) return result;
					}
					break;
				case 9 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:547:9: ( ID )=>i1= ID ( ( ( DOT ID )=> DOT i2= ID ) | ( ( SHARP ID )=> SHARP i2= ID ) | ( ( HASH ID )=> HASH i2= ID ) | ( ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID ) )* ( ( identifierSuffix )=> identifierSuffix )?
					{
					i1=(Token)match(input,ID,FOLLOW_ID_in_primary2887); if (state.failed) return result;
					if ( state.backtracking==0 ) { helper.emit(i1, DroolsEditorType.IDENTIFIER); }
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:548:9: ( ( ( DOT ID )=> DOT i2= ID ) | ( ( SHARP ID )=> SHARP i2= ID ) | ( ( HASH ID )=> HASH i2= ID ) | ( ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID ) )*
					loop58:
					while (true) {
						int alt58=5;
						int LA58_0 = input.LA(1);
						if ( (LA58_0==DOT) ) {
							int LA58_2 = input.LA(2);
							if ( (LA58_2==ID) ) {
								int LA58_6 = input.LA(3);
								if ( (synpred28_DRL5Expressions()) ) {
									alt58=1;
								}

							}

						}
						else if ( (LA58_0==SHARP) && (synpred29_DRL5Expressions())) {
							alt58=2;
						}
						else if ( (LA58_0==HASH) && (synpred30_DRL5Expressions())) {
							alt58=3;
						}
						else if ( (LA58_0==NULL_SAFE_DOT) && (synpred31_DRL5Expressions())) {
							alt58=4;
						}

						switch (alt58) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:549:13: ( ( DOT ID )=> DOT i2= ID )
							{
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:549:13: ( ( DOT ID )=> DOT i2= ID )
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:549:15: ( DOT ID )=> DOT i2= ID
							{
							DOT12=(Token)match(input,DOT,FOLLOW_DOT_in_primary2921); if (state.failed) return result;
							i2=(Token)match(input,ID,FOLLOW_ID_in_primary2925); if (state.failed) return result;
							if ( state.backtracking==0 ) { helper.emit(DOT12, DroolsEditorType.SYMBOL); helper.emit(i2, DroolsEditorType.IDENTIFIER); }
							}

							}
							break;
						case 2 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:551:13: ( ( SHARP ID )=> SHARP i2= ID )
							{
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:551:13: ( ( SHARP ID )=> SHARP i2= ID )
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:551:15: ( SHARP ID )=> SHARP i2= ID
							{
							SHARP13=(Token)match(input,SHARP,FOLLOW_SHARP_in_primary2965); if (state.failed) return result;
							i2=(Token)match(input,ID,FOLLOW_ID_in_primary2969); if (state.failed) return result;
							if ( state.backtracking==0 ) { helper.emit(SHARP13, DroolsEditorType.SYMBOL); helper.emit(i2, DroolsEditorType.IDENTIFIER); }
							}

							}
							break;
						case 3 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:553:13: ( ( HASH ID )=> HASH i2= ID )
							{
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:553:13: ( ( HASH ID )=> HASH i2= ID )
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:553:15: ( HASH ID )=> HASH i2= ID
							{
							HASH14=(Token)match(input,HASH,FOLLOW_HASH_in_primary3009); if (state.failed) return result;
							i2=(Token)match(input,ID,FOLLOW_ID_in_primary3013); if (state.failed) return result;
							if ( state.backtracking==0 ) { helper.emit(HASH14, DroolsEditorType.SYMBOL); helper.emit(i2, DroolsEditorType.IDENTIFIER); }
							}

							}
							break;
						case 4 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:555:13: ( ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID )
							{
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:555:13: ( ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID )
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:555:15: ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID
							{
							NULL_SAFE_DOT15=(Token)match(input,NULL_SAFE_DOT,FOLLOW_NULL_SAFE_DOT_in_primary3053); if (state.failed) return result;
							i2=(Token)match(input,ID,FOLLOW_ID_in_primary3057); if (state.failed) return result;
							if ( state.backtracking==0 ) { helper.emit(NULL_SAFE_DOT15, DroolsEditorType.SYMBOL); helper.emit(i2, DroolsEditorType.IDENTIFIER); }
							}

							}
							break;

						default :
							break loop58;
						}
					}

					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:556:12: ( ( identifierSuffix )=> identifierSuffix )?
					int alt59=2;
					int LA59_0 = input.LA(1);
					if ( (LA59_0==LEFT_SQUARE) ) {
						int LA59_1 = input.LA(2);
						if ( (synpred32_DRL5Expressions()) ) {
							alt59=1;
						}
					}
					else if ( (LA59_0==LEFT_PAREN) ) {
						int LA59_2 = input.LA(2);
						if ( (synpred32_DRL5Expressions()) ) {
							alt59=1;
						}
					}
					switch (alt59) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:556:13: ( identifierSuffix )=> identifierSuffix
							{
							pushFollow(FOLLOW_identifierSuffix_in_primary3079);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:559:1: inlineListExpression : LEFT_SQUARE ( expressionList )? RIGHT_SQUARE ;
	public final void inlineListExpression() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:560:5: ( LEFT_SQUARE ( expressionList )? RIGHT_SQUARE )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:560:9: LEFT_SQUARE ( expressionList )? RIGHT_SQUARE
			{
			match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_inlineListExpression3100); if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:560:21: ( expressionList )?
			int alt61=2;
			int LA61_0 = input.LA(1);
			if ( (LA61_0==BOOL||(LA61_0 >= DECIMAL && LA61_0 <= DECR)||LA61_0==FLOAT||LA61_0==HEX||(LA61_0 >= ID && LA61_0 <= INCR)||(LA61_0 >= LEFT_PAREN && LA61_0 <= LESS)||LA61_0==MINUS||LA61_0==NEGATION||LA61_0==NULL||LA61_0==PLUS||(LA61_0 >= STAR && LA61_0 <= TIME_INTERVAL)) ) {
				alt61=1;
			}
			switch (alt61) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:560:21: expressionList
					{
					pushFollow(FOLLOW_expressionList_in_inlineListExpression3102);
					expressionList();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_inlineListExpression3105); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:563:1: inlineMapExpression : LEFT_SQUARE mapExpressionList RIGHT_SQUARE ;
	public final void inlineMapExpression() throws RecognitionException {
		 inMap++; 
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:565:5: ( LEFT_SQUARE mapExpressionList RIGHT_SQUARE )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:565:7: LEFT_SQUARE mapExpressionList RIGHT_SQUARE
			{
			match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_inlineMapExpression3126); if (state.failed) return;
			pushFollow(FOLLOW_mapExpressionList_in_inlineMapExpression3128);
			mapExpressionList();
			state._fsp--;
			if (state.failed) return;
			match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_inlineMapExpression3130); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:569:1: mapExpressionList : mapEntry ( COMMA mapEntry )* ;
	public final void mapExpressionList() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:570:5: ( mapEntry ( COMMA mapEntry )* )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:570:7: mapEntry ( COMMA mapEntry )*
			{
			pushFollow(FOLLOW_mapEntry_in_mapExpressionList3151);
			mapEntry();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:570:16: ( COMMA mapEntry )*
			loop62:
			while (true) {
				int alt62=2;
				int LA62_0 = input.LA(1);
				if ( (LA62_0==COMMA) ) {
					alt62=1;
				}

				switch (alt62) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:570:17: COMMA mapEntry
					{
					match(input,COMMA,FOLLOW_COMMA_in_mapExpressionList3154); if (state.failed) return;
					pushFollow(FOLLOW_mapEntry_in_mapExpressionList3156);
					mapEntry();
					state._fsp--;
					if (state.failed) return;
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
	}
	// $ANTLR end "mapExpressionList"



	// $ANTLR start "mapEntry"
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:573:1: mapEntry : expression COLON expression ;
	public final void mapEntry() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:574:5: ( expression COLON expression )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:574:7: expression COLON expression
			{
			pushFollow(FOLLOW_expression_in_mapEntry3175);
			expression();
			state._fsp--;
			if (state.failed) return;
			match(input,COLON,FOLLOW_COLON_in_mapEntry3177); if (state.failed) return;
			pushFollow(FOLLOW_expression_in_mapEntry3179);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:577:1: parExpression returns [BaseDescr result] : LEFT_PAREN expr= expression RIGHT_PAREN ;
	public final BaseDescr parExpression() throws RecognitionException {
		BaseDescr result = null;


		ParserRuleReturnScope expr =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:578:5: ( LEFT_PAREN expr= expression RIGHT_PAREN )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:578:7: LEFT_PAREN expr= expression RIGHT_PAREN
			{
			match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_parExpression3200); if (state.failed) return result;
			pushFollow(FOLLOW_expression_in_parExpression3204);
			expr=expression();
			state._fsp--;
			if (state.failed) return result;
			match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_parExpression3206); if (state.failed) return result;
			if ( state.backtracking==0 ) {  if( buildDescr  ) {
			               result = (expr!=null?((DRL5Expressions.expression_return)expr).result:null);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:588:1: identifierSuffix : ( ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments );
	public final void identifierSuffix() throws RecognitionException {
		Token LEFT_SQUARE16=null;
		Token RIGHT_SQUARE17=null;
		Token DOT18=null;
		Token LEFT_SQUARE19=null;
		Token RIGHT_SQUARE20=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:589:5: ( ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments )
			int alt65=3;
			int LA65_0 = input.LA(1);
			if ( (LA65_0==LEFT_SQUARE) ) {
				int LA65_1 = input.LA(2);
				if ( (LA65_1==RIGHT_SQUARE) && (synpred33_DRL5Expressions())) {
					alt65=1;
				}
				else if ( (LA65_1==BOOL||(LA65_1 >= DECIMAL && LA65_1 <= DECR)||LA65_1==FLOAT||LA65_1==HEX||(LA65_1 >= ID && LA65_1 <= INCR)||(LA65_1 >= LEFT_PAREN && LA65_1 <= LESS)||LA65_1==MINUS||LA65_1==NEGATION||LA65_1==NULL||LA65_1==PLUS||(LA65_1 >= STAR && LA65_1 <= TIME_INTERVAL)) ) {
					alt65=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 65, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA65_0==LEFT_PAREN) ) {
				alt65=3;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 65, 0, input);
				throw nvae;
			}

			switch (alt65) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:589:7: ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key
					{
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:589:35: ( LEFT_SQUARE RIGHT_SQUARE )+
					int cnt63=0;
					loop63:
					while (true) {
						int alt63=2;
						int LA63_0 = input.LA(1);
						if ( (LA63_0==LEFT_SQUARE) ) {
							alt63=1;
						}

						switch (alt63) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:589:36: LEFT_SQUARE RIGHT_SQUARE
							{
							LEFT_SQUARE16=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_identifierSuffix3240); if (state.failed) return;
							if ( state.backtracking==0 ) { helper.emit(LEFT_SQUARE16, DroolsEditorType.SYMBOL); }
							RIGHT_SQUARE17=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_identifierSuffix3281); if (state.failed) return;
							if ( state.backtracking==0 ) { helper.emit(RIGHT_SQUARE17, DroolsEditorType.SYMBOL); }
							}
							break;

						default :
							if ( cnt63 >= 1 ) break loop63;
							if (state.backtracking>0) {state.failed=true; return;}
							EarlyExitException eee = new EarlyExitException(63, input);
							throw eee;
						}
						cnt63++;
					}

					DOT18=(Token)match(input,DOT,FOLLOW_DOT_in_identifierSuffix3325); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(DOT18, DroolsEditorType.SYMBOL); }
					pushFollow(FOLLOW_class_key_in_identifierSuffix3329);
					class_key();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:592:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
					{
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:592:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
					int cnt64=0;
					loop64:
					while (true) {
						int alt64=2;
						int LA64_0 = input.LA(1);
						if ( (LA64_0==LEFT_SQUARE) ) {
							int LA64_36 = input.LA(2);
							if ( (synpred34_DRL5Expressions()) ) {
								alt64=1;
							}

						}

						switch (alt64) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:592:8: ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE
							{
							LEFT_SQUARE19=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_identifierSuffix3344); if (state.failed) return;
							if ( state.backtracking==0 ) { helper.emit(LEFT_SQUARE19, DroolsEditorType.SYMBOL); }
							pushFollow(FOLLOW_expression_in_identifierSuffix3374);
							expression();
							state._fsp--;
							if (state.failed) return;
							RIGHT_SQUARE20=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_identifierSuffix3402); if (state.failed) return;
							if ( state.backtracking==0 ) { helper.emit(RIGHT_SQUARE20, DroolsEditorType.SYMBOL); }
							}
							break;

						default :
							if ( cnt64 >= 1 ) break loop64;
							if (state.backtracking>0) {state.failed=true; return;}
							EarlyExitException eee = new EarlyExitException(64, input);
							throw eee;
						}
						cnt64++;
					}

					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:595:9: arguments
					{
					pushFollow(FOLLOW_arguments_in_identifierSuffix3418);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:603:1: creator : ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) ;
	public final void creator() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:604:5: ( ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:604:7: ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest )
			{
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:604:7: ( nonWildcardTypeArguments )?
			int alt66=2;
			int LA66_0 = input.LA(1);
			if ( (LA66_0==LESS) ) {
				alt66=1;
			}
			switch (alt66) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:604:7: nonWildcardTypeArguments
					{
					pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator3440);
					nonWildcardTypeArguments();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			pushFollow(FOLLOW_createdName_in_creator3443);
			createdName();
			state._fsp--;
			if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:605:9: ( arrayCreatorRest | classCreatorRest )
			int alt67=2;
			int LA67_0 = input.LA(1);
			if ( (LA67_0==LEFT_SQUARE) ) {
				alt67=1;
			}
			else if ( (LA67_0==LEFT_PAREN) ) {
				alt67=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 67, 0, input);
				throw nvae;
			}

			switch (alt67) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:605:10: arrayCreatorRest
					{
					pushFollow(FOLLOW_arrayCreatorRest_in_creator3454);
					arrayCreatorRest();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:605:29: classCreatorRest
					{
					pushFollow(FOLLOW_classCreatorRest_in_creator3458);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:608:1: createdName : ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType );
	public final void createdName() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:609:5: ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType )
			int alt71=2;
			int LA71_0 = input.LA(1);
			if ( (LA71_0==ID) ) {
				int LA71_1 = input.LA(2);
				if ( (!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))) ) {
					alt71=1;
				}
				else if ( ((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))) ) {
					alt71=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 71, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 71, 0, input);
				throw nvae;
			}

			switch (alt71) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:609:7: ID ( typeArguments )? ( DOT ID ( typeArguments )? )*
					{
					match(input,ID,FOLLOW_ID_in_createdName3476); if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:609:10: ( typeArguments )?
					int alt68=2;
					int LA68_0 = input.LA(1);
					if ( (LA68_0==LESS) ) {
						alt68=1;
					}
					switch (alt68) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:609:10: typeArguments
							{
							pushFollow(FOLLOW_typeArguments_in_createdName3478);
							typeArguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:610:9: ( DOT ID ( typeArguments )? )*
					loop70:
					while (true) {
						int alt70=2;
						int LA70_0 = input.LA(1);
						if ( (LA70_0==DOT) ) {
							alt70=1;
						}

						switch (alt70) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:610:11: DOT ID ( typeArguments )?
							{
							match(input,DOT,FOLLOW_DOT_in_createdName3491); if (state.failed) return;
							match(input,ID,FOLLOW_ID_in_createdName3493); if (state.failed) return;
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:610:18: ( typeArguments )?
							int alt69=2;
							int LA69_0 = input.LA(1);
							if ( (LA69_0==LESS) ) {
								alt69=1;
							}
							switch (alt69) {
								case 1 :
									// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:610:18: typeArguments
									{
									pushFollow(FOLLOW_typeArguments_in_createdName3495);
									typeArguments();
									state._fsp--;
									if (state.failed) return;
									}
									break;

							}

							}
							break;

						default :
							break loop70;
						}
					}

					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:611:11: primitiveType
					{
					pushFollow(FOLLOW_primitiveType_in_createdName3510);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:614:1: innerCreator :{...}? => ID classCreatorRest ;
	public final void innerCreator() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:615:5: ({...}? => ID classCreatorRest )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:615:7: {...}? => ID classCreatorRest
			{
			if ( !((!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "innerCreator", "!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
			}
			match(input,ID,FOLLOW_ID_in_innerCreator3530); if (state.failed) return;
			pushFollow(FOLLOW_classCreatorRest_in_innerCreator3532);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:618:1: arrayCreatorRest : LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) ;
	public final void arrayCreatorRest() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:619:5: ( LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:619:9: LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
			{
			match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3551); if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:620:5: ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
			int alt75=2;
			int LA75_0 = input.LA(1);
			if ( (LA75_0==RIGHT_SQUARE) ) {
				alt75=1;
			}
			else if ( (LA75_0==BOOL||(LA75_0 >= DECIMAL && LA75_0 <= DECR)||LA75_0==FLOAT||LA75_0==HEX||(LA75_0 >= ID && LA75_0 <= INCR)||(LA75_0 >= LEFT_PAREN && LA75_0 <= LESS)||LA75_0==MINUS||LA75_0==NEGATION||LA75_0==NULL||LA75_0==PLUS||(LA75_0 >= STAR && LA75_0 <= TIME_INTERVAL)) ) {
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
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:620:9: RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer
					{
					match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3561); if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:620:22: ( LEFT_SQUARE RIGHT_SQUARE )*
					loop72:
					while (true) {
						int alt72=2;
						int LA72_0 = input.LA(1);
						if ( (LA72_0==LEFT_SQUARE) ) {
							alt72=1;
						}

						switch (alt72) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:620:23: LEFT_SQUARE RIGHT_SQUARE
							{
							match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3564); if (state.failed) return;
							match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3566); if (state.failed) return;
							}
							break;

						default :
							break loop72;
						}
					}

					pushFollow(FOLLOW_arrayInitializer_in_arrayCreatorRest3570);
					arrayInitializer();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:621:13: expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
					{
					pushFollow(FOLLOW_expression_in_arrayCreatorRest3584);
					expression();
					state._fsp--;
					if (state.failed) return;
					match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3586); if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:621:37: ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )*
					loop73:
					while (true) {
						int alt73=2;
						int LA73_0 = input.LA(1);
						if ( (LA73_0==LEFT_SQUARE) ) {
							int LA73_1 = input.LA(2);
							if ( ((!helper.validateLT(2,"]"))) ) {
								alt73=1;
							}

						}

						switch (alt73) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:621:38: {...}? => LEFT_SQUARE expression RIGHT_SQUARE
							{
							if ( !((!helper.validateLT(2,"]"))) ) {
								if (state.backtracking>0) {state.failed=true; return;}
								throw new FailedPredicateException(input, "arrayCreatorRest", "!helper.validateLT(2,\"]\")");
							}
							match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3591); if (state.failed) return;
							pushFollow(FOLLOW_expression_in_arrayCreatorRest3593);
							expression();
							state._fsp--;
							if (state.failed) return;
							match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3595); if (state.failed) return;
							}
							break;

						default :
							break loop73;
						}
					}

					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:621:106: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
					loop74:
					while (true) {
						int alt74=2;
						int LA74_0 = input.LA(1);
						if ( (LA74_0==LEFT_SQUARE) ) {
							int LA74_2 = input.LA(2);
							if ( (LA74_2==RIGHT_SQUARE) && (synpred35_DRL5Expressions())) {
								alt74=1;
							}

						}

						switch (alt74) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:621:107: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
							{
							match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3607); if (state.failed) return;
							match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3609); if (state.failed) return;
							}
							break;

						default :
							break loop74;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:625:1: variableInitializer : ( arrayInitializer | expression );
	public final void variableInitializer() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:626:5: ( arrayInitializer | expression )
			int alt76=2;
			int LA76_0 = input.LA(1);
			if ( (LA76_0==LEFT_CURLY) ) {
				alt76=1;
			}
			else if ( (LA76_0==BOOL||(LA76_0 >= DECIMAL && LA76_0 <= DECR)||LA76_0==FLOAT||LA76_0==HEX||(LA76_0 >= ID && LA76_0 <= INCR)||(LA76_0 >= LEFT_PAREN && LA76_0 <= LESS)||LA76_0==MINUS||LA76_0==NEGATION||LA76_0==NULL||LA76_0==PLUS||(LA76_0 >= STAR && LA76_0 <= TIME_INTERVAL)) ) {
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
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:626:7: arrayInitializer
					{
					pushFollow(FOLLOW_arrayInitializer_in_variableInitializer3638);
					arrayInitializer();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:627:13: expression
					{
					pushFollow(FOLLOW_expression_in_variableInitializer3652);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:630:1: arrayInitializer : LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY ;
	public final void arrayInitializer() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:631:5: ( LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:631:7: LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY
			{
			match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_arrayInitializer3669); if (state.failed) return;
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:631:18: ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )?
			int alt79=2;
			int LA79_0 = input.LA(1);
			if ( (LA79_0==BOOL||(LA79_0 >= DECIMAL && LA79_0 <= DECR)||LA79_0==FLOAT||LA79_0==HEX||(LA79_0 >= ID && LA79_0 <= INCR)||(LA79_0 >= LEFT_CURLY && LA79_0 <= LESS)||LA79_0==MINUS||LA79_0==NEGATION||LA79_0==NULL||LA79_0==PLUS||(LA79_0 >= STAR && LA79_0 <= TIME_INTERVAL)) ) {
				alt79=1;
			}
			switch (alt79) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:631:19: variableInitializer ( COMMA variableInitializer )* ( COMMA )?
					{
					pushFollow(FOLLOW_variableInitializer_in_arrayInitializer3672);
					variableInitializer();
					state._fsp--;
					if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:631:39: ( COMMA variableInitializer )*
					loop77:
					while (true) {
						int alt77=2;
						int LA77_0 = input.LA(1);
						if ( (LA77_0==COMMA) ) {
							int LA77_1 = input.LA(2);
							if ( (LA77_1==BOOL||(LA77_1 >= DECIMAL && LA77_1 <= DECR)||LA77_1==FLOAT||LA77_1==HEX||(LA77_1 >= ID && LA77_1 <= INCR)||(LA77_1 >= LEFT_CURLY && LA77_1 <= LESS)||LA77_1==MINUS||LA77_1==NEGATION||LA77_1==NULL||LA77_1==PLUS||(LA77_1 >= STAR && LA77_1 <= TIME_INTERVAL)) ) {
								alt77=1;
							}

						}

						switch (alt77) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:631:40: COMMA variableInitializer
							{
							match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer3675); if (state.failed) return;
							pushFollow(FOLLOW_variableInitializer_in_arrayInitializer3677);
							variableInitializer();
							state._fsp--;
							if (state.failed) return;
							}
							break;

						default :
							break loop77;
						}
					}

					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:631:68: ( COMMA )?
					int alt78=2;
					int LA78_0 = input.LA(1);
					if ( (LA78_0==COMMA) ) {
						alt78=1;
					}
					switch (alt78) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:631:69: COMMA
							{
							match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer3682); if (state.failed) return;
							}
							break;

					}

					}
					break;

			}

			match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_arrayInitializer3689); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:634:1: classCreatorRest : arguments ;
	public final void classCreatorRest() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:635:5: ( arguments )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:635:7: arguments
			{
			pushFollow(FOLLOW_arguments_in_classCreatorRest3706);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:638:1: explicitGenericInvocation : nonWildcardTypeArguments arguments ;
	public final void explicitGenericInvocation() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:639:5: ( nonWildcardTypeArguments arguments )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:639:7: nonWildcardTypeArguments arguments
			{
			pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation3724);
			nonWildcardTypeArguments();
			state._fsp--;
			if (state.failed) return;
			pushFollow(FOLLOW_arguments_in_explicitGenericInvocation3726);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:642:1: nonWildcardTypeArguments : LESS typeList GREATER ;
	public final void nonWildcardTypeArguments() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:643:5: ( LESS typeList GREATER )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:643:7: LESS typeList GREATER
			{
			match(input,LESS,FOLLOW_LESS_in_nonWildcardTypeArguments3743); if (state.failed) return;
			pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments3745);
			typeList();
			state._fsp--;
			if (state.failed) return;
			match(input,GREATER,FOLLOW_GREATER_in_nonWildcardTypeArguments3747); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:646:1: explicitGenericInvocationSuffix : ( super_key superSuffix | ID arguments );
	public final void explicitGenericInvocationSuffix() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:647:5: ( super_key superSuffix | ID arguments )
			int alt80=2;
			int LA80_0 = input.LA(1);
			if ( (LA80_0==ID) ) {
				int LA80_1 = input.LA(2);
				if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
					alt80=1;
				}
				else if ( (true) ) {
					alt80=2;
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 80, 0, input);
				throw nvae;
			}

			switch (alt80) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:647:7: super_key superSuffix
					{
					pushFollow(FOLLOW_super_key_in_explicitGenericInvocationSuffix3764);
					super_key();
					state._fsp--;
					if (state.failed) return;
					pushFollow(FOLLOW_superSuffix_in_explicitGenericInvocationSuffix3766);
					superSuffix();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:648:10: ID arguments
					{
					match(input,ID,FOLLOW_ID_in_explicitGenericInvocationSuffix3777); if (state.failed) return;
					pushFollow(FOLLOW_arguments_in_explicitGenericInvocationSuffix3779);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:651:1: selector : ( ( DOT super_key )=> DOT super_key superSuffix | ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator | ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )? | ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE );
	public final void selector() throws RecognitionException {
		Token DOT21=null;
		Token DOT22=null;
		Token DOT23=null;
		Token ID24=null;
		Token LEFT_SQUARE25=null;
		Token RIGHT_SQUARE26=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:652:5: ( ( DOT super_key )=> DOT super_key superSuffix | ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator | ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )? | ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )
			int alt83=4;
			int LA83_0 = input.LA(1);
			if ( (LA83_0==DOT) ) {
				int LA83_1 = input.LA(2);
				if ( (synpred36_DRL5Expressions()) ) {
					alt83=1;
				}
				else if ( (synpred37_DRL5Expressions()) ) {
					alt83=2;
				}
				else if ( (synpred38_DRL5Expressions()) ) {
					alt83=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 83, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA83_0==LEFT_SQUARE) && (synpred40_DRL5Expressions())) {
				alt83=4;
			}

			switch (alt83) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:652:9: ( DOT super_key )=> DOT super_key superSuffix
					{
					DOT21=(Token)match(input,DOT,FOLLOW_DOT_in_selector3804); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(DOT21, DroolsEditorType.SYMBOL); }
					pushFollow(FOLLOW_super_key_in_selector3808);
					super_key();
					state._fsp--;
					if (state.failed) return;
					pushFollow(FOLLOW_superSuffix_in_selector3810);
					superSuffix();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:653:9: ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator
					{
					DOT22=(Token)match(input,DOT,FOLLOW_DOT_in_selector3826); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(DOT22, DroolsEditorType.SYMBOL); }
					pushFollow(FOLLOW_new_key_in_selector3830);
					new_key();
					state._fsp--;
					if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:653:84: ( nonWildcardTypeArguments )?
					int alt81=2;
					int LA81_0 = input.LA(1);
					if ( (LA81_0==LESS) ) {
						alt81=1;
					}
					switch (alt81) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:653:85: nonWildcardTypeArguments
							{
							pushFollow(FOLLOW_nonWildcardTypeArguments_in_selector3833);
							nonWildcardTypeArguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					pushFollow(FOLLOW_innerCreator_in_selector3837);
					innerCreator();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:654:9: ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )?
					{
					DOT23=(Token)match(input,DOT,FOLLOW_DOT_in_selector3853); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(DOT23, DroolsEditorType.SYMBOL); }
					ID24=(Token)match(input,ID,FOLLOW_ID_in_selector3875); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(ID24, DroolsEditorType.IDENTIFIER); }
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:656:19: ( ( LEFT_PAREN )=> arguments )?
					int alt82=2;
					int LA82_0 = input.LA(1);
					if ( (LA82_0==LEFT_PAREN) ) {
						int LA82_1 = input.LA(2);
						if ( (synpred39_DRL5Expressions()) ) {
							alt82=1;
						}
					}
					switch (alt82) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:656:20: ( LEFT_PAREN )=> arguments
							{
							pushFollow(FOLLOW_arguments_in_selector3904);
							arguments();
							state._fsp--;
							if (state.failed) return;
							}
							break;

					}

					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:658:9: ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE
					{
					LEFT_SQUARE25=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_selector3925); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(LEFT_SQUARE25, DroolsEditorType.SYMBOL); }
					pushFollow(FOLLOW_expression_in_selector3952);
					expression();
					state._fsp--;
					if (state.failed) return;
					RIGHT_SQUARE26=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_selector3977); if (state.failed) return;
					if ( state.backtracking==0 ) { helper.emit(RIGHT_SQUARE26, DroolsEditorType.SYMBOL); }
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:663:1: superSuffix : ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? );
	public final void superSuffix() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:664:5: ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? )
			int alt85=2;
			int LA85_0 = input.LA(1);
			if ( (LA85_0==LEFT_PAREN) ) {
				alt85=1;
			}
			else if ( (LA85_0==DOT) ) {
				alt85=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return;}
				NoViableAltException nvae =
					new NoViableAltException("", 85, 0, input);
				throw nvae;
			}

			switch (alt85) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:664:7: arguments
					{
					pushFollow(FOLLOW_arguments_in_superSuffix3996);
					arguments();
					state._fsp--;
					if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:665:10: DOT ID ( ( LEFT_PAREN )=> arguments )?
					{
					match(input,DOT,FOLLOW_DOT_in_superSuffix4007); if (state.failed) return;
					match(input,ID,FOLLOW_ID_in_superSuffix4009); if (state.failed) return;
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:665:17: ( ( LEFT_PAREN )=> arguments )?
					int alt84=2;
					int LA84_0 = input.LA(1);
					if ( (LA84_0==LEFT_PAREN) ) {
						int LA84_1 = input.LA(2);
						if ( (synpred41_DRL5Expressions()) ) {
							alt84=1;
						}
					}
					switch (alt84) {
						case 1 :
							// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:665:18: ( LEFT_PAREN )=> arguments
							{
							pushFollow(FOLLOW_arguments_in_superSuffix4018);
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:668:1: squareArguments returns [java.util.List<String> args] : LEFT_SQUARE (el= expressionList )? RIGHT_SQUARE ;
	public final java.util.List<String> squareArguments() throws RecognitionException {
		java.util.List<String> args = null;


		java.util.List<String> el =null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:669:5: ( LEFT_SQUARE (el= expressionList )? RIGHT_SQUARE )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:669:7: LEFT_SQUARE (el= expressionList )? RIGHT_SQUARE
			{
			match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_squareArguments4041); if (state.failed) return args;
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:669:19: (el= expressionList )?
			int alt86=2;
			int LA86_0 = input.LA(1);
			if ( (LA86_0==BOOL||(LA86_0 >= DECIMAL && LA86_0 <= DECR)||LA86_0==FLOAT||LA86_0==HEX||(LA86_0 >= ID && LA86_0 <= INCR)||(LA86_0 >= LEFT_PAREN && LA86_0 <= LESS)||LA86_0==MINUS||LA86_0==NEGATION||LA86_0==NULL||LA86_0==PLUS||(LA86_0 >= STAR && LA86_0 <= TIME_INTERVAL)) ) {
				alt86=1;
			}
			switch (alt86) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:669:20: el= expressionList
					{
					pushFollow(FOLLOW_expressionList_in_squareArguments4046);
					el=expressionList();
					state._fsp--;
					if (state.failed) return args;
					if ( state.backtracking==0 ) { args = el; }
					}
					break;

			}

			match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_squareArguments4052); if (state.failed) return args;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:672:1: arguments : LEFT_PAREN ( expressionList )? RIGHT_PAREN ;
	public final void arguments() throws RecognitionException {
		Token LEFT_PAREN27=null;
		Token RIGHT_PAREN28=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:673:5: ( LEFT_PAREN ( expressionList )? RIGHT_PAREN )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:673:7: LEFT_PAREN ( expressionList )? RIGHT_PAREN
			{
			LEFT_PAREN27=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_arguments4069); if (state.failed) return;
			if ( state.backtracking==0 ) { helper.emit(LEFT_PAREN27, DroolsEditorType.SYMBOL); }
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:674:9: ( expressionList )?
			int alt87=2;
			int LA87_0 = input.LA(1);
			if ( (LA87_0==BOOL||(LA87_0 >= DECIMAL && LA87_0 <= DECR)||LA87_0==FLOAT||LA87_0==HEX||(LA87_0 >= ID && LA87_0 <= INCR)||(LA87_0 >= LEFT_PAREN && LA87_0 <= LESS)||LA87_0==MINUS||LA87_0==NEGATION||LA87_0==NULL||LA87_0==PLUS||(LA87_0 >= STAR && LA87_0 <= TIME_INTERVAL)) ) {
				alt87=1;
			}
			switch (alt87) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:674:9: expressionList
					{
					pushFollow(FOLLOW_expressionList_in_arguments4081);
					expressionList();
					state._fsp--;
					if (state.failed) return;
					}
					break;

			}

			RIGHT_PAREN28=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_arguments4092); if (state.failed) return;
			if ( state.backtracking==0 ) { helper.emit(RIGHT_PAREN28, DroolsEditorType.SYMBOL); }
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:678:1: expressionList returns [java.util.List<String> exprs] : f= expression ( COMMA s= expression )* ;
	public final java.util.List<String> expressionList() throws RecognitionException {
		java.util.List<String> exprs = null;


		ParserRuleReturnScope f =null;
		ParserRuleReturnScope s =null;

		 exprs = new java.util.ArrayList<String>();
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:680:3: (f= expression ( COMMA s= expression )* )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:680:7: f= expression ( COMMA s= expression )*
			{
			pushFollow(FOLLOW_expression_in_expressionList4122);
			f=expression();
			state._fsp--;
			if (state.failed) return exprs;
			if ( state.backtracking==0 ) { exprs.add( (f!=null?input.toString(f.start,f.stop):null) ); }
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:681:7: ( COMMA s= expression )*
			loop88:
			while (true) {
				int alt88=2;
				int LA88_0 = input.LA(1);
				if ( (LA88_0==COMMA) ) {
					alt88=1;
				}

				switch (alt88) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:681:8: COMMA s= expression
					{
					match(input,COMMA,FOLLOW_COMMA_in_expressionList4133); if (state.failed) return exprs;
					pushFollow(FOLLOW_expression_in_expressionList4137);
					s=expression();
					state._fsp--;
					if (state.failed) return exprs;
					if ( state.backtracking==0 ) { exprs.add( (s!=null?input.toString(s.start,s.stop):null) ); }
					}
					break;

				default :
					break loop88;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:684:1: assignmentOperator : ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN );
	public final void assignmentOperator() throws RecognitionException {
		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:685:5: ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN )
			int alt89=12;
			switch ( input.LA(1) ) {
			case EQUALS_ASSIGN:
				{
				alt89=1;
				}
				break;
			case PLUS_ASSIGN:
				{
				alt89=2;
				}
				break;
			case MINUS_ASSIGN:
				{
				alt89=3;
				}
				break;
			case MULT_ASSIGN:
				{
				alt89=4;
				}
				break;
			case DIV_ASSIGN:
				{
				alt89=5;
				}
				break;
			case AND_ASSIGN:
				{
				alt89=6;
				}
				break;
			case OR_ASSIGN:
				{
				alt89=7;
				}
				break;
			case XOR_ASSIGN:
				{
				alt89=8;
				}
				break;
			case MOD_ASSIGN:
				{
				alt89=9;
				}
				break;
			case LESS:
				{
				alt89=10;
				}
				break;
			case GREATER:
				{
				int LA89_11 = input.LA(2);
				if ( (LA89_11==GREATER) ) {
					int LA89_12 = input.LA(3);
					if ( (LA89_12==GREATER) && (synpred42_DRL5Expressions())) {
						alt89=11;
					}
					else if ( (LA89_12==EQUALS_ASSIGN) && (synpred43_DRL5Expressions())) {
						alt89=12;
					}

				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 89, 11, input);
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
					new NoViableAltException("", 89, 0, input);
				throw nvae;
			}
			switch (alt89) {
				case 1 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:685:9: EQUALS_ASSIGN
					{
					match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4158); if (state.failed) return;
					}
					break;
				case 2 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:686:7: PLUS_ASSIGN
					{
					match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_assignmentOperator4166); if (state.failed) return;
					}
					break;
				case 3 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:687:7: MINUS_ASSIGN
					{
					match(input,MINUS_ASSIGN,FOLLOW_MINUS_ASSIGN_in_assignmentOperator4174); if (state.failed) return;
					}
					break;
				case 4 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:688:7: MULT_ASSIGN
					{
					match(input,MULT_ASSIGN,FOLLOW_MULT_ASSIGN_in_assignmentOperator4182); if (state.failed) return;
					}
					break;
				case 5 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:689:7: DIV_ASSIGN
					{
					match(input,DIV_ASSIGN,FOLLOW_DIV_ASSIGN_in_assignmentOperator4190); if (state.failed) return;
					}
					break;
				case 6 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:690:7: AND_ASSIGN
					{
					match(input,AND_ASSIGN,FOLLOW_AND_ASSIGN_in_assignmentOperator4198); if (state.failed) return;
					}
					break;
				case 7 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:691:7: OR_ASSIGN
					{
					match(input,OR_ASSIGN,FOLLOW_OR_ASSIGN_in_assignmentOperator4206); if (state.failed) return;
					}
					break;
				case 8 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:692:7: XOR_ASSIGN
					{
					match(input,XOR_ASSIGN,FOLLOW_XOR_ASSIGN_in_assignmentOperator4214); if (state.failed) return;
					}
					break;
				case 9 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:693:7: MOD_ASSIGN
					{
					match(input,MOD_ASSIGN,FOLLOW_MOD_ASSIGN_in_assignmentOperator4222); if (state.failed) return;
					}
					break;
				case 10 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:694:7: LESS LESS EQUALS_ASSIGN
					{
					match(input,LESS,FOLLOW_LESS_in_assignmentOperator4230); if (state.failed) return;
					match(input,LESS,FOLLOW_LESS_in_assignmentOperator4232); if (state.failed) return;
					match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4234); if (state.failed) return;
					}
					break;
				case 11 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:695:7: ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN
					{
					match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4251); if (state.failed) return;
					match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4253); if (state.failed) return;
					match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4255); if (state.failed) return;
					match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4257); if (state.failed) return;
					}
					break;
				case 12 :
					// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:696:7: ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN
					{
					match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4272); if (state.failed) return;
					match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4274); if (state.failed) return;
					match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4276); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:702:1: extends_key :{...}? =>id= ID ;
	public final void extends_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:703:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:703:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "extends_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_extends_key4306); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:706:1: super_key :{...}? =>id= ID ;
	public final void super_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:707:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:707:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "super_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_super_key4335); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:710:1: instanceof_key :{...}? =>id= ID ;
	public final DRL5Expressions.instanceof_key_return instanceof_key() throws RecognitionException {
		DRL5Expressions.instanceof_key_return retval = new DRL5Expressions.instanceof_key_return();
		retval.start = input.LT(1);

		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:711:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:711:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
				if (state.backtracking>0) {state.failed=true; return retval;}
				throw new FailedPredicateException(input, "instanceof_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_instanceof_key4364); if (state.failed) return retval;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:714:1: boolean_key :{...}? =>id= ID ;
	public final void boolean_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:715:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:715:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "boolean_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_boolean_key4393); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:718:1: char_key :{...}? =>id= ID ;
	public final void char_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:719:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:719:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "char_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_char_key4422); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:722:1: byte_key :{...}? =>id= ID ;
	public final void byte_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:723:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:723:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "byte_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_byte_key4451); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:726:1: short_key :{...}? =>id= ID ;
	public final void short_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:727:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:727:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "short_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_short_key4480); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:730:1: int_key :{...}? =>id= ID ;
	public final void int_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:731:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:731:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "int_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INT))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_int_key4509); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:734:1: float_key :{...}? =>id= ID ;
	public final void float_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:735:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:735:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "float_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_float_key4538); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:738:1: long_key :{...}? =>id= ID ;
	public final void long_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:739:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:739:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "long_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.LONG))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_long_key4567); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:742:1: double_key :{...}? =>id= ID ;
	public final void double_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:743:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:743:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "double_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_double_key4596); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:746:1: void_key :{...}? =>id= ID ;
	public final void void_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:747:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:747:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.VOID)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "void_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.VOID))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_void_key4625); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:750:1: this_key :{...}? =>id= ID ;
	public final void this_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:751:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:751:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "this_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.THIS))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_this_key4654); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:754:1: class_key :{...}? =>id= ID ;
	public final void class_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:755:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:755:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.CLASS)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "class_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.CLASS))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_class_key4683); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:758:1: new_key :{...}? =>id= ID ;
	public final void new_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:759:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:759:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.NEW)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "new_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.NEW))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_new_key4713); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:762:1: not_key :{...}? =>id= ID ;
	public final void not_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:763:5: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:763:12: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "not_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.NOT))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_not_key4742); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:766:1: in_key :{...}? =>id= ID ;
	public final void in_key() throws RecognitionException {
		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:767:3: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:767:10: {...}? =>id= ID
			{
			if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {
				if (state.backtracking>0) {state.failed=true; return;}
				throw new FailedPredicateException(input, "in_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.IN))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_in_key4769); if (state.failed) return;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:770:1: operator_key :{...}? =>id= ID ;
	public final DRL5Expressions.operator_key_return operator_key() throws RecognitionException {
		DRL5Expressions.operator_key_return retval = new DRL5Expressions.operator_key_return();
		retval.start = input.LT(1);

		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:771:3: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:771:10: {...}? =>id= ID
			{
			if ( !(((helper.isPluggableEvaluator(false)))) ) {
				if (state.backtracking>0) {state.failed=true; return retval;}
				throw new FailedPredicateException(input, "operator_key", "(helper.isPluggableEvaluator(false))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_operator_key4794); if (state.failed) return retval;
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
	// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:774:1: neg_operator_key :{...}? =>id= ID ;
	public final DRL5Expressions.neg_operator_key_return neg_operator_key() throws RecognitionException {
		DRL5Expressions.neg_operator_key_return retval = new DRL5Expressions.neg_operator_key_return();
		retval.start = input.LT(1);

		Token id=null;

		try {
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:775:3: ({...}? =>id= ID )
			// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:775:10: {...}? =>id= ID
			{
			if ( !(((helper.isPluggableEvaluator(true)))) ) {
				if (state.backtracking>0) {state.failed=true; return retval;}
				throw new FailedPredicateException(input, "neg_operator_key", "(helper.isPluggableEvaluator(true))");
			}
			id=(Token)match(input,ID,FOLLOW_ID_in_neg_operator_key4819); if (state.failed) return retval;
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

	// $ANTLR start synpred1_DRL5Expressions
	public final void synpred1_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:138:8: ( primitiveType )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:138:9: primitiveType
		{
		pushFollow(FOLLOW_primitiveType_in_synpred1_DRL5Expressions548);
		primitiveType();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred1_DRL5Expressions

	// $ANTLR start synpred2_DRL5Expressions
	public final void synpred2_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:138:44: ( LEFT_SQUARE RIGHT_SQUARE )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:138:45: LEFT_SQUARE RIGHT_SQUARE
		{
		match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred2_DRL5Expressions559); if (state.failed) return;
		match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred2_DRL5Expressions561); if (state.failed) return;
		}

	}
	// $ANTLR end synpred2_DRL5Expressions

	// $ANTLR start synpred3_DRL5Expressions
	public final void synpred3_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:139:13: ( typeArguments )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:139:14: typeArguments
		{
		pushFollow(FOLLOW_typeArguments_in_synpred3_DRL5Expressions585);
		typeArguments();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred3_DRL5Expressions

	// $ANTLR start synpred4_DRL5Expressions
	public final void synpred4_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:139:55: ( typeArguments )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:139:56: typeArguments
		{
		pushFollow(FOLLOW_typeArguments_in_synpred4_DRL5Expressions599);
		typeArguments();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred4_DRL5Expressions

	// $ANTLR start synpred5_DRL5Expressions
	public final void synpred5_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:139:92: ( LEFT_SQUARE RIGHT_SQUARE )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:139:93: LEFT_SQUARE RIGHT_SQUARE
		{
		match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred5_DRL5Expressions611); if (state.failed) return;
		match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred5_DRL5Expressions613); if (state.failed) return;
		}

	}
	// $ANTLR end synpred5_DRL5Expressions

	// $ANTLR start synpred6_DRL5Expressions
	public final void synpred6_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:165:10: ( assignmentOperator )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:165:11: assignmentOperator
		{
		pushFollow(FOLLOW_assignmentOperator_in_synpred6_DRL5Expressions782);
		assignmentOperator();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred6_DRL5Expressions

	// $ANTLR start synpred7_DRL5Expressions
	public final void synpred7_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:323:6: ( not_key in_key )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:323:7: not_key in_key
		{
		pushFollow(FOLLOW_not_key_in_synpred7_DRL5Expressions1547);
		not_key();
		state._fsp--;
		if (state.failed) return;
		pushFollow(FOLLOW_in_key_in_synpred7_DRL5Expressions1549);
		in_key();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred7_DRL5Expressions

	// $ANTLR start synpred8_DRL5Expressions
	public final void synpred8_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:382:5: ( operator | LEFT_PAREN )
		int alt90=2;
		int LA90_0 = input.LA(1);
		if ( (LA90_0==EQUALS||(LA90_0 >= GREATER && LA90_0 <= GREATER_EQUALS)||(LA90_0 >= LESS && LA90_0 <= LESS_EQUALS)||LA90_0==NOT_EQUALS||LA90_0==TILDE) ) {
			alt90=1;
		}
		else if ( (LA90_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) {
			alt90=1;
		}
		else if ( (LA90_0==LEFT_PAREN) ) {
			alt90=2;
		}

		else {
			if (state.backtracking>0) {state.failed=true; return;}
			NoViableAltException nvae =
				new NoViableAltException("", 90, 0, input);
			throw nvae;
		}

		switch (alt90) {
			case 1 :
				// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:382:7: operator
				{
				pushFollow(FOLLOW_operator_in_synpred8_DRL5Expressions1764);
				operator();
				state._fsp--;
				if (state.failed) return;
				}
				break;
			case 2 :
				// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:382:18: LEFT_PAREN
				{
				match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred8_DRL5Expressions1768); if (state.failed) return;
				}
				break;

		}
	}
	// $ANTLR end synpred8_DRL5Expressions

	// $ANTLR start synpred9_DRL5Expressions
	public final void synpred9_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:393:7: ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:393:8: DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction
		{
		match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_synpred9_DRL5Expressions1821); if (state.failed) return;
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:393:20: ( fullAnnotation[null] )?
		int alt91=2;
		int LA91_0 = input.LA(1);
		if ( (LA91_0==AT) ) {
			alt91=1;
		}
		switch (alt91) {
			case 1 :
				// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:393:20: fullAnnotation[null]
				{
				pushFollow(FOLLOW_fullAnnotation_in_synpred9_DRL5Expressions1823);
				fullAnnotation(null);
				state._fsp--;
				if (state.failed) return;
				}
				break;

		}

		pushFollow(FOLLOW_andRestriction_in_synpred9_DRL5Expressions1827);
		andRestriction();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred9_DRL5Expressions

	// $ANTLR start synpred10_DRL5Expressions
	public final void synpred10_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:407:5: ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:407:6: DOUBLE_AMPER ( fullAnnotation[null] )? operator
		{
		match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_synpred10_DRL5Expressions1890); if (state.failed) return;
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:407:19: ( fullAnnotation[null] )?
		int alt92=2;
		int LA92_0 = input.LA(1);
		if ( (LA92_0==AT) ) {
			alt92=1;
		}
		switch (alt92) {
			case 1 :
				// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:407:19: fullAnnotation[null]
				{
				pushFollow(FOLLOW_fullAnnotation_in_synpred10_DRL5Expressions1892);
				fullAnnotation(null);
				state._fsp--;
				if (state.failed) return;
				}
				break;

		}

		pushFollow(FOLLOW_operator_in_synpred10_DRL5Expressions1896);
		operator();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred10_DRL5Expressions

	// $ANTLR start synpred11_DRL5Expressions
	public final void synpred11_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:424:8: ( squareArguments shiftExpression )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:424:9: squareArguments shiftExpression
		{
		pushFollow(FOLLOW_squareArguments_in_synpred11_DRL5Expressions1984);
		squareArguments();
		state._fsp--;
		if (state.failed) return;
		pushFollow(FOLLOW_shiftExpression_in_synpred11_DRL5Expressions1986);
		shiftExpression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred11_DRL5Expressions

	// $ANTLR start synpred12_DRL5Expressions
	public final void synpred12_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:447:7: ( shiftOp )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:447:8: shiftOp
		{
		pushFollow(FOLLOW_shiftOp_in_synpred12_DRL5Expressions2075);
		shiftOp();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred12_DRL5Expressions

	// $ANTLR start synpred13_DRL5Expressions
	public final void synpred13_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:458:11: ( PLUS | MINUS )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:
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
	// $ANTLR end synpred13_DRL5Expressions

	// $ANTLR start synpred14_DRL5Expressions
	public final void synpred14_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:490:9: ( castExpression )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:490:10: castExpression
		{
		pushFollow(FOLLOW_castExpression_in_synpred14_DRL5Expressions2395);
		castExpression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred14_DRL5Expressions

	// $ANTLR start synpred15_DRL5Expressions
	public final void synpred15_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:498:10: ( selector )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:498:11: selector
		{
		pushFollow(FOLLOW_selector_in_synpred15_DRL5Expressions2528);
		selector();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred15_DRL5Expressions

	// $ANTLR start synpred16_DRL5Expressions
	public final void synpred16_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:517:10: ( INCR | DECR )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:
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
	// $ANTLR end synpred16_DRL5Expressions

	// $ANTLR start synpred17_DRL5Expressions
	public final void synpred17_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:521:8: ( LEFT_PAREN primitiveType )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:521:9: LEFT_PAREN primitiveType
		{
		match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred17_DRL5Expressions2586); if (state.failed) return;
		pushFollow(FOLLOW_primitiveType_in_synpred17_DRL5Expressions2588);
		primitiveType();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred17_DRL5Expressions

	// $ANTLR start synpred18_DRL5Expressions
	public final void synpred18_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:522:8: ( LEFT_PAREN type )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:522:9: LEFT_PAREN type
		{
		match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred18_DRL5Expressions2611); if (state.failed) return;
		pushFollow(FOLLOW_type_in_synpred18_DRL5Expressions2613);
		type();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred18_DRL5Expressions

	// $ANTLR start synpred19_DRL5Expressions
	public final void synpred19_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:537:7: ( parExpression )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:537:8: parExpression
		{
		pushFollow(FOLLOW_parExpression_in_synpred19_DRL5Expressions2721);
		parExpression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred19_DRL5Expressions

	// $ANTLR start synpred20_DRL5Expressions
	public final void synpred20_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:538:9: ( nonWildcardTypeArguments )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:538:10: nonWildcardTypeArguments
		{
		pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred20_DRL5Expressions2740);
		nonWildcardTypeArguments();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred20_DRL5Expressions

	// $ANTLR start synpred21_DRL5Expressions
	public final void synpred21_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:539:9: ( literal )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:539:10: literal
		{
		pushFollow(FOLLOW_literal_in_synpred21_DRL5Expressions2765);
		literal();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred21_DRL5Expressions

	// $ANTLR start synpred22_DRL5Expressions
	public final void synpred22_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:541:9: ( super_key )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:541:10: super_key
		{
		pushFollow(FOLLOW_super_key_in_synpred22_DRL5Expressions2787);
		super_key();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred22_DRL5Expressions

	// $ANTLR start synpred23_DRL5Expressions
	public final void synpred23_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:542:9: ( new_key )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:542:10: new_key
		{
		pushFollow(FOLLOW_new_key_in_synpred23_DRL5Expressions2804);
		new_key();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred23_DRL5Expressions

	// $ANTLR start synpred24_DRL5Expressions
	public final void synpred24_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:543:9: ( primitiveType )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:543:10: primitiveType
		{
		pushFollow(FOLLOW_primitiveType_in_synpred24_DRL5Expressions2821);
		primitiveType();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred24_DRL5Expressions

	// $ANTLR start synpred25_DRL5Expressions
	public final void synpred25_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:545:9: ( inlineMapExpression )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:545:10: inlineMapExpression
		{
		pushFollow(FOLLOW_inlineMapExpression_in_synpred25_DRL5Expressions2852);
		inlineMapExpression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred25_DRL5Expressions

	// $ANTLR start synpred26_DRL5Expressions
	public final void synpred26_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:546:9: ( inlineListExpression )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:546:10: inlineListExpression
		{
		pushFollow(FOLLOW_inlineListExpression_in_synpred26_DRL5Expressions2867);
		inlineListExpression();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred26_DRL5Expressions

	// $ANTLR start synpred27_DRL5Expressions
	public final void synpred27_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:547:9: ( ID )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:547:10: ID
		{
		match(input,ID,FOLLOW_ID_in_synpred27_DRL5Expressions2882); if (state.failed) return;
		}

	}
	// $ANTLR end synpred27_DRL5Expressions

	// $ANTLR start synpred28_DRL5Expressions
	public final void synpred28_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:549:15: ( DOT ID )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:549:16: DOT ID
		{
		match(input,DOT,FOLLOW_DOT_in_synpred28_DRL5Expressions2916); if (state.failed) return;
		match(input,ID,FOLLOW_ID_in_synpred28_DRL5Expressions2918); if (state.failed) return;
		}

	}
	// $ANTLR end synpred28_DRL5Expressions

	// $ANTLR start synpred29_DRL5Expressions
	public final void synpred29_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:551:15: ( SHARP ID )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:551:16: SHARP ID
		{
		match(input,SHARP,FOLLOW_SHARP_in_synpred29_DRL5Expressions2960); if (state.failed) return;
		match(input,ID,FOLLOW_ID_in_synpred29_DRL5Expressions2962); if (state.failed) return;
		}

	}
	// $ANTLR end synpred29_DRL5Expressions

	// $ANTLR start synpred30_DRL5Expressions
	public final void synpred30_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:553:15: ( HASH ID )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:553:16: HASH ID
		{
		match(input,HASH,FOLLOW_HASH_in_synpred30_DRL5Expressions3004); if (state.failed) return;
		match(input,ID,FOLLOW_ID_in_synpred30_DRL5Expressions3006); if (state.failed) return;
		}

	}
	// $ANTLR end synpred30_DRL5Expressions

	// $ANTLR start synpred31_DRL5Expressions
	public final void synpred31_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:555:15: ( NULL_SAFE_DOT ID )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:555:16: NULL_SAFE_DOT ID
		{
		match(input,NULL_SAFE_DOT,FOLLOW_NULL_SAFE_DOT_in_synpred31_DRL5Expressions3048); if (state.failed) return;
		match(input,ID,FOLLOW_ID_in_synpred31_DRL5Expressions3050); if (state.failed) return;
		}

	}
	// $ANTLR end synpred31_DRL5Expressions

	// $ANTLR start synpred32_DRL5Expressions
	public final void synpred32_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:556:13: ( identifierSuffix )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:556:14: identifierSuffix
		{
		pushFollow(FOLLOW_identifierSuffix_in_synpred32_DRL5Expressions3076);
		identifierSuffix();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred32_DRL5Expressions

	// $ANTLR start synpred33_DRL5Expressions
	public final void synpred33_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:589:7: ( LEFT_SQUARE RIGHT_SQUARE )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:589:8: LEFT_SQUARE RIGHT_SQUARE
		{
		match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred33_DRL5Expressions3234); if (state.failed) return;
		match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred33_DRL5Expressions3236); if (state.failed) return;
		}

	}
	// $ANTLR end synpred33_DRL5Expressions

	// $ANTLR start synpred34_DRL5Expressions
	public final void synpred34_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:592:8: ( LEFT_SQUARE )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:592:9: LEFT_SQUARE
		{
		match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred34_DRL5Expressions3339); if (state.failed) return;
		}

	}
	// $ANTLR end synpred34_DRL5Expressions

	// $ANTLR start synpred35_DRL5Expressions
	public final void synpred35_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:621:107: ( LEFT_SQUARE RIGHT_SQUARE )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:621:108: LEFT_SQUARE RIGHT_SQUARE
		{
		match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred35_DRL5Expressions3601); if (state.failed) return;
		match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred35_DRL5Expressions3603); if (state.failed) return;
		}

	}
	// $ANTLR end synpred35_DRL5Expressions

	// $ANTLR start synpred36_DRL5Expressions
	public final void synpred36_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:652:9: ( DOT super_key )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:652:10: DOT super_key
		{
		match(input,DOT,FOLLOW_DOT_in_synpred36_DRL5Expressions3799); if (state.failed) return;
		pushFollow(FOLLOW_super_key_in_synpred36_DRL5Expressions3801);
		super_key();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred36_DRL5Expressions

	// $ANTLR start synpred37_DRL5Expressions
	public final void synpred37_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:653:9: ( DOT new_key )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:653:10: DOT new_key
		{
		match(input,DOT,FOLLOW_DOT_in_synpred37_DRL5Expressions3821); if (state.failed) return;
		pushFollow(FOLLOW_new_key_in_synpred37_DRL5Expressions3823);
		new_key();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred37_DRL5Expressions

	// $ANTLR start synpred38_DRL5Expressions
	public final void synpred38_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:654:9: ( DOT ID )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:654:10: DOT ID
		{
		match(input,DOT,FOLLOW_DOT_in_synpred38_DRL5Expressions3848); if (state.failed) return;
		match(input,ID,FOLLOW_ID_in_synpred38_DRL5Expressions3850); if (state.failed) return;
		}

	}
	// $ANTLR end synpred38_DRL5Expressions

	// $ANTLR start synpred39_DRL5Expressions
	public final void synpred39_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:656:20: ( LEFT_PAREN )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:656:21: LEFT_PAREN
		{
		match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred39_DRL5Expressions3899); if (state.failed) return;
		}

	}
	// $ANTLR end synpred39_DRL5Expressions

	// $ANTLR start synpred40_DRL5Expressions
	public final void synpred40_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:658:9: ( LEFT_SQUARE )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:658:10: LEFT_SQUARE
		{
		match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred40_DRL5Expressions3922); if (state.failed) return;
		}

	}
	// $ANTLR end synpred40_DRL5Expressions

	// $ANTLR start synpred41_DRL5Expressions
	public final void synpred41_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:665:18: ( LEFT_PAREN )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:665:19: LEFT_PAREN
		{
		match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred41_DRL5Expressions4013); if (state.failed) return;
		}

	}
	// $ANTLR end synpred41_DRL5Expressions

	// $ANTLR start synpred42_DRL5Expressions
	public final void synpred42_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:695:7: ( GREATER GREATER GREATER )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:695:8: GREATER GREATER GREATER
		{
		match(input,GREATER,FOLLOW_GREATER_in_synpred42_DRL5Expressions4243); if (state.failed) return;
		match(input,GREATER,FOLLOW_GREATER_in_synpred42_DRL5Expressions4245); if (state.failed) return;
		match(input,GREATER,FOLLOW_GREATER_in_synpred42_DRL5Expressions4247); if (state.failed) return;
		}

	}
	// $ANTLR end synpred42_DRL5Expressions

	// $ANTLR start synpred43_DRL5Expressions
	public final void synpred43_DRL5Expressions_fragment() throws RecognitionException {
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:696:7: ( GREATER GREATER )
		// src/main/resources/org/drools/compiler/lang/DRL5Expressions.g:696:8: GREATER GREATER
		{
		match(input,GREATER,FOLLOW_GREATER_in_synpred43_DRL5Expressions4266); if (state.failed) return;
		match(input,GREATER,FOLLOW_GREATER_in_synpred43_DRL5Expressions4268); if (state.failed) return;
		}

	}
	// $ANTLR end synpred43_DRL5Expressions

	// Delegated rules

	public final boolean synpred37_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred37_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred6_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred6_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred5_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred5_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred18_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred18_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred15_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred15_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred28_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred28_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred40_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred40_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred31_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred31_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred39_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred39_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred2_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred2_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred26_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred26_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred1_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred1_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred22_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred22_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred10_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred10_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred13_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred13_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred25_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred25_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred8_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred8_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred36_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred36_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred3_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred3_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred16_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred16_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred30_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred30_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred20_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred20_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred24_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred24_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred14_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred14_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred42_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred42_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred34_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred34_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred7_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred7_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred41_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred41_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred29_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred29_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred38_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred38_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred4_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred4_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred27_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred27_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred32_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred32_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred21_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred21_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred11_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred11_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred33_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred33_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred19_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred19_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred17_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred17_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred43_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred43_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred35_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred35_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred23_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred23_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred12_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred12_DRL5Expressions_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred9_DRL5Expressions() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred9_DRL5Expressions_fragment(); // can never throw exception
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
	public static final BitSet FOLLOW_assignmentOperator_in_expression789 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_expression793 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression820 = new BitSet(new long[]{0x0200000000000002L});
	public static final BitSet FOLLOW_ternaryExpression_in_conditionalExpression832 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_QUESTION_in_ternaryExpression854 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_ternaryExpression858 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_COLON_in_ternaryExpression860 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_ternaryExpression864 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AT_in_fullAnnotation894 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_fullAnnotation898 = new BitSet(new long[]{0x0000002000010000L});
	public static final BitSet FOLLOW_DOT_in_fullAnnotation904 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_fullAnnotation908 = new BitSet(new long[]{0x0000002000010000L});
	public static final BitSet FOLLOW_annotationArgs_in_fullAnnotation929 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_annotationArgs945 = new BitSet(new long[]{0x0800000080000000L});
	public static final BitSet FOLLOW_ID_in_annotationArgs962 = new BitSet(new long[]{0x0800000000000000L});
	public static final BitSet FOLLOW_annotationElementValuePairs_in_annotationArgs975 = new BitSet(new long[]{0x0800000000000000L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_annotationArgs989 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotationElementValuePair_in_annotationElementValuePairs1004 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_COMMA_in_annotationElementValuePairs1009 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_annotationElementValuePair_in_annotationElementValuePairs1011 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_ID_in_annotationElementValuePair1032 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_EQUALS_ASSIGN_in_annotationElementValuePair1034 = new BitSet(new long[]{0x808502F1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_annotationValue_in_annotationElementValuePair1038 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_annotationValue1053 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_annotationArray_in_annotationValue1057 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_CURLY_in_annotationArray1071 = new BitSet(new long[]{0x848502F1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_annotationValue_in_annotationArray1075 = new BitSet(new long[]{0x0400000000000400L});
	public static final BitSet FOLLOW_COMMA_in_annotationArray1079 = new BitSet(new long[]{0x808502F1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_annotationValue_in_annotationArray1081 = new BitSet(new long[]{0x0400000000000400L});
	public static final BitSet FOLLOW_RIGHT_CURLY_in_annotationArray1089 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression1110 = new BitSet(new long[]{0x0000000000040002L});
	public static final BitSet FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression1119 = new BitSet(new long[]{0x808502E1A1003180L,0x0000000000000007L});
	public static final BitSet FOLLOW_fullAnnotation_in_conditionalOrExpression1141 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression1147 = new BitSet(new long[]{0x0000000000040002L});
	public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression1182 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression1190 = new BitSet(new long[]{0x808502E1A1003180L,0x0000000000000007L});
	public static final BitSet FOLLOW_fullAnnotation_in_conditionalAndExpression1213 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression1219 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression1254 = new BitSet(new long[]{0x0040000000000002L});
	public static final BitSet FOLLOW_PIPE_in_inclusiveOrExpression1262 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression1266 = new BitSet(new long[]{0x0040000000000002L});
	public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression1301 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
	public static final BitSet FOLLOW_XOR_in_exclusiveOrExpression1309 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression1313 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
	public static final BitSet FOLLOW_equalityExpression_in_andExpression1348 = new BitSet(new long[]{0x0000000000000012L});
	public static final BitSet FOLLOW_AMPER_in_andExpression1356 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_equalityExpression_in_andExpression1360 = new BitSet(new long[]{0x0000000000000012L});
	public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression1395 = new BitSet(new long[]{0x0002000000100002L});
	public static final BitSet FOLLOW_EQUALS_in_equalityExpression1407 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_NOT_EQUALS_in_equalityExpression1413 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression1429 = new BitSet(new long[]{0x0002000000100002L});
	public static final BitSet FOLLOW_inExpression_in_instanceOfExpression1464 = new BitSet(new long[]{0x0000000080000002L});
	public static final BitSet FOLLOW_instanceof_key_in_instanceOfExpression1474 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_type_in_instanceOfExpression1488 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_relationalExpression_in_inExpression1533 = new BitSet(new long[]{0x0000000080000002L});
	public static final BitSet FOLLOW_not_key_in_inExpression1553 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_in_key_in_inExpression1557 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_inExpression1559 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_inExpression1581 = new BitSet(new long[]{0x0800000000000400L});
	public static final BitSet FOLLOW_COMMA_in_inExpression1600 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_inExpression1604 = new BitSet(new long[]{0x0800000000000400L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_inExpression1625 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_in_key_in_inExpression1641 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_inExpression1643 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_inExpression1665 = new BitSet(new long[]{0x0800000000000400L});
	public static final BitSet FOLLOW_COMMA_in_inExpression1684 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_inExpression1688 = new BitSet(new long[]{0x0800000000000400L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_inExpression1709 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_shiftExpression_in_relationalExpression1750 = new BitSet(new long[]{0x000201A08C100002L,0x0000000000000002L});
	public static final BitSet FOLLOW_orRestriction_in_relationalExpression1775 = new BitSet(new long[]{0x000201A08C100002L,0x0000000000000002L});
	public static final BitSet FOLLOW_andRestriction_in_orRestriction1810 = new BitSet(new long[]{0x0000000000040002L});
	public static final BitSet FOLLOW_DOUBLE_PIPE_in_orRestriction1832 = new BitSet(new long[]{0x000201A08C100080L,0x0000000000000002L});
	public static final BitSet FOLLOW_fullAnnotation_in_orRestriction1836 = new BitSet(new long[]{0x000201A08C100000L,0x0000000000000002L});
	public static final BitSet FOLLOW_andRestriction_in_orRestriction1842 = new BitSet(new long[]{0x0000000000040002L});
	public static final BitSet FOLLOW_EOF_in_orRestriction1861 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_singleRestriction_in_andRestriction1881 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_DOUBLE_AMPER_in_andRestriction1901 = new BitSet(new long[]{0x000201A08C100080L,0x0000000000000002L});
	public static final BitSet FOLLOW_fullAnnotation_in_andRestriction1922 = new BitSet(new long[]{0x000201A08C100000L,0x0000000000000002L});
	public static final BitSet FOLLOW_singleRestriction_in_andRestriction1927 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_operator_in_singleRestriction1963 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_squareArguments_in_singleRestriction1992 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_shiftExpression_in_singleRestriction1996 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_shiftExpression_in_singleRestriction2009 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_singleRestriction2034 = new BitSet(new long[]{0x000201A08C100000L,0x0000000000000002L});
	public static final BitSet FOLLOW_orRestriction_in_singleRestriction2038 = new BitSet(new long[]{0x0800000000000000L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_singleRestriction2040 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_additiveExpression_in_shiftExpression2064 = new BitSet(new long[]{0x0000008004000002L});
	public static final BitSet FOLLOW_shiftOp_in_shiftExpression2078 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_additiveExpression_in_shiftExpression2080 = new BitSet(new long[]{0x0000008004000002L});
	public static final BitSet FOLLOW_LESS_in_shiftOp2100 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_LESS_in_shiftOp2102 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GREATER_in_shiftOp2114 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_shiftOp2116 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_shiftOp2118 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GREATER_in_shiftOp2130 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_shiftOp2132 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression2160 = new BitSet(new long[]{0x0080020000000002L});
	public static final BitSet FOLLOW_set_in_additiveExpression2181 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression2189 = new BitSet(new long[]{0x0080020000000002L});
	public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression2217 = new BitSet(new long[]{0x8000100000004002L});
	public static final BitSet FOLLOW_set_in_multiplicativeExpression2229 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression2243 = new BitSet(new long[]{0x8000100000004002L});
	public static final BitSet FOLLOW_PLUS_in_unaryExpression2269 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpression2273 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MINUS_in_unaryExpression2291 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpression2295 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_INCR_in_unaryExpression2315 = new BitSet(new long[]{0x800400E0A1001100L,0x0000000000000005L});
	public static final BitSet FOLLOW_primary_in_unaryExpression2317 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DECR_in_unaryExpression2327 = new BitSet(new long[]{0x800400E0A1001100L,0x0000000000000005L});
	public static final BitSet FOLLOW_primary_in_unaryExpression2329 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression2341 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TILDE_in_unaryExpressionNotPlusMinus2371 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2373 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus2382 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2384 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus2398 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_unaryExpressionNotPlusMinus2426 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_COLON_in_unaryExpressionNotPlusMinus2428 = new BitSet(new long[]{0x800400E0A1001100L,0x0000000000000005L});
	public static final BitSet FOLLOW_ID_in_unaryExpressionNotPlusMinus2467 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
	public static final BitSet FOLLOW_UNIFY_in_unaryExpressionNotPlusMinus2469 = new BitSet(new long[]{0x800400E0A1001100L,0x0000000000000005L});
	public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus2514 = new BitSet(new long[]{0x0000004100012002L});
	public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus2531 = new BitSet(new long[]{0x0000004100012002L});
	public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus2561 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression2593 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_primitiveType_in_castExpression2595 = new BitSet(new long[]{0x0800000000000000L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression2597 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_unaryExpression_in_castExpression2601 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression2618 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_type_in_castExpression2620 = new BitSet(new long[]{0x0800000000000000L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression2622 = new BitSet(new long[]{0x800500E0A1001100L,0x0000000000000007L});
	public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression2624 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_boolean_key_in_primitiveType2643 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_char_key_in_primitiveType2651 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_byte_key_in_primitiveType2659 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_short_key_in_primitiveType2667 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_int_key_in_primitiveType2675 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_long_key_in_primitiveType2683 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_float_key_in_primitiveType2691 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_double_key_in_primitiveType2699 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_parExpression_in_primary2727 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_primary2744 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_explicitGenericInvocationSuffix_in_primary2747 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_this_key_in_primary2751 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_arguments_in_primary2753 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literal_in_primary2769 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_super_key_in_primary2791 = new BitSet(new long[]{0x0000002000010000L});
	public static final BitSet FOLLOW_superSuffix_in_primary2793 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_new_key_in_primary2808 = new BitSet(new long[]{0x0000008080000000L});
	public static final BitSet FOLLOW_creator_in_primary2810 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_primitiveType_in_primary2825 = new BitSet(new long[]{0x0000004000010000L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_primary2828 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_primary2830 = new BitSet(new long[]{0x0000004000010000L});
	public static final BitSet FOLLOW_DOT_in_primary2834 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_class_key_in_primary2836 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_inlineMapExpression_in_primary2856 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_inlineListExpression_in_primary2871 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_primary2887 = new BitSet(new long[]{0x4008006010010002L});
	public static final BitSet FOLLOW_DOT_in_primary2921 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_primary2925 = new BitSet(new long[]{0x4008006010010002L});
	public static final BitSet FOLLOW_SHARP_in_primary2965 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_primary2969 = new BitSet(new long[]{0x4008006010010002L});
	public static final BitSet FOLLOW_HASH_in_primary3009 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_primary3013 = new BitSet(new long[]{0x4008006010010002L});
	public static final BitSet FOLLOW_NULL_SAFE_DOT_in_primary3053 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_primary3057 = new BitSet(new long[]{0x4008006010010002L});
	public static final BitSet FOLLOW_identifierSuffix_in_primary3079 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineListExpression3100 = new BitSet(new long[]{0x908502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expressionList_in_inlineListExpression3102 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineListExpression3105 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineMapExpression3126 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_mapExpressionList_in_inlineMapExpression3128 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineMapExpression3130 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_mapEntry_in_mapExpressionList3151 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_COMMA_in_mapExpressionList3154 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_mapEntry_in_mapExpressionList3156 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_expression_in_mapEntry3175 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_COLON_in_mapEntry3177 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_mapEntry3179 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_parExpression3200 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_parExpression3204 = new BitSet(new long[]{0x0800000000000000L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_parExpression3206 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix3240 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix3281 = new BitSet(new long[]{0x0000004000010000L});
	public static final BitSet FOLLOW_DOT_in_identifierSuffix3325 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_class_key_in_identifierSuffix3329 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix3344 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_identifierSuffix3374 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix3402 = new BitSet(new long[]{0x0000004000000002L});
	public static final BitSet FOLLOW_arguments_in_identifierSuffix3418 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator3440 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_createdName_in_creator3443 = new BitSet(new long[]{0x0000006000000000L});
	public static final BitSet FOLLOW_arrayCreatorRest_in_creator3454 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_classCreatorRest_in_creator3458 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_createdName3476 = new BitSet(new long[]{0x0000008000010002L});
	public static final BitSet FOLLOW_typeArguments_in_createdName3478 = new BitSet(new long[]{0x0000000000010002L});
	public static final BitSet FOLLOW_DOT_in_createdName3491 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_createdName3493 = new BitSet(new long[]{0x0000008000010002L});
	public static final BitSet FOLLOW_typeArguments_in_createdName3495 = new BitSet(new long[]{0x0000000000010002L});
	public static final BitSet FOLLOW_primitiveType_in_createdName3510 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_innerCreator3530 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_classCreatorRest_in_innerCreator3532 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3551 = new BitSet(new long[]{0x908502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3561 = new BitSet(new long[]{0x0000005000000000L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3564 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3566 = new BitSet(new long[]{0x0000005000000000L});
	public static final BitSet FOLLOW_arrayInitializer_in_arrayCreatorRest3570 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_arrayCreatorRest3584 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3586 = new BitSet(new long[]{0x0000004000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3591 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_arrayCreatorRest3593 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3595 = new BitSet(new long[]{0x0000004000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3607 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3609 = new BitSet(new long[]{0x0000004000000002L});
	public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer3638 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_variableInitializer3652 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_CURLY_in_arrayInitializer3669 = new BitSet(new long[]{0x848502F1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer3672 = new BitSet(new long[]{0x0400000000000400L});
	public static final BitSet FOLLOW_COMMA_in_arrayInitializer3675 = new BitSet(new long[]{0x808502F1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer3677 = new BitSet(new long[]{0x0400000000000400L});
	public static final BitSet FOLLOW_COMMA_in_arrayInitializer3682 = new BitSet(new long[]{0x0400000000000000L});
	public static final BitSet FOLLOW_RIGHT_CURLY_in_arrayInitializer3689 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_arguments_in_classCreatorRest3706 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation3724 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_arguments_in_explicitGenericInvocation3726 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LESS_in_nonWildcardTypeArguments3743 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments3745 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_nonWildcardTypeArguments3747 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_super_key_in_explicitGenericInvocationSuffix3764 = new BitSet(new long[]{0x0000002000010000L});
	public static final BitSet FOLLOW_superSuffix_in_explicitGenericInvocationSuffix3766 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_explicitGenericInvocationSuffix3777 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_arguments_in_explicitGenericInvocationSuffix3779 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_selector3804 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_super_key_in_selector3808 = new BitSet(new long[]{0x0000002000010000L});
	public static final BitSet FOLLOW_superSuffix_in_selector3810 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_selector3826 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_new_key_in_selector3830 = new BitSet(new long[]{0x0000008080000000L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_selector3833 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_innerCreator_in_selector3837 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_selector3853 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_selector3875 = new BitSet(new long[]{0x0000002000000002L});
	public static final BitSet FOLLOW_arguments_in_selector3904 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_selector3925 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_selector3952 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_selector3977 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_arguments_in_superSuffix3996 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_superSuffix4007 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_superSuffix4009 = new BitSet(new long[]{0x0000002000000002L});
	public static final BitSet FOLLOW_arguments_in_superSuffix4018 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_squareArguments4041 = new BitSet(new long[]{0x908502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expressionList_in_squareArguments4046 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_squareArguments4052 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_arguments4069 = new BitSet(new long[]{0x888502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expressionList_in_arguments4081 = new BitSet(new long[]{0x0800000000000000L});
	public static final BitSet FOLLOW_RIGHT_PAREN_in_arguments4092 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expression_in_expressionList4122 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_COMMA_in_expressionList4133 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_expression_in_expressionList4137 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4158 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_PLUS_ASSIGN_in_assignmentOperator4166 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MINUS_ASSIGN_in_assignmentOperator4174 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MULT_ASSIGN_in_assignmentOperator4182 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DIV_ASSIGN_in_assignmentOperator4190 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AND_ASSIGN_in_assignmentOperator4198 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OR_ASSIGN_in_assignmentOperator4206 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_XOR_ASSIGN_in_assignmentOperator4214 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MOD_ASSIGN_in_assignmentOperator4222 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LESS_in_assignmentOperator4230 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_LESS_in_assignmentOperator4232 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4234 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GREATER_in_assignmentOperator4251 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_assignmentOperator4253 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_assignmentOperator4255 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4257 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GREATER_in_assignmentOperator4272 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_assignmentOperator4274 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4276 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_extends_key4306 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_super_key4335 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_instanceof_key4364 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_boolean_key4393 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_char_key4422 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_byte_key4451 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_short_key4480 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_int_key4509 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_float_key4538 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_long_key4567 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_double_key4596 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_void_key4625 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_this_key4654 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_class_key4683 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_new_key4713 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_not_key4742 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_in_key4769 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_operator_key4794 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_neg_operator_key4819 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_primitiveType_in_synpred1_DRL5Expressions548 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred2_DRL5Expressions559 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred2_DRL5Expressions561 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_typeArguments_in_synpred3_DRL5Expressions585 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_typeArguments_in_synpred4_DRL5Expressions599 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred5_DRL5Expressions611 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred5_DRL5Expressions613 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_assignmentOperator_in_synpred6_DRL5Expressions782 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_not_key_in_synpred7_DRL5Expressions1547 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_in_key_in_synpred7_DRL5Expressions1549 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_operator_in_synpred8_DRL5Expressions1764 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_synpred8_DRL5Expressions1768 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOUBLE_PIPE_in_synpred9_DRL5Expressions1821 = new BitSet(new long[]{0x000201A08C100080L,0x0000000000000002L});
	public static final BitSet FOLLOW_fullAnnotation_in_synpred9_DRL5Expressions1823 = new BitSet(new long[]{0x000201A08C100000L,0x0000000000000002L});
	public static final BitSet FOLLOW_andRestriction_in_synpred9_DRL5Expressions1827 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOUBLE_AMPER_in_synpred10_DRL5Expressions1890 = new BitSet(new long[]{0x000201808C100080L,0x0000000000000002L});
	public static final BitSet FOLLOW_fullAnnotation_in_synpred10_DRL5Expressions1892 = new BitSet(new long[]{0x000201808C100000L,0x0000000000000002L});
	public static final BitSet FOLLOW_operator_in_synpred10_DRL5Expressions1896 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_squareArguments_in_synpred11_DRL5Expressions1984 = new BitSet(new long[]{0x808502E1A1003100L,0x0000000000000007L});
	public static final BitSet FOLLOW_shiftExpression_in_synpred11_DRL5Expressions1986 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_shiftOp_in_synpred12_DRL5Expressions2075 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_castExpression_in_synpred14_DRL5Expressions2395 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_selector_in_synpred15_DRL5Expressions2528 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_synpred17_DRL5Expressions2586 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_primitiveType_in_synpred17_DRL5Expressions2588 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_synpred18_DRL5Expressions2611 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_type_in_synpred18_DRL5Expressions2613 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_parExpression_in_synpred19_DRL5Expressions2721 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred20_DRL5Expressions2740 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literal_in_synpred21_DRL5Expressions2765 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_super_key_in_synpred22_DRL5Expressions2787 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_new_key_in_synpred23_DRL5Expressions2804 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_primitiveType_in_synpred24_DRL5Expressions2821 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_inlineMapExpression_in_synpred25_DRL5Expressions2852 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_inlineListExpression_in_synpred26_DRL5Expressions2867 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_synpred27_DRL5Expressions2882 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_synpred28_DRL5Expressions2916 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_synpred28_DRL5Expressions2918 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SHARP_in_synpred29_DRL5Expressions2960 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_synpred29_DRL5Expressions2962 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_HASH_in_synpred30_DRL5Expressions3004 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_synpred30_DRL5Expressions3006 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NULL_SAFE_DOT_in_synpred31_DRL5Expressions3048 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_synpred31_DRL5Expressions3050 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_identifierSuffix_in_synpred32_DRL5Expressions3076 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred33_DRL5Expressions3234 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred33_DRL5Expressions3236 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred34_DRL5Expressions3339 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred35_DRL5Expressions3601 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred35_DRL5Expressions3603 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_synpred36_DRL5Expressions3799 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_super_key_in_synpred36_DRL5Expressions3801 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_synpred37_DRL5Expressions3821 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_new_key_in_synpred37_DRL5Expressions3823 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_synpred38_DRL5Expressions3848 = new BitSet(new long[]{0x0000000080000000L});
	public static final BitSet FOLLOW_ID_in_synpred38_DRL5Expressions3850 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_synpred39_DRL5Expressions3899 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred40_DRL5Expressions3922 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEFT_PAREN_in_synpred41_DRL5Expressions4013 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GREATER_in_synpred42_DRL5Expressions4243 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_synpred42_DRL5Expressions4245 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_synpred42_DRL5Expressions4247 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GREATER_in_synpred43_DRL5Expressions4266 = new BitSet(new long[]{0x0000000004000000L});
	public static final BitSet FOLLOW_GREATER_in_synpred43_DRL5Expressions4268 = new BitSet(new long[]{0x0000000000000002L});
}
