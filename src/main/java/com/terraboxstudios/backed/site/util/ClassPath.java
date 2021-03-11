package com.terraboxstudios.backed.site.util;

import java.io.File;

public class ClassPath {
    private static ClassPath instance = null;
    private String webInfPath;

    private ClassPath() {
        File myClass = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());

        int packageSubFolder = getClass().getPackage().getName().replaceAll("[^.]", "").length() + 3;

        for(int i = 0; i < packageSubFolder; i++) {
            myClass = myClass.getParentFile();
        }

        this.webInfPath = myClass.getAbsolutePath().replaceAll("%20", " ") + File.separator + "web" + File.separator + "WEB-INF" + File.separator;
    }

    public static ClassPath getInstance() {
        if(instance == null){
            instance = new ClassPath();
        }
        return instance;
    }

    public String getWebInfPath() {
        return this.webInfPath;
    }

}