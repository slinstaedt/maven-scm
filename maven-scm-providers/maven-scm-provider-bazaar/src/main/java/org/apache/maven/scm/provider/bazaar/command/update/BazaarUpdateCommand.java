package org.apache.maven.scm.provider.bazaar.command.update;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFile;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmFileStatus;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.command.changelog.ChangeLogCommand;
import org.apache.maven.scm.command.update.AbstractUpdateCommand;
import org.apache.maven.scm.command.update.UpdateScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.bazaar.BazaarUtils;
import org.apache.maven.scm.provider.bazaar.command.BazaarCommand;
import org.apache.maven.scm.provider.bazaar.command.BazaarConsumer;
import org.apache.maven.scm.provider.bazaar.command.changelog.BazaarChangeLogCommand;
import org.apache.maven.scm.provider.bazaar.command.diff.BazaarDiffConsumer;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:torbjorn@smorgrav.org">Torbj�rn Eikli Sm�rgrav</a>
 */
public class BazaarUpdateCommand
    extends AbstractUpdateCommand
    implements BazaarCommand
{

    protected UpdateScmResult executeUpdateCommand( ScmProviderRepository repo, ScmFileSet fileSet, String tag )
        throws ScmException
    {

        if ( !StringUtils.isEmpty( tag ) )
        {
            throw new ScmException( "This provider can't handle tags." );
        }

        File workingDir = fileSet.getBasedir();

        // Update branch
        String[] update_cmd = new String[]{BazaarCommand.PULL_CMD};
        ScmResult updateResult =
            BazaarUtils.execute( new BazaarConsumer( getLogger() ), getLogger(), workingDir, update_cmd );

        if ( !updateResult.isSuccess() )
        {
            return new UpdateScmResult( null, null, updateResult );
        }

        // Find changes from last revision
        int prev_revi = BazaarUtils.getCurrentRevisionNumber( getLogger(), workingDir ) - 1;
        String[] diffCmd = new String[]{DIFF_CMD, REVISION_OPTION, "" + prev_revi};
        BazaarDiffConsumer diffConsumer = new BazaarDiffConsumer( getLogger(), workingDir );
        ScmResult diffResult = BazaarUtils.execute( diffConsumer, getLogger(), workingDir, diffCmd );

        // Now translate between diff and update file status
        List updatedFiles = new ArrayList();
        List changes = new ArrayList();
        List diffFiles = diffConsumer.getChangedFiles();
        Map diffChanges = diffConsumer.getDifferences();
        for ( Iterator it = diffFiles.iterator(); it.hasNext(); )
        {
            ScmFile file = (ScmFile) it.next();
            changes.add( diffChanges.get( file ) );
            if ( file.getStatus() == ScmFileStatus.MODIFIED )
            {
                updatedFiles.add( new ScmFile( file.getPath(), ScmFileStatus.PATCHED ) );
            }
            else
            {
                updatedFiles.add( file );
            }
        }

        return new UpdateScmResult( updatedFiles, changes, diffResult );
    }

    protected ChangeLogCommand getChangeLogCommand()
    {
        BazaarChangeLogCommand command = new BazaarChangeLogCommand();
        command.setLogger( getLogger() );
        return command;
    }
}