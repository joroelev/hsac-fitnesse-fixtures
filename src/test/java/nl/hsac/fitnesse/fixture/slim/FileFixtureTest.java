package nl.hsac.fitnesse.fixture.slim;

import nl.hsac.fitnesse.fixture.Environment;
import nl.hsac.fitnesse.fixture.util.LineEndingHelper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FileFixtureTest {
    private final FileFixture fixture = new FileFixture();
    private final String txtFilename = "testFileFixture.txt";
    private final String copyFilename = "temp-copy.txt";
    private final String deleteFilename = "delete-me.txt";
    private final String curDir = Paths.get("").toAbsolutePath().toString();
    private final String testResourcesDir = curDir + "/src/test/resources/".replace('/', File.separatorChar);


    private String getTmpFolderPath() {
        return tmpFolder.getRoot() + File.separator;
    }

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testGetDirectory() {
        String defaultFilesDir = Environment.getInstance().getFitNesseFilesSectionDir();
        String defaultFixtureDir = defaultFilesDir + File.separator + "fileFixture" + File.separator;

        assertThat(defaultFixtureDir).isEqualTo(fixture.getDirectory());
    }

    @Test
    public void testSetAndGetDirectory() {
        fixture.setDirectory(getTmpFolderPath());
        assertThat(getTmpFolderPath()).isEqualTo(fixture.getDirectory());
    }

    @Test
    public void testGetAndSetDirectoryRelative() {
        String childDir = "bla";
        fixture.setDirectory(childDir);
        String fitNesseDir = Environment.getInstance().getFitNesseDir();
        String expected = new File(fitNesseDir, childDir).getAbsolutePath() + File.separator;
        assertEquals(expected, fixture.getDirectory());
    }

    @Test
    public void testTextIn() {
        try {
            fixture.setDirectory(testResourcesDir);
            assertEquals("# Expected as first line of text.\n# Expected as 2nd line of text.",
                    new LineEndingHelper().convertEndingsTo(fixture.textIn(txtFilename), "\n"));
            fixture.textIn("foobar");
        } catch (SlimFixtureException sfe) {
            assertEquals("message:<<Unable to find: " + testResourcesDir + "foobar>>", sfe.getMessage());
        }
    }

    @Test
    public void testDeleteIfExists_non_existing_returns_false() {
        String nonExistingFilePath = getTmpFolderPath() + "i-do-not-exist.lol";

        Boolean result = fixture.deleteIfExists(nonExistingFilePath);

        assertThat(result).isFalse();
    }

    @Test
    public void testDeleteIfExists_existing_returns_true() throws IOException {
        File fileToDelete = tmpFolder.newFile(deleteFilename);

        Boolean result = fixture.deleteIfExists(fileToDelete.getPath());

        assertThat(result).isTrue();
    }

    @Test
    public void testDeleteIfExists_failing_delete_throws_exception() throws IOException {
        File fileToDelete = tmpFolder.newFile(deleteFilename);

        //Forces file.delete in fixture.delete() to fail so expected exception will be thrown
        new RandomAccessFile(fileToDelete, "r");

        Throwable thrown = catchThrowable(() -> fixture.deleteIfExists(fileToDelete.getPath()));

        assertThat(thrown)
                .isInstanceOf(SlimFixtureException.class)
                .hasMessageContaining("Unable to delete file");
    }

    @Test
    public void testCopyTo() {
        fixture.setDirectory(testResourcesDir);
        assertTrue(fixture.exists(txtFilename));
        try {
            String res = fixture.copyTo(txtFilename, copyFilename);
            String expectedPath = getExpectedUrlPath();
            assertEquals("<a href=\"file:" + expectedPath + "\" target=\"_blank\">" + copyFilename + "</a>", res);
        } catch (IOException ioe) {
            fail("Should not happen: " + ioe.getMessage());
        }
        assertTrue(fixture.exists(copyFilename));
    }

    @Test
    public void testTakeFirstLineFrom() {
        try {
            fixture.setDirectory(testResourcesDir);
            fixture.copyTo(txtFilename, copyFilename);
            assertEquals("# Expected as first line of text.",
                    fixture.takeFirstLineFrom(copyFilename));
            assertEquals("# Expected as 2nd line of text.",
                    fixture.takeFirstLineFrom(copyFilename));
            fixture.textIn("foobar");
        } catch (SlimFixtureException sfe) {
            assertEquals("message:<<Unable to find: " + testResourcesDir + "foobar>>", sfe.getMessage());
        } catch (IOException ioe) {
            fail("Should not happen: " + ioe.getMessage());
        }
    }

    @Test
    public void testAppendContainingOnNewline() {
        try {
            fixture.setDirectory(testResourcesDir);
            fixture.copyTo(txtFilename, copyFilename);
            String res = fixture.appendToOnNewLine("Third line", copyFilename);
            String expectedPath = getExpectedUrlPath();
            assertEquals("<a href=\"file:" + expectedPath + "\" target=\"_blank\">" + copyFilename + "</a>", res);
            assertEquals("# Expected as first line of text.\n# Expected as 2nd line of text.\nThird line",
                    new LineEndingHelper().convertEndingsTo(fixture.textIn(copyFilename), "\n"));
        } catch (IOException ioe) {
            fail("Should not happen: " + ioe.getMessage());
        }
    }

    //
    @Test
    public void testAppendContainingOnSameline() {
        try {
            fixture.setDirectory(testResourcesDir);
            fixture.copyTo(txtFilename, copyFilename);
            String res = fixture.appendTo("Third line", copyFilename);
            String expectedPath = getExpectedUrlPath();
            assertEquals("<a href=\"file:" + expectedPath + "\" target=\"_blank\">" + copyFilename + "</a>", res);
            assertEquals("# Expected as first line of text.\n# Expected as 2nd line of text.Third line",
                    new LineEndingHelper().convertEndingsTo(fixture.textIn(copyFilename), "\n"));
        } catch (IOException ioe) {
            fail("Should not happen: " + ioe.getMessage());
        }
    }

    private String getExpectedUrlPath() {
        String path = testResourcesDir + copyFilename;
        String unixStylePath = path.replace(File.separatorChar, '/');
        if (!unixStylePath.startsWith("/")) {
            unixStylePath = "/" + unixStylePath;
        }
        return unixStylePath;
    }

    @Test
    public void testDelete_existing() throws IOException {
        File fileToDelete = tmpFolder.newFile(deleteFilename);

        fixture.delete(fileToDelete.getPath());

        assertThat(fileToDelete.exists()).isFalse();
    }

    @Test
    public void testDelete_non_existing_throws_exception() throws IOException {
        String nonExistingFilePath = getTmpFolderPath() + "i-do-not-exist.lol";

        Throwable thrown = catchThrowable(() -> fixture.delete(nonExistingFilePath));

        assertThat(thrown).isInstanceOf(SlimFixtureException.class);
    }
}
