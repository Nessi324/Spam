package de.bht.fpa.mail.gruppe6.model.applicationLogic;

import de.bht.fpa.mail.gruppe6.model.data.Folder;

/**
 *
 * @author Nessi
 */
public interface FolderManagerIF {

    public void loadContent(Folder f);

    public Folder getTopFolder();
}
