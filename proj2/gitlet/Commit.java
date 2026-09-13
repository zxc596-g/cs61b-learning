package gitlet;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;

import java.util.HashMap;
import java.util.Map;

import static gitlet.Repository.getHeadSha1;


public class Commit implements Serializable {

    private String message;

    private String parent_1 = null;
    private String parent_2 = null;
    private Date timestamp;
    private String sha1 = " ";
    private Map<String,String> trackedFiles;

    private static String formatDate(Date date) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", java.util.Locale.US);
        return sdf.format(date);
    }

    public String getMessage() {
        return this.message;
    }
    public String getTime() {
        return formatDate(this.timestamp);
    }
    public String getSha1() {
        return this.sha1;
    }

    private String getCommitSha1() {
        return Utils.sha1(
                this.message,
                this.parent_1 != null? parent_1: "",
                this.parent_2 != null? parent_2: "",
                this.getTime(),
                this.trackedFiles.toString()
        );
    }

    public Commit (String message, String parent1, String parent2, Map<String,String> trackedFiles) {
        this.message = message;
        this.parent_1 = parent1;
        this.parent_2 = parent2;
        this.trackedFiles = trackedFiles;
        this.timestamp = new Date();
        this.sha1 = getCommitSha1();
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
        this.sha1 = getCommitSha1();
    }

    public static Commit getCommit(String s1) {
        return Utils.readObject(Utils.join(Repository.obj1, s1), Commit.class);
    }

    public static Commit getHeadCommit() {
        String headSha1 = getHeadSha1();
        return getCommit(headSha1);
    }

    public Map<String, String> getTrackedFiles() {
        return trackedFiles == null ? Collections.emptyMap() : trackedFiles;
    }

    public String getParent_1() {
        return parent_1;
    }
    public String getParent_2() {
        return  parent_2;
    }
}