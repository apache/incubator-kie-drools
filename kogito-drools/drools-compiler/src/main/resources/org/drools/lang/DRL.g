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
	private PackageDescr packageDescr;
	private List errors = new ArrayList();
	private String source = "unknown";
	private int lineOffset = 0;
	private DescrFactory factory = new DescrFactory();
	private boolean parserDebug = false;
	private Location location = new Location( Location.LOCATION_UNKNOWN );
	
	// THE FOLLOWING LINES ARE DUMMY ATTRIBUTES TO WORK AROUND AN ANTLR BUG
	private BaseDescr from = null;
	private FieldConstraintDescr fc = null;
	private RestrictionConnectiveDescr and = null;
	private RestrictionConnectiveDescr or = null;
	private ConditionalElementDescr base = null;
	
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
                                                           mtne.toString() +
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
        
        public Location getLocation() {
                return this.location;
        }
      
}

@lexer::header {
	package org.drools.lang;
}

opt_semicolon
	: ';'?
	;

compilation_unit
	@init {
		// reset Location information
		this.location = new Location( Location.LOCATION_UNKNOWN );
	}
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
			(ATTRIBUTES ':')?
			(	','? a=rule_attribute
				{
					this.packageDescr.addAttribute( a );
				}
			)*		
	;
	
statement
	:
	(	function_import_statement 
	|	import_statement 
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
	

import_statement
        @init {
        	ImportDescr importDecl = null;
        }
	:	imp=IMPORT 
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
	:	imp=IMPORT FUNCTION 
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
		id=identifier 
		{ 
		    name=id.getText(); 
		    importDecl.setTarget( name );
		    importDecl.setEndCharacter( ((CommonToken)id).getStopIndex() );
		} 
		( '.' id=identifier 
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
	    GlobalDescr global = null;
	}
	:
		loc=GLOBAL 
		{
		    global = factory.createGlobal();
	            global.setStartCharacter( ((CommonToken)loc).getStartIndex() );
		    packageDescr.addGlobal( global );
		}
		type=dotted_name[null] 
		{
		    global.setType( type );
		}
		id=identifier opt_semicolon
		{
		    global.setIdentifier( id.getText() );
		    global.setEndCharacter( ((CommonToken)id).getStopIndex() );
		}
	;
	

function
	@init {
		FunctionDescr f = null;
	}
	:
		loc=FUNCTION (retType=dotted_name[null])? n=identifier
		{
			//System.err.println( "function :: " + n.getText() );
			f = factory.createFunction( n.getText(), retType );
			f.setLocation(offset(loc.getLine()), loc.getCharPositionInLine());
	        	f.setStartCharacter( ((CommonToken)loc).getStartIndex() );
			packageDescr.addFunction( f );
		} 
		'('
			(	(paramType=dotted_name[null])? paramName=argument
				{
					f.addParameter( paramType, paramName );
				}
				(	',' (paramType=dotted_name[null])? paramName=argument
					{
						f.addParameter( paramType, paramName );
					}
				)*
			)?
		')'
		body=curly_chunk[f]
		{
			//strip out '{','}'
			f.setText( body.substring( 1, body.length()-1 ) );
		}
	;



query returns [QueryDescr query]
	@init {
		query = null;
		AndDescr lhs = null;
	}
	:
		loc=QUERY queryName=name
		{ 
			query = factory.createQuery( queryName ); 
			query.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			query.setStartCharacter( ((CommonToken)loc).getStartIndex() );
			lhs = new AndDescr(); query.setLhs( lhs ); 
			lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
		}
		(
			normal_lhs_block[lhs]
		)
					
		loc=END
		{
			query.setEndCharacter( ((CommonToken)loc).getStopIndex() );
		}
	;


template returns [FactTemplateDescr template]
	@init {
		template = null;		
	}
	:
		loc=TEMPLATE templateName=identifier opt_semicolon
		{
			template = new FactTemplateDescr(templateName.getText());
			template.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );			
			template.setStartCharacter( ((CommonToken)loc).getStartIndex() );
		}
		(
			slot=template_slot 
			{
				template.addFieldTemplate(slot);
			}
		)+
		loc=END opt_semicolon 
		{
			template.setEndCharacter( ((CommonToken)loc).getStopIndex() );
		}		
	;
	
