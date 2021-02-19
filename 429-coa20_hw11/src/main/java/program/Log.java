package program;

import java.util.ArrayList;

public class Log {

	public static ArrayList<String> log = new ArrayList<>();

	public static void init() {
		log = new ArrayList<>();
	}

	public static void write(String instr) {
		log.add(instr);
	}

	public static boolean isSameWith(ArrayList<String> truth) {
		System.out.println("-----------" + truth.size() + " " + log.size());
		for (int i=0; i<truth.size(); i++) {
			System.out.println(truth.get(i));
		}
		System.out.println("--------------------");
		if (truth == null || log == null) {
			return false;
		}
		if (truth.size() != log.size()) {
			return false;
		}
		for (int i=0; i<log.size(); i++) {
			if (!log.get(i).equals(truth.get(i))) {
				System.out.println(truth.get(i) + "   " + log.get(i));
				return false;
			}
		}
		return true;
	}

}
