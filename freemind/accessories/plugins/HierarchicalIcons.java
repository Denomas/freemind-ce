/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */


package accessories.plugins;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import freemind.extensions.UndoEventReceiver;
import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.hooks.PermanentMindMapNodeHookAdapter;
import freemind.view.mindmapview.MultipleImage;

/** */
public class HierarchicalIcons extends PermanentMindMapNodeHookAdapter
		implements UndoEventReceiver {

	private HashMap<MindMapNode, TreeSet<String>> nodeIconSets = new HashMap<>();

	public void shutdownMapHook() {
		// remove all icons:
		MindMapNode root = getMindMapController().getRootNode();
		removeIcons(root);
		super.shutdownMapHook();
	}

	/**
     */
	private void removeIcons(MindMapNode node) {
		node.setStateIcon(getName(), null);
		getMindMapController().nodeRefresh(node);
		for (Iterator<MindMapNode> i = node.childrenUnfolded(); i.hasNext();) {
			MindMapNode child = i.next();
			removeIcons(child);
		}
	}

	/**
     *
     */
	public HierarchicalIcons() {
		super();

	}

	private void setStyle(MindMapNode node) {
		// precondition: all children are contained in nodeIconSets

		// gather all icons of my children and of me here:
		TreeSet<String> iconSet = new TreeSet<>();
		for (Iterator<MindMapNode> i = node.childrenUnfolded(); i.hasNext();) {
			MindMapNode child = i.next();
			addAccumulatedIconsToTreeSet(child, iconSet, (TreeSet<String>) nodeIconSets.get(child));
		}
		// remove my icons from the treeset:
		for (MindIcon icon :node.getIcons()) {
			iconSet.remove(icon.getName());
		}
		boolean dirty = true;
		// look for a change:
		if (nodeIconSets.containsKey(node)) {
			TreeSet<String> storedIconSet = nodeIconSets.get(node);
			if (storedIconSet.equals(iconSet)) {
				dirty = false;
			}
		}
		nodeIconSets.put(node, iconSet);

		if (dirty) {
			if (iconSet.size() > 0) {
				// create multiple image:
				MultipleImage image = new MultipleImage(0.75f);
				for (String iconName : iconSet) {
					// logger.info("Adding icon "+iconName + " to node "+
					// node.toString());
					MindIcon icon = MindIcon.factory(iconName);
					image.addImage(icon.getIcon());
				}
				node.setStateIcon(getName(), image);
			} else {
				node.setStateIcon(getName(), null);
			}
			getMindMapController().nodeRefresh(node);
		}

	}

	/**
     */
	private void addAccumulatedIconsToTreeSet(MindMapNode child,
			TreeSet<String> iconSet, TreeSet<String> childsTreeSet) {
		for (MindIcon icon : child.getIcons()) {
			iconSet.add(icon.getName());
		}
		if (childsTreeSet == null)
			return;
		for (String iconName : childsTreeSet) {
			iconSet.add(iconName);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * freemind.extensions.PermanentNodeHook#onAddChild(freemind.modes.MindMapNode
	 * )
	 */
	public void onAddChildren(MindMapNode newChildNode) {
		logger.finest("onAddChildren " + newChildNode);
		super.onAddChild(newChildNode);
		setStyleRecursive(newChildNode);
	}

	public void onRemoveChildren(MindMapNode removedChild, MindMapNode oldDad) {
		logger.finest("onRemoveChildren " + removedChild);
		super.onRemoveChildren(removedChild, oldDad);
		setStyleRecursive(oldDad);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * freemind.extensions.PermanentNodeHook#onUpdateChildrenHook(freemind.modes
	 * .MindMapNode)
	 */
	public void onUpdateChildrenHook(MindMapNode updatedNode) {
		super.onUpdateChildrenHook(updatedNode);
		setStyleRecursive(updatedNode);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see freemind.extensions.PermanentNodeHook#onUpdateNodeHook()
	 */
	public void onUpdateNodeHook() {
		super.onUpdateNodeHook();
		setStyle(getNode());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see freemind.extensions.NodeHook#invoke(freemind.modes.MindMapNode)
	 */
	public void invoke(MindMapNode node) {
		super.invoke(node);
		gatherLeavesAndSetStyle(node);
		gatherLeavesAndSetParentsStyle(node);
	}

	/**
     */
	private void gatherLeavesAndSetStyle(MindMapNode node) {
		if (node.getChildCount() == 0) {
			// call setStyle for all leaves:
			setStyle(node);
			return;
		}
		for (Iterator<MindMapNode> i = node.childrenUnfolded(); i.hasNext();) {
			MindMapNode child = i.next();
			gatherLeavesAndSetStyle(child);
		}
	}

	/**
     */
	private void gatherLeavesAndSetParentsStyle(MindMapNode node) {
		if (node.getChildCount() == 0) {
			// call setStyleRecursive for all parents:
			if (node.getParentNode() != null) {
				setStyleRecursive(node.getParentNode());
			}
			return;
		}
		for (Iterator<MindMapNode> i = node.childrenUnfolded(); i.hasNext();) {
			MindMapNode child = i.next();
			gatherLeavesAndSetParentsStyle(child);
		}
	}

	/**
     */
	private void setStyleRecursive(MindMapNode node) {
		// logger.finest("setStyle " + node);
		setStyle(node);
		// recurse:
		if (node.getParentNode() != null) {
			setStyleRecursive(node.getParentNode());
		}
	}

}
