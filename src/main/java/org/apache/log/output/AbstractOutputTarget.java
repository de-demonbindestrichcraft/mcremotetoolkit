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
package org.apache.log.output;

import org.apache.log.LogEvent;
import org.apache.log.format.Formatter;

/**
 * Abstract output target.
 * Any new output target that is writing to a single connected
 * resource should extend this class directly or indirectly.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public abstract class AbstractOutputTarget
    extends AbstractTarget
{
    /**
     * Formatter for target.
     *
     * @deprecated You should not be directly accessing this attribute
     *             as it will become private next release
     */
    protected Formatter m_formatter;

    /**
     * Parameterless constructor.
     */
    public AbstractOutputTarget()
    {
    }

    /**
     * Creation of a new abstract output target instance.
     * @param formatter the formatter to apply
     */
    public AbstractOutputTarget( final Formatter formatter )
    {
        m_formatter = formatter;
    }

    /**
     * Retrieve the associated formatter.
     *
     * @return the formatter
     * @deprecated Access to formatter is not advised and this method will be removed
     *             in future iterations. It remains only for backwards compatability.
     */
    public synchronized Formatter getFormatter()
    {
        return m_formatter;
    }

    /**
     * Set the formatter.
     *
     * @param formatter the formatter
     * @deprecated In future this method will become protected access.
     */
    public synchronized void setFormatter( final Formatter formatter )
    {
        writeTail();
        m_formatter = formatter;
        writeHead();
    }

    /**
     * Abstract method to write data.
     *
     * @param data the data to be output
     */
    protected void write( final String data )
    {
        output( data );
    }

    /**
     * Abstract method that will output event.
     *
     * @param data the data to be output
     * @deprecated User should overide write() instead of output(). Output exists
     *             for backwards compatability and will be removed in future.
     */
    protected void output( final String data )
    {
    }

    /**
     * Process a log event.
     * @param event the event to process
     */
    protected void doProcessEvent( LogEvent event )
    {
        final String data = format( event );
        write( data );
    }

    /**
     * Startup log session.
     *
     */
    protected synchronized void open()
    {
        if( !isOpen() )
        {
            super.open();
            writeHead();
        }
    }

    /**
     * Shutdown target.
     * Attempting to write to target after close() will cause errors to be logged.
     *
     */
    public synchronized void close()
    {
        if( isOpen() )
        {
            writeTail();
            super.close();
        }
    }

    /**
     * Helper method to format an event into a string, using the formatter if available.
     *
     * @param event the LogEvent
     * @return the formatted string
     */
    private String format( final LogEvent event )
    {
        if( null != m_formatter )
        {
            return m_formatter.format( event );
        }
        else
        {
            return event.toString();
        }
    }

    /**
     * Helper method to write out log head.
     * The head initiates a session of logging.
     */
    private void writeHead()
    {
        if( !isOpen() )
        {
            return;
        }

        final String head = getHead();
        if( null != head )
        {
            write( head );
        }
    }

    /**
     * Helper method to write out log tail.
     * The tail completes a session of logging.
     */
    private void writeTail()
    {
        if( !isOpen() )
        {
            return;
        }

        final String tail = getTail();
        if( null != tail )
        {
            write( tail );
        }
    }

    /**
     * Helper method to retrieve head for log session.
     * TODO: Extract from formatter
     *
     * @return the head string
     */
    private String getHead()
    {
        return null;
    }

    /**
     * Helper method to retrieve tail for log session.
     * TODO: Extract from formatter
     *
     * @return the head string
     */
    private String getTail()
    {
        return null;
    }
}
