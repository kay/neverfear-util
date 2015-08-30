package org.neverfear.util;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

public class ResourceFinder {


    public File fileOfAnyResource(String resourceOrFileName) throws URISyntaxException, IOException {
        return fileOfAnyResource(resourceOrFileName, ResourceFinder.class.getClassLoader());
    }

    public File fileOfAnyResource(String resourceOrFileName, ClassLoader classLoader) throws URISyntaxException, IOException {
        File fileSystemResource = new File(resourceOrFileName);
        if (fileSystemResource.exists()) {
            return fileSystemResource;
        }

        return fileOfClassPathResource(resourceOrFileName, classLoader);
    }

    public File fileOfClassPathResource(String resourceName, ClassLoader classLoader) throws URISyntaxException, IOException {
        URL resourceURL = classLoader.getResource(resourceName);
        if (resourceURL == null) {
            return null;
        }

        String protocol = resourceURL.getProtocol();
        if ("file".equals(protocol)) {
            return new File(resourceURL.toURI());
        } else if ("jar".equals(protocol)) {
            File outputFile = createTempFile();
            try (InputStream input = classLoader.getResourceAsStream(resourceName);
                 OutputStream output = new FileOutputStream(outputFile)) {
                copy(input, output);
            }
            return outputFile;
        }

        // No more methods to try
        return null;
    }

    private File createTempFile() throws IOException {
        return File.createTempFile(ResourceFinder.class.getName(), null);
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[4096];
        int count;
        while ((count = in.read(buffer)) != -1) {
            out.write(buffer, 0, count);
        }
    }
}