template_slot returns [FieldTemplateDescr field]
	@init {
		field = null;
	}
	:
	         {
			field = factory.createFieldTemplate();
	         }
		 fieldType=dotted_name[field] 
		 {
		        field.setClassType( fieldType );
		 }
		 
		 n=identifier opt_semicolon
		 {
		        field.setName( n.getText() );
			field.setLocation( offset(n.getLine()), n.getCharPositionInLine() );
			field.setEndCharacter( ((CommonToken)n).getStopIndex() );
		 } 
	;	
	
rule returns [RuleDescr rule]
	@init {
		rule = null;
		String consequence = "";
		AndDescr lhs = null;
	}
	:
		loc=RULE ruleName=name 
		{ 
			location.setType( Location.LOCATION_RULE_HEADER );
			debug( "start rule: " + ruleName );
			rule = new RuleDescr( ruleName, null ); 
			rule.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			rule.setStartCharacter( ((CommonToken)loc).getStartIndex() );
		}
		rule_attributes[rule]
		(	loc=WHEN ':'?
			{ 
				this.location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
				lhs = new AndDescr(); rule.setLhs( lhs ); 
				lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
				lhs.setStartCharacter( ((CommonToken)loc).getStartIndex() );
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
			(ATTRIBUTES ':')?
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
		|	a=date_effective {d = a; }
		|	a=date_expires {d = a; }
		|	a=enabled {d=a;}
		|	a=ruleflow_group { d = a; }
		|	a=lock_on_active{ d = a; }
		|	a=dialect {d = a; }
		
	;
	
date_effective returns [AttributeDescr d]
	@init {
		d = null;
	}	
	:
		loc=DATE_EFFECTIVE val=STRING  
		{
			d = new AttributeDescr( "date-effective", getString( val ) );
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
			d.setEndCharacter( ((CommonToken)val).getStopIndex() );
		}

	;

date_expires returns [AttributeDescr d]
	@init {
		d = null;
	}	
	:
		loc=DATE_EXPIRES val=STRING  
		{
			d = new AttributeDescr( "date-expires", getString( val ) );
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
			d.setEndCharacter( ((CommonToken)val).getStopIndex() );
		}

	;

	
enabled returns [AttributeDescr d]
	@init {
		d = null;
	}
	:
			loc=ENABLED t=BOOL   
			{
				d = new AttributeDescr( "enabled", t.getText() );
				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
				d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
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
	
no_loop returns [AttributeDescr d]
	@init {
		d = null;
	}
	:
		(
			loc=NO_LOOP   
			{
				d = new AttributeDescr( "no-loop", "true" );
				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
				d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
				d.setEndCharacter( ((CommonToken)loc).getStopIndex() );
			}
		) 
		|
		(
			loc=NO_LOOP t=BOOL   
			{
				d = new AttributeDescr( "no-loop", t.getText() );
				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
				d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
				d.setEndCharacter( ((CommonToken)t).getStopIndex() );
			}
		
		)
		
	;
	
auto_focus returns [AttributeDescr d]
	@init {
		d = null;
	}
	:
		(
			loc=AUTO_FOCUS   
			{
				d = new AttributeDescr( "auto-focus", "true" );
				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
				d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
				d.setEndCharacter( ((CommonToken)loc).getStopIndex() );
			}
		) 
		|
		(
			loc=AUTO_FOCUS t=BOOL   
			{
				d = new AttributeDescr( "auto-focus", t.getText() );
				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
				d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
				d.setEndCharacter( ((CommonToken)t).getStopIndex() );
			}
		
		)
		
	;	
	
activation_group returns [AttributeDescr d]
	@init {
		d = null;
	}
	:
		loc=ACTIVATION_GROUP n=STRING   
		{
			d = new AttributeDescr( "activation-group", getString( n ) );
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
			d.setEndCharacter( ((CommonToken)n).getStopIndex() );
		}
	;

ruleflow_group returns [AttributeDescr d]
	@init {
		d = null;
	}
	:
		loc=RULEFLOW_GROUP n=STRING   
		{
			d = new AttributeDescr( "ruleflow-group", getString( n ) );
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
			d.setEndCharacter( ((CommonToken)n).getStopIndex() );
		}
	;

agenda_group returns [AttributeDescr d]
	@init {
		d = null;
	}
	:
		loc=AGENDA_GROUP n=STRING   
		{
			d = new AttributeDescr( "agenda-group", getString( n ) );
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
			d.setEndCharacter( ((CommonToken)n).getStopIndex() );
		}
	;
	

duration returns [AttributeDescr d]
	@init {
		d = null;
	}
	:
		loc=DURATION i=INT 
		{
			d = new AttributeDescr( "duration", i.getText() );
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
			d.setEndCharacter( ((CommonToken)i).getStopIndex() );
		}
	;	
	
dialect returns [AttributeDescr d]
	@init {
		d = null;
	}
	:
		loc=DIALECT n=STRING   
		{
			d = new AttributeDescr( "dialect", getString( n ) );
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
			d.setEndCharacter( ((CommonToken)n).getStopIndex() );
		}
	;			


	
	
	
	
lock_on_active returns [AttributeDescr d]
	@init {
		d = null;
	}
	:
		(
			loc=LOCK_ON_ACTIVE   
			{
				d = new AttributeDescr( "lock-on-active", "true" );
				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
				d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
				d.setEndCharacter( ((CommonToken)loc).getStopIndex() );
			}
		) 
		|
		(
			loc=LOCK_ON_ACTIVE t=BOOL   
			{
				d = new AttributeDescr( "lock-on-active", t.getText() );
				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
				d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
				d.setEndCharacter( ((CommonToken)t).getStopIndex() );
			}
		
		)
		
	;		

normal_lhs_block[AndDescr descr]
	:
		(	d=lhs[descr]
			{ if(d != null) descr.addDescr( d ); }
		)*
	;

	
lhs[ConditionalElementDescr ce] returns [BaseDescr d]
	@init {
		d=null;
	}
	:	l=lhs_or { d = l; } 
	;

	
lhs_pattern returns [BaseDescr d]
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
	ds=from_source[d]
		{
			d.setDataSource(ds);
		
		}
		
		
		
	;
	
from_source[FromDescr from] returns [DeclarativeInvokerDescr ds]
	@init {
		ds = null;
		AccessorDescr ad = null;
	}
	:	
		ident=identifier
		{
			ad = new AccessorDescr(ident.getText());	
			ad.setLocation( offset(ident.getLine()), ident.getCharPositionInLine() );
			ad.setStartCharacter( ((CommonToken)ident).getStartIndex() );
			ad.setEndCharacter( ((CommonToken)ident).getStopIndex() );
			ds = ad;
			location.setProperty(Location.LOCATION_FROM_CONTENT, ident.getText());
		}
		(args=paren_chunk[from]
		{
			if( args != null ) {
				ad.setVariableName( null );
				FunctionCallDescr fc = new FunctionCallDescr(ident.getText());
				fc.setLocation( offset(ident.getLine()), ident.getCharPositionInLine() );			
				fc.setArguments(args);
				fc.setStartCharacter( ((CommonToken)ident).getStartIndex() );
				fc.setEndCharacter( ((CommonToken)ident).getStopIndex() );
				ad.addInvoker(fc);
				location.setProperty(Location.LOCATION_FROM_CONTENT, args);
			}
		}
		)?
		expression_chain[from, ad]?
		{
			if( ad != null ) {
				location.setProperty(Location.LOCATION_FROM_CONTENT, ad.toString() );
			}
		}
	;	
	
expression_chain[FromDescr from, AccessorDescr as]
	@init {
  		FieldAccessDescr fa = null;
	    	MethodAccessDescr ma = null;	
	}
	:
	( '.' field=identifier  
	    {
	        fa = new FieldAccessDescr(field.getText());	
		fa.setLocation( offset(field.getLine()), field.getCharPositionInLine() );
		fa.setStartCharacter( ((CommonToken)field).getStartIndex() );
		fa.setEndCharacter( ((CommonToken)field).getStopIndex() );
	    }
	  (
	    ( LEFT_SQUARE ) => sqarg=square_chunk[from]
	      {
	          fa.setArgument( sqarg );	
	      }
	    |
	    ( LEFT_PAREN ) => paarg=paren_chunk[from]
		{
	    	  ma = new MethodAccessDescr( field.getText(), paarg );	
		  ma.setLocation( offset(field.getLine()), field.getCharPositionInLine() );
		  ma.setStartCharacter( ((CommonToken)field).getStartIndex() );
		}
	  )?
	  {
	      if( ma != null ) {
	          as.addInvoker( ma );
	      } else {
	          as.addInvoker( fa );
	      }
	  }
	  expression_chain[from, as]?
	)  
	;	
	
accumulate_statement returns [AccumulateDescr d]
	@init {
		d = factory.createAccumulate();
	}
	:
	        loc=ACCUMULATE 
		{ 
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
			location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE );
		}	
		LEFT_PAREN pattern=lhs_pattern COMMA? 
		{
		        d.setSourcePattern( (PatternDescr)pattern );
		}
		INIT 
		{
			location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE_INIT );
		}
		text=paren_chunk[null] COMMA?
		{
			if( text != null ) {
			        d.setInitCode( text.substring(1, text.length()-1) );
				location.setProperty(Location.LOCATION_PROPERTY_FROM_ACCUMULATE_INIT_CONTENT, d.getInitCode());
				location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION );
			}
		}
		ACTION text=paren_chunk[null] COMMA?
		{
			if( text != null ) {
			        d.setActionCode( text.substring(1, text.length()-1) );
	       			location.setProperty(Location.LOCATION_PROPERTY_FROM_ACCUMULATE_ACTION_CONTENT, d.getActionCode());
				location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT );
			}
		}
		RESULT text=paren_chunk[null] 
		{
			if( text != null ) {
			        d.setResultCode( text.substring(1, text.length()-1) );
				location.setProperty(Location.LOCATION_PROPERTY_FROM_ACCUMULATE_RESULT_CONTENT, d.getResultCode());
			}
		}
		loc=RIGHT_PAREN
		{
			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
			d.setEndCharacter( ((CommonToken)loc).getStopIndex() );
		} 
	; 		
 		
