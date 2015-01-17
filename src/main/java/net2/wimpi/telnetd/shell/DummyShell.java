package net2.wimpi.telnetd.shell;

import java.io.IOException;
import java.util.HashMap;
import net2.wimpi.telnetd.io.BasicTerminalIO;
import net2.wimpi.telnetd.io.TerminalIO;
import net2.wimpi.telnetd.io.terminal.TerminalManager;
import net2.wimpi.telnetd.io.toolkit.BufferOverflowException;
import net2.wimpi.telnetd.io.toolkit.Checkbox;
import net2.wimpi.telnetd.io.toolkit.Editarea;
import net2.wimpi.telnetd.io.toolkit.Editfield;
import net2.wimpi.telnetd.io.toolkit.InputFilter;
import net2.wimpi.telnetd.io.toolkit.Label;
import net2.wimpi.telnetd.io.toolkit.Pager;
import net2.wimpi.telnetd.io.toolkit.Point;
import net2.wimpi.telnetd.io.toolkit.Selection;
import net2.wimpi.telnetd.io.toolkit.Statusbar;
import net2.wimpi.telnetd.io.toolkit.Titlebar;
import net2.wimpi.telnetd.net.Connection;
import net2.wimpi.telnetd.net.ConnectionData;
import net2.wimpi.telnetd.net.ConnectionEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DummyShell
  implements Shell
{
  private static Log log = LogFactory.getLog(DummyShell.class);
  private Connection m_Connection;
  private BasicTerminalIO m_IO;
  private Editfield m_EF;
  private static final String logo = "\nA looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong line!";
  
  public void run(Connection paramConnection)
  {
    try
    {
      this.m_Connection = paramConnection;
      this.m_IO = this.m_Connection.getTerminalIO();
      this.m_Connection.addConnectionListener(this);
      this.m_IO.eraseScreen();
      this.m_IO.homeCursor();
      this.m_IO.write("Dummy Shell. Please press enter to logout!\r\n");
      this.m_IO.flush();
      int i = 0;
      while (i == 0)
      {
        int j = this.m_IO.read();
        if ((j == -1) || (j == -2))
        {
          log.debug("Input(Code):" + j);
          i = 1;
        }
        if (j == 10)
        {
          i = 1;
        }
        else
        {
          Object localObject;
          if (j == 117)
          {
            localObject = this.m_Connection.getConnectionData();
            this.m_IO.write("\r\nDEBUG: Active Connection\r\n");
            this.m_IO.write("------------------------\r\n");
            this.m_IO.write("Connected from: " + ((ConnectionData)localObject).getHostName() + "[" + ((ConnectionData)localObject).getHostAddress() + ":" + ((ConnectionData)localObject).getPort() + "]" + "\r\n");
            this.m_IO.write("Guessed Locale: " + ((ConnectionData)localObject).getLocale() + "\r\n");
            this.m_IO.write("\r\n");
            this.m_IO.write("Negotiated Terminal Type: " + ((ConnectionData)localObject).getNegotiatedTerminalType() + "\r\n");
            this.m_IO.write("Negotiated Columns: " + ((ConnectionData)localObject).getTerminalColumns() + "\r\n");
            this.m_IO.write("Negotiated Rows: " + ((ConnectionData)localObject).getTerminalRows() + "\r\n");
            this.m_IO.write("\r\n");
            this.m_IO.write("Assigned Terminal instance: " + ((TerminalIO)this.m_IO).getTerminal());
            this.m_IO.write("\r\n");
            this.m_IO.write("Environment: " + ((ConnectionData)localObject).getEnvironment().toString());
            this.m_IO.write("\r\n");
            this.m_IO.write("-----------------------------------------------\r\n\r\n");
            this.m_IO.flush();
          }
          else if (j == 101)
          {
            localObject = new Label(this.m_IO, "testedit", "TestEdit: ");
            this.m_EF = new Editfield(this.m_IO, "edit", 50);
            this.m_EF.registerInputFilter(new InputFilter()
            {
              public int filterInput(int paramAnonymousInt)
                throws IOException
              {
                if (paramAnonymousInt == 116)
                {
                  try
                  {
                    DummyShell.this.m_EF.setValue("Test");
                  }
                  catch (BufferOverflowException localBufferOverflowException) {}
                  return -2000;
                }
                if (paramAnonymousInt == 99)
                {
                  DummyShell.this.m_EF.clear();
                  return -2000;
                }
                return paramAnonymousInt;
              }
            });
            ((Label)localObject).draw();
            this.m_EF.run();
          }
          else if (j == 116)
          {
            localObject = new Pager(this.m_IO);
            ((Pager)localObject).setShowPosition(true);
            ((Pager)localObject).page("\nA looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong line!");
            Label localLabel = new Label(this.m_IO, "label1");
            localLabel.setText("Hello World!");
            localLabel.setLocation(new Point(1, 5));
            localLabel.draw();
            this.m_IO.flush();
            this.m_IO.homeCursor();
            this.m_IO.eraseScreen();
            Titlebar localTitlebar1 = new Titlebar(this.m_IO, "title 1");
            localTitlebar1.setTitleText("MyTitle");
            localTitlebar1.setAlignment(3);
            localTitlebar1.setBackgroundColor("B");
            localTitlebar1.setForegroundColor("Y");
            localTitlebar1.draw();
            Statusbar localStatusbar1 = new Statusbar(this.m_IO, "status 1");
            localStatusbar1.setStatusText("MyStatus");
            localStatusbar1.setAlignment(2);
            localStatusbar1.setBackgroundColor("B");
            localStatusbar1.setForegroundColor("Y");
            localStatusbar1.draw();
            this.m_IO.flush();
            this.m_IO.setCursor(2, 1);
            Selection localSelection = new Selection(this.m_IO, "selection 1");
            String[] arrayOfString = TerminalManager.getReference().getAvailableTerminals();
            for (int k = 0; k < arrayOfString.length; k++) {
              localSelection.addOption(arrayOfString[k]);
            }
            localSelection.setLocation(1, 10);
            localSelection.run();
            Checkbox localCheckbox = new Checkbox(this.m_IO, "checkbox 1");
            localCheckbox.setText("Check me !");
            localCheckbox.setLocation(1, 12);
            localCheckbox.run();
            Editfield localEditfield1 = new Editfield(this.m_IO, "editfield 1", 20);
            localEditfield1.setLocation(1, 13);
            localEditfield1.run();
            try
            {
              localEditfield1.setValue("SETVALUE!");
            }
            catch (Exception localException2) {}
            Editfield localEditfield2 = new Editfield(this.m_IO, "editfield 2", 8);
            localEditfield2.setLocation(1, 14);
            localEditfield2.setPasswordField(true);
            localEditfield2.run();
            log.debug("Your secret password was:" + localEditfield2.getValue());
            this.m_IO.flush();
            this.m_IO.eraseScreen();
            this.m_IO.homeCursor();
            Titlebar localTitlebar2 = new Titlebar(this.m_IO, "title 1");
            localTitlebar2.setTitleText("jEditor v0.1");
            localTitlebar2.setAlignment(2);
            localTitlebar2.setBackgroundColor("B");
            localTitlebar2.setForegroundColor("Y");
            localTitlebar2.draw();
            Statusbar localStatusbar2 = new Statusbar(this.m_IO, "status 1");
            localStatusbar2.setStatusText("Status");
            localStatusbar2.setAlignment(2);
            localStatusbar2.setBackgroundColor("B");
            localStatusbar2.setForegroundColor("Y");
            localStatusbar2.draw();
            this.m_IO.setCursor(2, 1);
            Editarea localEditarea = new Editarea(this.m_IO, "area", this.m_IO.getRows() - 2, 100);
            this.m_IO.flush();
            localEditarea.run();
            log.debug(localEditarea.getValue());
            this.m_IO.eraseScreen();
            this.m_IO.homeCursor();
            this.m_IO.write("Dummy Shell. Please press enter to logout!\r\n");
            this.m_IO.flush();
          }
          else
          {
            log.debug("Input(Code):" + j);
          }
        }
      }
      this.m_IO.homeCursor();
      this.m_IO.eraseScreen();
      this.m_IO.write("Goodbye!.\r\n\r\n");
      this.m_IO.flush();
    }
    catch (Exception localException1)
    {
      log.error("run()", localException1);
    }
  }
  
  public void connectionTimedOut(ConnectionEvent paramConnectionEvent)
  {
    try
    {
      this.m_IO.write("CONNECTION_TIMEDOUT");
      this.m_IO.flush();
      this.m_Connection.close();
    }
    catch (Exception localException)
    {
      log.error("connectionTimedOut()", localException);
    }
  }
  
  public void connectionIdle(ConnectionEvent paramConnectionEvent)
  {
    try
    {
      this.m_IO.write("CONNECTION_IDLE");
      this.m_IO.flush();
    }
    catch (IOException localIOException)
    {
      log.error("connectionIdle()", localIOException);
    }
  }
  
  public void connectionLogoutRequest(ConnectionEvent paramConnectionEvent)
  {
    try
    {
      this.m_IO.write("CONNECTION_LOGOUTREQUEST");
      this.m_IO.flush();
    }
    catch (Exception localException)
    {
      log.error("connectionLogoutRequest()", localException);
    }
  }
  
  public void connectionSentBreak(ConnectionEvent paramConnectionEvent)
  {
    try
    {
      this.m_IO.write("CONNECTION_BREAK");
      this.m_IO.flush();
    }
    catch (Exception localException)
    {
      log.error("connectionSentBreak()", localException);
    }
  }
  
  public static Shell createShell()
  {
    return new DummyShell();
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.shell.DummyShell

 * JD-Core Version:    0.7.0.1

 */