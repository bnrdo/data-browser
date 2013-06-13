package com.bnrdo.databrowser.format;

import java.util.Date;
import java.util.List;

import com.bnrdo.databrowser.DBroUtil;
import com.bnrdo.databrowser.TableDataSourceFormat;
import com.bnrdo.databrowser.domain.Person;
import com.bnrdo.databrowser.domain.Logs;

public class LogsFormat implements TableDataSourceFormat<Logs>{

	@Override
	public String getValueAt(int index, Logs t) {
		if(index == 0)
			return t.getEvent_id().toString();
		else if(index == 1)
			return t.getProbe_id();
		else if(index == 2)
			return t.getEntity();
		else if(index == 3)
			return t.getEnvironment();
		else if(index == 4)
			return t.getUser_id();
		else if(index == 5)
			return t.getTransaction_id();
		else if(index == 6)
			return t.getMessage();
		else if(index == 7)
			return t.getNumber_val().toString();
		else if(index == 8)
			return t.getDate_value().toString();
		else if(index == 9)
			return t.getSend_time().toString();
		else if(index == 10)
			return t.getAbsolute_time().toString();
		else if(index == 11)
			return t.getThread_id().toString();
		else if(index == 12)
			return t.getIp_address();
		
		throw new IllegalArgumentException();
	}

	@Override
	public Logs extractEntityFromList(List<String> list) {
		Logs t = new Logs();
		t.setEvent_id(DBroUtil.toInt(list.get(0)));
		t.setProbe_id(list.get(1));
		t.setEntity(list.get(2));
		t.setEnvironment(list.get(3));
		t.setUser_id(list.get(4));
		t.setTransaction_id(list.get(5));
		t.setMessage(list.get(6));
		t.setNumber_val(DBroUtil.toFloat(list.get(7)));
		t.setDate_value(new Date());
		t.setSend_time(new Date());
		t.setAbsolute_time(new Date());
		t.setThread_id(DBroUtil.toInt(list.get(11)));
		t.setIp_address(list.get(12));
		
		return t;
	}

	@Override
	public int getColumnCount() {
		return 13;
	}


}
