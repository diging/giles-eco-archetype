#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.core.service;

import ${groupId}.gilesecosystem.requests.ICompletedStorageRequest;

public interface IFileService {

    String getStoragePath(ICompletedStorageRequest request);

    byte[] getFileContent(String requestId, String documentId, String filename);

    void deleteFile(String requestId, String documentId, String filename);


}