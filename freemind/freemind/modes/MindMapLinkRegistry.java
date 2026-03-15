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
/*$Id: MindMapLinkRegistry.java,v 1.6.18.1.16.4 2008/12/09 21:09:43 christianfoltin Exp $*/

package freemind.modes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Vector;

import freemind.main.Tools;

/**
 * Interface for the registry, which manages the ids of nodes and the existing
 * links in a map. Thus, this interface is bound to a map model, because other
 * maps have a different registry.
 */
public class MindMapLinkRegistry {
	/**
	 * All elements put into this sort of vectors are put into the
	 * SourceToLinks, too. This structure is kept synchronous to the IDToLinks
	 * structure, but reversed.
	 *
	 * @author foltin
	 * @date 23.01.2012
	 */
	@SuppressWarnings("serial")
	private class SynchronousVector extends Vector<MindMapLink> {
		/*
		 * (non-Javadoc)
		 *
		 * @see java.util.Vector#add(java.lang.Object)
		 */
		public synchronized boolean add(MindMapLink pE) {
			boolean add = super.add(pE);
			if (pE instanceof MindMapLink) {
				MindMapLink link = (MindMapLink) pE;
				MindMapNode source = link.getSource();
				if (!mSourceToLinks.containsKey(source)) {
					mSourceToLinks.put(source, new Vector<MindMapLink>());
				}
				mSourceToLinks.get(source).add(pE);
			}
			return add;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.util.Vector#removeElementAt(int)
		 */
		public synchronized void removeElementAt(int pIndex) {
			MindMapLink link = (MindMapLink) get(pIndex);
			MindMapNode source = link.getSource();
			Vector<MindMapLink> vector = mSourceToLinks.get(source);
			if (vector != null) {
				vector.remove(link);
				if (vector.isEmpty()) {
					mSourceToLinks.remove(source);
				}
			}
			super.removeElementAt(pIndex);
		}

	}

	/** source -> vector of links with same source */
	protected HashMap<MindMapNode, Vector<MindMapLink>> mSourceToLinks = new HashMap<>();

	// //////////////////////////////////////////////////////////////////////////////////////
	// // Attributes /////
	// //////////////////////////////////////////////////////////////////////////////////////

	/** MindMapNode = Target -> ID. */
	protected HashMap<MindMapNode, String> mTargetToId;
	/** MindMapNode-> ID. */
	protected HashMap<String, MindMapNode> mIdToTarget;
	/** id -> vector of links whose TargetToID.get(target) == id. */
	protected HashMap<String, Vector<MindMapLink>> mIdToLinks;
	/** id -> link */
	protected HashMap<String, MindMapLink> mIdToLink;
	/** id */
	protected HashSet<String> mLocallyLinkedIds;

	protected static java.util.logging.Logger logger = null;

	// //////////////////////////////////////////////////////////////////////////////////////
	// // Methods /////
	// //////////////////////////////////////////////////////////////////////////////////////
	public MindMapLinkRegistry(/* MindMap map */) {
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		mTargetToId = new HashMap<>();
		mIdToTarget = new HashMap<>();
		mIdToLinks = new HashMap<>();
		mIdToLink = new HashMap<>();
		mLocallyLinkedIds = new HashSet<>();
	}

	/**
	 * This can be used, if the id has to be known, before a node can be
	 * labeled.
	 */
	public String generateUniqueID(String proposedID) {
		return Tools.generateID(proposedID, mIdToLinks, "ID_");
	}

	/**
	 * This can be used, if the id has to be known, before a link can be labled.
	 */
	public String generateUniqueLinkId(String proposedID) {
		return Tools.generateID(proposedID, mIdToLink, "Arrow_ID_");
	}

	public String registerLinkTarget(MindMapNode pTarget) {
		return _registerLinkTarget(pTarget);

	}

	/**
	 * The second variant of the main method. The difference is that here an ID
	 * is proposed, but has not to be taken, though.
	 */
	public String registerLinkTarget(MindMapNode pTarget, String pProposedID) {
		return _registerLinkTarget(pTarget, pProposedID);
	}

	/**
	 * The main method. Registeres a node with a new (or an existing) node-id.
	 */
	public String _registerLinkTarget(MindMapNode target) {
		return _registerLinkTarget(target, null);
	}

	public String _registerLinkTarget(MindMapNode target, String proposedID) {
		// id already exists?
		if (mTargetToId.containsKey(target)) {
			String id = (String) mTargetToId.get(target);
			if (id != null)
				return id;
			// blank state.
			// is equal to no state.
		}
		// generate new id:
		String newId = generateUniqueID(proposedID);
		mTargetToId.put(target, newId);
		mIdToTarget.put(newId, target);

		// logger.fine("Register target node:"+target+", with ID="+newID);
		/*
		 * This is to allocate the link target in the IDToLinks map!.
		 */
		getAssignedLinksVector(newId);
		return newId;
	}

	/**
	 * @param node
	 * @return null, if not registered.
	 */
	public String getState(MindMapNode node) {
		if (mTargetToId.containsKey(node))
			return (String) mTargetToId.get(node);
		return null;
	}

	/**
	 * Reverses the getLabel method: searches for a node with the id given as
	 * the argument.
	 */
	public MindMapNode getTargetForId(String ID) {
		final Object target = mIdToTarget.get(ID);
		return (MindMapNode) target;
	}

	/** @return a Vector of {@link MindMapLink}s */
	private Vector<MindMapLink> getAssignedLinksVector(String newId) {
		String id = newId;
		// look, if target is already present:
		Vector<MindMapLink> vec;
		if (mIdToLinks.containsKey(id)) {
			vec = mIdToLinks.get(id);
		} else {
			vec = new SynchronousVector();
			mIdToLinks.put(id, vec);
		}

		// Dimitry : logger is a performance killer here
		// //logger.fine("getAssignedLinksVector "+vec);
		return vec;
	}

