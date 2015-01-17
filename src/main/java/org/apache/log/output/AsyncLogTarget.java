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

import java.util.LinkedList;
import org.apache.log.ErrorAware;
import org.apache.log.ErrorHandler;
import org.apache.log.LogEvent;
import org.apache.log.LogTarget;

/**
 * An asynchronous LogTarget that sends entries on in another thread.
 * It is the responsibility of the user of this class to start
 * the thread etc.
 *
 * <pre>
 * LogTarget mySlowTarget = ...;
 * AsyncLogTarget asyncTarget = new AsyncLogTarget( mySlowTarget );
 * Thread thread = new Thread( asyncTarget );
 * thread.setPriority( Thread.MIN_PRIORITY );
 * thread.start();
 *
 * logger.setLogTargets( new LogTarget[] { asyncTarget } );
 * </pre>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class AsyncLogTarget
    extends AbstractWrappingTarget
    implements Runnable
{
    private final LinkedList m_list;
    private final int m_queueSize;
    private final LogTarget m_logTarget;

    /**
     * Creation of a new async log target.
     * @param logTarget the underlying target
     */
    public AsyncLogTarget( final LogTarget logTarget )
    {
        this( logTarget, 15 );
    }

    /**
     * Creation of a new async log target.
     * @param logTarget the underlying target
     * @param queueSize the queue size
     */
    public AsyncLogTarget( final LogTarget logTarget, final int queueSize )
    {
        this( logTarget, queueSize, false );
    }

    /**
     * Creation of a new async log target.
     * @param logTarget the underlying target
     * @param closeTarget close the underlying target when this target is closed. This flag
     *        has no effect unless the logTarget implements Closeable.
     */
    public AsyncLogTarget( final LogTarget logTarget, final boolean closeTarget )
    {
        this( logTarget, 15, closeTarget );
    }

    /**
     * Creation of a new async log target.
     * @param logTarget the underlying target
     * @param queueSize the queue size
     * @param closeTarget close the underlying target when this target is closed. This flag
     *        has no effect unless the logTarget implements Closeable.
     */
    public AsyncLogTarget( final LogTarget logTarget, final int queueSize, final boolean closeTarget )
    {
        super( logTarget, closeTarget );
        m_logTarget = logTarget;
        m_list = new LinkedList();
        m_queueSize = queueSize;
        open();
    }

    /**
     * Provide component with ErrorHandler.
     *
     * @param errorHandler the errorHandler
     */
    public synchronized void setErrorHandler( final ErrorHandler errorHandler )
    {
        super.setErrorHandler( errorHandler );

        if( m_logTarget instanceof ErrorAware )
        {
            ( (ErrorAware)m_logTarget ).setErrorHandler( errorHandler );
        }
    }

    /**
     * Process a log event by adding it to queue.
     *
     * @param event the log event
     */
    public void doProcessEvent( final LogEvent event )
    {
        synchronized( m_list )
        {
            int size = m_list.size();
            while( m_queueSize <= size )
            {
                try
                {
                    m_list.wait();
                }
                catch( final InterruptedException ie )
                {
                    //This really should not occur ...
                    //Maybe we should log it though for
                    //now lets ignore it
                }
                size = m_list.size();
            }

            m_list.addFirst( event );

            if( size == 0 )
            {
                //tell the "server" thread to wake up
                //if it is waiting for a queue to contain some items
                m_list.notify();
            }
        }
    }

    /**
     * Thread startup.
     */
    public void run()
    {
        //set this variable when thread is interupted
        //so we know we can shutdown thread soon.
        boolean interupted = false;

        while( true )
        {
            LogEvent event = null;

            synchronized( m_list )
            {
                while( null == event )
                {
                    final int size = m_list.size();

                    if( size > 0 )
                    {
                        event = (LogEvent)m_list.removeLast();

                        if( size == m_queueSize )
                        {
                            //tell the "client" thread to wake up
                            //if it is waiting for a queue position to open up
                            m_list.notify();
                        }

                    }
                    else if( interupted || Thread.interrupted() )
                    {
                        //ie there is nothing in queue and thread is interrupted
                        //thus we stop thread
                        return;
                    }
                    else
                    {
                        try
                        {
                            m_list.wait();
                        }
                        catch( final InterruptedException ie )
                        {
                            //Ignore this and let it be dealt in next loop
                            //Need to set variable as the exception throw cleared status
                            interupted = true;
                        }
                    }
                }
            }

            try
            {
                //actually process an event
                m_logTarget.processEvent( event );
            }
            catch( final Throwable throwable )
            {
                getErrorHandler().error( "Unknown error writing event.", throwable, event );
            }
        }
    }
}
