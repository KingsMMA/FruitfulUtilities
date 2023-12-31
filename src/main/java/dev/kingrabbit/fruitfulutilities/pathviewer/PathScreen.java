package dev.kingrabbit.fruitfulutilities.pathviewer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.util.ColorOverlay;
import dev.kingrabbit.fruitfulutilities.util.NumberUtils;
import dev.kingrabbit.fruitfulutilities.util.Region;
import dev.kingrabbit.fruitfulutilities.util.SoundUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.*;
import java.util.List;

@SuppressWarnings("CanBeFinal")
public class PathScreen extends Screen {

    public static final String[] section_order = new String[]{
            "beginnings",
            "religion",
            "urban",
            "true_urban",
            "underground",
            "raid",
            "depths"
    };
    public static final HashMap<String, ItemGroup> section_icons = new HashMap<>();
    public static final Identifier PATH_ICON = new Identifier("fruitfulutilities", "textures/gui/path_icon.png");
    public static final Identifier MAJOR_PATH_ICON = new Identifier("fruitfulutilities", "textures/gui/major_path_icon.png");
    public static final int PRIMARY = new Color(70, 70, 70).getRGB();
    public static final int SECONDARY = new Color(50, 50, 50).getRGB();
    public static final int HEADER_COLOR = new Color(70, 215, 10).getRGB();
    private static final Identifier TAB_TEXTURE = new Identifier("fruitfulutilities", "textures/gui/tabs.png");
    public static String section = "beginnings";
    public static HashMap<String, float[]> sections = new HashMap<>();
    public static HashMap<String, JsonObject> selectedElement = new HashMap<>();

    public final HashMap<Region, JsonObject> drawnElements = new HashMap<>();
    public final List<String> sentErrors = new ArrayList<>();
    public List<OrderedText> tooltip = null;

    public PathScreen() {
        super(Text.empty());
    }

    @Override
    protected void init() {
        super.init();

        if (section_icons.isEmpty()) {
            section_icons.put("beginnings", ItemGroup.create(ItemGroup.Row.TOP, 0).displayName(Text.of("§aBeginnings")).icon(() -> new ItemStack(Items.GRASS_BLOCK)).build());
            section_icons.put("religion", ItemGroup.create(ItemGroup.Row.TOP, 0).displayName(Text.of("§aReligion")).icon(() -> new ItemStack(Items.ENCHANTING_TABLE)).build());
            section_icons.put("urban", ItemGroup.create(ItemGroup.Row.TOP, 0).displayName(Text.of("§aUrban")).icon(() -> new ItemStack(Items.IRON_BLOCK)).build());
            section_icons.put("true_urban", ItemGroup.create(ItemGroup.Row.TOP, 0).displayName(Text.of("§aTrue Urban")).icon(() -> new ItemStack(Items.GOLD_BLOCK)).build());
            section_icons.put("underground", ItemGroup.create(ItemGroup.Row.TOP, 0).displayName(Text.of("§aUnderground")).icon(() -> new ItemStack(Items.DIRT)).build());
            section_icons.put("raid", ItemGroup.create(ItemGroup.Row.TOP, 0).displayName(Text.of("§aRaid")).icon(() -> new ItemStack(Items.CARVED_PUMPKIN)).build());
            section_icons.put("depths", ItemGroup.create(ItemGroup.Row.TOP, 0).displayName(Text.of("§aDepths")).icon(() -> new ItemStack(Items.COBBLED_DEEPSLATE)).build());
        }

        if (!sections.containsKey("beginnings")) {
            sections.put("beginnings", new float[]{-19284, -64, 1});
        }

        if (xOffset() == -19284) {
            xOffset(width / 5f - 64 + 32 * 1.5f);
            yOffset(-64);
        }
    }

    @SuppressWarnings({"IfCanBeSwitch", "SpellCheckingInspection"})
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        clearChildren();
        drawnElements.clear();
        tooltip = null;

        if (xOffset() == -19284) {
            xOffset(width / 5f - 64 + 32 * 1.5f);
            yOffset(-64);
        }

        renderBackground(context);

        MatrixStack matrices = context.getMatrices();

        // Path Viewer
        matrices.push();
        float _zoom = zoom();
        matrices.scale(_zoom, _zoom, _zoom);
        
