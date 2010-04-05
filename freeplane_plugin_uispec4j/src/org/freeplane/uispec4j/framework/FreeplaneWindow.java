package org.freeplane.uispec4j.framework;
import javax.swing.JComponent;
import javax.swing.JFrame;

import junit.framework.Assert;

import org.freeplane.core.controller.Controller;
import org.uispec4j.Panel;
import org.uispec4j.TextBox;
import org.uispec4j.UIComponent;
import org.uispec4j.Window;

import com.lightdev.app.shtm.SHTMLPanel;


public class FreeplaneWindow extends Window {
	public FreeplaneWindow(JFrame frame) {
		super(frame);
		// TODO Auto-generated constructor stub
	}
	
	public JFrame getFreeMindFrame(){
		return (JFrame)getAwtComponent();
	}
	
	public Controller getController(){
		return (Controller) ((JComponent)getFreeMindFrame().getContentPane()).getClientProperty(Controller.class);
	}
	public Panel getToolbar(final String name){
		return new Panel(getController().getModeController().getUserInputListenerFactory().getToolBar(name));
	}

	public TextBox getNoteEditor(){
		SHTMLPanel panel = getShtmlPanel();
		return new TextBox(panel.getEditorPane());
	}

	public TextBox getNoteHtmlEditor(){
		SHTMLPanel panel = getShtmlPanel();
		return new TextBox(panel.getSourceEditorPane());
	}

	private SHTMLPanel getShtmlPanel() {
		final UIComponent[] components = getUIComponents(SHTMLPanel.class);
		Assert.assertTrue(components.length == 1);
		SHTMLPanel panel = (SHTMLPanel)components[0].getAwtComponent();
		return panel;
	}
}
