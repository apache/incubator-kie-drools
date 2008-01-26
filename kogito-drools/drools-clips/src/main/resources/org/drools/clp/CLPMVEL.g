grammar CLPMVEL;

@parser::header {
	package org.drools.clp;
	
    import org.drools.clp.mvel.*;	
    
	import org.drools.clp.valuehandlers.*;
	import java.util.List;
	import java.util.ArrayList;
	import java.util.Iterator;
	import java.util.HashMap;	
	import java.util.Set;	
	import java.util.HashSet;			
	import java.util.StringTokenizer;
	import org.drools.lang.descr.*;
	import org.drools.lang.Location;	
}

@parser::members {
	private PackageDescr packageDescr;
	private List errors = new ArrayList();
	private String source = "unknown";
	private int lineOffset = 0;
	private DescrFactory factory = new DescrFactory();
	private boolean parserDebug = false;
	private FunctionRegistry functionRegistry;	
	private Location location = new Location( Location.LOCATION_UNKNOWN );	
	
	public void setFunctionRegistry(FunctionRegistry functionRegistry) {
		this.functionRegistry = functionRegistry;
	}
	
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
                                                           //mtne.foundNode+ FIXME
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

/*
opt_semicolon
	: ';'?
	;


compilation_unit
	:	
		( statement )+
	;
*/
/*
statement
	:
	//later we add the other possible statements here 
	(  //do something with the returned rule here )
	;
*/		
/* prolog
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

eval_script[Shell  shell]
	:	/*(		  i=importDescr{ shell.importDescrHandler( i ); }
				| r=defrule { shell.ruleDescrHandler( r ); }
				//e=execution_block { parserHandler.lispFormHandler( e ); }
				| fc=lisp_list[shell, new LispForm(shell) ] { shell.lispFormHandler(fc); }
		)**/
	;
	
importDescr returns[ImportDescr importDescr]
	: LEFT_PAREN 'import' importName=NAME { importDescr = new ImportDescr( importName.getText() ); }RIGHT_PAREN
	;	
/*	

execution_list returns[ExecutionEngine engine]
	@init {
	        engine = new BlockExecutionEngine();
			BuildContext context = new ExecutionBuildContext( engine, functionRegistry );  	
	}
	
	:
		(fc=lisp_list[context, new LispForm(context) ] { context.addFunction( (FunctionCaller) fc ); })
	;	
*/	

/*
deffunction returns[Deffunction function]
	@init {
			BuildContext context = null;  	
	}
	:	loc=LEFT_PAREN	 
	  	DEFFUNCTION 
	  	ruleName=NAME {
	    	function = new Deffunction( ruleName.getText() );
			functionRegistry.addFunction( function );
	      	context = new ExecutionBuildContext( function, functionRegistry );
	  	}
		loc=LEFT_PAREN	 
		 (v=VAR {
			context.addVariable( function.addParameter( v.getText() ) );
		 })*	  
	 	 RIGHT_PAREN
	  	(fc=lisp_list[context, new LispForm(context) ] { context.addFunction( (FunctionCaller) fc ); })*
	  	RIGHT_PAREN
	;
*/	
	
/*	
deffunction_params[BuildContext context]
	:	loc=LEFT_PAREN	 
		 (v=VAR {
		    // this creates a parameter on the underlying function
		 	context.createLocalVariable( v.getText() );
		 })*	  
	 	 RIGHT_PAREN	
	;	
*/
defrule returns [RuleDescr rule]
	@init { 
	        rule = null; 
	        AndDescr lhs = null;
	        PatternDescr colum = null;
	        AttributeDescr module = null;	      
            Set declarations = null;  
	      }
	:	loc=LEFT_PAREN 
		
		DEFRULE ruleName=NAME
	  	{ 	  			  		
	  		debug( "start rule: " + ruleName.getText() );
	  		String ruleStr = ruleName.getText();

	        if ( ruleStr.indexOf("::") >= 0 ) {
	            String mod = ruleStr.substring(0, ruleStr.indexOf("::"));
	            ruleStr = ruleStr.substring(ruleStr.indexOf("::")+2);
				module = new AttributeDescr( "agenda-group", mod );
				module.setLocation( offset(ruleName.getLine()), ruleName.getCharPositionInLine() );
				module.setStartCharacter( ((CommonToken)ruleName).getStartIndex() );
				module.setEndCharacter( ((CommonToken)ruleName).getStopIndex() );
			}
		    
		    rule = new RuleDescr( ruleStr, null ); 
		    if( module != null ) {
		    	rule.addAttribute( module );
		    }
		        
			rule.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			rule.setStartCharacter( ((CommonToken)loc).getStartIndex() ); 
		
			// not sure how you define where a LHS starts in clips, so just putting it here for now
        	lhs = new AndDescr(); 
  	        rule.setLhs( lhs ); 
	        lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			lhs.setStartCharacter( ((CommonToken)loc).getStartIndex() );	
			
			rule.addAttribute( new AttributeDescr( "dialect", "clips") );	
			
			declarations = new HashSet();  											
		}
		documentation=STRING {
	    	// do nothing here for now
		}
		ruleAttribute[rule]
		
		ce[lhs, declarations]*
		
		'=>'
		
		t=lisp_list { rule.setConsequence( t ); }
		
		RIGHT_PAREN
	;


ruleAttribute[RuleDescr rule]
	:
		( LEFT_PAREN 'declare'
			( LEFT_PAREN d=salience { rule.addAttribute( d ); } RIGHT_PAREN )?
		RIGHT_PAREN )?
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
		

ce[ConditionalElementDescr in_ce, Set declarations]
	:	(   and_ce[in_ce, declarations]	
		  | or_ce[in_ce, declarations]
		  | not_ce[in_ce, declarations]
		  | exists_ce[in_ce, declarations]		  
 		  | eval_ce[in_ce, declarations] 		  
		  | normal_pattern[in_ce, declarations]
		  | bound_pattern[in_ce, declarations]
		)
	;
	
and_ce[ConditionalElementDescr in_ce, Set declarations]
    @init {
        AndDescr andDescr= null;        
    }
	:	LEFT_PAREN	
		AND {
	    	andDescr = new AndDescr();
			in_ce.addDescr( andDescr );
		}
		ce[andDescr, declarations]+		 
		RIGHT_PAREN					
	;	
	
or_ce[ConditionalElementDescr in_ce, Set declarations]
    @init {
        OrDescr orDescr= null;         
    }
	:	LEFT_PAREN	
		OR {
	    	orDescr = new OrDescr();
			in_ce.addDescr( orDescr );
		}
		ce[orDescr, declarations]+		 
		RIGHT_PAREN					
	;	
	
not_ce[ConditionalElementDescr in_ce, Set declarations]
    @init {
        NotDescr notDescr= null;         
    }
	:	LEFT_PAREN	
		NOT {
			notDescr = new NotDescr();
		    in_ce.addDescr( notDescr );
		}
		ce[notDescr, declarations]		 
		RIGHT_PAREN					
	;		
	
exists_ce[ConditionalElementDescr in_ce, Set declarations]
    @init {
        ExistsDescr existsDescr= null;        
    }
	:	LEFT_PAREN	
		EXISTS {
		    existsDescr = new ExistsDescr();
		    in_ce.addDescr( existsDescr );
		}
		ce[existsDescr, declarations]		 
		RIGHT_PAREN					
	;		

eval_ce[ConditionalElementDescr in_ce, Set declarations]
	:	LEFT_PAREN	
		TEST 
		t=lisp_list { EvalDescr evalDescr = new EvalDescr(); evalDescr.setContent( t ); in_ce.addDescr( evalDescr ); }			 
		RIGHT_PAREN					
	;		

normal_pattern[ConditionalElementDescr in_ce, Set declarations]
    @init {
        PatternDescr pattern = null;
        ConditionalElementDescr top = null;
    }
	:	LEFT_PAREN 
		name=NAME {
			pattern = new PatternDescr(name.getText());
			in_ce.addDescr( pattern );
			top = pattern.getConstraint();
			
		}
		field_constriant[top, declarations]* 	  
		RIGHT_PAREN
	;		
	


bound_pattern[ConditionalElementDescr in_ce, Set declarations]
    @init {
        PatternDescr pattern = null;
        String identifier = null;
        ConditionalElementDescr top = null;        
    }
	:	var=VAR {
			identifier = var.getText();
		}
		ASSIGN_OP LEFT_PAREN name=NAME 
		{
			pattern = new PatternDescr(name.getText());
			pattern.setIdentifier( identifier );
			in_ce.addDescr( pattern );
			top = pattern.getConstraint();				    
		}
		field_constriant[top, declarations]* 
		RIGHT_PAREN	
	;			
	
field_constriant[ConditionalElementDescr base, Set declarations] 
	@init {
     	List list = new ArrayList();
		FieldBindingDescr fbd = null;
		FieldConstraintDescr fc = null;
		RestrictionConnectiveDescr top = null;		
		String op = "==";
	}    
	:	
		LEFT_PAREN f=NAME 
		{
			fc = new FieldConstraintDescr(f.getText());
			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
			fc.setStartCharacter( ((CommonToken)f).getStartIndex() );
			base.addDescr( fc );	
			top = fc.getRestriction();		
		}	  
		
		or_restr_connective[top, base, fc, declarations] 
		RIGHT_PAREN		
	;
/*	
connected_constraint[RestrictionConnectiveDescr rc, ConditionalElementDescr base]
	:
	restriction[rc, base]
	( 
	    AMPERSAND { rc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND)); }
	    connected_constraint[rc, base]
	| 
	    PIPE {rc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR)); }
	    connected_constraint[rc, base]
	)?
	;	
*/


or_restr_connective[ RestrictionConnectiveDescr rcBase, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations ]
	options { 
		backtrack=true;
	}
	@init {
		RestrictionConnectiveDescr or = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR);
	}
	:
		and_restr_connective[or, ceBase, fcBase, declarations] 
		( 
			options {backtrack=true;}
			: PIPE
			{
				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
			}
			and_restr_connective[or, ceBase, fcBase, declarations] 
		)*
	;
	finally {
	        if( or.getRestrictions().size() == 1 ) {
	                $rcBase.addOrMerge( (RestrictionDescr) or.getRestrictions().get( 0 ) );
	        } else if ( or.getRestrictions().size() > 1 ) {
	        	$rcBase.addRestriction( or );
	        }
	}

