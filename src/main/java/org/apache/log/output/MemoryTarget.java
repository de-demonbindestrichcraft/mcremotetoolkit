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

import org.apache.log.LogEvent;
import org.apache.log.LogTarget;
import org.apache.log.Priority;

/**
 * Output LogEvents into an buffer in memory.
 * At a later stage these LogEvents can be forwarded or
 * pushed to another target. This pushing is triggered
 * when buffer is full, the priority of a LogEvent reaches a threshold
 * or when another class calls the push method.
 *
 * This is based on specification of MemoryHandler in Logging JSR47.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class MemoryTarget
    extends AbstractTarget
{
    ///Buffer for all the LogEvents
    private final LogEvent[] m_buffer;

    ///Priority at which to push LogEvents to next LogTarget
    private Priority m_threshold;

    ///Target to push LogEvents to
    private LogTarget m_target;

    ///Count of used events
    private int m_used;

    ///Position of last element inserted
    private int m_index;

    ///Flag indicating whether it is possible to overite elements in array
    private boolean m_overwrite;

    /**
     * Creation of a new instance of the memory target.
     * @param target the target to push LogEvents to
     * @param size the event buffer size
     * @param threshold the priority at which to push LogEvents to next LogTarget
     */
    public MemoryTarget( final LogTarget target,
                         final int size,
                         final Priority threshold )
    {
        m_target = target;
        m_buffer = new LogEvent[ size ];
        m_threshold = threshold;
        open();
    }

    /**
     * Set flag indicating whether it is valid to overwrite memory buffer.
     *
     * @param overwrite true if buffer should overwrite logevents in buffer, false otherwise
     */
    protected synchronized void setOverwrite( final boolean overwrite )
    {
        m_overwrite = overwrite;
    }

    /**
     * Process a log event, via formatting and outputting it.
     *
     * @param event the log event
     */
    protected synchronized void doProcessEvent( final LogEvent event )
    {
        //Check if it is full
        if( isFull() )
        {
            if( m_overwrite )
            {
                m_used--;
            }
            else
            {
                getErrorHandler().error( "Memory buffer is full", null, event );
                return;
            }
        }

        if( 0 == m_used )
        {
            m_index = 0;
        }
        else
        {
            m_index = ( m_index + 1 ) % m_buffer.length;
        }
        m_buffer[ m_index ] = event;
        m_used++;

        if( shouldPush( event ) )
        {
            push();
        }
    }

    /**
     * Check if memory buffer is full.
     *
     * @return true if buffer is full, false otherwise
     */
    public final synchronized boolean isFull()
    {
        return m_buffer.length == m_used;
    }

    /**
     * Determine if LogEvent should initiate a push to target.
     * Subclasses can overide this method to change the conditions
     * under which a push occurs.
     *
     * @param event the incoming LogEvent
     * @return true if should push, false otherwise
     */
    protected synchronized boolean shouldPush( final LogEvent event )
    {
        return ( m_threshold.isLowerOrEqual( event.getPriority() ) || isFull() );
    }

    /**
     * Push log events to target.
     */
    public synchronized void push()
    {
        if( null == m_target )
        {
            getErrorHandler().error( "Can not push events to a null target", null, null );
            return;
        }

        try
        {
            final int size = m_used;
            int base = m_index - m_used + 1;
            if( base < 0 )
            {
                base += m_buffer.length;
            }

            for( int i = 0; i < size; i++ )
            {
                final int index = ( base + i ) % m_buffer.length;

                //process event in buffer
                m_target.processEvent( m_buffer[ index ] );

                //help GC
                m_buffer[ index ] = null;
                m_used--;
            }
        }
        catch( final Throwable throwable )
        {
            getErrorHandler().error( "Unknown error pushing events.", throwable, null );
        }
    }
}
