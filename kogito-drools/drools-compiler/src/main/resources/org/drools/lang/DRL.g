grammar DRL; 
options {backtrack=true;}

@parser::header {
	package org.drools.lang;
	import java.util.List;
	import java.util.ArrayList;
	import java.util.Iterator;
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
			this.packageDescr = new PackageDescr( packageName ); 
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
        @init {
        	ImportDescr importDecl = null;
        }
	:	imp='import' 
	        {
	            importDecl = factory.createImport( );
	            importDecl.setStartCharacter( ((CommonToken)imp).getStartIndex() );
		    if (packageDescr != null) {
			packageDescr.addImport( importDecl );
		    }
	        }
	        import_name[importDecl] opt_semicolon
	;

function_import_statement
        @init {
        	FunctionImportDescr importDecl = null;
        }
	:	imp='import' 'function' 
	        {
	            importDecl = factory.createFunctionImport();
	            importDecl.setStartCharacter( ((CommonToken)imp).getStartIndex() );
		    if (packageDescr != null) {
			packageDescr.addFunctionImport( importDecl );
		    }
	        }
	        import_name[importDecl] opt_semicolon
	;


import_name[ImportDescr importDecl] returns [String name]
	@init {
		name = null;
	}
	:	
		id=ID 
		{ 
		    name=id.getText(); 
		    importDecl.setTarget( name );
		    importDecl.setEndCharacter( ((CommonToken)id).getStopIndex() );
		} 
		( '.' id=ID 
		    { 
		        name = name + "." + id.getText(); 
			importDecl.setTarget( name );
		        importDecl.setEndCharacter( ((CommonToken)id).getStopIndex() );
		    } 
		)* 
		( star='.*' 
		    { 
		        name = name + star.getText(); 
			importDecl.setTarget( name );
		        importDecl.setEndCharacter( ((CommonToken)star).getStopIndex() );
		    }
		)?
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
			rule.setStartCharacter( ((CommonToken)loc).getStartIndex() );
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
			{ if(d != null) descrs.addDescr( d ); }
		)*
	;

	
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

fact_binding returns [BaseDescr d]
	@init {
		d=null;
		boolean multi=false;
	}
 	:
 		id=ID ':' 
 		{
 		        // handling incomplete parsing
 		        d = new ColumnDescr( id.getText() );
 		}
 		fe=fact_expression[id.getText()]
 		{
 		        // override previously instantiated column
 			d=fe;
 			if( d != null ) {
   			    d.setStartCharacter( ((CommonToken)id).getStartIndex() );
   			}
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
 		loc=LEFT_PAREN {
 				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
 			        d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
 			} 
 		( constraints[(ColumnDescr) d]  )? 
 		endLoc=RIGHT_PAREN
		{
		        if( endLoc.getType() == RIGHT_PAREN ) {
				d.setEndLocation( offset(endLoc.getLine()), endLoc.getCharPositionInLine() );	
				d.setEndCharacter( ((CommonToken)endLoc).getStopIndex() );
			}
 		}
 	;
	
	
constraints[ColumnDescr column]
	:	(constraint[column]|predicate[column])
		( ',' (constraint[column]|predicate[column]))*
	;
	
constraint[ColumnDescr column]
	@init {
		FieldBindingDescr fbd = null;
		FieldConstraintDescr fc = null;
	}
	:
		( fb=ID ':' 
		    { 
			fbd = new FieldBindingDescr();
			fbd.setIdentifier( fb.getText() );
			fbd.setLocation( offset(fb.getLine()), fb.getCharPositionInLine() );
			fbd.setStartCharacter( ((CommonToken)fb).getStartIndex() );
			column.addDescr( fbd );

		    }
		)? 
		f=ID	
		{
			if ( fb != null ) {
			    fbd.setFieldName( f.getText() );
 			    fbd.setEndCharacter( ((CommonToken)f).getStopIndex() );
			} 
			fc = new FieldConstraintDescr(f.getText());
			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
			fc.setStartCharacter( ((CommonToken)f).getStartIndex() );
			
			// it must be a field constraint, as it is not a binding
			if( fb == null ) {
			    column.addDescr( fc );
			}
		}
		(
			(	rd=constraint_expression
				{
					fc.addRestriction(rd);
					// we must add now as we didn't before
					if( fb != null) {
					    column.addDescr( fc );
					}
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
			)
		|
			'->' predicate[column] 
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
	

predicate[ColumnDescr column]
	:
		text=paren_chunk
		{
		        String body = text.substring(1, text.length()-1);
			PredicateDescr d = new PredicateDescr( body );
			column.addDescr( d );
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
                loc=END
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
 		    rule.setEndCharacter( ((CommonToken)loc).getStopIndex() );
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
