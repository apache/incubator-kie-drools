tree grammar DescrBuilderTree5x;

options{
	tokenVocab=DRL5x;
	ASTLabelType=DroolsTree;
	TokenLabelType=DroolsToken;
}

@header {
	package org.drools.lang;

	import java.util.HashMap;
	import java.util.Map;
	import java.util.LinkedList;
	import org.drools.lang.descr.AccessorDescr;
	import org.drools.lang.descr.AccumulateDescr;
	import org.drools.lang.descr.AndDescr;
	import org.drools.lang.descr.AttributeDescr;
	import org.drools.lang.descr.BaseDescr;
	import org.drools.lang.descr.BehaviorDescr;
	import org.drools.lang.descr.DeclarativeInvokerDescr;
	import org.drools.lang.descr.DescrFactory;
	import org.drools.lang.descr.FactTemplateDescr;
	import org.drools.lang.descr.FieldConstraintDescr;
	import org.drools.lang.descr.FieldTemplateDescr;
	import org.drools.lang.descr.FromDescr;
	import org.drools.lang.descr.FunctionDescr;
	import org.drools.lang.descr.FunctionImportDescr;
	import org.drools.lang.descr.GlobalDescr;
	import org.drools.lang.descr.ImportDescr;
	import org.drools.lang.descr.PackageDescr;
	import org.drools.lang.descr.PatternSourceDescr;
	import org.drools.lang.descr.QueryDescr;
	import org.drools.lang.descr.RuleDescr;
	import org.drools.lang.descr.TypeDeclarationDescr;
	import org.drools.lang.descr.TypeFieldDescr;
}

@members {
	DescrFactory factory = new DescrFactory();
	PackageDescr packageDescr = null;
	
	public PackageDescr getPackageDescr() {
		return packageDescr;
	}
}

compilation_unit
	:	^(VT_COMPILATION_UNIT package_statement statement*)
	;

package_statement returns [String packageName]
	:	^(VK_PACKAGE packageId=package_id)
	{	this.packageDescr = factory.createPackage($packageId.idList);	
		$packageName = packageDescr.getName();	}
	|	
	{	this.packageDescr = factory.createPackage(null);	
		$packageName = "";	}
	;

package_id returns [List idList]
	:	^(VT_PACKAGE_ID tempList+=ID+)
	{	$idList = $tempList;	}
	;

statement
	:	a=rule_attribute
	{	this.packageDescr.addAttribute($a.attributeDescr);	}
	|	fi=function_import_statement
	{	this.packageDescr.addFunctionImport($fi.functionImportDescr);	}
	|	is=import_statement 
	{	this.packageDescr.addImport($is.importDescr);	}
	|	gl=global
	{	this.packageDescr.addGlobal($gl.globalDescr);	}
	|	fn=function
	{	this.packageDescr.addFunction($fn.functionDescr);	}
	|	tp=template
	{	this.packageDescr.addFactTemplate($tp.factTemplateDescr);	}
	|	rl=rule
	{	this.packageDescr.addRule($rl.ruleDescr);	}
	|	qr=query
	{	this.packageDescr.addRule($qr.queryDescr);	}
	|	td=type_declaration
	{	this.packageDescr.addTypeDeclaration($td.declaration);	}
	;

import_statement returns [ImportDescr importDescr]
	:	^(importStart=VK_IMPORT importId=import_name)
	{	$importDescr = factory.createImport($importStart, $importId.idList, $importId.dotStar);	}
	;

function_import_statement returns [FunctionImportDescr functionImportDescr]
	:	^(importStart=VT_FUNCTION_IMPORT VK_FUNCTION importId=import_name)
	{	$functionImportDescr = factory.createFunctionImport($importStart, $importId.idList, $importId.dotStar);	}
	;

import_name returns[List idList, DroolsTree dotStar]
	:	^(VT_IMPORT_ID tempList+=ID+ tempDotStar=DOT_STAR?)
	{	$idList = $tempList;
		$dotStar = $tempDotStar;	}
	;

global returns [GlobalDescr globalDescr]
	:	^(start=VK_GLOBAL dt=data_type globalId=VT_GLOBAL_ID)
	{	$globalDescr = factory.createGlobal($start,$dt.dataType, $globalId);	}
	;

