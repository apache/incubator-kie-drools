/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.planner.examples.traindesign.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.examples.common.domain.AbstractPersistable;
import org.drools.planner.examples.traindesign.domain.solver.RailNodeShortestPath;

/**
 * A CrewSegment can be used multiple times on different trains.
 */
@XStreamAlias("CrewSegment")
public class CrewSegment extends AbstractPersistable {

    private RailNode home;
    private RailNode away;

    @XStreamOmitField
    private RailNodeShortestPath homeAwayShortestPath;
    @XStreamOmitField
    private RailNodeShortestPath awayHomeShortestPath;

    public RailNode getHome() {
        return home;
    }

    public void setHome(RailNode home) {
        this.home = home;
    }

    public RailNode getAway() {
        return away;
    }

    public void setAway(RailNode away) {
        this.away = away;
    }

    public RailNodeShortestPath getHomeAwayShortestPath() {
        return homeAwayShortestPath;
    }

    public void setHomeAwayShortestPath(RailNodeShortestPath homeAwayShortestPath) {
        this.homeAwayShortestPath = homeAwayShortestPath;
    }

    public RailNodeShortestPath getAwayHomeShortestPath() {
        return awayHomeShortestPath;
    }

    public void setAwayHomeShortestPath(RailNodeShortestPath awayHomeShortestPath) {
        this.awayHomeShortestPath = awayHomeShortestPath;
    }

    @Override
    public String toString() {
        return home + "->" + away;
    }

    public void initializeShortestPath() {
        homeAwayShortestPath = home.getShortestPathTo(away);
        awayHomeShortestPath = away.getShortestPathTo(home);
    }

}
