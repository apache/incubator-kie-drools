package org.benchmarks.waltz;
import jess.*;

public class Make3Junction implements Userfunction {
    private static final int MOD_NUM = 10000;
    private int p1_offset, p2_offset, p3_offset;
    private int type_off, bp_off;
    private String tee = "tee", fork = "fork", arrow = "arrow";
    
    Make3Junction() {        
	p1_offset = 2 ;
	p2_offset = 3 ;
	p3_offset = 4 ;
	bp_off = 5 ;
	type_off = 6 ;
    }

    private int get_y(int val) {
        return val % MOD_NUM;
    }
    private int get_x(int val) {
        return (int) (val / MOD_NUM);
    }

    /**********************************************************************
     * This function is passed two points and calculates the angle between the
     * line defined by these points and the x-axis.
     **********************************************************************/
    private double get_angle(int p1, int p2) {
        /* Calculate (x2 - x1) and (y2 - y1).  The points are passed in the
         * form x1y1 and x2y2.  get_x() and get_y() are passed these points
         * and return the x and y values respectively.  For example,
         * get_x(1020) returns 10. */
        int delta_x = get_x(p2) - get_x(p1);
        int delta_y = get_y(p2) - get_y(p1);
        
        if (delta_x == 0) {
            if (delta_y > 0)
                return(Math.PI/2);
            else if (delta_y < 0)
                return(-Math.PI/2);
        }
        else if (delta_y == 0) {
            if (delta_x > 0)
                return(0.0);
            else if (delta_x < 0)
                return(Math.PI);
        }
        
        return(Math.atan2((double) delta_y, (double) delta_x));
    }
    
    /**********************************************************************
     * This procedure is passed the basepoint of the intersection of two lines
     * as well as the other two endpoints of the lines and calculates the
     * angle inscribed by these three points.
     **********************************************************************/
    private double inscribed_angle(int basepoint, int p1, int p2) {

	/* Get the angle between line #1 and the origin and the angle
	 * between line #2 and the origin, and then subtract these values. */
	double angle1 = get_angle(basepoint,p1);
	double angle2 = get_angle(basepoint,p2);
	double temp = angle1 - angle2;
	if (temp < 0.0)
            temp = -temp;

	/* We always want the smaller of the two angles inscribed, so if the
	 * answer is greater than 180 degrees, calculate the smaller angle and
	 * return it. */
	if (temp > Math.PI)
            temp = 2*Math.PI - temp;
	if (temp < 0.0)
            return(-temp);
	return(temp);
    }
    

    void make_3_junction(int basepoint, int p1, int p2, int p3, Rete engine)
        throws JessException {

	int shaft,barb1,barb2;
        double sum = 0;
	double delta = 0;

	double angle12 = inscribed_angle(basepoint,p1,p2);
	double angle13 = inscribed_angle(basepoint,p1,p3);
	double angle23 = inscribed_angle(basepoint,p2,p3);

	double sum1213 = angle12 + angle13;
	double sum1223 = angle12 + angle23;
	double sum1323 = angle13 + angle23;

	if (sum1213 < sum1223) {
		if (sum1213 < sum1323) {
			sum = sum1213;
			shaft = p1; barb1 = p2; barb2 = p3;
		}
		else {
			sum = sum1323;
			shaft = p3; barb1 = p1; barb2 = p2;
		}
	}
	else {
		if (sum1223 < sum1323) {
			sum = sum1223;
			shaft = p2; barb1 = p1; barb2 = p3;
		}
		else {
			sum = sum1323;
			shaft = p3; barb1 = p1; barb2 = p2;
		}
	}

	delta = sum - Math.PI;
	if (delta < 0.0)
            delta = -delta;
        // This is apparently defining a fact!
        Fact f = new Fact("junction", engine);
        f.setSlotValue("p1", new Value(barb1, RU.INTEGER));
        f.setSlotValue("p2", new Value(shaft, RU.INTEGER));
        f.setSlotValue("p3", new Value(barb2, RU.INTEGER));
        f.setSlotValue("base_point", new Value(basepoint, RU.INTEGER));
	if (delta < 0.001)
            f.setSlotValue("type", new Value("tee", RU.ATOM));
	else if (sum > Math.PI)
            f.setSlotValue("type", new Value("fork", RU.ATOM));
	else
            f.setSlotValue("type", new Value("arrow", RU.ATOM));

        engine.assertFact(f);
    }



    public String getName() {
        return "make_3_junction";
    }

    public Value call(ValueVector vv, Context c) throws JessException {
        double p1 = vv.get(1).floatValue(c);
        double p2 = vv.get(2).floatValue(c);
        double p3 = vv.get(3).floatValue(c);
        double basepoint = vv.get(4).intValue(c);
        make_3_junction((int) p1, (int) p2, (int) p3, (int) basepoint, c.getEngine());
        return Funcall.NIL;
    }
}
