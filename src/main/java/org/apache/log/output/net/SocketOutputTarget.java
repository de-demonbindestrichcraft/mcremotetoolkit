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
package org.apache.log.output.net;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import org.apache.log.LogEvent;
import org.apache.log.output.AbstractOutputTarget;

/**
 * SocketOutputTarget
 *
 * Useful for writing the output to a TCP/IP client socket.
 *
 * @author <a href="mailto:rghorpade@onebridge.de"> Rajendra Ghorpade </a>
 */
public class SocketOutputTarget extends AbstractOutputTarget
{

    /** Socket to communicate with the server */
    private Socket m_socket;

    /** Output strem to write the log */
    private ObjectOutputStream m_outputStream;

    /**
     * Creates output target with the end point  specified by the address and port
     *
     * @param address end point address
     * @param port the end point port
     * @exception IOException if an I/O error ocurrs when creating socket
     */
    public SocketOutputTarget( final InetAddress address,
                               final int port )
        throws IOException
    {
        m_socket = new Socket( address, port );
        m_outputStream = new ObjectOutputStream( m_socket.getOutputStream() );
        super.open();
    }

    /**
     * Creates the output target with the end point specified by host and port
     *
     * @param host end point host
     * @param port the end point port
     * @exception IOException if an I/O error ocurrs when creating socket
     */
    public SocketOutputTarget( final String host,
                               final int port )
        throws IOException
    {
        m_socket = new Socket( host, port );
        m_outputStream = new ObjectOutputStream( m_socket.getOutputStream() );
        super.open();
    }

    /**
     * Writes the output as a LogEvent without formatting.
     * Formatting ia applied on the server side where it is log.
     *
     * @param event the LogEvent
     */
    protected void write( LogEvent event )
    {
        try
        {
            m_outputStream.writeObject( event );
        }
        catch( final IOException ioex )
        {
            getErrorHandler().error( "Error writting to socket", ioex, null );
        }
    }

    /**
     * To process the LogEvent
     *
     * @param event the LogEvent
     */
    protected void doProcessEvent( LogEvent event )
    {
        write( event );
    }

    /**
     * Shutdown target.
     * Attempting to write to target after close() will cause errors to be logged.
     */
    public synchronized void close()
    {
        super.close();
        m_socket = null;
    }
}
