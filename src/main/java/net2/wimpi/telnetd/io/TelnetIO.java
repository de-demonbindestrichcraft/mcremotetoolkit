package net2.wimpi.telnetd.io;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import net2.wimpi.telnetd.net.Connection;
import net2.wimpi.telnetd.net.ConnectionData;
import net2.wimpi.telnetd.net.ConnectionEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TelnetIO
{
  private static Log log = LogFactory.getLog(TelnetIO.class);
  private Connection m_Connection;
  private ConnectionData m_ConnectionData;
  private DataOutputStream m_Out;
  private DataInputStream m_In;
  private IACHandler m_IACHandler;
  private InetAddress m_LocalAddress;
  private boolean m_NOIAC = false;
  private boolean m_Initializing;
  private boolean m_CRFlag;
  protected static final int IAC = 255;
  protected static final int GA = 249;
  protected static final int WILL = 251;
  protected static final int WONT = 252;
  protected static final int DO = 253;
  protected static final int DONT = 254;
  protected static final int SB = 250;
  protected static final int SE = 240;
  protected static final int NOP = 241;
  protected static final int DM = 242;
  protected static final int BRK = 243;
  protected static final int IP = 244;
  protected static final int AO = 245;
  protected static final int AYT = 246;
  protected static final int EC = 247;
  protected static final int EL = 248;
  protected static final int ECHO = 1;
  protected static final int SUPGA = 3;
  protected static final int NAWS = 31;
  protected static final int TTYPE = 24;
  protected static final int IS = 0;
  protected static final int SEND = 1;
  protected static final int LOGOUT = 18;
  protected static final int LINEMODE = 34;
  protected static final int LM_MODE = 1;
  protected static final int LM_EDIT = 1;
  protected static final int LM_TRAPSIG = 2;
  protected static final int LM_MODEACK = 4;
  protected static final int LM_FORWARDMASK = 2;
  protected static final int LM_SLC = 3;
  protected static final int LM_SLC_NOSUPPORT = 0;
  protected static final int LM_SLC_DEFAULT = 3;
  protected static final int LM_SLC_VALUE = 2;
  protected static final int LM_SLC_CANTCHANGE = 1;
  protected static final int LM_SLC_LEVELBITS = 3;
  protected static final int LM_SLC_ACK = 128;
  protected static final int LM_SLC_FLUSHIN = 64;
  protected static final int LM_SLC_FLUSHOUT = 32;
  protected static final int LM_SLC_SYNCH = 1;
  protected static final int LM_SLC_BRK = 2;
  protected static final int LM_SLC_IP = 3;
  protected static final int LM_SLC_AO = 4;
  protected static final int LM_SLC_AYT = 5;
  protected static final int LM_SLC_EOR = 6;
  protected static final int LM_SLC_ABORT = 7;
  protected static final int LM_SLC_EOF = 8;
  protected static final int LM_SLC_SUSP = 9;
  protected static final int NEWENV = 39;
  protected static final int NE_INFO = 2;
  protected static final int NE_VAR = 0;
  protected static final int NE_VALUE = 1;
  protected static final int NE_ESC = 2;
  protected static final int NE_USERVAR = 3;
  protected static final int NE_VAR_OK = 2;
  protected static final int NE_VAR_DEFINED = 1;
  protected static final int NE_VAR_DEFINED_EMPTY = 0;
  protected static final int NE_VAR_UNDEFINED = -1;
  protected static final int NE_IN_ERROR = -2;
  protected static final int NE_IN_END = -3;
  protected static final int NE_VAR_NAME_MAXLENGTH = 50;
  protected static final int NE_VAR_VALUE_MAXLENGTH = 1000;
  protected static final int EXT_ASCII = 17;
  protected static final int SEND_LOC = 23;
  protected static final int AUTHENTICATION = 37;
  protected static final int ENCRYPT = 38;
  private static final int SMALLEST_BELIEVABLE_WIDTH = 20;
  private static final int SMALLEST_BELIEVABLE_HEIGHT = 6;
  private static final int DEFAULT_WIDTH = 80;
  private static final int DEFAULT_HEIGHT = 25;
  
  public void initIO()
    throws IOException
  {
    this.m_IACHandler = new IACHandler();
    this.m_In = new DataInputStream(this.m_ConnectionData.getSocket().getInputStream());
    this.m_Out = new DataOutputStream(new BufferedOutputStream(this.m_ConnectionData.getSocket().getOutputStream()));
    this.m_LocalAddress = this.m_ConnectionData.getSocket().getLocalAddress();
    this.m_CRFlag = false;
    initTelnetCommunication();
  }
  
  public void setConnection(Connection paramConnection)
  {
    this.m_Connection = paramConnection;
    this.m_ConnectionData = this.m_Connection.getConnectionData();
  }
  
  public void write(byte paramByte)
    throws IOException
  {
    if ((!this.m_CRFlag) && (paramByte == 10)) {
      this.m_Out.write(13);
    }
    if ((this.m_CRFlag) && (paramByte != 10)) {
      this.m_Out.write(10);
    }
    this.m_Out.write(paramByte);
    if (paramByte == 13) {
      this.m_CRFlag = true;
    } else {
      this.m_CRFlag = false;
    }
  }
  
  public void write(int paramInt)
    throws IOException
  {
    write((byte)paramInt);
  }
  
  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    for (int i = 0; i < paramArrayOfByte.length; i++) {
      write(paramArrayOfByte[i]);
    }
  }
  
  public void write(int[] paramArrayOfInt)
    throws IOException
  {
    for (int i = 0; i < paramArrayOfInt.length; i++) {
      write((byte)paramArrayOfInt[i]);
    }
  }
  
  public void write(char paramChar)
    throws IOException
  {
    write((byte)paramChar);
  }
  
  public void write(String paramString)
    throws IOException
  {
    write(paramString.getBytes());
  }
  
  public void flush()
    throws IOException
  {
    this.m_Out.flush();
  }
  
  public void closeOutput()
  {
    try
    {
      write(255);
      write(253);
      write(18);
      this.m_Out.close();
    }
    catch (IOException localIOException)
    {
      log.error("closeOutput()", localIOException);
    }
  }
  
  private void rawWrite(int paramInt)
    throws IOException
  {
    this.m_Out.write(paramInt);
  }
  
  public int read()
    throws IOException
  {
    int i = rawread();
    this.m_NOIAC = false;
    while ((i == 255) && (!this.m_NOIAC))
    {
      i = rawread();
      if (i != 255)
      {
        this.m_IACHandler.handleC(i);
        i = rawread();
      }
      else
      {
        this.m_NOIAC = true;
      }
    }
    return stripCRSeq(i);
  }
  
  public void closeInput()
  {
    try
    {
      this.m_In.close();
    }
    catch (IOException localIOException) {}
  }
  
  private int read16int()
    throws IOException
  {
    int i = this.m_In.readUnsignedShort();
    return i;
  }
  
  private int rawread()
    throws IOException
  {
    int i = 0;
    i = this.m_In.readUnsignedByte();
    this.m_ConnectionData.activity();
    return i;
  }
  
  private int stripCRSeq(int paramInt)
    throws IOException
  {
    if (paramInt == 13)
    {
      rawread();
      return 10;
    }
    return paramInt;
  }
  
  private void initTelnetCommunication()
  {
    this.m_Initializing = true;
    try
    {
      if (this.m_ConnectionData.isLineMode())
      {
        this.m_IACHandler.doLineModeInit();
        log.debug("Line mode initialized.");
      }
      else
      {
        this.m_IACHandler.doCharacterModeInit();
        log.debug("Character mode initialized.");
      }
      this.m_ConnectionData.getSocket().setSoTimeout(1000);
      read();
      try
      {
        this.m_ConnectionData.getSocket().setSoTimeout(0);
      }
      catch (Exception localException1)
      {
        log.error("initTelnetCommunication()", localException1);
      }
      this.m_Initializing = false;
    }
    catch (Exception localException2) {}finally
    {
      try
      {
        this.m_ConnectionData.getSocket().setSoTimeout(0);
      }
      catch (Exception localException4)
      {
        log.error("initTelnetCommunication()", localException4);
      }
    }
  }
  
  private void IamHere()
  {
    try
    {
      write("[" + this.m_LocalAddress.toString() + ":Yes]");
      flush();
    }
    catch (Exception localException)
    {
      log.error("IamHere()", localException);
    }
  }
  
  private void nvtBreak()
  {
    this.m_Connection.processConnectionEvent(new ConnectionEvent(this.m_Connection, 104));
  }
  
  private void setTerminalGeometry(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 20) {
      paramInt1 = 80;
    }
    if (paramInt2 < 6) {
      paramInt2 = 25;
    }
    this.m_ConnectionData.setTerminalGeometry(paramInt1, paramInt2);
  }
  
  public void setEcho(boolean paramBoolean) {}
  
  class IACHandler
  {
    private int[] buffer = new int[2];
    private boolean DO_ECHO = false;
    private boolean DO_SUPGA = false;
    private boolean DO_NAWS = false;
    private boolean DO_TTYPE = false;
    private boolean DO_LINEMODE = false;
    private boolean DO_NEWENV = false;
    private boolean WAIT_DO_REPLY_SUPGA = false;
    private boolean WAIT_DO_REPLY_ECHO = false;
    private boolean WAIT_DO_REPLY_NAWS = false;
    private boolean WAIT_DO_REPLY_TTYPE = false;
    private boolean WAIT_DO_REPLY_LINEMODE = false;
    private boolean WAIT_LM_MODE_ACK = false;
    private boolean WAIT_LM_DO_REPLY_FORWARDMASK = false;
    private boolean WAIT_DO_REPLY_NEWENV = false;
    private boolean WAIT_NE_SEND_REPLY = false;
    private boolean WAIT_WILL_REPLY_SUPGA = false;
    private boolean WAIT_WILL_REPLY_ECHO = false;
    private boolean WAIT_WILL_REPLY_NAWS = false;
    private boolean WAIT_WILL_REPLY_TTYPE = false;
    
    IACHandler() {}
    
    public void doCharacterModeInit()
      throws IOException
    {
      sendCommand(251, 1, true);
      sendCommand(254, 1, true);
      sendCommand(253, 31, true);
      sendCommand(251, 3, true);
      sendCommand(253, 3, true);
      sendCommand(253, 24, true);
      sendCommand(253, 39, true);
    }
    
    public void doLineModeInit()
      throws IOException
    {
      sendCommand(253, 31, true);
      sendCommand(251, 3, true);
      sendCommand(253, 3, true);
      sendCommand(253, 24, true);
      sendCommand(253, 34, true);
      sendCommand(253, 39, true);
    }
    
    public void handleC(int paramInt)
      throws IOException
    {
      this.buffer[0] = paramInt;
      if (!parseTWO(this.buffer))
      {
        this.buffer[1] = TelnetIO.this.rawread();
        parse(this.buffer);
      }
      this.buffer[0] = 0;
      this.buffer[1] = 0;
    }
    
    private boolean parseTWO(int[] paramArrayOfInt)
    {
      switch (paramArrayOfInt[0])
      {
      case 255: 
        break;
      case 246: 
        TelnetIO.this.IamHere();
        break;
      case 241: 
      case 244: 
      case 245: 
      case 247: 
      case 248: 
        break;
      case 243: 
        TelnetIO.this.nvtBreak();
        break;
      case 242: 
      case 249: 
      case 250: 
      case 251: 
      case 252: 
      case 253: 
      case 254: 
      default: 
        return false;
      }
      return true;
    }
    
    private void parse(int[] paramArrayOfInt)
      throws IOException
    {
      switch (paramArrayOfInt[0])
      {
      case 251: 
        if ((!supported(paramArrayOfInt[1])) || (!isEnabled(paramArrayOfInt[1]))) {
          if ((waitDOreply(paramArrayOfInt[1])) && (supported(paramArrayOfInt[1])))
          {
            enable(paramArrayOfInt[1]);
            setWait(253, paramArrayOfInt[1], false);
          }
          else if (supported(paramArrayOfInt[1]))
          {
            sendCommand(253, paramArrayOfInt[1], false);
            enable(paramArrayOfInt[1]);
          }
          else
          {
            sendCommand(254, paramArrayOfInt[1], false);
          }
        }
        break;
      case 252: 
        if ((waitDOreply(paramArrayOfInt[1])) && (supported(paramArrayOfInt[1]))) {
          setWait(253, paramArrayOfInt[1], false);
        } else if ((supported(paramArrayOfInt[1])) && (isEnabled(paramArrayOfInt[1]))) {
          enable(paramArrayOfInt[1]);
        }
        break;
      case 253: 
        if ((!supported(paramArrayOfInt[1])) || (!isEnabled(paramArrayOfInt[1]))) {
          if ((waitWILLreply(paramArrayOfInt[1])) && (supported(paramArrayOfInt[1])))
          {
            enable(paramArrayOfInt[1]);
            setWait(251, paramArrayOfInt[1], false);
          }
          else if (supported(paramArrayOfInt[1]))
          {
            sendCommand(251, paramArrayOfInt[1], false);
            enable(paramArrayOfInt[1]);
          }
          else
          {
            sendCommand(252, paramArrayOfInt[1], false);
          }
        }
        break;
      case 254: 
        if ((waitWILLreply(paramArrayOfInt[1])) && (supported(paramArrayOfInt[1]))) {
          setWait(251, paramArrayOfInt[1], false);
        } else if ((supported(paramArrayOfInt[1])) && (isEnabled(paramArrayOfInt[1]))) {
          enable(paramArrayOfInt[1]);
        }
        break;
      case 242: 
        break;
      case 250: 
        if ((supported(paramArrayOfInt[1])) && (isEnabled(paramArrayOfInt[1]))) {
          switch (paramArrayOfInt[1])
          {
          case 31: 
            handleNAWS();
            break;
          case 24: 
            handleTTYPE();
            break;
          case 34: 
            handleLINEMODE();
            break;
          case 39: 
            handleNEWENV();
          }
        }
        break;
      }
    }
    
    private void handleNAWS()
      throws IOException
    {
      int i = TelnetIO.this.read16int();
      if (i == 255) {
        i = TelnetIO.this.read16int();
      }
      int j = TelnetIO.this.read16int();
      if (j == 255) {
        j = TelnetIO.this.read16int();
      }
      skipToSE();
      TelnetIO.this.setTerminalGeometry(i, j);
    }
    
    private void handleTTYPE()
      throws IOException
    {
      String str = "";
      TelnetIO.this.rawread();
      str = readIACSETerminatedString(40);
      TelnetIO.log.debug("Reported terminal name " + str);
      TelnetIO.this.m_ConnectionData.setNegotiatedTerminalType(str);
    }
    
    public void handleLINEMODE()
      throws IOException
    {
      int i = TelnetIO.this.rawread();
      switch (i)
      {
      case 1: 
        handleLMMode();
        break;
      case 3: 
        handleLMSLC();
        break;
      case 251: 
      case 252: 
        handleLMForwardMask(i);
        break;
      default: 
        skipToSE();
      }
    }
    
    public void handleLMMode()
      throws IOException
    {
      if (this.WAIT_LM_MODE_ACK)
      {
        int i = TelnetIO.this.rawread();
        if (i != 7) {
          TelnetIO.log.debug("Client violates linemodeack sent: " + i);
        }
        this.WAIT_LM_MODE_ACK = false;
      }
      skipToSE();
    }
    
    public void handleLMSLC()
      throws IOException
    {
      int[] arrayOfInt = new int[3];
      if (!readTriple(arrayOfInt)) {
        return;
      }
      if ((arrayOfInt[0] == 0) && (arrayOfInt[1] == 3) && (arrayOfInt[2] == 0))
      {
        skipToSE();
        TelnetIO.this.rawWrite(255);
        TelnetIO.this.rawWrite(250);
        TelnetIO.this.rawWrite(34);
        TelnetIO.this.rawWrite(3);
        for (int i = 1; i < 12; i++)
        {
          TelnetIO.this.rawWrite(i);
          TelnetIO.this.rawWrite(3);
          TelnetIO.this.rawWrite(0);
        }
        TelnetIO.this.rawWrite(255);
        TelnetIO.this.rawWrite(240);
        TelnetIO.this.flush();
      }
      else
      {
        TelnetIO.this.rawWrite(255);
        TelnetIO.this.rawWrite(250);
        TelnetIO.this.rawWrite(34);
        TelnetIO.this.rawWrite(3);
        TelnetIO.this.rawWrite(arrayOfInt[0]);
        TelnetIO.this.rawWrite(arrayOfInt[1] | 0x80);
        TelnetIO.this.rawWrite(arrayOfInt[2]);
        while (readTriple(arrayOfInt))
        {
          TelnetIO.this.rawWrite(arrayOfInt[0]);
          TelnetIO.this.rawWrite(arrayOfInt[1] | 0x80);
          TelnetIO.this.rawWrite(arrayOfInt[2]);
        }
        TelnetIO.this.rawWrite(255);
        TelnetIO.this.rawWrite(240);
        TelnetIO.this.flush();
      }
    }
    
    public void handleLMForwardMask(int paramInt)
      throws IOException
    {
      switch (paramInt)
      {
      case 252: 
        if (this.WAIT_LM_DO_REPLY_FORWARDMASK) {
          this.WAIT_LM_DO_REPLY_FORWARDMASK = false;
        }
        break;
      }
      skipToSE();
    }
    
    public void handleNEWENV()
      throws IOException
    {
      TelnetIO.log.debug("handleNEWENV()");
      int i = TelnetIO.this.rawread();
      switch (i)
      {
      case 0: 
        handleNEIs();
        break;
      case 2: 
        handleNEInfo();
        break;
      default: 
        skipToSE();
      }
    }
    
    private int readNEVariableName(StringBuffer paramStringBuffer)
      throws IOException
    {
      TelnetIO.log.debug("readNEVariableName()");
      int i = -1;
      for (;;)
      {
        i = TelnetIO.this.rawread();
        if (i == -1) {
          return -2;
        }
        if (i == 255)
        {
          i = TelnetIO.this.rawread();
          if (i == 255)
          {
            paramStringBuffer.append((char)i);
          }
          else
          {
            if (i == 240) {
              return -3;
            }
            return -2;
          }
        }
        else if (i == 2)
        {
          i = TelnetIO.this.rawread();
          if ((i == 2) || (i == 0) || (i == 3) || (i == 1)) {
            paramStringBuffer.append((char)i);
          } else {
            return -2;
          }
        }
        else
        {
          if ((i == 0) || (i == 3)) {
            return -1;
          }
          if (i == 1) {
            return 1;
          }
          if (paramStringBuffer.length() >= 50) {
            return -2;
          }
          paramStringBuffer.append((char)i);
        }
      }
    }
    
    private int readNEVariableValue(StringBuffer paramStringBuffer)
      throws IOException
    {
      TelnetIO.log.debug("readNEVariableValue()");
      int i = TelnetIO.this.rawread();
      if (i == -1) {
        return -2;
      }
      if (i == 255)
      {
        i = TelnetIO.this.rawread();
        if (i == 255) {
          return 0;
        }
        if (i == 240) {
          return -3;
        }
        return -2;
      }
      if ((i == 0) || (i == 3)) {
        return 0;
      }
      if (i == 2)
      {
        i = TelnetIO.this.rawread();
        if ((i == 2) || (i == 0) || (i == 3) || (i == 1)) {
          paramStringBuffer.append((char)i);
        } else {
          return -2;
        }
      }
      else
      {
        paramStringBuffer.append((char)i);
      }
      for (;;)
      {
        i = TelnetIO.this.rawread();
        if (i == -1) {
          return -2;
        }
        if (i == 255)
        {
          i = TelnetIO.this.rawread();
          if (i == 255)
          {
            paramStringBuffer.append((char)i);
          }
          else
          {
            if (i == 240) {
              return -3;
            }
            return -2;
          }
        }
        else if (i == 2)
        {
          i = TelnetIO.this.rawread();
          if ((i == 2) || (i == 0) || (i == 3) || (i == 1)) {
            paramStringBuffer.append((char)i);
          } else {
            return -2;
          }
        }
        else
        {
          if ((i == 0) || (i == 3)) {
            return 2;
          }
          if (paramStringBuffer.length() > 1000) {
            return -2;
          }
          paramStringBuffer.append((char)i);
        }
      }
    }
    
    public void readNEVariables()
      throws IOException
    {
      TelnetIO.log.debug("readNEVariables()");
      StringBuffer localStringBuffer = new StringBuffer(50);
      int i = TelnetIO.this.rawread();
      if (i == 255)
      {
        skipToSE();
        TelnetIO.log.debug("readNEVariables()::INVALID VARIABLE");
        return;
      }
      int j = 1;
      if ((i == 0) || (i == 3)) {
        do
        {
          switch (readNEVariableName(localStringBuffer))
          {
          case -2: 
            TelnetIO.log.debug("readNEVariables()::NE_IN_ERROR");
            return;
          case -3: 
            TelnetIO.log.debug("readNEVariables()::NE_IN_END");
            return;
          case 1: 
            TelnetIO.log.debug("readNEVariables()::NE_VAR_DEFINED");
            String str = localStringBuffer.toString();
            localStringBuffer.delete(0, localStringBuffer.length());
            switch (readNEVariableValue(localStringBuffer))
            {
            case -2: 
              TelnetIO.log.debug("readNEVariables()::NE_IN_ERROR");
              return;
            case -3: 
              TelnetIO.log.debug("readNEVariables()::NE_IN_END");
              return;
            case 0: 
              TelnetIO.log.debug("readNEVariables()::NE_VAR_DEFINED_EMPTY");
              break;
            case 2: 
              TelnetIO.log.debug("readNEVariables()::NE_VAR_OK:VAR=" + str + " VAL=" + localStringBuffer.toString());
              TelnetIO.this.m_ConnectionData.getEnvironment().put(str, localStringBuffer.toString());
              localStringBuffer.delete(0, localStringBuffer.length());
            }
            break;
          case -1: 
            TelnetIO.log.debug("readNEVariables()::NE_VAR_UNDEFINED");
          }
        } while (j != 0);
      }
    }
    
    public void handleNEIs()
      throws IOException
    {
      TelnetIO.log.debug("handleNEIs()");
      if (isEnabled(39)) {
        readNEVariables();
      }
    }
    
    public void handleNEInfo()
      throws IOException
    {
      TelnetIO.log.debug("handleNEInfo()");
      if (isEnabled(39)) {
        readNEVariables();
      }
    }
    
    public void getTTYPE()
      throws IOException
    {
      if (isEnabled(24))
      {
        TelnetIO.this.rawWrite(255);
        TelnetIO.this.rawWrite(250);
        TelnetIO.this.rawWrite(24);
        TelnetIO.this.rawWrite(1);
        TelnetIO.this.rawWrite(255);
        TelnetIO.this.rawWrite(240);
        TelnetIO.this.flush();
      }
    }
    
    public void negotiateLineMode()
      throws IOException
    {
      if (isEnabled(34))
      {
        TelnetIO.this.rawWrite(255);
        TelnetIO.this.rawWrite(250);
        TelnetIO.this.rawWrite(34);
        TelnetIO.this.rawWrite(1);
        TelnetIO.this.rawWrite(3);
        TelnetIO.this.rawWrite(255);
        TelnetIO.this.rawWrite(240);
        this.WAIT_LM_MODE_ACK = true;
        TelnetIO.this.rawWrite(255);
        TelnetIO.this.rawWrite(250);
        TelnetIO.this.rawWrite(34);
        TelnetIO.this.rawWrite(254);
        TelnetIO.this.rawWrite(2);
        TelnetIO.this.rawWrite(255);
        TelnetIO.this.rawWrite(240);
        this.WAIT_LM_DO_REPLY_FORWARDMASK = true;
        TelnetIO.this.flush();
      }
    }
    
    private void negotiateEnvironment()
      throws IOException
    {
      if (isEnabled(39))
      {
        TelnetIO.this.rawWrite(255);
        TelnetIO.this.rawWrite(250);
        TelnetIO.this.rawWrite(39);
        TelnetIO.this.rawWrite(1);
        TelnetIO.this.rawWrite(0);
        TelnetIO.this.rawWrite(3);
        TelnetIO.this.rawWrite(255);
        TelnetIO.this.rawWrite(240);
        this.WAIT_NE_SEND_REPLY = true;
        TelnetIO.this.flush();
      }
    }
    
    private void skipToSE()
      throws IOException
    {
      while (TelnetIO.this.rawread() != 240) {}
    }
    
    private boolean readTriple(int[] paramArrayOfInt)
      throws IOException
    {
      paramArrayOfInt[0] = TelnetIO.this.rawread();
      paramArrayOfInt[1] = TelnetIO.this.rawread();
      if ((paramArrayOfInt[0] == 255) && (paramArrayOfInt[1] == 240)) {
        return false;
      }
      paramArrayOfInt[2] = TelnetIO.this.rawread();
      return true;
    }
    
    private String readIACSETerminatedString(int paramInt)
      throws IOException
    {
      int i = 0;
      char[] arrayOfChar = new char[paramInt];
      int j = 32;
      int k = 1;
      do
      {
        int m = TelnetIO.this.rawread();
        switch (m)
        {
        case 255: 
          m = TelnetIO.this.rawread();
          if (m == 240) {
            k = 0;
          }
          break;
        case -1: 
          return new String("default");
        }
        if (k != 0)
        {
          j = (char)m;
          if ((j == 10) || (j == 13) || (i == paramInt)) {
            k = 0;
          } else {
            arrayOfChar[(i++)] = (char) j;
          }
        }
      } while (k != 0);
      return new String(arrayOfChar, 0, i);
    }
    
    private boolean supported(int paramInt)
    {
      switch (paramInt)
      {
      case 1: 
      case 3: 
      case 24: 
      case 31: 
      case 39: 
        return true;
      case 34: 
        return TelnetIO.this.m_ConnectionData.isLineMode();
      }
      return false;
    }
    
    private void sendCommand(int paramInt1, int paramInt2, boolean paramBoolean)
      throws IOException
    {
      TelnetIO.this.rawWrite(255);
      TelnetIO.this.rawWrite(paramInt1);
      TelnetIO.this.rawWrite(paramInt2);
      if ((paramInt1 == 253) && (paramBoolean)) {
        setWait(253, paramInt2, true);
      }
      if ((paramInt1 == 251) && (paramBoolean)) {
        setWait(251, paramInt2, true);
      }
      TelnetIO.this.flush();
    }
    
    private void enable(int paramInt)
      throws IOException
    {
      switch (paramInt)
      {
      case 3: 
        if (this.DO_SUPGA) {
          this.DO_SUPGA = false;
        } else {
          this.DO_SUPGA = true;
        }
        break;
      case 1: 
        if (this.DO_ECHO) {
          this.DO_ECHO = false;
        } else {
          this.DO_ECHO = true;
        }
        break;
      case 31: 
        if (this.DO_NAWS) {
          this.DO_NAWS = false;
        } else {
          this.DO_NAWS = true;
        }
        break;
      case 24: 
        if (this.DO_TTYPE)
        {
          this.DO_TTYPE = false;
        }
        else
        {
          this.DO_TTYPE = true;
          getTTYPE();
        }
        break;
      case 34: 
        if (this.DO_LINEMODE)
        {
          this.DO_LINEMODE = false;
          TelnetIO.this.m_ConnectionData.setLineMode(false);
        }
        else
        {
          this.DO_LINEMODE = true;
          negotiateLineMode();
        }
        break;
      case 39: 
        if (this.DO_NEWENV)
        {
          this.DO_NEWENV = false;
        }
        else
        {
          this.DO_NEWENV = true;
          negotiateEnvironment();
        }
        break;
      }
    }
    
    private boolean isEnabled(int paramInt)
    {
      switch (paramInt)
      {
      case 3: 
        return this.DO_SUPGA;
      case 1: 
        return this.DO_ECHO;
      case 31: 
        return this.DO_NAWS;
      case 24: 
        return this.DO_TTYPE;
      case 34: 
        return this.DO_LINEMODE;
      case 39: 
        return this.DO_NEWENV;
      }
      return false;
    }
    
    private boolean waitWILLreply(int paramInt)
    {
      switch (paramInt)
      {
      case 3: 
        return this.WAIT_WILL_REPLY_SUPGA;
      case 1: 
        return this.WAIT_WILL_REPLY_ECHO;
      case 31: 
        return this.WAIT_WILL_REPLY_NAWS;
      case 24: 
        return this.WAIT_WILL_REPLY_TTYPE;
      }
      return false;
    }
    
    private boolean waitDOreply(int paramInt)
    {
      switch (paramInt)
      {
      case 3: 
        return this.WAIT_DO_REPLY_SUPGA;
      case 1: 
        return this.WAIT_DO_REPLY_ECHO;
      case 31: 
        return this.WAIT_DO_REPLY_NAWS;
      case 24: 
        return this.WAIT_DO_REPLY_TTYPE;
      case 34: 
        return this.WAIT_DO_REPLY_LINEMODE;
      case 39: 
        return this.WAIT_DO_REPLY_NEWENV;
      }
      return false;
    }
    
    private void setWait(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      switch (paramInt1)
      {
      case 253: 
        switch (paramInt2)
        {
        case 3: 
          this.WAIT_DO_REPLY_SUPGA = paramBoolean;
          break;
        case 1: 
          this.WAIT_DO_REPLY_ECHO = paramBoolean;
          break;
        case 31: 
          this.WAIT_DO_REPLY_NAWS = paramBoolean;
          break;
        case 24: 
          this.WAIT_DO_REPLY_TTYPE = paramBoolean;
          break;
        case 34: 
          this.WAIT_DO_REPLY_LINEMODE = paramBoolean;
          break;
        case 39: 
          this.WAIT_DO_REPLY_NEWENV = paramBoolean;
        }
        break;
      case 251: 
        switch (paramInt2)
        {
        case 3: 
          this.WAIT_WILL_REPLY_SUPGA = paramBoolean;
          break;
        case 1: 
          this.WAIT_WILL_REPLY_ECHO = paramBoolean;
          break;
        case 31: 
          this.WAIT_WILL_REPLY_NAWS = paramBoolean;
          break;
        case 24: 
          this.WAIT_WILL_REPLY_TTYPE = paramBoolean;
        }
        break;
      }
    }
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.TelnetIO

 * JD-Core Version:    0.7.0.1

 */