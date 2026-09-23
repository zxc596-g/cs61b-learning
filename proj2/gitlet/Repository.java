package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static gitlet.Utils.*;

public class Repository implements Serializable {
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static final File OBJ = join(GITLET_DIR, "object");

    public static final File REFS = join(GITLET_DIR, "refs");

    public static final File LOG = join(GITLET_DIR, "logs");

    public static final File STAGE = join(GITLET_DIR, "stage");

    public static final File HEAD = join(GITLET_DIR, "HEAD");

    public static final File MASTER = join(REFS, "heads", "master");

    public static final File heads = join(REFS, "heads");

    public static final File obj1 = join(OBJ, "object1");

    public static final File obj2 = join(OBJ, "object2");

//    File remotes = join(REFS, "remotes");

    public static final File Map = join(STAGE, "Map");

    private Repository(){}

    public static String createMergelog(Commit targetCommit) {
        String firstSha1 = targetCommit.getParent_1().substring(0, 7);
        String secondSha1 = targetCommit.getParent_2().substring(0, 7);

        String current = "===\n" + "commit " + targetCommit.getSha1() + "\n"
                + "Merge: " + firstSha1 + " " + secondSha1 + "\n"
                + "Date: " + targetCommit.getTime() + "\n"
                + targetCommit.getMessage() + "\n";
        return current;
    }

    public static String createlog(String targetSha1) {
        Commit targetCommit = Commit.getCommit(targetSha1);
        if (targetCommit.getParent_2() != null) {
            return createMergelog(targetCommit);
        } else {
            String current = "===\n" + "commit " + targetCommit.getSha1() + "\n"
                    + "Date: " + targetCommit.getTime() + "\n"
                    + targetCommit.getMessage() + "\n";
            return current;
        }
    }

    private static boolean PointerIsExist(String name) {
        File current = join(REFS, "heads", name);
        return current.exists();
    }

    public static void updatePointer(String name, String commit_sha1) {
        if(!PointerIsExist(name)) {
            System.out.println("pointer is not exist!");
            return;
        }
        File current = join(REFS, "heads", name);
        Utils.writeContents(current, commit_sha1);
    }

    public static String getHeadSha1() {
        String address = Utils.readContentsAsString(HEAD).trim();
        File current = Utils.join(GITLET_DIR, address);
        return Utils.readContentsAsString(current).trim();
    }

    public static String getHeadname() {
        String address = Utils.readContentsAsString(HEAD).trim();
        File current = Utils.join(GITLET_DIR, address);
        return current.getName();
    }

