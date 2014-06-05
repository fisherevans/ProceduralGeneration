package com.fisherevans.proc_gen;

/**
 * Author: Fisher Evans
 * Date: 5/27/14
 */
public class MathUtil {
    public static short clamp(short low, short x, short high) {
        if(x < low)
            x = low;
        if(x > high)
            x = high;
        return x;
    }

    public static int clamp(int low, int x, int high) {
        if(x < low)
            x = low;
        if(x > high)
            x = high;
        return x;
    }

    public static long clamp(long low, long x, long high) {
        if(x < low)
            x = low;
        if(x > high)
            x = high;
        return x;
    }

    public static float clamp(float low, float x, float high) {
        if(x < low)
            x = low;
        if(x > high)
            x = high;
        return x;
    }

    public static double clamp(double low, double x, double high) {
        if(x < low)
            x = low;
        if(x > high)
            x = high;
        return x;
    }
}
