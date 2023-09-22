package callmemaple.bossvoicelines;

import callmemaple.bossvoicelines.data.Quote;

import com.google.inject.Provides;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.io.FileUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.MediaType;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import static callmemaple.bossvoicelines.BossVoiceLinesConfig.VERSION_KEY;
import static callmemaple.bossvoicelines.data.Boss.*;
import static callmemaple.bossvoicelines.data.Quote.findQuote;

@Slf4j
@PluginDescriptor(
	name = "Boss Voice Lines"
)
public class BossVoiceLinesPlugin extends Plugin
{
	@NonNull
	private static final HttpUrl RAW_GITHUB = Objects.requireNonNull(HttpUrl.parse("https://raw.githubusercontent.com/call-me-maple/Boss-Voice-Lines/audio"));
	public static final String CONFIG_GROUP = "boss-voice-lines";
	public static final String AUDIO_DIRECTORY = String.join(File.separator, RuneLite.RUNELITE_DIR.getPath(), "boss-voice-lines", "audio-cache-dont-use");

	@Inject
	private Client client;

	@Inject
	private ConfigManager configManager;

	@Inject
	private BossVoiceLinesConfig config;

	@Inject
	private OkHttpClient okHttpClient;

	private final Map<Quote, Clip> audioClips = new HashMap<>();

	@Nullable
	private Clip nowPlaying = null;

	@Override
	protected void startUp()
	{
		checkVersion();
		loadClips();
	}

	@Override
	protected void shutDown()
	{
		unloadClips();
	}

	private void loadClips()
	{
		for (Quote quote : Quote.QUOTES)
		{
			if (!config.getEnabledBosses().contains(quote.boss))
			{
				// continue to the next quote if the boss isn't enabled
				continue;
			}
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

		updateVolumeLevel();
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

	private void updateVolumeLevel()
	{
		for (Clip clip : audioClips.values())
		{
			FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			float gain = 20f * (float) Math.log10(config.getVolume() / 100f);
			gain = Math.min(gain, volume.getMaximum());
			gain = Math.max(gain, volume.getMinimum());
			volume.setValue(gain);
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

	private boolean downloadQuote(Quote quote)
	{
		if (quote.getFile().getParentFile().mkdirs())
		{
			log.debug("mkdirs {}", quote.getFile().getParent());
		}

		Path outputPath = quote.getFile().toPath();
		HttpUrl inputUrl = RAW_GITHUB.newBuilder()
				.addPathSegment(quote.boss.folderName)
				.addPathSegment(quote.filename).build();

		try (Response res = okHttpClient.newCall(new Request.Builder().url(inputUrl).build()).execute())
		{
			if (!res.isSuccessful() || res.body() == null)
			{
				log.error("failed to get audio file: {}",  res.body());
				return false;
			}
			MediaType contentType = res.body().contentType();
			if (contentType == null || !contentType.toString().equals("audio/wav"))
			{
				log.error("failed to get audio file: Content-Type must be 'audio/wav' not '{}' ", contentType);
				return false;
			}

			Files.copy(new BufferedInputStream(res.body().byteStream()), outputPath, StandardCopyOption.REPLACE_EXISTING);
			return true;

		} catch (IOException e)
		{
			log.error("failed to get audio file: ", e);
			return false;
		}
	}

	// Checks if the audio version has changed and if so clears the audio file directory
	private void checkVersion()
	{
		HttpUrl inputUrl = RAW_GITHUB.newBuilder().addPathSegment("version.properties").build();
		try (Response res = okHttpClient.newCall(new Request.Builder().url(inputUrl).build()).execute())
		{
			if (!res.isSuccessful() || res.body() == null)
			{
				log.error("failed to get version.properties file: {}",  res.body());
				return;
			}
			final Properties properties = new Properties();
			properties.load(res.body().byteStream());
			String currentVersion = properties.getProperty("version");
			if (!currentVersion.equals(config.getPreviousVersion()))
			{
				log.debug("New audio versions found. Resetting {}", AUDIO_DIRECTORY);
				FileUtils.deleteDirectory(new File(AUDIO_DIRECTORY));
				configManager.setConfiguration(CONFIG_GROUP, VERSION_KEY, currentVersion);
				return;
			}
			log.debug("version:{} Audios are up to date.", config.getPreviousVersion());
		} catch (IOException e)
		{
			log.error("failed to get version.properties file: ", e);
		}
	}

	@Subscribe
	public void onOverheadTextChanged(OverheadTextChanged event)
	{
		String actorName = event.getActor().getName();
		String line = event.getOverheadText();
		Quote quote = findQuote(findBoss(actorName), line);
		if (quote != null && audioClips.containsKey(quote))
		{
			log.debug("playing quote {}: \"{}\", from {}", quote.boss, quote.line, quote.filename);
			playClip(audioClips.get(quote));
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals(CONFIG_GROUP))
		{
			loadClips();
		}
	}

	@Provides
	BossVoiceLinesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BossVoiceLinesConfig.class);
	}
}
