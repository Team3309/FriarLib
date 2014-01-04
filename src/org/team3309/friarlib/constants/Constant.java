/*
 * Copyright (c) 2014, FRC Team 3309 All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.team3309.friarlib.constants;

public class Constant {

    private String name;

    private double doubleVal;

    private double[] doubleList;

    private boolean booleanVal;

    private Constant(String name) {
        if (name == null) {
            throw new NullPointerException("Constant name cannot be null");
        }
        this.name = name;
        ConstantsManager.addConstant(this);
    }

    protected Constant(String name, double defaultVal) {
        this(name);
        this.doubleVal = defaultVal;
    }

    protected Constant(String name, double[] defaultList) {
        this(name);
        this.doubleList = defaultList;
    }

    protected Constant(String name, boolean defaultVal) {
        this(name);
        this.booleanVal = defaultVal;
    }

    public String getName() {
        return name;
    }

    public double getDouble() {
        return doubleVal;
    }

    public double[] getList() {
        return doubleList;
    }

    public boolean getBoolean() {
        return booleanVal;
    }

    public void set(double val) {
        this.doubleVal = val;
    }

    public void set(double[] list) {
        this.doubleList = list;
    }

    public void set(boolean val){
        this.booleanVal = val;
    }

    @Override
    public String toString() {
        String s = "Constant: " + getName() + " = ";
        if (doubleList == null) {
            s += doubleVal;
        } else {
            s += "[";
            for (int i = 0; i < doubleList.length - 1; i++) {
                s += doubleList[i] + ", ";
            }
            s += doubleList[doubleList.length - 1];
            s += "]";
        }
        return s;
    }

}
