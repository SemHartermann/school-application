package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.entity.enums.StorageType;
import co.inventorsoft.academy.schoolapplication.exception.FileStorageError;
import co.inventorsoft.academy.schoolapplication.service.storage.ObjectStorageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileService {

    ObjectStorageService storageService;

    public String uploadFile(MultipartFile multipartFile, StorageType storageType, Long ownerId) {

        String fileName = storageService.generatePathForObjectStorage(storageType, ownerId, multipartFile.getOriginalFilename());
        storageService.uploadFile(transformMultipartFileToInputStream(multipartFile), fileName);
        return fileName;
    }

    private InputStream transformMultipartFileToInputStream(MultipartFile multipartFile) {

        try {
            return multipartFile.getInputStream();
        } catch (IOException e) {
            log.error("Error converting multipart file to InputStream", e);
            throw new FileStorageError("Error converting multipart file to InputStream");
        }
    }

    public InputStreamResource downloadFile(String fileName) {

        try (InputStream inputStream = storageService.downloadFile(fileName);) {
            return new InputStreamResource(inputStream);
        } catch (IOException ex) {
            throw new FileStorageError("Something went wrong to close InputStream");
        }

    }

    public void deleteFile(String fileName) {

        storageService.deleteFile(fileName);
    }
}