    private static void m_init() {
        if(GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        if(!CWD.exists()) {
            CWD.mkdir();
        }
        GITLET_DIR.mkdir();
        OBJ.mkdir();
        REFS.mkdir();
        heads.mkdir();
        Map.mkdir();
        obj1.mkdir();
        obj2.mkdir();

        Commit initcommit = new Commit();
        Utils.writeContents(MASTER, initcommit.getSha1());
        Utils.writeContents(HEAD, "refs/heads/master");
        Utils.writeObject(join(obj1, initcommit.getSha1()), initcommit);
    }

    public static void init(String[] args) {
        if (args.length != 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        m_init();
    }

    //        暂存一个已经暂存的文件会使用新内容覆盖暂存区中的旧条目。暂存区应该位于 .gitlet 的某个位置。

    //        如果当前工作版本的文件与当前提交中的版本相同，则不要暂存它以进行添加，

    //        如果它已经在暂存区中，则将其移除（这通常发生在文件被修改、添加，然后改回其原始版本时）。
    private static void m_add(String filename) {
        File current = Utils.join(CWD, filename);
        if (!current.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Stage stage = Stage.load();
        Blob blob = new Blob(current);
        String sha1 = blob.getSha1();
        String commitSha1 = getHeadSha1();
        Commit commit = Commit.getCommit(commitSha1);
        Map <String, String> trackedFiles = commit.getTrackedFiles();
        if (trackedFiles.containsKey(filename)) {
            if (trackedFiles.get(filename).equals(sha1)) {
                stage.getAddition().remove(filename);
                stage.getRemoval().remove(filename);
                stage.save();
                return;
            }
        }
        stage.add(filename, sha1);
        Utils.writeContents(Utils.join(obj2, sha1), blob.getContent());
        stage.save();
    }

    public static void add(String[] args) {
        isInit();
        if (args.length == 2) {
            m_add(args[1]);
        } else {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    private static boolean m_rm(String filename) {
        File current = join(CWD,filename);
        String commitSha1 = getHeadSha1();
        Commit commit = Commit.getCommit(commitSha1);
        Stage stage = Stage.load();
        Map <String, String> trackedFiles = commit.getTrackedFiles();
        if (trackedFiles.containsKey(filename)) {
            stage.getAddition().remove(filename);
            stage.getRemoval().add(filename);
            Utils.restrictedDelete(current);
            stage.save();
            return true;
        }
        if (!stage.getAddition().containsKey(filename)) {
            System.out.println("No reason to remove the file.");
            return false;
        }
        stage.getAddition().remove(filename);
        stage.save();
        return true;
    }

    public static void rm(String[] args) {
        isInit();
        if (args.length == 2) {
            Repository.m_rm(args[1]);
        } else {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    private static void m_commit(String message) {
        Stage stage = Stage.load();
        String headSha1 = getHeadSha1();
        Commit oldcommit = Commit.getCommit(headSha1);
        Map <String, String> lastestMap = oldcommit.getTrackedFiles();
        Map <String, String> map = new HashMap<>(lastestMap);
        Set<String> removal = stage.getRemoval();
        Map<String, String> addition = stage.getAddition();
        if (removal.isEmpty() && addition.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        if (!removal.isEmpty()) {
            for (String c : removal) {
                if (map.containsKey(c)) {
                    map.remove(c);
                }
            }
        }
        if (!addition.isEmpty()) {
            for(String c : addition.keySet()) {
                map.put(c, addition.get(c));
            }
        }
        stage.clear();
        Commit newcommit = new Commit(message, headSha1 , map);
        updatePointer(getHeadname(), newcommit.getSha1());
        Utils.writeObject(join(obj1, newcommit.getSha1()), newcommit);
    }

    public static void commit(String[] args) {
        isInit();
        if (args.length <= 1 || args[1] == null || args[1].trim().isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        Repository.m_commit(args[1]);
    }

    private static void m_log() {
        String headSha1 = getHeadSha1();
        Commit commit = Commit.getCommit(headSha1);
        System.out.println(createlog(headSha1));
        while(commit.getParent_1() != null && !commit.getParent_1().trim().isEmpty()) {
            commit = Commit.getCommit(commit.getParent_1());
            System.out.println(createlog(commit.getSha1()));
        }
    }

    public  static void log(String[] args) {
        isInit();
        if (args.length != 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        m_log();
    }

    private static void m_globallog() {
        //init commit exists so obj1 is not null
        for(String s : Utils.plainFilenamesIn(obj1)) {
            if (s.trim().length() == 40) {
                System.out.println(createlog(s));
            }
        }
    }

    public static void globallog(String[] args) {
        isInit();
        if (args.length != 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        m_globallog();
    }

    public static void isInit() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    private static void m_find(String message) {
        //Whether message is null will be determined in the find function
        boolean isfound = false;
        for (String s : Utils.plainFilenamesIn(obj1)) {
            if (s.trim().length() == 40) {
                Commit commit = Commit.getCommit(s);
                if (commit.getMessage().equals(message)) {
                    System.out.println(s);
                    isfound = true;
                }
            }
        }
        if (isfound == false) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    public static void find(String[] args) {
        isInit();
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        m_find(args[1]);
    }

    private static void m_branch(String name) {
        File current = Utils.join(heads, name);
        if (current.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        String headSha1 = getHeadSha1();
        Utils.writeContents(current, headSha1);
    }

    public static void branch(String[] args) {
        isInit();
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        m_branch(args[1]);
    }

    private static void m_rm_branch(String name) {
        String currentBranchName = getHeadname();
        if (currentBranchName.equals(name)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        File targetBranch = Utils.join(heads, name);
        if (!targetBranch.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        targetBranch.delete();
    }
    public static void rm_branch(String[] args) {
        isInit();
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        m_rm_branch(args[1]);
    }

    private static boolean isUntracked(String fileName) {
        Stage stage = Stage.load();
        Set<String> addition = stage.getAddition().keySet();
        Set<String> removal = stage.getRemoval();
        Commit commit = Commit.getHeadCommit();
        Set<String> tracked = commit.getTrackedFiles().keySet();
        return isUntracked(addition, removal, tracked, fileName);
    }

    private static boolean isUntracked(Set<String> addition, Set<String> removal, Set<String> tracked, String fileName) {
        if (!addition.contains(fileName)) {
            if (!tracked.contains(fileName)) {
                return true;
            } else {
                if (removal.contains(fileName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void m_status() {
        System.out.println("=== Branches ===");
        List<String> branches = Utils.plainFilenamesIn(heads);
        branches.sort(null);
        String currentBranch = getHeadname();
        for (String c : branches) {
            if (c.equals(currentBranch)) {
                System.out.println("*" + c);
            } else {
                System.out.println(c);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        Stage stage = Stage.load();
        Map<String, String> addition = stage.getAddition();
        List<String> stagedFile = new ArrayList<>(addition.keySet());
        stagedFile.sort(null);
        for (String c : stagedFile) {
            System.out.println(c);
        }

        System.out.println();

        System.out.println("=== Removed Files ===");
        Set<String> removal = stage.getRemoval();
        List<String>removedFile = new ArrayList<>(removal);
        removedFile.sort(null);
        for (String c : removedFile) {
            System.out.println(c);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();

        System.out.println("=== Untracked Files ===");
        Set<String> additionSet = addition.keySet();
        Set<String> untracked = Commit.getHeadCommit().getTrackedFiles().keySet();
        List<String> untrackedFileList = new ArrayList<>();
        for(String c : Utils.plainFilenamesIn(CWD)) {
            if (isUntracked(additionSet, removal, untracked, c)) {
                untrackedFileList.add(c);
            }
        }
        untrackedFileList.sort(null);
        for(String untrackedFileName : untrackedFileList) {
            System.out.println(untrackedFileName);
        }
        System.out.println();
    }

    public static void status(String[] args) {
        isInit();
        if (args.length != 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        m_status();
    }

    private static List<String> getSha1List() {
        List<String> sha1List = new ArrayList<>();
        for (String c : Utils.plainFilenamesIn(obj1)) {
            sha1List.add(c);
        }
        return sha1List;
    }

    private static Commit searchSha1(String preSha) {
        if (preSha.length() > 40) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        List<String> sha1List = getSha1List();
        String matchedSha1 = "";
        int matchCount = 0;
        for(String sha1_of_sha1List : sha1List) {
            if (sha1_of_sha1List.startsWith(preSha)) {
                matchedSha1 = sha1_of_sha1List;
                matchCount = matchCount + 1;
            }
        }
        if (matchCount > 1) {
            System.out.println("More than one commit has the same prefix.");
            System.exit(0);
        }
        if (matchCount == 0) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        return Commit.getCommit(matchedSha1);
    }

    private static void m_checkout(String commitSha1, String fileName) {
        Commit targetCommit = searchSha1(commitSha1);
        Map<String, String> targetMap = targetCommit.getTrackedFiles();
        if (!targetMap.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String blobSha1 = targetMap.get(fileName);
        File blobFile = Utils.join(obj2, blobSha1);
        byte[] content = Utils.readContents(blobFile);
        File headFile = Utils.join(fileName);
        Utils.writeContents(headFile, content);
        Stage stage = Stage.load();
        stage.getAddition().remove(fileName);
        stage.getRemoval().remove(fileName);
        stage.save();
    }

    private static void m_checkout(String fileName) {
        m_checkout(getHeadSha1(), fileName);
    }

    private static Commit m_checkoutfully(String commitSha1) {
        Stage stage = Stage.load();
        Set<String> addition = stage.getAddition().keySet();
        Set<String> removal = stage.getRemoval();
        Set<String> currentTrackedFile = Commit.getHeadCommit().getTrackedFiles().keySet();
        Commit targetCommit = searchSha1(commitSha1);
        Set<String> targetTrackedFiles = targetCommit.getTrackedFiles().keySet();
        for (String fileNameInCWD : Utils.plainFilenamesIn(CWD)) {
            if (isUntracked(addition, removal, currentTrackedFile, fileNameInCWD)) {
                if(targetTrackedFiles.contains(fileNameInCWD)) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }
        for (String currentTrackedFileName : currentTrackedFile) {
            if (!targetTrackedFiles.contains(currentTrackedFileName)) {
                //Utils.restrictedDelete(currentTrackedFileName);
                File file = join(CWD, currentTrackedFileName);
                file.delete();
            }
        }
        String targetCommitSha1 = targetCommit.getSha1();
        for (String fileNameInTrackedFile : targetTrackedFiles) {
            m_checkout(targetCommitSha1, fileNameInTrackedFile);
        }
        stage.clear();
        return targetCommit;
    }

    private static void updateHEAD(String branchName) {
        if (!Utils.join(heads, branchName).exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        Utils.writeContents(HEAD, "refs/heads/" + branchName);
    }

    private static void m_checkoutBranch(String branchName) {
        if (!Utils.join(heads, branchName).exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (getHeadname().equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        String branchSha1 = Utils.readContentsAsString(Utils.join(heads, branchName));
        m_checkoutfully(branchSha1);
        updateHEAD(branchName);
    }

    private static void m_reset(String preSha1) {
        Commit targetCommit = m_checkoutfully(preSha1);
        updatePointer(getHeadname(), targetCommit.getSha1());
    }

    public static void reset(String[] args) {
        isInit();
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        m_reset(args[1]);
    }
    public static void checkout(String[] args) {
        isInit();
        if (args.length == 2) {
            m_checkoutBranch(args[1]);
        } else if (args.length == 3) {
            if (!args[1].equals("--")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            m_checkout(args[2]);
        } else if (args.length == 4){
            if (!args[2].equals("--")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            m_checkout(args[1], args[3]);
        } else {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    private static String searchSplit(String branchName) {
        File branch = Utils.join(heads, branchName);
        if (!branch.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        String branchSha1 = Utils.readContentsAsString(branch);
        String headSha1 = getHeadSha1();
        if (headSha1.equals(branchSha1)) {
            return headSha1;
        }
        Map<String, Integer> distanceMap = new HashMap<>();
        Queue<String> que = new ArrayDeque<>();
        que.add(headSha1);
        distanceMap.put(headSha1, 0);
        while (!que.isEmpty()) {
            String currSha1 = que.remove();
            Commit currCommit = Commit.getCommit(currSha1);
            int currdistance = distanceMap.get(currSha1);

            String parent1Sha1 = currCommit.getParent_1();
            if (parent1Sha1 != null && !distanceMap.containsKey(parent1Sha1)) {
                distanceMap.put(parent1Sha1, currdistance + 1);
                que.add(parent1Sha1);
            }

            String parent2Sha1 = currCommit.getParent_2();
            if (parent2Sha1 != null && !distanceMap.containsKey(parent2Sha1)) {
                distanceMap.put(parent2Sha1, currdistance + 1);
                que.add(parent2Sha1);
            }
        }

        que.add(branchSha1);
        String targetSha1 = "";
        int targetDistance = Integer.MAX_VALUE;
        Set<String> visited = new HashSet<>();
        visited.add(branchSha1);
        while (!que.isEmpty()) {
            Commit branchCommit = Commit.getCommit(que.remove());
            String bSha1 = branchCommit.getSha1();
            if (distanceMap.containsKey(bSha1)) {
                if (targetDistance > distanceMap.get(bSha1)) {
                    targetDistance = distanceMap.get(bSha1);
                    targetSha1 = bSha1;
                }
                continue;
            }

            String parent1Sha1 = branchCommit.getParent_1();
            if (parent1Sha1 != null && !visited.contains(parent1Sha1)) {
                que.add(parent1Sha1);
                visited.add(parent1Sha1);
            }

            String parent2Sha1 = branchCommit.getParent_2();
            if (parent2Sha1 != null && !visited.contains(parent2Sha1)) {
                que.add(parent2Sha1);
                visited.add(parent2Sha1);
            }
        }
        return targetSha1;
    }

    private static String jointFiles(String condition_Head, String condition_Branch) {
        byte[] headContent = null;
        byte[] branchContent = null;
        if (!condition_Head.equals("-1")) {
            headContent = Utils.readContents(Utils.join(obj2, condition_Head));
        }
        if (!condition_Branch.equals("-1")) {
            branchContent = Utils.readContents(Utils.join(obj2, condition_Branch));
        }
        String headText = (headContent != null) ? new String(headContent, StandardCharsets.UTF_8) : "";
        String branchText = (branchContent != null) ? new String(branchContent, StandardCharsets.UTF_8) : "";

        String targetContent = "<<<<<<< HEAD\n" + headText
                + "=======\n"
                + branchText + ">>>>>>>\n";
        byte[] targetText = targetContent.getBytes();
        String targetSha1 = sha1(targetText);
        Utils.writeContents(Utils.join(obj2, targetSha1), targetText);
        return targetSha1;
    }

    private static boolean ismerged(String head, String given, String split) {
        if (!split.equals("-1")) {
            if (head.equals(given)) return false;
            if (given.equals(split)) return false;
        } else {
            if (given.equals("-1") && !head.equals("-1")) return false;
        }
        return true;
    }

    private static void m_merge(String branchName) {
        String headName = getHeadname();
        if (!Utils.join(heads, branchName).exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (branchName.equals(headName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        Stage stage = Stage.load();
        if (!stage.getAddition().isEmpty() || !stage.getRemoval().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        String splitPointSha1 = searchSplit(branchName);
        //如果分割点是当前分支，那么效果是检出给定分支，操作在打印“当前分支快速前进”的消息后结束。
        String headSha1 = getHeadSha1();
        String branchSha1 = Utils.readContentsAsString(Utils.join(heads, branchName));
        if (splitPointSha1.equals(headSha1)) {
            m_checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }

        //如果分割点是与给定分支相同的提交，那么我们不做任何事情；合并完成，操作以“给定分支是当前分支的祖先”的消息结束。
        if (splitPointSha1.equals(branchSha1)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }

        Map<String, String> headTrackedFiles = new HashMap<>(Commit.getCommit(headSha1).getTrackedFiles());
        Map<String, String> branchTrackedFiles = new HashMap<>(Commit.getCommit(branchSha1).getTrackedFiles());
        Map<String, String> splitTrackedFiles = new HashMap<>(Commit.getCommit(splitPointSha1).getTrackedFiles());
        Map<String, String> targetTrackedFiles = new HashMap<>(Commit.getCommit(headSha1).getTrackedFiles());

        Set<String> allFiles = new HashSet<>(headTrackedFiles.keySet());
        allFiles.addAll(branchTrackedFiles.keySet());
        allFiles.addAll(splitTrackedFiles.keySet());

        for (String fileName : allFiles) {
            String condition_Head = "-1";
            String condition_Branch = "-1";
            String condition_Spilt = "-1";
            if (headTrackedFiles.containsKey(fileName)) {
                condition_Head = headTrackedFiles.get(fileName);
            }
            if (branchTrackedFiles.containsKey(fileName)) {
                condition_Branch = branchTrackedFiles.get(fileName);
            }
            if (splitTrackedFiles.containsKey(fileName)) {
                condition_Spilt = splitTrackedFiles.get(fileName);
            }

            if (isUntracked(Collections.emptySet(), Collections.emptySet(), headTrackedFiles.keySet(), fileName)
                    && ismerged(condition_Head, condition_Branch, condition_Spilt)
                    && join(CWD, fileName).exists()) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        boolean hasConflict = false;
        for (String fileName : allFiles) {
            //-1 means file not exist,else restore Sha1 of the Blob
            String condition_Head = "-1";
            String condition_Branch = "-1";
            String condition_Spilt = "-1";
            if (headTrackedFiles.containsKey(fileName)) {condition_Head = headTrackedFiles.get(fileName);}
            if (branchTrackedFiles.containsKey(fileName)) {condition_Branch = branchTrackedFiles.get(fileName);}
            if (splitTrackedFiles.containsKey(fileName)) {condition_Spilt = splitTrackedFiles.get(fileName);}

            if (!condition_Spilt.equals("-1") && condition_Head.equals(condition_Spilt) && !condition_Branch.equals(condition_Spilt) && !condition_Branch.equals("-1")) {
                targetTrackedFiles.put(fileName, branchTrackedFiles.get(fileName));
                //Utils.writeContents(join(obj2, branchTrackedFiles.get(fileName)), readContents(join(CWD, fileName)));
            }

            else if (condition_Spilt.equals("-1") && condition_Head.equals("-1") && !condition_Branch.equals("-1")) {
                targetTrackedFiles.put(fileName, branchTrackedFiles.get(fileName));
                //Utils.writeContents(join(obj2, branchTrackedFiles.get(fileName)), readContents(join(CWD, fileName)));
            }

            else if (!condition_Spilt.equals("-1") && condition_Head.equals(condition_Spilt) && condition_Branch.equals("-1")) {
                targetTrackedFiles.remove(fileName);
                stage.getRemoval().add(fileName);
            }

            else if (!condition_Spilt.equals("-1") && !condition_Head.equals(condition_Spilt) && !condition_Branch.equals(condition_Spilt)
                && !condition_Head.equals(condition_Branch)) {
                String newSha1 = jointFiles(condition_Head, condition_Branch);
                //Utils.writeContents(Utils.join(CWD, fileName), Utils.readContents(join(obj2, newSha1)));
                targetTrackedFiles.put(fileName, newSha1);
                hasConflict = true;
            }

            else if (!condition_Spilt.equals("-1") && (!condition_Head.equals(condition_Spilt)&& !condition_Head.equals("-1") && condition_Branch.equals("-1"))
            || (!condition_Branch.equals(condition_Spilt)&& !condition_Branch.equals("-1") && condition_Head.equals("-1"))) {
                String newSha1 = jointFiles(condition_Head, condition_Branch);
                //Utils.writeContents(Utils.join(CWD, fileName), Utils.readContents(join(obj2, newSha1)));
                targetTrackedFiles.put(fileName, newSha1);
                hasConflict = true;
            }

            else if (condition_Spilt.equals("-1") && !condition_Head.equals("-1") && !condition_Branch.equals("-1")
            && !condition_Head.equals(condition_Branch)) {
                String newSha1 = jointFiles(condition_Head, condition_Branch);
                //Utils.writeContents(Utils.join(CWD, fileName), Utils.readContents(join(obj2, newSha1)));
                targetTrackedFiles.put(fileName, newSha1);
                hasConflict = true;
            }
        }

        for (String fileName : stage.getRemoval()) {
        //    Utils.restrictedDelete(join(CWD, fileName));
            File file = join(CWD, fileName);
            file.delete();
        }

        if (hasConflict == true) {
            System.out.println("Encountered a merge conflict.");
        }

        String message = "Merged " + branchName + " into " + getHeadname() + ".";
        Commit targetCommit = new Commit(message, headSha1, branchSha1, targetTrackedFiles);
        updatePointer(getHeadname(), targetCommit.getSha1());
        Utils.writeObject(join(obj1, targetCommit.getSha1()), targetCommit);
        for (String updateFiles : targetTrackedFiles.keySet()) {
            Utils.writeContents(Utils.join(CWD, updateFiles), Utils.readContents(join(obj2, targetTrackedFiles.get(updateFiles))));
        }
        stage.clear();
    }

    public static void merge(String[] args) {
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        m_merge(args[1]);
    }
}
