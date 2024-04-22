package com.rutaji.exaqua.util;

import net.minecraft.item.Item;

/**
 * Used to represent textures of {@link com.rutaji.exaqua.item.HandSieve hand sieve}. Textures change depending on the color of the fluid that the hand sieve holds.
 * Every texture has different color represented by {@link Color#HSV HSV value}.
 * When player picks up fluid, {@link ColorsToFloat ColorsToFloat} picks texture closest to the color of picked up fluid.
 * To chnage the texture set {@link com.rutaji.exaqua.item.HandSieve hand sieve} property "color" to the {@link Color#ModelValue model property value} of the color.
 * @see ColorsToFloat
 * @see ModItemModelProperties#makeHandSieve
 */
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
     * @param name name used for debugging and identification
     * @param modelVal property value
     * @param hsv must be value from 0 to 1
     */
    public Color(String name,float modelVal, float hsv)
    {
        Name = name;
        ModelValue = modelVal;
        HSV = hsv;
    }
}
