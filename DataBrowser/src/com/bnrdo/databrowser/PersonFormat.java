package com.bnrdo.databrowser;

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
}