collect_statement returns [CollectDescr d]
	@init {
		d = factory.createCollect();
	}
	:
	        loc=COLLECT 
		{ 
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
			location.setType( Location.LOCATION_LHS_FROM_COLLECT );
		}	
		LEFT_PAREN pattern=lhs_pattern loc=RIGHT_PAREN
		{
		        d.setSourcePattern( (PatternDescr)pattern );
			d.setEndCharacter( ((CommonToken)loc).getStopIndex() );
			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
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
 		        d = new PatternDescr( );
 		        ((PatternDescr) d).setIdentifier( id.getText() );
 		}
 		fe=fact_expression[id.getText()]
 		{
 		        // override previously instantiated pattern
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
 	:	LEFT_PAREN fe=fact_expression[id] RIGHT_PAREN { pd=fe; }
 	| 	f=fact
 		{
 			((PatternDescr)f).setIdentifier( id );
 			pd = f;
 		}
 		( (OR|DOUBLE_PIPE)
 			{	if ( ! multi ) {
 					BaseDescr first = pd;
 					pd = new OrDescr();
 					((OrDescr)pd).addDescr( first );
 					multi=true;
 				}
 			}
 			f=fact
 			{
 				((PatternDescr)f).setIdentifier( id );
 				((OrDescr)pd).addDescr( f );
 			}
 		)*	
	;

fact returns [BaseDescr d] 
	@init {
		d=null;
		PatternDescr pattern = null;
	}
 	:	
 	        {
 			pattern = new PatternDescr( );
 			d = pattern; 
 	        }
 	        id=dotted_name[d] 
 		{ 
 		        pattern.setObjectType( id );
 		        pattern.setEndCharacter( -1 );
 		}
 		loc=LEFT_PAREN {
		                location.setType( Location.LOCATION_LHS_INSIDE_CONDITION_START );
            			location.setProperty( Location.LOCATION_PROPERTY_CLASS_NAME, id );
 				
 				pattern.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
 			        pattern.setLeftParentCharacter( ((CommonToken)loc).getStartIndex() );
 			} 
 		( constraints[pattern]  )? 
 		endLoc=RIGHT_PAREN
		{
		        if( endLoc.getType() == RIGHT_PAREN ) {
				this.location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
				pattern.setEndLocation( offset(endLoc.getLine()), endLoc.getCharPositionInLine() );	
				pattern.setEndCharacter( ((CommonToken)endLoc).getStopIndex() );
 			        pattern.setRightParentCharacter( ((CommonToken)endLoc).getStartIndex() );
			}
 		}
 	;
	
	
constraints[PatternDescr pattern]
	:	constraint[pattern]
		( ',' { location.setType( Location.LOCATION_LHS_INSIDE_CONDITION_START ); } 
		  constraint[pattern] 
		)* 
	;
	
constraint[PatternDescr pattern]
	@init {
		ConditionalElementDescr top = null;
	}
	:
		{
			top = pattern.getConstraint();
		}
		or_constr[top]
	;	
	
or_constr[ConditionalElementDescr base]
	@init {
		OrDescr or = new OrDescr();
	}
	:
		and_constr[or] 
		( t=DOUBLE_PIPE 
			{
				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_START);
			}
		and_constr[or] 
		)*
		{
		        if( or.getDescrs().size() == 1 ) {
		                base.addOrMerge( (BaseDescr) or.getDescrs().get(0) );
		        } else if ( or.getDescrs().size() > 1 ) {
		        	base.addDescr( or );
		        }
		}
	;
	
