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
package org.apache.log.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.log.Logger;
import org.apache.log.Priority;

/**
 * Redirect an output stream to a logger.
 * This class is useful to redirect standard output or
 * standard error to a Logger. An example use is
 *
 * <pre>
 * final LoggerOutputStream outputStream =
 *     new LoggerOutputStream( logger, Priority.DEBUG );
 * final PrintStream output = new PrintStream( outputStream, true );
 *
 * System.setOut( output );
 * </pre>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class LoggerOutputStream
    extends OutputStream
{
    ///Logger that we log to
    private final Logger m_logger;

    ///Log level we log to
    private final Priority m_priority;

    ///The buffered output so far
    private final StringBuffer m_output = new StringBuffer();

    ///Flag set to true once stream closed
    private boolean m_closed;

    /**
     * Construct OutputStreamLogger to write to a particular logger at a particular priority.
     *
     * @param logger the logger to write to
     * @param priority the priority at which to log
     */
    public LoggerOutputStream( final Logger logger,
                               final Priority priority )
    {
        m_logger = logger;
        m_priority = priority;
    }

    /**
     * Shutdown stream.
     * @exception IOException if an error occurs while closing the stream
     */
    public void close()
        throws IOException
    {
        flush();
        super.close();
        m_closed = true;
    }

    /**
     * Write a single byte of data to output stream.
     *
     * @param data the byte of data
     * @exception IOException if an error occurs
     */
    public void write( final int data )
        throws IOException
    {
        checkValid();

        //Should we properly convert char using locales etc??
        m_output.append( (char)data );

        if( '\n' == data )
        {
            flush();
        }
    }

    /**
     * Flush data to underlying logger.
     *
     * @exception IOException if an error occurs
     */
    public synchronized void flush()
        throws IOException
    {
        checkValid();

        m_logger.log( m_priority, m_output.toString() );
        m_output.setLength( 0 );
    }

    /**
     * Make sure stream is valid.
     *
     * @exception IOException if an error occurs
     */
    private void checkValid()
        throws IOException
    {
        if( true == m_closed )
        {
            throw new EOFException( "OutputStreamLogger closed" );
        }
    }
}
