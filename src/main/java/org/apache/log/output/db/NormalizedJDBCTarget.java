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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import javax.sql.DataSource;
import org.apache.log.LogEvent;

/**
 * JDBC target that writes to normalized tables.
 * This reduces overhead and cost of querying/storing logs.
 *
 * <p>Parts based on JDBC logger from prottomatter by
 * <a href="mailto:nate@protomatter.com">Nate Sammons</a></p>
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class NormalizedJDBCTarget
    extends DefaultJDBCTarget
{
    private HashMap m_categoryIDs = new HashMap();
    private HashMap m_priorityIDs = new HashMap();

    public NormalizedJDBCTarget( final DataSource dataSource,
                                 final String table,
                                 final ColumnInfo[] columns )
    {
        super( dataSource, table, columns );
    }

    /**
     * Adds a single object into statement.
     */
    protected void specifyColumn( final PreparedStatement statement,
                                  final int index,
                                  final LogEvent event )
        throws SQLException
    {
        final ColumnInfo info = getColumn( index );
        int id = 0;
        String tableName = null;

        switch( info.getType() )
        {
            case ColumnType.CATEGORY:
                tableName = getTable() + "_" + ColumnType.CATEGORY_STR + "_SET";
                id = getID( tableName, m_categoryIDs, event.getCategory() );
                statement.setInt( index + 1, id );
                break;

            case ColumnType.PRIORITY:
                tableName = getTable() + "_" + ColumnType.PRIORITY_STR + "_SET";
                id = getID( tableName, m_priorityIDs, event.getPriority().getName() );
                statement.setInt( index + 1, id );
                break;

            default:
                super.specifyColumn( statement, index, event );
        }
    }

    protected synchronized int getID( final String tableName, final HashMap idMap, final String instance )
        throws SQLException
    {
        final Integer id = (Integer)idMap.get( instance );
        if( null != id ) return id.intValue();

        // see if it's been put in before.
        Statement statement = null;
        ResultSet resultSet = null;

        try
        {
            statement = getConnection().createStatement();

            final String querySql = "SELECT ID FROM " + tableName + " WHERE NAME='" + instance + "'";
            resultSet = statement.executeQuery( querySql );

            if( resultSet.next() )
            {
                final Integer newID = new Integer( resultSet.getInt( 1 ) );
                idMap.put( instance, newID );
                return newID.intValue();
            }

            resultSet.close();

            //Note that the next part should be a transaction but
            //it is not mega vital so ...

            //Find the max id in table and set
            //max to it's value if any items are present in table
            final String maxQuerySql = "SELECT MAX(ID) FROM " + tableName;
            resultSet = statement.executeQuery( maxQuerySql );
            int max = 0;
            if( resultSet.next() ) max = resultSet.getInt( 1 );
            resultSet.close();

            final int newID = max + 1;
            final String insertSQL = "INSERT INTO " + tableName +
                " (ID, NAME) VALUES ( " + newID + ", '" + instance + "')";
            statement.executeUpdate( insertSQL );

            idMap.put( instance, new Integer( newID ) );
            return newID;
        }
        finally
        {
            // close up shop
            if( null != resultSet )
            {
                try
                {
                    resultSet.close();
                }
                catch( final Exception e )
                {
                }
            }
            if( null != statement )
            {
                try
                {
                    statement.close();
                }
                catch( final Exception e )
                {
                }
            }
        }
    }
}
