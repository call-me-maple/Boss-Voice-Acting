package callmemaple.bossvoicelines;

import callmemaple.bossvoicelines.data.Boss;
import callmemaple.bossvoicelines.data.Quote;
import com.google.inject.Provides;
import javax.inject.Inject;
import javax.sound.sampled.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import static callmemaple.bossvoicelines.data.Boss.*;
import static callmemaple.bossvoicelines.data.Quote.findQuote;

@Slf4j
@PluginDescriptor(
	name = "Boss Voice Lines"
)
public class BossVoiceLinesPlugin extends Plugin
{
	private static final HttpUrl RAW_GITHUB = HttpUrl.parse("https://raw.githubusercontent.com/call-me-maple/Boss-Voice-Lines/audio");

	@Inject
	private Client client;

	@Inject
	private BossVoiceLinesConfig config;

	@Inject
	private OkHttpClient okHttpClient;

	private final Map<Quote, Clip> audioClips = new HashMap<>();
	private Clip nowPlaying;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Boss Voice Lines started!");
		nowPlaying = null;
		loadClips();
		updateVolumeLevel();
	}

	@Override
	protected void shutDown() throws Exception
	{
		unloadClips();
		log.info("Boss Voice Lines stopped!");
	}

	@Provides
	BossVoiceLinesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BossVoiceLinesConfig.class);
	}

	@Subscribe
	public void onOverheadTextChanged(OverheadTextChanged event)
	{
		String actorName = event.getActor().getName();
		String line = event.getOverheadText();
		Quote quote = findQuote(findBoss(actorName), line);
		if (quote != null)
		{
			if (audioClips.containsKey(quote))
			{
				log.debug("playing quote {}: \"{}\", from {}", quote.boss, quote.line, quote.filename);
				playClip(audioClips.get(quote));
			}
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("bossvoicelines"))
		{
			updateVolumeLevel();
		}
	}

	private void loadClips()
	{
		for (Quote quote : Quote.QUOTES)
		{
			if (!quote.getFile().exists())
			{
				log.debug("no file found {}", quote.getFile());
				if (!downloadQuote(quote))
				{
					// continue to the next quote if it can't be downloaded
					continue;
				}
			}
			try
			{
				Clip newClip = AudioSystem.getClip();
				loadClip(quote, newClip);
			} catch (LineUnavailableException e)
			{
				log.debug("Failed to create clip ", e);
			}
		}
	}

	private void loadClip(Quote quote, Clip clip)
	{
		try (InputStream fileStream = new BufferedInputStream(Files.newInputStream(quote.getFile().toPath())))
		{
			try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(fileStream))
			{
				clip.open(audioStream);
				audioClips.put(quote, clip);
				log.debug("loaded clip {} from file {}", quote.line, quote.getFile());
			}
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)
		{
			log.error("Failed to load quote " + quote.line, e);
		}
	}

	private void unloadClips()
	{
		for (Clip clip : audioClips.values())
		{
			clip.stop();
			clip.close();
		}
		audioClips.clear();
	}

	private void playClip(Clip clip)
	{
		if (nowPlaying != null && nowPlaying.isActive())
		{
			log.debug("interrupting now playing");
			stopClip(nowPlaying);
		}
		nowPlaying = clip;
		clip.setFramePosition(0);
		clip.loop(0);
	}

	private void stopClip(Clip clip)
	{
		clip.stop();
		nowPlaying = null;
	}

	private void updateVolumeLevel()
	{
		for (Clip clip : audioClips.values())
		{
			FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			float gain = 20f * (float) Math.log10(config.volume() / 100f);
			gain = Math.min(gain, volume.getMaximum());
			gain = Math.max(gain, volume.getMinimum());
			volume.setValue(gain);
		}
	}

	private boolean downloadQuote(Quote quote)
	{
		if (RAW_GITHUB == null)
		{
			return false;
		}
		HttpUrl soundUrl = RAW_GITHUB.newBuilder()
				.addPathSegment(quote.boss.folderName)
				.addPathSegment(quote.filename).build();

		if (quote.getFile().getParentFile().mkdirs())
		{
			log.debug("mkdirs {}", quote.getFile().getParent());
		}

		Path outputPath = quote.getFile().toPath();

		try (Response res = okHttpClient.newCall(new Request.Builder().url(soundUrl).build()).execute()) {
			if (res.isSuccessful() && res.body() != null)
			{
				Files.copy(new BufferedInputStream(res.body().byteStream()), outputPath, StandardCopyOption.REPLACE_EXISTING);
				return true;
			}
			log.error("url:{} response:{}", res.request().url(), res.body().string());
		} catch (IOException e) {
			log.error("could not download sounds", e);
			return false;
		}
		return false;
	}

	@Subscribe
	public void onCommandExecuted(CommandExecuted command)
	{
		String[] arguments = command.getArguments();

		if (command.getCommand().equals("vo"))
		{
			if (arguments.length < 1)
			{
				return;
			}
			String line = arguments[0].toUpperCase();

			try
			{
				Quote quote = Quote.findQuote(Boss.CHAOS_FANATIC, line);
				if (audioClips.containsKey(quote))
				{
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Playing voiceover " + line, null);
					playClip(audioClips.get(quote));
				}
			} catch (IllegalArgumentException e)
			{
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Unknown voiceover: " + line, null);
			}
		}
	}
}

