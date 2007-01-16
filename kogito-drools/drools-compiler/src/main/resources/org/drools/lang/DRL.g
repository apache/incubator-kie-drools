grammar DRL; 
options {backtrack=true;}

@parser::header {
	package org.drools.lang;
	import java.util.List;
	import java.util.ArrayList;
	import java.util.Iterator;
	import java.util.Map;	
	import java.util.HashMap;	
	import java.util.StringTokenizer;
	import org.drools.lang.descr.*;
	import org.drools.compiler.SwitchingCommonTokenStream;
}

@parser::members {
	private ExpanderResolver expanderResolver;
	private Expander expander;
	private boolean expanderDebug = false;
	private PackageDescr packageDescr;
	private List errors = new ArrayList();
	private String source = "unknown";
	private int lineOffset = 0;
	private DescrFactory factory = new DescrFactory();
	private boolean parserDebug = false;
	
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

	/**
	 * This may be set to enable debuggin of DSLs/expanders.
	 * If set to true, expander stuff will be sent to the Std out.
	 */	
	public void setExpanderDebug(boolean status) {
		expanderDebug = status;
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
	
	public void setExpanderResolver(ExpanderResolver expanderResolver) {
		this.expanderResolver = expanderResolver;
	}
	
	public ExpanderResolver getExpanderResolver() {
		return expanderResolver;
	}
	
	/** Expand the LHS */
	private String runWhenExpander(String text, int line) throws RecognitionException {
		String expanded = text.trim();
		if (expanded.startsWith(">")) {
			expanded = expanded.substring(1);  //escape !!
		} else {
			try {
				expanded = expander.expand( "when", text );			
			} catch (Exception e) {
				this.errors.add(new ExpanderException("Unable to expand: " + text + ". Due to " + e.getMessage(), line));
				return "";
			}
		}
		if (expanderDebug) {
			System.out.println("Expanding LHS: " + text + " ----> " + expanded + " --> from line: " + line);
		}
		return expanded;	
		
	}
	
    	/** This will apply a list of constraints to an LHS block */
    	private String applyConstraints(List constraints, String block) {
    		//apply the constraints as a comma seperated list inside the previous block
    		//the block will end in something like "foo()" and the constraint patterns will be put in the ()
    		if (constraints == null) {
    			return block;
    		}
    		StringBuffer list = new StringBuffer();    		
    		for (Iterator iter = constraints.iterator(); iter.hasNext();) {
				String con = (String) iter.next();
				list.append("\n\t\t");
				list.append(con);
				if (iter.hasNext()) {
					list.append(",");					
				}			
			}
    		if (block.endsWith("()")) {
    			return block.substring(0, block.length() - 2) + "(" + list.toString() + ")";
    		} else {
    			return block + "(" + list.toString() + ")";
    		}
    	}  	

        /** Reparse the results of the expansion */
    	private void reparseLhs(String text, AndDescr descrs) throws RecognitionException {
    		CharStream charStream = new ANTLRStringStream( text );
    		DRLLexer lexer = new DRLLexer( charStream );
    		TokenStream tokenStream = new CommonTokenStream( lexer );
    		DRLParser parser = new DRLParser( tokenStream );
    		parser.setLineOffset( descrs.getLine() );
    		parser.normal_lhs_block(descrs);
            
                if (parser.hasErrors()) {
    			this.errors.addAll(parser.getErrors());
    		}
		if (expanderDebug) {
			System.out.println("Reparsing LHS: " + text + " --> successful:" + !parser.hasErrors());
		}    		
    		
    	}
	
	/** Expand a line on the RHS */
	private String runThenExpander(String text, int startLine) {
		//System.err.println( "expand THEN [" + text + "]" );
		StringTokenizer lines = new StringTokenizer( text, "\n\r" );

		StringBuffer expanded = new StringBuffer();
		
		String eol = System.getProperty( "line.separator" );
				
		while ( lines.hasMoreTokens() ) {
			startLine++;
			String line = lines.nextToken();
			line = line.trim();
			if ( line.length() > 0 ) {
				if ( line.startsWith( ">" ) ) {
					expanded.append( line.substring( 1 ) );
					expanded.append( eol );
				} else {
					try {
						expanded.append( expander.expand( "then", line ) );
						expanded.append( eol );
					} catch (Exception e) {
						this.errors.add(new ExpanderException("Unable to expand: " + line + ". Due to " + e.getMessage(), startLine));			
					}
				}
			}
		}
		
		if (expanderDebug) {
			System.out.println("Expanding RHS: " + text + " ----> " + expanded.toString() + " --> from line starting: " + startLine);
		}		
		
		return expanded.toString();
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
	package org.drools.lang;
}

opt_semicolon
	: ';'?
	;

compilation_unit
	:	prolog 
		( statement )+
	;
	
prolog
	@init {
		String packageName = "";
	}
	:	( name=package_statement { packageName = name; } )?
		{ 
			this.packageDescr = new PackageDescr( name ); 
		}
	;
	
statement
	:
	(	import_statement 
	|       function_import_statement 
	|	global 
	|	function 
	|       t=template {this.packageDescr.addFactTemplate( t ); }
	|	r=rule 	{this.packageDescr.addRule( r ); }
	|	q=query	{this.packageDescr.addRule( q ); }
	) 
	;

package_statement returns [String packageName]
	@init{
		packageName = null;
	}
	:	
		'package' name=dotted_name opt_semicolon
		{
			packageName = name;
		}
	;
	

import_statement
	:	'import' name=import_name opt_semicolon
		{
			if (packageDescr != null) 
				packageDescr.addImport( name );
		}	
	;

function_import_statement
	:	'import' 'function' name=import_name opt_semicolon
		{
			if (packageDescr != null) 
				packageDescr.addFunctionImport( name );
		}	
	;


import_name returns [String name]
	@init {
		name = null;
	}
	:	
		id=ID { name=id.getText(); } ( '.' id=ID { name = name + "." + id.getText(); } )* (star='.*' { name = name + star.getText(); })?
	;


global
	@init {
	}
	:
		'global' type=dotted_name id=ID opt_semicolon
		{
			packageDescr.addGlobal( id.getText(), type );
		}
	;
	

function
	@init {
		FunctionDescr f = null;
	}
	:
		loc='function' (retType=dotted_name)? name=ID
		{
			//System.err.println( "function :: " + name.getText() );
			f = new FunctionDescr( name.getText(), retType );
			f.setLocation(offset(loc.getLine()), loc.getCharPositionInLine());
		} 
		'('
			(	(paramType=dotted_name)? paramName=argument
				{
					f.addParameter( paramType, paramName );
				}
				(	',' (paramType=dotted_name)? paramName=argument
					{
						f.addParameter( paramType, paramName );
					}
				)*
			)?
		')'
		body=curly_chunk
		{
			//strip out '{','}'
			f.setText( body.substring( 1, body.length()-1 ) );

			packageDescr.addFunction( f );
		}
	;



query returns [QueryDescr query]
	@init {
		query = null;
		AndDescr lhs = null;
	}
	:
		loc='query' queryName=word
		{ 
			query = new QueryDescr( queryName, null ); 
			query.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			lhs = new AndDescr(); query.setLhs( lhs ); 
			lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
		}
		(
//			{ expander != null }? expander_lhs_block[lhs] |
			normal_lhs_block[lhs]
		)
					
		'end'
	;


template returns [FactTemplateDescr template]
	@init {
		template = null;		
	}
	:
		loc='template' templateName=ID opt_semicolon
		{
			template = new FactTemplateDescr(templateName.getText());
			template.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );			
		}
		(
			slot=template_slot 
			{
				template.addFieldTemplate(slot);
			}
		)+
		'end' opt_semicolon		
	;
	
template_slot returns [FieldTemplateDescr field]
	@init {
		field = null;
	}
	:
		//name=ID ':' fieldType=dotted_name ( EOL | ';' )
		 fieldType=dotted_name name=ID opt_semicolon
		{
			
			
			field = new FieldTemplateDescr(name.getText(), fieldType);
			field.setLocation( offset(name.getLine()), name.getCharPositionInLine() );
		}
	;	
	
rule returns [RuleDescr rule]
	@init {
		rule = null;
		String consequence = "";
		AndDescr lhs = null;
	}
	:
		loc=RULE ruleName=word 
		{ 
			debug( "start rule: " + ruleName );
			rule = new RuleDescr( ruleName, null ); 
			rule.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
		}
		rule_attributes[rule]
		(	loc=WHEN ':'?
			{ 
				lhs = new AndDescr(); rule.setLhs( lhs ); 
				lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			}
			(
//				{ expander != null }? expander_lhs_block[lhs] |
				normal_lhs_block[lhs]
			)
					
		)?
		rhs_chunk[rule]
	;
	


rule_attributes[RuleDescr rule]
	: 
			('attributes' ':')?
			(	','? a=rule_attribute
				{
					rule.addAttribute( a );
				}
			)*
	;


	
rule_attribute returns [AttributeDescr d]
	@init {
		d = null;
	}
	:
			a=salience { d = a; }
		|	a=no_loop  { d = a; }
		|	a=agenda_group  { d = a; }		
		|	a=duration  { d = a; }			
		|	a=activation_group { d = a; }	
		|	a=auto_focus { d = a; }	
		|                         a=date_effective {d = a; }
		|	a=date_expires {d = a; }
		|                         a=enabled {d=a;}
		
	;
	
date_effective returns [AttributeDescr d]
	@init {
		d = null;
	}	
	:
		loc='date-effective' val=STRING
		{
			d = new AttributeDescr( "date-effective", getString( val ) );
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
		}

	;

date_expires returns [AttributeDescr d]
	@init {
		d = null;
	}	
	:
		loc='date-expires' val=STRING
		{
			d = new AttributeDescr( "date-expires", getString( val ) );
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
		}

	;

	
enabled returns [AttributeDescr d]
	@init {
		d = null;
	}
	:
			loc='enabled' t=BOOL opt_semicolon
			{
				d = new AttributeDescr( "enabled", t.getText() );
				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			}
		
		
	;	
	
	

salience returns [AttributeDescr d ]
	@init {
		d = null;
	}
	:	
		loc='salience' i=INT opt_semicolon
		{
			d = new AttributeDescr( "salience", i.getText() );
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
		}
	;
	
no_loop returns [AttributeDescr d]
	@init {
		d = null;
	}
	:
		(
			loc='no-loop' opt_semicolon
			{
				d = new AttributeDescr( "no-loop", "true" );
				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			}
		) 
		|
		(
			loc='no-loop' t=BOOL opt_semicolon
			{
				d = new AttributeDescr( "no-loop", t.getText() );
				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			}
		
		)
		
	;
	
auto_focus returns [AttributeDescr d]
	@init {
		d = null;
	}
	:
		(
			loc='auto-focus' opt_semicolon
			{
				d = new AttributeDescr( "auto-focus", "true" );
				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			}
		) 
		|
		(
			loc='auto-focus' t=BOOL opt_semicolon
			{
				d = new AttributeDescr( "auto-focus", t.getText() );
				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			}
		
		)
		
	;	
	
activation_group returns [AttributeDescr d]
	@init {
		d = null;
	}
	:
		loc='activation-group' name=STRING opt_semicolon
		{
			d = new AttributeDescr( "activation-group", getString( name ) );
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
		}
	;

agenda_group returns [AttributeDescr d]
	@init {
		d = null;
	}
	:
		loc='agenda-group' name=STRING opt_semicolon
		{
			d = new AttributeDescr( "agenda-group", getString( name ) );
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
		}
	;		


duration returns [AttributeDescr d]
	@init {
		d = null;
	}
	:
		loc='duration' i=INT
		{
			d = new AttributeDescr( "duration", i.getText() );
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
		}
	;		
	

normal_lhs_block[AndDescr descrs]
	:
		(	d=lhs
			{ descrs.addDescr( d ); }
		)*
	;

/*
expander_lhs_block[AndDescr descrs]
	@init {
		String lhsBlock = null;
		String eol = System.getProperty( "line.separator" );
		List constraints = null;
	}
	:
		(options{greedy=false;} : 
			text=paren_chunk (options{greedy=true;} : loc=EOL)
			{
				//only expand non null
				if (text != null) {
					if (text.trim().startsWith("-")) {
						if (constraints == null) {
							constraints = new ArrayList();
						}
						constraints.add(runWhenExpander( text, offset(loc.getLine())));
					} else {
						if (constraints != null) {
							lhsBlock = applyConstraints(constraints, lhsBlock);
							constraints = null;
						}
					
					
						if (lhsBlock == null) {					
							lhsBlock = runWhenExpander( text, offset(loc.getLine()));
						} else {
							lhsBlock = lhsBlock + eol + runWhenExpander( text, offset(loc.getLine()));
						}
					}
					text = null;
				}
			}
			
			
		 )* 
		
		{	
			//flush out any constraints left handing before the RHS
			lhsBlock = applyConstraints(constraints, lhsBlock);
			if (lhsBlock != null) {
				reparseLhs(lhsBlock, descrs);
			}
		}

	;
*/	
	
lhs returns [BaseDescr d]
	@init {
		d=null;
	}
	:	l=lhs_or { d = l; } 
	;

	
lhs_column returns [BaseDescr d]
	@init {
		d=null;
	}
	:	f=fact_binding	{ d = f; }
	|	f=fact		{ d = f; }
	;

from_statement returns [FromDescr d]
	@init {
		d=factory.createFrom();
	}
	:
	'from' ds=from_source
		{
			d.setDataSource(ds);
		
		}
		
		
		
	;
	
from_source returns [DeclarativeInvokerDescr ds]
	@init {
		ds = null;
		AccessorDescr ad = null;
	}
	:	
		(( functionName=ID args=paren_chunk			
		        {
 				ad = new AccessorDescr();	
				ad.setLocation( offset(functionName.getLine()), functionName.getCharPositionInLine() );
				ds = ad;
				FunctionCallDescr fc = new FunctionCallDescr(functionName.getText());
				fc.setLocation( offset(functionName.getLine()), functionName.getCharPositionInLine() );			
				fc.setArguments(args);
				ad.addInvoker(fc);
			}
		)
		|
		(   var=ID 
		    {
			ad = new AccessorDescr(var.getText());	
			ad.setLocation( offset(var.getLine()), var.getCharPositionInLine() );
			ds = ad;
		    }
		))  
		
		expression_chain[ad]?
	;	
	
expression_chain[AccessorDescr as]
	@init {
  		FieldAccessDescr fa = null;
	    	MethodAccessDescr ma = null;	
	}
	:
	( '.' field=ID  
	    {
	        fa = new FieldAccessDescr(field.getText());	
		fa.setLocation( offset(field.getLine()), field.getCharPositionInLine() );
	    }
	  (
	    ( LEFT_SQUARE ) => sqarg=square_chunk
	      {
	          fa.setArgument( sqarg );	
	      }
	    |
	    ( LEFT_PAREN ) => paarg=paren_chunk
		{
	    	  ma = new MethodAccessDescr( field.getText(), paarg );	
		  ma.setLocation( offset(field.getLine()), field.getCharPositionInLine() );
		}
	  )?
	  {
	      if( ma != null ) {
	          as.addInvoker( ma );
	      } else {
	          as.addInvoker( fa );
	      }
	  }
	  expression_chain[as]?
	)  
	;	
	
accumulate_statement returns [AccumulateDescr d]
	@init {
		d = factory.createAccumulate();
	}
	:
	        loc='from' 'accumulate' 
		{ 
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
		}	
		'(' column=lhs_column ',' 
		{
		        d.setSourceColumn( (ColumnDescr)column );
		}
		'init' text=paren_chunk ',' 
		{
		        d.setInitCode( text.substring(1, text.length()-1) );
		}
		'action' text=paren_chunk ',' 
		{
		        d.setActionCode( text.substring(1, text.length()-1) );
		}
		'result' text=paren_chunk ')'
		{
		        d.setResultCode( text.substring(1, text.length()-1) );
		} 
	; 		
 		
collect_statement returns [CollectDescr d]
	@init {
		d = factory.createCollect();
	}
	:
	        loc='from' 'collect' 
		{ 
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
		}	
		'(' column=lhs_column ')'
		{
		        d.setSourceColumn( (ColumnDescr)column );
		}
	; 		
/*
argument_list returns [ArrayList args]
	@init {
		args = new ArrayList();
	}
	:
		(param=argument_value  {
			if (param != null) {
				args.add(param);
			}
		}
		 
		(
			',' param=argument_value {
				if (param != null) {
					args.add(param);
				}
			}
		)*
		)?
	;		
	
argument_value returns [ArgumentValueDescr value]
	@init {
		value = null;
		String text = null;
	}
	:	(	t=STRING { text = getString( t );  value=new ArgumentValueDescr(ArgumentValueDescr.STRING, text);} 
		|	t=INT    { text = t.getText();  value=new ArgumentValueDescr(ArgumentValueDescr.INTEGRAL, text);}
		|	t=FLOAT	 { text = t.getText(); value=new ArgumentValueDescr(ArgumentValueDescr.DECIMAL, text); }
		|	t=BOOL 	 { text = t.getText(); value=new ArgumentValueDescr(ArgumentValueDescr.BOOLEAN, text); }
		|	t=ID { text = t.getText(); value=new ArgumentValueDescr(ArgumentValueDescr.VARIABLE, text);}	
		|	t='null' { text = "null"; value=new ArgumentValueDescr(ArgumentValueDescr.NULL, text);}	
		|       m=inline_map {  value=new ArgumentValueDescr(ArgumentValueDescr.MAP, m.getKeyValuePairs() ); }
		|       a=inline_array { value = new ArgumentValueDescr(ArgumentValueDescr.LIST, a ); }		
		)
	;			

inline_map returns [ArgumentValueDescr.MapDescr mapDescr]
    @init {
        mapDescr = new ArgumentValueDescr.MapDescr();
    }	
    :  '{' 
           ( key=argument_value '=>' value=argument_value {
                 if ( key != null ) {
                     mapDescr.add( new ArgumentValueDescr.KeyValuePairDescr( key, value ) );
                 }
             }
           )
           
           ( (EOL)? ',' (EOL)? key=argument_value '=>' value=argument_value {
                 if ( key != null ) {
                     mapDescr.add( new ArgumentValueDescr.KeyValuePairDescr( key, value ) );
                 }
             }
           )*           
       '}'
    ;
    
inline_array returns [List list]
    @init {
    	list = new ArrayList();
    }		    
    :
    '[' arg=argument_value { list.add(arg); }
    
     	 ( EOL? ',' EOL? arg=argument_value { list.add(arg); } )*
      ']'
      
    
    ; 	
*/
fact_binding returns [BaseDescr d]
	@init {
		d=null;
		boolean multi=false;
	}
 	:
 		id=ID 
 		
 		':' fe=fact_expression[id.getText()]
 		{
 			d=fe;
 		}
	;
 
 fact_expression[String id] returns [BaseDescr pd]
 	@init {
 		pd = null;
 		boolean multi = false;
 	}
 	:	'(' fe=fact_expression_in_paren[id] ')' { pd=fe; }
 	| 	f=fact
 		{
 			((ColumnDescr)f).setIdentifier( id );
 			pd = f;
 		}
	;

// in parenthesis alternative is allowed
 fact_expression_in_paren[String id] returns [BaseDescr pd]
 	@init {
 		pd = null;
 		boolean multi = false;
 	}
 	:	'(' fe=fact_expression_in_paren[id]')' { pd=fe; }
 	| 	f=fact
 		{
 			((ColumnDescr)f).setIdentifier( id );
 			pd = f;
 		}
 		( ('or'|'||')
 			{	if ( ! multi ) {
 					BaseDescr first = pd;
 					pd = new OrDescr();
 					((OrDescr)pd).addDescr( first );
 					multi=true;
 				}
 			}
 			f=fact
 			{
 				((ColumnDescr)f).setIdentifier( id );
 				((OrDescr)pd).addDescr( f );
 			}
 		)*	
	;
 
fact returns [BaseDescr d] 
	@init {
		d=null;
	}
 	:	id=dotted_name 
 		{ 
 			d = new ColumnDescr( id ); 
 		}
 		loc='(' {
 				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
 			} 
 		(	c=constraints
 			{
		 		for ( Iterator cIter = c.iterator() ; cIter.hasNext() ; ) {
 					((ColumnDescr)d).addDescr( (BaseDescr) cIter.next() );
 				}
 			}
  		)? 
 		endLoc=')'
		{
			d.setEndLocation( offset(endLoc.getLine()), endLoc.getCharPositionInLine() );	
 		}
 	;
	
	
constraints returns [List constraints]
	@init {
		constraints = new ArrayList();
	}
	:	(constraint[constraints]|predicate[constraints])
		( ',' (constraint[constraints]|predicate[constraints]))*
	;
	
constraint[List constraints]
	@init {
		BaseDescr d = null;
		FieldConstraintDescr fc = null;
	}
	:
		( fb=ID ':' )? 
		f=ID	
		{
			if ( fb != null ) {
				d = new FieldBindingDescr( f.getText(), fb.getText() );
				d.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
				constraints.add( d );
			} 
			fc = new FieldConstraintDescr(f.getText());
			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
		}
		(
			rd=constraint_expression
			{
				fc.addRestriction(rd);
				constraints.add(fc);
			}
			(
				con=('&'|'|')
				{
					if (con.getText().equals("&") ) {								
						fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND));	
					} else {
						fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR));	
					}							
				}
				rd=constraint_expression
				{
					fc.addRestriction(rd);
				}
			)*
		)?					
	;
	
