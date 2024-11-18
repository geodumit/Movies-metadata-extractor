package org.example.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class FolderLister {

    private static final Logger logger = LogManager.getLogger(FolderLister.class);

    public File[] getFiles(String directoryPath){
        File directory = new File(directoryPath);
        return returnFiles(directory);
    }

    public File[] getFiles(File inputFile){
        return returnFiles(inputFile);
    }

    public File[] returnFiles(File inputFile){
        File[] files;
        if (inputFile.isDirectory()) {
            files = inputFile.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        System.out.println("Folder: " + file.getName());
                    }
                }
            } else {
                logger.error("The directory is empty or an error occurred");
            }
        } else {
            logger.error("The directory doesn't exist");
            return null;
        }
        return files;
    }
}
