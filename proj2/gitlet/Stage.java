package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.join;

public class Stage implements Serializable {
    private Map<String, String> addition = new HashMap<>();
    private Set<String> removal = new HashSet<>();

    public static final File STAGE = join(Repository.GITLET_DIR, "stage");

    public Stage() { };

    public boolean stageIsEmpty() {
        return (addition.isEmpty() && removal.isEmpty());
    }

    public void save() {
        Utils.writeObject(STAGE, this);
    }

    public static Stage load() {
        if (!STAGE.exists()) {
            return new Stage();
        }
        return Utils.readObject(STAGE, Stage.class);
    }

    public void add(String name, String s1) {
        addition.put(name, s1);
        removal.remove(name);
    }

    public void remove(String name) {
        removal.add(name);
    }

    public Map<String, String> getAddition() {
        return addition == null ? Collections.emptyMap() : addition;
    }

    public Set<String> getRemoval() {
        return removal == null ? Collections.emptySet() : removal;
    }

    public void clear() {
        addition.clear();
        removal.clear();
        save();
    }
}