constraint_expression returns [RestrictionDescr rd]
        :	
		op=(	'=='
		|	'>'
		|	'>='
		|	'<'
		|	'<='
		|	'!='
		|	'contains'
		|	'matches'
		|       'excludes'
		)	
		(	bvc=ID
			{
				rd = new VariableRestrictionDescr(op.getText(), bvc.getText());
			}
		|
			lc=enum_constraint 
			{ 
				rd  = new LiteralRestrictionDescr(op.getText(), lc, true);
			}						
		|
			lc=literal_constraint 
			{ 
				rd  = new LiteralRestrictionDescr(op.getText(), lc);
			}
		|	rvc=retval_constraint 
			{ 
				rd = new ReturnValueRestrictionDescr(op.getText(), rvc);							
			} 
		)
	;	
		
literal_constraint returns [String text]
	@init {
		text = null;
	}
	:	(	t=STRING { text = getString( t ); } //t.getText(); text=text.substring( 1, text.length() - 1 ); }
		|	t=INT    { text = t.getText(); }
		|	t=FLOAT	 { text = t.getText(); }
		|	t=BOOL 	 { text = t.getText(); }
		|	t='null' { text = null; }
		)
	;
	
enum_constraint returns [String text]
	@init {
		text = null;
	}
	:	(cls=ID '.' en=ID) { text = cls.getText() + "." + en.getText(); }
	;	
	

