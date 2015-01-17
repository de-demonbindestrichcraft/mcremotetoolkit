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
package org.apache.log;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * This class encapsulates each individual log event.
 * LogEvents usually originate at a Logger and are routed
 * to LogTargets.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public final class LogEvent
    implements Serializable
{
    //A Constant used when retrieving time relative to start of applicaiton start
    private static final long START_TIME = System.currentTimeMillis();

    ///The category that this LogEvent concerns. (Must not be null)
    private String m_category;

    ///The message to be logged. (Must not be null)
    private String m_message;

    ///The exception that caused LogEvent if any. (May be null)
    private Throwable m_throwable;

    ///The time in millis that LogEvent occurred
    private long m_time;

    ///The priority of LogEvent. (Must not be null)
    private Priority m_priority;

    ///The context map associated with LogEvent. (May be null).
    private ContextMap m_contextMap;

    /**
     * The context stack associated with LogEvent. (May be null)
     * @deprecated ContextStack has been deprecated and thus so has this field.
     */
    private transient ContextStack m_contextStack;

    /**
     * Get Priority for LogEvent.
     *
     * @return the LogEvent Priority
     */
    public final Priority getPriority()
    {
        return m_priority;
    }

    /**
     * Set the priority of LogEvent.
     *
     * @param priority the new LogEvent priority
     */
    public final void setPriority( final Priority priority )
    {
        m_priority = priority;
    }

    /**
     * Get ContextMap associated with LogEvent
     *
     * @return the ContextMap
     */
    public final ContextMap getContextMap()
    {
        return m_contextMap;
    }

    /**
     * Set the ContextMap for this LogEvent.
     *
     * @param contextMap the context map
     */
    public final void setContextMap( final ContextMap contextMap )
    {
        m_contextMap = contextMap;
    }

    /**
     * Get ContextStack associated with LogEvent
     *
     * @return the ContextStack
     * @deprecated ContextStack has been deprecated and thus so has this method
     */
    public final ContextStack getContextStack()
    {
        return m_contextStack;
    }

    /**
     * Set the ContextStack for this LogEvent.
     * Note that if this LogEvent ever changes threads, the
     * ContextStack must be cloned.
     *
     * @param contextStack the context stack
     * @deprecated ContextStack has been deprecated and thus so has this method
     */
    public final void setContextStack( final ContextStack contextStack )
    {
        m_contextStack = contextStack;
    }

    /**
     * Get the category that LogEvent relates to.
     *
     * @return the name of category
     */
    public final String getCategory()
    {
        return m_category;
    }

    /**
     * Get the message associated with event.
     *
     * @return the message
     */
    public final String getMessage()
    {
        return m_message;
    }

    /**
     * Get throwabe instance associated with event.
     *
     * @return the Throwable
     */
    public final Throwable getThrowable()
    {
        return m_throwable;
    }

    /**
     * Get the absolute time of the log event.
     *
     * @return the absolute time
     */
    public final long getTime()
    {
        return m_time;
    }

    /**
     * Get the time of the log event relative to start of application.
     *
     * @return the time
     */
    public final long getRelativeTime()
    {
        return m_time - START_TIME;
    }

    /**
     * Set the LogEvent category.
     *
     * @param category the category
     */
    public final void setCategory( final String category )
    {
        m_category = category;
    }

    /**
     * Set the message for LogEvent.
     *
     * @param message the message
     */
    public final void setMessage( final String message )
    {
        m_message = message;
    }

    /**
     * Set the throwable for LogEvent.
     *
     * @param throwable the instance of Throwable
     */
    public final void setThrowable( final Throwable throwable )
    {
        m_throwable = throwable;
    }

    /**
     * Set the absolute time of LogEvent.
     *
     * @param time the time
     */
    public final void setTime( final long time )
    {
        m_time = time;
    }

    /**
     * Helper method that replaces deserialized priority with correct singleton.
     *
     * @return the singleton version of object
     * @exception ObjectStreamException if an error occurs
     */
    private Object readResolve()
        throws ObjectStreamException
    {
        if( null == m_category )
        {
            m_category = "";
        }
        if( null == m_message )
        {
            m_message = "";
        }

        String priorityName = "";
        if( null != m_priority )
        {
            priorityName = m_priority.getName();
        }

        m_priority = Priority.getPriorityForName( priorityName );

        return this;
    }
}
