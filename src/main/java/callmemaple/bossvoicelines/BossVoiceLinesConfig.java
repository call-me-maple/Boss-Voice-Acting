package callmemaple.bossvoicelines;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("bossvoicelines")
public interface BossVoiceLinesConfig extends Config
{
    @Range(
            max = 200
    )
    @ConfigItem(
            keyName = "volume",
            name = "Volume",
            description = "Adjust how loud the voice lines are. Range 0 to 200",
            position = 0
    )
    default int volume() {
        return 100;
    }
}
