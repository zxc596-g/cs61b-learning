package gitlet;
import java.io.File;
import java.io.Serializable;

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

    public static String createlog(Commit target) {
        String current = "===\n" + "Commit " + target.getsha1() + "\n"
                + "Date: " + target.gettime() + "\n"
                + target.getMessage() + "\n" + "\n";
        return current;
    }
    private static boolean PointerIsExist(String name) {
        File current = join(REFS, "heads", name);
        if(current.exists()) {
            return true;
        }
        return false;
    }

    public static void updatePointer(String name, String commit_sha1) {
        if(!PointerIsExist(name)) {
            System.out.println("pointer is not exist!");
            return;
        }
        File current = join(REFS, "heads", name);
        Utils.writeContents(current, commit_sha1);
    }

    public static void init() {
        if(GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        if(!CWD.exists()) {
            CWD.mkdir();
        }
        GITLET_DIR.mkdir();
        OBJ.mkdir();
        REFS.mkdir();
        STAGE.mkdir();
        heads.mkdir();
        Map.mkdir();
        obj1.mkdir();
        obj2.mkdir();

        Commit initcommit = new Commit();
        String log = createlog(initcommit);
        Utils.writeContents(LOG, log);
        Utils.writeContents(MASTER, initcommit.getsha1());
        Utils.writeContents(HEAD, "refs/heads/master");
    }

}
