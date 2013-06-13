package com.bnrdo.databrowser.format;

import java.util.Date;
import java.util.List;

import com.bnrdo.databrowser.DBroUtil;
import com.bnrdo.databrowser.TableDataSourceFormat;
import com.bnrdo.databrowser.domain.ThotLogs;

public class ThotLogsFormat implements TableDataSourceFormat<ThotLogs>{

	@Override
	public String getValueAt(int index, ThotLogs t) {
		if(index == 0) return Long.toString(t.getId());
		else if(index == 1) return t.getDate_val().toString();
		else if(index == 2) return t.getEntity();
		else if(index == 3) return t.getEnvironment();
		else if(index == 4) return t.getMessage_val();
		else if(index == 5) return Double.toString(t.getNumber_val());
		else if(index == 6) return t.getProbe_id();
		else if(index == 7) return Long.toString(t.getProcess_id());
		else if(index == 8) return t.getServer_ip();
		else if(index == 9) return Long.toString(t.getTimestamp());
		else if(index == 10) return t.getTransaction_id();
		else if(index == 11) return t.getUnknown();
		else if(index == 12) return t.getUser_id();
										
			
			
		throw new IllegalArgumentException();
	}

	@Override
	public ThotLogs extractEntityFromList(List<String> list) {
		ThotLogs t = new ThotLogs();
		t.setId(DBroUtil.toLong(list.get(0)));
		t.setDate_val(new Date());
		t.setEntity(list.get(2));
		t.setEnvironment(list.get(3));
		t.setMessage_val(list.get(4));
		t.setNumber_val(DBroUtil.toDouble(list.get(5)));
		t.setProbe_id(list.get(6));
		t.setProcess_id(DBroUtil.toLong(list.get(7)));
		t.setServer_ip(list.get(8));
		t.setTimestamp(DBroUtil.toLong(list.get(9)));
		t.setUnknown(list.get(10));
		t.setUser_id(list.get(11));
		return null;
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 13;
	}

}