and_constr[ConditionalElementDescr base]
	@init {
		AndDescr and = new AndDescr();
	}
	:
		unary_constr[and] 
		( t=DOUBLE_AMPER 
			{
				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_START);
			}
		unary_constr[and] 
		)*
		{
		        if( and.getDescrs().size() == 1) {
		                base.addOrMerge( (BaseDescr) and.getDescrs().get(0) );
		        } else if( and.getDescrs().size() > 1) {
		        	base.addDescr( and );
		        }
		}
	;
	
unary_constr[ConditionalElementDescr base]
	:
		( field_constraint[base] 
		| LEFT_PAREN or_constr[base] RIGHT_PAREN
		| EVAL predicate[base]
		)
	;	
		
field_constraint[ConditionalElementDescr base]
	@init {
		FieldBindingDescr fbd = null;
		FieldConstraintDescr fc = null;
		RestrictionConnectiveDescr top = null;
	}
	:
		( fb=ID ':' 
		    { 
			fbd = new FieldBindingDescr();
			fbd.setIdentifier( fb.getText() );
			fbd.setLocation( offset(fb.getLine()), fb.getCharPositionInLine() );
			fbd.setStartCharacter( ((CommonToken)fb).getStartIndex() );
			base.addDescr( fbd );

		    }
		)? 
		f=identifier	
		{
		    if( f != null ) {
			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
			location.setProperty(Location.LOCATION_PROPERTY_PROPERTY_NAME, f.getText());
		    
			if ( fbd != null ) {
			    fbd.setFieldName( f.getText() );
 			    fbd.setEndCharacter( ((CommonToken)f).getStopIndex() );
			} 
			fc = new FieldConstraintDescr(f.getText());
			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
			fc.setStartCharacter( ((CommonToken)f).getStartIndex() );
			top = fc.getRestriction();
			
			// it must be a field constraint, as it is not a binding
			if( fb == null ) {
			    base.addDescr( fc );
			}
		    }
		}
		(
			or_restr_connective[top]
			{
				// we must add now as we didn't before
				if( fb != null) {
				    base.addDescr( fc );
				}
			}
		|
			'->' predicate[base] 
		)?
	;
	

