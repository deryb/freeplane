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
package org.freeplane.view.swing.map;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.JTextComponent;

import org.freeplane.core.model.MindIcon;
import org.freeplane.core.model.NodeModel;
import org.freeplane.core.resources.ResourceController;
import org.freeplane.core.ui.components.FreeplaneMenuBar;
import org.freeplane.core.ui.components.MultipleImage;
import org.freeplane.core.ui.components.UITools;
import org.freeplane.core.util.HtmlTools;
import org.freeplane.features.common.edge.EdgeController;
import org.freeplane.features.common.link.NodeLinks;
import org.freeplane.features.common.nodestyle.NodeStyleController;
import org.freeplane.features.common.nodestyle.NodeStyleModel;

/**
 * Base class for all node views.
 */
public abstract class MainView extends JLabel {
	public static final Set executableExtensions = new HashSet(Arrays.asList(new String[] { "exe", "com", "vbs", "bat",
	        "lnk" }));
	static Dimension maximumSize = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	private static final int MIN_HOR_NODE_SIZE = 10;
	static Dimension minimumSize = new Dimension(0, 0);
    final static private Graphics2D fmg;
    static{
    	fmg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
    	fmg.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final JComponent standardLabel = new JLabel();
	protected int isDraggedOver = NodeView.DRAGGED_OVER_NO;

	MainView() {
		setUI(MainViewUI.createUI(this));
		setAlignmentX(Component.CENTER_ALIGNMENT);
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalAlignment(SwingConstants.CENTER);
	}

	protected void convertPointFromMap(final Point p) {
		UITools.convertPointFromAncestor(getNodeView().getMap(), p, this);
	}

	protected void convertPointToMap(final Point p) {
		UITools.convertPointToAncestor(this, p, getNodeView().getMap());
	}

	public boolean dropAsSibling(final double xCoord) {
		return isInVerticalRegion(xCoord, 1. / 3);
	}

	/** @return true if should be on the left, false otherwise. */
	public boolean dropPosition(final double xCoord) {
		/* here it is the same as me. */
		return getNodeView().isLeft();
	}

	abstract int getAlignment();

	abstract Point getCenterPoint();

	/** get x coordinate including folding symbol */
	public int getDeltaX() {
		return 0;
	}

	/** get y coordinate including folding symbol */
	public int getDeltaY() {
		return 0;
	}

	public int getDraggedOver() {
		return isDraggedOver;
	}


	protected int getIconWidth() {
		final Icon icon = getIcon();
		if (icon == null) {
			return 0;
		}
		return getNodeView().getMap().getZoomed(icon.getIconWidth());
	}

	abstract Point getLeftPoint();

	/** get height including folding symbol */
	protected int getMainViewHeightWithFoldingMark() {
		return getHeight();
	}

	/** get width including folding symbol */
	protected int getMainViewWidthWithFoldingMark() {
		return getWidth();
	}

	@Override
	public Dimension getMaximumSize() {
		return MainView.maximumSize;
	}

	@Override
	public Dimension getMinimumSize() {
		return MainView.minimumSize;
	}

	public NodeView getNodeView() {
		return (NodeView) SwingUtilities.getAncestorOfClass(NodeView.class, this);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		if (isPreferredSizeSet()) {
			return super.getPreferredSize();
		}
 		return super.getPreferredSize();
	}

	abstract Point getRightPoint();

	abstract String getStyle();

	float getZoom() {
		final float zoom = getNodeView().getMap().getZoom();
		return zoom;
	}

	int getZoomedFoldingSymbolHalfWidth() {
		return getNodeView().getZoomedFoldingSymbolHalfWidth();
	}

	public boolean isInFollowLinkRegion(final double xCoord) {
		final NodeView nodeView = getNodeView();
		final NodeModel model = nodeView.getModel();
		return NodeLinks.getLink(model) != null
		        && (model.isRoot() || !nodeView.getMap().getModeController().getMapController().hasChildren(model) || isInVerticalRegion(
		            xCoord, 1. / 2));
	}

	/**
	 * Determines whether or not the xCoord is in the part p of the node: if
	 * node is on the left: part [1-p,1] if node is on the right: part[ 0,p] of
	 * the total width.
	 */
	public boolean isInVerticalRegion(final double xCoord, final double p) {
		return getNodeView().isLeft() ? xCoord > getSize().width * (1.0 - p) : xCoord < getSize().width * p;
	}
	
	protected void paintBackground(final Graphics2D graphics, final Color color) {
		graphics.setColor(color);
		graphics.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
	}

	public void paintDragOver(final Graphics2D graphics) {
		if (isDraggedOver == NodeView.DRAGGED_OVER_SON) {
			if (getNodeView().isLeft()) {
				graphics.setPaint(new GradientPaint(getWidth() * 3 / 4, 0, getNodeView().getMap().getBackground(),
				    getWidth() / 4, 0, NodeView.dragColor));
				graphics.fillRect(0, 0, getWidth() * 3 / 4, getHeight() - 1);
			}
			else {
				graphics.setPaint(new GradientPaint(getWidth() / 4, 0, getNodeView().getMap().getBackground(),
				    getWidth() * 3 / 4, 0, NodeView.dragColor));
				graphics.fillRect(getWidth() / 4, 0, getWidth() - 1, getHeight() - 1);
			}
		}
		if (isDraggedOver == NodeView.DRAGGED_OVER_SIBLING) {
			graphics.setPaint(new GradientPaint(0, getHeight() * 3 / 5, getNodeView().getMap().getBackground(), 0,
			    getHeight() / 5, NodeView.dragColor));
			graphics.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
	}

	void paintFoldingMark(final NodeView nodeView, final Graphics2D g, final Point p) {
		final int zoomedFoldingSymbolHalfWidth = getZoomedFoldingSymbolHalfWidth();
		p.translate(-zoomedFoldingSymbolHalfWidth, -zoomedFoldingSymbolHalfWidth);
		final Color color = g.getColor();
		g.setColor(Color.WHITE);
		g.fillOval(p.x, p.y, zoomedFoldingSymbolHalfWidth * 2, zoomedFoldingSymbolHalfWidth * 2);
		final NodeModel model = nodeView.getModel();
		final Color edgeColor = EdgeController.getController(nodeView.getMap().getModeController()).getColor(model);
		g.setColor(edgeColor);
		g.drawOval(p.x, p.y, zoomedFoldingSymbolHalfWidth * 2, zoomedFoldingSymbolHalfWidth * 2);
		g.setColor(color);
	}

	public void paintSelected(final Graphics2D graphics) {
		if (getNodeView().useSelectionColors()) {
			paintBackground(graphics, getNodeView().getSelectedColor());
		}
		else {
			final Color backgroundColor = NodeStyleModel.getBackgroundColor(getNodeView().getModel());
			if (backgroundColor != null) {
				paintBackground(graphics, backgroundColor);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#processKeyBinding(javax.swing.KeyStroke,
	 * java.awt.event.KeyEvent, int, boolean)
	 */
	@Override
	protected boolean processKeyBinding(final KeyStroke ks, final KeyEvent e, final int condition, final boolean pressed) {
		if (super.processKeyBinding(ks, e, condition, pressed)) {
			return true;
		}
		final MapView mapView = (MapView) SwingUtilities.getAncestorOfClass(MapView.class, this);
		final FreeplaneMenuBar freeplaneMenuBar = mapView.getModeController().getController().getViewController()
		    .getFreeplaneMenuBar();
		return !freeplaneMenuBar.isVisible()
		        && freeplaneMenuBar.processKeyBinding(ks, e, JComponent.WHEN_IN_FOCUSED_WINDOW, pressed);
	}

	public void setDraggedOver(final int draggedOver) {
		isDraggedOver = draggedOver;
	}

	public void setDraggedOver(final Point p) {
		setDraggedOver((dropAsSibling(p.getX())) ? NodeView.DRAGGED_OVER_SIBLING : NodeView.DRAGGED_OVER_SON);
	}

	/**
	 * @return true if a link is to be displayed and the curser is the hand now.
	 */
	public boolean updateCursor(final double xCoord) {
		final boolean followLink = isInFollowLinkRegion(xCoord);
		final int requiredCursor = followLink ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR;
		if (getCursor().getType() != requiredCursor) {
			setCursor(requiredCursor != Cursor.DEFAULT_CURSOR ? new Cursor(requiredCursor) : null);
		}
		return followLink;
	}

	public void updateFont(final NodeView node) {
		final Font font = NodeStyleController.getController(node.getMap().getModeController()).getFont(node.getModel());
		setFont(font);
	}

	void updateIcons(final NodeView node) {
		setHorizontalTextPosition(node.isLeft() ? SwingConstants.LEADING : SwingConstants.TRAILING);
		final MultipleImage iconImages = new MultipleImage(1.0f);
		boolean iconPresent = false;
		/* fc, 06.10.2003: images? */
		final NodeModel model = node.getModel();
		for(Entry<String, ImageIcon> iconEntry : model.getStateIcons().entrySet()) {
			iconPresent = true;
			final ImageIcon myIcon = iconEntry.getValue();
			iconImages.addImage(myIcon);
		}
		for(MindIcon myIcon : model.getIcons()) {
			iconPresent = true;
			iconImages.addImage(myIcon.getIcon());
		}
		final URI link = NodeLinks.getLink(model);
		if (link != null) {
			iconPresent = true;
			String iconPath = "/images/Link.png";
			if (link.toString().startsWith("#")) {
				iconPath = "/images/LinkLocal.png";
			}
			else if (link.toString().startsWith("mailto:")) {
				iconPath = "/images/Mail.png";
			}
			else if (executableExtensions.contains(link)) {
				iconPath = "/images/Executable.png";
			}
			final ImageIcon icon = new ImageIcon(ResourceController.getResourceController().getResource(iconPath));
			iconImages.addImage(icon);
		}
		setIcon((iconPresent ? iconImages : null));
	}

	void updateText(String nodeText) {
		final MapView map = (MapView) SwingUtilities.getAncestorOfClass(MapView.class, this);
		if(map == null){
			return;
		}
		final boolean isHtml = nodeText.startsWith("<html>");
		boolean widthMustBeRestricted = false;
		boolean isLong = false;
		int iconWidth = getIconWidth();
		if(iconWidth != 0){
			iconWidth += map.getZoomed(getIconTextGap());
		}
		if (!isHtml) {
			final String[] lines = nodeText.split("\n");
			for (int line = 0; line < lines.length; line++) {
				setText(lines[line]);
				widthMustBeRestricted = getPreferredSize().width > map.getZoomed(map.getMaxNodeWidth())
				        + iconWidth;
				if (widthMustBeRestricted) {
					break;
				}
			}
			isLong = widthMustBeRestricted || lines.length > 1;
		}
		if (isHtml) {
			if (nodeText.indexOf("<img") >= 0 && nodeText.indexOf("<base ") < 0) {
				nodeText = "<html><base href=\"" + map.getModel().getURL() + "\">" + nodeText.substring(6);
			}
			final String htmlLongNodeHead = ResourceController.getResourceController().getProperty(
			    "html_long_node_head");
			if (htmlLongNodeHead != null && !htmlLongNodeHead.equals("")) {
				if (nodeText.matches("(?ims).*<head>.*")) {
					nodeText = nodeText.replaceFirst("(?ims).*<head>.*", "<head>" + htmlLongNodeHead);
				}
				else {
					nodeText = nodeText.replaceFirst("(?ims)<html>", "<html><head>" + htmlLongNodeHead + "</head>");
				}
			}
			if (nodeText.length() < 30000) {
				setText(nodeText);
				widthMustBeRestricted = getPreferredSize().width > map.getZoomed(map.getMaxNodeWidth())
				        + iconWidth;
			}
			else {
				widthMustBeRestricted = true;
			}
			if (widthMustBeRestricted) {
				nodeText = nodeText.replaceFirst("(?i)<body>", "<body width=\"" + map.getMaxNodeWidth() + "\">");
			}
			setText(nodeText);
		}
		else if (nodeText.startsWith("<table>")) {
			final String[] lines = nodeText.split("\n");
			lines[0] = lines[0].substring(7);
			final int startingLine = lines[0].matches("\\s*") ? 1 : 0;
			String text = "<html><table border=1 style=\"border-color: white\">";
			for (int line = startingLine; line < lines.length; line++) {
				text += "<tr><td style=\"border-color: white;\">"
				        + HtmlTools.toXMLEscapedText(lines[line])
				            .replaceAll("\t", "<td style=\"border-color: white\">");
			}
			setText(text);
		}
		else if (isLong) {
			String text = HtmlTools.plainToHTML(nodeText);
			if (widthMustBeRestricted) {
				text = text.replaceFirst("(?i)<p>", "<p width=\"" + map.getMaxNodeWidth() + "\">");
			}
			setText(text);
		}
		else {
			setText(nodeText);
		}
	}

	@Override
    public FontMetrics getFontMetrics(Font font) {
		if(! useFractionalMetrics()){
			return super.getFontMetrics(font);
		}
		fmg.setFont(font);
		final FontMetrics fontMetrics = fmg.getFontMetrics();
		return fontMetrics;
    }

	boolean useFractionalMetrics() {
		MapView map = getNodeView().getMap();
		if(map.isPrinting()){
			return true;
		}
		final float zoom = map.getZoom();
		return 1f != zoom;
	}

	void updateTextColor(final NodeView node) {
		final Color color = NodeStyleController.getController(node.getMap().getModeController()).getColor(
		    node.getModel());
		setForeground(color);
	}

	public boolean isEdited() {
	    return getComponentCount() == 1 && getComponent(0) instanceof JTextComponent;
    }

	FontMetrics getFontMetrics() {
		return getFontMetrics(getFont());
    }

	@Override
    public void setText(String text) {
		if (! (BasicHTML.isHTMLString(text) && useFractionalMetrics())){
			super.setText(text);
			return;
		}
		setFractionalMetrics();
		super.setText(text);
		unsetFractionalMetrics();
    }

	private void unsetFractionalMetrics() {
		BasicHTML.createHTMLView(standardLabel, "<html><b>1</b>2");
    }

	private void setFractionalMetrics() {
		BasicHTML.createHTMLView(this, "<html><b>1</b>2");
    }

	
}
