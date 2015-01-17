 package com.drdanick.McRKit.module;
 
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.lang.reflect.Constructor;
 import java.net.URI;
 import java.net.URL;
 import java.net.URLClassLoader;
 import java.util.jar.JarFile;
 import java.util.zip.ZipEntry;
 
 public class ModuleLoader
 {
   public Module loadModule(File paramFile)
     throws InvalidModuleException
   {
     Module localModule = null;
     ModuleMetadata localModuleMetadata = null;
     if (!paramFile.exists()) {
       throw new InvalidModuleException(new FileNotFoundException("The file " + paramFile.getName() + " does not exist!"));
     }
     Object localObject1;
     Object localObject2;
     try
     {
       JarFile localJarFile = new JarFile(paramFile);
       localObject1 = localJarFile.getJarEntry("module.txt");
       if (localObject1 == null) {
         throw new InvalidModuleException(new FileNotFoundException("Jar does not contain module.txt"));
       }
       localObject2 = localJarFile.getInputStream((ZipEntry)localObject1);
       localModuleMetadata = new ModuleMetadata((InputStream)localObject2);
       
       ((InputStream)localObject2).close();
       localJarFile.close();
     }
     catch (IOException localIOException)
     {
       throw new InvalidModuleException(localIOException);
     }
     try
     {
       URLClassLoader localURLClassLoader = new URLClassLoader(new URL[] { paramFile.toURI().toURL() }, getClass().getClassLoader());
       localObject1 = Class.forName((String)localModuleMetadata.get("main"), true, localURLClassLoader);
       localObject2 = ((Class)localObject1).asSubclass(Module.class);
       Constructor localConstructor = ((Class)localObject2).getConstructor(new Class[] { ModuleMetadata.class, ModuleLoader.class, ClassLoader.class });
       localModule = (Module)localConstructor.newInstance(new Object[] { localModuleMetadata, this, localURLClassLoader });
     }
     catch (Throwable localThrowable)
     {
       throw new InvalidModuleException(localThrowable);
     }
     return localModule;
   }
 }


/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar
 * Qualified Name:     com.drdanick.McRKit.module...ModuleLoader
 * JD-Core Version:    0.7.0.1
 */