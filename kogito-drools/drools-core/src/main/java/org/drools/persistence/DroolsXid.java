package org.drools.persistence;

import javax.transaction.xa.*;

    
    public class DroolsXid implements Xid
    {
        protected int formatId;
        protected byte gtrid[];
        protected byte bqual[];

        public DroolsXid()
        {
        }

        public DroolsXid(int formatId, byte gtrid[], byte bqual[])
        {
            this.formatId = formatId;
            this.gtrid = gtrid;
            this.bqual = bqual;
        }


        public int getFormatId()
        {
            return formatId;
        }

        public byte[] getBranchQualifier()
        {
            return bqual;
        }

        public byte[] getGlobalTransactionId()
        {
            return gtrid;
        }

    }
