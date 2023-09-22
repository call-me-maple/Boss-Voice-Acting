package callmemaple.bossvoicelines;

import callmemaple.bossvoicelines.data.Boss;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

import java.util.EnumSet;
import java.util.Set;

import static callmemaple.bossvoicelines.BossVoiceLinesPlugin.CONFIG_GROUP;

@ConfigGroup(CONFIG_GROUP)
public interface BossVoiceLinesConfig extends Config
{
    String VERSION_KEY = "version";

    @Range(
            max = 200,
            min = 1
    )
    @ConfigItem(
            keyName = "volume",
            name = "Volume",
            description = "Adjust how loud the voice lines are. Range 1 to 200",
            position = 1
    )
    default int getVolume() {
        return 100;
    }

    @ConfigSection(
            name = "Bosses",
            description = "Select which bosses to play voice lines for",
            position = 2,
            closedByDefault = true
    ) String bosses = "bosses";

    @ConfigItem(
            keyName = "enabledBosses",
            name = "Enabled",
            description = "Use ctrl+click to deselect a single one (similar functionality as Window's File Explorer selecting)",
            position = 2,
            section = bosses
    ) default Set<Boss> getEnabledBosses() {
        return EnumSet.allOf(Boss.class);
    }

    @ConfigItem(
            keyName = VERSION_KEY,
            name = "version",
            description = "Most recent version known of the plugin. The current version defined during build process.",
            hidden = true
    )
    default String getPreviousVersion() {
        return "1.0";
    }
}
