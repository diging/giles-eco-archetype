#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.core.service.impl;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ${package}.core.service.IFileService;
import ${groupId}.gilesecosystem.requests.ICompletedStorageRequest;
import ${groupId}.gilesecosystem.util.files.IFileStorageManager;
import ${groupId}.gilesecosystem.util.properties.IPropertiesManager;

@Service
public class FileService implements IFileService {

    @Autowired
    private IFileStorageManager fileStorageManager;
    
    @Autowired
    private IPropertiesManager propertiesManager;
    
    /* (non-Javadoc)
     * @see ${groupId}.gilesecosystem.carolus.core.linnaeus.impl.IPathService${symbol_pound}getStoragePath(${groupId}.gilesecosystem.requests.ICompletedStorageRequest)
     */
    @Override
    public String getStoragePath(ICompletedStorageRequest request) {
        File storageFolder = fileStorageManager.createFolder(request.getRequestId(), null, null, request.getDocumentId());
        return storageFolder.getAbsolutePath() + File.separator + request.getFilename();
    }
    
    @Override
    public byte[] getFileContent(String requestId, String documentId, String filename) {
        return fileStorageManager.getFileContent(requestId, documentId, null, filename);
    }
    
    @Override
    public void deleteFile(String requestId, String documentId, String filename) {
        fileStorageManager.deleteFile(requestId, documentId, null, filename, true);
    }
    
}
