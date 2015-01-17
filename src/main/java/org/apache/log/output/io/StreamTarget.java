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
package org.apache.log.output.io;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.log.format.Formatter;
import org.apache.log.output.AbstractOutputTarget;

/**
 * A basic target that writes to an OutputStream.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class StreamTarget
    extends AbstractOutputTarget
{
    /** OutputStream we are writing to. */
    private OutputStream m_outputStream;

    /** The encoding to use when creating byte array for string, may be null. */
    private String m_encoding;

    /**
     * Constructor that writes to a stream and uses a particular formatter.
     *
     * @param outputStream the OutputStream to write to
     * @param formatter the Formatter to use
     * @param encoding Desired encoding to use when writing to the log, null
     *                 implies the default system encoding.
     */
    public StreamTarget( final OutputStream outputStream,
                         final Formatter formatter,
                         final String encoding )
    {
        super( formatter );
        m_encoding = encoding;

        if( null != outputStream )
        {
            setOutputStream( outputStream );
            open();
        }
    }

    /**
     * Constructor that writes to a stream and uses a particular formatter.
     *
     * @param outputStream the OutputStream to write to
     * @param formatter the Formatter to use
     */
    public StreamTarget( final OutputStream outputStream,
                         final Formatter formatter )
    {
        // We can get the default system encoding by calling the following
        //  method, but it is not a standard API so we work around it by
        //  allowing encoding to be null.
        //    sun.io.Converters.getDefaultEncodingName();

        this( outputStream, formatter, null );
    }

    /**
     * Set the output stream.
     * Close down old stream and write tail if appropriate.
     *
     * @param outputStream the new OutputStream
     */
    protected synchronized void setOutputStream( final OutputStream outputStream )
    {
        if( null == outputStream )
        {
            throw new NullPointerException( "outputStream property must not be null" );
        }

        m_outputStream = outputStream;
    }

    /**
     * Abstract method that will output event.
     *
     * @param data the data to be output
     */
    protected synchronized void write( final String data )
    {
        //Cache method local version
        //so that can be replaced in another thread
        final OutputStream outputStream = m_outputStream;

        if( null == outputStream )
        {
            final String message = "Attempted to write data '" + data + "' to Null OutputStream";
            getErrorHandler().error( message, null, null );
            return;
        }

        try
        {
            byte[] bytes;
            if( m_encoding == null )
            {
                bytes = data.getBytes();
            }
            else
            {
                bytes = data.getBytes( m_encoding );
            }
            outputStream.write( bytes );
            outputStream.flush();
        }
        catch( final IOException ioe )
        {
            final String message = "Error writing data '" + data + "' to OutputStream";
            getErrorHandler().error( message, ioe, null );
        }
    }

    /**
     * Shutdown target.
     * Attempting to write to target after close() will cause errors to be logged.
     *
     */
    public synchronized void close()
    {
        super.close();
        shutdownStream();
    }

    /**
     * Shutdown output stream.
     */
    protected synchronized void shutdownStream()
    {
        final OutputStream outputStream = m_outputStream;
        m_outputStream = null;

        try
        {
            if( null != outputStream )
            {
                outputStream.close();
            }
        }
        catch( final IOException ioe )
        {
            getErrorHandler().error( "Error closing OutputStream", ioe, null );
        }
    }
}
