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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * A basic datasource that doesn't do any pooling but just wraps
 * around default mechanisms.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class DefaultDataSource
    implements DataSource
{
    private final String m_username;
    private final String m_password;
    private final String m_url;

    private PrintWriter m_logWriter;
    private int m_loginTimeout;

    public DefaultDataSource( final String url,
                              final String username,
                              final String password )
    {
        m_url = url;
        m_username = username;
        m_password = password;

        m_logWriter = new PrintWriter( System.err, true );
    }

    /**
     * Attempt to establish a database connection.
     *
     * @return the Connection
     */
    public Connection getConnection()
        throws SQLException
    {
        return getConnection( m_username, m_password );
    }

    /**
     * Attempt to establish a database connection.
     *
     * @return the Connection
     */
    public Connection getConnection( final String username, final String password )
        throws SQLException
    {
        return DriverManager.getConnection( m_url, username, password );
    }

    /**
     * Gets the maximum time in seconds that this data source can wait while
     * attempting to connect to a database.
     *
     * @return the login time
     */
    public int getLoginTimeout()
        throws SQLException
    {
        return m_loginTimeout;
    }

    /**
     * Get the log writer for this data source.
     *
     * @return the LogWriter
     */
    public PrintWriter getLogWriter()
        throws SQLException
    {
        return m_logWriter;
    }

    /**
     * Sets the maximum time in seconds that this data source will wait
     * while attempting to connect to a database.
     *
     * @param loginTimeout the loging timeout in seconds
     */
    public void setLoginTimeout( final int loginTimeout )
        throws SQLException
    {
        m_loginTimeout = loginTimeout;
    }

    public void setLogWriter( final PrintWriter logWriter )
        throws SQLException
    {
        m_logWriter = logWriter;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
