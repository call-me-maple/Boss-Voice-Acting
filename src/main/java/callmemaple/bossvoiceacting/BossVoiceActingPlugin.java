package callmemaple.bossvoiceacting;

import callmemaple.bossvoiceacting.data.Boss;
import callmemaple.bossvoiceacting.data.Quote;

import com.google.inject.Provides;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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

import static callmemaple.bossvoiceacting.data.Boss.*;
import static callmemaple.bossvoiceacting.data.Quote.findQuote;

@Slf4j
@PluginDescriptor(
	name = "Boss Voice Acting",
	description = "adds audio voice lines for various bosses' overhead text",
	tags = {"boss","bossing","voice","acting"}
)
public class BossVoiceActingPlugin extends Plugin
{
	@NonNull
	private static final HttpUrl RAW_GITHUB = Objects.requireNonNull(HttpUrl.parse("https://raw.githubusercontent.com/call-me-maple/Boss-Voice-Acting/audio"));
	public static final String AUDIO_DIRECTORY = String.join(File.separator, RuneLite.RUNELITE_DIR.getPath(), "boss-voice-acting", "audio-cache-dont-use");

	@Inject
	private ConfigManager configManager;

	@Inject
	private OkHttpClient okHttpClient;

	@Inject
	private BossVoiceActingConfig config;

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
		loadQuotes();
	}

	@Override
	protected void shutDown()
	{
		unloadQuotes();
	}

	/**
	 * 	Try to create and load each audio Clip updating the volumes at the end
	 */
	private void loadQuotes()
	{
		for (Quote quote : Quote.QUOTES)
		{
			if (!config.getEnabledBosses().contains(quote.getBoss()))
			{
				// continue to the next Quote if the boss isn't enabled
				continue;
			}
			if (!quote.getFile().exists())
			{
				log.debug("no file found {}", quote.getFile());
				// Create any missing directories
				if (quote.getFile().getParentFile().mkdirs())
				{
					log.debug("mkdirs {}", quote.getFile().getParent());
				}
				downloadQuote(quote);
				// continue to the next Quote will this one is downloading
				continue;
			}
			loadQuote(quote);
		}
	}

	/**
	 * 	Attempt to open and store the audio file of the Quote
	 */
	private void loadQuote(Quote quote)
	{
		// Try to open the file
		try (InputStream fileStream = new BufferedInputStream(Files.newInputStream(quote.getFile().toPath())))
		{
			Clip clip = AudioSystem.getClip();
			// Using an audio stream try to open the audio Clip and allocate the needed system resources
			try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(fileStream))
			{
				clip.open(audioStream);
				updateVolumeLevel(clip);
				loadedClips.put(quote, clip);
				log.debug("loaded clip {} from file {}", quote.getLine(), quote.getFile());
			}
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)
		{
			log.error("Failed to load quote " + quote.getLine(), e);
		}
	}

	/**
	 * 	Stop and close all loaded audio Clips releasing the system resources
	 */
	private void unloadQuotes()
	{
		for (Clip clip : loadedClips.values())
		{
			clip.stop();
			clip.close();
		}
		loadedClips.clear();
	}

	/**
	 * 	Update the passed Clip's master gain control based on the current volume config by
	 * 	converting the config value into a logarithmic decibel scale and setting the new gain adjustment
	 */
	private void updateVolumeLevel(Clip clip)
	{
		FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		float gain = 20f * (float) Math.log10(config.getVolume() / 100f);
		gain = Math.min(gain, volume.getMaximum());
		gain = Math.max(gain, volume.getMinimum());
		volume.setValue(gain);
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
	private void downloadQuote(Quote quote)
	{
		// Define the output path and the url source of the file
		Path outputPath = quote.getFile().toPath();
		HttpUrl inputUrl = RAW_GITHUB.newBuilder()
				.addPathSegment(quote.getBoss().getFolderName())
				.addPathSegment(quote.getFilename()).build();

		okHttpClient.newCall(new Request.Builder().url(inputUrl).build()).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
				log.error("failed to get audio file: ", e);
			}

			@Override
			public void onResponse(Call call, Response res) throws IOException
			{
				if (!res.isSuccessful() || res.body() == null)
				{
					log.error("failed to get audio file: {}",  res.body());
					return;
				}

				// Copy the response body to the output location
				Files.copy(new BufferedInputStream(res.body().byteStream()), outputPath, StandardCopyOption.REPLACE_EXISTING);
				log.debug("downloaded audio file: {}", outputPath);
				loadQuote(quote);
			}
		});
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

			// Load the version.properties and read the current version
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
		Boss boss = findBoss(actorName);
		Quote quote = findQuote(boss, line);

		if (quote == null)
		{
			if (boss != null)
			{
				log.error("missing boss voice line for {}: {}", actorName, line);
			}
			return;
		}
		log.debug("actor:{} line:{}", actorName, event.getOverheadText());

		// Play the Clip if it is loaded
		if (loadedClips.containsKey(quote))
		{
			log.debug("playing quote {}: \"{}\", from {}", boss, line, quote.getFilename());
			playClip(loadedClips.get(quote));
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals(BossVoiceActingConfig.CONFIG_GROUP))
		{
			return;
		}

		switch (event.getKey())
		{
			case BossVoiceActingConfig.VOLUME_KEY:
				for (Clip clip : loadedClips.values())
				{
					updateVolumeLevel(clip);
				}
				break;
			case BossVoiceActingConfig.ENABLED_BOSSES_KEY:
				unloadQuotes();
				loadQuotes();
				break;
		}
	}

	@Provides
	BossVoiceActingConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BossVoiceActingConfig.class);
	}
}
