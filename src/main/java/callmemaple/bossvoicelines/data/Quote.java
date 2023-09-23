package callmemaple.bossvoicelines.data;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Set;

import static callmemaple.bossvoicelines.BossVoiceLinesPlugin.AUDIO_DIRECTORY;
import static callmemaple.bossvoicelines.data.Boss.*;

@Getter
public class Quote
{
    public static final Set<Quote> QUOTES = ImmutableSet.of(
            new Quote(CERBERUS, "Grrrrrrrrrrrrrr", "Growl.wav"),
            new Quote(CERBERUS, "Aaarrrooooooo", "Arooo.wav"),
            new Quote(CERBERUS_GHOSTS, "I obey.", "SoulIObey.wav"),
            new Quote(CERBERUS_GHOSTS, "Join us.", "SoulJoinUs.wav"),
            new Quote(CERBERUS_GHOSTS, "Steal your soul.", "SoulStealYourSoul.wav"),

            new Quote(COMMANDER_ZILYANA, "All praise Saradomin!", "AllPraise.wav"),
            new Quote(COMMANDER_ZILYANA, "Death to the enemies of the light!", "DeathLight.wav"),
            new Quote(COMMANDER_ZILYANA, "Attack! Find the Godsword!", "FindTheGodsword.wav"),
            new Quote(COMMANDER_ZILYANA, "Forward! Our allies are with us!", "Forward!.wav"),
            new Quote(COMMANDER_ZILYANA, "Good will always triumph!", "GoodWill.wav"),
            new Quote(COMMANDER_ZILYANA, "In the name of Saradomin!", "InTheName.wav"),
            new Quote(COMMANDER_ZILYANA, "Saradomin lend me strength!", "LendMe.wav"),
            new Quote(COMMANDER_ZILYANA, "May Saradomin be my sword!", "MaySaradomin.wav"),
            new Quote(COMMANDER_ZILYANA, "Slay the evil ones!", "SlayEvil.wav"),
            new Quote(COMMANDER_ZILYANA, "Saradomin is with us!", "WithUs.wav"),

            new Quote(GENERAL_GRAARDOR, "Death to our enemies!", "DeathTo.wav"),
            new Quote(GENERAL_GRAARDOR, "Brargh!", "Brargh.wav"),
            new Quote(GENERAL_GRAARDOR, "Break their bones!", "BreakTheir.wav"),
            new Quote(GENERAL_GRAARDOR, "Split their skulls!", "SplitTheir.wav"),
            new Quote(GENERAL_GRAARDOR, "We feast on the bones of our enemies tonight!", "WeFeast.wav"),
            new Quote(GENERAL_GRAARDOR, "CHAAARGE!", "Charge.wav"),
            new Quote(GENERAL_GRAARDOR, "Crush them underfoot!", "CrushThem.wav"),
            new Quote(GENERAL_GRAARDOR, "All glory to Bandos!", "AllGlory.wav"),
            new Quote(GENERAL_GRAARDOR, "GRRRAAAAAR!", "GRRRAA.wav"),
            new Quote(GENERAL_GRAARDOR, "For the glory of the Big High War God!", "ForTheGlory.wav"),

            new Quote(KRIL_TSUTSAROTH, "Kill them, you cowards!", "KillThem.wav"),
            new Quote(KRIL_TSUTSAROTH, "Zamorak curse them!", "CurseThem.wav"),
            new Quote(KRIL_TSUTSAROTH, "Death to Saradomin's dogs!", "SaraDogs.wav"),
            new Quote(KRIL_TSUTSAROTH, "Rend them limb from limb!", "RendThem.wav"),
            new Quote(KRIL_TSUTSAROTH, "Flay them all!", "FlayThem.wav"),
            new Quote(KRIL_TSUTSAROTH, "Attack them, you dogs!", "AttackThem.wav"),
            new Quote(KRIL_TSUTSAROTH, "Forward!", "Forward.wav"),
            new Quote(KRIL_TSUTSAROTH, "Attack!", "Attack!.wav"),
            new Quote(KRIL_TSUTSAROTH, "The Dark One will have their souls!", "DarkOne.wav"),
            new Quote(KRIL_TSUTSAROTH, "No retreat!", "NoRetreat.wav"),
            new Quote(KRIL_TSUTSAROTH, "YARRRRRRR!", "YARRRR.wav"),

            new Quote(KREEARRA, "Skreeeee!", "Skreeee.wav"),
            new Quote(KREEARRA, "Kraaaw!", "Kraaaw.wav"),

            new Quote(NEX, "AT LAST!", "AtLast.wav"),
            new Quote(NEX, "Fumus!", "Minions.wav"), // This file reads out all the minion's names starting with Fumus
            new Quote(NEX, "I demand a blood sacrifice!", "BloodSacrifice.wav"),
            new Quote(NEX, "Contain this!", "ContainThis.wav"),
            new Quote(NEX, "Cruor, don't fail me!", "CruorFail.wav"),
            new Quote(NEX, "Darken my shadow!", "DarkShadow.wav"),
            new Quote(NEX, "Embrace darkness!", "EmbraceDarkness.wav"),
            new Quote(NEX, "Fear the shadow!", "FearShadow.wav"),
            new Quote(NEX, "Fill my soul with smoke!", "FillSmoke.wav"),
            new Quote(NEX, "Flood my lungs with blood!", "FloodLungs.wav"),
            new Quote(NEX, "Fumus, don't fail me!", "FumusFail.wav"),
            new Quote(NEX, "Glacies, don't fail me!", "GlaciesFail.wav"),
            new Quote(NEX, "Infuse me with the power of ice!", "InfuseIce.wav"),
            new Quote(NEX, "There is... NO ESCAPE!", "NoEscape.wav"),
            new Quote(NEX, "NOW, THE POWER OF ZAROS!", "PowerZaros.wav"),
            new Quote(NEX, "Die now, in a prison of ice!", "PrisonIce.wav"),
            new Quote(NEX, "A siphon will solve this!", "Siphon.wav"),
            new Quote(NEX, "Taste my wrath!", "TasteMyWrath.wav"),
            new Quote(NEX, "Umbra, don't fail me!", "UmbraFail.wav"),
            new Quote(NEX, "Let the virus flow through you!", "Virus.wav"),

            new Quote(AHRIM, "You dare disturb my rest!", "AHRest.wav"),
            new Quote(AHRIM, "You dare steal from us!", "AHSteal.wav"),
            new Quote(DHAROK, "You dare disturb my rest!", "DHRest.wav"),
            new Quote(DHAROK, "You dare steal from us!", "DHSteal.wav"),
            new Quote(GUTHAN, "You dare disturb my rest!", "GURest.wav"),
            new Quote(GUTHAN, "You dare steal from us!", "GUSteal.wav"),
            new Quote(KARIL, "You dare disturb my rest!", "KARest.wav"),
            new Quote(KARIL, "You dare steal from us!", "KASteal.wav"),
            new Quote(TORAG, "You dare disturb my rest!", "TORest.wav"),
            new Quote(TORAG, "You dare steal from us!", "TORSteal.wav"),
            new Quote(VERAC, "You dare disturb my rest!", "VERest.wav"),
            new Quote(VERAC, "You dare steal from us!", "VESteal.wav"),

            new Quote(CHAOS_FANATIC, "BURN!", "Burn.wav"),
            new Quote(CHAOS_FANATIC, "WEUGH!", "Weugh!.wav"),
            new Quote(CHAOS_FANATIC, "Devilish Oxen Roll!", "DevilishOxen.wav"),
            new Quote(CHAOS_FANATIC, "All your wilderness are belong to them!", "AllYourWilderness.wav"),
            new Quote(CHAOS_FANATIC, "AhehHeheuhHhahueHuUEehEahAH", "AhehHeheu.wav"),
            new Quote(CHAOS_FANATIC, "I shall call him squidgy and he shall be my squidgy!", "Squidgy.wav"),

            new Quote(DERANGED_ARCHAEOLOGIST, "Round and round and round and round!", "Round.wav"),
            new Quote(DERANGED_ARCHAEOLOGIST, "The plants! They're alive!", "Alive.wav"),
            new Quote(DERANGED_ARCHAEOLOGIST, "They came from the ground! They came from the ground!!!", "Ground.wav"),
            new Quote(DERANGED_ARCHAEOLOGIST, "Learn to Read!", "Read.wav"),
            new Quote(DERANGED_ARCHAEOLOGIST, "The doors won't stay closed forever!", "Doors.wav"),
            new Quote(DERANGED_ARCHAEOLOGIST, "They're cheering! Why are they cheering?", "Cheering.wav"),
            new Quote(DERANGED_ARCHAEOLOGIST, "Time is running out! She will rise again!!!", "Time.wav"),
            new Quote(DERANGED_ARCHAEOLOGIST, "No hiding!", "NoHiding.wav"),
            new Quote(DERANGED_ARCHAEOLOGIST, "Oh!", "Oh!.wav"),

            new Quote(CHAOS_ARCHAEOLOGIST, "I'm Bellock - respect me!", "Bellock.wav"),
            new Quote(CHAOS_ARCHAEOLOGIST, "Get off my site!", "GetOff.wav"),
            new Quote(CHAOS_ARCHAEOLOGIST, "No-one messes with Bellock's dig!", "Mess.wav"),
            new Quote(CHAOS_ARCHAEOLOGIST, "These ruins are mine!", "Ruins.wav"),
            new Quote(CHAOS_ARCHAEOLOGIST, "Taste my knowledge!", "Taste.wav"),
            new Quote(CHAOS_ARCHAEOLOGIST, "You belong in a museum!", "Museum.wav"),
            new Quote(CHAOS_ARCHAEOLOGIST, "Rain of knowledge!", "RainOfKnowledge.wav"),
            new Quote(CHAOS_ARCHAEOLOGIST, "Ow!", "Ow!.wav"),

            new Quote(CALVARION, "I will smite you!", "Smite!.wav"),
            new Quote(CALVARION, "I've got you now!", "GotYou.wav"),
            new Quote(CALVARION, "Stand still, rat!", "StillRat.wav"),
            new Quote(CALVARION, "You can't escape!", "CantEscape.wav"),
            new Quote(CALVARION, "For the lord!", "ForTheLord.wav"),
            new Quote(CALVARION, "You call that a weapon?!", "Weapon.wav"),
            new Quote(CALVARION, "Now I've got you!", "NowGot.wav"),
            new Quote(CALVARION, "Hands off, wretch!", "HandsOff.wav"),
            new Quote(CALVARION, "Grrrah!", "Grrrah!.wav"),
            new Quote(CALVARION, "Time to feast, hounds!", "TimeToFeast.wav"),
            new Quote(CALVARION, "Now... DO IT AGAIN!!!", "DoItAgain.wav"),
            new Quote(CALVARION, "DODGE THIS!", "DodgeThis.wav"),
            new Quote(CALVARION, "PERISH, FOOL!", "PerishFool.wav"),
            new Quote(CALVARION, "YOU ARE POWERLESS TO ME!", "Powerless.wav"),
            new Quote(CALVARION, "TIME TO DIE, MORTAL!", "TimeToDie.wav"),
            new Quote(CALVARION, "FILTHY WHELPS!", "FilthyWelps.wav"),
            new Quote(CALVARION, "YOU'RE NOT BLOCKING THIS ONE!", "NotBlocking.wav"),
            new Quote(CALVARION, "DEFEND YOURSELF!", "DefendYourself.wav"),
            new Quote(CALVARION, "Must I do everything around here?!", "MustIDo.wav"),
            new Quote(CALVARION, "I'LL KILL YOU FOR KILLING MY PETS!", "KillingMyPets.wav"),
            new Quote(CALVARION, "Urgh... not... again...", "NotAgain.wav"),
            new Quote(CALVARION, "Urk! I... failed...", "IFailed.wav"),
            new Quote(CALVARION, "I'll... be... back...", "BeBack.wav"),
            new Quote(CALVARION, "This isn't... the last... of me...", "LastOfMe.wav"),
            new Quote(CALVARION, "My lord... I'm... sorry...", "MyLordSorry.wav"),
            new Quote(CALVARION, "I'll get you... next... time...", "NextTime.wav"),

            new Quote(VETION, "I will smite you!", "Smite!.wav"),
            new Quote(VETION, "I've got you now!", "GotYou.wav"),
            new Quote(VETION, "Stand still, rat!", "StillRat.wav"),
            new Quote(VETION, "You can't escape!", "CantEscape.wav"),
            new Quote(VETION, "For the lord!", "ForTheLord.wav"),
            new Quote(VETION, "You call that a weapon?!", "Weapon.wav"),
            new Quote(VETION, "Now I've got you!", "NowGot.wav"),
            new Quote(VETION, "Hands off, wretch!", "HandsOff.wav"),
            new Quote(VETION, "Grrrah!", "Grrrah!.wav"),
            new Quote(VETION, "Time to feast, hounds!", "TimeToFeast.wav"),
            new Quote(VETION, "Now... DO IT AGAIN!!!", "DoItAgain.wav"),
            new Quote(VETION, "DODGE THIS!", "DodgeThis.wav"),
            new Quote(VETION, "PERISH, FOOL!", "PerishFool.wav"),
            new Quote(VETION, "YOU ARE POWERLESS TO ME!", "Powerless.wav"),
            new Quote(VETION, "TIME TO DIE, MORTAL!", "TimeToDie.wav"),
            new Quote(VETION, "FILTHY WHELPS!", "FilthyWelps.wav"),
            new Quote(VETION, "YOU'RE NOT BLOCKING THIS ONE!", "NotBlocking.wav"),
            new Quote(VETION, "DEFEND YOURSELF!", "DefendYourself.wav"),
            new Quote(VETION, "Must I do everything around here?!", "MustIDo.wav"),
            new Quote(VETION, "I'LL KILL YOU FOR KILLING MY PETS!", "KillingMyPets.wav"),
            new Quote(VETION, "Urgh... not... again...", "NotAgain.wav"),
            new Quote(VETION, "Urk! I... failed...", "IFailed.wav"),
            new Quote(VETION, "I'll... be... back...", "BeBack.wav"),
            new Quote(VETION, "This isn't... the last... of me...", "LastOfMe.wav"),
            new Quote(VETION, "My lord... I'm... sorry...", "MyLordSorry.wav"),
            new Quote(VETION, "I'll get you... next... time...", "NextTime.wav")
    );

    private final Boss boss;
    private final String line;
    private final String filename;

    public Quote(Boss boss, String line, String filename)
    {
        this.boss = boss;
        this.line = line;
        this.filename = filename;
    }

    /**
     * 	Return the location of the Quote's audio file as a File object
     */
    public File getFile()
    {
        String path = String.join(File.separator, AUDIO_DIRECTORY, boss.getFolderName(), filename);
        return new File(path);
    }

    /**
     * 	Search for the Quote based on the boss and their line
     */
    @Nullable
    public static Quote findQuote(Boss boss, String line)
    {
        if (boss == null)
        {
            return null;
        }

        for (Quote quote : QUOTES)
        {
            if (quote.boss.equals(boss) && quote.line.equals(line))
            {
                return quote;
            }
        }
        return null;
    }
}
