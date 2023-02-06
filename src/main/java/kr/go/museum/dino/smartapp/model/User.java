package kr.go.museum.dino.smartapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Entity
@DynamicUpdate
@Builder // save
@Table(name = "tb_user")
public class User implements UserDetails, Serializable {
	
	@Id
	@Column(name="email")
	private String email;
	private String password;
	private String name;
	private String phone;
	@Column(name = "login_yn", insertable=false)
	private Boolean loginYn;
	@Column(name = "del_yn", insertable=false)
	private Boolean delyn;
	@Column(name = "reg_date", updatable=false)
	private String regDate;
	@Column(name = "mod_date", insertable=false)
	private String modDate;
	@Column(name = "admin_yn", insertable=false)
	private Boolean adminYn;
	@Column(name = "check_yn", insertable=false)
	private Boolean checkYn;

	@Transient
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();
    
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
	}
	@Override
	public String getUsername() {
        return email;
	}
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
}
