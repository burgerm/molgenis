package org.molgenis.security.usermanager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.support.QueryImpl;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.omx.auth.MolgenisGroup;
import org.molgenis.omx.auth.MolgenisGroupMember;
import org.molgenis.omx.auth.MolgenisUser;
import org.molgenis.security.SecurityUtils;
import org.molgenis.security.user.MolgenisUserDetailsService;
import org.molgenis.security.usermanager.UserManagerServiceImplTest.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(classes =
{ Config.class })
public class UserManagerServiceImplTest extends AbstractTestNGSpringContextTests
{
	private static Authentication AUTHENTICATION_PREVIOUS;

	@Configuration
	@EnableWebSecurity
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	static class Config extends WebSecurityConfigurerAdapter
	{
		@Bean
		public DataService dataService() throws DatabaseException
		{
			return mock(DataService.class);
		}

		@Bean
		public UserManagerService userManagerService() throws DatabaseException
		{
			return new UserManagerServiceImpl(dataService());
		}

		@Override
		protected UserDetailsService userDetailsService()
		{
			return mock(MolgenisUserDetailsService.class);
		}

		@Bean
		@Override
		public UserDetailsService userDetailsServiceBean() throws Exception
		{
			return userDetailsService();
		}

		@Bean
		@Override
		public AuthenticationManager authenticationManagerBean() throws Exception
		{
			return super.authenticationManagerBean();
		}
	}

	@Autowired
	private UserManagerService userManagerService;

	@Autowired
	private DataService dataService;

	@BeforeClass
	public void setUpBeforeClass()
	{
		AUTHENTICATION_PREVIOUS = SecurityContextHolder.getContext().getAuthentication();
	}

