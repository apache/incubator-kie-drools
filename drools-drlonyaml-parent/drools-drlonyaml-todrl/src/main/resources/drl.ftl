// This file was automatically generated from a YAML representation.
<#if name?has_content>package ${name};</#if>
<#if unit?has_content>unit ${unit};</#if>
<#if dialect?has_content>dialect "${dialect}";</#if>
<#--

## DRL imports

-->
<#list imports as import>
import ${import.target};
</#list>
<#--

## DRL globals

-->
<#list globals as global>
global ${global.type} ${global.id};
</#list>
<#--

## DRL rules

-->
<#list rules as rule>
rule "${rule.name}"
when
<#list rule.when as pattern>
<#nt>  <@basePatternVisitor p=pattern /></#list>then
${rule.then.then}
end
</#list>
<#--

## general visitor

-->
<#macro basePatternVisitor p>
<#if p.class.simpleName == "Pattern">
<@patternVisitor p=p />
<#elseif p.class.simpleName == "Exists">
<@existsVisitor p=p />
<#elseif p.class.simpleName == "Not">
<@notVisitor p=p />
<#elseif p.class.simpleName == "All">
<@allVisitor p=p />
<#else>
<#stop "unknown type of basePatternVisitor: "+p.class>
</#if>
</#macro>
<#--

## Pattern()

-->
<#macro patternVisitor p>
<#if p.as?has_content>${p.as} : </#if><#if p.given?has_content>${p.given}(${p.having?join(", ")})</#if><#if p.datasource?has_content>/${p.datasource}[${p.having?join(", ")}]</#if><#if p.getFrom()?has_content> from ${p.getFrom()}</#if>
</#macro>
<#--

## not( )

-->
<#macro notVisitor p>
not(
<#list p.not as pattern>
<#nt>  <@basePatternVisitor p=pattern /></#list>)
</#macro>
<#--

## (and ) using infix form

-->
<#macro allVisitor p>
<#list p.all as pattern>
<#nt>  <@basePatternVisitor p=pattern /><#sep>  and
</#sep></#list>
</#macro>
<#--

## exists( )

-->
<#macro existsVisitor p>
exists(
<#list p.exists as pattern>
<#nt>  <@basePatternVisitor p=pattern /></#list>)
</#macro>
<#--

## DRL functions

-->
<#list functions as f>
function ${f.returnType} ${f.name}(<#list f.parameters as param>${param.type} ${param.name}<#sep>, </#sep></#list>) {
${f.body}
}
</#list>
