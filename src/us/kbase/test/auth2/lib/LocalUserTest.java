package us.kbase.test.auth2.lib;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import static us.kbase.test.auth2.TestCommon.set;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import us.kbase.auth2.lib.DisplayName;
import us.kbase.auth2.lib.EmailAddress;
import us.kbase.auth2.lib.LocalUser;
import us.kbase.auth2.lib.NewLocalUser;
import us.kbase.auth2.lib.NewRootUser;
import us.kbase.auth2.lib.Role;
import us.kbase.auth2.lib.UserDisabledState;
import us.kbase.auth2.lib.UserName;
import us.kbase.test.auth2.TestCommon;

public class LocalUserTest {
	
	@Test
	public void equals() {
		EqualsVerifier.forClass(LocalUser.class).usingGetClass()
				.withIgnoredFields("canGrantRoles").verify();

	}
	
	@Test
	public void constructWithoutReset() throws Exception {
		final UserName un = new UserName("foo");
		final EmailAddress e = new EmailAddress("f@g.com");
		final DisplayName dn = new DisplayName("bar");
		final Set<Role> r = set(Role.CREATE_ADMIN);
		final Set<String> cr = set("foobar");
		final Date d = new Date();
		Thread.sleep(2);
		final Date ll = new Date();
		final UserDisabledState uds = new UserDisabledState(new UserName("who"), d);
		final byte[] pwd = "foobarbazb".getBytes(StandardCharsets.UTF_8);
		final byte[] salt = "wh".getBytes(StandardCharsets.UTF_8);
		
		final LocalUser lu = new LocalUser(un, e, dn, r, cr, d, ll, uds,
				pwd, salt, false, null);
		assertThat("incorrect password hash",
				new String(lu.getPasswordHash(), StandardCharsets.UTF_8), is("foobarbazb"));
		assertThat("incorrect password salt",
				new String(lu.getSalt(), StandardCharsets.UTF_8), is("wh"));
		assertThat("incorrect pwd reset", lu.isPwdResetRequired(), is(false));
		assertThat("incorrect reset date", lu.getLastPwdReset(), is((Date) null));
		
		//check that super() is called correctly
		assertThat("incorrect disable admin", lu.getAdminThatToggledEnabledState(),
				is(new UserName("who")));
		assertThat("incorrect created date", lu.getCreated(), is(d));
		assertThat("incorrect custom roles", lu.getCustomRoles(), is(set("foobar")));
		assertThat("incorrect disabled state", lu.getDisabledState(), is(new UserDisabledState(
				new UserName("who"), d)));
		assertThat("incorrect display name", lu.getDisplayName(), is(new DisplayName("bar")));
		assertThat("incorrect email", lu.getEmail(), is(new EmailAddress("f@g.com")));
		assertThat("incorrect enable toggle date", lu.getEnableToggleDate(), is(d));
		assertThat("incorrect grantable roles", lu.getGrantableRoles(), is(set(Role.ADMIN)));
		assertThat("incorrect identities", lu.getIdentities(), is(Collections.emptySet()));
		assertThat("incorrect last login", lu.getLastLogin(), is(ll));
		assertThat("incorrect disabled reason", lu.getReasonForDisabled(), is((String) null));
		assertThat("incorrect roles", lu.getRoles(), is(set(Role.CREATE_ADMIN)));
		assertThat("incorrect user name", lu.getUserName(), is(new UserName("foo")));
		assertThat("incorrect is disabled", lu.isDisabled(), is(false));
		assertThat("incorrect is local", lu.isLocal(), is(true));
		assertThat("incorrect is root", lu.isRoot(), is(false));
	}
	
