#! /usr/bin/env node
'use strict';

/*
 * Copyright 2017 Anton Johansson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const spawn = require('child_process').spawn;

function getJavaVersion(callback)
{
    var buffer = '';
    var versionProcess = spawn('java', ['-version']);
    versionProcess.on('error', function(err)
    {
        return callback(new Error('No Java installation was found.'), null);
    });
    versionProcess.stderr.on('data', function(data)
    {
        buffer = buffer + data.toString();
    });
    versionProcess.on('exit', function()
    {
        var line = buffer.split('\n')[0];
        var javaVersionLine = new RegExp('java version').test(line) ? line.split(' ')[2].replace(/"/g, '') : false;
        if (javaVersionLine == false)
        {
            return callback(new Error('Java is required but no installation was found'));
        }
        var test = new RegExp(/(\d+)\.(\d+)\.(\d+)_(\d+)/).exec(javaVersionLine);
        if (!test)
        {
            return callback(new Error('Unknown Java version: ' + javaVersionLine));
        }
        var javaVersion =
        {
            major: test[1],
            minor: test[2],
            revision: test[3],
            build: test[4]
        }
        return callback(null, javaVersion);
    });
}

function runShell()
{
    var args = [ '-cp', __dirname + '/lib/*', 'com.antonjohansson.elasticsearchshell.EntryPoint' ];
    var javaProcess = spawn('java', args);
    process.stdin.pipe(javaProcess.stdin);
    javaProcess.stdout.on('data', function(data)
    {
        process.stdout.write(data.toString());
    });
    javaProcess.stderr.on('data', function(data)
    {
        process.stderr.write(data.toString());
    });
    javaProcess.on('exit', function()
    {
        process.exit();
    });
}

getJavaVersion(function(err, version)
{
    if (err)
    {
        console.error(err.message);
    }
    else
    {
        if (version.minor < 8)
        {
            console.error('Java 8 or above is required');
        }
        else
        {
            runShell();
        }
    }
});