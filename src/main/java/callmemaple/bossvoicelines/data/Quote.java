package callmemaple.bossvoicelines.data;

import com.google.common.collect.ImmutableSet;
import net.runelite.client.RuneLite;

import java.io.File;
import java.util.Set;

import static callmemaple.bossvoicelines.data.Boss.*;

public class Quote
{
    public static final Set<Quote> QUOTES = ImmutableSet.of(
            new Quote(CHAOS_FANATIC, "BURN!", "burn test.wav"),
            new Quote(CHAOS_FANATIC, "WEUGH!", "burn test.wav"),
            new Quote(CHAOS_FANATIC, "Devilish Oxen Roll!", "burn test.wav"),
            new Quote(CHAOS_FANATIC, "All your wilderness are belong to them!", "burn test.wav"),
            new Quote(CHAOS_FANATIC, "AhehHeheuhHhahueHuUEehEahAH", "burn test.wav"),
            new Quote(CHAOS_FANATIC, "I shall call him squidgy and he shall be my squidgy!", "burn test.wav"),

            new Quote(DERANGED_ARCHAEOLOGIST, "Round and round and round and round!", "burn test.wav"),
            new Quote(DERANGED_ARCHAEOLOGIST, "The plants! They're alive!", "burn test.wav"),
            new Quote(DERANGED_ARCHAEOLOGIST, "They came from the ground! They came from the ground!!!", "burn test.wav"),
            new Quote(DERANGED_ARCHAEOLOGIST, "Learn to Read!", "burn test.wav"),
            new Quote(DERANGED_ARCHAEOLOGIST, "The doors won't stay closed forever!", "burn test.wav"),
            new Quote(DERANGED_ARCHAEOLOGIST, "They're cheering! Why are they cheering?", "burn test.wav"),
            new Quote(DERANGED_ARCHAEOLOGIST, "Time is running out! She will rise again!!!", "burn test.wav"),
            new Quote(DERANGED_ARCHAEOLOGIST, "No hiding!", "burn test.wav"),
            new Quote(DERANGED_ARCHAEOLOGIST, "Oh!", "burn test.wav"),

            new Quote(CHAOS_ARCHAEOLOGIST, "I'm Bellock - respect me!", "burn test.wav"),
            new Quote(CHAOS_ARCHAEOLOGIST, "Get off my site!", "burn test.wav"),
            new Quote(CHAOS_ARCHAEOLOGIST, "No-one messes with Bellock's dig!", "burn test.wav"),
            new Quote(CHAOS_ARCHAEOLOGIST, "These ruins are mine!", "burn test.wav"),
            new Quote(CHAOS_ARCHAEOLOGIST, "Taste my knowledge!", "burn test.wav"),
            new Quote(CHAOS_ARCHAEOLOGIST, "You belong in a museum!", "burn test.wav"),
            new Quote(CHAOS_ARCHAEOLOGIST, "Rain of knowledge!", "burn test.wav"),
            new Quote(CHAOS_ARCHAEOLOGIST, "Ow!", "burn test.wav")
    );


    public Boss boss;
    public String line;
    public String filename;

    public Quote(Boss boss, String line, String filename)
    {
        this.boss = boss;
        this.line = line;
        this.filename = filename;
    }

    public File getFile()
    {
        String path = String.join(File.separator, RuneLite.RUNELITE_DIR.getPath(), "boss-voice-lines", boss.folderName, filename);
        return new File(path);
    }

    public static Quote findQuote(Boss boss, String line)
    {
        if (boss == UNKNOWN)
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