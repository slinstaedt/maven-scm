package org.apache.maven.scm.provider.synergy.consumer;

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

import org.apache.maven.scm.ScmTestCase;
import org.apache.maven.scm.log.DefaultLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author <a href="mailto:julien.henry@capgemini.com">Julien Henry</a>
 */
public class SynergyCreateTaskConsumerTest extends ScmTestCase
{
    public void testSynergyGetTaskObjectsConsumer() throws IOException
    {
        InputStream inputStream = getResourceAsStream( "/synergy/consumer/createTask.txt" );

        BufferedReader in = new BufferedReader( new InputStreamReader( inputStream ) );

        String s = in.readLine();

        SynergyCreateTaskConsumer consumer = new SynergyCreateTaskConsumer( new DefaultLog() );

        while ( s != null )
        {
            consumer.consumeLine( s );

            s = in.readLine();
        }

        assertEquals( 70, consumer.getTask() );

    }
}
