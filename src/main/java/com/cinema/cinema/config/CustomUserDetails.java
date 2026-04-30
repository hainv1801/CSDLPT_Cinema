package com.cinema.cinema.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

public class CustomUserDetails extends User {

    private String maCoSo;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,
            String maCoSo) {
        super(username, password, authorities); // Gọi constructor của class cha (User)
        this.maCoSo = maCoSo;
    }

    public String getMaCoSo() {
        return maCoSo;
    }

    public void setMaCoSo(String maCoSo) {
        this.maCoSo = maCoSo;
    }
}