predicate[List constraints]
	:
		decl=ID ':' field=ID '->' text=paren_chunk
		{
		        String body = text.substring(1, text.length()-1);
			PredicateDescr d = new PredicateDescr(field.getText(), decl.getText(), body );
			constraints.add( d );
		}
	;

paren_chunk returns [String text]
        @init {
           StringBuffer buf = null;
           Integer channel = null;
        }
	:
	        {
	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
		    buf = new StringBuffer();
	        }
		loc=LEFT_PAREN 
		{
		    buf.append( loc.getText());
 
		} 
		( 
			~(LEFT_PAREN|RIGHT_PAREN)
			  {
			    buf.append( input.LT(-1).getText() );
			  }
			|
			chunk=paren_chunk
			  {
			    buf.append( chunk );
			  }
		)*
		{
		    if( channel != null ) {
			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, channel.intValue());
		    } else {
			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, Token.HIDDEN_CHANNEL);
		    }
		}
                loc=RIGHT_PAREN
                {
                    buf.append( loc.getText() );
		    text = buf.toString();
                }
	;

curly_chunk returns [String text]
        @init {
           StringBuffer buf = null;
           Integer channel = null;
        }
	:
		loc=LEFT_CURLY 
		{
	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
		    buf = new StringBuffer();
		    
		    buf.append( loc.getText() );
		} 
		( 
			~(LEFT_CURLY|RIGHT_CURLY)
			  {
			    buf.append( input.LT(-1).getText() );
			  }
			|
			chunk=curly_chunk
			  {
			    buf.append( chunk );
			  }
		)*
		{
		    if( channel != null ) {
			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, channel.intValue());
		    } else {
			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, Token.HIDDEN_CHANNEL);
		    }
		}
                loc=RIGHT_CURLY
                {
                    buf.append( loc.getText() );
		    text = buf.toString();
                }
	;

