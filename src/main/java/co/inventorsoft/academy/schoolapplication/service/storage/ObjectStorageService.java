package co.inventorsoft.academy.schoolapplication.service.storage;

import co.inventorsoft.academy.schoolapplication.entity.enums.StorageType;

import java.io.InputStream;

public interface ObjectStorageService {
    void uploadFile(InputStream inputStream, String fileName);

    InputStream downloadFile(String fileName);

    void deleteFile(String fileName);

    String generatePathForObjectStorage(StorageType storageType, Long ownerId, String fileName);
}
