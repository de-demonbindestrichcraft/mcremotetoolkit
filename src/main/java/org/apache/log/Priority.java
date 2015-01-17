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

import java.io.Serializable;

/**
 * Class representing and holding constants for priority.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public final class Priority
    implements Serializable
{
    /**
     * Developer orientated messages, usually used during development of product.
     */
    public static final Priority DEBUG = new Priority( "DEBUG", 5 );

    /**
     * Useful information messages such as state changes, client connection, user login etc.
     */
    public static final Priority INFO = new Priority( "INFO", 10 );

    /**
     * A problem or conflict has occurred but it may be recoverable, then
     * again it could be the start of the system failing.
     */
    public static final Priority WARN = new Priority( "WARN", 15 );

    /**
     * A problem has occurred but it is not fatal. The system will still function.
     */
    public static final Priority ERROR = new Priority( "ERROR", 20 );

    /**
     * Something caused whole system to fail. This indicates that an administrator
     * should restart the system and try to fix the problem that caused the failure.
     */
    public static final Priority FATAL_ERROR = new Priority( "FATAL_ERROR", 25 );

    /**
     * Do not log anything.
     */
    public static final Priority NONE = new Priority( "NONE", Integer.MAX_VALUE );

    private final String m_name;
    private final int m_priority;

    /**
     * Retrieve a Priority object for the name parameter.
     *
     * @param priority the priority name
     * @return the Priority for name
     */
    public static Priority getPriorityForName( final String priority )
    {
        if( Priority.DEBUG.getName().equals( priority ) )
        {
            return Priority.DEBUG;
        }
        else if( Priority.INFO.getName().equals( priority ) )
        {
            return Priority.INFO;
        }
        else if( Priority.WARN.getName().equals( priority ) )
        {
            return Priority.WARN;
        }
        else if( Priority.ERROR.getName().equals( priority ) )
        {
            return Priority.ERROR;
        }
        else if( Priority.FATAL_ERROR.getName().equals( priority ) )
        {
            return Priority.FATAL_ERROR;
        }
        else if( Priority.NONE.getName().equals( priority ) )
        {
            return Priority.NONE;
        }
        else
        {
            return Priority.DEBUG;
        }
    }

    /**
     * Private Constructor to block instantiation outside class.
     *
     * @param name the string name of priority
     * @param priority the numerical code of priority
     */
    private Priority( final String name, final int priority )
    {
        if( null == name )
        {
            throw new NullPointerException( "name" );
        }

        m_name = name;
        m_priority = priority;
    }

    /**
     * Overidden string to display Priority in human readable form.
     *
     * @return the string describing priority
     */
    public String toString()
    {
        return "Priority[" + getName() + "/" + getValue() + "]";
    }

    /**
     * Get numerical value associated with priority.
     *
     * @return the numerical value
     */
    public int getValue()
    {
        return m_priority;
    }

    /**
     * Get name of priority.
     *
     * @return the priorities name
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Test whether this priority is greater than other priority.
     *
     * @param other the other Priority
     * @return TRUE if the priority is greater else FALSE
     */
    public boolean isGreater( final Priority other )
    {
        return m_priority > other.getValue();
    }

    /**
     * Test whether this priority is lower than other priority.
     *
     * @param other the other Priority
     * @return TRUE if the priority is lower else FALSE
     */
    public boolean isLower( final Priority other )
    {
        return m_priority < other.getValue();
    }

    /**
     * Test whether this priority is lower or equal to other priority.
     *
     * @param other the other Priority
     * @return TRUE if the priority is lower or equal else FALSE
     */
    public boolean isLowerOrEqual( final Priority other )
    {
        return m_priority <= other.getValue();
    }

    /**
     * Helper method that replaces deserialized object with correct singleton.
     *
     * @return the singleton version of object
     */
    private Object readResolve()
    {
        return getPriorityForName( m_name );
    }
}
