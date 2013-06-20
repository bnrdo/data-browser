package com.bnrdo.databrowser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.SortField;
import org.jooq.TableLike;
import org.jooq.impl.Factory;
import org.jooq.impl.TableImpl;

import com.bnrdo.databrowser.Constants.SORT_ORDER;

public class Query{
	
	private List<String> select;
	private List<String> from;
	private List<Condition> where;
	private List<String> orderBy;
	private Connection connection;
	private SQLDialect dialect;
	private int offset;
	private int limit;
	
	public Query(Connection con , SQLDialect di){
		connection = con;
		dialect = di;
		
		select = new ArrayList<String>();
		from = new ArrayList<String>();
		where = new ArrayList<Condition>();
		orderBy = new ArrayList<String>();
	}
	
	public void addSelect(String field){
		select.add(field);
	}
	
	public void addFrom(String field){
		from.add(field);
	}
	
	public void addCondition(Condition cond){
		where.add(cond);
	}
	
	public void addOrderBy(String orderField, SORT_ORDER order){
		orderBy.add(orderField + "###" + order.toString());
	}
	
	public List<String> getSelect() {
		return select;
	}

	public List<String> getFrom() {
		return from;
	}

	public List<Condition> getWhere() {
		return where;
	}

	public List<String> getOrderBy() {
		return orderBy;
	}

	public List<String> getFields(){
		return select;
	}
	
	public void setOffset(int off){
		offset = off;
	}
	
	public void setLimit(int lim){
		limit = lim;
	}
	
	public String buildSQL(){
		Factory bdr = new Factory(connection, dialect);
		
		List<Field<Object>> fields = new ArrayList<Field<Object>>();
		List<TableLike<?>> tables = new ArrayList<TableLike<?>>();
		List<SortField<?>> order = new ArrayList<SortField<?>>();
		
		for(String s : select){
			fields.add(Factory.fieldByName(s));
		}
		
		for(String s : from){
			TableLike<?> table = new TableImpl(s);
			tables.add(table);
		}
		
		for(String s : orderBy){
			String field = s.split("###")[0];
			String sortOrder = s.split("###")[1];
			if(sortOrder.equals(SORT_ORDER.DESC)){
				order.add(Factory.field(field).desc());
			}else{
				order.add(Factory.field(field).asc());
			}
		}
		
		return bdr.renderInlined(bdr.select(fields)
					.from(tables)
					.where(where)
					.orderBy(order)
					.limit(limit)
					.offset(offset));
	}
	public static void main(String[] args) throws SQLException{
		Factory bdr = new Factory(SQLDialect.ORACLE);
		String qry = bdr.renderInlined(bdr.select(Factory.fieldByName("ID"),
				Factory.fieldByName("DATE_VAL"),
				Factory.fieldByName("ENTITY"),
				Factory.fieldByName("ENVIRONMENT"))
			.from("LOGS")
			.where("ID > 1")
			.orderBy(Factory.fieldByName("ID").asc())
			.limit(10)
			.offset(0));
		
		System.out.println(qry);
	}
}
