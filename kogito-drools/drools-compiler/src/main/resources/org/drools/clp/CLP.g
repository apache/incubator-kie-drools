grammar CLP;

@parser::header {
	package org.drools.clp;
	import java.util.List;
	import java.util.ArrayList;
	import java.util.Iterator;
	import java.util.HashMap;	
	import java.util.StringTokenizer;
	import org.drools.lang.descr.*;
	import org.drools.compiler.SwitchingCommonTokenStream;
}

@parser::members {
	private PackageDescr packageDescr;
	private List errors = new ArrayList();
	private String source = "unknown";
	private int lineOffset = 0;
	private DescrFactory factory = new DescrFactory();
	private boolean parserDebug = false;
	
	// THE FOLLOWING LINE IS A DUMMY ATTRIBUTE TO WORK AROUND AN ANTLR BUG
	private BaseDescr from = null;
	
	public void setParserDebug(boolean parserDebug) {
		this.parserDebug = parserDebug;
	}
	
	public void debug(String message) {
		if ( parserDebug ) 
			System.err.println( "drl parser: " + message );
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	public DescrFactory getFactory() {
		return factory;
	}	

	public String getSource() {
		return this.source;
	}
	
	public PackageDescr getPackageDescr() {
		return packageDescr;
	}
	
	private int offset(int line) {
		return line + lineOffset;
	}
	
	/**
	 * This will set the offset to record when reparsing. Normally is zero of course 
	 */
	public void setLineOffset(int i) {
	 	this.lineOffset = i;
	}
	
	private String getString(Token token) {
		String orig = token.getText();
		return orig.substring( 1, orig.length() -1 );
	}
	
	public void reportError(RecognitionException ex) {
	        // if we've already reported an error and have not matched a token
                // yet successfully, don't report any errors.
                if ( errorRecovery ) {
                        return;
                }
                errorRecovery = true;

		ex.line = offset(ex.line); //add the offset if there is one
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
                                                           mtne.foundNode+
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
                } else if (e instanceof GeneralParseException) {
			message.append(" " + e.getMessage());
		}
               	return message.toString();
        }   
        
        void checkTrailingSemicolon(String text, int line) {
        	if (text.trim().endsWith( ";" ) ) {
        		this.errors.add( new GeneralParseException( "Trailing semi-colon not allowed", offset(line) ) );
        	}
        }
      
}

@lexer::header {
	package org.drools.clp;
}

opt_semicolon
	: ';'?
	;

/*
compilation_unit
	:	prolog 
		( statement )+
	;
	
prolog
	@init {
		String packageName = "";
	}
	:	( n=package_statement { packageName = n; } )?
		{ 
			this.packageDescr = factory.createPackage( packageName ); 
		}
	;
	
statement
	:
	(	import_statement 
	|       function_import_statement 
	|	global 
	|	function 
	|       t=template {this.packageDescr.addFactTemplate( t ); }
	|	r=rule { if( r != null ) this.packageDescr.addRule( r ); }			
	|	q=query	{ if( q != null ) this.packageDescr.addRule( q ); }
	) 
	;

package_statement returns [String packageName]
	@init{
		packageName = null;
	}
	:	
		PACKAGE n=dotted_name[null] opt_semicolon
		{
			packageName = n;
		}
	;	
*/
rule returns [RuleDescr rule]
	@init { 
	        rule = null; 
	        AndDescr lhs = null;
	        ColumnDescr colum = null;	        
	      }
	:	loc=LEFT_PAREN 'defrule' 	
		(	d=agenda_group {  rule.addAttribute( d ); } '::' )?
		
	  	ruleName=ID 
	  	{ 
	  		debug( "start rule: " + ruleName.getText() );
	        rule = new RuleDescr( ruleName.getText(), null ); 
			rule.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			rule.setStartCharacter( ((CommonToken)loc).getStartIndex() ); 
		
			// not sure how you define where a LHS starts in clips, so just putting it here for now
  	        lhs = new AndDescr(); 
  	        rule.setLhs( lhs ); 
   	        lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			lhs.setStartCharacter( ((CommonToken)loc).getStartIndex() );				
		}
		documentation=STRING {
	    	// do nothing here for now
		}
		ruleAttribute[rule]*
		
		lhs[lhs]*
		
		rhs[rule]
		
		RIGHT_PAREN
	;

ruleAttribute[RuleDescr rule]
	:
		LEFT_PAREN 'declare'
			LEFT_PAREN d=salience { rule.addAttribute( d ); }
			RIGHT_PAREN
		RIGHT_PAREN
	;