square_chunk returns [String text]
        @init {
           StringBuffer buf = null;
           Integer channel = null;
        }
	:
	        {
	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
		    buf = new StringBuffer();
	        }
		loc=LEFT_SQUARE 
		{
		    buf.append( loc.getText());
 
		} 
		( 
			~(LEFT_SQUARE|RIGHT_SQUARE)
			  {
			    buf.append( input.LT(-1).getText() );
			  }
			|
			chunk=square_chunk
			  {
			    buf.append( chunk );
			  }
		)*
		{
		    if( channel != null ) {
			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, channel.intValue());
		    } else {
			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, Token.HIDDEN_CHANNEL);
		    }
		}
                loc=RIGHT_SQUARE
                {
                    buf.append( loc.getText() );
		    text = buf.toString();
                }
	;
	
retval_constraint returns [String text]
	@init {
		text = null;
	}
	:	
		c=paren_chunk { text = c.substring(1, c.length()-1); }
	;




	

lhs_or returns [BaseDescr d]
	@init{
		d = null;
		OrDescr or = null;
	}
	:	
		left=lhs_and {d = left; }
		( ('or'|'||')
			right=lhs_and 
			{
				if ( or == null ) {
					or = new OrDescr();
					or.addDescr( left );
					d = or;
				}
				
				or.addDescr( right );
			}
		)*
	;
	
