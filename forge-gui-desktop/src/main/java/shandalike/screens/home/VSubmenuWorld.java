package shandalike.screens.home;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import forge.card.MagicColor;
import forge.gui.framework.DragCell;
import forge.gui.framework.DragTab;
import forge.gui.framework.EDocID;
import forge.screens.home.EMenuGroup;
import forge.screens.home.IVSubmenu;
import forge.screens.home.VHomeUI;
import forge.screens.home.VHomeUI.PnlDisplay;
import forge.toolbox.FCheckBox;
import forge.toolbox.FLabel;
import forge.toolbox.FList;
import forge.toolbox.FRadioButton;
import forge.toolbox.FScrollPane;
import forge.toolbox.FSkin;
import forge.toolbox.FTextField;
import forge.toolbox.JXButtonPanel;
import net.miginfocom.swing.MigLayout;
import shandalike.Constants;

/**
 * Assembles Swing components of Shandalike submenu singleton.
 *
 * <br><br><i>(V at beginning of class name denotes a view class.)</i>
 */
public enum VSubmenuWorld implements IVSubmenu<CSubmenuWorld> {
    SINGLETON_INSTANCE;

    // Fields used with interface IVDoc
    private DragCell parentCell;
    private final DragTab tab = new DragTab("Quest Data");

    private final FLabel lblTitle = new FLabel.Builder()
    .text("Resume Adventure").fontAlign(SwingConstants.CENTER)
    .opaque(true).fontSize(16).build();
    
    private final FLabel lblNew = new FLabel.Builder()
    .text("Begin Adventure").fontAlign(SwingConstants.CENTER)
    .opaque(true).fontSize(16).build();

