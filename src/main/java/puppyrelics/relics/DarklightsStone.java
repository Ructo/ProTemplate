package puppyrelics.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.evacipated.cardcrawl.mod.stslib.relics.BetterOnSmithRelic;
import basemod.abstracts.CustomSavable;

import static puppyrelics.ModFile.makeID;

public class DarklightsStone extends AbstractRelic implements BetterOnSmithRelic, CustomSavable<Integer> {
    public static final String ID = makeID("DarklightsStone");
    private static final int UPGRADE_THRESHOLD = 1;
    private static final String IMG_PATH = "puppyrelicsResources/images/relics/";
    private int currentStage = 0;

    public DarklightsStone() {
        super(ID, IMG_PATH + "DarklightsStone_stage1.png", RelicTier.RARE, LandingSound.FLAT);
        loadImages(1); // Initialize the default image
    }

    @Override
    public void betterOnSmith(AbstractCard card) {
        int upgradeCount = getUpgradeCount();
        int stage = upgradeCount / UPGRADE_THRESHOLD;
        if (stage > currentStage) {
            currentStage = stage;
            evolveRelic(stage);
            updateDescription(upgradeCount);
            loadImages(stage);
        }
    }

    private int getUpgradeCount() {
        if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) {
            return 0;
        }
        return (int) AbstractDungeon.player.masterDeck.group.stream().filter(card -> card.upgraded).count();
    }

    private void evolveRelic(int stage) {
        if (AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT) {
            return;
        }

        AbstractPlayer player = AbstractDungeon.player;
        if (stage >= 0) {
            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(player, 1));
        }
        if (stage >= 1) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(player, player, new StrengthPower(player, 1), 1));
        }
        if (stage >= 2) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(player, player, new DexterityPower(player, 1), 1));
        }
        if (stage >= 3) {
            player.gainEnergy(2);
        }
        flash();
    }

    @Override
    public void atBattleStart() {
        evolveRelic(currentStage);
        updateDescription(getUpgradeCount());
    }

    private void updateDescription(int upgradeCount) {
        int stage = upgradeCount / UPGRADE_THRESHOLD;
        StringBuilder descBuilder = new StringBuilder(DESCRIPTIONS[0]);

        if (stage >= 0) {
            descBuilder.append("#y").append(DESCRIPTIONS[1]).append(" NL ");
        } else {
            descBuilder.append(DESCRIPTIONS[1]).append(" NL ");
        }

        if (stage >= 1) {
            descBuilder.append("#y").append(DESCRIPTIONS[2]).append(" NL ");
        } else {
            descBuilder.append(DESCRIPTIONS[2]).append(" NL ");
        }

        if (stage >= 2) {
            descBuilder.append("#y").append(DESCRIPTIONS[3]).append(" NL ");
        } else {
            descBuilder.append(DESCRIPTIONS[3]).append(" NL ");
        }

        if (stage >= 3) {
            descBuilder.append("#y").append(DESCRIPTIONS[4]);
        } else {
            descBuilder.append(DESCRIPTIONS[4]);
        }

        this.description = descBuilder.toString();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        initializeTips();
    }

    private void loadImages(int stage) {
        String imgPath = IMG_PATH + "DarklightsStone_stage" + stage + ".png";
        this.img = ImageMaster.loadImage(imgPath);
        this.outlineImg = ImageMaster.loadImage(imgPath.replace(".png", "Outline.png"));
    }

    @Override
    public void onEquip() {
        updateDescription(getUpgradeCount());
        loadImages(currentStage);
    }

    @Override
    public Integer onSave() {
        return currentStage;
    }

    @Override
    public void onLoad(Integer stage) {
        if (stage != null) {
            currentStage = stage;
            loadImages(stage);
            updateDescription(getUpgradeCount());
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new DarklightsStone();
    }
}