function returns [FunctionDescr functionDescr]
	:	^(start=VK_FUNCTION dt=data_type? functionId=VT_FUNCTION_ID params=parameters content=VT_CURLY_CHUNK)
	{	$functionDescr = factory.createFunction($start, $dt.dataType, $functionId, $params.paramList, $content);	}
	;

template returns [FactTemplateDescr factTemplateDescr]
@init{
	List slotList = new LinkedList<FieldTemplateDescr>();
}	:	^(start=VK_TEMPLATE id=VT_TEMPLATE_ID
			( ts=template_slot {slotList.add($ts.fieldTemplateDescr);})+ end=VK_END)
	{	$factTemplateDescr = factory.createFactTemplate($start, $id, slotList, $end);	}
	;

template_slot returns [FieldTemplateDescr fieldTemplateDescr]
	:	^(VT_SLOT dt=data_type id=VT_SLOT_ID)
	{	$fieldTemplateDescr = factory.createFieldTemplate($dt.dataType, $id);	}
	;

query returns [QueryDescr queryDescr]
	:	^(start=VK_QUERY id=VT_QUERY_ID params=parameters? lb=lhs_block end=VK_END)
	{	$queryDescr = factory.createQuery($start, $id, $params.paramList, $lb.andDescr, $end);	}
	;

rule returns [RuleDescr ruleDescr]
@init {	List<Map> declMetadaList = new LinkedList<Map>();}
	:	^(start=VK_RULE id=VT_RULE_ID  (^(VK_EXTEND parent_id=VT_RULE_ID))?
		(dm=decl_metadata {declMetadaList.add($dm.attData);	})*
		 ra=rule_attributes? 
		 wn=when_part? content=VT_RHS_CHUNK)
	{	$ruleDescr = factory.createRule($start, $id, $parent_id, $ra.attrList, $wn.andDescr, $content, declMetadaList);	}
	;

when_part returns [AndDescr andDescr]
	:	WHEN lh=lhs_block
	{	$andDescr = $lh.andDescr;	}
	;

rule_attributes returns [List attrList]
@init{
	$attrList = new LinkedList<AttributeDescr>();
}	:	^(VT_RULE_ATTRIBUTES VK_ATTRIBUTES? (rl=rule_attribute {attrList.add($rl.attributeDescr);})+)
	;

parameters returns [List paramList]
@init{
	$paramList = new LinkedList<Map<BaseDescr, BaseDescr>>();
}	:	^(VT_PARAM_LIST (p=param_definition {$paramList.add($p.param);})*)
	;

param_definition returns [Map param]
	:	dt=data_type? a=argument
	{	$param = new HashMap<BaseDescr, BaseDescr>();
		$param.put($a.arg, $dt.dataType);	}
	;

argument returns [BaseDescr arg]
	:	id=ID (LEFT_SQUARE rightList+=RIGHT_SQUARE)*
	{	$arg = factory.createArgument($id, $rightList);	}
	;

type_declaration returns [TypeDeclarationDescr declaration]
@init {	List<Map> declMetadaList = new LinkedList<Map>();
		List<TypeFieldDescr> declFieldList = new LinkedList<TypeFieldDescr>(); }
	:	^(VK_DECLARE id=VT_TYPE_DECLARE_ID 
			(dm=decl_metadata {declMetadaList.add($dm.attData);	})* 
			(df=decl_field {declFieldList.add($df.fieldDescr);	})* VK_END)
	{	$declaration = factory.createTypeDeclr($id, declMetadaList, declFieldList);	}
	;

decl_metadata returns [Map attData]
@init {attData = new HashMap();}
	:	^(AT att=ID pc=VT_PAREN_CHUNK?)
	{	$attData.put($att, $pc);	}
	;

decl_field returns [TypeFieldDescr fieldDescr]
@init {List<Map> declMetadaList = new LinkedList<Map>(); }
	:	^(id=ID init=decl_field_initialization? dt=data_type (dm=decl_metadata {declMetadaList.add($dm.attData);})*)
	{	$fieldDescr = factory.createTypeField($id, $init.expr, $dt.dataType, declMetadaList);	}			
	;

