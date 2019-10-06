/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.marketHubServer.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 *
 * @author Tiago Albuquerque
 */
@Entity
public class Marketplace implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 100)
    private String name = "MarketPlace";
    
    @ManyToOne
    private Image image;
    
    @ManyToMany
    private List<EndPoint> endPoints;
    
    @OneToMany 
    @Cascade(value = {CascadeType.DELETE, CascadeType.SAVE_UPDATE})
    private List<MarketplaceAuthorization> authorizations;
    
    @JsonFormat(pattern = "yyyy-MM-dd : HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date link_date;

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public List<EndPoint> getEndPoints() {
        return endPoints;
    }

    public void setEndPoints(List<EndPoint> endPoints) {
        this.endPoints = endPoints;
    }

    public List<MarketplaceAuthorization> getAuthorizations() {
        return authorizations;
    }

    public void setAuthorization(List<MarketplaceAuthorization> authorization) {
        this.authorizations = authorization;
    }
    
    public Date getLink_date() {
        return link_date;
    }

    public void setLink_date(Date link_date) {
        this.link_date = link_date;
    }
}
