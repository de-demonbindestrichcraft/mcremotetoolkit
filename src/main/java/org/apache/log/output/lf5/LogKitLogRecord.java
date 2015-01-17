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

import java.util.Arrays;
import java.util.List;
import org.apache.log.ContextMap;
import org.apache.log.LogEvent;
import org.apache.log.Logger;
import org.apache.log.Priority;
import org.apache.log.format.Formatter;
import org.apache.log.util.StackIntrospector;
import org.apache.log4j.lf5.LogLevel;
import org.apache.log4j.lf5.LogRecord;

/**
 * An implementation of a LogFactor5 <code>LogRecord</code> based on a
 * LogKit {@link LogEvent}.
 *
 * @author <a href="sylvain@apache.org">Sylvain Wallez</a>
 * @version CVS $Revision: 1.5 $ $Date: 2003/04/07 11:36:51 $
 */

public class LogKitLogRecord
    extends LogRecord
{
    /** Is this a severe event ? */
    private boolean m_severe;

    /**
     * Create a LogFactor record from a LogKit event
     */
    public LogKitLogRecord( final LogEvent event, final Formatter fmt )
    {
        final ContextMap contextMap = event.getContextMap();

        Object contextObject;

        // Category
        setCategory( event.getCategory() );

        // Level
        setLevel( toLogLevel( event.getPriority() ) );
        m_severe = event.getPriority().isGreater( Priority.INFO );

        // Location
        if( null != contextMap && null != ( contextObject = contextMap.get( "method" ) ) )
        {
            setLocation( contextObject.toString() );
        }
        else
        {
            setLocation( StackIntrospector.getCallerMethod( Logger.class ) );
        }

        // Message
        setMessage( event.getMessage() );

        // Millis
        setMillis( event.getTime() );

        // NDC
        setNDC( fmt.format( event ) );

        // SequenceNumber
        //setSequenceNumber( 0L );

        // ThreadDescription
        if( null != contextMap && null != ( contextObject = contextMap.get( "thread" ) ) )
        {
            setThreadDescription( contextObject.toString() );
        }
        else
        {
            setThreadDescription( Thread.currentThread().getName() );
        }

        // Thrown
        setThrown( event.getThrowable() );

        // ThrownStackTrace
        //setThrownStackTrace("");
    }

    public boolean isSevereLevel()
    {
        return m_severe;
    }

    /**
     * Convert a LogKit <code>Priority</code> to a LogFactor <code>LogLevel</code>.
     */
    public LogLevel toLogLevel( final Priority priority )
    {
        if( Priority.DEBUG == priority )
        {
            return LogLevel.DEBUG;
        }
        else if( Priority.INFO == priority )
        {
            return LogLevel.INFO;
        }
        else if( Priority.WARN == priority )
        {
            return LogLevel.WARN;
        }
        else if( Priority.ERROR == priority )
        {
            return LogLevel.ERROR;
        }
        else if( Priority.FATAL_ERROR == priority )
        {
            return LogLevel.FATAL;
        }
        else
        {
            return new LogLevel( priority.getName(), priority.getValue() );
        }
    }

    /**
     * The <code>LogLevel</code>s corresponding to LogKit priorities.
     */
    public static final List LOGKIT_LOGLEVELS =
        Arrays.asList( new LogLevel[]{
            LogLevel.FATAL, LogLevel.ERROR, LogLevel.WARN, LogLevel.INFO, LogLevel.DEBUG
        } );
}
