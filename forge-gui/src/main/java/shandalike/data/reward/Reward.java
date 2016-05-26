package shandalike.data.reward;

import java.util.ArrayList;

import shandalike.UIModel;

public interface Reward {
	/**
	 * Get a textual description of this reward before it has been granted.
	 */
	public String getDescription();
	
	/**
	 * Builds internal information for the reward
	 */
	public void build();
	
	/**
	 * Returns true if the reward requires player choice.
	 */
	public boolean requiresChoice();
	
	/**
	 * Present a screen offering the player choice. Will mutate the state of the reward object
	 * indicating what the player chose.
	 */
	public void choose();
	
	/**
	 * Award the reward, finally adding the chosen or randomly determined cards to the
	 * player inventory.
	 */
	public void award();
	
	/**
	 * Show the reward in a UI.
	 */
	public void show(UIModel ui, boolean showPicker);
}
