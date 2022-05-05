package com.ancun.chain_storage.service_metadata;

public interface MetadataService {
    String uploadFile(long tokenId, String fileName, byte[] fileData) throws Exception;

    byte[] downloadFile(String uri) throws Exception;

    String uploadMetadata(long tokenId, String metadataJson) throws Exception;

    String downloadMetadata(String uri) throws Exception;
}
