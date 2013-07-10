package org.team3309.friarlib.util;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

public class LookupTable {

    private Hashtable table = new Hashtable();
    private double[] keys = null;

    private Interpolator mInterpolator = Interpolator.kNone;

    public static class Interpolator {
        private int val;

        private Interpolator(int val) {
            this.val = val;
        }

        private static final int kNoneVal = 0;
        private static final int kLinearVal = 1;

        public static final Interpolator kNone = new Interpolator(kNoneVal);
        public static final Interpolator kLinear = new Interpolator(kLinearVal);
    }

    /**
     * Create a new LookupTable with a linear interpolator
     */
    public LookupTable() {
        this(Interpolator.kLinear);
    }

    /**
     * Create a new LookupTable with the specified interpolator
     *
     * @param interpolator
     */
    public LookupTable(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    /**
     * Change the interpolation method
     *
     * @param interpolator
     */
    public void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    /**
     * Add a new key-value pair to the table
     *
     * @param key
     * @param value
     */
    public void put(double key, double value) {
        table.put(key, value);
        update();
    }

    /**
     * Update the array of keys, sorting it in ascending order
     */
    private void update() {
        keys = new double[table.size()];
        Enumeration tableKeys = table.keys();
        for (int i = 0; i < table.size() && tableKeys.hasMoreElements(); i++) {
            keys[i] = (Double) tableKeys.nextElement();
        }
        Arrays.sort(keys);
    }

    /**
     * Get the value from the LookupTable using the configured interpolator
     *
     * @param x the key we are searching for
     * @return
     */
    public double get(double x) {
        switch (mInterpolator.val) {
            case Interpolator.kNoneVal:
                return getNone(x);
            case Interpolator.kLinearVal:
                return getLinear(x);
        }
        return Double.MIN_VALUE;
    }

    /**
     * Get a value using a linear interpolation
     *
     * @param x
     * @return
     */
    private double getLinear(double x) {
        if (x < keys[0])
            return (Double) table.get(keys[0]);
        if (x > keys[keys.length - 1])
            return (Double) table.get(keys[keys.length - 1]);
        if (table.containsKey(x))
            return (Double) table.get(x);
        for (int i = 1; i < keys.length; i++) {
            if (x < keys[i] && x > keys[i - 1]) { //found the keys that x is between
                double x1 = keys[i - 1];
                double x2 = keys[i];
                double y1 = (Double) table.get(keys[i - 1]);
                double y2 = (Double) table.get(keys[i]);
                double slope = (y2 - y1) / (x2 - x1);
                double val = y1 + slope * (x - x1);
                return val;
            }
        }
        return Double.MIN_VALUE;
    }

    /**
     * Get a value using no interpolation
     *
     * @param x
     * @return
     */
    private double getNone(double x) {
        if (x < keys[0])
            return (Double) table.get(keys[0]);
        if (x > keys[keys.length - 1])
            return (Double) table.get(keys[keys.length - 1]);
        if (table.containsKey(x))
            return (Double) table.get(x);
        for (int i = 1; i < keys.length; i++) {
            if (x < keys[i] && x > keys[i - 1]) { //found the keys that x is between
                double diff = keys[i] - keys[i - 1];
                if (x <= keys[i - 1] + diff / 2)
                    return (Double) table.get(keys[i - 1]);
                else
                    return (Double) table.get(keys[i]);
            }
        }
        return Double.MIN_VALUE;
    }

}
