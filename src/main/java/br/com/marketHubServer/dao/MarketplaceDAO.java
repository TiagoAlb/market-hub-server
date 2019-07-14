/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.marketHubServer.dao;
import br.com.marketHubServer.model.Marketplace;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
/**
 *
 * @author Tiago Albuquerque
 */
@Repository
public interface MarketplaceDAO extends PagingAndSortingRepository<Marketplace, Integer> {
    //@Query(value = "SELECT * FROM marketplace JOIN profile_marketplaces ON profile_marketplaces.marketplaces_id = marketplace.id WHERE profile_marketplaces.profile_id = :id", nativeQuery = true)
    @Query("SELECT marketplace FROM Profile profile JOIN profile.marketplaces marketplace WHERE profile.id = :id")
    public Page<Marketplace> findByProfile(@Param("id") int id, Pageable pageable);
    @Query(value = "SELECT * FROM marketplace WHERE marketplace.id NOT IN (SELECT profile_marketplaces.marketplaces_id FROM profile_marketplaces WHERE profile_marketplaces.profile_id = :id) ORDER BY marketplace.name", nativeQuery = true)
    public List<Marketplace> findAvailable(@Param("id") int id);
}