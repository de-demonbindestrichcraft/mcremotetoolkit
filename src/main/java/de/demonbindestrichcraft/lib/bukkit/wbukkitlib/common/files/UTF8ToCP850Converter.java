/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.files;

import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ABC
 */
public class UTF8ToCP850Converter {

    private static Map<Character, Character> utf8_cp850 = new HashMap<Character, Character>();
    private static Map<Character, Character> cp850_utf8 = new HashMap<Character, Character>();
    private static boolean isInit = false;

    private static void init() {
        //ä character
        utf8_cp850.put((char) 228, (char) 132);
        cp850_utf8.put((char) 132, (char) 228);

        //ü character
        utf8_cp850.put((char) 252, (char) 129);
        cp850_utf8.put((char) 129, (char) 252);

        //ö character
        utf8_cp850.put((char) 246, (char) 148);
        cp850_utf8.put((char) 148, (char) 246);

        //Ä character
        utf8_cp850.put((char) 196, (char) 142);
        cp850_utf8.put((char) 142, (char) 196);

        //Ü character
        utf8_cp850.put((char) 220, (char) 154);
        cp850_utf8.put((char) 154, (char) 220);

        //Ö character
        utf8_cp850.put((char) 214, (char) 153);
        cp850_utf8.put((char) 153, (char) 214);
        isInit = true;
    }

    public static String convertFromUTF8(String s) {
        if (!isInit) {
            init();
        }
        int length = s.length();
        StringBuilder n = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char e = s.charAt(i);
            if (utf8_cp850.containsKey(e)) {
                e = utf8_cp850.get(e);
            }
            n = n.append(e);
        }

        return n.toString();
    }

    public static String convertFromCP850(String s) {
        if (!isInit) {
            init();
        }
        int length = s.length();
        StringBuilder n = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char e = s.charAt(i);
            if (cp850_utf8.containsKey(e)) {
                e = cp850_utf8.get(e);
            }
            n = n.append(e);
        }

        return n.toString();
    }

    public static int getUnsignedByte(byte b) {
        return ((b & 0xFF) | 0x80);
    }

    public static byte getSignedByte(int b) {
        return (byte) b;
    }

    public static PrintStream getOuter(boolean useDefault) {
        boolean supported;
        try {
            if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
                supported = Charset.isSupported("Cp850");
                if (supported) {
                    return new PrintStream(System.out, true, "Cp850");
                }
                supported = Charset.isSupported("UTF-8");
                if (supported) {
                    return new PrintStream(System.out, true, "UTF-8");
                }
                supported = Charset.isSupported("ISO-8859-1");
                if (supported) {
                    return new PrintStream(System.out, true, "ISO-8859-1");
                }
                if (!useDefault) {
                    return new PrintStream(System.out, true, Charset.defaultCharset().name());
                } else {
                    return System.out;
                }
            } else {
                supported = Charset.isSupported("UTF-8");
                if (supported) {
                    return new PrintStream(System.out, true, "UTF-8");
                }
                supported = Charset.isSupported("ISO-8859-1");
                if (supported) {
                    return new PrintStream(System.out, true, "ISO-8859-1");
                }
                supported = Charset.isSupported("Cp850");
                if (supported) {
                    return new PrintStream(System.out, true, "Cp850");
                }
                if (!useDefault) {
                    return new PrintStream(System.out, true, Charset.defaultCharset().name());
                } else {
                    return System.out;
                }
            }
        } catch (Throwable ex) {
            return null;
        }
    }

    public static InputStreamReader getInner(boolean useDefault) {
        boolean supported;
        try {
            if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
                supported = Charset.isSupported("Cp850");
                if (supported) {
                    return new InputStreamReader(System.in, "Cp850");
                }
                supported = Charset.isSupported("UTF-8");
                if (supported) {
                    return new InputStreamReader(System.in, "UTF-8");
                }
                supported = Charset.isSupported("ISO-8859-1");
                if (supported) {
                    return new InputStreamReader(System.in, "ISO-8859-1");
                }
                if (!useDefault) {
                    return new InputStreamReader(System.in, Charset.defaultCharset().name());
                } else {
                    return new InputStreamReader(System.in);
                }
            } else {
                supported = Charset.isSupported("UTF-8");
                if (supported) {
                    return new InputStreamReader(System.in, "UTF-8");
                }
                supported = Charset.isSupported("ISO-8859-1");
                if (supported) {
                    return new InputStreamReader(System.in, "ISO-8859-1");
                }
                supported = Charset.isSupported("Cp850");
                if (supported) {
                    return new InputStreamReader(System.in, "Cp850");
                }
                if (!useDefault) {
                    return new InputStreamReader(System.in, Charset.defaultCharset().name());
                } else {
                    return new InputStreamReader(System.in);
                }
            }
        } catch (Throwable ex) {
            return null;
        }
    }
}
