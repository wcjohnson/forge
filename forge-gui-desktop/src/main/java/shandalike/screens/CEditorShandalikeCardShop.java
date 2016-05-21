/*
 * Forge: Play Magic: the Gathering.
 * Copyright (C) 2011  Forge Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package shandalike.screens;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Function;

import forge.assets.FSkinProp;
import forge.deck.DeckBase;
import forge.gui.framework.DragCell;
import forge.gui.framework.FScreen;
import forge.item.InventoryItem;
import forge.itemmanager.ColumnDef;
import forge.itemmanager.ItemManagerConfig;
import forge.itemmanager.SpellShopManager;
import forge.itemmanager.views.ItemTableColumn;
import forge.screens.deckeditor.controllers.ACEditorBase;
import forge.screens.deckeditor.controllers.DeckController;
import forge.screens.deckeditor.views.VAllDecks;
import forge.screens.deckeditor.views.VCardCatalog;
import forge.screens.deckeditor.views.VCurrentDeck;
import forge.screens.deckeditor.views.VDeckgen;
import forge.screens.deckeditor.views.VProbabilities;
import forge.screens.home.quest.CSubmenuQuestDecks;
import forge.screens.match.controllers.CDetailPicture;
import forge.toolbox.FLabel;
import forge.toolbox.FSkin;
import forge.util.ItemPool;
import forge.util.gui.SOptionPane;
import shandalike.Model;
import shandalike.mtg.ShopModel;

/**
 * UI controller for 
 * 
 * @author Forge
 * @version $Id: CEditorQuestCardShop.java 15088 2012-04-07 11:34:05Z Max mtg $
 */
public final class CEditorShandalikeCardShop extends ACEditorBase<InventoryItem, DeckBase> {
	private ShopModel model;
	
	
	//////////////////////////// Comparator functions
    public final Function<Entry<? extends InventoryItem, Integer>, Object> fnPriceBuyGet = 
    new Function<Entry<? extends InventoryItem, Integer>, Object>() {
        @Override
        public Object apply(final Entry<? extends InventoryItem, Integer> from) {
        	return model.getBuyPrice(from.getKey(), 1);
        }
    };
    public final Function<Entry<? extends InventoryItem, Integer>, Object> fnPriceSellGet =
    new Function<Entry<? extends InventoryItem, Integer>, Object>() {
        @Override
        public Object apply(final Entry<? extends InventoryItem, Integer> from) {
        	return model.getSellPrice(from.getKey(), 1);
        }
    };
    public final Function<Entry<? extends InventoryItem, Integer>, Object> fnOwnedGet =
    new Function<Entry<? extends InventoryItem, Integer>, Object>() {
        @Override
        public Object apply(final Entry<? extends InventoryItem, Integer> from) {
        	return model.getQtyOwned(from.getKey());
        }
    };
    public final Function<Entry<InventoryItem, Integer>, Comparable<?>> fnPriceCompare
    = new Function<Entry<InventoryItem, Integer>, Comparable<?>>() {
        @Override
        public Comparable<?> apply(final Entry<InventoryItem, Integer> from) {
            return model.getBuyPrice(from.getKey(), 1);
        }
    };
    public final Function<Entry<InventoryItem, Integer>, Comparable<?>> fnOwnedCompare
    = new Function<Entry<InventoryItem, Integer>, Comparable<?>>() {
        @Override
        public Comparable<?> apply(final Entry<InventoryItem, Integer> from) {
            return model.getQtyOwned(from.getKey());
        }
    };
	
    private final FLabel creditsLabel = new FLabel.Builder()
            .icon(FSkin.getIcon(FSkinProp.ICO_QUEST_COINSTACK))
            .fontSize(15).build();

    // TODO: move these to the view where they belong
    private final FLabel sellPercentageLabel = new FLabel.Builder()
    		.icon(FSkin.getIcon(FSkinProp.ICO_QUEST_COINSTACK))
    		.text("0")
            .fontSize(15)
            .build();

    private ItemPool<InventoryItem> cardsForSale;
//    private final ItemPool<InventoryItem> fullCatalogCards =
//            ItemPool.createFrom(FModel.getMagicDb().getCommonCards().getAllCards(), InventoryItem.class);
//    private boolean showingFullCatalog = false;
    private DragCell allDecksParent = null;
    private DragCell deckGenParent = null;
    private DragCell probsParent = null;

    // remember changed gui elements
    private String CCTabLabel = new String();
    private String CCAddLabel = new String();
    private String CDTabLabel = new String();
    private String CDRemLabel = new String();

