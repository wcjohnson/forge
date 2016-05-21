package shandalike.game;

import java.util.Random;

public class RNG {
	Random random;
	public RNG() {
		random = new Random();
	}
	
	public float randomFloat() {
		return random.nextFloat();
	}
	
	public int randomInt(int r) {
		return random.nextInt(r);
	}
}
