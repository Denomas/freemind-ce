/***************************************************************************
 *
 *   FreeMindSplash, taken from GanttSplash.java.
 *
 *   Copyright (C) 2002 by Thomas Alexandre (alexthomas(at)ganttproject.org)
 *   Copyright (C) 2005-2008 by Christian Foltin and Daniel Polansky
 *
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package freemind.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.text.MessageFormat;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import freemind.view.ImageFactory;

/**
 * Class to put a splash during launching the application.
 */

public class FreeMindSplashModern extends JFrame implements IFreeMindSplash {

	private static final long serialVersionUID = 1L;

	private static final String FREEMIND_SPLASH = "images/Freemind_Splash_Butterfly_Modern.png";

	private static final int SPLASH_HEIGHT = 200;
	private static final int SPLASH_WIDTH = 300;

	private class FeedBackImpl implements FeedBack {

		private int mActualValue;
		private long mActualTimeStamp = System.currentTimeMillis();
		private long mTotalTime = 0;
		private String lastTaskId = null;
		private JLabel mImageJLabel = null;

		public void progress(final int act, String messageId, Object[] pMessageParameters) {
			MessageFormat formatter = new MessageFormat(
					frame.getResourceString(messageId));
			final String progressString = formatter.format(pMessageParameters);
			logger.fine(progressString);
			this.mActualValue = act;
			long timeDifference = System.currentTimeMillis() - mActualTimeStamp;
			mActualTimeStamp = System.currentTimeMillis();
			mTotalTime += timeDifference;
			logger.fine("Task: " + lastTaskId + " (" + act + ") last "
					+ (timeDifference) / 1000.0 + " seconds.\nTotal: "
					+ mTotalTime / 1000.0 + "\n");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					mProgressBar.setValue(act);
					double percent = act * 1.0 / mProgressBar.getMaximum();
					mProgressBar.setString(progressString);
					if (mImageJLabel != null) {
						mImageJLabel.putClientProperty("progressString",
								progressString);
						mImageJLabel.putClientProperty("progressPercent",
								Double.valueOf(percent));
						mImageJLabel.repaint();
					}
				}
			});
			logger.fine("Beginnig task:" + messageId);
			lastTaskId = messageId;
			// this is not nice, as other windows are probably more important!
//			// make it the top most window.
//			FreeMindSplashModern.this.toFront();
		}

		public int getActualValue() {
			return mActualValue;
		}

		public void setMaximumValue(int max) {
			mProgressBar.setMaximum(max);
			mProgressBar.setIndeterminate(false);
		}

		public void increase(String messageId, Object[] pMessageParameters) {
			progress(getActualValue() + 1, messageId, pMessageParameters);
		}

		public void setImageJLabel(JLabel imageJLabel) {
			mImageJLabel = imageJLabel;
		}
	}

	private final FreeMindMain frame;
	private final FeedBackImpl feedBack; // !
	private JProgressBar mProgressBar;
	private static Logger logger;
	private ImageIcon mIcon;

	public FeedBack getFeedBack() {
		return feedBack;
	}

	public FreeMindSplashModern(final FreeMindMain frame) {
		super("FreeMind CE");
		this.frame = frame;
		if (logger == null) {
			logger = frame.getLogger(this.getClass().getName());
		}

		this.feedBack = new FeedBackImpl();

		// http://www.kde-look.org/content/show.php?content=76812
		// License GPLV2+
		ImageFactory imageFactory = ImageFactory.getInstance();
		mIcon = imageFactory.createIcon(
				frame.getResource("images/76812-freemind_v0.4.png"));
		setIconImage(mIcon.getImage()); // Set the icon
		setDefaultLookAndFeelDecorated(false);
		setUndecorated(true);
		getRootPane().setWindowDecorationStyle(JRootPane.NONE); // Set no border
		// lamentablemente since 1.5: setAlwaysOnTop(true);

		final ImageIcon splashImage = imageFactory.createIcon(frame.getResource(FREEMIND_SPLASH));
		JLabel splashImageLabel = new JLabel(splashImage) {
			private Integer mWidth = null;
			private final Font progressFont = new Font("SansSerif", Font.PLAIN, 10);
			private Font versionTextFont = Tools.isAvailableFontFamily("Century Gothic") ? new Font("Century Gothic", Font.BOLD, 14) : new Font("SansSerif", Font.BOLD, 13);
			private Font classicEditionFont = new Font("Serif", Font.ITALIC, 10);

			private int calcYRelative(int y){
				return (int) (((float)y)/SPLASH_HEIGHT*splashImage.getIconHeight());
			}

			private int calcXRelative(int x){
				return (int) (((float)x)/SPLASH_WIDTH*splashImage.getIconWidth());
			}

			public void paint(Graphics g) {
				super.paint(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setFont(versionTextFont);
				String freemindVersion = frame.getFreemindVersion().toString();
				if (mWidth == null) {
					mWidth = Integer.valueOf(g2.getFontMetrics().stringWidth(
							freemindVersion));
				}
				int yCoordinate = calcYRelative(58);
				int xCoordinate = (int) (getSize().getWidth() / 2 - mWidth
						.intValue() / 2);
				g2.setColor(new Color(0x4a, 0x90, 0xd9));
				g2.drawString(freemindVersion, xCoordinate, yCoordinate);
				// Draw "Classic Edition" subtitle
				g2.setFont(classicEditionFont);
				String ceText = "Classic Edition";
				int ceWidth = g2.getFontMetrics().stringWidth(ceText);
				int ceX = (int) (getSize().getWidth() / 2 - ceWidth / 2);
				g2.setColor(new Color(0x4a, 0x90, 0xd9, 180));
				g2.drawString(ceText, ceX, calcYRelative(80));
				// Draw progress bar
				String progressString = (String) getClientProperty("progressString");
				if (progressString != null) {
					Double percent = (Double) getClientProperty("progressPercent");
					int xBase = calcXRelative(7);
					int yBase = calcYRelative(185);
					int width = calcXRelative(281);
					int barHeight = calcYRelative(4);
					int arcSize = barHeight;
					g2.setFont(progressFont);
					g2.setColor(new Color(0xff, 0xff, 0xff, 200));
					g2.drawString(progressString, xBase + calcXRelative(1), yBase - calcYRelative(4));
					g2.setColor(new Color(0xff, 0xff, 0xff, 60));
					g2.fillRoundRect(xBase, yBase, width, barHeight, arcSize, arcSize);
					g2.setColor(new Color(0x4a, 0x90, 0xd9));
					int progressWidth = (int) (width * percent.doubleValue());
					if (progressWidth > 0) {
						g2.fillRoundRect(xBase, yBase, progressWidth, barHeight, arcSize, arcSize);
					}
				}
			}
		};

		feedBack.setImageJLabel(splashImageLabel);

		getContentPane().add(splashImageLabel, BorderLayout.CENTER);

		mProgressBar = new JProgressBar();
		mProgressBar.setIndeterminate(true);
		mProgressBar.setStringPainted(true);

		pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = splashImageLabel.getPreferredSize();

		// Put image at the middle of the screen
		setLocation(screenSize.width / 2 - (labelSize.width / 2),
				screenSize.height / 2 - (labelSize.height / 2));

	}

	public void close() {
		setVisible(false);
		dispose();
	}

	public ImageIcon getWindowIcon() {
		return mIcon;
	}

}
