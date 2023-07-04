package callmemaple.bossvoicelines;

import callmemaple.bossvoicelines.data.Boss;
import callmemaple.bossvoicelines.data.Quote;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import static callmemaple.bossvoicelines.data.Boss.*;
import static callmemaple.bossvoicelines.data.Quote.findQuote;

@Slf4j
@PluginDescriptor(
	name = "Boss Voice Lines"
)
public class BossVoiceLinesPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private SoundEngine soundEngine;

	@Inject
	private BossVoiceLinesConfig config;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Boss Voice Lines started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Boss Voice Lines stopped!");
		soundEngine.close();
	}

	@Subscribe
	public void onOverheadTextChanged(OverheadTextChanged event)
	{
		log.debug("{}: \"{}\"", event.getActor().getName(), event.getOverheadText());
		String actorName = event.getActor().getName();
		Boss boss = findBoss(actorName);
		if (boss == UNKNOWN)
		{
			return;
		}
		Quote quote = findQuote(boss, event.getOverheadText());
		if (quote == null)
		{
			return;
		}
		log.debug("playing quote {}: \"{}\", from {}", quote.boss, quote.line, quote.filename);
		if (quote.line.isEmpty())
		{
			log.debug("empty filename");
		} else
		{
			soundEngine.playClip(quote);
		}
	}

	@Provides
	BossVoiceLinesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BossVoiceLinesConfig.class);
	}
}
