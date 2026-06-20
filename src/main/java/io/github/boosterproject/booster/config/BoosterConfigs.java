package io.github.boosterproject.booster.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public final class BoosterConfigs {
    public static final ForgeConfigSpec SERVER_SPEC;
    public static final Server SERVER;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        SERVER = new Server(builder);
        SERVER_SPEC = builder.build();
    }

    private BoosterConfigs() {
    }

    public static void register(ModLoadingContext context) {
        context.registerConfig(ModConfig.Type.SERVER, SERVER_SPEC, "booster-server.toml");
    }

    public static final class Server {
        public final ForgeConfigSpec.DoubleValue powerfulPumpStressImpact;
        public final ForgeConfigSpec.DoubleValue powerfulPumpPressureMultiplier;

        private Server(ForgeConfigSpec.Builder builder) {
            powerfulPumpStressImpact = builder
                .comment(
                    "Create base stress impact for booster:powerful_mechanical_pump at 1 RPM.",
                    "Create's kinetic network still scales the final stress cost with RPM; Booster does not multiply by speed here.",
                    "Must be greater than 0.",
                    "Restart the world or server after changing this value to guarantee existing kinetic networks are recalculated."
                )
                .defineInRange("powerfulPumpStressImpact", 16.0D, Double.MIN_VALUE, Double.MAX_VALUE);

            powerfulPumpPressureMultiplier = builder
                .comment(
                    "Pressure multiplier applied by booster:powerful_mechanical_pump.",
                    "Effective pressure is abs(speed) multiplied by this value, then clamped by Booster's safety cap.",
                    "Create's FluidNetwork derives transfer speed from pressure, so higher values increase throughput.",
                    "Must be greater than 0.",
                    "Restart the world or server after changing this value to guarantee pipe networks are recalculated."
                )
                .defineInRange("powerfulPumpPressureMultiplier", 8.0D, Double.MIN_VALUE, Double.MAX_VALUE);
        }
    }
}
