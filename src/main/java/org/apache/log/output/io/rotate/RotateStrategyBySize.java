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
 * Rotation strategy based on size written to log file.
 * The strategy will signal that a rotation is needed if the
 * size goes above a set limit. Due to performance reasons
 * the limit is not strictly enforced, however, the strategy has
 * at most an error of the longest single data message written to the
 * logging system. The error will occur immediately after a rotation,
 * when the strategy is reset and the data that triggered the
 * rotation is written. The strategy's internal counter will then
 * be off with data.length() bytes.
 *
 * @author <a href="mailto:leo.sutic@inspireinfrastructure.com">Leo Sutic</a>
 * @author <a href="mailto:bh22351@i-one.at">Bernhard Huber</a>
 */
public class RotateStrategyBySize
    implements RotateStrategy
{
    private long m_maxSize;
    private long m_currentSize;

    /**
     * Rotate logs by size.
     * By default do log rotation before writing approx. 1MB of messages
     */
    public RotateStrategyBySize()
    {
        this( 1024 * 1024 );
    }

    /**
     *  Rotate logs by size.
     *
     *  @param maxSize rotate before writing maxSize [byte] of messages
     */
    public RotateStrategyBySize( final long maxSize )
    {
        m_currentSize = 0;
        m_maxSize = maxSize;
    }

    /**
     * Reset log size written so far.
     */
    public void reset()
    {
        m_currentSize = 0;
    }

    /**
     *  Check if now a log rotation is neccessary.
     *
     *  @param data the message about to be written to the log system
     *  @return boolean return true if log rotation is neccessary, else false
     *  @param file not used
     */
    public boolean isRotationNeeded( final String data, final File file )
    {
        m_currentSize += data.length();

        return m_currentSize >= m_maxSize;
    }
}

