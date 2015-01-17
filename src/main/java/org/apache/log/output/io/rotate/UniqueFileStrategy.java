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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Strategy for naming log files based on appending time suffix.
 * A file name can be based on simply appending the number of miliseconds
 * since (not really sure) 1/1/1970.
 * Other constructors accept a pattern of a <code>SimpleDateFormat</code>
 * to form the appended string to the base file name as well as a suffix
 * which should be appended last.
 *
 * A <code>new UniqueFileStrategy( new File( "foo." ), "yyyy-MM-dd", ".log" )</code>
 * object will return <code>File</code> objects with file names like
 * <code>foo.2001-12-24.log</code>
 *
 * @author <a href="mailto:bh22351@i-one.at">Bernhard Huber</a>
 * @author <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 */
public class UniqueFileStrategy
    implements FileStrategy
{
    private File m_baseFile;

    private SimpleDateFormat m_formatter;

    private String m_suffix;

    /**
     * Creation of a new Unique File Strategy ??
     * @param baseFile the base file
     */
    public UniqueFileStrategy( final File baseFile )
    {
        m_baseFile = baseFile;
    }

    /**
     * Creation of a new Unique File Strategy ??
     * @param baseFile the base file
     * @param pattern the format pattern
     */
    public UniqueFileStrategy( final File baseFile, String pattern )
    {
        this( baseFile );
        m_formatter = new SimpleDateFormat( pattern );
    }

    /**
     * Creation of a new Unique File Strategy ??
     * @param baseFile the base file
     * @param pattern the format pattern
     * @param suffix the suffix ??
     */
    public UniqueFileStrategy( final File baseFile, String pattern, String suffix )
    {
        this( baseFile, pattern );
        m_suffix = suffix;
    }

    /**
     * Calculate the real file name from the base filename.
     *
     * @return File the calculated file name
     */
    public File nextFile()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append( m_baseFile );
        if( m_formatter == null )
        {
            sb.append( System.currentTimeMillis() );
        }
        else
        {
            final String dateString = m_formatter.format( new Date() );
            sb.append( dateString );
        }

        if( m_suffix != null )
        {
            sb.append( m_suffix );
        }

        return new File( sb.toString() );
    }
}

