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
import java.io.Writer;
import org.apache.log.format.Formatter;
import org.apache.log.output.AbstractOutputTarget;

/**
 * This target outputs to a writer.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class WriterTarget
    extends AbstractOutputTarget
{
    /**
     * @deprecated Accessing this variable in subclasses is no longer supported
     *             and will become private in the future.
     */
    protected Writer m_output;

    /**
     * Construct target with a specific writer and formatter.
     *
     * @param writer the writer
     * @param formatter the formatter
     */
    public WriterTarget( final Writer writer, final Formatter formatter )
    {
        super( formatter );

        if( null != writer )
        {
            setWriter( writer );
            open();
        }
    }

    /**
     * Set the writer.
     * Close down writer and write tail if appropriate.
     *
     * @param writer the new writer
     */
    protected synchronized void setWriter( final Writer writer )
    {
        if( null == writer )
        {
            throw new NullPointerException( "writer property must not be null" );
        }

        m_output = writer;
    }

    /**
     * Concrete implementation of output that writes out to underlying writer.
     *
     * @param data the data to output
     */
    protected void write( final String data )
    {
        try
        {
            m_output.write( data );
            m_output.flush();
        }
        catch( final IOException ioe )
        {
            getErrorHandler().error( "Caught an IOException", ioe, null );
        }
    }

    /**
     * Shutdown target.
     * Attempting to write to target after close() will cause errors to be logged.
     */
    public synchronized void close()
    {
        super.close();
        shutdownWriter();
    }

    /**
     * Shutdown Writer.
     */
    protected synchronized void shutdownWriter()
    {
        final Writer writer = m_output;
        m_output = null;

        try
        {
            if( null != writer )
            {
                writer.close();
            }
        }
        catch( final IOException ioe )
        {
            getErrorHandler().error( "Error closing Writer", ioe, null );
        }
    }
}
