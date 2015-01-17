 package com.twmacinta.util;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.FilterInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.UnsupportedEncodingException;
 
 public class MD5
 {
   MD5State state;
   MD5State finals;
   static byte[] padding = { -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
   private static boolean native_lib_loaded = false;
   private static boolean native_lib_init_pending = true;
   
   public synchronized void Init()
   {
     this.state = new MD5State();
     this.finals = null;
   }
   
   public MD5()
   {
     if (native_lib_init_pending) {
       _initNativeLibrary();
     }
     Init();
   }
   
   public MD5(Object ob)
   {
     this();
     Update(ob.toString());
   }
   
   private void Decode(byte[] buffer, int shift, int[] out)
   {
     out[0] = (buffer[shift] & 0xFF | (buffer[(shift + 1)] & 0xFF) << 8 | (buffer[(shift + 2)] & 0xFF) << 16 | buffer[(shift + 3)] << 24);
     
 
 
     out[1] = (buffer[(shift + 4)] & 0xFF | (buffer[(shift + 5)] & 0xFF) << 8 | (buffer[(shift + 6)] & 0xFF) << 16 | buffer[(shift + 7)] << 24);
     
 
 
     out[2] = (buffer[(shift + 8)] & 0xFF | (buffer[(shift + 9)] & 0xFF) << 8 | (buffer[(shift + 10)] & 0xFF) << 16 | buffer[(shift + 11)] << 24);
     
 
 
     out[3] = (buffer[(shift + 12)] & 0xFF | (buffer[(shift + 13)] & 0xFF) << 8 | (buffer[(shift + 14)] & 0xFF) << 16 | buffer[(shift + 15)] << 24);
     
 
 
     out[4] = (buffer[(shift + 16)] & 0xFF | (buffer[(shift + 17)] & 0xFF) << 8 | (buffer[(shift + 18)] & 0xFF) << 16 | buffer[(shift + 19)] << 24);
     
 
 
     out[5] = (buffer[(shift + 20)] & 0xFF | (buffer[(shift + 21)] & 0xFF) << 8 | (buffer[(shift + 22)] & 0xFF) << 16 | buffer[(shift + 23)] << 24);
     
 
 
     out[6] = (buffer[(shift + 24)] & 0xFF | (buffer[(shift + 25)] & 0xFF) << 8 | (buffer[(shift + 26)] & 0xFF) << 16 | buffer[(shift + 27)] << 24);
     
 
 
     out[7] = (buffer[(shift + 28)] & 0xFF | (buffer[(shift + 29)] & 0xFF) << 8 | (buffer[(shift + 30)] & 0xFF) << 16 | buffer[(shift + 31)] << 24);
     
 
 
     out[8] = (buffer[(shift + 32)] & 0xFF | (buffer[(shift + 33)] & 0xFF) << 8 | (buffer[(shift + 34)] & 0xFF) << 16 | buffer[(shift + 35)] << 24);
     
 
 
     out[9] = (buffer[(shift + 36)] & 0xFF | (buffer[(shift + 37)] & 0xFF) << 8 | (buffer[(shift + 38)] & 0xFF) << 16 | buffer[(shift + 39)] << 24);
     
 
 
     out[10] = (buffer[(shift + 40)] & 0xFF | (buffer[(shift + 41)] & 0xFF) << 8 | (buffer[(shift + 42)] & 0xFF) << 16 | buffer[(shift + 43)] << 24);
     
 
 
     out[11] = (buffer[(shift + 44)] & 0xFF | (buffer[(shift + 45)] & 0xFF) << 8 | (buffer[(shift + 46)] & 0xFF) << 16 | buffer[(shift + 47)] << 24);
     
 
 
     out[12] = (buffer[(shift + 48)] & 0xFF | (buffer[(shift + 49)] & 0xFF) << 8 | (buffer[(shift + 50)] & 0xFF) << 16 | buffer[(shift + 51)] << 24);
     
 
 
     out[13] = (buffer[(shift + 52)] & 0xFF | (buffer[(shift + 53)] & 0xFF) << 8 | (buffer[(shift + 54)] & 0xFF) << 16 | buffer[(shift + 55)] << 24);
     
 
 
     out[14] = (buffer[(shift + 56)] & 0xFF | (buffer[(shift + 57)] & 0xFF) << 8 | (buffer[(shift + 58)] & 0xFF) << 16 | buffer[(shift + 59)] << 24);
     
 
 
     out[15] = (buffer[(shift + 60)] & 0xFF | (buffer[(shift + 61)] & 0xFF) << 8 | (buffer[(shift + 62)] & 0xFF) << 16 | buffer[(shift + 63)] << 24);
   }
   
   private native void Transform_native(int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
   
   private void Transform(MD5State state, byte[] buffer, int shift, int[] decode_buf)
   {
     int a = state.state[0];
     int b = state.state[1];
     int c = state.state[2];
     int d = state.state[3];
     int[] x = decode_buf;
     
     Decode(buffer, shift, decode_buf);
     
 
     a += (b & c | (b ^ 0xFFFFFFFF) & d) + x[0] + -680876936;
     a = (a << 7 | a >>> 25) + b;
     d += (a & b | (a ^ 0xFFFFFFFF) & c) + x[1] + -389564586;
     d = (d << 12 | d >>> 20) + a;
     c += (d & a | (d ^ 0xFFFFFFFF) & b) + x[2] + 606105819;
     c = (c << 17 | c >>> 15) + d;
     b += (c & d | (c ^ 0xFFFFFFFF) & a) + x[3] + -1044525330;
     b = (b << 22 | b >>> 10) + c;
     
     a += (b & c | (b ^ 0xFFFFFFFF) & d) + x[4] + -176418897;
     a = (a << 7 | a >>> 25) + b;
     d += (a & b | (a ^ 0xFFFFFFFF) & c) + x[5] + 1200080426;
     d = (d << 12 | d >>> 20) + a;
     c += (d & a | (d ^ 0xFFFFFFFF) & b) + x[6] + -1473231341;
     c = (c << 17 | c >>> 15) + d;
     b += (c & d | (c ^ 0xFFFFFFFF) & a) + x[7] + -45705983;
     b = (b << 22 | b >>> 10) + c;
     
     a += (b & c | (b ^ 0xFFFFFFFF) & d) + x[8] + 1770035416;
     a = (a << 7 | a >>> 25) + b;
     d += (a & b | (a ^ 0xFFFFFFFF) & c) + x[9] + -1958414417;
     d = (d << 12 | d >>> 20) + a;
     c += (d & a | (d ^ 0xFFFFFFFF) & b) + x[10] + -42063;
     c = (c << 17 | c >>> 15) + d;
     b += (c & d | (c ^ 0xFFFFFFFF) & a) + x[11] + -1990404162;
     b = (b << 22 | b >>> 10) + c;
     
     a += (b & c | (b ^ 0xFFFFFFFF) & d) + x[12] + 1804603682;
     a = (a << 7 | a >>> 25) + b;
     d += (a & b | (a ^ 0xFFFFFFFF) & c) + x[13] + -40341101;
     d = (d << 12 | d >>> 20) + a;
     c += (d & a | (d ^ 0xFFFFFFFF) & b) + x[14] + -1502002290;
     c = (c << 17 | c >>> 15) + d;
     b += (c & d | (c ^ 0xFFFFFFFF) & a) + x[15] + 1236535329;
     b = (b << 22 | b >>> 10) + c;
     
 
 
     a += (b & d | c & (d ^ 0xFFFFFFFF)) + x[1] + -165796510;
     a = (a << 5 | a >>> 27) + b;
     d += (a & c | b & (c ^ 0xFFFFFFFF)) + x[6] + -1069501632;
     d = (d << 9 | d >>> 23) + a;
     c += (d & b | a & (b ^ 0xFFFFFFFF)) + x[11] + 643717713;
     c = (c << 14 | c >>> 18) + d;
     b += (c & a | d & (a ^ 0xFFFFFFFF)) + x[0] + -373897302;
     b = (b << 20 | b >>> 12) + c;
     
     a += (b & d | c & (d ^ 0xFFFFFFFF)) + x[5] + -701558691;
     a = (a << 5 | a >>> 27) + b;
     d += (a & c | b & (c ^ 0xFFFFFFFF)) + x[10] + 38016083;
     d = (d << 9 | d >>> 23) + a;
     c += (d & b | a & (b ^ 0xFFFFFFFF)) + x[15] + -660478335;
     c = (c << 14 | c >>> 18) + d;
     b += (c & a | d & (a ^ 0xFFFFFFFF)) + x[4] + -405537848;
     b = (b << 20 | b >>> 12) + c;
     
     a += (b & d | c & (d ^ 0xFFFFFFFF)) + x[9] + 568446438;
     a = (a << 5 | a >>> 27) + b;
     d += (a & c | b & (c ^ 0xFFFFFFFF)) + x[14] + -1019803690;
     d = (d << 9 | d >>> 23) + a;
     c += (d & b | a & (b ^ 0xFFFFFFFF)) + x[3] + -187363961;
     c = (c << 14 | c >>> 18) + d;
     b += (c & a | d & (a ^ 0xFFFFFFFF)) + x[8] + 1163531501;
     b = (b << 20 | b >>> 12) + c;
     
     a += (b & d | c & (d ^ 0xFFFFFFFF)) + x[13] + -1444681467;
     a = (a << 5 | a >>> 27) + b;
     d += (a & c | b & (c ^ 0xFFFFFFFF)) + x[2] + -51403784;
     d = (d << 9 | d >>> 23) + a;
     c += (d & b | a & (b ^ 0xFFFFFFFF)) + x[7] + 1735328473;
     c = (c << 14 | c >>> 18) + d;
     b += (c & a | d & (a ^ 0xFFFFFFFF)) + x[12] + -1926607734;
     b = (b << 20 | b >>> 12) + c;
     
 
 
     a += (b ^ c ^ d) + x[5] + -378558;
     a = (a << 4 | a >>> 28) + b;
     d += (a ^ b ^ c) + x[8] + -2022574463;
     d = (d << 11 | d >>> 21) + a;
     c += (d ^ a ^ b) + x[11] + 1839030562;
     c = (c << 16 | c >>> 16) + d;
     b += (c ^ d ^ a) + x[14] + -35309556;
     b = (b << 23 | b >>> 9) + c;
     
     a += (b ^ c ^ d) + x[1] + -1530992060;
     a = (a << 4 | a >>> 28) + b;
     d += (a ^ b ^ c) + x[4] + 1272893353;
     d = (d << 11 | d >>> 21) + a;
     c += (d ^ a ^ b) + x[7] + -155497632;
     c = (c << 16 | c >>> 16) + d;
     b += (c ^ d ^ a) + x[10] + -1094730640;
     b = (b << 23 | b >>> 9) + c;
     
     a += (b ^ c ^ d) + x[13] + 681279174;
     a = (a << 4 | a >>> 28) + b;
     d += (a ^ b ^ c) + x[0] + -358537222;
     d = (d << 11 | d >>> 21) + a;
     c += (d ^ a ^ b) + x[3] + -722521979;
     c = (c << 16 | c >>> 16) + d;
     b += (c ^ d ^ a) + x[6] + 76029189;
     b = (b << 23 | b >>> 9) + c;
     
     a += (b ^ c ^ d) + x[9] + -640364487;
     a = (a << 4 | a >>> 28) + b;
     d += (a ^ b ^ c) + x[12] + -421815835;
     d = (d << 11 | d >>> 21) + a;
     c += (d ^ a ^ b) + x[15] + 530742520;
     c = (c << 16 | c >>> 16) + d;
     b += (c ^ d ^ a) + x[2] + -995338651;
     b = (b << 23 | b >>> 9) + c;
     
 
 
     a += (c ^ (b | d ^ 0xFFFFFFFF)) + x[0] + -198630844;
     a = (a << 6 | a >>> 26) + b;
     d += (b ^ (a | c ^ 0xFFFFFFFF)) + x[7] + 1126891415;
     d = (d << 10 | d >>> 22) + a;
     c += (a ^ (d | b ^ 0xFFFFFFFF)) + x[14] + -1416354905;
     c = (c << 15 | c >>> 17) + d;
     b += (d ^ (c | a ^ 0xFFFFFFFF)) + x[5] + -57434055;
     b = (b << 21 | b >>> 11) + c;
     
     a += (c ^ (b | d ^ 0xFFFFFFFF)) + x[12] + 1700485571;
     a = (a << 6 | a >>> 26) + b;
     d += (b ^ (a | c ^ 0xFFFFFFFF)) + x[3] + -1894986606;
     d = (d << 10 | d >>> 22) + a;
     c += (a ^ (d | b ^ 0xFFFFFFFF)) + x[10] + -1051523;
     c = (c << 15 | c >>> 17) + d;
     b += (d ^ (c | a ^ 0xFFFFFFFF)) + x[1] + -2054922799;
     b = (b << 21 | b >>> 11) + c;
     
     a += (c ^ (b | d ^ 0xFFFFFFFF)) + x[8] + 1873313359;
     a = (a << 6 | a >>> 26) + b;
     d += (b ^ (a | c ^ 0xFFFFFFFF)) + x[15] + -30611744;
     d = (d << 10 | d >>> 22) + a;
     c += (a ^ (d | b ^ 0xFFFFFFFF)) + x[6] + -1560198380;
     c = (c << 15 | c >>> 17) + d;
     b += (d ^ (c | a ^ 0xFFFFFFFF)) + x[13] + 1309151649;
     b = (b << 21 | b >>> 11) + c;
     
     a += (c ^ (b | d ^ 0xFFFFFFFF)) + x[4] + -145523070;
     a = (a << 6 | a >>> 26) + b;
     d += (b ^ (a | c ^ 0xFFFFFFFF)) + x[11] + -1120210379;
     d = (d << 10 | d >>> 22) + a;
     c += (a ^ (d | b ^ 0xFFFFFFFF)) + x[2] + 718787259;
     c = (c << 15 | c >>> 17) + d;
     b += (d ^ (c | a ^ 0xFFFFFFFF)) + x[9] + -343485551;
     b = (b << 21 | b >>> 11) + c;
     
     state.state[0] += a;
     state.state[1] += b;
     state.state[2] += c;
     state.state[3] += d;
   }
   
   public void Update(MD5State stat, byte[] buffer, int offset, int length)
   {
     this.finals = null;
     if (length - offset > buffer.length) {
       length = buffer.length - offset;
     }
     int index = (int)(stat.count & 0x3F);
     stat.count += length;
     
     int partlen = 64 - index;
     int i;
     if (length >= partlen)
     {
       if (native_lib_loaded)
       {
         if (partlen == 64)
         {
           partlen = 0;
         }
         else
         {
           for (i = 0; i < partlen; i++) {
             stat.buffer[(i + index)] = buffer[(i + offset)];
           }
           Transform_native(stat.state, stat.buffer, 0, 64);
         }
         i = partlen + (length - partlen) / 64 * 64;
         
 
 
         int transformLength = length - partlen;
         int transformOffset = partlen + offset;
         int MAX_LENGTH = 65536;
         while (transformLength > 65536)
         {
           Transform_native(stat.state, buffer, transformOffset, 65536);
           transformLength -= 65536;
           transformOffset += 65536;
         }
         Transform_native(stat.state, buffer, transformOffset, transformLength);
       }
       else
       {
         int[] decode_buf = new int[16];
         if (partlen == 64)
         {
           partlen = 0;
         }
         else
         {
           for (i = 0; i < partlen; i++) {
             stat.buffer[(i + index)] = buffer[(i + offset)];
           }
           Transform(stat, stat.buffer, 0, decode_buf);
         }
         for (i = partlen; i + 63 < length; i += 64) {
           Transform(stat, buffer, i + offset, decode_buf);
         }
       }
       index = 0;
     }
     else
     {
       i = 0;
     }
     if (i < length)
     {
       int start = i;
       for (; i < length; i++) {
         stat.buffer[(index + i - start)] = buffer[(i + offset)];
       }
     }
   }
   
   public void Update(byte[] buffer, int offset, int length)
   {
     Update(this.state, buffer, offset, length);
   }
   
   public void Update(byte[] buffer, int length)
   {
     Update(this.state, buffer, 0, length);
   }
   
   public void Update(byte[] buffer)
   {
     Update(buffer, 0, buffer.length);
   }
   
   public void Update(byte b)
   {
     byte[] buffer = new byte[1];
     buffer[0] = b;
     
     Update(buffer, 1);
   }
   
   public void Update(String s)
   {
     byte[] chars = s.getBytes();
     Update(chars, chars.length);
   }
   
   public void Update(String s, String charset_name)
     throws UnsupportedEncodingException
   {
     if (charset_name == null) {
       charset_name = "ISO8859_1";
     }
     byte[] chars = s.getBytes(charset_name);
     Update(chars, chars.length);
   }
   
   public void Update(int i)
   {
     Update((byte)(i & 0xFF));
   }
   
   private byte[] Encode(int[] input, int len)
   {
     byte[] out = new byte[len];
     int j;
     for (int i = j = 0; j < len; j += 4)
     {
       out[j] = ((byte)(input[i] & 0xFF));
       out[(j + 1)] = ((byte)(input[i] >>> 8 & 0xFF));
       out[(j + 2)] = ((byte)(input[i] >>> 16 & 0xFF));
       out[(j + 3)] = ((byte)(input[i] >>> 24 & 0xFF));i++;
     }
     return out;
   }
   
   public synchronized byte[] Final()
   {
     if (this.finals == null)
     {
       MD5State fin = new MD5State(this.state);
       
       int[] count_ints = { (int)(fin.count << 3), (int)(fin.count >> 29) };
       byte[] bits = Encode(count_ints, 8);
       
       int index = (int)(fin.count & 0x3F);
       int padlen = index < 56 ? 56 - index : 120 - index;
       
       Update(fin, padding, 0, padlen);
       Update(fin, bits, 0, 8);
       
 
       this.finals = fin;
     }
     return Encode(this.finals.state, 16);
   }
   
   private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
   
   public static String asHex(byte[] hash)
   {
     char[] buf = new char[hash.length * 2];
     int i = 0;
     for (int x = 0; i < hash.length; i++)
     {
       buf[(x++)] = HEX_CHARS[(hash[i] >>> 4 & 0xF)];
       buf[(x++)] = HEX_CHARS[(hash[i] & 0xF)];
     }
     return new String(buf);
   }
   
   public String asHex()
   {
     return asHex(Final());
   }
   
   public static final synchronized boolean initNativeLibrary()
   {
     return initNativeLibrary(false);
   }
   
   public static final synchronized boolean initNativeLibrary(boolean disallow_lib_loading)
   {
     if (disallow_lib_loading) {
       native_lib_init_pending = false;
     } else {
       _initNativeLibrary();
     }
     return native_lib_loaded;
   }
   
   private static final synchronized void _initNativeLibrary()
   {
     if (!native_lib_init_pending) {
       return;
     }
     native_lib_loaded = _loadNativeLibrary();
     native_lib_init_pending = false;
   }
   
   private static final synchronized boolean _loadNativeLibrary()
   {
     try
     {
       String prop = System.getProperty("com.twmacinta.util.MD5.NO_NATIVE_LIB");
       if (prop != null)
       {
         prop = prop.trim();
         if ((prop.equalsIgnoreCase("true")) || (prop.equals("1"))) {
           return false;
         }
       }
       prop = System.getProperty("com.twmacinta.util.MD5.NATIVE_LIB_FILE");
       if (prop != null)
       {
         File f = new File(prop);
         if (f.canRead())
         {
           System.load(f.getAbsolutePath());
           return true;
         }
       }
       String os_name = System.getProperty("os.name");
       String os_arch = System.getProperty("os.arch");
       if ((os_name == null) || (os_arch == null)) {
         return false;
       }
       os_name = os_name.toLowerCase();
       os_arch = os_arch.toLowerCase();
       
 
 
       File arch_lib_path = null;
       String arch_libfile_suffix = null;
       if ((os_name.equals("linux")) && ((os_arch.equals("x86")) || (os_arch.equals("i386")) || (os_arch.equals("i486")) || (os_arch.equals("i586")) || (os_arch.equals("i686"))))
       {
         arch_lib_path = new File(new File(new File("lib"), "arch"), "linux_x86");
         arch_libfile_suffix = ".so";
       }
       else if ((os_name.equals("linux")) && (os_arch.equals("amd64")))
       {
         arch_lib_path = new File(new File(new File("lib"), "arch"), "linux_amd64");
         arch_libfile_suffix = ".so";
       }
       else if ((os_name.startsWith("windows ")) && ((os_arch.equals("x86")) || (os_arch.equals("i386")) || (os_arch.equals("i486")) || (os_arch.equals("i586")) || (os_arch.equals("i686"))))
       {
         arch_lib_path = new File(new File(new File("lib"), "arch"), "win32_x86");
         arch_libfile_suffix = ".dll";
       }
       else if ((os_name.startsWith("windows ")) && (os_arch.equals("amd64")))
       {
         arch_lib_path = new File(new File(new File("lib"), "arch"), "win_amd64");
         arch_libfile_suffix = ".dll";
       }
       else if ((os_name.startsWith("mac os x")) && (os_arch.equals("ppc")))
       {
         arch_lib_path = new File(new File(new File("lib"), "arch"), "darwin_ppc");
         arch_libfile_suffix = ".jnilib";
       }
       else if ((os_name.startsWith("mac os x")) && ((os_arch.equals("x86")) || (os_arch.equals("i386")) || (os_arch.equals("i486")) || (os_arch.equals("i586")) || (os_arch.equals("i686"))))
       {
         arch_lib_path = new File(new File(new File("lib"), "arch"), "darwin_x86");
         arch_libfile_suffix = ".jnilib";
       }
       else if ((os_name.startsWith("mac os x")) && (os_arch.equals("x86_64")))
       {
         arch_lib_path = new File(new File(new File("lib"), "arch"), "darwin_x86_64");
         arch_libfile_suffix = ".jnilib";
       }
       else if ((os_name.equals("freebsd")) && ((os_arch.equals("x86")) || (os_arch.equals("i386")) || (os_arch.equals("i486")) || (os_arch.equals("i586")) || (os_arch.equals("i686"))))
       {
         arch_lib_path = new File(new File(new File("lib"), "arch"), "freebsd_x86");
         arch_libfile_suffix = ".so";
       }
       else if ((os_name.equals("freebsd")) && (os_arch.equals("amd64")))
       {
         arch_lib_path = new File(new File(new File("lib"), "arch"), "freebsd_amd64");
         arch_libfile_suffix = ".so";
       }
       else
       {
         arch_libfile_suffix = ".so";
       }
       String fname = "MD5" + arch_libfile_suffix;
       if (arch_lib_path != null)
       {
         File f = new File(arch_lib_path, fname);
         if (f.canRead())
         {
           System.load(f.getAbsolutePath());
           return true;
         }
       }
       File f = new File(new File("lib"), fname);
       if (f.canRead())
       {
         System.load(f.getAbsolutePath());
         return true;
       }
       f = new File(fname);
       if (f.canRead())
       {
         System.load(f.getAbsolutePath());
         return true;
       }
     }
     catch (SecurityException e) {}catch (UnsatisfiedLinkError e)
     {
       e.printStackTrace();
     }
     return false;
   }
   
   public static byte[] getHash(File f)
     throws IOException
   {
     if (!f.exists()) {
       throw new FileNotFoundException(f.toString());
     }
     InputStream close_me = null;
     try
     {
       long buf_size = f.length();
       if (buf_size < 512L) {
         buf_size = 512L;
       }
       if (buf_size > 65536L) {
         buf_size = 65536L;
       }
       byte[] buf = new byte[(int)buf_size];
       MD5InputStream in = new MD5InputStream(new FileInputStream(f));
       close_me = in;
       while (in.read(buf) != -1) {}
       in.close();
       return in.hash();
     }
     catch (IOException e)
     {
       if (close_me != null) {
         try
         {
           close_me.close();
         }
         catch (Exception e2) {}
       }
       throw e;
     }
   }
   
   public static boolean hashesEqual(byte[] hash1, byte[] hash2)
   {
     if (hash1 == null) {
       return hash2 == null;
     }
     if (hash2 == null) {
       return false;
     }
     int targ = 16;
     if (hash1.length < 16)
     {
       if (hash2.length != hash1.length) {
         return false;
       }
       targ = hash1.length;
     }
     else if (hash2.length < 16)
     {
       return false;
     }
     for (int i = 0; i < targ; i++) {
       if (hash1[i] != hash2[i]) {
         return false;
       }
     }
     return true;
   }
 }



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     com.twmacinta.util...MD5

 * JD-Core Version:    0.7.0.1

 */