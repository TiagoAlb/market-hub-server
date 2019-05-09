/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.marketHubServer.dao;
import br.com.marketHubServer.model.Marketplace;
import br.com.marketHubServer.model.Profile;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
/**
 *
 * @author Tiago Albuquerque
 */
@Repository
public interface ProfileDAO extends PagingAndSortingRepository<Profile, Integer> {
    public Profile findByEmailAddress(String email);
    @Query(value = "SELECT marketplace.id FROM marketplace JOIN profile_marketplaces ON profile_marketplaces.marketplaces_id = marketplace.id WHERE marketplace.id = :marketplaceID AND profile_marketplaces.profile_id = :profileID", nativeQuery = true)
    public Marketplace findMarketplaceByProfile(@Param("profileID") int profileID, @Param("marketplaceID") int marketplaceID);
}