agenda_group returns [AttributeDescr d ]
	@init {
		d = null;
	}
	:
		t=ID   
		{
			d = new AttributeDescr( "agenda-group", t.getText() );
			d.setLocation( offset(t.getLine()), t.getCharPositionInLine() );
			d.setStartCharacter( ((CommonToken)t).getStartIndex() );
			d.setEndCharacter( ((CommonToken)t).getStopIndex() );
		}	
	;	

salience returns [AttributeDescr d ]
	@init {
		d = null;
	}
	:	
		loc=SALIENCE i=INT   
		{
			d = new AttributeDescr( "salience", i.getText() );
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
			d.setEndCharacter( ((CommonToken)i).getStopIndex() );
		}
	;
		
/* lhs is slightly different to ce as lhs allows pattern bindings, ce doesn't */
lhs[ConditionalElementDescr in_ce]
	:	(   and_ce[in_ce]	
		  | or_ce[in_ce]
		  | not_ce[in_ce]
		  | exists_ce[in_ce]		  
 		  | eval_ce[in_ce] 		  
		  | normal_pattern[in_ce]
		  | bound_pattern[in_ce]
		)
	;

rhs[RuleDescr rule]
	@init {
	        ExecutionEngine engine = new BlockExecutionEngine();
			ExecutionBuildContext context = new ExecutionBuildContext( engine );  	
	}
	
	:
		'=>'
	  function[context]* { rule.setConsequence( engine ); }		
	;	
	
ce[ConditionalElementDescr in_ce]
	:	(   and_ce[in_ce]	
	      | or_ce[in_ce]
		  | not_ce[in_ce]
		  | exists_ce[in_ce]		  
 		  | eval_ce[in_ce] 		  	      
	      | normal_pattern[in_ce]
	    )
	;
	
	
and_ce[ConditionalElementDescr in_ce]
    @init {
        AndDescr andDescr= null;        
    }
	:	LEFT_PAREN	
		AND {
	    	andDescr = new AndDescr();
			in_ce.addDescr( andDescr );
		}
		ce[andDescr]*		 
		RIGHT_PAREN					
	;	
	
or_ce[ConditionalElementDescr in_ce]
    @init {
        OrDescr orDescr= null;         
    }
	:	LEFT_PAREN	
		OR {
	    	orDescr = new OrDescr();
			in_ce.addDescr( orDescr );
		}
		ce[orDescr]*		 
		RIGHT_PAREN					
	;	
	
not_ce[ConditionalElementDescr in_ce]
    @init {
        NotDescr notDescr= null;         
    }
	:	LEFT_PAREN	
		NOT {
			notDescr = new NotDescr();
		    in_ce.addDescr( notDescr );
		}
		ce[notDescr]?		 
		RIGHT_PAREN					
	;		
	
exists_ce[ConditionalElementDescr in_ce]
    @init {
        ExistsDescr existsDescr= null;        
    }
	:	LEFT_PAREN	
		EXISTS {
		    existsDescr = new ExistsDescr();
		    in_ce.addDescr( existsDescr );
		}
		ce[existsDescr]?		 
		RIGHT_PAREN					
	;		

eval_ce[ConditionalElementDescr in_ce]
    @init {
        EvalDescr evalDescr= null;    
   		ExecutionEngine engine = new CLPEval();     
		ExecutionBuildContext context = new ExecutionBuildContext( engine );   		         
    }
	:	LEFT_PAREN	
		TEST {
		    evalDescr = new EvalDescr();
		    in_ce.addDescr( evalDescr );
		}
		function[context] {					
			evalDescr.setContent( engine );			
		}			 
		RIGHT_PAREN					
	;		

normal_pattern[ConditionalElementDescr in_ce]
    @init {
        ColumnDescr column = null;
    }
	:	LEFT_PAREN 
		name=ID {
			column = new ColumnDescr(name.getText());
			in_ce.addDescr( column );
		}
		field_constriant[column]* 	  
		RIGHT_PAREN
	;		
	


bound_pattern[ConditionalElementDescr in_ce]
    @init {
        ColumnDescr column = null;
        String identifier = null;
    }
	:	var=VAR {
			identifier = var.getText();
		}
		'<-' 
		LEFT_PAREN 
		name=ID {
			column = new ColumnDescr(name.getText());
			column.setIdentifier( identifier );
			in_ce.addDescr( column );	    
		}
		field_constriant[column]* 
		RIGHT_PAREN	
	;			
	
