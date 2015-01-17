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

import java.io.Serializable;
import java.util.Hashtable;

/**
 * The ContextMap contains non-hierarchical context information
 * relevant to a particular LogEvent. It may include information
 * such as;
 *
 * <ul>
 * <li>user      -&gt;fred</li>
 * <li>hostname  -&gt;helm.realityforge.org</li>
 * <li>ipaddress -&gt;1.2.3.4</li>
 * <li>interface -&gt;127.0.0.1</li>
 * <li>caller    -&gt;com.biz.MyCaller.method(MyCaller.java:18)</li>
 * <li>source    -&gt;1.6.3.2:33</li>
 * </ul>
 * The context is bound to a thread (and inherited by sub-threads) but
 * it can also be added to by LogTargets.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public final class ContextMap
    implements Serializable
{
    ///Thread local for holding instance of map associated with current thread
    private static final ThreadLocal c_localContext = new InheritableThreadLocal();

    private final ContextMap m_parent;

    ///Container to hold map of elements
    private Hashtable m_map = new Hashtable();

    ///Flag indicating whether this map should be readonly
    private transient boolean m_readOnly;

    /**
     * Get the Current ContextMap.
     * This method returns a ContextMap associated with current thread. If the
     * thread doesn't have a ContextMap associated with it then a new
     * ContextMap is created.
     *
     * @return the current ContextMap
     */
    public static final ContextMap getCurrentContext()
    {
        return getCurrentContext( true );
    }

    /**
     * Get the Current ContextMap.
     * This method returns a ContextMap associated with current thread.
     * If the thread doesn't have a ContextMap associated with it and
     * autocreate is true then a new ContextMap is created.
     *
     * @param autocreate true if a ContextMap is to be created if it doesn't exist
     * @return the current ContextMap
     */
    public static final ContextMap getCurrentContext( final boolean autocreate )
    {
        //Check security permission here???
        ContextMap context = (ContextMap)c_localContext.get();

        if( null == context && autocreate )
        {
            context = new ContextMap();
            c_localContext.set( context );
        }

        return context;
    }

    /**
     * Bind a particular ContextMap to current thread.
     *
     * @param context the context map (may be null)
     */
    public static final void bind( final ContextMap context )
    {
        //Check security permission here??
        c_localContext.set( context );
    }

    /**
     * Default constructor.
     */
    public ContextMap()
    {
        this( null );
    }

    /**
     * Constructor that sets parent contextMap.
     *
     * @param parent the parent ContextMap
     */
    public ContextMap( final ContextMap parent )
    {
        m_parent = parent;
    }

    /**
     * Make the context read-only.
     * This makes it safe to allow untrusted code reference
     * to ContextMap.
     */
    public void makeReadOnly()
    {
        m_readOnly = true;
    }

    /**
     * Determine if context is read-only.
     *
     * @return true if Context is read only, false otherwise
     */
    public boolean isReadOnly()
    {
        return m_readOnly;
    }

    /**
     * Empty the context map.
     *
     */
    public void clear()
    {
        checkReadable();

        m_map.clear();
    }

    /**
     * Get an entry from the context.
     *
     * @param key the key to map
     * @param defaultObject a default object to return if key does not exist
     * @return the object in context
     */
    public Object get( final String key, final Object defaultObject )
    {
        final Object object = get( key );

        if( null != object )
        {
            return object;
        }
        else
        {
            return defaultObject;
        }
    }

    /**
     * Get an entry from the context.
     *
     * @param key the key to map
     * @return the object in context or null if none with specified key
     */
    public Object get( final String key )
    {
        final Object result = m_map.get( key );

        if( null == result && null != m_parent )
        {
            return m_parent.get( key );
        }

        return result;
    }

    /**
     * Set a value in context
     *
     * @param key the key
     * @param value the value (may be null)
     */
    public void set( final String key, final Object value )
    {
        checkReadable();

        if( value == null )
        {
            m_map.remove( key );
        }
        else
        {
            m_map.put( key, value );
        }
    }

    /**
     * Retrieve keys of entries into context map.
     *
     * @return the keys of items in context
     */
    /*
    public String[] getKeys()
    {
        return (String[])m_map.keySet().toArray( new String[ 0 ] );
    }
    */

    /**
     * Get the number of contexts in map.
     *
     * @return the number of contexts in map
     */
    public int getSize()
    {
        return m_map.size();
    }

    /**
     * Helper method that sets context to read-only after de-serialization.
     *
     * @return the corrected object version
     */
    private Object readResolve()
    {
        makeReadOnly();
        return this;
    }

    /**
     * Utility method to verify that Context is read-only.
     */
    private void checkReadable()
    {
        if( isReadOnly() )
        {
            throw new IllegalStateException( "ContextMap is read only and can not be modified" );
        }
    }
}
