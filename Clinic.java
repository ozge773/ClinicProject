package clinic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a clinic with patients and doctors.
 * 
 */
public class Clinic {

	/**
	 * Add a new clinic patient.
	 * 
	 * @param first first name of the patient
	 * @param last last name of the patient
	 * @param ssn SSN number of the patient
	 */
	
	HashMap <String, Patient> patients = new HashMap<>();

	public void addPatient(String first, String last, String ssn) {
		Patient patient = new Patient(first, last, ssn);
		patients.put(ssn, patient);
		//System.out.println("patient added: "+patient.getPatientInfo());
	}


	/**
	 * Retrieves a patient information
	 * 
	 * @param ssn SSN of the patient
	 * @return the object representing the patient
	 * @throws NoSuchPatient in case of no patient with matching SSN
	 */
	
	public String getPatient(String ssn) throws NoSuchPatient {
		if(!patients.containsKey(ssn))
			throw new NoSuchPatient("Can't find the patient : " + ssn);
		Patient patient = patients.get(ssn);
		return patient.getPatientInfo();
		
	}

	/**
	 * Add a new doctor working at the clinic
	 * 
	 * @param first first name of the doctor
	 * @param last last name of the doctor
	 * @param ssn SSN number of the doctor
	 * @param docID unique ID of the doctor
	 * @param specialization doctor's specialization
	 */
	//it should be hashmap of the doctor id and doctor object 
	HashMap <Integer, Doctors> doctors = new HashMap<>();
//	Map <String, List<Integer>> Spec2Doc = new TreeMap<>();
	
	TreeMap <String, Integer> Spec2totPatients = new TreeMap<>();
	
	
	public void addDoctor(String first, String last, String ssn, int docID, String specialization) {

		Doctors doc = new Doctors(first, last, ssn, docID, specialization);
		doctors.put(docID, doc);
		//Spec2Doc TreeMap between the specialization and integer List of doctor ID's.
		
//		if (Spec2Doc.containsKey(specialization)) {
//		    List<Integer> doctor_list = Spec2Doc.get(specialization);
//		    doctor_list.add(docID);
//		} else {
//		    List<Integer> doctor_list = new ArrayList<>();
//		    doctor_list.add(docID);
//		    Spec2Doc.put(specialization, doctor_list);
//		}
	}

	/**
	 * Retrieves information about a doctor
	 * 
	 * @param docID ID of the doctor
	 * @return object with information about the doctor
	 * @throws NoSuchDoctor in case no doctor exists with a matching ID
	 */
	public String getDoctor(int docID) throws NoSuchDoctor {
		if(!doctors.containsKey(docID))
			throw new NoSuchDoctor(docID);
		Doctors myDoctor = doctors.get(docID);
		return myDoctor.getDocInfo();
		
		
	}
	
	/**
	 * Assign a given doctor to a patient
	 * 
	 * @param ssn SSN of the patient
	 * @param docID ID of the doctor
	 * @throws NoSuchPatient in case of not patient with matching SSN
	 * @throws NoSuchDoctor in case no doctor exists with a matching ID
	 */
	HashMap <String, Integer> patient2doc = new HashMap<>();
	Map<Integer, List<String>> doctor2patients = new TreeMap<>(Comparator.naturalOrder());
	
	public void assignPatientToDoctor(String ssn, int docID) throws NoSuchPatient, NoSuchDoctor {
		//does the patient exists among all the patients
		if(!patients.containsKey(ssn))
			throw new NoSuchPatient("Can't find the patient : " + ssn);
		//does the doctor  id exists among all the doctors
		if(!doctors.containsKey(docID))
			throw new NoSuchDoctor(docID);
		patient2doc.put(ssn, docID);	//if multiple times called  with different docID it will keep the last version
		//update the patients that doctor has add the patient to the doctors list of patients
		if (doctor2patients.containsKey(docID)) {
		    List<String> patients = doctor2patients.get(docID);
		    patients.add(ssn);
		    doctor2patients.put(docID, patients);
		    
		} else {
		    List<String> patients = new ArrayList<>();
		    patients.add(ssn);
		    doctor2patients.put(docID, patients);
		}

	} 

	/**
	 * Retrieves the id of the doctor assigned to a given patient.
	 * 
	 * @param ssn SSN of the patient
	 * @return id of the doctor
	 * @throws NoSuchPatient in case of not patient with matching SSN
	 * @throws NoSuchDoctor in case no doctor has been assigned to the patient
	 */
	public int getAssignedDoctor(String ssn) throws NoSuchPatient, NoSuchDoctor {
		//does the patient ever assigned to a doctor
		//if(!patient2doc.containsKey(ssn) && !patients.containsKey(ssn))
		if(!patients.containsKey(ssn))
			throw new NoSuchPatient(ssn);
			//throw new NoSuchDoctor();
		Integer id =patient2doc.get(ssn);
		if(patient2doc.get(ssn)==null || !doctors.containsKey(id))
			throw new NoSuchDoctor();// when the patient is not yet assigned to the doctor, should I print sth here
			//throw new NoSuchPatient(ssn);
		return patient2doc.get(ssn);
	}
	
