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

import java.util.Stack;

/**
 * The ContextStack records the nested context of an application.
 * The context is an application defined characteristeric. For instance
 * a webserver context may be defined as the session that is currently
 * used to connect to server. A application may have context
 * defined by current thread. A applet may have it's context defined
 * by the name of the applet etc.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @deprecated This class has been deprecated as it encouraged use of bad
 *             design practices. Use org.apache.log.ContextMap instead.
 */
public class ContextStack
{
    ///Thread local for holding instance of stack associated with current thread
    private static final ThreadLocal c_context = new ThreadLocal();

    ///Container to hold stack of elements
    private Stack m_stack = new Stack();

    /**
     * Get the Current ContextStack.
     * This method returns a ContextStack associated with current thread. If the
     * thread doesn't have a ContextStack associated with it then a new
     * ContextStack is created with the name of thread as base context.
     *
     * @return the current ContextStack
     */
    public static final ContextStack getCurrentContext()
    {
        return getCurrentContext( true );
    }

    /**
     * Get the Current ContextStack.
     * This method returns a ContextStack associated with current thread.
     * If the thread doesn't have a ContextStack associated with it and
     * autocreate is true then a new ContextStack is created with the name
     * of thread as base context.
     *
     * @param autocreate true if a ContextStack is to be created if it doesn't exist
     * @return the current ContextStack
     */
    static final ContextStack getCurrentContext( final boolean autocreate )
    {
        ContextStack context = (ContextStack)c_context.get();

        if( null == context && autocreate )
        {
            context = new ContextStack();
            context.push( Thread.currentThread().getName() );
            c_context.set( context );
        }

        return context;
    }

    /**
     * Empty the context stack.
     *
     */
    public void clear()
    {
        m_stack.setSize( 0 );
    }

    /**
     * Get the context at a particular depth.
     *
     * @param index the depth of the context to retrieve
     * @return the context
     */
    public Object get( final int index )
    {
        return m_stack.elementAt( index );
    }

    /**
     * Remove a context from top of stack and return it.
     *
     * @return the context that was on top of stack
     */
    public Object pop()
    {
        return m_stack.pop();
    }

    /**
     * Push the context onto top of context stack.
     *
     * @param context the context to place on stack
     */
    public void push( final Object context )
    {
        m_stack.push( context );
    }

    /**
     * Set the current ContextSet to be equl to other ContextStack.
     *
     * @param stack the value to copy
     */
    public void set( final ContextStack stack )
    {
        clear();
        final int size = stack.m_stack.size();

        for( int i = 0; i < size; i++ )
        {
            m_stack.push( stack.m_stack.elementAt( i ) );
        }
    }

    /**
     * Get the number of contexts in stack.
     *
     * @return the number of contexts in stack
     */
    public int getSize()
    {
        return m_stack.size();
    }

    /**
     * Format context stack into a string.
     * Each element in stack is printed out, separated by a '.' character.
     *
     * @return the string describing context stack
     */
    public String toString()
    {
        return toString( getSize() );
    }

    /**
     * Format context stack into a string.
     * Only write a maximum of count elements, separated by '.' separator.
     * Note that elements in stack will have toString() called and every occurence
     * of spearator character '.' replaced with '_'.
     *
     * @return the string describing context stack
     */
    public String toString( final int count )
    {
        final StringBuffer sb = new StringBuffer();

        final int end = getSize() - 1;
        final int start = Math.max( end - count + 1, 0 );

        for( int i = start; i < end; i++ )
        {
            sb.append( fix( get( i ).toString() ) );
            sb.append( '.' );
        }

        sb.append( fix( get( end ).toString() ) );

        return sb.toString();
    }

    /**
     * Correct a context string by replacing separators '.' with a '_'.
     *
     * @param context the un-fixed context
     * @return the fixed context
     */
    private String fix( final String context )
    {
        return context.replace( '.', '_' );
    }
}