and_restr_connective[ RestrictionConnectiveDescr rcBase, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations ]
	@init {
		RestrictionConnectiveDescr and = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND);
	}
	:
		restriction[and, ceBase, fcBase, declarations] 
		( AMPERSAND restriction[and, ceBase, fcBase, declarations] )*
		/*
		(	options {backtrack=true;}
		:	t=AMPERSAND 
			{
				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
			}
			restriction[and, ceBase] 
		)*
		*/
	;
	finally {
	        if( and.getRestrictions().size() == 1) {
	                $rcBase.addOrMerge( (RestrictionDescr) and.getRestrictions().get( 0 ) );
	        } else if ( and.getRestrictions().size() > 1 ) {
	        	$rcBase.addRestriction( and );
	        }
	}
	
restriction[RestrictionConnectiveDescr rc, ConditionalElementDescr base, FieldConstraintDescr fcBase, Set declarations ]
	@init {
			String op = "==";
	}
	:	(TILDE{op = "!=";})?	 	  	 
		(	predicate_constraint[rc, op, base]	  	  	
	  	|	return_value_restriction[op, rc]
	  	|	variable_restriction[op, rc, base, fcBase, declarations]
	  	| 	lc=literal_restriction {
     	    			rc.addRestriction( new LiteralRestrictionDescr(op, lc) );
		      		op = "==";
		        } 	  	  	  
		)		
	;		

