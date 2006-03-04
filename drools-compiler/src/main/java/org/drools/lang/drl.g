grammar RuleParser; 

@parser::header {
	package org.drools.lang;
	import java.util.List;
	import java.util.ArrayList;
	import org.drools.lang.descr.*;
}

@parser::members {
	private String    packageName = "";
	private List      rules       = new ArrayList();
	private List      imports     = new ArrayList();
	
	public String getPackageName() { return packageName; }
	public List getImports() { return imports; }
	public List getRules() { return rules; }
}

@lexer::header {
	package org.drools.lang;
}

opt_eol	:
		EOL*	
	;

compilation_unit
	:	prolog 
		(r=rule {this.rules.add( r ); })*
	;
	
prolog
	:	opt_eol
		( name=package_statement { this.packageName = name; } )?
		opt_eol
		( name=import_statement { this.imports.add( name ); } )*
		opt_eol
		use_expander?
		opt_eol
	;
	
package_statement returns [String packageName]
	@init{
		packageName = null;
	}
	:	'package' opt_eol id=ID { packageName = id.getText(); } ( '.' id=ID { packageName += "." + id.getText(); } )* ';'? opt_eol	
	;
	
import_statement returns [String importStatement]
	@init {
		importStatement = null;
	}
	:	'import' opt_eol name=java_package_or_class ';'? { importStatement = name; } opt_eol	
	;

use_expander
	:	'use' 'expander' ID ';'? opt_eol
	;


rule returns [RuleDescr rule]
	@init {
		rule = null;
		String consequence = "";
	}
	:
		opt_eol
		'rule' ruleName=word opt_eol 
		{ rule = new RuleDescr( ruleName, null ); }
		rule_options?
		(	'when' ':'? opt_eol
			{ AndDescr lhs = new AndDescr(); rule.setLhs( lhs ); }
				(l=lhs { lhs.addDescr( l ); } )*
		)?
		(	'then' ':'?
			(any=.
				{
					consequence = consequence + " " + any.getText();
				}
			)*
			{ rule.setConsequence( consequence ); }
		)?
		EOL 'end' opt_eol
	;

rule_options
	:	'options' ':'? opt_eol
			( salience | no_loop ) opt_eol ( ','? opt_eol ( salience | no_loop ) )* opt_eol
	;
	
salience
	:	
		'salience' INT ';'? opt_eol
	;
	
no_loop
	:
		'no-loop' ';'? opt_eol
	;
	
	
lhs returns [PatternDescr d]
	@init {
		d=null;
	}
	:	l=lhs_or { d = l; }
	;

	
lhs_column returns [ColumnDescr d]
	@init {
		d=null;
	}
	:	fact_binding
	|	fact
	;
 	
fact_binding returns [PatternDescr d]
	@init {
		d=null;
	}
 	:
 		ID opt_eol ':' opt_eol f=fact { d=f; } opt_eol
 	;
 
fact returns [PatternDescr d] 
	@init {
		d=null;
	}
 	:	ID opt_eol '(' opt_eol constraints? opt_eol ')' opt_eol
 	;
 	
	
constraints returns [List constraints]
	@init {
		constraints = new ArrayList();
	}
	:	opt_eol
		c=constraint  { constraints.add( c ); }
		( opt_eol ',' opt_eol c=constraint { constraints.add( c ); } )*
		opt_eol
	;
	
constraint returns [PatternDescr d]
	@init {
		d = null;
	}
	:	opt_eol
		f=ID	opt_eol op=(	'=='
			|	'>'
			|	'>='
			|	'<'
			|	'<='
			|	'!='
			) opt_eol	(	lc=literal_constraint { d = new LiteralDescr( f.getText(), null, lc ); }
					|	rvc=retval_constraint { d = new ReturnValueDescr( f.getText(), null, rvc ); } )
		opt_eol
	;
	
literal_constraint returns [String text]
	@init {
		text = null;
	}
	:	(	t=STRING { text = t.getText(); text=text.substring( 1, text.length() - 1 ); }
		|	t=INT    { text = t.getText(); }
		|	t=FLOAT	 { text = t.getText(); }
		)
	;
	
retval_constraint returns [String text]
	@init {
		text = null;
	}
	:	
		c=chunk { text = c; }
	;
	
chunk returns [String text]
	@init {
		text = null;
	}
	:	(	( any=. {
					if ( text == null ) {
						text = any.getText();
					} else {
						text = text + " " + any.getText(); 
					} 
				})
		|	( '(' c=chunk ')' 	{
							if ( text == null ) {
								text = "( " + c + " )";
							} else {
								text = text + " ( " + c + " )";
							}
						} )
		)*
	;
	
	
field_binding
	:
	;
	
lhs_or returns [PatternDescr d]
	@init{
		d = null;
	}
	:	
		{ OrDescr or = null; }
		left=lhs_and {d = left; }
		( 	('or'|'||') 
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
	
lhs_and returns [PatternDescr d]
	@init{
		d = null;
	}
	:
		{ AndDescr and = null; }
		left=lhs_unary { d = left; }
		(	('and'|'&&') 
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
	
lhs_unary returns [PatternDescr d]
	@init {
		d = null;
	}
	:	(	u=lhs_exist
		|	u=lhs_not
		|	u=lhs_eval
		|	u=lhs_column
		|	'(' u=lhs ')'
		) { d = u; }
	;
	
lhs_exist returns [PatternDescr d]
	@init {
		d = null;
	}
	:	'exists' column=lhs_column { d = new ExistsDescr( column ); }	
	;
	
lhs_not	returns [NotDescr d]
	@init {
		d = null;
	}
	:	'not' column=lhs_column { d = new NotDescr( column ); }
	;

lhs_eval returns [PatternDescr d]
	@init {
		d = null;
	}
	:	'eval' { d = new EvalDescr( "" ); }
	;
	
java_package_or_class returns [String name]
	@init {
		name = null;
	}
	:	
		id=ID { name=id.getText(); } ( '.' id=ID { name = name + "." + id.getText(); } )*
	;
	
	
word returns [String word]
	@init{
		word = null;
	}
	:	id=ID      { word=id.getText(); }
	|	'import'   { word="import"; }
	|	'use'      { word="use"; }
	|	'rule'     { word="rule"; }
	|	'salience' { word="salience"; }
 	|	'no-loop'  { word="no-loop"; }
	|	'when'     { word="when"; }
	|	'then'     { word="then"; }
	|	'end'      { word="end"; }
	|	str=STRING { word=str.getText(); word=word.substring( 1, word.length()-1 ); }
	;


WS      :       (	' '
                |	'\t'
                |	'\f'
                )
                { channel=99; }
        ;
        
EOL 	:	     
   		(       '\r\n'  // Evil DOS
                |       '\r'    // Macintosh
                |       '\n'    // Unix (the right way)
                )
        ;  
        
INT	
	:	('0'..'9')+
	;

FLOAT
	:	('0'..'9')+ '.' ('0'..'9')+
	;
	
STRING
	:	'"' ( options{greedy=false;} : .)* '"' 
	;
	
ID	
	:	('a'..'z'|'A'..'Z'|'_')+ 
	;

SH_STYLE_SINGLE_LINE_COMMENT	
	:	'#' ( options{greedy=false;} : .)* ('\r')? '\n' 
	;
        
        
C_STYLE_SINGLE_LINE_COMMENT	
	:	'//' ( options{greedy=false;} : .)* ('\r')? '\n' 
	;

MULTI_LINE_COMMENT
	:	'/*' (options{greedy=false;} : .)* '*/'
	;