	/**
	 * Retrieves the patients assigned to a doctor
	 * 
	 * @param id ID of the doctor
	 * @return collection of patient SSNs
	 * @throws NoSuchDoctor in case the {@code id} does not match any doctor 
	 */
	public Collection<String> getAssignedPatients(int id) throws NoSuchDoctor {
		//possibIlity : doctor totalLy doesn't exist
		//possibility doctor exists but never had a place in doctor2patientmap
		if(!doctors.containsKey(id))
			throw new NoSuchDoctor(id);
		//System.out.println("patients"+doctor2patients.get(id));
		if (doctor2patients.get(id) ==null) {
			//if doctors has no patient return empty list
			 List<String> emptyList = Collections.emptyList();  
			return emptyList;
			
		}
		else{
			return doctor2patients.get(id);
		}
	}
	
	/**
	 * Loads data about doctors and patients from the given stream.
	 * <p>
	 * The text file is organized by rows, each row contains info about
	 * either a patient or a doctor.</p>
	 * <p>
	 * Rows containing a patient's info begin with letter {@code "P"} followed by first name,
	 * last name, and SSN. Rows containing doctor's info start with letter {@code "M"},
	 * followed by badge ID, first name, last name, SSN, and speciality.<br>
	 * The elements on a line are separated by the {@code ';'} character possibly
	 * surrounded by spaces that should be ignored.</p>
	 * <p>
	 * In case of error in the data present on a given row, the method should be able
	 * to ignore the row and skip to the next one.<br>

	 * 
	 * @param reader reader linked to the file to be read
	 * @throws IOException in case of IO error
	 */
	public int loadData(Reader reader) throws IOException {
		return loadData(reader, l-> {});
	}


	/**
	 * Loads data about doctors and patients from the given stream.
	 * <p>
	 * The text file is organized by rows, each row contains info about
	 * either a patient or a doctor.</p>
	 * <p>
	 * Rows containing a patient's info begin with letter {@code "P"} followed by first name,
	 * last name, and SSN. Rows containing doctor's info start with letter {@code "M"},
	 * followed by badge ID, first name, last name, SSN, and speciality.<br>
	 * The elements on a line are separated by the {@code ';'} character possibly
	 * surrounded by spaces that should be ignored.</p>
	 * <p>
	 * In case of error in the data present on a given row, the method calls the
	 * {@link ErrorListener#offending} method passing the line itself,
	 * ignores the row, and skip to the next one.<br>
	 * 
	 * @param reader reader linked to the file to be read
	 * @param listener listener used for wrong line notifications
	 * @throws IOException in case of IO error
	 */
	
	public int loadData(Reader reader, ErrorListener listener) throws IOException {
	    int p_lines = 0;
	    try {
	        BufferedReader bufferedReader = new BufferedReader(reader);
	        String line;
	        
	        String Ppattern = "P\\s*;[A-Za-z]+\\s*;\\s*+[A-Za-z]+\\s*;\\s*[A-Za-z0-9]+\\s*";
	        String Mpattern = "M\\s*;\\s*[0-9]+\\s*;[A-Za-z]+\\s*;\\s*+[A-Za-z]+\\s*;\\s*[A-Za-z0-9]+\\s*;\\s*[A-Za-z]+\\s*";

	        while ((line = bufferedReader.readLine()) != null) {
	            line = line.trim();

	            if (Pattern.matches(Ppattern, line) || Pattern.matches(Mpattern, line)) {
	            	//load the patient or doctor the its data structure
	            	if(Pattern.matches(Ppattern, line)) {
	            		//add patient :P;Giovanni;Rossi; RSSGNN33B30F316I
	            		String[]new_line = line.split(";");
	            		String first = new_line[1].trim();
	            		String last =new_line[2].trim();
	            		String ssn = new_line[3].trim();
	            		addPatient(first, last, ssn);
	            	}else {
	            		//M;345;Mario;Bianchi;BNCMRA44C99A320Z;Surgeon
	            		//followed by badge ID, first name, last name, SSN, and specialization
	            		String[]l = line.split(";");
	            		int id = Integer.parseInt(l[1].trim());
	            		String first = l[2].trim();
	            		String last = l[3].trim();
	            		String ssn = l[4].trim();
	            		String spec = l[5].trim();
	            		addDoctor(first, last, ssn, id, spec);
	            	}
	                p_lines++;
	            }
	            else {
	            	listener.offending(line);
	            }
	        }

	        bufferedReader.close();
	    } catch (IOException ioe) {
	        ioe.printStackTrace();
	    }

	    return p_lines;
	}
	