        if (section.equals("beginnings")) {
            JsonObject beginnings = PathManager.paths.get("beginnings");
            renderUpgrade(context, beginnings.getAsJsonObject("faster_melon_spawn_rate"), 1, 1, mouseX, mouseY);
            renderUpgrade(context, beginnings.getAsJsonObject("hearth"), 2, 1, mouseX, mouseY);
            renderUpgrade(context, beginnings.getAsJsonObject("stronger_front_door"), 3, 1, mouseX, mouseY);
            renderUpgrade(context, beginnings.getAsJsonObject("better_jailors"), 4, 1, mouseX, mouseY);
            renderUpgrade(context, beginnings.getAsJsonObject("lock_and_key"), 5, 1, mouseX, mouseY);
            renderUpgrade(context, beginnings.getAsJsonObject("item_removal_procedures"), 6, 1, mouseX, mouseY);

            renderUpgrade(context, beginnings.getAsJsonObject("economic_room"), 1, 3, mouseX, mouseY);
            renderUpgrade(context, beginnings.getAsJsonObject("faster_coin_generation"), 2, 2, mouseX, mouseY);
            renderUpgrade(context, beginnings.getAsJsonObject("better_sell_deals"), 2, 3, mouseX, mouseY);
            renderUpgrade(context, beginnings.getAsJsonObject("increased_guard_limit"), 2, 4, mouseX, mouseY);
            connectUpgrades(1, 3, 2, 2);
            connectUpgrades(1, 3, 2, 3);
            connectUpgrades(1, 3, 2, 4);

            renderUpgrade(context, beginnings.getAsJsonObject("private_merchant"), 3, 2, mouseX, mouseY);
            renderUpgrade(context, beginnings.getAsJsonObject("personal_greenhouse"), 4, 2, mouseX, mouseY);
            connectUpgrades(3, 2, 4, 2);

            renderUpgrade(context, beginnings.getAsJsonObject("castle_ladder"), 3, 3, mouseX, mouseY);
            renderUpgrade(context, beginnings.getAsJsonObject("castle_roof"), 4, 3, mouseX, mouseY);

            renderUpgrade(context, beginnings.getAsJsonObject("castle_backyard"), 1, 5, mouseX, mouseY);
            renderUpgrade(context, beginnings.getAsJsonObject("castle_basement"), 2, 5, mouseX, mouseY);
            renderUpgrade(context, beginnings.getAsJsonObject("urban_start"), 3, 5, mouseX, mouseY);
            connectUpgrades(1, 5, 2, 5);
            connectUpgrades(2, 5, 3, 5);

            renderUpgrade(context, beginnings.getAsJsonObject("religion_start"), 3, 4, mouseX, mouseY);
            renderUpgrade(context, beginnings.getAsJsonObject("underground_start"), 4, 4, mouseX, mouseY);
        } else if (section.equals("religion")){
            JsonObject religion = PathManager.paths.get("religion");
            renderUpgrade(context, religion.getAsJsonObject("special_delivery"), 1, 1, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("for_your_convenience"), 2, 1, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("saving_grace"), 3, 1, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("devotion"), 4, 1, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("bountiful_harvest"), 5, 1, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("plentiful_prizes"), 6, 1, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("forgiving_gods"), 7, 1, mouseX, mouseY);

            renderUpgrade(context, religion.getAsJsonObject("harness_the_spirits"), 2, 4, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("faster_cooking"), 2, 2, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("faster_selling"), 1, 2, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("rise_from_ashes"), 1, 3, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("unconfined_existence"), 4, 3, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("better_farmers"), 1, 5, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("strict_trade_laws"), 1, 6, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("better_return_rates"), 2, 6, mouseX, mouseY);
            connectUpgrades(2, 2, 2, 4);
            connectUpgrades(1, 2, 2, 4);
            connectUpgrades(1, 3, 2, 4);
            connectUpgrades(2, 4, 4, 3);
            connectUpgrades(1, 5, 2, 4);
            connectUpgrades(1, 6, 2, 4);
            connectUpgrades(2, 4, 2, 6);

            renderUpgrade(context, religion.getAsJsonObject("packed_presents"), 3, 2, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("generous_gifts"), 3, 3, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("gold_rush"), 3, 4, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("the_graceful_one"), 5, 2, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("servitude"), 5, 4, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("another_dimension"), 5, 3, mouseX, mouseY);
            connectUpgrades(3, 2, 4, 3);
            connectUpgrades(3, 3, 4, 3);
            connectUpgrades(3, 4, 4, 3);
            connectUpgrades(4, 3, 5, 2);
            connectUpgrades(4, 3, 5, 3);
            connectUpgrades(4, 3, 5, 4);

            renderUpgrade(context, religion.getAsJsonObject("hive_minded_harbingers"), 6, 2, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("complete_clarity"), 7, 2, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("submission"), 7, 3, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("the_greater_good"), 7, 4, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("holy_protection"), 7, 5, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("divine_intervention"), 7, 6, mouseX, mouseY);
            renderUpgrade(context, religion.getAsJsonObject("by_the_power"), 6, 6, mouseX, mouseY);
            connectUpgrades(5, 3, 6, 2);
            connectUpgrades(5, 3, 7, 2);
            connectUpgrades(5, 3, 7, 3);
            connectUpgrades(5, 3, 7, 4);
            connectUpgrades(5, 3, 7, 5);
            connectUpgrades(5, 3, 7, 6);
            connectUpgrades(5, 3, 6, 6);

        } else if (section.equals("urban")) {
            JsonObject urban = PathManager.paths.get("urban");
            renderUpgrade(context, urban.getAsJsonObject("melon_fertilizer"), 1, 1, mouseX, mouseY);
            renderUpgrade(context, urban.getAsJsonObject("significantly_melonier_melons"), 2, 1, mouseX, mouseY);
            renderUpgrade(context, urban.getAsJsonObject("faster_cooking"), 3, 1, mouseX, mouseY);
            renderUpgrade(context, urban.getAsJsonObject("melon_teleporter"), 4, 1, mouseX, mouseY);

            renderUpgrade(context, urban.getAsJsonObject("intensive_research"), 1, 3, mouseX, mouseY);
            renderUpgrade(context, urban.getAsJsonObject("melon_harvesting_technology"), 2, 2, mouseX, mouseY);
            renderUpgrade(context, urban.getAsJsonObject("better_return_rates"), 2, 3, mouseX, mouseY);
            renderUpgrade(context, urban.getAsJsonObject("extreme_economy"), 2, 4, mouseX, mouseY);
            connectUpgrades(1, 3, 2, 2);
            connectUpgrades(1, 3, 2, 3);
            connectUpgrades(1, 3, 2, 4);

            renderUpgrade(context, urban.getAsJsonObject("science_start"), 3, 2, mouseX, mouseY);
            renderUpgrade(context, urban.getAsJsonObject("democracy_start"), 3, 3, mouseX, mouseY);
            renderUpgrade(context, urban.getAsJsonObject("true_urban_start"), 3, 4, mouseX, mouseY);
        } else if (section.equals("true_urban")) {
            JsonObject trueUrban = PathManager.paths.get("true_urban");
            renderUpgrade(context, trueUrban.getAsJsonObject("a_dollar_a_dime"), 1, 1, mouseX, mouseY);
            renderUpgrade(context, trueUrban.getAsJsonObject("doubled_city_funding"), 2, 1, mouseX, mouseY);
            renderUpgrade(context, trueUrban.getAsJsonObject("guaranteed_returns"), 3, 1, mouseX, mouseY);
            renderUpgrade(context, trueUrban.getAsJsonObject("better_farmers"), 4, 1, mouseX, mouseY);
            renderUpgrade(context, trueUrban.getAsJsonObject("incredibly_fast_growth"), 1, 2, mouseX, mouseY);
            renderUpgrade(context, trueUrban.getAsJsonObject("blast_protection"), 2, 2, mouseX, mouseY);
            renderUpgrade(context, trueUrban.getAsJsonObject("overclock_teleporter"), 3, 2, mouseX, mouseY);
            renderUpgrade(context, trueUrban.getAsJsonObject("strict_trade_laws"), 4, 2, mouseX, mouseY);

            renderUpgrade(context, trueUrban.getAsJsonObject("bigger_bunker"), 1, 4, mouseX, mouseY);
            renderUpgrade(context, trueUrban.getAsJsonObject("load_cannons"), 2, 3, mouseX, mouseY);
            renderUpgrade(context, trueUrban.getAsJsonObject("floating_islands"), 2, 5, mouseX, mouseY);
            connectUpgrades(1, 4, 2, 3);
            connectUpgrades(1, 4, 2, 5);

            renderUpgrade(context, trueUrban.getAsJsonObject("faster_selling"), 3, 3, mouseX, mouseY);
            renderUpgrade(context, trueUrban.getAsJsonObject("morale_boost"), 3, 4, mouseX, mouseY);
            renderUpgrade(context, trueUrban.getAsJsonObject("farming_island"), 3, 5, mouseX, mouseY);
            renderUpgrade(context, trueUrban.getAsJsonObject("lower_shipping_taxes"), 3, 6, mouseX, mouseY);
            renderUpgrade(context, trueUrban.getAsJsonObject("even_better_harvesting"), 3, 7, mouseX, mouseY);
            renderUpgrade(context, trueUrban.getAsJsonObject("island_skylights"), 2, 7, mouseX, mouseY);
            connectUpgrades(2, 5, 3, 3);
            connectUpgrades(2, 5, 3, 4);
            connectUpgrades(2, 5, 3, 5);
            connectUpgrades(2, 5, 3, 6);
            connectUpgrades(2, 5, 3, 7);
            connectUpgrades(2, 5, 2, 7);

            renderUpgrade(context, trueUrban.getAsJsonObject("quality_control"), 4, 4, mouseX, mouseY);
            renderUpgrade(context, trueUrban.getAsJsonObject("better_filters"), 4, 3, mouseX, mouseY);
            connectUpgrades(4, 3, 4, 4);
            renderUpgrade(context, trueUrban.getAsJsonObject("defender_island"), 4, 5, mouseX, mouseY);
            renderUpgrade(context, trueUrban.getAsJsonObject("deluxe_sky_farm"), 4, 6, mouseX, mouseY);
            connectUpgrades(3, 5, 4, 4);
            connectUpgrades(3, 5, 4, 5);
            connectUpgrades(3, 5, 4, 6);

            renderUpgrade(context, trueUrban.getAsJsonObject("public_executions"), 5, 3, mouseX, mouseY);
            renderUpgrade(context, trueUrban.getAsJsonObject("iron_skin"), 5, 4, mouseX, mouseY);
            renderUpgrade(context, trueUrban.getAsJsonObject("the_grand_finale"), 5, 5, mouseX, mouseY);
            renderUpgrade(context, trueUrban.getAsJsonObject("projectile_protection"), 5, 6, mouseX, mouseY);
            renderUpgrade(context, trueUrban.getAsJsonObject("pricey_gunpowder"), 5, 7, mouseX, mouseY);
            connectUpgrades(4, 5, 5, 3);
            connectUpgrades(4, 5, 5, 4);
            connectUpgrades(4, 5, 5, 5);
            connectUpgrades(4, 5, 5, 6);
            connectUpgrades(4, 5, 5, 7);
        } else if (section.equals("underground")) {
            JsonObject underground = PathManager.paths.get("underground");

            renderUpgrade(context, underground.getAsJsonObject("revoke_weapon_bans"), 1, 1, mouseX, mouseY);

            renderUpgrade(context, underground.getAsJsonObject("upgrade_town_bm"), 2, 1, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("quicker_trading"), 3, 1, mouseX, mouseY);
            connectUpgrades(2, 1, 3, 1);

            renderUpgrade(context, underground.getAsJsonObject("lucky_day"), 1, 2, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("farming_rally"), 2, 2, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("faster_cooking"), 3, 2, mouseX, mouseY);

            renderUpgrade(context, underground.getAsJsonObject("upgrade_town_armory"), 2, 3, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("advanced_weaponry"), 1, 3, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("public_executions"), 1, 4, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("increased_blast_resistance"), 1, 5, mouseX, mouseY);
            connectUpgrades(1, 3, 2, 3);
            connectUpgrades(1, 4, 2, 3);
            connectUpgrades(1, 5, 2, 3);

            renderUpgrade(context, underground.getAsJsonObject("better_bankers"), 2, 4, mouseX, mouseY);
            connectUpgrades(2, 3, 2, 4);

            renderUpgrade(context, underground.getAsJsonObject("faster_selling"), 3, 3, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("rapid_reload"), 3, 4, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("better_return_rates"), 3, 5, mouseX, mouseY);
            connectUpgrades(2, 3, 3, 3);
            connectUpgrades(2, 3, 3, 4);
            connectUpgrades(2, 3, 3, 5);

            renderUpgrade(context, underground.getAsJsonObject("upgrade_town_center"), 4, 3, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("quicker_sprouting"), 4, 1, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("fertile_soil"), 5, 1, mouseX, mouseY);
            connectUpgrades(4, 1, 4, 3);
            connectUpgrades(4, 3, 5, 1);

            renderUpgrade(context, underground.getAsJsonObject("golden_extractors"), 6, 1, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("market_manipulation"), 6, 2, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("projectile_proofing"), 6, 3, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("light_based_growth_spurts"), 6, 4, mouseX, mouseY);
            connectUpgrades(4, 3, 6, 1);
            connectUpgrades(4, 3, 6, 2);
            connectUpgrades(4, 3, 6, 3);
            connectUpgrades(4, 3, 6, 4);

            renderUpgrade(context, underground.getAsJsonObject("upgrade_town_depths"), 4, 6, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("richer_metals"), 3, 7, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("grenade_tech_breakthrough"), 4, 7, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("doubled_city_funding"), 5, 7, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("depths_start"), 3, 6, mouseX, mouseY);
            connectUpgrades(4, 3, 4, 6);
            connectUpgrades(3, 7, 4, 6);
            connectUpgrades(4, 6, 4, 7);
            connectUpgrades(4, 6, 5, 7);
            connectUpgrades(3, 6, 4, 6);

            renderUpgrade(context, underground.getAsJsonObject("upgrade_town_farm"), 7, 7, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("what_shines_bright"), 6, 7, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("guaranteed_returns"), 8, 7, mouseX, mouseY);
            connectUpgrades(4, 3, 7, 7);
            connectUpgrades(6, 7, 7, 7);
            connectUpgrades(7, 7, 8, 7);

            renderUpgrade(context, underground.getAsJsonObject("farming_reservations"), 6, 8, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("extra_fertilizer"), 7, 8, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("melon_generators"), 8, 8, mouseX, mouseY);
            connectUpgrades(6, 8, 7, 7);
            connectUpgrades(7, 7, 7, 8);
            connectUpgrades(7, 7, 8, 8);

            renderUpgrade(context, underground.getAsJsonObject("upgrade_town_second_wall"), 6, 5, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("upgrade_town_teleporter"), 7, 4, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("currency_exchange"), 7, 6, mouseX, mouseY);
            connectUpgrades(4, 3, 6, 5);
            connectUpgrades(6, 5, 7, 4);
            connectUpgrades(6, 5, 7, 6);

            renderUpgrade(context, underground.getAsJsonObject("upgrade_town_raid"), 7, 5, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("cutting_through"), 8, 4, mouseX, mouseY);
            renderUpgrade(context, underground.getAsJsonObject("raid_start"), 8, 6, mouseX, mouseY);
            connectUpgrades(6, 5, 7, 5);
            connectUpgrades(7, 5, 8, 4);
            connectUpgrades(7, 5, 8, 6);
        } else if (section.equals("raid")) {
            JsonObject raid = PathManager.paths.get("raid");

            renderUpgrade(context, raid.getAsJsonObject("standard_issue_forges"), 1, 1, mouseX, mouseY);
            renderUpgrade(context, raid.getAsJsonObject("basic_armory"), 2, 1, mouseX, mouseY);
            renderUpgrade(context, raid.getAsJsonObject("grenade_stockpiles"), 3, 1, mouseX, mouseY);
            renderUpgrade(context, raid.getAsJsonObject("power_through"), 4, 1, mouseX, mouseY);

            renderUpgrade(context, raid.getAsJsonObject("thorough_defense"), 1, 2, mouseX, mouseY);
            renderUpgrade(context, raid.getAsJsonObject("stronger_blades"), 2, 2, mouseX, mouseY);
            renderUpgrade(context, raid.getAsJsonObject("gemstone_blades"), 3, 2, mouseX, mouseY);

            renderUpgrade(context, raid.getAsJsonObject("diamond_forges"), 1, 3, mouseX, mouseY);
            renderUpgrade(context, raid.getAsJsonObject("the_perfect_weapons"), 2, 3, mouseX, mouseY);
            renderUpgrade(context, raid.getAsJsonObject("impenetrable_defenses"), 3, 3, mouseX, mouseY);
        } else if (section.equals("depths")) {
            JsonObject depths = PathManager.paths.get("depths");

            renderUpgrade(context, depths.getAsJsonObject("warrant_funding"), 2, 1, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("farmland_acquisition"), 3, 1, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("even_quicker_trading"), 4, 1, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("luckier_day"), 2, 2, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("value_duplication"), 3, 2, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("double_duplication"), 4, 2, mouseX, mouseY);
            connectUpgrades(3, 2, 4, 2);
            renderUpgrade(context, depths.getAsJsonObject("better_negotiations"), 2, 3, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("triple_city_funding"), 3, 3, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("dual_polishing_measures"), 4, 3, mouseX, mouseY);

            renderUpgrade(context, depths.getAsJsonObject("upgrade_town_east"), 6, 2, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("blacklight_photosynthesis"), 7, 1, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("farmland_expansion"), 8, 1, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("greed_is_good"), 9, 1, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("exponential_scaling"), 7, 2, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("quadruple_city_funding"), 8, 2, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("expert_economists"), 9, 2, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("underground_trade_routes"), 7, 3, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("extrinsic_value"), 8, 3, mouseX, mouseY);

            renderUpgrade(context, depths.getAsJsonObject("upgrade_town_north"), 1, 6, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("melon_to_melon_ratio"), 2, 5, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("insider_knowledge"), 3, 5, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("value_incrementation"), 4, 5, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("the_house_always_wins"), 2, 6, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("quintuple_city_funding"), 3, 6, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("multiply_demand"), 4, 6, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("quantum_fluctuations"), 2, 7, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("farming_permits"), 3, 7, mouseX, mouseY);

            renderUpgrade(context, depths.getAsJsonObject("upgrade_town_south"), 6, 6, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("tri_polishing_process"), 7, 5, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("sextuple_city_funding"), 8, 5, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("repeated_multiplication"), 7, 6, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("farmland_reclamation"), 8, 6, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("the_foundry"), 7, 7, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("perfect_profits"), 8, 7, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("we_need_to_go_deeper"), 10, 4, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("final_funding"), 10, 5, mouseX, mouseY);
            connectUpgrades(10, 4, 10, 5);

            renderUpgrade(context, depths.getAsJsonObject("better_farmers"), 11, 3, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("a_little_richer"), 12, 3, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("septuple_city_funding"), 13, 3, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("fullest_farmland"), 11, 4, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("octuple_city_funding"), 12, 4, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("true_value"), 13, 4, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("ultimate_exponential"), 11, 5, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("flawless_extraction"), 12, 5, mouseX, mouseY);
            renderUpgrade(context, depths.getAsJsonObject("one_billion_gold"), 13, 5, mouseX, mouseY);
        }

