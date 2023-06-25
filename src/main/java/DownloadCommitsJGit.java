import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class DownloadCommitsJGit {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: java DownloadCommitsJGit <repository_url> <commit_id>");
            System.exit(1);
        }

        String repositoryUrl = args[0];
        String commitId = args[1];

        // Clone the repository
        Git git = Git.cloneRepository()
                .setURI(repositoryUrl)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider("your_personal_access_token", ""))
                .setDirectory(new File("temp-repo"))
                .call();

        // Get the specified commit
        ObjectId objectId = git.getRepository().resolve(commitId);
        if (objectId == null) {
            System.err.printf("Commit '%s' not found%n", commitId);
            System.exit(1);
        }

        // Get the parent commit
        RevCommit parentCommit = null;
        try (RevWalk revWalk = new RevWalk(git.getRepository())) {
            RevCommit commit = revWalk.parseCommit(objectId);
            if (commit.getParentCount() > 0) {
                parentCommit = revWalk.parseCommit(commit.getParent(0));
            }
        }

        // Download the source code for the specified commit and its parent commit
        Path outputPath = Paths.get("download");
        downloadCommitSourceCode(git, objectId, outputPath.resolve(commitId));

        if (parentCommit != null) {
            downloadCommitSourceCode(git, parentCommit.getId(), outputPath.resolve(parentCommit.getId().getName()));
            System.out.printf("Commit %s and its parent %s source codes have been downloaded.%n", commitId, parentCommit.getId().getName());
        } else {
            System.out.println("Parent commit not found");
            System.exit(1);
        }

        // Clean up the temporary repository
        git.getRepository().close();
        git.close();
        deleteDirectory(Paths.get("temp-repo"));
    }

    private static void downloadCommitSourceCode(Git git, ObjectId commitId, Path outputPath) throws Exception {
        Git clonedRepo = Git.cloneRepository()
                .setURI(git.getRepository().getDirectory().toURI().toString())
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider("your_personal_access_token", ""))
                .setDirectory(outputPath.toFile())
                .setNoCheckout(true)
                .call();
        clonedRepo.checkout()
                .setName(commitId.getName())
                .call();
        clonedRepo.close();
    }



    private static void deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
}
