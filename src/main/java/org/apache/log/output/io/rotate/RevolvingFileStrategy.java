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
package org.apache.log.output.io.rotate;

import java.io.File;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;

/**
 * strategy for naming log files based on appending revolving suffix.
 * If the initial rotation is not specified then the class will attempt to
 * calculate the rotation number via the following algorithm.
 *
 * It will search for the file with the highest number in the rotation. It will
 * then increment its rotation number and use that number. If all files in rotation
 * are present then it will then set the initial rotation to the next rotation after
 * the most recently created file.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @author <a href="mailto:bh22351@i-one.at">Bernhard Huber</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @author <a href="mailto:david.gray@hic.gov.au">David Gray</a>
 */
public class RevolvingFileStrategy
    implements FileStrategy
{
    ///revolving suffix formatting pattern. ie. "'.'000000"
    private static final String PATTERN = "'.'000000";

    ///a revolving suffix formatter
    private DecimalFormat m_decimalFormat;

    ///current revolving suffix
    private int m_rotation;

    ///max revolving value.
    private int m_maxRotations;

    ///the base file name.
    private File m_baseFile;

    /**
     * Creation of a new instane ofthe revolving file strategy.
     * @param baseFile the base file
     * @param maxRotations the maximum number of rotations ??
     */
    public RevolvingFileStrategy( final File baseFile, final int maxRotations )
    {
        this( baseFile, -1, maxRotations );
    }

    /**
     * Creation of a new instane ofthe revolving file strategy.
     * @param baseFile the base file
     * @param initialRotation the number of initial rotations ??
     * @param maxRotations the maximum number of rotations??
     */
    public RevolvingFileStrategy( final File baseFile,
                                  final int initialRotation,
                                  final int maxRotations )
    {
        m_decimalFormat = new DecimalFormat( PATTERN );

        m_baseFile = baseFile;
        m_rotation = initialRotation;
        m_maxRotations = maxRotations;

        if( -1 == m_maxRotations )
        {
            m_maxRotations = Integer.MAX_VALUE;
        }

        if( -1 == initialRotation )
        {
            m_rotation = calculateInitialRotation();
        }

        if( m_rotation > m_maxRotations )
        {
            m_rotation = m_maxRotations;
        }

        if( m_rotation < 0 )
        {
            m_rotation = 0;
        }
    }

    /**
     * Calculate the real file name from the base filename.
     *
     * @return File the calculated file name
     */
    public File nextFile()
    {
        final StringBuffer sb = new StringBuffer();
        final FieldPosition position =
            new FieldPosition( NumberFormat.INTEGER_FIELD );
        sb.append( m_baseFile );

        final StringBuffer result =
            m_decimalFormat.format( m_rotation, sb, position );
        m_rotation += 1;

        if( m_rotation >= m_maxRotations )
        {
            m_rotation = 0;
        }

        return new File( result.toString() );
    }

    /**
     * Retrieve the current rotation number.
     *
     * @return the current rotation number.
     */
    public int getCurrentRotation()
    {
        return m_rotation;
    }

    /**
     * Method that searches through files that
     * match the pattern for resolving file and determine
     * the last generation written to.
     *
     * @return the initial rotation
     */
    private int calculateInitialRotation()
    {
        final File[] matchingFiles = getMatchingFiles();
        if( null == matchingFiles || 0 == matchingFiles.length )
        {
            return 0;
        }

        final int[] rotations = calculateRotations( matchingFiles );

        //First we go through and look for maximumRotation
        int maxRotation = 0;
        for( int i = 0; i < rotations.length; i++ )
        {
            final int rotation = rotations[ i ];
            if( rotation > maxRotation )
            {
                maxRotation = rotation;
            }
        }

        //If the max rotation present on filessytem
        //is less than max rotation possible then return that
        //rotation
        if( m_maxRotations != maxRotation )
        {
            return maxRotation + 1;
        }

        //Okay now we need to calculate the youngest file for our rotation
        long time = matchingFiles[ 0 ].lastModified();

        //index of oldest file
        int oldest = rotations[ 0 ];
        for( int i = 0; i < matchingFiles.length; i++ )
        {
            final File file = matchingFiles[ i ];
            final long lastModified = file.lastModified();
            if( lastModified < time )
            {
                time = lastModified;
                oldest = rotations[ i ];
            }
        }

        return oldest;
    }

    /**
     * Generate an array of rotation numbers for all the files specified.
     *
     * @param matchingFiles the files to generate rotation numbers for
     * @return the array containing rotations
     */
    private int[] calculateRotations( final File[] matchingFiles )
    {
        final int[] results = new int[ matchingFiles.length ];

        for( int i = 0; i < matchingFiles.length; i++ )
        {
            final File file = matchingFiles[ i ];

            // The files may be returned in any order
            //therefore calc the rotation number
            try
            {
                results[ i ] = calculateRotationForFile( file );
            }
            catch( final NumberFormatException nfe )
            {
                //If bad log file detected then set to -1
                results[ i ] = -1;
            }
        }
        return results;
    }

    /**
     * Return the rotation for the specified file
     *
     * @param file the file to check
     * @return the rotation of the file
     */
    private int calculateRotationForFile( final File file )
    {
        final String filename = file.toString();
        final int length = filename.length();
        final int minDigits =
            m_decimalFormat.getMinimumIntegerDigits();
        final String rotation = filename.substring( length - minDigits );

        return Integer.parseInt( rotation );
    }

    /**
     * Get a list of files that could have been part of the rotation.
     *
     * @return the list of files that match
     */
    private File[] getMatchingFiles()
    {
        // First get the path of the base file. Note that this path includes
        // the path and the base of the file name itself.
        final String fullFilePathName = m_baseFile.getPath();

        // Try to find the last path separator (if it exists)
        final int fileSeparatorPosition = fullFilePathName.lastIndexOf( File.separator );

        // If the last path separator does not exist the baseFile is a pure file name
        File basePath;
        String baseFileName;
        if( fileSeparatorPosition < 0 )
        {
            // assume the current directory
            basePath = new File( "." );
            baseFileName = fullFilePathName;
        }
        else
        {
            // Extract the sub-directory structure
            String m_parentPath = fullFilePathName.substring( 0, fileSeparatorPosition );
            baseFileName = fullFilePathName.substring( fileSeparatorPosition + 1 );
            basePath = new File( m_parentPath );
        }

        return basePath.listFiles( new BaseFileNameFilter( baseFileName ) );
    }
}
