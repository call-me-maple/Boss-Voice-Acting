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
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.events.OverheadTextChanged;
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

import static callmemaple.bossvoicelines.data.Boss.*;
import static callmemaple.bossvoicelines.data.Quote.findQuote;

@Slf4j
@PluginDescriptor(
	name = "Boss Voice Lines",
	description = "adds audio voice lines for various bosses' overhead text",
	tags = {"boss","bossing","voice","acting"}
)
public class BossVoiceLinesPlugin extends Plugin
{
	@NonNull
	private static final HttpUrl RAW_GITHUB = Objects.requireNonNull(HttpUrl.parse("https://raw.githubusercontent.com/call-me-maple/Boss-Voice-Lines/audio"));
	public static final String AUDIO_DIRECTORY = String.join(File.separator, RuneLite.RUNELITE_DIR.getPath(), "boss-voice-lines", "audio-cache-dont-use");

	@Inject
	private ConfigManager configManager;

	@Inject
	private OkHttpClient okHttpClient;

	@Inject
	private BossVoiceLinesConfig config;

	/**
	 * Used to store, play, and adjust the loaded audio Clips
	 */
	private final Map<Quote, Clip> loadedClips = new HashMap<>();

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

	/**
	 * 	Try to create and load each audio Clip updating the volumes at the end
	 */
	private void loadClips()
	{
		for (Quote quote : Quote.QUOTES)
		{
			if (!config.getEnabledBosses().contains(quote.getBoss()))
			{
				// continue to the next quote if the boss isn't enabled
				continue;
			}
			if (!quote.getFile().exists())
			{
				log.debug("no file found {}", quote.getFile());
				// Attempt to download the audio file
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
				log.error("Failed to create clip ", e);
			}
		}
		updateVolumeLevel();
	}

	/**
	 * 	Attempt to open and store the passed audio Clip
	 */
	private void loadClip(Quote quote, Clip clip)
	{
		// Try to open the file
		try (InputStream fileStream = new BufferedInputStream(Files.newInputStream(quote.getFile().toPath())))
		{
			// Using an audio stream try to open the audio Clip and allocate the needed system resources
			try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(fileStream))
			{
				clip.open(audioStream);
				loadedClips.put(quote, clip);
				log.debug("loaded clip {} from file {}", quote.getLine(), quote.getFile());
			}
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)
		{
			log.error("Failed to load quote " + quote.getLine(), e);
		}
	}

	/**
	 * 	Update each Clip's master gain control based on the current volume config by
	 * 	converting the config value into a logarithmic decibel scale and setting the new gain adjustment
	 */
	private void updateVolumeLevel()
	{
		for (Clip clip : loadedClips.values())
		{
			FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			float gain = 20f * (float) Math.log10(config.getVolume() / 100f);
			gain = Math.min(gain, volume.getMaximum());
			gain = Math.max(gain, volume.getMinimum());
			volume.setValue(gain);
		}
	}

	/**
	 * 	Stop and close all loaded audio Clips releasing the system resources
	 */
	private void unloadClips()
	{
		for (Clip clip : loadedClips.values())
		{
			clip.stop();
			clip.close();
		}
		loadedClips.clear();
	}

	/**
	 * 	Start playing the passed in audio Clip
	 * 	This interrupts any now playing audio Clip before to avoid multiple Clips playing together
	 * 	(will try update the audio clip timings to avoid this behavior ideally
	 * 	or maybe allow multiple Clips to play together for specific bosses like Cerberus)
	 */
	private void playClip(Clip clip)
	{
		if (nowPlaying != null && nowPlaying.isActive())
		{
			log.debug("interrupting now playing");
			stopClip(nowPlaying);
		}
		nowPlaying = clip;
		// From net.runelite.client.Notifier
		// Using loop instead of start prevents the clip from not being played sometimes
		// presumably a race condition in the underlying line driver
		clip.setFramePosition(0);
		clip.loop(0);
	}

	/**
	 * 	Stop the audio Clip passed in from playing
	 */
	private void stopClip(Clip clip)
	{
		clip.stop();
		nowPlaying = null;
	}

	/**
	 * 	Attempt to download the corresponding audio file for the passed in Quote
	 */
	private boolean downloadQuote(Quote quote)
	{
		if (quote.getFile().getParentFile().mkdirs())
		{
			log.debug("mkdirs {}", quote.getFile().getParent());
		}

		// Define the output path and the url source of the file
		Path outputPath = quote.getFile().toPath();
		HttpUrl inputUrl = RAW_GITHUB.newBuilder()
				.addPathSegment(quote.getBoss().getFolderName())
				.addPathSegment(quote.getFilename()).build();

		try (Response res = okHttpClient.newCall(new Request.Builder().url(inputUrl).build()).execute())
		{
			if (!res.isSuccessful() || res.body() == null)
			{
				log.error("failed to get audio file: {}",  res.body());
				return false;
			}

			// Checks the Content-Type to verify only "audio/wav" are downloaded
			MediaType contentType = res.body().contentType();
			if (contentType == null || !contentType.toString().equals("audio/wav"))
			{
				log.error("failed to get audio file: Content-Type must be 'audio/wav' not '{}' ", contentType);
				return false;
			}

			// Copy the response body to the output location
			Files.copy(new BufferedInputStream(res.body().byteStream()), outputPath, StandardCopyOption.REPLACE_EXISTING);
			log.debug("downloaded audio file {}: \"{}\", saved at {}", quote.getBoss(), quote.getLine(), outputPath);
			return true;

		} catch (IOException e)
		{
			log.error("failed to get audio file: ", e);
			return false;
		}
	}

	/**
	 * 	Checks if the audio files versions have changed and if so remove the old versions
	 */
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

			// Load the fetched version.properties and read the current version
			final Properties properties = new Properties();
			properties.load(res.body().byteStream());
			String currentVersion = properties.getProperty("version");

			// Remove old files and update the version config when the versions don't match
			if (!currentVersion.equals(config.getPreviousVersion()))
			{
				log.debug("New audio version {} found. Resetting {}", currentVersion, AUDIO_DIRECTORY);
				FileUtils.deleteDirectory(new File(AUDIO_DIRECTORY));
				config.setPreviousVersion(currentVersion);
				return;
			}
			log.debug("version:{} Audio files are up to date.", config.getPreviousVersion());
		} catch (IOException e)
		{
			log.error("failed to get version.properties file: ", e);
		}
	}

	/**
	 * 	Whenever an enabled boss 'says' a Quote then play the corresponding audio Clip
	 */
	@Subscribe
	public void onOverheadTextChanged(OverheadTextChanged event)
	{
		// Search for the Boss and their Quote based on the event's actor and overhead text
		String actorName = event.getActor().getName();
		String line = event.getOverheadText();
		Quote quote = findQuote(findBoss(actorName), line);
		log.debug("actor:{} line:{}",event.getActor(), event.getOverheadText());

		// Play the Clip if it is loaded
		if (quote != null && loadedClips.containsKey(quote))
		{
			log.debug("playing quote {}: \"{}\", from {}", quote.getBoss(), quote.getLine(), quote.getFilename());
			playClip(loadedClips.get(quote));
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals(BossVoiceLinesConfig.CONFIG_GROUP))
		{
			return;
		}
		if (BossVoiceLinesConfig.VOLUME_KEY.equals(event.getKey()))
		{
			updateVolumeLevel();
		}
		if (BossVoiceLinesConfig.ENABLED_BOSSES_KEY.equals(event.getKey()))
		{
			unloadClips();
			loadClips();
		}
	}

	@Provides
	BossVoiceLinesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BossVoiceLinesConfig.class);
	}
}
