package org.drools.games.wumpus.view;

import org.drools.games.wumpus.Cell;
import org.drools.games.wumpus.WumpusWorldMain;
import org.kie.internal.KnowledgeBase;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.internal.logger.KnowledgeRuntimeLogger;
import org.kie.internal.runtime.StatefulKnowledgeSession;

@PropertyReactive
public class GameView {
    private WumpusWorldMain          wumpusWorld;

    private int                      cellHeight;
    private int                      cellWidth;
    private int                      cellPadding;

    KnowledgeRuntimeLogger           klogger;

    private StatefulKnowledgeSession ksession;
    private boolean                  showAllCells;

    private int                      pittPercentage;
    private int                      rows;
    private int                      cols;

    public GameView() {

    }

    public void init(Cell[][] cells) {

    }

    public void init(int cellHeight,
                     int cellWidth,
                     int cellPadding,
                     int pittPercentage,
                     int rows,
                     int cols) {
        this.showAllCells = false;
        this.cellHeight = cellHeight;
        this.cellWidth = cellWidth;
        this.cellPadding = cellPadding;
        this.pittPercentage = pittPercentage;
        this.rows = rows;
        this.cols = cols;
    }

    public WumpusWorldMain getWumpusWorld() {
        return wumpusWorld;
    }

    public void setWumpusWorld(WumpusWorldMain wumpusWorld) {
        this.wumpusWorld = wumpusWorld;
    }

    public StatefulKnowledgeSession getKsession() {
        return ksession;
    }

    public void setKsession(StatefulKnowledgeSession ksession) {
        this.ksession = ksession;
    }

    public boolean isShowAllCells() {
        return showAllCells;
    }

    public void setShowAllCells(boolean showAllCells) {
        this.showAllCells = showAllCells;
    }

    public KnowledgeRuntimeLogger getKlogger() {
        return klogger;
    }

    public void setKlogger(KnowledgeRuntimeLogger klogger) {
        this.klogger = klogger;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public void setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public void setCellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
    }

    public int getCellPadding() {
        return cellPadding;
    }

    public void setCellPadding(int cellPadding) {
        this.cellPadding = cellPadding;
    }

    public int getPittPercentage() {
        return pittPercentage;
    }

    public void setPittPercentage(int pittPercentage) {
        this.pittPercentage = pittPercentage;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

}
