package com.bnrdo.databrowser;

import java.util.Date;
import java.util.List;

import com.bnrdo.databrowser.domain.Person;

public class PersonFormat implements TableDataSourceFormat<Person>{
	public String getValueAt(int index, Person p){
		if(index == 0){
			return p.getFirstName();
		}else if(index == 1){
			return p.getLastName();
		}else if(index == 2){
			return p.getBirthDay().toString();
		}else if(index == 3){
			return Integer.toString(p.getAge());
		}else if(index == 4){
			return p.getOccupation();
		}
		
		throw new IllegalStateException();
	}

	public int getColumnCount() {
		return 5;
	}

	@Override
	public Person extractEntityFromList(List<String> list){
		Person p = new Person();
		p.setFirstName(list.get(0));
		p.setLastName(list.get(1));
		p.setBirthDay(new Date());
		p.setAge(Integer.parseInt(list.get(3)));
		/*try {
			p.setBirthDay(new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(list.get(3)));
		} catch (ParseException e) {
			e.printStackTrace();
		}*/
		p.setOccupation(list.get(4));
		return p;
	}
}
