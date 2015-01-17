/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.log.output.lf5;

import org.apache.log.LogEvent;
import org.apache.log.LogTarget;
import org.apache.log.format.Formatter;
import org.apache.log.format.PatternFormatter;
import org.apache.log4j.lf5.viewer.LogBrokerMonitor;

/**
 * A {@link LogTarget} that displays log events using the
 * <a href="http://jakarta.apache.org/log4j/docs/lf5/overview.html">LogFactor5</a>
 * Swing GUI.
 *
 * @author <a href="sylvain@apache.org">Sylvain Wallez</a>
 * @version CVS $Revision: 1.6 $ $Date: 2003/04/07 11:36:51 $
 */
public class LF5LogTarget implements LogTarget
{
    /** Common monitor */
    private static LogBrokerMonitor c_defaultLogMonitor;

    /** Default context map formatter */
    private static Formatter c_defaultContextFormatter = new PatternFormatter( "" );

    /** Monitor for this LogTarget */
    private LogBrokerMonitor m_monitor;

    /** Format for context maps */
    private Formatter m_contextFormatter = c_defaultContextFormatter;

    /**
     * Create a <code>LogFactorLogTarget</code> on a given <code>LogBrokerMonitor</code>.
     * @param monitor the monitor
     */
    public LF5LogTarget( final LogBrokerMonitor monitor )
    {
        m_monitor = monitor;
    }

    /**
     * Create <code>LogFactorLogTarget</code> on the default <code>LogBrokerMonitor</code>.
     */
    public LF5LogTarget()
    {
        // Creation of m_monitor is deferred up to the first call to processEvent().
        // This allows the Swing window to pop up only if this target is actually used.
    }

    /**
     * Sets the {@link Formatter} that will be used to produce the "NDC" (nested diagnostic
     * context) text on the GUI.
     * @param formatter the message formatter
     */
    public void setNDCFormatter( final Formatter formatter )
    {
        m_contextFormatter = formatter;
    }

    /**
     * Get the default <code>LogBrokerMonitor</code> instance.
     *
     * @return the monitor
     */
    public static synchronized LogBrokerMonitor getDefaultMonitor()
    {
        if( null == c_defaultLogMonitor )
        {
            c_defaultLogMonitor = new LogBrokerMonitor( LogKitLogRecord.LOGKIT_LOGLEVELS );
            c_defaultLogMonitor.setFontSize( 12 );
            c_defaultLogMonitor.show();
        }

        return c_defaultLogMonitor;
    }

    /**
     * Process a log event.
     *
     * @param event the log event
     */
    public void processEvent( final LogEvent event )
    {
        if( null == m_monitor )
        {
            m_monitor = getDefaultMonitor();
        }

        m_monitor.addMessage( new LogKitLogRecord( event, m_contextFormatter ) );
    }
}
