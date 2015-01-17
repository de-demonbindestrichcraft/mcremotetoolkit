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
package org.apache.log.format;

import org.apache.log.ContextMap;
import org.apache.log.LogEvent;
import org.apache.log.Logger;
import org.apache.log.util.StackIntrospector;

/**
 * Formatter especially designed for debugging applications.
 *
 * This formatter extends the standard PatternFormatter to add
 * two new possible expansions. These expansions are %{method}
 * and %{thread}. In both cases the context map is first checked
 * for values with specified key. This is to facilitate passing
 * information about caller/thread when threads change (as in
 * AsyncLogTarget). They then attempt to determine appropriate
 * information dynamically.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.13 $ $Date: 2003/04/17 09:13:46 $
 */
public class ExtendedPatternFormatter
    extends PatternFormatter
{
    private static final int TYPE_METHOD = MAX_TYPE + 1;
    private static final int TYPE_THREAD = MAX_TYPE + 2;

    private static final String TYPE_METHOD_STR = "method";
    private static final String TYPE_THREAD_STR = "thread";

    private int m_callStackOffset = 0;

    /**
     * Creation of a new extended pattern formatter.
     * @param format the format string
     */
    public ExtendedPatternFormatter( final String format )
    {
        this( format, 0 );
    }

    /**
     * Creation of a new extended pattern formatter.
     *
     * @param format the format string
     * @param callStackOffset the offset
     */
    public ExtendedPatternFormatter( final String format, final int callStackOffset )
    {
        super( format );
        m_callStackOffset = callStackOffset;
    }

    /**
     * Retrieve the type-id for a particular string.
     *
     * @param type the string
     * @return the type-id
     */
    protected int getTypeIdFor( final String type )
    {
        if( type.equalsIgnoreCase( TYPE_METHOD_STR ) )
        {
            return TYPE_METHOD;
        }
        else if( type.equalsIgnoreCase( TYPE_THREAD_STR ) )
        {
            return TYPE_THREAD;
        }
        else
        {
            return super.getTypeIdFor( type );
        }
    }

    /**
     * Formats a single pattern run (can be extended in subclasses).
     *
     * @param event the log event
     * @param  run the pattern run to format.
     * @return the formatted result.
     */
    protected String formatPatternRun( final LogEvent event, final PatternRun run )
    {
        switch( run.m_type )
        {
            case TYPE_METHOD:
                return getMethod( event );
            case TYPE_THREAD:
                return getThread( event );
            default:
                return super.formatPatternRun( event, run );
        }
    }

    /**
     * Utility method to format category.
     *
     * @param event the event
     * @return the formatted string
     */
    private String getMethod( final LogEvent event )
    {
        final ContextMap map = event.getContextMap();
        if( null != map )
        {
            final Object object = map.get( "method" );
            if( null != object )
            {
                return object.toString();
            }
        }

        //Determine callee of user's class.  If offset is 0, we need to find
        // Logger.class.  If offset is 1, We need to find caller of Logger.class, etc.
        final Class clazz = StackIntrospector.getCallerClass( Logger.class, m_callStackOffset - 1 );
        if( null == clazz )
        {
            return "UnknownMethod";
        }

        final String result = StackIntrospector.getCallerMethod( clazz );
        if( null == result )
        {
            return "UnknownMethod";
        }
        return result;
    }

    /**
     * Utility thread to format category.
     *
     * @param event the even
     * @return the formatted string
     */
    private String getThread( final LogEvent event )
    {
        final ContextMap map = event.getContextMap();
        if( null != map )
        {
            final Object object = map.get( "thread" );
            if( null != object )
            {
                return object.toString();
            }
        }

        return Thread.currentThread().getName();
    }
}
