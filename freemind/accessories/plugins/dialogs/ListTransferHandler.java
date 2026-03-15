/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2006  Christian Foltin <christianfoltin@users.sourceforge.net>
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
/*$Id: ArrayListTransferHandler.java,v 1.1.4.2 2006/04/09 13:34:38 dpolivaev Exp $*/
package accessories.plugins.dialogs;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;
import freemind.swing.DefaultListModel;

@SuppressWarnings("serial")
public class ListTransferHandler extends TransferHandler {

	static private DataFlavor localListFlavor;
	static private DataFlavor[] dataFlavors;
	static {
		try {
			localListFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=java.util.List");
			dataFlavors = new DataFlavor[] { localListFlavor };
		} catch (ClassNotFoundException e) {
			System.out.println("ArrayListTransferHandler: unable to create data flavor");
		}
	}

	JList<?> source = null;
	int[] indices = null;
	int addIndex = -1; // Location where items were added
	int addCount = 0; // Number of items added


	@Override
	public boolean importData(JComponent c, Transferable t) {
		JList<?> target = (JList<?>) c;
		List<?> alist = null;

		if (!canImport(c, t.getTransferDataFlavors())) {
			return false;
		}
		try {
			alist = (List<?>) t.getTransferData(localListFlavor);
		} catch (UnsupportedFlavorException ufe) {
			System.out.println("importData: unsupported data flavor");
			return false;
		} catch (IOException ioe) {
			System.out.println("importData: I/O exception");
			return false;
		}

		// At this point we use the same code to retrieve the data
		// locally or serially.

		// We'll drop at the current selected index.
		int index = target.getSelectedIndex();

		// Prevent the user from dropping data back on itself.
		// For example, if the user is moving items #4,#5,#6 and #7 and
		// attempts to insert the items after item #5, this would
		// be problematic when removing the original items.
		// This is interpreted as dropping the same data on itself
		// and has no effect.
		if (source.equals(target)) {
			if (indices != null && index >= indices[0] - 1 && index <= indices[indices.length - 1]) {
				indices = null;
				return true;
			}
		}

		DefaultListModel listModel = (DefaultListModel<?>) target.getModel();
		int max = listModel.getSize();
		if (index < 0) {
			index = max;
		} else {
			index++;
			if (index > max) {
				index = max;
			}
		}
		addIndex = index;
		addCount = alist.size();

		listModel.addAll(index, alist);

		return true;
	}

	@Override
	protected void exportDone(JComponent c, Transferable data, int action) {
		if ((action == MOVE) && (indices != null)) {
			DefaultListModel model = (DefaultListModel<?>) source.getModel();

			// If we are moving items around in the same list, we
			// need to adjust the indices accordingly since those
			// after the insertion point have moved.
			if (addCount > 0) {
				for (int i = 0; i < indices.length; i++) {
					if (indices[i] > addIndex) {
						indices[i] += addCount;
					}
				}
			}
			for (int i = indices.length - 1; i >= 0; i--)
				model.remove(indices[i]);
		}
		indices = null;
		addIndex = -1;
		addCount = 0;
	}

	private boolean hasLocalListFlavor(DataFlavor[] flavors) {
		for (int i = 0; i < flavors.length; i++) {
			if (flavors[i].equals(localListFlavor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		if (hasLocalListFlavor(flavors)) {
			return true;
		}
		return false;
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		if (c instanceof JList) {
			source = (JList<?>) c;
			indices = source.getSelectedIndices();
			return new ListTransferable(source.getSelectedValuesList());
		}
		return null;
	}

	@Override
	public int getSourceActions(JComponent c) {
		return COPY_OR_MOVE;
	}

	public class ListTransferable implements Transferable {
		private List<?> data;

		public ListTransferable(List<?> list) {
			data = list;
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
			if (!isDataFlavorSupported(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return data;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return dataFlavors;
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return localListFlavor.equals(flavor);
		}
	}

}
