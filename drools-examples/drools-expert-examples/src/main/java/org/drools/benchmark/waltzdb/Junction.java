/*
 * Copyright 2010 JBoss Inc
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

package org.drools.benchmark.waltzdb;
//(literalize junction p1 p2 p3 base_point type name visited)
public class Junction {

    public static String L = "L";
    public Junction() {
        super();
    }

    public String toString() {
        String result = "JUNCTION: P1=" + p1 + ",P2=" + p2 + ",P3=" +
        p3 + ",BasePoint=" + basePoint + ", Type=" + type +
        ",Name=" + name + ",Visited=" + visited;
        return result;
    }
    private int p1;
    private int p2;
    private int p3;
    private int basePoint;
    private String type;
    private String name;
    private String visited;
    public Junction(String type, String name, int basePoint, int p1,int p2, String visited){
        super();
        this.p1 = p1;
        this.p2 = p2;
        this.basePoint = basePoint;
        this.type = type;
        this.name = name;
        this.visited = visited;
    }
    public Junction(int p1, int p2, int p3, int basePoint, String type, String name, String visited) {
        super();
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.basePoint = basePoint;
        this.type = type;
        this.name = name;
        this.visited = visited;
    }
    public Junction(int basePoint, String type, String name, String visited) {
        super();
        this.basePoint = basePoint;
        this.type = type;
        this.name = name;
        this.visited = visited;
    }
    public int getBasePoint() {
        return basePoint;
    }
    public void setBasePoint(int basePoint) {
        this.basePoint = basePoint;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getP1() {
        return p1;
    }
    public void setP1(int p1) {
        this.p1 = p1;
    }
    public int getP2() {
        return p2;
    }
    public void setP2(int p2) {
        this.p2 = p2;
    }
    public int getP3() {
        return p3;
    }
    public void setP3(int p3) {
        this.p3 = p3;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getVisited() {
        return visited;
    }
    public void setVisited(String visited) {
        this.visited = visited;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + basePoint;
        result = PRIME * result + ((name == null) ? 0 : name.hashCode());
        result = PRIME * result + p1;
        result = PRIME * result + p2;
        result = PRIME * result + p3;
        result = PRIME * result + ((type == null) ? 0 : type.hashCode());
        result = PRIME * result + ((visited == null) ? 0 : visited.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Junction other = (Junction) obj;
        if (basePoint != other.basePoint)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (p1 != other.p1)
            return false;
        if (p2 != other.p2)
            return false;
        if (p3 != other.p3)
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (visited == null) {
            if (other.visited != null)
                return false;
        } else if (!visited.equals(other.visited))
            return false;
        return true;
    }
}