        matrices.pop();

        // UI
        context.fill(0, 0, width / 5, height, PRIMARY);
        context.fill(width / 5, 0, width / 5 + 6, height, SECONDARY);

        context.drawCenteredTextWithShadow(textRenderer, "Path Viewer", width / 10, 20, HEADER_COLOR);
        int y = 40;
        if (!selectedElement.containsKey(section)) {
            for (OrderedText line : textRenderer.wrapLines(StringVisitable.plain("Click on an upgrade to view information about it."), width / 5 - 20)) {
                context.drawTextWithShadow(textRenderer, line, 10, y, 0xFFFFFF);
                y += textRenderer.fontHeight + 2;
            }
        } else {
            JsonObject _selectedElement = selectedElement.get(section);
            String display = _selectedElement.get("display").getAsString();
            int price = _selectedElement.get("price").getAsInt();
            for (OrderedText line : textRenderer.wrapLines(StringVisitable.plain(
                    "§6Name: §f" + display + "\n" +
                            "§6Description: §f" + _selectedElement.get("description").getAsString() + "\n" +
                            "§6Price: §f" + NumberUtils.toFancyNumber(price) + " " + Currency.valueOf(_selectedElement.get("currency").getAsString().toUpperCase()).format(price) + "\n" +
                            "§6Location: §f" + _selectedElement.get("location").getAsString().replaceAll(",", ", ")
            ), width / 5 - 20)) {
                context.drawTextWithShadow(textRenderer, line, 10, y, 0xFFFFFF);
                y += textRenderer.fontHeight + 2;
            }

            String upgradeId = PathManager.getId(_selectedElement);

            int maxWidth = 98;
            int horizontalSpacing = 8;
            int buttonWidth = Math.min(maxWidth, width / 5 - horizontalSpacing * 2);
            boolean sameLine = buttonWidth == maxWidth && width / 5 - horizontalSpacing * 3 >= maxWidth * 2 + horizontalSpacing * 3;
            boolean isTracking = PathManager.tracking.contains(_selectedElement);
            boolean isUnlocked = PathManager.purchasedIds.contains(upgradeId);

            ButtonWidget track = ButtonWidget.builder(Text.of(isTracking ? "Untrack" : "Track"), widget -> {
                if (isTracking) PathManager.tracking.remove(_selectedElement);
                else PathManager.tracking.add(_selectedElement);
            }).dimensions(width / 10 - (sameLine ? maxWidth + horizontalSpacing: buttonWidth / 2), y + 20, buttonWidth, 20).build();

            ButtonWidget unlock = ButtonWidget.builder(Text.of(isUnlocked ? "Lock" : "Unlock"), widget -> {
                if (isUnlocked) PathManager.purchasedIds.remove(upgradeId);
                else PathManager.purchasedIds.add(upgradeId);
                PathManager.clearCache();
            }).dimensions(width / 10 - (sameLine ? -horizontalSpacing : buttonWidth / 2), sameLine ? y + 20 : y + 50, buttonWidth, 20).build();

            addSelectableChild(track);
            addSelectableChild(unlock);
            track.render(context, mouseX, mouseY, delta);
            unlock.render(context, mouseX, mouseY, delta);

            y += 60;
            if (!sameLine) y += 30;
        }

