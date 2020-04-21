#sql("service.method")
    @RestMapping(name = "#(url)", comment = "#(comment)")
    public JBean #(methodName)() {

        return JBean.OK;
    }
#end

#sql("go.struct")
// #(comment)
type #(name) struct {
    #for(x : columns)
	#(x.field) #(x.type)
    #end
}

func List(filter *base.Filper) []#(name) {
	db, _ := mysql.Db()

	rows, _ := db.Query("select #for(x : columns)#(x.field), #end from #(name) order by createtime desc limit ?,?", filter.Offset, filter.Limit)
	var records []#(name)
	for rows.Next() {
		r := #(name){}
		rows.Scan(#for(x : columns)&r.#(x.field), #end)
		records = append(records, r)
	}
	return records
}

#end