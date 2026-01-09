package blackcard.blackcard.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class BlackCardConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        COMMON = new Common(builder);
        COMMON_SPEC = builder.build();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
    }

    public static class Common {
        public final ForgeConfigSpec.BooleanValue enableCustomDisplayName;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Black Card Mod Configuration")
                    .push("general");

            enableCustomDisplayName = builder
                    .translation("config.blackcard.enableCustomDisplayName")
                    .define("enableCustomDisplayName", true);

            builder.pop();
        }
    }
}