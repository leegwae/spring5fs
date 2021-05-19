package assembler;

import spring.MemberManager;
import spring.MemberRegisterService;
import spring.ChangePasswordService;

public class Assembler {
	private MemberManager memberManager;
	private MemberRegisterService memberRegisterService;
	private ChangePasswordService changePasswordService;
	
	public Assembler() {
		memberManager = new MemberManager();
		memberRegisterService = new MemberRegisterService(memberManager);
		changePasswordService = new ChangePasswordService();
		changePasswordService.setMemberManager(memberManager);
	}
	
	public MemberManager getMemberManager() {
		return memberManager;
	}
	
	public MemberRegisterService getMemberRegisterService() {
		return memberRegisterService;
	}
	
	public ChangePasswordService getChangePasswordService() {
		return changePasswordService;
	}

}
