package eus.martico.fileTransfer.seguridad.user.auth.map;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import eus.martico.fileTransfer.seguridad.auth.api.UserAuthenticationService;
import eus.martico.fileTransfer.seguridad.service.user.LoginUserIService;
import eus.martico.fileTransfer.seguridad.service.user.LoginUserServiceException;
import eus.martico.fileTransfer.seguridad.token.api.TokenServiceException;
import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@Service("UserAuthenticationService")
@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class UUIDAuthenticationService implements UserAuthenticationService {

	protected static Logger logger = LoggerFactory.getLogger(UUIDAuthenticationService.class);

	@NonNull
	LoginUserIService loginUser;

	@Override
	public Optional<String> login(final SegurUser _user) {
		final String uuid = UUID.randomUUID().toString();
		final SegurUser user = SegurUser
				.builder()
				.id(uuid)
				.nif(_user.getNif())
				.password(_user.getPassword())
				.build();

		try {
			loginUser.save(user);
		} catch (LoginUserServiceException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return Optional.of(uuid);
	}

	@Override
	public Optional<SegurUser> findByToken(final String token) {
		return loginUser.find(token);
	}

	@Override
	public void logout(final SegurUser user) {

	}

	@Override
	public Optional<String> refreshToken(SegurUser user) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public int getExpirationMin() {
		//No expira
		return -1;
	}

	@Override
	public boolean estaLogeado(SegurUser user) {
		//Retornara false si ha hecho logout
		return true;
	}

	@Override
	public Map<String, String> verifyToken(String token) throws TokenServiceException {
		return null;
	}

}

