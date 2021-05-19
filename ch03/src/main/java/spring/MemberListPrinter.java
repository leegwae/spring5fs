package spring;

import java.util.Collection;


public class MemberListPrinter {
	
	private MemberManager memberManager;
	private MemberPrinter printer;
	
	public MemberListPrinter(MemberManager memberManager, MemberPrinter memberPrinter) {
		this.memberManager = memberManager;
		printer = memberPrinter;
	}
	
	public void printAll() {
		Collection<Member> members = memberManager.selectAll();
		members.forEach(member -> printer.print(member));
	}
}
