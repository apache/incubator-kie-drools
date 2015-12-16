/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.dtable.shared.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.workbench.models.datamodel.auditlog.AuditLog;
import org.drools.workbench.models.datamodel.imports.HasImports;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.packages.HasPackageName;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.guided.dtable.shared.auditlog.DecisionTableAuditLogFilter;
import org.drools.workbench.models.guided.dtable.shared.model.adaptors.FactPatternPattern52Adaptor;

/**
 * This is a decision table model for a guided editor. It is not template or XLS
 * based. (template could be done relatively easily by taking a template, as a
 * String, and then String[][] data and driving the SheetListener interface in
 * the decision tables module). This works by taking the column definitions, and
 * combining them with the table of data to produce rule models.
 */
public class GuidedDecisionTable52 implements HasImports,
                                              HasPackageName {

    private static final long serialVersionUID = 510l;

    /**
     * Number of internal elements before ( used for offsets in serialization )
     */
    public static final int INTERNAL_ELEMENTS = 2;

    /**
     * Various attribute names
     */
    public static final String SALIENCE_ATTR = "salience";
    public static final String ENABLED_ATTR = "enabled";
    public static final String DATE_EFFECTIVE_ATTR = "date-effective";
    public static final String DATE_EXPIRES_ATTR = "date-expires";
    public static final String NO_LOOP_ATTR = "no-loop";
    public static final String AGENDA_GROUP_ATTR = "agenda-group";
    public static final String ACTIVATION_GROUP_ATTR = "activation-group";
    public static final String DURATION_ATTR = "duration";
    public static final String TIMER_ATTR = "timer";
    public static final String CALENDARS_ATTR = "calendars";
    public static final String AUTO_FOCUS_ATTR = "auto-focus";
    public static final String LOCK_ON_ACTIVE_ATTR = "lock-on-active";
    public static final String RULEFLOW_GROUP_ATTR = "ruleflow-group";
    public static final String DIALECT_ATTR = "dialect";
    public static final String NEGATE_RULE_ATTR = "negate";

    private String tableName;

    private String parentName;

    private RowNumberCol52 rowNumberCol = new RowNumberCol52();

    private DescriptionCol52 descriptionCol = new DescriptionCol52();

    private List<MetadataCol52> metadataCols = new ArrayList<MetadataCol52>();

    private List<AttributeCol52> attributeCols = new ArrayList<AttributeCol52>();

    private List<CompositeColumn<? extends BaseColumn>> conditionPatterns = new ArrayList<CompositeColumn<? extends BaseColumn>>();

    private List<ActionCol52> actionCols = new ArrayList<ActionCol52>();

    private AuditLog auditLog;

    private Imports imports = new Imports();

    private String packageName;

    public enum TableFormat {
        EXTENDED_ENTRY,
        LIMITED_ENTRY
    }

    private TableFormat tableFormat = TableFormat.EXTENDED_ENTRY;

    /**
     * First column is always row number. Second column is description.
     * Subsequent ones follow the above column definitions: attributeCols, then
     * conditionCols, then actionCols, in that order, left to right.
     */
    private List<List<DTCellValue52>> data = new ArrayList<List<DTCellValue52>>();

    public List<ActionCol52> getActionCols() {
        return actionCols;
    }

    public List<AttributeCol52> getAttributeCols() {
        return attributeCols;
    }

    /**
     * Return an immutable list of Pattern columns
     * @return
     */
    public List<Pattern52> getPatterns() {
        final List<Pattern52> patterns = new ArrayList<Pattern52>();
        for ( CompositeColumn<?> cc : conditionPatterns ) {
            if ( cc instanceof Pattern52 ) {
                patterns.add( (Pattern52) cc );
            }
        }
        return Collections.unmodifiableList( patterns );
    }

    public List<CompositeColumn<? extends BaseColumn>> getConditions() {
        return this.conditionPatterns;
    }

    public Pattern52 getConditionPattern( final String boundName ) {
        for ( CompositeColumn<?> cc : conditionPatterns ) {
            if ( cc instanceof Pattern52 ) {
                final Pattern52 p = (Pattern52) cc;
                if ( p.isBound() && p.getBoundName().equals( boundName ) ) {
                    return p;
                }
            } else if ( cc instanceof BRLConditionColumn ) {
                final BRLConditionColumn brlConditionColumn = (BRLConditionColumn) cc;
                for ( IPattern p : brlConditionColumn.getDefinition() ) {
                    if ( p instanceof FactPattern ) {
                        final FactPattern fp = (FactPattern) p;
                        if ( fp.isBound() && fp.getBoundName().equals( boundName ) ) {
                            return new FactPatternPattern52Adaptor( fp );
                        }
                    }
                }
            }
        }
        return null;
    }

    public Pattern52 getPattern( final ConditionCol52 col ) {
        for ( CompositeColumn<?> cc : conditionPatterns ) {
            if ( cc instanceof Pattern52 ) {
                final Pattern52 p = (Pattern52) cc;
                if ( p.getChildColumns().contains( col ) ) {
                    return p;
                }
            }
        }
        return new Pattern52();
    }

    public BRLColumn<?, ?> getBRLColumn( final BRLVariableColumn col ) {
        for ( CompositeColumn<? extends BaseColumn> cc : conditionPatterns ) {
            if ( cc instanceof BRLConditionColumn ) {
                final BRLConditionColumn brl = (BRLConditionColumn) cc;
                if ( brl.getChildColumns().contains( col ) ) {
                    return brl;
                }
            }
        }
        for ( ActionCol52 ac : actionCols ) {
            if ( ac instanceof BRLActionColumn ) {
                final BRLActionColumn brl = (BRLActionColumn) ac;
                if ( brl.getChildColumns().contains( col ) ) {
                    return brl;
                }
            }
        }
        throw new IllegalStateException( "col is not a child of any of the defined BRLColumns." );
    }

    public BRLConditionColumn getBRLColumn( BRLConditionVariableColumn col ) {
        for ( CompositeColumn<? extends BaseColumn> cc : conditionPatterns ) {
            if ( cc instanceof BRLConditionColumn ) {
                final BRLConditionColumn brl = (BRLConditionColumn) cc;
                if ( brl.getChildColumns().contains( col ) ) {
                    return brl;
                }
            }
        }
        throw new IllegalStateException( "col is not a child of any of the defined BRLColumns." );
    }

    public BRLActionColumn getBRLColumn( BRLActionVariableColumn col ) {
        for ( ActionCol52 ac : actionCols ) {
            if ( ac instanceof BRLActionColumn ) {
                final BRLActionColumn brl = (BRLActionColumn) ac;
                if ( brl.getChildColumns().contains( col ) ) {
                    return brl;
                }
            }
        }
        throw new IllegalStateException( "col is not a child of any of the defined BRLColumns." );
    }

    public long getConditionsCount() {
        long size = 0;
        for ( CompositeColumn<?> cc : this.conditionPatterns ) {
            size = size + cc.getChildColumns().size();
        }
        return size;
    }

    public List<List<DTCellValue52>> getData() {
        return data;
    }

    /**
     * This method expands Composite columns into individual columns where
     * knowledge of individual columns is necessary; for example separate
     * columns in the user-interface or where individual columns need to be
     * analysed.
     * @return A List of individual columns
     */
    public List<BaseColumn> getExpandedColumns() {
        final List<BaseColumn> columns = new ArrayList<BaseColumn>();
        columns.add( rowNumberCol );
        columns.add( descriptionCol );
        columns.addAll( metadataCols );
        columns.addAll( attributeCols );
        for ( CompositeColumn<?> cc : this.conditionPatterns ) {
            boolean explode = !( cc instanceof LimitedEntryCol );
            if ( explode ) {
                for ( BaseColumn bc : cc.getChildColumns() ) {
                    columns.add( bc );
                }
            } else {
                columns.add( cc );
            }
        }
        for ( ActionCol52 ac : this.actionCols ) {
            if ( ac instanceof BRLActionColumn ) {
                if ( ac instanceof LimitedEntryCol ) {
                    columns.add( ac );
                } else {
                    final BRLActionColumn bac = (BRLActionColumn) ac;
                    for ( BRLActionVariableColumn variable : bac.getChildColumns() ) {
                        columns.add( variable );
                    }
                }

            } else {
                columns.add( ac );
            }
        }
        return columns;
    }

    public DescriptionCol52 getDescriptionCol() {
        // De-serialising old models sets this field to null
        if ( this.descriptionCol == null ) {
            this.descriptionCol = new DescriptionCol52();
        }
        return this.descriptionCol;
    }

    public List<MetadataCol52> getMetadataCols() {
        if ( null == metadataCols ) {
            metadataCols = new ArrayList<MetadataCol52>();
        }
        return metadataCols;
    }

    public String getParentName() {
        return parentName;
    }

    public RowNumberCol52 getRowNumberCol() {
        // De-serialising old models sets this field to null
        if ( this.rowNumberCol == null ) {
            this.rowNumberCol = new RowNumberCol52();
        }
        return this.rowNumberCol;
    }

    public String getTableName() {
        return tableName;
    }

    public void setData( final List<List<DTCellValue52>> data ) {
        this.data = data;
    }

    public void setRowNumberCol( final RowNumberCol52 rowNumberCol ) {
        this.rowNumberCol = rowNumberCol;
    }

    public void setDescriptionCol( final DescriptionCol52 descriptionCol ) {
        this.descriptionCol = descriptionCol;
    }

    public void setMetadataCols( final List<MetadataCol52> metadataCols ) {
        this.metadataCols = metadataCols;
    }

    public void setAttributeCols( final List<AttributeCol52> attributeCols ) {
        this.attributeCols = attributeCols;
    }

    public void setConditionPatterns( final List<CompositeColumn<? extends BaseColumn>> conditionPatterns ) {
        this.conditionPatterns = conditionPatterns;
    }

    public void setActionCols( final List<ActionCol52> actionCols ) {
        this.actionCols = actionCols;
    }

    public void setParentName( final String parentName ) {
        this.parentName = parentName;
    }

    public void setTableName( final String tableName ) {
        this.tableName = tableName;
    }

    public TableFormat getTableFormat() {
        //GUVNOR-1820: Not possible to give default value to action columns
        return tableFormat == null ? TableFormat.EXTENDED_ENTRY : tableFormat;
    }

    public void setTableFormat( final TableFormat tableFormat ) {
        this.tableFormat = tableFormat;
    }

    /**
     * Retrieve, or lazily instantiate a new, AuditLog.
     * @return
     */
    public AuditLog getAuditLog() {
        if ( this.auditLog == null ) {
            this.auditLog = new AuditLog( new DecisionTableAuditLogFilter() );
        }
        return this.auditLog;
    }

    public Imports getImports() {
        return imports;
    }

    @Override
    public void setImports( final Imports imports ) {
        this.imports = imports;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName( String packageName ) {
        this.packageName = packageName;
    }

}