field_constriant[ColumnDescr column] 
	@init {
     	List list = new ArrayList();
		FieldBindingDescr fbd = null;
		FieldConstraintDescr fc = null;
		String op = "==";
	}    
	:	LEFT_PAREN 
		f=ID {
			fc = new FieldConstraintDescr(f.getText());
			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
			fc.setStartCharacter( ((CommonToken)f).getStartIndex() );
			column.addDescr( fc );			
		}	  
		
		restriction[fc, column] 
		(
		  connective[fc]	      
	      restriction[fc, column]      	      
		)*
		RIGHT_PAREN		
	;
	
connective	[FieldConstraintDescr fc]
	:	(	AMPERSAND { fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND)); }
	    	| PIPE {fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR)); }	      
		)		
	;
	
restriction[FieldConstraintDescr fc, ColumnDescr column]
	@init {
			String op = "==";
	}
	:	(TILDE{op = "!=";})?	 	  	 
		(		predicate_constraint[op, column]	  	  	
	  	    |	return_value_restriction[op, fc]
	  	  	|	variable_restriction[op, fc]
	  	    | 	lc=literal_restriction {
     	    	fc.addRestriction( new LiteralRestrictionDescr(op, lc, true) );
		      	op = "==";
		      } 	  	  	  
		)		
	;		

predicate_constraint[String op, ColumnDescr column]	
    @init {
   		ExecutionEngine engine = new CLPPredicate();
		ExecutionBuildContext context = new ExecutionBuildContext( engine );    
    }
	:	':'
		function[context] {	
			column.addDescr( new PredicateDescr( engine ) );
		}	
	;


return_value_restriction[String op, FieldConstraintDescr fc]
	@init {
		ExecutionEngine engine = new CLPReturnValue();
		ExecutionBuildContext context = new ExecutionBuildContext( engine );
	}
	:	'=' 
		function[context] {					
			fc.addRestriction( new ReturnValueRestrictionDescr (op, engine ) );
		}		
	;
		
variable_restriction[String op, FieldConstraintDescr fc]
	:	var=VAR {
			fc.addRestriction( new VariableRestrictionDescr(op, var.getText()) );
		}
	;	

	
literal_restriction returns [String text]
	@init {
		text = null;
	}
	:
	    t=literal {
	    	text = t;
	    }
	;		

function[ExecutionBuildContext context] returns[Function f]
	@init {
	    FunctionFactory factory = FunctionFactory.getInstance();
	}
	:	LEFT_PAREN
		name=function_name {
			if ( name.getText().equals("bind") ) {
		  		context.createLocalVariable( name.getText() );
			}
		  	f = factory.createFunction( name.getText() );		  
		}
	    	
		function_params[context, f]+ 
	    RIGHT_PAREN
	    { context.addFunction( f ); }
	;


modify_function[ExecutionBuildContext context] returns[Function f]
	@init {
	    FunctionFactory factory = FunctionFactory.getInstance();
		f = factory.createFunction( "modify" );
	}
	:
		LEFT_PAREN
			'modify'
			slot_name_value_pair[context, f]+
		RIGHT_PAREN		
	;	
	
function_params[ExecutionBuildContext context, Function f]
	@init {
		ValueHandler value  =  null;		
	}
	:
		(		t=VAR		{ value = context.getVariableValueHandler(t.getText() ); }
			|	t=STRING    { value = new ObjectLiteralValue( getString( t ) ); }
			| 	t=ID        { value = new ObjectLiteralValue( t.getText() ); }			
			|	t=FLOAT     { value = new DoubleLiteralValue( t.getText() ); }
			|	t=INT       { value = new LongLiteralValue( t.getText() ); }			
			|	t=BOOL      { value = new BooleanLiteralValue( t.getText() ); }						
			|	t=NULL      { value = ObjectLiteralValue.NULL; }
			|	nf=function[context] { value = nf; }			
		)	
		{ f.addParameter( value ); }	
		
	;		
	