        y += 20;

        context.drawCenteredTextWithShadow(textRenderer, "Tracked Upgrades", width / 10, y, HEADER_COLOR);
        y += 20;

        List<JsonObject> allTracked = PathManager.allTracked();

        if (allTracked.isEmpty()) {
            for (OrderedText line : textRenderer.wrapLines(StringVisitable.plain("Track an upgrade to view its waypoint and track your progress towards it."), width / 5 - 20)) {
                context.drawTextWithShadow(textRenderer, line, 10, y, 0xFFFFFF);
                y += textRenderer.fontHeight + 2;
            }
        } else {

            StringBuilder information = new StringBuilder("Currently tracking the following upgrades:");
            for (JsonObject tracked : allTracked) {
                HashMap<Currency, Long> cost = PathManager.getCost(tracked);
                if (cost.isEmpty()) {
                    information.append("\n    §7• §a").append(tracked.get("display").getAsString()).append("§7: ");
                    information.append("§eUnlocked!");
                } else {
                    information.append("\n    §7• §a").append(tracked.get("display").getAsString()).append("§7: ");
                    information = PathManager.appendFormattedCost(information, cost);
                }
            }
            for (OrderedText line : textRenderer.wrapLines(StringVisitable.plain(information.toString()), width / 5 - 20)) {
                context.drawTextWithShadow(textRenderer, line, 10, y, 0xFFFFFF);
                y += textRenderer.fontHeight + 2;
            }

        }

