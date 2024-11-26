package co.inventorsoft.academy.schoolapplication.service.storage;

import co.inventorsoft.academy.schoolapplication.entity.enums.StorageType;
import co.inventorsoft.academy.schoolapplication.exception.FileStorageError;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.Instant;

@Service
@Slf4j
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GcsService implements ObjectStorageService {

    static String GOOGLE_STORAGE_CONNECTION_ERROR = "Failed to connect co GCS ";

    static String OBJECT_WAS_NOT_FOUND = "The object wasn't found";

    static String GOOGLE_STORAGE_UPLOADING_ERROR = "Could not upload GCS object";

    String storageBucket;

    Storage storage;

    public GcsService(@Value("${spring.cloud.gcp.credentials.bucketName}") String googleCloudStorageBucket,
                      Storage storage) {
        storageBucket = googleCloudStorageBucket;
        this.storage = storage;
    }

    public void uploadFile(InputStream inputStream, String objectName) {
        log.info("Started file uploading process");
        uploadFileImpl(inputStream, objectName);
        log.info("File {} was uploaded to GCS", objectName);
    }

    private void uploadFileImpl(InputStream inputStream, String fileName) {

        BlobId blobId = BlobId.of(storageBucket, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        Storage.BlobWriteOption precondition;
        if (storage.get(storageBucket, fileName) == null) {
            precondition = Storage.BlobWriteOption.doesNotExist();
        } else {
            precondition = Storage.BlobWriteOption.generationMatch(
                    storage.get(storageBucket, fileName).getGeneration());
        }
        try {
            storage.createFrom(blobInfo, inputStream, precondition);
        } catch (IOException e) {
            throw new FileStorageError(GOOGLE_STORAGE_CONNECTION_ERROR, e);
        }
    }

    public void deleteFile(String fileName) {

        log.info("Started file deleting process");
        Blob blob = storage.get(storageBucket, fileName);
        if (blob == null) {
            log.info("The object {} wasn't found in {} ", fileName, storageBucket);
            throw new FileStorageError(OBJECT_WAS_NOT_FOUND);
        }
        Storage.BlobSourceOption precondition = Storage.BlobSourceOption.generationMatch(blob.getGeneration());
        storage.delete(storageBucket, fileName, precondition);
        log.info("file {}  was deleted", fileName);
    }

    @Override
    public String generatePathForObjectStorage(StorageType storageType, Long ownerId, String fileName) {

        return Paths.get(storageType.name(), ownerId.toString(),
                Instant.now().toEpochMilli() + "_" + fileName).toString().replace("\\", "/");
    }

    @Override
    public InputStream downloadFile(String fileName) {

        log.info("Started file downloading process");
        byte[] content = storage.readAllBytes(storageBucket, fileName);
        if (content.length == 0) {
            log.info("The object {} wasn't found in {} ", fileName, storageBucket);
            throw new FileStorageError(OBJECT_WAS_NOT_FOUND);
        }
        return new ByteArrayInputStream(content);
    }
}
