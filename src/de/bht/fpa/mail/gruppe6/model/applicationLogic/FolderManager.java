package de.bht.fpa.mail.gruppe6.model.applicationLogic;

import de.bht.fpa.mail.gruppe6.model.data.Folder;
import java.io.File;

/**
 *
 * @author Nessi
 */
public class FolderManager implements FolderManagerIF {

    private Folder baseFolder;

    public FolderManager(File file) {
        //if the give path is not a directory it can not be a root
        baseFolder = new Folder(file, true);
        loadContent(baseFolder);
    }

    @Override
    public void loadContent(Folder f) {
        if(f.getComponents().isEmpty()){
        File file = new File(f.getPath());
        for (File fs : file.listFiles()) {
            if(fs!=null){
            if (fs.isDirectory()) {
                f.addComponent(new Folder(fs, hasSubFolder(fs)));
            }
            }
        }
        }
    }

    private boolean hasSubFolder(File file) {
        if(file.listFiles()!=null){
        for (File x : file.listFiles()) {
            if (x.isDirectory()) {
                return true;
            }
        }}
       return false;
    }


    @Override
    public Folder getTopFolder() {
        return baseFolder;
    }
}
