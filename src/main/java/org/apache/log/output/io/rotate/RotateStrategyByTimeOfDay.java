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
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Rotation stragety based on a specific time of day.
 *
 * @author <a href="mailto:leif@apache.org">Leif Mortenson</a>
 */
public class RotateStrategyByTimeOfDay
    implements RotateStrategy
{
    /** Constant that stores the the number of ms in 24 hours. */
    private static final long TIME_24_HOURS = 24 * 3600 * 1000;

    /** Time in ms that the current rotation started. */
    private long m_currentRotation;

    /**
     * Rotate logs at specific time of day.
     * By default do log rotation at 00:00:00 every day.
     */
    public RotateStrategyByTimeOfDay()
    {
        this( 0 );
    }

    /**
     * Rotate logs at specific time of day.
     *
     * @param time Offset in milliseconds into the day to perform the log rotation.
     */
    public RotateStrategyByTimeOfDay( final long time )
    {
        // Calculate the time at the beginning of the current day and add the time to that.
        final GregorianCalendar cal = new GregorianCalendar();
        cal.set( Calendar.MILLISECOND, 0 );
        cal.set( Calendar.SECOND, 0 );
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.HOUR_OF_DAY, 0 );
        m_currentRotation = cal.getTime().getTime() + time;

        // Make sure that the current rotation time is in the past.
        if( m_currentRotation > System.currentTimeMillis() )
        {
            m_currentRotation -= TIME_24_HOURS;
        }
    }

    /**
     * reset interval history counters.
     */
    public void reset()
    {
        final long now = System.currentTimeMillis();

        // Make sure the currentRotation time is set so that the current system
        //  time is within 24 hours.
        while( m_currentRotation + TIME_24_HOURS < now )
        {
            m_currentRotation += TIME_24_HOURS;
        }
    }

    /**
     * Check if now a log rotation is neccessary.
     * If the time of the current rotation + 24 hours is less than the current time.
     *  If not then a rotation is needed.
     *
     * @param data the last message written to the log system
     * @param file not used
     * @return boolean return true if log rotation is neccessary, else false
     */
    public boolean isRotationNeeded( final String data, final File file )
    {
        final long now = System.currentTimeMillis();
        if( m_currentRotation + TIME_24_HOURS < now )
        {
            // Needs to be rotated.
            return true;
        }
        else
        {
            return false;
        }
    }
}