or_restr_connective[ RestrictionConnectiveDescr base ]
	@init {
		RestrictionConnectiveDescr or = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR);
	}
	:
		and_restr_connective[or] 
		( t=DOUBLE_PIPE 
			{
				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
			}
		  and_restr_connective[or] 
		)*
		{
		        if( or.getRestrictions().size() == 1 ) {
		                base.addOrMerge( (RestrictionDescr) or.getRestrictions().get( 0 ) );
		        } else if ( or.getRestrictions().size() > 1 ) {
		        	base.addRestriction( or );
		        }
		}
	;	

	
and_restr_connective[ RestrictionConnectiveDescr base ]
	@init {
		RestrictionConnectiveDescr and = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND);
	}
	:
		constraint_expression[and] 
		( t=DOUBLE_AMPER 
			{
				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
			}
		constraint_expression[and] 
		)*
		{
		        if( and.getRestrictions().size() == 1) {
		                base.addOrMerge( (RestrictionDescr) and.getRestrictions().get( 0 ) );
		        } else if ( and.getRestrictions().size() > 1 ) {
		        	base.addRestriction( and );
		        }
		}
	;

constraint_expression[RestrictionConnectiveDescr base]
        :	
		( compound_operator[base]
		| simple_operator[base]
		| LEFT_PAREN 
		{
			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
		}
		or_restr_connective[base] 
		RIGHT_PAREN
 		)
	;	
	
