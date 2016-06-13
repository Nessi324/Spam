package de.bht.fpa.mail.gruppe6.model.applicationLogic;

import de.bht.fpa.mail.gruppe6.model.data.Folder;
import java.io.File;

public class FolderManager {

    private Folder folder;

    public FolderManager(File file) {
        if (file != null) {
            folder = new Folder(file, true);
            loadContent(folder);
        }
    }

    public void loadContent(Folder f) {
        if (f != null && f.getComponents().isEmpty()) {
            File file = new File(f.getPath());
            if (!file.isHidden() && !file.getPath().startsWith(".")) {
                for (File fs : file.listFiles()) {
                    if (fs.isDirectory() && !fs.isHidden()) {
                        f.addComponent(new Folder(fs, hasSubFolder(fs)));
                    }

                }
            }
        }
    }

    private boolean hasSubFolder(File file) {
        if (file.listFiles() != null) {
            for (File x : file.listFiles()) {
                if (x.isDirectory()) {
                    return true;
                }
            }
        }
        return false;
    }

    public Folder getTopFolder() {
        return folder;
    }
}