lhs_and returns [BaseDescr d]
	@init{
		d = null;
		AndDescr and = null;
	}
	:
		left=lhs_unary { d = left; }
		( ('and'|'&&')
			right=lhs_unary
			{
				if ( and == null ) {
					and = new AndDescr();
					and.addDescr( left );
					d = and;
				}
				
				and.addDescr( right );
			}
		)* 
	;
	
lhs_unary returns [BaseDescr d]
	@init {
		d = null;
	}
	:	(	u=lhs_exist
		|	u=lhs_not
		|	u=lhs_eval
		|	u=lhs_column (
		           (fm=from_statement {fm.setColumn((ColumnDescr) u); u=fm;}) 
		          |(ac=accumulate_statement {ac.setResultColumn((ColumnDescr) u); u=ac;})
		          |(cs=collect_statement {cs.setResultColumn((ColumnDescr) u); u=cs;}) 
		        )?
		|	u=lhs_forall  
		|	'(' u=lhs ')'
		) { d = u; }
		opt_semicolon
	;
	
lhs_exist returns [BaseDescr d]
	@init {
		d = null;
	}
	:	loc='exists' ('(' column=lhs_or ')' | column=lhs_column)
		{ 
			d = new ExistsDescr( (ColumnDescr) column ); 
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
		}	
	;
	
