package cloneable

import picocli.CommandLine.IVersionProvider
import java.io.IOException
import java.net.URL
import java.util.Enumeration
import java.util.jar.Attributes
import java.util.jar.Manifest

/**
 * {@link IVersionProvider} implementation that returns version information from the picocli-x.x.jar file's {@code /META-INF/MANIFEST.MF} file.
 * https://github.com/remkop/picocli/blob/master/picocli-examples/src/main/java/picocli/examples/VersionProviderDemo2.java#L69
 * https://github.com/remkop/picocli/issues/757
 */
class ManifestVersionProvider implements IVersionProvider {
    String[] getVersion() throws Exception {
        Enumeration<URL> resources = ManifestVersionProvider.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            try {
                Manifest manifest = new Manifest(url.openStream());
                if (isApplicableManifest(manifest)) {
                    Attributes attr = manifest.getMainAttributes();
                    return ["${get(attr, 'Application-Name')} ${get(attr, 'Application-Version')}"] as String[]
                }
            } catch (IOException ex) {
                return ["Unable to read from ${url}: ${ex}"] as String[]
            }
        }
        return new String[0];
    }

    private Boolean isApplicableManifest(Manifest manifest) {
        Attributes attributes = manifest.getMainAttributes();
        return "cloneable".equals(get(attributes, "Application-Name"));
    }

    private static def get(Attributes attributes, String key) {
        return attributes.get(new Attributes.Name(key));
    }
}
