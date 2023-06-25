import com.sun.net.httpserver.Authenticator;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
//import org.junit.runner.JUnitCore;
//import org.junit.runner.Result;
//import org.junit.runner.notification.Failure;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import javax.xml.transform.Result;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
class GithubTestRunner {
    private static Object JUnitCore;

    public static void main(String[] args) throws Exception {
        String commitId = "1234567890abcdef";
        String testFilePath = ""; // The path to the test file
        String previousCommitId = ""; // The previous commit ID

        // Clone the repository
        Git git = Git.cloneRepository()
                .setURI("https://github.com/username/repo.git")
                .setDirectory(new File("repo"))
                .call();

        Repository repository = git.getRepository();

        // Get the commit details
        RevCommit commit = repository.parseCommit(repository.resolve(commitId));

        // Get the list of modified files in the commit
        for (RevCommit parentCommit : commit.getParents()) {
            Iterable<RevCommit> log = git.log().add(parentCommit).add(commit).call();
            for (RevCommit rev : log) {
                //for (String filePath : rev.getTree().entries()) {
                    // Check if the file was added or modified in the commit
                    //if (rev.getTree().findBlobMember(filePath) != null) {
                        // Check if it's a test file
                        //if (filePath.matches(testFilePath)) {
                            // Write the contents of the test file to a new file
                            //String testContents = new String(rev.getTree().findBlobMember(filePath).getBytes());
                            FileWriter writer = new FileWriter("newTestFile.java");
                            //writer.write(testContents);
                            writer.close();
                        //}
                    //}
                //}
            }
        }

        // Checkout the previous commit
        git.checkout().setName(previousCommitId).call();

        // Compile and run the tests
        // Compile the test file and run the tests
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int result = compiler.run(null, null, null, "newTestFile.java");

// Load the compiled test class using a URLClassLoader
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {new File("").toURI().toURL()});
        Class<?> testClass = Class.forName("newTestFile", true, classLoader);

// Run the tests using JUnitCore
        Class<?> junitResult = JUnitCore.getClass();
        // Parse the test results
        if ((boolean) junitResult.cast(testClass)) {
            System.out.println("Tests passed!");
        } else {
            //for (Authenticator.Failure failure : junitResult.getField(testClass)) {
                //System.out.println(failure.toString());
            //}
        }

        // Print out the commit ID and test result
        System.out.println("Commit ID: " + commitId);
        if ((boolean) junitResult.cast(testClass)) {
            System.out.println("Test result: pass");
        } else {
            System.out.println("Test result: fail");
        }
    }
}


public class main {

    public static void main(String[] args) throws Exception {
        GitService gitService = new GitServiceImpl();
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

        Repository repo = (Repository) gitService.cloneIfNotExists(
                //"uniVocity/univocity-parsers",
                //"https://github.com/uniVocity/univocity-parsers.git"
                "alibaba/fastjson",
                "https://github.com/alibaba/fastjson.git"
        );

        miner.detectAtCommit(repo, "18a99f2d449321393f57d50e2fbf35ca5748fae5", new RefactoringHandler() {
            @Override
            public void handle(String commitId, List<Refactoring> refactorings) {
                System.out.println("Refactorings at " + commitId);
                for (Refactoring ref : refactorings) {
                    System.out.println(ref.toString());

                }
                String str = "Refactorings at " + commitId;
                System.out.println(str);
                if (str.contains("no refactoring")) {
                    System.out.println("no");
                }
                else{
                    System.out.println("yes");

                }
            }
        });
    }

}