        addSelectableChild(ButtonWidget.builder(Text.of("Reset Position"), button -> {
            xOffset(width / 5f - 64 + 32 * 1.5f);
            yOffset(-64);
            zoom(1);
        }).dimensions(width - 103, height - 100, 98, 20).tooltip(Tooltip.of(Text.of("Reset the window's current offset and zoom. (R)"))).build()).render(context, mouseX, mouseY, delta);

        addSelectableChild(ButtonWidget.builder(Text.of("Track All"), button -> {
            for (JsonElement _pathUpgrade : PathManager.paths.get(section).asMap().values()) {
                JsonObject pathUpgrade = _pathUpgrade.getAsJsonObject();
                if (!PathManager.tracking.contains(pathUpgrade)) {
                    PathManager.tracking.add(pathUpgrade);
                }
            }
        }).dimensions(width - 103, height - 75, 98, 20).tooltip(Tooltip.of(Text.of("Track all upgrades in the selected path."))).build()).render(context, mouseX, mouseY, delta);

        addSelectableChild(ButtonWidget.builder(Text.of("Untrack All"),
                button -> PathManager.tracking.removeIf(jsonObject -> PathManager.paths.get(section).asMap().containsValue(jsonObject))
        ).dimensions(width - 103, height - 50, 98, 20).tooltip(Tooltip.of(Text.of("Untrack all upgrades in the selected path."))).build()).render(context, mouseX, mouseY, delta);