    /**
     * Child controller for quest card shop UI.
     * 
     * @param qd
     *            a {@link forge.quest.data.QuestData} object.
     */
    public CEditorShandalikeCardShop(final ShopModel m, final CDetailPicture cDetailPicture) {
        super(FScreen.QUEST_CARD_SHOP, cDetailPicture);

        this.model = m;

        final SpellShopManager catalogManager = new SpellShopManager(getCDetailPicture(), false);
        final SpellShopManager deckManager = new SpellShopManager(getCDetailPicture(), false);

        catalogManager.setCaption("Card Shop");
        deckManager.setCaption("Your Inventory");

        catalogManager.setAlwaysNonUnique(true);
        deckManager.setAlwaysNonUnique(true);

        this.setCatalogManager(catalogManager);
        this.setDeckManager(deckManager);
    }

    //=========== Overridden from ACEditorBase

    @Override
    protected CardLimit getCardLimit() {
        return CardLimit.None;
    }

    /* (non-Javadoc)
     * @see forge.gui.deckeditor.ACEditorBase#onAddItems()
     */
    @Override
    protected void onAddItems(Iterable<Entry<InventoryItem, Integer>> items, boolean toAlternate) {
        if (toAlternate || !model.canBuy()) {
            return;
        }
        
        // Interrogate the model
        ShopModel.TransactionResult br = model.buy(items, true);
        if(br.errorMessage != null) {
        	SOptionPane.showMessageDialog(br.errorMessage, "Shop");
        	return;
        }
        br = model.buy(items, false);
        
        // Update the views
        if(br.itemsShop != null) this.getCatalogManager().removeItems(br.itemsShop);
        if(br.itemsInventory != null) this.getDeckManager().addItems(br.itemsInventory);
        updateCreditsLabel();
    }

    /* (non-Javadoc)
     * @see forge.gui.deckeditor.ACEditorBase#onRemoveItems()
     */
    @Override
    protected void onRemoveItems(Iterable<Entry<InventoryItem, Integer>> items, boolean toAlternate) {
        if (toAlternate || !model.canSell()) { return; }
        
        // Interrogate the model
        ShopModel.TransactionResult sr = model.sell(items, true);
        if(sr.errorMessage != null) {
        	SOptionPane.showMessageDialog(sr.errorMessage, "Shop");
        	return;
        }
        sr = model.sell(items, false);
        
        // Update the views
        if(sr.itemsShop != null) this.getCatalogManager().addItems(sr.itemsShop);
        if(sr.itemsInventory != null) this.getDeckManager().removeItems(sr.itemsInventory);
        updateCreditsLabel();
    }

    @Override
    protected void buildAddContextMenu(EditorContextMenuBuilder cmb) {
           cmb.addMoveItems("Buy", null);
    }

    @Override
    protected void buildRemoveContextMenu(EditorContextMenuBuilder cmb) {
        cmb.addMoveItems("Sell", null);
    }

    private void updateCreditsLabel() {
    	this.creditsLabel.setText(model.getCurrencyName() + ": " + model.getPlayerCurrency());
    	this.sellPercentageLabel.setText(model.getCurrencyName() + ": " + model.getShopCurrency());
    }

    /*
     * (non-Javadoc)
     * 
     * @see forge.gui.deckeditor.ACEditorBase#resetTables()
     */
    @Override
    public void resetTables() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see forge.gui.deckeditor.ACEditorBase#getController()
     */
    @Override
    public DeckController<DeckBase> getDeckController() {
        return null;
    }