    // Active quests panel
    private final WorldLister lstQuests = new WorldLister();
    private final FScrollPane scrQuests = new FScrollPane(lstQuests, false);
    // Options panel
    private final JPanel pnlOptions = new JPanel();
    private final FTextField txtAdventureName = new FTextField.Builder().ghostText("[Name]").showGhostTextWithFocus().build();
    private final FRadioButton radEasy = new FRadioButton(Constants.DIFFICULTY_LEVEL_NAME[0]);
    private final FRadioButton radMedium = new FRadioButton(Constants.DIFFICULTY_LEVEL_NAME[1]);
    private final FRadioButton radHard = new FRadioButton(Constants.DIFFICULTY_LEVEL_NAME[2]);
    private final FRadioButton radExpert = new FRadioButton(Constants.DIFFICULTY_LEVEL_NAME[3]);
    private final FRadioButton radWhite = new FRadioButton(MagicColor.Color.WHITE.getName());
    private final FRadioButton radBlue = new FRadioButton(MagicColor.Color.BLUE.getName());
    private final FRadioButton radBlack = new FRadioButton(MagicColor.Color.BLACK.getName());
    private final FRadioButton radRed = new FRadioButton(MagicColor.Color.RED.getName());
    private final FRadioButton radGreen = new FRadioButton(MagicColor.Color.GREEN.getName());
    private final FCheckBox cbIronman = new FCheckBox("Ironman Mode");
    private final FCheckBox cbCheats = new FCheckBox("Developer Cheats");
    private final FLabel lblWorld = new FLabel.Builder()
    		.text("Worlds").fontAlign(SwingConstants.CENTER).opaque(true).build();
    private final JList<String> lstBaseWorlds = new FList<String>();
    private final FScrollPane scrRight  = new FScrollPane(lstBaseWorlds, true,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    private final FLabel btnEmbark = new FLabel.Builder().opaque(true)
            .fontSize(16).hoverable(true).text("New Adventure").build();

    /**
     * Constructor.
     */
    VSubmenuWorld() {

        lblTitle.setBackground(FSkin.getColor(FSkin.Colors.CLR_THEME2));

        // Bottom options panel
        pnlOptions.setOpaque(false);
        pnlOptions.setLayout(new MigLayout("insets 0, gap 5px, fillx, wrap 3"));
        
        /////////////////////////// Left side: difficulty, option checks
        final JXButtonPanel difficultyPanel = new JXButtonPanel();
        final String difficulty_constraints = "h 25px!, gapbottom 5";
        difficultyPanel.add(radEasy, difficulty_constraints);
        difficultyPanel.add(radMedium, difficulty_constraints);
        difficultyPanel.add(radHard, difficulty_constraints);
        difficultyPanel.add(radExpert, difficulty_constraints);
        radEasy.setSelected(true);
        cbIronman.setSelected(true); cbIronman.setEnabled(true);
        cbCheats.setSelected(false); cbCheats.setEnabled(true);
        final JPanel pnlDifficultyMode = new JPanel(new MigLayout("insets 0, gap 1%, flowy"));
        final FLabel lblAdventureName = new FLabel.Builder()
                .text("Character Name:").fontSize(14).build();
        pnlDifficultyMode.add(lblAdventureName, "h 30px!, gap 0 5px 0");
        pnlDifficultyMode.add(txtAdventureName, "h 30px!, w 200px!, gap 0 5px 0 0");
        pnlDifficultyMode.add(difficultyPanel, "gapright 4%");
        pnlDifficultyMode.add(cbIronman, "h 25px!, gapbottom 15, gapright 4%");
        pnlDifficultyMode.add(cbCheats, "h 25px!, gapbottom 15, gapright 4%");
        pnlDifficultyMode.setOpaque(false);
        pnlOptions.add(pnlDifficultyMode, "w 30%");
        
        /////////////////////////////// Middle: color sel.
        final JXButtonPanel colorPanel = new JXButtonPanel();
        colorPanel.add(radWhite, difficulty_constraints);
        colorPanel.add(radBlue, difficulty_constraints);
        colorPanel.add(radBlack, difficulty_constraints);
        colorPanel.add(radRed, difficulty_constraints);
        colorPanel.add(radGreen, difficulty_constraints);
        radWhite.setSelected(true);
        final JPanel pnlColor = new JPanel(new MigLayout("insets 0, gap 1%, flowy"));
        pnlColor.add(colorPanel, "gapright 4%");
        pnlColor.setOpaque(false);
        pnlOptions.add(pnlColor, "w 30%");
        
        /////////////////////////////// Right side: world picker
        final JPanel pnlWorldSelection = new JPanel(new MigLayout("insets 0, gap 1%, flowy"));
        pnlWorldSelection.add(lblWorld, "w 98%!, h 30px!, gap 1% 0 15px 15px");
        pnlWorldSelection.add(scrRight, "w 98%!, growy, pushy, gap 1% 0 0 20px");
        pnlWorldSelection.setOpaque(false);
        pnlOptions.add(pnlWorldSelection, "w 40%");

        pnlOptions.add(btnEmbark, "w 300px!, h 30px!, ax center, span 2, gap 0 0 15px 30px");
    }

    /* (non-Javadoc)
     * @see forge.view.home.IViewSubmenu#populate()
     */
    @Override
    public void populate() {
    	PnlDisplay homePanel = VHomeUI.SINGLETON_INSTANCE.getPnlDisplay();
        homePanel.removeAll();
        homePanel.setLayout(new MigLayout("insets 0, gap 0, wrap"));

        homePanel.add(lblTitle, "w 98%!, h 30px!, gap 1% 0 15px 15px");
        homePanel.add(scrQuests, "w 98%!, growy, pushy, gap 1% 0 0 20px");
        homePanel.add(lblNew, "w 98%!, h 30px!, gap 1% 0 15px 15px");
        homePanel.add(pnlOptions, "w 98%!, gap 1% 0 0 0");

        homePanel.repaintSelf();
        homePanel.revalidate();
    }

    /* (non-Javadoc)
     * @see forge.view.home.IViewSubmenu#getGroup()
     */
    @Override
    public EMenuGroup getGroupEnum() {
        return EMenuGroup.SHANDALIKE;
    }

    /* (non-Javadoc)
     * @see forge.gui.home.IVSubmenu#getMenuTitle()
     */
    @Override
    public String getMenuTitle() {
        return "Adventures";
    }

    /* (non-Javadoc)
     * @see forge.gui.home.IVSubmenu#getMenuName()
     */
    @Override
    public EDocID getItemEnum() {
        return EDocID.HOME_SHANDALIKE_WORLDS;
    }

    public WorldLister getWorldLister() {
        return this.lstQuests;
    }   
    public JList<String> getBaseWorldList() {
    	return this.lstBaseWorlds;
    }
    public String getAdventureName() {
    	return this.txtAdventureName.getText();
    }
    public int getSelectedDifficulty() {
        if (radEasy.isSelected()) {
            return 0;
        } else if (radMedium.isSelected()) {
            return 1;
        } else if (radHard.isSelected()) {
            return 2;
        } else if (radExpert.isSelected()) {
            return 3;
        }
        return 0;
    }
    public String getSelectedColor() {
    	if(radWhite.isSelected()) {
    		return MagicColor.Color.WHITE.getName();
    	} else if(radBlue.isSelected()) {
    		return MagicColor.Color.BLUE.getName();
    	} else if(radBlack.isSelected()) {
    		return MagicColor.Color.BLACK.getName();
    	} else if(radRed.isSelected()) {
    		return MagicColor.Color.RED.getName();
    	} else if(radGreen.isSelected()) {
    		return MagicColor.Color.GREEN.getName();
    	}
    	return MagicColor.Color.WHITE.getName();
    }
    public boolean isIronman() {
    	return cbIronman.isSelected();
    }
    public boolean isCheat() {
    	return cbCheats.isSelected();
    }
    

    public FLabel getBtnEmbark() {
        return btnEmbark;
    }

    //========== Overridden from IVDoc

    /* (non-Javadoc)
     * @see forge.gui.framework.IVDoc#getDocumentID()
     */
    @Override
    public EDocID getDocumentID() {
        return EDocID.HOME_SHANDALIKE_WORLDS;
    }

    /* (non-Javadoc)
     * @see forge.gui.framework.IVDoc#getTabLabel()
     */
    @Override
    public DragTab getTabLabel() {
        return tab;
    }

    /* (non-Javadoc)
     * @see forge.gui.framework.IVDoc#getLayoutControl()
     */
    @Override
    public CSubmenuWorld getLayoutControl() {
        return CSubmenuWorld.SINGLETON_INSTANCE;
    }

    /* (non-Javadoc)
     * @see forge.gui.framework.IVDoc#setParentCell(forge.gui.framework.DragCell)
     */
    @Override
    public void setParentCell(final DragCell cell0) {
        this.parentCell = cell0;
    }

    /* (non-Javadoc)
     * @see forge.gui.framework.IVDoc#getParentCell()
     */
    @Override
    public DragCell getParentCell() {
        return parentCell;
    }
}
