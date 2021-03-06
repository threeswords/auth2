package us.kbase.auth2.service;

import java.util.Arrays;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import us.kbase.auth2.lib.Authentication;
import us.kbase.auth2.lib.ExternalConfig;
import us.kbase.auth2.lib.identity.IdentityProviderConfig;
import us.kbase.auth2.lib.identity.IdentityProviderSet;
import us.kbase.auth2.lib.storage.AuthStorage;
import us.kbase.auth2.lib.storage.exceptions.StorageInitException;
import us.kbase.auth2.lib.storage.mongo.MongoStorage;
import us.kbase.auth2.service.exceptions.AuthConfigurationException;

public class AuthBuilder {

	//TODO TEST
	//TODO JAVADOC
	
	private MongoClient mc;
	private Authentication auth;
	
	public AuthBuilder(
			final IdentityProviderSet identities,
			final AuthStartupConfig cfg,
			final ExternalConfig defaultExternalConfig)
			throws StorageInitException, AuthConfigurationException {
		if (cfg == null) {
			throw new NullPointerException("cfg");
		}
		if (defaultExternalConfig == null) {
			throw new NullPointerException("defaultExternalConfig");
		}
		mc = buildMongo(cfg);
		auth = buildAuth(identities, cfg, mc, defaultExternalConfig);
	}
	
	public AuthBuilder(
			final IdentityProviderSet identities,
			final AuthStartupConfig cfg,
			final ExternalConfig defaultExternalConfig,
			final MongoClient mc)
			throws StorageInitException, AuthConfigurationException {
		if (cfg == null) {
			throw new NullPointerException("cfg");
		}
		if (mc == null) {
			throw new NullPointerException("mc");
		}
		if (defaultExternalConfig == null) {
			throw new NullPointerException("defaultExternalConfig");
		}
		this.mc = mc;
		auth = buildAuth(identities, cfg, mc, defaultExternalConfig);
	}
	
	private MongoClient buildMongo(final AuthStartupConfig c) throws StorageInitException {
		//TODO ZLATER handle shards & replica sets
		try {
			if (c.getMongoUser().isPresent()) {
				final List<MongoCredential> creds = Arrays.asList(MongoCredential.createCredential(
						c.getMongoUser().get(), c.getMongoDatabase(), c.getMongoPwd().get()));
				// unclear if and when it's safe to clear the password
				return new MongoClient(new ServerAddress(c.getMongoHost()), creds);
			} else {
				return new MongoClient(new ServerAddress(c.getMongoHost()));
			}
		} catch (MongoException e) {
			LoggerFactory.getLogger(getClass()).error(
					"Failed to connect to MongoDB: " + e.getMessage(), e);
			throw new StorageInitException("Failed to connect to MongoDB: " + e.getMessage(), e);
		}
	}
	
	private Authentication buildAuth(
			final IdentityProviderSet identities,
			final AuthStartupConfig c,
			final MongoClient mc,
			final ExternalConfig defaultExternalConfig)
			throws StorageInitException, AuthConfigurationException {
		final MongoDatabase db;
		try {
			db = mc.getDatabase(c.getMongoDatabase());
		} catch (MongoException e) {
			LoggerFactory.getLogger(getClass()).error(
					"Failed to get database from MongoDB: " + e.getMessage(), e);
			throw new StorageInitException("Failed to get database from MongoDB: " +
					e.getMessage(), e);
		}
		//TODO TEST authenticate to db, write actual test with authentication
		final AuthStorage s = new MongoStorage(db);
		configureIdentityProviders(identities, c);
		return new Authentication(s, identities, defaultExternalConfig);
	}
	
	private void configureIdentityProviders(
			final IdentityProviderSet identities,
			final AuthStartupConfig c)
			throws AuthConfigurationException {
		for (final IdentityProviderConfig idc: c.getIdentityProviderConfigs()) {
			try {
				identities.configure(idc);
			} catch (IllegalArgumentException e) {
				throw new AuthConfigurationException(String.format(
						"Error registering identity provider %s: %s",
						idc.getIdentityProviderName(),  e.getMessage()), e);
			}
		}
	}

	public MongoClient getMongoClient() {
		return mc;
	}

	public Authentication getAuth() {
		return auth;
	}
	
}