slot_name_value_pair[ExecutionBuildContext context, Function f]
	@init {
		SlotNameValuePair nameValuePair = null;
		String name = null;
	}
	:
		LEFT_PAREN
		id=ID {
			name = id.getText();
		}
		(		t=VAR       { nameValuePair = new SlotNameValuePair(name, context.getVariableValueHandler( t.getText() ) ); }
			| 	t=STRING    { nameValuePair = new SlotNameValuePair(name, new ObjectLiteralValue( getString( t ) ) ); }
			| 	t=ID        { nameValuePair = new SlotNameValuePair(name, new ObjectLiteralValue( t.getText() ) ); }			
			|	t=FLOAT     { nameValuePair = new SlotNameValuePair(name, new DoubleLiteralValue( t.getText() ) ); }
			|	t=INT       { nameValuePair = new SlotNameValuePair(name, new LongLiteralValue( t.getText() ) ); }			
			|	t=BOOL      { nameValuePair = new SlotNameValuePair(name, new BooleanLiteralValue( t.getText() ) ) ; }						
			|	t=NULL      { nameValuePair = new SlotNameValuePair(name, ObjectLiteralValue.NULL ); }
			|	nf=function[context]
			                { nameValuePair = new SlotNameValuePair(name, nf ); }
		)	
		{ f.addParameter( nameValuePair ); }		
		RIGHT_PAREN
	;	
	
literal returns [String text]
	@init {
		text = null;
	}
	:	(   t=STRING { text = getString( t ); } 
		  | t=ID     { text = t.getText(); }
		  | t=INT    { text = t.getText(); }
		  | t=FLOAT	 { text = t.getText(); }
		  | t=BOOL 	 { text = t.getText(); }
		  | t=NULL   { text = null; }
		)
	;
	
function_name returns [Token tok]
	:
	(	t=ID	
	|	t=MISC
	|	t=SYMBOL
	)
	{
	    tok = t;
	}
	;
	
	
DEFRULE	:	'defrule';
OR 	:	'or';
AND 	:	'and';
NOT 	:	'not';
EXISTS 	:	'exists';
TEST 	:	'test';

NULL	:	'null';

WS      :       (	' '
                |	'\t'
                |	'\f'
                |	EOL
                )
                { $channel=HIDDEN; }
        ;        
        
DECLARE 
	:	'declare';        		

SALIENCE 
	:	'salience';

fragment
EOL 	:	     
   		(       ( '\r\n' )=> '\r\n'  // Evil DOS
                |       '\r'    // Macintosh
                |       '\n'    // Unix (the right way)
                )
        ;  
        
INT	
	:	('-')?('0'..'9')+
	;

FLOAT
	:	('-')?('0'..'9')+ '.' ('0'..'9')+
	;

STRING
    :  ('"' ( EscapeSequence | ~('\\'|'"') )* '"')
     | ('\'' ( EscapeSequence | ~('\\'|'\'') )* '\'')
    ;

fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UnicodeEscape
    |   OctalEscape
    ;

fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

BOOL
	:	('true'|'false') 
	;
	
VAR 	: '?'('a'..'z'|'A'..'Z'|'_'|'$'|'\u00c0'..'\u00ff')('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\u00c0'..'\u00ff')* 
        ;

ID	
	:	('a'..'z'|'A'..'Z'|'_'|'$'|'\u00c0'..'\u00ff')('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\u00c0'..'\u00ff')* 
	;

SH_STYLE_SINGLE_LINE_COMMENT	
	:	'#' ( options{greedy=false;} : .)* EOL /* ('\r')? '\n'  */
                { $channel=HIDDEN; }
	;
        
        
C_STYLE_SINGLE_LINE_COMMENT	
	:	'//' ( options{greedy=false;} : .)* EOL // ('\r')? '\n' 
                { $channel=HIDDEN; }
	;


LEFT_PAREN
	:	'('
	;

RIGHT_PAREN
	:	')'
	;
        
LEFT_SQUARE
	:	'['
	;

RIGHT_SQUARE
	:	']'
	;        

LEFT_CURLY
	:	'{'
	;

RIGHT_CURLY
	:	'}'
	;
	
TILDE	:	'~'
	;	
	
AMPERSAND 
	:	'&'
	;
	
PIPE
	:	'|'
	;		
        
MULTI_LINE_COMMENT
	:	'/*' (options{greedy=false;} : .)* '*/'
                { $channel=HIDDEN; }
	;

MISC 	:
		'!' | '@' | '$' | '%' | '^' | '*' | '_' | '-' | '+'  | '?' | ',' | '=' | '/' | '\'' | '\\' | 
		'<' | '>' | '<=' | '>=' 
	;		

SYMBOL
	:	((~(' '|'\t'|'\n'|'\r'|'"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$'))|('$' ~('?'|' '|'\t'|'\n'|'\r'|'"'|'('|')'|';'|'&'|'|'|'~'|'<'))) 
	         (~(' '|'\t'|'\n'|'\r'|'"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?'))*
	;

