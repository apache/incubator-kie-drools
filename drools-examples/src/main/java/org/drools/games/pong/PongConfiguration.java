package org.drools.games.pong;

public class PongConfiguration {
    private int tableWidth;
    private int tableHeight;
    private int padding;
    
    private int sideLineWidth;

    private int netWidth;
    private int netDash;
    private int netGap;
    
    private int ballWidth;
    private int ballStartingSpeed;
    
    private int batWidth;
    private int batHeight;
    private int batSpeed;

    public PongConfiguration() {
        setTableWidth( 700 );
        setTableHeight( 500 );
        setSideLineWidth( 10 );
        
        setBallWidth( 20 );
        setBallStartingSpeed( 60 );
        
        setBatWidth( 5 );
        setBatHeight( 80 );
        setBatSpeed( 15 );
        setPadding( 10 );
        setNet( 2, 20, 10 );
    }

    public int getTableWidth() {
        return tableWidth;
    }

    public void setTableWidth(int tableWidth) {
        validTableDimension( tableWidth );
        this.tableWidth = tableWidth;
    }

    public int getTableHeight() {
        return tableHeight;
    }

    public void setTableHeight(int tableHeight) {
        validTableDimension( tableHeight );
        this.tableHeight = tableHeight;
    }

    private void validTableDimension(int dimension) {
        if ( dimension % 20 != 0 ) {
            throw new IllegalArgumentException( "Table dimensions must be divisiable by 20" );
        }
    }

    public int getSideLineWidth() {
        return sideLineWidth;
    }

    public void setSideLineWidth(int sideLineWidth) {
        this.sideLineWidth = sideLineWidth;
    }    
    
    public int getBallWidth() {
        return ballWidth;
    }

    public void setBallWidth(int ballWidth) {
        this.ballWidth = ballWidth;
    }

    public int getBallStartingSpeed() {
        return ballStartingSpeed;
    }

    public void setBallStartingSpeed(int ballStartingSpeed) {
        this.ballStartingSpeed = ballStartingSpeed;
    }

    public int getBatWidth() {
        return batWidth;
    }

    public void setBatWidth(int batWidth) {
        this.batWidth = batWidth;
    }

    public int getBatHeight() {
        return batHeight;
    }

    public void setBatHeight(int batHeight) {
        this.batHeight = batHeight;
    }
    
    public int getBatSpeed() {
        return batSpeed;
    }

    public void setBatSpeed(int batSpeed) {
        this.batSpeed = batSpeed;
    }    

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public void setNet(int width,
                       int dash,
                       int gap) {
        if ( width % 2 != 0 ) {
            throw new IllegalArgumentException( "Fence Width must be divisiable by 2" );
        }
        this.netWidth = width;

        this.netDash = dash;
        this.netGap = gap;
    }

    public int getNetWidth() {
        return netWidth;
    }

    public int getNetDash() {
        return netDash;
    }

    public int getNetGap() {
        return netGap;
    }



}
