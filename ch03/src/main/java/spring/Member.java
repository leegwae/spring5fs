package spring;

import java.time.LocalDateTime;

public class Member {

	private Long id;
	private String email;
	private String password;
	private String name;
	private LocalDateTime registerDateTime;
	
	public Member(String email, String password, String name, LocalDateTime registerDateTime) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.registerDateTime = registerDateTime;
	}
	
	void setId(Long id) { this.id = id; }
	public Long getId() { return id; }
	
	public String getEmail() { return email; }
	public String getpassword() { return password; }
	public String getName() { return name; }
	public LocalDateTime getRegisterDateTime() { return registerDateTime; }

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void changePassword(String oldPw, String newPw) {
		if (!password.equals(oldPw))
			throw new WrongIdPasswordException();
		
		setPassword(newPw);
	}

}
