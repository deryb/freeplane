/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2008 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitry Polivaev
 *
 *  This file is modified by Dimitry Polivaev in 2008.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.core.ui.components;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenuBar;
import javax.swing.KeyStroke;

/**
 * This is the menu bar for Freeplane. Actions are defined in MenuListener.
 * Moreover, the StructuredMenuHolder of all menus are hold here.
 */
public class FreeplaneMenuBar extends JMenuBar {
	public static final String EDIT_MENU = FreeplaneMenuBar.MENU_BAR_PREFIX + "/edit";
	public static final String EXTRAS_MENU = FreeplaneMenuBar.MENU_BAR_PREFIX + "/extras";
	public static final String FILE_MENU = FreeplaneMenuBar.MENU_BAR_PREFIX + "/file";
	public static final String FORMAT_MENU = FreeplaneMenuBar.MENU_BAR_PREFIX + "/format";
	public static final String HELP_MENU = FreeplaneMenuBar.MENU_BAR_PREFIX + "/help";
	public static final String INSERT_MENU = FreeplaneMenuBar.MENU_BAR_PREFIX + "/insert";
	public static final String MAP_POPUP_MENU = "/map_popup";
	public static final String MENU_BAR_PREFIX = "/menu_bar";
	public static final String MINDMAP_MENU = FreeplaneMenuBar.MENU_BAR_PREFIX + "/mindmaps";
	public static final String MODES_MENU = FreeplaneMenuBar.MINDMAP_MENU + "/modes";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String VIEW_MENU = FreeplaneMenuBar.MENU_BAR_PREFIX + "/view";

	public FreeplaneMenuBar() {
	}

	static final int KEY_MODIFIERS = KeyEvent.SHIFT_DOWN_MASK | KeyEvent.SHIFT_MASK | KeyEvent.ALT_GRAPH_DOWN_MASK | KeyEvent.ALT_GRAPH_MASK;
	public static KeyStroke derive(KeyStroke ks, Character keyChar){
		if(ks == null){
			return ks;
		}
    	int modifiers = ks.getModifiers();
    	if(0 == (modifiers  & KEY_MODIFIERS) || ks.getKeyChar() != KeyEvent.CHAR_UNDEFINED){
    		return ks;
    	}
    	final int keyCode;
    	switch(keyChar){
    		case '<':
    			keyCode = KeyEvent.VK_LESS;
    			break;
    		case '>':
    			keyCode = KeyEvent.VK_GREATER;
    			break;
    		case '+':
    			keyCode = KeyEvent.VK_PLUS;
    			break;
    		case '-':
    			keyCode = KeyEvent.VK_MINUS;
    			break;
    		case '=':
    			keyCode = KeyEvent.VK_EQUALS;
    			break;
    		default:
    			return ks;
    	}
    	return KeyStroke.getKeyStroke(keyCode, modifiers & ~ KEY_MODIFIERS, ks.isOnKeyRelease());
    }
	
	@Override
	public boolean processKeyBinding(final KeyStroke ks, final KeyEvent e, final int condition, final boolean pressed) {
		if(super.processKeyBinding(ks, e, condition, pressed)){
			return true;
		}
		final KeyStroke derivedKS = derive(ks, e.getKeyChar());
		if(derivedKS == ks){
			return false;
		}
		return super.processKeyBinding(derivedKS, e, condition, pressed);
	}
}
