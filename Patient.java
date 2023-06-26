package clinic;

public class Patient {
	String first, last, SSN;
	public Patient(String first, String last, String SSN) {
		this.first = first;
		this.last = last;
		this.SSN = SSN;
	}
	
	
	public String getPatientInfo() {
		//String sentence = String.format("%s %s (%s) [%s]:%s", last, first, SSN, docId, specialization);
		String sentence = String.format("%s %s (%s)", last, first, SSN);
		//return last+" "+first+" "+'('+SSN+')';
		return sentence;
		
	}
	

}
