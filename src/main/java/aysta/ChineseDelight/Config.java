package aysta.ChineseDelight;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.ArrayList;

@Mod.EventBusSubscriber
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue logDirtBlock;  // 改为 BooleanValue
    public static final ForgeConfigSpec.IntValue magicNumber;       // 改为 IntValue
    public static final ForgeConfigSpec.ConfigValue<String> magicNumberIntroduction;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> items;

    static {
        BUILDER.push("General Settings");

        logDirtBlock = BUILDER
                .comment("Whether to log the dirt block on common setup")
                .define("logDirtBlock", true);

        magicNumber = BUILDER
                .comment("A magic number")
                .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

        magicNumberIntroduction = BUILDER
                .comment("Introduction to the magic number")
                .define("magicNumberIntroduction", "The magic number is: ");

        items = BUILDER
                .comment("List of items")
                .defineList("items", new ArrayList<>(), obj -> obj instanceof String);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}