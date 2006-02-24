package ${package};

<#list imports as importEntry>import ${importEntry};</#list>

public class ${ruleClassName}
{
    <#list methods as method>
    ${method}
    </#list>	   
}      