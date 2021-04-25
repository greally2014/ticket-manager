package com.greally2014.ticketmanager.other;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUploadUtil {

    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Could not save image file: " + fileName, e);
        }
    }

    public static void deleteDirectoryJava(Path path)
            throws IOException {

        Files.walkFileTree(path,
                new SimpleFileVisitor<>() {

                    // delete directories or folders
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir,
                                                              IOException exc)
                            throws IOException {
                        Files.delete(dir);
                        System.out.printf("Directory is deleted : %s%n", dir);
                        return FileVisitResult.CONTINUE;
                    }

                    // delete files
                    @Override
                    public FileVisitResult visitFile(Path file,
                                                     BasicFileAttributes attrs)
                            throws IOException {
                        Files.delete(file);
                        System.out.printf("File is deleted : %s%n", file);
                        return FileVisitResult.CONTINUE;
                    }
                }
        );

    }
}
