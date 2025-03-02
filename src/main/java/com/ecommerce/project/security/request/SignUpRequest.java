package com.ecommerce.project.security.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Data
public class SignUpRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    private String email;

    @Getter
    @Setter
    private Set<String> role;

    @NotBlank
    @Size(min = 6, max = 50)
    private String password;

//    public Set<String> getRole(){
//        return this.role;
//    }
//
//    public void setRole(Set<String> role){
//        this.role = role;
//    }

}









