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
package org.apache.log.output.db;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.log.LogEvent;
import org.apache.log.output.AbstractTarget;

/**
 * Abstract JDBC target.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public abstract class AbstractJDBCTarget
    extends AbstractTarget
{
    ///Datasource to extract connections from
    private DataSource m_dataSource;

    ///Database connection
    private Connection m_connection;

    /**
     * Creation of a new instance of the AbstractJDBCTarget.
     * @param dataSource the JDBC datasource
     */
    protected AbstractJDBCTarget( final DataSource dataSource )
    {
        m_dataSource = dataSource;
    }

    /**
     * Process a log event, via formatting and outputting it.
     *
     * @param event the log event
     * @exception Exception if an event processing error occurs
     */
    protected synchronized void doProcessEvent( final LogEvent event )
        throws Exception
    {
        checkConnection();

        if( isOpen() )
        {
            output( event );
        }
    }

    /**
     * Output a log event to DB.
     * This must be implemented by subclasses.
     *
     * @param event the log event.
     */
    protected abstract void output( LogEvent event );

    /**
     * Startup log session.
     *
     */
    protected synchronized void open()
    {
        if( !isOpen() )
        {
            super.open();
            openConnection();
        }
    }

    /**
     * Open connection to underlying database.
     *
     */
    protected synchronized void openConnection()
    {
        try
        {
            m_connection = m_dataSource.getConnection();
        }
        catch( final Throwable throwable )
        {
            getErrorHandler().error( "Unable to open connection", throwable, null );
        }
    }

    /**
     * Utility method for subclasses to access connection.
     *
     * @return the Connection
     */
    protected final synchronized Connection getConnection()
    {
        return m_connection;
    }

    /**
     * Utility method to check connection and bring it back up if necessary.
     */
    protected final synchronized void checkConnection()
    {
        if( isStale() )
        {
            closeConnection();
            openConnection();
        }
    }

    /**
     * Detect if connection is stale and should be reopened.
     *
     * @return true if connection is stale, false otherwise
     */
    protected synchronized boolean isStale()
    {
        if( null == m_connection )
        {
            return true;
        }

        try
        {
            if( m_connection.isClosed() )
            {
                return true;
            }
        }
        catch( final SQLException se )
        {
            return true;
        }

        return false;
    }

    /**
     * Shutdown target.
     * Attempting to write to target after close() will cause errors to be logged.
     *
     */
    public synchronized void close()
    {
        if( isOpen() )
        {
            closeConnection();
            super.close();
        }
    }

    /**
     * Close connection to underlying database.
     *
     */
    protected synchronized void closeConnection()
    {
        if( null != m_connection )
        {
            try
            {
                m_connection.close();
            }
            catch( final SQLException se )
            {
                getErrorHandler().error( "Error shutting down JDBC connection", se, null );
            }

            m_connection = null;
        }
    }
}
