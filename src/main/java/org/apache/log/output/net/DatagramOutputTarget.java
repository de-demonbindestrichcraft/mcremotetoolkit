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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import org.apache.log.format.Formatter;
import org.apache.log.output.AbstractOutputTarget;

/**
 * A datagram output target.
 * Useful for writing using custom protocols or writing to syslog daemons.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class DatagramOutputTarget
    extends AbstractOutputTarget
{
    ///Default encoding of datagram
    private static final String DEFAULT_ENCODING = "US-ASCII";

    ///Socket on which to send datagrams
    private DatagramSocket m_socket;

    ///The encoding to use when creating byte array from string
    private String m_encoding;

    /**
     * Create a output target with end point specified by address and port.
     *
     * @param address the address endpoint
     * @param port the address port
     * @param formatter the message formatter
     * @param encoding the encoding to use when encoding string
     * @exception IOException if an error occurs
     */
    public DatagramOutputTarget( final InetAddress address,
                                 final int port,
                                 final Formatter formatter,
                                 final String encoding )
        throws IOException
    {
        super( formatter );
        m_socket = new DatagramSocket();
        m_socket.connect( address, port );
        m_encoding = encoding;
        open();
    }

    /**
     * Create a output target with end point specified by address and port.
     *
     * @param address the address endpoint
     * @param port the address port
     * @param formatter the message formatter
     * @exception IOException if an error occurs
     */
    public DatagramOutputTarget( final InetAddress address,
                                 final int port,
                                 final Formatter formatter )
        throws IOException
    {
        this( address, port, formatter, DEFAULT_ENCODING );
    }

    /**
     * Create a output target with end point specified by address and port.
     *
     * @param address the address endpoint
     * @param port the address port
     * @exception IOException if an error occurs
     */
    public DatagramOutputTarget( final InetAddress address, final int port )
        throws IOException
    {
        this( address, port, null );
    }

    /**
     * Method to write output to datagram.
     *
     * @param stringData the data to be output
     */
    protected void write( final String stringData )
    {

        try
        {
            final byte[] data = stringData.getBytes( m_encoding );
            final DatagramPacket packet = new DatagramPacket( data, data.length );
            m_socket.send( packet );
        }
        catch( final IOException ioe )
        {
            getErrorHandler().error( "Error sending datagram.", ioe, null );
        }
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
