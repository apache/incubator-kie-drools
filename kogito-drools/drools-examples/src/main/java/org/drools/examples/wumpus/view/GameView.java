package org.drools.examples.wumpus.view;

import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.examples.wumpus.Cell;
import org.drools.examples.wumpus.Gold;
import org.drools.examples.wumpus.Hero;
import org.drools.examples.wumpus.Pit;
import org.drools.examples.wumpus.Wumpus;
import org.drools.examples.wumpus.WumpusWorldServer;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.runtime.StatefulKnowledgeSession;

public class GameView {
    private WumpusWorldServer        wumpusWorld;
    private Cell[][]                 cells;
    private List<Pit>               pits;
    private Wumpus                   wumpus;
    private Gold                     gold;
    private Hero                     hero;
    private SensorsView              sensors;

    private boolean                  pitDeath;
    private boolean                  wumpusDeath;
    private boolean                  goldWin;
    
    private int                      cellheight;
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
    
    public void init(Cell[][] cells,
                     SensorsView sensors,
                     List<Pit> pits,
                     Wumpus wumpus,
                     Gold gold,
                     Hero hero,
                     int cellheight,
                     int cellWidth,
                     int cellPadding,
                     int pittPercentage,
                     int rows,
                     int cols) {
        this.showAllCells = false;
        this.pitDeath = false;
        this.goldWin = false;
        this.wumpusDeath = false;
        this.cells = cells;
        this.sensors = sensors;
        this.pits = pits;
        this.wumpus = wumpus;
        this.gold = gold;
        this.hero = hero;
        this.cellheight = cellheight;
        this.cellWidth = cellWidth;
        this.cellPadding = cellPadding;
        this.pittPercentage = pittPercentage;
        this.rows = rows;
        this.cols = cols;
    }

    public WumpusWorldServer getWumpusWorld() {
        return wumpusWorld;
    }

    public void setWumpusWorld(WumpusWorldServer wumpusWorld) {
        this.wumpusWorld = wumpusWorld;
    }

    public StatefulKnowledgeSession getKsession() {
        return ksession;
    }

    public void setKsession(StatefulKnowledgeSession ksession) {
        this.ksession = ksession;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    public List<Pit> getPits() {
        return pits;
    }

    public void setPits(List<Pit> pits) {
        this.pits = pits;
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

    public SensorsView getSensorsview() {
        return sensors;
    }

    public void setSensorsView(SensorsView sensors) {
        this.sensors = sensors;
    }

    public boolean isShowAllCells() {
        return showAllCells;
    }

    public void setShowAllCells(boolean showAllCells) {
        this.showAllCells = showAllCells;
    }

    public boolean isPitDeath() {
        return pitDeath;
    }

    public void setPitDeath(boolean pitDeath) {
        this.pitDeath = pitDeath;
    }

    public boolean isWumpusDeath() {
        return wumpusDeath;
    }

    public void setWumpusDeath(boolean wumpusDeath) {
        this.wumpusDeath = wumpusDeath;
    }

    public boolean isGoldWin() {
        return goldWin;
    }

    public void setGoldWin(boolean goldWin) {
        this.goldWin = goldWin;
    }

    public KnowledgeRuntimeLogger getKlogger() {
        return klogger;
    }

    public void setKlogger(KnowledgeRuntimeLogger klogger) {
        this.klogger = klogger;
    }

    public SensorsView getSensors() {
        return sensors;
    }

    public void setSensors(SensorsView sensors) {
        this.sensors = sensors;
    }

    public int getCellheight() {
        return cellheight;
    }

    public void setCellheight(int cellheight) {
        this.cellheight = cellheight;
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