	@AfterClass
	public static void tearDownAfterClass()
	{
		SecurityContextHolder.getContext().setAuthentication(AUTHENTICATION_PREVIOUS);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void userManagerServiceImpl()
	{
		new UserManagerServiceImpl(null);
	}

	@Test
	public void getAllMolgenisUsersSu() throws DatabaseException
	{
		this.setSecurityContextSuperUser();
		this.userManagerService.getAllMolgenisUsers();
	}

	@Test(expectedExceptions = AccessDeniedException.class)
	public void getAllMolgenisUsersNonSu() throws DatabaseException
	{
		this.setSecurityContextNonSuperUserWrite();
		this.userManagerService.getAllMolgenisUsers();
	}

	@Test
	public void getAllMolgenisGroupsSu() throws DatabaseException
	{
		this.setSecurityContextSuperUser();
		this.userManagerService.getAllMolgenisGroups();
	}

	@Test(expectedExceptions = AccessDeniedException.class)
	public void getAllMolgenisGroups_Non_SU() throws DatabaseException
	{
		this.setSecurityContextNonSuperUserWrite();
		this.userManagerService.getAllMolgenisGroups();
	}

	@Test(expectedExceptions = AccessDeniedException.class)
	public void getGroupsWhereUserIsMemberNonUs() throws DatabaseException
	{
		this.setSecurityContextNonSuperUserWrite();
		this.userManagerService.getGroupsWhereUserIsMember(Integer.valueOf("1"));
	}

	@Test
	public void getGroupsWhereUserIsMemberSu() throws DatabaseException
	{
		this.setSecurityContextSuperUser();

		MolgenisUser user1 = when(mock(MolgenisUser.class).getId()).thenReturn(Integer.valueOf("1")).getMock();
		MolgenisGroup group20 = mock(MolgenisGroup.class);
		MolgenisGroup group21 = mock(MolgenisGroup.class);

		final MolgenisGroupMember molgenisGroupMemberOne = mock(MolgenisGroupMember.class);
		molgenisGroupMemberOne.setMolgenisGroup(group20);
		molgenisGroupMemberOne.setMolgenisUser(user1);

		final MolgenisGroupMember molgenisGroupMemberTwo = mock(MolgenisGroupMember.class);
		molgenisGroupMemberTwo.setMolgenisGroup(group21);
		molgenisGroupMemberTwo.setMolgenisUser(user1);

		when(dataService.findOne(MolgenisUser.ENTITY_NAME, 1)).thenReturn(user1);
		when(
				dataService.findAllAsList(MolgenisGroupMember.ENTITY_NAME,
						new QueryImpl().eq(MolgenisGroupMember.MOLGENISUSER, user1))).thenReturn(
				Arrays.<Entity> asList(molgenisGroupMemberOne, molgenisGroupMemberTwo));
		List<MolgenisGroup> groups = this.userManagerService.getGroupsWhereUserIsMember(1);

		assertEquals(groups.size(), 2);
	}

	@Test(expectedExceptions = AccessDeniedException.class)
	public void getUsersMemberInGroupNonUs() throws DatabaseException
	{
		this.setSecurityContextNonSuperUserWrite();
		this.userManagerService.getUsersMemberInGroup(Integer.valueOf("22"));
	}

	@Test
	public void getUsersMemberInGroup() throws DatabaseException
	{
		this.setSecurityContextSuperUser();

		MolgenisUser user1 = mock(MolgenisUser.class);
		when(user1.getId()).thenReturn(1);
		when(user1.getUsername()).thenReturn("Jonathan");

		MolgenisGroup group22 = when(mock(MolgenisGroup.class).getId()).thenReturn(Integer.valueOf("22")).getMock();

		MolgenisGroupMember molgenisGroupMember = mock(MolgenisGroupMember.class);
		when(molgenisGroupMember.getMolgenisUser()).thenReturn(user1);
		when(molgenisGroupMember.getMolgenisGroup()).thenReturn(group22);

		when(dataService.findOne(MolgenisGroup.ENTITY_NAME, 22)).thenReturn(group22);
		when(
				dataService.findAllAsList(MolgenisGroupMember.ENTITY_NAME,
						new QueryImpl().eq(MolgenisGroupMember.MOLGENISGROUP, group22))).thenReturn(
				Arrays.<Entity> asList(molgenisGroupMember));

		List<MolgenisUserViewData> users = this.userManagerService.getUsersMemberInGroup(22);
		assertEquals(users.size(), 1);
	}

	@Test(expectedExceptions = AccessDeniedException.class)
	public void getGroupsWhereUserIsNotMemberNonUs() throws DatabaseException
	{
		this.setSecurityContextNonSuperUserWrite();
		this.userManagerService.getGroupsWhereUserIsNotMember(Integer.valueOf("1"));
	}

	@Test
	public void getGroupsWhereUserIsNotMemberUs() throws DatabaseException
	{
		setSecurityContextSuperUser();

		MolgenisUser user1 = when(mock(MolgenisUser.class).getId()).thenReturn(1).getMock();
		MolgenisGroup group22 = when(mock(MolgenisGroup.class).getId()).thenReturn(22).getMock();
		MolgenisGroup group33 = when(mock(MolgenisGroup.class).getId()).thenReturn(33).getMock();
		MolgenisGroup group44 = when(mock(MolgenisGroup.class).getId()).thenReturn(44).getMock();

		MolgenisGroupMember molgenisGroupMember = mock(MolgenisGroupMember.class);
		when(molgenisGroupMember.getMolgenisUser()).thenReturn(user1);
		when(molgenisGroupMember.getMolgenisGroup()).thenReturn(group22);

		when(dataService.findOne(MolgenisUser.ENTITY_NAME, 1)).thenReturn(user1);

		when(
				dataService.findAllAsList(MolgenisGroupMember.ENTITY_NAME,
						new QueryImpl().eq(MolgenisGroupMember.MOLGENISUSER, user1))).thenReturn(
				Arrays.<Entity> asList(molgenisGroupMember));
		when(dataService.findAllAsList(MolgenisGroup.ENTITY_NAME, new QueryImpl())).thenReturn(
				Arrays.<Entity> asList(group22, group33, group44));

		List<MolgenisGroup> groups = this.userManagerService.getGroupsWhereUserIsNotMember(1);
		assertEquals(groups.size(), 2);
	}

	@Test
	public void addUserToGroupSu() throws NumberFormatException, DatabaseException
	{
		setSecurityContextSuperUser();
		this.userManagerService.addUserToGroup(Integer.valueOf("22"), Integer.valueOf("1"));
	}

	@Test(expectedExceptions = AccessDeniedException.class)
	public void addUserToGroupNonSu() throws NumberFormatException, DatabaseException
	{
		setSecurityContextNonSuperUserWrite();
		this.userManagerService.addUserToGroup(Integer.valueOf("22"), Integer.valueOf("1"));
	}

	@Test
	public void removeUserFromGroupSu() throws NumberFormatException, DatabaseException
	{
		setSecurityContextSuperUser();

		MolgenisUser user1 = when(mock(MolgenisUser.class).getId()).thenReturn(1).getMock();
		MolgenisGroup group22 = when(mock(MolgenisGroup.class).getId()).thenReturn(22).getMock();
		MolgenisGroupMember molgenisGroupMember = mock(MolgenisGroupMember.class);
		when(molgenisGroupMember.getMolgenisUser()).thenReturn(user1);
		when(molgenisGroupMember.getMolgenisGroup()).thenReturn(group22);

		when(dataService.findOne(MolgenisUser.ENTITY_NAME, 1)).thenReturn(user1);
		when(dataService.findOne(MolgenisGroup.ENTITY_NAME, 22)).thenReturn(group22);

		Query q = new QueryImpl().eq(MolgenisGroupMember.MOLGENISUSER, user1).and()
				.eq(MolgenisGroupMember.MOLGENISGROUP, group22);

		when(dataService.findAllAsList(MolgenisGroupMember.ENTITY_NAME, q)).thenReturn(
				Arrays.<Entity> asList(molgenisGroupMember));

		this.userManagerService.removeUserFromGroup(22, 1);
	}

	@Test(expectedExceptions = AccessDeniedException.class)
	public void removeUserFromGroupNonSu() throws NumberFormatException, DatabaseException
	{
		setSecurityContextNonSuperUserWrite();
		this.userManagerService.removeUserFromGroup(Integer.valueOf("22"), Integer.valueOf("1"));
	}

	private void setSecurityContextSuperUser()
	{
		Collection<? extends GrantedAuthority> authorities = Arrays
				.<SimpleGrantedAuthority> asList(new SimpleGrantedAuthority(SecurityUtils.AUTHORITY_SU));
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(null, null, authorities));
	}

	private void setSecurityContextNonSuperUserWrite()
	{
		Collection<? extends GrantedAuthority> authorities = Arrays.<SimpleGrantedAuthority> asList(
				new SimpleGrantedAuthority(SecurityUtils.AUTHORITY_PLUGIN_READ_PREFIX + "USERMANAGER"),
				new SimpleGrantedAuthority(SecurityUtils.AUTHORITY_PLUGIN_WRITE_PREFIX + "USERMANAGER"));
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(null, null, authorities));
	}
}