        addSelectableChild(ButtonWidget.builder(Text.of("Clear Cache"),
                button -> PathManager.clearCache()
        ).dimensions(width - 103, height - 25, 98, 20).tooltip(Tooltip.of(Text.of("Reculates the state and cost of upgrades."))).build()).render(context, mouseX, mouseY, delta);

        // Item tabs
        matrices.push();
        matrices.scale(1.5f, 1.5f, 1.5f);
        int tabX = (int) ((width / 5 + 6) / 1.5f);
        int tabY = (int) (20 / 1.5f);
        for (String _section : section_order) {
            if (sections.containsKey(_section)) {
                ItemGroup icon = section_icons.get(_section);
                renderTabIcon(context, icon, section.equals(_section), tabX, tabY);
                if (tabX <= mouseX / 1.5f && mouseX / 1.5f <= tabX + 32 &&
                        tabY <= mouseY / 1.5f && mouseY / 1.5f <= tabY + 26) {
                    tooltip = Collections.singletonList(icon.getDisplayName().asOrderedText());
                }
                tabY += 27;
            }
        }
        matrices.pop();

        // Tooltip
        if (tooltip != null) {
            context.drawOrderedTooltip(textRenderer, tooltip, mouseX, mouseY);
        }
    }

    public void renderTabIcon(DrawContext context, ItemGroup group, boolean selected, int tabX, int tabY) {
        MatrixStack matrices = context.getMatrices();
        context.drawTexture(TAB_TEXTURE, tabX, tabY, 0, selected ? 26 : 0, 32, 26, 32, 52);
        matrices.push();
        matrices.translate(0.0f, 0.0f, 100.0f);
        ItemStack itemStack = group.getIcon();
        context.drawItem(itemStack, tabX + 8, tabY + 5);
        matrices.pop();
    }

    public void connectUpgrades(float x1, float y1, float x2, float y2) {
        float _xOffset = xOffset();
        float _yOffset = yOffset();
        float _zoom = zoom();

        x1 = 32 + x1 * 64;
        y1 = 32 + y1 * 64;
        x2 = 32 + x2 * 64;
        y2 = 32 + y2 * 64;

        if (x1 == x2) {
            float lineWidth = 1.5f;
            float calculatedX = (int) (x1 + _xOffset + 16);
            float calculatedY1 = (int) (y1 + _yOffset + 32);
            float calculatedY2 = (int) (y2 + _yOffset + 0.5);

            GlStateManager._depthMask(false);
            GlStateManager._disableCull();
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            Tessellator tessellator = RenderSystem.renderThreadTesselator();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex((calculatedX - lineWidth / 2) * _zoom,  calculatedY1 * _zoom, 0).color(0, 0, 0, 255).next();
            bufferBuilder.vertex((calculatedX + lineWidth / 2) * _zoom, calculatedY1 * _zoom, 0).color(0, 0, 0, 255).next();
            bufferBuilder.vertex((calculatedX + lineWidth / 2) * _zoom, calculatedY2 * _zoom, 0).color(0, 0, 0, 255).next();
            bufferBuilder.vertex((calculatedX - lineWidth / 2) * _zoom, calculatedY2 * _zoom, 0).color(0, 0, 0, 255).next();
            tessellator.draw();
            GlStateManager._enableCull();
            GlStateManager._depthMask(true);
            return;
        }

        float calculatedX1 = (int) (x1 + _xOffset + 30);
        float calculatedX2 = (int) (x2 + _xOffset + 2);
        float calculatedY1 = (int) (y1 + _yOffset + 16);
        float calculatedY2 = (int) (y2 + _yOffset + 16);

        float dx = calculatedX2 - calculatedX1;
        float dy = calculatedY2 - calculatedY1;
        double angle = Math.atan(dx / dy);
        double lineWidth = 1.5f / Math.sin(angle);

        GlStateManager._depthMask(false);
        GlStateManager._disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(calculatedX1 * _zoom, (calculatedY1 - lineWidth / 2) * _zoom, 0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(calculatedX1 * _zoom, (calculatedY1 + lineWidth / 2) * _zoom, 0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(calculatedX2 * _zoom, (calculatedY2 + lineWidth / 2) * _zoom, 0).color(0, 0, 0, 255).next();
        tessellator.draw();
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(calculatedX2 * _zoom, (calculatedY2 + lineWidth / 2) * _zoom, 0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(calculatedX2 * _zoom, (calculatedY2 - lineWidth / 2) * _zoom, 0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(calculatedX1 * _zoom, (calculatedY1 - lineWidth / 2) * _zoom, 0).color(0, 0, 0, 255).next();
        tessellator.draw();
        GlStateManager._enableCull();
        GlStateManager._depthMask(true);
    }

    public void renderUpgrade(DrawContext context, JsonObject upgrade, float x, float y, int mouseX, int mouseY) {
        if (upgrade == null) {
            String errorMessage = "Not rendering upgrade at " + x + ", " + y + " on path " + section + " as it is null.";
            if (!sentErrors.contains(errorMessage)) {
                sentErrors.add(errorMessage);
                FruitfulUtilities.LOGGER.error(errorMessage);
            }
            return;
        }

        float _xOffset = xOffset();
        float _yOffset = yOffset();
        float _zoom = zoom();

        x = 32 + x * 64;
        y = 32 + y * 64;

        drawnElements.put(new Region((x + _xOffset) * _zoom, (y + _yOffset) * _zoom, (x + 32 + _xOffset) * _zoom, (y + 32 + _yOffset) * _zoom), upgrade);

        boolean major = upgrade.has("path");
        boolean unlocked = PathManager.purchasedIds.contains(PathManager.getId(upgrade));
        boolean hovered = x + _xOffset <= mouseX / _zoom && mouseX / _zoom <= x + _xOffset + 32 &&
                y + _yOffset <= mouseY / _zoom && mouseY / _zoom <= y + _yOffset + 32;
        boolean selected = Objects.equals(selectedElement.get(section), upgrade);
        boolean locked = PathManager.locked(upgrade);
        boolean tracked = PathManager.tracking.contains(upgrade);

        Color color = new Color(255, 255, 255);
        if (unlocked) color = new Color(0, 255, 0);
        else if (locked) color = new Color(255, 0, 0);
        if (tracked) color = ColorOverlay.overlayColors(color, new Color(0, 255, 255), 0.25f);
        if (selected) color = ColorOverlay.overlayColors(color, new Color(255, 255, 150), 0.35f);
        if (hovered) color = ColorOverlay.overlayColors(color, new Color(150, 150, 150), 0.35f);

        context.setShaderColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        context.drawTexture(major ? MAJOR_PATH_ICON : PATH_ICON, (int) (_xOffset + x), (int) (_yOffset + y), 0, 0, 0, 32, 32, 32, 32);
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        if (hovered) {
            if (mouseX < width / 5 + 6) return;
            List<OrderedText> lines = textRenderer.wrapLines(StringVisitable.plain("§7" + upgrade.get("description").getAsString()), 150);
            List<OrderedText> newLines = new ArrayList<>();
            newLines.add(Text.literal("§a" + upgrade.get("display").getAsString()).asOrderedText());
            newLines.addAll(lines);
            String currencyRaw = upgrade.get("currency").getAsString();
            try {
                Currency currency = Currency.valueOf(currencyRaw.toUpperCase());
                int price = upgrade.get("price").getAsInt();
                newLines.add(Text.literal("§" + currency.getPrimaryColor() + "Price: §" + currency.getSecondaryColor() + NumberUtils.toFancyNumber(price) + " " + currency.format(price)).asOrderedText());

                tooltip = newLines;
            } catch (IllegalArgumentException exception) {
                FruitfulUtilities.LOGGER.error("Unknown currency: " + currencyRaw, exception);
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        zoom(zoom() + amount * 0.2f);
        if (zoom() < 0.2f) zoom(0.2f);
        else if (zoom() > 10f) zoom(10f);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0) {
            xOffset(xOffset() + deltaX / zoom());
            yOffset(yOffset() + deltaY / zoom());
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_R) {
            xOffset(width / 5f - 64 + 32 * 1.5f);
            yOffset(-64);
            zoom(1);

            SoundUtils.clickSound();
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        if (mouseX < width / 5f + 6) return super.mouseClicked(mouseX, mouseY, button);

        int tabX = (int) ((width / 5 + 6) / 1.5f);
        int tabY = (int) (20 / 1.5f);
        for (String _section : section_order) {
            if (sections.containsKey(_section)) {
                if (tabX <= mouseX / 1.5f && mouseX / 1.5f <= tabX + 32 &&
                        tabY <= mouseY / 1.5f && mouseY / 1.5f <= tabY + 26) {
                    if (!section.equals(_section)) {
                        section = _section;
                        SoundUtils.clickSound();

                        if (xOffset() == -19284) {
                            xOffset(width / 5f - 64 + 32 * 1.5f);
                            yOffset(-64);
                        }
                    }
                    return super.mouseClicked(mouseX, mouseY, button);
                }
                tabY += 27;
            }
        }

        for (Region region : drawnElements.keySet()) {
            if (region.contains(mouseX, mouseY)) {
                JsonObject upgradeData = drawnElements.get(region);
                if (Objects.equals(upgradeData, selectedElement.get(section))) {
                    selectedElement.remove(section);
                } else {
                    selectedElement.put(section, upgradeData);
                }
                SoundUtils.clickSound();
                return super.mouseClicked(mouseX, mouseY, button);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    // region Offset and zoom getters and setters
    public float xOffset() {
        return sections.get(section)[0];
    }

    public void xOffset(double xOffset) {
        xOffset((float) xOffset);
    }

    public void xOffset(float xOffset) {
        sections.get(section)[0] = xOffset;
    }

    public float yOffset() {
        return sections.get(section)[1];
    }

    public void yOffset(double yOffset) {
        yOffset((float) yOffset);
    }

    public void yOffset(float yOffset) {
        sections.get(section)[1] = yOffset;
    }

    public float zoom() {
        return sections.get(section)[2];
    }

    public void zoom(double zoom) {
        zoom((float) zoom);
    }

    public void zoom(float zoom) {
        sections.get(section)[2] = zoom;
    }
    // endregion

}