simple_operator[RestrictionConnectiveDescr base]
	@init {
		String op = null;
	}
	:
		(	t='=='
		|	t='>'
		|	t='>='
		|	t='<'
		|	t='<='
		|	t='!='
		|	t=CONTAINS
		|	t=MATCHES
		|	t=EXCLUDES
		|	t=MEMBEROF
		|	n=NOT t=MEMBEROF
		)
		{
  		    location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);
                    location.setProperty(Location.LOCATION_PROPERTY_OPERATOR, t.getText());
		    if( n != null ) {
		        op = "not "+t.getText();
		    } else {
		        op = t.getText();
		    }
		}
		rd=expression_value[op]
		{
			    if( rd != null ) {
				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_END);
			        base.addRestriction( rd );
			    } else if ( rd == null && op != null ) {
			        base.addRestriction( new LiteralRestrictionDescr(op, null) );
			    }
		}
	;	
	
compound_operator[RestrictionConnectiveDescr base]
	@init {
		String op = null;
		RestrictionConnectiveDescr group = null;
	}
	:
		( IN 
			{
			  op = "==";
			  group = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR);
			  base.addRestriction( group );
  		    	  location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);
                    	  location.setProperty(Location.LOCATION_PROPERTY_OPERATOR, "in");
			}
		| NOT IN 
			{
			  op = "!=";
			  group = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND);
			  base.addRestriction( group );
  		    	  location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT);
                    	  location.setProperty(Location.LOCATION_PROPERTY_OPERATOR, "in");
			}	
		)
		LEFT_PAREN rd=expression_value[op]
			{
			    if( rd != null ) {
			        group.addRestriction( rd );
			    }
			}
		( COMMA rd=expression_value[op]
			{
			    if( rd != null ) {
		        	group.addRestriction( rd );
			    }
			}
		)* 
		RIGHT_PAREN 
		{
			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_END);
		}
	;
	
expression_value[String op] returns [RestrictionDescr rd]
	:
		(	bvc=ID
			{
				rd = new VariableRestrictionDescr(op, bvc.getText());
			}
		|
			lc=enum_constraint 
			{ 
				rd  = new LiteralRestrictionDescr(op, lc, true);
			}						
		|
			lc=literal_constraint 
			{ 
				rd  = new LiteralRestrictionDescr(op, lc);
			}
		|	rvc=retval_constraint 
			{ 
				rd = new ReturnValueRestrictionDescr(op, rvc);							
			} 
		)	
		{
			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_END);
		}
	;	
	
literal_constraint returns [String text]
	@init {
		text = null;
	}
	:	(	t=STRING { text = getString( t ); } 
		|	t=INT    { text = t.getText(); }
		|	t=FLOAT	 { text = t.getText(); }
		|	t=BOOL 	 { text = t.getText(); }
		|	t=NULL   { text = null; }
		)
	;
	
enum_constraint returns [String text]
	@init {
		text = null;
	}
	:	
		id=ID { text=id.getText(); } ( '.' ident=identifier { text += "." + ident.getText(); } )+ 
	;	
	

predicate[ConditionalElementDescr base]
        @init {
		PredicateDescr d = null;
        }
	:
		{
			d = new PredicateDescr( );
		}
		text=paren_chunk[d]
		{
		        if( text != null ) {
			        String body = text.substring(1, text.length()-1);
			        d.setContent( body );
				base.addDescr( d );
		        }
		}
	;

paren_chunk[BaseDescr descr] returns [String text]
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
			chunk=paren_chunk[null]
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
		    if( descr != null ) {
		        descr.setEndCharacter( ((CommonToken)loc).getStopIndex() );
		    }
                }
	;

curly_chunk[BaseDescr descr] returns [String text]
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
			chunk=curly_chunk[descr]
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
		    if( descr != null ) {
		        descr.setEndCharacter( ((CommonToken)loc).getStopIndex() );
		    }
                }
	;

square_chunk[BaseDescr descr]  returns [String text]
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
			chunk=square_chunk[null]
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
		    if( descr != null ) {
		        descr.setEndCharacter( ((CommonToken)loc).getStopIndex() );
		    }
                }
	;
	
retval_constraint returns [String text]
	@init {
		text = null;
	}
	:	
		c=paren_chunk[null] { text = c.substring(1, c.length()-1); }
	;

