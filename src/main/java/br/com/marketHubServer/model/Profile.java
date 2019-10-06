/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.marketHubServer.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author Tiago Albuquerque
 */
@Entity
public class Profile implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 30)
    private String type = "Profile";
    
    @ManyToOne
    private Image image;
    
    @Column(unique = true, nullable = false, length = 255)
    private String emailAddress;
    
    @JsonIgnore
    private String password;
    
    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String newPassword;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, length = 100)
    private String legalName;
    
    @Column(nullable = false, length = 400)
    private String description;
    
    @Column(nullable = false, length = 14)
    private String cnpjCpf;
    
    @Column(nullable = false, length = 11)
    private String phoneNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    @Temporal(TemporalType.TIMESTAMP)
    private Date create_date = new Date(System.currentTimeMillis());
    
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions;
    
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> permissions;
    
    @JsonIgnore
    @ManyToMany
    private List<Ad> ads;
    
    @JsonIgnore
    @ManyToMany
    private List<Marketplace> marketplaces;

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCnpjCpf() {
        return cnpjCpf;
    }

    public void setCnpjCpf(String cnpjCpf) {
        this.cnpjCpf = cnpjCpf;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }
    
    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public List<Ad> getAds() {
        return ads;
    }

    public void setAds(List<Ad> ads) {
        this.ads = ads;
    }

    public List<Marketplace> getMarketplaces() {
        return marketplaces;
    }

    public void setMarketplaces(List<Marketplace> marketplaces) {
        this.marketplaces = marketplaces;
    }
}
