package com.greally2014.ticketmanager.utility;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Util {

    /**
     *
     * @param uploadDir directory to user's profile picture folder
     * @param fileName profile picture file name
     * @param file profile picture file data
     */
    public static void saveFile(String uploadDir, String fileName, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        deleteDirectory(uploadPath); // delete directory and recreate on each picture update

        Files.createDirectories(uploadPath); // create directory

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

    public static String determineTargetUrl(Authentication authentication) {

        Map<String, String> roleTargetUrlMap = new HashMap<>();
        roleTargetUrlMap.put("ROLE_GENERAL_MANAGER", "/projects/listAll");
        roleTargetUrlMap.put("ROLE_PROJECT_MANAGER", "/projects/listAll");
        roleTargetUrlMap.put("ROLE_DEVELOPER", "/tickets/listAll");
        roleTargetUrlMap.put("ROLE_SUBMITTER", "/tickets/listAll");

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {

            String authorityName = authority.getAuthority();
            if (roleTargetUrlMap.containsKey(authorityName)) {
                return roleTargetUrlMap.get(authorityName);
            }
        }

        throw new IllegalStateException();
    }
}