lhs_not	returns [NotDescr d]
	@init {
		d = null;
	}
	:	loc='not' ('(' column=lhs_or  ')' | column=lhs_column)
		{
			d = new NotDescr( column ); 
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
		}
	;

lhs_eval returns [BaseDescr d]
	@init {
		d = null;
	}
	:	loc='eval' c=paren_chunk
		{ 
		        String body = c.substring(1, c.length()-1);
			checkTrailingSemicolon( body, offset(loc.getLine()) );
			d = new EvalDescr( body ); 
		}
	;
	
lhs_forall returns [ForallDescr d]
	@init {
		d = factory.createForall();
	}
	:	loc='forall' '(' base=lhs_column   
		{
		        // adding the base column
		        d.addDescr( base );
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
		}
		( (',')? column=lhs_column
		{
		        // adding additional columns
			d.addDescr( column );
		}
		)+
		')'
	;

dotted_name returns [String name]
	@init {
		name = null;
	}
	:	
		id=ID { name=id.getText(); } ( '.' id=ID { name = name + "." + id.getText(); } )* ( '[' ']' { name = name + "[]";})*
	;
	
argument returns [String name]
	@init {
		name = null;
	}
	:
		id=ID { name=id.getText(); } ( '[' ']' { name = name + "[]";})*
	;

