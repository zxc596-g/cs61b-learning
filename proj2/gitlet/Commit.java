package gitlet;
import java.io.Serializable;
import java.util.Date;

import java.util.HashMap;
import java.util.Map;


public class Commit implements Serializable {

    private String message;

    private String parent_1 = null;
    private String parent_2 = null;
    private Date timestamp;
    private String sha1 = " ";
    private Map<String,String> trackedFiles;

    private String getcommitsha1() {
        return Utils.sha1(
                this.message,
                this.parent_1 != null? parent_1: "",
                this.parent_2 != null? parent_2: "",
                this.timestamp.toString(),
                this.trackedFiles.toString()
        );
    }

    public Commit (String message, String parent1, String parent2, Map<String,String> trackedFiles) {
        this.message = message;
        this.parent_1 = parent1;
        this.parent_2 = parent2;
        this.trackedFiles = trackedFiles;
        this.timestamp = new Date();
        this.sha1 = getcommitsha1();
    }

    public Commit (String message, String parent1, Map<String,String> trackedFiles) {
        this (message, parent1, null, trackedFiles);
    }

    public Commit () {
        this.message = "initial commit";
        this.parent_2 = null;
        this.parent_1 = null;
        this.trackedFiles = new HashMap<>();
        this.timestamp = new Date(0);
        this.sha1 = getcommitsha1();
    }
}

