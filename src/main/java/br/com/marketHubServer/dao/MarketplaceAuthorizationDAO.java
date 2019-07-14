/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.marketHubServer.dao;

import br.com.marketHubServer.model.Marketplace;
import br.com.marketHubServer.model.MarketplaceAuthorization;
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
public interface MarketplaceAuthorizationDAO extends PagingAndSortingRepository<MarketplaceAuthorization, Integer> {
    @Query(value = "SELECT marketplace_authorization.id FROM marketplace_authorization "
                 + "JOIN profile_marketplaces "
                 + "ON profile_marketplaces.profile_id = marketplace_authorization.profile_id "
                 + "WHERE profile_marketplaces.profile_id = :profile_id AND profile_marketplaces.marketplaces_id = :marketplace_id ORDER BY marketplace_authorization.id DESC LIMIT 1", nativeQuery = true)
    public Integer findAuthorizationByProfileAndMarketplace(@Param("profile_id") Integer profile_id, @Param("marketplace_id") Integer marketplace_id);
}
