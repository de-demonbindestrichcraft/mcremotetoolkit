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

/**
 * A class to hold all constants for ColumnTypes.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class ColumnType
{
    public static final int STATIC = 1;
    public static final int CATEGORY = 2;
    public static final int CONTEXT = 3;
    public static final int MESSAGE = 4;
    public static final int TIME = 5;
    public static final int RELATIVE_TIME = 6;
    public static final int THROWABLE = 7;
    public static final int PRIORITY = 8;
    public static final int HOSTNAME = 9;
    //public static final int     IPADDRESS       = 10;

    /**
     * The maximum value used for TYPEs. Subclasses can define their own TYPEs
     * starting at <code>MAX_TYPE + 1</code>.
     */
    //public static final int     MAX_TYPE        = IPADDRESS;

    public static final String STATIC_STR = "static";
    public static final String CATEGORY_STR = "category";
    public static final String CONTEXT_STR = "context";
    public static final String MESSAGE_STR = "message";
    public static final String TIME_STR = "time";
    public static final String RELATIVE_TIME_STR = "rtime";
    public static final String THROWABLE_STR = "throwable";
    public static final String PRIORITY_STR = "priority";
    public static final String HOSTNAME_STR = "hostname";
    //public static final String  IPADDRESS_STR  = "ipaddress";


    public static int getTypeIdFor( final String type )
    {
        if( type.equalsIgnoreCase( CATEGORY_STR ) )
        {
            return CATEGORY;
        }
        else if( type.equalsIgnoreCase( STATIC_STR ) )
        {
            return STATIC;
        }
        else if( type.equalsIgnoreCase( CONTEXT_STR ) )
        {
            return CONTEXT;
        }
        else if( type.equalsIgnoreCase( MESSAGE_STR ) )
        {
            return MESSAGE;
        }
        else if( type.equalsIgnoreCase( PRIORITY_STR ) )
        {
            return PRIORITY;
        }
        else if( type.equalsIgnoreCase( TIME_STR ) )
        {
            return TIME;
        }
        else if( type.equalsIgnoreCase( RELATIVE_TIME_STR ) )
        {
            return RELATIVE_TIME;
        }
        //else if( type.equalsIgnoreCase( IPADDRESS_STR ) ) return IPADDRESS;
        else if( type.equalsIgnoreCase( HOSTNAME_STR ) )
        {
            return HOSTNAME;
        }
        else if( type.equalsIgnoreCase( THROWABLE_STR ) )
        {
            return THROWABLE;
        }
        else
        {
            throw new IllegalArgumentException( "Unknown Type " + type );
        }
    }
}