	/**
	 * Retrieves the collection of doctors that have no patient at all.
	 * The doctors are returned sorted in alphabetical order
	 * 
	 * @return the collection of doctors' ids
	 */
	
//    List<Integer> unassignedDoctors = allDoctors.keySet().stream()
//            .filter(doctorId -> !doc2patient.containsKey(doctorId))
//            .toList();
	public Collection<Integer> idleDoctors(){
		List<Integer> myIdles = doctors.keySet().stream().filter(docId-> !doctor2patients.containsKey(docId)).collect(Collectors.toList());
		return myIdles;
	}

	/**
	 * Retrieves the collection of doctors having a number of patients larger than the average.
	 * 
	 * @return  the collection of doctors' ids
	 */
	public Collection<Integer> busyDoctors(){
		//sum of patients per each doctor and dividing that by the total number of doctors
		int totalDocs = doctor2patients.keySet().size();
		int sumPatientPerDoc =doctor2patients.values().stream().mapToInt(s ->1).sum();
		int avg  = sumPatientPerDoc / totalDocs;
		List<Integer> busy = doctor2patients.entrySet()
				                     .stream().filter(entry -> entry.getValue().size() > avg)
				                     .map(Map.Entry::getKey)
				                     .collect(Collectors.toList());
		//it is possible to create new method for this as it is not dependent on this metod

		return busy;
	}

	/**
	 * Retrieves the information about doctors and relative number of assigned patients.
	 * <p>
	 * The method returns list of strings formatted as "{@code ### : ID SURNAME NAME}" where {@code ###}
	 * represent the number of patients (printed on three characters).
	 * <p>
	 * The list is sorted by decreasing number of patients.
	 * 
	 * @return the collection of strings with information about doctors and patients count
	 */

	public Collection<String> doctorsByNumPatients(){

		List<Map.Entry<Integer, List<String>>> sortedDoctors = new ArrayList<>(doctor2patients.entrySet());
		Collections.sort(sortedDoctors, Comparator.comparingInt(e -> e.getValue().size()));
		Collections.reverse(sortedDoctors);
		List<String> toReturn = new ArrayList<>();
		for (Map.Entry<Integer, List<String>> entry : sortedDoctors) {
		    int doctorId = entry.getKey();
		    List<String> patients = entry.getValue();
		    int patientCount = patients.size();
		    
		    String numPatients = String.format("%3d", patientCount);
		    Doctors myDoctor = doctors.get(doctorId);
		    toReturn.add(numPatients+":"+myDoctor.getDocInfoStatistics(doctorId));
		}		
		Collection <Integer> idle = this.idleDoctors();
		for(int i : idle) {
			Doctors myDoctor = doctors.get(i);
			String numPatients = String.format("%3d", 0);
			toReturn.add(numPatients+":"+myDoctor.getDocInfoStatistics(i));
		}

		return toReturn;
	}
	
	/**
	 * Retrieves the number of patients per (their doctor's)  speciality
	 * <p>
	 * The information is a collections of strings structured as {@code ### - SPECIALITY}
	 * where {@code SPECIALITY} is the name of the speciality and 
	 * {@code ###} is the number of patients cured by doctors with such speciality (printed on three characters).
	 * <p>
	 * The elements are sorted first by decreasing count and then by alphabetic speciality.
	 * 
	 * @return the collection of strings with speciality and patient count information.
	 */

	public Collection<String> countPatientsPerSpecialization(){
		List<String> toReturnCp = new ArrayList<>();
		
		//it is possible to create new method for this as it is not dependent on this metod
		int prevValue;
		for(int ID :doctor2patients.keySet()) {

			Doctors myDoctor = doctors.get(ID);
			if(!Spec2totPatients.containsKey(myDoctor.ID2speciality())) {

				Spec2totPatients.put(myDoctor.ID2speciality(), doctor2patients.get(ID).size());
				prevValue  = doctor2patients.get(ID).size();
			}
			else {
				//speciality exist in the map increase the patients
				prevValue =Spec2totPatients.get(myDoctor.ID2speciality());

				Spec2totPatients.put(myDoctor.ID2speciality(), prevValue+doctor2patients.get(ID).size());
			}
		}
        // Sort the TreeMap based on values and keys
        TreeMap<String, Integer> sortedMap = Spec2totPatients.entrySet().stream()
                .sorted(Comparator.<java.util.Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue)
                        .reversed()
                        .thenComparing(Map.Entry::getKey))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, TreeMap::new));

	    for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
	            toReturnCp.add(String.format("%3d ", entry.getValue())+"-"+ entry.getKey());
	  
	        }
	    return toReturnCp;
		
		
	
	}

}
