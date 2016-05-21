package shandalike.data;

import forge.card.MagicColor;

public class AdventureSummary {
	public String name;
	public String worldName;
	public int mostRecentSaveSlot = 0;
	public int difficulty = 0;
	public boolean isIronMan = false;
	public String worldId;
	public int mostRecentLoadSlot = 0;
	public String color;
	public boolean isCheatEnabled = false;
	public boolean cheated = false;
	public int nWins = 0;
	public int nLosses = 0;
	public int nCards = 0;
	public long gold = 0;
	public String status = "Unknown";
	
	MagicColor.Color getPlayerColor() {
		for(MagicColor.Color c: MagicColor.Color.values()){
			if(c.getName().equals(color)) return c;
		}
		return MagicColor.Color.COLORLESS;
	}
}
