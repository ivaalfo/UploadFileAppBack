package eus.martico.fileTransfer.seguridad.token.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.impl.compression.GzipCompressionCodec;
import lombok.experimental.FieldDefaults;

import org.springframework.beans.factory.annotation.Autowired;
//import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import eus.martico.fileTransfer.seguridad.date.service.DateService;
import eus.martico.fileTransfer.seguridad.token.api.TokenService;
import eus.martico.fileTransfer.seguridad.token.api.TokenServiceException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.impl.TextCodec.BASE64;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang.StringUtils.substringBeforeLast;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class JWTTokenService implements Clock, TokenService {
	
	private static final String DOT = ".";
	private static final GzipCompressionCodec COMPRESSION_CODEC = new GzipCompressionCodec();

	@Autowired
	DateService dates;

	String issuer;
	int expirationSec;
	int refreshExpirationMin;
	int clockSkewSec;
	String secretKey;

	JWTTokenService(final DateService dates,
			@Value("${jwt.issuer:MARTICO}") final String issuer,
			/* 86400 segundos son 1 dia */
			@Value("${jwt.expiration-sec:86400}") final int expirationSec,
			/* 10080 minutos son 7 dias */
			/* 1440 minutos son 1 dia */
			@Value("${jwt.refreshExpiration-min:1440}") final int refreshExpirationMin,
			@Value("${jwt.clock-skew-sec:300}") final int clockSkewSec,
			@Value("${jwt.secret:secretMARTICO}") final String secret) {
		super();
		this.dates = requireNonNull(dates);
		this.issuer = requireNonNull(issuer);
		this.expirationSec = requireNonNull(expirationSec);
		this.refreshExpirationMin = requireNonNull(refreshExpirationMin);
		this.clockSkewSec = requireNonNull(clockSkewSec);
		this.secretKey = BASE64.encode(requireNonNull(secret));
	}

	@Override
	public String permanent(final Map<String, Object> attributes) {
		return newToken(attributes, 0);
	}

	@Override
	public String expiring(final Map<String, Object> attributes) {
		return newToken(attributes, expirationSec);
	}

	private String newToken(final Map<String, Object> attributes, final int expiresInSec) {
		//final DateTime now = dates.now();
		final Date now = dates.now();
		final Claims claims = Jwts
				.claims()
				.setIssuer(issuer)
				//.setIssuedAt(now.toDate());
				.setIssuedAt(now);

		if (expiresInSec > 0) {
			//final DateTime expiresAt = now.plusSeconds(expiresInSec);
			// Sumarle al date los segundos
			Calendar cal = Calendar.getInstance();
			cal.setTime(now);
			cal.add(Calendar.SECOND, expiresInSec);
			//final Date expiresAt = now.plusSeconds(expiresInSec);
			final Date expiresAt = cal.getTime();

			claims.setExpiration(expiresAt);
		}
		claims.putAll(attributes);

		return Jwts
				.builder()
				.setClaims(claims)
				.signWith(HS256, secretKey)
				.compressWith(COMPRESSION_CODEC)
				.compact();
	}

	@Override
	public Map<String, String> verify(final String token) {
		final JwtParser parser = Jwts
				.parser()
				.requireIssuer(issuer)
				.setClock(this)
				.setAllowedClockSkewSeconds(clockSkewSec)
				.setSigningKey(secretKey);
		return parseClaims(() -> parser.parseClaimsJws(token).getBody());
	}
	
	public Map<String, String> verifyWithException(final String token) 
			throws TokenServiceException {
		final JwtParser parser = Jwts
				.parser()
				.requireIssuer(issuer)
				.setClock(this)
				.setAllowedClockSkewSeconds(clockSkewSec)
				.setSigningKey(secretKey);
		try {
			return parseClaimsWithException(() -> parser.parseClaimsJws(token).getBody());
		} catch (Exception e) {
			throw new TokenServiceException(e);
		}
	}

	@Override
	public Map<String, String> untrusted(final String token) {
		final JwtParser parser = Jwts
				.parser()
				.requireIssuer(issuer)
				.setClock(this)
				.setAllowedClockSkewSeconds(clockSkewSec);

		// See: https://github.com/jwtk/jjwt/issues/135
		final String withoutSignature = substringBeforeLast(token, DOT) + DOT;
		return parseClaims(() -> parser.parseClaimsJwt(withoutSignature).getBody());
	}

	private static Map<String, String> parseClaims(final Supplier<Claims> toClaims) {
		try {
			final Claims claims = toClaims.get();
			final Map<String, String> map = new HashMap<String, String>();
			//final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
			for (final Map.Entry<String, Object> e: claims.entrySet()) {
				//builder.put(e.getKey(), String.valueOf(e.getValue()));
				map.put(e.getKey(), String.valueOf(e.getValue()));
			}
			//return builder.build();
			return map;
		} catch (final IllegalArgumentException e) {
			return null;
		} catch (final ExpiredJwtException e) {
			return null;
		} catch (final MalformedJwtException e) {
			return null;
		} catch (final JwtException e) {
			return null;
		}
	}

	private static Map<String, String> parseClaimsWithException(final Supplier<Claims> toClaims) 
			throws ExpiredJwtException, MalformedJwtException, JwtException {
		try {
			final Claims claims = toClaims.get();
			final Map<String, String> map = new HashMap<String, String>();
			//final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
			for (final Map.Entry<String, Object> e: claims.entrySet()) {
				//builder.put(e.getKey(), String.valueOf(e.getValue()));
				map.put(e.getKey(), String.valueOf(e.getValue()));
			}
			//return builder.build();
			return map;
		} catch (final IllegalArgumentException e) {
			throw e;
		} catch (final ExpiredJwtException e) {
			throw e;
		} catch (final MalformedJwtException e) {
			throw e;
		} catch (final JwtException e) {
			throw e;
		}
	}

	@Override
	public Date now() {
		//final DateTime now = dates.now();
		//final Date now = dates.now();
		return Calendar.getInstance().getTime();
	}

	@Override 
	public String refreshToken(final Map<String, Object> attributes) {
		return newRefreshToken(attributes, this.refreshExpirationMin);
	}

	public String newRefreshToken(final Map<String, Object> _attributes, final int _refreshExpirationMin) {

		final Date now = dates.now();
		LocalDateTime currentTime = LocalDateTime.now();
		final Claims claims = Jwts
				.claims()
				.setIssuer(issuer)
				.setIssuedAt(now);

		claims.putAll(_attributes);

		String refreshToken = Jwts.builder()
				.setClaims(claims)
				.setId(UUID.randomUUID().toString())
				.setExpiration(Date.from(currentTime
						.plusMinutes(_refreshExpirationMin)
						.atZone(ZoneId.systemDefault()).toInstant()))
				.signWith(HS256, secretKey)
				.compressWith(COMPRESSION_CODEC)
				.compact();

		return refreshToken;

	}

	public int getExpirationMin() {
		return Math.floorDiv(expirationSec,60);
	}

	public int getRefreshExpirationMin() {
		return refreshExpirationMin;
	}

}