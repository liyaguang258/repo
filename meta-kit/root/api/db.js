const db = {
    catalogList({dbAccount, dbPlatId}) { // database列表
        console.log(JSON.stringify(dbAccount))
        return red.postX('/_db/catalog_list', {dbAccount: escape(JSON.stringify(dbAccount)), dbPlatId})
    },
    tableList({dbPlatId, catalog}) { // 数据库表列表
        return red.postX('/_db/table_list', {dbPlatId, catalog})
    },
    tableInfo({dbPlatId, catalog, tableName}) {
        return red.postX('/_db/table_info', {dbPlatId, catalog, tableName})
    }
}