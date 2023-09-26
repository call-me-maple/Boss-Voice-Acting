package callmemaple.bossvoiceacting.data;

import lombok.Getter;

import javax.annotation.Nullable;

@Getter
public enum Boss
{
    AHRIM("barrows", "Ahrim the Blighted"),
    DHAROK("barrows", "Dharok the Wretched"),
    GUTHAN("barrows", "Guthan the Infested"),
    KARIL("barrows", "Karil the Tainted"),
    TORAG("barrows", "Torag the Corrupted"),
    VERAC("barrows", "Verac the Defiled"),
    CERBERUS("cerberus", "Cerberus"),
    CERBERUS_GHOSTS("cerberus", "Summoned Soul"),
    CHAOS_FANATIC("chaos-fanatic", "Chaos fanatic"),
    COMMANDER_ZILYANA("commander-zilyana", "Commander Zilyana"),
    CHAOS_ARCHAEOLOGIST("crazy-archaeologist", "Crazy archaeologist"),
    DERANGED_ARCHAEOLOGIST("deranged-archaeologist", "Deranged archaeologist"),
    GENERAL_GRAARDOR("general-graardor", "General Graardor"),
    KRIL_TSUTSAROTH("k'ril-tsutsaroth", "K'ril Tsutsaroth"),
    KREEARRA("kree'arra", "Kree'arra"),
    NEX("nex", "nex"),
    VETION("vet'ion", "Vet'ion"),
    CALVARION("vet'ion", "Calvar'ion"); // Calvar'ion shares quotes with Vet'ion, so they use the same audio files to avoid data redundancy

    private final String name;
    private final String folderName;

    Boss(String folderName, String name)
    {
        this.name = name;
        this.folderName = folderName;
    }

    /**
     * 	Search for the Boss based on the actor's name ignoring case
     */
    @Nullable
    public static Boss findBoss(String actorName)
    {
        for (Boss boss: values())
        {
            if (boss.name.equalsIgnoreCase(actorName))
            {
                return boss;
            }
        }
        return null;
    }
}
