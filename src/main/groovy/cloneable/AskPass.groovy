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

class AskPass {
    AskPass() {
        throw new IllegalStateException('ERROR: you\'ve encountered a bug.  Add --debug option and open an issue.')
    }

    static String getJarPath() {
        new File(AskPass.getProtectionDomain().getCodeSource().getLocation().file).canonicalFile
    }

    static String getAskPassScript(App options) {
        String script = new String(AskPass.getResourceAsStream('/cloneable/clone.script.jsp').getBytes()).trim()
        Map binding = [
            jarPath: getJarPath(),
            keyPath: new File(options.ghAppKey).canonicalPath,
            appId: options.ghAppId,
            owner: options.owner
        ]
        getScriptFromTemplate(script, binding)
    }

    static printScript(App options) {
        println(getAskPassScript(options))
    }
}