decl_field_initialization returns [String expr]
	:	^(EQUALS pc=VT_PAREN_CHUNK)
	{	$expr = $pc.text.substring(1, $pc.text.length() -1 ).trim();	}
	;

rule_attribute returns [AttributeDescr attributeDescr]
	:	(^(attrName=VK_SALIENCE (value=INT|value=VT_PAREN_CHUNK)) 
	|	^(attrName=VK_NO_LOOP value=BOOL?)  
	|	^(attrName=VK_AGENDA_GROUP value=STRING)  
	|	^(attrName=VK_TIMER (value=INT|value=VT_PAREN_CHUNK))   
	|	^(attrName=VK_ACTIVATION_GROUP value=STRING) 
	|	^(attrName=VK_AUTO_FOCUS value=BOOL?) 
	|	^(attrName=VK_DATE_EFFECTIVE value=STRING) 
	|	^(attrName=VK_DATE_EXPIRES value=STRING) 
	|	^(attrName=VK_ENABLED (value=BOOL|value=VT_PAREN_CHUNK)) 
	|	^(attrName=VK_RULEFLOW_GROUP value=STRING) 
	|	^(attrName=VK_LOCK_ON_ACTIVE value=BOOL?)
	|	^(attrName=VK_DIALECT value=STRING)
	|	^(attrName=VK_CALENDARS value=STRING))
	{	$attributeDescr = factory.createAttribute($attrName, $value);	}
	;
	
lhs_block returns [AndDescr andDescr]
@init{
	$andDescr = new AndDescr();
}	:	^(VT_AND_IMPLICIT (dt=lhs {$andDescr.addDescr($dt.baseDescr);})*)
	;

lhs	returns [BaseDescr baseDescr]
@init{
	List<BaseDescr> lhsList = new LinkedList<BaseDescr>();
}	:	^(start=VT_OR_PREFIX (dt=lhs {	lhsList.add($dt.baseDescr);	})+)
	{	$baseDescr = factory.createOr($start, lhsList);	}
	|	^(start=VT_OR_INFIX dt1=lhs dt2=lhs)
	{	lhsList.add($dt1.baseDescr);
		lhsList.add($dt2.baseDescr);
		$baseDescr = factory.createOr($start, lhsList);	}
	|	^(start=VT_AND_PREFIX (dt=lhs {	lhsList.add($dt.baseDescr);	})+)
	{	$baseDescr = factory.createAnd($start, lhsList);	}
	|	^(start=VT_AND_INFIX dt1=lhs dt2=lhs)
	{	lhsList.add($dt1.baseDescr);
		lhsList.add($dt2.baseDescr);
		$baseDescr = factory.createAnd($start, lhsList);	}
	|	^(start=VK_EXISTS dt=lhs)
	{	$baseDescr = factory.createExists($start, $dt.baseDescr);	}
	|	^(start=VK_NOT dt=lhs)
	{	$baseDescr = factory.createNot($start, $dt.baseDescr);	}
	|	^(start=VK_EVAL pc=VT_PAREN_CHUNK)
	{	$baseDescr = factory.createEval($start, $pc);	}
	|	^(start=VK_FORALL (dt=lhs {	lhsList.add($dt.baseDescr);	})+)
	{	$baseDescr = factory.createForAll($start, lhsList);	}
	|	^(FROM pn=lhs_pattern fe=from_elements)
	{	$baseDescr = factory.setupFrom($pn.baseDescr, $fe.patternSourceDescr);	}
	|	pn=lhs_pattern
	{	$baseDescr = $pn.baseDescr;	}
	;

from_elements returns [PatternSourceDescr patternSourceDescr]
	:	^(start=ACCUMULATE dt=lhs
	{	$patternSourceDescr = factory.createAccumulate($start, $dt.baseDescr);	} 
		ret=accumulate_parts[$patternSourceDescr])
	{	$patternSourceDescr = $ret.accumulateDescr;	}
	|	^(start=COLLECT dt=lhs)
	{	$patternSourceDescr = factory.createCollect($start, $dt.baseDescr);	}
	|	^(start=VK_ENTRY_POINT entryId=VT_ENTRYPOINT_ID)
	{	$patternSourceDescr = factory.createEntryPoint($start, $entryId);	}
	|	fs=from_source_clause
	{	$patternSourceDescr = $fs.fromDescr;	}
	;

