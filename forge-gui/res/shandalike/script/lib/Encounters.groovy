package lib

import shandalike.data.entity.Pawn
import shandalike.Util

import lib.Encounter

// A database of encounters.
class Encounters {
	def encounters = []
	def categories = []

	void add(Encounter enc) {
		encounters << enc
		if(!categories.find { it.equals(enc.category) }) {
			categories << enc.category
		}
	}

	Pawn random(String category, float offColorChance) {
		if(categories.size() == 0) return null
		
		if(Util.randomFloat() < offColorChance) {
			category = categories[ Util.randomInt(categories.size()) ]
		}

		def possibleEncounters = encounters.findAll { it.category.equals(category) }
		if(possibleEncounters.size() == 0) return null
		def encounter = possibleEncounters[ Util.randomInt(possibleEncounters.size()) ]
		encounter.spawn()
	}


}
