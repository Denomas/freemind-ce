/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 *
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
 *
 * Created on 23.06.2004
 */

package freemind.common;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import freemind.controller.Controller;
import freemind.controller.actions.generated.instance.WindowConfigurationStorage;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Resources;

/**
 * @author foltin Singleton
 * Rewritten for JAXB (replacing JiBX) as part of FreeMind CE modernization.
 */
public class XmlBindingTools {

	private static JAXBContext jaxbContext;

	private static class Holder {
		static final XmlBindingTools INSTANCE = createInstance();

		private static XmlBindingTools createInstance() {
			XmlBindingTools tools = new XmlBindingTools();
			try {
				jaxbContext = JAXBContext.newInstance(
						"freemind.controller.actions.generated.instance");
			} catch (JAXBException e) {
				freemind.main.Resources.getInstance().logException(e);
			}
			return tools;
		}
	}

	private XmlBindingTools() {
	}

	public static XmlBindingTools getInstance() {
		return Holder.INSTANCE;
	}

	public Marshaller createMarshaller() {
		try {
			Marshaller m = jaxbContext.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
			m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			return m;
		} catch (JAXBException e) {
			freemind.main.Resources.getInstance().logException(e);
			return null;
		}
	}

	public Unmarshaller createUnmarshaller() {
		try {
			return jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			freemind.main.Resources.getInstance().logException(e);
			return null;
		}
	}

	public void storeDialogPositions(Controller controller, JDialog dialog,
			WindowConfigurationStorage storage,
			String window_preference_storage_property) {
		String result = storeDialogPositions(storage, dialog);
		controller.setProperty(window_preference_storage_property, result);
	}

	protected String storeDialogPositions(WindowConfigurationStorage storage,
			JDialog dialog) {
		storage.setX((dialog.getX()));
		storage.setY((dialog.getY()));
		storage.setWidth((dialog.getWidth()));
		storage.setHeight((dialog.getHeight()));
		String marshalled = marshall(storage);
		String result = marshalled;
		return result;
	}

	public WindowConfigurationStorage decorateDialog(Controller controller,
			JDialog dialog, String window_preference_storage_property) {
		String marshalled = controller
				.getProperty(window_preference_storage_property);
		WindowConfigurationStorage result = decorateDialog(marshalled, dialog);
		return result;
	}

	public WindowConfigurationStorage decorateDialog(String marshalled,
			JDialog dialog) {
		if (marshalled != null) {
			WindowConfigurationStorage storage = (WindowConfigurationStorage) unMarshall(marshalled);
			if (storage != null) {
				Dimension screenSize;
				if (Resources.getInstance().getBoolProperty(
						"place_dialogs_on_first_screen")) {
					Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
					screenSize = defaultToolkit.getScreenSize();
				} else {
					screenSize = new Dimension();
					screenSize.height = Integer.MAX_VALUE;
					screenSize.width = Integer.MAX_VALUE;
				}
				int delta = 20;
				dialog.setLocation(
						Math.min(storage.getX(), screenSize.width - delta),
						Math.min(storage.getY(), screenSize.height - delta));
				dialog.setSize(new Dimension(storage.getWidth(), storage
						.getHeight()));
				return storage;
			}
		}

		final Frame rootFrame = JOptionPane.getFrameForComponent(dialog);
		final Dimension prefSize = rootFrame.getSize();
		prefSize.width = prefSize.width * 3 / 4;
		prefSize.height = prefSize.height * 3 / 4;
		dialog.setSize(prefSize);
		return null;
	}

	public String marshall(XmlAction action) {
		StringWriter writer = new StringWriter();
		Marshaller m = XmlBindingTools.getInstance().createMarshaller();
		try {
			m.marshal(action, writer);
		} catch (JAXBException e) {
			freemind.main.Resources.getInstance().logException(e);
			return null;
		}
		String result = writer.toString();
		return result;
	}

	public XmlAction unMarshall(String inputString) {
		return unMarshall(new StringReader(inputString));
	}

	public XmlAction unMarshall(Reader reader) {
		try {
			Unmarshaller u = XmlBindingTools.getInstance().createUnmarshaller();
			SAXSource source = createSecureSAXSource(reader);
			XmlAction doAction = (XmlAction) u.unmarshal(source);
			return doAction;
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			return null;
		}
	}

	/**
	 * Generic unmarshall for property-based testing and typed deserialization.
	 */
	@SuppressWarnings("unchecked")
	public <T> T unMarshall(Reader reader, Class<T> type) {
		try {
			Unmarshaller u = XmlBindingTools.getInstance().createUnmarshaller();
			SAXSource source = createSecureSAXSource(reader);
			Object result = u.unmarshal(source);
			return (T) result;
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			return null;
		}
	}

	/**
	 * Creates a SAXSource with secure processing enabled to prevent
	 * XML Entity Expansion (Billion Laughs) attacks.
	 */
	private SAXSource createSecureSAXSource(Reader reader) throws Exception {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		XMLReader xmlReader = spf.newSAXParser().getXMLReader();
		return new SAXSource(xmlReader, new InputSource(reader));
	}
}
