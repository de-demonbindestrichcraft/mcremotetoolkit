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

import org.apache.log.ErrorAware;
import org.apache.log.ErrorHandler;
import org.apache.log.LogEvent;
import org.apache.log.LogTarget;
import org.apache.log.util.Closeable;
import org.apache.log.util.DefaultErrorHandler;

/**
 * Abstract target.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public abstract class AbstractTarget
    implements LogTarget, ErrorAware, Closeable
{
    private static final ErrorHandler DEFAULT_ERROR_HANDLER = new DefaultErrorHandler();

    ///ErrorHandler used by target to delegate Error handling
    private ErrorHandler m_errorHandler = DEFAULT_ERROR_HANDLER;

    ///Flag indicating that log session is finished (aka target has been closed)
    private boolean m_isOpen;

    /**
     * AbstractTarget constructor.
     */
    public AbstractTarget()
    {
    }

    /**
     * AbstractTarget constructor.
     * @param errorHandler the error handler
     */
    public AbstractTarget( final ErrorHandler errorHandler )
    {
        if( errorHandler == null )
        {
            throw new NullPointerException( "errorHandler specified cannot be null" );
        }
        setErrorHandler( errorHandler );
    }

    /**
     * Provide component with ErrorHandler.
     *
     * @param errorHandler the errorHandler
     */
    public synchronized void setErrorHandler( final ErrorHandler errorHandler )
    {
        m_errorHandler = errorHandler;
    }

    /**
     * Return the open state of the target.
     * @return TRUE if the target is open else FALSE
     */
    protected synchronized boolean isOpen()
    {
        return m_isOpen;
    }

    /**
     * Startup log session.
     */
    protected synchronized void open()
    {
        if( !isOpen() )
        {
            m_isOpen = true;
        }
    }

    /**
     * Process a log event, via formatting and outputting it.
     *
     * @param event the log event
     */
    public synchronized void processEvent( final LogEvent event )
    {
        if( !isOpen() )
        {
            getErrorHandler().error( "Writing event to closed stream.", null, event );
            return;
        }

        try
        {
            doProcessEvent( event );
        }
        catch( final Throwable throwable )
        {
            getErrorHandler().error( "Unknown error writing event.", throwable, event );
        }
    }

    /**
     * Process a log event, via formatting and outputting it.
     * This should be overidden by subclasses.
     *
     * @param event the log event
     * @exception Exception if an event processing error occurs
     */
    protected abstract void doProcessEvent( LogEvent event )
        throws Exception;

    /**
     * Shutdown target.
     * Attempting to write to target after close() will cause errors to be logged.
     *
     */
    public synchronized void close()
    {
        if( isOpen() )
        {
            m_isOpen = false;
        }
    }

    /**
     * Helper method to retrieve ErrorHandler for subclasses.
     *
     * @return the ErrorHandler
     */
    protected final ErrorHandler getErrorHandler()
    {
        return m_errorHandler;
    }

    /**
     * Helper method to write error messages to error handler.
     *
     * @param message the error message
     * @param throwable the exception if any
     * @deprecated Use getErrorHandler().error(...) directly
     */
    protected final void error( final String message, final Throwable throwable )
    {
        getErrorHandler().error( message, throwable, null );
    }
}