accumulate_parts[PatternSourceDescr patternSourceDescr] returns [AccumulateDescr accumulateDescr]
	:	ac1=accumulate_init_clause[$patternSourceDescr]
	{	$accumulateDescr = $ac1.accumulateDescr;	}
	|	ac2=accumulate_id_clause[$patternSourceDescr]
	{	$accumulateDescr = $ac2.accumulateDescr;	}
	;

accumulate_init_clause [PatternSourceDescr accumulateParam] returns [AccumulateDescr accumulateDescr] 
	:	^(VT_ACCUMULATE_INIT_CLAUSE 
			^(start=VK_INIT pc1=VT_PAREN_CHUNK) 
			^(VK_ACTION pc2=VT_PAREN_CHUNK) 
			rev=accumulate_init_reverse_clause?
			^(VK_RESULT pc3=VT_PAREN_CHUNK))
	{	if (null == rev){
			$accumulateDescr = factory.setupAccumulateInit($accumulateParam, $start, $pc1, $pc2, $pc3, null);
		} else {
			$accumulateDescr = factory.setupAccumulateInit($accumulateParam, $start, $pc1, $pc2, $pc3, $rev.vkReverseChunk);
		}	}
	;

accumulate_init_reverse_clause returns [DroolsTree vkReverse, DroolsTree vkReverseChunk]
	:	^(vk=VK_REVERSE pc=VT_PAREN_CHUNK)
	{	$vkReverse = $vk;
		$vkReverseChunk = $pc;	}
	;

accumulate_id_clause [PatternSourceDescr accumulateParam] returns [AccumulateDescr accumulateDescr]
	:	^(VT_ACCUMULATE_ID_CLAUSE id=ID pc=VT_PAREN_CHUNK)
	{	$accumulateDescr = factory.setupAccumulateId($accumulateParam, $id, $pc);	}
	;

from_source_clause returns [FromDescr fromDescr, AccessorDescr retAccessorDescr]
scope{
	AccessorDescr accessorDescr;
}	:	^(VT_FROM_SOURCE id=ID pc=VT_PAREN_CHUNK? 
	{	$from_source_clause::accessorDescr = factory.createAccessor($id, $pc);	
		$retAccessorDescr = $from_source_clause::accessorDescr;	}
		expression_chain?)
	{	$fromDescr = factory.createFromSource(factory.setupAccessorOffset($from_source_clause::accessorDescr)); }
	;

expression_chain
	:	^(start=VT_EXPRESSION_CHAIN id=ID sc=VT_SQUARE_CHUNK? pc=VT_PAREN_CHUNK?
	{	DeclarativeInvokerDescr declarativeInvokerResult = factory.createExpressionChain($start, $id, $sc, $pc);	
		$from_source_clause::accessorDescr.addInvoker(declarativeInvokerResult);	}
		expression_chain?)
	;

lhs_pattern returns [BaseDescr baseDescr]
	:	^(VT_PATTERN fe=fact_expression) oc=over_clause?
	{	$baseDescr = factory.setupBehavior($fe.descr, $oc.behaviorList);	}
	;

over_clause returns [List behaviorList]
@init {$behaviorList = new LinkedList();}
	:	^(OVER (oe=over_element {$behaviorList.add($oe.behavior);})+)
	;

over_element returns [BehaviorDescr behavior]
	:	^(VT_BEHAVIOR ID id2=ID pc=VT_PAREN_CHUNK)
	{	$behavior = factory.createBehavior($id2,$pc);	}
	;