rhs_chunk[RuleDescr rule]
        @init {
           StringBuffer buf = null;
           Integer channel = null;
        }
	:
	        {
	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
		    buf = new StringBuffer();
	        }
		start='then'
		( 
			  ~END
			  {
			    buf.append( input.LT(-1).getText() );
			  }
		)*
		{
		    if( channel != null ) {
			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, channel.intValue());
		    } else {
			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, Token.HIDDEN_CHANNEL);
		    }
		}
                END
                {
                    // ignoring first line in the consequence
                    int index = 0;
                    while( (index < buf.length() ) && Character.isWhitespace( buf.charAt( index ) ) &&
                           (buf.charAt( index ) != 10 ) && (buf.charAt( index ) != 13 ))
                               index++;
                    if( (index < buf.length() ) && ( buf.charAt( index ) == '\r' ) )
                        index++;
                    if( (index < buf.length() ) && ( buf.charAt( index ) == '\n' ) )
                        index++;
                    
		    rule.setConsequence( buf.substring( index ) );
     		    rule.setConsequenceLocation(offset(start.getLine()), start.getCharPositionInLine());
                }
	;

	
word returns [String word]
	@init{
		word = null;
	}
	:	id=ID      { word=id.getText(); }
	|	'import'   { word="import"; }
	|	'use'      { word="use"; }
	|	RULE       { word="rule"; }
	|	'query'    { word="query"; }
	|	'salience' { word="salience"; }
 	|	'no-loop'  { word="no-loop"; }
	|	WHEN       { word="when"; }
	|	THEN       { word="then"; }
	|	END        { word="end"; }
	|	str=STRING { word=getString(str);} //str.getText(); word=word.substring( 1, word.length()-1 ); }
	;

//RHS	:'then' (options{greedy=false;} : .)* ('\n'|'\r') (' '|'\t'|'\f')* 'end'
//	;

WS      :       (	' '
                |	'\t'
                |	'\f'
                |	EOL
                )
                { $channel=HIDDEN; }
        ;

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
	
RULE    :	'rule';

WHEN    :	'when';

THEN	:    	'then';

END     :	'end';

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
        
MULTI_LINE_COMMENT
	:	'/*' (options{greedy=false;} : .)* '*/'
                { $channel=HIDDEN; }
	;

MISC 	:
		'!' | '@' | '$' | '%' | '^' | '&' | '*' | '_' | '-' | '+'  | '?' | '|' | ',' | '=' | '/' | '\'' | '\\'
	;
