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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.apache.log.format.Formatter;
import org.apache.log.format.PatternFormatter;
import org.apache.log.output.io.WriterTarget;

/**
 * This is a basic Output log target that writes to a stream.
 * The format is specified via a string.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @deprecated Use org.apache.log.output.io.WriterTarget or
 *             org.apache.log.output.io.StreamTarget as appropriate
 *             as this class encourages unsafe behaviour
 */
public class DefaultOutputLogTarget
    extends WriterTarget
{
    private static final String FORMAT =
        "%7.7{priority} %5.5{time}   [%8.8{category}] (%{context}): %{message}\\n%{throwable}";

    /**
     * Initialize the default pattern.
     *
     * @deprecated This is no longer the recomended way to set formatter. It is recomended
     *             that it be passed into constructor.
     */
    protected void initPattern()
    {
    }

    public DefaultOutputLogTarget( final Formatter formatter )
    {
        this( new OutputStreamWriter( System.out ), formatter );
    }

    /**
     * Default Constructor.
     *
     */
    public DefaultOutputLogTarget()
    {
        this( new OutputStreamWriter( System.out ) );
    }

    /**
     * Constructor that takes a stream arguement.
     *
     * @param output the output stream
     */
    public DefaultOutputLogTarget( final OutputStream output )
    {
        this( new OutputStreamWriter( output ) );
    }

    /**
     * Constructor that takes a writer parameter.
     *
     * @param writer the Writer
     */
    public DefaultOutputLogTarget( final Writer writer )
    {
        this( writer, new PatternFormatter( FORMAT ) );
    }

    public DefaultOutputLogTarget( final Writer writer, final Formatter formatter )
    {
        super( writer, formatter );
        initPattern();
    }

    /**
     * Set the format string for this target.
     *
     * @param format the format string
     * @deprecated This method is unsafe as it assumes formatter is PatternFormatter
     *             and accesses a protected attribute. Instead of calling this method
     *             It is recomended that a fully configured formatter is passed into
     *             constructor.
     */
    public void setFormat( final String format )
    {
        ( (PatternFormatter)m_formatter ).setFormat( format );
    }
}
