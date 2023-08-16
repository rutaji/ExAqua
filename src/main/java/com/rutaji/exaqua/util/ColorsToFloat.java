package com.rutaji.exaqua.util;


import java.util.Hashtable;

public class ColorsToFloat {
    public static float EMPTY = 0F;
    public static float Blue = 0.1F;
    public static float LAVA = 0.2F;
    public static float BEIGE = 0.3F;
    public static float BLACK = 0.4F;
    public static float CYAN = 0.5F;
    public static float DARKBLUE = 0.6F;
    public static float dargreen = 0.7F;
    public static float LIGHTBLUE = 0.8F;
    public static float ORANGE = 0.9F;
    public static float PINK = 1F;
    public static float PURPLE = 1.1F;
    public static float RED = 1.2F;
    public static float WHITE = 1.3F;
    public static float YELLOW = 1.4F;
    public static float unknown = 2f;
    public static Hashtable<String,Float> NameToColor = new Hashtable<String,Float>(){
        {put("",EMPTY);}
        {put("minecraft:water",Blue);}
        {put("minecraft:lava", LAVA);}
    };

    public static float Get(String s)
    {
        if(NameToColor.containsKey(s)){return  NameToColor.get(s);}
        return unknown;
    }

   /* public static void Load()  {

        String path = CraftingHelper.getStream.( new ResourceLocation(ExAqua.MOD_ID, "extra/colors.json"));
        try {

            Reader reader = Files.newBufferedReader(Paths.get(path));

            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(reader).getAsJsonObject();
        } catch (IOException e) {
            System.err.println("file is missing at" + path);
        }

    }*/
}
