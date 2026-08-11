package ted_2001.WeightRPG.Utils;

import org.bukkit.ChatColor;

/**
 * Color-code utilities used by configurable Weight-RPG messages.
 */
public final class ColorUtils {

    private ColorUtils() {
    }

    /**
     * Translates legacy ampersand color codes and valid &#RRGGBB hex colors.
     * Malformed color input is preserved as text instead of throwing an exception.
     *
     * @param text text to translate
     * @return translated text, or an empty string for null input
     */
    public static String translateColorCodes(String text) {
        if (text == null || text.isEmpty()) {
            return text == null ? "" : text;
        }

        StringBuilder result = new StringBuilder(text.length());
        int segmentStart = 0;
        int index = 0;

        while (index < text.length()) {
            if (text.charAt(index) != '&') {
                index++;
                continue;
            }

            // Flush ordinary/legacy text in one pass. Bukkit safely ignores invalid legacy codes.
            if (index > segmentStart) {
                result.append(ChatColor.translateAlternateColorCodes('&', text.substring(segmentStart, index)));
            }

            if (isValidHexColor(text, index)) {
                String hex = text.substring(index + 1, index + 8);
                result.append(net.md_5.bungee.api.ChatColor.of(hex));
                index += 8;
                segmentStart = index;
                continue;
            }

            // Keep the '&' in the pending segment so normal legacy codes such as &a still work.
            segmentStart = index;
            index++;
        }

        if (segmentStart < text.length()) {
            result.append(ChatColor.translateAlternateColorCodes('&', text.substring(segmentStart)));
        }

        return result.toString();
    }

    private static boolean isValidHexColor(String text, int ampersandIndex) {
        if (ampersandIndex + 8 > text.length() || text.charAt(ampersandIndex + 1) != '#') {
            return false;
        }

        for (int i = ampersandIndex + 2; i < ampersandIndex + 8; i++) {
            if (Character.digit(text.charAt(i), 16) == -1) {
                return false;
            }
        }
        return true;
    }
}
