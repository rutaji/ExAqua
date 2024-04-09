package com.rutaji.exaqua.util;

public class Color {
    public final String Name;
    public final float ModelValue;
    private final float HSV;

    public float GetHSV(){return HSV;}
    public Color(String name,float modelVal, int hsv)
    {
        Name = name;
        ModelValue = modelVal;
        HSV = (float)hsv/360;
    }

    /**
     *
     * @param name
     * @param modelVal
     * @param hsv must be value from 0 to 1
     */
    public Color(String name,float modelVal, float hsv)
    {
        Name = name;
        ModelValue = modelVal;
        HSV = hsv;
    }
}
