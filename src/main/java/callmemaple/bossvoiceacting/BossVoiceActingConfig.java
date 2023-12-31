package callmemaple.bossvoiceacting;

import callmemaple.bossvoiceacting.data.Boss;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

import java.util.EnumSet;
import java.util.Set;

@ConfigGroup(BossVoiceActingConfig.CONFIG_GROUP)
public interface BossVoiceActingConfig extends Config
{
    String CONFIG_GROUP = "boss-voice-acting";

    String VOLUME_KEY = "volume";
    @Range(max = 200)
    @ConfigItem(
            keyName = VOLUME_KEY,
            name = "Volume",
            description = "Adjust how loud the voice lines are by boosting or lowering the gain. Range 0 to 200",
            position = 1
    )
    default int getVolume() {
        return 175; // Setting the default volume higher for now as clips are too quiet across the board
    }

    @ConfigSection(
            name = "Bosses",
            description = "Select which bosses to play voice lines for",
            position = 2,
            closedByDefault = true
    ) String bosses = "bosses";

    String ENABLED_BOSSES_KEY = "enabledBosses";
    @ConfigItem(
            keyName = ENABLED_BOSSES_KEY,
            name = "Enabled",
            description = "Use ctrl+click to deselect a single one (similar functionality as Window's File Explorer selecting)",
            position = 2,
            section = bosses
    ) default Set<Boss> getEnabledBosses() {
        return EnumSet.allOf(Boss.class);
    }

    String VERSION_KEY = "version";
    @ConfigItem(
            keyName = VERSION_KEY,
            name = "version",
            description = "Get the most recently seen version of the audio files",
            hidden = true
    )
    default String getPreviousVersion() {
        return "1.0";
    }

    @ConfigItem(
            keyName = VERSION_KEY,
            name = "version",
            description = "Set the most recently seen version of the audio files",
            hidden = true
    )
    void setPreviousVersion(String version);
}
