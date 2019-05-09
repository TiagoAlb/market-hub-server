/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.marketHubServer.dao;
import br.com.marketHubServer.model.SystemAuthorization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
/**
 *
 * @author Tiago Albuquerque
 */
@Repository
public interface SystemAuthorizationDAO extends PagingAndSortingRepository<SystemAuthorization, Integer> {
    Page<SystemAuthorization> findById(int id, Pageable page);
    
}
