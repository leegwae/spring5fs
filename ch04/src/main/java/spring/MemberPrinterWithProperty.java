package spring;

import org.springframework.beans.factory.annotation.Autowired;

public class MemberPrinterWithProperty {

	private MemberManager memberManger;
	private MemberPrinter printer;

	public void printByEmail(String email) {
		Member member = memberManger.selectByEmail(email);
		
		if (member == null) {
			System.out.println("데이터 없음!");
			return;
		}
		printer.print(member);
		System.out.println();
	}
	
	@Autowired
	public void setMemberManager(MemberManager memberManager) {
		this.memberManger = memberManager;
	}
	
	@Autowired
	public void setPrinter(MemberPrinter printer) {
		this.printer = printer;
	}
}
