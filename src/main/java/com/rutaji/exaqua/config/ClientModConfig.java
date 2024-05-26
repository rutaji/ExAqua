package com.rutaji.exaqua.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 *  Loads client setting from ExAqua-client.toml file.
 */
public final class ClientModConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> CAULDRON_RENDER_ENTITY;

    static {
        BUILDER.push("Config for ExAqua");

        CAULDRON_RENDER_ENTITY = BUILDER.comment("Disables render entity, that renders liquid inside cauldron every frame.(requires client restart)").define("Cauldron render liquid",true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
