package spring;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.Collection;

public class MemberListPrinter {
	
	@Autowired
	private MemberManager memberManager;
	@Autowired
	private MemberPrinter printer;
	
	public MemberListPrinter() {

	}
	
	public void printAll() {
		Collection<Member> members = memberManager.selectAll();
		members.forEach(member -> printer.print(member));
	}
}
