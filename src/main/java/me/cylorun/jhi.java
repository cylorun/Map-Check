package me.cylorun;

import java.io.File;
import java.io.IOException;

public class jhi {
    public static void main(String[] args) {
        try {
            FileUtil.unFolderInAFolder(new File("C:\\Users\\alfgr\\Documents\\Wondershare\\BP_3.14.0"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
