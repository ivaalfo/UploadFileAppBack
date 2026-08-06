package eus.martico.fileTransfer.seguridad.user.entity;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eus.martico.fileTransfer.config.FileTransferConstantes;
import lombok.Builder;

@Builder
public class SegurUser implements UserDetails {

	private static final long serialVersionUID = -720105082126014288L;

	public static final String USERNI_KEY 	= "ni";
	public static final String USER_ID_KEY 	= "id";
	public static final String USERNIF_KEY 	= "nif";
	public static final String USER_NAME_KEY 	= "username";
	public static final String USER_PASSWORD_KEY = "password";
	public static final String USER_ROLES_KEY 	= "roles";	/* array de roles */
	public static final String USER_AUTHORITIES_KEY 	= "authorities";	/* array de authorities */
	
	
	Long ni;
	String id;
	String nif;
	String username;
	String password;
	String[] roles;
	
//	private String token;
	
	final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
	
	@JsonCreator
	public SegurUser(@JsonProperty(USER_ID_KEY) final String _id,
			@JsonProperty(USERNIF_KEY) final String _nif,
			@JsonProperty(USER_NAME_KEY) final String _name,
			@JsonProperty(USER_PASSWORD_KEY) final String _password){
		super();
		setId(requireNonNull(_id));
		this.nif = _nif;
		this.username = _name;
		this.password = _password;
	}
	
	@JsonCreator
	public SegurUser(@JsonProperty(USER_ID_KEY) final String _id,
			@JsonProperty(USER_NAME_KEY) final String _username){
		super();
		setId(requireNonNull(_id));
		this.nif = null;
		this.username = requireNonNull(_username);
		this.password = null;
	}
	
	@JsonCreator
	public SegurUser(@JsonProperty(USER_ID_KEY) final String _id,
			@JsonProperty(USERNIF_KEY) final String _nif,
			@JsonProperty(USER_NAME_KEY) final String _name,
			@JsonProperty(USER_PASSWORD_KEY) final String _password,
			@JsonProperty(USER_ROLES_KEY) final String... _roles){
		super();
		setId(requireNonNull(_id));
		//this.nif = requireNonNull(_nif);
		this.nif = _nif;
		this.username = _name;
		this.password = _password;
		//Se crean las authorities
		setRoles( _roles);
	}

	@JsonCreator
	public SegurUser(@JsonProperty(USERNI_KEY) final Long _ni,
			@JsonProperty(USER_ID_KEY) final String _id,
		    @JsonProperty(USERNIF_KEY) final String _nif,
		    @JsonProperty(USER_NAME_KEY) final String _name,
		    @JsonProperty(USER_PASSWORD_KEY) final String _password,
		    @JsonProperty(USER_ROLES_KEY) final String[] _roles
		    ){
		super();
		setNi(requireNonNull(_ni));
		//this.nif = requireNonNull(_nif);
		this.nif = _nif;
		this.username = _name;
		this.password = _password;
		//Se crean las authorities
		setRoles( _roles);
	}
	
	
	@JsonIgnore
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@JsonIgnore
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setRoles(String[] roles) {
		this.roles = roles;
		//Creamos las authorities
		if (roles!=null) {
			for (Object role: roles) {
				addAuthority(new SimpleGrantedAuthority(role.toString()));				
			}
		}
	}
	public String[] getRoles() {
		return roles;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	public void addAuthority(GrantedAuthority _authority) {
		this.authorities.add(_authority);
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	@Override
	public boolean isEnabled() {
		return true;
	}
	public Long getNi() {
		return ni;
	}
	public void setNi(final Long _ni) {
		this.ni = _ni;
		if (_ni!=null)
			this.id = _ni.toString();
	}

	public String getNif() {
		return nif;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}

	public String getId() {
		return id;
	}

	public void setId(String _id) {
		this.id = _id;
		try {
			this.ni = Long.valueOf(_id);
		} catch (NumberFormatException e) {
			this.ni=null;
		}
	}

	public boolean isAdmin() {
		return isRol(FileTransferConstantes.PROPERTY_KEY_ROL_ADMIN);
	}
	public boolean isSuper() {
//		return isRol(FileTransferConstantes.ROL_SUPER_CODIGO);
		return isRol(FileTransferConstantes.PROPERTY_KEY_ROL_ADMIN);
	}
	public boolean isSistema() {
//		return isRol(FileTransferConstantes.ROL_SISTEMA_CODIGO);
		return isRol(FileTransferConstantes.PROPERTY_KEY_ROL_ADMIN);
	}
	public boolean isRol(String _rol) {
		if (this.getAuthorities()!=null) {
			for (GrantedAuthority auth: this.getAuthorities()) {
				if (auth.getAuthority().equals(_rol)) {
					return true;
				}
			}			
		} else if (this.roles!=null) {
			for (String role: this.roles) {
				if (role.equals(_rol)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public String[] getArrayAuthorities() {
		return this.getAuthorities().stream().map( a -> a.getAuthority() ).toArray(String[]::new);
	}
	
//	@JsonIgnore
//	public String getToken() {
//		return token;
//	}
//
//	public void setToken(String token) {
//		this.token = token;
//	}
	
}
