package shandalike.screens.home;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.commons.lang3.ArrayUtils;

import forge.UiCommand;
import forge.gui.framework.ICDoc;
import shandalike.Model;
import shandalike.data.Adventure;
import shandalike.data.AdventureSummary;
import shandalike.data.World;
import shandalike.screens.ShandalikeUIUtil;

/**
 * Controls the Worlda submenu in the home UI.
 *
 * <br><br><i>(C at beginning of class name denotes a control class.)</i>
 *
 */
@SuppressWarnings("serial")
public enum CSubmenuWorld implements ICDoc {
    SINGLETON_INSTANCE;

    private final VSubmenuWorld view = VSubmenuWorld.SINGLETON_INSTANCE;

    private final UiCommand cmdQuestUpdate = new UiCommand() {
        @Override public void run() {
            update();
        }
    };

    @Override
    public void register() {
    }

    /* (non-Javadoc)
     * @see forge.control.home.IControlSubmenu#update()
     */
    @Override
    public void initialize() {
        view.getBtnEmbark().setCommand(
                new UiCommand() { @Override public void run() { newQuest(); } });
        view.getWorldLister().setSelectCommand( new UiCommand() {
        	public void run() {
        		// Load the selected worldstate and fully hydrate it
        		Adventure adv = CSubmenuWorld.this.view.getWorldLister().getSelectedAdventure();
        		Model.setActiveAdventure(adv);
        		Model.gameController.loadActiveAdventure();		
        		// Show the Shandalike map
        		ShandalikeUIUtil.showMap();
        	}
        });
    }

    /* (non-Javadoc)
     * @see forge.control.home.IControlSubmenu#update()
     */
    @Override
    public void update() {
        final VSubmenuWorld view = VSubmenuWorld.SINGLETON_INSTANCE;
        final WorldLister worldLister = view.getWorldLister();
        
        // Refresh the shandalike worlds list
        Model.loadAdventureSummaries();
        // Populate GUI world list
        worldLister.setWorlds(new ArrayList<>(Model.adventures.values()));
        // If there is an active world, hilight it in the list.
        if (Model.adventure != null) {
        	worldLister.setSelectedData(Model.adventure);
        }
        worldLister.setDeleteCommand(cmdQuestUpdate);
        // Populate base world list
        List<String> names = new ArrayList<String>();
        for (World bw: Model.worlds.values()) {
        	names.add(bw.getName());
        }
        view.getBaseWorldList().setListData(names.toArray(ArrayUtils.EMPTY_STRING_ARRAY));

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                view.getBtnEmbark().requestFocusInWindow();
            }
        });

    }

    /**
     * The actuator for new quests.
     */
    private void newQuest() {
    	// Get data from view.
    	final VSubmenuWorld view = VSubmenuWorld.SINGLETON_INSTANCE;
    	final int difficulty = view.getSelectedDifficulty();
    	final int selectedWorldIdx = view.getBaseWorldList().getSelectedIndex();
    	if(selectedWorldIdx == -1) return;
    	final String selectedWorldName = view.getBaseWorldList().getSelectedValue();
    	final String adventureName = view.getAdventureName();
    	String selectedWorldId = null;
    	for (World bw: Model.worlds.values()) {
    		if(bw.getName() == selectedWorldName) {
    			selectedWorldId = bw.id; break;
    		}
    	}
    	
    	// Create an AdventureSummary with all the user options
    	AdventureSummary summary = new AdventureSummary();
    	summary.name = adventureName;
    	summary.worldId = selectedWorldId;
    	summary.difficulty = difficulty;
    	summary.color = view.getSelectedColor();
    	summary.isIronMan = view.isIronman();
    	summary.isCheatEnabled = summary.cheated = view.isCheat();
    	
    	// Create an adventure from the summary.
    	Model.createAdventure(summary);
        update();

    }

}
