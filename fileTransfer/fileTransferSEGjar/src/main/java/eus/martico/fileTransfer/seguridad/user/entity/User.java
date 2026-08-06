package eus.martico.fileTransfer.seguridad.user.entity;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class User implements UserDetails {
  private static final long serialVersionUID = 2396654715019746670L;

  public static final String USER_ID_KEY 	= "id";
  public static final String USERNIF_KEY 	= "nif";
  public static final String USER_NAME_KEY 	= "username";
  public static final String USER_PASSWORD_KEY = "password";
  public static final String USER_SCOPES 	= "scopes";	/* array de roles */

//  public static final String ROLE_USER 		= "ROLE_USER";
//  public static final String ROLE_ADMIN 	= "ROLE_ADMIN";
  
  String username;
  String password;
  String id;
  
  String nif;
  
  List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

  @JsonCreator
  public User(@JsonProperty(USER_ID_KEY) final String _id,
       @JsonProperty(USERNIF_KEY) final String _nif,
       @JsonProperty(USER_NAME_KEY) final String _name,
       @JsonProperty(USER_PASSWORD_KEY) final String _password
       )  {
    super();
    this.id = requireNonNull(_id);
    this.nif = requireNonNull(_nif);
    this.username = _name;
    this.password = _password;
  }
  
  @JsonCreator
  public User(@JsonProperty(USER_ID_KEY) final String _id,
       @JsonProperty(USERNIF_KEY) final String _nif
       )  {
    super();
    this.id = requireNonNull(_id);
    this.nif = requireNonNull(_nif);
    this.username = _nif;
    this.password = null;
  }

	@JsonIgnore
	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@JsonIgnore
	@Override
	public String getPassword() {
		return password;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public String getNif() {
		return nif;
	}
	public void addAuthority(GrantedAuthority _authority) {
		this.authorities.add(_authority);
	}

	public List<GrantedAuthority> getScopes() {
		if (this.authorities.isEmpty())
			return null;
		else {
			// devolvemos el primer autority
			return this.authorities;
		}
	}
	
	public Long getNi() throws NumberFormatException {
	    if (this.id==null)
	    	return null;
	    else {
	    	return Long.valueOf(this.id);
	    }

	}
}
