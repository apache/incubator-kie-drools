// This file was automatically generated from a YAML representation.
<#if name?has_content>package ${name};</#if>

<#list imports as import>
import ${import.target};
</#list>

<#list rules as rule>
rule "${rule.name}"
when
<#list rule.when as pattern>
<#nt>  <@basePatternVisitor p=pattern /></#list>then
${rule.then.then}
end
</#list>

<#macro basePatternVisitor p>
<#if p.class.simpleName == "Pattern">
<@patternVisitor p=p />
</#if>
</#macro>

<#macro patternVisitor p>
<#if p.as?has_content>${p.as} : </#if>${p.given}(${p.having?join(", ")})<#if p.getFrom()?has_content> from ${p.getFrom()}</#if>
</#macro>
