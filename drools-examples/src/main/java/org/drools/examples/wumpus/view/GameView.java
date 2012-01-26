package org.drools.examples.wumpus.view;

import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.examples.wumpus.Cell;
import org.drools.examples.wumpus.Gold;
import org.drools.examples.wumpus.Hero;
import org.drools.examples.wumpus.Pitt;
import org.drools.examples.wumpus.Wumpus;
import org.drools.examples.wumpus.WumpusWorldServer;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.runtime.StatefulKnowledgeSession;

public class GameView {
    private WumpusWorldServer        wumpusWorld;
    private Cell[][]                 cells;
    private List<Pitt>               pits;
    private Wumpus                   wumpus;
    private Gold                     gold;
    private Hero                     hero;
    private SensorsView              sensors;

    private boolean                  pittDeath;
    private boolean                  wumpusDeath;
    private boolean                  goldWin;

    KnowledgeRuntimeLogger           klogger;

    private StatefulKnowledgeSession ksession;
    private KnowledgeBase            kbase;
    private boolean                  showAllCells;

    public GameView() {

    }

    public KnowledgeBase getKbase() {
        return kbase;
    }

    public void setKbase(KnowledgeBase kbase) {
        this.kbase = kbase;
    }

    public void init(Cell[][] cells,
                     SensorsView sensors,
                     List<Pitt> pits,
                     Wumpus wumpus,
                     Gold gold,
                     Hero hero) {
        this.showAllCells = false;
        this.pittDeath = false;
        this.goldWin = false;
        this.wumpusDeath = false;
        this.cells = cells;
        this.sensors = sensors;
        this.pits = pits;
        this.wumpus = wumpus;
        this.gold = gold;
        this.hero = hero;
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

    public List<Pitt> getPits() {
        return pits;
    }

    public void setPits(List<Pitt> pits) {
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

    public boolean isPittDeath() {
        return pittDeath;
    }

    public void setPittDeath(boolean pittDeath) {
        this.pittDeath = pittDeath;
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

}
