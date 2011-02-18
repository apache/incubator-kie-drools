/*
 * Copyright 2005 JBoss Inc
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

package org.drools.integrationtests.waltz;

public class WaltzUtil {
    private static double    PI      = 3.1415927;

    private static final int MOD_NUM = 10000;

    private static int get_y(final int val) {
        return val % WaltzUtil.MOD_NUM;
    }

    private static int get_x(final int val) {
        return (val / WaltzUtil.MOD_NUM);
    }

    /***************************************************************************
     * This function is passed two points and calculates the angle between the
     * line defined by these points and the x-axis.
     **************************************************************************/
    private static double get_angle(final int p1,
                                    final int p2) {
        int delta_x, delta_y;
        double ret = 0.0;
        /*
         * Calculate (x2 - x1) and (y2 - y1). The points are passed in the form
         * x1y1 and x2y2. get_x() and get_y() are passed these points and return
         * the x and y values respectively. For example, get_x(1020) returns 10.
         */
        delta_x = get_x( p2 ) - get_x( p1 );
        delta_y = get_y( p2 ) - get_y( p1 );

        if ( delta_x == 0 ) {
            if ( delta_y > 0 ) {
                ret = WaltzUtil.PI / 2;
            } else if ( delta_y < 0 ) {
                ret = -WaltzUtil.PI / 2;
            }
        } else if ( delta_y == 0 ) {
            if ( delta_x > 0 ) {
                ret = 0.0;
            } else if ( delta_x < 0 ) {
                ret = WaltzUtil.PI;
            }
        } else {
            ret = Math.atan2( delta_y,
                              delta_x );
        }
        return ret;
    }

    /***************************************************************************
     * This procedure is passed the basepoint of the intersection of two lines
     * as well as the other two endpoints of the lines and calculates the angle
     * inscribed by these three points.
     **************************************************************************/
    private static double inscribed_angle(final int basepoint,
                                          final int p1,
                                          final int p2) {
        double angle1, angle2, temp;

        /*
         * Get the angle between line #1 and the origin and the angle between
         * line #2 and the origin, and then subtract these values.
         */
        angle1 = get_angle( basepoint,
                            p1 );
        angle2 = get_angle( basepoint,
                            p2 );
        temp = angle1 - angle2;
        if ( temp < 0.0 ) {
            temp = -temp;
        }

        /*
         * We always want the smaller of the two angles inscribed, so if the
         * answer is greater than 180 degrees, calculate the smaller angle and
         * return it.
         */
        if ( temp > WaltzUtil.PI ) {
            temp = 2 * WaltzUtil.PI - temp;
        }
        if ( temp < 0.0 ) {
            return (-temp);
        }
        return (temp);
    }

    public static Junction make_3_junction(final int basepoint,
                                           final int p1,
                                           final int p2,
                                           final int p3) {
        int shaft, barb1, barb2;
        double angle12, angle13, angle23;
        double sum, sum1213, sum1223, sum1323;
        double delta;
        String j_type;

        angle12 = inscribed_angle( basepoint,
                                   p1,
                                   p2 );
        angle13 = inscribed_angle( basepoint,
                                   p1,
                                   p3 );
        angle23 = inscribed_angle( basepoint,
                                   p2,
                                   p3 );

        sum1213 = angle12 + angle13;
        sum1223 = angle12 + angle23;
        sum1323 = angle13 + angle23;

        if ( sum1213 < sum1223 ) {
            if ( sum1213 < sum1323 ) {
                sum = sum1213;
                shaft = p1;
                barb1 = p2;
                barb2 = p3;
            } else {
                sum = sum1323;
                shaft = p3;
                barb1 = p1;
                barb2 = p2;
            }
        } else {
            if ( sum1223 < sum1323 ) {
                sum = sum1223;
                shaft = p2;
                barb1 = p1;
                barb2 = p3;
            } else {
                sum = sum1323;
                shaft = p3;
                barb1 = p1;
                barb2 = p2;
            }
        }

        delta = sum - WaltzUtil.PI;
        if ( delta < 0.0 ) {
            delta = -delta;
        }

        if ( delta < 0.001 ) {
            j_type = Junction.TEE;
        } else if ( sum > WaltzUtil.PI ) {
            j_type = Junction.FORK;
        } else {
            j_type = Junction.ARROW;
        }

        return new Junction( barb1,
                             shaft,
                             barb2,
                             basepoint,
                             j_type );

    }
}
