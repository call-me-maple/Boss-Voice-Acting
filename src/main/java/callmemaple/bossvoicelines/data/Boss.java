package callmemaple.bossvoicelines.data;

public enum Boss
{
    CHAOS_FANATIC("Chaos fanatic", "chaos-fanatic"),
    CHAOS_ARCHAEOLOGIST("Crazy archaeologist", "crazy-archaeologist"),
    DERANGED_ARCHAEOLOGIST("Deranged archaeologist", "deranged-archaeologist"),
    UNKNOWN;

    public final String name;
    public final String folderName;

    Boss(String name, String folderName)
    {
        this.name = name;
        this.folderName = folderName;
    }

    Boss()
    {
        this.name = "UNKNOWN BOSS NAME";
        this.folderName ="";
    }

    public static Boss findBoss(String actorName)
    {
        for (Boss boss: values())
        {
            if (boss.name.equals(actorName))
            {
                return boss;
            }
        }
        return UNKNOWN;
    }
}
