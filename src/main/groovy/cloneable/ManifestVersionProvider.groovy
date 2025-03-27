package cloneable

import picocli.CommandLine.IVersionProvider

import java.io.IOException
import java.net.URL
import java.util.Enumeration
import java.util.jar.Attributes
import java.util.jar.Manifest

/**
 * {@link IVersionProvider} implementation that returns version information
 * from the cloneable.jar file's {@code /META-INF/MANIFEST.MF} file.
 *
 * This code was copied from picocli source code examples and modified for
 * Groovy.
 *
 * Source links:
 * https://github.com/remkop/picocli/blob/5ceaa33435a5f2d4e3df442b0f47a52bfe753046/picocli-examples/src/main/java/picocli/examples/VersionProviderDemo2.java#L50-L77
 * Apache 2.0 licensed https://github.com/remkop/picocli/blob/5ceaa33435a5f2d4e3df442b0f47a52bfe753046/LICENSE
 * https://github.com/remkop/picocli/issues/757
 */
class ManifestVersionProvider implements IVersionProvider {
    String[] getVersion() throws Exception {
        Enumeration<URL> resources = ManifestVersionProvider.class.classLoader.getResources("META-INF/MANIFEST.MF")
        Attributes attr
        URL url = resources.find { URL url ->
            Manifest manifest = new Manifest(url.openStream())
            attr = manifest.mainAttributes
            isApplicableManifest(attr)
        }
        if(url) {
            List<String> versionInfo = ['Application-Name', 'Application-Version'].collect { String prop ->
                get(attr, prop)
            }
            versionInfo << "(git-hash ${get(attr, 'Application-Git-Hash')})"
            [versionInfo.join(' ')] as String[]
        } else {
            new String[0]
        }
    }

    private Boolean isApplicableManifest(Attributes attributes) {
        "cloneable" == get(attributes, "Application-Name")
    }

    private static def get(Attributes attributes, String key) {
        attributes.get(new Attributes.Name(key))
    }
}
