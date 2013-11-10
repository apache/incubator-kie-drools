package org.drools.games;

public class GameConfiguration {

    private boolean exitOnClose;

    private int windowWidth;
    private int windowHeight;

    public GameConfiguration(int width, int height) {
        setWindowWidth( 700 );
        setWindowHeight( 500 );
    }

    public GameConfiguration() {
        this(700, 500 );
    }

    public boolean isExitOnClose() {
        return exitOnClose;
    }

    public void setExitOnClose(boolean exitOnClose) {
        this.exitOnClose = exitOnClose;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(int tableWidth) {
        validTableDimension( tableWidth );
        this.windowWidth = tableWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(int tableHeight) {
        validTableDimension( tableHeight );
        this.windowHeight = tableHeight;
    }

    private void validTableDimension(int dimension) {
        if ( dimension % 20 != 0 ) {
            throw new IllegalArgumentException( "Table dimensions must be divisiable by 20" );
        }
    }

}
