package net2.wimpi.telnetd.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public final class PropertiesLoader
{
  public static Properties loadProperties(String paramString)
    throws MalformedURLException, IOException
  {
    return loadProperties(new URL(paramString));
  }
  
  public static Properties loadProperties(URL paramURL)
    throws IOException
  {
    Properties localProperties = new Properties();
    InputStream localInputStream = paramURL.openStream();
    localProperties.load(localInputStream);
    localInputStream.close();
    return localProperties;
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.util.PropertiesLoader

 * JD-Core Version:    0.7.0.1

 */