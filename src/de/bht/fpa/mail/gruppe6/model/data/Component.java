package de.bht.fpa.mail.gruppe6.model.data;

import java.io.File;
import java.util.List;

public abstract class Component {
    private String path;
    private String name;
    
    public Component(File path) {
        this.path = path.getAbsolutePath();
        this.name = path.getName();
        
    }

    public void addComponent(Component comp) {
        throw new UnsupportedOperationException("Not supported."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Component> getComponents() {
        throw new UnsupportedOperationException("Not supported."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public abstract boolean isExpandable();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String p) {
        path = p;
    }

    public String getPath() {
        return path;
    }

    public String toString() {
        return name;
    }
}
