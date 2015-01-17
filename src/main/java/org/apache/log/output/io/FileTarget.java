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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.log.format.Formatter;

/**
 * A basic target that writes to a File.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class FileTarget
    extends StreamTarget
{
    ///File we are writing to
    private File m_file;

    ///Flag indicating whether or not file should be appended to
    private boolean m_append;

    /**
     * Construct file target to write to a file with a formatter.
     *
     * @param file the file to write to
     * @param append true if file is to be appended to, false otherwise
     * @param formatter the Formatter
     * @exception IOException if an error occurs
     */
    public FileTarget( final File file, final boolean append, final Formatter formatter )
        throws IOException
    {
        super( null, formatter );

        if( null != file )
        {
            setFile( file, append );
            openFile();
        }
    }

    /**
     * Set the file for this target.
     *
     * @param file the file to write to
     * @param append true if file is to be appended to, false otherwise
     * @exception IOException if directories can not be created or file can not be opened
     */
    protected synchronized void setFile( final File file, final boolean append )
        throws IOException
    {
        if( null == file )
        {
            throw new NullPointerException( "file property must not be null" );
        }

        if( isOpen() )
        {
            throw new IOException( "target must be closed before "
                                   + "file property can be set" );
        }

        m_append = append;
        m_file = file;
    }

    /**
     * Open underlying file and allocate resources.
     * This method will attempt to create directories below file and
     * append to it if specified.
     * @exception IOException if directories can not be created or file can not be opened
     */
    protected synchronized void openFile()
        throws IOException
    {
        if( isOpen() )
        {
            close();
        }

        final File file = getFile().getCanonicalFile();

        final File parent = file.getParentFile();
        if( null != parent && !parent.exists() )
        {
            parent.mkdirs();
        }

        final FileOutputStream outputStream =
            new FileOutputStream( file.getPath(), m_append );

        setOutputStream( outputStream );
        open();
    }

    /**
     * Retrieve file associated with target.
     * This allows subclasses to access file object.
     *
     * @return the output File
     */
    protected synchronized File getFile()
    {
        return m_file;
    }
}
