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

import org.apache.log.LogTarget;
import org.apache.log.util.Closeable;

/**
 * Abstract base class for targets that wrap other targets. The class
 * provides functionality for optionally closing a wrapped target that
 * implements  <code>org.apache.log.util.Closeable</code>.
 *
 * @see org.apache.log.util.Closeable
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public abstract class AbstractWrappingTarget
    extends AbstractTarget
{
    private final boolean m_closeWrapped;
    private final LogTarget m_wrappedLogTarget;

    /**
     * Creation of a new wrapping log target.
     *
     * @param logTarget the underlying target
     * @param closeWrappedTarget boolean flag indicating whether the wrapped log target
     *        should be closed when this target is closed. Note: This flag has no
     *        effect unless the underlying target implements <code>org.apache.log.util.Closeable</code>.
     * @see org.apache.log.util.Closeable
     */
    public AbstractWrappingTarget( final LogTarget logTarget, final boolean closeWrappedTarget )
    {
        m_wrappedLogTarget = logTarget;
        m_closeWrapped = closeWrappedTarget;
    }

    /**
     * Creation of a new wrapping log target. The underlying log target will
     * <b>not</b> be closed when this target is closed.
     *
     * @param logTarget the underlying target
     */
    public AbstractWrappingTarget( final LogTarget logTarget )
    {
        this( logTarget, false );
    }

    public void close()
    {
        super.close();

        if( m_closeWrapped && m_wrappedLogTarget instanceof Closeable )
        {
            ( (Closeable)m_wrappedLogTarget ).close();
        }
    }
}