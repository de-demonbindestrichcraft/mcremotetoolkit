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
package org.apache.log.output.io.rotate;

import java.io.File;
import java.io.IOException;
import org.apache.log.format.Formatter;
import org.apache.log.output.io.FileTarget;

/**
 * This is a basic Output log target that writes to rotating files.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @author <a href="mailto:mcconnell@osm.net">Stephen McConnell</a>
 * @author <a href="mailto:bh22351@i-one.at">Bernhard Huber</a>
 */
public class RotatingFileTarget
    extends FileTarget
{
    ///Flag indicating whether or not file should be appended to
    private boolean m_append;

    ///The rotation strategy to be used.
    private RotateStrategy m_rotateStrategy;

    ///The file strategy to be used.
    private FileStrategy m_fileStrategy;

    /**
     * Construct RotatingFileTarget object.
     *
     * @param formatter Formatter to be used
     * @param rotateStrategy RotateStrategy to be used
     * @param fileStrategy FileStrategy to be used
     * @exception IOException if a file access or write related error occurs
     */
    public RotatingFileTarget( final Formatter formatter,
                               final RotateStrategy rotateStrategy,
                               final FileStrategy fileStrategy )
        throws IOException
    {
        this( false, formatter, rotateStrategy, fileStrategy );
    }

    /**
     * Construct RotatingFileTarget object.
     *
     * @param append true if file is to be appended to, false otherwise
     * @param formatter Formatter to be used
     * @param rotateStrategy RotateStrategy to be used
     * @param fileStrategy FileStrategy to be used
     * @exception IOException if a file access or write related error occurs
     */
    public RotatingFileTarget( final boolean append,
                               final Formatter formatter,
                               final RotateStrategy rotateStrategy,
                               final FileStrategy fileStrategy )
        throws IOException
    {
        super( null, append, formatter );

        m_append = append;
        m_rotateStrategy = rotateStrategy;
        m_fileStrategy = fileStrategy;

        rotate();
    }

    /**
     * Rotates the file.
     * @exception IOException if a file access or write related error occurs
     */
    protected synchronized void rotate()
        throws IOException
    {
        close();

        final File file = m_fileStrategy.nextFile();
        setFile( file, m_append );
        openFile();
    }

    /**
     * Output the log message, and check if rotation is needed.
     * @param data the date to write to the target
     */
    protected synchronized void write( final String data )
    {
        // if rotation is needed, close old File, create new File
        if( m_rotateStrategy.isRotationNeeded( data, getFile() ) )
        {
            try
            {
                rotate();

                m_rotateStrategy.reset();
            }
            catch( final IOException ioe )
            {
                getErrorHandler().error( "Error rotating file", ioe, null );
            }
        }

        // write the log message
        super.write( data );
    }
}

