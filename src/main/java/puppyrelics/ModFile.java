package puppyrelics;

import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.helpers.CardBorderGlowManager;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import puppyrelics.cards.PirateRaidCard;
import puppyrelics.cards.RatRaceCard;
import puppyrelics.relics.*;
import puppyrelics.util.ProAudio;
import basemod.interfaces.PostDungeonInitializeSubscriber;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;



@SuppressWarnings({"unused", "WeakerAccess"})
@SpireInitializer
public class ModFile implements
        EditCardsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        EditCharactersSubscriber,
        AddAudioSubscriber,
        PostInitializeSubscriber,
        PostDungeonInitializeSubscriber {

    public static final String modID = "puppyrelics";
    public static final String FILE_NAME = "NinjaPuppyConfig";

    public static final String ENABLE_MOD = "enableMod";
    public static boolean modEnabled = true;

    public static final String LEGACY_MODE = "legacyMode";
    public static boolean legacyMode = false;

    public static final String MOUSE_RADIUS = "mouseRadius";
    public static int mouseRadius = 360;

    public static SpireConfig LOConfig;

    public static String makeID(String idText) {
        return modID + ":" + idText;
    }


    private static UIStrings uiStrings;
    private static String[] TEXT;


    public static Color characterColor = new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1);

    public static final String SHOULDER1 = makeCharacterPath("mainChar/shoulder.png");
    public static final String SHOULDER2 = makeCharacterPath("mainChar/shoulder2.png");
    public static final String CORPSE = makeCharacterPath("mainChar/corpse.png");
    private static final String ATTACK_S_ART = makeImagePath("512/attack.png");
    private static final String SKILL_S_ART = makeImagePath("512/skill.png");
    private static final String POWER_S_ART = makeImagePath("512/power.png");
    private static final String CARD_ENERGY_S = makeImagePath("512/energy.png");
    private static final String TEXT_ENERGY = makeImagePath("512/text_energy.png");
    private static final String ATTACK_L_ART = makeImagePath("1024/attack.png");
    private static final String SKILL_L_ART = makeImagePath("1024/skill.png");
    private static final String POWER_L_ART = makeImagePath("1024/power.png");
    private static final String CARD_ENERGY_L = makeImagePath("1024/energy.png");
    private static final String CHARSELECT_BUTTON = makeImagePath("charSelect/charButton.png");
    private static final String CHARSELECT_PORTRAIT = makeImagePath("charSelect/charBG.png");
    public static final String SOUND_RELIC_ENABLED = "soundRelicEnabled";
    public static final String SOUND_RELIC_MODE    = "soundRelicMode";
    public static final String SOUND_RELIC_PICK    = "soundRelicPick";


    public enum SoundMode { SPECIFIC, RANDOM_CLICK, RANDOM_ROOM, RANDOM_ACT, RANDOM_RUN }

    public static boolean soundRelicEnabled = false;
    public static SoundMode soundRelicMode = SoundMode.SPECIFIC;
    public static String soundRelicPick = ProAudio.kazoo.name();
    public static Settings.GameLanguage[] SupportedLanguages = {
            Settings.GameLanguage.ENG,
    };


    private String getLangString() {
        for (Settings.GameLanguage lang : SupportedLanguages) {
            if (lang.equals(Settings.language)) {
                return Settings.language.name().toLowerCase();
            }
        }
        return "eng";
    }

    public ModFile() {
        BaseMod.subscribe(this);
        Properties defaultSettings = new Properties();
        defaultSettings.setProperty(ENABLE_MOD, Boolean.toString(modEnabled));
        defaultSettings.setProperty(LEGACY_MODE, Boolean.toString(legacyMode));
        defaultSettings.setProperty(MOUSE_RADIUS, String.valueOf(mouseRadius));
        defaultSettings.setProperty(MONEY_BUSH_LEGACY_MODE, Boolean.toString(moneyBushLegacyMode));
        defaultSettings.setProperty(SOUND_RELIC_ENABLED, Boolean.toString(soundRelicEnabled));
        defaultSettings.setProperty(SOUND_RELIC_MODE, soundRelicMode.name());
        defaultSettings.setProperty(SOUND_RELIC_PICK, soundRelicPick);


        try {
            LOConfig = new SpireConfig(modID, FILE_NAME, defaultSettings);
            loadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadConfig() {
        try {
            modEnabled = LOConfig.getBool(ENABLE_MOD);
            legacyMode = LOConfig.getBool(LEGACY_MODE);
            mouseRadius = LOConfig.getInt(MOUSE_RADIUS);
            moneyBushLegacyMode = LOConfig.getBool(MONEY_BUSH_LEGACY_MODE);

            soundRelicEnabled = LOConfig.getBool(SOUND_RELIC_ENABLED);
            soundRelicMode = SoundMode.valueOf(LOConfig.getString(SOUND_RELIC_MODE));
            soundRelicPick = LOConfig.getString(SOUND_RELIC_PICK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static boolean moneyBushLegacyMode = false; // Add a boolean to track MoneyBush legacy mode
    private static final String MONEY_BUSH_LEGACY_MODE = "MoneyBushLegacyMode"; // Add a constant for config

    private void saveConfig() {
        try {
            LOConfig.setBool(ENABLE_MOD, modEnabled);
            LOConfig.setBool(LEGACY_MODE, legacyMode);
            LOConfig.setInt(MOUSE_RADIUS, mouseRadius);
            LOConfig.setBool(MONEY_BUSH_LEGACY_MODE, moneyBushLegacyMode);

            LOConfig.setBool(SOUND_RELIC_ENABLED, soundRelicEnabled);
            LOConfig.setString(SOUND_RELIC_MODE, soundRelicMode.name());
            LOConfig.setString(SOUND_RELIC_PICK, soundRelicPick);

            LOConfig.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String makePath(String resourcePath) {
        return modID + "Resources/" + resourcePath;
    }

    public static String makeImagePath(String resourcePath) {
        return modID + "Resources/images/" + resourcePath;
    }

    public static String makeRelicPath(String resourcePath) {
        return modID + "Resources/images/relics/" + resourcePath;
    }

    public static String makePowerPath(String resourcePath) {
        return modID + "Resources/images/powers/" + resourcePath;
    }

    public static String makeCharacterPath(String resourcePath) {
        return modID + "Resources/images/char/" + resourcePath;
    }

    public static String makeCardPath(String resourcePath) {
        return modID + "Resources/images/cards/" + resourcePath;
    }
    private static class ConfigDropdown implements basemod.IUIElement,
            com.megacrit.cardcrawl.screens.options.DropdownMenuListener {

        private final String label;
        private final float x, y;
        private final com.megacrit.cardcrawl.screens.options.DropdownMenu menu;
        private final java.util.function.IntConsumer onChange;
        private boolean enabled = true;
        private boolean visible = true;

        ConfigDropdown(String label, float x, float y, String[] items, int selectedIdx,
                       java.util.function.IntConsumer onChange) {
            this.label = label;
            this.x = x;
            this.y = y;
            this.onChange = onChange;

            java.util.ArrayList<String> options =
                    new java.util.ArrayList<>(java.util.Arrays.asList(items));
            this.menu = new com.megacrit.cardcrawl.screens.options.DropdownMenu(
                    this, options,
                    com.megacrit.cardcrawl.helpers.FontHelper.cardTitleFont,
                    com.megacrit.cardcrawl.core.Settings.CREAM_COLOR
            );
            this.menu.setSelectedIndex(Math.max(0, Math.min(selectedIdx, options.size() - 1)));
        }

        public void setEnabled(boolean v) { this.enabled = v; }
        public void setVisible(boolean v) { this.visible = v; }

        @Override
        public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch sb) {
            if (!visible) return;
            // tighter label spacing
            com.megacrit.cardcrawl.helpers.FontHelper.renderFontLeft(
                    sb, com.megacrit.cardcrawl.helpers.FontHelper.charDescFont,
                    label, x, y + 28f,
                    com.megacrit.cardcrawl.core.Settings.CREAM_COLOR
            );
            menu.render(sb, x, y);
        }

        @Override public void update() { if (visible && enabled) menu.update(); }
        @Override public int renderLayer() { return 0; }
        @Override public int updateOrder() { return 0; }

        @Override
        public void changedSelectionTo(com.megacrit.cardcrawl.screens.options.DropdownMenu menu,
                                       int index, String option) {
            if (onChange != null) onChange.accept(index);
        }
    }
    public static void initialize() {
        ModFile thismod = new ModFile();
    }

    @Override
    public void receiveEditCharacters() {
    }

    @Override
    public void receiveEditRelics() {
        new AutoAdd(modID)
                .packageFilter(AbstractEasyRelic.class)
                .any(AbstractEasyRelic.class, (info, relic) -> {
                    if (relic.color == null) {
                        BaseMod.addRelic(relic, RelicType.SHARED);
                    } else {
                        BaseMod.addRelicToCustomPool(relic, relic.color);
                    }
                    if (!info.seen) {
                        UnlockTracker.markRelicAsSeen(relic.relicId);
                    }
                });
        BaseMod.addRelic(new DarklightsStone(), RelicType.SHARED);
        UnlockTracker.markRelicAsSeen(DarklightsStone.ID);
    }

    @Override
    public void receiveEditCards() {
        BaseMod.addCard(new RatRaceCard());
        UnlockTracker.unlockCard(RatRaceCard.ID);
        BaseMod.addCard(new PirateRaidCard());
        UnlockTracker.unlockCard(PirateRaidCard.ID);
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(CardStrings.class, makePath("localization/" + getLangString() + "/Cardstrings.json"));
        BaseMod.loadCustomStringsFile(RelicStrings.class, makePath("localization/" + getLangString() + "/Relicstrings.json"));
        BaseMod.loadCustomStringsFile(CharacterStrings.class, makePath("localization/" + getLangString() + "/Charstrings.json"));
        BaseMod.loadCustomStringsFile(PowerStrings.class, makePath("localization/" + getLangString() + "/Powerstrings.json"));
        BaseMod.loadCustomStringsFile(UIStrings.class, makePath("localization/" + getLangString() + "/UIstrings.json"));
        BaseMod.loadCustomStringsFile(OrbStrings.class, makePath("localization/" + getLangString() + "/Orbstrings.json"));
        BaseMod.loadCustomStringsFile(StanceStrings.class, makePath("localization/" + getLangString() + "/Stancestrings.json"));
        BaseMod.loadCustomStringsFile(PotionStrings.class, makePath("localization/" + getLangString() + "/Potionstrings.json"));
    }

    @Override
    public void receiveAddAudio() {
        for (ProAudio a : ProAudio.values())
            BaseMod.addAudio(makeID(a.name()), makePath("audio/" + a.name().toLowerCase() + ".ogg"));
    }

    @Override
    public void receiveEditKeywords() {
        Gson gson = new Gson();
        String json = Gdx.files.internal(makePath("localization/" + getLangString() + "/Keywordstrings.json")).readString(String.valueOf(StandardCharsets.UTF_8));
        Keyword[] keywords = gson.fromJson(json, Keyword[].class);

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(modID, keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }
    @Override
    public void receivePostDungeonInitialize() {
        if (!soundRelicEnabled || AbstractDungeon.player == null) return;

        if (!AbstractDungeon.player.hasRelic(puppyrelics.relics.Kazoo.ID)) {
            AbstractDungeon.player.relics.add(new puppyrelics.relics.Kazoo());
            AbstractDungeon.player.reorganizeRelics();
        }
        if (soundRelicMode == SoundMode.RANDOM_RUN) {
            AbstractRelic r = AbstractDungeon.player.getRelic(puppyrelics.relics.Kazoo.ID);
            if (r instanceof puppyrelics.relics.Kazoo) {
                ((puppyrelics.relics.Kazoo) r).rollRandom();
            }
        }
    }

    @Override
    public void receivePostInitialize() {
        uiStrings = CardCrawlGame.languagePack.getUIString("puppyrelics:ModConfig");
        if (uiStrings != null && uiStrings.TEXT != null) {
            TEXT = uiStrings.TEXT;
        }

        loadConfig();
        try {
            legacyMode = LOConfig.getBool(LEGACY_MODE);
            moneyBushLegacyMode = LOConfig.getBool(MONEY_BUSH_LEGACY_MODE); // Load MoneyBush legacy mode
        } catch (Exception e) {
            e.printStackTrace();
        }

        ModPanel settingsPanel = new ModPanel();
        ModLabeledToggleButton legacyToggleButton = new ModLabeledToggleButton(
                TEXT[0],
                350.0f,
                750.0f,
                Settings.CREAM_COLOR,
                FontHelper.charDescFont,
                legacyMode,
                settingsPanel,
                (label) -> {
                },
                (button) -> {
                    legacyMode = button.enabled;

                    // Check the state of legacyMode
                    if (legacyMode) {
                        NikTheGOAT nikTheGoatLegacyRelic = (NikTheGOAT) RelicLibrary.getRelic(NikTheGOAT.ID);
                        if (nikTheGoatLegacyRelic != null) {
                            nikTheGoatLegacyRelic.refreshDescription();
                        }
                    } else {
                        NikTheGOAT nikTheGoatLegacyRelic = (NikTheGOAT) RelicLibrary.getRelic(NikTheGOAT.ID);
                        if (nikTheGoatLegacyRelic != null) {
                            nikTheGoatLegacyRelic.refreshDescription();
                        }
                    }

                    try {
                        LOConfig.setBool(LEGACY_MODE, legacyMode);
                        LOConfig.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
        settingsPanel.addUIElement(legacyToggleButton);
        ModLabeledToggleButton legacyMoneyBushToggleButton = new ModLabeledToggleButton(
                TEXT[1],
                350.0f,
                700.0f, // Adjust position
                Settings.CREAM_COLOR,
                FontHelper.charDescFont,
                moneyBushLegacyMode,
                settingsPanel,
                (label) -> {
                },
                (button) -> {
                    moneyBushLegacyMode = button.enabled;

                    // Check the state of legacyMode
                    if (moneyBushLegacyMode) {
                        MoneyBush moneyBushLegacyRelic = (MoneyBush) RelicLibrary.getRelic(MoneyBush.ID);
                        if (moneyBushLegacyRelic != null) {
                            moneyBushLegacyRelic.refreshDescription();
                        }
                    } else {
                        MoneyBush moneyBushLegacyRelic = (MoneyBush) RelicLibrary.getRelic(MoneyBush.ID);
                        if (moneyBushLegacyRelic != null) {
                            moneyBushLegacyRelic.refreshDescription();
                        }
                    }
                    // Save the MoneyBush legacy mode state
                    try {
                        LOConfig.setBool(MONEY_BUSH_LEGACY_MODE, moneyBushLegacyMode);
                        LOConfig.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
        settingsPanel.addUIElement(legacyMoneyBushToggleButton);

        final float yToggle = 650f;
        final float yMode   = 595f* Settings.scale;
        final float ySound  = 550f* Settings.scale;

// SOUND dropdown (all ProAudio values)
        String[] soundLabels = java.util.Arrays.stream(ProAudio.values())
                .map(Enum::name)
                .toArray(String[]::new);
        int initialSoundIdx = Math.max(0, java.util.Arrays.asList(soundLabels).indexOf(soundRelicPick));

        final ConfigDropdown soundDrop = new ConfigDropdown(
                TEXT[2], 350.0f* Settings.scale, ySound, soundLabels, initialSoundIdx,
                (idx) -> {
                    soundRelicPick = soundLabels[idx];
                    try {
                        LOConfig.setString(SOUND_RELIC_PICK, soundRelicPick);
                        LOConfig.save();
                    } catch (Exception e) { e.printStackTrace(); }
                }
        );
        settingsPanel.addUIElement(soundDrop);

// MODE dropdown
        String[] modeLabels = new String[]{
                TEXT[4], // Specific sound
                TEXT[5], // Random — every click
                TEXT[6], // Random — every room
                TEXT[7], // Random — new act
                TEXT[8]  // Random — every run
        };
        final ConfigDropdown modeDrop = new ConfigDropdown(
                TEXT[3], 350.0f* Settings.scale, yMode, modeLabels, soundRelicMode.ordinal(),
                (idx) -> {
                    soundRelicMode = SoundMode.values()[idx];
                    // show Sound picker only in SPECIFIC mode
                    soundDrop.setVisible(soundRelicEnabled && soundRelicMode == SoundMode.SPECIFIC);
                    try {
                        LOConfig.setString(SOUND_RELIC_MODE, soundRelicMode.name());
                        LOConfig.save();
                    } catch (Exception e) { e.printStackTrace(); }
                }
        );
        settingsPanel.addUIElement(modeDrop);

// initial visibility
        modeDrop.setVisible(soundRelicEnabled);
        soundDrop.setVisible(soundRelicEnabled && soundRelicMode == SoundMode.SPECIFIC);

// TOGGLE (after dropdowns so it can reference them)
        ModLabeledToggleButton kazooToggle = new ModLabeledToggleButton(
                TEXT[9],
                350.0f, yToggle,
                Settings.CREAM_COLOR, FontHelper.charDescFont,
                soundRelicEnabled, settingsPanel,
                (label) -> {},
                (button) -> {
                    soundRelicEnabled = button.enabled;
                    try {
                        LOConfig.setBool(SOUND_RELIC_ENABLED, soundRelicEnabled);
                        LOConfig.save();
                    } catch (Exception e) { e.printStackTrace(); }
                    // show/hide both dropdowns with the checkbox
                    modeDrop.setVisible(soundRelicEnabled);
                    soundDrop.setVisible(soundRelicEnabled && soundRelicMode == SoundMode.SPECIFIC);
                }
        );
        settingsPanel.addUIElement(kazooToggle);

        Texture badgeTexture = new Texture(Gdx.files.internal("puppyrelicsResources/images/ui/badge.png"));
        BaseMod.registerModBadge(
                badgeTexture,
                "Puppy Relics",
                "Ninja Puppy",
                TEXT[10], // now the description
                settingsPanel
        );
        // Register the global glow logic for cards with the BurningBridge relic
        CardBorderGlowManager.addGlowInfo(new CardBorderGlowManager.GlowInfo() {
            @Override
            public boolean test(AbstractCard c) {
                return shouldGlowRed(c);
            }

            @Override
            public Color getColor(AbstractCard c) {
                return Color.RED.cpy(); // Return the red color for the glow
            }

            @Override
            public String glowID() {
                return "puppyrelics:burningbridgeglow"; // Unique ID for this glow
            }
        });
    }
    private boolean shouldGlowRed(AbstractCard card) {
        // Ensure that the player exists and has the BurningBridge relic
        if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(BurningBridge.ID)) {
            int availableEnergy = EnergyPanel.totalCount;
            int energyDeficit = card.costForTurn - availableEnergy;

            // Use freeToPlay() instead of costForTurn and freeToPlayOnce checks
            return energyDeficit > 0 && !card.freeToPlay();
        }
        return false;
    }
}
