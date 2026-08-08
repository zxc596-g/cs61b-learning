package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {
    private String sha1;
    private byte[] content;

    public Blob() {
        sha1 = null;
        content = null;
    }

    public Blob(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("file does not exist");
        }
        content = Utils.readContents(file);
        sha1 = Utils.sha1(this.content);
    }
}
