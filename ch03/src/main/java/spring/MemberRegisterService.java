package spring;

import java.time.LocalDateTime;

public class MemberRegisterService {

	private MemberManager memberManager;

	public MemberRegisterService(MemberManager memberManager) {
		this.memberManager = memberManager;
	}
	
	public Long regist(RegisterRequest req) {
		String email = req.getEmail();
		Member member = memberManager.selectByEmail(email);
		
		if (member != null) {
			throw new DuplicateMemberException("dup email: " + email);
		}
		
		Member newMember = new Member(
				email, req.getPassword(), req.getName(), LocalDateTime.now());
		
		memberManager.insert(newMember);
		return newMember.getId();
	}
	
}