    /* (non-Javadoc)
     * @see forge.gui.deckeditor.ACEditorBase#show(forge.Command)
     */
    @Override
    public void update() {
        final Map<ColumnDef, ItemTableColumn> colOverridesCatalog = new HashMap<ColumnDef, ItemTableColumn>();
        final Map<ColumnDef, ItemTableColumn> colOverridesDeck = new HashMap<ColumnDef, ItemTableColumn>();

        // Add spell shop-specific columns
        ItemTableColumn.addColOverride(ItemManagerConfig.SPELL_SHOP, colOverridesCatalog, ColumnDef.PRICE, fnPriceCompare, fnPriceBuyGet);
        ItemTableColumn.addColOverride(ItemManagerConfig.SPELL_SHOP, colOverridesCatalog, ColumnDef.OWNED, fnOwnedCompare, fnOwnedGet);
        ItemTableColumn.addColOverride(ItemManagerConfig.QUEST_INVENTORY, colOverridesDeck, ColumnDef.PRICE, fnPriceCompare, fnPriceSellGet);
        ItemTableColumn.addColOverride(ItemManagerConfig.QUEST_INVENTORY, colOverridesDeck, ColumnDef.NEW, Model.adventure.getPlayer().getInventory().fnNewCompare, Model.adventure.getPlayer().getInventory().fnNewGet);
        ItemTableColumn.addColOverride(ItemManagerConfig.QUEST_INVENTORY, colOverridesDeck, ColumnDef.DECKS, fnOwnedCompare, fnOwnedGet);

        // Setup with current column set
        this.getCatalogManager().setup(ItemManagerConfig.SPELL_SHOP, colOverridesCatalog);
        this.getDeckManager().setup(ItemManagerConfig.QUEST_INVENTORY, colOverridesDeck);

        resetUI();

        CCTabLabel = VCardCatalog.SINGLETON_INSTANCE.getTabLabel().getText();
        VCardCatalog.SINGLETON_INSTANCE.getTabLabel().setText("Cards for sale");

        CCAddLabel = this.getBtnAdd().getText();
        this.getBtnAdd().setText("Buy Card");

        CDTabLabel = VCurrentDeck.SINGLETON_INSTANCE.getTabLabel().getText();
        VCurrentDeck.SINGLETON_INSTANCE.getTabLabel().setText("Your Cards");

        CDRemLabel = this.getBtnRemove().getText();
        this.getBtnRemove().setText("Sell Card");

        this.getBtnAddBasicLands().setVisible(false);

        VProbabilities.SINGLETON_INSTANCE.getTabLabel().setVisible(false);

        VCurrentDeck.SINGLETON_INSTANCE.getPnlHeader().setVisible(false);

        this.cardsForSale = model.getShopInventory(); 

        final ItemPool<InventoryItem> ownedItems = new ItemPool<InventoryItem>(InventoryItem.class);
        ownedItems.addAllOfType(model.getPlayerInventory());

        this.getCatalogManager().setPool(cardsForSale);
        this.getDeckManager().setPool(ownedItems);

//        this.getBtnRemove4().setText("Sell all extras");
//        this.getBtnRemove4().setToolTipText("Sell unneeded extra copies of all cards");
//        this.getBtnRemove4().setCommand(new UiCommand() {
//            @Override
//            public void run() {
//                QuestSpellShop.sellExtras(getCatalogManager(), getDeckManager());
//                updateCreditsLabel();
//            }
//        });

        this.getDeckManager().getPnlButtons().remove(this.getBtnRemove4());
        this.getDeckManager().getPnlButtons().add(creditsLabel, "gap 5px");

        this.getCatalogManager().getPnlButtons().remove(this.getBtnAdd4());
        this.getCatalogManager().getPnlButtons().add(sellPercentageLabel, "gap 5px");
        updateCreditsLabel();
//        this.sellPercentageLabel.setText("<html>Selling cards at " + formatter.format(multiPercent)
//                + "% of their value.<br>" + maxSellingPrice + "</html>");

        //TODO: Add filter for SItemManagerUtil.StatTypes.PACK

        deckGenParent = removeTab(VDeckgen.SINGLETON_INSTANCE);
        allDecksParent = removeTab(VAllDecks.SINGLETON_INSTANCE);
        probsParent = removeTab(VProbabilities.SINGLETON_INSTANCE);
    }

    /* (non-Javadoc)
     * @see forge.gui.deckeditor.controllers.ACEditorBase#canSwitchAway()
     */
    @Override
    public boolean canSwitchAway(boolean isClosing) {
        return true;
    }

    /* (non-Javadoc)
     * @see forge.gui.deckeditor.controllers.ACEditorBase#resetUIChanges()
     */
    @Override
    public void resetUIChanges() {
        CSubmenuQuestDecks.SINGLETON_INSTANCE.update();

        // undo Card Shop Specifics
        this.getCatalogManager().getPnlButtons().remove(sellPercentageLabel);
        this.getCatalogManager().getPnlButtons().add(this.getBtnAdd4());

        this.getDeckManager().getPnlButtons().remove(creditsLabel);
        this.getDeckManager().getPnlButtons().add(this.getBtnRemove4());
//        this.getBtnRemove4().setText(prevRem4Label);
//        this.getBtnRemove4().setToolTipText(prevRem4Tooltip);
//        this.getBtnRemove4().setCommand(prevRem4Cmd);

        VCardCatalog.SINGLETON_INSTANCE.getTabLabel().setText(CCTabLabel);
        VCurrentDeck.SINGLETON_INSTANCE.getTabLabel().setText(CDTabLabel);

        this.getBtnAdd().setText(CCAddLabel);
        this.getBtnRemove().setText(CDRemLabel);

        //TODO: Remove filter for SItemManagerUtil.StatTypes.PACK

        //Re-add tabs
        if (deckGenParent != null) {
            deckGenParent.addDoc(VDeckgen.SINGLETON_INSTANCE);
        }
        if (allDecksParent != null) {
            allDecksParent.addDoc(VAllDecks.SINGLETON_INSTANCE);
        }
        if (probsParent != null) {
            probsParent.addDoc(VProbabilities.SINGLETON_INSTANCE);
        }
    }
}
