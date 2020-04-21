#define arrToStr(arr)
    #for(x : arr)
        '#(x)' #if(!for.last),#end
    #end
#end

#sql("db.table_list")
    SELECT TABLE_NAME 'name',TABLE_COMMENT 'comment',table_schema 'catalog' FROM INFORMATION_SCHEMA.TABLES
    WHERE 1=1
    #if(catalogs)
        AND TABLE_SCHEMA IN (#@arrToStr(catalogs))
    #end
    #if(catalog)
        AND TABLE_SCHEMA = '#(catalog)'
    #end

    #if(tables)
        AND TABLE_NAME IN (#@arrToStr(tables))
    #end
    #if(table)
        AND TABLE_NAME = '#(table)'
    #end
#end
