package eus.martico.fileTransfer.seguridad.security;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import eus.martico.fileTransfer.seguridad.auth.api.UserAuthenticationService;
import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@Component
@Qualifier("TokenAuthenticationProvider")
@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class TokenAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
	@NonNull
	@Autowired
	@Qualifier("UserAuthenticationService")
	UserAuthenticationService auth;

	@Override
	protected void additionalAuthenticationChecks(final UserDetails d, final UsernamePasswordAuthenticationToken auth) {
		// Nothing to do
	}

	@Override
	protected UserDetails retrieveUser(final String username,
			final UsernamePasswordAuthenticationToken authentication) {
		final Object token = authentication.getCredentials();
		
		SegurUser user = Optional.ofNullable(token).map(String::valueOf).flatMap(auth::findByToken).orElseThrow(
				() -> new UsernameNotFoundException("Cannot find user with authentication token=" + token));
		/* Puede ser que el usuario haya hecho logout aunque el token sea valido */
//		if (auth.estaLogeado(user))
			return user;
//		else
//			throw new UsernameNotFoundException("El usuario no esta logeado");
	}
	
}