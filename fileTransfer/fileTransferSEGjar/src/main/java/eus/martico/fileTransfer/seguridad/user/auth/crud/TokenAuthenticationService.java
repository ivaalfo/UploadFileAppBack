package eus.martico.fileTransfer.seguridad.user.auth.crud;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

//import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import eus.martico.fileTransfer.seguridad.auth.api.UserAuthenticationService;
import eus.martico.fileTransfer.seguridad.model.JWTScopes;
import eus.martico.fileTransfer.seguridad.service.user.LoginUserIService;
import eus.martico.fileTransfer.seguridad.token.api.TokenService;
import eus.martico.fileTransfer.seguridad.token.api.TokenServiceException;
import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@Service("UserAuthenticationService")
@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class TokenAuthenticationService implements UserAuthenticationService {
	
	protected static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationService.class);

	@NonNull
	TokenService tokenService;
	@NonNull
	LoginUserIService loginUserService;

	@Override
	public Optional<String> login(final SegurUser _user) {
		// x Optional<User> user1 = users.findByName(username);
		Optional<SegurUser> user1 = loginUserService.find(_user.getId());
		Optional<SegurUser> user2 = user1.filter(user -> Objects.equals(_user.getPassword(), user.getPassword()));
		//    ImmutableMap<String, String> iMap = ImmutableMap.of("username", username);
		Map<String, Object> iMap = new HashMap<String, Object>();
		// x iMap.put("username", username);
		iMap.put(SegurUser.USER_NAME_KEY, _user.getUsername());
		iMap.put(SegurUser.USER_ID_KEY, _user.getId());
		//Metemos los roles
		Object o;
		if (_user.getAuthorities()!=null) {
			o = _user.getAuthorities().stream().map(s -> s.toString()).collect(Collectors.toList());
			iMap.put(SegurUser.USER_AUTHORITIES_KEY, o );
		}
//		if (_user.getAlmacenes()!=null) {
//			o = Arrays.asList(_user.getAlmacenes()).stream().map(s -> s.toString()).collect(Collectors.toList());
//			iMap.put(SegurUser.USER_ALMACENES_KEY, o );
//		}
//		if (_user.getTransportistas()!=null) {
//			o = Arrays.asList(_user.getTransportistas()).stream().map(s -> s.toString()).collect(Collectors.toList());
//			iMap.put(SegurUser.USER_TRANSPORTISTAS_KEY, o );
//		}
//		boolean esAdmin = _user.isAdmin();
//		boolean esSistema = _user.isSistema();	
//		for (GrantedAuthority rol: _user.getAuthorities()) {
//			if (rol.getAuthority()!=null) {
//				if (rol.getAuthority().equals(FileTransferConstantes.ROL_ADMIN_CODIGO)) {
//					esAdmin=true;
//				}
//				if (rol.getAuthority().equals(FileTransferConstantes.ROL_SISTEMA_CODIGO)) {
//					esSistema=true;
//				}
//			}
//		}
		/*
		 * Si el login es de SISTEMA generamos un token permanente 
		 */
		String token;
		if (_user.isSistema())
			token = tokenService.permanent(iMap);
		else
			token = tokenService.expiring(iMap);

		return user2.map(user -> token);
    
//		return users
//				.findByUsername(username)
//				.filter(user -> Objects.equals(password, user.getPassword()))
//				.map(user -> tokens.expiring(ImmutableMap.of("username", username)));
	}

	@Override
	public Optional<SegurUser> findByToken(final String token) {
//		Map<String, String> tokenverified = tokens.verify(token);
//		if (tokenverified!=null) {
//			String username = tokenverified.get("username");
//			Optional<User> user = users.findByUsername(username);
//		}
		@SuppressWarnings("unused")
		boolean isRefreshToken=false;

		String scopes = null;
//		String almacenes = null;
//		String transportistas = null;
		Map<String, String> datosToken = tokenService.verify(token);

		Optional<SegurUser> result = null;
		if (datosToken==null) {
			result = Optional.ofNullable(null);
		} else {
			scopes = (String) datosToken.get(SegurUser.USER_AUTHORITIES_KEY);
//			almacenes = (String) datosToken.get(SegurUser.USER_ALMACENES_KEY);
//			transportistas = (String) datosToken.get(SegurUser.USER_TRANSPORTISTAS_KEY);

			//Comprobamos si algun scope es REFESH_TOKEN
			if (!StringUtils.isEmpty(scopes) &&  scopes.contains(JWTScopes.REFRESH_TOKEN.authority())) {
				isRefreshToken = true;
			}
			/*Creamos un objeto User con la informacion del JWT */
			result = Optional
					.of( new SegurUser( datosToken.get(SegurUser.USER_ID_KEY), datosToken.get(SegurUser.USER_NAME_KEY) ) );
		}

		if (result.isPresent() /*&& !isRefreshToken*/) {
			SegurUser user = result.get();
			try {
				if (!StringUtils.isEmpty(scopes)) {
					// scopes devuelve un array de esta forma [ROLE1, ROLE2, ...]
					// usamos el JSONArray para trasformarlo en una List
					JSONArray jRoles = new JSONArray(scopes);
					List<Object> roles = jRoles.toList();  // [NOTARIO, ADMIN, ETC...]
					for (Object role: roles) {
						if (role!=null) {
							user.addAuthority(new SimpleGrantedAuthority(role.toString()));
						}
					}
				}
//				if (!StringUtils.isEmpty(almacenes)) {
//					JSONArray jAlms = new JSONArray(almacenes);
//					List<Object> alms = jAlms.toList();
//					List<String> lAlms = new ArrayList<String>();
//					for (Object alm: alms) {
//						if (alm!=null) {
//							lAlms.add(alm.toString());
//						}
//					}
//					user.setAlmacenes(lAlms.stream().toArray(String[]::new));
//				}
//				if (!StringUtils.isEmpty(transportistas)) {
//					JSONArray jTransp = new JSONArray(transportistas);
//					List<Object> transps = jTransp.toList();
//					List<String> lTransp = new ArrayList<String>();
//					for (Object transp: transps) {
//						if (transp!=null) {
//							lTransp.add(transp.toString());
//						}
//					}
//					user.setTransportistas(lTransp.stream().toArray(String[]::new));
//				}
			} catch (JSONException e) {
				logger.error(e.getMessage(), e);
			}		
		}


		/*	Quitamos el recoger el usuario del hashmap   
		 * {
		  Optional<String> idUser = Optional.of(datosToken) 
	      .map(map -> map.get(User.USER_ID_KEY));

		  result = idUser.flatMap(loginUser::find);
	  }
		 */      
		return result;

		/*	  return Optional
		      .of(tokens.verify(token)) 
		// x     .map(map -> map.get("username"))
		// x     .flatMap(users::findByUsername);
		      .map(map -> map.get(User.USER_ID_KEY))
		      .flatMap(loginUser::find);
		 */
	}

	@Override
	public void logout(final SegurUser user) {
		//Quitamos el usuario de la lista de users
		loginUserService.disable(user.getId());
	}

	@Override
	public Optional<String> refreshToken(final SegurUser _user) {
		Map<String, Object> iMap = new HashMap<String, Object>();
		iMap.put(SegurUser.USER_ID_KEY, _user.getId());
		iMap.put(SegurUser.USER_NAME_KEY, _user.getUsername());
		//Metemos los roles
		iMap.put(SegurUser.USER_AUTHORITIES_KEY, Arrays.asList(JWTScopes.REFRESH_TOKEN.authority()));   	  	
		return Optional.of(tokenService.refreshToken(iMap));
	}

	public int getExpirationMin() {
		return tokenService.getExpirationMin();
	}

	int getRefreshExpirationMin() {
		return tokenService.getRefreshExpirationMin();
	}

	@Override
	public boolean estaLogeado(SegurUser user) {
		return loginUserService.estaLogeado(user.getId());
	}
	@Override
	public Map<String, String> verifyToken(String _token) throws TokenServiceException {
		return tokenService.verifyWithException(_token);
	}

}
