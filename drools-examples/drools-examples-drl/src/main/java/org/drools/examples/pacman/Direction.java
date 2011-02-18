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

package org.drools.examples.pacman;

public class Direction {
    public static final int NONE = 0;
    public static final int LEFT  = -1;
    public static final int RIGHT = 1;
    public static final int UP    = 1;
    public static final int DOWN  = -1;

    private Character       character;
    private int             horizontal;
    private int             vertical;

    public Direction(Character character,
                     int horizontal,
                     int vertical) {
        this.character = character;
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public Character getCharacter() {
        return character;
    }

    public int getHorizontal() {
        return horizontal;
    }

    public int getVertical() {
        return vertical;
    }

    public static Direction newDirection(Character character,
                                         DirectionEnum directionEnum) {
        switch ( directionEnum ) {
            case LEFT : {
                return new Direction( character,
                                      -1,
                                      0 );
            }
            case RIGHT : {
                return new Direction( character,
                                      1,
                                      0 );
            }
            case UP : {
                return new Direction( character,
                                      0,
                                      1 );
            }
            case DOWN : {
                return new Direction( character,
                                      0,
                                      -1 );
            }
            default : {

            }
        }
        throw new IllegalArgumentException( "Direction must be a valid DirectionEnum" );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((character == null) ? 0 : character.hashCode());
        result = prime * result + horizontal;
        result = prime * result + vertical;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Direction other = (Direction) obj;
        if ( character == null ) {
            if ( other.character != null ) return false;
        } else if ( !character.equals( other.character ) ) return false;
        if ( horizontal != other.horizontal ) return false;
        if ( vertical != other.vertical ) return false;
        return true;
    }

    @Override
    public String toString() {
        if ( horizontal != 0 ) {
            return "Direction " + character + " " + ((horizontal == LEFT) ? "LEFT" : "RIGHT");
        } else {
            return "Direction " + character + " " + ((vertical == UP) ? "UP" : "DOWN");
        }

    }

}
