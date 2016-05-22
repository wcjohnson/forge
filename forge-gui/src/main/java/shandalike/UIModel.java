package shandalike;

import java.util.ArrayList;
import java.util.List;

import forge.UiCommand;
import forge.item.IPaperCard;
import forge.item.PaperCard;
import groovy.lang.GroovyObject;

/**
 * Allows Shandalike scripting to interact with Forge UI at arms' length by presenting a data-driven model.
 * Script populates data and registers callbacks; pushes new data on changes.
 * forge-gui binds to the model and updates views appropriately via a controller.
 * @author wcj
 *
 */
public class UIModel {
	public interface ChangeListener {
		public void onMenuModelChanged();
	}
	
	@SuppressWarnings("serial")
	public static class Callback implements UiCommand {
		GroovyObject target = null;
		String method;
		Object arg1 = null, arg2 = null;
		UiCommand command = null;

		@Override
		public void run() {
			if(target != null) {
				Object[] args = { arg1, arg2 };
				Model.script.pcall(target, method, args);
			} else if (command != null) {
				command.run();
			}
		}
	}
	
	public interface Widget {
		public String getWidgetType();
	}
	public static class Button implements Widget {
		public Callback callback = new Callback();
		public String text;
		@Override
		public String getWidgetType() {
			return "Button";
		}
	}
	public static class Panel implements Widget {
		@Override
		public String getWidgetType() { return "Panel"; }
		public String title;
		public String leftText;
		public List<Button> buttons = new ArrayList<Button>();
	}
	public static class Cards implements Widget {
		@Override
		public String getWidgetType() { return "Cards"; }
		public String title;
		public String leftText;
		public List<PaperCard> cards;
		public void setCards(List<PaperCard> cards) { this.cards = cards; }
		public void setCardsFromIPaperCard(List<IPaperCard> icards) {
			cards = new ArrayList<PaperCard>();
			for(IPaperCard ipc: icards) {
				cards.add(new PaperCard(
						ipc.getRules(), ipc.getEdition(), ipc.getRarity(), ipc.getArtIndex(), ipc.isFoil()
				));
			}
		}
	}
	
	public List<Widget> widgets = new ArrayList<Widget>();
	public List<ChangeListener> listeners = new ArrayList<ChangeListener>();
	
	public void listen(ChangeListener l) {
		listeners.add(l);
	}
	public void unlisten(ChangeListener l) {
		listeners.remove(l);
	}
	public void update() {
		for(ChangeListener listener: listeners) listener.onMenuModelChanged();
	}
	
	public void addButton(String text, GroovyObject context, String method, Object arg1, Object arg2) {
		Button btn = new Button();
		btn.callback.target = context;
		btn.callback.method = method;
		btn.callback.arg1 = arg1;
		btn.callback.arg2 = arg2;
		btn.text = text;
		widgets.add(btn);
	}
	
	public void addButton(String text, UiCommand cmd) {
		Button btn = new Button();
		btn.callback.command = cmd;
		btn.text = text;
		widgets.add(btn);
	}
	
	public void addPanel(String title, String text, GroovyObject context, Object... buttons) {
		Panel pnl = new Panel();
		pnl.title = title; pnl.leftText = text;
		for(int i=0; i<buttons.length; i+=3) {
			Button btn = new Button();
			btn.callback.target = context;
			btn.text = (String)buttons[i];
			btn.callback.method = (String)buttons[i+1];
			btn.callback.arg1 = buttons[i+2];
			pnl.buttons.add(btn);
		}
		widgets.add(pnl);
	}
	
	public void addHeading(String title) {
		addPanel(title, null, null);
	}
	
	public void addCards(String title, String text, List<IPaperCard> cards) {
		Cards pnl = new Cards();
		pnl.title = title; pnl.leftText = text;
		pnl.setCardsFromIPaperCard(cards);
		widgets.add(pnl);
	}
	
	public void clear() {
		widgets.clear();
	}
}