fact_expression returns [BaseDescr descr]
@init{
	List<BaseDescr> exprList = new LinkedList<BaseDescr>();
}	:	^(VT_FACT pt=pattern_type (fe=fact_expression {exprList.add($fe.descr);})*)
	{	$descr = factory.createPattern($pt.dataType, exprList);	}
	|	^(VT_FACT_BINDING label=VT_LABEL fact=fact_expression)
	{	$descr = factory.setupPatternBiding($label, $fact.descr);	}
	|	^(start=VT_FACT_OR left=fact_expression right=fact_expression)
	{	$descr = factory.createFactOr($start, $left.descr, $right.descr);	}

	|	^(VT_FIELD field=field_element fe=fact_expression?)
	{	if (null != fe){
			$descr = factory.setupFieldConstraint($field.element, $fe.descr);
		} else {
			$descr = factory.setupFieldConstraint($field.element, null);
		}	}
	|	^(VT_BIND_FIELD label=VT_LABEL fe=fact_expression)
	{	$descr = factory.createFieldBinding($label, $fe.descr);	}

	|	^(VK_EVAL pc=VT_PAREN_CHUNK)
	{	$descr = factory.createPredicate($pc);	}

	|	^(op=EQUAL fe=fact_expression)
	{	$descr = factory.setupRestriction($op, null, $fe.descr);	}
	|	^(op=NOT_EQUAL fe=fact_expression)
	{	$descr = factory.setupRestriction($op, null, $fe.descr);	}
	|	^(op=GREATER fe=fact_expression)
	{	$descr = factory.setupRestriction($op, null, $fe.descr);	}
	|	^(op=GREATER_EQUAL fe=fact_expression)
	{	$descr = factory.setupRestriction($op, null, $fe.descr);	}
	|	^(op=LESS fe=fact_expression)
	{	$descr = factory.setupRestriction($op, null, $fe.descr);	}
	|	^(op=LESS_EQUAL fe=fact_expression)
	{	$descr = factory.setupRestriction($op, null, $fe.descr);	}
	|	^(op=VK_OPERATOR not=VK_NOT? param=VT_SQUARE_CHUNK? fe=fact_expression)
	{	$descr = factory.setupRestriction($op, $not, $fe.descr, $param);	}

	|	^(VK_IN not=VK_NOT? (fe=fact_expression {exprList.add($fe.descr);})+)
	{	$descr = factory.createRestrictionConnective($not, exprList);	}

	|	^(DOUBLE_PIPE left=fact_expression right=fact_expression)
	{	$descr = factory.createOrRestrictionConnective($left.descr, $right.descr);	}
	|	^(DOUBLE_AMPER left=fact_expression right=fact_expression)
	{	$descr = factory.createAndRestrictionConnective($left.descr, $right.descr);	}

	|	^(VT_ACCESSOR_PATH (ae=accessor_element {exprList.add($ae.element);})+)
	{	$descr = factory.createAccessorPath(exprList);	}
	|	s=STRING
	{	$descr = factory.createStringLiteralRestriction($s);	}
	|	i=INT
	{	$descr = factory.createIntLiteralRestriction($i, false);	}
	|	f=FLOAT
	{	$descr = factory.createFloatLiteralRestriction($f, false);	}
	|	b=BOOL
	{	$descr = factory.createBoolLiteralRestriction($b);	}
	|	n=NULL
	{	$descr = factory.createNullLiteralRestriction($n);	}
	|	pc=VT_PAREN_CHUNK
	{	$descr = factory.createReturnValue($pc);	}
	;

field_element returns [FieldConstraintDescr element]
@init{
	List<BaseDescr> aeList = new LinkedList<BaseDescr>();
}	:	^(VT_ACCESSOR_PATH (ae=accessor_element {aeList.add($ae.element);})+)
	{	$element = factory.createFieldConstraint(aeList);	}
	;

accessor_element returns [BaseDescr element]
	:	^(VT_ACCESSOR_ELEMENT id=ID sc+=VT_SQUARE_CHUNK*)
	{	$element = factory.createAccessorElement($id, $sc);	}
	;

pattern_type returns [BaseDescr dataType]
	:	^(VT_PATTERN_TYPE idList+=ID+ (LEFT_SQUARE rightList+=RIGHT_SQUARE)*)
	{	$dataType = factory.createDataType($idList, $rightList);	}
	;

data_type returns [BaseDescr dataType]
	:	^(VT_DATA_TYPE idList+=ID+ (LEFT_SQUARE rightList+=RIGHT_SQUARE)*)
	{	$dataType = factory.createDataType($idList, $rightList);	}
	;
