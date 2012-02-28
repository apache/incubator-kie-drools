package org.drools.examples.wumpus.view;

import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.examples.wumpus.Cell;
import org.drools.examples.wumpus.Gold;
import org.drools.examples.wumpus.Hero;
import org.drools.examples.wumpus.Pit;
import org.drools.examples.wumpus.Wumpus;
import org.drools.examples.wumpus.WumpusWorldMain;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.runtime.StatefulKnowledgeSession;

public class GameView {
    private WumpusWorldMain        wumpusWorld;
    
    private Wumpus                   wumpus;
    private Gold                     gold;
    private Hero                     hero;
    
    private int                      cellHeight;
    private int                      cellWidth;
    private int                      cellPadding;

    KnowledgeRuntimeLogger           klogger;

    private StatefulKnowledgeSession ksession;
    private KnowledgeBase            kbase;
    private boolean                  showAllCells;
    
    private int                      pittPercentage;
    private int                      rows;
    private int                      cols;    

    public GameView() {

    }

    public KnowledgeBase getKbase() {
        return kbase;
    }

    public void setKbase(KnowledgeBase kbase) {
        this.kbase = kbase;
    }

    
    public void init(Cell[][] cells) {
        
    }
    
    public void init(Wumpus wumpus,
                     Gold gold,
                     Hero hero,
                     int cellHeight,
                     int cellWidth,
                     int cellPadding,
                     int pittPercentage,
                     int rows,
                     int cols) {
        this.showAllCells = false;
        this.wumpus = wumpus;
        this.gold = gold;
        this.hero = hero;
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

    public Wumpus getWumpus() {
        return wumpus;
    }

    public void setWumpus(Wumpus wumpus) {
        this.wumpus = wumpus;
    }

    public Gold getGold() {
        return gold;
    }

    public void setGold(Gold gold) {
        this.gold = gold;
    }

    public Hero getHero() {
        return hero;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
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
