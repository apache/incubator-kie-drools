<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="xml" indent="yes" />
	<!-- Converts a drools version 4 graphical ruleflow file (i.e. .rf file) to version 5-->
	<!-- How to get the first node so the the implementing class need not be mentioned here.-->
	<xsl:variable name="type">RuleFlow</xsl:variable>
	<xsl:variable name="name"><xsl:value-of select="//process/name/."/></xsl:variable>
	<xsl:variable name="id"><xsl:value-of select="//process/id/."/></xsl:variable>
	<xsl:variable name="packageName"><xsl:value-of select="//process/packageName/."/></xsl:variable>
	<xsl:variable name="version"><xsl:value-of select="//process/version/."/></xsl:variable>
	<xsl:variable name="routerLayout"><xsl:value-of select="//routerLayout/."/></xsl:variable>
	<xsl:param name="generateTypes">false</xsl:param>
	<xsl:param name="generateImports">false</xsl:param>
	<xsl:param name="generateIncludes">false</xsl:param>
	<xsl:param name="defaultIncludeFile">false</xsl:param>
	<xsl:template match="/">
		<!-- If we do not add the namespace then 5.0 Eclipse editor does not recognize the process 
		node if there are more than 2 sub processes defined in the ruleflow! 
		If we put the namespace then it gets reflected in child elements like ruleSet, join etc.-->
		<xsl:element name="process">
			<xsl:attribute name="type"><xsl:value-of select="$type"/></xsl:attribute>
			<xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>
			<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
			<xsl:attribute name="package-name"><xsl:value-of select="$packageName"/></xsl:attribute>
			<xsl:attribute name="version"><xsl:value-of select="$version"/></xsl:attribute>
			<xsl:attribute name="routerLayout"><xsl:value-of select="$routerLayout"/></xsl:attribute>
			<!-- TODO Could not add other namespace related attributes on the element node especially for schema location. -->
			<xsl:element name="header">
				<xsl:call-template name="processImports"/>
				<xsl:call-template name="processGlobals"/>
			</xsl:element>
			<xsl:element name="nodes">
				<xsl:call-template name="processNodes"/>
			</xsl:element>
			<xsl:element name="connections">
				<xsl:call-template name="formConnections" />
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="processImports">
		<xsl:if test="//imports">
			<xsl:element name="imports">
			<xsl:for-each select="//imports/string">
				<xsl:element name="import">
					<xsl:attribute name="name"><xsl:value-of select="."/></xsl:attribute>
				</xsl:element>
			</xsl:for-each>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="processGlobals">
		<xsl:if test="//globals">
			<xsl:element name="globals">
			<xsl:for-each select="//globals/entry">
				<xsl:element name="global">
					<xsl:attribute name="identifier"><xsl:value-of select="./string[1]/."/></xsl:attribute>
					<xsl:attribute name="type"><xsl:value-of select="./string[2]/."/></xsl:attribute>
				</xsl:element>
			</xsl:for-each>
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<xsl:template name="processNodes">
		<xsl:for-each select="//element[@id != '']">
			<xsl:call-template name="printNodes"><xsl:with-param name="className"><xsl:value-of select = "@class"/></xsl:with-param></xsl:call-template>
		</xsl:for-each>
		
		<!-- Only those from or to nodes that have id. When there is reference it means that the node was already declared as id earlier or would come later.-->
		<xsl:for-each select="//from[@id != '']|//to[@id != '']">
			<xsl:call-template name="printNodes"><xsl:with-param name="className"><xsl:value-of select = "@class"/></xsl:with-param></xsl:call-template>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="printNodes">
		<xsl:param name="className"/>
		<xsl:choose>
			<xsl:when test="$className = 'org.drools.ruleflow.core.impl.RuleSetNodeImpl'">
				<xsl:call-template name="RenderRuleSetNode"/>
			</xsl:when>
			<xsl:when test="$className = 'org.drools.ruleflow.core.impl.ActionNodeImpl'">
				<xsl:call-template name="RenderActionNode"/>
			</xsl:when>
			<xsl:when test="$className = 'org.drools.ruleflow.core.impl.SplitImpl'">
				<xsl:call-template name="RenderSplitNode"/>
			</xsl:when>
			<xsl:when test="$className = 'org.drools.ruleflow.core.impl.JoinImpl'">
				<xsl:call-template name="RenderJoinNode"/>
			</xsl:when>
			<xsl:when test="$className = 'org.drools.ruleflow.core.impl.SubFlowNodeImpl'">
				<xsl:call-template name="RenderSubflowNode"/>
			</xsl:when>
			<xsl:when test="$className = 'org.drools.ruleflow.core.impl.StartNodeImpl'">
				<xsl:call-template name="RenderStartNode"/>
			</xsl:when>
			<xsl:when test="$className = 'org.drools.ruleflow.core.impl.EndNodeImpl'">
				<xsl:call-template name="RenderEndNode"/>
			</xsl:when>
			<xsl:when test="$className = 'org.drools.ruleflow.core.impl.MilestoneNodeImpl'">
				<xsl:call-template name="RenderMilestoneNode"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="RenderSplitNode">
		<xsl:element name="split">
			<xsl:attribute name="type"><xsl:value-of select = "./type"/></xsl:attribute>
			<xsl:call-template name="renderIdAndNameAttribute"/>
			<!-- Add constraints element if XOR or OR type of Split Node i.e. it is not AND node-->
			<xsl:choose>
				<xsl:when test="./type != '1'">
					<xsl:element name="constraints">
						<xsl:for-each select="constraints/entry/org.drools.ruleflow.core.impl.ConstraintImpl">
							<xsl:call-template name="RenderConstraintNode"></xsl:call-template>
						</xsl:for-each>
					</xsl:element>
				</xsl:when>
			</xsl:choose>
		</xsl:element>
	</xsl:template>

	<xsl:template name="RenderConstraintNode">
		<xsl:element name="constraint">
			<xsl:attribute name="toNodeId">
				<xsl:choose>
					<xsl:when test="../org.drools.ruleflow.core.impl.ConnectionImpl[@id != '']">
						<xsl:for-each select = "../org.drools.ruleflow.core.impl.ConnectionImpl/to">
							<xsl:call-template name="printReferenceOrId"/>
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<xsl:variable name="constraintReference"><xsl:value-of select="../org.drools.ruleflow.core.impl.ConnectionImpl/@reference"/></xsl:variable>
						<xsl:for-each select = "//org.drools.ruleflow.core.impl.ConnectionImpl[@id = $constraintReference]/to">
							<xsl:call-template name="printReferenceOrId"/>
						</xsl:for-each>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="toType"><xsl:value-of select = "'DROOLS_DEFAULT'"/></xsl:attribute>
			<xsl:attribute name="priority"><xsl:value-of select = "./priority"/></xsl:attribute>
			<xsl:attribute name="type"><xsl:value-of select = "'rule'"/></xsl:attribute>
			<xsl:attribute name="dialect"><xsl:value-of select = "'mvel'"/></xsl:attribute>
			<xsl:attribute name="name"><xsl:value-of select = "./name"/></xsl:attribute>
			<xsl:value-of select = "./constraint"/>
		</xsl:element>
	</xsl:template>

	<xsl:template name="RenderSubflowNode">
		<xsl:element name="subProcess">
			<xsl:attribute name="processId"><xsl:value-of select = "./processId"/></xsl:attribute>
			<xsl:call-template name="renderIdAndNameAttribute"/>
		</xsl:element>
	</xsl:template>

	<xsl:template name="RenderJoinNode">
		<xsl:element name="join">
			<xsl:attribute name="type"><xsl:value-of select = "./type"/></xsl:attribute>
			<xsl:call-template name="renderIdAndNameAttribute"/>
		</xsl:element>
	</xsl:template>

	<xsl:template name="RenderRuleSetNode">
		<xsl:element name="ruleSet">
			<xsl:attribute name="ruleFlowGroup"><xsl:value-of select = "./ruleFlowGroup"/></xsl:attribute>
			<xsl:call-template name="renderIdAndNameAttribute"/>
		</xsl:element>
	</xsl:template>

	<xsl:template name="RenderActionNode">
		<xsl:element name="actionNode">
			<xsl:call-template name="renderIdAndNameAttribute"/>
			<xsl:element name="action">
			<!-- Hard coding type as expression and dialect as mvel. Not sure how this can be understood in the 4.0 format.+-->
				<xsl:attribute name="type"><xsl:value-of select = "'expression'"/></xsl:attribute>
				<xsl:attribute name="dialect"><xsl:value-of select = "'mvel'"/></xsl:attribute>
				<xsl:value-of select = "./action/consequence"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="RenderStartNode">
		<xsl:element name="start">
			<xsl:call-template name="renderIdAndNameAttribute"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="RenderEndNode">
		<xsl:element name="end">
			<xsl:call-template name="renderIdAndNameAttribute"/>
		</xsl:element>
	</xsl:template>

	<xsl:template name="RenderMilestoneNode">
		<xsl:element name="milestone">
			<xsl:call-template name="renderIdAndNameAttribute"/>
			<xsl:element name="constraint">
				<!-- Hard coded the type and dialect as in 4.0 it was assumed to be mvel. -->
				<xsl:attribute name="type"><xsl:value-of select="'rule'"/></xsl:attribute>
				<xsl:attribute name="dialect"><xsl:value-of select="'mvel'"/></xsl:attribute>
				<xsl:value-of select="./constraint"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template name="renderIdAndNameAttribute">
		<xsl:attribute name="id"><xsl:value-of select = "./@id"/></xsl:attribute>
		<xsl:attribute name="name"><xsl:value-of select = "./name"/></xsl:attribute>
		<xsl:call-template name="renderPosition"><xsl:with-param name="theId"><xsl:value-of select = "./@id"/></xsl:with-param></xsl:call-template>
	</xsl:template>
	
	<xsl:template name="renderPosition2">
		<xsl:param name="theId"/>
		<xsl:attribute name="shahad"><xsl:value-of select = "$theId"/></xsl:attribute>
	</xsl:template>
	
	<xsl:template name="renderPosition">
		<xsl:param name="theId"/>
		<xsl:for-each select="//element[./@id=$theId or ./@reference=$theId]">
				<xsl:apply-templates select="../constraint"/>
		</xsl:for-each>
	</xsl:template>
	
	
	<xsl:template match="constraint">
		<xsl:attribute name="x"><xsl:value-of select = "./x"/></xsl:attribute>
		<xsl:attribute name="y"><xsl:value-of select = "./y"/></xsl:attribute>
		<xsl:attribute name="width"><xsl:value-of select = "./width"/></xsl:attribute>
		<xsl:attribute name="height"><xsl:value-of select = "./height"/></xsl:attribute>
	</xsl:template>
	
	
	<xsl:template name="formConnections">
		<xsl:for-each select="//from">
			<xsl:element name="connection">
				<xsl:attribute name="from"><xsl:apply-templates select="current()"/></xsl:attribute>
				<xsl:attribute name="to"><xsl:apply-templates select="../to"/></xsl:attribute>
				<xsl:call-template name="renderSourceBendpoints"><xsl:with-param name="theId"><xsl:apply-templates select="current()"/></xsl:with-param></xsl:call-template>
				<xsl:call-template name="renderTargetBendpoints"><xsl:with-param name="theId"><xsl:apply-templates select="../to"/></xsl:with-param></xsl:call-template>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="to">
		<xsl:call-template name="printReferenceOrId"/>
	</xsl:template>
	<xsl:template match="from">
		<xsl:call-template name="printReferenceOrId"/>
	</xsl:template>
	<xsl:template name="printReferenceOrId">
		<xsl:for-each select="@*">
			<xsl:choose>
				<xsl:when test="(name() = 'reference') or (name() = 'id')">
					<xsl:value-of select="."/>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="renderSourceBendpoints">
		<xsl:param name="theId"/>
		<xsl:for-each select="//source/org.drools.eclipse.flow.common.editor.core.DefaultElementWrapper/default/element[./@id=$theId or ./@reference=$theId]">
			<xsl:if test="../../../../bendpoints/child::*">
				<xsl:attribute name="bendpoints">
					<xsl:text>[</xsl:text>
					<xsl:for-each select="../../../../bendpoints/child::*">
						<xsl:value-of select="concat(./x/.,',',./y/.)"/>
						<xsl:if test="position() != last()">;</xsl:if>
					</xsl:for-each>
					<xsl:text>]</xsl:text>
				</xsl:attribute>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="renderTargetBendpoints">
		<xsl:param name="theId"/>
		<xsl:for-each select="//target/org.drools.eclipse.flow.common.editor.core.DefaultElementWrapper/default/element[./@id=$theId or ./@reference=$theId]">
			<xsl:if test="../../../../bendpoints/child::*">
				<xsl:attribute name="bendpoints">
					<xsl:text>[</xsl:text>
					<xsl:for-each select="../../../../bendpoints/child::*">
						<xsl:value-of select="concat(./x/.,',',./y/.)"/>
						<xsl:if test="position() != last()">;</xsl:if>
					</xsl:for-each>
					<xsl:text>]</xsl:text>
				</xsl:attribute>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	
</xsl:stylesheet>