package callmemaple.bossvoiceacting;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BossVoiceActingPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BossVoiceActingPlugin.class);
		RuneLite.main(args);
	}
}