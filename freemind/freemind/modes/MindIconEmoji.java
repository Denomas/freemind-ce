/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2026 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
 */

package freemind.modes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps FreeMind built-in icon names to Unicode emoji characters.
 * Used by HTML export to produce standalone HTML files without external icon images.
 */
public final class MindIconEmoji {

	private static final Map<String, String> ICON_TO_EMOJI;

	static {
		Map<String, String> m = new HashMap<>();

		// Status / decision (icons.list order)
		m.put("idea", "\uD83D\uDCA1");           // 💡 light bulb
		m.put("help", "\u2753");                   // ❓ question mark
		m.put("yes", "\u2714\uFE0F");             // ✔️ blue check mark
		m.put("messagebox_warning", "\u26A0\uFE0F"); // ⚠️ warning sign
		m.put("stop-sign", "\uD83D\uDED1");       // 🛑 stop sign (octagon)
		m.put("closed", "\uD83D\uDD12");           // 🔒 closed lock
		m.put("info", "\u2139\uFE0F");             // ℹ️ info
		m.put("button_ok", "\u2705");              // ✅ green check
		m.put("button_cancel", "\u274C");          // ❌ red cross

		// Numbers (full-1 through full-0)
		m.put("full-1", "1\uFE0F\u20E3");         // 1️⃣
		m.put("full-2", "2\uFE0F\u20E3");         // 2️⃣
		m.put("full-3", "3\uFE0F\u20E3");         // 3️⃣
		m.put("full-4", "4\uFE0F\u20E3");         // 4️⃣
		m.put("full-5", "5\uFE0F\u20E3");         // 5️⃣
		m.put("full-6", "6\uFE0F\u20E3");         // 6️⃣
		m.put("full-7", "7\uFE0F\u20E3");         // 7️⃣
		m.put("full-8", "8\uFE0F\u20E3");         // 8️⃣
		m.put("full-9", "9\uFE0F\u20E3");         // 9️⃣
		m.put("full-0", "0\uFE0F\u20E3");         // 0️⃣

		// Traffic lights
		m.put("stop", "\uD83D\uDD34");            // 🔴 red circle
		m.put("prepare", "\uD83D\uDFE1");         // 🟡 yellow circle
		m.put("go", "\uD83D\uDFE2");              // 🟢 green circle

		// Navigation arrows
		m.put("back", "\u2B05\uFE0F");            // ⬅️
		m.put("forward", "\u27A1\uFE0F");          // ➡️
		m.put("up", "\u2B06\uFE0F");              // ⬆️
		m.put("down", "\u2B07\uFE0F");            // ⬇️

		// Objects
		m.put("attach", "\uD83D\uDCCE");          // 📎 paperclip
		m.put("ksmiletris", "\uD83D\uDE04");       // 😄 happy face
		m.put("smiley-neutral", "\uD83D\uDE10");   // 😐 neutral face
		m.put("smiley-oh", "\uD83D\uDE2E");        // 😮 surprised face
		m.put("smiley-angry", "\uD83D\uDE20");     // 😠 angry face
		m.put("smily_bad", "\uD83D\uDE1E");        // 😞 disappointed face
		m.put("clanbomber", "\uD83D\uDCA3");       // 💣 bomb
		m.put("desktop_new", "\uD83D\uDDA5\uFE0F"); // 🖥️ desktop computer
		m.put("gohome", "\uD83C\uDFE0");           // 🏠 house
		m.put("folder", "\uD83D\uDCC1");           // 📁 folder
		m.put("korn", "\uD83D\uDCE5");            // 📥 inbox tray (KDE mail notifier)
		m.put("Mail", "\uD83D\uDCE7");             // 📧 email
		m.put("kmail", "\u2709\uFE0F");            // ✉️ envelope
		m.put("list", "\uD83D\uDCCB");             // 📋 clipboard
		m.put("edit", "\u270F\uFE0F");             // ✏️ pencil
		m.put("kaddressbook", "\uD83D\uDCD6");     // 📖 open book
		m.put("knotify", "\uD83D\uDD14");          // 🔔 bell
		m.put("password", "\uD83D\uDD11");         // 🔑 key
		m.put("pencil", "\u270F\uFE0F");           // ✏️ pencil
		m.put("wizard", "\uD83E\uDDD9");           // 🧙 mage/wizard
		m.put("xmag", "\uD83D\uDD0D");            // 🔍 magnifying glass
		m.put("bell", "\uD83D\uDD14");             // 🔔 bell
		m.put("bookmark", "\uD83D\uDD16");         // 🔖 bookmark
		m.put("penguin", "\uD83D\uDC27");          // 🐧 penguin (Linux)
		m.put("licq", "\uD83D\uDCAC");             // 💬 speech bubble (messaging)
		m.put("freemind_butterfly", "\uD83E\uDD8B"); // 🦋 butterfly
		m.put("broken-line", "\u26A1");            // ⚡ lightning bolt

		// Time
		m.put("calendar", "\uD83D\uDCC5");         // 📅 calendar
		m.put("clock", "\u23F0");                  // ⏰ alarm clock
		m.put("clock2", "\uD83D\uDD50");           // 🕐 clock face one o'clock
		m.put("hourglass", "\u231B");              // ⌛ hourglass
		m.put("launch", "\uD83D\uDE80");           // 🚀 rocket

		// Flags
		m.put("flag-black", "\uD83C\uDFF4");       // 🏴 black flag
		m.put("flag-blue", "\uD83D\uDFE6");        // 🟦 blue square
		m.put("flag-green", "\uD83D\uDFE9");       // 🟩 green square
		m.put("flag-orange", "\uD83D\uDFE7");      // 🟧 orange square
		m.put("flag-pink", "\uD83C\uDFF3\uFE0F");  // 🏳️ white flag (pink not available)
		m.put("flag", "\uD83C\uDFF3\uFE0F");       // 🏳️ white flag
		m.put("flag-yellow", "\uD83D\uDFE8");      // 🟨 yellow square

		// People
		m.put("family", "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC66"); // 👨‍👩‍👧‍👦
		m.put("female1", "\uD83D\uDC69");          // 👩 woman
		m.put("female2", "\uD83D\uDC69");          // 👩 woman
		m.put("male1", "\uD83D\uDC68");            // 👨 man
		m.put("male2", "\uD83D\uDC68");            // 👨 man
		m.put("fema", "\uD83D\uDC64");             // 👤 person silhouette
		m.put("group", "\uD83D\uDC65");            // 👥 people silhouettes

		// Security (not in icons.list but PNG files exist)
		m.put("encrypted", "\uD83D\uDD12");        // 🔒 closed lock
		m.put("decrypted", "\uD83D\uDD13");        // 🔓 open lock

		// Other (not in icons.list but PNG files exist)
		m.put("redo", "\u21A9\uFE0F");             // ↩️ curved arrow
		m.put("star", "\u2B50");                   // ⭐ star

		ICON_TO_EMOJI = Collections.unmodifiableMap(m);
	}

	private MindIconEmoji() {
	}

	/**
	 * Returns the emoji string for the given FreeMind icon name,
	 * or a text fallback like [icon_name] if no mapping exists.
	 */
	public static String getEmoji(String iconName) {
		String emoji = ICON_TO_EMOJI.get(iconName);
		return emoji != null ? emoji : "[" + iconName + "]";
	}

	/**
	 * Returns true if there is an emoji mapping for the given icon name.
	 */
	public static boolean hasEmoji(String iconName) {
		return ICON_TO_EMOJI.containsKey(iconName);
	}
}
