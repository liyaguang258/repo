const table = {
    _sheets({cate, filePath, dbAccount, dbPlatId, catalog}) {
        return red.postX('/_table/sheets',{cate, filePath, dbAccount, dbPlatId, catalog})
    },
    sheetsExcel({filePath}) {
        return table._sheets({cate: "excel", filePath})
    },
    sheetMySql({dbPlatId, catalog}) {
        return table._sheets({cate: "mysql", dbPlatId, catalog})
    },
    sheetInfo({filePath, sheetName}) {
        return red.postX('/_table/sheet_info', {cate: 'excel', filePath, sheetName})
    },

    saveTable({dbPlatId, catalog, tableArr}) { //
        return red.postX('/_table/table_save', {cate: 'mysql', dbPlatId, catalog, tableArr: JSON.stringify(tableArr)})
    },
    saveSheet({filePath, sheetNames}) {
        return red.postX('/_table/table_save', {cate: 'excel', filePath, sheetNames: JSON.stringify(sheetNames)})
    }
}