	/** If there are still targets registered, they are removed, too. */
	public void deregisterLinkTarget(MindMapNode target)
			throws java.lang.IllegalArgumentException {
		// deregister all links :
		Vector<MindMapLink> links = getAllLinks(target);
		for (int i = links.size() - 1; i >= 0; --i) {
			MindMapLink link = (MindMapLink) links.get(i);
			deregisterLink(link);
		}
		// and process my sons:
		for (ListIterator<MindMapNode> e = target.childrenUnfolded(); e.hasNext();) {
			MindMapNode child = e.next();
			deregisterLinkTarget(child);
		}
		String id = getState(target);
		if (id != null) {
			// logger.fine("Deregister target node:"+target);
			mTargetToId.remove(target);
			mIdToTarget.remove(id);
			mIdToLinks.remove(id);
		}
	}

	/**
	 * Method to keep track of the sources associated to a target node. This
	 * method also sets the new id to the target. Moreover, it is not required
	 * that the target node is already registered. This will be done on the fly.
	 */
	public void registerLink(MindMapLink link)
			throws java.lang.IllegalArgumentException {
		if ((link.getSource() == null) || (link.getTarget() == null)
				|| (link.getDestinationLabel() == null))
			throw new java.lang.IllegalArgumentException(
					"Illegal link specification." + link);
		MindMapNode source = link.getSource();
		MindMapNode target = link.getTarget();
		logger.fine("Register link (" + link + ") from source node:" + source
				+ " to target " + target);
		String id = _registerLinkTarget(target);
		Vector<MindMapLink> vec = getAssignedLinksVector(id);
		// already present?
		for (int i = 0; i < vec.size(); ++i) {
			if (vec.get(i) == link)
				return;
		}
		vec.add(link);
		String uniqueId = link.getUniqueId();
		if (uniqueId == null) {
			((LinkAdapter) link).setUniqueId(generateUniqueLinkId(uniqueId));
			uniqueId = link.getUniqueId();
		}
		if (mIdToLink.containsKey(uniqueId)) {
			if (mIdToLink.get(uniqueId) != link) {
				logger.warning("link with duplicated unique id found:" + link);
				// new id:
				((LinkAdapter) link)
						.setUniqueId(generateUniqueLinkId(uniqueId));
			}
		}
		mIdToLink.put(link.getUniqueId(), link);
	}

	public void deregisterLink(MindMapLink link) {
		MindMapNode target = link.getTarget();
		String id = _registerLinkTarget(target);
		Vector<MindMapLink> vec = getAssignedLinksVector(id);
		for (int i = vec.size() - 1; i >= 0; --i) {
			// logger.fine("Test for equal node:"+source+" to vector(i) " +
			// vec.get(i));
			if (vec.get(i) == link) {
				vec.removeElementAt(i);
				logger.info("Deregister link  (" + link + ") from source node:"
						+ link.getSource() + " to target " + target);
			}
		}
		mIdToLink.remove(link.getUniqueId());
	}

	/**
	 * Reverses the getUniqueID method: searches for a link with the id given as
	 * the argument.
	 */
	public MindMapLink getLinkForId(String pId) {
		if (mIdToLink.containsKey(pId)) {
			return (MindMapLink) mIdToLink.get(pId);
		}
		return null;
	}

	/**
	 * @return Returns a Vector of {@link MindMapNode}s that point to the given
	 *         target node.
	 */
	public Vector<MindMapNode> getAllSources(MindMapNode target) {
		Vector<MindMapNode> returnValue;
		returnValue = new Vector<>();
		String id = getState(target);
		if (id != null) {
			Vector<MindMapLink> vec = getAssignedLinksVector(id);
			for (int i = 0; i < vec.size(); ++i) {
				returnValue.add(vec.get(i).getSource());
			}
		}
		return returnValue;
	}

	/** @return returns all links from or to this node. */
	public Vector<MindMapLink> getAllLinks(MindMapNode node) {
		Vector<MindMapLink> returnValue = new Vector<>();
		returnValue.addAll(getAllLinksIntoMe(node));
		returnValue.addAll(getAllLinksFromMe(node));
		// Dimitry : logger is a performance killer here
		// //logger.fine("All links  ("+returnValue+") from  node:"+node);
		return returnValue;
	}

	/** @return returns all links to this node as {@link MindMapLink} vector. */
	public Vector<MindMapLink> getAllLinksIntoMe(MindMapNode target) {
		Vector<MindMapLink> returnValue = new Vector<MindMapLink>();
		String id = getState(target);
		if (id != null) {
			Vector<MindMapLink> vec = getAssignedLinksVector(id);
			/* "clone" */
			returnValue.addAll(vec);
		}
		return returnValue;
	}

	/** @return returns all links from this node as {@link MindMapLink} vector. */
	public Vector<MindMapLink> getAllLinksFromMe(MindMapNode source) {
		Vector<MindMapLink> returnValue = new Vector<MindMapLink>();
		Collection<MindMapLink> vec = mSourceToLinks.get(source);
		if (vec != null) {
			returnValue.addAll(vec);
		}
		return returnValue;
	}

	public String getLabel(MindMapNode target) {
		return getState(target);
	}

	public void registerLocalHyperlinkId(String pTargetId) {
		mLocallyLinkedIds.add(pTargetId);
	}

	public boolean isTargetOfLocalHyperlinks(String pTargetId) {
		return mLocallyLinkedIds.contains(pTargetId);
	}
}
