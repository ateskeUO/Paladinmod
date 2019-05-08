package Paladin.cards;

        import Paladin.PaladinMod;
        import Paladin.characters.Paladin;
        import Paladin.powers.DevotionPower;
        import basemod.abstracts.CustomCard;
        import basemod.helpers.BaseModCardTags;
        import com.megacrit.cardcrawl.actions.AbstractGameAction;
        import com.megacrit.cardcrawl.actions.common.DamageAction;
        import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
        import com.megacrit.cardcrawl.cards.DamageInfo;
        import com.megacrit.cardcrawl.characters.AbstractPlayer;
        import com.megacrit.cardcrawl.core.CardCrawlGame;
        import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
        import com.megacrit.cardcrawl.localization.CardStrings;
        import com.megacrit.cardcrawl.monsters.AbstractMonster;

        import static Paladin.PaladinMod.makeCardPath;

public class TemplarsVerdict extends CustomCard {

    /*
     * Wiki-page: https://github.com/daviscook477/BaseMod/wiki/Custom-Cards
     *
     * Strike Deal 10 damage. If you have 3(2) or more Devotion, spend it and deal 25 instead.
     */

    // TEXT DECLARATION

    public static final String ID = PaladinMod.makeID("TemplarsVerdict");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public static final String IMG = makeCardPath("TemplarsVerdict.png");


    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;

    // /TEXT DECLARATION/


    // STAT DECLARATION

    private static final CardRarity RARITY = CardRarity.BASIC;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final CardType TYPE = CardType.ATTACK;
    public static final CardColor COLOR = Paladin.Enums.COLOR_GRAY;

    private static final int COST = 2;
    private static final int DAMAGE = 10;
    private static final int MAGIC = 3;
    private static final int UPGRADE_MAGIC_NUMBER = -1;
    private static final int ACTIVATED_DAMAGE = 25;


    // /STAT DECLARATION/

    public TemplarsVerdict() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);


        baseDamage = DAMAGE;

        this.tags.add(BaseModCardTags.BASIC_STRIKE); //Tag your strike, defend and form (Shadow form, demon form, echo form, etc.) cards so that they function correctly.
        this.tags.add(CardTags.STRIKE);

        this.magicNumber = baseMagicNumber = MAGIC;

    }

    @Override
    public void applyPowers() {
        int originalBaseDamage = this.baseDamage; //Track the original base damage.

        this.baseDamage = ACTIVATED_DAMAGE; //First set base damage to the damage when active; this will be used during calculation
        super.applyPowers(); //Calculate what the final result of this damage will be based on the player's powers

        int activeDamage = this.damage; //Store the calculated result.


        this.baseDamage = originalBaseDamage; //Set base damage back to the original base damage.
        super.applyPowers(); //Calculate the final damage for base damage.


        this.baseBlock = ACTIVATED_DAMAGE; //Store the active damage in card variables for display.
        this.block = activeDamage;
        this.isBlockModified = activeDamage != ACTIVATED_DAMAGE;
        //Note that card variables will only be automatically modified in applyPowers; there should be no issue using the block variables for this purpose. If this makes you uncomfortable though,
        //look up dynamic variables on the basemod wiki and use your own variables to store these values.

        //The final card description would be something like Deal !D! damage. NL If you have !M! or more Devotion, spend it and deal !B! instead.
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        //Same thing as applyPowers, but this calculation includes the target enemy's powers, such as Vulnerable. This is used when hovering on a target with a card.
        int originalBaseDamage = this.baseDamage;

        this.baseDamage = ACTIVATED_DAMAGE;
        super.calculateCardDamage(mo);

        int activeDamage = this.damage;


        this.baseDamage = originalBaseDamage;
        super.calculateCardDamage(mo);


        this.baseBlock = ACTIVATED_DAMAGE;
        this.block = activeDamage;
        this.isBlockModified = activeDamage != ACTIVATED_DAMAGE;
    }

    // Actions the card should do.
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if ((AbstractDungeon.player.hasPower(DevotionPower.POWER_ID)) && AbstractDungeon.player.getPower(DevotionPower.POWER_ID).amount >= this.magicNumber) {
            AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(AbstractDungeon.player, AbstractDungeon.player, DevotionPower.POWER_ID, this.magicNumber));
            AbstractDungeon.actionManager.addToBottom(
                    new DamageAction(m, new DamageInfo(p, block, damageTypeForTurn), // Use block variable, because that's where the activated damage was stored.
                            AbstractGameAction.AttackEffect.FIRE));
        }
        else {
            AbstractDungeon.actionManager.addToBottom( // Normal damage.
                    new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn),
                            AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));

        }
    }


    // Upgraded stats.
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_MAGIC_NUMBER);
            initializeDescription();
        }
    }
}
