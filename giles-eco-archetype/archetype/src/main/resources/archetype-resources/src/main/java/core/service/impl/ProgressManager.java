#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.core.service.impl;

import org.springframework.stereotype.Service;

import ${package}.core.service.IProgressManager;
import ${package}.core.service.ProgressPhase;
import ${groupId}.gilesecosystem.requests.IImageExtractionRequest;

@Service
public class ProgressManager implements IProgressManager {

    private int totalPages;
    private int currentPage;
    private IImageExtractionRequest currentRequest;
    private ProgressPhase phase;
    
    /* (non-Javadoc)
     * @see ${groupId}.gilesecosystem.cepheus.service.progress.impl.IProgressManager${symbol_pound}startNewRequest(${groupId}.gilesecosystem.requests.IImageExtractionRequest)
     */
    @Override
    public void startNewRequest(IImageExtractionRequest request) {
        this.currentRequest = request;
    }
    
    @Override
    public int getTotalPages() {
        return totalPages;
    }

    @Override
    public int getCurrentPage() {
        return currentPage;
    }

    /* (non-Javadoc)
     * @see ${groupId}.gilesecosystem.cepheus.service.progress.impl.IProgressManager${symbol_pound}setTotalPages(int)
     */
    @Override
    public void setTotalPages(int total) {
        this.totalPages = total;
    }
    
    /* (non-Javadoc)
     * @see ${groupId}.gilesecosystem.cepheus.service.progress.impl.IProgressManager${symbol_pound}updateCurrentPage(int)
     */
    @Override
    public void updateCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    @Override
    public ProgressPhase getPhase() {
        return phase;
    }

    @Override
    public void setPhase(ProgressPhase phase) {
        this.phase = phase;
    }

    @Override
    public IImageExtractionRequest getCurrentRequest() {
        return currentRequest;
    }
    
    @Override
    public void reset() {
        currentRequest = null;
        currentPage = 0;
        totalPages = 0;
        phase = ProgressPhase.IDLE;
    }
}