	@Test
	public void constructWithReset() throws Exception {
		final UserName un = new UserName("foo");
		final EmailAddress e = new EmailAddress("f@g.com");
		final DisplayName dn = new DisplayName("bar");
		final Set<Role> r = Collections.emptySet();
		final Set<String> cr = Collections.emptySet();
		final Date d = new Date();
		final UserDisabledState uds = new UserDisabledState();
		final byte[] pwd = "foobarbaz1".getBytes(StandardCharsets.UTF_8);
		final byte[] salt = "we".getBytes(StandardCharsets.UTF_8);
		
		final LocalUser lu = new LocalUser(un, e, dn, r, cr, d, null, uds,
				pwd, salt, true, d);
		assertThat("incorrect password hash",
				new String(lu.getPasswordHash(), StandardCharsets.UTF_8), is("foobarbaz1"));
		assertThat("incorrect password salt",
				new String(lu.getSalt(), StandardCharsets.UTF_8), is("we"));
		assertThat("incorrect pwd reset", lu.isPwdResetRequired(), is(true));
		assertThat("incorrect reset date", lu.getLastPwdReset(), is(d));
		
		// super() already tested in the other construct method
	}
	
	@Test
	public void constructorFail() throws Exception {
		final byte[] pwd = "foobarbaz8".getBytes(StandardCharsets.UTF_8);
		final byte[] salt = "wo".getBytes(StandardCharsets.UTF_8);
		failConstruct(null, salt,
				new IllegalArgumentException("passwordHash missing or too small"));
		failConstruct("foobarbaz".getBytes(StandardCharsets.UTF_8), salt,
				new IllegalArgumentException("passwordHash missing or too small"));
		failConstruct(pwd, null, new IllegalArgumentException("salt missing or too small"));
		failConstruct(pwd, "f".getBytes(StandardCharsets.UTF_8),
				new IllegalArgumentException("salt missing or too small"));
	}
	
	private void failConstruct(
			final byte[] passwordHash,
			final byte[] salt,
			final Exception e) {
		try {
			new LocalUser(new UserName("foo"), new EmailAddress("e@g.com"),
					new DisplayName("bar"), Collections.emptySet(), Collections.emptySet(),
					new Date(), null, new UserDisabledState(), passwordHash, salt, false, null);
			fail("excpected exception");
		} catch (Exception got) {
			TestCommon.assertExceptionCorrect(got, e);
		}
	}
	
	@Test
	public void newLocalUser() throws Exception {
		final byte[] pwd = "foobarbaz8".getBytes(StandardCharsets.UTF_8);
		final byte[] salt = "wo".getBytes(StandardCharsets.UTF_8);
		final NewLocalUser lu = new NewLocalUser(new UserName("foo"), new EmailAddress("e@g.com"),
				new DisplayName("bar"), pwd, salt, false);
		
		assertThat("incorrect password hash",
				new String(lu.getPasswordHash(), StandardCharsets.UTF_8), is("foobarbaz8"));
		assertThat("incorrect password salt",
				new String(lu.getSalt(), StandardCharsets.UTF_8), is("wo"));
		assertThat("incorrect pwd reset", lu.isPwdResetRequired(), is(false));
		assertThat("incorrect reset date", lu.getLastPwdReset(), is((Date) null));
		
		//check that super() is called correctly
		assertThat("incorrect disable admin", lu.getAdminThatToggledEnabledState(),
				is((UserName) null));
		TestCommon.assertDateNoOlderThan(lu.getCreated(), 500);
		assertThat("incorrect custom roles", lu.getCustomRoles(), is(Collections.emptySet()));
		assertThat("incorrect disabled state", lu.getDisabledState(), is(new UserDisabledState()));
		assertThat("incorrect display name", lu.getDisplayName(), is(new DisplayName("bar")));
		assertThat("incorrect email", lu.getEmail(), is(new EmailAddress("e@g.com")));
		assertThat("incorrect enable toggle date", lu.getEnableToggleDate(), is((Date) null));
		assertThat("incorrect grantable roles", lu.getGrantableRoles(),
				is(Collections.emptySet()));
		assertThat("incorrect identities", lu.getIdentities(), is(Collections.emptySet()));
		assertThat("incorrect last login", lu.getLastLogin(), is((Date) null));
		assertThat("incorrect disabled reason", lu.getReasonForDisabled(), is((String) null));
		assertThat("incorrect roles", lu.getRoles(), is(Collections.emptySet()));
		assertThat("incorrect user name", lu.getUserName(), is(new UserName("foo")));
		assertThat("incorrect is disabled", lu.isDisabled(), is(false));
		assertThat("incorrect is local", lu.isLocal(), is(true));
		assertThat("incorrect is root", lu.isRoot(), is(false));
	}
	