predicate_constraint[RestrictionConnectiveDescr rc, String op, ConditionalElementDescr base]	
	:	COLON
		t=lisp_list { $rc.addRestriction( new PredicateDescr( t ) ); }	
		
	;


return_value_restriction[String op, RestrictionConnectiveDescr rc]
	:	EQUALS 
		t=lisp_list {rc.addRestriction( new ReturnValueRestrictionDescr (op, t ) ); }		
	;
		
//will add a declaration field binding, if this is the first time the name  is used		
variable_restriction[String op, RestrictionConnectiveDescr rc, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations ]
	:	VAR {
	        if ( declarations.contains( $VAR.text ) ) {
				rc.addRestriction( new VariableRestrictionDescr(op, $VAR.text) );
		 	} else {
		 		FieldBindingDescr fbd = new FieldBindingDescr();
		 		fbd.setIdentifier( $VAR.text );		
		 		fbd.setFieldName( fcBase.getFieldName() ); 		
		 		ceBase.insertBeforeLast( FieldConstraintDescr.class, fbd );
		 		declarations.add( $VAR.text );
		 	}
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

 
eval_sExpressions returns[List<SExpression> list]
    @init {
		list = new ArrayList<SExpression>();
    }
	:
		(a=lisp_list { list.add( a ); })*
//		{ sExpressions = ( SExpression[] ) list.toArray( new SExpression[ list.size () ] ); }
	;
	
lisp_list returns[SExpression sExpression]
    @init {
        List list = new ArrayList();
        sExpression = null;
    }
	:	LEFT_PAREN	
	
		(
		    t=NAME { list.add( new SymbolLispAtom( t.getText() ) ); }
		    |
		    t=VAR { list.add( new VariableLispAtom( t.getText() ) ); }	    
	    )
		(		a=lisp_atom	{ list.add( a ); }
			|	a=lisp_list	{ list.add( a ); }
		)*								    	
	    RIGHT_PAREN
	    { sExpression = new LispForm( ( SExpression[] ) list.toArray( new SExpression[ list.size () ] ) ); }
	;
	
lisp_atom returns[SExpression sExpression] 
	@init {
		sExpression  =  null;		
	}
	:
		/*(		
			 	t=FLOAT		{ sExpression = new LispAtom2( t.getText() ); }
			|	t=INT 		{ sExpression = new LispAtom2( t.getText() ); }			
			|	t=BOOL		{ sExpression = new LispAtom2( t.getText() ); }						
			|	t=NULL		{ sExpression = new LispAtom2( null ); }
			|	t=STRING	{ sExpression = new LispAtom2( getString( t ) ); }
			| 	t=NAME		{ sExpression = new LispAtom2( t.getText() ); }			

		)*/	
		
		(		
			 	t=VAR		{ sExpression = new VariableLispAtom( t.getText() ); }
			|	t=STRING	{ sExpression = new StringLispAtom( getString( t ) ); }											
			|	t=FLOAT		{ sExpression = new FloatLispAtom( t.getText() ); }
			|	t=INT		{ sExpression = new IntLispAtom( t.getText() ); }
			| 	t=BOOL		{ sExpression = new BoolLispAtom( t.getText() ); }			
			| 	t=NULL		{ sExpression = new NullLispAtom( null ); }						
	        |   t=NAME		{ sExpression = new SymbolLispAtom( "\"" +t.getText() + "\""); }				

		)		
	;		
	
literal returns [String text]
	@init {
		text = null;
	}
	:	(   t=STRING { text = getString( t ); } 
		  | t=NAME     { text = t.getText(); }
		  | t=INT    { text = t.getText(); }
		  | t=FLOAT	 { text = t.getText(); }
		  | t=BOOL 	 { text = t.getText(); }
		  | t=NULL   { text = null; }
		)
	;
	
WS      :       (	' '
                |	'\t'
                |	'\f'
                |	EOL
                )
                { $channel=HIDDEN; }
        ;                      
        
DEFRULE		:	'defrule';
//DEFFUNCTION :	'deffunction';
OR 			:	'or';
AND 		:	'and';
NOT 		:	'not';
EXISTS 		:	'exists';
TEST 		:	'test';

NULL		:	'null';

DECLARE 	:	'declare';        		

SALIENCE	:	'salience';

//MODIFY  :	'modify';

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
	
VAR 	: '?' SYMBOL_CHAR+
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
	
ASSIGN_OP 
	:	'<-'	
	;

COLON	:	':';

EQUALS	:	'=';	
        
MULTI_LINE_COMMENT
	:	'/*' (options{greedy=false;} : .)* '*/'
                { $channel=HIDDEN; }
	;

NAME :	SYMBOL ;

fragment
SYMBOL : FIRST_SYMBOL_CHAR SYMBOL_CHAR* ;	

// allowed <
// not allowed ?
fragment
FIRST_SYMBOL_CHAR : ('a'..'z'|'A'..'Z'|'0'..'9'|'!'|'$'|'%'|'^'|'*'|'_'|'-'|'+'|'='|'\\'|'/'|'@'|'#'|':'|'>'|'<'|','|'.'|'['|']'|'{'|'}');	

// allowed ? 
// not allowed <
fragment
SYMBOL_CHAR : ('a'..'z'|'A'..'Z'|'0'..'9'|'!'|'$'|'%'|'^'|'*'|'_'|'-'|'+'|'='|'\\'|'/'|'@'|'#'|':'|'>'|','|'.'|'['|']'|'{'|'}'|'?');		



