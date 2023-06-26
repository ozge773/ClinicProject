package clinic;

public class Doctors {
	String first, last, SSN, specialization;
	Integer docId;
	
	
	

	public Doctors(String first, String last, String SSN, Integer docId, String specialization) {
		this.first = first;
		this.last = last;
		this.SSN = SSN;
		this.docId = docId;
		this.specialization = specialization;
	}
	//this is to achieve the property where a doctor can have a properties of patients
	private Patient patient;

	
	public String getDocInfo() {
		String sentence = String.format("%s %s (%s) [%s]:%s", last, first, SSN, docId, specialization);
		//return last+" "+first+" "+"("+SSN+")"+" "+"["+docId+"]"+":"+specialization;
		return sentence;
		
	}
	public String getDocInfoStatistics(Integer Id) {
		//ID SURNAME NAME
		//in the exam use this formatting method it is cleaner and less error prone
		//in the readme if it says (<>) dont foget to adda the () but you should not add<>
		String str = String.format("%s %s %s", Id, last, first);
		String up_str = str.toUpperCase();
		return up_str;
	}
	//return specialty 
	public String ID2speciality(){
		return specialization;
		
	}
    public Doctors(Patient patient) {
        this.patient = patient;
    }

}
