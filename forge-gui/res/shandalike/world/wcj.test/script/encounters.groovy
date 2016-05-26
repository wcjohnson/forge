import lib.Encounters
import lib.Encounter
import groovy.transform.Field

@Field def encs = null

// Build the encounter database
void buildEncounters() {
	encs = new Encounters()
	encs.add(new Encounter("w2", "Cleric", "white", "enemy_white_1.sprite.json", "W 2 Cleric.dck", 2.0f))
	encs.add(new Encounter("u1", "Sea Dragon", "blue", "enemy_blue_1.sprite.json", "U 1 Sea Dragon.dck", 1.0f))
	encs.add(new Encounter("b1", "Nether Fiend", "black", "enemy_black_1.sprite.json", "B 1 Nether Fiend.dck", 1.0f))
	encs.add(new Encounter("r1", "Ape Lord", "red", "enemy_red_1.sprite.json", "R 1 Ape Lord.dck", 1.0f))
	encs.add(new Encounter("g1", "Druid", "green", "enemy_green_1.sprite.json", "G 1 Druid.dck", 1.0f))
}

Encounters getEncounters() {
	if(encs == null) buildEncounters()
	return encs
}
