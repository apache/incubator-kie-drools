package org.drools.examples.pacman;

public class Direction {
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
    
    public static Direction newDirection(Character character, DirectionEnum directionEnum) {
        switch(directionEnum) {
            case LEFT: {
                return new Direction(character, -1, 0);
            }
            case RIGHT: {
                return new Direction(character, 1, 0);
            }
            case UP: {
                return new Direction(character, 0, 1);
            }
            case DOWN: {
                return new Direction(character, 0, -1);
            }
            default: {
                
            }
        }
        throw new IllegalArgumentException( "Direction must be a valid DirectionEnum" );
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
