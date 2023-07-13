package callmemaple.bossvoicelines.data;

public enum Boss
{
    AHRIM("barrows", "Ahrim the Blighted"),
    DHAROK("barrows", "Dharok the Wretched"),
    GUTHAN("barrows", "Guthan the Infested"),
    KARIL("barrows", "Karil the Tainted"),
    TORAG("barrows", "Torag the Corrupted"),
    VERAC("barrows", "Verac the Defiled"),

    CERBERUS("cerberus", "Cerberus"),
    CERB_GHOST("cerberus", "Summoned Soul"),

    CHAOS_FANATIC("chaos-fanatic", "Chaos fanatic"),
    COMMANDER_ZILYANA("commander-zilyana", "Commander Zilyana"),
    CHAOS_ARCHAEOLOGIST("crazy-archaeologist", "Crazy archaeologist"),
    DERANGED_ARCHAEOLOGIST("deranged-archaeologist", "Deranged archaeologist"),
    GENERAL_GRAARDOR("general-graardor", "General Graardor"),
    KRIL_TSUTSAROTH("k'ril-tsutsaroth", "K'ril Tsutsaroth"),
    KREEARRA("kree'arra", "Kree'arra"),
    NEX("nex", "nex"),
    VETION("vet'ion", "Vet'ion"),
    CALVARION("vet'ion", "Calvar'ion"), // Shares audio files with Vet'ion
    UNKNOWN;

    public final String name;
    public final String folderName;

    Boss(String folderName, String name)
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
            if (boss.name.equalsIgnoreCase(actorName))
            {
                return boss;
            }
        }
        return UNKNOWN;
    }
}
