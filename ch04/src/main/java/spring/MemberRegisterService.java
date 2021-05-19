package spring;

import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;

public class MemberRegisterService {

	@Autowired
	private MemberManager memberManager;

	public MemberRegisterService() {
		
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
