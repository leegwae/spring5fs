package spring;

public class ChangePasswordService {

	private MemberManager memberManager;
	
	public void setMemberManager(MemberManager memberManager) {
		this.memberManager = memberManager;
	}
	
	public void changePassword(String email, String oldPw, String newPw) {
		Member member = memberManager.selectByEmail(email);
		
		if (member == null)
			throw new MemberNotFoundException();
		
		member.changePassword(oldPw, newPw);
		
		memberManager.update(member);
	}

}