	@Test
	public void newRootUser() throws Exception {
		final byte[] pwd = "foobarbaz8".getBytes(StandardCharsets.UTF_8);
		final byte[] salt = "wo".getBytes(StandardCharsets.UTF_8);
		final NewRootUser lu = new NewRootUser(new EmailAddress("e@g.com"),
				new DisplayName("bar"), pwd, salt);
		
		assertThat("incorrect password hash",
				new String(lu.getPasswordHash(), StandardCharsets.UTF_8), is("foobarbaz8"));
		assertThat("incorrect password salt",
				new String(lu.getSalt(), StandardCharsets.UTF_8), is("wo"));
		assertThat("incorrect pwd reset", lu.isPwdResetRequired(), is(false));
		assertThat("incorrect reset date", lu.getLastPwdReset(), is((Date) null));
		
		assertThat("incorrect disable admin", lu.getAdminThatToggledEnabledState(),
				is((UserName) null));
		TestCommon.assertDateNoOlderThan(lu.getCreated(), 500);
		assertThat("incorrect custom roles", lu.getCustomRoles(), is(Collections.emptySet()));
		assertThat("incorrect disabled state", lu.getDisabledState(), is(new UserDisabledState()));
		assertThat("incorrect display name", lu.getDisplayName(), is(new DisplayName("bar")));
		assertThat("incorrect email", lu.getEmail(), is(new EmailAddress("e@g.com")));
		assertThat("incorrect enable toggle date", lu.getEnableToggleDate(), is((Date) null));
		assertThat("incorrect grantable roles", lu.getGrantableRoles(),
				is(set(Role.CREATE_ADMIN)));
		assertThat("incorrect identities", lu.getIdentities(), is(Collections.emptySet()));
		assertThat("incorrect last login", lu.getLastLogin(), is((Date) null));
		assertThat("incorrect disabled reason", lu.getReasonForDisabled(), is((String) null));
		assertThat("incorrect roles", lu.getRoles(), is(set(Role.ROOT)));
		assertThat("incorrect user name", lu.getUserName(), is(UserName.ROOT));
		assertThat("incorrect is disabled", lu.isDisabled(), is(false));
		assertThat("incorrect is local", lu.isLocal(), is(true));
		assertThat("incorrect is root", lu.isRoot(), is(true));
	}
	
	@Test
	public void localUserToString() throws Exception {
		final UserName un = new UserName("foo");
		final EmailAddress e = new EmailAddress("f@g.com");
		final DisplayName dn = new DisplayName("bar");
		final Set<Role> r = set(Role.CREATE_ADMIN);
		final Set<String> cr = set("foobar");
		final Date d = new Date();
		Thread.sleep(2);
		final Date ll = new Date();
		final UserDisabledState uds = new UserDisabledState(new UserName("who"), d);
		final byte[] pwd = "foobarbazb".getBytes(StandardCharsets.UTF_8);
		final byte[] salt = "wh".getBytes(StandardCharsets.UTF_8);
		
		final LocalUser lu = new LocalUser(un, e, dn, r, cr, d, ll, uds,
				pwd, salt, false, d);
		
		assertThat("incorrect toString", lu.toString(), is(String.format(
				"LocalUser [passwordHash=[102, 111, 111, 98, 97, 114, 98, 97, 122, 98], " +
				"salt=[119, 104], forceReset=false, lastReset=%s, " +
				"getDisplayName()=DisplayName [name=bar], " +
				"getEmail()=EmailAddress [email=f@g.com], getUserName()=UserName [name=foo], " +
				"getRoles()=[CREATE_ADMIN], getCustomRoles()=[foobar], " +
				"getCreated()=%s, getLastLogin()=%s, " +
				"getDisabledState()=UserDisabledState [disabledReason=null, " +
				"byAdmin=UserName [name=who], time=%s]]", d.getTime(), d, ll, d.getTime())));
		
	}

}
