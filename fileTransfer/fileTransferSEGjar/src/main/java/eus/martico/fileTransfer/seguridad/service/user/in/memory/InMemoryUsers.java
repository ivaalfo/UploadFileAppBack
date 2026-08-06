package eus.martico.fileTransfer.seguridad.service.user.in.memory;

import static java.util.Optional.ofNullable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import eus.martico.fileTransfer.seguridad.service.user.LoginUserIService;
import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;

//@Service
final class InMemoryUsers implements LoginUserIService {

//	Map<String, SegurUser> users = new HashMap<>();
	private static Map<String, SegurUser> users = new ConcurrentHashMap<>();

	@Override
	public SegurUser save(final SegurUser user) {
		return save(user, false);
	}

	@Override
	public Optional<SegurUser> find(final String id) {
		return ofNullable(users.get(id));
	}

//	@Override
//	public Optional<SegurUser> findByNif(final String usernif) {
//		Optional<SegurUser> user = users
//				.values()
//				.stream()
//				.filter(u -> Objects.equals(usernif, u.getNif()))
//				.findFirst();
//
//		if (!user.isPresent()) {
//			// No esta en memoria lo buscamos en la BD
//			// acceso a BD
//			//user = save(user);
//		}
//
//		return user;
//	}

	@Override
	public void disable(String id) {
		users.remove(id);
	}

	@Override
	public SegurUser save(SegurUser _user, boolean _guardarEnBD) {
		return users.put(_user.getId(), _user);
	}

	@Override
	public boolean estaLogeado(String _id) {
		if (users.get(_id)==null)
			return false;
		else
			return true;
	}
}
