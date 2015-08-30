package org.neverfear.util;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;

public class ResourceFinderTest {
    private static final String BUILD_DIRECTORY = System.getProperty("project.build.outputDirectory", "target/classes");
    private static final String TEST_CLASSES_DIRECTORY = System.getProperty("project.build.testOutputDirectory", "target/test-classes");

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ResourceFinder subject = new ResourceFinder();

    @Test
    public void givenAbsolutePathOfExistingFile_whenFileOfAnyResource_expectExistingFile() throws Exception {
        // Given
        File existing = temporaryFolder.newFile();
        String filePath = existing.getAbsolutePath();

        // When
        File actual = subject.fileOfAnyResource(filePath);

        // Then
        assertFile(filePath, existing, actual);
    }

    @Test
    public void givenRelativePathOfExistingFile_whenFileOfAnyResource_expectExistingFile() throws Exception {
        // Given
        File existing = temporaryFolder.newFile();
        Path cwd = new File(System.getProperty("user.dir")).getAbsoluteFile().toPath();
        Path relative = cwd.relativize(existing.toPath());
        String filePath = relative.toFile().getPath();

        // When
        File actual = subject.fileOfAnyResource(filePath);

        // Then
        assertFile(filePath, existing, actual);
    }

    @Test
    public void givenAbsolutePathOfUnknownFileOrResource_whenFileOfAnyResource_expectNull() throws Exception {
        // Given
        String filePath = new File("i-do-not-exist").getAbsolutePath();

        // When
        File actual = subject.fileOfAnyResource(filePath);

        // Then
        Assert.assertNull(filePath, actual);
    }

    @Test
    public void givenRelativePathOfUnknownFileOrResource_whenFileOfAnyResource_expectNull() throws Exception {
        // Given
        String filePath = "i-do-not-exist";

        // When
        File actual = subject.fileOfAnyResource(filePath);

        // Then
        Assert.assertNull(filePath, actual);
    }

    @Test
    public void givenExistingResourceInTestDirectory_whenFileOfAnyResource_expectPathInTestDirectory() throws Exception {
        // Given
        String resourcePath = "resource-finder/some_resource";

        // When
        File actual = subject.fileOfAnyResource(resourcePath);

        // Then
        File expected = new File(TEST_CLASSES_DIRECTORY, "resource-finder/some_resource");
        assertFile(resourcePath, expected, actual);
    }

    @Test
    public void givenExistingClassInBuildDirectory_whenFileOfAnyResource_expectPathInBuildDirectory() throws Exception {
        // Given
        String resourcePath = "org/neverfear/util/ResourceFinder.class";

        // When
        File actual = subject.fileOfAnyResource(resourcePath);

        // Then
        File expected = new File(BUILD_DIRECTORY, "org/neverfear/util/ResourceFinder.class");
        assertFile(resourcePath, expected, actual);
    }

    @Test
    public void givenExistingResourceInClassPathDependency_whenFileOfAnyResource_expectFileCopyInTempDirectory() throws Exception {
        // Given
        String resourcePath = "org/junit/Test.class";

        // When
        File actual = subject.fileOfAnyResource(resourcePath);

        // Then
        File expectedParent = new File(System.getProperty("java.io.tmpdir"));
        Assert.assertNotNull(resourcePath, actual);
        assertFile(resourcePath, expectedParent, actual.getParentFile());

        ClassLoader classLoader = ResourceFinderTest.class.getClassLoader();
        try (InputStream expectedInput = classLoader.getResourceAsStream(resourcePath);
             InputStream actualInput = new FileInputStream(actual)) {
            assertSimpleContents(resourcePath, expectedInput, actualInput);
        }
    }

    /*
     * This is a little hacky but works great. It tries to fit the expected and actual into memory.
     */
    private static void assertSimpleContents(String message, InputStream expectedStream, InputStream actualStream) throws IOException {
        byte[] expectedBuffer = new byte[4096];
        byte[] actualBuffer = new byte[expectedBuffer.length];
        int expectedCount = expectedStream.read(expectedBuffer);
        int actualCount = actualStream.read(actualBuffer);
        Assert.assertEquals("Mismatched in the number of bytes read", expectedCount, actualCount);

        Assert.assertEquals("Simple assertion has failed, please consider increasing the buffer size to fit the entire resource in memory",
                -1, expectedStream.read(new byte[1]));
        Assert.assertEquals("Simple assertion has failed, please consider increasing the buffer size to fit the entire resource in memory",
                -1, actualStream.read(new byte[1]));

        byte[] expectedCopy = new byte[expectedCount];
        byte[] actualCopy = new byte[actualCount];
        System.arraycopy(expectedBuffer, 0, expectedCopy, 0, expectedCount);
        System.arraycopy(actualBuffer, 0, actualCopy, 0, actualCount);
        Assert.assertArrayEquals(message, expectedCopy, actualCopy);
    }


    private static void assertFile(String message, File expected, File actual) throws IOException {
        if (expected == null) {
            if (actual == null) {
                return;
            }
            Assert.fail("expected null but was " + actual);
        }

        Assert.assertNotEquals(message, null, actual);
        Assert.assertEquals(message, expected.getCanonicalPath(), actual.getCanonicalPath());
    }
}
