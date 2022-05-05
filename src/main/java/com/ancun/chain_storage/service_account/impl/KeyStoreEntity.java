package com.ancun.chain_storage.service_account.impl;

import javax.persistence.*;

@Entity
@Table(name = "keystore")
public class KeyStoreEntity {
    public KeyStoreEntity(String address, String password, byte[] data) {
        this.address = address;
        this.password = password;
        this.data = data;
    }

    public KeyStoreEntity() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String address;

    private String password;
    private byte[] data;
}
