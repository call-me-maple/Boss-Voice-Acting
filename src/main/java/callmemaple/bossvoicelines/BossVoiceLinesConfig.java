package callmemaple.bossvoicelines;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("bossvoicelines")
public interface BossVoiceLinesConfig extends Config
{
    @ConfigItem(
            keyName = "volume",
            name = "Volume",
            description = "Adjust how loud the voice lines are.",
            position = 0
    )
    default int volume() {
        return 100;
    }
}
