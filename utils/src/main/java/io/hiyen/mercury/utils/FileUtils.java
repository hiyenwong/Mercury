package io.hiyen.mercury.utils;


import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;

/**
 * File Utils 文件工具包
 *
 * @author Hi Yen Wong
 * @date 2022/11/29 17:14
 */
public class FileUtils {
    private FileUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * @param multipartFile file
     * @return 32 md5
     */
    public static String getFileMd5(MultipartFile multipartFile) {
        try {
            byte[] bytes = multipartFile.getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] bytes1 = messageDigest.digest(bytes);
            return new BigInteger(1, bytes1).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param fileName 带路径
     * @return 32 md5
     */
    public static String getFileMd5(String fileName) {
        try (InputStream is = Files.newInputStream(Paths.get(fileName))) {
            return DigestUtils.md5Hex(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件，目录删除
     *
     * @param filePath
     * @throws IOException
     */

    public static void delete(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.walkFileTree(path,
                new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file,
                                                     BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir,
                                                              IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
    }

}
