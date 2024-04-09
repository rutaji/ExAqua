package com.rutaji.exaqua.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ServerModConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> CauldronRainMaxBound;
    public static final ForgeConfigSpec.ConfigValue<Integer> AutoSqueezerRFperTick;
    public static final ForgeConfigSpec.ConfigValue<Integer> AutoSqueezerTimeForRecipie;
    public static final ForgeConfigSpec.ConfigValue<Integer> HandSieveBucketUse;

    static {
        BUILDER.push("Config for ExAqua");

        CauldronRainMaxBound = BUILDER.comment("Chance that cauldron catches water when it is raining. Every tick game generates random number between 0 and this and if its 0 cauldron fills with water." +
                "\n In short, chance = 100/this number (higher number => lower chance). If set to 0 cauldron will never collect rain.").define("Cauldron rain max bound",90);
        AutoSqueezerRFperTick = BUILDER.comment("RF per tick the AutoSquuzer consumes while running.").define("AutoSqueezer RF",50);
        AutoSqueezerTimeForRecipie =  BUILDER.comment("Number of tick it takes AutoSqueezer to craft one recipie.").define("AutoSqueezer crafting time",20);
        HandSieveBucketUse = BUILDER.comment("Number of uses Handsieve get for 1 bucket of fluid.").define("Hand sieve uses per bucket",20);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
