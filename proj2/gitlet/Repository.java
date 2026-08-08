package gitlet;
import java.io.File;
import static gitlet.Utils.*;

public class Repository {
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static final File OBJ = join(GITLET_DIR, "object");

    public static final File REFS = join(GITLET_DIR, "refs");


}
