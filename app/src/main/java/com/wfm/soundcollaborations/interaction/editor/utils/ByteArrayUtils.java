package com.wfm.soundcollaborations.interaction.editor.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class ByteArrayUtils {

    public static byte[] toByteArray(String filePath) {

        File file = new File(filePath);
        //init array with file length
        byte[] byteArray = new byte[(int) file.length()];

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(byteArray); //read file into bytes[]
        } catch (IOException e) {
            System.err.println("----> Can not read files" + e); //TODO android logging bzw. pop up
            throw new RuntimeException(e);
        }

        return byteArray;
        //byte[] soundBytes = Files.readAllBytes(Paths.get(filePath)); //TODO when using mindsk 26
    }
}
