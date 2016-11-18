package us.kbase.auth2.lib;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AuthConfig {

	//TODO TEST
	//TODO JAVADOC
	
	/* might want to have separate current config state classes and config
	 * change classes. Try with the conflated semantics for now.
	 */
	
	public static final ProviderConfig DEFAULT_PROVIDER_CONFIG =
			new ProviderConfig(false, false);
	
	public static final boolean DEFAULT_LOGIN_ALLOWED = false;
	
	public static final Map<TokenLifetimeType, Long>
			DEFAULT_TOKEN_LIFETIMES_MS;
	static {
		final Map<TokenLifetimeType, Long> m = new HashMap<>();
		m.put(TokenLifetimeType.EXT_CACHE,			   5 * 60 * 1000L);
		m.put(TokenLifetimeType.LOGIN,		14 * 24 * 60 * 60 * 1000L);
		m.put(TokenLifetimeType.DEV,		90 * 24 * 60 * 60 * 1000L);
		m.put(TokenLifetimeType.SERV, 99_999_999_999L * 24 * 60 * 60 * 1000L);
		DEFAULT_TOKEN_LIFETIMES_MS = Collections.unmodifiableMap(m);
	}

	public static enum TokenLifetimeType {
		LOGIN, DEV, SERV, EXT_CACHE;
	}
	
	public static class ProviderConfig {
		
		private final Boolean enabled;
		private final Boolean forceLinkChoice;
		
		public ProviderConfig(
				final Boolean enabled,
				final Boolean forceLinkChoice) {
			super();
			this.enabled = enabled;
			this.forceLinkChoice = forceLinkChoice;
		}

		public Boolean isEnabled() {
			return enabled;
		}

		public Boolean isForceLinkChoice() {
			return forceLinkChoice;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("ProviderConfig [enabled=");
			builder.append(enabled);
			builder.append(", forceLinkChoice=");
			builder.append(forceLinkChoice);
			builder.append("]");
			return builder.toString();
		}
	}
	
	private final Boolean loginAllowed;
	private final Map<String, ProviderConfig> providers;
	private final Map<TokenLifetimeType, Long> tokenLifetimeMS;
	
	public AuthConfig(
			final Boolean loginAllowed,
			Map<String, ProviderConfig> providers,
			Map<TokenLifetimeType, Long> tokenLifetimeMS) {
		// nulls indicate no value or no change depending on context
		if (providers == null) {
			providers = new HashMap<>();
		}
		for (final String p: providers.keySet()) {
			if (p == null || p.isEmpty()) {
				throw new IllegalArgumentException(
						"provider names cannot be null or empty");
			}
			if (providers.get(p) == null) {
				throw new NullPointerException(
						"Provider config cannot be null");
			}
		}
		if (tokenLifetimeMS == null) {
			tokenLifetimeMS = new HashMap<>();
		}
		for (final TokenLifetimeType t: tokenLifetimeMS.keySet()) {
			if (t == null) {
				throw new NullPointerException(
						"null key in token life time map");
			}
			if (tokenLifetimeMS.get(t) < 60 * 1000) {
				throw new IllegalArgumentException(
						"token lifetimes must be at least 60,000 ms");
			}
		}
		this.loginAllowed = loginAllowed;
		this.providers = Collections.unmodifiableMap(providers);
		this.tokenLifetimeMS = Collections.unmodifiableMap(tokenLifetimeMS);
	}

	public Boolean isLoginAllowed() {
		return loginAllowed;
	}

	public Map<String, ProviderConfig> getProviders() {
		return providers;
	}

	public Map<TokenLifetimeType, Long> getTokenLifetimeMS() {
		return tokenLifetimeMS;
	}
	
	public Long getTokenLifetimeMS(final TokenLifetimeType type) {
		if (!tokenLifetimeMS.containsKey(type)) {
			return DEFAULT_TOKEN_LIFETIMES_MS.get(type);
		}
		return tokenLifetimeMS.get(type);
	}
	
	public ProviderConfig getProviderConfig(final String provider) {
		if (!providers.containsKey(provider)) {
			throw new IllegalArgumentException("No such provider: " +
					provider);
		}
		return providers.get(provider);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AuthConfig [loginAllowed=");
		builder.append(loginAllowed);
		builder.append(", providers=");
		builder.append(providers);
		builder.append(", tokenLifetimeMS=");
		builder.append(tokenLifetimeMS);
		builder.append("]");
		return builder.toString();
	}
}