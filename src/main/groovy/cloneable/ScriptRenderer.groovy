/*
 * Copyright 2020-2025 Sam Gleske
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

package cloneable

import static net.gleske.jervis.tools.AutoRelease.getScriptFromTemplate

import picocli.AutoComplete

class ScriptRenderer {
    ScriptRenderer() {
        throw new IllegalStateException('ERROR: you\'ve encountered a bug.  Add --debug option and open an issue.')
    }

    static String getCanonicalPath(String path) {
        if(!path) {
            return ''
        }
        new File(path).canonicalPath
    }

    static String getJarPath() {
        getCanonicalPath(ScriptRenderer.getProtectionDomain().getCodeSource().getLocation().file)
    }

    static readFileFromJar(String path) {
        new String(ScriptRenderer.getResourceAsStream(path).getBytes()).trim()
    }

    static String renderScript(String path, Map vars) {
        getScriptFromTemplate(readFileFromJar(path), vars)
    }

    static printAskpassScript(App options) {
        Map vars = [
            jarPath: getJarPath(),
            keyPath: getCanonicalPath(options.ghAppKey),
            appId: options.ghAppId,
            owner: options.owner
        ]
        println(renderScript('/cloneable/askpass-tempate.jsp', vars))
    }

    static void printUpdateScript(App options) {
        Map vars = [
            isHttpUpdate: options.httpUrl
        ]
        println(renderScript('/cloneable/update-template.jsp', vars))
    }

    static void printCliScript() {
        Map vars = [
            jarPath: getJarPath()
        ]
        println(renderScript('/cloneable/cli-template.jsp', vars))
    }

    static void printCloneScript(App options) {
        List additional_args = options.optionsToArgList()
        Map vars = [
            jarPath: getJarPath(),
            additional_args: additional_args.join(' \\\n  ')
        ]
        println(renderScript('/cloneable/clone-template.jsp', vars))
    }

    static void printBashCompletion(App options) {
        println(AutoComplete.bash('cloneable', options.spec.commandLine()).trim())
    }
}
