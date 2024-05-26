package com.rutaji.exaqua.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Loads serer config from ExAqua-server.toml file.
 */
public final class ServerModConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> CAULDRON_RAIN_MAX_BOUND;
    public static final ForgeConfigSpec.ConfigValue<Integer> AUTO_SQUEEZER_R_FPER_TICK;
    public static final ForgeConfigSpec.ConfigValue<Integer> AUTO_SQUEEZER_TIME_FOR_RECIPIE;
    public static final ForgeConfigSpec.ConfigValue<Integer> HAND_SIEVE_BUCKET_USE;

    public static final ForgeConfigSpec.ConfigValue<Boolean> REQUIRE_ELECTRICITY;

    static {
        BUILDER.push("Config for ExAqua");

        CAULDRON_RAIN_MAX_BOUND = BUILDER.comment("Chance that cauldron catches water when it is raining. Every tick game generates random number between 0 and this and if its 0 cauldron fills with water." +
                "\n In short, chance = 100/this number (higher number => lower chance). If set to 0 cauldron will never collect rain.").define("Cauldron rain max bound",90);
        AUTO_SQUEEZER_R_FPER_TICK = BUILDER.comment("RF per tick the AutoSquuzer consumes while running.").define("AutoSqueezer RF",50);
        AUTO_SQUEEZER_TIME_FOR_RECIPIE =  BUILDER.comment("Number of tick it takes AutoSqueezer to craft one recipie.").define("AutoSqueezer crafting time",20);
        HAND_SIEVE_BUCKET_USE = BUILDER.comment("Number of uses Handsieve get for 1 bucket of fluid.").define("Hand sieve uses per bucket",20);
        REQUIRE_ELECTRICITY = BUILDER.comment("If false all machines run without energy.").define("Require electricity",Boolean.TRUE);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
