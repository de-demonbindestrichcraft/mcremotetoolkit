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
package org.apache.log;

import org.apache.log.format.PatternFormatter;
import org.apache.log.output.io.StreamTarget;
import org.apache.log.util.DefaultErrorHandler;
import org.apache.log.util.LoggerListener;

/**
 * This class encapsulates a basic independent log hierarchy.
 * The hierarchy is essentially a safe wrapper around root logger.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class Hierarchy
{
    ///Format of default formatter
    public static final String DEFAULT_FORMAT =
        "%7.7{priority} %23.23{time:yyyy-MM-dd' 'HH:mm:ss.SSS} " +
        "[%8.8{category}] (%{context}): %{message}\n%{throwable}";

    ///The instance of default hierarchy
    private static final Hierarchy c_hierarchy = new Hierarchy();

    ///Error Handler associated with hierarchy
    private ErrorHandler m_errorHandler;

    ///The root logger which contains all Loggers in this hierarchy
    private Logger m_rootLogger;

    ///LoggerListener associated with hierarchy
    private LoggerListener m_loggerListener;

    /**
     * Retrieve the default hierarchy.
     *
     * <p>In most cases the default LogHierarchy is the only
     * one used in an application. However when security is
     * a concern or multiple independent applications will
     * be running in same JVM it is advantageous to create
     * new Hierarchies rather than reuse default.</p>
     *
     * @return the default Hierarchy
     */
    public static Hierarchy getDefaultHierarchy()
    {
        return c_hierarchy;
    }

    /**
     * Create a hierarchy object.
     * The default LogTarget writes to stdout.
     */
    public Hierarchy()
    {
        m_errorHandler = new DefaultErrorHandler();
        m_rootLogger = new Logger( new InnerErrorHandler(),
                                   new InnerLoggerListener(),
                                   "", null, null );

        //Setup default output target to print to console
        final PatternFormatter formatter = new PatternFormatter( DEFAULT_FORMAT );
        final StreamTarget target = new StreamTarget( System.out, formatter );

        setDefaultLogTarget( target );
    }

    /**
     * Set the default log target for hierarchy.
     * This is the target inherited by loggers if no other target is specified.
     *
     * @param target the default target
     */
    public void setDefaultLogTarget( final LogTarget target )
    {
        if( null == target )
        {
            throw new IllegalArgumentException( "Can not set DefaultLogTarget to null" );
        }

        final LogTarget[] targets = new LogTarget[]{target};
        getRootLogger().setLogTargets( targets );
    }

    /**
     * Set the default log targets for this hierarchy.
     * These are the targets inherited by loggers if no other targets are specified
     *
     * @param targets the default targets
     */
    public void setDefaultLogTargets( final LogTarget[] targets )
    {
        if( null == targets || 0 == targets.length )
        {
            throw new IllegalArgumentException( "Can not set DefaultLogTargets to null" );
        }

        for( int i = 0; i < targets.length; i++ )
        {
            if( null == targets[ i ] )
            {
                final String message = "Can not set DefaultLogTarget element to null";
                throw new IllegalArgumentException( message );
            }
        }

        getRootLogger().setLogTargets( targets );
    }

    /**
     * Set the default priority for hierarchy.
     * This is the priority inherited by loggers if no other priority is specified.
     *
     * @param priority the default priority
     */
    public void setDefaultPriority( final Priority priority )
    {
        if( null == priority )
        {
            final String message = "Can not set default Hierarchy Priority to null";
            throw new IllegalArgumentException( message );
        }

        getRootLogger().setPriority( priority );
    }

    /**
     * Set the ErrorHandler associated with hierarchy.
     *
     * @param errorHandler the ErrorHandler
     */
    public void setErrorHandler( final ErrorHandler errorHandler )
    {
        if( null == errorHandler )
        {
            final String message = "Can not set default Hierarchy ErrorHandler to null";
            throw new IllegalArgumentException( message );
        }

        m_errorHandler = errorHandler;
    }

    /**
     * Set the LoggerListener associated with hierarchy.  This is a
     * unicast listener, so only one LoggerListener is allowed.
     *
     * @param loggerListener the LoggerListener
     *
     * @throws UnsupportedOperationException if no more LoggerListeners are
     *         permitted.
     */
    public synchronized void addLoggerListener( final LoggerListener loggerListener )
    {
        if( null == loggerListener )
        {
            throw new NullPointerException( "loggerListener" );
        }

        if( null == m_loggerListener )
        {
            m_loggerListener = loggerListener;
        }
        else
        {
            final String message = "LoggerListener already set on a unicast event notifier";
            throw new UnsupportedOperationException( message );
        }
    }

    /**
     * Remove the LoggerListener associated with hierarchy.  Perform this
     * step before adding a new one if you want to change it.
     *
     * @param loggerListener the LoggerListener
     */
    public synchronized void removeLoggerListener( final LoggerListener loggerListener )
    {
        if( null == loggerListener )
        {
            throw new NullPointerException( "loggerListener" );
        }

        if( null != m_loggerListener && m_loggerListener == loggerListener ) ;
        {
            m_loggerListener = null;
        }
    }

    /**
     * Retrieve a logger for named category.
     *
     * @param category the context
     * @return the Logger
     */
    public Logger getLoggerFor( final String category )
    {
        return getRootLogger().getChildLogger( category );
    }

    /**
     * Logs an error message to error handler.
     * Default Error Handler is stderr.
     *
     * @param message a message to log
     * @param throwable a Throwable to log
     * @deprecated Logging components should use ErrorHandler rather than Hierarchy.log()
     */
    public void log( final String message, final Throwable throwable )
    {
        m_errorHandler.error( message, throwable, null );
    }

    /**
     * Logs an error message to error handler.
     * Default Error Handler is stderr.
     *
     * @param message a message to log
     * @deprecated Logging components should use ErrorHandler rather than Hierarchy.log()
     */
    public void log( final String message )
    {
        m_errorHandler.error( message, null, null );
    }

    /**
     * Notify logger listener (if any) that a new logger was created.
     *
     * @param category the category of new logger
     * @param logger the logger
     */
    private synchronized void notifyLoggerCreated( final String category,
                                                   final Logger logger )
    {
        if( null != m_loggerListener )
        {
            m_loggerListener.loggerCreated( category, logger );
        }
    }

    /**
     * Inner class to redirect to hierarchys real LoggerListener if any.
     * Used so that all the loggers will not have to be updated
     * when LoggerListener changes.
     */
    private class InnerLoggerListener
        extends LoggerListener
    {
        /**
         * Notify listener that a logger was created.
         *
         * @param category the category of logger
         * @param logger the logger object
         */
        public void loggerCreated( final String category,
                                   final Logger logger )
        {
            notifyLoggerCreated( category, logger );
        }
    }

    private class InnerErrorHandler
        implements ErrorHandler
    {
        /**
         * Log an unrecoverable error.
         *
         * @param message the error message
         * @param throwable the exception associated with error (may be null)
         * @param event the LogEvent that caused error, if any (may be null)
         */
        public void error( final String message,
                           final Throwable throwable,
                           final LogEvent event )
        {
            m_errorHandler.error( message, throwable, event );
        }
    }

    /**
     * Utility method to retrieve logger for hierarchy.
     * This method is intended for use by sub-classes
     * which can take responsibility for manipulating
     * Logger directly.
     *
     * @return the Logger
     */
    public final Logger getRootLogger()
    {
        return m_rootLogger;
    }
}