lhs_or returns [BaseDescr d]
	@init{
		d = null;
		OrDescr or = null;
	}
	:	
		left=lhs_and {d = left; }
		( (OR|'||')
			{
				location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
			}
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
		( (AND|DOUBLE_AMPER)
			{
				location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
			}
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
		|	u=lhs_pattern (
		          FROM 
		          {
				location.setType(Location.LOCATION_LHS_FROM);
				location.setProperty(Location.LOCATION_FROM_CONTENT, "");
		          }
		          (
		           ( ACCUMULATE ) => (ac=accumulate_statement {ac.setResultPattern((PatternDescr) u); u=ac; })
		          |( COLLECT ) => (cs=collect_statement {cs.setResultPattern((PatternDescr) u); u=cs; }) 
		          |( ~(ACCUMULATE|COLLECT) ) => (fm=from_statement {fm.setPattern((PatternDescr) u); u=fm; }) 
		          )
		        )?
		|	u=lhs_forall  
		|	'(' u=lhs_or ')'
		) { d = u; }
		opt_semicolon
	;
	
lhs_exist returns [BaseDescr d]
	@init {
		d = null;
	}
	:	loc=EXISTS 
		{
			d = new ExistsDescr( ); 
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS );
		}
	        ( ( '(' pattern=lhs_or 
	           	{ if ( pattern != null ) ((ExistsDescr)d).addDescr( pattern ); }
	           end=')' 
	                { if ( end != null ) d.setEndCharacter( ((CommonToken)end).getStopIndex() ); }
	        )    
	        | pattern=lhs_pattern
	                {
	                	if ( pattern != null ) {
	                		((ExistsDescr)d).addDescr( pattern );
	                		d.setEndCharacter( pattern.getEndCharacter() );
	                	}
	                }
	        )
	;
	
lhs_not	returns [NotDescr d]
	@init {
		d = null;
	}
	:	loc=NOT 
		{
			d = new NotDescr( ); 
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_NOT );
		}
		( ( '(' pattern=lhs_or  
	           	{ if ( pattern != null ) d.addDescr( pattern ); }
	           end=')' 
	                { if ( end != null ) d.setEndCharacter( ((CommonToken)end).getStopIndex() ); }
		  )
		| 
		pattern=lhs_pattern
	                {
	                	if ( pattern != null ) {
	                		d.addDescr( pattern );
	                		d.setEndCharacter( pattern.getEndCharacter() );
	                	}
	                }
		)
	;

lhs_eval returns [BaseDescr d]
	@init {
		d = new EvalDescr( );
	}
	:
		loc=EVAL 
		{
			location.setType( Location.LOCATION_LHS_INSIDE_EVAL );
		}
		c=paren_chunk[d]
		{ 
			if ( loc != null ) d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
		        if( c != null ) {
	  		    this.location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
		            String body = c.length() > 1 ? c.substring(1, c.length()-1) : "";
			    checkTrailingSemicolon( body, offset(loc.getLine()) );
			    ((EvalDescr) d).setContent( body );
			    location.setProperty(Location.LOCATION_EVAL_CONTENT, body);
			}
		}
	;
	
lhs_forall returns [ForallDescr d]
	@init {
		d = factory.createForall();
	}
	:	loc=FORALL '(' base=lhs_pattern   
		{
			if ( loc != null ) d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
		        // adding the base pattern
		        d.addDescr( base );
			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
		}
		( (',')? pattern=lhs_pattern
		{
		        // adding additional patterns
			d.addDescr( pattern );
		}
		)+
		end=')'
		{
		        if ( end != null ) d.setEndCharacter( ((CommonToken)end).getStopIndex() );
		}
	;

dotted_name[BaseDescr descr] returns [String name]
	@init {
		name = null;
	}
	:	
		id=ID 
		{ 
		    name=id.getText(); 
		    if( descr != null ) {
			descr.setStartCharacter( ((CommonToken)id).getStartIndex() );
			descr.setEndCharacter( ((CommonToken)id).getStopIndex() );
		    }
		} 
		( '.' ident=identifier 
		    { 
		        name = name + "." + ident.getText(); 
    		        if( descr != null ) {
			    descr.setEndCharacter( ((CommonToken)ident).getStopIndex() );
		        }
		    } 
		)* 
		( '[' loc=']'
		    { 
		        name = name + "[]";
    		        if( descr != null ) {
			    descr.setEndCharacter( ((CommonToken)loc).getStopIndex() );
		        }
		    }
		)*
	;
	
argument returns [String name]
	@init {
		name = null;
	}
	:
		id=identifier { name=id.getText(); } ( '[' ']' { name = name + "[]";})*
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
		start=THEN
		{
			location.setType( Location.LOCATION_RHS );
		}
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
 		    location.setProperty( Location.LOCATION_RHS_CONTENT, rule.getConsequence() );
                }
	;

