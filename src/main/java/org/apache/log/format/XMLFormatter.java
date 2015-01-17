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

import java.util.Date;
import org.apache.log.LogEvent;

/**
 * Basic XML formatter that writes out a basic XML-ified log event.
 *
 * Note that this formatter assumes that the category and context
 * values will produce strings that do not need to be escaped in XML.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class XMLFormatter
    implements Formatter, org.apache.log.Formatter
{
    private static final String EOL = System.getProperty( "line.separator", "\n" );

    //Booleans indicating whether or not we
    //print out a particular field
    private boolean m_printTime = true;
    private boolean m_printRelativeTime = false;
    private boolean m_printPriority = true;
    private boolean m_printCategory = true;
    private boolean m_printContext = true;
    private boolean m_printMessage = true;
    private boolean m_printException = true;

    private boolean m_printNumericTime = true;

    /**
     * Print out time field to log.
     *
     * @param printTime true to print time, false otherwise
     */
    public void setPrintTime( final boolean printTime )
    {
        m_printTime = printTime;
    }

    /**
     * Print out relativeTime field to log.
     *
     * @param printRelativeTime true to print relativeTime, false otherwise
     */
    public void setPrintRelativeTime( final boolean printRelativeTime )
    {
        m_printRelativeTime = printRelativeTime;
    }

    /**
     * Print out priority field to log.
     *
     * @param printPriority true to print priority, false otherwise
     */
    public void setPrintPriority( final boolean printPriority )
    {
        m_printPriority = printPriority;
    }

    /**
     * Print out category field to log.
     *
     * @param printCategory true to print category, false otherwise
     */
    public void setPrintCategory( final boolean printCategory )
    {
        m_printCategory = printCategory;
    }

    /**
     * Print out context field to log.
     *
     * @param printContext true to print context, false otherwise
     */
    public void setPrintContext( final boolean printContext )
    {
        m_printContext = printContext;
    }

    /**
     * Print out message field to log.
     *
     * @param printMessage true to print message, false otherwise
     */
    public void setPrintMessage( final boolean printMessage )
    {
        m_printMessage = printMessage;
    }

    /**
     * Print out exception field to log.
     *
     * @param printException true to print exception, false otherwise
     */
    public void setPrintException( final boolean printException )
    {
        m_printException = printException;
    }

    /**
     * Format log event into string.
     *
     * @param event the event
     * @return the formatted string
     */
    public String format( final LogEvent event )
    {
        final StringBuffer sb = new StringBuffer( 400 );

        sb.append( "<log-entry>" );
        sb.append( EOL );

        if( m_printTime )
        {
            sb.append( "  <time>" );

            if( m_printNumericTime )
            {
                sb.append( event.getTime() );
            }
            else
            {
                sb.append( new Date( event.getTime() ) );
            }

            sb.append( "</time>" );
            sb.append( EOL );
        }

        if( m_printRelativeTime )
        {
            sb.append( "  <relative-time>" );
            sb.append( event.getRelativeTime() );
            sb.append( "</relative-time>" );
            sb.append( EOL );
        }

        if( m_printPriority )
        {
            sb.append( "  <priority>" );
            sb.append( event.getPriority().getName() );
            sb.append( "</priority>" );
            sb.append( EOL );
        }

        if( m_printCategory )
        {
            sb.append( "  <category>" );
            sb.append( event.getCategory() );
            sb.append( "</category>" );
            sb.append( EOL );
        }

        if( m_printContext && null != event.getContextStack() )
        {
            sb.append( "  <context-stack>" );
            sb.append( event.getContextStack() );
            sb.append( "</context-stack>" );
            sb.append( EOL );
        }

        if( m_printMessage && null != event.getMessage() )
        {
            sb.append( "  <message><![CDATA[" );
            sb.append( event.getMessage() );
            sb.append( "]]></message>" );
            sb.append( EOL );
        }

        if( m_printException && null != event.getThrowable() )
        {
            sb.append( "  <exception><![CDATA[" );
            //sb.append( event.getThrowable() );
            sb.append( "]]></exception>" );
            sb.append( EOL );
        }

        sb.append( "</log-entry>" );
        sb.append( EOL );

        return sb.toString();
    }
}
