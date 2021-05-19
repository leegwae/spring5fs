package spring;

import org.springframework.beans.factory.annotation.Autowired;
public class ChangePasswordService {

	@Autowired
	private MemberManager memberManager;
	
	public void changePassword(String email, String oldPw, String newPw) {
		Member member = memberManager.selectByEmail(email);
		
		if (member == null)
			throw new MemberNotFoundException();
		
		member.changePassword(oldPw, newPw);
		
		memberManager.update(member);
	}

}
