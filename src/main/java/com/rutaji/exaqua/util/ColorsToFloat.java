package com.rutaji.exaqua.util;


import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 * {@link com.rutaji.exaqua.item.HandSieve hand sieve} has 17 textures. Each texture is represented with float value 0 and 2.
 * This class help to choose and set texture.
 * Each texture is represented with object of class {@link Color Color}.
 */
public class ColorsToFloat {

    /**
     * Textures and their property value
     * Empty: 0F
     * Blue: 0.1F
     * Lava: 0.2F
     * Beige: 0.3F
     * Black: 0.4F
     * Light cyan: 0.5F
     * Dark blue: 0.6F
     * Dark green: 0.7F
     * Cyan: 0.8F
     * Orange: 0.9F
     * Pink: 1F
     * Purple: 1.1F
     * Red: 1.2F
     * White: 1.3F
     * Yellow: 1.4F
     * Brown: 1.5F
     * Green: 1.6F
     * Unknown: 2F
     */

    /**
     * List of all textures, except lava texture. Lava texture is reserve only for lava.
     * When fluid that doesn't have it's texture set in {@link ColorsToFloat#NameToColor NameToColor} is encountered, the closest color from this list is choosen.
     * @see ColorsToFloat#NameToColor
     * @see ColorsToFloat#ComputeColor
     */
    public static List<Color> Colors =  Arrays.asList(
            new Color("blue",0.1f,208),
            new Color("beige",0.3f,37),
            new Color("light cyan",0.5f,170),
            new Color("dark blue",0.6f,233),
            new Color("dark green",0.7f,130),
            new Color("cyan",0.8f,151),
            new Color("orange",0.9f,19),
            new Color("pink",1f,296),
            new Color("purple",1.1f,256),
            new Color("red",1.2f,5),
            new Color("yellow",1.4f,63),
            new Color("brown",1.5f,15),
            new Color("green",1.6f,100),
            new Color("red other end",1.2f,360)

    );
    public static  float BLACK = 0.4f;
    public static  float WHITE = 1.3f;
    public static float EMPTY = 0F;
    public static float LAVA = 0.2F;
    public static float UNKNOWN = 2f;
    /**
     * Holds pairs of fluids and textures. Textures are represented by their property value.
     */
    public static Hashtable<String,Float> NameToColor = new Hashtable<String,Float>(){
        {put("",EMPTY);}
        {put("minecraft:water",0.1F);}
        {put("minecraft:lava", LAVA);}
        {put("exaqua:mud_fluid",1.5F);}
    };

    /**
     * Returns property from {@link ColorsToFloat#NameToColor }. If the fluid isn't in {@link ColorsToFloat#NameToColor } it calls {@link ColorsToFloat#ComputeColor}
     * returns the texture.
     * @param s fluid that is contained in hand sieve.
     * @return constant that refers to texture.
     */
    public static float Get(String s)
    {

        if (NameToColor.containsKey(s)){return NameToColor.get(s);}
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(s));
        if (fluid == null ) {return UNKNOWN;}
        return ComputeColor(fluid);


    }

    /**
     * Takes fluid and finds the closest texture to that color, according to hsv. Then adds the fluid and its color to {@link ColorsToFloat#NameToColor NameToColor}.
     * @return returns color of the fluid.
     */
    public static float ComputeColor(Fluid fluid)
    {
        FluidAttributes fluidAttributes = fluid.getAttributes();
        int color = fluidAttributes.getColor();
        int r = (color >> 16 & 0xFF);
        int g = (color >> 8 & 0xFF);
        int b = (color & 0xFF);
        float[] hsv = java.awt.Color.RGBtoHSB(r,g,b,null);
        int rgbSum = r+b+g;

        float smallestDifference = 1;
        Color closestColor= null;
        if (rgbSum > 235*3)
        {
            NameToColor.put(fluid.getRegistryName().toString(), WHITE);
            return WHITE;
        }
        if (rgbSum < 30*3)
        {
            NameToColor.put(fluid.getRegistryName().toString(), BLACK);
            return BLACK;
        }
        for (Color c:Colors) {
            float differece= Math.abs(hsv[0] - c.GetHSV());
            if(differece < smallestDifference)
            {
                smallestDifference = differece;
                closestColor = c;
            }
        }
        NameToColor.put(fluid.getRegistryName().toString(), closestColor.ModelValue);

        return closestColor.ModelValue;
    }
}
