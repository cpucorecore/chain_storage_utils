package com.ancun.chain_storage.service_metadata.impl;

import com.ancun.chain_storage.service_metadata.MetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.ancun.chain_storage.util.CommonUtils.sha256InString;

@Service
public class LocalFileMetadataService implements MetadataService {

    @Value("${nft.metadata.RootDirectory}")
    private String nftMetadataRootDirectory;

    private Logger logger = LoggerFactory.getLogger(LocalFileMetadataService.class);

    @Override
    public String uploadFile(long tokenId, String fileName, byte[] fileData) throws Exception {
        String sha256 = sha256InString(fileData);
        Path fileURI = Paths.get(String.valueOf(tokenId), sha256, fileName);
        Path path = preparePath(fileURI);
        Files.write(path, fileData);
        return fileURI.toString();
    }

    @Override
    public byte[] downloadFile(String uri) throws Exception {
        return new byte[0];
    }

    @Override
    public String uploadMetadata(long tokenId, String metadataJson) throws Exception {
        String sha256 = sha256InString(metadataJson);
        Path tokenURI = Paths.get(String.valueOf(tokenId), sha256, "metadata.json");
        Path path = preparePath(tokenURI);
        Files.write(path, metadataJson.getBytes());
        return tokenURI.toString();
    }

    @Override
    public String downloadMetadata(String uri) throws Exception {
        return null;
    }

    private Path preparePath(Path fileURI) throws Exception {
        Path path = Paths.get(nftMetadataRootDirectory, fileURI.toString());
        File dir = path.toFile().getParentFile();
        if (!dir.exists()) {
            boolean mkdirsOk = dir.mkdirs();
            if (!mkdirsOk) {
                throw new Exception("create dir[" + dir.getAbsolutePath() + "] failed");
            }
        }

        return path;
    }
}
