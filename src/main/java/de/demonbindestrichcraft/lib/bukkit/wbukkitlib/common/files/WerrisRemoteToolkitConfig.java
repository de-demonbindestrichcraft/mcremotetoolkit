/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.files;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author ABC
 */
public final class WerrisRemoteToolkitConfig {

    private Map<String, String> properties;
    private boolean isRTKInstalled;

    public WerrisRemoteToolkitConfig() {
        properties = new HashMap<String, String>();
        update();
    }

    public synchronized void update() {
        properties.clear();
        File remoteProperties = new File("toolkit/remote.properties");
        if (!remoteProperties.exists()) {
            isRTKInstalled = false;
        } else {
            isRTKInstalled = true;
        }
        String message_playback_count = GenerallyFileManager.FileReadLine("message-playback-count", remoteProperties, "=");
        String remote_bind_address = GenerallyFileManager.FileReadLine("remote-bind-address", remoteProperties, "=");
        String auth_salt = GenerallyFileManager.FileReadLine("auth-salt", remoteProperties, "=");
        String shell_password_mask = GenerallyFileManager.FileReadLine("shell-password-mask", remoteProperties, "=");
        String telnet_enabled = GenerallyFileManager.FileReadLine("telnet-enabled", remoteProperties, "=");
        String remote_control_port = GenerallyFileManager.FileReadLine("remote-control-port", remoteProperties, "=");
        String shell_input_echo = GenerallyFileManager.FileReadLine("shell-input-echo", remoteProperties, "=");
        properties.put("message-playback-count", message_playback_count);
        properties.put("remote-bind-address", remote_bind_address);
        properties.put("auth-salt", auth_salt);
        properties.put("shell-password-mask", shell_password_mask);
        properties.put("telnet-enabled", telnet_enabled);
        properties.put("remote-control-port", remote_control_port);
        properties.put("shell-input-echo", shell_input_echo);
    }

    public synchronized Map<String, String> getProperties() {
        return new HashMap<String, String>(properties);
    }

    public String getString(String key) throws NullPointerException, ClassCastException {
        return properties.get(key);
    }

    public int getInt(String key) throws NumberFormatException, NullPointerException, ClassCastException {
        return Integer.parseInt(properties.get(key));
    }

    public float getFloat(String key) throws NumberFormatException, NullPointerException, ClassCastException {
        return Float.parseFloat(properties.get(key));
    }

    public double getDouble(String key) throws NumberFormatException, NullPointerException, ClassCastException {
        return Double.parseDouble(properties.get(key));
    }

    public long getLong(String key) throws NumberFormatException, NullPointerException, ClassCastException {
        return Long.parseLong(properties.get(key));
    }

    public short getShort(String key) throws NumberFormatException, NullPointerException, ClassCastException {
        return Short.parseShort(properties.get(key));
    }

    public boolean getBoolean(String key) throws NumberFormatException, NullPointerException, ClassCastException {
        return Boolean.parseBoolean(properties.get(key));
    }

    public boolean isRTKInstalled() {
        return isRTKInstalled;
    }
}
