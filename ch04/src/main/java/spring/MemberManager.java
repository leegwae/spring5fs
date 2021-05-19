package spring;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemberManager {

	private static long nextId = 0;
	
	private Map<String, Member> members = new HashMap<>();
	
	public Member selectByEmail(String email) {
		return members.get(email);
	}
	
	public void insert(Member member) {
		String email = member.getEmail();

		member.setId(++nextId);
		members.put(email, member);
	}
	
	public void update(Member member) {
		String email = member.getEmail();

		members.put(email, member);
	}
	
	public Collection<Member> selectAll(){
		return members.values();
	}
}
