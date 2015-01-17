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
package org.apache.log.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A set of utilities to inspect current stack frame.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @author <a href="mailto:sylvain@apache.org">Sylvain Wallez</a>
 * @author <a href="mailto:stuart.roebuck@adolos.com">Stuart Roebuck</a>
 * @version CVS $Revision: 1.14 $ $Date: 2003/04/17 09:13:49 $
 */
public final class StackIntrospector
{
    /**
     * Hack to get the call stack as an array of classes. The
     * SecurityManager class provides it as a protected method, so
     * change it to public through a new method !
     */
    private static final class CallStack
        extends SecurityManager
    {
        /**
         * Returns the current execution stack as an array of classes.
         * The length of the array is the number of methods on the execution
         * stack. The element at index 0 is the class of the currently executing
         * method, the element at index 1 is the class of that method's caller,
         * and so on.
         */
        public Class[] get()
        {
            return getClassContext();
        }
    }

    ///Method to cache CallStack hack as needed
    private static CallStack c_callStack;

    /**
     * Private constructor to block instantiation.
     *
     */
    private StackIntrospector()
    {
    }

    /**
     * Create Hack SecurityManager to get CallStack.
     *
     * @return the CallStack object
     * @exception SecurityException if an existing SecurityManager disallows construction
     *            of another SecurityManager
     */
    private static synchronized CallStack getCallStack()
        throws SecurityException
    {
        if( null == c_callStack )
        {
            //Lazily create CallStack accessor as appropriate
            c_callStack = new CallStack();
        }

        return c_callStack;
    }

    /**
     * Find the caller of the passed in Class.
     * May return null if caller not found on execution stack
     *
     * @param clazz the Class to search for on stack to find caller of
     * @return the Class of object that called parrameter class
     * @exception SecurityException if an existing SecurityManager disallows construction
     *            of another SecurityManager and thus blocks method results
     */
    public static final Class getCallerClass( final Class clazz )
        throws SecurityException
    {
        return getCallerClass( clazz, 0 );
    }

    /**
     * Find the caller of the passed in Class.
     * May return null if caller not found on execution stack
     *
     * @param clazz the Class to search for on stack to find caller of
     * @param stackDepthOffset Offset call-stack depth to find caller
     * @return the Class of object that called parrameter class
     * @exception SecurityException if an existing SecurityManager disallows construction
     *            of another SecurityManager and thus blocks method results
     */
    public static final Class getCallerClass( final Class clazz, int stackDepthOffset )
    {
        final Class[] stack = getCallStack().get();

        // Traverse the call stack in reverse order until we find clazz
        for( int i = stack.length - 1; i >= 0; i-- )
        {
            if( clazz.isAssignableFrom( stack[ i ] ) )
            {
                // Found : the caller is the previous stack element
                return stack[ i + 1 + stackDepthOffset ];
            }
        }

        //Unable to locate class in call stack
        return null;
    }

    /**
     * Get the method path name for the method from which the LogEvent was
     * created, this includes the path name and the source filename and line
     * number if the source was compiled with debugging on.
     *
     * @param clazz the Class to search for on stack to find caller of
     * @return The method path name in the form "the.package.path.Method"
     */
    public static final String getCallerMethod( final Class clazz )
    {
        final String className = clazz.getName();

        //Extract stack into a StringBuffer
        final StringWriter sw = new StringWriter();
        final Throwable throwable = new Throwable();
        throwable.printStackTrace( new PrintWriter( sw, true ) );
        final StringBuffer buffer = sw.getBuffer();

        //Cache vars used in loop
        final StringBuffer line = new StringBuffer();
        final int length = buffer.length();

        //setup state
        boolean found = false;
        int state = 0;

        //parse line
        for( int i = 0; i < length; i++ )
        {
            final char ch = buffer.charAt( i );

            switch( state )
            {
                case 0:
                    //Strip the first line from input
                    if( '\n' == ch )
                    {
                        state = 1;
                    }
                    break;

                case 1:
                    //strip 't' from 'at'
                    if( 't' == ch )
                    {
                        state = 2;
                    }
                    break;

                case 2:
                    //Strip space after 'at'
                    line.setLength( 0 );
                    state = 3;
                    break;

                case 3:
                    //accumulate all characters to end of line
                    if( '\n' != ch )
                    {
                        line.append( ch );
                    }
                    else
                    {
                        //At this stage you have the line that looks like
                        //com.biz.SomeClass.someMethod(SomeClass.java:22)
                        final String method = line.toString();

                        ///Determine if line is a match for class
                        final boolean match = method.startsWith( className );
                        if( !found && match )
                        {
                            //If this is the first time we cound class then
                            //set found to true and look for caller into class
                            found = true;
                        }
                        else if( found && !match )
                        {
                            //We have now located caller of Clazz
                            return method;
                        }

                        //start parsing from start of line again
                        state = 1;
                    }
            }
        }

        return "";
    }

    /**
     * Return the current call stack as a String, starting with the first call
     * in the stack after a reference to the <code>clazz</code> class, and then
     * display <code>entries</code> entries.
     *
     * <p>This can be useful for debugging code to determine where calls to a
     * method are coming from.</p>
     *
     * @param clazz the last class on the stack you are <i>not</i> interested in!
     * @param entries the number of stack lines to return.
     *
     * @return The method path name in the form "the.package.path.Method"
     */
    public static final String getRecentStack( final Class clazz, int entries )
    {
        final String className = clazz.getName();

        //Extract stack into a StringBuffer
        final StringWriter sw = new StringWriter();
        final Throwable throwable = new Throwable();
        throwable.printStackTrace( new PrintWriter( sw, true ) );
        final StringBuffer buffer = sw.getBuffer();

        //Cache vars used in loop
        final StringBuffer line = new StringBuffer();
        final StringBuffer stack = new StringBuffer();
        final int length = buffer.length();

        //setup state
        boolean found = false;
        int state = 0;

        //parse line
        for( int i = 0; i < length; i++ )
        {
            final char ch = buffer.charAt( i );

            switch( state )
            {
                case 0:
                    //Strip the first line from input
                    if( '\n' == ch )
                    {
                        state = 1;
                    }
                    break;

                case 1:
                    //strip 't' from 'at'
                    if( 't' == ch )
                    {
                        state = 2;
                    }
                    break;

                case 2:
                    //Strip space after 'at'
                    line.setLength( 0 );
                    state = 3;
                    break;

                case 3:
                    //accumulate all characters to end of line
                    if( '\n' != ch )
                    {
                        line.append( ch );
                    }
                    else
                    {
                        //At this stage you have the line that looks like
                        //com.biz.SomeClass.someMethod(SomeClass.java:22)
                        final String method = line.toString();

                        ///Determine if line is a match for class
                        final boolean match = method.startsWith( className );
                        if( !found && match )
                        {
                            //If this is the first time we cound class then
                            //set found to true and look for caller into class
                            found = true;
                        }
                        else if( found && !match )
                        {
                            //We are looking at the callers of Clazz
                            stack.append( method );
                            entries--;
                            if( entries == 0 )
                            {
                                return stack.toString();
                            }
                            stack.append( "\n" );
                        }

                        //start parsing from start of line again
                        state = 1;
                    }
            }
        }

        return "";
    }
}

