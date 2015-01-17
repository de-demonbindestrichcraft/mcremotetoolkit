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
 * Rotation stragety based on SimpleDateFormat.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 * @version $Revision: 1.11 $ $Date: 2003/04/17 09:13:48 $
 */
public class RotateStrategyByDate
    implements RotateStrategy
{
    private SimpleDateFormat m_format;
    private Date m_date;
    private String m_current;

    /**
     * Creation of a new rotation strategy based on a date policy.
     */
    public RotateStrategyByDate()
    {
        this( "yyyyMMdd" );
    }

    /**
     * Creation of a new rotation strategy based on a date policy
     * using a supplied pattern.
     * @param pattern the message formatting pattern
     */
    public RotateStrategyByDate( final String pattern )
    {
        m_format = new SimpleDateFormat( pattern );
        m_date = new Date();
        m_current = m_format.format( m_date );
    }

    /**
     * Reset the strategy.
     */
    public void reset()
    {
        m_date.setTime( System.currentTimeMillis() );
        m_current = m_format.format( m_date );
    }

    /**
     * Test is a rotation is required.  Documentation pending ??
     *
     * @param data not used
     * @param file not used
     * @return TRUE if a rotation is required else FALSE
     */
    public boolean isRotationNeeded( final String data, final File file )
    {
        m_date.setTime( System.currentTimeMillis() );
        if( m_current.equals( m_format.format( m_date ) ) )
        {
            return false;
        }
        return true;
    }
}
