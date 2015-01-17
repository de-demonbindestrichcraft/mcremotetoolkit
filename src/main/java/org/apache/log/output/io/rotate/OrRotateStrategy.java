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

/**
 * Hierarchical rotation strategy.
 * This object is initialised with several rotation strategy objects.
 * The <code>isRotationNeeded</code> method checks the first rotation
 * strategy object. If a rotation is needed, this result is returned.
 * If not, the next rotation strategy object is checked, and so on.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 */
public class OrRotateStrategy
    implements RotateStrategy
{
    private RotateStrategy[] m_strategies;

    /** The rotation strategy used. This marker is required for the reset()
     *  method.
     */
    private int m_usedRotation = -1;

    /**
     * Constructor
     * @param strategies the set of rotation strategies
     */
    public OrRotateStrategy( final RotateStrategy[] strategies )
    {
        this.m_strategies = strategies;
    }

    /**
     * reset.
     */
    public void reset()
    {
        for( int i = 0; i < m_strategies.length; i++ )
        {
            m_strategies[ i ].reset();
        }
    }

    /**
     *  check if now a log rotation is neccessary.
     *  This object is initialised with several rotation strategy objects.
     *  The <code>isRotationNeeded</code> method checks the first rotation
     *  strategy object. If a rotation is needed, this result is returned.
     *  If not the next rotation strategy object is asked and so on.
     *  @param data the last message written to the log system
     *  @param file ???
     *  @return boolean return true if log rotation is neccessary, else false
     */
    public boolean isRotationNeeded( final String data, final File file )
    {
        m_usedRotation = -1;

        if( null != m_strategies )
        {
            final int length = m_strategies.length;
            for( int i = 0; i < length; i++ )
            {
                if( true == m_strategies[ i ].isRotationNeeded( data, file ) )
                {
                    m_usedRotation = i;
                    return true;
                }
            }
        }

        return false;
    }
}

