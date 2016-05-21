package shandalike.screens;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import forge.screens.match.QuestWinLoseCardViewer;
import forge.toolbox.FLabel;
import forge.toolbox.FPanel;
import net.miginfocom.swing.MigLayout;
import shandalike.UIModel;
import shandalike.UIModel.Widget;

@SuppressWarnings("serial")
public class MenuPanel extends JPanel implements HierarchyListener, UIModel.ChangeListener {
	public interface WidgetViewController {
		public Component build(MenuPanel panel, UIModel.Widget widget);
		public void update(Component component, UIModel.Widget widget);
		public String getConstraints();
	}
	
	public static class ButtonController implements WidgetViewController {
		@Override
		public Component build(MenuPanel panel, Widget widget) {
			FLabel btn = new FLabel.ButtonBuilder().fontSize(16).build();
			this.update(btn, widget);
			return btn;
		}

		@Override
		public void update(Component component, Widget widget) {
			FLabel btn = (FLabel)component;
			UIModel.Button btnModel = (UIModel.Button)widget;
			btn.setText(btnModel.text);
			btn.setCommand(btnModel.callback);
		}

		@Override
		public String getConstraints() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public static class PanelController implements WidgetViewController {
		@Override
		public Component build(MenuPanel panel, Widget widget) {
			UIModel.Panel panelModel = (UIModel.Panel)widget;
			FPanel pnl = new FPanel(new MigLayout("insets 10px, gap 0, fill","[al left][al right]"));
			JPanel pnlText = new JPanel(new MigLayout("insets 0, gap 10px, flowy"));
			pnl.add(pnlText, "growx");
			pnlText.setOpaque(false);
			JPanel pnlButtons = new JPanel(new MigLayout("insets 0, gap 10px, ax right, ay center"));
			pnl.add(pnlButtons, "growy");
			pnlButtons.setOpaque(false);
			pnlText.add(new FLabel.Builder().fontAlign(SwingConstants.LEFT).fontSize(16).fontStyle(Font.BOLD).build(), "w 100%");
			pnlText.add(new FLabel.Builder().fontAlign(SwingConstants.LEFT).fontSize(16).build(), "w 100%");
			for(int i=0; i<panelModel.buttons.size(); i++) {
				pnlButtons.add(new FLabel.ButtonBuilder().fontSize(16).build(), "h 90%!");
			}
			this.update(pnl, widget);
			return pnl;
		}

		@Override
		public void update(Component component, Widget widget) {
			FPanel uiPanel = (FPanel)component;
			UIModel.Panel panelModel = (UIModel.Panel)widget;
			FLabel txtTitle = (FLabel)( ((JPanel)uiPanel.getComponent(0)).getComponent(0) );
			FLabel txtMain = (FLabel)( ((JPanel)uiPanel.getComponent(0)).getComponent(1) );
			txtTitle.setText(panelModel.title);
			txtMain.setText(panelModel.leftText);
			for(int i=0; i<panelModel.buttons.size(); i++) {
				FLabel btn = (FLabel)( ((JPanel)uiPanel.getComponent(1)).getComponent(i) );
				btn.setText(panelModel.buttons.get(i).text);
				btn.setCommand(panelModel.buttons.get(i).callback);
			}
		}

		@Override
		public String getConstraints() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public static class CardsController implements WidgetViewController {
		@Override
		public Component build(MenuPanel panel, Widget widget) {
			UIModel.Cards panelModel = (UIModel.Cards)widget;
			FPanel pnl = new FPanel(new MigLayout("insets 10px, gap 0, fill","[al left][al right]"));
			JPanel pnlText = new JPanel(new MigLayout("insets 0, gap 10px, flowy"));
			pnl.add(pnlText, "growx");
			pnlText.setOpaque(false);
			FPanel pnlCards = new QuestWinLoseCardViewer(panelModel.cards);
			pnl.add(pnlCards, "w 45%!, h 330px!");
			pnlText.add(new FLabel.Builder().fontAlign(SwingConstants.LEFT).fontSize(16).fontStyle(Font.BOLD).build(), "w 100%");
			pnlText.add(new FLabel.Builder().fontAlign(SwingConstants.LEFT).fontSize(16).build(), "w 100%");
			this.update(pnl, widget);
			return pnl;
		}

		@Override
		public void update(Component component, Widget widget) {
			FPanel uiPanel = (FPanel)component;
			UIModel.Cards panelModel = (UIModel.Cards)widget;
			FLabel txtTitle = (FLabel)( ((JPanel)uiPanel.getComponent(0)).getComponent(0) );
			FLabel txtMain = (FLabel)( ((JPanel)uiPanel.getComponent(0)).getComponent(1) );
			txtTitle.setText(panelModel.title);
			txtMain.setText(panelModel.leftText);
		}

		@Override
		public String getConstraints() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public static final Map<String, WidgetViewController> viewControllers;
	static {
		viewControllers = new HashMap<String, WidgetViewController>();
		viewControllers.put("Button", new ButtonController());
		viewControllers.put("Panel", new PanelController());
		viewControllers.put("Cards", new CardsController());
	}
	
	
	public UIModel model;
	public ArrayList<String> componentTypes = new ArrayList<String>();
	
	public MenuPanel() {
		super(new MigLayout("insets 10px, gap 5px, wrap 1, ax center"));
		this.setOpaque(false);
		this.addHierarchyListener(this);
	}
	
	public void setModel(UIModel model) {
		if(this.model != null) {
			this.model.unlisten(this);
		}
		if(model != null) {
			model.listen(this);
		}
		this.model = model;
		rebuild();
	}

	// Stop listening to the model when we get removed from Swing.
	@Override
	public void hierarchyChanged(HierarchyEvent e) {
		//check for Hierarchy event
	   if(e.getChangeFlags() == HierarchyEvent.DISPLAYABILITY_CHANGED) {       
	        //do the required action upon close
	       if(!this.isDisplayable()) {
	    	   if(model != null) model.unlisten(this);
	       }
	   }	
	}
	
	// Rebuild the whole shebang.
	private void rebuild() {
		this.removeAll();
		componentTypes.clear();
		for(UIModel.Widget w: model.widgets) {
			WidgetViewController ctrl = viewControllers.get(w.getWidgetType());
			String constraints = ctrl.getConstraints();
			if(constraints == null) constraints = "h 50px::, w 90%!";
			if(ctrl != null) {
				componentTypes.add(w.getWidgetType());
				this.add(ctrl.build(this, w), constraints);
			}
		}
		this.revalidate();
		this.repaint();
	}

	@Override
	public void onMenuModelChanged() {
		rebuild();
	}
}
