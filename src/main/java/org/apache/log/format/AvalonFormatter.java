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
package org.apache.log.format;

import org.apache.log.LogEvent;

/**
 * This formatter extends ExtendedPatternFormatter so that
 * CascadingExceptions are formatted with all nested exceptions.
 *
 * <ul>
 * <li><code>class</code> : outputs the name of the class that has logged the
 *     message. The optional <code>short</code> subformat removes the
 *     package name. Warning : this pattern works only if formatting occurs in
 *     the same thread as the call to Logger, i.e. it won't work with
 *     <code>AsyncLogTarget</code>.</li>
 * </ul>
 *
 * @deprecated Use <code>org.apache.avalon.framework.logger.AvalonFormatter</code>
 *             instead of this one.
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public class AvalonFormatter
    extends ExtendedPatternFormatter
{
    private static final int TYPE_CLASS = MAX_TYPE + 1;

    private static final String TYPE_CLASS_STR = "class";

    /**
     * The constant defining the default stack depth when
     * none other is specified.
     */
    public static final int DEFAULT_STACK_DEPTH = 8;

    /**
     * The constant defining the default behaviour for printing
     * nested exceptions.
     */
    public static final boolean DEFAULT_PRINT_CASCADING = true;

    //The depth to which stacktraces are printed out
    private final int m_stackDepth;

    //Determines if nested exceptions should be logged
    private final boolean m_printCascading;

    /**
     * Construct the formatter with the specified pattern
     * and which which prints out exceptions to stackDepth of 8.
     *
     * @param pattern The pattern to use to format the log entries
     */
    public AvalonFormatter( final String pattern )
    {
        this( pattern, DEFAULT_STACK_DEPTH, DEFAULT_PRINT_CASCADING );
    }

    /**
     * Construct the formatter with the specified pattern
     * and which which prints out exceptions to stackDepth specified.
     *
     * @param pattern The pattern to use to format the log entries
     * @param stackDepth The depth to which stacktraces are printed out
     * @param printCascading true enables printing of nested exceptions,
     *   false only prints out the outermost exception
     */
    public AvalonFormatter( final String pattern, final int stackDepth,
                            final boolean printCascading )
    {
        super( pattern );
        m_stackDepth = stackDepth;
        m_printCascading = printCascading;
    }

    /**
     * Utility method to format stack trace.
     *
     * @param throwable the throwable instance
     * @param format ancilliary format parameter - allowed to be null
     * @return the formatted string
     */
    protected String getStackTrace( final Throwable throwable, final String format )
    {
        if( null == throwable )
        {
            return "";
        }
        return ExceptionUtil.printStackTrace( throwable, m_stackDepth, m_printCascading );
    }

    /**
     * Retrieve the type-id for a particular string.
     *
     * @param type the string
     * @return the type-id
     */
    protected int getTypeIdFor( final String type )
    {
        if( type.equalsIgnoreCase( TYPE_CLASS_STR ) )
        {
            return TYPE_CLASS;
        }
        else
        {
            return super.getTypeIdFor( type );
        }
    }

    protected String formatPatternRun( LogEvent event, PatternFormatter.PatternRun run )
    {
        switch( run.m_type )
        {
            case TYPE_CLASS:
                return getClassname();
            default:
                return super.formatPatternRun( event, run );
        }
    }

    /**
     * Finds the class that has called Logger.
     */
    private String getClassname()
    {
        return "Unknown-class";
    }
}
