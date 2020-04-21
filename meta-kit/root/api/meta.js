
const meta = {
    /*getTableList(callback) {
        red.getJSON("/meta/tablelist",{}, json => callback(json));
    },*/
    /*getTableDetail({name}, callback) {
        red.getJSON("/meta/tableinfo",{name}, json => callback(json));
    },*/

    serviceSave({service}) {
        return red.postX('/meta/service_save', {service: JSON.stringify(service)})
    },
    getServiceList(callback) {
        red.getJSON("/meta/service_list",{}, json => {
            json = red.replacePoint(json);
            callback(json);
        });
    },
    getServiceInfo({name}, callback) {
        red.getJSON("/meta/service_info",{name}, res => {

            let {name, comment, table, edits, imports, shows, exports, filters, details, dels} = red.replacePoint(res)

            callback({name, comment, table, edits, imports, shows, exports, filters, details, dels});
        });
    },
    getServiceDetail({name}, callback) {
        red.getJSON("/meta/service_detail",{name}, json => {
            json = red.replacePoint(json)
            callback(json);
        });
    },
    getCfg({name}) {
        return red.postX("/meta/cfg",{name}, res => {
            return  red.replacePoint(res)
        });
    },
    getDataList({name, filters, orders, limit}, callback) {
        let fbean = red.replace$({name, filters, orders, limit})
        red.getJSON("/data/list",{fBean: JSON.stringify(fbean)}, (json = {rows:[], total:0}) => {
            callback(json)
        })
    },
    tableLinkList() { // metaLink 管理的表列表使用
        return red.postX('/meta/table_link_list')
    },
    linkSave({link}) { //
        return red.postX('/meta/link_save', {link: JSON.stringify(link)})
    },
    linkList({alias}) {
        return red.postX('/meta/link_list', {alias})
    },
    linkInfoList({alias}) {
        return red.postX('/meta/link_info_list', {alias})
    },
    tableInfo({name, alias}) {
        return red.postX('/meta/tableinfo', {name, alias})
    },

    //meta-service
    showSave({name, shows}) {
        shows = red.replace$(shows);
        return red.postX('/meta/showsort', {name, shows: JSON.stringify(shows)})
    },
    filterSave({name, filters}) {
        filters = red.replace$(filters);
        return red.postX('/meta/filter_update', {name, filters: JSON.stringify(filters)})
    },
    exportSave({name, exports}) {
        exports = red.replace$(exports);
        return red.postX('/meta/exportsave', {name, exports: JSON.stringify(exports)})
    },
    detailSave({name, details}) {
        details = red.replace$(details);
        return red.postX('/meta/detailsave', {name, details: JSON.stringify(details)})
    },
    editSave({name, edits}) {
        edits = red.replace$(edits);
        return red.postX('/meta/editsave', {name, edits: JSON.stringify(edits)})
    },
    delSave({name, dels}) {
        dels = red.replace$(dels);
        return red.postX('/meta/delsave', {name, dels: JSON.stringify(dels)})
    },

    // Meta-Table
    tableList() {
        return red.postX('/meta/tablelist')
    },
    itemSort({alias, items}) {
        return red.postX('/meta/itemsort', {alias, items: JSON.stringify(items)})
    },
    itemUpdate({alias, items}) {
        return red.postX('/meta/itemupdate', {alias, items: JSON.stringify(items)})
    },

    //client
    exportData({fbean, cate}) {
        if (cate == 'excel') {
            location.href = "/data/export?fBean=" + JSON.stringify(fbean) + "&platToken=" + red.getPlatToken() + "&cate=csv";
        } else if (cate == 'cvs') {

        } else if (cate == 'json') {

        }
    },

    dataDel({name, id}) {
        return red.postX('/data/del', {name, data: JSON.stringify({id})})
    },

    dataSave({name, data}) {
        return red.postX('/data/save', {name, data: JSON.stringify(data)})
    },

    // 刷新服务端缓存数据
    refresh() {
        return red.postX("/meta/refresh", {})
    }
}

