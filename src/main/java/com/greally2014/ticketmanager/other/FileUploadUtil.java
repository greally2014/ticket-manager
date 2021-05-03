package com.greally2014.ticketmanager.other;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;

public class FileUploadUtil {

    public static void saveFile(String uploadDir, String fileName, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        deleteDirectory(uploadPath);

        Files.createDirectories(uploadPath);

        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Could not save image file: " + fileName, e);
        }
    }

    public static void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path).map(Path::toFile).forEach(File::delete);
        }
    }
}
