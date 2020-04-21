
const plat = {

    platList(params = {}) { // 平台列表
        params['platToken'] = 'xx'
        return red.getX('/plat/list', params)
    },
    platSave({plat}) {
        return red.postX('/plat/save', {plat:JSON.stringify(plat)})
    },

    dbList(params) { // 数据源列表
        return red.getX('/plat/db_list', params)
    },
    dbSave({plat}) {
        // todo：解决重复点击保存出现的异常
        plat["url"] = escape(plat["url"])
        return red.postX('/plat/db_save', {plat: JSON.stringify(plat)})
    },

}