package gedi.solutions.geode.spring.security.data;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class UserProfileDetails extends User
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -595503042970610169L;

	public UserProfileDetails(String username, String password, boolean enabled, boolean accountNonExpired,
	boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities)
	{
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
	}//------------------------------------------------
	public UserProfileDetails(String username, String password, Collection<? extends GrantedAuthority> authorities)
	{
		super(username, password, authorities);
	}//------------------------------------------------
	
    /**
	 * @return the email
	 */
	public String getEmail()
	{
		return email;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName()
	{
		return firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName()
	{
		return lastName;
	}
	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return title;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email)
	{
		this.email = email;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("UserProfileDetails [email=").append(email).append(", firstName=").append(firstName)
		.append(", lastName=").append(lastName).append(", title=").append(title).append(", toString()=")
		.append(super.toString()).append("]");
		return builder.toString();
	}



	private String email = "";
    private String firstName = "";
    private String lastName = "";  
    private String title = "";

}
