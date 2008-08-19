package org.drools.persistence;

import javax.transaction.xa.Xid;

public class DroolsXid
    implements
    Xid {
    protected int  formatId;
    protected byte gtrid[];
    protected byte bqual[];

    public DroolsXid() {
    }

    public DroolsXid(int formatId,
                     byte gtrid[],
                     byte bqual[]) {
        this.formatId = formatId;
        this.gtrid = gtrid;
        this.bqual = bqual;
    }

    public int getFormatId() {
        return formatId;
    }

    public byte[] getBranchQualifier() {
        return bqual;
    }

    public byte[] getGlobalTransactionId() {
        return gtrid;
    }

    public String toString() {
        int hexVal;
        StringBuffer sb = new StringBuffer( 512 );
        sb.append( "formatId=" + formatId );
        sb.append( " gtrid(" + gtrid.length + ")={0x" );
        for ( int i = 0; i < gtrid.length; i++ ) {
            hexVal = gtrid[i] & 0xFF;
            if ( hexVal < 0x10 ) {
                sb.append( "0" + Integer.toHexString( gtrid[i] & 0xFF ) );
            } else {
                sb.append( Integer.toHexString( gtrid[i] & 0xFF ) );
            }
        }

        sb.append( "} bqual(" + bqual.length + ")={0x" );
        for ( int i = 0; i < bqual.length; i++ ) {
            hexVal = bqual[i] & 0xFF;
            if ( hexVal < 0x10 ) {
                sb.append( "0" + Integer.toHexString( bqual[i] & 0xFF ) );
            } else {
                sb.append( Integer.toHexString( bqual[i] & 0xFF ) );
            }
        }

        sb.append( "}" );
        return sb.toString();
    }
}