name returns [String s]
	:
	( 
	    tok=ID
	    {
	        s = tok.getText();
	    }
	|
	    str=STRING
	    {
	       s = getString( str );
	    }
	)
	;
	
identifier returns [Token tok]
	:	
	(       t=ID      
	|	t=PACKAGE
	|	t=FUNCTION
	|	t=GLOBAL
	|	t=IMPORT  
	|	t=RULE
	|	t=QUERY 
        |       t=TEMPLATE        
        |       t=ATTRIBUTES      
        |       t=ENABLED         
        |       t=SALIENCE 	
        |       t=DURATION 	
        |       t=FROM	        
        |       t=ACCUMULATE 	
        |       t=INIT	        
        |       t=ACTION	        
        |       t=RESULT	        
        |       t=COLLECT         
        |       t=OR	        
        |       t=AND	        
        |       t=CONTAINS 	
        |       t=EXCLUDES 	
        |       t=MEMBEROF
        |       t=MATCHES         
        |       t=NULL	        
        |       t=EXISTS	        
        |       t=NOT	        
        |       t=EVAL	        
        |       t=FORALL	            					
        |       t=WHEN            
        |       t=THEN	        
        |       t=END     
        |	t=IN        
	) 
	{
	    tok = t;
	}
	;
	
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
    :   '\\' ('b'|'B'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\'|'.'|'o'|'x'|'a'|'e'|'c'|'d'|'D'|'s'|'S'|'w'|'W'|'p'|'A'|'G'|'Z'|'z'|'Q'|'E')
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

PACKAGE	:	'package';

IMPORT	:	'import';

FUNCTION :	'function';

GLOBAL	:	'global';
	
RULE    :	'rule';

QUERY	:	'query';

TEMPLATE :	'template';

ATTRIBUTES :	'attributes';
	
DATE_EFFECTIVE 
	:	'date-effective';

DATE_EXPIRES 
	:	'date-expires';	
	
ENABLED :	'enabled';

SALIENCE 
	:	'salience';
	
NO_LOOP :	'no-loop';

AUTO_FOCUS 
	:	'auto-focus';
	
ACTIVATION_GROUP 
	:	'activation-group';
	
AGENDA_GROUP 
	:	'agenda-group';
	
DIALECT 
	:	'dialect';	
	
RULEFLOW_GROUP 
	:	'ruleflow-group';
	
DURATION 
	:	'duration';
	
LOCK_ON_ACTIVE
	:	'lock-on-active';	
	
FROM	:	'from';

ACCUMULATE 
	:	'accumulate';
	
INIT	:	'init';

ACTION	:	'action';

RESULT	:	'result';

COLLECT :	'collect';

OR	:	'or';

AND	:	'and';

CONTAINS 
	:	'contains';
	
EXCLUDES 
	:	'excludes';
	
MEMBEROF
	:	'memberOf';

MATCHES :	'matches';

IN	:	'in';

NULL	:	'null';

EXISTS	:	'exists';

NOT	:	'not';

EVAL	:	'eval';

FORALL	:	'forall';							

WHEN    :	'when'; 

THEN	:    	'then';

END     :	'end';

ID	
	:	('a'..'z'|'A'..'Z'|'_'|'$'|'\u00c0'..'\u00ff')('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\u00c0'..'\u00ff')* 
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
        
COMMA	:	','
	;
	
DOUBLE_AMPER
	:	'&&'
	;
	
DOUBLE_PIPE
	:	'||'
	;				
	
SH_STYLE_SINGLE_LINE_COMMENT	
	:	'#' ( options{greedy=false;} : .)* EOL /* ('\r')? '\n'  */
                { $channel=HIDDEN; }
	;
        
        
C_STYLE_SINGLE_LINE_COMMENT	
	:	'//' ( options{greedy=false;} : .)* EOL // ('\r')? '\n' 
                { $channel=HIDDEN; }
	;

MULTI_LINE_COMMENT
	:	'/*' (options{greedy=false;} : .)* '*/'
                { $channel=HIDDEN; }
	;

MISC 	:
		'!' | '@' | '$' | '%' | '^' | '*' | '_' | '-' | '+'  | '?' | '=' | '/' | '\'' | '\\' | '|' | '&'
	;
