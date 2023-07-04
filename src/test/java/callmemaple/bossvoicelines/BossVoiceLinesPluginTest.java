package callmemaple.bossvoicelines;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BossVoiceLinesPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BossVoiceLinesPlugin.class);
		RuneLite.main(args);
	}
}