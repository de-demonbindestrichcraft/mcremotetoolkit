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
 * rotation stragety based when log writting started.
 *
 * @author <a href="mailto:bh22351@i-one.at">Bernhard Huber</a>
 */
public class RotateStrategyByTime
    implements RotateStrategy
{
    ///time interval when rotation is triggered.
    private long m_timeInterval;

    ///time when logging started.
    private long m_startingTime;

    ///rotation count.
    private long m_currentRotation;

    /**
     * Rotate logs by time.
     * By default do log rotation every 24 hours
     */
    public RotateStrategyByTime()
    {
        this( 1000 * 60 * 60 * 24 );
    }

    /**
     *  Rotate logs by time.
     *
     *  @param timeInterval rotate before time-interval [ms] has expired
     */
    public RotateStrategyByTime( final long timeInterval )
    {
        m_startingTime = System.currentTimeMillis();
        m_currentRotation = 0;
        m_timeInterval = timeInterval;
    }

    /**
     * reset interval history counters.
     */
    public void reset()
    {
        m_startingTime = System.currentTimeMillis();
        m_currentRotation = 0;
    }

    /**
     * Check if now a log rotation is neccessary.
     * If
     * <code>(current_time - m_startingTime) / m_timeInterval &gt; m_currentRotation </code>
     * rotation is needed.
     *
     * @param data the last message written to the log system
     * @param file not used
     * @return boolean return true if log rotation is neccessary, else false
     */
    public boolean isRotationNeeded( final String data, final File file )
    {
        final long newRotation =
            ( System.currentTimeMillis() - m_startingTime ) / m_timeInterval;

        if( newRotation > m_currentRotation )
        {
            m_currentRotation = newRotation;
            return true;
        }
        else
        {
            return false;
        }
    }
}


